package variables.gremio

import estaticos.AtlantaMain
import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Camino
import estaticos.Constantes
import estaticos.Constantes.esPosicionEquipamiento
import estaticos.Encriptador.celdaIDAHash
import estaticos.Encriptador.getValorHashPorNumero
import estaticos.GestorSQL.DELETE_RECAUDADOR
import estaticos.GestorSQL.SALVAR_OBJETO
import estaticos.GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO
import estaticos.GestorSalida.ENVIAR_GA_MOVER_SPRITE_MAPA
import estaticos.GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA
import estaticos.GestorSalida.ENVIAR_GM_RECAUDADOR_A_MAPA
import estaticos.GestorSalida.ENVIAR_gA_MENSAJE_SOBRE_RECAUDADOR
import estaticos.GestorSalida.ENVIAR_gITM_GREMIO_INFO_RECAUDADOR
import estaticos.GestorSalida.ENVIAR_gITP_INFO_DEFENSORES_RECAUDADOR
import estaticos.GestorSalida.ENVIAR_gITp_INFO_ATACANTES_RECAUDADOR
import estaticos.GestorSalida.ENVIAR_gT_PANEL_RECAUDADORES_GREMIO
import estaticos.Mundo.addObjeto
import estaticos.Mundo.eliminarObjeto
import estaticos.Mundo.eliminarRecaudador
import estaticos.Mundo.getGremio
import estaticos.Mundo.getMapa
import estaticos.Mundo.getObjeto
import estaticos.Mundo.getPersonaje
import sprites.Exchanger
import sprites.PreLuchador
import sprites.Preguntador
import variables.hechizo.StatHechizo
import variables.mapa.Celda
import variables.mapa.Mapa
import variables.objeto.Objeto
import variables.pelea.Pelea
import variables.personaje.Personaje
import variables.stats.Stats
import variables.stats.TotalStats
import java.util.*
import java.util.regex.Pattern

class Recaudador(
    private val _id: Int, mapa2: Short, celdaID: Short, orientacion: Byte, gremioID: Int,
    N1: String, N2: String, objetos: String, kamas: Long, xp: Long, tiempoProteccion: Long,
    tiempoCreacion: Long, dueño: Int
) : PreLuchador, Exchanger, Preguntador {
    val dueño: Int
    val tiempoCreacion: Long
    private val _objetos: MutableMap<Int, Objeto> = TreeMap()
    private val _objModeloID: MutableMap<Int, Int> = TreeMap()
    val gremio: Gremio?
    val mapa: Mapa?
    private var _direccion: Byte
    private var _kamas: Long = 0
    var exp: Long
        private set
    private var _proxMovimiento: Long = -1
    var tiempoProteccion: Long
        private set
    //
// public byte getEstadoPelea() {
// try {
// return _pelea.getFase();
// } catch (Exception e) {
// return 0;
// }
// }
    var enRecolecta = false
        private set
    var n1 = ""
    var n2 = ""
    var pelea: Pelea? = null
        private set
    var celda: Celda? = null
        private set
    private var _totalStats: TotalStats? = null
    private var _recaudando: Personaje? = null
    private var _tiempo_recaudacion: Long = 0

    fun addTiempProtecion(segundos: Int) {
        var l = Math.max(System.currentTimeMillis(), tiempoProteccion)
        l += segundos * 1000L
        if (l < System.currentTimeMillis()) {
            l = 0
        }
        tiempoProteccion = l
    }

    fun addTiempo_Proteccion_Recaudacion(segundos: Int) {
        var l = Math.max(System.currentTimeMillis(), _tiempo_recaudacion)
        l += segundos * 1000L
        if (l < System.currentTimeMillis()) {
            l = 0
        }
        _tiempo_recaudacion = l
    }

    val tiempoRestProteccion: Long
        get() {
            var l = tiempoProteccion - System.currentTimeMillis()
            if (l < 0) {
                l = 0
            }
            return l
        }

    val tiempoRestProteccionRecaudacion: Long
        get() {
            var l = _tiempo_recaudacion - System.currentTimeMillis()
            if (l < 0) {
                l = 0
            }
            return l
        }

    private fun restarMovimiento() {
        _proxMovimiento = System.currentTimeMillis() + AtlantaMain.SEGUNDOS_MOVER_RECAUDADOR * 1000
    }

//    fun getId(): Int {
//        return _id
//    }

    override fun setPelea(pelea: Pelea?) {
        this.pelea = pelea
    }

    val peleaID: Short
        get() = if (pelea == null) {
            -1
        } else pelea!!.ID

    val podsActuales: Int
        get() {
            var pods = 0
            for ((_, value) in _objetos) {
                pods += value.objModelo?.peso?.times(value.cantidad) ?: 0
            }
            return pods
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

    fun addExp(xp: Long) {
        exp += xp
    }

    fun setEnRecolecta(recolecta: Boolean, perso: Personaje?) {
        enRecolecta = recolecta
        if (recolecta && tiempoRestProteccionRecaudacion == 0L) {
            addTiempProtecion(300)
            addTiempo_Proteccion_Recaudacion(900)
        }
        if (_recaudando != null) {
            _recaudando!!.cerrarVentanaExchange("a")
        }
        if (recolecta) {
            _recaudando = perso
        }
    }

    val orientacion: Int
        get() = _direccion.toInt()

    fun puedeMoverRecaudador() {
        if (_proxMovimiento <= 0) {
            return
        }
        if (System.currentTimeMillis() - _proxMovimiento >= 0) {
            moverRecaudador()
            restarMovimiento()
        }
    }

    fun moverRecaudador() {
        if (enRecolecta || pelea != null) {
            return
        }
        val celdaDestino = Camino.celdaMoverSprite(mapa!!, celda!!.id)
        if (celdaDestino.toInt() == -1) {
            return
        }
        val pathCeldas = Camino.getPathPelea(
            mapa, celda!!.id, celdaDestino, -1, null,
            false
        )
            ?: return
        val celdas = pathCeldas._segundo
        val pathStr = Camino.getPathComoString2(mapa, celdas, celda!!.id, false)
        if (pathStr.isEmpty()) {
            redactarLogServidorln(
                "Fallo de desplazamiento de mob grupo: camino vacio - MapaID: " + mapa.id
                        + " - CeldaID: " + celda!!.id
            )
            return
        }
        //		try {
//			Thread.sleep(100);
//		} catch (final Exception e) {}
        ENVIAR_GA_MOVER_SPRITE_MAPA(
            mapa, 0, 1, _id.toString() + "", getValorHashPorNumero(_direccion.toInt())
                .toString() + celdaIDAHash(celda!!.id) + pathStr
        )
        _direccion = Camino.getIndexPorDireccion(pathStr[pathStr.length - 3])
        celda = mapa.getCelda(celdaDestino)
    }

    fun borrarRecaudador() {
        ENVIAR_GM_BORRAR_GM_A_MAPA(mapa!!, _id)
        for (obj in _objetos.values) {
            eliminarObjeto(obj.id)
        }
        eliminarRecaudador(this)
    }

    val objetos: Collection<Objeto>
        get() = _objetos.values

    fun clearObjetos() {
        _objetos.clear()
    }

    fun addObjAInventario(objeto: Objeto) {
        if (_objetos.containsKey(objeto.id)) {
            return
        }
        // tipo piedra de alma y mascota
        if (objeto.puedeTenerStatsIguales()) {
            for (obj in _objetos.values) {
                if (esPosicionEquipamiento(obj.posicion)) {
                    continue
                }
                if (objeto.id != obj.id && obj.objModeloID == objeto.objModeloID && obj.sonStatsIguales(
                        objeto
                    )
                ) {
                    obj.cantidad = obj.cantidad + objeto.cantidad
                    if (objeto.id > 0) {
                        eliminarObjeto(objeto.id)
                    }
                    return
                }
            }
        }
        addObjeto(objeto)
    }

    private fun addObjeto(objeto: Objeto) {
        try {
            if (objeto.id == 0) {
                addObjeto(objeto, false)
            } else {
                SALVAR_OBJETO(objeto)
            }
            _objetos[objeto.id] = objeto
        } catch (ignored: Exception) {
        }
    }

    fun stringListaObjetosBD(): String {
        val str = StringBuilder()
        for (obj in _objetos.values) {
            str.append(obj.id).append("|")
        }
        return str.toString()
    }

    private fun stringRecolecta(): String {
        val str = StringBuilder("|" + exp)
        for ((key, value) in _objModeloID) {
            str.append(";").append(key).append(",").append(value)
        }
        // for (final Objeto obj : _objetos.values()) {
// str.append(";" + obj.getIDObjModelo() + "," + obj.getCantidad());
// }
        return str.toString()
    }

    fun stringPanelInfo(perso: Personaje): String {
        return n1 + "," + n2 + "|" + mapa!!.id + "|" + mapa.x + "|" + mapa.y + "|" + perso
            .nombre
    }

    fun stringGM(): String {
        if (pelea != null || gremio == null) {
            return ""
        }
        val str = StringBuilder()
        str.append(celda!!.id.toInt()).append(";")
        str.append(_direccion.toInt()).append(";")
        str.append("0;")
        str.append(_id).append(";")
        str.append(n1).append(",").append(n2).append(";")
        str.append("-6;") // tipo
        str.append("6000^100;") // gfxID ^ talla
        str.append(gremio.nivel.toInt()).append(";")
        str.append(gremio.nombre).append(";").append(gremio.emblema)
        return str.toString()
    }

    fun mensajeDeAtaque(): String {
        return "A" + n1 + "," + n2 + "|.|" + mapa!!.id + "|" + celda!!.id
    }

    fun atacantesAlGremio(): String {
        val str = StringBuilder("+" + Integer.toString(_id, 36))
        try {
            for (luchador in pelea!!.luchadoresDeEquipo(1)) {
                val perso = luchador.personaje ?: continue
                str.append("|").append(Integer.toString(perso.Id, 36)).append(";")
                str.append(perso.nombre).append(";")
                str.append(perso.nivel).append(";")
            }
        } catch (ignored: Exception) {
        }
        return str.toString()
    }

    fun defensoresDelGremio(): String {
        val str = StringBuilder("+" + Integer.toString(_id, 36))
        try {
            val stra = StringBuilder("-")
            for (luchador in pelea!!.luchadoresDeEquipo(2)) {
                val perso = luchador.personaje ?: continue
                str.append("|").append(Integer.toString(perso.Id, 36)).append(";")
                str.append(perso.nombre).append(";")
                str.append(perso.getGfxID(false)).append(";")
                str.append(perso.nivel).append(";")
            }
            stra.append(str.substring(1))
            pelea!!.setListaDefensores(stra.toString())
        } catch (ignored: Exception) {
        }
        return str.toString()
    }

    override fun getListaExchanger(perso: Personaje): String {
        val str = StringBuilder()
        for (obj in _objetos.values) {
            str.append("O").append(obj.stringObjetoConGuiño())
        }
        if (_kamas > 0) {
            str.append("G").append(_kamas)
        }
        return str.toString()
    }

    override fun actualizarAtacantesDefensores() {
        if (pelea == null) return
        val str = defensoresDelGremio()
        val str2 = atacantesAlGremio()
        if (gremio != null) {
            for (p in gremio.miembros) {
                if (p!!.enLinea()) {
                    ENVIAR_gITP_INFO_DEFENSORES_RECAUDADOR(p, str)
                    ENVIAR_gITp_INFO_ATACANTES_RECAUDADOR(p, str2)
                }
            }
        }
    }

    override fun sobrevivio() {
        val str = "S" + n1 + "," + n2 + "|.|" + mapa!!.id + "|" + celda!!.id
        val str2 = gremio!!.analizarRecaudadores()
        for (pj in gremio.miembros) {
            if (pj!!.enLinea()) {
                ENVIAR_gITM_GREMIO_INFO_RECAUDADOR(pj, str2)
                ENVIAR_gA_MENSAJE_SOBRE_RECAUDADOR(pj, str)
            }
        }
        setPelea(null)
        ENVIAR_GM_RECAUDADOR_A_MAPA(mapa, "+" + stringGM())
    }

    override fun murio() {
        val str = "D" + n1 + "," + n2 + "|.|" + mapa!!.id + "|" + celda!!.id
        val str2 = gremio!!.analizarRecaudadores()
        for (pj in gremio.miembros) {
            if (pj!!.enLinea()) {
                ENVIAR_gITM_GREMIO_INFO_RECAUDADOR(pj, str2)
                ENVIAR_gA_MENSAJE_SOBRE_RECAUDADOR(pj, str)
            }
        }
        borrarRecaudador()
    }

    override val Alineacion: Byte
        get() = Constantes.ALINEACION_NULL
    override val id: Int
        get() = _id

    override fun getGfxID(buff: Boolean): Int {
        return 6000 // recaudador
    }

    override val pdvMax: Int
        get() = gremio!!.nivel * 100
    override var pdv: Int
        get() = pdvMax
        set(value) {}
    override val TotalStatsPelea: TotalStats?
        get() = _totalStats


    override val Nivel: Int
        get() = gremio!!.nivel.toInt()
    override val GradoAlineacion: Int
        get() = 0


    override fun stringGMLuchador(): String {
        if (gremio != null) {
            return "-6;6000^100;" + gremio.nivel + ";"
        }
        return ""
    }
    fun HechizosGremio(): Map<Int, StatHechizo> {
        val r = mutableMapOf<Int, StatHechizo>()
        if (gremio != null) {
            for (entry in gremio.hechizos) {
                if (entry.value != null) {
                    r[entry.key] = entry.value!!
                }
            }
        }
        return r.toMap()
    }
    override val Hechizos: Map<Int, StatHechizo>
        get() = HechizosGremio()
    override val Deshonor: Int
        get() = 0
    override val Honor: Int
        get() = 0


    override fun addHonor(honor: Int) {}
    override fun addDeshonor(honor: Int): Boolean {
        return false
    }

    override fun addKamasGanada(kamas: Long) {}
    override fun addXPGanada(exp: Long) {}
    @Synchronized
    override fun addObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
    }

    @Synchronized
    override fun remObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        if (!_objetos.containsKey(objeto.id)) {
            return
        }
        if (cantidad > objeto.cantidad) {
            cantidad = objeto.cantidad
        }
        val nuevaCant = objeto.cantidad - cantidad
        if (nuevaCant < 1) {
            perso.addObjIdentAInventario(objeto, true)
            _objetos.remove(objeto.id)
            ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, "O-" + objeto.id)
        } else {
            val persoObj = objeto.clonarObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
            perso.addObjIdentAInventario(persoObj, true)
            objeto.cantidad = nuevaCant
            ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(
                perso, "O+" + objeto.id + "|" + objeto.cantidad + "|"
                        + objeto.objModeloID + "|" + objeto.convertirStatsAString(false)
            )
        }
        _objModeloID.putIfAbsent(objeto.objModeloID, 0)
        _objModeloID[objeto.objModeloID] = _objModeloID[objeto.objModeloID]!! + cantidad
    }

    override fun cerrar(perso: Personaje?, exito: String) {
        val s = gremio!!.analizarRecaudadores()
        val str = StringBuilder()
        str.append(perso?.let { stringPanelInfo(it) })
        str.append(stringRecolecta())
        for (miembro in gremio.miembros) {
            if (miembro!!.enLinea()) {
                ENVIAR_gITM_GREMIO_INFO_RECAUDADOR(miembro, s)
                ENVIAR_gT_PANEL_RECAUDADORES_GREMIO(miembro, 'G', str.toString())
            }
        }
        gremio.addExperiencia(exp, false)
        borrarRecaudador()
        perso?.cerrarVentanaExchange(exito)
    }

    override fun botonOK(perso: Personaje) {}
    override fun getArgsDialogo(args: String?): String? {
        if (gremio != null) {
            return ";" + gremio.infoGremio
        }
        return ""
    }

    val infoPanel: String
        get() {
            val str = StringBuilder()
            str.append(_id.toString(36)).append(";")
            str.append(n1).append(",").append(n2).append(",")
            val dueño = getPersonaje(dueño)
            if (dueño != null) {
                str.append(dueño.nombre)
            }
            str.append(",").append(tiempoCreacion).append(",,100000000,100000000")
            str.append(";").append(Integer.toString(mapa!!.id.toInt(), 36)).append(",").append(mapa.x.toInt())
                .append(",").append(mapa.y.toInt()).append(";")
            var estadoR = 0
            if (pelea != null) {
                when (pelea!!.fase) {
                    Constantes.PELEA_FASE_INICIO, Constantes.PELEA_FASE_POSICION -> estadoR = 1
                    Constantes.PELEA_FASE_COMBATE -> estadoR = 2
                }
            }
            str.append(estadoR).append(";")
            if (estadoR == 1) {
                str.append(pelea!!.tiempoFaltInicioPelea).append(";")
            } else {
                str.append("0;")
            }
            str.append((AtlantaMain.SEGUNDOS_INICIO_PELEA * 1000).toString() + ";")
            str.append(if (pelea == null) 0 else pelea!!.getPosPelea(2) - 1).append(";")
            return str.toString()
        }

    init {
        this.mapa = getMapa(mapa2)
        if (mapa != null) {
            celda = mapa.getCelda(celdaID)
        }
        _direccion = orientacion
        n1 = N1
        n2 = N2
        for (str in objetos.split(Pattern.quote("|").toRegex()).toTypedArray()) {
            try {
                val obj = getObjeto(str.toInt()) ?: continue
                _objetos[obj.id] = obj
            } catch (ignored: Exception) {
            }
        }
        exp = xp
        this.tiempoProteccion = tiempoProteccion
        this.tiempoCreacion = tiempoCreacion
        this.dueño = dueño
        addKamas(kamas, null)
        restarMovimiento()
        gremio = getGremio(gremioID)
        try {
            gremio!!.addRecaudador(this)
            _totalStats = TotalStats(gremio.statsPelea, null, Stats(), null, 3)
        } catch (e: Exception) {
            redactarLogServidorln("Recaudador con gremio inexistente $gremioID")
            DELETE_RECAUDADOR(_id)
        }
    }
}