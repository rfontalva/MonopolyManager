package com.example.monopolymanager.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.BoolRes
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.monopolymanager.R
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.entities.Property
import com.example.monopolymanager.entities.User
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
    lateinit var addEditTitle : TextView
    lateinit var rmvHouseBtn : Button
    lateinit var addHouseBtn : Button
    lateinit var buyBtn : Button
    lateinit var colorSpinner : Spinner
    lateinit var nameSpinner : Spinner
    var property : Property? = null
    var isAdd : Boolean = true
    var idUser : Int = 0
    var user : User? = null
    var totalPrice = 0

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
        propertyDao = db?.propertyDao()
        userDao = db?.userDao()
        groupDao = db?.groupDao()
        housesAmtTxt = v.findViewById(R.id.housesAmtTxt)
        rentTxt = v.findViewById(R.id.rentTxt)
        priceTxt = v.findViewById(R.id.priceTxt)
        addEditTitle = v.findViewById(R.id.addEditTitle)
        rmvHouseBtn = v.findViewById(R.id.rmvHouseBtn)
        addHouseBtn = v.findViewById(R.id.addHouseBtn)
        buyBtn = v.findViewById(R.id.buyBtn)
        colorSpinner = v.findViewById(R.id.colorSpinner)
        nameSpinner = v.findViewById(R.id.nameSpinner)
        db = appDatabase.getAppDataBase(v.context)
        isAdd = AddEditArgs.fromBundle(requireArguments()).isAdd
        if (isAdd) {
            addEditTitle.text = getString(R.string.add)
        } else {
            addEditTitle.text = getString(R.string.edit)
            property = AddEditArgs.fromBundle(requireArguments()).property
        }
        return v
    }

    override fun onStart() {
        super.onStart()
        val sharedPref: SharedPreferences =
            requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        idUser = sharedPref.getInt("idUser", -1)
        user = userDao?.loadPersonById(idUser)

        if (isAdd) addLogic() else editLogic()

        buyBtn.setOnClickListener {
            if (property != null) {
                val (cash, canPay) = if (totalPrice < 0)
                    user!!.charge(totalPrice)
                else
                    user!!.pay(totalPrice)

                if (canPay) {
                    property!!.idOwner = idUser
                    propertyDao?.updateProperty(property)
                    userDao?.updatePerson(user)
                    v.findNavController().navigate(AddEditDirections.actionAddEditToHome2())
                }
                else {
                    Snackbar.make(v,"${getString(R.string.notEnoughCash)} (${cash})", Snackbar.LENGTH_SHORT).show()
                }
            }
            else {
                Snackbar.make(v,getString(R.string.selectFirst), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun editLogic() {
        val hasWholeGroup = propertyDao?.checkWholeGroup(property!!.group, idUser) == 0
        val groupHousePrice = groupDao?.getPricePerHouseByNumber(property!!.group)!!
        var originalHouseAmt = property!!.houses
        totalPrice = property!!.houses * groupHousePrice
        "${getString(R.string.price)} ${totalPrice}".also { priceTxt.text = it}
        val color = groupDao?.getColorName(property?.group)
        val colorArray = mutableListOf(color!!)
        val colorSpinnerAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_spinner_item, colorArray)
        colorSpinner.isEnabled = false
        colorSpinner.adapter = colorSpinnerAdapter

        val nameArray: MutableList<String?> = mutableListOf(property!!.name)
        val propertySpinnerAdapter = ArrayAdapter(v.context, android.R.layout.simple_spinner_item, nameArray)
        nameSpinner.adapter = propertySpinnerAdapter
        nameSpinner.isEnabled = false

        if (!hasWholeGroup) {
            addHouseBtn.setOnClickListener {
                Snackbar.make(v,getString(R.string.allPropertiesError), Snackbar.LENGTH_SHORT).show()
            }
            rmvHouseBtn.setOnClickListener {
                Snackbar.make(v,getString(R.string.allPropertiesError), Snackbar.LENGTH_SHORT).show()
            }
        } else {
            addHouseBtn.setOnClickListener {
                if (property!!.addHouse()) {
                    housesAmtTxt.text = "${property?.houses}"
                    "${getString(R.string.rent)} ${property?.getRentPrice()}".also {
                        rentTxt.text = it
                    }
                    "${getString(R.string.price)} ${(property!!.houses - originalHouseAmt) * groupHousePrice}".also {
                        priceTxt.text = it
                    }
                } else {
                    housesAmtTxt.text = getString(R.string.hotel)
                    "${getString(R.string.rent)} ${property?.rentHotel}".also { rentTxt.text = it }
                    "${getString(R.string.price)} ${groupHousePrice * (5 - originalHouseAmt)}".also {
                        priceTxt.text = it
                    }
                }
            }

            rmvHouseBtn.setOnClickListener {
                if (property!!.removeHouse()) {
                    housesAmtTxt.text = "${property?.houses}"
                    totalPrice = if (property!!.houses < originalHouseAmt)
                        (property!!.houses - originalHouseAmt) * groupHousePrice / 2
                    else (property!!.houses - originalHouseAmt) * groupHousePrice
                } else {
                    housesAmtTxt.text = "0"
                }
                "${getString(R.string.rent)} ${property?.getRentPrice()}".also { rentTxt.text = it }
                "${getString(R.string.price)} ${property!!.houses * groupHousePrice}".also {
                    priceTxt.text = it
                }
            }
        }
    }

    private fun addLogic() {
        var housesAmt: Int
        var hasHotel: Boolean
        var groupNumber: Int
        val colors = groupDao?.getAllAvailableColors()
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
                groupNumber = groupDao?.getGroupNumberByColor(colors[position])!!
                val propertyNames = propertyDao?.loadAvailablePropertiesByGroup(groupNumber)
                val propertySpinnerAdapter = ArrayAdapter(v.context, android.R.layout.simple_spinner_item, propertyNames!!)
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
                        totalPrice = property!!.price
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

        addHouseBtn.isEnabled = false
        rmvHouseBtn.isEnabled = false

        addHouseBtn.setOnClickListener {
            Snackbar.make(v,getString(R.string.housesError), Snackbar.LENGTH_SHORT).show()
        }

        rmvHouseBtn.setOnClickListener {
            Snackbar.make(v,getString(R.string.housesError), Snackbar.LENGTH_SHORT).show()
        }

    }

}