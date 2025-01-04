package presentation.screens.postDetailsScreen

import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.base.postSharedEvent
import presentation.model.shared.OnShareLinkSharedEvent
import presentation.navigation.NavigateBackEffect
import presentation.navigation.SharedMemory

class PostDetailsViewModel : BaseViewModel<Unit>() {

    init {
    }

    fun doNothing() {}

    fun onBackClick() = viewModelScope.launch {
        SharedMemory.effectFlow.emit(NavigateBackEffect())
    }

    override fun onInitState() = Unit

    fun onShareLink(url: String) {
        postSharedEvent(OnShareLinkSharedEvent(url))
    }


}