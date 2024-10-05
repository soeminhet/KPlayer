package com.smh.player.manager

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.smh.design.util.URIPathHelper
import io.github.anilbeesetti.nextlib.mediainfo.MediaInfo
import io.github.anilbeesetti.nextlib.mediainfo.MediaInfoBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FFMPEGManager(
    private val context: Context,
) {
    suspend fun checkCacheAndExtractFFMPEG(
        uri: Uri
    ): String = withContext(Dispatchers.IO) {
        val path = URIPathHelper.getPath(context, uri) ?: return@withContext ""
        val file = File(path)
        val name = file.nameWithoutExtension
        val tempFileName = "$name.png"
        val tempFile = File(context.cacheDir, tempFileName)
        if (tempFile.exists()) return@withContext tempFile.absolutePath
        else return@withContext extractWithFFMPEG(uri, tempFileName)
    }

    private suspend fun extractWithFFMPEG(
        uri: Uri,
        fileNameWithExtension: String,
    ): String = withContext(Dispatchers.IO) {
        val mediaInfo = MediaInfoBuilder().from(context, uri).build() ?: return@withContext ""
        val bitmap = mediaInfo.getFrame() ?: return@withContext ""
        mediaInfo.release()

        var tempFile = File(context.cacheDir, fileNameWithExtension)
        if (tempFile.exists()) {
            tempFile.delete()
            tempFile = File(context.cacheDir, fileNameWithExtension)
        }
        val outputImagePath = tempFile.absolutePath

        try {
            val outputStream = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return@withContext ""
        }

        return@withContext outputImagePath
    }
}