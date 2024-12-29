package data.repository

import domain.model.PostModel

interface FavoriteRepository {

    suspend fun add(post: PostModel)

    suspend fun remove(id: String)

}

class FavoriteLocalRepositoryImpl : FavoriteRepository {

    override suspend fun add(post: PostModel) {

    }

    override suspend fun remove(id: String) {

    }

}