package presentation.screens.postListScreen

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

    init {
        fetchFeed()
    }

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
            }
        }
    }

    fun updatePostFavorite(post: PostModel) {
        val posts = state.value.posts.map {
            if (it.link == post.link) post else it
        }

        state.value = state.value.copy(posts = posts)
    }

}