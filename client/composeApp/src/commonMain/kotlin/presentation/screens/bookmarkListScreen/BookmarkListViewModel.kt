package presentation.screens.bookmarkListScreen


import data.repository.ChangeInfo
import data.repository.FavoriteRepository
import domain.model.PostModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.IdleResource
import presentation.model.LoadingResource
import presentation.model.Resource

class BookmarkListViewModel(
    private val favoriteRepository: FavoriteRepository,
) : BaseViewModel<BookmarkListViewModel.State>() {

    data class State(
        val posts: List<PostModel> = emptyList(),
        val readyState: Resource<Unit> = IdleResource,
    )

    init {
        observeFavoriteChanges()
        fetchFeed()
    }

    private fun observeFavoriteChanges() {
        favoriteRepository.changesFlow
            .onEach { change ->
                println("mylog Observed from BookmarkScreen: $change")
                when (change) {
                    else -> {
                        fetchFeed()
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun fetchFeed() {
        viewModelScope.launch {
            try {
                state.value = state.value.copy(readyState = LoadingResource)
                state.value = state.value.copy(
                    posts = favoriteRepository.fetch().list,
                    readyState = CompleteResource(Unit)
                )
            } catch (e: Throwable) {
                state.value = state.value.copy(readyState = ExceptionResource(e))
                e.printStackTrace()
            }
        }
    }

    override fun onInitState(): State = State()

    fun onRemoveFavoriteClick(post: PostModel) {
        viewModelScope.launch {
            try {
                favoriteRepository.remove(post.id)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

}

