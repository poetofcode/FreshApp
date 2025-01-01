package data.utils

import domain.model.FavoritePost
import domain.model.LocalAppData
import kotlinx.serialization.encodeToString

interface AppDataStorage {

    suspend fun saveFavorites(favorites: List<FavoritePost>)

    suspend fun fetchFavorites() : List<FavoritePost>

}

class AppDataStorageImpl(
    private val contentProvider: ContentProvider
) : AppDataStorage {

    private val json by lazy { JsonProvider.json }

    private var cache: LocalAppData? = null

    override suspend fun saveFavorites(favorites: List<FavoritePost>) {
        if (cache == null) {
            saveAppData(LocalAppData(
                favoritePosts = favorites
            ))
        } else {
            cache?.let {
                saveAppData(it.copy(
                    favoritePosts = favorites
                ))
            }
        }
    }

    override suspend fun fetchFavorites(): List<FavoritePost> {
        return if (cache == null) {
            fetchAppData()?.favoritePosts.orEmpty()
        } else {
            cache?.favoritePosts.orEmpty()
        }
    }

    private suspend fun fetchAppData() : LocalAppData? {
        return try {
            val fileContent = contentProvider.provideContent()
            val data = json.decodeFromString<LocalAppData>(fileContent)
            data.also {
                cache = data
            }
        } catch (e: Throwable) {
            // e.printStackTrace()
            null
        }
    }

    private suspend fun saveAppData(data: LocalAppData) {
        try {
            val fileContent = json.encodeToString<LocalAppData>(data)
            contentProvider.saveContent(fileContent)
            cache = data
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

}