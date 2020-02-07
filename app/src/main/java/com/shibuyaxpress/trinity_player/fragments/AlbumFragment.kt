package com.shibuyaxpress.trinity_player.fragments


import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shibuyaxpress.trinity_player.R
import com.shibuyaxpress.trinity_player.adapters.AlbumAdapter
import com.shibuyaxpress.trinity_player.database.AppDatabase
import com.shibuyaxpress.trinity_player.models.Album
import com.shibuyaxpress.trinity_player.repository.AlbumRepository
import com.shibuyaxpress.trinity_player.utils.ItemOffsetDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumFragment : Fragment() {

    private lateinit var albumList: List<Album>
    private lateinit var albumRecyclerView: RecyclerView
    private lateinit var albumAdapter: AlbumAdapter
    private var parentView: View? = null
    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_album, container, false)
        albumRecyclerView = parentView!!.findViewById(R.id.recyclerViewAlbum)
        albumRecyclerView.addItemDecoration(
            ItemOffsetDecoration(
                activity!!.applicationContext,
                R.dimen.item_offset
            )
        )
        db = AppDatabase(activity!!.applicationContext)
        setupAdapter()
        //here comes the db load
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                getAlbumsFromDatabase()
            }
            updateUI()
        }
        return parentView
    }

    //UI Fun
    private fun setupAdapter() {
        albumAdapter = AlbumAdapter(activity!!.applicationContext, AlbumRepository().getAlbumFromRepository())
        albumRecyclerView.layoutManager = GridLayoutManager(activity!!.applicationContext, 2)
        albumRecyclerView.itemAnimator = DefaultItemAnimator()
        albumRecyclerView.adapter = albumAdapter
    }

    private suspend fun getAlbumsFromDatabase() {
       albumList = db.albumDao().getAllAlbums()
    }

    private fun updateUI() {
        albumAdapter.setAlbumList(albumList)
        albumAdapter.notifyDataSetChanged()
    }

}
