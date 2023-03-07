package com.udacity.ui

import android.app.Application
import android.app.DownloadManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.udacity.R

private const val TAG = "MainViewModel"

class MainViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _selectedRepositoryForDownloadingUrl = MutableLiveData<DownloadUrl>()
    private val _selectedRepositoryForDownloadName = MutableLiveData<String>()
    private val downloadManager =
        app.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
    private lateinit var downloadProgressWatcher: DownloadProgressWatcher

    private val _downloadId = MutableLiveData<Long?>()

    val downloadProgress = MutableLiveData<Long?>()

    init {
        _downloadId.value = 0L
        downloadProgress.value = null
    }

    fun setSelectedRepositoryForDownloadingName(name: String) {
        _selectedRepositoryForDownloadName.value = name
    }

    fun setSelectedRepositoryForDownloadingToGlide() {
        Log.d(TAG, "Selected Glide as the repository to download.")
        _selectedRepositoryForDownloadingUrl.value = DownloadUrl.GLIDE
    }

    fun setSelectedRepositoryForDownloadingToLoadApp() {
        Log.d(TAG, "Selected Load App as the repository to download.")
        _selectedRepositoryForDownloadingUrl.value = DownloadUrl.LOAD_APP
    }

    fun setSelectedRepositoryForDownloadingToRetrofit() {
        Log.d(TAG, "Selected Retrofit as the repository to download.")
        _selectedRepositoryForDownloadingUrl.value = DownloadUrl.RETROFIT
    }

    private fun getSelectedRepositoryForDownload() = _selectedRepositoryForDownloadingUrl.value?.url

    fun download() {
        val request =
            DownloadManager.Request(Uri.parse(getSelectedRepositoryForDownload()))
                .setTitle(_selectedRepositoryForDownloadName.value)
                .setDescription(app.getString(R.string.app_description))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        _downloadId.value =
            downloadManager.enqueue(request)


        downloadProgressWatcher =
            DownloadProgressWatcher(_downloadId.value!!, downloadManager) { progress ->
                run {
                    if (progress > 0L) {
                        downloadProgress.value = progress
                        Log.i(TAG, "Download Progress = $progress")

                        if (progress == 100L) {
                            _downloadId.value = null
                        }
                    }
                }
            }

        downloadProgressWatcher.startWatching()
    }
}

enum class DownloadUrl(val url: String) {
    GLIDE("https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"),
    LOAD_APP("https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"),
    RETROFIT("https://github.com/square/retrofit")
}

class DownloadProgressWatcher(
    private val downloadId: Long,
    private val downloadManager: DownloadManager,
    private val listener: (Long) -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private var isWatching = false
    private val millisDelayBetweenPolling = 1000L

    fun startWatching() {
        if (isWatching) return

        isWatching = true
        handler.post(watcherRunnable)
    }

    fun stopWatching() {
        if (!isWatching) return

        isWatching = false
        handler.removeCallbacks(watcherRunnable)
    }

    private val watcherRunnable = object : Runnable {
        override fun run() {
            Log.i(TAG, "downloadId: $downloadId")

            val query = DownloadManager.Query().apply {
                setFilterById(downloadId)
            }

            val cursor = downloadManager.query(query)

            cursor?.use {
                if (it.moveToFirst()) {
                    when (it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            Log.i(TAG, "Download Successful")
                            listener(100)
                            stopWatching()
                        }
                        DownloadManager.STATUS_FAILED -> {
                            Log.i(TAG, "Download Failed")
                            listener(-1)
                            stopWatching()
                        }
                        DownloadManager.STATUS_PENDING -> {
                            Log.i(TAG, "Download Pending")
                        }
                        DownloadManager.STATUS_PAUSED -> {
                            Log.i(TAG, "Download Paused")
                        }
                        DownloadManager.STATUS_RUNNING -> {
                            Log.i(TAG, "Download Running")

                            val totalSize =
                                it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                            Log.d(TAG, "Total Size: $totalSize")

                            val downloadedSize =
                                it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                            val progress =
                                if (totalSize > 0) (downloadedSize * 100L / totalSize) else 0
                            listener(progress)
                        }
                    }
                }
            }

            if (isWatching) {
                handler.postDelayed(this, millisDelayBetweenPolling)
            }
        }
    }
}