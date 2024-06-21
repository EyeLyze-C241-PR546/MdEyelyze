package com.example.eyelyze.di.feature

import com.example.eyelyze.data.repository.HistoryRepository
import org.koin.dsl.module

val historyModule = module {

    factory { HistoryRepository(get()) }
}