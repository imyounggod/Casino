package com.example.casino.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.casino.utils.MetricUtils

class VerticalSpaceBetweenItemsItemDecoration(private val spaceBetweenCards: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val currItemPosition = parent.getChildAdapterPosition(view)
        val itemsCount = parent.adapter!!.itemCount
        if (currItemPosition != itemsCount - 1) {
            outRect.bottom = spaceBetweenCards
        }
    }
}