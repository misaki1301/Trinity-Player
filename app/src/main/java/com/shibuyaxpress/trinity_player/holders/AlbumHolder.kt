package com.shibuyaxpress.trinity_player.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.shibuyaxpress.trinity_player.R
import com.shibuyaxpress.trinity_player.models.Album
import com.shibuyaxpress.trinity_player.utils.GlideApp
import com.shibuyaxpress.trinity_player.utils.OnRecyclerItemClickListener

class AlbumHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var title: TextView? = null
    var imageAlbum: ImageView? = null
    var artist: TextView? = null
    var card: CardView? = null
    init {
        title = itemView.findViewById(R.id.textViewAlbum)
        imageAlbum = itemView.findViewById(R.id.albumImageView)
        artist = itemView.findViewById(R.id.textViewAlbumArtistName)
        card = itemView.findViewById(R.id.cardViewAlbum)
    }

    fun bind(album: Album, clickListener: OnRecyclerItemClickListener, position: Int){
        title!!.text = album.name
        artist!!.text = album.artist.name
        GlideApp.with(itemView).load(album.imageCover)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.placeholder_song)
            .error(R.drawable.placeholder_song)
            .centerCrop()
            .into(imageAlbum!!)
        card!!.setOnClickListener {
            clickListener.onItemClicked(album, position, itemView)
        }
    }
}