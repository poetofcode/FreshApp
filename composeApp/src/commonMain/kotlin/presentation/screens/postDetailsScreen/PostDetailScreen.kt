package presentation.screens.postDetailsScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import presentation.navigation.BaseScreen

class PostDetailsScreen() : BaseScreen<PostDetailsViewModel>() {

    override val id: String
        get() = "PostDetailsScreen"

    override val viewModel: PostDetailsViewModel
        get() = viewModelStore.getViewModel<PostDetailsViewModel>(id)


    @Composable
    override fun Content() {
        Box(Modifier.fillMaxSize()) {
            Text(text = "Detail screen")
        }
    }

}
