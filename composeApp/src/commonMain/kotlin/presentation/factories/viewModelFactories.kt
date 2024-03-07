package factories

import base.ViewModelFactory
import screens.home_tab_screen.HomeTabViewModel

class HomeTabViewModelFactory : ViewModelFactory<HomeTabViewModel> {
    override fun createViewModel(): HomeTabViewModel {
        return HomeTabViewModel()
    }

    override val vmTypeName: String
        get() = HomeTabViewModel::class.java.typeName

}

val viewModelFactories = listOf(
    HomeTabViewModelFactory()
)