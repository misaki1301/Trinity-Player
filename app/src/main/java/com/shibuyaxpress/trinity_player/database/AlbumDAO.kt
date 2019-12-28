package com.shibuyaxpress.trinity_player.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.shibuyaxpress.trinity_player.models.ArtistHasAlbums

@Dao
interface AlbumDAO {

    @Transaction
    @Query("Select * from artists")
    fun getArtistsWithAlbum():List<ArtistHasAlbums>
}