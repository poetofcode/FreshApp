package data.repository

import data.service.FreshApi
import data.service.MainApi
import data.utils.ProfileStorage

interface RepositoryFactory {

    fun createProfileRepository(): ProfileRepository

    fun createFeedRepository() : FeedRepository

}

class RepositoryFactoryImpl(
    val api: MainApi,
    val freshApi: FreshApi,
    val profileStorage: ProfileStorage,
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

}