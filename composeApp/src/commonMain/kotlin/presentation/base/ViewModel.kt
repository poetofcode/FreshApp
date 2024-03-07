package presentation.base

import kotlinx.coroutines.CoroutineScope

interface ViewModel {

    
}

abstract class BaseViewModel : ViewModel {

    protected lateinit var viewModelScope: CoroutineScope

    fun setCoroutineScope(scope: CoroutineScope) {
        viewModelScope = scope
    }

}