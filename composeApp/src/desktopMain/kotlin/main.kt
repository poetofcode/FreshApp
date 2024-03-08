import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import presentation.App
import presentation.base.Config
import presentation.base.ViewModelStore
import presentation.factories.NetworkingFactory
import presentation.factories.NetworkingFactoryImpl
import presentation.factories.RepositoryFactoryImpl
import presentation.factories.viewModelFactories

fun main() = application {
    // val repositoryFactory = MockRepositoryFactory()
    val networkingFactory: NetworkingFactory = NetworkingFactoryImpl()

    val repositoryFactory = RepositoryFactoryImpl(
        api = networkingFactory.createApi()
    )

    val vmStoreImpl = ViewModelStore(
        coroutineScope = rememberCoroutineScope(),
        vmFactories = viewModelFactories(repositoryFactory = repositoryFactory)
    )

    Window(onCloseRequest = ::exitApplication, title = "FreshApp") {
        App(Config(
            viewModelStore = vmStoreImpl,
            repositoryFactory = repositoryFactory,
        ))
    }
}