package domain.model

import data.entity.FeedResponse

data class FeedModel(
    val posts: List<PostModel>,
    val page: Int,
    val timestamp: Long,
    val isNextAllowed: Boolean,
)

fun FeedResponse.toModel() : FeedModel {
    return FeedModel(
        posts = posts.orEmpty().map { post ->
        PostModel(
            title = post.title.orEmpty(),
            source = post.source.orEmpty(),
            image = post.image,
            link = post.link.orEmpty(),
            commentsCount = post.commentsCount ?: "0",
            isFavorite = false,  // TODO implement
            createdAt = post.createdAt,
        )
    },
        page = page ?: 0,
        timestamp = timestamp ?: 0,
        isNextAllowed = isNextAllowed ?: false,
    )
}