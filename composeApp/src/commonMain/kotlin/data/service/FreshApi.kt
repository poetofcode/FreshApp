package data.service

import data.entity.DataResponse
import data.entity.FeedResponse
import data.utils.buildEndpoint
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class FreshApi(
    val httpClient: HttpClient,
    val baseUrl: String
) {

    suspend fun fetchFeed(): FeedResponse {
        val response: DataResponse<FeedResponse> = httpClient.get("/site/fresh/feed".buildEndpoint(baseUrl)).body()
        return response.result
    }

}