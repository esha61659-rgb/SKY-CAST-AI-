package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_cities")
data class SavedCityEntity(
    @PrimaryKey
    val cityName: String,
    val country: String,
    val admin1: String,
    val latitude: Double,
    val longitude: Double,
    val isFavorite: Boolean = false,
    val lastAccessed: Long = System.currentTimeMillis()
)
