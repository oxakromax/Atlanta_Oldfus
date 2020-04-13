package variables.montura

import java.util.*

class MonturaModelo(
    val colorID: Int,
    stats: String,
    color: String,
    val certificadoModeloID: Int,
    generacion: Byte
) {
    val generacionID: Byte
    private val _stats: MutableMap<Int?, Int> = HashMap()
    var strColor = ""

    val stats: Map<Int?, Int>
        get() = _stats

    init {
        strColor = color
        generacionID = generacion
        if (stats.isNotEmpty()) {
            for (str in stats.split(";".toRegex()).toTypedArray()) {
                try {
                    val s = str.split(",".toRegex()).toTypedArray()
                    _stats[s[0].toInt()] = s[1].toInt()
                } catch (ignored: Exception) {
                }
            }
        }
    }
}