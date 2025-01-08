package domain.model

import kotlinx.serialization.Serializable

const val VERSION_APP_DATA = 1

@Serializable
data class LocalAppData(
    val version: Int = VERSION_APP_DATA,
    val favoritePosts: List<FavoritePost> = emptyList(),
)