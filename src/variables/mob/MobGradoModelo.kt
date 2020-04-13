package variables.mob

import estaticos.AtlantaMain
import estaticos.AtlantaMain.FUN_RANDOM_MOB
import estaticos.AtlantaMain.FUN_RANDOM_MOB_RESIS
import estaticos.Constantes
import estaticos.Mundo
import variables.hechizo.StatHechizo
import variables.pelea.Luchador
import variables.stats.Stats
import java.util.*

class MobGradoModelo(
    val mobModelo: MobModelo, val grado: Byte, PA: Int, PM: Int, resist: String,
    statstexto: String, hechizos: String, var pDVMAX: Int, iniciativa: Int, exp: Int, minKamas: Int,
    maxKamas: Int
) {
    val resistencias: String
    val stats = Stats()
    private val _hechizos: MutableMap<Int, StatHechizo> = TreeMap()
    private val _textresist: String
    private val _PA: Int
    private val _PM: Int
    private val _iniciativa: Int
    private val _exporiginal: Int
    var nivel: Short = 0
        private set
    var baseXp: Int
    var minKamas: Int
    var maxKamas: Int
    var spells: String? = null
    private var _textstats: String
    val _fuerza = 0
    val _inte = 0
    val _agilidad = 0
    val _suerte = 0
    private var _pdvoriginal = 0
    private var _aleatorizado = false
    private fun calcular_stat_random(
        valor: Int,
        posibilidadnegativo: Boolean,
        esresis: Boolean,
        nopuedeser0: Boolean,
        desbuffeable: Boolean
    ): Int {
        if (AtlantaMain.RATE_RANDOM_MOB == 1.0) {
            return valor
        }
        //		_contsatsO+=valor;
        val random = FUN_RANDOM_MOB()
        var resultado = 0.0
        var negativo = false
        resultado = if (esresis) {
            FUN_RANDOM_MOB_RESIS() * valor
        } else {
            random * valor
        }
        if (nopuedeser0 && resultado == 0.0) {
            resultado += 1.0
        }
        if (posibilidadnegativo && mobModelo.nombre != "Kralamar Gigante") {
            if (Math.random() <= 0.3) { // %30 prob, puede que lo sea, como puede que no, esto lo aleatoriza.
                negativo = true
                resultado *= -1.0
            }
        } else if (resultado < 0) { // Se asegura de que no sea negativo
            resultado *= -1.0
        }
        if (desbuffeable) {
            if (Math.random() <= 0.25) { // 25% probabilidad
                var debuff = Math.random()
                while (debuff < 0.3) { // no puede desbufearse mas que un 70%
                    debuff = Math.random()
                }
                resultado *= debuff // Lo multiplica por un numero desde 0.3.....1 a 1
            }
        }
        if (esresis) {
            if (valor >= 90 && !negativo) {
                return valor
            } else if (resultado > 60) {
                return calcular_stat_random(valor, true, true, false, true)
            } else if (resultado < -100) {
                return calcular_stat_random(valor, true, true, false, true)
            }
        }
        //		if (esresis && valor>0){
//			_expcoef +=(resultado/valor);
//		} else if (esresis && valor<0){
//			_expcoef +=-1*(resultado/valor);
//		}
//		if (!esresis && resultado<valor){
//			_expcoef -=(1/(resultado/valor));
//		} else if (!esresis && resultado>valor){
//			_expcoef +=(resultado/valor);
//		}
//		_contstatsR+=resultado;
        return resultado.toInt()
    }

    //	public double posibilidad_negativo(double a){
//		if (Math.random() <= 0.5){
//			a*=-1;
//			return a;
//		}
//		return a;
//	}
    fun stats_aleatorias() {
        if (!_aleatorizado) {
            reiniciar_stats()
        }
        _aleatorizado = true
        val mapStats: MutableMap<Int, Int>? = TreeMap()
        val _contsatsO = 0.0
        val _contstatsR = 0.0
        var i = -1
        for (sValor in _textresist.split(",".toRegex()).toTypedArray()) {
            try {
                if (sValor.isEmpty()) {
                    continue
                }
                if (i == -1) {
                    nivel = sValor.toShort()
                } else {
                    mapStats?.set(ORDEN_RESISTENCIAS[i], calcular_stat_random(sValor.toInt(), true, true, false, true))
                }
                i++
            } catch (ignored: Exception) {
            }
        }
        i = 0
        loop@ for (s in _textstats.split(",".toRegex()).toTypedArray()) {
            try {
                if (s.isEmpty()) {
                    continue
                }
                if (s.contains(":")) {
                    val s2 = s.split(":".toRegex()).toTypedArray()
                    mapStats?.set(s2[0].toInt(), calcular_stat_random(s2[1].toInt(), false, false, false, true))
                } else {
                    i++
                    var idStat = 0
                    idStat = when (i) {
                        1 -> Constantes.STAT_MAS_FUERZA
                        2 -> Constantes.STAT_MAS_INTELIGENCIA
                        3 -> Constantes.STAT_MAS_SUERTE
                        4 -> Constantes.STAT_MAS_AGILIDAD
                        else -> continue@loop
                    }
                    mapStats?.set(idStat, calcular_stat_random(s.toInt(), false, false, false, true))
                }
            } catch (ignored: Exception) {
            }
        }
        mapStats?.putIfAbsent(Constantes.STAT_MAS_CRIATURAS_INVO, 1)
        if (mapStats?.get(Constantes.STAT_MAS_INICIATIVA) == null) {
            mapStats?.set(Constantes.STAT_MAS_INICIATIVA, calcular_stat_random(_iniciativa, false, false, false, false))
        }
        if (mapStats != null) {
            if (mapStats.containsKey(Constantes.STAT_MAS_PA)) {
                mapStats.replace(Constantes.STAT_MAS_PA, _PA)
                mapStats.replace(Constantes.STAT_MAS_PM, _PM)
            } else {
                mapStats.set(Constantes.STAT_MAS_PA, _PA)
                mapStats.set(Constantes.STAT_MAS_PM, _PM)
            }
        }
        //		_stats = new Stats();
//		double coefxp=Math.random();
//		while (coefxp < 0.4){
//			coefxp=Math.random();
//		}
//		if (_contsatsO<_contstatsR){
//			coefxp+=1;
//		}
//		_baseXP=(int) (coefxp*_baseXP);
        stats.nuevosStats(mapStats)
        pDVMAX = calcular_stat_random(_pdvoriginal, false, false, true, true)
    }

    private fun stats_base(): Map<Int, Int>? {
        baseXp = _exporiginal
        val mapStats: MutableMap<Int, Int>? = TreeMap()
        var i = -1
        for (sValor in _textresist.split(",".toRegex()).toTypedArray()) {
            try {
                if (sValor.isEmpty()) {
                    continue
                }
                if (i == -1) {
                    nivel = sValor.toShort()
                } else {
                    mapStats?.set(ORDEN_RESISTENCIAS[i], sValor.toInt())
                }
                i++
            } catch (ignored: Exception) {
            }
        }
        i = 0
        loop@ for (s in _textstats.split(",".toRegex()).toTypedArray()) {
            try {
                if (s.isEmpty()) {
                    continue
                }
                if (s.contains(":")) {
                    val s2 = s.split(":".toRegex()).toTypedArray()
                    mapStats?.set(s2[0].toInt(), s2[1].toInt())
                } else {
                    i++
                    var idStat = 0
                    idStat = when (i) {
                        1 -> Constantes.STAT_MAS_FUERZA
                        2 -> Constantes.STAT_MAS_INTELIGENCIA
                        3 -> Constantes.STAT_MAS_SUERTE
                        4 -> Constantes.STAT_MAS_AGILIDAD
                        else -> continue@loop
                    }
                    mapStats?.set(idStat, s.toInt())
                }
            } catch (ignored: Exception) {
            }
        }
        mapStats?.putIfAbsent(Constantes.STAT_MAS_CRIATURAS_INVO, 1)
        mapStats?.computeIfAbsent(Constantes.STAT_MAS_INICIATIVA) { k: Int? -> _iniciativa }
        if (mapStats != null) {
            if (mapStats.containsKey(Constantes.STAT_MAS_PA)) {
                mapStats.replace(Constantes.STAT_MAS_PA, _PA)
                mapStats.replace(Constantes.STAT_MAS_PM, _PM)
            } else {
                mapStats[Constantes.STAT_MAS_PA] = _PA
                mapStats[Constantes.STAT_MAS_PM] = _PM
            }
        }
        //		_stats = new Stats();
//		_PDVMAX=_pdvoriginal;
        return mapStats
    }

    fun reiniciar_stats() {
        stats.nuevosStats(stats_base())
        _aleatorizado = false
        pDVMAX = _pdvoriginal
    }

    fun invocarMob(id: Int, clon: Boolean, invocador: Luchador?): MobGrado {
        var invocador = invocador
        val copia = MobGrado(this, mobModelo, grado, nivel, pDVMAX, stats)
        if (clon) {
            copia.stats.fijarStatID(Constantes.STAT_MAS_CRIATURAS_INVO, 0)
            if (invocador != null) {
                copia.setPDVMAX(invocador.pdvMaxSinBuff)
                copia.pdv = invocador.pdvSinBuff
            }
        } else if (invocador != null) { // Lo de abajo era para aleatorizar las invos de los mobs
//			if (invocador.getMob() != null && AtlantaMain.RATE_RANDOM_MOB != 1){
//				copia.getMobGradoModelo().stats_aleatorias();
//			}
//			this.setPDVMAX(_pdvoriginal);
            var coefStats = 1f
            var coefVita = 1f
            val stats = copia.stats
            //			stats.nuevosStats(copia.getMobGradoModelo().stats_base());
            while (invocador!!.esInvocacion()) {
                invocador = invocador.invocador
                // coefStats -= 0.3f;
                stats.fijarStatID(Constantes.STAT_MAS_CRIATURAS_INVO, 0)
            }
            if (invocador.personaje != null) {
                coefVita += if (AtlantaMain.RATE_RANDOM_ITEM != 1.0 && AtlantaMain.RATE_RANDOM_ITEM >= 2) {
                    invocador.pdvConBuff / 1650f
                } else {
                    invocador.pdvConBuff / 1000f
                }
                coefStats += invocador.nivel / 10f
                //				invocador.getTotalStats()
            } else {
                coefVita += (FUN_RANDOM_MOB() - 1.0).toFloat()
            }
            val s = intArrayOf(
                Constantes.STAT_MAS_FUERZA, Constantes.STAT_MAS_INTELIGENCIA, Constantes.STAT_MAS_SUERTE,
                Constantes.STAT_MAS_AGILIDAD
            )
            val o = intArrayOf(
                Constantes.STAT_MAS_SABIDURIA, Constantes.STAT_MAS_RETIRO_PA,
                Constantes.STAT_MAS_RETIRO_PM, Constantes.STAT_MAS_ESQUIVA_PERD_PA,
                Constantes.STAT_MAS_ESQUIVA_PERD_PM, Constantes.STAT_MAS_PLACAJE
            )
            var tipoPelea = invocador.pelea.tipoPelea.toByte()
            if (invocador.personaje != null) {
                for (i in o) {
                    if (stats.getStatParaMostrar(i) <= 30) {
                        stats.fijarStatID(
                            i,
                            stats.getStatParaMostrar(i) + (copia.Nivel * 10)
                        )
                    }
                }
            }
            for (i in s) {
                if (invocador.personaje != null && (tipoPelea == Constantes.PELEA_TIPO_PVM || tipoPelea == Constantes.PELEA_TIPO_PVM_NO_ESPADA)) {
                    if (AtlantaMain.RATE_RANDOM_ITEM != 1.0 && AtlantaMain.RATE_RANDOM_ITEM >= 1.5) {
                        stats.fijarStatID(
                            i,
                            (stats.getStatParaMostrar(i) * (1 + invocador.totalStats.getTotalStatParaMostrar(i) / (100f * (AtlantaMain.RATE_RANDOM_ITEM / 1.2)))).toInt()
                        )
                    } else if (AtlantaMain.RATE_RANDOM_ITEM != 1.0) {
                        stats.fijarStatID(
                            i,
                            (stats.getStatParaMostrar(i) * (1 + invocador.totalStats.getTotalStatParaMostrar(i) / 100f)).toInt()
                        )
                    } else {
                        stats.fijarStatID(
                            i,
                            (stats.getStatParaMostrar(i) * (1 + invocador.totalStats.getTotalStatParaMostrar(i) / 70f)).toInt()
                        )
                    }
                } else if (invocador.personaje != null && (Constantes.PELEA_TIPO_CACERIA == tipoPelea || Constantes.PELEA_TIPO_DESAFIO == tipoPelea || Constantes.PELEA_TIPO_KOLISEO == tipoPelea || Constantes.PELEA_TIPO_PRISMA == tipoPelea || Constantes.PELEA_TIPO_PVP == tipoPelea || Constantes.PELEA_TIPO_RECAUDADOR == tipoPelea)) {
                    stats.fijarStatID(
                        i,
                        (stats.getStatParaMostrar(i) * (1 + invocador.totalStats.getTotalStatParaMostrar(i) / 180f)).toInt()
                    )
                } else {
                    stats.fijarStatID(i, stats.getStatParaMostrar(i))
                }
            }
            copia.setPDVMAX((_pdvoriginal * coefVita).toInt()) // + Math.sqrt(invocador.getPDVMaxSinBuff())
            copia.pdv = copia.pdvMax
        }
        copia.setIDPersonal(id)
        return copia
    }

    fun stringStatsActualizado(): String {
        val strStats = StringBuilder()
        for ((key, value) in stats.entrySet) {
            when (key) {
                Constantes.STAT_MAS_PA, Constantes.STAT_MAS_PM, Constantes.STAT_MAS_CRIATURAS_INVO, Constantes.STAT_MAS_SUERTE, Constantes.STAT_MAS_AGILIDAD, Constantes.STAT_MAS_FUERZA, Constantes.STAT_MAS_INTELIGENCIA, Constantes.STAT_MAS_INICIATIVA -> {
                    if (strStats.isNotEmpty()) {
                        strStats.append(",")
                    }
                    strStats.append(key).append(":").append(value)
                }
            }
        }
        return strStats.toString()
    }

    fun Set_StatsBase(a: String) {
        _textstats = a
    }

    val pA: Int
        get() = stats.getStatParaMostrar(Constantes.STAT_MAS_PA)

    val pM: Int
        get() = stats.getStatParaMostrar(Constantes.STAT_MAS_PM)

    val hechizos: Map<Int, StatHechizo>
        get() = _hechizos

    fun setHechizos(hechizos: String) {
        val aHechizo = hechizos.split(";".toRegex()).toTypedArray()
        for (str in aHechizo) {
            if (str.isEmpty()) {
                continue
            }
            val hechizoInfo = str.split("@".toRegex()).toTypedArray()
            var hechizoID = 0
            var hechizoNivel = 0
            try {
                hechizoID = hechizoInfo[0].toInt()
                hechizoNivel = hechizoInfo[1].toInt()
            } catch (e: Exception) {
                continue
            }
            if (hechizoID <= 0 || hechizoNivel <= 0) {
                continue
            }
            val hechizo = Mundo.getHechizo(hechizoID) ?: continue
            val hechizoStats = hechizo.getStatsPorNivel(hechizoNivel) ?: continue
            _hechizos.set(hechizoID, hechizoStats)
        }
    }

    val idModelo: Int
        get() = mobModelo.id

    val gfxID: Short
        get() = mobModelo.gfxID

    companion object {
        private val ORDEN_RESISTENCIAS = intArrayOf(
            Constantes.STAT_MAS_RES_PORC_NEUTRAL, Constantes.STAT_MAS_RES_PORC_TIERRA,
            Constantes.STAT_MAS_RES_PORC_FUEGO, Constantes.STAT_MAS_RES_PORC_AGUA, Constantes.STAT_MAS_RES_PORC_AIRE,
            Constantes.STAT_MAS_ESQUIVA_PERD_PA, Constantes.STAT_MAS_ESQUIVA_PERD_PM
        )
    }

    init {
        _pdvoriginal = pDVMAX
        baseXp = exp
        _exporiginal = exp
        resistencias = resist
        _textstats = statstexto
        _textresist = resist
        _PA = PA
        _PM = PM
        _iniciativa = iniciativa
        if (hechizos != "-1") {
            spells = hechizos
        }
        this.minKamas = minKamas
        this.maxKamas = maxKamas
        val mapStats: MutableMap<Int, Int>? = TreeMap()
        mapStats?.set(Constantes.STAT_MAS_PA, PA)
        mapStats?.set(Constantes.STAT_MAS_PM, PM)
        var i = -1
        for (sValor in resist.split(",".toRegex()).toTypedArray()) {
            try {
                if (sValor.isEmpty()) {
                    continue
                }
                if (i == -1) {
                    nivel = sValor.toShort()
                } else {
                    mapStats?.set(ORDEN_RESISTENCIAS[i], sValor.toInt())
                }
                i++
            } catch (ignored: Exception) {
            }
        }
        // STATS
        i = 0
        loop@ for (s in statstexto.split(",".toRegex()).toTypedArray()) {
            try {
                if (s.isEmpty()) {
                    continue
                }
                if (s.contains(":")) {
                    val s2 = s.split(":".toRegex()).toTypedArray()
                    mapStats?.set(s2[0].toInt(), s2[1].toInt())
                } else {
                    i++
                    var idStat = 0
                    idStat = when (i) {
                        1 -> Constantes.STAT_MAS_FUERZA
                        2 -> Constantes.STAT_MAS_INTELIGENCIA
                        3 -> Constantes.STAT_MAS_SUERTE
                        4 -> Constantes.STAT_MAS_AGILIDAD
                        else -> continue@loop
                    }
                    mapStats?.set(idStat, s.toInt())
                }
            } catch (ignored: Exception) {
            }
        }
        mapStats?.putIfAbsent(Constantes.STAT_MAS_CRIATURAS_INVO, 1)
        mapStats?.putIfAbsent(Constantes.STAT_MAS_INICIATIVA, iniciativa)
        stats.nuevosStats(mapStats)
        setHechizos(hechizos)
    }
}