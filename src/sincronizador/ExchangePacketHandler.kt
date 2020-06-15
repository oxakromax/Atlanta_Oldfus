package sincronizador

import estaticos.AtlantaMain
import estaticos.GestorSalida
import estaticos.Mundo
import estaticos.database.GestorSQL
import servidor.ServidorServer
import variables.personaje.Cuenta

object ExchangePacketHandler {
    @JvmStatic
    fun parser(packet: String) {
        try {
            when (packet[0]) {
                'I' -> try {
                    val infos = packet.substring(1).split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val ip = infos[0]
                    val cantidad = ServidorServer.getIPsClientes(ip)
                    ExchangeClient.INSTANCE?.send("I$ip;$cantidad")
                } catch (ignored: Exception) {
                }

                'A'// cuenta
                -> {
                    try {
                        val infos =
                            packet.substring(1).split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val id = Integer.parseInt(infos[0])
                        var cuenta: Cuenta? = Mundo.getCuenta(id)
                        if (cuenta == null) {
                            GestorSQL.CARGAR_CUENTA_POR_ID(id)// cuenta nueva
                            cuenta = Mundo.getCuenta(id)
                        }
                        if (cuenta == null) {
                            AtlantaMain.redactarLogServidorln("SE QUIERE REGISTRAR CUENTA FALSA: $packet")
                            return
                        }
                        try {
                            if (cuenta.socket != null) {
                                GestorSalida.ENVIAR_AlEd_DESCONECTAR_CUENTA_CONECTADA(cuenta.socket)
                                GestorSalida.ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(
                                    cuenta.socket, "45",
                                    "OTHER PLAYER CONNECTED WITH YOUR ACCOUNT", ""
                                )
                                cuenta.socket?.cerrarSocket(true, "analizarPackets()")
                                cuenta.socket = null
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        if (AtlantaMain.PARAM_SISTEMA_IP_ESPERA) {
                            ServidorServer.addIPEspera(infos[1])
                        }
                        ServidorServer.addEsperandoCuenta(cuenta)
                        ExchangeClient.INSTANCE?.send("A" + id + ";" + cuenta.personajes.size)
                    } catch (e: Exception) {
                        AtlantaMain.redactarLogServidorln(" EXPCETION AL CARGAR CUENTA: $e")
                    }
                }
            }
        } catch (ignored: Exception) {
        }

    }
}