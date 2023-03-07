package com.udacity.ui

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.R
import com.udacity.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel
    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detail,
            container,
            false
        )
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        binding.okButton.setOnClickListener {
            viewModel.shouldNavigateToMainFragment()
        }

        viewModel.navigateToMainFragment.observe(viewLifecycleOwner) { shouldNavigateToMainFragment ->
            if (shouldNavigateToMainFragment) {
                findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToMainFragment())
                viewModel.resetNavigationToMainFragment()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancelAll()

        val args = arguments

        args?.getInt("downloadStatus").let {
            binding.downloadStatusValue.apply {
                if (it == DownloadManager.STATUS_FAILED) {
                    text = context.getString(R.string.fail)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.carmine_pink))
                } else {
                    text = context.getString(R.string.success)
                }
            }
        }

        args?.getString("fileName").let {
            binding.fileNameValue.text = it
        }
    }

}