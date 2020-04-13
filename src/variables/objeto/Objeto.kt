package variables.objeto

import estaticos.AtlantaMain
import estaticos.AtlantaMain.FUN_RANDOM_ITEM
import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Constantes
import estaticos.Constantes.esEfectoHechizo
import estaticos.Constantes.esStatDePelea
import estaticos.Constantes.esStatHechizo
import estaticos.Constantes.esStatRepetible
import estaticos.Constantes.esStatTexto
import estaticos.Constantes.getPesoStat
import estaticos.Constantes.getPorcCrearRuna
import estaticos.Constantes.getPotenciaRunaPorStat
import estaticos.Constantes.getRunaPorStat
import estaticos.Constantes.getStatPositivoDeNegativo
import estaticos.Constantes.getTiempoActualEscala
import estaticos.Constantes.getTiempoDeUnStat
import estaticos.Constantes.getTipoRuna
import estaticos.Constantes.getZonaEfectoArma
import estaticos.Formulas.getRandomDecimal
import estaticos.Formulas.getRandomInt
import estaticos.GestorSalida.ENVIAR_OM_MOVER_OBJETO
import estaticos.Mundo.getCreaTuItem
import estaticos.Mundo.getEncarnacionModelo
import estaticos.Mundo.getMascotaModelo
import estaticos.Mundo.getObjeto
import estaticos.Mundo.getObjetoModelo
import variables.encarnacion.Encarnacion
import variables.hechizo.EfectoHechizo
import variables.objeto.ObjetoModelo.CAPACIDAD_STATS
import variables.objeto.ObjetoModelo.Companion.getStatSegunFecha
import variables.objeto.ObjetoModelo.Companion.getTipoConStatsModelo
import variables.objeto.ObjetoModelo.Companion.statSimiliar
import variables.oficio.Trabajo
import variables.personaje.Personaje
import variables.stats.Stats
import java.util.*
import kotlin.math.ceil

class Objeto {
    var objModelo: ObjetoModelo? = null
        private set
    var posicion = Constantes.OBJETO_POS_NO_EQUIPADO
    var id = 0
    var cantidad: Int = 0
    var objevivoID = 0
        private set
    var objModeloID = 0
        private set
    var durabilidad = -1
        private set
    var durabilidadMax = -1
        private set
    var dueñoTemp = 0
    var precio = 0
    var stats = Stats()
        private set
    var efectosNormales: ArrayList<EfectoHechizo>? = null
        private set
    var encarnacion: Encarnacion? = null
        private set

    // public void destruir() {
// try {
// this.finalize();
// } catch (Throwable e) {}
// }
    constructor() {
        cantidad = 1
        stats = Stats()
    }

    constructor(
        id: Int, idObjModelo: Int, cant: Int, pos: Byte, strStats: String,
        idObjevi: Int, precio: Int
    ) {
        this.id = id
        objModelo = getObjetoModelo(idObjModelo)
        if (objModelo == null) {
            redactarLogServidorln(
                "La id del objeto " + id + " esta bug porque no tiene objModelo "
                        + idObjModelo
            )
            return
        }
        cantidad = cant
        posicion = pos
        stats = Stats()
        objevivoID = idObjevi
        objModeloID = idObjModelo
        this.precio = precio
        if (AtlantaMain.IDS_OBJETOS_STATS_MAXIMOS.contains(idObjModelo)) {
            convertirStringAStats(objModelo!!.generarStatsModelo(CAPACIDAD_STATS.MAXIMO))
        } else if (AtlantaMain.IDS_OBJETOS_STATS_MINIMOS.contains(idObjModelo)) {
            convertirStringAStats(objModelo!!.generarStatsModelo(CAPACIDAD_STATS.MINIMO))
        } else if (AtlantaMain.IDS_OBJETOS_STATS_RANDOM.contains(idObjModelo)) {
            convertirStringAStats(objModelo!!.generarStatsModelo(CAPACIDAD_STATS.RANDOM))
        } else {
            if (AtlantaMain.RATE_RANDOM_ITEM != 1.0) {
                convertirStringAStats(objModelo!!.generarStatsModelo(CAPACIDAD_STATS.MAXIMO))
            } else {
                convertirStringAStats(strStats)
            }
        }
        crearEncarnacion()
    }

    constructor(
        id: Int, idObjModelo: Int, cant: Int, pos: Byte, strStats: String,
        idObjevi: Int, precio: Int, esbase: Boolean
    ) {
        this.id = id
        objModelo = getObjetoModelo(idObjModelo)
        if (objModelo == null) {
            redactarLogServidorln(
                "La id del objeto " + id + " esta bug porque no tiene objModelo "
                        + idObjModelo
            )
            return
        }
        cantidad = cant
        posicion = pos
        stats = Stats()
        objevivoID = idObjevi
        objModeloID = idObjModelo
        this.precio = precio
        if (AtlantaMain.IDS_OBJETOS_STATS_MAXIMOS.contains(idObjModelo)) {
            convertirStringAStats_Base(objModelo!!.generarStatsModelo(CAPACIDAD_STATS.MAXIMO))
        } else if (AtlantaMain.IDS_OBJETOS_STATS_MINIMOS.contains(idObjModelo)) {
            convertirStringAStats_Base(objModelo!!.generarStatsModelo(CAPACIDAD_STATS.MINIMO))
        } else if (AtlantaMain.IDS_OBJETOS_STATS_RANDOM.contains(idObjModelo)) {
            convertirStringAStats_Base(objModelo!!.generarStatsModelo(CAPACIDAD_STATS.RANDOM))
        } else {
            convertirStringAStats_Base(strStats)
        }
        crearEncarnacion()
    }

    fun addDurabilidad(valor: Int): Boolean {
        durabilidad += valor
        if (durabilidad > durabilidadMax) {
            durabilidad = durabilidadMax
        }
        return durabilidad < 1
    }

    fun puedeTenerStatsIguales(): Boolean { // parece q es para q no se junten los items en 1 solo
        return objModelo?.tipo?.toInt() != Constantes.OBJETO_TIPO_PIEDRA_DE_ALMA_LLENA && objModelo
            ?.tipo?.toInt() != Constantes.OBJETO_TIPO_MASCOTA && objModelo?.tipo?.toInt() != Constantes.OBJETO_TIPO_FANTASMA_MASCOTA && objModelo?.id?.let {
            getCreaTuItem(
                it
            )
        } == null && encarnacion == null
    }

    fun sonStatsIguales(otro: Objeto): Boolean {
        if (durabilidad != otro.durabilidad || durabilidadMax != otro.durabilidadMax) {
            return false
        }
        if (objevivoID > 0 || otro.objevivoID > 0) {
            return false
        }
        if (!stats.sonStatsIguales(otro.stats)) {
            return false
        }
        if (efectosNormales == null && otro.efectosNormales == null) { // nada
        } else if (efectosNormales == null && otro.efectosNormales != null || otro.efectosNormales == null || efectosNormales!!.isEmpty() && !otro.efectosNormales!!.isEmpty() || otro.efectosNormales!!.isEmpty() && !efectosNormales!!.isEmpty()) {
            return false
        } else if (!efectosNormales!!.isEmpty()) {
            val ePropios = ArrayList<String>()
            val eOtros = ArrayList<String>()
            for (eh in efectosNormales!!) {
                ePropios.add(eh.efectoID.toString() + "," + eh.args)
            }
            for (eh in otro.efectosNormales!!) {
                eOtros.add(eh.efectoID.toString() + "," + eh.args)
            }
            for (eh in efectosNormales!!) {
                val entry: String = eh.efectoID.toString() + "," + eh.args
                if (!eOtros.contains(entry)) {
                    return false
                } else {
                    eOtros.remove(entry)
                }
            }
            for (eh in otro.efectosNormales!!) {
                val entry: String = eh.efectoID.toString() + "," + eh.args
                if (!ePropios.contains(entry)) {
                    return false
                } else {
                    ePropios.remove(entry)
                }
            }
        }
        return true
    }

    fun setIDObjevivo(id: Int) {
        objevivoID = id
    }

    fun setIDOjbModelo(idObjModelo: Int) {
        if (getObjetoModelo(idObjModelo) == null) {
            return
        }
        objModeloID = idObjModelo
        objModelo = getObjetoModelo(idObjModelo)
    }

    fun setPosicion(newPos: Byte, perso: Personaje?, refrescarStuff: Boolean) {
        if (posicion == newPos) {
            return
        }
        val oldPos = posicion
        posicion = newPos
        if (perso != null) {
            perso.cambiarPosObjeto(this, oldPos, newPos, refrescarStuff)
            if (!refrescarStuff && perso.enLinea()) {
                ENVIAR_OM_MOVER_OBJETO(perso, this)
            }
        }
    }

    val efectosCriticos: ArrayList<EfectoHechizo>?
        get() {
            if (efectosNormales != null) {
                val efectos = ArrayList<EfectoHechizo>()
                for (EH in efectosNormales!!) {
                    try {
                        if (EH.efectoID == Constantes.STAT_MENOS_PA) {
                            efectos.add(EH)
                        } else {
                            val infos = EH.args.split(",".toRegex()).toTypedArray()
                            var dados = ""
                            var primerValor = infos[0].toInt()
                            var segundoValor = infos[1].toInt()
                            if (segundoValor <= 0) {
                                segundoValor = -1
                                primerValor += objModelo!!.bonusGC.toInt()
                                dados = "0d0+$primerValor"
                            } else {
                                primerValor += objModelo!!.bonusGC.toInt()
                                segundoValor += objModelo!!.bonusGC.toInt()
                                dados = "1d" + (segundoValor - primerValor) + "+" + (primerValor - 1)
                            }
                            val eh = EfectoHechizo(
                                EH.efectoID, primerValor.toString() + "," + segundoValor + ",-1,0,0,"
                                        + dados, 0, -1, getZonaEfectoArma(objModelo!!.tipo.toInt())
                            )
                            eh.afectados = 2
                            efectos.add(eh)
                        }
                    } catch (ignored: Exception) {
                    }
                }
                return efectos
            }
            return null
        }

    fun getStatValor(statID: Int): Int {
        return stats.getStatParaMostrar(statID)
    }

    fun fijarStatValor(statID: Int, valor: Int) {
        stats.fijarStatID(statID, valor)
    }

    fun addStatTexto(statID: Int, texto: String?) {
        stats.addStatTexto(statID, texto!!, false)
    }

    fun tieneStatTexto(statID: Int): Boolean {
        return stats.tieneStatTexto(statID)
    }

    fun tieneAlgunStatExo(): Boolean {
        for ((key) in stats.entrySet) {
            if (esStatExo(key)) {
                return true
            }
        }
        return false
    }

    fun tieneStatExo(statID: Int): Boolean {
        return if (!esStatExo(statID)) {
            false
        } else getStatValor(statID) != 0
    }

    fun esStatExo(statID: Int): Boolean {
        return if (!esStatDePelea(statID)) {
            false
        } else !objModelo!!.tieneStatInicial(statID)
        // siempre sera positivo
    }

    fun esStatOver(statID: Int, valor: Int): Boolean {
        if (!esStatDePelea(statID)) {
            return false
        }
        val duo = objModelo!!.getDuoInicial(statID) ?: return false
        return valor > duo._segundo
    }

    fun convertirStringAStats(strStats: String) {
        stats.clear()
        efectosNormales = null
        durabilidadMax = -1
        durabilidad = durabilidadMax
        for (x in strStats.split(",".toRegex()).toTypedArray()) {
            if (x.isEmpty()) {
                continue
            }
            var str = x
            try {
                val stats2 = str.split("#".toRegex()).toTypedArray()
                val statID = statSimiliar(stats2[0].toInt(16))
                // si no es objevivo y tiene stats entre 970 y 974
// if (_objModelo.getTipo() != Constantes.OBJETO_TIPO_OBJEVIVO && statID >= 970 && statID
// <= 974) {
// continue;
// }
// if (_idObjevivo > 0 && (statID == Constantes.STAT_RECIBIDO_EL || statID ==
// Constantes.STAT_SE_HA_COMIDO_EL)) {
// continue;
// }
                if (Constantes.STAT_RECIBIDO_EL == statID) {
                    if (stats2.size > 1 && stats2[1] == "0") {
                        val actual = Calendar.getInstance()
                        str =
                            stats2[0] + "#" + (Integer.toHexString(actual[Calendar.YEAR]) + "#" + Integer.toHexString(
                                actual[Calendar.MONTH] * 100 + actual[Calendar.DAY_OF_MONTH]
                            ) + "#" + Integer.toHexString(
                                actual[Calendar.HOUR_OF_DAY] * 100 + actual[Calendar.MINUTE]
                            ))
                    }
                }
                if (Constantes.STAT_RESISTENCIA == statID) {
                    durabilidad = stats2[2].toInt(16)
                    durabilidadMax = stats2[3].toInt(16)
                } else if (esStatHechizo(statID)) {
                    stats.addStatHechizo(str)
                } else if (esStatRepetible(statID)) {
                    stats.addStatRepetido(str)
                } else if (esStatTexto(statID)) {
                    stats.addStatTexto(statID, str, true)
                } else if (esEfectoHechizo(statID)) {
                    var dados = ""
                    val primerValor = stats2[1].toInt(16)
                    var segundoValor = stats2[2].toInt(16)
                    if (segundoValor <= 0) {
                        segundoValor = 0
                        dados = "0d0+$primerValor"
                    } else {
                        dados = "1d" + (segundoValor - primerValor) + "+" + (primerValor - 1)
                    }
                    val eh = EfectoHechizo(
                        statID, "$primerValor,$segundoValor,-1,0,0,$dados", 0, -1,
                        getZonaEfectoArma(objModelo!!.tipo.toInt())
                    )
                    eh.afectados = 2
                    if (efectosNormales == null) {
                        efectosNormales = ArrayList()
                    }
                    efectosNormales!!.add(eh)
                } else { // int statPositivo = Constantes.getStatPositivoDeNegativo(statID);// +agi
                    val valor = stats2[1].toInt(16) // 100
                    // if (_objModelo.tieneStatInicial(statPositivo)) {//+agi100
// int cantStatInicial = _objModelo.getStatsIniciales().get(statPositivo)._primero;
// if (cantStatInicial < 0) {// si es stat inicial negativo
// // -agi -20
// int tempValor = valor;
// if (statPositivo != statID) {
// tempValor = -tempValor;
// }
// if (tempValor > 0) {
// continue;
// }
// }
// }
                    if (objModelo!!.ogrinas > 0) {
                        stats.addStatID(statID, valor)
                    } else {
                        var encontrado = false
                        for (a in Constantes.FUN_STATS_RESTRINGIDAS) {
                            if (statID == a) {
                                encontrado = true
                            }
                        }
                        for (a in Constantes.FUN_STATS_RESTRINGIDAS_SOLO_OBJ) {
                            if (statID == a) {
                                encontrado = true
                            }
                        }
                        if (encontrado) {
                            stats.addStatID(statID, valor)
                        } else {
                            stats.addStatID(statID, (FUN_RANDOM_ITEM() * valor).toInt())
                        }
                    }
                }
            } catch (e: Exception) {
                redactarLogServidorln(
                    "BUG OBJETO ID: " + id + ", OBJMOD: " + objModeloID + ", STAT BUG: " + str
                            + ", STATS: " + strStats + ", STATS MODELO: " + objModelo!!.statsModelo
                )
                e.printStackTrace()
            }
        }
        efectosNormales?.trimToSize()
        encarnacion?.refrescarStatsItem()
    }

    fun convertirStringAStats_Base(strStats: String) {
        stats.clear()
        efectosNormales = null
        durabilidadMax = -1
        durabilidad = durabilidadMax
        for (x in strStats.split(",".toRegex()).toTypedArray()) {
            if (x.isEmpty()) {
                continue
            }
            var str = x
            try {
                val stats2 = str.split("#".toRegex()).toTypedArray()
                val statID = statSimiliar(stats2[0].toInt(16))
                // si no es objevivo y tiene stats entre 970 y 974
// if (_objModelo.getTipo() != Constantes.OBJETO_TIPO_OBJEVIVO && statID >= 970 && statID
// <= 974) {
// continue;
// }
// if (_idObjevivo > 0 && (statID == Constantes.STAT_RECIBIDO_EL || statID ==
// Constantes.STAT_SE_HA_COMIDO_EL)) {
// continue;
// }
                if (Constantes.STAT_RECIBIDO_EL == statID) {
                    if (stats2.size > 1 && stats2[1] == "0") {
                        val actual = Calendar.getInstance()
                        str =
                            stats2[0] + "#" + (Integer.toHexString(actual[Calendar.YEAR]) + "#" + Integer.toHexString(
                                actual[Calendar.MONTH] * 100 + actual[Calendar.DAY_OF_MONTH]
                            ) + "#" + Integer.toHexString(
                                actual[Calendar.HOUR_OF_DAY] * 100 + actual[Calendar.MINUTE]
                            ))
                    }
                }
                if (Constantes.STAT_RESISTENCIA == statID) {
                    durabilidad = stats2[2].toInt(16)
                    durabilidadMax = stats2[3].toInt(16)
                } else if (esStatHechizo(statID)) {
                    stats.addStatHechizo(str)
                } else if (esStatRepetible(statID)) {
                    stats.addStatRepetido(str)
                } else if (esStatTexto(statID)) {
                    stats.addStatTexto(statID, str, true)
                } else if (esEfectoHechizo(statID)) {
                    var dados = ""
                    val primerValor = stats2[1].toInt(16)
                    var segundoValor = stats2[2].toInt(16)
                    if (segundoValor <= 0) {
                        segundoValor = 0
                        dados = "0d0+$primerValor"
                    } else {
                        dados = "1d" + (segundoValor - primerValor) + "+" + (primerValor - 1)
                    }
                    val eh = EfectoHechizo(
                        statID, "$primerValor,$segundoValor,-1,0,0,$dados", 0, -1,
                        getZonaEfectoArma(objModelo!!.tipo.toInt())
                    )
                    eh.afectados = 2
                    if (efectosNormales == null) {
                        efectosNormales = ArrayList()
                    }
                    efectosNormales!!.add(eh)
                } else { // int statPositivo = Constantes.getStatPositivoDeNegativo(statID);// +agi
                    val valor = stats2[1].toInt(16) // 100
                    // if (_objModelo.tieneStatInicial(statPositivo)) {//+agi100
// int cantStatInicial = _objModelo.getStatsIniciales().get(statPositivo)._primero;
// if (cantStatInicial < 0) {// si es stat inicial negativo
// // -agi -20
// int tempValor = valor;
// if (statPositivo != statID) {
// tempValor = -tempValor;
// }
// if (tempValor > 0) {
// continue;
// }
// }
// }
                    stats.addStatID(statID, valor)
                }
            } catch (e: Exception) {
                redactarLogServidorln(
                    "BUG OBJETO ID: " + id + ", OBJMOD: " + objModeloID + ", STAT BUG: " + str
                            + ", STATS: " + strStats + ", STATS MODELO: " + objModelo!!.statsModelo
                )
                e.printStackTrace()
            }
        }
        efectosNormales?.trimToSize()
        encarnacion?.refrescarStatsItem()
    }

    private fun crearEncarnacion() {
        if (tieneStatTexto(Constantes.STAT_ENCARNACION_NIVEL)) {
            if (encarnacion == null) {
                val encarID = getParamStatTexto(Constantes.STAT_ENCARNACION_NIVEL, 1).toInt(16)
                val encarExp = getParamStatTexto(Constantes.STAT_ENCARNACION_NIVEL, 2).toInt(16)
                val encarModelo = getEncarnacionModelo(encarID)
                if (encarModelo != null) {
                    encarnacion = Encarnacion(this, encarExp, encarModelo)
                }
            }
        }
    }

    fun convertirStatsAString(sinAdicionales: Boolean): String {
        val stats2 = StringBuilder()
        if (getTipoConStatsModelo(objModelo!!.tipo.toInt())) {
            stats2.append(objModelo!!.statsModelo)
        } else {
            if (encarnacion != null) {
                addStatTexto(
                    Constantes.STAT_ENCARNACION_NIVEL,
                    Integer.toHexString(encarnacion!!.gfxID) + "#" + Integer
                        .toHexString(encarnacion!!.exp) + "#" + Integer.toHexString(encarnacion!!.nivel)
                )
            }
            if (efectosNormales != null) {
                for (EH in efectosNormales!!) {
                    if (stats2.length > 0) {
                        stats2.append(",")
                    }
                    val infos = EH.args.split(",".toRegex()).toTypedArray()
                    try {
                        stats2.append(Integer.toHexString(EH.efectoID)).append("#")
                            .append(Integer.toHexString(infos[0].toInt())).append("#")
                            .append(Integer.toHexString(infos[1].toInt())).append("#0#")
                            .append(infos[5])
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            if (durabilidadMax > 0 && durabilidad > 0) {
                if (stats2.length > 0) {
                    stats2.append(",")
                }
                stats2.append(Integer.toHexString(Constantes.STAT_RESISTENCIA)).append("#0#")
                    .append(Integer.toHexString(durabilidad)).append("#")
                    .append(Integer.toHexString(durabilidadMax)).append("#0d0+").append(durabilidad)
            }
            val oStats: String = stats.getStringStats(this)
            if (!oStats.isEmpty()) {
                if (stats2.length > 0) {
                    stats2.append(",")
                }
                stats2.append(oStats)
            }
            if (!sinAdicionales) {
                if (objevivoID > 0 && objevivoID != id) {
                    val objevivo = getObjeto(objevivoID)
                    if (objevivo != null) {
                        if (stats2.isNotEmpty()) {
                            stats2.append(",")
                        }
                        stats2.append(objevivo.convertirStatsAString(false))
                    }
                } else {
                    objevivoID = 0
                }
            }
        }
        // fuera de for
        if (objModelo!!.ogrinas > 0 && getCreaTuItem(objModelo!!.id) == null) {
            if (stats2.isNotEmpty()) {
                stats2.append(",")
            }
            stats2.append(Integer.toHexString(Constantes.STAT_COLOR_NOMBRE_OBJETO)).append("#1")
        }
        return stats2.toString()
    }

    fun strGrupoMob(): String {
        val stats2 = StringBuilder()
        for (str in stats.statRepetidos ?: return "") {
            try {
                val s = str.split("#".toRegex()).toTypedArray()
                if (s[0].toInt(16) != Constantes.STAT_INVOCA_MOB && s[0].toInt(
                        16
                    ) != Constantes.STAT_INVOCA_MOB_2
                ) {
                    continue
                }
                if (stats2.isNotEmpty()) {
                    stats2.append(";")
                }
                stats2.append(s[3].toInt(16)).append(",").append(s[1].toInt(16)).append(",")
                    .append(s[1].toInt(16))
            } catch (ignored: Exception) {
            }
        }
        return stats2.toString()
    }

    fun strDarObjetos(): String {
        val stats2 = StringBuilder()
        for (str in stats.statRepetidos ?: return "") {
            try {
                val s = str.split("#".toRegex()).toTypedArray()
                if (s[0].toInt(16) != Constantes.STAT_DAR_OBJETO) continue
                if (stats2.isNotEmpty()) {
                    stats2.append(";")
                }
                stats2.append(s[1].toInt(16)).append(",").append(s[2].toInt(16))
            } catch (ignored: Exception) {
            }
        }
        return stats2.toString()
    }

    fun pasoIntercambiableDesde(): Boolean {
        if (tieneStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE)) {
            if (getDiferenciaTiempo(Constantes.STAT_INTERCAMBIABLE_DESDE, 60 * 1000) >= 0) {
                addStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE, "")
            } else {
                return true
            }
        }
        return false
    }

    fun getParamStatTexto(stat: Int, parametro: Int): String {
        return stats.getParamStatTexto(stat, parametro)
    }

    fun comerComida(idModComida: Int) {
        var nroComidas = 0
        val mascModelo = getMascotaModelo(objModeloID) ?: return
        try {
            if (tieneStatTexto(Constantes.STAT_NUMERO_COMIDAS)) {
                nroComidas = getParamStatTexto(Constantes.STAT_NUMERO_COMIDAS, 1).toInt(16)
            }
        } catch (ignored: Exception) {
        }
        nroComidas++
        addStatTexto(Constantes.STAT_ULTIMA_COMIDA, "0#0#" + Integer.toHexString(idModComida))
        if (nroComidas == 1) {
            nroComidas = 0
            val comida = mascModelo.getComida(idModComida) ?: return
            val efecto = comida.iDStat
            var maximo = 0
            var maxPorStat = 0
            for ((statID, value) in stats.entrySet) {
                var factor: Byte = 1
                when (statID) {
                    Constantes.STAT_MAS_RES_PORC_AGUA, Constantes.STAT_MAS_RES_PORC_TIERRA, Constantes.STAT_MAS_RES_PORC_AIRE, Constantes.STAT_MAS_RES_PORC_NEUTRAL, Constantes.STAT_MAS_RES_PORC_FUEGO -> factor =
                        6
                }
                maximo += value * factor
                if (statID == efecto) {
                    maxPorStat = value * factor
                }
            }
            if (maximo >= mascModelo.maxStats || maxPorStat >= mascModelo.getStatsPorEfecto(efecto)) { // no hace ni mierda
            } else {
                if (efecto == Constantes.STAT_MAS_INICIATIVA || efecto == Constantes.STAT_MAS_PODS) {
                    stats.addStatID(efecto, 10)
                } else {
                    stats.addStatID(efecto, 1)
                }
            }
        }
        addStatTexto(Constantes.STAT_NUMERO_COMIDAS, nroComidas.toString() + "")
    }

    fun comerAlma(idMobModelo: Int, cantAlmasDevor: Int) {
        val mascModelo = getMascotaModelo(objModeloID) ?: return
        val comida = mascModelo.getComida(idMobModelo) ?: return
        var valorTemp = 0
        var index = -1
        val efecto = comida.iDStat
        var maximo = 0
        var maxPorStat = 0
        if (stats.statRepetidos != null) {
            for (stati in stats.statRepetidos!!) {
                try {
                    val x = stati.split("#".toRegex()).toTypedArray()
                    if (x[0].toInt(16) != Constantes.STAT_NOMBRE_MOB) {
                        continue
                    }
                    val i = x[1].toInt(16)
                    val c = x[3].toInt(16)
                    if (i == idMobModelo) {
                        valorTemp = c
                        index = stats.statRepetidos!!.indexOf(stati)
                    }
                } catch (ignored: Exception) {
                }
            }
            if (index > -1) {
                stats.statRepetidos!!.removeAt(index)
            }
        }
        stats.addStatRepetido(
            Integer.toHexString(Constantes.STAT_NOMBRE_MOB) + "#" + Integer.toHexString(
                idMobModelo
            ) + "#0#" + Integer.toHexString(valorTemp + cantAlmasDevor) + "#0"
        )
        for ((statID, value) in stats.entrySet) {
            var por: Byte = 1
            when (statID) {
                Constantes.STAT_MAS_RES_PORC_TIERRA, Constantes.STAT_MAS_RES_PORC_AGUA, Constantes.STAT_MAS_RES_PORC_AIRE, Constantes.STAT_MAS_RES_PORC_FUEGO, Constantes.STAT_MAS_RES_PORC_NEUTRAL -> por =
                    6
            }
            maximo += value * por
            if (statID == efecto) {
                maxPorStat = value * por
            }
        }
        if (maximo >= mascModelo.maxStats || maxPorStat >= mascModelo.getStatsPorEfecto(efecto)) {
            return
        }
        if ((valorTemp + cantAlmasDevor) / comida.cantidad > valorTemp / comida.cantidad) {
            if (efecto == Constantes.STAT_MAS_INICIATIVA || efecto == Constantes.STAT_MAS_PODS) {
                stats.addStatID(efecto, 10)
            } else {
                stats.addStatID(efecto, 1)
            }
        }
    }

    fun getDiferenciaTiempo(stat: Int, escala: Int): Long {
        val tiempoActual = getTiempoActualEscala(escala.toLong())
        val tiempoDif = getTiempoDeUnStat(stats.getStatTexto(stat)!!, escala)
        return tiempoActual - tiempoDif
    }

    fun horaComer(forzado: Boolean, corpulencia1: Int): Boolean {
        if (forzado || getDiferenciaTiempo(
                Constantes.STAT_SE_HA_COMIDO_EL, 60
                        * 1000
            ) >= AtlantaMain.MINUTOS_ALIMENTACION_MASCOTA
        ) {
            addStatTexto(Constantes.STAT_SE_HA_COMIDO_EL, getStatSegunFecha(Calendar.getInstance()))
            corpulencia = corpulencia1
            return true
        }
        return false
    }

    var pDV: Int
        get() = if (!tieneStatTexto(Constantes.STAT_PUNTOS_VIDA)) {
            -1
        } else getParamStatTexto(
            Constantes.STAT_PUNTOS_VIDA,
            3
        ).toInt(16)
        set(pdv) {
            addStatTexto(Constantes.STAT_PUNTOS_VIDA, "0#0#" + Integer.toHexString(pdv))
        }

    var corpulencia: Int
        get() {
            if (!tieneStatTexto(Constantes.STAT_CORPULENCIA)) {
                return -1
            }
            if (getParamStatTexto(Constantes.STAT_CORPULENCIA, 3) == "7") {
                return Constantes.CORPULENCIA_DELGADO.toInt()
            }
            return if (getParamStatTexto(Constantes.STAT_CORPULENCIA, 2) == "7") {
                Constantes.CORPULENCIA_OBESO.toInt()
            } else Constantes.CORPULENCIA_NORMAL.toInt()
        }
        set(numero) {
            when (numero) {
                Constantes.CORPULENCIA_OBESO.toInt() -> addStatTexto(Constantes.STAT_CORPULENCIA, "0#7#0")
                Constantes.CORPULENCIA_DELGADO.toInt() -> addStatTexto(Constantes.STAT_CORPULENCIA, "0#0#7")
                Constantes.CORPULENCIA_NORMAL.toInt() -> addStatTexto(Constantes.STAT_CORPULENCIA, "0#0#0")
            }
        }

    fun esDevoradorAlmas(): Boolean {
        return try {
            getMascotaModelo(objModeloID)!!.esDevoradorAlmas()
        } catch (e: Exception) {
            false
        }
    }

    val dañoPromedioNeutral: Int
        get() {
            if (efectosNormales != null) {
                for (EH in efectosNormales!!) {
                    try {
                        if (EH.efectoID != Constantes.STAT_DAÑOS_NEUTRAL) {
                            continue
                        }
                        val infos = EH.args.split(",".toRegex()).toTypedArray()
                        return (infos[1].toInt(16) + infos[0].toInt(16)) / 2
                    } catch (ignored: Exception) {
                    }
                }
            }
            return 1
        }

    fun forjaMagiaGanar(statID: Int, potencia: Int) {
        when (statID) {
            96, 97, 98, 99 -> if (efectosNormales != null) {
                for (EH in efectosNormales!!) {
                    if (EH.efectoID != Constantes.STAT_DAÑOS_NEUTRAL) {
                        continue
                    }
                    val infos = EH.args.split(",".toRegex()).toTypedArray()
                    try {
                        val min = infos[0].toInt()
                        val max = infos[1].toInt()
                        var nuevoMin =
                            (Math.floor((min - 1) * (potencia / 100f).toDouble()) + 1).toInt() // 50 y 78
                        val nuevoMax =
                            (Math.floor((min - 1) * (potencia / 100f).toDouble()) + Math.floor(
                                ((max - min + 1)
                                        * (potencia / 100f)).toDouble()
                            )).toInt()
                        if (AtlantaMain.MODO_DEBUG) {
                            println("min $min")
                            println("max $max")
                            println("nuevoMin $nuevoMin")
                            println("nuevoMax $nuevoMax")
                        }
                        if (nuevoMin == 0) {
                            nuevoMin = 1
                        }
                        val nuevosArgs =
                            (nuevoMin.toString() + "," + nuevoMax + ",-1,0,0," + "1d" + (nuevoMax - nuevoMin + 1) + "+"
                                    + (nuevoMin - 1))
                        if (AtlantaMain.MODO_DEBUG) {
                            println("Nuevo Args FM elemental $nuevosArgs")
                        }
                        EH.args = nuevosArgs
                        EH.efectoID = statID
                        return
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            else -> stats.addStatID(statID, potencia)
        }
    }

    fun runasRomperObjeto(runas: MutableMap<Int, Int>, cantObjeto: Int) {
        for (i in 1..cantObjeto) {
            for (s in convertirStatsAString(true).split(",".toRegex()).toTypedArray()) {
                try {
                    if (s.isEmpty()) {
                        continue
                    }
                    val stats = s.split("#".toRegex()).toTypedArray()
                    val statID = stats[0].toInt(16)
                    val statPositivo = getStatPositivoDeNegativo(statID)
                    var valor = stats[1].toInt(16)
                    if (statID != statPositivo) {
                        valor = -valor
                    }
                    if (valor < 1) {
                        continue
                    }
                    if (getRunaPorStat(statID, 1) == 0) {
                        continue
                    }
                    val pesoIndividual = getPesoStat(statID)
                    var pesoStat = pesoIndividual * valor
                    while (pesoStat > 0) {
                        var tipoRuna = getTipoRuna(statID, pesoStat)
                        if (tipoRuna == 0) {
                            break
                        }
                        val v = getPotenciaRunaPorStat(statID)
                        val jet = getRandomDecimal(3)
                        val prob =
                            getPorcCrearRuna(statID, pesoStat, tipoRuna, objModelo!!.nivel.toInt()).toInt()
                        tipoRuna--
                        var red = 0f
                        if (prob >= jet) {
                            red += v[tipoRuna] * pesoIndividual * 3
                            // tipoRuna = Formulas.getRandomInt(0, tipoRuna);//aqui cambia la runa por mas o menos
                            val runa = getRunaPorStat(statID, tipoRuna + 1)
                            // if (!AtlantaMain.RUNAS_NO_PERMITIDAS.contains(runa)) {
                            var cant = Math.pow(2.0, tipoRuna - tipoRuna.toDouble()).toInt()
                            if (runas[runa] != null) {
                                cant += runas[runa]!!
                            }
                            runas[runa] = cant
                            // }
                        }
                        red += v[tipoRuna] * pesoIndividual
                        red = ceil(red / AtlantaMain.FACTOR_OBTENER_RUNAS.toDouble()).toFloat()
                        pesoStat -= red
                    }
                } catch (ignored: Exception) {
                }
            }
        }
    }

    fun forjaMagiaPerder(statMaguear: Int, potencia: Int, afectarStatMagueo: Boolean) {
        var pozoResidual = getStatValor(Constantes.STAT_POZO_RESIDUAL)
        var pesoRunaRestar = Math.ceil(getPesoStat(statMaguear) * potencia.toDouble()).toInt()
        if (AtlantaMain.MODO_DEBUG) {
            println("------------- PERDIENDO STATS FM -------------------")
            println("pozoResidual: $pozoResidual")
            println("pesoRuna: $pesoRunaRestar")
        }
        val pesoOrigRuna = pesoRunaRestar
        if (pesoRunaRestar > 0) { // si sobro peso a restar, se tiene q disminuir
            val statsCheckeados = ArrayList<Int>()
            if (!afectarStatMagueo) {
                statsCheckeados.add(statMaguear)
            }
            while (pesoRunaRestar > 0) {
                var statPerder = getStatElegidoAPerder(pesoOrigRuna, statMaguear, statsCheckeados)
                if (AtlantaMain.MODO_DEBUG) {
                    println("SE ESCOGIO A PERDER STATID $statPerder")
                }
                if (statPerder == 0) {
                    break
                }
                var overExo = Trabajo.MAGUEO_NORMAL.toInt()
                if (statPerder > 2000) {
                    statPerder -= 2000
                    overExo = Trabajo.MAGUEO_EXO.toInt()
                } else if (statPerder > 1000) {
                    statPerder -= 1000
                    overExo = Trabajo.MAGUEO_OVER.toInt()
                }
                if (overExo == Trabajo.MAGUEO_NORMAL.toInt()) {
                    if (pozoResidual > 0) {
                        pesoRunaRestar -= pozoResidual // 100 .... 35 - 52 = -17
                        pozoResidual = 0
                        continue
                    }
                }
                statsCheckeados.add(statPerder)
                val pesoRunaPerder = getPesoStat(statPerder)
                if (pesoRunaPerder == 0f) {
                    continue
                }
                val cantStatPerder = getStatValor(statPerder)
                // lo mas cercano a positivo, minimo es 1
                val cantDebePerder = Math.ceil(pesoRunaRestar / pesoRunaPerder.toDouble()).toInt()
                val maxPerder = Math.min(cantStatPerder, cantDebePerder)
                if (AtlantaMain.MODO_DEBUG) {
                    println(
                        "statPerder " + statPerder + " cantStatPerder " + cantStatPerder + " pesoRunaRestar "
                                + pesoRunaRestar + " cantDebePerder " + cantDebePerder + " maxPerder " + maxPerder
                    )
                }
                if (maxPerder <= 0) {
                    continue
                }
                var random = maxPerder
                random = when (overExo.toByte()) {
                    Trabajo.MAGUEO_OVER, Trabajo.MAGUEO_EXO -> getRandomInt(1, cantStatPerder)
                    else -> if (pesoRunaRestar == 1) {
                        1
                    } else {
                        getRandomInt(1, maxPerder)
                    }
                }
                stats.addStatID(statPerder, -random)
                val pesoPerder = (random * pesoRunaPerder).toInt()
                pesoRunaRestar -= pesoPerder
            }
        }
        if (pesoRunaRestar > 0) {
            pesoRunaRestar = 0
        }
        stats.fijarStatID(Constantes.STAT_POZO_RESIDUAL, Math.abs(pesoRunaRestar))
    }

    private fun getStatElegidoAPerder(
        pesoOrigRuna: Int, statRuna: Int,
        statsCheckeados: ArrayList<Int>
    ): Int {
        val listaStats = ArrayList<Int>()
        for ((statID, valor) in stats.entrySet) {
            if (AtlantaMain.MODO_DEBUG) {
                println("Se intenta perder el stat $statID valor $valor")
            }
            if (getStatPositivoDeNegativo(statID) != statID) { // si es negativo no se le borrara
                if (AtlantaMain.MODO_DEBUG) {
                    println("-- Cancel 1")
                }
                continue
            }
            if (!esStatDePelea(statID)) {
                if (AtlantaMain.MODO_DEBUG) {
                    println("-- Cancel 2")
                }
                continue
            }
            if (statsCheckeados.contains(statID)) {
                if (AtlantaMain.MODO_DEBUG) {
                    println("-- Cancel 3")
                }
                continue
            }
            if (esStatOver(statID, valor)) { // si el stat es OVER
                if (statID != statRuna) {
                    return statID + 1000
                }
            } else if (esStatExo(statID)) { // si el stat es EXO retorna primero
                if (statID != statRuna) {
                    return statID + 2000
                }
            }
            listaStats.add(statID)
        }
        while (!listaStats.isEmpty()) { // if (listaStats.size() == statsCheckeados.size()) {
// statsCheckeados.clear();
// }
            val statID = listaStats[getRandomInt(0, listaStats.size - 1)]
            listaStats.remove(statID as Any)
            val pesoRunaPerder = getPesoStat(statID)
            // si es 3 = sabiduria, pero tiene 10 de sab y el otro es 10 agilidad
            if (AtlantaMain.MODO_DEBUG) {
                println("-> Escoger statID $statID pesoRuna $pesoRunaPerder pesoOrig $pesoOrigRuna")
            }
            if (pesoRunaPerder > pesoOrigRuna) {
                val suerte = getRandomInt(1, 101)
                if (suerte <= pesoOrigRuna * 100 / pesoRunaPerder) {
                    return statID
                }
            } else {
                return statID
            }
        }
        return 0
    }

    fun stringObjetoConGuiño(): String {
        val str = StringBuilder()
        try {
            str.append(Integer.toHexString(id)).append("~")
                .append(Integer.toHexString(objModeloID)).append("~").append(
                    Integer.toHexString(
                        cantidad
                    )
                ).append("~").append(
                    if (posicion == Constantes.OBJETO_POS_NO_EQUIPADO) "" else Integer.toHexString(posicion.toInt())
                ).append("~").append(convertirStatsAString(false)).append("~").append(objModelo!!.kamas / 10)
            str.append(";")
        } catch (e: Exception) {
            redactarLogServidorln("OBJETO BUG stringObjetoConGuiño " + id + " Exception: " + e.toString())
        }
        return str.toString()
    }

    fun stringObjetoConPalo(cantidad: Int): String {
        val str = StringBuilder()
        try {
            str.append(id).append("|").append(cantidad).append("|").append(objModeloID).append("|")
                .append(convertirStatsAString(false))
        } catch (e: Exception) {
            redactarLogServidorln("OBJETO BUG stringObjetoConPalo " + id + " Exception: " + e.toString())
        }
        return str.toString()
    }

    fun convertirPerfecto(cantMod: Int): Boolean {
        return objModelo!!.convertirStatsPerfecto(cantMod, stats)
    }

    val statsModelo: String?
        get() = objModelo!!.statsModelo

    @Synchronized
    fun clonarObjeto(cantidad: Int, pos: Byte): Objeto {
        var cantidad = cantidad
        if (cantidad < 1) {
            cantidad = 1
        }
        return Objeto(0, objModeloID, cantidad, pos, convertirStatsAString(true), 0, 0)
    }
}