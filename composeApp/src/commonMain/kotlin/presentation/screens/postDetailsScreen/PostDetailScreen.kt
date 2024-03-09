package presentation.screens.postDetailsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import presentation.navigation.BaseScreen
import presentation.navigation.NavigateBackEffect
import presentation.navigation.NavigateEffect
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
        Box(Modifier.fillMaxSize()) {
            val state = rememberWebViewState(postUrl)
            WebView(state = state, modifier = Modifier.fillMaxSize())

            OutlinedButton(
                modifier = Modifier.align(Alignment.TopStart).background(Color.Transparent).padding(10.dp),
                onClick = {
                    SharedMemory.effectFlow.tryEmit(NavigateBackEffect)
                }) {
                Text(text = "←", fontSize = 24.sp)
            }
        }
    }

}
