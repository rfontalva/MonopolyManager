package com.example.monopolymanager.database

import androidx.room.*
import com.example.monopolymanager.entities.Group

@Dao
interface groupDao {
    @Query("SELECT * FROM PropertyGroup ORDER BY idGroup")
    fun selectAll(): MutableList<Group>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroup(Group: Group?)

    @Update
    fun updateGroup(Group: Group?)

    @Delete
    fun delete(Group: Group?)

    @Query("SELECT DISTINCT colorName FROM PropertyGroup")
    fun getAllColors(): MutableList<String>

    @Query("SELECT DISTINCT colorName FROM PropertyGroup JOIN Property using(groupNumber) where idUser is null")
    fun getAllAvailableColors(): MutableList<String>

    @Query("SELECT color FROM PropertyGroup WHERE groupNumber = :group")
    fun getColor(group: Int?): String?

    @Query("SELECT colorName FROM PropertyGroup WHERE groupNumber = :group")
    fun getColorName(group: Int?): String?

    @Query("SELECT groupNumber FROM PropertyGroup WHERE colorName = :color")
    fun getGroupNumberByColor(color: String?): Int?

    @Query("SELECT * FROM PropertyGroup WHERE groupNumber = :group")
    fun getGroupByNumber(group: Int?): Group?

    @Query("SELECT pricePerHouse FROM PropertyGroup WHERE groupNumber = :group")
    fun getPricePerHouseByNumber(group: Int?): Int?
}