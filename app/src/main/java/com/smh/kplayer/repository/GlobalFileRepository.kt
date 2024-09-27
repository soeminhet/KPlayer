package com.smh.kplayer.repository

import com.smh.player.manager.FileManager
import com.smh.player.manager.VideoInfoModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface GlobalFileRepository {
    val rootVideoFolderNamesAndCount: StateFlow<List<Pair<String, Int>>>
    suspend fun initLoad()
    suspend fun getRootVideoFolderNamesAndCount(forceRefresh: Boolean = false): List<Pair<String, Int>>
    suspend fun getVideosInFolder(folderName: String, forceRefresh: Boolean = false): List<VideoInfoModel>
}

class GlobalFileRepositoryImpl @Inject constructor(
    private val fileManager: FileManager
): GlobalFileRepository {
    private var videoFiles: Map<String, List<VideoInfoModel>> = emptyMap()

    private val _rootVideoFolderNamesAndCount: MutableStateFlow<List<Pair<String, Int>>> = MutableStateFlow(emptyList())
    override val rootVideoFolderNamesAndCount: StateFlow<List<Pair<String, Int>>> = _rootVideoFolderNamesAndCount.asStateFlow()

    override suspend fun initLoad() {
        if (videoFiles.isEmpty()) {
            loadVideos()
        }
    }

    override suspend fun getRootVideoFolderNamesAndCount(forceRefresh: Boolean): List<Pair<String, Int>> {
        if (forceRefresh || videoFiles.isEmpty()) {
            loadVideos()
        }
        return videoFiles.map { it.key to it.value.count() }
    }

    override suspend fun getVideosInFolder(folderName: String, forceRefresh: Boolean): List<VideoInfoModel> {
        if (forceRefresh || videoFiles.isEmpty()) {
            loadVideos()
        }
        return videoFiles[folderName] ?: emptyList()
    }

    private suspend fun loadVideos() {
        videoFiles = fileManager.getVideos()
        _rootVideoFolderNamesAndCount.emit(videoFiles.map { it.key to it.value.count() })
    }
}