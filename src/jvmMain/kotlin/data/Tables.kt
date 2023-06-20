package data

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate


//object StarWarsFilms : Table() {
//    val id: Column<Int> = integer("id").autoIncrement()
//    val sequelId: Column<Int> = integer("sequel_id").uniqueIndex()
//    val name: Column<String> = varchar("name", 50)
//    val director: Column<String> = varchar("director", 50)
//    override val primaryKey = PrimaryKey(id, name = "PK_StarWarsFilms_Id") // PK_StarWarsFilms_Id is optional here
//}

object Bus : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val productionYear = integer("production_year")
    val modelId = integer("model_id") references BusModel.id
    override val primaryKey = PrimaryKey(id)
}

object BusModel : Table("BUS_MODEL") {
    val id: Column<Int> = integer("id").autoIncrement()
    val modelName: Column<String> = varchar("model_name", 50)
    val imageUrl: Column<String> = varchar("image_url", 200)
    override val primaryKey = PrimaryKey(id)
}

object Stop : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val xCoordinate: Column<Double> = double("x_coordinate")
    val yCoordinate: Column<Double> = double("y_coordinate")
    val name: Column<String> = varchar("name", 100)
    override val primaryKey = PrimaryKey(id)
}

object RouteStop : Table("ROUTE_STOP") {
    val id: Column<Int> = integer("id").autoIncrement()
    val routeId = integer("route_id") references Route.id
    val stopId = integer("stop_id") references Stop.id
    val serialNumber = integer("serial_number")
    override val primaryKey = PrimaryKey(id)
}

object Route : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val color = integer("color")
    val numStops = integer("num_stops")
    override val primaryKey = PrimaryKey(id)
}

object Trip : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val routeId: Column<Int> = integer("route_id") references Route.id
    val busId: Column<Int> = integer("bus_id") references Bus.id
    val startTime: Column<LocalDate> = date("start_time")
    val endTime: Column<LocalDate> = date("end_time")
    override val primaryKey = PrimaryKey(id)
}