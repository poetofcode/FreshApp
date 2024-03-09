import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import data.repository.RepositoryFactoryImpl
import data.service.NetworkingFactory
import data.service.NetworkingFactoryImpl
import dev.datlag.kcef.KCEF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import presentation.App
import presentation.base.Config
import presentation.base.ViewModelStore
import presentation.factories.*
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

//    Window(onCloseRequest = ::exitApplication, title = "FreshApp") {
//        App(Config(
//            viewModelStore = vmStoreImpl,
//            repositoryFactory = repositoryFactory,
//        ))
//    }


    Window(onCloseRequest = ::exitApplication, title = "FreshApp") {
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
                // MainWebView()
                // Text(text = "Main Web Vieww")
                // val state = rememberWebViewState("https://habr.com")
                // WebView(state = state, modifier = Modifier.fillMaxSize())

                App(Config(
                    viewModelStore = vmStoreImpl,
                    repositoryFactory = repositoryFactory,
                ))

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