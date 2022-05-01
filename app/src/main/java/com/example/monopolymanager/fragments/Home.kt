package com.example.monopolymanager.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.monopolymanager.R
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.entities.Property
import com.example.monopolymanager.propertyAdapter.PropertyAdapter
import com.example.monopolymanager.viewmodels.HomeViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


lateinit var addButton : FloatingActionButton
private var PREF_NAME = "MONOPOLY"

class Home : Fragment() {

    companion object {
        fun newInstance() = Home()
    }
    lateinit var greetingTxt : TextView
    lateinit var cashTxt : TextView
    private var db: appDatabase? = null
    private var userDao: userDao? = null
    private var propertyDao: propertyDao? = null
    lateinit var v: View
    var properties : MutableList<Property>? = null
    private lateinit var recycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.home_fragment, container, false)
        recycler = v.findViewById(R.id.propertiesList)
        greetingTxt = v.findViewById(R.id.greeting)
        cashTxt = v.findViewById(R.id.cashTxt)
        addButton = v.findViewById(R.id.addButton)
        return v
    }

    override fun onStart() {
        super.onStart()

        db = appDatabase.getAppDataBase(v.context)
        userDao = db?.userDao()
        propertyDao = db?.propertyDao()
        val navController = v.findNavController()
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        var idUser = sharedPref.getInt("idUser", -1)
        var user = userDao?.loadPersonById(idUser)
        properties = propertyDao?.loadPropertiesByUserId(user?.idUser)

        if (user != null) {
            recycler.setHasFixedSize(true)
            recycler.layoutManager = LinearLayoutManager(context)
            recycler.adapter = PropertyAdapter(properties = properties){ index->
                onItemClick(index)
            }
//        var user = MainActivityArgs.fromBundle(requireArguments()).user
            greetingTxt.text = "${getString(R.string.greeting)} ${user.getUsername()}!"
            cashTxt.text = "$${user.getCash()}"
        }

        addButton.setOnClickListener {
            navController.navigate(HomeDirections.actionHome2ToAddEdit(isAdd = true))
        }
    }

    fun onItemClick(position: Int) {
        v.findNavController().navigate(HomeDirections.actionHome2ToDetail(property = properties!![position]))
    }
}