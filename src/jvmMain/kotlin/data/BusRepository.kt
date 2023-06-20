package data

import androidx.compose.ui.graphics.Color
import domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate


class MyStdOutSqlLogger(private val tag: String) : SqlLogger {
    override fun log(context: StatementContext, transaction: Transaction) {
        System.out.println("$tag: ${context.expandArgs(transaction)}")
    }
}

object BusRepository {

    val allBuses = MutableStateFlow<List<BusEntity>>(emptyList())

    val allStops = MutableStateFlow<List<StopEntity>>(emptyList())

    val allRoutes = MutableStateFlow<List<RouteEntity>>(emptyList())

    val allModels = MutableStateFlow<List<BusModelEntity>>(emptyList())

    val allTrips = MutableStateFlow<List<TripEntity>>(emptyList())

    init {
        allBuses.value = loadAllBuses()
        allStops.value = loadAllStops()
        allRoutes.value = loadAllRoutes()
        allModels.value = loadAllModels()
        allTrips.value = loadAllTrips()
    }

    fun addBus(productionYear: Int, modelId: Int) {
        transaction(DatabaseFactory.database) {
            addLogger(StdOutSqlLogger)
            Bus.insert {
                it[Bus.productionYear] = productionYear
                it[Bus.modelId] = modelId
            }
        }
        allBuses.value = loadAllBuses()
    }

    fun addModel(name: String, imageUrl: String) {
        transaction(DatabaseFactory.database) {
            addLogger(StdOutSqlLogger)
            BusModel.insert {
                it[BusModel.modelName] = name
                it[BusModel.imageUrl] = imageUrl
            }
        }
        allModels.value = loadAllModels()
    }

    fun addTrip(start: LocalDate, end: LocalDate, busId: Int, routeId: Int) {
        transaction(DatabaseFactory.database) {
            addLogger(StdOutSqlLogger)
            Trip.insert {
                it[Trip.busId] = busId
                it[Trip.routeId] = routeId
                it[Trip.startTime] = start
                it[Trip.endTime] = end
            }
        }
        allTrips.value = loadAllTrips()
    }

    fun addStop(x: Double, y: Double, name: String) {
        transaction(DatabaseFactory.database) {
            addLogger(StdOutSqlLogger)
            Stop.insert {
                it[Stop.name] = name
                it[xCoordinate] = x
                it[yCoordinate] = y
            }
            allStops.value = loadAllStops()
        }
    }

    fun addRoute(stopIds: List<Int>, name: String, color: Int) {
        transaction(DatabaseFactory.database) {
            addLogger(MyStdOutSqlLogger("add_route"))
            val routeId = Route.insert {
                it[Route.name] = name
                it[Route.color] = color
                it[numStops] = stopIds.size
            }[Route.id]

            stopIds.forEachIndexed { index, stopId ->
                RouteStop.insert {
                    it[RouteStop.stopId] = stopId
                    it[RouteStop.routeId] = routeId
                    it[RouteStop.serialNumber] = index
                }
            }
            allRoutes.value = loadAllRoutes()
        }
    }

    private fun loadAllBuses(): List<BusEntity> {
        return transaction(DatabaseFactory.database) {
            addLogger(MyStdOutSqlLogger("load_all_buses:"))
            (Bus innerJoin BusModel).selectAll()
                .map { row ->
                    BusEntity(
                        id = row[Bus.id],
                        productionYear = row[Bus.productionYear],
                        modelName = row[BusModel.modelName],
                        modelImageUrl = row[BusModel.imageUrl]
                    )
                }
        }
    }

    private fun loadAllStops(): List<StopEntity> {
        return transaction(DatabaseFactory.database) {
            addLogger(MyStdOutSqlLogger("load_all_stops"))
            addLogger(StdOutSqlLogger)
            Stop.selectAll()
                .map { row ->
                    StopEntity(
                        id = row[Stop.id],
                        x = row[Stop.xCoordinate],
                        y = row[Stop.yCoordinate],
                        name = row[Stop.name]
                    )
                }
        }
    }

    private fun loadAllRoutes(): List<RouteEntity> {
        return transaction(DatabaseFactory.database) {
            addLogger(addLogger(MyStdOutSqlLogger("load_all_routes")))
            Route.selectAll()
                .map { row ->
                    RouteEntity(
                        id = row[Route.id],
                        name = row[Route.name],
                        color = Color(row[Route.color]),
                        stops = loadAllStops(row[Route.id])
                    )
                }
        }
    }

    private fun loadAllStops(routeId: Int): List<StopEntity> {
        return transaction(DatabaseFactory.database) {
            addLogger(MyStdOutSqlLogger("load_all_stops_by_route"))
            Join(Stop, RouteStop, JoinType.INNER, onColumn = Stop.id, otherColumn = RouteStop.stopId)
                .slice(Stop.id, Stop.xCoordinate, Stop.yCoordinate, Stop.name)
                .select { RouteStop.routeId eq routeId }
                .orderBy(RouteStop.serialNumber to SortOrder.ASC)
                .map { row ->
                    StopEntity(
                        id = row[Stop.id],
                        x = row[Stop.xCoordinate],
                        y = row[Stop.yCoordinate],
                        name = row[Stop.name]
                    )
                }
        }
    }

    private fun loadAllModels(): List<BusModelEntity> {
        return transaction(DatabaseFactory.database) {
            BusModel.selectAll()
                .map {
                    BusModelEntity(
                        name = it[BusModel.modelName],
                        imageUrl = it[BusModel.imageUrl],
                        id = it[BusModel.id]
                    )
                }
        }
    }

    private fun loadAllTrips(): List<TripEntity> {
        return transaction(DatabaseFactory.database) {
            Trip.selectAll()
                .map {
                    TripEntity(
                        routeId = it[Trip.routeId],
                        id = it[Trip.id],
                        busId = it[Trip.busId],
                        startTime = it[Trip.startTime],
                        endTime = it[Trip.endTime]
                    )
                }
        }
    }
}