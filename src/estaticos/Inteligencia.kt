package estaticos

import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Formulas.getRandomInt
import estaticos.Formulas.randomBoolean
import estaticos.Mundo.Duo
import variables.hechizo.EfectoHechizo
import variables.hechizo.Hechizo
import variables.hechizo.HechizoLanzado
import variables.hechizo.StatHechizo
import variables.mapa.Celda
import variables.mapa.Mapa
import variables.pelea.Luchador
import variables.pelea.Pelea
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.min

class Inteligencia(private val _lanzador: Luchador, private val _pelea: Pelea) : Thread() {
    private val _celdasHechizos: MutableMap<Short, Map<StatHechizo, MutableMap<Celda, ArrayList<Luchador>>>?> =
        LinkedHashMap()
    private val _influencias: MutableMap<Luchador, MutableMap<EfectoHechizo, Int>?> = HashMap()
    private var _fin = false
    private var _resetearCeldasHechizos = false
    private var _resetearInfluencias = false
    private var _refrescarMov = false
    private var _celdasMovimiento = ArrayList<Short>()
    val tipoIA: Int
        get() {
            if (_lanzador == null) {
                return -1
            }
            try {
                if (_lanzador.esDoble()) {
                    return 5
                } else if (_lanzador.recaudador != null) {
                    return 21
                } else if (_lanzador.prisma != null) {
                    return 20
                } else if (_lanzador.mob != null) {
                    return _lanzador.mob!!.mobModelo.tipoIA.toInt()
                } else if (_lanzador.personaje != null) {
                    return 1
                }
            } catch (e: Exception) {
                redactarLogServidorln("EXCEPTION getTipoIA $e")
                e.printStackTrace()
            }
            return -1
        }

    fun forzarRefrescarMov() {
        _refrescarMov = true
    }

    fun nullear() {
        _resetearCeldasHechizos = true
        _resetearInfluencias = true
    }

    // public void destruir() {
// try {
// finalize();
// } catch (final Throwable e) {}
// }
    @Synchronized
    fun arrancar() {
        try {
            if (this.state == State.NEW) {
                start()
            } else if (this.state != State.TERMINATED) {
                interrupt()
            }
        } catch (e: Exception) {
            redactarLogServidorln(
                "Exception ARRANCAR IA tipo: " + tipoIA + ", atacante: "
                        + (if (_lanzador == null) "Null" else _lanzador.nombre) + ", " + (if (_pelea == null) " pelea Null" else "pMapa: " + _pelea.mapaCopia!!.id + " pID: " + _pelea.ID + " pEstado: " + _pelea.fase)
                        + ", Exception " + e.toString()
            )
        }
    }

    // solo funciona cuando se cancela una pelea
    @Synchronized
    fun parar() {
        try {
            _fin = true
            _pelea.removeIA(this)
            interrupt()
        } catch (e: Exception) {
            e.printStackTrace()
            redactarLogServidorln(
                "EXCEPTION parar IA tipo: " + tipoIA + ", atacante: " + (_lanzador.nombre) + ", " + ("pMapa: " + _pelea.mapaCopia!!.id + " pID: " + _pelea.ID + " pEstado: " + _pelea.fase)
                        + ", Exception " + e.toString()
            )
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }

    override fun run() {
        var sTipo = "ninguna"
        try {
            while (!_fin && Mundo.SERVIDOR_ESTADO != Constantes.SERVIDOR_OFFLINE) {
//                if (this.isInterrupted) {
//                    break
//                }
                val tipo = tipoIA
                sTipo = tipo.toString() + ""
                nullear()
                when (tipo) {
                    -1 -> {
                    }
                    0 -> tipo_0()
                    1 -> tipo_1()
                    2 -> tipo_2()
                    3 -> tipo_3()
                    4 -> tipo_4()
                    5 -> tipo_5()
                    6 -> tipo_6()
                    7 -> tipo_7()
                    8 -> tipo_8()
                    9 -> tipo_9()
                    10 -> tipo_10()
                    11 -> tipo_11()
                    12 -> tipo_12()
                    13 -> tipo_13()
                    14 -> tipo_14()
                    15 -> tipo_15()
                    16 -> tipo_16()
                    20 -> tipo_20()
                    21 -> tipo_21()
                }
                //
                if (!_lanzador.estaMuerto()) { // esta vivo y va a pasar turno
                    _pelea.pasarTurno(_lanzador)
                }
                if (_lanzador.estaMuerto()) {
                    _pelea.addMuertosReturnFinalizo(_lanzador, null)
                }
                if (!_lanzador.puedeJugar()) {
                    break
                }
            }
            parar()
        } catch (e: Exception) {
            e.printStackTrace()
            redactarLogServidorln(
                "EXCEPTION run IA tipo: " + sTipo + ", atacante: " + (if (_lanzador == null) "Null" else _lanzador.nombre) + ", " + (if (_pelea == null) " pelea Null" else "pMapa: " + _pelea.mapaCopia!!.id + " pID: " + _pelea.ID + " pEstado: " + _pelea.fase)
                        + ", Exception " + e.toString()
            )
            try {
                if (_pelea != null) {
                    redactarLogServidorln("ELMINANDO A LA IA DEL MOB")
                    _pelea.addMuertosReturnFinalizo(_lanzador, null)
                }
            } catch (ignored: Exception) {
            }
        } finally {
            parar()
        }
    }

    private fun clearInfluencias() {
        if (_resetearInfluencias) {
            _influencias.clear()
            _resetearInfluencias = false
        }
    }

    private fun clearCeldasHechizos() {
        if (_resetearCeldasHechizos) {
            _celdasHechizos.clear()
            _resetearCeldasHechizos = false
        }
    }

    private fun setCeldasHechizoCeldaLanz() {
        clearCeldasHechizos()
        val celdaLanzID = _lanzador.celdaPelea?.id
        if (!_celdasHechizos.containsKey(celdaLanzID)) {
            val a = celdaLanzID?.let { getObjHechDesdeCeldaLanz(it) }
            if (a == null || a.isEmpty()) {
                return
            }
            _celdasHechizos[celdaLanzID] = a
        }
    }

    private fun getObjetivosGuardado(celdaPosibleLanzamiento: Celda, SH: StatHechizo): ArrayList<Luchador>? {
        for (a in _celdasHechizos.values) {
            if (a!![SH] != null) {
                if (a[SH]!![celdaPosibleLanzamiento] != null) {
                    return a[SH]!![celdaPosibleLanzamiento]
                }
            }
        }
        return null
    }

    private fun getObjHechDesdeCeldaLanz(celdaLanzador: Short): Map<StatHechizo, MutableMap<Celda, ArrayList<Luchador>>> {
        val map: MutableMap<StatHechizo, MutableMap<Celda, ArrayList<Luchador>>> = HashMap()
        for (SH2 in hechizosLanzables()) {
            val celdas = Camino.celdasPosibleLanzamiento(
                SH2, _lanzador, _pelea.mapaCopia!!, celdaLanzador,
                (-1).toShort()
            )
            for (celdaPosibleLanzamiento in celdas) {
                if (_pelea.puedeLanzarHechizo(
                        _lanzador, SH2, celdaPosibleLanzamiento,
                        celdaLanzador
                    ) != EstadoLanzHechizo.PODER
                ) {
                    continue
                }
                var o = getObjetivosGuardado(celdaPosibleLanzamiento, SH2)
                if (o == null) {
                    o = SH2.listaObjetivosAfectados(_lanzador, _pelea.mapaCopia, celdaPosibleLanzamiento.id)
                }
                // if (!o.isEmpty()) {
                map.computeIfAbsent(SH2) { k: StatHechizo? -> HashMap() }
                map[SH2]?.set(celdaPosibleLanzamiento, o)
                // }
            }
        }
        return map
    }

    private fun tipo_0() {
        if (!_lanzador.puedeJugar()) {
            return
        }
        val amigos = ordenLuchadores(_lanzador.paramEquipoAliado.toInt(), Orden.NIVEL_MAS_A_MENOS)
        val enemigos = ordenLuchadores(
            _lanzador.paramEquipoEnemigo.toInt(), Orden.PDV_MENOS_A_MAS,
            Orden.INVOS_ULTIMOS
        )
        val porcPDV = _lanzador.porcPDV
        if (porcPDV > 50) {
            var movAtq: EstadoMovAtq
            while (buffeaSiEsPosible(amigos)) {
            }
            while (trampearSiEsPosible()) {
            }
            invocarSiEsPosible(enemigos)
            fullAtaqueSioSi(enemigos)
            do {
                movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR)
            } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
            teleportSiEsPosible(enemigos)
            while (curaSiEsPosible(amigos)) {
            }
            var acercarse = EstadoDistancia.NO_PUEDE
            do {
                if (movAtq != EstadoMovAtq.NO_TIENE_HECHIZOS) {
                    fullAtaqueSioSi(enemigos)
                }
            } while (movAtq != EstadoMovAtq.NO_PUEDE_MOVERSE && acercarseA(
                    enemigos, true,
                    true
                ).also { acercarse = it } == EstadoDistancia.ACERCARSE
            )
            while (trampearSiEsPosible()) {
            }
            siEsInvisible(acercarse)
        } else {
            while (curaSiEsPosible(amigos)) {
            }
            while (buffeaSiEsPosible(amigos)) {
            }
            while (trampearSiEsPosible()) {
            }
            var movAtq: EstadoMovAtq
            invocarSiEsPosible(enemigos)
            fullAtaqueSioSi(enemigos)
            do {
                movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR)
            } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
            var acercarse = EstadoDistancia.NO_PUEDE
            do {
                if (movAtq != EstadoMovAtq.NO_TIENE_HECHIZOS) {
                    fullAtaqueSioSi(enemigos)
                }
            } while (movAtq != EstadoMovAtq.NO_PUEDE_MOVERSE && acercarseA(
                    enemigos, true,
                    true
                ).also { acercarse = it } == EstadoDistancia.ACERCARSE
            )
            siEsInvisible(acercarse)
        }
    }

    private fun tipo_1() {
        if (!_lanzador.puedeJugar()) {
            return
        }
        val amigos = ordenLuchadores(_lanzador.paramEquipoAliado.toInt(), Orden.NIVEL_MAS_A_MENOS)
        val enemigos = ordenLuchadores(
            _lanzador.paramEquipoEnemigo.toInt(), Orden.PDV_MENOS_A_MAS,
            Orden.INVOS_ULTIMOS
        )
        val porcPDV = _lanzador.porcPDV
        val azar = randomBoolean
        if (porcPDV > 50 || azar) {
            var movAtq: EstadoMovAtq
            while (buffeaSiEsPosible(amigos)) {
            }
            if (azar) {
                fullAtaqueSioSi(enemigos)
                invocarSiEsPosible(enemigos)
            } else {
                invocarSiEsPosible(enemigos)
                fullAtaqueSioSi(enemigos)
            }
            do {
                movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR)
            } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
            teleportSiEsPosible(enemigos)
            while (curaSiEsPosible(amigos)) {
            }
            var acercarse = EstadoDistancia.NO_PUEDE
            do {
                if (movAtq != EstadoMovAtq.NO_TIENE_HECHIZOS) {
                    fullAtaqueSioSi(enemigos)
                }
            } while (movAtq != EstadoMovAtq.NO_PUEDE_MOVERSE && acercarseA(
                    enemigos, true,
                    true
                ).also { acercarse = it } == EstadoDistancia.ACERCARSE
            )
            while (trampearSiEsPosible()) {
            }
            siEsInvisible(acercarse)
        } else {
            while (curaSiEsPosible(amigos)) {
            }
            while (buffeaSiEsPosible(amigos)) {
            }
            while (trampearSiEsPosible()) {
            }
            var movAtq: EstadoMovAtq
            invocarSiEsPosible(enemigos)
            fullAtaqueSioSi(enemigos)
            do {
                movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR)
            } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
            var acercarse = EstadoDistancia.NO_PUEDE
            do {
                if (movAtq != EstadoMovAtq.NO_TIENE_HECHIZOS) {
                    fullAtaqueSioSi(enemigos)
                }
            } while (movAtq != EstadoMovAtq.NO_PUEDE_MOVERSE && acercarseA(
                    enemigos, true,
                    true
                ).also { acercarse = it } == EstadoDistancia.ACERCARSE
            )
            siEsInvisible(acercarse)
        }
    }

    private fun tipo_2() { // esfera xelor
        if (!_lanzador.puedeJugar()) {
            return
        }
        val enemigos = ordenLuchadores(
            _lanzador.paramEquipoEnemigo.toInt(), Orden.PDV_MENOS_A_MAS,
            Orden.INVOS_ULTIMOS
        )
        fullAtaqueSioSi(enemigos)
    }

    private fun tipo_3() { // mobs salas de entrenamiento
        if (!_lanzador.puedeJugar()) {
            return
        }
        val enemigos = ordenLuchadores(
            _lanzador.paramEquipoEnemigo.toInt(), Orden.PDV_MENOS_A_MAS,
            Orden.INVOS_ULTIMOS
        )
        var acercarse = EstadoDistancia.NO_PUEDE
        do {
            fullAtaqueSioSi(enemigos)
        } while (acercarseA(enemigos, true, true).also { acercarse = it } == EstadoDistancia.ACERCARSE)
        siEsInvisible(acercarse)
    }

    private fun tipo_4() { // tofu, prespic
        if (!_lanzador.puedeJugar()) {
            return
        }
        val amigos = ordenLuchadores(_lanzador.paramEquipoAliado.toInt(), Orden.NIVEL_MAS_A_MENOS)
        val enemigos = ordenLuchadores(
            _lanzador.paramEquipoEnemigo.toInt(), Orden.PDV_MENOS_A_MAS,
            Orden.INVOS_ULTIMOS
        )
        var tempMovAtq = EstadoMovAtq.NULO
        // aqui comienza todo
        var movAtq: EstadoMovAtq
        do {
            movAtq = moverYLanzarAlgo(amigos, Accion.BOOSTEAR)
            val eEnemigos = moverYLanzarAlgo(enemigos, Accion.BOOSTEAR)
            if (eEnemigos == EstadoMovAtq.LANZO_HECHIZO) {
                tempMovAtq = EstadoMovAtq.LANZO_HECHIZO
            }
            if (eEnemigos == EstadoMovAtq.TACLEADO || eEnemigos == EstadoMovAtq.SE_MOVIO || eEnemigos == EstadoMovAtq.LANZO_HECHIZO) {
                movAtq = eEnemigos
            }
        } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
        do {
            movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR)
            if (movAtq == EstadoMovAtq.LANZO_HECHIZO) {
                tempMovAtq = EstadoMovAtq.LANZO_HECHIZO
            }
        } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
        if (tempMovAtq == EstadoMovAtq.NULO) {
            if (acercarseA(enemigos, true, true) == EstadoDistancia.ACERCARSE) {
                if (movAtq != EstadoMovAtq.NO_TIENE_HECHIZOS) {
                    if (fullAtaqueSioSi(enemigos)) {
                        tempMovAtq = EstadoMovAtq.LANZO_HECHIZO
                    }
                }
            }
        }
        while (buffeaSiEsPosible(amigos)) {
        }
        if (tempMovAtq == EstadoMovAtq.LANZO_HECHIZO) {
            while (alejarseDeEnemigo()) {
            }
        }
    }

    private fun tipo_5() { // la bloqueadora
        if (!_lanzador.puedeJugar()) {
            return
        }
        val enemigos = ordenLuchadores(
            _lanzador.paramEquipoEnemigo.toInt(), Orden.PDV_MENOS_A_MAS,
            Orden.INVOS_ULTIMOS
        )
        var acercarse = EstadoDistancia.NO_PUEDE
        while (acercarseA(enemigos, false, false).also { acercarse = it } == EstadoDistancia.ACERCARSE) {
        }
        siEsInvisible(acercarse)
    }

    private fun tipo_6() { // la hinchable, conejo
        if (!_lanzador.puedeJugar()) {
            return
        }
        val amigos = ordenLuchadores(_lanzador.paramEquipoAliado.toInt(), Orden.NIVEL_MAS_A_MENOS)
        val enemigos = ordenLuchadores(
            _lanzador.paramEquipoEnemigo.toInt(), Orden.PDV_MENOS_A_MAS,
            Orden.INVOS_ULTIMOS
        )
        invocarSiEsPosible(enemigos)
        do {
            var movAtq: EstadoMovAtq
            do {
                movAtq = moverYLanzarAlgo(amigos, Accion.BOOSTEAR)
            } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
            do {
                movAtq = moverYLanzarAlgo(amigos, Accion.CURAR)
            } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
        } while (acercarseA(amigos, false, false) == EstadoDistancia.ACERCARSE)
    }

    private fun tipo_7() { // gatake, pala animada, jabali, crujidor
        if (!_lanzador.puedeJugar()) {
            return
        }
        val enemigos = ordenLuchadores(
            _lanzador.paramEquipoEnemigo.toInt(), Orden.PDV_MENOS_A_MAS,
            Orden.INVOS_ULTIMOS
        )
        var acercarse = EstadoDistancia.NO_PUEDE
        do {
            while (buffeaSiEsPosible(null)) {
            }
            var movAtq: EstadoMovAtq
            do {
                movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR)
            } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
            while (buffeaSiEsPosible(enemigos)) {
            }
        } while (acercarseA(enemigos, false, false).also { acercarse = it } == EstadoDistancia.ACERCARSE)
        siEsInvisible(acercarse)
    }

    private fun tipo_8() { // mochila animada
        if (!_lanzador.puedeJugar()) {
            return
        }
        val amigos = ordenLuchadores(_lanzador.paramEquipoAliado.toInt(), Orden.NIVEL_MAS_A_MENOS)
        var movAtq: EstadoMovAtq
        do {
            movAtq = moverYLanzarAlgo(amigos, Accion.BOOSTEAR)
        } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
        if (movAtq == EstadoMovAtq.TIENE_HECHIZOS_SIN_LANZAR) {
            while (acercarseA(amigos, false, false) == EstadoDistancia.ACERCARSE) {
            }
        } else {
            while (alejarseDeEnemigo()) {
            }
        }
    }

    private fun tipo_9() { // cofre animado, arbol de vida
        if (!_lanzador.puedeJugar()) {
            return
        }
        while (lanzaHechizoAlAzar(null, Accion.BOOSTEAR)) {
        }
    }

    private fun tipo_10() { // cascara explosiva
        if (!_lanzador.puedeJugar()) {
            return
        }
        val enemigos = ordenLuchadores(
            _lanzador.paramEquipoEnemigo.toInt(), Orden.PDV_MENOS_A_MAS,
            Orden.INVOS_ULTIMOS
        )
        var movAtq: EstadoMovAtq
        do {
            movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR)
        } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
        while (buffeaSiEsPosible(null)) {
        }
    }

    private fun tipo_11() { // chafer y chaferloko
        if (!_lanzador.puedeJugar()) {
            return
        }
        val todos = ordenLuchadores(3, Orden.PDV_MENOS_A_MAS, Orden.NADA)
        val enemigos = ordenLuchadores(
            _lanzador.paramEquipoEnemigo.toInt(), Orden.PDV_MENOS_A_MAS,
            Orden.INVOS_ULTIMOS
        )
        while (buffeaSiEsPosible(null)) {
        } // auto-buff
        var acercarse = EstadoDistancia.NO_PUEDE
        do {
            fullAtaqueSioSi(todos)
        } while (acercarseA(enemigos, false, true).also { acercarse = it } == EstadoDistancia.ACERCARSE)
        siEsInvisible(acercarse)
    }

    private fun tipo_12() { // kralamar
        if (!_lanzador.puedeJugar()) {
            return
        }
        val enemigos = ordenLuchadores(
            _lanzador.paramEquipoEnemigo.toInt(), Orden.PDV_MENOS_A_MAS,
            Orden.INVOS_ULTIMOS
        )
        invocarSiEsPosible(enemigos)
        buffeaSiEsPosible(null)
        fullAtaqueSioSi(enemigos)
    }

    private fun tipo_13() { // vasija
        if (!_lanzador.puedeJugar()) {
            return
        }
        while (lanzaHechizoAlAzar(null, Accion.BOOSTEAR)) {
        } // auto boost
        val enemigos = ordenLuchadores(
            _lanzador.paramEquipoEnemigo.toInt(), Orden.PDV_MENOS_A_MAS,
            Orden.INVOS_ULTIMOS
        )
        fullAtaqueSioSi(enemigos)
    }

    private fun tipo_14() { // aguja buscadora
        if (!_lanzador.puedeJugar()) {
            return
        }
        var acercarse = EstadoDistancia.NO_PUEDE
        val enemigos = ordenLuchadores(
            _lanzador.paramEquipoEnemigo.toInt(), Orden.PDV_MENOS_A_MAS,
            Orden.INVOS_ULTIMOS
        )
        do {
            while (lanzaHechizoAlAzar(enemigos, Accion.ATACAR)) {
            } // ataca
        } while (acercarseA(enemigos, true, true).also { acercarse = it } == EstadoDistancia.ACERCARSE)
        siEsInvisible(acercarse)
    }

    private fun tipo_15() { // IA para @fox discord
        if (!_lanzador.puedeJugar()) {
            return
        }
        val amigos = ordenLuchadores(_lanzador.paramEquipoAliado.toInt(), Orden.NIVEL_MAS_A_MENOS)
        val enemigos = ordenLuchadores(
            _lanzador.paramEquipoEnemigo.toInt(), Orden.PDV_MENOS_A_MAS,
            Orden.INVOS_ULTIMOS
        )
        while (buffeaSiEsPosible(amigos)) {
        }
        invocarSiEsPosible(enemigos)
        while (buffeaSiEsPosible(enemigos)) {
        }
        var ataco = false
        if (fullAtaqueSioSi(enemigos)) {
            ataco = true
        } else {
            if (acercarseA(enemigos, true, true) == EstadoDistancia.ACERCARSE) {
                if (fullAtaqueSioSi(enemigos)) {
                    ataco = true
                }
            }
        }
        while (buffeaSiEsPosible(amigos)) {
        }
        if (ataco) {
            while (alejarseDeEnemigo()) {
            }
        }
    }

    private fun tipo_16() { // tentaculos
        if (!_lanzador.puedeJugar()) {
            return
        }
        val amigos = ordenLuchadores(_lanzador.paramEquipoAliado.toInt(), Orden.NIVEL_MAS_A_MENOS)
        val enemigos = ordenLuchadores(
            _lanzador.paramEquipoEnemigo.toInt(), Orden.PDV_MENOS_A_MAS,
            Orden.INVOS_ULTIMOS
        )
        var movAtq: EstadoMovAtq
        invocarSiEsPosible(enemigos)
        fullAtaqueSioSi(enemigos)
        while (buffeaSiEsPosible(amigos)) {
        }
        do {
            movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR)
        } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
        while (curaSiEsPosible(amigos)) {
        }
        var acercarse = EstadoDistancia.NO_PUEDE
        do {
            if (movAtq != EstadoMovAtq.NO_TIENE_HECHIZOS) {
                fullAtaqueSioSi(enemigos)
            }
        } while (movAtq != EstadoMovAtq.NO_PUEDE_MOVERSE && acercarseA(
                enemigos, true,
                true
            ).also { acercarse = it } == EstadoDistancia.ACERCARSE
        )
        siEsInvisible(acercarse)
    }

    private fun tipo_20() { // Prisma
        if (!_lanzador.puedeJugar()) {
            return
        }
        val amigos = ordenLuchadores(_lanzador.paramEquipoAliado.toInt(), Orden.NIVEL_MAS_A_MENOS)
        val enemigos = ordenLuchadores(
            _lanzador.paramEquipoEnemigo.toInt(), Orden.PDV_MENOS_A_MAS,
            Orden.INVOS_ULTIMOS
        )
        curaSiEsPosible(amigos)
        buffeaSiEsPosible(amigos)
        fullAtaqueSioSi(enemigos)
    }

    private fun tipo_21() { // recaudador
        if (!_lanzador.puedeJugar()) {
            return
        }
        val amigos = ordenLuchadores(_lanzador.paramEquipoAliado.toInt(), Orden.NIVEL_MAS_A_MENOS)
        val enemigos = ordenLuchadores(
            _lanzador.paramEquipoEnemigo.toInt(), Orden.PDV_MENOS_A_MAS,
            Orden.INVOS_ULTIMOS
        )
        val porcPDV = _lanzador.porcPDV
        if (porcPDV > 50) {
            var movAtq: EstadoMovAtq
            buffeaSiEsPosible(amigos)
            fullAtaqueSioSi(enemigos)
            do {
                movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR)
            } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
            while (curaSiEsPosible(amigos)) {
            }
            var acercarse = EstadoDistancia.NO_PUEDE
            do {
                if (movAtq != EstadoMovAtq.NO_TIENE_HECHIZOS) {
                    fullAtaqueSioSi(enemigos)
                }
            } while (movAtq != EstadoMovAtq.NO_PUEDE_MOVERSE && acercarseA(
                    enemigos, true,
                    true
                ).also { acercarse = it } == EstadoDistancia.ACERCARSE
            )
            siEsInvisible(acercarse)
        } else {
            var movAtq: EstadoMovAtq
            while (curaSiEsPosible(null)) {
            }
            buffeaSiEsPosible(amigos)
            fullAtaqueSioSi(enemigos)
            do {
                movAtq = moverYLanzarAlgo(enemigos, Accion.ATACAR)
            } while (movAtq == EstadoMovAtq.TACLEADO || movAtq == EstadoMovAtq.SE_MOVIO || movAtq == EstadoMovAtq.LANZO_HECHIZO)
            var acercarse = EstadoDistancia.NO_PUEDE
            do {
                if (movAtq != EstadoMovAtq.NO_TIENE_HECHIZOS) {
                    fullAtaqueSioSi(enemigos)
                }
            } while (movAtq != EstadoMovAtq.NO_PUEDE_MOVERSE && acercarseA(
                    enemigos, true,
                    true
                ).also { acercarse = it } == EstadoDistancia.ACERCARSE
            )
            siEsInvisible(acercarse)
        }
    }

    private fun siEsInvisible(acercarse: EstadoDistancia) {
        if (acercarse == EstadoDistancia.INVISIBLE) {
            if (getRandomInt(1, 3) == 2) {
                while (acercarseAInvis()) {
                }
            } else {
                while (alejarseDeEnemigo()) {
                }
            }
        }
    }

    private fun objetivoApto(objetivo: Luchador?, pInvi: Boolean): Boolean {
        return if (objetivo == null) {
            false
        } else objetivo.estaMuerto() || pInvi && objetivo.esInvisible(_lanzador.id)
    }

    private fun hechizosLanzables(): ArrayList<StatHechizo> {
        val disponibles = ArrayList<StatHechizo>()
        if (_lanzador.hechizos == null) {
            return disponibles
        }
        for (SH in _lanzador.hechizos.values) {
            if (SH == null) {
                continue
            }
            try {
                if (AtlantaMain.MODO_DEBUG) {
                    println("hechizosLanzables() -> Hechizo " + SH.hechizo!!.nombre)
                }
                // filtra los hechizos q esten con tiempo o faltos de PA o algo por el estilo
                if (_pelea.filtraHechizoDisponible(_lanzador, SH, 0) != EstadoLanzHechizo.PODER) {
                    continue
                }
                disponibles.add(SH)
                for (EH in SH.efectosNormales) {
                    if (Constantes.estimaDaño(EH.efectoID) == 1) {
                        if (_lanzador.distMinAtq == -1) {
                            _lanzador.distMinAtq = SH.minAlc.toInt()
                        }
                        _lanzador.distMinAtq = min(SH.minAlc.toInt(), _lanzador.distMinAtq)
                        break
                    }
                }
            } catch (ignored: Exception) {
            }
        }
        return disponibles
    }

    private fun ordenLuchadores(equipo: Int, vararg orden: Orden): ArrayList<Luchador?> {
        val temporales = ArrayList<Luchador?>()
        for (luch in _pelea.luchadoresDeEquipo(equipo)) {
            if (objetivoApto(luch, true)) {
                continue
            }
            temporales.add(luch)
        }
        ordena(temporales, *orden)
        return temporales
    }

    private fun ordenarLuchMasCercano(preLista: ArrayList<Luchador?>, celdas: ArrayList<Short>, vararg orden: Orden) {
        var alejados: ArrayList<Luchador?>? = ArrayList()
        var cercanos: ArrayList<Luchador?>? = ArrayList()
        for (objetivo in preLista) {
            if (objetivoApto(objetivo, true)) {
                continue
            }
            if (celdas.contains(objetivo!!.celdaPelea?.id)) {
                cercanos!!.add(objetivo)
            } else {
                alejados!!.add(objetivo)
            }
        }
        ordena(cercanos, *orden)
        ordena(alejados, *orden)
        preLista.clear()
        preLista.addAll(cercanos!!)
        preLista.addAll(alejados!!)
        alejados = null
        cercanos = null
    }

    private fun ordenarLuchVulnerables(preLista: ArrayList<Luchador?>) {
        var vulnerables: ArrayList<Luchador?>? = ArrayList()
        var invulnerables: ArrayList<Luchador?>? = ArrayList()
        for (objetivo in preLista) {
            if (objetivoApto(objetivo, true)) {
                continue
            }
            if (esInvulnerable(objetivo)) {
                invulnerables!!.add(objetivo)
            } else {
                vulnerables!!.add(objetivo)
            }
        }
        preLista.clear()
        preLista.addAll(vulnerables!!)
        preLista.addAll(invulnerables!!)
        vulnerables = null
        invulnerables = null
    }

    private fun enemigoMasCercano(objetivos: ArrayList<Luchador?>): Luchador? {
        var dist = 1000
        var tempObjetivo: Luchador? = null
        for (objetivo in objetivos) {
            if (objetivoApto(objetivo, true)) {
                continue
            }
            try {
                val d = objetivo
                    ?.celdaPelea?.id?.let {
                        _lanzador.celdaPelea?.id?.let { it1 ->
                            Camino.distanciaDosCeldas(
                                _pelea.mapaCopia,
                                it1,
                                it
                            ).toInt()
                        }
                    }
                if (d != null) {
                    if (d < dist) {
                        dist = d
                        tempObjetivo = objetivo
                    }
                }
            } catch (ignored: Exception) {
            }
        }
        return tempObjetivo
    }

    private fun alejarseDeEnemigo(): Boolean {
        if (!_lanzador.puedeJugar()) {
            return false
        }
        if (_lanzador.pmRestantes <= 0) {
            return false
        }
        val celdaIDLanzador = _lanzador.celdaPelea?.id
        val celdasMovimiento = Camino.celdasDeMovimiento(
            _pelea, _lanzador.celdaPelea!!, true, true,
            null
        )
        if (celdaIDLanzador != null) {
            celdasMovimiento.add(celdaIDLanzador)
        }
        val enemigos = ArrayList<Luchador>()
        for (blanco in _pelea.luchadoresDeEquipo(_lanzador.paramEquipoEnemigo.toInt())) {
            if (objetivoApto(blanco, false)) {
                continue
            }
            enemigos.add(blanco)
        }
        val mapa = _pelea.mapaCopia
        var distEntreTodos = -1
        var celdaIdeal: Short = -1
        for (celdaTemp in celdasMovimiento) {
            var distTemp = 0
            for (blanco in enemigos) {
                distTemp += blanco.celdaPelea?.id?.let { Camino.distanciaDosCeldas(mapa, celdaTemp, it).toInt() }!!
            }
            if (distTemp >= distEntreTodos) {
                distEntreTodos = distTemp
                celdaIdeal = celdaTemp
            }
        }
        if (celdaIdeal.toInt() == -1 || celdaIdeal == celdaIDLanzador) {
            return false
        }
        val pathCeldas = celdaIDLanzador?.let {
            Camino.getPathPelea(
                mapa, it, celdaIdeal, -1, null,
                false
            )
        }
            ?: return false
        val path = pathCeldas._segundo
        val finalPath = ArrayList<Celda>()
        for (a in 0 until _lanzador.pmRestantes) {
            if (path.size == a || path[a]?.primerLuchador != null) {
                break
            }
            path[a]?.let { finalPath.add(it) }
        }
        val pathStr = Camino.getPathComoString(mapa, finalPath, celdaIDLanzador, true)
        if (pathStr.isEmpty()) {
            return false
        }
        val resultado = _pelea.intentarMoverse(_lanzador, pathStr.toString(), 0, null)
        return if (resultado == "stop") {
            false
        } else resultado == "ok"
    }

    // private static boolean mueveLoMasLejosPosible(final Pelea pelea, final Luchador lanzador) {
// if (!lanzador.puedeJugar()) {
// return false;
// }
// final int PM = pelea.getTempPM();
// if (PM <= 0) {
// return false;
// }
//
// final short celdaIDLanzador = lanzador.getCeldaPelea().getID();
// final Mapa mapa = pelea.getMapaCopia();
// final short dist[] = {1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000}, celda[] =
// {0, 0, 0, 0, 0, 0, 0,
// 0, 0, 0};
// for (int i = 0; i < 10; i++) {
// for (final Luchador blanco : pelea.luchadoresDeEquipo(lanzador.getParamEquipoEnemigo())) {
// if (blanco.estaMuerto()) {
// continue;
// }
// final short celdaEnemigo = blanco.getCeldaPelea().getID();
// if (celdaEnemigo == celda[0] || celdaEnemigo == celda[1] || celdaEnemigo == celda[2]
// || celdaEnemigo == celda[3] || celdaEnemigo == celda[4] || celdaEnemigo == celda[5] ||
// celdaEnemigo == celda[6]
// || celdaEnemigo == celda[7] || celdaEnemigo == celda[8] || celdaEnemigo == celda[9]) {
// continue;
// }
// short d = 0;
// d = Camino.distanciaDosCeldas(mapa, celdaIDLanzador, celdaEnemigo);
// if (d == 0) {
// continue;
// }
// if (d < dist[i]) {
// dist[i] = d;
// celda[i] = celdaEnemigo;
// }
// if (dist[i] == 1000) {
// dist[i] = 0;
// celda[i] = celdaIDLanzador;
// }
// }
// }
// if (dist[0] == 0) {
// return false;
// }
// final int dist2[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
// final byte ancho = mapa.getAncho(), alto = mapa.getAlto();
// short celdaInicio = celdaIDLanzador;
// short celdaDestino = celdaIDLanzador;
// final short ultCelda = Camino.ultimaCeldaID(mapa);
// final int valor = Formulas.getRandomValor(0, 3);
// int[] movidas;
// if (valor == 0) {
// movidas = new int[]{0, 1, 2, 3};
// } else if (valor == 1) {
// movidas = new int[]{1, 2, 3, 0};
// } else if (valor == 2) {
// movidas = new int[]{2, 3, 0, 1};
// } else {
// movidas = new int[]{3, 0, 1, 2};
// }
// for (int i = 0; i <= PM; i++) {
// if (celdaDestino > 0) {
// celdaInicio = celdaDestino;
// }
// short celdaTemporal = celdaInicio;
// int infl = 0, inflF = 0;
// for (final int x : movidas) {
// switch (x) {
// case 0 :
// celdaTemporal = (short) (celdaTemporal + ancho);
// break;
// case 1 :
// celdaTemporal = (short) (celdaInicio + (ancho - 1));
// break;
// case 2 :
// celdaTemporal = (short) (celdaInicio - ancho);
// break;
// case 3 :
// celdaTemporal = (short) (celdaInicio - (ancho - 1));
// break;
// }
// infl = 0;
// for (int a = 0; a < 10 && dist[a] != 0; a++) {
// dist2[a] = Camino.distanciaDosCeldas(mapa, celdaTemporal, celda[a]);
// if (dist2[a] > dist[a]) {
// infl++;
// }
// }
// if (infl > inflF && celdaTemporal > 0 && celdaTemporal < ultCelda
// && !Camino.celdaSalienteLateral(ancho, alto, celdaDestino, celdaTemporal)
// && mapa.getCelda(celdaTemporal).esCaminable(true)) {
// inflF = infl;
// celdaDestino = celdaTemporal;
// }
// }
// }
// if (celdaDestino < 0 || celdaDestino > ultCelda || celdaDestino == celdaIDLanzador
// || !mapa.getCelda(celdaDestino).esCaminable(true)) {
// return false;
// }
// final ArrayList<Celda> path = Camino.pathMasCortoEntreDosCeldas(mapa, celdaIDLanzador,
// celdaDestino, 0);
// if (path == null) {
// return false;
// }
// final ArrayList<Celda> finalPath = new ArrayList<Celda>();
// for (int a = 0; a < pelea.getTempPM(); a++) {
// if (path.size() == a) {
// break;
// }
// finalPath.add(path.get(a));
// }
// final StringBuilder pathStr = new StringBuilder();
// try {
// short tempCeldaID = celdaIDLanzador;
// for (final Celda c : finalPath) {
// final char d = Camino.dirEntreDosCeldas(mapa, tempCeldaID, c.getID(), true);
// if (d == 0) {
// return false;
// }
// if (finalPath.indexOf(c) != 0) {
// pathStr.append(Encriptador.celdaIDACodigo(tempCeldaID));
// }
// pathStr.append(d);
// tempCeldaID = c.getID();
// }
// if (tempCeldaID != celdaIDLanzador) {
// pathStr.append(Encriptador.celdaIDACodigo(tempCeldaID));
// }
// } catch (final Exception e) {
// e.printStackTrace();
// }
// String resultado = pelea.intentaMoverseLuchador(lanzador, pathStr.toString(), 0);
// if (resultado.equals("stop")) {
// return mueveLoMasLejosPosible(pelea, lanzador);
// }
// return resultado.equals("ok");
// }
//
    private fun acercarseAInvis(): Boolean {
        if (!_lanzador.puedeJugar()) {
            return false
        }
        if (_lanzador.pmRestantes <= 0) {
            return false
        }
        val enemigos = ArrayList<Luchador>()
        for (blanco in _pelea.luchadoresDeEquipo(_lanzador.paramEquipoEnemigo.toInt())) {
            if (objetivoApto(blanco, false)) {
                continue
            }
            enemigos.add(blanco)
        }
        val mapa = _pelea.mapaCopia
        val celdaIDLanzador = _lanzador.celdaPelea?.id
        val celdasMovimiento = Camino.celdasDeMovimiento(_pelea, _lanzador.celdaPelea!!, false, false, null)
//        val celdasMovimiento = _lanzador.celdaPelea?.let {
//            Camino.celdasDeMovimiento(_pelea, it, false, false,
//                null)
//        }
        if (celdasMovimiento.isEmpty()) {
            return false
        }
        _lanzador.celdaPelea?.id?.let { celdasMovimiento.add(it) }
        //
        val tempObjetivos = ArrayList(enemigos)
        var tempCeldaID: Short = -1
        var dist = 1000
        var repeticiones = 100
        val path = ArrayList<Celda?>()
        for (objetivo in tempObjetivos) {
            if (objetivoApto(objetivo, false)) {
                continue
            }
            if (objetivo === _lanzador) {
                continue
            }
            if (objetivo.esEstatico() && objetivo.equipoBin == _lanzador.equipoBin) {
                continue
            }
            var celdaTempObj = objetivo.celdaPelea?.id
            val pathTemp = Camino.getPathPelea(mapa, celdaIDLanzador!!, celdaTempObj!!, -1, null, false)
//            val pathTemp = celdaIDLanzador?.let {
//                celdaTempObj?.let { it1 ->
//                    Camino.getPathPelea(mapa, it, it1, -1, null,
//                            false)
//                }
//            }
            if (pathTemp == null || pathTemp._segundo.isEmpty()) {
                tempCeldaID = -2
                continue
            } else if (pathTemp._primero < repeticiones) {
                celdaTempObj = pathTemp._segundo[pathTemp._segundo.size - 1]?.id
                if (celdasMovimiento != null) {
                    if (celdasMovimiento.contains(objetivo.celdaPelea?.id) && pathTemp._segundo.size <= _lanzador
                            .pmRestantes && pathTemp._primero == 0
                    ) {
                        path.addAll(pathTemp._segundo)
                        if (celdaTempObj != null) {
                            tempCeldaID = celdaTempObj
                        }
                        // break;
                    } else {
                        val d = celdaTempObj?.let { Camino.distanciaDosCeldas(mapa, celdaIDLanzador, it).toInt() }
                        if (d != null) {
                            if (d < dist || pathTemp._primero < repeticiones) {
                                path.addAll(pathTemp._segundo)
                                if (celdaTempObj != null) {
                                    tempCeldaID = celdaTempObj
                                }
                                dist = d
                            }
                        }
                    }
                }
                repeticiones = pathTemp._primero
                break
            }
        }
        if (tempCeldaID.toInt() == -1) {
            return false
        } else if (tempCeldaID.toInt() == -2) { // (-2) el path es nulo porq no hay camino
            return false
        }
        val finalPath = ArrayList<Celda>()
        for (a in 0 until _lanzador.pmRestantes) {
            if (path.size == a || path[a]?.primerLuchador != null) {
                break
            }
            path[a]?.let { finalPath.add(it) }
        }
        val pathStr = celdaIDLanzador?.let { Camino.getPathComoString(mapa, finalPath, it, true) }
        if (pathStr != null) {
            if (pathStr.isEmpty()) {
                return false
            }
        }
        when (_pelea.intentarMoverse(_lanzador, pathStr.toString(), 0, null)) {
            "stop", "ok", "tacleado" -> return true
        }
        return false
    }

    private fun acercarseA(
        objetivos: ArrayList<Luchador?>, masCercano: Boolean,
        paraAtacar: Boolean
    ): EstadoDistancia {
        if (!_lanzador.puedeJugar()) {
            return EstadoDistancia.NO_PUEDE
        }
        if (_lanzador.pmRestantes <= 0) {
            return EstadoDistancia.NO_PUEDE
        }
        if (objetivos.isEmpty()) {
            return EstadoDistancia.INVISIBLE
        }
        val mapa = _pelea.mapaCopia
        val celdaIDLanzador = _lanzador.celdaPelea?.id
        val celdasMovimiento = Camino.celdasDeMovimiento(
            _pelea, _lanzador.celdaPelea!!, false, false,
            null
        )
        if (celdasMovimiento.isEmpty()) {
            return EstadoDistancia.NO_PUEDE
        }
        _lanzador.celdaPelea?.id?.let { celdasMovimiento.add(it) }
        //
        val tempObjetivos = ArrayList(objetivos)
        if (masCercano) {
            ordenarLuchMasCercano(tempObjetivos, celdasMovimiento, Orden.PDV_MENOS_A_MAS)
        }
        if (paraAtacar) {
            ordenarLuchVulnerables(tempObjetivos)
        }
        var tempCeldaID: Short = -1
        var celdaLuchObjetivo: Short = -1
        var dist = 1000
        var repeticiones = 100
        val path = ArrayList<Celda?>()
        for (objetivo in tempObjetivos) {
            if (objetivoApto(objetivo, true)) {
                continue
            }
            if (objetivo === _lanzador) {
                continue
            }
            if (objetivo!!.esEstatico() && objetivo.equipoBin == _lanzador.equipoBin) {
                continue
            }
            var celdaTempObj = objetivo.celdaPelea?.id
            val pathTemp = celdaIDLanzador?.let {
                celdaTempObj?.let { it1 ->
                    Camino.getPathPelea(
                        mapa, it, it1, -1, null,
                        false
                    )
                }
            }
            if (pathTemp == null || pathTemp._segundo.isEmpty()) {
                tempCeldaID = -2
                continue
            } else if (pathTemp._primero < repeticiones) {
                celdaTempObj = pathTemp._segundo[pathTemp._segundo.size - 1]?.id
                if (celdasMovimiento.contains(objetivo.celdaPelea?.id) && pathTemp._segundo.size <= _lanzador
                        .pmRestantes && pathTemp._primero == 0
                ) {
                    path.addAll(pathTemp._segundo)
                    if (celdaTempObj != null) {
                        tempCeldaID = celdaTempObj
                    }
                    celdaLuchObjetivo = objetivo.celdaPelea?.id!!
                    // break;
                } else {
                    val d = celdaTempObj?.let { Camino.distanciaDosCeldas(mapa, celdaIDLanzador, it).toInt() }
                    if (d != null) {
                        if (d < dist || pathTemp._primero < repeticiones) {
                            path.addAll(pathTemp._segundo)
                            if (celdaTempObj != null) {
                                tempCeldaID = celdaTempObj
                            }
                            celdaLuchObjetivo = objetivo.celdaPelea?.id!!
                            dist = d
                        }
                    }
                }
                repeticiones = pathTemp._primero
                break
            }
        }
        if (tempCeldaID.toInt() == -1) {
            return EstadoDistancia.NO_PUEDE
        } else if (tempCeldaID.toInt() == -2) { // (-2) el path es nulo porq no hay camino
            return EstadoDistancia.NO_PUEDE
        }
        val finalPath = ArrayList<Celda>()
        for (a in 0 until _lanzador.pmRestantes) {
            if (path.size == a || path[a]?.primerLuchador != null) {
                break
            }
            if (paraAtacar) {
                val d = Camino.distanciaDosCeldas(mapa, path[a]?.id!!, celdaLuchObjetivo).toInt()
                if (_lanzador.distMinAtq != -1 && d < _lanzador.distMinAtq) {
                    break
                }
            }
            path[a]?.let { finalPath.add(it) }
        }
        val pathStr = celdaIDLanzador?.let { Camino.getPathComoString(mapa, finalPath, it, true) }
        if (pathStr != null) {
            if (pathStr.isEmpty()) {
                return EstadoDistancia.NO_PUEDE
            }
        }
        when (_pelea.intentarMoverse(_lanzador, pathStr.toString(), 0, null)) {
            "stop", "ok" -> return EstadoDistancia.ACERCARSE
            "tacleado" -> return EstadoDistancia.TACLEADO
        }
        return EstadoDistancia.NO_PUEDE
    }

    private fun lanzaHechizoAlAzar(objetivos: ArrayList<Luchador?>?, accion: Accion): Boolean {
        var objetivos = objetivos
        if (!_lanzador.puedeJugar()) {
            return false
        }
        if (objetivos == null || objetivos.isEmpty()) {
            objetivos = ArrayList()
            objetivos.add(_lanzador)
        }
        for (objetivo in objetivos) {
            if (objetivoApto(objetivo, true)) {
                continue
            }
            val SH = hechizoAlAzar(objetivo) ?: continue
            if (accion == Accion.ATACAR && tieneReenvio(_lanzador, objetivo, SH)) {
                continue
            }
            if (objetivo!!.celdaPelea?.let {
                    _pelea.intentarLanzarHechizo(
                        _lanzador,
                        SH,
                        it,
                        true
                    )
                } != EstadoLanzHechizo.PODER) {
                continue
            }
            return true
        }
        return false
    }

    private fun setCeldasMovimiento() {
        if (_refrescarMov) {
            _celdasMovimiento = Camino.celdasDeMovimiento(_pelea, _lanzador.celdaPelea!!, true, true, _lanzador)
            _refrescarMov = false
        }
    }

    private fun moverYLanzarAlgo(objetivos: ArrayList<Luchador?>?, buffInvoAtaq: Accion): EstadoMovAtq {
        if (objetivos == null || !_lanzador.puedeJugar()) {
            return EstadoMovAtq.NO_HIZO_NADA
        }
        var filtroPorHechizo: StatHechizo? = null
        for (SH2 in hechizosLanzables()) {
            for (objetivo in objetivos) {
                val filtroIA = getFiltroIA(SH2, objetivo, buffInvoAtaq)
                if (filtroIA < 0) {
                    continue
                }
                filtroPorHechizo = SH2
                break
            }
            if (filtroPorHechizo != null) {
                break
            }
        }
        if (filtroPorHechizo == null) {
            return EstadoMovAtq.NO_TIENE_HECHIZOS
        }
        val tempObjetivos = ArrayList(objetivos)
        if (buffInvoAtaq == Accion.ATACAR) {
            sort(CompPDVMenosMas())
        }
        sort(CompInvosUltimos())
        // -----
        val mapa = _pelea.mapaCopia
        var SH: StatHechizo? = null
        var celdaObjetivoLanz: Celda? = null
        val celdaIDLanzador = _lanzador.celdaPelea?.id
        var celdaDestinoMov: Short = 0
        var influenciaMax = -1000000000
        var distancia = 10000
        val tempCeldasMovPrioridad = ArrayList<Short>()
        _lanzador.celdaPelea?.id?.let { tempCeldasMovPrioridad.add(it) }
        setCeldasHechizoCeldaLanz()
        setCeldasMovimiento()
        // ordena por prioridad las celdas segun los objetivos
        if (AtlantaMain.NIVEL_INTELIGENCIA_ARTIFICIAL > INTELIGENCIA_ORDENAR_PRIORIDAD_OBJETIVOS) {
            for (objetivo in tempObjetivos) {
                if (objetivoApto(objetivo, true)) {
                    continue
                }
                if (buffInvoAtaq == Accion.CURAR && objetivo!!.porcPDV > PDV_MINIMO_CURAR) {
                    continue
                }
                val pathTemp = objetivo
                    ?.celdaPelea?.id?.let {
                        _lanzador.celdaPelea?.id?.let { it1 ->
                            Camino.getPathPelea(
                                mapa,
                                it1,
                                it,
                                -1,
                                _lanzador,
                                true
                            )
                        }
                    }
                    ?: continue
                for (c in pathTemp._segundo) {
                    if (c != null) {
                        if (!_celdasMovimiento.contains(c.id) || tempCeldasMovPrioridad.contains(c.id)) {
                            continue
                        }
                    }
                    if (c != null) {
                        tempCeldasMovPrioridad.add(c.id)
                    }
                }
            }
        }
        for (c in _celdasMovimiento) {
            if (tempCeldasMovPrioridad.contains(c)) {
                continue
            }
            tempCeldasMovPrioridad.add(c)
        }
        for (tempCeldaLanz in tempCeldasMovPrioridad) {
            var map = _celdasHechizos[tempCeldaLanz]
            if (map == null) {
                map = getObjHechDesdeCeldaLanz(tempCeldaLanz)
                if (map == null || map.isEmpty()) {
                    continue
                }
                _celdasHechizos[tempCeldaLanz] = map
            }
            // }
// if (_celdasHechizos.isEmpty()) {
// return EstadoMovAtq.NO_TIENE_HECHIZOS;
// }
// for (short tempCeldaLanz : tempCeldasMovPrioridad) {
            var prioridadObj = tempObjetivos.size
            for (objetivo in tempObjetivos) {
                prioridadObj--
                if (objetivoApto(objetivo, true)) {
                    continue
                }
                var duo: Duo<Int, Duo<StatHechizo?, Celda?>>? = null
                if (buffInvoAtaq == Accion.ATACAR) {
                    duo = mejorAtaque(tempCeldaLanz, objetivo, map)
                } else if (buffInvoAtaq == Accion.BOOSTEAR) {
                    duo = mejorBuff(tempCeldaLanz, objetivo, map)
                } else if (buffInvoAtaq == Accion.CURAR) {
                    duo = mejorCura(tempCeldaLanz, objetivo, map)
                }
                if (duo == null || duo._primero <= 0) {
                    continue
                }
                val tempInf = duo._primero + prioridadObj
                // esto era un antiguo codigo
                if (SH == null || tempInf > influenciaMax) {
                    influenciaMax = tempInf
                    SH = duo._segundo._primero
                    celdaObjetivoLanz = duo._segundo._segundo
                    celdaDestinoMov = tempCeldaLanz
                    if (AtlantaMain.NIVEL_INTELIGENCIA_ARTIFICIAL <= INTELIGENCIA_COMPARAR_INF_OBJETIVOS) {
                        break
                    }
                    if (AtlantaMain.NIVEL_INTELIGENCIA_ARTIFICIAL > INTELIGENCIA_COMPARAR_INF_DIST_OBJETIVOS) {
                        distancia =
                            celdaIDLanzador?.let { Camino.distanciaDosCeldas(mapa, it, tempCeldaLanz).toInt() }!!
                    }
                    //
// objetivo = tempObjetivo;
                }
                if (AtlantaMain.NIVEL_INTELIGENCIA_ARTIFICIAL > INTELIGENCIA_COMPARAR_INF_DIST_OBJETIVOS) {
                    if (tempInf == influenciaMax && celdaIDLanzador?.let {
                            Camino.distanciaDosCeldas(
                                mapa,
                                it,
                                tempCeldaLanz
                            )
                        }!! < distancia.toShort()) {
                        SH = duo._segundo._primero
                        celdaObjetivoLanz = duo._segundo._segundo
                        celdaDestinoMov = tempCeldaLanz
                        distancia = celdaIDLanzador.let { Camino.distanciaDosCeldas(mapa, it, tempCeldaLanz).toInt() }
                        // objetivo = tempObjetivo;
                    }
                }
                // else
            }
            if (AtlantaMain.NIVEL_INTELIGENCIA_ARTIFICIAL <= INTELIGENCIA_COMPARAR_INF_DIST_OBJETIVOS) {
                if (SH != null) {
                    break
                }
            }
        }
        if (SH == null) {
            return EstadoMovAtq.TIENE_HECHIZOS_SIN_LANZAR
        }
        if (celdaDestinoMov.toInt() == 0 || celdaDestinoMov == celdaIDLanzador) { // si no hay necesidad de moverse y solo se lanza el hechizo sobre la ubicacion
            val i = _pelea.intentarLanzarHechizo(_lanzador, SH, celdaObjetivoLanz!!, false)
            return if (i == EstadoLanzHechizo.PODER) {
                EstadoMovAtq.LANZO_HECHIZO
            } else when (i) {
                EstadoLanzHechizo.NO_TIENE_ALCANCE -> EstadoMovAtq.TIENE_HECHIZOS_SIN_LANZAR
                else -> EstadoMovAtq.NO_HIZO_NADA
            }
        }
        val pathCeldas = celdaIDLanzador?.let {
            Camino.getPathPelea(
                mapa, it, celdaDestinoMov, -1,
                null, false
            )
        }
            ?: return EstadoMovAtq.TIENE_HECHIZOS_SIN_LANZAR
        val path = pathCeldas._segundo
        val finalPath = ArrayList<Celda>()
        var a = 0
        while (a < _lanzador.pmRestantes && a < path.size) {
            if (path[a]?.primerLuchador != null) {
                break
            }
            // int d = Camino.distanciaDosCeldas(mapa, path.get(a).getID(), celdaDestinoMov);
// if (lanzador._distMinAtq != -1 && d < lanzador._distMinAtq) {
// break;
// }
            path[a]?.let { finalPath.add(it) }
            a++
        }
        val pathStr = Camino.getPathComoString(mapa, finalPath, celdaIDLanzador, true)
        if (pathStr.isEmpty()) {
            return EstadoMovAtq.TIENE_HECHIZOS_SIN_LANZAR
        }
        when (_pelea.intentarMoverse(_lanzador, pathStr.toString(), 0, null)) {
            "ok" -> {
                val i = _pelea.intentarLanzarHechizo(_lanzador, SH, celdaObjetivoLanz!!, false)
                return if (i == EstadoLanzHechizo.PODER) {
                    EstadoMovAtq.LANZO_HECHIZO
                } else EstadoMovAtq.SE_MOVIO
            }
            "stop" -> return EstadoMovAtq.SE_MOVIO
        }
        return EstadoMovAtq.TIENE_HECHIZOS_SIN_LANZAR
    }

    private fun fullAtaqueSioSi(enemigos: ArrayList<Luchador?>?): Boolean {
        if (enemigos == null || !_lanzador.puedeJugar()) {
            return false
        }
        var ataco = false
        var objetivos: ArrayList<Luchador?>? = ArrayList(enemigos)
        sort(CompPDVMenosMas())
        sort(CompInvosUltimos())
        while (atacaSiEsPosible(objetivos) == EstadoLanzHechizo.PODER) {
            ataco = true
        }
        objetivos = null
        return ataco
    }

    private fun atacaSiEsPosible(objetivos: ArrayList<Luchador?>?): EstadoLanzHechizo {
        if (objetivos == null || !_lanzador.puedeJugar()) {
            return EstadoLanzHechizo.NO_PODER
        }
        setCeldasHechizoCeldaLanz()
        // objetivos = listaLuchadoresMasCercano(pelea, lanzador, objetivos, Orden.PDV_MENOS_A_MAS);
        for (objetivo in objetivos) {
            if (objetivoApto(objetivo, true)) {
                continue
            }
            val celdaLanzID = _lanzador.celdaPelea?.id
            val duo = celdaLanzID?.let { mejorAtaque(it, objetivo, _celdasHechizos[celdaLanzID]) }
            if (duo != null) {
                return _pelea.intentarLanzarHechizo(_lanzador, duo._segundo._primero, duo._segundo._segundo!!, true)
            }
        }
        return EstadoLanzHechizo.NO_PODER // no pudo lanzar
    }

    private fun buffeaSiEsPosible(objetivos: ArrayList<Luchador?>?): Boolean {
        var objetivos = objetivos
        if (!_lanzador.puedeJugar()) {
            return false
        }
        setCeldasHechizoCeldaLanz()
        if (objetivos == null || objetivos.isEmpty()) {
            objetivos = ArrayList()
            objetivos.add(_lanzador)
        }
        // Collections.sort(objetivos, new CompNivelMasMenos());
        for (objetivo in objetivos) {
            if (objetivoApto(objetivo, true)) {
                continue
            }
            val celdaLanzID = _lanzador.celdaPelea?.id
            val duo = celdaLanzID?.let { mejorBuff(it, objetivo, _celdasHechizos[celdaLanzID]) }
            if (duo != null) {
                return _pelea.intentarLanzarHechizo(
                    _lanzador, duo._segundo._primero, duo._segundo._segundo!!,
                    true
                ) == EstadoLanzHechizo.PODER
            }
        }
        return false
    }

    private fun curaSiEsPosible(objetivos: ArrayList<Luchador?>?): Boolean {
        if (!_lanzador.puedeJugar()) {
            return false
        }
        setCeldasHechizoCeldaLanz()
        val paraCurar = ArrayList<Luchador?>()
        if (objetivos == null || objetivos.isEmpty()) {
            paraCurar.add(_lanzador)
        } else {
            paraCurar.addAll(objetivos)
        }
        sort(CompPDVMenosMas())
        for (objetivo in paraCurar) {
            if (objetivoApto(objetivo, true)) {
                continue
            }
            if (objetivo!!.porcPDV > PDV_MINIMO_CURAR) {
                continue
            }
            val celdaLanzID = _lanzador.celdaPelea?.id
            val duo = celdaLanzID?.let { mejorCura(it, objetivo, _celdasHechizos[celdaLanzID]) }
            if (duo != null) {
                return _pelea.intentarLanzarHechizo(
                    _lanzador, duo._segundo._primero, duo._segundo._segundo!!,
                    true
                ) == EstadoLanzHechizo.PODER
            }
        }
        return false
    }

    private fun invocarSiEsPosible(objetivos: ArrayList<Luchador?>?) {
        if (objetivos == null || !_lanzador.puedeJugar()) {
            return
        }
        if (_lanzador.nroInvocaciones >= _lanzador.totalStats.getTotalStatParaMostrar(
                Constantes.STAT_MAS_CRIATURAS_INVO
            )
        ) {
            return
        }
        val enemigoCercano = enemigoMasCercano(objetivos) ?: return
        val hechizo = mejorInvocacion(enemigoCercano) ?: return
        _pelea.intentarLanzarHechizo(_lanzador, hechizo._segundo, hechizo._primero, true)
        try {
            sleep(1500)
        } catch (e: Exception) {
            redactarLogServidorln(e.toString())
        }
    }

    private fun teleportSiEsPosible(objetivos: ArrayList<Luchador?>?) {
        if (objetivos == null || !_lanzador.puedeJugar()) {
            return
        }
        if (_lanzador.tieneEstado(Constantes.ESTADO_PESADO.toInt()) || _lanzador.tieneEstado(Constantes.ESTADO_ARRAIGADO.toInt())
            || _lanzador.tieneEstado(Constantes.ESTADO_PORTADOR.toInt()) || _lanzador.tieneEstado(Constantes.ESTADO_TRANSPORTADO.toInt())
            || objetivos.isEmpty()
        ) {
            return
        }
        if (_lanzador.celdaPelea?.id?.let {
                Camino.getEnemigoAlrededor(
                    it, _pelea.mapaCopia!!, null, _lanzador
                        .equipoBin.toInt()
                )
            } != null) {
            return
        }
        val enemigoCercano = objetivos[0] ?: return
        val hechizo = mejorTeleport(enemigoCercano) ?: return
        _pelea.intentarLanzarHechizo(_lanzador, hechizo._segundo, hechizo._primero, true)
    }

    private fun trampearSiEsPosible(): Boolean {
        if (!_lanzador.puedeJugar()) {
            return false
        }
        val hechizo = mejorGlifoTrampa() ?: return false
        return _pelea.intentarLanzarHechizo(
            _lanzador,
            hechizo._segundo,
            hechizo._primero,
            true
        ) == EstadoLanzHechizo.PODER
    }

    private fun hechizoAlAzar(objetivo: Luchador?): StatHechizo? {
        if (!_lanzador.puedeJugar() || objetivo == null) {
            return null
        }
        val hechizos = ArrayList<StatHechizo>()
        for (SH in hechizosLanzables()) {
            if (_lanzador.celdaPelea?.id?.let {
                    objetivo.celdaPelea?.id?.let { it1 ->
                        _pelea.dentroDelRango(
                            _lanzador,
                            SH,
                            it,
                            it1
                        )
                    }
                }!!) {
                continue
            }
            hechizos.add(SH)
        }
        return if (hechizos.isEmpty()) {
            null
        } else hechizos[getRandomInt(0, hechizos.size - 1)]
    }

    private fun getFiltroIA(SH2: StatHechizo, objetivo: Luchador?, accion: Accion): Int {
        var c = ' '
        if (objetivo != null) {
            c = if (_lanzador.equipoBin == objetivo.equipoBin) {
                '+'
            } else {
                '-'
            }
        }
        return SH2.filtroValorIA(accion, c)
    }

    private fun mejorAtaque(
        celdaLanzador: Short, objetivo: Luchador?,
        map: Map<StatHechizo, MutableMap<Celda, ArrayList<Luchador>>>?
    ): Duo<Int, Duo<StatHechizo?, Celda?>>? {
        if (!_lanzador.puedeJugar()) {
            return null
        }
        if (map == null) {
            return null
        }
        var menorCostePA = 1000
        var influenciaMax = 0
        var SH: StatHechizo? = null
        var celdaObjetivo: Celda? = null
        if (objetivoApto(objetivo, true)) {
            return null
        }
        clearInfluencias()
        for ((SH2, value) in map) {
            val filtroIA = getFiltroIA(SH2, objetivo, Accion.ATACAR)
            if (filtroIA < 0) {
                continue
            }
            for ((celdaPosibleObj, value1) in value) {
                if (estaDentroObjetivos(objetivo, value1, celdaPosibleObj)) {
                    continue
                }
                if (_pelea.puedeLanzarHechizo(
                        _lanzador,
                        SH2,
                        celdaPosibleObj,
                        celdaLanzador
                    ) != EstadoLanzHechizo.PODER
                ) {
                    continue
                }
                val influencia = calculaInfluenciaDaño(
                    _pelea.mapaCopia, SH2, celdaPosibleObj.id, celdaLanzador,
                    filtroIA
                )
                if (influencia <= 0) {
                    continue
                }
                if (influencia > influenciaMax || influencia == influenciaMax && SH2.costePA < menorCostePA) {
                    SH = SH2
                    celdaObjetivo = celdaPosibleObj
                    menorCostePA = SH2.costePA.toInt()
                    influenciaMax = influencia
                }
            }
        }
        val a = Duo(SH, celdaObjetivo)
        if (AtlantaMain.MODO_DEBUG) {
            if (celdaObjetivo != null) {
                println(
                    "mejorAtaque() Hechizo: " + SH!!.hechizo!!.nombre + " (" + SH.hechizoID
                            + ") Celda: " + celdaObjetivo.id + " Inf: " + influenciaMax
                )
            }
        }
        if (celdaObjetivo == null) {
            return null
        }
        return Duo(influenciaMax, a)
    }

    private fun mejorBuff(
        celdaLanzador: Short, objetivo: Luchador?,
        map: Map<StatHechizo, MutableMap<Celda, ArrayList<Luchador>>>?
    ): Duo<Int, Duo<StatHechizo?, Celda?>>? {
        if (!_lanzador.puedeJugar()) {
            return null
        }
        if (map == null) {
            return null
        }
        var menorCostePA = 1000
        var influenciaMax = 0
        var SH: StatHechizo? = null
        var celdaObjetivo: Celda? = null
        if (objetivoApto(objetivo, true)) {
            return null
        }
        clearInfluencias()
        for ((SH2, value) in map) {
            val filtroIA = getFiltroIA(SH2, objetivo, Accion.BOOSTEAR)
            if (filtroIA < 0) {
                continue
            }
            for ((celdaPosibleObj, value1) in value) {
                if (estaDentroObjetivos(objetivo, value1, celdaPosibleObj)) {
                    continue
                }
                if (_pelea.puedeLanzarHechizo(
                        _lanzador,
                        SH2,
                        celdaPosibleObj,
                        celdaLanzador
                    ) != EstadoLanzHechizo.PODER
                ) {
                    continue
                }
                val influencia = calculaInfluenciaBuff(
                    _pelea.mapaCopia, SH2, celdaPosibleObj.id, celdaLanzador,
                    filtroIA
                )
                if (influencia <= 0) {
                    continue
                }
                if (influencia > influenciaMax || influencia == influenciaMax && SH2.costePA < menorCostePA) {
                    SH = SH2
                    celdaObjetivo = celdaPosibleObj
                    menorCostePA = SH2.costePA.toInt()
                    influenciaMax = influencia
                }
            }
        }
        val a = Duo(SH, celdaObjetivo)
        if (AtlantaMain.MODO_DEBUG) {
            if (celdaObjetivo != null) {
                println(
                    "mejorBuff() Hechizo: " + SH!!.hechizo!!.nombre + " (" + SH.hechizoID + ") Celda: "
                            + celdaObjetivo.id + " Inf: " + influenciaMax
                )
            }
        }
        if (celdaObjetivo == null) {
            return null
        }
        return Duo(influenciaMax, a)
    }

    private fun mejorCura(
        celdaLanzador: Short, objetivo: Luchador?,
        map: Map<StatHechizo, MutableMap<Celda, ArrayList<Luchador>>>?
    ): Duo<Int, Duo<StatHechizo?, Celda?>>? {
        if (!_lanzador.puedeJugar()) {
            return null
        }
        if (map == null) {
            return null
        }
        var menorCostePA = 1000
        var influenciaMax = 0
        var SH: StatHechizo? = null
        var celdaObjetivo: Celda? = null
        if (objetivoApto(objetivo, true)) {
            return null
        }
        clearInfluencias()
        for ((SH2, value) in map) {
            val filtroIA = getFiltroIA(SH2, objetivo, Accion.CURAR)
            if (filtroIA < 0) {
                continue
            }
            for ((celdaPosibleObj, value1) in value) {
                if (estaDentroObjetivos(objetivo, value1, celdaPosibleObj)) {
                    continue
                }
                if (_pelea.puedeLanzarHechizo(
                        _lanzador,
                        SH2,
                        celdaPosibleObj,
                        celdaLanzador
                    ) != EstadoLanzHechizo.PODER
                ) {
                    continue
                }
                val influencia = calculaInfluenciaCura(
                    _pelea.mapaCopia, SH2, celdaPosibleObj.id, celdaLanzador,
                    filtroIA
                )
                if (influencia <= 0) {
                    continue
                }
                if (influencia > influenciaMax || influencia == influenciaMax && SH2.costePA < menorCostePA) {
                    SH = SH2
                    celdaObjetivo = celdaPosibleObj
                    menorCostePA = SH2.costePA.toInt()
                    influenciaMax = influencia
                }
            }
        }
        val a = Duo(SH, celdaObjetivo)
        if (AtlantaMain.MODO_DEBUG) {
            if (celdaObjetivo != null) {
                println(
                    "mejorCura() Hechizo: " + SH!!.hechizo!!.nombre + " (" + SH.hechizoID + ") Celda: "
                            + celdaObjetivo.id + " Inf: " + influenciaMax
                )
            }
        }
        if (celdaObjetivo == null) {
            return null
        }
        return Duo(influenciaMax, a)
    }

    private fun mejorInvocacion(objetivo: Luchador?): Duo<Celda, StatHechizo>? {
        if (!_lanzador.puedeJugar()) {
            return null
        }
        if (objetivo == null) {
            return null
        }
        for (SH in hechizosLanzables()) {
            val filtroIA = getFiltroIA(SH, objetivo, Accion.INVOCAR)
            if (filtroIA < 0) {
                continue
            }
            if (filtroIA == 0) {
                var esInvocacion = false
                for (EH in SH.efectosNormales) {
                    when (EH.efectoID) {
                        180, 181, 185, 780 -> esInvocacion = true
                    }
                }
                if (!esInvocacion) {
                    continue
                }
            }
            var distancia = 1000
            var celdaObjetivo: Celda? = null
            val celdas = _lanzador
                .celdaPelea?.id?.let {
                    Camino.celdasPosibleLanzamiento(
                        SH,
                        _lanzador,
                        _pelea.mapaCopia!!,
                        it,
                        (-1).toShort()
                    )
                }
            if (celdas != null) {
                for (celda in celdas) {
                    val dist = objetivo.celdaPelea
                        ?.id?.let { Camino.distanciaDosCeldas(_pelea.mapaCopia, celda.id, it).toInt() }
                    if (dist != null) {
                        if (dist < distancia) {
                            celdaObjetivo = celda
                            distancia = dist
                        }
                    }
                }
            }
            if (celdaObjetivo == null) {
                continue
            }
            if (AtlantaMain.MODO_DEBUG) {
                println(
                    "mejorInvocacion() Hechizo: " + SH.hechizo!!.nombre + " (" + SH.hechizoID
                            + ") Celda: " + celdaObjetivo.id
                )
            }
            return Duo(celdaObjetivo, SH)
        }
        return null
    }

    private fun mejorGlifoTrampa(): Duo<Celda, StatHechizo>? {
        if (!_lanzador.puedeJugar()) {
            return null
        }
        for (SH in hechizosLanzables()) {
            var tamaño = 0
            var trampa = 0
            var daño = false
            val filtroIA = getFiltroIA(SH, null, Accion.TRAMPEAR)
            if (filtroIA < 0) {
                continue
            }
            if (filtroIA == 0) {
                for (EH in SH.efectosNormales) {
                    if (trampa == 3) {
                        break
                    }
                    when (EH.efectoID) {
                        82, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 275, 276, 277, 278, 279 -> trampa =
                            3
                        400, 401, 402 -> {
                            trampa = if (EH.efectoID == 400) {
                                1
                            } else {
                                2
                            }
                            tamaño = EH.zonaEfecto?.get(1)?.let { Encriptador.getNumeroPorValorHash(it).toInt() }!!
                            val sh = Mundo.getHechizo(EH.primerValor)?.getStatsPorNivel(EH.segundoValor)
                            if (sh != null) {
                                for (eh in sh.efectosNormales) {
                                    daño = Constantes.estimaDaño(eh.efectoID) == 1
                                }
                            }
                        }
                    }
                }
                if (trampa == 3 || trampa == 0) {
                    continue
                }
            }
            var distancia = 10000
            val objetivos = ArrayList(
                ordenLuchadores(
                    if (daño) _lanzador.paramEquipoEnemigo.toInt() else _lanzador.paramEquipoAliado.toInt(),
                    Orden.NADA
                )
            )
            var celdaObjetivo: Celda? = null
            for (celda in _lanzador
                .celdaPelea?.id?.let {
                    Camino.celdasPosibleLanzamiento(
                        SH,
                        _lanzador,
                        _pelea.mapaCopia!!,
                        it,
                        (-1).toShort()
                    )
                }!!) {
                if (trampa == 1) {
                    if (celda.esTrampa()) {
                        continue
                    }
                } else if (trampa == 2) {
                    if (celda.esGlifo()) {
                        continue
                    }
                }
                for (objetivo in objetivos) {
                    val dist = objetivo!!.celdaPelea
                        ?.id?.let { Camino.distanciaDosCeldas(_pelea.mapaCopia, celda.id, it).toInt() }
                    if (dist != null) {
                        if (dist - tamaño > 3) {
                            continue
                        }
                    }
                    if (dist != null) {
                        if (dist - tamaño < distancia) {
                            celdaObjetivo = celda
                            distancia = dist
                            if (dist == 0) {
                                break
                            }
                        }
                    }
                }
            }
            if (celdaObjetivo == null) {
                continue
            }
            if (AtlantaMain.MODO_DEBUG) {
                println(
                    "mejorGlifoTrampa() Hechizo: " + SH.hechizo!!.nombre + " (" + SH.hechizoID
                            + ") Celda: " + celdaObjetivo.id
                )
            }
            return Duo(celdaObjetivo, SH)
        }
        return null
    }

    private fun mejorTeleport(objetivo: Luchador?): Duo<Celda, StatHechizo>? {
        if (!_lanzador.puedeJugar()) {
            return null
        }
        if (objetivo == null) {
            return null
        }
        for (SH in hechizosLanzables()) {
            val filtroIA = getFiltroIA(SH, objetivo, Accion.TELEPORTAR)
            if (filtroIA < 0) {
                continue
            }
            if (filtroIA == 0) {
                var esTeleport = false
                var esDaño = false
                for (EH in SH.efectosNormales) {
                    when (EH.efectoID) {
                        4 -> esTeleport = true
                        82, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 275, 276, 277, 278, 279 -> esDaño =
                            true
                    }
                }
                if (!esTeleport || esDaño) {
                    continue
                }
            }
            var distancia = 1000
            var celdaObjetivo: Celda? = null
            val celdas = _lanzador
                .celdaPelea?.id?.let {
                    Camino.celdasPosibleLanzamiento(
                        SH,
                        _lanzador,
                        _pelea.mapaCopia!!,
                        it,
                        (-1).toShort()
                    )
                }
            if (celdas != null) {
                for (celda in celdas) {
                    val dist = objetivo.celdaPelea
                        ?.id?.let { Camino.distanciaDosCeldas(_pelea.mapaCopia, celda.id, it).toInt() }
                    if (dist != null) {
                        if (dist < distancia) {
                            celdaObjetivo = celda
                            distancia = dist
                        }
                    }
                }
            }
            if (celdaObjetivo == null) {
                continue
            }
            if (AtlantaMain.MODO_DEBUG) {
                println(
                    "mejorTeleport() Hechizo: " + SH.hechizo!!.nombre + " (" + SH.hechizoID
                            + ") Celda: " + celdaObjetivo.id
                )
            }
            return Duo(celdaObjetivo, SH)
        }
        return null
    }

    private fun calculaInfluenciaBuff(
        mapa: Mapa?, SH: StatHechizo?, celdaLanzamientoID: Short,
        celdaLanzadorID: Short, filtroIA: Int
    ): Int {
        if (SH == null) {
            return -1
        }
        var obligarUsar = false
        var influenciaTotal = 0
        // int suerte = 0, suerteMax = 0, azar = 0;
// boolean tiene666 = false;
        var efectos = SH.efectosNormales
        if (SH.efectosCriticos != null && SH.efectosCriticos.isNotEmpty()) {
            efectos = SH.efectosCriticos
        }
        var matanza = false
        if (filtroIA == 0) {
            var retorna: Byte = 0
            for (EH in efectos) { // if (EH.getEfectoID() == 666)
// tiene666 = true;
// suerteMax += EH.getSuerte();
                when (EH.efectoID) {
                    108, 81 -> if (retorna.toInt() == 0) {
                        retorna = 1
                    }
                    4, 5, 6, 82, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 180, 181, 185, 275, 276, 277, 278, 279, 780 -> return -1
                    141, 405 -> {
                        matanza = true
                        retorna = 3
                    }
                    else -> retorna = 3
                }
            }
            if (retorna < 2) {
                return -1
            }
        }
        // if (suerteMax > 0) {
// azar = Formulas.getRandomValor(1, suerteMax);
// }
        for (EH in efectos) {
            val listaLuchadores = _lanzador.let { Hechizo.getObjetivosEfecto(mapa, it, EH, celdaLanzamientoID) }
            when (EH.efectoID) {
                141, 405 -> {
                    matanza = true
                    if (listaLuchadores != null) {
                        if (listaLuchadores.isEmpty()) {
                            return -1
                        }
                    }
                }
            }
            val max = EH.valorParaPromediar
            loop@ for (objetivo in listaLuchadores) {
                if (_lanzador.let { HechizoLanzado.puedeLanzPorObjetivo(it, objetivo.id, SH) }) {
                    continue
                }
                if (objetivo.estaMuerto()) {
                    continue
                }
                var influencia = 0
                if (_influencias.containsKey(objetivo) && _influencias[objetivo]!!.containsKey(EH)) {
                    influencia = _influencias[objetivo]!![EH]!!
                } else {
                    influencia = _lanzador.let {
                        Constantes.getInflBuffPorEfecto(
                            EH.efectoID, it, objetivo, max, celdaLanzamientoID,
                            SH
                        )
                    }
                    if (!_influencias.containsKey(objetivo)) {
                        _influencias[objetivo] = HashMap()
                    }
                    _influencias[objetivo]!![EH] = influencia
                }
                if (influencia == 0) {
                    continue
                }
                if (EH.suerte == 0) {
                    if (!objetivo.esInvocacion()) {
                        if (influencia > 0) {
                            influencia += 1000
                        } else {
                            influencia -= 1000
                        }
                    }
                }
                when (EH.efectoID) {
                    77, 84, 266, 267, 268, 269, 270 -> {
                        if (matanza) {
                            influenciaTotal += influencia * max
                            break@loop
                        }
                        var preTotal = influencia * max
                        if (_lanzador.equipoBin == objetivo.equipoBin) {
                            preTotal = -preTotal
                            if (influencia < 0) {
                                preTotal += objetivo.nivel
                            }
                        }
                        if (preTotal > 0) {
                            obligarUsar = true
                        }
                        influenciaTotal += preTotal
                    }
                    else -> {
                        var preTotal = influencia * max
                        if (_lanzador.equipoBin == objetivo.equipoBin) {
                            preTotal = -preTotal
                            if (influencia < 0) {
                                preTotal += objetivo.nivel
                            }
                        }
                        if (preTotal > 0) {
                            obligarUsar = true
                        }
                        influenciaTotal += preTotal
                    }
                }
            }
        }
        if (filtroIA == 1 && obligarUsar && influenciaTotal <= 0) {
            influenciaTotal = 1
        }
        return influenciaTotal
    }

    private fun calculaInfluenciaCura(
        mapa: Mapa?, SH: StatHechizo?, celdaLanzamientoID: Short,
        celdaLanzadorID: Short, filtroIA: Int
    ): Int {
        if (SH == null) {
            return -1
        }
        var obligarUsar = false
        var influenciaTotal = 0
        // int suerte = 0, suerteMax = 0, azar = 0;
// boolean tiene666 = false;
        var efectos = SH.efectosNormales
        if (SH.efectosCriticos != null && SH.efectosCriticos.isNotEmpty()) {
            efectos = SH.efectosCriticos
        }
        if (filtroIA == 0) {
            var retorna: Byte = 0
            for (EH in efectos) { // if (EH.getEfectoID() == 666)
// tiene666 = true;
// suerteMax += EH.getSuerte();
                when (EH.efectoID) {
                    108, 81 -> retorna = 3
                    4, 5, 6, 82, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 180, 181, 185, 275, 276, 277, 278, 279, 780 -> return -1
                    141, 405 -> {
                    }
                    else -> {
                    }
                }
            }
            if (retorna < 2) {
                return -1
            }
        }
        // if (suerteMax > 0) {
// azar = Formulas.getRandomValor(1, suerteMax);
// }
        for (EH in efectos) {
            val listaLuchadores = _lanzador.let { Hechizo.getObjetivosEfecto(mapa, it, EH, celdaLanzamientoID) }
            when (EH.efectoID) {
                141, 405 -> if (listaLuchadores != null) {
                    if (listaLuchadores.isEmpty()) {
                        return -1
                    }
                }
            }
            val max = EH.valorParaPromediar
            if (listaLuchadores != null) {
                for (objetivo in listaLuchadores) {
                    if (_lanzador.let { HechizoLanzado.puedeLanzPorObjetivo(it, objetivo.id, SH) }) {
                        continue
                    }
                    if (objetivo.estaMuerto()) {
                        continue
                    }
                    var influencia = 0
                    if (_influencias.containsKey(objetivo) && _influencias[objetivo]!!.containsKey(EH)) {
                        influencia = _influencias[objetivo]!![EH]!!
                    } else {
                        influencia = _lanzador.let {
                            Constantes.getInflDañoPorEfecto(
                                EH.efectoID, it, objetivo, max, celdaLanzamientoID,
                                SH
                            )
                        }
                        if (!_influencias.containsKey(objetivo)) {
                            _influencias[objetivo] = HashMap()
                        }
                        _influencias[objetivo]!![EH] = influencia
                    }
                    if (influencia == 0) {
                        continue
                    }
                    if (EH.suerte == 0) {
                        if (!objetivo.esInvocacion()) {
                            if (influencia > 0) {
                                influencia += 1000
                            } else {
                                influencia -= 1000
                            }
                        }
                    }
                    var preTotal = influencia * max
                    if (_lanzador.equipoBin == objetivo.equipoBin) {
                        preTotal = -preTotal
                    }
                    if (preTotal > 0) {
                        obligarUsar = true
                    }
                    influenciaTotal += preTotal
                }
            }
        }
        if (filtroIA == 1 && obligarUsar && influenciaTotal <= 0) {
            influenciaTotal = 1
        }
        return influenciaTotal
    }

    private fun calculaInfluenciaDaño(
        mapa: Mapa?, SH: StatHechizo?, celdaLanzamientoID: Short,
        celdaLanzadorID: Short, filtroIA: Int
    ): Int {
        if (SH == null) {
            return -1
        }
        var obligarUsar = false
        var influenciaTotal = 0
        // int suerte = 0, suerteMax = 0, azar = 0;
// boolean tiene666 = false;
        var efectos = SH.efectosNormales
        if (SH.efectosCriticos != null && SH.efectosCriticos.isNotEmpty()) {
            efectos = SH.efectosCriticos
        }
        var matanza = false
        var retorna: Byte = 0
        if (filtroIA == 0) {
            loop@ for (EH in efectos) { // if (EH.getEfectoID() == 666)
// tiene666 = true;
// suerteMax += EH.getSuerte();
                when (EH.efectoID) {
                    4 -> continue@loop
                    180, 181, 185, 780 ->  // System.out.println("calculaInf efectos -1");
                        return -1
                    5, 6, 8, 77, 82, 84, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 116, 127, 131, 132, 140, 145, 152, 153, 154, 155, 156, 157, 162, 163, 168, 169, 215, 216, 217, 218, 219, 266, 267, 268, 269, 270, 271, 275, 276, 277, 278, 279 -> retorna =
                        3
                    141, 405 -> {
                        matanza = true
                        retorna = 3
                    }
                }
            }
            if (retorna < 2) {
                return -1
            }
            retorna = 0
        }
        // if (suerteMax > 0) {
// azar = Formulas.getRandomValor(1, suerteMax);
// }
        loop@ for (EH in efectos) {
            val listaLuchadores = _lanzador.let { Hechizo.getObjetivosEfecto(mapa, it, EH, celdaLanzamientoID) }
            var esDaño = false
            when (EH.efectoID) {
                4 -> continue@loop
                141, 405 -> {
                    if (listaLuchadores != null) {
                        if (listaLuchadores.isEmpty()) {
                            return -1
                        }
                    }
                    esDaño = true
                }
                5, 6, 8, 77, 82, 84, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 116, 127, 131, 132, 140, 145, 152, 153, 154, 155, 156, 157, 162, 163, 168, 169, 215, 216, 217, 218, 219, 266, 267, 268, 269, 270, 271, 275, 276, 277, 278, 279 -> esDaño =
                    true
            }
            val max = EH.valorParaPromediar
            loop1@ for (objetivo in listaLuchadores) {
                if (_lanzador.let { HechizoLanzado.puedeLanzPorObjetivo(it, objetivo.id, SH) }) {
                    continue
                }
                if (objetivo.estaMuerto()) {
                    continue
                }
                var influencia = 0
                if (_influencias.containsKey(objetivo) && _influencias[objetivo]!!.containsKey(EH)) {
                    influencia = _influencias[objetivo]!![EH]!!
                } else {
                    influencia = _lanzador.let {
                        Constantes.getInflDañoPorEfecto(
                            EH.efectoID, it, objetivo, max, celdaLanzamientoID,
                            SH
                        )
                    }
                    if (!_influencias.containsKey(objetivo)) {
                        _influencias[objetivo] = HashMap()
                    }
                    _influencias[objetivo]!![EH] = influencia
                }
                if (influencia == 0) {
                    continue
                }
                if (EH.suerte == 0) {
                    if (!objetivo.esInvocacion()) {
                        if (influencia > 0) {
                            influencia += 1000
                        } else {
                            influencia -= 1000
                        }
                    }
                }
                when (EH.efectoID) {
                    77, 84, 266, 267, 268, 269, 270 -> {
                        if (matanza) {
                            influenciaTotal += influencia * max
                            break@loop1
                        }
                        var preTotal = influencia * max
                        if (_lanzador.esIAChafer() || _lanzador.equipoBin != objetivo.equipoBin) {
                            if (esDaño && influencia > 0) {
                                retorna = 3
                            }
                        } else {
                            preTotal = -preTotal
                        }
                        if (preTotal > 0) {
                            obligarUsar = true
                        }
                        influenciaTotal += preTotal
                    }
                    else -> {
                        var preTotal = influencia * max
                        if (_lanzador.esIAChafer() || _lanzador.equipoBin != objetivo.equipoBin) {
                            if (esDaño && influencia > 0) {
                                retorna = 3
                            }
                        } else {
                            preTotal = -preTotal
                        }
                        if (preTotal > 0) {
                            obligarUsar = true
                        }
                        influenciaTotal += preTotal
                    }
                }
                break
            }
        }
        if (filtroIA == 1 && obligarUsar && influenciaTotal <= 0) {
            influenciaTotal = 1
        } else if (retorna < 2) {
            return -1
        }
        return influenciaTotal
    }

    enum class EstadoLanzHechizo {
        PODER, NO_TIENE_PA, NO_TIENE_ALCANCE, OBJETIVO_NO_PERMITIDO, NO_PODER, FALLAR, COOLDOWN, NO_OBJETIVO
    }

    enum class EstadoDistancia {
        TACLEADO, ACERCARSE, NO_PUEDE, INVISIBLE
    }

    enum class EstadoMovAtq {
        SE_MOVIO, NO_HIZO_NADA, LANZO_HECHIZO, TACLEADO, NO_PUEDE_MOVERSE, NO_TIENE_HECHIZOS, TIENE_HECHIZOS_SIN_LANZAR, NULO
    }

    enum class Orden {
        PDV_MENOS_A_MAS, PDV_MAS_A_MENOS, NADA, NIVEL_MENOS_A_MAS, NIVEL_MAS_A_MENOS, INVOS_PRIMEROS, INVOS_ULTIMOS
    }

    enum class Accion {
        ATACAR, BOOSTEAR, CURAR, TRAMPEAR, INVOCAR, TELEPORTAR, NADA
    }

    // sorts ordena siempre en orden ascendente
    class CompPDVMenosMas : Comparator<Luchador> {
        override fun compare(p1: Luchador, p2: Luchador): Int {
            return p1.pdvSinBuff.compareTo(p2.pdvSinBuff)
        }
    }

    class CompPDVMasMenos : Comparator<Luchador> {
        override fun compare(p1: Luchador, p2: Luchador): Int {
            return p2.pdvSinBuff.compareTo(p1.pdvSinBuff)
        }
    }

    class CompNivelMenosMas : Comparator<Luchador> {
        override fun compare(p1: Luchador, p2: Luchador): Int {
            return p1.nivel.compareTo(p2.nivel)
        }
    }

    class CompNivelMasMenos : Comparator<Luchador> {
        override fun compare(p1: Luchador, p2: Luchador): Int {
            return p2.nivel.compareTo(p1.nivel)
        }
    }

    class CompInvosUltimos : Comparator<Luchador> {
        override fun compare(p1: Luchador, p2: Luchador): Int {
            return if (!p1.esInvocacion()) {
                -1
            } else 1
        }
    }

    class CompInvosPrimeros : Comparator<Luchador> {
        override fun compare(p1: Luchador, p2: Luchador): Int {
            return if (p1.esInvocacion()) {
                -1
            } else 1
        }
    }

    companion object {
        private const val INTELIGENCIA_COMPARAR_INF_OBJETIVOS = 9
        private const val INTELIGENCIA_COMPARAR_INF_DIST_OBJETIVOS = 11
        private const val INTELIGENCIA_ORDENAR_PRIORIDAD_OBJETIVOS = 5
        private const val INTELIGENCIA_SOLO_CELDAS_CON_LUCHADOR = 7
        private const val PDV_MINIMO_CURAR = 99
        private fun estaDentroObjetivos(objetivo: Luchador?, objetivos: ArrayList<Luchador>, celda: Celda): Boolean {
            if (objetivo != null) {
                if (!objetivos.contains(objetivo)) {
                    return true
                }
                if (AtlantaMain.NIVEL_INTELIGENCIA_ARTIFICIAL <= INTELIGENCIA_SOLO_CELDAS_CON_LUCHADOR) {
                    return objetivo.celdaPelea !== celda
                }
            }
            return false
        }

        @JvmStatic
        fun tieneReenvio(lanzador: Luchador?, objetivo: Luchador?, SH: StatHechizo): Boolean {
            if (objetivo == null) {
                return false
            }
            return if (objetivo === lanzador) {
                false
            } else objetivo.getValorPorBuffsID(106) >= SH.grado
        }

        private fun ordena(lista: ArrayList<Luchador?>?, vararg orden: Orden) {
            for (o in orden) {
                if (o == Orden.PDV_MENOS_A_MAS) {
                    sort(CompPDVMenosMas())
                }
                if (o == Orden.PDV_MAS_A_MENOS) {
                    sort(CompPDVMasMenos())
                }
                if (o == Orden.NIVEL_MENOS_A_MAS) {
                    sort(CompNivelMenosMas())
                }
                if (o == Orden.NIVEL_MAS_A_MENOS) {
                    sort(CompNivelMasMenos())
                }
                if (o == Orden.INVOS_PRIMEROS) {
                    sort(CompInvosPrimeros())
                }
                if (o == Orden.INVOS_ULTIMOS) {
                    sort(CompInvosUltimos())
                }
            }
        }

        private fun esInvulnerable(objetivo: Luchador?): Boolean {
            if (objetivo == null) {
                return false
            }
            val stats = objetivo.totalStats
            return stats.getTotalStatParaMostrar(Constantes.STAT_REDUCCION_FISICA) > 100 || stats.getTotalStatParaMostrar(
                Constantes.STAT_REDUCCION_MAGICA
            ) > 100 || stats.getTotalStatParaMostrar(
                Constantes.STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA
            ) > 100
        }
    }

    init {
        _pelea.addIA(this)
        thread(false, true, null, null, NORM_PRIORITY, { run() })
//        this.isDaemon = true
//        priority = NORM_PRIORITY
    }
}

private fun sort(compPDVMenosMas: Comparator<Luchador>) {
}


