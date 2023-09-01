package edu.skku.cs.pp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class LikeListAdapter(private val dataSet: ArrayList<String>, val context: Context, val token: String) :
    RecyclerView.Adapter<LikeListAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cocktail_name = view.findViewById<TextView>(R.id.cocktail_name)
        var cocktail_img = view.findViewById<ImageView>(R.id.cocktail_img)
        var h_btn = view.findViewById<ImageView>(R.id.heart)
        init {
            // Define click listener for the ViewHolder's View.
        }

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.like_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val client = OkHttpClient()
        var name = dataSet[position]
        var sname = name
        val api_url = "https://www.thecocktaildb.com/api/json/v1/1"
        val aws_url = "deleted"
        val mycon = this

        val user = FirebaseAuth.getInstance().currentUser
        val token = token
        var is_like = true

        fun dellike(){
            Log.w("pp8", "dellike! $name")
            val json = Gson().toJson(Dellike(token, name))
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
            val json = Gson().toJson(Addlike(token, name))
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

        if (name.length > 15) sname = name.slice(0..12) + "..."
        viewHolder.cocktail_name.text = sname

        viewHolder.cocktail_name.setOnClickListener {
            val intent = Intent(context, CocktailInfoActivity::class.java).apply{
                putExtra(HomeActivity.EXT_CNAME, name)
                putExtra(HomeActivity.EXT_Token, token)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        viewHolder.h_btn.setOnClickListener{
            if (is_like) {
                dellike()
                is_like = false
                viewHolder.h_btn.setImageResource(R.drawable.heart_e)
                dataSet.remove(name)
                this.notifyItemRemoved(position)
            }
            else{
                addlike()
                is_like = true
                viewHolder.h_btn.setImageResource(R.drawable.heart)
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
