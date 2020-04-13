package variables.mob

import estaticos.*
import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Formulas.getRandomInt
import estaticos.Formulas.randomBoolean
import estaticos.GestorSQL.SALVAR_OBJETO
import estaticos.GestorSalida.ENVIAR_GA_MOVER_SPRITE_MAPA
import variables.mapa.Mapa
import variables.mob.AparecerMobs.Aparecer
import variables.mob.MobModelo.TipoGrupo
import variables.objeto.Objeto
import variables.pelea.Pelea
import variables.personaje.Personaje
import java.util.*
import java.util.regex.Pattern
import kotlin.math.max

class GrupoMob {
    val tipo: TipoGrupo
    val mobs = ArrayList<MobGradoModelo>()
    private val _almas = HashMap<Int, Int?>()
    val objetosHeroico = ArrayList<Int>()
    private var _muerto = false
    var fijo = false
        private set
    private var _orientacion: Byte = 3
    var alineacion = Constantes.ALINEACION_NULL
        private set
    var distAgresion: Byte = 0
        private set
    var celdaID: Short = 0
    private var _bonusEstrellas = AtlantaMain.INICIO_BONUS_ESTRELLAS_MOBS
    var id: Int = 0
    var segundosRespawn = 0
    // public void setCondIniciaPelea(final String cond) {
// _condInicioPelea = cond;
// }
    var condInicioPelea = ""
        private set
    var strGrupoMob = ""
        private set
    var condUnirsePelea = ""
    private var _timer: Timer? = null
    private var _pelea: Pelea? = null
    var kamasHeroico: Long = 0
    var mapasRandom: ArrayList<Mapa>? = null

    constructor(
        posiblesMobs: ArrayList<MobPosible>?, mapa: Mapa, celdaID: Short,
        maxMobsPorGrupo: Int
    ) {
        tipo = TipoGrupo.NORMAL
        if (posiblesMobs == null || posiblesMobs.isEmpty()) {
            return
        }
        if (maxMobsPorGrupo < AtlantaMain.MIN_CANTIDAD_MOBS_EN_GRUPO) {
            return
        }
        val nroMobs = getRandomInt(AtlantaMain.MIN_CANTIDAD_MOBS_EN_GRUPO, maxMobsPorGrupo)
        // if (nroMobs > 8) {
// nroMobs = 8;
// }
        this.celdaID = if (celdaID.toInt() == -1) mapa.randomCeldaIDLibre else celdaID
        if (celdaID.toInt() == 0) {
            return
        }
        var maxNivel = 0
        var archi = false
        val str = StringBuilder()
        val mobsEscogidos = ArrayList<MobGradoModelo?>()
        val tempPosibles = ArrayList<MobPosible>()
        for (i in 0..7) {
            tempPosibles.clear()
            tempPosibles.addAll(posiblesMobs)
            var nivelTotal = 0
            while (mobsEscogidos.size < nroMobs && tempPosibles.isNotEmpty()) {
                val mp = tempPosibles[getRandomInt(0, tempPosibles.size - 1)]
                if (mp.cantMax > 0) {
                    var cantidad = 0
                    for (m in mobsEscogidos) {
                        if (mp.tieneMob(m)) {
                            cantidad++
                        }
                    }
                    if (cantidad >= mp.cantMax) {
                        tempPosibles.remove(mp)
                        continue
                    }
                }
                if (!mp.pasoProbabilidad()) {
                    continue
                }
                val mob = mp.randomMob
                nivelTotal += mob!!.nivel.toInt()
                mobsEscogidos.add(mob)
            }
            if (mapa.esNivelGrupoMobPermitido(nivelTotal)) {
                break
            }
        }
        for (mg in mobsEscogidos) {
            var mobGrado = mg
            val idMobModelo = mobGrado!!.idModelo
            if (!archi && !mapa.esMazmorra()) {
                val archiMob = mobGrado.mobModelo.archiMob
                if (archiMob != null) {
                    if (archiMob.puedeSubArea(Objects.requireNonNull(mapa.subArea)!!.id)) {
                        val prob = archiMob.probabilidadAparecer
                        if (prob == -1 || prob >= 100 || prob >= getRandomInt(1, 100)) {
                            mobGrado = archiMob.getGradoPorGrado(mg!!.grado)
                            archi = true
                        }
                    }
                }
            }
            if (Mundo.MOB_EVENTO > 0) {
                val array = Mundo.mobsEventoDelDia
                if (array != null && array.isNotEmpty()) {
                    for (duo in array) {
                        if (duo._primero == idMobModelo) {
                            try {
                                mobGrado = Mundo.getMobModelo(duo._segundo)?.randomGrado
                            } catch (ignored: Exception) {
                            }
                        }
                    }
                }
            }
            if (mobGrado == null) {
                continue
            }
            if (mobGrado.nivel > maxNivel) {
                maxNivel = mobGrado.nivel.toInt()
            }
            if (_almas.containsKey(idMobModelo)) {
                val valor = _almas[idMobModelo]!!
                _almas.remove(idMobModelo)
                _almas[idMobModelo] = valor + 1
            } else {
                _almas[idMobModelo] = 1
            }
            if (alineacion == Constantes.ALINEACION_NULL) {
                alineacion = mobGrado.mobModelo.alineacion
            }
            if (distAgresion < mobGrado.mobModelo.distAgresion) {
                distAgresion = mobGrado.mobModelo.distAgresion
            }
            if (str.isNotEmpty()) {
                str.append(";")
            }
            str.append(mobGrado.idModelo).append(",").append(mobGrado.nivel.toInt()).append(",")
                .append(mobGrado.nivel.toInt())
            mobs.add(mobGrado)
        }
        if (mobs.isEmpty()) {
            return
        }
        id = mapa.sigIDGrupoMob()
        if (distAgresion.toInt() == 0) {
            distAgresion = Constantes.distAgresionPorNivel(maxNivel)
        }
        if (alineacion == Constantes.ALINEACION_BONTARIANO || alineacion == Constantes.ALINEACION_BRAKMARIANO) {
            distAgresion = 10
        } else if (alineacion == Constantes.ALINEACION_NEUTRAL) {
            distAgresion = 30
        }
        _orientacion = (getRandomInt(0, 3) * 2 + 1).toByte()
        strGrupoMob = str.toString()
    }

    constructor(mapa: Mapa, celdaID: Short, strGrupoMob: String, tipo: TipoGrupo, condiciones: String) {
        this.celdaID = if (celdaID.toInt() == -1) mapa.randomCeldaIDLibre else celdaID
        this.tipo = tipo
        if (celdaID.toInt() == 0) {
            return
        }
        fijo = true
        this.strGrupoMob = strGrupoMob
        var maxNivel = 0
        val grados: MutableList<Byte> = ArrayList()
        for (data in strGrupoMob.split(";".toRegex()).toTypedArray()) {
            try {
                val infos = data.split(",".toRegex()).toTypedArray()
                val idMobModelo = infos[0].toInt()
                val mobModelo = Mundo.getMobModelo(idMobModelo)
                var min = 0
                var max = 0
                try {
                    min = infos[1].toInt()
                } catch (ignored: Exception) {
                }
                try {
                    max = infos[2].toInt()
                } catch (ignored: Exception) {
                }
                grados.clear()
                if (mobModelo != null) {
                    for (mob in mobModelo.grados.values) {
                        if (mob.nivel in min..max) {
                            grados.add(mob.grado)
                        }
                    }
                }
                if (mobModelo == null) {
                    continue
                }
                var mob = if (grados.isEmpty()) {
                    mobModelo.randomGrado!!
                } else {
                    val grado = grados[getRandomInt(0, grados.size - 1)]
                    mobModelo.getGradoPorGrado(grado)!!
                }
                if (mob.nivel > maxNivel) {
                    maxNivel = mob.nivel.toInt()
                }
                if (_almas.containsKey(idMobModelo)) {
                    val valor = _almas[idMobModelo]!!
                    _almas.remove(idMobModelo)
                    _almas[idMobModelo] = valor + 1
                } else {
                    _almas[idMobModelo] = 1
                }
                if (alineacion == Constantes.ALINEACION_NULL) {
                    alineacion = mobModelo.alineacion
                }
                if (distAgresion < mobModelo.distAgresion) {
                    distAgresion = mobModelo.distAgresion
                }
                mobs.add(mob)
            } catch (ignored: Exception) {
            }
        }
        if (mobs.isEmpty()) {
            return
        }
        id = mapa.sigIDGrupoMob()
        condInicioPelea = condiciones
        condUnirsePelea = condiciones
        if (distAgresion.toInt() == 0) {
            distAgresion = Constantes.distAgresionPorNivel(maxNivel)
        }
        if (alineacion == Constantes.ALINEACION_BONTARIANO || alineacion == Constantes.ALINEACION_BRAKMARIANO) {
            distAgresion = 10
        } else if (alineacion == Constantes.ALINEACION_NEUTRAL) {
            distAgresion = 30
        }
        _orientacion = (getRandomInt(0, 3) * 2 + 1).toByte()
    }

    fun moverGrupoMob(mapa: Mapa) {
        if (_pelea != null) {
            return
        }
        val celdaDestino = Camino.celdaMoverSprite(mapa, celdaID)
        if (celdaDestino.toInt() == -1) {
            return
        }
        val pathCeldas = Camino.getPathPelea(
            mapa, celdaID, celdaDestino, -1, null,
            false
        ) ?: return
        val celdas = pathCeldas._segundo
        val pathStr = Camino.getPathComoString2(mapa, celdas, celdaID, false)
        if (pathStr.isEmpty()) {
            redactarLogServidorln(
                "Fallo de desplazamiento de mob grupo: camino vacio - MapaID: " + mapa.id
                        + " - CeldaID: " + celdaID
            )
            return
        }
        try {
            Thread.sleep(100)
        } catch (ignored: Exception) {
        }
        ENVIAR_GA_MOVER_SPRITE_MAPA(
            mapa, 0, 1, id.toString() + "", Encriptador.getValorHashPorNumero(_orientacion.toInt())
                .toString() + Encriptador.celdaIDAHash(celdaID) + pathStr
        )
        _orientacion = Camino.getIndexPorDireccion(pathStr[pathStr.length - 3])
        celdaID = celdaDestino
    }

    fun tieneMobModeloID(id: Int, lvlMin: Int, lvlMax: Int): Boolean {
        for (m in mobs) {
            if (m.idModelo == id) {
                if (m.nivel in lvlMin..lvlMax) {
                    return true
                }
            }
        }
        return false
    }

    fun addObjetosKamasInicioServer(heroico: String) {
        val infos = heroico.split(Pattern.quote("|").toRegex()).toTypedArray()
        if (infos.size > 1 && infos[1].isNotEmpty()) {
            for (s in infos[1].split(",".toRegex()).toTypedArray()) {
                try {
                    if (s.isEmpty()) continue
                    addIDObjeto(s.toInt())
                } catch (ignored: Exception) {
                }
            }
        }
        if (infos.size > 2 && infos[2].isNotEmpty()) {
            var kamas: Long = 0
            try {
                kamas = infos[1].toLong()
            } catch (ignored: Exception) {
            }
            addKamasHeroico(kamas)
        }
    }

    fun addKamasHeroico(kamas: Long) {
        if (kamas < 1) {
            return
        }
        kamasHeroico += kamas
        max(0, kamasHeroico)
    }

    fun esHeroico(): Boolean {
        return kamasHeroico > 0 || objetosHeroico.isNotEmpty()
    }

    private fun addIDObjeto(id: Int) {
        if (!objetosHeroico.contains(id)) objetosHeroico.add(id)
    }

    val iDsObjeto: String
        get() {
            val str = StringBuilder()
            for (i in objetosHeroico) {
                if (str.isNotEmpty()) {
                    str.append(",")
                }
                str.append(i)
            }
            return str.toString()
        }

    fun cantObjHeroico(): Int {
        return objetosHeroico.size
    }

    fun borrarObjetosHeroico() {
        objetosHeroico.clear()
    }

    fun addObjAInventario(objeto: Objeto) {
        if (objetosHeroico.contains(objeto.id)) {
            return
        }
        // tipo piedra de alma y mascota
        if (objeto.puedeTenerStatsIguales()) {
            for (id in objetosHeroico) {
                val obj = Mundo.getObjeto(id) ?: continue
                if (Constantes.esPosicionEquipamiento(obj.posicion)) {
                    continue
                }
                if (objeto.id != obj.id && obj.objModeloID == objeto.objModeloID && obj.sonStatsIguales(
                        objeto
                    )
                ) {
                    obj.cantidad = obj.cantidad + objeto.cantidad
                    if (objeto.id > 0) {
                        Mundo.eliminarObjeto(objeto.id)
                    }
                    return
                }
            }
        }
        if (objeto.id == 0) {
            Mundo.addObjeto(objeto, false)
        } else {
            SALVAR_OBJETO(objeto)
        }
        addIDObjeto(objeto.id)
    }

    fun puedeTimerReaparecer(mapa: Mapa, grupoMob: GrupoMob?, i: Aparecer?) {
        if (tipo == TipoGrupo.SOLO_UNA_PELEA) {
            return
        }
        when (i) {
            Aparecer.INICIO_PELEA -> mapa.addSiguienteGrupoMob(grupoMob, true)
            Aparecer.FINAL_PELEA -> mapa.addUltimoGrupoMob(grupoMob, true)
        }
    }

    fun enPelea(): Boolean {
        return _pelea != null
    }

    fun estaMuerto(): Boolean {
        return _muerto
    }

    fun setMuerto(muerto: Boolean) {
        _muerto = muerto
    }

    var bonusEstrellas: Int
        get() {
            bonusEstrellas = _bonusEstrellas
            return max(0, _bonusEstrellas)
        }
        set(estrellas) {
            _bonusEstrellas = estrellas
            if (_bonusEstrellas < AtlantaMain.INICIO_BONUS_ESTRELLAS_MOBS) {
                _bonusEstrellas = AtlantaMain.INICIO_BONUS_ESTRELLAS_MOBS
            }
            if (_bonusEstrellas > AtlantaMain.MAX_BONUS_ESTRELLAS_MOBS) {
                _bonusEstrellas = if (AtlantaMain.PARAM_REINICIAR_ESTRELLAS_SI_LLEGA_MAX) {
                    AtlantaMain.INICIO_BONUS_ESTRELLAS_MOBS
                } else {
                    AtlantaMain.MAX_BONUS_ESTRELLAS_MOBS
                }
            }
        }

    fun realBonusEstrellas(): Int {
        return _bonusEstrellas
    }

    fun subirBonusEstrellas(cant: Int) {
        bonusEstrellas = _bonusEstrellas + cant
    }

    var orientacion: Byte
        get() = _orientacion
        set(o) {
            _orientacion = 0
        }

    fun agredePersonaje(perso: Personaje?): Boolean {
        var nivelmax = 1
        val nivelperso = perso?.nivel
        for (mob in mobs) {
            if (nivelmax < mob.nivel.toInt()) {
                nivelmax = mob.nivel.toInt()
            }
        }
        return nivelmax >= nivelperso!!
    }

    // public MobGrado getMobGradoPorID(final int id) {
// return _mobsGradoMod.get(id);
// }
    val cantMobs: Int
        get() = mobs.size

    fun stringGM(): String {
        if (mobs.isEmpty()) {
            return ""
        }
        val mobIDs = StringBuilder()
        val mobGFX = StringBuilder()
        val mobNiveles = StringBuilder()
        val colorAccesorios = StringBuilder()
        val forma = if (randomBoolean) "," else ":"
        var totalExp: Long = 0
        for (mob in mobs) {
            if (mobIDs.isNotEmpty()) {
                mobIDs.append(",")
                mobGFX.append(forma)
                mobNiveles.append(",")
            }
            mobIDs.append(mob.mobModelo.id)
            mobGFX.append(mob.mobModelo.gfxID.toInt()).append("^").append(mob.mobModelo.talla.toInt())
            mobNiveles.append(mob.nivel.toInt())
            totalExp += mob.baseXp.toLong()
        }
        totalExp = (totalExp * (bonusEstrellas / 100f + AtlantaMain.RATE_XP_PVM)).toLong()
        for (mob in mobs) {
            if (colorAccesorios.isNotEmpty()) {
                colorAccesorios.append(";")
            }
            colorAccesorios.append(mob.mobModelo.colores)
            colorAccesorios.append(";")
            // colorAccesorios.append("accesorios");
        }
        val s = StringBuilder()
        s.append(celdaID.toInt()).append(";").append(_orientacion.toInt()).append(";").append(bonusEstrellas)
            .append(";").append(id).append(";").append(mobIDs.toString()).append(";-3;").append(mobGFX.toString())
            .append(";").append(mobNiveles.toString()).append(";")
        if (AtlantaMain.PARAM_MOSTRAR_EXP_MOBS) {
            s.append(totalExp)
        }
        s.append(";").append(colorAccesorios.toString())
        return s.toString()
    }

    val almasMobs: Map<Int, Int?>
        get() = _almas

    fun startTiempoCondicion() {
        _timer = Timer()
        _timer!!.schedule(object : TimerTask() {
            override fun run() {
                _timer!!.cancel()
                condInicioPelea = ""
                condUnirsePelea = ""
            }
        }, AtlantaMain.SEGUNDOS_ARENA * 1000.toLong())
    }

    // public void startTimerRespawn(final Mapa mapa) {
// final GrupoMob g = this;
// _timer = new Timer();
// _timer.schedule(new TimerTask() {
// public void run() {
// _timer.cancel();
// mapa.addSigGrupoMobRespawn(g);
// }
// }, _segundosRespawn * 1000);
// }
    fun setPelea(pelea: Pelea?) {
        _pelea = pelea
    }
}

private fun <E> addAll(elements: ArrayList<E?>) {

}
