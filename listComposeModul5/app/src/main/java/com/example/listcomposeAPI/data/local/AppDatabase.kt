package com.example.listxml.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.listxml.data.local.entity.MovieEntity

@Database(entities = [MovieEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}