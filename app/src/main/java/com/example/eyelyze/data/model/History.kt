package com.example.eyelyze.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class History (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var label: String,
    var confidence: String,
    var imageBase64: String,
    var createdAt: Date
)