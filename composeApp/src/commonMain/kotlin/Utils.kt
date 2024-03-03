import androidx.compose.runtime.Composable

interface ImageUtil {

    @Composable
    fun AsyncImage(url: String)
}