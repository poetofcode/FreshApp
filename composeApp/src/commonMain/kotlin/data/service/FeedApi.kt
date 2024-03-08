package data.service

import data.model.FeedResponse
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class FeedApi() {
    
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
        }
    }
    
    suspend fun fetchFeed(url: String) : FeedResponse {
        val response: HttpResponse = client.get(url)
        
        return FeedResponse()
    }
    
}