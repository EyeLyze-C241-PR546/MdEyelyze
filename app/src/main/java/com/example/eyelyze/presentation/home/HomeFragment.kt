package com.example.eyelyze.presentation.home

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.eyelyze.R
import com.example.eyelyze.base.BaseFragment
import com.example.eyelyze.databinding.FragmentHomeBinding
import com.example.eyelyze.presentation.CameraFragment
import com.example.eyelyze.utils.showSnackBar
import timber.log.Timber

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private var currentImageUri: Uri? = null
    private var currentLabel: String = "TES"
    private var currentConfidenceScore: String = "80"

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                binding.root.showSnackBar(getString(R.string.permission_granted))
                findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
            } else {
                binding.root.showSnackBar(getString(R.string.permission_denied))
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun initUI() {

        setFragmentResultListener("cameraXResult") { _, bundle ->
            if (bundle.containsKey(CameraFragment.EXTRA_CAMERAX_IMAGE)) {
                currentImageUri = bundle.getString(CameraFragment.EXTRA_CAMERAX_IMAGE)?.toUri()
                showImage()
            }
        }
    }

    override fun initActions() {
        binding.apply {
            galleryButton.setOnClickListener {
                startGallery()
            }

            cameraButton.setOnClickListener {
                startCamera()
            }

            analyzeButton.setOnClickListener {
                analyzeImage()
            }

            historyButton.setOnClickListener {
                // moveToHistory()
            }
        }
    }

    override fun initProcess() {

    }

    override fun initObservers() {


    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Toast.makeText(requireContext(), "No media selected", Toast.LENGTH_LONG).show()
        }
    }


    private fun startCamera() {
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        } else {

            findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
        }


    }

    private fun showImage() {
        currentImageUri?.let {
            Timber.d("showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        if (currentImageUri == null) {
            binding.root.showSnackBar("Belum ada gambar")
            return
        }
//        ImageClassifierHelper2(
//            context = requireContext(),
//            onSuccess = { label, confidenceScore ->
//                currentLabel = label
//                currentConfidenceScore = confidenceScore
//                moveToResult()
//            },
//            onFailed = { binding.root.showSnackBar(it) }
//        ).classifyStaticImage(requireContext(), currentImageUri!!)
        moveToResult()

    }

    private fun moveToResult() {
        binding.root.showSnackBar("label : $currentLabel, score : $currentConfidenceScore")
        val toResultFragment = HomeFragmentDirections.actionHomeFragmentToResultFragment(
            /* label = */ currentLabel,
            /* score = */ currentConfidenceScore,
            /* imageUri = */ currentImageUri.toString(),
            /* isNew = */ true
        )
        findNavController().navigate(toResultFragment)
    }


    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
