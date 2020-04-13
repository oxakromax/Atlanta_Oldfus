package variables.pelea

import estaticos.*
import estaticos.Formulas.getRandomInt
import estaticos.GestorSalida.ENVIAR_As_STATS_DEL_PJ
import estaticos.GestorSalida.ENVIAR_GA950_ACCION_PELEA_ESTADOS
import estaticos.GestorSalida.ENVIAR_GA998_AGREGAR_BUFF_PELEA
import estaticos.GestorSalida.ENVIAR_GA_ACCION_PELEA
import estaticos.GestorSalida.ENVIAR_GA_ACCION_PELEA_LUCHADOR
import estaticos.GestorSalida.ENVIAR_GIC_APARECER_LUCHADORES_INVISIBLES
import estaticos.GestorSalida.ENVIAR_GIe_QUITAR_BUFF
import estaticos.Mundo.Duo
import sprites.PreLuchador
import variables.gremio.Recaudador
import variables.hechizo.Buff
import variables.hechizo.EfectoHechizo.TipoDaño
import variables.hechizo.HechizoLanzado
import variables.hechizo.StatHechizo
import variables.mapa.Celda
import variables.mob.MobGrado
import variables.objeto.Objeto
import variables.pelea.Pelea.Companion.getStrParaGA998
import variables.pelea.Reto.EstReto
import variables.personaje.Personaje
import variables.stats.Stats
import variables.stats.TotalStats
import variables.zotros.Prisma
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

class Luchador(pelea: Pelea, pre: PreLuchador, espectador: Boolean) {
    val id: Int
    val nivelViejo: Int
    val pelea: Pelea
    val totalStats: TotalStats
    val hechizosLanzados = ArrayList<HechizoLanzado>()
    val hechizosLanzadosReto = ArrayList<Int>()
    val mobsAsesinadosReto = ArrayList<Int>()
    private val _visibles = ArrayList<Int>()
    val buffsPelea = CopyOnWriteArrayList<Buff>()
    // private final ArrayList<Buff> _buffsCond = new ArrayList<Buff>();
    private val _estados: MutableMap<Int, Int> = TreeMap()
    val bonusCastigo: MutableMap<Int, Int?> = TreeMap()
    @JvmField
    var retrocediendo = false
    var tiempoempujado: Long = 0
    @JvmField
    var empujo = false
    private var _estaMuerto = false
    private var _estaRetirado = false
    private var _puedeJugar = false
    private var _contaminado = false
    private var _desconectado = false
    private var _estatico = false
    // FIXME aun no se para q sirve esto
    var sirveParaBuff = true
    private var _esBomba = false
    private var _esDoble = false
    private var _idReal = true
    private var _listo = false
    private var _espectadorAdmin = false
    private var _esAbonado = false
    private var _saqueado = false
    var msjMuerto = false
    var updateGTM = false
    var direccion = 'b'
    private var _tipoLuch: Byte = 0
    // 0 = agresor
// 1 = agredido
    var equipoBin: Byte = -2
    var turnosParaMorir: Byte = 0
        private set
    var turnosRestantes: Byte = 20
        private set
    var alineacion = Constantes.ALINEACION_NULL
    var ultimoElementoDaño = Constantes.ELEMENTO_NULO.toInt()
    var pdvMaxSinBuff: Int = 0
    var pdvSinBuff: Int = 0
    var gfxID: Int = 0
    var nroInvocaciones = 0
    var idHechizoLanzado = -1
    var idCeldaInicioTurno = 0
    var luchQueAtacoUltimo = 0
    var prospeccionLuchador = 100
        private set
    private var _escudo = 0
    private var _colorNombre = -1
    var distMinAtq = -1
    private var _PArestantes = 0
    private var _PMrestantes = 0
    var paUsados = 0
        private set
    var pmUsados = 0
        private set
    private var _kamasGanadas: Long = 0
    private var _xpGanada: Long = 0
    var bonusAlinExp = 0f
    var bonusAlinDrop = 0f
    private var _celda: Celda? = null
    var transportando: Luchador? = null
    var portador: Luchador? = null
        private set
    var invocador: Luchador? = null
    var muertoPor: Luchador? = null
    var preLuchador: PreLuchador? = null
    private val _bombas: ArrayList<Luchador>? = null
    var stringBuilderGTM = StringBuilder()
        private set
    var ia: Inteligencia? = null
        private set
    var nombre // , _strGMLuchador;
            : String? = null
    private var _objDropeados: MutableMap<Objeto, Boolean>? = null

    var paRestantes: Int
        get() = _PArestantes
        private set(p) {
            _PArestantes = p
            updateGTM = true
        }

    var pmRestantes: Int
        get() = _PMrestantes
        private set(p) {
            val oldPM = _PMrestantes
            _PMrestantes = p
            updateGTM = true
            if (oldPM != _PMrestantes) {
                if (ia != null) {
                    ia!!.forzarRefrescarMov()
                }
            }
        }

    fun addPARestantes(p: Int): Int {
        var r = p
        if (r > 0) {
            if (_PArestantes < 0) {
                r += _PArestantes
            }
        }
        paRestantes = _PArestantes + p
        return r
    }

    fun addPMRestantes(p: Int): Int {
        var r = p
        if (r > 0) {
            if (_PMrestantes < 0) {
                r += _PMrestantes
            }
        }
        pmRestantes = _PMrestantes + p
        return r
    }

    fun addPAUsados(p: Int) {
        paUsados += p
        if (paUsados < 0) {
            paUsados = 0
        }
    }

    fun addPMUsados(p: Int) {
        pmUsados += p
        if (pmUsados < 0) {
            pmUsados = 0
        }
    }

    fun resetPuntos() {
        val statsLuch = totalStats
        paRestantes = statsLuch.getTotalStatParaMostrar(Constantes.STAT_MAS_PA)
        pmRestantes = statsLuch.getTotalStatParaMostrar(Constantes.STAT_MAS_PM)
        paUsados = 0
        pmUsados = 0
    }

    val comandoPasarTurno: Boolean
        get() = if (personaje != null) {
            personaje!!.comandoPasarTurno
        } else false

    fun setSaqueado(b: Boolean) {
        _saqueado = b
    }

    fun fueSaqueado(): Boolean {
        return _saqueado
    }

    private fun limpiarStatsBuffs() {
        totalStats.statsBuff!!.clear()
    }

    fun addKamasGanadas(kamas: Long) {
        kamasGanadas += kamas
        preLuchador!!.addKamasGanada(kamas)
    }

    fun addXPGanada(xp: Long) {
        expGanada += xp
        preLuchador!!.addXPGanada(xp)
    }

    fun esNoIA(): Boolean {
        return ia == null
    }

    fun estaListo(): Boolean {
        return _listo
    }

    fun setListo(listo: Boolean) {
        _listo = listo
    }

    fun esIDReal(): Boolean {
        return _idReal
    }

    fun esMultiman(): Boolean {
        return if (personaje == null) {
            false
        } else personaje!!.esMultiman()
    }

    fun setIDReal(b: Boolean) {
        _idReal = b
    }

    fun esIAChafer(): Boolean {
        return if (ia == null) {
            false
        } else ia!!.tipoIA == 11
    }

    // bandera desafio
    // bandera de recaudador
    // bandera mobs
    // bandera pj
    val flag: Byte
        get() = when (_tipoLuch.toInt()) {
            0 -> 2 // bandera desafio
            5 -> 3 // bandera de recaudador
            4 -> 1 // bandera mobs
            else -> 0 // bandera pj
        }

    fun resetStringBuilderGTM() {
        stringBuilderGTM = StringBuilder()
    }

    fun borrarBomba(bomba: Luchador?) {
        _bombas!!.remove(bomba)
    }

    fun addBomba(bomba: Luchador) {
        if (_bombas!!.size < 3) {
            _bombas.add(bomba)
        }
    }

    fun esBomba(): Boolean {
        return _esBomba
    }

    fun setBomba(bomba: Boolean) {
        _esBomba = bomba
    }

    fun addEscudo(escudo: Int) {
        _escudo += escudo
        if (_escudo < 0) {
            _escudo = 0
        }
    }

    fun esInvisible(idMirador: Int): Boolean {
        if (idMirador != 0) {
            if (idMirador == id || _visibles.contains(idMirador)) {
                return false
            }
        }
        return tieneBuff(150)
    }

    fun hacerseVisible() {
        for (buff in buffsPelea) {
            if (buff.condicionBuff.isNotEmpty()) {
                continue
            }
            if (buff.efectoID == 150) {
                removeBuff(buff)
            }
        }
        ENVIAR_GA_ACCION_PELEA(pelea, 7, 150, id.toString() + "", "$id,0")
        ENVIAR_GIC_APARECER_LUCHADORES_INVISIBLES(pelea, 7, this)
    }

    fun aparecer(mostrar: Luchador) {
        _visibles.add(mostrar.id)
        ENVIAR_GA_ACCION_PELEA_LUCHADOR(mostrar, 150, id.toString() + "", "$id,0")
        ENVIAR_GIC_APARECER_LUCHADORES_INVISIBLES(mostrar, id.toString() + ";" + _celda!!.id)
    }

    fun vaciarVisibles() {
        _visibles.clear()
    }

    fun aplicarBuffInicioTurno(pelea: Pelea?) {
        for (buff in buffsPelea) {
            if (buff.condicionBuff.isNotEmpty()) {
                continue
            }
            buff.aplicarBuffDeInicioTurno(pelea, this)
        }
    }

    fun getBuff(id: Int): Buff? {
        for (buff in buffsPelea) {
            if (buff.condicionBuff.isNotEmpty()) {
                continue
            }
            if (buff.efectoID == id) {
                return buff
            }
        }
        return null
    }

    fun tieneBuff(id: Int): Boolean {
        return getBuff(id) != null
    }

    fun getBuffsPorEfectoID(efectotID: Int): ArrayList<Buff> {
        val buffs = ArrayList<Buff>()
        for (buff in buffsPelea) {
            if (buff.condicionBuff.isNotEmpty()) {
                continue
            }
            if (buff.efectoID == efectotID) {
                buffs.add(buff)
            }
        }
        return buffs
    }

    private fun getBuffPorHechizoYEfecto(hechizoID: Int, efectoID: Int): Buff? {
        for (buff in buffsPelea) {
            if (buff.condicionBuff.isNotEmpty()) {
                continue
            }
            if (buff.hechizoID == hechizoID && (efectoID == 0 || efectoID == buff.efectoID)) {
                return buff
            }
        }
        return null
    }

    fun tieneBuffPorHechizoYEfecto(hechizoID: Int, efectoID: Int): Boolean {
        return getBuffPorHechizoYEfecto(hechizoID, efectoID) != null
    }

    fun getValorPorBuffsID(efectoID: Int): Int {
        var valor = 0
        for (buff in buffsPelea) {
            if (buff.condicionBuff.isNotEmpty()) {
                continue
            }
            if (buff.efectoID == efectoID) {
                if (efectoID == 106 || efectoID == 750) { // reenvio de hechizo y efecto de captura de almas
                    if (buff.primerValor > valor) {
                        valor = buff.primerValor
                    }
                } else {
                    valor += buff.primerValor
                }
            }
        }
        return valor
    }

    fun getValorPorPrimerYEfectoID(efectoID: Int, primerValor: Int): Int {
        var valor = 0
        for (buff in buffsPelea) {
            if (buff.condicionBuff.isNotEmpty()) {
                continue
            }
            if (buff.efectoID == efectoID && buff.primerValor == primerValor) {
                valor += buff.segundoValor
            }
        }
        return valor
    }

    // la conmbinacion de GTF , GTM y GTS realiza la disminucion de turnos de los buffs
    fun disminuirBuffsPelea() {
        disminuirEstados()
        if (!buffsPelea.isEmpty()) {
            loop@ for (buff in buffsPelea) {
                val turnosRestantes = buff.disminuirTurnosRestantes()
                if (turnosRestantes <= -1) {
                    continue
                }
                if (turnosRestantes == 0) {
                    when (buff.efectoID) {
                        Constantes.STAT_MAS_VITALIDAD -> if (buff.hechizoID != 441) {
                            continue@loop
                        }
                        422 -> addEscudo(-buff.primerValor)
                        150 -> hacerseVisible()
                    }
                    removeBuff(buff)
                }
            }
            actualizarBuffStats()
            if (pdvConBuff <= 0) {
                pelea.addMuertosReturnFinalizo(this, null)
            }
        }
    }

    fun resetearBuffs(buffs: ArrayList<Buff>) {
        addNuevosBuffs(buffs)
        for (buff in buffsPelea) {
            if (buff.condicionBuff.isNotEmpty()) {
                continue
            }
            ENVIAR_GA998_AGREGAR_BUFF_PELEA(
                pelea, 7, getStrParaGA998(
                    buff.efectoID, id, buff
                        .getTurnosRestantes(false), buff.hechizoID, buff.args
                )
            )
        }
    }

    private fun addNuevosBuffs(buffs: ArrayList<Buff>) {
        updateGTM = true
        buffsPelea.clear()
        buffsPelea.addAll(buffs)
        actualizarBuffStats()
    }

    private fun addBuff(buff: Buff) {
        updateGTM = true
        buffsPelea.add(buff)
        actualizarBuffStats()
    }

    private fun removeBuff(buff: Buff) { // solo lo usa para quitar invisbilidad
        updateGTM = true
        buffsPelea.remove(buff)
    }

    private fun actualizarBuffStats() { // refresh buffs, refrescar buffs
        limpiarStatsBuffs()
        for (buff in buffsPelea) {
            if (buff.condicionBuff.isNotEmpty()) {
                continue
            }
            totalStats.statsBuff!!.addStatID(buff.efectoID, buff.primerValor)
        }
    }

    fun paraDeshechizar(equipoBin: Int): Boolean {
        var i = 0
        for (buff in buffsPelea) {
            if (buff.condicionBuff.isNotEmpty()) {
                continue
            }
            i += Constantes.estimaDaño(buff.efectoID)
        }
        if (equipoBin != equipoBin) {
            i = -i
        }
        return i > 0
    }

    fun clonarLuchador(id: Int): Luchador? {
        var ret: Luchador? = null
        ret = if (preLuchador!!::class.java == Personaje::class.java) {
            Luchador(pelea, Personaje.crearClon(personaje, id), false)
        } else if (preLuchador!!::class.java == MobGrado::class.java) {
            Luchador(pelea, mob!!.mobGradoModelo.invocarMob(id, true, this), false)
        } else {
            return null
        }
        ret._esDoble = true
        ret.setInteligenciaArtificial(Inteligencia(ret, pelea))
        return ret
    }

    val hechizos: Map<Int, StatHechizo>
        get() = preLuchador!!.Hechizos

    fun setBonusCastigo(bonus: Int, stat: Int) {
        bonusCastigo[stat] = bonus
    }

    fun getBonusCastigo(stat: Int): Int {
        var bonus = 0
        if (bonusCastigo.containsKey(stat)) {
            bonus = bonusCastigo[stat]!!
        }
        return bonus
    }

    // public int getTipo() {
// return _tipo;
// }
    fun setEstatico(estatico: Boolean) {
        _estatico = estatico
    }

    fun esEstatico(): Boolean {
        return _estatico
    }

    fun actualizaHechizoLanzado() {
        val copia = ArrayList(hechizosLanzados)
        for (HL in copia) {
            HL.actuSigLanzamiento()
            if (HL.sigLanzamiento <= 0) {
                hechizosLanzados.remove(HL)
            }
        }
        copia.clear()
    }

    fun addHechizoLanzado(lanzador: Luchador?, hechizo: StatHechizo?, objetivo: Luchador?) {
        if (hechizo != null) {
            if (lanzador != null) {
                hechizosLanzados.add(HechizoLanzado(lanzador, hechizo, objetivo?.id ?: 0))
            }
        } else {
            AtlantaMain.redactarLogServidor("Algo salio mal en addHechizoLanzado en Luchador.kt")
        }
    }

    val tipoIA: Int
        get() = if (ia == null) -1 else ia!!.tipoIA

    fun setTransportadoPor(transportadoPor: Luchador?) {
        portador = transportadoPor
    }

    var celdaPelea: Celda?
        get() = _celda
        set(celda) {
            _celda = celda
            if (_celda != null) {
                _celda!!.addLuchador(this)
            }
            for (l in pelea.luchadoresDeEquipo(3)) {
                if (l.ia != null) {
                    l.ia!!.forzarRefrescarMov()
                }
            }
        }

    fun estaMuerto(): Boolean {
        return _estaMuerto
    }

    fun setEstaMuerto(m: Boolean) {
        _estaMuerto = m
    }

    fun estaRetirado(): Boolean {
        return _estaRetirado
    }

    fun puedeGolpeCritico(SH: StatHechizo): Boolean { // formula de golpes criticos
        var probGC = SH.probabilidadGC.toInt()
        if (probGC < 2) {
            return false
        }
        if (tieneBuff(781)) { // mala sombra
            return false
        }
        val statsConBuff = totalStats
        var agilidad = statsConBuff.getTotalStatParaMostrar(Constantes.STAT_MAS_AGILIDAD)
        if (agilidad < 0) {
            agilidad = 0
        }
        if (personaje != null) {
            if (personaje!!.tieneModfiSetClase(SH.hechizoID)) {
                val modi = personaje!!.getModifSetClase(SH.hechizoID, 287)
                probGC -= modi
            }
        }
        probGC = ((probGC - statsConBuff.getTotalStatParaMostrar(Constantes.STAT_MAS_GOLPES_CRITICOS)) * (1.1 * Math.E
                / ln(agilidad + 12.toDouble()))).toInt()
        if (probGC < 2) {
            probGC = 2
        }
        val jet = getRandomInt(1, probGC)
        return jet == probGC
    }

    fun puedeFalloCritico(SH: StatHechizo): Boolean {
        var probFC = SH.probabilidadFC.toInt()
        if (probFC < 2) {
            return false
        }
        val statsConBuff = totalStats
        probFC -= statsConBuff.getTotalStatParaMostrar(Constantes.STAT_MAS_FALLOS_CRITICOS)
        if (probFC < 2) {
            probFC = 2
        }
        val jet = getRandomInt(1, probFC)
        return jet == probFC
    }

    val baseStats: Stats?
        get() = totalStats.statsBase

    val objetosStats: Stats?
        get() = totalStats.statsObjetos

    val buffsStats: Stats?
        get() = totalStats.statsBuff

    fun stringGM(idMirador: Int): String {
        val str = StringBuilder()
        str.append(if (idMirador != 0 && esInvisible(idMirador)) 0 else _celda!!.id.toInt()).append(";")
        str.append(Camino.getIndexPorDireccion(direccion).toInt()).append(";") // direccion
        str.append("0" + "^").append(_esAbonado).append(";") // estrellas bonus
        str.append(id).append(";")
        str.append(nombre).append("^").append(_colorNombre).append(";")
        str.append(preLuchador!!.stringGMLuchador()) // ex _strGMLuchador
        str.append(pdvConBuff).append(";")
        str.append(_PArestantes).append(";") // PA
        str.append(_PMrestantes).append(";") // PM
        var resist = ""
        resist = when (pelea.tipoPelea.toByte()) {
            Constantes.PELEA_TIPO_DESAFIO, Constantes.PELEA_TIPO_KOLISEO, Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_RECAUDADOR -> {
                Constantes.STAT_MAS_RES_PORC_PVP_NEUTRAL.toString() + "," + Constantes.STAT_MAS_RES_PORC_PVP_TIERRA + "," +
                        Constantes.STAT_MAS_RES_PORC_PVP_FUEGO + "," + Constantes.STAT_MAS_RES_PORC_PVP_AGUA + "," +
                        Constantes.STAT_MAS_RES_PORC_PVP_AIRE
            }
            else -> {
                Constantes.STAT_MAS_RES_PORC_NEUTRAL.toString() + "," + Constantes.STAT_MAS_RES_PORC_TIERRA + "," +
                        Constantes.STAT_MAS_RES_PORC_FUEGO + "," + Constantes.STAT_MAS_RES_PORC_AGUA + "," +
                        Constantes.STAT_MAS_RES_PORC_AIRE
            }
        }
        resist += "," + Constantes.STAT_MAS_ESQUIVA_PERD_PA + "," + Constantes.STAT_MAS_ESQUIVA_PERD_PM
        for (r in resist.split(",".toRegex()).toTypedArray()) {
            val statID = r.toInt()
            val total = totalStats.getTotalStatConComplemento(statID)
            str.append(total).append(";")
        }
        str.append(equipoBin.toInt()).append(";")
        var perso: Personaje? = null
        if (preLuchador!!::class.java == Personaje::class.java) {
            perso = preLuchador as Personaje?
        }
        if (perso != null) {
            if (perso.estaMontando() && perso.montura != null) {
                str.append(perso.montura.getStringColor(perso.stringColor()))
            }
            str.append(";")
        }
        str.append(totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_HUIDA)).append(";")
        str.append(totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_PLACAJE)).append(";")
        return str.toString()
    }

    val pdvMaxConBuff: Int
        get() = pdvMaxSinBuff + buffsStats!!.getStatParaMostrar(Constantes.STAT_MAS_VITALIDAD)

    val pdvConBuff: Int
        get() = pdvSinBuff + buffsStats!!.getStatParaMostrar(Constantes.STAT_MAS_VITALIDAD)

    fun restarPDV(pdv: Int) { // positivo = restar vida, negativo = curar}
        var pdv = pdv
        if (pdv > 0) {
            if (_escudo > 0) {
                val escudo = _escudo
                addEscudo(-pdv)
                pdv -= escudo
                if (pdv < 0) {
                    return
                }
            }
        }
        setPDV(pdvSinBuff - pdv)
        if (pdv > 0) {
            var pdvMax = pdvMaxSinBuff
            pdvMax -= floor(pdv * AtlantaMain.PORCENTAJE_DAÑO_NO_CURABLE / 100.toDouble()).toInt()
            if (pdvMax < 1) {
                pdvMax = 1
            }
            setPDVMAX(pdvMax, false)
            if (pelea.retos != null && !_esDoble && esNoIA()) {
                for ((retoID, reto) in pelea.retos!!) {
                    var exitoReto = reto.estado
                    if (exitoReto !== EstReto.EN_ESPERA) {
                        continue
                    }
                    when (retoID) {
                        Constantes.RETO_INTOCABLE -> exitoReto = EstReto.FALLADO
                        Constantes.RETO_CONTAMINACION -> setContaminado(true)
                    }
                    reto.estado = exitoReto
                }
            }
        }
    }

    fun setPDV(pdv: Int) {
        pdvSinBuff = pdv
        if (pdvSinBuff > pdvMaxSinBuff) {
            pdvSinBuff = pdvMaxSinBuff
        }
    }

    fun setPDVMAX(pdvMax: Int, conPorc: Boolean) {
        var porc = 0
        if (pdvMaxSinBuff != 0) {
            porc = pdvSinBuff * 100 / pdvMaxSinBuff
        }
        val max = pdvMax >= pdvMaxSinBuff
        pdvMaxSinBuff = pdvMax
        if (pdvSinBuff > pdvMaxSinBuff) {
            pdvSinBuff = pdvMaxSinBuff
        }
        if (!conPorc) {
            return
        }
        var newPDV = pdvMaxSinBuff * porc / 100
        newPDV = if (max) {
            max(pdvSinBuff, newPDV)
        } else {
            min(pdvSinBuff, newPDV)
        }
        setPDV(newPDV)
    }

    val porcPDV: Float
        get() {
            val vitalidad = buffsStats!!.getStatParaMostrar(Constantes.STAT_MAS_VITALIDAD)
            if (pdvMaxSinBuff + vitalidad <= 0) {
                return 0F
            }
            var porc = (pdvSinBuff + vitalidad) * 100f / (pdvMaxSinBuff + vitalidad)
            porc = max(0f, porc)
            porc = min(100f, porc)
            return porc
        }

    fun setEstado(estado: Int, turnos: Int) {
        if (!sirveParaBuff) {
            return
        }
        if (turnos != 0) {
            if (_estados[estado] != null) {
                if (_estados[estado] == -1 || _estados[estado]!! > turnos) { // no hace nada, porq es infinito o mayor al actual
                    return
                } else {
                    _estados[estado] = turnos
                }
            } else {
                _estados[estado] = turnos
            }
        } else {
            if (_estados[estado] == null) {
                return
            }
            _estados.remove(estado)
        }
        ENVIAR_GA950_ACCION_PELEA_ESTADOS(pelea, 7, id, estado, turnos != 0)
    }

    fun tieneEstado(id: Int): Boolean { // return _estados.get(id) != null;
        return if (_estados[id] == null) {
            false
        } else _estados[id] != 0
    }

    private fun disminuirEstados() {
        val copia: MutableMap<Int, Int> = TreeMap()
        for ((key, value) in _estados) {
            if (value <= 0) {
                copia[key] = value
                continue
            }
            val nVal = value - 1
            if (nVal == 0) {
                ENVIAR_GA950_ACCION_PELEA_ESTADOS(pelea, 7, id, key, false)
                continue
            }
            copia[key] = nVal
        }
        _estados.clear()
        _estados.putAll(copia)
    }

    @Synchronized
    fun deshechizar(luchador: Luchador?, desbuffTodo: Boolean) { // desbuffear
// if idLanzador es 0, deshechiza normal
        if (!buffsPelea.isEmpty()) {
            var tiene = false
            val nuevosBuffs = ArrayList<Buff>()
            for (buff in buffsPelea) {
                if (!buff.esDesbufeable() || AtlantaMain.HECHIZOS_NO_DESHECHIZABLE.contains(buff.hechizoID)) {
                    nuevosBuffs.add(buff)
                    continue
                }
                if (buff.condicionBuff.isNotEmpty()) {
                    continue
                }
                if (!desbuffTodo) {
                    if (luchador != null && buff.lanzador.id != luchador.id) {
                        nuevosBuffs.add(buff)
                        continue
                    }
                }
                tiene = true
                var valor = buff.primerValor
                when (buff.efectoID) {
                    111, 120 -> {
                        valor = addPARestantes(-valor)
                        if (valor < 0) {
                            ENVIAR_GA_ACCION_PELEA(
                                pelea, 7, Constantes.STAT_MAS_PA, id.toString() + "", id.toString() + ","
                                        + valor
                            )
                        }
                    }
                    101, 168, 84 -> {
                        valor = addPARestantes(valor)
                        if (valor > 0) {
                            ENVIAR_GA_ACCION_PELEA(
                                pelea, 7, Constantes.STAT_MAS_PA, id.toString() + "", id.toString() + ","
                                        + valor
                            )
                        }
                    }
                    78, 128 -> {
                        valor = addPMRestantes(-valor)
                        if (valor < 0) {
                            ENVIAR_GA_ACCION_PELEA(
                                pelea, 7, Constantes.STAT_MAS_PM, id.toString() + "", id.toString() + ","
                                        + valor
                            )
                        }
                    }
                    127, 169, 77 -> {
                        valor = addPMRestantes(valor)
                        if (valor > 0) {
                            ENVIAR_GA_ACCION_PELEA(
                                pelea, 7, Constantes.STAT_MAS_PM, id.toString() + "", id.toString() + ","
                                        + valor
                            )
                        }
                    }
                    422 -> addEscudo(-buff.primerValor)
                    150 -> hacerseVisible()
                }
            }
            // acaba el for
            if (!desbuffTodo && luchador != null) {
                if (!tiene) {
                    return
                } else {
                    ENVIAR_GIe_QUITAR_BUFF(pelea, 7, id)
                }
            }
            resetearBuffs(nuevosBuffs)
            if (pdvConBuff <= 0) {
                pelea.addMuertosReturnFinalizo(this, luchador)
            } else if (puedeJugar() && !estaRetirado() && personaje != null) {
                ENVIAR_As_STATS_DEL_PJ(personaje!!)
            }
        }
    }

    fun addBuffConGIE(
        efectoID: Int, valor: Int, turnosRestantes: Int, hechizoID: Int,
        args: String, lanzador: Luchador, conGIE: Boolean, tipo: TipoDaño, condicion: String
    ): Duo<Boolean, Buff?> { // se usa para todos menos los de la clase buff porq tienen condicional
        return addBuffConGIE(efectoID, valor, turnosRestantes, hechizoID, args, lanzador, conGIE, tipo, condicion, true)
    }

    private fun addBuffConGIE(
        efectoID: Int, valor: Int, turnosRestantes: Int,
        hechizoID: Int, args: String, lanzador: Luchador, conGIE: Boolean, tipo: TipoDaño,
        condicionHechizo: String, inicioBuff: Boolean
    ): Duo<Boolean, Buff?> {
        var buff: Buff? = null
        var variosGIE = false
        if (sirveParaBuff) {
            variosGIE = true
            var desbufeable = true
            var tempTurnos = turnosRestantes
            if (inicioBuff && tipo != TipoDaño.TRAMPA && puedeJugar()) {
                variosGIE = false
                tempTurnos++
            }
            if (tempTurnos == 0) {
                when (efectoID) {
                    81, 108, 82, 90, 275, 276, 277, 278, 279, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100 -> {
                    }
                    else -> {
                        variosGIE = false
                        tempTurnos = 1
                    }
                }
            }
            when (efectoID) {
                293, 294, 788, Constantes.STAT_MENOS_PORC_PDV_TEMPORAL, Constantes.STAT_DAR_ESTADO -> desbufeable = true
            }
            when (hechizoID) {
                413, 414, 421, 170, 150, 136, 50, 56, 185, 232, 235, 53, 244, 237, 250, 280, 286, 287, 293, 296, 313, 334, 339, 476, 526, 540, 552, 577, 724, 730, 742, 757, 779, 789, 797, 806, 819, 826, 823, 835, 836, 849, 852, 856, 865, 869, 870, 879, 886, 898, 907, 909, 934, 975, 985, 1023, 1024, 1036, 1048, 1063, 1070, 1080, 1085, 1086, 108 -> desbufeable =
                    false
            }
            var estado = false
            if (condicionHechizo.isEmpty()) {
                when (efectoID) {
                    Constantes.STAT_MAS_PM, Constantes.STAT_MAS_PM_2 -> addPMRestantes(valor)
                    Constantes.STAT_MAS_PA, Constantes.STAT_MAS_PA_2 -> addPARestantes(valor)
                    Constantes.STAT_MENOS_PM_FIJO, Constantes.STAT_MENOS_PM -> addPMRestantes(-valor)
                    Constantes.STAT_MENOS_PA_FIJO, Constantes.STAT_MENOS_PA -> addPARestantes(-valor)
                    Constantes.STAT_QUITAR_ESTADO -> {
                        tempTurnos = 0
                        setEstado(valor, tempTurnos)
                        estado = true
                    }
                    Constantes.STAT_DAR_ESTADO -> {
                        setEstado(valor, tempTurnos)
                        estado = true
                    }
                }
            }
            if (!estado) {
                buff = Buff(
                    if (efectoID == 424) 153 else efectoID,
                    hechizoID,
                    desbufeable,
                    tempTurnos,
                    lanzador,
                    args,
                    tipo
                )
                addBuff(buff)
            }
            if (condicionHechizo.isNotEmpty()) {
                buff!!.setCondBuff(condicionHechizo)
            } else if (conGIE || !variosGIE) {
                ENVIAR_GA998_AGREGAR_BUFF_PELEA(
                    pelea, 7, getStrParaGA998(
                        efectoID, id, tempTurnos,
                        hechizoID, args
                    )
                )
            }
        }
        return Duo(variosGIE, buff)
    }

    fun esDoble(): Boolean {
        return _esDoble
    }

    val nivel: Int
        get() = preLuchador!!.Nivel

    val nivelAlineacion: Int
        get() = preLuchador!!.GradoAlineacion

    fun xpStringLuch(str: String): String {
        return if (preLuchador!!::class.java == Personaje::class.java) {
            (preLuchador as Personaje?)!!.stringExperiencia(str)
        } else "0" + str + "0" + str + "0"
    }

    fun addKamasLuchador() {
        if (esInvocacion()) {
            try {
                invocador!!.personaje!!.addKamas(kamasGanadas, false, false)
            } catch (ignored: Exception) {
            }
        } else if (personaje != null) {
            personaje!!.addKamas(kamasGanadas, false, false)
        } else if (recaudador != null) { // nada
        } else if (mob != null) {
            if (equipoBin.toInt() == 1) {
                if (pelea.mobGrupo != null) {
                    pelea.mobGrupo!!.addKamasHeroico(kamasGanadas)
                    pelea.salvarMobHeroico = true
                }
            }
        }
    }

    fun addObjetoAInventario(obj: Objeto?) {
        if (esInvocacion()) {
            try {
                invocador!!.personaje!!.addObjDropPelea(obj, true)
            } catch (ignored: Exception) {
            }
        } else if (personaje != null) {
            personaje!!.addObjDropPelea(obj, true)
        } else if (recaudador != null) {
            if (obj != null) {
                recaudador!!.addObjAInventario(obj)
            }
        } else if (mob != null) {
            if (equipoBin.toInt() == 1) {
                if (pelea.mobGrupo != null) {
                    pelea.mobGrupo!!.addObjAInventario(obj!!)
                    pelea.salvarMobHeroico = true
                }
            }
        }
    }

    fun addDropLuchador(objeto: Objeto, addInventario: Boolean) {
        if (_objDropeados == null) {
            _objDropeados = HashMap()
        }
        // tipo piedra de alma y mascota
        if (objeto.puedeTenerStatsIguales()) {
            for (obj in _objDropeados!!.keys) {
                if (obj == null) {
                    continue
                }
                if (Constantes.esPosicionEquipamiento(obj.posicion)) {
                    continue
                }
                if (obj.objModeloID == objeto.objModeloID && obj.sonStatsIguales(objeto)) {
                    obj.cantidad = obj.cantidad + objeto.cantidad
                    if (objeto.id > 0) {
                        Mundo.eliminarObjeto(objeto.id)
                    }
                    return
                }
            }
        }
        _objDropeados!![objeto] = addInventario
    }

    val personaje: Personaje?
        get() {
            if (_esDoble) {
                return null
            }
            return if (preLuchador!!::class.java == Personaje::class.java) {
                preLuchador as Personaje?
            } else null
        }

    val mob: MobGrado?
        get() = if (preLuchador!!::class.java == MobGrado::class.java) {
            preLuchador as MobGrado?
        } else null

    val recaudador: Recaudador?
        get() = if (preLuchador!!::class.java == Recaudador::class.java) {
            preLuchador as Recaudador?
        } else null

    val prisma: Prisma?
        get() = if (preLuchador!!::class.java == Prisma::class.java) {
            preLuchador as Prisma?
        } else null

    val paramEquipoAliado: Byte
        get() = pelea.getParamMiEquipo(id)

    val paramEquipoEnemigo: Byte
        get() = pelea.getParamEquipoEnemigo(id)

    fun puedeJugar(): Boolean {
        return _puedeJugar
    }

    fun esInvocacion(): Boolean {
        return invocador != null && !pelea.esLuchInicioPelea(this)
    }

    fun esRecaudador(): Boolean {
        return !pelea.esLuchInicioPelea(this)
    }

    fun addNroInvocaciones(add: Int) {
        nroInvocaciones += add
    }

    fun fullPDV() {
        pdvSinBuff = pdvMaxSinBuff
    }

    fun setTurnosRestantes(_turnosRestantes: Int) {
        turnosRestantes = _turnosRestantes.toByte()
    }

    fun estaDesconectado(): Boolean {
        return _desconectado
    }

    fun setDesconectado(_desconectado: Boolean) {
        this._desconectado = _desconectado
    }

    fun esEspectadorAdmin(): Boolean {
        return _espectadorAdmin
    }

    fun setEspectadorAdmin(_espectadorAdmin: Boolean) {
        this._espectadorAdmin = _espectadorAdmin
    }

    fun setPuedeJugar(_puedeJugar: Boolean) {
        this._puedeJugar = _puedeJugar
    }

    fun setInteligenciaArtificial(_IA: Inteligencia?) {
        ia = _IA
    }

    fun addTurnosParaMorir() {
        turnosParaMorir++
    }

    fun estaContaminado(): Boolean {
        return _contaminado
    }

    private fun setContaminado(_contaminado: Boolean) {
        this._contaminado = _contaminado
    }

    var expGanada: Long
        get() = _xpGanada
        set(_expGanada) {
            var _expGanada = _expGanada
            if (_expGanada <= 0) {
                _expGanada = 0
            }
            _xpGanada = _expGanada
        }

    var kamasGanadas: Long
        get() = _kamasGanadas
        set(_kamasGanadas) {
            var _kamasGanadas = _kamasGanadas
            if (_kamasGanadas <= 0) {
                _kamasGanadas = 0
            }
            this._kamasGanadas = _kamasGanadas
        }

    fun setEstaRetirado(b: Boolean) {
        _estaRetirado = b
    }

    val objDropeados: Map<Objeto, Boolean>?
        get() = _objDropeados

    fun setProspeccion(_prospeccion: Int) {
        prospeccionLuchador = _prospeccion
    }

    init {
        preLuchador = pre
        this.pelea = pelea
        id = pre.id
        if (pre.javaClass == Personaje::class.java) {
            _tipoLuch = 1
            nombre = (pre as Personaje).nombre
            _colorNombre = pre.colorNombre
            _esAbonado = pre.esAbonado()
        } else if (pre.javaClass == MobGrado::class.java) {
            _tipoLuch = 4
            // final int IA = ((MobGrado) pre).getMobModelo().getTipoIA();
// if (IA == 0 || IA == 9 || IA == 6) {
// _sirveParaBuff = true;
// }
            setInteligenciaArtificial(Inteligencia(this, this.pelea))
            nombre = (pre as MobGrado).idModelo.toString() + ""
        } else if (pre.javaClass == Recaudador::class.java) {
            _tipoLuch = 5
            setInteligenciaArtificial(Inteligencia(this, this.pelea))
            nombre = (pre as Recaudador).n1 + "," + pre.n2
        } else if (pre.javaClass == Prisma::class.java) {
            _tipoLuch = 2
            setInteligenciaArtificial(Inteligencia(this, this.pelea))
            nombre = (if (pre.Alineacion == Constantes.ALINEACION_BONTARIANO) 1111 else 1112).toString() + ""
        }
        // _strGMLuchador = pre.stringGMLuchador();
        nivelViejo = pre.Nivel
        totalStats = preLuchador!!.TotalStatsPelea!!
        limpiarStatsBuffs()
        resetPuntos()
        if (!espectador) {
            alineacion = pre.Alineacion
            pdvMaxSinBuff = pre.pdvMax
            pdvSinBuff = pre.pdv
            gfxID = pre.getGfxID(false)
        }
    }
}