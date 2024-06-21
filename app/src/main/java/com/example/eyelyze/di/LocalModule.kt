package com.example.eyelyze.di

import android.app.Application
import androidx.room.Room
import com.example.eyelyze.BuildConfig
import com.example.eyelyze.data.local.room.EyeLyzeDatabse
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val localModule = module {
    factory { get<EyeLyzeDatabse>().getHistoryDao() }

    fun provideDatabase(application: Application): EyeLyzeDatabse {
        return Room.databaseBuilder(application, EyeLyzeDatabse::class.java, BuildConfig.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    single { provideDatabase(androidApplication()) }
}