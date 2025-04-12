package specific

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.poetofcode.freshapp.utils.AndroidImageUtil

private val imageUtil = AndroidImageUtil()

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