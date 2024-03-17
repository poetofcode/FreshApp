package presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import presentation.navigation.NavState

import presentation.navigation.NavStateImpl
import presentation.navigation.Navigator
import presentation.screens.homeTabScreen.HomeTabScreen


@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App(config: Config) {
    MaterialTheme {
        val navState = remember { NavStateImpl(viewModelStore = config.viewModelStore) }

        AppLayout(
            deviceType = config.deviceType,
            menu = Menu(
                tabs = Tabs.entries,
                onTabClick = { tab ->
                    navState.moveToFront(tab.key)
                },
                itemContent = { tab, isSelected ->
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
            ),
        ) {
            Navigator(
                modifier = Modifier.fillMaxWidth(),
                state = navState
            )
        }

        LaunchedEffect(Unit) {
            navState.push(HomeTabScreen())
            // navState.push(ProfileTabScreen())
            navState.moveToFront(Tabs.HOME.key)
        }
    }
}

@Composable
fun AppLayout(
    deviceType: Config.DeviceTypes,
    menu: Menu,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    if (/* deviceType.isMobile */ true) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.weight(1f)) {
                content()
            }

            Row(
                modifier = Modifier.fillMaxWidth().background(Color.LightGray),
                horizontalArrangement = Arrangement.Center
            ) {
                menu.tabs.forEach { tab ->
                    Box(Modifier.clickable { menu.onTabClick(tab) }) {
                        menu.itemContent(tab, false)
                    }
                }
            }
        }
    } else {

    }
}

data class Menu(
    val tabs: List<Tabs>,
    val onTabClick: (Tabs) -> Unit,
    val itemContent: @Composable (tab: Tabs, isSelected: Boolean) -> Unit
)


enum class Tabs(val key: String) {
    HOME("tab_home"),
    PROFILE("tab_profile"),
}