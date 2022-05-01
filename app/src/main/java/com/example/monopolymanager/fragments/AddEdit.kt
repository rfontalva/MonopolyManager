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
        db = appDatabase.getAppDataBase(v.context)
        userDao = db?.userDao()
        propertyDao = db?.propertyDao()
        groupDao = db?.groupDao()
        housesAmtTxt = v.findViewById(R.id.housesAmtTxt)
        rentTxt = v.findViewById(R.id.rentTxt)
        priceTxt = v.findViewById(R.id.priceTxt)
        rmvHouseBtn = v.findViewById(R.id.rmvHouseBtn)
        addHouseBtn = v.findViewById(R.id.addHouseBtn)
        buyBtn = v.findViewById(R.id.buyBtn)
        colorSpinner = v.findViewById(R.id.colorSpinner)
        nameSpinner = v.findViewById(R.id.nameSpinner)
        db = appDatabase.getAppDataBase(v.context)
        return v
    }

    override fun onStart() {
        super.onStart()
        var housesAmt : Int
        var hasHotel : Boolean
        var groupNumber : Int
        var groupHousePrice = 0
        var totalPrice = 0
        var hasPropertySelected = false
        var colors = groupDao?.getAllAvailableColors()
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
                hasPropertySelected = true
                groupNumber = groupDao?.getGroupNumberByColor(colors[position])!!
                groupHousePrice = groupDao?.getPricePerHouseByNumber(groupNumber)!!
                var propertyNames = propertyDao?.loadAvailablePropertiesByGroup(groupNumber)
                var propertySpinnerAdapter = ArrayAdapter(v.context, android.R.layout.simple_spinner_item, propertyNames!!)
                nameSpinner.adapter = propertySpinnerAdapter
                nameSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parentView: AdapterView<*>?,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        property = propertyDao?.loadPropertyByName(propertyNames[position])
                        "${getString(R.string.rent)} ${property?.getRentPrice()}".also { rentTxt.text = it }
                        "${getString(R.string.price)} ${property?.price}".also { priceTxt.text = it }
                        hasHotel = property!!.hasHotel
                        housesAmt = property!!.houses
                        if (hasHotel) {
                            housesAmtTxt.text = getString(R.string.hotel)
                        }
                        else {
                            housesAmtTxt.text = "$housesAmt"
                        }
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

        hasWholeGroup = propertyDao?.checkWholeGroup(groupNumber)

        if (!hasPropertySelected || !hasWholeGroup) {
            addHouseBtn.isEnabled = false
            rmvHouseBtn.isEnabled = false
        }
        addHouseBtn.setOnClickListener {
            if (property!!.addHouse()) {
                housesAmtTxt.text = "${property?.houses}"
                "${getString(R.string.rent)} ${property?.getRentPrice()}".also { rentTxt.text = it }
                "${getString(R.string.price)} ${property!!.houses * groupHousePrice!! + property!!.price}"
            } else {
                housesAmtTxt.text = getString(R.string.hotel)
                "${getString(R.string.rent)} ${property?.rentHotel}".also { rentTxt.text = it }
                "${getString(R.string.price)} ${5 * groupHousePrice!! + property!!.price}"
            }
        }

        addHouseBtn.setOnClickListener {
            if (property!!.addHouse()) {
                housesAmtTxt.text = "${property?.houses}"
                "${getString(R.string.rent)} ${property?.getRentPrice()}".also { rentTxt.text = it }
                "${getString(R.string.price)} ${property!!.houses * groupHousePrice!! + property!!.price}"
            } else {
                housesAmtTxt.text = getString(R.string.hotel)
                "${getString(R.string.rent)} ${property?.rentHotel}".also { rentTxt.text = it }
                "${getString(R.string.price)} ${5 * groupHousePrice!! + property!!.price}"
            }
        }

        rmvHouseBtn.setOnClickListener {
            if (property!!.removeHouse()) {
                housesAmtTxt.text = "${property?.houses}"
            }
            housesAmtTxt.text = "0"
        }

    }

}