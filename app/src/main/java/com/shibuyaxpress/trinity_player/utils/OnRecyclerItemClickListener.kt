package com.shibuyaxpress.trinity_player.utils

import android.view.View

interface OnRecyclerItemClickListener {
    fun onItemClicked(item: Any, position:Int, view: View)
}