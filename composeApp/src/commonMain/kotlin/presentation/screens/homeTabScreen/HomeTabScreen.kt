package presentation.screens.homeTabScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import presentation.Tabs
import presentation.navigation.BaseScreen
import presentation.navigation.NavStateImpl
import presentation.navigation.Navigator
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
        Navigator(modifier = Modifier.fillMaxSize(), state = navState)
    }

}
