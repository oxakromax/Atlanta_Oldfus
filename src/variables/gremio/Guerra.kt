package variables.gremio

import estaticos.AtlantaMain
import estaticos.Constantes
import estaticos.GestorSalida
import estaticos.Mundo
import variables.objeto.ObjetoModelo
import variables.pelea.Luchador
import variables.pelea.Pelea
import variables.personaje.Personaje
import kotlin.concurrent.thread

class Guerra(var gremio: Gremio) {
    var Integrantes = arrayListOf<Personaje>()
    var recaudadores = arrayListOf<Recaudador>()
    var recaudadoresMuertos = 0
    var EnPelea = false
    var pelea: Pelea? = null
    var thread: Thread? = null
    fun AnunciarVictoriaSobre(gremio: Gremio?) {
        if (gremio == null) {
            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(
                "EL GREMIO [${this.gremio.nombre}] " +
                        "HA GANADO LA GUERRA!!!"
            )
            return
        } else {
            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(
                "EL GREMIO [${this.gremio.nombre}] " +
                        "HA DERROCADO A [${gremio.nombre}] Y SE HA ALZADO CON LA VICTORIA!!"
            )
        }
    }

    private fun setLider(perso: Personaje?) {
        if (perso == null) {
            return
        }
        if (perso.gremio != gremio) return
//        if (!perso.miembroGremio.puede(Constantes.G_RECOLECTAR_RECAUDADOR)) return
        Integrantes.add(0, perso)
    }

    fun recompensa(luchador: Luchador) {
        val perso = luchador.personaje ?: return
        if (!Integrantes.contains(perso)) return
        val recompensas = AtlantaMain.OBJETOS_GUERRA.split(";") as ArrayList<String>
        for (recompensa in recompensas) {
            try {
                val r = recompensa.split(",")
                val id = r[0].toInt()
                val cantidad = r[1].toInt()
                val cap = ObjetoModelo.CAPACIDAD_STATS.RANDOM
                val obj =
                    Mundo.getObjetoModelo(id)?.crearObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO, cap) ?: continue
                luchador.addDropLuchador(obj, true)
            } catch (e: Exception) {
            }
        }
    }

    fun verificador() {
        while (!EnPelea && Integrantes.isNotEmpty()) {
            try {
                filtrarIntegrantes()
                if (filtrarRecaudadores()) {
                    finalizarGuerra()
                    break
                }
                if (Integrantes.isEmpty()) finalizarGuerra()
                Thread.sleep(5000)
            } catch (e: Exception) {
            }
        }
        thread = null
    }

    fun finalizarGuerra(victoria: Boolean = false, anuncio: Boolean = false) {
        thread = null
        recaudadoresMuertos = 0
        pelea = null
        EnPelea = false
        GestorGuerras.GremiosEnGuerra.remove(this)
        Integrantes.forEach {
            it.fullPDV()
            it.teleport(it.MapaAnteriorPVP.id)
            it.addEnergiaConIm(3000, true)
        }
        Integrantes.clear()
        recaudadores.clear()
        if (anuncio) anunciar(victoria)
    }

    fun filtrarIntegrantes() {
        Integrantes = Integrantes.filter {
            (anunciarsalida(it, false))
        } as ArrayList<Personaje>
    }

    fun filtrarRecaudadores(): Boolean {
        recaudadores = gremio._recaudadores.filter { it.pelea == null }.take(3) as ArrayList<Recaudador>
        return recaudadores.size != AtlantaMain.GUERRA_RECAUDADORES
    }

    fun cumpleintegrantes(): Boolean {
        filtrarIntegrantes()
        return Integrantes.size == AtlantaMain.GUERRA_PERSONAJES
    }

    fun anunciarsalida(perso: Personaje?, forzado: Boolean): Boolean {
        if (perso == null || perso.pelea != null || !perso.enLinea() || forzado) {
            GestorSalida.ENVIAR_ANUNCIO_CHAT_MENSAJE_GREMIO(
                gremio,
                "El jugador ${perso?.nombre} se ha retirado, no participara en guerra."
            )
            if (GestorGuerras.GremiosEnGuerra.contains(this)) GestorGuerras.GremiosEnGuerra.remove(this)
            return false
        }
        return true
    }

    fun anunciar(victoria: Boolean = false, anulada: Boolean = false) {
        if (Integrantes.isEmpty()) {
            GestorSalida.ENVIAR_ANUNCIO_CHAT_MENSAJE_GREMIO(
                gremio,
                "La guerra ha finalizado... ${if (!anulada) {
                    if (victoria) "HEMOS GANADO!!!" else "lamentablemente... hemos perdido"
                } else "Debido a que no hay guerreros presentes"}"
            )
            return
        }
        when {
            !cumpleintegrantes() -> {
                GestorSalida.ENVIAR_ANUNCIO_CHAT_MENSAJE_GREMIO(
                    gremio, "EL GREMIO RECLUTA PARA LA GUERRA DE GREMIOS, FALTAN" +
                            " ${AtlantaMain.GUERRA_PERSONAJES - Integrantes.size} INTEGRANTES"
                )
            }
            else -> {
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("EL GREMIO ${this.gremio.nombre} ESTA BUSCANDO UN CONTRINCANTE PARA LA GUERRA DE GREMIOS")
                GestorGuerras.GremiosEnGuerra.add(this)
            }
        }

    }

    fun yaexisteIP(perso: Personaje?): Boolean {
        val pj = perso ?: return false
        val ip = pj.servidorSocket?.actualIP ?: return false
        return Integrantes.any { it.servidorSocket?.actualIP == ip }
    }

    fun AgregarIntegrante(perso: Personaje?): Boolean {
        if (perso == null) {
            return false
        }
        if (!perso.EnLinea) {
            return false
        }
        if (perso.pelea != null) {
            return false
        }
        if (yaexisteIP(perso)) {
            perso.enviarmensajeRojo("Ya hay un personaje inscrito con tu IP")
            return false
        }
        if (cumpleintegrantes()) return false
//        if (Integrantes.isEmpty() && perso.miembroGremio.puede(Constantes.G_RECOLECTAR_RECAUDADOR)) return false
        if (Integrantes.isEmpty()) {
            if (gremio._recaudadores.filter { it.pelea == null }.size < AtlantaMain.GUERRA_RECAUDADORES) {
                perso.enviarmensajeRojo(
                    "Tu gremio no tiene recaudadores suficientes, le faltan " +
                            "${AtlantaMain.GUERRA_RECAUDADORES - gremio._recaudadores.filter { it.pelea == null }.size} Recaudador(es)"
                )
                return false
            }
            setLider(
                perso
            )
            thread = thread { verificador() }
        } else {
            Integrantes.add(perso)
        }
        anunciar(anulada = true)
        return true
    }

    fun RemoverIntegrante(perso: Personaje?) {
        if (perso == null) {
            return
        }
        if (Integrantes.contains(perso)) {
            Integrantes = Integrantes.filter { it != perso } as ArrayList<Personaje>
            anunciarsalida(perso, true)
            if (perso.enLinea()) {
                perso.enviarmensajeRojo("Has salido de la guerra de gremios")
            }
        }
    }
}