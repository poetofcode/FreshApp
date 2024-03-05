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

            Row(modifier = Modifier.fillMaxWidth().background(Color.LightGray), horizontalArrangement = Arrangement.Center) {
                Tabs.entries.forEach { tab ->
                    Button(
                        modifier = Modifier.padding(20.dp),
                        onClick = {

                        }
                    ) {
                        val text = when (tab) {
                            Tabs.HOME -> "Лента"
                            Tabs.PROFILE -> "Профиль"
                        }
                        Text(text = text)
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            navState.push(HomeTabScreen())
            navState.push(ProfileTabScreen())
        }
    }
}

enum class Tabs(val key: String) {
    HOME("tab_home"),
    PROFILE("tab_profile"),
}

class HomeTabScreen : Screen {
    override val id: String
        get() = Tabs.HOME.key

    @Composable
    override fun Content() {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Лента постов")
        }
    }

}

class ProfileTabScreen : Screen {
    override val id: String
        get() = Tabs.PROFILE.key

    @Composable
    override fun Content() {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "О приложении")
        }
    }

}