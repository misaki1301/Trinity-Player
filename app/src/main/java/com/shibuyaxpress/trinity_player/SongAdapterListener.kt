package com.shibuyaxpress.trinity_player

import com.shibuyaxpress.trinity_player.models.AuxSong

interface SongAdapterListener {
    fun onSongSelected(song:AuxSong)
}