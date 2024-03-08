package presentation.screens.home_tab_screen

import data.repository.FeedRepository
import data.service.FreshApi
import domain.model.PostModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import presentation.base.BaseViewModel

class HomeTabViewModel(
    val feedRepository: FeedRepository
) : BaseViewModel() {
    
    val posts = MutableStateFlow<List<PostModel>>(emptyList())

    fun getScreenTitle() : String = "Home screen for example"

    fun fetchFeed() {
        viewModelScope.launch {
            posts.value = feedRepository.fetchFeed()
        }
    }

    fun fetchFeedSync() = runBlocking {
        return@runBlocking feedRepository.fetchFeed()
    }

}
