package sprites

import variables.hechizo.StatHechizo
import variables.pelea.Pelea
import variables.stats.TotalStats

interface PreLuchador {
    val Alineacion: Byte
    val id: Int
    fun getGfxID(rolePlayBuff: Boolean): Int
    val pdvMax: Int
    var pdv: Int
    val TotalStatsPelea: TotalStats?
    val Nivel: Int
    val GradoAlineacion: Int
    fun setPelea(pelea: Pelea?)
    fun actualizarAtacantesDefensores()
    fun stringGMLuchador(): String?
    val Hechizos: Map<Int, StatHechizo>
    val Deshonor: Int
    val Honor: Int
    fun addHonor(honor: Int)
    fun addDeshonor(honor: Int): Boolean
    fun addKamasGanada(kamas: Long)
    fun addXPGanada(exp: Long)
    fun murio()
    fun sobrevivio()
}