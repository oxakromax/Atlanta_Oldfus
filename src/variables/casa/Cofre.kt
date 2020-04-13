package variables.casa

import estaticos.AtlantaMain
import estaticos.Constantes
import estaticos.Constantes.esPosicionEquipamiento
import estaticos.GestorSQL.GET_COFRE_POR_MAPA_CELDA
import estaticos.GestorSQL.INSERT_COFRE_MODELO
import estaticos.GestorSQL.REPLACE_COFRE
import estaticos.GestorSalida.ENVIAR_BN_NADA
import estaticos.GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS
import estaticos.GestorSalida.ENVIAR_EL_LISTA_EXCHANGER
import estaticos.GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.GestorSalida.ENVIAR_KCK_VENTANA_CLAVE
import estaticos.GestorSalida.ENVIAR_KKE_ERROR_CLAVE
import estaticos.GestorSalida.ENVIAR_KV_CERRAR_VENTANA_CLAVE
import estaticos.GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO
import estaticos.GestorSalida.ENVIAR_cs_CHAT_MENSAJE
import estaticos.Mundo.addCofre
import estaticos.Mundo.addObjeto
import estaticos.Mundo.eliminarObjeto
import estaticos.Mundo.getCasa
import estaticos.Mundo.getCasaDentroPorMapa
import estaticos.Mundo.getCofrePorUbicacion
import estaticos.Mundo.getMapa
import estaticos.Mundo.getObjeto
import sprites.Exchanger
import variables.objeto.Objeto
import variables.personaje.Cuenta
import variables.personaje.Personaje
import java.util.*
import java.util.regex.Pattern

class Cofre(val iD: Int, val casaID: Int, val mapaID: Short, val celdaID: Short, limite: Int) : Exchanger {
    private val _limite: Int
    private val _objetos: MutableMap<Int, Objeto?> = HashMap()
    private val _consultores = ArrayList<Personaje>()
    var dueñoID = 0
    private var _kamas: Long = 0
    var clave = "-"
    fun actualizarCofre(objetos: String, kamas: Long, clave: String, dueñoID: Int) {
        for (str in objetos.split(Pattern.quote("|").toRegex()).toTypedArray()) {
            try {
                if (str.isEmpty()) {
                    continue
                }
                val infos = str.split(":".toRegex()).toTypedArray()
                val objetoID = infos[0].toInt()
                val objeto = getObjeto(objetoID) ?: continue
                _objetos[objetoID] = objeto
            } catch (ignored: Exception) {
            }
        }
        addKamas(kamas, null)
        this.clave = clave
        this.dueñoID = dueñoID
    }


    fun setKamasCero() {
        _kamas = 0
    }

    override fun addKamas(kamas: Long, perso: Personaje?) {
        if (kamas == 0L) {
            return
        }
        _kamas += kamas
        if (_kamas < 0) {
            _kamas = 0
        }
    }

    override val kamas: Long
        get() = _kamas


    fun intentarAcceder(perso: Personaje, clave: String) {
        if (perso.estaDisponible(false, true)) {
            ENVIAR_BN_NADA(perso, "INTENTA COFRE 1")
            return
        }
        val casa = getCasa(casaID)
        if (casa == null) {
            abrirCofre(perso)
        } else {
            val esDelGremio = perso.gremio != null && perso.gremio.id == casa.gremioID
            if (casa.actParametros && !esDelGremio && casa.tieneDerecho(
                    Constantes.C_ACCESO_PROHIBIDO_COFRES_NO_MIEMBROS
                )
            ) {
                ENVIAR_Im_INFORMACION(perso, "1101")
            } else if (clave.isEmpty()) {
                if (esSuCofreOPublico(perso) || this.clave == "-" || casa.actParametros && esDelGremio && casa
                        .tieneDerecho(Constantes.C_ACCESOS_COFRES_MIEMBROS_SIN_CODIGO)
                ) {
                    abrirCofre(perso)
                } else {
                    ponerClave(perso, false) // para insertar clave
                }
            } else {
                if (clave == this.clave) {
                    cerrarVentanaClave(perso)
                    abrirCofre(perso)
                } else {
                }
                ENVIAR_KKE_ERROR_CLAVE(perso)
            }
        }
    }

    fun modificarClave(perso: Personaje, packet: String) {
        if (packet.isEmpty()) {
            return
        }
        if (dueñoID == perso.Id) {
            clave = packet
        }
        cerrarVentanaClave(perso)
    }

    fun ponerClave(perso: Personaje, modificarClave: Boolean) {
        perso.consultarCofre = this
        ENVIAR_KCK_VENTANA_CLAVE(perso, modificarClave, 8.toByte()) // para bloquear
    }

    fun cerrarVentanaClave(perso: Personaje) {
        perso.consultarCofre = null
        ENVIAR_KV_CERRAR_VENTANA_CLAVE(perso)
    }

    fun abrirCofre(perso: Personaje) {
        if (!_consultores.contains(perso)) {
            _consultores.add(perso)
        }
        perso.exchanger = this
        perso.tipoExchange = Constantes.INTERCAMBIO_TIPO_COFRE
        ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(perso, Constantes.INTERCAMBIO_TIPO_COFRE.toInt(), "")
        ENVIAR_EL_LISTA_EXCHANGER(perso, this)
    }

    fun esSuCofreOPublico(perso: Personaje): Boolean {
        return dueñoID == perso.Id || dueñoID == -1
    }

    val objetos: Collection<Objeto?>
        get() = _objetos.values

    override fun getListaExchanger(perso: Personaje): String {
        val packet = StringBuilder()
        for (objeto in _objetos.values) {
            if (objeto == null) {
                continue
            }
            packet.append("O").append(objeto.stringObjetoConGuiño())
        }
        if (_kamas > 0) {
            packet.append("G").append(kamas)
        }
        return packet.toString()
    }

    fun analizarObjetoCofreABD(): String {
        val str = StringBuilder()
        for (objeto in _objetos.values) {
            if (objeto == null) {
                continue
            }
            if (str.length > 0) {
                str.append("|")
            }
            str.append(objeto.id)
        }
        return str.toString()
    }

    fun addObjetoRapido(obj: Objeto?) {
        if (obj == null) {
            return
        }
        _objetos[obj.id] = obj
    }

    @Synchronized
    override fun addObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        if (casaID != -1) {
            if (objeto.tieneStatTexto(Constantes.STAT_LIGADO_A_CUENTA)) {
                ENVIAR_BN_NADA(perso, "INTERCAMBIO MOVER LIGADO")
                return
            }
            if (objeto.pasoIntercambiableDesde()) {
                ENVIAR_BN_NADA(perso, "INTERCAMBIO MOVER NO INTERCAMBIABLE")
                return
            }
        }
        if (_objetos.size >= _limite) {
            ENVIAR_cs_CHAT_MENSAJE(
                perso, "Llegaste al máximo de objetos que puede soportar este cofre",
                Constantes.COLOR_ROJO
            )
            return
        }
        if (!perso.tieneObjetoID(objeto.id) || objeto.posicion != Constantes.OBJETO_POS_NO_EQUIPADO) {
            ENVIAR_Im_INFORMACION(perso, "1OBJECT_DONT_EXIST")
            return
        }
        if (cantidad > objeto.cantidad) {
            cantidad = objeto.cantidad
        }
        var str = ""
        var cofreObj = objetoSimilarEnElCofre(objeto)
        val nuevaCant = objeto.cantidad - cantidad
        if (cofreObj == null) {
            if (nuevaCant <= 0) {
                perso.borrarOEliminarConOR(objeto.id, false)
                _objetos[objeto.id] = objeto
                str = "O+" + objeto.id + "|" + objeto.cantidad + "|" + objeto.objModeloID + "|" + objeto
                    .convertirStatsAString(false)
            } else {
                cofreObj = objeto.clonarObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                addObjeto(cofreObj, false)
                _objetos[cofreObj.id] = cofreObj
                objeto.cantidad = nuevaCant
                str =
                    "O+" + cofreObj.id + "|" + cofreObj.cantidad + "|" + cofreObj.objModeloID + "|" + cofreObj
                        .convertirStatsAString(false)
                ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objeto)
            }
        } else {
            if (nuevaCant <= 0) {
                perso.borrarOEliminarConOR(objeto.id, true)
                cofreObj.cantidad = cofreObj.cantidad + objeto.cantidad
                str =
                    "O+" + cofreObj.id + "|" + cofreObj.cantidad + "|" + cofreObj.objModeloID + "|" + cofreObj
                        .convertirStatsAString(false)
            } else {
                objeto.cantidad = nuevaCant
                cofreObj.cantidad = cofreObj.cantidad + cantidad
                str =
                    "O+" + cofreObj.id + "|" + cofreObj.cantidad + "|" + cofreObj.objModeloID + "|" + cofreObj
                        .convertirStatsAString(false)
                ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objeto)
            }
        }
        for (pj in _consultores) {
            ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(pj, str)
        }
    }

    @Synchronized
    override fun remObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        if (!_objetos.containsKey(objeto.id)) {
            ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, "O-" + objeto.id)
            return
        }
        if (cantidad > objeto.cantidad) {
            cantidad = objeto.cantidad
        }
        val str: String
        val nuevaCant = objeto.cantidad - cantidad
        if (nuevaCant < 1) {
            _objetos.remove(objeto.id)
            perso.addObjIdentAInventario(objeto, true)
            str = "O-" + objeto.id
        } else {
            val nuevoObj = objeto.clonarObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
            perso.addObjIdentAInventario(nuevoObj, true)
            objeto.cantidad = nuevaCant
            str = "O+" + objeto.id + "|" + objeto.cantidad + "|" + objeto.objModeloID + "|" + objeto
                .convertirStatsAString(false)
        }
        for (pj in _consultores) {
            ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(pj, str)
        }
    }

    @Synchronized
    private fun objetoSimilarEnElCofre(objeto: Objeto): Objeto? {
        if (objeto.puedeTenerStatsIguales()) {
            for (obj in _objetos.values) {
                if (esPosicionEquipamiento(obj!!.posicion)) {
                    continue
                }
                if (objeto.id != obj.id && obj.objModeloID == objeto.objModeloID && obj.sonStatsIguales(
                        objeto
                    )
                ) {
                    return obj
                }
            }
        }
        return null
    }

    @Synchronized
    fun limpiarCofre() {
        for ((key) in _objetos) {
            eliminarObjeto(key)
        }
        _objetos.clear()
    }

    @Synchronized
    fun moverCofreABanco(cuenta: Cuenta) {
        for (obj in _objetos.values) {
            cuenta.addObjetoAlBanco(obj)
        }
        _objetos.clear()
    }

    override fun cerrar(perso: Personaje?, exito: String) {
        _consultores.remove(perso)
        if (perso != null) {
            perso.cerrarVentanaExchange(exito)
        }
    }

    override fun botonOK(perso: Personaje) {}

    companion object {
        fun insertarCofre(mapaID: Short, celdaID: Short): Cofre? {
            return try {
                val casa = getCasaDentroPorMapa(mapaID) ?: return null
                val c = getCofrePorUbicacion(mapaID, celdaID)
                if (c != null) {
                    return null
                }
                if (getMapa(mapaID)!!.getCelda(celdaID)!!.objetoInteractivo == null) {
                    return null
                }
                var id = GET_COFRE_POR_MAPA_CELDA(mapaID, celdaID)
                if (id == -1) {
                    INSERT_COFRE_MODELO(casa.id, mapaID, celdaID)
                    id = GET_COFRE_POR_MAPA_CELDA(mapaID, celdaID)
                    if (id == -1) {
                        return null
                    }
                }
                val cofre = Cofre(id, casa.id, mapaID, celdaID, AtlantaMain.LIMITE_OBJETOS_COFRE)
                cofre.actualizarCofre("", 0, "-", if (casa.dueño != null) casa.dueño!!.Id else 0)
                addCofre(cofre)
                REPLACE_COFRE(cofre, false)
                cofre
            } catch (e: Exception) {
                null
            }
        }
    }

    init {
        if (iD <= 0) {
            dueñoID = -1
        }
        _limite = limite
    }
}