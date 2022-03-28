package com.hcorp.tuition.dateSecure

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.String.format
import java.net.HttpRetryException


class SecureDate {

    interface ResponseCallback{
        fun processFinish(output:Boolean, date: String)
    }

    fun currentDate(context: Context, callBack: ResponseCallback) = runBlocking{
        getResponse(context, callBack)
    }

    private fun getResponse(context: Context, callBack: ResponseCallback) = CoroutineScope(Dispatchers.Main).launch {
        val url = "https://timeapi.io/api/Time/current/zone?timeZone=Europe/Amsterdam"
        try{
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                {
                    val  data = format(it.getString("year") + it.getString("month") + it.getString("day"))
                    callBack.processFinish(true, data)
                },
                {
                    callBack.processFinish(false, "null")
                }
            )
            HttpRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest)
        } catch (e:HttpRetryException){
            callBack.processFinish(false, "null")
        }
    }
    fun expirayDate(context: Context, subscriptionInDays:Int, callBack: ResponseCallback) = CoroutineScope(Dispatchers.Main).launch {
        postRequest(subscriptionInDays, context, callBack)
    }
    private fun postRequest(subscirptionInDays: Int, context: Context, callBack: ResponseCallback) = CoroutineScope(Dispatchers.Main).launch{
        val urlPost = "https://timeapi.io/api/Calculation/current/increment"
        try{
            val map:MutableMap<String, String> = HashMap()
            map["timeZone"] = "Europe/Amsterdam"
            map["timeSpan"] = "$subscirptionInDays:00:00:00"
            val jsonObject = JSONObject(map as Map<*, *>)
            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, urlPost, jsonObject,
                {
                    val dateJSON = it.getJSONObject("calculationResult")
                    val data:String = format(dateJSON.getString("year") + dateJSON.getString("month") + dateJSON.getString("day"))
                    callBack.processFinish(true, data)
                },
                {
                    callBack.processFinish(false, "null")
                }
            )
            HttpRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest)
        }catch (e:HttpRetryException){
            callBack.processFinish(false, "null")
        }

    }
}