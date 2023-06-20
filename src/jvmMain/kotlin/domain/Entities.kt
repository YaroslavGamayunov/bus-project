package domain

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

data class BusEntity(
    val id: Int,
    val productionYear: Int,
    val modelName: String,
    val modelImageUrl: String
)

data class StopEntity(
    val x: Double,
    val y: Double,
    val name: String,
    val id: Int
)

data class RouteEntity(
    val stops: List<StopEntity>,
    val color: Color,
    val name: String,
    val id: Int
)

data class BusModelEntity(
    val name: String,
    val imageUrl: String,
    val id: Int
)

data class TripEntity(
    val busId: Int,
    val routeId: Int,
    val startTime: LocalDate,
    val endTime: LocalDate,
    val id: Int
)