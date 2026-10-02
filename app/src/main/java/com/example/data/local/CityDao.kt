package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Query("SELECT * FROM saved_cities ORDER BY isFavorite DESC, lastAccessed DESC")
    fun getAllSavedCities(): Flow<List<SavedCityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: SavedCityEntity)

    @Query("UPDATE saved_cities SET isFavorite = :isFavorite WHERE cityName = :cityName")
    suspend fun updateFavorite(cityName: String, isFavorite: Boolean)

    @Query("DELETE FROM saved_cities WHERE cityName = :cityName")
    suspend fun deleteCity(cityName: String)

    @Query("SELECT * FROM saved_cities WHERE cityName = :cityName LIMIT 1")
    suspend fun getCityByName(cityName: String): SavedCityEntity?
}
