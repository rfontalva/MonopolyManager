package com.example.monopolymanager.database

import androidx.room.*
import com.example.monopolymanager.entities.Property

@Dao
public interface propertyDao {

    @Query("SELECT * FROM Property ORDER BY idProperty")
    fun selectAll(): MutableList<Property>

    @Query("SELECT * FROM Property WHERE idUser is null ORDER BY idProperty")
    fun selectAllAvailable(): MutableList<Property>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProperty(property: Property?)

    @Update
    fun updateProperty(property: Property?)

    @Delete
    fun delete(property: Property?)

    @Query("SELECT name from Property WHERE groupNumber = :groupNumber")
    fun loadPropertiesByGroup(groupNumber: Int): MutableList<String>

    @Query("SELECT name FROM Property WHERE idUser is null AND groupNumber = :groupNumber ORDER BY idProperty")
    fun loadAvailablePropertiesByGroup(groupNumber: Int): MutableList<String?>

    @Query("SELECT * from Property WHERE idProperty = :id")
    fun loadPropertyById(id: Int): Property?

    @Query("SELECT * from Property WHERE idUser = :idUser")
    fun loadPropertiesByUserId(idUser: Int?): MutableList<Property>

    @Query("SELECT * from Property WHERE name = :name")
    fun loadPropertyByName(name: String?): Property?

    @Query("SELECT idUser from Property WHERE idProperty = :idProperty")
    fun isAvailable(idProperty: Int?): Int

    @Query("SELECT count(*) from Property WHERE groupNumber = :groupNumber AND (idUser <> :idUser OR idUser is null)")
    fun checkWholeGroup(groupNumber: Int, idUser: Int?) : Int
}