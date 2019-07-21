package com.shibuyaxpress.trinity_player.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.shibuyaxpress.trinity_player.models.Song

@Dao
interface SongDataDao {
    @Query("SELECT * from Song")
    fun getAll(): List<Song>

    @Insert(onConflict = REPLACE)
    fun insert(song: Song)

    @Query("DELETE from Song")
    fun deleteAll()
}