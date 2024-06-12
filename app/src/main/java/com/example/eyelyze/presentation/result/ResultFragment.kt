package com.example.eyelyze.presentation.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.eyelyze.base.BaseFragment
import com.example.eyelyze.databinding.FragmentResultBinding

class ResultFragment : BaseFragment<FragmentResultBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentResultBinding {
        return FragmentResultBinding.inflate(inflater, container, false)
    }

    override fun initUI() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun initActions() {

    }

    override fun initProcess() {

    }

    override fun initObservers() {0

    }

}