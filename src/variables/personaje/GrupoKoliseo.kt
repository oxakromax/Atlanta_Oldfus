package variables.personaje

import estaticos.AtlantaMain
import estaticos.GestorSalida.ENVIAR_kM_AGREGAR_PJ_KOLISEO
import estaticos.GestorSalida.ENVIAR_kM_EXPULSAR_PJ_KOLISEO
import estaticos.GestorSalida.ENVIAR_kV_DEJAR_KOLISEO
import java.util.*

class GrupoKoliseo(koli1: Personaje) {
    private val _kolis = ArrayList<Personaje>()
    val puntuacion: Int
        get() {
            var punt = 0
            for (p in _kolis) {
                punt += p.puntoKoli
            }
            return punt
        }

    fun dejarGrupo(p: Personaje) {
        if (!_kolis.contains(p)) {
            return
        }
        p.grupoKoliseo = null
        _kolis.remove(p)
        try {
            if (_kolis.size == 1) {
                _kolis[0].grupoKoliseo = null
                ENVIAR_kV_DEJAR_KOLISEO(_kolis[0])
            } else {
                ENVIAR_kM_EXPULSAR_PJ_KOLISEO(this, p.Id)
            }
        } catch (ignored: Exception) {
        }
    }

    fun limpiarGrupo() {
        for (p in _kolis) {
            p.grupoKoliseo = null
            ENVIAR_kV_DEJAR_KOLISEO(p)
        }
        _kolis.clear()
    }

    val cantPjs: Int
        get() = _kolis.size

    fun addPersonaje(koli: Personaje): Boolean {
        if (_kolis.size >= AtlantaMain.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO) {
            return false
        }
        if (AtlantaMain.RANGO_NIVEL_KOLISEO > 0) {
            for (p in _kolis) {
                if (p.nivel > koli.nivel + AtlantaMain.RANGO_NIVEL_KOLISEO) {
                    return false
                }
                if (p.nivel < koli.nivel - AtlantaMain.RANGO_NIVEL_KOLISEO) {
                    return false
                }
            }
        }
        ENVIAR_kM_AGREGAR_PJ_KOLISEO(this, koli.stringInfoGrupo())
        _kolis.add(koli)
        return true
    }

    val miembros: ArrayList<Personaje>
        get() = ArrayList(_kolis)

    fun contieneIPOtroGrupo(grupo: GrupoKoliseo): Boolean {
        if (AtlantaMain.PARAM_PERMITIR_MULTICUENTA_PELEA_KOLISEO) {
            return false
        }
        for (p in _kolis) {
            for (p2 in grupo._kolis) {
                if (p.cuenta.actualIP.equals(p2.cuenta.actualIP, ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }

    init {
        _kolis.add(koli1)
    }
}