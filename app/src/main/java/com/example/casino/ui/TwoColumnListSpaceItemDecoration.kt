package com.example.casino.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.casino.utils.MetricUtils

class TwoColumnListSpaceItemDecoration(private val spaceBetweenCards: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val currItemPosition = parent.getChildAdapterPosition(view)
        val itemsCount = parent.adapter!!.itemCount
        val lastItemIndexWithBottomSpace: Int
        if ((currItemPosition % 2) == 0) {
            outRect.right = spaceBetweenCards / 2
            lastItemIndexWithBottomSpace = itemsCount - 3
        } else {
            outRect.left = spaceBetweenCards / 2
            lastItemIndexWithBottomSpace = itemsCount - 2
        }
        if (itemsCount > 2 && currItemPosition <= lastItemIndexWithBottomSpace) {
            outRect.bottom = spaceBetweenCards
        }
    }
}