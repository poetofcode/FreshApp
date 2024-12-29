package data.repository

import domain.model.PostModel

interface FavoriteRepository {

    suspend fun add(post: PostModel)

    suspend fun remove(id: String)

}

class FavoriteRepositoryImpl : FavoriteRepository {

    override suspend fun add(post: PostModel) {
        TODO("Not yet implemented")
    }

    override suspend fun remove(id: String) {
        TODO("Not yet implemented")
    }

}