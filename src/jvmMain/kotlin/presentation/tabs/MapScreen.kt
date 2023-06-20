package presentation.tabs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import data.BusRepository
import data.Route
import data.Stop
import domain.RouteEntity
import domain.StopEntity
import presentation.common.loadImageBitmap
import java.awt.color.ColorSpace
import java.io.File
import kotlin.random.Random
import kotlin.random.nextULong


fun getRandColor(): Color {
    val toChoose = listOf(Color.Blue, Color.Green, Color.Yellow, Color.Red)
    return toChoose[Random(System.currentTimeMillis()).nextInt(toChoose.size)]
}

@Composable
fun MapScreen(stops: List<StopEntity>, routes: List<RouteEntity>) {
    var mapSize by remember { mutableStateOf(Size.Zero) }
    var newStopCoordinates by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var newStopName by remember { mutableStateOf("") }

    var newRouteStops by remember { mutableStateOf(emptyList<StopEntity>()) }
    var newRouteColor by remember { mutableStateOf(getRandColor()) }

    var isShowingAddRouteDialog by remember { mutableStateOf(false) }

    var selectedStop by remember { mutableStateOf<StopEntity?>(null) }

    var routesToDraw by remember { mutableStateOf<List<RouteEntity>>(emptyList()) }

    Row {
        Box(modifier = Modifier.fillMaxSize().weight(0.6f)) {
            Canvas(
                modifier = Modifier.fillMaxSize()
                    .onGloballyPositioned { coordinates -> mapSize = coordinates.size.toSize() }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { tapOffset ->
                                newStopCoordinates =
                                    tapOffset.x / size.height.toDouble() to tapOffset.y / size.width.toDouble()
//                                var index = 0
//                                for (rect in dotRects) {
//                                    if (rect.contains(tapOffset)) {
//                                        // Handle the click here and do
//                                        // some action based on the index
//                                        break // don't need to check other points,
//                                        // so break
//                                    }
//                                    index++
//                                }
                            },
                            onDoubleTap = { tapOffset ->
                                val coordinates =
                                    Offset(tapOffset.x / size.height.toFloat(), tapOffset.y / size.width.toFloat())
                                selectedStop = stops.minBy {
                                    (Offset(it.x.toFloat(), it.y.toFloat()) - coordinates).getDistance()
                                }
                            }
                        )
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                drawImage(
                    image = loadImageBitmap(File("src/jvmMain/resources/SA_MAP.jpg")),
                    dstSize = IntSize(canvasWidth.toInt(), canvasHeight.toInt())
                )

                stops.forEach {
                    val x = (canvasHeight * it.x).toInt()
                    val y = (canvasWidth * it.y).toInt()

                    drawImage(
                        image = loadImageBitmap(File("src/jvmMain/resources/marker.png")),
                        dstSize = IntSize(100, 100),
                        dstOffset = IntOffset(x - 50, y - 100)
                    )
                }

                newStopCoordinates?.let {
                    val x = (canvasHeight * it.first).toInt()
                    val y = (canvasWidth * it.second).toInt()

                    println("shit: ${it.first}, ${it.second}")

                    drawImage(
                        image = loadImageBitmap(File("src/jvmMain/resources/marker.png")),
                        dstSize = IntSize(100, 100),
                        dstOffset = IntOffset(x - 50, y - 100)
                    )
                }

                for (i in 1 until newRouteStops.size) {
                    val current = newRouteStops[i]
                    val prev = newRouteStops[i - 1]

                    val x1 = (canvasHeight * prev.x.toFloat())
                    val y1 = (canvasWidth * prev.y.toFloat())

                    val x2 = (canvasHeight * current.x.toFloat())
                    val y2 = (canvasWidth * current.y.toFloat())

                    drawLine(newRouteColor, Offset(x1, y1), Offset(x2, y2), strokeWidth = 7f)
                }

                for (route in routesToDraw) {
                    for (i in 1 until route.stops.size) {
                        val current = route.stops[i]
                        val prev = route.stops[i - 1]

                        val x1 = (canvasHeight * prev.x.toFloat())
                        val y1 = (canvasWidth * prev.y.toFloat())

                        val x2 = (canvasHeight * current.x.toFloat())
                        val y2 = (canvasWidth * current.y.toFloat())

                        drawLine(route.color, Offset(x1, y1), Offset(x2, y2), strokeWidth = 7f)
                    }
                }
//            val canvasWidth = size.width
//            val canvasHeight = size.height
//            drawLine(
//                start = Offset(x = canvasWidth, y = 0f),
//                end = Offset(x = 0f, y = canvasHeight),
//                color = Color.Blue
//            )
            }

            newStopCoordinates?.let {
                val x = with(LocalDensity.current) {
                    (mapSize.height * it.first).toInt().toDp()
                }
                val y = with(LocalDensity.current) {
                    (mapSize.width * it.second).toInt().toDp()
                }

                Row(
                    modifier = Modifier
                        .offset(x, y)
                        .wrapContentSize()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                ) {
                    TextField(
                        value = newStopName,
                        modifier = Modifier.padding(16.dp),
                        onValueChange = { text ->
                            newStopName = text
                        },
                        label = { Text("New stop name") },
                    )
                    Button(
                        onClick = {
                            newStopCoordinates?.let {
                                BusRepository.addStop(it.first, it.second, newStopName)
                                newStopCoordinates = null
                                newStopName = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterVertically).padding(16.dp)
                    ) {
                        Icon(Icons.Filled.Add, "")
                    }
                    Button(
                        onClick = {
                            newStopCoordinates?.let {
                                newStopCoordinates = null
                                newStopName = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterVertically).padding(16.dp)
                    ) {
                        Icon(Icons.Filled.Close, "")
                    }
                }
            }

            stops.forEach { stop ->
                val x = with(LocalDensity.current) {
                    (mapSize.height * stop.x).toInt().toDp()
                }
                val y = with(LocalDensity.current) {
                    (mapSize.width * stop.y).toInt().toDp()
                }

                if (stop != selectedStop) {
                    Text(
                        text = "${stop.name} id=${stop.id}",
                        modifier = Modifier.offset(x, y).clip(RoundedCornerShape(10.dp)).background(Color.White)
                            .padding(4.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier.offset(x, y)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "${stop.name} id=${stop.id}"
                        )
                        when {
                            newRouteStops.isEmpty() -> {
                                Text(
                                    "start route",
                                    modifier = Modifier.clickable {
                                        newRouteStops = listOf(stop)
                                    }
                                )

                                Text(
                                    "close",
                                    modifier = Modifier.clickable {
                                        selectedStop = null
                                    }
                                        .padding(top = 4.dp)
                                )
                            }

                            else -> {
                                Text(
                                    "continue route",
                                    modifier = Modifier.clickable {
                                        newRouteStops += stop
                                    }
                                )
                                Text(
                                    "finish route",
                                    modifier = Modifier.clickable {
                                        isShowingAddRouteDialog = true
                                        newRouteStops += selectedStop!!
                                        selectedStop = null
                                    }
                                )
                                Text(
                                    "cancel route",
                                    modifier = Modifier.clickable {
                                        newRouteStops = emptyList()
                                        selectedStop = null
                                    }
                                )
                                Text(
                                    "close",
                                    modifier = Modifier.clickable {
                                        selectedStop = null
                                    }
                                        .padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
            if (isShowingAddRouteDialog) {
                AddRouteDialog(
                    onClose = {
                        isShowingAddRouteDialog = false
                    },
                    onSubmit = { name ->
                        BusRepository.addRoute(
                            stopIds = newRouteStops.map { it.id },
                            name = name,
                            color = newRouteColor.toArgb()
                        )
                        isShowingAddRouteDialog = false
                        newRouteColor = getRandColor()
                        newRouteStops = emptyList()
                    }
                )
            }
        }
        Column(modifier = Modifier.fillMaxSize().weight(0.4f).padding(16.dp)) {
            Text(
                text = "Routes",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
            routes.forEach { route ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = route in routesToDraw,
                        onCheckedChange = { checked ->
                            if (checked) {
                                routesToDraw += route
                            } else {
                                routesToDraw -= route
                            }
                        })
                    Text(text = route.name, modifier = Modifier.padding(end = 32.dp))
                    Text("Color:", modifier = Modifier.padding(end = 16.dp))
                    Box(modifier = Modifier.size(30.dp, 30.dp).background(route.color))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AddRouteDialog(
    onClose: () -> Unit,
    onSubmit: (name: String) -> Unit
) {
    var newRouteName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onClose,
        buttons = {
            Button(
                modifier = Modifier.fillMaxWidth().padding(all = 16.dp),
                onClick = {
                    onSubmit(newRouteName)
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
                    value = newRouteName,
                    onValueChange = { text ->
                        newRouteName = text
                    },
                    label = { Text("Route name") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    )
}
