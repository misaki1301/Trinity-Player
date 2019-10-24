package com.shibuyaxpress.trinity_player

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shibuyaxpress.trinity_player.utils.PermissionUtil
import com.shibuyaxpress.trinity_player.adapters.SongAdapter
import com.shibuyaxpress.trinity_player.models.AuxSong
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private var songList: ArrayList<AuxSong> = ArrayList()
    private val artworkUri: Uri = Uri.parse("content://media/external/audio/albumart")
    private val STORAGE_PERMISSION_ID: Int = 0
    private var songRecyclerView: RecyclerView? = null
    private var songAdapter: SongAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //init to check current permissions
        init()
        setUpAdapter()
    }

    //this will run on the fragment tho
    private fun setUpAdapter(){
        songAdapter = SongAdapter(applicationContext, songList)
        songRecyclerView!!.layoutManager = LinearLayoutManager(this)
        songRecyclerView!!.itemAnimator = DefaultItemAnimator()
        songRecyclerView!!.addItemDecoration(DividerItemDecoration(songRecyclerView!!.context, DividerItemDecoration.VERTICAL))
        songRecyclerView!!.adapter = songAdapter

    }

    private fun getSongList(){
        val contentResolver: ContentResolver = contentResolver
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
        Toast.makeText(this, songList.size.toString() + "Songs Found!!!" , Toast.LENGTH_SHORT).show()
    }

    private fun init(){
        if (!checkStorePermission(STORAGE_PERMISSION_ID)) {
            showRequestPermission(STORAGE_PERMISSION_ID)
        }
        //this will be linked on the fragment too
        songRecyclerView = findViewById(R.id.recyclerViewSong)

    }

    private fun checkStorePermission(permission: Int): Boolean {
        return if (permission == STORAGE_PERMISSION_ID) {
            PermissionUtil.checkPermissions(this, listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        } else {
            true
        }
    }

    private fun showRequestPermission(requestCode: Int) {
        val permissions: List<String> = if (requestCode == STORAGE_PERMISSION_ID) {
            listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            listOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)

        }
        PermissionUtil.requestPermissions(this, requestCode, permissions)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            for ((index, _) in permissions.withIndex()) {
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    getSongList()
                    return
                }
            }
        }
    }
}
