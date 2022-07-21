package com.example.casino.ui.sport

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.casino.R
import com.example.casino.data.business_objects.Match

class MatchesAdapter : RecyclerView.Adapter<MatchesAdapter.ViewHolder>() {

    var items = listOf<Match>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_match, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val firstTeamLogo: ImageView = view.findViewById(R.id.first_team_logo)
        private val firstTeamName: TextView = view.findViewById(R.id.first_team_name)
        private val secondTeamLogo: ImageView = view.findViewById(R.id.second_team_logo)
        private val secondTeamName: TextView = view.findViewById(R.id.second_team_name)
        private val league: TextView = view.findViewById(R.id.league)
        private val date: TextView = view.findViewById(R.id.date)
        private val score: TextView = view.findViewById(R.id.score)

        @SuppressLint("SetTextI18n")
        fun bind(match: Match) {
            firstTeamName.text = match.firstTeam.name
            secondTeamName.text = match.secondTeam.name

            val scoreFirst = if (match.firstTeam.score.isNotEmpty()) match.firstTeam.score else "0"
            val scoreSecond =
                if (match.secondTeam.score.isNotEmpty()) match.secondTeam.score else "0"
            score.text = "$scoreFirst : $scoreSecond"

            league.text = match.leagueName
            date.text = match.date

            firstTeamLogo.setImageResource(match.firstTeam.logoDrawableRes)
            secondTeamLogo.setImageResource(match.secondTeam.logoDrawableRes)
        }
    }
}