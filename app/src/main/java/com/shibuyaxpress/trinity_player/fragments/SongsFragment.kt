package com.shibuyaxpress.trinity_player.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.shibuyaxpress.trinity_player.activities.MenuActivity
import com.shibuyaxpress.trinity_player.R
import com.shibuyaxpress.trinity_player.adapters.SongAdapter
import com.shibuyaxpress.trinity_player.models.AuxSong

class SongsFragment : Fragment() {

    companion object{
        var permissionGranted: Boolean = false
        var songList: ArrayList<AuxSong> = ArrayList()
        var songRecyclerView: RecyclerView? = null
        var songAdapter: SongAdapter? = null
        set(value) {
            songAdapter?.notifyDataSetChanged()
            field = value
        }
    }
    var songAdapter: SongAdapter? = null
    var parentView:View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_songs, container, false)
        songRecyclerView = parentView!!.findViewById(R.id.recyclerViewSong)
        setUpAdapter()
        MenuActivity.setSongList(songList)
        return parentView
    }

    private fun setUpAdapter(){
        songAdapter = SongAdapter(activity!!.applicationContext, songList)
        songRecyclerView!!.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        songRecyclerView!!.itemAnimator = DefaultItemAnimator()
        songRecyclerView!!.addItemDecoration(DividerItemDecoration(songRecyclerView!!.context, DividerItemDecoration.VERTICAL))
        songRecyclerView!!.adapter = songAdapter
    }

}
