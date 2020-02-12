package com.shibuyaxpress.trinity_player.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shibuyaxpress.trinity_player.activities.MainActivity
import com.shibuyaxpress.trinity_player.R
import com.shibuyaxpress.trinity_player.holders.SongHolder
import com.shibuyaxpress.trinity_player.models.Song
import com.shibuyaxpress.trinity_player.utils.OnRecyclerItemClickListener

const val TYPE_LIST_COVER = 1
const val TYPE_LIST_NO_COVER = 0

class SongAdapter(context:Context, val itemClickListener: OnRecyclerItemClickListener): RecyclerView.Adapter<SongHolder>(), Filterable {

    private var songList: List<Song>? = ArrayList()
    private var context: Context? = null


    init {
        this.context = context
    }

    fun setSongList(list:List<Song>){
        this.songList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        var itemview: View? = null
        when(viewType){
            TYPE_LIST_COVER -> {
                itemview = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
            }

            TYPE_LIST_NO_COVER -> {
                itemview = LayoutInflater.from(parent.context).inflate(R.layout.item_song_on_album_detail, parent,false)
            }
        }
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongHolder(itemView)
    }

    override fun getItemCount(): Int {
        return songList!!.size
    }

    override fun onBindViewHolder(holder: SongHolder, position: Int) {
        val song = songList!![position]
        holder.bind(song, itemClickListener, position)

    }

    override fun getItemViewType(position: Int): Int {
        if (isOnAlbumDetailed){
            return TYPE_LIST_NO_COVER
        } else {
            return TYPE_LIST_COVER
        }
    }

    override fun getFilter(): Filter {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}