package variables.stats

import estaticos.Constantes
import variables.objeto.Objeto
import variables.personaje.Personaje
import java.util.*

class Stats {
    private val _statsIDs: MutableMap<Int, Int>? = TreeMap()
    var statHechizos: ArrayList<String>? = null
        private set
    var statRepetidos: ArrayList<String>? = null
        private set
    private var _statsTextos: MutableMap<Int, String>? = null

    constructor()
    constructor(stats: Stats) {
        _statsIDs!!.putAll(stats._statsIDs!!)
    }

    constructor(stats: Map<Int, Int>?) {
        _statsIDs!!.putAll(stats!!)
    }

    fun nuevosStatsBase(stats: Map<Int, Int>?, perso: Personaje?) {
        _statsIDs!!.clear()
        if (stats != null) {
            _statsIDs.putAll(stats)
        }
        // _statsIDs.put(Constantes.STAT_MAS_PA, perso.getNivel() < 100 ? Elbustemu.INICIO_PA :
// Elbustemu.INICIO_PA + 1);// PA
// _statsIDs.put(Constantes.STAT_MAS_PM, Elbustemu.INICIO_PM);// PM
// _statsIDs.put(Constantes.STAT_MAS_PROSPECCION, perso.getClase(false) ==
// Constantes.CLASE_ANUTROF
// ? Elbustemu.INICIO_PROSPECCION + 20
// : Elbustemu.INICIO_PROSPECCION);// prospeccion
// _statsIDs.put(Constantes.STAT_MAS_PODS, Elbustemu.INICIO_PODS);// pods
// _statsIDs.put(Constantes.STAT_MAS_CRIATURAS_INVO, 1);// invocaciones
        if (perso != null) {
            if (perso.clase != null) {
                for ((key, value) in perso.clase.stats) {
                    addStatID(key, value)
                }
            }
            if (perso.nivel >= 100) {
                addStatID(Constantes.STAT_MAS_PA, 1)
            }
        }
    }

    // private Map<Integer, Integer> getStatsComoMap() {
// return _statsIDs;
// }
    val entrySet: Set<Map.Entry<Int, Int>>
        get() = _statsIDs!!.entries

    fun nuevosStats(stats: Map<Int, Int>?) {
        _statsIDs!!.clear()
        if (stats != null) {
            _statsIDs.putAll(stats)
        }
    }

    fun nuevosStats(stats: Stats?) {
        _statsIDs!!.clear()
        if (stats != null) {
            _statsIDs.putAll(stats._statsIDs!!)
        }
    }

    fun clear() {
        _statsIDs!!.clear()
        statHechizos = null
        statRepetidos = null
        _statsTextos = null
    }

    fun acumularStats(stats: Stats?) {
        if (stats == null) {
            return
        }
        for ((key, value) in stats._statsIDs!!) {
            addStatID(key, value)
        }
        if (stats.statHechizos != null) {
            for (s in stats.statHechizos!!) {
                addStatHechizo(s)
            }
        }
        if (stats.statRepetidos != null) {
            for (s in stats.statRepetidos!!) {
                addStatRepetido(s)
            }
        }
        if (stats._statsTextos != null) {
            for ((key, value) in stats._statsTextos!!) {
                addStatTexto(key, value, false)
            }
        }
    }

    fun acumularStats(stats: Map<Int, Int>?) {
        if (stats == null || stats.isEmpty()) {
            return
        }
        for ((key, value) in stats) {
            addStatID(key, value)
        }
    }

    fun addStatID(statID: Int, valor: Int) {
        var valor = valor
        if (_statsIDs!![statID] != null) {
            valor += _statsIDs[statID]!!
        }
        fijarStatID(statID, valor)
    }

    fun fijarStatID(statID: Int, valor: Int) {
        var statID = statID
        var valor = valor
        val statOpuesto = Constantes.getStatOpuesto(statID)
        if (statOpuesto != statID && valor < 0) {
            fijarStatID(statOpuesto, -valor)
            // le manda al statOpuesto
            return
        }
        var restar = 0
        _statsIDs!!.remove(statID)
        if (statOpuesto != statID && _statsIDs[statOpuesto] != null) {
            restar = _statsIDs[statOpuesto]!!
            valor -= restar
            _statsIDs.remove(statOpuesto)
            if (valor < 0) {
                statID = statOpuesto
                valor = -valor
            }
        }
        if (valor > 0) {
            _statsIDs[statID] = valor
        }
    }

    fun tieneStatID(statID: Int): Boolean {
        return _statsIDs!![statID] != null
    }

    fun addStatHechizo(str: String) {
        if (statHechizos == null) {
            statHechizos = ArrayList()
        }
        statHechizos!!.add(str)
    }

    fun addStatRepetido(str: String) {
        if (statRepetidos == null) {
            statRepetidos = ArrayList()
        }
        statRepetidos!!.add(str)
    }

    fun addStatTexto(statID: Int, str: String, completo: Boolean) {
        if (_statsTextos == null) {
            _statsTextos = TreeMap()
        }
        if (str.isEmpty()) {
            _statsTextos!!.remove(statID)
        } else {
            _statsTextos!![statID] = if (completo) str.split("#".toRegex(), 2).toTypedArray()[1] else str
        }
    }

    fun tieneStatTexto(statID: Int): Boolean {
        return if (_statsTextos == null) {
            false
        } else _statsTextos!![statID] != null
    }

    fun getStatTexto(stat: Int): String? {
        return if (_statsTextos == null || _statsTextos!![stat] == null) {
            ""
        } else _statsTextos!![stat]
    }

    fun getParamStatTexto(stat: Int, parametro: Int): String {
        try {
            val s = getStatTexto(stat)
            if (s!!.isNotEmpty()) {
                if (s.split("#".toRegex()).toTypedArray().size > parametro - 1) {
                    return s.split("#".toRegex()).toTypedArray()[parametro - 1]
                }
            }
        } catch (ignored: Exception) {
        }
        return ""
    }

    fun getStringStats(objeto: Objeto): String {
        val stats = StringBuilder()
        if (statHechizos != null) {
            for (hechizo in statHechizos!!) {
                if (stats.isNotEmpty()) {
                    stats.append(",")
                }
                stats.append(hechizo)
            }
        }
        if (statRepetidos != null) {
            for (str in statRepetidos!!) {
                if (stats.isNotEmpty()) {
                    stats.append(",")
                }
                stats.append(str)
            }
        }
        if (_statsTextos != null) {
            for ((key, value) in _statsTextos!!) {
                if (stats.isNotEmpty()) {
                    stats.append(",")
                }
                stats.append(Integer.toHexString(key)).append("#").append(value)
            }
        }
        for ((key, value) in _statsIDs!!) {
            if (stats.isNotEmpty()) {
                stats.append(",")
            }
            val esExo = objeto.esStatExo(key)
            stats.append(Integer.toHexString(key)).append("#").append(Integer.toHexString(value)).append("#0#")
                .append(if (esExo) "18B5B" else "0").append("#0d0+").append(value)
        }
        return stats.toString()
    }

    fun sonStatsIguales(stats: Stats): Boolean {
        if (statHechizos == null && stats.statHechizos == null) { // nada
        } else if (statHechizos == null && stats.statHechizos != null || stats.statHechizos == null || statHechizos!!.isEmpty() && stats.statHechizos!!.isNotEmpty() || statHechizos!!.isNotEmpty() && stats.statHechizos!!.isEmpty()) {
            return false
        } else if (statHechizos!!.isNotEmpty()) {
            for (entry in statHechizos!!) {
                if (!stats.statHechizos!!.contains(entry)) {
                    return false
                }
            }
            for (entry in stats.statHechizos!!) {
                if (!statHechizos!!.contains(entry)) {
                    return false
                }
            }
        }
        if (_statsTextos == null && stats._statsTextos == null) { // nada
        } else if (_statsTextos == null && stats._statsTextos != null || stats._statsTextos == null || _statsTextos!!.isEmpty() && stats._statsTextos!!.isNotEmpty() || _statsTextos!!.isNotEmpty() && stats._statsTextos!!.isEmpty()) {
            return false
        } else if (_statsTextos!!.isNotEmpty()) {
            for ((key, value) in _statsTextos!!) {
                if (stats._statsTextos!![key] == null || !stats._statsTextos!![key].equals(
                        value, ignoreCase = true
                    )
                ) {
                    return false
                }
            }
            for ((key, value) in stats._statsTextos!!) {
                if (_statsTextos!![key] == null || !_statsTextos!![key].equals(value, ignoreCase = true)) {
                    return false
                }
            }
        }
        if (statRepetidos == null && stats.statRepetidos == null) { // nada
        } else if (statRepetidos == null && stats.statRepetidos != null || stats.statRepetidos == null || statRepetidos!!.isEmpty() && stats.statRepetidos!!.isNotEmpty() || statRepetidos!!.isNotEmpty() && stats.statRepetidos!!.isEmpty()) {
            return false
        } else if (statRepetidos!!.isNotEmpty()) {
            val repetidos = ArrayList(stats.statRepetidos)
            for (entry in statRepetidos!!) {
                if (!repetidos.contains(entry)) {
                    return false
                } else {
                    repetidos.remove(entry)
                }
            }
            repetidos.clear()
            repetidos.addAll(statRepetidos!!)
            for (entry in stats.statRepetidos!!) {
                if (!repetidos.contains(entry)) {
                    return false
                } else {
                    repetidos.remove(entry)
                }
            }
        }
        if (_statsIDs == null && stats._statsIDs != null || _statsIDs != null && stats._statsIDs == null || Objects.requireNonNull<Map<Int, Int>?>(
                _statsIDs
            )
            !!.isEmpty() && Objects.requireNonNull<Map<Int, Int>?>(stats._statsIDs)!!
                .isNotEmpty() || Objects.requireNonNull<Map<Int, Int>?>(
                stats._statsIDs
            )!!.isEmpty() && _statsIDs!!.isNotEmpty()
        ) {
            return false
        } else if (_statsIDs!!.isNotEmpty()) {
            for ((key, value) in _statsIDs) {
                if (stats._statsIDs!![key] == null || stats._statsIDs[key] != value) {
                    return false
                }
            }
            for ((key, value) in stats._statsIDs!!) {
                if (_statsIDs[key] == null || _statsIDs[key] != value) {
                    return false
                }
            }
        }
        return true
    }

    fun convertirStatsAString(): String {
        val str = StringBuilder()
        for ((key, value) in _statsIDs!!) {
            if (str.isNotEmpty()) {
                str.append(",")
            }
            str.append(Integer.toHexString(key)).append("#").append(Integer.toHexString(value)).append("#0#0")
        }
        return str.toString()
    }

    fun getStatParaMostrar(statID: Int): Int {
        var valor = 0
        if (_statsIDs!![statID] != null) {
            valor = _statsIDs[statID]!!
        }
        when (statID) {
            Constantes.STAT_MAS_DAÑOS_DE_AGUA -> if (_statsIDs[Constantes.STAT_MENOS_DAÑOS_DE_AGUA] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_DAÑOS_DE_AGUA]!!
            }
            Constantes.STAT_MAS_DAÑOS_DE_AIRE -> if (_statsIDs[Constantes.STAT_MENOS_DAÑOS_DE_AIRE] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_DAÑOS_DE_AIRE]!!
            }
            Constantes.STAT_MAS_DAÑOS_DE_FUEGO -> if (_statsIDs[Constantes.STAT_MENOS_DAÑOS_DE_FUEGO] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_DAÑOS_DE_FUEGO]!!
            }
            Constantes.STAT_MAS_DAÑOS_DE_TIERRA -> if (_statsIDs[Constantes.STAT_MENOS_DAÑOS_DE_TIERRA] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_DAÑOS_DE_TIERRA]!!
            }
            Constantes.STAT_MAS_DAÑOS_DE_NEUTRAL -> if (_statsIDs[Constantes.STAT_MENOS_DAÑOS_DE_NEUTRAL] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_DAÑOS_DE_NEUTRAL]!!
            }
            Constantes.STAT_MAS_DAÑOS_EMPUJE -> if (_statsIDs[Constantes.STAT_MENOS_DAÑOS_EMPUJE] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_DAÑOS_EMPUJE]!!
            }
            Constantes.STAT_MAS_REDUCCION_CRITICOS -> if (_statsIDs[Constantes.STAT_MENOS_REDUCCION_CRITICOS] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_REDUCCION_CRITICOS]!!
            }
            Constantes.STAT_MAS_DAÑOS_CRITICOS -> if (_statsIDs[Constantes.STAT_MENOS_DAÑOS_CRITICOS] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_DAÑOS_CRITICOS]!!
            }
            Constantes.STAT_MAS_GOLPES_CRITICOS -> if (_statsIDs[Constantes.STAT_MENOS_GOLPES_CRITICOS] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_GOLPES_CRITICOS]!!
            }
            Constantes.STAT_MAS_REDUCCION_EMPUJE -> if (_statsIDs[Constantes.STAT_MENOS_REDUCCION_EMPUJE] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_REDUCCION_EMPUJE]!!
            }
            Constantes.STAT_MAS_RETIRO_PA -> if (_statsIDs[Constantes.STAT_MENOS_RETIRO_PA] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_RETIRO_PA]!!
            }
            Constantes.STAT_MAS_RETIRO_PM -> if (_statsIDs[Constantes.STAT_MENOS_RETIRO_PM] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_RETIRO_PM]!!
            }
            Constantes.STAT_MAS_HUIDA -> if (_statsIDs[Constantes.STAT_MENOS_HUIDA] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_HUIDA]!!
            }
            Constantes.STAT_MAS_PLACAJE -> if (_statsIDs[Constantes.STAT_MENOS_PLACAJE] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_PLACAJE]!!
            }
            Constantes.STAT_MAS_ESQUIVA_PERD_PA -> if (_statsIDs[Constantes.STAT_MENOS_ESQUIVA_PERD_PA] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_ESQUIVA_PERD_PA]!!
            }
            Constantes.STAT_MAS_ESQUIVA_PERD_PM -> if (_statsIDs[Constantes.STAT_MENOS_ESQUIVA_PERD_PM] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_ESQUIVA_PERD_PM]!!
            }
            Constantes.STAT_MAS_INICIATIVA -> if (_statsIDs[Constantes.STAT_MENOS_INICIATIVA] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_INICIATIVA]!!
            }
            Constantes.STAT_MAS_AGILIDAD -> {
                if (_statsIDs[154] != null) {
                    valor -= _statsIDs[154]!!
                }
                if (_statsIDs[609] != null) {
                    valor += _statsIDs[609]!!
                }
            }
            Constantes.STAT_MAS_FUERZA -> {
                if (_statsIDs[157] != null) {
                    valor -= _statsIDs[157]!!
                }
                if (_statsIDs[607] != null) {
                    valor += _statsIDs[607]!!
                }
            }
            Constantes.STAT_MAS_SUERTE -> {
                if (_statsIDs[152] != null) {
                    valor -= _statsIDs[152]!!
                }
                if (_statsIDs[608] != null) {
                    valor += _statsIDs[608]!!
                }
            }
            Constantes.STAT_MAS_SABIDURIA -> {
                if (_statsIDs[606] != null) {
                    valor += _statsIDs[606]!!
                }
                if (_statsIDs[156] != null) {
                    valor -= _statsIDs[156]!!
                }
            }
            Constantes.STAT_MAS_VITALIDAD -> {
                if (_statsIDs[110] != null) {
                    valor += _statsIDs[110]!!
                }
                if (_statsIDs[153] != null) {
                    valor -= _statsIDs[153]!!
                }
                if (_statsIDs[424] != null) {
                    valor -= _statsIDs[424]!!
                }
                if (_statsIDs[610] != null) {
                    valor += _statsIDs[610]!!
                }
            }
            Constantes.STAT_MAS_INTELIGENCIA -> {
                if (_statsIDs[155] != null) {
                    valor -= _statsIDs[155]!!
                }
                if (_statsIDs[611] != null) {
                    valor += _statsIDs[611]!!
                }
            }
            Constantes.STAT_MAS_PA -> {
                if (_statsIDs[120] != null) {
                    valor += _statsIDs[120]!!
                }
                if (_statsIDs[101] != null) {
                    valor -= _statsIDs[101]!!
                }
                if (_statsIDs[168] != null) {
                    valor -= _statsIDs[168]!!
                }
            }
            Constantes.STAT_MAS_PM -> {
                if (_statsIDs[78] != null) {
                    valor += _statsIDs[78]!!
                }
                if (_statsIDs[127] != null) {
                    valor -= _statsIDs[127]!!
                }
                if (_statsIDs[169] != null) {
                    valor -= _statsIDs[169]!!
                }
            }
            Constantes.STAT_MAS_ALCANCE -> if (_statsIDs[116] != null) {
                valor -= _statsIDs[116]!!
            }
            Constantes.STAT_MAS_DAÑOS -> {
                if (_statsIDs[121] != null) {
                    valor += _statsIDs[121]!!
                }
                if (_statsIDs[145] != null) {
                    valor -= _statsIDs[145]!!
                }
                if (_statsIDs[144] != null) {
                    valor -= _statsIDs[144]!!
                }
            }
            Constantes.STAT_MAS_PORC_DAÑOS -> if (_statsIDs[186] != null) {
                valor -= _statsIDs[186]!!
            }
            Constantes.STAT_MAS_PODS -> if (_statsIDs[159] != null) {
                valor -= _statsIDs[159]!!
            }
            Constantes.STAT_MAS_PROSPECCION -> if (_statsIDs[177] != null) {
                valor -= _statsIDs[177]!!
            }
            Constantes.STAT_MAS_CURAS -> if (_statsIDs[179] != null) {
                valor -= _statsIDs[179]!!
            }
            Constantes.STAT_MAS_RES_PORC_TIERRA -> if (_statsIDs[Constantes.STAT_MENOS_RES_PORC_TIERRA] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_RES_PORC_TIERRA]!!
            }
            Constantes.STAT_MAS_RES_PORC_AGUA -> if (_statsIDs[Constantes.STAT_MENOS_RES_PORC_AGUA] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_RES_PORC_AGUA]!!
            }
            Constantes.STAT_MAS_RES_PORC_AIRE -> if (_statsIDs[Constantes.STAT_MENOS_RES_PORC_AIRE] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_RES_PORC_AIRE]!!
            }
            Constantes.STAT_MAS_RES_PORC_FUEGO -> if (_statsIDs[Constantes.STAT_MENOS_RES_PORC_FUEGO] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_RES_PORC_FUEGO]!!
            }
            Constantes.STAT_MAS_RES_PORC_NEUTRAL -> if (_statsIDs[Constantes.STAT_MENOS_RES_PORC_NEUTRAL] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_RES_PORC_NEUTRAL]!!
            }
            Constantes.STAT_MAS_RES_FIJA_TIERRA -> if (_statsIDs[Constantes.STAT_MENOS_RES_FIJA_TIERRA] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_RES_FIJA_TIERRA]!!
            }
            Constantes.STAT_MAS_RES_FIJA_AGUA -> if (_statsIDs[Constantes.STAT_MENOS_RES_FIJA_AGUA] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_RES_FIJA_AGUA]!!
            }
            Constantes.STAT_MAS_RES_FIJA_AIRE -> if (_statsIDs[Constantes.STAT_MENOS_RES_FIJA_AIRE] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_RES_FIJA_AIRE]!!
            }
            Constantes.STAT_MAS_RES_FIJA_FUEGO -> if (_statsIDs[Constantes.STAT_MENOS_RES_FIJA_FUEGO] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_RES_FIJA_FUEGO]!!
            }
            Constantes.STAT_MAS_RES_FIJA_NEUTRAL -> if (_statsIDs[Constantes.STAT_MENOS_RES_FIJA_NEUTRAL] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_RES_FIJA_NEUTRAL]!!
            }
            Constantes.STAT_MAS_RES_PORC_PVP_TIERRA -> if (_statsIDs[Constantes.STAT_MENOS_RES_PORC_PVP_TIERRA] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_RES_PORC_PVP_TIERRA]!!
            }
            Constantes.STAT_MAS_RES_PORC_PVP_AGUA -> if (_statsIDs[Constantes.STAT_MENOS_RES_PORC_PVP_AGUA] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_RES_PORC_PVP_AGUA]!!
            }
            Constantes.STAT_MAS_RES_PORC_PVP_AIRE -> if (_statsIDs[Constantes.STAT_MENOS_RES_PORC_PVP_AIRE] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_RES_PORC_PVP_AIRE]!!
            }
            Constantes.STAT_MAS_RES_PORC_PVP_FUEGO -> if (_statsIDs[Constantes.STAT_MENOS_RES_PORC_PVP_FUEGO] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_RES_PORC_PVP_FUEGO]!!
            }
            Constantes.STAT_MAS_RES_PORC_PVP_NEUTRAL -> if (_statsIDs[Constantes.STAT_MENOS_RES_PORC_PVP_NEUTRAL] != null) {
                valor -= _statsIDs[Constantes.STAT_MENOS_RES_PORC_PVP_NEUTRAL]!!
            }
            Constantes.STAT_AURA -> {
            }
        }
        return valor
    }
}

private fun <V> putAll(from: Map<V, V?>) {

}
