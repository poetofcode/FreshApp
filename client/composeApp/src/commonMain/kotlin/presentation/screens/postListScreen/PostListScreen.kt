package presentation.screens.postListScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.model.PostModel
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.LoadingResource
import presentation.navigation.BaseScreen
import presentation.navigation.NavigateEffect
import presentation.navigation.SharedMemory
import presentation.screens.postDetailsScreen.PostDetailsScreen
import presentation.theme.AppTheme
import specific.AsyncImage
import specific.ScrollBar
import specific.ScrollBarOrientation
import specific.ScrollableComponentState

@ExperimentalMaterial3Api
class PostListScreen : BaseScreen<PostListViewModel>() {

    override val viewModel: PostListViewModel
        get() = viewModelStore.getViewModel<PostListViewModel>(screenId)

    override val isMenuVisible: Boolean = true

    private val gridState = LazyGridState()

    @Composable
    override fun Content() = with(viewModel.state.value) {
        AppTheme {
            Column {
                TopAppBar(
                    title = { Text(text = "Лента") },
                    navigationIcon = {},
                    actions = {
                        if (readyState !is LoadingResource) {
                            IconButton(onClick = {
                                viewModel.fetchFeed()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reload",
                                )
                            }
                        }
                    }
                )

                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
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
                }

            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun Posts(posts: List<PostModel>) {

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxHeight().padding().weight(1f),
                state = gridState,
                columns = GridCells.Adaptive(minSize = 300.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                userScrollEnabled = true,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(posts) { post ->
                    Post(post = post)
                }
            }

            ScrollBar(
                modifier =  Modifier.width(20.dp).fillMaxHeight(),
                orientation = ScrollBarOrientation.VERTICAL,
                state = ScrollableComponentState.LazyGridComponentState(gridState)
            )
        }
    }

    @Composable
    private fun Post(post: PostModel) = Surface {
        // val context = LocalContext.current
        Column(
            modifier = Modifier
                .clickable {
                    SharedMemory.effectFlow.tryEmit(
                        NavigateEffect(
                            PostDetailsScreen(
                                postUrl = post.link
                            )
                        )
                    )
                }
                .padding(vertical = 5.dp)
                .fillMaxWidth()
                .sizeIn(minHeight = 300.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(8.dp)
        ) {
            post.image?.let { imageUrl ->
                AsyncImage(
                    modifier = Modifier.height(250.dp),
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
