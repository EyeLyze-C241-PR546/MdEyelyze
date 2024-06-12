package com.example.eyelyze.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.eyelyze.R
import com.example.eyelyze.base.BaseFragment
import com.example.eyelyze.databinding.FragmentHomeBinding

class HomeFragment: BaseFragment<FragmentHomeBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun initUI() {

    }

    override fun initActions() {
        binding.apply {
            galleryButton.setOnClickListener {
                // startGallery()
            }

            cameraButton.setOnClickListener {
                // startCamera()
            }

            analyzeButton.setOnClickListener {
                 // analyzeImage()
                    moveToResult()
            }

            historyButton.setOnClickListener {
                 // moveToHistory()
            }
        }
    }

    override fun initProcess() {
6
    }

    override fun initObservers() {0

    }

    private fun moveToResult() {
        findNavController().navigate(R.id.action_homeFragment_to_resultFragment)
    }
}
