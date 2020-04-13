package estaticos

import servidor.ServidorServer
import servidor.ServidorSocket
import sprites.Exchanger
import utilidades.economia.Economia
import variables.casa.Cofre
import variables.gremio.Gremio
import variables.gremio.MiembroGremio
import variables.mapa.Celda
import variables.mapa.Cercado
import variables.mapa.Mapa
import variables.montura.Montura
import variables.objeto.Objeto
import variables.oficio.StatOficio
import variables.pelea.Luchador
import variables.pelea.Pelea
import variables.personaje.Cuenta
import variables.personaje.Grupo
import variables.personaje.GrupoKoliseo
import variables.personaje.Personaje
import java.util.*
import kotlin.math.max

object GestorSalida {
    fun enviarEnCola(perso2: Personaje?, packet: String, redactar: Boolean) {
        var perso = perso2
        try {
            if (perso == null) {
                return
            }
            if (perso.esMultiman()) {
                perso = perso.compañero
                if (perso == null) {
                    return
                }
            }
//            if (perso._multi != null && perso._multi != perso){
//                if (perso._multi.enLinea()){
//                    if (perso._multi.cargandoMapa) {
//                        perso._multi.addPacketCola(packet)
//                    } else {
//                        perso._multi.servidorSocket!!.enviarPW(packet, redactar, true)
//                    }
//                }
//            }
            try {
                if (perso.enLinea()) {
                    if (perso.Multi == perso || perso.Multi == null) {
                        if (perso.cargandoMapa) {
                            perso.addPacketCola(packet)
                        } else {
                            perso.servidorSocket?.enviarPW(packet, redactar, true)
                        }
                    }
                }
            } catch (e: Exception) {
                if (perso.enLinea()) {
                    if (perso.cargandoMapa) {
                        perso.addPacketCola(packet)
                    } else {
                        perso.servidorSocket?.enviarPW(packet, redactar, true)
                    }
                }
                AtlantaMain.redactarLogServidorln("Error Gestor salida en diferenciar lider de Multi \n $e")
            }
            try {
                if (perso.pelea != null) {
                    if (perso.LiderIP != null && !perso.EsliderIP) {
                        val lider = perso.LiderIP
                        if (lider.enLinea() && lider.pelea == perso.pelea && lider.Multi == perso) {
                            if (lider.cargandoMapa) {
                                lider.addPacketCola(packet)
                            } else {
                                lider.servidorSocket?.enviarPW(packet, redactar, true)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                AtlantaMain.redactarLogServidorln("Error en el GestorSalida $e")
            }
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln("Error en el GestorSalida $e")
        }

    }

    fun enviar(perso2: Personaje?, packet: String) {
        var perso = perso2
        try {
            if (perso == null) {
                return
            }
            if (perso.esMultiman()) {
                perso = perso.compañero
            }
            if (perso!!.enLinea()) {
                perso.servidorSocket?.enviarPW(packet, true, true)
            }
        } catch (ignored: Exception) {
        }

    }

    fun enviarTodos(tiempo: Int, packet: String) {
        var tiempo = tiempo
        tiempo = max(tiempo, 1)
        for (ep in ServidorServer.clientes) {
            try {
                Thread.sleep(tiempo.toLong())
                ep.enviarPW(packet)
            } catch (ignored: Exception) {
            }

        }
    }

    private fun imprimir(pre: String, packet: String) {
        if (AtlantaMain.MOSTRAR_ENVIOS) {
            println("$pre>> $packet")
        }
    }

    fun ENVIAR_pong(perso: Personaje) {
        val packet = "pong"
        enviarEnCola(perso, packet, true)
        imprimir("DOFUS PONG: PERSO", packet)
    }

    fun ENVIAR_qpong(ss: Personaje) {
        val packet = "qpong"
        enviarEnCola(ss, packet, true)
        imprimir("DOFUS QPONG: PERSO", packet)
    }

    fun ENVIAR_HG_SALUDO_JUEGO_GENERAL(ss: ServidorSocket) {
        val packet = "HG"
        ss.enviarPW(packet)
        imprimir("SALUDO JUEGO: OUT", packet)
    }

    fun ENVIAR_XML_POLICY_FILE(ss: ServidorSocket) {
        val packet = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"//

                + "<cross-domain-policy>" + "<site-control permitted-cross-domain-policies=\"all\" />"
                + "<allow-access-from domain=\"*\" to-ports=\"*\" secure=\"false\" />"
                + "<allow-http-request-headers-from domain=\"*\" headers=\"*\" secure=\"false\"/>" // s

                + "</cross-domain-policy>")
        ss.enviarPW(packet)
        imprimir("POLICY FILE: OUT", packet)
    }

    fun ENVIAR_APK_NOMBRE_PJ_ALEATORIO(ss: ServidorSocket, nombre: String) {
        val packet = "APK$nombre"
        ss.enviarPW(packet)
        imprimir("NOMBRE PJ ALEATORIO: OUT", packet)
    }

    fun ENVIAR_AlEk_CUENTA_BANEADA_TIEMPO(ss: ServidorSocket, tiempo: Long) {
        var tiempo = tiempo
        var packet = "AlEk"
        tiempo -= System.currentTimeMillis()
        val dia = (tiempo / (1000 * 3600 * 24)).toInt()
        tiempo %= (1000 * 3600 * 24).toLong()
        val horas = (tiempo / (1000 * 3600)).toInt()
        tiempo %= (1000 * 3600).toLong()
        val min = (tiempo / (1000 * 60)).toInt()
        packet += "$dia|$horas|$min"
        ss.enviarPW(packet)
        imprimir("CUENTA BANEADA TIEMPO: CONEXION", packet)
    }

    fun ENVIAR_AlEb_CUENTA_BANEADA_DEFINITIVO(ss: ServidorSocket) {
        val packet = "AlEb"
        ss.enviarPW(packet)
        imprimir("CUENTA BANEADA DEFINITIVA: CONEXION", packet)
    }

    fun ENVIAR_AlEw_MUCHOS_JUG_ONLINE(ss: ServidorSocket) {
        val packet = "AlEw"
        ss.enviarPW(packet)
        imprimir("MAX JUG ONLINE: CONEXION", packet)
    }

    fun ENVIAR_AlEx_NOMBRE_O_PASS_INCORRECTA(ss: ServidorSocket) {
        val packet = "AlEx"
        ss.enviarPW(packet)
        imprimir("LOGIN ERROR: CONEXION", packet)
    }

    fun ENVIAR_AlEd_DESCONECTAR_CUENTA_CONECTADA(ss: ServidorSocket?) {
        val packet = "AlEd"
        ss?.enviarPW(packet)
        imprimir("MISMA CUENTA CONECTADA: CONEXION", packet)
    }

    fun ENVIAR_AlEr_CAMBIAR_NOMBRE(ss: ServidorSocket) {
        val packet = "AlEr"
        ss.enviarPW(packet)
        imprimir("CAMBIAR NOMBRE: PERSO", packet)
    }

    fun ENVIAR_AlEr_CAMBIAR_NOMBRE(ss: Personaje) {
        val packet = "AlEr"
        enviarEnCola(ss, packet, true)
        imprimir("CAMBIAR NOMBRE: PERSO", packet)
    }

    fun ENVIAR_AlEm_SERVER_MANTENIMIENTO(ss: ServidorSocket) {
        val packet = "AlEm"
        ss.enviarPW(packet)
        imprimir("SERVER MANTENIMIENTO: CONEXION", packet)
    }

    fun ENVIAR_Af_ABONADOS_POSCOLA(
        ss: ServidorSocket, posicion: Int, totalAbo: Int,
        totalNonAbo: Int, subscribe: String, colaID: Int
    ) {
        val packet = "Af$posicion|$totalAbo|$totalNonAbo|$subscribe|$colaID"
        ss.enviarPW(packet)
        imprimir("MULTIPAQUETES: CONEXION", packet)
    }

    fun ENVIAR_AN_MENSAJE_NUEVO_NIVEL(perso: Personaje, nivel: Int) {
        val packet = "AN$nivel"
        enviarEnCola(perso, packet, true)
        imprimir("SUBIO NIVEL: PERSO", packet)
    }

    fun ENVIAR_ATK_TICKET_A_CUENTA(ss: ServidorSocket, key: String) {
        val packet = "ATK$key"
        ss.enviarPWSinEncriptar(packet)
        imprimir("TICKET A CUENTA: OUT", packet)
    }

    fun ENVIAR_AK_KEY_ENCRIPTACION_PACKETS(ss: ServidorSocket, key: String) {
        val packet = "AK$key"
        ss.enviarPWSinEncriptar(packet)
        imprimir("KEY ENCRIPTACION: OUT", packet)
    }

    fun ENVIAR_ATE_TICKET_FALLIDA(ss: ServidorSocket) {
        val packet = "ATE"
        ss.enviarPW(packet)
        imprimir("TICKET FALLIDA: OUT", packet)
    }

    fun ENVIAR_AV_VERSION_REGIONAL(ss: ServidorSocket) {
        val packet = "AV0"
        ss.enviarPW(packet)
        imprimir("VERSION DE REGION: OUT", packet)
    }

    fun ENVIAR_APE2_GENERAR_NOMBRE_RANDOM(ss: ServidorSocket) {
        val packet = "APE2"
        ss.enviarPW(packet)
        imprimir("GENERAR NOMBRE: PERSO", packet)
    }

    fun ENVIAR_ALK_LISTA_DE_PERSONAJES(ss: Personaje, cuenta: Cuenta) {
        val packet = StringBuilder("ALK" + cuenta.tiempoAbono + "|" + cuenta.personajes.size)
        for (perso in cuenta.personajes) {
            if (perso != null) {
                packet.append(perso.stringParaListaPJsServer())
            }
        }
        enviarEnCola(ss, packet.toString(), true)
        imprimir("LISTA DE PJS: OUT", packet.toString())
    }

    fun ENVIAR_ALK_LISTA_DE_PERSONAJES(ss: ServidorSocket, cuenta: Cuenta?) {
        val packet = StringBuilder("ALK" + cuenta!!.tiempoAbono + "|" + cuenta.personajes.size)
        for (perso in cuenta.personajes) {
            if (perso != null) {
                packet.append(perso.stringParaListaPJsServer())
            }
        }
        ss.enviarPW(packet.toString())
        imprimir("LISTA DE PJS: OUT", packet.toString())
    }

    fun ENVIAR_Ag_LISTA_REGALOS(ss: ServidorSocket, idObjeto: Int, codObjeto: String) {
        val packet = ("Ag1|" + idObjeto + "|Regalo " + AtlantaMain.NOMBRE_SERVER + "|SIN RELLENO|SIN FOTO|"
                + codObjeto)
        // packet = "Ag"+idObjeto+"|" + idObjeto + "|Regalo " + Bustemu.NOMBRE_SERVER +
        // "|SIN RELLENO|SIN
        // FOTO|"+"1~2411~1~~3cc#0#0#1,3cb#0#0#1,3cd#0#0#11,3ca#0#0#0,3ce#0#0#0;111~2412~2~~76#2#0#0#0d0+2;22~100~20~~7d#4#0#0#0d0+4,77#4#0#0#0d0+4";
        ss.enviarPW(packet)
        imprimir("LISTA REGALOS: PERSO", packet)
    }

    fun ENVIAR_AG_SIGUIENTE_REGALO(ss: ServidorSocket) {
        val packet = "AGK"
        ss.enviarPW(packet)
        imprimir("SIGUIENTE REGALO: PERSO", packet)
    }

    fun ENVIAR_AAE_ERROR_CREAR_PJ(ss: ServidorSocket, letra: String) {
        val packet = "AAE$letra"
        ss.enviarPW(packet)
        imprimir("ERROR CREAR PJ: OUT", packet)
    }

    fun ENVIAR_AAE_ERROR_CREAR_PJ(perso: Personaje, letra: String) {
        val packet = "AAE$letra"
        enviarEnCola(perso, packet, true)
        imprimir("ERROR CREAR PJ: OUT", packet)
    }

    fun ENVIAR_AAK_CREACION_PJ_OK(ss: ServidorSocket) {
        val packet = "AAK"
        ss.enviarPW(packet)
        imprimir("CREAR PJ OK: PERSO", packet)
    }

    fun ENVIAR_ADE_ERROR_BORRAR_PJ(ss: ServidorSocket) {
        val packet = "ADE"
        ss.enviarPW(packet)
        imprimir("ERROR BORRAR PJ: OUT", packet)
    }

    fun ENVIAR_ASE_SELECCION_PERSONAJE_FALLIDA(ss: ServidorSocket) {
        val packet = "ASE"
        ss.enviarPW(packet)
        imprimir("ERROR SELECCION PJ: OUT", packet)
    }

    fun ENVIAR_ASK_PERSONAJE_SELECCIONADO(perso: Personaje) {
        val packet = "ASK|" + perso.Id + "|" + perso.nombre + "|" + perso.nivel + "|" + perso
            .getClaseID(false) + "|" + perso.sexo + "|" + perso.getGfxID(false) + "|" + (if (perso.color1 == -1)
            "-1"
        else
            Integer.toHexString(perso.color1)) + "|" + (if (perso.color2 == -1)
            "-1"
        else
            Integer.toHexString(perso.color2)) + "|" + (if (perso.color3 == -1)
            "-1"
        else
            Integer.toHexString(perso.color3)) + "|" + perso.strListaObjetos()
        enviarEnCola(perso, packet, true)
        imprimir("PERSONAJE SELECCIONADO: OUT", packet)
    }

    fun ENVIAR_ASK_PERSONAJE_SELECCIONADO_A_LIDER(perso: Personaje, lider: Personaje) {
        val packet = "ASK|" + perso.Id + "|" + perso.nombre + "|" + perso.nivel + "|" + perso
            .getClaseID(false) + "|" + perso.sexo + "|" + perso.getGfxID(false) + "|" + (if (perso.color1 == -1)
            "-1"
        else
            Integer.toHexString(perso.color1)) + "|" + (if (perso.color2 == -1)
            "-1"
        else
            Integer.toHexString(perso.color2)) + "|" + (if (perso.color3 == -1)
            "-1"
        else
            Integer.toHexString(perso.color3)) + "|" + perso.strListaObjetos()
        enviarEnCola(lider, packet, true)
        imprimir("PERSONAJE SELECCIONADO: OUT", packet)
    }

    fun ENVIAR_ASK_PERSONAJE_A_ESPIAR(perso: Personaje, espiador: Personaje) {
        val packet = "ASK|" + perso.Id + "|" + perso.nombre + "|" + perso.nivel + "|" + perso
            .getClaseID(false) + "|" + perso.sexo + "|" + perso.getGfxID(false) + "|" + (if (perso.color1 == -1)
            "-1"
        else
            Integer.toHexString(perso.color1)) + "|" + (if (perso.color2 == -1)
            "-1"
        else
            Integer.toHexString(perso.color2)) + "|" + (if (perso.color3 == -1)
            "-1"
        else
            Integer.toHexString(perso.color3)) + "|" + perso.strListaObjetos()
        enviarEnCola(espiador, packet, false)
        imprimir("PERSONAJE SELECCIONADO: OUT", packet)
    }

    fun ENVIAR_AR_RESTRICCIONES_PERSONAJE(perso: Personaje) {
        val packet = "AR" + perso.restriccionesA
        enviarEnCola(perso, packet, true)
        imprimir("RESTRICCIONES: PERSO", packet)
    }

    fun ENVIAR_al_ESTADO_ZONA_ALINEACION(ss: Personaje) {
        val packet = "al|" + Mundo.alineacionTodasSubareas
        enviarEnCola(ss, packet, false)
        imprimir("SUBAREAS ALINEACION: PERSO", packet)
    }

    fun ENVIAR_am_CAMBIAR_ALINEACION_SUBAREA(
        perso: Personaje, subArea: Int, nuevaAlin: Byte,
        mensaje: Boolean
    ) {
        val packet = "am" + subArea + "|" + nuevaAlin + "|" + if (mensaje) 0 else 1
        enviarEnCola(perso, packet, false)
        imprimir("MSJ ALIN SUBAREA: PERSO", packet)
    }

    fun ENVIAR_aM_CAMBIAR_ALINEACION_AREA(perso: Personaje, area: Int, alineacion: Byte) {
        val packet = "aM$area|$alineacion"
        enviarEnCola(perso, packet, true)
        imprimir("MSJ ALIN AREA: PERSO", packet)
    }

    fun ENVIAR_BD_FECHA_SERVER(perso: Personaje) {
        val packet = ServidorServer.fechaHoy
        enviarEnCola(perso, packet, true)
        imprimir("FECHA SERVER: PERSO", packet)
    }

    fun ENVIAR_BT_TIEMPO_SERVER(perso: Personaje) {
        val packet = ServidorServer.getHoraHoy(perso)
        enviarEnCola(perso, packet, true)
        imprimir("TIEMPO SERVER: PERSO", packet)
    }

    fun ENVIAR_BN_NADA(perso: Personaje?) {
        val packet = "BN"
        enviarEnCola(perso, packet, true)
        imprimir("NADA: PERSO", packet)
    }

    fun ENVIAR_BN_NADA(ss: ServidorSocket) {
        val packet = "BN "
        ss.enviarPW(packet)
        imprimir("NADA: PERSO", packet)
    }

    fun ENVIAR_BN_NADA(ss: ServidorSocket, s: String) {
        val packet = "BN $s"
        ss.enviarPW(packet)
        imprimir("NADA: PERSO", packet)
    }

    fun ENVIAR_BN_NADA(perso: Personaje?, s: String) {
        val packet = "BN $s"
        enviarEnCola(perso, packet, true)
        imprimir("NADA: PERSO", packet)
    }

    fun ENVIAR_EHS_BUSCAR_OBJETO_MERCADILLO(ss: Personaje, str: String) {
        val packet = "EHS+$str"
        enviarEnCola(ss, packet, true)
        imprimir("BUSCAR OBJ MERCADILLO: PERSO", packet)
    }

    fun ENVIAR_dV_CERRAR_DOCUMENTO(ss: Personaje) {
        val packet = "dV"
        enviarEnCola(ss, packet, true)
        imprimir("CERRAR DOCUMENTO: PERSO", packet)
    }

    fun ENVIAR_dC_ABRIR_DOCUMENTO(ss: Personaje, str: String) {
        val packet = "dCK$str"
        enviarEnCola(ss, packet, true)
        imprimir("ABRIR DOCUMENTO: PERSO", packet)
    }

    fun ENVIAR_cMK_A_TODOS(es: String, fr: String) {
        val packet = "cMK+|0|ELBUSTEMU|$es"
        val packetFr = "cMK+|0|ELBUSTEMU|$fr"
        for (perso in Mundo.PERSONAJESONLINE) {
            if (perso.cuenta.idioma == "fr" && fr.isNotEmpty()) {
                enviarEnCola(perso, packetFr, false)
            } else if (es.isNotEmpty()) {
                enviarEnCola(perso, packet, false)
            }
        }
        imprimir("MENSAJE ROJO: TODOS", packet)
    }

    fun ENVIAR_SLo_MOSTRAR_TODO_HECHIZOS(ss: Personaje, mostrar: Boolean) {
        val packet = "SLo" + if (mostrar) "+" else "-"
        enviarEnCola(ss, packet, true)
        imprimir("MOSTRAR MAS HECHIZOS: PERSO", packet)
    }

    fun ENVIAR_FO_MOSTRAR_CONEXION_AMIGOS(ss: Personaje, mostrar: Boolean) {
        val packet = "FO" + if (mostrar) "+" else "-"
        enviarEnCola(ss, packet, true)
        imprimir("MOSTRAR AMIGOS CONEX: PERSO", packet)
    }

    fun ENVIAR_GCK_CREAR_PANTALLA_PJ(ss: Personaje) {
        val packet = "GCK|1"
        enviar(ss, packet)
        imprimir("CREAR PANTALLA: PERSO", packet)
    }

    fun ENVIAR_GA2_CARGANDO_MAPA(perso: Personaje) {
        val packet = "GA;2;"
        enviar(perso, packet)
        imprimir("CARGANDO MAPA: PERSO", packet)
    }

    fun ENVIAR_GDM_CAMBIO_DE_MAPA(perso: Personaje, mapa: Mapa) {
        perso.setCargandoMapa(true, null)
        perso.mapaGDM = mapa
        val packet = "GDM|" + mapa.id + "|" + mapa.fecha + "|"
        enviar(perso, packet)
        imprimir("CAMBIO MAPA: PERSO", packet)
    }

    fun ENVIAR_GDM_MAPDATA_COMPLETO(perso: Personaje) {
        perso.setCargandoMapa(true, null)
        val mapa = perso.mapaGDM
        val packet = ("GDM|" + mapa.id + "|" + mapa.fecha + "||" + mapa.ancho + "|" + mapa.alto
                + "|" + mapa.bgID + "|" + mapa.musicID + "|" + mapa.ambienteID + "|" + mapa.outDoor + "|" + mapa
            .capabilities + "|" + mapa.mapData + "|1")
        enviar(perso, packet)
        imprimir("CAMBIO MAPA: PERSO", packet)
    }

    fun ENVIAR_GDE_FRAME_OBJECT_EXTERNAL(perso: Personaje, str: String) {
        val packet = "GDE|$str"
        enviarEnCola(perso, packet, true)
        imprimir("FRAME OBJ EXT: PERSO", packet)
    }

    fun ENVIAR_GDE_FRAME_OBJECT_EXTERNAL(mapa: Mapa, str: String) {
        val packet = "GDE|$str"
        for (pj in mapa.arrayPersonajes!!) {
            enviarEnCola(pj, packet, true)
        }
        imprimir("FRAME OBJ EXT: MAPA", packet)
    }

    fun ENVIAR_GDK_CARGAR_MAPA(ss: Personaje) {
        val packet = "GDK"
        enviarEnCola(ss, packet, true)
        imprimir("CARGAR MAPA: PERSO", packet)
    }

    fun ENVIAR_fL_LISTA_PELEAS_AL_MAPA(mapa: Mapa) {
        val packet = StringBuilder("fL")
        var peleas = 0
        for (pelea in mapa.peleas!!.values) {
            if (peleas > 0) {
                packet.append("|")
            }
            try {
                val info = pelea.strParaListaPelea()
                if (info.isNotEmpty()) {
                    packet.append(info)
                    peleas++
                }
            } catch (ignored: Exception) {
            }

        }
        for (pj in mapa.arrayPersonajes!!) {
            if (pj.pelea == null) {
                enviarEnCola(pj, packet.toString(), true)
                enviarEnCola(pj, "fC$peleas", true)
            }
        }
        imprimir("LISTA PELEAS: MAPA", packet.toString())
    }

    fun ENVIAR_fL_LISTA_PELEAS(ss: Personaje, mapa: Mapa) {
        val packet = StringBuilder("fL")
        var peleas = 0
        for (pelea in mapa.peleas!!.values) {
            if (peleas > 0) {
                packet.append("|")
            }
            try {
                val info = pelea.strParaListaPelea()
                if (info.isNotEmpty()) {
                    packet.append(info)
                    peleas++
                }
            } catch (ignored: Exception) {
            }

        }
        enviarEnCola(ss, packet.toString(), true)
        enviarEnCola(ss, "fC$peleas", true)
        imprimir("LISTA PELEAS: PERSO", packet.toString())
    }

    fun ENVIAR_fC_CANTIDAD_DE_PELEAS(ss: Personaje, mapa: Mapa) {
        val packet = "fC" + mapa.numeroPeleas
        enviarEnCola(ss, packet, true)
        imprimir("CANTIDAD PELEAS: PERSO", packet)
    }

    fun ENVIAR_fC_CANTIDAD_DE_PELEAS(mapa: Mapa) {
        val packet = "fC" + mapa.numeroPeleas
        for (pj in mapa.arrayPersonajes!!) {
            if (pj.pelea == null) {
                enviarEnCola(pj, packet, true)
            }
        }
        imprimir("CANTIDAD PELEAS: MAPA", packet)
    }

    fun ENVIAR_GJK_UNIRSE_PELEA(
        perso: Personaje, estado: Int, botonCancelar: Boolean,
        mostrarBotones: Boolean, espectador: Boolean, tiempo: Long, tipoPelea: Int
    ) {
        val packet = ("GJK" + estado + "|" + (if (botonCancelar) 1 else 0) + "|" + (if (mostrarBotones) 1 else 0) + "|"
                + (if (espectador) 1 else 0) + "|" + tiempo + "|" + tipoPelea)
        enviarEnCola(perso, packet, true)
        imprimir("UNIRSE PELEA: PERSO", packet)
    }

    fun ENVIAR_GJK_UNIRSE_PELEA(
        pelea: Pelea, equipos: Int, estado: Int,
        botonCancelar: Boolean, mostrarBotones: Boolean, espectador: Boolean, tiempo: Long,
        tipoPelea: Int
    ) {
        val packet = ("GJK" + estado + "|" + (if (botonCancelar) 1 else 0) + "|" + (if (mostrarBotones) 1 else 0) + "|"
                + (if (espectador) 1 else 0) + "|" + tiempo + "|" + tipoPelea)
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("UNIRSE PELEA: PELEA", packet)
    }

    fun ENVIAR_GP_POSICIONES_PELEA(perso: Personaje, posiciones: String, equipo: Int) {
        val packet = "GP$posiciones|$equipo"
        enviarEnCola(perso, packet, true)
        imprimir("POSICIONES PELEA: PERSO", packet)
    }

    fun ENVIAR_GP_POSICIONES_PELEA(
        pelea: Pelea, equipos: Int, posiciones: String,
        colorEquipo: Int
    ) {
        val packet = "GP$posiciones|$colorEquipo"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("POSICIONES PELEA: PELEA", packet)
    }

    fun ENVIAR_Gc_MOSTRAR_ESPADA_EN_MAPA(mapa: Mapa, packet: String) {
        for (perso in mapa.arrayPersonajes!!) {
            if (perso.pelea == null) {
                enviarEnCola(perso, packet, true)
            }
        }
        imprimir("MOSTRAR ESPADA: MAPA", packet)
    }

    fun ENVIAR_Gc_MOSTRAR_ESPADA_A_JUGADOR(perso: Personaje, packet: String) {
        enviarEnCola(perso, packet, true)
        imprimir("MOSTRAR ESPADA: PERSO", packet)
    }

    fun ENVIAR_Gc_BORRAR_ESPADA_EN_MAPA(mapa: Mapa, idPelea: Int) {
        val packet = "Gc-$idPelea"
        for (perso in mapa.arrayPersonajes!!) {
            if (perso.pelea == null) {
                enviarEnCola(perso, packet, true)
            }
        }
        imprimir("BORRAR ESPADA: MAPA", packet)
    }

    fun ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapa: Mapa, idInit1: Int, luchador: Luchador) {
        val packet = "Gt" + idInit1 + "|+" + luchador.id + ";" + luchador.nombre + ";" + luchador
            .nivel
        for (perso in mapa.arrayPersonajes!!) {
            if (perso.pelea == null) {
                enviarEnCola(perso, packet, true)
            }
        }
        imprimir("AGREGAR NOMBRE ESPADA: MAPA", packet)
    }

    fun ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(perso: Personaje, idInit1: Int, str: String) {
        val packet = "Gt$idInit1|+$str"
        enviarEnCola(perso, packet, true)
        imprimir("AGREGAR NOMBRE ESPADA: PERSO", packet)
    }

    fun ENVIAR_Gt_BORRAR_NOMBRE_ESPADA(mapa: Mapa, idInit1: Int, luchador: Luchador) {
        val packet = "Gt" + idInit1 + "|-" + luchador.id
        for (perso in mapa.arrayPersonajes!!) {
            if (perso.pelea == null) {
                enviarEnCola(perso, packet, true)
            }
        }
        imprimir("BORRAR NOMBRE ESPADA: MAPA", packet)
    }

    fun ENVIAR_Os_SETS_RAPIDOS(perso: Personaje) {
        val packet = "Os" + perso.setsRapidos
        enviarEnCola(perso, packet, true)
        imprimir("SETS RAPIDOS: PERSO", packet)
    }

    fun ENVIAR_Oa_CAMBIAR_ROPA_MAPA(mapa: Mapa, perso: Personaje) {
        val packet = perso.strRopaDelPJ()
        for (pj in mapa.arrayPersonajes!!) {
            if (pj.pelea == null) {
                enviarEnCola(pj, packet, true)
            }
        }
        imprimir("CAMBIAR ROPA: MAPA", packet)
    }

    fun ENVIAR_Oa_CAMBIAR_ROPA_PELEA(pelea: Pelea, perso: Personaje) {
        val packet = perso.strRopaDelPJ()
        for (luchador in pelea.luchadoresDeEquipo(3)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("CAMBIAR ROPA: PELEA", packet)
    }

    fun ENVIAR_GIC_CAMBIAR_POS_PELEA(
        pelea: Pelea, equipos: Int, mapa: Mapa, id: Int,
        celda: Short
    ) {
        val packet = "GIC|$id;$celda"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("CAMBIAR POS PELEA: PELEA", packet)
    }

    fun ENVIAR_Go_BOTON_ESPEC_AYUDA(mapa: Mapa, s: Char, opcion: Char, id: Int) {
        val packet = "Go$s$opcion$id"
        for (pj in mapa.arrayPersonajes!!) {
            enviarEnCola(pj, packet, true)
        }
        imprimir("BOT. ESPEC. AYUDA: MAPA", packet)
    }

    fun ENVIAR_Go_BOTON_ESPEC_AYUDA(perso: Personaje, s: Char, opcion: Char, id: Int) {
        val packet = "Go$s$opcion$id"
        enviarEnCola(perso, packet, true)
        imprimir("BOT. ESPEC. AYUDA: PERSO", packet)
    }

    fun ENVIAR_GR_TODOS_LUCHADORES_LISTOS(
        pelea: Pelea, equipos: Int, id: Int,
        b: Boolean
    ) {
        val packet = "GR" + (if (b) "1" else "0") + id
        if (pelea.fase.toInt() != 2) {
            return
        }
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("LUCHADORES LISTO: PELEA", packet)
    }

    fun ENVIAR_Im_INFORMACION(perso: Personaje?, str: String) {
        val packet = "Im$str"
        if (perso != null) {
            enviarEnCola(perso, packet, true)
        }
        imprimir("INFORMACION: PERSO", packet)
    }

    fun ENVIAR_Im_INFORMACION_A_TODOS(str: String) {
        val packet = "Im$str"
        for (perso in Mundo.PERSONAJESONLINE) {
            enviarEnCola(perso, packet, false)
        }
        imprimir("INFORMACION: TODOS", packet)
    }

    fun ENVIAR_Im_INFORMACION_KOLISEO(str: String) {
        val packet = "Im$str"
        for (perso in Mundo.inscritosKoliseo) {
            enviarEnCola(perso, packet, false)
        }
        imprimir("INFORMACION: KOLISEO", packet)
    }

    fun ENVIAR_Im1223_MENSAJE_IMBORRABLE_PELEA(pelea: Pelea, equipos: Int, str: String) {
        val packet = "Im1223;$str"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("Im1223: PELEA", packet)
    }

    fun ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(es: String, fr: String) {
        val packet = "Im1223;$es"
        val packetFr = "Im1223;$fr"
        for (perso in Mundo.PERSONAJESONLINE) {
            if (perso.cuenta.idioma == "fr" && fr.isNotEmpty()) {
                enviarEnCola(perso, packetFr, false)
            } else if (es.isNotEmpty()) {
                enviarEnCola(perso, packet, false)
            }
        }
        imprimir("Im1223: TODOS", packet)
    }

    fun ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(es: String) {
        val packet = "Im1223;$es"
        for (perso in Mundo.PERSONAJESONLINE) {
            enviarEnCola(perso, packet, false)
        }
        imprimir("Im1223: TODOS", packet)
    }

    fun ENVIAR_Im1223_MENSAJE_IMBORRABLE_KOLISEO(es: String, fr: String) {
        val packet = "Im1223;$es"
        val packetFr = "Im1223;$fr"
        for (perso in Mundo.inscritosKoliseo) {
            if (perso.cuenta.idioma == "fr" && fr.isNotEmpty()) {
                enviarEnCola(perso, packetFr, false)
            } else if (es.isNotEmpty()) {
                enviarEnCola(perso, packet, false)
            }
        }
        imprimir("Im1223: KOLISEO", packet)
    }

    fun ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso: Personaje, str: String) {
        val packet = "Im1223;$str"
        enviarEnCola(perso, packet, true)
        imprimir("Im1223: PERSO", packet)
    }

    fun ENVIAR_ILS_TIEMPO_REGENERAR_VIDA(perso: Personaje, tiempoRegen: Int) {
        val packet = "ILS$tiempoRegen"
        enviarEnCola(perso, packet, true)
        imprimir("TIEMPO REGEN VIDA: PERSO", packet)
    }

    fun ENVIAR_ILF_CANTIDAD_DE_VIDA(perso: Personaje, cantidad: Int) {
        val packet = "ILF$cantidad"
        enviarEnCola(perso, packet, true)
        imprimir("CANT VIDA REGENERADA: PERSO", packet)
    }

    fun ENVIAR_Im_INFORMACION_A_MAPA(mapa: Mapa, id: String) {
        val packet = "Im$id"
        for (pj in mapa.arrayPersonajes!!) {
            enviarEnCola(pj, packet, true)
        }
        imprimir("INFORMACION: MAPA", packet)
    }

    fun ENVIAR_eUK_EMOTE_MAPA(mapa: Mapa, id: Int, emote: Int, tiempo: Int) {
        val packet = "eUK$id|$emote|$tiempo"
        for (pj in mapa.arrayPersonajes!!) {
            enviarEnCola(pj, packet, true)
        }
        imprimir("EMOTE: MAPA", packet)
    }

    fun ENVIAR_Im_INFORMACION_A_PELEA(pelea: Pelea, equipos: Int, msj: String) {
        val packet = "Im$msj"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("INFORMACION: PELEA", packet)
    }

    fun ENVIAR_cs_CHAT_MENSAJE(perso: Personaje, msj: String, color: String) {
        val packet = "cs<font color='#$color'>$msj</font>"
        enviarEnCola(perso, packet, true)
        imprimir("CHAT: PERSO", packet)
    }

    fun ENVIAR_cs_CHAT_MENSAJE_A_TODOS(msj: String, color: String) {
        val packet = "cs<font color='#$color'>$msj</font>"
        for (pj in Mundo.PERSONAJESONLINE) {
            enviarEnCola(pj, packet, true)
        }
        imprimir("CHAT: TODOS", packet)
    }

    fun ENVIAR_cs_CHAT_MENSAJE_A_MAPA(mapa: Mapa, msj: String, color: String) {
        val packet = "cs<font color='#$color'>$msj</font>"
        for (pj in mapa.arrayPersonajes!!) {
            enviarEnCola(pj, packet, true)
        }
        imprimir("CHAT: MAPA", packet)
    }

    fun ENVIAR_cs_CHAT_MENSAJE_A_PELEA(pelea: Pelea, msj: String, color: String) {
        val packet = "cs<font color='#$color'>$msj</font>"
        for (luchador in pelea.luchadoresDeEquipo(7)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("CHAT: MAPA", packet)
    }

    fun ENVIAR_GA900_DESAFIAR(mapa: Mapa, id: Int, id2: Int) {
        val packet = "GA;900;$id;$id2"
        for (pj in mapa.arrayPersonajes!!) {
            if (pj.pelea == null) {
                enviarEnCola(pj, packet, true)
            }
        }
        imprimir("DESAFIAR: MAPA ID " + mapa.id + ": MAPA", packet)
    }

    fun ENVIAR_GA901_ACEPTAR_DESAFIO(pj: Personaje, id: Int, id2: Int) {
        val packet = "GA;901;$id;$id2"
        enviarEnCola(pj, packet, true)
        imprimir("ACEPTAR DESAFIO: MAPA", packet)
    }

    fun ENVIAR_GA902_RECHAZAR_DESAFIO(pj: Personaje, id: Int, id2: Int) {
        val packet = "GA;902;$id;$id2"
        enviarEnCola(pj, packet, true)
        imprimir("RECHAZAR DESAFIO: PERSO", packet)
    }

    fun ENVIAR_GA903_ERROR_PELEA(perso: Personaje?, c: Char) {
        if (perso == null) {
            return
        }
        val packet = "GA;903;;$c"
        enviarEnCola(perso, packet, true)
        imprimir("ERROR JUEGO: PERSO", packet)
    }

    fun ENVIAR_GIC_UBICACION_LUCHADORES_INICIAR(pelea: Pelea, equipos: Int) {
        val packet = StringBuilder("GIC|")
        for (luchador in pelea.luchadoresDeEquipo(3)) {
            if (luchador.estaRetirado() || luchador.celdaPelea == null || luchador.esMultiman()) {
                continue
            }
            packet.append(luchador.id).append(";").append(luchador.celdaPelea!!.id.toInt()).append("|")
        }
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet.toString(), true)
        }
        imprimir("UBIC LUCH INICIAR: PELEA", packet.toString())
    }

    fun ENVIAR_GIC_APARECER_LUCHADORES_INVISIBLES(
        pelea: Pelea, equipos: Int,
        luch: Luchador
    ) {
        val packet = "GIC|" + luch.id + ";" + luch.celdaPelea!!.id
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("APARECER LUCH INVI: PELEA", packet)
    }

    fun ENVIAR_GIC_APARECER_LUCHADORES_INVISIBLES(luchador: Luchador, str: String) {
        val packet = "GIC|$str"
        if (luchador.estaRetirado() || luchador.esMultiman()) {
            return
        }
        enviarEnCola(luchador.personaje, packet, true)
        imprimir("APARECER LUCH INVI: PERSO", packet)
    }

    fun ENVIAR_GS_EMPEZAR_COMBATE_EQUIPOS(pelea: Pelea, equipos: Int) {
        val packet = "GS"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("INICIAR PELEA: PELEA", packet)
    }

    fun ENVIAR_GS_EMPEZAR_COMBATE(perso: Personaje) {
        val packet = "GS"
        enviarEnCola(perso, packet, true)
        imprimir("INICIO PELEA: PERSO", packet)
    }

    fun ENVIAR_GTL_ORDEN_JUGADORES(pelea: Pelea, equipos: Int) {
        val packet = pelea.stringOrdenJugadores()
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("ORDEN LUCH: PELEA", packet)
    }

    fun ENVIAR_GTL_ORDEN_JUGADORES(perso: Personaje, pelea: Pelea) {
        val packet = pelea.stringOrdenJugadores()
        enviarEnCola(perso, packet, true)
        imprimir("ORDEN LUCH: PERSO", packet)
    }

    fun ENVIAR_Gñ_IDS_PARA_MODO_CRIATURA(pelea: Pelea, perso: Personaje) {
        val packet = StringBuilder("Gñ")
        for (luchador in pelea.luchadoresDeEquipo(3)) {
            if (luchador.estaMuerto() || luchador.esInvisible(perso.Id)) {
                continue
            }
            if (packet.length > 2) {
                packet.append(",")
            }
            packet.append(luchador.id)
        }
        enviarEnCola(perso, packet.toString(), true)
        imprimir("IDS MODO CRIATURA: PERSO", packet.toString())
    }

    fun ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_TODOS(
        pelea: Pelea, equipos: Int,
        como999: Boolean
    ) {
        val aEnviar = pelea.luchadoresDeEquipo(equipos)
        for (luchador in pelea.luchadoresDeEquipo(3)) {
            val totalStats = luchador.totalStats
            val packet1 = StringBuilder()
            val packet2 = StringBuilder()
            packet1.append("|").append(luchador.id).append(";")
            if (luchador.estaMuerto()) {
                packet1.append(1.toString() + ";")
            } else {
                if (como999 && !luchador.updateGTM) {
                    continue
                }
                packet1.append(0.toString() + ";")
                packet1.append(luchador.pdvConBuff).append(";")
                packet1.append(max(0, luchador.paRestantes)).append(";")
                packet1.append(max(0, luchador.pmRestantes)).append(";")// PM
                packet2.append(";")
                packet2.append(luchador.pdvMaxConBuff).append(";")
                packet2.append(totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_HUIDA)).append(";")
                packet2.append(totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_PLACAJE)).append(";")
                val resist = IntArray(7)
                when (pelea.tipoPelea) {
                    Constantes.PELEA_TIPO_DESAFIO.toInt(), Constantes.PELEA_TIPO_KOLISEO.toInt(), Constantes.PELEA_TIPO_PVP.toInt(), Constantes.PELEA_TIPO_RECAUDADOR.toInt() -> {
                        resist[0] = Constantes.STAT_MAS_RES_PORC_PVP_NEUTRAL
                        resist[1] = Constantes.STAT_MAS_RES_PORC_PVP_TIERRA
                        resist[2] = Constantes.STAT_MAS_RES_PORC_PVP_FUEGO
                        resist[3] = Constantes.STAT_MAS_RES_PORC_PVP_AGUA
                        resist[4] = Constantes.STAT_MAS_RES_PORC_PVP_AIRE
                    }
                    else -> {
                        resist[0] = Constantes.STAT_MAS_RES_PORC_NEUTRAL
                        resist[1] = Constantes.STAT_MAS_RES_PORC_TIERRA
                        resist[2] = Constantes.STAT_MAS_RES_PORC_FUEGO
                        resist[3] = Constantes.STAT_MAS_RES_PORC_AGUA
                        resist[4] = Constantes.STAT_MAS_RES_PORC_AIRE
                    }
                }
                resist[5] = Constantes.STAT_MAS_ESQUIVA_PERD_PA
                resist[6] = Constantes.STAT_MAS_ESQUIVA_PERD_PM
                for (statID in resist) {
                    val total = totalStats.getTotalStatConComplemento(statID)
                    packet2.append(total).append(",")
                }
                luchador.updateGTM = false
            }
            for (enviar in aEnviar) {
                if (enviar.estaRetirado() || enviar.personaje == null || enviar.esMultiman()) {
                    continue
                }
                enviar.stringBuilderGTM.append(packet1.toString())
                if (!luchador.estaMuerto()) {
                    enviar.stringBuilderGTM.append(
                        if (luchador.celdaPelea == null || luchador.esInvisible(enviar.id))
                            "-1"
                        else
                            luchador.celdaPelea!!.id
                    ).append(";").append(packet2.toString())
                }
            }
        }
        for (enviar in aEnviar) {
            if (enviar.estaRetirado() || enviar.personaje == null || enviar.esMultiman()) {
                continue
            }
            if (enviar.stringBuilderGTM.toString().isEmpty()) {
                continue
            }
            var packet = ""
            packet += if (como999) {
                "GA;999;;" + "GTU"
            } else {
                "GTM"
            }
            packet += enviar.stringBuilderGTM.toString()
            enviar.resetStringBuilderGTM()
            enviarEnCola(enviar.personaje, packet, true)
            imprimir("INFO STATS LUCH: PERSO " + enviar.id + "", packet)
        }
    }

    fun ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_PERSO(perso: Personaje?, pelea: Pelea) {
        if (perso == null) {
            return
        }
        val packet = StringBuilder("GTM")
        for (luchador in pelea.luchadoresDeEquipo(3)) {
            val totalStats = luchador.totalStats
            packet.append("|").append(luchador.id).append(";")
            if (luchador.estaMuerto()) {
                packet.append(1.toString() + ";")
            } else {
                packet.append(0.toString() + ";")
                packet.append(luchador.pdvConBuff).append(";")
                packet.append(max(0, luchador.paRestantes)).append(";")
                packet.append(max(0, luchador.pmRestantes)).append(";")// PM
                packet.append(
                    if (luchador.celdaPelea == null || luchador.esInvisible(perso.Id))
                        "-1"
                    else
                        luchador.celdaPelea!!.id
                ).append(";")
                packet.append(";")
                packet.append(luchador.pdvMaxConBuff).append(";")
                packet.append(totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_HUIDA)).append(";")
                packet.append(totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_PLACAJE)).append(";")
                val resist = IntArray(7)
                when (pelea.tipoPelea) {
                    Constantes.PELEA_TIPO_DESAFIO.toInt(), Constantes.PELEA_TIPO_KOLISEO.toInt(), Constantes.PELEA_TIPO_PVP.toInt(), Constantes.PELEA_TIPO_RECAUDADOR.toInt() -> {
                        resist[0] = Constantes.STAT_MAS_RES_PORC_PVP_NEUTRAL
                        resist[1] = Constantes.STAT_MAS_RES_PORC_PVP_TIERRA
                        resist[2] = Constantes.STAT_MAS_RES_PORC_PVP_FUEGO
                        resist[3] = Constantes.STAT_MAS_RES_PORC_PVP_AGUA
                        resist[4] = Constantes.STAT_MAS_RES_PORC_PVP_AIRE
                    }
                    else -> {
                        resist[0] = Constantes.STAT_MAS_RES_PORC_NEUTRAL
                        resist[1] = Constantes.STAT_MAS_RES_PORC_TIERRA
                        resist[2] = Constantes.STAT_MAS_RES_PORC_FUEGO
                        resist[3] = Constantes.STAT_MAS_RES_PORC_AGUA
                        resist[4] = Constantes.STAT_MAS_RES_PORC_AIRE
                    }
                }
                resist[5] = Constantes.STAT_MAS_ESQUIVA_PERD_PA
                resist[6] = Constantes.STAT_MAS_ESQUIVA_PERD_PM
                for (statID in resist) {
                    val total = totalStats.getTotalStatConComplemento(statID)
                    packet.append(total).append(",")
                }
            }
        }
        enviarEnCola(perso, packet.toString(), true)
        imprimir("INFO STATS LUCH: PERSO", packet.toString())
    }

    fun ENVIAR_GTS_INICIO_TURNO_PELEA(
        pelea: Pelea, equipos: Int, id: Int,
        tiempo: Int
    ) {
        val packet = "GTS$id|$tiempo"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("INICIO TURNO: PELEA", packet)
    }

    fun ENVIAR_GTS_INICIO_TURNO_PELEA(perso: Personaje, id: Int, tiempo: Int) {
        val packet = "GTS$id|$tiempo"
        enviarEnCola(perso, packet, true)
        imprimir("INICIO TURNO: PERSO", packet)
    }

    fun ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(perso: Personaje) {
        val packet = "GV"
        enviarEnCola(perso, packet, true)
        imprimir("RESETEAR PANTALLA JUEGO: PERSO", packet)
    }

    fun ENVIAR_GAS_INICIO_DE_ACCION(perso: Personaje, id: Int) {
        val packet = "GAS$id"
        enviarEnCola(perso, packet, true)
        imprimir("INICIO ACCION: PELEA", packet)
    }

    fun ENVIAR_GA_ACCION_DE_JUEGO(
        perso: Personaje, accionID: Int, s2: String,
        s3: String
    ) {
        if (Constantes.esAccionParaMostrar(accionID)) {
            return
        }
        var packet = "GA;$accionID"
        if (s2.isNotEmpty()) {
            packet += ";$s2"
        }
        if (s3.isNotEmpty()) {
            packet += ";$s3"
        }
        enviarEnCola(perso, packet, true)
        imprimir("ACCION DE JUEGO: PERSO", packet)
    }

    fun ENVIAR_GA_ACCION_PELEA_LUCHADOR(
        luchador: Luchador, accionID: Int, s2: String,
        s3: String
    ) {
        if (Constantes.esAccionParaMostrar(accionID)) {
            return
        }
        if (luchador.estaRetirado() || luchador.esMultiman()) {
            return
        }
        var packet = "GA;$accionID;$s2"
        if (s3.isNotEmpty()) {
            packet += ";$s3"
        }
        enviarEnCola(luchador.personaje, packet, true)
        imprimir("ACCION PELEA: PERSO", packet)
        //		try {
        //			Thread.sleep(EfectoHechizo.TIEMPO_GAME_ACTION);
        //		} catch (Exception e) {}
    }

    // public static void ENVIAR_GA_ACCION_PELEA_CON_DURACION(final Pelea pelea, final int equipos,
    // final int accionID, final Luchador lanzador,
    // final int idObjetivo , final int valor, final int duracion) {
    // final StringBuilder packet = new StringBuilder("GA;" + accionID + ";" + lanzador.getID() + ";"
    // + idObjetivo +"," + valor +","+ (lanzador.puedeJugar() ? duracion + 1 ) );
    // for (final Luchador luchador : pelea.luchadoresDeEquipo(equipos)) {
    // if (luchador.estaRetirado() || luchador.esMultiman()) {
    // continue;
    // }
    // enviar(luchador.getPersonaje(), packet.toString());
    // }
    // if (Bustemu.MOSTRAR_ENVIOS) {
    // imprimir("ACCION PELEA DURACION: PELEA" , packet.toString());
    // }
    // }
    fun ENVIAR_GA_ACCION_PELEA(
        pelea: Pelea, equipos: Int, accionID: Int, s2: String,
        s3: String
    ) {
        if (Constantes.esAccionParaMostrar(accionID)) {
            return
        }
        var packet = "GA;$accionID;$s2"
        if (s3.isNotEmpty()) {
            packet += ";$s3"
        }
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            if (luchador.personaje != null) {
                enviarEnCola(luchador.personaje, packet, true)
            }
        }
        imprimir("ACCION PELEA: PELEA", packet)
        //		try {
        //			Thread.sleep(EfectoHechizo.TIEMPO_GAME_ACTION);
        //		} catch (Exception e) {}
    }

    fun ENVIAR_GA950_ACCION_PELEA_ESTADOS(
        pelea: Pelea, equipos: Int, afectado: Int, estado: Int,
        activo: Boolean
    ) {
        val accionID = 950
        if (Constantes.esAccionParaMostrar(accionID)) {
            return
        }
        val packet = "GA;" + accionID + ";" + afectado + ";" + afectado + "," + estado + "," + if (activo) 1 else 0
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("ACCION 950 ESTADOS: PELEA", packet)
        //		try {
        //			Thread.sleep(EfectoHechizo.TIEMPO_GAME_ACTION);
        //		} catch (Exception e) {}
    }

    fun ENVIAR_GA_ACCION_PELEA_CON_RESPUESTA(
        pelea: Pelea, equipos: Int, respuestaID: Int,
        accionID: Int, s2: String, s3: String
    ) {
        if (Constantes.esAccionParaMostrar(accionID)) {
            return
        }
        val packet = "GA$respuestaID;$accionID;$s2;$s3"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("ACCION PELEA CON RESP.: PELEA", packet)
    }

    fun ENVIAR_GA_ACCION_PELEA_MOVERSE(
        pelea: Pelea, movedor: Luchador, equipos: Int,
        respuestaID: Int, accionID: Int, s2: String, s3: String
    ) {
        val packet = "GA$respuestaID;$accionID;$s2;$s3"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || movedor.esInvisible(luchador.id) || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("ACCION PELEA MOVERSE: PELEA", packet)
    }

    // public static void ENVIAR_GA_PERDER_PM_PELEA(final Pelea pelea, final int equipos, final String
    // packet) {
    // if (packet.isEmpty()) {
    // return;
    // }
    // for (final Luchador luchador : pelea.luchadoresDeEquipo(equipos)) {
    // if (luchador.estaRetirado() || luchador.esMultiman()) {
    // continue;
    // }
    // enviar(luchador.getPersonaje(), packet, true);
    // }
    // if (Bustemu.MOSTRAR_ENVIOS) {
    // imprimir("PM USADOS PARA MOVERSE: PELEA" , packet);
    // }
    // }
    fun ENVIAR_GAF_FINALIZAR_ACCION(perso: Personaje, luchID: Int, unicaID: Int) {
        var packet = "GAF$luchID"
        if (unicaID >= 0) {
            packet += "|$unicaID"// si se pone accion envia un GKK(ID_UNICA)
        }
        enviarEnCola(perso, packet, true)
        imprimir("FINALIZAR ACCION: PERSO", packet)
    }

    fun ENVIAR_GAs_PARAR_MOVIMIENTO_SPRITE(perso: Personaje, luchID: Int) {
        val packet = "GAF$luchID"
        enviarEnCola(perso, packet, true)
        imprimir("PARAR MOVIMIENTO SPRITE: PERSO", packet)
    }

    fun ENVIAR_GAC_LIMPIAR_ACCION(perso: Personaje) {
        val packet = "GAC" + Constantes.x0char + "GA;940"
        enviarEnCola(perso, packet, true)
        imprimir("LIMPIAR ACCION: PERSO", packet)
    }

    fun ENVIAR_GA_DEBUG_ACCIONES(perso: Personaje) {
        val packet = "GA;940"
        enviarEnCola(perso, packet, true)
        imprimir("DEBUG ACCIONES: PERSO", packet)
    }

    fun ENVIAR_GTF_FIN_DE_TURNO(pelea: Pelea, equipos: Int, id: Int) {
        val packet = "GTF$id"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("FIN TURNO: PELEA", packet)
    }

    fun ENVIAR_GTR_TURNO_LISTO(pelea: Pelea, equipos: Int, id: Int) {
        val packet = "GTR$id"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("TURNO LISTO: PELEA", packet)
    }

    fun ENVIAR_cS_EMOTICON_MAPA(mapa: Mapa, id: Int, pid: Int) {
        val packet = "cS$id|$pid"
        for (pj in mapa.arrayPersonajes!!) {
            enviarEnCola(pj, packet, true)
        }
        imprimir("EMOTE: MAPA", packet)
    }

    fun ENVIAR_SUE_NIVEL_HECHIZO_ERROR(ss: Personaje) {
        val packet = "SUE"
        enviarEnCola(ss, packet, true)
        imprimir("NIVEL HECHIZO ERROR: OUT", packet)
    }

    fun ENVIAR_SUK_NIVEL_HECHIZO(perso: Personaje, hechizoID: Int, nivel: Int) {
        val packet = "SUK$hechizoID~$nivel"
        enviarEnCola(perso, packet, true)
        imprimir("NIVEL HECHIZOS: PERSO", packet)
    }

    fun ENVIAR_SL_LISTA_HECHIZOS(perso: Personaje) {
        val packet = "SL" + perso.stringListaHechizos()
        enviarEnCola(perso, packet, true)
        imprimir("LISTA HECHIZOS: PERSO", packet)
    }

    fun ENVIAR_SL_LISTA_HECHIZOS_A_LIDER(perso: Personaje, lider: Personaje) {
        val packet = "SL" + perso.stringListaHechizos()
        enviarEnCola(lider, packet, true)
        imprimir("LISTA HECHIZOS: PERSO", packet)
    }

    fun ENVIAR_GA103_JUGADOR_MUERTO(pelea: Pelea, equipos: Int, id: Int) {
        val packet = "GA;103;$id;$id"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("LUCH. MUERTO: PELEA", packet)
    }

    fun ENVIAR_GE_PANEL_RESULTADOS_PELEA(pelea: Pelea, equipos: Int, packet: String) {
        val ips = ArrayList<String>()
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            if (luchador.personaje != null) {
                val ip = luchador.personaje!!.cuenta.actualIP
                if (!ips.contains(ip)) {
                    ips.add(ip)
                    enviarEnCola(luchador.personaje, packet, true)
                    continue
                }
                enviarEnCola(luchador.personaje, packet, true)

//                ENVIAR_EV_CERRAR_VENTANAS(luchador.personaje,"")
//                ENVIAR_bV_CERRAR_PANEL(luchador.personaje)
//                luchador.personaje.teleport(luchador.personaje.mapa.id,luchador.personaje.celda.id)
            }
        }
        imprimir("PANEL RESULTADOS: PELEA", packet)
    }

    fun ENVIAR_GA998_AGREGAR_BUFF_PELEA(pelea: Pelea, equipos: Int, packet: String) {
        var packet = packet
        if (packet.isEmpty()) {
            return
        }
        packet = "GA;998;$packet"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("AGREGAR BUFF: PELEA", packet)
    }

    fun ENVIAR_GA998_AGREGAR_BUFF(perso: Personaje, packet: String) {
        var packet = packet
        if (packet.isEmpty()) {
            return
        }
        packet = "GA;998;$packet"
        enviarEnCola(perso, packet, true)
        imprimir("AGREGAR BUFF: PERSO", packet)
    }

    fun ENVIAR_GIE_AGREGAR_BUFF_PELEA(pelea: Pelea, equipos: Int, packet: String) {
        var packet = packet
        if (packet.isEmpty()) {
            return
        }
        packet = "GIE$packet"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("AGREGAR BUFF: PELEA", packet)
    }

    fun ENVIAR_GIE_AGREGAR_BUFF(perso: Personaje, packet: String) {
        var packet = packet
        if (packet.isEmpty()) {
            return
        }
        packet = "GIE$packet"
        enviarEnCola(perso, packet, true)
        imprimir("AGREGAR BUFF: PERSO", packet)
    }

    fun ENVIAR_GIe_QUITAR_BUFF(pelea: Pelea, equipos: Int, id: Int) {
        val packet = "GIe$id"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("QUITAR BUFFS: PELEA", packet)
    }

    fun ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(
        perso: Personaje, sufijo: String, id: Int,
        nombre: String, msj: String
    ) {
        if (perso.tieneCanal(sufijo)) {
            return
        }
        val packet = "cMK$sufijo|$id|$nombre|$msj"
        enviarEnCola(perso, packet, true)
        imprimir("CHAT: PERSO", packet)
    }

    fun ENVIAR_cMK_CHAT_MENSAJE_PELEA(
        pelea: Pelea, equipos: Int, sufijo: String,
        id: Int, nombre: String, msj: String
    ) {
        val packet = "cMK$sufijo|$id|$nombre|$msj"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador == null) {
                continue
            }
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            val p = luchador.personaje
            if (p == null || p.tieneCanal(sufijo)) {
                continue
            }
            enviarEnCola(p, packet, true)
        }
        imprimir("CHAT: PELEA", packet)
    }

    fun ENVIAR_cMK_CHAT_MENSAJE_MAPA(perso: Personaje, sufijo: String, msj: String) {
        val packet = "cMK" + sufijo + "|" + perso.Id + "|" + perso.nombre + "|" + msj
        for (p in perso.mapa.arrayPersonajes!!) {
            if (p.tieneCanal(sufijo)) {
                continue
            }
            enviarEnCola(p, packet, true)
        }
        imprimir("CHAT: MAPA", packet)
    }

    fun ENVIAR_cMK_MENSAJE_CHAT_GRUPO(perso: Personaje, msj: String) {
        val sufijo = "$"
        val packet = "cMK" + sufijo + "|" + perso.Id + "|" + perso.nombre + "|" + msj
        for (p in perso.grupoParty.miembros) {
            if (p.tieneCanal(sufijo)) {
                continue
            }
            enviarEnCola(p, packet, true)
        }
        imprimir("MSJ CHAT GRUPO: GRUPO", packet)
    }

    fun ENVIAR_cMK_CHAT_MENSAJE_GREMIO(perso: Personaje, msj: String) {
        val sufijo = "%"
        val packet = "cMK" + sufijo + "|" + perso.Id + "|" + perso.nombre + "|" + msj
        for (p in perso.gremio!!.miembros) {
            if (p != null) {
                if (p.tieneCanal(sufijo)) {
                    continue
                }
            }
            enviarEnCola(p, packet, true)
        }
        imprimir("CHAT: GREMIO", packet)
    }

    fun ENVIAR_cMK_CHAT_MENSAJE_KOLISEO(perso: Personaje, msj: String) {
        val sufijo = "¿"
        val packet = "cMK" + sufijo + "|" + perso.Id + "|" + perso.nombre + "|" + msj
        for (p in perso.grupoKoliseo.miembros) {
            if (p.tieneCanal(sufijo)) {
                continue
            }
            enviarEnCola(p, packet, true)
        }
        imprimir("CHAT: KOLISEO", packet)
    }

    fun ENVIAR_cMK_CHAT_MENSAJE_TODOS(sufijo: String, perso: Personaje?, msj: String) {
        if (perso == null) {
            return
        }
        val packet = "cMK" + sufijo + "|" + perso.Id + "|" + perso.nombre + "|" + msj
        try {
            loop@ for (p in Mundo.PERSONAJESONLINE) {
                if (p.tieneCanal(sufijo)) {
                    continue
                }
                if (p == null) {
                    continue
                }
                if (p.servidorSocket == null) {
                    continue
                }
                try {
                    when (sufijo) {
                        "^"// mensaje incarnam
                        -> if (p.mapa.subArea!!.area.superArea!!.id != 3) {
                            continue@loop
                        }
                        "!"// alineacion
                        -> if (p.alineacion != perso.alineacion) {
                            continue@loop
                        }
                        "¡"// vip
                        -> if (!AtlantaMain.PARAM_MOSTRAR_CHAT_VIP_TODOS && !p.cuenta.esAbonado()) {
                            continue@loop
                        }
                        "@"// admin
                        -> if (p.cuenta.admin <= 0) {
                            continue@loop
                        }
                        ":"// mensaje comercio
                        -> {
                        }
                    }
                    enviarEnCola(p, packet, false)
                } catch (e: Exception) {
                    AtlantaMain.redactarLogServidorln("Error en Comercio $e")
                }
            }
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln("Error en el comercio FOR $e")
        }
        imprimir("CHAT $sufijo : TODOS", packet)
    }

    fun ENVIAR_GDZ_COLOREAR_ZONA_EN_PELEA(
        pelea: Pelea, equipos: Int, add: String,
        celda: Short, tamaño: Int, color: Int, forma: Char
    ) {// , int radioInt
        val packet = "GDZ$add$celda;$tamaño;$color;$forma"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador == null) {
                continue
            }
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("COLOREAR ZONA: PELEA", packet)
    }

    fun ENVIAR_GDZ_COLOREAR_ZONA_A_LUCHADORES(
        luchadores: ArrayList<Luchador>, add: String,
        celda: Short, tamaño: Int, color: Int, forma: Char
    ) {
        val packet = "GDZ$add$celda;$tamaño;$color;$forma"
        for (luchador in luchadores) {
            if (luchador == null) {
                continue
            }
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("COLOREAR ZONA: LUCHADORES", packet)
    }

    fun ENVIAR_GDZ_COLOREAR_ZONA_A_LUCHADOR(
        luchador: Luchador, add: String, celda: Short,
        tamaño: Int, color: Int, forma: Char
    ) {
        val packet = "GDZ$add$celda;$tamaño;$color;$forma"
        enviarEnCola(luchador.personaje, packet, true)
        imprimir("COLOREAR ZONA: LUCHADOR", packet)
    }

    fun ENVIAR_GDC_ACTUALIZAR_CELDA_EN_PELEA(
        pelea: Pelea, equipos: Int, celda: Short,
        s1: String, permanente: Boolean
    ) {
        val packet = "GDC" + celda + ";" + s1 + ";" + if (permanente) "0" else "1"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador == null) {
                continue
            }
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("ACTUALIZAR CELDA: PELEA", packet)
    }

    fun ENVIAR_GDC_ACTUALIZAR_CELDA_A_LUCHADORES(
        luchadores: ArrayList<Luchador>, celda: Short,
        s1: String, permanente: Boolean
    ) {
        val packet = "GDC" + celda + ";" + s1 + ";" + if (permanente) "0" else "1"
        for (luchador in luchadores) {
            if (luchador == null) {
                continue
            }
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("ACTUALIZAR CELDA: LUCHADORES", packet)
    }

    fun ENVIAR_GDC_ACTUALIZAR_CELDA_A_LUCHADOR(
        luchador: Luchador, celda: Short, s1: String,
        permanente: Boolean
    ) {
        val packet = "GDC" + celda + ";" + s1 + ";" + if (permanente) "0" else "1"
        enviarEnCola(luchador.personaje, packet, true)
        imprimir("ACTUALIZAR CELDA: LUCHADOR", packet)
    }

    fun ENVIAR_GDC_ACTUALIZAR_CELDA_MAPA(
        mapa: Mapa, celda: Short, s1: String,
        permanente: Boolean
    ) {// FIXME
        val packet = "GDC" + celda + ";" + s1 + ";" + if (permanente) "0" else "1"
        for (perso in mapa.arrayPersonajes!!) {
//            enviarEnCola(perso, packet, true)
            ENVIAR_GDM_MAPDATA_COMPLETO(perso)
        }
        imprimir("AUTORIZAR CELDA: PELEA", packet)
    }

    fun ENVIAR_cMEf_CHAT_ERROR(ss: Personaje, nombre: String) {
        val packet = "cMEf$nombre"
        enviarEnCola(ss, packet, true)
        imprimir("CHAT ERROR: PERSO", packet)
    }

    fun ENVIAR_eD_CAMBIAR_ORIENTACION(mapa: Mapa, id: Int, dir: Byte) {
        val packet = "eD$id|$dir"
        for (perso in mapa.arrayPersonajes!!) {
            enviarEnCola(perso, packet, true)
        }
        imprimir("CAMBIAR ORIENTACION: MAPA", packet)
    }

    // public static void ENVIAR_eF_CAMBIAR_ORIENTACION_SI_O_SI(final Pelea pelea, final int id, final
    // byte dir) {
    // final String packet = "eF" + id + "|" + dir;
    // for (final Luchador luchador : pelea.luchadoresDeEquipo(7)) {
    // if (luchador.estaRetirado()) {
    // continue;
    // }
    // enviar(luchador.getPersonaje(), packet, true);
    // }
    // if (Bustemu.MOSTRAR_ENVIOS) {
    // imprimir("CAMBIAR ORIENTACION: PELEA" , packet);
    // }
    // }
    fun ENVIAR_TB_CINEMA_INICIO_JUEGO(ss: Personaje) {
        val packet = "TB"
        enviarEnCola(ss, packet, true)
        imprimir("CINEMA INICIO JUEGO: PERSO", packet)
    }

    fun ENVIAR_TB_CINEMA_INICIO_JUEGO(ss: ServidorSocket) {
        val packet = "TB"
        ss.enviarPW(packet)
        imprimir("CINEMA INICIO JUEGO: PERSO", packet)
    }

    fun ENVIAR_GA2_CINEMATIC(perso: Personaje, cinema: String) {
        val packet = "GA;2;;$cinema"
        enviarEnCola(perso, packet, true)
        imprimir("CINEMATIC: PERSO", packet)
    }

    fun ENVIAR_TC_CARGAR_TUTORIAL(ss: Personaje, tutorial: Int) {
        val packet = "TC$tutorial|7001010000"
        enviarEnCola(ss, packet, true)
        imprimir("CARGAR TUTORIAL: PERSO", packet)
    }

    fun ENVIAR_TT_MOSTRAR_TIP(ss: Personaje, tutorial: Int) {
        val packet = "TT$tutorial"
        enviarEnCola(ss, packet, true)
        imprimir("MOSTRAR TIP: PERSO", packet)
    }

    fun ENVIAR_DCK_CREAR_DIALOGO(ss: Personaje, id: Int) {
        val packet = "DCK$id"
        enviarEnCola(ss, packet, true)
        imprimir("CREAR DIALOGO: PERSO", packet)
    }

    fun ENVIAR_DQ_DIALOGO_PREGUNTA(ss: Personaje, str: String) {
        val packet = "DQ$str"
        enviarEnCola(ss, packet, true)
        imprimir("DIALOGO PREGUNTA: PERSO", packet)
    }

    fun ENVIAR_DV_FINALIZAR_DIALOGO(perso: Personaje) {
        val packet = "DV"
        enviarEnCola(perso, packet, true)
        imprimir("CERRAR DIALOGO: PERSO", packet)
    }

    fun ENVIAR_BAT2_CONSOLA(ss: Personaje, str: String) {
        val packet = "BAT2$str"
        enviarEnCola(ss, packet, true)
        imprimir("CONSOLA COMANDOS: PERSO", packet)
    }

    fun ENVIAR_EBE_ERROR_DE_COMPRA(ss: Personaje) {
        val packet = "EBE"
        enviarEnCola(ss, packet, true)
        imprimir("ERROR COMPRA: PERSO", packet)
    }

    fun ENVIAR_ESE_ERROR_VENTA(perso: Personaje) {
        val packet = "ESE"
        enviarEnCola(perso, packet, true)
        imprimir("ERROR VENTA: PERSO", packet)
    }

    fun ENVIAR_EBK_COMPRADO(ss: Personaje) {
        val packet = "EBK"
        enviarEnCola(ss, packet, true)
        imprimir("COMPRADO: PERSO", packet)
    }

    fun ENVIAR_ESK_VENDIDO(perso: Personaje) {
        val packet = "ESK"
        enviarEnCola(perso, packet, true)
        imprimir("VENDIDO: PERSO", packet)
    }

    fun ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso: Personaje, obj: Objeto) {
        val packet = "OQ" + obj.id + "|" + obj.cantidad
        enviarEnCola(perso, packet, true)
        imprimir("CAMBIA CANT OBJETO: PERSO", packet)
    }

    fun ENVIAR_OR_ELIMINAR_OBJETO(perso: Personaje, id: Int) {
        val packet = "OR$id"
        enviarEnCola(perso, packet, true)
        imprimir("ELIMINAR OBJETO: PERSO", packet)
    }

    fun ENVIAR_ODE_ERROR_ELIMINAR_OBJETO(ss: Personaje) {
        val packet = "ODE"
        enviarEnCola(ss, packet, true)
        imprimir("ERROR ELIMINAR OBJETO: PERSO", packet)
    }

    fun ENVIAR_OM_MOVER_OBJETO(perso: Personaje, obj: Objeto) {
        var packet = "OM" + obj.id + "|"
        if (obj.posicion != Constantes.OBJETO_POS_NO_EQUIPADO) {
            packet += obj.posicion
        }
        enviarEnCola(perso, packet, true)
        imprimir("MOVER OBJETO: PERSO", packet)
    }

    fun ENVIAR_cS_EMOTE_EN_PELEA(pelea: Pelea, equipos: Int, id: Int, id2: Int) {
        val packet = "cS$id|$id2"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("EMOTE PELEA: PELEA", packet)
    }

    fun ENVIAR_OAEL_ERROR_AGREGAR_OBJETO(ss: Personaje) {
        val packet = "OAEL"
        enviarEnCola(ss, packet, true)
        imprimir("ERROR AGREGAR OBJETO: PERSO", packet)
    }

    fun ENVIAR_Ow_PODS_DEL_PJ(perso: Personaje?) {
        if (perso == null) {
            ENVIAR_BN_NADA(perso)
            return
        }
        val packet = "Ow" + perso.podsUsados + "|" + perso.podsMaximos
        enviarEnCola(perso, packet, true)
        imprimir("PODS: PERSO", packet)
    }

    fun ENVIAR_OAKO_APARECER_OBJETO(perso: Personaje, objeto: Objeto) {
        val packet = "OAKO" + objeto.stringObjetoConGuiño()
        enviarEnCola(perso, packet, true)
        imprimir("APARECER OBJETO: PERSO", packet)
        //		try {
        //			Thread.sleep(10);
        //		} catch (InterruptedException e) {}
    }

    fun ENVIAR_OAKO_APARECER_MUCHOS_OBJETOS(perso: Personaje, str: String) {
        val packet = "OAKO$str"
        enviarEnCola(perso, packet, true)
        imprimir("APARECER MUCHOS OBJETO: PERSO", packet)
        //		try {
        //			Thread.sleep(10);
        //		} catch (InterruptedException e) {}
    }

    fun ENVIAR_OCK_ACTUALIZA_OBJETO(perso: Personaje, objeto: Objeto?) {
        if (objeto == null)
            return
        val packet = "OCK" + objeto.stringObjetoConGuiño()
        enviarEnCola(perso, packet, true)
        imprimir("ACTUALIZA OBJETO: PERSO", packet)
        //		try {
        //			Thread.sleep(10);
        //		} catch (InterruptedException e) {}
    }

    fun ENVIAR_ERK_CONSULTA_INTERCAMBIO(ss: Personaje, id: Int, idT: Int, tipo: Int) {
        val packet = "ERK$id|$idT|$tipo"
        enviarEnCola(ss, packet, true)
        imprimir("CONSULTA INTERCAMBIO: PERSO", packet)
    }

    fun ENVIAR_ERE_ERROR_CONSULTA(ss: Personaje, c: Char) {
        val packet = "ERE$c"
        enviarEnCola(ss, packet, true)
        imprimir("CONSULTA ERROR: PERSO", packet)
    }

    fun ENVIAR_EMK_MOVER_OBJETO_LOCAL(
        ss: Personaje, tipoOG: Char, signo: String,
        s1: String
    ) {
        var packet = "EMK$tipoOG$signo"
        if (s1.isNotEmpty()) {
            packet += s1
        }
        enviarEnCola(ss, packet, true)
        imprimir("MOVER OBJ LOCAL: PERSO", packet)
    }

    fun ENVIAR_EmK_MOVER_OBJETO_DISTANTE(
        perso: Personaje, tipoOG: Char, signo: String,
        s1: String
    ) {
        var packet = "EmK$tipoOG$signo"
        if (s1.isNotEmpty()) {
            packet += s1
        }
        enviarEnCola(perso, packet, true)
        imprimir("MOVER OBJ DISTANTE: PERSO", packet)
    }

    fun ENVIAR_EmE_ERROR_MOVER_OBJETO_DISTANTE(
        ss: Personaje, tipoOG: Char, signo: String,
        s1: String
    ) {
        var packet = "EmE$tipoOG$signo"
        if (s1.isNotEmpty()) {
            packet += s1
        }
        enviarEnCola(ss, packet, true)
        imprimir("MOVER OBJ DISTANTE: PERSO", packet)
    }

    fun ENVIAR_EiK_MOVER_OBJETO_TIENDA(
        ss: Personaje, tipoOG: Char, signo: String,
        s1: String
    ) {
        var packet = "EiK$tipoOG$signo"
        if (s1.isNotEmpty()) {
            packet += s1
        }
        enviarEnCola(ss, packet, true)
        imprimir("MOVER OBJ TIENDA: PERSO", packet)
    }

    fun ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(
        ss: Personaje, tipo: Int, objKama: String,
        signo: String, s1: String
    ) {
        val packet = "Ep" + tipo + "K" + objKama + signo + s1
        enviarEnCola(ss, packet, true)
        imprimir("PAGO POR TRABAJO: PERSO", packet)
    }

    fun ENVIAR_ErK_RESULTADO_TRABAJO(
        perso: Personaje, objKama: String, signo: String,
        s1: String
    ) {
        val packet = "ErK$objKama$signo$s1"
        enviarEnCola(perso, packet, true)
        imprimir("RESULTADO TRABAJO: PERSO", packet)
    }

    fun ENVIAR_EK_CHECK_OK_INTERCAMBIO(ss: Personaje, ok: Boolean, id: Int) {
        val packet = "EK" + (if (ok) "1" else "0") + id
        enviarEnCola(ss, packet, true)
        imprimir("ACEPTAR INTER: PERSO", packet)
    }

    fun ENVIAR_EV_CERRAR_VENTANAS(perso: Personaje, exito: String) {
        val packet = "EV$exito"
        enviarEnCola(perso, packet, true)
        imprimir("CERRAR VENTANA: PERSO", packet)
    }

    fun ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(perso: Personaje, tipo: Int, str: String) {
        var packet = "ECK$tipo"
        if (str.isNotEmpty()) {
            packet += "|$str"
        }
        enviarEnCola(perso, packet, true)
        imprimir("PANEL INTERCAMBIOS: PERSO", packet)
    }

    fun ENVIAR_El_LISTA_OBJETOS_COFRE_PRECARGADO(perso: Personaje, cofre: Cofre) {
        val lista = cofre.getListaExchanger(perso)
        if (lista.isEmpty()) {
            return
        }
        val packet = "El$lista"
        enviarEnCola(perso, packet, true)
        imprimir("PRECARGA OBJ COFRE: PERSO", packet)
    }

    // public static void ENVIAR_EL_LISTA_DE_OBJETO_MERCADILLO_POR_CUENTA(final Personaje perso,
    // Mercadillo mercadillo) {
    // StringBuilder packet = new StringBuilder();
    // for (final ObjetoMercadillo objMerca : mercadillo.getObjetosMercadillos()) {
    // if (objMerca == null) {
    // continue;
    // }
    // if (objMerca.getIDCuenta() != perso.getIDCuenta()) {
    // continue;
    // }
    // if (packet.length() > 0) {
    // packet.append("|");
    // }
    // packet.append(objMerca.analizarParaEL());
    // }
    // packet = new StringBuilder("EL" + packet.toString());
    // enviarEnCola(perso, packet.toString(), true);
    // if (AtlantaMain.MOSTRAR_ENVIOS) {
    // imprimir("LISTA OBJ MERCADILLO: PERSO", packet.toString());
    // }
    // }
    // public static void ENVIAR_EL_LISTA_OBJETOS_COFRE(final Personaje perso, final Cofre cofre) {
    // String lista = cofre.listaObjCofre();
    // // if (lista.isEmpty()) {
    // // return;
    // // }
    // final String packet = "EL" + lista;
    // enviarEnCola(perso, packet, true);
    // if (AtlantaMain.MOSTRAR_ENVIOS) {
    // imprimir("LISTA OBJ COFRE: PERSO", packet);
    // }
    // }
    //
    //
    // public static void ENVIAR_EL_LISTA_OBJETOS_NPC(final Personaje ss, final String str) {
    // final String packet = "EL" + str;
    // enviarEnCola(ss, packet, true);
    // if (AtlantaMain.MOSTRAR_ENVIOS) {
    // imprimir("LISTA OBJ NPC: PERSO", packet);
    // }
    // }
    //
    // public static void ENVIAR_EL_LISTA_OBJETOS_RECAUDADOR(final Personaje ss, final String str) {
    // final String packet = "EL" + str;
    // enviarEnCola(ss, packet, true);
    // if (AtlantaMain.MOSTRAR_ENVIOS) {
    // imprimir("LISTA OBJ RECAUDADOR: PERSO", packet);
    // }
    // }
    //
    // public static void ENVIAR_EL_LISTA_OBJETOS_DRAGOPAVO(final Personaje ss, final String str) {
    // final String packet = "EL" + str;
    // enviarEnCola(ss, packet, true);
    // if (AtlantaMain.MOSTRAR_ENVIOS) {
    // imprimir("LISTA MOCHILA DP: PERSO", packet);
    // }
    // }
    //
    fun ENVIAR_EL_LISTA_EXCHANGER(perso: Personaje, exchanger: Exchanger) {
        val packet = "EL" + exchanger.getListaExchanger(perso)
        enviarEnCola(perso, packet, true)
        imprimir("LISTA TIENDA PJ: PERSO", packet)
    }

    fun ENVIAR_PIE_ERROR_INVITACION_GRUPO(ss: Personaje, s: String) {
        val packet = "PIE$s"
        enviarEnCola(ss, packet, true)
        imprimir("ERROR INVIT GRUPO: PERSO", packet)
    }

    fun ENVIAR_PIK_INVITAR_GRUPO(perso: Personaje, n1: String, n2: String) {
        val packet = "PIK$n1|$n2"
        enviarEnCola(perso, packet, true)
        imprimir("INVITAR AL GRUPO: PERSO", packet)
    }

    fun ENVIAR_PCK_CREAR_GRUPO(perso: Personaje, s: String) {
        val packet = "PCK$s"
        enviarEnCola(perso, packet, true)
        imprimir("CREAR GRUPO: PERSO", packet)
    }

    fun ENVIAR_PL_LIDER_GRUPO(perso: Personaje, id: Int) {
        val packet = "PL$id"
        enviarEnCola(perso, packet, true)
        imprimir("LIDER GRUPO: PERSO", packet)
    }

    fun ENVIAR_PR_RECHAZAR_INVITACION_GRUPO(perso: Personaje) {
        val packet = "PR"
        enviarEnCola(perso, packet, true)
        imprimir("RECHAZ INVIT GRUPO: PERSO", packet)
    }

    fun ENVIAR_PA_ACEPTAR_INVITACION_GRUPO(perso: Personaje) {
        val packet = "PA"
        enviarEnCola(perso, packet, true)
        imprimir("ACEPTAR INVIT GRUPO: PERSO", packet)
    }

    fun ENVIAR_PV_DEJAR_GRUPO(perso: Personaje, s: String) {
        val packet = "PV$s"
        enviarEnCola(perso, packet, true)
        imprimir("DEJAR GRUPO: PERSO", packet)
    }

    fun ENVIAR_PM_TODOS_MIEMBROS_GRUPO_A_GRUPO(grupo: Grupo) {
        val packet = StringBuilder()
        for (pj in grupo.miembros) {
            if (packet.isNotEmpty()) {
                packet.append("|")
            }
            packet.append(pj.stringInfoGrupo())
        }
        packet.insert(0, "PM+")
        for (pj in grupo.miembros) {
            enviarEnCola(pj, packet.toString(), true)
        }
        imprimir("MIEMBROS GRUPO: GRUPO", packet.toString())
    }

    fun ENVIAR_PM_TODOS_MIEMBROS_GRUPO_A_PERSO(perso: Personaje, grupo: Grupo) {
        val packet = StringBuilder()
        for (pj in grupo.miembros) {
            if (packet.isNotEmpty()) {
                packet.append("|")
            }
            packet.append(pj.stringInfoGrupo())
        }
        packet.insert(0, "PM+")
        enviarEnCola(perso, packet.toString(), true)
        imprimir("MIEMBROS GRUPO: PERSO", packet.toString())
    }

    fun ENVIAR_PM_AGREGAR_PJ_GRUPO_A_GRUPO(grupo: Grupo, s: String) {
        val packet = "PM+$s"
        for (pj in grupo.miembros) {
            enviarEnCola(pj, packet, true)
        }
        imprimir("AGREGAR PJ GRUPO: GRUPO", packet)
    }

    fun ENVIAR_PM_ACTUALIZAR_INFO_PJ_GRUPO(grupo: Grupo, s: String) {
        val packet = "PM~$s"
        for (pj in grupo.miembros) {
            enviarEnCola(pj, packet, true)
        }
        imprimir("ACTUALIZAR INFO GRUPO: GRUPO", packet)
    }

    fun ENVIAR_PM_EXPULSAR_PJ_GRUPO(grupo: Grupo, id: Int) {
        val packet = "PM-$id"
        for (pj in grupo.miembros) {
            enviarEnCola(pj, packet, true)
        }
        imprimir("EXPULSAR PJ GRUPO: GRUPO", packet)
    }

    fun ENVIAR_kIE_ERROR_INVITACION_KOLISEO(ss: Personaje, s: String) {
        val packet = "kIE$s"
        enviarEnCola(ss, packet, true)
        imprimir("ERROR INVIT GRUPO: PERSO", packet)
    }

    fun ENVIAR_kIK_INVITAR_KOLISEO(perso: Personaje, n1: String, n2: String) {
        val packet = "kIK$n1|$n2"
        enviarEnCola(perso, packet, true)
        imprimir("INVITAR AL KOLISEO: PERSO", packet)
    }

    fun ENVIAR_kR_RECHAZAR_INVITACION_KOLISEO(ss: Personaje) {
        val packet = "kR"
        enviarEnCola(ss, packet, true)
        imprimir("RECHAZ INVIT KOLISEO: PERSO", packet)
    }

    fun ENVIAR_kA_ACEPTAR_INVITACION_KOLISEO(ss: Personaje) {
        val packet = "kA"
        enviarEnCola(ss, packet, true)
        imprimir("ACEPTAR INVIT KOLISEO: PERSO", packet)
    }

    fun ENVIAR_kP_PANEL_KOLISEO(ss: Personaje) {
        val packet = "kP" + Mundo.cantKoliseo() + ";" + Mundo.SEGUNDOS_INICIO_KOLISEO
        enviarEnCola(ss, packet, true)
        imprimir("PANEL KOLISEO: PERSO", packet)
    }

    fun ENVIAR_kV_DEJAR_KOLISEO(perso: Personaje) {
        val packet = "kV"
        enviarEnCola(perso, packet, true)
        imprimir("DEJAR KOLISEO: PERSO", packet)
    }

    fun ENVIAR_kCK_CREAR_KOLISEO(perso: Personaje) {
        val packet = "kCK"
        enviarEnCola(perso, packet, true)
        imprimir("CREAR KOLISEO: PERSO", packet)
    }

    fun ENVIAR_kM_TODOS_MIEMBROS_KOLISEO(perso: Personaje, grupo: GrupoKoliseo) {
        val packet = StringBuilder("kM+")
        var primero = true
        for (pj in grupo.miembros) {
            if (!primero) {
                packet.append("|")
            }
            packet.append(pj.stringInfoGrupo())
            primero = false
        }
        enviarEnCola(perso, packet.toString(), true)
        imprimir("MIEMBROS GRUPO: PERSO", packet.toString())
    }

    fun ENVIAR_kM_AGREGAR_PJ_KOLISEO(grupo: GrupoKoliseo, s: String) {
        val packet = "kM+$s"
        for (pj in grupo.miembros) {
            enviarEnCola(pj, packet, true)
        }
        imprimir("AGREGAR PJ GRUPO: GRUPO", packet)
    }

    fun ENVIAR_kM_ACTUALIZAR_INFO_PJ_KOLISEO(grupo: GrupoKoliseo, s: String) {
        val packet = "kM~$s"
        for (pj in grupo.miembros) {
            enviarEnCola(pj, packet, true)
        }
        imprimir("ACTUALIZAR INFO GRUPO: GRUPO", packet)
    }

    fun ENVIAR_kM_EXPULSAR_PJ_KOLISEO(grupo: GrupoKoliseo, id: Int) {
        val packet = "kM-$id"
        for (pj in grupo.miembros) {
            enviarEnCola(pj, packet, true)
        }
        imprimir("EXPULSAR PJ GRUPO: GRUPO", packet)
    }

    fun ENVIAR_fD_DETALLES_PELEA(ss: Personaje, pelea: Pelea?) {
        if (pelea == null) {
            return
        }
        val packet = StringBuilder("fD" + pelea.id + "|")
        for (luchador in pelea.luchadoresDeEquipo(1)) {
            if (luchador.esInvocacion()) {
                continue
            }
            packet.append(luchador.nombre).append("~").append(luchador.nivel).append(";")
        }
        packet.append("|")
        for (luchador in pelea.luchadoresDeEquipo(2)) {
            if (luchador.esInvocacion()) {
                continue
            }
            packet.append(luchador.nombre).append("~").append(luchador.nivel).append(";")
        }
        enviarEnCola(ss, packet.toString(), true)
        imprimir("DETALLES PELEA: PERSO", packet.toString())
    }

    fun ENVIAR_IQ_NUMERO_ARRIBA_PJ(perso: Personaje, idPerso: Int, numero: Int) {
        val packet = "IQ$idPerso|$numero"
        enviarEnCola(perso, packet, true)
        imprimir("NUMERO ARRIBA PJ: PERSO", packet)
    }

    fun ENVIAR_JN_OFICIO_NIVEL(perso: Personaje, oficioID: Int, nivel: Int) {
        val packet = "JN$oficioID|$nivel"
        enviarEnCola(perso, packet, true)
        imprimir("OFICIO NIVEL: PERSO", packet)
    }

    // private static void ENVIAR_GDF_OBJETOS_INTERACTIVOS(final Personaje ss, final Mapa mapa) {
    // final String packet = mapa.getObjetosInteracGDF();
    // if (packet.isEmpty()) {
    // return;
    // }
    // enviar(ss, packet, true);
    // if (Bustemu.MOSTRAR_ENVIOS) {
    // imprimir("OBJ INTERACTIVOS: PERSO" , packet);
    // }
    // }
    fun ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(mapa: Mapa, celda: Celda) {
        var packet = "GDF|" + celda.id + ";"
        if (celda.objetoInteractivo == null) {
            packet += celda.estado
        } else {
            packet += celda.objetoInteractivo!!.infoPacket
        }
        for (pj in mapa.arrayPersonajes!!) {
            if (pj.pelea == null) {
                enviarEnCola(pj, packet, true)
            }
        }
        imprimir("ESTADO OBJ INTERACTIVO: MAPA", packet)
    }

    fun ENVIAR_GDF_FORZADO_MAPA(mapa: Mapa, str: String) {
        val packet = "GDF|$str"
        for (pj in mapa.arrayPersonajes!!) {
            if (pj.pelea == null) {
                enviarEnCola(pj, packet, true)
            }
        }
        imprimir("EST OBJ INTER FORZADO: MAPA", packet)
    }

    fun ENVIAR_GDF_FORZADO_PERSONAJE(perso: Personaje, str: String) {
        val packet = "GDF|$str"
        enviarEnCola(perso, packet, true)
        imprimir("EST OBJ INTER FORZADO: MAPA", packet)
    }

    fun ENVIAR_GA_ACCION_JUEGO_AL_MAPA(
        mapa: Mapa, idUnica: Int, idAccionModelo: Int,
        s1: String, s2: String
    ) {
        var packet = "GA" + (if (idUnica <= -1) "" else idUnica) + ";" + idAccionModelo + ";" + s1
        if (s2.isNotEmpty()) {
            packet += ";$s2"
        }
        for (pj in mapa.arrayPersonajes!!) {
            enviarEnCola(pj, packet, true)
        }
        imprimir("ACCION JUEGO: MAPA", packet)
    }

    fun ENVIAR_GA_MOVER_SPRITE_MAPA(
        mapa: Mapa, idUnica: Int, idAccionModelo: Int,
        s1: String, s2: String
    ) {
        var packet = "GA" + (if (idUnica <= -1) "" else idUnica) + ";" + idAccionModelo + ";" + s1
        if (s2.isNotEmpty()) {
            packet += ";$s2"
        }
        for (pj in mapa.arrayPersonajes!!) {
            enviarEnCola(pj, packet, true)
        }
    }

    // public static void ENVIAR_EL_LISTA_OBJETOS_BANCO(final Personaje perso) {
    // final String packet = "EL" + perso.listaObjBanco();
    // enviarEnCola(perso, packet, true);
    // if (AtlantaMain.MOSTRAR_ENVIOS) {
    // imprimir("LISTA OBJ BANCO: PERSO", packet);
    // }
    // }
    //
    // public static void ENVIAR_El_LISTA_OBJETOS_BANCO_PRECARGADO(final Personaje perso) {
    // final String packet = "El" + perso.listaObjBanco();
    // enviarEnCola(perso, packet, true);
    // }
    fun ENVIAR_JX_EXPERINENCIA_OFICIO(perso: Personaje, oficios: Collection<StatOficio>) {
        val packet = StringBuilder("JX")
        for (statOficio in oficios) {
            if (statOficio.posicion.toInt() != 7) {
                packet.append("|").append(statOficio.oficio.id).append(";").append(statOficio.nivel).append(";").append(
                    statOficio.getExpString(
                        ";"
                    )
                ).append(";")
            }
        }
        enviarEnCola(perso, packet.toString(), true)
        imprimir("EXPERIENCIA OFICIO: PERSO", packet.toString())
    }

    fun ENVIAR_JX_EXPERINENCIA_OFICIO(perso: Personaje, statOficio: StatOficio) {
        val packet = "JX" + "|" + statOficio.oficio.id + ";" + statOficio.nivel + ";" + statOficio
            .getExpString(";") + ";"
        enviarEnCola(perso, packet, true)
        imprimir("EXPERIENCIA OFICIO: PERSO", packet)
    }

    fun ENVIAR_JO_OFICIO_OPCIONES(perso: Personaje, oficios: Collection<StatOficio>) {
        for (statOficio in oficios) {
            if (statOficio.posicion.toInt() == 7) {
                continue
            }
            val packet = "JO" + statOficio.posicion + "|" + statOficio.opcionBin + "|" + statOficio
                .slotsPublico
            enviarEnCola(perso, packet, true)
            imprimir("OFICIO OPCIONES: PERSO", packet)
        }
    }

    fun ENVIAR_JO_OFICIO_OPCIONES(perso: Personaje, statOficio: StatOficio) {
        val packet = "JO" + statOficio.posicion + "|" + statOficio.opcionBin + "|" + statOficio
            .slotsPublico
        enviarEnCola(perso, packet, true)
        imprimir("OFICIO OPCIONES: PERSO", packet)
    }

    fun ENVIAR_EJ_DESCRIPCION_LIBRO_ARTESANO(perso: Personaje, str: String) {
        val packet = "EJ$str"
        enviarEnCola(perso, packet, true)
        imprimir("DESCRIP LIBRO ARTESANO: PERSO", packet)
    }

    fun ENVIAR_Ej_AGREGAR_LIBRO_ARTESANO(perso: Personaje, str: String) {
        val packet = "Ej$str"
        enviarEnCola(perso, packet, true)
        imprimir("AGREG LIBRO ARTESANO: PERSO", packet)
    }

    fun ENVIAR_JS_SKILLS_DE_OFICIO(perso: Personaje, oficios: Collection<StatOficio>) {
        val packet = StringBuilder("JS")
        for (statOficio in oficios) {
            if (statOficio.posicion.toInt() != 7) {
                packet.append(statOficio.stringSKillsOficio())
            }
        }
        enviarEnCola(perso, packet.toString(), true)
        imprimir("TRABAJO POR OFICIO: PERSO", packet.toString())
    }

    fun ENVIAR_JS_SKILL_DE_OFICIO(perso: Personaje, statsOficios: StatOficio) {
        val packet = "JS" + statsOficios.stringSKillsOficio()
        enviarEnCola(perso, packet, true)
        imprimir("TRABAJO POR OFICIO: PERSO", packet)
    }

    fun ENVIAR_JR_OLVIDAR_OFICIO(perso: Personaje, id: Int) {
        val packet = "JR$id"
        enviarEnCola(perso, packet, true)
        imprimir("OLVIDAR OFICIO: PERSO", packet)
    }

    fun ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso: Personaje, str: String) {
        val packet = "EsK$str"
        enviarEnCola(perso, packet, true)
        imprimir("MOVER OBJ: PERSO", packet)
    }

    fun ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(
        pelea: Pelea, equipos: Int, id: Int,
        celdaID: Short
    ) {
        val packet = "Gf$id|$celdaID"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("MOSTRAR CELDA: PELEA", packet)
    }

    fun ENVIAR_Gf_MOSTRAR_CELDA(perso: Personaje, id: Int, celdaID: Short) {
        val packet = "Gf$id|$celdaID"
        enviarEnCola(perso, packet, true)
        imprimir("MOSTRAR CELDA: PERSO", packet)
    }

    fun ENVIAR_Ea_MENSAJE_RECETAS(perso: Personaje, cant: Byte) {
        val packet = "Ea$cant"
        enviarEnCola(perso, packet, true)
        imprimir("TERMINOS RECETAS: PERSO", packet)
    }

    fun ENVIAR_EA_TURNO_RECETA(perso: Personaje, cant: Int) {
        val packet = "EA$cant"
        enviarEnCola(perso, packet, true)
        imprimir("TURNO RECETA: PERSO", packet)
    }

    fun ENVIAR_Ec_RESULTADO_RECETA(perso: Personaje, str: String) {
        val packet = "Ec$str"
        enviarEnCola(perso, packet, true)
        imprimir("INICIAR RECETA: PERSO", packet)
    }

    fun ENVIAR_IO_ICONO_OBJ_INTERACTIVO(mapa: Mapa, id: Int, str: String) {
        val packet = "IO$id|$str"
        for (pj in mapa.arrayPersonajes!!) {
            if (pj.pelea == null) {
                enviarEnCola(pj, packet, true)
            }
        }
        imprimir("ICONO OBJ INTERACTIVO: MAPA", packet)
    }

    fun ENVIAR_FL_LISTA_DE_AMIGOS(perso: Personaje) {
        var packet = "FL" + perso.cuenta.stringListaAmigos()
        enviarEnCola(perso, packet, true)
        imprimir("AMIGOS LINEA: PERSO", packet)
        if (perso.esposoID != 0) {
            packet = "FS" + perso.esposoListaAmigos
            enviarEnCola(perso, packet, true)
            imprimir("ESPOSO: PERSO", packet)
        }
    }

    fun ENVIAR_Im0143_AMIGO_CONECTADO(perso: Personaje, str: String) {
        val packet = "Im0143;$str"
        enviarEnCola(perso, packet, true)
        imprimir("MENSAJE AMIGO CONECTADO: PERSO", packet)
    }

    fun ENVIAR_FA_AGREGAR_AMIGO(perso: Personaje, str: String) {
        val packet = "FA$str"
        enviarEnCola(perso, packet, true)
        imprimir("AGREGAR AMIGO: PERSO", packet)
    }

    fun ENVIAR_FD_BORRAR_AMIGO(perso: Personaje, str: String) {
        val packet = "FD$str"
        enviarEnCola(perso, packet, true)
        imprimir("BORRAR AMIGO: PERSO", packet)
    }

    fun ENVIAR_iA_AGREGAR_ENEMIGO(perso: Personaje, str: String) {
        val packet = "iA$str"
        enviarEnCola(perso, packet, true)
        imprimir("AGREGAR ENEMIGO: PERSO", packet)
    }

    fun ENVIAR_iD_BORRAR_ENEMIGO(perso: Personaje, str: String) {
        val packet = "iD$str"
        enviarEnCola(perso, packet, true)
        imprimir("BORRAR ENEMIGO: PERSO", packet)
    }

    fun ENVIAR_iL_LISTA_ENEMIGOS(perso: Personaje) {
        val packet = "iL" + perso.cuenta.stringListaEnemigos()
        enviarEnCola(perso, packet, true)
        imprimir("LISTA ENEMIGOS: PERSO", packet)
    }

    fun ENVIAR_Rp_INFORMACION_CERCADO(perso: Personaje, cercado: Cercado?) {
        var packet = ""
        if (cercado == null) {
            return
        }
        packet = "Rp" + cercado.dueñoID + ";" + cercado.precio + ";" + cercado.capacidadMax + ";" + cercado
            .cantObjMax + ";"
        val gremio = cercado.gremio
        packet += if (gremio != null) {
            gremio.nombre + ";" + gremio.emblema
        } else {
            ";"
        }
        enviarEnCola(perso, packet, true)
        imprimir("INFO CERCADO: PERSO", packet)
    }

    fun ENVIAR_OS_BONUS_SET(perso: Personaje, setID: Int, numero: Int) {
        var packet = "OS"
        var num = 0
        num = if (numero != -1) {
            numero
        } else {
            perso.getNroObjEquipadosDeSet(setID)
        }
        val OS = Mundo.getObjetoSet(setID)
        if (num == 0 || OS == null) {
            packet += "-$setID"
        } else {
            packet += "+$setID|"
            val objetos = StringBuilder()
            for (OM in OS.objetosModelos) {
                if (perso.tieneObjModeloEquipado(OM.id)) {
                    if (objetos.isNotEmpty()) {
                        objetos.append(";")
                    }
                    objetos.append(OM.id)
                }
            }
            packet += objetos.toString() + "|" + OS.getBonusStatPorNroObj(num)!!.convertirStatsAString()
        }
        enviarEnCola(perso, packet, true)
        imprimir("BONUS SET: PERSO", packet)
    }

    fun ENVIAR_Re_DETALLES_MONTURA(perso: Personaje, simbolo: String, dragopavo: Montura?) {
        var packet = "Re$simbolo"
        if (simbolo == "+" && dragopavo != null) {
            packet += dragopavo.detallesMontura()
        }
        enviarEnCola(perso, packet, true)
        imprimir("DETALLE MONTURA: PERSO", packet)
    }

    fun ENVIAR_Rd_DESCRIPCION_MONTURA(perso: Personaje, dragopavo: Montura) {
        val packet = "Rd" + dragopavo.detallesMontura()
        enviarEnCola(perso, packet, true)
        imprimir("DESCRIPCION MONTURA: PERSO", packet)
    }

    fun ENVIAR_Rr_ESTADO_MONTADO(perso: Personaje, montado: String) {
        val packet = "Rr$montado"
        enviarEnCola(perso, packet, true)
        imprimir("ESTADO MONTADO: PERSO", packet)
    }

    fun ENVIAR_AC_CAMBIAR_CLASE(perso: Personaje, clase: Int) {
        val packet = "AC$clase"
        enviarEnCola(perso, packet, true)
        imprimir("CAMBIAR CLASE: PERSO", packet)
    }

    fun ENVIAR_AI_CAMBIAR_ID(perso: Personaje, id: Int) {
        val packet = "AI$id"
        enviarEnCola(perso, packet, true)
        imprimir("CAMBIAR ID: PERSO", packet)
    }

    fun ENVIAR_Rz_STATS_VIP(perso: Personaje, stats: String) {
        val packet = "Rz$stats"
        enviarEnCola(perso, packet, true)
        imprimir("STATS VIP: PERSO", packet)
    }

    fun ENVIAR_GM_BORRAR_GM_A_MAPA(mapa: Mapa, id: Int) {
        val packet = "GM|-$id"
        for (pj in mapa.arrayPersonajes!!) {
            enviarEnCola(pj, packet, true)
        }
        imprimir("BORRAR PJ: MAPA ID " + mapa.id + ": MAPA", packet)
    }

    fun ENVIAR_GM_BORRAR_LUCHADOR(pelea: Pelea, id: Int, equipos: Int) {
        val packet = "GM|-$id"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            if (luchador.personaje == null || luchador.id == id) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("BORRRA LUCH: PELEA ID " + pelea.id + ": PELEA", packet)
    }

    fun ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(mapa: Mapa, perso: Personaje) {
        val packet = "GM|~" + perso.stringGM()
        for (pj in mapa.arrayPersonajes!!) {
            if (pj.pelea == null) {
                enviarEnCola(pj, packet, true)
            }
        }
        imprimir("REFRESCAR PJ: MAPA", packet)
    }

    fun ENVIAR_GM_REFRESCAR_PJ_EN_MAPA_SIN_HUMO(mapa: Mapa, perso: Personaje) {
        val packet = "GM|-" + perso.Id + Constantes.x0char + "GM|+" + perso.stringGM()
        for (pj in mapa.arrayPersonajes!!) {
            if (pj.pelea == null) {
                enviarEnCola(pj, packet, true)
            }
        }
        imprimir("REFRESCAR PJ: MAPA", packet)
    }

    fun ENVIAR_GM_REFRESCAR_LUCHADOR_EN_PELEA(pelea: Pelea, luch: Luchador) {
        val packet = "GM|~" + luch.stringGM(0)
        for (luchador in pelea.luchadoresDeEquipo(3)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("REFRESCAR PJ: PELEA", packet)
    }

    fun ENVIAR_GM_LUCHADORES_A_PERSO(pelea: Pelea, mapa: Mapa, perso: Personaje) {
        val packet = mapa.getGMsLuchadores(perso.Id)
        if (packet.isEmpty()) {
            return
        }
        enviarEnCola(perso, packet, true)
        imprimir("GM LUCHADORES: PERSO", packet)
    }

    fun ENVIAR_GM_LUCHADORES_A_PELEA(pelea: Pelea, equipos: Int, mapa: Mapa) {
        val packet = mapa.getGMsLuchadores(0)
        if (packet.isEmpty()) {
            return
        }
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("GM LUCHADORES: PELEA", packet)
    }

    fun ENVIAR_GM_PERSONAJES_MAPA_A_PERSO(mapa: Mapa, perso: Personaje) {
        val packet = mapa.getGMsPersonajes(perso)
        if (packet.isEmpty()) {
            return
        }
        enviarEnCola(perso, packet, true)
        imprimir("GM PERSONAJE: MAPA", packet)
    }

    fun ENVIAR_GM_JUGADOR_UNIRSE_PELEA(pelea: Pelea, equipos: Int, luch: Luchador) {
        val packet = "GM|+" + luch.stringGM(0)
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador === luch || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("LUCH UNIR PELEA: PELEA", packet)
    }

    fun ENVIAR_GM_MERCANTE_A_MAPA(mapa: Mapa, str: String) {
        val packet = "GM|$str"
        for (pj in mapa.arrayPersonajes!!) {
            if (pj.pelea == null) {
                enviarEnCola(pj, packet, true)
            }
        }
        imprimir("GM MERCANTE: MAPA", packet)
    }

    fun ENVIAR_GM_PJ_A_MAPA(mapa: Mapa, perso: Personaje) {
        val packet = "GM|+" + perso.stringGM()
        val i = perso.esIndetectable()
        for (pj in mapa.arrayPersonajes!!) {
            if (pj.pelea != null) {
                continue
            }
            if (i && !pj.esIndetectable()) {
                continue
            }
            enviarEnCola(pj, packet, true)
        }
        imprimir("AGREGAR PJ: MAPA ID " + mapa.id + ": MAPA", packet)
    }

    fun ENVIAR_GM_GRUPOMOB_A_MAPA(mapa: Mapa, str: String) {
        val packet = "GM|$str"
        for (pj in mapa.arrayPersonajes!!) {
            if (pj.pelea == null) {
                enviarEnCola(pj, packet, true)
            }
        }
        imprimir("GM GRUPOMOB: MAPA", packet)
    }

    fun ENVIAR_GM_DRAGOPAVO_A_MAPA(mapa: Mapa, signo: String, montura: Montura) {
        val packet = "GM|" + signo + montura.stringGM()
        val esPublico = mapa.cercado!!.esPublico()
        for (pj in mapa.arrayPersonajes!!) {
            if (esPublico && montura.dueñoID != pj.Id) {
                continue
            }
            if (pj.pelea == null) {
                enviarEnCola(pj, packet, true)
            }
        }
        imprimir("GM DRAGOPAVO: MAPA", packet)
    }

    fun ENVIAR_GM_PRISMA_A_MAPA(mapa: Mapa, str: String) {
        val packet = "GM|$str"
        for (pj in mapa.arrayPersonajes!!) {
            if (pj.pelea == null) {
                enviarEnCola(pj, packet, true)
            }
        }
        imprimir("GM PRISMA: MAPA", packet)
    }

    fun ENVIAR_GM_NPC_A_MAPA(mapa: Mapa, signo: Char, str: String) {
        val packet = "GM|$signo$str"
        for (pj in mapa.arrayPersonajes!!) {
            if (pj.pelea == null) {
                enviarEnCola(pj, packet, true)
            }
        }
        imprimir("GM AGREGAR NPC: MAPA", packet)
    }

    fun ENVIAR_GM_RECAUDADOR_A_MAPA(mapa: Mapa, str: String) {
        val packet = "GM|$str"
        for (pj in mapa.arrayPersonajes!!) {
            if (pj.pelea == null) {
                enviarEnCola(pj, packet, true)
            }
        }
        imprimir("GM AGREGAR RECAUDADOR: MAPA", packet)
    }

    fun ENVIAR_As_STATS_DEL_PJ(perso: Personaje) {
        val packet = perso.stringStats()
        enviar(perso, packet)
        ENVIAR_Ab_CIRCULO_XP_BANNER(perso)
        imprimir("STATS COMPLETO PJ: PERSO", packet)
    }

    fun ENVIAR_As_STATS_DEL_PJ_LIDER(perso: Personaje, lider: Personaje) {
        val packet = perso.stringStats()
        enviar(lider, packet)
        ENVIAR_Ab_CIRCULO_XP_BANNER(lider)
        imprimir("STATS COMPLETO PJ: PERSO", packet)
    }

    fun ENVIAR_Ak_KAMAS_PDV_EXP_PJ(perso: Personaje) {
        val packet = perso.stringStats2()
        enviar(perso, packet)
        ENVIAR_Ab_CIRCULO_XP_BANNER(perso)
        imprimir("STATS KAMAS PDV EXP PJ: PERSO", packet)
    }

    fun ENVIAR_Ak_KAMAS_PDV_EXP_PJ_LIDER(perso: Personaje, lider: Personaje) {
        val packet = perso.stringStats2()
        enviar(lider, packet)
        ENVIAR_Ab_CIRCULO_XP_BANNER(lider)
        imprimir("STATS KAMAS PDV EXP PJ: PERSO", packet)
    }

    private fun ENVIAR_Ab_CIRCULO_XP_BANNER(perso: Personaje) {
        enviarEnCola(perso, "Ab", true)
    }

    fun ENVIAR_Rx_EXP_DONADA_MONTURA(perso: Personaje) {
        val packet = "Rx" + perso.porcXPMontura
        enviarEnCola(perso, packet, true)
        imprimir("XP DONADA MONTURA: PERSO", packet)
    }

    fun ENVIAR_Rn_CAMBIO_NOMBRE_MONTURA(perso: Personaje, nombre: String) {
        val packet = "Rn$nombre"
        enviarEnCola(perso, packet, true)
        imprimir("CAMBIO NOMBRE MONTURA: PERSO", packet)
    }

    fun ENVIAR_Ee_MONTURA_A_ESTABLO(perso: Personaje, c: Char, s: String) {
        val packet = "Ee$c$s"
        enviarEnCola(perso, packet, true)
        imprimir("PANEL MONTURA A ESTABLO: PERSO", packet)
    }

    fun ENVIAR_Ef_MONTURA_A_CRIAR(perso: Personaje, c: Char, s: String) {
        val packet = "Ef$c$s"
        enviarEnCola(perso, packet, true)
        imprimir("PANEL MONTURA A CRIAR: PERSO", packet)
    }

    fun ENVIAR_cC_SUSCRIBIR_CANAL(perso: Personaje, c: Char, s: String) {
        val packet = "cC$c$s"
        enviarEnCola(perso, packet, true)
        imprimir("SUSCRIBIR CANAL: PERSO", packet)
    }

    fun ENVIAR_GDO_OBJETO_TIRAR_SUELO(
        mapa: Mapa, agre_borr: Char, celda: Short,
        idObjetoMod: Int, gigante: Boolean, durabilidad: String
    ) {
        val packet = "GDO$agre_borr$celda" + if (agre_borr == '-')
            ""
        else
            ";" + idObjetoMod + ";" + if (gigante) 1.toString() + if (durabilidad.isEmpty()) "" else ";$durabilidad" else 0
        for (pj in mapa.arrayPersonajes!!) {
            if (pj.pelea == null) {
                enviarEnCola(pj, packet, true)
            }
        }
        imprimir("OBJ TIRADO SUELO: MAPA", packet)
    }

    fun ENVIAR_GDO_OBJETO_TIRAR_SUELO(
        perso: Personaje, agre_borr: Char, celda: Short,
        idObjetoMod: Int, gigante: Boolean, durabilidad: String
    ) {
        val packet = "GDO$agre_borr$celda" + if (agre_borr == '-')
            ""
        else
            ";" + idObjetoMod + ";" + if (gigante) 1.toString() + if (durabilidad.isEmpty()) "" else ";$durabilidad" else 0
        enviarEnCola(perso, packet, true)
        imprimir("OBJ TIRADO SUELO: PERSO", packet)
    }

    fun ENVIAR_ZC_CAMBIAR_ESPECIALIDAD_ALINEACION(perso: Personaje, especialidad: Int) {
        val packet = "ZC$especialidad"
        enviarEnCola(perso, packet, true)
        imprimir("CAMBIAR ESPEC. ALIN.: PERSO", packet)
    }

    fun ENVIAR_ZS_SET_ESPECIALIDAD_ALINEACION(ss: Personaje, especialidad: Int) {
        val packet = "ZS$especialidad"
        enviarEnCola(ss, packet, true)
        imprimir("SET ESPEC. ALIN.: PERSO", packet)
    }

    fun ENVIAR_GIP_ACT_DES_ALAS_PERDER_HONOR(perso: Personaje, a: Int) {
        val packet = "GIP$a"
        enviarEnCola(perso, packet, true)
        imprimir("ACT. ALAS HONOR: PERSO", packet)
    }

    fun ENVIAR_gn_CREAR_GREMIO(perso: Personaje) {
        val packet = "gn"
        enviarEnCola(perso, packet, true)
        imprimir("CREAR GREMIO: PERSO", packet)
    }

    fun ENVIAR_gC_CREAR_PANEL_GREMIO(perso: Personaje, s: String) {
        val packet = "gC$s"
        enviarEnCola(perso, packet, true)
        imprimir("CREAR PANEL GREMIO: PERSO", packet)
    }

    fun ENVIAR_gV_CERRAR_PANEL_GREMIO(perso: Personaje) {
        val packet = "gV"
        enviarEnCola(perso, packet, true)
        imprimir("CERRAR PANEL GREMIO: PERSO", packet)
    }

    fun ENVIAR_gIM_GREMIO_INFO_MIEMBROS(perso: Personaje, g: Gremio, c: Char) {
        var packet = "gIM$c"
        when (c) {
            '+', '-' -> try {
                packet += g.analizarMiembrosGM()
            } catch (ignored: NullPointerException) {
            }

        }
        enviarEnCola(perso, packet, true)
        imprimir("INFO MIEMBROS GREMIO: PERSO", packet)
    }

    fun ENVIAR_gIB_GREMIO_INFO_BOOST(perso: Personaje, infos: String) {
        val packet = "gIB$infos"
        enviarEnCola(perso, packet, true)
        imprimir("INFO BOOST GREMIO: PERSO", packet)
    }

    fun ENVIAR_gIH_GREMIO_INFO_CASAS(perso: Personaje, infos: String) {
        val packet = "gIH$infos"
        enviarEnCola(perso, packet, true)
        imprimir("INFO CASAS GREMIO: PERSO", packet)
    }

    fun ENVIAR_gS_STATS_GREMIO(perso: Personaje, miembro: MiembroGremio) {
        val gremio = miembro.gremio
        val packet = "gS" + gremio.nombre + "|" + gremio.emblema.replace(',', '|') + "|" + miembro
            .analizarDerechos()
        enviarEnCola(perso, packet, true)
        imprimir("GREMIO STATS: PERSO", packet)
    }

    fun ENVIAR_gJ_GREMIO_UNIR(perso: Personaje, str: String) {
        val packet = "gJ$str"
        enviarEnCola(perso, packet, true)
        imprimir("UNIR GREMIO: PERSO", packet)
    }

    fun ENVIAR_gK_GREMIO_BAN(perso: Personaje, str: String) {
        val packet = "gK$str"
        enviarEnCola(perso, packet, true)
        imprimir("BAN GREMIO: PERSO", packet)
    }

    fun ENVIAR_gIG_GREMIO_INFO_GENERAL(perso: Personaje, gremio: Gremio?) {
        if (gremio == null) {
            return
        }
        val packet = gremio.infoPanelGremio()
        enviarEnCola(perso, packet, true)
        imprimir("INFO GENERAL GREMIO: PERSO", packet)
    }

    fun ENVIAR_WC_MENU_ZAAP(perso: Personaje) {
        val packet = "WC" + perso.listaZaap()
        enviarEnCola(perso, packet, true)
        imprimir("MENU ZAAP: PERSO", packet)
    }

    fun ENVIAR_Wp_MENU_PRISMA(perso: Personaje) {
        val packet = "Wp" + perso.listaPrismas()
        enviarEnCola(perso, packet, true)
        imprimir("MENU PRISMA: PERSO", packet)
    }

    fun ENVIAR_WV_CERRAR_ZAAP(perso: Personaje) {
        val packet = "WV"
        enviarEnCola(perso, packet, true)
        imprimir("CERRAR ZAAP: PERSO", packet)
    }

    fun ENVIAR_Ww_CERRAR_PRISMA(perso: Personaje) {
        val packet = "Ww"
        enviarEnCola(perso, packet, true)
        imprimir("CERRAR PRISMA: PERSO", packet)
    }

    fun ENVIAR_Wv_CERRAR_ZAPPI(perso: Personaje) {
        val packet = "Wv"
        enviarEnCola(perso, packet, true)
        imprimir("CERRAR ZAAPIS: PERSO", packet)
    }

    fun ENVIAR_zV_CERRAR_ZONAS(perso: Personaje) {
        val packet = "zV"
        enviarEnCola(perso, packet, true)
        imprimir("CERRAR ZONAS: PERSO", packet)
    }

    fun ENVIAR_zC_LISTA_ZONAS(perso: Personaje) {
        val packet = "zC" + Mundo.LISTA_ZONAS
        enviarEnCola(perso, packet, true)
        imprimir("LISTA ZONAS: PERSO", packet)
    }

    fun ENVIAR_Wc_LISTA_ZAPPIS(perso: Personaje, lista: String) {
        val packet = "Wc$lista"
        enviarEnCola(perso, packet, true)
        imprimir("MENU ZAAPIS: PERSO", packet)
    }

    fun ENVIAR_WUE_ZAPPI_ERROR(perso: Personaje) {
        val packet = "WUE"
        enviarEnCola(perso, packet, true)
        imprimir("ERROR ZAPPI: ENVIAR", packet)
    }

    fun ENVIAR_eL_LISTA_EMOTES(perso: Personaje, s: Int) {
        val packet = "eL$s"
        enviarEnCola(perso, packet, true)
        imprimir("LISTA EMOTES: PERSO", packet)
    }

    fun ENVIAR_eA_AGREGAR_EMOTE(perso: Personaje, s: Int, mostrar: Boolean) {
        ENVIAR_eL_LISTA_EMOTES(perso, perso.emotes)
        val packet = "eA" + s + "|" + if (mostrar) 1 else 0
        enviarEnCola(perso, packet, true)
        imprimir("AGREGAR EMOTE: PERSO", packet)
    }

    fun ENVIAR_eR_BORRAR_EMOTE(perso: Personaje, s: Int, mostrar: Boolean) {
        ENVIAR_eL_LISTA_EMOTES(perso, perso.emotes)
        val packet = "eR" + s + "|" + if (mostrar) 1 else 0
        enviarEnCola(perso, packet, true)
        imprimir("BORRAR EMOTE: PERSO", packet)
    }

    fun ENVIAR_eUE_EMOTE_ERROR(perso: Personaje) {
        val packet = "eUE"
        enviarEnCola(perso, packet, true)
        imprimir("EMOTE ERROR: PERSO", packet)
    }

    fun ENVIAR_BWK_QUIEN_ES(perso: Personaje, str: String) {
        val packet = "BWK$str"
        enviarEnCola(perso, packet, true)
        imprimir("QUIEN ES: PERSO", packet)
    }

    fun ENVIAR_KCK_VENTANA_CLAVE(perso: Personaje, modificar: Boolean, cant: Byte) {
        val packet = "KCK" + (if (modificar) 1 else 0) + "|" + cant
        enviarEnCola(perso, packet, true)
        imprimir("VENTANA CLAVE: PERSO", packet)
    }

    fun ENVIAR_KKE_ERROR_CLAVE(perso: Personaje) {
        val packet = "KKE"
        enviarEnCola(perso, packet, true)
        imprimir("CLAVE ERROR: PERSO", packet)
    }

    fun ENVIAR_KV_CERRAR_VENTANA_CLAVE(perso: Personaje) {
        val packet = "KV"
        enviarEnCola(perso, packet, true)
        imprimir("CERRAR CLAVE: PERSO", packet)
    }

    fun ENVIAR_hL_INFO_CASA(perso: Personaje, str: String) {
        val packet = "hL$str"
        enviarEnCola(perso, packet, true)
        imprimir("CASA: PERSO", packet)
    }

    fun ENVIAR_hP_PROPIEDADES_CASA(perso: Personaje, str: String) {
        val packet = "hP$str"
        enviarEnCola(perso, packet, true)
        imprimir("CASA: PERSO", packet)
    }

    fun ENVIAR_hV_CERRAR_VENTANA_COMPRA_CASA(perso: Personaje) {
        val packet = "hV"
        enviarEnCola(perso, packet, true)
        imprimir("CASA: PERSO", packet)
    }

    fun ENVIAR_hCK_VENTANA_COMPRA_VENTA_CASA(perso: Personaje, str: String) {
        val packet = "hCK$str"
        enviarEnCola(perso, packet, true)
        imprimir("CASA: PERSO", packet)
    }

    fun ENVIAR_hSK_FIJAR_PRECIO_CASA(perso: Personaje, str: String) {
        val packet = "hSK$str"
        enviarEnCola(perso, packet, true)
        imprimir("CASA: PERSO", packet)
    }

    fun ENVIAR_hG_DERECHOS_GREMIO_CASA(perso: Personaje, str: String) {
        val packet = "hG$str"
        enviarEnCola(perso, packet, true)
        imprimir("CASA: PERSO", packet)
    }

    fun ENVIAR_hX_CERROJO_CASA(perso: Personaje, casaID: Int, activar: Boolean) {
        val packet = "hX" + casaID + "|" + if (activar) 1 else 0
        enviarEnCola(perso, packet, true)
        imprimir("CASA: PERSO", packet)
    }

    fun ENVIAR_SF_OLVIDAR_HECHIZO(signo: Char, perso: Personaje) {
        val packet = "SF$signo"
        enviarEnCola(perso, packet, true)
        imprimir("OLVIDAR HECHIZO: PERSO", packet)
    }

    fun ENVIAR_Rv_MONTURA_CERRAR(ss: Personaje) {
        val packet = "Rv"
        enviarEnCola(ss, packet, true)
        imprimir("MONTURA CERRAR: PERSO", packet)
    }

    fun ENVIAR_RD_COMPRAR_CERCADO(perso: Personaje, str: String) {
        val packet = "RD$str"
        enviarEnCola(perso, packet, true)
        imprimir("COMPRAR CERCADO: PERSO", packet)
    }

    fun ENVIAR_gIF_GREMIO_INFO_CERCADOS(perso: Personaje, str: String) {
        val packet = "gIF$str"
        enviarEnCola(perso, packet, true)
        imprimir("INFO CERCADOS: PERSO", packet)
    }

    fun ENVIAR_gITM_GREMIO_INFO_RECAUDADOR(perso: Personaje, str: String) {
        if (str.isEmpty()) {
            return
        }
        val packet = "gITM$str"
        enviarEnCola(perso, packet, true)
        imprimir("INFO RECAUDOR: PERSO", packet)
    }

    fun ENVIAR_gITp_INFO_ATACANTES_RECAUDADOR(perso: Personaje, str: String) {
        val packet = "gITp$str"
        enviarEnCola(perso, packet, true)
        imprimir("INFO ATACANTES RECAU: PERSO", packet)
    }

    fun ENVIAR_gITP_INFO_DEFENSORES_RECAUDADOR(perso: Personaje, str: String) {
        val packet = "gITP$str"
        enviarEnCola(perso, packet, true)
        imprimir("INFO DEFENSORES RECAU: PERSO", packet)
    }

    fun ENVIAR_CP_INFO_DEFENSORES_PRISMA(perso: Personaje, str: String) {
        val packet = "CP$str"
        enviarEnCola(perso, packet, true)
        imprimir("INFO DEFENSORES PRISMA: PERSO", packet)
    }

    fun ENVIAR_Cp_INFO_ATACANTES_PRISMA(perso: Personaje, str: String) {
        val packet = "Cp$str"
        enviarEnCola(perso, packet, true)
        imprimir("INFO ATACANTES PRISMA: PERSO", packet)
    }

    fun ENVIAR_gT_PANEL_RECAUDADORES_GREMIO(perso: Personaje, c: Char, str: String) {
        val packet = "gT$c$str"
        enviarEnCola(perso, packet, true)
        imprimir("PANEL RECAU GREMIO: PERSO", packet)
    }

    fun ENVIAR_gUT_PANEL_CASA_GREMIO(perso: Personaje) {
        val packet = "gUT"
        enviarEnCola(perso, packet, true)
        imprimir("PANEL CASA GREMIO: PERSO", packet)
    }

    fun ENVIAR_gUF_PANEL_CERCADOS_GREMIO(perso: Personaje) {
        val packet = "gUF"
        enviarEnCola(perso, packet, true)
        imprimir("PANEL CERCADO GREMIO: PERSO", packet)
    }

    fun ENVIAR_EHm_DETALLE_LINEA_CON_PRECIOS(perso: Personaje?, signo: String, str: String) {
        val packet = "EHm$signo$str"
        enviarEnCola(perso, packet, true)
        imprimir("MOVER OBJMERCA X PRECIO: PERSO", packet)
    }

    fun ENVIAR_EHM_MOVER_OBJMERCA_POR_MODELO(perso: Personaje, signo: String, str: String) {
        val packet = "EHM$signo$str"
        enviarEnCola(perso, packet, true)
        imprimir("MOVER OBJMERCA X MODELO: PERSO", packet)
    }

    fun ENVIAR_EHP_PRECIO_PROMEDIO_OBJ(perso: Personaje, modeloID: Int, precio: Long) {
        val packet = "EHP$modeloID|$precio"
        enviarEnCola(perso, packet, true)
        imprimir("PRECIO PROMEDIO OBJ: PERSO", packet)
    }

    fun ENVIAR_EHl_LISTA_LINEAS_OBJMERCA_POR_MODELO(perso: Personaje, str: String) {
        val packet = "EHl$str"
        enviarEnCola(perso, packet, true)
        imprimir("LISTA OBJ MERCA MODELO: PERSO", packet)
    }

    fun ENVIAR_EHL_LISTA_OBJMERCA_POR_TIPO(perso: Personaje, categ: Int, modelos: String) {
        val packet = "EHL$categ|$modelos"
        enviarEnCola(perso, packet, true)
        imprimir("LISTA OBJ CATEG MERCADILLO: PERSO", packet)
    }

    fun GAME_SEND_EHL_PACKET(perso: Personaje, str: String) {
        val packet = "EHL$str"
        enviarEnCola(perso, packet, true)
        imprimir("LISTA OBJ CATEG MERCADILLO: PERSO", packet)
    }

    fun ENVIAR_GA_ACCIONES_MATRIMONIO(
        mapa: Mapa, accionID: Int, propone: Int, propuesto: Int,
        sacerdote: Int
    ) {
        val packet = "GA;$accionID;$propone;$propuesto,$propone,$sacerdote"
        for (pj in mapa.arrayPersonajes!!) {
            enviarEnCola(pj, packet, true)
        }
        imprimir("ACCIONES MATRIMONIO: PERSO", packet)
    }

    fun ENVIAR_Gd_RETO_A_LOS_LUCHADORES(pelea: Pelea, reto: String) {
        val packet = "Gd$reto"
        for (luchador in pelea.luchadoresDeEquipo(1)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("MOSTRAR RETOS: PELEA", packet)
    }

    fun ENVIAR_Gd_RETO_A_PERSONAJE(perso: Personaje, reto: String) {
        val packet = "Gd$reto"
        enviarEnCola(perso, packet, true)
        imprimir("MOSTRAR RETOS: PERSO", packet)
    }

    fun ENVIAR_GdaK_RETO_REALIZADO(perso: Personaje, reto: Int) {
        val packet = "GdaK$reto"
        enviarEnCola(perso, packet, true)
        imprimir("RETO GANADO: PERSO", packet)
    }

    fun ENVIAR_GdaO_RETO_FALLADO(perso: Personaje, reto: Int) {
        val packet = "GdaO$reto"
        enviarEnCola(perso, packet, true)
        imprimir("RETO PERDIDO: PERSO", packet)
    }

    fun ENVIAR_GdaK_RETO_REALIZADO(pelea: Pelea, reto: Int) {
        val packet = "GdaK$reto"
        for (luchador in pelea.luchadoresDeEquipo(5)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("RETO GANADO: PELEA", packet)
    }

    fun ENVIAR_GdaO_RETO_FALLADO(pelea: Pelea, reto: Int) {
        val packet = "GdaO$reto"
        for (luchador in pelea.luchadoresDeEquipo(5)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("RETO PERDIDO: PELEA", packet)
    }

    fun ENVIAR_Eq_PREGUNTAR_MERCANTE(
        perso: Personaje, todoItems: Int, tasa: Int,
        precioPagar: Long
    ) {
        val packet = "Eq$todoItems|$tasa|$precioPagar"
        enviarEnCola(perso, packet, true)
        imprimir("PREG. MERCANTE: PERSO", packet)
    }

    fun ENVIAR_SB_HECHIZO_BOOST_SET_CLASE(perso: Personaje, modificacion: String) {
        val packet = "SB$modificacion"
        enviarEnCola(perso, packet, true)
        imprimir("HECHIZO BOOST: PERSO", packet)
    }

    fun ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(
        perso: Personaje, id: Int,
        msj: String, nombre: String
    ) {
        val packet = "M1$id|$msj|$nombre"
        enviarEnCola(perso, packet, true)
        imprimir("MSJ SERVER: PERSO", packet)
    }

    // public static void ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(final Personaje perso,
    // final String id,
    // final String msj, final String nombre) {
    // String packet = "M0" + id + "|" + msj + "|" + nombre;
    // enviarEnCola(perso, packet, true);
    // // if (Bustemu.MOSTRAR_ENVIOS) {
    // // imprimir("MSJ SERVER: PERSO" , packet);
    // // }
    // }
    fun ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(
        ss: ServidorSocket?, id: String,
        msj: String, nombre: String
    ) {
        val packet = "M0$id|$msj|$nombre"
        ss?.enviarPW(packet)
        imprimir("MSJ SERVER: PERSO", packet)
    }

    fun ENVIAR_IH_COORDENADAS_UBICACION(perso: Personaje, str: String) {
        val packet = "IH$str"
        enviarEnCola(perso, packet, true)
        imprimir("COORD UBIC: PERSO", packet)
    }

    fun ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(perso: Personaje, str: String) {
        val packet = "IC$str"
        enviarEnCola(perso, packet, true)
        imprimir("PJ BAND COMPAS: PERSO", packet)
    }

    fun ENVIAR_IC_BORRAR_BANDERA_COMPAS(perso: Personaje) {
        val packet = "IC|"
        enviarEnCola(perso, packet, true)
        imprimir("BORRAR BAND COMPAS: PERSO", packet)
    }

    fun ENVIAR_gA_MENSAJE_SOBRE_RECAUDADOR(perso: Personaje, str: String) {
        val packet = "gA$str"
        enviarEnCola(perso, packet, true)
        imprimir("MSJ SOBRE RECAU: PERSO", packet)
    }

    fun ENVIAR_CA_MENSAJE_ATAQUE_PRISMA(perso: Personaje, str: String) {
        val packet = "CA$str"
        enviarEnCola(perso, packet, true)
        imprimir("MSJ ATAQ PRISMA: PERSO", packet)
    }

    fun ENVIAR_CS_MENSAJE_SOBREVIVIO_PRISMA(perso: Personaje, str: String) {
        val packet = "CS$str"
        enviarEnCola(perso, packet, true)
        // if (Bustemu.MOSTRAR_ENVIOS) {
        // imprimir("MSJ SOBREVIVIO PRISMA: PERSO>> " + "CS" + str);
        // }
    }

    fun ENVIAR_CD_MENSAJE_MURIO_PRISMA(perso: Personaje, str: String) {
        val packet = "CD$str"
        enviarEnCola(perso, packet, true)
        // if (Bustemu.MOSTRAR_ENVIOS) {
        // imprimir("MSJ MURIO PRISMA: PERSO>> " + "CD" + str);
        // }
    }

    fun ENVIAR_PF_SEGUIR_PERSONAJE(perso: Personaje, str: String) {
        val packet = "PF$str"
        enviarEnCola(perso, packet, true)
        imprimir("SEGUIR PERSO: PERSO", packet)
    }

    fun ENVIAR_OT_OBJETO_HERRAMIENTA(ss: Personaje, id: Int) {
        val packet = "OT" + if (id > 0) id else ""
        enviarEnCola(ss, packet, true)
        imprimir("OBJ HERRAMIENTA: PERSO", packet)
    }

    fun ENVIAR_EW_OFICIO_MODO_PUBLICO(ss: Personaje, signo: String) {
        val packet = "EW$signo"
        enviarEnCola(ss, packet, true)
        imprimir("MODO PUBLICO: PERSO", packet)
    }

    fun ENVIAR_EW_OFICIO_MODO_INVITACION(
        perso: Personaje, signo: String, idPerso: Int,
        idOficios: String
    ) {
        val packet = "EW$signo$idPerso|$idOficios"
        enviarEnCola(perso, packet, true)
        imprimir("INVITAR TALLER: PERSO", packet)
    }

    fun ENVIAR_Cb_BALANCE_CONQUISTA(perso: Personaje, str: String) {
        val packet = "Cb$str"
        enviarEnCola(perso, packet, true)
        imprimir("BALANCE CONQUISTA: PERSO", packet)
    }

    fun ENVIAR_CB_BONUS_CONQUISTA(perso: Personaje, str: String) {
        val packet = "CB$str"
        enviarEnCola(perso, packet, true)
        imprimir("BONUS CONQUISTA: PERSO", packet)
    }

    fun ENVIAR_CW_INFO_MUNDO_CONQUISTA(perso: Personaje, str: String) {
        val packet = "CW$str"
        enviarEnCola(perso, packet, true)
        imprimir("MUNDO CONQUISTA: PERSO", packet)
    }

    fun ENVIAR_CIJ_INFO_UNIRSE_PRISMA(perso: Personaje, str: String) {
        val packet = "CIJ$str"
        enviarEnCola(perso, packet, true)
        imprimir("UNIRSE PRISMA: PERSO", packet)
    }

    fun ENVIAR_CIV_CERRAR_INFO_CONQUISTA(perso: Personaje) {
        val packet = "CIV"
        enviarEnCola(perso, packet, true)
        imprimir("CERRAR INFO CONQUISTA: PERSO", packet)
    }

    fun ENVIAR_M145_MENSAJE_PANEL_INFORMACION(perso: Personaje, str: String) {
        val packet = "M145|" + str.replace("\\n", "\n").replace("\\t", "\t").replace("\\r", "\r")
        enviarEnCola(perso, packet, true)
    }

    fun ENVIAR_M145_MENSAJE_PANEL_INFORMACION(ss: ServidorSocket, str: String) {
        val packet = "M145|" + str.replace("\\n", "\n").replace("\\t", "\t").replace("\\r", "\r")
        ss.enviarPW(packet)
    }

    fun ENVIAR_M145_MENSAJE_PANEL_INFORMACION_TODOS(str: String) {
        val packet = "M145|$str"
        for (perso in Mundo.PERSONAJESONLINE) {
            enviarEnCola(perso, packet, true)
        }
        imprimir("PANEL INFORMACION: PERSO", packet)
    }

    fun ENVIAR_BAIO_HABILITAR_ADMIN(perso: Personaje, str: String) {
        val packet = "BAIO$str"
        enviarEnCola(perso, packet, true)
        imprimir("HABILITAR ADMIN: PERSO", packet)
    }

    fun ENVIAR_Ew_PODS_MONTURA(perso: Personaje) {
        val packet = "Ew" + perso.montura.pods + ";" + perso.montura.totalPods
        enviarEnCola(perso, packet, true)
        imprimir("PODS MONTURA: PERSO", packet)
    }

    fun ENVIAR_QL_LISTA_MISIONES(perso: Personaje, str: String) {
        val packet = "QL$str"
        enviarEnCola(perso, packet, true)
        imprimir("LISTA MISIONES: PERSO", packet)
    }

    fun ENVIAR_QS_PASOS_RECOMPENSA_MISION(perso: Personaje, str: String) {
        val packet = "QS$str"
        enviarEnCola(perso, packet, true)
        imprimir("RECOMPENSA MISION: PERSO", packet)
    }

    fun ENVIAR_GO_GAME_OVER(perso: Personaje) {
        val packet = "GO"
        enviarEnCola(perso, packet, true)
        imprimir("GAME OVER: PERSO", packet)
    }

    fun ENVIAR_bRI_INICIAR_CUENTA_REGRESIVA(perso: Personaje) {
        if (Mundo.SEG_CUENTA_REGRESIVA == 0L)
            return
        val packet = "bRI" + Mundo.MSJ_CUENTA_REGRESIVA + ";" + Mundo.SEG_CUENTA_REGRESIVA
        enviarEnCola(perso, packet, true)
    }

    fun ENVIAR_GX_EXTRA_CLIP(perso: Personaje, str: String) {
        val packet = "GX$str"
        enviarEnCola(perso, packet, true)
        imprimir("EXTRA CLIP: PERSO", packet)
    }

    fun ENVIAR_GX_EXTRA_CLIP_PELEA(pelea: Pelea, equipos: Int, str: String) {
        if (!AtlantaMain.PARAM_MOSTRAR_NRO_TURNOS) {
            return
        }
        val packet = "GX$str"
        for (luchador in pelea.luchadoresDeEquipo(equipos)) {
            if (luchador.estaRetirado() || luchador.esMultiman()) {
                continue
            }
            enviarEnCola(luchador.personaje, packet, true)
        }
        imprimir("EXTRA CLIP: PELEA", packet)
    }

    fun ENVIAR_bOC_ABRIR_PANEL_SERVICIOS(ss: Personaje, creditos: Int, ogrinas: Int) {
        val packet = "bOC" + creditos + "^" + ogrinas + "^" + Mundo.stringServicios(ss)
        enviarEnCola(ss, packet, true)
        imprimir("ABRIR PANEL OGRINAS: PERSO", packet)
    }

    fun ENVIAR_bB_PANEL_CREAR_ITEM(ss: Personaje) {
        val packet = "bB"
        enviarEnCola(ss, packet, true)
        imprimir("PANEL CREAR ITEM: PERSO", packet)
    }

    fun ENVIAR_bb_DATA_CREAR_ITEM(ss: Personaje) {
        val packet = "bb" + Mundo.CREA_TU_ITEM_DATA
        enviarEnCola(ss, packet, true)
        imprimir("PANEL CREAR ITEM: PERSO", packet)
    }

    fun ENVIAR_bSP_PANEL_ITEMS(ss: Personaje) {
        val packet = "bSP" + (if (AtlantaMain.SISTEMA_ITEMS_TIPO_DE_PAGO == "KAMAS") "K" else "O") + Mundo
            .tiposPanelItems
        enviarEnCola(ss, packet, true)
        imprimir("PANEL SISTEMA ITEMS: PERSO", packet)
    }

    fun ENVIAR_bSO_PANEL_ITEMS_OBJETOS_POR_TIPO(ss: Personaje, str: String) {
        val packet = "bSO$str"
        enviarEnCola(ss, packet, true)
    }

    fun ENVIAR_bP_VOTO_RPG_PARADIZE(perso: Personaje, tiempo: Int, url: Boolean) {
        val packet = "bP" + tiempo + "," + if (url) "1" else "0"
        enviarEnCola(perso, packet, true)
        imprimir("VOTO RGP PARADIZE: PERSO", packet)
    }

    fun ENVIAR_bC_CAMBIAR_COLOR(ss: Personaje) {
        val packet = "bC"
        enviarEnCola(ss, packet, true)
        imprimir("CAMBIAR COLOR: PERSO", packet)
    }

    fun ENVIAR_bRS_PARAR_CUENTA_REGRESIVA(perso: Personaje) {
        val packet = "bRS"
        enviarEnCola(perso, packet, true)
        imprimir("PARAR CUENTA REGRESIVA: PERSO", packet)
    }

    fun ENVIAR_bOA_ACTUALIZAR_PANEL_OGRINAS(ss: Personaje, puntos: Long) {
        val packet = "bOA$puntos"
        enviarEnCola(ss, packet, true)
        imprimir("ACTUALIZAR PANEL OGRINAS: PERSO", packet)
    }

    fun ENVIAR_bI_SISTEMA_RECURSO(ss: Personaje, data: String) {
        val packet = "bI$data"
        enviarEnCola(ss, packet, true)
        imprimir("SISTEMA RECURSO: PERSO", packet)
    }

    fun ENVIAR_bT_PANEL_LOTERIA(ss: Personaje, data: String) {
        val packet = "bT$data"
        enviarEnCola(ss, packet, true)
        imprimir("PANEL LOTERIA: PERSO", packet)
    }

    fun ENVIAR_bL_RANKING_PERMITIDOS(ss: Personaje) {
        val packet = "bL" + Mundo.rankingsPermitidos()
        enviarEnCola(ss, packet, true)
        imprimir("RANKING PERMITIDOS: PERSO", packet)
    }

    fun ENVIAR_bl_RANKING_DATA(ss: Personaje, param: String, data: String) {
        val packet = "bl$param|$data"
        enviarEnCola(ss, packet, true)
        imprimir("RANKING DATA: PERSO", packet)
    }

    fun ENVIAR_bA_ESCOGER_NIVEL(perso: Personaje) {
        val packet = "bA" + perso.nivel
        enviarEnCola(perso, packet, true)
        imprimir("ESCOGER NIVEL: PERSO", packet)
    }

    fun ENVIAR_bm_TRANSFORMAR_MONTURA(perso: Personaje) {
        val packet = "bm" + Mundo.LISTA_MASCOTAS
        enviarEnCola(perso, packet, true)
        imprimir("TRANSFORMAR MONTURA: PERSO", packet)
    }

    fun ENVIAR_bRS_PARAR_CUENTA_REGRESIVA_TODOS() {
        val packet = "bRS"
        for (perso in Mundo.PERSONAJESONLINE) {
            enviarEnCola(perso, packet, true)
        }
        imprimir("PARAR CUENTA REGRESIVA: PERSO", packet)
    }

    fun ENVIAR_bRI_INICIAR_CUENTA_REGRESIVA_TODOS() {
        if (Mundo.SEG_CUENTA_REGRESIVA == 0L)
            return
        val packet = "bRI" + Mundo.MSJ_CUENTA_REGRESIVA + ";" + Mundo.SEG_CUENTA_REGRESIVA
        for (perso in Mundo.PERSONAJESONLINE) {
            enviarEnCola(perso, packet, true)
        }
        imprimir("INICIAR CUENTA REGRESIVA: TODOS", packet)
    }

    fun ENVIAR_brP_RULETA_PREMIOS(ss: Personaje, str: String) {
        val packet = "brP$str"
        enviar(ss, packet)
        imprimir("RULETA PREMIOS: PERSO", packet)
    }

    fun ENVIAR_brG_RULETA_GANADOR(ss: ServidorSocket, index: Int) {
        val packet = "brG$index"
        ss.enviarPW(packet)
        imprimir("RULETA GANADOR: PERSO", packet)
    }

    fun ENVIAR_bV_CERRAR_PANEL(_perso: Personaje) {
        val packet = "bV"
        enviarEnCola(_perso, packet, false)
    }

    fun ENVIAR_bn_CAMBIAR_NOMBRE_CONFIRMADO(_perso: Personaje, nombre: String) {
        val packet = "bn$nombre"
        enviarEnCola(_perso, packet, false)
    }

    fun ENVIAR_bñ_PANEL_ORNAMENTOS(perso: Personaje) {
        val packet = "bñ" + Mundo.listarOrnamentos(perso)
        enviarEnCola(perso, packet, true)
        imprimir("PANEL ORNAMENTOS: PERSO", packet)
    }

    fun ENVIAR_bt_PANEL_TITULOS(perso: Personaje) {
        val packet = "bt" + Mundo.listarTitulos(perso)
        enviarEnCola(perso, packet, true)
        imprimir("PANEL TITULOS: PERSO", packet)
    }

    fun ENVIAR_Ñs_BOTON_BOUTIQUE(ss: ServidorSocket) {
        val packet = "Ñs"
        ss.enviarPW(packet)
    }

    fun ENVIAR_ÑL_BOTON_LOTERIA(ss: ServidorSocket, bMostrar: Boolean) {
        val packet = "ÑL" + if (bMostrar) "1" else "0"
        ss.enviarPW(packet)
    }

    fun ENVIAR_ÑU_URL_IMAGEN_VOTO(ss: ServidorSocket) {
        val packet = "ÑU" + AtlantaMain.URL_IMAGEN_VOTO
        ss.enviarPW(packet)
    }

    fun ENVIAR_Ñu_URL_LINK_VOTO(ss: ServidorSocket) {
        val packet = "Ñu" + AtlantaMain.URL_LINK_VOTO
        ss.enviarPW(packet)
    }

    fun ENVIAR_Ñx_URL_LINK_BUG(ss: ServidorSocket) {
        val packet = "Ñx" + AtlantaMain.URL_LINK_BUG
        ss.enviarPW(packet)
    }

    fun ENVIAR_Ñz_URL_LINK_COMPRA(ss: ServidorSocket) {
        val packet = "Ñz" + AtlantaMain.URL_LINK_COMPRA
        ss.enviarPW(packet)
    }

    fun ENVIAR_Ñe_EXO_PANEL_ITEMS(ss: ServidorSocket) {
        val packet = ("Ñe" + AtlantaMain.SISTEMA_ITEMS_PERFECTO_MULTIPLICA_POR + ","
                + AtlantaMain.SISTEMA_ITEMS_EXO_PA_PRECIO + "," + AtlantaMain.SISTEMA_ITEMS_EXO_PM_PRECIO)
        ss.enviarPW(packet)
    }

    fun ENVIAR_Ña_AUTO_PASAR_TURNO(ss: ServidorSocket) {
        val packet = "Ña" + if (AtlantaMain.PARAM_AUTO_SALTAR_TURNO) "1" else "0"
        ss.enviarPW(packet)
    }

    fun ENVIAR_Ñr_SUFJIO_RESET(ss: ServidorSocket) {
        val packet = "Ñr" + AtlantaMain.SUFIJO_RESET
        ss.enviarPW(packet)
    }

    fun ENVIAR_ÑL_BOTON_LOTERIA_TODOS(bMostrar: Boolean) {
        val packet = "ÑL" + if (bMostrar) "1" else "0"
        for (perso in Mundo.PERSONAJESONLINE) {
            enviarEnCola(perso, packet, false)
        }
    }

    fun ENVIAR_bD_LISTA_REPORTES(_perso: Personaje, str: String) {
        val packet = "bD$str"
        enviarEnCola(_perso, packet, true)
    }

    fun ENVIAR_ÑA_LISTA_GFX(ss: ServidorSocket) {
        val packet = "ÑA" + Mundo.LISTA_GFX
        ss.enviarPW(packet)
    }

    fun ENVIAR_Bv_SONAR_MP3(_perso: Personaje, str: String) {
        val packet = "Bv$str"
        enviarEnCola(_perso, packet, true)
    }

    fun ENVIAR_ÑV_ACTUALIZAR_URL_LINK_MP3(ss: ServidorSocket) {
        val packet = "ÑV" + AtlantaMain.URL_LINK_MP3
        ss.enviarPW(packet)
    }

    fun ENVIAR_ÑA_LISTA_GFX_TODOS() {
        val packet = "ÑA" + Mundo.LISTA_GFX
        for (perso in Mundo.PERSONAJESONLINE) {
            enviarEnCola(perso, packet, false)
        }
    }

    fun ENVIAR_ÑB_LISTA_NIVEL(ss: ServidorSocket) {
        val packet = "ÑB" + Mundo.LISTA_NIVEL
        ss.enviarPW(packet)
    }

    fun ENVIAR_ÑB_LISTA_NIVEL_TODOS() {
        val packet = "ÑB" + Mundo.LISTA_NIVEL
        for (perso in Mundo.PERSONAJESONLINE) {
            enviarEnCola(perso, packet, false)
        }
    }

    fun ENVIAR_ÑE_DETALLE_MOB(ss: ServidorSocket, str: String) {
        val packet = "ÑE$str"
        ss.enviarPW(packet)
    }

    fun ENVIAR_ÑF_BESTIARIO_MOBS(ss: ServidorSocket, str: String) {
        val packet = "ÑF$str"
        ss.enviarPW(packet)
    }

    fun ENVIAR_Ñf_BESTIARIO_DROPS(ss: ServidorSocket, str: String) {
        val packet = "Ñf$str"
        ss.enviarPW(packet)
        // if (AtlantaMain.MOSTRAR_ENVIOS) {
        // imprimir("BESTIARIO DROPS: PERSO", packet);
        // }
    }

    fun ENVIAR_ÑV_VOTO_RPG(ss: ServidorSocket, str: String) {
        ss.enviarPW("ÑV$str")
    }

    fun ENVIAR_ÑR_ACTIVAR_BOTON_RECURSOS(ss: ServidorSocket) {
        ss.enviarPW("ÑR")
    }

    fun ENVIAR_ÑX_PANEL_ALMANAX(ss: ServidorSocket, str: String) {
        ss.enviarPW("ÑX$str")
    }

    fun ENVIAR_ÑK_TEST_DAÑO_MOB(_perso: Personaje, str: String) {
        val packet = "ÑK$str"
        enviarEnCola(_perso, packet, false)
    }

    fun ENVIAR_ÑS_SERVER_HEROICO(ss: ServidorSocket) {
        ss.enviarPW("ÑS")
    }

    fun ENVIAR_ÑR_BOTON_RECURSOS(ss: ServidorSocket) {
        ss.enviarPW("ÑR")
    }

    fun ENVIAR_Ñm_MENSAJE_NOMBRE_SERVER(ss: ServidorSocket) {
        ss.enviarPW("Ñm" + AtlantaMain.NOMBRE_SERVER)
    }

    fun ENVIAR_ÑG_CLASES_PERMITIDAS(ss: ServidorSocket) {
        val packet = "ÑG" + Mundo.CLASES_PERMITIDAS
        ss.enviarPW(packet)
    }

    fun ENVIAR_ÑO_ID_OBJETO_MODELO_MAX(ss: ServidorSocket) {
        ss.enviarPW("ÑO" + AtlantaMain.MAX_ID_OBJETO_MODELO)
    }

    fun ENVIAR_ÑD_DAÑO_PERMANENTE(ss: ServidorSocket) {
        val packet = "ÑD" + AtlantaMain.PORCENTAJE_DAÑO_NO_CURABLE
        ss.enviarPW(packet)
    }

    fun ENVIAR_ÑM_PANEL_MIMOBIONTE(_perso: Personaje) {
        val packet = "ÑM"
        enviarEnCola(_perso, packet, true)
    }

    fun ENVIAR_ÑJ_STATS_DEFECTO_MOB(_perso: Personaje, str: String) {
        val packet = "ÑJ$str"
        enviarEnCola(_perso, packet, false)
    }

    fun ENVIAR_ÑI_CREA_TU_ITEM_OBJETOS(ss: ServidorSocket) {
        val packet = "ÑI" + Mundo.CREA_TU_ITEM_OBJETOS
        ss.enviarPW(packet)
    }

    fun ENVIAR_Ñi_CREA_TU_ITEM_PRECIOS(ss: ServidorSocket) {
        val packet = "Ñi" + Mundo.CREAT_TU_ITEM_PRECIOS
        ss.enviarPW(packet)
    }

    fun ENVIAR_Ñp_RANGO_NIVEL_PVP(ss: ServidorSocket) {
        val packet = "Ñp" + AtlantaMain.RANGO_NIVEL_PVP
        ss.enviarPW(packet)
    }

    fun ENVIAR_ÑZ_COLOR_CHAT(ss: ServidorSocket) {
        if (AtlantaMain.CANALES_COLOR_CHAT.isEmpty()) {
            return
        }
        val packet = "ÑZ" + AtlantaMain.CANALES_COLOR_CHAT
        ss.enviarPW(packet)
    }

    fun ENVIAR_bo_RESTRINGIR_COLOR_DIA(ss: ServidorSocket) {
        val packet = "bo" + if (AtlantaMain.PARAM_RESTRINGIR_COLOR_DIA) "1" else "0"
        ss.enviarPW(packet)
    }

    fun ENVIAR_SIMULACION_ECONOMIA_COMPRA(ss: Personaje, int: Long) {
        val packet = "H$${Economia.formatNumber(int)}"
        enviarEnCola(ss, packet, true)
        imprimir("ENVIANDO SIMULACION OGRINAS: ", packet)
    }

    fun ENVIAR_SIMULACION_ECONOMIA_VENTA(ss: Personaje, int: Long) {
        val packet = "H%${Economia.formatNumber(int)}"
        enviarEnCola(ss, packet, true)
        imprimir("ENVIANDO SIMULACION OGRINAS: ", packet)
    }

}
