package com.shibuyaxpress.trinity_player.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.MediaStore
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.media.session.MediaButtonReceiver
import com.shibuyaxpress.trinity_player.MainActivity
import com.shibuyaxpress.trinity_player.R
import com.shibuyaxpress.trinity_player.models.AuxSong


class MusicService: Service(),
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {

    private var player: MediaPlayer? = null
    private var songList: List<AuxSong> = ArrayList()
    private var songPosition: Int = 0
    private var musicBind = MusicBinder()
    private val CHANNEL_ID = "13"
    private val ACTION_PAUSE = "ACTION_PAUSE"
    private val ACTION_SKIP_PREV = "ACTION_SKIP_PREV"
    private val ACTION_SKIP_NEXT = "ACTION_SKIP_NEXT"
    lateinit var mediaSession : MediaSessionCompat
    lateinit var token: MediaSessionCompat.Token
    var isPlaying = false

    override fun onCreate(){
        super.onCreate()
        songPosition = 0
        player = MediaPlayer()
        initMusicPlayer()
        mediaSession = MediaSessionCompat(this, "token")
        token = mediaSession.sessionToken
    }

    fun setSongList(songList:List<AuxSong>) {
        this.songList = songList
    }
    fun setSongPosition(index:Int) {
        songPosition = index
    }

    fun playSong() {
        //plays song
        player?.reset()
        //get song
        var playSong = songList[songPosition!!]
        //get id
        var currentSong = playSong.id
        //set URI
        var trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong!!)
        //set datasource
        try {
            player?.setDataSource(applicationContext, trackUri)
        } catch (error: Exception) {
            Log.e("MusicService.Class","Error: setting data source", error)
        }
        player?.prepareAsync()
        isPlaying = true
    }

    fun playPreviousSong() {
        songPosition--
        if (songPosition < 0) {
            songPosition = songList.size - 1
        }
        playSong()
    }

    fun playNext() {
        songPosition++
        if (songPosition >= songList.size) {
            songPosition = 0
        }
        playSong()
    }

    inner class MusicBinder : Binder() {
        fun getService() : MusicService {
            return this@MusicService
        }
    }

    private fun initMusicPlayer(){
        player!!.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            player!!.setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())
        }
        player?.setOnPreparedListener(this)
        player?.setOnCompletionListener(this)
        player?.setOnErrorListener(this)
    }

    private val NOTIFY_ID = 1

    private fun createContentIntent(): PendingIntent {
        val intent =  Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
        //create pendingintents
        var pauseIntent = Intent(this, MusicService::class.java)
        pauseIntent.action = ACTION_PAUSE
        //create notification channel
        val name = "Music Service"
        val description = "Just hear about ur music"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID,name,importance)
        channel.description = description
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        //converting image to bitmap and getting current song
        var currentSong = songList[songPosition]
        var bitmap: Bitmap
        try {
            bitmap =
                MediaStore.Images.Media.getBitmap(contentResolver, currentSong.thumbnail!!.toUri())
        } catch (error:java.lang.Exception) {
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.misaki_face)
        }
        //setting notification with media player
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        notificationBuilder
            .setSmallIcon(R.drawable.cover_photo)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(android.R.drawable.ic_media_previous,"Previous", MediaButtonReceiver.buildMediaButtonPendingIntent(this@MusicService, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))
            .addAction(android.R.drawable.ic_media_pause,"Pause", MediaButtonReceiver.buildMediaButtonPendingIntent(this@MusicService, PlaybackStateCompat.ACTION_PLAY_PAUSE))
            .addAction(android.R.drawable.ic_media_next,"Next", MediaButtonReceiver.buildMediaButtonPendingIntent(this@MusicService, PlaybackStateCompat.ACTION_SKIP_TO_NEXT))
            .setContentIntent(createContentIntent())
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(token)
                .setShowActionsInCompactView(0,1,2)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP)))
            .setContentTitle(currentSong.title)
            .setContentText(currentSong.artist)
            //.setSubText("Song Name")
            .setChannelId(CHANNEL_ID)
            .setLargeIcon(bitmap)
            .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,PlaybackStateCompat.ACTION_STOP))
        if (isPlaying) {
            val intent = Intent(this, MusicService::class.java)
            ContextCompat.startForegroundService(this, intent)
            startForeground(NOTIFY_ID, notificationBuilder.build())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("flags",flags.toString())
        Log.d("intent",intent.toString())
        Log.d("startID",startId.toString())
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        stopForeground(true)
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        p0?.reset()
        return false
    }

    override fun onCompletion(p0: MediaPlayer?) {
        if (p0?.currentPosition!! > 0) {
            p0?.reset()
            playNext()
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return musicBind
    }

    override fun onUnbind(intent: Intent?): Boolean {
        player?.stop()
        player?.release()
        return false
    }

}