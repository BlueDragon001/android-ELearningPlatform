package com.hcorp.tuition


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hcorp.tuition.dataList.DataList
import com.hcorp.tuition.userHandler.LogReg
import com.hcorp.tuition.userHandler.UserProfile
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var button: Button
    private lateinit var loginUSerText: TextView
    private lateinit var userProfilePic: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        loginUSerText = findViewById(R.id.userName)
        userProfilePic = findViewById(R.id.userProfPic)
        button = findViewById(R.id.openClassObj)
        button.setOnClickListener {
            val intent = Intent(this, DataList::class.java)
            startActivity(intent)
        }
        reload()
        userProfilePic.setOnClickListener {
            val intent = Intent(this, UserProfile::class.java)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            loginUSerText.setOnClickListener {
                val intent = Intent(this, LogReg::class.java)
                startActivity(intent)
            }
        } else {
            return
        }
        reload()

    }

    override fun onResume() {
        super.onResume()
        if (auth.currentUser != null) {
            reload()
        }else{
            defaultUserInfo()
        }
    }

    private fun reload() {
        if (auth.currentUser != null) {
            val databaseRef = database.reference.child("Users").child(auth.uid.toString())
            databaseRef.child("userName").get().addOnSuccessListener {
                loginUSerText.text = it.value.toString()
            }
            userProfilePic.scaleX = 2F
            userProfilePic.scaleY = 2F
            databaseRef.child("profilePicture").get().addOnSuccessListener {
                Picasso.get().load(it.value.toString()).into(userProfilePic)
            }
        } else {
            return
        }
    }

    private fun defaultUserInfo(){
        loginUSerText.text = "Click here to Login"
        userProfilePic.scaleX = 1F
        userProfilePic.scaleY = 1F
        userProfilePic.setImageResource(R.drawable.profile_pic)

    }

}