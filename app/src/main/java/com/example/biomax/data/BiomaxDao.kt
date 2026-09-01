package com.example.biomax.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.biomax.model.AuditLog
import com.example.biomax.model.OrderTransaction
import com.example.biomax.model.PartnerReview
import com.example.biomax.model.SystemAlertNotification
import com.example.biomax.model.UserAccount
import com.example.biomax.model.WasteListing
import kotlinx.coroutines.flow.Flow

@Dao
interface BiomaxDao {

    // Users
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserById(userId: String): Flow<UserAccount?>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccount)

    @Update
    suspend fun updateUser(user: UserAccount)

    // Listings
    @Query("SELECT * FROM listings ORDER BY createdAt DESC")
    fun getAllListings(): Flow<List<WasteListing>>

    @Query("SELECT * FROM listings WHERE restaurantId = :restaurantId ORDER BY createdAt DESC")
    fun getListingsForRestaurant(restaurantId: String): Flow<List<WasteListing>>

    @Query("SELECT * FROM listings WHERE isReserved = 0 ORDER BY createdAt DESC")
    fun getAvailableListings(): Flow<List<WasteListing>>

    @Query("SELECT * FROM listings WHERE id = :id LIMIT 1")
    suspend fun getListingById(id: String): WasteListing?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: WasteListing)

    @Update
    suspend fun updateListing(listing: WasteListing)

    @Query("DELETE FROM listings WHERE id = :id")
    suspend fun deleteListing(id: String)

    // Orders & Logistics Transactions
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderTransaction>>

    @Query("SELECT * FROM orders WHERE restaurantId = :restaurantId ORDER BY createdAt DESC")
    fun getOrdersForRestaurant(restaurantId: String): Flow<List<OrderTransaction>>

    @Query("SELECT * FROM orders WHERE biogasPlantId = :plantId ORDER BY createdAt DESC")
    fun getOrdersForBiogasPlant(plantId: String): Flow<List<OrderTransaction>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    fun getOrderByIdFlow(id: String): Flow<OrderTransaction?>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: String): OrderTransaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderTransaction)

    @Update
    suspend fun updateOrder(order: OrderTransaction)

    // Reviews
    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    fun getAllReviews(): Flow<List<PartnerReview>>

    @Query("SELECT * FROM reviews WHERE toUserId = :toUserId ORDER BY createdAt DESC")
    fun getReviewsForUser(toUserId: String): Flow<List<PartnerReview>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: PartnerReview)

    // System Alerts & Notifications
    @Query("SELECT * FROM system_alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<SystemAlertNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: SystemAlertNotification)

    @Query("UPDATE system_alerts SET isRead = 1 WHERE id = :id")
    suspend fun markAlertRead(id: String)

    @Query("UPDATE system_alerts SET isRead = 1")
    suspend fun markAllAlertsRead()

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog)
}
