package com.example.casino.ui.sport

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.casino.R
import com.example.casino.data.business_objects.Sport
import com.example.casino.ui.VerticalSpaceBetweenItemsItemDecoration
import com.example.casino.ui.view_extensions.addOnPageChangedListener
import com.example.casino.ui.view_extensions.setVisible
import com.example.casino.ui.views.BannersAdapter
import com.example.casino.utils.MetricUtils
import kotlinx.android.synthetic.main.fragment_sport.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SportFragment : Fragment(R.layout.fragment_sport) {

    private val vm: SportViewModel by viewModel()

    private val adapterBanners: BannersAdapter = BannersAdapter()
    private val adapterMatches: MatchesAdapter = MatchesAdapter()
    private lateinit var filterAdapter: SportFilterAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        setUpBannersAdapter()
        adapterBanners.items = vm.getBanners()
        setUpFilterAdapter()
        setUpMatchesAdapter()
        setUpObservers()
    }

    private fun setUpBannersAdapter() {
        banner.adapter = adapterBanners
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

    private fun setUpFilterAdapter() {
        sport_filter.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        filterAdapter = SportFilterAdapter(Sport.SOCCER) { position, sport ->
            (sport_filter.layoutManager as LinearLayoutManager).scrollToPosition(position)
            vm.loadMatches(sport)
        }
        sport_filter.adapter = filterAdapter
    }

    private fun setUpMatchesAdapter() {
        matches.adapter = adapterMatches
        matches.addItemDecoration(
            VerticalSpaceBetweenItemsItemDecoration(
                MetricUtils.convertDpToPixel(14F).toInt()
            )
        )
        matches.layoutManager = LinearLayoutManager(context)
    }

    private fun setUpObservers() {
        vm.selectedSportMatchesIsLoading.observe(viewLifecycleOwner, { isLoading ->
            loader.setVisible(isLoading)
        })
        vm.selectedSportMatches.observe(viewLifecycleOwner, { matches ->
            adapterMatches.items = matches.orEmpty()
            empty_state.setVisible(matches != null && matches.isEmpty())
            matches?.forEach {
                Log.d(
                    "SportFragment",
                    "matchId: ${it.matchId}, " +
                            "leagueName: ${it.leagueName}, " +
                            "date: ${it.date}, " +
                            "team1: ${it.firstTeam.name} - ${it.firstTeam.score}, " +
                            "team2: ${it.secondTeam.name} - ${it.secondTeam.score}\n"
                )
            }
        })
    }
}