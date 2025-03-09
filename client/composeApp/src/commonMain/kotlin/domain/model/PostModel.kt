package domain.model

import data.utils.toSha1
import java.time.ZonedDateTime

data class PostModel(
    val title: String,
    val image: String?,
    val link: String,
    val commentsCount: String,
    val isFavorite: Boolean,
    val createdAt: ZonedDateTime,
) {
    val id: String get() = link.toSha1()
}
