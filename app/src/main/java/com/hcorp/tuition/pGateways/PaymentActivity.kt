package com.hcorp.tuition.pGateways

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.esewa.android.sdk.payment.ESewaConfiguration
import com.esewa.android.sdk.payment.ESewaPayment
import com.esewa.android.sdk.payment.ESewaPaymentActivity
import com.esewa.android.sdk.payment.ESewaPaymentConfirmActivity
import com.hcorp.tuition.R

class PaymentActivity : AppCompatActivity() {
    private lateinit var eSewaPaymentBtn:LinearLayout
    private lateinit var eSewaClientID:String
    private lateinit var eSewaClientSecret:String
    private val price = 500
    private val productName = "Video"


    enum class RequestCodes {
        OK{
            override fun requestCode(): Int {
                return 1
            }
        };
        abstract fun requestCode():Int}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        eSewaPaymentBtn = findViewById(R.id.EsewaPayment)
        val keysMetaData= applicationContext.packageManager.getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        eSewaClientID = keysMetaData.metaData["ESewaClientId"].toString()
        eSewaClientSecret = keysMetaData.metaData["ESewaClientKey"].toString()
        eSewaPaymentBtn.setOnClickListener{
            eSewPayment()
        }
    }

    private fun eSewPayment(){
        val eSewaConfiguration: ESewaConfiguration = ESewaConfiguration()
            .clientId(eSewaClientID)
            .secretKey(eSewaClientSecret)
            .environment(ESewaConfiguration.ENVIRONMENT_TEST)
        val eSewaPayment = ESewaPayment(price.toString(),
        productName, 1.toString(), null)

        val intent = Intent(this, ESewaPaymentActivity::class.java)
        intent.putExtra(ESewaConfiguration.ESEWA_CONFIGURATION, eSewaConfiguration)

        intent.putExtra(ESewaPayment.ESEWA_PAYMENT, eSewaPayment)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intent = Intent(this, ESewaPaymentConfirmActivity::class.java)
        if (requestCode == RequestCodes.OK.requestCode()) {
            startActivity(intent)
            if (resultCode == Activity.RESULT_OK) {
                val s = data!!.getStringExtra(ESewaPayment.EXTRA_RESULT_MESSAGE)

                if (s != null) {
                    Log.i("Proof of Payment", s)
                }
                Toast.makeText(this, "SUCCESSFUL PAYMENT", Toast.LENGTH_SHORT).show()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                val s = data!!.getStringExtra(ESewaPayment.EXTRA_RESULT_MESSAGE)
                if (s != null) {
                    Log.i("Proof of Payment", s)
                }
                Toast.makeText(this, "Canceled By User", Toast.LENGTH_SHORT).show()
            } else if (resultCode == ESewaPayment.RESULT_EXTRAS_INVALID) {
                val s = data!!.getStringExtra(ESewaPayment.EXTRA_RESULT_MESSAGE)
                if (s != null) {
                    Log.i("Proof of Payment", s)
                }
            }
        }
    }

}