package data.repository

import data.utils.PersistentStorage
import data.utils.getValue
import data.utils.setValue
import domain.model.FavoritePost
import domain.model.PostModel
import domain.model.toFavoritePost

interface FavoriteRepository {

    suspend fun add(post: PostModel)

    suspend fun remove(id: String)

}

class FavoriteLocalRepositoryImpl(
    storage: PersistentStorage,
) : FavoriteRepository {

    private var favoritePosts: List<FavoritePost>? by storage

    override suspend fun add(post: PostModel) {
        // TODO по идее тут нужно подрубить FileProvider
        //

        println("mylog Favorites posts: $favoritePosts")

        favoritePosts = favoritePosts.orEmpty() + listOf(post.toFavoritePost())
    }

    override suspend fun remove(id: String) {
        // favoritePosts = null
    }

}