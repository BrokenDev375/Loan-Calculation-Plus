package com.loancaculator.data.finance

import com.loancaculator.data.db.CalculationHistoryEntity
import com.loancaculator.data.db.CompareItemEntity
import com.loancaculator.data.db.FinancialDao
import com.loancaculator.data.db.WorldClockEntryEntity
import com.loancaculator.data.db.ExchangeRateCacheEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalculationRepository @Inject constructor(private val dao: FinancialDao) {
    fun history(): Flow<List<CalculationHistoryEntity>> = dao.observeHistory()
    fun calculation(id: Long): Flow<CalculationHistoryEntity?> = dao.observeCalculation(id)
    fun compare(): Flow<List<CompareItemEntity>> = dao.observeCompare()
    fun clocks(): Flow<List<WorldClockEntryEntity>> = dao.observeClocks()
    suspend fun save(item: CalculationHistoryEntity): Long = dao.insertCalculation(item)
    suspend fun delete(id: Long) = dao.deleteCalculation(id)
    suspend fun exchangeCache(base: String): ExchangeRateCacheEntity? = dao.getExchangeRates(base)
    suspend fun saveExchangeCache(item: ExchangeRateCacheEntity) = dao.saveExchangeRates(item)
    suspend fun addCompare(id: Long) = dao.addCompare(CompareItemEntity(historyId = id, createdAt = System.currentTimeMillis()))
    suspend fun removeCompare(item: CompareItemEntity) = dao.removeCompare(item)
    suspend fun addClock(city: String, zoneId: String) = dao.addClock(WorldClockEntryEntity(city = city, zoneId = zoneId))
    suspend fun removeClock(item: WorldClockEntryEntity) = dao.removeClock(item)
    suspend fun clearHistory() { dao.clearHistory(); dao.clearCompare() }
}
