package com.example.monopolymanager.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PropertyGroup")
class Group (
    @ColumnInfo(name = "groupNumber")
    var group: Int?,

    @ColumnInfo(name = "color")
    var color: String?,

    @ColumnInfo(name = "colorName")
    var colorName: String?,

    @ColumnInfo(name = "pricePerHouse")
    var pricePerHouse: Int?
){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idGroup")
    var idGroup : Int = 0
}