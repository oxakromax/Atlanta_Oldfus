package servidor.filter

internal class IpInstance {
    /**
     * Declaring config *
     */
    var connections = 0
        private set
    var lastConnection: Long = 0
        private set
    var isBanned = false
    var timebanned: Long = 0

    fun addConnection() {
        connections++
    }

    fun resetConnections() {
        connections = 0
    }

    fun updateLastConnection() {
        lastConnection = System.currentTimeMillis()
    }

    fun ban() {
        timebanned = System.currentTimeMillis() + 60000
        isBanned = true
    }

}