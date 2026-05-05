package com.example.myfoodappfinal.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myfoodappfinal.model.Food

@Database(entities = [Food::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
}