package io.github.romanvht.byedpi.library.data

import org.junit.Assert.*
import org.junit.Test

class StrategyTest {

    @Test
    fun strategy_successPercentage_calculatesCorrectly() {
        val strategy = Strategy(
            command = "-f-200",
            name = "Test Strategy",
            description = "Test description",
            successCount = 7,
            totalRequests = 10
        )
        assertEquals(70, strategy.successPercentage)
    }

    @Test
    fun strategy_successPercentage_zeroOnNoRequests() {
        val strategy = Strategy(
            command = "-f-200",
            successCount = 0,
            totalRequests = 0
        )
        assertEquals(0, strategy.successPercentage)
    }

    @Test
    fun strategy_reset_clearsResults() {
        val strategy = Strategy(
            command = "-f-200",
            name = "Test",
            successCount = 5,
            totalRequests = 10,
            isCompleted = true
        )
        val reset = strategy.reset()
        
        assertEquals("Test", reset.name)
        assertEquals(0, reset.successCount)
        assertEquals(0, reset.totalRequests)
        assertFalse(reset.isCompleted)
    }

    @Test
    fun strategy_defaultValues_areEmpty() {
        val strategy = Strategy(command = "-f-200")
        
        assertEquals("", strategy.name)
        assertEquals("", strategy.description)
        assertEquals(0, strategy.successCount)
        assertEquals(0, strategy.totalRequests)
        assertFalse(strategy.isCompleted)
    }
}