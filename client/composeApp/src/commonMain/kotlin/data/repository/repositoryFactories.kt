package data.repository

import data.service.FreshApi
import data.service.MainApi
import data.utils.PersistentStorage
import data.utils.ProfileStorage

interface RepositoryFactory {

    fun createProfileRepository(): ProfileRepository

    fun createFeedRepository() : FeedRepository

    fun createFavoriteRepository() : FavoriteRepository

}

class RepositoryFactoryImpl(
    val api: MainApi,
    val freshApi: FreshApi,
    val profileStorage: ProfileStorage,
    val appDataStorage: PersistentStorage,
) : RepositoryFactory {

    override fun createProfileRepository(): ProfileRepository {
        return ProfileRepositoryImpl(
            api = api,
            storage = profileStorage
        )
    }

    override fun createFeedRepository(): FeedRepository {
        return FeedRepositoryImpl(freshApi)
    }

    override fun createFavoriteRepository(): FavoriteRepository {
        return FavoriteLocalRepositoryImpl(storage = appDataStorage)
    }

}