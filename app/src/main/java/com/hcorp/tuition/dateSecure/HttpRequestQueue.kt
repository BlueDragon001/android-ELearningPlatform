package com.hcorp.tuition.dateSecure

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class HttpRequestQueue constructor(context: Context) {
    companion object{
        @Volatile
        private var INSTANCE: HttpRequestQueue? = null
        fun getInstance(context: Context) =
            run {
                INSTANCE ?:synchronized(this){
                    INSTANCE ?: HttpRequestQueue(context).also {
                        INSTANCE = it
                    }
                }
            }
    }

    val requestQueue:RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>){
        requestQueue.add(req)
    }
}