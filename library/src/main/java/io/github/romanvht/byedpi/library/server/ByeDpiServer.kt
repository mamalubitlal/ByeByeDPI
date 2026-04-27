package io.github.romanvht.byedpi.library.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Manages the ByeDPI proxy server lifecycle
 * 
 * This class provides a pure Kotlin wrapper for managing the ByeDPI proxy.
 * Since the native library is part of the consuming app, this class uses
 * a provider pattern to delegate actual start/stop operations.
 * 
 * Example usage:
 * ```
 * val server = ByeDpiServer()
 * 
 * // Set the native proxy controller (provided by your app)
 * server.setProxyController(object : ProxyController {
 *     override suspend fun startProxy(args: Array<String>): Int {
 *         return YourNativeProxy.start(args)
 *     }
 *     override suspend fun stopProxy(): Int {
 *         return YourNativeProxy.stop()
 *     }
 * })
 * 
 * // Start with config
 * server.start(ProxyConfig.DEFAULT)
 * 
 * // Stop when done
 * server.stop()
 * ```
 */
class ByeDpiServer {

    private val _status = MutableStateFlow(ServerStatus.STOPPED)
    val status: StateFlow<ServerStatus> = _status.asStateFlow()

    private val _config = MutableStateFlow<ProxyConfig?>(null)
    val config: StateFlow<ProxyConfig?> = _config.asStateFlow()

    private var proxyController: ProxyController? = null
    private var eventListener: ServerEventListener? = null

    /**
     * Whether the server is currently running
     */
    val isRunning: Boolean get() = _status.value == ServerStatus.RUNNING

    /**
     * Current server status
     */
    val currentStatus: ServerStatus get() = _status.value

    /**
     * Current configuration (if running)
     */
    val currentConfig: ProxyConfig? get() = _config.value

    /**
     * Set the proxy controller that handles native operations
     */
    fun setProxyController(controller: ProxyController) {
        proxyController = controller
    }

    /**
     * Set event listener for server events
     */
    fun setEventListener(listener: ServerEventListener?) {
        eventListener = listener
    }

    /**
     * Start the proxy server
     * 
     * @param config Proxy configuration
     * @return Result of the start operation
     */
    suspend fun start(config: ProxyConfig = ProxyConfig.DEFAULT): ServerResult = withContext(Dispatchers.IO) {
        if (isRunning) {
            return@withContext ServerResult.Error(-1, "Server is already running")
        }

        val controller = proxyController
        if (controller == null) {
            return@withContext ServerResult.Error(
                -1,
                "No ProxyController set. Call setProxyController() before starting the server."
            )
        }

        updateStatus(ServerStatus.STARTING)
        _config.value = config

        try {
            val args = config.buildArgs()
            val result = controller.startProxy(args)

            if (result == 0) {
                updateStatus(ServerStatus.RUNNING)
                eventListener?.onServerStarted(config)
                ServerResult.Success("Server started on ${config.address}")
            } else {
                updateStatus(ServerStatus.ERROR)
                eventListener?.onServerError(result, "Failed to start proxy (code: $result)")
                ServerResult.Error(result, "Failed to start proxy (code: $result)")
            }
        } catch (e: Exception) {
            updateStatus(ServerStatus.ERROR)
            eventListener?.onServerError(-1, e.message ?: "Unknown error")
            ServerResult.Error(-1, e.message ?: "Failed to start server")
        }
    }

    /**
     * Stop the proxy server
     * 
     * @return Result of the stop operation
     */
    suspend fun stop(): ServerResult = withContext(Dispatchers.IO) {
        if (!isRunning) {
            return@withContext ServerResult.Error(-1, "Server is not running")
        }

        updateStatus(ServerStatus.STOPPING)

        try {
            val controller = proxyController
            controller?.stopProxy()

            updateStatus(ServerStatus.STOPPED)
            eventListener?.onServerStopped("Normal shutdown")
            _config.value = null

            ServerResult.Success("Server stopped")
        } catch (e: Exception) {
            updateStatus(ServerStatus.ERROR)
            ServerResult.Error(-1, e.message ?: "Failed to stop server")
        }
    }

    /**
     * Restart the server with new configuration
     * 
     * @param config New configuration (null to use current)
     * @return Result of the restart operation
     */
    suspend fun restart(config: ProxyConfig? = null): ServerResult {
        val current = _config.value
        stop()
        return start(config ?: current ?: ProxyConfig.DEFAULT)
    }

    /**
     * Check if the server is responsive
     */
    fun ping(): Boolean {
        val host = _config.value?.ip
        val port = _config.value?.port
        if (host.isNullOrEmpty() || port == null || port <= 0) {
            return false
        }
        return try {
            val socket = java.net.Socket()
            socket.connect(
                java.net.InetSocketAddress(host, port),
                1000
            )
            socket.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun updateStatus(newStatus: ServerStatus) {
        val oldStatus = _status.value
        _status.value = newStatus
        eventListener?.onStatusChanged(oldStatus, newStatus)
    }
}

/**
 * Interface for controlling the native proxy.
 * Implement this in your app to bridge with native code.
 * 
 * Example:
 * ```
 * class NativeProxyController(private val proxy: ByeDpiProxy) : ProxyController {
 *     override suspend fun startProxy(args: Array<String>): Int {
 *         return proxy.startProxy(ByeDpiProxyCmdPreferences(args))
 *     }
 *     override suspend fun stopProxy(): Int {
 *         return proxy.stopProxy()
 *     }
 * }
 * ```
 */
interface ProxyController {
    /**
     * Start the native proxy with given arguments
     * @return 0 on success, non-zero on failure
     */
    suspend fun startProxy(args: Array<String>): Int

    /**
     * Stop the native proxy
     * @return 0 on success, non-zero on failure
     */
    suspend fun stopProxy(): Int
}
