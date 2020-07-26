package utilidades.LigaPvP

import estaticos.AtlantaMain
import estaticos.Formulas
import estaticos.Mundo
import variables.mapa.Mapa
import variables.pelea.Pelea
import variables.personaje.GrupoKoliseo
import variables.personaje.Personaje
import java.util.*
import kotlin.concurrent.fixedRateTimer

class PeleaPvP(val id: Int, val ronda: Ronda, val p1: Personaje, val p2: Personaje) {
    var pelea: Pelea? = null
    val mapa: Mapa? = Mundo.getMapa(
        (AtlantaMain.MAPAS_KOLISEO.split(",")[Formulas.getRandomInt(
            0,
            AtlantaMain.MAPAS_KOLISEO.split(",").size - 1
        )]).toShort()
    )
    var Ganador: Personaje? = null
    var Perdedor: Personaje? = null
    var timer: Timer? = null
    var terminada = false
    var anunciada = false

    init {
        if (mapa != null) {
            p1.teleport(mapa.id)
            p2.teleport(mapa.id)
            val g1 = GrupoKoliseo(p1)
            val g2 = GrupoKoliseo(p2)
            p1.Koliseo = g1
            p2.Koliseo = g2
            pelea = mapa.iniciarPeleaTorneo(g1, g2)
            ronda.participantes.forEach { it.enviarmensajeRojo("---- ${p1.nombre} VS ${p2.nombre} ----") }
            timer = fixedRateTimer("Evaluador Pelea", period = 1000, initialDelay = 1000) {
                if (!pelea!!.continuaPelea()) {
                    if (pelea?.equipoGanador() == 1) {
                        Ganador = p1
                        Perdedor = p2
                        terminada = true
                        timer?.cancel()
                    } else if (pelea?.equipoGanador() == 2) {
                        Ganador = p2
                        Perdedor = p1
                        terminada = true
                        timer?.cancel()
                    }
                }
            }
        }
    }
}