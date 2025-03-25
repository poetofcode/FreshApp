@file:OptIn(ExperimentalResourceApi::class)

package presentation.screens.sharedUi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.model.PostModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import presentation.navigation.NavigateEffect
import presentation.navigation.SharedMemory
import presentation.screens.postDetailsScreen.PostDetailsScreen
import specific.AsyncImage
import specific.ScrollBar
import specific.ScrollBarOrientation
import specific.ScrollableComponentState
import java.time.format.DateTimeFormatter

private const val PAGE_COUNT = 5


@Composable
fun Posts(
    posts: List<PostModel>,
    gridState: LazyGridState,
    lifecycleScope: CoroutineScope,
    canLoadMore: () -> Boolean = { false },
    loadNextPage: () -> Unit = {},
    bottomContent: @Composable () -> Unit = {},
    postContent: @Composable (PostModel) -> Unit = { Post(it) },
) {
    val firstItemVisible by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex == 0
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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
                itemsIndexed(posts) { postIndex, post ->
                    LaunchedEffect(post) {
                        val indexHalfOfLastPage = posts.size - PAGE_COUNT / 2 - 1
                        if (postIndex > indexHalfOfLastPage) {
                            if (canLoadMore()) {
                                loadNextPage()
                            }
                        }
                    }
                    postContent(post)
                }
                item { bottomContent() }
            }

            ScrollBar(
                modifier = Modifier.width(20.dp).fillMaxHeight(),
                orientation = ScrollBarOrientation.VERTICAL,
                state = ScrollableComponentState.LazyGridComponentState(gridState)
            )
        }

        AnimatedVisibility(
            visible = !firstItemVisible,
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        ) {
            FloatingActionButton(
                modifier = Modifier,
                onClick = {
                    scrollTop(
                        lifecycleScope = lifecycleScope,
                        gridState = gridState,
                        isAnimate = true
                    )
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

private fun scrollTop(
    lifecycleScope: CoroutineScope,
    gridState: LazyGridState,
    isAnimate: Boolean = false
) {
    lifecycleScope.launch {
        if (isAnimate) {
            gridState.animateScrollToItem(0)
        } else {
            gridState.scrollToItem(0)
        }
    }
}


// TODO move to /domain/entities
enum class PostButtonType {
    FAVORITE
}

@Composable
fun Post(
    post: PostModel,
    buttons: List<PostButtonType> = emptyList(),
    buttonContent: @Composable (PostButtonType) -> Unit = {},
) = Surface {
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
        Text(
            modifier = Modifier.padding(horizontal = 10.dp),
            text = post.title,
            fontSize = 17.sp
        )

        // Bottom buttons (Fav and etc)
        //
        if (buttons.isNotEmpty()) {
            Row(modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)) {
                // Метка "Source"
                Text(
                    text = post.source.uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                )

                // Метка "даты-время"
                Text(
                    modifier = Modifier.padding(start = 10.dp).alpha(0.5f),
                    text = post.createdAt.format(DateTimeFormatter.ofPattern("dd MMM HH:mm")).toString(),
                    fontSize = 14.sp,
                )

                Spacer(modifier = Modifier.weight(1f))

                buttons.forEach { buttonType ->
                    buttonContent(buttonType)
                }
            }
        }
    }
}


@Composable
fun PostButton(
    iconRes: DrawableResource,
    onClick: () -> Unit,
    tintColor: Color? = null,
) {
    Box(modifier = Modifier.clickable {
        onClick()
    }) {
        val imageModifier = Modifier.height(21.dp)
        if (tintColor != null) {
            Image(
                modifier = imageModifier,
                painter = painterResource(iconRes),
                contentDescription = null,
                contentScale = ContentScale.None,
                colorFilter = ColorFilter.tint(tintColor),
            )
        } else {
            Image(
                modifier = imageModifier,
                painter = painterResource(iconRes),
                contentDescription = null,
                contentScale = ContentScale.None,
            )
        }
    }
}