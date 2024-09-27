package com.smh.player.manager

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class VideoInfoModel(
    val uri: Uri,
    val name: String,
    val duration: Int,
    val size: Int,
    val parentFolder: String,
    val thumbnail: String = ""
) {
    companion object {
        val examples = listOf(
            VideoInfoModel(
                uri = "VideoName1".toUri(),
                name = "Video Name",
                size = 1024 * 1024 * 8,
                thumbnail = "thumbnail",
                duration = 10,
                parentFolder = "Folder Name"
            ),
            VideoInfoModel(
                uri = "VideoName2".toUri(),
                name = "Video Name 2",
                size = 1024 * 1024 * 16,
                thumbnail = "thumbnail",
                duration = 10,
                parentFolder = "Folder Name"
            )
        )
    }
}


class FileManager(private val context: Context) {
    suspend fun getVideos(): Map<String, List<VideoInfoModel>> {
        return suspendCoroutine { continuation ->
            val contentResolver = context.contentResolver

            val videoInfoModelList = mutableListOf<VideoInfoModel>()

            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATA
            )

            contentResolver.query(collection, projection, null, null, null)?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val duration = cursor.getInt(durationColumn)
                    val size = cursor.getInt(sizeColumn)
                    val filePath = cursor.getString(dataColumn)
                    val parentFolder = File(filePath).parentFile?.name ?: "Unknown"
                    val contentUri =
                        ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)

                    videoInfoModelList += VideoInfoModel(
                        contentUri,
                        name,
                        duration,
                        size,
                        parentFolder
                    )
                }
            }

            continuation.resume(videoInfoModelList.groupBy { it.parentFolder })
        }
    }
}