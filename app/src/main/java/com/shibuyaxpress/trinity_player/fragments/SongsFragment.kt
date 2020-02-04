package com.shibuyaxpress.trinity_player.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.shibuyaxpress.trinity_player.activities.MainActivity
import com.shibuyaxpress.trinity_player.R
import com.shibuyaxpress.trinity_player.adapters.SongAdapter
import com.shibuyaxpress.trinity_player.database.AppDatabase
import com.shibuyaxpress.trinity_player.models.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SongsFragment : Fragment() {

    companion object{
        var permissionGranted: Boolean = false
        var songList: ArrayList<Song> = ArrayList()
        var songRecyclerView: RecyclerView? = null
        var songAdapter: SongAdapter? = null
        set(value) {
            songAdapter?.notifyDataSetChanged()
            field = value
        }
    }
    private var songAdapter: SongAdapter? = null
    private var parentView:View? = null
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_songs, container, false)
        songRecyclerView = parentView!!.findViewById(R.id.recyclerViewSong)
        db = AppDatabase(activity!!.applicationContext)
        setUpAdapter()
        MainActivity.setSongList(songList)
        return parentView
    }

    private fun setUpAdapter(){
        songAdapter = SongAdapter(activity!!.applicationContext, songList)
        songRecyclerView!!.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        songRecyclerView!!.itemAnimator = DefaultItemAnimator()
        songRecyclerView!!.addItemDecoration(DividerItemDecoration(songRecyclerView!!.context, DividerItemDecoration.VERTICAL))
        songRecyclerView!!.adapter = songAdapter
        getSongsFromDatabase()
    }

    private fun getSongsFromDatabase() {
        GlobalScope.launch(Dispatchers.IO) {
            songAdapter!!.setSongList(db.songDao().getSongList())
            songAdapter!!.notifyDataSetChanged()
        }
    }

}
