package com.shibuyaxpress.trinity_player.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shibuyaxpress.trinity_player.models.Album
import com.shibuyaxpress.trinity_player.models.Artist
import com.shibuyaxpress.trinity_player.models.Song

@Database(entities = [Album::class, Song::class, Artist::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao() : SongDAO
}