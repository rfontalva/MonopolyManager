package com.example.monopolymanager.entities

data class PropertyBasic(
    var name: String? = null,
    var price: Int,
    var group: Int,
    var rent: String?,
    var rentHotel: Int,
    var mortgage: Int
)