package data.repository

import data.service.FreshApi
import domain.model.PostModel

interface FeedRepository {

    suspend fun fetchFeed(): List<PostModel>

}

class FeedRepositoryImpl(
    private val api: FreshApi,
    private val favoriteRepository: FavoriteRepository,
) : FeedRepository {

    override suspend fun fetchFeed(): List<PostModel> {
        val posts = api.fetchFeed()
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