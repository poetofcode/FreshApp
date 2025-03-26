package specific

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

@Composable
expect fun AsyncImage(
    modifier: Modifier,
    url: String,
    contentScale: ContentScale = ContentScale.Fit,
    loadingView: @Composable () -> Unit,
    errorView: @Composable () -> Unit,
)