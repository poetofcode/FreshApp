package presentation.screens.bookmarkTabScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import presentation.Tabs
import presentation.base.BaseViewModel
import presentation.base.EmptyViewModel
import presentation.navigation.BaseScreen


class BookmarkTabScreen : BaseScreen<EmptyViewModel>() {
    override val screenId: String
        get() = Tabs.BOOKMARK.key

    override val viewModel: EmptyViewModel
        get() = EmptyViewModel.INSTANCE

    override val isMenuVisible: Boolean = true
    
    @Composable
    override fun Content() {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "TODO: Избранное")
        }
    }

}