package screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import base.ViewModel
import base.ViewModelFactory
import base.ViewModelStore
import navigation.BaseScreen
import navigation.NavStateImpl
import navigation.Navigator
import navigation.Screen
import screens.home_tab_screen.HomeTabViewModel

class HomeTabScreen : BaseScreen<HomeTabViewModel>() {

    override val id: String
        get() = Tabs.HOME.key

    override val viewModel: HomeTabViewModel
        get() = viewModelStore.getViewModel<HomeTabViewModel>(id)

//    private val navState = NavStateImpl().apply {
//        // push(PostListScreen())
//    }

    @Composable
    override fun Content() {
        // Navigator(modifier = Modifier.fillMaxSize(), state = navState)
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = viewModel.getScreenTitle())
            Spacer(Modifier.size(50.dp))
        }
    }


    /*
    inner class PostListScreen : Screen {
        override val id: String
            get() = "post_list"

        @Composable
        override fun Content() {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Лента постов (Список)")

                Spacer(Modifier.size(50.dp))

                Button(onClick = {
                    navState.push(PostDetailsScreen())
                }) {
                    Text(text = "Открыть детали")
                }
            }
        }

    }

    inner class PostDetailsScreen : Screen {
        override val id: String
            get() = "post_details"

        @Composable
        override fun Content() {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Лента постов (Детальный)")

                Spacer(Modifier.size(50.dp))

                Button(onClick = {
                    navState.pop()
                }) {
                    Text(text = "< Назад")
                }
            }
        }

    }
    */
}
