package variables.objeto

import estaticos.Mundo.Duo
import estaticos.Mundo.getObjetoModelo
import java.util.*
import java.util.regex.Pattern

class MascotaModelo(
    val iD: Int, val maxStats: Int, statsPorEfecto: String, comidas: String,
    devorador: Int, val fantasma: Int
) {
    private val _statsPorEfecto = ArrayList<Duo<Int, Int>>()
    private val _comidas = ArrayList<Comida>()
    private val _esDevorador: Boolean
    var strComidas = ""

    fun getComida(idModComida: Int): Comida? {
        for (comi in _comidas) {
            if (comi.iDComida < 0) {
                if (Math.abs(comi.iDComida) == getObjetoModelo(idModComida)!!.tipo.toInt()) {
                    return comi
                }
            } else {
                if (comi.iDComida == idModComida) {
                    return comi
                }
            }
        }
        return null
    }

    val comidaPrimera: Comida?
        get() {
            for (comi in _comidas) {
                return comi
            }
            return null
        }

    fun esDevoradorAlmas(): Boolean {
        return _esDevorador
    }

    fun getStatsPorEfecto(stat: Int): Int {
        for (duo in _statsPorEfecto) {
            if (duo._primero == stat) {
                return duo._segundo
            }
        }
        return 0
    }

    class Comida(val iDComida: Int, val cantidad: Int, val iDStat: Int)

    init {
        if (!comidas.isEmpty()) {
            for (comida in comidas.split(Pattern.quote("|").toRegex()).toTypedArray()) {
                try {
                    val str = comida.split(";".toRegex()).toTypedArray()
                    _comidas.add(Comida(str[0].toInt(), str[1].toInt(), str[2].toInt()))
                } catch (ignored: Exception) {
                }
            }
        }
        strComidas = "comidas: $comidas statsPorEfecto: $statsPorEfecto maxStats: $maxStats"
        val stats =
            statsPorEfecto.split(Pattern.quote("|").toRegex()).toTypedArray()
        for (s in stats) {
            try {
                _statsPorEfecto.add(
                    Duo(
                        s.split(";".toRegex()).toTypedArray()[0].toInt(),
                        s.split(";".toRegex()).toTypedArray()[1].toInt()
                    )
                )
            } catch (ignored: Exception) {
            }
        }
        _esDevorador = devorador == 1
    }
}