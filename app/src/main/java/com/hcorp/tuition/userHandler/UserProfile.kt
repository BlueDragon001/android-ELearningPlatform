package com.hcorp.tuition.userHandler


import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hcorp.tuition.R
import com.hcorp.tuition.dateSecure.SecureDate
import com.squareup.picasso.Picasso

class UserProfile : AppCompatActivity() {
    private var userPicture: ImageView? = null
    private var userEmail: TextView? = null
    private var userClass: TextView? = null
    private var userName: TextView? = null
    private var userContact: TextView? = null
    private var logOut: Button? = null
    private var userClassSelector: Spinner? = null
    private var userInfoEdit: Button? = null
    private var userNameEdit: EditText? = null
    private var userContactEdit: EditText? = null
    private var isEditMode: Boolean = false
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        setContentView(R.layout.activity_user_profile)
        varInitiator()


    }

    private fun varInitiator() {
        userPicture = findViewById(R.id.userProfPic)
        userEmail = findViewById(R.id.userEmail)
        userName = findViewById(R.id.userNameProf)
        userInfoEdit = findViewById(R.id.userInfoEdit)
        logOut = findViewById(R.id.logouBtn)
        userClassSelector = findViewById(R.id.classSelectorInfo)
        userContact = findViewById(R.id.userContact)
        userClass = findViewById(R.id.userClassInfo)
        userNameEdit = findViewById(R.id.editUsername)
        userContactEdit = findViewById(R.id.userContactEditor)

    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            profileLoader()
            logOut?.setOnClickListener {
                auth.signOut()
                userPicture?.scaleX = 1F
                userPicture?.scaleY = 1F
                val mainInetent = intent
                finish()
                startActivity(mainInetent)
            }

            userInfoEdit?.setOnClickListener {
                if (!isEditMode) {
                    editMode()
                } else {
                    updateInfo()
                }
            }
        }

    }

    private fun profileLoader() {

        val databaseReference = database.reference.child("Users").child(auth.uid.toString())
        databaseReference.child("profilePicture").get().addOnSuccessListener {
            userPicture?.scaleY = 2f
            userPicture?.scaleX = 2f
            Picasso.get().load(it.value.toString()).into(userPicture)
        }


        databaseReference.child("userName").get().addOnSuccessListener {
            userName?.text = it.value.toString()
            userNameEdit?.setText(it.value.toString())
        }
        databaseReference.child("className").get().addOnSuccessListener {
            userClass?.text = it.value.toString()
        }
        databaseReference.child("userContact").get().addOnSuccessListener {
            userContact?.text = it.value.toString()
            userContactEdit?.setText(it.value.toString())
        }
        userEmail?.text = auth.currentUser?.email

    }

    private fun editMode() {
        isEditMode = true
        userInfoEdit?.text = "Confirm"
        userClassSelector?.visibility = View.VISIBLE
        userContactEdit?.visibility = View.VISIBLE
        userNameEdit?.visibility = View.VISIBLE
        userContact?.visibility = View.GONE
        userClass?.visibility = View.GONE
        userName?.visibility = View.GONE


        ArrayAdapter.createFromResource(this, R.array.Classes, R.layout.dropdown_items).also {
            it.setDropDownViewResource(R.layout.dropdown_items)
            userClassSelector?.adapter = it
        }


    }

    private fun updateInfo() {


        val databaseReference = database.reference.child("Users").child(auth.uid.toString())
        userClassSelector?.visibility = View.GONE
        userContactEdit?.visibility = View.GONE
        userNameEdit?.visibility = View.GONE
        userContact?.visibility = View.VISIBLE
        userClass?.visibility = View.VISIBLE
        userName?.visibility = View.VISIBLE
        val classInt: Int = userClassSelector?.selectedItemPosition!!
        val userClass: String = resources.getStringArray(R.array.Classes)[classInt]
        val newName: String = userName?.text.toString()
        val userNewContact: String = userContactEdit?.text.toString()
        val map: MutableMap<String, String> = HashMap()
        map["className"] = userClass
        map["userName"] = newName
        map["userContact"] = userNewContact


        databaseReference.updateChildren(map as Map<String, Any>).addOnSuccessListener {
            isEditMode = false
            userInfoEdit?.text = "Edit"
            profileLoader()

            val databaseRef =
                database.reference.child("Users").child(auth.uid.toString()).child("Subjects")
            databaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val child: Iterable<DataSnapshot> = snapshot.children
                    val arrayList: ArrayList<String> = ArrayList()
                    var name: String
                    for (snap: DataSnapshot in child) {
                        name = snap.key.toString()
                        arrayList.add(name)
                    }

                    if (!arrayList.contains(userClass)) {
                        getSubject(userClass)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }

    }

    private fun getSubject(className: String) {
        val dataRef = database.reference.child("Videos").child(className).child("Subjects")

        dataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val child: Iterable<DataSnapshot> = snapshot.children
                val arrayList: ArrayList<String> = ArrayList()
                var name: String
                for (snap: DataSnapshot in child) {

                    name = snap.key.toString()
                    arrayList.add(name)

                }
                val map: MutableMap<String, String> = HashMap()
                val databaseRef =
                    database.reference.child("Users").child(auth.uid.toString()).child("Subjects")
                        .child(className)
                SecureDate().currentDate(this@UserProfile, object : SecureDate.ResponseCallback{
                    override fun processFinish(output: Boolean, date: String) {
                        for (string: String in arrayList) {
                            map[string] = date
                        }
                        databaseRef.updateChildren(map as Map<String, Any>)
                    }

                })



            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }
}