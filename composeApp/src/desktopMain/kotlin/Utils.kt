import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO

class DesktopImageUtil : ImageUtil {

    @Composable
    override fun AsyncImage(url: String) {
        val scope = rememberCoroutineScope()
        val imageBitmapState = remember { mutableStateOf<Resource<ImageBitmap>>(IdleResource) }

        when (val bitmap = imageBitmapState.value) {
            is CompleteResource -> {
                Image(
                    bitmap = bitmap.result,
                    ""
                )
            }
            IdleResource, LoadingResource -> {
                Box(Modifier.padding(20.dp)) {
                    Text(text = "Загрузка", color = Color.Blue)
                }
            }
            is ExceptionResource -> {
                Box(Modifier.padding(20.dp)) {
                    Text(text = "Ошибка", color = Color.Red)
                }
            }
        }

        LaunchedEffect(Unit) {
            scope.launch {
                if (imageBitmapState.value is IdleResource) {
                    imageBitmapState.value = LoadingResource

                    imageBitmapState.value = try {
                        val imageRes = loadNetworkImage(url)
                        CompleteResource(imageRes)
                    } catch (e: Throwable) {
                        ExceptionResource(e)
                    }
                }
            }
        }
    }
}

sealed interface Resource<out T>

data object IdleResource : Resource<Nothing>

data class CompleteResource<T>(val result: T) : Resource<T>

data class ExceptionResource(val exception: Throwable) : Resource<Nothing>

data object LoadingResource : Resource<Nothing>


suspend fun loadNetworkImage(link: String): ImageBitmap = withContext(Dispatchers.IO) {
    val url = URL(link)
    val connection = url.openConnection() as HttpURLConnection
    connection.connect()

    val inputStream = connection.inputStream
    val bufferedImage = ImageIO.read(inputStream)

    val stream = ByteArrayOutputStream()
    ImageIO.write(bufferedImage, "png", stream)
    val byteArray = stream.toByteArray()

    return@withContext Image.makeFromEncoded(byteArray).asImageBitmap()
}