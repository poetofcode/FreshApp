package presentation.factories

import presentation.base.ViewModelFactory
import presentation.screens.home_tab_screen.HomeTabViewModel

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