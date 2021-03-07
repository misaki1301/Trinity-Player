package com.shibuyaxpress.trinity_player.utils

import android.content.ContentResolver
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import androidx.core.graphics.ColorUtils
import androidx.core.net.toUri
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch
import com.shibuyaxpress.trinity_player.R
import com.shibuyaxpress.trinity_player.models.Song


class MusicUtils {
    companion object {
        fun getBitmapFromCover(song: Song, contentResolver: ContentResolver, resources: Resources): Bitmap {
            val bitmap: Bitmap = try {
                MediaStore.Images.Media.getBitmap(contentResolver, song.imageCover!!.toUri())
            } catch (error: java.lang.Exception) {
                BitmapFactory.decodeResource(resources, R.drawable.misaki_face)
            }
            return bitmap
        }
    }
}