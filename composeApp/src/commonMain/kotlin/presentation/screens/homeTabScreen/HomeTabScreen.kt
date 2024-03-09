package presentation.screens.homeTabScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedButton
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
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.IdleResource
import presentation.model.LoadingResource
import presentation.navigation.BaseScreen
import presentation.screens.homeTabScreen.HomeTabViewModel
import specific.AsyncImage

class HomeTabScreen() : BaseScreen<HomeTabViewModel>() {

    override val id: String
        get() = Tabs.HOME.key

    override val viewModel: HomeTabViewModel
        get() = viewModelStore.getViewModel<HomeTabViewModel>(id)

//    private val navState = NavStateImpl().apply {
//        // push(PostListScreen())
//    }

    @Composable
    override fun Content() = with(viewModel.state.value) {
        LaunchedEffect(Unit) {
            viewModel.fetchFeed()
        }

        // Navigator(modifier = Modifier.fillMaxSize(), state = navState)
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (readyState) {
                is CompleteResource -> Posts(posts)

                is ExceptionResource -> {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Ошибка загрузки", color = Color.Red)
                        Spacer(Modifier.size(10.dp))
                        Text(text = "${readyState.exception}")
                    }
                }

                else -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (readyState !is LoadingResource) {
                OutlinedButton(
                    modifier = Modifier.align(Alignment.TopEnd).background(Color.Transparent).padding(10.dp),
                    onClick = {
                        viewModel.fetchFeed()
                    }) {
                    Text(text = "🔁", fontSize = 24.sp)
                }
            }
        }
    }

    @Composable
    private fun Posts(posts: List<PostModel>) {
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
                AsyncImage(
                    modifier = Modifier,
                    url = imageUrl,
                    loadingView = {},
                    errorView = {}
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = post.title.orEmpty(), fontSize = 16.sp)
        }
    }

}
