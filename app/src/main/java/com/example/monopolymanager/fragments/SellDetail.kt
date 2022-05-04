package com.example.monopolymanager.fragments

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.monopolymanager.R
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.entities.Property
import com.example.monopolymanager.entities.User
import com.example.monopolymanager.viewmodels.SellDetailViewModel
import com.google.android.material.snackbar.Snackbar

private var PREF_NAME = "MONOPOLY"

class SellDetail : Fragment() {

    companion object {
        fun newInstance() = SellDetail()
    }

    private var db: appDatabase? = null
    private var userDao: userDao? = null
    private var propertyDao: propertyDao? = null
    lateinit var v: View
    private lateinit var viewModel: SellDetailViewModel
    lateinit var mortgageTxt : TextView
    lateinit var mortgageBtn : Button
    var property : Property? = null
    var user : User? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.sell_detail_fragment, container, false)
        db = appDatabase.getAppDataBase(v.context)
        propertyDao = db?.propertyDao()
        userDao = db?.userDao()
        mortgageTxt = v.findViewById(R.id.mortgageTxt)
        mortgageBtn = v.findViewById(R.id.mortgageBtn)
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val idProperty = sharedPref.getInt("idProperty", -1)
        val idUser = sharedPref.getInt("idUser", -1)
        user = userDao?.loadPersonById(idUser)
        property = propertyDao?.loadPropertyById(idProperty)
        return v
    }

    override fun onStart() {
        super.onStart()
        "${getString(R.string.mortgagePrice)} ${property?.mortgage}".also { mortgageTxt.text = it }
        if (property!!.isMortgaged) {
            mortgageBtn.text = getString(R.string.unmortgage)
        }
        mortgageBtn.setOnClickListener {
            var succesful = property?.mortgage(user!!)
            if (property!!.isMortgaged) {
                mortgageBtn.text = getString(R.string.unmortgage)
            } else {
                mortgageBtn.text = getString(R.string.mortgage)
            }
            if (!succesful!!) {
                Snackbar.make(v,getString(R.string.notEnoughCash), Snackbar.LENGTH_SHORT).show()
            } else {
                propertyDao?.updateProperty(property)
                userDao?.updatePerson(user)
            }
        }
    }

}