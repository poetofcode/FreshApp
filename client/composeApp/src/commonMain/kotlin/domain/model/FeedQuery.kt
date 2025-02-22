package domain.model

data class FeedQuery(
    val categoryId: CategoryModel? = null,
    val sources: List<String> = emptyList(),
)