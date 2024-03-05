package screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import navigation.NavStateImpl
import navigation.Navigator
import navigation.Screen

class HomeTabScreen : Screen {
    override val id: String
        get() = Tabs.HOME.key

    private val navState = NavStateImpl().apply {
        push(PostListScreen())
    }

    @Composable
    override fun Content() {
        Navigator(modifier = Modifier.fillMaxSize(), state = navState)
    }


    inner class PostListScreen : Screen {
        override val id: String
            get() = "post_list"

        @Composable
        override fun Content() {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Лента постов (Список)")
            }
        }

    }

    inner class PostDetailsScreen : Screen {
        override val id: String
            get() = "post_details"

        @Composable
        override fun Content() {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Лента постов (Детальный)")
            }
        }

    }
}
