package variables.npc

import java.util.*

class ObjetoTrueque(val iD: Int, necesita: String, prioridad: Int, npcs: String) :
    Comparable<ObjetoTrueque> {
    private val _necesita: MutableMap<Int, Int> = TreeMap()
    val prioridad: Int
    private var _npcs: ArrayList<Int>? = null
    val necesita: Map<Int, Int>
        get() = _necesita

    fun permiteNPC(id: Int): Boolean {
        return _npcs == null || _npcs!!.isEmpty() || _npcs!!.contains(id)
    }

    override fun compareTo(obj: ObjetoTrueque): Int {
        val otro = obj.prioridad.toLong()
        if (otro > prioridad) {
            return 1
        }
        return if (otro == prioridad.toLong()) {
            0
        } else -1
    }

    init {
        for (s in necesita.split(";".toRegex()).toTypedArray()) {
            try {
                _necesita[s.split(",".toRegex()).toTypedArray()[0].toInt()] =
                    s.split(",".toRegex()).toTypedArray()[1].toInt()
            } catch (ignored: Exception) {
            }
        }
        this.prioridad = prioridad
        if (!npcs.isEmpty()) {
            for (s in npcs.split(",".toRegex()).toTypedArray()) {
                if (s.isEmpty()) {
                    continue
                }
                try {
                    if (_npcs == null) {
                        _npcs = ArrayList()
                    }
                    _npcs!!.add(s.toInt())
                } catch (ignored: Exception) {
                }
            }
        }
    }
}