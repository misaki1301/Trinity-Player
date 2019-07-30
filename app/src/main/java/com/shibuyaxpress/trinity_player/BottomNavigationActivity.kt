package com.shibuyaxpress.trinity_player

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.shibuyaxpress.trinity_player.Utils.PermissionUtil
import com.shibuyaxpress.trinity_player.fragments.AlbumFragment
import com.shibuyaxpress.trinity_player.fragments.HomeFragment
import com.shibuyaxpress.trinity_player.fragments.SongsFragment

class BottomNavigationActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION_ID: Int = 0

    companion object {
        var TAG_FRAGMENT_ONE = "fragment_home"
        var TAG_FRAGMENT_TWO = "fragment_songs"
        var TAG_FRAGMENT_THREE = "fragment_albums"
    }
    private var fragmentManager: FragmentManager? = null
    private var homeFragment = HomeFragment()
    private var songsFragment = SongsFragment()
    private var albumFragment = AlbumFragment()
    private var activeFragment: Fragment? = homeFragment

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        showSelectedFragment(item.itemId)
    }

    private fun showSelectedFragment(itemId: Int): Boolean {
        when (itemId) {
            R.id.navigation_home -> {
                fragmentManager!!.beginTransaction().hide(activeFragment!!).show(homeFragment).commit()
                activeFragment = homeFragment
                return true
            }
            R.id.navigation_songs -> {
                fragmentManager!!.beginTransaction().hide(activeFragment!!).show(songsFragment).commit()
                activeFragment = songsFragment
                return true
            }
            R.id.navigation_albums -> {
                fragmentManager!!.beginTransaction().hide(activeFragment!!).show(albumFragment).commit()
                activeFragment = albumFragment
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation)
        fragmentManager = supportFragmentManager
        prepareFragments()
        init()
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    private fun init(){
        if (!checkStorePermission(STORAGE_PERMISSION_ID)) {
            showRequestPermission(STORAGE_PERMISSION_ID)
        }
    }

    private fun checkStorePermission(permission: Int): Boolean {
        return if (permission == STORAGE_PERMISSION_ID) {
            PermissionUtil.checkPermissions(this, listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        } else {
            true
        }
    }

    private fun showRequestPermission(requestCode: Int) {
        val permissions: List<String> = if (requestCode == STORAGE_PERMISSION_ID) {
            listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            listOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)

        }
        PermissionUtil.requestPermissions(this, requestCode, permissions)
    }

    private fun prepareFragments() {
        fragmentManager!!.beginTransaction()
            .add(R.id.contentFrame, songsFragment, "2")
            .hide(songsFragment)
            .commit()
        fragmentManager!!.beginTransaction()
            .add(R.id.contentFrame, albumFragment, "3")
            .hide(albumFragment)
            .commit()
        fragmentManager!!.beginTransaction()
            .add(R.id.contentFrame, homeFragment, "1")
            .commit()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            for ((index, _) in permissions.withIndex()) {
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    //getSongList()
                    SongsFragment.permissionGranted = true
                    return
                }
            }
        }
    }
}
