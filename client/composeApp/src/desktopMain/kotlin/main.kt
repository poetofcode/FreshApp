import androidx.compose.material.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import data.repository.RepositoryFactoryImpl
import data.service.NetworkingFactory
import data.service.NetworkingFactoryImpl
import data.utils.AppDataStorageImpl
import data.utils.FileContentProvider
import data.utils.PersistentStorage
import data.utils.ProfileStorageImpl
import data.utils.getValue
import data.utils.setValue
import dev.datlag.kcef.KCEF
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import presentation.App
import presentation.LocalMainAppState
import presentation.MainAppState
import presentation.base.Config
import presentation.base.ViewModelStore
import presentation.factories.viewModelFactories
import presentation.model.shared.OnOpenExternalBrowserSharedEvent
import presentation.model.shared.ShowDesktopNotificationSharedEvent
import presentation.navigation.SharedMemory
import utils.openWebpage
import java.io.File
import kotlin.math.max


const val DEFAULT_WINDOW_WIDTH = 600
const val DEFAULT_WINDOW_HEIGHT = 400
const val DEFAULT_POSITION_X = 300
const val DEFAULT_POSITION_Y = 300

fun main() = application {
    // val repositoryFactory = MockRepositoryFactory()
    val profileStorage = ProfileStorageImpl(
        FileContentProvider(
            fileName = "sessioncache.json",
            relativePath = "appcache",
        )
    )
    val networkingFactory: NetworkingFactory = NetworkingFactoryImpl(
        profileStorage,
        Config.DeviceTypes.DESKTOP,
    )

    val configStorage = PersistentStorage(
        FileContentProvider(
            fileName = "config.json",
            relativePath = "appcache",
        )
    )

    val appDataStorage = AppDataStorageImpl(
        FileContentProvider(
            fileName = "appdata.json",
            relativePath = "appcache",
        )
    )

    val repositoryFactory = RepositoryFactoryImpl(
        api = networkingFactory.createApi(),
        profileStorage = profileStorage,
        appDataStorage = appDataStorage,
    )

    val vmStoreImpl = ViewModelStore(
        coroutineScope = rememberCoroutineScope(),
        vmFactories = viewModelFactories(
            repositoryFactory = repositoryFactory,
            configStorage = configStorage,
        )
    )

    var windowWidth: Int? by configStorage
    var windowHeight: Int? by configStorage
    var positionX: Int? by configStorage
    var positionY: Int? by configStorage
    var isMaximized: Boolean? by configStorage

    val windowState = rememberWindowState(
        size = DpSize(
            windowWidth?.dp ?: DEFAULT_WINDOW_WIDTH.dp,
            windowHeight?.dp ?: DEFAULT_WINDOW_HEIGHT.dp
        ),
        position = WindowPosition(
            positionX?.dp ?: DEFAULT_POSITION_X.dp,
            positionY?.dp ?: DEFAULT_POSITION_Y.dp
        ),
        placement = if (isMaximized == true) WindowPlacement.Maximized else WindowPlacement.Floating
    )

    LaunchedEffect(windowState) {
        snapshotFlow {
            windowState.size
        }.onEach {
            with(it) {
                if (windowState.placement == WindowPlacement.Floating) {
                    windowWidth = width.value.toInt()
                    windowHeight = height.value.toInt()
                }
                if (width.value < DEFAULT_WINDOW_WIDTH) {
                    windowState.size = DpSize(DEFAULT_WINDOW_WIDTH.dp, height)
                }
                if (height.value < DEFAULT_WINDOW_HEIGHT) {
                    windowState.size = DpSize(height, DEFAULT_WINDOW_HEIGHT.dp)
                }
            }
        }.launchIn(this)

        snapshotFlow {
            windowState.position
        }.onEach {
            with(it) {
                if (windowState.placement == WindowPlacement.Floating) {
                    positionX = x.value.toInt()
                    positionY = y.value.toInt()
                }
            }
        }.launchIn(this)

        snapshotFlow {
            windowState.placement
        }.onEach {
            isMaximized = it == WindowPlacement.Maximized
        }.launchIn(this)
    }

    val trayState = rememberTrayState()

//    Tray(
//        state = trayState,
//        icon = TrayIcon,
//        menu = {
//            Item(
//                "Exit",
//                onClick = ::exitApplication
//            )
//        }
//    )

    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        title = "FreshApp",
        icon = painterResource("ic_logo.png")
    ) {
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

                    release("jbr-release-17.0.10b1087.23")

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
                CompositionLocalProvider(
                    LocalMainAppState provides MainAppState(config = Config(
                        deviceType = Config.DeviceTypes.DESKTOP,
                        viewModelStore = vmStoreImpl,
                        repositoryFactory = repositoryFactory,
                        storage = configStorage,
                    )),
                ) {
                    App()
                }
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

    vmStoreImpl.coroutineScope.listenToSharedEvents(trayState)
}

fun CoroutineScope.listenToSharedEvents(trayState: TrayState) = launch {
    SharedMemory.eventFlow.collect { event ->
        when (event) {
            is ShowDesktopNotificationSharedEvent -> {
                trayState.sendNotification(
                    Notification(
                        title = event.title,
                        message = event.message,
                        type = Notification.Type.Info
                    )
                )
            }

            is OnOpenExternalBrowserSharedEvent -> {
                openWebpage(event.url)
            }
        }
    }
}

