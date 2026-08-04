package com.intentflow.provider.gemma

import android.content.Context
import com.intentflow.core.common.dispatcher.DefaultDispatcherProvider
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class GemmaModelManagerTest {

    private val context: Context = mockk()
    private val tempDir = File(System.getProperty("java.io.tmpdir"), "intentflow_gemma_test")

    private lateinit var modelManager: GemmaModelManager

    @Before
    fun setup() {
        tempDir.mkdirs()
        every { context.filesDir } returns tempDir
        modelManager = GemmaModelManager(context, DefaultDispatcherProvider())
    }

    @Test
    fun testInitialStatusNotDownloaded() {
        modelManager.modelFile.delete()
        assertFalse(modelManager.isModelDownloaded())
    }

    @Test
    fun testStartDownloadAndDeletion() = runBlocking {
        modelManager.startDownload()

        assertTrue(modelManager.isModelDownloaded())
        assertTrue(modelManager.downloadState.value is ModelDownloadState.Downloaded)

        val deleted = modelManager.deleteModel()
        assertTrue(deleted)
        assertFalse(modelManager.isModelDownloaded())
        assertTrue(modelManager.downloadState.value is ModelDownloadState.NotDownloaded)
    }
}
