package com.example.eyelyze.presentation.result

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.eyelyze.base.BaseFragment
import com.example.eyelyze.databinding.FragmentResultBinding
import org.koin.android.ext.android.inject

class ResultFragment : BaseFragment<FragmentResultBinding>() {

    private val viewModel: ResultViewModel by inject()

    private var label: String? = ""
    private var confidenceScore: String? = ""
    private var imageUri: String? = ""
    private var isNew: Boolean = false
    private val args: ResultFragmentArgs by navArgs()
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentResultBinding {
        return FragmentResultBinding.inflate(inflater, container, false)
    }

    override fun initIntent() {
        label = args.label
        confidenceScore = args.score
        imageUri = args.imageUri
        isNew = args.isNew
    }

    override fun initUI() {
        binding.apply {
            tvResultScanning.text = args.label
            tvConfidenceScore.text = args.score
            resultImage.setImageURI(Uri.parse(args.imageUri))
        }


    }

    override fun initActions() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun initProcess() {
        if (args.isNew) {
            viewModel.insertHistory(label ?: "", confidenceScore ?: "", imageUri ?: "")
        }
    }

    override fun initObservers() {
        0

    }

}