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

        groups.add(Group(1, "#44b502", "lime",50))
        groups.add(Group(2, "#FF0000", "red",50))
        groups.add(Group(3, "#0205b5", "blue",100))
        groups.add(Group(4, "#FF8000", "orange",100))
        groups.add(Group(5, "#18e2f5", "lightblue",150))
        groups.add(Group(6, "#FFFF00", "yellow",150))
        groups.add(Group(7, "#014d05", "green",200))
        groups.add(Group(8, "#3e0173", "purple",200))
        
        properties.add(Property("mediterranean", 60, 1, "2,10,30,90,160", 250, 30))
        properties.add(Property("baltic", 60, 1, "4,20,60,180,320", 450, 30))
        properties.add(Property("oriental", 100, 2, "6,30,90,270,400", 550, 50))
        properties.add(Property("vermont", 100, 2, "6,30,90,270,400", 550, 50))
        properties.add(Property("connecticut", 120, 2, "8,40,100,300,450", 600, 60))
        properties.add(Property("stCharles", 140, 3, "10,50,150,450,625", 750, 70))
        properties.add(Property("states",140, 3, "10,50,150,450,625", 750, 70))
        properties.add(Property("virginia",160, 3, "12,60,180,500,700", 900, 80))
        properties.add(Property("stJames", 180, 4, "10,70,200,550,750", 950, 90))
        properties.add(Property("tennessee", 180, 4, "10,70,200,550,750", 950, 90))
        properties.add(Property("newYork", 200, 4, "16,80,220,600,800", 1000, 100))
        properties.add(Property("kentucky", 220, 5, "18,90,250,700,875", 1050, 110))
        properties.add(Property("indiana", 220, 5, "18,90,250,700,875", 1050, 110))
        properties.add(Property("illinois", 240, 5, "20,100,300,750,925", 1100, 120))
        properties.add(Property("atlantic", 260, 6, "22,110,330,800,975", 1150, 130))
        properties.add(Property("ventnor",260, 6, "22,110,330,800,975", 1150, 130))
        properties.add(Property("marvin", 280, 6, "22,110,330,800,975,1150", 1200, 140))
        properties.add(Property("pacific" ,300, 7, "26,130,390,900,1100", 1275, 150))
        properties.add(Property("northCarolina", 300, 7, "26,130,390,900,1100", 1275, 150))
        properties.add(Property("pennsylvania", 280, 7, "28,150,450,1000,1200", 1400, 160))
        properties.add(Property("parkPlace", 350, 8, "35,175,500,1100,1300", 1500, 150))
        properties.add(Property("boardWalk", 400, 8, "50,200,600,1400,1700", 2000, 175))

        groups.forEach{
            groupDao?.insertGroup(it)
        }

        properties.forEach{
            propertyDao?.insertProperty(it)
        }
    }
}