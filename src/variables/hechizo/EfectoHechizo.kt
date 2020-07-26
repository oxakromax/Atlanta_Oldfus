package variables.hechizo

import estaticos.AtlantaMain
import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Camino
import estaticos.Constantes
import estaticos.Constantes.getElementoPorEfectoID
import estaticos.Encriptador.getNumeroPorValorHash
import estaticos.Formulas.getRandomInt
import estaticos.GestorSalida.ENVIAR_As_STATS_DEL_PJ
import estaticos.GestorSalida.ENVIAR_GA_ACCION_PELEA
import estaticos.GestorSalida.ENVIAR_GIe_QUITAR_BUFF
import estaticos.GestorSalida.ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA
import estaticos.GestorSalida.ENVIAR_ILF_CANTIDAD_DE_VIDA
import estaticos.GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION_A_PELEA
import estaticos.GestorSalida.ENVIAR_cs_CHAT_MENSAJE_A_PELEA
import estaticos.Mundo.getHechizo
import estaticos.Mundo.getMobModelo
import variables.mapa.Celda
import variables.mob.MobGrado
import variables.pelea.Glifo
import variables.pelea.Luchador
import variables.pelea.Pelea
import variables.pelea.Reto.EstReto
import variables.pelea.Trampa
import java.util.*
import kotlin.math.sqrt

open class EfectoHechizo {
    val hechizoID: Int

    //
    protected var _suerte: Byte = 0
    var efectoID = 0
    protected var _nivelHechizoID = 1
    var duracion = 0
    var afectados = 0
    var afectadosCond = 0
    var primerValor = -1
    var segundoValor = -1
    var tercerValor = -1
        protected set
    protected var _args = ""
    protected var _condicionHechizo = ""

    // condicion es para especificar si el buff hara efecto segun la condicion DAÑO AGUA, DAÑO TIERRA,
// CURA , MENOS_PA, NADA ... etc
    var zonaEfecto: String? = null
        protected set

    constructor(hechizoID: Int) {
        this.hechizoID = hechizoID
    }

    constructor(efectoID: Int, argsString: String, hechizo: Int, grado: Int, zonaEfecto: String?) {
        this.efectoID = efectoID
        hechizoID = hechizo
        _nivelHechizoID = grado
        this.zonaEfecto = zonaEfecto
        args = argsString
    }

    fun setCondicion(condicion: String) {
        if (duracion <= 0) {
            _condicionHechizo = ""
            return
        }
        if (!condicion.contains("BN")) {
            _condicionHechizo = condicion.toUpperCase().trim { it <= ' ' }
        }
    }

    fun esMismoHechizo(id: Int): Boolean {
        return hechizoID == id
    }

    val suerte: Int
        get() = _suerte.toInt()

    open var args: String
        get() = _args
        set(args) {
            _args = args
            val split = _args.split(",".toRegex()).toTypedArray()
            try {
                primerValor = split[0].toInt()
            } catch (ignored: Exception) {
            }
            try {
                segundoValor = split[1].toInt()
            } catch (ignored: Exception) {
            }
            try {
                tercerValor = split[2].toInt()
            } catch (ignored: Exception) {
            }
            try {
                duracion = split[3].toInt()
            } catch (ignored: Exception) {
            }
            try {
                _suerte = split[4].toByte()
            } catch (ignored: Exception) {
            }
            if (duracion <= -1) {
                duracion = -3
            }
        }

    fun getRandomValor(objetivo: Luchador): Int {
        if (segundoValor <= 0) {
            return primerValor
        }
        return if (objetivo.tieneBuff(781)) { // mala sombra
            Math.min(primerValor, segundoValor)
        } else getRandomInt(primerValor, segundoValor)
    }

    val valorParaPromediar: Int
        get() = when (efectoID) {
            5, 6, 8, 132, 141, 405, 765 -> 1
            else -> {
                val split = _args.split(",".toRegex()).toTypedArray()
                var max = 1
                try {
                    if (split[0] != "null") {
                        max = Math.max(max, split[0].toInt())
                    }
                } catch (ignored: Exception) {
                }
                try {
                    if (split[1] != "null") {
                        max = Math.max(max, split[1].toInt())
                    }
                } catch (ignored: Exception) {
                }
                max
            }
        }

    protected fun getMaxMinHechizo(objetivo: Luchador, valor: Int): Int { // System.out.println("old valor " + valor);
// if (objetivo.tieneBuff(781)) {// mala sombra
// valor = _primerValor;
// } else
        var valor = valor
        if (objetivo.tieneBuff(782)) { // brokle
            valor = Math.max(primerValor, segundoValor)
        }
        // System.out.println("new valor " + valor);
        return valor
    }

    private fun curaSiLoGolpeas(
        objetivo: Luchador,
        lanzador: Luchador,
        afectados: StringBuilder,
        daño: Int
    ) {
        if (this.javaClass == Buff::class.java) {
            return
        }
        if (objetivo.tieneBuff(786)) {
            restarPDVLuchador(null, objetivo, lanzador, afectados, -daño)
        }
    }

    private fun duracionFinal(luch: Luchador): Int {
        return if (luch.puedeJugar()) duracion + 1 else duracion
    }

    private fun quitarInvisibilidad(lanzador: Luchador, tipo: TipoDaño) {
        if (lanzador.esInvisible(0) && (tipo == TipoDaño.CAC || tipo == TipoDaño.NORMAL)) {
            lanzador.hacerseVisible()
        }
    }

    private fun aplicarHechizoDeBuff(pelea: Pelea, objetivo: Luchador, celdaObjetivo: Celda) {
        val hechizo = getHechizo(primerValor) ?: return
        val sh = hechizo.getStatsPorNivel(segundoValor) ?: return
        Hechizo.aplicaHechizoAPelea(pelea, objetivo, celdaObjetivo, sh.efectosNormales, TipoDaño.NORMAL, false)
    }

    fun aplicarAPelea(
        pelea: Pelea, lanzador: Luchador, objetivos: ArrayList<Luchador>?,
        celdaObjetivo: Celda, tipo: TipoDaño, esGC: Boolean
    ) {
        try {
            if (objetivos != null) {
                pelea.setUltAfec(objetivos.size.toByte())
                // pelea.addTiempoHechizo(objetivos.size() * 200);
            }
            if ((pelea.tipoPelea == Constantes.PELEA_TIPO_PVM.toInt() || pelea
                    .tipoPelea == Constantes.PELEA_TIPO_PVM_NO_ESPADA.toInt()) && pelea.retos != null && lanzador.esNoIA()
            ) {
                for ((retoID, reto) in pelea.retos!!) {
                    var exitoReto = reto.estado
                    if (exitoReto !== EstReto.EN_ESPERA) {
                        continue
                    }
                    val elementoDaño = getElementoPorEfectoID(efectoID).toInt()
                    when (retoID) {
                        Constantes.RETO_BARBARO -> if (tipo != TipoDaño.CAC) {
                            exitoReto = EstReto.FALLADO
                        }
                        Constantes.RETO_INCURABLE -> if (efectoID == 108) { // cura
                            exitoReto = EstReto.FALLADO
                        }
                        Constantes.RETO_ELEMENTAL -> if (elementoDaño != Constantes.ELEMENTO_NULO.toInt()) {
                            for (luch in pelea.inicioLuchEquipo2) {
                                if (Objects.requireNonNull(objetivos)!!.contains(luch)) {
                                    if (pelea.ultimoElementoReto == Constantes.ELEMENTO_NULO.toInt()) {
                                        pelea.ultimoElementoReto = elementoDaño
                                        // fija para siempre el elemento
                                    } else if (pelea.ultimoElementoReto != elementoDaño) {
                                        exitoReto = EstReto.FALLADO
                                    }
                                }
                                break
                            }
                        }
                        Constantes.RETO_CIRCULEN -> if (efectoID == Constantes.STAT_ROBA_PM || efectoID == Constantes.STAT_MENOS_PM || efectoID == Constantes.STAT_MENOS_PM_FIJO
                        ) {
                            for (luch in pelea.inicioLuchEquipo2) {
                                if (Objects.requireNonNull(objetivos)!!.contains(luch)) {
                                    exitoReto = EstReto.FALLADO
                                    break
                                }
                            }
                        }
                        Constantes.RETO_EL_TIEMPO_PASA -> if (efectoID == Constantes.STAT_ROBA_PA || efectoID == Constantes.STAT_MENOS_PA || efectoID == Constantes.STAT_MENOS_PA_FIJO
                        ) {
                            for (luch in pelea.inicioLuchEquipo2) {
                                if (Objects.requireNonNull(objetivos)!!.contains(luch)) {
                                    exitoReto = EstReto.FALLADO
                                    break
                                }
                            }
                        }
                        Constantes.RETO_PERDIDO_DE_VISTA -> if (efectoID == Constantes.STAT_MENOS_ALCANCE || efectoID == Constantes.STAT_ROBA_ALCANCE) {
                            for (luch in pelea.inicioLuchEquipo2) {
                                if (Objects.requireNonNull(objetivos)!!.contains(luch)) {
                                    exitoReto = EstReto.FALLADO
                                    break
                                }
                            }
                        }
                        Constantes.RETO_FOCALIZACION -> if (elementoDaño != Constantes.ELEMENTO_NULO.toInt()) { // es efecto de daño
                            if (reto.luchMob == null) {
                                for (luch in Objects.requireNonNull(objetivos)!!) {
                                    if (luch.equipoBin.toInt() == 1) {
                                        reto.setMob(luch)
                                        break
                                    }
                                }
                            }
                            for (luch in Objects.requireNonNull(objetivos)!!) {
                                if (luch.equipoBin.toInt() == 1) {
                                    if (reto.luchMob != null && luch.id != reto.luchMob!!.id) {
                                        exitoReto = EstReto.FALLADO
                                        break
                                    }
                                }
                            }
                        }
                        Constantes.RETO_ELITISTA, Constantes.RETO_IMPREVISIBLE ->  // son lo mismo, execpto q el mob cambia cada turno en elitista
                            if (elementoDaño != Constantes.ELEMENTO_NULO.toInt()) {
                                if (reto.luchMob != null) {
                                    for (luch in Objects.requireNonNull(objetivos)!!) {
                                        if (!pelea.inicioLuchEquipo2.contains(luch)) {
                                            continue
                                        }
                                        if (luch.id != reto.luchMob!!.id) {
                                            exitoReto = EstReto.FALLADO
                                            break
                                        }
                                    }
                                }
                            }
                        Constantes.RETO_ABNEGACION -> if (efectoID == Constantes.STAT_CURAR || efectoID == Constantes.STAT_CURAR_2) { // cura
                            for (luch in Objects.requireNonNull(objetivos)!!) {
                                if (luch.id == lanzador.id) {
                                    exitoReto = EstReto.FALLADO
                                    break
                                }
                            }
                        }
                        Constantes.RETO_DUELO, Constantes.RETO_CADA_UNO_CON_SU_MONSTRUO -> if (elementoDaño != Constantes.ELEMENTO_NULO.toInt()) {
                            for (luch in Objects.requireNonNull(objetivos)!!) {
                                if (!pelea.inicioLuchEquipo2.contains(luch)) {
                                    continue
                                }
                                if (luch.luchQueAtacoUltimo == 0) {
                                    luch.luchQueAtacoUltimo = lanzador.id
                                } else {
                                    if (luch.luchQueAtacoUltimo != lanzador.id) {
                                        exitoReto = EstReto.FALLADO
                                        break
                                    }
                                }
                            }
                        }
                    }
                    reto.estado = exitoReto
                }
            }
        } catch (e: Exception) {
            redactarLogServidorln("Exception en aplicarAPelea $e")
            e.printStackTrace()
            return
        }
        aplicarEfecto(pelea, lanzador, objetivos, celdaObjetivo, tipo, esGC)
    }

    fun aplicarEfecto(
        pelea: Pelea, lanzador: Luchador, objetivos: ArrayList<Luchador>?,
        celdaObjetivo: Celda, tipo: TipoDaño, esGC: Boolean
    ) {
        when (efectoID) {
            4 -> efecto_Telenstransporta(pelea, lanzador, celdaObjetivo)
            5 -> efecto_Empujar(objetivos, pelea, lanzador, celdaObjetivo)
            6 -> efecto_Atraer(objetivos, pelea, lanzador, celdaObjetivo)
            8 -> efecto_Intercambiar_Posiciones(objetivos, pelea, lanzador, celdaObjetivo)
            9, 786, 787, 788, 782, 781, 766, 750, 751, 765, 131, 140, 79 ->  // FIXME TODO
                efecto_Buff_Valor_Fijo(objetivos, pelea, lanzador, celdaObjetivo)
            50 -> efecto_Levantar_Jugador(pelea, lanzador, celdaObjetivo)
            51 -> efecto_Lanzar_Jugador(pelea, lanzador, celdaObjetivo)
            84, 77 -> efecto_Robo_PA_PM(objetivos, pelea, lanzador, celdaObjetivo)
            81, 108 -> efecto_Cura(objetivos, pelea, lanzador, tipo)
            82 -> efecto_Robo_PDV_Fijo(objetivos, pelea, lanzador, celdaObjetivo, tipo, esGC)
            90 -> efecto_Dona_Porc_Vida(objetivos, pelea, lanzador, celdaObjetivo)
            275, 276, 277, 278, 279, 85, 86, 87, 88, 89 -> efecto_Daños_Porc_Elemental(
                objetivos,
                pelea,
                lanzador,
                tipo,
                esGC
            )
            91, 92, 93, 94, 95 -> efecto_Roba_PDV_Elemental(objetivos, pelea, lanzador, tipo, esGC)
            96, 97, 98, 99, 100 -> efecto_Daños_Elemental(objetivos, pelea, lanzador, tipo, esGC)
            101, 127, 168, 169 -> efecto_Menos_PA_PM(objetivos, pelea, lanzador, celdaObjetivo, tipo)
            106 -> efecto_Reenvio_Hechizo(objetivos, pelea, lanzador, celdaObjetivo)
            109 -> efecto_Daños_Para_Lanzador(pelea, lanzador, celdaObjetivo, tipo, esGC)
            78, 105, 107, 110, 111, 112, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 128, 138, 142, 144, 145, 152, 153, 154, 155, 156, 157, 160, 161, 162, 163, 164, 171, 176, 177, 178, 179, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 265, 182, 183, 184, 186, 410, 411, 413, 414, 415, 416, 417, 418, 419, 425, 430, 429, 431, 432, 433, 434, 435, 436, 437, 438, 439, 440, 441, 442, 443, 444, 606, 607, 608, 609, 610, 611, 776 -> efecto_Bonus_Malus(
                objetivos,
                pelea,
                lanzador,
                celdaObjetivo,
                tipo
            )
            130 -> efecto_Robar_Kamas(objetivos, pelea, lanzador, celdaObjetivo)
            132 -> efecto_Deshechizar(objetivos, pelea, lanzador, celdaObjetivo)
            141 -> efecto_Matar_Objetivo(objetivos, pelea, lanzador, celdaObjetivo)
            143 -> efecto_Curar_Sin_Stats(objetivos, pelea, lanzador, celdaObjetivo)
            149 -> efecto_Cambiar_Apariencia(objetivos, pelea, lanzador, celdaObjetivo)
            150 -> efecto_Invisibilidad(objetivos, pelea, lanzador, celdaObjetivo)
            165 -> efecto_Dominio_Arma(objetivos, pelea, lanzador, celdaObjetivo)
            180 -> efecto_Invoca_Doble(pelea, lanzador, celdaObjetivo)
            181, 405 -> efecto_Invoca_Mob(pelea, lanzador, celdaObjetivo)
            185 -> efecto_Invoca_Estatico(pelea, lanzador, celdaObjetivo)
            202 -> efecto_Revela_Invisibles(objetivos, pelea, lanzador, celdaObjetivo)
            266, 267, 268, 269, 270, 271 -> efecto_Robo_Bonus(objetivos, pelea, lanzador, celdaObjetivo)
            293 -> efecto_Aumenta_Daños_Hechizo(pelea, lanzador, celdaObjetivo)
            300 -> {
            }
            302 -> aplicarHechizoDeBuff(pelea, lanzador, celdaObjetivo)
            301, 303, 304, 305 -> efecto_Efectos_De_Hechizos(objetivos, pelea, lanzador)
            311 -> efecto_Cura_Porc_Vida_Objetivo(objetivos, pelea, lanzador, celdaObjetivo)
            320 -> efecto_Robo_Alcance(objetivos, pelea, lanzador, celdaObjetivo)
            400 -> efecto_Poner_Trampa(pelea, lanzador, celdaObjetivo)
            401 -> efecto_Glifo_Libre(pelea, lanzador, celdaObjetivo)
            402 -> efecto_Glifo_Fin_Turno(pelea, lanzador, celdaObjetivo)
            420 -> efecto_Quita_Efectos_Hechizo(objetivos, pelea, lanzador, celdaObjetivo)
            421 -> efecto_Retroceder(objetivos, pelea, lanzador, celdaObjetivo)
            422 -> efecto_Porc_PDV_Escudo(objetivos, pelea, lanzador, celdaObjetivo)
            423 -> efecto_Avanzar(objetivos, pelea, lanzador, celdaObjetivo)
            424 -> efecto_Menos_Porc_PDV_Temporal(objetivos, lanzador, celdaObjetivo)
            666 -> {
            }
            670, 671, 672 -> efecto_Daños_Porc_Vida_Neutral(objetivos, pelea, lanzador, celdaObjetivo, tipo, esGC)
            780 -> efecto_Resucitar(pelea, lanzador, celdaObjetivo)
            783 -> efecto_Retrocede_Hasta_Cierta_Casilla(pelea, lanzador, celdaObjetivo)
            784 -> efecto_Teleport_Inicio(objetivos, pelea, lanzador, celdaObjetivo)
            950, 951 -> efecto_Estados(objetivos, pelea, lanzador, celdaObjetivo)
            else -> redactarLogServidorln("Efecto no implantado ID: " + efectoID + " args: " + _args)
        }
        //        for (Luchador L :
//                objetivos) {
//            if (L.estaMuerto()){
//                L.getPelea().addMuertosReturnFinalizo(L,lanzador);
//            }
//        }
    }

    // private void efectosDeHechizoAfeitado(Luchador lanzador, Pelea pelea, Luchador objetivo) {
// final Buff buff = objetivo.getBuffPorHechizoYEfecto(1038, Constantes.STAT_MAS_PA);
// objetivo.addBuffConGIE(Constantes.STAT_MAS_PA, buff.getPrimerValor(),
// buff.getTurnosRestantes(), _hechizoID,
// convertirArgs(buff.getPrimerValor(), Constantes.STAT_MAS_PA, buff.getArgs()), lanzador, true,
// TipoDaño.POST_TURNOS);
// GestorSalida.ENVIAR_GA_ACCION_PELEA(pelea, 7, Constantes.STAT_MAS_PA, lanzador.getID() + "",
// objetivo.getID() + ","
// + buff.getPrimerValor() + "," + buff.getTurnosRestantes());
// }
    private fun efecto_Quita_Efectos_Hechizo(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        for (objetivo in objetivos!!) {
            val buffs = ArrayList<Buff>()
            var tiene = false
            when (tercerValor) {
                2201 -> {
                    if (objetivo.tieneEstado(63)) {
                        objetivo.setEstado(63, 0)
                    }
                    if (objetivo.tieneEstado(62)) {
                        objetivo.setEstado(62, 0)
                    }
                }
                2207 -> {
                    if (objetivo.tieneEstado(64)) {
                        objetivo.setEstado(64, 0)
                    }
                    if (objetivo.tieneEstado(62)) {
                        objetivo.setEstado(62, 0)
                    }
                }
                2209 -> {
                    if (objetivo.tieneEstado(64)) {
                        objetivo.setEstado(64, 0)
                    }
                    if (objetivo.tieneEstado(63)) {
                        objetivo.setEstado(63, 0)
                    }
                }
            }
            for (buff in objetivo.buffsPelea) {
                if (buff.hechizoID != tercerValor) {
                    buffs.add(buff)
                } else {
                    tiene = true
                }
            }
            if (!tiene) {
                continue
            }
            ENVIAR_GIe_QUITAR_BUFF(pelea, 7, objetivo.id)
            objetivo.resetearBuffs(buffs)
        }
    }

    private fun efecto_Telenstransporta(
        pelea: Pelea,
        lanzador: Luchador,
        celdaObjetivo: Celda
    ) { // teletransporta
        if (lanzador.estaMuerto() || lanzador.esEstatico() || lanzador.tieneEstado(Constantes.ESTADO_PESADO.toInt()) || lanzador
                .tieneEstado(Constantes.ESTADO_ARRAIGADO.toInt()) || lanzador.tieneEstado(Constantes.ESTADO_TRANSPORTADO.toInt()) || lanzador
                .tieneEstado(Constantes.ESTADO_PORTADOR.toInt())
        ) {
            return
        }
        if (!celdaObjetivo.esCaminable(true)) {
            ENVIAR_Im_INFORMACION_A_PELEA(pelea, 7, "1CELDA_NO_CAMINABLE")
            return
        }
        if (celdaObjetivo.primerLuchador != null) {
            ENVIAR_GA_ACCION_PELEA(pelea, 7, 151, lanzador.id.toString() + "", hechizoID.toString() + "")
            return
        }
        lanzador.celdaPelea!!.moverLuchadoresACelda(celdaObjetivo)
        ENVIAR_GA_ACCION_PELEA(
            pelea, 7, 4, lanzador.id.toString() + "", lanzador.id.toString() + "," + celdaObjetivo
                .id
        )
        try {
            Thread.sleep(500)
        } catch (ignored: Exception) {
        }
        verificaTrampas(lanzador)
    }

    private fun efecto_Empujar(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        if (duracion == 0) {
            for (objetivo in objetivos!!) {
                var celdaInicio: Celda? = celdaObjetivo
                if (objetivo.celdaPelea!!.id == celdaObjetivo.id) {
                    celdaInicio = lanzador.celdaPelea
                }
                var nroCasillas = primerValor
                if (objetivo.tieneEstado(Constantes.ESTADO_ESCARIFICADO.toInt())) {
                    if (hechizoID == 3298) {
                        nroCasillas += 1
                    }
                    if (hechizoID == 3299) {
                        nroCasillas += 2
                    }
                }
                efectoEmpujones(
                    pelea,
                    lanzador,
                    objetivo,
                    celdaInicio,
                    objetivo.celdaPelea,
                    nroCasillas,
                    true
                )
            }
        }
    }

    // auto retroceder - zobal
    private fun efecto_Retroceder(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        val nroCasillas = primerValor
        // for (final Luchador objetivo : objetivos) {
        efectoEmpujones(
            pelea,
            lanzador,
            lanzador,
            celdaObjetivo,
            lanzador.celdaPelea,
            nroCasillas,
            true
        )
        // }
    }

    // auto avanzar - zobal
    private fun efecto_Avanzar(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        val nroCasillas = -primerValor
        // for (final Luchador objetivo : objetivos) {
        efectoEmpujones(
            pelea,
            lanzador,
            lanzador,
            celdaObjetivo,
            lanzador.celdaPelea,
            nroCasillas,
            false
        )
        // }
    }

    private fun efecto_Atraer(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        for (objetivo in objetivos!!) {
            var celdaInicio: Celda? = celdaObjetivo
            if (objetivo.celdaPelea!!.id == celdaObjetivo.id) {
                celdaInicio = lanzador.celdaPelea
            }
            val nroCasillas = -primerValor
            efectoEmpujones(
                pelea,
                lanzador,
                objetivo,
                celdaInicio,
                objetivo.celdaPelea,
                nroCasillas,
                false
            )
        }
    }

    private fun efecto_Porc_PDV_Escudo(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        val escudo = lanzador.pdvConBuff * primerValor / 100
        val efectoID = getStatPorEfecto(efectoID)
        for (objetivo in objetivos!!) {
            if (objetivo.estaMuerto()) {
                continue
            }
            objetivo.addEscudo(escudo)
            objetivo.addBuffConGIE(
                efectoID,
                escudo,
                duracion,
                hechizoID,
                convertirArgs(escudo, efectoID, _args),
                lanzador,
                false,
                TipoDaño.POST_TURNOS,
                _condicionHechizo
            )
        }
    }

    private fun efecto_Menos_Porc_PDV_Temporal(
        objetivos: ArrayList<Luchador>?, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        val efectoID = getStatPorEfecto(efectoID)
        for (objetivo in objetivos!!) {
            if (objetivo.estaMuerto()) {
                continue
            }
            val valor = objetivo.pdvConBuff * primerValor / 100
            objetivo.addBuffConGIE(
                efectoID,
                valor,
                duracion,
                hechizoID,
                convertirArgs(valor, efectoID, _args),
                lanzador,
                true,
                TipoDaño.POST_TURNOS,
                _condicionHechizo
            )
        }
    }

    private fun efecto_Intercambiar_Posiciones(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        if (lanzador.estaMuerto() || lanzador.esEstatico()) {
            return
        }
        if (lanzador.tieneEstado(Constantes.ESTADO_PESADO.toInt()) || lanzador.tieneEstado(Constantes.ESTADO_ARRAIGADO.toInt()) || lanzador
                .tieneEstado(Constantes.ESTADO_TRANSPORTADO.toInt()) || lanzador.tieneEstado(Constantes.ESTADO_PORTADOR.toInt())
        ) {
            return
        }
        if (objetivos == null || objetivos.isEmpty()) {
            return
        }
        val objetivo = objetivos[0]
        if (objetivo == null || objetivo.estaMuerto()) {
            return
        }
        when (hechizoID) {
            438, 449, 445 -> if (objetivo.tieneEstado(Constantes.ESTADO_ARRAIGADO.toInt()) || objetivo.tieneEstado(
                    Constantes.ESTADO_TRANSPORTADO.toInt()
                )
                || objetivo.tieneEstado(Constantes.ESTADO_PORTADOR.toInt())
            ) {
                return
            }
            else -> {
                if (objetivo.esEstatico()) {
                    return
                }
                if (objetivo.tieneEstado(Constantes.ESTADO_ARRAIGADO.toInt()) || objetivo.tieneEstado(Constantes.ESTADO_TRANSPORTADO.toInt())
                    || objetivo.tieneEstado(Constantes.ESTADO_PORTADOR.toInt())
                ) {
                    return
                }
            }
        }
        val exCeldaObjetivo = objetivo.celdaPelea
        val exCeldaLanzador = lanzador.celdaPelea
        exCeldaObjetivo!!.limpiarLuchadores()
        exCeldaLanzador!!.limpiarLuchadores()
        objetivo.celdaPelea = exCeldaLanzador
        lanzador.celdaPelea = exCeldaObjetivo
        ENVIAR_GA_ACCION_PELEA(
            pelea, 7, 4, lanzador.id.toString() + "", objetivo.id.toString() + "," + exCeldaLanzador
                .id
        )
        ENVIAR_GA_ACCION_PELEA(
            pelea, 7, 4, lanzador.id.toString() + "", lanzador.id.toString() + "," + exCeldaObjetivo
                .id
        )
        try {
            Thread.sleep(500)
        } catch (ignored: Exception) {
        }
        verificaTrampas(objetivo)
        verificaTrampas(lanzador)
    }

    // esquiva golpes retrocediendo casillas
    private fun efecto_Buff_Valor_Fijo(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        val efectoID = getStatPorEfecto(efectoID)
        val temp = ArrayList<Luchador>()
        for (objetivo in objetivos!!) {
            if (objetivo.estaMuerto()) {
                continue
            }
            temp.add(objetivo)
        }
        if (!temp.isEmpty()) {
            pelea.addBuffLuchadores(
                temp, efectoID, primerValor, duracion, hechizoID, convertirArgs(
                    primerValor, efectoID,
                    _args
                ), lanzador, TipoDaño.POST_TURNOS, _condicionHechizo
            )
        }
    }

    // permite levantar a un jugador
    private fun efecto_Levantar_Jugador(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        val objetivo = celdaObjetivo.primerLuchador
        if (lanzador.estaMuerto() || lanzador.esEstatico() || lanzador.tieneEstado(Constantes.ESTADO_TRANSPORTADO.toInt())
            || lanzador.tieneEstado(Constantes.ESTADO_PORTADOR.toInt())
        ) {
            return
        }
        if (objetivo == null || objetivo.estaMuerto() || objetivo.esEstatico() || objetivo.tieneEstado(
                Constantes.ESTADO_ARRAIGADO.toInt()
            ) || objetivo.tieneEstado(Constantes.ESTADO_TRANSPORTADO.toInt()) || objetivo.tieneEstado(
                Constantes.ESTADO_PORTADOR.toInt()
            )
        ) {
            return
        }
        objetivo.celdaPelea!!.removerLuchador(objetivo)
        objetivo.celdaPelea = lanzador.celdaPelea
        objetivo.setEstado(Constantes.ESTADO_TRANSPORTADO.toInt(), -1) // infinito
        lanzador.setEstado(Constantes.ESTADO_PORTADOR.toInt(), -1)
        objetivo.setTransportadoPor(lanzador)
        lanzador.transportando = objetivo
        ENVIAR_GA_ACCION_PELEA(pelea, 7, 50, lanzador.id.toString() + "", "" + objetivo.id)
        try {
            Thread.sleep(500)
        } catch (ignored: Exception) {
        }
    }

    // lanza a un jugador
    private fun efecto_Lanzar_Jugador(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        if (!celdaObjetivo.esCaminable(true) || celdaObjetivo.primerLuchador != null) {
            return
        }
        val objetivo = lanzador.transportando
        objetivo!!.celdaPelea!!.removerLuchador(objetivo)
        objetivo.celdaPelea = celdaObjetivo
        ENVIAR_GA_ACCION_PELEA(pelea, 7, 51, lanzador.id.toString() + "", celdaObjetivo.id.toString() + "")
        try {
            Thread.sleep(1000)
        } catch (ignored: Exception) {
        }
        pelea.quitarTransportados(lanzador)
    }

    private fun efecto_Robo_PA_PM(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        val valor = getRandomValor(lanzador)
        val afectados = StringBuilder()
        var ganados = 0
        var efectoID = getStatPorEfecto(efectoID)
        for (objetivo in objetivos!!) {
            if (objetivo.estaMuerto()) {
                continue
            }
            var paso = false
            for (b in objetivo.buffsPelea) {
                if (b.condicionBuff.isEmpty()) {
                    continue
                }
                if (efectoID == Constantes.STAT_MENOS_PA && b.condicionBuff.contains("-PA")) {
                    paso = true
                    b.aplicarBuffCondicional(objetivo)
                    continue
                }
                if (efectoID == Constantes.STAT_MENOS_PM && b.condicionBuff.contains("-PM")) {
                    paso = true
                    b.aplicarBuffCondicional(objetivo)
                    continue
                }
            }
            if (paso) {
                continue
            }
            var perdidos = getPuntosPerdidos(efectoID, valor, lanzador, objetivo)
            perdidos = getMaxMinHechizo(objetivo, perdidos)
            if (perdidos < valor) {
                ENVIAR_GA_ACCION_PELEA(
                    pelea, 7, if (efectoID == Constantes.STAT_MENOS_PM) 309 else 308, lanzador.id
                        .toString() + "", objetivo.id.toString() + "," + (valor - perdidos)
                )
            }
            if (perdidos >= 1) {
                objetivo.addBuffConGIE(
                    efectoID,
                    perdidos,
                    duracion,
                    hechizoID,
                    convertirArgs(perdidos, efectoID, _args),
                    lanzador,
                    true,
                    TipoDaño.POST_TURNOS,
                    _condicionHechizo
                )
                if (afectados.length > 0) {
                    afectados.append("¬")
                }
                afectados.append(objetivo.id).append(",").append(-perdidos).append(",").append(duracionFinal(objetivo))
                ganados += perdidos
            }
        }
        if (afectados.length > 0 && _condicionHechizo.isEmpty()) {
            ENVIAR_GA_ACCION_PELEA(pelea, 7, efectoID, lanzador.id.toString() + "", afectados.toString())
        }
        if (ganados > 0) {
            efectoID = if (this.efectoID == Constantes.STAT_ROBA_PM) Constantes.STAT_MAS_PM else Constantes.STAT_MAS_PA
            lanzador.addBuffConGIE(
                efectoID,
                ganados,
                duracion,
                hechizoID,
                convertirArgs(ganados, efectoID, _args),
                lanzador,
                true,
                TipoDaño.POST_TURNOS,
                _condicionHechizo
            )
            ENVIAR_GA_ACCION_PELEA(
                pelea, 7, efectoID, lanzador.id.toString() + "", lanzador.id.toString() + "," + ganados
                        + "," + duracionFinal(lanzador)
            )
        }
    }

    // FIXME ANALIZAR PORQ DURACION + 1
    private fun efecto_Robo_Alcance(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        val valor = getRandomValor(lanzador)
        val afectados = StringBuilder()
        var ganados = 0
        var efectoID = getStatPorEfecto(efectoID)
        val temp = ArrayList<Luchador>()
        for (objetivo in objetivos!!) {
            if (objetivo.estaMuerto()) {
                continue
            }
            if (afectados.length > 0) afectados.append("¬")
            afectados.append(objetivo.id).append(",").append(valor).append(",").append(duracionFinal(objetivo))
            temp.add(objetivo)
            ganados += valor
        }
        if (afectados.length > 0 && _condicionHechizo.isEmpty()) {
            ENVIAR_GA_ACCION_PELEA(pelea, 7, efectoID, lanzador.id.toString() + "", afectados.toString())
        }
        if (!temp.isEmpty()) {
            pelea.addBuffLuchadores(
                temp,
                efectoID,
                valor,
                duracion,
                hechizoID,
                convertirArgs(valor, efectoID, _args),
                lanzador,
                TipoDaño.POST_TURNOS,
                _condicionHechizo
            )
        }
        if (ganados > 0) {
            efectoID = Constantes.STAT_MAS_ALCANCE
            lanzador.addBuffConGIE(
                efectoID,
                ganados,
                duracion,
                hechizoID,
                convertirArgs(ganados, efectoID, _args),
                lanzador,
                true,
                TipoDaño.POST_TURNOS,
                _condicionHechizo
            )
            ENVIAR_GA_ACCION_PELEA(
                pelea, 7, efectoID, lanzador.id.toString() + "", lanzador.id.toString() + "," + ganados
                        + "," + duracionFinal(lanzador)
            )
        }
    }

    // FIXME OBSERVAR
    private fun efecto_Robo_Bonus(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        var valor = getRandomValor(lanzador)
        val afectados = StringBuilder()
        var robo = 0
        val efectoID = getStatPorEfecto(efectoID)
        val valor2 = valor
        val temp = ArrayList<Luchador>()
        for (objetivo in objetivos!!) {
            if (objetivo.estaMuerto()) {
                continue
            }
            valor = getMaxMinHechizo(objetivo, valor)
            if (valor != valor2) {
                objetivo.addBuffConGIE(
                    efectoID,
                    valor,
                    duracion,
                    hechizoID,
                    convertirArgs(valor, efectoID, _args),
                    lanzador,
                    true,
                    TipoDaño.POST_TURNOS,
                    _condicionHechizo
                )
            } else {
                temp.add(objetivo)
            }
            if (afectados.length > 0) afectados.append("¬")
            afectados.append(objetivo.id).append(",").append(valor).append(",").append(duracionFinal(objetivo))
            robo += valor
            valor = valor2
        }
        if (afectados.length > 0 && _condicionHechizo.isEmpty()) {
            ENVIAR_GA_ACCION_PELEA(pelea, 7, efectoID, lanzador.id.toString() + "", afectados.toString())
        }
        if (!temp.isEmpty()) {
            pelea.addBuffLuchadores(
                temp,
                efectoID,
                valor,
                duracion,
                hechizoID,
                convertirArgs(valor, efectoID, _args),
                lanzador,
                TipoDaño.POST_TURNOS,
                _condicionHechizo
            )
        }
        if (robo > 0) {
            val stat = getStatContrario(efectoID)
            lanzador.addBuffConGIE(
                stat,
                robo,
                duracion,
                hechizoID,
                convertirArgs(robo, stat, _args),
                lanzador,
                true,
                TipoDaño.POST_TURNOS,
                _condicionHechizo
            )
            ENVIAR_GA_ACCION_PELEA(
                pelea, 7, stat, lanzador.id.toString() + "", lanzador.id.toString() + "," + robo + ","
                        + duracionFinal(lanzador)
            )
        }
    }

    private fun efecto_Robo_PDV_Fijo(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda, tipo: TipoDaño, esGC: Boolean
    ) {
        val efectoID = getStatPorEfecto(efectoID)
        if (duracion == 0) {
            quitarInvisibilidad(lanzador, tipo)
            val afectados = StringBuilder()
            for (a in objetivos!!) {
                var objetivo = a
                if (objetivo.estaMuerto()) {
                    continue
                }
                objetivo =
                    reenvioHechizo(pelea, _nivelHechizoID, hechizoID, objetivo, lanzador, tipo)
                objetivo = sacrificio(pelea, objetivo)
                var daño = primerValor
                daño = calcularDañoFinal(
                    pelea, lanzador, objetivo, 10 + getElementoPorEfectoID(efectoID), daño.toFloat(),
                    hechizoID, tipo, esGC
                )
                daño = aplicarBuffContraGolpe(
                    efectoID,
                    daño,
                    objetivo,
                    lanzador,
                    pelea,
                    hechizoID,
                    tipo
                )
                daño = restarPDVLuchador(pelea, objetivo, lanzador, afectados, daño)
                restarPDVLuchador(pelea, lanzador, lanzador, afectados, -daño / 2)
                curaSiLoGolpeas(objetivo, lanzador, afectados, daño)
            }
            if (afectados.length > 0) {
                ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.id.toString() + "", afectados.toString())
            }
        } else {
            val temp = ArrayList<Luchador>()
            for (objetivo in objetivos!!) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                temp.add(objetivo)
            }
            if (!temp.isEmpty()) {
                pelea.addBuffLuchadores(
                    temp, efectoID, primerValor, duracion, hechizoID, convertirArgs(
                        primerValor,
                        efectoID, _args
                    ), lanzador, tipo, _condicionHechizo
                )
            }
        }
    }

    private fun efecto_Robar_Kamas(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) { // robar
// kamas
        if (pelea.tipoPelea == 0) {
            return
        }
        var valor = getRandomValor(lanzador)
        val afectados = StringBuilder()
        for (objetivo in objetivos!!) {
            val perso = objetivo.personaje
            if (objetivo.estaMuerto() || perso == null) {
                continue
            }
            if (valor > perso.kamas) {
                valor = perso.kamas.toInt()
            }
            if (valor == 0) {
                continue
            }
            perso.addKamas(-valor.toLong(), false, false)
            val perso2 = lanzador.personaje
            perso2?.addKamas(valor.toLong(), false, false)
            if (afectados.length > 0) {
                afectados.append("¬")
            }
            afectados.append(objetivo.id).append(",").append(valor)
        }
        if (afectados.length > 0 && _condicionHechizo.isEmpty()) {
            ENVIAR_GA_ACCION_PELEA(pelea, 7, 130, lanzador.id.toString() + "", afectados.toString())
        }
    }

    private fun efecto_Efectos_De_Hechizos(
        objetivos: ArrayList<Luchador>?,
        pelea: Pelea,
        lanzador: Luchador
    ) {
        val efectoID = getStatPorEfecto(efectoID)
        val temp = ArrayList<Luchador>()
        for (objetivo in objetivos!!) {
            if (objetivo.estaMuerto()) {
                continue
            }
            temp.add(objetivo)
        }
        if (!temp.isEmpty()) {
            pelea.addBuffLuchadores(
                temp, efectoID, primerValor, duracion, hechizoID, convertirArgs(
                    primerValor, efectoID,
                    _args
                ), lanzador, TipoDaño.POST_TURNOS, _condicionHechizo
            )
        }
    }

    private fun efecto_Menos_PA_PM(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda, tipo: TipoDaño
    ) {
        val valor = getRandomValor(lanzador)
        val efectoID = getStatPorEfecto(efectoID)
        val temp = ArrayList<Luchador>()
        val afectados = StringBuilder()
        loop@ for (a in objetivos!!) {
            var objetivo = a
            if (objetivo.estaMuerto()) {
                continue
            }
            var paso = false
            for (b in objetivo.buffsPelea) {
                if (b.condicionBuff.isEmpty()) {
                    continue
                }
                if (efectoID == Constantes.STAT_MENOS_PA && b.condicionBuff.contains("-PA")) {
                    paso = true
                    b.aplicarBuffCondicional(objetivo)
                    continue
                }
                if (efectoID == Constantes.STAT_MENOS_PM && b.condicionBuff.contains("-PM")) {
                    paso = true
                    b.aplicarBuffCondicional(objetivo)
                    continue
                }
            }
            if (paso) {
                continue
            }
            var perdidos = valor
            when (this.efectoID) {
                Constantes.STAT_MENOS_PA -> {
                    objetivo = reenvioHechizo(
                        pelea,
                        _nivelHechizoID,
                        hechizoID,
                        objetivo,
                        lanzador,
                        tipo
                    )
                    if (objetivo.estaMuerto()) {
                        continue@loop
                    }
                    perdidos = getPuntosPerdidos(this.efectoID, valor, lanzador, objetivo)
                    perdidos = getMaxMinHechizo(objetivo, perdidos)
                    if (perdidos < valor) { // esquivados
                        ENVIAR_GA_ACCION_PELEA(
                            pelea, 7, if (efectoID == Constantes.STAT_MENOS_PM) 309 else 308, lanzador
                                .id.toString() + "", objetivo.id.toString() + "," + (valor - perdidos)
                        )
                    }
                    if (perdidos >= 1) {
                        objetivo.addBuffConGIE(
                            efectoID,
                            perdidos,
                            duracion,
                            hechizoID,
                            convertirArgs(perdidos, this.efectoID, _args),
                            lanzador,
                            true,
                            tipo,
                            _condicionHechizo
                        )
                    }
                }
                Constantes.STAT_MENOS_PM -> {
                    perdidos = getPuntosPerdidos(this.efectoID, valor, lanzador, objetivo)
                    perdidos = getMaxMinHechizo(objetivo, perdidos)
                    if (perdidos < valor) {
                        ENVIAR_GA_ACCION_PELEA(
                            pelea, 7, if (efectoID == Constantes.STAT_MENOS_PM) 309 else 308, lanzador
                                .id.toString() + "", objetivo.id.toString() + "," + (valor - perdidos)
                        )
                    }
                    if (perdidos >= 1) {
                        objetivo.addBuffConGIE(
                            efectoID,
                            perdidos,
                            duracion,
                            hechizoID,
                            convertirArgs(perdidos, this.efectoID, _args),
                            lanzador,
                            true,
                            tipo,
                            _condicionHechizo
                        )
                    }
                }
                Constantes.STAT_MENOS_PA_FIJO, Constantes.STAT_MENOS_PM_FIJO -> temp.add(objetivo)
            }
            if (perdidos <= 0) {
                continue
            }
            if (afectados.length > 0) {
                afectados.append("¬")
            }
            afectados.append(objetivo.id).append(",").append(-perdidos).append(",").append(duracionFinal(objetivo))
        }
        if (afectados.length > 0 && _condicionHechizo.isEmpty()) {
            ENVIAR_GA_ACCION_PELEA(pelea, 7, efectoID, lanzador.id.toString() + "", afectados.toString())
        }
        if (!temp.isEmpty()) {
            pelea.addBuffLuchadores(
                temp,
                efectoID,
                valor,
                duracion,
                hechizoID,
                convertirArgs(valor, efectoID, _args),
                lanzador,
                tipo,
                _condicionHechizo
            )
        }
    }

    protected fun efecto_Cura(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        tipo: TipoDaño?
    ) { // curacion
// if (lanzador.tieneEstado(Constantes.ESTADO_ALTRUISTA)) {
// return;
// }
        val efectoID = getStatPorEfecto(efectoID)
        var modi = 0
        val perso = lanzador.personaje
        if (perso != null) {
            if (perso.tieneModfiSetClase(hechizoID)) {
                modi = perso.getModifSetClase(hechizoID, 284)
            }
        }
        val cura2 = getRandomValor(lanzador)
        if (duracion == 0) {
            val afectados = StringBuilder()
            for (objetivo in objetivos!!) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                // if (objetivo.tieneEstado(Constantes.ESTADO_ALTRUISTA)) {
// continue;
// }
                var paso = false
                for (b in objetivo.buffsPelea) {
                    if (b.condicionBuff.isEmpty()) {
                        continue
                    }
                    if (b.condicionBuff.contains("SOIN")) {
                        paso = true
                        b.aplicarBuffCondicional(objetivo)
                        continue
                    }
                }
                if (paso) {
                    continue
                }
                var cura = getMaxMinHechizo(objetivo, cura2)
                cura = calcularCuraFinal(lanzador, cura) + modi
                restarPDVLuchador(pelea, objetivo, lanzador, afectados, -cura)
            }
            if (afectados.length > 0) {
                ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.id.toString() + "", afectados.toString())
            }
        } else {
            val temp = ArrayList<Luchador>()
            for (objetivo in objetivos!!) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                temp.add(objetivo)
            }
            if (!temp.isEmpty()) {
                pelea.addBuffLuchadores(
                    temp, efectoID, primerValor, duracion, hechizoID, convertirArgs(
                        primerValor,
                        efectoID, _args
                    ), lanzador, tipo!!, _condicionHechizo
                )
            }
        }
    }

    private fun efecto_Cura_Porc_Vida_Objetivo(
        objetivos: ArrayList<Luchador>?, pelea: Pelea,
        lanzador: Luchador, celdaObjetivo: Celda
    ) {
        val efectoID = 108
        if (duracion == 0) {
            val porc = getRandomValor(lanzador)
            val afectados = StringBuilder()
            for (objetivo in objetivos!!) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                val pdvMaxBuff = objetivo.pdvMaxConBuff
                val cura = porc * pdvMaxBuff / 100
                restarPDVLuchador(pelea, objetivo, lanzador, afectados, -cura)
            }
            if (afectados.length > 0) {
                ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.id.toString() + "", afectados.toString())
            }
        } else {
            val temp = ArrayList<Luchador>()
            for (objetivo in objetivos!!) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                temp.add(objetivo)
            }
            if (!temp.isEmpty()) {
                pelea.addBuffLuchadores(
                    temp, efectoID, primerValor, duracion, hechizoID, convertirArgs(
                        primerValor,
                        efectoID, _args
                    ), lanzador, TipoDaño.POST_TURNOS, _condicionHechizo
                )
            }
        }
    }

    private fun efecto_Curar_Sin_Stats(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        if (duracion == 0) {
            val cura = getRandomValor(lanzador)
            val afectados = StringBuilder()
            for (objetivo in objetivos!!) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                val curaTemp = getMaxMinHechizo(objetivo, cura)
                restarPDVLuchador(pelea, objetivo, lanzador, afectados, -curaTemp)
            }
            if (afectados.length > 0) {
                ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.id.toString() + "", afectados.toString())
            }
        } else {
            val efectoID = getStatPorEfecto(efectoID)
            val temp = ArrayList<Luchador>()
            for (objetivo in objetivos!!) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                temp.add(objetivo)
            }
            if (!temp.isEmpty()) pelea.addBuffLuchadores(
                temp, efectoID, primerValor, duracion, hechizoID, convertirArgs(
                    primerValor,
                    efectoID, _args
                ), lanzador, TipoDaño.POST_TURNOS, _condicionHechizo
            )
        }
    }

    private fun efecto_Dona_Porc_Vida(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        val efectoID = getStatPorEfecto(efectoID)
        if (duracion == 0) {
            val afectados = StringBuilder()
            val porc = getRandomValor(lanzador)
            var daño = porc * lanzador.pdvConBuff / 100
            if (daño > lanzador.pdvConBuff) {
                daño = lanzador.pdvConBuff - 1
            }
            daño = restarPDVLuchador(pelea, lanzador, lanzador, afectados, daño)
            for (objetivo in objetivos!!) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                restarPDVLuchador(pelea, objetivo, lanzador, afectados, -daño)
            }
            if (afectados.length > 0) {
                ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.id.toString() + "", afectados.toString())
            }
        } else {
            val temp = ArrayList<Luchador>()
            for (objetivo in objetivos!!) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                temp.add(objetivo)
            }
            if (!temp.isEmpty()) pelea.addBuffLuchadores(
                temp, efectoID, primerValor, duracion, hechizoID, convertirArgs(
                    primerValor,
                    efectoID, _args
                ), lanzador, TipoDaño.POST_TURNOS, _condicionHechizo
            )
        }
    }

    // daños % de vida elemental --> tenia el ex CaC como true
    protected fun efecto_Daños_Porc_Elemental(
        objetivos: ArrayList<Luchador>?, pelea: Pelea,
        lanzador: Luchador, tipo: TipoDaño, esGC: Boolean
    ) {
        val efectoID = getStatPorEfecto(efectoID)
        if (duracion == 0) {
            quitarInvisibilidad(lanzador, tipo)
            val afectados = StringBuilder()
            for (a in objetivos!!) {
                var objetivo = a
                if (objetivo.estaMuerto()) {
                    continue
                }
                objetivo =
                    reenvioHechizo(pelea, _nivelHechizoID, hechizoID, objetivo, lanzador, tipo)
                objetivo = sacrificio(pelea, objetivo)
                var porc = getRandomValor(lanzador)
                porc = getMaxMinHechizo(objetivo, porc)
                var daño = porc * lanzador.pdvConBuff / 100
                daño = getDañoAumentadoPorHechizo(lanzador, hechizoID, daño)
                daño = calcularDañoFinal(
                    pelea, lanzador, objetivo, 10 + getElementoPorEfectoID(this.efectoID), daño.toFloat(),
                    hechizoID, tipo, esGC
                )
                daño = aplicarBuffContraGolpe(
                    efectoID,
                    daño,
                    objetivo,
                    lanzador,
                    pelea,
                    hechizoID,
                    tipo
                )
                daño = restarPDVLuchador(pelea, objetivo, lanzador, afectados, daño)
                curaSiLoGolpeas(objetivo, lanzador, afectados, daño)
            }
            if (afectados.length > 0) {
                ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.id.toString() + "", afectados.toString())
            }
        } else {
            val temp = ArrayList<Luchador>()
            for (objetivo in objetivos!!) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                temp.add(objetivo)
            }
            if (!temp.isEmpty()) pelea.addBuffLuchadores(
                temp, efectoID, primerValor, duracion, hechizoID, convertirArgs(
                    primerValor,
                    efectoID, _args
                ), lanzador, tipo, _condicionHechizo
            )
        }
    }

    // roba PDV elementales
    protected fun efecto_Roba_PDV_Elemental(
        objetivos: ArrayList<Luchador>?, pelea: Pelea,
        lanzador: Luchador, tipo: TipoDaño, esGC: Boolean
    ) {
        var modi = 0
        val perso = lanzador.personaje
        if (perso != null) {
            if (perso.tieneModfiSetClase(hechizoID)) {
                modi = perso.getModifSetClase(hechizoID, 283)
            }
        }
        if (duracion == 0) {
            quitarInvisibilidad(lanzador, tipo)
            val afectados = StringBuilder()
            for (a in objetivos!!) {
                var objetivo = a
                if (objetivo.estaMuerto()) {
                    continue
                }
                objetivo =
                    reenvioHechizo(pelea, _nivelHechizoID, hechizoID, objetivo, lanzador, tipo)
                objetivo = sacrificio(pelea, objetivo)
                var daño = getRandomValor(lanzador)
                daño = getMaxMinHechizo(objetivo, daño)
                daño = getDañoAumentadoPorHechizo(lanzador, hechizoID, daño)
                daño = calcularDañoFinal(
                    pelea, lanzador, objetivo, getElementoPorEfectoID(efectoID).toInt(), daño.toFloat(),
                    hechizoID, tipo, esGC
                )
                daño += modi
                dañoPorEspalda(pelea, lanzador, objetivo, daño)
                daño = aplicarBuffContraGolpe(
                    efectoID,
                    daño,
                    objetivo,
                    lanzador,
                    pelea,
                    hechizoID,
                    tipo
                )
                daño = restarPDVLuchador(pelea, objetivo, lanzador, afectados, daño)
                restarPDVLuchador(pelea, lanzador, lanzador, afectados, -daño / 2)
                curaSiLoGolpeas(objetivo, lanzador, afectados, daño)
            }
            if (afectados.length > 0) {
                ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.id.toString() + "", afectados.toString())
            }
        } else {
            val temp = ArrayList<Luchador>()
            for (objetivo in objetivos!!) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                temp.add(objetivo)
            }
            if (!temp.isEmpty()) pelea.addBuffLuchadores(
                temp, efectoID, primerValor, duracion, hechizoID, convertirArgs(
                    primerValor,
                    efectoID, _args
                ), lanzador, tipo, _condicionHechizo
            )
        }
    }

    // daños elementales por parte de los hechizos
    protected fun efecto_Daños_Elemental(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        tipo: TipoDaño, esGC: Boolean
    ) {
        val efectoID = getStatPorEfecto(efectoID)
        var modi = 0
        val perso = lanzador.personaje
        if (perso != null) {
            if (perso.tieneModfiSetClase(hechizoID)) {
                modi = perso.getModifSetClase(hechizoID, 283)
            }
        }
        if (duracion == 0) {
            quitarInvisibilidad(lanzador, tipo)
            val afectados = StringBuilder()
            for (a in objetivos!!) {
                var objetivo = a
                if (objetivo.estaMuerto()) {
                    continue
                }
                objetivo =
                    reenvioHechizo(pelea, _nivelHechizoID, hechizoID, objetivo, lanzador, tipo)
                objetivo = sacrificio(pelea, objetivo)
                var daño = getRandomValor(lanzador)
                daño = getMaxMinHechizo(objetivo, daño)
                daño = getDañoAumentadoPorHechizo(lanzador, hechizoID, daño)
                daño = calcularDañoFinal(
                    pelea, lanzador, objetivo, getElementoPorEfectoID(this.efectoID).toInt(), daño.toFloat(),
                    hechizoID, tipo, esGC
                )
                daño += modi
                dañoPorEspalda(pelea, lanzador, objetivo, daño)
                daño = aplicarBuffContraGolpe(
                    efectoID,
                    daño,
                    objetivo,
                    lanzador,
                    pelea,
                    hechizoID,
                    tipo
                )
                daño = restarPDVLuchador(pelea, objetivo, lanzador, afectados, daño)
                curaSiLoGolpeas(objetivo, lanzador, afectados, daño)
            }
            if (afectados.length > 0) {
                ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.id.toString() + "", afectados.toString())
            }
        } else {
            val temp = ArrayList<Luchador>()
            for (objetivo in objetivos!!) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                temp.add(objetivo)
            }
            if (!temp.isEmpty()) pelea.addBuffLuchadores(
                temp, efectoID, primerValor, duracion, hechizoID, convertirArgs(
                    primerValor,
                    efectoID, _args
                ), lanzador, tipo, _condicionHechizo
            )
        }
    }

    // daños para el lanzador (fixe) (FIJOS)--> no sacrificio, no reenvio
    private fun efecto_Daños_Para_Lanzador(
        pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda,
        tipo: TipoDaño, esGC: Boolean
    ) {
        val efectoID = getStatPorEfecto(efectoID)
        if (duracion == 0) {
            quitarInvisibilidad(lanzador, tipo)
            var daño = getRandomValor(lanzador)
            daño = calcularDañoFinal(
                pelea, lanzador, lanzador, getElementoPorEfectoID(this.efectoID).toInt(), daño.toFloat(),
                hechizoID, tipo, esGC
            )
            daño = aplicarBuffContraGolpe(
                efectoID,
                daño,
                lanzador,
                lanzador,
                pelea,
                hechizoID,
                tipo
            )
            daño = restarPDVLuchador(pelea, lanzador, lanzador, null, daño)
        } else {
            lanzador.addBuffConGIE(
                efectoID, primerValor, duracion, hechizoID, convertirArgs(
                    primerValor, efectoID,
                    _args
                ), lanzador, true, tipo, _condicionHechizo
            )
        }
    }

    private fun efecto_Daños_Porc_Vida_Neutral(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda, tipo: TipoDaño, esGC: Boolean
    ) {
        val `val` = getRandomValor(lanzador) / 100f
        val pdvMax = lanzador.pdvMaxConBuff
        val pdvMedio = pdvMax / 2
        var porc = 1f
        if (efectoID == 672) { // enemigos
            porc = 1 - Math.abs(lanzador.pdvConBuff - pdvMedio) / pdvMedio.toFloat()
        }
        val daño = (`val` * pdvMax * porc).toInt()
        quitarInvisibilidad(lanzador, tipo)
        val afectados = StringBuilder()
        for (a in objetivos!!) {
            var objetivo = a
            if (objetivo.estaMuerto()) {
                continue
            }
            objetivo =
                reenvioHechizo(pelea, _nivelHechizoID, hechizoID, objetivo, lanzador, tipo)
            objetivo = sacrificio(pelea, objetivo)
            var daño2 = calcularDañoFinal(
                pelea, lanzador, objetivo, (10 + Constantes.ELEMENTO_NEUTRAL).toInt(), daño.toFloat(),
                hechizoID, tipo, esGC
            )
            daño2 = aplicarBuffContraGolpe(
                efectoID,
                daño2,
                objetivo,
                lanzador,
                pelea,
                hechizoID,
                tipo
            )
            daño2 = restarPDVLuchador(pelea, objetivo, lanzador, afectados, daño2)
        }
        if (afectados.length > 0) {
            ENVIAR_GA_ACCION_PELEA(pelea, 7, 100, lanzador.id.toString() + "", afectados.toString())
        }
    }

    private fun efecto_Matar_Objetivo(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        quitarInvisibilidad(lanzador, TipoDaño.NORMAL)
        for (a in objetivos!!) {
            var objetivo = a
            if (objetivo.estaMuerto()) {
                continue
            }
            if (duracion == 0) {
                objetivo = sacrificio(pelea, objetivo)
            }
            pelea.addMuertosReturnFinalizo(objetivo, lanzador)
//            try {
//                Thread.sleep(500)
//            } catch (ignored: Exception) {
//            }
        }
    }

    private fun efecto_Dominio_Arma(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        val efectoID = getStatPorEfecto(efectoID)
        val temp = ArrayList<Luchador>()
        for (objetivo in objetivos!!) {
            if (objetivo.estaMuerto()) {
                continue
            }
            temp.add(objetivo)
        }
        if (!temp.isEmpty()) {
            pelea.addBuffLuchadores(
                temp, efectoID, primerValor, duracion, hechizoID, convertirArgs(
                    primerValor, efectoID,
                    _args
                ), lanzador, TipoDaño.POST_TURNOS, _condicionHechizo
            )
        }
    }

    private fun efecto_Bonus_Malus(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda, tipo: TipoDaño
    ) { // solo tiene los mas pa y mas pm, no esta los menos
        if (hechizoID == 2210) { // furia de zobal
            if (celdaObjetivo.primerLuchador == null) {
                return
            }
        }
        var valor = getRandomValor(lanzador)
        val efectoID = getStatPorEfecto(efectoID)
        val valor2 = valor
        val temp = ArrayList<Luchador>()
        val afectados = StringBuilder()
        for (objetivo in objetivos!!) {
            if (objetivo.estaMuerto()) {
                continue
            }
            valor = getMaxMinHechizo(objetivo, valor2)
            if (valor != valor2) {
                objetivo.addBuffConGIE(
                    efectoID,
                    valor,
                    duracion,
                    hechizoID,
                    convertirArgs(valor, efectoID, _args),
                    lanzador,
                    true,
                    tipo,
                    _condicionHechizo
                )
            } else {
                temp.add(objetivo)
            }
            when (this.efectoID) {
                78, 111, 128, 120, 110, 112, 114, 115, 116, 117, 118, 119, 121, 122, 123, 124, 125, 126, 138, 142, 144, 145, 152, 153, 154, 155, 156, 157, 160, 161, 162, 163, 171, 176, 177, 178, 179, 182, 183, 184, 186, 425, 606, 607, 608, 609, 610, 611, 776 -> {
                    if (afectados.length > 0) {
                        afectados.append("¬")
                    }
                    afectados.append(objetivo.id).append(",").append(valor).append(",").append(duracionFinal(objetivo))
                }
            }
        }
        if (afectados.length > 0 && _condicionHechizo.isEmpty()) {
            ENVIAR_GA_ACCION_PELEA(pelea, 7, this.efectoID, lanzador.id.toString() + "", afectados.toString())
        }
        if (!temp.isEmpty()) {
            pelea.addBuffLuchadores(
                temp,
                efectoID,
                valor,
                duracion,
                hechizoID,
                convertirArgs(valor, efectoID, _args),
                lanzador,
                tipo,
                _condicionHechizo
            )
        }
    }

    private fun efecto_Reenvio_Hechizo(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        val nivelMax = segundoValor
        val efectoID = getStatPorEfecto(efectoID)
        if (nivelMax == -1) {
            return
        }
        val temp = ArrayList<Luchador>()
        for (objetivo in objetivos!!) {
            if (objetivo.estaMuerto()) {
                continue
            } else if (objetivo.mob != null) {
                if (objetivo.mob!!.mobModelo.id == 423) { // Al krala no se le puede reenviar hechizos
                    continue
                }
            }
            temp.add(objetivo)
        }
        if (!temp.isEmpty()) pelea.addBuffLuchadores(
            temp,
            efectoID,
            nivelMax,
            duracion,
            hechizoID,
            convertirArgs(nivelMax, efectoID, _args),
            lanzador,
            TipoDaño.POST_TURNOS,
            _condicionHechizo
        )
        try {
            Thread.sleep(200)
        } catch (ignored: Exception) {
        }
    }

    private fun efecto_Deshechizar(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) { // deshechiza
        val afectados = StringBuilder()
        for (objetivo in objetivos!!) {
            if (objetivo.estaMuerto()) {
                continue
            }
            if (afectados.length > 0) {
                afectados.append("¬")
            }
            afectados.append(objetivo.id)
        }
        if (afectados.length > 0 && _condicionHechizo.isEmpty()) {
            ENVIAR_GA_ACCION_PELEA(pelea, 7, efectoID, lanzador.id.toString() + "", afectados.toString())
            for (objetivo in objetivos) {
                if (objetivo.estaMuerto()) {
                    continue
                }
                objetivo.deshechizar(lanzador, true)
            }
        }
    }

    // cambia la apariencia
    private fun efecto_Cambiar_Apariencia(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        var gfxID = tercerValor
        val efectoID = getStatPorEfecto(efectoID)
        val temp = ArrayList<Luchador>()
        quitarInvisibilidad(lanzador, TipoDaño.NORMAL)
        for (objetivo in objetivos!!) {
            if (objetivo.estaMuerto()) {
                continue
            }
            temp.add(objetivo)
            if (gfxID == 8010 && objetivo.personaje != null && objetivo.personaje!!
                    .sexo == Constantes.SEXO_FEMENINO
            ) {
                gfxID = 8011
            }
            if (gfxID == -1 || duracion == 0) {
                ENVIAR_GA_ACCION_PELEA(
                    pelea, 7, this.efectoID, lanzador.id.toString() + "", objetivo.id.toString() + ","
                            + objetivo.gfxID + "," + objetivo.gfxID
                )
            } else {
                ENVIAR_GA_ACCION_PELEA(
                    pelea, 7, this.efectoID, lanzador.id.toString() + "", objetivo.id.toString() + ","
                            + objetivo.gfxID + "," + gfxID + "," + duracionFinal(objetivo)
                )
            }
        }
        if (gfxID > -1 && duracion != 0) {
            if (!temp.isEmpty()) pelea.addBuffLuchadores(
                temp,
                efectoID,
                gfxID,
                duracion,
                hechizoID,
                convertirArgs(gfxID, efectoID, _args),
                lanzador,
                TipoDaño.POST_TURNOS,
                _condicionHechizo
            )
        }
    }

    // vuelve invisible a un pj
    private fun efecto_Invisibilidad(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        val efectoID = getStatPorEfecto(efectoID)
        val temp = ArrayList<Luchador>()
        val afectados = StringBuilder()
        for (objetivo in objetivos!!) {
            if (objetivo.estaMuerto()) {
                continue
            }
            temp.add(objetivo)
            objetivo.vaciarVisibles()
            if (afectados.length > 0) afectados.append("¬")
            afectados.append(objetivo.id).append(",").append(duracionFinal(objetivo))
        }
        if (afectados.length > 0 && _condicionHechizo.isEmpty()) {
            ENVIAR_GA_ACCION_PELEA(pelea, 7, this.efectoID, lanzador.id.toString() + "", afectados.toString())
        }
        if (!temp.isEmpty()) pelea.addBuffLuchadores(
            temp, efectoID, primerValor, duracion, hechizoID, convertirArgs(
                primerValor, efectoID,
                _args
            ), lanzador, TipoDaño.POST_TURNOS, _condicionHechizo
        )
    }

    // invocar doble
    private fun efecto_Invoca_Doble(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        if (lanzador.nroInvocaciones >= lanzador.totalStats.getTotalStatParaMostrar(182)) {
            ENVIAR_Im_INFORMACION(
                lanzador.personaje, "0CANT_SUMMON_MORE_CREATURE;" + lanzador
                    .nroInvocaciones
            )
            return
        }
        if (!celdaObjetivo.esCaminable(true)) {
            ENVIAR_Im_INFORMACION_A_PELEA(pelea, 7, "1CELDA_NO_CAMINABLE")
            ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.id, celdaObjetivo.id)
            return
        }
        if (celdaObjetivo.primerLuchador != null) {
            ENVIAR_GA_ACCION_PELEA(pelea, 7, 151, lanzador.id.toString() + "", hechizoID.toString() + "")
            ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.id, celdaObjetivo.id)
            return
        }
        val doble = lanzador.clonarLuchador(pelea.sigIDLuchador)
        doble!!.equipoBin = lanzador.equipoBin
        doble.invocador = lanzador
        doble.setPDVMAX(lanzador.pdvMaxSinBuff, false)
        doble.setPDV(lanzador.pdvMaxSinBuff)
        doble.celdaPelea = celdaObjetivo
        pelea.ordenLuchadores.add(pelea.ordenLuchadores.indexOf(lanzador) + 1, doble)
        pelea.addLuchadorEnEquipo(doble, lanzador.equipoBin.toInt())
        ENVIAR_GA_ACCION_PELEA(pelea, 7, 180, lanzador.id.toString() + "", "+" + doble.stringGM(0))
        ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, lanzador.id.toString() + "", pelea.stringOrdenJugadores())
        lanzador.addNroInvocaciones(1)
        try {
            Thread.sleep(1000)
        } catch (ignored: Exception) {
        }
        verificaTrampas(doble)
        // pelea.actualizarNumTurnos(null);
    }

    // invocar una criatura
    private fun efecto_Invoca_Mob(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        if (lanzador.nroInvocaciones >= lanzador.totalStats.getTotalStatParaMostrar(
                Constantes.STAT_MAS_CRIATURAS_INVO
            )
        ) {
            ENVIAR_Im_INFORMACION(
                lanzador.personaje, "0CANT_SUMMON_MORE_CREATURE;" + lanzador
                    .nroInvocaciones
            )
            return
        }
        if (efectoID == 405) { // mata para invocar
            if (celdaObjetivo.primerLuchador != null) {
                pelea.addMuertosReturnFinalizo(celdaObjetivo.primerLuchador!!, lanzador)
                try {
                    Thread.sleep(1000)
                } catch (ignored: Exception) {
                }
            }
        }
        if (!celdaObjetivo.esCaminable(true)) {
            ENVIAR_Im_INFORMACION_A_PELEA(pelea, 7, "1CELDA_NO_CAMINABLE")
            ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.id, celdaObjetivo.id)
            return
        }
        if (celdaObjetivo.primerLuchador != null) {
            ENVIAR_GA_ACCION_PELEA(pelea, 7, 151, lanzador.id.toString() + "", hechizoID.toString() + "")
            ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.id, celdaObjetivo.id)
            return
        }
        var mobID = 0
        var mobNivel: Byte = 0
        try {
            mobID = _args.split(",".toRegex()).toTypedArray()[0].toInt()
            mobNivel = _args.split(",".toRegex()).toTypedArray()[1].toByte()
        } catch (ignored: Exception) {
        }
        var mob: MobGrado? = null
        val idInvocacion = pelea.sigIDLuchador
        mob = try {
            getMobModelo(mobID)!!.getGradoPorGrado(mobNivel)!!.invocarMob(idInvocacion, false, lanzador)
        } catch (e: Exception) {
            redactarLogServidorln("El Mob ID esta reparandose: $mobID")
            return
        }
        val invocacion = Luchador(pelea, mob, false)
        invocacion.equipoBin = lanzador.equipoBin
        invocacion.invocador = lanzador
        invocacion.celdaPelea = celdaObjetivo
        pelea.ordenLuchadores.add(pelea.ordenLuchadores.indexOf(lanzador) + 1, invocacion)
        pelea.addLuchadorEnEquipo(invocacion, lanzador.equipoBin.toInt())
        //
        ENVIAR_GA_ACCION_PELEA(pelea, 7, 181, lanzador.id.toString() + "", "+" + invocacion.stringGM(0))
        ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, lanzador.id.toString() + "", pelea.stringOrdenJugadores())
        lanzador.addNroInvocaciones(1)
        if (AtlantaMain.PARAM_MOSTRAR_STATS_INVOCACION) {
            val str = StringBuilder()
            str.append("<b>STATS INVOCATION [</b>")
            str.append("<b>STR:</b> ").append(invocacion.totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_FUERZA))
                .append(", ")
            str.append("<b>INT:</b> ")
                .append(invocacion.totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_INTELIGENCIA)).append(", ")
            str.append("<b>CHA:</b> ").append(invocacion.totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_SUERTE))
                .append(", ")
            str.append("<b>AGI:</b> ")
                .append(invocacion.totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_AGILIDAD)).append("<b>]</b>")
            ENVIAR_cs_CHAT_MENSAJE_A_PELEA(pelea, str.toString(), Constantes.COLOR_NARANJA)
        }
        try {
            Thread.sleep(1000)
        } catch (ignored: Exception) {
        }
        when (mobID) {
            556, 282, 2750 -> {
                invocacion.setEstatico(true)
                invocacion.sirveParaBuff = false
            }
        }
        verificaTrampas(invocacion)
        // pelea.actualizarNumTurnos(null);
    }

    // invoca una criatura estatica
    private fun efecto_Invoca_Estatico(
        pelea: Pelea,
        lanzador: Luchador,
        celdaObjetivo: Celda
    ) { // if (lanzador.getNroInvocaciones() >= lanzador.getTotalStats().getStatParaMostrar(
// CentroInfo.STAT_MAS_CRIATURAS_INVO)) {
// GestorSalida.ENVIAR_Im_INFORMACION(lanzador.getPersonaje(), "0CANT_SUMMON_MORE_CREATURE;"
// + lanzador.getNroInvocaciones());
// return;
// }
        if (!celdaObjetivo.esCaminable(true)) {
            ENVIAR_Im_INFORMACION_A_PELEA(pelea, 7, "1CELDA_NO_CAMINABLE")
            ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.id, celdaObjetivo.id)
            return
        }
        if (celdaObjetivo.primerLuchador != null) {
            ENVIAR_GA_ACCION_PELEA(pelea, 7, 151, lanzador.id.toString() + "", hechizoID.toString() + "")
            ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.id, celdaObjetivo.id)
            return
        }
        var mobID = 0
        var mobnivel: Byte = 0
        try {
            mobID = _args.split(",".toRegex()).toTypedArray()[0].toInt()
            mobnivel = _args.split(",".toRegex()).toTypedArray()[1].toByte()
        } catch (ignored: Exception) {
        }
        var mob: MobGrado? = null
        val idInvocacion = pelea.sigIDLuchador
        mob = try {
            getMobModelo(mobID)!!.getGradoPorGrado(mobnivel)!!.invocarMob(idInvocacion, false, lanzador)
        } catch (e: Exception) {
            redactarLogServidorln("El Mob ID esta mal configurado: $mobID")
            return
        }
        val invocacion = Luchador(pelea, mob, false)
        val equipoLanz = lanzador.equipoBin
        invocacion.equipoBin = equipoLanz
        invocacion.invocador = lanzador
        invocacion.setEstatico(true)
        invocacion.sirveParaBuff = false
        invocacion.celdaPelea = celdaObjetivo
        pelea.addLuchadorEnEquipo(invocacion, equipoLanz.toInt())
        ENVIAR_GA_ACCION_PELEA(pelea, 7, 185, lanzador.id.toString() + "", "+" + invocacion.stringGM(0))
        // lanzador.aumentarInvocaciones(); supuestamente no cuenta para estas
        try {
            Thread.sleep(1000)
        } catch (ignored: Exception) {
        }
        // invocacion.setEstado(Constantes.ESTADO_ARRAIGADO, -1);
// invocacion.setEstado(Constantes.ESTADO_PESADO, -1);
        verificaTrampas(invocacion)
    }

    // invoca a un aliado muerto en combate, revivir
    private fun efecto_Resucitar(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        var objetivo: Luchador? = null
        for (i in pelea.listaMuertos.indices.reversed()) {
            val muerto = pelea.listaMuertos[i]
            if (muerto.estaRetirado()) {
                continue
            }
            if (muerto.equipoBin == lanzador.equipoBin) {
                if (muerto.esInvocacion()) {
                    if (muerto.invocador!!.estaMuerto()) {
                        continue
                    }
                }
                objetivo = muerto
                break
            }
        }
        if (objetivo == null) {
            return
        }
        objetivo.setEstaMuerto(false)
        objetivo.celdaPelea = celdaObjetivo
        objetivo.buffsPelea.clear()
        pelea.listaMuertos.remove(objetivo)
        val vida = primerValor * objetivo.pdvMaxConBuff / 100
        val iniciador = pelea.esLuchInicioPelea(objetivo)
        if (!iniciador) {
            pelea.ordenLuchadores.add(pelea.ordenLuchadores.indexOf(lanzador) + 1, objetivo)
        } else if (objetivo.personaje != null) {
            ENVIAR_ILF_CANTIDAD_DE_VIDA(objetivo.personaje!!, vida)
        }
        objetivo.setPDV(vida)
        objetivo.invocador = lanzador
        pelea.addLuchadorEnEquipo(objetivo, lanzador.equipoBin.toInt())
        ENVIAR_GA_ACCION_PELEA(
            pelea, 7, if (iniciador) 147 else 780, lanzador.id.toString() + "", "+" + objetivo.stringGM(
                0
            )
        )
        ENVIAR_GA_ACCION_PELEA(pelea, 7, 999, lanzador.id.toString() + "", pelea.stringOrdenJugadores())
        if (objetivo.personaje != null) {
            ENVIAR_As_STATS_DEL_PJ(objetivo.personaje!!)
        }
        if (!iniciador) {
            lanzador.addNroInvocaciones(1)
        }
        try {
            Thread.sleep(1000)
        } catch (ignored: Exception) {
        }
        verificaTrampas(objetivo)
        // pelea.actualizarNumTurnos(null);
    }

    private fun efecto_Revela_Invisibles(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        val aliados = pelea.luchadoresDeEquipo(lanzador.paramEquipoAliado.toInt())
        val celdasObj = zonaEfecto?.let {
            Camino.celdasAfectadasEnElArea(
                pelea.mapaCopia!!, celdaObjetivo.id,
                lanzador.celdaPelea!!.id, it
            )
        }
        for (mostrar in pelea.luchadoresDeEquipo(3)) {
            if (mostrar.estaMuerto() || !mostrar.esInvisible(0)) {
                continue
            }
            if (celdasObj != null) {
                if (!celdasObj.contains(mostrar.celdaPelea)) {
                    continue
                }
            }
            for (aliado in aliados) {
                if (mostrar.esInvisible(aliado.id)) {
                    mostrar.aparecer(aliado)
                }
            }
        }
        if (pelea.trampas != null) {
            for (trampa in pelea.trampas!!) {
                if (celdasObj != null) {
                    if (!celdasObj.contains(trampa.celda)) {
                        continue
                    }
                }
                trampa.aparecer(objetivos!!)
            }
        }
        //
    }

    // aumenta los daños del hechizo X
    private fun efecto_Aumenta_Daños_Hechizo(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        val efectoID = getStatPorEfecto(efectoID)
        lanzador.addBuffConGIE(
            efectoID,
            primerValor,
            duracion,
            hechizoID,
            convertirArgs(primerValor, efectoID, _args),
            lanzador,
            true,
            TipoDaño.POST_TURNOS,
            _condicionHechizo
        )
    }

    // pone una trampa de nivel X
    private fun efecto_Poner_Trampa(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        if (!celdaObjetivo.esCaminable(true)) {
            ENVIAR_Im_INFORMACION_A_PELEA(pelea, 7, "1CELDA_NO_CAMINABLE")
            ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.id, celdaObjetivo.id)
            return
        }
        if (celdaObjetivo.primerLuchador != null) {
            ENVIAR_GA_ACCION_PELEA(pelea, 7, 151, lanzador.id.toString() + "", hechizoID.toString() + "")
            ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.id, celdaObjetivo.id)
            return
        }
        if (celdaObjetivo.esTrampa()) {
            ENVIAR_Im_INFORMACION(lanzador.personaje, "1229")
            ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.id, celdaObjetivo.id)
            return
        }
        val SH = getHechizo(primerValor)!!.getStatsPorNivel(segundoValor) ?: return
        val tamaño = getNumeroPorValorHash(zonaEfecto!![1])
        val celdas = Camino.celdasAfectadasEnElArea(
            pelea.mapaCopia!!, celdaObjetivo.id, lanzador
                .celdaPelea!!.id, zonaEfecto!!
        )
        Trampa(
            pelea, lanzador, celdaObjetivo, tamaño, SH, hechizoID, pelea.luchadoresDeEquipo(
                lanzador
                    .paramEquipoAliado.toInt()
            ), celdas, tercerValor
        )
    }

    // pone un glifo nivel X
    private fun efecto_Glifo_Libre(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        if (!celdaObjetivo.esCaminable(true)) {
            ENVIAR_Im_INFORMACION_A_PELEA(pelea, 7, "1CELDA_NO_CAMINABLE")
            ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.id, celdaObjetivo.id)
            return
        }
        val SH = getHechizo(primerValor)!!.getStatsPorNivel(segundoValor) ?: return
        val tamaño = getNumeroPorValorHash(zonaEfecto!![1])
        val tipo = zonaEfecto!![0]
        val celdas = Camino.celdasAfectadasEnElArea(
            pelea.mapaCopia!!, celdaObjetivo.id, lanzador
                .celdaPelea!!.id, zonaEfecto!!
        )
        Glifo(pelea, lanzador, celdaObjetivo, tamaño, SH, duracion, hechizoID, true, celdas, tercerValor, tipo)
    }

    // pone un glifo nivel X asi halla un jugador, efecto al final de turno
    private fun efecto_Glifo_Fin_Turno(pelea: Pelea, lanzador: Luchador, celdaObjetivo: Celda) {
        if (!celdaObjetivo.esCaminable(true)) {
            ENVIAR_Im_INFORMACION_A_PELEA(pelea, 7, "1CELDA_NO_CAMINABLE")
            ENVIAR_Gf_MOSTRAR_CELDA_EN_PELEA(pelea, 7, lanzador.id, celdaObjetivo.id)
            return
        }
        val SH = getHechizo(primerValor)!!.getStatsPorNivel(segundoValor) ?: return
        val tamaño = getNumeroPorValorHash(zonaEfecto!![1])
        val celdas = Camino.celdasAfectadasEnElArea(
            pelea.mapaCopia!!, celdaObjetivo.id, lanzador
                .celdaPelea!!.id, zonaEfecto!!
        )
        Glifo(
            pelea, lanzador, celdaObjetivo, tamaño, SH, duracion, hechizoID, false, celdas, tercerValor,
            zonaEfecto!![0]
        )
    }

    // hechizo miedo
    private fun efecto_Retrocede_Hasta_Cierta_Casilla(
        pelea: Pelea,
        lanzador: Luchador,
        celdaObjetivo: Celda
    ) {
        val celdaLanzamiento = lanzador.celdaPelea
        val mapaCopia = pelea.mapaCopia
        val dir = Camino.direccionEntreDosCeldas(mapaCopia, celdaLanzamiento!!.id, celdaObjetivo.id, true)
        val sigCeldaID = Camino.getSigIDCeldaMismaDir(celdaLanzamiento.id, dir, mapaCopia, true)
        val sigCelda = mapaCopia!!.getCelda(sigCeldaID)
        if (sigCelda == null || sigCelda.primerLuchador == null) {
            return
        }
        val objetivo = sigCelda.primerLuchador
        if (objetivo!!.estaMuerto() || objetivo.tieneEstado(Constantes.ESTADO_ARRAIGADO.toInt())) {
            return
        }
        val distancia = Camino.distanciaDosCeldas(mapaCopia, sigCeldaID, celdaObjetivo.id).toInt()
        efectoEmpujones(pelea, lanzador, objetivo, celdaLanzamiento, sigCelda, distancia, false)
    }

    private fun efecto_Teleport_Inicio(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) { // teletransporta
        if (lanzador.tieneEstado(Constantes.ESTADO_PESADO.toInt())) {
            return
        }
        for (objetivo in objetivos!!) {
            if (objetivo.estaMuerto() || objetivo.esInvocacion()) {
                continue
            }
            var celda1: Celda? = null
            for ((key, value) in pelea.posInicial) {
                if (key == objetivo.id) {
                    celda1 = value
                    break
                }
            }
            if (Objects.requireNonNull(celda1)!!.esCaminable(true) && celda1!!.primerLuchador == null) {
                objetivo.celdaPelea!!.moverLuchadoresACelda(celda1)
                ENVIAR_GA_ACCION_PELEA(
                    pelea, 7, 4, lanzador.id.toString() + "", objetivo.id.toString() + "," + celda1
                        .id
                )
                verificaTrampas(objetivo)
            }
        }
    }

    private fun efecto_Estados(
        objetivos: ArrayList<Luchador>?, pelea: Pelea, lanzador: Luchador,
        celdaObjetivo: Celda
    ) { // estatdo X
        val estadoID = tercerValor
        if (estadoID == -1) {
            return
        }
        val temp = ArrayList<Luchador>()
        // StringBuilder afectados = new StringBuilder();
        for (objetivo in objetivos!!) {
            if (objetivo.estaMuerto()) {
                continue
            }
            temp.add(objetivo)
            if (efectoID == Constantes.STAT_QUITAR_ESTADO && !objetivo.tieneEstado(estadoID)) {
                continue
            }
            // if (afectados.length() > 0) {
// afectados.append("¬");
// }
// afectados.append(objetivo.getID() + "," + idEstado + "," + (_efectoID ==
// Constantes.STAT_DAR_ESTADO ? 1 : 0));
        }
        if (!temp.isEmpty()) {
            pelea.addBuffLuchadores(
                temp, efectoID, estadoID, duracion, hechizoID, convertirArgs(
                    estadoID, efectoID,
                    _args
                ), lanzador, TipoDaño.POST_TURNOS, _condicionHechizo
            )
        }
    }

    enum class TipoDaño {
        NORMAL, POST_TURNOS, GLIFO, TRAMPA, CAC, NULL
    }

    companion object {
        const val TIEMPO_POR_LUCHADOR_MUERTO = 500

        // staticos
        var TIEMPO_ENTRE_EFECTOS = 50
        var TIEMPO_GAME_ACTION = 5
        var TIEMPO_POR_LANZAR_HECHIZO = 1000
        var MULTIPLICADOR_DAÑO_PJ = 1f
        var MULTIPLICADOR_DAÑO_MOB = 1f
        var MULTIPLICADOR_DAÑO_CAC = 1f
        fun getStatPorEfecto(efecto: Int): Int {
            when (efecto) {
                Constantes.STAT_ROBA_PM, 169 -> return Constantes.STAT_MENOS_PM
                Constantes.STAT_ROBA_PA, 168 -> return Constantes.STAT_MENOS_PA
                266 -> return Constantes.STAT_MENOS_SUERTE
                267 -> return Constantes.STAT_MENOS_VITALIDAD
                268 -> return Constantes.STAT_MENOS_AGILIDAD
                269 -> return Constantes.STAT_MENOS_INTELIGENCIA
                270 -> return Constantes.STAT_MENOS_SABIDURIA
                271 -> return Constantes.STAT_MENOS_FUERZA
                Constantes.STAT_ROBA_ALCANCE -> return Constantes.STAT_MENOS_ALCANCE
                606 -> return Constantes.STAT_MAS_SABIDURIA
                607 -> return Constantes.STAT_MAS_FUERZA
                608 -> return Constantes.STAT_MAS_SUERTE
                609 -> return Constantes.STAT_MAS_AGILIDAD
                610 -> return Constantes.STAT_MAS_VITALIDAD
                611 -> return Constantes.STAT_MAS_INTELIGENCIA
            }
            return efecto
        }

        private fun getStatContrario(efecto: Int): Int {
            when (efecto) {
                Constantes.STAT_MAS_PM -> return Constantes.STAT_MENOS_PM
                Constantes.STAT_MAS_PA -> return Constantes.STAT_MENOS_PA
                Constantes.STAT_MAS_SUERTE -> return Constantes.STAT_MENOS_SUERTE
                Constantes.STAT_MAS_VITALIDAD -> return Constantes.STAT_MENOS_VITALIDAD
                Constantes.STAT_MAS_AGILIDAD -> return Constantes.STAT_MENOS_AGILIDAD
                Constantes.STAT_MAS_INTELIGENCIA -> return Constantes.STAT_MENOS_INTELIGENCIA
                Constantes.STAT_MAS_SABIDURIA -> return Constantes.STAT_MENOS_SABIDURIA
                Constantes.STAT_MAS_FUERZA -> return Constantes.STAT_MENOS_FUERZA
                Constantes.STAT_MAS_ALCANCE -> return Constantes.STAT_MENOS_ALCANCE
                Constantes.STAT_MENOS_PM -> return Constantes.STAT_MAS_PM
                Constantes.STAT_MENOS_PA -> return Constantes.STAT_MAS_PA
                Constantes.STAT_MENOS_SUERTE -> return Constantes.STAT_MAS_SUERTE
                Constantes.STAT_MENOS_VITALIDAD -> return Constantes.STAT_MAS_VITALIDAD
                Constantes.STAT_MENOS_AGILIDAD -> return Constantes.STAT_MAS_AGILIDAD
                Constantes.STAT_MENOS_INTELIGENCIA -> return Constantes.STAT_MAS_INTELIGENCIA
                Constantes.STAT_MENOS_SABIDURIA -> return Constantes.STAT_MAS_SABIDURIA
                Constantes.STAT_MENOS_FUERZA -> return Constantes.STAT_MAS_FUERZA
                Constantes.STAT_MENOS_ALCANCE -> return Constantes.STAT_MAS_ALCANCE
            }
            return efecto
        }

        fun convertirArgs(valor: Int, efectoID: Int, args: String): String {
            var valor = valor
            if (efectoID == 788) { // castigo
                return args
            }
            val splits = args.split(",".toRegex()).toTypedArray()
            var valMax = "-1"
            when (efectoID) {
                81, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 107, 108, 220, 265 -> {
                    valor = splits[0].toInt()
                    if (splits[1] != valor.toString() + "") {
                        valMax = splits[1]
                    }
                }
                9, 79, 106, 131, 165, 181, 293, 301, 302, 303, 304, 305, 787 ->  // Castigo X durante Y turnos
                    valMax = splits[1]
            }
            return valor.toString() + "," + valMax + "," + splits[2] + "," + splits[3] + "," + splits[4]
        }

        // aumentados por efecto 293
        fun getDañoAumentadoPorHechizo(lanzador: Luchador, hechizo: Int, daño: Int): Int {
            var daño = daño
            if (hechizo != 0) {
                for (buff in lanzador.getBuffsPorEfectoID(293)) {
                    if (buff.primerValor == hechizo) {
                        val add = buff.tercerValor
                        if (add <= 0) {
                            continue
                        }
                        daño += add
                    }
                }
            }
            return daño
        }

        // param official
        fun getPorcHuida2(
            agiMov: Int,
            agiTac: Int
        ): Int { // int porcTac = (int) ((100 * (Math.pow(agiTac + 25, 2))) / ((Math.pow(agiTac + 25, 2)) +
// (Math.pow(agiMov + 25, 2))));
            var porcTac = 300 * (agiMov + 25) / (agiMov + agiTac + 50) - 100
            porcTac = Math.min(100, porcTac)
            porcTac = Math.max(0, porcTac)
            return porcTac
        }

        fun getPorcHuida(movedor: Luchador, tacleador: Luchador): Int {
            val statsTac = tacleador.totalStats
            val placajeTac = statsTac.getTotalStatConComplemento(Constantes.STAT_MAS_PLACAJE)
            if (2 * (placajeTac + 2) <= 0) {
                return 100
            }
            val statsMov = movedor.totalStats
            var huidaMov = statsMov.getTotalStatConComplemento(Constantes.STAT_MAS_HUIDA)
            if (huidaMov < -2) {
                huidaMov = -2
            }
            var porc = (huidaMov + 2) * 100 / (2 * (placajeTac + 2))
            if (porc < 0) {
                porc = 0
            } else if (porc > 100) {
                porc = 100
            }
            return porc
        }

        private fun reenvioDaño(objetivo: Luchador): Int {
            val totalStats = objetivo.totalStats
            val sabiduria = totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_SABIDURIA)
            val rEquipo = totalStats.getTotalStatParaMostrar(Constantes.STAT_REENVIA_DAÑOS)
            var rHechizos = 0
            for (buff in objetivo.getBuffsPorEfectoID(Constantes.STAT_DAÑOS_DEVUELTOS)) {
                rHechizos += buff.getRandomValor(buff.lanzador)
            }
            for (buff in objetivo.getBuffsPorEfectoID(Constantes.STAT_REENVIA_DAÑOS)) {
                rHechizos += buff.getRandomValor(buff.lanzador)
            }
            return (rHechizos * (1 + sabiduria / AtlantaMain.SABIDURIA_PARA_REENVIO.toFloat()) + rEquipo).toInt()
        }

        fun aplicarBuffContraGolpe(
            efectoID: Int, daño: Int, objetivo: Luchador, lanzador: Luchador,
            pelea: Pelea, hechizoID: Int, tipo: TipoDaño
        ): Int {
            var daño = daño
            if ((tipo == TipoDaño.CAC || tipo == TipoDaño.NORMAL || tipo == TipoDaño.GLIFO || tipo == TipoDaño.TRAMPA)
                && lanzador.id != objetivo.id
            ) {
                var reenvio = reenvioDaño(objetivo)
                if (reenvio > daño) {
                    reenvio = daño
                }
                // el reenvio no disminuye los daños
// dañoFinal -= reenvio;
                if (reenvio > 0) {
                    pelea.setUltimoTipoDaño(TipoDaño.NORMAL)
                    ENVIAR_GA_ACCION_PELEA(pelea, 7, 107, "-1", objetivo.id.toString() + "," + reenvio)
                    restarPDVLuchador(pelea, lanzador, objetivo, null, reenvio)
                }
            }
            if (objetivo.estaMuerto()) {
                return 0
            }
            // for (final int id : Constantes.BUFF_ACCION_RESPUESTA) {
            loop@ for (buff in objetivo.buffsPelea) {
                if (objetivo.estaMuerto()) {
                    return 0
                }
                if (!buff.condicionBuff.isEmpty()) {
                    continue
                }
                when (buff.efectoID) {
                    9 -> {
                        if (tipo == TipoDaño.TRAMPA || tipo == TipoDaño.GLIFO) {
                            continue@loop
                        }
                        if (lanzador.celdaPelea
                                ?.id?.let {
                                    Camino.distanciaDosCeldas(
                                        pelea.mapaCopia, objetivo.celdaPelea!!.id, it
                                    )
                                }!! > 1
                        ) {
                            continue@loop
                        }
                        val elusion = buff.primerValor
                        val azar = getRandomInt(1, 100)
                        if (azar > elusion) {
                            continue@loop
                        }
                        var nroCasillas = 0
                        try {
                            nroCasillas = buff.args.split(",".toRegex()).toTypedArray()[1].toInt()
                        } catch (ignored: Exception) {
                        }
                        efectoEmpujones(
                            pelea, lanzador, objetivo, lanzador.celdaPelea, objetivo.celdaPelea, nroCasillas,
                            true
                        )
                        daño = 0
                    }
                    79 -> {
                        if (tipo == TipoDaño.TRAMPA || tipo == TipoDaño.GLIFO) {
                            continue@loop
                        }
                        try {
                            val infos = buff.args.split(",".toRegex()).toTypedArray()
                            val coefDaño = infos[0].toInt()
                            val coefCura = infos[1].toInt()
                            val suerte = infos[2].toInt()
                            if (getRandomInt(1, 100) <= suerte) { // Cura
                                daño *= coefCura
                                daño = Math.min(daño, objetivo.pdvMaxConBuff - objetivo.pdvConBuff)
                                daño = -daño
                            } else {
                                daño *= coefDaño
                            }
                        } catch (ignored: Exception) {
                        }
                    }
                    304 -> for (o in pelea.luchadoresDeEquipo(3)) {
                        if (o.estaMuerto()) {
                            continue
                        }
                        if (o.tieneEstado(Constantes.ESTADO_TRANSPORTADO.toInt())) {
                            continue
                        }
                        val b = o.getBuff(766)
                        if (b != null && b.lanzador.id == objetivo.id) {
                            b.aplicarHechizoDeBuff(pelea, o, o.celdaPelea)
                        }
                    }
                    305 -> if (tipo == TipoDaño.CAC) {
                        buff.aplicarHechizoDeBuff(pelea, objetivo, objetivo.celdaPelea)
                    }
                    776 -> {
                        if (tipo == TipoDaño.TRAMPA || tipo == TipoDaño.GLIFO) {
                            continue@loop
                        }
                        if (objetivo.tieneBuff(776)) { // si posee daños incurables
                            var pdvMax = objetivo.pdvMaxSinBuff
                            val pdaño = objetivo.getValorPorBuffsID(776) / 100f
                            pdvMax -= (daño * pdaño).toInt()
                            if (pdvMax < 0) {
                                pdvMax = 0
                            }
                            objetivo.setPDVMAX(pdvMax, false)
                        }
                    }
                    788 -> {
                        when (efectoID) {
                            85, 86, 87, 88, 89 -> return daño
                        }
                        val porc = if (lanzador.personaje == null) 1 else 2
                        var bonusGanado = daño / porc
                        var stat = buff.primerValor
                        if (stat == Constantes.STAT_CURAR) {
                            stat = Constantes.STAT_MAS_VITALIDAD
                        }
                        var max = 0
                        try {
                            max = buff.args.split(",".toRegex()).toTypedArray()[1].toInt()
                        } catch (ignored: Exception) {
                        }
                        max -= objetivo.getBonusCastigo(stat)
                        if (max <= 0 || bonusGanado <= 0) {
                            continue@loop
                        }
                        if (bonusGanado > max) {
                            bonusGanado = max
                        }
                        objetivo.setBonusCastigo(objetivo.getBonusCastigo(stat) + bonusGanado, stat)
                        val splits = buff.args.split(",".toRegex(), 2).toTypedArray()
                        val duo = objetivo.addBuffConGIE(
                            stat, bonusGanado, 5, buff.hechizoID, convertirArgs(
                                bonusGanado, stat, bonusGanado.toString() + "," + splits[1]
                            ), lanzador, true, TipoDaño.POST_TURNOS, ""
                        )
                        if (duo._segundo != null) {
                            duo._segundo!!.setDesbufeable(AtlantaMain.PARAM_BOOST_SACRO_DESBUFEABLE)
                        }
                        ENVIAR_GA_ACCION_PELEA(
                            pelea, 7, stat, lanzador.id.toString() + "", objetivo.id.toString() + ","
                                    + bonusGanado + "," + 5
                        )
                    }
                    else -> {
                    }
                }
                // }
            }
            return daño
        }

        private fun efectoEmpujones(
            pelea: Pelea, lanzador: Luchador, objetivo: Luchador?, celdaInicio: Celda?,
            celdaDestino: Celda?, nCeldasAMover: Int, golpe: Boolean
        ) {
            var nCeldasAMover = nCeldasAMover
            if (nCeldasAMover == 0 || objetivo!!.estaMuerto() || objetivo.esEstatico() || objetivo.tieneEstado(
                    Constantes.ESTADO_ARRAIGADO.toInt()
                )
            ) {
                return
            }
            if (nCeldasAMover < 100) {
                objetivo.retrocediendo = false
            }
            //		else if (nCeldasAMover>999 && objetivo.retrocediendo){
//			nCeldasAMover-=1000;
//		}
            if (objetivo.retrocediendo && nCeldasAMover < 999) {
                return
            }
            if (nCeldasAMover > 999) {
                nCeldasAMover -= 1000
            }
            val mapaCopia = pelea.mapaCopia
            val duo = Camino.getCeldaDespuesDeEmpujon(pelea, celdaInicio!!, celdaDestino!!, nCeldasAMover)
            val celdasFaltantes = duo._primero
            if (celdasFaltantes == -1) {
                return
            }
            nCeldasAMover = Math.abs(nCeldasAMover)
            var dañoEmpuje = 0
            val nuevaCeldaID = duo._segundo
            if (celdasFaltantes in 1..99) { // si falto celdas para seguir empujando
                nCeldasAMover -= celdasFaltantes
                if (golpe) {
                    var empujador: Luchador? = lanzador
                    if (AtlantaMain.PARAM_MOB_TENER_NIVEL_INVOCADOR_PARA_EMPUJAR) {
                        while (empujador!!.esInvocacion()) {
                            empujador = empujador.invocador
                        }
                    }
                    val nivelEmpujador = empujador!!.nivel
                    val statsDañoEmpuje =
                        lanzador.totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_DAÑOS_EMPUJE)
                    // modifique esta formula por la q me dio un frances, y suena mejor
                    val max = Math.max(1, 8 * nivelEmpujador / 50)
                    val rand = getRandomInt(1, max)
                    dañoEmpuje = (8 + rand) * celdasFaltantes
                    if (dañoEmpuje < 8) {
                        dañoEmpuje = 8
                    }
                    dañoEmpuje += statsDañoEmpuje
                    if (dañoEmpuje < 0) {
                        dañoEmpuje = 0
                    }
                }
            }
            if (nuevaCeldaID > 0 && objetivo.celdaPelea!!.id != nuevaCeldaID) {
                val nuevaCelda = mapaCopia!!.getCelda(nuevaCeldaID)
                if (nuevaCelda != null) {
                    val transportado = objetivo.transportando
                    if (transportado != null) {
                        objetivo.celdaPelea!!.removerLuchador(objetivo)
                        objetivo.celdaPelea = nuevaCelda
                        pelea.quitarTransportados(objetivo)
                    } else {
                        objetivo.celdaPelea!!.moverLuchadoresACelda(nuevaCelda)
                    }
                    ENVIAR_GA_ACCION_PELEA(
                        pelea,
                        7,
                        5,
                        lanzador.id.toString() + "",
                        objetivo.id.toString() + "," + nuevaCeldaID
                    )
                    try { //                    lanzador.tiempoempujado+=(long)(300 + (200 * Math.sqrt(nCeldasAMover))) *2;
                        lanzador.empujo = true
                        //                    System.out.println((int) (300 + (200 * Math.sqrt(nCeldasAMover))));
                        Thread.sleep(((300 + (200 * sqrt(nCeldasAMover.toDouble()))).toLong()))
                    } catch (ignored: Exception) {
                    }
                }
            }
            if (dañoEmpuje > 0) {
                val dir = Camino.direccionEntreDosCeldas(mapaCopia, celdaInicio.id, celdaDestino.id, true)
                var afectado: Luchador? = null
                var celdaQueGolpea: Celda? = null
                while (dañoEmpuje > 0) {
                    if (celdaQueGolpea == null) {
                        afectado = objetivo
                        celdaQueGolpea = objetivo.celdaPelea
                    } else {
                        val sigCeldaID = Camino.getSigIDCeldaMismaDir(celdaQueGolpea.id, dir, mapaCopia, true)
                        val sigCelda = mapaCopia!!.getCelda(sigCeldaID) ?: break
                        celdaQueGolpea = sigCelda
                        afectado = sigCelda.primerLuchador
                        if (afectado == null) {
                            break
                        }
                    }
                    val redDañoEmpuje =
                        afectado.totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_REDUCCION_EMPUJE)
                    val dañoEmpuje2 = dañoEmpuje - redDañoEmpuje
                    dañoEmpuje /= 2
                    if (redDañoEmpuje > 0) {
                        ENVIAR_GA_ACCION_PELEA(
                            pelea, 7, Constantes.STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA, lanzador.id
                                .toString() + "", afectado.id.toString() + "," + redDañoEmpuje
                        )
                    }
                    if (dañoEmpuje2 > 0) {
                        restarPDVLuchador(pelea, afectado, lanzador, null, dañoEmpuje2)
                        if (afectado.estaMuerto()) {
                            break
                        } else {
                            val buff = afectado.getBuff(303)
                            buff?.aplicarHechizoDeBuff(pelea, afectado, afectado.celdaPelea)
                        }
                    }
                }
            }
            verificaTrampas(objetivo)
            if (AtlantaMain.SRAM_EMPUJADOR && celdasFaltantes > 1000) { //			verificaTrampas(objetivo);
//			celdasFaltantes-=1;
                objetivo.retrocediendo = true
                efectoEmpujones(
                    pelea,
                    lanzador,
                    objetivo,
                    celdaDestino,
                    mapaCopia!!.getCelda(nuevaCeldaID),
                    celdasFaltantes - 1,
                    true
                )
            }
        }

        fun verificaTrampas(objetivo: Luchador?) {
            if (objetivo!!.celdaPelea!!.trampas == null) {
                return
            }
            for (trampa in objetivo.celdaPelea!!.trampas!!) {
                trampa.activarTrampa(objetivo)
            }
        }

        private fun reenvioHechizo(
            pelea: Pelea, nivelHechizo: Int, hechizoID: Int, objetivo: Luchador,
            lanzador: Luchador, tipo: TipoDaño
        ): Luchador {
            if (tipo != TipoDaño.NORMAL) {
                return objetivo
            }
            var retorno = objetivo
            if (hechizoID != 0 && objetivo.getValorPorBuffsID(106) >= nivelHechizo) {
                val azar = getRandomInt(1, 100)
                val reenvia = azar <= objetivo.getBuff(106)!!.tercerValor
                ENVIAR_GA_ACCION_PELEA(
                    pelea,
                    7,
                    106,
                    objetivo.id.toString() + "",
                    objetivo.id.toString() + "," + if (reenvia) 1 else 0
                )
                if (reenvia) {
                    retorno = lanzador
                }
            }
            return retorno
        }

        fun sacrificio(pelea: Pelea?, objetivo: Luchador): Luchador {
            var retorno = objetivo
            if (retorno.tieneBuff(765)) {
                if (retorno.estaMuerto() || retorno.esEstatico() || retorno.tieneEstado(Constantes.ESTADO_ARRAIGADO.toInt()) || retorno
                        .tieneEstado(Constantes.ESTADO_TRANSPORTADO.toInt()) || retorno.tieneEstado(Constantes.ESTADO_PORTADOR.toInt())
                ) {
                    return retorno
                }
                val sacrificado = retorno.getBuff(765)!!.lanzador
                if (sacrificado.estaMuerto() || sacrificado.esEstatico() || sacrificado.tieneEstado(Constantes.ESTADO_ARRAIGADO.toInt())
                    || sacrificado.tieneEstado(Constantes.ESTADO_TRANSPORTADO.toInt()) || sacrificado.tieneEstado(
                        Constantes.ESTADO_PORTADOR.toInt()
                    )
                ) {
                    return retorno
                }
                val cSacrificado = sacrificado.celdaPelea
                val cObjetivo = objetivo.celdaPelea
                cSacrificado!!.limpiarLuchadores()
                cObjetivo!!.limpiarLuchadores()
                sacrificado.celdaPelea = cObjetivo
                objetivo.celdaPelea = cSacrificado
                ENVIAR_GA_ACCION_PELEA(
                    pelea!!, 7, 4, objetivo.id.toString() + "", objetivo.id.toString() + "," + cSacrificado
                        .id
                )
                ENVIAR_GA_ACCION_PELEA(
                    pelea, 7, 4, sacrificado.id.toString() + "", sacrificado.id.toString() + "," + cObjetivo
                        .id
                )
                retorno = sacrificado
                try {
                    Thread.sleep(250)
                } catch (ignored: Exception) {
                }
            }
            if (retorno.tieneBuff(766)) { // intercepcion de daños (no cambia de posicion)
                if (retorno.esEstatico() || retorno.estaMuerto()) {
                    return retorno
                }
                val sacrificado = retorno.getBuff(766)!!.lanzador
                if (sacrificado.estaMuerto()) {
                    return retorno
                }
                retorno = sacrificado
            }
            return retorno
        }

        // public static int maxResistencia(Luchador objetivo, int resPorcT) {
// if (objetivo.getMob() != null) {
// return resPorcT;
// }
// // if (objetivo.getPersonaje() != null && resPorcT >
// AtlantaMain.LIMITE_PORC_RESISTENCIA_BUFFS)
// // {
// // return AtlantaMain.LIMITE_PORC_RESISTENCIA_BUFFS;
// // }
// if (resPorcT > AtlantaMain.LIMITE_PORC_RESISTENCIA_BUFFS) {// recaurdador, prisma
// return AtlantaMain.LIMITE_PORC_RESISTENCIA_BUFFS;
// }
// return resPorcT;
// }
        fun calcularCuraFinal(curador: Luchador, base: Int): Int {
            val stats = curador.totalStats
            val inteligencia =
                Math.max(0, stats.getTotalStatParaMostrar(Constantes.STAT_MAS_INTELIGENCIA))
            val curas = Math.max(0, stats.getTotalStatParaMostrar(Constantes.STAT_MAS_CURAS))
            return Math.max(0, base * (100 + inteligencia) / 100 + curas)
        }

        fun getDañosReducidos(afectado: Luchador, elementoID: Int): Int {
            var defensa = 0
            val stats = intArrayOf(
                Constantes.STAT_MAS_DAÑOS_REDUCIDOS_ARMADURAS_FECA,
                Constantes.STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA
            )
            for (efectoID in stats) {
                loop@ for (buff in afectado.getBuffsPorEfectoID(efectoID)) { // daños reducidos
                    var statComplementario = Constantes.STAT_MAS_INTELIGENCIA
                    when (buff.hechizoID) {
                        1, 452 -> {
                            if (elementoID != Constantes.ELEMENTO_FUEGO.toInt()) {
                                continue@loop
                            }
                            statComplementario = Constantes.STAT_MAS_INTELIGENCIA
                        }
                        6, 453 -> {
                            if (elementoID != Constantes.ELEMENTO_NEUTRAL.toInt() && elementoID != Constantes.ELEMENTO_TIERRA.toInt()) {
                                continue@loop
                            }
                            statComplementario = Constantes.STAT_MAS_FUERZA
                        }
                        14, 454 -> {
                            if (elementoID != Constantes.ELEMENTO_AIRE.toInt()) {
                                continue@loop
                            }
                            statComplementario = Constantes.STAT_MAS_AGILIDAD
                        }
                        18, 451 -> {
                            if (elementoID != Constantes.ELEMENTO_AGUA.toInt()) {
                                continue@loop
                            }
                            statComplementario = Constantes.STAT_MAS_SUERTE
                        }
                    }
                    var lTemp = buff.lanzador
                    if (efectoID == Constantes.STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA) {
                        lTemp = afectado
                    }
                    var inteligencia =
                        afectado.totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_INTELIGENCIA)
                    inteligencia = Math.max(0, inteligencia)
                    var complemento = lTemp.totalStats.getTotalStatParaMostrar(statComplementario)
                    complemento = Math.max(0, complemento)
                    val value = buff.getRandomValor(buff.lanzador)
                    val sinComp = 1 + complemento / 100f
                    val conComp = 1 + inteligencia / 200f + complemento / 200f
                    defensa = (defensa + Math.max(conComp, sinComp) * value).toInt()
                }
            }
            return defensa
        }

        fun getPuntosPerdidos(
            efecto: Int, puntosARestar: Int, lanzador: Luchador,
            objetivo: Luchador
        ): Int {
            val esquivaLanzador: Int
            var esquivaObjetivo: Int
            var puntosIniciales: Int
            var puntosActuales = 0
            esquivaLanzador = lanzador.totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_SABIDURIA) / 4
            if (efecto == Constantes.STAT_MENOS_PM) { // movimiento
                esquivaObjetivo = objetivo.totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_ESQUIVA_PERD_PM)
                puntosActuales = objetivo.totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_PM)
                puntosIniciales = objetivo.baseStats!!.getStatParaMostrar(Constantes.STAT_MAS_PM)
                if (objetivo.objetosStats != null) {
                    puntosIniciales += objetivo.objetosStats!!.getStatParaMostrar(Constantes.STAT_MAS_PM)
                }
            } else {
                esquivaObjetivo = objetivo.totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_ESQUIVA_PERD_PA)
                puntosActuales = objetivo.totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_PA)
                puntosIniciales = objetivo.baseStats!!.getStatParaMostrar(Constantes.STAT_MAS_PA)
                if (objetivo.objetosStats != null) {
                    puntosIniciales += objetivo.objetosStats!!.getStatParaMostrar(Constantes.STAT_MAS_PA)
                }
            }
            var plus = 0
            if (esquivaObjetivo < 0) {
                plus = Math.abs(esquivaObjetivo)
            }
            esquivaObjetivo = Math.max(1, esquivaObjetivo)
            puntosIniciales = Math.max(1, puntosIniciales)
            // System.out.println("--------------");
// System.out.println("esquivaLanzador " + esquivaLanzador);
// System.out.println("esquivaObjetivo " + esquivaObjetivo);
// System.out.println("puntosActuales " + puntosActuales);
// System.out.println("puntosiniciales " + puntosIniciales);
            var restar = 0
            for (i in 0 until puntosARestar) {
                var acierto =
                    (puntosActuales / puntosIniciales.toFloat() * (esquivaLanzador / esquivaObjetivo.toFloat())
                            * 50).toInt()
                // System.out.println("prob " + acierto);
                acierto += plus
                if (acierto < 10) {
                    acierto = 10
                } else if (acierto > 90) {
                    acierto = 90
                }
                if (acierto >= getRandomInt(1, 100)) {
                    puntosActuales--
                    restar++
                }
            }
            if (restar > puntosActuales) {
                restar = puntosActuales
            }
            return restar
        }

        fun dañoPorEspalda(
            pelea: Pelea?,
            lanzador: Luchador?,
            objetivo: Luchador?,
            daño: Int
        ): Int { // if (AtlantaMain.BONUS_ATAQUE_ESPALDA > 0 && lanzador.getDireccion() ==
// objetivo.getDireccion()) {
// if (Camino.esSiguienteA(lanzador.getCeldaPelea(), objetivo.getCeldaPelea())) {
// daño += (daño * AtlantaMain.BONUS_ATAQUE_ESPALDA / 100);
// // GestorSalida.ENVIAR_cS_EMOTE_EN_PELEA(pelea, 7, objetivo.getID(), 61);
// // GestorSalida.ENVIAR_cS_EMOTE_EN_PELEA(pelea, 7, lanzador.getID(), 68);
// }
// }
            return daño
        }

        fun calcularDañoFinal(
            pelea: Pelea, lanzador: Luchador, objetivo: Luchador, elemento: Int,
            dañoInicial: Float, hechizoID: Int, tipoDaño: TipoDaño, esGC: Boolean
        ): Int {
            var elemento = elemento
            val esCaC = tipoDaño == TipoDaño.CAC
            val tStatsLanzador = lanzador.totalStats
            val tStatsObjetivo = objetivo.totalStats
            var multiDañoPJ = MULTIPLICADOR_DAÑO_PJ
            var statC = 0
            var resMasO = 0
            var resPorcO = 0
            var redMag = 0
            var redFis = 0
            var redArmadO = 0
            var masDaños = 0
            var porcDaños = 0
            var multiplicaDaños = 0
            val lanzaPerso = lanzador.personaje
            var info: StringBuilder? = null
            if (AtlantaMain.PARAM_INFO_DAÑO_BATALLA) {
                info = StringBuilder()
                info.append("SpellID: ").append(hechizoID).append(" TargetID: ").append(objetivo.id).append(" CaC: ")
                    .append(esCaC).append(" GC: ").append(esGC).append(" DmgStart: ").append(dañoInicial)
            }
            when (elemento.toByte()) {
                (Constantes.ELEMENTO_NEUTRAL + 10).toByte(), Constantes.ELEMENTO_NEUTRAL -> {
                    statC = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_FUERZA)
                    masDaños = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_DAÑO_FISICO)
                    masDaños = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_DAÑOS_DE_NEUTRAL)
                    resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_NEUTRAL)
                    resPorcO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_NEUTRAL)
                    redFis = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_REDUCCION_FISICA)
                    when (pelea.tipoPelea.toByte()) {
                        Constantes.PELEA_TIPO_DESAFIO, Constantes.PELEA_TIPO_KOLISEO, Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_RECAUDADOR -> {
                            resPorcO =
                                tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_PVP_NEUTRAL)
                            resMasO =
                                tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_PVP_NEUTRAL)
                        }
                    }
                }
                (Constantes.ELEMENTO_TIERRA + 10).toByte(), Constantes.ELEMENTO_TIERRA -> {
                    statC = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_FUERZA)
                    masDaños = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_DAÑO_FISICO)
                    masDaños = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_DAÑOS_DE_TIERRA)
                    resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_TIERRA)
                    resPorcO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_TIERRA)
                    redFis = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_REDUCCION_FISICA)
                    when (pelea.tipoPelea.toByte()) {
                        Constantes.PELEA_TIPO_DESAFIO, Constantes.PELEA_TIPO_KOLISEO, Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_RECAUDADOR -> {
                            resPorcO =
                                tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_PVP_TIERRA)
                            resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_PVP_TIERRA)
                        }
                    }
                }
                (Constantes.ELEMENTO_FUEGO + 10).toByte(), Constantes.ELEMENTO_FUEGO -> {
                    statC = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_INTELIGENCIA)
                    masDaños = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_DAÑOS_DE_FUEGO)
                    resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_FUEGO)
                    resPorcO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_FUEGO)
                    redMag = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_REDUCCION_MAGICA)
                    when (pelea.tipoPelea.toByte()) {
                        Constantes.PELEA_TIPO_DESAFIO, Constantes.PELEA_TIPO_KOLISEO, Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_RECAUDADOR -> {
                            resPorcO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_PVP_FUEGO)
                            resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_PVP_FUEGO)
                        }
                    }
                }
                (Constantes.ELEMENTO_AGUA + 10).toByte(), Constantes.ELEMENTO_AGUA -> {
                    statC = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_SUERTE)
                    masDaños = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_DAÑOS_DE_AGUA)
                    resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_AGUA)
                    resPorcO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_AGUA)
                    redMag = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_REDUCCION_MAGICA)
                    when (pelea.tipoPelea.toByte()) {
                        Constantes.PELEA_TIPO_DESAFIO, Constantes.PELEA_TIPO_KOLISEO, Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_RECAUDADOR -> {
                            resPorcO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_PVP_AGUA)
                            resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_PVP_AGUA)
                        }
                    }
                }
                (Constantes.ELEMENTO_AIRE + 10).toByte(), Constantes.ELEMENTO_AIRE -> {
                    statC = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_AGILIDAD)
                    masDaños = tStatsLanzador.getTotalStatConComplemento(Constantes.STAT_MAS_DAÑOS_DE_AIRE)
                    resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_AIRE)
                    resPorcO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_AIRE)
                    redMag = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_REDUCCION_MAGICA)
                    when (pelea.tipoPelea.toByte()) {
                        Constantes.PELEA_TIPO_DESAFIO, Constantes.PELEA_TIPO_KOLISEO, Constantes.PELEA_TIPO_PVP, Constantes.PELEA_TIPO_RECAUDADOR -> {
                            resPorcO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_PORC_PVP_AIRE)
                            resMasO = tStatsObjetivo.getTotalStatConComplemento(Constantes.STAT_MAS_RES_FIJA_PVP_AIRE)
                        }
                    }
                }
            }
            if (elemento >= 10) {
                elemento -= 10
                statC = 0
                masDaños = 0
                porcDaños = 0
            } else {
                masDaños += tStatsLanzador.getTotalStatParaMostrar(Constantes.STAT_MAS_DAÑOS)
                porcDaños += tStatsLanzador.getTotalStatParaMostrar(Constantes.STAT_MAS_PORC_DAÑOS)
            }
            multiplicaDaños = tStatsLanzador.getTotalStatParaMostrar(Constantes.STAT_MULTIPLICA_DAÑOS)
            if (AtlantaMain.PARAM_INFO_DAÑO_BATALLA) {
                Objects.requireNonNull(info)!!.append("\n")
                info!!.append("PtsStats: ").append(statC).append(" +Dmg: ").append(masDaños).append(" %Dmg: ")
                    .append(porcDaños).append(" +ResTarget: ").append(resMasO).append(" %ResTarget: ").append(resPorcO)
                    .append(" RedMagTarget: ").append(redMag).append(" RedPhyTarget: ").append(redFis).append(" xDmg: ")
                    .append(multiplicaDaños)
            }
            // resPorcO = maxResistencia(objetivo, resPorcO);
            var armaClase = 90
            var dominioArma = 0
            if (lanzaPerso != null && esCaC) {
                multiDañoPJ = MULTIPLICADOR_DAÑO_CAC
                var armaTipo = 0
                try {
                    armaTipo = lanzaPerso.getObjPosicion(Constantes.OBJETO_POS_ARMA).objModelo?.tipo?.toInt() ?: 0
                    val clase = lanzaPerso.getClaseID(true).toInt()
                    dominioArma = lanzador.getValorPorPrimerYEfectoID(165, armaTipo)
                    when (armaTipo) {
                        Constantes.OBJETO_TIPO_ARCO -> armaClase = if (clase == Constantes.CLASE_SRAM.toInt()) {
                            95
                        } else {
                            if (clase != Constantes.CLASE_OCRA.toInt()) {
                                90
                            } else {
                                100
                            }
                        }
                        Constantes.OBJETO_TIPO_VARITA -> armaClase =
                            if (clase == Constantes.CLASE_FECA.toInt() || clase == Constantes.CLASE_XELOR.toInt()) {
                                95
                            } else {
                                if (clase != Constantes.CLASE_ANIRIPSA.toInt()) {
                                    90
                                } else {
                                    100
                                }
                            }
                        Constantes.OBJETO_TIPO_BASTON -> armaClase =
                            if (clase == Constantes.CLASE_ANIRIPSA.toInt() || clase == Constantes.CLASE_OSAMODAS.toInt() || clase == Constantes.CLASE_PANDAWA.toInt()) {
                                95
                            } else {
                                if (clase != Constantes.CLASE_FECA.toInt() && clase != Constantes.CLASE_SADIDA.toInt()) {
                                    90
                                } else {
                                    100
                                }
                            }
                        Constantes.OBJETO_TIPO_DAGAS -> armaClase =
                            if (clase == Constantes.CLASE_OCRA.toInt() || clase == Constantes.CLASE_ZURCARAK.toInt()) {
                                95
                            } else {
                                if (clase != Constantes.CLASE_SRAM.toInt()) {
                                    90
                                } else {
                                    100
                                }
                            }
                        Constantes.OBJETO_TIPO_ESPADA -> {
                            if (clase != Constantes.CLASE_YOPUKA.toInt() && clase != Constantes.CLASE_ZURCARAK.toInt()) {
                            } else {
                                armaClase = 100
                            }
                        }
                        Constantes.OBJETO_TIPO_MARTILLO -> armaClase =
                            if (clase == Constantes.CLASE_ANUTROF.toInt() || clase == Constantes.CLASE_YOPUKA.toInt() || clase == Constantes.CLASE_SADIDA.toInt()) {
                                95
                            } else {
                                if (clase != Constantes.CLASE_OSAMODAS.toInt() && clase != Constantes.CLASE_XELOR.toInt()) {
                                    90
                                } else {
                                    100
                                }
                            }
                        Constantes.OBJETO_TIPO_PALA -> {
                            if (clase != Constantes.CLASE_ANUTROF.toInt()) {
                            } else {
                                armaClase = 100
                            }
                        }
                        Constantes.OBJETO_TIPO_HACHA -> {
                            if (clase != Constantes.CLASE_PANDAWA.toInt()) {
                            } else {
                                armaClase = 100
                            }
                        }
                    }
                    if (AtlantaMain.PARAM_INFO_DAÑO_BATALLA) {
                        Objects.requireNonNull(info)!!.append(" %DmgWeapon: ").append(dominioArma)
                            .append(" ClasseWeapon: ").append(armaClase)
                    }
                } catch (ignored: Exception) {
                }
            }
            if (statC < 0) {
                statC = 0
            }
            if (tipoDaño == TipoDaño.TRAMPA) {
                val porcTrampa = tStatsLanzador.getTotalStatParaMostrar(Constantes.STAT_MAS_PORC_DAÑOS_TRAMPA)
                val masTrampa = tStatsLanzador.getTotalStatParaMostrar(Constantes.STAT_MAS_DAÑOS_TRAMPA)
                porcDaños += porcTrampa
                masDaños += masTrampa
                if (AtlantaMain.PARAM_INFO_DAÑO_BATALLA) {
                    Objects.requireNonNull(info)!!.append(" %DmgTrap: ").append(porcTrampa)
                        .append(" +DmgTrap: ").append(masTrampa)
                }
            }
            if (multiplicaDaños < 1) {
                multiplicaDaños = 1
            }
            if (AtlantaMain.PARAM_INFO_DAÑO_BATALLA) {
                Objects.requireNonNull(info)!!.append("\n")
                info!!.append("Formule: ")
                info.append("DmgFinal = DmgStart X xDmg {").append(dañoInicial).append(" * ").append(multiplicaDaños)
                    .append("}")
            }
            var dañoFinal = dañoInicial * multiplicaDaños
            if (esCaC) {
                if (AtlantaMain.PARAM_INFO_DAÑO_BATALLA) {
                    info!!.append("\n")
                    info.append("DmgFinal = DmgFinal X ((100 + %DmgWeapon) / 100) X (ClasseWeapon / 100)   {")
                        .append(dañoFinal).append(" * ").append(" ((100 + ").append(dominioArma).append(") / 100f) * (")
                        .append(armaClase).append("/ 100f)}")
                }
                dañoFinal = dañoFinal * ((100 + dominioArma) / 100f) * (armaClase / 100f)
            }
            if (AtlantaMain.PARAM_INFO_DAÑO_BATALLA) {
                info!!.append("\n")
                info.append("DmgFinal = (DmgFinal X (100 + PtsStats + %Dmg) / 100) + +Dmg   {(").append(dañoFinal)
                    .append(" * ").append(" (100 + ").append(statC).append(" + ").append(porcDaños)
                    .append(") / 100f) + ").append(masDaños).append(" }")
            }
            dañoFinal = dañoFinal * (100 + statC + porcDaños) / 100f
            dañoFinal += masDaños.toFloat()
            if (esGC) {
                val dañoCritico = tStatsLanzador.getTotalStatParaMostrar(Constantes.STAT_MAS_DAÑOS_CRITICOS)
                val redDañoCritico = tStatsObjetivo.getTotalStatParaMostrar(Constantes.STAT_MAS_REDUCCION_CRITICOS)
                if (AtlantaMain.PARAM_INFO_DAÑO_BATALLA) {
                    Objects.requireNonNull(info)!!.append("\n")
                    info!!.append("DmgFinal = DmgFinal + +DmgCritique - +RedCritTarget  {").append(dañoFinal)
                        .append(" + ").append(dañoCritico).append(" + ").append(redDañoCritico).append("}")
                }
                dañoFinal += dañoCritico.toFloat()
                dañoFinal -= redDañoCritico.toFloat()
            }
            dañoFinal *= if (lanzador.mob != null) {
                if (lanzador.esInvocacion()) {
                    MULTIPLICADOR_DAÑO_MOB - 0.3f
                } else {
                    MULTIPLICADOR_DAÑO_MOB
                }
            } else {
                multiDañoPJ
            }
            // if (hechizoID == 2006 && elemento != -1) {//hechizo la sacrificada
// daño = lanzador.getPDVMaxSinBuff();
// }
            if (dañoFinal < 0) {
                dañoFinal = 0f
            }
            if (tipoDaño != TipoDaño.POST_TURNOS) {
                redArmadO = getDañosReducidos(objetivo, elemento)
                if (redArmadO > 0) {
                    ENVIAR_GA_ACCION_PELEA(
                        pelea, 7, Constantes.STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA, lanzador.id
                            .toString() + "", objetivo.id.toString() + "," + redArmadO
                    )
                }
            } else {
                resMasO = 0
            }
            val dañoReducido = (dañoFinal * resPorcO / 100f).toInt()
            if (AtlantaMain.PARAM_INFO_DAÑO_BATALLA) {
                Objects.requireNonNull(info)!!.append("\n")
                info!!.append("DmgFinal = DmgFinal - (DmgFinal X  %ResTarget / 100)  {").append(dañoFinal)
                    .append(" - (")
                    .append(dañoFinal).append(" * ").append(resPorcO).append(" / 100f)}")
            }
            dañoFinal -= dañoReducido.toFloat()
            val resistencias = resMasO + redMag + redFis + redArmadO
            if (AtlantaMain.PARAM_INFO_DAÑO_BATALLA) {
                info!!.append("\n")
                info.append("DmgFinal = DmgFinal - (ResistElem + RedMagic + RedPhysic + ArmSpell)  {").append(dañoFinal)
                    .append(" - (").append(resMasO).append(" + ").append(redMag).append(" + ").append(redFis)
                    .append(" + ").append(redArmadO).append(")}")
            }
            dañoFinal -= resistencias.toFloat()
            if (dañoFinal < 0) {
                dañoFinal = 0f
            }
            for (b in objetivo.buffsPelea) {
                if (b.condicionBuff.isEmpty()) {
                    continue
                }
                var condicion = ""
                when (elemento.toByte()) {
                    Constantes.ELEMENTO_NEUTRAL -> condicion = "N"
                    Constantes.ELEMENTO_TIERRA -> condicion = "E"
                    Constantes.ELEMENTO_FUEGO -> condicion = "F"
                    Constantes.ELEMENTO_AGUA -> condicion = "W"
                    Constantes.ELEMENTO_AIRE -> condicion = "A"
                }
                if (b.condicionBuff.contains("DMG_ALL") || b.condicionBuff.contains("DMG_$condicion") || b
                        .condicionBuff.contains("D_") && b.condicionBuff.contains(condicion)
                ) {
                    b.aplicarBuffCondicional(objetivo)
                }
            }
            if (AtlantaMain.PARAM_INFO_DAÑO_BATALLA && lanzaPerso != null) {
                ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                    lanzaPerso,
                    Objects.requireNonNull(info).toString()
                )
            }
            if ((pelea.tipoPelea == Constantes.PELEA_TIPO_PVM.toInt() || pelea
                    .tipoPelea == Constantes.PELEA_TIPO_PVM_NO_ESPADA.toInt()) && pelea.retos != null && lanzador.esNoIA()
            ) {
                for ((retoID, reto) in pelea.retos!!) {
                    val exitoReto = reto.estado
                    if (exitoReto !== EstReto.EN_ESPERA) {
                        continue
                    }
                    if (retoID == Constantes.RETO_BLITZKRIEG) {
                        if (objetivo.equipoBin.toInt() == 1) {
                            if (reto.luchMob == null) {
                                reto.setMob(objetivo)
                            }
                        }
                    }
                }
            }
            pelea.setUltimoTipoDaño(tipoDaño)
            objetivo.ultimoElementoDaño = elemento
            return dañoFinal.toInt()
        }

        fun buffFinTurno(luchTurno: Luchador) {
            var cadaCuantosPA: Int
            var nroPAusados: Int
            // efecto daños por PA usados
            for (buff in luchTurno.getBuffsPorEfectoID(131)) {
                if (luchTurno.estaMuerto()) {
                    continue
                }
                val dañoPorPA = buff.segundoValor
                if (dañoPorPA <= 0) {
                    continue
                }
                cadaCuantosPA = buff.primerValor
                nroPAusados = Math.floor(luchTurno.paUsados / cadaCuantosPA.toDouble()).toInt()
                val statsTotal = buff.lanzador.totalStats
                val inteligencia = statsTotal.getTotalStatParaMostrar(Constantes.STAT_MAS_INTELIGENCIA)
                val pDaños = statsTotal.getTotalStatParaMostrar(Constantes.STAT_MAS_PORC_DAÑOS)
                val masDaños = statsTotal.getTotalStatParaMostrar(Constantes.STAT_MAS_DAÑOS)
                val reduccion = statsTotal.getTotalStatParaMostrar(Constantes.STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA)
                val factor = (100 + inteligencia + pDaños) / 100f
                var daño = (dañoPorPA * factor * nroPAusados).toInt() + masDaños
                if (reduccion > 0) {
                    ENVIAR_GA_ACCION_PELEA(
                        luchTurno.pelea,
                        7,
                        Constantes.STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA,
                        luchTurno.id.toString() + "",
                        luchTurno.id.toString() + "," + reduccion
                    )
                    daño -= reduccion
                }
                if (daño <= 0) {
                    continue
                }
                luchTurno.pelea.setUltimoTipoDaño(TipoDaño.POST_TURNOS)
                if (luchTurno.mob != null) {
                    if (luchTurno.mob!!.mobModelo.id == 423) { // kralamar
                        continue
                    }
                }
                restarPDVLuchador(luchTurno.pelea, luchTurno, buff.lanzador, null, daño)
                if (luchTurno.estaMuerto()) {
                    break
                }
            }
        }

        private fun restarPDVLuchador(
            pelea: Pelea?, objetivo: Luchador?, lanzador: Luchador, afectados: StringBuilder?,
            valor: Int
        ): Int {
            var valor = valor
            if (valor < 0) {
                var cura = -valor
                if (cura + objetivo!!.pdvSinBuff > objetivo.pdvMaxSinBuff) {
                    cura = objetivo.pdvMaxSinBuff - objetivo.pdvSinBuff
                }
                valor = -cura
            }
            val vitalidad = objetivo!!.buffsStats!!.getStatParaMostrar(Constantes.STAT_MAS_VITALIDAD)
            objetivo.restarPDV(valor)
            val pdv = objetivo.pdvSinBuff + vitalidad
            if (pdv <= 0) {
                valor += pdv // si pdv es menor a 0 le resta al daño
                ENVIAR_GA_ACCION_PELEA(
                    pelea!!,
                    7,
                    100,
                    lanzador.id.toString() + "",
                    objetivo.id.toString() + "," + -valor
                )
                pelea.addMuertosReturnFinalizo(objetivo, lanzador)
            } else if (afectados == null) {
                ENVIAR_GA_ACCION_PELEA(
                    pelea!!,
                    7,
                    100,
                    lanzador.id.toString() + "",
                    objetivo.id.toString() + "," + -valor
                )
            } else {
                if (afectados.length > 0) {
                    afectados.append("¬")
                }
                afectados.append(objetivo.id).append(",").append(-valor)
            }
            return valor
        }
    }

}