package data.model

data class FeedResponse(
    var result: String? = null,
    var posts: List<Post>? = null
) {
    data class Post(
        var title: String? = null,
        var image: String? = null,
        var link: String? = null,
        var commentsCount: String? = null
    )
}
