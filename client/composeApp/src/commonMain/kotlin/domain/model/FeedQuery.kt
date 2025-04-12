package domain.model

data class FeedQuery(
    val category: CategoryModel? = null,
    val sources: List<String> = emptyList(),
)

fun FeedQuery.isSourceSelected(source: String): Boolean {
    val sources = finalSources().map { s -> s.lowercase() }
    return if (sources.isNotEmpty()) {
        sources.contains(source.lowercase())
    } else {
        true
    }
}

fun FeedQuery.isCategorySelected(categoryArg: CategoryModel): Boolean {
    return when {
        category != null -> category == categoryArg
        sources.isEmpty() -> categoryArg.sources.isEmpty()
        else -> categoryArg.sources.isNotEmpty() && sources.containsAll(categoryArg.sources)
    }
}

fun FeedQuery.finalSources(): List<String> {
    return when {
        category != null -> category.sources
        else -> sources
    }
}

fun FeedQuery.toggleSource(dashboardSources: List<String>, targetSource: String): FeedQuery {
    val tempQuery = FeedQuery(
        sources = finalSources().takeIf { it.isNotEmpty() } ?: dashboardSources
    )
    return FeedQuery(
        sources = if (tempQuery.isSourceSelected(targetSource))
            tempQuery.finalSources() - targetSource
        else
            tempQuery.finalSources() + targetSource
    )
}