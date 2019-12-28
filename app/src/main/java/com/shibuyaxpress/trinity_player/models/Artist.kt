package com.shibuyaxpress.trinity_player.models

import androidx.room.Entity

@Entity(tableName = "artists")
data class Artist (
    var id: Long?,
    var name: String?,
    var image: String?
)