package presentation.screens.profileScreen

import data.repository.ProfileRepository
import data.service.ServerUrlStore
import data.service.ServerUrlValidation
import domain.model.Profile
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.base.postEffect
import presentation.base.postSharedEvent
import presentation.base.postSideEffect
import presentation.model.shared.OnQuitProfileSharedEvent
import presentation.model.shared.OnReceivedTokenSharedEvent
import presentation.navigation.HideBottomSheetEffect
import presentation.navigation.NavigateEffect
import presentation.navigation.SharedEvent
import presentation.screens.authScreen.AuthScreen
import presentation.screens.regScreen.RegScreen

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val serverUrlStore: ServerUrlStore,
) : BaseViewModel<ProfileViewModel.State>() {

    data class State(
        val profile: Profile? = null,
        val serverUrl: String = String(),
        val isServerUrlDefault: Boolean = true,
        val serverUrlError: String? = null,
    )

    init {
        fetchProfile()

        // fetchNotifis()
    }

    fun fetchNotifis() {
        viewModelScope.launch {
            try {
                val notifis = profileRepository.fetchNotifications()
            } catch(t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    private fun fetchProfile() {
        val profile = profileRepository.fetchProfileLocal()
        reduce { copy(profile = profile) }
    }

    private fun fetchServerUrl() {
        reduce {
            copy(
                serverUrl = serverUrlStore.currentUrl,
                isServerUrlDefault = serverUrlStore.isDefault,
            )
        }
    }

    fun onEditServerUrlClick() {
        reduce { copy(serverUrlError = null) }
    }

    fun clearServerUrlError() {
        if (state.value.serverUrlError != null) {
            reduce { copy(serverUrlError = null) }
        }
    }

    fun onSaveServerUrl(useDefault: Boolean, raw: String) {
        if (useDefault) {
            applyDefaultServerUrl()
            return
        }

        when (val validation = serverUrlStore.save(raw)) {
            is ServerUrlValidation.Invalid -> {
                reduce { copy(serverUrlError = validation.error) }
            }

            is ServerUrlValidation.Valid -> {
                val urlChanged = validation.url != state.value.serverUrl
                reduce {
                    copy(
                        serverUrl = serverUrlStore.currentUrl,
                        isServerUrlDefault = serverUrlStore.isDefault,
                        serverUrlError = null,
                    )
                }
                postSideEffect(HideBottomSheetEffect)

                if (urlChanged) {
                    dropSessionAfterServerChange()
                }
            }
        }
    }

    private fun applyDefaultServerUrl() {
        val urlChanged = !serverUrlStore.isDefault
        serverUrlStore.reset()
        fetchServerUrl()
        postSideEffect(HideBottomSheetEffect)

        if (urlChanged) {
            dropSessionAfterServerChange()
        }
    }

    private fun dropSessionAfterServerChange() {
        profileRepository.clearProfileLocal()
        fetchProfile()
        postSharedEvent(OnQuitProfileSharedEvent)
    }

    fun onSignInToAccountButtonClick() {
        postEffect(
            NavigateEffect(
                AuthScreen(),
            )
        )
    }

    override fun onInitState() : State = State(
        serverUrl = serverUrlStore.currentUrl,
        isServerUrlDefault = serverUrlStore.isDefault,
    )

    override fun obtainSharedEvent(event: SharedEvent) {
        when (event) {
            is OnReceivedTokenSharedEvent -> {
                fetchProfile()
            }

            OnQuitProfileSharedEvent -> {
                fetchProfile()
                postSideEffect(HideBottomSheetEffect)
            }
        }
    }

    fun onConfirmQuit() {
        viewModelScope.launch {
            try {
                profileRepository.deleteSession()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            joinAll()
            postSharedEvent(OnQuitProfileSharedEvent)
        }
    }

    fun onRegistrationButtonClick() {
        postEffect(
            NavigateEffect(
                RegScreen(),
            )
        )
    }


}
