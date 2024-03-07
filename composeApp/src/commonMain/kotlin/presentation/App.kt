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
import presentation.base.Config
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview

import presentation.navigation.NavStateImpl
import presentation.navigation.Navigator
import presentation.screens.home_tab_screen.HomeTabScreen


@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App(config: Config) {
    MaterialTheme {
        val navState = remember { NavStateImpl(viewModelStore = config.viewModelStore) }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Navigator(
                modifier = Modifier.weight(1f),
                state = navState
            )

            Row(
                modifier = Modifier.fillMaxWidth().background(Color.LightGray),
                horizontalArrangement = Arrangement.Center
            ) {
                Tabs.entries.forEach { tab ->
                    Button(
                        modifier = Modifier.padding(20.dp),
                        onClick = {
                            navState.moveToFront(tab.key)
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
            // navState.push(ProfileTabScreen())
            navState.moveToFront(Tabs.HOME.key)
        }
    }
}

enum class Tabs(val key: String) {
    HOME("tab_home"),
    PROFILE("tab_profile"),
}