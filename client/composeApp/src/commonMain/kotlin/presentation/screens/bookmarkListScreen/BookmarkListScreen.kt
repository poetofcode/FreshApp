package presentation.screens.bookmarkListScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import freshapp.composeapp.generated.resources.Res
import freshapp.composeapp.generated.resources.ic_delete_24
import org.jetbrains.compose.resources.ExperimentalResourceApi
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.LoadingResource
import presentation.navigation.BaseScreen
import presentation.screens.sharedUi.Post
import presentation.screens.sharedUi.PostButton
import presentation.screens.sharedUi.PostButtonType
import presentation.screens.sharedUi.Posts
import presentation.theme.AppColors
import presentation.theme.AppTheme


@ExperimentalResourceApi
@ExperimentalMaterial3Api
class BookmarkListScreen : BaseScreen<BookmarkListViewModel>() {

    override val viewModel: BookmarkListViewModel
        get() = viewModelStore.getViewModel<BookmarkListViewModel>(screenId)

    override val isMenuVisible: Boolean = true

    private val gridState = LazyGridState()

    @Composable
    override fun Content() = with(viewModel.state.value) {
        AppTheme {
            Column {
                TopAppBar(
                    title = { Text(text = "Избранное") },
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
                        is CompleteResource -> {
                            if (posts.isNotEmpty()) {
                                Posts(
                                    posts = posts,
                                    gridState = gridState,
                                    lifecycleScope = lifecycleScope,
                                ) { post ->
                                    Post(
                                        post,
                                        buttons = listOf(PostButtonType.FAVORITE)
                                    ) { buttonType ->
                                        when (buttonType) {
                                            PostButtonType.FAVORITE -> {
                                                PostButton(
                                                    iconRes = Res.drawable.ic_delete_24,
                                                    onClick = { viewModel.onRemoveFavoriteClick(post) },
                                                    tintColor = AppColors.iconMutedColor,
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = "Вы пока ничего не добавили",
                                    color = AppColors.iconMutedColor
                                )
                            }
                        }

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

}
