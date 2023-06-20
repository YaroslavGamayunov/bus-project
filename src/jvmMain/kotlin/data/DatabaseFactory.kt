package data

import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*

object DatabaseFactory {
    lateinit var database: Database
    fun init() {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:file:./build/db"
        database = Database.connect(jdbcURL, driverClassName)

        transaction(database) {
            // print sql to std-out
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(Bus, BusModel, Stop, RouteStop, Route, Trip)

            // insert new city. SQL: INSERT INTO Cities (name) VALUES ('St. Petersburg')
//            val id = BusModel.insert {
//                it[modelName] = "BUBUBUS"
//                it[imageUrl] = "jddjdjd"
//            } get BusModel.id

            // 'select *' SQL: SELECT Cities.id, Cities.name FROM Cities
//            println("Cities: ${BusModel.selectAll()}")
        }
    }
}