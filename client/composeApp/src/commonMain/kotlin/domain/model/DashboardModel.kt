package domain.model

import data.entity.DashboardResponse

data class DashboardModel(
    val categories: List<CategoryModel> = emptyList(),
    val sources: List<String> = emptyList()
)

data class CategoryModel(
    val id: String,
    val title: String,
    val sources: List<String>,
)

fun DashboardResponse.toModel(): DashboardModel {
    return DashboardModel(
        categories = categories.orEmpty().filterNot {
            it.id.isNullOrBlank()
        }.map { category ->
            CategoryModel(
                id = category.id.orEmpty(),
                sources = category.sources.orEmpty(),
                title = category.title.orEmpty()
            )
        },
        sources = sources.orEmpty().filterNot {
            it.isBlank()
        }
    )
}