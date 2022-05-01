package com.example.monopolymanager.fragments

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.monopolymanager.viewmodels.GeneralDetailViewModel
import com.example.monopolymanager.R
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.database.userDao
private var PREF_NAME = "MONOPOLY"

class GeneralDetail : Fragment() {

    companion object {
        fun newInstance() = GeneralDetail()
    }

    private var db: appDatabase? = null
    private var propertyDao: propertyDao? = null
    private var userDao: userDao? = null
//    private var groupDao: groupDao? = null
    lateinit var v: View
    lateinit var propertyName : TextView
    lateinit var housesAmt : TextView
    lateinit var currentRent : TextView
    lateinit var price : TextView
    lateinit var noHousesRent : TextView
    lateinit var oneHouseRent : TextView
    lateinit var twoHousesRent : TextView
    lateinit var threeHousesRent : TextView
    lateinit var fourHousesRent : TextView
    lateinit var hotelRent : TextView

    private lateinit var viewModel: GeneralDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.general_detail_fragment, container, false)
        db = appDatabase.getAppDataBase(v.context)
        propertyDao = db?.propertyDao()
        propertyName = v.findViewById(R.id.propertyName)
        housesAmt = v.findViewById(R.id.housesAmt)
        currentRent = v.findViewById(R.id.currentRent)
        price = v.findViewById(R.id.price)
        noHousesRent = v.findViewById(R.id.noHousesRent)
        oneHouseRent = v.findViewById(R.id.oneHouseRent)
        twoHousesRent = v.findViewById(R.id.twoHouseRent)
        threeHousesRent = v.findViewById(R.id.threeHouseRent)
        fourHousesRent = v.findViewById(R.id.fourHouseRent)
        hotelRent = v.findViewById(R.id.hotelRent)
        return v
    }

    override fun onStart() {
        super.onStart()

        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        var idProperty = sharedPref.getInt("idProperty", -1)
        val property = propertyDao?.loadPropertyById(idProperty)
        propertyName.text = property?.name
        price.text = "${getString(R.string.price)} ${property?.price}"
        currentRent.text = "${getString(R.string.rent)} ${property?.getRentPrice()}"
        housesAmt.text = "${getString(R.string.houses)} ${property?.houses}"
        val rentArray = property!!.getRentArray()
        noHousesRent.text = "$${ rentArray[0] }"
        oneHouseRent.text = "$${ rentArray[1] }"
        twoHousesRent.text = "$${ rentArray[2] }"
        threeHousesRent.text = "$${ rentArray[3] }"
        fourHousesRent.text = "$${ rentArray[4] }"
        hotelRent.text = "$${property?.rentHotel}"
    }

}