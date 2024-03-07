package presentation.screens.home_tab_screen

import androidx.compose.runtime.mutableStateOf
import data.model.FeedResponse
import data.repository.FeedRepository
import presentation.base.BaseViewModel
import presentation.base.ViewModel

class HomeTabViewModel(
    feedRepository: FeedRepository
) : BaseViewModel() {
    
    val posts = mutableStateOf<List<FeedResponse.Post>>(emptyList())

    fun getScreenTitle() : String = "Home screen for example"

    init {

    }

}
