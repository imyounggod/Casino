package com.example.casino.ui.roulettes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.casino.R
import com.example.casino.data.business_objects.Roulette

class RoulettesAdapter(val onRouletteClickedListener: (roulette: Roulette) -> Unit) :
    RecyclerView.Adapter<RoulettesAdapter.ViewHolder>() {

    var items = listOf<Roulette>()
        set(value) {
            field = value
            filterItems()
        }

    var filteredItems = listOf<Roulette>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var searchString: String = ""
        set(value) {
            field = value
            filterItems()
        }

    private fun filterItems() {
        filteredItems = items.filter { it.name.lowercase().contains(searchString.lowercase()) }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_roulette, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredItems[position])
    }

    override fun getItemCount(): Int = filteredItems.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image: ImageView = view.findViewById(R.id.image)
        private val name: TextView = view.findViewById(R.id.name)

        fun bind(roulette: Roulette) {
            image.setImageResource(roulette.icon)
            name.text = roulette.name
            itemView.setOnClickListener {
                onRouletteClickedListener.invoke(roulette)
            }
        }
    }
}