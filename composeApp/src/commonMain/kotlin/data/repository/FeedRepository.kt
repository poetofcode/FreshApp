package data.repository

import domain.model.PostModel

interface FeedRepository {

    suspend fun fetchFeed() : List<PostModel>

}