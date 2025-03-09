package data.entity

import data.utils.DateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class FeedResponse(
    val result: String? = null,
    val posts: List<Post>? = null,
    val page: Int? = null,
    val timestamp: Long? = null,
    val isNextAllowed: Boolean? = null,
) {

    @Serializable
    data class Post(
        val title: String? = null,
        val image: String? = null,
        val link: String? = null,
        val commentsCount: String? = null,
        val source: String? = null,
        @Serializable(with = DateSerializer::class)
        val createdAt: LocalDateTime,
    )
}
