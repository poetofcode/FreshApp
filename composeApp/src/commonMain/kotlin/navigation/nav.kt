package navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

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

class NavStateImpl : NavState {
    private val _screens = mutableStateOf<List<Screen>>(emptyList())

    override val screens: State<List<Screen>>
        get() = _screens

    override fun push(screen: Screen) {
        _screens.value += screen
    }

    override fun pop() {
        if (_screens.value.size <= 1) {
            return
        }
        _screens.value = _screens.value.subList(0, _screens.value.size - 1)
    }

}


@Composable
fun Navigator(
    modifier: Modifier = Modifier,
    state: NavState,
) {
    Box(modifier) {
        val screens = state.screens.value
        screens.forEach { screen ->
            Box(Modifier.fillMaxSize().background(Color.White))
            screen.Content()
        }
    }
}