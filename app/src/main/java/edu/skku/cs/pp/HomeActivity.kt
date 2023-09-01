package edu.skku.cs.pp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class HomeActivity : AppCompatActivity() {
    companion object{
        const val EXT_CNAME = "cname"
        const val EXT_Token = "token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.hide()

        val user = FirebaseAuth.getInstance().currentUser

        val g_email = user?.email
        val g_name = user?.displayName
        val g_photoUrl = user?.photoUrl
        var photourl = ""
        val token = intent.getStringExtra(SignInActivity.EXT_Token).toString()
        //val token = g_email.toString()

        val rv = findViewById<RecyclerView>(R.id.randcockview)
        val rv2 = findViewById<RecyclerView>(R.id.likelistview)
        val usertv = findViewById<TextView>(R.id.user_name)
        val chatbt = findViewById<ImageView>(R.id.chatbt)

        val search_et = findViewById<EditText>(R.id.search_name)
        val client = OkHttpClient()
        val mycon = this
        val search_bt = findViewById<Button>(R.id.search_button)
        val items = ArrayList<CoctailList>()
        var like_list = ArrayList<String>()

        val api_url = "https://www.thecocktaildb.com/api/json/v1/1"
        val aws_url = "deleted"

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

        fun search_cocktail(name: String){
            val host = "$api_url/search.php?s=$name"
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

                                if ((search_drinks.drinks != null) && (name != "")) {
                                    val intent = Intent(mycon, CocktailInfoActivity::class.java).apply{
                                        putExtra(EXT_CNAME, search_drinks.drinks[0].strDrink)
                                        putExtra(EXT_Token, token)
                                    }
                                    startActivity(intent)
                                }
                                else{
                                    val intent = Intent(mycon , NoresultActivity::class.java).apply{
                                        putExtra(EXT_Token, token)
                                    }
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
            })
        }

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

        fun getlike(){
            val myAdapter = LikeListAdapter(like_list, applicationContext, token)
            rv2.layoutManager = LinearLayoutManager(mycon)
            rv2.setLayoutManager(
                LinearLayoutManager(
                    mycon,
                    RecyclerView.VERTICAL,
                    false
                )
            )
            rv2.adapter = myAdapter
        }

        fun reguser(token: String){
            Log.w("pp4", "reguser!")
            val json = Gson().toJson(User_token(token))
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val host = "$aws_url/reguser"
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
                        Log.w("pp4", str)
                        val res: userinfo? = Gson().fromJson(str, userinfo::class.java)
                        CoroutineScope(Dispatchers.Main).launch {
                            if (res != null) {
                                Log.w("pp4", res.like.toString())
                                like_list = res.like
                                getlike()
                            }
                        }
                    }
                }
            })
        }

        fun getuser(token: String){
                Log.w("pp4", "getuser!")
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
                        Log.w("pp4", str)
                        val res: userinfo? = Gson().fromJson(str, userinfo::class.java)
                        CoroutineScope(Dispatchers.Main).launch {
                            if (res != null) {
                                Log.w("pp4", res.like.toString())
                                like_list = res.like
                                getlike()
                            }
                        }
                    }
                }
            })
        }

        fun isuser(token: String){

            Log.w("pp4", token.toString())
            val json = Gson().toJson(User_token(token))
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val host = "$aws_url/isuser"
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
                        Log.w("pp4", str)
                        val res: Isuser? = Gson().fromJson(str, Isuser::class.java)
                        CoroutineScope(Dispatchers.Main).launch {
                            if (res != null) {
                                if (res.ans) {
                                    Log.w("pp4", "yes....!")
                                    getuser(token)
                                } else {
                                    Log.w("pp4", "no....!")
                                    reguser(token)
                                }
                            }
                        }
                    }
                }
            })
        }


        usertv.text = g_name

        getuser(token)
        //isuser(token)

        for(i in 1..10) random_cocktail()
        search_bt.setOnClickListener {
            search_cocktail(search_et.text.toString())
        }

        rv.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(!rv.canScrollHorizontally(1)
                    && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    for(i in 1..10) random_cocktail()
                }
            }
        })

        chatbt.setOnClickListener {
            val intent = Intent(mycon , ChatBartenderActivity::class.java).apply{
                putExtra(EXT_Token, g_name)
            }
            startActivity(intent)
        }
    }

    override fun onRestart() {
        super.onRestart()
        val search_et = findViewById<EditText>(R.id.search_name)
        search_et.text.clear()

        val user = FirebaseAuth.getInstance().currentUser
        val token = intent.getStringExtra(SignInActivity.EXT_Token).toString()

        val rv2 = findViewById<RecyclerView>(R.id.likelistview)
        val aws_url = "deleted"
        val client = OkHttpClient()
        val mycon = this

        fun Regetlike(){
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
                        Log.w("pp4", str)
                        val res: userinfo? = Gson().fromJson(str, userinfo::class.java)
                        CoroutineScope(Dispatchers.Main).launch {
                            if (res != null) {
                                if (res.like != null) {
                                    Log.w("pp4", res.like.toString())

                                    val myAdapter = LikeListAdapter(res.like, applicationContext, token)

                                    rv2.layoutManager = LinearLayoutManager(mycon)
                                    rv2.setLayoutManager(
                                        LinearLayoutManager(
                                            mycon,
                                            RecyclerView.VERTICAL,
                                            false
                                        )
                                    )
                                    rv2.adapter = myAdapter
                                }
                            }
                        }
                    }
                }
            })
        }
        Regetlike()
    }


}