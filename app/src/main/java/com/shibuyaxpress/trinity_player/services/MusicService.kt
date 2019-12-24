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

private const val CHANNEL_ID = "13"
private const val NOTIFY_ID = 1

class MusicService: Service(),
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {

    private var player: MediaPlayer? = null
    private var songList: List<AuxSong> = ArrayList()
    private var songPosition: Int = 0
    private var musicBind = MusicBinder()

    //media session artifacts
    private lateinit var mediaSession : MediaSessionCompat
    //var mediaSessionCallback = MediaSessionCallback()
    private lateinit var token: MediaSessionCompat.Token
    var isPlaying = false

    private var mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            super.onPlay()
            if (!isPlaying) {
                resumeSong()
            }
        }

        override fun onPause() {
            super.onPause()
            if (isPlaying) {
                pauseSong()
            } else {
                resumeSong()
            }
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            playNext()
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            playPreviousSong()
        }
    }

    override fun onCreate(){
        super.onCreate()
        songPosition = 0
        player = MediaPlayer()
        initMusicPlayer()
        mediaSession = MediaSessionCompat(this, "token")
        token = mediaSession.sessionToken
        //some flags like FLAG_HANDLES_MEDIA_BUTTONS ARE SET BY DEFAULT IN 8+ Android
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        val playbackBuilder = PlaybackStateCompat.Builder()
        //playbackBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE)
        //playbackBuilder.setActions(PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
        playbackBuilder.setActions(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
        mediaSession.setPlaybackState(playbackBuilder.build())
        mediaSession.setCallback(mediaSessionCallback)
        mediaSession.isActive = true

    }

    fun setSongList(songList:List<AuxSong>) {
        this.songList = songList
    }
    fun setSongPosition(index:Int) {
        songPosition = index
    }

    fun pauseSong() {
        player?.pause()
        isPlaying = false
        buildNotification()
    }

    fun resumeSong() {
        player?.start()
        isPlaying = true
        buildNotification()
    }
    fun playSong() {
        //plays song
        player?.reset()
        //get song
        val playSong = songList[songPosition]
        //get id
        val currentSong = playSong.id
        //set URI
        val trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong!!)
        //set data source
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



    private fun createContentIntent(): PendingIntent {
        val intent =  Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
    }
    private fun buildNotification() {
        val name = "Music Service"
        val description = "Just hear about ur music"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID,name,importance)
        channel.description = description
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        //converting image to bitmap and getting current song
        val currentSong = songList[songPosition]
        val bitmap: Bitmap
        bitmap = try {
            MediaStore.Images.Media.getBitmap(contentResolver, currentSong.thumbnail!!.toUri())
        } catch (error:java.lang.Exception) {
            BitmapFactory.decodeResource(resources, R.drawable.misaki_face)
        }
        //setting notification with media player
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        notificationBuilder
            .setSmallIcon(R.drawable.ic_stat_asset_3)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(createContentIntent())
            .setContentTitle(currentSong.title)
            .setContentText(currentSong.artist)
            //.setSubText("Song Name")
            .setChannelId(CHANNEL_ID)
            .setLargeIcon(bitmap)
            .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,PlaybackStateCompat.ACTION_STOP))
        notificationBuilder
            .addAction(android.R.drawable.ic_media_previous,"Previous", MediaButtonReceiver.buildMediaButtonPendingIntent(this@MusicService, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))
        if (!isPlaying) {
            notificationBuilder
                .addAction(android.R.drawable.ic_media_play, "Play", MediaButtonReceiver.buildMediaButtonPendingIntent(this@MusicService, PlaybackStateCompat.ACTION_PLAY))
        }else {
            notificationBuilder
                .addAction(android.R.drawable.ic_media_pause,"Pause", MediaButtonReceiver.buildMediaButtonPendingIntent(this@MusicService, PlaybackStateCompat.ACTION_PAUSE))
        }
        notificationBuilder
            .addAction(android.R.drawable.ic_media_next,"Next", MediaButtonReceiver.buildMediaButtonPendingIntent(this@MusicService, PlaybackStateCompat.ACTION_SKIP_TO_NEXT))
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(token)
                .setShowActionsInCompactView(0,1,2)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP)))

        //if (isPlaying) {
        val intent = Intent(this, MusicService::class.java)
        ContextCompat.startForegroundService(this, intent)
        startForeground(NOTIFY_ID, notificationBuilder.build())
        //}
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
        //create pendingintents
        //val pauseIntent = Intent(this, MusicService::class.java)
        //pauseIntent.action = ACTION_PLAY_PAUSE

        //val nextIntent = Intent(this, MusicService::class.java)
        //nextIntent.action = ACTION_SKIP_NEXT

        //val prevIntent = Intent(this, MusicService::class.java)
        //prevIntent.action = ACTION_SKIP_PREV
        //create notification channel
        buildNotification()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("flags",flags.toString())
        Log.d("intent",intent.toString())
        Log.d("startID",startId.toString())
        MediaButtonReceiver.handleIntent(mediaSession, intent)
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
            p0.reset()
            playNext()
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return musicBind
    }

    //here the service detect when its unbinded but if the user touch the notification to go to
    //the main activity it cuts the player
    //so we comment player.stop && player.release
    //need to make a validation
    override fun onUnbind(intent: Intent?): Boolean {
        //player?.stop()
        //player?.release()
        return false
    }

}