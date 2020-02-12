package com.shibuyaxpress.trinity_player.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import com.shibuyaxpress.trinity_player.activities.MainActivity
import com.shibuyaxpress.trinity_player.R
import com.shibuyaxpress.trinity_player.adapters.SongAdapter
import com.shibuyaxpress.trinity_player.database.AppDatabase
import com.shibuyaxpress.trinity_player.models.Song
import com.shibuyaxpress.trinity_player.services.MusicService
import com.shibuyaxpress.trinity_player.utils.OnRecyclerItemClickListener
import kotlinx.coroutines.*

class SongsFragment : Fragment(), OnRecyclerItemClickListener {

    companion object{
        var permissionGranted: Boolean = false
    }
    private lateinit var songRecyclerView: RecyclerView
    private lateinit var songAdapter: SongAdapter
    private var parentView:View? = null
    private lateinit var db: AppDatabase
    private lateinit var songList: List<Song>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (parentView == null) {
            // Inflate the layout for this fragment
            parentView = inflater.inflate(R.layout.fragment_songs, container, false)
            songRecyclerView = parentView!!.findViewById(R.id.recyclerViewSong)
            db = AppDatabase(activity!!.applicationContext)
            setUpAdapter()
            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    getSongsFromDB()
                }
                updateUI()
            }
        }
        return parentView
    }

    //UI JOB
    private fun setUpAdapter(){
        songAdapter = SongAdapter(activity!!.applicationContext,this)
        songRecyclerView.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        songRecyclerView.itemAnimator = DefaultItemAnimator()
        songRecyclerView.addItemDecoration(DividerItemDecoration(songRecyclerView.context,
            DividerItemDecoration.VERTICAL))
        songRecyclerView.adapter = songAdapter
    }

    private suspend fun getSongsFromDB() {
        songList = db.songDao().getSongList()
    }

    private fun updateUI() {
        songAdapter.setSongList(songList)
        MusicService().switchSongList(songList)
        songAdapter.notifyDataSetChanged()
    }

    override fun onItemClicked(item: Any, position: Int, view: View) {
        val song = item as Song
        Log.d(SongsFragment::class.simpleName, "Now Playing by user: ${song.title} by " +
                "${song.artist.name}")
        MainActivity.songPicked(position)
    }

}
