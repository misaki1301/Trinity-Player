package com.shibuyaxpress.trinity_player.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shibuyaxpress.trinity_player.MainActivity
import com.shibuyaxpress.trinity_player.R
import com.shibuyaxpress.trinity_player.SongAdapterListener
import com.shibuyaxpress.trinity_player.holders.SongHolder
import com.shibuyaxpress.trinity_player.models.AuxSong

class SongAdapter(context:Context, songList:List<AuxSong>): RecyclerView.Adapter<SongHolder>(), Filterable {

    private var songList: List<AuxSong>? = ArrayList()
    private var songListFiltered: List<AuxSong>? = ArrayList()
    private var context: Context? = null
    //private var listener: SongAdapterListener

    init {
        this.context = context
        this.songList = songList
        //this.listener = listener
        this.songListFiltered = songList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongHolder(itemView)
    }

    override fun getItemCount(): Int {
        return songListFiltered!!.size
    }

    override fun onBindViewHolder(holder: SongHolder, position: Int) {
        val song = songListFiltered?.get(position)
        holder.title!!.text = song!!.title
        holder.artist!!.text = song.artist
        Glide.with(context!!).load(song.thumbnail)
            .placeholder(R.drawable.placeholder_song)
            .error(R.drawable.placeholder_song).centerCrop().into(holder.imageAlbum!!)

        holder.itemView.setOnClickListener {
            MainActivity.songPicked(position)
        }

    }

    override fun getFilter(): Filter {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}