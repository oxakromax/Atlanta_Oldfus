package variables.mob

import sprites.PreLuchador
import variables.hechizo.StatHechizo
import variables.mapa.Celda
import variables.pelea.Pelea
import variables.stats.Stats
import variables.stats.TotalStats

class MobGrado(
    val mobGradoModelo: MobGradoModelo, val mobModelo: MobModelo, grado: Byte, nivel: Short,
    pdvMax: Int, stats: Stats?
) : PreLuchador {
    private val _grado: Byte
    private val _nivel: Short
    val stats: Stats = Stats(stats!!)
    private val _totalStats: TotalStats?
    private var _grupoMob: GrupoMob? = null
    private var _id = 0
    private var _PDV: Int
    private var _PDVMAX: Int
    var celdaPelea: Celda? = null
    fun setIDPersonal(id: Int) {
        _id = id
    }

    fun setGrupoMob(gm: GrupoMob?) {
        _grupoMob = gm
    }

    val baseXp: Long
        get() = mobGradoModelo.baseXp.toLong()

//    fun getHechizos(): Map<Int, StatHechizo> {
//        return mobGradoModelo.hechizos
//    }

    val idModelo: Int
        get() = mobModelo.id


    fun setPDVMAX(pdv: Int) {
        _PDVMAX = pdv
    }


    override val Alineacion: Byte
        get() = mobModelo.alineacion
    override val id: Int
        get() = _id

    override fun getGfxID(buff: Boolean): Int {
        return mobGradoModelo.gfxID.toInt()
    }

    override val pdvMax: Int
        get() = _PDVMAX
    override var pdv: Int
        get() = _PDV
        set(value) {
            _PDV = value
        }
    override val TotalStatsPelea: TotalStats?
        get() = _totalStats
    override val Nivel: Int
        get() = _nivel.toInt()
    override val GradoAlineacion: Int
        get() = _grado.toInt()

//    fun getTotalStatsPelea(): TotalStats {
//        return _totalStats
//    }
//
//    fun getAlineacion(): Byte {
//        return mobModelo.alineacion
//    }

    override fun stringGMLuchador(): String {
        val str = StringBuilder()
        str.append("-2;")
        str.append(mobGradoModelo.gfxID.toInt()).append("^").append(mobModelo.talla.toInt()).append(";")
        str.append(_grado.toInt()).append(";")
        str.append(mobModelo.colores.replace(",", ";")).append(";")
        str.append("0,0,0,0;")
        return str.toString()
    }

    override val Hechizos: Map<Int, StatHechizo>
        get() = mobGradoModelo.hechizos
    override val Deshonor: Int
        get() = 0
    override val Honor: Int = 0

//    fun getDeshonor(): Int {
//        return 0
//    }
//
//    fun getHonor(): Int {
//        return Honor
//    }
//
//    fun getGradoAlineacion(): Int {
//        return 1
//    }

    override fun addHonor(honor: Int) {}
    override fun addDeshonor(honor: Int): Boolean {
        return false
    }

    override fun addKamasGanada(kamas: Long) {
        if (_grupoMob != null) _grupoMob!!.addKamasHeroico(kamas)
    }

    override fun addXPGanada(exp: Long) {}
    override fun setPelea(pelea: Pelea?) {}
    override fun actualizarAtacantesDefensores() {}
    override fun murio() {}
    override fun sobrevivio() {}

    init {
        _totalStats = TotalStats(this.stats, null, Stats(), null, 2)
        _grado = grado
        _nivel = nivel
        _PDVMAX = pdvMax
        _PDV = _PDVMAX
    }
}