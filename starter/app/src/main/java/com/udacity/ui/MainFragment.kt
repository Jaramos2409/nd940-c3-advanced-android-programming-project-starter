package com.udacity.ui

import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.udacity.R
import com.udacity.customviews.LoadingButton
import com.udacity.databinding.FragmentMainBinding


private const val TAG = "MainFragment"

class MainFragment : Fragment() {

    private val CHANNEL_ID = "channelId"

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

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

        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.radioGlide.setOnClickListener {
            if ((it as RadioButton).isChecked)
                viewModel.setSelectedRepositoryForDownloadingToGlide()
        }

        binding.radioLoadApp.setOnClickListener {
            if ((it as RadioButton).isChecked)
                viewModel.setSelectedRepositoryForDownloadingToLoadApp()
        }

        binding.radioRetrofit.setOnClickListener {
            if ((it as RadioButton).isChecked)
                viewModel.setSelectedRepositoryForDownloadingToRetrofit()
        }

        binding.customButton.setOnClickListener {
            Log.d(TAG, "Clicked on custom button.")
            viewModel.download()
            (it as LoadingButton).setButtonStateClicked()
        }

        viewModel.shouldButtonBeClickable.observe(viewLifecycleOwner) { shouldButtonBeClickable ->
            run {
                shouldButtonBeClickable?.let {
                    binding.customButton.isClickable = shouldButtonBeClickable
                }
            }
        }

        return binding.root
    }


}

