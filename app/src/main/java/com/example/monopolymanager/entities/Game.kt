package com.example.monopolymanager.entities

data class Game (
    var name: String,
    var players: MutableList<UserGameDetails>,
    var properties: MutableList<PropertyGameDetails>,
    var password: String?) {
    constructor() : this("", mutableListOf(), mutableListOf(), "")

    fun getCashFromUsername(username: String) : Int? {
        return players.firstOrNull {it.player == username}?.cash
    }

    fun getUserDetailsFromUsername(username: String) : UserGameDetails? {
        return players.firstOrNull {it.player == username}
    }

    fun update(property: Property, user: User) {
        properties.removeIf { it.name == property.name }
        properties.add(property.getDetails())
        players.removeIf { it.player == user.getUsername() }
        players.add(user.getDetails())
    }

    fun updateBalance(userSrc: String, userDest: String, amount: Int) {
        val userSrcDetails = getUserDetailsFromUsername(userSrc)
        val userDestDetails = getUserDetailsFromUsername(userDest)
        userSrcDetails!!.cash = userSrcDetails.cash?.minus(amount)
        userDestDetails!!.cash = userDestDetails.cash?.plus(amount)
        players.removeIf { it.player == userSrc }
        players.removeIf { it.player == userDest }
        players.add(userSrcDetails)
        players.add(userDestDetails)
    }
}