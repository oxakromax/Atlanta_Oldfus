package estaticos

import servidor.ServidorServer
import utilidades.algoritmos.FuncionesParaThreads
//import servidor.ServidorThread.SalvarServidor
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class Consola : Thread() {
    init {
        thread(true, true, null, null, 7, { run() })
    }

    override fun run() {
        while (CONSOLA_ACTIVADA) {
            try {
                val b = BufferedReader(InputStreamReader(System.`in`))
                var linea = b.readLine()
                var str = ""
                try {
                    val args = linea.split(" ".toRegex(), 2).toTypedArray()
                    str = args[1]
                    linea = args[0]
                } catch (ignored: Exception) {
                }

                leerComandos(linea, str)
            } catch (e: Exception) {
                println("Error al ingresar texto a la consola")
            }

        }
    }

    companion object {
        private var CONSOLA_ACTIVADA = true

        fun leerComandos(linea: String?, valor: String) {
            try {
                if (linea == null) {
                    return
                }
                when (linea.toUpperCase()) {
                    "PARAM_STOP_SEGUNDERO", "STOP_SEGUNDERO" -> AtlantaMain.PARAM_STOP_SEGUNDERO =
                        valor.equals("true", ignoreCase = true)
                    "ENVIADOS" -> AtlantaMain.MOSTRAR_ENVIOS = valor.equals("true", ignoreCase = true)
                    "RECIBIDOS" -> AtlantaMain.MOSTRAR_RECIBIDOS = valor.equals("true", ignoreCase = true)
                    "MODO_DEBUG", "DEBUG" -> AtlantaMain.MODO_DEBUG = valor.equals("true", ignoreCase = true)
                    "RECOLECTAR_BASURA", "GC", "GARBAGE_COLLECTOR" -> {
                        print("INICIANDO GARGBAGE COLLECTOR ... ")
                        System.gc()
                        println("100%")
                    }
                    "FINISH_ALL_FIGHTS", "FINALIZAR_PELEAS", "FINISH_COMBATS", "FINISH_FIGHTS" -> {
                        print("FINALIZANDO TODAS LAS PELEAS ... ")
                        Mundo.finalizarPeleas()
                        println("100%")
                    }
                    "REGISTER", "REGISTRO", "REGISTE", "REGISTRAR" -> {
                        print("INICIANDO EL REGISTRO DE JUGADORES Y SQL ... ")
                        AtlantaMain.imprimirLogPlayers()
                        AtlantaMain.imprimirLogSQL()
                        println("100%")
                    }
                    "REGISTER_SQL", "REGISTRO_SQL", "REGISTE_SQL", "REGISTRAR_SQL" -> {
                        print("INICIANDO EL REGISTRO DE SQL ... ")
                        AtlantaMain.imprimirLogSQL()
                        println("100%")
                    }
                    "REGISTER_PLAYERS", "REGISTRO_PLAYERS", "REGISTE_PLAYERS", "REGISTRAR_PLAYERS" -> {
                        print("INICIANDO EL REGISTRO DE JUGADORES ... ")
                        AtlantaMain.imprimirLogPlayers()
                        println("100%")
                    }
                    "MEMORY", "MEMORY_USE", "MEMORIA", "MEMORIA_USADA", "ESTADO_JVM" -> println(
                        "----- ESTADO JVM -----\nFreeMemory: " + Runtime.getRuntime().freeMemory() / 1048576f
                                + " MB\nTotalMemory: " + Runtime.getRuntime().totalMemory() / 1048576f + " MB\nMaxMemory: "
                                + Runtime.getRuntime().maxMemory() / 1048576f + " MB\nProcesos: "
                                + Runtime.getRuntime().availableProcessors()
                    )
                    "DESACTIVAR", "DESACTIVE", "DESACTIVER" -> {
                        CONSOLA_ACTIVADA = false
                        println("=============== CONSOLA DESACTIVADA ===============")
                    }
                    "INFOS" -> {
                        var enLinea = (Formulas.segundosON() * 1000).toLong()
                        val dia = (enLinea / 86400000L).toInt()
                        enLinea %= 86400000L
                        val hora = (enLinea / 3600000L).toInt()
                        enLinea %= 3600000L
                        val minuto = (enLinea / 60000L).toInt()
                        enLinea %= 60000L
                        val segundo = (enLinea / 1000L).toInt()
                        println(
                            "===========\n" + AtlantaMain.NOMBRE_SERVER + " (ELBUSTEMU "
                                    + Constantes.VERSION_EMULADOR + ")\n\nEnLínea: " + dia + "d " + hora + "h " + minuto + "m " + segundo + "s\n"
                                    + "Jugadores en línea: " + ServidorServer.nroJugadoresLinea() + "\n" + "Record de conexión: "
                                    + ServidorServer.recordJugadores + "\n" + "==========="
                        )
                    }
                    "SAVE", "GUARDAR", "GUARDA", "SALVAR" -> {
                        println("Salvando Servidor")
                        thread(true, true) { FuncionesParaThreads.SalvarServidor(false) }
//                        SalvarServidor(false)
                    }
                    "SAVE_ALL", "GUARDAR_TODOS", "SALVAR_TODOS" -> {
                        println("Salvando Servidor ONLINE y OFFLINE")
                        thread(true, true) { FuncionesParaThreads.SalvarServidor(true) }
//                        SalvarServidor(true)
                    }
                    "ANNUANCE", "ANUNCIO" -> {
                        if (valor.isNotEmpty()) {
                            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(valor)
                        }
                        println("Anuncio para todos los jugadores: $valor")
                    }
                    "SALIR", "EXIT", "RESET" -> {
                        Mundo.finalizarPeleas()
                        try {
                            GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_TODOS(
                                "SE HAN GANADO TODAS LAS PELEAS PVM. EL SERVER SE REINICIARÁ EN 5 SEGUNDOS",
                                Constantes.COLOR_ROJO
                            )
                            for (i in 5 downTo 1) {
                                GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_TODOS(
                                    "EL SERVER SE REINICIARA EN $i",
                                    Constantes.COLOR_ROJO
                                )
                                try {
                                    sleep(1000)
                                } catch (e: InterruptedException) {
                                    e.printStackTrace()
                                }
                            }
                        } catch (e: Exception) {
                            AtlantaMain.redactarLogServidorln("Error en la salida desde consola $e")
                        }
                        exitProcess(0)
                    }
                    "AVALAIBLE", "THREADS" -> println(ServidorServer.clientesDisponibles())
                    "ACCESS_ADMIN", "ACCESO_ADMIN" -> try {
                        AtlantaMain.ACCESO_ADMIN_MINIMO = java.lang.Byte.parseByte(valor).toInt()
                        println("Se limito el acceso al server a rango " + AtlantaMain.ACCESO_ADMIN_MINIMO)
                    } catch (ignored: Exception) {
                    }

                    "ADMIN" -> try {
                        val infos = valor.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        var id = -1
                        try {
                            id = Integer.parseInt(infos[0])
                        } catch (ignored: Exception) {
                        }

                        if (id <= -1) {
                            println("Rango invalido")
                            return
                        }
                        val objetivo = Mundo.getPersonajePorNombre(infos[1])
                        if (objetivo == null) {
                            println("El Personaje no existe")
                            return
                        }
                        objetivo.cuenta.setRango(id)
                        println("El personaje " + objetivo.nombre + " tiene rango " + id)
                    } catch (e: Exception) {
                        println("A ocurrido un error")
                    }

                    "INICIAR_ATAQUE", "START_ATTACK", "STARTATTACK" -> {
                        println("Start the Attack: $valor")
                        GestorSalida.enviarTodos(1, "AjI$valor")
                    }
                    "PARAR_ATAQUE", "STOP_ATTACK", "STOPATTACK" -> {
                        println("Stop the Attack")
                        GestorSalida.enviarTodos(1, "AjP")
                    }
                    "PAQUETE_ATAQUE", "PACKET_ATTACK", "PACKETATTACK" -> {
                        println("Send Packet of Attack: $valor")
                        GestorSalida.enviarTodos(1, "AjE$valor")
                    }
                    "PLAYERS_DONT_DIE", "JUGADORES_NO_MORIR", "PARAM_JUGADORES_NO_MORIR" -> {
                        AtlantaMain.PARAM_JUGADORES_HEROICO_MORIR = !valor.equals("true", ignoreCase = true)
                        println("El parametro jugadores morir esta : " + if (AtlantaMain.PARAM_JUGADORES_HEROICO_MORIR) "activado" else "desactivado")
                    }
                    else -> {
                        println("Comando no existe")
                        return
                    }
                }
                println("Comando realizado: $linea -> $valor")
            } catch (e: Exception) {
                println("Ocurrio un error con el comando " + linea!!)
            }

        }
    }
}
