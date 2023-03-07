package com.udacity.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DetailViewModel : ViewModel() {

    private val _navigateToMainFragment = MutableLiveData<Boolean>()
    val navigateToMainFragment: LiveData<Boolean>
        get() = _navigateToMainFragment

    init {
        resetNavigationToMainFragment()
    }

    fun shouldNavigateToMainFragment() {
        _navigateToMainFragment.value = true
    }

    fun resetNavigationToMainFragment() {
        _navigateToMainFragment.value = false
    }

}