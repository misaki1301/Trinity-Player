package com.shibuyaxpress.trinity_player.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shibuyaxpress.trinity_player.R
import com.shibuyaxpress.trinity_player.activities.MainActivity
import com.shibuyaxpress.trinity_player.adapters.SongAdapter
import com.shibuyaxpress.trinity_player.database.AppDatabase
import com.shibuyaxpress.trinity_player.fragments.AlbumDetailFragmentArgs.Companion.fromBundle
import com.shibuyaxpress.trinity_player.models.Album
import com.shibuyaxpress.trinity_player.models.Song
import com.shibuyaxpress.trinity_player.services.MusicService
import com.shibuyaxpress.trinity_player.utils.GlideApp
import com.shibuyaxpress.trinity_player.utils.OnRecyclerItemClickListener
import kotlinx.android.synthetic.main.fragment_album_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass.
 */
class AlbumDetailFragment : Fragment(), OnRecyclerItemClickListener {


    val album by lazy {
        fromBundle(arguments!!).albumSelected
    }
    private var parentView: View? = null
    private lateinit var songAdapter: SongAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var songList: List<Song>
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (parentView == null) {
            Log.d(AlbumDetailFragment::class.java.simpleName, "the album $album")
            db = AppDatabase(activity!!.applicationContext)
            parentView = inflater.inflate(R.layout.fragment_album_detail, container, false)
            GlobalScope.launch(Dispatchers.Main) {
                setUpAdapter()
                updateUI(album, parentView!!)
                withContext(Dispatchers.IO) {
                    getRelatedSongsFromDB()
                }
                endTouches()
            }
        }
        // Inflate the layout for this fragment
        return parentView
    }
    private suspend fun getRelatedSongsFromDB() {
        songList = db.songDao().getSongsFromAlbum(album.id)
    }

    private fun setUpAdapter() {
        songAdapter = SongAdapter(activity!!.applicationContext, this)
        songAdapter.setIsOnAlbumDetail(true)
        albumDetailRecycler.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        albumDetailRecycler.itemAnimator = DefaultItemAnimator()
        albumDetailRecycler
            .addItemDecoration(DividerItemDecoration(albumDetailRecycler.context,
                DividerItemDecoration.VERTICAL))
        albumDetailRecycler.adapter = songAdapter
    }

    private fun updateUI(album: Album, parentView:View) {
        GlideApp.with(parentView).load(album.imageCover).into(imageAlbumDetail)
        albumDetailArtirst.text = album.artist.name
        albumDetailName.text = album.name
    }

    private fun endTouches() {
        songAdapter.setSongList(songList)
        //fix this piece of crap to avoid repeat in each click event
        MainActivity.musicService!!.switchSongList(songList)
        songAdapter.notifyDataSetChanged()
    }

    override fun onItemClicked(item: Any, position: Int, view: View) {
        val song = item as Song
        Log.d(AlbumDetailFragment::class.java.simpleName, "Now playing by user: $song")
        MainActivity.songPicked(position)
        //how to call a method from main activity on fragments
        //apparently this kill the music reproduction for some reason
        //(activity as MainActivity).setMusicComponents()
    }




}
