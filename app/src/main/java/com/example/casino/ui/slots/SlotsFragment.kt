package com.example.casino.ui.slots

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.casino.R
import com.example.casino.ui.TwoColumnListSpaceItemDecoration
import com.example.casino.ui.slots.details.SlotDetailsFragment
import com.example.casino.utils.MetricUtils
import kotlinx.android.synthetic.main.fragment_slots.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SlotsFragment : Fragment(R.layout.fragment_slots) {

    private val vm: SlotsViewModel by viewModel()

    private val adapter: SlotsAdapter = SlotsAdapter {
        findNavController().navigate(
            R.id.action_slotsFragment_to_slotDetailsFragment,
            SlotDetailsFragment.args(it)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val anim: Animation = AnimationUtils.loadAnimation(activity, nextAnim)
        anim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationRepeat(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                if (enter) {
                    vm.loadSlots()
                }
            }
        })
        return anim

    }

    private fun initViews() {
        setUpAdapter()
        search.addTextChangedListener {
            adapter.searchString = search.text.toString()
        }
        close.setOnClickListener {
            findNavController().navigateUp()
        }
        setUpObservers()
    }

    private fun setUpAdapter() {
        slots.adapter = adapter
        slots.addItemDecoration(TwoColumnListSpaceItemDecoration(MetricUtils.convertDpToPixel(20F).toInt()))
        slots.layoutManager = GridLayoutManager(context, 2)
    }

    private fun setUpObservers() {
        vm.slots.observe(viewLifecycleOwner, {
            adapter.items = it
        })
    }
}