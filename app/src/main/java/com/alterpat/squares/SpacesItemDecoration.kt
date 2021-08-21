package com.alterpat.squares

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class SpacesItemDecoration(private val space: Int, private val count : Int) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = space
        outRect.right = space
        outRect.bottom = space
        outRect.top = space


        /*
        System.out.println(parent.getChildLayoutPosition(view) % count)
        // Add double left margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) % count == 0) {
            outRect.left = space
            outRect.right = space
        }


        else{
            outRect.right = space
        }
         */

        }
}