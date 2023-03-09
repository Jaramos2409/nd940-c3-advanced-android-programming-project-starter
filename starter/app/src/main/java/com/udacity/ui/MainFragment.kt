package com.udacity.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.udacity.R
import com.udacity.customviews.LoadingButton
import com.udacity.databinding.FragmentMainBinding


private const val TAG = "MainFragment"

class MainFragment : Fragment() {

    private lateinit var layout: View

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i(TAG, "Permission was granted.")
            } else {
                Log.i(TAG, "Permission was not granted.")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentMainBinding>(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )

        layout = binding.mainLayout

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            askForNotificationPermissions(binding.root)
        }

        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.radioGlide.setOnClickListener {
            if ((it as RadioButton).isChecked)
                viewModel.apply {
                    setSelectedRepositoryForDownloadingToGlide()
                    setSelectedRepositoryForDownloadingName(it.text as String)
                }

        }

        binding.radioLoadApp.setOnClickListener {
            if ((it as RadioButton).isChecked)
                viewModel.apply {
                    setSelectedRepositoryForDownloadingToLoadApp()
                    setSelectedRepositoryForDownloadingName(it.text as String)
                }
        }

        binding.radioRetrofit.setOnClickListener {
            if ((it as RadioButton).isChecked)
                viewModel.apply {
                    setSelectedRepositoryForDownloadingToRetrofit()
                    setSelectedRepositoryForDownloadingName(it.text as String)
                }
        }

        binding.customButton.setOnClickListener {
            Log.d(TAG, "Clicked on custom button.")
            if (binding.radioGlide.isChecked || binding.radioLoadApp.isChecked || binding.radioRetrofit.isChecked) {
                viewModel.download()
                (it as LoadingButton).setButtonStateClicked()
            } else {
                (it as LoadingButton).triggerAnimationWithoutSelection()
                Toast.makeText(
                    requireContext(),
                    "Please select file to download",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        createChannel(
            getString(R.string.download_notification_channel_id),
            getString(R.string.download_notification_channel_name)
        )

        return binding.root
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description =
                getString(R.string.download_notification_channel_description)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun askForNotificationPermissions(view: View) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i(TAG, "POST NOTIFICATION PERMISSION GRANTED")
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.POST_NOTIFICATIONS
            ) -> {
                layout.showSnackBar(
                    view,
                    getString(R.string.permission_required),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.ok)
                ) {
                    requestPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    private fun View.showSnackBar(
        view: View,
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackBar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {
            snackBar.setAction(actionMessage) {
                action(this)
            }.show()
        } else {
            snackBar.show()
        }
    }

}

