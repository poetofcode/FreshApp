package presentation.screens.postListScreen

import data.repository.ChangeInfo
import data.repository.FavoriteRepository
import data.repository.FeedRepository
import domain.model.FetchFeedInput
import domain.model.PostModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.base.postSideEffect
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.IdleResource
import presentation.model.LoadingResource
import presentation.model.Resource

class PostListViewModel(
    private val feedRepository: FeedRepository,
    private val favoriteRepository: FavoriteRepository,
) : BaseViewModel<PostListViewModel.State>() {

    companion object {
        val mockSources = listOf("lenta", "dtf", "habr")   // TODO replace on actual data
    }

    data class State(
        val posts: List<PostModel> = emptyList(),
        val sources: List<String> = mockSources,
        val readyState: Resource<Unit> = IdleResource,
        val currentPage: Int = 0,
        val lastTimestamp: Long = 0,
        val isNextAllowed: Boolean = false,
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

    fun resetAndFetch() {
        val currentSources = state.value.sources
        postSideEffect(ScrollToTopSideEffect)
        state.value = onInitState().copy(
            sources = currentSources
        )
        fetchFeed()
    }

    private var fetchFeedJob: Job? = null

    fun fetchFeed() {
        if (fetchFeedJob?.isActive == true) {
            return
        }

        fetchFeedJob = viewModelScope.launch {
            try {
                state.value = state.value.copy(readyState = LoadingResource)
                val feed = feedRepository.fetchFeed(
                    FetchFeedInput(
                        sources = mockSources,
                        page = state.value.currentPage,
                        lastTimestamp = state.value.lastTimestamp,
                    )
                )
                state.value = state.value.copy(
                    posts = state.value.posts + feed.posts,
                    currentPage = feed.page + 1,
                    readyState = CompleteResource(Unit),
                    lastTimestamp = feed.timestamp,
                    isNextAllowed = feed.isNextAllowed,
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