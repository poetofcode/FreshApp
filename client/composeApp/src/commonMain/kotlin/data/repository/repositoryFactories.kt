package data.repository

import data.service.FreshApi
import data.service.MainApi
import data.utils.AppDataStorage
import data.utils.PersistentStorage
import data.utils.ProfileStorage

interface RepositoryFactory {

    fun createProfileRepository(): ProfileRepository

    fun createFeedRepository(favoriteRepository: FavoriteRepository) : FeedRepository

    fun createFavoriteRepository() : FavoriteRepository

}

class RepositoryFactoryImpl(
    val api: MainApi,
    val profileStorage: ProfileStorage,
    val appDataStorage: AppDataStorage,
) : RepositoryFactory {

    override fun createProfileRepository(): ProfileRepository {
        return ProfileRepositoryImpl(
            api = api,
            storage = profileStorage
        )
    }

    override fun createFeedRepository(favoriteRepository: FavoriteRepository): FeedRepository {
        return FeedRepositoryImpl(api, favoriteRepository)
    }

    override fun createFavoriteRepository(): FavoriteRepository {
        return FavoriteLocalRepositoryImpl(storage = appDataStorage)
    }

}