package com.shibuyaxpress.trinity_player.fragments


import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.shibuyaxpress.trinity_player.R
import com.shibuyaxpress.trinity_player.Utils.PermissionUtil
import com.shibuyaxpress.trinity_player.adapters.SongAdapter
import com.shibuyaxpress.trinity_player.models.AuxSong
import kotlinx.android.synthetic.main.fragment_songs.*

class SongsFragment : Fragment() {

    companion object{
        var permissionGranted: Boolean = false
    }

    private var songList: ArrayList<AuxSong> = ArrayList()
    private val artworkUri: Uri = Uri.parse("content://media/external/audio/albumart")
    private var songRecyclerView: RecyclerView? = null
    private var songAdapter: SongAdapter? = null
    private var parentView:View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_songs, container, false)
        songRecyclerView = parentView!!.findViewById(R.id.recyclerViewSong)
        setUpAdapter()
        //if (permissionGranted){
            getSongList()
        //}
        return parentView
    }

    private fun setUpAdapter(){
        songAdapter = SongAdapter(activity!!.applicationContext, songList)
        songRecyclerView!!.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        songRecyclerView!!.itemAnimator = DefaultItemAnimator()
        songRecyclerView!!.addItemDecoration(DividerItemDecoration(songRecyclerView!!.context, DividerItemDecoration.VERTICAL))
        songRecyclerView!!.adapter = songAdapter
    }

    private fun getSongList(){
        val contentResolver: ContentResolver = activity!!.contentResolver
        val musicUri: Uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val musicCursor: Cursor? = contentResolver.query(musicUri, null, null, null, null)
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns of each file
            val titleCol = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
            val idCol  = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
            val albumIdCol = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM_ID)
            val artistCol = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST)
            val songLinkCol = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA)
            do {
                val thisId = musicCursor.getLong(idCol)
                val thisTitle = musicCursor.getString(titleCol)
                val thisAlbumID = musicCursor.getLong(albumIdCol)
                val imageAlbum = ContentUris.withAppendedId(artworkUri, thisAlbumID)
                val thisArtist = musicCursor.getString(artistCol)
                val thisSongLink = Uri.parse(musicCursor.getString(songLinkCol))
                songList.add(AuxSong(thisId, thisTitle, thisArtist, imageAlbum.toString(), thisSongLink.toString()))
            } while(musicCursor.moveToNext())
        }
        musicCursor?.close()
        //Sort music alphabetically
        songList.sortBy { it.title }
        songAdapter!!.notifyDataSetChanged()
        Toast.makeText(
            activity!!,
            songList.size.toString() + "Songs Found!!!",
            Toast.LENGTH_SHORT)
            .show()
    }
    /*
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }*/

}
