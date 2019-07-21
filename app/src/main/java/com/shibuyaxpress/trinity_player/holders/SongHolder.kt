package com.shibuyaxpress.trinity_player.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shibuyaxpress.trinity_player.R


class SongHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
    var title: TextView? = null
    var imageAlbum: ImageView? = null
    var artist: TextView? = null
    init {
        title = itemView.findViewById(R.id.titleTextView)
        imageAlbum = itemView.findViewById(R.id.albumImageView)
        artist = itemView.findViewById(R.id.artistNameTextView)
    }
}