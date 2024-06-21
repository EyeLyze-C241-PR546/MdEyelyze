package com.example.eyelyze.presentation.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eyelyze.data.model.History
import com.example.eyelyze.data.repository.HistoryRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date

class ResultViewModel(private val historyRepository: HistoryRepository) : ViewModel() {

    fun insertHistory(label: String, score: String, imgUri: String) {
        viewModelScope.launch {
            val history = History(
                id = 0,
                label = label,
                confidence = score,
                imageBase64 = imgUri,
                createdAt = Date()
                )
            historyRepository.addHistory(history)
            Timber.d(history.toString())
        }

    }

}