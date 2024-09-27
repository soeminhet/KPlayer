package com.smh.player.manager

import android.content.Context
import android.net.Uri
import android.util.Log
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.smh.design.util.URIPathHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FFMPEGManager(
    private val context: Context,
) {
    suspend fun checkCacheAndExtractFFMPEG(
        uri: Uri,
        isHardwareAccelerationSupported: Boolean
    ): String = withContext(Dispatchers.IO) {
        val path = URIPathHelper.getPath(context, uri) ?: return@withContext ""
        val file = File(path)
        val name = file.nameWithoutExtension
        val tempFileName = "$name.jpg"
        val tempFile = File(context.cacheDir, tempFileName)
        if (tempFile.exists()) return@withContext tempFile.absolutePath
        else return@withContext extractWithFFMPEG(path, tempFileName, isHardwareAccelerationSupported)
    }

    suspend fun checkHardwareAccelerationSupport(): Boolean = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            val command = arrayOf("-hwaccels")
            FFmpeg.executeAsync(command) { _, returnCode ->
                if (returnCode == Config.RETURN_CODE_SUCCESS) {
                    Log.i("FF_MPEG", "Hardware acceleration methods supported on this device.")
                    continuation.resume(true)
                } else {
                    Log.e("FF_MPEG", "Failed to query hardware acceleration methods.")
                    continuation.resume(false)
                }
            }
        }
    }

    private suspend fun extractWithFFMPEG(
        path: String,
        fileNameWithExtension: String,
        isHardwareAccelerationSupported: Boolean,
    ): String = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            // Use a temporary file for the extracted frame
            var tempFile = File(context.cacheDir, fileNameWithExtension)
            if (tempFile.exists()) {
                tempFile.delete()
                tempFile = File(context.cacheDir, fileNameWithExtension)
            }
            val outputImagePath = tempFile.absolutePath

            // Extract frame at 1 seconds
            val timePosition = "00:00:01"

            val normalCommand = arrayOf(
                "-ss", timePosition,      // Seek before decoding
                "-i", path,               // Input video path
                "-vframes", "1",          // Extract 1 frame
                "-q:v", "2",              // Quality of the output image (2 is high quality)
                outputImagePath           // Output image path
            )

            val accelCommand = arrayOf(
                "-hwaccel", "mediacodec", // Use hardware acceleration (mediacodec for Android)
                "-ss", timePosition,      // Seek before decoding
                "-i", path,               // Input video path
                "-vframes", "1",          // Extract 1 frame
                "-q:v", "2",              // Quality of the output image (2 is high quality)
                outputImagePath           // Output image path
            )

            val command = if (isHardwareAccelerationSupported) accelCommand else normalCommand

            FFmpeg.executeAsync(command) { _, returnCode ->
                if (returnCode == Config.RETURN_CODE_SUCCESS) {
                    Log.i("FF_MPEG", "Frame extraction successful")
                    continuation.resume(outputImagePath)
                } else {
                    Log.e("FF_MPEG", "Frame extraction failed with returnCode: $returnCode")
                    continuation.resume("")
                }
            }
        }
    }
}