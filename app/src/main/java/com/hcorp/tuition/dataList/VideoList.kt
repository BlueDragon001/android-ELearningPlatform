package com.hcorp.tuition.dataList

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.hcorp.tuition.R
import com.hcorp.tuition.tools.CustomAlerts

class VideoList : AppCompatActivity() {
    private lateinit var subjectName:String
    private lateinit var className:String
    private lateinit var recyclerView:RecyclerView
    private lateinit var auth:FirebaseAuth
    lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_video_list)
        val extras: Bundle? = intent.extras
        subjectName = extras?.getString("selectedSubject")?: "null"
        className = extras?.getString("classRef")?:"null"
        recyclerView = findViewById(R.id.videoListRec)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val databaseRef = database.reference.child("Videos").child(className).child("Subjects").child(subjectName)
        videoListLoader(databaseRef)

    }

    private fun videoListLoader(database:DatabaseReference){
        val loadingD = CustomAlerts().loadingDialog(this, "Loading")

        database.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val child:Iterable<DataSnapshot> = snapshot.children
                val arrayList:ArrayList<String> = ArrayList()
                var name:String
                for(snap:DataSnapshot in child){
                    name = snap.key.toString()
                    arrayList.add(name)
                }
                val adapter = DataListAdapter(arrayList,null,null, null, true)
                val linearLayout = LinearLayoutManager(this@VideoList)
                linearLayout.orientation = LinearLayoutManager.VERTICAL
                recyclerView.layoutManager = linearLayout
                recyclerView.adapter = adapter
                loadingD.dismiss()

            }

            override fun onCancelled(error: DatabaseError) {
                loadingD.dismiss()
            }

        })
    }

}