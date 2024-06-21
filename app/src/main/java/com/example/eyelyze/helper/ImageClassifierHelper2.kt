package com.example.eyelyze.helper


import android.R.id.input
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.text.NumberFormat


class ImageClassifierHelper2(
    private var threshold: Float = 0.2f,
    private var maxResults: Int = 3,
    private val modelName: String = "modelv2.tflite",
    private val context: Context,
    private val onSuccess: (String, String) -> Unit,
    private val onFailed: (String) -> Unit,
) {

    private var interpreter: Interpreter? = null

    init {
        setupInterpreter()
    }


    private fun setupInterpreter() {
        try {
            val assetFileDescriptor = context.assets.openFd(modelName)
            val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
            val fileChannel = fileInputStream.channel
            val startOffset = assetFileDescriptor.startOffset
            val declaredLength = assetFileDescriptor.declaredLength
            val buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            interpreter = Interpreter(buffer)
        } catch (e: Exception) {
            onFailed("Terjadi Kesalahan: ${e.message}")
            e.printStackTrace()
        }
    }

    fun classifyStaticImage(context: Context, imageUri: Uri) {
        if (interpreter == null) {
            setupInterpreter()
        }

        val bitmap = uriToBitmap(context, imageUri) ?: return
        val inputImage = processImage(bitmap)
        val outputBuffer = ByteBuffer.allocateDirect(4 * 3) // Adjusted for 3 classes
        outputBuffer.order(ByteOrder.nativeOrder())

        interpreter?.run(inputImage, outputBuffer)
        val results = processOutput(outputBuffer)
        generateResult(results)
    }

    private fun processImage(bitmap: Bitmap): ByteBuffer {
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(299, 299, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 1f))
            .build()

        val tensorImage = TensorImage.fromBitmap(bitmap)
        val processedImage = imageProcessor.process(tensorImage)

        val inputBuffer =
            ByteBuffer.allocateDirect(4 * 299 * 299 * 3) // Float size * width * height * channels
        inputBuffer.order(ByteOrder.nativeOrder())
        inputBuffer.put(processedImage.buffer)
        return inputBuffer
    }

    private fun processOutput(outputBuffer: ByteBuffer): List<Pair<String, Float>> {
        outputBuffer.rewind()
        val results = FloatArray(3) // Adjusted for 3 classes
        outputBuffer.asFloatBuffer().get(results)
        val resultPairs = mutableListOf<Pair<String, Float>>()
        for (i in results.indices) {
            resultPairs.add(Pair("Label $i", results[i]))
        }
        return resultPairs.sortedByDescending { it.second }.take(maxResults)
    }

    private fun generateResult(data: List<Pair<String, Float>>?) {
        data?.let {
            if (it.isNotEmpty()) {
                val highestResult = it.maxBy { it.second }
                val label = highestResult?.first ?: "Unknown"
                val confidenceScore =
                    NumberFormat.getPercentInstance().format(highestResult?.second ?: 0.0f)
                onSuccess(label, confidenceScore)
            } else {
                onFailed("No results found")
            }
        }
    }

    private fun uriToBitmap(context: Context, imageUri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(imageUri)?.use { input ->
                BitmapFactory.decodeStream(input)
            }
        } catch (e: Exception) {
            null
        }
    }
}