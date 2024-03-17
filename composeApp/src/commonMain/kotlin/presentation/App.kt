package presentation

import androidx.compose.foundation.Image
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
import freshapp.composeapp.generated.resources.Res
import freshapp.composeapp.generated.resources.compose_multiplatform
import freshapp.composeapp.generated.resources.ic_home_tab
import freshapp.composeapp.generated.resources.ic_profile_tab
import presentation.base.Config
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.Tabs.*
import presentation.navigation.NavState

import presentation.navigation.NavStateImpl
import presentation.navigation.Navigator
import presentation.screens.ProfileTabScreen
import presentation.screens.homeTabScreen.HomeTabScreen


@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App(config: Config) {
    MaterialTheme {
        val selectedTab = remember { mutableStateOf<Tabs>(Tabs.HOME) }
        val navState = remember { NavStateImpl(viewModelStore = config.viewModelStore).apply {
            push(HomeTabScreen())
            push(ProfileTabScreen())
        } }

        AppLayout(
            deviceType = config.deviceType,
            menu = Menu(
                tabs = Tabs.entries,
                onTabClick = { tab ->
                    navState.moveToFront(tab.key)
                },
                itemContent = { tab, _ ->

                    Box(Modifier.size(30.dp, 30.dp), contentAlignment = Alignment.Center) {
                        val icon = when (tab) {
                            HOME -> Res.drawable.ic_home_tab
                            PROFILE -> Res.drawable.ic_profile_tab
                        }
                        Image(
                            painter = painterResource(icon),
                            contentDescription = null
                        )
                    }
                }
            ),
        ) {
            Navigator(
                modifier = Modifier.fillMaxWidth(),
                state = navState
            )
        }

        LaunchedEffect(selectedTab) {
            navState.moveToFront(selectedTab.value.key)
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
    if (deviceType.isMobile) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
        Row(
            modifier = modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxHeight().background(Color.LightGray),
            ) {
                menu.tabs.forEach { tab ->
                    Box(
                        modifier = Modifier.clickable { menu.onTabClick(tab) }.size(60.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        menu.itemContent(tab, false)
                    }
                }
            }

            Box(Modifier.weight(1f)) {
                content()
            }
        }
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