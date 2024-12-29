package presentation.screens.postListScreen

import data.repository.FavoriteRepository
import data.repository.FeedRepository
import domain.model.PostModel
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.model.*

class PostListViewModel(
    val feedRepository: FeedRepository,
    val favoriteRepository: FavoriteRepository,
) : BaseViewModel<PostListViewModel.State>() {

    data class State(
        val posts: List<PostModel> = emptyList(),
        val readyState: Resource<Unit> = IdleResource,
    )

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

    override fun onInitState(): State = State()

    fun onFavoriteClick(post: PostModel) {
        viewModelScope.launch {
            try {
                if (post.isFavorite) {
                    favoriteRepository.remove(post.id)
                } else {
                    favoriteRepository.add(post)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

}