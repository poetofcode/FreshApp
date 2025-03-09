package data.repository

import data.entity.FetchFeedRequestBody
import data.service.MainApi
import domain.model.PostModel

interface FeedRepository {

    suspend fun fetchFeed(): List<PostModel>

}

class FeedRepositoryImpl(
    private val api: MainApi,
    private val favoriteRepository: FavoriteRepository,
) : FeedRepository {

    override suspend fun fetchFeed(): List<PostModel> {
        val posts = api.fetchFeed(
            FetchFeedRequestBody(sources = listOf("lenta"))
        )
            .resultOrError()
            .posts
            .orEmpty()
            .map { post ->
                PostModel(
                    title = post.title.orEmpty(),
                    image = post.image,
                    link = post.link.orEmpty(),
                    commentsCount = post.commentsCount ?: "0",
                    isFavorite = false  // TODO implement
                )
            }
        val favoriteIds = favoriteRepository.filterFavoriteIds(posts.map { it.id })
        return posts.map {
            it.copy(isFavorite = favoriteIds.contains(it.id))
        }
    }

}