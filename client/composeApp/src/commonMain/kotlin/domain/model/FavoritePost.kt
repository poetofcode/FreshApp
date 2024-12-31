package domain.model

import data.utils.DateSerializer
import data.utils.toSha1
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class FavoritePost(
    val title: String,
    val image: String?,
    val link: String,
    @Serializable(with = DateSerializer::class)
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