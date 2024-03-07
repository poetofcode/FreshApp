package presentation.screens.home_tab_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.model.PostModel
import presentation.Tabs
import presentation.navigation.BaseScreen

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
        LaunchedEffect(Unit) {
            viewModel.fetchFeed()
        }
        val posts by viewModel.posts.collectAsState()

        // Navigator(modifier = Modifier.fillMaxSize(), state = navState)
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            LazyColumn(
                modifier = Modifier.padding(),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(posts) { post ->
                    Post(post = post)
                }
            }
        }
    }

    @Composable
    private fun Post(post: PostModel) {
        // val context = LocalContext.current
        Column(
            modifier = Modifier
                .padding(vertical = 5.dp)
                .clickable {
                    // context.openLinkInBrowser(post.link ?: return@clickable)
                }
                .fillMaxWidth()
                .background(
                    color = Color.LightGray, // MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(8.dp)
        ) {
            post.image?.let { imageUrl ->
//                AsyncImage(
//                    modifier = Modifier.fillMaxWidth(),
//                    model = imageUrl,
//                    contentDescription = null,
//                    )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = post.title.orEmpty(), fontSize = 16.sp)
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
