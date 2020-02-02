package com.shibuyaxpress.trinity_player.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shibuyaxpress.trinity_player.models.Artist

@Dao
interface ArtistDAO {

    @Query("Select * from artists")
    fun getArtistList():List<Artist>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllArtists(items: List<Artist>)
}