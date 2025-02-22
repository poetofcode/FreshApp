package domain.model

data class FeedQuery(
    val category: CategoryModel? = null,
    val sources: List<String> = emptyList(),
)