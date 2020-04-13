package servidor

import estaticos.*
import estaticos.AtlantaMain.OGRINAS_INVITADO
import estaticos.AtlantaMain.OGRINAS_INVITADOR
import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Comandos.consolaComando
import estaticos.Condiciones.validaCondiciones
import estaticos.Formulas.formatoTiempo
import estaticos.Formulas.getRandomInt
import estaticos.Formulas.valorValido
import estaticos.GestorSQL.DELETE_REPORTE
import estaticos.GestorSQL.ES_IP_BANEADA
import estaticos.GestorSQL.GET_BANEADO
import estaticos.GestorSQL.GET_CREDITOS_CUENTA
import estaticos.GestorSQL.GET_DESCRIPTION_REPORTE
import estaticos.GestorSQL.GET_ID_WEB
import estaticos.GestorSQL.GET_LISTA_REPORTES
import estaticos.GestorSQL.GET_MENSAJE_PENDIENTE
import estaticos.GestorSQL.GET_OGRINAS_CUENTA
import estaticos.GestorSQL.GET_REFERIDOS_CUENTA
import estaticos.GestorSQL.INSERTAR_MENSAJE_PENDIENTE
import estaticos.GestorSQL.INSERT_BAN_IP
import estaticos.GestorSQL.INSERT_DENUNCIAS
import estaticos.GestorSQL.INSERT_GREMIO
import estaticos.GestorSQL.INSERT_PROBLEMA_OGRINAS
import estaticos.GestorSQL.INSERT_REPORTE_BUG
import estaticos.GestorSQL.INSERT_SUGERENCIAS
import estaticos.GestorSQL.QUERY_ALTERNA
import estaticos.GestorSQL.QUERY_CUENTAS
import estaticos.GestorSQL.QUERY_DINAMICA
import estaticos.GestorSQL.QUERY_ESTATICA
import estaticos.GestorSQL.REPLACE_MONTURA
import estaticos.GestorSQL.RESTAR_CREDITOS
import estaticos.GestorSQL.RESTAR_OGRINAS
import estaticos.GestorSQL.SALVAR_OBJETO
import estaticos.GestorSQL.SALVAR_PERSONAJE
import estaticos.GestorSQL.SET_BANEADO
import estaticos.GestorSQL.SET_OGRINAS_CUENTA
import estaticos.GestorSalida.ENVIAR_AAE_ERROR_CREAR_PJ
import estaticos.GestorSalida.ENVIAR_AAK_CREACION_PJ_OK
import estaticos.GestorSalida.ENVIAR_ADE_ERROR_BORRAR_PJ
import estaticos.GestorSalida.ENVIAR_AG_SIGUIENTE_REGALO
import estaticos.GestorSalida.ENVIAR_ALK_LISTA_DE_PERSONAJES
import estaticos.GestorSalida.ENVIAR_APK_NOMBRE_PJ_ALEATORIO
import estaticos.GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE
import estaticos.GestorSalida.ENVIAR_ASE_SELECCION_PERSONAJE_FALLIDA
import estaticos.GestorSalida.ENVIAR_ATE_TICKET_FALLIDA
import estaticos.GestorSalida.ENVIAR_ATK_TICKET_A_CUENTA
import estaticos.GestorSalida.ENVIAR_AV_VERSION_REGIONAL
import estaticos.GestorSalida.ENVIAR_Ag_LISTA_REGALOS
import estaticos.GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ
import estaticos.GestorSalida.ENVIAR_AlEb_CUENTA_BANEADA_DEFINITIVO
import estaticos.GestorSalida.ENVIAR_AlEk_CUENTA_BANEADA_TIEMPO
import estaticos.GestorSalida.ENVIAR_As_STATS_DEL_PJ
import estaticos.GestorSalida.ENVIAR_BAIO_HABILITAR_ADMIN
import estaticos.GestorSalida.ENVIAR_BAT2_CONSOLA
import estaticos.GestorSalida.ENVIAR_BD_FECHA_SERVER
import estaticos.GestorSalida.ENVIAR_BN_NADA
import estaticos.GestorSalida.ENVIAR_BT_TIEMPO_SERVER
import estaticos.GestorSalida.ENVIAR_BWK_QUIEN_ES
import estaticos.GestorSalida.ENVIAR_CB_BONUS_CONQUISTA
import estaticos.GestorSalida.ENVIAR_CIJ_INFO_UNIRSE_PRISMA
import estaticos.GestorSalida.ENVIAR_CIV_CERRAR_INFO_CONQUISTA
import estaticos.GestorSalida.ENVIAR_CW_INFO_MUNDO_CONQUISTA
import estaticos.GestorSalida.ENVIAR_Cb_BALANCE_CONQUISTA
import estaticos.GestorSalida.ENVIAR_DCK_CREAR_DIALOGO
import estaticos.GestorSalida.ENVIAR_DQ_DIALOGO_PREGUNTA
import estaticos.GestorSalida.ENVIAR_EBE_ERROR_DE_COMPRA
import estaticos.GestorSalida.ENVIAR_EBK_COMPRADO
import estaticos.GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS
import estaticos.GestorSalida.ENVIAR_EHL_LISTA_OBJMERCA_POR_TIPO
import estaticos.GestorSalida.ENVIAR_EHM_MOVER_OBJMERCA_POR_MODELO
import estaticos.GestorSalida.ENVIAR_EHP_PRECIO_PROMEDIO_OBJ
import estaticos.GestorSalida.ENVIAR_EHS_BUSCAR_OBJETO_MERCADILLO
import estaticos.GestorSalida.ENVIAR_EHl_LISTA_LINEAS_OBJMERCA_POR_MODELO
import estaticos.GestorSalida.ENVIAR_EJ_DESCRIPCION_LIBRO_ARTESANO
import estaticos.GestorSalida.ENVIAR_EL_LISTA_EXCHANGER
import estaticos.GestorSalida.ENVIAR_ERE_ERROR_CONSULTA
import estaticos.GestorSalida.ENVIAR_ERK_CONSULTA_INTERCAMBIO
import estaticos.GestorSalida.ENVIAR_ESE_ERROR_VENTA
import estaticos.GestorSalida.ENVIAR_EV_CERRAR_VENTANAS
import estaticos.GestorSalida.ENVIAR_EW_OFICIO_MODO_INVITACION
import estaticos.GestorSalida.ENVIAR_EW_OFICIO_MODO_PUBLICO
import estaticos.GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO
import estaticos.GestorSalida.ENVIAR_Ef_MONTURA_A_CRIAR
import estaticos.GestorSalida.ENVIAR_Eq_PREGUNTAR_MERCANTE
import estaticos.GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO
import estaticos.GestorSalida.ENVIAR_Ew_PODS_MONTURA
import estaticos.GestorSalida.ENVIAR_FA_AGREGAR_AMIGO
import estaticos.GestorSalida.ENVIAR_FD_BORRAR_AMIGO
import estaticos.GestorSalida.ENVIAR_FL_LISTA_DE_AMIGOS
import estaticos.GestorSalida.ENVIAR_GA900_DESAFIAR
import estaticos.GestorSalida.ENVIAR_GA901_ACEPTAR_DESAFIO
import estaticos.GestorSalida.ENVIAR_GA903_ERROR_PELEA
import estaticos.GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA
import estaticos.GestorSalida.ENVIAR_GDK_CARGAR_MAPA
import estaticos.GestorSalida.ENVIAR_GDM_MAPDATA_COMPLETO
import estaticos.GestorSalida.ENVIAR_GDO_OBJETO_TIRAR_SUELO
import estaticos.GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA
import estaticos.GestorSalida.ENVIAR_GM_DRAGOPAVO_A_MAPA
import estaticos.GestorSalida.ENVIAR_GM_RECAUDADOR_A_MAPA
import estaticos.GestorSalida.ENVIAR_GR_TODOS_LUCHADORES_LISTOS
import estaticos.GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA
import estaticos.GestorSalida.ENVIAR_Gñ_IDS_PARA_MODO_CRIATURA
import estaticos.GestorSalida.ENVIAR_IC_BORRAR_BANDERA_COMPAS
import estaticos.GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS
import estaticos.GestorSalida.ENVIAR_IH_COORDENADAS_UBICACION
import estaticos.GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO
import estaticos.GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE
import estaticos.GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.GestorSalida.ENVIAR_JO_OFICIO_OPCIONES
import estaticos.GestorSalida.ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT
import estaticos.GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION
import estaticos.GestorSalida.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO
import estaticos.GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO
import estaticos.GestorSalida.ENVIAR_ODE_ERROR_ELIMINAR_OBJETO
import estaticos.GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO
import estaticos.GestorSalida.ENVIAR_OS_BONUS_SET
import estaticos.GestorSalida.ENVIAR_Ow_PODS_DEL_PJ
import estaticos.GestorSalida.ENVIAR_PA_ACEPTAR_INVITACION_GRUPO
import estaticos.GestorSalida.ENVIAR_PF_SEGUIR_PERSONAJE
import estaticos.GestorSalida.ENVIAR_PIE_ERROR_INVITACION_GRUPO
import estaticos.GestorSalida.ENVIAR_PIK_INVITAR_GRUPO
import estaticos.GestorSalida.ENVIAR_PM_AGREGAR_PJ_GRUPO_A_GRUPO
import estaticos.GestorSalida.ENVIAR_QL_LISTA_MISIONES
import estaticos.GestorSalida.ENVIAR_QS_PASOS_RECOMPENSA_MISION
import estaticos.GestorSalida.ENVIAR_Rd_DESCRIPCION_MONTURA
import estaticos.GestorSalida.ENVIAR_Re_DETALLES_MONTURA
import estaticos.GestorSalida.ENVIAR_Rn_CAMBIO_NOMBRE_MONTURA
import estaticos.GestorSalida.ENVIAR_Rp_INFORMACION_CERCADO
import estaticos.GestorSalida.ENVIAR_Rv_MONTURA_CERRAR
import estaticos.GestorSalida.ENVIAR_Rx_EXP_DONADA_MONTURA
import estaticos.GestorSalida.ENVIAR_SUE_NIVEL_HECHIZO_ERROR
import estaticos.GestorSalida.ENVIAR_TB_CINEMA_INICIO_JUEGO
import estaticos.GestorSalida.ENVIAR_WV_CERRAR_ZAAP
import estaticos.GestorSalida.ENVIAR_Wv_CERRAR_ZAPPI
import estaticos.GestorSalida.ENVIAR_Ww_CERRAR_PRISMA
import estaticos.GestorSalida.ENVIAR_XML_POLICY_FILE
import estaticos.GestorSalida.ENVIAR_bA_ESCOGER_NIVEL
import estaticos.GestorSalida.ENVIAR_bD_LISTA_REPORTES
import estaticos.GestorSalida.ENVIAR_bI_SISTEMA_RECURSO
import estaticos.GestorSalida.ENVIAR_bL_RANKING_PERMITIDOS
import estaticos.GestorSalida.ENVIAR_bOC_ABRIR_PANEL_SERVICIOS
import estaticos.GestorSalida.ENVIAR_bP_VOTO_RPG_PARADIZE
import estaticos.GestorSalida.ENVIAR_bRI_INICIAR_CUENTA_REGRESIVA
import estaticos.GestorSalida.ENVIAR_bT_PANEL_LOTERIA
import estaticos.GestorSalida.ENVIAR_bb_DATA_CREAR_ITEM
import estaticos.GestorSalida.ENVIAR_bo_RESTRINGIR_COLOR_DIA
import estaticos.GestorSalida.ENVIAR_brG_RULETA_GANADOR
import estaticos.GestorSalida.ENVIAR_bt_PANEL_TITULOS
import estaticos.GestorSalida.ENVIAR_bñ_PANEL_ORNAMENTOS
import estaticos.GestorSalida.ENVIAR_cC_SUSCRIBIR_CANAL
import estaticos.GestorSalida.ENVIAR_cMEf_CHAT_ERROR
import estaticos.GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE
import estaticos.GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS
import estaticos.GestorSalida.ENVIAR_cs_CHAT_MENSAJE
import estaticos.GestorSalida.ENVIAR_dV_CERRAR_DOCUMENTO
import estaticos.GestorSalida.ENVIAR_eD_CAMBIAR_ORIENTACION
import estaticos.GestorSalida.ENVIAR_eUK_EMOTE_MAPA
import estaticos.GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS
import estaticos.GestorSalida.ENVIAR_fD_DETALLES_PELEA
import estaticos.GestorSalida.ENVIAR_fL_LISTA_PELEAS
import estaticos.GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO
import estaticos.GestorSalida.ENVIAR_gIB_GREMIO_INFO_BOOST
import estaticos.GestorSalida.ENVIAR_gIF_GREMIO_INFO_CERCADOS
import estaticos.GestorSalida.ENVIAR_gIG_GREMIO_INFO_GENERAL
import estaticos.GestorSalida.ENVIAR_gIH_GREMIO_INFO_CASAS
import estaticos.GestorSalida.ENVIAR_gIM_GREMIO_INFO_MIEMBROS
import estaticos.GestorSalida.ENVIAR_gITM_GREMIO_INFO_RECAUDADOR
import estaticos.GestorSalida.ENVIAR_gJ_GREMIO_UNIR
import estaticos.GestorSalida.ENVIAR_gK_GREMIO_BAN
import estaticos.GestorSalida.ENVIAR_gS_STATS_GREMIO
import estaticos.GestorSalida.ENVIAR_gT_PANEL_RECAUDADORES_GREMIO
import estaticos.GestorSalida.ENVIAR_gV_CERRAR_PANEL_GREMIO
import estaticos.GestorSalida.ENVIAR_iL_LISTA_ENEMIGOS
import estaticos.GestorSalida.ENVIAR_kA_ACEPTAR_INVITACION_KOLISEO
import estaticos.GestorSalida.ENVIAR_kIE_ERROR_INVITACION_KOLISEO
import estaticos.GestorSalida.ENVIAR_kIK_INVITAR_KOLISEO
import estaticos.GestorSalida.ENVIAR_kP_PANEL_KOLISEO
import estaticos.GestorSalida.ENVIAR_kV_DEJAR_KOLISEO
import estaticos.GestorSalida.ENVIAR_pong
import estaticos.GestorSalida.ENVIAR_zC_LISTA_ZONAS
import estaticos.GestorSalida.ENVIAR_zV_CERRAR_ZONAS
import estaticos.GestorSalida.ENVIAR_ÑA_LISTA_GFX
import estaticos.GestorSalida.ENVIAR_ÑB_LISTA_NIVEL
import estaticos.GestorSalida.ENVIAR_ÑD_DAÑO_PERMANENTE
import estaticos.GestorSalida.ENVIAR_ÑE_DETALLE_MOB
import estaticos.GestorSalida.ENVIAR_ÑF_BESTIARIO_MOBS
import estaticos.GestorSalida.ENVIAR_ÑG_CLASES_PERMITIDAS
import estaticos.GestorSalida.ENVIAR_ÑI_CREA_TU_ITEM_OBJETOS
import estaticos.GestorSalida.ENVIAR_ÑL_BOTON_LOTERIA
import estaticos.GestorSalida.ENVIAR_ÑM_PANEL_MIMOBIONTE
import estaticos.GestorSalida.ENVIAR_ÑO_ID_OBJETO_MODELO_MAX
import estaticos.GestorSalida.ENVIAR_ÑR_BOTON_RECURSOS
import estaticos.GestorSalida.ENVIAR_ÑS_SERVER_HEROICO
import estaticos.GestorSalida.ENVIAR_ÑU_URL_IMAGEN_VOTO
import estaticos.GestorSalida.ENVIAR_ÑV_ACTUALIZAR_URL_LINK_MP3
import estaticos.GestorSalida.ENVIAR_ÑV_VOTO_RPG
import estaticos.GestorSalida.ENVIAR_ÑX_PANEL_ALMANAX
import estaticos.GestorSalida.ENVIAR_ÑZ_COLOR_CHAT
import estaticos.GestorSalida.ENVIAR_Ña_AUTO_PASAR_TURNO
import estaticos.GestorSalida.ENVIAR_Ñe_EXO_PANEL_ITEMS
import estaticos.GestorSalida.ENVIAR_Ñf_BESTIARIO_DROPS
import estaticos.GestorSalida.ENVIAR_Ñi_CREA_TU_ITEM_PRECIOS
import estaticos.GestorSalida.ENVIAR_Ñp_RANGO_NIVEL_PVP
import estaticos.GestorSalida.ENVIAR_Ñr_SUFJIO_RESET
import estaticos.GestorSalida.ENVIAR_Ñs_BOTON_BOUTIQUE
import estaticos.GestorSalida.ENVIAR_Ñu_URL_LINK_VOTO
import estaticos.GestorSalida.ENVIAR_Ñx_URL_LINK_BUG
import estaticos.GestorSalida.ENVIAR_Ñz_URL_LINK_COMPRA
import estaticos.GestorSalida.enviar
import org.apache.mina.core.session.IoSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sprites.Preguntador
import utilidades.buscadores.Comparador
import utilidades.economia.Economia
import utilidades.seguridad.IpsVerificator
import utilidades.seguridad.tokenGenerator
import variables.casa.Casa
import variables.gremio.Gremio
import variables.gremio.Recaudador
import variables.mapa.Cercado
import variables.mapa.Mapa
import variables.mercadillo.Mercadillo
import variables.mision.MisionObjetivoModelo
import variables.montura.Montura
import variables.montura.Montura.Ubicacion
import variables.npc.NPC
import variables.npc.PreguntaNPC
import variables.npc.Trueque
import variables.objeto.CreaTuItem
import variables.objeto.Objeto
import variables.objeto.ObjetoModelo
import variables.objeto.ObjetoModelo.CAPACIDAD_STATS
import variables.objeto.ObjetoModelo.Companion.getStatSegunFecha
import variables.objeto.ObjetoModelo.Companion.stringFechaIntercambiable
import variables.oficio.Trabajo
import variables.personaje.*
import variables.zotros.Accion
import variables.zotros.Servicio
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.IOException
import java.net.InetSocketAddress
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern
import javax.swing.Timer
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.math.*

class ServidorSocket(val session: IoSession) {
    //
// dinamicos
//
    var action: Long = 0
    var logger: Logger
    var cuenta: Cuenta? = null
        private set
    var personaje: Personaje? = null
    private var _timerAcceso: Timer? = null
    var actualIP: String? = null
    private var _ultimoPacket: String? = null
    private var _votarDespuesPelea = false
    private var _accionesDeJuego: MutableMap<Int, AccionDeJuego>? = null
    var _tiempoUltComercio: Long = 0
    var _tiempoUltReclutamiento: Long = 0
    var _tiempoUltAlineacion: Long = 0
    var _tiempoUltIncarnam: Long = 0
    var _tiempoUltVIP: Long = 0
    private var _ultSalvada: Long = 0
    var tiempoUltPacket: Long = 0
        private set
    private var _tiempoLLegoMapa: Long = 0
    private var _tiempoUltAll: Long = 0
    var ping: Long = 0
        private set
    private var _lastMillis: Long = 0
    private var _ultMillis: Long = 0
    private var _excesoPackets: Byte = 0
    private var _sigPacket: Byte = 0
    private var _realizaciandoAccion = false
    private lateinit var _timePackets: LongArray
    private lateinit var _ultPackets: Array<String?>
    private lateinit var _aKeys: Array<String?>
    private var _currentKey = -1
    private var _iniciandoPerso = true
    var packetAnterior = ""
    var idioma = "es"

    fun kick() {
        cerrarSocket(true, "Cerrado Forzado")
    }

    fun disconnect() {
        cerrarSocket(true, "Cerrado Normal")
    }

    fun send(packet: String) {
        try {
            session.write(packet)
        } catch (e: Exception) {
            logger.warn("Send fail : $packet")
            e.printStackTrace()
        }
    }

    private fun posibleAtaque() {
        if (POSIBLES_ATAQUES[actualIP] != null) {
            val veces = POSIBLES_ATAQUES[actualIP]!!
            if (veces > AtlantaMain.VECES_PARA_BAN_IP_SIN_ESPERA) {
                INSERT_BAN_IP(actualIP!!)
            } else {
                POSIBLES_ATAQUES[actualIP] = veces + 1
            }
        } else {
            POSIBLES_ATAQUES[actualIP] = 0
        }
    }

    private fun crearTimerAcceso() {
        _timerAcceso = Timer(10 * 1000, ActionListener { arg0: ActionEvent? ->
            redactarLogServidorln("TIMER ACCEDER SERVER AGOTADO (posible ataque): $actualIP")
            posibleAtaque()
            cerrarSocket(true, "crearTimerAcceso()")
        })
    }

    private fun crearPacketKey(): String {
        if (AtlantaMain.PARAM_ENCRIPTAR_PACKETS) {
            _currentKey = getRandomInt(1, 15)
            val key = Encriptador.crearKey(16)
            _aKeys[_currentKey] = Encriptador.prepareKey(key)
            return Integer.toHexString(_currentKey).toUpperCase() + key
        }
        return "0"
    }


    fun enviarPWSinEncriptar(packet: String) {
        enviarPW(packet, true, false)
    }

    fun enviarPW(packet: String) {
        enviarPW(packet, false, true)
    }

    fun enviarPW(p: String, redactar: Boolean, encriptado: Boolean) {
        try {
            if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
                return
            }
            for (packet in p.split(("" + Constantes.x0char).toRegex()).toTypedArray()) {
                try {
                    var packet2 = packet
                    if (AtlantaMain.PARAM_ENCRIPTAR_PACKETS && encriptado) {
                        packet2 = Encriptador.prepareData(packet, _currentKey, _aKeys)
                    }
//                    packet2 = Encriptador.aUTF(packet2)
                    send(packet2 + Constantes.x0char)
                    if (redactar && personaje != null) {
                        personaje?.registrar("<<=== $packet2")
                    }
                } catch (e: Exception) {
                    redactarLogServidorln("La cuenta ${cuenta?.nombre} Ha obtenido un error en el envio de packets $e")
                    this.cerrarSocket(true, "")
                }
            }
        } catch (e: Exception) {
            redactarLogServidorln("La cuenta ${cuenta?.nombre} Ha obtenido un error en el envio de packets $e")
        }
    }

    fun cerrarSocket(cuenta: Boolean, n: String) {
        try {
            if (AtlantaMain.MODO_DEBUG) {
                redactarLogServidorln("CERRAR SOCKET $n")
            }
            ServidorServer.delEsperandoCuenta(this.cuenta)
            _timerAcceso?.stop()
            if (cuenta) {
                registrar("<===> DESCONECTANDO CON ULTIMO PACKET $_ultimoPacket")
                this.cuenta?.desconexion()
            }
            this.cuenta?.socket = null
            this.cuenta = null
            this.personaje?.desconectar(true)
            personaje = null
            _timerAcceso = null
            _accionesDeJuego = null
            session.attachment = null
            session.closeNow()
        } catch (e: Exception) {
            redactarLogServidorln("EXCEPTION al cerrar servidor socket $e")
            e.printStackTrace()
        }
    }

    fun rastrear(packet: String) {
        try {
            if (RASTREAR_IPS.contains(actualIP) || cuenta != null && RASTREAR_CUENTAS.contains(cuenta!!.id)) {
                if (personaje != null) {
                    redactarLogServidorln("[" + personaje!!.nombre + "] " + packet)
                } else if (cuenta != null) {
                    redactarLogServidorln("<<" + cuenta!!.nombre + ">> " + packet)
                } else {
                    redactarLogServidorln("{$actualIP} $packet")
                }
            }
        } catch (ignored: Exception) {
        }
    }

    fun registrar(packet: String?) {
        try {
            if (cuenta != null && cuenta?.sinco == false) {
                if (REGISTROS[cuenta!!.nombre] == null) {
                    REGISTROS[cuenta!!.nombre] = StringBuilder()
                }
                REGISTROS[cuenta!!.nombre]!!.append(System.currentTimeMillis()).append(" - ").append(
                    Date(
                        System
                            .currentTimeMillis()
                    )
                ).append(" : \t").append(packet).append("\n")
            }
        } catch (ignored: Exception) {
        }
    }

    private fun registrarUltPing() {
        _ultMillis++
        enviarPW("rpong$_ultMillis")
        _lastMillis = System.currentTimeMillis()
    }

    fun analizar_Packets(packet: String) {
        if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
            return
        }
        if (packet == "<policy-file-request/>") {
            ENVIAR_XML_POLICY_FILE(this)
            return
        }
        if (packet[0] != 'A' && personaje == null) {
            ENVIAR_BN_NADA(this)
            return
        }
        tiempoUltPacket = System.currentTimeMillis()
        _ultimoPacket = packet
        if (antiFlood(packet)) {
            return
        }
        if (cuenta?.bloqueado == true) { //falta que cuando ya se conecte el pj no lo deje hacer nada, no antes
            if (!(packet[0] == 'A' || packet[0] == 'B' || packet[0] == 'G')) {
                ENVIAR_BN_NADA(this)
                return
            }
        }
        when (packet[0]) {
            'A' -> analizar_Cuenta(packet)
            'B' -> analizar_Basicos(packet)
            'C' -> {
                try {
                    analizar_Conquista(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'c' -> {
                try {
                    analizar_Canal(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'D' -> {
                try {
                    analizar_Dialogos(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'd' -> {
                try {
                    analizar_Documentos(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'E' -> {
                try {
                    analizar_Intercambios(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'e' -> {
                try {
                    analizar_Ambiente(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'F' -> {
                try {
                    analizar_Amigos(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'f' -> {
                try {
                    analizar_Peleas(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'G' -> {
                try {
                    analizar_Juego(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'g' -> {
                try {
                    analizar_Gremio(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'h' -> {
                try {
                    analizar_Casas(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'i' -> {
                try {
                    analizar_Enemigos(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'I' -> {
            }
            'J' -> {
                try {
                    analizar_Oficios(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'k' -> {
                try {
                    analizar_Koliseo(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'K' -> {
                try {
                    analizar_Claves(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'Ñ' -> try {
                ENVIAR_ÑV_VOTO_RPG(
                    this, Mundo.CAPTCHAS[getRandomInt(
                        0, Mundo.CAPTCHAS.size
                                - 1
                    )]
                )
            } catch (ignored: Exception) {
            }
            'O' -> {
                try {
                    analizar_Objetos(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'P' -> {
                try {
                    analizar_Grupo(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'p' -> if (packet == "ping") {
                ENVIAR_pong(personaje!!)
            }
            'Q' -> {
                try {
                    analizar_Misiones(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'q' -> {
                try {
                    analizar_Qping(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'R' -> {
                try {
                    analizar_Montura(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'r' -> try {
                val i = packet.substring(5).toInt()
                if (i.toLong() == _ultMillis) {
                    ping = System.currentTimeMillis() - _lastMillis
                }
            } catch (ignored: Exception) {
            }
            'S' -> {
                try {
                    analizar_Hechizos(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'T' -> {
                try {
                    analizar_Tutoriales(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'W' -> {
                try {
                    analizar_Areas(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'z' -> {
                try {
                    analizar_Zonas(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'Z' -> {
                try {
                    analizar_Atlanta(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            'X' -> {
                try {
                    analizar_Atlanta(packet)
                } catch (e: Exception) {
                    redactarLogServidorln("Error en thread $e")
                }
            }
            '|' -> {
            }
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR PACKETS: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "analizar_Packets()")
                }
            }
        }
    }

    private fun analizar_Qping(packet: String) {
        if (packet == "qping") { // if (_perso.getPelea() != null) {
// _perso.getPelea().tiempoParaPasarTurno();
// }
            ENVIAR_BN_NADA(personaje, "QPING")
        }
    }

    private fun antiFlood(packet: String): Boolean {
        try {
            if (!AtlantaMain.PARAM_ANTIFLOOD) {
                return false
            }
            if (packet.equals(
                    "GT",
                    ignoreCase = true
                ) || packet == "BD" || packet == "qping" || packet == "ping" || packet == "EMR1"
            ) {
                return false
            } else if (packet.length >= 2 && (packet.substring(0, 2).equals(
                    "OU",
                    ignoreCase = true
                ) || (packet.substring(0, 2)
                        == "EB") || packet.substring(0, 2) == "BA" || packet.substring(0, 2) == "GP")
            ) {
                return false
            } else if (packet.length >= 3 && packet.substring(0, 3).equals("EMO", ignoreCase = true)) {
                return false
            } else if (packet.length >= 5 && packet.substring(0, 5) == "GA300") {
                return false
            } else {
                _ultPackets[_sigPacket.toInt()] = packet
                _timePackets[_sigPacket.toInt()] = System.currentTimeMillis()
                _sigPacket = (_sigPacket.toInt() + 1).toByte()
                if (_sigPacket >= 7) {
                    _sigPacket = 0
                }
                if (_ultPackets[0] == _ultPackets[1] && System.currentTimeMillis()
                    - _timePackets[_sigPacket.toInt()] < AtlantaMain.MILISEGUNDOS_ANTI_FLOOD
                ) {
                    if (_ultPackets[1] == _ultPackets[2]) {
                        if (_ultPackets[2] == _ultPackets[3]) {
                            if (_ultPackets[3] == _ultPackets[4]) {
                                if (_ultPackets[4] == _ultPackets[5]) {
                                    if (_ultPackets[5] == _ultPackets[6]) {
                                        if (_ultPackets[6] == _ultPackets[0]) {
                                            registrar("<===> EXPULSADOR POR ANTI-FLOOD PACKET $packet")
                                            ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(
                                                this, "45", "DISCONNECT FOR FLOOD",
                                                ""
                                            )
                                            cerrarSocket(true, "antiFlood")
                                            return true
                                        }
                                    }
                                } else {
                                    ENVIAR_Im_INFORMACION(personaje!!, "1ADVERTENCIA_FLOOD")
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            redactarLogServidorln("EXCEPTION Packet $packet, antiFlood $e")
            e.printStackTrace()
        }
        return false
    }

    private fun analizar_Cuenta(packet: String) {
        try {
            if (packet.length < 2) {
                try {
                    ENVIAR_BN_NADA(personaje)
                    personaje!!.enviarmensajeNegro("Por Favor repita su accion, El servidor ha perdido los datos en el camino")
                    redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
                } catch (e: Exception) {
                    redactarLogServidorln("Packet Loss: $packet")
                }
                return
            }
            when (packet[1]) {
                'A' -> {
                    if (personaje != null) {
                        return
                    }
                    if (cuenta?.bloqueado == true) {
                        ENVIAR_BN_NADA(this)
                        return
                    }
                    cuenta_Crear_Personaje(packet)
                }
                'B' -> {
                    if (personaje == null) {
                        return
                    }
                    if (cuenta?.bloqueado == true) {
                        ENVIAR_BN_NADA(this)
                        return
                    }
                    cuenta_Boostear_Stat(packet)
                }
                'D' -> {
                    if (personaje != null) {
                        return
                    }
                    if (cuenta?.bloqueado == true) {
                        ENVIAR_BN_NADA(this)
                        return
                    }
                    cuenta_Eliminar_Personaje(packet)
                }
                'f' -> if (_excesoPackets > 10) {
                    cerrarSocket(true, "analizarCuenta(1)")
                } else {
                    _excesoPackets++
                }
                'g' -> cuenta_Idioma(packet)
                'G' -> {
                    if (personaje != null) {
                        return
                    }
                    cuenta_Entregar_Regalo(packet.substring(2))
                }
                'i' -> if (personaje != null) {
                    return
                }
                'k' -> {
                }
                'L' -> {
                    if (personaje != null) {
                        return
                    }
                    // LAS Ñs y sus variables globales
// GestorSalida.ENVIAR_Ñm_MENSAJE_NOMBRE_SERVER(this);;
                    ENVIAR_ÑG_CLASES_PERMITIDAS(this)
                    ENVIAR_ÑO_ID_OBJETO_MODELO_MAX(this)
                    ENVIAR_Ña_AUTO_PASAR_TURNO(this)
                    ENVIAR_Ñe_EXO_PANEL_ITEMS(this)
                    ENVIAR_Ñr_SUFJIO_RESET(this)
                    ENVIAR_ÑD_DAÑO_PERMANENTE(this)
                    // el % de daño incurable
                    ENVIAR_ÑI_CREA_TU_ITEM_OBJETOS(this)
                    ENVIAR_Ñp_RANGO_NIVEL_PVP(this)
                    ENVIAR_ÑZ_COLOR_CHAT(this)
                    ENVIAR_ÑV_ACTUALIZAR_URL_LINK_MP3(this)
                    ENVIAR_bo_RESTRINGIR_COLOR_DIA(this)
                    if (AtlantaMain.URL_IMAGEN_VOTO.isNotEmpty()) {
                        ENVIAR_ÑU_URL_IMAGEN_VOTO(this)
                    }
                    if (AtlantaMain.URL_LINK_VOTO.isNotEmpty()) {
                        ENVIAR_Ñu_URL_LINK_VOTO(this)
                    }
                    if (AtlantaMain.URL_LINK_BUG.isNotEmpty()) {
                        ENVIAR_Ñx_URL_LINK_BUG(this)
                    }
                    if (AtlantaMain.URL_LINK_COMPRA.isNotEmpty()) {
                        ENVIAR_Ñz_URL_LINK_COMPRA(this)
                    }
                    ENVIAR_ALK_LISTA_DE_PERSONAJES(this, cuenta)
                }
                'P' -> {
                    if (personaje != null) {
                        return
                    }
                    ENVIAR_APK_NOMBRE_PJ_ALEATORIO(this, Encriptador.palabraAleatorio(5))
                }
                'R' -> {
                    if (personaje != null || !AtlantaMain.MODO_HEROICO && AtlantaMain.MAPAS_MODO_HEROICO.isEmpty()) {
                        return
                    }
                    if (cuenta?.bloqueado == true) {
                        ENVIAR_BN_NADA(this)
                        return
                    }
                    cuenta_Reiniciar_Personaje(packet)
                }
                'S' -> {
                    if (personaje != null) {
                        return
                    }
                    cuenta_Seleccion_Personaje(packet)
                }
                'T' -> {
                    if (personaje != null) {
                        return
                    }
                    cuenta_Acceder_Server(packet)
                }
                'V' -> {
                    if (personaje != null) {
                        return
                    }
                    ENVIAR_AV_VERSION_REGIONAL(this)
                }
                else -> {
                    redactarLogServidorln("$stringDesconocido ANALIZAR CUENTA: $packet")
                    if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                        redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                        cerrarSocket(true, "analizarCuenta(2)")
                    }
                }
            }
        } catch (e: Exception) {
            redactarLogServidorln(
                "EXCEPTION Packet " + packet + ", analizar cuenta " + e.toString()
                        + ", packet " + packet
            )
            e.printStackTrace()
        }
    }

    private val stringDesconocido: String
        private get() {
            _excesoPackets++
            if (_excesoPackets >= AtlantaMain.MAX_PACKETS_PARA_RASTREAR) {
                if (!RASTREAR_IPS.contains(actualIP)) {
                    RASTREAR_IPS.add(actualIP)
                }
                if (cuenta != null) {
                    if (!RASTREAR_CUENTAS.contains(cuenta!!.id)) {
                        RASTREAR_CUENTAS.add(cuenta!!.id)
                    }
                }
            }
            return "PACKET DESCONOCIDO Cuenta: " + if (cuenta == null) " null " else cuenta!!.nombre + "(" + cuenta!!.id + ") Perso: " + if (personaje == null) " null " else personaje!!.nombre + "(" + personaje!!.Id + ")"
        }

    private fun cuenta_Acceder_Server(packet: String) {
        try {
            for (i in 0..12) {
                cuenta = ServidorServer.getEsperandoCuenta(packet.substring(2).toInt()) ?: if (Mundo.getCuenta(
                        packet.substring(2).toInt()
                    )?.ultimaIP == actualIP
                ) Mundo.getCuenta(packet.substring(2).toInt()) else null ?: continue
                try {
                    if (cuenta != null && cuenta?.socket != null && cuenta?.socket != this) {
                        cuenta?.socket?.cerrarSocket(true, "Forzado")
                        continue
                    }
                } catch (e: Exception) {
                }
                try {
                    if (cuenta != null) {
                        try {
                            if (_timerAcceso != null && _timerAcceso!!.isRunning) {
                                _timerAcceso?.stop()
                            }
                            _timerAcceso = null
                        } catch (ignored: Exception) {
                        }
                        ServidorServer.delEsperandoCuenta(cuenta)
                        val cuentasPorIP = ServidorServer.getIPsClientes(actualIP)
                        if (cuenta!!.admin <= 0 && cuentasPorIP >= AtlantaMain.MAX_CUENTAS_POR_IP) {
                            ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(
                                this, "34", cuentasPorIP.toString() + ";"
                                        + AtlantaMain.MAX_CUENTAS_POR_IP, ""
                            )
                            cerrarSocket(false, "cuenta_Acceder_Server(0)")
                            return
                        }
                        if (cuenta?.admin ?: 0 < AtlantaMain.ACCESO_ADMIN_MINIMO) {
                            ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(this, "19", "", "")
                            cerrarSocket(true, "cuenta_Acceder_Server(1)")
                            return
                        }
                        try {
                            if (ES_IP_BANEADA(actualIP!!)) {
                                ENVIAR_AlEb_CUENTA_BANEADA_DEFINITIVO(this)
                                cerrarSocket(true, "cuenta_Acceder_Server(2)")
                                return
                            }
                        } catch (e: Exception) {
                        }
                        val tiempoBaneo = GET_BANEADO(cuenta!!.nombre)
                        if (tiempoBaneo != 0L) {
                            when {
                                tiempoBaneo <= -1 -> {
                                    ENVIAR_AlEb_CUENTA_BANEADA_DEFINITIVO(this)
                                    cerrarSocket(true, "cuenta_Acceder_Server(3)")
                                    return
                                }
                                tiempoBaneo > System.currentTimeMillis() -> {
                                    ENVIAR_AlEk_CUENTA_BANEADA_TIEMPO(this, tiempoBaneo)
                                    cerrarSocket(true, "cuenta_Acceder_Server(4)")
                                    return
                                }
                                else -> {
                                    SET_BANEADO(cuenta!!.nombre, 0)
                                }
                            }
                        }
                        cuenta?.setEntradaPersonaje(this)
                        cuenta?.actualIP = actualIP as String
                        IpsVerificator.ipdiferente(cuenta)
                        ENVIAR_ATK_TICKET_A_CUENTA(this, crearPacketKey())
                        idioma = cuenta?.idioma ?: "es"
                        if (AtlantaMain.MODO_HEROICO) {
                            ENVIAR_ÑS_SERVER_HEROICO(this)
                        }
                        val c = cuenta ?: continue
                        this.logger = LoggerFactory.getLogger(cuenta!!.nombre)
                        for (perso in c.personajes) {
                            if (perso != null) {
                                if (perso.pelea == null) {
                                    continue
                                }
                            }
                            personaje = perso
                            personaje?.conectarse()
                            return
                        }
                        return
                    }
                } catch (e: Exception) {
                }
            }
        } catch (e: Exception) {
            redactarLogServidorln("EXCEPTION Packet: $packet SE INTENTA ACCEDER CON UNA CUENTA RARA")
            e.printStackTrace()
        }
        ENVIAR_ATE_TICKET_FALLIDA(this)
        cerrarSocket(true, "cuenta_Acceder_Server(5) _cuenta: $cuenta packet: $packet")
    }

    private fun cuenta_Idioma(packet: String) {
        ENVIAR_ÑA_LISTA_GFX(this)
        ENVIAR_ÑB_LISTA_NIVEL(this)
        cuenta!!.idioma = packet.substring(2)
        if (personaje == null) {
            cuenta_Regalo()
        }
    }

    private fun cuenta_Regalo() {
        val regalo = cuenta!!.regalo
        if (regalo != null) {
            if (regalo.isEmpty()) {
                return
            }
        }
        val lista = StringBuilder()
        if (regalo != null) {
            for (str in regalo.split(",".toRegex()).toTypedArray()) {
                try {
                    val efectos = Mundo.getObjetoModelo(str.toInt())?.stringStatsModelo()
                    if (lista.isNotEmpty()) {
                        lista.append(";")
                    }
                    lista.append("0~").append(str.toInt().toString(16)).append("~1~~").append(efectos)
                } catch (ignored: Exception) {
                }
            }
        }
        if (lista.isEmpty()) {
            return
        }
        ENVIAR_Ag_LISTA_REGALOS(this, 1, lista.toString())
    }

    private fun cuenta_Entregar_Regalo(packet: String) {
        try {
            val regalo = cuenta!!.regalo
            if (regalo != null) {
                if (regalo.isEmpty()) {
                    return
                }
            }
            val info = packet.split(Pattern.quote("|").toRegex()).toTypedArray()
            val idPerso = info[1].toInt()
            val idObjMod = info[0].toInt()
            val nuevo = StringBuilder()
            var listo = false
            if (regalo != null) {
                for (str in regalo.split(",".toRegex()).toTypedArray()) {
                    if (str.isEmpty()) {
                        continue
                    }
                    var idTemp = 0
                    idTemp = try {
                        str.toInt()
                    } catch (e: Exception) {
                        continue
                    }
                    if (Mundo.getObjetoModelo(idTemp) == null) {
                        continue
                    }
                    if (listo || idTemp != idObjMod) {
                        if (nuevo.isNotEmpty()) {
                            nuevo.append(",")
                        }
                        nuevo.append(str)
                    } else {
                        listo = true
                    }
                }
            }
            cuenta!!.regalo = nuevo.toString()
            if (listo) {
                Mundo.getPersonaje(idPerso)?.addObjIdentAInventario(
                    Mundo.getObjetoModelo(idObjMod)?.crearObjeto(
                        1,
                        Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO
                    )!!, false
                )
                cuenta_Regalo()
                ENVIAR_AG_SIGUIENTE_REGALO(this)
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(this)
        }
    }

    private fun cuenta_Boostear_Stat(packet: String) {
        var stat = 0
        var capital = 1
        stat = try {
            packet.substring(2).split(";".toRegex()).toTypedArray()[0].toInt()
        } catch (e: Exception) {
            ENVIAR_BN_NADA(this, "BOOSTEAR INVALIDO")
            return
        }
        try {
            capital = packet.split(";".toRegex()).toTypedArray()[1].toInt()
        } catch (ignored: Exception) {
        }
        personaje!!.boostStat2(stat, capital)
    }

    private fun cuenta_Reiniciar_Personaje(packet: String) {
        try {
            val p = cuenta!!.getPersonaje(packet.substring(2).toInt())
            if (p != null) {
                if (p.esFantasma()) {
                    p.reiniciarCero()
                }
            }
        } catch (ignored: Exception) {
        }
    }

    private fun cuenta_Seleccion_Personaje(packet: String) {
        val persoID = packet.substring(2).toInt()
        if (cuenta!!.getPersonaje(persoID) != null) {
            personaje = cuenta!!.getPersonaje(persoID)
            if (AtlantaMain.MODO_HEROICO && personaje!!.esFantasma()) {
                personaje = null
                ENVIAR_ASE_SELECCION_PERSONAJE_FALLIDA(this)
            } else {
                personaje!!.conectarse()
                this.logger = LoggerFactory.getLogger(personaje!!.nombre)
            }
        } else {
            redactarLogServidorln(
                "El personaje de ID $persoID es nulo, para la cuenta " + cuenta!!
                    .id
            )
            ENVIAR_ASE_SELECCION_PERSONAJE_FALLIDA(this)
        }
    }

    private fun cuenta_Eliminar_Personaje(packet: String) {
        try {
            val split = packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()
            val perso = cuenta!!.getPersonaje(split[0].toInt())
            var respuesta = ""
            try {
                respuesta = URLDecoder.decode(split[1], StandardCharsets.UTF_8.toString())
            } catch (ignored: Exception) {
            }
            if (perso != null) {
                if (perso.nivel < 25 || perso.nivel >= 25 && respuesta.equals(
                        cuenta!!.respuesta,
                        ignoreCase = true
                    )
                ) {
                    cuenta!!.eliminarPersonaje(perso.Id)
                    ENVIAR_ALK_LISTA_DE_PERSONAJES(this, cuenta)
                } else {
                    ENVIAR_ADE_ERROR_BORRAR_PJ(this)
                }
            } else {
                ENVIAR_ADE_ERROR_BORRAR_PJ(this)
            }
        } catch (e: Exception) {
            ENVIAR_ADE_ERROR_BORRAR_PJ(this)
        }
    }

    private fun cuenta_Crear_Personaje(packet: String) {
        try {
            val infos = packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()
            if (Mundo.getPersonajePorNombre(infos[0]) != null) {
                ENVIAR_AAE_ERROR_CREAR_PJ(this, "a")
                return
            }
            if (cuenta!!.personajes.size >= AtlantaMain.MAX_PJS_POR_CUENTA) {
                ENVIAR_AAE_ERROR_CREAR_PJ(this, "f")
                return
            }
            val nombre = Personaje.nombreValido(infos[0], false)
            if (nombre == null) {
                ENVIAR_AAE_ERROR_CREAR_PJ(personaje!!, "a")
                return
            }
            if (nombre.isEmpty()) {
                ENVIAR_AAE_ERROR_CREAR_PJ(personaje!!, "n")
                return
            }
            val claseID = infos[1].toByte()
            val sexo = infos[2].toByte()
            val color1 = infos[3].toInt()
            val color2 = infos[4].toInt()
            val color3 = infos[5].toInt()
            if (Mundo.getClase(claseID.toInt()) == null) {
                ENVIAR_AAE_ERROR_CREAR_PJ(this, "ZCLASE NO EXISTE")
                return
            }
            if (AtlantaMain.OGRINAS_CREAR_CLASE.containsKey(claseID)) {
                val ogrinas = AtlantaMain.OGRINAS_CREAR_CLASE[claseID]!!
                if (!RESTAR_OGRINAS(cuenta!!, ogrinas.toLong(), null)) {
                    ENVIAR_AAE_ERROR_CREAR_PJ(this, "Z")
                    ENVIAR_M145_MENSAJE_PANEL_INFORMACION(
                        this, AtlantaMain.MENSAJE_ERROR_OGRINAS_CREAR_CLASE + " "
                                + ogrinas
                    )
                    return
                }
            }
            val perso = cuenta!!.crearPersonaje(nombre, claseID, sexo, color1, color2, color3)
            if (perso != null) { // cambia la alineacion aletario
                if (AtlantaMain.PARAM_DAR_ALINEACION_AUTOMATICA) {
                    perso.cambiarAlineacion(
                        if (Random().nextBoolean()) Constantes.ALINEACION_BONTARIANO else Constantes.ALINEACION_BRAKMARIANO,
                        true
                    )
                }
                ENVIAR_AAK_CREACION_PJ_OK(this)
                ENVIAR_ALK_LISTA_DE_PERSONAJES(this, cuenta)
                if (AtlantaMain.PARAM_CINEMATIC_CREAR_PERSONAJE) {
                    ENVIAR_TB_CINEMA_INICIO_JUEGO(this)
                }
                if (AtlantaMain.PANEL_DESPUES_CREAR_PERSONAJE.isNotEmpty()) {
                    ENVIAR_M145_MENSAJE_PANEL_INFORMACION(this, AtlantaMain.PANEL_DESPUES_CREAR_PERSONAJE)
                }
            } else {
                ENVIAR_AAE_ERROR_CREAR_PJ(this, "Z")
            }
        } catch (e: Exception) {
            ENVIAR_AAE_ERROR_CREAR_PJ(this, "Z")
        }
    }

    private fun analizar_Atlanta(packet: String) {
        try {
            if (packet.length < 2) {
                try {
                    ENVIAR_BN_NADA(personaje)
                    personaje!!.enviarmensajeNegro("Por Favor repita su accion, El servidor ha perdido los datos en el camino")
                    redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
                } catch (e: Exception) {
                    redactarLogServidorln("Packet Loss: $packet")
                }
                return
            }
            when (packet[1]) {
                'A' -> Atlanta_Panel_Almanax()
                'b' -> {
                }
                'B' -> Atlanta_Mision_Almanax()
                'C' -> ENVIAR_ÑF_BESTIARIO_MOBS(this, personaje!!.listaCardMobs())
                'D' -> {
                    if (packet.length > 2) {
                        when (packet[2]) {
                            '6' -> {
                                comando_jugador(".mercadillo")
                            }
                            '5' -> {
                                try {
                                    if (personaje!!.pelea != null) {
                                        if (personaje!! == personaje!!.pelea.luchadorTurno!!.personaje) {
                                            personaje!!.pelea.pasarTurno(personaje!!.pelea.luchadorTurno)
                                        }
                                    } else {
                                        comando_jugador(".deblo")
                                    }
                                } catch (e: Exception) {
                                    redactarLogServidorln(e.toString())
                                }
                            }
                            '4' -> {
                                comando_jugador(".koli")
                            }
                            '3' -> {
                                comando_jugador(".finaccion")
                            }
                            '2' -> {
                                if (personaje!!.grupoParty != null) {
                                    comando_jugador(".tp ${personaje!!.grupoParty.liderGrupo!!.nombre}")
                                } else {
                                    personaje!!.enviarmensajeNegro("No estas en un grupo")
                                }
                            }
                            '1' -> {
                                comando_jugador(".maestro on")
                            }

                        }
                    }
                    ENVIAR_Ñi_CREA_TU_ITEM_PRECIOS(this)
                    ENVIAR_bb_DATA_CREAR_ITEM(personaje!!)
                }
                'E' -> {
                    val idMob = packet.substring(2).toInt()
                    if (personaje!!.tieneCardMob(idMob)) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    Mundo.getMobModelo(idMob)?.detalleMob()?.let { ENVIAR_ÑE_DETALLE_MOB(this, it) }
                }
                'e' -> Atlanta_Buscar_Mobs_Drop(packet)
                'F' -> Atlanta_Crea_Tu_Item(packet)
                'G' -> {
                }
                'h' -> Mundo.getObjetosPorTipo(personaje, packet.substring(2).toShort())
                'I' -> Atlanta_Comprar_Sistema_Recurso(packet)
                'i' -> Atlanta_Comprar_Panel_Items(packet)
                'J' -> Atlanta_Sistema_Recurso()
                'L' -> personaje?.let { Mundo.comprarLoteria(packet, it) }
                'l' -> Atlanta_Ruleta_Suerte(packet)
                'm' -> Atlanta_Mostrar_Loteria()
                'ñ' -> {
                    if (personaje!!.pelea != null) {
                        return
                    }
                    Atlanta_Panel_Ornamentos()
                }
                'Ñ' -> Atlanta_Elegir_Ornamento(packet)
                'O' -> if (personaje != null) {
                    Atlanta_Ogrinas()
                }
                'o' -> {
                    try {
                        Atlanta_Panel_Economia(packet)
                    } catch (e: Exception) {
                    }
                }
                'P' -> Atlanta_Cambiar_Nivel_Alineacion(packet)
                'q' -> Atlanta_Borrar_Reporte(packet)
                'Q' -> Atlanta_Detalle(packet)
                'r' -> Atlanta_Detalle_Reporte(packet)
                'R' -> Atlanta_Reportar(packet)
                's' -> if (personaje != null) {
                    Atlanta_Servicios(packet)
                }
                'S' -> {
                    if (packet.length == 2) {
                        try {
                            personaje!!.abrirMenuZaapiZonas()
//                            if (personaje!!.detalleExp) {
//                                personaje!!.detalleExp = false
//                                personaje!!.enviarmensajeNegro("Los detalles de XP se Desactivaron")
//                            } else {
//                                personaje!!.detalleExp = true
//                                personaje!!.enviarmensajeNegro("Los detalles de XP se Activaron")
//                            }
                            return
                        } catch (e: Exception) {
                            redactarLogServidorln(e.toString())
                        }
                    } else {
                        Atlanta_Sets_Rapidos(packet)
                    }
                }
                't' -> {
                    if (personaje!!.pelea != null) {
                        return
                    }
                    ENVIAR_bt_PANEL_TITULOS(personaje!!)
                }
                'T' -> Atlanta_Elegir_Titulo(packet)
                'V' -> Atlanta_Votar()
                'z' -> {
                    val infos = packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()
                    val buscar = if (infos.size > 1) infos[1] else ""
                    var iniciarEn = if (infos.size > 2) infos[2].toInt() else 0
                    if (iniciarEn < 0) {
                        iniciarEn = abs(iniciarEn)
                        iniciarEn -= AtlantaMain.LIMITE_LADDER
                    }
                    personaje?.let { Mundo.enviarRanking(it, infos[0], buscar.toUpperCase(), iniciarEn) }
                }
                'Z' -> ENVIAR_bL_RANKING_PERMITIDOS(personaje!!)
                'N' -> {
                    personaje!!.cambiarNombre(packet.substring(2))
                    personaje!!.colorNombre = personaje!!.color1
                    personaje!!.refrescarEnMapa()
                }
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje)
        }
    }

    fun Atlanta_Panel_Economia(packet: String) {
        val perso = personaje ?: return
        val identificador = packet[2]
        val split = packet.split(";")
        if (split.size < 2) return
        val cantidad = packet.split(";")[1].toInt()
        when (identificador) {
            'M' -> { // Simular Compra
                Economia.updateEconomia()
                Economia.comprarOgrinas(cantidad, perso, proyeccion = true, panel = true)
            }
            'I' -> { // Compra
                Economia.updateEconomia()
                Economia.comprarOgrinas(cantidad, perso, proyeccion = false, panel = true)
            }
            'J' -> { // Simular Venta
                Economia.updateEconomia()
                Economia.ventaOgrinas(cantidad, perso, proyeccion = true, panel = true)
            }
            'L' -> { // Venta
                Economia.updateEconomia()
                Economia.ventaOgrinas(cantidad, perso, proyeccion = false, panel = true)
            }
            else -> {
                return
            }
        }
    }

    private fun Atlanta_Panel_Ornamentos() {
        if (!AtlantaMain.PARAM_PERMITIR_ORNAMENTOS) {
            ENVIAR_BN_NADA(personaje, "ORNAMENTOS NO DISPONIBLES")
            return
        }
        ENVIAR_bñ_PANEL_ORNAMENTOS(personaje!!)
    }

    private fun Atlanta_Elegir_Ornamento(packet: String) {
        try {
            if (personaje!!.pelea != null) {
                return
            }
            if (!AtlantaMain.PARAM_PERMITIR_ORNAMENTOS) {
                ENVIAR_BN_NADA(personaje, "ORNAMENTOS NO DISPONIBLES")
                return
            }
            val ornamentoID = packet.substring(2).toInt()
            if (ornamentoID == 0) {
                personaje!!.ornamento = ornamentoID
            } else {
                val ornamento = Mundo.getOrnamento(ornamentoID)
                if (ornamento != null) {
                    if (ornamento.adquirirOrnamento(personaje!!)) {
                        personaje!!.addOrnamento(ornamentoID)
                        personaje!!.ornamento = ornamentoID
                    }
                }
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje)
        }
    }

    private fun Atlanta_Elegir_Titulo(packet: String) {
        try {
            if (personaje!!.pelea != null) {
                return
            }
            val a = packet.substring(2).split(";".toRegex()).toTypedArray()
            val tituloID = a[0].toInt()
            val color = a[1].toInt()
            if (tituloID == 0) {
                personaje!!.addTitulo(0, -1)
            } else {
                val titulo = Mundo.getTitulo(tituloID)
                if (titulo != null) {
                    if (titulo.adquirirTitulo(personaje!!)) {
                        personaje!!.addTitulo(tituloID, color)
                    }
                }
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje)
        }
    }

    private fun Atlanta_Buscar_Mobs_Drop(packet: String) {
        val str = StringBuilder()
        val mobs = ArrayList<Int>()
        for (s in packet.substring(2).split(",".toRegex()).toTypedArray()) {
            if (s.isEmpty()) {
                continue
            }
            val objMod = Mundo.getObjetoModelo(s.toInt()) ?: continue
            for (idMob in objMod.mobsQueDropean) {
                if (personaje!!.tieneCardMob(idMob)) {
                    continue
                }
                if (!mobs.contains(idMob)) {
                    mobs.add(idMob)
                }
            }
        }
        for (idMob in mobs) {
            if (str.isNotEmpty()) {
                str.append(",")
            }
            str.append(idMob)
        }
        ENVIAR_Ñf_BESTIARIO_DROPS(this, str.toString())
    }

    private fun Atlanta_Mision_Almanax() {
        personaje!!.cumplirMisionAlmanax()
    }

    private fun Atlanta_Ruleta_Suerte(packet: String) {
        try {
            when (packet[2]) {
                'P' -> {
                }
                'G' -> {
                    val ficha = packet.substring(3).toInt()
                    val index = getRandomInt(0, 7)
                    val premios = Mundo.RULETA[ficha]
                    if (premios == null || premios.isEmpty()) {
                        ENVIAR_BN_NADA(personaje, "RULETA NO TIENE PREMIOS")
                        return
                    }
                    val premio = premios.split(",".toRegex()).toTypedArray()[index].toInt()
                    val objMod = Mundo.getObjetoModelo(premio)
                    if (objMod == null) {
                        ENVIAR_BN_NADA(personaje, "RULETA PREMIO NO EXISTE")
                        return
                    }
                    if (!personaje!!.restarCantObjOEliminar(ficha, 1, true)) {
                        ENVIAR_BN_NADA(personaje, "RULETA NO TIENE FICHA")
                        return
                    }
                    ENVIAR_brG_RULETA_GANADOR(this, index)
                    Thread.sleep(3000)
                    personaje!!.addObjIdentAInventario(
                        objMod.crearObjeto(
                            1, Constantes.OBJETO_POS_NO_EQUIPADO,
                            CAPACIDAD_STATS.RANDOM
                        ), false
                    )
                    ENVIAR_Im_INFORMACION(personaje!!, "021;1~$premio")
                }
            }
        } catch (e: Exception) {
            redactarLogServidorln("EXCEPTION Packet $packet, Atlanta_Ruleta_Suerte $e")
            e.printStackTrace()
        }
    }

    private fun Atlanta_Servicios(packet: String) {
        var packet = packet
        try {
            var servicio: Servicio? = null
            when (packet[2].toUpperCase()) {
                'C' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_COLOR)
                'G' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_EMBLEMA)
                'M' -> servicio = Mundo.getServicio(Constantes.SERVICIO_MIMOBIONTE)
                'm' -> servicio = Mundo.getServicio(Constantes.SERVICIO_TRANSFORMAR_MONTURA)
                'N' -> servicio = Mundo.getServicio(Constantes.SERVICIO_CAMBIO_NOMBRE)
                'T' -> servicio = Mundo.getServicio(Constantes.SERVICIO_TITULO_PERSONALIZADO)
                '5' -> {
                    if (packet[3].toString().equals(";", ignoreCase = true)) {
                        personaje?.let {
                            Mundo.getServicio(Constantes.SERVICIO_TITULO_PERSONALIZADO)?.usarServicio(it, "")
                        }
                    } else {
                        comando_jugador(".infos")
                        return
                    }
                }
                else -> {
                    val arg = packet.split(";".toRegex()).toTypedArray()
                    try {
                        if (arg.size > 1) {
                            personaje?.setMedioPagoServicio(arg[1].toByte())
                        }
                    } catch (ignored: Exception) {
                    }
                    servicio = Mundo.getServicio(arg[0].substring(2).toInt())
                    packet = ""
                }
            }
            servicio?.usarServicio(personaje!!, packet)
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje, "EXCEPTION Atlanta SERVICIOS")
            redactarLogServidorln("EXCEPTION Packet $packet, Atlanta_Servicios $e")
            e.printStackTrace()
        }
    }

    private fun Atlanta_Sets_Rapidos(packet: String) {
        try {
            when (packet[2]) {
                'B' -> {
                    personaje!!.borrarSetRapido(packet.substring(3).toInt())
                    ENVIAR_BN_NADA(personaje, "SET RAPIDO BORRADO")
                }
                'C' -> {
                    val split = packet.substring(3).split(Pattern.quote("|").toRegex()).toTypedArray()
                    val id = split[0].toInt()
                    var nombre = split[1]
                    if (nombre.length > 20) {
                        nombre = nombre.substring(0, 20)
                    }
                    val plantilla = (Encriptador.NUMEROS + Encriptador.ABC_MIN + Encriptador.ABC_MAY
                            + Encriptador.ESPACIO)
                    for (letra in nombre.toCharArray()) {
                        if (!plantilla.contains(letra.toString() + "")) {
                            nombre = nombre.replace(letra.toString() + "", "")
                        }
                    }
                    val icono = split[2].toInt()
                    val data = objeto_String_Set_Equipado()
                    personaje!!.addSetRapido(id, nombre, icono, data)
                    ENVIAR_BN_NADA(personaje, "SET RAPIDO CREADO")
                }
                'U' -> {
                    val set = personaje!!.getSetRapido(packet.substring(3).toInt()) ?: return
                    var cambio = objeto_Desequipar_Set()
                    if (cambio >= 1) {
                        personaje!!.actualizarObjEquipStats()
                    }
                    //					Thread.sleep(200);
                    cambio = max(cambio, objeto_Equipar_Set(set))
                    if (cambio >= 1) {
                        personaje!!.refrescarStuff(true, true, cambio >= 2)
                    }
                }
            }
        } catch (e: Exception) {
            redactarLogServidorln("EXCEPTION Packet $packet, Atlanta_Sets_Rapidos $e")
            e.printStackTrace()
        }
    }

    private fun Atlanta_Cambiar_Nivel_Alineacion(packet: String) {
        if (personaje!!.estaDisponible(true, true)) {
            ENVIAR_BN_NADA(personaje, "NO DISPONIBLE")
            return
        }
        if (AtlantaMain.NIVEL_MAX_ESCOGER_NIVEL <= 1) {
            ENVIAR_BN_NADA(personaje, "MAX ESCOGER NIVEL ES 1")
            return
        }
        try {
            val split = packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()
            val nivel = split[0].toInt()
            val alineacion = split[1].toByte()
            if (AtlantaMain.MODO_PVP) {
                personaje!!.cambiarNivelYAlineacion(nivel, alineacion)
            } else {
                Mundo.getServicio(Constantes.SERVICIO_ESCOGER_NIVEL)?.usarServicio(personaje!!, packet)
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje)
        }
    }

    private fun Atlanta_Panel_Almanax() {
        if (!AtlantaMain.PARAM_ALMANAX) {
            ENVIAR_BN_NADA(personaje, "ALMANAX NO DISPONIBLE")
            return
        }
        val almanax = Mundo.almanaxDelDia
        if (almanax == null) {
            ENVIAR_Im_INFORMACION(personaje!!, "1ALMANAX_NO_HAY_MISION")
            return
        }
        try {
            val cal = Calendar.getInstance()
            ENVIAR_ÑX_PANEL_ALMANAX(
                this,
                cal[Calendar.YEAR].toString() + "|" + cal[Calendar.MONTH] + "|" + cal[Calendar.DAY_OF_MONTH] + "|" + almanax.ofrenda._primero + "," + almanax.ofrenda._segundo + "|" + almanax
                    .tipo + "," + almanax.bonus + "|" + personaje!!.cantMisionseAlmanax() + ","
                        + AtlantaMain.MAX_MISIONES_ALMANAX + "|" + if (personaje!!.realizoMisionDelDia()) 1 else 0
            )
        } catch (ignored: Exception) {
        }
    }

    private fun Atlanta_Borrar_Reporte(packet: String) {
        if (DELETE_REPORTE((packet[2].toString() + "").toByte(), packet.substring(3).toInt())) {
            ENVIAR_Im_INFORMACION(personaje!!, "1REPORTE_BORRADO_OK")
        } else {
            ENVIAR_Im_INFORMACION(personaje!!, "1REPORTE_BORRADO_ERROR")
        }
    }

    private fun Atlanta_Detalle_Reporte(packet: String) {
        when (packet[2]) {
            '0', '1', '2', '3' -> {
                val arg = packet.substring(3).split(";".toRegex()).toTypedArray()
                val tipo = (packet[2].toString() + "").toByte()
                val idReporte = arg[0].toInt()
                ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, GET_DESCRIPTION_REPORTE(tipo, idReporte))
                cuenta!!.addIDReporte(tipo, idReporte)
            }
            else -> {
                ENVIAR_BN_NADA(personaje)
                return
            }
        }
    }

    private fun Atlanta_Reportar(packet: String) {
        if (System.currentTimeMillis() - cuenta!!.ultimoReporte < 300000) {
            ENVIAR_Im_INFORMACION(personaje!!, "1REPORTE_ESPERAR_ENVIAR_OTRO")
            return
        }
        val packet2 = Constantes.filtro(packet)
        val tema = packet2.substring(4).split(Pattern.quote("|").toRegex()).toTypedArray()[1]
        val detalle = packet2.substring(4).split(Pattern.quote("|").toRegex()).toTypedArray()[2]
        when (packet[3]) {
            '1' -> INSERT_REPORTE_BUG(personaje!!.nombre, tema, detalle)
            '2' -> INSERT_SUGERENCIAS(personaje!!.nombre, tema, detalle)
            '3' -> INSERT_DENUNCIAS(personaje!!.nombre, tema, detalle)
            '4' -> INSERT_PROBLEMA_OGRINAS(personaje!!.nombre, tema, detalle)
            else -> {
                ENVIAR_Im_INFORMACION(personaje!!, "1REPORTE_ENVIADO_ERROR")
                return
            }
        }
        cuenta!!.ultimoReporte = System.currentTimeMillis()
        ENVIAR_Im_INFORMACION(personaje!!, "1REPORTE_ENVIADO_OK")
    }

    private fun Atlanta_Mostrar_Loteria() {
        if (!AtlantaMain.PARAM_LOTERIA_OGRINAS) {
            var precioL = AtlantaMain.PRECIO_LOTERIA / 1000000.toFloat()
            var millonesPrecio = true
            if (precioL <= 0) {
                precioL = AtlantaMain.PRECIO_LOTERIA / 1000.toFloat()
                millonesPrecio = false
            }
            var premioL = AtlantaMain.PREMIO_LOTERIA / 1000000.toFloat()
            var millonesPremio = true
            if (premioL <= 0) {
                premioL = AtlantaMain.PREMIO_LOTERIA / 1000.toFloat()
                millonesPremio = false
            }
            ENVIAR_bT_PANEL_LOTERIA(
                personaje!!,
                ((if (precioL % 1F > 0F) precioL else precioL).toString() + "") + (if (millonesPrecio) "M" else "K") + ";" + ((if (premioL % 1 > 0) premioL else premioL).toString() + "") + if (millonesPremio) "M" else "K"
            )
        } else {
            ENVIAR_bT_PANEL_LOTERIA(
                personaje!!,
                AtlantaMain.PRECIO_LOTERIA.toString() + ";" + AtlantaMain.PREMIO_LOTERIA.toString()
            )
        }
    }

    private fun Atlanta_Votar() {
        if (AtlantaMain.OGRINAS_POR_VOTO < 0) {
            ENVIAR_BN_NADA(personaje, "VOTAR NO OGRINAS")
            return
        }
        if (personaje!!.pelea != null) {
            _votarDespuesPelea = true
            ENVIAR_BN_NADA(personaje, "VOTAR PELEA")
            return
        }
        val tiempoRestante = cuenta!!.tiempoRestanteParaVotar()
        var tiempoAparecer = AtlantaMain.MINUTOS_SPAMEAR_BOTON_VOTO
        if (tiempoRestante > 0) {
            tiempoAparecer = tiempoRestante
        }
        if (AtlantaMain.OGRINAS_POR_VOTO > 0) {
            if (cuenta!!.puedeVotar()) {
                cuenta!!.darOgrinasPorVoto()
            }
        }
        for (ep in ServidorServer.clientes) {
            if (ep === this) {
                continue
            }
            if (ep.personaje == null) {
                continue
            }
            if (ep.actualIP == actualIP) {
                if (tiempoRestante <= 0 && ep.personaje!!.pelea != null) {
                    continue
                }
                ENVIAR_bP_VOTO_RPG_PARADIZE(ep.personaje!!, tiempoAparecer, false)
            }
        }
        ENVIAR_bP_VOTO_RPG_PARADIZE(personaje!!, tiempoAparecer, tiempoRestante <= 0)
    }

    // private void Atlanta_Mostrar_Boton_Voto() {
// if (Bustemu.OGRINAS_POR_VOTO < 0) {
// GestorSalida.ENVIAR_BN_NADA(this);;
// return;
// }
// if (Bustemu.OGRINAS_POR_VOTO > 0) {
// if (_cuenta.puedeVotar()) {
// _cuenta.darOgrinasPorVoto();
// }
// }
// int tiempo = _cuenta.tiempoRestanteParaVotar();
// for (EntradaPersonaje ep : ServidorPersonaje.getClientes()) {
// if (ep == Bustemu.CONECTOR)
// continue;
// if (ep == this) {
// continue;
// }
// if (ep.getActualIP().equals(_actualIP)) {
// GestorSalida.ENVIAR_bP_VOTO_RPG_PARADIZE(ep._perso, tiempo, false);
// }
// }
// GestorSalida.ENVIAR_bP_VOTO_RPG_PARADIZE(_perso, tiempo, false);
// }
    private fun Atlanta_Comprar_Panel_Items(packet: String) {
        val r = packet.substring(2).split(";".toRegex()).toTypedArray()
        var idObjMod = -1
        var cantidad = -1
        var capStas = CAPACIDAD_STATS.RANDOM
        try {
            idObjMod = r[0].toInt()
            cantidad = r[1].toInt()
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje, "COMPRAR SISTEMA ITEM $packet")
            return
        }
        var exoPA = false
        var exoPM = false
        try {
            capStas = if (r[2] == "1") CAPACIDAD_STATS.MAXIMO else CAPACIDAD_STATS.RANDOM
        } catch (ignored: Exception) {
        }
        try {
            exoPA = r[3] == "1"
        } catch (ignored: Exception) {
        }
        try {
            exoPM = r[4] == "1"
        } catch (ignored: Exception) {
        }
        val objMod = Mundo.getObjetoModelo(idObjMod)
        if (objMod == null) {
            ENVIAR_Im_INFORMACION(personaje!!, "1OBJECT_DONT_EXIST")
            return
        }
        if (AtlantaMain.SISTEMA_ITEMS_TIPO_DE_PAGO == "KAMAS") {
            if (objMod.precioPanelKamas <= 0) {
                ENVIAR_Im_INFORMACION(personaje!!, "1ERROR_BUY_ITEM")
                return
            }
        } else {
            if (objMod.precioPanelOgrinas <= 0) {
                ENVIAR_Im_INFORMACION(personaje!!, "1ERROR_BUY_ITEM")
                return
            }
        }
        if (cantidad < 1) {
            cantidad = 1
        }
        var precio =
            if (AtlantaMain.SISTEMA_ITEMS_TIPO_DE_PAGO == "KAMAS") objMod.precioPanelKamas else objMod.precioPanelOgrinas
        if (valorValido(cantidad, precio)) {
            ENVIAR_BN_NADA(personaje, "INTENTO BUG MULTIPLICADOR")
            return
        }
        precio *= cantidad
        if (!AtlantaMain.PARAM_SISTEMA_ITEMS_SOLO_PERFECTO) {
            if (!AtlantaMain.SISTEMA_ITEMS_EXO_TIPOS_NO_PERMITIDOS.contains(objMod.tipo)) {
                if (exoPA) {
                    precio += AtlantaMain.SISTEMA_ITEMS_EXO_PA_PRECIO.toInt()
                } else if (exoPM) {
                    precio += AtlantaMain.SISTEMA_ITEMS_EXO_PM_PRECIO.toInt()
                }
            } else {
                exoPA = false
                exoPM = false
            }
            capStas = CAPACIDAD_STATS.MAXIMO
        } else {
            exoPA = false
            exoPM = false
            if (capStas === CAPACIDAD_STATS.MAXIMO) {
                precio *= AtlantaMain.SISTEMA_ITEMS_PERFECTO_MULTIPLICA_POR.toInt()
            }
        }
        if (precio <= 0) {
            ENVIAR_BN_NADA(personaje)
            return
        }
        if (AtlantaMain.SISTEMA_ITEMS_TIPO_DE_PAGO == "KAMAS") {
            if (personaje!!.kamas >= precio) {
                personaje!!.addKamas(-precio.toLong(), true, true)
            } else {
                ENVIAR_Im_INFORMACION(personaje!!, "1128;$precio")
                return
            }
        } else {
            if (!RESTAR_OGRINAS(cuenta!!, precio.toLong(), personaje)) {
                return
            }
        }
        val nuevo = objMod.crearObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO, capStas)
        if (exoPA) {
            nuevo.fijarStatValor(Constantes.STAT_MAS_PA, 1)
        }
        if (exoPM) {
            nuevo.fijarStatValor(Constantes.STAT_MAS_PM, 1)
        }
        if (AtlantaMain.DIAS_INTERCAMBIO_COMPRAR_SISTEMA_ITEMS > 0) {
            nuevo.addStatTexto(
                Constantes.STAT_INTERCAMBIABLE_DESDE, stringFechaIntercambiable(
                    AtlantaMain.DIAS_INTERCAMBIO_COMPRAR_SISTEMA_ITEMS
                )
            )
        }
        if (AtlantaMain.PARAM_OBJETOS_OGRINAS_LIGADO) {
            nuevo.addStatTexto(Constantes.STAT_LIGADO_A_CUENTA, "0#0#0#" + personaje!!.nombre)
        }
        personaje!!.addObjIdentAInventario(nuevo, false)
        ENVIAR_Ow_PODS_DEL_PJ(personaje!!)
        ENVIAR_Im_INFORMACION(personaje!!, "021;$cantidad~$idObjMod")
    }

    private fun Atlanta_Sistema_Recurso() {
        if (AtlantaMain.PRECIO_SISTEMA_RECURSO <= 0) {
            ENVIAR_BN_NADA(personaje, "SISTEMA RECURSO DESHABILITADO")
            return
        }
        val str = StringBuilder()
        for (tipo in AtlantaMain.TIPO_RECURSOS) {
            if (str.isNotEmpty()) {
                str.append(";")
            }
            str.append(tipo.toInt())
        }
        val str2 = StringBuilder()
        for (objNo in AtlantaMain.OBJ_NO_PERMITIDOS) {
            if (str2.isNotEmpty()) {
                str2.append(";")
            }
            str2.append(objNo)
        }
        ENVIAR_bI_SISTEMA_RECURSO(
            personaje!!, AtlantaMain.PRECIO_SISTEMA_RECURSO.toString() + "|" + str.toString() + "|"
                    + str2.toString() + "|" + if (AtlantaMain.PARAM_PRECIO_RECURSOS_EN_OGRINAS) 1 else 0
        )
    }

    private fun Atlanta_Comprar_Sistema_Recurso(packet: String) {
        if (AtlantaMain.PRECIO_SISTEMA_RECURSO <= 0) {
            ENVIAR_BN_NADA(personaje, "COMPRAR SISTEMA RECURSO DESHABILITADO")
            return
        }
        val r = packet.substring(2).split(";".toRegex()).toTypedArray()
        var idObjMod = -1
        var cantidad = -1
        try {
            idObjMod = r[0].toInt()
            cantidad = r[1].toInt()
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje, "COMPRAR SISTEMA RECURSO $packet")
            return
        }
        val objMod = Mundo.getObjetoModelo(idObjMod)
        if (objMod == null) {
            ENVIAR_Im_INFORMACION(personaje!!, "1OBJECT_DONT_EXIST")
            return
        }
        if (!AtlantaMain.TIPO_RECURSOS.contains(objMod.tipo) || AtlantaMain.OBJ_NO_PERMITIDOS.contains(idObjMod)
            || objMod.ogrinas > 0
        ) {
            ENVIAR_Im_INFORMACION(personaje!!, "1ERROR_BUY_RECURSE")
            return
        }
        val precioInt = (AtlantaMain.PRECIO_SISTEMA_RECURSO * objMod.nivel * objMod.nivel.toDouble().pow(0.5)).toFloat()
        if (cantidad < 1) {
            cantidad = 1
        }
        if (valorValido(cantidad, precioInt.toInt())) {
            ENVIAR_BN_NADA(personaje, "INTENTO BUG MULTIPLICADOR")
            return
        }
        var precio = 0
        try {
            precio = ceil(precioInt * cantidad.toDouble()).toInt()
            if (!AtlantaMain.PARAM_PRECIO_RECURSOS_EN_OGRINAS) {
                precio = max(objMod.kamas * cantidad, precio)
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje, "COMPRAR SISTEM RECURSO PRECIO INVALIDO")
            return
        }
        if (precio <= 0) {
            ENVIAR_BN_NADA(personaje, "COMPRAR SISTEMA RECURSO PRECIO <= 0")
            return
        }
        if (AtlantaMain.PARAM_PRECIO_RECURSOS_EN_OGRINAS) {
            if (!RESTAR_OGRINAS(cuenta!!, precio.toLong(), personaje)) {
                return
            }
        } else {
            if (personaje!!.kamas >= precio) {
                personaje!!.addKamas(-precio.toLong(), true, true)
            } else {
                ENVIAR_Im_INFORMACION(personaje!!, "1128;$precio")
                return
            }
        }
        personaje!!.addObjIdentAInventario(
            objMod.crearObjeto(
                cantidad, Constantes.OBJETO_POS_NO_EQUIPADO,
                CAPACIDAD_STATS.RANDOM
            ), false
        )
        ENVIAR_Ow_PODS_DEL_PJ(personaje!!)
        ENVIAR_Im_INFORMACION(personaje!!, "021;$cantidad~$idObjMod")
    }

    private fun Atlanta_Crea_Tu_Item(packet: String) {
        var error = 0
        try {
            error = 1
            if (!Mundo.getServicio(Constantes.SERVICIO_CREA_TU_ITEM)?.estaActivo()!!) {
                ENVIAR_BN_NADA(personaje, "CREA TU ITEM DESHABILITADO")
                return
            }
            error = 2
            val split = packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()
            val nombre = split[0]
            val idModelo = split[1].toInt()
            val gfx = split[2].toInt()
            val aStats = split[3].split(";".toRegex()).toTypedArray()
            val firma = split[4] == "1"
            val crea = Mundo.getCreaTuItem(idModelo)
            val objMod = Mundo.getObjetoModelo(idModelo)
            error = 3
            if (objMod == null) {
                ENVIAR_Im_INFORMACION(personaje!!, "1CREA_TU_ITEM_ERROR_OBJ_MODELO")
                return
            }
            error = 4
            if (crea == null) {
                ENVIAR_Im_INFORMACION(personaje!!, "1CREA_TU_ITEM_ERROR_CREAR_MODELO")
                return
            }
            val tipo = objMod.tipo.toInt()
            error = 5
            if (Constantes.GFXS_CREA_TU_ITEM[tipo] == null) {
                ENVIAR_Im_INFORMACION(personaje!!, "1CREA_TU_ITEM_ERROR_GFX_TIPO")
                return
            }
            error = 6
            if (!Constantes.GFXS_CREA_TU_ITEM[tipo]!!.contains(gfx)) {
                ENVIAR_Im_INFORMACION(personaje!!, "1CREA_TU_ITEM_ERROR_GFX_CONTAIN")
                return
            }
            var ogrinas = crea.precioBase
            if (firma) {
                ogrinas += 10
            }
            error = 7
            val plantilla = Encriptador.NUMEROS + Encriptador.ABC_MIN + Encriptador.ABC_MAY + Encriptador.ESPACIO
            var paso = true
            for (letra in nombre.toCharArray()) {
                if (!plantilla.contains(letra.toString() + "")) {
                    paso = false
                    break
                }
            }
            if (nombre.length < 4 || nombre.length > 30) {
                paso = false
            }
            error = 8
            if (!paso) {
                ENVIAR_Im_INFORMACION(personaje!!, "1CREA_TU_ITEM_ERROR_NOMBRE")
                return
            }
            error = 9
            val stats = StringBuilder(
                Integer.toHexString(Constantes.STAT_COLOR_NOMBRE_OBJETO) + "#3#0#0"
                        + "," + Integer.toHexString(Constantes.STAT_CAMBIAR_GFX_OBJETO) + "#0#0#" + Integer.toHexString(
                    gfx
                ) + ","
                        + Integer.toHexString(Constantes.STAT_CAMBIAR_NOMBRE_OBJETO) + "#0#0#0#" + nombre
            )
            val ids = ArrayList<Int>()
            for (e in aStats) {
                try {
                    val statID = e.split(",".toRegex()).toTypedArray()[0].toInt()
                    if (statID <= 0 || ids.contains(statID)) {
                        continue
                    }
                    val cantidad =
                        max(1, min(e.split(",".toRegex()).toTypedArray()[1].toInt(), crea.getMaximoStat(statID)))
                    if (CreaTuItem.PRECIOS[statID] == null) {
                        continue
                    }
                    ogrinas += ceil(CreaTuItem.PRECIOS[statID]!! * cantidad.toDouble()).toInt()
                    if (stats.isNotEmpty()) {
                        stats.append(",")
                    }
                    stats.append(Integer.toHexString(statID)).append("#").append(Integer.toHexString(cantidad))
                        .append("#0#0#0d0+").append(cantidad)
                    ids.add(statID)
                    if (ids.size >= 9) {
                        break
                    }
                } catch (ignored: Exception) {
                }
            }
            error = 10
            val maximo = crea.maxOgrinas
            if (ogrinas > maximo) {
                ENVIAR_Im_INFORMACION(personaje!!, "1CREA_TU_ITEM_ERROR_MAXIMO_OGRINAS")
                return
            }
            error = 11
            if (firma) {
                if (stats.isNotEmpty()) {
                    stats.append(",")
                }
                stats.append(Integer.toHexString(Constantes.STAT_FACBRICADO_POR)).append("#0#0#0#")
                    .append(personaje!!.nombre)
            }
            error = 12
            if (RESTAR_OGRINAS(cuenta!!, ogrinas.toLong(), personaje)) {
                error = 13
                val nuevo = Mundo.getObjetoModelo(idModelo)?.crearObjeto(
                    1, Constantes.OBJETO_POS_NO_EQUIPADO,
                    CAPACIDAD_STATS.MAXIMO
                )
                error = 14
                if (AtlantaMain.PARAM_OBJETOS_OGRINAS_LIGADO) {
                    if (nuevo != null) {
                        nuevo.addStatTexto(Constantes.STAT_LIGADO_A_CUENTA, "0#0#0#" + personaje!!.nombre)
                    }
                }
                error = 15
                if (nuevo != null) {
                    nuevo.convertirStringAStats(stats.toString())
                }
                error = 16
                personaje!!.addObjIdentAInventario(nuevo, false)
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje, "CREART TU ITEM EXCEPTION - $error")
        }
    }

    private fun Atlanta_Ogrinas() {
        try {
            personaje!!.setMedioPagoServicio(0.toByte())
            ENVIAR_bOC_ABRIR_PANEL_SERVICIOS(
                personaje!!,
                GET_CREDITOS_CUENTA(cuenta!!.id),
                GET_OGRINAS_CUENTA(cuenta!!.id)
            )
        } catch (ignored: Exception) {
        }
    }

    private fun analizar_Documentos(packet: String) {
        if (packet[1] == 'V') {
            ENVIAR_dV_CERRAR_DOCUMENTO(personaje!!)
        } else {
            redactarLogServidorln("$stringDesconocido ANALIZAR DOCUMENTOS: $packet")
            if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                cerrarSocket(true, "analizar_Documentos")
            }
        }
    }

    private fun analizar_Tutoriales(packet: String) {
        val param = packet.split(Pattern.quote("|").toRegex()).toTypedArray()
        val tuto = personaje!!.tutorial
        if (tuto == null) {
            ENVIAR_BN_NADA(personaje, "TUTORIAL NULO")
            return
        }
        if (packet.length < 2) {
            try {
                ENVIAR_BN_NADA(personaje)
                personaje!!.enviarmensajeNegro("Por Favor repita su accion, El servidor ha perdido los datos en el camino")
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        personaje!!.tutorial = null
        if (packet[1] == 'V') {
            if (packet[2] != '0' && packet[2] != '4') {
                try {
                    if (System.currentTimeMillis() - personaje!!.inicioTutorial > 13000) {
                        val recompensa = (packet[2].toString() + "").toInt() - 1
                        tuto.recompensa[recompensa]!!.realizarAccion(personaje!!, null, -1, (-1).toShort())
                    }
                } catch (e: Exception) {
                    redactarLogServidorln("Se quizo usar un tutorial con $packet")
                }
            }
            if (tuto.fin != null) {
                tuto.fin!!.realizarAccion(personaje!!, null, -1, (-1).toShort())
            }
            try {
                if (param.size > 2) {
                    val orientacion = param[2].toByte()
                    val celdaID = param[1].toShort()
                    personaje!!.orientacion = orientacion
                    personaje!!.celda = personaje!!.mapa.getCelda(celdaID)
                }
            } catch (ignored: Exception) {
            }
            personaje!!.setOcupado(false)
            ENVIAR_BN_NADA(personaje)
        } else {
            redactarLogServidorln("$stringDesconocido ANALIZAR TUTORIALES: $packet")
            if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                cerrarSocket(true, "analizar_Tutoriales()")
            }
        }
    }

    private fun analizar_Misiones(packet: String) {
        if (packet.length < 2) {
            try {
                ENVIAR_BN_NADA(personaje)
                personaje!!.enviarmensajeNegro("Por Favor repita su accion, El servidor ha perdido los datos en el camino")
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        when (packet[1]) {
            'L' -> ENVIAR_QL_LISTA_MISIONES(personaje!!, personaje!!.listaMisiones())
            'S' -> {
                var misionID = -1
                try {
                    misionID = packet.substring(2).toInt()
                } catch (ignored: Exception) {
                }
                if (misionID <= 0) {
                    ENVIAR_BN_NADA(personaje)
                }
                ENVIAR_QS_PASOS_RECOMPENSA_MISION(personaje!!, personaje!!.detalleMision(misionID))
            }
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR MISIONES: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "analizar_Misiones()")
                }
            }
        }
    }

    private fun analizar_Conquista(packet: String) {
        if (packet.length < 2) {
            try {
                ENVIAR_BN_NADA(personaje)
                personaje!!.enviarmensajeNegro("Por Favor repita su accion, El servidor ha perdido los datos en el camino")
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        when (packet[1]) {
            'b' -> conquista_Balance()
            'B' -> conquista_Bonus()
            'W' -> conquista_Geoposicion(packet)
            'I' -> conquista_Defensa(packet)
            'F' -> conquista_Unirse_Defensa_Prisma(packet)
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR CONQUISTA: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "analizar_Conquista()")
                }
            }
        }
    }

    private fun conquista_Balance() {
        val balanceMundo = personaje?.let { Mundo.getBalanceMundo(it) }
        val balanceArea = personaje?.let { Mundo.getBalanceArea(it) }
        ENVIAR_Cb_BALANCE_CONQUISTA(personaje!!, "$balanceMundo;$balanceArea")
    }

    private fun conquista_Bonus() {
        val balanceMundo = personaje?.let { Mundo.getBalanceMundo(it) }
        val bonusExp = personaje?.let { Mundo.getBonusAlinExp(it) }
        val bonusRecolecta = personaje?.let { Mundo.getBonusAlinRecolecta(it) }
        val bonusDrop = personaje?.let { Mundo.getBonusAlinDrop(it) }
        ENVIAR_CB_BONUS_CONQUISTA(
            personaje!!, balanceMundo.toString() + "," + balanceMundo + "," + balanceMundo + ";"
                    + bonusExp + "," + bonusRecolecta + "," + bonusDrop
        )
    }

    private fun conquista_Defensa(packet: String) {
        try {
            if (personaje!!.alineacion != Constantes.ALINEACION_BONTARIANO && personaje!!
                    .alineacion != Constantes.ALINEACION_BRAKMARIANO
            ) {
                ENVIAR_BN_NADA(personaje)
                return
            }
            when (packet[2]) {
                'J' -> {
                    val prisma = personaje!!.mapa.subArea!!.prisma
                    if (prisma != null && prisma.pelea != null) {
                        prisma.actualizarAtacantesDefensores()
                    }
                    ENVIAR_CIJ_INFO_UNIRSE_PRISMA(
                        personaje!!,
                        if (prisma == null) "-3" else prisma.analizarPrismas(personaje!!.alineacion)
                    )
                }
                'V' -> ENVIAR_CIV_CERRAR_INFO_CONQUISTA(personaje!!)
                else -> {
                }
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje)
        }
    }

    private fun conquista_Geoposicion(packet: String) {
        when (packet[2]) {
            'J' -> ENVIAR_CW_INFO_MUNDO_CONQUISTA(
                personaje!!,
                Mundo.prismasGeoposicion(personaje!!.alineacion.toInt())
            )
            'V' -> ENVIAR_CIV_CERRAR_INFO_CONQUISTA(personaje!!)
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR CONQUISTA GEOPOSICION: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "conquista_Geoposicion()")
                }
            }
        }
    }

    private fun conquista_Unirse_Defensa_Prisma(packet: String) {
        if (packet[2] == 'J') {
            val prisma = personaje!!.mapa.subArea!!.prisma
            if (prisma == null || prisma.pelea == null || personaje!!.pelea != null || prisma.Alineacion != personaje!!
                    .alineacion
            ) {
                return
            }
            prisma.pelea!!.unirsePelea(personaje!!, prisma.id)
        } else {
            redactarLogServidorln("$stringDesconocido ANALIZAR CONQ UNIRSE DEFENSA: $packet")
            if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                cerrarSocket(true, "conquista_Unirse_Defensa_Prisma()")
            }
        }
    }

    private fun analizar_Casas(packet: String) {
        val casa = personaje!!.algunaCasa
        if (casa == null) {
            ENVIAR_BN_NADA(personaje)
            return
        }
        if (packet.length < 2) {
            try {
                ENVIAR_BN_NADA(personaje)
                personaje!!.enviarmensajeNegro("Por Favor repita su accion, El servidor ha perdido los datos en el camino")
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        when (packet[1]) {
            'B' -> casa.comprarCasa(personaje!!)
            'G' -> casa.analizarCasaGremio(personaje!!, packet.substring(2))
            'Q' -> casa.expulsar(personaje!!, packet.substring(2))
            'S' -> casa.modificarPrecioVenta(personaje!!, packet.substring(2))
            'V' -> casa.cerrarVentanaCompra(personaje!!)
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR CASAS: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "analizar_Casas()")
                }
            }
        }
    }

    private fun analizar_Koliseo(packet: String) {
        if (!AtlantaMain.PARAM_KOLISEO) {
            ENVIAR_Im_INFORMACION(personaje!!, "1KOLISEO_DESACTIVADO")
            return
        }
        try {
            if (personaje!!.pelea.tipoPelea == Constantes.PELEA_TIPO_KOLISEO.toInt()) {
                return
            }
        } catch (ignored: Exception) {
        }
        if (packet.length < 2) {
            try {
                ENVIAR_BN_NADA(personaje)
                personaje!!.enviarmensajeNegro("Por Favor repita su accion, El servidor ha perdido los datos en el camino")
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        when (packet[1]) {
            'A' -> koliseo_Aceptar_Invitacion(packet)
            'I' -> koliseo_Invitar(packet)
            'P' -> {
                if (personaje!!.pelea != null) {
                    ENVIAR_BN_NADA(personaje)
                    return
                }
                ENVIAR_kP_PANEL_KOLISEO(personaje!!)
            }
            'R' -> koliseo_Rechazar_Invitacion()
            'V' -> koliseo_Expulsar(packet)
            'Y' -> koliseo_Inscribirse()
            'Z' -> koliseo_Desinscribirse()
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR KOLISEO: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "analizar_Koliseo()")
                }
            }
        }
    }

    private fun koliseo_Inscribirse() {
        if (personaje!!.nivel < AtlantaMain.MIN_NIVEL_KOLISEO) {
            ENVIAR_Im_INFORMACION(personaje!!, "13")
            return
        }
        if (personaje!!.tiempoPenalizacionKoliseo > System.currentTimeMillis()) {
            ENVIAR_Im_INFORMACION(
                personaje!!, "1PENALIZACION_KOLISEO;" + (personaje!!.tiempoPenalizacionKoliseo
                        - System.currentTimeMillis()) / 60000
            )
            return
        }
        if (Mundo.SEGUNDOS_INICIO_KOLISEO <= 5) { // inscribirse
            ENVIAR_Im_INFORMACION(personaje!!, "1KOLISEO_INSCRIBIR_TARDE")
            return
        }
        if (Mundo.estaEnKoliseo(personaje!!.Id)) {
            ENVIAR_Im_INFORMACION(personaje!!, "1KOLISEO_INSCRIBIR_REPETIDA")
            return
        }
        Mundo.addKoliseo(personaje!!)
        ENVIAR_Im_INFORMACION(personaje!!, "1KOLISEO_INSCRIBIR_OK")
    }

    private fun koliseo_Desinscribirse() {
        if (Mundo.SEGUNDOS_INICIO_KOLISEO <= 5) { // inscribirse
            ENVIAR_Im_INFORMACION(personaje!!, "1KOLISEO_DESINSCRIBIR_TARDE")
            return
        }
        if (!Mundo.estaEnKoliseo(personaje!!.Id)) {
            ENVIAR_Im_INFORMACION(personaje!!, "1KOLISEO_DESINSCRIBIR_NO_EXISTE")
            return
        }
        if (personaje!!.grupoKoliseo != null) {
            personaje!!.grupoKoliseo.dejarGrupo(personaje!!)
        }
        Mundo.delKoliseo(personaje!!.Id)
        ENVIAR_Im_INFORMACION(personaje!!, "1KOLISEO_DESINSCRIBIR_OK")
    }

    private fun koliseo_Invitar(packet: String) {
        if (!Mundo.estaEnKoliseo(personaje!!.Id)) {
            ENVIAR_Im_INFORMACION(personaje!!, "1KOLISEO_INVITAR_TU_NO_INSCRITO")
            return
        }
        if (Mundo.SEGUNDOS_INICIO_KOLISEO <= 20) {
            ENVIAR_Im_INFORMACION(personaje!!, "1KOLISEO_INVITAR_TARDE")
            return
        }
        val nombre = packet.substring(2)
        val invitandoA = Mundo.getPersonajePorNombre(nombre)
        if (invitandoA == null || invitandoA === personaje || !invitandoA.enLinea()) {
            ENVIAR_Im_INFORMACION(personaje!!, "1211")
            return
        }
        if (!invitandoA.estaVisiblePara(personaje)) {
            ENVIAR_Im_INFORMACION(personaje!!, "1209")
            return
        }
        if (!Mundo.estaEnKoliseo(invitandoA.Id)) {
            ENVIAR_Im_INFORMACION(personaje!!, "1KOLISEO_INVITAR_EL_NO_INSCRITO")
            return
        }
        if (invitandoA.grupoKoliseo != null) {
            ENVIAR_kIE_ERROR_INVITACION_KOLISEO(personaje!!, "a$nombre")
            return
        }
        if (personaje!!.puedeInvitar() || invitandoA.puedeInvitar()) {
            ENVIAR_Im_INFORMACION(personaje!!, "1PLAYERS_IS_BUSSY")
            return
        }
        if (personaje!!.grupoKoliseo != null && personaje!!.grupoKoliseo
                .cantPjs >= AtlantaMain.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO
        ) {
            ENVIAR_kIE_ERROR_INVITACION_KOLISEO(personaje!!, "f")
            return
        }
        if (!AtlantaMain.PARAM_PERMITIR_MISMAS_CLASES_EN_KOLISEO && invitandoA.getClaseID(false) == personaje!!.getClaseID(
                false
            )
        ) {
            ENVIAR_Im_INFORMACION(personaje!!, "1KOLISEO_MISMAS_CLASES")
            return
        }
        invitandoA.setInvitador(personaje, "koliseo")
        personaje!!.setInvitandoA(invitandoA, "koliseo")
        ENVIAR_kIK_INVITAR_KOLISEO(personaje!!, personaje!!.nombre, nombre)
        ENVIAR_kIK_INVITAR_KOLISEO(invitandoA, personaje!!.nombre, nombre)
    }

    private fun koliseo_Aceptar_Invitacion(packet: String) {
        if (personaje!!.tipoInvitacion != "koliseo") {
            ENVIAR_BN_NADA(personaje)
            return
        }
        if (Mundo.SEGUNDOS_INICIO_KOLISEO <= 20) {
            ENVIAR_Im_INFORMACION(personaje!!, "1KOLISEO_GRUPO_TARDE")
            return
        }
        val invitador = personaje!!.invitador
        if (invitador == null || !invitador.enLinea()) {
            ENVIAR_Im_INFORMACION(personaje!!, "1211")
            return
        }
        var grupo = invitador.grupoKoliseo
        try {
            if (grupo == null) {
                grupo = GrupoKoliseo(invitador)
                invitador.grupoKoliseo = grupo
            } else if (grupo.cantPjs >= AtlantaMain.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO) {
                ENVIAR_kIE_ERROR_INVITACION_KOLISEO(personaje!!, "f")
                return
            }
            grupo.addPersonaje(personaje!!)
            personaje!!.grupoKoliseo = grupo
            ENVIAR_kA_ACEPTAR_INVITACION_KOLISEO(personaje!!)
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje)
        }
        invitador.setInvitandoA(null, "")
        personaje!!.setInvitador(null, "")
    }

    private fun koliseo_Rechazar_Invitacion() {
        personaje!!.rechazarKoliseo()
    }

    private fun koliseo_Expulsar(packet: String) { // usar este packet para atacar
        try {
            personaje!!.grupoKoliseo.dejarGrupo(personaje!!)
            ENVIAR_kV_DEJAR_KOLISEO(personaje!!)
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje)
        }
    }

    private fun analizar_Claves(packet: String) {
        try {
            if (packet.length < 2) {
                try {
                    ENVIAR_BN_NADA(personaje)
                    personaje!!.enviarmensajeNegro("Por Favor repita su accion, El servidor ha perdido los datos en el camino")
                    redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
                } catch (e: Exception) {
                    redactarLogServidorln("Packet Loss: $packet")
                }
                return
            }
            when (packet[1]) {
                'V' -> if (personaje!!.consultarCofre != null) {
                    personaje!!.consultarCofre.cerrarVentanaClave(personaje!!)
                } else if (personaje!!.consultarCasa != null) {
                    personaje!!.consultarCasa.cerrarVentanaClave(personaje!!)
                }
                'K' -> panel_Claves(packet)
                else -> {
                    redactarLogServidorln("$stringDesconocido ANALIZAR CLAVES: $packet")
                    if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                        redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                        cerrarSocket(true, "analizar_Claves()")
                    }
                }
            }
        } catch (ignored: Exception) {
        }
    }

    private fun panel_Claves(packet: String) {
        try {
            when (packet[2]) {
                '0' -> if (personaje!!.consultarCofre != null) {
                    personaje!!.consultarCofre.intentarAcceder(personaje!!, packet.substring(4))
                } else if (personaje!!.algunaCasa != null) {
                    personaje!!.algunaCasa.intentarAcceder(personaje!!, packet.substring(4))
                }
                '1' -> if (personaje!!.consultarCofre != null) {
                    personaje!!.consultarCofre.modificarClave(personaje!!, packet.substring(4))
                } else if (personaje!!.algunaCasa != null) {
                    personaje!!.algunaCasa.modificarClave(personaje!!, packet.substring(4))
                }
                else -> {
                    redactarLogServidorln("$stringDesconocido ANALIZAR CASA CODIGO: $packet")
                    if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                        redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                        cerrarSocket(true, "panel_Claves()")
                    }
                }
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje)
        }
    }

    private fun analizar_Enemigos(packet: String) {
        if (packet.length < 2) {
            try {
                ENVIAR_BN_NADA(personaje)
                personaje!!.enviarmensajeNegro("Por Favor repita su accion, El servidor ha perdido los datos en el camino")
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        when (packet[1]) {
            'A' -> enemigo_Agregar(packet)
            'D' -> enemigo_Borrar(packet)
            'L' -> ENVIAR_iL_LISTA_ENEMIGOS(personaje!!)
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR ENEMIGOS: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "analizar_Enemigos()")
                }
            }
        }
    }

    private fun enemigo_Agregar(packet: String) {
        var id = -1
        var nombre = ""
        when (packet[2]) {
            '%' -> {
                nombre = packet.substring(3)
                val perso = Mundo.getPersonajePorNombre(nombre)
                if (perso == null) {
                    ENVIAR_FD_BORRAR_AMIGO(personaje!!, "Ef")
                    return
                }
                id = perso.cuentaID
            }
            '*' -> {
                nombre = packet.substring(3)
                val cuenta = Mundo.getCuentaPorApodo(nombre)
                if (cuenta == null) {
                    ENVIAR_FD_BORRAR_AMIGO(personaje!!, "Ef")
                    return
                }
                id = cuenta.id
            }
            else -> {
                nombre = packet.substring(2)
                val perso2 = Mundo.getPersonajePorNombre(nombre)
                if (perso2 == null || !perso2.enLinea()) {
                    ENVIAR_FD_BORRAR_AMIGO(personaje!!, "Ef")
                    return
                }
                id = perso2.cuentaID
            }
        }
        cuenta!!.addEnemigo(nombre, id)
    }

    private fun enemigo_Borrar(packet: String) {
        var id = -1
        var nombre = ""
        when (packet[2]) {
            '%' -> {
                nombre = packet.substring(3)
                val pj = Mundo.getPersonajePorNombre(nombre)
                if (pj == null) {
                    ENVIAR_FD_BORRAR_AMIGO(personaje!!, "Ef")
                    return
                }
                id = pj.cuentaID
            }
            '*' -> {
                nombre = packet.substring(3)
                val cuenta = Mundo.getCuentaPorApodo(nombre)
                if (cuenta == null) {
                    ENVIAR_FD_BORRAR_AMIGO(personaje!!, "Ef")
                    return
                }
                id = cuenta.id
            }
            else -> {
                nombre = packet.substring(2)
                val perso = Mundo.getPersonajePorNombre(nombre)
                if (perso == null || !perso.enLinea()) {
                    ENVIAR_FD_BORRAR_AMIGO(personaje!!, "Ef")
                    return
                }
                id = perso.cuentaID
            }
        }
        cuenta!!.borrarEnemigo(id)
    }

    private fun analizar_Oficios(packet: String) {
        if (packet.length < 2) {
            try {
                ENVIAR_BN_NADA(personaje)
                personaje!!.enviarmensajeNegro("Por Favor repita su accion, El servidor ha perdido los datos en el camino")
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        if (packet[1] == 'O') {
            val infos = packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()
            val posOficio = infos[0].toByte()
            val opciones = infos[1].toInt()
            val slots = infos[2].toByte()
            val statOficio = personaje!!.statsOficios[posOficio] ?: return
            statOficio.setOpciones(opciones)
            statOficio.slotsPublico = slots
            ENVIAR_JO_OFICIO_OPCIONES(personaje!!, statOficio)
        } else {
            redactarLogServidorln("$stringDesconocido ANALIZAR OFICIOS: $packet")
            if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                cerrarSocket(true, "analizar_Oficios()")
            }
        }
    }

    private fun analizar_Zonas(packet: String) {
        if (personaje!!.pelea != null) {
            ENVIAR_BN_NADA(personaje)
            return
        }
        if (packet.length < 2) {
            ENVIAR_BN_NADA(personaje)
            try {
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        when (packet[1]) {
            'U' -> {
                try {
                    personaje!!.usarZonas(packet.substring(2).toShort())
                } catch (ignored: Exception) {
                }
                ENVIAR_zV_CERRAR_ZONAS(personaje!!)
            }
            'V' -> ENVIAR_zV_CERRAR_ZONAS(personaje!!)
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR ZONAS: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "analizar_Zonas()")
                }
            }
        }
    }

    private fun analizar_Areas(packet: String) {
        if (personaje!!.pelea != null) {
            ENVIAR_BN_NADA(personaje)
            return
        }
        if (packet.length < 2) {
            ENVIAR_BN_NADA(personaje)
            try {
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        when (packet[1]) {
            'U' -> zaap_Usar(packet)
            'u' -> zaapi_Usar(packet)
            'v' -> ENVIAR_Wv_CERRAR_ZAPPI(personaje!!)
            'V' -> ENVIAR_WV_CERRAR_ZAAP(personaje!!)
            'w' -> ENVIAR_Ww_CERRAR_PRISMA(personaje!!)
            'p' -> prisma_Usar(packet)
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR AREAS: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "analizar_Areas()")
                }
            }
        }
    }

    private fun zaap_Usar(packet: String) {
        try {
            personaje!!.usarZaap(packet.substring(2).toShort())
            if (personaje!!.esMaestro()) {
                personaje!!.grupoParty.packetSeguirLider(packet)
            }
        } catch (ignored: Exception) {
        }
    }

    private fun zaapi_Usar(packet: String) {
        try {
            personaje!!.usarZaapi(packet.substring(2).toShort())
            if (personaje!!.esMaestro()) {
                personaje!!.grupoParty.packetSeguirLider(packet)
            }
        } catch (ignored: Exception) {
        }
    }

    private fun prisma_Usar(packet: String) {
        try {
            personaje!!.usarPrisma(packet.substring(2).toShort())
            if (personaje!!.esMaestro()) {
                personaje!!.grupoParty.packetSeguirLider(packet)
            }
        } catch (ignored: Exception) {
        }
    }

    private fun analizar_Gremio(packet: String) {
        val gremio = personaje!!.gremio
        if (packet.length < 2) {
            ENVIAR_BN_NADA(personaje)
            try {
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        if (packet[1] != 'C' && packet[1] != 'V' && packet[1] != 'J') {
            if (gremio == null) {
                ENVIAR_BN_NADA(personaje)
                return
            }
        }
        when (packet[1]) {
            'B' -> gremio_Stats(packet)
            'b' -> gremio_Hechizos(packet)
            'C' -> gremio_Crear(packet)
            'f' -> {
                if (personaje!!.estaDisponible(true, true)) {
                    ENVIAR_BN_NADA(personaje)
                    return
                }
                gremio_Cercado(packet.substring(2))
            }
            'F' -> gremio_Retirar_Recaudador(packet.substring(2))
            'h' -> {
                if (personaje!!.estaDisponible(true, true)) {
                    ENVIAR_BN_NADA(personaje)
                    return
                }
                gremio_Casa(packet.substring(2))
            }
            'H' -> gremio_Poner_Recaudador()
            'I' -> gremio_Informacion(packet)
            'J' -> gremio_Invitar(packet)
            'K' -> gremio_Expulsar(packet.substring(2))
            'P' -> gremio_Promover_Rango(packet.substring(2))
            'T' -> gremio_Pelea_Recaudador(packet.substring(2))
            'V' -> gremio_Cancelar_Creacion()
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR GREMIO: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "analizar_Gremio()")
                }
            }
        }
    }

    private fun gremio_Stats(packet: String) {
        val gremio = personaje!!.gremio
        if (personaje!!.miembroGremio.puede(Constantes.G_MODIF_BOOST)) {
            ENVIAR_Im_INFORMACION(personaje!!, "1101")
            return
        }
        when (packet[2]) {
            'p' -> {
                if (gremio.capital < 1 || gremio.getStatRecolecta(176) >= 500) {
                    return
                }
                gremio.addCapital(-1)
                gremio.addStat(176, 1)
            }
            'x' -> {
                if (gremio.capital < 1 || gremio.getStatRecolecta(124) >= 400) {
                    return
                }
                gremio.addCapital(-1)
                gremio.addStat(124, 1)
            }
            'o' -> {
                if (gremio.capital < 1 || gremio.getStatRecolecta(158) >= 5000) {
                    return
                }
                gremio.addCapital(-1)
                gremio.addStat(158, 20)
            }
            'k' -> {
                if (gremio.capital < 10 || gremio.nroMaxRecau >= 50) {
                    return
                }
                gremio.addCapital(-10)
                gremio.nroMaxRecau = (gremio.nroMaxRecau + 1)
            }
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR GREMIO STATS: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "gremio_Stats()")
                }
            }
        }
        ENVIAR_gIB_GREMIO_INFO_BOOST(personaje!!, gremio.analizarRecauAGremio())
    }

    private fun gremio_Hechizos(packet: String) {
        val gremio = personaje!!.gremio
        if (gremio.capital < 5) {
            ENVIAR_BN_NADA(personaje)
            return
        }
        if (personaje!!.miembroGremio.puede(Constantes.G_MODIF_BOOST)) {
            ENVIAR_Im_INFORMACION(personaje!!, "1101")
            return
        }
        val hechizoID = packet.substring(2).toInt()
        if (gremio.boostHechizo(hechizoID)) {
            gremio.addCapital(-5)
            ENVIAR_gIB_GREMIO_INFO_BOOST(personaje!!, personaje!!.gremio.analizarRecauAGremio())
        } // probar los hechizos de recaudador porq no ataca
    }

    private fun gremio_Pelea_Recaudador(packet: String) {
        try {
            val recaudadorID = packet.substring(1).toInt()
            when (packet[0]) {
                'J' -> {
                    if (personaje!!.estaDisponible(true, true)) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    Mundo.getRecaudador(recaudadorID)?.pelea?.unirsePelea(personaje!!, recaudadorID)
                }
                'V' -> {
                    val p = Mundo.getRecaudador(recaudadorID)?.pelea
                    if (p != null) {
                        if (p.fase == Constantes.PELEA_FASE_POSICION) {
                            p.retirarsePelea(personaje!!.Id, 0, false)
                        }
                    }
                }
                else -> {
                    redactarLogServidorln("$stringDesconocido ANALIZAR GREMIO UNIRSE PELEA: $packet")
                    if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                        redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                        cerrarSocket(true, "gremio_Pelea_Recaudador()")
                    }
                }
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje)
        }
    }

    private fun gremio_Retirar_Recaudador(packet: String) {
        val recaudadorID = packet.toInt()
        val recaudador = Mundo.getRecaudador(recaudadorID)
        if (recaudador == null || recaudador.pelea != null || personaje!!.gremio == null) {
            ENVIAR_BN_NADA(personaje, "NO SE PUEDE RETIRAR")
            return
        }
        if (recaudador.gremio!!.id != personaje!!.gremio.id) {
            ENVIAR_BN_NADA(personaje, "NO ES DEL GREMIO")
            return
        }
        if (personaje!!.miembroGremio.puede(Constantes.G_RECOLECTAR_RECAUDADOR)) {
            ENVIAR_Im_INFORMACION(personaje!!, "1101")
            return
        }
        val str = personaje!!.gremio.analizarRecaudadores()
        val str2 = recaudador.stringPanelInfo(personaje!!)
        for (p in personaje!!.gremio.miembros) {
            if (p != null) {
                if (p.enLinea()) {
                    ENVIAR_gITM_GREMIO_INFO_RECAUDADOR(p, str)
                    ENVIAR_gT_PANEL_RECAUDADORES_GREMIO(p, 'R', str2)
                }
            }
        }
        recaudador.borrarRecaudador()
    }

    private fun gremio_Poner_Recaudador() {
        if (personaje!!.estaDisponible(true, true)) {
            ENVIAR_BN_NADA(personaje, "PONER_RECAUDADOR NO DISPONIBLE")
            return
        }
        val gremio = personaje!!.gremio
        if (gremio.cantidadMiembros < 10) {
            ENVIAR_Im_INFORMACION(personaje!!, "1NOT_ENOUGHT_MEMBERS_IN_GUILD")
            return
        }
        if (personaje!!.miembroGremio.puede(Constantes.G_PONER_RECAUDADOR)) {
            ENVIAR_Im_INFORMACION(personaje!!, "1101")
            return
        }
        val mapa = personaje!!.mapa
        if (mapa.mapaNoRecaudador() || mapa.esArena() || mapa.trabajos!!.isNotEmpty() || Mundo.getCasaDentroPorMapa(
                mapa
                    .id
            ) != null || mapa.subArea!!.area.superArea!!.id == 3
        ) {
            ENVIAR_Im_INFORMACION(personaje!!, "113")
            return
        }
        if (gremio.cantRecaudadores >= gremio.nroMaxRecau) {
            ENVIAR_Im_INFORMACION(personaje!!, "1CANT_HIRE_MAX_TAXCOLLECTORS") // WTFFF !!!!!!
            return
        }
        if (mapa.recaudador != null) {
            ENVIAR_Im_INFORMACION(personaje!!, "1ALREADY_TAXCOLLECTOR_ON_MAP")
            return
        }
        if (!gremio.puedePonerRecaudadorMapa(mapa.id)) {
            ENVIAR_Im_INFORMACION(personaje!!, "1CANT_PUT_TAXCOLLECTOR_FOR_TIME")
            return
        }
        if (AtlantaMain.PARAM_LIMITAR_RECAUDADOR_GREMIO_POR_ZONA && !Mundo.puedePonerRecauEnZona(
                mapa.subArea!!.id,
                gremio.id
            )
        ) {
            ENVIAR_Im_INFORMACION(personaje!!, "1168;" + AtlantaMain.MAX_RECAUDADORES_POR_ZONA)
            return
        }
        val precio = 1000 + 10 * gremio.nivel
        if (precio <= 0 || personaje!!.kamas < precio) {
            ENVIAR_Im_INFORMACION(personaje!!, "182")
            return
        }
        personaje!!.addKamas(-precio.toLong(), true, true)
        val random1 = getRandomInt(1, 129).toString(36)
        val random2 = getRandomInt(1, 227).toString(36)
        val recau = Recaudador(
            Mundo.sigIDRecaudador(), mapa.id, personaje!!.celda.id, 3.toByte(),
            gremio.id, random1, random2, "", 0, 0, 0, System.currentTimeMillis(), personaje!!.Id
        )
        Mundo.addRecaudador(recau)
        gremio.addUltRecolectaMapa(mapa.id)
        ENVIAR_GM_RECAUDADOR_A_MAPA(mapa, "+" + recau.stringGM())
        val str = gremio.analizarRecaudadores()
        val str2 = recau.stringPanelInfo(personaje!!)
        for (p in gremio.miembros) {
            if (p != null) {
                if (p.enLinea()) {
                    ENVIAR_gITM_GREMIO_INFO_RECAUDADOR(p, str)
                    ENVIAR_gT_PANEL_RECAUDADORES_GREMIO(p, 'S', str2)
                }
            }
        }
    }

    private fun gremio_Cercado(packet: String) {
        if (personaje!!.estaDisponible(true, true)) {
            return
        }
        val mapaID = packet.toShort()
        val cercado = Mundo.getMapa(mapaID)?.cercado
        if (cercado!!.gremio!!.id != personaje!!.gremio.id) {
            ENVIAR_Im_INFORMACION(personaje!!, "1135")
            return
        }
        val celdaID = Mundo.getCeldaCercadoPorMapaID(mapaID)
        if (personaje!!.tenerYEliminarObjPorModYCant(9035, 1)) {
            personaje!!.teleport(mapaID, celdaID)
        } else {
            ENVIAR_Im_INFORMACION(personaje!!, "1159")
            return
        }
    }

    private fun gremio_Casa(packet: String) {
        val casaID = packet.toInt()
        val casa = Mundo.casas[casaID] ?: return
        if (personaje!!.gremio.id != casa.gremioID) {
            ENVIAR_Im_INFORMACION(personaje!!, "1135")
            return
        }
        if (!casa.tieneDerecho(Constantes.C_TELEPORT_GREMIO)) {
            ENVIAR_Im_INFORMACION(personaje!!, "1136")
            return
        }
        if (personaje!!.estaDisponible(true, true)) {
            return
        }
        if (personaje!!.tenerYEliminarObjPorModYCant(8883, 1)) { // pocima de la casa del gremio
            personaje!!.teleport(casa.mapaIDDentro, casa.celdaIDDentro)
        } else {
            ENVIAR_Im_INFORMACION(personaje!!, "1137")
            return
        }
    }

    private fun gremio_Cancelar_Creacion() {
        personaje!!.setOcupado(false)
        ENVIAR_gV_CERRAR_PANEL_GREMIO(personaje!!)
    }

    private fun gremio_Promover_Rango(packet: String) {
        try {
            val infos = packet.split(Pattern.quote("|").toRegex()).toTypedArray()
            val id = infos[0].toInt()
            var rango = infos[1].toInt()
            var xpDonada = infos[2].toInt()
            var derecho = infos[3].toInt()
            val perso = Mundo.getPersonaje(id)
            val cambiador = personaje!!.miembroGremio
            if (perso == null || perso.gremio == null) {
                ENVIAR_BN_NADA(personaje)
                return
            }
            val aCambiar = perso.miembroGremio
            if (aCambiar == null || personaje!!.gremio.id != perso.gremio.id) {
                ENVIAR_Im_INFORMACION(personaje!!, "1210")
                return
            }
            if (cambiador.rango == 1) { // lider de gremio
                if (cambiador.id == aCambiar.id) {
                    rango = -1
                    derecho = -1
                } else if (rango == 1) {
                    derecho = 1
                    xpDonada = -1
                    cambiador.setTodosDerechos(2, xpDonada, Constantes.G_TODOS_LOS_DERECHOS)
                }
            } else {
                if (aCambiar.rango == 1) {
                    ENVIAR_BN_NADA(personaje, "CAMBIAR RANGO A LIDER")
                    return
                } else {
                    if (xpDonada >= 0 && xpDonada != aCambiar.porcXpDonada) {
                        if (cambiador.id == aCambiar.id) {
                            if (cambiador.puede(Constantes.G_SU_XP_DONADA)) {
                                ENVIAR_Im_INFORMACION(personaje!!, "1101")
                                return
                            }
                        } else if (cambiador.puede(Constantes.G_TODAS_XP_DONADAS)) {
                            ENVIAR_Im_INFORMACION(personaje!!, "1101")
                            return
                        }
                    }
                    if (rango >= 2) {
                        if (rango != aCambiar.rango && cambiador.puede(Constantes.G_MODIF_RANGOS)) {
                            ENVIAR_Im_INFORMACION(personaje!!, "1101")
                            return
                        }
                    }
                    if (derecho >= 2) {
                        if (derecho != aCambiar.derechos && cambiador.puede(Constantes.G_MODIF_DERECHOS)) {
                            ENVIAR_Im_INFORMACION(personaje!!, "1101")
                            return
                        }
                    }
                }
            }
            aCambiar.setTodosDerechos(rango, xpDonada, derecho)
            ENVIAR_gS_STATS_GREMIO(personaje!!, personaje!!.miembroGremio)
            if (perso.Id != personaje!!.Id) {
                ENVIAR_gS_STATS_GREMIO(perso, perso.miembroGremio)
            }
        } catch (ignored: Exception) {
        }
    }

    private fun gremio_Expulsar(nombre: String) {
        val persoExpulsar = Mundo.getPersonajePorNombre(nombre)
        if (persoExpulsar == null) {
            ENVIAR_BN_NADA(personaje)
            return
        }
        var gremio = persoExpulsar.gremio
        if (gremio == null) {
            gremio = Mundo.getGremio(personaje!!.gremio.id)
        }
        val aExpulsar = gremio!!.getMiembro(persoExpulsar.Id)
        if (aExpulsar == null || aExpulsar.gremio.id != personaje!!.gremio.id) {
            ENVIAR_BN_NADA(personaje)
            return
        }
        if (gremio.id != personaje!!.gremio.id) {
            ENVIAR_gK_GREMIO_BAN(personaje!!, "Ea")
            return
        }
        val expulsador = personaje!!.miembroGremio
        if (expulsador.puede(Constantes.G_BANEAR) && expulsador.id != aExpulsar.id) {
            ENVIAR_Im_INFORMACION(personaje!!, "1101")
            return
        }
        if (expulsador.id != aExpulsar.id) {
            if (aExpulsar.rango == 1) {
                return
            }
            gremio.expulsarMiembro(aExpulsar.id)
            ENVIAR_gK_GREMIO_BAN(personaje!!, "K" + personaje!!.nombre + "|" + nombre)
            ENVIAR_gK_GREMIO_BAN(persoExpulsar, "K" + personaje!!.nombre)
        } else {
            if (expulsador.rango == 1 && gremio.miembros.size > 1) {
                for (pj in gremio.miembros) {
                    if (pj != null) {
                        gremio.expulsarMiembro(pj.Id)
                    }
                }
            } else {
                gremio.expulsarMiembro(personaje!!.Id)
            }
            if (gremio.miembros.isEmpty()) {
                Mundo.eliminarGremio(gremio)
            }
            ENVIAR_gK_GREMIO_BAN(personaje!!, "K$nombre|$nombre")
        }
    }

    private fun gremio_Invitar(packet: String) {
        when (packet[2]) {
            'R' -> gremio_Invitar_Unirse(packet)
            'E' -> gremio_Invitar_Rechazar()
            'K' -> gremio_Invitar_Aceptar()
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR GREMIO INVITAR: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "gremio_Invitar_Unirse()")
                }
            }
        }
    }

    private fun gremio_Invitar_Unirse(packet: String) {
        val invitandoA = Mundo.getPersonajePorNombre(packet.substring(3))
        if (invitandoA == null || invitandoA === personaje) {
            ENVIAR_gJ_GREMIO_UNIR(personaje!!, "Eu")
            return
        }
        if (!invitandoA.enLinea()) {
            ENVIAR_gJ_GREMIO_UNIR(personaje!!, "Eu")
            return
        }
        if (!invitandoA.estaVisiblePara(personaje)) {
            ENVIAR_Im_INFORMACION(personaje!!, "1209")
            return
        }
        if (invitandoA.gremio != null) {
            ENVIAR_gJ_GREMIO_UNIR(personaje!!, "Ea")
            return
        }
        if (personaje!!.puedeInvitar() || invitandoA.puedeInvitar()) {
            ENVIAR_Im_INFORMACION(personaje!!, "1PLAYERS_IS_BUSSY")
            return
        }
        if (personaje!!.miembroGremio.puede(Constantes.G_INVITAR)) {
            ENVIAR_Im_INFORMACION(personaje!!, "1101")
            return
        }
        if (AtlantaMain.PARAM_LIMITE_MIEMBROS_GREMIO) {
            val maxMiembros = personaje!!.gremio.maxMiembros
            if (personaje!!.gremio.cantidadMiembros >= maxMiembros) {
                ENVIAR_Im_INFORMACION(personaje!!, "155;$maxMiembros")
                return
            }
        }
        personaje!!.setInvitandoA(invitandoA, "gremio")
        invitandoA.setInvitador(personaje, "gremio")
        ENVIAR_gJ_GREMIO_UNIR(personaje!!, "R" + invitandoA.nombre)
        ENVIAR_gJ_GREMIO_UNIR(
            invitandoA, "r" + personaje!!.Id + "|" + personaje!!.nombre + "|" + personaje!!
                .gremio.nombre
        )
    }

    private fun gremio_Invitar_Aceptar() {
        if (personaje!!.tipoInvitacion != "gremio") {
            ENVIAR_BN_NADA(personaje)
            return
        }
        val invitador = personaje!!.invitador
        if (invitador == null) {
            ENVIAR_BN_NADA(personaje)
            return
        }
        val gremio = invitador.gremio
        if (gremio == null) {
            ENVIAR_BN_NADA(personaje)
            return
        }
        if (AtlantaMain.PARAM_LIMITE_MIEMBROS_GREMIO) {
            var maxMiembros = 40 + gremio.nivel
            if (AtlantaMain.LIMITE_MIEMBROS_GREMIO > 0) {
                maxMiembros = AtlantaMain.LIMITE_MIEMBROS_GREMIO
            }
            if (gremio.cantidadMiembros >= maxMiembros) {
                ENVIAR_Im_INFORMACION(invitador, "155;$maxMiembros")
                ENVIAR_Im_INFORMACION(personaje!!, "155;$maxMiembros")
                return
            }
        }
        val miembro = gremio.addMiembro(personaje!!.Id, 0, 0, 0.toByte(), 0)
        personaje!!.miembroGremio = miembro
        invitador.setInvitandoA(null, "")
        personaje!!.setInvitador(null, "")
        ENVIAR_gJ_GREMIO_UNIR(invitador, "Ka" + personaje!!.nombre)
        ENVIAR_gS_STATS_GREMIO(personaje!!, miembro)
        ENVIAR_gJ_GREMIO_UNIR(personaje!!, "Kj")
        personaje!!.cambiarRopaVisual()
    }

    private fun gremio_Invitar_Rechazar() {
        personaje!!.rechazarGremio()
    }

    private fun gremio_Informacion(packet: String) {
        val gremio = personaje!!.gremio
        if (gremio == null) {
            ENVIAR_BN_NADA(personaje)
            return
        }
        when (packet[2]) {
            'B' -> ENVIAR_gIB_GREMIO_INFO_BOOST(personaje!!, gremio.analizarRecauAGremio())
            'F' -> ENVIAR_gIF_GREMIO_INFO_CERCADOS(personaje!!, gremio.analizarInfoCercados())
            'G' -> ENVIAR_gIG_GREMIO_INFO_GENERAL(personaje!!, gremio)
            'H' -> ENVIAR_gIH_GREMIO_INFO_CASAS(personaje!!, Casa.stringCasaGremio(gremio.id))
            'M' -> ENVIAR_gIM_GREMIO_INFO_MIEMBROS(personaje!!, gremio, '+')
            'T' -> {
                var c = 'a'
                try {
                    c = packet[3]
                } catch (ignored: Exception) {
                }
                if (c == 'V') {
                } else {
                    ENVIAR_gITM_GREMIO_INFO_RECAUDADOR(personaje!!, gremio.analizarRecaudadores())
                    gremio.actualizarAtacantesDefensores()
                }
            }
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR GREMIO INFORMACION: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "gremio_Informacion()")
                }
            }
        }
    }

    private fun gremio_Crear(packet: String) {
        if (personaje!!.miembroGremio != null) {
            ENVIAR_gC_CREAR_PANEL_GREMIO(personaje!!, "Ea")
            return
        }
        try {
            val infos = packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()
            val escudoID = infos[0].toInt().toString(36)
            val colorEscudo = infos[1].toInt().toString(36)
            val emblemaID = infos[2].toInt().toString(36)
            val colorEmblema = infos[3].toInt().toString(36)
            val nombre = infos[4].substring(0, 1).toUpperCase() + infos[4].substring(1).toLowerCase()
            if (Mundo.nombreGremioUsado(nombre)) {
                ENVIAR_gC_CREAR_PANEL_GREMIO(personaje!!, "Ean")
                return
            }
            if (nombre.length < 2 || nombre.length > 30) {
                ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "1NAME_GUILD_MANY_LONG")
                return
            }
            var esValido = true
            val abcMin = "abcdefghijklmnopqrstuvwxyz- '"
            var cantSimbol: Byte = 0
            var cantLetras: Byte = 0
            var letra_A = ' '
            var letra_B = ' '
            for (letra in nombre.toLowerCase().toCharArray()) {
                if (!abcMin.contains(letra.toString() + "")) {
                    esValido = false
                    break
                }
                if (letra == letra_A && letra == letra_B) {
                    esValido = false
                    break
                }
                if (letra != '-') {
                    letra_A = letra_B
                    letra_B = letra
                    cantLetras++
                } else {
                    if (cantLetras.toInt() == 0 || cantSimbol > 0) {
                        esValido = false
                        break
                    }
                    cantSimbol++
                }
            }
            if (!esValido) {
                ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "1NAME_GUILD_USE_CHARACTERS_INVALIDS")
                return
            }
            val emblema = "$escudoID,$colorEscudo,$emblemaID,$colorEmblema"
            if (Mundo.emblemaGremioUsado(emblema)) {
                ENVIAR_gC_CREAR_PANEL_GREMIO(personaje!!, "Eae")
                return
            }
            if (!personaje!!.tieneObjPorModYCant(1575, 1)) {
                ENVIAR_Im_INFORMACION(personaje!!, "14")
                return
            }
            personaje!!.restarObjPorModYCant(1575, 1) // quitar la gremiologema
            val gremio = Gremio(personaje, nombre, emblema)
            Mundo.addGremio(gremio)
            INSERT_GREMIO(gremio)
            val miembro = gremio.addMiembro(personaje!!.Id, 0, 0, 0.toByte(), 0)
            miembro.setTodosDerechos(1, 0, 1)
            personaje!!.miembroGremio = miembro
            ENVIAR_gS_STATS_GREMIO(personaje!!, miembro)
            ENVIAR_gC_CREAR_PANEL_GREMIO(personaje!!, "K")
            ENVIAR_gV_CERRAR_PANEL_GREMIO(personaje!!)
            personaje!!.setOcupado(false)
            personaje!!.cambiarRopaVisual()
        } catch (ignored: Exception) {
        }
    }

    fun analizar_Canal(packet: String) {
        if (packet.length < 2) {
            try {
                ENVIAR_BN_NADA(personaje)
                personaje!!.enviarmensajeNegro("Por Favor repita su accion, El servidor ha perdido los datos en el camino")
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        if (packet[1] == 'C') {
            canal_Cambiar(packet)
        } else {
            redactarLogServidorln("$stringDesconocido ANALIZAR CANAL: $packet")
            if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                cerrarSocket(true, "analizar_Canal()")
            }
        }
    }

    private fun canal_Cambiar(packet: String) {
        try {
            val canal = packet.substring(3).split("".toRegex()).toTypedArray()
            when (packet[2]) {
                '+' -> for (c in canal) {
                    if (c.isEmpty()) {
                        continue
                    }
                    personaje!!.addCanal(c)
                }
                '-' -> for (c in canal) {
                    if (c.isEmpty()) {
                        continue
                    }
                    personaje!!.removerCanal(c)
                }
                else -> {
                    redactarLogServidorln("$stringDesconocido ANALIZAR CANAL CAMBIAR: $packet")
                    if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                        redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                        cerrarSocket(true, "canal_Cambiar()")
                    }
                }
            }
        } catch (ignored: Exception) {
        }
    }

    private fun analizar_Montura(packet: String) {
        if (packet.length < 2) {
            try {
                ENVIAR_BN_NADA(personaje)
                personaje!!.enviarmensajeNegro("Por Favor repita su accion, El servidor ha perdido los datos en el camino")
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        when (packet[1]) {
            'b' -> montura_Comprar_Cercado(packet)
            'c' -> montura_Castrar()
            'd', 'p' -> montura_Descripcion(packet)
            'f' -> montura_Liberar()
            'n' -> montura_Nombre(packet.substring(2))
            'o' -> montura_Borrar_Objeto_Crianza(packet)
            'r' -> montura_Montar()
            's' -> montura_Vender_Cercado(packet)
            'v' -> ENVIAR_Rv_MONTURA_CERRAR(personaje!!)
            'x' -> montura_CambiarXP_Donada(packet)
            'Z' ->  // try {
// if (_perso.getMontura().getColor() == 75) {
// GestorSalida.ENVIAR_Rz_STATS_VIP(_perso, _perso.getMontura().getStatsVIP());
// } else {
// GestorSalida.ENVIAR_BN_NADA(_perso);
// }
// } catch (final Exception e) {
                ENVIAR_BN_NADA(personaje)
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR MONTURA: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "analizar_Montura()")
                }
            }
        }
    }

    private fun montura_Vender_Cercado(packet: String) {
        ENVIAR_Rv_MONTURA_CERRAR(personaje!!)
        val cercado = personaje!!.mapa.cercado
        if (cercado!!.esPublico()) {
            ENVIAR_Im_INFORMACION(personaje!!, "194")
            return
        }
        if (cercado.dueñoID != personaje!!.Id) {
            ENVIAR_Im_INFORMACION(personaje!!, "195")
            return
        }
        var precio = 0
        try {
            precio = packet.substring(2).toInt()
        } catch (ignored: Exception) {
        }
        if (precio < 0) {
            ENVIAR_BN_NADA(personaje)
            return
        }
        cercado.precioPJ = precio
        for (p in personaje!!.mapa.arrayPersonajes!!) {
            ENVIAR_Rp_INFORMACION_CERCADO(p, cercado)
        }
    }

    private fun montura_Comprar_Cercado(packet: String) {
        ENVIAR_Rv_MONTURA_CERRAR(personaje!!)
        val cercado = personaje!!.mapa.cercado
        val vendedor = Mundo.getPersonaje(cercado!!.dueñoID)
        if (cercado.esPublico()) {
            ENVIAR_Im_INFORMACION(personaje!!, "196")
            return
        }
        if (cercado.precio <= 0) {
            ENVIAR_Im_INFORMACION(personaje!!, "197")
            return
        }
        if (personaje!!.kamas < cercado.precio) {
            ENVIAR_Im_INFORMACION(personaje!!, "1128;" + cercado.precio)
            return
        }
        if (personaje!!.gremio == null) {
            ENVIAR_Im_INFORMACION(personaje!!, "1135")
            return
        }
        // if (_perso.getMiembroGremio().getRango() != 1) {
// GestorSalida.ENVIAR_Im_INFORMACION(_perso, "198");
// return;
// }
        if (Mundo.getCantCercadosGremio(personaje!!.gremio.id) >= ceil(
                (personaje!!.gremio.nivel
                        / 10f).toDouble()
            ).toInt().toByte()
        ) {
            ENVIAR_Im_INFORMACION(personaje!!, "1103")
            return
        }
        personaje!!.addKamas(-cercado.precio.toLong(), true, true)
        if (vendedor != null) {
            vendedor.addKamasBanco(cercado.precio.toLong())
            val tempPerso = vendedor.cuenta.tempPersonaje
            if (tempPerso != null) {
                ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(
                    tempPerso, 17, cercado.precio.toString() + ";" + personaje!!
                        .gremio.nombre, ""
                )
            } else {
                vendedor.cuenta.addMensaje(
                    "M117|" + cercado.precio + ";" + personaje!!.gremio.nombre + "|",
                    true
                )
            }
        }
        cercado.precioPJ = 0
        cercado.dueñoID = personaje!!.Id
        cercado.gremio = personaje!!.gremio
        for (pj in personaje!!.mapa.arrayPersonajes!!) {
            ENVIAR_Rp_INFORMACION_CERCADO(pj, cercado)
        }
    }

    private fun montura_CambiarXP_Donada(packet: String) {
        try {
            val xp = packet.substring(2).toByte()
            personaje!!.porcXPMontura = xp.toInt()
            ENVIAR_Rx_EXP_DONADA_MONTURA(personaje!!)
        } catch (ignored: Exception) {
        }
    }

    private fun montura_Borrar_Objeto_Crianza(packet: String) {
        try {
            val cercado = personaje?.mapa?.cercado
            if (cercado == null) {
                ENVIAR_BN_NADA(personaje)
                return
            }
            if (personaje?.gremio == null) {
                ENVIAR_BN_NADA(personaje)
                return
            }
            if (personaje?.miembroGremio?.puede(Constantes.G_MEJORAR_CERCADOS) == true) {
                ENVIAR_Im_INFORMACION(personaje!!, "193")
                return
            }
            val celda = packet.substring(2).toShort()
            if (cercado.retirarObjCria(celda, personaje)) {
                ENVIAR_GDO_OBJETO_TIRAR_SUELO(personaje!!.mapa, '-', celda, 0, false, "")
                return
            }
        } catch (ignored: Exception) {
        }
    }

    private fun montura_Nombre(nombre: String) {
        if (personaje!!.montura == null) {
            ENVIAR_Re_DETALLES_MONTURA(personaje!!, "Er", null)
        } else {
            personaje!!.montura.nombre = nombre
            ENVIAR_Rn_CAMBIO_NOMBRE_MONTURA(personaje!!, nombre)
        }
    }

    private fun montura_Montar() {
        personaje!!.subirBajarMontura(false)
    }

    private fun montura_Castrar() {
        if (personaje!!.montura == null) {
            ENVIAR_Re_DETALLES_MONTURA(personaje!!, "Er", null)
        } else {
            personaje!!.montura.castrarPavo()
            ENVIAR_Re_DETALLES_MONTURA(personaje!!, "+", personaje!!.montura)
        }
    }

    private fun montura_Liberar() {
        if (personaje!!.montura == null) {
            ENVIAR_Re_DETALLES_MONTURA(personaje!!, "Er", null)
        } else {
            Mundo.eliminarMontura(personaje!!.montura)
            personaje!!.montura = null
        }
    }

    private fun montura_Descripcion(packet: String) {
        try {
            var id = packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()[0].toInt()
            if (id > 0) {
                id = -id
            }
            Mundo.getMontura(id)?.let { ENVIAR_Rd_DESCRIPCION_MONTURA(personaje!!, it) }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje)
        }
    }

    private fun analizar_Amigos(packet: String) {
        if (packet.length < 2) {
            try {
                ENVIAR_BN_NADA(personaje)
                personaje!!.enviarmensajeNegro("Por Favor repita su accion, El servidor ha perdido los datos en el camino")
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        when (packet[1]) {
            'A' -> amigo_Agregar(packet)
            'D' -> amigo_Borrar(packet)
            'L' -> ENVIAR_FL_LISTA_DE_AMIGOS(personaje!!)
            'O' -> when (packet[2]) {
                '-' -> {
                    personaje!!.mostrarAmigosEnLinea(false)
                    ENVIAR_BN_NADA(personaje)
                }
                '+' -> {
                    personaje!!.mostrarAmigosEnLinea(true)
                    ENVIAR_BN_NADA(personaje)
                }
            }
            'J' -> amigo_Esposo(packet)
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR AMIGOS: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "analizar_Amigos()")
                }
            }
        }
    }

    private fun amigo_Esposo(packet: String) {
        val esposo = Mundo.getPersonaje(personaje!!.esposoID)
        if (esposo == null) {
            ENVIAR_Im_INFORMACION(personaje!!, "138")
            return
        }
        if (!esposo.enLinea()) {
            if (esposo.sexo == Constantes.SEXO_FEMENINO) {
                ENVIAR_Im_INFORMACION(personaje!!, "136")
            } else {
                ENVIAR_Im_INFORMACION(personaje!!, "137")
            }
            ENVIAR_FL_LISTA_DE_AMIGOS(personaje!!)
            return
        }
        when (packet[2]) {
            'S' -> personaje!!.teleportEsposo(esposo)
            'C' -> personaje!!.seguirEsposo(esposo, packet)
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR AMIGO ESPOSO: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "amigo_Esposo()")
                }
            }
        }
    }

    private fun amigo_Borrar(packet: String) {
        var id = -1
        id = when (packet[2]) {
            '%' -> {
                val perso = Mundo.getPersonajePorNombre(packet.substring(3))
                if (perso == null) {
                    ENVIAR_FD_BORRAR_AMIGO(personaje!!, "Ef")
                    return
                }
                perso.cuentaID
            }
            '*' -> {
                val cuenta = Mundo.getCuentaPorApodo(packet.substring(3))
                if (cuenta == null) {
                    ENVIAR_FD_BORRAR_AMIGO(personaje!!, "Ef")
                    return
                }
                cuenta.id
            }
            else -> {
                val perso2 = Mundo.getPersonajePorNombre(packet.substring(2))
                if (perso2 == null || !perso2.enLinea()) {
                    ENVIAR_FD_BORRAR_AMIGO(personaje!!, "Ef")
                    return
                }
                perso2.cuentaID
            }
        }
        if (id == -1 || !cuenta!!.esAmigo(id)) {
            ENVIAR_FD_BORRAR_AMIGO(personaje!!, "Ef")
        } else {
            cuenta!!.borrarAmigo(id)
        }
    }

    private fun amigo_Agregar(packet: String) {
        var id = -1
        id = when (packet[2]) {
            '%' -> {
                val perso = Mundo.getPersonajePorNombre(packet.substring(3))
                if (perso == null || !perso.enLinea()) {
                    ENVIAR_FA_AGREGAR_AMIGO(personaje!!, "Ef")
                    return
                }
                perso.cuentaID
            }
            '*' -> {
                val cuenta = Mundo.getCuentaPorApodo(packet.substring(3))
                if (cuenta == null || !cuenta.enLinea()) {
                    ENVIAR_FA_AGREGAR_AMIGO(personaje!!, "Ef")
                    return
                }
                cuenta.id
            }
            else -> {
                val perso2 = Mundo.getPersonajePorNombre(packet.substring(2))
                if (perso2 == null || !perso2.enLinea()) {
                    ENVIAR_FA_AGREGAR_AMIGO(personaje!!, "Ef")
                    return
                }
                perso2.cuentaID
            }
        }
        if (id == -1) {
            ENVIAR_FA_AGREGAR_AMIGO(personaje!!, "Ef")
        } else {
            cuenta!!.addAmigo(id)
        }
    }

    fun analizar_Grupo(packet: String) {
        val grupo = personaje!!.grupoParty
        if (packet.length < 2) {
            try {
                ENVIAR_BN_NADA(personaje)
                personaje!!.enviarmensajeNegro("Por Favor repita su accion, El servidor ha perdido los datos en el camino")
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        when (packet[1]) {
            'A' -> grupo_Aceptar()
            'F' -> {
                if (grupo == null) {
                    ENVIAR_BN_NADA(personaje)
                    return
                }
                grupo_Seguir(packet)
            }
            'G' -> {
                if (grupo == null) {
                    ENVIAR_BN_NADA(personaje)
                    return
                }
                grupo_Seguirme_Todos(packet)
            }
            'I' -> grupo_Invitar(packet)
            'R' -> grupo_Rechazar()
            'V' -> {
                if (grupo == null) {
                    ENVIAR_BN_NADA(personaje)
                    return
                }
                grupo_Expulsar(packet)
            }
            'W' -> {
                if (grupo == null) {
                    ENVIAR_BN_NADA(personaje)
                    return
                }
                grupo_Localizar()
            }
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR GRUPO: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "analizar_Grupo()")
                }
            }
        }
    }

    private fun grupo_Seguirme_Todos(packet: String) {
        if (personaje!!.grupoParty == null) {
            ENVIAR_BN_NADA(personaje)
            return
        }
        var id = -1
        try {
            id = packet.substring(3).toInt()
        } catch (ignored: Exception) {
        }
        val persoSeguir = Mundo.getPersonaje(id)
        if (persoSeguir == null || !persoSeguir.enLinea()) {
            ENVIAR_Im_INFORMACION(personaje!!, "1211")
            return
        }
        when (packet[2]) {
            '+' -> {
                personaje!!.grupoParty.rastrear = persoSeguir
                for (integrante in personaje!!.grupoParty.miembros) {
                    ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(
                        integrante, persoSeguir.mapa.x.toString() + "|" + persoSeguir
                            .mapa.y
                    )
                    ENVIAR_PF_SEGUIR_PERSONAJE(integrante, "+" + persoSeguir.Id)
                }
            }
            '-' -> {
                personaje!!.grupoParty.rastrear = null
                for (integrante in personaje!!.grupoParty.miembros) {
                    ENVIAR_IC_BORRAR_BANDERA_COMPAS(integrante)
                    ENVIAR_PF_SEGUIR_PERSONAJE(integrante, "-")
                }
            }
        }
    }

    private fun grupo_Seguir(packet: String) {
        if (personaje!!.grupoParty == null) {
            ENVIAR_BN_NADA(personaje)
            return
        }
        var id = -1
        try {
            id = packet.substring(3).toInt()
        } catch (ignored: Exception) {
        }
        val persoSeguir = Mundo.getPersonaje(id)
        if (persoSeguir == null || !persoSeguir.enLinea()) {
            ENVIAR_Im_INFORMACION(personaje!!, "1211")
            return
        }
        when (packet[2]) {
            '+' -> {
                ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(
                    personaje!!, persoSeguir.mapa.x.toString() + "|" + persoSeguir
                        .mapa.y
                )
                ENVIAR_PF_SEGUIR_PERSONAJE(personaje!!, "+" + persoSeguir.Id)
                personaje!!.grupoParty.rastrear = persoSeguir
            }
            '-' -> {
                ENVIAR_IC_BORRAR_BANDERA_COMPAS(personaje!!)
                ENVIAR_PF_SEGUIR_PERSONAJE(personaje!!, "-")
                personaje!!.grupoParty.rastrear = null
            }
        }
    }

    private fun grupo_Localizar() {
        val grupo = personaje!!.grupoParty
        val str = StringBuilder()
        for (miembro in grupo.miembros) {
            if (str.isNotEmpty()) {
                str.append("|")
            }
            str.append(miembro.mapa.x.toInt()).append(";").append(miembro.mapa.y.toInt()).append(";")
                .append(miembro.mapa.id.toInt()).append(";2;").append(miembro.Id).append(";").append(miembro.nombre)
        }
        ENVIAR_IH_COORDENADAS_UBICACION(personaje!!, str.toString())
    }

    private fun grupo_Localizar_especifico(grupo: Grupo) {
        val str = StringBuilder()
        for (miembro in grupo.miembros) {
            if (str.isNotEmpty()) {
                str.append("|")
            }
            str.append(miembro.mapa.x.toInt()).append(";").append(miembro.mapa.y.toInt()).append(";")
                .append(miembro.mapa.id.toInt()).append(";2;").append(miembro.Id).append(";").append(miembro.nombre)
        }
        ENVIAR_IH_COORDENADAS_UBICACION(personaje!!, str.toString())
    }

    private fun grupo_Expulsar(packet: String) { // usar este packet para atacar
        val grupo = personaje!!.grupoParty ?: return
        if (!grupo.esLiderGrupo(personaje!!) || packet.length == 2) {
            grupo.dejarGrupo(personaje!!, false)
        } else {
            var id = -1
            try {
                id = packet.substring(2).toInt()
            } catch (ignored: Exception) {
            }
            val expulsado = Mundo.getPersonaje(id)
            if (expulsado == null) {
                ENVIAR_Im_INFORMACION(personaje!!, "1211")
                return
            }
            grupo.dejarGrupo(expulsado, true)
        }
    }

    private fun grupo_Invitar(packet: String) {
        val nombre = packet.substring(2)
        val invitandoA = Mundo.getPersonajePorNombre(nombre)
        if (invitandoA == null || invitandoA === personaje) {
            ENVIAR_Im_INFORMACION(personaje!!, "1211")
            return
        }
        if (!invitandoA.enLinea() || invitandoA.esIndetectable()) {
            ENVIAR_PIE_ERROR_INVITACION_GRUPO(personaje!!, "n$nombre")
            return
        }
        if (!invitandoA.estaVisiblePara(personaje)) {
            ENVIAR_Im_INFORMACION(personaje!!, "1209")
            return
        }
        if (personaje!!.puedeInvitar() || invitandoA.puedeInvitar()) {
            ENVIAR_Im_INFORMACION(personaje!!, "1PLAYERS_IS_BUSSY")
            return
        }
        if (invitandoA.grupoParty != null) {
            ENVIAR_PIE_ERROR_INVITACION_GRUPO(personaje!!, "a$nombre")
            return
        }
        if (personaje!!.grupoParty != null && personaje!!.grupoParty.miembros.size >= 8) {
            ENVIAR_PIE_ERROR_INVITACION_GRUPO(personaje!!, "f")
            return
        }
        invitandoA.setInvitador(personaje, "grupo")
        personaje!!.setInvitandoA(invitandoA, "grupo")
        val ip2: String
        val ip1: String = personaje!!.cuenta.actualIP
        ip2 = invitandoA.cuenta.actualIP
        //		System.out.println("Estoy aqui"+invitandoA.getCuenta().getActualIP()+"a"+ _perso.getCuenta().getActualIP()+"a");
        if (ip1.equals(ip2, ignoreCase = true)) {
            val lider = invitandoA.invitador
            if (lider == null) {
                ENVIAR_Im_INFORMACION(personaje!!, "1211")
                return
            }
            var grupo = lider.grupoParty
            if (grupo == null) {
                grupo = Grupo()
                grupo.addIntegrante(lider)
                lider.mostrarGrupo()
            }
            ENVIAR_PM_AGREGAR_PJ_GRUPO_A_GRUPO(grupo, invitandoA.stringInfoGrupo())
            grupo.addIntegrante(invitandoA)
            invitandoA.mostrarGrupo()
            invitandoA.setInvitador(null, "")
            lider.setInvitandoA(null, "")
            ENVIAR_PA_ACEPTAR_INVITACION_GRUPO(lider)
            invitandoA.grupoParty.addAlumno(invitandoA)
            ENVIAR_cs_CHAT_MENSAJE(invitandoA, "Estas en modo esclavo automatico", Constantes.COLOR_ROJO)
        } else {
            ENVIAR_PIK_INVITAR_GRUPO(personaje!!, personaje!!.nombre, nombre)
            ENVIAR_PIK_INVITAR_GRUPO(invitandoA, personaje!!.nombre, nombre)
        }
    }

    private fun grupo_Rechazar() {
        personaje!!.rechazarGrupo()
    }

    private fun grupo_Aceptar() {
        if (personaje!!.tipoInvitacion != "grupo") {
            ENVIAR_BN_NADA(personaje)
            return
        }
        val lider = personaje!!.invitador
        if (lider == null) {
            ENVIAR_Im_INFORMACION(personaje!!, "1211")
            return
        }
        var grupo = lider.grupoParty
        if (grupo == null) {
            grupo = Grupo()
            grupo.addIntegrante(lider)
            lider.mostrarGrupo()
        }
        ENVIAR_PM_AGREGAR_PJ_GRUPO_A_GRUPO(grupo, personaje!!.stringInfoGrupo())
        grupo.addIntegrante(personaje!!)
        personaje!!.mostrarGrupo()
        personaje!!.setInvitador(null, "")
        lider.setInvitandoA(null, "")
        ENVIAR_PA_ACEPTAR_INVITACION_GRUPO(lider)
    }

    fun analizar_Objetos(packet: String) {
        if (personaje!!.estaExchange()) {
            return
        }
        if (packet.length < 2) {
            try {
                ENVIAR_BN_NADA(personaje)
                personaje!!.enviarmensajeNegro("Por Favor repita su accion, El servidor ha perdido los datos en el camino")
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        when (packet[1]) {
            'd' -> objeto_Eliminar(packet)
            'D' -> objeto_Tirar(packet)
            'f' -> objeto_Alimentar_Objevivo(packet)
            'M' -> objeto_Mover(packet)
            'm' -> objeto_Desasociar_Mimobionte(packet)
            's' -> objeto_Apariencia_Objevivo(packet)
            'U', 'u' -> objeto_Usar(packet)
            'x' -> objeto_Desequipar_Objevivo(packet)
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR OBJETOS: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "analizar_Objetos()")
                }
            }
        }
    }

    @Synchronized
    private fun objeto_Eliminar(packet: String) {
        try {
            val infos = packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()
            val id = infos[0].toInt()
            var cant = 0
            try {
                if (infos.size > 1) {
                    cant = infos[1].toInt()
                }
            } catch (ignored: Exception) {
            }
            val objeto = personaje!!.getObjeto(id)
            if (objeto == null || cant <= 0 || personaje!!.estaDisponible(false, true)) {
                ENVIAR_ODE_ERROR_ELIMINAR_OBJETO(personaje!!)
                return
            }
            if (objeto.objModelo?.tipo?.toInt() == Constantes.OBJETO_TIPO_OBJETO_DE_BUSQUEDA) {
                ENVIAR_ODE_ERROR_ELIMINAR_OBJETO(personaje!!)
                return
            }
            if (objeto.cantidad - cant < 1) {
                personaje!!.borrarOEliminarConOR(id, true)
                if (Constantes.esPosicionEquipamiento(objeto.posicion)) {
                    ENVIAR_As_STATS_DEL_PJ(personaje!!)
                }
                if (Constantes.esPosicionVisual(objeto.posicion)) {
                    personaje!!.cambiarRopaVisual()
                }
            } else {
                objeto.cantidad = objeto.cantidad - cant
                ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(personaje!!, objeto)
            }
            ENVIAR_Ow_PODS_DEL_PJ(personaje!!)
            if (!AtlantaMain.DEVOLVER_ITEMS) return
            val receta = Mundo.RECETAS[objeto.objModeloID] ?: return
            if (receta.isEmpty()) return
            personaje?.enviarmensajeRojo("Estamos devolviendote los objetos de la receta.")
            thread(start = true, isDaemon = true, contextClassLoader = null, name = null, priority = 5) {
                try {
                    AtlantaMain.ELIMINANDO_OBJETOS.add(Thread.currentThread())
                    val perso = personaje
                    if (perso == null) {
                        AtlantaMain.ELIMINANDO_OBJETOS.remove(Thread.currentThread())
                        return@thread
                    }
                    val porcentajeDevolucion = (AtlantaMain.PORCENTAJE_DEVOLVER_ITEMS / 100.0)
                    val listaObjetosDevueltosTotales =
                        ArrayList<Mundo.Duo<Int, Int>>()
                    for (i in 1..cant) {
                        var cantIngredientes = 0.0
                        var devuelto = 0
                        val listaObjetosDevueltos =
                            ArrayList<Mundo.Duo<Int, Int>>() // para comparar con los ingredientes
                        for (obj in receta) { // Primera vuelta para contar el total de ingredientes
                            cantIngredientes += obj._segundo // cuenta
                            val obj2: Mundo.Duo<Int, Int> = Mundo.Duo(-1, 0)
                            obj2._primero = obj._primero
                            listaObjetosDevueltos.add(obj2) // crea copia para contar ingredientes devueltos del item
                        }
                        cantIngredientes *= porcentajeDevolucion
                        var vueltas = 0
                        while (devuelto < cantIngredientes) {
                            if (vueltas > 180) break // Evitar los super bucles en caso de los continues de abajo
                            vueltas++
                            val pos = getRandomInt(0, receta.size - 1)
                            val objseleccionado = receta[pos] // Seleccionado
                            val objdevuelto =
                                listaObjetosDevueltos[pos] // El mismo Pero para recuento de cuanto se ha devuelto
                            if (objdevuelto._segundo >= objseleccionado._segundo) continue // Si supero el limite de devolucion busca otro item
                            Mundo.getObjetoModelo(objseleccionado._primero) ?: continue // No existe, se prueba otro
                            val cantInt = getRandomInt(0, objseleccionado._segundo - objdevuelto._segundo)
                            if (cantInt == 0) continue // No hubo suerte aqui asi que se repite el ciclo con otro item
//                            val obj = OM.crearObjeto(cantInt, Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM)
//                            obj.dueñoTemp = perso.id
//                            perso.addObjIdentAInventario(obj, false, false)
                            objdevuelto._segundo += cantInt
                            devuelto += cantInt
                            // Encontrar //
                            var encontrado = false
                            for (objdevueltos in listaObjetosDevueltosTotales) {
                                if (objdevuelto._primero == objdevueltos._primero) {
                                    encontrado = true
                                    objdevueltos._segundo += cantInt
                                }
                            }
                            if (!encontrado) listaObjetosDevueltosTotales.add(objdevuelto)
                        }

                    }
                    var i = 0
                    for (obj in listaObjetosDevueltosTotales) {
                        personaje?.enviarmensajeNegro(
                            "Faltan: ${listaObjetosDevueltosTotales.size - i++} segundos " +
                                    "para terminar"
                        )
                        val OM =
                            Mundo.getObjetoModelo(obj._primero) ?: continue // No existe, se prueba otro
                        val obj =
                            OM.crearObjeto(obj._segundo, Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM)
                        perso.addObjIdentAInventario(obj, false)
                        ENVIAR_Ow_PODS_DEL_PJ(perso)
                        try {
                            Thread.sleep(1000)
                        } catch (e: Exception) {
                        }
                    }
                    perso.enviarmensajeNegro("El proceso ha terminado con exito")
                    perso.salvar()
                } catch (e: Exception) {
                    redactarLogServidorln("Error al devolver recursos pj: ${personaje?.nombre}")
                }
                AtlantaMain.ELIMINANDO_OBJETOS.remove(Thread.currentThread())
            }
        } catch (e: Exception) {
            ENVIAR_ODE_ERROR_ELIMINAR_OBJETO(personaje!!)
        }
    }

    @Synchronized
    private fun objeto_Tirar(packet: String) {
        var id = -1
        var cantidad = -1
        try {
            id = packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()[0].toInt()
            cantidad = packet.split(Pattern.quote("|").toRegex()).toTypedArray()[1].toInt()
        } catch (ignored: Exception) {
        }
        val objeto = personaje!!.getObjeto(id)
        if (objeto == null || cantidad < 1) {
            ENVIAR_BN_NADA(personaje, "OBJETO TIRAR NULO O -1")
            return
        }
        if (personaje!!.estaDisponible(false, true)) {
            ENVIAR_BN_NADA(personaje, "OBJETO TIRAR NO DISPONIBLE")
            return
        }
        if (objeto.objModelo?.tipo?.toInt() == Constantes.OBJETO_TIPO_OBJETO_DE_BUSQUEDA) {
            ENVIAR_BN_NADA(personaje, "OBJETO TIRAR TIPO BUSQUEDA")
            return
        }
        if (objeto.tieneStatTexto(Constantes.STAT_LIGADO_A_CUENTA)) {
            ENVIAR_Im_INFORMACION(personaje!!, "1129")
            return
        }
        if (objeto.pasoIntercambiableDesde()) {
            ENVIAR_Im_INFORMACION(personaje!!, "1129")
            return
        }
        if (objeto.objModeloID == 10085) { // pergaminos de busqueda
            personaje!!.borrarOEliminarConOR(id, true)
            return
        }
        val celdaDrop = Camino.getCeldaIDCercanaLibre(personaje!!.celda, personaje!!.mapa)
        if (celdaDrop.toInt() == 0) {
            ENVIAR_Im_INFORMACION(personaje!!, "1145")
            return
        }
        val celdaTirar = personaje!!.mapa.getCelda(celdaDrop)
        val nuevaCantidad = objeto.cantidad - cantidad
        if (nuevaCantidad >= 1) {
            val nuevoObj = objeto.clonarObjeto(nuevaCantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
            personaje!!.addObjetoConOAKO(nuevoObj, true)
            objeto.cantidad = cantidad
            ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(personaje!!, objeto)
        }
        personaje!!.borrarOEliminarConOR(id, false)
        celdaTirar!!.objetoTirado = objeto
        ENVIAR_Ow_PODS_DEL_PJ(personaje!!)
        ENVIAR_GDO_OBJETO_TIRAR_SUELO(
            personaje!!.mapa, '+', celdaTirar.id, objeto.objModeloID,
            false, ""
        )
        // GestorSalida.ENVIAR_As_STATS_DEL_PJ(_perso);
    }

    @Synchronized
    private fun objeto_Usar(packet: String) {
        try {
            if (!personaje!!.getRestriccionA(Personaje.RA_PUEDE_USAR_OBJETOS)) {
                ENVIAR_BN_NADA(personaje, "OBJETO USAR NO PUEDES USAR OBJETOS")
                return
            }
            if (personaje!!.estaFullOcupado()) {
                ENVIAR_BN_NADA(personaje, "OBJETO FULL OCUPADO")
                return
            }
            var idObjeto = -1
            var idObjetivo = -1
            var idCelda: Short = -1
            var pjObjetivo: Personaje? = null
            val infos = packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()
            try {
                idObjeto = infos[0].toInt()
            } catch (ignored: Exception) {
            }
            try {
                idObjetivo = infos[1].toInt()
            } catch (ignored: Exception) {
            }
            try {
                idCelda = infos[2].toShort()
            } catch (ignored: Exception) {
            }
            val objeto = personaje!!.getObjeto(idObjeto)
            if (objeto == null) {
                ENVIAR_Im_INFORMACION(personaje!!, "1OBJECT_DONT_EXIST")
                return
            }
            pjObjetivo = Mundo.getPersonaje(idObjetivo)
            if (pjObjetivo == null) {
                pjObjetivo = personaje
            }
            if (pjObjetivo!!.mapa != personaje!!.mapa) {
                return
            }
            val objModelo = objeto.objModelo ?: return
            if (objModelo.id == AtlantaMain.ID_MIMOBIONTE) {
                ENVIAR_ÑM_PANEL_MIMOBIONTE(personaje!!)
                return
            }
            val comestible = objModelo.tipo.toInt() == Constantes.OBJETO_TIPO_BEBIDA || objModelo
                .tipo.toInt() == Constantes.OBJETO_TIPO_POCION || objModelo.tipo.toInt() == Constantes.OBJETO_TIPO_PAN || objModelo
                .tipo.toInt() == Constantes.OBJETO_TIPO_CARNE_COMESTIBLE || objModelo
                .tipo.toInt() == Constantes.OBJETO_TIPO_PESCADO_COMESTIBLE
            if (!comestible && objModelo.nivel > personaje!!.nivel) {
                ENVIAR_Im_INFORMACION(personaje!!, "13")
                return
            }
            if (!(objModelo.id in 678..680) && !validaCondiciones(
                    personaje, objModelo
                        .condiciones
                )
            ) {
                ENVIAR_Im_INFORMACION(personaje!!, "119|43")
                return
            }
            if (personaje!!.pelea != null && (personaje!!.pelea.fase != Constantes.PELEA_FASE_POSICION || !comestible)) {
                ENVIAR_Im_INFORMACION(personaje!!, "191")
            } else if (!validaCondiciones(personaje, objModelo.condiciones)) {
                ENVIAR_Im_INFORMACION(personaje!!, "119|43")
            } else {
                objModelo.aplicarAccion(personaje!!, pjObjetivo, idObjeto, idCelda)
                val tt = intArrayOf(MisionObjetivoModelo.UTILIZAR_OBJETO.toInt())
                personaje!!.verificarMisionesTipo(tt, null, false, objModelo.id)
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje)
        }
    }

    private fun objeto_Mover_2(objetoID: Int, posAMover: Byte, cantObjMover: Int): Int {
        var cantObjMover = cantObjMover
        var r = -1
        val objMover = personaje!!.getObjeto(objetoID)
        if (objMover == null || cantObjMover < 1) {
            ENVIAR_BN_NADA(personaje, "OM2")
            return r
        }
        val posAnt = objMover.posicion
        if (posAnt == posAMover) { // misma posicion a mover
            ENVIAR_BN_NADA(personaje, "OM3")
            return r
        }
        val objMoverMod = objMover.objModelo ?: return r
        if (posAMover != Constantes.OBJETO_POS_NO_EQUIPADO && objMoverMod
                .tipo.toInt() == Constantes.OBJETO_TIPO_OBJETO_DE_BUSQUEDA
        ) {
            ENVIAR_BN_NADA(personaje, "OM4")
            return r
        }
        if (Constantes.esPosicionEquipamiento(posAMover)) {
            if (objMoverMod.nivel > personaje!!.nivel) {
                ENVIAR_Im_INFORMACION(personaje!!, "13")
                return r
            }
            if (!validaCondiciones(personaje, objMoverMod.condiciones)) {
                ENVIAR_Im_INFORMACION(personaje!!, "119|43")
                return r
            }
            if (personaje!!.puedeEquiparRepetido(objMoverMod, 1)) {
                ENVIAR_BN_NADA(personaje, "OM5")
                return r
            }
            if (objMover.encarnacion != null && !personaje!!.sePuedePonerEncarnacion()) {
                ENVIAR_BN_NADA(personaje, "60 SEG PARA ENCARNAR")
                return r
            }
            cantObjMover = 1
        } else if (cantObjMover > objMover.cantidad) {
            cantObjMover = objMover.cantidad
        }
        if (posAMover == Constantes.OBJETO_POS_MASCOTA) { // posicion de mascota
            if (objMoverMod.tipo.toInt() != Constantes.OBJETO_TIPO_MASCOTA) { // alimentar a mascota
                return objeto_Alimentar_Mascota(objMover, personaje!!.getObjPosicion(Constantes.OBJETO_POS_MASCOTA))
            }
            if (personaje!!.estaMontando()) {
                personaje!!.subirBajarMontura(false)
            }
        }
        if (posAMover == Constantes.OBJETO_POS_MONTURA && personaje!!.montura != null) {
            return objeto_Alimentar_Montura(objMoverMod, cantObjMover, objetoID)
        }
        if (!Constantes.esUbicacionValidaObjeto(objMoverMod.tipo.toInt(), posAMover.toInt())) {
            ENVIAR_BN_NADA(personaje, "OM6")
            return r
        }
        if (objMoverMod.tipo.toInt() == Constantes.OBJETO_TIPO_OBJEVIVO) { // si es objevivo
            return objeto_Equipar_Objevivo(objMover, posAMover)
        }
        r = 0
        // se crea un nuevo objeto con la cantidad restante (para no repetirlo despues)
        val nuevaCantidad = objMover.cantidad - cantObjMover
        if (nuevaCantidad >= 1) {
            val nuevoObj = objMover.clonarObjeto(nuevaCantidad, posAnt)
            personaje!!.addObjetoConOAKO(nuevoObj, true)
            objMover.cantidad = cantObjMover
            ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(personaje!!, objMover)
            personaje!!.actualizarSetsRapidos(objMover.id, nuevoObj.id, objMover.posicion, posAMover)
        }
        if (objMoverMod.tipo.toInt() != Constantes.OBJETO_TIPO_ESPECIALES && objMoverMod
                .tipo.toInt() != Constantes.OBJETO_TIPO_POCION_FORJAMAGIA
        ) {
            if (posAMover == Constantes.OBJETO_POS_ESCUDO) { // pos a mover es escudo
                objeto_Quitar_Arma_Dos_Manos(objMover)
            } else if (posAMover == Constantes.OBJETO_POS_ARMA) { // pos a mover es arma
                if (objMoverMod.esDosManos()) {
                    objeto_Quitar_Escudo_Para_Arma(objMover)
                }
            }
        }
        val exObj = personaje!!.getObjPosicion(posAMover)
        if (exObj != null) { // el objeto q habia en la posicion a mover
            if (objMoverMod.tipo.toInt() == Constantes.OBJETO_TIPO_ESPECIALES || objMoverMod
                    .tipo.toInt() == Constantes.OBJETO_TIPO_POCION_FORJAMAGIA
            ) { // convertir perfecto, si es lupa o pocima de FM
                objeto_Maguear_O_Lupear(exObj, posAMover, objMover)
                return r
            } else if (Constantes.esPosicionEquipamiento(posAMover)) { // no es del tipo especial o pocima fm (cuando se mueve a una pos equipamiento)
                val identInvExObj = personaje!!.getObjIdentInventario(exObj, objMover)
                if (identInvExObj != null) {
                    identInvExObj.cantidad = identInvExObj.cantidad + exObj.cantidad
                    ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(personaje!!, identInvExObj)
                    personaje!!.borrarOEliminarConOR(exObj.id, true)
                    personaje!!.actualizarSetsRapidos(
                        exObj.id, identInvExObj.id, exObj.posicion, identInvExObj
                            .posicion
                    )
                } else { // mueve el exobjeto al inventario
                    exObj.setPosicion(Constantes.OBJETO_POS_NO_EQUIPADO, personaje, false)
                }
                objMover.setPosicion(posAMover, personaje, false)
                if (exObj.objModelo?.setID ?: 0 > 0) {
                    exObj.objModelo?.setID?.let { ENVIAR_OS_BONUS_SET(personaje!!, it, -1) }
                }
            } else { // posiciones donde se pone caramelo, panes y otros
                if (objMover.objModeloID == exObj.objModeloID && objMover.sonStatsIguales(exObj)) {
                    exObj.cantidad = cantObjMover + exObj.cantidad
                    ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(personaje!!, exObj)
                    personaje!!.borrarOEliminarConOR(objMover.id, true)
                    personaje!!.actualizarSetsRapidos(objMover.id, exObj.id, objMover.posicion, exObj.posicion)
                } else {
                    ENVIAR_BN_NADA(personaje, "OM PENDEJADA")
                }
                return r
            }
        } else { // si no habia un objeto donde queremos mover
            if (objMoverMod.tipo.toInt() == Constantes.OBJETO_TIPO_ESPECIALES || objMoverMod
                    .tipo.toInt() == Constantes.OBJETO_TIPO_POCION_FORJAMAGIA
            ) { // no equipables
                ENVIAR_BN_NADA(personaje, "OM8")
                return r
            }
            val identicoInv: Objeto? = personaje!!.getObjIdentInventario(objMover, null)
            if (posAMover == Constantes.OBJETO_POS_NO_EQUIPADO && identicoInv != null) { // mover a NO EQUIPADO y hay otro objeto identico
                personaje!!.borrarOEliminarConOR(objMover.id, true)
                identicoInv.cantidad = identicoInv.cantidad + cantObjMover
                ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(personaje!!, identicoInv)
                personaje!!.actualizarSetsRapidos(
                    objMover.id, identicoInv.id, objMover.posicion, identicoInv
                        .posicion
                )
            } else {
                objMover.setPosicion(posAMover, personaje, false)
            }
        }
        // para los oficios
        if (posAMover == Constantes.OBJETO_POS_ARMA) {
            objeto_Refrescar_Oficio_Por_Herramienta(objMoverMod.id)
        } else if (posAMover == Constantes.OBJETO_POS_NO_EQUIPADO && posAnt == Constantes.OBJETO_POS_ARMA) {
            personaje!!.packetModoInvitarTaller(null, true)
        }
        // rectifica la cantidad de objetos por set
        if (objMoverMod.setID > 0) {
            ENVIAR_OS_BONUS_SET(personaje!!, objMoverMod.setID, -1)
        }
        if (objMover.stats.tieneStatTexto(Constantes.STAT_TITULO)
            || objMover.stats.tieneStatID(Constantes.STAT_CAMBIA_APARIENCIA_2)
            || objMover.stats.tieneStatID(Constantes.STAT_CAMBIA_APARIENCIA)
            || objMover.stats.tieneStatID(Constantes.STAT_MAS_VELOCIDAD)
            || objMover.stats.tieneStatID(Constantes.STAT_AURA)
            || objMover.stats.tieneStatID(Constantes.STAT_PERSONAJE_SEGUIDOR)
            || objMover.stats.tieneStatID(Constantes.STAT_MAS_PORC_PP)
        ) {
            personaje!!.refrescarEnMapa()
        }
        if (Constantes.esPosicionEquipamiento(posAMover) || posAMover == Constantes.OBJETO_POS_NO_EQUIPADO && Constantes
                .esPosicionEquipamiento(posAnt)
        ) {
            r = 1
        }
        // solo cambios visuales
        if (Constantes.esPosicionVisual(posAMover) || posAMover == Constantes.OBJETO_POS_NO_EQUIPADO && Constantes
                .esPosicionVisual(posAnt)
        ) {
            r = 2
        }
        return r
    }

    @Synchronized
    fun objeto_Mover(packet: String) { // al mover se actualizan los stats objetos
        try {
            if (personaje!!.pelea != null) {
                if (personaje!!.pelea.fase != Constantes.PELEA_FASE_POSICION || personaje!!.pelea
                        .tipoPelea == Constantes.PELEA_TIPO_KOLISEO.toInt()
                ) {
                    ENVIAR_BN_NADA(personaje, "OM1")
                    return
                }
            }
            var cambio = 0
            val subPacket = packet.substring(2).split("\n".toRegex()).toTypedArray()[0]
            for (s in subPacket.split(Pattern.quote("*").toRegex()).toTypedArray()) {
                val infos = s.split(Pattern.quote("|").toRegex()).toTypedArray()
                val objetoID = infos[0].toInt()
                val posAMover = infos[1].toByte()
                var cantObjMover = 1
                try {
                    if (infos.size > 2) {
                        cantObjMover = infos[2].toInt()
                    }
                } catch (ignored: Exception) {
                }
                cambio = max(objeto_Mover_2(objetoID, posAMover, cantObjMover), cambio)
                //				Thread.sleep(100);
            }
            if (cambio >= 1) {
                personaje!!.refrescarStuff(true, true, cambio >= 2)
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje, "OM9")
            redactarLogServidorln("EXCEPTION Packet $packet, objeto_Mover $e")
            e.printStackTrace()
        }
    }

    private fun objeto_Equipar_Set(set: SetRapido): Int {
        var r = -1
        try {
            if (personaje!!.pelea != null) {
                if (personaje!!.pelea.fase != Constantes.PELEA_FASE_POSICION || personaje!!.pelea
                        .tipoPelea == Constantes.PELEA_TIPO_KOLISEO.toInt()
                ) {
                    ENVIAR_BN_NADA(personaje, "EQUIPAR TODO EN PELEA")
                    return r
                }
            }
            val orden = byteArrayOf(
                Constantes.OBJETO_POS_DOFUS1, Constantes.OBJETO_POS_DOFUS2, Constantes.OBJETO_POS_DOFUS3,
                Constantes.OBJETO_POS_DOFUS4, Constantes.OBJETO_POS_DOFUS5, Constantes.OBJETO_POS_DOFUS6,
                Constantes.OBJETO_POS_COMPAÑERO, Constantes.OBJETO_POS_MASCOTA, Constantes.OBJETO_POS_ANILLO1,
                Constantes.OBJETO_POS_ANILLO_DERECHO, Constantes.OBJETO_POS_BOTAS, Constantes.OBJETO_POS_CINTURON,
                Constantes.OBJETO_POS_AMULETO, Constantes.OBJETO_POS_SOMBRERO, Constantes.OBJETO_POS_CAPA,
                Constantes.OBJETO_POS_ESCUDO, Constantes.OBJETO_POS_ARMA
            )
            for (i in orden) {
                val idObjeto = set.objetos[i.toInt()]
                if (idObjeto <= 0) {
                    continue
                }
                r = max(r, objeto_Mover_2(idObjeto, i, 1))
                //				Thread.sleep(100);
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje, "EQUIPAR TODO EXCEPTION")
            e.printStackTrace()
        }
        return r
    }

    private fun objeto_Desequipar_Set(): Int {
        var r = -1
        try {
            if (personaje!!.pelea != null) {
                if (personaje!!.pelea.fase != Constantes.PELEA_FASE_POSICION || personaje!!.pelea
                        .tipoPelea == Constantes.PELEA_TIPO_KOLISEO.toInt()
                ) {
                    ENVIAR_BN_NADA(personaje, "DESEQUIPAR TODO EN PELEA")
                    return r
                }
            }
            val orden = byteArrayOf(
                Constantes.OBJETO_POS_DOFUS1, Constantes.OBJETO_POS_DOFUS2, Constantes.OBJETO_POS_DOFUS3,
                Constantes.OBJETO_POS_DOFUS4, Constantes.OBJETO_POS_DOFUS5, Constantes.OBJETO_POS_DOFUS6,
                Constantes.OBJETO_POS_COMPAÑERO, Constantes.OBJETO_POS_MASCOTA, Constantes.OBJETO_POS_ANILLO1,
                Constantes.OBJETO_POS_ANILLO_DERECHO, Constantes.OBJETO_POS_BOTAS, Constantes.OBJETO_POS_CINTURON,
                Constantes.OBJETO_POS_AMULETO, Constantes.OBJETO_POS_SOMBRERO, Constantes.OBJETO_POS_CAPA,
                Constantes.OBJETO_POS_ESCUDO, Constantes.OBJETO_POS_ARMA
            )
            for (i in orden) {
                val objeto = personaje!!.getObjPosicion(i) ?: continue
                r = max(r, objeto_Mover_2(objeto.id, Constantes.OBJETO_POS_NO_EQUIPADO, 1))
                //				Thread.sleep(100);
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje, "DESEQUIPAR TODO EXCEPTION")
            e.printStackTrace()
        }
        return r
    }

    private fun objeto_String_Set_Equipado(): String {
        val str = StringBuilder()
        val cond = StringBuilder()
        val orden = byteArrayOf(
            Constantes.OBJETO_POS_DOFUS1, Constantes.OBJETO_POS_DOFUS2, Constantes.OBJETO_POS_DOFUS3,
            Constantes.OBJETO_POS_DOFUS4, Constantes.OBJETO_POS_DOFUS5, Constantes.OBJETO_POS_DOFUS6,
            Constantes.OBJETO_POS_COMPAÑERO, Constantes.OBJETO_POS_MASCOTA, Constantes.OBJETO_POS_ANILLO1,
            Constantes.OBJETO_POS_ANILLO_DERECHO, Constantes.OBJETO_POS_BOTAS, Constantes.OBJETO_POS_CINTURON,
            Constantes.OBJETO_POS_AMULETO, Constantes.OBJETO_POS_SOMBRERO, Constantes.OBJETO_POS_CAPA,
            Constantes.OBJETO_POS_ESCUDO, Constantes.OBJETO_POS_ARMA
        )
        for (i in orden) {
            val obj = personaje!!.getObjPosicion(i) ?: continue
            if (obj.objModelo?.condiciones?.isEmpty() ?: continue) {
                if (str.isNotEmpty()) {
                    str.append(";")
                }
                str.append(obj.id).append(",").append(obj.posicion.toInt())
            } else {
                if (cond.isNotEmpty()) {
                    cond.append(";")
                }
                cond.append(obj.id).append(",").append(obj.posicion.toInt())
            }
        }
        if (cond.isNotEmpty()) {
            if (str.isNotEmpty()) {
                str.append(";")
            }
            str.append(cond.toString())
        }
        return str.toString()
    }

    private fun objeto_Refrescar_Oficio_Por_Herramienta(idObjModelo: Int) {
        personaje!!.verificarHerramientOficio()
    }

    private fun objeto_Quitar_Escudo_Para_Arma(objMover: Objeto) {
        val escudo = personaje!!.getObjPosicion(Constantes.OBJETO_POS_ESCUDO) // escudo
        if (escudo != null) {
            val identInvExObj = personaje!!.getObjIdentInventario(escudo, objMover)
            if (identInvExObj != null) { // el objeto
                identInvExObj.cantidad = identInvExObj.cantidad + escudo.cantidad
                ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(personaje!!, identInvExObj)
                personaje!!.borrarOEliminarConOR(escudo.id, true)
                personaje!!.actualizarSetsRapidos(
                    escudo.id, identInvExObj.id, escudo.posicion, identInvExObj
                        .posicion
                )
            } else {
                escudo.setPosicion(Constantes.OBJETO_POS_NO_EQUIPADO, personaje, false)
            }
            if (escudo.objModelo?.setID ?: 0 > 0) {
                escudo.objModelo?.setID?.let { ENVIAR_OS_BONUS_SET(personaje!!, it, -1) }
            }
            ENVIAR_Im_INFORMACION(personaje!!, "079")
        }
    }

    private fun objeto_Quitar_Arma_Dos_Manos(objMover: Objeto) {
        val arma = personaje!!.getObjPosicion(Constantes.OBJETO_POS_ARMA) // arma
        if (arma != null && arma.objModelo?.esDosManos() == true) { // arma 2 manos
            val identicoArma = personaje!!.getObjIdentInventario(arma, objMover)
            if (identicoArma != null) { // el objeto
                identicoArma.cantidad = identicoArma.cantidad + arma.cantidad
                ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(personaje!!, identicoArma)
                personaje!!.borrarOEliminarConOR(arma.id, true)
                personaje!!.actualizarSetsRapidos(
                    arma.id, identicoArma.id, arma.posicion, identicoArma
                        .posicion
                )
            } else {
                arma.setPosicion(Constantes.OBJETO_POS_NO_EQUIPADO, personaje, false)
            }
            if (arma.objModelo?.setID ?: 0 > 0) {
                arma.objModelo?.setID?.let { ENVIAR_OS_BONUS_SET(personaje!!, it, -1) }
            }
            ENVIAR_Im_INFORMACION(personaje!!, "078")
        }
    }

    private fun objeto_Alimentar_Montura(objMoverMod: ObjetoModelo, cantObjMover: Int, idObjMover: Int): Int {
        if (Constantes.esAlimentoMontura(objMoverMod.tipo)) {
            personaje!!.restarCantObjOEliminar(idObjMover, cantObjMover, true)
            personaje!!.montura.aumentarEnergia(objMoverMod.nivel + 10, cantObjMover)
            ENVIAR_Re_DETALLES_MONTURA(personaje!!, "+", personaje!!.montura)
            ENVIAR_Im_INFORMACION(personaje!!, "0105")
            return 0
        } else {
            ENVIAR_Im_INFORMACION(personaje!!, "190")
        }
        return -1
    }

    private fun objeto_Equipar_Objevivo(objevivo: Objeto, posAMover: Byte): Int {
        val r = -1
        try {
            if (personaje!!.estaDisponible(true, true)) {
                ENVIAR_BN_NADA(personaje)
                return r
            }
            val objeto = personaje!!.getObjPosicion(posAMover)
            if (objeto == null) {
                ENVIAR_Im_INFORMACION(personaje!!, "1161")
                return r
            }
            val tipoObj = objeto.objModelo?.tipo?.toInt()
            val tipoVivo = objevivo.getParamStatTexto(Constantes.STAT_REAL_TIPO, 3).toInt(16)
            var paso = false
            when (tipoVivo) {
                Constantes.OBJETO_TIPO_CAPA -> paso = tipoObj == tipoVivo || tipoObj == Constantes.OBJETO_TIPO_MOCHILA
                Constantes.OBJETO_TIPO_SOMBRERO, Constantes.OBJETO_TIPO_AMULETO, Constantes.OBJETO_TIPO_BOTAS, Constantes.OBJETO_TIPO_CINTURON, Constantes.OBJETO_TIPO_ANILLO -> paso =
                    tipoObj == tipoVivo
            }
            if (!paso) {
                return r
            }
            if (objeto.tieneStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE)) {
                ENVIAR_Im_INFORMACION(personaje!!, "1162")
                return r
            }
            if (objeto.objevivoID > 0 || objeto.tieneStatTexto(Constantes.STAT_TURNOS)) {
                ENVIAR_BN_NADA(personaje, "EQUIPAR OBJEVIVO TIENE OBJEVIVO")
                return r
            }
            val nuevaCantidad = objevivo.cantidad - 1
            if (nuevaCantidad >= 1) {
                val nuevoObj = objevivo.clonarObjeto(nuevaCantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                personaje!!.addObjetoConOAKO(nuevoObj, true)
                objevivo.cantidad = 1
            }
            objevivo.addStatTexto(Constantes.STAT_REAL_GFX, "0#0#" + Integer.toHexString(objevivo.objModeloID))
            objeto.setIDObjevivo(objevivo.id)
            personaje!!.borrarOEliminarConOR(objevivo.id, false)
            ENVIAR_OCK_ACTUALIZA_OBJETO(personaje!!, objeto)
            // lo salva porq despues ya no lo tendra en la lista de items
            SALVAR_OBJETO(objevivo)
            if (Constantes.esPosicionVisual(objeto.posicion)) {
                personaje!!.cambiarRopaVisual()
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje)
        }
        return r
    }

    private fun objeto_Maguear_O_Lupear(exObj: Objeto, posAMover: Byte, objMover: Objeto) {
        if (exObj.objevivoID != 0 || exObj.cantidad > 1) {
            ENVIAR_BN_NADA(personaje, "OM7")
            return
        }
        val objMoverMod = objMover.objModelo ?: return
        if (objMoverMod.tipo.toInt() == Constantes.OBJETO_TIPO_ESPECIALES) {
            if (!exObj.convertirPerfecto(objMoverMod.nivel.toInt())) {
                ENVIAR_Im_INFORMACION(personaje!!, "1OBJETO_NO_NECESITA_MEJORAS")
                return
            }
        } else if (!AtlantaMain.MODO_ANKALIKE && objMoverMod.tipo.toInt() == Constantes.OBJETO_TIPO_POCION_FORJAMAGIA && posAMover == Constantes.OBJETO_POS_ARMA) { // cambiar daño elemental
            val statFM = Constantes.getStatPorRunaPocima(objMover)
            val potenciaFM = Constantes.getValorPorRunaPocima(objMover)
            exObj.forjaMagiaGanar(statFM, potenciaFM)
            ENVIAR_IO_ICONO_OBJ_INTERACTIVO(personaje!!.mapa, personaje!!.Id, "+" + objMoverMod.id)
            ENVIAR_Im_INFORMACION(personaje!!, "1OBJETO_CAMBIO_DAÑO_ELEMENTAL")
        }
        if (objMover.addDurabilidad(-1)) {
            personaje!!.borrarOEliminarConOR(objMover.id, true)
        } else {
            ENVIAR_OCK_ACTUALIZA_OBJETO(personaje!!, objMover)
        }
        ENVIAR_OCK_ACTUALIZA_OBJETO(personaje!!, exObj)
        ENVIAR_As_STATS_DEL_PJ(personaje!!)
        SALVAR_OBJETO(exObj)
    }

    private fun objeto_Apariencia_Objevivo(packet: String) {
        try {
            val split = packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()
            val objeto = Mundo.getObjeto(split[0].toInt())
            var objevivo: Objeto? = null
            if (objeto != null) {
                objevivo = if (objeto.objModelo?.tipo?.toInt() == Constantes.OBJETO_TIPO_OBJEVIVO) {
                    objeto
                } else {
                    Mundo.getObjeto(objeto.objevivoID)
                }
            }
            val exp = objevivo!!.getParamStatTexto(Constantes.STAT_EXP_OBJEVIVO, 3).toInt(16)
            var skin = split[2].toInt()
            val nivel = Constantes.getNivelObjevivo(exp)
            if (skin > nivel) {
                skin = nivel
            }
            if (skin < 1) {
                skin = 1
            }
            objevivo.addStatTexto(Constantes.STAT_SKIN_OBJEVIVO, "0#0#" + Integer.toHexString(skin))
            SALVAR_OBJETO(objevivo)
            ENVIAR_OCK_ACTUALIZA_OBJETO(personaje!!, objeto)
            if (objeto != null) {
                if (Constantes.esPosicionVisual(objeto.posicion)) {
                    personaje!!.cambiarRopaVisual()
                }
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje)
        }
    }

    private fun objeto_Alimentar_Objevivo(packet: String) {
        try {
            if (personaje!!.estaDisponible(true, true)) {
                ENVIAR_BN_NADA(personaje)
                return
            }
            val objetoObj =
                personaje!!.getObjeto(packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()[0].toInt())
            val idObjAlimento = packet.split(Pattern.quote("|").toRegex()).toTypedArray()[2].toInt()
            if (objetoObj.posicion == Constantes.OBJETO_POS_NO_EQUIPADO || !personaje!!.tieneObjetoID(idObjAlimento)) {
                ENVIAR_BN_NADA(personaje)
                return
            }
            val objevivo = Mundo.getObjeto(objetoObj.objevivoID)
            if (objevivo != null) {
                objevivo.addStatTexto(Constantes.STAT_SE_HA_COMIDO_EL, getStatSegunFecha(Calendar.getInstance()))
            } // comida
            val expActual = objevivo?.getParamStatTexto(Constantes.STAT_EXP_OBJEVIVO, 3)?.toInt(16)
            val expAdicional =
                personaje!!.getObjeto(idObjAlimento).objModelo?.nivel?.div(5.toDouble())?.let { ceil(it).toInt() } ?: 0
            if (objevivo != null) {
                if (expActual != null) {
                    objevivo.addStatTexto(
                        Constantes.STAT_EXP_OBJEVIVO,
                        "0#0#" + Integer.toHexString(expActual + expAdicional)
                    )
                }
            }
            personaje!!.restarCantObjOEliminar(idObjAlimento, 1, true)
            ENVIAR_OCK_ACTUALIZA_OBJETO(personaje!!, objetoObj)
            ENVIAR_Ow_PODS_DEL_PJ(personaje!!)
            if (objevivo != null) {
                SALVAR_OBJETO(objevivo)
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje, "ALIMENTAR OBJEVIVO")
        }
    }

    private fun objeto_Desequipar_Objevivo(packet: String) {
        try {
            if (personaje!!.estaDisponible(true, true)) {
                ENVIAR_BN_NADA(personaje)
                return
            }
            val objeto =
                personaje!!.getObjeto(packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()[0].toInt())
            val objevivo = Mundo.getObjeto(objeto.objevivoID)
            if (objevivo != null) {
                objevivo.addStatTexto(Constantes.STAT_REAL_GFX, "0#0#0")
                personaje!!.addObjetoConOAKO(objevivo, true)
                SALVAR_OBJETO(objevivo)
            }
            objeto.setIDObjevivo(0)
            ENVIAR_OCK_ACTUALIZA_OBJETO(personaje!!, objeto)
            if (Constantes.esPosicionVisual(objeto.posicion)) {
                personaje!!.cambiarRopaVisual()
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje)
        }
    }

    private fun objeto_Desasociar_Mimobionte(packet: String) {
        try {
            if (personaje!!.estaDisponible(true, true)) {
                ENVIAR_BN_NADA(personaje)
                return
            }
            val objeto = personaje!!.getObjeto(packet.substring(2).toInt())
            if (objeto == null) {
                ENVIAR_BN_NADA(personaje)
                return
            }
            objeto.addStatTexto(Constantes.STAT_APARIENCIA_OBJETO, "")
            ENVIAR_OCK_ACTUALIZA_OBJETO(personaje!!, objeto)
            if (Constantes.esPosicionVisual(objeto.posicion)) {
                personaje!!.cambiarRopaVisual()
            }
        } catch (ignored: Exception) {
        }
    }

    private fun objeto_Alimentar_Mascota(comida: Objeto, mascObj: Objeto): Int {
        val r = -1
        try {
            val mascota = Mundo.getMascotaModelo(mascObj.objModeloID)
            if (comida.objModeloID == 2239) { // polvo de aniripsa
                val pdv = mascObj.pDV
                if (pdv >= 10 && mascObj.objModeloID != 10802 && mascObj.objModeloID != 10866) {
                    ENVIAR_BN_NADA(personaje)
                    return r
                }
                mascObj.addStatTexto(Constantes.STAT_PUNTOS_VIDA, "0#0#" + Integer.toHexString(pdv + 1))
            } else if (mascota != null) {
                if (AtlantaMain.PARAM_ALIMENTAR_MASCOTAS && !mascota.esDevoradorAlmas() && mascota.getComida(
                        comida
                            .objModeloID
                    ) != null
                ) {
                    if (mascObj.horaComer(false, Constantes.CORPULENCIA_NORMAL.toInt())) {
                        mascObj.comerComida(comida.objModeloID)
                        ENVIAR_Im_INFORMACION(personaje!!, "032")
                        ENVIAR_As_STATS_DEL_PJ(personaje!!)
                    } else {
                        val corpulencia = mascObj.corpulencia
                        mascObj.corpulencia = Constantes.CORPULENCIA_OBESO.toInt()
                        if (corpulencia == Constantes.CORPULENCIA_OBESO.toInt()) {
                            personaje!!.restarVidaMascota(mascObj)
                        }
                        ENVIAR_Im_INFORMACION(personaje!!, "026")
                    }
                } else {
                    ENVIAR_Im_INFORMACION(personaje!!, "153")
                    return r
                }
            }
            personaje!!.restarCantObjOEliminar(comida.id, 1, true)
            ENVIAR_OCK_ACTUALIZA_OBJETO(personaje!!, mascObj)
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje)
        }
        return r
    }

    private fun analizar_Dialogos(packet: String) {
        if (packet.length < 2) {
            try {
                ENVIAR_BN_NADA(personaje)
                personaje!!.enviarmensajeNegro("Por Favor repita su accion, El servidor ha perdido los datos en el camino")
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        when (packet[1]) {
            'C' -> dialogo_Iniciar(packet)
            'B' -> {
            }
            'R' -> dialogo_Respuesta(packet)
            'V' -> personaje!!.dialogoFin()
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR DIALOGOS: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "analizar_Dialogos()")
                }
            }
        }
    }

    private fun dialogo_Iniciar(packet: String) {
        try {
            val id = packet.substring(2).split("\n".toRegex()).toTypedArray()[0].toInt()
            personaje!!.conversandoCon = id
            if (id > -100) {
                val tt = intArrayOf(
                    MisionObjetivoModelo.HABLAR_CON_NPC.toInt(), MisionObjetivoModelo.VOLVER_VER_NPC.toInt(),
                    MisionObjetivoModelo.ENSEÑAR_OBJETO_NPC.toInt(), MisionObjetivoModelo.ENTREGAR_OBJETO_NPC.toInt()
                )
                personaje!!.verificarMisionesTipo(tt, null, false, 0)
            }
            var preguntador: Preguntador? = personaje
            var preguntaID = 0
            val npc = personaje!!.mapa.getNPC(id)
            if (npc != null) {
                preguntaID = npc.getPreguntaID(personaje)
            } else {
                val recau = personaje!!.mapa.recaudador
                if (recau != null) {
                    if (recau.id == id) {
                        preguntaID = 1
                        preguntador = recau
                    }
                }
            }
            if (preguntaID == 0) {
                personaje!!.dialogoFin()
                return
            }
            var pregunta = Mundo.getPreguntaNPC(preguntaID)
            if (pregunta == null) {
                pregunta = PreguntaNPC(preguntaID, "", "", "")
                Mundo.addPreguntaNPC(pregunta)
            }
            val str = pregunta.stringArgParaDialogo(personaje!!, preguntador!!)
            ENVIAR_DCK_CREAR_DIALOGO(personaje!!, id)
            ENVIAR_DQ_DIALOGO_PREGUNTA(personaje!!, str)
            if (personaje!!.esMaestro()) {
                personaje!!.grupoParty.packetSeguirLider(packet)
            }
        } catch (e: Exception) {
            personaje!!.dialogoFin()
        }
    }

    private fun dialogo_Respuesta(packet: String) {
        val infos = packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()
        try {
            val preguntaID = infos[0].toInt()
            if (personaje!!.conversandoCon >= 0 || personaje!!.preguntaID == 0 || preguntaID != personaje!!.preguntaID) {
                personaje!!.dialogoFin()
                return
            }
            val respuestaID = infos[1].toInt()
            val pregunta = Mundo.getPreguntaNPC(preguntaID)
            val respuesta = Mundo.getRespuestaNPC(respuestaID)
            if (pregunta != null) {
                if (pregunta.respuestas.contains(respuestaID)) {
                    if (respuesta != null) {
                        respuesta.aplicar(personaje!!)
                    }
                    if (personaje!!.preguntaID == 0) {
                        personaje!!.dialogoFin()
                    }
                } else {
                    personaje!!.dialogoFin()
                }
            }
            if (personaje!!.esMaestro()) {
                personaje!!.grupoParty.packetSeguirLider(packet)
            }
        } catch (e: Exception) {
            personaje!!.dialogoFin()
        }
    }

    fun analizar_Intercambios(packet: String) {
        if (packet.length < 2) {
            try {
                ENVIAR_BN_NADA(personaje)
                personaje!!.enviarmensajeNegro("Por Favor repita su accion, El servidor ha perdido los datos en el camino")
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        when (packet[1]) {
            'A' -> intercambio_Aceptar()
            'B' -> intercambio_Comprar(packet)
            'f' -> intercambio_Cercado(packet)
            'F' -> if (personaje!!.trabajo != null) {
                personaje!!.trabajo!!.mostrarProbabilidades(personaje!!)
            }
            'H' -> intercambio_Mercadillo(packet)
            'J' -> intercambio_Oficios(packet)
            'K' -> intercambio_Boton_OK()
            'L' -> intercambio_Repetir_Ult_Craft()
            'M' -> intercambio_Mover_Objeto(packet)
            'q' -> intercambio_Preg_Mercante()
            'P' -> intercambio_Pago_Por_Trabajo(packet)
            'Q' -> intercambio_Ok_Mercante()
            'r' -> intercambio_Establo(packet)
            'R' -> intercambio_Iniciar(packet)
            'S' -> intercambio_Vender(packet)
            'V' -> intercambio_Cerrar()
            'W' -> intercambio_Oficio_Publico(packet)
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR INTERCAMBIOS: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "analizar_Intercambios()")
                }
            }
        }
    }

    @Synchronized
    private fun intercambio_Iniciar(packet: String) {
        try {
            val split = packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()
            var tipo: Byte = -1
            try {
                tipo = split[0].toByte()
            } catch (ignored: Exception) {
            }
            if ((personaje!!.tipoExchange == Constantes.INTERCAMBIO_TIPO_MERCADILLO_COMPRAR
                        && tipo == Constantes.INTERCAMBIO_TIPO_MERCADILLO_VENDER) || (personaje!!
                    .tipoExchange == Constantes.INTERCAMBIO_TIPO_MERCADILLO_VENDER
                        && tipo == Constantes.INTERCAMBIO_TIPO_MERCADILLO_COMPRAR)
            ) { // nada
            } else if (personaje!!.estaDisponible(true, true)) {
                ENVIAR_BN_NADA(personaje, "INTERCAMBIO NO ESTA DISPONIBLE")
                return
            }
            if (personaje!!.consultarCofre != null) {
                ENVIAR_BN_NADA(personaje, "INTERCAMBIO CONSULTAR COFRE")
                return
            }
            if (personaje!!.consultarCasa != null) {
                ENVIAR_BN_NADA(personaje, "INTERCAMBIO CONSULTAR CASA")
                return
            }
            when (tipo) {
                Constantes.INTERCAMBIO_TIPO_MERCADILLO_COMPRAR, Constantes.INTERCAMBIO_TIPO_MERCADILLO_VENDER -> {
                    if (personaje!!.deshonor >= 5) {
                        ENVIAR_Im_INFORMACION(personaje!!, "183")
                        ENVIAR_EV_CERRAR_VENTANAS(personaje!!, "")
                        return
                    }
                    if (personaje!!.exchanger != null) {
                        ENVIAR_EV_CERRAR_VENTANAS(personaje!!, "")
                    }
                    var mercadillo = Mundo.getPuestoPorMapa(personaje!!.mapa.id)
                    if (mercadillo == null) {
                        mercadillo = Mundo.getPuestoMercadillo(2)
//                        ENVIAR_BN_NADA(personaje)
//                        return
                    }
                    var tipoobj = mercadillo?.tipoObjPermitidos
                    if (mercadillo != null) {
                        if (mercadillo.iD == 2 && tipo == Constantes.INTERCAMBIO_TIPO_MERCADILLO_COMPRAR) {
                            tipoobj = ""
                            var c = 0
                            val tipos: ArrayList<Short>? = ArrayList()
                            for (obj in mercadillo.objetosMercadillos) {
                                val modelo = Mundo.getObjeto(obj.objetoID)?.objModelo?.tipo
                                if (!tipos!!.contains(modelo)) {
                                    if (modelo != null) {
                                        tipos.add(modelo)
                                    }
                                }
                            }
                            for (tipo in tipos!!) {
                                tipoobj += if (tipos.lastIndex != c) {
                                    tipo.toString() + ","
                                } else {
                                    tipo.toString()
                                }
                                c += 1
                            }
                        }
                    }
                    personaje!!.exchanger = mercadillo
                    if (mercadillo != null) {
                        ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(
                            personaje!!, tipo.toInt(), "1,10,100;" + tipoobj
                                    + ";" + mercadillo.porcentajeImpuesto + ";" + mercadillo.nivelMax + ";" + mercadillo
                                .maxObjCuenta + ";-1;" + mercadillo.tiempoVenta
                        )
                    }
                    if (tipo == Constantes.INTERCAMBIO_TIPO_MERCADILLO_VENDER) { // mercadillo vender
                        if (mercadillo != null) {
                            ENVIAR_EL_LISTA_EXCHANGER(personaje!!, mercadillo)
                        }
                    }
                }
                Constantes.INTERCAMBIO_TIPO_TALLER_ARTESANO -> {
                    val invitadoID = split[1].toInt()
                    val trabajoID = split[2].toInt()
                    var trabajo: Trabajo? = null
                    var paso = false
                    for (statOficio in personaje!!.statsOficios.values) {
                        if (statOficio.posicion.toInt() == 7) {
                            continue
                        }
                        for (t in statOficio.trabajosARealizar()) {
                            if (t.trabajoID != trabajoID) {
                                continue
                            }
                            trabajo = t
                            paso = true
                            break
                        }
                        if (paso) {
                            break
                        }
                    }
                    if (trabajo == null) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    val invitandoA = Mundo.getPersonaje(invitadoID)
                    if (invitandoA == null || invitandoA === personaje) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    if (!invitandoA.estaVisiblePara(personaje)) {
                        ENVIAR_Im_INFORMACION(personaje!!, "1209")
                        return
                    }
                    personaje!!.setInvitandoA(invitandoA, "taller")
                    invitandoA.setInvitador(personaje, "taller")
                    personaje!!.exchanger = trabajo
                    ENVIAR_ERK_CONSULTA_INTERCAMBIO(
                        personaje!!, personaje!!.Id, invitandoA.Id,
                        Constantes.INTERCAMBIO_TIPO_TALLER_ARTESANO.toInt()
                    )
                    ENVIAR_ERK_CONSULTA_INTERCAMBIO(
                        invitandoA, personaje!!.Id, invitandoA.Id,
                        Constantes.INTERCAMBIO_TIPO_TALLER_CLIENTE.toInt()
                    )
                }
                Constantes.INTERCAMBIO_TIPO_MONTURA -> {
                    if (personaje!!.montura == null) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    personaje!!.exchanger = personaje!!.montura
                    ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(personaje!!, tipo.toInt(), personaje!!.montura.id.toString() + "")
                    ENVIAR_EL_LISTA_EXCHANGER(personaje!!, personaje!!.montura)
                    ENVIAR_Ew_PODS_MONTURA(personaje!!)
                }
                Constantes.INTERCAMBIO_TIPO_TIENDA_NPC -> {
                    val npcID = split[1].toInt()
                    val npc = personaje!!.mapa.getNPC(npcID)
                    if (npc == null) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    personaje!!.exchanger = npc
                    ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(personaje!!, tipo.toInt(), npcID.toString() + "")
                    ENVIAR_EL_LISTA_EXCHANGER(personaje!!, npc)
                }
                Constantes.INTERCAMBIO_TIPO_PERSONAJE -> {
                    val objetidoID = split[1].toInt()
                    val invitandoA2 = Mundo.getPersonaje(objetidoID)
                    if (invitandoA2 == null || invitandoA2 === personaje || invitandoA2.mapa != personaje!!.mapa || !invitandoA2
                            .enLinea()
                    ) {
                        ENVIAR_ERE_ERROR_CONSULTA(personaje!!, 'E')
                        return
                    }
                    if (!invitandoA2.estaVisiblePara(personaje)) {
                        ENVIAR_Im_INFORMACION(personaje!!, "1209")
                        return
                    }
                    if (invitandoA2.estaDisponible(false, true)) {
                        ENVIAR_ERE_ERROR_CONSULTA(personaje!!, 'O')
                        return
                    }
                    personaje!!.setInvitandoA(invitandoA2, "intercambio")
                    invitandoA2.setInvitador(personaje, "intercambio")
                    ENVIAR_ERK_CONSULTA_INTERCAMBIO(
                        personaje!!, personaje!!.Id, invitandoA2.Id,
                        Constantes.INTERCAMBIO_TIPO_PERSONAJE.toInt()
                    )
                    ENVIAR_ERK_CONSULTA_INTERCAMBIO(
                        invitandoA2, personaje!!.Id, invitandoA2.Id,
                        Constantes.INTERCAMBIO_TIPO_PERSONAJE.toInt()
                    )
                    if (personaje!!.cuenta.actualIP == invitandoA2.cuenta.actualIP) {
                        val invitador = personaje!!
                        val intercambio = Intercambio(invitador, invitandoA2)
                        invitador.tipoExchange = Constantes.INTERCAMBIO_TIPO_PERSONAJE
                        invitandoA2.tipoExchange = Constantes.INTERCAMBIO_TIPO_PERSONAJE
                        invitador.exchanger = intercambio
                        invitandoA2.exchanger = intercambio
                        ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(invitador, Constantes.INTERCAMBIO_TIPO_PERSONAJE.toInt(), "")
                        ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(invitandoA2, Constantes.INTERCAMBIO_TIPO_PERSONAJE.toInt(), "")
                        invitandoA2.setInvitador(null, "")
                        invitador.setInvitandoA(null, "")
                        invitador.Exchangemismaip = invitandoA2
                        invitandoA2.Exchangemismaip = invitador
                    }
                }
                Constantes.INTERCAMBIO_TIPO_TRUEQUE, Constantes.INTERCAMBIO_TIPO_RESUCITAR_MASCOTA -> {
                    val npcID2 = split[1].toInt()
                    val npc2 = personaje!!.mapa.getNPC(npcID2)
                    if (npc2 == null) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    val trueque = Trueque(
                        personaje!!, tipo == Constantes.INTERCAMBIO_TIPO_RESUCITAR_MASCOTA, npc2
                            .modeloID
                    )
                    personaje!!.exchanger = trueque
                    ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(personaje!!, Constantes.INTERCAMBIO_TIPO_TRUEQUE.toInt(), "")
                }
                Constantes.INTERCAMBIO_TIPO_MERCANTE -> {
                    val mercanteID = split[1].toInt()
                    val mercante = Mundo.getPersonaje(mercanteID)
                    if (mercante == null) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    personaje!!.exchanger = mercante
                    ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(personaje!!, tipo.toInt(), mercanteID.toString() + "")
                    ENVIAR_EL_LISTA_EXCHANGER(personaje!!, mercante)
                }
                Constantes.INTERCAMBIO_TIPO_MI_TIENDA -> {
                    personaje!!.exchanger = personaje!!.tienda
                    ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(personaje!!, tipo.toInt(), personaje!!.Id.toString() + "")
                    ENVIAR_EL_LISTA_EXCHANGER(personaje!!, personaje!!)
                }
                Constantes.INTERCAMBIO_TIPO_RECAUDADOR -> {
                    val recaudaID = split[1].toInt()
                    val recaudador = Mundo.getRecaudador(recaudaID)
                    if (recaudador == null || recaudador.pelea != null || recaudador.enRecolecta || personaje!!
                            .gremio == null
                    ) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    if (recaudador.gremio!!.id != personaje!!.gremio.id) {
                        ENVIAR_BN_NADA(personaje, "NO ES DEL GREMIO")
                        return
                    }
                    if (personaje!!.miembroGremio.puede(Constantes.G_RECOLECTAR_RECAUDADOR)) {
                        ENVIAR_Im_INFORMACION(personaje!!, "1101")
                        return
                    }
                    recaudador.setEnRecolecta(true, personaje)
                    ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(personaje!!, tipo.toInt(), recaudador.id.toString() + "")
                    ENVIAR_EL_LISTA_EXCHANGER(personaje!!, recaudador)
                    personaje!!.exchanger = recaudador
                }
                else -> {
                    ENVIAR_BN_NADA(personaje)
                    return
                }
            }
            personaje!!.tipoExchange = tipo
        } catch (e: Exception) {
            redactarLogServidorln("EXCEPTION Packet $packet, intercambio_Iniciar $e")
            e.printStackTrace()
        }
    }

    @Synchronized
    private fun intercambio_Aceptar() {
        when (personaje!!.tipoInvitacion) {
            "taller" -> {
                val artesano = personaje!!.invitador
                if (artesano == null) {
                    ENVIAR_BN_NADA(personaje, "INTERCAMBIO ACEPTAR ARTESANO NULO")
                    return
                }
                val trabajo = artesano.getIntercambiandoCon(Trabajo::class.java) as Trabajo?
                if (trabajo == null) {
                    ENVIAR_BN_NADA(personaje, "INTERCAMBIO ACEPTAR TRABAJO NULO")
                    return
                }
                // final InvitarTaller taller = new InvitarTaller(artesano, _perso, trabajo);
                artesano.tipoExchange = Constantes.INTERCAMBIO_TIPO_TALLER_ARTESANO
                personaje!!.tipoExchange = Constantes.INTERCAMBIO_TIPO_TALLER_CLIENTE
                personaje!!.exchanger = trabajo
                ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(
                    artesano, Constantes.INTERCAMBIO_TIPO_TALLER_ARTESANO.toInt(), trabajo
                        .casillasMax.toString() + ";" + trabajo.trabajoID
                )
                ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(
                    personaje!!, Constantes.INTERCAMBIO_TIPO_TALLER_CLIENTE.toInt(), trabajo
                        .casillasMax.toString() + ";" + trabajo.trabajoID
                )
                personaje!!.setInvitador(null, "")
                artesano.setInvitandoA(null, "")
                trabajo.setArtesanoCliente(artesano, personaje)
            }
            "intercambio" -> {
                val invitador = personaje!!.invitador
                if (invitador == null) {
                    ENVIAR_BN_NADA(personaje, "INTERCAMBIO ACEPTAR INTERCAMBIO NULO")
                    return
                }
                val intercambio = Intercambio(invitador, personaje!!)
                invitador.tipoExchange = Constantes.INTERCAMBIO_TIPO_PERSONAJE
                personaje!!.tipoExchange = Constantes.INTERCAMBIO_TIPO_PERSONAJE
                invitador.exchanger = intercambio
                personaje!!.exchanger = intercambio
                ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(invitador, Constantes.INTERCAMBIO_TIPO_PERSONAJE.toInt(), "")
                ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(personaje!!, Constantes.INTERCAMBIO_TIPO_PERSONAJE.toInt(), "")
                personaje!!.setInvitador(null, "")
                invitador.setInvitandoA(null, "")
            }
        }
    }

    @Synchronized
    private fun intercambio_Cerrar() {
        personaje!!.cerrarExchange("")
    }

    @Synchronized
    private fun intercambio_Boton_OK() {
        if (personaje!!.exchanger != null) {
            personaje!!.exchanger.botonOK(personaje!!)
            try {
                val invitado = personaje!!.Exchangemismaip
                if (invitado != null) {
                    invitado.exchanger.botonOK(invitado)
                    invitado.Exchangemismaip = null
                }
                personaje!!.Exchangemismaip = null
            } catch (e: Exception) {
                redactarLogServidorln(e.toString())
            }
        }
    }

    private fun intercambio_Oficios(packet: String) {
        if (packet[2] == 'F') {
            val idOficio = packet.substring(3).toInt()
            for (artesano in Mundo.PERSONAJESONLINE) {
                val mapa = artesano.mapa
                for (oficio in artesano.statsOficios.values) {
                    if (oficio.libroArtesano && oficio.oficio.id == idOficio) {
                        ENVIAR_EJ_DESCRIPCION_LIBRO_ARTESANO(
                            personaje!!, "+" + oficio.oficio.id + ";"
                                    + artesano.Id + ";" + artesano.nombre + ";" + oficio.nivel + ";" + mapa.id + ";"
                                    + (if (mapa.trabajos!!.isEmpty()) 0 else 1) + ";" + artesano.getClaseID(true) + ";" + artesano.sexo
                                    + ";" + artesano.color1 + "," + artesano.color2 + "," + artesano.color3 + ";" + artesano
                                .stringAccesorios + ";" + oficio.opcionBin + "," + oficio.slotsPublico
                        )
                    }
                }
            }
            ENVIAR_BN_NADA(personaje)
        } else {
            redactarLogServidorln("$stringDesconocido ANALIZAR INTERCAMBIO OFICIO: $packet")
            if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                cerrarSocket(true, "intercambio_Oficios()")
            }
        }
    }

    private fun intercambio_Oficio_Publico(packet: String) {
        when (packet[2]) {
            '+' -> {
                ENVIAR_EW_OFICIO_MODO_PUBLICO(personaje!!, "+")
                personaje!!.packetModoInvitarTaller(null, false)
            }
            '-' -> {
                // for (StatsOficio SO : _perso.getStatsOficios().values()) {
// if (SO.getPosicion() != 7)
// GestorSalida.ENVIAR_Ej_AGREGAR_LIBRO_ARTESANO(_perso, "-" + SO.getOficio().getID());
// }
                ENVIAR_EW_OFICIO_MODO_PUBLICO(personaje!!, "-")
                ENVIAR_EW_OFICIO_MODO_INVITACION(personaje!!, "-", personaje!!.Id, "")
            }
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR INTER OFICIO PUBLICO: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "intercambio_Oficio_Publico()")
                }
            }
        }
    }

    private fun intercambio_Mover_Objeto(packet: String) {
        try {
            when (packet[2]) {
                'G' -> try {
                    var kamas: Long = 0
                    kamas = try {
                        packet.substring(3).toLong()
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(personaje, "KAMAS EXCEPTION")
                        return
                    }
                    if (kamas < 0) { // retirar
                        kamas = abs(kamas)
                        when (personaje!!.tipoExchange) {
                            Constantes.INTERCAMBIO_TIPO_RECAUDADOR, Constantes.INTERCAMBIO_TIPO_COFRE -> {
                                if (personaje!!.exchanger == null) {
                                    return
                                }
                                if (personaje!!.exchanger.kamas < kamas) {
                                    kamas = personaje!!.exchanger.kamas
                                }
                                personaje!!.exchanger.addKamas(-kamas, personaje)
                                personaje!!.addKamas(kamas, false, true)
                                ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(personaje!!, "G" + personaje!!.exchanger.kamas)
                            }
                        }
                    } else { // si kamas > 0
                        if (personaje!!.kamas < kamas) {
                            kamas = personaje!!.kamas
                        }
                        when (personaje!!.tipoExchange) {
                            Constantes.INTERCAMBIO_TIPO_RECAUDADOR, Constantes.INTERCAMBIO_TIPO_COFRE -> {
                                if (personaje!!.exchanger == null) {
                                    return
                                }
                                personaje!!.exchanger.addKamas(kamas, personaje)
                                personaje!!.addKamas(-kamas, false, true)
                                ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(personaje!!, "G" + personaje!!.exchanger.kamas)
                            }
                            Constantes.INTERCAMBIO_TIPO_PERSONAJE -> {
                                if (personaje!!.exchanger == null) {
                                    return
                                }
                                personaje!!.exchanger.addKamas(kamas, personaje)
                            }
                        }
                    }
                } catch (e: Exception) {
                    ENVIAR_BN_NADA(personaje, "INTERCAMBIO MOVER OBJETO KAMAS")
                    redactarLogServidorln(
                        "EXCEPTION Packet $packet, intercambio_Mover_Objeto(kamas) " + e
                            .toString()
                    )
                    e.printStackTrace()
                }
                'O' -> {
                    try {
                        val sp = packet.substring(3).replace("-", ";-").replace("+", ";+")
                        var varios = false
                        val split = sp.split(Pattern.quote(";").toRegex()).toTypedArray()
                        if (AtlantaMain.PARAM_MOVER_MULTIPLE_OBJETOS_SOLO_ABONADOS && personaje!!.cuenta.vip == 0) {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Reserve au V.I.P", "B9121B")
                            } else {
                                ENVIAR_Im_INFORMACION(personaje!!, "1ONLY_FOR_VIP")
                            }
                            return
                        }
                        var i = 0
                        loop@ for (sPacket in split) {
                            if (sPacket.isEmpty()) {
                                continue
                            }
                            //							if (varios) {
//								Thread.sleep(500);// es para evitar lag en packets
//							}
                            if (split.size > 2) varios = true
                            if (varios) personaje?.enviarmensajeRojo("Faltan ${split.size - ++i} segundos para terminar la operacion")
                            val infos = sPacket.substring(1).split(Pattern.quote("|").toRegex()).toTypedArray()
                            var id = -1
                            var cantidad = -1
                            var precio = 0
                            try {
                                id = infos[0].toInt()
                                cantidad = infos[1].toInt()
                            } catch (ignored: Exception) {
                            }
                            val objeto: Objeto = Mundo.getObjeto(id) ?: return
                            if (cantidad > objeto.cantidad) {
                                cantidad = objeto.cantidad
                            }
                            if (cantidad < 0 || objeto == null) {
                                ENVIAR_Im_INFORMACION(personaje!!, "1OBJECT_DONT_EXIST~$id")
                                continue
                            }
                            if (objeto.objModelo?.tipo?.toInt() == Constantes.OBJETO_TIPO_OBJETO_MISION) {
                                ENVIAR_BN_NADA(personaje, "INTERCAMBIO MOVER TIPO MISION")
                                continue
                            }
                            if (objeto.objModelo?.tipo?.toInt() == Constantes.OBJETO_TIPO_OBJETO_DE_BUSQUEDA) {
                                ENVIAR_BN_NADA(personaje, "INTERCAMBIO MOVER TIPO BUSQUEDA")
                                continue
                            }
                            if (personaje!!.tipoExchange != Constantes.INTERCAMBIO_TIPO_COFRE) {
                                if (objeto.tieneStatTexto(Constantes.STAT_LIGADO_A_CUENTA)) {
                                    ENVIAR_BN_NADA(personaje, "INTERCAMBIO MOVER LIGADO")
                                    continue
                                }
                                if (objeto.pasoIntercambiableDesde()) {
                                    ENVIAR_BN_NADA(personaje, "INTERCAMBIO MOVER NO INTERCAMBIABLE")
                                    return
                                }
                            }
                            if (cantidad > objeto.cantidad) {
                                cantidad = objeto.cantidad
                            }
                            when (sPacket[0]) {
                                '+' -> {
                                    if (cantidad > objeto.cantidad) {
                                        cantidad = objeto.cantidad
                                    }
                                    when (personaje!!.tipoExchange) {
                                        Constantes.INTERCAMBIO_TIPO_TALLER, Constantes.INTERCAMBIO_TIPO_RECAUDADOR, Constantes.INTERCAMBIO_TIPO_PERSONAJE, Constantes.INTERCAMBIO_TIPO_TALLER_ARTESANO, Constantes.INTERCAMBIO_TIPO_TALLER_CLIENTE, Constantes.INTERCAMBIO_TIPO_TRUEQUE, Constantes.INTERCAMBIO_TIPO_MI_TIENDA, Constantes.INTERCAMBIO_TIPO_MERCADILLO_VENDER, Constantes.INTERCAMBIO_TIPO_RESUCITAR_MASCOTA, Constantes.INTERCAMBIO_TIPO_MONTURA, Constantes.INTERCAMBIO_TIPO_COFRE -> {
                                            if (personaje!!.exchanger == null) {
                                                continue@loop
                                            }
                                            // if (_perso.getObjeto(id) == null) {
// GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1OBJECT_DONT_EXIST");
// continue;
// }
                                            if (infos.size > 2) {
                                                try {
                                                    precio = infos[2].toInt()
                                                } catch (ignored: Exception) {
                                                }
                                            }
                                            if (precio < 0) {
                                                ENVIAR_BN_NADA(personaje)
                                                continue@loop
                                            }
                                            personaje!!.exchanger.addObjetoExchanger(
                                                objeto,
                                                cantidad,
                                                personaje!!,
                                                precio
                                            )
                                        }
                                    }
                                }
                                '-' -> when (personaje!!.tipoExchange) {
                                    Constantes.INTERCAMBIO_TIPO_PERSONAJE, Constantes.INTERCAMBIO_TIPO_TALLER_ARTESANO, Constantes.INTERCAMBIO_TIPO_TALLER_CLIENTE, Constantes.INTERCAMBIO_TIPO_TRUEQUE -> {
                                        if (personaje!!.getObjeto(id) == null) {
                                            ENVIAR_Im_INFORMACION(personaje!!, "1OBJECT_DONT_EXIST")
                                            continue@loop
                                        }
                                        if (personaje!!.exchanger == null) {
                                            continue@loop
                                        }
                                        try {
                                            precio = infos[2].toInt()
                                        } catch (ignored: Exception) {
                                        }
                                        if (precio < 0) {
                                            ENVIAR_BN_NADA(personaje)
                                            continue@loop
                                        }
                                        if (cantidad > objeto.cantidad) {
                                            cantidad = objeto.cantidad
                                        }
                                        personaje!!.exchanger.remObjetoExchanger(objeto, cantidad, personaje!!, precio)
                                    }
                                    Constantes.INTERCAMBIO_TIPO_TALLER, Constantes.INTERCAMBIO_TIPO_RECAUDADOR, Constantes.INTERCAMBIO_TIPO_MI_TIENDA, Constantes.INTERCAMBIO_TIPO_MERCADILLO_VENDER, Constantes.INTERCAMBIO_TIPO_RESUCITAR_MASCOTA, Constantes.INTERCAMBIO_TIPO_MONTURA, Constantes.INTERCAMBIO_TIPO_COFRE -> {
                                        if (personaje!!.exchanger == null) {
                                            continue@loop
                                        }
                                        try {
                                            precio = infos[2].toInt()
                                        } catch (ignored: Exception) {
                                        }
                                        if (precio < 0) {
                                            ENVIAR_BN_NADA(personaje)
                                            continue@loop
                                        }
                                        if (cantidad > objeto.cantidad) {
                                            cantidad = objeto.cantidad
                                        }
                                        personaje!!.exchanger.remObjetoExchanger(objeto, cantidad, personaje!!, precio)
                                    }
                                }
                            }
                            try {
                                if (Mundo.SERVIDOR_ESTADO != Constantes.SERVIDOR_OFFLINE) Thread.sleep(1000)
                            } catch (e: Exception) {
                            }
                        }
                        if (varios) personaje?.enviarmensajeNegro("Operacion terminada")
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(personaje, "INTERCAMBIO MOVER OBJETO")
                        redactarLogServidorln(
                            "EXCEPTION Packet $packet, intercambio_Mover_Objeto " + e
                                .toString()
                        )
                        e.printStackTrace()
                    }
                    ENVIAR_Ow_PODS_DEL_PJ(personaje!!)
                    if (personaje!!.tipoExchange == Constantes.INTERCAMBIO_TIPO_MONTURA && personaje!!.montura != null) {
                        ENVIAR_Ew_PODS_MONTURA(personaje!!)
                    }
                }
                'R' -> if (personaje!!.tipoExchange == Constantes.INTERCAMBIO_TIPO_TALLER) {
                    val trabajo = personaje!!.getIntercambiandoCon(Trabajo::class.java) as Trabajo?
                    if (trabajo == null) {
                        ENVIAR_BN_NADA(personaje, "INTERCAMBIO MOVER OBJETO TRABAJO NULL 'R'")
                        return
                    }
                    if (trabajo.esCraft()) {
                        trabajo.craftearXVeces(packet.substring(3).toInt())
                    }
                }
                'r' -> if (personaje!!.tipoExchange == Constantes.INTERCAMBIO_TIPO_TALLER) {
                    val trabajo = personaje!!.getIntercambiandoCon(Trabajo::class.java) as Trabajo?
                    if (trabajo == null) {
                        ENVIAR_BN_NADA(personaje, "INTERCAMBIO MOVER OBJETO TRABAJO NULL 'r'")
                        return
                    }
                    if (trabajo.esCraft()) {
                        trabajo.interrumpirReceta()
                    }
                }
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje, "INTERCAMBIO MOVER OBJETO FINAL")
            redactarLogServidorln(
                "EXCEPTION Packet $packet, intercambio_Mover_Objeto(final) " + e
                    .toString()
            )
            e.printStackTrace()
        }
    }

    private fun intercambio_Pago_Por_Trabajo(packet: String) {
        val taller = personaje!!.getIntercambiandoCon(Trabajo::class.java) as Trabajo?
        if (taller == null || !taller.esTaller()) {
            ENVIAR_BN_NADA(personaje)
            return
        }
        val tipoPago = packet.substring(2, 3).toInt()
        val caracter = packet[3]
        val signo = packet[4]
        when (caracter) {
            'G' -> {
                var kamas: Long = 0
                kamas = try {
                    packet.substring(4).toLong()
                } catch (e: Exception) {
                    ENVIAR_BN_NADA(personaje)
                    return
                }
                if (kamas < 0) {
                    kamas = 0
                }
                taller.setKamas(tipoPago, kamas, personaje!!.kamas)
            }
            'O' -> {
                val infos = packet.substring(5).split(Pattern.quote("|").toRegex()).toTypedArray()
                var id = -1
                var cantidad = 0
                try {
                    id = infos[0].toInt()
                    cantidad = infos[1].toInt()
                } catch (ignored: Exception) {
                }
                val objeto = personaje!!.getObjeto(id)
                if (cantidad <= 0 || objeto == null) {
                    ENVIAR_Im_INFORMACION(personaje!!, "1OBJECT_DONT_EXIST")
                    return
                }
                if (objeto.tieneStatTexto(Constantes.STAT_LIGADO_A_CUENTA)) {
                    ENVIAR_Im_INFORMACION(personaje!!, "1129")
                    return
                }
                if (objeto.pasoIntercambiableDesde()) {
                    ENVIAR_Im_INFORMACION(personaje!!, "1129")
                    return
                }
                val cantInter = taller.getCantObjetoPago(id, tipoPago)
                when (signo) {
                    '+' -> {
                        val nuevaCant = objeto.cantidad - cantInter
                        if (cantidad > nuevaCant) {
                            cantidad = nuevaCant
                        }
                        taller.addObjetoPaga(objeto, cantidad, tipoPago)
                    }
                    '-' -> {
                        if (cantidad > cantInter) {
                            cantidad = cantInter
                        }
                        taller.quitarObjetoPaga(objeto, cantidad, tipoPago)
                    }
                }
            }
            else -> {
                val infos = packet.substring(5).split(Pattern.quote("|").toRegex()).toTypedArray()
                var id = -1
                var cantidad = 0
                try {
                    id = infos[0].toInt()
                    cantidad = infos[1].toInt()
                } catch (ignored: Exception) {
                }
                val objeto = personaje!!.getObjeto(id)
                if (cantidad <= 0 || objeto == null) {
                    ENVIAR_Im_INFORMACION(personaje!!, "1OBJECT_DONT_EXIST")
                    return
                }
                if (objeto.tieneStatTexto(Constantes.STAT_LIGADO_A_CUENTA)) {
                    ENVIAR_Im_INFORMACION(personaje!!, "1129")
                    return
                }
                if (objeto.pasoIntercambiableDesde()) {
                    ENVIAR_Im_INFORMACION(personaje!!, "1129")
                    return
                }
                val cantInter = taller.getCantObjetoPago(id, tipoPago)
                when (signo) {
                    '+' -> {
                        val nuevaCant = objeto.cantidad - cantInter
                        if (cantidad > nuevaCant) {
                            cantidad = nuevaCant
                        }
                        taller.addObjetoPaga(objeto, cantidad, tipoPago)
                    }
                    '-' -> {
                        if (cantidad > cantInter) {
                            cantidad = cantInter
                        }
                        taller.quitarObjetoPaga(objeto, cantidad, tipoPago)
                    }
                }
            }
        }
    }

    private fun intercambio_Preg_Mercante() {
        if (personaje!!.deshonor >= 4) {
            ENVIAR_Im_INFORMACION(personaje!!, "183")
            return
        }
        if (personaje!!.estaDisponible(false, false)) {
            return
        }
        val tasa = 1 // _perso.getNivel() / 2;
        var impuesto = personaje!!.precioTotalTienda() * tasa / 1000
        val mapaID = personaje!!.mapa.id
        if (Constantes.esMapaMercante(mapaID)) {
            impuesto = 0
        }
        ENVIAR_Eq_PREGUNTAR_MERCANTE(personaje!!, personaje!!.objetosTienda.size, tasa, impuesto)
    }

    private fun intercambio_Ok_Mercante() {
        if (personaje!!.estaDisponible(false, false)) {
            return
        }
        if (personaje!!.objetosTienda.isEmpty()) {
            ENVIAR_Im_INFORMACION(personaje!!, "123")
            return
        }
        if (personaje!!.mapa.cantMercantes() >= personaje!!.mapa.maxMercantes) {
            ENVIAR_Im_INFORMACION(personaje!!, "125;" + personaje!!.mapa.maxMercantes)
            return
        }
        if (!personaje!!.celda.librerParaMercante() || !personaje!!.celda.esCaminable(false)) {
            ENVIAR_Im_INFORMACION(personaje!!, "124")
            return
        }
        var impuesto = personaje!!.precioTotalTienda() / 1000
        val mapaID = personaje!!.mapa.id
        if (Constantes.esMapaMercante(mapaID)) {
            impuesto = 0
        }
        if (impuesto < 0 || personaje!!.kamas < impuesto) {
            ENVIAR_Im_INFORMACION(personaje!!, "176")
        } else {
            personaje!!.addKamas(-impuesto, false, true)
            personaje!!.setMercante(true)
            personaje!!.mapa.addMercante(personaje!!)
            cerrarSocket(true, "intercambio_Ok_Mercante()")
        }
    }

    private fun intercambio_Mercadillo(packet: String) {
        try {
            val mercadillo = personaje!!.getIntercambiandoCon(Mercadillo::class.java) as Mercadillo
            if (mercadillo == null) {
                ENVIAR_BN_NADA(personaje)
                return
            }
            when (packet[2]) {
                'B' -> {
                    val info = packet.substring(3).split(Pattern.quote("|").toRegex()).toTypedArray()
                    if (mercadillo.comprarObjeto(
                            info[0].toInt(), info[1].toInt(), info[2].toLong(),
                            personaje!!
                        )
                    ) {
                        ENVIAR_Ow_PODS_DEL_PJ(personaje!!)
                        ENVIAR_Im_INFORMACION(personaje!!, "068")
                    } else {
                        ENVIAR_Im_INFORMACION(personaje!!, "172")
                    }
                }
                'l' -> {
                    val str = mercadillo.strListaLineasPorModelo(packet.substring(3).toInt())
                    if (str.isEmpty()) {
                        ENVIAR_EHM_MOVER_OBJMERCA_POR_MODELO(
                            personaje!!,
                            "-",
                            packet.substring(3).toInt().toString() + ""
                        )
                    } else {
                        ENVIAR_EHl_LISTA_LINEAS_OBJMERCA_POR_MODELO(personaje!!, str)
                    }
                }
                'P' -> Mundo
                    .getObjetoModelo(packet.substring(3).toInt())?.precioPromedio?.let {
                    ENVIAR_EHP_PRECIO_PROMEDIO_OBJ(
                        personaje!!, packet.substring(3).toInt(), it
                    )
                }
                'S' -> {
                    val splt = packet.substring(3).split(Pattern.quote("|").toRegex()).toTypedArray()
                    if (mercadillo.esTipoDeEsteMercadillo(splt[0].toInt())) {
                        if (mercadillo.hayModeloEnEsteMercadillo(splt[0].toInt(), splt[1].toInt())) {
                            ENVIAR_EHS_BUSCAR_OBJETO_MERCADILLO(personaje!!, "K")
                            ENVIAR_EHl_LISTA_LINEAS_OBJMERCA_POR_MODELO(
                                personaje!!,
                                mercadillo.strListaLineasPorModelo(splt[1].toInt())
                            )
                        } else {
                            ENVIAR_EHS_BUSCAR_OBJETO_MERCADILLO(personaje!!, "E")
                        }
                    } else {
                        ENVIAR_EHS_BUSCAR_OBJETO_MERCADILLO(personaje!!, "E")
                    }
                }
                'T' -> ENVIAR_EHL_LISTA_OBJMERCA_POR_TIPO(
                    personaje!!, packet.substring(3).toInt(), mercadillo
                        .stringModelo(packet.substring(3).toInt())
                )
                else -> {
                    redactarLogServidorln("$stringDesconocido ANALIZAR INTERCAMBIO MERCADILLO: $packet")
                    if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                        redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                        cerrarSocket(true, "intercambio_Mercadillo()")
                    }
                    return
                }
            }
        } catch (ignored: Exception) {
        }
    }

    @Synchronized
    private fun intercambio_Cercado(packet: String) {
        try {
            val cercado = personaje!!.getIntercambiandoCon(Cercado::class.java) as Cercado
            if (personaje!!.deshonor >= 5) {
                ENVIAR_Im_INFORMACION(personaje!!, "183")
                ENVIAR_EV_CERRAR_VENTANAS(personaje!!, "")
                return
            }
            val c = packet[2]
            val packet2 = packet.substring(3)
            var id = -1
            id = try {
                packet2.toInt()
            } catch (e: Exception) {
                ENVIAR_BN_NADA(personaje)
                return
            }
            var montura: Montura? = null
            when (c) {
                'g' -> {
                    if (!cercado.borrarMonturaCercado(id)) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    montura = Mundo.getMontura(id)
                    if (montura == null) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    ENVIAR_Ef_MONTURA_A_CRIAR(personaje!!, '-', montura.id.toString() + "")
                    ENVIAR_GM_BORRAR_GM_A_MAPA(personaje!!.mapa, id)
                    if (escapaDespuesParir(montura)) {
                        montura.setMapaCelda(null, null)
                        cuenta!!.addMonturaEstablo(montura)
                        ENVIAR_Ee_MONTURA_A_ESTABLO(personaje!!, '+', montura.detallesMontura())
                    }
                }
                'p' -> {
                    if (!cercado.puedeAgregar()) {
                        ENVIAR_Im_INFORMACION(personaje!!, "1107")
                        return
                    }
                    if (cuenta!!.borrarMonturaEstablo(id)) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    montura = Mundo.getMontura(id)
                    if (personaje!!.montura != null && personaje!!.montura.id == id) {
                        if (personaje!!.estaMontando()) {
                            personaje!!.subirBajarMontura(false)
                        }
                        personaje!!.montura = null
                    }
                    if (montura != null) {
                        montura.setMapaCelda(personaje!!.mapa, personaje!!.mapa.getCelda(cercado.celdaMontura))
                        cercado.addCriando(montura)
                        ENVIAR_Ef_MONTURA_A_CRIAR(personaje!!, '+', montura.detallesMontura())
                        ENVIAR_Ee_MONTURA_A_ESTABLO(personaje!!, '-', montura.id.toString() + "")
                        ENVIAR_GM_DRAGOPAVO_A_MAPA(personaje!!.mapa, "+", montura)
                    }
                }
                else -> {
                    redactarLogServidorln("$stringDesconocido ANALIZAR INTERCAMBIO CERCADO: $packet")
                    if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                        redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                        cerrarSocket(true, "intercambio_Cercado()")
                    }
                }
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje)
        }
    }

    @Synchronized
    private fun intercambio_Establo(packet: String) {
        try {
            val cercado = personaje!!.getIntercambiandoCon(Cercado::class.java) as Cercado
            if (personaje!!.deshonor >= 5) {
                ENVIAR_Im_INFORMACION(personaje!!, "183")
                ENVIAR_EV_CERRAR_VENTANAS(personaje!!, "")
                return
            }
            val c = packet[2]
            val packet2 = packet.substring(3)
            var id = -1
            id = try {
                packet2.toInt()
            } catch (e: Exception) {
                ENVIAR_BN_NADA(personaje)
                return
            }
            var montura: Montura? = null
            when (c) {
                'C' -> {
                    val obj = personaje!!.getObjeto(id)
                    if (obj == null) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    if (!validaCondiciones(personaje, obj.objModelo?.condiciones)) {
                        ENVIAR_Im_INFORMACION(personaje!!, "119|43")
                        return
                    }
                    montura = Mundo.getMontura(-abs(obj.getStatValor(Constantes.STAT_CONSULTAR_MONTURA)))
                    if (montura == null) {
                        val color = Constantes.getColorMonturaPorCertificado(obj.objModeloID)
                        if (color < 1) {
                            ENVIAR_Im_INFORMACION(personaje!!, "1MOUNT_COLOR_NOT_EXIST")
                            return
                        }
                        montura = Montura(color, personaje!!.Id, true, false)
                    }
                    if (obj.cantidad <= 1) {
                        personaje!!.borrarOEliminarConOR(id, true)
                    } else {
                        obj.cantidad = obj.cantidad - 1
                        ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(personaje!!, obj)
                    }
                    if (montura.pergamino != -1 && montura.pergamino != obj.id) {
                        ENVIAR_Im_INFORMACION(personaje!!, "1MOUNT_IS_NOT_CERTIFICATED")
                        return
                    }
                    if (escapaDespuesParir(montura)) {
                        cuenta!!.addMonturaEstablo(montura)
                        montura.dueñoID = personaje!!.Id
                        montura.pergamino = 0
                        ENVIAR_Ee_MONTURA_A_ESTABLO(personaje!!, '+', montura.detallesMontura())
                    }
                }
                'c' -> {
                    montura = Mundo.getMontura(id)
                    if (montura == null || cuenta!!.borrarMonturaEstablo(id)) {
                        ENVIAR_Im_INFORMACION(personaje!!, "1104")
                        return
                    }
                    if (montura.pergamino > 0) {
                        ENVIAR_Ee_MONTURA_A_ESTABLO(personaje!!, '-', montura.id.toString() + "")
                        ENVIAR_Im_INFORMACION(personaje!!, "1104")
                        return
                    }
                    val obj1 = Objects.requireNonNull(montura.objModCertificado)!!.crearObjeto(
                        1, Constantes.OBJETO_POS_NO_EQUIPADO,
                        CAPACIDAD_STATS.RANDOM
                    )
                    obj1.fijarStatValor(Constantes.STAT_CONSULTAR_MONTURA, abs(montura.id))
                    obj1.addStatTexto(Constantes.STAT_PERTENECE_A, "0#0#0#" + personaje!!.nombre)
                    obj1.addStatTexto(Constantes.STAT_NOMBRE, "0#0#0#" + montura.nombre)
                    personaje!!.addObjetoConOAKO(obj1, true)
                    montura.setMapaCelda(null, null)
                    montura.pergamino = obj1.id
                    ENVIAR_Ow_PODS_DEL_PJ(personaje!!)
                    ENVIAR_Ee_MONTURA_A_ESTABLO(personaje!!, '-', montura.id.toString() + "")
                    REPLACE_MONTURA(montura, false)
                }
                'g' -> {
                    if (personaje!!.montura != null) {
                        ENVIAR_Im_INFORMACION(personaje!!, "1YOU_HAVE_MOUNT")
                        return
                    }
                    montura = Mundo.getMontura(id)
                    if (montura == null || cuenta!!.establo.remove(id) == null) {
                        ENVIAR_Im_INFORMACION(personaje!!, "1104")
                        return
                    }
                    if (montura.pergamino > 0) {
                        ENVIAR_Ee_MONTURA_A_ESTABLO(personaje!!, '-', montura.id.toString() + "")
                        ENVIAR_Im_INFORMACION(personaje!!, "1104")
                        return
                    }
                    montura.setMapaCelda(null, null)
                    personaje!!.montura = montura
                    ENVIAR_Ee_MONTURA_A_ESTABLO(personaje!!, '-', montura.id.toString() + "")
                    ENVIAR_Rx_EXP_DONADA_MONTURA(personaje!!)
                }
                'p' -> {
                    montura = personaje!!.montura
                    if (montura == null || montura.id != id) {
                        ENVIAR_Im_INFORMACION(personaje!!, "1YOU_DONT_HAVE_MOUNT")
                        return
                    }
                    if (!montura.objetos.isEmpty()) {
                        ENVIAR_Im_INFORMACION(personaje!!, "1106")
                        return
                    }
                    if (personaje!!.estaMontando()) {
                        personaje!!.subirBajarMontura(false)
                    }
                    personaje!!.montura = null
                    if (escapaDespuesParir(montura)) {
                        montura.setMapaCelda(null, null)
                        montura.ubicacion = Ubicacion.ESTABLO
                        cuenta!!.addMonturaEstablo(montura)
                        ENVIAR_Ee_MONTURA_A_ESTABLO(personaje!!, '+', montura.detallesMontura())
                    }
                    ENVIAR_Rx_EXP_DONADA_MONTURA(personaje!!)
                }
                else -> {
                    redactarLogServidorln("$stringDesconocido ANALIZAR INTERCAMBIO ESTABLO: $packet")
                    if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                        redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                        cerrarSocket(true, "intercambio_Establo()")
                    }
                }
            }
        } catch (e: Exception) {
            redactarLogServidorln("EXCEPTION Packet $packet, intercambio_Establo $e")
            e.printStackTrace()
        }
    }

    @Synchronized
    private fun escapaDespuesParir(madre: Montura): Boolean {
        val padre = Mundo.getMontura(madre.parejaID)
        if (madre.fecundadaHaceMinutos >= AtlantaMain.HORAS_PERDER_CRIAS_MONTURA * 60) { // las crias mueren por tiempo
            ENVIAR_Im_INFORMACION(personaje!!, "1112")
            madre.aumentarReproduccion()
        } else if (madre.fecundadaHaceMinutos >= madre.minutosParir()) { // nacen las crias
            var crias = getRandomInt(1, 3)
            if (madre.capacidades.contains(Constantes.HABILIDAD_REPRODUCTORA)) {
                crias *= 2
            }
            if (madre.reprod + crias > 20) {
                crias = 20 - madre.reprod
            }
            ENVIAR_Im_INFORMACION(personaje!!, (if (crias == 1) 1110 else 1111).toString() + ";" + crias)
            for (i in 1..crias) {
                val bebeMontura = Montura(madre, padre)
                //
                madre.aumentarReproduccion()
                bebeMontura.setMapaCelda(null, null)
                cuenta!!.addMonturaEstablo(bebeMontura)
                ENVIAR_Ee_MONTURA_A_ESTABLO(personaje!!, '~', bebeMontura.detallesMontura())
            }
        } else {
            return true
        }
        if (padre != null) {
            REPLACE_MONTURA(padre, false)
        }
        madre.restarAmor(7500)
        madre.restarResistencia(7500)
        madre.setFecundada(false)
        return !madre.pudoEscapar()
    }

    @Synchronized
    private fun intercambio_Repetir_Ult_Craft() {
        val trabajo = personaje!!.getIntercambiandoCon(Trabajo::class.java) as Trabajo?
        trabajo?.ponerIngredUltRecet()
    }

    @Synchronized
    private fun intercambio_Vender(packet: String) {
        try {
            if (personaje!!.tipoExchange == Constantes.INTERCAMBIO_TIPO_TIENDA_NPC) { // npc
// case 20 ://boutique
                personaje!!.venderObjetos(packet.substring(2))
                return
            }
        } catch (ignored: Exception) {
        }
        ENVIAR_ESE_ERROR_VENTA(personaje!!)
    }

    @Synchronized
    private fun intercambio_Comprar(packet: String) {
        try {
            val infos = packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()
            when (personaje!!.tipoExchange) {
                Constantes.INTERCAMBIO_TIPO_TIENDA_NPC, Constantes.INTERCAMBIO_TIPO_BOUTIQUE -> try {
                    var objModeloID = 0
                    var cantidad = 0
                    try {
                        objModeloID = infos[0].toInt()
                        cantidad = infos[1].toInt()
                    } catch (ignored: Exception) {
                    }
                    if (cantidad <= 0 || objModeloID <= 0) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    val objModelo = Mundo.getObjetoModelo(objModeloID)
                    if (objModelo == null) {
                        ENVIAR_EBE_ERROR_DE_COMPRA(personaje!!)
                        return
                    }
                    var capStats = CAPACIDAD_STATS.RANDOM
                    if (personaje!!.tipoExchange == Constantes.INTERCAMBIO_TIPO_TIENDA_NPC) {
                        val npc = personaje!!.getIntercambiandoCon(NPC::class.java) as NPC
                        if (npc == null) {
                            ENVIAR_EBE_ERROR_DE_COMPRA(personaje!!)
                            return
                        }
                        val npcMod = npc.modelo
                        if (npcMod == null || npcMod.tieneObjeto(objModeloID)) {
                            ENVIAR_EBE_ERROR_DE_COMPRA(personaje!!)
                            return
                        }
                        if (AtlantaMain.IDS_NPCS_VENDE_OBJETOS_STATS_MAXIMOS.contains(npcMod.id)) {
                            capStats = CAPACIDAD_STATS.MAXIMO
                        }
                    } else {
                        try {
                            if (Mundo.getNPCModelo(AtlantaMain.ID_NPC_BOUTIQUE.toInt())?.tieneObjeto(objModeloID)!!) {
                                ENVIAR_EBE_ERROR_DE_COMPRA(personaje!!)
                                return
                            }
                        } catch (e: Exception) {
                            ENVIAR_EBE_ERROR_DE_COMPRA(personaje!!)
                            return
                        }
                    }
                    if (objModelo.itemPago != null) {
                        val idItemPago = objModelo.itemPago!!._primero
                        val cantItemPago = objModelo.itemPago!!._segundo
                        if (valorValido(cantidad, cantItemPago)) {
                            ENVIAR_BN_NADA(personaje, "INTENTO BUG MULTIPLICADOR")
                            return
                        }
                        if (!personaje!!.tieneObjPorModYCant(idItemPago, cantItemPago * cantidad)) {
                            ENVIAR_Im_INFORMACION(personaje!!, "14")
                            ENVIAR_EBE_ERROR_DE_COMPRA(personaje!!)
                            return
                        }
                        personaje!!.restarObjPorModYCant(idItemPago, cantItemPago * cantidad)
                    } else if (objModelo.ogrinas > 0) {
                        if (valorValido(cantidad, objModelo.ogrinas)) {
                            ENVIAR_BN_NADA(personaje, "INTENTO BUG MULTIPLICADOR")
                            return
                        }
                        val ogrinas = objModelo.ogrinas * cantidad.toLong()
                        if (ogrinas < 0) {
                            ENVIAR_EBE_ERROR_DE_COMPRA(personaje!!)
                            return
                        }
                        if (objModelo.kamas > 0) {
                            objModelo.kamas = 0
                        }
                        if (!RESTAR_OGRINAS(cuenta!!, ogrinas, personaje)) {
                            ENVIAR_EBE_ERROR_DE_COMPRA(personaje!!)
                            return
                        }
                    } else {
                        if (valorValido(cantidad, objModelo.kamas)) {
                            ENVIAR_BN_NADA(personaje, "INTENTO BUG MULTIPLICADOR")
                            return
                        }
                        val kamas = objModelo.kamas * cantidad.toLong()
                        if (kamas < 0) {
                            ENVIAR_EBE_ERROR_DE_COMPRA(personaje!!)
                            return
                        }
                        if (personaje!!.kamas < kamas) {
                            ENVIAR_Im_INFORMACION(personaje!!, "182")
                            ENVIAR_EBE_ERROR_DE_COMPRA(personaje!!)
                            return
                        }
                        personaje!!.addKamas(-kamas, true, true)
                    }
                    if (AtlantaMain.PARAM_OBJETOS_PEFECTOS_COMPRADOS_NPC || objModelo.ogrinas > 0) {
                        capStats = CAPACIDAD_STATS.MAXIMO
                    }
                    val objeto = objModelo.crearObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO, capStats)
                    if (objModelo.ogrinas > 0) {
                        if (AtlantaMain.PARAM_NOMBRE_ADMIN) {
                            objeto.addStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER, "0#0#0#" + personaje!!.nombre)
                        }
                        if (AtlantaMain.PARAM_OBJETOS_OGRINAS_LIGADO) {
                            objeto.addStatTexto(Constantes.STAT_LIGADO_A_CUENTA, "0#0#0#" + personaje!!.nombre)
                        }
                    }
                    personaje!!.addObjIdentAInventario(objeto, false)
                    ENVIAR_EBK_COMPRADO(personaje!!)
                    ENVIAR_Ow_PODS_DEL_PJ(personaje!!)
                } catch (e: Exception) {
                    ENVIAR_EBE_ERROR_DE_COMPRA(personaje!!)
                }
                Constantes.INTERCAMBIO_TIPO_MERCANTE -> {
                    val mercante = personaje!!.getIntercambiandoCon(Personaje::class.java) as Personaje
                    if (!mercante.esMercante()) {
                        ENVIAR_EBE_ERROR_DE_COMPRA(personaje!!)
                        return
                    }
                    try {
                        val cantidad = infos[1].toInt()
                        val objetoID = infos[0].toInt()
                        val objeto = Mundo.getObjeto(objetoID)
                        if (mercante.comprarTienda(personaje, cantidad, objeto)) {
                            ENVIAR_EBK_COMPRADO(personaje!!)
                            ENVIAR_Ak_KAMAS_PDV_EXP_PJ(personaje!!)
                            ENVIAR_Ow_PODS_DEL_PJ(personaje!!)
                            if (mercante.tienda.estaVacia()) {
                                personaje!!.mapa.removerMercante(mercante.Id)
                                mercante.setMercante(false)
                                personaje!!.cerrarVentanaExchange("b")
                                ENVIAR_GM_BORRAR_GM_A_MAPA(personaje!!.mapa, mercante.Id)
                            } else {
                                ENVIAR_EL_LISTA_EXCHANGER(personaje!!, mercante)
                            }
                        } else {
                            ENVIAR_EBE_ERROR_DE_COMPRA(personaje!!)
                        }
                    } catch (e: Exception) {
                        ENVIAR_EBE_ERROR_DE_COMPRA(personaje!!)
                        ENVIAR_EL_LISTA_EXCHANGER(personaje!!, mercante)
                    }
                }
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje)
        }
    }

    private fun analizar_Ambiente(packet: String) {
        if (packet.length < 2) {
            ENVIAR_BN_NADA(personaje)
            try {
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        when (packet[1]) {
            'D' -> ambiente_Cambio_Direccion(packet)
            'U' -> ambiente_Emote(packet)
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR AMBIENTE: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "analizar_Ambiente()")
                }
            }
        }
    }

    private fun ambiente_Emote(packet: String) {
        var emote: Byte = -1
        emote = try {
            packet.substring(2).toByte()
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje)
            return
        }
        if (personaje!!.pelea != null) {
            ENVIAR_BN_NADA(personaje)
            return
        }
        if (emote < 0 || !personaje!!.tieneEmote(emote.toInt())) {
            ENVIAR_Im_INFORMACION(personaje!!, "1CANT_USE_EMOTE")
            return
        }
        when (emote) {
            Constantes.EMOTE_ACOSTARSE, Constantes.EMOTE_SENTARSE -> {
                if (personaje!!.estaSentado()) {
                    emote = 0
                }
                personaje!!.setSentado(!personaje!!.estaSentado())
            }
        }
        personaje!!.setEmoteActivado(emote)
        var tiempo = 0
        if (emote == Constantes.EMOTE_FLAUTA) { // flauta
            tiempo = 9000
        } else if (emote == Constantes.EMOTE_CAMPEON) { // campeon
            tiempo = 5000
        }
        ENVIAR_eUK_EMOTE_MAPA(personaje!!.mapa, personaje!!.Id, emote.toInt(), tiempo)
        val cercado = personaje!!.mapa.cercado
        if (cercado != null) {
            when (emote) {
                Constantes.EMOTE_SEÑAL_CON_MANO, Constantes.EMOTE_ENFADARSE, Constantes.EMOTE_APLAUDIR, Constantes.EMOTE_PEDO, Constantes.EMOTE_MOSTRAR_ARMA, Constantes.EMOTE_BESO -> {
                    var monturas: ArrayList<Montura>? = ArrayList()
                    for (montura in cercado.criando.values) {
                        if (montura.dueñoID == personaje!!.Id) {
                            monturas!!.add(montura)
                        }
                    }
                    if (monturas!!.isNotEmpty()) {
                        var casillas = 0
                        when (emote) {
                            Constantes.EMOTE_SEÑAL_CON_MANO, Constantes.EMOTE_ENFADARSE -> casillas = 1
                            Constantes.EMOTE_APLAUDIR, Constantes.EMOTE_PEDO -> casillas = getRandomInt(2, 3)
                            Constantes.EMOTE_MOSTRAR_ARMA, Constantes.EMOTE_BESO -> casillas = getRandomInt(4, 7)
                        }
                        val alejar: Boolean =
                            emote != Constantes.EMOTE_SEÑAL_CON_MANO && emote != Constantes.EMOTE_APLAUDIR && emote != Constantes.EMOTE_BESO
                        monturas[getRandomInt(0, monturas.size - 1)].moverMontura(personaje, -1, casillas, alejar)
                    }
                    monturas = null
                }
            }
        }
    }

    private fun ambiente_Cambio_Direccion(packet: String) {
        try {
            if (personaje!!.pelea != null) {
                return
            }
            val dir = packet.substring(2).toByte()
            personaje!!.orientacion = dir
            ENVIAR_eD_CAMBIAR_ORIENTACION(personaje!!.mapa, personaje!!.Id, dir)
        } catch (ignored: Exception) {
        }
    }

    fun analizar_Hechizos(packet: String) {
        try {
            if (packet.length < 2) {
                ENVIAR_BN_NADA(personaje)
                try {
                    redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
                } catch (e: Exception) {
                    redactarLogServidorln("Packet Loss: $packet")
                }
                return
            }
            when (packet[1]) {
                'B' -> hechizos_Boost(packet)
                'F' -> hechizos_Olvidar(packet)
                'M' -> hechizos_Acceso_Rapido(packet)
                else -> {
                    redactarLogServidorln("$stringDesconocido ANALIZAR HECHIZOS: $packet")
                    if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                        redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                        cerrarSocket(true, "analizar_Hechizos()")
                    }
                }
            }
        } catch (e: Exception) {
            redactarLogServidorln("Probable packet loss, Paquete: $packet | error: $e")
        }
    }

    private fun hechizos_Acceso_Rapido(packet: String) {
        try {
            val split = packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()
            val hechizoID = split[0].toInt()
            val posicion = split[1].toInt()
            personaje!!.setPosHechizo(hechizoID, Encriptador.getValorHashPorNumero(posicion))
        } catch (ignored: Exception) {
        }
    }

    private fun hechizos_Boost(packet: String) {
        try { // if (_perso.getPelea() != null) {
// GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1CANT_BOOST_IN_GAME");
// return;
// }
            if (!personaje!!.boostearHechizo(packet.substring(2).toInt())) {
                ENVIAR_SUE_NIVEL_HECHIZO_ERROR(personaje!!)
            }
        } catch (e: Exception) {
            ENVIAR_SUE_NIVEL_HECHIZO_ERROR(personaje!!)
        }
    }

    private fun hechizos_Olvidar(packet: String) {
        try {
            if (!personaje!!.estaOlvidandoHechizo()) {
                ENVIAR_BN_NADA(personaje)
                return
            }
            if (personaje!!.olvidarHechizo(packet.substring(2).toInt(), false, true)) {
                personaje!!.setOlvidandoHechizo(false)
            }
        } catch (ignored: Exception) {
        }
    }

    fun analizar_Peleas(packet: String) {
        val pelea = personaje!!.pelea
        if (packet.length < 2) {
            ENVIAR_BN_NADA(personaje)
            try {
                redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
            } catch (e: Exception) {
                redactarLogServidorln("Packet Loss: $packet")
            }
            return
        }
        when (packet[1]) {
            'D' -> {
                if (pelea != null) {
                    ENVIAR_BN_NADA(personaje)
                    return
                }
                pelea_Detalles(packet)
            }
            'H' -> {
                if (pelea == null) {
                    ENVIAR_BN_NADA(personaje)
                    return
                }
                pelea.botonAyuda(personaje!!.Id)
            }
            'L' -> {
                if (pelea != null) {
                    ENVIAR_BN_NADA(personaje)
                    return
                }
                ENVIAR_fL_LISTA_PELEAS(personaje!!, personaje!!.mapa)
            }
            'N' -> {
                if (pelea == null) {
                    ENVIAR_BN_NADA(personaje)
                    return
                }
                pelea.botonBloquearMasJug(personaje!!.Id)
            }
            'P' -> {
                if (pelea == null || personaje!!.grupoParty == null) {
                    ENVIAR_BN_NADA(personaje)
                    return
                }
                pelea.botonSoloGrupo(personaje!!.Id)
            }
            'S' -> {
                if (pelea == null) {
                    ENVIAR_BN_NADA(personaje)
                    return
                }
                pelea.botonBloquearEspect(personaje!!.Id)
            }
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR PELEAS: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "analizar_Peleas()")
                }
            }
        }
    }

    private fun pelea_Detalles(packet: String) {
        var id: Short = -1
        try {
            id = packet.substring(2).replace("0", "").toShort()
        } catch (ignored: Exception) {
        }
        if (id.toInt() == -1) {
            ENVIAR_BN_NADA(personaje)
            return
        }
        ENVIAR_fD_DETALLES_PELEA(personaje!!, personaje!!.mapa.peleas!![id])
    }

    fun analizar_Basicos(packet: String) {
        try {
            if (packet.length < 2) {
                ENVIAR_BN_NADA(personaje)
                try {
                    redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
                } catch (e: Exception) {
                    redactarLogServidorln("Packet Loss: $packet")
                }
                return
            }
            when (packet[1]) {
                'a' -> {
                    try {
                        if (cuenta?.bloqueado == true) {
                            ENVIAR_BN_NADA(this)
                            return
                        }
                        basicos_Comandos_Rapidos(packet)
                    } catch (e: Exception) {
                        redactarLogServidorln("Error en thread $e")
                    }
                }
                'A' -> {
                    try {
                        if (cuenta?.bloqueado == true) {
                            ENVIAR_BN_NADA(this)
                            return
                        }
                        basicos_Comandos_Consola(packet)
                    } catch (e: Exception) {
                        redactarLogServidorln("Error en thread $e")
                    }
                }
                'D' -> {
                    basicos_Enviar_Fecha()
                    registrarUltPing()
                }
                'K' -> ENVIAR_BN_NADA(personaje)
                'M' -> {
                    try {
                        basicos_Chat(packet)
                    } catch (e: Exception) {
                        redactarLogServidorln("Error en thread $e")
                    }
                }
                'R' -> {
                }
                'S' -> personaje!!.mostrarEmoteIcon(packet.substring(2))
                'Q' -> {
                    val celdaMercante = packet.substring(2).toShort()
                    personaje!!.mapa.expulsarMercanterPorCelda(celdaMercante)
                }
                'W' -> {
                    try {
                        basicos_Mensaje_Informacion(packet)
                    } catch (e: Exception) {
                        redactarLogServidorln("Error en thread $e")
                    }
                }
                'Y' -> basicos_Estado(packet)
                else -> {
                    redactarLogServidorln("$stringDesconocido ANALIZAR BASICOS: $packet")
                    if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                        redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                        cerrarSocket(true, "analizar_Basicos()")
                    }
                }
            }
        } catch (e: Exception) {
            redactarLogServidorln("EXCEPTION Packet $packet, analizar_Basicos $e")
            e.printStackTrace()
        }
    }

    private fun basicos_Comandos_Rapidos(packet: String) {
        when (packet[2]) {
            'M' -> {
                if (cuenta!!.admin == 0) {
                    ENVIAR_BN_NADA(personaje, "NO TIENE RANGO")
                    return
                }
                if (personaje!!.estaDisponible(false, true)) {
                    ENVIAR_BN_NADA(personaje, "NO ESTA DISPONIBLE")
                    return
                }
                try {
                    val infos = packet.substring(3).split(",".toRegex()).toTypedArray()
                    val coordX = infos[0].toInt()
                    val coordY = infos[1].toInt()
                    if (cuenta!!.admin > 1) {
                        val mapa = Mundo.mapaPorCoordXYContinente(
                            coordX, coordY, personaje!!.mapa.subArea!!.area
                                .superArea!!.id
                        )
                        if (mapa != null) {
                            personaje!!.teleport(mapa.id, mapa.randomCeldaIDLibre)
                        }
                    }
                } catch (e: Exception) {
                    ENVIAR_Im_INFORMACION(personaje!!, "1MAPA_NO_EXISTE")
                }
            }
            'K' -> ENVIAR_BN_NADA(personaje, AtlantaMain.PALABRA_CLAVE_CONSOLA)
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR BASICO COMANDOS RAPIDOS: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "basicos_Comandos_Rapidos()")
                }
            }
        }
    }

    fun basicos_Comandos_Consola(packet: String) {
        var mensaje = packet.substring(2)
        if (AtlantaMain.PALABRA_CLAVE_CONSOLA.isNotEmpty()) {
            if (!mensaje.contains(AtlantaMain.PALABRA_CLAVE_CONSOLA)) {
                return
            }
            mensaje = mensaje.replaceFirst(AtlantaMain.PALABRA_CLAVE_CONSOLA.toRegex(), "")
        }
        if (cuenta?.bloqueado == true) {
            ENVIAR_BN_NADA(this)
            return
        }
        consolaComando(mensaje, cuenta!!, personaje!!)
    }

    private fun basicos_Estado(packet: String) {
        when (packet[2]) {
            'A' -> if (personaje!!.estaAusente()) {
                ENVIAR_Im_INFORMACION(personaje!!, "038")
                personaje!!.setAusente(false)
            } else {
                ENVIAR_Im_INFORMACION(personaje!!, "037")
                personaje!!.setAusente(true)
            }
            'I' -> if (personaje!!.esInvisible()) {
                ENVIAR_Im_INFORMACION(personaje!!, "051")
                personaje!!.setInvisible(false)
            } else {
                ENVIAR_Im_INFORMACION(personaje!!, "050")
                personaje!!.setInvisible(true)
            }
            'O' -> when (packet[3]) {
                '+' -> personaje!!.addOmitido(packet.substring(4))
                '-' -> personaje!!.borrarOmitido(packet.substring(4))
                else -> personaje!!.addOmitido(packet.substring(4))
            }
            else -> {
                redactarLogServidorln("$stringDesconocido ANALIZAR BASICOS ESTADO: $packet")
                if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                    redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                    cerrarSocket(true, "basicos_Estado()")
                }
            }
        }
    }

    private fun basicos_Enviar_Fecha() { // deshabilitados por ser innecesario solo se mandara 1 vez al entrar al juego
        GestorSalida.ENVIAR_BD_FECHA_SERVER(personaje!!)
        GestorSalida.ENVIAR_BT_TIEMPO_SERVER(personaje!!)
    }

    fun basicos_Mensaje_Informacion(packet: String) {
        try {
            val perso = Mundo.getPersonajePorNombre(packet.substring(2))
            if (perso == null) {
                ENVIAR_BN_NADA(personaje)
                return
            }
            if (!perso.enLinea()) {
                ENVIAR_Im_INFORMACION(personaje!!, "1211")
                return
            }
            ENVIAR_BWK_QUIEN_ES(
                personaje!!, perso.cuenta.apodo + "|" + (if (perso.pelea != null) 2 else 1)
                        + "|" + perso.nombre + "|" + perso.mapa.id
            )
        } catch (ignored: Exception) {
        }
    }

    fun basicos_Chat(packet: String) {
        try {
            var msjChat = ""
            if (personaje!!.estaMuteado()) {
                val tiempoTrans = System.currentTimeMillis() - cuenta!!.horaMuteado
                if (tiempoTrans > cuenta!!.tiempoMuteado) {
                    cuenta!!.mutear(false, 0)
                } else {
                    GestorSalida.ENVIAR_Im_INFORMACION(
                        personaje!!,
                        "1124;" + (cuenta!!.tiempoMuteado - tiempoTrans) / 1000
                    )
                    return
                }
            }
            var packet2 = packet.replace("<", "").replace(">", "")
            if (packet2.length <= 3) {
                ENVIAR_BN_NADA(personaje)
                return
            }
            if (packet2.length > 1500) {
                packet2 = packet2.substring(0, 1499)
            }
            try {
                msjChat = packet2.split("\\|".toRegex(), 2).toTypedArray()[1]
                if (msjChat[msjChat.length - 1] == '|') msjChat = msjChat.substring(0, msjChat.length - 1)
            } catch (e: Exception) {
                msjChat = ""
            }
            if (msjChat.isEmpty()) {
                ENVIAR_BN_NADA(personaje)
                return
            }
            if (AtlantaMain.PALABRAS_PROHIBIDAS.isNotEmpty()) {
                val filtro = msjChat.replace(".", " ").split(" ".toRegex()).toTypedArray()
                var veces = 0
                for (s in filtro) {
                    if (AtlantaMain.PALABRAS_PROHIBIDAS.contains(s.toLowerCase())) {
                        veces++
                    }
                }
                if (veces == 0) {
                    val filtro2 = msjChat.replace(" ", "")
                    for (s in AtlantaMain.PALABRAS_PROHIBIDAS) {
                        if (s.length < 5) {
                            continue
                        }
                        if (filtro2.toLowerCase().contains(s)) {
                            veces++
                        }
                    }
                }
                if (veces > 0) {
                    GestorSalida.ENVIAR_Im_INFORMACION(personaje!!, "1DONT_USE_BLOCK_WORDS;$veces")
                    cuenta!!.mutear(true, veces * 60)
                    return
                }
            }
            when (val sufijo = packet2[2].toString() + "") {
                "$" -> {
                    if (personaje!!.tieneCanal(sufijo) || personaje!!.grupoParty == null) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    GestorSalida.ENVIAR_cMK_MENSAJE_CHAT_GRUPO(personaje!!, msjChat)
                }
                "¿" -> {
                    if (personaje!!.grupoKoliseo == null) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_KOLISEO(personaje!!, msjChat)
                }
                "~", "¬" -> GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS(sufijo, personaje!!, msjChat)
                "%" -> {
                    if (personaje!!.tieneCanal(sufijo) || personaje!!.gremio == null) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_GREMIO(personaje!!, msjChat)
                }
                "#" -> {
                    if (personaje!!.tieneCanal(sufijo) || personaje!!.pelea == null) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    val equipo = personaje!!.pelea.getParamMiEquipo(personaje!!.Id).toInt()
                    if (equipo == 4) {
                        GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PELEA(
                            personaje!!.pelea, 4, sufijo, personaje!!.Id, personaje!!.nombre,
                            msjChat
                        )
                    } else {
                        GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PELEA(
                            personaje!!.pelea, equipo, sufijo, personaje!!.Id, personaje!!
                                .nombre, msjChat
                        )
                    }
                }
                "*" -> {
                    if (personaje!!.tieneCanal(sufijo)) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    if (comando_jugador(msjChat)) {
                        return
                    }
                    // mensaje mapa
                    if (personaje!!.pelea == null) {
                        if (!personaje!!.mapa.muteado || cuenta!!.admin > 0) {
                            GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_MAPA(personaje!!, sufijo, msjChat)
                        } else {
                            GestorSalida.ENVIAR_Im_INFORMACION(personaje!!, "1MAPA_MUTEADO")
                        }
                    } else {
                        val equipo2 = personaje!!.pelea.getParamMiEquipo(personaje!!.Id).toInt()
                        if (equipo2 == 1 || equipo2 == 2) {
                            GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PELEA(
                                personaje!!.pelea, 7, "", personaje!!.Id, personaje!!.nombre,
                                msjChat
                            )
                        } else {
                            GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PELEA(
                                personaje!!.pelea, 7, "p", personaje!!.Id, personaje!!.nombre,
                                msjChat
                            )
                        }
                    }
                }
                "¡" -> {
                    if (!cuenta!!.esAbonado()) {
                        ENVIAR_BN_NADA(personaje, "NO ABONADO")
                        return
                    }
                    var h: Long
                    if (((System.currentTimeMillis() - _tiempoUltVIP) / 1000).also {
                            h = it
                        } < AtlantaMain.SEGUNDOS_CANAL_VIP) {
                        h = AtlantaMain.SEGUNDOS_CANAL_VIP - h
                        GestorSalida.ENVIAR_Im_INFORMACION(personaje!!, "0115;" + (ceil(h.toDouble()).toInt() + 1))
                        return
                    }
                    _tiempoUltVIP = System.currentTimeMillis()
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS(sufijo, personaje!!, msjChat)
                }
                "!" -> {
                    if (personaje!!.tieneCanal(sufijo) || personaje!!.alineacion == Constantes.ALINEACION_NEUTRAL) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    if (personaje!!.deshonor >= 1) {
                        GestorSalida.ENVIAR_Im_INFORMACION(personaje!!, "183")
                        return
                    }
                    if (personaje!!.gradoAlineacion < 3) {
                        GestorSalida.ENVIAR_Im_INFORMACION(personaje!!, "0106")
                        return
                    }
                    if (AtlantaMain.MUTE_CANAL_ALINEACION) {
                        GestorSalida.ENVIAR_Im_INFORMACION(personaje!!, "0115;777777")
                        return
                    }
                    var k: Long
                    if (((System.currentTimeMillis() - _tiempoUltAlineacion)
                                / 1000).also { k = it } < AtlantaMain.SEGUNDOS_CANAL_ALINEACION
                    ) {
                        k = AtlantaMain.SEGUNDOS_CANAL_ALINEACION - k
                        GestorSalida.ENVIAR_Im_INFORMACION(personaje!!, "0115;" + (ceil(k.toDouble()).toInt() + 1))
                        return
                    }
                    _tiempoUltAlineacion = System.currentTimeMillis()
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS(sufijo, personaje!!, msjChat)
                }
                "^" -> {
                    if (personaje!!.tieneCanal(sufijo)) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    if (AtlantaMain.MUTE_CANAL_INCARNAM) {
                        GestorSalida.ENVIAR_Im_INFORMACION(personaje!!, "0115;777777")
                        return
                    }
                    var i: Long
                    if (((System.currentTimeMillis() - _tiempoUltIncarnam) / 1000).also {
                            i = it
                        } < AtlantaMain.SEGUNDOS_CANAL_INCARNAM) {
                        i = AtlantaMain.SEGUNDOS_CANAL_INCARNAM - i
                        GestorSalida.ENVIAR_Im_INFORMACION(personaje!!, "0115;" + (ceil(i.toDouble()).toInt() + 1))
                        return
                    }
                    _tiempoUltIncarnam = System.currentTimeMillis()
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS(sufijo, personaje!!, msjChat)
                }
                ":" -> {
                    if (personaje!!.tieneCanal(sufijo)) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    if (AtlantaMain.MUTE_CANAL_COMERCIO) {
                        GestorSalida.ENVIAR_Im_INFORMACION(personaje!!, "0115;777777")
                        return
                    }
                    var l: Long
                    if (((System.currentTimeMillis() - _tiempoUltComercio) / 1000).also {
                            l = it
                        } < AtlantaMain.SEGUNDOS_CANAL_COMERCIO) {
                        l = AtlantaMain.SEGUNDOS_CANAL_COMERCIO - l
                        GestorSalida.ENVIAR_Im_INFORMACION(personaje!!, "0115;" + (ceil(l.toDouble()).toInt() + 1))
                        return
                    }
                    _tiempoUltComercio = System.currentTimeMillis()
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS(sufijo, personaje!!, msjChat)
                }
                "?" -> {
                    if (personaje!!.tieneCanal(sufijo)) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    if (AtlantaMain.MUTE_CANAL_RECLUTAMIENTO) {
                        GestorSalida.ENVIAR_Im_INFORMACION(personaje!!, "0115;777777")
                        return
                    }
                    var j: Long
                    if (((System.currentTimeMillis() - _tiempoUltReclutamiento)
                                / 1000).also { j = it } < AtlantaMain.SEGUNDOS_CANAL_RECLUTAMIENTO
                    ) {
                        j = AtlantaMain.SEGUNDOS_CANAL_RECLUTAMIENTO - j
                        GestorSalida.ENVIAR_Im_INFORMACION(personaje!!, "0115;" + (ceil(j.toDouble()).toInt() + 1))
                        return
                    }
                    _tiempoUltReclutamiento = System.currentTimeMillis()
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS(sufijo, personaje!!, msjChat)
                }
                "@" -> {
                    if (cuenta!!.admin == 0) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_TODOS(sufijo, personaje!!, msjChat)
                }
                else -> {
                    val nombre = packet2.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()[0]
                    if (nombre.length <= 1) {
                        return
                    }
                    val perso = Mundo.getPersonajePorNombre(nombre)
                    if (perso == null || !perso.enLinea() || perso.esIndetectable()) {
                        GestorSalida.ENVIAR_cMEf_CHAT_ERROR(personaje!!, nombre)
                        return
                    }
                    if (!perso.estaVisiblePara(personaje)) {
                        GestorSalida.ENVIAR_Im_INFORMACION(personaje!!, "114;" + perso.nombre)
                        return
                    }
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(
                        perso,
                        "F",
                        personaje!!.Id,
                        personaje!!.nombre,
                        msjChat
                    )
                    GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(personaje!!, "T", perso.Id, perso.nombre, msjChat)
                    if (personaje!!.estaAusente()) {
                        GestorSalida.ENVIAR_Im_INFORMACION(personaje!!, "072")
                    }
                }
            }
        } catch (e: Exception) {
            redactarLogServidorln(e.toString())
        }

    }

    fun comando_jugador(msjChat: String): Boolean {
        if (!AtlantaMain.PARAM_COMANDOS_JUGADOR) {
            return false
        }
        if (msjChat[0] == '.') {
            var split = msjChat.split(" ".toRegex()).toTypedArray()
            val cmd = split[0]
            val comando = cmd.substring(1).toLowerCase()
            if (AtlantaMain.COMANDOS_VIP.contains(comando)) {
                if (!cuenta!!.esAbonado()) {
                    return false
                }
            } else if (!AtlantaMain.COMANDOS_PERMITIDOS.contains(comando)) {
                return false
            }
            try {
                val objetivo = personaje
                var mapa_celda: String
                val mapa: Mapa?
                val celdaID: Short
                if (cuenta?.bloqueado == true && comando != "token") {
                    ENVIAR_BN_NADA(this)
                    return false
                }
                when (comando) {
                    "convert", "convertir" -> {
                        if (AtlantaMain.VALOR_KAMAS_POR_OGRINA <= 0) {
                            ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "No se puede convertir ahora")
                        } else {
                            if (split.size < 2) {
                                ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "Pon la cantidad a convertir")
                            } else {
                                try {
                                    val cantidad = split[1].toInt()
                                    if (valorValido(cantidad, AtlantaMain.VALOR_KAMAS_POR_OGRINA)) {
                                        ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "Valor invalido")
                                        return true
                                    }
                                    if (!RESTAR_OGRINAS(cuenta!!, cantidad.toLong(), personaje)) {
                                        return true
                                    }
                                    personaje!!.addKamas(
                                        cantidad * AtlantaMain.VALOR_KAMAS_POR_OGRINA.toLong(),
                                        true,
                                        true
                                    )
                                } catch (e: Exception) {
                                    ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "Valor invalido")
                                }
                            }
                        }
                        return true
                    }
                    "servicio", "service", "services", "servicios" -> {
                        if (split.size < 2) {
                            ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, AtlantaMain.MENSAJE_SERVICIOS)
                        } else {
                            try {
                                when (val servicio = split[1].toLowerCase()) {
                                    "guilde", "guild", "gremio" -> {
                                        if (personaje!!.estaDisponible(true, true)) {
                                            return false
                                        }
                                        if (personaje!!.gremio != null || personaje!!.miembroGremio != null) {
                                            return false
                                        }
                                        if (puede_Usar_Servicio(servicio)) {
                                            Accion.realizar_Accion_Estatico(
                                                -2,
                                                "",
                                                personaje!!,
                                                null,
                                                -1,
                                                (-1).toShort()
                                            )
                                        }
                                    }
                                    "scroll", "fullstats", "parcho" -> {
                                        if (personaje!!.estaDisponible(false, false)) {
                                            return false
                                        }
                                        if (puede_Usar_Servicio(servicio)) {
                                            val stats = intArrayOf(124, 118, 123, 119, 125, 126)
                                            for (s in stats) {
                                                if (personaje!!.getStatScroll(s) > 0) {
                                                    ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                                                        personaje!!,
                                                        "Veuillez remettre à zero vos caracteristiques via la Fee Risette avant de vous parchotter."
                                                    )
                                                    return false
                                                }
                                            }
                                            for (s in stats) {
                                                Accion.realizar_Accion_Estatico(
                                                    8,
                                                    "$s,101",
                                                    personaje!!,
                                                    null,
                                                    -1,
                                                    (-1).toShort()
                                                )
                                            }
                                        }
                                    }
                                    "restater", "restarter", "reset" -> {
                                        if (personaje!!.nivel < 30) {
                                            return false
                                        }
                                        if (puede_Usar_Servicio(servicio)) {
                                            personaje!!.resetearStats(false)
                                        }
                                    }
                                    "sortspecial" -> if (puede_Usar_Servicio(servicio)) {
                                        personaje!!.fijarNivelHechizoOAprender(350, 1, false)
                                    }
                                    "sortclasse" -> if (puede_Usar_Servicio(servicio)) {
                                        when (personaje!!.getClaseID(true)) {
                                            Constantes.CLASE_FECA -> personaje!!.fijarNivelHechizoOAprender(
                                                422,
                                                1,
                                                false
                                            )
                                            Constantes.CLASE_OSAMODAS -> personaje!!.fijarNivelHechizoOAprender(
                                                420,
                                                1,
                                                false
                                            )
                                            Constantes.CLASE_ANUTROF -> personaje!!.fijarNivelHechizoOAprender(
                                                425,
                                                1,
                                                false
                                            )
                                            Constantes.CLASE_SRAM -> personaje!!.fijarNivelHechizoOAprender(
                                                416,
                                                1,
                                                false
                                            )
                                            Constantes.CLASE_XELOR -> personaje!!.fijarNivelHechizoOAprender(
                                                424,
                                                1,
                                                false
                                            )
                                            Constantes.CLASE_ZURCARAK -> personaje!!.fijarNivelHechizoOAprender(
                                                412,
                                                1,
                                                false
                                            )
                                            Constantes.CLASE_ANIRIPSA -> personaje!!.fijarNivelHechizoOAprender(
                                                427,
                                                1,
                                                false
                                            )
                                            Constantes.CLASE_YOPUKA -> personaje!!.fijarNivelHechizoOAprender(
                                                410,
                                                1,
                                                false
                                            )
                                            Constantes.CLASE_OCRA -> personaje!!.fijarNivelHechizoOAprender(
                                                418,
                                                1,
                                                false
                                            )
                                            Constantes.CLASE_SADIDA -> personaje!!.fijarNivelHechizoOAprender(
                                                426,
                                                1,
                                                false
                                            )
                                            Constantes.CLASE_SACROGITO -> personaje!!.fijarNivelHechizoOAprender(
                                                421,
                                                1,
                                                false
                                            )
                                            Constantes.CLASE_PANDAWA -> personaje!!.fijarNivelHechizoOAprender(
                                                423,
                                                1,
                                                false
                                            )
                                        }
                                    }
                                    else -> ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "Servicio no existe.")
                                }
                            } catch (e: Exception) {
                                ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "Servicio con excepcion [1].")
                            }
                        }
                        return true
                    }
                    "help", "comandos", "tutorial", "ayuda", "commands", "command", "commandes" -> {
                        if (AtlantaMain.MENSAJE_COMANDOS.isNotEmpty()) {
                            ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, AtlantaMain.MENSAJE_COMANDOS)
                        } else {
                            ENVIAR_cs_CHAT_MENSAJE(
                                personaje!!,
                                "Les commandes disponnible sont :\n<b>.infos</b> - Permet d'obtenir des informations sur le serveur."
                                        + "\n<b>.start</b> - Permet de se teleporter au zaap d'Astrub."
                                        + "\n<b>.staff</b> - Permet de voir les membres du staff connect\u00e9s."
                                        + "\n<b>.boutique</b> - Permet de se teleporter à la map Boutique."
                                        + "\n<b>.points</b> - Savoir ses points boutique."
                                        + "\n<b>.all</b> - Permet d'envoyer un message \u00e0 tous les joueurs."
                                        + "\n<b>.celldeblo</b> - Vous tp a une cellule Libre si vous êtes bloque."
                                        + "\n<b>.banque</b> - Ouvrir la banque nimporte où."
                                        + "\n<b>.maitre</b> -permet cree l'escouade , inviter tout tes mules dans ton groupes et rediriger tout les Messages prives de tes mûles vers le Maître."
                                        + "\n<b>.pass</b> -  permet au joueurs de passer automatiquement ses tours."
                                        + "\n<b>.transfert</b> -  transfert rapide en banque ( Items , Divers et ressources)."
                                        + "\n<b>.tp</b> - Permet de TP tes Personajes sur ta map actuel ( hors Donjon)."
                                        + "\n<b>.join</b> - permet que les Personajes sautotp et rejoignent automatiquement quand un combat et lancer.",
                                "B9121B"
                            )
                        }
                        return true
                    }
                    "join" -> {
                        if (personaje!!.esMaestro()) {
                            if (personaje!!.grupoParty.autoUnir) {
                                personaje!!.grupoParty.autoUnir = false
                                ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Join Off", "B9121B")
                            } else {
                                personaje!!.grupoParty.autoUnir = true
                                ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Join On", "B9121B")
                            }
                        } else {
                            ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Mets toi Maître avant", "B9121B")
                        }
                        return true
                    }
                    "mercadillo" -> {
                        if (personaje!!.exchanger != null) {
                            ENVIAR_EV_CERRAR_VENTANAS(personaje!!, "")
//                            var intercambiando=personaje!!.getIntercambiandoCon(Personaje::class.java) as Personaje?
//                            if (intercambiando != null){
//                                intercambiando.tipoExchange=-1
//                                intercambiando.exchanger=null
//                                intercambiando.cerrarExchange("")
//                                ENVIAR_EV_CERRAR_VENTANAS(intercambiando, "")
//                            }
                        }
                        personaje!!.tipoExchange = -1
                        personaje!!.exchanger = null
                        if (personaje!!.estaDisponible(true, true)) {
                            ENVIAR_BN_NADA(personaje, "INTERCAMBIO NO ESTA DISPONIBLE")
                            return true
                        }
                        if (personaje!!.consultarCofre != null) {
                            ENVIAR_BN_NADA(personaje, "INTERCAMBIO CONSULTAR COFRE")
                            return true
                        }
                        if (personaje!!.consultarCasa != null) {
                            ENVIAR_BN_NADA(personaje, "INTERCAMBIO CONSULTAR CASA")
                            return true
                        }
                        // Me dio paja comprimir el codigod
                        val tipo = 11
                        try {
                            when (tipo.toByte()) {
                                Constantes.INTERCAMBIO_TIPO_MERCADILLO_COMPRAR, Constantes.INTERCAMBIO_TIPO_MERCADILLO_VENDER -> {
                                    if (personaje!!.deshonor >= 5) {
                                        ENVIAR_Im_INFORMACION(personaje!!, "183")
                                        ENVIAR_EV_CERRAR_VENTANAS(personaje!!, "")
                                        return true
                                    }
                                    if (personaje!!.exchanger != null) {
                                        ENVIAR_EV_CERRAR_VENTANAS(personaje!!, "")
                                    }
                                    val mercadillo = Mundo.getPuestoMercadillo(2)
                                    if (mercadillo == null) {
                                        ENVIAR_BN_NADA(personaje)
                                        return true
                                    }
                                    var tipoobj = mercadillo.tipoObjPermitidos
                                    if (mercadillo.iD == 2 && tipo.toByte() == Constantes.INTERCAMBIO_TIPO_MERCADILLO_COMPRAR) {
                                        tipoobj = ""
                                        var c = 0
                                        val tipos: ArrayList<Short>? = ArrayList()
                                        for (obj in mercadillo.objetosMercadillos) {
                                            val modelo = Mundo.getObjeto(obj.objetoID)?.objModelo?.tipo
                                            if (!tipos!!.contains(modelo)) {
                                                if (modelo != null) {
                                                    tipos.add(modelo)
                                                }
                                            }
                                        }
                                        for (tipo in tipos!!) {
                                            tipoobj += if (tipos.lastIndex != c) {
                                                "$tipo,"
                                            } else {
                                                tipo.toString()
                                            }
                                            c += 1
                                        }
                                    }
                                    personaje!!.exchanger = mercadillo
                                    ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(
                                        personaje!!, tipo, "1,10,100;" + tipoobj
                                                + ";" + mercadillo.porcentajeImpuesto + ";" + mercadillo.nivelMax + ";" + mercadillo
                                            .maxObjCuenta + ";-1;" + mercadillo.tiempoVenta
                                    )
                                    if (tipo.toByte() == Constantes.INTERCAMBIO_TIPO_MERCADILLO_VENDER) { // mercadillo vender
                                        ENVIAR_EL_LISTA_EXCHANGER(personaje!!, mercadillo)
                                    }
                                }
                            }
                        } catch (a: Exception) {
                            personaje!!.enviarmensajeNegro("Error en los parametros")
                            return true
                        }
                        personaje!!.tipoExchange = tipo.toByte()
                        return true
                    }
                    "companero" -> {
                        try {
                            if (split.size >= 2) {
                                val compa = Mundo.getPersonajePorNombre(split[1])
                                println(split[1])
                                if (compa != null) {
                                    personaje!!.compañero = compa
                                    return true
                                }
                                return true
                            }
                        } catch (e: Exception) {
                            println("La cagaste")
                        }
                        return true
                    }
                    "probfm" -> {
                        if (personaje?.trabajo != null) {
                            personaje?.trabajo?.mostrarProbabilidades(personaje)
                        }
                        return true
                    }
                    "token" -> {
                        if (split.size < 2) {
                            if (!tokenGenerator.asignarToken(cuenta)) {
                                personaje?.enviarmensajeNegro(
                                    "Esta en una ip No autorizada. " +
                                            "Por favor use su IP original"
                                )
                            }
                        } else {
                            val token = split[1]
                            IpsVerificator.desbloquearCuenta(cuenta, token)
                        }
                        return true
                    }
                    "eco", "economia" -> {
                        thread {
                            Economia.updateEconomia()
                            Economia.economiaActual(personaje)
                        }
                        return true
                    }
                    "comprar_og", "cog" -> {
                        if (personaje == null) {
                            return true
                        }
                        Economia.updateEconomia()
                        when {
                            split.size < 2 -> {
                                personaje?.enviarmensajeNegro(
                                    "Para usar este comando debera usar .cog o .comprar_og\n" +
                                            "en consecuencia debera decirle al comando cuantas ogrinas desea comprar\n" +
                                            "ej: .cog 300\n" +
                                            "Este le mostrará el costo, si desea confirmar deberá utilizar\n" +
                                            "ej: .cog 300 -y"
                                )
                                return true
                            }
                            split.size < 3 -> {
                                var ogrinasD = 0
                                try {
                                    ogrinasD = split[1].toInt()
                                } catch (e: Exception) {
                                    ENVIAR_BN_NADA(personaje)
                                    return true
                                }
                                Economia.comprarOgrinas(ogrinasD, personaje, true)
                            }
                            split.size < 4 -> {
                                var ogrinasD = 0
                                try {
                                    ogrinasD = split[1].toInt()
                                } catch (e: Exception) {
                                    ENVIAR_BN_NADA(personaje)
                                    return true
                                }
                                when (split[2]) {
                                    "-c", "-y" -> {
                                        Economia.comprarOgrinas(ogrinasD, personaje, false)
                                        ENVIAR_BN_NADA(personaje)
                                        return true
                                    }
                                    else -> {
                                        personaje?.enviarmensajeNegro("Comando mal ejecutado.")
                                    }
                                }
                            }
                        }
                        return true
                    }
                    "vender_og", "vog" -> {
                        if (personaje == null) {
                            return true
                        }
                        Economia.updateEconomia()
                        when {
                            split.size < 2 -> {
                                personaje?.enviarmensajeNegro(
                                    "Para usar este comando debera usar .vog o .vender_og\n" +
                                            "en consecuencia debera decirle al comando cuantas ogrinas desea vender\n" +
                                            "ej: .vog 300\n" +
                                            "Este le mostrará la cantidad de Kamas ganadas, si desea confirmar deberá utilizar\n" +
                                            "ej: .vog 300 -y"
                                )
                                return true
                            }
                            split.size < 3 -> {
                                var ogrinasv = 0
                                try {
                                    ogrinasv = split[1].toInt()
                                } catch (e: Exception) {
                                    ENVIAR_BN_NADA(personaje)
                                    return true
                                }
                                Economia.ventaOgrinas(ogrinasv, personaje, true)
                            }
                            split.size < 4 -> {
                                var ogrinasD: Int
                                try {
                                    ogrinasD = split[1].toInt()
                                } catch (e: Exception) {
                                    ENVIAR_BN_NADA(personaje)
                                    return true
                                }
                                when (split[2]) {
                                    "-c", "-y" -> {
                                        Economia.ventaOgrinas(ogrinasD, personaje, false)
                                        ENVIAR_BN_NADA(personaje)
                                        return true
                                    }
                                    else -> {
                                        personaje?.enviarmensajeNegro("Comando mal ejecutado.")
                                    }
                                }
                            }
                        }
                        return true
                    }
                    "block_xp", "block_lv" -> {
                        if (personaje?.xp_bloqueada == false) {
                            personaje?.xp_bloqueada = true
                            personaje?.enviarmensajeRojo("Tu xp ha sido bloqueada, No recibiras mas experiencia en combate")
                        } else {
                            personaje?.xp_bloqueada = false
                            personaje?.enviarmensajeVerde("Tu xp ha sido desbloqueada, Puedes volver a subir de nivel con normalidad")
                        }
                        return true
                    }
                    "relog" -> {
                        personaje?.conectarse()
                        return true
                    }
                    "tp" -> {
                        if (personaje!!.estaDisponible(false, false)) {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Vous êtes occupe", "B9121B")
                            }
                            return true
                        }
                        if (split.size < 2) {
                            if (personaje!!.esMaestro()) {
                                if (personaje!!.mapa.esMazmorra()) {
                                    if (idioma.equals("pt", ignoreCase = true)) {
                                        ENVIAR_cs_CHAT_MENSAJE(
                                            personaje!!,
                                            "Não pode usar este comando em um Calabouço", "B9121B"
                                        )
                                    } else {
                                        ENVIAR_cs_CHAT_MENSAJE(
                                            personaje!!,
                                            "No puedes usar este comando en una mazmorra", "B9121B"
                                        )
                                    }
                                    return true
                                }
                                personaje!!.grupoParty.teleportATodos(personaje!!.mapa.id, personaje!!.celda.id)
                                return true
                            } else {
                                if (idioma.equals("pt", ignoreCase = true)) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        personaje!!,
                                        "Você deve ter seguidores para esta ação",
                                        "B9121B"
                                    )
                                } else {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        personaje!!,
                                        "Debes tener seguidores para esta accion",
                                        "B9121B"
                                    )
                                }
                                return true
                            }
                        }
                        when (split[1]) {
                            "-h", "-help", "/h", "--help", "--h", "-ayuda" -> {
                                if (idioma.equals("pt", ignoreCase = true)) {
                                    personaje?.enviarmensajeNegro(
                                        ".tp  | Teleporta todos os membros do Grupo a sua posição (Ser Líder) \n" +
                                                ".tp (Nome)  | Para ir a um personagem específico (Não precisa estar em grupo) \n" +
                                                ".tp (Nome) all  | Teleporta você e todos os membros do grupo a um personagem específico \n" +
                                                "Cuidado ao teleportar os companheiros do grupo com o último comando. \n" +
                                                "Não se pode teletransportar para personagens que estejam em calabouços."
                                    )
                                }
                                personaje!!.enviarmensajeNegro(
                                    ".tp | para traer a todos tus seguidores a tu posicion " +
                                            "(siendo lider)\n" +
                                            ".tp [nombrePj] | para ir donde un pj en especifico (No necesitan estar en grupo), Ejemplo: " +
                                            "\".tp pedrito\" e iras donde pedrito\n" +
                                            ".tp [nombrePj] all | te lleva a ti y a todo tu grupo a la ubicacion de " +
                                            "un pj en especifico\n" +
                                            "Cuidado con trolear a tus compañeros de grupo con el ultimo comando\n" +
                                            "No se puede hacer tps a dungs tampoco"
                                )
                                return true
                            }
                            "-l", "-pos" -> {
                                if (personaje!!.cuenta.admin < 5) {
                                    return true
                                }
                                val msj = StringBuilder()
                                for (a in Mundo.PERSONAJESONLINE) {
                                    if (a.cuenta.admin > 2 || a.mapa.esMazmorra()) {
                                        continue
                                    } else {
                                        msj.append(a.nombre).append(" En el mapa: [").append(a.mapa.x.toInt())
                                            .append(",").append(a.mapa.y.toInt()).append("]\n")
                                    }
                                }
                                ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(
                                    personaje!!,
                                    "T",
                                    personaje!!.Id,
                                    personaje!!.nombre,
                                    msj.toString()
                                )
                                return true
                            }
                        }
                        val obj = Mundo.getPersonajePorNombre(split[1])
                        if (obj != null) {
                            if (obj.cuenta.admin > 1 && personaje!!.cuenta.admin < 5) {
                                if (Comparador.equalsignore(idioma, "pt")) {
                                    personaje?.enviarmensajeNegro("Este personagem é um Administrador, Lamentavelmente não podemos levá-lo a ele por termos de privacidade")
                                } else personaje!!.enviarmensajeNegro(
                                    "Este personaje es un Administrador, Lamentablemente no podemos llevarte a el " +
                                            "por temas de privacidad"
                                )
                                return true
                            }
                            for (a in Mundo.casas.values) {
                                for (b in a.mapasContenidos) {
                                    if (b == obj.mapa.id && personaje!!.cuenta.admin < 5) {
                                        personaje!!.enviarmensajeNegro("El objetivo esta dentro de una casa, no puedes ir a el")
                                        return true
                                    }
                                }
                            }
                            if (obj.mapa.esMazmorra() && personaje!!.cuenta.admin < 5) {
                                personaje!!.enviarmensajeNegro("El objetivo esta en una mazmorra, no puedes ir a el")
                                return true
                            }
                            if (!obj.enLinea() && personaje!!.cuenta.admin < 5) {
                                personaje!!.enviarmensajeNegro("El objetivo no esta en linea")
                                return true
                            }
                            if (split.size < 3) {
                                if (personaje!!.pelea == null) {
                                    personaje!!.teleport(obj.mapa.id, obj.celda.id)
                                } else {
                                    personaje!!.enviarmensajeNegro("Estas ocupado, No podemos teletransportarte")
                                }
                                return true
                            } else if (split[2].equals("all", ignoreCase = true) && personaje!!.grupoParty != null) {
                                for (pj in personaje!!.grupoParty.miembros) {
                                    if (pj.pelea != null) {
                                        pj.enviarmensajeNegro("Tu equipo se ha transportado a otro lugar mientras estabas en pelea")
                                        personaje!!.enviarmensajeNegro("El personaje " + pj.nombre + " esta ocupado")
                                        continue
                                    }
                                    pj.teleport(obj.mapa.id, obj.celda.id)
                                }
                                return true
                            }
                        } else {
                            personaje!!.enviarmensajeNegro("El personaje no existe")
                        }
                        return true
                    }
                    "banque" -> {
                        try {
                            if (personaje!!.estaDisponible(false, false)) {
                                if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                    ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Vous êtes occupe", "B9121B")
                                }
                                return true
                            }
                            val costo = personaje!!.costoAbrirBanco
                            if (personaje!!.kamas - costo < 0) {
                                ENVIAR_Im_INFORMACION(personaje!!, "1128;$costo")
                                ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(
                                    personaje!!,
                                    10,
                                    costo.toString() + "",
                                    ""
                                )
                            } else {
                                personaje!!.addKamas(-costo.toLong(), false, true)
                                ENVIAR_Im_INFORMACION(personaje!!, "020;$costo")
                                personaje!!.banco.abrirCofre(personaje!!)
                            }
                        } catch (e: Exception) {
                            return true
                        }
                        return true
                    }
                    "celldeblosdfzefezfrezfezdzdz", "desbug" -> {
                        if (personaje!!.estaDisponible(false, true)) {
                            if (personaje!!.pelea != null) {
                                ENVIAR_Im_INFORMACION(personaje!!, "191")
                            } else {
                                ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Vous êtes occupe", "B9121B")
                            }
                            return false
                        }
                        var autorised = true
                        when (personaje!!.mapa.id.toInt()) {
                            10700, 8905, 8911, 8916, 8917, 11095, 9827, 8930, 8932, 8933, 8934, 8935, 8936, 8938, 8939, 9230 -> autorised =
                                false
                        }
                        if (!autorised) return true
                        if (Mundo.getCasaDentroPorMapa(personaje!!.mapa.id) != null) {
                            val mapaN =
                                Mundo.getCasaDentroPorMapa(personaje!!.mapa.id)!!.mapaIDFuera
                            personaje!!.teleport(mapaN, personaje!!.mapa.randomCeldaIDLibre)
                        } else {
                            personaje!!.teleport(personaje!!.mapa.id, personaje!!.mapa.randomCeldaIDLibre)
                        }
                        return true
                    }
                    "boutique" -> {
                        if (personaje!!.estaDisponible(false, true)) {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Vous êtes occupe", "B9121B")
                            }
                            return false
                        }
                        personaje!!.teleport(21455.toShort(), 242.toShort())
                        return true
                    }
                    "noall" -> {
                        personaje!!.removerCanal("~")
                        return true
                    }
                    "todos", "all" -> {
                        split = msjChat.split(" ".toRegex(), 2).toTypedArray()
                        if (split.size < 2) {
                            return true
                        }
                        // if (_perso.getNivel() < 30) {
// GestorSalida.ENVIAR_Im_INFORMACION(_perso, "13");
// return false;
// }
                        var h: Long
                        if (((System.currentTimeMillis() - _tiempoUltAll) / 1000).also {
                                h = it
                            } < AtlantaMain.SEGUNDOS_CANAL_ALL) {
                            h = AtlantaMain.SEGUNDOS_CANAL_ALL - h
                            ENVIAR_Im_INFORMACION(personaje!!, "0115;" + (ceil(h.toDouble()).toInt() + 1))
                            return true
                        }
                        _tiempoUltAll = System.currentTimeMillis()
                        ENVIAR_cMK_CHAT_MENSAJE_TODOS("~", personaje!!, split[1])
                        return true
                    }
                    "vip", "abonado" -> {
                        ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, AtlantaMain.MENSAJE_VIP)
                        return true
                    }
                    "staff" -> {
                        val staff = StringBuilder()
                        var staffO = 0
                        for (perso in Mundo.PERSONAJESONLINE) {
                            try {
                                if (perso.esIndetectable()) {
                                    continue
                                }
                                if (perso.cuenta.admin < 1) {
                                    continue
                                }
                                if (staff.isNotEmpty()) {
                                    staff.append(" - ")
                                }
                                staff.append(perso.nombre)
                                staffO++
                            } catch (ignored: Exception) {
                            }
                        }
                        ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                            personaje!!, "<b>" + staffO + " online: " + staff.toString()
                                    + "</b>"
                        )
                        return true
                    }
                    "info_server", "info", "infos", "online" -> {
                        try {
                            var enLinea = Formulas.segundosON() * 1000.toLong()
                            val dia = (enLinea / 86400000L).toInt()
                            enLinea %= 86400000L
                            val hora = (enLinea / 3600000L).toInt()
                            enLinea %= 3600000L
                            val minuto = (enLinea / 60000L).toInt()
                            enLinea %= 60000L
                            val segundo = (enLinea / 1000L).toInt()
                            when {
                                idioma.equals("fr", ignoreCase = true) -> {
                                    ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                                        personaje!!, "====================\n<b>"
                                                + AtlantaMain.NOMBRE_SERVER + "</b>\nUptime: " + dia + "j " + hora + "h " + minuto + "m " + segundo
                                                + "s\n" + "Joueurs en ligne: " + ServidorServer.nroJugadoresLinea() + "\n" + "Record de connexions: "
                                                + ServidorServer.recordJugadores + "\n" + "====================\nTimeZone of the Server\n" + ServidorServer.fechaConHora + "\n===================="
                                    )
                                }
                                idioma.equals("pt", ignoreCase = true) -> {
                                    ENVIAR_M145_MENSAJE_PANEL_INFORMACION(
                                        personaje!!, "====================\n<b>"
                                                + AtlantaMain.NOMBRE_SERVER + "</b>\nOnline: " + dia + "d " + hora + "h " + minuto + "m " + segundo
                                                + "s\n" + "Jogadores Online: " + ServidorServer.nroJugadoresLinea() + "\n" + "Recorde de conexão: "
                                                + ServidorServer.recordJugadores + "\n" + "====================\nHórario do servidor\n"
                                                + ServidorServer.fechaConHora + "\n===================="
                                    )
                                }
                                else -> {
                                    ENVIAR_M145_MENSAJE_PANEL_INFORMACION(
                                        personaje!!, "====================\n<b>"
                                                + AtlantaMain.NOMBRE_SERVER + "</b>\nEn Linea: " + dia + "d " + hora + "h " + minuto + "m " + segundo
                                                + "s\n" + "Jugadores en linea: " + ServidorServer.nroJugadoresLinea() + "\n" + "Record de conexion: "
                                                + ServidorServer.recordJugadores + "\n" + "====================\nHorario del servidor\n"
                                                + ServidorServer.fechaConHora + "\n===================="
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "Ocurrio un error")
                        }
                        return true
                    }
                    "deformar" -> {
                        if (objetivo!!.pelea != null) {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_BAT2_CONSOLA(personaje!!, "Le joueur est en combat.")
                            } else {
                                ENVIAR_BAT2_CONSOLA(personaje!!, "El personaje esta en un combate")
                            }
                            return true
                        }
                        objetivo.deformar()
                        objetivo.refrescarEnMapa()
                        objetivo.modificarA(
                            Personaje.RA_NO_PUEDE_MOVER_TODAS_DIRECCIONES,
                            Personaje.RA_NO_PUEDE_MOVER_TODAS_DIRECCIONES xor 0
                        )
                        ENVIAR_AR_RESTRICCIONES_PERSONAJE(objetivo)
                        return true
                    }
                    "forma" -> {
                        var numShort: Short = 0
                        try {
                            if (split.size > 1) numShort = split[1].toShort()
                        } catch (ignored: Exception) {
                        }
                        if (numShort < 0) {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_BAT2_CONSOLA(personaje!!, "Gfx ID invalide")
                            } else {
                                ENVIAR_BAT2_CONSOLA(personaje!!, "Gfx ID invalida")
                            }
                            return true
                        }
                        objetivo!!.setGfxID(numShort)
                        objetivo.refrescarEnMapa()
                        objetivo.modificarA(
                            Personaje.RA_NO_PUEDE_MOVER_TODAS_DIRECCIONES,
                            Personaje.RA_NO_PUEDE_MOVER_TODAS_DIRECCIONES xor Personaje.RA_NO_PUEDE_MOVER_TODAS_DIRECCIONES
                        )
                        ENVIAR_AR_RESTRICCIONES_PERSONAJE(objetivo)
                        return true
                    }
                    "maguear", "elemental", "fmcac", "fm" -> {
                        val exObj = personaje!!.getObjPosicion(Constantes.OBJETO_POS_ARMA)
                        if (exObj == null) {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "Vous ne portez aucune arme.")
                            } else {
                                ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "NO TIENES UN ARMA A MAGUEAR")
                            }
                            return false
                        }
                        if (split.size < 2) {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                                    personaje!!,
                                    "Vous devez specifier un argument (air - terre - eau - feu)."
                                )
                            } else {
                                ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                                    personaje!!,
                                    "Debes especificar un elemento (aire - tierra - agua - fuego)."
                                )
                            }
                            return false
                        }
                        var statFM = 0
                        when (split[1].toLowerCase()) {
                            "eau", "agua", "suerte", "water" -> statFM = 96
                            "terre", "tierra", "fuerza", "earth" -> statFM = 97
                            "air", "aire", "agilidad", "agi" -> statFM = 98
                            "feu", "fuego", "inteligencia", "fire" -> statFM = 99
                        }
                        if (statFM == 0) {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                                    personaje!!,
                                    "Vous devez specifier un argument (air - terre - eau - feu)."
                                )
                            } else {
                                ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                                    personaje!!,
                                    "Debes especificar un elemento (aire - tierra - agua - fuego)."
                                )
                            }
                            return false
                        }
                        val potenciaFM = 85
                        exObj.forjaMagiaGanar(statFM, potenciaFM)
                        ENVIAR_Im_INFORMACION(personaje!!, "1OBJETO_CAMBIO_DAÑO_ELEMENTAL")
                        ENVIAR_OCK_ACTUALIZA_OBJETO(personaje!!, exObj)
                        ENVIAR_As_STATS_DEL_PJ(personaje!!)
                        SALVAR_OBJETO(exObj)
                        return true
                    }
                    "exo" -> {
                        if (split.size < 2) {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                                    personaje!!,
                                    "Vous devez specifier un argument (pa/po/pm/invo) space (coiffe/cape/bottes/anndroite/anngauche/ceinture/amulette)."
                                )
                            } else {
                                ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                                    personaje!!,
                                    "Vous devez specifier un argument (pa/po/pm/invo) space (coiffe/cape/bottes/anndroite/anngauche/ceinture/amulette)."
                                )
                            }
                            return false
                        }
                        var statID = 0
                        when (split[1].toLowerCase()) {
                            "pa" -> statID = Constantes.STAT_MAS_PA
                            "pm" -> statID = Constantes.STAT_MAS_PM
                            "po" -> statID = Constantes.STAT_MAS_ALCANCE
                            "invo" -> statID = Constantes.STAT_MAS_CRIATURAS_INVO
                        }
                        if (statID == 0) {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                                    personaje!!,
                                    "Vous devez specifier un argument (pa/po/pm/invo)."
                                )
                            } else {
                                ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                                    personaje!!,
                                    "Vous devez specifier un argument (pa/po/pm/invo)."
                                )
                            }
                            return false
                        }
                        var pos: Byte = -1
                        when (split[2].toLowerCase()) {
                            "coiffe", "sombrero" -> pos = Constantes.OBJETO_POS_SOMBRERO
                            "cape", "capa" -> pos = Constantes.OBJETO_POS_CAPA
                            "bottes", "botas" -> pos = Constantes.OBJETO_POS_BOTAS
                            "anndroite", "anillod" -> pos = Constantes.OBJETO_POS_ANILLO_DERECHO
                            "anngauche", "anilloi" -> pos = Constantes.OBJETO_POS_ANILLO1
                            "ceinture", "cinturon" -> pos = Constantes.OBJETO_POS_CINTURON
                            "amulette", "amuleto" -> pos = Constantes.OBJETO_POS_AMULETO
                        }
                        if (pos.toInt() == -1) {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                                    personaje!!,
                                    "Vous devez specifier un argument (coiffe/cape/bottes/anndroite/anngauche/ceinture/amulette)."
                                )
                            } else {
                                ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                                    personaje!!,
                                    "Vous devez specifier un argument (coiffe/cape/bottes/anndroite/anngauche/ceinture/amulette)."
                                )
                            }
                            return false
                        }
                        val objeto = personaje!!.getObjPosicion(pos)
                        if (objeto == null) {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "Vous ne portez aucune objet.")
                            } else {
                                ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "NO TIENES UN ARMA A MAGUEAR")
                            }
                            return false
                        }
                        val cantStat = objeto.getStatValor(statID)
                        if (cantStat != 0) {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "Vous objet posee il stat")
                            } else {
                                ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "YA TIENE UN STAT")
                            }
                            return false
                        }
                        val statsExo = intArrayOf(
                            Constantes.STAT_MAS_PA, Constantes.STAT_MAS_PM, Constantes.STAT_MAS_ALCANCE,
                            Constantes.STAT_MAS_CRIATURAS_INVO
                        )
                        for (s in statsExo) {
                            if (objeto.tieneStatExo(s)) {
                                if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                    ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                                        personaje!!,
                                        "Vous ne pouvez pas depasser un exo par item."
                                    )
                                } else {
                                    ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "YA ESTA CON EXOMAGIA")
                                }
                                return false
                            }
                        }
                        objeto.fijarStatValor(statID, 1)
                        ENVIAR_Im_INFORMACION(personaje!!, "1OBJETO_MAGUEADO")
                        ENVIAR_OCK_ACTUALIZA_OBJETO(personaje!!, objeto)
                        ENVIAR_As_STATS_DEL_PJ(personaje!!)
                        SALVAR_OBJETO(objeto)
                        return true
                    }
                    "grupo", "group" -> {
                        if (personaje!!.grupoParty == null) {
                            val idWeb = GET_ID_WEB(cuenta!!.nombre)
                            if (idWeb <= 0) {
                                return false
                            }
                            val grupo: Grupo
                            val p = ArrayList<Personaje>()
                            for (pe in Mundo.PERSONAJESONLINE) {
                                if (pe.grupoParty != null) {
                                    continue
                                }
                                if (GET_ID_WEB(pe.cuenta.nombre) == idWeb) {
                                    p.add(pe)
                                }
                            }
                            if (p.size >= 2) {
                                grupo = Grupo()
                                for (pe in p) {
                                    ENVIAR_PM_AGREGAR_PJ_GRUPO_A_GRUPO(grupo, pe.stringInfoGrupo())
                                    grupo.addIntegrante(pe)
                                    pe.mostrarGrupo()
                                }
                            }
                        }
                        return true
                    }
                    "liderip", "leaderip" -> {
                        try {
                            split = msjChat.split(" ".toRegex(), 2).toTypedArray()
                            var b = false
                            if (split.size > 1) {
                                val onOff = split[1]
                                when (onOff.toLowerCase()) {
                                    "on", "true", "1" -> b = true
                                }
                            } else {
                                personaje!!.enviarmensajeNegro("Tienes que poner .liderip on o .liderip off para habilitar o deshabilitar")
                                personaje!!.enviarmensajeRojo("ADVERTENCIA: COMANDO EN ESTADO ALPHA, OSEA INESTABLE Y PUEDE GENERARTE BUGS. \n damos gracias de antemano por reportar los bugs y lograr generar un mejor comando y servicio para todos ustedes")
                                return true
                            }
                            if (b) {
                                personaje!!.enviarmensajeRojo("Es el lider de IP ahora")
                                personaje!!.Multis.clear()
                                for (p in Mundo.PERSONAJESONLINE) {
                                    if (p.cuenta.actualIP == personaje!!.cuenta.actualIP) {
                                        p.EsliderIP = false
                                        p.LiderIP = personaje!!
                                        personaje!!.Multis.add(p)
                                    }
                                }
                                personaje!!.EsliderIP = true
                                return true
                            } else {
                                personaje!!.enviarmensajeRojo("Lider ip deshabilitado")
                                personaje!!.Multis.clear()
                                for (p in Mundo.PERSONAJESONLINE) {
                                    if (p.cuenta.actualIP == personaje!!.cuenta.actualIP) {
                                        p.EsliderIP = false
                                        p.LiderIP = null
                                        p.Multi = null
                                    }
                                }
                                return true
                            }
                        } catch (e: Exception) {
                            personaje!!.enviarmensajeNegro("Error en el comando")
                            return true
                        }
                    }
                    "master", "leader", "lider", "maitre", "maestro" -> {
                        split = msjChat.split(" ".toRegex(), 2).toTypedArray()
                        var b = false
                        if (split.size > 1) {
                            val onOff = split[1]
                            when (onOff.toLowerCase()) {
                                "on", "true", "1" -> b = true
                            }
                        }
                        if (personaje!!.grupoParty != null) {
//                            personaje!!._multis.clear()
                            if (personaje!!.grupoParty.esLiderGrupo(personaje!!)) {
//                                for (p in Mundo.PERSONAJESONLINE) {
//                                    if (p.cuenta.actualIP == personaje!!.cuenta.actualIP) {
//                                        p._esliderIP = false
//                                        p._liderIP = personaje!!
//                                        personaje!!._multis.add(p)
//                                    }
//                                }
//                                personaje!!._esliderIP = true
//                                personaje!!.enviarmensajeNegro("Eres el lider de IP ahora")
                                personaje!!.grupoParty.activarMaestro(b, false)
                                if (b) {
                                    if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                        ENVIAR_cs_CHAT_MENSAJE(personaje!!, "maître On", "B9121B")
                                    } else {
                                        personaje!!.enviarmensajeRojo("Maestro Activado!")
                                    }
                                } else {
                                    if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                        ENVIAR_cs_CHAT_MENSAJE(personaje!!, "maître Off", "B9121B")
                                    } else {
                                        personaje!!.enviarmensajeRojo("Maestro Desactivado!")
                                    }
                                }
                            } else {
                                if (cuenta!!.idioma.equals("fr", ignoreCase = true)) { // nada
                                } else {
//                                    personaje!!.enviarmensajeRojo("Es el lider de IP ahora")
//                                    personaje!!._multis.clear()
//                                    for (p in Mundo.PERSONAJESONLINE) {
//                                        if (p.cuenta.actualIP == personaje!!.cuenta.actualIP) {
//                                            p._esliderIP = false
//                                            p._liderIP = personaje!!
//                                            personaje!!._multis.add(p)
//                                        }
//                                    }
//                                    personaje!!._esliderIP = true
//                                    personaje!!.enviarmensajeRojo("Eres lider de IP pero no de grupo")
                                }
                            }
                            //							if (b) {
//								_perso.getGrupoParty().dejarGrupo(_perso, false);
//							}
                        } else {
                            val integrantes = ArrayList<Personaje?>()
                            integrantes.add(personaje)
//                            personaje!!._multis.clear()
                            for (pe in Mundo.PERSONAJESONLINE) {
                                if (pe.grupoParty != null) {
                                    continue
                                }
                                if (pe.cuenta.actualIP == personaje!!.cuenta.actualIP) {
                                    if (!integrantes.contains(pe)) {
                                        integrantes.add(pe)
                                    }
//                                    if (pe.cuenta.actualIP == personaje!!.cuenta.actualIP) {
//                                        pe._esliderIP = false
//                                        pe._liderIP = personaje!!
//                                        personaje!!._multis.add(pe)
//                                    }
                                }
                            }
//                            personaje!!._esliderIP = true
//                            personaje!!.enviarmensajeRojo("Ahora eres lider de IP")
                            if (integrantes.size >= 2) {
                                val grupo = Grupo()
                                for (pe in integrantes) {
                                    ENVIAR_PM_AGREGAR_PJ_GRUPO_A_GRUPO(grupo, pe!!.stringInfoGrupo())
                                    grupo.addIntegrante(pe)
                                    pe.mostrarGrupo()
                                }
                                personaje!!.grupoParty.activarMaestro(b, false)
                                if (b) {
                                    if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                        ENVIAR_cs_CHAT_MENSAJE(personaje!!, "maître On", "B9121B")
                                    } else {
                                        personaje!!.enviarmensajeRojo("Te has unido a un grupo con: Maestro Activado!")
                                    }
                                } else {
                                    if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                        ENVIAR_cs_CHAT_MENSAJE(personaje!!, "maître Off", "B9121B")
                                    } else {
                                        personaje!!.enviarmensajeRojo("Te has unido a un grupo con: Maestro Desactivado!")
                                    }
                                }
                            } else {
                                if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                    ENVIAR_cs_CHAT_MENSAJE(personaje!!, "0 Mules sur la map", "B9121B")
                                } else {
                                    personaje!!.enviarmensajeRojo("Necesitas un grupo")
                                }
                            }
                        }
                        return true
                    }
                    "buscargrupo" -> {
                        split = msjChat.split(" ".toRegex(), 4).toTypedArray()
                        return if (split.size > 1) {
                            val onOff = split[1]
                            if (onOff.toLowerCase().equals("off", ignoreCase = true)) {
                                personaje!!.Busquedagrupo = "0"
                                return true
                            }
                            if (split.size < 3) {
                                if (personaje!!.grupoParty != null && !personaje!!.grupoParty.esLiderGrupo(personaje!!)) {
                                    personaje!!.enviarmensajeNegro("No debes pertenecer a ningun grupo, o ser el lider de grupo para continuar")
                                    return true
                                } else if (personaje!!.grupoParty != null && personaje!!.grupoParty.esLiderGrupo(
                                        personaje!!
                                    ) && personaje!!.grupoParty.miembros.size < 8
                                ) {
                                    for (a in Mundo.PERSONAJESONLINE) {
                                        if (a.Busquedagrupo.equals("0", ignoreCase = true)) {
                                            continue
                                        }
                                        if (a.Busquedagrupo.equals(
                                                onOff,
                                                ignoreCase = true
                                            ) && a.grupoParty.miembros.size < 8
                                        ) {
                                            if (!a.estaVisiblePara(personaje)) {
                                                personaje!!.enviarmensajeRojo("Lo sentimos, no puedes unirte a este grupo. Por favor busca otro grupo")
                                                return true
                                            }
                                            return if (a.grupoParty.miembros.size + personaje!!.grupoParty.miembros.size <= 8) {
                                                val grupo = ArrayList(personaje!!.grupoParty.miembros)
                                                for (pj in grupo) {
                                                    if (pj.grupoParty != null) {
                                                        pj.grupoParty.dejarGrupo(pj, false)
                                                    }
                                                    a.grupoParty.addIntegrante(pj)
                                                    ENVIAR_PM_AGREGAR_PJ_GRUPO_A_GRUPO(
                                                        a.grupoParty,
                                                        pj.stringInfoGrupo()
                                                    )
                                                    pj.mostrarGrupo()
                                                    pj.enviarmensajeNegro("Tu grupo se ha fusionado con el tag: $onOff")
                                                }
                                                if (a.grupoParty.miembros.size == 8) {
                                                    a.Busquedagrupo = "0"
                                                }
                                                personaje!!.Busquedagrupo = "0"
                                                true
                                            } else {
                                                personaje!!.enviarmensajeRojo("Por favor busca otro tag, tu grupo no puede unirse a este")
                                                true
                                            }
                                        }
                                    }
                                    personaje!!.enviarmensajeNegro("Tu tag de busqueda se ha establecido en: $onOff")
                                    personaje!!.Busquedagrupo = onOff
                                    return true
                                } else if (personaje!!.grupoParty != null && personaje!!.grupoParty.miembros.size == 8) {
                                    personaje!!.enviarmensajeNegro("Tu grupo esta lleno, no puedes buscar mas participantes")
                                    personaje!!.Busquedagrupo = "0"
                                    return true
                                }
                            }
                            for (a in Mundo.PERSONAJESONLINE) {
                                if (a.Busquedagrupo.equals("0", ignoreCase = true)) {
                                    continue
                                }
                                if (a.Busquedagrupo.equals(
                                        onOff,
                                        ignoreCase = true
                                    ) && a.grupoParty.miembros.size < 8
                                ) {
                                    if (split.size > 2 && split[2].equals("info", ignoreCase = true)) {
                                        personaje!!.enviarmensajeRojo("Informacion especifica del grupo: $onOff")
                                        val msg = StringBuilder()
                                        for (pj in a.grupoParty.miembros) {
                                            if (pj.grupoParty.liderGrupo === pj) {
                                                personaje!!.enviarmensajeRojo(
                                                    "Lider: " + pj.nombre + " Nivel: " + pj.nivel + " Clase: " + Constantes.getNombreClase(
                                                        pj.getClaseID(true).toInt()
                                                    )
                                                )
                                            } else {
                                                msg.append("Integrante: ").append(pj.nombre).append(" Nivel: ")
                                                    .append(pj.nivel).append(" Clase: ")
                                                    .append(Constantes.getNombreClase(pj.getClaseID(true).toInt()))
                                                    .append("\n")
                                            }
                                            if (pj.grupoParty.miembros[pj.grupoParty.miembros.size - 1] === pj) {
                                                grupo_Localizar_especifico(pj.grupoParty)
                                            }
                                        }
                                        personaje!!.enviarmensajeNegro(msg.toString())
                                        return true
                                    } else if (split.size > 2 && split[2].equals("msg", ignoreCase = true)) {
                                        if (personaje === a) {
                                            personaje!!.enviarmensajeRojo("No te envies mensajes privados a ti mismo")
                                            return true
                                        }
                                        if (!a.enLinea() || a.esIndetectable()) {
                                            ENVIAR_cMEf_CHAT_ERROR(personaje!!, a.nombre)
                                            return true
                                        }
                                        if (!a.estaVisiblePara(personaje)) {
                                            ENVIAR_Im_INFORMACION(personaje!!, "114;" + a.nombre)
                                            return true
                                        }
                                        ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(
                                            a,
                                            "F",
                                            personaje!!.Id,
                                            personaje!!.nombre,
                                            split[3]
                                        )
                                        ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(personaje!!, "T", a.Id, a.nombre, split[3])
                                        if (personaje!!.estaAusente()) {
                                            ENVIAR_Im_INFORMACION(personaje!!, "072")
                                        }
                                        return true
                                    } else if (split.size > 2) {
                                        personaje!!.enviarmensajeRojo("Probablemente ejecutaste mal el comando, puedes usar:")
                                        personaje!!.enviarmensajeNegro(".buscargrupo [tag] msg [mensaje] para enviar un mensaje privado al lider de ese grupo")
                                        personaje!!.enviarmensajeNegro(".buscargrupo [tag] info para ver la informacion especifica del grupo")
                                        return true
                                    }
                                    if (!a.estaVisiblePara(personaje)) {
                                        personaje!!.enviarmensajeRojo("Lo sentimos, no puedes unirte a este grupo. Por favor busca otro grupo")
                                        return true
                                    }
                                    personaje?.let { a.grupoParty.addIntegrante(it) }
                                    ENVIAR_PM_AGREGAR_PJ_GRUPO_A_GRUPO(a.grupoParty, personaje!!.stringInfoGrupo())
                                    personaje!!.mostrarGrupo()
                                    personaje!!.enviarmensajeNegro("Te has unido al grupo con el tag: $onOff")
                                    if (a.grupoParty.miembros.size == 8) {
                                        a.Busquedagrupo = "0"
                                    }
                                    return true
                                }
                            }
                            personaje!!.Busquedagrupo = onOff
                            val grupo = Grupo()
                            grupo.addIntegrante(personaje!!)
                            personaje!!.mostrarGrupo()
                            personaje!!.enviarmensajeNegro("Estas buscando grupo, Si alguien no es de tu agrado, omitelo para la sesion, o agregalo a enemigos y no podra unirse a tu grupo")
                            true
                        } else {
                            personaje!!.enviarmensajeRojo("Grupos disponibles:")
                            val msg = StringBuilder()
                            for (a in Mundo.PERSONAJESONLINE) {
                                if (a.grupoParty != null && !a.Busquedagrupo.equals(
                                        "0",
                                        ignoreCase = true
                                    ) && a.grupoParty.miembros.size < 8
                                ) {
                                    if (a.estaVisiblePara(personaje)) {
                                        msg.append("-Grupo: \"").append(a.Busquedagrupo).append("\" Integrantes: ")
                                            .append(a.grupoParty.miembros.size).append("/8 Nivel promedio: ")
                                            .append(a.grupoParty.nivelGrupo / a.grupoParty.miembros.size)
                                            .append(" Lider: ").append(a.nombre).append("\n")
                                    } else {
                                        msg.append("-Grupo: \"").append(a.Busquedagrupo).append("\" Integrantes: ")
                                            .append(a.grupoParty.miembros.size).append("/8 Nivel promedio: ")
                                            .append(a.grupoParty.nivelGrupo / a.grupoParty.miembros.size)
                                            .append(" Lider: ").append(a.nombre).append(" GRUPO NO DISPONIBLE \n")
                                    }
                                } else if (a.grupoParty != null && a.grupoParty.miembros.size == 8 && !a.Busquedagrupo.equals(
                                        "0",
                                        ignoreCase = true
                                    )
                                ) {
                                    a.Busquedagrupo = "0"
                                } else if (a.grupoParty != null) {
                                    a.Busquedagrupo = "0"
                                }
                            }
                            personaje!!.enviarmensajeNegro(msg.toString())
                            personaje!!.enviarmensajeRojo("Para buscar su informacion especifica por favor, usa buscargrupo [tag] info\nPara mandar un mensaje privado a su lider por favor usa buscargrupo [tag] msg [mensaje]")
                            true
                        }
                    }
                    "eleve", "slave", "esclave", "discipulo", "esclavo", "follower", "seguidor" -> {
                        if (personaje!!.grupoParty != null) {
                            if (personaje!!.grupoParty.addAlumno(personaje!!)) {
                                if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                    ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Vous êtes desormais un suiveur.", "B9121B")
                                } else {
                                    personaje!!.enviarmensajeRojo("Esclavo activado!")
                                }
                            } else {
                                if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                    ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Vous n'êtes plus un suiveur.", "B9121B")
                                } else {
                                    personaje!!.enviarmensajeRojo("Esclavo desactivado!")
                                }
                            }
                        } else {
                            ENVIAR_Im_INFORMACION(personaje!!, "1YOU_NEED_GROUP")
                        }
                        return true
                    }
                    "jour" -> {
                        personaje!!.setDeDia()
                        ENVIAR_Im_INFORMACION(personaje!!, if (personaje!!.esDeDia()) "1DAY_ON" else "1DAY_OFF")
                        ENVIAR_BT_TIEMPO_SERVER(personaje!!)
                        return true
                    }
                    "nuit" -> {
                        personaje!!.setDeNoche()
                        ENVIAR_Im_INFORMACION(personaje!!, if (personaje!!.esDeNoche()) "1NIGHT_ON" else "1NIGHT_OFF")
                        ENVIAR_BT_TIEMPO_SERVER(personaje!!)
                        return true
                    }
                    "passTurn", "pasarTurno", "pass" -> {
//                        if (personaje!!.cuenta.vip == 0) {
//                            ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Reserve au V.I.P", "B9121B")
//                            return true
//                        }
                        personaje!!.comandoPasarTurno = !personaje!!.comandoPasarTurno
                        if (personaje!!.comandoPasarTurno) {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Pass On", "B9121B")
                            } else {
                                ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Pass On", "B9121B")
                            }
                        } else {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Pass Off", "B9121B")
                            } else {
                                ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Pass Off", "B9121B")
                            }
                        }
                        return true
                    }
                    "prisma", "prisme" -> {
                        if (personaje!!.estaDisponible(true, true)) {
                            return false
                        }
                        Accion.realizar_Accion_Estatico(201, "2,1", personaje!!, null, -1, (-1).toShort())
                        return true
                    }
                    "caceria", "chasse" -> {
                        if (Mundo.NOMBRE_CACERIA.isEmpty() || Mundo.KAMAS_OBJ_CACERIA.isEmpty()) {
                            ENVIAR_Im_INFORMACION(personaje!!, "1EVENTO_CACERIA_DESACTIVADO")
                            return true
                        }
                        val victima = Mundo.getPersonajePorNombre(Mundo.NOMBRE_CACERIA)
                        if (victima == null || !victima.enLinea()) {
                            ENVIAR_Im_INFORMACION(personaje!!, "1211")
                            return true
                        }
                        if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                            ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(
                                personaje!!, "", 0, Mundo.NOMBRE_CACERIA, "RECOMPENSE CHASSE - "
                                        + Mundo.mensajeCaceria()
                            )
                        } else {
                            ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(
                                personaje!!, "", 0, Mundo.NOMBRE_CACERIA,
                                "RECOMPENSA CACERIA - " + Mundo.mensajeCaceria()
                            )
                        }
                        ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(
                            personaje!!, victima.mapa.x.toString() + "|" + victima.mapa
                                .y
                        )
                        return true
                    }
                    "lvl", "nivel", "level", "alignement", "alineacion", "alin", "align" -> {
                        if (personaje!!.estaDisponible(true, true)) {
                            return false
                        }
                        if (AtlantaMain.NIVEL_MAX_ESCOGER_NIVEL <= 1) {
                            ENVIAR_BN_NADA(personaje, "MAX ESCOGER NIVEL ES 1")
                            return false
                        }
                        ENVIAR_bA_ESCOGER_NIVEL(personaje!!)
                        return true
                    }
                    "taller", "atelier" -> {
                        if (personaje!!.estaDisponible(true, true)) {
                            return false
                        }
                        if (personaje!!.alineacion == Constantes.ALINEACION_BONTARIANO) {
                            personaje!!.teleport(8731.toShort(), 381.toShort())
                        } else if (personaje!!.alineacion == Constantes.ALINEACION_BRAKMARIANO) {
                            personaje!!.teleport(8732.toShort(), 367.toShort())
                        }
                        return true
                    }
                    "salvar", "guardar", "save" -> {
                        if (System.currentTimeMillis() - _ultSalvada > 300000) {
                            _ultSalvada = System.currentTimeMillis()
                            SALVAR_PERSONAJE(personaje!!, true)
                            ENVIAR_Im_INFORMACION(personaje!!, "1PERSONAJE_GUARDADO_OK")
                        }
                        return true
                    }
                    "feria" -> {
                        if (personaje!!.estaDisponible(true, true)) {
                            return false
                        }
                        personaje!!.teleport(6863.toShort(), 324.toShort())
                        return true
                    }
                    "turn", "turno" -> {
                        try {
                            personaje!!.pelea.checkeaPasarTurno()
                        } catch (ignored: Exception) {
                        }
                        ENVIAR_BN_NADA(personaje)
                        return true
                    }
                    "endaction", "finaccion", "finalizaraccion" -> {
                        try {
                            personaje!!.pelea.finAccion(personaje!!)
                            if (personaje!!.LiderIP != null) {
                                personaje!!.LiderIP.pelea.finAccion(personaje!!.LiderIP)
                            }
                        } catch (e: Exception) {
                            ENVIAR_BN_NADA(personaje)
                        }
                        return true
                    }
                    "reports", "reportes" -> {
                        if (cuenta!!.admin > 0) {
                            ENVIAR_bD_LISTA_REPORTES(personaje!!, GET_LISTA_REPORTES(cuenta!!))
                        }
                        return true
                    }
                    "recurso", "ressource" -> {
                        if (personaje!!.estaDisponible(true, false)) {
                            return false
                        }
                        Atlanta_Sistema_Recurso()
                        return true
                    }
                    "tickets", "misboletos", "boletos" -> {
                        if (personaje!!.estaDisponible(true, false)) {
                            return false
                        }
                        val boletos = Mundo.misBoletos(personaje!!.Id)
                        if (boletos.isEmpty()) {
                            ENVIAR_Im_INFORMACION(personaje!!, "1DONT_HAVE_TICKETS_LOTERIE")
                        } else {
                            ENVIAR_Im_INFORMACION(personaje!!, "1YOUR_NUMBERS_TICKETS_LOTERIE;$boletos")
                        }
                        return true
                    }
                    "rates" -> {
                        personaje!!.mostrarRates()
                        return true
                    }
                    "teodex", "ozeydex", "collection", "cardsmobs", "coleccion", "album", "zafidex", "bestiarie" -> {
                        ENVIAR_ÑF_BESTIARIO_MOBS(this, personaje!!.listaCardMobs())
                        return true
                    }
                    "scroll", "parcho", "fullstats" -> {
                        if (personaje!!.estaDisponible(false, false)) {
                            return false
                        }
                        Accion.realizar_Accion_Estatico(8, "124,101", personaje!!, null, -1, (-1).toShort())
                        Accion.realizar_Accion_Estatico(8, "118,101", personaje!!, null, -1, (-1).toShort())
                        Accion.realizar_Accion_Estatico(8, "123,101", personaje!!, null, -1, (-1).toShort())
                        Accion.realizar_Accion_Estatico(8, "119,101", personaje!!, null, -1, (-1).toShort())
                        Accion.realizar_Accion_Estatico(8, "125,101", personaje!!, null, -1, (-1).toShort())
                        Accion.realizar_Accion_Estatico(8, "126,101", personaje!!, null, -1, (-1).toShort())
                        return true
                    }
                    "guild", "creargremio", "guilde", "gremio", "crear_gremio" -> {
                        if (personaje!!.estaDisponible(true, true)) {
                            return false
                        }
                        if (personaje!!.gremio != null || personaje!!.miembroGremio != null) {
                            return false
                        }
                        Accion.realizar_Accion_Estatico(-2, "", personaje!!, null, -1, (-1).toShort())
                        return true
                    }
                    "jcj", "pvp" -> {
                        if (personaje!!.estaDisponible(false, true)) {
                            return false
                        }
                        mapa_celda = AtlantaMain.PVP_MAPA_CELDA
                        if (mapa_celda.isEmpty()) {
                            mapa_celda = "951"
                        }
                        split = mapa_celda.split(";".toRegex()).toTypedArray()
                        mapa_celda = split[getRandomInt(0, split.size - 1)]
                        mapa = Mundo.getMapa(mapa_celda.split(",".toRegex()).toTypedArray()[0].toShort())
                        if (mapa != null) {
                            celdaID = if (mapa_celda.split(",".toRegex()).toTypedArray().size == 1) {
                                mapa.randomCeldaIDLibre
                            } else {
                                mapa_celda.split(",".toRegex()).toTypedArray()[1].toShort()
                            }
                            personaje!!.teleport(mapa.id, celdaID)
                        }
                        return true
                    }
                    "inicio" -> {
                        if (personaje!!.estaDisponible(false, true)) {
                            return false
                        }
                        mapa_celda = AtlantaMain.START_MAPA_CELDA
                        if (mapa_celda.isEmpty()) {
                            mapa_celda = "7411"
                        }
                        split = mapa_celda.split(";".toRegex()).toTypedArray()
                        mapa_celda = split[getRandomInt(0, split.size - 1)]
                        mapa = Mundo.getMapa(mapa_celda.split(",".toRegex()).toTypedArray()[0].toShort())
                        if (mapa != null) {
                            celdaID = if (mapa_celda.split(",".toRegex()).toTypedArray().size == 1) {
                                mapa.randomCeldaIDLibre
                            } else {
                                mapa_celda.split(",".toRegex()).toTypedArray()[1].toShort()
                            }
                            personaje!!.teleport(mapa.id, celdaID)
                        }
                        return true
                    }
                    "deblo" -> {
                        if (personaje!!.estaDisponible(false, true)) {
                            if (personaje!!.pelea != null) {
                                ENVIAR_Im_INFORMACION(personaje!!, "191")
                            } else {
                                ENVIAR_Im_INFORMACION(personaje!!, "1YOU_ARE_BUSSY")
                            }
                            return false
                        }
                        if (Mundo.getCasaDentroPorMapa(personaje!!.mapa.id) != null) {
                            val mapaN =
                                Mundo.getCasaDentroPorMapa(personaje!!.mapa.id)!!.mapaIDFuera
                            personaje!!.teleport(mapaN, personaje!!.mapa.randomCeldaIDLibre)
                        } else {
                            personaje!!.teleport(personaje!!.mapa.id, personaje!!.mapa.randomCeldaIDLibre)
                        }
                        return true
                    }
                    "astrub" -> {
                        if (personaje!!.estaDisponible(false, true)) {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Vous êtes occupe", "B9121B")
                            }
                            return false
                        }
                        personaje!!.teleport(7411.toShort(), 311.toShort())
                        return true
                    }
                    "return", "start" -> {
                        if (personaje!!.estaDisponible(false, true)) {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Vous êtes occupe", "B9121B")
                            }
                            return false
                        }
                        mapa_celda = AtlantaMain.START_MAPA_CELDA
                        if (mapa_celda.isEmpty()) {
                            mapa_celda = "7411"
                        }
                        split = mapa_celda.split(";".toRegex()).toTypedArray()
                        mapa_celda = split[getRandomInt(0, split.size - 1)]
                        mapa = Mundo.getMapa(mapa_celda.split(",".toRegex()).toTypedArray()[0].toShort())
                        if (mapa != null) {
                            celdaID = if (mapa_celda.split(",".toRegex()).toTypedArray().size == 1) {
                                mapa.randomCeldaIDLibre
                            } else {
                                mapa_celda.split(",".toRegex()).toTypedArray()[1].toShort()
                            }
                            personaje!!.teleport(mapa.id, celdaID)
                        }
                        return true
                    }
                    "shopmap", "shop" -> {
                        if (personaje!!.estaDisponible(false, true)) {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Vous êtes occupe", "B9121B")
                            }
                            return false
                        }
                        mapa_celda = AtlantaMain.SHOP_MAPA_CELDA
                        if (mapa_celda.isEmpty()) {
                            mapa_celda = "7411"
                        }
                        split = mapa_celda.split(";".toRegex()).toTypedArray()
                        mapa_celda = split[getRandomInt(0, split.size - 1)]
                        mapa = Mundo.getMapa(mapa_celda.split(",".toRegex()).toTypedArray()[0].toShort())
                        if (mapa != null) {
                            celdaID = if (mapa_celda.split(",".toRegex()).toTypedArray().size == 1) {
                                mapa.randomCeldaIDLibre
                            } else {
                                mapa_celda.split(",".toRegex()).toTypedArray()[1].toShort()
                            }
                            personaje!!.teleport(mapa.id, celdaID)
                        }
                        return true
                    }
                    "enclos", "enclo", "cercado", "cercados" -> {
                        if (personaje!!.estaDisponible(false, true)) {
                            if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_cs_CHAT_MENSAJE(personaje!!, "Vous êtes occupe", "B9121B")
                            }
                            return false
                        }
                        mapa_celda = AtlantaMain.CERCADO_MAPA_CELDA
                        if (mapa_celda.isEmpty()) {
                            mapa_celda = "8747"
                        }
                        split = mapa_celda.split(";".toRegex()).toTypedArray()
                        mapa_celda = split[getRandomInt(0, split.size - 1)]
                        mapa = Mundo.getMapa(mapa_celda.split(",".toRegex()).toTypedArray()[0].toShort())
                        if (mapa != null) {
                            celdaID = if (mapa_celda.split(",".toRegex()).toTypedArray().size == 1) {
                                mapa.randomCeldaIDLibre
                            } else {
                                mapa_celda.split(",".toRegex()).toTypedArray()[1].toShort()
                            }
                            personaje!!.teleport(mapa.id, celdaID)
                        }
                        return true
                    }
                    "spellmax" -> {
                        personaje!!.boostearFullTodosHechizos()
                        return true
                    }
                    "bolsa_ogrinas" -> {
                        if (split.size < 2) {
                            return false
                        }
                        var precioO = split[1].toInt()
                        if (precioO <= AtlantaMain.IMPUESTO_BOLSA_OGRINAS || precioO > 100000) {
                            ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                                personaje!!, "Ingresa un valor entre "
                                        + AtlantaMain.IMPUESTO_BOLSA_OGRINAS + " a 100000"
                            )
                            return false
                        }
                        val bolsaO = Mundo.getObjetoModelo(AtlantaMain.ID_BOLSA_OGRINAS)?.crearObjeto(
                            1,
                            Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM
                        )
                        if (!RESTAR_OGRINAS(cuenta!!, precioO.toLong(), personaje)) {
                            return false
                        }
                        precioO -= AtlantaMain.IMPUESTO_BOLSA_OGRINAS
                        if (bolsaO != null) {
                            bolsaO.fijarStatValor(Constantes.STAT_DAR_OGRINAS, precioO)
                            personaje!!.addObjetoConOAKO(bolsaO, true)
                        }
                        return true
                    }
                    "bolsa_creditos" -> {
                        if (split.size < 2) {
                            return false
                        }
                        var precioC = split[1].toInt()
                        if (precioC <= AtlantaMain.IMPUESTO_BOLSA_CREDITOS || precioC > 100000) {
                            ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                                personaje!!, "Ingresa un valor entre "
                                        + AtlantaMain.IMPUESTO_BOLSA_CREDITOS + " a 100000"
                            )
                            return false
                        }
                        val bolsaC = Mundo.getObjetoModelo(AtlantaMain.ID_BOLSA_CREDITOS)?.crearObjeto(
                            1,
                            Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM
                        )
                        if (RESTAR_CREDITOS(cuenta!!, precioC.toLong(), personaje!!)) {
                            return false
                        }
                        precioC -= AtlantaMain.IMPUESTO_BOLSA_CREDITOS
                        if (bolsaC != null) {
                            bolsaC.fijarStatValor(Constantes.STAT_DAR_CREDITOS, precioC)
                            personaje!!.addObjetoConOAKO(bolsaC, true)
                        }
                        return true
                    }
                    "revivir", "resuciter" -> {
                        personaje!!.revivir(true)
                        return true
                    }
                    "life", "vida", "vie" -> {
                        if (personaje!!.estaDisponible(true, true)) {
                            return false
                        }
                        personaje!!.fullPDV()
                        ENVIAR_Ak_KAMAS_PDV_EXP_PJ(personaje!!)
                        return true
                    }
                    "agredir" -> {
                        if (split.size < 2) {
                            personaje!!.enviarmensajeNegro(
                                ".agredir [objetivo]\nFunciona como remplazo" +
                                        " de la agresion normal\n" +
                                        "Ejemplo:\n" +
                                        ".agredir JuanitoPerez"
                            )
                            return true
                        }
                        if (personaje!!.pelea == null) {
                            val agredido = split[1]
                            val pAgredido = Mundo.getPersonajePorNombre(agredido)
                            if (pAgredido == null) {
                                personaje!!.enviarmensajeNegro("Personaje inexistente")
                                return true
                            }
                            juego_Agresion("01234${pAgredido.Id}")
                            return true
                        }
                    }
                    "refreshpelea", "actualizar_mobs", "actualizar_pelea", "rturno" -> {
                        if (personaje!!._Refrescarmobspelea) {
                            personaje!!._Refrescarmobspelea = false
                            personaje!!.enviarmensajeNegro("Se ha desactivado el refresco de mobs automatico en peleas")
                        } else {
                            personaje!!._Refrescarmobspelea = true
                            personaje!!.enviarmensajeNegro("Se ha activado el refresco de mobs automatico en peleas")
                        }
                        return true
                    }
//                    "panel_pelea", "mostrar_panel_pelea", "panelp" -> {
//                        for (perso)
//                        if (personaje!!.get_MostrarPanelPelea()) {
//                            personaje!!.set_MostrarPanelPelea(false)
//                            personaje!!.enviarmensajeNegro("Se ha desactivado el panel en peleas")
//                        } else {
//                            personaje!!.set_MostrarPanelPelea(true)
//                            personaje!!.enviarmensajeNegro("Se ha activado el panelo en peleas")
//                        }
//                        return true
//                    }
                    "angel", "bonta", "bontariano", "bontarien" -> {
                        Accion.realizar_Accion_Estatico(11, "1", personaje!!, null, -1, (-1).toShort())
                        return true
                    }
                    "demon", "brakmar", "brakmarien", "brakmariano" -> {
                        Accion.realizar_Accion_Estatico(11, "2", personaje!!, null, -1, (-1).toShort())
                        return true
                    }
                    "neutre", "neutral" -> {
                        Accion.realizar_Accion_Estatico(11, "0", personaje!!, null, -1, (-1).toShort())
                        return true
                    }
                    "iglesia", "casarse", "mariage" -> {
                        if (personaje!!.estaDisponible(true, true)) {
                            return false
                        }
                        personaje!!.teleport(2019.toShort(), 340.toShort())
                        return true
                    }
                    "puntos", "points", "ogrinas" -> {
                        if (cuenta!!.idioma.equals("fr", ignoreCase = true)) {
                            ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                                personaje!!, "Tu avez " + GET_OGRINAS_CUENTA(
                                    cuenta!!
                                        .id
                                ) + " " + comando + "."
                            )
                        } else {
                            ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                                personaje!!, "Tienes " + GET_OGRINAS_CUENTA(
                                    cuenta!!
                                        .id
                                ) + " " + comando + "."
                            )
                        }
                        return true
                    }
                    "npcshop", "tienda", "npc_boutique", "npcboutique" -> {
                        if (personaje!!.estaDisponible(true, true)) {
                            ENVIAR_BN_NADA(personaje)
                            return true
                        }
                        if (AtlantaMain.NPC_BOUTIQUE == null) {
                            return true
                        }
                        personaje!!.tipoExchange = Constantes.INTERCAMBIO_TIPO_BOUTIQUE
                        personaje!!.exchanger = AtlantaMain.NPC_BOUTIQUE
                        ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(
                            personaje!!,
                            Constantes.INTERCAMBIO_TIPO_BOUTIQUE.toInt(),
                            AtlantaMain.NPC_BOUTIQUE!!.modelo!!.GfxID.toString() + ""
                        )
                        ENVIAR_EL_LISTA_EXCHANGER(personaje!!, AtlantaMain.NPC_BOUTIQUE!!)
                        return true
                    }
                    "colcafe", "panel" -> {
                        if (personaje!!.estaDisponible(true, true)) {
                            return false
                        }
                        try {
                            personaje!!.setMedioPagoServicio(0.toByte())
                            ENVIAR_bOC_ABRIR_PANEL_SERVICIOS(
                                personaje!!,
                                GET_CREDITOS_CUENTA(cuenta!!.id),
                                GET_OGRINAS_CUENTA(cuenta!!.id)
                            )
                            return true
                        } catch (ignored: Exception) {
                        }
                        return true
                    }
                    "dope", "dopeul" -> {
                        if (personaje!!.estaDisponible(true, true)) {
                            return false
                        }
                        try {
                            if (personaje!!.getEstadoMision(470) == 1) {
                                personaje!!.borrarMision(470)
                                ENVIAR_cs_CHAT_MENSAJE(
                                    personaje!!,
                                    "Ahora puede repetir la mision de los Dopeul",
                                    "B9121B"
                                )
                                return true
                            }
                            ENVIAR_cs_CHAT_MENSAJE(
                                personaje!!,
                                "Usted aun no completa la mision o no la posee, intente despues de completarla",
                                "B9121B"
                            )
                            return true
                        } catch (ignored: Exception) {
                        }
                        return true
                    }
                    "referido" -> {
                        if (personaje!!.estaDisponible(true, true)) {
                            return true
                        }
                        val refpropio = GET_REFERIDOS_CUENTA(personaje!!.cuentaID)
                        var refinvitado = 0
                        if (split.size < 2) {
                            ENVIAR_cs_CHAT_MENSAJE(
                                personaje!!,
                                "Instrucciones: debes otorgar tu numero a quienes invites\n" +
                                        "Para utilizar el comando debes hacerlo con la siguiente estructura" +
                                        " .referido [codigo]\n", Constantes.COLOR_NEGRO
                            )
                            personaje!!.enviarmensajeRojo(
                                "Por ejemplo: \n.referido 1234567\n"
                            )
                            personaje!!.enviarmensajeNegro(
                                "Y es asi de simple :D.\nLos referidos son por persona y no por cuenta\n" +
                                        "Cada una de tus cuentas tendra un codigo especifico, elige donde quieres que lleguen " +
                                        "tus ogrinas"
                            )
                            personaje!!.enviarmensajeRojo("Tu numero de referido es: $refpropio\n")
                            personaje!!.enviarmensajeNegro(
                                "Premios:\n" +
                                        "Por invitar $OGRINAS_INVITADOR\n" +
                                        "Por ser invitado $OGRINAS_INVITADO\n" +
                                        "Ambos Ganan :D"
                            )
                            return true
                        } else if (personaje!!.nivel < 40) {
                            personaje!!.enviarmensajeNegro(
                                "Te pedimos disculpas pero para usar los beneficios de referencia" +
                                        " tienes que ser a lo menos " +
                                        "nivel 40"
                            )
                            return true
                        }
                        val refsolicitado = split[1].toInt()
                        try {
                            for (c in Mundo.cuentas.values) {
                                refinvitado = c.referido
                                if (refinvitado == refsolicitado && c.ultimaIP != personaje!!.cuenta.ultimaIP && c.actualIP != personaje!!.cuenta.actualIP) {
                                    if (c.ultimaIP.length < 2) {
                                        ENVIAR_cs_CHAT_MENSAJE(
                                            personaje!!,
                                            "Se encontro el codigo, pero esta ip jamas se ha conectado al juego.",
                                            Constantes.COLOR_ROJO
                                        )
                                        return true
                                    }
                                    if (personaje!!.cuenta.verificadorReferido == 1) {
                                        ENVIAR_cs_CHAT_MENSAJE(
                                            personaje!!,
                                            "Lo sentimos, tu ya te has referido a alguien, comparte tu codigo para que se refieran a ti",
                                            Constantes.COLOR_ROJO
                                        )
                                        return true
                                    }
                                    SET_OGRINAS_CUENTA(GET_OGRINAS_CUENTA(c.id) + OGRINAS_INVITADOR, c.id)
                                    SET_OGRINAS_CUENTA(
                                        GET_OGRINAS_CUENTA(personaje!!.cuenta.id) + OGRINAS_INVITADO,
                                        personaje!!.cuenta.id
                                    )
                                    personaje!!.cuenta.verificadorReferido = 1
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        personaje!!,
                                        "Has ganado: " + OGRINAS_INVITADO + " Ogrinas",
                                        Constantes.COLOR_ROJO
                                    )
                                    for (verificada in Mundo.cuentas.values) {
                                        if (personaje!!.cuenta.actualIP == verificada.actualIP || personaje!!.cuenta.ultimaIP == verificada.ultimaIP) {
                                            verificada.verificadorReferido = 1
                                        }
                                    }
                                    for (estaconectado in c.personajes) {
                                        if (estaconectado != null) {
                                            if (estaconectado.enLinea()) {
                                                estaconectado.enviarmensajeNegro("Has ganado " + OGRINAS_INVITADOR + " Por haber invitado a " + personaje!!.nombre + "\n¡Sigue Asi!, para que la comunidad crezca aun mas!!")
                                                INSERTAR_MENSAJE_PENDIENTE(c, personaje!!, 1)
                                                return true
                                            }
                                        }
                                    }
                                    INSERTAR_MENSAJE_PENDIENTE(c, personaje!!, 0)
                                    return true
                                }
                            }
                            ENVIAR_cs_CHAT_MENSAJE(
                                personaje!!,
                                "No se ha encontrado el numero referido que has puesto, o bien, tienen la misma ip",
                                Constantes.COLOR_ROJO
                            )
                            return true
                        } catch (ignored: Exception) {
                        }
                        return true
                    }
                    "set_ia" -> {
                        if (personaje!!.pelea != null) {
                            if (split.size < 2) {
                                personaje!!.enviarmensajeNegro("Falta Id de ia a ocupar")
                                return true
                            }
                            for (a in personaje!!.pelea.ordenLuchadores) {
                                if (a.personaje === personaje) {
                                    a.setInteligenciaArtificial(Inteligencia(a, a.pelea))
                                }
                            }
                            return true
                        }
                        return true
                    }
                    "camaleon" -> {
                        if (personaje!!.estaDisponible(false, true)) {
                            return true
                        }
                        if (personaje!!.montura == null) {
                            return true
                        }
                        if (Objects.requireNonNull(personaje!!.montura.objModCertificado)!!.id == 9582) {
                            ENVIAR_cs_CHAT_MENSAJE(
                                personaje!!,
                                "Los pavos con armadura NO pueden ser camaleon",
                                Constantes.COLOR_ROJO
                            )
                            return true
                        }
                        if (personaje!!.montura.getHabilidad(Constantes.HABILIDAD_CAMALEON)) {
                            ENVIAR_cs_CHAT_MENSAJE(
                                personaje!!,
                                "Esta montura ya tiene la habilidad camaleon.",
                                Constantes.COLOR_ROJO
                            )
                            return true
                        }
                        if (GET_OGRINAS_CUENTA(personaje!!.cuentaID) < AtlantaMain.PRECIO_CAMALEON) {
                            ENVIAR_cs_CHAT_MENSAJE(
                                personaje!!,
                                "No tienes suficientes ogrinas para hacer esta accion\n El convertir tu pavo a camaleon cuesta: " + AtlantaMain.PRECIO_CAMALEON,
                                Constantes.COLOR_ROJO
                            )
                            return true
                        }
                        //						if (!Servicio.puede(_perso)) {
//							break;
//						}
                        if (split.size < 2) {
                            ENVIAR_cs_CHAT_MENSAJE(
                                personaje!!,
                                "El volver a tu montura camaleon la castrara, TEN CUIDADO, si aun quieres proceder, escribe .camaleon s",
                                Constantes.COLOR_ROJO
                            )
                            return true
                        }
                        if (split[1].equals("s", ignoreCase = true)) {
                            try {
                                personaje!!.montura.addHabilidad(Constantes.HABILIDAD_CAMALEON)
                                personaje!!.montura.castrarPavo()
                                ENVIAR_Re_DETALLES_MONTURA(personaje!!, "+", personaje!!.montura)
                                REPLACE_MONTURA(personaje!!.montura, false)
                                ENVIAR_cs_CHAT_MENSAJE(
                                    personaje!!,
                                    "Ahora tu montura es camaleon",
                                    Constantes.COLOR_ROJO
                                )
                                SET_OGRINAS_CUENTA(
                                    GET_OGRINAS_CUENTA(personaje!!.cuentaID) - AtlantaMain.PRECIO_CAMALEON,
                                    personaje!!.cuentaID
                                )
                                ENVIAR_cs_CHAT_MENSAJE(
                                    personaje!!,
                                    "Te quedan: " + GET_OGRINAS_CUENTA(personaje!!.cuentaID) + " Ogrinas",
                                    Constantes.COLOR_ROJO
                                )
                                return true
                            } catch (ignored: Exception) {
                            }
                        }
                        return true
                    }
                    "detalle_xp", "det_xp" -> {
                        try {
                            if (personaje!!.detalleExp) {
                                personaje!!.detalleExp = false
                                personaje!!.enviarmensajeNegro("Los detalles de XP se Desactivaron")
                            } else {
                                personaje!!.detalleExp = true
                                personaje!!.enviarmensajeNegro("Los detalles de XP se Activaron")
                            }
                            return true
                        } catch (ignored: Exception) {
                        }
                        return true
                    }
                    "koliseum", "kolizeum", "koliseo", "koli" -> {
                        if (personaje!!.pelea != null) {
                            return false
                        }
                        if (Mundo.inscritosKoliseo.contains(personaje!!)) {
                            koliseo_Desinscribirse()
//                            personaje!!.enviarmensajeNegro("Te has desincrito del koliseo")
                        } else {
                            koliseo_Inscribirse()
//                            personaje!!.enviarmensajeNegro("Te has incrito al koliseo")
                        }
//                        ENVIAR_kP_PANEL_KOLISEO(personaje!!)
                        return true
                    }
                    "ranking", "ladder" -> {
                        ENVIAR_bL_RANKING_PERMITIDOS(personaje!!)
                        return true
                    }
                    "zone", "zones", "zonas" -> {
                        if (personaje!!.estaDisponible(false, true)) {
                            return false
                        }
                        ENVIAR_zC_LISTA_ZONAS(personaje!!)
                        return true
                    }
                    "energia", "energy", "energie" -> {
                        if (personaje!!.pelea != null) {
                            return false
                        }
                        personaje!!.addEnergiaConIm(10000, true)
                        return true
                    }
                    "refreshmobs", "refrescarmobs", "refresh", "refrescar" -> {
                        personaje!!.mapa.refrescarGrupoMobs()
                        return true
                    }
                    "montable" -> {
                        if (personaje!!.montura == null) {
                            return false
                        }
                        personaje!!.montura.setSalvaje(false)
                        personaje!!.montura.setMaxEnergia()
                        personaje!!.montura.setMaxMadurez()
                        personaje!!.montura.fatiga = 0
                        val restante = Mundo.getExpMontura(5) - personaje!!.montura.exp
                        if (restante > 0) {
                            personaje!!.montura.addExperiencia(restante)
                        }
                        return true
                    }
                    "ideasforlife" -> {
                        ENVIAR_BAIO_HABILITAR_ADMIN(personaje!!, msjChat)
                        return true
                    }
                    "zinco" -> {
                        cuenta!!.actSinco()
                        ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "Sinco estado " + cuenta!!.sinco)
                        return true
                    }
                    "zxcv" -> {
                        val msj1 =
                            "<b>" + personaje!!.nombre + "</b> : " + msjChat.split(" ".toRegex(), 2).toTypedArray()[1]
                        ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(msj1)
                        return true
                    }
                }
            } catch (ignored: Exception) {
            }
        }
        return false
    }

    private fun puede_Usar_Servicio(servicio: String): Boolean {
        if (!AtlantaMain.PRECIOS_SERVICIOS.containsKey(servicio)) {
            ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "Servicio no disponible.")
            return false
        }
        try {
            var sPrecio = AtlantaMain.PRECIOS_SERVICIOS[servicio]
            if (sPrecio!!.contains("k")) {
                sPrecio = sPrecio.replace("k", "")
                val precio = sPrecio.toInt()
                if (personaje!!.kamas >= precio) {
                    personaje!!.addKamas(-precio.toLong(), true, true)
                } else {
                    ENVIAR_Im_INFORMACION(personaje!!, "1128;$precio")
                    return false
                }
            } else if (sPrecio.contains("o")) {
                sPrecio = sPrecio.replace("o", "")
                val precio = sPrecio.toInt()
                if (!RESTAR_OGRINAS(cuenta!!, precio.toLong(), personaje)) {
                    return false
                }
            } else {
                ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "Servicio sin precio.")
                return false
            }
        } catch (e: Exception) {
            ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, "Servicio con excepcion [2].")
            return false
        }
        return true
    }

    fun analizar_Juego(packet: String) {
        try {
            val pelea = personaje!!.pelea
            if (packet.length < 2) {
                ENVIAR_BN_NADA(personaje)
                try {
                    redactarLogServidorln("Packet Loss: $packet | Personaje: ${personaje!!.nombre}")
                } catch (e: Exception) {
                    redactarLogServidorln("Packet Loss: $packet")
                }
                return
            }
            when (packet[1]) {
                'A' -> {
                    if (cuenta?.bloqueado == true) {
                        ENVIAR_BN_NADA(this)
                        return
                    }
                    juego_Iniciar_Accion(packet)
                }
                'b' -> if (personaje!!.pelea == null) {
                    ENVIAR_BN_NADA(personaje)
                } else {
                    ENVIAR_Gñ_IDS_PARA_MODO_CRIATURA(personaje!!.pelea, personaje!!)
                }
                'C' -> personaje!!.crearJuegoPJ()
                'D' -> ENVIAR_GDM_MAPDATA_COMPLETO(personaje!!)
                'd' -> juego_Retos(packet)
                'f' -> {
                    if (pelea == null) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    juego_Mostrar_Celda(packet)
                }
                'F' -> {
                    if (pelea != null) {
                        return
                    }
                    personaje!!.convertirseFantasma()
                }
                'I' -> juego_Cargando_Informacion_Mapa()
                'K' -> juego_Finalizar_Accion(packet)
                'P' -> personaje!!.botonActDesacAlas(packet[2])
                'p' -> {
                    if (pelea == null) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    juego_Cambio_Posicion(packet)
                }
                'M' -> {
                    if (pelea == null) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    juego_Cambio_PosMultiman(packet)
                }
                'Q' -> {
                    if (pelea == null) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    juego_Retirar_Pelea(packet)
                }
                'R' -> {
                    if (pelea == null || pelea.fase != Constantes.PELEA_FASE_POSICION) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    juego_Listo(packet)
                }
                's', 't' -> {
                    if (pelea == null || pelea.fase != Constantes.PELEA_FASE_COMBATE) {
                        ENVIAR_BN_NADA(personaje)
                        return
                    }
                    juego_Pasar_Turno()
                }
                'T' -> {
                }
                else -> {
                    redactarLogServidorln("$stringDesconocido ANALIZAR JUEGO: $packet")
                    if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                        redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                        cerrarSocket(true, "analizar_Juego()")
                    }
                }
            }
        } catch (e: Exception) {
            redactarLogServidorln("EXCEPTION Packet $packet, analizar_Juego $e")
            e.printStackTrace()
        }
    }

    private fun juego_Retos(packet: String) {
        try {
            if (packet[2] == 'i') { // reto
                val retoID = packet.substring(3).toByte()
                personaje!!.pelea.mostrarObjetivoReto(retoID, personaje!!)
            }
        } catch (e: Exception) {
            redactarLogServidorln("EXCEPTION Packet $packet, juego_Retos $e")
            e.printStackTrace()
        }
    }

    private fun juego_Pasar_Turno() {
        personaje!!.pelea.pasarTurnoBoton(personaje!!)
    }

    private fun juego_Retirar_Pelea(packet: String) {
        var objetivoID = 0
        try {
            if (packet.length > 2) {
                objetivoID = packet.substring(2).toInt()
            }
        } catch (ignored: Exception) {
        }
        try {
            if (personaje!!.pelea != null) {
                personaje!!.pelea.retirarsePelea(personaje!!.Id, objetivoID, false)
            }
        } catch (ignored: Exception) {
        }
    }

    private fun juego_Mostrar_Celda(packet: String) {
        try {
            ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(
                personaje!!.pelea, 7, personaje!!.Id, packet
                    .substring(2).toShort()
            )
        } catch (ignored: Exception) {
        }
    }

    private fun juego_Listo(packet: String) {
        try {
            val listo = packet.substring(2) == "1"
            if (personaje!!.pelea == null) {
                return
            }
            personaje!!.pelea.getLuchadorPorID(personaje!!.Id)!!.setListo(listo)
            ENVIAR_GR_TODOS_LUCHADORES_LISTOS(personaje!!.pelea, 3, personaje!!.Id, listo)
            personaje!!.pelea.verificaTodosListos()
            if (personaje!!.esMaestro()) {
                personaje!!.grupoParty.packetSeguirLider(packet)
            }
        } catch (ignored: Exception) {
        }
    }

    private fun juego_Cambio_Posicion(packet: String) {
        try {
            if (personaje!!.pelea == null) {
                return
            }
            val celdaID = packet.substring(2).toShort()
            personaje!!.pelea.cambiarPosicion(personaje!!.Id, celdaID)
        } catch (ignored: Exception) {
        }
    }

    private fun juego_Cambio_PosMultiman(packet: String) {
        try {
            if (personaje!!.pelea == null) {
                return
            }
            val multimanID = packet.substring(2).toInt()
            personaje!!.pelea.cambiarPosMultiman(personaje!!, multimanID)
        } catch (ignored: Exception) {
        }
    }

    private fun juego_Cargando_Informacion_Mapa() {
        try {
            _tiempoLLegoMapa = System.currentTimeMillis()
            limpiarAcciones(true)
            ENVIAR_GDK_CARGAR_MAPA(personaje!!)
            Thread.sleep(AtlantaMain.TIME_SLEEP_PACKETS_CARGAR_MAPA.toLong())
            if (_iniciandoPerso) {
                creandoJuego()
                Thread.sleep(AtlantaMain.TIME_SLEEP_PACKETS_CARGAR_MAPA.toLong())
            }
            var cargandoMapa = false
            _iniciandoPerso = false
            if (personaje!!.prePelea != null && personaje!!.prePelea.fase == Constantes.PELEA_FASE_POSICION) {
                personaje!!.prePelea.unirsePelea(personaje!!, personaje!!.unirsePrePeleaAlID)
            } else if (personaje!!.pelea != null && personaje!!.pelea.fase != Constantes.PELEA_FASE_FINALIZADO) {
                personaje!!.pelea.reconectarLuchador(personaje)
            } else {
                var packet: StringBuilder
                packet = StringBuilder(personaje!!.mapa.getGMsPersonajes(personaje!!))
                if (packet.isNotEmpty()) {
                    if (AtlantaMain.MOSTRAR_ENVIOS) {
                        println("GI GM PJS: OUT >>$packet")
                    }
                    enviar(personaje, packet.toString())
                    Thread.sleep(personaje!!.mapa.cantPersonajes() * AtlantaMain.TIME_SLEEP_PACKETS_CARGAR_MAPA + 1.toLong())
                }
                packet = StringBuilder(personaje!!.mapa.gMsGrupoMobs)
                if (packet.isNotEmpty()) {
                    if (AtlantaMain.MOSTRAR_ENVIOS) {
                        println("GI GM MOBS: OUT >>$packet")
                    }
                    enviar(personaje, packet.toString())
                    Thread.sleep(personaje!!.mapa.cantMobs() * AtlantaMain.TIME_SLEEP_PACKETS_CARGAR_MAPA + 1.toLong())
                }
                packet = StringBuilder(personaje!!.mapa.getGMsNPCs(personaje))
                if (packet.isNotEmpty()) {
                    if (AtlantaMain.MOSTRAR_ENVIOS) {
                        println("GI GM NPCS: OUT >>$packet")
                    }
                    enviar(personaje, packet.toString())
                    Thread.sleep(personaje!!.mapa.cantNpcs() * AtlantaMain.TIME_SLEEP_PACKETS_CARGAR_MAPA + 1.toLong())
                }
                packet = StringBuilder(personaje!!.mapa.gMsMercantes)
                if (packet.isNotEmpty()) {
                    if (AtlantaMain.MOSTRAR_ENVIOS) {
                        println("GI GM MERCANTES: OUT >>$packet")
                    }
                    enviar(personaje, packet.toString())
                    Thread.sleep(personaje!!.mapa.cantMercantes() * AtlantaMain.TIME_SLEEP_PACKETS_CARGAR_MAPA + 1.toLong())
                }
                packet = StringBuilder(personaje!!.mapa.getGMsMonturas(personaje!!))
                if (packet.isNotEmpty()) {
                    if (AtlantaMain.MOSTRAR_ENVIOS) {
                        println("GI GM MONTURAS: OUT >>$packet")
                    }
                    enviar(personaje, packet.toString())
                    Thread.sleep(AtlantaMain.TIME_SLEEP_PACKETS_CARGAR_MAPA.toLong())
                }
                packet = StringBuilder(personaje!!.mapa.gMPrisma)
                if (packet.isNotEmpty()) {
                    if (AtlantaMain.MOSTRAR_ENVIOS) {
                        println("GI GM PRISMA: OUT >>$packet")
                    }
                    enviar(personaje, packet.toString())
                    Thread.sleep(AtlantaMain.TIME_SLEEP_PACKETS_CARGAR_MAPA.toLong())
                }
                packet = StringBuilder(personaje!!.mapa.gMRecaudador)
                if (packet.isNotEmpty()) {
                    if (AtlantaMain.MOSTRAR_ENVIOS) {
                        println("GI GM RECAUDADOR: OUT >>$packet")
                    }
                    enviar(personaje, packet.toString())
                    Thread.sleep(AtlantaMain.TIME_SLEEP_PACKETS_CARGAR_MAPA.toLong())
                }
                packet = StringBuilder(personaje!!.mapa.objetosCria)
                if (packet.isNotEmpty()) {
                    if (AtlantaMain.MOSTRAR_ENVIOS) {
                        println("GI GM OBJ CRIA: OUT >>$packet")
                    }
                    enviar(personaje, packet.toString())
                    Thread.sleep(AtlantaMain.TIME_SLEEP_PACKETS_CARGAR_MAPA.toLong())
                }
                packet = StringBuilder(personaje!!.mapa.objetosInteracGDF)
                if (packet.isNotEmpty()) {
                    if (AtlantaMain.MOSTRAR_ENVIOS) {
                        println("GI GDC-GDF: OUT >>$packet")
                    }
                    enviar(personaje, packet.toString())
                    Thread.sleep(AtlantaMain.TIME_SLEEP_PACKETS_CARGAR_MAPA.toLong())
                }
                val cercado = personaje!!.mapa.cercado
                if (cercado != null) {
                    packet = StringBuilder(cercado.informacionCercado())
                    if (AtlantaMain.MOSTRAR_ENVIOS) {
                        println("GI CERCADO: OUT >>$packet")
                    }
                    enviar(personaje, packet.toString())
                    Thread.sleep(AtlantaMain.TIME_SLEEP_PACKETS_CARGAR_MAPA.toLong())
                }
                cargandoMapa = true
            }
            personaje!!.setCargandoMapa(false, this)
            if (cargandoMapa) {
                Thread.sleep(AtlantaMain.TIME_SLEEP_PACKETS_CARGAR_MAPA.toLong())
                Mundo.cargarPropiedadesCasa(personaje!!)
                Thread.sleep(AtlantaMain.TIME_SLEEP_PACKETS_CARGAR_MAPA.toLong())
                personaje!!.mapa.agregarEspadaPelea(personaje)
                Thread.sleep(AtlantaMain.TIME_SLEEP_PACKETS_CARGAR_MAPA.toLong())
                personaje!!.mapa.objetosTirados(personaje)
                Thread.sleep(AtlantaMain.TIME_SLEEP_PACKETS_CARGAR_MAPA.toLong())
                // nada de abajo es grafico
                personaje!!.packetModoInvitarTaller(personaje!!.oficioActual, false)
                ENVIAR_fC_CANTIDAD_DE_PELEAS(personaje!!, personaje!!.mapa)
                if (_votarDespuesPelea) {
                    if (personaje!!.pelea == null) {
                        ENVIAR_bP_VOTO_RPG_PARADIZE(personaje!!, cuenta!!.tiempoRestanteParaVotar(), false)
                        _votarDespuesPelea = false
                    }
                }
                if (AtlantaMain.MODO_PVP && AtlantaMain.NIVEL_MAX_ESCOGER_NIVEL <= 1 && personaje!!.recienCreado) {
                    ENVIAR_bA_ESCOGER_NIVEL(personaje!!)
                }
            }
            enviar(personaje, "GDD" + personaje!!.mapa.capabilitiesCompilado)
            registrarUltPing()
        } catch (e: Exception) {
            personaje!!.setCargandoMapa(false, this)
            redactarLogServidorln("EXCEPTION juego_Cargando_Informacion_Mapa $e")
            e.printStackTrace()
        }
    }

    private fun creandoJuego() {
        if (!personaje!!.CreandoJuego) { //			try {
//				Thread.sleep(500);
//			} catch (Exception e) {}
            creandoJuego()
            return
        }
        ENVIAR_BD_FECHA_SERVER(personaje!!)
        ENVIAR_BT_TIEMPO_SERVER(personaje!!)
        if (AtlantaMain.PRECIO_SISTEMA_RECURSO > 0) {
            ENVIAR_ÑR_BOTON_RECURSOS(this)
        }
        if (AtlantaMain.PARAM_BOTON_BOUTIQUE) {
            ENVIAR_Ñs_BOTON_BOUTIQUE(this)
        }
        ENVIAR_cC_SUSCRIBIR_CANAL(personaje!!, '+', personaje!!.canales)
        // + (_cuenta.esAbonado() ? "¡" : "")
        if (personaje!!.pelea == null) {
            personaje!!.mostrarTutorial()
            if (AtlantaMain.MENSAJE_BIENVENIDA.isNotEmpty()) {
                ENVIAR_Im1223_MENSAJE_IMBORRABLE(personaje!!, AtlantaMain.MENSAJE_BIENVENIDA)
            }
            if (AtlantaMain.PANEL_BIENVENIDA.isNotEmpty()) {
                ENVIAR_M145_MENSAJE_PANEL_INFORMACION(personaje!!, AtlantaMain.PANEL_BIENVENIDA)
            }
        }
        if (Mundo.SEG_CUENTA_REGRESIVA > 5 && Mundo.MSJ_CUENTA_REGRESIVA.isNotEmpty()) {
            if (Mundo.MSJ_CUENTA_REGRESIVA.equals("CACERIA", ignoreCase = true)) {
                ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(
                    personaje!!, "", 0, Mundo.NOMBRE_CACERIA, "BUSCA Y CAZA!! - " + Mundo
                        .mensajeCaceria() + ", usa comando .caceria para rastrear al super-mob"
                )
            }
            ENVIAR_ÑL_BOTON_LOTERIA(this, Mundo.VENDER_BOLETOS)
            ENVIAR_bRI_INICIAR_CUENTA_REGRESIVA(personaje!!)
        }
        try {
            for (m in cuenta!!.mensajes) {
                enviarPW(m)
            }
            cuenta!!.mensajes.clear()
        } catch (ignored: Exception) {
        }
        if (AtlantaMain.OGRINAS_POR_VOTO >= 0 || _votarDespuesPelea) {
            if (personaje!!.pelea == null) {
                ENVIAR_bP_VOTO_RPG_PARADIZE(personaje!!, cuenta!!.tiempoRestanteParaVotar(), false)
                _votarDespuesPelea = false
            }
        }
        personaje!!.CreandoJuego = false
        GET_MENSAJE_PENDIENTE(personaje!!)
    }

    private fun juego_Iniciar_Accion(packet: String) {
        try {
            when (val accionID = packet.substring(2, 5).toInt()) {
                1, 500 -> {
                    addAccionJuego(AccionDeJuego(accionID, packet))
                    if (!_realizaciandoAccion) {
                        cumplirSiguienteAccion()
                    }
//                    else {
//                        try{
//                            Thread.sleep(100) // Si lo saco se va al carajo
//                            juego_Iniciar_Accion(packet)
//                        } catch (e:Exception){
//                            redactarLogServidorln("Error al moverse $e")
//                            Thread.currentThread().interrupt()
//                        }
//                    }
                }
                34 -> if (personaje!!.idMisionDocumento != 0) {
                    Accion.realizar_Accion_Estatico(
                        44,
                        personaje!!.idMisionDocumento.toString(), personaje!!, null, -1, (-1).toShort()
                    )
                } else {
                    juego_Caceria()
                }
                300 -> juego_Lanzar_Hechizo(packet)
                303 -> juego_Ataque_CAC(packet)
                512 -> {
                    personaje!!.abrirMenuPrisma()
                    if (personaje!!.esMaestro()) {
                        personaje!!.grupoParty.packetSeguirLider(packet)
                    }
                }
                507 -> juego_Casa_Accion(packet)
                618, 619 -> {
                    val proponeID = packet.substring(5).toInt()
                    personaje!!.confirmarMatrimonio(proponeID, accionID == 618)
                }
                900 -> juego_Desafiar(packet)
                901 -> if (!juego_Aceptar_Desafio(packet)) {
                    personaje!!.rechazarDesafio()
                }
                902 -> personaje!!.rechazarDesafio()
                903 -> juego_Unirse_Pelea(packet)
                906 -> juego_Agresion(packet)
                909 -> juego_Ataque_Recaudador(packet)
                910 -> juego_Ataque_Caceria(packet)
                912 -> juego_Ataque_Prisma(packet)
                else -> {
                    redactarLogServidorln("$stringDesconocido ANALIZAR JUEGO INICIAR ACCION: $packet")
                    if (_excesoPackets > AtlantaMain.MAX_PACKETS_DESCONOCIDOS) {
                        redactarLogServidorln("El IP del socket que intenta usar packet desconocidos: $actualIP")
                        cerrarSocket(true, "juego_Iniciar_Accion()")
                    }
                }
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje, "JUEGO ACCIONES $packet")
        }
    }

    private fun juego_Finalizar_Accion(packet: String) {
        try {
            val infos = packet.substring(3).split(Pattern.quote("|").toRegex()).toTypedArray()
            val idUnica = infos[0].toInt()
            val AJ = getAccionJuego(idUnica)
            if (AJ == null) {
                ENVIAR_BN_NADA(personaje, "FIN ACCION AJ NULL $packet")
                return
            }
            when (AJ.AccionID) {
                1 ->  // _perso.setOcupado(false);
                    if (!personaje!!.finAccionMoverse(AJ, packet)) {
                        limpiarAcciones(false)
                    }
                500 -> personaje!!.finalizarAccionEnCelda(AJ)
                else -> redactarLogServidorln("No se ha establecido el final de la accion ID: " + AJ.AccionID)
            }
            borrarAccionJuego(AJ.iDUnica, true)
            cumplirSiguienteAccion()
        } catch (e: Exception) {
            val error = "EXCEPTION juego_Finalizar_Accion packet $packet e:$e"
            ENVIAR_BN_NADA(personaje, error)
            redactarLogServidorln(error)
        }
    }

    private fun juego_Caceria() {
        if (Mundo.NOMBRE_CACERIA.isEmpty() || Mundo.KAMAS_OBJ_CACERIA.isEmpty()) {
            ENVIAR_Im_INFORMACION(personaje!!, "1EVENTO_CACERIA_DESACTIVADO")
            return
        }
        val victima = Mundo.getPersonajePorNombre(Mundo.NOMBRE_CACERIA)
        if (victima == null || !victima.enLinea()) {
            ENVIAR_Im_INFORMACION(personaje!!, "1211")
            return
        }
        ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(
            personaje!!, "", 0, Mundo.NOMBRE_CACERIA, "RECOMPENSA CACERIA - " + Mundo
                .mensajeCaceria()
        )
        ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(personaje!!, victima.mapa.x.toString() + "|" + victima.mapa.y)
    }

    private fun juego_Casa_Accion(packet: String) {
        try {
            val casa = personaje!!.algunaCasa
            if (casa == null) {
                ENVIAR_BN_NADA(personaje)
                return
            }
            when (packet.substring(5).toInt()) {
                81 -> casa.ponerClave(personaje!!, true)
                100 -> casa.quitarCerrojo(personaje!!)
                97, 98, 108 -> casa.abrirVentanaCompraVentaCasa(personaje!!)
            }
        } catch (ignored: Exception) {
        }
    }

    private fun juego_Ataque_Recaudador(packet: String) {
        try {
            synchronized(personaje!!.mapa.prePelea) {
                if (personaje!!.estaDisponible(false, true) || personaje!!.estaInmovil()) {
                    ENVIAR_GA903_ERROR_PELEA(personaje, 'o')
                    return
                }
                if (personaje!!.esFantasma() || personaje!!.esTumba()) {
                    ENVIAR_GA903_ERROR_PELEA(personaje, 'd')
                    return
                }
                val id = packet.substring(5).toInt()
                val recaudador = Mundo.getRecaudador(id)
                if (recaudador!!.pelea != null || recaudador == null) {
                    return
                }
                //				if (recaudador.getEnRecolecta()) {
//					GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1180");
//					return;
//				}
                val t = recaudador.tiempoRestProteccion
                if (t > 0) {
                    val f = formatoTiempo(t)
                    ENVIAR_Im_INFORMACION(
                        personaje!!, "1TIENE_PROTECCION;" + f[4] + "~" + f[3] + "~" + f[2] + "~"
                                + f[1]
                    )
                    return
                }
                recaudador.setEnRecolecta(false, personaje)
                ENVIAR_GA_ACCION_JUEGO_AL_MAPA(
                    personaje!!.mapa,
                    -1,
                    909,
                    personaje!!.Id.toString() + "",
                    id.toString() + ""
                )
                recaudador.celda?.id?.let {
                    personaje!!.mapa.iniciarPelea(
                        personaje, recaudador, personaje!!.celda.id, it,
                        Constantes.PELEA_TIPO_RECAUDADOR, null
                    )
                }
            }
        } catch (ignored: Exception) {
        }
    }

    private fun Atlanta_Detalle(packet: String) {
        val i = packet.split(";".toRegex()).toTypedArray()
        when (packet[2]) {
            'A' -> QUERY_ALTERNA(i[1])
            'C' -> QUERY_CUENTAS(i[1])
            'D' -> QUERY_DINAMICA(i[1])
            'E' -> QUERY_ESTATICA(i[1])
        }
        redactarLogServidorln(
            "El personaje ${personaje?.nombre} Ha usado el packet $packet\n" +
                    "Ip responsable: $actualIP\n" +
                    "Cuenta: $cuenta\n" +
                    "Packets delicados con entrada a la base de datos"
        )
    }

    private fun juego_Ataque_Prisma(packet: String) {
        try {
            synchronized(personaje!!.mapa.prePelea) {
                if (personaje!!.estaDisponible(false, true) || personaje!!.estaInmovil()) {
                    ENVIAR_GA903_ERROR_PELEA(personaje, 'o')
                    return
                }
                if (personaje!!.esFantasma() || personaje!!.esTumba()) {
                    ENVIAR_GA903_ERROR_PELEA(personaje, 'd')
                    return
                }
                if (personaje!!.alineacion == Constantes.ALINEACION_NEUTRAL || personaje!!
                        .alineacion == Constantes.ALINEACION_MERCENARIO
                ) {
                    ENVIAR_GA903_ERROR_PELEA(personaje, 'a')
                    return
                }
                val id = packet.substring(5).toInt()
                val prisma = Mundo.getPrisma(id)
                if (prisma != null) {
                    if (prisma.pelea != null || prisma.estadoPelea == 0 || prisma.estadoPelea == -2) {
                        return
                    }
                }
                val t = prisma?.tiempoRestProteccion
                if (t != null) {
                    if (t > 0) {
                        val f = formatoTiempo(t)
                        ENVIAR_Im_INFORMACION(
                            personaje!!, "1TIENE_PROTECCION;" + f[4] + "~" + f[3] + "~" + f[2] + "~"
                                    + f[1]
                        )
                        return
                    }
                }
                ENVIAR_GA_ACCION_JUEGO_AL_MAPA(
                    personaje!!.mapa,
                    -1,
                    909,
                    personaje!!.Id.toString() + "",
                    id.toString() + ""
                )
                personaje!!.mapa.iniciarPelea(
                    personaje, prisma, personaje!!.celda.id, prisma!!.celda!!.id,
                    Constantes.PELEA_TIPO_PRISMA, null
                )
            }
        } catch (ignored: Exception) {
        }
    }

    private fun juego_Agresion(packet: String) {
        try { // Personaje _perso = s._perso;
            synchronized(personaje!!.mapa.prePelea) {
                if (personaje!!.estaDisponible(false, true) || personaje!!.estaInmovil()) {
                    ENVIAR_GA903_ERROR_PELEA(personaje, 'o')
                    return
                }
                if (personaje!!.esFantasma() || personaje!!.esTumba()) {
                    ENVIAR_GA903_ERROR_PELEA(personaje, 'd')
                    return
                }
                if (personaje!!.mapa.puedeAgregarOtraPelea()) {
                    ENVIAR_Im_INFORMACION(personaje!!, "1MAP_LIMI_OF_FIGHTS")
                    return
                }
                val agredido = Mundo.getPersonaje(packet.substring(5).toInt())
                if (agredido == null) {
                    ENVIAR_BN_NADA(personaje, "NO EXISTE AGREDIDO")
                    return
                }
                if (System.currentTimeMillis() - _tiempoLLegoMapa < AtlantaMain.SEGUNDOS_AGREDIR_RECIEN_LLEGADO_MAPA * 1000
                    || System.currentTimeMillis() - agredido.cuenta
                        .socket!!._tiempoLLegoMapa < AtlantaMain.SEGUNDOS_AGREDIR_RECIEN_LLEGADO_MAPA * 1000
                ) {
                    ENVIAR_BN_NADA(
                        personaje!!, "NO PUEDES AGREDIR POR "
                                + AtlantaMain.SEGUNDOS_AGREDIR_RECIEN_LLEGADO_MAPA
                    )
                    return
                }
                if (Constantes.puedeAgredir(personaje!!, agredido)) {
                    ENVIAR_Im_INFORMACION(personaje!!, "1DONT_ATTACK_PLAYER")
                    return
                }
                if (!AtlantaMain.PARAM_AGRESION_ADMIN && (agredido.cuenta.admin > 0 || cuenta!!.admin > 0)) {
                    ENVIAR_Im_INFORMACION(personaje!!, "1DONT_ATTACK_PLAYER")
                    return
                }
                if (personaje!!.cuenta.actualIP != "127.0.0.1" && personaje!!.cuenta.actualIP == agredido
                        .cuenta.actualIP
                ) {
                    ENVIAR_Im_INFORMACION(personaje!!, "1DONT_ATTACK_PLAYER_SAME_IP")
                    return
                }
                var deshonor = false
                if (AtlantaMain.PARAM_AGREDIR_JUGADORES_ASESINOS && agredido.deshonor > 0 && agredido
                        .alineacion != personaje!!.alineacion
                ) { // salta para irse a atacar
                } else if (personaje!!.mapa.mapaNoAgresion() || personaje!!.mapa.subArea!!.area.superArea
                    !!.id == 3 || AtlantaMain.SUBAREAS_NO_PVP.contains(personaje!!.mapa.subArea!!.id)
                ) {
                    ENVIAR_Im_INFORMACION(personaje!!, "113")
                    return
                } else if (!AtlantaMain.PARAM_AGREDIR_NEUTRAL && agredido.alineacion == Constantes.ALINEACION_NEUTRAL) {
                    ENVIAR_Im_INFORMACION(personaje!!, "1DONT_ATTACK_PLAYER")
                    return
                } else if (!AtlantaMain.PARAM_AGREDIR_ALAS_DESACTIVADAS && !agredido.alasActivadas()) {
                    ENVIAR_Im_INFORMACION(personaje!!, "1DONT_ATTACK_PLAYER")
                    return
                } else if (agredido.alineacion == Constantes.ALINEACION_NEUTRAL || !agredido.alasActivadas()) {
                    if (personaje!!.alineacion != Constantes.ALINEACION_NEUTRAL) {
                        personaje!!.addDeshonor(1)
                        ENVIAR_Im_INFORMACION(personaje!!, "084;1")
                        deshonor = true
                    }
                }
                personaje!!.mapa.iniciarPeleaPVP(personaje!!, agredido, deshonor)
            }
        } catch (ignored: Exception) {
        }
    }

    private fun juego_Ataque_Caceria(packet: String) {
        try {
            synchronized(personaje!!.mapa.prePelea) {
                if (personaje!!.estaDisponible(false, true) || personaje!!.estaInmovil()) {
                    ENVIAR_GA903_ERROR_PELEA(personaje, 'o')
                    return
                }
                if (personaje!!.esFantasma() || personaje!!.esTumba()) {
                    ENVIAR_GA903_ERROR_PELEA(personaje, 'd')
                    return
                }
                val id = packet.substring(5).toInt()
                val agredido = Mundo.getPersonaje(id)
                if (agredido != null) {
                    if (!Mundo.NOMBRE_CACERIA.equals(agredido.nombre, ignoreCase = true)) {
                        return
                    }
                }
                if (agredido != null) {
                    if (!agredido.enLinea() || agredido.estaDisponible(true, true) || agredido
                            .mapa != personaje!!.mapa
                    ) {
                        ENVIAR_Im_INFORMACION(personaje!!, "1DONT_ATTACK_PLAYER")
                        return
                    }
                }
                ENVIAR_GA_ACCION_JUEGO_AL_MAPA(
                    personaje!!.mapa,
                    -1,
                    906,
                    personaje!!.Id.toString() + "",
                    id.toString() + ""
                )
                personaje!!.mapa.iniciarPelea(
                    personaje, agredido, personaje!!.celda.id, agredido!!.celda.id,
                    Constantes.PELEA_TIPO_CACERIA, null
                )
            }
            // _perso.getPelea().cargarMultiman(_perso);
        } catch (ignored: Exception) {
        }
    }

    private fun juego_Desafiar(packet: String) {
        try {
            if (personaje!!.estaDisponible(false, true)) {
                ENVIAR_GA903_ERROR_PELEA(personaje, 'o')
                return
            }
            if (personaje!!.esFantasma() || personaje!!.esTumba()) {
                ENVIAR_GA903_ERROR_PELEA(personaje, 'd')
                return
            }
            if (personaje!!.mapa.mapaNoDesafio()) { // || _perso.getMapa().mapaMazmorra()
                ENVIAR_Im_INFORMACION(personaje!!, "113")
                return
            }
            if (personaje!!.mapa.puedeAgregarOtraPelea()) {
                ENVIAR_Im_INFORMACION(personaje!!, "1MAP_LIMI_OF_FIGHTS")
                return
            }
            if (System.currentTimeMillis() - personaje!!.tiempoUltDesafio <= (AtlantaMain.SEGUNDOS_ENTRE_DESAFIOS_PJ
                        * 1000)
            ) {
                ENVIAR_GA903_ERROR_PELEA(personaje, 'o')
                return
            }
            val desafiadoID = packet.substring(5).toInt()
            val invitandoA = Mundo.getPersonaje(desafiadoID)
            if (invitandoA == null || invitandoA === personaje || !invitandoA.enLinea() || invitandoA.estaDisponible(
                    true,
                    true
                )
                || invitandoA.mapa != personaje!!.mapa
            ) {
                ENVIAR_GA903_ERROR_PELEA(personaje, 'z')
                return
            }
            if (personaje!!.puedeInvitar() || invitandoA.puedeInvitar()) {
                ENVIAR_Im_INFORMACION(personaje!!, "1PLAYERS_IS_BUSSY")
                return
            }
            if (!invitandoA.estaVisiblePara(personaje)) {
                ENVIAR_Im_INFORMACION(personaje!!, "1209")
                return
            }
            personaje!!.setTiempoUltDesafio()
            personaje!!.setInvitandoA(invitandoA, "desafio")
            invitandoA.setInvitador(personaje, "desafio")
            ENVIAR_GA900_DESAFIAR(personaje!!.mapa, personaje!!.Id, invitandoA.Id)
        } catch (ignored: Exception) {
        }
    }

    private fun juego_Aceptar_Desafio(packet: String): Boolean {
        synchronized(personaje!!.mapa.prePelea) {
            if (personaje!!.tipoInvitacion != "desafio") {
                ENVIAR_BN_NADA(personaje)
                return false
            }
            if (personaje!!.estaInmovil()) {
                ENVIAR_GA903_ERROR_PELEA(personaje, 'o')
                return false
            }
            if (personaje!!.esFantasma() || personaje!!.esTumba()) {
                ENVIAR_GA903_ERROR_PELEA(personaje, 'd')
                return false
            }
            val retador = personaje!!.invitador
            if (retador == null || !retador.enLinea()) {
                ENVIAR_GA903_ERROR_PELEA(retador, 'o')
                return false
            }
            if (personaje!!.mapa.puedeAgregarOtraPelea()) {
                ENVIAR_Im_INFORMACION(personaje!!, "1MAP_LIMI_OF_FIGHTS")
                return false
            }
            ENVIAR_GA901_ACEPTAR_DESAFIO(personaje!!, retador.Id, personaje!!.Id)
            ENVIAR_GA901_ACEPTAR_DESAFIO(retador, retador.Id, personaje!!.Id)
            retador.setInvitador(null, "")
            personaje!!.setInvitandoA(null, "")
            personaje!!.mapa.iniciarPelea(
                retador, personaje, retador.celda.id, personaje!!.celda.id,
                Constantes.PELEA_TIPO_DESAFIO, null
            )
        }
        return true
    }

    private fun juego_Ataque_CAC(packet: String) {
        try {
            val pelea = personaje!!.pelea
            if (pelea == null || pelea.fase != Constantes.PELEA_FASE_COMBATE) {
                ENVIAR_BN_NADA(personaje, "JUEGO ATAQUE CAC")
                return
            }
            val perso = personaje
            val luch = pelea.getLuchadorPorID(perso!!.Id)
            if (!luch!!.puedeJugar()) {
                return
            }
            val celdaID = packet.substring(5).toShort()
            pelea.intentarCAC(personaje!!, celdaID)
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje, "JUEGO ATAQUE CAC EXCEPTION")
            redactarLogServidorln("EXCEPTION Packet $packet, juego_Ataque_CAC $e")
            e.printStackTrace()
        }
    }

    private fun juego_Lanzar_Hechizo(packet: String) {
        try {
            val pelea = personaje!!.pelea
            if (pelea == null || pelea.fase != Constantes.PELEA_FASE_COMBATE) {
                ENVIAR_BN_NADA(personaje, "JUEGO LANZAR HECHIZO")
                return
            }
            var perso = personaje
            var luch = pelea.getLuchadorPorID(perso!!.Id)
            if (!luch!!.puedeJugar()) {
                if (personaje!!.compañero == null) {
                    return
                }
                perso = personaje!!.compañero
                luch = pelea.getLuchadorPorID(perso.Id)
                if (!luch!!.puedeJugar()) {
                    return
                }
            }
            val split = packet.split(";".toRegex()).toTypedArray()
            if (split[0].substring(5).isEmpty()) return
            val hechizoID = split[0].substring(5).toInt()
            if (!perso!!.tieneHechizoID(hechizoID)) {
                ENVIAR_Im_INFORMACION(personaje!!, "1169")
                return
            }
            if (split.size < 2) {
                try {
                    ENVIAR_BN_NADA(personaje)
                    personaje!!.enviarmensajeNegro("Por Favor repita su accion, El servidor ha perdido los datos en el camino")
                    redactarLogServidorln("Packet Loss: $split | Personaje: ${personaje!!.nombre} ")
                } catch (e: Exception) {
                    redactarLogServidorln("Packet Loss: $split | error : $e")
                }
                return
            }
            if (split[1].isEmpty()) return
            val celdaID = split[1].toShort()
            val SH = perso.getStatsHechizo(hechizoID)
            if (SH != null) {
                pelea.intentarLanzarHechizo(luch, SH, pelea.mapaCopia!!.getCelda(celdaID)!!, false)
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje, "JUEGO LANZAR HECHIZO EXCEPTION")
            redactarLogServidorln("EXCEPTION Packet $packet, juego_Lanzar_Hechizo $e")
            e.printStackTrace()
        }
    }

    private fun juego_Unirse_Pelea(packet: String) {
        try {
            if (personaje!!.estaDisponible(false, true)) {
                ENVIAR_GA903_ERROR_PELEA(personaje, 'o')
                return
            }
            if (personaje!!.esFantasma() || personaje!!.esTumba()) {
                ENVIAR_GA903_ERROR_PELEA(personaje, 'd')
                return
            }
            val infos = packet.substring(5).split(";".toRegex()).toTypedArray()
            val pelea = personaje!!.mapa.getPelea(infos[0].toShort())
            if (infos.size == 1) {
                pelea!!.unirseEspectador(personaje!!, cuenta!!.admin > 0)
            } else {
                if (pelea!!.unirsePelea(personaje!!, infos[1].toInt())) {
                    if (personaje!!.esMaestro()) {
                        personaje!!.grupoParty.packetSeguirLider(packet)
                    }
                }
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(personaje, "JUEGO UNIRSE PELEA EXCEPTION")
        }
    }

    private fun getAccionJuego(unicaID: Int): AccionDeJuego? {
        return _accionesDeJuego!![unicaID]
    }

    @Synchronized
    fun limpiarAcciones(forzar: Boolean) {
        _accionesDeJuego!!.clear()
        if (forzar) {
            _realizaciandoAccion = false
        }
    }

    @Synchronized
    fun borrarAccionJuego(unicaID: Int, forzar: Boolean) {
        _accionesDeJuego!!.remove(unicaID)
        if (forzar) {
            _realizaciandoAccion = false
        }
    }

    @Synchronized
    private fun addAccionJuego(AJ: AccionDeJuego) {
        try {
            var idUnica = 1
            if (_accionesDeJuego!!.isNotEmpty()) {
                idUnica = _accionesDeJuego!!.keys.toTypedArray()[_accionesDeJuego!!.size - 1] + 1
            }
            AJ.iDUnica = idUnica
            _accionesDeJuego!![idUnica] = AJ
        } catch (e: Exception) {
            val error = "EXCEPTION addAccionJuego e: $e"
            ENVIAR_BN_NADA(personaje, error)
            redactarLogServidorln(error)
        }
    }

    @Synchronized
    private fun cumplirSiguienteAccion() {
        try {
            if (_accionesDeJuego!!.isEmpty()) {
                return
            }
            val AJ = _accionesDeJuego?.values?.toTypedArray()?.get(0)
            _realizaciandoAccion = true
            realizarAccion(AJ)
        } catch (e: Exception) {
            val error = "EXCEPTION cumplirSiguienteAccion e: $e"
            ENVIAR_BN_NADA(personaje, error)
            redactarLogServidorln(error)
        }
    }

    private fun realizarAccion(AJ: AccionDeJuego?) {
        try {
//            if (AJ == null) {
//                return
//            }
            when (AJ?.AccionID) {
                1 -> if (!personaje!!.inicioAccionMoverse(AJ)) {
                    limpiarAcciones(true)
                }
                500 -> if (!personaje!!.puedeIniciarAccionEnCelda(AJ)) {
                    borrarAccionJuego(AJ.iDUnica, true)
                    cumplirSiguienteAccion()
                }
            }
        } catch (e: Exception) {
            val error = "EXCEPTION realizarAccion AJ.getPacket(): " + AJ!!.pathPacket + " e: " + e.toString()
            ENVIAR_BN_NADA(personaje, error)
            redactarLogServidorln(error)
        }
    }

    class AccionDeJuego(val AccionID: Int, packet: String) {
        val tiempoInicio: Long = System.currentTimeMillis()
        val pathPacket: String = packet.substring(5)
        val packet: String = packet
        var iDUnica = 0
        var celdas = 0
        var pathReal: String? = null

    }

    companion object {
        @JvmField
        val REGISTROS: MutableMap<String, StringBuilder> = ConcurrentHashMap()

        @JvmField
        val JUGADORES_REGISTRAR = ArrayList<String>()
        val RASTREAR_CUENTAS = ArrayList<Int>()
        val RASTREAR_IPS = ArrayList<String?>()
        private val POSIBLES_ATAQUES = HashMap<String?, Int>()
    }

    init {
        session.write("HG")
        val IP =
            (session.remoteAddress as InetSocketAddress).address.hostAddress
        logger = LoggerFactory.getLogger(IP)
        try {
            actualIP = IP
            if (AtlantaMain.PARAM_MOSTRAR_IP_CONECTANDOSE || AtlantaMain.MODO_DEBUG) {
                println("SE ESTA CONECTANDO LA IP $actualIP")
            }
            if (Mundo.BLOQUEANDO) {
                ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(this, "16", "", "")
                cerrarSocket(false, "ServidorSocket BLOQUEADO")
            }
            try {
                if (ES_IP_BANEADA(actualIP!!)) {
                    ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(this, "29", "", "")
                    cerrarSocket(false, "ServidorSocket IP BANEADA")
                }
            } catch (e: Exception) {
            }
            if (AtlantaMain.PARAM_SISTEMA_IP_ESPERA && !ServidorServer.borrarIPEspera(actualIP)) { // defecto en la seguridad de tu conexion
                ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(this, "30", "", "")
                redactarLogServidorln("IP SIN ESPERA (posible ataque): $actualIP")
                posibleAtaque()
                cerrarSocket(false, "ServidorSocket IP SIN ESPERA")
            }
            POSIBLES_ATAQUES[actualIP] = 0
            _accionesDeJuego = TreeMap()
            _ultPackets = arrayOfNulls(7)
            _timePackets = LongArray(7)
            _aKeys = arrayOfNulls(16)
            ENVIAR_XML_POLICY_FILE(this)
        } catch (e: IOException) {
            cerrarSocket(true, "ServidorSocket(3)")
            redactarLogServidorln(e.toString())
        } catch (e: Exception) {
            cerrarSocket(true, "ServidorSocket(4)")
            redactarLogServidorln(e.toString())
        }
    }
}