import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import data.repository.RepositoryFactoryImpl
import data.service.NetworkingFactory
import data.service.NetworkingFactoryImpl
import data.utils.ContentBasedPersistentStorage
import data.utils.FileContentProvider
import data.utils.getValue
import data.utils.setValue
import dev.datlag.kcef.KCEF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import presentation.App
import presentation.base.Config
import presentation.base.ViewModelStore
import presentation.factories.viewModelFactories
import java.io.File
import kotlin.math.max


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

    val fileProvider = FileContentProvider(
        fileName = "config.json",
        relativePath = "appcache",
    )

    val storage = ContentBasedPersistentStorage(fileProvider)

    var windowWidth: Int? by storage
    var windowHeight: Int? by storage

    val windowState = rememberWindowState(
        size = DpSize(windowWidth?.dp ?: 400.dp, windowHeight?.dp ?: 300.dp),
        position = WindowPosition(300.dp, 300.dp)
    )

    LaunchedEffect(windowState) {
        snapshotFlow {
            windowState.size
        }.onEach { snap ->
            windowWidth = snap.width.value.toInt()
            windowHeight = snap.height.value.toInt()
        }.launchIn(this)
    }

    Window(state = windowState, onCloseRequest = ::exitApplication, title = "FreshApp") {
        var restartRequired by remember { mutableStateOf(false) }
        var downloading by remember { mutableStateOf(0F) }
        var initialized by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                KCEF.init(builder = {
                    installDir(File("kcef-bundle"))
                    progress {
                        onDownloading {
                            downloading = max(it, 0F)
                        }
                        onInitialized {
                            initialized = true
                        }
                    }
                    settings {
                        cachePath = File("cache").absolutePath
                    }
                }, onError = {
                    it?.printStackTrace()
                }, onRestartRequired = {
                    restartRequired = true
                })
            }
        }

        if (restartRequired) {
            Text(text = "Restart required.")
        } else {
            if (initialized) {
                App(
                    Config(
                        viewModelStore = vmStoreImpl,
                        repositoryFactory = repositoryFactory,
                    )
                )

            } else {
                Text(text = "Downloading $downloading%")
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                KCEF.disposeBlocking()
            }
        }
    }
}
