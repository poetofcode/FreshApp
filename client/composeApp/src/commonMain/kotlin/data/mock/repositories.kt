package data.mock

import data.repository.DashboardRepository
import domain.model.CategoryModel
import domain.model.DashboardModel


class MockDashboardRepositoryImpl : DashboardRepository {

    override suspend fun fetchDashboard(): DashboardModel {
        return DashboardModel(
            categories = createCategories(),
            sources = createSources()
        )
    }

    private fun createSources(): List<String> = listOf(
        "habr", "dtf", "3dnews"
    )

    private fun createCategories(): List<CategoryModel> = listOf(
        CategoryModel(id = "1", title = "Все", sources = createSources()),
        CategoryModel(id = "2", title = "Технологии", sources = listOf("habr", "3dnews")),
        CategoryModel(id = "3", title = "Игры", sources = listOf("dtf"))
    )


}