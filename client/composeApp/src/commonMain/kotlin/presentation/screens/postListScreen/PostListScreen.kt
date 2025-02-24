@file:OptIn(ExperimentalLayoutApi::class)

package presentation.screens.postListScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import domain.model.CategoryModel
import domain.model.FeedQuery
import domain.model.isCategorySelected
import domain.model.isSourceSelected
import freshapp.composeapp.generated.resources.Res
import freshapp.composeapp.generated.resources.ic_cell_fav_disabled
import freshapp.composeapp.generated.resources.ic_cell_fav_enabled
import org.jetbrains.compose.resources.ExperimentalResourceApi
import presentation.composables.RoundedButton
import presentation.composables.muted
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.LoadingResource
import presentation.navigation.BaseScreen
import presentation.navigation.ShowModalBottomSheetEffect
import presentation.navigation.postSideEffect
import presentation.screens.sharedUi.Post
import presentation.screens.sharedUi.PostButton
import presentation.screens.sharedUi.PostButtonType
import presentation.screens.sharedUi.Posts
import presentation.theme.AppColors
import presentation.theme.AppTheme

@ExperimentalResourceApi
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
                    title = {
                        ChooseCategoryButton(
                            title = screenTitle,
                            onClick = {
                                postSideEffect(ShowModalBottomSheetEffect {
                                    ChooseCategoryDialog()
                                })
                            }
                        )
                    },
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
                        is CompleteResource -> Posts(posts, gridState) { post ->
                            Post(
                                post,
                                buttons = listOf(PostButtonType.FAVORITE)
                            ) { buttonType ->
                                when (buttonType) {
                                    PostButtonType.FAVORITE -> {
                                        val isChecked = post.isFavorite
                                        val icon = when (isChecked) {
                                            false -> Res.drawable.ic_cell_fav_disabled
                                            true -> Res.drawable.ic_cell_fav_enabled
                                        }
                                        val tintColor = when (isChecked) {
                                            true -> AppColors.favoriteRedColor
                                            false -> AppColors.iconMutedColor
                                        }

                                        PostButton(
                                            iconRes = icon,
                                            onClick = { viewModel.onFavoriteClick(post) },
                                            tintColor = tintColor,
                                        )
                                    }
                                }
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

    @Composable
    private fun ChooseCategoryButton(title: String, onClick: () -> Unit) {
        Row(
            modifier = Modifier.clickable {
                onClick()
            }.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title)
            Spacer(modifier = Modifier.size(20.dp))
            IconButton(onClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.List,
                    contentDescription = "Categories",
                )
            }
        }
    }

    @Composable
    private fun ChooseCategoryDialog() = Surface {
        with(viewModel.state.value) {
            Column(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sources list
                DialogTitle(text = "Источники")
                SourceList(sources = dashboard.sources)

                // Category list
                DialogTitle(text = "Категории")
                for (category in dashboard.categories) {
                    CategoryItem(category)
                }
            }
        }
    }

    @Composable
    private fun DialogTitle(text: String) {
        Text(text = text)
    }

    @Composable
    private fun SourceList(sources: List<String>) {
        FlowRow(
            modifier = Modifier.sizeIn(maxWidth = 300.dp),
            verticalArrangement = Arrangement.spacedBy(space = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(
                space = 10.dp,
                Alignment.CenterHorizontally
            ),
        ) {
            for (source in sources) {
                SourceItem(source)
            }
        }
    }


    @Composable
    private fun SourceItem(source: String) {
        val state = viewModel.state.value
        val isSelected = state.currentFeedQuery.isSourceSelected(source)
        RoundedButton(
            modifier = Modifier.padding(),
            title = source,
            solidColor = if (isSelected)
                AppColors.sourceSolidColor()
            else
                AppColors.sourceSolidColorUnselected()
        ) {
            viewModel.onSourceClick(source)
        }
    }

    @Composable
    private fun CategoryItem(category: CategoryModel) {
        val state = viewModel.state.value
        RoundedButton(
            modifier = Modifier.sizeIn(minWidth = 170.dp),
            solidColor = if (state.currentFeedQuery.isCategorySelected(category))
                AppColors.categorySolidColor
            else
                AppColors.categorySolidColorUnselected,
            borderColor = AppColors.categoryTextColor().muted(),
            content = {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = category.title,
                    color = AppColors.categoryTextColor(),
                )
            }
        ) {
            viewModel.onCategoryClick(category)
        }
    }

}


val PostListViewModel.State.screenTitle : String get() {
    val query = this.currentFeedQuery
    return when {
        query.category != null -> query.category.title
        query.sources.isNotEmpty() -> query.sources.joinToString(" / ")
        else -> "Все"
    }
}
