package com.example.eyelyze.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.eyelyze.data.local.dao.HistoryDao
import com.example.eyelyze.data.model.History
import com.example.eyelyze.data.remote.ApiResponse

class HistoryRepository(private val historyDao: HistoryDao) {

    fun getHistories(): LiveData<ApiResponse<List<History>>> = liveData {
        emit(ApiResponse.Loading)
        try {
            val localData: LiveData<ApiResponse<List<History>>> = historyDao.getAllHistories().map { ApiResponse.Success(it) }
            emitSource(localData)
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message.toString()))
        }

    }

    suspend fun addHistory(history: History) {
        historyDao.insertHistory(history)
    }


}