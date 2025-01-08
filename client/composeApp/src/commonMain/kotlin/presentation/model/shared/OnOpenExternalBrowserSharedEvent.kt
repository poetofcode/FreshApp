package presentation.model.shared

import presentation.navigation.SharedEvent

data class OnOpenExternalBrowserSharedEvent(
    val url: String,
) : SharedEvent