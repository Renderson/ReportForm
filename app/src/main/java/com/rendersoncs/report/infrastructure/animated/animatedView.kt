package com.rendersoncs.report.infrastructure.animated

import android.transition.Slide
import android.transition.Transition
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING


fun animatedView(recyclerView: RecyclerView, view: View) {
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0) {
                view.visibility = View.VISIBLE
                val transition: Transition = Slide(Gravity.BOTTOM)
                transition.duration = 500
                transition.addTarget(view)
            } else if (dy < 0) {
                view.visibility = View.VISIBLE
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
