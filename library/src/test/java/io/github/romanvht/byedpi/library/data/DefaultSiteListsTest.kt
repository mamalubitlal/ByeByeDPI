package io.github.romanvht.byedpi.library.data

import org.junit.Assert.*
import org.junit.Test

class SiteListTest {

    @Test
    fun siteList_defaultValues() {
        val siteList = SiteList(
            id = "test",
            name = "Test Site",
            domains = listOf("example.com")
        )
        
        assertEquals("test", siteList.id)
        assertEquals("Test Site", siteList.name)
        assertEquals(1, siteList.domains.size)
        assertTrue(siteList.isActive)
        assertFalse(siteList.isBuiltIn)
    }

    @Test
    fun withActiveState_returnsCopyWithNewState() {
        val siteList = SiteList(
            id = "test",
            name = "Test",
            domains = listOf("example.com"),
            isActive = true
        )
        
        val inactive = siteList.withActiveState(false)
        
        assertFalse(inactive.isActive)
        assertEquals(siteList.id, inactive.id)
    }
}

class DefaultSiteListsTest {

    @Test
    fun allSiteLists_isNotEmpty() {
        assertTrue(DefaultSiteLists.ALL.isNotEmpty())
    }

    @Test
    fun allSiteLists_haveUniqueIds() {
        val ids = DefaultSiteLists.ALL.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun getActive_returnsOnlyActiveLists() {
        val activeLists = DefaultSiteLists.getActive()
        
        for (list in activeLists) {
            assertTrue(list.isActive)
        }
    }

    @Test
    fun getActiveDomains_returnsDistinctDomains() {
        val domains = DefaultSiteLists.getActiveDomains()
        
        assertTrue(domains.isNotEmpty())
        assertEquals(domains.size, domains.distinct().size)
    }

    @Test
    fun getById_returnsCorrectList() {
        val list = DefaultSiteLists.getById("youtube")
        
        assertNotNull(list)
        assertEquals("youtube", list?.id)
    }

    @Test
    fun getById_returnsNullForUnknown() {
        val list = DefaultSiteLists.getById("unknown_list")
        
        assertNull(list)
    }

    @Test
    fun createCustomList_generatesValidId() {
        val customList = DefaultSiteLists.createCustomList(
            name = "My Custom List",
            domains = listOf("test.com")
        )
        
        assertEquals("my_custom_list", customList.id)
        assertEquals("My Custom List", customList.name)
        assertFalse(customList.isBuiltIn)
    }

    @Test
    fun createCustomList_acceptsActiveState() {
        val customList = DefaultSiteLists.createCustomList(
            name = "Inactive List",
            domains = listOf("test.com"),
            isActive = false
        )
        
        assertFalse(customList.isActive)
    }
}