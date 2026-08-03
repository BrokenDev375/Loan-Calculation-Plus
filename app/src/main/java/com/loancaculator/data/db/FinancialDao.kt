package com.loancaculator.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialDao {
    @Query("SELECT * FROM calculation_history ORDER BY createdAt DESC")
    fun observeHistory(): Flow<List<CalculationHistoryEntity>>

    @Query("SELECT * FROM calculation_history WHERE id = :id LIMIT 1")
    fun observeCalculation(id: Long): Flow<CalculationHistoryEntity?>

    @Insert
    suspend fun insertCalculation(item: CalculationHistoryEntity): Long

    @Query("DELETE FROM calculation_history WHERE id = :id")
    suspend fun deleteCalculation(id: Long)

    @Query("DELETE FROM calculation_history")
    suspend fun clearHistory()

    @Query("SELECT * FROM compare_items ORDER BY createdAt DESC")
    fun observeCompare(): Flow<List<CompareItemEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCompare(item: CompareItemEntity)

    @Delete
    suspend fun removeCompare(item: CompareItemEntity)

    @Query("DELETE FROM compare_items")
    suspend fun clearCompare()

    @Query("SELECT * FROM world_clock_entries ORDER BY id")
    fun observeClocks(): Flow<List<WorldClockEntryEntity>>

    @Insert
    suspend fun addClock(item: WorldClockEntryEntity): Long

    @Delete
    suspend fun removeClock(item: WorldClockEntryEntity)

    @Query("SELECT * FROM exchange_rate_cache WHERE baseCurrency = :base LIMIT 1")
    suspend fun getExchangeRates(base: String): ExchangeRateCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveExchangeRates(item: ExchangeRateCacheEntity)
}
