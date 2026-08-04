package com.intentflow.catalog.runtime

import com.intentflow.core.model.CatalogVersion
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogVersionManagerTest {

    private val versionManager = DefaultCatalogVersionManager()

    @Test
    fun testRegisterVersionSetsHighestVersionAsActive() = runBlocking {
        val v1 = CatalogVersion(1, "1.0.0")
        val v2 = CatalogVersion(2, "2.0.0")

        versionManager.registerVersion(v1, "bundled/catalog_v1.json")
        assertEquals(1, versionManager.activeVersion.value?.versionCode)

        versionManager.registerVersion(v2, "downloads/catalog_v2.json")
        assertEquals(2, versionManager.activeVersion.value?.versionCode)
        assertEquals("downloads/catalog_v2.json", versionManager.getActiveVersionPath())
    }

    @Test
    fun testSwitchActiveVersion() = runBlocking {
        val v1 = CatalogVersion(1, "1.0.0")
        val v2 = CatalogVersion(2, "2.0.0")

        versionManager.registerVersion(v1, "bundled/v1.json")
        versionManager.registerVersion(v2, "downloads/v2.json")

        val switched = versionManager.setActiveVersion(1)
        assertTrue(switched)
        assertEquals(1, versionManager.activeVersion.value?.versionCode)
        assertEquals("bundled/v1.json", versionManager.getActiveVersionPath())
    }
}
