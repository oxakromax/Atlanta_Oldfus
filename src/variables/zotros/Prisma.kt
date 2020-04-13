package variables.zotros

import estaticos.AtlantaMain
import estaticos.Constantes
import estaticos.GestorSalida.ENVIAR_CD_MENSAJE_MURIO_PRISMA
import estaticos.GestorSalida.ENVIAR_CP_INFO_DEFENSORES_PRISMA
import estaticos.GestorSalida.ENVIAR_CS_MENSAJE_SOBREVIVIO_PRISMA
import estaticos.GestorSalida.ENVIAR_Cp_INFO_ATACANTES_PRISMA
import estaticos.GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA
import estaticos.GestorSalida.ENVIAR_GM_PRISMA_A_MAPA
import estaticos.GestorSalida.ENVIAR_aM_CAMBIAR_ALINEACION_AREA
import estaticos.GestorSalida.ENVIAR_am_CAMBIAR_ALINEACION_SUBAREA
import estaticos.Mundo
import estaticos.Mundo.eliminarPrisma
import estaticos.Mundo.getArea
import estaticos.Mundo.getExpAlineacion
import estaticos.Mundo.getMapa
import estaticos.Mundo.getMobModelo
import estaticos.Mundo.getSubArea
import sprites.PreLuchador
import variables.hechizo.StatHechizo
import variables.mapa.Area
import variables.mapa.Celda
import variables.mapa.Mapa
import variables.mapa.SubArea
import variables.mob.MobGradoModelo
import variables.pelea.Pelea
import variables.stats.Stats
import variables.stats.TotalStats
import java.util.*

class Prisma(
    private val _id: Int, private val _alineacion: Byte, nivel: Byte, mapaID: Short, celdaID: Short,
    honor: Int, area: Int, subArea: Int, tiempoProteccion: Long
) : PreLuchador {
    private val _dir: Byte
    val mapa: Mapa?
    val celda: Celda?
    private var _idMob: Short = 0
    private val _gfx: Short
    val area: Area?
    val subArea: SubArea?
    private val _stats = Stats()
    private val _totalStats = TotalStats(_stats, null, Stats(), null, 4)
    private val _hechizos: MutableMap<Int, StatHechizo> = TreeMap()
    private var _honor = 0
    private var _PDVMAX = 0
    private var _nivel: Int
    var tiempoProteccion: Long
        private set
    var pelea: Pelea? = null
        private set

    fun addTiempProtecion(segundos: Int) {
        var l = Math.max(System.currentTimeMillis(), tiempoProteccion)
        l += segundos * 1000L
        if (l < System.currentTimeMillis()) {
            l = 0
        }
        tiempoProteccion = l
    }

    val tiempoRestProteccion: Long
        get() {
            var l = tiempoProteccion - System.currentTimeMillis()
            if (l < 0) {
                l = 0
            }
            return l
        }

//    fun getId(): Int {
//        return _id
//    }
//
//    fun getAlineacion(): Byte {
//        return _alineacion
//    }
//
//    fun getHechizos(): Map<Int, StatHechizo> {
//        return _hechizos
//    }

    private fun actualizarStats(mob: MobGradoModelo?) {
        _stats.nuevosStats(mob!!.stats)
        _stats.fijarStatID(Constantes.STAT_MAS_INICIATIVA, _nivel * 1000)
        if (_hechizos != null) {
            mob.hechizos.let { _hechizos.putAll(it) }
        }
        _PDVMAX = mob.pDVMAX
    }

    val estadoPelea: Int
        get() {
            if (pelea == null) {
                return -1
            }
            if (pelea!!.fase == Constantes.PELEA_FASE_POSICION) {
                return 0
            }
            return if (pelea!!.fase == Constantes.PELEA_FASE_COMBATE) {
                -2
            } else 4
        }

    override fun setPelea(pelea: Pelea?) {
        this.pelea = pelea
    }

//    fun getHonor(): Int {
//        return _honor
//    }

    override fun addHonor(honor: Int) {
        _honor += honor
        val nivel = _nivel
        if (_honor < 0) {
            _honor = 0
        } else if (_honor >= getExpAlineacion(AtlantaMain.NIVEL_MAX_ALINEACION)) {
            _nivel = AtlantaMain.NIVEL_MAX_ALINEACION
            _honor = getExpAlineacion(AtlantaMain.NIVEL_MAX_ALINEACION)
        }
        for (n in 1..AtlantaMain.NIVEL_MAX_ALINEACION) {
            if (_honor < getExpAlineacion(n)) {
                _nivel = (n - 1)
                break
            }
        }
        if (nivel != _nivel) {
            val mob =
                getMobModelo(_idMob.toInt())!!.getGradoPorNivel(Math.ceil(_nivel / 2f.toDouble()).toInt())
            actualizarStats(mob)
        }
    }

    fun stringGM(): String {
        return if (pelea != null) {
            ""
        } else celda!!.id.toString() + ";" + _dir + ";0;" + _id + ";" + _idMob + ";-10;" + _gfx + "^100;" + _nivel + ";" + _nivel + ";" + _alineacion

    }

    fun atacantesDePrisma(): String {
        val str = StringBuilder("+" + Integer.toString(_id, 36))
        for (luchador in pelea!!.luchadoresDeEquipo(1)) {
            val perso = luchador.personaje ?: continue
            str.append("|").append(Integer.toString(perso.Id, 36)).append(";")
            str.append(perso.nombre).append(";")
            str.append(perso.nivel).append(";")
        }
        return str.toString()
    }

    fun defensoresDePrisma(): String {
        val str = StringBuilder("+" + Integer.toString(_id, 36))
        val stra = StringBuilder("-")
        for (luchador in pelea!!.luchadoresDeEquipo(2)) {
            val perso = luchador.personaje ?: continue
            str.append("|").append(Integer.toString(perso.Id, 36)).append(";")
            str.append(perso.nombre).append(";")
            str.append(perso.getGfxID(false)).append(";")
            str.append(perso.nivel).append(";")
            if (pelea!!.cantLuchDeEquipo(2) >= 8) {
                str.append("1;")
            } else {
                str.append("0;")
            }
        }
        stra.append(str.substring(1))
        pelea!!.setListaDefensores(stra.toString())
        return str.toString()
    }

    override fun actualizarAtacantesDefensores() {
        val str = atacantesDePrisma()
        val str2 = defensoresDePrisma()
        for (perso in Mundo.PERSONAJESONLINE) {
            if (perso.alineacion == _alineacion) {
                ENVIAR_CP_INFO_DEFENSORES_PRISMA(perso, str2)
                ENVIAR_Cp_INFO_ATACANTES_PRISMA(perso, str)
            }
        }
    }

    fun analizarPrismas(alineacion: Byte): String {
        return if (alineacion != _alineacion) {
            "-3"
        } else if (estadoPelea == 0) {
            "0;" + pelea!!.tiempoFaltInicioPelea + ";" + AtlantaMain.SEGUNDOS_INICIO_PELEA * 1000 + ";7"
        } else {
            estadoPelea.toString() + ""
        }
    }

    override val Alineacion: Byte
        get() = _alineacion
    override val id: Int
        get() = _id

    override fun getGfxID(buff: Boolean): Int {
        return _gfx.toInt()
    }

    override val pdvMax: Int
        get() = _PDVMAX
    override var pdv: Int
        get() = _PDVMAX
        set(value) {
            _PDVMAX = value
        }
    override val TotalStatsPelea: TotalStats?
        get() = _totalStats
    override val Nivel: Int
        get() = _nivel
    override val GradoAlineacion: Int
        get() = _nivel

//    fun getPdvMax(): Int {
//        return _PDVMAX
//    }
//
//    fun getPdv(): Int {
//        return _PDVMAX
//    }
//
//    fun getTotalStatsPelea(): TotalStats {
//        return _totalStats
//    }
//
//    fun getNivel(): Int {
//        return _nivel
//    }

    override fun stringGMLuchador(): String {
        val str = StringBuilder()
        str.append("-2;")
        str.append(if (_alineacion.toInt() == 1) 8101 else 8100).append("^100;")
        str.append(_nivel).append(";")
        str.append("-1;-1;-1;")
        str.append("0,0,0,0;")
        return str.toString()
    }

    override val Hechizos: Map<Int, StatHechizo>
        get() = _hechizos
    override val Deshonor: Int
        get() = 0
    override val Honor: Int
        get() = _honor

    override fun sobrevivio() {
        val str = mapa!!.id.toString() + "|" + mapa.x + "|" + mapa.y
        for (pj in Mundo.PERSONAJESONLINE) {
            if (pj.alineacion == _alineacion) {
                ENVIAR_CS_MENSAJE_SOBREVIVIO_PRISMA(pj, str)
            }
        }
        setPelea(null)
        ENVIAR_GM_PRISMA_A_MAPA(mapa, "+" + stringGM())
    }

    override fun murio() {
        val str = mapa!!.id.toString() + "|" + mapa.x + "|" + mapa.y
        for (pj in Mundo.PERSONAJESONLINE) {
            if (pj.alineacion == _alineacion) {
                ENVIAR_CD_MENSAJE_MURIO_PRISMA(pj, str)
            }
            if (area != null) {
                ENVIAR_aM_CAMBIAR_ALINEACION_AREA(pj, area.id, (-1).toByte())
            }
            ENVIAR_am_CAMBIAR_ALINEACION_SUBAREA(pj, subArea!!.id, Constantes.ALINEACION_NULL, true)
            ENVIAR_am_CAMBIAR_ALINEACION_SUBAREA(pj, subArea.id, Constantes.ALINEACION_NEUTRAL, false)
        }
        ENVIAR_GM_BORRAR_GM_A_MAPA(mapa, _id)
        eliminarPrisma(this)
    }

//    fun getGradoAlineacion(): Int {
//        return _nivel
//    }
//
//    fun getDeshonor(): Int {
//        return 0
//    }

    override fun addDeshonor(honor: Int): Boolean {
        return false
    }

    override fun addKamasGanada(kamas: Long) { // TODO Auto-generated method stub
    }

    override fun addXPGanada(exp: Long) { // TODO Auto-generated method stub
    }

    // public void destruir() {
// try {
// this.finalize();
// } catch (Throwable e) {}
// }
//
    init {
        _nivel = nivel.toInt()
        mapa = getMapa(mapaID)
        celda = mapa!!.getCelda(celdaID)
        _dir = 1
        _idMob = if (_alineacion == Constantes.ALINEACION_BONTARIANO) {
            1111
        } else {
            1112
        }
        val mob =
            getMobModelo(_idMob.toInt())!!.getGradoPorNivel(Math.ceil(_nivel / 2f.toDouble()).toInt())
        actualizarStats(mob)
        _gfx = mob!!.gfxID
        _honor = honor
        this.subArea = getSubArea(subArea)
        this.area = getArea(area)
        this.tiempoProteccion = tiempoProteccion
    }
}