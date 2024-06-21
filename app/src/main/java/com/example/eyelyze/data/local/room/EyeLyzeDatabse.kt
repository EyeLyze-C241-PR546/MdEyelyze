package com.example.eyelyze.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.eyelyze.data.local.dao.HistoryDao
import com.example.eyelyze.data.model.History
import com.example.eyelyze.utils.Converters

@Database(entities = [History::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class EyeLyzeDatabse : RoomDatabase() {

    abstract fun getHistoryDao(): HistoryDao

}