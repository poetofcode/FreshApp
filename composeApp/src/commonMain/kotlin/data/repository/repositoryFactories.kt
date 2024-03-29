package data.repository

import data.mock.MockFeedRepository
import data.service.FreshApi

interface RepositoryFactory {
    
    fun createFeedRepository() : FeedRepository
    
}

class MockRepositoryFactory : RepositoryFactory {
    override fun createFeedRepository(): FeedRepository = MockFeedRepository()

}

class RepositoryFactoryImpl(
    val api: FreshApi
) : RepositoryFactory {

    override fun createFeedRepository(): FeedRepository {
        return FeedRepositoryImpl(api)
    }

}