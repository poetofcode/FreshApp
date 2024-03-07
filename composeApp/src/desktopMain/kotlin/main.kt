import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import presentation.App
import presentation.base.Config
import presentation.base.ViewModelStore
import presentation.factories.MockRepositoryFactory
import presentation.factories.viewModelFactories

fun main() = application {
    val repositoryFactory = MockRepositoryFactory()

    val vmStoreImpl = ViewModelStore(
        coroutineScope = rememberCoroutineScope(),
        vmFactories = viewModelFactories(repositoryFactory = repositoryFactory)
    )

    Window(onCloseRequest = ::exitApplication, title = "FreshApp") {
        App(Config(
            viewModelStore = vmStoreImpl,
            repositoryFactory = repositoryFactory,
            imageUtil = DesktopImageUtil()
        ))
    }
}