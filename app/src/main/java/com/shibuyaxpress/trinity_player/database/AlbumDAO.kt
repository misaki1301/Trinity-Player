package com.shibuyaxpress.trinity_player.database

import androidx.room.*
import com.shibuyaxpress.trinity_player.models.Album
import com.shibuyaxpress.trinity_player.models.ArtistHasAlbums

@Dao
interface AlbumDAO {

    @Transaction
    @Query("Select * from artists")
    suspend fun getArtistsWithAlbum():List<ArtistHasAlbums>

    @Query("Select * from albums")
    suspend fun getAllAlbums(): List<Album>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(albums: List<Album>)
}