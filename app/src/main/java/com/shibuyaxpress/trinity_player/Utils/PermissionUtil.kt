package com.shibuyaxpress.trinity_player.Utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class PermissionUtil {
    companion object{
        fun checkPermissions(context:Context, permissions:List<String>): Boolean {
            for (permission in permissions) {
                if (!checkPermission(context, permission)){
                    return false
                }
            }
            return true
        }

        private fun checkPermission(context: Context, permission: String): Boolean {
            return ActivityCompat.checkSelfPermission(context,permission) == PackageManager.PERMISSION_GRANTED
        }

        fun requestPermissions(activity:Activity, requestCode: Int, permissions:List<String>){
            ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), requestCode)
        }
    }
}