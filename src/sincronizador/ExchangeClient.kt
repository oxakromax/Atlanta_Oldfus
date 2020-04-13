package sincronizador

import ch.qos.logback.classic.Logger
import estaticos.AtlantaMain
import estaticos.AtlantaMain.IP_MULTISERVIDOR
import estaticos.AtlantaMain.PUERTO_SINCRONIZADOR
import estaticos.Constantes
import estaticos.Mundo
import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.future.ConnectFuture
import org.apache.mina.core.service.IoConnector
import org.apache.mina.core.session.IoSession
import org.apache.mina.transport.socket.nio.NioSocketConnector
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets

class ExchangeClient private constructor() {
    companion object {
        @JvmField
        var logger =
            LoggerFactory.getLogger(ExchangeClient::class.java) as Logger

        @JvmField
        var INSTANCE: ExchangeClient? = null
        private fun StringToIoBuffer(packet: String): IoBuffer {
            val ioBuffer = IoBuffer.allocate(30000)
            ioBuffer.put(packet.toByteArray())
            return ioBuffer.flip()
        }


        init {
            INSTANCE = ExchangeClient()
        }
    }

    private val _IP: String = IP_MULTISERVIDOR[AtlantaMain.INDEX_IP]
    private val _puerto: Int = PUERTO_SINCRONIZADOR
    private var ioSession: IoSession? = null
    private var connectFuture: ConnectFuture? = null
    private var ioConnector: IoConnector? = null
    fun setIoSession(ioSession: IoSession?) {
        this.ioSession = ioSession
    }

    private fun init() {
        AtlantaMain.ES_LOCALHOST = _IP == "127.0.0.1"
        AtlantaMain.INDEX_IP = (AtlantaMain.INDEX_IP + 1) % IP_MULTISERVIDOR.size
        if (AtlantaMain.MOSTRAR_SINCRONIZACION) {
            println("INTENTO SINCRONIZAR IP: $_IP - PUERTO: $_puerto")
        }
        ioConnector = null
        ioConnector = NioSocketConnector()
        ioConnector?.handler = ExchangeHandler()
        ioConnector?.connectTimeoutMillis = 1000
    }

    fun start(): Boolean {
        connectFuture = try {
            ioConnector?.connect(
                InetSocketAddress(
                    IP_MULTISERVIDOR[0],
                    PUERTO_SINCRONIZADOR
                )
            )
        } catch (e: Exception) {
            logger.error("Can't find login server : ", e)
            return false
        }
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        if (connectFuture?.isConnected != true) {
            logger.error("Can't connect to login server")
            return false
        }
        logger.info(
            "Exchange client connected on address : {},{}",
            IP_MULTISERVIDOR,
            PUERTO_SINCRONIZADOR
        )
        send(
            "D" + AtlantaMain.SERVIDOR_ID + ";" + AtlantaMain.PUERTO_SERVIDOR + ";"
                    + AtlantaMain.SERVIDOR_PRIORIDAD + ";" + Mundo.SERVIDOR_ESTADO
                    + if (AtlantaMain.IP_PUBLICA_SERVIDOR.isEmpty()) "" else ";" + AtlantaMain.IP_PUBLICA_SERVIDOR
        )
        return true
    }

    fun stop() {
        ioSession?.closeNow()
        connectFuture?.cancel()
        connectFuture = null
        ioConnector?.handler = null
        ioConnector?.dispose(true)
        logger.info("Exchange client was stopped.")
    }

    fun restart() {
        if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_ONLINE) {
            stop()
            init()
            while (!INSTANCE!!.start()) {
                try {
                    Thread.sleep(5000)
                } catch (ignored: InterruptedException) {
                }
            }
        }
    }

    fun send(packet: String) {
        try {
            ioSession?.write(
                StringToIoBuffer(
                    String(packet.toByteArray(), StandardCharsets.UTF_8)
                )
            )
        } catch (e: Exception) {
        }
    }

    init {
        init()
    }
}