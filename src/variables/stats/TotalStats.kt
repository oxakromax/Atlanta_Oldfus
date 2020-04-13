package variables.stats

import estaticos.AtlantaMain
import estaticos.Constantes
import kotlin.math.max
import kotlin.math.min

class TotalStats(
    val statsBase: Stats?,
    val statsObjetos: Stats?,
    val statsBuff: Stats?,
    val statsBendMald: Stats?,
    private val _tipo: Int
) {

    fun clear() {
        statsBase?.clear()
        statsBendMald?.clear()
        statsObjetos?.clear()
        statsBuff?.clear()
    }

    //	private boolean tieneStatID(int statID) {
//		boolean b = false;
//		if (_statsBase != null) {
//			b |= _statsBase.tieneStatID(statID);
//		}
//		if (_statsBendMald != null) {
//			b |= _statsBendMald.tieneStatID(statID);
//		}
//		if (_statsObjetos != null) {
//			b |= _statsObjetos.tieneStatID(statID);
//		}
//		if (_statsBuff != null) {
//			b |= _statsBuff.tieneStatID(statID);
//		}
//		return b;
//	}
// aqui se aplican los limites
    fun getTotalStatConComplemento(statID: Int): Int {
        val valores2: IntArray
        var divisor = 1
        when (statID) {
            Constantes.STAT_MAS_RES_FIJA_PVP_TIERRA -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_FIJA_TIERRA)
            Constantes.STAT_MAS_RES_FIJA_PVP_AGUA -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_FIJA_AGUA)
            Constantes.STAT_MAS_RES_FIJA_PVP_AIRE -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_FIJA_AIRE)
            Constantes.STAT_MAS_RES_FIJA_PVP_FUEGO -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_FIJA_FUEGO)
            Constantes.STAT_MAS_RES_FIJA_PVP_NEUTRAL -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_FIJA_NEUTRAL)
            Constantes.STAT_MAS_RES_PORC_PVP_TIERRA -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_PORC_TIERRA)
            Constantes.STAT_MAS_RES_PORC_PVP_AGUA -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_PORC_AGUA)
            Constantes.STAT_MAS_RES_PORC_PVP_AIRE -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_PORC_AIRE)
            Constantes.STAT_MAS_RES_PORC_PVP_FUEGO -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_PORC_FUEGO)
            Constantes.STAT_MAS_RES_PORC_PVP_NEUTRAL -> valores2 = getArrayStats(Constantes.STAT_MAS_RES_PORC_NEUTRAL)
            Constantes.STAT_MAS_ESQUIVA_PERD_PA, Constantes.STAT_MAS_ESQUIVA_PERD_PM -> {
                valores2 = getArrayStats(Constantes.STAT_MAS_SABIDURIA)
                divisor = 4
            }
            Constantes.STAT_MAS_PROSPECCION -> {
                valores2 = getArrayStats(Constantes.STAT_MAS_SUERTE)
                divisor = 10
            }
            Constantes.STAT_MAS_HUIDA, Constantes.STAT_MAS_PLACAJE -> {
                valores2 = getArrayStats(Constantes.STAT_MAS_AGILIDAD)
                divisor = 10
            }
            Constantes.STAT_AURA -> return getTotalStatParaMostrar(statID)
            else -> return getTotalStatParaMostrar(statID)
        }
        var valor = 0
        if (statsBase != null) {
            valor += statsBase.getStatParaMostrar(statID)
            valor += valores2[0] / divisor
        }
        if (statsBendMald != null) {
            valor += statsBendMald.getStatParaMostrar(statID)
            valor += valores2[1] / divisor
        }
        if (statsObjetos != null) {
            valor += statsObjetos.getStatParaMostrar(statID)
            valor += valores2[2] / divisor
        }
        var limitSin = 0
        if (_tipo == 1) {
            if (AtlantaMain.LIMITE_STATS_SIN_BUFF[statID] != null) {
                limitSin = AtlantaMain.LIMITE_STATS_SIN_BUFF[statID]!!
                valor = min(valor, limitSin)
            }
        }
        if (statsBuff != null) {
            val v = statsBuff.getStatParaMostrar(statID)
            valor += v
            valor += valores2[3] / divisor
        }
        if (_tipo == 1) {
            if (AtlantaMain.LIMITE_STATS_CON_BUFF[statID] != null) {
                val limitCon = max(limitSin, AtlantaMain.LIMITE_STATS_CON_BUFF[statID]!!)
                valor = min(valor, limitCon)
            }
        }
        return valor
    }

    private fun getArrayStats(statID: Int): IntArray {
        val valores = IntArray(5)
        var valor = 0
        if (statsBase != null) {
            valores[0] = statsBase.getStatParaMostrar(statID)
            valor += valores[0]
        }
        if (statsBendMald != null) {
            valores[1] += statsBendMald.getStatParaMostrar(statID)
            valor += valores[1]
        }
        if (statsObjetos != null) {
            valores[2] += statsObjetos.getStatParaMostrar(statID)
            valor += valores[2]
        }
        var limitSin = 0
        if (_tipo == 1) {
            if (AtlantaMain.LIMITE_STATS_SIN_BUFF[statID] != null) {
                limitSin = AtlantaMain.LIMITE_STATS_SIN_BUFF[statID]!!
                valor = min(valor, limitSin)
            }
        }
        if (statsBuff != null) {
            valores[3] += statsBuff.getStatParaMostrar(statID)
            valor += valores[3]
        }
        if (_tipo == 1) {
            if (AtlantaMain.LIMITE_STATS_CON_BUFF[statID] != null) {
                val limitCon = max(limitSin, AtlantaMain.LIMITE_STATS_CON_BUFF[statID]!!)
                valor = min(valor, limitCon)
            }
        }
        valores[4] = valor
        return valores
    }

    fun getTotalStatParaMostrar(statID: Int): Int {
        var valor = 0
        if (statsBase != null) {
            valor += statsBase.getStatParaMostrar(statID)
        }
        if (statsBendMald != null) {
            valor += statsBendMald.getStatParaMostrar(statID)
        }
        if (statsObjetos != null) {
            valor += statsObjetos.getStatParaMostrar(statID)
        }
        var limitSin = 0
        if (_tipo == 1) {
            if (AtlantaMain.LIMITE_STATS_SIN_BUFF[statID] != null) {
                limitSin = AtlantaMain.LIMITE_STATS_SIN_BUFF[statID]!!
                valor = min(valor, limitSin)
            }
        }
        if (statsBuff != null) {
            valor += statsBuff.getStatParaMostrar(statID)
        }
        if (_tipo == 1) {
            if (AtlantaMain.LIMITE_STATS_CON_BUFF[statID] != null) {
                val limitCon = max(limitSin, AtlantaMain.LIMITE_STATS_CON_BUFF[statID]!!)
                valor = min(valor, limitCon)
            }
        }
        return valor
    }

    fun clearBuffStats() {
        statsBuff?.clear()
    }

}