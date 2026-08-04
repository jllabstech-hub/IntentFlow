package com.intentflow.sdk

import com.intentflow.core.model.DomainDefinition
import com.intentflow.core.model.IntentDefinition
import org.junit.Assert.assertEquals
import org.junit.Test

class IntentFlowSdkTest {

    @Test
    fun testRegisterDomainAndIntent() {
        val domain = DomainDefinition("test_domain", "Test Domain", "Test Description")
        val intent = IntentDefinition("test.intent", "Test Intent", "Desc", "test_domain")

        IntentFlowSdk.registerDomain(domain)
        IntentFlowSdk.registerIntent(intent)

        assertEquals(1, IntentFlowSdk.getRegisteredDomainsCount())
        assertEquals(1, IntentFlowSdk.getRegisteredIntentsCount())
    }
}
