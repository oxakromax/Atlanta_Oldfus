package variables.casa

import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Constantes
import estaticos.GestorSalida.ENVIAR_BN_NADA
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.GestorSalida.ENVIAR_KCK_VENTANA_CLAVE
import estaticos.GestorSalida.ENVIAR_KKE_ERROR_CLAVE
import estaticos.GestorSalida.ENVIAR_KV_CERRAR_VENTANA_CLAVE
import estaticos.GestorSalida.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO
import estaticos.GestorSalida.ENVIAR_hCK_VENTANA_COMPRA_VENTA_CASA
import estaticos.GestorSalida.ENVIAR_hG_DERECHOS_GREMIO_CASA
import estaticos.GestorSalida.ENVIAR_hL_INFO_CASA
import estaticos.GestorSalida.ENVIAR_hP_PROPIEDADES_CASA
import estaticos.GestorSalida.ENVIAR_hSK_FIJAR_PRECIO_CASA
import estaticos.GestorSalida.ENVIAR_hV_CERRAR_VENTANA_COMPRA_CASA
import estaticos.Mundo.cantCasasGremio
import estaticos.Mundo.casas
import estaticos.Mundo.getCasaDePj
import estaticos.Mundo.getCofresPorCasa
import estaticos.Mundo.getMapa
import estaticos.Mundo.getPersonaje
import estaticos.database.GestorSQL.REPLACE_COFRE
import variables.gremio.Gremio
import variables.personaje.Personaje

class Casa(
    val id: Int, val mapaIDFuera: Short, val celdaIDFuera: Short, var mapaIDDentro: Short,
    var celdaIDDentro: Short, precio: Long, mapasContenidosString: String
) {
    private val _tieneDerecho: MutableMap<Int, Boolean> = HashMap()
    val mapasContenidos = ArrayList<Short>()
    var actParametros = false
        private set
    var dueño: Personaje? = null
        private set
    private var _gremio: Gremio? = null
    var derechosGremio = 0
        private set
    var kamasVenta: Long = 1000000
        private set
    var clave = "-"
        private set

    fun actualizarCasa(
        dueñoInt: Int, precio: Long, bloqueado: Byte, clave: String,
        derechos: Int
    ) {
        this.dueño = getPersonaje(dueñoInt)
        kamasVenta = precio
        this.clave = clave
        actParametros = bloqueado.toInt() == 1
        if (actParametros && this.dueño != null) {
            _gremio = dueño!!.gremio
            if (_gremio == null) {
                actParametros = false
            }
        }
        derechosGremio = derechos
        analizarDerechos(derechosGremio)
    }

    fun esSuCasa(id: Int): Boolean {
        return dueño != null && dueño!!.Id == id
    }

    fun resetear() {
        dueño = null
        _gremio = null
        kamasVenta = 1000000
        clave = "-"
        actParametros = false
        actualizarDerechos(0)
    }

    val gremioID: Int
        get() = if (_gremio == null) 0 else _gremio!!.id

    fun nullearGremio() {
        _gremio = null
    }

    fun tieneDerecho(derecho: Int): Boolean {
        return _tieneDerecho[derecho]!!
    }

    fun iniciarDerechos() {
        _tieneDerecho.clear()
        _tieneDerecho[Constantes.C_VISIBLE_PARA_GREMIO] = false
        _tieneDerecho[Constantes.C_ESCUDO_VISIBLE_MIEMBROS] = false
        _tieneDerecho[Constantes.C_ESCUDO_VISIBLE_PARA_TODOS] = false
        _tieneDerecho[Constantes.C_ACCESOS_MIEMBROS_SIN_CODIGO] = false
        _tieneDerecho[Constantes.C_ACCESO_PROHIBIDO_NO_MIEMBROS] = false
        _tieneDerecho[Constantes.C_ACCESOS_COFRES_MIEMBROS_SIN_CODIGO] = false
        _tieneDerecho[Constantes.C_ACCESO_PROHIBIDO_COFRES_NO_MIEMBROS] = false
        _tieneDerecho[Constantes.C_TELEPORT_GREMIO] = false
        _tieneDerecho[Constantes.C_DESCANSO_GREMIO] = false
    }

    fun analizarDerechos(derechos: Int) {
        iniciarDerechos()
        for (i in 0..7) {
            val exp = Math.pow(2.0, i.toDouble()).toInt()
            if (exp and derechos == exp) {
                _tieneDerecho[exp] = true
            }
        }
    }

    fun intentarAcceder(perso: Personaje, clave: String) {
        if (perso.estaDisponible(false, true)) {
            ENVIAR_BN_NADA(perso)
            return
        }
        val esDelGremio = perso.gremio != null && perso.gremio.id == gremioID
        if (actParametros && !esDelGremio && tieneDerecho(Constantes.C_ACCESO_PROHIBIDO_NO_MIEMBROS)) { //
            ENVIAR_Im_INFORMACION(perso, "1101")
        } else if (clave.isEmpty()) {
            if (esSuCasa(perso.Id) || this.clave == "-" || actParametros && esDelGremio && tieneDerecho(
                    Constantes.C_ACCESOS_MIEMBROS_SIN_CODIGO
                )
            ) {
                perso.teleport(mapaIDDentro, celdaIDDentro)
            } else {
                ponerClave(perso, false)
            }
        } else {
            if (clave == this.clave) {
                cerrarVentanaClave(perso)
                perso.teleport(mapaIDDentro, celdaIDDentro)
            } else {
                ENVIAR_KKE_ERROR_CLAVE(perso)
            }
        }
    }

    fun expulsar(perso: Personaje, packet: String) {
        if (!esSuCasa(perso.Id)) {
            ENVIAR_BN_NADA(perso)
        } else {
            try {
                val objetivo = getPersonaje(packet.toInt())
                if (objetivo == null || !objetivo.enLinea() || objetivo.pelea != null || objetivo.mapa
                        .id != perso.mapa.id
                ) {
                    return
                }
                objetivo.teleport(mapaIDFuera, celdaIDFuera)
                ENVIAR_Im_INFORMACION(objetivo, "018;" + perso.nombre)
            } catch (ignored: Exception) {
            }
        }
    }

    fun quitarCerrojo(perso: Personaje) {
        if (esSuCasa(perso.Id)) {
            clave = "-"
            // _actParametros = false;
            ENVIAR_hL_INFO_CASA(perso, informacionCasa(perso.Id))
        } else {
            ENVIAR_BN_NADA(perso)
        }
    }

    fun ponerClave(perso: Personaje, modificarClave: Boolean) {
        perso.consultarCasa = this
        ENVIAR_KCK_VENTANA_CLAVE(perso, modificarClave, 8.toByte()) // para bloquear clave
    }

    fun cerrarVentanaClave(perso: Personaje) {
        perso.consultarCasa = null
        ENVIAR_KV_CERRAR_VENTANA_CLAVE(perso)
    }

    fun modificarClave(perso: Personaje, packet: String) {
        if (packet.isEmpty()) {
            return
        }
        if (esSuCasa(perso.Id)) {
            clave = packet
            // _actParametros = false;
            if (packet.length > 8) {
                clave = packet.substring(0, 8)
            }
            ENVIAR_hL_INFO_CASA(perso, informacionCasa(perso.Id))
        }
        cerrarVentanaClave(perso)
    }

    fun comprarCasa(perso: Personaje) {
        if (esSuCasa(perso.Id) || getCasaDePj(perso.Id) != null) {
            ENVIAR_Im_INFORMACION(perso, "132;1")
            return
        }
        if (kamasVenta <= 0 || perso.kamas < kamasVenta) {
            ENVIAR_Im_INFORMACION(perso, "1CANT_BUY_HOUSE;" + kamasVenta)
            return
        }
        perso.addKamas(-kamasVenta, true, true)
        var kamasCofre: Long = 0
        for (cofre in getCofresPorCasa(this)) {
            try {
                cofre.moverCofreABanco(dueño!!.cuenta)
            } catch (ignored: Exception) {
            }
            kamasCofre += cofre.kamas
            cofre.setKamasCero()
            cofre.clave = "-"
            cofre.dueñoID = perso.Id
            REPLACE_COFRE(cofre, false)
        }
        try {
            dueño!!.addKamasBanco(kamasVenta + kamasCofre)
            val tempPerso = dueño!!.cuenta.tempPersonaje
            if (tempPerso != null) {
                ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(
                    tempPerso, 5, "$kamasVenta;" + perso
                        .nombre, ""
                )
            } else {
                dueño!!.cuenta.addMensaje("M15|" + kamasVenta + ";" + perso.nombre + "|", true)
            }
        } catch (ignored: Exception) {
        }
        dueño = perso
        kamasVenta = 0
        clave = "-"
        actParametros = false
        _gremio = null
        actualizarDerechos(0)
        cerrarVentanaCompra(perso)
        ENVIAR_hL_INFO_CASA(perso, informacionCasa(perso.Id))
        for (p in getMapa(mapaIDFuera)!!.arrayPersonajes!!) {
            ENVIAR_hP_PROPIEDADES_CASA(p, propiedadesPuertaCasa(p))
        }
    }

    fun modificarPrecioVenta(perso: Personaje, packet: String) {
        if (esSuCasa(perso.Id)) {
            val precio = packet.toInt()
            if (precio < 0) {
                ENVIAR_BN_NADA(perso)
                return
            }
            kamasVenta = precio.toLong()
            ENVIAR_hV_CERRAR_VENTANA_COMPRA_CASA(perso)
            ENVIAR_hSK_FIJAR_PRECIO_CASA(perso, id.toString() + "|" + kamasVenta)
            for (p in getMapa(mapaIDFuera)!!.arrayPersonajes!!) {
                ENVIAR_hP_PROPIEDADES_CASA(p, propiedadesPuertaCasa(p))
            }
            ENVIAR_hL_INFO_CASA(perso, informacionCasa(perso.Id))
        }
    }

    fun abrirVentanaCompraVentaCasa(perso: Personaje) {
        perso.consultarCasa = this
        ENVIAR_hCK_VENTANA_COMPRA_VENTA_CASA(perso, id.toString() + "|" + kamasVenta)
    }

    fun cerrarVentanaCompra(perso: Personaje) {
        ENVIAR_hV_CERRAR_VENTANA_COMPRA_CASA(perso)
        perso.consultarCasa = null
    }

    fun analizarCasaGremio(perso: Personaje, packet: String) {
        if (!esSuCasa(perso.Id)) {
            ENVIAR_BN_NADA(perso)
            return
        }
        try {
            when (packet) {
                "+" -> {
                    val gremio = dueño!!.gremio ?: return
                    if (cantCasasGremio(gremio.id) >= Math.ceil(gremio.nivel / 10f.toDouble()).toInt().toByte()) {
                        ENVIAR_Im_INFORMACION(perso, "1151")
                        return
                    } else if (gremio.cantidadMiembros < 10) {
                        ENVIAR_Im_INFORMACION(perso, "1NOT_ENOUGHT_MEMBERS_IN_GUILD")
                        return
                    }
                    _gremio = gremio
                    actParametros = true
                }
                "-", "0" -> {
                    _gremio = null
                    actParametros = false
                }
                "" -> {
                }
                else -> try {
                    actualizarDerechos(packet.toInt())
                } catch (ignored: Exception) {
                }
            }
            ENVIAR_hG_DERECHOS_GREMIO_CASA(
                perso,
                id.toString() + if (actParametros && _gremio != null) ";" + _gremio!!.nombre + ";" + _gremio!!.emblema + ";" + derechosGremio else ""
            )
        } catch (e: Exception) {
            ENVIAR_BN_NADA(perso, "EXCEPTION ANALIZAR CASA GREMIO")
            redactarLogServidorln("EXCEPTION Packet $packet, analizarCasaGremio $e")
            e.printStackTrace()
        }
    }

    fun actualizarDerechos(derechos: Int) {
        derechosGremio = derechos
        analizarDerechos(derechosGremio)
    }

    // poner el mensaje de condicin cuando el mob tiene condicino
// probar los canales en reconeccion
// poner finalize al map q se crea para la pelea
//
    fun propiedadesPuertaCasa(perso: Personaje): String {
        val packet = StringBuilder("$id|")
        try {
            packet.append(dueño!!.nombre)
        } catch (ignored: Exception) {
        }
        packet.append(";").append(if (kamasVenta > 0) 1 else 0)
        val esDelGremio = perso.gremio != null && perso.gremio.id == gremioID
        if (_gremio != null) {
            if (_gremio!!.cantidadMiembros < 10) {
                _gremio = null
            } else if (tieneDerecho(Constantes.C_ESCUDO_VISIBLE_PARA_TODOS) || tieneDerecho(
                    Constantes.C_ESCUDO_VISIBLE_MIEMBROS
                ) && esDelGremio
            ) {
                packet.append(";").append(_gremio!!.nombre).append(";").append(_gremio!!.emblema)
            }
        }
        return packet.toString()
    }

    fun informacionCasa(id: Int): String {
        return ((if (esSuCasa(id)) "+" else "-") + "|" + this.id + ";" + (if (clave == "-") 0 else 1) + ";" + (if (kamasVenta > 0) 1 else 0)
                + ";" + if (derechosGremio > 0) 1 else 0)
    }

    companion object {
        fun stringCasaGremio(gremioID: Int): String {
            val packet = StringBuilder()
            for (casa in casas.values) {
                if (casa.gremioID == gremioID && casa.derechosGremio > 0) {
                    if (packet.length > 0) {
                        packet.append("|")
                    }
                    packet.append(casa.id).append(";")
                    try {
                        packet.append(casa.dueño!!.nombre).append(";")
                    } catch (e: Exception) {
                        packet.append("?;")
                    }
                    val mapa = getMapa(casa.mapaIDDentro)
                    packet.append(mapa!!.x.toInt()).append(",").append(mapa.y.toInt()).append(";")
                    packet.append("0;")
                    packet.append(casa.derechosGremio)
                }
            }
            return if (packet.length == 0) {
                ""
            } else "+$packet"
        }
    }

    init {
        kamasVenta = precio
        for (str in mapasContenidosString.split(";".toRegex()).toTypedArray()) {
            try {
                mapasContenidos.add(str.toShort())
            } catch (ignored: Exception) {
            }
        }
        mapasContenidos.trimToSize()
    }
}