package com.rendersoncs.report.common.animated

import android.transition.Slide
import android.transition.Transition
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

fun animatedView(recyclerView: RecyclerView, view: View, fabNewItem: ExtendedFloatingActionButton) {
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0) {
                view.visibility = View.VISIBLE
                val transition: Transition = Slide(Gravity.BOTTOM)
                transition.duration = 500
                transition.addTarget(view)
                fabNewItem.shrink()
            } else if (dy < 0) {
                view.visibility = View.VISIBLE
                fabNewItem.extend()
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == SCROLL_STATE_SETTLING) {
                view.visibility = View.GONE
            }
        }
    })
}
