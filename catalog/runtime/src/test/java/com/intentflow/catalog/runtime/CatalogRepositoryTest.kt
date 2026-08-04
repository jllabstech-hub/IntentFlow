package com.intentflow.catalog.runtime

import com.intentflow.core.common.dispatcher.DefaultDispatcherProvider
import com.intentflow.core.model.CatalogData
import com.intentflow.core.model.CatalogVersion
import com.intentflow.core.model.ExecutionRule
import com.intentflow.core.model.IntentDefinition
import com.intentflow.core.model.PermissionDefinition
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class CatalogRepositoryTest {

    private val catalogLoader: DefaultCatalogLoader = mockk()
    private val catalogCache = ThreadSafeCatalogCache()
    private lateinit var repository: DefaultCatalogRepository

    private val sampleData = CatalogData(
        version = CatalogVersion(1, "1.0.0"),
        intents = listOf(IntentDefinition("messaging.send", "Send SMS", "Desc", "messaging")),
        executionRules = listOf(ExecutionRule("rule_1", "messaging.send", timeoutMs = 3000)),
        permissions = listOf(PermissionDefinition("perm_1", "android.permission.SEND_SMS", "Rationale"))
    )

    @Before
    fun setup() {
        repository = DefaultCatalogRepository(catalogLoader, catalogCache, DefaultDispatcherProvider())
        catalogCache.putCatalogData(sampleData)
    }

    @Test
    fun testExpandedCatalogQueries() = runBlocking {
        val intentDef = repository.getIntentById("messaging.send")
        assertNotNull(intentDef)

        val rule = repository.getExecutionRule("messaging.send")
        assertNotNull(rule)
        assertEquals(3000L, rule?.timeoutMs)

        val perms = repository.getPermissions("perm_1")
        assertEquals(1, perms.size)
        assertEquals("android.permission.SEND_SMS", perms.first().androidPermission)
    }
}
