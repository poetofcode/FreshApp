package data.service

import data.entity.CreateSessionRequestBody
import data.entity.CreateSessionResponse
import data.entity.DashboardResponse
import data.entity.DataResponse
import data.entity.ExceptionResponse
import data.entity.FailureResponse
import data.entity.FeedResponse
import data.entity.FetchFeedRequestBody
import data.entity.NotificationResponse
import data.entity.ResultResponse
import data.entity.SaveFirebasePushTokenRequestBody
import data.entity.SubscriptionResponse
import data.utils.ProfileStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import presentation.base.Config

class MainApi(
    private val httpClient: HttpClient,
    private val profileStorage: ProfileStorage,
    private val deviceType: Config.DeviceTypes,
    private val appVersion: String,
    /**
     * Полный базовый адрес сервера вместе с префиксом, например
     * "http://127.0.0.1:3010/freshapp". Должен совпадать с basePath на бэкенде.
     */
    private val baseUrl: String,
) {

    suspend fun createSession(requestBody: CreateSessionRequestBody): ResultResponse<CreateSessionResponse> =
        parseRequestResult<CreateSessionResponse> {
            httpClient.post {
                nonAuthBlock {
                    apiPath("sessions")
                    setBody(requestBody)
                }
            }
        }

    suspend fun deleteSession(token: String) {
        httpClient.delete {
            authBlock {
                apiPath("sessions", token)
            }
        }
    }

    suspend fun createSubscription() {
        httpClient.post {
            authBlock {
                apiPath("subscriptions")
            }
        }
    }

    suspend fun deleteSubscription() {
        httpClient.delete {
            authBlock {
                apiPath("subscriptions")
            }
        }
    }

    suspend fun getSubscription() = parseRequestResult<SubscriptionResponse> {
        httpClient.get {
            authBlock {
                apiPath("subscriptions", "me")
            }
        }
    }

    suspend fun getNotifications() = parseRequestResult<NotificationResponse> {
        httpClient.get {
            authBlock {
                apiPath("notifications")
            }
        }
    }

    suspend fun saveFirebasePushToken(pushToken: String) {
        httpClient.post {
            authBlock {
                apiPath("sessions", "push_token")
                setBody(
                    SaveFirebasePushTokenRequestBody(
                        pushToken = pushToken
                    )
                )
            }
        }
    }

    suspend fun markNotificationsAsSeen() {
        httpClient.post {
            authBlock {
                apiPath("users", "me", "notifications", "seen")
            }
        }
    }

    suspend fun fetchFeed(requestBody: FetchFeedRequestBody = FetchFeedRequestBody()): ResultResponse<FeedResponse> =
        parseRequestResult<FeedResponse> {
            httpClient.post {
                nonAuthBlock {
                    apiPath("feed")
                    setBody(requestBody)
                }
            }
        }

    suspend fun fetchDashboard(): ResultResponse<DashboardResponse> =
        parseRequestResult<DashboardResponse> {
            httpClient.post {
                nonAuthBlock {
                    apiPath("dashboard")
                }
            }
        }

    private suspend inline fun <reified T : Any> parseRequestResult(doRequest: () -> HttpResponse): ResultResponse<T> {
        var response: HttpResponse? = null
        val parsed: ResultResponse<T> = try {
            response = doRequest()
            response.body<DataResponse<T>>()
        } catch (e: Throwable) {
            response?.tryParseFailure() ?: ExceptionResponse(e)
        }
        return parsed
    }

    private suspend inline fun <reified T : Any> HttpResponse.tryParseFailure(): FailureResponse<T>? =
        try {
            body<FailureResponse<T>>()
        } catch (e: Throwable) {
            null
        }

    /**
     * Собирает абсолютный путь запроса от baseUrl.
     *
     * BaseUrl применяется здесь явно через takeFrom, а не через DefaultRequest:
     * при мердже дефолтного URL Ktor подставляет его путь только если путь
     * запроса пустой, поэтому appendPathSegments в одиночку терял префикс
     * (например "/freshapp") и запрос уходил на старую схему "/api/v1/...".
     *
     * Сегменты передаются по отдельности, чтобы слэши не попали в URL
     * в закодированном виде (%2F).
     */
    private fun HttpRequestBuilder.apiPath(vararg segments: String) {
        url {
            takeFrom(baseUrl)
            appendPathSegments("api", "v1", *segments)
        }
    }

    private fun HttpRequestBuilder.nonAuthBlock(block: HttpRequestBuilder.() -> Unit) {
        contentType(ContentType.Application.Json)
        header("x-client-type", deviceType.title)
        header("x-client-version", appVersion)
        block()
    }

    private fun HttpRequestBuilder.authBlock(block: HttpRequestBuilder.() -> Unit) {
        nonAuthBlock {
            profileStorage.load()?.let {
                bearerAuth(it.token)
            }
        }
        block()
    }

    suspend fun createAccount(requestBody: CreateSessionRequestBody) {
        httpClient.post {
            nonAuthBlock {
                apiPath("users")
                setBody(requestBody)
            }
        }
    }

}