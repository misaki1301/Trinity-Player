package com.shibuyaxpress.trinity_player.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shibuyaxpress.trinity_player.models.Album
import com.shibuyaxpress.trinity_player.models.Artist
import com.shibuyaxpress.trinity_player.models.Song

@Database(entities = [Album::class, Song::class, Artist::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun songDao() : SongDAO

    companion object {
        @Volatile private var instance: AppDatabase? = null
        private var LOCK = Any()

        operator fun invoke(context:Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {instance = it}
        }
        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            AppDatabase::class.java, "trinity-player.db").build()
    }
}