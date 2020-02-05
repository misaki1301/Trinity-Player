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
import com.shibuyaxpress.trinity_player.services.MusicService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SongsFragment : Fragment() {

    companion object{
        var permissionGranted: Boolean = false
        /*var songList: ArrayList<Song> = ArrayList()
        var songRecyclerView: RecyclerView? = null*/
        /*set(value) {
            songAdapter?.notifyDataSetChanged()
            field = value
        }*/
    }
    private lateinit var songRecyclerView: RecyclerView
    private lateinit var songAdapter: SongAdapter
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
        return parentView
    }

    private fun setUpAdapter(){
        songAdapter = SongAdapter(activity!!.applicationContext)
        songRecyclerView.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        songRecyclerView.itemAnimator = DefaultItemAnimator()
        songRecyclerView.addItemDecoration(DividerItemDecoration(songRecyclerView.context,
            DividerItemDecoration.VERTICAL))
        songRecyclerView.adapter = songAdapter
        getSongsFromDatabase()
    }

    private fun getSongsFromDatabase() {
        var songList: List<Song>
        GlobalScope.launch(Dispatchers.IO) {
            songList = db.songDao().getSongList()
            songAdapter.setSongList(songList)
            MusicService.setSongList(songList)
        }
        songAdapter.notifyDataSetChanged()
    }

}
