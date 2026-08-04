package com.intentflow.catalog.distribution

import com.intentflow.catalog.api.CatalogRepository
import com.intentflow.core.model.CatalogSwitchResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CatalogManagerTest {

    private val catalogRepository: CatalogRepository = mockk(relaxed = true)
    private lateinit var catalogManager: CatalogManager

    @Before
    fun setup() {
        coEvery { catalogRepository.loadCatalog(any()) } returns true
        catalogManager = DefaultCatalogManager(catalogRepository)
    }

    @Test
    fun testCatalogManagerInitialState() = runBlocking {
        val installed = catalogManager.listInstalledCatalogs()

        assertEquals(1, installed.size)
        assertEquals(1, installed.first().versionCode)
        assertTrue(installed.first().isActive)
    }

    @Test
    fun testDownloadAndSwitchCatalogVersion() = runBlocking {
        val progressValues = catalogManager.downloadCatalog("https://example.com/catalog-v2.zip", "checksum_v2").toList()
        assertEquals(listOf(25, 50, 75, 100), progressValues)

        val installed = catalogManager.listInstalledCatalogs()
        assertEquals(2, installed.size)

        val switchResult = catalogManager.switchCatalog(2)
        assertEquals(CatalogSwitchResult.SUCCESS, switchResult)
        assertEquals(2, catalogManager.activeManifest.value?.versionCode)
    }
}
