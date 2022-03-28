package com.hcorp.tuition

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.runBlocking

class UserData {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    lateinit var userName: String
    lateinit var userClass: String
    lateinit var userSex: String
    lateinit var userContact: String
    var userProfilePic: String? = "null"
    val userEmail: String
  var databaseRef:DatabaseReference = database.reference.child("User").child(auth.uid.toString())

    init {


        getValue("userName", object : ListenerCallback {
            override fun finishProcess(output: Boolean, returnValue: String?) {
                userName = returnValue.toString()
            }
        })

        getValue("className", object : ListenerCallback {
            override fun finishProcess(output: Boolean, returnValue: String?) {
                userClass = returnValue.toString()
            }
        })

        getValue("userGender", object : ListenerCallback {
            override fun finishProcess(output: Boolean, returnValue: String?) {
                userSex = returnValue.toString()
            }
        })

        getValue("userContact", object : ListenerCallback {
            override fun finishProcess(output: Boolean, returnValue: String?) {
                userContact = returnValue.toString()
            }
        })
        getValue("profilePicture", object : ListenerCallback {
            override fun finishProcess(output: Boolean, returnValue: String?) {
                userProfilePic = returnValue.toString()
            }
        })

        userEmail = auth.currentUser?.email.toString()



    }



    private interface ListenerCallback{
        fun finishProcess(output:Boolean, returnValue: String?)
    }

    private fun getValue(keyForString:String, callback:ListenerCallback ) = runBlocking{
        getValueMethod(keyForString, callback)
    }

    private fun getValueMethod(keyForString:String, callback:ListenerCallback){
        val database = databaseRef.child(keyForString).get()
        database.addOnSuccessListener {
            val dataValue = it.value.toString()
            callback.finishProcess(true, dataValue)
        }.addOnFailureListener{
            callback.finishProcess(false, null)
        }
    }
}