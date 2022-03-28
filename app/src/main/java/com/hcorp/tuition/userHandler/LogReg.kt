package com.hcorp.tuition.userHandler

import android.animation.LayoutTransition
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.hcorp.tuition.R
import com.hcorp.tuition.dateSecure.SecureDate
import com.hcorp.tuition.tools.CustomAlerts


class LogReg : AppCompatActivity() {
    private var InputHandler: LinearLayout? = null
    private var ProfileILayout: LinearLayout? = null
    private var UserInfoLayout: LinearLayout? = null
    private var UserCLayout: LinearLayout? = null
    private var UserCGLayout: LinearLayout? = null
    //Layout Variable End

    //Non Credentials Inputs Fields Start
    private var UserContactField: EditText? = null
    private var UserNameField: EditText? = null
    private var ClassSelector: Spinner? = null
    private var GenderSelector: Spinner? = null
    private var ProfileImgSelector: ImageView? = null
    //Non Credentials Inputs Fields End

    // Credentials Input Fields Start
    private lateinit var PasswordInputField: EditText
    private lateinit var ConfirmPasswordInputField: EditText
    private lateinit var EmailInputField: EditText
    // Credentials Input Fields End

    // Buttons&Others Inputs Start
    private var NextButton: Button? = null
    private var Login: Button? = null
    private var QuoteText: TextView? = null
    // Buttons Inputs End
    //LayoutVariable End

    private var regSec: Boolean = false

    private var isRegPage = true

    lateinit var password: String
    private lateinit var authe: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var fstorage: FirebaseStorage
    private var isUserDataPage: Boolean = false
    var uploadUrl: String? = ""

    // Non Layout Variable Start

    // Non Layout Variable End
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration2)
        authe = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        fstorage = FirebaseStorage.getInstance()
        VarIntantiator()
        findViewById<LinearLayout>(R.id.inputHandler).apply {
            LayoutTransition.APPEARING
        }

        NextButton?.setOnClickListener {
            if (!isUserDataPage) {
                if (authe.currentUser != null) {
                    UserDataFunction()
                } else {
                    userRegistrationVerification()
                }

            } else {
                userDhata()
            }

        }


    }

    override fun onStart() {
        super.onStart()
        findViewById<LinearLayout>(R.id.inputHandler).apply {
            LayoutTransition.APPEARING
        }
        Login?.setOnClickListener {
            RegandLogin()
        }


    }

    private fun VarIntantiator() {

        //Layout Start
        InputHandler = findViewById(R.id.inputHandler)
        ProfileILayout = findViewById(R.id.profilePicLayout)
        UserInfoLayout = findViewById(R.id.userInfoLayout)
        UserCLayout = findViewById(R.id.userCredentialLayout)
        UserCGLayout = findViewById(R.id.userClassGenLayout)
        //Layout End

        //Non Credential Input Field Start
        UserContactField = findViewById(R.id.phoneInputField)
        UserNameField = findViewById(R.id.nameInputField)
        ClassSelector = findViewById(R.id.classSelectorInfo)
        GenderSelector = findViewById(R.id.genderSelector)
        ProfileImgSelector = findViewById(R.id.userProfilePic)

        //Non Credential Input Field End

        //Credential Input Field Start
        EmailInputField = findViewById(R.id.inputEmail)
        PasswordInputField = findViewById(R.id.inputPassword)
        ConfirmPasswordInputField = findViewById(R.id.confirmPassword)

        //Credential Input Field End

        //Other Input Variable Start
        NextButton = findViewById(R.id._next)
        Login = findViewById(R.id.loginBtn)
        QuoteText = findViewById<TextView>(R.id.regQuote)
        //Other Input Variable End


    }

    private fun RegandLogin() {
        if (isRegPage) {
            isUserDataPage = false
            isRegPage = false
            QuoteText?.text = "Hey there, Welcome Back"
            Login?.text = "Registration"
            val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.attg)
            val animation2 = AnimationUtils.loadAnimation(this, R.anim.atg)
            val animation3 = AnimationUtils.loadAnimation(this, R.anim.wd)

            ConfirmPasswordInputField.visibility = View.GONE
            NextButton?.startAnimation(animation2)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                    NextButton?.startAnimation(animation3)
                }

                override fun onAnimationEnd(p0: Animation?) {

                }

                override fun onAnimationRepeat(p0: Animation?) {

                }

            })
        } else {
            isRegPage = true
            QuoteText?.text = "Let's Create Your Account"
            Login?.text = "Login"
            ConfirmPasswordInputField.visibility = View.VISIBLE
        }

    }

    private fun UserDataFunction() {
        if (isRegPage) {
            isUserDataPage = true
            ProfileILayout?.visibility = View.VISIBLE
            UserInfoLayout?.visibility = View.VISIBLE
            UserCLayout?.visibility = View.GONE
            Login?.visibility = View.GONE
            NextButton?.text = "Submit"
            QuoteText?.text = "Let's,Get Personalized"
            isRegPage = false
            ListInflator()

            ProfileImgSelector?.setOnClickListener {
                UserImage()

            }
        } else {
            return
        }
    }

    private fun ListInflator() {
        ArrayAdapter.createFromResource(this, R.array.Classes, R.layout.dropdown_items)
            .also { adapter ->
                adapter.setDropDownViewResource(R.layout.dropdown_items)
                ClassSelector?.adapter = adapter
            }
        ArrayAdapter.createFromResource(this, R.array.Sex, R.layout.dropdown_items)
            .also { arrayAdapter ->
                arrayAdapter.setDropDownViewResource(R.layout.dropdown_items)
                GenderSelector?.adapter = arrayAdapter

            }

    }

    private fun UserImage() {
        val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
        photoPickerIntent.type = "image/*"
        getResult.launch(photoPickerIntent)
    }

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val value = it.data?.dataString
                uploadUrl = value
                val chosenImageUri: Uri = Uri.parse(value)
                ProfileImgSelector?.scaleType = ImageView.ScaleType.CENTER_INSIDE
                ProfileImgSelector?.setImageURI(chosenImageUri)
            }
        }


//    override fun onBackPressed() {
//        if (!isRegPage){
//            ProfileILayout?.visibility = View.GONE
//            UserInfoLayout?.visibility = View.GONE
//            UserCLayout?.visibility = View.VISIBLE
//            Login?.visibility = View.VISIBLE.also { Login?.text = "Login" }
//            ConfirmPasswordInputField.visibility = View.VISIBLE
//            NextButton?.text = "Next"
//            QuoteText?.text = "Let's, Create Your Account"
//            isRegPage = true
//            ConfirmPasswordInputField.hint = "Password"
//        }
//        else{
//            super.onBackPressed()
//        }
//
//    }

    private fun userRegistrationVerification() {
        val passwordC = PasswordInputField.text
        val confirmPassword = ConfirmPasswordInputField.text
        val email = EmailInputField.text
        password = passwordC.toString()

        if (email != null && passwordC != null) {
            if (passwordC.length >= 8) {
                val emailS = email.toString()
                if (!isRegPage) {
                    signInUser(emailS, password)
                } else {
                    if (password == confirmPassword.toString()) {
                        registerUser(emailS, password)
                    } else {
                        ConfirmPasswordInputField.hint = "Password Doesn't match"
                    }
                }
            } else {
                PasswordInputField.hint = "Password at least be 8 Charcters"
            }
        } else {
            Toast.makeText(this, "Password or Email is wrong", Toast.LENGTH_LONG).show()
        }

    }


    private fun registerUser(email: String, password: String) {
        val loading = CustomAlerts().loadingDialog(this, "Registring User")
        loading.show()
        authe.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {

                loading.dismiss()
                UserDataFunction()
            } else {
                Toast.makeText(
                    baseContext, "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()

                loading.dismiss()
            }
        }
    }

    private fun signInUser(email: String, password: String) {
        val loading = CustomAlerts().loadingDialog(this,"Singing In")
        loading.show()
        authe.signInWithEmailAndPassword(email, password).addOnCompleteListener {

            if (it.isSuccessful) {
                loading.dismiss()


            } else {
                Toast.makeText(
                    baseContext, "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
                loading.dismiss()

            }
        }
    }


    private fun userDhata() {
        var loading = CustomAlerts().loadingDialog(this,"Creating Account")
        loading.show()
        var photo: String
        val uid = authe.uid.toString()
        val userStorage = fstorage.reference.child(uid + "/images")
        val uploadTask = userStorage.putFile(Uri.parse(uploadUrl))
        uploadTask.addOnSuccessListener {
            userStorage.downloadUrl.addOnSuccessListener {
                photo = it.toString()
                val userName: String = UserNameField?.text.toString()
                val userContact: String = "+977" + UserContactField?.text.toString()
                val classInt: Int = ClassSelector?.selectedItemPosition?.toInt()!!
                val userClass: String = resources.getStringArray(R.array.Classes)[classInt]
                val genderInt: Int = GenderSelector?.selectedItemPosition?.toInt()!!
                val userSex: String = resources.getStringArray(R.array.Sex)[genderInt]
                val post = firebasePost(userName, userClass, userSex, userContact, photo)
                val databaseRef = database.child("Users").child(uid)
                getSubject(userClass)

                databaseRef.setValue(post).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Sucess", Toast.LENGTH_LONG).show()
                        loading.hide()
                        loading.dismiss()
                        Toast.makeText(this, "Sucess Subject", Toast.LENGTH_LONG).show()


                    } else {
                        Toast.makeText(this, "Sucess", Toast.LENGTH_LONG).show()
                        loading.hide()
                        loading.dismiss()
                    }
                }
            }
        }


    }

    private fun getSubject(className: String) {
        val dataRef = database.child("Videos").child(className).child("Subjects")
        dataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val child: kotlin.collections.Iterable<DataSnapshot> = snapshot.children
                val map: MutableMap<String, String> = HashMap()
                var arrayList: ArrayList<String> = ArrayList()
                var name: String
                for (snap: DataSnapshot in child) {

                    name = snap.key.toString()
                    arrayList.add(name)
                }
                val databaseRef =
                    database.child("Users").child(authe.uid.toString()).child("Subjects")
                        .child(className)
                SecureDate().currentDate(applicationContext, object : SecureDate.ResponseCallback{
                    override fun processFinish(output: Boolean, date: String) {
                        for(string:String in arrayList){
                            map.put(string, date.toString())
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


