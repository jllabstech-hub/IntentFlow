package com.intentflow.presentation.renderer

import org.junit.Assert.assertEquals
import org.junit.Test

class ComposeUiRendererTest {

    private val renderer = ComposeUiRenderer()

    @Test
    fun testComposeUiRendererTargetSurfaceIsMobileCompose() {
        assertEquals(SurfaceTarget.MOBILE_COMPOSE, renderer.targetSurface)
    }
}
