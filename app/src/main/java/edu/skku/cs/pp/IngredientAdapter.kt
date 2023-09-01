package edu.skku.cs.pp

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.io.InputStream


class IngredientAdapter(private val dataSet: ArrayList<IngredientList>, val context: Context) :
    RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cocktail_name = view.findViewById<TextView>(R.id.cocktail_name)
        var cocktail_img = view.findViewById<ImageView>(R.id.cocktail_img)
        var coctail_measure = view.findViewById<TextView>(R.id.cocktail_measure)
        init {
            // Define click listener for the ViewHolder's View.
        }

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.ingredient_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val img_host = "https://www.thecocktaildb.com/images/ingredients/${dataSet[position].name}-Small.png"
        Glide.with(context).load(img_host).placeholder(R.drawable.cocktail_back).into(viewHolder.cocktail_img)

        var text = dataSet[position].name
        if (text.length > 15) text = text.slice(0..11) + "..."
        viewHolder.cocktail_name.text = text


        viewHolder.coctail_measure.text = dataSet[position].measure

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
