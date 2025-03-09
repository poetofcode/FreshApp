package data.repository

import data.service.MainApi
import domain.model.FetchFeedInput
import domain.model.PostModel
import domain.model.toRequestBody

interface FeedRepository {

    suspend fun fetchFeed(input: FetchFeedInput): List<PostModel>

}

class FeedRepositoryImpl(
    private val api: MainApi,
    private val favoriteRepository: FavoriteRepository,
) : FeedRepository {

    override suspend fun fetchFeed(input: FetchFeedInput): List<PostModel> {
        val posts = api.fetchFeed(input.toRequestBody())
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