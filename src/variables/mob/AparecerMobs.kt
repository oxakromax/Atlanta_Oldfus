package variables.mob

import estaticos.AtlantaMain
import variables.mapa.Mapa
import kotlin.concurrent.thread

class AparecerMobs(private val _mapa: Mapa, private val _grupoMob: GrupoMob, private val _tipoAparecer: Aparecer) :
    Thread() {
    override fun run() {
        var tiempo = AtlantaMain.SEGUNDOS_REAPARECER_MOBS
        if (_grupoMob.segundosRespawn > 0) {
            tiempo = _grupoMob.segundosRespawn
        }
        if (tiempo > 0) {
            try {
                sleep(tiempo * 1000.toLong())
            } catch (ignored: Exception) {
            }
        }
        when (_tipoAparecer) {
            Aparecer.INICIO_PELEA -> _mapa.addSiguienteGrupoMob(_grupoMob, false)
            Aparecer.FINAL_PELEA -> _mapa.addUltimoGrupoMob(_grupoMob, false)
        }
    }

    enum class Aparecer {
        INICIO_PELEA, FINAL_PELEA
    }

    init {
        thread(true, true, null, null, 6, { run() })
//        isDaemon = true
//        priority = 6
//        start()
    }
}