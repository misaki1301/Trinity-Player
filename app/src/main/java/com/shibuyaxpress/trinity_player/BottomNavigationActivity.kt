package com.shibuyaxpress.trinity_player

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.shibuyaxpress.trinity_player.utils.PermissionUtil
import com.shibuyaxpress.trinity_player.fragments.AlbumFragment
import com.shibuyaxpress.trinity_player.fragments.HomeFragment
import com.shibuyaxpress.trinity_player.fragments.SongsFragment
import android.widget.TextView
import android.media.MediaPlayer
import android.widget.SeekBar
import android.widget.LinearLayout
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.media.AudioManager
import android.net.Uri
import android.os.Handler
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.shibuyaxpress.trinity_player.utils.GlideApp
import com.shibuyaxpress.trinity_player.utils.TimeUtil
import kotlinx.android.synthetic.main.activity_bottom_navigation.*
import java.lang.Exception
import java.nio.file.Files.size
import com.shibuyaxpress.trinity_player.models.Song
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.os.postDelayed


class BottomNavigationActivity : AppCompatActivity(), View.OnClickListener,
    SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.iv_play -> {
                playMusic()
            }
            R.id.iv_previous -> {
                playPreviousSong()
            }
            R.id.iv_next -> {
                playNextSong()
            }
        }
    }

    private fun playMusic() {
        if (isPlaying) {
            iv_play.background = resources.getDrawable(android.R.drawable.ic_media_pause)
            isPlaying = false
            mMediaPlayer.start()
            return
        }
        iv_play.background = resources.getDrawable(android.R.drawable.ic_media_play)
        mMediaPlayer.pause()
        isPlaying = true
    }

    fun playSong() {
        try {
            mMediaPlayer.reset()
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer.setDataSource(applicationContext, Uri.parse(song.getSongLink()))
            mMediaPlayer.prepare()
            mMediaPlayer.start()
            //Displaying Song Title
            isPlaying = true
            iv_play.background = resources.getDrawable(android.R.drawable.ic_media_pause)
            mMediaLayout.visibility = View.VISIBLE
            mTvTitle.text = song.title
            GlideApp.with(applicationContext).load(song.thumbnail).placeholder(R.drawable.cover_photo).error(R.drawable.cover_photo)
                .crossFade().centerCrop().into(iv_artwork)
            //set progressbar value
            songProgressBar.setProgress(0)
            songProgressBar.max = 100
            //updating progress bar
            updateProgressBar()
        } catch (error: Exception){
            error.printStackTrace()
        }
    }

    private fun playNextSong() {
        if (currentSongIndex < songList.size() -1) {
            var song = songList.get(currentSongIndex + 1)
            playSong()
            currentSongIndex += 1
        } else {
            playSong(songList.get(0))
            currentSongIndex = 0
        }
    }

    private fun playPreviousSong() {
        if (currentSongIndex > 0) {
            val song = songList.get(currentSongIndex - 1)
            playSong(song)
            currentSongIndex -= 1
        } else {
            val song = songList.get(songList.size() - 1)
            playSong(song)
            currentSongIndex = songList.size() - 1
        }
    }

    val updateTimeTask = Runnable {
        if (mMediaPlayer == null) {
            return@Runnable
        }
        var totalDuration = mMediaPlayer.duration
        var currentDuration = mMediaPlayer.currentPosition
        mTvTotalDuration.text = String.format("%s", utils.millisecondsToTimer(totalDuration.toLong()))
        mTvCurrentDuration.text = String.format("%s", utils.millisecondsToTimer(currentDuration.toLong()))
        var progress = utils.getProgressPercentage(currentDuration.toLong(),totalDuration.toLong())
        songProgressBar.setProgress(progress)
    }
    mHandler.postDelayed(updateTimeTask, 100)

    private var mMediaLayout: LinearLayout? = null
    private var mTvTitle: TextView? = null
    private var mIvArtwork: ImageView? = null
    private var mIvPlay: ImageView? = null
    private var mIvPrevious: ImageView? = null
    private var mIvNext: ImageView? = null
    private var isPlaying = false
    private var songProgressBar: SeekBar? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var mTvCurrentDuration: TextView? = null
    private var mTvTotalDuration: TextView? = null
    private var utils: TimeUtil? = null
    private var currentSongIndex: Int = 0
    // Handler to update UI timer, progress bar etc,.
    private val mHandler = Handler()

    private val STORAGE_PERMISSION_ID: Int = 0

    companion object {
        var TAG_FRAGMENT_ONE = "fragment_home"
        var TAG_FRAGMENT_TWO = "fragment_songs"
        var TAG_FRAGMENT_THREE = "fragment_albums"
    }
    private var fragmentManager: FragmentManager? = null
    private var homeFragment = HomeFragment()
    private var songsFragment = SongsFragment()
    private var albumFragment = AlbumFragment()
    private var activeFragment: Fragment? = homeFragment

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        showSelectedFragment(item.itemId)
    }

    private fun showSelectedFragment(itemId: Int): Boolean {
        when (itemId) {
            R.id.navigation_home -> {
                fragmentManager!!.beginTransaction().hide(activeFragment!!).show(homeFragment).commit()
                activeFragment = homeFragment
                return true
            }
            R.id.navigation_songs -> {
                fragmentManager!!.beginTransaction().hide(activeFragment!!).show(songsFragment).commit()
                activeFragment = songsFragment
                return true
            }
            R.id.navigation_albums -> {
                fragmentManager!!.beginTransaction().hide(activeFragment!!).show(albumFragment).commit()
                activeFragment = albumFragment
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation)
        fragmentManager = supportFragmentManager
        prepareFragments()
        init()
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    private fun init(){
        if (!checkStorePermission(STORAGE_PERMISSION_ID)) {
            showRequestPermission(STORAGE_PERMISSION_ID)
        }
        mMediaPlayer = MediaPlayer()
        utils = TimeUtil()

    }
    private fun setupListeners() {
        iv_play.setOnClickListener(this)
        iv_previous.setOnClickListener(this)
        iv_next.setOnClickListener(this)
        songProgressBar?.setOnSeekBarChangeListener(this)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            for ((index, _) in permissions.withIndex()) {
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    //getSongList()
                    SongsFragment.permissionGranted = true
                    return
                }
            }
        }
    }
}
