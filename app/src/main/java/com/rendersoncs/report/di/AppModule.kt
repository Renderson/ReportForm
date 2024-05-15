package com.rendersoncs.report.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.data.local.AppDatabase
import com.rendersoncs.report.repository.ReportRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "report.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            ReportConstants.FIREBASE.FIRE_USERS,
            Context.MODE_PRIVATE
        )
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    @Provides
    fun provideReportRepository(appDatabase: AppDatabase): ReportRepository {
        return ReportRepository(appDatabase)
    }
}