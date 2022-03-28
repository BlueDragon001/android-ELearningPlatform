package com.hcorp.tuition.dataList


import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.hcorp.tuition.R
import com.hcorp.tuition.dateSecure.SecureDate
import com.hcorp.tuition.pGateways.PaymentActivity
import com.hcorp.tuition.tools.CustomAlerts


class DataListAdapter(private val dataSet: ArrayList<String>,private val context: Context?,
                      private val database: DatabaseReference?, private val classRef:String?, private val isVideoList:Boolean = false):
    RecyclerView.Adapter<DataListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.subject_list_holder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val button = holder.textView
        button.text = dataSet[position]
        if(!isVideoList){
            button.gravity = Gravity.CENTER
        }else{
            button.gravity = Gravity.START
        }
        button.setOnClickListener{
            if(!isVideoList){
                paymentValidator(button)
            }

        }
    }

    override fun getItemCount() = dataSet.size
    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val textView: TextView = view.findViewById(R.id.videoListHolder)
        val layout:FrameLayout = view.findViewById(R.id.listLayout)

    }

    private fun paymentValidator(subjectButton:TextView){
        val subjectName:String = subjectButton.text.toString()
        val loadingDialog = context?.let { CustomAlerts().loadingDialog(it, "Loading") }
        loadingDialog?.show()
        context?.let { context1 ->
            SecureDate().currentDate(context1, object : SecureDate.ResponseCallback{
                override fun processFinish(output: Boolean, date: String) {
                    val oldDatabase = classRef?.let { it1 -> database?.child("Subjects")?.child(it1)?.child(subjectName) }
                    oldDatabase?.get()?.addOnSuccessListener {
                        val expiryDate = it.value.toString().toInt()
                        val currentDate = date.toInt()
                        loadingDialog?.dismiss()
                        if(expiryDate <= currentDate){
                            val intent = Intent(context, PaymentActivity::class.java)
                            intent.putExtra("selectedSubject", subjectName)
                            intent.putExtra("classRef", classRef)
                            startActivity(context, intent, null)
                        }else{
                            val intent = Intent(context, VideoList::class.java)
                            intent.putExtra("selectedSubject", subjectName)
                            intent.putExtra("classRef", classRef)
                            startActivity(context, intent, null)
                        }

                    }
                }

            })
        }

    }
}