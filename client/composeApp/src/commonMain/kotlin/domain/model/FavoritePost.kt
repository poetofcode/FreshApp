package domain.model

import data.utils.toSha1
import java.time.LocalDateTime

data class FavoritePost(
    val title: String,
    val image: String?,
    val link: String,
    val createdAt: LocalDateTime,
) {
    val id : String get() = link.toSha1()
}

fun PostModel.toFavoritePost() : FavoritePost {
    return FavoritePost(
        title = title,
        image = image,
        link = link,
        createdAt = LocalDateTime.now(),
    )
}