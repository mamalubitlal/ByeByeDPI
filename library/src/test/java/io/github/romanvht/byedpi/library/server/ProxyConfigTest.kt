package io.github.romanvht.byedpi.library.server

import org.junit.Assert.*
import org.junit.Test

class ProxyConfigTest {

    @Test
    fun defaultConfig_hasExpectedValues() {
        val config = ProxyConfig.DEFAULT
        
        assertEquals("127.0.0.1", config.ip)
        assertEquals(1080, config.port)
        assertFalse(config.httpConnect)
        assertTrue(config.resolveDns)
        assertTrue(config.ipv6)
        assertFalse(config.debug)
    }

    @Test
    fun buildArgs_withDefaultConfig_returnsBasicArgs() {
        val config = ProxyConfig()
        val args = config.buildArgs()
        
        assertTrue(args.isNotEmpty())
        assertEquals("ciadpi", args[0])
    }

    @Test
    fun buildArgs_withCustomIPAndPort() {
        val config = ProxyConfig(ip = "192.168.1.1", port = 3128)
        val args = config.buildArgs()
        
        assertTrue(args.any { it.startsWith("-i192.168.1.1") })
        assertTrue(args.any { it.startsWith("-p3128") })
    }

    @Test
    fun buildArgs_withHttpConnect() {
        val config = ProxyConfig(httpConnect = true)
        val args = config.buildArgs()
        
        assertTrue(args.contains("-G"))
    }

    @Test
    fun buildArgs_withDebug() {
        val config = ProxyConfig(debug = true)
        val args = config.buildArgs()
        
        assertTrue(args.contains("-d"))
    }

    @Test
    fun buildArgs_withCustomArgs() {
        val config = ProxyConfig(
            customArgs = "-f-200 -Qr",
            useCustomCommand = true
        )
        val args = config.buildArgs()
        
        assertTrue(args.contains("ciadpi"))
        assertTrue(args.contains("-f-200"))
        assertTrue(args.contains("-Qr"))
    }

    @Test
    fun fromCommand_parsesIPAndPort() {
        val config = ProxyConfig.fromCommand("-i192.168.1.1 -p3000 -G")
        
        assertEquals("192.168.1.1", config.ip)
        assertEquals(3000, config.port)
        assertTrue(config.httpConnect)
    }

    @Test
    fun fromCommand_withMinimalCommand() {
        val config = ProxyConfig.fromCommand("-f-200")
        
        assertEquals("127.0.0.1", config.ip)
        assertEquals(1080, config.port)
        assertFalse(config.httpConnect)
    }

    @Test
    fun address_returnsIpAndPort() {
        val config = ProxyConfig(ip = "10.0.0.1", port = 8080)
        
        assertEquals("10.0.0.1:8080", config.address)
    }

    @Test
    fun buildArgs_withHosts_blacklistMode() {
        val config = ProxyConfig(
            hostsMode = HostsMode.BLACKLIST,
            hosts = "example.com\ntest.com"
        )
        val args = config.buildArgs()
        
        assertTrue(args.any { it.contains("-H") })
        assertTrue(args.any { it.contains("-An") })
    }

    @Test
    fun buildArgs_withHosts_whitelistMode() {
        val config = ProxyConfig(
            hostsMode = HostsMode.WHITELIST,
            hosts = "allowed.com"
        )
        val args = config.buildArgs()
        
        assertTrue(args.any { it.contains("-H") })
        assertTrue(args.any { it.contains("-Kt,h") })
    }
}