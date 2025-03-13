package presentation.screens.postListScreen

import data.repository.ChangeInfo
import data.repository.DashboardRepository
import data.repository.FavoriteRepository
import data.repository.FeedRepository
import data.utils.PersistentStorage
import data.utils.getValue
import data.utils.setValue
import domain.model.CategoryModel
import domain.model.DashboardModel
import domain.model.FeedQuery
import domain.model.FetchFeedInput
import domain.model.PostModel
import domain.model.finalSources
import domain.model.toggleSource
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
import presentation.navigation.HideBottomSheetEffect

class PostListViewModel(
    private val configStorage: PersistentStorage,
    private val feedRepository: FeedRepository,
    private val favoriteRepository: FavoriteRepository,
    private val dashboardRepository: DashboardRepository,
) : BaseViewModel<PostListViewModel.State>() {

    data class State(
        val posts: List<PostModel> = emptyList(),
        val sources: List<String> = emptyList(),
        val readyState: Resource<Unit> = IdleResource,
        val currentPage: Int = 0,
        val lastTimestamp: Long = 0,
        val isNextAllowed: Boolean = false,
        val currentFeedQuery: FeedQuery = FeedQuery(),
        val dashboard: DashboardModel = DashboardModel(),
    )

    private var querySources: List<String>? by configStorage

    init {
        observeFavoriteChanges()
        restoreFeedQueryFromConfig(querySources.orEmpty())
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
        val currentFeedQuery = state.value.currentFeedQuery
        postSideEffect(ScrollToTopSideEffect)
        state.value = onInitState().copy(
            sources = currentSources,
            currentFeedQuery = currentFeedQuery,
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
                val dashboard = dashboardRepository.fetchDashboard()
                val feed = feedRepository.fetchFeed(
                    FetchFeedInput(
                        sources = state.value.currentFeedQuery.sources,
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
                    dashboard = dashboard,
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

    fun onCategoryClick(category: CategoryModel) {
        reduce {
            copy(
                currentFeedQuery = FeedQuery(
                    category = category
                )
            )
        }
        postSideEffect(HideBottomSheetEffect)
        querySources = state.value.currentFeedQuery.finalSources()
        fetchFeed()
    }

    fun onSourceClick(source: String) {
        reduce {
            copy(
                currentFeedQuery = FeedQuery(
                    sources = listOf(source)
                )
            )
        }
        postSideEffect(HideBottomSheetEffect)
        querySources = state.value.currentFeedQuery.finalSources()
        fetchFeed()
    }

    fun onSourceAddRemoveClick(source: String) {
        reduce {
            copy(
                currentFeedQuery = currentFeedQuery.toggleSource(dashboard.sources, source)
            )
        }
        querySources = state.value.currentFeedQuery.finalSources()
        fetchFeed()
    }

    private fun restoreFeedQueryFromConfig(dashboardSources: List<String>) {
        reduce { copy(
            currentFeedQuery = FeedQuery(
                sources = dashboardSources
            )
        ) }
    }

}