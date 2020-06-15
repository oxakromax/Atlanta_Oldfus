package variables.zotros

import estaticos.AtlantaMain
import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Constantes
import estaticos.Encriptador
import estaticos.GestorSalida.ENVIAR_AAE_ERROR_CREAR_PJ
import estaticos.GestorSalida.ENVIAR_BN_NADA
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO
import estaticos.GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO
import estaticos.GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA_MAPA
import estaticos.GestorSalida.ENVIAR_Re_DETALLES_MONTURA
import estaticos.GestorSalida.ENVIAR_bA_ESCOGER_NIVEL
import estaticos.GestorSalida.ENVIAR_bB_PANEL_CREAR_ITEM
import estaticos.GestorSalida.ENVIAR_bSP_PANEL_ITEMS
import estaticos.GestorSalida.ENVIAR_bV_CERRAR_PANEL
import estaticos.GestorSalida.ENVIAR_bm_TRANSFORMAR_MONTURA
import estaticos.GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO
import estaticos.GestorSalida.enviar
import estaticos.Mundo.eliminarMontura
import estaticos.Mundo.emblemaGremioUsado
import estaticos.Mundo.getObjetoModelo
import estaticos.Mundo.nombreGremioUsado
import estaticos.database.GestorSQL.CAMBIAR_SEXO_CLASE
import estaticos.database.GestorSQL.GET_ABONO
import estaticos.database.GestorSQL.REPLACE_MONTURA
import estaticos.database.GestorSQL.RESTAR_CREDITOS
import estaticos.database.GestorSQL.RESTAR_OGRINAS
import estaticos.database.GestorSQL.SET_ABONO
import variables.objeto.ObjetoModelo.CAPACIDAD_STATS
import variables.personaje.Personaje
import java.util.regex.Pattern

class Servicio(
    val id: Int,
    private val _creditosSinAbono: Int,
    private val _ogrinasSinAbono: Int,
    private val _activado: Boolean,
    private val _creditosAbonado: Int,
    private val _ogrinasAbonado: Int
) {
    private val _biPagoSinAbono: Boolean
    private val _biPagoAbonado: Boolean

    fun estaActivo(): Boolean {
        return _activado
    }

    fun string(abonado: Boolean): String {
        if (!_activado) {
            return ""
        }
        return if (abonado) {
            "$id;$_creditosAbonado;$_ogrinasAbonado"
        } else {
            "$id;$_creditosSinAbono;$_ogrinasSinAbono"
        }
    }

    private fun puede(_perso: Personaje): Boolean {
        if (!_activado) {
            return true
        }
        if (_perso.cuenta.esAbonado()) {
            if (_biPagoAbonado) {
                when (_perso.medioPagoServicio) {
                    1 -> return RESTAR_CREDITOS(_perso.cuenta, _creditosAbonado.toLong(), _perso)
                    2 -> return !RESTAR_OGRINAS(_perso.cuenta, _ogrinasAbonado.toLong(), _perso)
                }
            }
            if (_creditosAbonado > 0) {
                return RESTAR_CREDITOS(_perso.cuenta, _creditosAbonado.toLong(), _perso)
            } else if (_ogrinasAbonado > 0) {
                return !RESTAR_OGRINAS(_perso.cuenta, _ogrinasAbonado.toLong(), _perso)
            }
        } else {
            if (_biPagoSinAbono) {
                when (_perso.medioPagoServicio) {
                    1 -> return RESTAR_CREDITOS(_perso.cuenta, _creditosSinAbono.toLong(), _perso)
                    2 -> return !RESTAR_OGRINAS(_perso.cuenta, _ogrinasSinAbono.toLong(), _perso)
                }
            }
            if (_creditosSinAbono > 0) {
                return RESTAR_CREDITOS(_perso.cuenta, _creditosSinAbono.toLong(), _perso)
            } else if (_ogrinasSinAbono > 0) {
                return !RESTAR_OGRINAS(_perso.cuenta, _ogrinasSinAbono.toLong(), _perso)
            }
        }
        return false
    }

    fun usarServicio(_perso: Personaje, packet: String) {
        if (!_activado) {
            return
        }
        try {
            when (id) {
                Constantes.SERVICIO_CAMBIO_NOMBRE -> {
                    if (_perso.estaDisponible(false, true)) {
                        return
                    }
                    if (!packet.isEmpty()) {
                        val params =
                            packet.substring(3).split(";".toRegex()).toTypedArray()
                        var nombre: String? = params[0]
                        var colorN = 0
                        try {
                            colorN = params[1].toInt()
                            if (colorN > 16777215) {
                                colorN = 0
                            }
                        } catch (e: Exception) {
                            return
                        }
                        if (nombre.equals(
                                _perso.nombre,
                                ignoreCase = true
                            )
                        ) { // si tiene el mismo nombre y diferente color
                            if (colorN == _perso.colorNombre) {
                                return
                            }
                        }
                        nombre = Personaje.nombreValido(nombre, false)
                        if (nombre == null) {
                            ENVIAR_AAE_ERROR_CREAR_PJ(_perso, "a")
                            return
                        }
                        if (nombre.isEmpty()) {
                            ENVIAR_AAE_ERROR_CREAR_PJ(_perso, "n")
                            return
                        }
                        if (puede(_perso)) {
                            return
                        }
                        _perso.colorNombre = colorN
                        _perso.cambiarNombre(nombre)
                    } else {
                        enviar(_perso, "bN" + _perso.colorNombre)
                    }
                }
                Constantes.SERVICIO_CAMBIO_COLOR -> {
                    if (_perso.estaDisponible(false, true)) {
                        return
                    }
                    if (!packet.isEmpty()) {
                        if (puede(_perso)) {
                            return
                        }
                        val colores =
                            packet.substring(3).split(";".toRegex()).toTypedArray()
                        _perso.setColores(colores[0].toInt(), colores[1].toInt(), colores[2].toInt())
                        _perso.refrescarEnMapa()
                        ENVIAR_bV_CERRAR_PANEL(_perso)
                    } else {
                        enviar(_perso, "bC")
                    }
                }
                Constantes.SERVICIO_CAMBIO_SEXO -> {
                    if (_perso.estaDisponible(false, true)) {
                        return
                    }
                    if (puede(_perso)) {
                        return
                    }
                    _perso.cambiarSexo()
                    _perso.deformar()
                    _perso.refrescarEnMapa()
                    CAMBIAR_SEXO_CLASE(_perso)
                    ENVIAR_bV_CERRAR_PANEL(_perso)
                }
                Constantes.SERVICIO_REVIVIR -> {
                    if (_perso.pelea != null) {
                        return
                    }
                    if (puede(_perso)) {
                        return
                    }
                    _perso.revivir(true)
                    ENVIAR_bV_CERRAR_PANEL(_perso)
                }
                Constantes.SERVICIO_TITULO_PERSONALIZADO -> if (!packet.isEmpty()) {
                    if (packet.substring(3).isEmpty()) {
                        _perso.tituloVIP = ""
                    } else {
                        val str = packet.substring(3).split(";".toRegex()).toTypedArray()
                        val titulo = str[0]
                        var colorT = 0
                        try {
                            colorT = str[1].toInt()
                            if (colorT > 16777215) {
                                colorT = 0
                            }
                        } catch (e: Exception) {
                            return
                        }
                        if (titulo.isEmpty() || titulo.length > 25) {
                            return
                        }
                        val plantilla = (Encriptador.NUMEROS + Encriptador.ABC_MIN + Encriptador.ABC_MAY
                                + Encriptador.ESPACIO + Encriptador.GUIONES)
                        for (letra in titulo.toCharArray()) {
                            if (!plantilla.contains(letra.toString() + "")) {
                                return
                            }
                        }
                        if (puede(_perso)) {
                            return
                        }
                        _perso.tituloVIP = "$titulo*$colorT"
                    }
                    _perso.refrescarEnMapa()
                    ENVIAR_bV_CERRAR_PANEL(_perso)
                } else {
                    enviar(_perso, "bÑ")
                }
                Constantes.SERVICIO_MIMOBIONTE -> if (!packet.isEmpty()) {
                    val split =
                        packet.substring(3).split(Pattern.quote("|").toRegex()).toTypedArray()
                    val huesped = _perso.getObjeto(split[0].toInt())
                    val mascara = _perso.getObjeto(split[1].toInt())
                    if (huesped == null || mascara == null) {
                        ENVIAR_Im_INFORMACION(_perso, "1OBJECT_DONT_EXIST")
                        return
                    }
                    if (huesped.objevivoID != 0 || mascara.objevivoID != 0) {
                        ENVIAR_Im_INFORMACION(_perso, "1MIMOBIONTE_ERROR_TYPES")
                        return
                    }
                    if (huesped.id == mascara.id) {
                        ENVIAR_Im_INFORMACION(_perso, "1MIMOBIONTE_ERROR_IDS")
                        return
                    }
                    val tipos = intArrayOf(
                        Constantes.OBJETO_TIPO_AMULETO, Constantes.OBJETO_TIPO_ARCO, Constantes.OBJETO_TIPO_VARITA,
                        Constantes.OBJETO_TIPO_BASTON, Constantes.OBJETO_TIPO_DAGAS, Constantes.OBJETO_TIPO_ESPADA,
                        Constantes.OBJETO_TIPO_MARTILLO, Constantes.OBJETO_TIPO_PALA, Constantes.OBJETO_TIPO_ANILLO,
                        Constantes.OBJETO_TIPO_CINTURON, Constantes.OBJETO_TIPO_BOTAS, Constantes.OBJETO_TIPO_SOMBRERO,
                        Constantes.OBJETO_TIPO_CAPA, Constantes.OBJETO_TIPO_MASCOTA, Constantes.OBJETO_TIPO_HACHA,
                        Constantes.OBJETO_TIPO_PICO, Constantes.OBJETO_TIPO_GUADAÑA, Constantes.OBJETO_TIPO_MOCHILA,
                        Constantes.OBJETO_TIPO_ESCUDO
                    )
                    var esTipo = false
                    for (t in tipos) {
                        if (t == huesped.objModelo?.tipo?.toInt()) {
                            esTipo = true
                            break
                        }
                    }
                    if (!esTipo || huesped.objModelo?.tipo != mascara.objModelo?.tipo) {
                        ENVIAR_Im_INFORMACION(_perso, "1MIMOBIONTE_ERROR_TYPES")
                        return
                    }
                    if (huesped.objModelo?.nivel ?: 0 < mascara.objModelo?.nivel ?: 0) {
                        ENVIAR_Im_INFORMACION(_perso, "1MIMOBIONTE_ERROR_LEVELS")
                        return
                    }
                    if (AtlantaMain.ID_MIMOBIONTE != -1) {
                        if (!_perso.tenerYEliminarObjPorModYCant(AtlantaMain.ID_MIMOBIONTE, 1)) {
                            ENVIAR_Im_INFORMACION(_perso, "14|43")
                            return
                        }
                        ENVIAR_Im_INFORMACION(_perso, "022;" + 1 + "~" + AtlantaMain.ID_MIMOBIONTE)
                    } else if (puede(_perso)) {
                        ENVIAR_BN_NADA(_perso, "MIMOBIONTE DESHABILITADO")
                        return
                    }
                    if (!_perso.restarCantObjOEliminar(mascara.id, 1, true)) {
                        ENVIAR_Im_INFORMACION(_perso, "14|43")
                        return
                    }
                    ENVIAR_Im_INFORMACION(_perso, "022;" + 1 + "~" + mascara.objModeloID)
                    val nuevaCantidad = huesped.cantidad - 1
                    if (nuevaCantidad >= 1) {
                        val nuevo = huesped.clonarObjeto(nuevaCantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                        _perso.addObjetoConOAKO(nuevo, true)
                        huesped.cantidad = 1
                        ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, huesped)
                    }
                    huesped.addStatTexto(
                        Constantes.STAT_APARIENCIA_OBJETO, "0#0#" + Integer.toHexString(
                            mascara
                                .objModeloID
                        )
                    )
                    ENVIAR_OCK_ACTUALIZA_OBJETO(_perso, huesped)
                } else {
                    enviar(_perso, "bM")
                }
                Constantes.SERVICIO_CREA_TU_ITEM -> ENVIAR_bB_PANEL_CREAR_ITEM(_perso)
                Constantes.SERVICIO_SISTEMA_ITEMS -> ENVIAR_bSP_PANEL_ITEMS(_perso)
                Constantes.SERVICIO_CAMBIO_EMBLEMA -> {
                    if (_perso.estaDisponible(false, true)) {
                        return
                    }
                    if (_perso.miembroGremio == null || _perso.miembroGremio.rango != 1) {
                        ENVIAR_Im_INFORMACION(_perso, "1YOU_DONT_HAVE_GUILD")
                        return
                    }
                    if (!packet.isEmpty()) {
                        val infos =
                            packet.substring(3).split(Pattern.quote("|").toRegex()).toTypedArray()
                        val escudoID = Integer.toString(infos[0].toInt(), 36)
                        val colorEscudo = Integer.toString(infos[1].toInt(), 36)
                        val emblemaID = Integer.toString(infos[2].toInt(), 36)
                        val colorEmblema = Integer.toString(infos[3].toInt(), 36)
                        val nombreGremio =
                            infos[4].substring(0, 1).toUpperCase() + infos[4].substring(1).toLowerCase()
                        if (nombreGremio.length < 2 || nombreGremio.length > 20 || !_perso.gremio.nombre
                                .equals(nombreGremio, ignoreCase = true) && nombreGremioUsado(nombreGremio)
                        ) {
                            ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "Ean")
                            return
                        }
                        var esValido = true
                        val abcMin = "abcdefghijklmnopqrstuvwxyz- '"
                        var cantSimbol: Byte = 0
                        var cantLetras: Byte = 0
                        var letra_A = ' '
                        var letra_B = ' '
                        for (letra in nombreGremio.toLowerCase().toCharArray()) {
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
                            ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "Ean")
                            return
                        }
                        val emblema = "$escudoID,$colorEscudo,$emblemaID,$colorEmblema"
                        if (emblemaGremioUsado(emblema)) {
                            ENVIAR_gC_CREAR_PANEL_GREMIO(_perso, "Eae")
                            return
                        }
                        if (puede(_perso)) {
                            return
                        }
                        val gremio = _perso.gremio
                        gremio.nombre = nombreGremio
                        gremio.emblema = emblema
                        if (_perso.pelea == null) {
                            ENVIAR_Oa_CAMBIAR_ROPA_MAPA(_perso.mapa, _perso)
                        }
                        _perso.refrescarEnMapa()
                        ENVIAR_bV_CERRAR_PANEL(_perso)
                    } else {
                        enviar(_perso, "bG")
                    }
                }
                Constantes.SERVICIO_ESCOGER_NIVEL -> {
                    if (_perso.estaDisponible(false, true)) {
                        return
                    }
                    if (!packet.isEmpty()) {
                        if (puede(_perso)) {
                            return
                        }
                        val split =
                            packet.substring(2).split(Pattern.quote("|").toRegex()).toTypedArray()
                        val nivel = split[0].toInt()
                        val alineacion = split[1].toByte()
                        _perso.cambiarNivelYAlineacion(nivel, alineacion)
                    } else {
                        ENVIAR_bA_ESCOGER_NIVEL(_perso)
                    }
                }
                Constantes.SERVICIO_TRANSFORMAR_MONTURA -> {
                    if (_perso.estaDisponible(false, true)) {
                        return
                    }
                    if (_perso.montura == null) {
                        ENVIAR_Im_INFORMACION(_perso, "1104")
                        return
                    }
                    if (!packet.isEmpty()) {
                        if (puede(_perso)) {
                            return
                        }
                        val statsMontura = _perso.montura.stats.convertirStatsAString()
                        val mascota = getObjetoModelo(packet.substring(3).toInt())!!.crearObjeto(
                            1,
                            Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO
                        )
                        mascota.convertirStringAStats_Base(statsMontura)
                        _perso.addObjetoConOAKO(mascota, true)
                        if (_perso.estaMontando()) {
                            _perso.subirBajarMontura(false)
                        }
                        eliminarMontura(_perso.montura)
                        _perso.montura = null
                    } else {
                        ENVIAR_bm_TRANSFORMAR_MONTURA(_perso)
                    }
                }
                Constantes.SERVICIO_ALINEACION_MERCENARIO -> {
                    if (_perso.estaDisponible(false, true)) {
                        return
                    }
                    if (_perso.alineacion == Constantes.ALINEACION_MERCENARIO) {
                        return
                    }
                    if (_perso.deshonor >= 2) {
                        ENVIAR_Im_INFORMACION(_perso, "183")
                        return
                    }
                    if (puede(_perso)) {
                        return
                    }
                    _perso.cambiarAlineacion(Constantes.ALINEACION_MERCENARIO, false)
                }
                Constantes.SERVICIO_MONTURA_CAMALEON -> {
                    if (_perso.estaDisponible(false, true)) {
                        return
                    }
                    if (_perso.montura == null) {
                        return
                    }
                    if (puede(_perso)) {
                        return
                    }
                    _perso.montura.addHabilidad(Constantes.HABILIDAD_CAMALEON)
                    ENVIAR_Re_DETALLES_MONTURA(_perso, "+", _perso.montura)
                    REPLACE_MONTURA(_perso.montura, false)
                }
                Constantes.SERVICIO_ABONO_DIA, Constantes.SERVICIO_ABONO_SEMANA, Constantes.SERVICIO_ABONO_MES, Constantes.SERVICIO_ABONO_TRES_MESES -> {
                    if (puede(_perso)) {
                        return
                    }
                    var dias = 1
                    when (id) {
                        Constantes.SERVICIO_ABONO_DIA -> {
                        }
                        Constantes.SERVICIO_ABONO_SEMANA -> dias = 7
                        Constantes.SERVICIO_ABONO_MES -> dias = 30
                        Constantes.SERVICIO_ABONO_TRES_MESES -> dias = 90
                        else -> return
                    }
                    var abonoD = Math.max(
                        GET_ABONO(_perso.cuenta.nombre),
                        System.currentTimeMillis()
                    )
                    abonoD += dias * 24 * 3600 * 1000L
                    abonoD = Math.max(abonoD, System.currentTimeMillis() - 1000)
                    SET_ABONO(abonoD, _perso.cuentaID)
                    ENVIAR_Im_INFORMACION(_perso, "1NUEVO_ABONO;$dias")
                }
                101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112 -> {
                    if (_perso.estaDisponible(false, true)) {
                        return
                    }
                    if (_perso.encarnacionN != null) {
                        ENVIAR_Im_INFORMACION(_perso, "1NO_PUEDES_CAMBIAR_CLASE")
                        return
                    }
                    if (puede(_perso)) {
                        return
                    }
                    val clase = (id - 100).toByte()
                    _perso.cambiarClase(clase)
                    ENVIAR_bV_CERRAR_PANEL(_perso)
                }
            }
        } catch (e: Exception) {
            ENVIAR_BN_NADA(_perso, "EXCEPTION USAR SERVICIO")
            redactarLogServidorln("EXCEPTION Packet $packet, usarServicios $e")
            e.printStackTrace()
        }
    }

    init {
        _biPagoSinAbono = _creditosSinAbono > 0 && _ogrinasSinAbono > 0
        _biPagoAbonado = _creditosAbonado > 0 && _ogrinasAbonado > 0
    }
}