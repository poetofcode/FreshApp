package presentation.factories

import data.mock.MockFeedRepository
import data.repository.FeedRepository

interface RepositoryFactory {
    
    fun createFeedRepository() : FeedRepository
    
}

class MockRepositoryFactory : RepositoryFactory {
    override fun createFeedRepository(): FeedRepository = MockFeedRepository()

}