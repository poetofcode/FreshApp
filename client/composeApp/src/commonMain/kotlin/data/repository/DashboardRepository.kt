package data.repository

import domain.model.DashboardModel

interface DashboardRepository {

    suspend fun fetchDashboard() : DashboardModel

}