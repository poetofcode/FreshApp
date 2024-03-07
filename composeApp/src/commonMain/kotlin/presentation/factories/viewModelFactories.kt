package presentation.factories

import data.repository.FeedRepository
import presentation.base.ViewModelFactory
import presentation.screens.home_tab_screen.HomeTabViewModel

class HomeTabViewModelFactory(val feedRepository: FeedRepository) : ViewModelFactory<HomeTabViewModel> {
    override fun createViewModel(): HomeTabViewModel {
        return HomeTabViewModel(feedRepository = feedRepository)
    }

    override val vmTypeName: String
        get() = HomeTabViewModel::class.java.typeName

}

fun viewModelFactories(
    repositoryFactory: RepositoryFactory
) = listOf(
    HomeTabViewModelFactory(repositoryFactory.createFeedRepository())
)