package com.shibuyaxpress.trinity_player.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shibuyaxpress.trinity_player.R
import com.shibuyaxpress.trinity_player.models.Song
import com.shibuyaxpress.trinity_player.utils.OnRecyclerItemClickListener


class SongHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
    var title: TextView? = null
    var imageAlbum: ImageView? = null
    var artist: TextView? = null
    var card: CardView? = null
    init {
        card = itemView.findViewById(R.id.cardViewSong)
        title = itemView.findViewById(R.id.titleTextView)
        imageAlbum = itemView.findViewById(R.id.albumImageView)
        artist = itemView.findViewById(R.id.artistNameTextView)
    }
    fun bind(song: Song, clickListener: OnRecyclerItemClickListener, position:Int){
        title!!.text = song.title
        artist!!.text = song.artist.name
        Glide.with(itemView).load(song.imageCover)
            .placeholder(R.drawable.placeholder_song)
            .error(R.drawable.placeholder_song).centerCrop().into(imageAlbum!!)
        card!!.setOnClickListener {
            clickListener.onItemClicked(song, position, itemView)
        }
    }
}