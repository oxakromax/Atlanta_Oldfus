package variables.encarnacion

import estaticos.Mundo.getHechizo
import java.util.*

class EncarnacionModelo(
    val gfxID: Int,
    statsBase: String,
    statsPorNivel: String,
    strHechizos: String
) {
    private val _posicionHechizos: MutableMap<Int, Char> = HashMap()
    private val _statsBase: MutableMap<Int, Int> = HashMap()
    private val _statsPorNivel: MutableMap<Int, Float> = HashMap()
    private fun analizarStatsBase(statsBase: String) {
        for (s in statsBase.split("\\|".toRegex()).toTypedArray()) {
            try {
                if (s.isEmpty()) {
                    continue
                }
                val statID = s.split(",".toRegex()).toTypedArray()[0].toInt()
                val valor = s.split(",".toRegex()).toTypedArray()[1].toInt()
                _statsBase[statID] = valor
            } catch (ignored: Exception) {
            }
        }
    }

    private fun analizarStatsPorNivel(statsPorNivel: String) {
        for (s in statsPorNivel.split("\\|".toRegex()).toTypedArray()) {
            try {
                if (s.isEmpty()) {
                    continue
                }
                val statID = s.split(",".toRegex()).toTypedArray()[0].toInt()
                val valor = s.split(",".toRegex()).toTypedArray()[1].toFloat()
                _statsPorNivel[statID] = valor
            } catch (ignored: Exception) {
            }
        }
    }

    private fun analizarPosHechizos(str: String) {
        val hechizos = str.split(";".toRegex()).toTypedArray()
        for (s in hechizos) {
            try {
                if (s.isEmpty()) {
                    continue
                }
                val id = s.split(",".toRegex()).toTypedArray()[0].toInt()
                if (getHechizo(id) == null) {
                    continue
                }
                val pos = s.split(",".toRegex()).toTypedArray()[1][0]
                _posicionHechizos[id] = pos
            } catch (ignored: Exception) {
            }
        }
    }

    val posicionsHechizos: Map<Int, Char>
        get() = _posicionHechizos

    val statsBase: Map<Int, Int>
        get() = _statsBase

    val statsPorNivel: Map<Int, Float>
        get() = _statsPorNivel

    init {
        analizarPosHechizos(strHechizos)
        analizarStatsBase(statsBase)
        analizarStatsPorNivel(statsPorNivel)
    }
}