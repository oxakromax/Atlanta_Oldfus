package estaticos

import variables.gremio.Recaudador
import variables.mapa.Mapa
import variables.pelea.Botin
import variables.pelea.Luchador
import variables.personaje.Personaje
import variables.stats.TotalStats
import java.security.SecureRandom
import java.util.*
import kotlin.math.*

object Formulas {
    val RANDOM = SecureRandom()

    val randomBoolean: Boolean
        get() = RANDOM.nextBoolean()

    fun lanzarError() {
        try {
            Integer.parseInt("3RR0R")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun valorValido(cantidad: Int, precio: Int): Boolean {
        // if (cantidad > 1000) {
        // return false;
        // }
        if (precio == 0 || cantidad == 0) {
            return false
        }
        val signo = precio >= 0
        for (i in 0 until cantidad) {
            val signo2 = precio * (i + 1) >= 0
            if (signo2 != signo) {
                return true
            }
        }
        return false
    }

    fun segundosON(): Int {
        return ((System.currentTimeMillis() - AtlantaMain.encendido) / 1000).toInt()
    }

    fun minutosON(): Int {
        return segundosON() / 60
    }

    fun horasON(): Int {
        return minutosON() / 60
    }

    fun diasON(): Int {
        return horasON() / 24
    }

    fun getRandomInt(i1: Int, i2: Int): Int {
        try {
            if (i1 < 0) {
                return i2
            }
            if (i2 < 0) {
                return i1
            }
            return if (i1 > i2) {
                RANDOM.nextInt(i1 - i2 + 1) + i2
            } else {
                RANDOM.nextInt(i2 - i1 + 1) + i1
            }
        } catch (e: Exception) {
            return 0
        }

    }

    private fun getRandomLong(i1: Long, i2: Long): Long {
        try {
            if (i1 < 0) {
                return i2
            }
            if (i2 < 0) {
                return i1
            }
            return if (i1 > i2) {
                RANDOM.nextLong() % (i1 - i2) + i2
            } else {
                RANDOM.nextLong() % (i2 - i1) + i1
            }
        } catch (e: Exception) {
            return 0
        }

    }

    fun formatoTiempo(milis: Long): IntArray {
        var milis = milis
        val f = intArrayOf(1, 1000, 60000, 3600000, 86400000)
        val formato = IntArray(f.size)
        for (i in f.indices.reversed()) {
            formato[i] = (milis / f[i]).toInt()
            milis %= f[i].toLong()
        }
        return formato
    }

    fun calcularCosteZaap(mapa1: Mapa, mapa2: Mapa): Int {
        return if (mapa1.id == mapa2.id) {
            0
        } else 10 * Camino.distanciaEntreMapas(mapa1, mapa2)
    }

    // @SuppressWarnings("unused")
    // private static long getXPGanadaPVM(final ArrayList<Luchador> luchadores, final Luchador
    // luchRec, int nivelGrupoPJ,
    // int nivelGrupoMob, long totalExp, float coefBonus) {
    // if (luchRec.getPersonaje() == null) {
    // return 0;
    // }
    // final TotalStats totalStats = luchRec.getTotalStats();
    // // 910 multiplicar la xp
    // int numJugadores = 0;
    // final float coefSab = (totalStats.getStatParaMostrar(Constantes.STAT_MAS_SABIDURIA)
    // + totalStats.getStatParaMostrar(Constantes.STAT_MAS_PORC_EXP) + 100) / 100f;
    // float coefEntreNiv = nivelGrupoMob / (float) nivelGrupoPJ;
    // if (coefEntreNiv <= 1.1f && coefEntreNiv >= 0.9) {
    // coefEntreNiv = 1;
    // } else if (coefEntreNiv > 1) {
    // coefEntreNiv = 1 / coefEntreNiv;
    // } else if (coefEntreNiv < 0.01) {
    // coefEntreNiv = 0.01f;
    // }
    // for (final Luchador luch : luchadores) {
    // if (luch.esInvocacion() || luch.estaRetirado()) {
    // continue;
    // }
    // numJugadores++;
    // }
    // float coefMul = 1;
    // switch (numJugadores) {
    // case 0 :
    // coefMul = 0.5f;
    // break;
    // case 1 :
    // coefMul = 1;
    // break;
    // case 2 :
    // coefMul = 1.1f;
    // break;
    // case 3 :
    // coefMul = 1.5f;
    // break;
    // case 4 :
    // coefMul = 2.3f;
    // break;
    // case 5 :
    // coefMul = 3.1f;
    // break;
    // case 6 :
    // coefMul = 3.6f;
    // break;
    // case 7 :
    // coefMul = 4.2f;
    // break;
    // case 8 :
    // coefMul = 4.7f;
    // break;
    // default :
    // coefMul = 4.7f;
    // break;
    // }
    // long expFinal = (long) ((1 + coefSab + coefBonus) * (coefMul + coefEntreNiv) * (totalExp /
    // numJugadores))
    // * AtlantaMain.RATE_XP_PVM;
    // if (expFinal < 0) {
    // expFinal = 0;
    // }
    // return expFinal;
    // }
    fun getXPGanadaRecau(recaudador: Recaudador, totalXP: Long): Long {
        val coef = (recaudador.gremio!!.getStatRecolecta(Constantes.STAT_MAS_SABIDURIA) + 100) / 100f
        return (coef * totalXP).toLong()
    }

    fun getXPOficial(
        luchadores: ArrayList<Luchador>?, mobs: ArrayList<Luchador>,
        luchador: Luchador, coefEstrellas: Float, coefReto: Float, gano: Boolean
    ): Long {
        var sumaNivelesLuch = 0
        var maxNivelLuch = 0
        var sumaNivelesMobs = 0
        var maxNivelMob = 0
        var sumaExpMobs = 0
        var cantLuch = 0
        var esRecaudador = true
        if (luchadores != null) {
            for (luch in luchadores) {
                if (luch.id == luchador.id) {
                    esRecaudador = false
                }
                if (luch.esInvocacion() || luch.estaRetirado()) {
                    continue
                }
                cantLuch++
                sumaNivelesLuch += luch.nivelViejo
                if (maxNivelLuch < luch.nivelViejo) {
                    maxNivelLuch = luch.nivelViejo
                }
            }
        } else {
            sumaNivelesLuch = luchador.nivelViejo
            maxNivelLuch = luchador.nivelViejo
        }
        for (luch in mobs) {
            if (luch.esInvocacion() || luch.estaRetirado()) {
                continue
            }
            if (luch.mob == null) {
                continue
            }
            if (gano || luch.muertoPor != null && luch.muertoPor!!.equipoBin == luchador.equipoBin) {
                sumaExpMobs += luch.mob!!.baseXp.toInt()
                sumaNivelesMobs += luch.nivelViejo
                if (maxNivelMob < luch.nivelViejo) {
                    maxNivelMob = luch.nivelViejo
                }
            }
        }
        if (sumaExpMobs <= 0) {
            return 0
        }
        val coefSab = (luchador.totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_SABIDURIA) + luchador
            .totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_PORC_EXP) + 100) / 100f
        // ahora se calcula la media
        var coefMobLuch =
            min(luchador.nivelViejo.toLong(), (2.5f * maxNivelMob).roundToLong()) / sumaNivelesLuch.toFloat()
        if (coefMobLuch > 1) {
            coefMobLuch = 1f
        } else if (coefMobLuch < 0.2f) {
            coefMobLuch = 0.2f
        }
        var ratioLuch = 0
        if (!esRecaudador && gano) {
            for (luch in luchadores!!) {
                if (luch.esInvocacion() || luch.estaRetirado()) {
                    continue
                }
                if (luch.nivelViejo >= maxNivelLuch / 3) {
                    ratioLuch++
                }
            }
        }
        var coefNivel = 1f
        if (cantLuch > 1) {
            if (sumaNivelesLuch - 5 > sumaNivelesMobs) {
                coefNivel = sumaNivelesMobs.toFloat() / sumaNivelesLuch
            } else if (sumaNivelesLuch + 10 < sumaNivelesMobs + 10) {
                coefNivel = sumaNivelesLuch / sumaNivelesMobs.toFloat()
            }
        }
        if (coefNivel > 1.2f) {
            coefNivel = 1.2f
        } else if (coefNivel < 0.8f) {
            coefNivel = 0.8f
        }
        var coefMult = 0f
        coefMult = when (ratioLuch) {
            0 -> 0.5f
            1 -> 1f
            2 -> 1.1f
            3 -> 1.5f
            4 -> 2.3f
            5 -> 3.1f
            6 -> 3.6f
            7 -> 4.2f
            8 -> 4.7f
            else -> 4.7f
        }
        var baseXp = sumaExpMobs.toLong()
        if (!esRecaudador) {
            baseXp = (baseXp.toFloat() * coefMult * coefMobLuch * coefNivel).toLong()
        }
        var xp = (baseXp * coefSab).roundToLong().toLong()
        if (!esRecaudador) {
            xp = (xp.toFloat() * (coefReto + 1) * (coefEstrellas + 1)).toLong()
            if (luchador.personaje!!.detalleExp) {
                luchador.personaje!!.enviarmensajeNegro("Exp base: $baseXp")
                luchador.personaje!!.enviarmensajeNegro("Bonus de sabiduria: " + (coefSab - 1) * 100 + "%")
                luchador.personaje!!.enviarmensajeNegro("Bonus reto: " + coefReto * 100 + "%\nBonus Estrellas: " + coefEstrellas * 100 + "%")
                luchador.personaje!!.enviarmensajeNegro("XP antes del rate: $xp")
                luchador.personaje!!.enviarmensajeNegro("Bonus Rate pvm: " + AtlantaMain.RATE_XP_PVM * 100 + "%")
            }
            xp *= AtlantaMain.RATE_XP_PVM.toLong()

            if (luchador.personaje!!.detalleExp) {
                luchador.personaje!!.enviarmensajeNegro("Xp Ganada: $xp")
                if (!luchador.personaje!!.esAbonado()) {
                    luchador.personaje!!.enviarmensajeNegro("Exp si usted fuera abonado: " + xp * AtlantaMain.RATE_XP_PVM_ABONADOS + "\nEl Rate de Abonados multiplica la exp por: " + AtlantaMain.RATE_XP_PVM_ABONADOS * 100 + "%")
                }
            }
            //			if (ips.size()>1){
            //				xp *= (1+(0.15*ips.size()));
            //			}
            if (luchador.personaje!!.detalleExp) {
                //				luchador.getPersonaje().enviarmensajeNegro("Se detectaron: "+ips.size()+" ips diferentes en su equipo, se le ha otorgado un bonus de: "+(ips.size()*15)+"% de exp extra");
                //				luchador.getPersonaje().enviarmensajeNegro("Xp Final extra por ips diferentes en pelea: "+xp);
                if (!luchador.personaje!!.esAbonado()) {
                    luchador.personaje!!.enviarmensajeNegro("Exp si usted fuera abonado: " + xp * AtlantaMain.RATE_XP_PVM_ABONADOS + "\nEl Rate de Abonados multiplica la exp por: " + AtlantaMain.RATE_XP_PVM_ABONADOS)
                }
            }
        } else {
            xp *= AtlantaMain.RATE_XP_RECAUDADOR
        }
        if (luchador.personaje != null) {
            if (AtlantaMain.RATE_XP_PVM_ABONADOS > 1) {
                if (luchador.personaje!!.esAbonado()) {
                    xp *= AtlantaMain.RATE_XP_PVM_ABONADOS.toLong()
                    if (luchador.personaje!!.detalleExp) {
                        luchador.personaje!!.enviarmensajeNegro("Bonus abonados: " + AtlantaMain.RATE_XP_PVM_ABONADOS * 100 + "%\nXp Final por Abono: " + xp)
                    }
                }
            }
        }
        if (luchador.personaje != null && luchador.personaje!!.ultimoNivel > luchador.personaje!!.nivel && AtlantaMain.MODO_HEROICO) {
            luchador.personaje!!.enviarmensajeNegro("Exp multiplicada x2 debido a muerte heroica: " + xp * 2)
        }
        if (AtlantaMain.MODO_DEBUG) {
            println("suma exp $sumaExpMobs")
            println("ratioLuch $ratioLuch")
            println("coefMob $coefMobLuch")
            println("coefMult $coefMult")
            println("coefSab $coefSab")
            println("coefReto $coefReto")
            println("coefEstrellas $coefEstrellas")
            println("AtlantaMain.RATE_XP_PVM " + AtlantaMain.RATE_XP_PVM)
            println("sumaExpMobs $sumaExpMobs")
            println("baseXp $baseXp")
            println("xp es $xp")
        }
        if (xp < 1) {
            xp = 0
        }
        return xp
    }

    fun getRandomDecimal(decimales: Int): Float {
        val entero = RANDOM.nextInt(100)
        var decimal = 0f
        if (decimales > 0) {
            val b = decimales.toDouble()
            decimal = (RANDOM.nextInt(b.toInt()) + 1) / b.toFloat()
        }
        return entero + decimal
    }

    fun getPorcDropLuchador(porcDrop: Float, luch: Luchador): Float {
        var porcDrop = porcDrop
        porcDrop += (luch.prospeccionLuchador + luch.totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_PORC_PP) - 100) / 1000f
        if (porcDrop < 0.01) {
            porcDrop = 0.01f
        }
        return porcDrop
    }

    fun getPorcParaDropAlEquipo(
        prospecEquipo: Int, coefEstrellas: Float, coefReto: Float, drop: Botin,
        cantDropeadores: Int
    ): Float {
        // int pp = prospec * (coefReto + coefEstrellas +rate);
        var porcDrop = drop.porcentajeBotin * 1000
        var cantCeros = 0
        if (porcDrop >= 1) {
            cantCeros = log10(porcDrop.toDouble()).toInt() + 1
        }
        var rate = 0
        var porcEquipo = ((prospecEquipo - drop.prospeccionBotin).toFloat() * AtlantaMain.FACTOR_PLUS_PP_PARA_DROP
                * cantDropeadores.toFloat())
        var factor = AtlantaMain.FACTOR_ZERO_DROP
        if (drop.esDropFijo()) {
            rate = AtlantaMain.RATE_DROP_ARMAS_ETEREAS
            factor += 3
        } else {
            rate = AtlantaMain.RATE_DROP_NORMAL
        }
        if (cantCeros < factor) {
            // si factor zero es mas alto, mayor sera la dificultad para dropear
            porcEquipo = (porcEquipo / (factor - cantCeros).toFloat())
        }
        porcDrop += porcEquipo
        var coef = rate.toFloat()
        if (!AtlantaMain.PARAM_PERMITIR_BONUS_PELEA_AFECTEN_PROSPECCION) {
            coef += coefReto + coefEstrellas
        }
        var entero = (porcDrop / 1000).toInt()
        var decimal = (porcDrop % 1000).toInt()
        entero += (sqrt(entero.toDouble()) * coef).toInt()
        decimal += (sqrt(decimal.toDouble()) * coef).toInt()
        return entero + decimal / 1000f// decimal
    }

    fun getIniciativa(totalStats: TotalStats, coefPDV: Float): Int {
        var iniciativa = 0
        iniciativa += totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_INICIATIVA)// iniciativa
        iniciativa += totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_AGILIDAD)
        iniciativa += totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_INTELIGENCIA)
        iniciativa += totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_SUERTE)
        iniciativa += totalStats.getTotalStatConComplemento(Constantes.STAT_MAS_FUERZA)
        // iniciativa += getPDVMax() / fact;
        iniciativa = (iniciativa * (coefPDV / 100)).toInt()
        if (iniciativa < 0) {
            iniciativa = 0
        }
        return iniciativa
    }

    fun getXPDonada(nivelPerso: Int, nivelOtro: Int, xpGanada: Long): Long {
        val dif = nivelPerso - nivelOtro
        var coef = 0.1f
        if (dif < 10) {
        } else if (dif < 20) {
            coef = 0.08f
        } else if (dif < 30) {
            coef = 0.06f
        } else if (dif < 40) {
            coef = 0.04f
        } else if (dif < 50) {
            coef = 0.03f
        } else if (dif < 60) {
            coef = 0.02f
        } else if (dif < 70) {
            coef = 0.015f
        } else if (dif > 70) {
            coef = 0.01f
        }
        return (xpGanada * coef).toLong()
    }

    fun getXPMision(nivelGanador: Int): Long {
        var nivelGanador = nivelGanador
        if (nivelGanador >= AtlantaMain.NIVEL_MAX_PERSONAJE) {
            nivelGanador = AtlantaMain.NIVEL_MAX_PERSONAJE - 1
        }
        val experiencia = Mundo.getExpCazaCabezas(nivelGanador)
        return experiencia * AtlantaMain.RATE_XP_PVP
        // float coef = 0.125f;
        // if (nivelGanador > nivelPerdedor) {
        // coef = 1 / ((float) Math.sqrt(nivelGanador - nivelPerdedor) * 8);
        // } else if (nivelGanador < nivelPerdedor) {
        // coef = (2 / (float) Math.sqrt(nivelGanador - nivelPerdedor));
        // }
        // return (long) (exp * Bustemu.RATE_XP_PVP * coef * 8);
    }

    fun getHonorGanado(
        ganadores: ArrayList<Luchador>, perdedores: ArrayList<Luchador>,
        recibidor: Luchador, peleaMobs: Boolean
    ): Int {
        if (peleaMobs) {
            return 0
        }
        var totalNivLuchGanador = 0
        var totalNivLuchPerdedor = 0
        var cantGanadores: Byte = 0
        var cantPerdedores: Byte = 0
        val ips = ArrayList<String>(16)
        val oGanadores = ArrayList<Luchador>()
        val oPerdedores = ArrayList<Luchador>()
        while (oGanadores.size < ganadores.size) {
            var mayor = -1
            var lTemp: Luchador? = null
            for (luch in ganadores) {
                if (oGanadores.contains(luch)) {
                    continue
                }
                if (luch.nivelViejo > mayor) {
                    mayor = luch.nivelViejo
                    lTemp = luch
                }
            }
            if (lTemp != null) {
                oGanadores.add(lTemp)
            }
        }
        while (oPerdedores.size < perdedores.size) {
            var mayor = -1
            var lTemp: Luchador? = null
            for (luch in perdedores) {
                if (oPerdedores.contains(luch)) {
                    continue
                }
                if (luch.nivelViejo > mayor) {
                    mayor = luch.nivelViejo
                    lTemp = luch
                }
            }
            if (lTemp != null) {
                oPerdedores.add(lTemp)
            }
        }
        var i = 1
        for (luch in oGanadores) {
            if (luch.esInvocacion()) {
                continue
            }
            if (luch.alineacion == Constantes.ALINEACION_NEUTRAL) {
                return 0
            }
            if (!AtlantaMain.ES_LOCALHOST) {
                if (luch.personaje != null) {
                    ips.add(luch.personaje!!.cuenta.actualIP)
                }
            }
            totalNivLuchGanador += luch.nivelViejo / i
            cantGanadores++
        }
        i = 1
        for (luch in oPerdedores) {
            if (luch.esInvocacion()) {
                continue
            }
            if (luch.alineacion == Constantes.ALINEACION_NEUTRAL) {
                return 0
            }
            if (!AtlantaMain.ES_LOCALHOST) {
                if (luch.personaje != null) {
                    if (ips.contains(luch.personaje!!.cuenta.actualIP)) {
                        return 0
                    }
                }
            }
            totalNivLuchPerdedor += luch.nivelViejo / i
            // totalNivAlinPerdedor += luch.getNivel()Alineacion();
            cantPerdedores++
        }
        // System.out.println("totalNivLuchGanador " + totalNivLuchGanador);
        // System.out.println("cantPerdedores " + cantPerdedores);
        // System.out.println("totalNivLuchPerdedor " + totalNivLuchPerdedor);
        // System.out.println("cantGanadores " + cantGanadores);
        if (cantGanadores.toInt() == 0 || cantPerdedores.toInt() == 0) {
            return 0
        }
        var paso = false
        var honor = 0
        val porcPerd = totalNivLuchPerdedor * 20 / 100
        if (totalNivLuchGanador <= totalNivLuchPerdedor || abs(totalNivLuchPerdedor - totalNivLuchGanador) < AtlantaMain.RANGO_NIVEL_PVP || abs(
                totalNivLuchPerdedor - totalNivLuchGanador
            ) < porcPerd
        ) {
            paso = true
        }
        // System.out.println("porcPerd " + porcPerd);
        // System.out.println("paso " + paso);
        if (!paso) {
            return 0
        }
        var nivelAlin = recibidor.nivelAlineacion
        if (nivelAlin < 1) {
            nivelAlin = 1
        } else if (nivelAlin > 10) {
            nivelAlin = 10
        }
        if (AtlantaMain.PARAM_GANAR_HONOR_RANDOM) {
            honor = if (!ganadores.contains(recibidor)) {
                // para el perdedor negativo
                -(Mundo.getExpAlineacion(nivelAlin) * 5 / 100)
            } else {
                getRandomInt(80, 120)
            }
        } else {
            val ratio = min(2.0f, totalNivLuchPerdedor.toFloat() / totalNivLuchGanador)
            val xp = Mundo.getExpParaNivelAlineacion(nivelAlin + 1)
            // System.out.println("ratio " + ratio);
            // System.out.println("xp " + xp);
            honor = (xp.toFloat() * ratio * 10.0f / 100.0f).toInt()
            honor = min(400, honor)
            honor = max(honor, 0)
            if (!ganadores.contains(recibidor)) {
                // para el perdedor negativo
                honor = -honor
            }
        }
        return AtlantaMain.RATE_HONOR * honor
    }

    fun getKamasGanadas(maxKamas: Long, minKamas: Long, perso: Personaje?): Long {
        var posiblesKamas: Long = 0
        posiblesKamas = if (minKamas > maxKamas) {
            getRandomLong(maxKamas, minKamas)
        } else {
            getRandomLong(maxKamas, minKamas)
        }
        var coef = sqrt(AtlantaMain.RATE_KAMAS.toDouble()).toFloat()
        if (perso != null) {
            if (AtlantaMain.RATE_KAMAS_ABONADOS > 1) {
                if (perso.esAbonado()) {
                    coef += (sqrt(AtlantaMain.RATE_KAMAS_ABONADOS.toDouble()) - 1).toFloat()
                }
            }
        }
        posiblesKamas = (posiblesKamas * coef).toLong()
        return posiblesKamas
    }

    fun getKamasKoliseo(nivel: Int): Int {
        return (sqrt(nivel.toDouble()) * AtlantaMain.KOLISEO_PREMIO_KAMAS).toInt()
    }
}
