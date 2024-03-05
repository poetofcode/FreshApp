import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import freshapp.composeapp.generated.resources.Res
import freshapp.composeapp.generated.resources.compose_multiplatform
import navigation.NavState
import navigation.NavStateImpl
import navigation.Navigator
import navigation.Screen

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        val navState = remember { NavStateImpl() }

        Column (horizontalAlignment = Alignment.CenterHorizontally) {
            Navigator(
                modifier = Modifier.weight(1f),
                state = navState
            )

            Button(modifier = Modifier.padding(20.dp), onClick = { navState.pop() }) {
                Text(text = "Вернуться назад")
            }
        }

        LaunchedEffect(Unit) {
            navState.push(ScreenOne())
            navState.push(ScreenTwo())
        }
    }
}

class ScreenOne : Screen {
    override val id: String
        get() = "one"

    @Composable
    override fun Content() {
        Box(Modifier.fillMaxSize().background(Color.Blue)) {
            Text(text = "Screen One", color = Color.White)
        }
    }

}

class ScreenTwo : Screen {
    override val id: String
        get() = "two"

    @Composable
    override fun Content() {
        Box(Modifier.fillMaxSize().background(Color.Green)) {
            Text(text = "Screen two", color = Color.White)
        }
    }

}