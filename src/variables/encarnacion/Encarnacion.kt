package variables.encarnacion

import estaticos.AtlantaMain
import estaticos.GestorSalida.ENVIAR_BN_NADA
import estaticos.GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO
import estaticos.Mundo.getExpEncarnacion
import estaticos.Mundo.getHechizo
import variables.hechizo.StatHechizo
import variables.objeto.Objeto
import variables.personaje.Personaje
import variables.stats.Stats
import variables.stats.TotalStats
import java.util.*

class Encarnacion(private val _objeto: Objeto, exp: Int, private val _encarnacionModelo: EncarnacionModelo) {
    private val _hechizos: MutableMap<Int, StatHechizo?> = HashMap()
    private val _posicionHechizos: MutableMap<Int, Char> = HashMap()
    var exp = 0
        private set
    var nivel = 0
        private set
    private var _noLevel = false
    var totalStats: TotalStats? = null
    var ultEquipada: Long = 0
        private set

    fun addExperiencia(exp: Long, perso: Personaje?) {
        if (_noLevel) {
            return
        }
        if (this.exp >= getExpEncarnacion(AtlantaMain.NIVEL_MAX_ENCARNACION)) {
            return
        }
        this.exp += exp.toInt()
        val nivel = nivel
        while (this.exp >= getExpEncarnacion(this.nivel + 1) && this.nivel < AtlantaMain.NIVEL_MAX_ENCARNACION) {
            subirNivel()
        }
        if (nivel != this.nivel && perso != null) {
            if (perso.enLinea()) {
                perso.refrescarStuff(true, false, false)
                ENVIAR_OCK_ACTUALIZA_OBJETO(perso, _objeto)
            }
        }
    }

    private fun subirNivel() {
        nivel++
        refrescarStatsItem()
        refrescarHechizos()
    }

    fun refrescarHechizos() {
        val nivel = nivel / 10
        for (hechizoID in _posicionHechizos.keys) {
            _hechizos[hechizoID] = getHechizo(hechizoID)!!.getStatsPorNivel(nivel + 1)
        }
    }

    fun refrescarStatsItem() {
        if (!_encarnacionModelo.statsPorNivel.isEmpty()) {
            for ((key, value) in _encarnacionModelo.statsPorNivel) {
                val valor = (value * nivel).toInt()
                if (valor > 0) {
                    _objeto.stats.fijarStatID(key, valor)
                }
            }
        }
    }

    fun stringListaHechizos(): String {
        val nivel = nivel / 10
        val str = StringBuilder()
        for ((key, value) in _posicionHechizos) {
            str.append(key).append("~").append(nivel + 1).append("~").append(value).append(";")
        }
        return str.toString()
    }

    fun tieneHechizoID(hechizoID: Int): Boolean {
        return _posicionHechizos[hechizoID] != null
    }

    val statHechizos: Map<Int, StatHechizo?>
        get() = _hechizos

    fun getStatsHechizo(hechizoID: Int): StatHechizo? {
        return _hechizos[hechizoID]
    }

    fun setEquipado() {
        ultEquipada = System.currentTimeMillis()
    }

    val iD: Int
        get() = _objeto.id

    val gfxID: Int
        get() = _encarnacionModelo.gfxID

    fun setPosHechizo(hechizoID: Int, pos: Char, perso: Personaje?) {
        if (pos == 'a') {
            ENVIAR_BN_NADA(perso, "SET POS HECHIZO - POS INVALIDA")
            return
        }
        if (!tieneHechizoID(hechizoID)) {
            ENVIAR_BN_NADA(perso, "SET POS HECHIZO - NO TIENE HECHIZO")
            return
        }
        var exID = -1
        if (pos != '_') {
            for ((key, value) in _posicionHechizos) {
                if (value == pos) {
                    exID = key
                    break
                }
            }
        }
        if (exID != -1) {
            _posicionHechizos[exID] = '_'
        }
        _posicionHechizos[hechizoID] = pos
        ENVIAR_BN_NADA(perso!!)
    }

    init {
        if (!_encarnacionModelo.statsBase.isEmpty()) {
            val statsBase = Stats(_encarnacionModelo.statsBase)
            totalStats =
                TotalStats(statsBase, Stats(), Stats(), Stats(), 1)
            _noLevel = true
        }
        _posicionHechizos.putAll(_encarnacionModelo.posicionsHechizos)
        addExperiencia(exp.toLong(), null)
    }
}