package com.example.monopolymanager.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monopolymanager.database.AppDatabase
import com.example.monopolymanager.database.propertyDao
import com.example.monopolymanager.entities.PropertiesRepository
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class LoginViewModel(private var context: Context) : ViewModel() {
    private var db = Firebase.firestore
    private var roomDb: AppDatabase? = null
    private var propertyDao: propertyDao? = null
    var success: MutableLiveData<Boolean?> = MutableLiveData(null)
    var email: MutableLiveData<String?> = MutableLiveData(null)

    init {
        roomDb = context.let { AppDatabase.getAppDataBase(it) }
        propertyDao = roomDb?.propertyDao()
    }

    fun initializeProperties() {
        if (propertyDao?.propertyCount() == 0)
            PropertiesRepository(context)
    }

    suspend fun validate(email: String?, password: String) {
        var res: AuthResult? = null
        try {
            res = FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email!!, password).await()
            if (res != null)
                success.postValue(true)
            else
                success.postValue(false)
        } catch (e: Exception) {
            Log.d("debug", "e.localizedMessage")
            success.postValue(false)
        }
    }

    fun getEmail(username: String) {
        val docRef = db.collection("User").document(username)
        docRef.get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot != null) {
                    email.postValue(dataSnapshot.get("email") as String)
                } else {
                    Log.d("Test", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Test", "get failed with ", exception)
            }
    }
}
