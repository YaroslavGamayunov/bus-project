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
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.seanproctor.datatable.Table
import com.seanproctor.datatable.TableColumnDefinition
import data.BusRepository
import domain.BusEntity
import presentation.common.AsyncImage
import presentation.common.loadImageBitmap

@Composable
fun BusesScreen(buses: List<BusEntity>) {
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
                            Text("Model name")
                        },
                        TableColumnDefinition {
                            Text("Production year")
                        },
                        TableColumnDefinition {
                            Text("Image")
                        },
                        TableColumnDefinition {
                            Text("Count of trips")
                        },
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    buses.forEach { bus ->
                        row {
                            cell { Text(bus.id.toString()) }
                            cell { Text(bus.modelName) }
                            cell { Text(bus.productionYear.toString()) }
                            cell {
                                AsyncImage(
                                    load = { loadImageBitmap(bus.modelImageUrl) },
                                    painterFor = { BitmapPainter(it) },
                                    contentScale = ContentScale.FillHeight,
                                    contentDescription = "",
                                    modifier = Modifier.height(80.dp).padding(10.dp)
                                )
                            }
                            cell {
                                Text(bus.countOfTrips.toString())
                            }
                        }
                    }
                }

                if (isShowingAddDialog) {
                    AddBusDialog(
                        onSubmit = { productionYear, modelId ->
                            BusRepository.addBus(
                                productionYear = productionYear,
                                modelId = modelId
                            )
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
private fun AddBusDialog(
    onClose: () -> Unit,
    onSubmit: (productionYear: Int, modelId: Int) -> Unit
) {
    var productionYear by remember { mutableStateOf("") }
    var modelId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onClose,
        buttons = {
            Button(
                modifier = Modifier.fillMaxWidth().padding(all = 16.dp),
                onClick = {
                    onSubmit(productionYear.toInt(), modelId.toInt())
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
                    value = productionYear,
                    onValueChange = { text ->
                        productionYear = text
                    },
                    label = { Text("Production year") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                TextField(
                    modifier = Modifier.padding(all = 16.dp),
                    value = modelId,
                    onValueChange = { text ->
                        modelId = text
                    },
                    label = { Text("Model id") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    )
}
