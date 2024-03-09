package data.service

import data.entity.DataResponse
import data.entity.FeedResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class FreshApi(
    val httpClient: HttpClient
) {

    suspend fun fetchFeed(): FeedResponse {
        val url = "http://91.215.153.157:8080/site/fresh/feed?debug_key=secretkey"    // TODO

        val response: DataResponse<FeedResponse> = httpClient.get(url).body()

        println("Response: $response")

        return response.result
    }

}