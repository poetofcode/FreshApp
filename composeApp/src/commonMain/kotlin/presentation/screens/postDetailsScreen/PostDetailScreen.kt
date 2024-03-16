package presentation.screens.postDetailsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import presentation.navigation.BaseScreen
import presentation.navigation.NavigateBackEffect
import presentation.navigation.SharedMemory

class PostDetailsScreen(
    val postUrl: String
) : BaseScreen<PostDetailsViewModel>() {

    override val id: String
        get() = "PostDetailsScreen"

    override val viewModel: PostDetailsViewModel
        get() = viewModelStore.getViewModel<PostDetailsViewModel>(id)


    @Composable
    override fun Content() {
        Column(Modifier.fillMaxSize()) {
            OutlinedButton(
                modifier = Modifier.background(Color.Transparent).padding(10.dp),
                onClick = {
                    SharedMemory.effectFlow.tryEmit(NavigateBackEffect)
                }) {
                Text(text = "←", fontSize = 24.sp)
            }

            val webViewState = rememberWebViewState(postUrl)
            WebView(state = webViewState, modifier = Modifier.fillMaxSize())

            DisposableEffect(Unit) {
                webViewState.webSettings.apply {
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
        }
    }

}
