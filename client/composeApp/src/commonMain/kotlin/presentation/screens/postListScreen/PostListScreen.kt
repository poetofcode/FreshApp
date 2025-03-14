@file:OptIn(ExperimentalLayoutApi::class)

package presentation.screens.postListScreen

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import domain.model.CategoryModel
import domain.model.isCategorySelected
import domain.model.isSourceSelected
import freshapp.composeapp.generated.resources.Res
import freshapp.composeapp.generated.resources.ic_cell_fav_disabled
import freshapp.composeapp.generated.resources.ic_cell_fav_enabled
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import presentation.composables.RoundedButton
import presentation.composables.muted
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.LoadingResource
import presentation.navigation.BaseScreen
import presentation.navigation.SideEffect
import presentation.navigation.ShowModalBottomSheetEffect
import presentation.navigation.postSideEffect
import presentation.screens.sharedUi.Post
import presentation.screens.sharedUi.PostButton
import presentation.screens.sharedUi.PostButtonType
import presentation.screens.sharedUi.Posts
import presentation.theme.AppColors
import presentation.theme.AppTheme


object ScrollToTopSideEffect : SideEffect

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
                                viewModel.resetAndFetch()
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
                    Posts(
                        posts = posts,
                        gridState = gridState,
                        canLoadMore = {
                            isNextAllowed
                        },
                        loadNextPage = {
                            viewModel.fetchFeed()
                        },
                        bottomContent = {
                            if (readyState is LoadingResource && !isLoadingFromScratch()) {
                                LoadingOverlay(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )
                            }

                            if (readyState is ExceptionResource && !isLoadingFromScratch()) {
                                ErrorOverlay(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )
                            }
                        }
                    ) { post ->
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

                    if (readyState is LoadingResource && isLoadingFromScratch()) {
                        LoadingOverlay()
                    }

                    if (readyState is ExceptionResource && isLoadingFromScratch()) {
                        ErrorOverlay()
                    }

                    val firstItemVisible by remember {
                        derivedStateOf {
                            gridState.firstVisibleItemIndex == 0
                        }
                    }

                    this@Column.AnimatedVisibility(
                        visible = !firstItemVisible,
                        modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
                    ) {
                        FloatingActionButton(
                            modifier = Modifier,
                            onClick = {
                                scrollTop(isAnimate = true)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = null,
                            )
                        }
                    }
                }

            }
        }
    }

    override fun obtainSideEffect(effect: SideEffect) {
        when (effect) {
            is ScrollToTopSideEffect -> scrollTop()
        }
    }

    private fun scrollTop(isAnimate: Boolean = false) {
        lifecycleScope.launch {
            if (isAnimate) {
                gridState.animateScrollToItem(0)
            } else {
                gridState.scrollToItem(0)
            }
        }
    }

    @Composable
    private fun ChooseCategoryButton(title: String, onClick: () -> Unit) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable {
                onClick()
            }.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                overflow = TextOverflow.Ellipsis, maxLines = 1
            )
            Icon(
                modifier = Modifier,
                imageVector = Icons.AutoMirrored.Default.List,
                contentDescription = "Categories",
            )
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
        Row {
            // Select 'only it' button
            RoundedButton(
                modifier = Modifier.padding(),
                title = source,
                shape = RoundedCornerShape(
                    topStart = 10.dp,
                    bottomStart = 10.dp,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp
                ),
                borderColor = Color.Transparent,
                solidColor = if (isSelected)
                    AppColors.sourceSolidColor()
                else
                    AppColors.sourceSolidColorUnselected()
            ) {
                viewModel.onSourceClick(source)
            }
            Spacer(Modifier.size(1.dp))
            // (+) / (-)
            RoundedButton(
                modifier = Modifier.padding(),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    bottomStart = 0.dp,
                    topEnd = 10.dp,
                    bottomEnd = 10.dp
                ),
                // title = if (isSelected) "-" else "+",
                borderColor = Color.Transparent,
                solidColor = if (isSelected)
                    AppColors.sourceSolidColor()
                else
                    AppColors.sourceSolidColorUnselected(),
                content = {
                    Icon(
                        imageVector = if (isSelected)
                            Icons.Default.Clear
                        else
                            Icons.Default.Add,
                        contentDescription = "+/-",
                    )
                }
            ) {
                viewModel.onSourceAddRemoveClick(source)
            }
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


val PostListViewModel.State.screenTitle: String
    get() {
        val query = this.currentFeedQuery
        return when {
            query.category != null -> query.category.title
            query.sources.isNotEmpty() -> query.sources.joinToString(" / ")
            else -> "Все"
        }
    }


@Composable
fun ErrorOverlay(
    modifier: Modifier = Modifier.fillMaxSize(),
    exception: Throwable? = null,
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Ошибка загрузки", color = Color.Red)
        if (exception != null) {
            Spacer(Modifier.size(10.dp))
            Text(text = "${exception}")
        }
    }
}

@Composable
fun LoadingOverlay(
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

fun PostListViewModel.State.isLoadingFromScratch(): Boolean {
    return lastTimestamp == 0L
}