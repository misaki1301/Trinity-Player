package com.shibuyaxpress.trinity_player.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shibuyaxpress.trinity_player.R
import com.shibuyaxpress.trinity_player.holders.AlbumHolder
import com.shibuyaxpress.trinity_player.models.Album
import com.shibuyaxpress.trinity_player.utils.GlideApp

class AlbumAdapter(context: Context, albumList:List<Album>) : RecyclerView.Adapter<AlbumHolder>() {

    private var albumList: List<Album>? = ArrayList()
    private var context: Context? = null

    init {
        this.context = context
        this.albumList = albumList
    }

    fun setAlbumList(list:ArrayList<Album>) {
        this.albumList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false)
        return AlbumHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if (albumList!!.isNotEmpty()){
            albumList!!.size
        }else{
            0
        }
    }

    override fun onBindViewHolder(holder: AlbumHolder, position: Int) {
        val album = albumList?.get(position)
        holder.title!!.text = album!!.name
        holder.artist!!.text = album.artist_id.toString()
        GlideApp.with(context!!).load(album.imageCover)
            .error(R.drawable.placeholder_song).centerCrop().into(holder.imageAlbum!!)
    }

}