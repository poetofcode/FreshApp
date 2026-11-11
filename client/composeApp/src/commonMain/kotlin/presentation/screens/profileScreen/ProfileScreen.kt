package presentation.screens.profileScreen

import com.poetofcode.freshapp.BuildConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import presentation.LocalMainAppState
import presentation.Tabs
import presentation.base.postSharedEvent
import presentation.model.shared.OnOpenExternalBrowserSharedEvent
import presentation.navigation.BaseScreen
import presentation.navigation.HideBottomSheetEffect
import presentation.navigation.ShowModalBottomSheetEffect
import presentation.navigation.postSideEffect
import presentation.theme.AppColors
import presentation.theme.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
class ProfileScreen : BaseScreen<ProfileViewModel>() {
    override val screenId: String
        get() = Tabs.PROFILE.key

    override val viewModel: ProfileViewModel
        get() = viewModelStore.getViewModel<ProfileViewModel>()

    override val isMenuVisible: Boolean = true

    val state get() = viewModel.state.value

    @Composable
    override fun Content() {
        val isAuth = state.profile != null

        AppTheme {
            Column {
                TopAppBar(
                    title = { Text(text = "Настройки") },
                    navigationIcon = {},
                    actions = {
//                        if (readyState !is LoadingResource) {
//                            IconButton(onClick = {
//                                viewModel.fetchFeed()
//                            }) {
//                                Icon(
//                                    imageVector = Icons.Default.Refresh,
//                                    contentDescription = "Reload",
//                                )
//                            }
//                        }
                    }
                )

                SettingsList(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    isAuth = isAuth,
                )
            }
        }
    }

    @Composable
    fun SettingsList(modifier: Modifier = Modifier, isAuth: Boolean) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (isAuth) {
                GroupedBox(title = "Аккаунт") {
                    AccountBox()
                }
            }

            GroupedBox(
                modifier = if (isAuth) Modifier.padding(top = 16.dp) else Modifier,
                title = "Отображение"
            ) {
                CommonPrefs()
            }

            GroupedBox(
                modifier = Modifier.padding(top = 16.dp),
                title = "Настройки соединения"
            ) {
                ConnectionPrefs()
            }

            GroupedBox(
                modifier = Modifier.padding(top = 16.dp),
                title = "О приложении"
            ) {
                AboutApp()
            }
        }
    }

    @Composable
    fun GroupedBox(
        modifier: Modifier = Modifier,
        title: String,
        content: @Composable () -> Unit
    ) {
        Column(
            modifier = modifier.clip(shape = RoundedCornerShape(10.dp))
                .background(color = AppColors.contentBackgroundColor)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = AppColors.iconMutedColor,
            )
            Spacer(modifier = Modifier.size(8.dp))
            Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)) {
                content()
            }
        }
    }

    @Composable
    fun CommonPrefs(modifier: Modifier = Modifier) {
        val localMainAppState = LocalMainAppState.current

        Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Switch(
                    checked = localMainAppState.isDarkMode.value,
                    onCheckedChange = {
                        localMainAppState.isDarkMode.value = it
                    }
                )
                Text(
                    text = "Тёмная тема",
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
        }
    }

    @Composable
    fun ConnectionPrefs(modifier: Modifier = Modifier) {
        val isMobile = LocalMainAppState.current.config.deviceType.isMobile

        Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
            if (isMobile) {
                ConnectionLabel()
                ConnectionActions(
                    modifier = Modifier.padding(top = 12.dp),
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        ConnectionLabel()
                    }
                    ConnectionActions()
                }
            }

            if (!state.isServerUrlDefault) {
                Text(
                    text = "По умолчанию: ${state.defaultServerUrl}",
                    fontSize = 12.sp,
                    color = AppColors.iconMutedColor,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }

    @Composable
    fun ConnectionLabel() {
        Text(
            text = "Строка подключения",
            fontSize = 13.sp,
            color = AppColors.iconMutedColor,
        )
        Text(
            text = state.serverUrl,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(top = 4.dp),
        )
    }

    @Composable
    fun ConnectionActions(modifier: Modifier = Modifier) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(
                onClick = { openServerUrlEditor() }
            ) {
                Text(text = "Изменить")
            }
            TextButton(
                enabled = !state.isServerUrlDefault,
                onClick = { openServerUrlResetConfirm() }
            ) {
                Text(text = "Вернуть к дефолтным")
            }
        }
    }

    private fun openServerUrlEditor() {
        viewModel.onEditServerUrlClick()
        val initialValue = state.serverUrl
        postSideEffect(ShowModalBottomSheetEffect {
            EditServerUrlContent(initialValue = initialValue)
        })
    }

    private fun openServerUrlResetConfirm() {
        viewModel.onResetServerUrlClick()
        postSideEffect(ShowModalBottomSheetEffect {
            ConfirmResetServerUrlContent()
        })
    }

    @Composable
    fun EditServerUrlContent(initialValue: String) = Surface {
        var draft by remember(initialValue) { mutableStateOf(initialValue) }
        val error = state.serverUrlError

        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Строка подключения",
                fontWeight = FontWeight.Bold,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                value = draft,
                onValueChange = {
                    draft = it
                    viewModel.clearServerUrlError()
                },
                singleLine = true,
                isError = error != null,
                placeholder = { Text(text = "http://127.0.0.1:3010/freshapp") },
                supportingText = {
                    Text(
                        text = error ?: "Укажите адрес вместе с префиксом API",
                        color = if (error != null) {
                            MaterialTheme.colorScheme.error
                        } else {
                            AppColors.iconMutedColor
                        }
                    )
                },
            )
            Row(
                Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier,
                    onClick = {
                        viewModel.clearServerUrlError()
                        postSideEffect(HideBottomSheetEffect)
                    }
                ) {
                    Text(text = "Отменить")
                }
                Button(
                    modifier = Modifier,
                    onClick = {
                        viewModel.onSaveServerUrl(draft)
                    }
                ) {
                    Text(text = "Сохранить")
                }
            }
        }
    }

    @Composable
    fun ConfirmResetServerUrlContent() = Surface {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Вернуть строку подключения к значению по умолчанию?")
            Text(
                text = state.defaultServerUrl,
                fontFamily = FontFamily.Monospace,
                color = AppColors.iconMutedColor,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = "Текущая сессия будет сброшена — потребуется войти заново.",
                fontSize = 12.sp,
                color = AppColors.iconMutedColor,
                modifier = Modifier.padding(top = 8.dp),
            )
            Row(
                Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier,
                    onClick = {
                        postSideEffect(HideBottomSheetEffect)
                    }
                ) {
                    Text(text = "Отмена")
                }
                Button(
                    modifier = Modifier,
                    onClick = {
                        viewModel.onConfirmResetServerUrl()
                    }
                ) {
                    Text(text = "Вернуть")
                }
            }
        }
    }

    @Composable
    fun AccountBox(modifier: Modifier = Modifier) {
        Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Вы авторизованы",
                    color = Color.Gray,
                )
                Text(
                    text = state.profile?.email.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 6.dp),
                )
            }

            ClickableItem(
                modifier = Modifier.padding(top = 10.dp),
                text = "Выйти из аккаунта"
            ) {
                postSideEffect(ShowModalBottomSheetEffect {
                    ConfirmContent()
                })
            }
        }
    }

    @Composable
    fun ConfirmContent() = Surface {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Вы действительно хотите выйти из аккаунта?")
            Row(
                Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier,
                    onClick = {
                        postSideEffect(HideBottomSheetEffect)
                    }
                ) {
                    Text(text = "Отмена")
                }
                Button(
                    modifier = Modifier,
                    onClick = {
                        viewModel.onConfirmQuit()
                    }
                ) {
                    Text(text = "Да")
                }
            }
        }
    }

    @Composable
    fun AboutApp(modifier: Modifier = Modifier) {
        val versionName = BuildConfig.VERSION_NAME
        val versionCode = BuildConfig.VERSION_CODE
        val githubLink = "https://github.com/poetofcode/freshapp"
        Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Версия $versionName ($versionCode)",
                    modifier = Modifier,
                )
            }

            Row(
                modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ClickableItem(
                    modifier = Modifier,
                    text = "Страница на Github"
                ) {
                    viewModel.postSharedEvent(OnOpenExternalBrowserSharedEvent(githubLink))
                }
            }
        }
    }

    @Composable
    fun ClickableItem(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
        Box(modifier.clickable {
            onClick()
        }) {
            Text(
                text = text,
                color = AppColors.linkColor,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }

}
