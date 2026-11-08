package data.service

import com.poetofcode.freshapp.BuildConfig
import data.utils.ProfileStorage
import io.ktor.client.HttpClient
import presentation.base.Config

interface NetworkingFactory {

    fun createHttpClient(): HttpClient

    fun createApi(): MainApi

}

class NetworkingFactoryImpl(
    private val profileStorage: ProfileStorage,
    private val deviceType: Config.DeviceTypes,
    private val appVersion: String = "0.1",
) : NetworkingFactory {

    override fun createHttpClient(): HttpClient {
        return HttpClientFactory(baseUrl = resolveBaseUrl()).createClient()
    }

    override fun createApi(): MainApi {
        return MainApi(
            httpClient = createHttpClient(),
            profileStorage = profileStorage,
            deviceType = deviceType,
            appVersion = appVersion,
            baseUrl = resolveBaseUrl(),
        )
    }

    /**
     * Адрес сервера вместе с префиксом (например "http://127.0.0.1:3010/freshapp").
     *
     * Хвостовой слэш убирается, чтобы при склейке не получать "//api/v1/...".
     */
    private fun resolveBaseUrl(): String {
        return BuildConfig.SERVER_URL
            .takeIf { it.isNotBlank() }
            ?.trimEnd('/')
            ?: DEFAULT_BASE_URL
    }

    private companion object {
        const val DEFAULT_BASE_URL = "http://127.0.0.1:3000/freshapp"
    }

}