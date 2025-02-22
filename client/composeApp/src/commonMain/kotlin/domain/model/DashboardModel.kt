package domain.model

data class DashboardModel(
    val categories: List<CategoryModel> = emptyList(),
    val sources: List<String> = emptyList()
)

data class CategoryModel(
    val id: String,
    val title: String,
    val sources: List<String>,
)