package com.loancaculator.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ScanHistory::class, CalculationHistoryEntity::class, CompareItemEntity::class, WorldClockEntryEntity::class, ExchangeRateCacheEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanHistoryDao(): ScanHistoryDao

    abstract fun financialDao(): FinancialDao
}
