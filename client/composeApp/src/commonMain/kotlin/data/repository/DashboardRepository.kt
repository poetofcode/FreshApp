package data.repository

import data.service.MainApi
import domain.model.DashboardModel
import domain.model.toModel

interface DashboardRepository {
    suspend fun fetchDashboard() : DashboardModel
}

class DashboardRepositoryImpl(
    private val api: MainApi,
) : DashboardRepository {

    override suspend fun fetchDashboard(): DashboardModel {
        val dashboard = api.fetchDashboard()
            .resultOrError()
            .toModel()
        return dashboard
    }

}
