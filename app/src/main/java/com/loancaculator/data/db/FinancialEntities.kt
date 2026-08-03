package com.loancaculator.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class CalculationHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val calculatorType: String,
    val title: String,
    val category: String,
    val createdAt: Long,
    val inputJson: String,
    val resultSummary: String,
)

@Entity(tableName = "compare_items")
data class CompareItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val historyId: Long,
    val createdAt: Long,
)

@Entity(tableName = "world_clock_entries")
data class WorldClockEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val city: String,
    val zoneId: String,
)

@Entity(tableName = "exchange_rate_cache")
data class ExchangeRateCacheEntity(
    @PrimaryKey val baseCurrency: String,
    val ratesJson: String,
    val lastUpdatedAt: Long,
    val nextUpdateAt: Long,
)
