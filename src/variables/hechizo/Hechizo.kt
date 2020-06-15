package variables.hechizo

import estaticos.Camino
import estaticos.Constantes
import estaticos.Constantes.convertirStringArray
import estaticos.Constantes.getElementoPorEfectoID
import estaticos.Constantes.getNombreEfecto
import estaticos.Formulas.getRandomInt
import estaticos.GestorSalida.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_TODOS
import estaticos.database.GestorSQL.UPDATE_HECHIZO_AFECTADOS
import variables.hechizo.EfectoHechizo.TipoDaño
import variables.mapa.Celda
import variables.mapa.Mapa
import variables.pelea.Luchador
import variables.pelea.Pelea
import java.util.*
import java.util.regex.Pattern

class Hechizo(
    val iD: Int, val nombre: String, var spriteID: Int, // tipo lanz, anim pj, 1 o 0 (frente al sprite)
    var spriteInfos: String,
    valorIA: Int
) {
    private val _statsHechizos: MutableMap<Int, StatHechizo> = HashMap()
    // public ArrayList<Integer> getArrayAfectados() {
// return _afectados;
// }
    var valorIA = 0

    fun setAfectados(afectados: String) {
        var afectados = afectados
        if (afectados.contains(":") || afectados.contains(";")) {
            afectados = afectados.replace(":", "|").replace(";", ",")
            UPDATE_HECHIZO_AFECTADOS(iD, afectados)
        }
        var normales = ""
        try {
            normales = afectados.split(Pattern.quote("|").toRegex()).toTypedArray()[0]
        } catch (ignored: Exception) {
        }
        var criticos = ""
        try {
            criticos = afectados.split(Pattern.quote("|").toRegex()).toTypedArray()[1]
        } catch (ignored: Exception) {
        }
        val aNormales = normales.split(",".toRegex()).toTypedArray()
        val aCriticos = criticos.split(",".toRegex()).toTypedArray()
        for (sh in _statsHechizos.values) {
            if (sh == null) {
                continue
            }
            sh.setAfectados(aNormales, aCriticos)
        }
    }

    fun setCondiciones(condicion: String) {
        var condicion = condicion
        if (condicion.contains(":") || condicion.contains(";")) {
            condicion = condicion.replace(":", "|").replace(";", ",")
        }
        var normales = ""
        try {
            normales = condicion.split(Pattern.quote("|").toRegex()).toTypedArray()[0]
        } catch (ignored: Exception) {
        }
        var criticos = ""
        try {
            criticos = condicion.split(Pattern.quote("|").toRegex()).toTypedArray()[1]
        } catch (ignored: Exception) {
        }
        val aNormales = normales.split(",".toRegex()).toTypedArray()
        val aCriticos = criticos.split(",".toRegex()).toTypedArray()
        for (sh in _statsHechizos.values) {
            if (sh == null) {
                continue
            }
            sh.setCondiciones(aNormales, aCriticos)
        }
    }

    fun getStatsPorNivel(nivel: Int): StatHechizo? {
        return _statsHechizos[nivel]
    }

    fun addStatsHechizos(nivel: Int, stats: StatHechizo) {
        _statsHechizos[nivel] = stats
    }

    companion object {
        // public static String strDañosStats2(StatHechizo sh, int valoresStat[]) {
// StringBuilder str = new StringBuilder(sh.getHechizoID() + "");
// for (EfectoHechizo eh : sh.getEfectosNormales()) {
// int valorStat = 0;
// switch (eh.getEfectoID()) {
// case Constantes.STAT_CURAR :
// case Constantes.STAT_CURAR_2 :
// valorStat = valoresStat[2];// inteligencia
// break;
// default :
// byte elemento = Constantes.getElementoPorEfectoID(eh.getEfectoID());
// if (elemento < 0) {
// continue;
// }
// valorStat = valoresStat[elemento];
// break;
// }
// str.append(";" + eh.getEfectoID() + "," + EfectoHechizo.strMinMax(eh, valorStat));
// }
// return str.toString();
// }
//
        fun strDañosStats(sh: StatHechizo, valoresStat: IntArray): String {
            val str = StringBuilder(sh.hechizoID.toString() + "")
            var paso = false
            for (eh in sh.efectosNormales) {
                val nombre = getNombreEfecto(eh.efectoID)
                var valorStat = -1
                when (eh.efectoID) {
                    Constantes.STAT_CURAR, Constantes.STAT_CURAR_2 -> valorStat = valoresStat[2] // inteligencia
                    else -> {
                        val elemento = getElementoPorEfectoID(eh.efectoID)
                        if (elemento != Constantes.ELEMENTO_NULO) {
                            valorStat = valoresStat[elemento.toInt()]
                        }
                    }
                }
                if (paso) {
                    str.append("\n")
                } else {
                    str.append(";")
                }
                str.append("-> ").append(nombre)
                str.append(" ").append(stringDataEfecto(eh, valorStat))
                paso = true
            }
            return str.toString()
        }

        private fun stringDataEfecto(EH: EfectoHechizo, valorStat: Int): String {
            var s = ""
            s = if (valorStat != -1) {
                if (EH.segundoValor != -1) {
                    "of " + EH.primerValor * (100 + valorStat) / 100 + " a " + (EH.segundoValor * (100 + valorStat)
                            / 100)
                } else if (EH.primerValor != -1) {
                    "fix " + EH.primerValor * (100 + valorStat) / 100
                } else {
                    "[" + EH.primerValor + ", " + EH.segundoValor + "]"
                }
            } else {
                "[" + EH.primerValor + ", " + EH.segundoValor + "]"
            }
            s += " (Turns: " + (if (EH.duracion <= -1) "Inf." else EH.duracion) + ")"
            return "<i>$s</i>"
        }

        fun aplicaHechizoAPelea(
            pelea: Pelea, lanzador: Luchador, celdaObj: Celda,
            efectosH: ArrayList<EfectoHechizo>?, tipo: TipoDaño?, esGC: Boolean
        ) {
            val cantObjetivos =
                aplicaHechizoAPeleaSinGTM(pelea, lanzador, celdaObj, efectosH, tipo, esGC)
            if (cantObjetivos > 0) {
                ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_TODOS(pelea, 7, true)
            }
        }

        fun aplicaHechizoAPeleaSinGTM(
            pelea: Pelea, lanzador: Luchador, celdaObj: Celda,
            efectosH: ArrayList<EfectoHechizo>?, tipo: TipoDaño?, esGC: Boolean
        ): Int {
            if (efectosH == null) {
                return 0
            }
            var suerte = 0
            var suerteMax = 0
            var cantObjetivos = 0
            var azar = 0
            for (EH in efectosH) {
                suerteMax += EH.suerte
            }
            if (suerteMax > 0) {
                azar = getRandomInt(1, suerteMax)
            }
            for (EH in efectosH) {
                if (pelea.fase == Constantes.PELEA_FASE_FINALIZADO) {
                    return 0
                }
                if (suerteMax > 0) {
                    if (EH.suerte > 0 && EH.suerte < 100) {
                        if (azar < suerte || azar >= EH.suerte + suerte) {
                            suerte += EH.suerte
                            continue
                        }
                        suerte += EH.suerte
                    }
                }
                val objetivos =
                    getObjetivosEfecto(pelea.mapaCopia, lanzador, EH, celdaObj.id)
                if (cantObjetivos < objetivos.size) {
                    cantObjetivos = objetivos.size
                }
                EH.aplicarAPelea(pelea, lanzador, objetivos, celdaObj, tipo!!, esGC)
                //			try {
//				Thread.sleep(EfectoHechizo.TIEMPO_ENTRE_EFECTOS);
//			} catch (Exception e) {}
            }
            return cantObjetivos
        }

        // public static int aplicaHechizoAPelea(final Pelea pelea, final Luchador lanzador, final Celda
// celdaObj,
// final ArrayList<EfectoHechizo> efectosH, final TipoDaño tipo, final boolean esGC,
// ArrayList<ArrayList<Luchador>> aObjetivos) {
// if (efectosH == null) {
// return 0;
// }
// int suerte = 0, suerteMax = 0, cantObjetivos = 0, azar = 0;
// for (final EfectoHechizo EH : efectosH) {
// suerteMax += EH.getSuerte();
// }
// if (suerteMax > 0) {
// azar = Formulas.getRandomValor(1, suerteMax);
// }
// int index = 0;
// for (final EfectoHechizo EH : efectosH) {
// index++;
// if (pelea.getFase() == Constantes.PELEA_FASE_FINALIZADO) {
// return 0;
// }
// if (suerteMax > 0) {
// if (EH.getSuerte() > 0 && EH.getSuerte() < 100) {
// if (azar < suerte || azar >= EH.getSuerte() + suerte) {
// suerte += EH.getSuerte();
// continue;
// }
// suerte += EH.getSuerte();
// }
// }
// ArrayList<Luchador> objetivos = aObjetivos.get(index);
// if (cantObjetivos < objetivos.size()) {
// cantObjetivos = objetivos.size();
// }
// EH.aplicarAPelea(pelea, lanzador, objetivos, celdaObj, tipo, esGC);
// try {
// Thread.sleep(EfectoHechizo.TIEMPO_ENTRE_EFECTOS);
// } catch (Exception e) {}
// }
// return cantObjetivos;
// }
        fun getObjetivosEfecto(
            mapa: Mapa?, lanzador: Luchador, EH: EfectoHechizo,
            celdaObjetivo: Short
        ): ArrayList<Luchador> {
            var objetivos = ArrayList<Luchador>()
            val elemento = EH.afectadosCond
            if (elemento > 0) { // son bytes
                val ultDaño = lanzador.ultimoElementoDaño
                if (ultDaño < Constantes.ELEMENTO_NULO) {
                    return objetivos
                }
                if (1 shl ultDaño and elemento == 0) {
                    return objetivos
                }
            }
            val celdasObj = lanzador.celdaPelea
                ?.id?.let {
                EH.zonaEfecto?.let { it1 ->
                    Camino.celdasAfectadasEnElArea(
                        mapa!!, celdaObjetivo, it, it1
                    )
                }
            }
            objetivos = celdasObj?.let { getAfectadosZona(lanzador, it, EH.afectados, celdaObjetivo) }!!
            return objetivos
        }

        private fun getAfectadosZona(
            lanzador: Luchador, celdasObj: ArrayList<Celda>, afectados: Int,
            celdaObjetivo: Short
        ): ArrayList<Luchador> {
            val objetivos = ArrayList<Luchador>()
            for (C in celdasObj) {
                if (C == null) {
                    continue
                }
                val luchTemp = C.primerLuchador ?: continue
                // no afecta a los aliados
                if (afectados >= 0) {
                    if (afectados and 1 != 0 && luchTemp.equipoBin == lanzador.equipoBin) {
                        continue
                    }
                    // no afecta al lanzador
                    if (afectados and 2 != 0 && luchTemp.id == lanzador.id) {
                        continue
                    }
                    // no afecta a los enemigos
                    if (afectados and 4 != 0 && luchTemp.equipoBin != lanzador.equipoBin) {
                        continue
                    }
                    // no afecta a los combatientes (solamente invocaciones)
                    if (afectados and 8 != 0 && !luchTemp.esInvocacion()) {
                        continue
                    }
                    // No afecta a las invocaciones
                    if (afectados and 16 != 0 && luchTemp.esInvocacion()) {
                        continue
                    }
                    // 32 y 64 son de agregar si o si, respectivamente lanzador e invocador
                    if (afectados == 32 && luchTemp.id != lanzador.id) {
                        continue
                    }
                    if (afectados == 64 && lanzador.invocador != null && lanzador.invocador!!.id != luchTemp
                            .id
                    ) {
                        continue
                    }
                    // no afecta a la casilla donde se lanza
                    if (afectados and 128 != 0 && celdaObjetivo == luchTemp.celdaPelea!!.id) {
                        continue
                    }
                    if (afectados and 256 != 0 && luchTemp.esInvocacion()) {
                        continue
                    }
                    // de aqui pasa el siguiente filtro
                }
                if (!objetivos.contains(luchTemp)) {
                    objetivos.add(luchTemp)
                }
            }
            // agrega si o si al lanzador
            if (afectados >= 0) {
                if (afectados and 32 != 0) {
                    if (!objetivos.contains(lanzador)) {
                        objetivos.add(lanzador)
                    }
                }
                // agrega si o si al invocador
                if (afectados and 64 != 0) {
                    val invocador = lanzador.invocador
                    if (invocador != null && !objetivos.contains(invocador)) {
                        objetivos.add(invocador)
                    }
                }
            }
            return objetivos
        }

        //
        fun analizarHechizoStats(hechizoID: Int, grado: Int, str: String?): StatHechizo {
            val stat = convertirStringArray(str!!)
            val efectosNormales = stat[0].replace("\"", "")
            val efectosCriticos = stat[1].replace("\"", "")
            val costePA = stat[2].toByte()
            val alcMin = stat[3].toByte()
            val alcMax = stat[4].toByte()
            val probGC = stat[5].toShort()
            val probFC = stat[6].toShort()
            val lanzarLinea = stat[7].equals("true", ignoreCase = true)
            val lineaVista = stat[8].equals("true", ignoreCase = true)
            val celdaVacia = stat[9].equals("true", ignoreCase = true)
            val alcanceModificable = stat[10].equals("true", ignoreCase = true)
            val tipoHechizo = stat[11].toByte()
            val maxPorTurno = stat[12].toByte()
            val maxPorObjetivo = stat[13].toByte()
            val sigLanzamiento = stat[14].toByte()
            val areaAfectados = stat[15].replace("\"", "")
            val estadosNecesarios = stat[16]
            val estadosProhibidos = stat[17]
            val nivelReq = stat[18].toInt()
            val finTurnoSiFC = stat[19].equals("true", ignoreCase = true)
            val necesitaObjetivo = stat.size >= 21 && stat[20].equals("true", ignoreCase = true)
            val stats = StatHechizo(
                hechizoID,
                grado,
                costePA,
                alcMin,
                alcMax,
                probGC,
                probFC,
                lanzarLinea,
                lineaVista,
                celdaVacia,
                alcanceModificable,
                maxPorTurno,
                maxPorObjetivo,
                sigLanzamiento,
                nivelReq,
                finTurnoSiFC,
                estadosProhibidos,
                estadosNecesarios,
                tipoHechizo,
                necesitaObjetivo
            )
            stats.analizarEfectos(efectosNormales, efectosCriticos, areaAfectados, hechizoID)
            return stats
        }
    }

    init {
        this.valorIA = valorIA
    }
}