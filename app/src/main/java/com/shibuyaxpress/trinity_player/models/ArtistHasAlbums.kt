package com.shibuyaxpress.trinity_player.models

import androidx.room.Embedded
import androidx.room.Relation

data class ArtistHasAlbums(
    @Embedded val artist: Artist,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    ) val albums: List<Album>
)