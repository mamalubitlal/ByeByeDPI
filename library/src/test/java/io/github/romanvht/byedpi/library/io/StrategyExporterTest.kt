package io.github.romanvht.byedpi.library.io

import io.github.romanvht.byedpi.library.data.Strategy
import org.junit.Assert.*
import org.junit.Test

class StrategyExporterTest {

    @Test
    fun exportStrategiesToJson_producesValidJson() {
        val strategies = listOf(
            Strategy(command = "-f-200", name = "Strategy 1"),
            Strategy(command = "-d1", name = "Strategy 2")
        )
        
        val json = StrategyExporter.exportStrategiesToJson(strategies)
        
        assertTrue(json.contains("-f-200"))
        assertTrue(json.contains("Strategy 1"))
    }

    @Test
    fun exportStrategiesToText_producesTextWithNames() {
        val strategies = listOf(
            Strategy(command = "-f-200", name = "Test Strategy")
        )
        
        val text = StrategyExporter.exportStrategiesToText(strategies)
        
        assertTrue(text.contains("# Test Strategy"))
        assertTrue(text.contains("-f-200"))
    }

    @Test
    fun importStrategiesFromJson_parsesValidJson() {
        val json = """[
            {"command": "-f-200", "name": "Strategy 1", "description": "Test"},
            {"command": "-d1", "name": "Strategy 2"}
        ]"""
        
        val strategies = StrategyExporter.importStrategiesFromJson(json)
        
        assertEquals(2, strategies.size)
        assertEquals("-f-200", strategies[0].command)
        assertEquals("Strategy 1", strategies[0].name)
    }

    @Test
    fun importStrategiesFromJson_returnsEmptyOnInvalid() {
        val invalid = """{not valid json"""
        
        val strategies = StrategyExporter.importStrategiesFromJson(invalid)
        
        assertTrue(strategies.isEmpty())
    }

    @Test
    fun importStrategiesFromText_parsesCommands() {
        val text = """
            # Strategy 1
            -f-200
            # Strategy 2
            -d1
        """.trimIndent()
        
        val strategies = StrategyExporter.importStrategiesFromText(text)
        
        assertEquals(2, strategies.size)
        assertEquals("Strategy 1", strategies[0].name)
    }

    @Test
    fun importStrategiesFromText_ignoresComments() {
        val text = """
            # This is a comment
            -f-200
            # Another comment
        """.trimIndent()
        
        val strategies = StrategyExporter.importStrategiesFromText(text)
        
        assertEquals(1, strategies.size)
    }

    @Test
    fun importStrategiesFromText_handlesEmptyLines() {
        val text = """
            -f-200

            -d1
        """.trimIndent()
        
        val strategies = StrategyExporter.importStrategiesFromText(text)
        
        assertEquals(2, strategies.size)
    }

    @Test
    fun exportSiteListsToJson_producesValidJson() {
        val lists = listOf(
            io.github.romanvht.byedpi.library.data.SiteList(
                id = "test",
                name = "Test",
                domains = listOf("example.com")
            )
        )
        
        val json = StrategyExporter.exportSiteListsToJson(lists)
        
        assertTrue(json.contains("test"))
        assertTrue(json.contains("example.com"))
    }

    @Test
    fun exportSiteListsToText_producesValidText() {
        val lists = listOf(
            io.github.romanvht.byedpi.library.data.SiteList(
                id = "test",
                name = "Test",
                domains = listOf("example.com"),
                isActive = true
            )
        )
        
        val text = StrategyExporter.exportSiteListsToText(lists)
        
        assertTrue(text.contains("# Test"))
        assertTrue(text.contains("example.com"))
    }

    @Test
    fun exportResultsToJson_includesSuccessCount() {
        val strategies = listOf(
            Strategy(
                command = "-f-200",
                name = "Test",
                successCount = 7,
                totalRequests = 10
            )
        )
        
        val json = StrategyExporter.exportResultsToJson(strategies)
        
        assertTrue(json.contains("successCount"))
        assertTrue(json.contains("7"))
    }

    @Test
    fun exportResultsToCsv_producesCsvFormat() {
        val strategies = listOf(
            Strategy(
                command = "-f-200",
                name = "Test",
                successCount = 5,
                totalRequests = 10
            )
        )
        
        val csv = StrategyExporter.exportResultsToCsv(strategies)
        
        assertTrue(csv.contains("Strategy"))
        assertTrue(csv.contains("Success %"))
        assertTrue(csv.contains("Test"))
    }
}