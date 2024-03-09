package presentation.navigation

sealed interface Effect

data class Navigate(val screen: Screen) : Effect


object Cache {
    
    val effectFlow: MutableSharedFlow<Effect> = MutableSharedFlow()
    
} 