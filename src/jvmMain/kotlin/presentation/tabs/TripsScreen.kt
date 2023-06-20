package presentation.tabs

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.Table
import com.seanproctor.datatable.TableColumnDefinition
import data.BusRepository
import domain.TripEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TripsScreen(trips: List<TripEntity>) {
    var isShowingAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isShowingAddDialog = true
                },
            ) {
                Icon(Icons.Filled.Add, "")
            }
        }
    ) {
        val stateVertical = rememberScrollState(0)

        Box {
            Box(modifier = Modifier.verticalScroll(stateVertical).fillMaxSize()) {
                Table(
                    columns = listOf(
                        TableColumnDefinition {
                            Text("Id")
                        },
                        TableColumnDefinition {
                            Text("Route id")
                        },
                        TableColumnDefinition {
                            Text("Bus id")
                        },
                        TableColumnDefinition {
                            Text("Start date")
                        },
                        TableColumnDefinition {
                            Text("End date")
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    trips.forEach { trip ->
                        row {
                            cell { Text(trip.id.toString()) }
                            cell { Text(trip.routeId.toString()) }
                            cell { Text(trip.busId.toString()) }
                            cell {
                                Text(trip.startTime.toString())
                            }
                            cell {
                                Text(trip.endTime.toString())
                            }
                        }
                    }
                }

                if (isShowingAddDialog) {
                    AddTripDialog(
                        onSubmit = { start, end, busId, routeId ->
                            BusRepository.addTrip(start, end, busId, routeId)
                            isShowingAddDialog = false
                        },
                        onClose = {
                            isShowingAddDialog = false
                        }
                    )
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd)
                    .fillMaxHeight(),
                adapter = rememberScrollbarAdapter(stateVertical)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AddTripDialog(
    onClose: () -> Unit,
    onSubmit: (start: LocalDate, end: LocalDate, busId: Int, routeId: Int) -> Unit
) {
    var y1 by remember { mutableStateOf("") }
    var d1 by remember { mutableStateOf("") }

    var y2 by remember { mutableStateOf("") }
    var d2 by remember { mutableStateOf("") }

    var busId by remember { mutableStateOf("") }
    var routeId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onClose,
        buttons = {
            Button(
                modifier = Modifier.fillMaxWidth().padding(all = 16.dp),
                onClick = {
                    onSubmit(
                        LocalDate.ofYearDay(y1.toInt(), d1.toInt()),
                        LocalDate.ofYearDay(y2.toInt(), d2.toInt()),
                        busId.toInt(),
                        routeId.toInt()
                    )
                },
                content = {
                    Text("Submit")
                }
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(
                    modifier = Modifier.padding(all = 16.dp),
                    value = y1,
                    onValueChange = { text ->
                        y1 = text
                    },
                    label = { Text("Year of start") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                TextField(
                    modifier = Modifier.padding(all = 16.dp),
                    value = d1,
                    onValueChange = { text ->
                        d1 = text
                    },
                    label = { Text("Day of start") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                TextField(
                    modifier = Modifier.padding(all = 16.dp),
                    value = y2,
                    onValueChange = { text ->
                        y2 = text
                    },
                    label = { Text("Year of end") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                TextField(
                    modifier = Modifier.padding(all = 16.dp),
                    value = d2,
                    onValueChange = { text ->
                        d2 = text
                    },
                    label = { Text("Day of end") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                TextField(
                    modifier = Modifier.padding(all = 16.dp),
                    value = busId,
                    onValueChange = { text ->
                        busId = text
                    },
                    label = { Text("Bus id") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                TextField(
                    modifier = Modifier.padding(all = 16.dp),
                    value = routeId,
                    onValueChange = { text ->
                        routeId = text
                    },
                    label = { Text("Route id") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    )
}
