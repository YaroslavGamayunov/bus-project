package presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import data.Bus
import data.BusRepository
import presentation.tabs.BusModelScreen
import presentation.tabs.BusesScreen
import presentation.tabs.MapScreen
import presentation.tabs.TripsScreen

@Composable
fun TabScreen() {
    var tabIndex by remember { mutableStateOf(0) }

    val tabs = listOf("Buses", "Bus models", "Map", "Trips")
    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }
        when (tabIndex) {
            0 -> {
                BusesScreen(BusRepository.allBuses.collectAsState().value)
            }

            1 -> {
                BusModelScreen(BusRepository.allModels.collectAsState().value)
            }

            2 -> MapScreen(
                stops = BusRepository.allStops.collectAsState().value,
                routes = BusRepository.allRoutes.collectAsState().value,
            )

            3 -> TripsScreen(
                trips = BusRepository.allTrips.collectAsState().value,
            )
        }
    }
}
