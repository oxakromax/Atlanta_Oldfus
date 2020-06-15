package variables.mapa

import estaticos.*
import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Condiciones.validaCondiciones
import estaticos.Formulas.getRandomInt
import estaticos.Formulas.randomBoolean
import estaticos.GestorSalida.ENVIAR_BN_NADA
import estaticos.GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA
import estaticos.GestorSalida.ENVIAR_GDO_OBJETO_TIRAR_SUELO
import estaticos.GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA
import estaticos.GestorSalida.ENVIAR_GM_GRUPOMOB_A_MAPA
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.GestorSalida.ENVIAR_Ow_PODS_DEL_PJ
import estaticos.GestorSalida.ENVIAR_fC_CANTIDAD_DE_PELEAS
import estaticos.GestorSalida.enviar
import estaticos.GestorSalida.enviarEnCola
import estaticos.database.GestorSQL.CARGAR_TRIGGERS_POR_MAPA
import estaticos.database.GestorSQL.DELETE_MAPA_HEROICO
import estaticos.database.GestorSQL.DELETE_MOBS_FIX_MAPA
import estaticos.database.GestorSQL.REPLACE_MAPAS_HEROICO
import estaticos.database.GestorSQL.UPDATE_SET_MOBS_MAPA
import sprites.PreLuchador
import variables.gremio.Recaudador
import variables.mapa.interactivo.ObjetoInteractivo
import variables.mob.AparecerMobs
import variables.mob.AparecerMobs.Aparecer
import variables.mob.GrupoMob
import variables.mob.MobModelo.TipoGrupo
import variables.mob.MobPosible
import variables.npc.NPC
import variables.npc.NPCModelo
import variables.pelea.Luchador
import variables.pelea.Pelea
import variables.personaje.GrupoKoliseo
import variables.personaje.Personaje
import variables.zotros.Accion
import variables.zotros.Prisma
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.regex.Pattern
import kotlin.system.exitProcess

class Mapa {
    val id: Short
    val x: Short
    val y: Short
    val ancho: Byte
    val alto: Byte
    private val _celdas: MutableMap<Short, Celda> = TreeMap()
    val posTeamRojo1 = ArrayList<Short>()
    val posTeamAzul2 = ArrayList<Short>()
    val prePelea = java.lang.Boolean.TRUE!!
    private var _sigIDNpc: Byte = -51
    var muteado = false
    private var _capabilities: Short = 0
    private var _bgID: Short = 0
    private var _musicID: Short = 0
    private var _ambienteID: Short = 0
    var maxGrupoDeMobs: Byte = 5
    var maxMobsPorGrupo: Byte = 8
    var maxMercantes: Byte = 5
    private var _maxPeleas: Byte = 99
    private var _outDoor: Byte = 0
    var fecha: String
    var mapData = ""
        private set
    private var _celdasPelea = ""
    private var _peleas: MutableMap<Short, Pelea>? = null
    private var _npcs: MutableMap<Int, NPC>? = null
    private var _grupoMobsTotales: MutableMap<Int, GrupoMob>? = null
    private var _grupoMobsEnCola: CopyOnWriteArrayList<GrupoMob>? = null
    var arrayPersonajes: CopyOnWriteArrayList<Personaje>? = null
        private set
    private var _accionFinPelea: MutableMap<Int, ArrayList<Accion>>? = null
    private var _mercantes: MutableMap<Int, Personaje>? = null
    private var _mobPosibles: ArrayList<MobPosible>? = null
    var trabajos: ArrayList<Int>? = null
        private set
    var objetosInteractivos: ArrayList<ObjetoInteractivo>? = null
        private set
    var colorCeldasAtacante = ""
    var subArea: SubArea? = null
        private set
    var cercado: Cercado? = null
    var prisma: Prisma? = null
    var recaudador: Recaudador? = null
    var minNivelGrupoMob = 0
        private set
    var maxNivelGrupoMob = 0
        private set

    constructor(
        id: Short, fecha: String, ancho: Byte, alto: Byte, posDePelea: String,
        mapData: String, key: String, mobs: String, X: Short, Y: Short, subArea: Short,
        maxGrupoDeMobs: Byte, maxMobsPorGrupo: Byte, maxMercantes: Byte, parametros: Short,
        maxPeleas: Byte, bgID: Short, musicID: Short, ambienteID: Short, outDoor: Byte, minNivelGrupoMob: Int,
        maxNivelGrupoMob: Int
    ) {
        var mapData = mapData
        mapaNormal()
        this.id = id
        this.fecha = fecha
        this.ancho = ancho
        this.alto = alto
        x = X
        y = Y
        if (AtlantaMain.MODO_DEBUG) {
            println("  --> Descifrando MapData ID " + this.id + " con key " + key)
        }
        if (key.trim { it <= ' ' }.isNotEmpty()) {
            mapData = Encriptador.decifrarMapData(key, mapData)
        }
        this.mapData = mapData
        _bgID = bgID
        _musicID = musicID
        _ambienteID = ambienteID
        _outDoor = outDoor
        _capabilities = parametros
        this.maxGrupoDeMobs = maxGrupoDeMobs
        this.maxMobsPorGrupo = maxMobsPorGrupo
        this.maxMercantes = maxMercantes
        _maxPeleas = maxPeleas
        this.minNivelGrupoMob = minNivelGrupoMob
        this.maxNivelGrupoMob = maxNivelGrupoMob
        try {
            this.subArea = Mundo.getSubArea(subArea.toInt())
            Mundo.getSubArea(subArea.toInt())?.addMapa(this)
        } catch (e: Exception) {
            redactarLogServidorln("Error al cargar el mapa ID $id subAreaID $subArea no existe")
            exitProcess(1)
            return
        }
        if (AtlantaMain.MODO_DEBUG) {
            println("  --> Decompilando MapData ID " + this.id)
        }
        Encriptador.decompilarMapaData(this)
        trabajos!!.trimToSize()
        if (AtlantaMain.MODO_DEBUG) {
            println("  --> MobPosibles en mapaID " + this.id + " mobs " + mobs)
        }
        mobPosibles(mobs)
        if (AtlantaMain.MODO_DEBUG) {
            println("  --> Agregando Mobs mapaID " + this.id)
        }
        if (AtlantaMain.PARAM_PERMITIR_MOBS) {
            agregarMobsInicioServer()
        }
        decodificarPosPelea(posDePelea)
        // corregirPosPelea();
        if (colorCeldasAtacante.isEmpty() && (esMazmorra() || esArena())) {
            colorCeldasAtacante = "red"
        }
        if (colorCeldasAtacante.isEmpty()) {
            colorCeldasAtacante = AtlantaMain.COLOR_CELDAS_PELEA_AGRESOR
        }
        convertCeldasPelea
    }

    constructor(mapa: Mapa, posDePelea: String) {
        id = mapa.id
        fecha = mapa.fecha
        ancho = mapa.ancho
        alto = mapa.alto
        x = mapa.x
        y = mapa.y
        for (newCelda in mapa._celdas.values) {
            val celda = Celda(
                this, newCelda.id, newCelda.activo, newCelda.movimiento, newCelda
                    .level, newCelda.slope, newCelda.lineaDeVista(), -1
            )
            _celdas[newCelda.id] = celda
            celda.celdaPelea()
        }
        decodificarPosPelea(posDePelea)
        convertCeldasPelea
    }

    private fun agregarMobsInicioServer() {
        val s1 = Mundo.getMapaEstrellas(id)
        val s2 = Mundo.getMapaHeroico(id)
        if (_mobPosibles!!.isNotEmpty()) {
            for (i in 0 until maxGrupoDeMobs) {
                try {
                    var estrellas: Short = -1
                    var heroico = ""
                    if (s1 != null && s1.isNotEmpty()) {
                        estrellas = s1[0]
                    }
                    if (s2 != null && s2.isNotEmpty()) {
                        heroico = s2[0]
                    }
                    if (AtlantaMain.MODO_DEBUG) {
                        println(
                            "  --> Agregando grupoMob mapaID: " + id + ", estrellas: " + estrellas + ", heroico: "
                                    + heroico
                        )
                    }
                    val grupoMob = getGrupoMobInicioServer(heroico, estrellas.toInt()) ?: break // neutral
                    if (s1 != null && s1.isNotEmpty()) {
                        s1.removeAt(0)
                    }
                    if (s2 != null && s2.isNotEmpty()) {
                        s2.removeAt(0)
                    }
                } catch (ignored: Exception) {
                }
            }
        }
        if (s2 != null) {
            for (s in s2) {
                try {
                    if (s.isEmpty()) {
                        continue
                    }
                    val strMob = s.split(Pattern.quote("|").toRegex()).toTypedArray()[0]
                    val grupoMob = GrupoMob(this, (-1).toShort(), strMob, TipoGrupo.HASTA_QUE_MUERA, "")
                    if (grupoMob.mobs.isEmpty()) {
                        continue
                    }
                    grupoMob.addObjetosKamasInicioServer(s)
                    addUltimoGrupoMob(grupoMob)
                } catch (ignored: Exception) {
                }
            }
        }
    }

    fun esNivelGrupoMobPermitido(nivel: Int): Boolean {
        var min = 0
        var max = 0
        if (subArea!!.minNivelGrupoMob > 0) {
            min = subArea!!.minNivelGrupoMob
        }
        if (subArea!!.maxNivelGrupoMob > 0) {
            max = subArea!!.maxNivelGrupoMob
        }
        if (minNivelGrupoMob > 0) {
            min = minNivelGrupoMob
        }
        if (maxNivelGrupoMob > 0) {
            max = maxNivelGrupoMob
        }
        return if (min == 0 && max == 0) {
            true
        } else nivel in min..max
    }

    private fun mapaNormal() {
        _peleas = HashMap()
        _npcs = HashMap()
        _grupoMobsTotales = HashMap()
        // _grupoMobsFix = new HashMap<Integer, GrupoMob>();
        _grupoMobsEnCola = CopyOnWriteArrayList()
        arrayPersonajes = CopyOnWriteArrayList()
        _accionFinPelea = HashMap()
        _mercantes = TreeMap()
        _mobPosibles = ArrayList()
        trabajos = ArrayList()
        objetosInteractivos = ArrayList()
    }

    fun copiarMapa(posPelea: String): Mapa {
        return Mapa(this, posPelea)
    }

    fun setStrCeldasPelea(posDePelea: String) {
        _celdasPelea = posDePelea
        colorCeldasAtacante = ""
        decodificarPosPelea(_celdasPelea)
    }

    fun decodificarPosPelea(posDePelea: String) {
        try {
            posTeamRojo1.clear()
            posTeamAzul2.clear()
            // _colorCeldaAtacante = "";
            val str = posDePelea.split(Pattern.quote("|").toRegex()).toTypedArray()
            if (str.isNotEmpty() && str[0].isNotEmpty()) {
                Encriptador.analizarCeldasDeInicio(str[0], posTeamRojo1)
            }
            if (str.size > 1 && str[1].isNotEmpty()) {
                Encriptador.analizarCeldasDeInicio(str[1], posTeamAzul2)
            }
            if (str.size > 2 && str[2].isNotEmpty()) {
                colorCeldasAtacante = str[2]
            }
        } catch (ignored: Exception) {
        }
    }

    // private void corregirPosPelea() {
// short temp = -1;
// for (int i = 0; i < _posPeleaRojo1.size(); i++) {
// short celdaRoja = _posPeleaRojo1.get(i);
// if (temp == -1) {
// temp = celdaRoja;
// continue;
// }
// Duo<Integer, ArrayList<Celda>> path = Camino.getPathPelea(this, temp, celdaRoja, -1, null,
// true);
// if (path == null || path._segundo.isEmpty()) {
// _posPeleaRojo1.remove(i);
// i--;
// continue;
// }
// if (path._segundo.get(path._segundo.size() - 1).getID() != celdaRoja) {
// _posPeleaRojo1.remove(i);
// i--;
// continue;
// }
// }
// for (int i = 0; i < _posPeleaAzul2.size(); i++) {
// short celdaAzul = _posPeleaAzul2.get(i);
// if (temp == -1) {
// temp = celdaAzul;
// continue;
// }
// Duo<Integer, ArrayList<Celda>> path = Camino.getPathPelea(this, temp, celdaAzul, -1, null,
// true);
// if (path == null || path._segundo.isEmpty()) {
// _posPeleaAzul2.remove(i);
// i--;
// continue;
// }
// if (path._segundo.get(path._segundo.size() - 1).getID() != celdaAzul) {
// _posPeleaAzul2.remove(i);
// i--;
// continue;
// }
// }
// }
    fun addCeldaPelea(equipo: Int, celdaID: Short) {
        posTeamRojo1.remove(celdaID as Any)
        posTeamAzul2.remove(celdaID as Any)
        if (equipo == 1) {
            posTeamRojo1.add(celdaID)
        } else if (equipo == 2) {
            posTeamAzul2.add(celdaID)
        }
    }

    fun borrarCeldasPelea(celdaID: Short) {
        posTeamRojo1.remove(celdaID as Any)
        posTeamAzul2.remove(celdaID as Any)
    }

    val convertCeldasPelea: String
        get() {
            var vacio = true
            val str = StringBuilder()
            for (s in posTeamRojo1) {
                vacio = false
                str.append(Encriptador.celdaIDAHash(s))
            }
            str.append("|")
            for (s in posTeamAzul2) {
                vacio = false
                str.append(Encriptador.celdaIDAHash(s))
            }
            _celdasPelea = str.toString()
            if (!vacio && colorCeldasAtacante.isNotEmpty()) {
                vacio = false
                str.append("|")
                str.append(colorCeldasAtacante)
            }
            return str.toString()
        }

    private fun prepararCeldasPelea(cant1: Int, cant2: Int): Boolean {
        var cant1 = cant1
        cant1 = if (posTeamAzul2.isEmpty() && posTeamRojo1.isEmpty()) 8 else cant1
        when (aptoParaPelea(cant1, cant2)) {
            0 -> return false
            1 -> getPosicionesAleatorias(cant1, 0)
            2 -> getPosicionesAleatorias(0, cant2)
            -1 -> getPosicionesAleatorias(8, 8)
        }
        return aptoParaPelea(cant1, cant2) != 0
    }

    private fun aptoParaPelea(cant1: Int, cant2: Int): Int {
        when (colorCeldasAtacante) {
            "red" -> {
                if (posTeamRojo1.size < cant1) {
                    return if (posTeamAzul2.size < cant2) {
                        -1
                    } else {
                        1
                    }
                }
                if (posTeamAzul2.size < cant2) {
                    return 2
                }
            }
            "blue" -> {
                if (posTeamAzul2.size < cant1) {
                    return if (posTeamRojo1.size < cant2) {
                        -1
                    } else {
                        2
                    }
                }
                if (posTeamRojo1.size < cant2) {
                    return 1
                }
            }
            else -> {
                if (posTeamAzul2.size < cant1 || posTeamRojo1.size < cant1) {
                    return -1
                }
                if (posTeamRojo1.size < cant2 || posTeamAzul2.size < cant2) {
                    return -1
                }
            }
        }
        return 0
    }

    private fun getPosicionesAleatorias(cant1: Int, cant2: Int) {
        val celdaLibres = ArrayList<Short>()
        for ((key1, value) in _celdas) {
            if (!value.esCaminable(true)) {
                continue
            }
            celdaLibres.add(key1)
        }
        if (celdaLibres.isEmpty() || celdaLibres.size < cant1 + cant2) {
            return
        }
        var temp: Short = -1
        if (cant1 >= 1) {
            posTeamRojo1.clear()
            while (posTeamRojo1.size < cant1 && celdaLibres.isNotEmpty()) {
                val rand = getRandomInt(0, celdaLibres.size - 1)
                val t = celdaLibres[rand]
                if (temp.toInt() == -1) {
                    posTeamRojo1.add(t)
                    temp = t
                } else {
                    val path = Camino.getPathPelea(this, temp, t, -1, null, true)
                    if (path != null && path._segundo.isNotEmpty()) {
                        if (path._segundo[path._segundo.size - 1]!!.id == t) {
                            posTeamRojo1.add(t)
                        }
                    }
                }
                celdaLibres.removeAt(rand)
            }
        }
        if (cant2 >= 1) {
            posTeamAzul2.clear()
            while (posTeamAzul2.size < cant2 && celdaLibres.isNotEmpty()) {
                val rand = getRandomInt(0, celdaLibres.size - 1)
                val t = celdaLibres[rand]
                if (temp.toInt() == -1) {
                    posTeamAzul2.add(t)
                    temp = t
                } else {
                    val path = Camino.getPathPelea(this, temp, t, -1, null, true)
                    if (path != null && path._segundo.isNotEmpty()) {
                        if (path._segundo[path._segundo.size - 1]!!.id == t) {
                            posTeamAzul2.add(t)
                        }
                    }
                }
                celdaLibres.removeAt(rand)
            }
        }
        convertCeldasPelea
    }

    fun panelPosiciones(perso: Personaje?, mostrar: Boolean) {
        val str = StringBuilder()
        val signo = if (mostrar) "+" else "-"
        for (s in posTeamRojo1) {
            str.append("|").append(signo).append(s.toInt()).append(";0;4")
        }
        for (s in posTeamAzul2) {
            str.append("|").append(signo).append(s.toInt()).append(";0;11")
        }
        if (cercado != null) {
            for (s in cercado!!.celdasObj) {
                str.append("|").append(signo).append(s.toInt()).append(";0;5")
            }
        }
        if (str.isEmpty()) {
            return
        }
        enviarEnCola(perso, "GDZ$str", false)
    }

    val bgID: Int
        get() = _bgID.toInt()

    val musicID: Int
        get() = _musicID.toInt()

    val ambienteID: Int
        get() = _ambienteID.toInt()

    val outDoor: Int
        get() = _outDoor.toInt()

    val capabilities: Int
        get() = _capabilities.toInt()

    fun strCeldasPelea(): String {
        return _celdasPelea
    }

    fun setMaxPeleas(max: Byte) {
        _maxPeleas = max
    }

    val maxNumeroPeleas: Int
        get() = _maxPeleas.toInt()

    private fun mobPosibles(mobs: String) {
        if (_mobPosibles == null) {
            return
        }
        _mobPosibles!!.clear()
        val ids = ArrayList<Int>()
        for (str in mobs.split(Pattern.quote("|").toRegex()).toTypedArray()) {
            try {
                if (str.isEmpty()) {
                    continue
                }
                var mobID = 0
                val split = str.split(",".toRegex()).toTypedArray()
                try {
                    mobID = split[0].toInt()
                } catch (ignored: Exception) {
                }
                mobID = Constantes.getMobSinHalloween(mobID)
                if (ids.contains(mobID)) {
                    continue
                }
                val mobModelo = Mundo.getMobModelo(mobID)
                if (mobModelo == null || mobModelo.tipoMob.toInt() == Constantes.MOB_TIPO_LOS_ARCHIMONSTRUOS) {
                    continue
                }
                ids.add(mobID)
                if (!mobModelo.puedeSubArea(subArea!!.id)) {
                    continue
                }
                var minLvl = 0
                var maxLvl = 0
                var cantidad = 0
                var probabilidad = mobModelo.probabilidadAparecer
                if (split.size > 1) {
                    try {
                        minLvl = split[1].toInt()
                        maxLvl = split[2].toInt()
                        cantidad = split[3].toInt()
                        probabilidad = split[4].toInt()
                    } catch (ignored: Exception) {
                    }
                }
                val mobP = MobPosible(cantidad, probabilidad)
                for (mob in Mundo.getMobModelo(mobID)?.grados?.values!!) {
                    if (minLvl > 0 && mob.nivel < minLvl) {
                        continue
                    }
                    if (maxLvl > 0 && mob.nivel > maxLvl) {
                        continue
                    }
                    //					mob.stats_aleatorias();
                    mobP.addMobPosible(mob)
                    addMobPosibles(mobP)
                }
            } catch (ignored: Exception) {
            }
        }
        _mobPosibles!!.trimToSize()
    }

    fun setKeyMapData(fecha: String, key: String, mapData: String) {
        var mapData = mapData
        this.fecha = fecha
        if (key.trim { it <= ' ' }.isNotEmpty()) {
            mapData = Encriptador.decifrarMapData(key, mapData)
        }
        this.mapData = mapData
        actualizarCasillas()
        CARGAR_TRIGGERS_POR_MAPA(id)
    }

    // public void setKeyMap(final String key) {
// _key = key;
// }
    val key: String
        get() = ""

    private fun addMobPosibles(mob: MobPosible) {
        if (_mobPosibles == null) {
            return
        }
        if (_mobPosibles!!.contains(mob)) {
            return
        }
        _mobPosibles!!.add(mob)
    }

    fun agregarEspadaPelea(perso: Personaje?) {
        for (pelea in _peleas!!.values) {
            pelea.infoEspadaPelea(perso!!)
        }
    }

    fun insertarMobs(mobs: String) {
        if (_mobPosibles == null) {
            return
        }
        mobPosibles(mobs)
        if (_mobPosibles!!.isEmpty()) {
            return
        }
        for (i in 0 until maxGrupoDeMobs) {
            getGrupoMobInicioServer("", AtlantaMain.INICIO_BONUS_ESTRELLAS_MOBS)
        }
    }

    // public void addCeldaObjInteractivo(final Celda celda) {
// _celdasObjInterac.add(celda);
// _celdasObjInterac.trimToSize();
// }
// public ArrayList<Celda> getCeldasObjInter() {
// return _celdasObjInterac;
// }
    fun setParametros(d: Short) {
        _capabilities = d
        _capabilities = capabilitiesCompilado
        if (colorCeldasAtacante.isEmpty() && (esMazmorra() || esArena())) {
            colorCeldasAtacante = "red"
        }
    }

    fun mapaNoAgresion(): Boolean {
        return _capabilities.toInt() and 1 == 1
    }

    fun esArena(): Boolean {
        return _capabilities.toInt() and 2 == 2
    }

    fun esMazmorra(): Boolean {
        return _capabilities.toInt() and 4 == 4
    }

    fun mapaNoDesafio(): Boolean {
        return _capabilities.toInt() and 8 == 8
    }

    fun mapaNoRecaudador(): Boolean {
        return _capabilities.toInt() and 16 == 16
    }

    private fun mapaNoMercante(): Boolean {
        return _capabilities.toInt() and 32 == 32
    }

    fun mapaAbonado(): Boolean {
        return _capabilities.toInt() and 64 == 64
    }

    fun mapaNoPrisma(): Boolean {
        return if (esMazmorra() || esArena() || Mundo.getCasaDentroPorMapa(id) != null || trabajos!!.isNotEmpty()) {
            true
        } else _capabilities.toInt() and 128 == 128
    }

    fun mapaNoPuedeSalvarTeleport(): Boolean {
        return _capabilities.toInt() and 256 == 256
    }

    fun mapaNoPuedeTeleportarse(): Boolean {
        return _capabilities.toInt() and 512 == 512
    }

    val capabilitiesCompilado: Short
        get() {
            var parametros: Short = 0
            if (mapaNoAgresion()) {
                parametros = (parametros.toInt() + 1).toShort()
            }
            if (esArena()) {
                parametros = (parametros.toInt() + 2).toShort()
            }
            if (esMazmorra()) {
                parametros = (parametros.toInt() + 4).toShort()
            }
            if (mapaNoDesafio()) {
                parametros = (parametros.toInt() + 8).toShort()
            }
            if (mapaNoRecaudador()) {
                parametros = (parametros.toInt() + 16).toShort()
            }
            if (mapaNoMercante()) {
                parametros = (parametros.toInt() + 32).toShort()
            }
            if (mapaAbonado()) {
                parametros = (parametros.toInt() + 64).toShort()
            }
            if (mapaNoPrisma()) {
                parametros = (parametros.toInt() + 128).toShort()
            }
            return parametros
        }

    fun addAccionFinPelea(tipoPelea: Int, accion: Accion) {
        delAccionFinPelea(tipoPelea, accion.id, accion.condicion)
        _accionFinPelea!!.computeIfAbsent(tipoPelea) { k: Int? -> ArrayList() }
        _accionFinPelea!![tipoPelea]!!.add(accion)
    }

    private fun delAccionFinPelea(tipoPelea: Int, tipoAccion: Int, condicion: String) {
        if (_accionFinPelea!![tipoPelea] == null) {
            return
        }
        val copy = ArrayList(_accionFinPelea!![tipoPelea])
        for (acc in copy) {
            if (acc.id == tipoAccion && acc.condicion == condicion) {
                _accionFinPelea!![tipoPelea]!!.remove(acc)
            }
        }
    }

    fun borrarAccionesPelea() {
        _accionFinPelea!!.clear()
    }

    fun aplicarAccionFinPelea(
        tipo: Int, ganadores: Collection<Luchador>,
        acciones: ArrayList<Accion>?
    ) {
        val acc = ArrayList<Accion>()
        if (acciones != null) {
            acc.addAll(acciones)
        }
        if (_accionFinPelea!![tipo] != null) {
            _accionFinPelea!![tipo]?.let { acc.addAll(it) }
        }
        for (accion in acc) {
            for (ganador in ganadores) {
                if (ganador.estaRetirado()) {
                    continue
                }
                val perso = ganador.personaje
                if (perso != null) {
                    if (!validaCondiciones(perso, accion.condicion)) {
                        ENVIAR_Im_INFORMACION(perso, "119|45")
                        continue
                    }
                    accion.realizarAccion(perso, null, -1, (-1).toShort())
                }
            }
        }
    }

    private fun actualizarCasillas() {
        if (mapData.isNotEmpty()) {
            Encriptador.decompilarMapaData(this)
        }
    }

    // public void setMapData(final String mapdata) {
// _mapData = mapdata;
// }
    fun panelTriggers(perso: Personaje?, mostrar: Boolean) {
        val str = StringBuilder()
        for (c in _celdas.values) {
            if (c.acciones == null) {
                return
            }
            if (c.acciones!!.containsKey(0)) {
                str.append("|").append(if (mostrar) "+" else "-").append(c.id.toInt()).append(";0;11")
            }
        }
        if (str.isEmpty()) {
            return
        }
        enviarEnCola(perso, "GDZ$str", false)
    }

    val celdas: MutableMap<Short, Celda>
        get() = _celdas

    fun getCelda(id: Short): Celda? {
        return _celdas[id]
    }

    val npCs: Map<Int, NPC>?
        get() = _npcs

    private fun sigIDNPC(): Int {
        return if (_sigIDNpc <= -100) {
            -51
        } else _sigIDNpc--.toInt()
    }

    fun addNPC(npcModelo: NPCModelo?, celdaID: Short, dir: Byte): NPC {
        val npc = NPC(npcModelo, sigIDNPC(), celdaID, dir)
        _npcs!![npc.id] = npc
        return npc
    }

    fun borrarNPC(id: Int) {
        _npcs!!.remove(id)
    }

    fun getNPC(id: Int): NPC? {
        return _npcs!![id]
    }

    fun addPersonaje(perso: Personaje) {
        if (arrayPersonajes == null) {
            return
        }
        if (!arrayPersonajes!!.contains(perso)) {
            arrayPersonajes!!.add(perso)
        }
    }

    fun removerPersonaje(perso: Personaje?) {
        if (arrayPersonajes == null) {
            return
        }
        arrayPersonajes!!.remove(perso)
    }

    fun expulsarMercanterPorCelda(celda: Short) {
        for (perso in _mercantes!!.values) {
            if (perso.celda.id == celda) {
                removerMercante(perso.Id)
                perso.setMercante(false)
                ENVIAR_GM_BORRAR_GM_A_MAPA(perso.mapa, perso.Id)
                return
            }
        }
    }

    fun removerMercante(id: Int) {
        _mercantes!!.remove(id)
    }

    fun mercantesEnCelda(celda: Short): Int {
        var i = 0
        for (perso in _mercantes!!.values) {
            if (perso.celda.id == celda) {
                i++
            }
        }
        return i
    }

    fun addMercante(perso: Personaje) {
        if (_mercantes!!.size >= maxMercantes) {
            return
        }
        _mercantes!![perso.Id] = perso
    }

    fun cantMercantes(): Int {
        return _mercantes!!.size
    }

    fun cantPersonajes(): Int {
        return if (arrayPersonajes == null) 0 else arrayPersonajes!!.size
    }

    fun cantNpcs(): Int {
        return _npcs!!.size
    }

    fun cantMobs(): Int {
        return _grupoMobsTotales!!.size
    }

    fun puedeAgregarOtraPelea(): Boolean {
        return _peleas!!.size >= _maxPeleas
    }

    val gMPrisma: String
        get() = if (prisma == null) {
            ""
        } else "GM|+" + prisma!!.stringGM()

    val gMRecaudador: String
        get() = if (recaudador == null) {
            ""
        } else "GM|+" + recaudador!!.stringGM()

    fun getGMsPersonajes(perso: Personaje): String {
        val str = StringBuilder("GM")
        try {
            val i = !perso.esIndetectable()
            for (p in arrayPersonajes!!) {
                if (i && p.esIndetectable()) {
                    continue
                }
                if (removePersonajeBug(p)) {
                    continue
                }
                if (p.pelea == null) {
                    str.append("|+").append(p.stringGM())
                }
            }
        } catch (e: Exception) {
            return getGMsPersonajes(perso)
        }
        return if (str.length < 3) {
            ""
        } else str.toString()
    }

    private fun removePersonajeBug(perso: Personaje): Boolean {
        if (!perso.enLinea()) {
            try {
                perso.celda = null
            } catch (ignored: Exception) {
            }
            removerPersonaje(perso)
            return true
        }
        return false
    }

    fun getGMsLuchadores(idMirador: Int): String {
        val str = StringBuilder("GM")
        for (celda in _celdas.values) {
            try {
                if (celda.luchadores == null) return ""
                for (luchador in celda.luchadores!!) {
                    str.append("|+").append(luchador.stringGM(idMirador))
                }
            } catch (ignored: Exception) {
            }
        }
        return if (str.length < 3) {
            ""
        } else str.toString()
    }

    val gMsGrupoMobs: String
        get() {
            if (_grupoMobsTotales!!.isEmpty()) {
                return ""
            }
            val str = StringBuilder("GM")
            var GM: String
            for (grupoMob in _grupoMobsTotales!!.values) {
                try {
                    GM = grupoMob.stringGM()
                    if (GM.isEmpty()) {
                        continue
                    }
                    str.append("|+").append(GM)
                } catch (ignored: Exception) {
                }
            }
            return str.toString()
        }

    fun RefrescarGM_Mobs_Enmapa() {
        for (grupoMob in _grupoMobsTotales!!.values) {
            try {
                ENVIAR_GM_BORRAR_GM_A_MAPA(this, grupoMob.id)
                ENVIAR_GM_GRUPOMOB_A_MAPA(this, "+" + grupoMob.stringGM())
            } catch (ignored: Exception) {
            }
        }
    }

    fun RefrescarGM_OI_Enmapa(perso: Personaje) {
        val packet: StringBuilder = StringBuilder(perso.mapa.objetosInteracGDF)
        if (packet.isNotEmpty()) {
            enviar(perso, packet.toString())
        }
    }

    fun getGMsNPCs(perso: Personaje?): String {
        if (_npcs!!.isEmpty()) {
            return ""
        }
        val str = StringBuilder("GM")
        for (npc in _npcs!!.values) {
            try {
                str.append("|+").append(npc.strinGM(perso))
            } catch (ignored: Exception) {
            }
        }
        return str.toString()
    }

    val gMsMercantes: String
        get() {
            if (_mercantes!!.isEmpty()) {
                return ""
            }
            val str = StringBuilder("GM")
            for (perso in _mercantes!!.values) {
                try {
                    str.append("|+").append(perso.stringGMmercante())
                } catch (ignored: Exception) {
                }
            }
            return str.toString()
        }

    fun getGMsMonturas(perso: Personaje): String {
        if (cercado == null || cercado!!.criando.isEmpty()) {
            return ""
        }
        val str = StringBuilder("GM")
        val esPublico = cercado!!.esPublico()
        for (montura in cercado!!.criando.values) {
            if (!AtlantaMain.PARAM_MOSTRAR_MONTURAS_CERCADOS && esPublico && montura.dueñoID != perso.Id) {
                continue
            }
            str.append("|+").append(montura.stringGM())
        }
        return str.toString()
    }

    val objetosCria: String
        get() {
            if (cercado == null || cercado!!.objetosCrianza.isEmpty()) {
                return ""
            }
            val str = StringBuilder()
            for ((key1, value) in cercado!!.objetosCrianza) {
                if (str.isNotEmpty()) {
                    str.append("|")
                }
                if (cercado!!.dueñoID == -1) {
                    str.append(key1).append(";").append(Constantes.getObjCriaPorMapa(id)).append(";1;1000;1000")
                } else {
                    str.append(key1).append(";").append(value!!.objModeloID).append(";1;").append(value.durabilidad)
                        .append(";").append(
                            value.durabilidadMax
                        )
                }
            }
            return "GDO+$str"
        }

    val objetosInteracGDF: String
        get() {
            val str = StringBuilder("GDC")
            val str2 = StringBuilder("GDF")
            for (celda in _celdas.values) {
                if (celda.objetoInteractivo != null) {
                    str2.append("|").append(celda.id.toInt()).append(";").append(celda.objetoInteractivo!!.infoPacket)
                } else if (celda.estado.toInt() != 1) {
                    str2.append("|").append(celda.id.toInt()).append(";").append(celda.estado.toInt())
                    str.append(celda.id.toInt()).append(";aaVaaaaaaa800|")
                }
            }
            if (str.length == 3 && str2.length == 3) {
                return ""
            }
            if (str.length == 3) {
                return str2.toString()
            }
            return if (str2.length == 3) {
                str.toString()
            } else str.toString() + Constantes.x0char + str2.toString()
        }

    val randomCeldaIDLibre: Short
        get() {
            val celdaLibre = ArrayList<Short>()
            for (celda in _celdas.values) {
                if (!celda.esCaminable(true) || celda.primerPersonaje != null) {
                    continue
                }
                if (cercado != null && cercado!!.celdasObj.contains(celda.id)) continue
                celdaLibre.add(celda.id)
            }
            for (grupoMob in _grupoMobsTotales!!.values) {
                celdaLibre.remove(grupoMob.celdaID as Any)
            }
            for (npc in _npcs!!.values) {
                celdaLibre.remove(npc.celdaID as Any)
            }
            return if (celdaLibre.isEmpty()) {
                -1
            } else celdaLibre[getRandomInt(0, celdaLibre.size - 1)]
        }

    fun refrescarGrupoMobs() {
        if (AtlantaMain.MODO_HEROICO || AtlantaMain.MAPAS_MODO_HEROICO.contains(id)) {
            return
        }
        val idsBorrar = ArrayList<Int>()
        for (gm in _grupoMobsTotales!!.values) {
            if (gm.tipo != TipoGrupo.NORMAL) {
                continue
            }
            val id = gm.id
            idsBorrar.add(id)
            ENVIAR_GM_BORRAR_GM_A_MAPA(this, id)
        }
        for (id in idsBorrar) {
            _grupoMobsTotales!!.remove(id)
        }
        if (_mobPosibles == null) {
            return
        }
        if (_mobPosibles!!.isEmpty()) {
            return
        }
        for (i in 1..maxGrupoDeMobs) {
            getGrupoMobInicioServer("", AtlantaMain.INICIO_BONUS_ESTRELLAS_MOBS)
        }
    }

    fun refrescarGrupoMobs_con_estrellas(estrellas: Int) {
        if (AtlantaMain.MODO_HEROICO || AtlantaMain.MAPAS_MODO_HEROICO.contains(id)) {
            return
        }
        val idsBorrar = ArrayList<Int>()
        for (gm in _grupoMobsTotales!!.values) {
            if (gm.tipo != TipoGrupo.NORMAL) {
                continue
            }
            val id = gm.id
            idsBorrar.add(id)
            ENVIAR_GM_BORRAR_GM_A_MAPA(this, id)
        }
        for (id in idsBorrar) {
            _grupoMobsTotales!!.remove(id)
        }
        if (_mobPosibles == null) {
            return
        }
        if (_mobPosibles!!.isEmpty()) {
            return
        }
        for (i in 1..maxGrupoDeMobs) {
            getGrupoMobInicioServer("", estrellas)
        }
    }

    fun moverGrupoMobs(mover: Int) { // String str = "";
        var mover = mover
        try {
            mover = (Math.random() * (mover - 1)).toInt() + 1
            var cantGruposAMover = 0
            while (cantGruposAMover < mover) {
                var noHay = true
                for (grupoMob in _grupoMobsTotales!!.values) {
                    if (grupoMob.enPelea()) {
                        continue
                    }
                    if (!AtlantaMain.PARAM_MOVER_MOBS_FIJOS && grupoMob.tipo == TipoGrupo.FIJO) {
                        continue
                    }
                    noHay = false
                    if (randomBoolean) {
                        grupoMob.moverGrupoMob(this)
                        cantGruposAMover++
                    }
                }
                if (noHay) {
                    break
                }
            }
        } catch (ignored: Exception) {
        }
        // return str;
    }

    fun subirEstrellasOI(cant: Int) {
        for (oi in objetosInteractivos!!) {
            oi.subirBonusEstrellas(cant * 20)
        }
    }

    fun subirEstrellasMobs(cant: Int) {
        for (grupoMob in _grupoMobsTotales!!.values) { // * (mapaMazmorra() ? 45 ):
            try {
                if (grupoMob.bonusEstrellas != AtlantaMain.MAX_BONUS_ESTRELLAS_MOBS) {
                    grupoMob.subirBonusEstrellas(cant * 20)
                    ENVIAR_GM_BORRAR_GM_A_MAPA(this, grupoMob.id)
                    ENVIAR_GM_GRUPOMOB_A_MAPA(this, "+" + grupoMob.stringGM())
                }
            } catch (e: Exception) {
                redactarLogServidorln(e.toString())
            }
        }
    }

    @Synchronized
    fun jugadorLLegaACelda(
        perso: Personaje, celdaIDDestino: Short, celdaIDPacket: Short,
        ok: Boolean
    ): Boolean {
        var bug: Byte = 0
        try {
            val celdaDestino = getCelda(celdaIDDestino)
            if (celdaDestino == null) {
                ENVIAR_BN_NADA(perso, " FINALIZAR DESPLAZAMIENTO CELDA NULA")
                return false
            }
            bug = 1
            if (perso.mapa.id != id || perso.estaDisponible(false, true)) {
                return false
            }
            bug = 2
            perso.celda = celdaDestino
            val objTirado = celdaDestino.objetoTirado
            bug = 3
            if (objTirado != null) {
                celdaDestino.objetoTirado = null
                perso.addObjIdentAInventario(objTirado, true)
                ENVIAR_GDO_OBJETO_TIRAR_SUELO(this, '-', celdaDestino.id, 0, false, "")
                ENVIAR_Ow_PODS_DEL_PJ(perso)
            }
            bug = 4
            var activoInt = false
            if (celdaIDPacket != celdaIDDestino) { // cuando no se puede llegar a cierta celda por un stop:
                val celdaObjetivo = getCelda(celdaIDPacket)
                if (celdaObjetivo != null) {
                    val objInteractivo = celdaObjetivo.objetoInteractivo
                    if (objInteractivo != null && objInteractivo.objIntModelo == null) {
                        activoInt = true
                        perso.realizarOtroInteractivo(celdaObjetivo, objInteractivo)
                    }
                }
            }
            bug = 5
            if (!activoInt) {
                if (ok) {
                    if (perso.estaDisponible(true, true)) {
                        bug = 6
                    } else {
                        bug = 7
                        for (p in arrayPersonajes!!) {
                            if (removePersonajeBug(p)) {
                                continue
                            }
                            if (!Constantes.puedeIniciarPelea(perso, p, this, celdaDestino)) {
                                continue
                            }
                            val agroP = p.statsObjEquipados.getStatParaMostrar(Constantes.STAT_AGREDIR_AUTOMATICAMENTE)
                            val agroPerso =
                                perso.statsObjEquipados.getStatParaMostrar(Constantes.STAT_AGREDIR_AUTOMATICAMENTE)
                            val agresor = if (agroPerso >= agroP) perso else p
                            val agredido = if (agroPerso >= agroP) p else perso
                            iniciarPeleaPVP(agresor, agredido, false)
                            return true
                        }
                        bug = 8
                        var agredido = true
                        for (grupoMob in _grupoMobsTotales!!.values) {
                            if (!Constantes.puedeIniciarPelea(perso, grupoMob, this, celdaDestino)) {
                                agredido = false// System.out.println("no cumple las condiciones para iniciar pelea");
                                continue
                            }
                            agredido = true
                            if (perso.pelea != null) {
                                continue
                            }
                            val pelea = iniciarPelea(
                                perso, null, perso.celda.id, (-1).toShort(), Constantes.PELEA_TIPO_PVM,
                                grupoMob
                            )
                            if (perso.grupoParty != null) {
                                if (perso.esMaestro() && pelea != null) {
                                    perso.grupoParty.unirAPelea(pelea)
                                } else if (perso.grupoParty.miembros.size > 0 && pelea != null) { //	System.out.println("Tiene grupo");
                                    val lider = perso.grupoParty.liderGrupo
                                    if (lider != null) {
                                        if (!lider.estaAusente() && lider.pelea == null && lider.grupoParty.tieneAlumnos()) {
                                            pelea.unirsePelea(lider, perso.Id)
                                        }
                                    }
                                    for (alumnos in perso.grupoParty.alumnos) {
                                        if (!alumnos.estaAusente() && alumnos.pelea == null && alumnos != perso) {
                                            Objects.requireNonNull(pelea)!!.unirsePelea(alumnos, perso.Id)
//                                        lider.grupoParty.unirAPelea(pelea)
                                        }
                                    }
                                }
                            }
                            return agredido
                        }
                    }
                }
                bug = 9
                celdaDestino.aplicarAccion(perso)
            }
        } catch (e: Exception) {
            val error = "EXCEPTION jugadorLLegaACelda bug: $bug e:$e"
            ENVIAR_BN_NADA(perso, error)
            redactarLogServidorln(error)
        }
        return ok
    }

    private fun addSigGrupoMobEnCola(grupoMob: GrupoMob): Boolean {
        var gm: GrupoMob? = null
        if (_grupoMobsEnCola != null) {
            for (g in _grupoMobsEnCola!!) {
                if (g.tipo != grupoMob.tipo) {
                    continue
                }
                if (g.strGrupoMob.equals(grupoMob.strGrupoMob, ignoreCase = true)) {
                    gm = g
                    break
                }
            }
        }
        if (gm != null) {
            _grupoMobsEnCola!!.remove(gm)
            addGrupoMobSioSi(gm)
            return true
        }
        return false
    }

    // public synchronized void addSigGrupoMobRespawn(GrupoMob grupoMob) {
// GrupoMob gm = null;
// if (grupoMob.getTipo() == TipoGrupo.NORMAL) {
// gm = addGrupoMobPosible(grupoMob.getCeldaID());
// } else if (grupoMob.getTipo() == TipoGrupo.FIJO) {
// gm = addGrupoMobPorTipo(grupoMob.getCeldaID(), grupoMob.getStrGrupoMob(), grupoMob.getTipo(),
// grupoMob
// .getCondInicioPelea());
// } else if (grupoMob.getTipo() == TipoGrupo.HASTA_QUE_MUERA) {
// addGrupoMobSioSi(grupoMob);
// }
// }
    @Synchronized
    fun addSiguienteGrupoMob(grupoMob: GrupoMob?, filtro: Boolean) {
        if (grupoMob == null) {
            return
        }
        when (grupoMob.tipo) {
            TipoGrupo.HASTA_QUE_MUERA, TipoGrupo.SOLO_UNA_PELEA, TipoGrupo.NORMAL -> return
            TipoGrupo.FIJO -> {
                if (filtro) {
                    if (!addSigGrupoMobEnCola(grupoMob)) {
                        AparecerMobs(this, grupoMob, Aparecer.INICIO_PELEA)
                    }
                    return
                }
                val gm = addGrupoMobPorTipo(
                    grupoMob.celdaID, grupoMob.strGrupoMob, TipoGrupo.FIJO, grupoMob
                        .condInicioPelea, grupoMob.mapasRandom
                )
                if (gm != null) {
                    gm.segundosRespawn = grupoMob.segundosRespawn
                }
            }
        }
    }

    @Synchronized
    fun addUltimoGrupoMob(grupoMob: GrupoMob?, filtro: Boolean) {
        if (grupoMob == null) {
            return
        }
        when (grupoMob.tipo) {
            TipoGrupo.SOLO_UNA_PELEA -> return
            TipoGrupo.HASTA_QUE_MUERA -> {
                if (!grupoMob.estaMuerto()) {
                    addGrupoMobSioSi(grupoMob)
                }
                return
            }
            TipoGrupo.NORMAL -> {
                // System.out.println("estaMuerto: " + grupoMob.estaMuerto());
// System.out.println("esHeroico: " + grupoMob.esHeroico());
                if (filtro) { // if (addSigGrupoMobEnCola()) {
// return;
// }
                    if (!grupoMob.estaMuerto() && grupoMob.esHeroico()) {
                        addGrupoMobSioSi(grupoMob)
                    } else {
                        AparecerMobs(this, grupoMob, Aparecer.FINAL_PELEA)
                    }
                    return
                }
                var celdaID = grupoMob.celdaID
                if (AtlantaMain.PARAM_MOBS_RANDOM_REAPARECER_OTRA_CELDA) {
                    celdaID = -1
                }
                var gm: GrupoMob? = null
                gm = if (grupoMob.fijo) { // esta en duda, porq en gestorsql, al normal se le cambia a fijo
                    addGrupoMobPorTipo(
                        celdaID, grupoMob.strGrupoMob, TipoGrupo.NORMAL, grupoMob.condInicioPelea,
                        grupoMob.mapasRandom
                    )
                } else {
                    addGrupoMobPosible(celdaID)
                }
                if (gm != null) {
                    gm.segundosRespawn = grupoMob.segundosRespawn
                }
            }
            TipoGrupo.FIJO -> if (!grupoMob.estaMuerto() && grupoMob.esHeroico()) {
                addUltimoGrupoMob(grupoMob)
            }
        }
    }

    private fun addUltimoGrupoMob(grupoMob: GrupoMob) {
        if (grupoMob.estaMuerto()) {
            return
        }
        if (AtlantaMain.MODO_HEROICO || AtlantaMain.MAPAS_MODO_HEROICO.contains(id)) {
            _grupoMobsEnCola!!.add(grupoMob)
        }
    }

    // --------------------------------
    @Synchronized
    fun sigIDGrupoMob(): Int {
        for (id in -1 downTo -50) {
            if (_grupoMobsTotales!![id] == null) {
                var usado = false
                for (pelea in _peleas!!.values) {
                    if (pelea.IDLuchInit2 == id) {
                        usado = true
                        break
                    }
                }
                if (usado) {
                    continue
                }
                return id
            }
        }
        return -1
    }

    @Synchronized
    private fun getGrupoMobInicioServer(heroico: String, estrellas: Int): GrupoMob? {
        if (_mobPosibles == null || _mobPosibles!!.isEmpty() || _grupoMobsTotales!!.size >= maxGrupoDeMobs) {
            return null
        }
        var grupoMob: GrupoMob? = null
        if (heroico.isNotEmpty()) {
            val strMob = heroico.split(Pattern.quote("|").toRegex()).toTypedArray()[0]
            grupoMob = addGrupoMobPorTipo((-1).toShort(), strMob, TipoGrupo.NORMAL, "", null)
            grupoMob!!.addObjetosKamasInicioServer(heroico)
        } else {
            grupoMob = addGrupoMobPosible((-1).toShort())
        }
        if (grupoMob == null) {
            return null
        }
        for (a in grupoMob.mobs) { //			a.stats_aleatorias();
        }
        grupoMob.bonusEstrellas = estrellas
        return grupoMob
    }

    // --------------------------
// AQUI ES PARA MOSTRAR LOS GRUPOS DE MOBS
    @Synchronized
    fun addGrupoMobSioSi(grupoMob: GrupoMob) {
        grupoMob.id = sigIDGrupoMob()
        _grupoMobsTotales!![grupoMob.id] = grupoMob
        if (cantPersonajes() > 0) {
            ENVIAR_GM_GRUPOMOB_A_MAPA(this, "+" + grupoMob.stringGM())
        }
    }

    @Synchronized
    private fun addGrupoMobPosible(celdaID: Short): GrupoMob? {
        if (_mobPosibles == null || _mobPosibles!!.isEmpty()) {
            return null
        }
        val grupoMob = GrupoMob(_mobPosibles, this, celdaID, maxMobsPorGrupo.toInt())
        if (grupoMob.mobs.isEmpty()) {
            return null
        }
        _grupoMobsTotales!![grupoMob.id] = grupoMob
        if (cantPersonajes() > 0) {
            ENVIAR_GM_GRUPOMOB_A_MAPA(this, "+" + grupoMob.stringGM())
        }
        return grupoMob
    }

    @Synchronized
    fun addGrupoMobPorTipo(
        celdaID: Short, strGrupoMob: String?, tipo: TipoGrupo?,
        condicion: String?, mapas: ArrayList<Mapa>?
    ): GrupoMob? {
        var mapas = mapas
        var mapa = this
        if (mapas != null && mapas.size > 1) {
            mapa = mapas[getRandomInt(0, mapas.size - 1)]
        } else {
            mapas = null
        }
        val grupoMob = GrupoMob(mapa, celdaID, strGrupoMob!!, tipo!!, condicion!!)
        if (grupoMob.mobs.isEmpty()) {
            return null
        }
        grupoMob.mapasRandom = mapas
        mapa._grupoMobsTotales!![grupoMob.id] = grupoMob
        if (mapa.cantPersonajes() > 0) {
            ENVIAR_GM_GRUPOMOB_A_MAPA(mapa, "+" + grupoMob.stringGM())
        }
        return grupoMob
    }

    @Synchronized
    fun iniciarPeleaPVP(agresor: Personaje, agredido: Personaje, deshonor: Boolean) {
        agresor.botonActDesacAlas('+')
        agredido.agresion = true
        agresor.agresion = true
        ENVIAR_GA_ACCION_JUEGO_AL_MAPA(this, -1, 906, agresor.Id.toString() + "", agredido.Id.toString() + "")
        iniciarPelea(
            agresor, agredido, agresor.celda.id, agredido.celda.id, Constantes.PELEA_TIPO_PVP,
            null
        )
        agresor.pelea.setDeshonor(deshonor)
        agredido.agresion = false
        agresor.agresion = false
    }

    @Synchronized
    fun iniciarPelea(
        pre1: PreLuchador?, pre2: PreLuchador?, celda1: Short, celda2: Short,
        tipo: Byte, grupoMob: GrupoMob?
    ): Pelea? {
        var pre2 = pre2
        var celda2 = celda2
        try {
            if (puedeAgregarOtraPelea()) {
                return null
            }
            val cant = grupoMob?.cantMobs ?: 1
            if (prepararCeldasPelea(1, cant)) {
                return null
            }
            if (grupoMob != null) {
                if (grupoMob.enPelea()) {
                    return null
                }
                grupoMob.mobs[0].stats_aleatorias()
                pre2 = grupoMob.mobs[0].invocarMob(grupoMob.id, false, null)
                celda2 = _celdas[grupoMob.celdaID]?.let { Camino.getCeldaIDCercanaLibre(it, this) }!!
                grupoMob.mobs[0].reiniciar_stats()
            }
            //            if (grupoMob != null) {
//                if (grupoMob.enPelea()) {
//                    return null;
//                }
//                for (MobGradoModelo a :
//                        grupoMob.getMobs()) {
//                    if (AtlantaMain.RATE_RANDOM_MOB != 1) {
//                        a.stats_aleatorias();
//                    } else {
//                        a.stats_base();
//                    }
//                }
//            }
            val id = sigIDPelea()
            val pelea = Pelea(id, this, pre1!!, pre2!!, celda1, celda2, tipo, grupoMob, strCeldasPeleaPosAtacante())
            _peleas!![id] = pelea
            if (grupoMob != null) {
                _grupoMobsTotales!!.remove(grupoMob.id)
                // _grupoMobsFix.remove(grupoMob.getID());
                for (i in 1 until grupoMob.mobs.size) {
                    if (AtlantaMain.RATE_RANDOM_MOB != 1.0) {
                        grupoMob.mobs[i].stats_aleatorias()
                    }
                    val mob = grupoMob.mobs[i].invocarMob(pelea.sigIDLuchadores(), false, null)
                    if (mob.id == pre2.id) {
                        continue
                    }
                    pelea.unirsePelea(mob, pre2.id)
                    if (AtlantaMain.RATE_RANDOM_MOB != 1.0) {
                        grupoMob.mobs[i].reiniciar_stats()
                    }
                }
            }
            //			pelea.Reiniciar_stats_grupo_mob_fun();
            ENVIAR_fC_CANTIDAD_DE_PELEAS(this)
            return pelea
        } catch (e: Exception) {
            redactarLogServidorln("EXCEPTION iniciarPelea $e")
            e.printStackTrace()
        }
        return null
    }

    @Synchronized
    fun iniciarPeleaKoliseo(init1: GrupoKoliseo?, init2: GrupoKoliseo?): Boolean {
        if (prepararCeldasPelea(
                AtlantaMain.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO,
                AtlantaMain.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO
            )
        ) {
            return false
        }
        val id = sigIDPelea()
        val pelea = Pelea(id, this, init1!!, init2!!, strCeldasPeleaPosAtacante())
        _peleas!![id] = pelea
        ENVIAR_fC_CANTIDAD_DE_PELEAS(this)
        return true
    }

    private fun strCeldasPeleaPosAtacante(): String {
        return "$_celdasPelea|$colorCeldasAtacante"
    }

    @Synchronized
    fun sigIDPelea(): Short {
        var id: Short = 1
        while (true) {
            if (_peleas!![id] == null) {
                return id
            }
            id++
        }
    }

    val numeroPeleas: Int
        get() = _peleas!!.size

    val peleas: Map<Short, Pelea>?
        get() = _peleas

    fun borrarPelea(id: Short) {
        _peleas!!.remove(id)
    }

    fun getPelea(id: Short): Pelea? {
        return _peleas!![id]
    }

    val puertaCercado: ObjetoInteractivo?
        get() {
            for (c in _celdas.values) {
                try {
                    if (c.objetoInteractivo!!.objIntModelo!!.id == 120) {
                        return c.objetoInteractivo
                    }
                } catch (ignored: Exception) {
                }
            }
            return null
        }

    val grupoMobsTotales: Map<Int, GrupoMob>?
        get() = _grupoMobsTotales

    // public CopyOnWriteArrayList<GrupoMob> getGrupoMobsHeroicos() {
// return _grupoMobsHeroicos;
// }
    fun borrarGrupoMob(id: Int) {
        _grupoMobsTotales!!.remove(id)
    }

    fun borrarTodosMobsNoFijos() {
        _mobPosibles!!.clear()
        val idsBorrar = ArrayList<Int>()
        for (gm in _grupoMobsTotales!!.values) {
            if (gm.tipo == TipoGrupo.FIJO) {
                continue
            }
            val id = gm.id
            idsBorrar.add(id)
            ENVIAR_GM_BORRAR_GM_A_MAPA(this, id)
        }
        for (id in idsBorrar) {
            _grupoMobsTotales!!.remove(id)
        }
        UPDATE_SET_MOBS_MAPA(id.toInt(), "")
    }

    fun borrarTodosMobsFijos() {
        val idsBorrar = ArrayList<Int>()
        for (gm in _grupoMobsTotales!!.values) {
            if (gm.tipo != TipoGrupo.FIJO) {
                continue
            }
            val id = gm.id
            idsBorrar.add(id)
            ENVIAR_GM_BORRAR_GM_A_MAPA(this, id)
        }
        for (id in idsBorrar) {
            _grupoMobsTotales!!.remove(id)
        }
        DELETE_MOBS_FIX_MAPA(id.toInt())
    }

    @Synchronized
    fun salvarMapaHeroico() {
        if (_grupoMobsTotales!!.isEmpty() && _grupoMobsEnCola!!.isEmpty()) { //			GestorSQL.DELETE_MAPA_HEROICO(_id);
            return
        }
        val mobs = StringBuilder()
        val objetos = StringBuilder()
        val kamas = StringBuilder()
        val grupoMobs = ArrayList<GrupoMob>()
        grupoMobs.addAll(_grupoMobsTotales!!.values)
        grupoMobs.addAll(_grupoMobsEnCola!!)
        var paso = false
        for (g in grupoMobs) {
            if (g.kamasHeroico <= 0 && g.cantObjHeroico() == 0) {
                continue
            }
            if (paso) {
                mobs.append("|")
                objetos.append("|")
                kamas.append("|")
            }
            mobs.append(g.strGrupoMob)
            objetos.append(g.iDsObjeto)
            kamas.append(g.kamasHeroico)
            paso = true
        }
        if (!paso) {
            DELETE_MAPA_HEROICO(id.toInt())
            return
        }
        REPLACE_MAPAS_HEROICO(id.toInt(), mobs.toString(), objetos.toString(), kamas.toString())
    }

    fun objetosTirados(perso: Personaje?) {
        for (c in _celdas.values) {
            if (c.objetoTirado != null) {
                c.objetoTirado?.objModelo?.id?.let {
                    ENVIAR_GDO_OBJETO_TIRAR_SUELO(
                        perso!!, '+', c.id, it,
                        false, ""
                    )
                }
            }
        }
    }

    fun limpiarobjetostirados() {
        for (c in _celdas.values) {
            if (c.objetoTirado != null) {
                c.objetoTirado = null
            }
        }
    }

    fun getCeldaPorPos(x: Byte, y: Byte): Celda? {
        for (c in _celdas.values) {
            if (c.coordX == x && c.coordY == y) {
                return c
            }
        }
        return null
    }
}