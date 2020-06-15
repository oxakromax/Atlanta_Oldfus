package variables.objeto

import estaticos.AtlantaMain
import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Constantes
import estaticos.Formulas.getRandomInt
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.Mundo.Duo
import estaticos.database.GestorSQL.UPDATE_GFX_OBJMODELO
import estaticos.database.GestorSQL.UPDATE_NIVEL_OBJMODELO
import estaticos.database.GestorSQL.UPDATE_PRECIO_MEDIO_OBJETO_MODELO
import estaticos.database.GestorSQL.UPDATE_PRECIO_OBJETO_MODELO
import variables.hechizo.EfectoHechizo
import variables.hechizo.StatHechizo
import variables.personaje.Personaje
import variables.stats.Stats
import variables.zotros.Accion
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.system.exitProcess

class ObjetoModelo(
    val id: Int,
    statsModelo: String?,
    val nombre: String,
    val tipo: Short,
    private var _nivel: Short,
    val peso: Short,
    private var _kamas: Int,
    val condiciones: String,
    infoArma: String,
    var vendidos: Int,
    var precioPromedio: Long,
    private var _ogrinas: Int,
    private val _forjaMagueable: Boolean,
    var gfx: Int,
    var nivelModifi: Boolean,
    private val _etereo: Boolean,
    private val _diasIntercambio: Int,
    val precioPanelOgrinas: Int,
    val precioPanelKamas: Int,
    itemPago: String
) {
    private val _accionesDeUso: MutableMap<Int, Accion> = TreeMap()
    private val _efectosModelo = ArrayList<EfectoHechizo>()
    private val _statsIniciales: MutableMap<Int, Duo<Int, Int>> = TreeMap()
    val mobsQueDropean = ArrayList<Int>()
    private var _esDosManos = false
    var bonusGC: Byte = 0
    var costePA: Byte = 0
    var probabilidadGC: Short = 0
    var setID = 0
    var statHechizo: StatHechizo? = null
    private var _statsModelo: String? = null
    var itemPago: Duo<Int, Int>? = null
    fun addMobQueDropea(id: Int) {
        if (!mobsQueDropean.contains(id)) {
            mobsQueDropean.add(id)
        }
    }

    fun delMobQueDropea(id: Int) {
        mobsQueDropean.remove(id as Any)
    }

    fun esEtereo(): Boolean {
        return _etereo
    }

    var gFX: Int
        get() = gfx
        set(gfx) {
            this.gfx = gfx
            UPDATE_GFX_OBJMODELO(id, this.gfx)
        }

    val statsIniciales: Map<Int, Duo<Int, Int>>
        get() = _statsIniciales

    fun getDuoInicial(statID: Int): Duo<Int, Int>? {
        return _statsIniciales[statID]
    }

    fun tieneStatInicial(statID: Int): Boolean {
        return _statsIniciales[statID] != null
    }

    fun addAccion(accion: Accion) {
        _accionesDeUso[accion.id] = accion
    }

    fun cantAcciones(): Int {
        return _accionesDeUso.size
    }

    fun borrarAcciones() {
        _accionesDeUso.clear()
    }

    fun esForjaMagueable(): Boolean {
        return !_forjaMagueable
    }

    fun esDosManos(): Boolean {
        return _esDosManos
    }

    var ogrinas: Int
        get() = _ogrinas
        set(ogrinas) {
            _ogrinas = ogrinas
            UPDATE_PRECIO_OBJETO_MODELO(id, ogrinas, true)
        }

    fun stringStatsModelo(): String? {
        return _statsModelo
    }

    var nivel: Short
        get() = _nivel
        set(nivel) {
            _nivel = nivel
            nivelModifi = true
            UPDATE_NIVEL_OBJMODELO(id, nivel)
        }

    var kamas: Int
        get() = _kamas
        set(kamas) {
            _kamas = kamas
            UPDATE_PRECIO_OBJETO_MODELO(id, kamas, false)
        }// es negativo// filtra si es statDePelea// HECHIZO ID = 0 (3 param)

    // aqui convierte los stats raros a stats normales, para q trabaje bien
    var statsModelo: String?
        get() = _statsModelo
        set(nuevosStats) {
            _statsModelo = nuevosStats
            _statsIniciales.clear()
            if (_statsModelo!!.isEmpty()) {
                return
            }
            for (stat in _statsModelo!!.split(",".toRegex()).toTypedArray()) {
                if (stat.isEmpty()) {
                    continue
                }
                val stats = stat.split("#".toRegex()).toTypedArray()
                var statID = stats[0].toInt(16)
                if (statID != statSimiliar(statID)) { // aqui convierte los stats raros a stats normales, para q trabaje bien
                    statID = statSimiliar(statID)
                    _statsModelo = _statsModelo!!.replaceFirst(
                        stat.toRegex(),
                        stat.replaceFirst(stats[0].toRegex(), Integer.toHexString(statID))
                    )
                }
                var esEfecto = false
                for (a in Constantes.BUFF_ARMAS) {
                    if (a == statID) { // HECHIZO ID = 0 (3 param)
                        val eh = EfectoHechizo(
                            statID, stats[1] + "," + stats[2] + ",-1,0,0," + stats[4], 0, -1,
                            Constantes.getZonaEfectoArma(tipo.toInt())
                        )
                        eh.afectados = 2
                        _efectosModelo.add(eh)
                        esEfecto = true
                        break
                    }
                }
                if (esEfecto) {
                    continue
                }
                val statPositivo = Constantes.getStatPositivoDeNegativo(statID)
                if (Constantes.esStatDePelea(statPositivo)) { // filtra si es statDePelea
                    var min = stats[1].toInt(16)
                    var max = stats[2].toInt(16)
                    if (max <= 0) {
                        max = min
                    }
                    if (statPositivo != statID) { // es negativo
                        min = -min
                        max = -max
                    }
                    val duo = Duo(min(min, max), max(min, max))
                    _statsIniciales[statPositivo] = duo
                }
            }
        }

    fun stringDeStatsParaTienda(): String {
        val str = StringBuilder()
        str.append(id).append(";").append(_statsModelo)
        if (itemPago != null) { // no pasa nada
        } else if (_ogrinas > 0) {
            if (_statsModelo!!.isNotEmpty()) {
                str.append(",")
            }
            str.append(Integer.toHexString(Constantes.STAT_COLOR_NOMBRE_OBJETO)).append("#1")
        }
        str.append(";")
        if (itemPago != null) {
            str.append(itemPago!!._segundo).append(";").append(itemPago!!._primero)
        } else if (_ogrinas > 0) {
            str.append(_ogrinas)
        } else {
            str.append(_kamas)
        }
        return str.toString()
    }

    fun aplicarAccion(perso: Personaje, objetivo: Personaje?, objID: Int, celda: Short) {
        var b = false
        for (accion in _accionesDeUso.values) {
            if (accion.realizarAccion(perso, objetivo, objID, celda)) b = true
        }
        if (b) {
            perso.restarCantObjOEliminar(objID, 1, true)
            ENVIAR_Im_INFORMACION(perso, "022;1~$id")
        }
    }

    fun nuevoPrecio(cantidad: Int, precio: Long) {
        val viejaVenta = vendidos
        vendidos += cantidad
        precioPromedio = (precioPromedio * viejaVenta + precio) / vendidos
        UPDATE_PRECIO_MEDIO_OBJETO_MODELO(this)
    }

    fun crearObjeto(
        cantidad: Int,
        pos: Byte,
        capStats: CAPACIDAD_STATS
    ): Objeto { // capStats => 0 = random, 1 = maximo, 2 = minimio
        var cantidad = cantidad
        var pos = pos
        if (cantidad < 1) {
            cantidad = 1
        }
        val stats = StringBuilder()
        // Calendar actual = Calendar.getInstance();
// if (_ogrinas > 0) {
// stats.append("325#" + Integer.toHexString(actual.get(1)) + "#"
// + Integer.toHexString(actual.get(2) * 100 + actual.get(5)) + "#"
// + Integer.toHexString(actual.get(11) * 100 + actual.get(12)) + ",");
// }
// stats.append("3d7#" + Integer.toHexString((actual.get(2) + 3) / 12 + actual.get(1)) + "#"
// + Integer.toHexString(((actual.get(2) + 3) % 12) * 100 + actual.get(5)) + "#"
// + Integer.toHexString(actual.get(11) * 100 + (actual.get(12)));
        if (_diasIntercambio > 0) {
            stats.append(Integer.toHexString(Constantes.STAT_INTERCAMBIABLE_DESDE)).append("#").append(
                stringFechaIntercambiable(
                    _diasIntercambio
                )
            )
        }
        if (tipo.toInt() == Constantes.OBJETO_TIPO_OBJETO_MUTACION) { // objeto de mutacion
            pos = Constantes.OBJETO_POS_OBJ_MUTACION
        } else if (tipo.toInt() == Constantes.OBJETO_TIPO_ALIMENTO_BOOST) { // alimento boost
            pos = Constantes.OBJETO_POS_BOOST
        } else if (tipo.toInt() == Constantes.OBJETO_TIPO_BENDICION) { // maldicion
            pos = Constantes.OBJETO_POS_MALDICION
        } else if (tipo.toInt() == Constantes.OBJETO_TIPO_MALDICION) { // bendicion
            pos = Constantes.OBJETO_POS_BENDICION
        } else if (tipo.toInt() == Constantes.OBJETO_TIPO_ROLEPLAY_BUFF) { // role play
            pos = Constantes.OBJETO_POS_ROLEPLAY
        } else if (tipo.toInt() == Constantes.OBJETO_TIPO_PJ_SEGUIDOR) { // personaje seguidor
            pos = Constantes.OBJETO_POS_PJ_SEGUIDOR
        }
        if (tipo.toInt() == Constantes.OBJETO_TIPO_MASCOTA && AtlantaMain.PARAM_ALIMENTAR_MASCOTAS) { // mascotas
            if (stats.isNotEmpty()) {
                stats.append(",")
            }
            stats.append("320#0#0#a")
            if (capStats == CAPACIDAD_STATS.MAXIMO) { // maximo stats
                if (stats.isNotEmpty()) {
                    stats.append(",")
                }
                stats.append(generarStatsModelo(capStats))
            }
        } else if (tipo.toInt() == Constantes.OBJETO_TIPO_CERTIFICADO_DE_LA_PETRERA || tipo.toInt() == Constantes.OBJETO_TIPO_CERTIFICADO_DE_MONTURA) { // certificados
// nada
        } else if (getTipoConStatsModelo(tipo.toInt())) { // pocima, perga exp, pan, golosina, pescado, carne
            if (stats.isNotEmpty()) {
                stats.append(",")
            }
            stats.append(_statsModelo)
        } else {
            if (stats.isNotEmpty()) {
                stats.append(",")
            }
            stats.append(generarStatsModelo(capStats))
        }
        return Objeto(0, id, cantidad, pos, stats.toString(), 0, 0)
    }

    fun convertirStatsPerfecto(cantMod: Int, stats: Stats): Boolean {
        try {
            val tempStats: MutableMap<Int, Int> = TreeMap()
            for ((statID, value) in _statsIniciales) {
                val valor = value._segundo
                if (stats.getStatParaMostrar(statID) < valor) {
                    tempStats[statID] = valor
                }
            }
            if (tempStats.isEmpty()) {
                return false
            }
            for (x in 1..cantMod) {
                if (tempStats.isEmpty()) {
                    break
                }
                val i = tempStats.keys.toTypedArray()[getRandomInt(0, tempStats.size - 1)]
                stats.fijarStatID(i, tempStats[i]!!)
                tempStats.remove(i)
            }
            return true
        } catch (ignored: Exception) {
        }
        return false
    }

    fun generarStatsModelo(capStats: CAPACIDAD_STATS): String {
        val statsObjeto = StringBuilder()
        for (s in _statsModelo!!.split(",".toRegex()).toTypedArray()) {
            try {
                if (s.isEmpty()) {
                    continue
                }
                val stats = s.split("#".toRegex()).toTypedArray()
                val statID = stats[0].toInt(16)
                if (statsObjeto.isNotEmpty()) {
                    statsObjeto.append(",")
                }
                if (Constantes.STAT_RECIBIDO_EL == statID) {
                    val actual = Calendar.getInstance()
                    statsObjeto.append(stats[0]).append("#").append(Integer.toHexString(actual[Calendar.YEAR]))
                        .append("#").append(
                            Integer
                                .toHexString(actual[Calendar.MONTH] * 100 + actual[Calendar.DAY_OF_MONTH])
                        ).append("#").append(
                            Integer
                                .toHexString(actual[Calendar.HOUR_OF_DAY] * 100 + actual[Calendar.MINUTE])
                        )
                    continue
                }
                if (Constantes.esStatRepetible(statID) || Constantes.esStatTexto(statID) || Constantes.esStatHechizo(
                        statID
                    )
                    || statID == Constantes.STAT_RESISTENCIA
                ) {
                    statsObjeto.append(s)
                    continue
                }
                if (statID == Constantes.STAT_TURNOS || statID == Constantes.STAT_PUNTOS_VIDA) {
                    statsObjeto.append(stats[0]).append("#0#0#").append(stats[3])
                    continue
                }
                var esEfecto = false
                for (a in Constantes.BUFF_ARMAS) {
                    if (a == statID) {
                        statsObjeto.append(stats[0]).append("#").append(stats[1]).append("#").append(stats[2])
                            .append("#0#").append(stats[4])
                        esEfecto = true
                        break
                    }
                }
                if (esEfecto) {
                    continue
                }
                val esNegativo = Constantes.getStatPositivoDeNegativo(statID) != statID
                var valor = 1
                var min = -1
                var max = -1
                try {
                    try {
                        min = stats[1].toInt(16)
                    } catch (ignored: Exception) {
                    }
                    try {
                        max = stats[2].toInt(16)
                    } catch (ignored: Exception) {
                    }
                    if (max <= 0) {
                        max = min
                    }
                    valor = if (capStats == CAPACIDAD_STATS.MAXIMO) { // stas maximos
                        if (esNegativo) {
                            min(min, max)
                        } else {
                            max(min, max)
                        }
                    } else if (capStats == CAPACIDAD_STATS.MINIMO) { // stats minimos
                        if (esNegativo) {
                            max(min, max)
                        } else {
                            min(min, max)
                        }
                    } else { // random
                        getRandomInt(min, max)
                    }
                    if (valor < 0) {
                        valor = 0
                    }
                } catch (ignored: Exception) {
                }
                statsObjeto.append(stats[0]).append("#").append(Integer.toHexString(valor)).append("#0#")
                    .append(stats[3]).append("#0d0+").append(valor)
            } catch (ignored: Exception) {
            }
        }
        return statsObjeto.toString()
    }

    enum class CAPACIDAD_STATS {
        RANDOM, MINIMO, MAXIMO
    }

    companion object {
        @JvmStatic
        fun statSimiliar(statID: Int): Int {
            when (statID) {
                Constantes.STAT_MAS_PA_2 -> return Constantes.STAT_MAS_PA
                Constantes.STAT_MAS_DAÑOS_2 -> return Constantes.STAT_MAS_DAÑOS
                Constantes.STAT_MAS_PM_2 -> return Constantes.STAT_MAS_PM
                Constantes.STAT_DAÑOS_DEVUELTOS -> return Constantes.STAT_REENVIA_DAÑOS
            }
            return statID
        }

        @JvmStatic
        fun stringFechaIntercambiable(dias: Int): String {
            val actual = Calendar.getInstance()
            actual.add(Calendar.DAY_OF_YEAR, dias)
            return getStatSegunFecha(actual)
        }

        @JvmStatic
        fun getStatSegunFecha(actual: Calendar): String {
            val año = actual[Calendar.YEAR]
            val mes = actual[Calendar.MONTH]
            val dia_del_mes = actual[Calendar.DAY_OF_MONTH]
            val hora_del_dia = actual[Calendar.HOUR_OF_DAY]
            val minuto_de_hora = actual[Calendar.MINUTE]
            return Integer.toHexString(año) + "#" + Integer.toHexString(mes * 100 + dia_del_mes) + "#" + Integer.toHexString(
                hora_del_dia * 100 + minuto_de_hora
            )
        }

        @JvmStatic
        fun getTipoConStatsModelo(tipo: Int): Boolean {
            when (tipo) {
                Constantes.OBJETO_TIPO_POCION, Constantes.OBJETO_TIPO_PERGAMINO_EXP, Constantes.OBJETO_TIPO_PAN, Constantes.OBJETO_TIPO_GOLOSINA, Constantes.OBJETO_TIPO_PESCADO_COMESTIBLE, Constantes.OBJETO_TIPO_PIEDRA_DEL_ALMA, Constantes.OBJETO_TIPO_CARNE_COMESTIBLE, Constantes.OBJETO_TIPO_OBJETO_CRIA -> return true
            }
            return false
        }
    }

    init {
        this@ObjetoModelo.statsModelo = statsModelo
        if (_statsModelo!!.isNotEmpty()) {
            try {
                if (infoArma.isNotEmpty()) {
                    val infos = infoArma.split(",".toRegex()).toTypedArray()
                    bonusGC = infos[0].toByte()
                    costePA = infos[1].toByte()
                    val minAlc = infos[2].toByte()
                    val maxAlc = infos[3].toByte()
                    probabilidadGC = infos[4].toShort()
                    val porcFC = infos[5].toShort()
                    val lanzarLinea = infos[6].equals("true", ignoreCase = true)
                    val lineaVista = infos[7].equals("true", ignoreCase = true)
                    _esDosManos = infos[8].equals("true", ignoreCase = true)
                    statHechizo = StatHechizo(
                        0,
                        1,
                        costePA,
                        minAlc,
                        maxAlc,
                        probabilidadGC,
                        porcFC,
                        lanzarLinea,
                        lineaVista,
                        false,
                        false,
                        0.toByte(),
                        0.toByte(),
                        0.toByte(),
                        0,
                        true,
                        "[18, 19, 1, 3, 41, 42]",
                        "",
                        (-1).toByte(),
                        false
                    )
                }
            } catch (e: Exception) {
                redactarLogServidorln("Objeto Modelo $id tiene bug en infosArma")
                e.printStackTrace()
                exitProcess(1)
            }
        }
        if (itemPago.isNotEmpty()) {
            try {
                val idItemPago = itemPago.split(",".toRegex()).toTypedArray()[0].toInt()
                val cantItemPago = itemPago.split(",".toRegex()).toTypedArray()[1].toInt()
                this.itemPago = Duo(idItemPago, cantItemPago)
            } catch (ignored: Exception) {
            }
        }
    }
}