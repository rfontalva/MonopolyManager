package com.example.monopolymanager.entities

import android.content.Context
import android.content.res.Resources
import com.example.monopolymanager.R
import com.example.monopolymanager.database.appDatabase
import com.example.monopolymanager.database.groupDao
import com.example.monopolymanager.database.propertyDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException


class PropertiesRepository(context: Context) {
    var properties : MutableList<Property> = mutableListOf()
    var groups : MutableList<Group> = mutableListOf()
    private var db: appDatabase? = null
    private var groupDao: groupDao? = null
    private var propertyDao: propertyDao? = null
    init {
        db = appDatabase.getAppDataBase(context)
        groupDao = db?.groupDao()
        propertyDao = db?.propertyDao()
        groups.add(Group(1, "#8DE541", context.resources.getString(R.string.lime),50))
        groups.add(Group(2, "#FF0000", context.resources.getString(R.string.red),50))
        groups.add(Group(3, "#003366", context.resources.getString(R.string.blue),100))
        groups.add(Group(4, "#FF8000", context.resources.getString(R.string.orange),100))
        groups.add(Group(5, "#00FFFF", context.resources.getString(R.string.lightblue),150))
        groups.add(Group(6, "#FFFF00", context.resources.getString(R.string.yellow),150))
        groups.add(Group(7, "#20540D", context.resources.getString(R.string.green),200))
        groups.add(Group(8, "#190033", context.resources.getString(R.string.purple),200))
        
        properties.add(Property(context.resources.getString(R.string.mediterranean), 60, 1, "2,10,30,90,160", 250, 30))
        properties.add(Property(context.resources.getString(R.string.baltic), 60, 1, "4,20,60,180,320", 450, 30))
        properties.add(Property(context.resources.getString(R.string.oriental), 100, 2, "6,30,90,270,400", 550, 50))
        properties.add(Property(context.resources.getString(R.string.vermont), 100, 2, "6,30,90,270,400", 550, 50))
        properties.add(Property(context.resources.getString(R.string.connecticut), 120, 2, "8,40,100,300,450", 600, 60))
        properties.add(Property(context.resources.getString(R.string.stCharles), 140, 3, "10,50,150,450,625", 750, 70))
        properties.add(Property(context.resources.getString(R.string.states), 140, 3, "10,50,150,450,625", 750, 70))
        properties.add(Property(context.resources.getString(R.string.virginia), 160, 3, "12,60,180,500,700", 900, 80))
        properties.add(Property(context.resources.getString(R.string.stJames), 180, 4, "10,70,200,550,750", 950, 90))
        properties.add(Property(context.resources.getString(R.string.tennessee), 180, 4, "10,70,200,550,750", 950, 90))
        properties.add(Property(context.resources.getString(R.string.newYork), 200, 4, "16,80,220,600,800", 1000, 100))
        properties.add(Property(context.resources.getString(R.string.kentucky), 220, 5, "18,90,250,700,875", 1050, 110))
        properties.add(Property(context.resources.getString(R.string.indiana), 220, 5, "18,90,250,700,875", 1050, 110))
        properties.add(Property(context.resources.getString(R.string.illinois), 240, 5, "20,100,300,750,925", 1100, 120))
        properties.add(Property(context.resources.getString(R.string.atlantic), 260, 6, "22,110,330,800,975", 1150, 130))
        properties.add(Property(context.resources.getString(R.string.ventnor), 260, 6, "22,110,330,800,975", 1150, 130))
        properties.add(Property(context.resources.getString(R.string.marvin), 280, 6, "22,110,330,800,975,1150", 1200, 140))
        properties.add(Property(context.resources.getString(R.string.pacific), 300, 7, "26,130,390,900,1100", 1275, 150))
        properties.add(Property(context.resources.getString(R.string.northCarolina), 300, 7, "26,130,390,900,1100", 1275, 150))
        properties.add(Property(context.resources.getString(R.string.pennsylvania), 280, 7, "28,150,450,1000,1200", 1400, 160))
        properties.add(Property(context.resources.getString(R.string.parkPlace), 350, 8, "35,175,500,1100,1300", 1500, 150))
        properties.add(Property(context.resources.getString(R.string.boardWalk), 400, 8, "50,200,600,1400,1700", 2000, 175))

        groups.forEach{
            groupDao?.insertGroup(it)
        }

        properties.forEach{
            propertyDao?.insertProperty(it)
        }
    }
}