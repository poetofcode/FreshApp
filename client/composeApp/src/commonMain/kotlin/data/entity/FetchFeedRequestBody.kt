package data.entity

import kotlinx.serialization.Serializable

@Serializable
data class FetchFeedRequestBody(
    val sources: List<String> = emptyList(),
    val page: Int = 0,
    val timestamp: Long? = null,
)