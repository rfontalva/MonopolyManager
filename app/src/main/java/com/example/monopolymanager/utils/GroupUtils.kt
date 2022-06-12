package com.example.monopolymanager.utils

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.monopolymanager.entities.Group
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class GroupUtils {
    val getGroupsDone : MutableLiveData<Boolean?> = MutableLiveData(null)

    fun getGroups() : MutableList<Group> {
        val groups = mutableListOf<Group>()
        try {
            Firebase.firestore.collection("Group")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        groups.add(document.toObject())
                    }
                    getGroupsDone.postValue(true)
                }
                .addOnFailureListener { exception ->
                    Log.d("debug", "Error getting documents: ", exception)
                    getGroupsDone.postValue(false)
                }
        } catch (e: Exception) {
            Log.d("Test", "Error getting documents: ", e)
            getGroupsDone.postValue(false)
        }
        return groups
    }
}