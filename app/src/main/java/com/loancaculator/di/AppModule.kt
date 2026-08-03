package com.loancaculator.di

import android.content.Context
import androidx.room.Room
import com.loancaculator.data.db.AppDatabase
import com.loancaculator.data.db.ScanHistoryDao
import com.loancaculator.data.db.FinancialDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt DI. Repository (AppInventoryRepository, VersionCheckRepository) đã @Inject constructor
 * nên không cần @Provides; chỉ cần cung cấp Room DB + DAO.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "software_update.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideScanHistoryDao(db: AppDatabase): ScanHistoryDao = db.scanHistoryDao()

    @Provides
    fun provideFinancialDao(db: AppDatabase): FinancialDao = db.financialDao()
}
