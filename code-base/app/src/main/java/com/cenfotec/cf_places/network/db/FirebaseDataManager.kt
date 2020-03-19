package com.cenfotec.cf_places.network.db

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseDataManager {

    /* * * * * * * * * * * * * * * * * * * * * * * * * * *
    * LOCAL VARIABLES
    * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private val firebaseDB = Firebase.firestore

    init {
        Log.d("DEBUG/", "FIRE_BASE MANAGER")
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * *
    * LOCAL VARIABLES
    * * * * * * * * * * * * * * * * * * * * * * * * * * */

    fun pushUserToDB(email: String?, name: String?, imageUrl: String?) {
        // TODO: save user on the database if it doesn't exist.
        val db = Firebase.firestore

        val user = hashMapOf(
            "name" to name,
            "imageUrl" to imageUrl
        )

        email?.let {
            db.collection("users")
                .document(email)
                .set(user)
                .addOnSuccessListener {
                    Log.d("DEBUG/", "DocumentSnapshot added with ID")
                }
                .addOnFailureListener { e ->
                    Log.w("DEBUG/", "Error adding document", e)
                }
        }
    }

    fun setUserToDB(email: String?, user: HashMap<String, String?>): Task<Void>? {
        return email?.let {
            firebaseDB.collection("users")
                .document(email)
                .set(user)
        }
    }


    fun getUsersFromDB(): Task<QuerySnapshot> {
        return firebaseDB.collection("users").get()
    }
}