package presentation.screens.postDetailsScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import presentation.navigation.SetBackHandlerEffect
import presentation.navigation.BaseScreen
import presentation.navigation.NavigateBackEffect
import presentation.navigation.SharedMemory
import specific.BackHandler

class PostDetailsScreen(
    val postUrl: String
) : BaseScreen<PostDetailsViewModel>() {

    override val viewModel: PostDetailsViewModel
        get() = viewModelStore.getViewModel<PostDetailsViewModel>()


    @Composable
    override fun Content() {
        BackHandler {
            viewModel.onBackClick()
            true
        }

        val initialUrl = postUrl
        val state = rememberWebViewState(url = initialUrl)
        DisposableEffect(Unit) {
            state.webSettings.apply {
                isJavaScriptEnabled = true
                zoomLevel = 1.2
                // customUserAgentString = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1) AppleWebKit/625.20 (KHTML, like Gecko) Version/14.3.43 Safari/625.20"
                backgroundColor = Color.White

                androidWebSettings.apply {
                    isAlgorithmicDarkeningAllowed = true
                    safeBrowsingEnabled = true
                    supportZoom = true
                    domStorageEnabled = true
                }

                desktopWebSettings.apply {
                    offScreenRendering = false
                    // transparent = false
                }
            }
            onDispose { }
        }
        val navigator = rememberWebViewNavigator()
        var textFieldValue by remember(state.lastLoadedUrl) {
            mutableStateOf(state.lastLoadedUrl)
        }
        MaterialTheme {
            Column {
                TopAppBar(
                    title = { Text(text = state.pageTitle ?: state.lastLoadedUrl ?: "Загрузка..") },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (!navigator.canGoBack) {
                                SharedMemory.effectFlow.tryEmit(NavigateBackEffect)
                            } else {
                                navigator.navigateBack()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    },
                )

                Row {
                    Box(modifier = Modifier.weight(1f)) {
                        if (state.errorsForCurrentRequest.isNotEmpty()) {
                            Image(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Error",
                                colorFilter = ColorFilter.tint(Color.Red),
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(8.dp),
                            )
                        }

                        OutlinedTextField(
                            value = textFieldValue ?: "",
                            onValueChange = { textFieldValue = it },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                        )
                    }

                    Button(
                        onClick = {
                            textFieldValue?.let {
                                navigator.loadUrl(it)
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterVertically),
                    ) {
                        Text("Go")
                    }
                }

                val loadingState = state.loadingState
                if (loadingState is LoadingState.Loading) {
                    LinearProgressIndicator(
                        progress = loadingState.progress,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                WebView(
                    state = state,
                    modifier = Modifier.fillMaxSize(),
                    navigator = navigator,
                )
            }
        }

    }

}
