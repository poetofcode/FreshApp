package specific

import DesktopImageUtil
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

private val imageUtil = DesktopImageUtil()

@Composable
actual fun AsyncImage(
    modifier: Modifier,
    url: String,
    contentScale: ContentScale,
    loadingView: @Composable () -> Unit,
    errorView: @Composable () -> Unit,
) {
    imageUtil.AsyncImage(url, contentScale, modifier)
}