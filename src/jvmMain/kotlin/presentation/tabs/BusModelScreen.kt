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
import domain.BusModelEntity
import presentation.common.AsyncImage
import presentation.common.loadImageBitmap

@Composable
fun BusModelScreen(busModels: List<BusModelEntity>) {
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
                            Text("Image")
                        },
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    busModels.forEach { model ->
                        row {
                            cell { Text(model.id.toString()) }
                            cell { Text(model.name) }
                            cell {
                                AsyncImage(
                                    load = { loadImageBitmap(model.imageUrl) },
                                    painterFor = { BitmapPainter(it) },
                                    contentScale = ContentScale.FillHeight,
                                    contentDescription = "",
                                    modifier = Modifier.height(80.dp).padding(10.dp)
                                )
                            }
                        }
                    }
                }

                if (isShowingAddDialog) {
                    AddModelDialog(
                        onSubmit = { name, url ->
                            BusRepository.addModel(
                                name, url
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
private fun AddModelDialog(
    onClose: () -> Unit,
    onSubmit: (name: String, imageUrl: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onClose,
        buttons = {
            Button(
                modifier = Modifier.fillMaxWidth().padding(all = 16.dp),
                onClick = {
                    onSubmit(name, imageUrl)
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
                    value = name,
                    onValueChange = { text ->
                        name = text
                    },
                    label = { Text("Model name") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                TextField(
                    modifier = Modifier.padding(all = 16.dp),
                    value = imageUrl,
                    onValueChange = { text ->
                        imageUrl = text
                    },
                    label = { Text("Image url") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    )
}
