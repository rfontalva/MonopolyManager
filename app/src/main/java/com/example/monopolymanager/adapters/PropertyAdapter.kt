package com.example.monopolymanager.adapters

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.monopolymanager.R
import com.example.monopolymanager.database.AppDatabase
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.entities.Group
import com.example.monopolymanager.entities.Property

private var PREF_NAME = "MONOPOLY"

class PropertyAdapter (private var lifeCycle: LifecycleOwner, private var properties : MutableList<Property>?,
                       var onClick : (Int) -> Unit,
) : RecyclerView.Adapter<PropertyAdapter.PropertyHolder>()  {
    class PropertyHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var roomDb: AppDatabase? = null
        private var groupDao: groupDao? = null
        private var groups = mutableListOf<Group>()

        init {
            roomDb = AppDatabase.getAppDataBase(v.context)
            groupDao = roomDb?.groupDao()
            groups = groupDao?.selectAll()!!
        }

        fun setProperty(prop: Property?) {
            val propertyNameTxt : TextView = view.findViewById(R.id.propertyNameTxt)
            val nameId = view.resources.getIdentifier("com.example.monopolymanager:string/${prop?.name}", null, null)
            propertyNameTxt.text = view.resources.getString(nameId)

            val propertyPriceTxt : TextView = view.findViewById(R.id.propertyPriceTxt)
            val hasWholeGroup = false
            if (hasWholeGroup && prop!!.houses == 0)
                "$${prop.getRentPrice()!! * 2}".also { propertyPriceTxt.text = it }
            else
                "$${prop?.getRentPrice()}".also { propertyPriceTxt.text = it }

            val isMortgagedTxt : TextView = view.findViewById(R.id.isMortgagedTxt)
            if (prop!!.isMortgaged) {
                isMortgagedTxt.text = view.context.resources.getString(R.string.isMortgaged)
            } else {
                isMortgagedTxt.text = ""
            }

            val imageViewIcon: ImageView = view.findViewById(R.id.propertyColorImg) as ImageView
            val color = groups.firstOrNull { it.group == prop.group }?.color
            imageViewIcon.setBackgroundColor(Color.parseColor(color))
        }

        fun getCardView() : CardView {
            return view.findViewById(R.id.cardProperty)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.property,parent,false)
        return (PropertyHolder(view))
    }

    override fun onBindViewHolder(holder: PropertyHolder, position: Int) {

        holder.setProperty(properties?.get(position))
        holder.getCardView().setOnClickListener {
            onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return properties!!.size
    }

    fun remove(position: Int) {
        properties?.removeAt(position)
    }
}