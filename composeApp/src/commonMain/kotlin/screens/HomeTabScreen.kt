package screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
}
