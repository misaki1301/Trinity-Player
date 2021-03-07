package com.shibuyaxpress.trinity_player.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.MediaStore
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media.session.MediaButtonReceiver
import com.shibuyaxpress.trinity_player.R
import com.shibuyaxpress.trinity_player.activities.MainActivity
import com.shibuyaxpress.trinity_player.models.Song
import com.shibuyaxpress.trinity_player.utils.MusicUtils
import com.shibuyaxpress.trinity_player.utils.TimeUtil
import kotlinx.coroutines.*


private const val CHANNEL_ID = "13"
private const val NOTIFY_ID = 1
private const val ACTION = "com.shibuyaxpress.trinity_player.activities.MainActivity"

class MusicService: Service(),
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {

    companion object {
        val ACTION = "com.shibuyaxpress.trinity_player.activities.MainActivity"
    }

    var songList: List<Song> = emptyList()
    var songPosition: Int = 0


    fun switchSongList(list: List<Song>) {
        songList = list
    }
    fun switchSongPosition(index: Int) {
        songPosition = index
    }

    var player: MediaPlayer? = null

    private var musicBind = MusicBinder()
    private var mSeekBar: SeekBar? = null
    private var mProgress: ProgressBar? = null
    lateinit var mCurrentPos: TextView
    lateinit var mTotalDuration: TextView
    //media session artifacts
    private lateinit var mediaSession : MediaSessionCompat
    //var mediaSessionCallback = MediaSessionCallback()
    private lateinit var token: MediaSessionCompat.Token
    var isPlaying = false
    private val scope = CoroutineScope(Dispatchers.Main)

    private fun updateProgressSong(): Job {
        return scope.launch {
            if (mSeekBar != null) {
                mSeekBar!!.progress = player!!.currentPosition
                mProgress!!.progress = player!!.currentPosition
                if (player!!.isPlaying) {
                    Log.d(MusicService::class.java.name, "now in time ${player!!.currentPosition}")
                    delay(1000)
                }
            }
        }
    }

    private val mProgressRunner: Runnable = object : Runnable {
        override fun run() {
            if (mSeekBar != null) {
                mSeekBar!!.progress = player!!.currentPosition
                mProgress!!.progress = player!!.currentPosition
                if (player!!.isPlaying) {
                    Log.d(MusicService::class.java.name, "now in time ${player!!.currentPosition}")
                    mSeekBar!!.postDelayed(this, 1000)
                }
            }
        }
    }

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

    override fun onCreate() {
        super.onCreate()

        songPosition = 0
        player = MediaPlayer()
        initMusicPlayer()

        mediaSession = MediaSessionCompat(this, "token")
        token = mediaSession.sessionToken

        //some flags like FLAG_HANDLES_MEDIA_BUTTONS ARE SET BY DEFAULT IN 8+ Android
        mediaSession
            .setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                        or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
        val playbackBuilder = PlaybackStateCompat.Builder()

        playbackBuilder
            .setActions(
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        or PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            )

        mediaSession.setPlaybackState(playbackBuilder.build())
        mediaSession.setCallback(mediaSessionCallback)
        mediaSession.isActive = true

    }

    fun pauseSong() {
        player?.pause()
        isPlaying = false
        mSeekBar?.removeCallbacks(mProgressRunner)
        buildNotification()
    }

    fun resumeSong() {
        player?.start()
        isPlaying = true
        mProgressRunner.run()
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
        val trackUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            currentSong!!
        )
        //set data source
        try {
            player?.setDataSource(applicationContext, trackUri)
        } catch (error: Exception) {
            Log.e("MusicService.Class", "Error: setting data source", error)
        }
        player?.prepareAsync()
        mProgressRunner.run()
        isPlaying = true
        //create a pending intent
        val intent = Intent(ACTION)
        intent.putExtra("status", "PLAYING_SONG")
        intent.putExtra("currentSong", playSong)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        Log.d(
            MusicService::class.java.simpleName,
            "Now playing from service $playSong cursor position $songPosition"
        )
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
            player!!.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        }
        player?.setOnPreparedListener(this)
        player?.setOnCompletionListener(this)
        player?.setOnErrorListener(this)
    }

    fun setUIControls(
        progress: ProgressBar,
        seekBar: SeekBar,
        currentPos: TextView,
        totalDuration: TextView
    ) {
        mSeekBar = seekBar
        mCurrentPos = currentPos
        mTotalDuration = totalDuration
        mProgress = progress
        mSeekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player!!.seekTo(progress)
                }

                mCurrentPos.text = TimeUtil.timerConverter(progress.toLong())

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }


    private fun createContentIntent(): PendingIntent {
        val intent =  Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
    private fun buildNotification() {
        val name = "Trinity Player Music Service"
        val description = "Just hear about your music"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance)

        channel.description = description

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)

        //converting image to bitmap and getting current song
        val currentSong = songList[songPosition]

        val bitmap = MusicUtils.getBitmapFromCover(currentSong, contentResolver, resources)

        //setting notification with media player
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        notificationBuilder
            .setSmallIcon(R.drawable.ic_stat_asset_3)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(createContentIntent())
            .setContentTitle(currentSong.title)
            .setContentText(currentSong.artist.name)
            //.setSubText("Song Name")
            .setChannelId(CHANNEL_ID)
            .setLargeIcon(bitmap)
            .setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_STOP
                )
            )

        notificationBuilder
            .addAction(
                android.R.drawable.ic_media_previous, "Previous",
                MediaButtonReceiver
                    .buildMediaButtonPendingIntent(
                        this@MusicService,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    )
            )

        if (!isPlaying) {
            notificationBuilder
                .addAction(
                    android.R.drawable.ic_media_play, "Play",
                    MediaButtonReceiver
                        .buildMediaButtonPendingIntent(
                            this@MusicService,
                            PlaybackStateCompat.ACTION_PLAY
                        )
                )
        }else {
            notificationBuilder
                .addAction(
                    android.R.drawable.ic_media_pause, "Pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this@MusicService,
                        PlaybackStateCompat.ACTION_PAUSE
                    )
                )
        }

        notificationBuilder
            .addAction(
                android.R.drawable.ic_media_next, "Next",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this@MusicService,
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                )
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(token)
                    .setShowActionsInCompactView(0, 1, 2)
                    .setCancelButtonIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            this,
                            PlaybackStateCompat.ACTION_STOP
                        )
                    )
            )

        //if (isPlaying) {
        val intent = Intent(this, MusicService::class.java)
        ContextCompat.startForegroundService(this, intent)
        startForeground(NOTIFY_ID, notificationBuilder.build())
        //}
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
        val duration = mp?.duration
        mSeekBar!!.max = duration!!
        mProgress!!.max = duration
        mTotalDuration.text = TimeUtil.timerConverter(duration.toLong())
        //calls coroutine to update ui
        mSeekBar?.postDelayed(mProgressRunner, 1000)

        //create notification channel
        buildNotification()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("flags", flags.toString())
        Log.d("intent", intent.toString())
        Log.d("startID", startId.toString())
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