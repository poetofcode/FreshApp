package data.repository

import data.utils.AppDataStorage
import domain.model.PostModel
import domain.model.toFavoritePost

interface FavoriteRepository {

    suspend fun add(post: PostModel)

    suspend fun remove(id: String)

}

class FavoriteLocalRepositoryImpl(
    private val storage: AppDataStorage,
) : FavoriteRepository {

    // private var favoritePosts = mutableListOf<PostModel>()

    override suspend fun add(post: PostModel) {

        // TODO возможно не загружать каждлый раз, а держать в кэше.
        //      Проверить время жизни репозитория. Если он синглтон, то грузить только первый раз

        val favoritePosts = storage.fetchFavorites().apply {
            println("mylog Current favorites posts: $this")
        }

        storage.saveFavorites(favoritePosts + listOf(post.toFavoritePost()))
    }

    override suspend fun remove(id: String) {
        // favoritePosts = null
    }

}