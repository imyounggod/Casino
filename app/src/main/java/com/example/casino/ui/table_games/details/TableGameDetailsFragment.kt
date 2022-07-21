package com.example.casino.ui.table_games.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.casino.R
import com.example.casino.data.business_objects.TableGame
import kotlinx.android.synthetic.main.fragment_table_game_details.*
import kotlinx.android.synthetic.main.fragment_table_game_details.close

class TableGameDetailsFragment : Fragment(R.layout.fragment_table_game_details) {

    companion object {
        private const val ARG_TABLE_GAME_NAME = "ARG_TABLE_GAME_NAME"
        private const val ARG_TABLE_GAME_DESCRIPTION = "ARG_TABLE_GAME_DESCRIPTION"

        fun args(tableGame: TableGame): Bundle {
            return Bundle().apply {
                putString(ARG_TABLE_GAME_NAME, tableGame.name)
                putString(ARG_TABLE_GAME_DESCRIPTION, tableGame.description)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(ARG_TABLE_GAME_NAME)?.let {
            name.text = it
        }
        arguments?.getString(ARG_TABLE_GAME_DESCRIPTION)?.let {
            description.text = it
        }
        close.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}