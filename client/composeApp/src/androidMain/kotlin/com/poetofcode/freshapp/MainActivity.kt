package com.poetofcode.freshapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStarted
import com.poetofcode.freshapp.utils.openLink
import com.poetofcode.freshapp.utils.shareLink
import data.repository.RepositoryFactoryImpl
import data.service.NetworkingFactory
import data.service.NetworkingFactoryImpl
import data.utils.AppDataStorageImpl
import data.utils.PersistentStorage
import data.utils.ProfileStorageImpl
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import presentation.App
import presentation.LocalMainAppState
import presentation.MainAppState
import presentation.base.Config
import presentation.base.ViewModelStore
import presentation.factories.viewModelFactories
import presentation.model.shared.OnOpenExternalBrowserSharedEvent
import presentation.model.shared.OnReceivedTokenSharedEvent
import presentation.model.shared.OnShareLinkSharedEvent
import presentation.navigation.SetBackHandlerEffect
import presentation.navigation.SharedMemory
import specific.AndroidContentProvider


class MainActivity : ComponentActivity() {
    // val repositoryFactory = MockRepositoryFactory()
    private val profileStorage = ProfileStorageImpl(
        AndroidContentProvider(
            fileName = "sessioncache.json",
            context = this,
        )
    )

    private val networkingFactory: NetworkingFactory = NetworkingFactoryImpl(
        profileStorage,
        Config.DeviceTypes.ANDROID,
    )

    private val configStorage = PersistentStorage(
        AndroidContentProvider(
            fileName = "config.json",
            context = this,
        )
    )

    private val appDataStorage = AppDataStorageImpl(
        AndroidContentProvider(
            fileName = "appdata.json",
            context = this,
        )
    )

    private val repositoryFactory = RepositoryFactoryImpl(
        api = networkingFactory.createApi(),
        profileStorage = profileStorage,
        appDataStorage = appDataStorage,
    )

    private var backHandleCallback: (() -> Boolean)? = null

    private val vmStoreImpl = ViewModelStore(
        coroutineScope = lifecycleScope,
        vmFactories = viewModelFactories(
            repositoryFactory = repositoryFactory,
            configStorage = configStorage,
        )
    )

    private var firebasePushToken: String? = null

    private val profileRepository by lazy {
        repositoryFactory.createProfileRepository()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Делает активити защищённым от скриншотов, отображения контента в миниатюре и т.д.
        // window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        retrieveFirebasePushToken { token ->
            val msg = "FCM Token: $token"
            Log.d("mylog", msg)
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

            firebasePushToken = token
            saveTokenOnServer()
        }

        setContent {
            CompositionLocalProvider(
                LocalMainAppState provides MainAppState(
                    config = Config(
                        deviceType = Config.DeviceTypes.ANDROID,
                        viewModelStore = vmStoreImpl,
                        repositoryFactory = repositoryFactory,
                        storage = configStorage
                    ))
                ) {
                    App()
                }
        }

        lifecycleScope.launch {
            SharedMemory.effectFlow
                .onEach { effect ->
                    if (effect is SetBackHandlerEffect) {
                        backHandleCallback = effect.cb
                    }
                }.launchIn(this)
        }

        listenToSharedEvents()
    }

    private fun listenToSharedEvents() {
        lifecycleScope.launch {
            withStarted {}

            SharedMemory.eventFlow.collect { event ->
                when (event) {
                    is OnReceivedTokenSharedEvent -> {
                        saveTokenOnServer()
                    }

                    is OnShareLinkSharedEvent -> {
                        shareLink(event.url)
                    }

                    is OnOpenExternalBrowserSharedEvent -> {
                        openLink(event.url)
                    }
                }
            }
        }
    }

    private fun saveTokenOnServer() {
        lifecycleScope.launch {
            try {
                profileRepository.saveFirebasePushToken(firebasePushToken ?: return@launch)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    override fun onBackPressed() {
        backHandleCallback?.let { cb ->
            if (cb()) {
                return
            } else {
                return@let
            }
        }
        super.onBackPressed()
    }

}

@Preview
@Composable
fun AppAndroidPreview() {
//
//    val vmStoreImpl = ViewModelStore(
//        coroutineScope = rememberCoroutineScope(),
//        vmFactories = viewModelFactories
//    )
//    App(Config(viewModelStore = vmStoreImpl))
}