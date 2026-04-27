package io.github.romanvht.byedpi.library.data

import org.junit.Assert.*
import org.junit.Test

class TestConfigTest {

    @Test
    fun defaultConfig_hasExpectedValues() {
        val config = TestConfig.DEFAULT
        
        assertEquals(1, config.delaySeconds)
        assertEquals(1, config.requestsPerSite)
        assertEquals(5L, config.requestTimeoutSeconds)
        assertEquals(20, config.maxConcurrentRequests)
        assertEquals("google.com", config.sniValue)
        assertFalse(config.fullLog)
    }

    @Test
    fun quickConfig_hasShorterTimeouts() {
        val config = TestConfig.QUICK
        
        assertEquals(1, config.delaySeconds)
        assertEquals(1, config.requestsPerSite)
        assertEquals(3L, config.requestTimeoutSeconds)
        assertEquals(30, config.maxConcurrentRequests)
    }

    @Test
    fun thoroughConfig_hasMoreRequests() {
        val config = TestConfig.THOROUGH
        
        assertEquals(3, config.requestsPerSite)
        assertEquals(10L, config.requestTimeoutSeconds)
        assertEquals(10, config.maxConcurrentRequests)
    }

    @Test
    fun customConfig_acceptsAllParameters() {
        val config = TestConfig(
            delaySeconds = 2,
            requestsPerSite = 5,
            requestTimeoutSeconds = 15,
            maxConcurrentRequests = 50,
            sniValue = "custom.com",
            fullLog = true
        )
        
        assertEquals(2, config.delaySeconds)
        assertEquals(5, config.requestsPerSite)
        assertEquals(15L, config.requestTimeoutSeconds)
        assertEquals(50, config.maxConcurrentRequests)
        assertEquals("custom.com", config.sniValue)
        assertTrue(config.fullLog)
    }
}