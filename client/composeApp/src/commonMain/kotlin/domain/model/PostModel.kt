package domain.model

import data.utils.toSha1

data class PostModel(
    val title: String,
    val image: String?,
    val link: String,
    val commentsCount: String,
    val isFavorite: Boolean,
) {
    val id : String get() = link.toSha1()

    val source: String get() = title.split(":").takeIf {
        it.isNotEmpty()
    }?.getOrNull(0) ?: "Unknown"
}

