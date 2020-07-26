package utilidades.LigaPvP

import estaticos.AtlantaMain
import estaticos.GestorSalida
import variables.personaje.Personaje
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.thread

class Ronda(val id: Int, val torneoPvP: TorneoPvP, val participantes: MutableList<Personaje>) {
    var timer: Timer? = null
    var nombre = ""
    var acabo = false
    val ganadores = arrayListOf<Personaje>()
    val perdedores = arrayListOf<Personaje>()

    init {
        when (id - 1) {
            1 -> {
                nombre = "Final"
            }
            2 -> {
                nombre = "Semifinal"
            }
            3 -> {
                nombre = "Cuartos de Final"
            }
        }
        val e1: MutableList<Personaje> = emptyList<Personaje>().toMutableList()
        val e2: MutableList<Personaje> = emptyList<Personaje>().toMutableList()
        var c = 0
        val peleas: MutableList<PeleaPvP> = emptyList<PeleaPvP>().toMutableList()
        for (personaje in participantes) {
            if (c % 2 == 0) {
                e1.add(personaje)
            } else {
                e2.add(personaje)
            }
            c++
        }
        c = 0
        if (e1.size == e2.size) {
            for (personaje in participantes) {
                if (personaje.pelea != null) {
                    personaje.pelea.cancelarPelea()
                }
                personaje.enviarmensajeNegro("Fase de preparacion!\nLa ronda comenzara en 60 segundos!")
                personaje.teleport(AtlantaMain.MAPA_ESPERA_TORNEO.toShort())
            }
            try {
                Thread.sleep(60000)
            } catch (e: Exception) {
            }
            while (c < e1.size) {
                peleas.add(PeleaPvP(c, this, e1[c], e2[c]))
                c++
            }
            timer = fixedRateTimer("Verificador Ganadores", initialDelay = 10000, period = 1000) {
                val peleasTerminadas = peleas.filter { it.terminada }
                peleas.filter { it.terminada && !it.anunciada }.forEach {
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(
                        "El jugador ${it.Ganador?.nombre} ha ganado contra ${it.Perdedor?.nombre} y ${if (peleas.size != 1)
                            "Ha avanzado a la siguiente ronda ${if (nombre != "") "($nombre)" else ""}" else "GANÓ EL TORNEO!!"} "
                    )
                    it.anunciada = true
                    thread {
                        try {
                            Thread.sleep(5000)
                        } catch (e: Exception) {
                        }
                        val perdedor = it.Perdedor
                        perdedor?.teleport(perdedor.MapaAnteriorPVP.id)
                        if (peleas.size == 1) {
                            val ganador = it.Ganador
                            ganador?.teleport(ganador.MapaAnteriorPVP.id)
                        }
                    }
                }
                if (peleasTerminadas.size == peleas.size) {
                    for (peleaPvP in peleasTerminadas) {
                        val w = peleaPvP.Ganador
                        val l = peleaPvP.Perdedor
                        if (w != null) {
                            ganadores.add(w)
                        }
                        if (l != null) {
                            perdedores.add(l)
                            torneoPvP.removeParticipante(l)
                        }
//                        thread {
//                            try {
//                                Thread.sleep(10000)
//                            } catch (e: Exception) {
//                            }
//                            w?.teleport(w.MapaAnteriorPVP.id)
////                            l?.teleport(l.MapaAnteriorPVP.id)
//                        }
                    }
                    torneoPvP.participantesRestantes.clear()
                    torneoPvP.participantesRestantes.addAll(ganadores)
                    torneoPvP.participantesPerdedores.addAll(perdedores)
                    acabo = true
                    timer?.cancel()
                }
            }
        } else if (e1.size == 1 && (id - 1) == 0) {
            torneoPvP.terminar(e1[0])
            acabo = true
        }
        torneoPvP.rondas.add(this)
    }
}