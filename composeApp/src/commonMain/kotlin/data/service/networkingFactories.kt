package data.service

import data.entity.DataResponse
import data.entity.TokenResponse
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

        private val bearerTokenStorage = mutableListOf<BearerTokens>()

        private val client by lazy {
            bearerTokenStorage.add(BearerTokens("", ""))

            HttpClient(CIO) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                    // filter { request ->
                    //    request.url.host.contains("ktor.io")
                    // }
                    sanitizeHeader { header -> header == HttpHeaders.Authorization }
                }
    
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    })
                }

                install(Auth) {
                    bearer {
                        loadTokens {
                            bearerTokenStorage.last()
                        }

                        refreshTokens { // this: RefreshTokensParams
                            try {
                                println("Refresh Token Invoke Request")

                                val tokenResponse: DataResponse<TokenResponse> = client.post("http://91.215.153.157:8080/site/token").body()
                                val remoteToken : String = tokenResponse.result.token!!

                                println("RemoteTokem: $remoteToken")

                                val newToken : String = run {
                                    "1234567"
                                }
                                bearerTokenStorage.add(BearerTokens(newToken, remoteToken))
                            } catch (e: Throwable) {
                                e.printStackTrace()
                            }

                            bearerTokenStorage.last()
                        }
                    }
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