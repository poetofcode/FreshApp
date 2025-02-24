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
import domain.model.PostModel
import domain.model.finalSources
import domain.model.isSourceSelected
import domain.model.toggleSource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.base.postSideEffect
import presentation.model.*
import presentation.navigation.HideBottomSheetEffect

class PostListViewModel(
    private val configStorage: PersistentStorage,
    private val feedRepository: FeedRepository,
    private val favoriteRepository: FavoriteRepository,
    private val dashboardRepository: DashboardRepository,
) : BaseViewModel<PostListViewModel.State>() {

    private var querySources: List<String>? by configStorage

    data class State(
        val posts: List<PostModel> = emptyList(),
        val dashboard: DashboardModel = DashboardModel(),
        val readyState: Resource<Unit> = IdleResource,
        val currentFeedQuery: FeedQuery = FeedQuery(),
    )

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

    fun fetchFeed() {
        viewModelScope.launch {
            try {
                state.value = state.value.copy(readyState = LoadingResource)
                state.value = state.value.copy(
                    posts = feedRepository.fetchFeed(state.value.currentFeedQuery),
                    dashboard = dashboardRepository.fetchDashboard(),
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