package variables.hechizo

import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Mundo.getHechizo
import variables.mapa.Celda
import variables.pelea.Luchador
import variables.pelea.Pelea
import java.util.*

class Buff(
    efectoIDint: Int, hechizoID: Int, desbufeable: Boolean, turnos: Int,
    lanzador: Luchador, args: String, tipo: TipoDaño
) : EfectoHechizo(hechizoID) {
    val lanzador: Luchador

    // por el momento las condiciones son
// SOIN, BN,DN,DE,DA,DW,DF,-PA,-PM,PA,PM
    private var _tipo: TipoDaño? = null
    var turnosRestantesOriginal: Int
        private set
    private var _desbuffeable: Boolean
    var condicionBuff = ""
        private set

    fun setArgs2(argsString: String) {
        _args = argsString
        val split = _args.split(",".toRegex()).toTypedArray()
        try {
            if (split[0] != "null") {
                primerValor = split[0].toInt()
            } // valor
        } catch (ignored: Exception) {
        }
        try {
            if (split[1] != "null") {
                segundoValor = split[1].toInt() // valor max
            }
        } catch (ignored: Exception) {
        }
        try {
            if (split[2] != "null") {
                tercerValor = split[2].toInt()
            }
        } catch (ignored: Exception) {
        }
    }

    fun setCondBuff(condicion: String) {
        condicionBuff = condicion.toUpperCase()
    }

    fun getTurnosRestantes(puedeJugar: Boolean): Int {
        return if (!puedeJugar || turnosRestantesOriginal <= -1) {
            turnosRestantesOriginal
        } else turnosRestantesOriginal - 1
    }

    fun disminuirTurnosRestantes(): Int {
        if (turnosRestantesOriginal > 0) {
            turnosRestantesOriginal--
        }
        return turnosRestantesOriginal
    }

    fun esDesbufeable(): Boolean {
        return _desbuffeable
    }

    fun setDesbufeable(b: Boolean) {
        _desbuffeable = b
    }

    fun aplicarBuffDeInicioTurno(pelea: Pelea?, objetivo: Luchador) {
        try {
            val obj2 = ArrayList<Luchador>()
            obj2.add(objetivo)
            when (efectoID) {
                85, 86, 87, 88, 89 -> {
                    if (objetivo.mob != null) {
                        if (objetivo.mob!!.mobModelo.id != 423) {
                            if (pelea != null) {
                                _tipo?.let { efecto_Daños_Porc_Elemental(obj2, pelea, lanzador, it, false) }
                            }
                        }
                    } else {
                        if (pelea != null) {
                            _tipo?.let { efecto_Daños_Porc_Elemental(obj2, pelea, lanzador, it, false) }
                        }
                    }
                    return
                }
                91, 92, 93, 94, 95 -> {
                    if (objetivo.mob != null) {
                        if (objetivo.mob!!.mobModelo.id != 423) {
                            if (pelea != null) {
                                _tipo?.let { efecto_Roba_PDV_Elemental(obj2, pelea, lanzador, it, false) }
                            }
                        }
                    } else {
                        if (pelea != null) {
                            _tipo?.let { efecto_Roba_PDV_Elemental(obj2, pelea, lanzador, it, false) }
                        }
                    }
                    return
                }
                96, 97, 98, 99, 100 -> {
                    if (objetivo.mob != null) {
                        if (objetivo.mob!!.mobModelo.id != 423) {
                            if (pelea != null) {
                                _tipo?.let { efecto_Daños_Elemental(obj2, pelea, lanzador, it, false) }
                            }
                        }
                    } else {
                        if (pelea != null) {
                            _tipo?.let { efecto_Daños_Elemental(obj2, pelea, lanzador, it, false) }
                        }
                    }
                    return
                }
                81, 108 -> {
                    if (pelea != null) {
                        efecto_Cura(obj2, pelea, lanzador, _tipo)
                    }
                    return
                }
                301, 787 -> {
                    aplicarHechizoDeBuff(pelea, objetivo, objetivo.celdaPelea)
                    return
                }
            }
        } catch (e: Exception) {
            redactarLogServidorln("EXCEPTION BUFF INICIO, HECHIZO:$hechizoID, ARGS:$_args")
            e.printStackTrace()
        }
    }

    fun aplicarBuffCondicional(objetivo: Luchador) {
        val objetivos = ArrayList<Luchador>()
        objetivos.add(objetivo)
        val c = condicionBuff
        condicionBuff = ""
        objetivo.celdaPelea?.let {
            _tipo?.let { it1 ->
                aplicarEfecto(
                    objetivo.pelea, objetivo, objetivos, it,
                    it1, false
                )
            }
        }
        condicionBuff = c
    }

    fun aplicarHechizoDeBuff(pelea: Pelea?, objetivo: Luchador?, celdaObjetivo: Celda?) {
        val hechizo = getHechizo(primerValor) ?: return
        val sh = hechizo.getStatsPorNivel(segundoValor) ?: return
        if (pelea != null) {
            if (objetivo != null) {
                if (celdaObjetivo != null) {
                    Hechizo.aplicaHechizoAPelea(
                        pelea,
                        objetivo,
                        celdaObjetivo,
                        sh.efectosNormales,
                        TipoDaño.NORMAL,
                        false
                    )
                }
            }
        }
    }

    init {
        efectoID = efectoIDint
        _desbuffeable = desbufeable
        turnosRestantesOriginal = if (turnos <= -1) -3 else turnos
        duracion = 0
        this.lanzador = lanzador
        _tipo = if (tipo == TipoDaño.GLIFO || tipo == TipoDaño.TRAMPA) {
            tipo
        } else {
            TipoDaño.POST_TURNOS
        }
        setArgs2(args)
    }
}