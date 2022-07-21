package com.example.casino.ui.sport

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.casino.R
import com.example.casino.data.business_objects.Sport
import com.example.casino.data.business_objects.getIcon
import com.example.casino.ui.view_extensions.setVisible

class SportFilterAdapter(
    defaultSelectedSport: Sport,
    val onSportClickedListener: (position: Int, sport: Sport) -> Unit
) :
    RecyclerView.Adapter<SportFilterAdapter.ViewHolder>() {

    private var items = listOf(
        Sport.SOCCER,
        Sport.BASKETBALL,
        Sport.GOLF,
        Sport.AMERICAN_FOOTBALL,
        Sport.TENNIS
    )
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectedSport: Sport = defaultSelectedSport

    init {
        onSportClickedListener.invoke(items.indexOf(selectedSport), selectedSport)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_sport_filter, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val icon: ImageView = view.findViewById(R.id.icon)
        private val title: TextView = view.findViewById(R.id.title)

        fun bind(position: Int, sport: Sport) {
            icon.setImageResource(sport.getIcon())
            title.text = sport.sportName
            title.setVisible(selectedSport == sport)
            itemView.setOnClickListener {
                selectedSport = sport
                notifyDataSetChanged()
                onSportClickedListener.invoke(position, sport)
            }
        }
    }
}