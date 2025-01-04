package presentation.model.shared

import presentation.navigation.SharedEvent

data class OnShareLinkSharedEvent(
    val url: String,
) : SharedEvent