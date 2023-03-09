package com.udacity

import android.app.DownloadManager
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.udacity.receiver.DownloadReceiver

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val downloadReceiver = DownloadReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        setupActionBarWithNavController(
            (supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController,
            AppBarConfiguration(setOf(R.id.mainFragment, R.id.detailFragment))
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downloadReceiver)
    }

}
