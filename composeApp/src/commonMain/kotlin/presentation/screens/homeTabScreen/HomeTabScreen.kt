package presentation.screens.homeTabScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import presentation.Tabs
import presentation.navigation.*
import presentation.screens.postListScreen.PostListScreen

class HomeTabScreen() : BaseScreen<HomeTabViewModel>() {

    override val id: String
        get() = Tabs.HOME.key

    override val viewModel: HomeTabViewModel
        get() = viewModelStore.getViewModel<HomeTabViewModel>(id)

    private val navState by lazy {
        NavStateImpl(viewModelStore).apply {
            push(PostListScreen())
        }
    }

    @Composable
    override fun Content() {
        LaunchedEffect(Unit) {
            SharedMemory.effectFlow.collectLatest { effect ->
                when (effect) {
                    NavigateBackEffect -> {
                        navState.pop()
                    }

                    is NavigateEffect -> {
                        navState.push(effect.screen)
                    }
                }
            }
        }

        Navigator(modifier = Modifier.fillMaxSize(), state = navState)
    }

}
