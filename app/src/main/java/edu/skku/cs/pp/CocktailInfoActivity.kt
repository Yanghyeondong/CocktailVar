package edu.skku.cs.pp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class CocktailInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cocktail_info)
        supportActionBar?.hide()

        val cname = intent.getStringExtra(HomeActivity.EXT_CNAME) ?: "empty"
        val tv = findViewById<TextView>(R.id.cocktailname)
        val tv_ins = findViewById<TextView>(R.id.instructions)
        val h_btn = findViewById<ImageButton>(R.id.heart)
        val iv = findViewById<ImageView>(R.id.imageView)
        val rv = findViewById<RecyclerView>(R.id.tagrec)
        val rv2 = findViewById<RecyclerView>(R.id.ing_rv)
        val client = OkHttpClient()
        var photourl = ""
        val mycon = this
        var is_like = false

        val user = FirebaseAuth.getInstance().currentUser
        val token = intent.getStringExtra(HomeActivity.EXT_Token).toString()
        val aws_url = "deleted"

        fun cocktail_info(name: String){
            val host = "https://www.thecocktaildb.com/api/json/v1/1/search.php?s=$name"
            val req = Request.Builder().url(host).get().build()
            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }
                override fun onResponse(call: Call, response: Response) {
                    response.use{
                        if (!response.isSuccessful) {
                            throw IOException("Unexpected code $response")
                        }
                        val str = response.body!!.string()
                        Log.w("pp5",str)
                        val search_drinks: Search_drink? = Gson().fromJson(str, Search_drink::class.java)


                        CoroutineScope(Dispatchers.Main).launch {
                            if (search_drinks != null) {

                                var final_item = ArrayList<String>()
                                var i_list = ArrayList<IngredientList>()

                                photourl = search_drinks.drinks[0].strDrinkThumb
                                var text = search_drinks.drinks[0].strDrink

                                if (text.length > 22){
                                    text = text.slice(0..17) + " ..."
                                }
                                tv.text = text + "  "
                                tv_ins.text = search_drinks.drinks[0].strInstructions

                                final_item.add(search_drinks.drinks[0].strAlcoholic)
                                if (search_drinks.drinks[0].strCategory == "Ordinary Drink") final_item.add("Ordinary")
                                else if (search_drinks.drinks[0].strCategory == "Punch / Party Drink") final_item.add("Punch / Party")
                                else final_item.add(search_drinks.drinks[0].strCategory)

                                if ((search_drinks.drinks[0].strIngredient1 != null) && (search_drinks.drinks[0].strMeasure1 != null))
                                    i_list.add(IngredientList(search_drinks.drinks[0].strIngredient1, search_drinks.drinks[0].strMeasure1))
                                if ((search_drinks.drinks[0].strIngredient2 != null) && (search_drinks.drinks[0].strMeasure2 != null))
                                    i_list.add(IngredientList(search_drinks.drinks[0].strIngredient2, search_drinks.drinks[0].strMeasure2))
                                if ((search_drinks.drinks[0].strIngredient3 != null) && (search_drinks.drinks[0].strMeasure3 != null))
                                    i_list.add(IngredientList(search_drinks.drinks[0].strIngredient3, search_drinks.drinks[0].strMeasure3))
                                if ((search_drinks.drinks[0].strIngredient4 != null) && (search_drinks.drinks[0].strMeasure4 != null))
                                    i_list.add(IngredientList(search_drinks.drinks[0].strIngredient4, search_drinks.drinks[0].strMeasure4))
                                if ((search_drinks.drinks[0].strIngredient5 != null) && (search_drinks.drinks[0].strMeasure5 != null))
                                    i_list.add(IngredientList(search_drinks.drinks[0].strIngredient5, search_drinks.drinks[0].strMeasure5))
                                if ((search_drinks.drinks[0].strIngredient6 != null) && (search_drinks.drinks[0].strMeasure6 != null))
                                    i_list.add(IngredientList(search_drinks.drinks[0].strIngredient6, search_drinks.drinks[0].strMeasure6))
                                if ((search_drinks.drinks[0].strIngredient7 != null) && (search_drinks.drinks[0].strMeasure7 != null))
                                    i_list.add(IngredientList(search_drinks.drinks[0].strIngredient7, search_drinks.drinks[0].strMeasure7))
                                if ((search_drinks.drinks[0].strIngredient8 != null) && (search_drinks.drinks[0].strMeasure8 != null))
                                    i_list.add(IngredientList(search_drinks.drinks[0].strIngredient8, search_drinks.drinks[0].strMeasure8))


                                val myAdapter = TagAdapter(final_item, applicationContext)
                                rv.layoutManager = LinearLayoutManager(
                                    mycon,
                                    RecyclerView.HORIZONTAL,
                                    false
                                )
                                rv.adapter = myAdapter

                                val myAdapter2 = IngredientAdapter(i_list, applicationContext)

                                rv2.layoutManager = LinearLayoutManager(mycon)
                                rv2.setLayoutManager(
                                    LinearLayoutManager(
                                        mycon,
                                        RecyclerView.HORIZONTAL,
                                        false
                                    )
                                )
                                rv2.adapter = myAdapter2

                            }
                            val img_host = photourl

                            Glide.with(mycon).load(img_host).placeholder(R.drawable.cocktail_back).into(iv)
                        }
                    }
                }
            })
        }

        fun btnset(){

            Log.w("pp4", "btnset!")
            val json = Gson().toJson(User_token(token))
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val host = "$aws_url/getuser"
            val req = Request.Builder().url(host).post(json.toString().toRequestBody(mediaType)).build()

            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }
                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            throw IOException("Unexpected code $response")
                        }
                        val str = response.body!!.string()
                        Log.w("pp8", str)
                        val res: userinfo? = Gson().fromJson(str, userinfo::class.java)
                        CoroutineScope(Dispatchers.Main).launch {
                            if (res != null) {
                                if (res.like != null) {
                                    Log.w("pp8", res.like.toString())
                                    Log.w("pp8", cname.toString())
                                    if (res.like.contains(cname)) {
                                        Log.w("pp8", "good")
                                        h_btn.setImageResource(R.drawable.heart)
                                        is_like = true
                                    }
                                }
                                else {h_btn.setImageResource(R.drawable.heart)}

                            }
                        }
                    }
                }
            })

        }

        fun dellike(){
            Log.w("pp8", "dellike! $cname $token")
            val json = Gson().toJson(Dellike(token, cname))
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val host = "$aws_url/dellike"
            val req = Request.Builder().url(host).post(json.toString().toRequestBody(mediaType)).build()

            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }
                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            throw IOException("Unexpected code $response")
                        }
                        val str = response.body!!.string()
                        Log.w("pp8", str)
                    }
                }
            })
        }

        fun addlike(){
            Log.w("pp8", "addlike!")
            val json = Gson().toJson(Addlike(token, cname))
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val host = "$aws_url/addlike"
            val req = Request.Builder().url(host).post(json.toString().toRequestBody(mediaType)).build()

            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }
                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            throw IOException("Unexpected code $response")
                        }
                        val str = response.body!!.string()
                        Log.w("pp8", str)
                    }
                }
            })
        }

        if (cname != null) cocktail_info(cname)
        btnset()

        h_btn.setOnClickListener{
            if (is_like) {
                dellike()
                is_like = false
                h_btn.setImageResource(R.drawable.heart_e)
            }
            else{
                addlike()
                is_like = true
                h_btn.setImageResource(R.drawable.heart)
            }

        }
    }
}