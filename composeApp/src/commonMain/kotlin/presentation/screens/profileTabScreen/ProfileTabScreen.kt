package presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import presentation.Tabs
import presentation.base.BaseViewModel
import presentation.navigation.BaseScreen
import presentation.navigation.Screen


class ProfileTabScreen : BaseScreen<ProfileTabViewModel>() {
    override val id: String
        get() = Tabs.PROFILE.key
    
    override val viewModel: ProfileTabViewModel
        get() = viewModelStore.getViewModel<ProfileTabViewModel>(id) 

    @Composable
    override fun Content() {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "О приложении")
        }
    }

}


class ProfileTabViewModel : BaseViewModel() {

}