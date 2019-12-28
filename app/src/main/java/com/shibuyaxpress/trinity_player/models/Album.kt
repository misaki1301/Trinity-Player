package com.shibuyaxpress.trinity_player.models

import androidx.room.Entity
import androidx.room.Ignore

@Entity(tableName = "albums")
data class Album(
    var id: Long?,
    var name: String?,
    var imageCover: String?,
    var artist_id: Long?,
    @Ignore var numberOfSong: Int?
)