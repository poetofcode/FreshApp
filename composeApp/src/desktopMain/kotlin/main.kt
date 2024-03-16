import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import data.repository.RepositoryFactoryImpl
import data.service.NetworkingFactory
import data.service.NetworkingFactoryImpl
import dev.datlag.kcef.KCEF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.json.Json.Default.encodeToString
import presentation.App
import presentation.base.Config
import presentation.base.ViewModelStore
import presentation.factories.viewModelFactories
import java.io.File
import kotlin.math.max
import kotlin.reflect.KProperty


@Serializable
data class Generic<T>(
    val data: T? = null,
    val extensions: Map<String, @kotlinx.serialization.Serializable(with = AnySerializer::class) Any>? = null
)

object AnySerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Any")

    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as JsonEncoder
        val jsonElement = serializeAny(value)
        jsonEncoder.encodeJsonElement(jsonElement)
    }

    private fun serializeAny(value: Any?): JsonElement = when (value) {
        is Map<*, *> -> {
            val mapContents = value.entries.associate { mapEntry ->
                mapEntry.key.toString() to serializeAny(mapEntry.value)
            }
            JsonObject(mapContents)
        }

        is List<*> -> {
            val arrayContents = value.map { listEntry -> serializeAny(listEntry) }
            JsonArray(arrayContents)
        }

        is Number -> JsonPrimitive(value)
        is Boolean -> JsonPrimitive(value)
        else -> JsonPrimitive(value.toString())
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement()

        return deserializeJsonElement(element)
    }

    private fun deserializeJsonElement(element: JsonElement): Any = when (element) {
        is JsonObject -> {
            element.mapValues { deserializeJsonElement(it.value) }
        }

        is JsonArray -> {
            element.map { deserializeJsonElement(it) }
        }

        is JsonPrimitive -> element.toString()
    }
}


interface ContentProvider {

    fun provideContent(): String

    fun saveContent(content: String)

}

class FileContentProvider(
    val fileName: String,
    val relativePath: String,
) : ContentProvider {
    override fun provideContent(): String {
        val cachePath = File("./", relativePath)
        cachePath.mkdirs()
        val stream = File("$cachePath/$fileName").bufferedReader()
        return stream.use { it.readText() }
    }

    override fun saveContent(content: String) {
        val cachePath = File("./", relativePath)
        cachePath.mkdirs()
        val stream = File("$cachePath/$fileName")
        stream.printWriter().use {
            it.write(content)
        }
    }

}


interface PersistentStorage {

    fun save(key: String, param: Any)

    fun fetch(key: String): Any?

}

class DesktopPersistentStorage(
    private val contentPorvider: ContentProvider
) : PersistentStorage {

    private val json by lazy {
        Json {
            ignoreUnknownKeys = true
        }
    }

    private val map: MutableMap<String, Any> by lazy {
        val content = try {
            contentPorvider.provideContent()
        } catch (e: Throwable) {
            "{}"
        }
        val map = json.decodeFromString<Generic<Unit>>(content).extensions?.toMutableMap() ?: mutableMapOf()
        map.toMap().toMutableMap()
    }


    override fun save(key: String, param: Any) {
        map[key] = param

        val str = json.encodeToString(Generic<Unit>(Unit, map))
        contentPorvider.saveContent(str)
    }

    override fun fetch(key: String): Any? {
        println("MAP: $map")
        return map[key].apply {
            println("map[$key] = $this@map")
        }
    }

    operator inline fun <reified T : Any> getValue(nothing: Nothing?, property: KProperty<*>): T? {
        val properyName = property.name
        val res = fetch(properyName)
        res?.let {
            println("RES: $res, clazz: ${res::class.java.typeName}")
        }
        return when (T::class) {
            String::class -> res?.toString() as? T
            Int::class -> res?.toString()?.toIntOrNull() as? T

            else -> null
        }
    }

    operator inline fun <reified T : Any> setValue(nothing: Nothing?, property: KProperty<*>, value: T?) {
        // println("$value has been assigned to '${property.name}' in $thisRef.")
        val propertyName = property.name
        value?.let {
            this.save(propertyName, value)
        }
    }

}


//private operator fun DesktopPersistentStorage.setValue(nothing: Nothing?, property: KProperty<*>, i: Int?) {
//    TODO("Not yet implemented")
//}
//
//private operator fun DesktopPersistentStorage.getValue(nothing: Nothing?, property: KProperty<*>): T? {
//    println("TypeName: ${T::class.java}, string:class = ${String::class.java}")
//    val properyName = property.name
//    return this.fetch(properyName) as T
//}


fun main() = application {
    // val repositoryFactory = MockRepositoryFactory()
    val networkingFactory: NetworkingFactory = NetworkingFactoryImpl()

    val repositoryFactory = RepositoryFactoryImpl(
        api = networkingFactory.createApi()
    )

    val vmStoreImpl = ViewModelStore(
        coroutineScope = rememberCoroutineScope(),
        vmFactories = viewModelFactories(repositoryFactory = repositoryFactory)
    )


//    val map : Map<String, Any?> = mapOf()
//
//    val storage: String by map


    val fileProvider = FileContentProvider(
        fileName = "config.json",
        relativePath = "appcache",
    )

    val storage = DesktopPersistentStorage(fileProvider)

    // val windowWidth: Int = storage.get("window_width")
    // val windowHeight: Int = storage.get("window_height")

    var windowWidth: Int? by storage
    var windowHeight: Int? by storage

    // storage.save("windowHeight", 100)

    // storage.save("my_param", "SOME VALUE")
    // val fetched = storage.fetch("my_param")
    println("FETCHED: $windowHeight")


    // println("windowsWidth: $windowWidth")

    val windowState = rememberWindowState(
        size = DpSize(windowWidth?.dp ?: 400.dp, windowHeight?.dp ?: 300.dp),
        position = WindowPosition(300.dp, 300.dp)
    )


    LaunchedEffect(windowState) {
        snapshotFlow {
            windowState.size
        }.onEach { snap ->
            windowWidth = snap.width.value.toInt()
            windowHeight = snap.height.value.toInt()
        }.launchIn(this)
    }

    Window(state = windowState, onCloseRequest = ::exitApplication, title = "FreshApp") {
        var restartRequired by remember { mutableStateOf(false) }
        var downloading by remember { mutableStateOf(0F) }
        var initialized by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                KCEF.init(builder = {
                    installDir(File("kcef-bundle"))
                    progress {
                        onDownloading {
                            downloading = max(it, 0F)
                        }
                        onInitialized {
                            initialized = true
                        }
                    }
                    settings {
                        cachePath = File("cache").absolutePath
                    }
                }, onError = {
                    it?.printStackTrace()
                }, onRestartRequired = {
                    restartRequired = true
                })
            }
        }

        if (restartRequired) {
            Text(text = "Restart required.")
        } else {
            if (initialized) {
                App(
                    Config(
                        viewModelStore = vmStoreImpl,
                        repositoryFactory = repositoryFactory,
                    )
                )

            } else {
                Text(text = "Downloading $downloading%")
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                KCEF.disposeBlocking()
            }
        }
    }
}
