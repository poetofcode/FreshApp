@file:OptIn(ExperimentalMaterial3Api::class)

package presentation.screens.postDetailsScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
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

    override val viewModel: PostDetailsViewModel
        get() = viewModelStore.getViewModel<PostDetailsViewModel>()


    @Composable
    override fun Content() {
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

        var textFieldValue by remember(state.lastLoadedUrl) {
            mutableStateOf(state.lastLoadedUrl)
        }

        val interactionSource = remember { MutableInteractionSource() }
        val isFieldFocused by interactionSource.collectIsFocusedAsState()

        var isSubmitVisible by remember { mutableStateOf(false) }

        fun checkSubmitVisibility(textFieldCurrentValue: String) {
            isSubmitVisible = textFieldCurrentValue != state.lastLoadedUrl
        }

        AppTheme {
            Column {
//                TopAppBar(
//                    title = { Text(text = state.pageTitle ?: state.lastLoadedUrl ?: "Загрузка..") },
//                    navigationIcon = {
//                        IconButton(onClick = { onBackClick() }) {
//                            Icon(
//                                imageVector = Icons.Default.ArrowBack,
//                                contentDescription = "Back",
//                            )
//                        }
//                    },
//                )

                WebView(
                    state = state,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    navigator = navigator,
                )

                val loadingState = state.loadingState
                LinearProgressIndicator(
                    progress = (loadingState as? LoadingState.Loading)?.progress ?: 0f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (loadingState is LoadingState.Loading) 1f else 0f),
                )

                // Url field at bottom of page
                //
                Row {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    }

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
                            },
                            modifier = Modifier.align(Alignment.CenterVertically),
                        ) {
                            Text("Go")
                        }
                    }
                }
            }
        }

    }

}
