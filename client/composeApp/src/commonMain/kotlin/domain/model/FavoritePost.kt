package domain.model

import data.utils.DateSerializer
import data.utils.toSha1
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class FavoritePost(
    val title: String,
    val image: String?,
    val link: String,
    @Serializable(with = DateSerializer::class)
    val createdAt: ZonedDateTime,
) {
    val id : String get() = link.toSha1()
}

fun PostModel.toFavoritePost() : FavoritePost {
    return FavoritePost(
        title = title,
        image = image,
        link = link,
        createdAt = ZonedDateTime.now(),
    )
}

fun FavoritePost.toPostModel() : PostModel {
    return PostModel(
        title = title,
        image = image,
        link = link,
        commentsCount = "",
        isFavorite = true,
        createdAt = createdAt,
    )
}