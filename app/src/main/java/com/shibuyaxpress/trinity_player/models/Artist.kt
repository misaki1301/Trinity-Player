package com.shibuyaxpress.trinity_player.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "artists")
data class Artist (
    @PrimaryKey(autoGenerate = false)
    var id: Long?,
    var name: String?,
    var image: String?
):Parcelable