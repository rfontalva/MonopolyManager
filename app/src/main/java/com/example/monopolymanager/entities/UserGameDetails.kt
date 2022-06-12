package com.example.monopolymanager.entities

data class UserGameDetails (
    var player: String?,
    var cash: Int?,
) {
    constructor(): this("", 1500)
}