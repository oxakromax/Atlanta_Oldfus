package variables.objeto

import java.util.*

class CreaTuItem(val iD: Int, _statsMaximos: String, _maxOgrinas: Int, precioBase: Int) {
    private val _statsMaximos: MutableMap<Int, Int> = TreeMap()
    val maxOgrinas: Int
    val precioBase: Int

    fun getMaximoStat(stat: Int): Int {
        return if (_statsMaximos[stat] == null) {
            0
        } else _statsMaximos[stat]!!
    }

    val maximosStats: String
        get() {
            val s = StringBuilder()
            for ((key, value) in _statsMaximos) {
                if (s.length > 0) {
                    s.append(",")
                }
                s.append(key).append("*").append(value)
            }
            return s.toString()
        }

    companion object {
        val PRECIOS: MutableMap<Int, Float> = TreeMap()
    }

    init {
        for (s in _statsMaximos.split("\\|".toRegex()).toTypedArray()) {
            try {
                this._statsMaximos[s.split(",".toRegex()).toTypedArray()[0].toInt()] =
                    s.split(",".toRegex()).toTypedArray()[1].toInt()
            } catch (ignored: Exception) {
            }
        }
        maxOgrinas = _maxOgrinas
        this.precioBase = precioBase
    }
}