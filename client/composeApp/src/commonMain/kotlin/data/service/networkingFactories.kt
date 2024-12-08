package data.service

import data.utils.ProfileStorage
import io.ktor.client.HttpClient
import presentation.base.Config

interface NetworkingFactory {

    fun createHttpClient() : HttpClient

    fun createApi() : MainApi

    fun createFreshHttpClient() : HttpClient

    fun createFreshApi() : FreshApi

}

class NetworkingFactoryImpl(
    private val profileStorage: ProfileStorage,
    private val deviceType: Config.DeviceTypes,
    private val appVersion: String = "0.1",
) : NetworkingFactory {
    
    override fun createHttpClient(): HttpClient {
        return HttpClientFactory(
            baseUrl = BASE_URL,
        ).createClient()
    }

    override fun createApi(): MainApi {
        return MainApi(
            httpClient = createHttpClient(),
            profileStorage = profileStorage,
            deviceType = deviceType,
            appVersion = appVersion,
        )
    }

    override fun createFreshHttpClient(): HttpClient {
        return FreshHttpClientFactory(
            baseUrl = FRESH_URL,
            apiKey = API_KEY
        ).createClient()
    }

    override fun createFreshApi(): FreshApi {
        return FreshApi(
            httpClient = createFreshHttpClient(),
            baseUrl = FRESH_URL
        )
    }

    private companion object {
        // TODO вынести в buildConfig
        const val BASE_URL = "http://127.0.0.1:3000"
        const val FRESH_URL = "http://91.215.153.157:8080"
        const val API_KEY = "secret-api-key"
    }

}