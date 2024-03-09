package data.service

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

interface NetworkingFactory {

    fun createHttpClient() : HttpClient

    fun createApi() : FreshApi

}

class NetworkingFactoryImpl : NetworkingFactory {

        private val client by lazy {
            HttpClient(CIO) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
    //                filter { request ->
    //                    request.url.host.contains("ktor.io")
    //                }
                    sanitizeHeader { header -> header == HttpHeaders.Authorization }
                }
    
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                    })
                }
            }
        }
    
    override fun createHttpClient(): HttpClient {
        return client
    }

    override fun createApi(): FreshApi {
        return FreshApi(createHttpClient())
    }


}