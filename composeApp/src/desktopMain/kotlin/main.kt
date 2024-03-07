import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import base.ConfigImpl
import base.ViewModelStore
import factories.viewModelFactories

fun main() = application {
    val vmStoreImpl = ViewModelStore(
        vmFactories = viewModelFactories
    )

    Window(onCloseRequest = ::exitApplication, title = "FreshApp") {
        App(ConfigImpl(vmStore = vmStoreImpl))
    }
}