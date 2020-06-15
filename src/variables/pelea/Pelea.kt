package variables.pelea

import estaticos.*
import estaticos.Inteligencia.EstadoLanzHechizo
import estaticos.Mundo.Duo
import estaticos.database.GestorSQL
import servidor.ServidorSocket.AccionDeJuego
import sprites.PreLuchador
import utilidades.logdropeo
import variables.gremio.Recaudador
import variables.hechizo.EfectoHechizo
import variables.hechizo.EfectoHechizo.TipoDaño
import variables.hechizo.Hechizo
import variables.hechizo.HechizoLanzado
import variables.hechizo.StatHechizo
import variables.mapa.Celda
import variables.mapa.Mapa
import variables.mision.MisionObjetivoModelo
import variables.mob.AparecerMobs.Aparecer
import variables.mob.GrupoMob
import variables.mob.MobGrado
import variables.mob.MobModelo.TipoGrupo
import variables.montura.Montura
import variables.objeto.Objeto
import variables.objeto.ObjetoModelo.CAPACIDAD_STATS
import variables.pelea.Reto.EstReto
import variables.personaje.GrupoKoliseo
import variables.personaje.MisionPVP
import variables.personaje.Personaje
import variables.ranking.RankingPVP
import variables.zotros.Accion
import variables.zotros.Prisma
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference
import java.util.regex.Pattern
import javax.swing.Timer
import kotlin.collections.ArrayList
import kotlin.collections.Map.Entry
import kotlin.math.*

class Pelea {
    private val _equipo1 = ConcurrentHashMap<Int, Luchador>()
    private val _equipo2 = ConcurrentHashMap<Int, Luchador>()
    private val _espectadores = ConcurrentHashMap<Int, Luchador>()
    private val _asesinos = StringBuilder()
    private val _celdasPos1 = ArrayList<Celda>()
    private val _celdasPos2 = ArrayList<Celda>()
    private val _inicioLuchEquipo1 = ArrayList<Luchador>(9)
    private val _inicioLuchEquipo2 = ArrayList<Luchador>(9)
    private val _ordenLuchadores = ArrayList<Luchador>()
    private val _listaMuertos = ArrayList<Luchador>()
    private val _IAs = ArrayList<Inteligencia>()
    private val _posInicialLuch = TreeMap<Int, Celda>()
    private var _tacleado: Boolean = false
    private var _cerrado1: Boolean = false
    private var _soloGrupo1: Boolean = false
    private var _cerrado2: Boolean = false
    private var _soloGrupo2: Boolean = false
    private var _sinEspectador: Boolean = false
    private var _ayuda1: Boolean = false
    private var _ayuda2: Boolean = false
    private var _deshonor: Boolean = false
    private var _byteColor2: Byte = 0
    private var _byteColor1: Byte = 0
    private var _cantMuertosReto2x1: Byte = 0
    private var _cantUltAfec: Byte = 0
    var fase: Byte = 0
        private set
    private var _tipo: Byte = -1
    private var _vecesQuePasa: Byte = 0
    private var _alinPelea = Constantes.ALINEACION_NEUTRAL
    private var _cantCAC: Byte = 0
    private var _nroOrdenLuc: Byte = -1
    var id: Short = 0
        private set
    private var _celdaID1: Short = 0
    private var _celdaID2: Short = 0
    private var _bonusEstrellas = -1
    private var _tiempoHechizo: Int = 0
    private var _idLuchInit1: Int = 0
    private var _idLuchInit2: Int = 0// _idMobReto,
    private var _ultimaInvoID = 0
    private var _idUnicaAccion = -1
    private var _tiempoPreparacion: Long = 0
    private var _tiempoCombate: Long = 0
    private var _tiempoTurno: Long = 0
    private var _kamasRobadas: Long = 0
    private var _expRobada: Long = 0
    private var _tempAccion = ""
    private var _listadefensores = ""
    var mapaCopia: Mapa? = null
        private set
    var mapaReal: Mapa? = null
        private set
    private var _luchInit1: Luchador? = null
    private var _luchInit2: Luchador? = null
    var luchadorTurno: Luchador? = null
        private set
    var mobGrupo: GrupoMob? = null
    private var _rebootTurno: Timer? = null
    private var _glifos: CopyOnWriteArrayList<Glifo>? = null
    var trampas: CopyOnWriteArrayList<Trampa>? = null
        private set
    private var _capturadores: MutableList<Luchador>? = null
    private var _domesticadores: MutableList<Luchador>? = null
    var retos: ConcurrentHashMap<Byte, Reto>? = null
        private set
    private var _objetosRobados: ArrayList<Objeto>? = null
    private var _acciones: ArrayList<Accion>? = null
    private var _posiblesBotinPelea: ArrayList<Botin>? = null
    var salvarMobHeroico: Boolean = false
    private var _1vs1: Boolean = false
    var prospeccionEquipo: Int = 0
        private set
    private var _luchMenorNivelReto: Int = 0
    var ultimoElementoReto = Constantes.ELEMENTO_NULO.toInt()
    private var _ultimoMovedorIDReto: Int = 0
    private var _ultimoTipoDañoReto = TipoDaño.NULL
    private var _tiempoesperaturno = 0L
    private var _tiempoesperatemp = 0L
    private var animacionhechizo = false
    private var _timerPelea: Timer? = Timer(1) { e -> actionListener() }
    private var existioempuje = false

    val inicioLuchEquipo1: List<Luchador>
        get() = _inicioLuchEquipo1

    val inicioLuchEquipo2: List<Luchador>
        get() = _inicioLuchEquipo2

    // private void checkeaInicioPelea() {
    // switch (_estadoPelea) {
    // case Informacion.PELEA_ESTADO_INICIO :
    // case Informacion.PELEA_ESTADO_POSICION :
    // if (getTiempoFaltInicioPelea() <= 0) {
    // try {
    // iniciarPelea();
    // } catch (final Exception e) {
    // e.printStackTrace();
    // acaboPelea((byte) 2);
    // }
    // }
    // break;
    // }
    // }
    val tiempoFaltInicioPelea: Long
        get() = max(0, AtlantaMain.SEGUNDOS_INICIO_PELEA * 1000 - (System.currentTimeMillis() - _tiempoPreparacion))

    val posInicial: Map<Int, Celda>
        get() = _posInicialLuch

    val IDLuchInit2: Int
        get() = if (_luchInit2 == null) {
            -1
        } else _luchInit2!!.id

    val tipoPelea: Int
        get() = _tipo.toInt()

    val ordenLuchadores: ArrayList<Luchador>
        get() = _ordenLuchadores

    val PMLuchadorTurno: Int
        get() = luchadorTurno!!.pmRestantes
    val ID: Short
        get() = id
    val strAcciones: String
        get() {
            if (_acciones == null) {
                return ""
            }
            val str = StringBuilder()
            for (accion in _acciones!!) {
                str.append("\nAccion ID: ").append(accion.id).append(", Arg: ").append(accion.args)
            }
            return str.toString()
        }

    private val luchadorOrden: Luchador?
        get() {
            return try {
                if (_nroOrdenLuc < 0 || _nroOrdenLuc >= _ordenLuchadores.size) {
                    _nroOrdenLuc = 0
                }
                _ordenLuchadores[_nroOrdenLuc.toInt()]
            } catch (e: Exception) {
                null
            }

        }

    val sigIDLuchador: Int
        get() = --_ultimaInvoID

    val listaMuertos: ArrayList<Luchador>
        get() = _listaMuertos

    constructor(
        id: Short, mapa: Mapa, pre1: PreLuchador, pre2: PreLuchador, celda1: Short,
        celda2: Short, tipo: Byte, grupoMob: GrupoMob?, posPelea: String
    ) {
        try {
            pre1.setPelea(this)
            pre2.setPelea(this)
            fase = Constantes.PELEA_FASE_POSICION
            _celdaID1 = celda1
            _celdaID2 = celda2
            if (_celdaID1 == _celdaID2) {
                var nCelda: Short = -1
                for (i in 1..4) {
                    for (c in Camino.celdasPorDistancia(mapa.getCelda(_celdaID2), mapa, i)) {
                        val celda = mapa.getCelda(c)
                        if (celda!!.id == _celdaID1 || !celda.esCaminable(false)) {
                            continue
                        }
                        nCelda = celda.id
                        break
                    }
                    if (nCelda.toInt() != -1) {
                        break
                    }
                }
                _celdaID2 = nCelda
            }
            _tiempoPreparacion = System.currentTimeMillis()
            _tiempoCombate = _tiempoPreparacion
            _tipo = tipo
            this.id = id
            mapaCopia = mapa.copiarMapa(posPelea)
            mapaReal = mapa
            _alinPelea = mapaReal!!.subArea!!.alineacion
            _luchInit1 = Luchador(this, pre1, false)
            _idLuchInit1 = _luchInit1!!.id
            _equipo1[_idLuchInit1] = _luchInit1!!
            _luchInit2 = Luchador(this, pre2, false)
            _idLuchInit2 = _luchInit2!!.id
            _equipo2[_idLuchInit2] = _luchInit2!!
            if (_tipo != Constantes.PELEA_TIPO_DESAFIO) {
                startTimerInicioPelea()
                if (_tipo != Constantes.PELEA_TIPO_PVP && _luchInit1!!.alineacion != Constantes.ALINEACION_NULL && _luchInit2!!
                        .alineacion != Constantes.ALINEACION_NULL && _luchInit1!!.alineacion != _luchInit2!!.alineacion
                ) {
                    _tipo = Constantes.PELEA_TIPO_PRISMA
                }
            }
            GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(
                this,
                3,
                fase.toInt(),
                _tipo == Constantes.PELEA_TIPO_DESAFIO,
                true,
                false,
                (if (_tipo == Constantes.PELEA_TIPO_DESAFIO) 0 else AtlantaMain.SEGUNDOS_INICIO_PELEA * 1000 - 1500).toLong(),
                _tipo.toInt()
            )
            definirCeldasPos()
            if (pre1.javaClass == Personaje::class.java) {
                GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(pre1 as Personaje, 0)
                pre1.celda = null
            }
            if (pre2.javaClass == Personaje::class.java) {
                GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(pre2 as Personaje, 0)
                pre2.celda = null
            }
            GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(mapaReal!!, _idLuchInit1)
            GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(mapaReal!!, _idLuchInit2)
            _luchInit1!!.celdaPelea = getCeldaRandom(_celdasPos1)
            _luchInit2!!.celdaPelea = getCeldaRandom(_celdasPos2)
            _luchInit1!!.equipoBin = 0.toByte()
            _luchInit2!!.equipoBin = 1.toByte()
            if (_tipo != Constantes.PELEA_TIPO_PVM_NO_ESPADA) {
                GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_EN_MAPA(mapaReal!!, mostrarEspada())
                GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapaReal!!, _idLuchInit1, _luchInit1!!)
                GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(mapaReal!!, _idLuchInit2, _luchInit2!!)
            }
            GestorSalida.ENVIAR_GM_LUCHADORES_A_PELEA(this, 7, mapaCopia!!)
            if (pre2.javaClass == Prisma::class.java) {
                val str = mapaReal!!.id.toString() + "|" + mapaReal!!.x + "|" + mapaReal!!.y
                for (p in Mundo.PERSONAJESONLINE) {
                    if (p.alineacion != pre2.Alineacion) {
                        continue
                    }
                    GestorSalida.ENVIAR_CA_MENSAJE_ATAQUE_PRISMA(p, str)
                }
            } else if (pre2.javaClass == Recaudador::class.java) {
                val recaudador = pre2 as Recaudador
                recaudador.actualizarAtacantesDefensores()
                val str = recaudador.mensajeDeAtaque()
                for (p in recaudador.gremio!!.miembros) {
                    if (p != null) {
                        if (p.enLinea()) {
                            GestorSalida.ENVIAR_gA_MENSAJE_SOBRE_RECAUDADOR(p, str)
                        }
                    }
                }
            }
            if (grupoMob != null) {
                setGrupoMob(grupoMob)
            }
            if (_luchInit1!!.personaje != null) {
                cargarMultiman(_luchInit1!!.personaje)
            }
            if (_luchInit2!!.personaje != null) {
                cargarMultiman(_luchInit2!!.personaje)
            }
            GestorSalida.ENVIAR_fL_LISTA_PELEAS_AL_MAPA(mapaReal!!)
        } catch (e: Exception) {
            e.printStackTrace()
            pre1.setPelea(null)
            pre2.setPelea(null)
        }

    }

    constructor(id: Short, mapa: Mapa, grupo1: GrupoKoliseo, grupo2: GrupoKoliseo, posPelea: String) {
        fase = Constantes.PELEA_FASE_POSICION
        _tiempoPreparacion = System.currentTimeMillis()
        _tiempoCombate = _tiempoPreparacion
        _tipo = Constantes.PELEA_TIPO_KOLISEO
        this.id = id
        mapaCopia = mapa.copiarMapa(posPelea)
        mapaReal = mapa
        _alinPelea = mapaReal!!.subArea!!.alineacion
        var b = false
        for (perso in grupo1.miembros) {
            GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(perso.mapa, perso.Id)
            perso.celda = null
            if (perso.mapa.id != mapa.id) {
                perso.teleportSinTodos(mapa.id, 100.toShort())
            }
            perso.pelea = this
            val l = Luchador(this, perso, false)
            l.equipoBin = 0.toByte()
            _equipo1[perso.Id] = l
            if (!b) {
                _luchInit1 = l
                _idLuchInit1 = _luchInit1!!.id
                b = true
            }
            GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(perso, 0)
        }
        b = false
        for (perso in grupo2.miembros) {
            GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(perso.mapa, perso.Id)
            perso.celda = null
            if (perso.mapa.id != mapa.id) {
                perso.teleportSinTodos(mapa.id, 100.toShort())
            }
            perso.pelea = this
            val l = Luchador(this, perso, false)
            l.equipoBin = 1.toByte()
            _equipo2[perso.Id] = l
            if (!b) {
                _luchInit2 = l
                _idLuchInit2 = _luchInit2!!.id
                b = true
            }
            GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(perso, 0)
        }
//        try {
//            Thread.sleep(1500)
//        } catch (ignored: Exception) {
//        }

        GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(
            this,
            3,
            2,
            false,
            true,
            false,
            (AtlantaMain.SEGUNDOS_INICIO_PELEA * 1000 - 1500).toLong(),
            _tipo.toInt()
        )
        startTimerInicioPelea()
        val equipo1 = ArrayList<Entry<Int, Luchador>>(_equipo1.entries)
        val equipo2 = ArrayList<Entry<Int, Luchador>>(_equipo2.entries)
        definirCeldasPos()
        val gm1 = StringBuilder("GM")
        val gm2 = StringBuilder("GM")
        for ((_, lucha) in equipo1) {
            val celdaRandom = getCeldaRandom(_celdasPos1)
            if (celdaRandom == null) {
                _equipo1.remove(lucha.id)
                continue
            }
            lucha.celdaPelea = celdaRandom
            gm1.append("|+").append(lucha.stringGM(0))
        }
        for ((_, lucha) in equipo2) {
            val celdaRandom = getCeldaRandom(_celdasPos2)
            if (celdaRandom == null) {
                _equipo2.remove(lucha.id)
                continue
            }
            lucha.celdaPelea = celdaRandom
            gm2.append("|+").append(lucha.stringGM(0))
        }
        for (persos in grupo1.miembros) {
            GestorSalida.enviarEnCola(persos, gm1.toString(), true)
            if (AtlantaMain.PARAM_VER_JUGADORES_KOLISEO) {
                GestorSalida.enviarEnCola(persos, gm2.toString(), true)
            }
        }
        if (AtlantaMain.MOSTRAR_ENVIOS) {
            println("GM LUCHADORES TEAM 1: KOLISEO>> $gm1")
        }
        for (persos in grupo2.miembros) {
            GestorSalida.enviarEnCola(persos, gm2.toString(), true)
            if (AtlantaMain.PARAM_VER_JUGADORES_KOLISEO) {
                GestorSalida.enviarEnCola(persos, gm1.toString(), true)
            }
        }
        GestorSalida.ENVIAR_fL_LISTA_PELEAS_AL_MAPA(mapaReal!!)
        if (AtlantaMain.MOSTRAR_ENVIOS) {
            println("GM LUCHADORES TEAM 2: KOLISEO>> $gm2")
        }
    }

    fun getPosPelea(color: Int): Int {
        when (color) {
            1 -> return _celdasPos1.size
            2 -> return _celdasPos2.size
        }
        return 0
    }

    fun setUltimoTipoDaño(t: TipoDaño) {
        _ultimoTipoDañoReto = t
    }

    private fun addDropPelea(dropM: DropMob) {
        if (_posiblesBotinPelea == null) {
            _posiblesBotinPelea = ArrayList()
        }
        for (dropP in _posiblesBotinPelea!!) {
            if (dropM.esIdentico(dropP.drop)) {
                dropP.addBotinMaximo(dropM.maximo)
                return
            }
        }
        _posiblesBotinPelea!!.add(Botin(dropM))
    }

    fun setDeshonor(d: Boolean) {
        _deshonor = d
    }

    fun setUltAfec(afec: Byte) {
        _cantUltAfec = afec
    }

    fun setListaDefensores(str: String) {
        _listadefensores = str
    }

    // public void setIDMobReto(final int mob) {
    // _idMobReto = mob;
    // }
    //
    // public int getIDMobReto() {
    // return _idMobReto;
    // }
    private fun setGrupoMob(gm: GrupoMob) {
        mobGrupo = gm
        if (mobGrupo == null) {
            return
        }
        mobGrupo!!.setPelea(this)
        if (_tipo == Constantes.PELEA_TIPO_PVM) {
            _bonusEstrellas = mobGrupo!!.bonusEstrellas
        }
    }

    @Synchronized
    private fun cargarMultiman(perso: Personaje?) {
        if (!AtlantaMain.PERMITIR_MULTIMAN_TIPO_COMBATE.contains(_tipo)) {
            return
        }
        if (perso == null || perso.compañero != null) {
            return
        }
        val obj = perso.getObjPosicion(Constantes.OBJETO_POS_COMPAÑERO) ?: return
        var mobMultiman = 0
        try {
            mobMultiman = Integer.parseInt(obj.getParamStatTexto(Constantes.STAT_MAS_COMPAÑERO, 3), 16)
        } catch (ignored: Exception) {
        }

        val mobModelo = Mundo.getMobModelo(mobMultiman) ?: return
        val idMultiman = sigIDLuchadores()
        val multiman = Personaje.crearMultiman(
            idMultiman, perso.nivel, perso.totalStatsPelea
                .getTotalStatParaMostrar(Constantes.STAT_MAS_INICIATIVA), mobModelo
        )
        multiman.mapa = mapaCopia
        multiman.celda = perso.celda
        if (unirsePelea(multiman, perso.Id)) {
            perso.compañero = multiman
            multiman.compañero = perso
        }
    }

    @Synchronized
    fun sigIDLuchadores(): Int {
        var id = 0
        for (luchador in luchadoresDeEquipo(3)) {
            if (luchador.id < id) {
                id = luchador.id
            }
        }
        return --id
    }

    private fun definirCeldasPos() {
        val b = Formulas.randomBoolean
        _byteColor1 = (if (b) 1 else 0).toByte()
        _byteColor2 = (if (_byteColor1.toInt() == 1) 0 else 1).toByte()
        when (mapaReal!!.colorCeldasAtacante.toLowerCase()) {
            "red" -> {
                _byteColor1 = 0
                _byteColor2 = 1
            }
            "blue" -> {
                _byteColor1 = 1
                _byteColor2 = 2
            }
        }
        analizarPosiciones(_byteColor1, _celdasPos1)
        analizarPosiciones(_byteColor2, _celdasPos2)
        GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 1, mapaCopia!!.strCeldasPelea(), _byteColor1.toInt())
        GestorSalida.ENVIAR_GP_POSICIONES_PELEA(this, 2, mapaCopia!!.strCeldasPelea(), _byteColor2.toInt())
    }

    private fun actionListener() {
        when (fase) {
            Constantes.PELEA_FASE_INICIO, Constantes.PELEA_FASE_POSICION -> try {
                iniciarPelea()
            } catch (e1: Exception) {
                acaboPelea(2.toByte())
            }

            Constantes.PELEA_FASE_COMBATE -> preFinTurno(null)
            Constantes.PELEA_FASE_FINALIZADO -> {
            }
        }
    }

    private fun startTimerInicioPelea() {
        if (_timerPelea != null) {
            _timerPelea!!.isRepeats = false
            _timerPelea!!.initialDelay = AtlantaMain.SEGUNDOS_INICIO_PELEA * 1000
            _timerPelea!!.delay = AtlantaMain.SEGUNDOS_INICIO_PELEA * 1000
            _timerPelea!!.restart()
        }
    }

    private fun startTimerInicioTurno() {
        if (_timerPelea != null) {
            _timerPelea!!.stop()
            _timerPelea!!.isRepeats = false
            _timerPelea!!.initialDelay = AtlantaMain.SEGUNDOS_TURNO_PELEA * 1000
            _timerPelea!!.delay = AtlantaMain.SEGUNDOS_TURNO_PELEA * 1000
            _timerPelea!!.restart()
        }
    }

    @Synchronized
    fun puedeUnirsePelea(pre: PreLuchador, idInitAUnir: Int): Celda? {
        var perso: Personaje? = null
        try {
            if (pre.javaClass == Personaje::class.java) {
                perso = pre as Personaje
            }
        } catch (ignored: Exception) {
        }

        if (fase > Constantes.PELEA_FASE_POSICION) {
            GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'l')
            return null
        }
        if (perso != null) {
            if (perso.calabozo || perso.estaInmovil()) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1DONT_MOVE_TEMP")
                return null
            }
            if (mobGrupo != null && !Condiciones.validaCondiciones(perso, mobGrupo!!.condUnirsePelea)) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'i')
                return null
            }
        }
        if (_equipo1.containsKey(idInitAUnir)) {
            if (_equipo1.size >= 8) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 't')
                return null
            }
            if (_soloGrupo1) {
                val grupo = _luchInit1!!.personaje!!.grupoParty
                if (grupo != null && !grupo.iDsPersos.contains(pre.id)) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'f')
                    return null
                }
            }
            if (_cerrado1) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'f')
                return null
            }
            if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA) {
                if (_luchInit1!!.alineacion != pre.Alineacion && pre.Alineacion != Constantes.ALINEACION_MERCENARIO) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'a')
                    return null
                }
                if (perso != null) {
                    if (!AtlantaMain.PARAM_PERMITIR_MULTICUENTA_PELEA_PVP) {
                        for (luch in _equipo1.values) {
                            try {
                                if (luch.personaje == null || luch.personaje!!.cuenta == null) {
                                    continue
                                }
                                if (luch.personaje!!.cuenta.actualIP.equals(perso.cuenta.actualIP, ignoreCase = true)) {
                                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'f')
                                    return null
                                }
                            } catch (ignored: Exception) {
                            }

                        }
                    }
                }
            } else if (_tipo == Constantes.PELEA_TIPO_RECAUDADOR) {
                if (perso != null) {
                    if (perso.gremio != null && _luchInit2!!.recaudador!!.gremio!!.id == perso.gremio!!
                            .id
                    ) {
                        GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'g')
                        return null
                    }
                    if (!AtlantaMain.PARAM_PERMITIR_MULTICUENTA_PELEA_RECAUDADOR) {
                        for (luch in _equipo1.values) {
                            try {
                                if (luch.personaje == null || luch.personaje!!.cuenta == null) {
                                    continue
                                }
                                if (luch.personaje!!.cuenta.actualIP.equals(perso.cuenta.actualIP, ignoreCase = true)) {
                                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'f')
                                    return null
                                }
                            } catch (ignored: Exception) {
                            }

                        }
                    }
                }
            }
        } else if (_equipo2.containsKey(idInitAUnir)) {
            if (_equipo2.size >= 8) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 't')
                return null
            }
            if (_soloGrupo2) {
                val grupo = _luchInit2!!.personaje!!.grupoParty
                if (grupo != null && !grupo.iDsPersos.contains(pre.id)) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'f')
                    return null
                }
            }
            if (_cerrado2) {
                GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'f')
                return null
            }
            if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA) {
                if (_luchInit2!!.alineacion != pre.Alineacion) {
                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'a')
                    return null
                }
                if (perso != null) {
                    if (!AtlantaMain.PARAM_PERMITIR_MULTICUENTA_PELEA_PVP) {
                        for (luch in _equipo2.values) {
                            try {
                                if (luch.personaje == null || luch.personaje!!.cuenta == null) {
                                    continue
                                }
                                if (luch.personaje!!.cuenta.actualIP.equals(perso.cuenta.actualIP, ignoreCase = true)) {
                                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'f')
                                    return null
                                }
                            } catch (ignored: Exception) {
                            }

                        }
                    }
                }
            } else if (_tipo == Constantes.PELEA_TIPO_RECAUDADOR) {
                if (perso != null) {
                    if (perso.gremio != null && _luchInit2!!.recaudador!!.gremio!!.id != perso.gremio!!
                            .id
                    ) {
                        GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'g')
                        return null
                    }
                    if (!AtlantaMain.PARAM_PERMITIR_MULTICUENTA_PELEA_RECAUDADOR) {
                        for (luch in _equipo2.values) {
                            try {
                                if (luch.personaje == null || luch.personaje!!.cuenta == null) {
                                    continue
                                }
                                if (luch.personaje!!.cuenta.actualIP.equals(perso.cuenta.actualIP, ignoreCase = true)) {
                                    GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'f')
                                    return null
                                }
                            } catch (ignored: Exception) {
                            }

                        }
                    }
                }
            }
        }
        var celda: Celda? = null
        if (_equipo1.containsKey(idInitAUnir)) {
            celda = getCeldaRandom(_celdasPos1)
            if (celda == null) {
                return null
            }
        } else if (_equipo2.containsKey(idInitAUnir)) {
            celda = getCeldaRandom(_celdasPos2)
            if (celda == null) {
                return null
            }
        }
        if (perso != null && !perso.esMultiman()) {
            if (perso.mapa.id != mapaCopia!!.id) {
                when (_tipo) {
                    Constantes.PELEA_TIPO_PRISMA, Constantes.PELEA_TIPO_RECAUDADOR -> if (perso.prePelea == null || perso.prePelea.mapaCopia!!.id != mapaCopia!!.id) {
                        perso.setPrePelea(this, idInitAUnir)
                        perso.teleportSinTodos(mapaCopia!!.id, Objects.requireNonNull(celda)!!.id)
                        return null
                    }
                    else -> {
                        GestorSalida.ENVIAR_GA903_ERROR_PELEA(perso, 'p')
                        return null
                    }
                }
            }
        }
        return celda
    }

    @Synchronized
    fun unirsePelea(pre: PreLuchador, idInitAUnir: Int): Boolean {
        val celda = puedeUnirsePelea(pre, idInitAUnir) ?: return false
        var perso: Personaje? = null
        try {
            if (pre.javaClass == Personaje::class.java) {
                perso = pre as Personaje
            }
        } catch (ignored: Exception) {
        }

        pre.setPelea(this)
        val luchadorAUnirse = Luchador(this, pre, false)
        if (perso != null) {
            perso.setPrePelea(null, 0)
            if (perso.esMultiman()) {
                luchadorAUnirse.setIDReal(false)
            } else {
                GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA(perso, 0)
                GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(perso.mapa, perso.Id)
                perso.celda = null
            }
            if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA) {
                perso.botonActDesacAlas('+')
            }
        }
        if (_equipo1.containsKey(idInitAUnir)) {
            luchadorAUnirse.equipoBin = 0.toByte()
            _equipo1[pre.id] = luchadorAUnirse
        } else if (_equipo2.containsKey(idInitAUnir)) {
            luchadorAUnirse.equipoBin = 1.toByte()
            _equipo2[pre.id] = luchadorAUnirse
        }
        luchadorAUnirse.celdaPelea = celda
        GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(
            mapaReal!!, (if (luchadorAUnirse.equipoBin.toInt() == 0)
                _luchInit1
            else
                _luchInit2)!!.id, luchadorAUnirse
        )
        seUnioAPelea(pre)
        if (_deshonor && perso != null) {
            if (perso.addDeshonor(1)) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "084;" + perso.deshonor)
            }
        }
        if (luchadorAUnirse.personaje != null) {
            cargarMultiman(luchadorAUnirse.personaje)
        }
        return true
    }

    private fun seUnioAPelea(pre: PreLuchador) {
        var perso: Personaje? = null
        try {
            if (pre.javaClass == Personaje::class.java) {
                perso = pre as Personaje
            }
        } catch (ignored: Exception) {
        }

        if (perso != null && !perso.esMultiman()) {
            if (_tipo == Constantes.PELEA_TIPO_DESAFIO || fase == Constantes.PELEA_FASE_COMBATE) {
                GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(perso, fase.toInt(), true, true, false, 0, _tipo.toInt())
            } else {
                GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(
                    perso,
                    fase.toInt(),
                    false,
                    true,
                    false,
                    max(tiempoFaltInicioPelea - 1500, 0),
                    _tipo.toInt()
                )
            }
        }
        when (fase) {
            Constantes.PELEA_FASE_POSICION -> {
                if (perso != null && !perso.esMultiman()) {
                    if (_equipo1.containsKey(perso.Id)) {
                        GestorSalida.ENVIAR_GP_POSICIONES_PELEA(
                            perso,
                            mapaCopia!!.strCeldasPelea(),
                            _byteColor1.toInt()
                        )
                    } else if (_equipo2.containsKey(perso.Id)) {
                        GestorSalida.ENVIAR_GP_POSICIONES_PELEA(
                            perso,
                            mapaCopia!!.strCeldasPelea(),
                            _byteColor2.toInt()
                        )
                    }
                    GestorSalida.ENVIAR_GM_LUCHADORES_A_PERSO(this, mapaCopia!!, perso)
                }
                _luchInit2!!.preLuchador?.actualizarAtacantesDefensores()
            }
            Constantes.PELEA_FASE_COMBATE -> if (perso != null && !perso.esMultiman()) {
                GestorSalida.ENVIAR_GM_LUCHADORES_A_PERSO(this, mapaCopia!!, perso)
                GestorSalida.ENVIAR_GS_EMPEZAR_COMBATE(perso)
                try {
                    Thread.sleep(500)
                } catch (ignored: Exception) {
                }

                GestorSalida.ENVIAR_GTL_ORDEN_JUGADORES(perso, this)
                GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_PERSO(perso, this)
            }
            else -> return
        }
        GestorSalida.ENVIAR_GM_JUGADOR_UNIRSE_PELEA(this, 7, getLuchadorPorID(pre.id)!!)
    }

    fun unirseEspectador(perso: Personaje, siOsi: Boolean) {
        if (fase != Constantes.PELEA_FASE_COMBATE) {
            GestorSalida.ENVIAR_Im_INFORMACION(perso, "157")
            return
        }
        if (!siOsi && !perso.esIndetectable()) {
            if (perso.esFantasma() || perso.pelea != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1116")
                return
            }
            if (_sinEspectador) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "157")
                return
            }
        }
        perso.pelea = this
        val espectador = Luchador(this, perso, true)
        _espectadores[perso.Id] = espectador
        if (!siOsi) {
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7, "036;" + perso.nombre)
        } else {
            espectador.setEspectadorAdmin(true)
        }
        reconectandoMostrandoInfo(espectador, true)
        // actualizarNumTurnos(perso);
    }

    private fun enviarRetosPersonaje(perso: Personaje) {
        if (retos == null) {
            return
        }
        if (_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) {
            for (reto in retos!!.values) {
                GestorSalida.ENVIAR_Gd_RETO_A_PERSONAJE(perso, reto.infoReto())
                // if (reto.getEstado() == EstReto.REALIZADO) {
                // GestorSalida.ENVIAR_GdaK_RETO_REALIZADO(perso, _id);
                // } else if (reto.getEstado() == EstReto.FALLADO) {
                // GestorSalida.ENVIAR_GdaO_RETO_FALLADO(perso, _id);
                // }
            }
        }
    }

    fun desconectarLuchador(perso: Personaje?) {
        try {
            if (perso == null) {
                return
            }
            val luchador = getLuchadorPorID(perso.Id) ?: return
            luchador.setDesconectado(true)
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(
                this, 7, "1182;" + luchador.nombre + "~" + luchador
                    .turnosRestantes
            )
            if (!perso.esMultiman()) {
                desconectarLuchador(perso.compañero)
            }
            if (luchador.puedeJugar()) {
                luchador.setTurnosRestantes((luchador.turnosRestantes - 1).toByte().toInt())
                pasarTurno(luchador)
            }
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln("Exception desconectarLuchador $e")
            e.printStackTrace()
        }

    }

    fun reconectarLuchador(perso: Personaje?) {
        if (perso == null) {
            return
        }
        val luchador = getLuchadorPorID(perso.Id)
        if (luchador == null || !luchador.estaDesconectado()) {
            return
        }
        perso.mostrarGrupo()
        reconectandoMostrandoInfo(luchador, false)
    }

    private fun reconectandoMostrandoInfo(luchador: Luchador, espectador: Boolean) {
        try {
            val perso = luchador.personaje
            perso!!.celda = null
            if (espectador) {
                GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(perso.mapa, perso.Id)
            }
            try {
                Thread.sleep(500)
            } catch (ignored: InterruptedException) {
            }

            if (_tipo == Constantes.PELEA_TIPO_DESAFIO || fase == Constantes.PELEA_FASE_COMBATE) {
                GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(
                    perso,
                    fase.toInt(),
                    true,
                    !espectador,
                    espectador,
                    0,
                    _tipo.toInt()
                )
            } else {
                GestorSalida.ENVIAR_GJK_UNIRSE_PELEA(
                    perso, fase.toInt(), false, !espectador, espectador, max(
                        tiempoFaltInicioPelea - 1500, 0
                    ), _tipo.toInt()
                )
            }
            if (!espectador) {
                GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7, "1184;" + perso.nombre)
                luchador.setDesconectado(false)
                if (!perso.esMultiman()) {
                    val compañero = perso.compañero
                    if (compañero != null) {
                        GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7, "1184;" + compañero.nombre)
                        try {
                            getLuchadorPorID(compañero.Id)!!.setDesconectado(false)
                        } catch (ignored: Exception) {
                        }

                    }
                }
            }
            when (fase) {
                Constantes.PELEA_FASE_POSICION -> {
                    if (_equipo1.containsKey(perso.Id)) {
                        GestorSalida.ENVIAR_GP_POSICIONES_PELEA(
                            perso,
                            mapaCopia!!.strCeldasPelea(),
                            _byteColor1.toInt()
                        )
                    } else if (_equipo2.containsKey(perso.Id)) {
                        GestorSalida.ENVIAR_GP_POSICIONES_PELEA(
                            perso,
                            mapaCopia!!.strCeldasPelea(),
                            _byteColor2.toInt()
                        )
                    }
                    GestorSalida.ENVIAR_GM_LUCHADORES_A_PERSO(this, mapaCopia!!, perso)
                }
                Constantes.PELEA_FASE_COMBATE -> {
                    val tiempoRestante =
                        (AtlantaMain.SEGUNDOS_TURNO_PELEA * 1000 - (System.currentTimeMillis() - _tiempoTurno)).toInt()
                    GestorSalida.ENVIAR_GM_LUCHADORES_A_PERSO(this, mapaCopia!!, perso)
                    GestorSalida.ENVIAR_GS_EMPEZAR_COMBATE(perso)
                    try {
                        Thread.sleep(500)
                    } catch (ignored: InterruptedException) {
                    }

                    GestorSalida.ENVIAR_GTL_ORDEN_JUGADORES(perso, this)
                    GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_PERSO(perso, this)
                    try {
                        Thread.sleep(500)
                    } catch (ignored: InterruptedException) {
                    }

                    GestorSalida.ENVIAR_GTS_INICIO_TURNO_PELEA(perso, luchadorTurno!!.id, tiempoRestante)
                    enviarRetosPersonaje(perso)
                    mostrarBuffsDeTodosAPerso(perso)
                    mostrarGlifos(luchador)
                    mostrarTrampas(luchador)
                }
            }
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln("Exception mostrarTodaInfo $e")
            e.printStackTrace()
        }

    }

    private fun mostrarGlifos(luchador: Luchador) {
        if (_glifos != null) {
            for (glifo in _glifos!!) {
                GestorSalida.ENVIAR_GDZ_COLOREAR_ZONA_A_LUCHADOR(
                    luchador, "+", glifo.celda.id, glifo.tamaño.toInt(),
                    glifo.color, glifo.forma
                )
            }
        }
    }

    private fun mostrarTrampas(luchador: Luchador) {
        if (trampas != null) {
            for (trampa in trampas!!) {
                if (!trampa.esInvisiblePara(luchador.id)) {
                    GestorSalida.ENVIAR_GDZ_COLOREAR_ZONA_A_LUCHADOR(
                        luchador, "+", trampa.celda.id, trampa.tamaño.toInt(),
                        trampa.color, ' '
                    )
                    val permisos = BooleanArray(16)
                    val valores = IntArray(16)
                    permisos[2] = true
                    permisos[0] = true
                    valores[2] = 25
                    valores[0] = 1
                    GestorSalida.ENVIAR_GDC_ACTUALIZAR_CELDA_A_LUCHADOR(
                        luchador, trampa.celda.id, Encriptador
                            .stringParaGDC(permisos, valores), false
                    )
                }
            }
        }
    }

    private fun refrescarCeldas(luchador: Luchador) {
        for (a in mapaCopia!!.celdas.values) {
            if (a.primerLuchador != null) {
                val permisos = BooleanArray(16)
                val valores = IntArray(16)
                permisos[11] = false
                valores[11] = 0
                GestorSalida.ENVIAR_GDC_ACTUALIZAR_CELDA_A_LUCHADOR(
                    luchador, a.id, Encriptador
                        .stringParaGDC(permisos, valores), false
                )
            }
        }
    }

    fun addTrampa(trampa: Trampa) {
        if (trampas == null) {
            trampas = CopyOnWriteArrayList()
        }
        if (!trampas!!.contains(trampa))
            trampas!!.add(trampa)
    }

    fun borrarTrampa(trampa: Trampa) {
        if (trampas == null) {
            return
        }
        trampas!!.remove(trampa)
    }

    fun addGlifo(glifo: Glifo) {
        if (_glifos == null) {
            _glifos = CopyOnWriteArrayList()
        }
        if (!_glifos!!.contains(glifo))
            _glifos!!.add(glifo)
    }

    fun borrarGlifo(glifo: Glifo) {
        if (_glifos == null) {
            return
        }
        _glifos!!.remove(glifo)
    }

    private fun getCeldaRandom(celdas: List<Celda>): Celda? {
        if (celdas.isEmpty()) {
            return null
        }
        val celdas2 = ArrayList<Celda>()
        for (c in celdas) {
            if (c == null || c.primerLuchador != null) {
                continue
            }
            celdas2.add(c)
        }
        return if (celdas2.isEmpty()) {
            null
        } else celdas2[Formulas.getRandomInt(0, celdas2.size - 1)]
    }

    private fun analizarPosiciones(color: Byte, celdas: ArrayList<Celda>) {
        if (color.toInt() == 0) {
            for (s in mapaCopia!!.posTeamRojo1) {
                mapaCopia!!.getCelda(s)?.let { celdas.add(it) }
            }
        } else if (color.toInt() == 1) {
            for (s in mapaCopia!!.posTeamAzul2) {
                mapaCopia!!.getCelda(s)?.let { celdas.add(it) }
            }
        }
    }

    fun luchadoresDeEquipo(equipos: Int): ArrayList<Luchador> {
        var equipos = equipos
        try {
            val luchadores = ArrayList<Luchador>()
            if (equipos - 4 >= 0) {
                luchadores.addAll(_espectadores.values)
                equipos -= 4
            }
            if (equipos - 2 >= 0) {
                luchadores.addAll(_equipo2.values)
                equipos -= 2
            }
            if (equipos - 1 >= 0) {
                luchadores.addAll(_equipo1.values)
            }
            return luchadores
        } catch (e: Exception) {
            e.printStackTrace()
            return luchadoresDeEquipo(equipos)
        }

    }

    fun cantLuchDeEquipo(equipos: Int): Int {
        var equipos = equipos
        try {
            var luchadores = 0
            if (equipos - 4 >= 0) {
                luchadores += _espectadores.size
                equipos -= 4
            }
            if (equipos - 2 >= 0) {
                luchadores += _equipo2.size
                equipos -= 2
            }
            if (equipos - 1 >= 0) {
                luchadores += _equipo1.size
            }
            return luchadores
        } catch (e: Exception) {
            return cantLuchDeEquipo(equipos)
        }

    }

    @Synchronized
    fun cambiarPosMultiman(perso: Personaje, idMultiman: Int) {
        if (fase != Constantes.PELEA_FASE_POSICION) {
            return
        }
        val dueño = getLuchadorPorID(perso.Id)
        if (dueño == null || dueño.estaListo()) {
            return
        }
        if (perso.compañero == null) {
            return
        }
        val multiman = getLuchadorPorID(perso.compañero.Id)
        if (multiman == null || multiman.id != idMultiman) {
            return
        }
        val cMultiman = multiman.celdaPelea
        val cDueño = dueño.celdaPelea
        cMultiman?.limpiarLuchadores()
        cDueño?.limpiarLuchadores()
        dueño.celdaPelea = cMultiman
        multiman.celdaPelea = cDueño
        if (cDueño != null) {
            GestorSalida.ENVIAR_GIC_CAMBIAR_POS_PELEA(this, 3, mapaCopia!!, multiman.id, cDueño.id)
        }
        if (cMultiman != null) {
            GestorSalida.ENVIAR_GIC_CAMBIAR_POS_PELEA(this, 3, mapaCopia!!, dueño.id, cMultiman.id)
        }
    }

    @Synchronized
    fun cambiarPosicion(id: Int, celda: Short) {
        if (fase != Constantes.PELEA_FASE_POSICION) {
            return
        }
        if (mapaCopia!!.getCelda(celda) == null) {
            return
        }
        val luchador = getLuchadorPorID(id)
        val equipo = getParamMiEquipo(id).toInt()
        if (luchador == null || mapaCopia!!.getCelda(celda)!!.primerLuchador != null || luchador.estaListo()
            || equipo == 1 && grupoCeldasContiene(_celdasPos1, celda.toInt()) || equipo == 2 && grupoCeldasContiene(
                _celdasPos2,
                celda.toInt()
            )
        ) {
            return
        }
        luchador.celdaPelea?.moverLuchadoresACelda(mapaCopia!!.getCelda(celda)!!)
        GestorSalida.ENVIAR_GIC_CAMBIAR_POS_PELEA(this, 3, mapaCopia!!, id, celda)
    }

    private fun grupoCeldasContiene(celdas: ArrayList<Celda>, celda: Int): Boolean {
        for (c in celdas) {
            if (c.id.toInt() == celda) {
                return false
            }
        }
        return true
    }

    fun verificaTodosListos() {
        if (_tipo == Constantes.PELEA_TIPO_RECAUDADOR || _tipo == Constantes.PELEA_TIPO_PRISMA) {
            return
        }
        var listo = true
        for (luch in _equipo1.values) {
            if (luch.personaje == null || luch.personaje!!.esMultiman()) {
                continue
            }
            if (!luch.estaListo()) {
                listo = false
                break
            }
        }
        if (!listo) {
            return
        }
        for (luch in _equipo2.values) {
            if (luch.personaje == null || luch.personaje!!.esMultiman()) {
                continue
            }
            if (!luch.estaListo()) {
                listo = false
                break
            }
        }
        if (!listo) {
            return
        }
        iniciarPelea()
    }

    private fun antesIniciarPelea() {
        try {
            _luchInit2!!.preLuchador?.actualizarAtacantesDefensores()
            if (_tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) {
                // vacio
            } else if (_tipo == Constantes.PELEA_TIPO_PVP) {
                // milicianos
                if (AtlantaMain.PARAM_PERMITIR_MILICIANOS_EN_PELEA && _luchInit1!!
                        .alineacion != Constantes.ALINEACION_NEUTRAL && _luchInit2!!.alineacion == Constantes.ALINEACION_NEUTRAL
                    && _alinPelea == Constantes.ALINEACION_NEUTRAL
                ) {
                    val str = StringBuilder()
                    for (l in _equipo1.values) {
                        if (l.estaRetirado()) {
                            continue
                        }
                        if (str.isNotEmpty()) {
                            str.append(";")
                        }
                        str.append(394.toString() + ",").append(Constantes.getNivelMiliciano(l.nivel)).append(",")
                            .append(
                                Constantes.getNivelMiliciano(
                                    l
                                        .nivel
                                )
                            )
                    }
                    val gm = GrupoMob(mapaReal!!, 1.toShort(), str.toString(), TipoGrupo.SOLO_UNA_PELEA, "")
                    for (mobG in gm.mobs) {
                        val mob = mobG.invocarMob(sigIDLuchadores(), false, null)
                        unirsePelea(mob, _idLuchInit2)
                    }
                }
            } else if (mobGrupo != null) {
                mobGrupo!!.puedeTimerReaparecer(mapaReal!!, mobGrupo, Aparecer.INICIO_PELEA)
            }
            _inicioLuchEquipo1.addAll(luchadoresDeEquipo(1))
            _inicioLuchEquipo2.addAll(luchadoresDeEquipo(2))
            if (_tipo == Constantes.PELEA_TIPO_PVP) {
                for (luch in luchadoresDeEquipo(3)) {
                    val perso = luch.personaje
                    if (perso == null || perso.esMultiman()) {
                        continue
                    }
                    if (Mundo.getRankingPVP(perso.Id) == null) {
                        val rank = RankingPVP(perso.Id, perso.nombre, 0, 0, perso.alineacion.toInt())
                        Mundo.addRankingPVP(rank)
                    }
                }
                if (_inicioLuchEquipo1.size == 1 && _inicioLuchEquipo2.size == 1) {
                    val p1 = _luchInit1!!.personaje
                    val p2 = _luchInit2!!.personaje
                    if (p1 != null && p2 != null) {
                        p1.addAgredirA(p2.nombre)
                        p2.addAgredidoPor(p1.nombre)
                        _1vs1 = true
                    }
                }
            }
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(
                "Exception antesIniciarPelea - Mapa: " + mapaCopia!!.id + " PeleaID: "
                        + id + ", Exception: " + e.toString()
            )
            e.printStackTrace()
        }

    }

    fun Reiniciar_stats_grupo_mob_fun() {
        for (a in mobGrupo!!.mobs) {
            a.reiniciar_stats()
        }
    }

    private fun iniciarPelea() {
        //        for (MobGradoModelo a :
        //                _mobGrupo.getMobs()) {
        //            a.reiniciar_stats();
        //        }
        GestorSalida.ENVIAR_Gc_BORRAR_ESPADA_EN_MAPA(mapaReal!!, id.toInt())
        try {
            Thread.sleep(200)
        } catch (ignored: InterruptedException) {
        }

        antesIniciarPelea()
        acaboPelea(3.toByte())
        if (fase > Constantes.PELEA_FASE_POSICION) {
            return
        }
        _tiempoCombate = System.currentTimeMillis()
        fase = Constantes.PELEA_FASE_COMBATE
        GestorSalida.ENVIAR_fL_LISTA_PELEAS_AL_MAPA(mapaReal!!)
        val gm = StringBuilder("GM")
        for (luchador in luchadoresDeEquipo(3)) {
            if (luchador.id < _ultimaInvoID) {
                _ultimaInvoID = luchador.id
            }
        }
        if (!AtlantaMain.PARAM_VER_JUGADORES_KOLISEO && _tipo == Constantes.PELEA_TIPO_KOLISEO) {
            val gm1 = StringBuilder("GM")
            val gm2 = StringBuilder("GM")
            for (l in _equipo1.values) {
                if (l != null)
                    gm1.append("|+").append(l.stringGM(0))
            }
            for (l in _equipo2.values) {
                if (l != null)
                    gm2.append("|+").append(l.stringGM(0))
            }
            for (l in _equipo1.values) {
                if (l != null)
                    GestorSalida.enviarEnCola(l.personaje, gm2.toString(), true)
            }
            for (l in _equipo2.values) {
                if (l != null)
                    GestorSalida.enviarEnCola(l.personaje, gm1.toString(), true)
            }
        } else if (_tipo != Constantes.PELEA_TIPO_KOLISEO) {
            if (gm.length > 2) {
                for (l in _equipo1.values) {
                    if (l != null) {
                        GestorSalida.enviarEnCola(l.personaje, gm.toString(), true)
                    }
                }
                for (l in _equipo2.values) {
                    if (l != null) {
                        GestorSalida.enviarEnCola(l.personaje, gm.toString(), true)
                    }
                }
            }
        }
        try {
            Thread.sleep(500)
        } catch (ignored: Exception) {
        }
        for (luchador in luchadoresDeEquipo(3)) {
            if (_tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA || _tipo == Constantes.PELEA_TIPO_PVM) break
            if (luchador.personaje == null) {
                continue
            }
            luchador.personaje!!.guardarPAPMoriginales()
            val limitePA = AtlantaMain.LIMITE_PA_PVP
            val limitePM = AtlantaMain.LIMITE_PM_PVP
            if (luchador.totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_PA) > limitePA) {
                luchador.objetosStats?.fijarStatID(
                    Constantes.STAT_MAS_PA,
                    min(
                        luchador.objetosStats!!.getStatParaMostrar(Constantes.STAT_MAS_PA),
                        limitePA - luchador.baseStats!!.getStatParaMostrar(Constantes.STAT_MAS_PA)
                    )
                )
            }

            if (luchador.totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_PM) > limitePM) {
                luchador.objetosStats?.fijarStatID(
                    Constantes.STAT_MAS_PM,
                    min(
                        luchador.objetosStats!!.getStatParaMostrar(Constantes.STAT_MAS_PM),
                        limitePM - luchador.baseStats!!.getStatParaMostrar(Constantes.STAT_MAS_PM)
                    )
                )
            }
        }

        GestorSalida.ENVIAR_GIC_UBICACION_LUCHADORES_INICIAR(this, 7)
        GestorSalida.ENVIAR_GS_EMPEZAR_COMBATE_EQUIPOS(this, 7)
        iniciarOrdenLuchadores()
        try {
            Thread.sleep(500)
        } catch (ignored: Exception) {
        }

        GestorSalida.ENVIAR_GTL_ORDEN_JUGADORES(this, 7)
        GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_TODOS(this, 7, false)
        for (luchador in luchadoresDeEquipo(3)) {
            if (luchador == null) {
                continue
            }
            _posInicialLuch[luchador.id] = luchador.celdaPelea!!
            val perso = luchador.personaje ?: continue
            if (perso.estaMontando()) {
                GestorSalida.ENVIAR_GA950_ACCION_PELEA_ESTADOS(
                    this,
                    3,
                    perso.Id,
                    Constantes.ESTADO_CABALGANDO.toInt(),
                    true
                )
            }
        }
        try {
            Thread.sleep(500)
        } catch (ignored: Exception) {
        }

        if (_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) {
            retos = ConcurrentHashMap()
            val retosPosibles = ArrayList<Byte>()
            loop@ for (retoID in 1..50) {
                when (retoID) {
                    13// no tienen nada
                        , 26, 27 -> continue@loop
                    else -> if (Constantes.esRetoPosible1(retoID, this)) {
                        retosPosibles.add(retoID.toByte())
                    }
                }
            }
            var retoID = retosPosibles[Formulas.getRandomInt(0, retosPosibles.size - 1)]
            retos!![retoID] = Constantes.getReto(retoID, this)
            if (mapaReal!!.esArena() || mapaReal!!.esMazmorra()) {
                retoID = retosPosibles[Formulas.getRandomInt(0, retosPosibles.size - 1)]
                var repetir = true
                while (repetir) {
                    repetir = false
                    retoID = retosPosibles[Formulas.getRandomInt(0, retosPosibles.size - 1)]
                    for ((key) in retos!!) {
                        if (Constantes.esRetoPosible2(key.toInt(), retoID.toInt()) && !repetir) {
                        } else {
                            repetir = true
                        }
                    }
                }
                retos!![retoID] = Constantes.getReto(retoID, this)
            }
            ordenarRetos()
        }
        iniciarTurno()
    }

    private fun ordenarRetos() {
        if (retos == null) {
            return
        }
        for ((retoID, reto) in retos!!) {
            val exitoReto = reto.estado
            if (exitoReto != EstReto.EN_ESPERA) {
                continue
            }
            var nivel = 10000
            when (retoID) {
                Constantes.RETO_LOS_PEQUEÑOS_ANTES// los pequeños antes
                -> for (luch in _equipo1.values) {
                    if (luch.nivel < nivel) {
                        _luchMenorNivelReto = luch.id
                        nivel = luch.nivel
                    }
                }
                Constantes.RETO_ELEGIDO_VOLUNTARIO, Constantes.RETO_APLAZAMIENTO, Constantes.RETO_ELITISTA, Constantes.RETO_ASESINO_A_SUELDO -> {
                    var mob: Luchador? = null
                    try {
                        val equipo2 = ArrayList<Luchador>()
                        for (luch in _equipo2.values) {
                            if (luch.estaMuerto() || luch.esInvocacion()) {
                                continue
                            }
                            equipo2.add(luch)
                        }
                        if (equipo2.isNotEmpty()) {
                            val azar = Formulas.getRandomInt(0, equipo2.size - 1)
                            mob = equipo2[azar]
                        }
                    } catch (ignored: Exception) {
                    }

                    if (mob != null) {
                        mob.celdaPelea?.id?.let { GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(this, 5, mob.id, it) }
                        reto.setMob(mob)
                    }
                }
            }
            GestorSalida.ENVIAR_Gd_RETO_A_LOS_LUCHADORES(this, reto.infoReto())
        }
    }

    //
    // private void actualizarNumTurnos(final Personaje perso) {
    // if (!Bustemu.PARAM_MOSTRAR_NRO_TURNOS) {
    // return;
    // }
    // try {
    // int i = 1;
    // final StringBuilder str = new StringBuilder();
    // for (final Luchador luch : _ordenJugadores) {
    // if (luch.estaMuerto()) {
    // continue;
    // }
    // if (i > 1) {
    // str.append(";");
    // }
    // str.append(luch.getID() + ",1" + i);
    // i++;
    // }
    // if (perso == null) {
    // GestorSalida.ENVIAR_GX_EXTRA_CLIP_PELEA(this, 7, "+|" + str.toString() + "|0|1");
    // } else {
    // GestorSalida.ENVIAR_GX_EXTRA_CLIP(perso, "+|" + str.toString() + "|0|1");
    // }
    // } catch (Exception e) {
    // try {
    // Thread.sleep(500);
    // } catch (final Exception e1) {}
    // actualizarNumTurnos(perso);
    // }
    // }
    //
    private fun hechizoDisponible(luchador: Luchador, idHechizo: Int): Boolean {
        var ver = false
        if (luchador.personaje!!.tieneHechizoID(idHechizo)) {
            ver = true
            for (hl in luchador.hechizosLanzados) {
                if (hl.hechizoID == idHechizo && hl.sigLanzamiento > 0) {
                    ver = false
                }
            }
        }
        return !ver
    }

    private fun iniciarOrdenLuchadores() {
        var cantLuch = 0
        val iniLuch = ArrayList<Duo<Int, Luchador>>()
        for (luch in luchadoresDeEquipo(3)) {
            luch.resetPuntos()
            iniLuch.add(Duo(Formulas.getIniciativa(luch.totalStats, luch.porcPDV), luch))
            cantLuch++
        }
        var equipo1 = 0
        var equipo2 = 0
        var primero = 0
        var luchMaxIni: Luchador? = null
        var ultLuch: Luchador? = null
        while (_ordenLuchadores.size < cantLuch) {
            var tempIni = 0
            for (entry in iniLuch) {
                if (_ordenLuchadores.contains(entry._segundo)) {
                    continue
                }
                if (primero == 0 || equipo1 == _equipo1.size || equipo2 == _equipo2.size || Objects.requireNonNull(
                        ultLuch
                    )
                    !!.equipoBin != entry._segundo.equipoBin
                ) {
                    if (tempIni <= entry._primero) {
                        luchMaxIni = entry._segundo
                        tempIni = entry._primero
                    }
                }
            }
            ultLuch = luchMaxIni
            if (luchMaxIni != null) {
                _ordenLuchadores.add(luchMaxIni)
            }
            if (_equipo1.containsValue(luchMaxIni)) {
                equipo1++
            } else {
                equipo2++
            }
            primero++
        }
    }

    fun botonBloquearMasJug(id: Int) {
        if (_luchInit1 != null && _idLuchInit1 == id) {
            _cerrado1 = !_cerrado1
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(mapaReal!!, if (_cerrado1) '+' else '-', 'A', id)
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 1, if (_cerrado1) "095" else "096")
        } else if (_luchInit2 != null && _idLuchInit2 == id) {
            _cerrado2 = !_cerrado2
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(mapaReal!!, if (_cerrado2) '+' else '-', 'A', id)
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 2, if (_cerrado2) "095" else "096")
        }
    }

    fun botonSoloGrupo(idInit: Int) {
        var expulsadoID: Int
        var luch: Luchador
        if (_luchInit1 != null && _idLuchInit1 == idInit) {
            _soloGrupo1 = !_soloGrupo1
            if (_soloGrupo1) {
                val lista = ArrayList<Int>()
                val expulsar = ArrayList<Int>()
                try {
                    lista.addAll(_luchInit1!!.personaje!!.grupoParty.iDsPersos)
                } catch (ignored: Exception) {
                }

                for ((key, value) in _equipo1) {
                    try {
                        luch = value
                        expulsadoID = key
                        if (!lista.contains(expulsadoID)) {
                            expulsar.add(expulsadoID)
                            GestorSalida.ENVIAR_GM_BORRAR_LUCHADOR(this, expulsadoID, 3)
                            luchadorSalirPelea(luch)
                            GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(luch.personaje!!)
                            luch.celdaPelea?.removerLuchador(luch)
                            GestorSalida.ENVIAR_Gt_BORRAR_NOMBRE_ESPADA(mapaReal!!, _idLuchInit1, luch)
                        }
                    } catch (ignored: Exception) {
                    }

                }
                for (ID in expulsar) {
                    _equipo1.remove(ID)
                }
            }
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(mapaReal!!, if (_soloGrupo1) '+' else '-', 'P', idInit)
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 1, if (_soloGrupo1) "093" else "094")
        } else if (_luchInit2 != null && _idLuchInit2 == idInit) {
            _soloGrupo2 = !_soloGrupo2
            if (_soloGrupo2) {
                val lista = ArrayList<Int>()
                val expulsar = ArrayList<Int>()
                try {
                    lista.addAll(_luchInit2!!.personaje!!.grupoParty.iDsPersos)
                } catch (ignored: Exception) {
                }

                for ((key, value) in _equipo2) {
                    try {
                        luch = value
                        expulsadoID = key
                        if (!lista.contains(expulsadoID)) {
                            expulsar.add(expulsadoID)
                            GestorSalida.ENVIAR_GM_BORRAR_LUCHADOR(this, expulsadoID, 3)
                            luchadorSalirPelea(luch)
                            GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(luch.personaje!!)
                            luch.celdaPelea?.removerLuchador(luch)
                            GestorSalida.ENVIAR_Gt_BORRAR_NOMBRE_ESPADA(mapaReal!!, _idLuchInit2, luch)
                        }
                    } catch (ignored: Exception) {
                    }

                }
                for (ID in expulsar) {
                    _equipo2.remove(ID)
                }
            }
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(mapaReal!!, if (_soloGrupo2) '+' else '-', 'P', idInit)
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 2, if (_soloGrupo2) "095" else "096")
        }
    }

    fun botonBloquearEspect(id: Int) {
        if (_luchInit1 != null && _idLuchInit1 == id || _luchInit2 != null && _idLuchInit2 == id) {
            _sinEspectador = !_sinEspectador
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(mapaReal!!, if (_sinEspectador) '+' else '-', 'S', id)
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 7, if (_sinEspectador) "039" else "040")
        }
        if (_sinEspectador) {
            val espectadores = TreeMap(_espectadores)
            for (espectador in espectadores.values) {
                try {
                    if (espectador.esEspectadorAdmin()) {
                        continue
                    }
                    _espectadores.remove(espectador.id)
                    luchadorSalirPelea(espectador)
                    GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(espectador.personaje!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    fun botonAyuda(id: Int) {
        if (_luchInit1 != null && _idLuchInit1 == id) {
            _ayuda1 = !_ayuda1
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(mapaReal!!, if (_ayuda1) '+' else '-', 'H', id)
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 1, if (_ayuda1) "0103" else "0104")
        } else if (_luchInit2 != null && _idLuchInit2 == id) {
            _ayuda2 = !_ayuda2
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(mapaReal!!, if (_ayuda2) '+' else '-', 'H', id)
            GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(this, 2, if (_ayuda2) "0103" else "0104")
        }
    }

    fun finAccion(perso: Personaje?) {
        if (perso == null) {
            return
        }
        val luchador = getLuchadorPorID(perso.Id)
        finAccion(luchador)
    }

    private fun finAccion(luchador: Luchador?) {
        for (luchadortemp in this.luchadoresDeEquipo(7)) {
            if (luchadortemp.estaMuerto()) {
                pasarTurno(luchadortemp)
            }
        }
        if (luchadorTurno!!.paRestantes < 2 && luchadorTurno!!.mob != null) {
            pasarTurno(luchadorTurno)
        }
        GestorSalida.ENVIAR_GM_LUCHADORES_A_PERSO(this, mapaCopia!!, luchador!!.personaje!!)
        this.refrescarCeldas(luchador)
//        GestorSalida.ENVIAR_GDM_MAPDATA_COMPLETO(luchador!!.personaje!!);
        checkeaPasarTurno()
        if (luchador.personaje == null || !luchador.puedeJugar()) {
            return
        }
        if (luchadorTurno!!.estaMuerto()) {
            pasarTurno(luchadorTurno)
        }
        val perso = luchador.personaje
        try {
            Thread.sleep(500)
        } catch (ignored: Exception) {
        }

        GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(perso!!, luchador.id, _idUnicaAccion)
        if (_idUnicaAccion != -1) {
            _idUnicaAccion = -1
            return
        }
        GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
        _tempAccion = ""
        //		 GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso);
//        if (!AtlantaMain.PARAM_JUGAR_RAPIDO)
//            GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(perso, "El tempAccion fue limpiado, ahora puedes seguir jugando.")
    }

    fun esLuchInicioPelea(luch: Luchador): Boolean {
        return _inicioLuchEquipo1.contains(luch) || _inicioLuchEquipo2.contains(luch)
    }

    // este metodo es recibido por el comando jugador .turno
    @Synchronized
    fun checkeaPasarTurno() {
        if (fase == Constantes.PELEA_FASE_COMBATE) {
            if (_tiempoTurno <= 0) {
                return
            }
            if (System.currentTimeMillis() - _tiempoTurno >= AtlantaMain.SEGUNDOS_TURNO_PELEA * 1000 - 5000) {
                preFinTurno(null)
            }
        }
    }

    @Synchronized
    fun pasarTurnoBoton(perso: Personaje) {
        var luchador: Luchador? = getLuchadorPorID(perso.Id) ?: return
        if (!luchador!!.puedeJugar()) {
            if (perso.compañero == null) {
                return
            }
            luchador = getLuchadorPorID(perso.compañero.Id)
            if (luchador == null || !luchador.puedeJugar()) {
                return
            }
        }
        if (_tempAccion.isNotEmpty()) {
            if (!AtlantaMain.PARAM_JUGAR_RAPIDO) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1REALIZANDO_TEMP_ACCION;$_tempAccion")
            } else {
                finAccion(luchador)
            }
            return
        }
        pasarTurno(luchador)
    }

    private fun eliminarTimer() {
        if (_timerPelea != null) {
            _timerPelea!!.stop()
        }
        _timerPelea = null
    }

    // la conmbinacion de GTF , GTM y GTS realiza la disminucion de turnos de los buffs
    @Synchronized
    private fun iniciarTurno() { // inicioturno
        // _cantTurnos++;
        acaboPelea(3.toByte())
        if (fase == Constantes.PELEA_FASE_FINALIZADO) {
            return
        }
        startTimerInicioTurno()
        _ultimoTipoDañoReto = TipoDaño.NULL
        _tiempoHechizo = 0
        _idUnicaAccion = -1
        _nroOrdenLuc++
        _tempAccion = ""
        _cantUltAfec = 0
        _cantMuertosReto2x1 = 0
        _cantCAC = 0
        luchadorTurno = luchadorOrden
        if (luchadorTurno == null) {
            return
        }
        //		 GestorSalida.ENVIAR_GTR_TURNO_LISTO(this, 7, this.getID());//hace q retorne luego un
        // GT(sprite)
        // SI ESTA MUERTO SE VA DE FRENTE AL SIGUIENTE TURNO
        luchadorTurno!!.distMinAtq = -1
        luchadorTurno!!.setPuedeJugar(true)
        if (luchadorTurno!!.estaMuerto()) {
            pasarTurno(luchadorTurno)
            return
        }
        // ACTIVA LOS GLIFOS
        if (_glifos != null) {
            for (glifo in _glifos!!) {
                if (fase == Constantes.PELEA_FASE_FINALIZADO) {
                    return
                }
                if (glifo.lanzador.id == luchadorTurno!!.id && glifo.disminuirDuracion() == 0) {
                    glifo.desaparecer()
                }
            }
        }
        if (luchadorTurno!!.personaje != null) {
            try {
                val perso = luchadorTurno!!.personaje!!
                var lider: Personaje? = null
                for (l in luchadoresDeEquipo(7)) {
                    if (l.personaje == null) {
                        continue
                    } else {
                        var lperso = l.personaje!!
                        if (perso.cuenta.actualIP == lperso.cuenta.actualIP || perso.cuenta.ultimaIP == lperso.cuenta.actualIP) {
                            if (lperso.EsliderIP && lperso.enLinea()) {
                                try {
                                    if (lperso.cuenta.socket!!.personaje != lperso) {
                                        lperso.cuenta.socket!!.personaje = lperso
                                    }
                                } catch (e: Exception) {
                                }
                                lider = lperso
                            }
                        }
                    }
                }
                if (lider != null) {
                    lider.Multi = perso
                    GestorSalida.ENVIAR_AI_CAMBIAR_ID(perso, luchadorTurno!!.id)
                    GestorSalida.ENVIAR_SL_LISTA_HECHIZOS_A_LIDER(perso, perso)
                    GestorSalida.ENVIAR_As_STATS_DEL_PJ_LIDER(perso, lider)
                    //GestorSalida.ENVIAR_As_STATS_DEL_PJ(perso)
                    GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
//                    getLuchadorPorID(lider.id)
                }
            } catch (e: Exception) {
            }
            try {
//                System.out.println("Tiempo final: "+_tiempoesperaturno)
                Thread.sleep(_tiempoesperaturno)
                if (luchadorTurno!!.personaje!!._Refrescarmobspelea) {
                    try {
//                        if (existioempuje) {
//                            Thread.sleep(1500)
//                        } else {
//                            Thread.sleep(1000)
//                        }
                    } catch (e: Exception) {
                        AtlantaMain.redactarLogServidorln(e.toString())
                    }
                    GestorSalida.ENVIAR_GM_LUCHADORES_A_PERSO(this, mapaCopia!!, luchadorTurno!!.personaje!!)
//                    this.refrescarCeldas(luchadorTurno!!)
                    existioempuje = false
                }
                _tiempoesperaturno = 0L
                animacionhechizo = false
            } catch (e: Exception) {

            }
            if (luchadorTurno!!.personaje!!.servidorSocket != null) {
                luchadorTurno!!.personaje!!.servidorSocket!!.limpiarAcciones(true)
            }
            val compañero = luchadorTurno!!.personaje!!.compañero
            if (compañero != null) {
                if (!luchadorTurno!!.esIDReal()) {
                    luchadorTurno!!.setIDReal(true)
                    getLuchadorPorID(compañero.Id)!!.setIDReal(false)
                    GestorSalida.ENVIAR_AI_CAMBIAR_ID(luchadorTurno!!.personaje!!, luchadorTurno!!.id)
                    GestorSalida.ENVIAR_SL_LISTA_HECHIZOS(luchadorTurno!!.personaje!!)
                }
            }
        }
//        try {
//            Thread.sleep(250)
//        } catch (ignored: Exception) {
//        }

        GestorSalida.ENVIAR_GTS_INICIO_TURNO_PELEA(this, 7, luchadorTurno!!.id, AtlantaMain.SEGUNDOS_TURNO_PELEA * 1000)
//        try {
//            Thread.sleep(250)
//        } catch (ignored: Exception) {
//        }

        luchadorTurno!!.aplicarBuffInicioTurno(this)
        luchadorTurno!!.bonusCastigo.clear()
        luchadorTurno!!.actualizaHechizoLanzado()
        if (luchadorTurno!!.estaMuerto()) {
            addMuertosReturnFinalizo(luchadorTurno!!,null)
            return
        }
        if (luchadorTurno!!.celdaPelea!!.glifos != null) {
            for (glifo in luchadorTurno!!.celdaPelea!!.glifos!!) {
                if (fase == Constantes.PELEA_FASE_FINALIZADO) {
                    return
                }
                if (!glifo.esInicioTurno()) {
                    continue
                }
                glifo.activarGlifo(luchadorTurno!!)
//                try {
//                    Thread.sleep(500)
//                } catch (ignored: Exception) {
//                }

            }
        }
        if (luchadorTurno!!.pdvConBuff <= 0 || luchadorTurno!!.estaMuerto()) {
            addMuertosReturnFinalizo(luchadorTurno!!, null)
            return
        }
        if (luchadorTurno!!.tieneBuff(140) || luchadorTurno!!.comandoPasarTurno) {
            try {
                Thread.sleep(500)
            } catch (e: Exception) {
            }
            // efecto de pasar turno
            pasarTurno(luchadorTurno)
            return
        }
        if (luchadorTurno!!.estaDesconectado()) {
            var liderIP: Personaje? = null
            if (luchadorTurno!!.personaje?.LiderIP != null) {
                liderIP = luchadorTurno!!.personaje!!.LiderIP
            }
            if (liderIP != null) {
                if (!liderIP.enLinea() && liderIP.pelea != this) {
                    luchadorTurno!!.setTurnosRestantes(luchadorTurno!!.turnosRestantes - 1)
                    if (luchadorTurno!!.turnosRestantes <= 0) {
                        luchadorTurno!!.setDesconectado(false)
                        retirarsePelea(luchadorTurno!!.id, -1, true)
                        // luchador.getPersonaje().desconectar();
                    } else {
                        GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(
                            this, 7, "0162;" + luchadorTurno!!.nombre + "~"
                                    + luchadorTurno!!.turnosRestantes
                        )
                        pasarTurno(luchadorTurno)
                    }
                }
            } else {
                luchadorTurno!!.setTurnosRestantes(luchadorTurno!!.turnosRestantes - 1)
                if (luchadorTurno!!.turnosRestantes <= 0) {
                    luchadorTurno!!.setDesconectado(false)
                    retirarsePelea(luchadorTurno!!.id, -1, true)
                    // luchador.getPersonaje().desconectar();
                } else {
                    GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA(
                        this, 7, "0162;" + luchadorTurno!!.nombre + "~"
                                + luchadorTurno!!.turnosRestantes
                    )
                    pasarTurno(luchadorTurno)
                }
            }
            return
        }
        if (luchadorTurno!!.estaMuerto()) {
            addMuertosReturnFinalizo(luchadorTurno!!,null)
            return
        }
        if (AtlantaMain.MODO_DEBUG) {
            println("_tempLuchadorPA es " + luchadorTurno!!.paRestantes)
            println("_tempLuchadorPM es " + luchadorTurno!!.pmRestantes)
        }
        if (luchadorTurno!!.personaje != null) {
            GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(luchadorTurno!!.personaje!!)
            GestorSalida.ENVIAR_As_STATS_DEL_PJ(luchadorTurno!!.personaje!!)
        }
        _tiempoTurno = System.currentTimeMillis()
        if ((_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) && retos != null) {
            loop@ for ((retoID, reto) in retos!!) {
                var exitoReto = reto.estado
                if (exitoReto != EstReto.EN_ESPERA) {
                    continue
                }
                if (luchadorTurno!!.esNoIA()) {
                    when (retoID) {
                        Constantes.RETO_ESTATUA -> luchadorTurno!!.idCeldaInicioTurno =
                            luchadorTurno!!.celdaPelea?.id!!.toInt()
                        Constantes.RETO_VERSATIL -> luchadorTurno!!.hechizosLanzadosReto.clear()
                        Constantes.RETO_IMPREVISIBLE -> {
                            val mobsVivos = ArrayList<Luchador>()
                            for (luch in _inicioLuchEquipo2) {
                                if (luch.estaMuerto()) {
                                    continue
                                }
                                mobsVivos.add(luch)
                            }
                            if (mobsVivos.isNotEmpty()) {
                                val mob = mobsVivos[Formulas.getRandomInt(0, mobsVivos.size - 1)]
                                reto.setMob(mob)
                                mob.celdaPelea?.id?.let {
                                    GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(
                                        this,
                                        5,
                                        mob.id,
                                        it
                                    )
                                }
                            }
                        }
                        Constantes.RETO_CONTAMINACION -> {
                            if (!luchadorTurno!!.estaContaminado()) {
                                break@loop
                            }
                            luchadorTurno!!.addTurnosParaMorir()
                            if (luchadorTurno!!.turnosParaMorir <= 3) {
                                break@loop
                            }
                            exitoReto = EstReto.FALLADO
                        }
                    }
                } else {
                    if (retoID == Constantes.RETO_BLITZKRIEG) {
                        if (luchadorTurno!!.equipoBin.toInt() == 1) {
                            if (reto.luchMob != null) {
                                if (luchadorTurno!!.id == reto.luchMob!!.id) {
                                    exitoReto = EstReto.FALLADO
                                }
                            }
                        }
                    }
                }
                reto.estado = exitoReto
            }
        }
//
        if (luchadorTurno!!.estaMuerto()) {
            addMuertosReturnFinalizo(luchadorTurno!!,null)
            return
        }
        acaboPelea(3.toByte())
        try {
            if (luchadorTurno?.ia != null) {
                _tiempoesperatemp = 0L
                luchadorTurno?.ia?.parar() // para finalizar thread
                luchadorTurno?.setInteligenciaArtificial(Inteligencia(luchadorTurno!!, this))
                luchadorTurno?.ia?.arrancar()
            }
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(
                "Exception en inicioTurno Mapa: " + mapaCopia!!.id + ", luchador: "
                        + (if (luchadorTurno == null) "null" else luchadorTurno!!.id) + e.toString()
            )
            e.printStackTrace()
        }

    }

    fun mostrarObjetivoReto(retoID: Byte, perso: Personaje) {
        if (retos == null) {
            return
        }
        for (reto in retos!!.values) {
            if (reto.id != retoID || reto.luchMob == null) {
                continue
            }
            reto.luchMob!!.celdaPelea?.id?.let { GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA(perso, reto.luchMob!!.id, it) }
        }
    }

    private fun mostrarBuffsDeTodosAPerso(perso: Personaje) {
        for (luch in luchadoresDeEquipo(3)) {
            for (buff in luch.buffsPelea) {
                if (buff.condicionBuff.isNotEmpty()) {
                    continue
                }
                GestorSalida.ENVIAR_GA998_AGREGAR_BUFF(
                    perso, getStrParaGA998(
                        buff.efectoID, luch.id, buff
                            .getTurnosRestantes(false), buff.hechizoID, buff.args
                    )
                )
            }
        }
    }

    private fun refrescarBuffsPorMuerte(luchador: Luchador) {
        for (luch in luchadoresDeEquipo(3)) {
            if (luch.id == luchador.id) {
                continue
            }
            luch.deshechizar(luchador, false)
        }
    }

    private fun cantLuchIniMuertos(equipo: Int): Int {
        var i = 0
        for (muerto in _listaMuertos) {
            if (equipo == 1) {
                if (_inicioLuchEquipo1.contains(muerto)) {
                    i++
                }
            } else if (equipo == 2) {
                if (_inicioLuchEquipo2.contains(muerto)) {
                    i++
                }
            }
        }
        return i
    }

    fun addMuertosReturnFinalizo(victima: Luchador, asesino: Luchador?): Boolean {
        // agregar a muertos, agregarmuerto
        try {
            if (victima.estaMuerto() || fase == Constantes.PELEA_FASE_FINALIZADO) {
                return false
            }
            victima.setEstaMuerto(true)
            victima.muertoPor = asesino
            victima.setPDV(1)
            val victimaID = victima.id
            if (!victima.estaRetirado()) {
                if (!_listaMuertos.contains(victima)) {
                    _listaMuertos.add(victima)
                }
            }
            if (luchadorTurno == null) {
                return false
            }
            for (l in luchadoresDeEquipo(3)) {
                if (l.ia != null) {
                    l.ia!!.forzarRefrescarMov()
                    l.ia!!.nullear()
                    l.ia!!.interrupt()
                }
            }
            if ((_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) && retos != null
                && (asesino == null || asesino.esNoIA()) && victima.equipoBin.toInt() == 1
            ) {
                // si la victima no es del equipo, y asesino es un jugador
                loop@ for ((retoID, reto) in retos!!) {
                    var exitoReto = reto.estado
                    if (exitoReto != EstReto.EN_ESPERA) {
                        continue
                    }
                    val nivelVict = victima.nivel
                    when (retoID) {
                        Constantes.RETO_BLITZKRIEG -> if (reto.luchMob === victima) {
                            reto.setMob(null)
                        }
                        Constantes.RETO_MANOS_LIMPIAS -> {
                            if (victima.esInvocacion()) {
                                continue@loop
                            }
                            if (_ultimoTipoDañoReto == TipoDaño.NORMAL) {
                                exitoReto = EstReto.FALLADO
                            }
                        }
                        Constantes.RETO_FOCALIZACION -> if (reto.luchMob != null && reto.luchMob!!.id == victimaID) {
                            reto.setMob(null)
                        }
                        Constantes.RETO_ELEGIDO_VOLUNTARIO// elegido voluntario
                        -> {
                            if (victima.esInvocacion()) {
                                break@loop
                            }
                            if (cantLuchIniMuertos(2) > 0) {
                                exitoReto = if (reto.luchMob != null && reto.luchMob!!.id == victima.id) {
                                    EstReto.REALIZADO
                                } else {
                                    EstReto.FALLADO
                                }
                            }
                        }
                        Constantes.RETO_APLAZAMIENTO// aplazamiento
                        -> {
                            if (victima.esInvocacion()) {
                                break@loop
                            }
                            if (reto.luchMob != null && reto.luchMob!!.id == victima.id) {
                                exitoReto = if (cantLuchIniMuertos(2) == _inicioLuchEquipo2.size) {
                                    EstReto.REALIZADO
                                } else {
                                    EstReto.FALLADO
                                }
                            }
                        }
                        Constantes.RETO_CRUEL// cruel
                        -> {
                            if (victima.esInvocacion()) {
                                continue@loop
                            }
                            for (e in _equipo2.values) {
                                if (e.esInvocacion() || e.estaMuerto()) {
                                    continue
                                }
                                if (e.nivel < nivelVict) {
                                    exitoReto = EstReto.FALLADO
                                    break
                                }
                            }
                        }
                        Constantes.RETO_ORDENADO// ordenado
                        -> {
                            if (victima.esInvocacion()) {
                                continue@loop
                            }
                            for (e in _equipo2.values) {
                                if (e.esInvocacion() || e.estaMuerto()) {
                                    continue
                                }
                                if (e.nivel > nivelVict) {
                                    exitoReto = EstReto.FALLADO
                                    break
                                }
                            }
                        }
                        Constantes.RETO_NI_PIAS_NI_SUMISAS// ni pias ni sumisas
                        -> if (luchadorTurno!!.personaje!!.sexo == Constantes.SEXO_MASCULINO) {
                            exitoReto = EstReto.FALLADO
                        }
                        Constantes.RETO_NI_PIOS_NI_SUMISOS // ni pios ni sumisos
                        -> if (luchadorTurno!!.personaje!!.sexo == Constantes.SEXO_FEMENINO) {
                            exitoReto = EstReto.FALLADO
                        }
                        Constantes.RETO_LOS_PEQUEÑOS_ANTES// los pequeños antes
                        -> if (luchadorTurno!!.id != _luchMenorNivelReto) {
                            exitoReto = EstReto.FALLADO
                        }
                        Constantes.RETO_ELITISTA// elitista
                        -> {
                            if (victima.esInvocacion()) {
                                break@loop
                            }
                            exitoReto = if (reto.luchMob != null && reto.luchMob!!.id == victimaID) {
                                EstReto.REALIZADO
                            } else {
                                EstReto.FALLADO
                            }
                        }
                        Constantes.RETO_ASESINO_A_SUELDO// asesino a sueldo
                        -> {
                            if (victima.esInvocacion()) {
                                break@loop
                            }
                            if (reto.luchMob != null && reto.luchMob!!.id == victimaID) {
                                var mob: Luchador? = null
                                val equipo2 = ArrayList<Luchador>()
                                for (luch in _equipo2.values) {
                                    if (luch.estaMuerto() || luch.esInvocacion()) {
                                        continue
                                    }
                                    equipo2.add(luch)
                                }
                                if (equipo2.isNotEmpty()) {
                                    val azar = Formulas.getRandomInt(0, equipo2.size - 1)
                                    mob = equipo2[azar]
                                }
                                if (mob != null) {
                                    mob.celdaPelea?.id?.let {
                                        GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(
                                            this,
                                            5,
                                            mob.id,
                                            it
                                        )
                                    }
                                    reto.setMob(mob)
                                }
                            } else {
                                exitoReto = EstReto.FALLADO
                            }
                        }
                        Constantes.RETO_EL_DOS_POR_UNO // el dos por uno
                        -> _cantMuertosReto2x1++
                        Constantes.RETO_REPARTO// reparto
                            , Constantes.RETO_CADA_UNO_CON_SU_MONSTRUO// cada uno con su monstruo
                        -> {
                            if (victima.esInvocacion()) {
                                continue@loop
                            }
                            luchadorTurno!!.mobsAsesinadosReto.add(victimaID)
                        }
                    }
                    reto.estado = exitoReto
                }
            }
            GestorSalida.ENVIAR_GA103_JUGADOR_MUERTO(this, 7, victimaID)
            _tiempoHechizo += EfectoHechizo.TIEMPO_POR_LUCHADOR_MUERTO
            if (victima.transportando != null) {
                quitarTransportados(victima)
            } else if (victima.portador != null) {
                quitarTransportados(victima.portador)
            }
            victima.celdaPelea?.removerLuchador(victima)
            val team = TreeMap<Int, Luchador>()
            if (victima.equipoBin.toInt() == 0) {
                team.putAll(_equipo1)
            } else if (victima.equipoBin.toInt() == 1) {
                team.putAll(_equipo2)
            }
            for (luch in team.values) {
                if (luch.estaMuerto() || luch.estaRetirado() || luch.invocador == null || luch.invocador!!
                        .id != victimaID
                ) {
                    continue
                }
                addMuertosReturnFinalizo(luch, asesino)
            }
            if (victima.esInvocacion() && !victima.esEstatico()) {
                victima.invocador?.addNroInvocaciones(-1)
                if (_ordenLuchadores.isNotEmpty()) {
                    val index = _ordenLuchadores.indexOf(victima)
                    if (index > -1) {
                        if (_nroOrdenLuc >= index && _nroOrdenLuc > 0) {
                            _nroOrdenLuc--
                        }
                        _ordenLuchadores.remove(victima)
                    }
                    if (_nroOrdenLuc < 0) {
                        return false
                    }
                    if (_equipo1.containsKey(victimaID)) {
                        _equipo1.remove(victimaID)// expulsa invocacion
                    } else
                        _equipo2.remove(victimaID)
                    GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 999, victimaID.toString() + "", stringOrdenJugadores())
                    try {
                        Thread.sleep(500)
                    } catch (ignored: Exception) {
                    }
                }
            }
            if (_glifos != null) {
                for (glifo in _glifos!!) {
                    if (glifo.lanzador.id == victimaID) {
                        glifo.desaparecer()
                    }
                }
            }
            if (trampas != null) {
                for (trampa in trampas!!) {
                    if (trampa.lanzador.id == victimaID) {
                        trampa.activarTrampa(null)
                    }
                }
            }
            GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 999, victimaID.toString() + "", stringOrdenJugadores())
            refrescarBuffsPorMuerte(victima)
            if (victima.recaudador != null || victima.prisma != null) {
                acaboPelea(2.toByte())
                return true
            } else if (cantLuchIniMuertos(1) == _inicioLuchEquipo1.size || cantLuchIniMuertos(2) == _inicioLuchEquipo2
                    .size
            ) {
                acaboPelea(if (cantLuchIniMuertos(1) == _inicioLuchEquipo1.size) 1.toByte() else 2.toByte())
                return true
            }
            comprobarPasarTurnoDespuesMuerte(victima)
            if (victima.estaMuerto()) {
                pasarTurno(victima)
            }
            try {
                Thread.sleep(500)
            } catch (ignored: Exception) {
            }
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln("Exception en addMuertosReturnFinalizo $e")
            e.printStackTrace()
        }

        return false
    }

    private fun comprobarPasarTurnoDespuesMuerte(victima: Luchador) {
        if (victima.estaMuerto()) {
            preFinTurno(victima)
        }
    }

    fun quitarTransportados(portador: Luchador?) {
        if (portador == null) {
            return
        }
        // if (nuevaCelda != null && reubicar != null){
        // reubicar.getCeldaPelea().removerLuchador(reubicar);
        // reubicar.setCeldaPelea(nuevaCelda);
        // }
        val transportado = portador.transportando
        portador.setEstado(Constantes.ESTADO_PORTADOR.toInt(), 0)
        portador.transportando = null
        if (transportado != null && !transportado.estaMuerto()) {
            transportado.setEstado(Constantes.ESTADO_TRANSPORTADO.toInt(), 0)
            transportado.setTransportadoPor(null)
        }
        try {
            Thread.sleep(500)
        } catch (ignored: Exception) {
        }

    }

    fun setTempAccion(str: String) {
        _tempAccion = str
        if (_tempAccion.equals("pasar", ignoreCase = true)) {
            pasarTurno(null)
        }
    }

    // private void tiempoParaPasarTurno() {
    // if (_fase == Constantes.PELEA_FASE_COMBATE && System.currentTimeMillis() - _ultQping > 500 &&
    // _cantTurnos > 1) {
    // _ultQping = System.currentTimeMillis();
    // checkeaPasarTurno(getLuchadorTurno());
    // }
    // }
    private fun preFinTurno(victima: Luchador?) {
        if (_vecesQuePasa >= 10) {
            _tempAccion = ""
        }
        // System.out.println("victima preFinTurno " + (victima == null ? victima : victima.getID()));
        if (_tempAccion.isNotEmpty() || System.currentTimeMillis() < _tiempoTurno + _tiempoHechizo) {
            if (_rebootTurno == null) {
                _rebootTurno = Timer(20) { e ->
                    _vecesQuePasa++
                    preFinTurno(victima)
                }
            }
            _rebootTurno?.isRepeats = false
            _rebootTurno?.restart()
        } else {
            pasarTurno(victima)
        }
    }

    @Synchronized
    fun pasarTurno(pasoTurno: Luchador?): String {
        if (fase == Constantes.PELEA_FASE_FINALIZADO) {
            return "Pelea finalizada"
        }
        if (pasoTurno != null) {
            if (luchadorTurno!!.id != pasoTurno.id) {
                return "No es mismo luchador de turno"
            }
        } else {
            if (System.currentTimeMillis() - _tiempoTurno < AtlantaMain.SEGUNDOS_TURNO_PELEA * 1000 - 5000) {
                return "Evitar doble pasar turno"
            }
        }
        if (_rebootTurno != null) {
            _rebootTurno!!.stop()
        }
        if (luchadorTurno == null) {
            return "Null Pointer"
        }
        if (!luchadorTurno!!.puedeJugar()) {
            return "El luchador  " + luchadorTurno!!.nombre + " no puede jugar"
        }
        if (!luchadorTurno!!.estaMuerto()) {
            // esto es mas q todo para las sangres de lo multimans o sea no afecta a las IAs
            if (luchadorTurno!!.esMultiman()) {
                for (sh in luchadorTurno!!.hechizos.values) {
                    if (sh.esAutomaticoAlFinalTurno()) {
                        luchadorTurno!!.celdaPelea?.let { intentarLanzarHechizo(luchadorTurno, sh, it, true) }
                    }
                }
            }
        }
        _tempAccion = ""
        _vecesQuePasa = 0
        _tiempoTurno = System.currentTimeMillis()
        luchadorTurno!!.setPuedeJugar(false)
        luchadorTurno!!.ultimoElementoDaño = Constantes.ELEMENTO_NULO.toInt()
        try {
            if (!luchadorTurno!!.estaMuerto()) {
                GestorSalida.ENVIAR_GTF_FIN_DE_TURNO(this, 7, luchadorTurno!!.id)
                try {
                    Thread.sleep(250)
                } catch (ignored: Exception) {
                }

                if ((_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) && retos != null
                    && luchadorTurno!!.esNoIA()
                ) {
                    loop@ for ((retoID, reto) in retos!!) {
                        var exitoReto = reto.estado
                        if (exitoReto != EstReto.EN_ESPERA) {
                            continue
                        }
                        when (retoID) {
                            Constantes.RETO_EL_DOS_POR_UNO // el dos por uno
                            -> if (_cantMuertosReto2x1 > 0 && _cantMuertosReto2x1.toInt() != 2) {
                                exitoReto = EstReto.FALLADO
                            }
                            Constantes.RETO_ZOMBI -> {
                                if (luchadorTurno!!.pmUsados != 0) {
                                    break@loop
                                }
                                exitoReto = EstReto.FALLADO
                            }
                            Constantes.RETO_ESTATUA -> {
                                if (luchadorTurno!!.idCeldaInicioTurno == luchadorTurno!!.celdaPelea?.id!!.toInt()) {
                                    break@loop
                                }
                                exitoReto = EstReto.FALLADO
                            }
                            Constantes.RETO_JARDINERO -> {
                                if (hechizoDisponible(luchadorTurno!!, 367)) {// zanahowia
                                    break@loop
                                }
                                exitoReto = EstReto.FALLADO
                            }
                            Constantes.RETO_NOMADA -> {
                                if (luchadorTurno!!.pmRestantes <= 0) {
                                    break@loop
                                }
                                exitoReto = EstReto.FALLADO
                            }
                            Constantes.RETO_SEPULTURERO -> {
                                if (hechizoDisponible(luchadorTurno!!, 373)) {// invocacion chaferloko
                                    break@loop
                                }
                                exitoReto = EstReto.FALLADO
                            }
                            Constantes.RETO_CASINO_REAL -> {
                                if (hechizoDisponible(luchadorTurno!!, 101)) {// ruleta
                                    break@loop
                                }
                                exitoReto = EstReto.FALLADO
                            }
                            Constantes.RETO_ARACNOFILO -> {
                                if (hechizoDisponible(luchadorTurno!!, 370)) {
                                    break@loop
                                }
                                exitoReto = EstReto.FALLADO
                            }
                            Constantes.RETO_ENTOMOLOGO -> {
                                if (hechizoDisponible(luchadorTurno!!, 311)) {// escarainvoc
                                    break@loop
                                }
                                exitoReto = EstReto.FALLADO
                            }
                            Constantes.RETO_AUDAZ -> {
                                if (Camino.hayAlrededorAmigoOEnemigo(mapaCopia, luchadorTurno!!, false, false)) {
                                    break@loop
                                }
                                exitoReto = EstReto.FALLADO
                            }
                            Constantes.RETO_PEGAJOSO -> {
                                if (Camino.hayAlrededorAmigoOEnemigo(mapaCopia, luchadorTurno!!, true, false)) {
                                    break@loop
                                }
                                exitoReto = EstReto.FALLADO
                            }
                            Constantes.RETO_ANACORETA -> {
                                if (!Camino.hayAlrededorAmigoOEnemigo(mapaCopia, luchadorTurno!!, true, false)) {
                                    break@loop
                                }
                                exitoReto = EstReto.FALLADO
                            }
                            Constantes.RETO_PUSILANIME -> {
                                if (!Camino.hayAlrededorAmigoOEnemigo(mapaCopia, luchadorTurno!!, false, false)) {
                                    break@loop
                                }
                                exitoReto = EstReto.FALLADO
                            }
                            Constantes.RETO_IMPETUOSO -> {
                                if (luchadorTurno!!.paRestantes <= 0) {
                                    break@loop
                                }
                                exitoReto = EstReto.FALLADO
                            }
                        }
                        reto.estado = exitoReto
                    }
                }
                val luchTurno = luchadorTurno
                EfectoHechizo.buffFinTurno(luchTurno!!)
                if (!luchTurno.estaMuerto()) {
                    if (luchTurno.celdaPelea!!.glifos != null) {
                        for (glifo in luchTurno.celdaPelea!!.glifos!!) {
                            if (fase == Constantes.PELEA_FASE_FINALIZADO) {
                                return "Se finalizó la pelea en glifos"
                            }
                            if (luchTurno.estaMuerto()) {
                                continue
                            }
                            if (glifo.esInicioTurno()) {
                                continue
                            }
                            glifo.activarGlifo(luchTurno)
                            if (luchTurno.pdvConBuff <= 0) {
                                addMuertosReturnFinalizo(luchTurno, glifo.lanzador)
                            }
                        }
                    }
                }
                if (!luchTurno.estaMuerto()) {
                    // Disminuye los estados y buffs
                    luchTurno.disminuirBuffsPelea()
                }
                if (luchTurno.personaje != null && luchTurno.personaje!!.enLinea()) {
                    GestorSalida.ENVIAR_As_STATS_DEL_PJ(luchTurno.personaje!!)
                }
                luchTurno.resetPuntos()
                GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_TODOS(this, 7, true)
                Thread.sleep(250)
            }
//            System.out.println("Tiempo espera temp total: "+_tiempoesperatemp)
            if (_tiempoesperatemp > _tiempoesperaturno) {
                _tiempoesperaturno = _tiempoesperatemp
            }
            if (luchadorTurno?.estaMuerto() == true || luchadorTurno?.pdvConBuff ?: 1 <= 0) addMuertosReturnFinalizo(
                luchadorTurno!!, null
            )
            if (luchadorOrden?.estaMuerto() == true) {
                addMuertosReturnFinalizo(luchadorOrden!!, null)
            }
            iniciarTurno()
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln("Excepcion de fin de turno $e")
            e.printStackTrace()
            pasarTurno(null)
        }

        return "Return GOOD !!"
    }

    fun intentarMoverse(movedor: Luchador?, path: String, idUnica: Int, AJ: AccionDeJuego?): String {
        if (movedor == null || !movedor.puedeJugar()) {
            return "no"
        }
        _tacleado = false
        if (luchadorTurno == null || _tempAccion.isNotEmpty() || fase != Constantes.PELEA_FASE_COMBATE || path
                .isEmpty()
        ) {
            if (movedor.personaje != null) {
                if (path.isEmpty()) {
                    GestorSalida.ENVIAR_Im_INFORMACION(movedor.personaje!!, "1102")
                } else if (_tempAccion.isNotEmpty()) {
                    if (AtlantaMain.PARAM_JUGAR_RAPIDO) {
                        finAccion(movedor)
                    } else {
                        GestorSalida.ENVIAR_Im_INFORMACION(movedor.personaje!!, "1REALIZANDO_TEMP_ACCION;$_tempAccion")
                    }
                }
            }
            return "no"
        }
        // movedor.tieneEstado(55) poner el estado inmovible
        if (luchadorTurno!!.id != movedor.id) {
            if (movedor.personaje != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(movedor.personaje!!, "1NO_ES_TU_TURNO")
            }
            return "no"
        }
        val persoM = movedor.personaje
        if (movedor.tieneEstado(Constantes.ESTADO_ARRAIGADO.toInt()) || movedor.esInvisible(0)) {
            // no es tacleado
        } else {
            // esto es para ser tacleado
            var porcHuida = 100
            var agiTac = 0
            var paso = false
            val tacleadores = ArrayList<Int?>()
            for (i in 0..3) {
                val tacleador = movedor.celdaPelea?.id?.let {
                    Camino.getEnemigoAlrededor(
                        it, mapaCopia!!, tacleadores,
                        movedor.equipoBin.toInt()
                    )
                }
                if (tacleador != null) {
                    if (!tacleador.esEstatico() && !tacleador.esInvisible(0)) {
                        tacleadores.add(tacleador.id)
                        // no puede placar con estado arraigado
                        if (!tacleador.tieneEstado(Constantes.ESTADO_ARRAIGADO.toInt())) {
                            paso = true
                            if (AtlantaMain.PARAM_FORMULA_TIPO_OFICIAL) {
                                agiTac += tacleador.totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_AGILIDAD) + tacleador
                                    .totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_PLACAJE) * 10
                            } else {
                                porcHuida = EfectoHechizo.getPorcHuida(movedor, tacleador) * porcHuida / 100
                            }
                        }
                    }
                } else {
                    break
                }
            }
            if (paso) {
                if (AtlantaMain.PARAM_FORMULA_TIPO_OFICIAL) {
                    var agiMov = movedor.totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_AGILIDAD) + movedor
                        .totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_HUIDA) * 10
                    agiTac = max(0, agiTac)
                    agiMov = max(0, agiMov)
                    porcHuida = EfectoHechizo.getPorcHuida2(agiMov, agiTac)
                }
                val random = Formulas.getRandomInt(1, 100)
                if (AtlantaMain.PARAM_MOSTRAR_PROBABILIDAD_TACLEO) {
                    GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_PELEA(
                        this, "% FUITE: <b>" + porcHuida + "</b>, RANDOM: <b>" + random
                                + "</b>", Constantes.COLOR_NARANJA
                    )
                }
                if (random > porcHuida) {
                    GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 104, movedor.id.toString() + ";", "")
                    // PA
                    var pierdePA = max(0, movedor.paRestantes)
                    pierdePA = round((pierdePA * (100 - porcHuida) / 100f).toDouble()).toInt()
                    pierdePA = abs(pierdePA)
                    // PM
                    var pierdePM = max(0, movedor.pmRestantes)
                    if (!AtlantaMain.PARAM_FORMULA_TIPO_OFICIAL) {
                        pierdePM = round((pierdePM * (100 - porcHuida) / 100f).toDouble()).toInt()
                        pierdePM = abs(pierdePM)
                        pierdePM = max(1, pierdePM)
                    }
                    pierdePM = movedor.addPMRestantes(-pierdePM)
                    if (pierdePM != 0) {
                        GestorSalida.ENVIAR_GA_ACCION_PELEA(
                            this,
                            7,
                            129,
                            movedor.id.toString() + "",
                            movedor.id.toString() + "," + pierdePM
                        )
                    }
                    pierdePA = movedor.addPARestantes(-pierdePA)
                    if (pierdePA != 0) {
                        GestorSalida.ENVIAR_GA_ACCION_PELEA(
                            this,
                            7,
                            102,
                            movedor.id.toString() + "",
                            movedor.id.toString() + "," + pierdePA
                        )
                    }
                    _tacleado = true
                    return "tacleado"
                }
            }
        }
        var moverse = "ok"
        val pathRef = AtomicReference(path)
        var ultimaCelda: Short = -1
        try {
            ultimaCelda = Encriptador.hashACeldaID(path.substring(path.length - 2))
        } catch (ignored: Exception) {
        }

        var nroCeldasMov = movedor.celdaPelea?.id?.let {
            Camino.nroCeldasAMover(mapaCopia!!, this, pathRef, it, ultimaCelda, null).toInt()
        }
        // System.out.println("celdas " + nroCeldasMov);
        if (nroCeldasMov != null) {
            if (nroCeldasMov >= 10000) {
                moverse = "stop"
                if (nroCeldasMov >= 20000) {
                    // invisible
                    if (movedor.personaje != null) {
                        GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 151, movedor.id.toString() + "", "-1")
                        GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(movedor.personaje!!, movedor.id, _idUnicaAccion)
                    }
                    nroCeldasMov -= 10000
                }
                nroCeldasMov -= 10000
            }
        }
        // System.out.println("nroCeldasMov " + nroCeldasMov);
        if (nroCeldasMov != null) {
            if ((nroCeldasMov <= 0 || nroCeldasMov > movedor.pmRestantes)) {
                if (movedor.esNoIA()) {
                    GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(movedor.personaje!!, movedor.id, _idUnicaAccion)
                }
                return "no"
            }
        }
        if (AJ != null) {
            if (nroCeldasMov != null) {
                AJ.celdas = nroCeldasMov
            }
        }
        movedor.addPMRestantes(-nroCeldasMov!!)
        movedor.addPMUsados(nroCeldasMov.toInt())
        if ((_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) && retos != null
            && movedor.personaje != null
        ) {
            loop@ for ((retoID, reto) in retos!!) {
                var exitoReto = reto.estado
                if (exitoReto != EstReto.EN_ESPERA) {
                    continue
                }
                when (retoID) {
                    Constantes.RETO_ZOMBI -> {
                        if (movedor.pmUsados == 1) {
                            break@loop
                        }
                        exitoReto = EstReto.FALLADO
                    }
                }
                reto.estado = exitoReto
            }
        }
        val nuevoPath = pathRef.get()
        val ultPathMov = nuevoPath.substring(nuevoPath.length - 3)
        val sigCeldaID = Encriptador.hashACeldaID(ultPathMov.substring(1))
        val nuevaCelda = mapaCopia!!.getCelda(sigCeldaID)
        if (nuevaCelda == null) {
            if (movedor.personaje != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(movedor.personaje!!, "1102")
                GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(movedor.personaje!!, movedor.id, _idUnicaAccion)
            }
            return "no"
        }
        movedor.direccion = ultPathMov[0]
        if (persoM != null) {// confirma el inicio de una accion
            GestorSalida.ENVIAR_GAS_INICIO_DE_ACCION(persoM, movedor.id)
        }
        _idUnicaAccion = idUnica
        // confirma q se movio
        GestorSalida.ENVIAR_GA_ACCION_PELEA_MOVERSE(this, movedor, 7, _idUnicaAccion, 1, movedor.id.toString() + "", "a"
                + movedor.celdaPelea?.id?.let { Encriptador.celdaIDAHash(it) } + nuevoPath)
        val portador = movedor.portador
        if (portador != null && nuevaCelda !== portador.celdaPelea) {
            movedor.celdaPelea?.removerLuchador(movedor)
            movedor.celdaPelea = nuevaCelda
            quitarTransportados(portador)
            if (movedor.ia != null) {
                movedor.ia!!.nullear()
            }
        } else {
            movedor.celdaPelea?.moverLuchadoresACelda(nuevaCelda)
        }
        // final Luchador transportado = movedor.getTransportando();
        // if (transportado != null) {
        // transportado.setCeldaPelea(movedor.getCeldaPelea());
        // }
        _ultimoMovedorIDReto = movedor.id
        _tempAccion = "Moverse"
        GestorSalida.ENVIAR_GA_ACCION_PELEA(
            this,
            7,
            129,
            movedor.id.toString() + "",
            movedor.id.toString() + "," + -nroCeldasMov
        )
        if (persoM == null) {
            val factor = if (nroCeldasMov >= 4) 280 else 400
            try {
                val timer = 200 + factor * nroCeldasMov
//                movedor.tiempoempujado += (timer / 1.8).toLong()
//                _tiempoesperatemp += (timer / 1.3).toLong()
//                System.out.println("Moviendose: "+_tiempoesperatemp)
//                System.out.println("Total: "+_tiempoesperatemp)
                Thread.sleep((timer / 1.35).toInt().toLong())
            } catch (ignored: Exception) {
            }

            // GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(this, 7, movedor.getID(),
            // movedor.getCeldaPelea().getID());
            _tempAccion = ""
            EfectoHechizo.verificaTrampas(movedor)
        } else {
            // test
            // finalizarMovimiento(persoM);
        }
        return moverse
    }

    fun finalizarMovimiento(perso: Personaje): Boolean {
        var perso = perso
        if (_tacleado) {
            GestorSalida.ENVIAR_BN_NADA(perso, "FIN MOVIMIENTO TACLEADO")
            return false
        }
        if (luchadorTurno == null || _tempAccion.isEmpty() || fase != Constantes.PELEA_FASE_COMBATE) {
            GestorSalida.ENVIAR_BN_NADA(perso, "FIN MOVIMIENTO OTROS")
            return false
        }
        var idLuch = perso.Id
        if (idLuch != _ultimoMovedorIDReto) {
            if (perso.compañero == null) {
                return false
            }
            idLuch = perso.compañero.Id
            perso = perso.compañero
            if (idLuch != _ultimoMovedorIDReto) {
                return false
            }
        }
        val luchador = getLuchadorPorID(idLuch)
        if (luchador == null) {
            GestorSalida.ENVIAR_BN_NADA(perso, "FIN MOVIMIENTO LUCHADOR NULL")
            return false
        }
        // GestorSalida.ENVIAR_GA_PERDER_PM_PELEA(this, 7, _tempAccion);
        // eso puede ser opcional si borro el GAC
        _idUnicaAccion = -1
        _tempAccion = ""
        EfectoHechizo.verificaTrampas(luchador)
        GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(perso, idLuch, -1)
        GestorSalida.ENVIAR_GAs_PARAR_MOVIMIENTO_SPRITE(perso, idLuch)
        return true
    }

    fun LanzarHechizo(
        lanzador: Luchador?, SH: StatHechizo?,
        celdaObjetivo: Celda
    ) {
        if (SH == null) {
            return
        }
        if (lanzador == null) {
            return
        }
        var perso = lanzador.personaje
        var costePA = SH.costePA
        var cantObjetivos = 0
        var esFC = false
        var puede = EstadoLanzHechizo.PODER
        lanzador.addHechizoLanzado(lanzador, SH, celdaObjetivo.primerLuchador)
        val esGC = lanzador.puedeGolpeCritico(SH)
        val efectos = if (esGC) SH.efectosCriticos else SH.efectosNormales
        val hechizoStr = SH.hechizoID.toString() + "," + celdaObjetivo.id + "," + SH.spriteID + "," + SH
            .grado + "," + SH.spriteInfos
        GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 300, lanzador.id.toString() + "", hechizoStr)
        if (esGC) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 301, lanzador.id.toString() + "", hechizoStr)
        }
        // el PA cambiado , hace un setSpellStateOnAllContainers a todos los hechizos, y asi
        // actualiza los estados
        if (costePA > 0 && lanzador.esInvisible(0)) {
            lanzador.celdaPelea?.id?.let {
                GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(
                    this,
                    7,
                    lanzador.id,
                    it
                )
            }
        }
        cantObjetivos = Hechizo.aplicaHechizoAPeleaSinGTM(
            this, lanzador, celdaObjetivo, efectos, TipoDaño.NORMAL,
            esGC
        )
        // salio del fallo o lanz normal
        if (perso != null) {
            GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(perso, lanzador.id, -1)
        }
        if (cantObjetivos > 0) {
            GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_TODOS(this, 7, true)
        }
        if (lanzador.ia != null) {
            lanzador.tiempoempujado += (_tiempoHechizo / 1.3).toLong()
            _tiempoesperatemp += (_tiempoHechizo / 1.1).toLong()
            animacionhechizo = true
//                System.out.println("Hechizo: "+_tiempoesperatemp)
//                System.out.println("Total: "+_tiempoesperatemp)
//                try {
//                    Thread.sleep((_tiempoHechizo / 1.3).toInt().toLong())
//                } catch (ignored: Exception) {
//                }

        }
        try {
            if (lanzador.empujo) {
                existioempuje = true
//                Thread.sleep(lanzador.tiempoempujado)
                lanzador.tiempoempujado = 0
                lanzador.empujo = false
                _tiempoesperatemp = 0L
                _tiempoesperaturno = 0L
                animacionhechizo = false
            }
        } catch (e: Exception) {

        }
        if (esFC && (lanzador.ia != null || SH.esFinTurnoSiFC())) {
            puede = EstadoLanzHechizo.FALLAR
            pasarTurno(lanzador)
        }
        if (lanzador.esNoIA()) {
            GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso!!)
        }
        _tempAccion = ""
        if (lanzador.ia != null) {
            lanzador.ia!!.nullear()
        }
        comprobarPasarTurnoDespuesMuerte(lanzador)
        if (lanzador.estaMuerto()) {
            pasarTurno(lanzador)
        }
        if (AtlantaMain.MODO_DEBUG) {
            println(
                "intentarLanzarHechizo() Hechizo: " + SH.hechizo!!.nombre + " (" + SH.hechizoID
                        + ") Estado: " + puede
            )
        }
        return
    }

    @Synchronized
    fun intentarLanzarHechizo(
        lanzador: Luchador?, SH: StatHechizo?,
        celdaObjetivo: Celda, obligaLanzar: Boolean
    ): EstadoLanzHechizo {
        if (lanzador == null) {
            return EstadoLanzHechizo.NO_PODER
        }
        if (lanzador.pelea.puedeLanzarHechizo(
                lanzador,
                SH,
                celdaObjetivo,
                lanzador.celdaPelea!!.id
            ) == EstadoLanzHechizo.NO_PODER
        ) {
            if (lanzador.esNoIA()) {
                GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(lanzador.personaje!!)
            }
            return EstadoLanzHechizo.NO_PODER
        }
        val perso = lanzador.personaje
        if (!lanzador.puedeJugar()) {
            if (lanzador.esNoIA()) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso!!, "1NO_ES_TU_TURNO")
                GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
            }
            if (AtlantaMain.MODO_DEBUG) {
                println(
                    "intentarLanzarHechizo() Hechizo: " + SH!!.hechizo!!.nombre + " (" + SH.hechizoID
                            + ") Estado: NO PODER"
                )
            }
            return EstadoLanzHechizo.NO_PODER
        }
        if (_tempAccion.isNotEmpty() && perso != null || SH == null) {
            if (lanzador.esNoIA()) {
                if (_tempAccion.isNotEmpty()) {
                    if (!AtlantaMain.PARAM_JUGAR_RAPIDO) {
                        GestorSalida.ENVIAR_Im_INFORMACION(perso!!, "1REALIZANDO_TEMP_ACCION;$_tempAccion")
                    } else {
                        finAccion(lanzador)
                        if (AtlantaMain.MODO_DEBUG) {
                            println(
                                "intentarLanzarHechizo() Hechizo: " + Objects.requireNonNull(SH)!!.hechizo!!.nombre + " (" + SH!!
                                    .hechizoID + ") Estado: NO PODER"
                            )
                        }
                        return EstadoLanzHechizo.NO_PODER
                    }
                } else {
                    GestorSalida.ENVIAR_Im_INFORMACION(perso!!, "1169")
                }
                GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
            }
            if (AtlantaMain.MODO_DEBUG) {
                println(
                    "intentarLanzarHechizo() Hechizo: " + Objects.requireNonNull(SH)!!.hechizo!!.nombre + " (" + SH!!.hechizoID
                            + ") Estado: NO PODER"
                )
            }
            return EstadoLanzHechizo.NO_PODER
        }
        _tempAccion = "Hechizo"
        _tiempoHechizo = EfectoHechizo.TIEMPO_POR_LANZAR_HECHIZO
        if (luchadorTurno!!.ia != null) {
            var empuja = false
            for (efecto in SH.efectosNormales) {
                if (efecto.efectoID == 5) {
                    empuja = true
                }
            }
            if (empuja && animacionhechizo) {
                animacionhechizo = false
//                try {
//                    Thread.sleep(_tiempoesperaturno)
//                } catch (e: Exception) {
//                    AtlantaMain.redactarLogServidorln("Error en la espera de turno. Mapa: ${this.mapaReal!!.id}")
//                }
                _tiempoesperaturno = 0L
            }
        }
        var puede = EstadoLanzHechizo.PODER
        puede = puedeLanzarHechizo(
            lanzador, SH, celdaObjetivo,
            (-1).toShort()
        )
        if (obligaLanzar || puede == EstadoLanzHechizo.PODER) {
            var costePA = SH.costePA.toInt()
            if (perso != null && perso.tieneModfiSetClase(SH.hechizoID)) {
                costePA -= perso.getModifSetClase(SH.hechizoID, 285)
                if (costePA < 0) {
                    costePA = 0
                }
            }
            if (perso != null) {
                GestorSalida.ENVIAR_GAS_INICIO_DE_ACCION(perso, lanzador.id)// inicia la accion
            }
            lanzador.addPARestantes(-costePA)
            lanzador.addPAUsados(costePA)
            //			 try {
            ////			 aun cuando el socket se pierde (desconexion del jugador) el thread continua su curso hasta
            ////			 terminar su metodo.
            //			 Thread.sleep(10000);
            //			 } catch (InterruptedException e) {
            //			 e.printStackTrace();
            //			 }
            val esFC = lanzador.puedeFalloCritico(SH)
            var cantObjetivos = 0
            GestorSalida.ENVIAR_GA_ACCION_PELEA(
                this,
                7,
                102,
                lanzador.id.toString() + "",
                lanzador.id.toString() + "," + -costePA
            )
            if (esFC) {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(
                    this,
                    7,
                    302,
                    lanzador.id.toString() + "",
                    SH.hechizoID.toString() + ""
                )
            } else {// es golpe normal
                if ((_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) && retos != null
                    && lanzador.esNoIA()
                ) {
                    for ((retoID, reto) in retos!!) {
                        var exitoReto = reto.estado
                        if (exitoReto != EstReto.EN_ESPERA) {
                            continue
                        }
                        when (retoID) {
                            Constantes.RETO_AHORRADOR, Constantes.RETO_VERSATIL -> if (lanzador.hechizosLanzadosReto.contains(
                                    SH.hechizoID
                                )
                            ) {
                                exitoReto = EstReto.FALLADO
                            } else {
                                lanzador.hechizosLanzadosReto.add(SH.hechizoID)
                            }
                            Constantes.RETO_LIMITADO -> {
                                val hechizoID = SH.hechizoID
                                if (lanzador.idHechizoLanzado == -1) {
                                    lanzador.idHechizoLanzado = hechizoID
                                } else if (lanzador.idHechizoLanzado != hechizoID) {
                                    exitoReto = EstReto.FALLADO
                                }
                            }
                        }
                        reto.estado = exitoReto
                    }
                }
                lanzador.addHechizoLanzado(lanzador, SH, celdaObjetivo.primerLuchador)
                val esGC = lanzador.puedeGolpeCritico(SH)
                val efectos = if (esGC) SH.efectosCriticos else SH.efectosNormales
                val hechizoStr = SH.hechizoID.toString() + "," + celdaObjetivo.id + "," + SH.spriteID + "," + SH
                    .grado + "," + SH.spriteInfos
                GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 300, lanzador.id.toString() + "", hechizoStr)
                if (esGC) {
                    GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 301, lanzador.id.toString() + "", hechizoStr)
                }
                // el PA cambiado , hace un setSpellStateOnAllContainers a todos los hechizos, y asi
                // actualiza los estados
                if (costePA > 0 && lanzador.esInvisible(0)) {
                    lanzador.celdaPelea?.id?.let {
                        GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(
                            this,
                            7,
                            lanzador.id,
                            it
                        )
                    }
                }
                cantObjetivos = Hechizo.aplicaHechizoAPeleaSinGTM(
                    this, lanzador, celdaObjetivo, efectos, TipoDaño.NORMAL,
                    esGC
                )
            }
            // salio del fallo o lanz normal
            if (perso != null) {
                GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(perso, lanzador.id, -1)
            }
            if (cantObjetivos > 0) {
                GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_TODOS(this, 7, true)
            }
            if (lanzador.ia != null) {
                lanzador.tiempoempujado += (_tiempoHechizo / 1.3).toLong()
                _tiempoesperatemp += (_tiempoHechizo / 1.1).toLong()
                animacionhechizo = true
//                System.out.println("Hechizo: "+_tiempoesperatemp)
//                System.out.println("Total: "+_tiempoesperatemp)
//                try {
//                    Thread.sleep((_tiempoHechizo / 1.3).toInt().toLong())
//                } catch (ignored: Exception) {
//                }

            }
            try {
                if (lanzador.empujo) {
                    existioempuje = true
                    Thread.sleep(lanzador.tiempoempujado)
                    lanzador.tiempoempujado = 0
                    lanzador.empujo = false
                    _tiempoesperatemp = 0L
                    _tiempoesperaturno = 0L
                    animacionhechizo = false
                }
            } catch (e: Exception) {

            }
            if (esFC && (lanzador.ia != null || SH.esFinTurnoSiFC())) {
                puede = EstadoLanzHechizo.FALLAR
                pasarTurno(lanzador)
            }
        } else if (lanzador.esNoIA()) {
            GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso!!)
        }
        _tempAccion = ""
        if (lanzador.ia != null) {
            lanzador.ia!!.nullear()
        }
        comprobarPasarTurnoDespuesMuerte(lanzador)
        if (lanzador.estaMuerto()) {
            pasarTurno(lanzador)
        }
        if (AtlantaMain.MODO_DEBUG) {
            println(
                "intentarLanzarHechizo() Hechizo: " + SH.hechizo!!.nombre + " (" + SH.hechizoID
                        + ") Estado: " + puede
            )
        }
        return puede
    }

    // ataque cuerpo a cuerpo CAC
    @Synchronized
    fun intentarCAC(perso: Personaje, idCeldaObj: Short) {
        val lanzador = getLuchadorPorID(perso.Id)
        if (lanzador == null) {
            GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
            return
        }
        if (!lanzador.puedeJugar()) {
            if (lanzador.esNoIA()) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1175")
                GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
            }
            return
        }
        if (_tempAccion.isNotEmpty()) {
            if (lanzador.esNoIA()) {
                if (_tempAccion.isNotEmpty()) {
                    if (!AtlantaMain.PARAM_JUGAR_RAPIDO) {
                        GestorSalida.ENVIAR_Im_INFORMACION(perso, "1REALIZANDO_TEMP_ACCION;$_tempAccion")
                    } else {
                        finAccion(lanzador)
                        return
                    }
                }
                GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
            }
            return
        }
        if ((_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) && retos != null) {// mobs
            for ((retoID, reto) in retos!!) {
                var exitoReto = reto.estado
                if (exitoReto != EstReto.EN_ESPERA) {
                    continue
                }
                when (retoID) {
                    Constantes.RETO_AHORRADOR, Constantes.RETO_VERSATIL -> if (lanzador.hechizosLanzadosReto.contains(0)) {
                        exitoReto = EstReto.FALLADO
                    } else {
                        lanzador.hechizosLanzadosReto.add(0)
                    }
                    Constantes.RETO_MISTICO -> exitoReto = EstReto.FALLADO
                    Constantes.RETO_LIMITADO -> {
                        val hechizoID = 0
                        if (lanzador.idHechizoLanzado == -1) {
                            lanzador.idHechizoLanzado = hechizoID
                        } else if (lanzador.idHechizoLanzado != hechizoID) {
                            exitoReto = EstReto.FALLADO
                        }
                    }
                }
                reto.estado = exitoReto
            }
        }
        var SH = Mundo.getHechizo(0)!!.getStatsPorNivel(1)
        var eNormales = SH?.efectosNormales
        var eCriticos: ArrayList<EfectoHechizo>? = SH?.efectosCriticos
        val arma = perso.getObjPosicion(Constantes.OBJETO_POS_ARMA)
        if (arma != null) {
            SH = arma.objModelo?.statHechizo
            eNormales = arma.efectosNormales
            eCriticos = arma.efectosCriticos
            val costePA = arma.objModelo?.costePA?.toInt()
            if (AtlantaMain.MAX_GOLPES_CAC[costePA] != null) {
                val maximo = AtlantaMain.MAX_GOLPES_CAC[costePA]
                if (maximo != null) {
                    if (maximo <= _cantCAC) {
                        GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
                        return
                    }
                }
            }
        }
        val celdaObjetivo = mapaCopia!!.getCelda(idCeldaObj)
        val puede = puedeLanzarHechizo(lanzador, SH, celdaObjetivo, (-1).toShort())
        if (puede != EstadoLanzHechizo.PODER) {
            if (perso.LiderIP != null) {
                GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso.LiderIP)
            }
            GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
            return
        }
        if (AtlantaMain.MAX_CAC_POR_TURNO in 1.._cantCAC) {
            GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(perso)
            return
        }
        _cantCAC++
        if (lanzador.esInvisible(0)) {
            lanzador.hacerseVisible()
        }
        GestorSalida.ENVIAR_GAS_INICIO_DE_ACCION(lanzador.personaje!!, lanzador.id)
        val costePA = SH?.costePA
        lanzador.addPARestantes(-costePA!!)
        lanzador.addPAUsados(costePA.toInt())
        GestorSalida.ENVIAR_GA_ACCION_PELEA(
            this,
            7,
            102,
            perso.Id.toString() + "",
            perso.Id.toString() + "," + -costePA
        )
        val esFC = SH?.let { lanzador.puedeFalloCritico(it) }
        if (esFC!!) {
            GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 305, perso.Id.toString() + "", "")
        } else {
            _tempAccion = "CAC"
            val esGC = SH?.let { lanzador.puedeGolpeCritico(it) }
            GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 303, perso.Id.toString() + "", idCeldaObj.toString() + "")
            if (esGC!!) {
                GestorSalida.ENVIAR_GA_ACCION_PELEA(this, 7, 301, perso.Id.toString() + "", "0")
            }
            if (celdaObjetivo != null) {
                Hechizo.aplicaHechizoAPelea(
                    this,
                    lanzador,
                    celdaObjetivo,
                    if (esGC) eCriticos else eNormales,
                    TipoDaño.CAC,
                    esGC
                )
            }
            _tempAccion = ""
        }
        GestorSalida.ENVIAR_GAF_FINALIZAR_ACCION(perso, perso.Id, -1)
        if (esFC) {
            pasarTurno(lanzador)
        } else {
            comprobarPasarTurnoDespuesMuerte(lanzador)
        }
    }

    fun puedeLanzarHechizo(
        lanzador: Luchador, SH: StatHechizo?,
        celdaBlancoHechizo: Celda?, celdaDeLanzador: Short
    ): EstadoLanzHechizo {
        if (luchadorTurno == null) {
            return EstadoLanzHechizo.NO_PODER
        }
        val perso = lanzador.personaje
        if (celdaBlancoHechizo == null) {
            if (perso != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1172")
            }
            if (AtlantaMain.MODO_DEBUG) {
                println("puedeLanzarHechizo() -> La celda blanco hechizo es nula")
            }
            return EstadoLanzHechizo.NO_PODER
        }
        if (SH == null) {
            if (perso != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1169")
            }
            if (AtlantaMain.MODO_DEBUG) {
                println("puedeLanzarHechizo() -> El hechizo es nulo")
            }
            return EstadoLanzHechizo.NO_PODER
        }
        if (SH.esAutomaticoAlFinalTurno()) {
            if (perso != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1IS_AUTOMATIC_END_TURN")
            }
            if (AtlantaMain.MODO_DEBUG) {
                println("puedeLanzarHechizo() -> El hechizo es para lanzamiento automatico")
            }
            return EstadoLanzHechizo.NO_PODER
        }
        val objetivo = celdaBlancoHechizo.primerLuchador
        val filtro = filtraHechizoDisponible(lanzador, SH, objetivo?.id ?: 0)
        if (filtro != EstadoLanzHechizo.PODER) {
            return filtro
        }
        if (perso == null) {
            if ((SH.esIntercambioPos() || SH.esSoloMover()) && objetivo != null && objetivo.tieneEstado(
                    Constantes.ESTADO_ARRAIGADO.toInt()
                )
            ) {
                return EstadoLanzHechizo.OBJETIVO_NO_PERMITIDO
            }
        }
        return if (dentroDelRango(lanzador, SH, celdaDeLanzador, celdaBlancoHechizo.id)) {
            EstadoLanzHechizo.NO_TIENE_ALCANCE
        } else EstadoLanzHechizo.PODER
    }

    fun dentroDelRango(lanzador: Luchador, SH: StatHechizo?, celdaIDLanzador: Short, celdaIDBlanco: Short): Boolean {
        if (SH == null) {
            if (AtlantaMain.MODO_DEBUG) {
                println("dentroDelRango() -> El hechizo es nulo")
            }
            return true
        }
        val mapa = mapaCopia
        if (mapa!!.getCelda(celdaIDBlanco) == null) {
            return true
        }
        val perso = lanzador.personaje
        if (SH.esTrampa() && mapa.getCelda(celdaIDBlanco)!!.esTrampa()) {
            if (perso != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1229")
            }
            return true
        }
        val tempCeldaIDLanzador: Short
        tempCeldaIDLanzador = if (celdaIDLanzador <= -1) {
            lanzador.celdaPelea!!.id
        } else {
            celdaIDLanzador
        }
        return !Camino.celdasPosibleLanzamiento(SH, lanzador, mapaCopia!!, tempCeldaIDLanzador, celdaIDBlanco).contains(
            mapa
                .getCelda(celdaIDBlanco)
        )
        // if (SH.esNecesarioCeldaLibre() && mapa.getCelda(celdaIDBlanco).getPrimerLuchador() != null) {
        // if (perso != null) {
        // GestorSalida.ENVIAR_Im_INFORMACION(perso, "1173");
        // }
        // if (Bustemu.MODO_DEBUG) {
        // System.out.println("dentroDelRango() -> El hechizo " + SH.getHechizo().getNombre() +
        // " necesita celda libre");
        // }
        // return false;
        // }
        // boolean modif = false;
        // final int hechizoID = SH.getHechizoID();
        // if (perso != null && perso.tieneModfiSetClase(hechizoID)) {
        // modif = perso.getModifSetClase(hechizoID, 288) == 1;
        // }
        // if (SH.esLanzarLinea() && !modif
        // && !Camino.siCeldasEstanEnMismaLinea(mapa, mapa.getCelda(tempCeldaIDLanzador),
        // mapa.getCelda(celdaIDBlanco))) {
        // if (perso != null) {
        // GestorSalida.ENVIAR_Im_INFORMACION(perso, "1173");
        // }
        // if (Bustemu.MODO_DEBUG) {
        // System.out.println("dentroDelRango() -> El hechizo " + SH.getHechizo().getNombre() +
        // " no esta en Linea");
        // }
        // return false;
        // }
        // modif = false;
        // if (perso != null && perso.tieneModfiSetClase(hechizoID)) {
        // modif = perso.getModifSetClase(hechizoID, 289) == 1;
        // }
        // if (!modif && SH.esLineaVista()
        // && !Camino.lineaDeVistaPelea(mapa, tempCeldaIDLanzador, celdaIDBlanco, lanzador.getID())) {
        // if (perso != null) {
        // GestorSalida.ENVIAR_Im_INFORMACION(perso, "1174");
        // }
        // if (Bustemu.MODO_DEBUG) {
        // System.out.println("dentroDelRango() -> El hechizo " + SH.getHechizo().getNombre() +
        // " tiene linea de vista");
        // }
        // return false;
        // }
        // byte maxAlc = SH.getMaxAlc();
        // final byte minAlc = SH.getMinAlc();
        // modif = false;
        // if (perso != null && perso.tieneModfiSetClase(hechizoID)) {
        // maxAlc += perso.getModifSetClase(hechizoID, 281);
        // modif = perso.getModifSetClase(hechizoID, 282) == 1;
        // }
        // if (modif || SH.esAlcanceModificable()) {
        // maxAlc += lanzador.getTotalStats().getStatParaMostrar(Constantes.STAT_MAS_ALCANCE);
        // }
        // if (maxAlc < minAlc) {
        // maxAlc = minAlc;
        // }
        // final int dist = Camino.distanciaDosCeldas(mapa, tempCeldaIDLanzador, celdaIDBlanco);
        // if (dist < minAlc || dist > maxAlc) {
        // if (perso != null) {
        // GestorSalida.ENVIAR_Im_INFORMACION(perso, "1171;" + minAlc + "~" + maxAlc + "~" + dist);
        // }
        // if (Bustemu.MODO_DEBUG) {
        // System.out.println("dentroDelRango() -> El hechizo " + SH.getHechizo().getNombre() +
        // " esta fuera del alcance");
        // }
        // return false;
        // }
    }

    fun filtraHechizoDisponible(lanzador: Luchador, SH: StatHechizo, idObjetivo: Int): EstadoLanzHechizo {
        val hechizoID = SH.hechizoID
        val perso = lanzador.personaje
        if (SH.esNecesarioObjetivo() && idObjetivo == 0) {
            if (perso != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1NEED_A_TARGET")
            }
            if (AtlantaMain.MODO_DEBUG) {
                println("filtrarHechizo() -> Necesita un objetivo")
            }
            return EstadoLanzHechizo.NO_OBJETIVO
        }
        for (estado in SH.estadosProhibido) {
            if (lanzador.tieneEstado(estado)) {
                if (perso != null) {
                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "1IN_FORBIDDEN_STATE;$estado")
                }
                if (AtlantaMain.MODO_DEBUG) {
                    println(
                        "filtrarHechizo() -> Tiene el estado prohibido $estado para lanzar " + SH
                            .hechizo!!.nombre
                    )
                }
                return EstadoLanzHechizo.NO_PODER
            }
        }
        for (estado in SH.estadosNecesario) {
            if (!lanzador.tieneEstado(estado)) {
                if (perso != null) {
                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "1NOT_IN_REQUIRED_STATE;$estado")
                }
                if (AtlantaMain.MODO_DEBUG) {
                    println(
                        "filtrarHechizo() -> No tiene el estado necesario $estado para lanzar " + SH
                            .hechizo!!.nombre
                    )
                }
                return EstadoLanzHechizo.NO_PODER
            }
        }
        var costePA = SH.costePA.toInt()
        if (perso != null && perso.tieneModfiSetClase(hechizoID)) {
            costePA -= perso.getModifSetClase(hechizoID, 285)
        }
        val PA = lanzador.paRestantes
        if (PA < costePA) {
            if (perso != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1170;" + PA + "~" + SH.costePA)
            }
            if (AtlantaMain.MODO_DEBUG) {
                println("filtrarHechizo() -> No tiene suficientes PA para lanzar " + SH.hechizo!!.nombre)
            }
            return EstadoLanzHechizo.NO_TIENE_PA
        }
        val sigTurnoLanz = HechizoLanzado.poderSigLanzamiento(lanzador, hechizoID)
        if (sigTurnoLanz > 0) {
            if (perso != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1CANT_LAUNCH_BEFORE;$sigTurnoLanz")
            }
            if (AtlantaMain.MODO_DEBUG) {
                println(
                    "filtrarHechizo() -> Falta $sigTurnoLanz turnos para lanzar " + SH.hechizo!!
                        .nombre
                )
            }
            return EstadoLanzHechizo.COOLDOWN
        }
        var nroLanzTurno = SH.maxLanzPorTurno.toInt()
        if (perso != null && perso.tieneModfiSetClase(hechizoID)) {
            nroLanzTurno += perso.getModifSetClase(hechizoID, 290)
        }
        if (nroLanzTurno > 0 && nroLanzTurno - HechizoLanzado.getNroLanzamientos(lanzador, hechizoID) <= 0) {
            if (perso != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(perso, "1CANT_LAUNCH_MORE;$nroLanzTurno")
            }
            if (AtlantaMain.MODO_DEBUG) {
                println(
                    "filtrarHechizo() -> El nroLanzTurno es " + nroLanzTurno
                            + ", por lo tanto no se puede lanzar " + SH.hechizo!!.nombre
                )
            }
            return EstadoLanzHechizo.COOLDOWN
        }
        if (idObjetivo != 0) {
            var nroLanzMaxObjetivo = SH.maxLanzPorObjetivo.toInt()
            if (perso != null && perso.tieneModfiSetClase(hechizoID)) {
                nroLanzMaxObjetivo += perso.getModifSetClase(hechizoID, 291)
            }
            if (nroLanzMaxObjetivo >= 1 && HechizoLanzado.getNroLanzPorObjetivo(
                    lanzador, idObjetivo,
                    hechizoID
                ) >= nroLanzMaxObjetivo
            ) {
                if (perso != null) {
                    GestorSalida.ENVIAR_Im_INFORMACION(perso, "1CANT_ON_THIS_PLAYER")
                }
                if (AtlantaMain.MODO_DEBUG) {
                    println(
                        "filtrarHechizo() -> El nroMaxObjetivo " + nroLanzMaxObjetivo
                                + " por lo tanto no se puede lanzar " + SH.hechizo!!.nombre
                    )
                }
                return EstadoLanzHechizo.OBJETIVO_NO_PERMITIDO
            }
        }
        var esInvo = false
        for (efecto in SH.efectosNormales) {
            when (efecto.efectoID) {
                180, 181, 185, 405 -> esInvo = true
            }
        }
        if (lanzador.nroInvocaciones >= lanzador.totalStats.getTotalStatParaMostrar(
                Constantes.STAT_MAS_CRIATURAS_INVO
            ) && esInvo
        ) {
            if (lanzador.personaje != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(
                    lanzador.personaje, "0CANT_SUMMON_MORE_CREATURE;" + lanzador
                        .nroInvocaciones
                )
                if (lanzador.personaje!!.LiderIP != null) {
                    GestorSalida.ENVIAR_Im_INFORMACION(
                        lanzador.personaje!!.LiderIP, "0CANT_SUMMON_MORE_CREATURE;" + lanzador
                            .nroInvocaciones
                    )
                    GestorSalida.ENVIAR_GAC_LIMPIAR_ACCION(lanzador.personaje!!.LiderIP)
                }

            }
            return EstadoLanzHechizo.NO_PODER
        }
        return EstadoLanzHechizo.PODER
    }

    private fun robarPersonajePerdedor(luch: Luchador) {
        if (!AtlantaMain.PARAM_JUGADORES_HEROICO_MORIR) {
            return
        }
        val pjPerdedor = luch.personaje ?: return
        if (luch.fueSaqueado()) {
            return
        }
        imprimiAsesinos(luch)
        if (esMapaHeroico()) {
            when (_tipo) {
                Constantes.PELEA_TIPO_DESAFIO, Constantes.PELEA_TIPO_KOLISEO -> {
                }
                else -> {
                    luch.setSaqueado(true)
                    val montura = pjPerdedor.montura
                    if (montura != null) {
                        pjPerdedor.montura = null
                        if (montura.pergamino <= 0) {
                            try {
                                val obj1 = Objects.requireNonNull(montura.objModCertificado)!!.crearObjeto(
                                    1, Constantes.OBJETO_POS_NO_EQUIPADO,
                                    CAPACIDAD_STATS.RANDOM
                                )
                                obj1.fijarStatValor(Constantes.STAT_CONSULTAR_MONTURA, abs(montura.id))
                                obj1.addStatTexto(Constantes.STAT_PERTENECE_A, "0#0#0#" + pjPerdedor.nombre)
                                obj1.addStatTexto(Constantes.STAT_NOMBRE, "0#0#0#" + montura.nombre)
                                pjPerdedor.addObjetoConOAKO(obj1, true)
                                montura.pergamino = obj1.id
                            } catch (ignored: Exception) {
                            }

                        }
                    }
                    val kamas = pjPerdedor.kamas
                    _kamasRobadas += kamas
                    _expRobada += pjPerdedor.experiencia / 10
                    pjPerdedor.addKamas(-kamas, false, false)
                    val objPerder = ArrayList(pjPerdedor.objetosTodos)
                    for (obj in objPerder) {
                        if (robarObjPersonaje(obj, pjPerdedor)) {
                            pjPerdedor.borrarOEliminarConOR(obj.id, false)
                        }
                    }
                    objPerder.clear()
                    objPerder.addAll(pjPerdedor.objetosTienda)
                    for (obj in objPerder) {
                        if (robarObjPersonaje(obj, pjPerdedor)) {
                            pjPerdedor.borrarObjTienda(obj)
                        }
                    }
                    pjPerdedor.convertirseTumba()
                    GestorSQL.INSERT_CEMENTERIO(
                        pjPerdedor.nombre, pjPerdedor.nivel, pjPerdedor.sexo, pjPerdedor
                            .getClaseID(true), _asesinos.toString(), mapaReal!!.subArea!!.id
                    )
                    GestorSQL.SALVAR_PERSONAJE(pjPerdedor, true)
                }
            }
        }
    }

    // private int getPorcFinal(int porcentaje, float coef) {
// // formulda para drop porcentaje
// int f = (int) ((1 - (Math.pow(1 - (porcentaje / 100000f), coef))) * 100000);
// return Math.max(1, f);
// }
    private fun robarObjPersonaje(objeto: Objeto, pjPerd: Personaje?): Boolean {
        if (objeto.pasoIntercambiableDesde()) {
            pjPerd!!.addObjetoAlBanco(objeto)
            return false
        }
        if (objeto.tieneStatTexto(Constantes.STAT_LIGADO_A_CUENTA)) {
            pjPerd!!.addObjetoAlBanco(objeto)
            return false
        }
        if (!AtlantaMain.PARAM_HEROICO_PIERDE_ITEMS_VIP && objeto.objModelo?.ogrinas ?: 0 > 0) {
            return false
        }
        if (objeto.posicion in 20..27) {
            return false
        }
        if (objeto.objModelo?.tipo?.toInt() == Constantes.OBJETO_TIPO_OBJETO_DE_BUSQUEDA) {
            return false
        }
        objeto.setPosicion(Constantes.OBJETO_POS_NO_EQUIPADO, pjPerd, false)
        addObjetosRobados(objeto)
        return true
    }

    private fun getMisionPVPPorEquipo(equipo: Int): MisionPVP? {
        if (equipo == 1 && _inicioLuchEquipo1.size == 1) {
            try {
                val init = _luchInit1!!.personaje
                if (init != null && init.misionPVP != null) {
                    val victima = init.misionPVP.nombreVictima
                    for (luchador in _inicioLuchEquipo2) {
                        val p = luchador.personaje ?: continue
                        if (p.nombre.equals(victima, ignoreCase = true)) {
                            return init.misionPVP
                        }
                        if (p.misionPVP != null) {
                            try {
                                if (p.misionPVP.nombreVictima.equals(init.nombre, ignoreCase = true)) {
                                    return p.misionPVP
                                }
                            } catch (ignored: Exception) {
                            }

                        }
                    }
                }
            } catch (ignored: Exception) {
            }

        }
        if (equipo == 2 && _inicioLuchEquipo2.size == 1) {
            try {
                val init = _luchInit2!!.personaje
                if (init != null && init.misionPVP != null) {
                    val victima = init.misionPVP.nombreVictima
                    for (luchador in _inicioLuchEquipo1) {
                        val p = luchador.personaje ?: continue
                        if (p.nombre.equals(victima, ignoreCase = true)) {
                            return init.misionPVP
                        }
                        if (p.misionPVP != null) {
                            try {
                                if (p.misionPVP.nombreVictima.equals(init.nombre, ignoreCase = true)) {
                                    return p.misionPVP
                                }
                            } catch (ignored: Exception) {
                            }

                        }
                    }
                }
            } catch (ignored: Exception) {
            }

        }
        return null
    }

    private fun recompensaMision(luchGanador: Luchador, ganador: Boolean) {
        val mision = getMisionPVPPorEquipo(luchGanador.paramEquipoAliado.toInt()) ?: return
        if (ganador) {
            val objetos = StringBuilder()
            if (mision.esCazaCabezas()) {
                objetos.append(AtlantaMain.MISION_PVP_OBJETOS)
                val craneo = mision.craneo
                if (craneo != 0) {
                    if (objetos.isNotEmpty()) {
                        objetos.append(";")
                    }
                    objetos.append(craneo).append(",").append(1)
                }
                val pergRec = mision.pergRec
                if (pergRec != 0) {
                    if (objetos.isNotEmpty()) {
                        objetos.append(";")
                    }
                }
            }
            for (s in objetos.toString().split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                try {
                    if (s.isEmpty()) {
                        continue
                    }
                    val id = Integer.parseInt(s.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
                    val cant = Integer.parseInt(s.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
                    val obj = Mundo.getObjetoModelo(id)?.crearObjeto(
                        cant, Constantes.OBJETO_POS_NO_EQUIPADO,
                        CAPACIDAD_STATS.RANDOM
                    )
                    if (obj != null) {
                        luchGanador.addDropLuchador(obj, true)
                    }
                } catch (ignored: Exception) {
                }

            }
            if (AtlantaMain.PARAM_GANAR_KAMAS_PVP) {
                val kamas = mision.kamasRecompensa
                luchGanador.addKamasGanadas(kamas)
            }
            if (AtlantaMain.PARAM_GANAR_EXP_PVP) {
                val expPorMision = mision.expMision
                luchGanador.addXPGanada(expPorMision)
                GestorSalida.ENVIAR_Im_INFORMACION(luchGanador.personaje!!, "08;$expPorMision")
            }
        }
        if (mision === luchGanador.personaje!!.misionPVP) {
            luchGanador.personaje!!.eliminarPorObjModeloRecibidoDesdeMinutos(10085, 0)// pergamino
            luchGanador.personaje!!.eliminarPorObjModeloRecibidoDesdeMinutos(9917, 0)// pergamino recompenza
            luchGanador.personaje!!.eliminarPorObjModeloRecibidoDesdeMinutos(10621, 0)// ordenes sacre
            luchGanador.personaje!!.misionPVP = null
        }
    }

    private fun addObjetosRobados(obj: Objeto) {
        if (_objetosRobados == null) {
            _objetosRobados = ArrayList()
        }
        _objetosRobados!!.add(obj)
    }

    fun acaboPelea(equipoMuerto: Byte): Boolean {
        // equipoMuero = 3, verifica si acabo el combate
        var linea = 0
        try {
            if (fase == Constantes.PELEA_FASE_FINALIZADO || Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
                return false
            }
            var equipo1Muerto = true
            var equipo2Muerto = true
            linea = 1
            if (equipoMuerto.toInt() == 3) {
                for (luch in _equipo1.values) {
                    if (luch.esInvocacion()) {
                        continue
                    }
                    if (!luch.estaMuerto()) {
                        equipo1Muerto = false
                        break
                    }
                }
                for (luch in _equipo2.values) {
                    if (luch.esInvocacion()) {
                        continue
                    }
                    if (!luch.estaMuerto()) {
                        equipo2Muerto = false
                        break
                    }
                }
            } else {
                equipo1Muerto = equipoMuerto.toInt() == 1
                equipo2Muerto = equipoMuerto.toInt() == 2
            }
            linea = 2
            if (equipo1Muerto || equipo2Muerto) {
                linea = 3
                if (fase == Constantes.PELEA_FASE_POSICION) {
                    antesIniciarPelea()
                }
                linea = 4
                val packet = getPanelResultados(if (equipo1Muerto) 2 else 1)
                if (equipo1Muerto) {
                    _luchInit2!!.preLuchador?.sobrevivio()
                } else {
                    _luchInit2!!.preLuchador?.murio()
                }
                linea = 5
                mostrarResultados(packet)
                linea = 6
                if (salvarMobHeroico) {
                    mapaReal!!.salvarMapaHeroico()
                }
                linea = 7
            }
            return equipo1Muerto || equipo2Muerto
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(
                "Exception acaboPelea - Mapa: " + mapaCopia!!.id + " PeleaID: " + id
                        + " Linea: " + linea + ", Exception: " + e.toString()
            )
            e.printStackTrace()
        }

        return false
    }

    fun cancelarPelea() {
        try {
            if (fase == Constantes.PELEA_FASE_FINALIZADO || Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
                return
            }
            if (fase == Constantes.PELEA_FASE_POSICION) {
                antesIniciarPelea()
            }
            val packet = getPanelResultados(3)
            _luchInit2!!.preLuchador?.sobrevivio()
            mostrarResultados(packet)
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(
                "Exception cancelarPelea - Mapa: " + mapaCopia!!.id + " PeleaID: " + id
                        + ", Exception: " + e.toString()
            )
            e.printStackTrace()
        }

    }

    private fun mostrarResultados(packet: String) {
        try {
            if (AtlantaMain.SALVAR_LOGS_TIPO_COMBATE.contains(_tipo)) {
                LOG_COMBATES.append(Date()).append("\t").append(_tipo.toInt()).append("\t")
                    .append(mapaCopia!!.id.toInt()).append("\t").append(packet).append("\n")
            }
            GestorSalida.ENVIAR_GE_PANEL_RESULTADOS_PELEA(this, 7, packet)
            GestorSalida.ENVIAR_fL_LISTA_PELEAS_AL_MAPA(mapaReal!!)
            pararIAs()
            eliminarTimer()
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(
                "Exception mostrarResultados - Mapa: " + mapaCopia!!.id + " PeleaID: "
                        + id + ", Exception: " + e.toString()
            )
            e.printStackTrace()
        }

    }

    private fun getPanelResultados(equipoGanador: Int): String {
        try {
            // equipoGanador 3 = cancelar la pelea
            if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
                return ""
            }
            //			for (Luchador a :
            //					_equipo1.values()) {
            //				if (a.getMob() != null){
            //					a.getMob().getMobGradoModelo().reiniciar_stats();
            //				}
            //			}
            //			for (Luchador a :
            //					_equipo2.values()) {
            //				if (a.getMob() != null){
            //					a.getMob().getMobGradoModelo().reiniciar_stats();
            //				}
            //			}
            if (fase < Constantes.PELEA_FASE_COMBATE) {
                GestorSalida.ENVIAR_Gc_BORRAR_ESPADA_EN_MAPA(mapaReal!!, id.toInt())
            }
            fase = Constantes.PELEA_FASE_FINALIZADO
            mapaReal!!.borrarPelea(id)
            val tiempo = System.currentTimeMillis() - _tiempoCombate
            val initID = _idLuchInit1
            var tipoX: Byte = 0
            when (_tipo) {
                Constantes.PELEA_TIPO_PRISMA -> {
                    if (AtlantaMain.OBJETOS_PELEA_PRISMA.isNotEmpty()) {
                        val equipo = if (equipoGanador == 1) _equipo1 else _equipo2
                        for (luchador in equipo.values) {
                            val p = luchador.personaje ?: continue
                            for (s in AtlantaMain.OBJETOS_PELEA_PRISMA.split(";")) {
                                try {
                                    val ss = s.split(",")
                                    val idModelo = ss[0].toInt()
                                    val cant = ss[1].toInt()
                                    val condicion = ss[2]
                                    if (!Condiciones.validaCondiciones(p, condicion)) continue // No cumplio condicion
                                    val om = Mundo.getObjetoModelo(idModelo) ?: continue // No existe el modelo
                                    val obj =
                                        om.crearObjeto(cant, Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM)
                                    luchador.addDropLuchador(obj, true)
                                } catch (e: Exception) {
                                }
                            }
                        }
                    }
                }
                Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_KOLISEO -> tipoX = 1
                Constantes.PELEA_TIPO_RECAUDADOR -> if (equipoGanador == 1) {
                    _kamasRobadas += _luchInit2!!.recaudador!!.kamas
                    _expRobada += _luchInit2!!.recaudador!!.exp
                    if (_objetosRobados == null) {
                        _objetosRobados = ArrayList()
                    }
                    _objetosRobados!!.addAll(_luchInit2!!.recaudador!!.objetos)
                    _luchInit2!!.recaudador!!.clearObjetos()
                }
                Constantes.PELEA_TIPO_PVM -> {
                    if (mobGrupo != null) {
                        mobGrupo!!.setPelea(null)
                        if (equipoGanador == 1) {
                            mobGrupo!!.bonusEstrellas = AtlantaMain.INICIO_BONUS_ESTRELLAS_MOBS
                            mobGrupo!!.setMuerto(true)
                            if (mobGrupo!!.esHeroico()) {
                                for (id in mobGrupo!!.objetosHeroico) {
                                    val obj = Mundo.getObjeto(id) ?: continue
                                    addObjetosRobados(obj)
                                }
                                _kamasRobadas += mobGrupo!!.kamasHeroico
                                mobGrupo!!.borrarObjetosHeroico()
                                mobGrupo!!.kamasHeroico = 0
                                mapaReal!!.salvarMapaHeroico()
                            }
                        }
                    }
                    if (retos != null) {
                        if (equipoGanador == 1) {
                            for ((retoID, reto) in retos!!) {
                                var exitoReto = reto.estado
                                if (exitoReto != EstReto.EN_ESPERA) {
                                    continue
                                }
                                when (retoID) {
                                    Constantes.RETO_SUPERVIVIENTE // superviviente
                                    -> for (luchador in _inicioLuchEquipo1) {
                                        if (luchador.estaMuerto()) {
                                            exitoReto = EstReto.FALLADO
                                            break
                                        }
                                    }
                                    Constantes.RETO_REPARTO// reparto
                                        , Constantes.RETO_CADA_UNO_CON_SU_MONSTRUO // cada uno con su mousntro
                                    -> for (luchador in _inicioLuchEquipo1) {
                                        if (luchador.mobsAsesinadosReto.isNotEmpty()) {
                                            exitoReto = EstReto.FALLADO
                                            break
                                        }
                                    }
                                }
                                if (exitoReto == EstReto.EN_ESPERA) {
                                    exitoReto = EstReto.REALIZADO
                                }
                                reto.estado = exitoReto
                            }
                        } else if (equipoGanador == 2) {
                            for ((_, reto) in retos!!) {
                                val exitoReto = reto.estado
                                if (exitoReto != EstReto.EN_ESPERA) {
                                    continue
                                }
                                reto.estado = EstReto.FALLADO
                            }
                        }
                    }
                }
                Constantes.PELEA_TIPO_PVM_NO_ESPADA -> if (retos != null) {
                    if (equipoGanador == 1) {
                        for ((retoID, reto) in retos!!) {
                            var exitoReto = reto.estado
                            if (exitoReto != EstReto.EN_ESPERA) {
                                continue
                            }
                            when (retoID) {
                                Constantes.RETO_SUPERVIVIENTE -> for (luchador in _inicioLuchEquipo1) {
                                    if (luchador.estaMuerto()) {
                                        exitoReto = EstReto.FALLADO
                                        break
                                    }
                                }
                                Constantes.RETO_REPARTO, Constantes.RETO_CADA_UNO_CON_SU_MONSTRUO -> for (luchador in _inicioLuchEquipo1) {
                                    if (luchador.mobsAsesinadosReto.isNotEmpty()) {
                                        exitoReto = EstReto.FALLADO
                                        break
                                    }
                                }
                            }
                            if (exitoReto == EstReto.EN_ESPERA) {
                                exitoReto = EstReto.REALIZADO
                            }
                            reto.estado = exitoReto
                        }
                    } else if (equipoGanador == 2) {
                        for ((_, reto) in retos!!) {
                            val exitoReto = reto.estado
                            if (exitoReto != EstReto.EN_ESPERA) {
                                continue
                            }
                            reto.estado = EstReto.FALLADO
                        }
                    }
                }
            }
            for (luch in _espectadores.values) {
                if (luch.estaRetirado()) {
                    continue
                }
                luchadorSalirPelea(luch)
            }
            val packet = StringBuilder("GE")
            packet.append(tiempo).append(";").append(_bonusEstrellas).append("|").append(initID).append("|")
                .append(tipoX.toInt()).append("|")
            if (equipoGanador == 3) {
                // cancelar la pelea
                val cancelados = ArrayList<Luchador>()
                cancelados.addAll(_equipo1.values)
                cancelados.addAll(_equipo2.values)
                for (luch in cancelados) {
                    val pjGanador = luch.personaje
                    if (luch.estaRetirado()) {
                        continue
                    }
                    // salen todos porq se cancelo
                    luchadorSalirPelea(luch)
                    if (luch.esInvocacion()) {
                        continue
                    }
                    if (_tipo != Constantes.PELEA_TIPO_DESAFIO) {
                        pjGanador?.setPdv(max(1, luch.pdvSinBuff), false)
                    }
                    if (tipoX.toInt() == 0) {// PVM -> SIN HONOR
                        packet.append("2;").append(luch.id).append(";").append(luch.nombre).append(";")
                            .append(luch.nivel).append(";").append(
                                if (luch
                                        .estaMuerto()
                                )
                                    "1"
                                else
                                    "0"
                            ).append(";")
                        packet.append(luch.xpStringLuch(";")).append(";")
                        packet.append(if (luch.expGanada == 0L) "" else luch.expGanada).append(";")
                        packet.append("" + ";")
                        packet.append("" + ";")
                        packet.append("" + ";")
                        packet.append(if (luch.kamasGanadas == 0L) "" else luch.kamasGanadas).append("|")
                    } else { // PVP -> CON HONOR
                        packet.append("2;").append(luch.id).append(";").append(luch.nombre).append(";")
                            .append(luch.nivel)
                        packet.append(";").append(if (luch.estaMuerto()) 1 else 0).append(";")
                        packet.append(stringHonor(luch)).append(";")
                        packet.append(0.toString() + ";")
                        packet.append(luch.nivelAlineacion).append(";")
                        packet.append(luch.preLuchador?.Deshonor).append(";")
                        packet.append(0.toString() + ";")
                        packet.append("" + ";")
                        packet.append(luch.kamasGanadas).append(";")
                        packet.append(luch.xpStringLuch(";")).append(";")
                        packet.append(luch.expGanada).append("|")
                    }
                }
                return packet.toString()
            }
            // si la pelea no se cancela
            val ganadores = ArrayList<Luchador>()
            val perdedores = ArrayList<Luchador>()
            if (equipoGanador == 1) {
                ganadores.addAll(_equipo1.values)
                perdedores.addAll(_equipo2.values)
            } else if (equipoGanador == 2) {
                ganadores.addAll(_equipo2.values)
                perdedores.addAll(_equipo1.values)
            }
            mapaReal!!.aplicarAccionFinPelea(_tipo.toInt(), ganadores, _acciones)
            for (luch in ganadores) {
                if (luch.estaRetirado() || luch.esInvocacion()) {
                    continue
                }
                val perso = luch.personaje
                if (perso != null) {
                    if (_asesinos.isNotEmpty()) {
                        _asesinos.append("~")
                    }
                    _asesinos.append(perso.nombre)
                } else if (luch.mob != null && !luch.esInvocacion()) {
                    if (_asesinos.isNotEmpty()) {
                        _asesinos.append("~")
                    }
                    _asesinos.append(luch.mob!!.idModelo)
                }
                if (perso == null) {
                    continue
                }
                when (_tipo) {
                    Constantes.PELEA_TIPO_KOLISEO -> if (perso != null) {
                        val rank = Mundo.getRankingKoliseo(perso.Id)
                        rank?.aumentarVictoria()
                        if (perso.grupoKoliseo != null) {
                            perso.grupoKoliseo.dejarGrupo(perso)
                        }
                    }
                    Constantes.PELEA_TIPO_PVP -> {
                        if (perso != null) {
                            val rank = Mundo.getRankingPVP(perso.Id)
                            rank?.aumentarVictoria()
                        }
                        recompensaMision(luch, true)
                    }
                    Constantes.PELEA_TIPO_PVM -> if (perso != null) {
                        val balance = Mundo.getBalanceMundo(perso)
                        val bonusExp = Mundo.getBonusAlinExp(perso)
                        luch.bonusAlinExp = balance * bonusExp
                        val bonusDrop = Mundo.getBonusAlinDrop(perso)
                        luch.bonusAlinDrop = balance * bonusDrop
                        if (AtlantaMain.PARAM_BESTIARIO) {
                            for (luchPerdedor in perdedores) {
                                if (luchPerdedor.esInvocacion() && luchPerdedor.mob == null) {
                                    continue
                                }
                                perso.addCardMob(luchPerdedor.mob!!.idModelo)
                            }
                        }
                    }
                    Constantes.PELEA_TIPO_PRISMA -> GestorSalida.ENVIAR_CP_INFO_DEFENSORES_PRISMA(
                        perso,
                        _listadefensores
                    )
                    Constantes.PELEA_TIPO_RECAUDADOR -> GestorSalida.ENVIAR_gITP_INFO_DEFENSORES_RECAUDADOR(
                        perso,
                        _listadefensores
                    )
                }
                if (_tipo != Constantes.PELEA_TIPO_DESAFIO) {
                    perso.setPdv(max(1, luch.pdvSinBuff), false)
                }
            }
            for (luch in perdedores) {
                if (luch.estaRetirado() || luch.esInvocacion()) {
                    continue
                }
                robarPersonajePerdedor(luch)
                val perso = luch.personaje ?: continue
                when (_tipo) {
                    Constantes.PELEA_TIPO_KOLISEO -> if (perso != null) {
                        val rank = Mundo.getRankingKoliseo(perso.Id)
                        rank?.aumentarDerrota()
                        if (perso.grupoKoliseo != null) {
                            perso.grupoKoliseo.dejarGrupo(perso)
                        }
                    }
                    Constantes.PELEA_TIPO_PVP -> {
                        if (perso != null) {
                            val rank = Mundo.getRankingPVP(perso.Id)
                            rank?.aumentarDerrota()
                        }
                        recompensaMision(luch, false)
                    }
                    Constantes.PELEA_TIPO_PRISMA -> GestorSalida.ENVIAR_CP_INFO_DEFENSORES_PRISMA(
                        perso,
                        _listadefensores
                    )
                    Constantes.PELEA_TIPO_RECAUDADOR -> GestorSalida.ENVIAR_gITP_INFO_DEFENSORES_RECAUDADOR(
                        perso,
                        _listadefensores
                    )
                }
            }
            var minkamas: Long = 0
            var maxkamas: Long = 0
            var coefEstrellas = 0f
            var coefRetoDrop = 0f
            var coefRetoXP = 0f
            if (AtlantaMain.PARAM_PERMITIR_BONUS_ESTRELLAS && _bonusEstrellas > 0) {
                coefEstrellas = _bonusEstrellas / 100f
            }
            var lucConMaxPP: Luchador? = null
            var dropRobado: ArrayList<Objeto>? = null
            var mobCapturable = true
            var monturaSalvaje = false
            // piedras
            //
            for (luchGanador in ganadores) {
                if (luchGanador.esDoble()) {
                    continue
                }
                if (luchGanador.esInvocacion()) {
                    if (luchGanador.mob != null && luchGanador.mob!!.idModelo != 285) {// cofre
                        continue
                    }
                }
                var prospeccionLuchador = luchGanador.totalStats.getTotalStatConComplemento(
                    Constantes.STAT_MAS_PROSPECCION
                ) + luchGanador.totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_PORC_PP)
                var coefPPLuchador = 1f
                val pjGanador = luchGanador.personaje
                if (pjGanador != null) {
                    if (_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) {
                        val mascObj = pjGanador.getObjPosicion(Constantes.OBJETO_POS_MASCOTA)
                        var comio = false
                        if (mascObj != null && mascObj.esDevoradorAlmas()) {
                            for ((key, value) in mobGrupo!!.almasMobs) {
                                try {
                                    if (Mundo.getMascotaModelo(mascObj.objModeloID)!!.getComida(key) != null) {
                                        comio = true
                                        if (value != null) {
                                            mascObj.comerAlma(key, value)
                                        }
                                    }
                                } catch (ignored: Exception) {
                                }

                            }
                            if (comio) {
                                GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(pjGanador, mascObj)
                            }
                        }
                        val tt = intArrayOf(
                            MisionObjetivoModelo.VENCER_AL_MOB.toInt(),
                            MisionObjetivoModelo.VENCER_MOBS_UN_COMBATE.toInt()
                        )
                        pjGanador.verificarMisionesTipo(tt, mobGrupo!!.almasMobs, false, 0)
                    }
                    if (pjGanador.realizoMisionDelDia()) {
                        val almanax = Mundo.almanaxDelDia
                        if (almanax != null && almanax.tipo == Constantes.ALMANAX_BONUS_DROP) {
                            coefPPLuchador += almanax.bonus / 100f
                        }
                    }
                    if (pjGanador.alasActivadas() && _alinPelea == pjGanador.alineacion) {
                        coefPPLuchador += luchGanador.bonusAlinDrop / 100f
                    }
                    if (AtlantaMain.RATE_DROP_ABONADOS > 1) {
                        if (pjGanador.esAbonado()) {
                            coefPPLuchador += AtlantaMain.RATE_DROP_ABONADOS / 2f
                        }
                    }
                }
                if (AtlantaMain.PARAM_PERMITIR_BONUS_PELEA_AFECTEN_PROSPECCION) {
                    coefPPLuchador += coefEstrellas + coefRetoDrop
                }
                prospeccionLuchador *= coefPPLuchador.toInt()
                luchGanador.setProspeccion(prospeccionLuchador)
                prospeccionEquipo += prospeccionLuchador
                // luchGanador.setPorcAdicDrop((int) (prospeccionLuchador * coefDropBonus));
            }
            if (prospeccionEquipo < 1) {
                prospeccionEquipo = 1
            }
            if (equipoGanador == 1) {
                if (_tipo == Constantes.PELEA_TIPO_PVM) {
                    for (luchPerdedor in perdedores) {
                        try {
                            if (luchPerdedor.mob == null) {
                                mobCapturable = false
                                break
                            }
                            val mobModelo = luchPerdedor.mob!!.mobModelo
                            if (mobModelo.id == 171 || mobModelo.id == 200 || mobModelo.id == 666) {
                                monturaSalvaje = true
                            }
                            mobCapturable = mobCapturable and mobModelo.esCapturable()
                        } catch (e: Exception) {
                            mobCapturable = false
                        }

                    }
                    if ((monturaSalvaje || mobCapturable) && !mapaReal!!.esArena()) {
                        var maxNivel = 0
                        val piedraStats = StringBuilder()
                        var monturaID = 0
                        for (luchGanador in ganadores) {
                            if (luchGanador.tieneEstado(Constantes.ESTADO_CAPT_ALMAS.toInt())) {
                                if (_capturadores == null) {
                                    _capturadores = ArrayList(8)
                                }
                                _capturadores!!.add(luchGanador)
                            }
                            if (luchGanador.tieneEstado(Constantes.ESTADO_DOMESTICACIÓN.toInt())) {
                                if (_domesticadores == null) {
                                    _domesticadores = ArrayList(8)
                                }
                                _domesticadores!!.add(luchGanador)
                            }
                        }
                        if (_capturadores == null || _capturadores!!.isEmpty()) {
                            mobCapturable = false
                        }
                        if (_domesticadores == null || _domesticadores!!.isEmpty()) {
                            monturaSalvaje = false
                        }
                        var objPiedraModID = 7010
                        if (monturaSalvaje || mobCapturable) {
                            for (luchPerdedor in perdedores) {
                                try {
                                    val mob = luchPerdedor.mob
                                    if (luchPerdedor.mob == null) {
                                        continue
                                    }
                                    val m = luchPerdedor.mob!!.idModelo
                                    if (monturaSalvaje) {
                                        if (m == 171 || m == 200 || m == 666) {
                                            if (monturaID == 0 || Formulas.randomBoolean) {
                                                monturaID = m
                                            }
                                        }
                                    }
                                    if (mobCapturable) {
                                        if (mob!!.mobModelo.tipoMob.toInt() == Constantes.MOB_TIPO_LOS_ARCHIMONSTRUOS) {
                                            if (objPiedraModID == 7010) {
                                                objPiedraModID = 10418
                                            }
                                        }
                                        if (mob.mobModelo.id == 423) {
                                            objPiedraModID = 9720
                                        }
                                        if (piedraStats.isNotEmpty()) {
                                            piedraStats.append(",")
                                        }
                                        piedraStats.append(Integer.toHexString(Constantes.STAT_INVOCA_MOB)).append("#")
                                            .append(Integer.toHexString(luchPerdedor.nivel)).append("#0#")
                                            .append(Integer.toHexString(m))
                                        if (luchPerdedor.nivel > maxNivel) {
                                            maxNivel = luchPerdedor.nivel
                                        }
                                    }
                                } catch (ignored: Exception) {
                                }

                            }
                        }
                        if (monturaSalvaje) {
                            for (luchCapt in _domesticadores!!) {
                                try {
                                    val persoCapt = luchCapt.personaje
                                    val redCapt = persoCapt!!.getObjPosicion(Constantes.OBJETO_POS_ARMA)
                                    if (redCapt != null && redCapt.objModelo?.tipo?.toInt() == Constantes.OBJETO_TIPO_RED_CAPTURA
                                        && persoCapt.montura == null
                                    ) {
                                        val suerteCaptura =
                                            luchCapt.getValorPorBuffsID(751) + AtlantaMain.RATE_CAPTURA_MONTURA * redCapt.getStatValor(
                                                Constantes.STAT_DOMESTICAR_MONTURA
                                            )
                                        if (Formulas.getRandomInt(1, 100) <= suerteCaptura) {
                                            persoCapt.borrarOEliminarConOR(redCapt.id, false)
                                            val color = Constantes.getColorMonturaPorMob(monturaID)
                                            val montura = Montura(color, luchCapt.id, false, true)
                                            val pergamino = montura.objModCertificado!!.crearObjeto(
                                                1,
                                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM
                                            )
                                            if (AtlantaMain.PARAM_CAPTURAR_MONTURA_COMO_PERGAMINO) {
                                                pergamino.fijarStatValor(
                                                    Constantes.STAT_CONSULTAR_MONTURA,
                                                    abs(montura.id)
                                                )
                                                pergamino.addStatTexto(
                                                    Constantes.STAT_PERTENECE_A,
                                                    "0#0#0#" + luchCapt.nombre
                                                )
                                                pergamino.addStatTexto(
                                                    Constantes.STAT_NOMBRE,
                                                    "0#0#0#" + montura.nombre
                                                )
                                                montura.setMapaCelda(null, null)
                                            } else {
                                                persoCapt.montura = montura
                                            }
                                            luchCapt.addDropLuchador(
                                                pergamino,
                                                AtlantaMain.PARAM_CAPTURAR_MONTURA_COMO_PERGAMINO
                                            )
                                            break
                                        }
                                    }
                                } catch (ignored: Exception) {
                                }

                            }
                        }
                        if (mobCapturable) {
                            for (luchCapt in _capturadores!!) {
                                try {// falta agregar al azar
                                    val persoCapt = luchCapt.personaje
                                    val piedra = persoCapt!!.getObjPosicion(Constantes.OBJETO_POS_ARMA)
                                    if (piedra != null && piedra.objModelo?.tipo?.toInt() == Constantes.OBJETO_TIPO_PIEDRA_DEL_ALMA) {
                                        val nivelPiedra = Integer.parseInt(
                                            piedra.getParamStatTexto(
                                                Constantes.STAT_POTENCIA_CAPTURA_ALMA,
                                                3
                                            ), 16
                                        )
                                        if (nivelPiedra >= maxNivel) {
                                            val sPiedra = Integer.parseInt(
                                                piedra.getParamStatTexto(Constantes.STAT_POTENCIA_CAPTURA_ALMA, 1),
                                                16
                                            )
                                            val suerte =
                                                luchCapt.getValorPorBuffsID(Constantes.STAT_BONUS_CAPTURA_ALMA) + sPiedra
                                            if (suerte >= Formulas.getRandomInt(1, 100)) {
                                                persoCapt.borrarOEliminarConOR(piedra.id, false)
                                                luchCapt.addDropLuchador(
                                                    Objeto(
                                                        0,
                                                        objPiedraModID,
                                                        1,
                                                        Constantes.OBJETO_POS_NO_EQUIPADO,
                                                        piedraStats.toString(),
                                                        0,
                                                        0,
                                                        true
                                                    ), true
                                                )
                                                break
                                            }
                                        }
                                    }
                                } catch (ignored: Exception) {
                                }

                            }
                        }
                    }
                }
                if ((_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) && retos != null) {
                    for ((_, reto) in retos!!) {
                        if (reto.estado == EstReto.REALIZADO) {
                            if (AtlantaMain.PARAM_PERMITIR_BONUS_DROP_RETOS) {
                                coefRetoDrop += reto.bonusDrop() / 100f
                            }
                            if (AtlantaMain.PARAM_PERMITIR_BONUS_EXP_RETOS) {
                                coefRetoXP += reto.bonusXP() / 100f
                            }
                        }
                    }
                }
                try {
                    var nivelPromMobs = 0
                    if (_tipo == Constantes.PELEA_TIPO_PVM || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA) {
                        var mob: MobGrado?
                        var cant = 0
                        for (luchPerdedor in perdedores) {
                            mob = luchPerdedor.mob
                            if (luchPerdedor.esInvocacion() || mob == null) {
                                continue
                            }
                            if (AtlantaMain.PARAM_SISTEMA_ORBES) {
                                if (AtlantaMain.MOBS_NO_ORBES.contains(mob.idModelo)) {
                                    continue
                                }
                                var cantidad = 0
                                val nivel = mob.Nivel
                                cantidad += if (nivel in 1..50) {
                                    floor(nivel * 0.1).toInt()
                                } else if (nivel in 51..100) {
                                    floor(nivel * 0.13).toInt()
                                } else if (nivel in 101..130) {
                                    floor(nivel * 0.16).toInt()
                                } else if (nivel in 131..150) {
                                    floor(nivel * 0.19).toInt()
                                } else if (nivel in 151..200) {
                                    floor(nivel * 0.21).toInt()
                                } else {
                                    floor(nivel * 0.25).toInt()
                                }
                                if (AtlantaMain.MOBS_DOBLE_ORBES.contains(mob.idModelo)) {
                                    cantidad *= 2
                                }
                                val drop = DropMob(AtlantaMain.ID_ORBE, 0, 99.99f, cantidad, "")
                                addDropPelea(drop)
                            } else {
                                // drops de recursos, objetos, etc normales
                                cant++
                                nivelPromMobs += mob.Nivel
                                minkamas += mob.mobGradoModelo.minKamas.toLong()
                                maxkamas += mob.mobGradoModelo.maxKamas.toLong()
                                for (drop in mob.mobModelo.drops) {
                                    if (drop.prospeccion == 0 || drop.prospeccion <= prospeccionEquipo) {
                                        addDropPelea(drop)
                                    }
                                }
                            }
                        }
                        if (cant > 0) {
                            nivelPromMobs /= cant
                        }
                    }
                    if (_tipo == Constantes.PELEA_TIPO_PVM) {// drops fijos
                        for (drop in Mundo.listaDropsFijos()) {
                            // armas etereas, materias, dominios
                            if (drop.nivelMin <= nivelPromMobs && nivelPromMobs <= drop.nivelMax) {
                                addDropPelea(drop)
                            }
                        }
                    }
                } catch (ignored: Exception) {
                }

                // hasta aqui acaba todo lo q tiene q ver con ganadores
            }
            val todosConPP = TreeMap<Int, Luchador>()
            var prospTemp: Int
            var tempPP: Int
            val dropeadores = ArrayList<Luchador>()
            val ordenLuchMasAMenosPP = ArrayList<Luchador>()
            for (luchGanador in ganadores) {
                prospTemp = luchGanador.prospeccionLuchador
                while (todosConPP.containsKey(prospTemp)) {
                    prospTemp += 1
                }
                todosConPP[prospTemp] = luchGanador
            }
            while (ordenLuchMasAMenosPP.size < ganadores.size) {
                tempPP = -1
                for ((key, value) in todosConPP) {
                    if (key > tempPP && !ordenLuchMasAMenosPP.contains(value)) {
                        lucConMaxPP = value
                        tempPP = key
                    }
                }
                if (lucConMaxPP != null) {
                    ordenLuchMasAMenosPP.add(lucConMaxPP)
                }
            }
            if (_objetosRobados != null) {
                for (obj in _objetosRobados!!) {
                    if (dropRobado == null) {
                        dropRobado = ArrayList()
                    }
                    if (obj.cantidad > 1) {
                        for (i in 1..obj.cantidad) {
                            dropRobado.add(obj.clonarObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO))
                        }
                    } else {
                        dropRobado.add(obj)
                    }
                }
                _objetosRobados!!.clear()
            }
            // solo para pjs o mobs en heroico
            for (luch in ordenLuchMasAMenosPP) {
                if (luch.esDoble()) {
                    continue
                }
                if (luch.personaje != null && luch.personaje!!.esMultiman()) {
                    continue
                }
                if (luch.esInvocacion()) {
                    if (luch.mob == null || luch.mob!!.idModelo != 285) {// cofre
                        continue
                    }
                }
                if (luch.mob != null) {
                    if (luch.mob!!.idModelo == 394) {// caballero
                        continue
                    }
                }
                dropeadores.add(luch)
            }
            var ganarHonor = 0
            val deshonor = 0
            var xpParaGremio: Long = 0
            var xpParaMontura: Long = 0
            val cantGanadores = dropeadores.size
            var strDrops = StringBuilder()
            // DROPEANDO LOS OBJETOS DE SERVER HEROICO O RECAUDADOR
            // if (AtlantaMain.MODO_HEROICO ||
            // AtlantaMain.MAPAS_MODO_HEROICO.contains(_mapaReal.getID())) {
            repartirDropRobado(dropRobado, dropeadores)
            // }
            var tempCantDropeadores = dropeadores.size
            val recaudador = mapaReal!!.recaudador
            if (equipoGanador == 1) {
                if (_posiblesBotinPelea != null) {
                    if (AtlantaMain.MODO_DEBUG) {
                        println("========== START DROPS PLAYERS ===========")
                        println("PROSPECCION DEL EQUIPO ES $prospeccionEquipo")
                    }
                    for (drop in _posiblesBotinPelea!!) {
                        if (AtlantaMain.MODO_DEBUG) {
                            println("===========================================")
                            println(
                                "Posibilidad Drop (" + drop.idObjModelo + ") " + Mundo.getObjetoModelo(
                                    drop
                                        .idObjModelo
                                )!!.nombre + " , MaximoDrop: " + drop.botinMaximo + " , DropFijo: " + drop
                                    .esDropFijo()
                            )
                        }
                        var maxDrop = drop.botinMaximo
                        if (maxDrop <= 0) {
                            continue
                        }
                        var repartido = false
                        if (drop.prospeccionBotin == 0) {
                            // para q el drop se si o si
                            repartido = true
                            if (drop.porcentajeBotin >= 100) {
                                // cada jugador dropea la maxima cantidad
                                maxDrop *= dropeadores.size
                            }
                        }
                        var nuevoMaxDrop = maxDrop
                        if (drop.prospeccionBotin > 0) {
                            nuevoMaxDrop = 0
                            for (m in 1..maxDrop) {
                                val fSuerte = Formulas.getRandomDecimal(3)// 0.001 - 100.000
                                // si es drop etero o no
                                val fPorc = Formulas.getPorcParaDropAlEquipo(
                                    prospeccionEquipo, coefEstrellas, coefRetoDrop, drop,
                                    dropeadores.size
                                )
                                if (AtlantaMain.MODO_DEBUG) {
                                    println(
                                        " -> DropItem: " + drop.idObjModelo + " , %Drop: " + drop.porcentajeBotin
                                                + " , %TeamDrop: " + fPorc + " , RandValue: " + fSuerte
                                    )
                                }
                                if (fPorc >= fSuerte) {
                                    nuevoMaxDrop++
                                }
                            }
                        }
                        if (AtlantaMain.MODO_DEBUG) {
                            println(
                                "Repartiendo drop " + Mundo.getObjetoModelo(drop.idObjModelo)!!.nombre + " ("
                                        + drop.idObjModelo + ") cantidad " + nuevoMaxDrop
                            )
                        }
                        var dropsGanados = 0
                        while (dropsGanados < nuevoMaxDrop) {
                            if (drop.condicionBotin.isNotEmpty() || repartido) {
                                var gano = false
                                var pasoCondicion = false
                                for (j in 1..dropeadores.size) {
                                    val k = (tempCantDropeadores + j) % dropeadores.size
                                    val posibleDropeador = dropeadores[k]
                                    if (posibleDropeador.personaje == null || !Condiciones.validaCondiciones(
                                            posibleDropeador
                                                .personaje, drop.condicionBotin
                                        )
                                    ) {
                                        // si no es personaje y si no cumple la condicion
                                        if (drop.prospeccionBotin == 0 && drop.porcentajeBotin >= 100) {
                                            dropsGanados++
                                        }
                                        continue
                                    }
                                    pasoCondicion = true
                                    val nPorcAzar = Formulas.getRandomDecimal(3)
                                    val porcDropFinal =
                                        Formulas.getPorcDropLuchador(drop.porcentajeBotin, posibleDropeador)
                                    if (porcDropFinal >= nPorcAzar) {
                                        Mundo.getObjetoModelo(drop.idObjModelo)?.crearObjeto(
                                            1,
                                            Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM
                                        )?.let {
                                            posibleDropeador.addDropLuchador(
                                                it, true
                                            )
                                        }
                                        tempCantDropeadores = k
                                        gano = true
                                        break
                                    }
                                }
                                if (!pasoCondicion) {
                                    break
                                }
                                if (gano) {
                                    dropsGanados++
                                }
                            } else {// si es etereo o no tiene condicion
                                val nPorcAzar = Formulas.getRandomInt(1, prospeccionEquipo)
                                var suma = 0
                                var dropeador: Luchador? = null
                                for (l in dropeadores) {
                                    suma += l.prospeccionLuchador
                                    if (suma >= nPorcAzar) {
                                        dropeador = l
                                        break
                                    }
                                }
                                if (dropeador != null) {
                                    Mundo.getObjetoModelo(drop.idObjModelo)?.crearObjeto(
                                        1,
                                        Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM
                                    )?.let {
                                        dropeador.addDropLuchador(
                                            it, true
                                        )
                                    }
                                    dropsGanados++
                                }
                            }
                        }
                        drop.addBotinMaximo(-nuevoMaxDrop)
                    }
                    if (AtlantaMain.MODO_DEBUG) {
                        println("========== FINISH DROPS PLAYERS ===========")
                    }
                }
                if (_tipo == Constantes.PELEA_TIPO_PVM && recaudador != null) {
                    strDrops = StringBuilder()
                    val luchRecau = Luchador(this, recaudador, true)
                    val gremio = recaudador.gremio
                    val ppRecau = gremio!!.getStatRecolecta(Constantes.STAT_MAS_PROSPECCION)
                    val expGanada = Formulas.getXPOficial(
                        ganadores, perdedores, luchRecau, coefEstrellas, coefRetoXP,
                        equipoGanador == 1
                    )
                    val kamasGanadas = Formulas.getKamasGanadas(minkamas, maxkamas, null)
                    luchRecau.kamasGanadas = kamasGanadas
                    luchRecau.expGanada = expGanada
                    recaudador.addExp(expGanada)
                    recaudador.addKamas(kamasGanadas, null)
                    // SI AUN PUEDE DROPEAR
                    if (gremio.getStatRecolecta(Constantes.STAT_MAS_PODS) > recaudador.podsActuales) {
                        if (_posiblesBotinPelea != null) {
                            if (AtlantaMain.MODO_DEBUG) {
                                println("========== START DROPS RECAUDADOR ===========")
                                println("PROSPECCION RECAUDADOR: $ppRecau")
                            }
                            for (drop in _posiblesBotinPelea!!) {
                                val maxDrop = drop.botinMaximo
                                if (maxDrop <= 0) {
                                    continue
                                }
                                if (drop.condicionBotin.isNotEmpty()) {
                                    continue
                                }
                                if (drop.prospeccionBotin > 0) {
                                    for (m in 1..maxDrop) {
                                        val fSuerte = Formulas.getRandomDecimal(3)
                                        val fPorc = Formulas.getPorcParaDropAlEquipo(
                                            ppRecau,
                                            coefEstrellas,
                                            coefRetoDrop,
                                            drop,
                                            1
                                        )
                                        if (AtlantaMain.MODO_DEBUG) {
                                            println(
                                                "DropItem: " + drop.idObjModelo + " , %Drop: " + drop.porcentajeBotin
                                                        + " , %TeamDrop: " + fPorc + " , RandValue: " + fSuerte
                                            )
                                        }
                                        if (fPorc >= fSuerte) {
                                            val objModelo = Mundo.getObjetoModelo(drop.idObjModelo)
                                            if (objModelo != null) {
                                                luchRecau.addDropLuchador(
                                                    objModelo.crearObjeto(
                                                        1, Constantes.OBJETO_POS_NO_EQUIPADO,
                                                        CAPACIDAD_STATS.RANDOM
                                                    ), true
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            if (AtlantaMain.MODO_DEBUG) {
                                println("========== FIN DROPS RECAUDADOR ===========")
                            }
                        }
                        if (luchRecau.objDropeados != null) {
                            for ((obj, value) in luchRecau.objDropeados!!) {
                                if (strDrops.isNotEmpty()) {
                                    strDrops.append(",")
                                }
                                strDrops.append(obj.objModeloID).append("~").append(obj.cantidad)
                                if (value) {
                                    luchRecau.addObjetoAInventario(obj)
                                }
                            }
                        }
                    }
                    packet.append("5;").append(luchRecau.id).append(";").append(luchRecau.nombre).append(";")
                        .append(luchRecau.nivel).append(";").append(if (luchRecau.estaMuerto()) "1" else "0")
                        .append(";")
                    packet.append(luchRecau.xpStringLuch(";")).append(";")
                    packet.append(if (luchRecau.expGanada == 0L) "" else luchRecau.expGanada).append(";")
                    packet.append(if (xpParaGremio == 0L) "" else xpParaGremio).append(";")
                    packet.append(if (xpParaMontura == 0L) "" else xpParaMontura).append(";")
                    packet.append(strDrops.toString()).append(";")
                    packet.append(if (luchRecau.kamasGanadas == 0L) "" else luchRecau.kamasGanadas).append("|")
                }
            }
            var cuentas_g: StringBuilder? = null
            var personajes_g: StringBuilder? = null
            var ips_g: StringBuilder? = null
            var puntos_g: StringBuilder? = null
            var cuentas_p: StringBuilder? = null
            var personajes_p: StringBuilder? = null
            var ips_p: StringBuilder? = null
            var puntos_p: StringBuilder? = null
            if (AtlantaMain.PARAM_SALVAR_LOGS_AGRESION_SQL && _tipo == Constantes.PELEA_TIPO_PVP) {
                cuentas_g = StringBuilder()
                personajes_g = StringBuilder()
                ips_g = StringBuilder()
                puntos_g = StringBuilder()
                cuentas_p = StringBuilder()
                personajes_p = StringBuilder()
                ips_p = StringBuilder()
                puntos_p = StringBuilder()
            }
            for (luchGanador in ganadores) {
                if (luchGanador.esDoble()) {
                    continue
                }
                if (luchGanador.esInvocacion()) {
                    if (luchGanador.mob != null && luchGanador.mob!!.idModelo != 285) {// cofre
                        continue
                    }
                }
                strDrops = StringBuilder()
                val pjGanador = luchGanador.personaje
                ganarHonor = 0
                xpParaMontura = ganarHonor.toLong()
                xpParaGremio = xpParaMontura
                if (pjGanador != null && pjGanador.esMultiman()) {
                    // multiman no reciven ningun bonus
                } else if (!luchGanador.estaRetirado()) {
                    when (_tipo) {
                        Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_PRISMA -> {
                            ganarHonor = Formulas.getHonorGanado(ganadores, perdedores, luchGanador, mobGrupo != null)
                            if (_1vs1) {
                                var div = 0
                                if (luchGanador.id == _luchInit1!!.id) {
                                    // mientras mas se agrade menos honor se ganar
                                    try {
                                        for (t in luchGanador.personaje!!.getAgredirA(_luchInit2!!.nombre)!!) {
                                            if (t + 1000L * 60 * 60 * 24 > System.currentTimeMillis()) {
                                                div++
                                            }
                                        }
                                    } catch (ignored: Exception) {
                                    }

                                } else if (luchGanador.id == _luchInit2!!.id) {
                                    try {
                                        for (t in luchGanador.personaje!!.getAgredidoPor(_luchInit1!!.nombre)!!) {
                                            if (t + 1000L * 60 * 60 * 24 > System.currentTimeMillis()) {
                                                div++
                                            }
                                        }
                                    } catch (ignored: Exception) {
                                    }

                                }
                                if (div < 1) {
                                    div = 1
                                }
                                ganarHonor /= div
                            }
                            if (ganarHonor > 0) {
                                luchGanador.preLuchador?.addDeshonor(-1)
                                luchGanador.preLuchador?.addHonor(ganarHonor)
                            } else if (ganarHonor < 0) {
                                ganarHonor = 0
                            }
                        }
                        Constantes.PELEA_TIPO_PVM -> {
                            if (pjGanador != null) {
                                val arma = pjGanador.getObjPosicion(Constantes.OBJETO_POS_ARMA)
                                val oficio = pjGanador.getStatOficioPorID(Constantes.OFICIO_CAZADOR)
                                if (arma != null && oficio != null && oficio.oficio.esHerramientaValida(arma.objModeloID)) {
                                    val nivelOficio = oficio.nivel
                                    for (mob in perdedores) {
                                        try {
                                            if (mob.esInvocacion() || mob.mob == null)
                                                continue
                                            val carne = Constantes.getCarnePorMob(mob.mob!!.idModelo, nivelOficio)
                                            if (carne != -1) {
                                                val cant = Formulas.getRandomInt(
                                                    1,
                                                    max(1, luchGanador.prospeccionLuchador / 100)
                                                )
                                                Mundo.getObjetoModelo(carne)?.crearObjeto(
                                                        cant,
                                                        Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM
                                                    )
                                                    ?.let {
                                                        luchGanador.addDropLuchador(
                                                            it, true
                                                        )
                                                    }
                                            }
                                        } catch (ignored: Exception) {
                                        }

                                    }
                                }
                            }
                            luchGanador.kamasGanadas = luchGanador.kamasGanadas + Formulas.getKamasGanadas(
                                minkamas, maxkamas,
                                luchGanador.personaje
                            )
                            if (pjGanador != null) {
                                val expGanada = Formulas.getXPOficial(
                                    ganadores, perdedores, luchGanador, coefEstrellas, coefRetoXP,
                                    equipoGanador == 1
                                )
                                luchGanador.expGanada = luchGanador.expGanada + expGanada
                                if (pjGanador.realizoMisionDelDia()) {
                                    val almanax = Mundo.almanaxDelDia
                                    if (almanax != null) {
                                        if (almanax.tipo == Constantes.ALMANAX_BONUS_KAMAS) {
                                            luchGanador.kamasGanadas =
                                                luchGanador.kamasGanadas + luchGanador.kamasGanadas * almanax.bonus / 100
                                        }
                                        if (almanax.tipo == Constantes.ALMANAX_BONUS_EXP_PJ) {
                                            val expBonus = luchGanador.expGanada * almanax.bonus / 100
                                            luchGanador.expGanada = luchGanador.expGanada + expBonus
                                        }
                                    }
                                }
                                if (pjGanador.alasActivadas() && _alinPelea == pjGanador.alineacion) {
                                    val expBonus = (luchGanador.expGanada * luchGanador.bonusAlinExp / 100).toLong()
                                    luchGanador.expGanada = luchGanador.expGanada + expBonus
                                    val kamasBonus =
                                        (luchGanador.kamasGanadas * luchGanador.bonusAlinDrop / 100).toLong()
                                    luchGanador.kamasGanadas = luchGanador.kamasGanadas + kamasBonus
                                }
                                if (pjGanador.miembroGremio != null) {
                                    xpParaGremio =
                                        (luchGanador.expGanada * pjGanador.miembroGremio.porcXpDonada / 100f).toLong()
                                    luchGanador.expGanada = luchGanador.expGanada - xpParaGremio
                                    if (xpParaGremio > 0) {
                                        xpParaGremio = Formulas.getXPDonada(
                                            pjGanador.nivel, pjGanador.miembroGremio.gremio
                                                .nivel.toInt(), xpParaGremio
                                        )
                                        pjGanador.miembroGremio.darXpAGremio(xpParaGremio)
                                    }
                                }
                                if (pjGanador.montura != null) {
                                    xpParaMontura = luchGanador.expGanada * pjGanador.porcXPMontura / 100
                                    //									xpParaMontura = xpParaMontura * pjGanador.getMontura().velocidadAprendizaje() / 100;
                                    luchGanador.expGanada = luchGanador.expGanada - xpParaMontura
                                    if (xpParaMontura > 0) {
                                        xpParaMontura = Formulas.getXPDonada(
                                            pjGanador.nivel, pjGanador.montura.nivel,
                                            xpParaMontura
                                        ) * AtlantaMain.RATE_XP_MONTURA
                                        pjGanador.montura.addExperiencia(xpParaMontura)
                                    }
                                    GestorSalida.ENVIAR_Re_DETALLES_MONTURA(pjGanador, "+", pjGanador.montura)
                                }
                            }
                        }
                        // no lleva break porq continua la formula
                        Constantes.PELEA_TIPO_PVM_NO_ESPADA -> {
                            luchGanador.kamasGanadas = luchGanador.kamasGanadas + Formulas.getKamasGanadas(
                                minkamas,
                                maxkamas,
                                luchGanador.personaje
                            )
                            if (pjGanador != null) {
                                val expGanada = Formulas.getXPOficial(
                                    ganadores,
                                    perdedores,
                                    luchGanador,
                                    coefEstrellas,
                                    coefRetoXP,
                                    equipoGanador == 1
                                )
                                luchGanador.expGanada = luchGanador.expGanada + expGanada
                                if (pjGanador.realizoMisionDelDia()) {
                                    val almanax = Mundo.almanaxDelDia
                                    if (almanax != null) {
                                        if (almanax.tipo == Constantes.ALMANAX_BONUS_KAMAS) {
                                            luchGanador.kamasGanadas =
                                                luchGanador.kamasGanadas + luchGanador.kamasGanadas * almanax.bonus / 100
                                        }
                                        if (almanax.tipo == Constantes.ALMANAX_BONUS_EXP_PJ) {
                                            val expBonus = luchGanador.expGanada * almanax.bonus / 100
                                            luchGanador.expGanada = luchGanador.expGanada + expBonus
                                        }
                                    }
                                }
                                if (pjGanador.alasActivadas() && _alinPelea == pjGanador.alineacion) {
                                    val expBonus = (luchGanador.expGanada * luchGanador.bonusAlinExp / 100).toLong()
                                    luchGanador.expGanada = luchGanador.expGanada + expBonus
                                    val kamasBonus =
                                        (luchGanador.kamasGanadas * luchGanador.bonusAlinDrop / 100).toLong()
                                    luchGanador.kamasGanadas = luchGanador.kamasGanadas + kamasBonus
                                }
                                if (pjGanador.miembroGremio != null) {
                                    xpParaGremio =
                                        (luchGanador.expGanada * pjGanador.miembroGremio.porcXpDonada / 100f).toLong()
                                    luchGanador.expGanada = luchGanador.expGanada - xpParaGremio
                                    if (xpParaGremio > 0) {
                                        xpParaGremio = Formulas.getXPDonada(
                                            pjGanador.nivel,
                                            pjGanador.miembroGremio.gremio.nivel.toInt(),
                                            xpParaGremio
                                        )
                                        pjGanador.miembroGremio.darXpAGremio(xpParaGremio)
                                    }
                                }
                                if (pjGanador.montura != null) {
                                    xpParaMontura = luchGanador.expGanada * pjGanador.porcXPMontura / 100
                                    luchGanador.expGanada = luchGanador.expGanada - xpParaMontura
                                    if (xpParaMontura > 0) {
                                        xpParaMontura = Formulas.getXPDonada(
                                            pjGanador.nivel,
                                            pjGanador.montura.nivel,
                                            xpParaMontura
                                        ) * AtlantaMain.RATE_XP_MONTURA
                                        pjGanador.montura.addExperiencia(xpParaMontura)
                                    }
                                    GestorSalida.ENVIAR_Re_DETALLES_MONTURA(pjGanador, "+", pjGanador.montura)
                                }
                            }
                        }
                        Constantes.PELEA_TIPO_RECAUDADOR -> if (pjGanador != null) {
                            if (pjGanador.miembroGremio != null) {
                                xpParaGremio = luchGanador.expGanada
                                luchGanador.expGanada = 0
                                if (xpParaGremio > 0) {
                                    xpParaGremio = Formulas.getXPDonada(
                                        pjGanador.nivel, pjGanador.miembroGremio.gremio
                                            .nivel.toInt(), xpParaGremio
                                    )
                                    pjGanador.miembroGremio.darXpAGremio(xpParaGremio)
                                }
                            }
                        }
                        Constantes.PELEA_TIPO_KOLISEO -> if (pjGanador != null) {
                            luchGanador.expGanada =
                                luchGanador.expGanada + Formulas.getXPMision(pjGanador.nivel) / AtlantaMain.KOLISEO_DIVISOR_XP
                            luchGanador.personaje!!.setPdv(luchGanador.personaje!!.pdvMax, false)
                            if (AtlantaMain.KOLISEO_PREMIO_KAMAS > 0) {
                                luchGanador.kamasGanadas = luchGanador.kamasGanadas + Formulas.getKamasKoliseo(
                                    pjGanador
                                        .nivel
                                )
                            }
                            for (s in AtlantaMain.KOLISEO_PREMIO_OBJETOS.split(";".toRegex())
                                .dropLastWhile { it.isEmpty() }.toTypedArray()) {
                                try {
                                    val objID =
                                        Integer.parseInt(s.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                                            .toTypedArray()[0])
                                    val cant =
                                        Integer.parseInt(s.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                                            .toTypedArray()[1])
                                    Mundo.getObjetoModelo(objID)?.crearObjeto(
                                        cant,
                                        Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM
                                    )?.let {
                                        luchGanador.addDropLuchador(
                                            it, true
                                        )
                                    }
                                } catch (ignored: Exception) {
                                }

                            }
                        }
                        Constantes.PELEA_TIPO_CACERIA -> if (pjGanador != null) {
                            try {
                                val str = Mundo.KAMAS_OBJ_CACERIA.split(Pattern.quote("|").toRegex())
                                    .dropLastWhile { it.isEmpty() }.toTypedArray()
                                luchGanador.kamasGanadas =
                                    luchGanador.kamasGanadas + Integer.parseInt(str[0]) / cantGanadores
                                if (str.size > 1) {
                                    for (s in str[1].split(";".toRegex()).dropLastWhile { it.isEmpty() }
                                        .toTypedArray()) {
                                        try {
                                            val objID =
                                                Integer.parseInt(s.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                                                    .toTypedArray()[0])
                                            val cant =
                                                Integer.parseInt(s.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                                                    .toTypedArray()[1])
                                            Mundo.getObjetoModelo(objID)?.crearObjeto(
                                                    cant,
                                                    Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM
                                                )
                                                ?.let {
                                                    luchGanador.addDropLuchador(
                                                        it, true
                                                    )
                                                }
                                        } catch (ignored: Exception) {
                                        }

                                    }
                                }
                            } catch (ignored: Exception) {
                            }

                        }
                    }
                    luchGanador.addKamasLuchador()
                    // AQUI CONVIERTE LOS OBJ DROPS AL INVENTARIO DE CADA GANADOR
                    if (luchGanador.objDropeados != null) {
                        val logdropeo = logdropeo(luchGanador)
                        for ((obj, value) in luchGanador.objDropeados!!) {
                            if (strDrops.isNotEmpty()) {
                                strDrops.append(",")
                            }
                            strDrops.append(obj.objModeloID).append("~").append(obj.cantidad)
                            if (value) {
                                luchGanador.addObjetoAInventario(obj)
                                logdropeo.addlogdrop(obj)
                            }
                        }
                        logdropeo.imprimirlog()
                        var recibidor = pjGanador
                        if (luchGanador.esInvocacion()) {
                            recibidor = luchGanador.invocador?.personaje
                        }
                        if (recibidor != null) {
                            if (recibidor.enLinea()) {
                                val oako = StringBuilder()
                                for ((key, value) in recibidor.dropsPelea) {
                                    if (value) {
                                        oako.append(key.stringObjetoConGuiño())
                                    } else {
                                        GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(recibidor, key)
                                    }
                                }
                                if (oako.isNotEmpty()) {
                                    GestorSalida.ENVIAR_OAKO_APARECER_MUCHOS_OBJETOS(recibidor, oako.toString())
                                }
                                GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(recibidor)
                            }
                            recibidor.dropsPelea.clear()
                        }
                    }
                    if (pjGanador != null) {
                        if (esMapaHeroico()) {
                            if (pjGanador.nivel < pjGanador.ultimoNivel) {
                                luchGanador.expGanada = luchGanador.expGanada * 2
                            }
                        }
                        val ips = ArrayList<String>()
                        for (a in this.ordenLuchadores) {
                            if (a.esInvocacion() || a.ia != null || a.equipoBin != luchGanador.equipoBin) {
                                continue
                            } else if (a.estaRetirado() || a.recaudador != null || a.estaRetirado()) {
                                continue
                            } else if (a.personaje == null || a.esRecaudador()) {
                                continue
                            } else if (!ips.contains(a.personaje!!.cuenta.actualIP)) {
                                ips.add(a.personaje!!.cuenta.actualIP)
                            } else {
                                continue
                            }
                        }

                        if (ips.size > 1) {
                            luchGanador.expGanada = (luchGanador.expGanada * (1 + 0.15 * ips.size)).toInt().toLong()
                        }
                        if (luchGanador.personaje!!.detalleExp && ips.size > 1) {
                            luchGanador.personaje!!.enviarmensajeNegro("Se detectaron: " + ips.size + " ips diferentes en su equipo, se le ha otorgado un bonus de: " + ips.size * 15 + "% de exp extra")
                            luchGanador.personaje!!.enviarmensajeNegro("Xp Final extra por ips diferentes en pelea: " + luchGanador.expGanada)
                        }
                        pjGanador.addExperiencia(luchGanador.expGanada, tipoX.toInt() == 1)
                    }
                }
                packet.append("2;").append(luchGanador.id).append(";").append(luchGanador.nombre).append(";")
                    .append(luchGanador.nivel)
                packet.append(";").append(if (luchGanador.estaMuerto()) 1 else 0).append(";")
                if (tipoX.toInt() == 0) {// PVM -> SIN HONOR
                    packet.append(luchGanador.xpStringLuch(";")).append(";")
                    packet.append(if (luchGanador.expGanada == 0L) "" else luchGanador.expGanada).append(";")
                    packet.append(if (xpParaGremio == 0L) "" else xpParaGremio).append(";")
                    packet.append(if (xpParaMontura == 0L) "" else xpParaMontura).append(";")
                    packet.append(strDrops.toString()).append(";")
                    packet.append(if (luchGanador.kamasGanadas == 0L) "" else luchGanador.kamasGanadas).append("|")
                } else { // PVP -> CON HONOR
                    if (AtlantaMain.PARAM_SALVAR_LOGS_AGRESION_SQL && _tipo == Constantes.PELEA_TIPO_PVP) {
                        if (luchGanador.personaje != null && luchGanador.personaje!!.cuenta != null) {
                            if (cuentas_g!!.isNotEmpty()) {
                                cuentas_g.append(",")
                                personajes_g!!.append(",")
                                ips_g!!.append(",")
                                puntos_g!!.append(",")
                            }
                            cuentas_g.append(luchGanador.personaje!!.cuentaID)
                            personajes_g!!.append(luchGanador.id)
                            ips_g!!.append(luchGanador.personaje!!.cuenta.actualIP)
                            puntos_g!!.append(ganarHonor)
                        }
                    }
                    packet.append(stringHonor(luchGanador)).append(";")
                    packet.append(ganarHonor).append(";")
                    packet.append(luchGanador.nivelAlineacion).append(";")
                    packet.append(luchGanador.preLuchador?.Deshonor).append(";")
                    packet.append(deshonor).append(";")
                    packet.append(strDrops.toString()).append(";")
                    packet.append(luchGanador.kamasGanadas).append(";")
                    packet.append(luchGanador.xpStringLuch(";")).append(";")
                    packet.append(luchGanador.expGanada).append("|")
                }
            }
            // -----------
            // PERDODORES
            // -----------
            for (luchPerdedor in perdedores) {
                if (luchPerdedor.esDoble()) {
                    continue
                }
                if (luchPerdedor.esInvocacion()) {
                    if (luchPerdedor.mob != null && luchPerdedor.mob!!.idModelo != 285) {// cofre
                        continue
                    }
                }
                strDrops = StringBuilder()
                val pjPerdedor = luchPerdedor.personaje
                ganarHonor = 0
                xpParaMontura = ganarHonor.toLong()
                xpParaGremio = xpParaMontura
                if (pjPerdedor != null) {
                    if (pjPerdedor.esMultiman()) {
                        // multiman no reciven ningun bonus
                    } else if (!luchPerdedor.estaRetirado()) {
                        when (_tipo) {
                            Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_PRISMA -> {
                                ganarHonor =
                                    Formulas.getHonorGanado(ganadores, perdedores, luchPerdedor, mobGrupo != null)
                                if (_1vs1) {
                                    var div = 0
                                    if (luchPerdedor.id == _luchInit1!!.id) {
                                        try {
                                            for (t in luchPerdedor.personaje!!.getAgredirA(_luchInit2!!.nombre)!!) {
                                                if (t + 1000 * 60 * 60 * 24 > System.currentTimeMillis()) {
                                                    div++
                                                }
                                            }
                                        } catch (ignored: Exception) {
                                        }

                                    } else if (luchPerdedor.id == _luchInit2!!.id) {
                                        try {
                                            for (t in luchPerdedor.personaje!!.getAgredidoPor(_luchInit1!!.nombre)!!) {
                                                if (t + 1000 * 60 * 60 * 24 > System.currentTimeMillis()) {
                                                    div++
                                                }
                                            }
                                        } catch (ignored: Exception) {
                                        }

                                    }
                                    if (div < 1) {
                                        div = 1
                                    }
                                    ganarHonor /= div
                                }
                                if (ganarHonor < 0) {
                                    luchPerdedor.preLuchador?.addHonor(ganarHonor)
                                }
                            }
                            Constantes.PELEA_TIPO_PVM -> {
                                val expGanada = Formulas.getXPOficial(
                                    perdedores, ganadores, luchPerdedor, coefEstrellas, coefRetoXP,
                                    equipoGanador == 1
                                )
                                luchPerdedor.expGanada = expGanada
                            }
                        }
                        if (pjPerdedor.miembroGremio != null) {
                            xpParaGremio =
                                (luchPerdedor.expGanada * pjPerdedor.miembroGremio.porcXpDonada / 100f).toLong()
                            luchPerdedor.expGanada = luchPerdedor.expGanada - xpParaGremio
                            if (xpParaGremio > 0) {
                                xpParaGremio = Formulas.getXPDonada(
                                    pjPerdedor.nivel, pjPerdedor.miembroGremio.gremio
                                        .nivel.toInt(), xpParaGremio
                                )
                                pjPerdedor.miembroGremio.darXpAGremio(xpParaGremio)
                            }
                        }
                        if (pjPerdedor.montura != null) {
                            xpParaMontura = (luchPerdedor.expGanada * pjPerdedor.porcXPMontura / 100f).toLong()
                            //							xpParaMontura = xpParaMontura * pjPerdedor.getMontura().velocidadAprendizaje() / 100;
                            luchPerdedor.expGanada = luchPerdedor.expGanada - xpParaMontura
                            if (xpParaMontura > 0) {
                                xpParaMontura = Formulas.getXPDonada(
                                    pjPerdedor.nivel, pjPerdedor.montura.nivel,
                                    xpParaMontura
                                ) * AtlantaMain.RATE_XP_MONTURA
                                pjPerdedor.montura.addExperiencia(xpParaMontura)
                            }
                            GestorSalida.ENVIAR_Re_DETALLES_MONTURA(pjPerdedor, "+", pjPerdedor.montura)
                        }
                        if (esMapaHeroico()) {
                            if (pjPerdedor.nivel < pjPerdedor.ultimoNivel) {
                                luchPerdedor.expGanada = luchPerdedor.expGanada * 2
                            }
                        }
                        pjPerdedor.addExperiencia(luchPerdedor.expGanada, tipoX.toInt() == 1)
                        pjPerdedor.addKamas(luchPerdedor.kamasGanadas, false, false)
                    }
                }
                packet.append("0;").append(luchPerdedor.id).append(";").append(luchPerdedor.nombre).append(";")
                    .append(luchPerdedor.nivel)
                packet.append(";").append(if (luchPerdedor.estaMuerto()) 1 else 0).append(";")
                if (tipoX.toInt() == 0) {// PVM -> SIN HONOR
                    packet.append(luchPerdedor.xpStringLuch(";")).append(";")
                    packet.append(if (luchPerdedor.expGanada == 0L) "" else luchPerdedor.expGanada).append(";")
                    packet.append(if (xpParaGremio == 0L) "" else xpParaGremio).append(";")
                    packet.append(if (xpParaMontura == 0L) "" else xpParaMontura).append(";")
                    packet.append(strDrops.toString()).append(";")
                    packet.append(if (luchPerdedor.kamasGanadas == 0L) "" else luchPerdedor.kamasGanadas).append("|")
                } else {// PVP -> CON HONOR
                    if (AtlantaMain.PARAM_SALVAR_LOGS_AGRESION_SQL && _tipo == Constantes.PELEA_TIPO_PVP) {
                        if (luchPerdedor.personaje != null && luchPerdedor.personaje!!.cuenta != null) {
                            if (cuentas_p!!.isNotEmpty()) {
                                cuentas_p.append(",")
                                personajes_p!!.append(",")
                                ips_p!!.append(",")
                                puntos_p!!.append(",")
                            }
                            cuentas_p.append(luchPerdedor.personaje!!.cuentaID)
                            personajes_p!!.append(luchPerdedor.id)
                            ips_p!!.append(luchPerdedor.personaje!!.cuenta.actualIP)
                            puntos_p!!.append(ganarHonor)
                        }
                    }
                    packet.append(stringHonor(luchPerdedor)).append(";")
                    packet.append(ganarHonor).append(";")
                    packet.append(luchPerdedor.nivelAlineacion).append(";")
                    packet.append(luchPerdedor.preLuchador?.Deshonor).append(";")
                    packet.append(deshonor).append(";")
                    packet.append(strDrops.toString())
                    packet.append(";").append(luchPerdedor.kamasGanadas).append(";")
                    packet.append(luchPerdedor.xpStringLuch(";")).append(";")
                    packet.append(luchPerdedor.expGanada).append("|")
                }
            }
            if (AtlantaMain.PARAM_SALVAR_LOGS_AGRESION_SQL && _tipo == Constantes.PELEA_TIPO_PVP) {
                GestorSQL.INSERT_LOG_PELEA(
                    cuentas_g!!.toString(),
                    personajes_g!!.toString(),
                    ips_g!!.toString(),
                    puntos_g!!.toString(),
                    cuentas_p!!.toString(),
                    personajes_p!!.toString(),
                    ips_p!!.toString(),
                    puntos_p!!.toString(),
                    tiempo / 1000,
                    _luchInit1!!
                        .id,
                    _luchInit2!!.id,
                    mapaCopia!!.id
                )
            }
            if (_tipo == Constantes.PELEA_TIPO_PVM) {
                mobGrupo!!.puedeTimerReaparecer(mapaReal!!, mobGrupo, Aparecer.FINAL_PELEA)
            }
            if (equipoGanador == 1) {
                if (_tipo == Constantes.PELEA_TIPO_CACERIA) {
                    Mundo.NOMBRE_CACERIA = ""
                    Mundo.KAMAS_OBJ_CACERIA = ""
                    if (AtlantaMain.SEGUNDOS_REBOOT_SERVER > 0) {
                        Mundo.SEG_CUENTA_REGRESIVA = AtlantaMain.SEGUNDOS_REBOOT_SERVER.toLong()
                    }
                    Mundo.MSJ_CUENTA_REGRESIVA = AtlantaMain.MENSAJE_TIMER_REBOOT
                    GestorSalida.ENVIAR_bRS_PARAR_CUENTA_REGRESIVA_TODOS()
                }
            }
            for (luch in ganadores) {
                if (luch.estaRetirado() || luch.esInvocacion()) {
                    continue
                }
                luchadorSalirPelea(luch)
            }
            for (luch in perdedores) {
                if (luch.estaRetirado() || luch.esInvocacion()) {
                    continue
                }
                consecuenciasPerder(luch)
            }
            return packet.toString()
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(
                "Exception PanelResultados - Mapa: " + mapaCopia!!.id + " PeleaID: " + id
                        + " e -> " + e.toString()
            )
            e.printStackTrace()
            return "EXCEPTION"
        }

    }

    private fun repartirDropRobado(dropRobado: ArrayList<Objeto>?, dropeadores: ArrayList<Luchador>) {
        when (_tipo) {
            Constantes.PELEA_TIPO_DESAFIO, Constantes.PELEA_TIPO_KOLISEO -> {
            }
            else// diferentes a 0 y 6
            -> {
                // REPARTIR ITEMS ROBADOS A LOS JUGADORES
                if (dropRobado != null) {
                    for (obj in dropRobado) {
                        val nPorcAzar = Formulas.getRandomInt(1, prospeccionEquipo)
                        var suma = 0
                        var dropeador: Luchador? = null
                        for (l in dropeadores) {
                            suma += l.prospeccionLuchador
                            if (suma >= nPorcAzar) {
                                dropeador = l
                                break
                            }
                        }
                        dropeador?.addDropLuchador(obj, true)
                    }
                }
                for (luchGanador in dropeadores) {
                    // esto es solo para el grupo de mobs y otros
                    var fParte = luchGanador.prospeccionLuchador / prospeccionEquipo.toFloat()
                    fParte = min(1f, max(0f, fParte))
                    if (_kamasRobadas > 0) {
                        luchGanador.addKamasGanadas((_kamasRobadas * fParte).toLong())
                    }
                    if (_expRobada > 0) {
                        luchGanador.addXPGanada((_expRobada * fParte).toLong())
                    }
                }
            }
        }
    }

    private fun stringHonor(luchGanador: Luchador): String {
        return when (luchGanador.alineacion) {
            Constantes.ALINEACION_BONTARIANO, Constantes.ALINEACION_BRAKMARIANO, Constantes.ALINEACION_MERCENARIO -> {
                val nivelA = luchGanador.nivelAlineacion
                Mundo.getExpAlineacion(nivelA).toString() + ";" + luchGanador.preLuchador!!.Honor + ";" + Mundo
                    .getExpAlineacion(nivelA + 1)
            }
            else -> "0;0;0"
        }
    }

    fun addIA(IA: Inteligencia) {
        _IAs.add(IA)
    }
    fun removeIA(IA: Inteligencia){
        _IAs.remove(IA)
    }

    private fun pararIAs() {
        try {
            while (_IAs.isNotEmpty()) {
                try {
                    _IAs[0].parar()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addAccion(accion: Accion) {
        if (_acciones == null) {
            _acciones = ArrayList()
        }
        _acciones!!.add(accion)
    }

    fun getParamMiEquipo(id: Int): Byte {
        if (_equipo1.containsKey(id)) {
            return 1
        }
        if (_equipo2.containsKey(id)) {
            return 2
        }
        return if (_espectadores.containsKey(id)) {
            4
        } else -1
    }

    fun getParamEquipoEnemigo(id: Int): Byte {
        if (_equipo1.containsKey(id)) {
            return 2
        }
        return if (_equipo2.containsKey(id)) {
            1
        } else -1
    }

    // incluye espectadores
    fun getLuchadorPorID(id: Int): Luchador? {
        if (_equipo1[id] != null) {
            return _equipo1[id]
        }
        if (_equipo2[id] != null) {
            return _equipo2[id]
        }
        return if (_espectadores[id] != null) {
            _espectadores[id]
        } else null
    }

    fun esEspectador(id: Int): Boolean {
        return _espectadores.containsKey(id)
    }

    private fun luchadorSalirPelea(luch: Luchador?) {
        if (luch == null) {
            return
        }
        luch.totalStats.clearBuffStats()
        val perdedor = luch.personaje ?: return
        perdedor.salirPelea(false, false)
    }

    private fun esMapaHeroico(): Boolean {
        return AtlantaMain.MODO_HEROICO || AtlantaMain.MAPAS_MODO_HEROICO.contains(mapaReal!!.id)
    }

    private fun consecuenciasPerder(luch: Luchador?) {
        if (luch == null) {
            return
        }
        luch.totalStats.clearBuffStats()
        val pjPerdedor = luch.personaje ?: return
        if (esMapaHeroico()) {
            // switch (_tipo) {
            // // case CentroInfo.PELEA_TIPO_DESAFIO :
            // case Constantes.PELEA_TIPO_KOLISEO :
            // break;
            // default :
            // pjPerdedor.salirPelea(false, false);
            // robarPersonajePerdedor(luch);
            // break;
            // }
            pjPerdedor.salirPelea(false, false)
            robarPersonajePerdedor(luch)
        } else {
            when (_tipo) {
                Constantes.PELEA_TIPO_DESAFIO -> {
                }
                Constantes.PELEA_TIPO_PVP -> {
                    pjPerdedor.addEnergiaConIm(-10 * pjPerdedor.nivel, true)
                    pjPerdedor.setPdv(1, false)
                    imprimiAsesinos(luch)
                }
                Constantes.PELEA_TIPO_RECAUDADOR -> {
                    pjPerdedor.addEnergiaConIm(-3000, true)
                    pjPerdedor.setPdv(1, false)
                }
                Constantes.PELEA_TIPO_KOLISEO -> {
                    pjPerdedor.setPdv(pjPerdedor.pdvMax, false)
                    imprimiAsesinos(luch)
                }
                else -> {
                    pjPerdedor.addEnergiaConIm(-10 * pjPerdedor.nivel, true)
                    pjPerdedor.setPdv(1, false)
                    pjPerdedor.restarVidaMascota(null)
                }
            }
            pjPerdedor.salirPelea(_tipo != Constantes.PELEA_TIPO_DESAFIO, false)
        }
    }

    private fun imprimiAsesinos(luch: Luchador) {
        val pjPerdedor = luch.personaje
        if (pjPerdedor == null || !luch.msjMuerto) {
            return
        }
        if (esMapaHeroico()) {
            if (!AtlantaMain.PARAM_MENSAJE_ASESINOS_HEROICO) {
                return
            }
        } else {
            when (_tipo) {
                Constantes.PELEA_TIPO_KOLISEO -> if (!AtlantaMain.PARAM_MENSAJE_ASESINOS_KOLISEO) {
                    return
                }
                Constantes.PELEA_TIPO_PVP -> if (!AtlantaMain.PARAM_MENSAJE_ASESINOS_PVP) {
                    return
                }
                else -> return
            }
        }
        luch.msjMuerto = true
        if (_asesinos.toString().isEmpty()) {
            GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1776;" + pjPerdedor.nombre)
        } else {
            GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1777;" + pjPerdedor.nombre + "~" + _asesinos.toString())
        }
    }

    @Synchronized
    fun retirarsePelea(idRetirador: Int, idExpulsado: Int, obligado: Boolean) {
        val luchRetirador = getLuchadorPorID(idRetirador)
        val luchExpulsado = getLuchadorPorID(idExpulsado)
        if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE || luchRetirador == null) {
            return
        }
        _cantUltAfec = 1
        if (!esEspectador(idRetirador)) {
            when (fase) {
                Constantes.PELEA_FASE_COMBATE// empezo pelea
                -> {
                    val persoRetirador = luchRetirador.personaje
                    if (!obligado) {
                        // 5 segundos para q se pueda retirar
                        if (System.currentTimeMillis() - _tiempoCombate < 5000) {
                            GestorSalida.ENVIAR_BN_NADA(persoRetirador!!, "ESPERAR 5 SEG")
                            return
                        }
                    }
                    if (luchRetirador.estaRetirado()) {
                        _espectadores.remove(idRetirador)
                        luchadorSalirPelea(luchRetirador)
                        GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(persoRetirador!!)
                        return
                    }
                    if (persoRetirador!!.compañero != null && !persoRetirador.esMultiman()) {
                        val idMultiman = persoRetirador.compañero.Id
                        retirarsePelea(idMultiman, idMultiman, true)
                    }
                    if (addMuertosReturnFinalizo(luchRetirador, null)) {
                        // si se finalizo la pelea
                        return
                    } else {
                        luchRetirador.setEstaRetirado(true)
                        _listaMuertos.remove(luchRetirador)
                        consecuenciasPerder(luchRetirador)
                        if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA) {
                            luchRetirador.preLuchador?.addHonor(-500)
                        }
                        // GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(_mapaCopia, idRetirador);
                        if (!persoRetirador.esMultiman()) {
                            GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(persoRetirador)
                        }
                        if (_tipo == Constantes.PELEA_TIPO_KOLISEO) {
                            persoRetirador.setPenalizarKoliseo()
                        }
                    }
                }
                Constantes.PELEA_FASE_POSICION// pelea en estado posicion
                -> {
                    if (_tipo == Constantes.PELEA_TIPO_PVP && !AtlantaMain.PARAM_EXPULSAR_PREFASE_PVP) {
                        GestorSalida.ENVIAR_BN_NADA(luchRetirador.personaje!!, "NO SE PUEDE EXPULSAR PREFASE PVP")
                        return
                    }
                    if (!obligado) {
                        if (System.currentTimeMillis() - _tiempoCombate < 3000) {
                            GestorSalida.ENVIAR_BN_NADA(luchRetirador.personaje!!, "ESPERAR MINIMO 3 SEG")
                            return
                        }
                    }
                    var puedeExpulsar = false
                    if (idRetirador == _idLuchInit1 || idRetirador == _idLuchInit2) {
                        puedeExpulsar = true
                    }
                    var luchASalir: Luchador = luchRetirador
                    if (puedeExpulsar) {
                        if (luchExpulsado != null && luchExpulsado.id != luchRetirador.id) {
                            // si puede expulsar, y expulsa a otro jugador
                            if (luchExpulsado.equipoBin == luchRetirador.equipoBin) {
                                val persoExpulsado = luchExpulsado.personaje
                                if (persoExpulsado != null && persoExpulsado.compañero != null && !persoExpulsado.esMultiman()) {
                                    val idMultiman = persoExpulsado.compañero.Id
                                    retirarsePelea(idRetirador, idMultiman, true)
                                }
                                luchadorSalirPelea(luchExpulsado)
                                luchASalir = luchExpulsado
                            } else {
                                return
                            }
                        } else {
                            // si puede expulsar y se expulsa a si mismo
                            if (_tipo == Constantes.PELEA_TIPO_DESAFIO) {
                                for (luch in luchadoresDeEquipo(3)) {
                                    luchadorSalirPelea(luch)
                                    if (luch.personaje != null && !luch.personaje!!.esMultiman()) {
                                        GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(luch.personaje!!)
                                    }
                                }
                                fase = Constantes.PELEA_FASE_FINALIZADO
                                mapaReal!!.borrarPelea(id)
                                GestorSalida.ENVIAR_Gc_BORRAR_ESPADA_EN_MAPA(mapaReal!!, id.toInt())
                                GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS(mapaReal!!)
                                return
                            } else {
                                val persoExpulsado = luchRetirador.personaje
                                if (persoExpulsado != null && persoExpulsado.compañero != null && !persoExpulsado.esMultiman()) {
                                    val idMultiman = persoExpulsado.compañero.Id
                                    retirarsePelea(idRetirador, idMultiman, true)
                                }
                                consecuenciasPerder(luchRetirador)
                                if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA) {
                                    luchRetirador.preLuchador?.addHonor(-500)
                                }
                            }
                        }
                    } else {
                        if (luchExpulsado != null) {
                            GestorSalida.ENVIAR_BN_NADA(luchRetirador.personaje!!, "NO HAY LUCH A EXPULSAR")
                            return
                        }
                        val persoExpulsado = luchRetirador.personaje
                        if (_tipo == Constantes.PELEA_TIPO_DESAFIO) {
                            if (persoExpulsado != null && persoExpulsado.compañero != null && !persoExpulsado.esMultiman()) {
                                val idMultiman = persoExpulsado.compañero.Id
                                retirarsePelea(idRetirador, idMultiman, true)
                            }
                            luchadorSalirPelea(luchRetirador)
                        } else {
                            if (persoExpulsado != null && persoExpulsado.compañero != null && !persoExpulsado.esMultiman()) {
                                val idMultiman = persoExpulsado.compañero.Id
                                retirarsePelea(idRetirador, idMultiman, true)
                            }
                            consecuenciasPerder(luchRetirador)
                            if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA) {
                                luchRetirador.preLuchador?.addHonor(-500)
                            }
                        }
                    }
                    GestorSalida.ENVIAR_GM_BORRAR_LUCHADOR(this, luchASalir.id, 3)
                    GestorSalida.ENVIAR_Gt_BORRAR_NOMBRE_ESPADA(
                        mapaReal!!, if (_equipo1.containsKey(luchASalir.id))
                            _idLuchInit1
                        else
                            _idLuchInit2, luchASalir
                    )
                    if (_equipo1.containsKey(luchASalir.id)) {
                        luchASalir.celdaPelea?.removerLuchador(luchASalir)
                        _equipo1.remove(luchASalir.id)// en estado de posiciones
                    } else if (_equipo2.containsKey(luchASalir.id)) {
                        luchASalir.celdaPelea?.removerLuchador(luchASalir)
                        _equipo2.remove(luchASalir.id)
                    }
                    luchASalir.setEstaMuerto(true)
                    if (luchASalir.personaje != null && !luchASalir.personaje!!.esMultiman()) {
                        GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(luchASalir.personaje!!)
                        if (_tipo == Constantes.PELEA_TIPO_KOLISEO) {
                            luchASalir.personaje!!.setPenalizarKoliseo()
                        }
                    }
                    if (!acaboPelea(3.toByte()) && _tipo == Constantes.PELEA_TIPO_DESAFIO) {
                        verificaTodosListos()
                    }
                }
                else -> println(
                    "ERROR RETIRARSE, estado de combate: " + fase + " tipo de combate:" + _tipo
                            + " LuchadorExp:" + luchExpulsado + " LuchadorRet:" + luchRetirador + " mapaID: " + mapaCopia!!.id
                            + " peleaID: " + id
                )
            }
        } else {
            _espectadores.remove(luchRetirador.id)
            luchadorSalirPelea(luchRetirador)
            GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(luchRetirador.personaje!!)
        }
    }

    fun stringOrdenJugadores(): String {
        val packet = StringBuilder("GTL")
        for (luchador in _ordenLuchadores) {
            if (!luchador.estaMuerto()) {
                packet.append("|").append(luchador.id)
            }
        }
        return packet.toString()
    }

    fun addLuchadorEnEquipo(luchador: Luchador, equipo: Int) {
        if (equipo == 0) {
            _equipo1[luchador.id] = luchador
        } else if (equipo == 1) {
            _equipo2[luchador.id] = luchador
        }
    }

    fun strParaListaPelea(): String {
        if (fase == Constantes.PELEA_FASE_FINALIZADO) {
            mapaReal!!.borrarPelea(id)
            return ""
        }
        val infos = StringBuilder()
        infos.append(id.toInt()).append(";")
        infos.append(if (fase <= Constantes.PELEA_FASE_POSICION) "-1" else _tiempoCombate).append(";")
        var jugEquipo1 = 0
        var jugEquipo2 = 0
        for (l in _equipo1.values) {
            if (l == null || l.esInvocacion()) {
                continue
            }
            jugEquipo1++
        }
        for (l in _equipo2.values) {
            if (l == null || l.esInvocacion()) {
                continue
            }
            jugEquipo2++
        }
        if (jugEquipo1 == 0 || jugEquipo2 == 0) {
            acaboPelea(3.toByte())
            return ""
        }
        infos.append(_luchInit1!!.flag.toInt()).append(",")
        infos.append(
            (if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA)
                _luchInit1!!.alineacion
            else
                0).toInt()
        ).append(",")
        infos.append(jugEquipo1).append(";")
        infos.append(_luchInit2!!.flag.toInt()).append(",")
        infos.append(
            (if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA)
                _luchInit2!!.alineacion
            else
                0).toInt()
        ).append(",")
        infos.append(jugEquipo2).append(";")
        return infos.toString()
    }

    fun continuaPelea(): Boolean {
        var equipo1Vivo = false
        var equipo2Vivo = false
        for (luchador in _equipo1.values) {
            if (luchador.esInvocacion()) {
                continue
            }
            if (!luchador.estaMuerto()) {
                equipo1Vivo = true
                break
            }
        }
        for (luchador in _equipo2.values) {
            if (luchador.esInvocacion()) {
                continue
            }
            if (!luchador.estaMuerto()) {
                equipo2Vivo = true
                break
            }
        }
        return equipo1Vivo && equipo2Vivo
    }

    fun cuantosQuedanDelEquipo(id: Int): Int {
        var num = 0
        if (_equipo1.containsKey(id)) {
            for (luchador in _equipo1.values) {
                if (luchador.estaMuerto() || luchador.esInvocacion()) {
                    continue
                }
                num++
            }
        } else if (_equipo2.containsKey(id)) {
            for (luchador in _equipo2.values) {
                if (luchador.estaMuerto() || luchador.esInvocacion()) {
                    continue
                }
                num++
            }
        }
        return num
    }

    private fun mostrarEspada(): String {
        // final String packet = "Gc+" + idPelea + ";" + tipoPelea + "|" + id1 + ";" + celda1 + ";" +
        // flag1 + ";" + alin1
        // + "|" + id2 + ";" + celda2 + ";" + flag2 + ";" + alin2;
        val p = StringBuilder("Gc+")
        p.append(id.toInt()).append(";")
        if (_tipo == Constantes.PELEA_TIPO_CACERIA) {
            p.append(0)
        } else {
            p.append(_tipo.toInt())
        }
        p.append("|").append(_idLuchInit1).append(";").append(_celdaID1.toInt()).append(";")
            .append(_luchInit1!!.flag.toInt()).append(";")
        if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA) {
            p.append(_luchInit1!!.alineacion.toInt())
        } else {
            p.append(Constantes.ALINEACION_NULL.toInt())
        }
        p.append("|").append(_idLuchInit2).append(";").append(_celdaID2.toInt()).append(";")
            .append(_luchInit2!!.flag.toInt()).append(";")
        if (_tipo == Constantes.PELEA_TIPO_PVP || _tipo == Constantes.PELEA_TIPO_PRISMA) {
            p.append(_luchInit2!!.alineacion.toInt())
        } else {
            p.append(Constantes.ALINEACION_NULL.toInt())
        }
        return p.toString()
    }

    fun infoEspadaPelea(perso: Personaje) {
        try {
            if (fase != Constantes.PELEA_FASE_POSICION || _tipo == Constantes.PELEA_TIPO_PVM_NO_ESPADA)
                return
            GestorSalida.ENVIAR_Gc_MOSTRAR_ESPADA_A_JUGADOR(perso, mostrarEspada())
            var enviar = StringBuilder()
            for ((_, luchador) in _equipo1) {
                if (enviar.toString().isNotEmpty()) {
                    enviar.append("|+")
                }
                enviar.append(luchador.id).append(";").append(luchador.nombre).append(";").append(luchador.nivel)
            }
            GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(perso, _idLuchInit1, enviar.toString())
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(perso, if (_cerrado1) '+' else '-', 'A', _idLuchInit1)
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(perso, if (_ayuda1) '+' else '-', 'H', _idLuchInit1)
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(perso, if (_soloGrupo1) '+' else '-', 'P', _idLuchInit1)
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(perso, if (_sinEspectador) '+' else '-', 'S', _idLuchInit1)
            enviar = StringBuilder()
            for ((_, luchador) in _equipo2) {
                if (enviar.toString().isNotEmpty()) {
                    enviar.append("|+")
                }
                enviar.append(luchador.id).append(";").append(luchador.nombre).append(";").append(luchador.nivel)
            }
            GestorSalida.ENVIAR_Gt_AGREGAR_NOMBRE_ESPADA(perso, _idLuchInit2, enviar.toString())
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(perso, if (_cerrado2) '+' else '-', 'A', _idLuchInit2)
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(perso, if (_ayuda2) '+' else '-', 'H', _idLuchInit2)
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(perso, if (_soloGrupo2) '+' else '-', 'P', _idLuchInit2)
            GestorSalida.ENVIAR_Go_BOTON_ESPEC_AYUDA(perso, if (_sinEspectador) '+' else '-', 'S', _idLuchInit2)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun addBuffLuchadores(
        objetivos: ArrayList<Luchador>, efectoID: Int, valor: Int,
        turnosRestantes: Int, hechizoID: Int, args: String, lanzador: Luchador, tipo: TipoDaño,
        condicionHechizo: String
    ) {
        if (objetivos.isEmpty()) {
            return
        }
        val str = StringBuilder()
        for (luch in objetivos) {
            // aqui no envia el GIE, lo envia abajo
            if (!luch.addBuffConGIE(
                    efectoID, valor, turnosRestantes, hechizoID, args, lanzador, false, tipo,
                    condicionHechizo
                )._primero
            ) {
                continue
            }
            if (condicionHechizo.isNotEmpty()) {
                continue
            }
            if (str.isNotEmpty()) {
                str.append(",")
            }
            str.append(luch.id)
        }
        if (str.isNotEmpty()) {
            val gie = getStrParaGIE(efectoID, str.toString(), turnosRestantes, hechizoID, args)
            GestorSalida.ENVIAR_GIE_AGREGAR_BUFF_PELEA(this, 7, gie)
        }
    }

    companion object {
        var LOG_COMBATES = StringBuilder()

        private fun getStrParaGIE(
            efectoID: Int, objetivos: String, turnos: Int, hechizoID: Int,
            args: String
        ): String {
            // para varios
            val s =
                args.replace("-1".toRegex(), "null").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val mParam1 = s[0]
            val valMax = s[1]
            val mParam3 = s[2]
            val suerte = s[4]
            return (efectoID.toString() + ";" + objetivos + ";" + mParam1 + ";" + valMax + ";" + mParam3 + ";" + suerte + ";"
                    + turnos + ";" + hechizoID)
        }

        fun getStrParaGA998(
            efectoID: Int, objetivo: Int, turnos: Int, hechizoID: Int,
            args: String
        ): String {
            // para uno solo
            val s =
                args.replace("-1".toRegex(), "null").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val mParam1 = s[0]
            val valMax = s[1]
            val mParam3 = s[2]
            val suerte = s[4]
            return (objetivo.toString() + "," + efectoID + "," + mParam1 + "," + valMax + "," + mParam3 + "," + suerte + ","
                    + turnos + "," + hechizoID)
        }
    }
}
