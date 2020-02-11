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

    @Query("Select * from songs order by title")
    suspend fun getSongList():List<Song>

    @Query("select * from songs where title like '%'+:title+'%'")
    suspend fun getSongByTitle(title: String): List<Song>

    @Query("select * from  albums")
    suspend fun getAlbumList():List<Album>

    @Query("select * from artists")
    suspend fun getArtistList():List<Artist>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(song: Song)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(songs: List<Song>)

    @Query("SELECT * FROM songs where album_id = :album_id")
    suspend fun getSongsFromAlbum(album_id: kotlin.Long?):List<Song>
}