package com.example.casino.ui.roulettes.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.casino.R
import com.example.casino.data.business_objects.Roulette
import com.example.casino.utils.AssetReaderUtils
import kotlinx.android.synthetic.main.fragment_roulette_details.*
import kotlinx.android.synthetic.main.fragment_roulette_details.close

class RouletteDetailsFragment : Fragment(R.layout.fragment_roulette_details) {

    companion object {
        private const val ARG_ROULETTE_NAME = "ARG_ROULETTE_NAME"
        private const val ARG_ROULETTE_DESCRIPTION = "ARG_ROULETTE_DESCRIPTION"

        fun args(roulette: Roulette): Bundle {
            return Bundle().apply {
                putString(ARG_ROULETTE_NAME, roulette.name)
                putString(ARG_ROULETTE_DESCRIPTION, roulette.description)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(ARG_ROULETTE_NAME)?.let {
            name.text = it
        }
        arguments?.getString(ARG_ROULETTE_DESCRIPTION)?.let {
            description.text = it
        }
        close.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}