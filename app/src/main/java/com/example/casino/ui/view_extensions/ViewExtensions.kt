package com.example.casino.ui.view_extensions

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun View.setVisible(visible: Boolean) {
    if (visible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

fun RecyclerView.addOnPageChangedListener(listener: (pos: Int) -> Unit) {

    val layoutManager = this.layoutManager as LinearLayoutManager
    var lastPos = -1

    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val pos = layoutManager.findFirstCompletelyVisibleItemPosition()
            if (pos != -1 && pos != lastPos) {
                lastPos = pos
                listener.invoke(pos)
            }
        }
    })

}