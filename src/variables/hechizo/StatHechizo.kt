package variables.hechizo

import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Constantes
import estaticos.Constantes.convertirStringArray
import estaticos.Constantes.getInflDañoPorEfecto
import estaticos.Constantes.prioridadEfecto
import estaticos.Formulas.getRandomInt
import estaticos.Inteligencia
import estaticos.Mundo.getHechizo
import variables.mapa.Mapa
import variables.pelea.Luchador
import java.util.*
import java.util.regex.Pattern

// private final ArrayList<Integer> _afectados = new ArrayList<Integer>();
class StatHechizo(
    hechizoID: Int, grado: Int, costePA: Byte, minAlc: Byte, maxAlc: Byte,
    probGC: Short, probFC: Short, lanzarLinea: Boolean, lineaDeVista: Boolean, esCeldaVacia: Boolean,
    esModifAlc: Boolean, maxLanzPorTurno: Byte, maxLanzPorObjetivo: Byte, sigLanzamiento: Byte,
    reqLevel: Int, esFinTurnoSiFC: Boolean, estadosProhibidos: String, estadosNecesarios: String,
    tipoHechizo: Byte, necesitaObjetivo: Boolean
) {
    val grado // nivel
            : Int
    val costePA // coste de PA
            : Byte
    val minAlc // minimo alcance
            : Byte
    val maxAlc // maximo alcance
            : Byte
    val probabilidadGC // probabilidad de golpe critico
            : Short
    val probabilidadFC // probabilidad de fallo critico
            : Short
    private val _lanzarLinea // lanzar en linea
            : Boolean
    private val _lineaDeVista // linea de vuelo
            : Boolean
    private val _necesarioCeldaLibre // celda vacia
            : Boolean
    private val _alcModificable // alcance modificalble
            : Boolean
    private var _necesitaObjetivo: Boolean // alcance modificalble = false
    val maxLanzPorTurno // cantidad de veces por turno
            : Byte
    val maxLanzPorObjetivo // cantidad de veces por objetivo
            : Byte
    val sigLanzamiento // cantidad de turnos para volver a lanzar el hechizo
            : Byte
    val nivelRequerido // nivel requerido
            : Int
    private val _esFinTurnoSiFC // si falla, es final del turno
            : Boolean
    private val _efectosNormales = ArrayList<EfectoHechizo>()
    private val _efectosCriticos = ArrayList<EfectoHechizo>()
    val efectosNormales = ArrayList<EfectoHechizo>()
    val efectosCriticos = ArrayList<EfectoHechizo>()

    // private final String _areaEfecto;// genera un estado, tipo portador
    val estadosProhibido = ArrayList<Int>()
    val estadosNecesario = ArrayList<Int>()
    val hechizo: Hechizo?
    val tipo // 0 normal, 1 pergamino, 2 invocacion, 3 dominios, 4 de clase, 5
            : Byte

    // de recaudador
    private var _trampa = false
    private var _intercambioPos = false
    private var _soloMover = false
    private var _automaticoAlFinalTurno = false

    // public boolean esHechizoParaAliados() {
// return _hechizo.getValorIA() != 2;
// }
//
// public boolean esHechizoParaEnemigos() {
// return _hechizo.getValorIA() != 1;
// }
    fun filtroValorIA(tipo: Inteligencia.Accion?, c: Char): Int {
        val valorIA = hechizo!!.valorIA
        if (valorIA == 0) {
            return 0
        }
        var v = 0
        if (tipo != null) {
            when (tipo) {
                Inteligencia.Accion.ATACAR -> v = 1
                Inteligencia.Accion.BOOSTEAR -> v = 2
                Inteligencia.Accion.CURAR -> v = 3
                Inteligencia.Accion.TRAMPEAR -> v = 4
                Inteligencia.Accion.INVOCAR -> v = 5
                Inteligencia.Accion.TELEPORTAR -> v = 6
                Inteligencia.Accion.NADA -> {
                }
            }
        }
        if (Math.abs(valorIA) != v) {
            return -1
        }
        if (c != ' ') {
            if (c == '+' && valorIA < 0) {
                return -1
            } else if (c == '-' && valorIA > 0) {
                return -1
            }
        }
        return 1
    }

    fun esTrampa(): Boolean {
        return _trampa
    }

    fun esSoloMover(): Boolean {
        return _soloMover
    }

    fun esIntercambioPos(): Boolean {
        return _intercambioPos
    }

    fun esAutomaticoAlFinalTurno(): Boolean {
        return _automaticoAlFinalTurno
    }

    private fun fijarEfectos(efectoID: Int) {
        if (efectoID == 8) {
            _intercambioPos = true
        }
        _soloMover = efectoID == 5 || efectoID == 6
        if (efectoID == 400) {
            _trampa = true
        }
        if (efectoID == 300) {
            _automaticoAlFinalTurno = true
        }
    }

    fun analizarEfectos(
        efectosN: String,
        efectosC: String,
        zonaEfecto: String,
        hechizoID: Int
    ) {
        var num = 0
        var splt = convertirStringArray(efectosN)
        for (b in splt) {
            var a = b
            try {
                if (a == "null" || a.isEmpty()) {
                    continue
                }
                a = a.replace('[', ' ').replace(']', ' ').replace(" ", "")
                val efectoID = a.split(",".toRegex()).toTypedArray()[0].toInt()
                val args = a.split(",".toRegex(), 2).toTypedArray()[1]
                val eh = EfectoHechizo(
                    efectoID, args, hechizoID, grado, zonaEfecto.substring(
                        num * 2, num * 2
                                + 2
                    )
                )
                _efectosNormales.add(eh)
                fijarEfectos(efectoID)
                num++
            } catch (e: Exception) {
                redactarLogServidorln("[BUG HECHIZO ID] $hechizoID : $efectosN")
                e.printStackTrace()
                System.exit(1)
                return
            }
        }
        _efectosNormales.trimToSize()
        splt = convertirStringArray(efectosC)
        for (b in splt) {
            var a = b
            try {
                if (a == "null" || a.isEmpty()) {
                    continue
                }
                a = a.replace('[', ' ').replace(']', ' ').replace(" ", "")
                val efectoID = a.split(",".toRegex()).toTypedArray()[0].toInt()
                val args = a.split(",".toRegex(), 2).toTypedArray()[1]
                val eh = EfectoHechizo(
                    efectoID, args, hechizoID, grado, zonaEfecto.substring(
                        num * 2, num * 2
                                + 2
                    )
                )
                _efectosCriticos.add(eh)
                fijarEfectos(efectoID)
                num++
            } catch (e: Exception) {
                redactarLogServidorln("[BUG HECHIZO ID] $hechizoID : $efectosC")
                e.printStackTrace()
                System.exit(1)
                return
            }
        }
        _efectosCriticos.trimToSize()
        ordenar()
    }

    private fun setValorAfectado(eh: EfectoHechizo, normales: Array<String>, i: Int) {
        var afectado = 0
        var afectadoCond = 0
        if (i < normales.size && !normales[i].isEmpty()) {
            val s =
                normales[i].toUpperCase().split(Pattern.quote("*").toRegex()).toTypedArray()
            var a = ""
            if (s.size > 1) {
                a = s[1]
            }
            try {
                afectado = s[0].toInt()
            } catch (e: Exception) {
                if (a.isEmpty()) {
                    a = s[0]
                }
            }
            try {
                if (a.contains("D_")) {
                    val ele = a.replace("D_", "").split("".toRegex()).toTypedArray()
                    for (e in ele) {
                        when (e) {
                            "A" -> afectadoCond = afectadoCond or 1 shl Constantes.ELEMENTO_AIRE.toInt()
                            "W" -> afectadoCond = afectadoCond or 1 shl Constantes.ELEMENTO_AGUA.toInt()
                            "F" -> afectadoCond = afectadoCond or 1 shl Constantes.ELEMENTO_FUEGO.toInt()
                            "E" -> afectadoCond = afectadoCond or 1 shl Constantes.ELEMENTO_TIERRA.toInt()
                            "N" -> afectadoCond = afectadoCond or 1 shl Constantes.ELEMENTO_NEUTRAL.toInt()
                        }
                    }
                }
            } catch (ignored: Exception) {
            }
        }
        eh.afectados = afectado
        eh.afectadosCond = afectadoCond
    }

    fun setAfectados(normales: Array<String>, criticos: Array<String>) {
        for (i in _efectosNormales.indices) {
            val eh = _efectosNormales[i]
            setValorAfectado(eh, normales, i)
        }
        for (i in _efectosCriticos.indices) {
            val eh = _efectosCriticos[i]
            setValorAfectado(eh, criticos, i)
        }
    }

    fun setCondiciones(normales: Array<String>, criticos: Array<String>) {
        for (i in _efectosNormales.indices) {
            val eh = _efectosNormales[i]
            if (i < normales.size && !normales[i].isEmpty()) {
                eh.setCondicion(normales[i])
            }
        }
        for (i in _efectosCriticos.indices) {
            val eh = _efectosCriticos[i]
            if (i < criticos.size && !criticos[i].isEmpty()) {
                eh.setCondicion(criticos[i])
            }
        }
    }

    private fun ordenar() {
        efectosNormales.clear()
        efectosNormales.addAll(_efectosNormales)
        efectosNormales.sortWith(CompPrioridad())
        efectosNormales.trimToSize()
        efectosCriticos.clear()
        efectosCriticos.addAll(_efectosCriticos)
        efectosCriticos.sortWith(CompPrioridad())
        efectosCriticos.trimToSize()
    }

    val hechizoID: Int
        get() = hechizo!!.iD

    val spriteID: Int
        get() = hechizo!!.spriteID

    val spriteInfos: String
        get() = hechizo!!.spriteInfos

    fun esLanzarLinea(): Boolean {
        return _lanzarLinea
    }

    fun esLineaVista(): Boolean {
        return _lineaDeVista
    }

    fun esNecesarioCeldaLibre(): Boolean {
        return _necesarioCeldaLibre
    }

    fun esAlcanceModificable(): Boolean {
        return _alcModificable
    }

    fun esNecesarioObjetivo(): Boolean {
        return _necesitaObjetivo
    }

    fun esFinTurnoSiFC(): Boolean {
        return _esFinTurnoSiFC
    }

    fun beneficio(lanzador: Luchador, mapa: Mapa?, idCeldaObjetivo: Short, objetivo: Luchador?): Int {
        var efectos = _efectosNormales
        if (!_efectosCriticos.isEmpty()) {
            efectos = _efectosCriticos
        }
        var suerte = 0
        var suerteMax = 0
        var azar = 0
        var cantidad = 0
        var tiene666 = false
        var filtrarSuerte = false
        for (EH in efectos) {
            if (EH.efectoID == 666 && EH.suerte > 0) {
                tiene666 = true
            }
            if (EH.suerte == 0) {
                filtrarSuerte = true
            }
            suerteMax += EH.suerte
        }
        if (suerteMax > 0) {
            azar = getRandomInt(1, suerteMax)
        }
        for (EH in efectos) {
            if (EH.suerte > 0) {
                if (filtrarSuerte || tiene666) {
                    continue
                }
                if (azar < suerte || azar >= EH.suerte + suerte) {
                    suerte += EH.suerte
                    continue
                }
                suerte += EH.suerte
            }
            val listaLuchadores =
                Hechizo.getObjetivosEfecto(mapa, lanzador, EH, idCeldaObjetivo)
            val estima = getInflDañoPorEfecto(
                EH.efectoID, lanzador, objetivo!!, EH.valorParaPromediar,
                idCeldaObjetivo, this
            )
            for (L in listaLuchadores) {
                if (estima > 0) {
                    if (L.equipoBin != lanzador.equipoBin) {
                        cantidad++
                    } else {
                        cantidad--
                    }
                } else if (estima < 0) {
                    if (L.equipoBin == lanzador.equipoBin) {
                        cantidad++
                    } else {
                        cantidad--
                    }
                }
            }
        }
        return cantidad
    }

    fun listaObjetivosAfectados(
        lanzador: Luchador?, mapa: Mapa?,
        celdaObjetivoID: Short
    ): ArrayList<Luchador> {
        val objetivos = ArrayList<Luchador>()
        var efectos = _efectosNormales
        if (!_efectosCriticos.isEmpty()) {
            efectos = _efectosCriticos
        }
        var suerte = 0
        var suerteMax = 0
        var azar = 0
        var tiene666 = false
        var filtrarSuerte = false
        for (EH in efectos) {
            if (EH.efectoID == 666 && EH.suerte > 0) {
                tiene666 = true
            }
            if (EH.suerte == 0) {
                filtrarSuerte = true
            }
            suerteMax += EH.suerte
        }
        if (suerteMax > 0) {
            azar = getRandomInt(1, suerteMax)
        }
        for (EH in efectos) {
            if (EH.suerte > 0) {
                if (filtrarSuerte || tiene666) {
                    continue
                }
                if (azar < suerte || azar >= EH.suerte + suerte) {
                    suerte += EH.suerte
                    continue
                }
                suerte += EH.suerte
            }
            val objs = lanzador?.let { Hechizo.getObjetivosEfecto(mapa, it, EH, celdaObjetivoID) }
            if (objs != null) {
                for (o in objs) {
                    if (!objetivos.contains(o)) {
                        objetivos.add(o)
                    }
                }
            }
        }
        return objetivos
    }

    fun estaDentroAfectados(
        lanzador: Luchador?, objetivo: Luchador?, mapa: Mapa?,
        celdaObjetivoID: Short
    ): Boolean {
        var efectos = _efectosNormales
        if (!_efectosCriticos.isEmpty()) {
            efectos = _efectosCriticos
        }
        var suerte = 0
        var suerteMax = 0
        var azar = 0
        var tiene666 = false
        var filtrarSuerte = false
        for (EH in efectos) {
            if (EH.efectoID == 666 && EH.suerte > 0) {
                tiene666 = true
            }
            if (EH.suerte == 0) {
                filtrarSuerte = true
            }
            suerteMax += EH.suerte
        }
        if (suerteMax > 0) {
            azar = getRandomInt(1, suerteMax)
        }
        for (EH in efectos) {
            if (EH.suerte > 0) {
                if (filtrarSuerte || tiene666) {
                    continue
                }
                if (azar < suerte || azar >= EH.suerte + suerte) {
                    suerte += EH.suerte
                    continue
                }
                suerte += EH.suerte
            }
            val objetivos =
                lanzador?.let { Hechizo.getObjetivosEfecto(mapa, it, EH, celdaObjetivoID) }
            if (objetivos != null) {
                if (objetivos.contains(objetivo)) {
                    return true
                }
            }
        }
        return false
    }

    private class CompPrioridad : Comparator<EfectoHechizo> {
        override fun compare(p1: EfectoHechizo, p2: EfectoHechizo): Int {
            return Integer.compare(
                prioridadEfecto(p1.efectoID),
                prioridadEfecto(p2.efectoID)
            )
        }
    }

    init {
        var esCeldaVacia = esCeldaVacia
        this.grado = grado // nivel
        this.costePA = costePA // coste de PA
        this.minAlc = minAlc // minimo alcance
        this.maxAlc = maxAlc // maximo alcance
        probabilidadGC = probGC // tasa/probabilidad de golpe critico
        probabilidadFC = probFC // tasa/probabilidad de fallo critico
        _lanzarLinea = lanzarLinea // lanzado en linea
        _lineaDeVista = lineaDeVista // linea de vuelo
        if (necesitaObjetivo.also { _necesitaObjetivo = it }) {
            esCeldaVacia = false
        }
        _necesarioCeldaLibre = esCeldaVacia // celda libre
        _alcModificable = esModifAlc // alcance modificable
        this.maxLanzPorTurno = maxLanzPorTurno // cantidad de veces por turno
        this.maxLanzPorObjetivo = maxLanzPorObjetivo // cantidad de veces por objetivo
        this.sigLanzamiento = sigLanzamiento // cantidad de turnos para volver a lanzar el hechizo
        nivelRequerido = reqLevel // nivel requerido
        _esFinTurnoSiFC = esFinTurnoSiFC // si es fallo critico , final de turno
        tipo = tipoHechizo
        hechizo = getHechizo(hechizoID)
        if (!estadosProhibidos.isEmpty()) {
            val estados = convertirStringArray(estadosProhibidos)
            for (esta in estados) {
                if (esta.isEmpty()) {
                    continue
                }
                estadosProhibido.add(esta.toInt())
            }
        }
        if (!estadosNecesarios.isEmpty()) {
            val estados = convertirStringArray(estadosNecesarios)
            for (esta in estados) {
                if (esta.isEmpty()) {
                    continue
                }
                estadosNecesario.add(esta.toInt())
            }
        }
    }
}