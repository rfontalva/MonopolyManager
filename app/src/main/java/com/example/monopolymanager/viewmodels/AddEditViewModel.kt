package com.example.monopolymanager.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.monopolymanager.R
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.entities.User
import com.example.monopolymanager.entities.Property

private var PREF_NAME = "MONOPOLY"

abstract class AddEditViewModel(context: Context) : ViewModel() {
    var db: appDatabase? = null
    var userDao: userDao? = null
    var propertyDao: propertyDao? = null
    var groupDao: groupDao? = null
    abstract var property: Property?

    abstract val addHousesMsg : Int
    abstract val removeHousesMsg : Int

    init {
        db = context.let { appDatabase.getAppDataBase(it) }
        userDao = db?.userDao()
        propertyDao = db?.propertyDao()
        groupDao = db?.groupDao()
    }


    abstract fun getRentPrice() : Int?
    abstract fun getPrice() : Int
    abstract fun addHouse() : Boolean
    abstract fun removeHouse() : Boolean
    abstract fun getColorArray() : MutableList<String?>
    abstract fun canBuy() : Pair<Int, Boolean>
    abstract fun getHousesAmt() : Int
    abstract fun hasHotel() : Boolean

    abstract fun getNameArray(groupNumber: Int): MutableList<String?>
    abstract fun setProperty(position: Int)
}


class AddViewModel(var context: Context) : AddEditViewModel(context) {
    private var user: User? = null
    override var property: Property? = null
    override val addHousesMsg = R.string.housesError
    override val removeHousesMsg = R.string.housesError
    var idUser: Int = -1
    private var propertyArray: MutableList<String?> = mutableListOf()
    var colors: MutableList<String?> = mutableListOf()

    init {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        idUser = sharedPref.getInt("idUser", -1)
        user = userDao?.loadPersonById(idUser)
    }

    override fun setProperty(position: Int) {
        property = propertyDao?.loadPropertyByName(propertyArray[position])
    }

    override fun getRentPrice() : Int? {
        val hasWholeGroup = propertyDao?.checkWholeGroup(property!!.group, idUser) == 0
        if (hasWholeGroup && property!!.houses == 0)
            return property?.getRentPrice()!! * 2
        return property?.getRentPrice()!!
    }

    override fun getPrice() : Int {
        return property!!.price
    }

    override fun addHouse() : Boolean {
        return true
    }

    override fun removeHouse() : Boolean {
        return true
    }

    override fun getColorArray(): MutableList<String?> {
        colors = groupDao?.getAllAvailableColors()!!
        val colorNames : MutableList<String?> = mutableListOf()
        colors.forEach {
            colorNames.add(context.getString(context.resources.getIdentifier("com.example.monopolymanager:string/${it}", null, null)))
        }
        return colorNames
    }

    override fun getNameArray(position: Int) : MutableList<String?> {
        val colors = groupDao?.getAllAvailableColors()
        val groupNumber = groupDao?.getGroupNumberByColor(colors!![position])!!
        propertyArray = propertyDao?.loadAvailablePropertiesByGroup(groupNumber)!!
        val propertyNamesArray : MutableList<String?> = mutableListOf()
        propertyArray.forEach {
            propertyNamesArray.add(context.getString(context.resources.getIdentifier("com.example.monopolymanager:string/${it}", null, null)))
        }
        return propertyNamesArray
    }

    override fun canBuy() : Pair<Int, Boolean> {
        val (cash, canPay) = user!!.pay(property!!.price)

        if (canPay) {
            property!!.idOwner = idUser
            propertyDao?.updateProperty(property)
            userDao?.updatePerson(user)
        }
        return Pair(cash, canPay)
    }

    override fun getHousesAmt() : Int {
        return 0
    }

    override fun hasHotel() : Boolean {
        return false
    }

}

class EditViewModel(var context: Context, override var property: Property?) : AddEditViewModel(context) {

    private var user: User? = null
    private var hasWholeGroup: Boolean = false
    private var groupHousePrice : Int = 0
    private val originalHouseAmt = property!!.houses
    override val addHousesMsg = R.string.allPropertiesError
    override val removeHousesMsg = R.string.allPropertiesError
    var idUser: Int = -1

    init {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        idUser = sharedPref.getInt("idUser", -1)
        user = userDao?.loadPersonById(idUser)
        hasWholeGroup = propertyDao?.checkWholeGroup(property!!.group, idUser) == 0
        groupHousePrice = groupDao?.getPricePerHouseByNumber(property!!.group)!!
    }

    override fun setProperty(position: Int) {
        //
    }

    override fun getRentPrice() : Int? {
        return property!!.getRentPrice()
    }

    override fun getPrice() : Int {
        var totalPrice = groupHousePrice * (property!!.houses - originalHouseAmt)
        if (totalPrice < 0)
            totalPrice /= 2
        return totalPrice
    }

    override fun addHouse() : Boolean {
        if (hasWholeGroup) {
            return property!!.addHouse()
        }
        return false
    }

    override fun removeHouse() : Boolean {
        if (hasWholeGroup) {
            return property!!.removeHouse()
        }
        return false
    }

    override fun getColorArray() : MutableList<String?> {
        val color = groupDao?.getColorName(property?.group)
        val colorNames : MutableList<String?> = mutableListOf()
        mutableListOf(color!!).forEach {
            colorNames.add(context.getString(context.resources.getIdentifier("com.example.monopolymanager:string/${it}", null, null)))
        }
        return colorNames
    }

    override fun getNameArray(groupNumber: Int) : MutableList<String?> {
        val propertyNamesArray : MutableList<String?> = mutableListOf()
        mutableListOf(property!!.name).forEach {
            propertyNamesArray.add(context.getString(context.resources.getIdentifier("com.example.monopolymanager:string/${it}", null, null)))
        }
        return propertyNamesArray
    }

    override fun canBuy() : Pair<Int, Boolean> {
        val totalPrice = getPrice()
        val (cash, canPay) = if (totalPrice < 0)
            user!!.charge(-1 * totalPrice)
        else
            user!!.pay(totalPrice)

        if (canPay) {
            property!!.idOwner = idUser
            propertyDao?.updateProperty(property)
            userDao?.updatePerson(user)
        }
        return Pair(cash, canPay)
    }

    override fun getHousesAmt() : Int {
        return property!!.houses
    }

    override fun hasHotel() : Boolean {
        return property!!.hasHotel
    }
}