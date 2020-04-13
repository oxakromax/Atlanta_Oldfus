package variables.personaje

import estaticos.GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA_SIN_HUMO
import estaticos.GestorSalida.ENVIAR_IC_BORRAR_BANDERA_COMPAS
import estaticos.GestorSalida.ENVIAR_IH_COORDENADAS_UBICACION
import estaticos.GestorSalida.ENVIAR_PF_SEGUIR_PERSONAJE
import estaticos.GestorSalida.ENVIAR_PM_EXPULSAR_PJ_GRUPO
import estaticos.GestorSalida.ENVIAR_PV_DEJAR_GRUPO
import variables.pelea.Pelea
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class Grupo {
    val miembros = CopyOnWriteArrayList<Personaje>()
    val alumnos = CopyOnWriteArrayList<Personaje>()
    var rastrear: Personaje? = null
    private var _packet: String? = null
    var autoUnir = true
    fun esLiderGrupo(perso: Personaje): Boolean {
        return if (miembros.isEmpty()) {
            false
        } else miembros[0].Id == perso.Id
    }

    fun activarMaestro(slaveON: Boolean, soloVIP: Boolean) {
        for (perso in miembros) {
            if (esLiderGrupo(perso)) {
                continue
            }
            if (soloVIP && !perso.esAbonado()) {
                continue
            }
            if (slaveON) {
                if (!alumnos.contains(perso)) {
                    alumnos.add(perso)
                    perso.enviarmensajeRojo("Esclavo Activado!")
                }
            } else {
                if (alumnos.remove(perso)) {
                    perso.enviarmensajeRojo("Esclavo Desactivado!")
                }
            }
        }
    }

    fun addIntegrante(perso: Personaje) {
        if (miembros.contains(perso)) {
            return
        }
        miembros.add(perso)
        perso.setGrupo(this)
        if (!liderGrupo!!.Busquedagrupo.equals("0", ignoreCase = true) && miembros.size == 8) {
            liderGrupo!!.Busquedagrupo = "0"
        }
    }

    fun addAlumno(perso: Personaje): Boolean {
        if (!miembros.contains(perso)) {
            return false
        }
        if (esLiderGrupo(perso)) {
            return false
        }
        return if (alumnos.contains(perso)) {
            alumnos.remove(perso)
            false
        } else {
            alumnos.add(perso)
            true
        }
    }

    val iDsPersos: ArrayList<Int>
        get() {
            val lista = ArrayList<Int>()
            for (perso in miembros) {
                lista.add(perso.Id)
                if (perso.compañero != null && perso.compañero.esMultiman()) {
                    lista.add(perso.compañero.Id)
                }
            }
            return lista
        }

    val nivelGrupo: Int
        get() {
            var nivel = 0
            for (p in miembros) {
                nivel += p.nivel
            }
            return nivel
        }


    val liderGrupo: Personaje?
        get() = if (miembros.isEmpty()) {
            null
        } else miembros[0]

    fun dejarGrupo(expulsado: Personaje, expLider: Boolean) {
        expulsado.Busquedagrupo = "0"
        if (!miembros.contains(expulsado)) {
            return
        }
        if (rastrear === expulsado) {
            rastrear = null
            for (perso in miembros) {
                ENVIAR_IC_BORRAR_BANDERA_COMPAS(perso!!)
                ENVIAR_PF_SEGUIR_PERSONAJE(perso, "-")
            }
        }
        if (expulsado.enLinea()) {
            ENVIAR_PV_DEJAR_GRUPO(expulsado, if (expLider) liderGrupo!!.Id.toString() + "" else "")
            ENVIAR_IH_COORDENADAS_UBICACION(expulsado, "")
        }
        if (liderGrupo === expulsado) {
            alumnos.clear()
        }
        expulsado.setGrupo(null)
        miembros.remove(expulsado)
        alumnos.remove(expulsado)
        if (miembros.size == 1) {
            dejarGrupo(miembros[0], false)
            //			if (!_integrantes.get(0).get_busquedagrupo().equalsIgnoreCase("0")){
//				_integrantes.get(0).set_busquedagrupo("0");
//			}
        } else if (miembros.size >= 2) {
            ENVIAR_PM_EXPULSAR_PJ_GRUPO(this, expulsado.Id)
        }
    }

    @Synchronized
    private fun ejecutarPacket(packet: String?) {
        if (liderGrupo == null) {
            return
        }
        loop@ for (p in alumnos) {
            when (packet!![0]) {
                'G' -> {
                    if (p.mapa != liderGrupo!!.mapa) {
                        continue@loop
                    }
                    when (packet[1]) {
                        'A' -> if (p.celda != liderGrupo!!.celda) {
                            p.celda = liderGrupo!!.celda
                            ENVIAR_GM_REFRESCAR_PJ_EN_MAPA_SIN_HUMO(p.mapa, p)
                        }
                        'K' -> {
                        }
                    }
                }
                'W', 'w', 'D' -> {
                }
            }
            try {
                p.cuenta.socket?.analizar_Packets(packet)
            } catch (ignored: Exception) {
            }
        }
    }

    fun tieneAlumnos(): Boolean {
        return !alumnos.isEmpty()
    }

    @Synchronized
    private fun teleport(id: Short, cell: Short) {
        if (liderGrupo == null) {
            return
        }
        for (p in alumnos) {
            try {
                if (p.Id == liderGrupo!!.Id) {
                    continue
                }
                if (p.pelea != null) {
                    continue
                }
                p.teleport(id, cell)
            } catch (ignored: Exception) {
            }
        }
    }

    @Synchronized
    fun teleportATodos(id: Short, cell: Short) {
        if (alumnos.isEmpty()) {
            return
        }
        val thread = Thread(Runnable { teleport(id, cell) })
        thread.start()
    }

    @Synchronized
    fun unirAPelea(pelea: Pelea) {
        if (alumnos.isEmpty() || !autoUnir) {
            return
        }
        _packet = "GA903" + pelea.ID + ";" + liderGrupo!!.Id
        val thread: Thread = object : Thread() {
            val packet2 = _packet!!
            override fun run() { //				try {
//					Thread.sleep(500);
//				} catch (Exception e) {}
                ejecutarPacket(packet2)
            }
        }
        thread.start()
    }

    @Synchronized
    fun packetSeguirLider(packet: String?) {
        if (alumnos.isEmpty()) {
            return
        }
        _packet = packet
        val thread: Thread = object : Thread() {
            val packet2 = _packet
            override fun run() {
                ejecutarPacket(packet2)
            }
        }
        thread.start()
    }
}