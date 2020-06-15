package utilidades.algoritmos

import estaticos.*
import estaticos.database.GestorSQL
import servidor.ServidorServer
import sincronizador.ExchangeClient
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.net.UnknownHostException
import java.util.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

object FuncionesParaThreads {
    private var _nroPub = 0
    fun mensajeReinicio(_str: String) {
        try {
            GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("115;$_str")
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun Reiniciar(i: Int) {
        try {
            try {
                if (i == 0) {
                    Mundo.finalizarPeleas()
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_TODOS(
                        "SE HAN GANADO TODAS LAS PELEAS PVM",
                        Constantes.COLOR_VERDE_OSCURO
                    )
                } else {
                    Mundo.cancelarPeleas()
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_TODOS(
                        "SE HAN CANCELADO TODAS LAS PELEAS",
                        Constantes.COLOR_ROJO
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            for (i in 5 downTo 1) {
                GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_TODOS("EL SERVER SE REINICIARA EN $i", Constantes.COLOR_ROJO)
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            exitProcess(i)
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun AntiDDOS() {
        try {
            val _minConexionesXSeg = 5
            if (!Mundo.BLOQUEANDO) {
                var ataque = true
                for (i in ServidorServer._conexiones.indices) {
                    ataque = ataque and (ServidorServer._conexiones[i] > _minConexionesXSeg)
                }
                if (ataque) {
                    Mundo.BLOQUEANDO = true
                    AtlantaMain.redactarLogServidorln("SE ACTIVO EL BLOQUEO AUTOMATICO CONTRA ATAQUES")
                }
            } else {
                var ataque = true
                for (i in ServidorServer._conexiones.indices) {
                    ataque = ataque and (ServidorServer._conexiones[i] < _minConexionesXSeg)
                }
                if (ataque) {
                    try {
                        for (ss in ServidorServer.clientes) {
                            if (ss.personaje == null) {
                                // expulsa a los q no tienen personajes
                                GestorSalida.ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(ss, "16", "", "")
                                ss.cerrarSocket(true, "Antiddos.run()")
                            }
                        }
                    } catch (ignored: Exception) {
                    }

                    Mundo.BLOQUEANDO = false
                    AtlantaMain.redactarLogServidorln("SE DESACTIVO EL BLOQUEO AUTOMATICO CONTRA ATAQUES")
                }
            }
            ServidorServer._j = (ServidorServer._j + 1) % 5
            ServidorServer._conexiones[ServidorServer._j] = 0
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun AumentarSegundos() {
        try {
            ExchangeClient.INSTANCE?.send("C" + ServidorServer.nroJugadoresLinea())
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun ActualizarLadder() {
        try {
            Mundo.actualizarRankings()
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun ExpulsarInactivos() {
        try {
            Mundo.expulsarInactivos()
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun SalvarServidor(inclusoOffline: Boolean) {
        try {
            if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_SALVANDO) {
                AtlantaMain.redactarLogServidorln(
                    "Se esta intentando salvar el servidor, cuando este ya se esta salvando (MUNDO DOFUS)"
                )
            } else {
                Mundo.salvarServidor(inclusoOffline)
            }
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun ResetRates() {
        try {
            AtlantaMain.resetRates()
            GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1FINISH_SUPER_RATES")
            AtlantaMain.SEGUNDOS_RESET_RATES = 0
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun LiveAction() {
        try {
            GestorSQL.CARGAR_LIVE_ACTION()
            GestorSQL.VACIAR_LIVE_ACTION()
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun GarbageCollector() {
        try {
            System.gc()
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun SubirEstrellas() {
        try {
            Mundo.subirEstrellasMobs(1)
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun DetectarDDOS() {
        try {
            try {
                if (AtlantaMain.URL_DETECTAR_DDOS.isEmpty()) {
                    return
                }
                val obj = URL(AtlantaMain.URL_DETECTAR_DDOS)
                val con = obj.openConnection()
                con.setRequestProperty("Content-type", "charset=Unicode")
                val `in` = BufferedReader(InputStreamReader(con.getInputStream()))
                while (`in`.readLine() != null) {
                    Thread.sleep(1)
                }
                `in`.close()
                if (!AtlantaMain.PARAM_JUGADORES_HEROICO_MORIR) {
                    AtlantaMain.redactarLogServidorln(
                        "============= SE HA FINALIZADO ATAQUE DDOS (" + Date()
                                + ") ============="
                    )
                    AtlantaMain.PARAM_JUGADORES_HEROICO_MORIR = true
                    GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1SERVER_RESTORING_ATTACK")
                }
            } catch (e1: MalformedURLException) {
                if (AtlantaMain.PARAM_JUGADORES_HEROICO_MORIR) {
                    AtlantaMain.redactarLogServidorln("============= SE DETECTO ATAQUE DDOS (" + Date() + ") =============")
                    AtlantaMain.PARAM_JUGADORES_HEROICO_MORIR = false
                    GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1SERVER_IS_BEING_ATTACKED")
                    Mundo.salvarServidor(false)
                }
            } catch (e1: UnknownHostException) {
                if (AtlantaMain.PARAM_JUGADORES_HEROICO_MORIR) {
                    AtlantaMain.redactarLogServidorln("============= SE DETECTO ATAQUE DDOS (" + Date() + ") =============")
                    AtlantaMain.PARAM_JUGADORES_HEROICO_MORIR = false
                    GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1SERVER_IS_BEING_ATTACKED")
                    Mundo.salvarServidor(false)
                }
            } catch (e: Exception) {
                AtlantaMain.redactarLogServidorln("EXCEPTION DE DETECTAR DDOS: $e")
                e.printStackTrace()
            }
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun MoverPavos() {
        try {
            Mundo.moverMonturas()
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun MoverMobs() {
        try {
            Mundo.moverMobs()
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun PequeñoSalvar() {
        try {
            Mundo.pequeñosalvar(false)
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun Publicidad() {
        try {
            if (AtlantaMain.PUBLICIDAD.isEmpty()) {
                return
            }
            val _str = AtlantaMain.PUBLICIDAD[_nroPub]
            _nroPub++
            if (_nroPub >= AtlantaMain.PUBLICIDAD.size) {
                _nroPub = 0
            }
            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(_str)
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun SortearLoteria() {
        try {
            Mundo.sortearBoletos()
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun IniciarLoteria() {
        try {
            Mundo.iniciarLoteria()
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun BorrarCuentaRegresiva() {
        try {
            if (AtlantaMain.SEGUNDOS_REBOOT_SERVER > 0) {
                Mundo.SEG_CUENTA_REGRESIVA = AtlantaMain.SEGUNDOS_REBOOT_SERVER.toLong()
            }
            Mundo.MSJ_CUENTA_REGRESIVA = AtlantaMain.MENSAJE_TIMER_REBOOT
            GestorSalida.ENVIAR_bRS_PARAR_CUENTA_REGRESIVA_TODOS()
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun Koliseo() {
        try {
            Mundo.SEGUNDOS_INICIO_KOLISEO--
            if (Mundo.SEGUNDOS_INICIO_KOLISEO == 60) {
                GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_1_MINUTO_INICIA")
            } else if (Mundo.SEGUNDOS_INICIO_KOLISEO == 5) {
                GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_5_SEGUNDOS_INICIA")
            } else if (Mundo.SEGUNDOS_INICIO_KOLISEO == 0) {
                Mundo.iniciarKoliseo()
            }
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun DisminuirFatiga() {
        try {
            Mundo.disminuirFatigaMonturas()
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun CheckearObjInteractivos() {
        try {
            Mundo.checkearObjInteractivos()
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun MoverRecaudadores() {
        try {
            Mundo.moverRecaudadores()
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun ResetExpDia() {
        try {
            Mundo.resetExpDia()
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(e.toString())
        }
    }

    fun cadaSegundo() {
        try {
            val inicio = System.currentTimeMillis()
            if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
                return
            }
            thread(true, true, null, null, 5) { AumentarSegundos() }
            if (AtlantaMain.PARAM_STOP_SEGUNDERO) {
                return
            }
            if (AtlantaMain.SEGUNDOS_REBOOT_SERVER > 0) {
                AtlantaMain.SEGUNDOS_REBOOT_SERVER--
                if (AtlantaMain.SEGUNDOS_REBOOT_SERVER == 0) {
                    thread(true, true, null, null, 5) {
                        Reiniciar(
                            1
                        )
                    }
                    return
                } else {
                    val segundosFaltan = AtlantaMain.SEGUNDOS_REBOOT_SERVER
                    if (segundosFaltan % 60 == 0) {
                        val minutosFaltan = segundosFaltan / 60
                        if (minutosFaltan <= 60 && (minutosFaltan % 10 == 0 || minutosFaltan <= 5)) {
                            thread(true, true, null, null, 5) {
                                mensajeReinicio(
                                    "$minutosFaltan minutes"
                                )
                            }
                        }
                    }
                }
            }
//            if (AtlantaMain.PARAM_ANTI_DDOS) {
//                thread(true, true, null, null, 5) { AntiDDOS() }
//            }
            if (AtlantaMain.PARAM_LADDER_NIVEL) {
                if (Formulas.segundosON() % 15 == 0) {
                    thread(true, true, null, null, 5) { ActualizarLadder() }
                }
            }
            if (AtlantaMain.SEGUNDOS_INACTIVIDAD > 0) {
                if (Formulas.segundosON() % 3000 == 0) { // es 3000 para q se refresque rapido
                    thread(true, true, null, null, 5) { ExpulsarInactivos() }
                }
            }
            if (AtlantaMain.SEGUNDOS_SALVAR > 0) {
                if (Formulas.segundosON() % AtlantaMain.SEGUNDOS_SALVAR == 0) {
                    thread(true, true, null, null, 5) {
                        SalvarServidor(
                            false
                        )
                    }
                }
            }
            if (AtlantaMain.SEGUNDOS_RESET_RATES > 0) {
                if (Formulas.segundosON() % AtlantaMain.SEGUNDOS_RESET_RATES == 0) {
                    thread(true, true, null, null, 5) { ResetRates() }
                }
            }
            if (AtlantaMain.SEGUNDOS_LIVE_ACTION > 0) {
                if (Formulas.segundosON() % AtlantaMain.SEGUNDOS_LIVE_ACTION == 0) {
                    thread(true, true, null, null, 5) { LiveAction() }
                }
            }
//            if (AtlantaMain.SEGUNDOS_LIMPIAR_MEMORIA > 0) {
//                if (Formulas.segundosON() % AtlantaMain.SEGUNDOS_LIMPIAR_MEMORIA == 0) {
//                    thread(true, true, null, null, 5) { GarbageCollector() }
//                }
//            }
            if (AtlantaMain.SEGUNDOS_ESTRELLAS_GRUPO_MOBS > 0) {
                if (Formulas.segundosON() % AtlantaMain.SEGUNDOS_ESTRELLAS_GRUPO_MOBS == 0) {
                    thread(true, true, null, null, 5) { SubirEstrellas() }
                }
            }
//            if (AtlantaMain.SEGUNDOS_DETECTAR_DDOS > 0) {
//                if (Formulas.segundosON() % AtlantaMain.SEGUNDOS_DETECTAR_DDOS == 0) {
//                    thread(true, true, null, null, 5) { DetectarDDOS() }
//                }
//            }
            if (AtlantaMain.SEGUNDOS_MOVER_MONTURAS > 0) {
                if (Formulas.segundosON() % AtlantaMain.SEGUNDOS_MOVER_MONTURAS == 0) {
                    thread(true, true, null, null, 5) { MoverPavos() }
                }
            }
            if (AtlantaMain.SEGUNDOS_MOVER_GRUPO_MOBS > 0) {
                if (Formulas.segundosON() % AtlantaMain.SEGUNDOS_MOVER_GRUPO_MOBS == 0) {
                    thread(true, true, null, null, 5) { MoverMobs() }
                }
            }
            //				if (AtlantaMain.RATE_RANDOM_MOB != 1){
//					if (_segundosON % 120 == 0){
//						new AleatorizarMobs();
//					}
//				}
            if (AtlantaMain.SEGUNDOS_PEQUEÑO_SALVAR > 0) {
                if (Formulas.segundosON() % AtlantaMain.SEGUNDOS_PEQUEÑO_SALVAR == 0) {
                    thread(true, true, null, null, 5) { PequeñoSalvar() }
                }
            }
            if (AtlantaMain.SEGUNDOS_PUBLICIDAD > 0) {
                if (Formulas.segundosON() % AtlantaMain.SEGUNDOS_PUBLICIDAD == 0) {
                    thread(true, true, null, null, 5) { Publicidad() }
                }
            }
            if (AtlantaMain.PARAM_LOTERIA) {
                if (Mundo.SEG_CUENTA_REGRESIVA > 0) {
                    if (Mundo.SEG_CUENTA_REGRESIVA - 1 == 0L) {
                        if (Mundo.MSJ_CUENTA_REGRESIVA.equals("LOTERIA", ignoreCase = true)) {
                            thread(true, true, null, null, 5) { SortearLoteria() }
                        } else {
                            GestorSalida.ENVIAR_ÑL_BOTON_LOTERIA_TODOS(false)
                        }
                    }
                } else if (Formulas.segundosON() % 3600 == 0) {
                    thread(true, true, null, null, 5) { IniciarLoteria() }
                }
            }
            if (Mundo.SEG_CUENTA_REGRESIVA > 0) {
                if (--Mundo.SEG_CUENTA_REGRESIVA == 0L) {
                    thread(true, true, null, null, 5) { BorrarCuentaRegresiva() }
                }
            }
            if (AtlantaMain.PARAM_KOLISEO) {
                thread(true, true, null, null, 5) { Koliseo() }
            }
            // new EmbarazoMonturas();
            thread(true, true, null, null, 5) { DisminuirFatiga() }
            thread(true, true, null, null, 5) { CheckearObjInteractivos() }
            thread(true, true) { MoverRecaudadores() }
            val dia = Calendar.getInstance()[Calendar.DAY_OF_YEAR]
            if (Mundo.DIA_DEL_AÑO != dia) {
                Mundo.DIA_DEL_AÑO = dia
                thread(true, true) { ResetExpDia() }
            }
        } catch (e: Exception) {
            thread(true, true) { AumentarSegundos() }
            AtlantaMain.redactarLogServidorln("Error en el timer $e")
        }
    }
}