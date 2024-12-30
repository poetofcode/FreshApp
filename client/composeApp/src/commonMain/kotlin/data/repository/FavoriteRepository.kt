package data.repository

import data.utils.PersistentStorage
import data.utils.getValue
import data.utils.setValue
import domain.model.PostModel

interface FavoriteRepository {

    suspend fun add(post: PostModel)

    suspend fun remove(id: String)

}

class FavoriteLocalRepositoryImpl(
    storage: PersistentStorage,
) : FavoriteRepository {

    private var favoritePosts: List<PostModel>? by storage

    override suspend fun add(post: PostModel) {
        // TODO по идее тут нужно подрубить FileProvider
        //

        favoritePosts = favoritePosts.orEmpty() + listOf(post)
    }

    override suspend fun remove(id: String) {
        // favoritePosts = null
    }

}