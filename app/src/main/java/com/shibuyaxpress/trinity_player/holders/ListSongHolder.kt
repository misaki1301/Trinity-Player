package com.shibuyaxpress.trinity_player.holders

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.shibuyaxpress.trinity_player.R
import com.shibuyaxpress.trinity_player.models.Song
import com.shibuyaxpress.trinity_player.utils.OnRecyclerItemClickListener


class ListSongHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
    var title: TextView? = null
    //var imageAlbum: ImageView? = null
    var artist: TextView? = null
    var card: CardView? = null
    var position: TextView? = null

    init {
        card = itemView.findViewById(R.id.cardViewSongAlbum)
        title = itemView.findViewById(R.id.titleTextView)
        //imageAlbum = itemView.findViewById(R.id.albumImageView)
        artist = itemView.findViewById(R.id.artistNameTextView)
        position = itemView.findViewById(R.id.positionTextView)
    }

    fun bind(song: Song, clickListener: OnRecyclerItemClickListener, position:Int){
        title!!.text = song.title
        artist!!.text = song.artist.name
        this.position!!.text = "${position + 1}"
        /*Glide.with(itemView).load(song.imageCover)
            .placeholder(R.drawable.placeholder_song)
            .error(R.drawable.placeholder_song).centerCrop().into(imageAlbum!!)*/
        card!!.setOnClickListener {
            clickListener.onItemClicked(song, position, itemView)
        }
    }
}