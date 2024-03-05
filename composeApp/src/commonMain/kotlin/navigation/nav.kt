package navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

interface Screen {
    val id: String
    
    @Composable
    fun Content()
}

interface NavState {
    val screens : State<List<Screen>>

    fun push(screen: Screen)

    fun pop()
    
    // fun moveToFront(screenId: String)
}


@Composable
fun Navigator(
    modifier: Modifier = Modifier,
    state: NavState,
) {
    Box(modifier) {
        val screens = state.screens.value
        screens.forEach { screen ->
            screen.Content()
        }
    }
}