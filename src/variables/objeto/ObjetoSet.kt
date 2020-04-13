package variables.objeto

import estaticos.AtlantaMain
import estaticos.AtlantaMain.redactarLogServidor
import estaticos.Constantes
import estaticos.Constantes.esEfectoHechizo
import estaticos.Constantes.esStatHechizo
import estaticos.Constantes.esStatRepetible
import estaticos.Constantes.esStatTexto
import estaticos.Mundo.getObjetoModelo
import variables.objeto.ObjetoModelo.Companion.statSimiliar
import variables.stats.Stats
import java.util.*

class ObjetoSet(val iD: Int, val nombre: String, objetos: String) {
    val objetosModelos = ArrayList<ObjetoModelo>()
    private val _bonus: MutableMap<Int, Stats> =
        TreeMap()
    private val _stats = ArrayList<String>()
    private val _cantidad = ArrayList<Int>()
    fun setStats(str: String, cantObjetos: Int) {
        if (str.isEmpty()) {
            return
        }
        _stats.add(str)
        _cantidad.add(cantObjetos)
        val stats = Stats()
        convertirStringAStatsSet(stats, str)
        _bonus[cantObjetos] = stats
    }

    fun recargar() {
        _bonus.clear()
        for (i in _stats.indices) {
            setStats(_stats[i], _cantidad[i])
        }
    }

    fun getBonusStatPorNroObj(numero: Int): Stats? {
        return try {
            _bonus[numero]
        } catch (e: Exception) {
            _bonus[1]
        }
    }

    companion object {
        private fun convertirStringAStatsSet(stats: Stats, strStats: String) {
            for (str in strStats.split(",".toRegex()).toTypedArray()) {
                if (str.isEmpty()) {
                    continue
                }
                try {
                    val splitStats = str.split("#".toRegex()).toTypedArray()
                    val statID = statSimiliar(splitStats[0].toInt(16))
                    var encontrado = false
                    for (a in Constantes.FUN_STATS_RESTRINGIDAS) {
                        if (statID == a) {
                            encontrado = true
                        }
                    }
                    if (esStatHechizo(statID)) {
                        stats.addStatHechizo(str)
                    } else if (esStatRepetible(statID)) {
                        stats.addStatRepetido(str)
                    } else if (esStatTexto(statID)) {
                        stats.addStatTexto(statID, str, true)
                    } else if (esEfectoHechizo(statID)) { // no da efectos de daño
                    } else {
                        val valor = splitStats[1].toInt(16)
                        if (encontrado) {
                            stats.addStatID(statID, valor)
                        } else {
                            stats.addStatID(statID, (valor * AtlantaMain.RATE_RANDOM_ITEM).toInt())
                        }
                    }
                } catch (ignored: Exception) {
                }
            }
        }
    }

    init {
        _bonus[1] = Stats()
        for (s in objetos.split(",".toRegex()).toTypedArray()) {
            try {
                val idMod = s.trim { it <= ' ' }.toInt()
                val objMod = getObjetoModelo(idMod)
                if (objMod != null) {
                    objMod.setID = iD
                    objetosModelos.add(objMod)
                }
            } catch (e: Exception) {
                redactarLogServidor(
                    "El objeto modelo " + s
                            + " no existe y no se le puede asignar a un objeto set"
                )
            }
        }
    }
}