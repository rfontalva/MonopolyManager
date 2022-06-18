package com.example.monopolymanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.monopolymanager.entities.Group
import com.example.monopolymanager.entities.Property

@Database(entities = [Group::class, Property::class], version = 7, exportSchema = false)
public  abstract class AppDatabase : RoomDatabase() {

    abstract fun propertyDao(): propertyDao
    abstract fun groupDao(): groupDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "monopolyDB")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}