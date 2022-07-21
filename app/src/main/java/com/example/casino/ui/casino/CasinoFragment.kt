package com.example.casino.ui.casino

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.casino.R
import com.example.casino.ui.view_extensions.addOnPageChangedListener
import com.example.casino.ui.views.BannersAdapter
import kotlinx.android.synthetic.main.fragment_casino.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class CasinoFragment : Fragment(R.layout.fragment_casino) {

    private val vm: CasinoViewModel by viewModel()

    val adapter: BannersAdapter = BannersAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        setUpAdapter()
        slots.setOnClickListener {
            findNavController().navigate(R.id.action_casinoFragment_to_slotsFragment)
        }
        table_games.setOnClickListener {
            findNavController().navigate(R.id.action_casinoFragment_to_tableGamesFragment)
        }
        roulette.setOnClickListener {
            findNavController().navigate(R.id.action_casinoFragment_to_roulettesFragment)
        }
        adapter.items = vm.getBanners()
    }

    private fun setUpAdapter() {
        banner.adapter = adapter
        banner.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(banner)
        banner.addOnPageChangedListener { position ->
            when (position) {
                0 -> {
                    slider_page_1.setImageResource(R.drawable.ic_slider_tab_selected)
                    slider_page_2.setImageResource(R.drawable.ic_slider_tab_not_selected)
                    slider_page_3.setImageResource(R.drawable.ic_slider_tab_not_selected)
                }
                1 -> {
                    slider_page_1.setImageResource(R.drawable.ic_slider_tab_not_selected)
                    slider_page_2.setImageResource(R.drawable.ic_slider_tab_selected)
                    slider_page_3.setImageResource(R.drawable.ic_slider_tab_not_selected)
                }
                2 -> {
                    slider_page_1.setImageResource(R.drawable.ic_slider_tab_not_selected)
                    slider_page_2.setImageResource(R.drawable.ic_slider_tab_not_selected)
                    slider_page_3.setImageResource(R.drawable.ic_slider_tab_selected)
                }
            }
        }
    }
}