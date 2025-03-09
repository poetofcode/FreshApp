package data.repository

import data.service.MainApi
import domain.model.FeedModel
import domain.model.FetchFeedInput
import domain.model.toModel
import domain.model.toRequestBody

interface FeedRepository {

    suspend fun fetchFeed(input: FetchFeedInput): FeedModel

}

class FeedRepositoryImpl(
    private val api: MainApi,
    private val favoriteRepository: FavoriteRepository,
) : FeedRepository {

    override suspend fun fetchFeed(input: FetchFeedInput): FeedModel {
        val feed = api.fetchFeed(input.toRequestBody())
            .resultOrError()
            .toModel()

        val favoriteIds = favoriteRepository.filterFavoriteIds(feed.posts.map { it.id })
        return feed.copy(posts = feed.posts.map {
            it.copy(isFavorite = favoriteIds.contains(it.id))
        })
    }

}