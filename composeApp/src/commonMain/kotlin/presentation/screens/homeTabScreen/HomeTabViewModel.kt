package presentation.screens.homeTabScreen

import androidx.compose.runtime.mutableStateOf
import data.repository.FeedRepository
import data.service.FreshApi
import domain.model.PostModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import presentation.base.BaseViewModel
import presentation.model.*

class HomeTabViewModel(
    val feedRepository: FeedRepository
) : BaseViewModel() {

    data class State(
        val posts: List<PostModel> = emptyList(),
        val readyState: Resource<Unit> = IdleResource,
    )

    val state = mutableStateOf(State())

    fun fetchFeed() {
        viewModelScope.launch {
            try {
                state.value = state.value.copy(readyState = LoadingResource)
                state.value = state.value.copy(
                    posts = feedRepository.fetchFeed(),
                    readyState = CompleteResource(Unit)
                )
            } catch (e: Throwable) {
                state.value = state.value.copy(readyState = ExceptionResource(e))
                e.printStackTrace()
                println("EXCEPTION HANDLED: $e")
            }
        }
    }

    fun fetchFeedSync() = runBlocking {
        return@runBlocking feedRepository.fetchFeed()
    }

}