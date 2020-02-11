package com.shibuyaxpress.trinity_player.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ArtistHasAlbums(
    @Embedded val artist: Artist,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    ) val albums: List<Album>
):Parcelable