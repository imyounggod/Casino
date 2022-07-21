package com.example.casino.ui.slots.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.casino.R
import com.example.casino.data.business_objects.Slot
import kotlinx.android.synthetic.main.fragment_slot_details.*
import kotlinx.android.synthetic.main.fragment_slot_details.close
import kotlinx.android.synthetic.main.fragment_slots.*

class SlotDetailsFragment : Fragment(R.layout.fragment_slot_details) {

    companion object {
        private const val ARG_SLOT_NAME = "ARG_SLOT_NAME"
        private const val ARG_SLOT_DESCRIPTION = "ARG_SLOT_DESCRIPTION"

        fun args(slot: Slot): Bundle {
            return Bundle().apply {
                putString(ARG_SLOT_NAME, slot.name)
                putString(ARG_SLOT_DESCRIPTION, slot.description)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(ARG_SLOT_NAME)?.let {
            name.text = it
        }
        arguments?.getString(ARG_SLOT_DESCRIPTION)?.let {
            description.text = it
        }
        close.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}