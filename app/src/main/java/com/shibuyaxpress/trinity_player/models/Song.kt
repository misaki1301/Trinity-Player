package com.shibuyaxpress.trinity_player.models

import androidx.room.*

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
    //@PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Long?,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "album_id") var albumId: Long?,
    @ColumnInfo(name = "artist_id") var artistId: Long?,
    @ColumnInfo(name = "image_cover") var imageCover: String?,
    @ColumnInfo(name = "album_year") var albumYear: String?,
    @ColumnInfo(name = "length") var length: String?,
    @ColumnInfo(name = "bitrate") var bitrate: String?,
    @ColumnInfo(name = "composer") var composer: String?,
    @ColumnInfo(name = "filePath") var filePath: String?,
    @ColumnInfo(name = "audioType") var audioType: String?
)