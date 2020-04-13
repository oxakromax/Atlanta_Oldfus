package variables.personaje

import estaticos.Mundo.getDonStat
import variables.stats.Stats
import java.util.*
import java.util.regex.Pattern

class Especialidad(val iD: Int, val orden: Int, val nivel: Int, donesString: String) {
    val dones = ArrayList<Don>()

    inner class Don(val iD: Int, val nivel: Int, statID: Int, valor: Int) {
        val stat = Stats()

        init {
            if (statID > 0 && valor > 0) {
                stat.addStatID(statID, valor)
            }
        }
    }

    init {
        for (s in donesString.split(Pattern.quote("|").toRegex()).toTypedArray()) {
            if (s.isEmpty()) {
                continue
            }
            try {
                val args = s.split(",".toRegex()).toTypedArray()
                val donID = args[0].toInt()
                val donNivel = args[1].toInt()
                val donStat = getDonStat(donID)
                var valor = 0
                if (args.size > 2) {
                    valor = args[2].toInt()
                }
                dones.add(Don(donID, donNivel, donStat, valor))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}