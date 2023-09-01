package edu.skku.cs.pp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class NoresultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noresult)
        supportActionBar?.hide()

        val rv = findViewById<RecyclerView>(R.id.randcockview)
        val client = OkHttpClient()
        val api_url = "https://www.thecocktaildb.com/api/json/v1/1"
        val items = ArrayList<CoctailList>()
        val mycon = this
        val token = intent.getStringExtra(HomeActivity.EXT_Token).toString()
        val rvAdapter = RandcocktailAdapter(items, applicationContext, token)

        rv.layoutManager = LinearLayoutManager(mycon)
        rv.setLayoutManager(
            LinearLayoutManager(
                mycon,
                RecyclerView.HORIZONTAL,
                false
            )
        )
        rv.adapter = rvAdapter

        fun random_cocktail(){

            val host = "$api_url/random.php"
            val req = Request.Builder().url(host).get().build()
            val ans = client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }
                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            throw IOException("Unexpected code $response")
                        }
                        val str = response.body!!.string()
                        val search_drinks: Search_drink? = Gson().fromJson(str, Search_drink::class.java)
                        CoroutineScope(Dispatchers.Main).launch {
                            if (search_drinks != null) {
                                items.add(CoctailList(search_drinks.drinks[0].strDrink, search_drinks.drinks[0].strDrinkThumb))
                            }
                            rvAdapter.notifyDataSetChanged()
                        }
                    }
                }
            })
        }

        for(i in 1..10) random_cocktail()

        rv.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(!rv.canScrollHorizontally(1)
                    && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    for(i in 1..10) random_cocktail()
                }
            }
        })

    }
}