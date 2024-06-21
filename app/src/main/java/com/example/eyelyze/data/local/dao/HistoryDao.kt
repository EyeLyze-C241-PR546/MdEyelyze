package com.example.eyelyze.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eyelyze.data.model.History

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHistory(history: History)

    @Query("SELECT * FROM History ORDER BY createdAt DESC")
    fun getAllHistories(): LiveData<List<History>>

    @Query("SELECT * FROM History WHERE id = :id")
    suspend fun getDetailHistory(id: Int): History
}