@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)

package presentation.screens.postDetailsScreen

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import freshapp.composeapp.generated.resources.Res
import freshapp.composeapp.generated.resources.ic_cell_fav_disabled
import freshapp.composeapp.generated.resources.ic_cell_fav_enabled
import freshapp.composeapp.generated.resources.ic_open_in_new_24
import freshapp.composeapp.generated.resources.ic_share_24
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import presentation.LocalMainAppState
import presentation.base.Config
import presentation.navigation.BaseScreen
import presentation.theme.AppTheme
import specific.BackHandler


/*
 TODO Для Андроида:
  https://stackoverflow.com/questions/69495413/jetpack-compose-force-switch-night-notnight-resources

    Для Desktop: не нашёл пока способа переопределять системный флаг тёмной темы. В процессе..

 */


class PostDetailsScreen(
    val postUrl: String
) : BaseScreen<PostDetailsViewModel>() {

    override val viewModel by lazy { viewModelStore.getViewModel<PostDetailsViewModel>() }

    @Composable
    override fun Content() {
        LaunchedEffect(Unit) {
            viewModel.onScreenReady(postUrl)
        }

        val navigator = rememberWebViewNavigator()

        fun onBackClick(): Boolean {
            if (navigator.canGoBack) {
                navigator.navigateBack()
            } else {
                viewModel.onBackClick()
            }
            return true
        }

        BackHandler { onBackClick() }

        val initialUrl = postUrl
        val state = rememberWebViewState(url = initialUrl)
        val surfaceColor = MaterialTheme.colorScheme.surfaceContainer
        DisposableEffect(Unit) {
            state.webSettings.apply {
                isJavaScriptEnabled = true
                zoomLevel = 1.2

                // Mobile
                customUserAgentString =
                    "Mozilla/5.0 (Linux; Android 14; SM-N960U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.6312.80 Mobile Safari/537.36"

                // PC
                // customUserAgentString = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_1) AppleWebKit/625.20 (KHTML, like Gecko) Version/14.3.43 Safari/625.20"

                backgroundColor = surfaceColor

                androidWebSettings.apply {
                    isAlgorithmicDarkeningAllowed = false
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

        AppTheme {
            Column {
                val mainState = LocalMainAppState.current

                when (mainState.config.deviceType) {
                    Config.DeviceTypes.ANDROID -> mobileScreenLayout(
                        navigationBar = {
                            navigationBar(
                                state = state,
                                navigator = navigator,
                                onBackClick = { onBackClick() }
                            )
                        },
                        progressBar = {
                            progressBar(loadingState = state.loadingState)
                        },
                        webView = {
                            webContent(state = state, navigator = navigator)
                        }
                    )

                    Config.DeviceTypes.DESKTOP -> desktopScreenLayout(
                        navigationBar = {
                            navigationBar(
                                state = state,
                                navigator = navigator,
                                onBackClick = { onBackClick() }
                            )
                        },
                        progressBar = {
                            progressBar(loadingState = state.loadingState)
                        },
                        webView = {
                            webContent(state = state, navigator = navigator)
                        }
                    )
                }
            }
        }

    }

    @Composable
    fun webContent(state: WebViewState, navigator: WebViewNavigator) = WebView(
        state = state,
        modifier = Modifier
            .fillMaxSize(),
        navigator = navigator,
    )

    @Composable
    fun progressBar(loadingState: LoadingState) = LinearProgressIndicator(
        progress = (loadingState as? LoadingState.Loading)?.progress ?: 0f,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (loadingState is LoadingState.Loading) 1f else 0f),
    )

    // Url text field
    //
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun navigationBar(state: WebViewState, navigator: WebViewNavigator, onBackClick: () -> Unit) {
        var textFieldValue by remember(state.lastLoadedUrl) {
            mutableStateOf(state.lastLoadedUrl)
        }

        val interactionSource = remember { MutableInteractionSource() }
        // val isFieldFocused by interactionSource.collectIsFocusedAsState()

        var isSubmitVisible by remember { mutableStateOf(false) }

        fun checkSubmitVisibility(textFieldCurrentValue: String) {
            isSubmitVisible = textFieldCurrentValue != state.lastLoadedUrl
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                )
            }

            Box(modifier = Modifier.weight(1f)) {
//                        if (state.errorsForCurrentRequest.isNotEmpty()) {
//                            Image(
//                                imageVector = Icons.Default.Close,
//                                contentDescription = "Error",
//                                colorFilter = ColorFilter.tint(Color.Red),
//                                modifier = Modifier
//                                    .align(Alignment.CenterEnd)
//                                    .padding(8.dp),
//                            )
//                        }

                OutlinedTextField(
                    value = textFieldValue ?: "",
                    onValueChange = {
                        textFieldValue = it
                        checkSubmitVisibility(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    interactionSource = interactionSource,
                    maxLines = 1,
                )
            }

            if (isSubmitVisible) {
                Button(
                    onClick = {
                        textFieldValue?.let {
                            navigator.loadUrl(it)
                        }
                        isSubmitVisible = false
                    },
                    modifier = Modifier.align(Alignment.CenterVertically),
                ) {
                    Text("Go")
                }
            }

            // Context buttons
            //
            val isFavorite = viewModel.state.value.isFavorite
            Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                IconButton(onClick = {
                    // viewModel.onShareLink(textFieldValue.orEmpty())
                }) {
                    Icon(
                        painter = if (isFavorite) {
                            painterResource(Res.drawable.ic_cell_fav_enabled)
                        } else {
                            painterResource(Res.drawable.ic_cell_fav_disabled)
                        },
                        contentDescription = "Add to favorites",
                    )
                }

                platformSpecificActions(textFieldValue)
            }
        }
    }

    @Composable
    fun RowScope.platformSpecificActions(textFieldValue: String?) {
        val mainState = LocalMainAppState.current
        when (mainState.config.deviceType) {
            Config.DeviceTypes.ANDROID -> {
                IconButton(onClick = {
                    viewModel.onShareLink(textFieldValue.orEmpty())
                }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_share_24),
                        contentDescription = "Share",
                    )
                }
            }

            Config.DeviceTypes.DESKTOP -> {
                IconButton(onClick = {
                    viewModel.onOpenExternalBrowser(textFieldValue.orEmpty())
                }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_open_in_new_24),
                        contentDescription = "Open in browser",
                    )
                }
            }
        }
    }

    @Composable
    fun ColumnScope.mobileScreenLayout(
        navigationBar: @Composable () -> Unit,
        progressBar: @Composable () -> Unit,
        webView: @Composable () -> Unit,
    ) {
        Box(modifier = Modifier.weight(1f)) {
            webView()
        }
        progressBar()
        navigationBar()
    }

    @Composable
    fun ColumnScope.desktopScreenLayout(
        navigationBar: @Composable () -> Unit,
        progressBar: @Composable () -> Unit,
        webView: @Composable () -> Unit,
    ) {
        navigationBar()
        progressBar()
        Box(modifier = Modifier.weight(1f)) {
            webView()
        }
    }

}
