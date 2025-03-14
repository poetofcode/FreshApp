package data.entity

import kotlinx.serialization.Serializable

@Serializable
data class DashboardResponse(
    val categories: List<Category>? = emptyList(),
    val sources: List<String>? = emptyList()
) {

    @Serializable
    data class Category(
        val id: String? = null,
        val title: String?= null,
        val sources: List<String>? = null,
    )

}