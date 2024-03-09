package data.service

import data.entity.*
import data.utils.buildEndpoint
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class FreshApi(
    val httpClient: HttpClient,
    val baseUrl: String
) {

    suspend fun fetchFeed(): ResultResponse<FeedResponse> {
        var response: HttpResponse? = null

        suspend fun HttpResponse.tryParseFailure(): FailureResponse<FeedResponse>? = try {
            body<FailureResponse<FeedResponse>>()
        } catch (e: Throwable) {
            null
        }

        val parsed: ResultResponse<FeedResponse> = try {
            response = httpClient.get("/site/fresh/feed".buildEndpoint(baseUrl))
            response.body<DataResponse<FeedResponse>>()
        } catch (e: Throwable) {
            response?.tryParseFailure() ?: ExceptionResponse(e)
        }

        return parsed
    }

}