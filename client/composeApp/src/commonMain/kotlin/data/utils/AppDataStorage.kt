package data.utils

import domain.model.FavoritePost

interface AppDataStorage {

    suspend fun saveFavorites(favorites: List<FavoritePost>)

    suspend fun fetchFavorites() : List<FavoritePost>

}

class AppDataStorageImpl(
    private val contentProvider: ContentProvider
) : AppDataStorage {

    override suspend fun saveFavorites(favorites: List<FavoritePost>) {

    }

    override suspend fun fetchFavorites(): List<FavoritePost> {
        return emptyList()
    }

}