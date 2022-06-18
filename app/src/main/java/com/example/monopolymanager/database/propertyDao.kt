package com.example.monopolymanager.database

import androidx.room.*
import com.example.monopolymanager.entities.Property

@Dao
public interface propertyDao {

    @Query("SELECT * FROM Property ORDER BY idProperty")
    fun selectAll(): MutableList<Property>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProperty(property: Property?)

    @Update
    fun updateProperty(property: Property?)

    @Delete
    fun delete(property: Property?)

    @Query("SELECT name from Property WHERE groupNumber = :groupNumber")
    fun loadPropertiesByGroup(groupNumber: Int): MutableList<String>

    @Query("SELECT * from Property WHERE groupNumber = :groupNumber")
    fun loadAllInGroup(groupNumber: Int): MutableList<Property>

    @Query("SELECT name FROM Property WHERE groupNumber = :groupNumber ORDER BY idProperty")
    fun loadAvailablePropertiesByGroup(groupNumber: Int): MutableList<String?>

    @Query("SELECT * FROM Property WHERE name NOT IN (:occupiedNames) ORDER BY idProperty")
    fun loadAvailablePropertiesByName(occupiedNames: List<String>): MutableList<Property>

    @Query("SELECT * from Property WHERE idProperty = :id")
    fun loadPropertyById(id: Int): Property?

    @Query("SELECT * from Property WHERE name = :name")
    fun loadPropertyByName(name: String?): Property?

    @Query("SELECT * from Property WHERE name in (:names) order by idProperty")
    fun loadAllPropertiesByNames(names: List<String?>): MutableList<Property>

    @Query("SELECT count(*) from Property")
    fun propertyCount() : Int
}