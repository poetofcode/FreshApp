package domain.model

data class DashboardModel(
    val categories: List<CategoryModel>,
    val sources: List<String>
)

data class CategoryModel(
    val id: String,
    val title: String,
    val sources: List<String>,
)