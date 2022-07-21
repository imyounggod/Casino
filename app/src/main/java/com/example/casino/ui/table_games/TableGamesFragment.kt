package com.example.casino.ui.table_games

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
import com.example.casino.ui.table_games.details.TableGameDetailsFragment
import com.example.casino.utils.MetricUtils
import kotlinx.android.synthetic.main.fragment_table_games.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class TableGamesFragment : Fragment(R.layout.fragment_table_games) {

    private val vm: TableGamesViewModel by viewModel()

    private val adapter: TableGamesAdapter = TableGamesAdapter {
        findNavController().navigate(
            R.id.action_tableGamesFragment_to_tableGameDetailsFragment,
            TableGameDetailsFragment.args(it)
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
                    vm.loadTableGames()
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
        table_games.adapter = adapter
        table_games.addItemDecoration(
            TwoColumnListSpaceItemDecoration(
                MetricUtils.convertDpToPixel(
                    20F
                ).toInt()
            )
        )
        table_games.layoutManager = GridLayoutManager(context, 2)
    }

    private fun setUpObservers() {
        vm.tableGames.observe(viewLifecycleOwner, {
            adapter.items = it
        })
    }
}