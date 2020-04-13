package variables.mob

import estaticos.AtlantaMain
import estaticos.Constantes
import estaticos.Formulas.getRandomInt
import estaticos.GestorSQL.UPDATE_STATS_MOB
import estaticos.GestorSQL.UPDATE_STATS_PUNTOS_PDV_XP_MOB
import estaticos.Mundo
import variables.hechizo.Hechizo
import variables.pelea.DropMob
import java.util.*
import java.util.regex.Pattern

class MobModelo(
    val id: Int, val nombre: String, val gfxID: Short, val alineacion: Byte, var colores: String,
    grados: String, hechizos: String, stats: String, pdvs: String, puntos: String,
    strIniciativa: String, mK: String, MK: String, exps: String, var tipoIA: Byte,
    private val _esCapturable: Boolean, var talla: Short, var distAgresion: Byte, val tipoMob: Byte,
    private val _esKickeable: Boolean
) {
    private val _subAreasAparecer = ArrayList<Int>()
    private val _grados: MutableMap<Byte, MobGradoModelo> = TreeMap()
    val drops = ArrayList<DropMob>()
    var archiMob: MobModelo? = null
    var probabilidadAparecer = -1
        private set

    fun setDataExtra(probabilidad: Int, subareas: String) {
        probabilidadAparecer = probabilidad
        for (s in subareas.split(",".toRegex()).toTypedArray()) {
            if (s.isEmpty()) {
                continue
            }
            try {
                _subAreasAparecer.add(s.toInt())
            } catch (ignored: Exception) {
            }
        }
    }

    fun puedeSubArea(subarea: Int): Boolean {
        return _subAreasAparecer.isEmpty() || _subAreasAparecer.contains(subarea)
    }

    fun modificarStats(grado: Byte, packet: String): Boolean {
        try {
            val mob = _grados[grado] ?: return false
            val split = packet.split(Pattern.quote("|").toRegex()).toTypedArray()
            val stats = split[0].split(",".toRegex()).toTypedArray()
            for (stat in stats) {
                try {
                    val a = stat.split(":".toRegex()).toTypedArray()
                    mob.stats.fijarStatID(a[0].toInt(), a[1].toInt())
                } catch (ignored: Exception) {
                }
            }
            mob.Set_StatsBase(split[0])
            mob.pDVMAX = split[1].toInt()
            mob.baseXp = split[2].toInt()
            mob.minKamas = split[3].toInt()
            mob.maxKamas = split[4].toInt()
            val s = strStatsTodosMobs().split("~".toRegex()).toTypedArray()
            UPDATE_STATS_PUNTOS_PDV_XP_MOB(id, s[0], s[1], s[2], split[3], split[4])
            return true
        } catch (ignored: Exception) {
        }
        return false
    }

    fun strStatsTodosMobs(): String { // se usa mas q todo para el panel mobs, pero minkamas y maxkamas son int para sql
        val strStats = StringBuilder()
        val strPDV = StringBuilder()
        val strExp = StringBuilder()
        val strMinKamas = StringBuilder()
        val strMaxKamas = StringBuilder()
        var e = false
        for (i in 1..11) {
            val mob = _grados[i.toByte()] ?: break
            if (e) {
                strStats.append("|")
                strPDV.append("|")
                strExp.append("|")
                strMinKamas.append("|")
                strMaxKamas.append("|")
            }
            strStats.append(mob.stringStatsActualizado())
            strPDV.append(mob.pDVMAX)
            strExp.append(mob.baseXp)
            strMinKamas.append(mob.minKamas)
            strMaxKamas.append(mob.maxKamas)
            e = true
        }
        return (strStats.toString() + "~" + strPDV.toString() + "~" + strExp.toString() + "~" + strMinKamas.toString() + "~"
                + strMaxKamas.toString())
    }

    // public String testDaño(byte grado, String s) {
// MobGradoModelo mg = getGradoPorGrado(grado);
// int[] stats = getStatsParaCalculo(grado, s);
// if (stats == null) {
// return "MOB NO EXISTE";
// }
// StringBuilder str = new StringBuilder("");
// for (StatHechizo sh : mg.getHechizos().values()) {
// if (str.length() > 0) {
// str.append("|");
// }
// str.append(Hechizo.strDañosStats2(sh, stats));
// }
// return str.toString();
// }
    fun calculoDaño(grado: Byte, s: String): String {
        val mg = getGradoPorGrado(grado)
        val stats = getStatsParaCalculo(grado, s) ?: return ""
        val str = StringBuilder()
        // str.append("\nCalculo de daño del mob " + _nombre + ":");
        var paso = false
        if (mg!!.hechizos != null) {
            for (sh in mg.hechizos.values) {
                if (paso) {
                    str.append("|")
                }
                paso = true
                str.append(sh.let { Hechizo.strDañosStats(it, stats) })
            }
        }
        return str.toString()
    }

    private fun getStatsParaCalculo(grado: Byte, s: String): IntArray? {
        val mg = getGradoPorGrado(grado) ?: return null
        val stats = IntArray(5)
        try {
            for (s2 in s.split(";".toRegex()).toTypedArray()) {
                val statID = s2.split(",".toRegex()).toTypedArray()[0].toInt()
                val valor = s2.split(",".toRegex()).toTypedArray()[1].toInt()
                when (statID) {
                    Constantes.STAT_MAS_FUERZA -> {
                        stats[1] = valor
                        stats[0] = stats[1]
                    }
                    Constantes.STAT_MAS_INTELIGENCIA -> stats[2] = valor
                    Constantes.STAT_MAS_SUERTE -> stats[3] = valor
                    Constantes.STAT_MAS_AGILIDAD -> stats[4] = valor
                }
            }
        } catch (e: Exception) {
            stats[0] = mg.stats.getStatParaMostrar(Constantes.STAT_MAS_FUERZA)
            stats[1] = mg.stats.getStatParaMostrar(Constantes.STAT_MAS_FUERZA)
            stats[2] = mg.stats.getStatParaMostrar(Constantes.STAT_MAS_INTELIGENCIA)
            stats[3] = mg.stats.getStatParaMostrar(Constantes.STAT_MAS_SUERTE)
            stats[4] = mg.stats.getStatParaMostrar(Constantes.STAT_MAS_AGILIDAD)
        }
        return stats
    }

    fun detalleMob(): String {
        val str = StringBuilder()
        str.append(tipoMob.toInt()).append("|")
        var str2 = StringBuilder()
        for (drop in drops) {
            if (str2.isNotEmpty()) {
                str2.append(";")
            }
            str2.append(drop.IDObjModelo).append(",").append(drop.prospeccion).append("#")
                .append(drop.porcentaje * 1000).append("#").append(drop.maximo)
        }
        str.append(str2.toString()).append("|")
        str2 = StringBuilder()
        for (mob in _grados.values) {
            if (str2.isNotEmpty()) {
                str2.append("|")
            }
            str2.append(mob.pDVMAX).append("~").append(mob.pA).append("~").append(mob.pM).append("~")
                .append(mob.resistencias).append("~").append(
                    mob
                        .spells!!.replace(";", ",")
                ).append("~").append(mob.baseXp)
            str2.append("~").append(mob.minKamas).append(" - ").append(mob.maxKamas)
        }
        str.append(str2.toString())
        return str.toString()
    }

    fun listaNiveles(): String {
        val str = StringBuilder()
        for (mob in _grados.values) {
            if (str.isNotEmpty()) {
                str.append(", ")
            }
            str.append(mob.nivel.toInt())
        }
        return str.toString()
    }

    fun esKickeable(): Boolean {
        return _esKickeable
    }

    fun addDrop(drop: DropMob) {
        borrarDrop(drop.IDObjModelo)
        Mundo.getObjetoModelo(drop.IDObjModelo)?.addMobQueDropea(id)
        drops.add(drop)
        drops.trimToSize()
    }

    fun borrarDrop(id: Int) {
        var remove: DropMob? = null
        for (d in drops) {
            if (d.IDObjModelo == id) {
                remove = d
                break
            }
        }
        if (remove != null) {
            Mundo.getObjetoModelo(id)?.delMobQueDropea(this.id)
            drops.remove(remove)
        }
    }

    val grados: Map<Byte, MobGradoModelo>
        get() = _grados

    fun Aleatorizarstats() {
        if (AtlantaMain.RATE_RANDOM_MOB != 1.0) {
            for (a in _grados.values) {
                a.stats_aleatorias()
            }
        }
    }

    fun NormalizarStats() {
        if (AtlantaMain.RATE_RANDOM_MOB != 1.0) {
            for (a in _grados.values) {
                a.reiniciar_stats()
            }
        }
    }

    fun getGradoPorNivel(nivel: Int): MobGradoModelo? {
        for (grado in _grados.values) {
            if (grado.nivel.toInt() == nivel) {
                return grado
            }
        }
        return null
    }

    fun getGradoPorGrado(pos: Byte): MobGradoModelo? {
        return _grados[pos]
    }

    val randomGrado: MobGradoModelo?
        get() = _grados[getRandomInt(1, _grados.size).toByte()]

    fun esCapturable(): Boolean {
        return _esCapturable
    }

    enum class TipoGrupo {
        FIJO, NORMAL, SOLO_UNA_PELEA, HASTA_QUE_MUERA
    }

    init {
        val aGrados = grados.split(Pattern.quote("|").toRegex()).toTypedArray()
        val aStats = stats.split(Pattern.quote("|").toRegex()).toTypedArray()
        val aHechizos = hechizos.split(Pattern.quote("|").toRegex()).toTypedArray()
        val aPuntos = puntos.split(Pattern.quote("|").toRegex()).toTypedArray()
        val aExp = exps.split(Pattern.quote("|").toRegex()).toTypedArray()
        val aPDV = pdvs.split(Pattern.quote("|").toRegex()).toTypedArray()
        val aIniciativa = strIniciativa.split(Pattern.quote("|").toRegex()).toTypedArray()
        val aMinKamas = mK.split(Pattern.quote("|").toRegex()).toTypedArray()
        val aMaxKamas = MK.split(Pattern.quote("|").toRegex()).toTypedArray()
        var grado: Byte = 1
        var PA: Byte = 6
        var PM: Byte = 3
        var tempPDV = 1
        var tempIniciativa = 0
        var tempExp = 0
        var tempMinKamas = 0
        var tempMaxKamas = 0
        var tempHechizo = ""
        var tempResistNivel = ""
        var tempStats = ""
        for (n in aGrados.indices) {
            tempResistNivel = try {
                aGrados[n].split("@".toRegex()).toTypedArray()[1]
            } catch (e: Exception) {
                continue
            }
            if (tempResistNivel.isEmpty()) {
                continue
            }
            try {
                tempExp = aExp[n].toInt()
            } catch (ignored: Exception) {
            }
            try {
                tempStats = aStats[n]
            } catch (ignored: Exception) {
            }
            try {
                tempHechizo = aHechizos[n]
            } catch (ignored: Exception) {
            }
            try {
                tempPDV = aPDV[n].toInt()
            } catch (ignored: Exception) {
            }
            try {
                tempIniciativa = aIniciativa[n].toInt()
            } catch (ignored: Exception) {
            }
            try {
                PA = aPuntos[n].split(";".toRegex()).toTypedArray()[0].toByte()
            } catch (ignored: Exception) {
            }
            try {
                PM = aPuntos[n].split(";".toRegex()).toTypedArray()[1].toByte()
            } catch (ignored: Exception) {
            }
            try {
                tempMinKamas = aMinKamas[n].toInt()
            } catch (ignored: Exception) {
            }
            try {
                tempMaxKamas = aMaxKamas[n].toInt()
            } catch (ignored: Exception) {
            }
            _grados[grado] = MobGradoModelo(
                this, grado, PA.toInt(), PM.toInt(), tempResistNivel, tempStats, tempHechizo, tempPDV,
                tempIniciativa, tempExp, tempMinKamas, tempMaxKamas
            )
            grado++
        }
        if (stats.isNotEmpty() && !stats.contains(":")) {
            val strStats = StringBuilder()
            var e = false
            for (i in 1..11) {
                val mob = _grados[i.toByte()] ?: break
                if (e) {
                    strStats.append("|")
                }
                strStats.append(mob.stringStatsActualizado())
                e = true
            }
            UPDATE_STATS_MOB(id, strStats.toString())
        }
    }
}