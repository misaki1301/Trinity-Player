package com.shibuyaxpress.trinity_player.models

import androidx.room.Entity

@Entity(tableName = "albums")
data class Album(
    var id: Int?,
    var name: String?,
    var imageCover: String?,
    var artistName: String?,
    var numberOfSong: Int?
)