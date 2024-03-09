package data.service

import data.entity.DataResponse
import data.entity.TokenResponse
import data.utils.toSha1
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

interface NetworkingFactory {

    fun createHttpClient() : HttpClient

    fun createApi() : FreshApi

}

class NetworkingFactoryImpl : NetworkingFactory {
    
    override fun createHttpClient(): HttpClient {
        return HttpClientFactory().createClient()
    }

    override fun createApi(): FreshApi {
        return FreshApi(createHttpClient())
    }


}