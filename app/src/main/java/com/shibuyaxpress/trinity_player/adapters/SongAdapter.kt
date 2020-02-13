package com.shibuyaxpress.trinity_player.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.shibuyaxpress.trinity_player.R
import com.shibuyaxpress.trinity_player.holders.ListSongHolder
import com.shibuyaxpress.trinity_player.holders.SongHolder
import com.shibuyaxpress.trinity_player.models.Song
import com.shibuyaxpress.trinity_player.utils.OnRecyclerItemClickListener

const val TYPE_LIST_COVER = 1
const val TYPE_LIST_NO_COVER = 0

class SongAdapter(context:Context, val itemClickListener: OnRecyclerItemClickListener): RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var songList: List<Song>? = ArrayList()
    private var context: Context? = null
    private var isOnAlbumDetailed: Boolean = false

    init {
        this.context = context
    }

    fun setSongList(list:List<Song>){
        this.songList = list
    }
    fun setIsOnAlbumDetail(isOn: Boolean) {
        this.isOnAlbumDetailed = isOn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        var view: View? = null
        return when (viewType) {
            TYPE_LIST_COVER -> {
                view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
                return SongHolder(view)
            }

            TYPE_LIST_NO_COVER -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_song_on_album_detail, parent, false)
                return ListSongHolder(view)
            }
            else -> {
                view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
                return SongHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return songList!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val song = songList!![position]
        when(getItemViewType(position)){
            TYPE_LIST_NO_COVER -> {
                holder as ListSongHolder
                holder.bind(song, itemClickListener, position)
            }
            TYPE_LIST_COVER -> {
                holder as SongHolder
                holder.bind(song, itemClickListener, position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isOnAlbumDetailed){
            TYPE_LIST_NO_COVER
        } else {
            TYPE_LIST_COVER
        }
    }

    override fun getFilter(): Filter {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}