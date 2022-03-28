package com.hcorp.tuition.tools

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.hcorp.tuition.R

class CustomAlerts {
    fun loadingDialog(context: Context, alertMessage: String): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val textView = view.findViewById<TextView>(R.id.loadMessage)
        textView.text = alertMessage
        builder.setView(view)
        return builder.create()
    }
}