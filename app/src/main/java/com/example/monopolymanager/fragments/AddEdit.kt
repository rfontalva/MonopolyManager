package com.example.monopolymanager.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.monopolymanager.R
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.entities.Property
import com.example.monopolymanager.viewmodels.AddEditViewModel
import com.google.android.material.snackbar.Snackbar

private var PREF_NAME = "MONOPOLY"

class AddEdit : Fragment() {

    private var db: appDatabase? = null
    private var userDao: userDao? = null
    private var propertyDao: propertyDao? = null
    private var groupDao: groupDao? = null
    lateinit var v: View
    lateinit var housesAmtTxt : TextView
    lateinit var rentTxt : TextView
    lateinit var priceTxt : TextView
    lateinit var rmvHouseBtn : Button
    lateinit var addHouseBtn : Button
    lateinit var buyBtn : Button
    lateinit var colorSpinner : Spinner
    lateinit var nameSpinner : Spinner
    var property : Property? = null

    companion object {
        fun newInstance() = AddEdit()
    }

    private lateinit var viewModel: AddEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.add_edit_fragment, container, false)
        housesAmtTxt = v.findViewById(R.id.housesAmtTxt)
        rentTxt = v.findViewById(R.id.rentTxt)
        priceTxt = v.findViewById(R.id.priceTxt)
        rmvHouseBtn = v.findViewById(R.id.rmvHouseBtn)
        addHouseBtn = v.findViewById(R.id.addHouseBtn)
        buyBtn = v.findViewById(R.id.buyBtn)
        colorSpinner = v.findViewById(R.id.colorSpinner)
        nameSpinner = v.findViewById(R.id.nameSpinner)
        db = appDatabase.getAppDataBase(v.context)
        userDao = db?.userDao()
        propertyDao = db?.propertyDao()
        groupDao = db?.groupDao()
        return v
    }

    override fun onStart() {
        super.onStart()
        var colors = groupDao?.getAllColors()
        val colorSpinnerAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_spinner_item, colors!!)
        colorSpinner.adapter = colorSpinnerAdapter
        colorSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                var properties = propertyDao?.loadPropertiesByGroup(groupDao?.getGroupNumberByColor(colors[position])!!)
                var propertySpinnerAdapter = ArrayAdapter(v.context, android.R.layout.simple_spinner_item, properties!!)
                nameSpinner.adapter = propertySpinnerAdapter
                nameSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parentView: AdapterView<*>?,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        property = propertyDao?.loadPropertyByName(properties[position])
                        "${getString(R.string.rent)} ${property?.getRentPrice()}".also { rentTxt.text = it }
                        "${getString(R.string.rent)} ${property?.price}".also { priceTxt.text = it }
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>?) {
                        //
                    }
                })
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                nameSpinner.isEnabled = false
            }
        })
        //////////
        buyBtn.setOnClickListener {
            if (property != null) {
                val sharedPref: SharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                var idUser = sharedPref.getInt("idUser", -1)
                var user = userDao?.loadPersonById(idUser)
                val (cash, canPay) = user!!.pay(property!!.price)
                if (canPay) {
                    property!!.idOwner = idUser
                    propertyDao?.updateProperty(property)
                    userDao?.updatePerson(user)
                    v.findNavController().navigate(AddEditDirections.actionAddEditToHome2())
                }
                else {
                    Snackbar.make(v,"${getString(R.string.notEnoughCash)} (${cash}", Snackbar.LENGTH_SHORT).show()
                }
            }
            else {
                Snackbar.make(v,getString(R.string.selectFirst), Snackbar.LENGTH_SHORT).show()
            }
        }

    }

}