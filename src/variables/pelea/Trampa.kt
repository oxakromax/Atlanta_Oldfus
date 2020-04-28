package variables.pelea

import estaticos.Constantes
import estaticos.Encriptador
import estaticos.GestorSalida.ENVIAR_GA_ACCION_PELEA
import estaticos.GestorSalida.ENVIAR_GDC_ACTUALIZAR_CELDA_A_LUCHADORES
import estaticos.GestorSalida.ENVIAR_GDZ_COLOREAR_ZONA_A_LUCHADORES
import variables.hechizo.EfectoHechizo.TipoDaño
import variables.hechizo.Hechizo
import variables.hechizo.StatHechizo
import variables.mapa.Celda
import java.util.*

class Trampa(
    private val _pelea: Pelea, val lanzador: Luchador, val celda: Celda, val tamaño: Byte,
    private val _trampaSH: StatHechizo, private val _hechizoID: Int, mostrar: ArrayList<Luchador>,
    celdas: ArrayList<Celda>, val color: Int
) : Comparable<Trampa> {
    private val _visibles = ArrayList<Int>()
    private val _celdas: ArrayList<Celda>
    private var _paramEquipoDueño: Byte = -1

    val paramEquipoDueño: Int
        get() = _paramEquipoDueño.toInt()

    fun esInvisiblePara(idMirador: Int): Boolean {
        return if (idMirador != 0) {
            !_visibles.contains(idMirador)
        } else true
    }

    fun activarTrampa(victima: Luchador?) {
        if (_pelea.fase != Constantes.PELEA_FASE_COMBATE) {
            return
        }
        _pelea.borrarTrampa(this)
        for (c in _celdas) {
            c.borrarTrampa(this)
        }
        desaparecer()
        if (victima == null) {
            return
        }
        ENVIAR_GA_ACCION_PELEA(
            _pelea, 7, 306, victima.id.toString() + "", _hechizoID.toString() + "," + celda.id
                    + ",0,1,1," + lanzador.id
        )
        if (!victima.estaMuerto()) {
            Hechizo.aplicaHechizoAPelea(_pelea, lanzador, celda, _trampaSH.efectosNormales, TipoDaño.TRAMPA, false)
            if (_pelea.luchadorTurno!!.ia != null) {
                _pelea.luchadorTurno!!.ia!!.nullear()
            }
        }
    }

    fun GetSH(): StatHechizo {
        return _trampaSH
    }

    fun aparecer(luchadores: ArrayList<Luchador>) {
        for (luchador in luchadores) {
            if (!_visibles.contains(luchador.id)) {
                _visibles.add(luchador.id)
            }
        }
        ENVIAR_GDZ_COLOREAR_ZONA_A_LUCHADORES(luchadores, "+", celda.id, tamaño.toInt(), color, ' ')
        val permisos = BooleanArray(16)
        val valores = IntArray(16)
        permisos[2] = true
        permisos[0] = true
        valores[2] = 25
        valores[0] = 1
        ENVIAR_GDC_ACTUALIZAR_CELDA_A_LUCHADORES(
            luchadores, celda.id, Encriptador.stringParaGDC(
                permisos, valores
            ), false
        )
    }

    private fun desaparecer() {
        val luchadores = ArrayList<Luchador>()
        for (i in _visibles) {
            if (_pelea.getLuchadorPorID(i) != null) {
                luchadores.add(_pelea.getLuchadorPorID(i)!!)
            }
        }
        ENVIAR_GDZ_COLOREAR_ZONA_A_LUCHADORES(luchadores, "-", celda.id, tamaño.toInt(), color, ' ')
        val permisos = BooleanArray(16)
        val valores = IntArray(16)
        permisos[2] = true
        permisos[0] = true
        ENVIAR_GDC_ACTUALIZAR_CELDA_A_LUCHADORES(
            luchadores, celda.id, Encriptador.stringParaGDC(
                permisos, valores
            ), false
        )
    }

    private val prioridad: Int
        get() {
            var p = 0
            for (eh in _trampaSH.efectosNormales) {
                if (eh.efectoID == 5) {
                    p = 10
                }
            }
            return p
        }

    override fun compareTo(other: Trampa): Int {
        return prioridad.compareTo(other.prioridad)
    }

    // private ArrayList<Luchador> _objetivos;
    init {
        _paramEquipoDueño = lanzador.paramEquipoAliado
        _pelea.addTrampa(this)
        _celdas = celdas
        for (c in celdas) {
            c.addTrampa(this)
        }
        aparecer(mostrar)
    }
}