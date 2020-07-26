package variables.mapa

import estaticos.Constantes
import estaticos.Mundo
import variables.zotros.Prisma
import java.util.*

class SubArea(
    val id: Int, areaID: Short, val nombre: String, conquistable: Boolean,
    minNivelGrupoMob: Int, maxNivelGrupoMob: Int, cementerio: String
) {
    val mapas = ArrayList<Mapa>()
    private val _conquistable: Boolean
    val area: Area = Mundo.getArea(areaID.toInt())!!
    private var _alineacion = Constantes.ALINEACION_NEUTRAL
    private var _prisma: Prisma? = null
    var minNivelGrupoMob = 0
    var maxNivelGrupoMob = 0
    var cementerio = ""

    var prisma: Prisma?
        get() = _prisma
        set(prisma) {
            _prisma = prisma
            alineacion = prisma?.Alineacion ?: Constantes.ALINEACION_NEUTRAL
        }

    fun esConquistable(): Boolean {
        return !_conquistable
    }

    var alineacion: Byte
        get() {
            if (area.id == 7) {
                return Constantes.ALINEACION_BONTARIANO
            }
            return if (area.id == 11) {
                Constantes.ALINEACION_BRAKMARIANO
            } else _alineacion
        }
        private set(alin) {
            if (_alineacion == alin) {
                return
            }
            if (_alineacion == Constantes.ALINEACION_BONTARIANO) {
                BONTAS--
            }
            if (_alineacion == Constantes.ALINEACION_BRAKMARIANO) {
                BRAKMARS--
            }
            if (alin == Constantes.ALINEACION_BONTARIANO) {
                BONTAS++
            }
            if (alin == Constantes.ALINEACION_BRAKMARIANO) {
                BRAKMARS++
            }
            _alineacion = alin
        }

    fun addMapa(mapa: Mapa) {
        mapas.add(mapa)
    }

    companion object {
        @JvmField
        var BONTAS = 0

        @JvmField
        var BRAKMARS = 0

        fun subareasBontas(): Int {
            return BONTAS
        }

        fun subareasBrakmars(): Int {
            return BRAKMARS
        }
    }

    init {
        _conquistable = conquistable
        this.minNivelGrupoMob = minNivelGrupoMob
        this.maxNivelGrupoMob = maxNivelGrupoMob
        this.cementerio = cementerio
    }
}