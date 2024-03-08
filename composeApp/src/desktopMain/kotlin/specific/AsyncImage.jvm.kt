package specific

import DesktopImageUtil
import androidx.compose.runtime.Composable

private val imageUtil = DesktopImageUtil()

@Composable
actual fun AsyncImage(url: String) {
    imageUtil.AsyncImage(url)
}