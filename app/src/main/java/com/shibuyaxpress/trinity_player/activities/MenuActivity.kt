package com.shibuyaxpress.trinity_player.activities

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.shibuyaxpress.trinity_player.R
import com.shibuyaxpress.trinity_player.database.AppDatabase
import com.shibuyaxpress.trinity_player.fragments.AlbumFragment
import com.shibuyaxpress.trinity_player.fragments.HomeFragment
import com.shibuyaxpress.trinity_player.fragments.SongsFragment
import com.shibuyaxpress.trinity_player.models.Album
import com.shibuyaxpress.trinity_player.models.Artist
import com.shibuyaxpress.trinity_player.models.AuxSong
import com.shibuyaxpress.trinity_player.models.Song
import com.shibuyaxpress.trinity_player.services.MusicService
import com.shibuyaxpress.trinity_player.services.MusicService.MusicBinder
import com.shibuyaxpress.trinity_player.utils.PermissionUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val STORAGE_PERMISSION_ID: Int = 0
class MenuActivity : AppCompatActivity() {

    private val artworkUri: Uri = Uri.parse("content://media/external/audio/albumart")
    private var fragmentManager: FragmentManager? = null
    private var homeFragment = HomeFragment()
    private var songsFragment = SongsFragment()
    private var albumFragment = AlbumFragment()
    private var activeFragment: Fragment? = homeFragment
    private lateinit var db: AppDatabase

    companion object {
        var musicService: MusicService? = null
        var playIntent: Intent? = null
        var musicBound: Boolean = false
        var songList = ArrayList<Song>()

        fun setSongList(list: List<Song>) {
            songList = list as ArrayList<Song>
            Log.d("song list size", songList.size.toString())
        }

        fun songPicked(position: Int) {
            //Log.d("???",view.tag.toString())
            musicService?.setSongPosition(position)
            musicService?.playSong()
        }

        var TAG_FRAGMENT_ONE = "fragment_home"
        var TAG_FRAGMENT_TWO = "fragment_songs"
        var TAG_FRAGMENT_THREE = "fragment_albums"
    }


    private val onNavigationItemSelectedListener = BottomNavigationView
        .OnNavigationItemSelectedListener { item ->
        showSelectedFragment(item.itemId)
    }

    private fun showSelectedFragment(itemId: Int): Boolean {
        when (itemId) {
            R.id.navigation_home -> {
                fragmentManager!!.beginTransaction()
                    .hide(activeFragment!!).show(homeFragment).commit()
                activeFragment = homeFragment
                return true
            }
            R.id.navigation_songs -> {
                fragmentManager!!.beginTransaction()
                    .hide(activeFragment!!).show(songsFragment).commit()
                activeFragment = songsFragment
                return true
            }
            R.id.navigation_albums -> {
                fragmentManager!!.beginTransaction()
                    .hide(activeFragment!!).show(albumFragment).commit()
                activeFragment = albumFragment
                return true
            }
        }
        return false
    }
    private fun prepareFragments() {
        fragmentManager!!.beginTransaction()
            .add(R.id.contentFrame, songsFragment, "2")
            .hide(songsFragment)
            .commit()
        fragmentManager!!.beginTransaction()
            .add(R.id.contentFrame, albumFragment, "3")
            .hide(albumFragment)
            .commit()
        fragmentManager!!.beginTransaction()
            .add(R.id.contentFrame, homeFragment, "1")
            .commit()
    }

    override fun onStart() {
        super.onStart()
        if (playIntent == null) {
            playIntent = Intent(this, MusicService::class.java)
            bindService(playIntent,musicConnection, Context.BIND_AUTO_CREATE)
            startService(playIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = AppDatabase(this)
        fragmentManager = supportFragmentManager
        prepareFragments()
        init()
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    private var musicConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicBinder
            //get service
            musicService = binder.getService()
            //past list of song to play
            musicService!!.setSongList(
                songList
            )
            musicBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicBound = false
        }
    }

    private fun init() {
        if (!checkStorePermission(STORAGE_PERMISSION_ID)) {
            showRequestPermission(STORAGE_PERMISSION_ID)
        }
        //mMediaPlayer = MediaPlayer()
        //utils = TimeUtil()
    }



    override fun onDestroy() {
        super.onDestroy()
        stopService(playIntent)
       unbindService(musicConnection)
        musicService = null

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
                    SongsFragment.permissionGranted = true
                    return
                }
            }
        }
    }

    private fun getSongList(){
        var artistList: ArrayList<Artist> = ArrayList()
        var albumList: ArrayList<Album> = ArrayList()

        val contentResolver: ContentResolver = applicationContext.contentResolver
        val musicUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val musicCursor: Cursor? = contentResolver
            .query(musicUri, null, null, null, null)
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns of each file
            val titleCol = musicCursor
                .getColumnIndex(MediaStore.Audio.Media.TITLE)
            val idCol  = musicCursor
                .getColumnIndex(MediaStore.Audio.Media._ID)
            val albumIdCol = musicCursor
                .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val artistCol = musicCursor
                .getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val artistIDCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)
            val songLinkCol = musicCursor
                .getColumnIndex(MediaStore.Audio.Media.DATA)
            //val lengthCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val composerCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER)
            var filePathCol = musicCursor
                .getColumnIndex(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString())
            //val audioTypeCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.CONTENT_TYPE)
            val albumIDCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY)

            val albumNameCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            do {
                val thisAlbumKEY = musicCursor.getLong(albumIDCol)
                val thisId = musicCursor.getLong(idCol)
                val thisTitle = musicCursor.getString(titleCol)
                val thisAlbumID = musicCursor.getLong(albumIdCol)
                val imageAlbum = ContentUris.withAppendedId(artworkUri, thisAlbumID)
                val thisArtist = musicCursor.getString(artistCol)
                val thisSongLink = Uri.parse(musicCursor.getString(songLinkCol))
                val thisArtistID = musicCursor.getLong(artistIDCol)
                val thisAlbumName = musicCursor.getString(albumNameCol)
                //populate songList with music data
                Log.d("MusicTarget","\n AlbumID $thisAlbumID name $thisAlbumName, \n " +
                        "ArtistID $thisArtistID Artistname: $thisArtist " +
                        "\n SongName: $thisTitle SongID:$thisId")
                /*songList
                    .add(
                        AuxSong(thisId, thisTitle, thisArtist, imageAlbum.toString(),
                            thisSongLink.toString()))*/
                val currentArtist = Artist(thisArtistID, thisArtist,"")
                val currentAlbum = Album(thisAlbumID, thisAlbumName, imageAlbum.toString(),
                    thisArtistID, currentArtist)
                songList
                    .add(
                        Song(thisId, thisTitle, thisAlbumID, thisArtistID, imageAlbum.toString(),
                            thisSongLink.toString(),currentArtist, currentAlbum))
                artistList
                    .add(currentArtist)
                albumList
                    .add(currentAlbum)

            } while(musicCursor.moveToNext())
        }
        artistList = artistList.distinct() as ArrayList<Artist>
        albumList = albumList.distinct() as ArrayList<Album>
        //add music to db
        //1st add artist
        //2nd then add album attaching artist
        //3rd link artist and album to each song
        GlobalScope.launch {
            db.artistDao().insertAllArtists(artistList.toList())
            db.albumDao().insertAll(albumList)
            db.songDao().insertAll(songList.toList())
        }
        musicCursor?.close()
        //Sort music alphabetically
        songList.sortBy { it.title }
        Toast.makeText(
            applicationContext!!,
            songList.size.toString() + "Songs Found!!!",
            Toast.LENGTH_SHORT)
            .show()
    }
}
