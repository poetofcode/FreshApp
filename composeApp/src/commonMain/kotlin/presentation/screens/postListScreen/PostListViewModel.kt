package presentation.screens.postListScreen

import androidx.compose.runtime.mutableStateOf
import data.repository.FeedRepository
import domain.model.PostModel
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.model.*

class PostListViewModel(
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

}