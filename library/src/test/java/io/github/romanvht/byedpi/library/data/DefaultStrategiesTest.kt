package io.github.romanvht.byedpi.library.data

import org.junit.Assert.*
import org.junit.Test

class DefaultStrategiesTest {

    @Test
    fun allStrategies_isNotEmpty() {
        assertTrue(DefaultStrategies.ALL.isNotEmpty())
    }

    @Test
    fun allStrategies_haveValidCommands() {
        for (strategy in DefaultStrategies.ALL) {
            assertTrue(strategy.command.isNotEmpty())
        }
    }

    @Test
    fun getByCategory_sniBased_filtersCorrectly() {
        val strategies = DefaultStrategies.getByCategory(StrategyCategory.SNI_BASED)
        
        assertTrue(strategies.isNotEmpty())
        for (strategy in strategies) {
            assertTrue(strategy.command.contains("{sni}"))
        }
    }

    @Test
    fun getByCategory_fragmentation_filtersCorrectly() {
        val strategies = DefaultStrategies.getByCategory(StrategyCategory.FRAGMENTATION)
        
        assertTrue(strategies.isNotEmpty())
        for (strategy in strategies) {
            assertTrue(strategy.command.contains("-f"))
        }
    }

    @Test
    fun getByCategory_delay_filtersCorrectly() {
        val strategies = DefaultStrategies.getByCategory(StrategyCategory.DELAY)
        
        assertTrue(strategies.isNotEmpty())
        for (strategy in strategies) {
            assertTrue(strategy.command.contains("-d"))
        }
    }

    @Test
    fun getByCategory_allCategories_returnsAll() {
        val strategies = DefaultStrategies.getByCategory(StrategyCategory.ALL_CATEGORIES)
        
        assertEquals(DefaultStrategies.ALL.size, strategies.size)
    }

    @Test
    fun fromString_parsesValidCommands() {
        val content = """
            -f-200 -Qr
            -d1 -s1
            -o1 -a1
        """.trimIndent()
        
        val strategies = DefaultStrategies.fromString(content)
        
        assertEquals(3, strategies.size)
        assertTrue(strategies[0].command.contains("-f-200"))
    }

    @Test
    fun fromString_ignoresComments() {
        val content = """
            # This is a comment
            -f-200
            # Another comment
            -d1
        """.trimIndent()
        
        val strategies = DefaultStrategies.fromString(content)
        
        assertEquals(2, strategies.size)
    }

    @Test
    fun fromString_ignoresEmptyLines() {
        val content = """
            -f-200

            -d1

        """.trimIndent()
        
        val strategies = DefaultStrategies.fromString(content)
        
        assertEquals(2, strategies.size)
    }

    @Test
    fun fromString_assignsCorrectNames() {
        val content = "-f-200"
        
        val strategies = DefaultStrategies.fromString(content)
        
        assertEquals("Strategy 1", strategies[0].name)
    }
}