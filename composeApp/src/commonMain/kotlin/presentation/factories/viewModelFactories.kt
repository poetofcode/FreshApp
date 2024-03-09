package presentation.factories

import data.repository.FeedRepository
import data.repository.RepositoryFactory
import presentation.base.ViewModelFactory
import presentation.screens.homeTabScreen.HomeTabViewModel
import presentation.screens.postListScreen.PostListViewModel

class HomeTabViewModelFactory() : ViewModelFactory<HomeTabViewModel> {
    override fun createViewModel(): HomeTabViewModel {
        return HomeTabViewModel()
    }

    override val vmTypeName: String
        get() = HomeTabViewModel::class.java.typeName

}

class PostListViewModelFactory(val feedRepository: FeedRepository) : ViewModelFactory<PostListViewModel> {
    override fun createViewModel(): PostListViewModel {
        return PostListViewModel(feedRepository = feedRepository)
    }

    override val vmTypeName: String
        get() = PostListViewModel::class.java.typeName

}


fun viewModelFactories(
    repositoryFactory: RepositoryFactory
) = listOf(
    HomeTabViewModelFactory(),
    PostListViewModelFactory(repositoryFactory.createFeedRepository()),
)