package variables.mapa

import estaticos.Constantes
import estaticos.Mundo
import variables.zotros.Prisma
import java.util.*

class Area(val id: Int, superArea: Short, val nombre: String) {
    // --Commented out by Inspection START (29-06-2019 19:15):
    val subAreas = ArrayList<SubArea>()
    private var _alineacion = Constantes.ALINEACION_NEUTRAL
    var superArea: SuperArea?
    private var _prisma: Prisma? = null
    var prisma: Prisma?
        get() = _prisma
        set(prisma) {
            _prisma = prisma
            alineacion = prisma?.Alineacion ?: Constantes.ALINEACION_NEUTRAL
        }

    var alineacion: Byte
        get() {
            if (id == 7) {
                return Constantes.ALINEACION_BONTARIANO
            }
            return if (id == 11) {
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

    fun addSubArea(sa: SubArea) {
        subAreas.add(sa)
    }

    //	public ArrayList<Mapa> getMapas() {
//		final ArrayList<Mapa> mapas = new ArrayList<>();
//		for (final SubArea SA : _subAreas) {
//			mapas.addAll(SA.getMapas());
//		}
//		return mapas;
//	}
// --Commented out by Inspection STOP (29-06-2019 19:15)
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
        this.superArea = Mundo.getSuperArea(superArea.toInt())
        if (this.superArea == null) {
            this.superArea = SuperArea(superArea.toInt())
            Mundo.addSuperArea(this.superArea!!)
        }
    }
}