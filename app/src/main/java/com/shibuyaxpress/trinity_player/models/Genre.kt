package com.shibuyaxpress.trinity_player.models

import androidx.room.Entity

@Entity(tableName = "genres")
data class Genre(
    var id: Int?,
    var name: String?
)