package com.example.eyelyze.presentation.history

import androidx.lifecycle.ViewModel
import com.example.eyelyze.data.repository.HistoryRepository

class HistoryViewModel(private val historyRepository: HistoryRepository) : ViewModel() {

    fun getHistories() = historyRepository.getHistories()

}