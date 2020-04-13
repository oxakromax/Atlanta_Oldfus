package variables.oficio

import estaticos.AtlantaMain
import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Constantes
import estaticos.Constantes.getPesoStat
import estaticos.Formulas.getRandomInt
import estaticos.GestorSalida.ENVIAR_EA_TURNO_RECETA
import estaticos.GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS
import estaticos.GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO
import estaticos.GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL
import estaticos.GestorSalida.ENVIAR_Ea_MENSAJE_RECETAS
import estaticos.GestorSalida.ENVIAR_Ec_RESULTADO_RECETA
import estaticos.GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE
import estaticos.GestorSalida.ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS
import estaticos.GestorSalida.ENVIAR_ErK_RESULTADO_TRABAJO
import estaticos.GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA
import estaticos.GestorSalida.ENVIAR_GDF_FORZADO_PERSONAJE
import estaticos.GestorSalida.ENVIAR_IO_ICONO_OBJ_INTERACTIVO
import estaticos.GestorSalida.ENVIAR_IQ_NUMERO_ARRIBA_PJ
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.GestorSalida.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO
import estaticos.GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO
import estaticos.GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO
import estaticos.GestorSalida.ENVIAR_Ow_PODS_DEL_PJ
import estaticos.GestorSalida.ENVIAR_cs_CHAT_MENSAJE
import estaticos.Mundo
import estaticos.Mundo.Duo
import sprites.Exchanger
import utilidades.algoritmos.RandomCondicionado
import variables.mapa.Celda
import variables.objeto.Objeto
import variables.objeto.ObjetoModelo.CAPACIDAD_STATS
import variables.personaje.Personaje
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.*

class Trabajo(// private static float TOLERANCIA_NORMAL = 1.0f, TOLERANCIA_VIP = 1.8f;
    val trabajoID: Int, min: Int, max: Int, esCraft: Boolean, nSuerteTiempo: Int,
    xpGanada: Int, oficio: StatOficio?
) : Runnable, Exchanger {
    private var _esCraft = false
    private val _esForjaMagia: Boolean
    private val _statOficio: StatOficio?
    var casillasMin = 1
    var casillasMax = 1
    var suerte = 100
    var tiempo = 0
    var _thread: Thread? = null
    private var _xpGanadaRecoleccion = 0
    private var _cuantasRepeticiones = 0
    private var _varios = false
    private var _interrumpir: Byte = 0
    private var _ingredientes: MutableMap<Int, Int>? = null
    private var _ultimosIngredientes: MutableMap<Int, Int>? = null
    private var _artesano: Personaje? = null
    private var _cliente: Personaje? = null
    var celda: Celda? = null
        private set
    private var _finThread = true
    // taller
    private var kamasPaga: Long = 0
    private var kamasSiSeConsigue: Long = 0
    private var _ok1 = false
    private var _ok2 = false
    private var _objArtesano: ArrayList<Duo<Int, Int>>? = null
    private var _objCliente: ArrayList<Duo<Int, Int>>? = null
    private var _objetosPago: ArrayList<Duo<Int, Int>>? = null
    private var _objetosSiSeConsegui: ArrayList<Duo<Int, Int>>? = null
    var objRunaOPocima: Objeto? = null
    @Synchronized
    fun iniciarTrabajo(perso: Personaje?, idUnica: Int, celda: Celda?): Boolean {
        _artesano = perso
        this.celda = celda
        return if (_esCraft) {
            ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(_artesano!!, 3, "$casillasMax;$trabajoID")
            ENVIAR_GDF_FORZADO_PERSONAJE(_artesano!!, celda!!.id.toString() + ";" + 2 + ";" + 1)
            _artesano!!.tipoExchange = Constantes.INTERCAMBIO_TIPO_TALLER
            _artesano!!.exchanger = this
            false
        } else { // Recolecta
            if (celda!!.objetoInteractivo != null && celda.objetoInteractivo?.puedeIniciarRecolecta()!!) {
                celda.objetoInteractivo!!.iniciarRecolecta(tiempo.toLong())
                ENVIAR_GA_ACCION_JUEGO_AL_MAPA(
                    _artesano!!.mapa, idUnica, 501, _artesano!!.Id.toString() + "", celda
                        .id.toString() + "," + tiempo
                )
                iniciarThread()
            }
            true
        }
    }

    fun puedeFinalizarRecolecta(): Boolean {
        return if (celda!!.objetoInteractivo == null) {
            false
        } else celda!!.objetoInteractivo!!.puedeFinalizarRecolecta()
    }

    @get:Synchronized
    val expFinalizarRecoleccion: Int
        get() {
            if (celda!!.objetoInteractivo == null) {
                return 0
            }
            val coefEstrellas = celda!!.objetoInteractivo!!.bonusEstrellas / 100f
            celda!!.objetoInteractivo!!.activandoRecarga(Constantes.OI_ESTADO_VACIANDO, Constantes.OI_ESTADO_VACIO)
            return (preExp(_xpGanadaRecoleccion) + _xpGanadaRecoleccion * coefEstrellas).toInt()
        }

    private fun preExp(exp: Int): Int {
        var exp = exp
        exp *= AtlantaMain.RATE_XP_OFICIO
        var finalExp = exp
        if (_artesano != null) {
            if (AtlantaMain.RATE_XP_OFICIO_ABONADOS > 1) {
                if (_artesano!!.esAbonado()) {
                    finalExp = (finalExp * AtlantaMain.RATE_XP_OFICIO_ABONADOS).toInt()
                }
            }
        }
        return finalExp
    }

    @Synchronized
    fun recogerRecolecta() {
        if (celda!!.objetoInteractivo == null) {
            return
        }
        val estrellas = celda!!.objetoInteractivo!!.bonusEstrellas
        val especial = getRandomInt(0, 100 - AtlantaMain.PROBABILIDAD_RECURSO_ESPECIAL) == 0
        var cantidad = if (casillasMax > casillasMin) getRandomInt(casillasMin, casillasMax) else casillasMin
        if (especial) {
            cantidad = 1
        }
        var cantidadTotal = cantidad
        if (_artesano!!.alasActivadas() && _artesano!!.mapa.subArea!!.alineacion == _artesano!!.alineacion) {
            val balance = Mundo.getBalanceMundo(_artesano!!)
            val bonusExp = Mundo.getBonusAlinExp(_artesano!!)
            cantidadTotal += (cantidad * balance * bonusExp / 100).toInt()
        }
        cantidadTotal += cantidad * estrellas / 100
        if (cantidadTotal > 0) {
            val OM = Mundo.getObjetoModelo(Constantes.getObjetoPorRecurso(trabajoID, especial))
            if (OM != null) {
                _artesano!!.addObjIdentAInventario(
                    OM.crearObjeto(
                        cantidadTotal, Constantes.OBJETO_POS_NO_EQUIPADO,
                        CAPACIDAD_STATS.RANDOM
                    ), false
                )
                ENVIAR_Ow_PODS_DEL_PJ(_artesano!!)
            } else {
                redactarLogServidorln("El idTrabajoMod $trabajoID no tiene objeto para recolectar")
            }
            ENVIAR_IQ_NUMERO_ARRIBA_PJ(_artesano!!, _artesano!!.Id, cantidadTotal)
        }
    }

    fun esCraft(): Boolean {
        return _esCraft
    }

    fun esTaller(): Boolean {
        return _cliente != null
    }

    fun esFM(): Boolean {
        return _esForjaMagia
    }

    fun sepuedemostrarProb(): Boolean {
        var objAMaguear: Objeto? = null
        var statMagueo = -1
        for (idIngrediente in _ingredientes!!.keys) {
            val ing = _artesano!!.getObjeto(idIngrediente) ?: return false
            val idModelo = ing.objModeloID
            val statRuna = Constantes.getStatPorRunaPocima(ing)
            if (statRuna > 0) {
                statMagueo = statRuna
                if (objRunaOPocima == ing) {
                    return false
                }
                objRunaOPocima = ing
            } else if (idModelo == 7508) { // runa de firma
            } else {
                val tipo = ing.objModelo?.tipo?.toInt()
                if (tipo in 1..11 || tipo in 16..22 || tipo == 81 || tipo == 102 || tipo == 114 || ing
                        .objModelo?.costePA ?: 0 > 0
                ) {
                    objAMaguear = ing
                }
            }
        }
        if (_statOficio == null) {
            return false
        }
        if (objAMaguear == null) {
            return false
        }
        if (objRunaOPocima == null) {
            return false
        }
        var pesoRuna = getPesoStat(statMagueo)
        when (statMagueo) {
            96, 97, 98, 99 -> pesoRuna = 1f
        }
        if (pesoRuna <= 0) {
            return false
        }
        return true
    }

    fun mostrarProbabilidades(perso: Personaje?) {
        if (perso == null) {
            return
        }
        val precio = AtlantaMain.KAMAS_MOSTRAR_PROBABILIDAD_FORJA
//        if (precio <= 0) {
//            ENVIAR_cs_CHAT_MENSAJE(perso, "<b>NO SE PUEDE USAR ESTA ACCION</b>", Constantes.COLOR_ROJO)
//            return
//        }
        if (perso.kamas < precio) {
            ENVIAR_Im_INFORMACION(perso, "1128;$precio")
            return
        }
        perso.addKamas(-precio.toLong(), true, true)
        var objAMaguear: Objeto? = null
        var objRunaOPocima: Objeto? = null
        var statMagueo = -1
        var valorRuna = 0
        var pesoPlusRuna = 0
        for (idIngrediente in _ingredientes!!.keys) {
            val ing = _artesano?.getObjeto(idIngrediente) ?: continue
//            if (ing == null) {
//                ENVIAR_cs_CHAT_MENSAJE(perso, "<b>HAY UN INGREDIENTE NULO</b>", Constantes.COLOR_ROJO)
//                return
//            }
            val idModelo = ing.objModeloID
            val statRuna = Constantes.getStatPorRunaPocima(ing)
            if (statRuna > 0) {
                statMagueo = statRuna
                valorRuna = Constantes.getValorPorRunaPocima(ing)
                pesoPlusRuna = Constantes.getPotenciaPlusRuna(ing)
                objRunaOPocima = ing
            } else if (idModelo == 7508) { // runa de firma
            } else {
                val tipo = ing.objModelo?.tipo?.toInt()
                if (tipo in 1..11 || tipo in 16..22 || tipo == 81 || tipo == 102 || tipo == 114 || ing
                        .objModelo?.costePA ?: 0 > 0
                ) {
                    objAMaguear = ing
                }
            }
        }
        if (_statOficio == null) {
//            ENVIAR_cs_CHAT_MENSAJE(perso, "<b>EL STATOFICIO ES NULO</b>", Constantes.COLOR_ROJO)
            return
        }
        if (objAMaguear == null) {
//            ENVIAR_cs_CHAT_MENSAJE(perso, "<b>EL OBJETO A MAGUEAR ES NULO</b>", Constantes.COLOR_ROJO)
            return
        }
        if (objRunaOPocima == null) {
//            ENVIAR_cs_CHAT_MENSAJE(perso, "<b>LA RUNA O POCIMA ES NULO</b>", Constantes.COLOR_ROJO)
            return
        }
        var pesoRuna = getPesoStat(statMagueo)
        when (statMagueo) {
            96, 97, 98, 99 -> pesoRuna = 1f
        }
        if (pesoRuna <= 0) {
            ENVIAR_cs_CHAT_MENSAJE(perso, "<b>RUNA FORJAMAGIA INCORRECTA</b>", Constantes.COLOR_ROJO)
            return
        }
        var ExitoCritico = 0
        var ExitoNormal = 0
        var FallaNormal = 0
        var FalloCritico = 0
        var resultados = IntArray(4)
        resultados = getProbabilidadesMagueo(objAMaguear, objRunaOPocima, statMagueo, valorRuna, pesoPlusRuna)
        ExitoCritico = resultados[0]
        ExitoNormal = resultados[1]
        FallaNormal = resultados[2]
        FalloCritico = resultados[3]
        if (perso.cuenta.idioma.equals("fr", ignoreCase = true)) {
            ENVIAR_cs_CHAT_MENSAJE(
                perso, "La probabilité de FM ton item <b>" + objAMaguear.objModelo
                    ?.nombre + "</b>:", Constantes.COLOR_NEGRO
            )
            ENVIAR_cs_CHAT_MENSAJE(
                perso, "<b>[Succès critique] = $ExitoCritico%</b>",
                Constantes.COLOR_AZUL
            )
            ENVIAR_cs_CHAT_MENSAJE(
                perso, "<b>[Succès] = $ExitoNormal%</b>",
                Constantes.COLOR_VERDE_OSCURO
            )
            ENVIAR_cs_CHAT_MENSAJE(perso, "<b>[Echec] = $FallaNormal%</b>", Constantes.COLOR_NARANJA)
            ENVIAR_cs_CHAT_MENSAJE(
                perso, "<b>[Echec Critique] = $FalloCritico%</b>",
                Constantes.COLOR_ROJO
            )
        } else {
            ENVIAR_cs_CHAT_MENSAJE(
                perso, "La probabilidad de magueo del objeto <b>" + objAMaguear.objModelo
                    ?.nombre + "</b> con la <b>" + objRunaOPocima.objModelo?.nombre + "</b>", Constantes.COLOR_NEGRO
            )
            ENVIAR_cs_CHAT_MENSAJE(
                perso, "<b>[Exito Crítico] = $ExitoCritico%</b>",
                Constantes.COLOR_AZUL
            )
            ENVIAR_cs_CHAT_MENSAJE(
                perso, "<b>[Exito Normal] = $ExitoNormal%</b>",
                Constantes.COLOR_VERDE_OSCURO
            )
            ENVIAR_cs_CHAT_MENSAJE(
                perso, "<b>[Fallo Normal] = $FallaNormal%</b>",
                Constantes.COLOR_NARANJA
            )
            ENVIAR_cs_CHAT_MENSAJE(
                perso, "<b>[Fallo Crítico] = $FalloCritico%</b>",
                Constantes.COLOR_ROJO
            )
            perso.enviarmensajeNegro("<b>[Pozo Residual] = ${objAMaguear.stats.getStatParaMostrar(Constantes.STAT_POZO_RESIDUAL)}</b>")
            val pesoRunaRestar =
                ceil(getPesoStat(statMagueo) * valorRuna.toFloat()).toInt()
            perso.enviarmensajeNegro("<b>[Peso Runa] = ${pesoRunaRestar}</b>")
        }
    }

    private fun getProbabilidadesMagueo(
        objMaguear: Objeto, objRuna: Objeto, statMagueo: Int, cantAumRuna: Int,
        pesoPlus: Int
    ): IntArray {
        val probabilidades = IntArray(4)
        val objModelo = objMaguear.objModelo ?: return probabilidades
        val razonMax = AtlantaMain.NIVEL_MAX_PERSONAJE / AtlantaMain.NIVEL_MAX_OFICIO.toFloat()
        var nivelOficio = (razonMax * _statOficio!!.nivel / objModelo.nivel).toInt()
        if (nivelOficio > 25) {
            nivelOficio = 25
        } else if (nivelOficio < 0) {
            nivelOficio = 0
        }
        when (statMagueo) {
            96, 97, 98, 99 -> {
                var suerte = objModelo.probabilidadGC * objModelo.costePA / (objModelo.bonusGC + objMaguear
                    .dañoPromedioNeutral)
                suerte += cantAumRuna + nivelOficio
                if (suerte > 100) {
                    suerte = 100
                } else if (suerte < 5) {
                    suerte = 5
                }
                probabilidades[0] = suerte
                probabilidades[1] = 0
                probabilidades[2] = 100 - suerte
                probabilidades[3] = 0
            }
            else -> {
                var pozoResidual = 0
                if (AtlantaMain.PARAM_FM_CON_POZO_RESIDUAL) {
                    pozoResidual += objMaguear.stats.getStatParaMostrar(Constantes.STAT_POZO_RESIDUAL)
                }
                var pesoGlActual = 0f
                var pesoGlMin = 0f
                var pesoGlMax = 0f
                var cantStMin = 0
                var cantStMax = 0
                var cantStActual = 0
                var pesoStActual = 0f
                var pesoStMax = 0f
                var pesoStMin = 0f
                pesoGlActual -= pozoResidual.toFloat()
                if (AtlantaMain.MODO_DEBUG) {
                    println("-------------- FORMULA FM --------------")
                    println("statMagueo: $statMagueo")
                    println("pozoResidual: $pozoResidual")
                }
                var pesoExo = 0f
                var pesoExcesoOver = 0f
                for (entry in objMaguear.stats.entrySet) {
                    val statID = entry.key
                    var cant = entry.value
                    val statPositivo = Constantes.getStatPositivoDeNegativo(statID)
                    var coef = 1f
                    if (statPositivo != statID) {
                        cant *= -1
                    }
                    if (statPositivo == statMagueo) {
                        cantStActual = cant
                        pesoStActual = getPesoStat(statPositivo) * cant
                    }
                    if (objModelo.tieneStatInicial(statPositivo)) {
                        val max = objModelo.getDuoInicial(statPositivo)!!._segundo
                        if (max < cant) { // over
                            if (statPositivo != statMagueo) {
                                pesoExcesoOver += getPesoStat(statPositivo) * (cant - max)
                            }
                            coef = 1.2f
                        }
                    } else { // exo
                        if (statPositivo != statMagueo) {
                            pesoExo += getPesoStat(statPositivo) * cant
                        }
                        coef = 1.4f
                    }
                    pesoGlActual += getPesoStat(statPositivo) * cant * coef
                }
                for ((statID, value) in objModelo.statsIniciales) {
                    val statMin = value._primero
                    val statMax = value._segundo
                    if (statID == statMagueo) {
                        cantStMin = statMin
                        cantStMax = statMax
                        pesoStMin = getPesoStat(statID) * statMin
                        pesoStMax = getPesoStat(statID) * statMax
                    }
                    pesoGlMin += getPesoStat(statID) * statMin
                    pesoGlMax += getPesoStat(statID) * statMax
                }
                // if (Bustemu.RATE_FM != 1) {///
// cantStMax *= Bustemu.RATE_FM;
// pesoStMax *= Bustemu.RATE_FM;
// pesoGlMax *= Bustemu.RATE_FM;
// }
                var tipoMagueo = MAGUEO_EXO
                if (pesoStMax != 0f) {
                    tipoMagueo = MAGUEO_NORMAL // 0
                }
                if (tipoMagueo == MAGUEO_NORMAL && cantStActual + cantAumRuna > cantStMax) { // es stat over
                    tipoMagueo = MAGUEO_OVER // 1
                }
                val pesoRuna = ceil(getPesoStat(statMagueo) * cantAumRuna.toDouble()).toInt()
                if (AtlantaMain.MODO_DEBUG) {
                    println("tipoMagueo: $tipoMagueo")
                    println("cantAumRuna: $cantAumRuna , pesoRuna: $pesoRuna")
                    println("cantStMax: $cantStMax , cantStActual: $cantStActual")
                    println(
                        "pesoGlMax: " + pesoGlMax + " , pesoGlMin: " + pesoGlMin + " , pesoGlActual: "
                                + pesoGlActual
                    )
                    println(
                        "pesoStMax: " + pesoStMax + " , pesoStMin: " + pesoStMin + " , pesoStActual: "
                                + pesoStActual
                    )
                }
                var puede = true
                if (pesoGlMax == 0f) {
                    puede = false
                }
                pesoGlMax += pesoPlus.toFloat() // el aumento de la runa plus
                if (pesoStMax <= pesoStActual && Constantes.excedioLimitePeso(objRuna, cantStActual + cantAumRuna)) {
                    if (AtlantaMain.MODO_DEBUG) {
                        println("fallo en 6")
                    }
                    puede = false
                }
                when (tipoMagueo) {
                    MAGUEO_EXO -> {
                        if (pesoGlActual < 0 && pesoGlMax < 0) {
                            if (AtlantaMain.MODO_DEBUG) {
                                println("fallo en 1")
                            }
                            puede = false
                        }
//                        if (pesoStActual * 3 + pesoRuna + pesoGlActual >= pesoGlMax * 2) {
//                            if (AtlantaMain.MODO_DEBUG) {
//                                println("fallo en 2")
//                            }
//                            puede = false
//                        }
                        if (Constantes.excedioLimiteMagueoDeRuna(objRuna.objModeloID, cantStActual + cantAumRuna)) {
                            if (AtlantaMain.MODO_DEBUG) {
                                println("fallo en 3")
                            }
                            puede = false
                        }
                        if (Constantes.excedioLimiteExomagia(statMagueo, cantStActual + cantAumRuna)) {
                            puede = false
                        }
                    }
                    MAGUEO_OVER -> {
                        if (pesoStActual + pesoRuna > pesoStMax * 2) {
                            if (AtlantaMain.MODO_DEBUG) {
                                println("fallo en 5")
                            }
                            puede = false
                        }
                        if (Constantes.excedioLimiteOvermagia(statMagueo, cantStActual + cantAumRuna)) {
                            puede = false
                        }
                        if (cantStMin < 0 && (cantStActual >= 0 || cantStActual >= cantStMax)) {
                            if (AtlantaMain.MODO_DEBUG) {
                                println("fallo en 7")
                            }
                            puede = false
                        }
                        if (cantStMax < 0 && cantStMax < cantStActual + cantAumRuna) {
                            if (AtlantaMain.MODO_DEBUG) {
                                println("fallo en 8")
                            }
                            puede = false
                        }
                    }
                    MAGUEO_NORMAL -> {
                        if (cantStMin < 0 && (cantStActual >= 0 || cantStActual >= cantStMax)) {
                            if (AtlantaMain.MODO_DEBUG) {
                                println("fallo en 7")
                            }
                            puede = false
                        }
                        if (cantStMax < 0 && cantStMax < cantStActual + cantAumRuna) {
                            if (AtlantaMain.MODO_DEBUG) {
                                println("fallo en 8")
                            }
                            puede = false
                        }
                    }
                }
                if (!puede) {
                    if (AtlantaMain.MODO_DEBUG) {
                        println("No se puede maguear, esta fuera de las estadisticas")
                    }
//                    Mundo.getPersonaje(objMaguear.dueñoTemp)?.enviarmensajeNegro("No intentes mas esto, No entrará esta runa en este item")
                    var FC = 40 + pesoRuna / 2
                    if (AtlantaMain.PROBABLIDAD_PERDER_STATS_FM != -1) {
                        FC = AtlantaMain.PROBABLIDAD_PERDER_STATS_FM
                    }
                    if (FC > 100) {
                        FC = 100
                    }
                    probabilidades[0] = 0
                    probabilidades[1] = 0
                    probabilidades[2] = 100 - FC
                    probabilidades[3] = FC
                } else { // pGlActual = Math.max(0, pGlActual);
// pGlMax = Math.max(0, pGlMax);
// pGlMin = Math.max(0, pGlMin);
                    var porcGlobal = 0f
                    var porcStat = 0f
                    if (pesoGlMin < 0 || pesoGlMax < 0) {
                        if (pesoGlActual > 0) {
                            pesoGlActual += abs(pesoGlMin)
                        }
                        porcGlobal = abs(pesoGlActual * 100 / pesoGlMin)
                    } else {
                        porcGlobal = if (pesoGlMax == 0f) { // no tiene stats
                            100f
                        } else if (pesoGlMax == pesoGlMin) { // son stats fijos
                            pesoGlActual * 100f / pesoGlMax
                        } else {
                            (pesoGlActual - pesoGlMin) * 100f / (pesoGlMax - pesoGlMin)
                        }
                    }
                    if (pesoStMin < 0 || pesoStMax < 0) {
                        if (pesoStActual > 0) {
                            pesoStActual += abs(pesoStMin)
                        }
                        porcStat = abs(pesoStActual * 100 / pesoStMin)
                    } else {
                        if (pesoStMax == 0f) { // exo
                            porcStat = 100f
                        } else if (pesoStMax == pesoStMin) {
                            porcStat = pesoStActual * 100f / pesoStMax
                        } else {
                            if (tipoMagueo == MAGUEO_NORMAL) {
                                pesoStMin -= pesoExo / 2 + pesoExcesoOver / 3
                                pesoStMin = max(0f, pesoStMin)
                            }
                            porcStat = (pesoStActual - pesoStMin) * 100f / (pesoStMax - pesoStMin)
                        }
                    }
                    porcGlobal = max(0f, porcGlobal)
                    porcStat = max(0f, porcStat)
                    // el porcStat esta basado desde el Min Valor al Max Valor
                    if (AtlantaMain.MODO_DEBUG) {
                        println("Antes-> porcGlobal: $porcGlobal , porcStat: $porcStat")
                    }
                    var pSG = ((pesoStMax + pesoPlus) * 100 / pesoGlMax).toInt()
                    pSG = max(0, pSG)
                    var pG = (100 - porcGlobal).toInt()
                    var pS = (100 - porcStat).toInt()
                    var porcMaxExito = 0
                    var EC = 0
                    var EN = 0
                    var FN = 0
                    var FC = 0
                    when (tipoMagueo) {
                        MAGUEO_EXO -> porcMaxExito = 50 - (pesoRuna / 10)
                        MAGUEO_OVER -> porcMaxExito = 70 - (pesoRuna / 2)
                        MAGUEO_NORMAL -> porcMaxExito = 100
                    }
                    when (tipoMagueo) {
                        MAGUEO_EXO -> {
                            if (pesoRuna + pesoExo * 3 + pesoExcesoOver * 2 >= pesoGlMax) {
                                pG = 0
                            }
                            pS = 0
                            EN = pG / 2 // puede ser maximo 50
                        }
                        MAGUEO_OVER -> {
                            if (pesoRuna + pesoExo * 3 + pesoExcesoOver * 2 >= pesoGlMax) {
                                pG = 0
                            } else if (pesoExo > 0) {
                                if (pesoExo > pesoRuna) {
                                    pG -= (pesoExo / pesoRuna).toInt()
                                }
                            }
                            EN = pSG + pS // pS aqui es negativo
                        }
                        MAGUEO_NORMAL -> {
                            if (pesoExo > 0) {
                                if (pesoExo > pesoRuna) {
                                    pG -= (pesoExo / pesoRuna).toInt()
                                }
                            }
                            if (pesoExcesoOver > 0) {
                                if (pesoExcesoOver > pesoRuna) {
                                    pG -= (pesoExcesoOver / pesoRuna).toInt()
                                }
                            }
                            if (porcStat > AtlantaMain.MAX_PORCENTAJE_DE_STAT_PARA_FM) {
                                pS = sqrt(pS.toDouble()).toInt()
                            }
                            EN = pS
                        }
                    }
                    if (AtlantaMain.MODO_DEBUG) {
                        println("Despues-> porcGlobal: $porcGlobal , porcStat: $porcStat")
                        println("Despues-> pG: $pG, pS: $pS, pSG: $pSG")
                        println("Anterior-> EN: $EN")
                    }
                    EN = max(0, EN)
                    EN = min(porcMaxExito, EN)
                    val factorRate = (100 - EN) * AtlantaMain.RATE_FM / 100
                    EN += factorRate // aqui se adiciona el rate de la FM
                    if (AtlantaMain.MODO_DEBUG) {
                        println("Despues-> EN: $EN")
                    }
                    when (tipoMagueo) {
                        MAGUEO_EXO -> {
                            EC = ceil(EN / 2f.toDouble()).toInt()
                            EN = 0
                        }
                        MAGUEO_OVER -> {
                        }
                        MAGUEO_NORMAL -> {
                            var critico = 0
                            critico = if (pG <= 0) {
                                (pS + pSG) / 2
                            } else {
                                (pS + pSG + pG) / 2
                            }
                            critico = max(1, critico)
                            critico = min(99, critico)
                            if (critico < EN) {
                                EC = critico
                                EN -= critico
                            } else {
                                EC = EN
                                EN = 0
                            }
                        }
                    }
                    // if (pesoStMax == pesoGlMax && pesoStActual == 0) {
// // cuando tiene un solo stat
// EC = 99;
// EN = 1;
// } else
                    if (AtlantaMain.PROBABLIDAD_PERDER_STATS_FM != -1) {
                        FC = AtlantaMain.PROBABLIDAD_PERDER_STATS_FM
                        if (FC > 100 - (EN + EC)) {
                            FC = 100 - (EN + EC)
                        }
                    } else if (100 - (EN + EC) > 0) {
                        FC = RandomCondicionado.getRandA(
                            100 - (EN + EC), 100 - (EN + EC),
                            getRandomInt(0, 100).toFloat() / 100
                        )
                    } else {
                        FC = 0
//                        FC = getRandomInt(0, 100 - (EN + EC))
                    }
                    FN = (100 - (EN + EC)) - FC // no pasa nada
                    // FC = pesoRuna * FN / 100;
// if (pesoGlActual == 0) {
// FC = 0;
// }
// FN = FN - FC;
                    probabilidades[0] = EC
                    probabilidades[1] = EN
                    probabilidades[2] = FN
                    probabilidades[3] = FC
                }
            }
        }
        return probabilidades
    }

    private fun addIngrediente(idModelo: Int, cantidad: Int) {
        if (_ingredientes!![idModelo] == null) {
            _ingredientes!![idModelo] = cantidad
        } else {
            val nueva = _ingredientes!![idModelo]!! + cantidad
            _ingredientes!!.remove(idModelo)
            _ingredientes!![idModelo] = nueva
        }
    }

    fun setArtesanoCliente(artesano: Personaje?, _perso: Personaje?) {
        _artesano = artesano
        _cliente = _perso
        if (_objArtesano == null) {
            _objArtesano = ArrayList()
        }
        if (_objCliente == null) {
            _objCliente = ArrayList()
        }
        if (_objetosPago == null) {
            _objetosPago = ArrayList()
        }
        if (_objetosSiSeConsegui == null) {
            _objetosSiSeConsegui = ArrayList()
        }
    }

    // machacar recursos
    private fun iniciarTaller(
        objArtesano: ArrayList<Duo<Int, Int>>?,
        objCliente: ArrayList<Duo<Int, Int>>?
    ): Boolean {
        if (!_esCraft) {
            return false
        }
        _ingredientes!!.clear()
        for (duo in objArtesano!!) {
            addIngrediente(duo._primero, duo._segundo)
        }
        for (duo in objCliente!!) {
            addIngrediente(duo._primero, duo._segundo)
        }
        return if (Constantes.esSkillMago(trabajoID)) {
            trabajoPagoFM()
        } else {
            trabajoPagoCraft()
        }
    }

    private fun iniciarThread() {
        if (!_finThread) {
            return
        }
        _thread = thread(true, true, null, null, Thread.MAX_PRIORITY, { run() })
//        _thread.isDaemon = true
//        _thread.priority = 10
//        _thread.start()
    }

    override fun run() {
        try {
            _finThread = false
            if (_esCraft) { //                boolean esVIP = _artesano.esAbonado();
                var speedCraft = AtlantaMain.PARAM_SUPER_CRAFT_SPEED
                _ultimosIngredientes!!.clear()
                _ultimosIngredientes!!.putAll(_ingredientes!!)
                try {
                    for (a in _cuantasRepeticiones downTo 1) {
                        if (_interrumpir != MENSAJE_SIN_RESULTADO && _interrumpir != MENSAJE_OBJETO_FABRICADO) {
                            break
                        }
                        if (a == 1) {
                            speedCraft = false
                        }
                        if (_cuantasRepeticiones > 1 && a != _cuantasRepeticiones && !AtlantaMain.PARAM_SUPER_CRAFT_SPEED) {
                            ENVIAR_EA_TURNO_RECETA(_artesano!!, a)
                        }
                        speedCraft = iniciarCraft(speedCraft)
                        if (a == 1 && _interrumpir == MENSAJE_SIN_RESULTADO) {
                            _interrumpir = MENSAJE_OBJETO_FABRICADO
                        }
                        if (speedCraft != AtlantaMain.PARAM_SUPER_CRAFT_SPEED && a != 1) {
                            _interrumpir = MENSAJE_INTERRUMPIDA
                            break
                        }
                        if (_interrumpir == MENSAJE_SIN_RESULTADO || a % 10 == 0) {
                            Thread.sleep(if (speedCraft) 25 else 1000.toLong())
                        }
                    }
                } catch (ignored: Exception) {
                }
                when (_interrumpir) {
                    MENSAJE_RECETA_NO_FUNCIONA, MENSAJE_FALTA_RECURSOS -> {
                        if (!_esForjaMagia) {
                            ENVIAR_Ec_RESULTADO_RECETA(_artesano!!, "EI")
                        }
                        ENVIAR_IO_ICONO_OBJ_INTERACTIVO(_artesano!!.mapa, _artesano!!.Id, "-")
                    }
                }
                if (_cuantasRepeticiones > 1 || _interrumpir > 1) {
                    _artesano?.let { ENVIAR_Ea_MENSAJE_RECETAS(it, _interrumpir) }
                }
                mostrarProbabilidades(_artesano)
            } else { // recolecta
                try {
                    Thread.sleep(tiempo.toLong())
                } catch (ignored: Exception) {
                }
                _statOficio!!.finalizarTrabajo(_artesano!!)
            }
        } catch (e: Exception) {
            redactarLogServidorln("EXCEPTION DE RUN TRABAJO EN OFICIO esCraft: $_esCraft $e")
            e.printStackTrace()
        } finally {
            if (_ingredientes != null) {
                if (!_esForjaMagia || esTaller()) {
                    _ingredientes!!.clear()
                }
            }
            _cuantasRepeticiones = 0
            _interrumpir = MENSAJE_SIN_RESULTADO
            _finThread = true
            _thread?.interrupt()
        }
    }

    private fun iniciarCraft(esSpeedCraft: Boolean): Boolean {
        if (Constantes.esSkillMago(trabajoID)) {
            return trabajoMaguear(esSpeedCraft)
        } else {
            trabajoCraftear(esSpeedCraft)
            return esSpeedCraft
        }
    }

    private fun trabajoCraftear(esSpeedCraft: Boolean) {
        try {
            val ingredientesModelo: MutableMap<Int, Int?> = TreeMap()
            val runasModelo: MutableMap<Int, Int> = TreeMap()
            for ((objetoID, cantObjeto) in _ingredientes!!) {
                val objeto = _artesano!!.getObjeto(objetoID)
                if (objeto == null || objeto.cantidad < cantObjeto) {
                    if (objeto == null) {
                        continue
                    } else {
                        ENVIAR_Im_INFORMACION(
                            _artesano!!, "1CRAFT_NOT_ENOUGHT;" + objeto.objModeloID + " ("
                                    + objeto.cantidad + ")"
                        )
                    }
                    _interrumpir = MENSAJE_FALTA_RECURSOS
                    return
                }
                if (_varios && !esSpeedCraft) {
                    ENVIAR_EMK_MOVER_OBJETO_LOCAL(_artesano!!, 'O', "+", "$objetoID|$cantObjeto")
                }
                if (trabajoID == Constantes.SKILL_ROMPER_OBJETO) {
                    objeto.runasRomperObjeto(runasModelo, cantObjeto)
                } else {
                    ingredientesModelo[objeto.objModeloID] = cantObjeto
                }
                val nuevaCant = objeto.cantidad - cantObjeto
                if (nuevaCant == 0) {
                    _artesano!!.borrarOEliminarConOR(objetoID, true)
                } else {
                    objeto.cantidad = nuevaCant
                    try {
                        ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano!!, objeto)
                    } catch (e: Exception) {
                        redactarLogServidorln(e.toString())
                    }
                }
            }
            _varios = true
            var firmado = false
            if (ingredientesModelo.containsKey(7508)) {
                ingredientesModelo.remove(7508)
                firmado = true
            }
            var resultadoReceta = -1
            resultadoReceta = if (trabajoID == Constantes.SKILL_ROMPER_OBJETO) {
                if (runasModelo.isEmpty()) -1 else 8378
            } else {
                Mundo.getIDRecetaPorIngredientes(
                    _statOficio!!.oficio.listaRecetaPorTrabajo(trabajoID),
                    ingredientesModelo
                )
            }
            if (resultadoReceta == -1 || Mundo.getObjetoModelo(resultadoReceta) == null || trabajoID != Constantes.SKILL_ROMPER_OBJETO && _statOficio!!.oficio.puedeReceta(
                    trabajoID,
                    resultadoReceta
                )
            ) {
                _interrumpir = MENSAJE_RECETA_NO_FUNCIONA
                return
            }
            var suerte = 100
            when (trabajoID) {
                Constantes.SKILL_PELAR_PATATAS, Constantes.SKILL_UTILIZAR_BANCO, Constantes.SKILL_MACHACAR_RECURSOS -> {
                }
                Constantes.SKILL_ROMPER_OBJETO -> suerte = 99
                else -> suerte = Constantes.getSuerteNivelYSlots(_statOficio!!.nivel, ingredientesModelo.size)
            }
            val exito = AtlantaMain.PARAM_CRAFT_SIEMPRE_EXITOSA || suerte == 100 || suerte >= getRandomInt(1, 100)
            if (exito) {
                var objCreado = Mundo.getObjetoModelo(resultadoReceta)?.crearObjeto(
                    1, Constantes.OBJETO_POS_NO_EQUIPADO,
                    if (AtlantaMain.PARAM_CRAFT_PERFECTO_STATS) CAPACIDAD_STATS.MAXIMO else CAPACIDAD_STATS.RANDOM
                )
                if (trabajoID == Constantes.SKILL_ROMPER_OBJETO) {
                    val st = StringBuilder()
                    for ((key, value) in runasModelo) {
                        if (value > 0) {
                            if (st.isNotEmpty()) {
                                st.append(",")
                            }
                            st.append("1f4#").append(Integer.toHexString(key)).append("#")
                                .append(Integer.toHexString(value))
                        }
                    }
                    if (objCreado != null) {
                        objCreado.convertirStringAStats(st.toString())
                    }
                } else if (firmado) {
                    if (objCreado != null) {
                        objCreado.addStatTexto(Constantes.STAT_FACBRICADO_POR, "0#0#0#" + _artesano!!.nombre)
                    }
                }
                val igual = _artesano!!.getObjIdentInventario(objCreado, null)
                if (igual == null) {
                    _artesano!!.addObjetoConOAKO(objCreado, true)
                } else {
                    igual.cantidad = igual.cantidad + 1
                    ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano!!, igual)
                    objCreado = igual
                }
                if (!esSpeedCraft) {
                    if (objCreado != null) {
                        ENVIAR_EmK_MOVER_OBJETO_DISTANTE(
                            _artesano!!, 'O', "+", objCreado.stringObjetoConPalo(
                                objCreado
                                    .cantidad
                            )
                        )
                    }
                    ENVIAR_Ec_RESULTADO_RECETA(_artesano!!, "K;$resultadoReceta")
                }
            } else {
                if (!esSpeedCraft) {
                    ENVIAR_Ec_RESULTADO_RECETA(_artesano!!, "EF")
                    ENVIAR_Im_INFORMACION(_artesano!!, "0118")
                }
            }
            if (!esSpeedCraft) {
                ENVIAR_IO_ICONO_OBJ_INTERACTIVO(
                    _artesano!!.mapa, _artesano!!.Id, (if (exito) "+" else "-")
                            + resultadoReceta
                )
                ENVIAR_Ow_PODS_DEL_PJ(_artesano!!)
            }
            when (trabajoID) {
                Constantes.SKILL_PELAR_PATATAS, Constantes.SKILL_UTILIZAR_BANCO, Constantes.SKILL_ROMPER_OBJETO, Constantes.SKILL_MACHACAR_RECURSOS -> {
                }
                else -> {
                    val exp = Constantes.calculXpGanadaEnOficio(_statOficio!!.nivel, ingredientesModelo.size)
                    _statOficio.addExperiencia(_artesano, preExp(exp), Constantes.OFICIO_EXP_TIPO_CRAFT)
                }
            }
        } catch (e: Exception) {
            _interrumpir = MENSAJE_INTERRUMPIDA
            redactarLogServidorln("Error en receta oficio $e")
        }
    }

    private fun trabajoMaguear(esSpeedCraft2: Boolean): Boolean {
        try {
            var esSpeedCraft = esSpeedCraft2
            var objAMaguear: Objeto? = null
            var objRunaFirma: Objeto? = null
            var objRunaOPocima: Objeto? = null
            var statMagueo = -1
            var valorRuna = 0
            var pesoPlusRuna = 0
            var firmado = false
            if (_statOficio == null) {
                _interrumpir = MENSAJE_RECETA_NO_FUNCIONA
                return esSpeedCraft
            }
            for (idIngrediente in _ingredientes!!.keys) {
                val ing = _artesano!!.getObjeto(idIngrediente)
                    ?: // GestorSalida.ENVIAR_Im_INFORMACION(_artesano, "1OBJECT_DONT_EXIST;" + idIngrediente);
// _interrumpir = MENSAJE_FALTA_RECURSOS;
// return null;
                    continue
                val statRuna = Constantes.getStatPorRunaPocima(ing)
                val idModelo = ing.objModeloID
                if (idModelo == 7508) {
                    firmado = true
                    objRunaFirma = ing
                } else if (statRuna > 0) {
                    statMagueo = statRuna
                    valorRuna = Constantes.getValorPorRunaPocima(ing)
                    pesoPlusRuna = Constantes.getPotenciaPlusRuna(ing)
                    objRunaOPocima = ing
                } else {
                    when (ing.objModelo?.tipo?.toInt()) {
                        Constantes.OBJETO_TIPO_AMULETO, Constantes.OBJETO_TIPO_ARCO, Constantes.OBJETO_TIPO_VARITA, Constantes.OBJETO_TIPO_BASTON, Constantes.OBJETO_TIPO_DAGAS, Constantes.OBJETO_TIPO_ESPADA, Constantes.OBJETO_TIPO_MARTILLO, Constantes.OBJETO_TIPO_PALA, Constantes.OBJETO_TIPO_ANILLO, Constantes.OBJETO_TIPO_CINTURON, Constantes.OBJETO_TIPO_BOTAS, Constantes.OBJETO_TIPO_SOMBRERO, Constantes.OBJETO_TIPO_CAPA, Constantes.OBJETO_TIPO_HACHA, Constantes.OBJETO_TIPO_HERRAMIENTA, Constantes.OBJETO_TIPO_PICO, Constantes.OBJETO_TIPO_GUADAÑA, Constantes.OBJETO_TIPO_MOCHILA, Constantes.OBJETO_TIPO_BALLESTA, Constantes.OBJETO_TIPO_ARMA_MAGICA -> objAMaguear =
                            ing
                    }
                }
            }
            if (objAMaguear == null || objRunaOPocima == null) {
                _interrumpir = MENSAJE_FALTA_RECURSOS
                return esSpeedCraft
            }
            var pesoRuna = getPesoStat(statMagueo)
            when (statMagueo) {
                96, 97, 98, 99 -> pesoRuna = 1f
            }
            if (pesoRuna <= 0) {
                ENVIAR_cs_CHAT_MENSAJE(_artesano!!, "<b>RUNA FORJAMAGIA INCORRECTA</b>", Constantes.COLOR_ROJO)
                return esSpeedCraft
            }
            run {
                val nuevaCant = objRunaOPocima.cantidad - 1
                if (nuevaCant <= 0) {
                    _artesano!!.borrarOEliminarConOR(objRunaOPocima.id, true)
                } else {
                    objRunaOPocima.cantidad = nuevaCant
                    ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano!!, objRunaOPocima)
                }
                val n = _ingredientes!![objRunaOPocima.id]!! - 1
                if (n <= 0) {
                    _ultimosIngredientes!!.remove(objRunaOPocima.id)
                } else {
                    _ultimosIngredientes!!.put(objRunaOPocima.id, n)
                }
            }
            if (objRunaFirma != null) {
                val nuevaCant = objRunaFirma.cantidad - 1
                if (nuevaCant <= 0) {
                    _artesano!!.borrarOEliminarConOR(objRunaFirma.id, true)
                } else {
                    objRunaFirma.cantidad = nuevaCant
                    ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano!!, objRunaFirma)
                }
                val n = _ingredientes!![objRunaFirma.id]!! - 1
                if (n <= 0) {
                    _ultimosIngredientes!!.remove(objRunaFirma.id)
                } else {
                    _ultimosIngredientes!![objRunaFirma.id] = n
                }
            }
            val nuevaCantidad = objAMaguear.cantidad - 1
            if (nuevaCantidad >= 1) {
                val nuevoObj = objAMaguear.clonarObjeto(nuevaCantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                _artesano!!.addObjetoConOAKO(nuevoObj, true)
                objAMaguear.cantidad = 1
                ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_artesano!!, objAMaguear)
                _ultimosIngredientes!![objAMaguear.id] = objAMaguear.cantidad
            }
            _ingredientes!!.clear()
            _ingredientes!!.putAll(_ultimosIngredientes!!)
            var ExitoCritico = 0
            var ExitoNormal = 0
            var FallaNormal = 0
            var FalloCritico = 0
            val objModeloID = objAMaguear.objModeloID
            val jet = getRandomInt(1, 100)
            var resultados = IntArray(4)
            resultados = getProbabilidadesMagueo(objAMaguear, objRunaOPocima, statMagueo, valorRuna, pesoPlusRuna)
            ExitoCritico = resultados[0]
            ExitoNormal = resultados[1]
            FallaNormal = resultados[2]
            FalloCritico = resultados[3]
            if (ExitoCritico and ExitoNormal == 0 && FallaNormal + FalloCritico == 100 && esSpeedCraft) {
                esSpeedCraft = false
            }
            if (AtlantaMain.MODO_DEBUG) {
                println("ExitoCritico: $ExitoCritico")
                println("ExitoNormal: $ExitoNormal")
                println("FallaNormal: $FallaNormal")
                println("FalloCritico: $FalloCritico")
                println("Jet: $jet")
            }
            var r = 0
            var t = 0
//            var temp = 0
//            val rCondicionado = utilidades.algoritmos.RandomCondicionado
//            while (r == 0) {
//                var listaAcertados = arrayListOf<Int>()
//                temp = 0
//                for (i in resultados) {
//                    if (i <= 0) {
//                        temp++
//                        continue
//                    }
//                    temp++
//                    val random = rCondicionado.getRandA(4, temp, (i.toFloat() / 100))
//                    if (AtlantaMain.MODO_DEBUG) {
//                        println("random: $random\ntemp: $temp\ni: ${(i.toFloat() / 100)}")
//                    }
//                    if (random == temp) {
//                        listaAcertados.add(temp)
//                    }
//                }
//                if (listaAcertados.isEmpty()){
//                    continue
//                } else if (listaAcertados.size == 1){
//                    r=listaAcertados[0]
//                } else {
//                    while (listaAcertados.size != 1){
//                        val listaAcertadosSecundaria = arrayListOf<Int>()
//                        for (x in listaAcertados){
//                            val random2 = rCondicionado.getRandA(4, x, (resultados[x-1].toFloat() / 100))
//                            if (AtlantaMain.MODO_DEBUG) {
//                                println("Segunda Vuelta\nrandom: $random2\ntemp: $x\ni: ${(resultados[x-1].toFloat() / 100)}")
//                            }
//                            if (random2 == x){
//                                listaAcertadosSecundaria.add(x)
//                            }
//                        }
//                        if (listaAcertadosSecundaria.size >=1) {
//                            listaAcertados = listaAcertadosSecundaria
//                        }
//                    }
//                    r=listaAcertados[0]
//                }
//            }
            for (i in resultados) {
                r++
                t += i
                if (jet <= t) {
                    break
                }
            }
            if (AtlantaMain.MODO_DEBUG) {
                var res = "NADA"
                when (r.toByte()) {
                    RESULTADO_EXITO_CRITICO -> res = "RESULTADO_EXITO_CRITICO"
                    RESULTADO_EXITO_NORMAL -> res = "RESULTADO_EXITO_NORMAL"
                    RESULTADO_FALLO_NORMAL -> res = "RESULTADO_FALLO_NORMAL"
                    RESULTADO_FALLO_CRITICO -> res = "RESULTADO_FALLO_CRITICO"
                }
                println("Resultado: $res")
            }
            var exito = false
            when (r.toByte()) {
                RESULTADO_EXITO_NORMAL -> {
                    // va antes porq agrega el mensaje de la magia no ha funcionado bien
                    objAMaguear.forjaMagiaPerder(statMagueo, valorRuna, false)
                    if (!esSpeedCraft) {
                        ENVIAR_Im_INFORMACION(_artesano!!, "0194")
                    }
                    if (firmado) {
                        objAMaguear.addStatTexto(Constantes.STAT_MODIFICADO_POR, "0#0#0#" + _artesano!!.nombre)
                    }
                    objAMaguear.forjaMagiaGanar(statMagueo, valorRuna)
                    exito = true
                    if (esSpeedCraft) {
                        esSpeedCraft = false
                    }
                }
                RESULTADO_EXITO_CRITICO -> {
                    if (firmado) {
                        objAMaguear.addStatTexto(Constantes.STAT_MODIFICADO_POR, "0#0#0#" + _artesano!!.nombre)
                    }
                    objAMaguear.forjaMagiaGanar(statMagueo, valorRuna)
                    exito = true
                    if (esSpeedCraft) {
                        esSpeedCraft = false
                    }
                }
                RESULTADO_FALLO_NORMAL -> if (!esSpeedCraft) {
                    ENVIAR_Im_INFORMACION(_artesano!!, "0183")
                }
                RESULTADO_FALLO_CRITICO -> {
                    objAMaguear.forjaMagiaPerder(statMagueo, valorRuna, true)
                    if (!esSpeedCraft) {
                        ENVIAR_Im_INFORMACION(_artesano!!, "0117")
                    }
//                    if (esSpeedCraft){
//                        esSpeedCraft = false
//                    }
                }
            }
            if (!esSpeedCraft) {
                ENVIAR_OCK_ACTUALIZA_OBJETO(_artesano!!, objAMaguear)
                if (exito) {
                    ENVIAR_Ec_RESULTADO_RECETA(_artesano!!, "K;" + objAMaguear.objModeloID)
                } else {
                    ENVIAR_Ec_RESULTADO_RECETA(_artesano!!, "EF")
                }
                ENVIAR_EmK_MOVER_OBJETO_DISTANTE(
                    _artesano!!, 'O', "+", objAMaguear.stringObjetoConPalo(
                        objAMaguear
                            .cantidad
                    )
                )
                for ((key, value) in _ultimosIngredientes!!) {
                    ENVIAR_EMK_MOVER_OBJETO_LOCAL(_artesano!!, 'O', "+", "$key|$value")
                }
                ENVIAR_IO_ICONO_OBJ_INTERACTIVO(
                    _artesano!!.mapa, _artesano!!.Id, (if (exito) "+" else "-")
                            + objModeloID
                )
            }
            val exp = objAMaguear.objModelo
                ?.nivel?.toInt()?.let {
                Constantes.getExpForjamaguear(
                    getPesoStat(statMagueo) * valorRuna, it
                )
            }
            exp?.let { preExp(it) }?.let { _statOficio.addExperiencia(_artesano, it, Constantes.OFICIO_EXP_TIPO_CRAFT) }
            return esSpeedCraft
        } catch (e: Exception) {
            _interrumpir = MENSAJE_INTERRUMPIDA
            redactarLogServidorln("Error en la FM $e")
            println("Error en la FM ${e.printStackTrace()}\n Error: $e\n message ${e.message}")
        }
        return false
    }

    private fun trabajoPagoCraft(): Boolean {
        return try {
            var nuevoObj: Objeto? = null
            var r = RESULTADO_FALLO_NORMAL.toInt()
            val ingredientesPorModelo: MutableMap<Int, Int?> = TreeMap()
            for ((objetoID, cantObjeto) in _ingredientes!!) {
                val objeto = Mundo.getObjeto(objetoID)
                var dueño: Personaje? = null
                if (_artesano!!.tieneObjetoID(objetoID)) {
                    dueño = _artesano
                } else if (_cliente!!.tieneObjetoID(objetoID)) {
                    dueño = _cliente
                }
                if (dueño == null || objeto == null || objeto.cantidad < cantObjeto) {
                    _artesano!!.forjaEc = "EI"
                    _cliente!!.forjaEc = "EI"
                    if (objeto == null) {
                        ENVIAR_Im_INFORMACION(_artesano!!, "1OBJECT_DONT_EXIST;$objetoID")
                    } else {
                        ENVIAR_Im_INFORMACION(
                            _artesano!!, "1CRAFT_NOT_ENOUGHT;" + objeto.objModeloID + " ("
                                    + objeto.cantidad + ")"
                        )
                    }
                    return false
                }
                val nuevaCant = objeto.cantidad - cantObjeto
                if (nuevaCant <= 0) { // agregar si lo tiene el artesano o el cliente
                    dueño.borrarOEliminarConOR(objetoID, true)
                } else {
                    objeto.cantidad = nuevaCant
                    ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(dueño, objeto)
                }
                val idModelo = objeto.objModeloID
                if (ingredientesPorModelo[idModelo] == null) {
                    ingredientesPorModelo[idModelo] = cantObjeto
                } else {
                    val nueva = ingredientesPorModelo[idModelo]!! + cantObjeto
                    ingredientesPorModelo.remove(idModelo)
                    ingredientesPorModelo[idModelo] = nueva
                }
            }
            var firmado = false
            if (ingredientesPorModelo.containsKey(7508)) {
                ingredientesPorModelo.remove(7508)
                firmado = true
            }
            val recetaID = Mundo.getIDRecetaPorIngredientes(
                _statOficio!!.oficio.listaRecetaPorTrabajo(trabajoID),
                ingredientesPorModelo
            )
            if (recetaID == -1 || _statOficio.oficio.puedeReceta(trabajoID, recetaID)) {
                r = RESULTADO_FALLO_CRITICO.toInt()
            }
            var exito = false
            if (r != RESULTADO_FALLO_CRITICO.toInt()) {
                val suerte = Constantes.getSuerteNivelYSlots(_statOficio.nivel, ingredientesPorModelo.size)
                exito = AtlantaMain.PARAM_CRAFT_SIEMPRE_EXITOSA || suerte >= getRandomInt(1, 100)
                if (exito) {
                    r = RESULTADO_EXITO_NORMAL.toInt()
                    nuevoObj = Mundo.getObjetoModelo(recetaID)?.crearObjeto(
                        1, Constantes.OBJETO_POS_NO_EQUIPADO,
                        if (AtlantaMain.PARAM_CRAFT_PERFECTO_STATS) CAPACIDAD_STATS.MAXIMO else CAPACIDAD_STATS.RANDOM
                    )
                    if (firmado) {
                        if (nuevoObj != null) {
                            nuevoObj.addStatTexto(Constantes.STAT_FACBRICADO_POR, "0#0#0#" + _artesano!!.nombre)
                        }
                    }
                    val igual = _cliente!!.getObjIdentInventario(nuevoObj, null)
                    if (igual == null) {
                        _cliente!!.addObjetoConOAKO(nuevoObj, true)
                    } else {
                        igual.cantidad = igual.cantidad + 1
                        ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_cliente!!, igual)
                        nuevoObj = igual
                    }
                }
            }
            ENVIAR_IO_ICONO_OBJ_INTERACTIVO(
                _artesano!!.mapa,
                _artesano!!.Id,
                (if (nuevoObj == null) "-" else "+") + recetaID
            )
            ENVIAR_Ow_PODS_DEL_PJ(_artesano!!)
            ENVIAR_Ow_PODS_DEL_PJ(_cliente!!)
            when (r.toByte()) {
                RESULTADO_EXITO_NORMAL -> {
                    val statsNuevoObj = nuevoObj!!.convertirStatsAString(false)
                    val todaInfo = nuevoObj.stringObjetoConPalo(nuevoObj.cantidad)
                    ENVIAR_ErK_RESULTADO_TRABAJO(_artesano!!, "O", "+", todaInfo)
                    ENVIAR_ErK_RESULTADO_TRABAJO(_cliente!!, "O", "+", todaInfo)
                    _artesano!!.forjaEc = "K;" + recetaID + ";T" + _cliente!!.nombre + ";" + statsNuevoObj
                    _cliente!!.forjaEc = "K;" + recetaID + ";B" + _artesano!!.nombre + ";" + statsNuevoObj
                }
                RESULTADO_FALLO_NORMAL, RESULTADO_FALLO_CRITICO -> {
                    ENVIAR_Im_INFORMACION(_artesano!!, "0118")
                    _artesano!!.forjaEc = "EF"
                    _cliente!!.forjaEc = "EF"
                }
            }
            val exp = Constantes.calculXpGanadaEnOficio(_statOficio.nivel, ingredientesPorModelo.size)
            _statOficio.addExperiencia(_artesano, preExp(exp), Constantes.OFICIO_EXP_TIPO_CRAFT)
            exito
        } catch (e: Exception) {
            false
        }
    }

    private fun trabajoPagoFM(): Boolean {
        return try {
            var objAMaguear: Objeto? = null
            var objRunaFirma: Objeto? = null
            var objRunaOPocima: Objeto? = null
            var statMagueo = -1
            var valorRuna = 0
            var pesoPlusRuna = 0
            var firmado = false
            for (idIngrediente in _ingredientes!!.keys) {
                val ing = Mundo.getObjeto(idIngrediente)
                if (ing == null) {
                    _artesano!!.forjaEc = "EI"
                    _cliente!!.forjaEc = "EI"
                    return false
                }
                val statRuna = Constantes.getStatPorRunaPocima(ing)
                val idModelo = ing.objModeloID
                if (idModelo == 7508) {
                    firmado = true
                    objRunaFirma = ing
                } else if (statRuna > 0) {
                    statMagueo = statRuna
                    valorRuna = Constantes.getValorPorRunaPocima(ing)
                    pesoPlusRuna = Constantes.getPotenciaPlusRuna(ing)
                    objRunaOPocima = ing
                } else {
                    when (ing.objModelo?.tipo?.toInt()) {
                        Constantes.OBJETO_TIPO_AMULETO, Constantes.OBJETO_TIPO_ARCO, Constantes.OBJETO_TIPO_VARITA, Constantes.OBJETO_TIPO_BASTON, Constantes.OBJETO_TIPO_DAGAS, Constantes.OBJETO_TIPO_ESPADA, Constantes.OBJETO_TIPO_MARTILLO, Constantes.OBJETO_TIPO_PALA, Constantes.OBJETO_TIPO_ANILLO, Constantes.OBJETO_TIPO_CINTURON, Constantes.OBJETO_TIPO_BOTAS, Constantes.OBJETO_TIPO_SOMBRERO, Constantes.OBJETO_TIPO_CAPA, Constantes.OBJETO_TIPO_HACHA, Constantes.OBJETO_TIPO_HERRAMIENTA, Constantes.OBJETO_TIPO_PICO, Constantes.OBJETO_TIPO_GUADAÑA, Constantes.OBJETO_TIPO_MOCHILA, Constantes.OBJETO_TIPO_BALLESTA, Constantes.OBJETO_TIPO_ARMA_MAGICA -> {
                            objAMaguear = ing
                            val nuevaCantidad = objAMaguear.cantidad - 1
                            if (nuevaCantidad >= 1) {
                                val modificado = if (_artesano!!.tieneObjetoID(idIngrediente)) _artesano else _cliente
                                val nuevoObj =
                                    objAMaguear.clonarObjeto(nuevaCantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                                modificado!!.addObjetoConOAKO(nuevoObj, true)
                                objAMaguear.cantidad = 1
                                ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(modificado, objAMaguear)
                            }
                        }
                    }
                }
            }
            if (_statOficio == null || objAMaguear == null || objRunaOPocima == null) {
                _artesano!!.forjaEc = "EI"
                _cliente!!.forjaEc = "EI"
                return false
            }
            val pesoRuna = getPesoStat(statMagueo)
            if (pesoRuna <= 0) {
                _artesano!!.forjaEc = "EI"
                _cliente!!.forjaEc = "EI"
                return false
            }
            if (objRunaFirma != null) {
                val modificado = if (_artesano!!.tieneObjetoID(objRunaFirma.id)) _artesano else _cliente
                val nuevaCant = objRunaFirma.cantidad - 1
                if (nuevaCant <= 0) {
                    modificado!!.borrarOEliminarConOR(objRunaFirma.id, true)
                } else {
                    objRunaFirma.cantidad = nuevaCant
                    ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(modificado!!, objRunaFirma)
                }
            }
            run {
                val modificado = if (_artesano!!.tieneObjetoID(objRunaOPocima.id)) _artesano else _cliente
                val nuevaCant = objRunaOPocima.cantidad - 1
                if (nuevaCant <= 0) {
                    modificado!!.borrarOEliminarConOR(objRunaOPocima.id, true)
                } else {
                    objRunaOPocima.cantidad = nuevaCant
                    ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(modificado!!, objRunaOPocima)
                }
            }
            val objModeloID = objAMaguear.objModeloID
            val jet = getRandomInt(1, 100)
            val resultados = getProbabilidadesMagueo(objAMaguear, objRunaOPocima, statMagueo, valorRuna, pesoPlusRuna)
            var r = 0
            var t = 0
//            var temp = 0
//            val rCondicionado = utilidades.algoritmos.RandomCondicionado
//            while (r == 0) {
//                var listaAcertados = arrayListOf<Int>()
//                temp = 0
//                for (i in resultados) {
//                    if (i <= 0) {
//                        temp++
//                        continue
//                    }
//                    temp++
//                    val random = rCondicionado.getRandA(4, temp, (i.toFloat() / 100))
//                    if (AtlantaMain.MODO_DEBUG) {
//                        println("random: $random\ntemp: $temp\ni: ${(i.toFloat() / 100)}")
//                    }
//                    if (random == temp) {
//                        listaAcertados.add(temp)
//                    }
//                }
//                if (listaAcertados.isEmpty()){
//                    continue
//                } else if (listaAcertados.size == 1){
//                    r=listaAcertados[0]
//                } else {
//                    while (listaAcertados.size != 1){
//                        val listaAcertadosSecundaria = arrayListOf<Int>()
//                        for (x in listaAcertados){
//                            val random2 = rCondicionado.getRandA(4, x, (resultados[x-1].toFloat() / 100))
//                            if (AtlantaMain.MODO_DEBUG) {
//                                println("Segunda Vuelta\nrandom: $random2\ntemp: $x\ni: ${(resultados[x-1].toFloat() / 100)}")
//                            }
//                            if (random2 == x){
//                                listaAcertadosSecundaria.add(x)
//                            }
//                        }
//                        if (listaAcertadosSecundaria.size >=1) {
//                            listaAcertados = listaAcertadosSecundaria
//                        }
//                    }
//                    r=listaAcertados[0]
//                }
//            }
            for (i in resultados) {
                r++
                t += i
                if (jet <= t) {
                    break
                }
            }
            when (r.toByte()) {
                RESULTADO_EXITO_NORMAL -> {
                    objAMaguear.forjaMagiaPerder(statMagueo, valorRuna, false)
                    ENVIAR_Im_INFORMACION(_cliente!!, "0194")
                    ENVIAR_Im_INFORMACION(_artesano!!, "0194")
                    if (firmado) {
                        objAMaguear.addStatTexto(Constantes.STAT_MODIFICADO_POR, "0#0#0#" + _artesano!!.nombre)
                    }
                    objAMaguear.forjaMagiaGanar(statMagueo, valorRuna)
                }
                RESULTADO_EXITO_CRITICO -> {
                    if (firmado) {
                        objAMaguear.addStatTexto(Constantes.STAT_MODIFICADO_POR, "0#0#0#" + _artesano!!.nombre)
                    }
                    objAMaguear.forjaMagiaGanar(statMagueo, valorRuna)
                }
                RESULTADO_FALLO_NORMAL -> {
                    ENVIAR_Im_INFORMACION(_cliente!!, "0183")
                    ENVIAR_Im_INFORMACION(_artesano!!, "0183")
                }
                RESULTADO_FALLO_CRITICO -> {
                    objAMaguear.forjaMagiaPerder(statMagueo, valorRuna, true)
                    ENVIAR_Im_INFORMACION(_cliente!!, "0117")
                    ENVIAR_Im_INFORMACION(_artesano!!, "0117")
                }
            }
            val modificado = if (_artesano!!.tieneObjetoID(objAMaguear.id)) _artesano else _cliente
            ENVIAR_Ow_PODS_DEL_PJ(_cliente!!)
            ENVIAR_Ow_PODS_DEL_PJ(_artesano!!)
            ENVIAR_OCK_ACTUALIZA_OBJETO(modificado!!, objAMaguear)
            val todaInfo = objAMaguear.stringObjetoConPalo(objAMaguear.cantidad)
            ENVIAR_ErK_RESULTADO_TRABAJO(_artesano!!, "O", "+", todaInfo)
            ENVIAR_ErK_RESULTADO_TRABAJO(_cliente!!, "O", "+", todaInfo)
            when (r.toByte()) {
                RESULTADO_EXITO_NORMAL, RESULTADO_EXITO_CRITICO -> {
                    val statsNuevoObj = objAMaguear.convertirStatsAString(false)
                    _artesano!!.forjaEc = "K;" + objModeloID + ";T" + _cliente!!.nombre + ";" + statsNuevoObj
                    _cliente!!.forjaEc = "K;" + objModeloID + ";B" + _artesano!!.nombre + ";" + statsNuevoObj
                }
                RESULTADO_FALLO_NORMAL, RESULTADO_FALLO_CRITICO -> {
                    _artesano!!.forjaEc = "EF"
                    _cliente!!.forjaEc = "EF"
                }
            }
            val exp = objAMaguear.objModelo
                ?.nivel?.toInt()?.let {
                Constantes.getExpForjamaguear(
                    getPesoStat(statMagueo) * valorRuna, it
                )
            }
            exp?.let { preExp(it) }?.let { _statOficio.addExperiencia(_artesano, it, Constantes.OFICIO_EXP_TIPO_CRAFT) }
            r == RESULTADO_EXITO_CRITICO.toInt() || r == RESULTADO_EXITO_NORMAL.toInt()
        } catch (e: Exception) {
            false
        }
    }

    fun limpiarReceta() {
        _ingredientes?.clear()
        _ultimosIngredientes?.clear()
        kamasSiSeConsigue = 0
        kamasPaga = kamasSiSeConsigue
        _ok2 = false
        _ok1 = _ok2
        _cliente = null
        _artesano = null
        if (_objArtesano != null) {
            _objArtesano!!.clear()
        }
        if (_objCliente != null) {
            _objCliente!!.clear()
        }
        if (_objetosPago != null) {
            _objetosPago!!.clear()
        }
        if (_objetosSiSeConsegui != null) {
            _objetosSiSeConsegui!!.clear()
        }
    }

    fun craftearXVeces(cantidad: Int) {
        if (_esCraft) {
            if (_cuantasRepeticiones > 0) {
                return
            }
            try {
                _cuantasRepeticiones = abs(cantidad)
                _interrumpir = MENSAJE_SIN_RESULTADO
                _varios = false
                iniciarThread()
            } catch (ignored: Exception) {
            }
        }
    }

    fun interrumpirReceta() {
        if (_esCraft) {
            _interrumpir = MENSAJE_INTERRUMPIDA
        }
    }

    fun ponerIngredUltRecet() {
        if (_ultimosIngredientes == null || _ultimosIngredientes!!.isEmpty() || _ingredientes == null || _ingredientes!!.isNotEmpty()) {
            return
        }
        _ingredientes!!.putAll(_ultimosIngredientes!!)
        for ((key, value) in _ingredientes!!) {
            val objeto = _artesano!!.getObjeto(key) ?: continue
            if (objeto.cantidad < value) {
                continue
            }
            ENVIAR_EMK_MOVER_OBJETO_LOCAL(_artesano!!, 'O', "+", objeto.id.toString() + "|" + value)
        }
    }

    override fun cerrar(perso: Personaje?, exito: String) {
        if (_artesano != null) {
            _artesano!!.cerrarVentanaExchange(exito)
            _artesano!!.setInvitandoA(null, "")
            _artesano!!.setInvitador(null, "")
        }
        if (_cliente != null) {
            _cliente!!.cerrarVentanaExchange(exito)
            _cliente!!.setInvitandoA(null, "")
            _cliente!!.setInvitador(null, "")
        }
        interrumpirReceta()
        limpiarReceta()
    }

    override fun getListaExchanger(perso: Personaje): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Synchronized
    override fun botonOK(perso: Personaje) {
        if (_artesano!!.Id == perso.Id) {
            _ok1 = !_ok1
            ENVIAR_EK_CHECK_OK_INTERCAMBIO(_artesano!!, _ok1, perso.Id)
            ENVIAR_EK_CHECK_OK_INTERCAMBIO(_cliente!!, _ok1, perso.Id)
        } else if (_cliente!!.Id == perso.Id) {
            _ok2 = !_ok2
            ENVIAR_EK_CHECK_OK_INTERCAMBIO(_artesano!!, _ok2, perso.Id)
            ENVIAR_EK_CHECK_OK_INTERCAMBIO(_cliente!!, _ok2, perso.Id)
        } else {
            return
        }
        if (_ok1 && _ok2) {
            aplicar()
        }
    }

    private fun desCheck() {
        _ok1 = false
        _ok2 = false
        ENVIAR_EK_CHECK_OK_INTERCAMBIO(_artesano!!, false, _artesano!!.Id)
        ENVIAR_EK_CHECK_OK_INTERCAMBIO(_cliente!!, _ok1, _artesano!!.Id)
        ENVIAR_EK_CHECK_OK_INTERCAMBIO(_artesano!!, _ok2, _cliente!!.Id)
        ENVIAR_EK_CHECK_OK_INTERCAMBIO(_cliente!!, _ok2, _cliente!!.Id)
    }

    fun setKamas(tipoPago: Int, kamas: Long, kamasT: Long) {
        var kamas = kamas
        desCheck()
        if (kamas < 0) {
            return
        }
        if (tipoPago == 1) {
            if (kamasSiSeConsigue + kamas > kamasT) {
                kamas = kamasT - kamasSiSeConsigue
            }
            kamasPaga = kamas
        } else {
            if (kamasPaga + kamas > kamasT) {
                kamas = kamasT - kamasPaga
            }
            kamasSiSeConsigue = kamas
        }
        ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_artesano!!, tipoPago, "G", "+", kamas.toString() + "")
        ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_cliente!!, tipoPago, "G", "+", kamas.toString() + "")
    }

    @Synchronized
    fun aplicar() {
        try {
            val resultado = iniciarTaller(_objArtesano, _objCliente)
            val oficio = _artesano!!.getStatOficioPorTrabajo(trabajoID)
            if (_cliente!!.kamas < kamasSiSeConsigue + kamasPaga) {
                kamasPaga = _cliente!!.kamas
                kamasSiSeConsigue = 0
            }
            if (oficio != null) {
                if (resultado) {
                    _cliente!!.addKamas(-kamasSiSeConsigue, true, true)
                    _artesano!!.addKamas(kamasSiSeConsigue, true, true)
                    for (duo in _objetosSiSeConsegui!!) {
                        try {
                            val cant = duo._segundo
                            if (cant == 0) {
                                continue
                            }
                            val obj = _cliente!!.getObjeto(duo._primero)
                            if (obj.cantidad - cant < 1) {
                                _cliente!!.borrarOEliminarConOR(duo._primero, false)
                                _artesano!!.addObjIdentAInventario(obj, true)
                            } else {
                                val nuevoOjb = obj.clonarObjeto(cant, Constantes.OBJETO_POS_NO_EQUIPADO)
                                _artesano!!.addObjIdentAInventario(nuevoOjb, false)
                                obj.cantidad = obj.cantidad - cant
                                ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_cliente!!, obj)
                            }
                        } catch (ignored: Exception) {
                        }
                    }
                }
                if (!oficio.esGratisSiFalla() || resultado) {
                    _cliente!!.addKamas(-kamasPaga, true, true)
                    _artesano!!.addKamas(kamasPaga, true, true)
                    for (duo in _objetosPago!!) {
                        try {
                            val cant = duo._segundo
                            if (cant == 0) {
                                continue
                            }
                            val obj = _cliente!!.getObjeto(duo._primero)
                            if (obj.cantidad - cant < 1) {
                                _cliente!!.borrarOEliminarConOR(duo._primero, false)
                                _artesano!!.addObjIdentAInventario(obj, true)
                            } else {
                                obj.cantidad = obj.cantidad - cant
                                ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_cliente!!, obj)
                                val nuevoOjb = obj.clonarObjeto(cant, Constantes.OBJETO_POS_NO_EQUIPADO)
                                _artesano!!.addObjIdentAInventario(nuevoOjb, false)
                            }
                        } catch (ignored: Exception) {
                        }
                    }
                }
            }
            _objetosSiSeConsegui!!.clear()
            _objetosPago!!.clear()
            _objArtesano!!.clear()
            _objCliente!!.clear()
            kamasPaga = 0
            kamasSiSeConsigue = 0
            ENVIAR_Ec_RESULTADO_RECETA(_artesano!!, _artesano!!.forjaEc)
            ENVIAR_Ec_RESULTADO_RECETA(_cliente!!, _cliente!!.forjaEc)
            _artesano!!.forjaEc = ""
            _cliente!!.forjaEc = ""
        } catch (e: Exception) {
            cerrar(null, "")
        }
    }

    private fun cantSlotsctual(): Int {
        return _objArtesano!!.size + _objCliente!!.size
    }

    @Synchronized
    override fun addObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
        if (!perso.tieneObjetoID(objeto.id) || objeto.posicion != Constantes.OBJETO_POS_NO_EQUIPADO) {
            ENVIAR_Im_INFORMACION(perso, "1OBJECT_DONT_EXIST")
            return
        }
        if (esTaller()) {
            addObjetoExchangerTaller(objeto, cantidad, perso, precio)
        } else {
            addObjetoExchangerCraft(objeto, cantidad, perso, precio)
        }
    }

    @Synchronized
    override fun remObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
        if (esTaller()) {
            addObjetoExchangerTaller(objeto, -cantidad, perso, precio)
        } else {
            addObjetoExchangerCraft(objeto, -cantidad, perso, precio)
        }
    }

    @Synchronized
    fun addObjetoExchangerCraft(objeto: Objeto, cantidad: Int, perso: Personaje?, precio: Int) {
        var cantidad = cantidad
        if (cantidad > 0) { //
            if (objeto.objModeloID == 7508) { // runa de firma
            } else if (objeto.objModelo?.esForjaMagueable() == true && _esForjaMagia || objeto.objModelo?.tipo?.toInt()?.let {
                    Constantes
                        .getTipoObjPermitidoEnTrabajo(trabajoID, it)
                } == true
            ) {
                return
            }
            if (Constantes.getStatPorRunaPocima(objeto) <= 0 && Constantes.esSkillMago(trabajoID)) {
                val coef = AtlantaMain.NIVEL_MAX_PERSONAJE / AtlantaMain.NIVEL_MAX_OFICIO.toFloat()
                if (objeto.objModelo?.nivel ?: 1 > _statOficio!!.nivel * coef) {
                    ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(
                        perso!!, 22, objeto.objModelo?.nivel
                            .toString() + ";" + ((objeto.objModelo?.nivel?.div(coef))?.toInt() ?: 1), ""
                    )
                    return
                }
            }
        }
        val cantInter: Int = if (_ingredientes!![objeto.id] == null) 0 else _ingredientes!![objeto.id]!!
        if (cantidad + cantInter > objeto.cantidad) {
            cantidad = objeto.cantidad - cantInter
        } else if (cantidad + cantInter < 0) {
            cantidad = -cantInter
        }
        if (cantidad == 0) {
            return
        }
        _ingredientes!!.remove(objeto.id)
        val nuevaCant = cantidad + cantInter
        if (nuevaCant > 0) {
            _ingredientes!![objeto.id] = nuevaCant
            ENVIAR_EMK_MOVER_OBJETO_LOCAL(perso!!, 'O', "+", objeto.id.toString() + "|" + nuevaCant)
        } else {
            ENVIAR_EMK_MOVER_OBJETO_LOCAL(perso!!, 'O', "-", objeto.id.toString() + "")
        }
        if (sepuedemostrarProb()) {
            mostrarProbabilidades(_artesano)
        }
    }

    @Synchronized
    fun addObjetoExchangerTaller(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        val objetoID = objeto.id
        val artesano = if (_artesano!!.Id == perso.Id) _artesano else _cliente
        val cliente = if (_artesano!!.Id == perso.Id) _cliente else _artesano
        val objMovedor = if (_artesano!!.Id == perso.Id) _objArtesano else _objCliente
        val objDelOtro = if (_artesano!!.Id == perso.Id) _objCliente else _objArtesano
        val duoMovedor = objMovedor?.let { Mundo.getDuoPorIDPrimero(it, objetoID) }
        val duoOtro = objDelOtro?.let { Mundo.getDuoPorIDPrimero(it, objetoID) }
        val cantInter = if (duoMovedor == null) 0 else duoMovedor._segundo
        if (cantidad + cantInter > objeto.cantidad) {
            cantidad = objeto.cantidad - cantInter
        } else if (cantidad + cantInter < 0) {
            cantidad = -cantInter
        }
        if (cantidad == 0) {
            return
        }
        if (cantidad > 0) {
            if (objeto.objModeloID == 7508) { // runa de firma
            } else if (objeto.objModelo?.esForjaMagueable() == true && _esForjaMagia || objeto.objModelo?.tipo?.toInt()?.let {
                    Constantes
                        .getTipoObjPermitidoEnTrabajo(trabajoID, it)
                } == true
            ) {
                return
            }
            if (_esForjaMagia) {
                if (Constantes.getStatPorRunaPocima(objeto) <= 0 && Constantes.esSkillMago(trabajoID)) {
                    val coef = AtlantaMain.NIVEL_MAX_PERSONAJE / AtlantaMain.NIVEL_MAX_OFICIO.toFloat()
                    if (objeto.objModelo?.nivel ?: 1 > _statOficio!!.nivel * coef) {
                        ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(
                            artesano!!, 22, objeto.objModelo?.nivel
                                .toString() + ";" + ((objeto.objModelo?.nivel?.div(coef))?.toInt() ?: 1), ""
                        )
                        ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(
                            cliente!!, 22, objeto.objModelo?.nivel
                                .toString() + ";" + ((objeto.objModelo?.nivel?.div(coef))?.toInt() ?: 1), ""
                        )
                        return
                    }
                }
            }
            if (duoMovedor == null && duoOtro == null && cantSlotsctual() >= casillasMax) { // si el item es nuevo y ya llego al limite de slots
                return
            }
        }
        desCheck()
        val str = "$objetoID|$cantidad"
        val add = "|" + objeto.objModeloID + "|" + objeto.convertirStatsAString(false)
        if (duoMovedor != null) {
            duoMovedor._segundo += cantidad
            if (duoMovedor._segundo > 0) {
                ENVIAR_EMK_MOVER_OBJETO_LOCAL(artesano!!, 'O', "+", "" + objetoID + "|" + duoMovedor._segundo)
                ENVIAR_EmK_MOVER_OBJETO_DISTANTE(
                    cliente!!, 'O', "+", "" + objetoID + "|" + duoMovedor._segundo
                            + add
                )
            } else {
                objMovedor.remove(duoMovedor)
                ENVIAR_EMK_MOVER_OBJETO_LOCAL(artesano!!, 'O', "-", objeto.id.toString() + "")
                ENVIAR_EmK_MOVER_OBJETO_DISTANTE(cliente!!, 'O', "-", objeto.id.toString() + "")
            }
        } else {
            ENVIAR_EMK_MOVER_OBJETO_LOCAL(artesano!!, 'O', "+", str)
            ENVIAR_EmK_MOVER_OBJETO_DISTANTE(cliente!!, 'O', "+", str + add)
            objMovedor!!.add(Duo(objetoID, cantidad))
        }
    }

    @Synchronized
    fun addObjetoPaga(obj: Objeto, cant: Int, pagoID: Int) {
        desCheck()
        if (cant == 1) {
        }
        val idObj = obj.id
        val str = "$idObj|$cant"
        val add = "|" + obj.objModeloID + "|" + obj.convertirStatsAString(false)
        if (pagoID == 1) {
            val duo = _objetosPago?.let { Mundo.getDuoPorIDPrimero(it, idObj) }
            if (duo != null) {
                duo._segundo += cant
                ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(
                    _artesano!!, pagoID, "O", "+", idObj.toString() + "|" + duo._segundo
                            + add
                )
                ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(
                    _cliente!!,
                    pagoID,
                    "O",
                    "+",
                    idObj.toString() + "|" + duo._segundo
                )
            } else {
                ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_artesano!!, pagoID, "O", "+", str + add)
                ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_cliente!!, pagoID, "O", "+", str)
                _objetosPago!!.add(Duo(idObj, cant))
            }
        } else {
            val duo = _objetosSiSeConsegui?.let { Mundo.getDuoPorIDPrimero(it, idObj) }
            if (duo != null) {
                duo._segundo += cant
                ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(
                    _artesano!!, pagoID, "O", "+", idObj.toString() + "|" + duo._segundo
                            + add
                )
                ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(
                    _cliente!!,
                    pagoID,
                    "O",
                    "+",
                    idObj.toString() + "|" + duo._segundo
                )
            } else {
                ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_artesano!!, pagoID, "O", "+", str + add)
                ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_cliente!!, pagoID, "O", "+", str)
                _objetosSiSeConsegui!!.add(Duo(idObj, cant))
            }
        }
    }

    @Synchronized
    fun quitarObjetoPaga(obj: Objeto, cant: Int, idPago: Int) {
        desCheck()
        val idObj = obj.id
        if (idPago == 1) {
            val duo = _objetosPago?.let { Mundo.getDuoPorIDPrimero(it, idObj) } ?: return
            ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_artesano!!, idPago, "O", "-", idObj.toString() + "")
            ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_cliente!!, idPago, "O", "-", idObj.toString() + "")
            val nuevaCantidad = duo._segundo - cant
            if (nuevaCantidad <= 0) {
                _objetosPago!!.remove(duo)
            } else {
                duo._segundo = nuevaCantidad
                ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(
                    _artesano!!, idPago, "O", "+", idObj.toString() + "|" + nuevaCantidad + "|"
                            + obj.objModeloID + "|" + obj.convertirStatsAString(false)
                )
                ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_cliente!!, idPago, "O", "+", "$idObj|$nuevaCantidad")
            }
        } else {
            val duo = _objetosSiSeConsegui?.let { Mundo.getDuoPorIDPrimero(it, idObj) } ?: return
            ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_artesano!!, idPago, "O", "-", idObj.toString() + "")
            ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_cliente!!, idPago, "O", "-", idObj.toString() + "")
            val nuevaCantidad = duo._segundo - cant
            if (nuevaCantidad <= 0) {
                _objetosSiSeConsegui!!.remove(duo)
            } else {
                duo._segundo = nuevaCantidad
                ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(
                    _artesano!!, idPago, "O", "+", idObj.toString() + "|" + nuevaCantidad + "|"
                            + obj.objModeloID + "|" + obj.convertirStatsAString(false)
                )
                ENVIAR_Ep_PAGO_TRABAJO_KAMAS_OBJETOS(_cliente!!, idPago, "O", "+", "$idObj|$nuevaCantidad")
            }
        }
    }

    @Synchronized
    fun getCantObjetoPago(idObj: Int, tipoPago: Int): Int {
        val objetos: ArrayList<Duo<Int, Int>>? = if (tipoPago == 1) {
            _objetosPago
        } else {
            _objetosSiSeConsegui
        }
        for (duo in objetos!!) {
            if (duo._primero == idObj) {
                return duo._segundo
            }
        }
        return 0
    }

    override fun addKamas(kamas: Long, perso: Personaje?) {}
    override val kamas: Long
        get() = 0L

//    override fun getListaExchanger(perso: Personaje): String { // TODO Auto-generated method stub
//        return null
//    }

    companion object {
        const val MENSAJE_SIN_RESULTADO: Byte = 0
        const val MENSAJE_OBJETO_FABRICADO: Byte = 1
        const val MENSAJE_INTERRUMPIDA: Byte = 2
        const val MENSAJE_FALTA_RECURSOS: Byte = 3
        const val MENSAJE_RECETA_NO_FUNCIONA: Byte = 4
        const val MAGUEO_EXO: Byte = 2
        const val MAGUEO_OVER: Byte = 1
        const val MAGUEO_NORMAL: Byte = 0
        const val RESULTADO_EXITO_CRITICO: Byte = 1
        const val RESULTADO_EXITO_NORMAL: Byte = 2
        const val RESULTADO_FALLO_NORMAL: Byte = 3
        const val RESULTADO_FALLO_CRITICO: Byte = 4
    }

    init {
        casillasMax = max
        casillasMin = min
        _statOficio = oficio
        if (esCraft.also { _esCraft = it }) {
            suerte = nSuerteTiempo
            _ingredientes = TreeMap()
            _ultimosIngredientes = TreeMap()
        } else {
            tiempo = nSuerteTiempo
            _xpGanadaRecoleccion = xpGanada
        }
        _esForjaMagia = Constantes.esOficioMago(_statOficio!!.oficio.id)
    }
}