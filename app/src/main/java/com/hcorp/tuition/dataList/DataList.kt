package com.hcorp.tuition.dataList

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.hcorp.tuition.R
import com.hcorp.tuition.tools.CustomAlerts

class DataList : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    lateinit var database: FirebaseDatabase
    lateinit var databaseRef:DatabaseReference
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_data_list)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        recyclerView = findViewById(R.id.videoListRecycler)
        databaseRef = database.reference.child("Users").child(auth.uid.toString())
        val databaseReference = databaseRef.child("className")
        databaseReference.get().addOnSuccessListener {
            val classRef = it.value.toString()
            recyclerViewAdapter(classRef, database, this,recyclerView)
        }


    }

    fun recyclerViewAdapter(value:String,database: FirebaseDatabase,context: Context,recycler: RecyclerView, isVideoList:Boolean = false, ){
        val loadingDialog = CustomAlerts().loadingDialog(this,"Loading")
        loadingDialog.show()
        val databaseR:DatabaseReference = database.reference.child("Videos").child(value).child("Subjects")
        if(isVideoList){
            databaseR.child(value)
        }
        databaseR.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val child:Iterable<DataSnapshot> = snapshot.children
                val arrayList:ArrayList<String> = ArrayList()
                var name:String
                for(snap:DataSnapshot in child){
                    name = snap.key.toString()
                    arrayList.add(name)
                }

                val adapter = DataListAdapter(arrayList,context,databaseRef, value )

                val linearLayoutManager = LinearLayoutManager(context)
                linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                recycler.layoutManager = linearLayoutManager
                recycler.adapter = adapter
                loadingDialog.dismiss()

            }

            override fun onCancelled(error: DatabaseError) {
                loadingDialog.dismiss()
            }

        })

    }

}