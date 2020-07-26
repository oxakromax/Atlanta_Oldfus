package variables.gremio

import estaticos.AtlantaMain
import estaticos.Constantes
import estaticos.GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA_MAPA
import estaticos.Mundo
import estaticos.Mundo.getCasaDePj
import estaticos.Mundo.getExpGremio
import estaticos.Mundo.getHechizo
import estaticos.Mundo.getPersonaje
import estaticos.Mundo.sigIDGremio
import estaticos.database.GestorSQL.DELETE_MIEMBRO_GREMIO
import variables.hechizo.StatHechizo
import variables.personaje.Personaje
import variables.stats.Stats
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.regex.Pattern
import kotlin.math.floor

class Gremio {
    val id: Int
    private val _hechizos: MutableMap<Int, StatHechizo?> = hechizosPrimarios()
    private val _statsRecolecta: MutableMap<Int, Int> = HashMap()
    private val _tiempoMapaRecolecta: MutableMap<Short, Long?> = HashMap()
    private val _miembros: MutableMap<Int, MiembroGremio> = HashMap()
    val _recaudadores = CopyOnWriteArrayList<Recaudador>()
    val statsPelea = Stats()
    var nroMaxRecau = 0
    var nivel: Short = 1
        private set
    private var _capital: Short = 0
    var experiencia: Long = 0
        private set
    var nombre = ""
    var emblema = ""
    var guerra: Guerra? = null

    constructor(dueño: Personaje?, nombre: String, emblema: String) {
        id = sigIDGremio()
        this.nombre = nombre
        this.emblema = emblema
        experiencia = 0
        decompilarStats("176;100|158;1000|124;0")
        guerra = Guerra(this)
    }

    constructor(
        id: Int, nombre: String, emblema: String, nivel: Short, xp: Long,
        capital: Short, nroMaxRecau: Byte, hechizos: String, stats: String
    ) {
        this.id = id
        addExperiencia(xp, true)
        this.nombre = nombre
        this.emblema = emblema
        _capital = capital
        this.nroMaxRecau = nroMaxRecau.toInt()
        decompilarHechizos(hechizos)
        decompilarStats(stats)
        guerra = Guerra(this)
    }

    fun addMiembro(
        id: Int, rango: Int, expDonada: Long, porcXPDonar: Byte,
        derechos: Int
    ): MiembroGremio {
        val miembro = MiembroGremio(id, this, rango, expDonada, porcXPDonar, derechos)
        _miembros[id] = miembro
        return miembro
    }

    val cantRecaudadores: Int
        get() = _recaudadores.size

    fun addRecaudador(r: Recaudador) {
        _recaudadores.add(r)
    }

    fun delRecaudador(r: Recaudador?) {
        _recaudadores.remove(r)
    }

    fun eliminarTodosRecaudadores() {
        for (r in _recaudadores) {
            r.borrarRecaudador()
        }
    }

    val infoGremio: String
        get() = nombre + "," + getStatRecolecta(Constantes.STAT_MAS_PODS) + "," + getStatRecolecta(
            Constantes.STAT_MAS_PROSPECCION
        ) + "," + getStatRecolecta(Constantes.STAT_MAS_SABIDURIA) + "," + _recaudadores
            .size

    val capital: Int
        get() = _capital.toInt()

    fun addCapital(nro: Int) {
        _capital = (_capital + nro.toShort()).toShort()
    }

    val hechizos: Map<Int, StatHechizo?>
        get() = _hechizos

    fun olvidarHechizo(hechizoID: Int, porCompleto: Boolean): Boolean {
        val h = _hechizos.get(hechizoID) ?: return false
        for (i in 1 until h.grado) {
            _capital = (_capital + i.toShort()).toShort()
        }
        return fijarNivelHechizoOAprender(hechizoID, if (porCompleto) 0 else 1, false)
    }

    fun boostHechizo(hechizoID: Int): Boolean {
        if (_hechizos != null) {
            if (!_hechizos.containsKey(hechizoID)) {
                return false
            }
        }
        val SH = _hechizos.get(hechizoID)
        if (SH != null && SH.grado >= 5) {
            return false
        }
        val hechizo = getHechizo(hechizoID) ?: return false
        val nivel = if (SH == null) 1 else SH.grado + 1
        return fijarNivelHechizoOAprender(hechizoID, nivel, false)
    }

    fun fijarNivelHechizoOAprender(hechizoID: Int, nivel: Int, mensaje: Boolean): Boolean {
        if (nivel > 0) {
            val hechizo = getHechizo(hechizoID) ?: return false
            val statHechizo = hechizo.getStatsPorNivel(nivel)
            if (statHechizo == null || statHechizo.nivelRequerido > this.nivel) {
                return false
            }
            _hechizos.set(hechizoID, statHechizo)
        } else {
            return false // aca puede haber quilombo
        }
        return true
    }

    val cantidadMiembros: Int
        get() = _miembros.size

    fun infoPanelGremio(): String {
        val xpMin = getExpGremio(nivel.toInt())
        val xpMax = getExpGremio(nivel + 1)
        return ("gIG" + (if (cantidadMiembros >= 10) 1 else 0) + "|" + nivel + "|" + xpMin + "|"
                + experiencia + "|" + xpMax)
    }

    fun analizarMiembrosGM(): String {
        val str = StringBuilder()
        for (miembro in _miembros.values) {
            if (miembro.personaje == null) {
                continue
            }
            if (str.length > 0) {
                str.append("|")
            }
            str.append(miembro.id).append(";")
            str.append(miembro.nombre).append(";")
            str.append(miembro.nivel).append(";")
            str.append(miembro.gfx).append(";")
            str.append(miembro.rango).append(";")
            str.append(miembro.xpDonada).append(";")
            str.append(miembro.porcXpDonada).append(";")
            str.append(miembro.derechos).append(";")
            str.append(if (miembro.personaje.enLinea()) 1 else 0).append(";")
            str.append(miembro.personaje.alineacion.toInt()).append(";")
            str.append(miembro.horasDeUltimaConeccion)
        }
        return str.toString()
    }

    val miembros: ArrayList<Personaje?>
        get() {
            val a = ArrayList<Personaje?>()
            for (miembro in _miembros.values) {
                a.add(miembro.personaje)
            }
            return a
        }

    // public Collection<MiembroGremio> getMiembros() {
// return _miembros.values();
// }
    fun getMiembro(idMiembro: Int): MiembroGremio? {
        return _miembros[idMiembro]
    }

    fun expulsarTodosMiembros() {
        val a = ArrayList(_miembros.values)
        for (miembro in a) {
            expulsarMiembro(miembro.id)
        }
    }

    fun expulsarMiembro(persoID: Int) {
        val casa = getCasaDePj(persoID)
        if (casa != null) {
            casa.nullearGremio()
            casa.actualizarDerechos(0)
        }
        _miembros.remove(persoID)
        DELETE_MIEMBRO_GREMIO(persoID)
        val perso = getPersonaje(persoID)
        if (perso != null) {
            perso.miembroGremio = null
            if (perso.enLinea() && perso.pelea == null) {
                ENVIAR_Oa_CAMBIAR_ROPA_MAPA(perso.mapa, perso)
            }
        }
    }

    fun addExperiencia(xp: Long, sinPuntos: Boolean) {
        experiencia += xp
        val nivelAnt = nivel.toInt()
        while (experiencia >= getExpGremio(nivel + 1) && nivel < AtlantaMain.NIVEL_MAX_GREMIO) {
            subirNivel(sinPuntos)
        }
        if (!sinPuntos) {
            if (nivel.toInt() != nivelAnt) {
                refrescarStatsPelea()
            }
        }
    }

    fun subirNivel(sinPuntos: Boolean) {
        nivel = (nivel + 1).toShort()
        if (!sinPuntos) {
            _capital = (_capital + 5).toShort()
        }
    }

    fun refrescarStatsPelea() {
        val stats: MutableMap<Int, Int> = TreeMap()
        stats[Constantes.STAT_MAS_PA] = 6
        stats[Constantes.STAT_MAS_PM] = 5
        stats[Constantes.STAT_MAS_SABIDURIA] = getStatRecolecta(Constantes.STAT_MAS_SABIDURIA)
        stats[Constantes.STAT_MAS_DAÑOS] = nivel.toInt()
        val statsIDs = intArrayOf(
            Constantes.STAT_MAS_RES_PORC_NEUTRAL, Constantes.STAT_MAS_RES_PORC_TIERRA,
            Constantes.STAT_MAS_RES_PORC_FUEGO, Constantes.STAT_MAS_RES_PORC_AIRE, Constantes.STAT_MAS_RES_PORC_AGUA,
            Constantes.STAT_MAS_ESQUIVA_PERD_PA, Constantes.STAT_MAS_ESQUIVA_PERD_PM
        )
        val resistencia = Math.min(50, nivel.toInt())
        for (s in statsIDs) {
            stats[s] = resistencia
        }
        statsPelea.nuevosStats(stats)
    }

    fun addUltRecolectaMapa(mapaID: Short) {
        _tiempoMapaRecolecta[mapaID] = System.currentTimeMillis()
    }

    fun puedePonerRecaudadorMapa(mapaID: Short): Boolean {
        if (AtlantaMain.HORAS_VOLVER_A_PONER_RECAUDADOR_MAPA < 1) {
            return true
        }
        if (_tiempoMapaRecolecta.containsKey(mapaID)) {
            val tiempoM = 60 * 60 * 1000 * AtlantaMain.HORAS_VOLVER_A_PONER_RECAUDADOR_MAPA
            return _tiempoMapaRecolecta[mapaID]!! + tiempoM <= System.currentTimeMillis()
        }
        return true
    }

    fun analizarInfoCercados(): String {
        val maxCercados = floor(nivel / 10.toDouble()).toInt().toByte()
        val str = StringBuilder(maxCercados.toInt())
        for (cercados in Mundo.CERCADOS.values) {
            if (cercados.gremio === this) {
                str.append("|").append(cercados.mapa!!.id.toInt()).append(";").append(cercados.capacidadMax).append(";")
                    .append(
                        cercados
                            .cantObjMax.toInt()
                    )
                if (cercados.criando.size > 0) {
                    str.append(";")
                    var primero = false
                    for (DP in cercados.criando.values) {
                        if (DP == null) {
                            continue
                        }
                        if (primero) {
                            str.append(",")
                        }
                        str.append(DP.color).append(",").append(DP.nombre).append(",")
                        if (getPersonaje(DP.dueñoID) == null) {
                            str.append("SIN DUEÑO")
                        } else {
                            str.append(getPersonaje(DP.dueñoID)!!.nombre)
                        }
                        primero = true
                    }
                }
            }
        }
        return str.toString()
    }

    private fun hechizosPrimarios(): MutableMap<Int, StatHechizo?> {
        val hechizos = mutableMapOf<Int, StatHechizo?>()
        for (split in "462;0|461;0|460;0|459;0|458;0|457;0|456;0|455;0|454;0|453;0|452;0|451;0".split(
            Pattern.quote("|").toRegex()
        ).toTypedArray()) {
            try {
                val id = split.split(";".toRegex()).toTypedArray()[0].toInt()
                val nivel = split.split(";".toRegex()).toTypedArray()[1].toInt()
                if (getHechizo(id) == null) {
                    continue
                }
                hechizos.put(id, getHechizo(id)?.getStatsPorNivel(nivel))
            } catch (ignored: Exception) {
            }
        }
        return hechizos
    }

    fun decompilarHechizos(strHechizo: String) {
        if (strHechizo.isEmpty()) return
        for (split in strHechizo.split(Pattern.quote("|").toRegex()).toTypedArray()) {
            try {
                val id = split.split(";".toRegex()).toTypedArray()[0].toInt()
                val nivel = split.split(";".toRegex()).toTypedArray()[1].toInt()
                _hechizos[id] = getHechizo(id)?.getStatsPorNivel(nivel)
            } catch (ignored: Exception) {
            }
        }
    }

    fun decompilarStats(statsStr: String) {
        for (split in statsStr.split(Pattern.quote("|").toRegex()).toTypedArray()) {
            try {
                val stat = split.split(";".toRegex()).toTypedArray()[0].toInt()
                val cant = split.split(";".toRegex()).toTypedArray()[1].toInt()
                _statsRecolecta[stat] = cant
            } catch (ignored: Exception) {
            }
        }
        refrescarStatsPelea()
    }

    fun compilarHechizo(): String {
        val str = StringBuilder()
        for ((key, value) in _hechizos) {
            if (str.isNotEmpty()) {
                str.append("|")
            }
            if (value != null) {
                str.append(key).append(";")
                    .append(value.grado)
            } else {
                str.append(key).append(";")
                    .append(0)
            }
        }
        return str.toString()
    }

    fun compilarStats(): String {
        val str = StringBuilder()
        for ((key, value) in _statsRecolecta) {
            if (str.length > 0) {
                str.append("|")
            }
            str.append(key).append(";").append(value)
        }
        return str.toString()
    }

    fun getStatRecolecta(id: Int): Int {
        return if (_statsRecolecta[id] != null) _statsRecolecta[id]!! else 0
    }

    fun addStat(id: Int, add: Int) {
        try {
            _statsRecolecta[id] = _statsRecolecta[id]!! + add
        } catch (e: Exception) {
            _statsRecolecta[id] = add
        }
    }

    fun analizarRecauAGremio(): String {
        return (nroMaxRecau.toString() + "|" + _recaudadores.size + "|" + 100 * nivel + "|" + nivel + "|"
                + getStatRecolecta(Constantes.STAT_MAS_PODS) + "|" + getStatRecolecta(Constantes.STAT_MAS_PROSPECCION) + "|"
                + getStatRecolecta(Constantes.STAT_MAS_SABIDURIA) + "|" + nroMaxRecau + "|" + _capital + "|" + (1000 + (10
                * nivel)) + "|" + compilarHechizo())
    }

    fun analizarRecaudadores(): String {
        val str = StringBuilder()
        for (r in _recaudadores) {
            if (str.length > 0) {
                str.append("|")
            }
            str.append(r.infoPanel)
        }
        return if (str.length == 0) {
            ""
        } else "+$str"
    }

    fun actualizarAtacantesDefensores() {
        for (re in _recaudadores) {
            re.actualizarAtacantesDefensores()
        }
    }

    val maxMiembros: Int
        get() {
            var maxMiembros = 40 + nivel * 4
            if (AtlantaMain.LIMITE_MIEMBROS_GREMIO > 0) {
                maxMiembros = AtlantaMain.LIMITE_MIEMBROS_GREMIO
            }
            return maxMiembros
        }
}