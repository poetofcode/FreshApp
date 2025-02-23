package data.repository

import data.service.FreshApi
import domain.model.FeedQuery
import domain.model.PostModel
import domain.model.isSourceSelected

interface FeedRepository {

    suspend fun fetchFeed(query: FeedQuery): List<PostModel>

}

class FeedRepositoryImpl(
    private val api: FreshApi,
    private val favoriteRepository: FavoriteRepository,
) : FeedRepository {

    override suspend fun fetchFeed(query: FeedQuery): List<PostModel> {
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
            .filter { query.isSourceSelected(it.source) }
        val favoriteIds = favoriteRepository.filterFavoriteIds(posts.map { it.id })
        return posts.map {
            it.copy(isFavorite = favoriteIds.contains(it.id))
        }
    }

}