package variables.personaje

import estaticos.Constantes
import java.util.*

class Clase(
    val id: Int,
    gfxs: String,
    tallas: String,
    val mapaInicio: Short,
    val celdaInicio: Short,
    PDV: Int,
    boostVitalidad: String,
    boostSabiduria: String,
    boostFuerza: String,
    boostInteligencia: String,
    boostAgilidad: String,
    boostSuerte: String,
    stats: String,
    hechizos: String
) {
    private val _gfxs = ArrayList<Int>(3)
    private val _tallas = ArrayList<Int>(3)
    private val _boostFuerza = ArrayList<BoostStat>()
    private val _boostInteligencia = ArrayList<BoostStat>()
    private val _boostVitalidad = ArrayList<BoostStat>()
    private val _boostSabiduria = ArrayList<BoostStat>()
    private val _boostAgilidad = ArrayList<BoostStat>()
    private val _boostSuerte = ArrayList<BoostStat>()
    private val _stats: MutableMap<Int, Int> = HashMap()
    private val _hechizos: MutableMap<Int, Int> = HashMap()
    var pDV = 50
    private fun addBoostStat(sBoost: String, boost: ArrayList<BoostStat>) {
        for (s in sBoost.split("\\|".toRegex()).toTypedArray()) {
            try {
                val ss = s.split(",".toRegex()).toTypedArray()
                val inicio = ss[0].toInt()
                val coste = ss[1].toInt()
                var puntos = 1
                try {
                    puntos = ss[2].toInt()
                } catch (ignored: Exception) {
                }
                boost.add(BoostStat(inicio, coste, puntos))
            } catch (ignored: Exception) {
            }
        }
    }

    fun getBoostStat(statID: Int, valorStat: Int): BoostStat {
        val boosts: ArrayList<BoostStat> = when (statID) {
            Constantes.STAT_MAS_VITALIDAD -> _boostVitalidad
            Constantes.STAT_MAS_FUERZA -> _boostFuerza
            Constantes.STAT_MAS_INTELIGENCIA -> _boostInteligencia
            Constantes.STAT_MAS_AGILIDAD -> _boostAgilidad
            Constantes.STAT_MAS_SUERTE -> _boostSuerte
            else -> _boostSabiduria
        }
        var boost = BoostStat.BoostDefecto
        var temp = -1
        for (b in boosts) {
            if (b._inicio in (temp + 1)..valorStat) {
                temp = b._inicio
                boost = b
            }
        }
        return boost
    }

    fun aprenderHechizo(perso: Personaje, nivel: Int): Boolean {
        var bool = false
        for ((key, value) in _hechizos) {
            if (value == nivel) {
                perso.fijarNivelHechizoOAprender(key, 1, false)
                bool = true
            }
        }
        return bool
    }

    val stats: Map<Int, Int>
        get() = _stats

    fun getGfxs(index: Int): Int {
        return try {
            _gfxs[index]
        } catch (e: Exception) {
            id * 10 + 3
        }
    }

    fun getTallas(index: Int): Int {
        return try {
            _tallas[index]
        } catch (e: Exception) {
            100
        }
    }

    class BoostStat(val _inicio: Int, val coste: Int, val puntos: Int) {

        companion object {
            val BoostDefecto = BoostStat(0, 1, 1)
        }

    }

    init {
        pDV = PDV
        for (s in gfxs.split(",".toRegex()).toTypedArray()) {
            try {
                _gfxs.add(s.toInt())
            } catch (ignored: Exception) {
            }
        }
        for (s in tallas.split(",".toRegex()).toTypedArray()) {
            try {
                _tallas.add(s.toInt())
            } catch (ignored: Exception) {
            }
        }
        addBoostStat(boostVitalidad, _boostVitalidad)
        addBoostStat(boostSabiduria, _boostSabiduria)
        addBoostStat(boostFuerza, _boostFuerza)
        addBoostStat(boostInteligencia, _boostInteligencia)
        addBoostStat(boostAgilidad, _boostAgilidad)
        addBoostStat(boostSuerte, _boostSuerte)
        for (s in stats.split("\\|".toRegex()).toTypedArray()) {
            try {
                _stats[s.split(",".toRegex()).toTypedArray()[0].toInt()] =
                    s.split(",".toRegex()).toTypedArray()[1].toInt()
            } catch (ignored: Exception) {
            }
        }
        for (s in hechizos.split("\\|".toRegex()).toTypedArray()) {
            try {
                _hechizos[s.split(",".toRegex()).toTypedArray()[1].toInt()] =
                    s.split(",".toRegex()).toTypedArray()[0].toInt()
            } catch (ignored: Exception) {
            }
        }
    }
}