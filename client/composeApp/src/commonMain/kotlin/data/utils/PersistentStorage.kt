package data.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import java.io.File
import kotlin.reflect.KProperty

import kotlinx.serialization.Serializer
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.float
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
        return try { stream.use { it.readText() } } catch (e: Throwable) {
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


interface PersistentStorage {

    fun save(key: String, param: Any)

    fun fetch(key: String): Any?

}

class ContentBasedPersistentStorage(
    private val contentProvider: ContentProvider
) : PersistentStorage {

    private val map: MutableMap<String, JsonElement> by lazy {
        val content = try {
            contentProvider.provideContent()
        } catch (e: Throwable) {
            "{}"
        }
        // json.decodeFromString<PreferencesInfo>(content).root.orEmpty().toMutableMap()
        val map = json.decodeFromString<Map<String, JsonElement>>(content)
        map.toMutableMap()
    }


    override fun save(key: String, param: Any) {
        map[key] = when (param) {
            is String -> JsonPrimitive(param)
            is Int -> JsonPrimitive(param)
            is Float -> JsonPrimitive(param)
            is Boolean -> JsonPrimitive(param)
            else -> JsonPrimitive(param.toString())
        }

        val str = json.encodeToString(map)

        println("mylog STR: $str")

        contentProvider.saveContent(str)
    }

    override fun fetch(key: String): Any? {
        val param = (map[key] as? JsonPrimitive) ?: return null
        return when {
            param.isString -> param.content
            param.booleanOrNull != null -> param.boolean
            param.intOrNull != null -> param.int
            param.floatOrNull != null -> param.float
            else -> null
        }
    }
}


inline operator fun <reified T : Any> PersistentStorage.getValue(nothing: Any?, property: KProperty<*>): T? {
    val propertyName = property.name
    val res = fetch(propertyName)
    return when (T::class) {
        String::class -> res?.toString() as? T
        Int::class -> res?.toString()?.toIntOrNull() as? T
        Float::class -> res?.toString()?.toFloatOrNull() as? T
        Boolean::class -> res?.toString()?.toBooleanStrictOrNull() as? T

        else -> null
    }
}

inline operator fun <reified T : Any> PersistentStorage.setValue(nothing: Any?, property: KProperty<*>, value: T?) {
    val propertyName = property.name
    value?.let {
        this.save(propertyName, value)
    }
}
