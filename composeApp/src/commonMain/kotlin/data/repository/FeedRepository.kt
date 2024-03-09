package data.repository

import data.entity.DataResponse
import data.entity.ExceptionResponse
import data.entity.FailureResponse
import data.service.FreshApi
import domain.model.PostModel
import java.lang.Exception

interface FeedRepository {

    suspend fun fetchFeed(): List<PostModel>

}

class SomeNetworkException(val reason: String) : Exception(reason)

class FeedRepositoryImpl(val api: FreshApi) : FeedRepository {

    override suspend fun fetchFeed(): List<PostModel> {
        val feedResponse = api.fetchFeed()

        return when (feedResponse) {
            is DataResponse -> {
                feedResponse.result.posts?.map { post ->
                    PostModel(
                        title = post.title.orEmpty(),
                        image = post.image,
                        link = post.link.orEmpty(),
                        commentsCount = post.commentsCount ?: "0"
                    )
                } ?: emptyList()
            }

            is ExceptionResponse -> {
                throw SomeNetworkException(feedResponse.error.message.orEmpty())
            }

            is FailureResponse -> {
                throw SomeNetworkException(feedResponse.description)
            }
        }
    }

}