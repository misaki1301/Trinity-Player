package com.shibuyaxpress.trinity_player.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists")
data class Artist (
    @PrimaryKey(autoGenerate = false)
    var id: Long?,
    var name: String?,
    var image: String?
)