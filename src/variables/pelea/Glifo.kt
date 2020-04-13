package variables.pelea

import estaticos.Constantes
import estaticos.GestorSalida.ENVIAR_GA_ACCION_PELEA
import estaticos.GestorSalida.ENVIAR_GDZ_COLOREAR_ZONA_EN_PELEA
import variables.hechizo.EfectoHechizo.TipoDaño
import variables.hechizo.Hechizo
import variables.hechizo.StatHechizo
import variables.mapa.Celda
import java.util.*

class Glifo(
    private val _pelea: Pelea,
    val lanzador: Luchador,
    val celda: Celda,
    val tamaño: Byte,
    private val _glifoSH: StatHechizo,
    var duracion: Int,
    private val _hechizoID: Int,
    private val _inicioTurno: Boolean,
    celdas: ArrayList<Celda>,
    val color: Int,
    forma: Char
) {
    private val _celdas: ArrayList<Celda>
    val forma: Char
    fun esInicioTurno(): Boolean {
        return _inicioTurno
    }

    fun disminuirDuracion(): Int {
        duracion--
        return duracion
    }

    fun activarGlifo(glifeado: Luchador) {
        if (_pelea.fase != Constantes.PELEA_FASE_COMBATE) {
            return
        }
        ENVIAR_GA_ACCION_PELEA(
            _pelea, 7, 307, glifeado.id.toString() + "", _hechizoID.toString() + "," + celda.id
                    + ",0,1,1," + lanzador.id
        )
        //		try {
//			Thread.sleep(100);
//		} catch (Exception e) {}
        glifeado.celdaPelea?.let {
            Hechizo.aplicaHechizoAPelea(
                _pelea, lanzador, it, _glifoSH.efectosNormales,
                TipoDaño.GLIFO, false
            )
        }
        // _pelea.acaboPelea((byte) 3);
    }

    fun desaparecer() {
        _pelea.borrarGlifo(this)
        for (c in _celdas) {
            c.borrarGlifo(this)
        }
        ENVIAR_GDZ_COLOREAR_ZONA_EN_PELEA(_pelea, 7, "-", celda.id, tamaño.toInt(), color, ' ')
        // GestorSalida.ENVIAR_GDC_ACTUALIZAR_CELDA_EN_PELEA(_pelea, 7, _celda.getID(),
// "Haaaaaaaaa3005", false);
    }

    init {
        _pelea.addGlifo(this)
        _celdas = celdas
        this.forma = forma
        for (c in celdas) {
            c.addGlifo(this)
        }
        ENVIAR_GDZ_COLOREAR_ZONA_EN_PELEA(_pelea, 7, "+", celda.id, tamaño.toInt(), color, this.forma)
        // GestorSalida.ENVIAR_GDC_ACTUALIZAR_CELDA_EN_PELEA(pelea, 7, celda.getID(), "Haaaaaaaaa3005",
// false);
    }
}