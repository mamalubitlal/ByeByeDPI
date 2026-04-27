package io.github.romanvht.byedpi.library.data

import org.junit.Assert.*
import org.junit.Test

class SiteCheckResultTest {

    @Test
    fun successPercentage_calculatesCorrectly() {
        val result = SiteCheckResult(
            site = "example.com",
            successCount = 3,
            totalCount = 4
        )
        assertEquals(75, result.successPercentage)
    }

    @Test
    fun successPercentage_zeroOnNoRequests() {
        val result = SiteCheckResult(
            site = "example.com",
            successCount = 0,
            totalCount = 0
        )
        assertEquals(0, result.successPercentage)
    }

    @Test
    fun successPercentage_handlesAllSuccess() {
        val result = SiteCheckResult(
            site = "example.com",
            successCount = 5,
            totalCount = 5
        )
        assertEquals(100, result.successPercentage)
    }
}