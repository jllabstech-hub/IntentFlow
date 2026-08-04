package com.intentflow.core.model

import kotlinx.serialization.Serializable

/**
 * Category of executor providing system capabilities.
 */
@Serializable
enum class CapabilityProviderType {
    ANDROID_PLUGIN,
    AI_PROVIDER,
    REST_API,
    DEEP_LINK,
    EXTENSION
}

/**
 * Descriptor representing a capability registered with the Capability Registry.
 */
@Serializable
data class CapabilityDescriptor(
    val capabilityId: String,
    val providerType: CapabilityProviderType,
    val supportedIntentIds: List<String>,
    val displayName: String,
    val isOfflineCapable: Boolean = true,
    val priority: Int = 100,
    val requiredPermissions: List<String> = emptyList(),
    val metadata: Map<String, String> = emptyMap()
)
