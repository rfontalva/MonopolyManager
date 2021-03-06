package com.example.monopolymanager.entities

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

fun stringToArray(string: String) : IntArray {
    var foundNumbers : String = ""
    val r : MutableList<Int> = mutableListOf()
    for (s in string) {
        foundNumbers = if (s == ' ') {
            continue
        } else if (s != ',') "${foundNumbers}${s}"
        else {
            r.add(foundNumbers.toInt())
            ""
        }
    }
    r.add(foundNumbers.toInt())
    return r.toIntArray()
}


@Entity(tableName = "Property")
class Property(
    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "price")
    var price: Int,

    @ColumnInfo(name = "groupNumber")
    var group: Int,

    @ColumnInfo(name = "rent")
    var rent: String?,

    @ColumnInfo(name = "rentHotel")
    var rentHotel: Int,

    @ColumnInfo(name = "mortgage")
    var mortgage: Int
) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idProperty")
    var idProperty : Int = 0

    var houses: Int = 0
    var idOwner : String? = null
    var hasHotel: Boolean = false
    var isMortgaged: Boolean = false

    constructor() : this("",0,0,"",0,0)

    constructor(parcel: Parcel) : this (
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(price)
        parcel.writeInt(group)
        parcel.writeString(rent)
        parcel.writeInt(rentHotel)
        parcel.writeInt(mortgage)
        parcel.writeInt(houses)
        parcel.writeString(idOwner!!)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(hasHotel)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Property> {
        override fun createFromParcel(parcel: Parcel): Property {
            return Property(parcel)
        }

        override fun newArray(size: Int): Array<Property?> {
            return arrayOfNulls(size)
        }
    }

    fun setDetails(details: PropertyGameDetails) {
        isMortgaged = details.isMortgaged
        hasHotel = details.hasHotel
        houses = details.houses!!
        idOwner = details.idOwner!!
    }

    fun getDetails() : PropertyGameDetails{
        val details = PropertyGameDetails()
        details.isMortgaged = isMortgaged
        details.hasHotel = hasHotel
        details.houses = houses
        details.idOwner = idOwner
        details.name = name
        details.group = group
        return details
    }

    fun getRentPrice() : Int? {
        val rentArray = stringToArray(rent!!)
        return if (hasHotel) rentHotel else rentArray[houses]
    }

    fun addHouse() : Boolean {
        if (hasHotel || houses == 4) {
            hasHotel = true
            houses++
            return false
        }
        houses++
        return true
    }

    fun removeHouse() : Boolean {
        if (hasHotel) {
            hasHotel = false
        }
        if (houses == 0) {
            return false
        }
        houses--
        return true
    }

    fun getRentArray() : IntArray {
        return stringToArray(rent!!)
    }

    fun mortgage(user: User) : Boolean {
        val isSuccessful: Boolean = if (isMortgaged) {
            user.pay(mortgage).second
        } else {
            user.charge(mortgage).second
        }
        if (isSuccessful)
            isMortgaged = !isMortgaged
        return isSuccessful
    }
}