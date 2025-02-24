package data.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.float
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KProperty

/*
    Detecting the present annotations within the given object passed into a constructor
    https://stackoverflow.com/questions/4365095/detecting-the-present-annotations-within-the-given-object-passed-into-a-construc

    Serializer for class '...' is not found. Mark the class as @Serializable or provide the serializer explicitly
    https://stackoverflow.com/questions/71988144/serializer-for-class-is-not-found-mark-the-class-as-serializable-or-prov
 */

private val json = JsonProvider.json

//@Serializable
//data class PreferencesInfo(
//    val root: Map<String, @Serializable(with = AnySerializer::class) Any>? = null
//)

@Serializer(forClass = LocalDateTime::class)
object DateSerializer : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), formatter)
    }
}

//object AnySerializer : KSerializer<Any> {
//    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Any")
//
//    override fun serialize(encoder: Encoder, value: Any) {
//        val jsonEncoder = encoder as JsonEncoder
//        val jsonElement = serializeAny(value)
//        jsonEncoder.encodeJsonElement(jsonElement)
//    }
//
//    private fun serializeAny(value: Any?): JsonElement = when (value) {
//        is Map<*, *> -> {
//            val mapContents = value.entries.associate { mapEntry ->
//                mapEntry.key.toString() to serializeAny(mapEntry.value)
//            }
//            JsonObject(mapContents)
//        }
//
//        is List<*> -> {
//            val arrayContents = value.map { listEntry -> serializeAny(listEntry) }
//            JsonArray(arrayContents)
//        }
//
//        is Number -> JsonPrimitive(value)
//        is Boolean -> JsonPrimitive(value)
//        // is String -> JsonPrimitive(value)
//        else -> JsonPrimitive(value.toString())
//    }
//
//    override fun deserialize(decoder: Decoder): Any {
//        val jsonDecoder = decoder as JsonDecoder
//        val element = jsonDecoder.decodeJsonElement()
//        return deserializeJsonElement(element)
//    }
//
//    private fun deserializeJsonElement(element: JsonElement): Any = when (element) {
//        is JsonObject -> {
//            element.mapValues { deserializeJsonElement(it.value) }
//        }
//
//        is JsonArray -> {
//            element.map { deserializeJsonElement(it) }
//        }
//
//        is JsonPrimitive -> element.toString()
//    }
//}


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
        return try {
            stream.use { it.readText() }
        } catch (e: Throwable) {
            String()
        }
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


//interface PersistentStorage {
//
//    fun save(key: String, param: Any)
//
//    fun fetch(key: String): Any?
//
//}

class PersistentStorage(
    private val contentProvider: ContentProvider
) {
    private val _map: MutableMap<String, JsonElement> by lazy {
        val content = try {
            contentProvider.provideContent()
        } catch (e: Throwable) {
            "{}"
        }
        val map = json.decodeFromString<Map<String, JsonElement>>(content)
        map.toMutableMap()
    }

    val map: Map<String, JsonElement> = _map

    fun save(key: String, param: Any) {
        _map[key] = toJsonElement(param)
        val str = json.encodeToString(_map)
        contentProvider.saveContent(str)
    }

    fun deserializeAny(jsonElement: JsonElement): Any? {
        println("mylog JsonElement: $jsonElement, isPrimitive: ${jsonElement is JsonPrimitive}")
        return when (jsonElement) {
            is JsonPrimitive -> when {
                jsonElement.booleanOrNull != null -> jsonElement.boolean
                jsonElement.intOrNull != null -> {
                    println("mylog JsonElement int parsed: ${jsonElement.int}")
                    jsonElement.int
                }
                jsonElement.floatOrNull != null -> jsonElement.float
                jsonElement.isString -> jsonElement.content
                else -> null
            }

            is JsonArray -> jsonElement.map { deserializeAny(it) }

            else -> null
        }
    }

    inline fun <reified T : Any> fetch(key: String): T? {
        val res = map.get(key) ?: return null
        println("Fetch, key[$key] = $res")
        return (deserializeAny(res) as? T).apply {
            println("Fetch (2), key[$key] = $this")
        }
    }

    /*
            return when (T::class) {
            String::class -> {
                (res as? JsonPrimitive)?.contentOrNull as? T
            }

            Int::class -> {
                (res as? JsonPrimitive)?.intOrNull as? T
            }

            Float::class -> {
                (res as? JsonPrimitive)?.floatOrNull as? T
            }

            Boolean::class -> {
                (res as? JsonPrimitive)?.booleanOrNull as? T
            }

            List::class -> {
                (res as? JsonArray)?.forEach { element ->
                    {
                        when (element) {
                            is JsonPrimitive
                        }
                    }
                }

                else -> null
            }

     */
    private fun toJsonElement(param: Any): JsonElement {
        return when (param) {
            is String -> JsonPrimitive(param)
            is Int -> JsonPrimitive(param)
            is Float -> JsonPrimitive(param)
            is Boolean -> JsonPrimitive(param)

            is List<*> -> {
                val arrayContents = param.mapNotNull { listEntry ->
                    toJsonElement(listEntry!!)
                }
                JsonArray(arrayContents)
            }

            else -> JsonPrimitive(param.toString())
        }
    }
}


inline operator fun <reified T : Any> PersistentStorage.getValue(
    nothing: Any?,
    property: KProperty<*>
): T? {
    val propertyName = property.name
    return (fetch(propertyName) as? T)?.apply {
        println("mylog name: $propertyName, value: $this")
    }
}

inline operator fun <reified T : Any> PersistentStorage.setValue(
    nothing: Any?,
    property: KProperty<*>,
    value: T?
) {
    val propertyName = property.name
    value?.let {
        this.save(propertyName, value)
    }
}
