package com.example.monopolymanager.propertyAdapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.monopolymanager.R
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.database.userDao
import com.example.monopolymanager.entities.Property
import com.example.monopolymanager.entities.User


class PropertyAdapter (private var properties : MutableList<Property>?,
               var onClick : (Int) -> Unit,
) : RecyclerView.Adapter<PropertyAdapter.PropertyHolder>()  {
    class PropertyHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var db: appDatabase? = null
        private var groupDao: groupDao? = null

        init {
            db = appDatabase.getAppDataBase(v.context)
            groupDao = db?.groupDao()
        }

        fun setProperty(prop: Property?) {
            val propertyNameTxt : TextView = view.findViewById(R.id.propertyNameTxt)
            val nameId = view.resources.getIdentifier("com.example.monopolymanager:string/${prop?.name}", null, null);
            propertyNameTxt.text = view.resources.getString(nameId)

            val propertyPriceTxt : TextView = view.findViewById(R.id.propertyPriceTxt)
            "$${prop?.getRentPrice()}".also { propertyPriceTxt.text = it }

            val isMortgagedTxt : TextView = view.findViewById(R.id.isMortgagedTxt)
            if (prop!!.isMortgaged) {
                isMortgagedTxt.text = view.context.resources.getString(R.string.isMortgaged)
            } else {
                isMortgagedTxt.text = ""
            }

            val imageViewIcon: ImageView = view.findViewById(R.id.propertyColorImg) as ImageView
            val color = groupDao?.getColor(prop.group)
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