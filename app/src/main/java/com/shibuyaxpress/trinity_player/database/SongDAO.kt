package com.shibuyaxpress.trinity_player.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shibuyaxpress.trinity_player.models.Album
import com.shibuyaxpress.trinity_player.models.Artist
import com.shibuyaxpress.trinity_player.models.Song

@Dao
interface SongDAO {
    @Query("Select * from songs")
    fun getSongList():List<Song>

    @Query("select * from songs where title like '%'+:title+'%'")
    fun getSongByTitle(title: String): List<Song>

    @Query("select * from  albums")
    fun getAlbumList():List<Album>

    @Query("select * from artists")
    fun getArtistList():List<Artist>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(song: Song)
}