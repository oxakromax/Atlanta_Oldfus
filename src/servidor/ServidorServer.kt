package servidor

import estaticos.AtlantaMain
import org.apache.mina.core.service.IoAcceptor
import org.apache.mina.core.session.IdleStatus
import org.apache.mina.core.session.IoSession
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.filter.codec.textline.LineDelimiter
import org.apache.mina.filter.codec.textline.TextLineCodecFactory
import org.apache.mina.transport.socket.nio.NioSocketAcceptor
import org.slf4j.LoggerFactory
import sincronizador.ExchangeClient
import variables.personaje.Cuenta
import variables.personaje.Personaje
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.stream.Collectors

class ServidorServer private constructor() {
    companion object {
        private val waitingClients = ArrayList<Cuenta>()
        private val log = LoggerFactory.getLogger(ServidorServer::class.java)
        var MAX_PLAYERS: Short = 700
        var INSTANCE = ServidorServer()
        val _conexiones = IntArray(5)
        private val _IpsClientes: MutableMap<String?, ArrayList<ServidorSocket>> = ConcurrentHashMap()
        private val _cuentasEspera = CopyOnWriteArrayList<Cuenta>()
        private val _IpsEspera = CopyOnWriteArrayList<String>()
        var _j = 0
        var recordJugadores = 0
        var segundosON = 0
        val clientes: List<ServidorSocket>
            get() = INSTANCE.acceptor.managedSessions.values.stream()
                .filter { session: IoSession -> session.attachment != null }
                .map { session: IoSession -> session.attachment as ServidorSocket }
                .collect(Collectors.toList())

        val playersNumberByIp: Int
            get() = clientes.stream()
                .filter { client: ServidorSocket? -> client?.cuenta != null }
                .map { client: ServidorSocket -> client.cuenta?.actualIP }
                .distinct().count().toInt()

        @JvmStatic
        fun getAndDeleteWaitingAccount(id: Int): Cuenta? {
            val it: MutableIterator<Cuenta> = waitingClients.listIterator()
            while (it.hasNext()) {
                val account = it.next()
                if (account.id == id) {
                    it.remove()
                    return account
                }
            }
            return null
        }

        fun addWaitingAccount(account: Cuenta) {
            if (!waitingClients.contains(account)) waitingClients.add(account)
        }

        @JvmStatic
        fun a() {
            log.warn("Unexpected behaviour detected")
        }


        fun getCliente(b: Int): ServidorSocket {
            return clientes[b]
        }

        private fun addIPsClientes(s: ServidorSocket) {
            val ip = s.actualIP
            _IpsClientes.computeIfAbsent(ip) { k: String? -> ArrayList() }
            if (!_IpsClientes[ip]!!.contains(s)) {
                _IpsClientes[ip]!!.add(s)
            }
        }

        private fun borrarIPsClientes(s: ServidorSocket) {
            val ip = s.actualIP
            if (_IpsClientes[ip] == null) {
                return
            }
            _IpsClientes[ip]!!.remove(s)
        }

        fun getIPsClientes(ip: String?): Int {
            return if (_IpsClientes[ip] == null) {
                0
            } else _IpsClientes[ip]!!.size
        }

        fun nroJugadoresLinea(): Int {
            return clientes.size
        }

        fun actualizarMaxJugadoresEnLinea() {
            if (recordJugadores < nroJugadoresLinea()) {
                recordJugadores = nroJugadoresLinea()
            }
        }

        fun delEsperandoCuenta(cuenta: Cuenta?) {
            if (cuenta == null) {
                return
            }
            _cuentasEspera.remove(cuenta)
        }

        fun addEsperandoCuenta(cuenta: Cuenta?) {
            _cuentasEspera.remove(cuenta)
            if (cuenta != null) {
                _cuentasEspera.add(cuenta)
            }
        }

        fun getEsperandoCuenta(id: Int): Cuenta? {
            for (cuenta in _cuentasEspera) {
                if (cuenta.id == id) {
                    return cuenta
                }
            }
            return null
        }

        fun borrarIPEspera(ip: String?): Boolean {
            return _IpsEspera.remove(ip)
        }

        fun addIPEspera(ip: String) {
            _IpsEspera.add(ip)
        }

        fun clientesDisponibles(): String {
            val IPs = ArrayList<String?>()
            for (ep in clientes) {
                try {
                    if (!IPs.contains(ep.actualIP)) {
                        IPs.add(ep.actualIP)
                    }
                } catch (ignored: Exception) {
                }
            }
            return "IP Availables for attack: " + IPs.size
        }

        fun listaClientesBug(segundos: Int): String {
            val str = StringBuilder()
            for (ep in clientes) {
                try {
                    if (ep.personaje != null) {
                        if (!ep.personaje!!.enLinea()) {
                            ep.cerrarSocket(true, "listaClientesBug(1)")
                            str.append("\n").append(ep.actualIP)
                        }
                    } else {
                        if (System.currentTimeMillis() - ep.tiempoUltPacket > segundos * 1000) {
                            ep.cerrarSocket(true, "listaClientesBug(2)")
                            str.append("\n").append(ep.actualIP)
                        }
                    }
                } catch (ignored: Exception) {
                }
            }
            return str.toString()
        }

        fun borrarCuentasBug(segundos: Int): Int {
            val sesionesSinAttach = INSTANCE.acceptor.managedSessions.values.stream()
                .filter { session: IoSession -> session.attachment == null }
                .collect(Collectors.toList())
            var i = 0
            for (ep in clientes) {
                try {
                    if (ep.personaje == null) {
                        if (System.currentTimeMillis() - ep.tiempoUltPacket > segundos * 1000) {
                            ep.session.closeNow()
                            i++
                        }
                    }
                } catch (ignored: Exception) {
                }
            }
            for (ioSession in sesionesSinAttach) {
                ioSession.closeNow()
                i++
            }
            return i
        }

        fun borrarClientesBug(segundos: Int): Int {
            var i = 0
            for (ep in clientes) {
                try {
                    if (ep.personaje != null) {
                        if (ep.personaje?.enLinea() != true) {
                            ep.cerrarSocket(true, "borrarClientesBug(1)")
                            i++
                        }
                    } else {
                        if (System.currentTimeMillis() - ep.tiempoUltPacket > segundos * 1000) {
                            ep.session.closeNow()
                            i++
                        }
                    }
                } catch (ignored: Exception) {
                }
            }
            return i
        }

        fun getHoraHoy(perso: Personaje): String {
            val hoy = Calendar.getInstance()
            if (perso.esDeDia()) {
                hoy[Calendar.HOUR_OF_DAY] = AtlantaMain.HORA_DIA
                hoy[Calendar.MINUTE] = AtlantaMain.MINUTOS_DIA
            } else if (perso.esDeNoche()) {
                hoy[Calendar.HOUR_OF_DAY] = AtlantaMain.HORA_NOCHE
                hoy[Calendar.MINUTE] = AtlantaMain.MINUTOS_NOCHE
            }
            return "BT" + hoy.timeInMillis // + hoy.getTimeZone().getRawOffset()
        }

        val fechaHoy: String
            get() {
                val hoy = Calendar.getInstance()
                val dia = StringBuilder(hoy[Calendar.DAY_OF_MONTH].toString() + "")
                while (dia.length < 2) {
                    dia.insert(0, "0")
                }
                val mes = StringBuilder(hoy[Calendar.MONTH].toString() + "")
                while (mes.length < 2) {
                    mes.insert(0, "0")
                }
                val año = hoy[Calendar.YEAR]
                return "BD$año|$mes|$dia"
            }

        val fechaConHora: String
            get() {
                val hoy = Calendar.getInstance()
                val segundo = StringBuilder(hoy[Calendar.SECOND].toString() + "")
                while (segundo.length < 2) {
                    segundo.insert(0, "0")
                }
                val minuto = StringBuilder(hoy[Calendar.MINUTE].toString() + "")
                while (minuto.length < 2) {
                    minuto.insert(0, "0")
                }
                val hora = StringBuilder(hoy[Calendar.HOUR_OF_DAY].toString() + "")
                while (hora.length < 2) {
                    hora.insert(0, "0")
                }
                val dia = StringBuilder(hoy[Calendar.DAY_OF_MONTH].toString() + "")
                while (dia.length < 2) {
                    dia.insert(0, "0")
                }
                val mes = StringBuilder((hoy[Calendar.MONTH] + 1).toString() + "")
                while (mes.length < 2) {
                    mes.insert(0, "0")
                }
                val año = hoy[Calendar.YEAR]
                return "Year: $dia/$mes/$año\nTime: $hora:$minuto:$segundo"
            }
    }

    private val acceptor: IoAcceptor
    fun start(): Boolean {
        if (acceptor.isActive) {
            log.warn("Error already start but try to launch again")
            return false
        }
        return try {
            acceptor.bind(InetSocketAddress(AtlantaMain.PUERTO_SERVIDOR))
            log.info(
                "Game server started on address : {}:{}",
                AtlantaMain.IP_PUBLICA_SERVIDOR,
                AtlantaMain.PUERTO_SERVIDOR
            )
            true
        } catch (e: IOException) {
            log.error("Error while starting game server", e)
            false
        }
    }

    fun stop() {
        if (!acceptor.isActive) {
            acceptor.managedSessions.values.stream()
                .filter { session: IoSession -> session.isConnected || !session.isClosing }
                .forEach { session: IoSession -> session.closeNow() }
            acceptor.dispose()
            acceptor.unbind()
        }
        log.error("The game server was stopped.")
    }

    fun setState(state: Int) {
        ExchangeClient.INSTANCE!!.send("SS$state")
    }


    init {
        acceptor = NioSocketAcceptor()
        acceptor.filterChain.addLast(
            "codec",
            ProtocolCodecFilter(
                TextLineCodecFactory(
                    StandardCharsets.UTF_8,
                    LineDelimiter.NUL,
                    LineDelimiter("\n\u0000")
                )
            )
        )
        acceptor.sessionConfig.setIdleTime(IdleStatus.BOTH_IDLE, 60 * 10 /*10 Minutes*/)
        acceptor.handler = ServidorHandler()
    }
}