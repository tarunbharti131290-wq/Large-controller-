package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.ActionTarget
import com.example.model.ActionType
import com.example.model.MemoryType
import com.example.service.SaraBrainEngine
import com.example.service.SaraMemoryVault
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Large Controller", appName)
    }

    @Test
    fun `sara parses wifi toggle action`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val vault = SaraMemoryVault(context)
        val engine = SaraBrainEngine()
        val response = engine.processCommand("Turn on Wi-Fi", currentVolume = 50, currentBattery = 90, memoryVault = vault)
        assertNotNull(response.action)
        assertEquals(ActionType.SETTING_TOGGLE, response.action?.type)
        assertEquals(ActionTarget.WIFI, response.action?.target)
        assertEquals("ON", response.action?.state)
    }

    @Test
    fun `sara parses launch app action`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val vault = SaraMemoryVault(context)
        val engine = SaraBrainEngine()
        val response = engine.processCommand("Launch Camera", currentVolume = 50, currentBattery = 90, memoryVault = vault)
        assertNotNull(response.action)
        assertEquals(ActionType.LAUNCH_APP, response.action?.type)
        assertEquals("CAMERA", response.action?.targetAppName)
    }

    @Test
    fun `sara learning loop and mango rule`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val vault = SaraMemoryVault(context)
        vault.clearAllMemories()
        val engine = SaraBrainEngine()

        // 1. Ask about Mango before teaching - SARA states knowledge gap and invites user to guide
        val gapResponse = engine.processCommand("What is a mango?", currentVolume = 50, currentBattery = 90, memoryVault = vault)
        assertTrue(gapResponse.displayText.contains("Mangifera indica"))
        assertTrue(gapResponse.displayText.contains("teach me", ignoreCase = true))

        // 2. User teaches SARA: "Mango is the king of fruits"
        val teachResponse = engine.processCommand("Mango is the king of fruits", currentVolume = 50, currentBattery = 90, memoryVault = vault)
        assertNotNull(teachResponse.memoryItem)
        assertEquals("Mango", teachResponse.memoryItem?.concept)
        assertEquals(MemoryType.KNOWLEDGE_TEACHING, teachResponse.memoryItem?.memoryType)
        assertTrue(teachResponse.displayText.contains("king of fruits"))

        // 3. Ask about Mango after teaching - SARA recalls user context seamlessly
        val recallResponse = engine.processCommand("Tell me what you know about mango", currentVolume = 50, currentBattery = 90, memoryVault = vault)
        assertTrue(recallResponse.displayText.contains("king of fruits"))
    }

    @Test
    fun `sara absorbs personal experiences with empathy`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val vault = SaraMemoryVault(context)
        val engine = SaraBrainEngine()

        val response = engine.processCommand("I fell down and hurt my knee", currentVolume = 50, currentBattery = 90, memoryVault = vault)
        assertNotNull(response.memoryItem)
        assertEquals(MemoryType.PERSONAL_EVENT, response.memoryItem?.memoryType)
        assertTrue(response.displayText.contains("sorry", ignoreCase = true))
        assertTrue(response.displayText.contains("rest", ignoreCase = true) || response.displayText.contains("first aid", ignoreCase = true))
    }

    @Test
    fun `sara parses background execute action`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val vault = SaraMemoryVault(context)
        val engine = SaraBrainEngine()

        val response = engine.processCommand("Start background daemon to monitor device", currentVolume = 50, currentBattery = 90, memoryVault = vault)
        assertNotNull(response.action)
        assertEquals(ActionType.BACKGROUND_EXECUTE, response.action?.type)
        assertEquals(ActionTarget.BACKGROUND_DAEMON, response.action?.target)
        assertTrue(response.action?.taskDescription?.isNotBlank() == true)
        assertTrue(response.displayText.contains("BACKGROUND_EXECUTE"))
    }

    @Test
    fun `sara parses security audit and ethical hacking action`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val vault = SaraMemoryVault(context)
        val engine = SaraBrainEngine()

        val response = engine.processCommand("Run cybersecurity port scan with nmap", currentVolume = 50, currentBattery = 90, memoryVault = vault)
        assertNotNull(response.action)
        assertEquals(ActionType.SECURITY_AUDIT, response.action?.type)
        assertEquals(ActionTarget.CYBERSECURITY, response.action?.target)
        assertEquals("PORTS", response.action?.auditParameter)
        assertTrue(response.displayText.contains("SECURITY_AUDIT"))
    }

    @Test
    fun `sara parses mobile data toggle action`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val vault = SaraMemoryVault(context)
        val engine = SaraBrainEngine()

        val response = engine.processCommand("Turn on mobile data", currentVolume = 50, currentBattery = 90, memoryVault = vault)
        assertNotNull(response.action)
        assertEquals(ActionType.SETTING_TOGGLE, response.action?.type)
        assertEquals(ActionTarget.DATA, response.action?.target)
        assertEquals("ON", response.action?.state)
    }
}

