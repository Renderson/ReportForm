package com.rendersoncs.report.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.rendersoncs.report.common.constants.ReportConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Singleton

@InstallIn(ActivityComponent::class)
@Module
class SharedPreferencesModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(
            ReportConstants.FIREBASE.FIRE_USERS,
            Context.MODE_PRIVATE
        )
    }
}
