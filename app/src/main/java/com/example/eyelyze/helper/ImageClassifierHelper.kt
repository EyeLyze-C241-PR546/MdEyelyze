package com.example.eyelyze.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.SystemClock
import com.example.eyelyze.R
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.NumberFormat

class ImageClassifierHelper(
    private var threshold: Float = 0.1f,
    private var maxResults: Int = 3,
    private val modelName: String = "modelv2.tflite",
    private val context: Context,
    private val onSuccess: (String, String) -> Unit,
    private val onFailed: (String) -> Unit,
) {
    private var imageClassifier: ImageClassifier? = null

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
        val baseOptionsBuilder = BaseOptions.builder()
            .setNumThreads(4)
        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )
        } catch (e: IllegalStateException) {
            onFailed(context.getString(R.string.image_classifier_failed))
            Timber.tag("classifyHelper").e(e.message.toString())
        }
    }

    private fun normalizeBitmap(bitmap: Bitmap): FloatArray {
        val imageSize = bitmap.width * bitmap.height
        val floatValues = FloatArray(imageSize * 3)
        val intValues = IntArray(imageSize)

        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (i in intValues.indices) {
            val pixel = intValues[i]
            // Convert pixel to float (0 - 255)
            val red = (pixel shr 16 and 0xff) / 255.0f;
            val green = (pixel shr 8 and 0xff) / 255.0f;
            val blue = (pixel and 0xff) / 255.0f;

            // Inception V3 normalization: [-1, 1]
            floatValues[i * 3 + 0] = (red - 0.5f) / 0.5f;
            floatValues[i * 3 + 1] = (green - 0.5f) / 0.5f;
            floatValues[i * 3 + 2] = (blue - 0.5f) / 0.5f;
        }

        return floatValues
    }


    fun classifyStaticImage(context: Context, imageUri: Uri) {
        if (imageClassifier == null) {
            setupImageClassifier()
        }

        val bitmap = uriToBitmap(context,imageUri)
//        val normalizedImage = bitmap?.let { normalizeBitmap(it) }

        val imageProcessor = ImageProcessor.Builder()
//            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
//            .add(CastOp(DataType.UINT8))
//            .add(Rot90Op(-imageRotation / 90))
            .add(NormalizeOp(127.5f, 127.5f))
            .build()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(uriToBitmap(context, imageUri)))


        var inferenceTime = SystemClock.uptimeMillis()
        val results = imageClassifier?.classify(tensorImage)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        generateResult(results)
    }

    private fun generateResult(data: List<Classifications>?) {
        data?.let { it ->
            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                println(it)
                val highestResult =
                    it[0].categories.maxBy {
                        it?.score ?: 0.0f
                    }

                val label = highestResult.label
                val confidenceScore = NumberFormat.getPercentInstance()
                    .format(highestResult.score)

                onSuccess(label, confidenceScore)
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