package factories

import base.ViewModelFactory
import screens.HomeTabViewModel

class HomeTabViewModelFactory : ViewModelFactory<HomeTabViewModel> {
    override fun createViewModel(): HomeTabViewModel {
        return HomeTabViewModel()
    }

}

val viewModelFactories = listOf(
    HomeTabViewModelFactory()
)