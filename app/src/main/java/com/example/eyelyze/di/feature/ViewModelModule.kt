package com.example.eyelyze.di.feature

import com.example.eyelyze.presentation.result.ResultViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ResultViewModel(get()) }
}