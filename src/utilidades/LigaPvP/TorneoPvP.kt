package utilidades.LigaPvP

import estaticos.AtlantaMain
import estaticos.GestorSalida
import estaticos.Mundo
import variables.personaje.Personaje
import variables.zotros.Accion
import kotlin.concurrent.thread
import kotlin.math.pow

class TorneoPvP {
    val participantes = mutableListOf<Personaje>()
    val ips = mutableListOf<String>()
    var comenzo = false
    var idRonda = 0
    var rondaActual: Ronda? = null
    var rondas = arrayListOf<Ronda>()
    val participantesPerdedores = mutableListOf<Personaje>()
    val participantesRestantes = mutableListOf<Personaje>()
    var termino = false
    var thread: Thread? = null
    fun addParticipante(personaje: Personaje): Boolean {
        val ip = personaje.cuenta.actualIP
        return when {
            ips.contains(ip) -> false
            participantes.contains(personaje) -> false
            comenzo -> false
            termino -> false
            else -> {
                GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(
                    personaje,
                    "Te has unido con exito a un torneo PVP, espere a mas jugadores."
                )
                participantes.add(personaje)
                ips.add(ip)
                true
            }
        }
    }

    fun listaParticipantes(): String {
        val str = StringBuilder()
        str.append("Participantes: \n")
        participantes.forEach { str.append("-${it.nombre}\n") }
        return str.toString()
    }

    fun removeParticipante(personaje: Personaje): Boolean {
        val tenia = participantes.contains(personaje)
        participantes.remove(personaje)
        val ip = personaje.cuenta.actualIP
        ips.remove(ip)
        return tenia
    }

    fun cancelar(): Boolean {
        return if (comenzo) false else {
            participantes.forEach {
                it.enviarmensajeNegro("El torneo ha sido cancelado")
                removeParticipante(it)
            }
            Mundo.TORNEOSPVP.remove(this)
            termino = true
            thread?.interrupt()
            true
        }
    }

    fun terminar(personaje: Personaje): Boolean {
        thread {
            try {
                Thread.sleep(10000)
            } catch (e: Exception) {
            }
            personaje.teleport(personaje.MapaAnteriorPVP.id)
            val s = AtlantaMain.RECOMPENSA_TORNEO.split("|", limit = 2)
            try {
                val id = s[0]
                val arg = s[1]
                Accion.realizar_Accion_Estatico(id.toInt(), arg, personaje, null, -1, -1)
            } catch (e: Exception) {
                personaje.enviarmensajeNegro("Hubo un error al darle su recompensa, Por favor contacte con un administrador")
            }
        }
        Mundo.TORNEOSPVP.remove(this)
        termino = true
        try {
            Thread.sleep(1000)
        } catch (e: Exception) {
        }
        thread?.interrupt()
        return true
    }

    init {
        thread = thread(name = "TorneoPVP ${Mundo.TORNEOSPVP.indexOf(this)}") {
            try {
                Mundo.TORNEOSPVP.add(this)
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("SE HA INICIADO UN NUEVO TORNEO")
                val t = System.currentTimeMillis() + 900000
                while (participantes.size < AtlantaMain.RONDAS_TORNEO) {
                    for (personaje in participantes) {
                        if (!personaje.enLinea()) removeParticipante(personaje)
                    }
                    try {
                        Thread.sleep(1000)
                    } catch (e: Exception) {
                    }
                    if (System.currentTimeMillis() > t || participantes.isEmpty()) {
                        cancelar()
                        break
                    }
                }
                participantesRestantes.addAll(participantes)
                for (i in 1..100) {
                    if (AtlantaMain.RONDAS_TORNEO.toDouble().pow((1.0 / i)) == 2.0) {
                        idRonda = i
                        break
                    }
                }
                participantesRestantes.forEach { it.MapaAnteriorPVP = it.Mapa }
                rondaActual = Ronda(idRonda, this, participantesRestantes)
                comenzo = true
                while (!termino) {
                    if (!rondaActual!!.acabo) {
                        try {
                            Thread.sleep(1000)
                        } catch (e: Exception) {
                        }
                    } else {
                        idRonda--
                        rondaActual = Ronda(idRonda, this, participantesRestantes)
                    }
                }
            } catch (e: Exception) {
                cancelar()
            }
        }
    }
}