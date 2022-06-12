package com.example.monopolymanager.entities

data class Game (
    var name: String,
    var players: MutableList<UserGameDetails>,
    var properties: MutableList<Property>,
    var password: String?) {
    constructor() : this("", mutableListOf(), mutableListOf(), "")
}