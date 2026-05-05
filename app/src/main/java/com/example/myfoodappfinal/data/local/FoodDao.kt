package com.example.myfoodappfinal.data.local

import androidx.room.*
import com.example.myfoodappfinal.model.Food

@Dao
interface FoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(food: Food)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Food>)

    @Query("SELECT * FROM food_table")
    suspend fun getAll(): List<Food>

    @Query("SELECT * FROM food_table WHERE id = :id")
    suspend fun getById(id: Int): Food?

    @Delete
    suspend fun delete(food: Food)

    @Query("SELECT * FROM food_table")
    fun getAllFlow(): kotlinx.coroutines.flow.Flow<List<Food>>
}