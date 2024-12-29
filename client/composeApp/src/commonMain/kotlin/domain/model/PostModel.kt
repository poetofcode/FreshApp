package domain.model

data class PostModel(
    val title: String,
    val image: String?,
    val link: String,
    val commentsCount: String,
    val isFavorite: Boolean,
)
