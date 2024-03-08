package data.repository

import data.service.FreshApi
import domain.model.PostModel

interface FeedRepository {

    suspend fun fetchFeed(): List<PostModel>

}

class FeedRepositoryImpl(val api: FreshApi) : FeedRepository {

    override suspend fun fetchFeed(): List<PostModel> {
        val feedResponse = api.fetchFeed()

        return feedResponse.posts?.map { post ->
            PostModel(
                title = post.title.orEmpty(),
                image = post.image,
                link = post.link.orEmpty(),
                commentsCount = post.commentsCount ?: "0"
            )
        } ?: emptyList()
    }

}