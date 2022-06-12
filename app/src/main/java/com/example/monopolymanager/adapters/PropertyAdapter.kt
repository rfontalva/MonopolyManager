package com.example.monopolymanager.adapters

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
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
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.entities.Group
import com.example.monopolymanager.entities.Property
import com.example.monopolymanager.entities.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

private var PREF_NAME = "MONOPOLY"

class PropertyAdapter (private var lyfeCycle: LifecycleOwner, private var properties : MutableList<Property>?,
                       var onClick : (Int) -> Unit,
) : RecyclerView.Adapter<PropertyAdapter.PropertyHolder>()  {
    class PropertyHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        private var db = Firebase.firestore
        private var groups = mutableListOf<Group>()
        var isInitialized: MutableLiveData<Boolean> = MutableLiveData(false)

        init {
            try {
                db.collection("Group")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                             groups.add(document.toObject())
                        }
                        isInitialized.value = true
                    }
                    .addOnFailureListener { exception ->
                        Log.d("debug", "Error getting documents: ", exception)
                    }
            } catch (e: Exception) {
                Log.d("Test", e.localizedMessage)
            }
        }

        fun setProperty(prop: Property?) {
            val propertyNameTxt : TextView = view.findViewById(R.id.propertyNameTxt)
            val nameId = view.resources.getIdentifier("com.example.monopolymanager:string/${prop?.name}", null, null);
            propertyNameTxt.text = view.resources.getString(nameId)

            val propertyPriceTxt : TextView = view.findViewById(R.id.propertyPriceTxt)
            val sharedPref: SharedPreferences = view.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val idUser = sharedPref.getString("idUser", "")
            val hasWholeGroup = false
            if (hasWholeGroup && prop!!.houses == 0)
                "$${prop?.getRentPrice()!! * 2}".also { propertyPriceTxt.text = it }
            else
                "$${prop?.getRentPrice()}".also { propertyPriceTxt.text = it }

            val isMortgagedTxt : TextView = view.findViewById(R.id.isMortgagedTxt)
            if (prop!!.isMortgaged) {
                isMortgagedTxt.text = view.context.resources.getString(R.string.isMortgaged)
            } else {
                isMortgagedTxt.text = ""
            }

            val imageViewIcon: ImageView = view.findViewById(R.id.propertyColorImg) as ImageView
            val color = groups.filter { it.group == prop.group }[0].color
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
        holder.isInitialized.observe(lyfeCycle) { result ->
            if (result) {
                holder.setProperty(properties?.get(position))
                holder.getCardView().setOnClickListener {
                    onClick(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return properties!!.size
    }

    fun remove(position: Int) {
        properties?.removeAt(position)
    }
}