package com.loancaculator.data.finance

import com.google.gson.JsonParser
import com.google.gson.Gson
import com.loancaculator.data.db.ExchangeRateCacheEntity
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class ExchangeRateSnapshot(
    val base: String,
    val rates: Map<String, Double>,
    val lastUpdatedAt: Long,
    val nextUpdateAt: Long,
    val fromCache: Boolean,
)

@Singleton
class ExchangeRateRepository @Inject constructor(private val repository: CalculationRepository) {
    private val gson = Gson()

    suspend fun load(base: String, forceRefresh: Boolean = false): ExchangeRateSnapshot = withContext(Dispatchers.IO) {
        val normalizedBase = base.uppercase()
        val cached = repository.exchangeCache(normalizedBase)
        val now = System.currentTimeMillis()
        if (!forceRefresh && cached != null && cached.nextUpdateAt > now) return@withContext cached.toSnapshot(gson, true)

        try {
            val connection = (URL("https://open.er-api.com/v6/latest/$normalizedBase").openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 10_000
                doInput = true
            }
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()
            val json = JsonParser.parseString(body).asJsonObject
            if (json["result"]?.asString != "success") error("Exchange rate API returned an error")
            val rates = json["rates"].asJsonObject.entrySet().associate { it.key to it.value.asDouble }
            val lastUpdated = json["time_last_update_unix"]?.asLong?.times(1000) ?: now
            val nextUpdate = json["time_next_update_unix"]?.asLong?.times(1000) ?: now + 86_400_000L
            val cache = ExchangeRateCacheEntity(normalizedBase, gson.toJson(rates), lastUpdated, nextUpdate)
            repository.saveExchangeCache(cache)
            cache.toSnapshot(gson, false)
        } catch (error: Exception) {
            if (cached != null) cached.toSnapshot(gson, true) else throw error
        }
    }

    private fun ExchangeRateCacheEntity.toSnapshot(gson: Gson, fromCache: Boolean): ExchangeRateSnapshot = ExchangeRateSnapshot(
        base = baseCurrency,
        rates = gson.fromJson(ratesJson, Map::class.java).entries.associate { it.key.toString() to (it.value as Number).toDouble() },
        lastUpdatedAt = lastUpdatedAt,
        nextUpdateAt = nextUpdateAt,
        fromCache = fromCache,
    )
}
