package com.shibuyaxpress.trinity_player.models

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "songs",
    indices = [Index(value = ["id"],unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = Album::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("album_id"),
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = Artist::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("artist_id"),
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)

data class Song(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id") var id: Long?,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "album_id") var albumId: Long?,
    @ColumnInfo(name = "artist_id") var artistId: Long?,
    @ColumnInfo(name = "image_cover") var imageCover: String?,
    @ColumnInfo(name = "filePath") var filePath: String?,
    @Embedded(prefix = "song_artist_") var artist: Artist,
    @Embedded(prefix = "song_album_") var album: Album
):Parcelable