@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)

package presentation.screens.bookmarkTabScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.ExperimentalResourceApi
import presentation.Tabs
import presentation.navigation.BaseScreen
import presentation.navigation.NavStateImpl
import presentation.navigation.NavigateBackEffect
import presentation.navigation.NavigateEffect
import presentation.navigation.Navigator
import presentation.navigation.SharedMemory


class BookmarkTabScreen() : BaseScreen<BookmarkTabViewModel>() {

    override val screenId: String
        get() = Tabs.BOOKMARK.key

    override val viewModel: BookmarkTabViewModel
        get() = viewModelStore.getViewModel<BookmarkTabViewModel>()

    private val navState by lazy {
        NavStateImpl(viewModelStore).apply {
            // push(PostListScreen())
        }
    }

    override val isMenuVisible: Boolean = true

    @Composable
    override fun Content() {
        LaunchedEffect(Unit) {
            SharedMemory.effectFlow.collectLatest { effect ->
                when (effect) {
                    is NavigateBackEffect -> {
                        navState.pop()
                    }

                    is NavigateEffect -> {
                        navState.push(effect.screen)
                    }

                    else -> Unit
                }
            }
        }

        Navigator(modifier = Modifier.fillMaxSize(), state = navState)
    }

}
