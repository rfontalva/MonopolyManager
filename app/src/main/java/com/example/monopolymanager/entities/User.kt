package com.example.monopolymanager.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.monopolymanager.R

//import kotlinx.android.parcel.Parcelize

@Entity(tableName = "User")
class User(
    @ColumnInfo(name = "username")
    private var username: String?,

    @ColumnInfo(name = "password")
    private var password: String?,

    @ColumnInfo(name = "mail")
    private var mail: String?
) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idUser")
    var idUser : Int = 0

    @ColumnInfo(name = "cash")
    private var cash: Int = 1500

    @ColumnInfo(name = "avatar")
    private var avatar: Int = R.drawable.dog

    constructor(parcel: Parcel) : this (
        parcel.readString(),
        parcel.readString(),
        parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(password)
        parcel.writeString(mail)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

    fun getCash() : Int {
        return cash
    }

    fun setCash(i: Int) {
        cash = i
    }

    fun getUsername() : String? {
        return username
    }

    fun getPassword() : String? {
        return password
    }

    fun getMail() : String? {
        return mail
    }

    fun setMail(newMail: String) {
        mail = newMail
    }

    fun getAvatar(): Int {
        return avatar
    }

    fun setAvatar(id: Int) {
        avatar = id
    }

    fun pay(amount: Int) : Pair<Int, Boolean> {
        if (amount < 0 || amount > cash)
            return Pair(cash, false)
        cash -= amount
        return Pair(cash, true)
    }

    fun charge(amount: Int) : Pair<Int, Boolean> {
        if (amount < 0)
            return Pair(cash, false)
        cash += amount
        return Pair(cash, true)
    }
}