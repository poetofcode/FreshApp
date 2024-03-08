package specific

import androidx.compose.runtime.Composable
import com.poetofcode.freshapp.AndroidImageUtil

private val imageUtil = AndroidImageUtil()

@Composable
actual fun AsyncImage(url: String) {
    imageUtil.AsyncImage(url)
}