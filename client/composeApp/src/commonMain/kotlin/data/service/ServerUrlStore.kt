package data.service

import com.poetofcode.freshapp.BuildConfig
import data.utils.PersistentStorage

class ServerUrlStore(
    private val storage: PersistentStorage,
    defaultUrl: String? = null,
) {

    private val defaultUrl: String = defaultUrl
        ?.let(::resolveDefaultUrl)
        ?: resolveDefaultUrl(BuildConfig.SERVER_URL)

    private var cachedUrl: String? = storage.fetch<String>(STORAGE_KEY)
        ?.takeIf { it.isNotBlank() }

    val currentDefaultUrl: String
        get() = defaultUrl

    val currentUrl: String
        get() = cachedUrl ?: defaultUrl

    val isDefault: Boolean
        get() = cachedUrl == null || cachedUrl == defaultUrl

    fun save(raw: String): ServerUrlValidation {
        val validation = validate(raw)
        if (validation is ServerUrlValidation.Invalid) {
            return validation
        }

        val normalized = (validation as ServerUrlValidation.Valid).url
        if (normalized == defaultUrl) {
            reset()
        } else {
            cachedUrl = normalized
            storage.save(STORAGE_KEY, normalized)
        }
        return validation
    }

    fun reset() {
        cachedUrl = null
        storage.remove(STORAGE_KEY)
    }

    fun validate(raw: String): ServerUrlValidation {
        val candidate = normalize(raw)
        if (candidate.isEmpty()) {
            return ServerUrlValidation.Invalid(EMPTY_ERROR)
        }
        val lowercased = candidate.lowercase()
        if (!lowercased.startsWith("http://") && !lowercased.startsWith("https://")) {
            return ServerUrlValidation.Invalid(SCHEME_ERROR)
        }
        val host = candidate
            .substringAfter("://")
            .substringBefore('/')
            .substringBefore(':')
        if (host.isBlank()) {
            return ServerUrlValidation.Invalid(HOST_ERROR)
        }
        return ServerUrlValidation.Valid(candidate)
    }

    companion object {
        const val STORAGE_KEY = "serverUrl"

        const val FALLBACK_DEFAULT_URL = "http://127.0.0.1:3000/freshapp"

        private const val EMPTY_ERROR = "Строка подключения не может быть пустой"
        private const val SCHEME_ERROR = "Адрес должен начинаться с http:// или https://"
        private const val HOST_ERROR = "В адресе не указан хост"

        fun normalize(raw: String): String = raw.trim().trimEnd('/')

        fun resolveDefaultUrl(raw: String?): String =
            raw
                ?.trim()
                ?.takeIf { it.isNotEmpty() && !it.equals("null", ignoreCase = true) }
                ?.let(::normalize)
                ?: FALLBACK_DEFAULT_URL
    }

}

sealed interface ServerUrlValidation {

    data class Valid(val url: String) : ServerUrlValidation

    data class Invalid(val error: String) : ServerUrlValidation

}
