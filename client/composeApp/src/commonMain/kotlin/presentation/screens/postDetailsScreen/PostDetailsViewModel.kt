package presentation.screens.postDetailsScreen

import data.repository.FavoriteRepository
import data.utils.toSha1
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.base.postSharedEvent
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.IdleResource
import presentation.model.LoadingResource
import presentation.model.Resource
import presentation.model.shared.OnOpenExternalBrowserSharedEvent
import presentation.model.shared.OnShareLinkSharedEvent
import presentation.navigation.NavigateBackEffect
import presentation.navigation.SharedMemory

class PostDetailsViewModel(
    val favoriteRepository: FavoriteRepository
) : BaseViewModel<PostDetailsViewModel.State>() {

    data class State(
        val favoriteReadyState: Resource<Unit> = IdleResource,
        val isFavorite: Boolean = false,
    )

    fun onScreenReady(postUrl: String) {
        val postId = postUrl.toSha1()
        viewModelScope.launch {
            try {
                reduce { copy(favoriteReadyState = LoadingResource) }
                val favoriteIds = favoriteRepository.filterFavoriteIds(listOf(postId))
                reduce { copy(
                    favoriteReadyState = CompleteResource(Unit),
                    isFavorite = favoriteIds.contains(postId)
                ) }
            } catch (e: Throwable) {
                reduce { copy(favoriteReadyState = ExceptionResource(e)) }
            }
        }

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

}