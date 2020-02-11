package com.shibuyaxpress.trinity_player.models

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "genres")
data class Genre(
    var id: Int?,
    var name: String?
):Parcelable