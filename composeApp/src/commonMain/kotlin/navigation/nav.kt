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
import base.ViewModel
import base.ViewModelStore


typealias AnyScreen = BaseScreen<*>

interface Screen<T: ViewModel> {
    val id: String
    
    val viewModel: T

    @Composable
    fun Content()
}

abstract class BaseScreen<T: ViewModel> : Screen<T> {

    protected lateinit var viewModelStore: ViewModelStore

    fun setVMStore(viewModelStore: ViewModelStore) {
        this.viewModelStore = viewModelStore
    }

}

interface NavState {
    val screens : State<List<AnyScreen>>

    fun push(screen: AnyScreen)

    fun pop()
    
    fun moveToFront(screenId: String)
}

class NavStateImpl(val viewModelStore: ViewModelStore) : NavState {
    private val _screens = mutableStateOf<List<AnyScreen>>(emptyList())

    override val screens: State<List<AnyScreen>>
        get() = _screens

    override fun push(screen: AnyScreen) {
        screen.setVMStore(viewModelStore)
        _screens.value += screen
    }

    override fun pop() {
        if (_screens.value.size <= 1) {
            return
        }
        _screens.value = _screens.value.subList(0, _screens.value.size - 1)
    }

    override fun moveToFront(screenId: String) {
        var currScreens = _screens.value.toMutableList()
        currScreens.firstOrNull {
            it.id == screenId
        }?.let { found ->
            currScreens = currScreens.filterNot { it.id == screenId }.toMutableList()
            currScreens.add(found)
            _screens.value = currScreens
        }
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