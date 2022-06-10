package com.example.monopolymanager.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class Group (
    var group: Int?,
    var color: String?,
    var colorName: String?,
    var pricePerHouse: Int?
)