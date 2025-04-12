package presentation.screens.postDetailsScreen

import data.repository.ChangeInfo
import data.repository.FavoriteRepository
import domain.model.PostModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.base.postSharedEvent
import presentation.base.postSideEffect
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.IdleResource
import presentation.model.LoadingResource
import presentation.model.Resource
import presentation.model.shared.OnOpenExternalBrowserSharedEvent
import presentation.model.shared.OnShareLinkSharedEvent
import presentation.navigation.NavigateBackEffect
import presentation.navigation.OnFavoriteFlagChanged
import presentation.navigation.SharedMemory

class PostDetailsViewModel(
    val favoriteRepository: FavoriteRepository
) : BaseViewModel<PostDetailsViewModel.State>() {

    data class State(
        val favoriteReadyState: Resource<Unit> = IdleResource,
        val isFavorite: Boolean = false,
    )

    init {
        observeFavoriteChanges()
    }

    fun onScreenReady(post: PostModel) {
        viewModelScope.launch {
            try {
                reduce { copy(favoriteReadyState = LoadingResource) }
                val favoriteIds = favoriteRepository.filterFavoriteIds(listOf(post.id))
                reduce { copy(
                    favoriteReadyState = CompleteResource(Unit),
                    isFavorite = favoriteIds.contains(post.id)
                ) }
            } catch (e: Throwable) {
                reduce { copy(favoriteReadyState = ExceptionResource(e)) }
            }
        }
    }

    private fun observeFavoriteChanges() {
        favoriteRepository.changesFlow
            .onEach { change ->
                var postId: String
                var newIsFavorite: Boolean
                when (change) {
                    is ChangeInfo.AddedItem -> {
                        postId = change.id
                        newIsFavorite = true
                    }
                    is ChangeInfo.DeletedItem -> {
                        postId = change.id
                        newIsFavorite = false
                    }
                }
                postSideEffect(OnFavoriteFlagChanged(
                    id = postId,
                    isFavorite = newIsFavorite
                ))
            }
            .launchIn(viewModelScope)
    }

    fun onBackClick() = viewModelScope.launch {
        SharedMemory.effectFlow.emit(NavigateBackEffect())
    }

    override fun onInitState() = State()

    fun onShareLink(url: String) {
        postSharedEvent(OnShareLinkSharedEvent(url))
    }

    fun onOpenExternalBrowser(url: String) {
        postSharedEvent(OnOpenExternalBrowserSharedEvent(url))
    }

    fun onFavoriteClick(post: PostModel) = with(state.value) {
        if (favoriteReadyState !is CompleteResource) {
            return@with
        }
        viewModelScope.launch {
            try {
                if (isFavorite) {
                    favoriteRepository.remove(post.id)
                } else {
                    favoriteRepository.add(post)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun changeFavoriteState(newIsFavorite: Boolean) {
        reduce { copy(isFavorite = newIsFavorite) }
    }

}