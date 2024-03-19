package presentation.navigation

import kotlinx.coroutines.flow.MutableSharedFlow

sealed interface Effect

data class NavigateEffect(val screen: BaseScreen<*>) : Effect

data object NavigateBackEffect : Effect

data class BackHandleEffect(
    val cb: () -> Boolean
) : Effect

object SharedMemory {
    
    val effectFlow: MutableSharedFlow<Effect> = MutableSharedFlow(
        extraBufferCapacity = 1
    )
    
} 