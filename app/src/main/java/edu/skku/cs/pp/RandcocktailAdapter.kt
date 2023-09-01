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
import com.bumptech.glide.Glide


class RandcocktailAdapter(private val dataSet: ArrayList<CoctailList>, val context: Context, val token: String) :
    RecyclerView.Adapter<RandcocktailAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cocktail_name = view.findViewById<TextView>(R.id.cocktail_name)
        var cocktail_img = view.findViewById<ImageView>(R.id.cocktail_img)
        init {
            // Define click listener for the ViewHolder's View.
        }

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val img_host = dataSet[position].img_link
        Glide.with(context).load(img_host).placeholder(R.drawable.cocktail_back).into(viewHolder.cocktail_img)
        viewHolder.cocktail_name.text = dataSet[position].name
        viewHolder.itemView.setOnClickListener{
            Log.w("pp3", dataSet[position].name)
            val intent = Intent(context, CocktailInfoActivity::class.java).apply{
                putExtra(HomeActivity.EXT_CNAME, dataSet[position].name)
                putExtra(HomeActivity.EXT_Token, token)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
