package presentation.screens.postListScreen

import data.repository.ChangeInfo
import data.repository.FavoriteRepository
import data.repository.FeedRepository
import domain.model.FetchFeedInput
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

class PostListViewModel(
    private val feedRepository: FeedRepository,
    private val favoriteRepository: FavoriteRepository,
) : BaseViewModel<PostListViewModel.State>() {

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
                when (change) {
                    is ChangeInfo.AddedItem -> {
                        reduce { copy(posts = posts.map {
                            if (it.id == change.id) {
                                it.copy(isFavorite = true)
                            } else it
                        }) }
                    }
                    is ChangeInfo.DeletedItem -> {
                        reduce { copy(posts = posts.map {
                            if (it.id == change.id) {
                                it.copy(isFavorite = false)
                            } else it
                        }) }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun fetchFeed() {
        val mockSources = listOf("lenta", "dtf", "habr")   // TODO replace on actual data

        viewModelScope.launch {
            try {
                state.value = state.value.copy(readyState = LoadingResource)
                state.value = state.value.copy(
                    posts = feedRepository.fetchFeed(
                        FetchFeedInput(sources = mockSources)
                    ),
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