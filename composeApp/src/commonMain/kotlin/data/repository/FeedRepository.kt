package data.repository

import data.service.FreshApi
import domain.model.PostModel
import repository.resultOrError

interface FeedRepository {

    suspend fun fetchFeed(): List<PostModel>

}

class FeedRepositoryImpl(val api: FreshApi) : FeedRepository {

    override suspend fun fetchFeed(): List<PostModel> {
        val resultResponse = api.fetchFeed()
        return resultResponse.resultOrError()?.posts?.map { post ->
                PostModel(
                    title = post.title.orEmpty(),
                    image = post.image,
                    link = post.link.orEmpty(),
                    commentsCount = post.commentsCount ?: "0"
                )
        } ?: emptyList()
    }

}