package data.repository

import data.utils.AppDataStorage
import domain.model.PostModel
import domain.model.toFavoritePost

interface FavoriteRepository {

    suspend fun add(post: PostModel)

    suspend fun remove(id: String)

    suspend fun filterFavoriteIds(ids: List<String>) : List<String>

}

class FavoriteLocalRepositoryImpl(
    private val storage: AppDataStorage,
) : FavoriteRepository {

    override suspend fun add(post: PostModel) {
        val favoritePosts = storage.fetchFavorites().filterNot {
            it.id == post.id
        }
        storage.saveFavorites(favoritePosts + listOf(post.toFavoritePost()))
    }

    override suspend fun remove(id: String) {
        storage.saveFavorites(storage.fetchFavorites().filterNot {
            it.id == id
        })
    }

    override suspend fun filterFavoriteIds(ids: List<String>): List<String> {
        return storage.fetchFavorites()
            .filter { ids.contains(it.id) }
            .map { it.id }
    }

}