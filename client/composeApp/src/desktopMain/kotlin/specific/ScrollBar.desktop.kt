package specific

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.v2.ScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import presentation.theme.AppColors
import specific.ScrollBarOrientation.HORIZONTAL
import specific.ScrollBarOrientation.VERTICAL

private val scrollBarStyle
    @Composable get() = defaultScrollbarStyle().copy(
        hoverColor = AppColors.scrollBarHoverBackgroundColor,
        unhoverColor = AppColors.scrollBarBackgroundColor
    )


@Composable
actual fun ScrollBar(
    modifier: Modifier,
    orientation: ScrollBarOrientation,
    state: ScrollableComponentState
) {
    val adapter: ScrollbarAdapter = when (state) {
        is ScrollableComponentState.LazyListComponentState -> {
            rememberScrollbarAdapter(state.state)
        }

        is ScrollableComponentState.LazyGridComponentState -> {
            rememberScrollbarAdapter(state.state)
        }

        is ScrollableComponentState.ColumnComponentState -> {
            rememberScrollbarAdapter(state.state)
        }

        else -> throw Exception("Not implemented")
    }

    when (orientation) {
        VERTICAL -> {
            VerticalScrollbar(
                modifier = modifier,
                adapter = adapter,
                style = scrollBarStyle
            )
        }

        HORIZONTAL -> {
            HorizontalScrollbar(
                modifier = modifier,
                adapter = adapter,
                style = scrollBarStyle
            )
        }
    }
}