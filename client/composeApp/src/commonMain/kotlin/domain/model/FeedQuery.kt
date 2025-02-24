package domain.model

data class FeedQuery(
    val category: CategoryModel? = null,
    val sources: List<String> = emptyList(),
)

fun FeedQuery.isSourceSelected(source: String) : Boolean {
    val sources = (category?.sources ?: sources).map { s -> s.lowercase() }
    return if (sources.isNotEmpty()) {
        sources.contains(source.lowercase())
    } else {
        true
    }
}

fun FeedQuery.isCategorySelected(categoryArg: CategoryModel) : Boolean {
    return when {
        category != null -> category == categoryArg
        sources.isEmpty() -> categoryArg.sources.isEmpty()
        else -> categoryArg.sources.isNotEmpty() && sources.containsAll(categoryArg.sources)
    }
}

fun FeedQuery.finalSources() : List<String> {
    return when {
        category != null -> category.sources
        else -> sources
    }
}