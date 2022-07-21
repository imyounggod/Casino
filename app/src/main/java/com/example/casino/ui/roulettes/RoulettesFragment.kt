package com.example.casino.ui.roulettes

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
import com.example.casino.ui.roulettes.details.RouletteDetailsFragment
import com.example.casino.utils.MetricUtils
import kotlinx.android.synthetic.main.fragment_roulettes.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RoulettesFragment : Fragment(R.layout.fragment_roulettes) {

    private val vm: RoulettesViewModel by viewModel()

    private val adapter: RoulettesAdapter = RoulettesAdapter {
        findNavController().navigate(
            R.id.action_roulettesFragment_to_rouletteDetailsFragment,
            RouletteDetailsFragment.args(it)
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
                    vm.loadRoulettes()
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
        roulettes.adapter = adapter
        roulettes.addItemDecoration(TwoColumnListSpaceItemDecoration(MetricUtils.convertDpToPixel(20F).toInt()))
        roulettes.layoutManager = GridLayoutManager(context, 2)
    }

    private fun setUpObservers() {
        vm.roulettes.observe(viewLifecycleOwner, {
            adapter.items = it
        })
    }
}