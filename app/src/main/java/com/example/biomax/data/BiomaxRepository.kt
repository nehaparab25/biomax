package com.example.biomax.data

import com.example.biomax.model.*
import kotlinx.coroutines.flow.Flow

class BiomaxRepository(private val dao: BiomaxDao) {

    val allListings: Flow<List<WasteListing>> = dao.getAllListings()
    val availableListings: Flow<List<WasteListing>> = dao.getAvailableListings()
    val allOrders: Flow<List<OrderTransaction>> = dao.getAllOrders()
    val allReviews: Flow<List<PartnerReview>> = dao.getAllReviews()
    val allAlerts: Flow<List<SystemAlertNotification>> = dao.getAllAlerts()
    val allAuditLogs: Flow<List<AuditLog>> = dao.getAllAuditLogs()

    fun getUser(userId: String): Flow<UserAccount?> = dao.getUserById(userId)
    fun getListingsForRestaurant(restaurantId: String): Flow<List<WasteListing>> = dao.getListingsForRestaurant(restaurantId)
    fun getOrdersForRestaurant(restaurantId: String): Flow<List<OrderTransaction>> = dao.getOrdersForRestaurant(restaurantId)
    fun getOrdersForBiogasPlant(plantId: String): Flow<List<OrderTransaction>> = dao.getOrdersForBiogasPlant(plantId)
    fun getOrderFlow(orderId: String): Flow<OrderTransaction?> = dao.getOrderByIdFlow(orderId)

    suspend fun insertListing(listing: WasteListing) = dao.insertListing(listing)
    suspend fun updateListing(listing: WasteListing) = dao.updateListing(listing)
    suspend fun deleteListing(id: String) = dao.deleteListing(id)
    suspend fun deleteAllListings() = dao.deleteAllListings()

    suspend fun insertOrder(order: OrderTransaction) = dao.insertOrder(order)
    suspend fun updateOrder(order: OrderTransaction) = dao.updateOrder(order)
    suspend fun getOrderById(id: String): OrderTransaction? = dao.getOrderById(id)
    suspend fun deleteAllOrders() = dao.deleteAllOrders()

    suspend fun insertReview(review: PartnerReview) = dao.insertReview(review)
    suspend fun deleteAllReviews() = dao.deleteAllReviews()

    suspend fun insertAlert(alert: SystemAlertNotification) = dao.insertAlert(alert)
    suspend fun markAlertRead(id: String) = dao.markAlertRead(id)
    suspend fun markAllAlertsRead() = dao.markAllAlertsRead()
    suspend fun deleteAllAlerts() = dao.deleteAllAlerts()

    suspend fun insertAuditLog(log: AuditLog) = dao.insertAuditLog(log)
    suspend fun deleteAllAuditLogs() = dao.deleteAllAuditLogs()

    suspend fun updateUser(user: UserAccount) = dao.updateUser(user)
    suspend fun insertUser(user: UserAccount) = dao.insertUser(user)

    suspend fun resetDatabase() {
        populateInitialData(dao)
    }
}
