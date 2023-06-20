import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.singleWindowApplication
import data.DatabaseFactory
import presentation.TabScreen
import kotlin.system.exitProcess

fun main() = singleWindowApplication(
    title = "Bus manager"
) {
    DatabaseFactory.init()

    TabScreen()
}
