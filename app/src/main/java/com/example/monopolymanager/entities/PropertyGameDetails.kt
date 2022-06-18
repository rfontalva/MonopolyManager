package com.example.monopolymanager.entities

data class PropertyGameDetails(
    var name: String?,
    var hasHotel: Boolean,
    var houses: Int?,
    var idOwner: String?,
    var isMortgaged: Boolean,
    var group: Int,
    ) {
    constructor() : this("",false, 0, "", false, 0)
}