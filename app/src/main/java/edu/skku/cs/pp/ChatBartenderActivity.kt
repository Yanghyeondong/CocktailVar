package edu.skku.cs.pp

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit


class ChatBartenderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbar)
        supportActionBar?.hide()
        var tv = findViewById<TextView>(R.id.chatres)
        var bell = findViewById<ImageView>(R.id.bell)
        var et = findViewById<EditText>(R.id.userreq)
        var question_end = " 칵테일 레시피 1개와 만드는 법 간단히 알려줘"
        var question = ""
        val client = OkHttpClient()
            .newBuilder().connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        fun getRes(que: String){
            val apiKey = "deleted"
            val url="deleted"
            Log.w("pp3",que)
            val requestBody="""
            {
            "prompt": "$que",
            "max_tokens": 400,
            "temperature": 1.0
            }
            """.trimIndent()

            val request = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("error","API failed",e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body=response.body?.string()
                    if (body != null) {
                        Log.v("data",body)
                    }
                    else{
                        Log.v("data","empty")
                    }
                    val jsonObject= JSONObject(body)
                    val jsonArray: JSONArray =jsonObject.getJSONArray("choices")
                    val textResult=jsonArray.getJSONObject(0).getString("text")

                    CoroutineScope(Dispatchers.Main).launch {
                        tv.text = textResult.trim() + "\n\n"
                    }

                }
            })
        }

        bell.setOnClickListener {
            tv.text = "잠시만 기다려주십시오..."
            var question_start = et.text.toString()
            var que = question_start + question_end
            getRes(que)
        }

    }
}
