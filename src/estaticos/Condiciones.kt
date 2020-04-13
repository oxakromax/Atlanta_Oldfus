package estaticos

import com.singularsys.jep.Jep
import variables.mision.Mision
import variables.personaje.Personaje
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.regex.Pattern
import kotlin.math.max

object Condiciones {
    fun validaCondiciones(perso: Personaje?, condiciones: String?): Boolean {
        var condiciones = condiciones
        try {
            if (perso == null) {
                return false
            }
            if (condiciones == null || condiciones.isEmpty() || condiciones == "BN" || condiciones == "-1"
                || condiciones.equals("ALL", ignoreCase = true)
            ) {
                return true
            }
            val jep = Jep()
            for (s in splittear(condiciones!!)) {
                try {
                    if (s.isEmpty()) {
                        continue
                    }
                    when (val cond = s.substring(0, 2)) {
                        "-1", "PX", "MK", "AO"// puedeAprenderOficio(condiciones, perso);
                            , "PN"// nombre
                            , "Pz", "DF"// drop fijo
                        -> condiciones = condiciones.replaceFirst(Pattern.quote(s).toRegex(), "true")
                        "BI" -> condiciones = condiciones.replaceFirst(Pattern.quote(s).toRegex(), "false")
                        "Pj" -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), tieneOficio(s, perso))
                        "PJ" -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), tieneNivelOficio(s, perso))
                        "OR", "DH" -> condiciones = condiciones.replaceFirst(
                            Pattern.quote(s).toRegex(),
                            tieneObjetoRecibidoDespuesDeHoras(s, perso)
                        )
                        "DM" -> condiciones = condiciones.replaceFirst(
                            Pattern.quote(s).toRegex(),
                            tieneObjetoRecibidoDespuesDeMinutos(s, perso)
                        )
                        "DS" -> condiciones = condiciones.replaceFirst(
                            Pattern.quote(s).toRegex(),
                            tieneObjetoRecibidoDespuesDeSegundos(s, perso)
                        )
                        "TE" -> condiciones = condiciones.replaceFirst(Pattern.quote(s).toRegex(), tieneEtapa(s, perso))
                        "TO" -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), tieneEstadoObjetivo(s, perso))
                        "TM" -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), tieneEstadoMision(s, perso))
                        "QO" -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), confirmarObjetivoMision(s, perso))
                        "QE" -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), confirmarEtapaMision(s, perso))
                        "SO" -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), tieneStatObjetoLlaveoAlma(s, perso))
                        "So" -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), tieneStatObjeto(s, perso))
                        "PO" -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), tieneObjModeloNoEquip(s, perso))
                        "EQ" -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), tieneObjModeloEquipado(s, perso))
                        "Pg"// don
                        -> condiciones = condiciones.replaceFirst(Pattern.quote(s).toRegex(), tieneDon(s, perso))
                        "XO" -> condiciones = condiciones.replaceFirst(
                            Pattern.quote(s).toRegex(),
                            celdasOcupadasPersonajesOtroMapa(s, perso)
                        )
                        "CO"// celda ocupada personaje
                        -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), celdasOcupadasPersonajes(s, perso))
                        "Co"// celda ocupada mob
                        -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), celdasOcupadasMob(s, perso))
                        "Cr" -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), celdasObjetoTirado(s, perso))
                        "PH" -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), tieneHechizo(s, perso))
                        "FM" -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), tieneMobsEnPelea(s, perso))
                        "MM" -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), tieneMobsEnMapa(s, perso))
                        "Is" -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), tieneObjetosSet(s, perso))
                        "IS" -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), tieneCantObjetosSet(s, perso))
                        "BS" -> condiciones =
                            condiciones.replaceFirst(Pattern.quote(s).toRegex(), tieneCantBonusSet(s, perso))
                        // ------------------------
                        // ------------------------
                        // DE AQUI PARA ABAJO SON ADD VARIABLE
                        // ------------------------
                        // ------------------------
                        "CI", "CV", "CA", "CW", "CC", "CS", "CM", "CP" -> {
                            val totalStas = perso.totalStats
                            jep.addVariable(
                                "CI",
                                totalStas.getTotalStatParaMostrar(Constantes.STAT_MAS_INTELIGENCIA).toDouble()
                            )
                            jep.addVariable(
                                "CV",
                                totalStas.getTotalStatParaMostrar(Constantes.STAT_MAS_VITALIDAD).toDouble()
                            )
                            jep.addVariable(
                                "CA",
                                totalStas.getTotalStatParaMostrar(Constantes.STAT_MAS_AGILIDAD).toDouble()
                            )
                            jep.addVariable(
                                "CW",
                                totalStas.getTotalStatParaMostrar(Constantes.STAT_MAS_SABIDURIA).toDouble()
                            )
                            jep.addVariable(
                                "CC",
                                totalStas.getTotalStatParaMostrar(Constantes.STAT_MAS_SUERTE).toDouble()
                            )
                            jep.addVariable(
                                "CS",
                                totalStas.getTotalStatParaMostrar(Constantes.STAT_MAS_FUERZA).toDouble()
                            )
                            jep.addVariable("CM", totalStas.getTotalStatParaMostrar(Constantes.STAT_MAS_PM).toDouble())
                            jep.addVariable("CP", totalStas.getTotalStatParaMostrar(Constantes.STAT_MAS_PA).toDouble())
                        }
                        "Ci", "Cv", "Ca", "Cw", "Cc", "Cs" -> {
                            val statsBase = perso.totalStats.statsBase
                            if (statsBase != null) {
                                jep.addVariable(
                                    "Ci",
                                    statsBase.getStatParaMostrar(Constantes.STAT_MAS_INTELIGENCIA).toDouble()
                                )
                            }
                            if (statsBase != null) {
                                jep.addVariable(
                                    "Cv",
                                    statsBase.getStatParaMostrar(Constantes.STAT_MAS_VITALIDAD).toDouble()
                                )
                            }
                            if (statsBase != null) {
                                jep.addVariable(
                                    "Ca",
                                    statsBase.getStatParaMostrar(Constantes.STAT_MAS_AGILIDAD).toDouble()
                                )
                            }
                            if (statsBase != null) {
                                jep.addVariable(
                                    "Cw",
                                    statsBase.getStatParaMostrar(Constantes.STAT_MAS_SABIDURIA).toDouble()
                                )
                            }
                            if (statsBase != null) {
                                jep.addVariable(
                                    "Cc",
                                    statsBase.getStatParaMostrar(Constantes.STAT_MAS_SUERTE).toDouble()
                                )
                            }
                            if (statsBase != null) {
                                jep.addVariable(
                                    "Cs",
                                    statsBase.getStatParaMostrar(Constantes.STAT_MAS_FUERZA).toDouble()
                                )
                            }
                        }
                        "PQ" -> jep.addVariable(cond, perso.preguntaID.toDouble())
                        "PG" -> jep.addVariable(cond, perso.getClaseID(true).toDouble())
                        "PD" -> jep.addVariable(cond, perso.deshonor.toDouble())
                        "PK" -> jep.addVariable(cond, perso.kamas.toDouble())
                        "PF" -> jep.addVariable(
                            cond,
                            (if (perso.pelea == null) -1 else perso.pelea.tipoPelea).toDouble()
                        )
                        "FP" -> jep.addVariable(
                            cond,
                            (if (perso.pelea == null) -1 else perso.pelea.prospeccionEquipo).toDouble()
                        )
                        "Fp" -> jep.addVariable(
                            cond, (if (perso.pelea == null)
                                -1
                            else
                                perso.pelea.getLuchadorPorID(perso.Id)!!.prospeccionLuchador).toDouble()
                        )
                        "PL" -> jep.addVariable(cond, perso.nivel.toDouble())
                        "PP" -> jep.addVariable(cond, perso.gradoAlineacion.toDouble())
                        "Ps" -> jep.addVariable(cond, perso.alineacion.toDouble())
                        "Pa" -> jep.addVariable(cond, perso.ordenNivel.toDouble())
                        "Pr" -> jep.addVariable(cond, perso.especialidad.toDouble())
                        "PS" -> jep.addVariable(cond, perso.sexo.toDouble())
                        "PW" -> jep.addVariable(cond, perso.alasActivadas())
                        "PM" -> jep.addVariable(cond, perso.getGfxID(false).toDouble())
                        "PR" -> jep.addVariable(cond, perso.esposoID != 0)
                        "PC" -> jep.addVariable(cond, perso.puedeCasarse())
                        "PZ" -> jep.addVariable(cond, (if (perso.esAbonado()) 1 else 0).toDouble())
                        "PV" -> jep.addVariable(cond, perso.esAbonado())
                        "GL" -> jep.addVariable(cond, perso.nivelGremio.toDouble())
                        "MA" -> jep.addVariable(cond, perso.realizoMisionDelDia())
                        "Mi" -> jep.addVariable(cond, perso.Id.toDouble())
                        "NR" -> jep.addVariable(cond, perso.resets.toDouble())
                        "mK" -> jep.addVariable(cond, perso.mapa.id.toDouble())
                        "mC" -> jep.addVariable(cond, perso.celda.id.toDouble())
                        "Tc" -> jep.addVariable(
                            cond,
                            ((System.currentTimeMillis() - perso.celda.ultimoUsoTrigger) / 1000).toDouble()
                        )
                    }
                } catch (e: Exception) {
                    AtlantaMain.redactarLogServidorln(
                        "EXCEPTION condicion: $s validaCondiones(splittear) " + e
                            .toString()
                    )
                    e.printStackTrace()
                }

            }
            condiciones = condiciones.replace("&", "&&").replace("=", "==").replace("|", "||").replace("!", "!=")
            jep.parse(condiciones)
            // System.out.println("jep condition: " + jep.rootNodeToString());
            val resultado = jep.evaluate()
            var ok = false
            if (resultado != null) {
                ok = java.lang.Boolean.valueOf(resultado.toString())
            }
            return ok
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln(
                "EXCEPTION Condiciones: $condiciones, validaCondiciones" + e
                    .toString()
            )
            e.printStackTrace()
            return false
        }

    }

    private fun splittear(cond: String): Array<String> {
        return cond.replace("[ ()]".toRegex(), "").split("[|&]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        // FIXME los corchetes en un split, quiere decir q cada simbolo sera un split para el string
    }

    private fun tieneObjetosSet(s: String, perso: Personaje?): String {
        try {
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val setID = Integer.parseInt(args[0])
            val cant = Integer.parseInt(args[1])
            val tiene = perso!!.getNroObjEquipadosDeSet(setID)
            return tiene.toString() + "" + s[2] + "" + cant
        } catch (ignored: Exception) {
        }

        return "false"
    }

    private fun tieneCantBonusSet(s: String, perso: Personaje?): String {
        try {
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val cant = Integer.parseInt(args[0])
            val map = TreeMap<Int, Int>()
            for (pos in Constantes.POSICIONES_EQUIPAMIENTO) {
                val obj = perso!!.getObjPosicion(pos) ?: continue
                val setID = obj.objModelo?.setID ?: continue
                if (setID < 1) {
                    continue
                }
                var v = 1
                if (map.containsKey(setID)) {
                    v = map[setID]!! + 1
                }
                map[setID] = v
            }
            var tiene = 0
            for (v in map.values) {
                tiene += v - 1
            }
            return tiene.toString() + "" + s[2] + "" + cant
        } catch (ignored: Exception) {
        }

        return "false"
    }

    private fun tieneCantObjetosSet(s: String, perso: Personaje?): String {
        try {
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val cant = Integer.parseInt(args[0])
            val map = TreeMap<Int, Int>()
            for (pos in Constantes.POSICIONES_EQUIPAMIENTO) {
                val obj = perso!!.getObjPosicion(pos) ?: continue
                val setID = obj.objModelo?.setID ?: continue
                if (setID < 1) {
                    continue
                }
                var v = 1
                if (map.containsKey(setID)) {
                    v = map[setID]!! + 1
                }
                map[setID] = v
            }
            var tiene = 0
            for (v in map.values) {
                if (v > tiene) {
                    tiene = v
                }
            }
            return tiene.toString() + "" + s[2] + "" + cant
        } catch (ignored: Exception) {
        }

        return "false"
    }

    private fun tieneMobsEnMapa(s: String, perso: Personaje?): String {
        var b = false
        try {
            val ss = s.substring(3).split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val args = ss[1].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val mobID = Integer.parseInt(args[0])
            var lvlMin = 0
            var lvlMax = 99999
            try {
                if (args.size > 1) {
                    lvlMin = Integer.parseInt(args[1])
                    lvlMax = Integer.parseInt(args[2])
                }
            } catch (ignored: Exception) {
            }

            for (m in ss[0].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                if (m.isEmpty()) {
                    continue
                }
                try {
                    val mapa = Mundo.getMapa(java.lang.Short.parseShort(m)) ?: continue
                    for (gm in mapa.grupoMobsTotales!!.values) {
                        if (gm.tieneMobModeloID(mobID, lvlMin, lvlMax)) {
                            b = true
                            break
                        }
                    }
                } catch (ignored: Exception) {
                }

            }
            if (s.contains("!")) {
                b = !b
            }
        } catch (ignored: Exception) {
        }

        return b.toString() + ""
    }

    private fun tieneMobsEnPelea(s: String, perso: Personaje?): String {
        var b = false
        try {
            if (perso!!.pelea != null) {
                val mobs = CopyOnWriteArrayList(perso.pelea.mobGrupo!!.mobs)
                val ss = s.substring(3).split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (a in ss) {
                    val args = a.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val mobID = Integer.parseInt(args[0])
                    var lvlMin = 0
                    var lvlMax = 99999
                    try {
                        if (args.size > 1) {
                            lvlMin = Integer.parseInt(args[1])
                            lvlMax = Integer.parseInt(args[2])
                        }
                    } catch (ignored: Exception) {
                    }

                    var tiene = false
                    for (gm in mobs) {
                        if (gm.idModelo == mobID) {
                            if (gm.nivel in lvlMin..lvlMax) {
                                mobs.remove(gm)
                                tiene = true
                                b = true
                                break
                            }
                        }
                    }
                    if (!tiene) {
                        b = false
                        break
                    }
                    if (s.contains("!")) {
                        b = false
                    }
                }
            }
        } catch (ignored: Exception) {
        }

        return b.toString() + ""
    }

    private fun celdasObjetoTirado(s: String, perso: Personaje?): String {
        var b = false
        try {
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val id = Integer.parseInt(args[0])
            val objetoID = Integer.parseInt(args[1])
            val obj = perso!!.mapa.getCelda(id.toShort())!!.objetoTirado
            if (obj != null) {
                b = objetoID == obj.objModeloID
            }
            if (s.contains("!")) {
                b = !b
            }
        } catch (ignored: Exception) {
        }

        return b.toString() + ""
    }

    private fun celdasOcupadasPersonajesOtroMapa(s: String, perso: Personaje?): String {
        var b = false
        try {
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val mapaID = Integer.parseInt(args[0])
            val celdaID = Integer.parseInt(args[1])
            var clase: Byte = -1
            if (args.size > 2) {
                clase = java.lang.Byte.parseByte(args[2])
            }
            var sexo: Byte = -1
            if (args.size > 2) {
                sexo = java.lang.Byte.parseByte(args[3])
            }
            val p = Mundo.getMapa(mapaID.toShort())?.getCelda(celdaID.toShort())!!.primerPersonaje
            b = p != null
            if (b && sexo.toInt() != -1) {
                b = p!!.sexo == sexo
            }
            if (b && clase.toInt() != -1) {
                b = p!!.getClaseID(true) == clase
            }
            if (s.contains("!")) {
                b = !b
            }
        } catch (ignored: Exception) {
        }

        return b.toString() + ""
    }

    private fun celdasOcupadasPersonajes(s: String, perso: Personaje?): String {
        var b = false
        try {
            val id = Integer.parseInt(s.substring(3))
            b = perso!!.mapa.getCelda(id.toShort())!!.primerPersonaje != null
            if (s.contains("!")) {
                b = !b
            }
        } catch (ignored: Exception) {
        }

        return b.toString() + ""
    }

    private fun celdasOcupadasMob(s: String, perso: Personaje?): String {
        var b = false
        try {
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val celdaID = Integer.parseInt(args[0])
            val mobID = Integer.parseInt(args[1])
            for (gm in perso!!.mapa.grupoMobsTotales!!.values) {
                if (gm.celdaID.toInt() == celdaID) {
                    if (gm.tieneMobModeloID(mobID, 0, 99999)) {
                        b = true
                        break
                    }
                }
            }
            if (s.contains("!")) {
                b = !b
            }
        } catch (ignored: Exception) {
        }

        return b.toString() + ""
    }

    private fun tieneStatObjeto(s: String, perso: Personaje?): String {
        var b = false
        try {
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val objetoID = Integer.parseInt(args[0])
            val statVerificar = Integer.parseInt(args[1])
            for (obj in perso!!.objetosTodos) {
                if (obj.objModeloID != objetoID) {
                    continue
                }
                val stats =
                    obj.convertirStatsAString(true).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (st in stats) {
                    val statID =
                        Integer.parseInt(st.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0], 16)
                    if (statID == statVerificar) {
                        b = true
                    }
                }
                if (b) {
                    break
                }
            }
            if (s.contains("!")) {
                b = !b
            }
        } catch (ignored: Exception) {
        }

        return b.toString() + ""
    }

    private fun tieneStatObjetoLlaveoAlma(s: String, perso: Personaje?): String {
        var b = false
        try {
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val objetoID = Integer.parseInt(args[0])
            val solicitaID = Integer.parseInt(args[1])
            var statVerificar = Constantes.STAT_LLAVE_MAZMORRA
            loop@ for (obj in perso!!.objetosTodos) {
                if (objetoID == 7010) {
                    when (obj.objModeloID) {
                        7010, 9720, 10417, 10418 -> statVerificar = Constantes.STAT_INVOCA_MOB
                        else -> continue@loop
                    }
                } else if (obj.objModeloID != objetoID) {
                    continue
                }
                val stats =
                    obj.convertirStatsAString(true).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (st in stats) {
                    val statID =
                        Integer.parseInt(st.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0], 16)
                    val tSolicitaID =
                        Integer.parseInt(st.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[3], 16)
                    if (statID != statVerificar) {
                        continue
                    }
                    if (tSolicitaID == solicitaID) {
                        b = true
                    }
                }
                if (b) {
                    break
                }
            }
            if (s.contains("!")) {
                b = !b
            }
        } catch (ignored: Exception) {
        }

        return b.toString() + ""
    }

    private fun tieneObjModeloNoEquip(s: String, perso: Personaje?): String {
        var b = false
        try {
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val id = Integer.parseInt(args[0])
            var cant = 1
            try {
                cant = Integer.parseInt(args[1])
            } catch (ignored: Exception) {
            }

            cant = max(1, cant)
            b = perso!!.getObjModeloNoEquipado(id, cant) != null
            if (s.contains("!")) {
                b = !b
            }
        } catch (ignored: Exception) {
        }

        return b.toString() + ""
    }

    private fun tieneObjModeloEquipado(s: String, perso: Personaje?): String {
        var b = false
        try {
            val id = Integer.parseInt(s.substring(3))
            b = perso!!.tieneObjModeloEquipado(id)
            if (s.contains("!")) {
                b = !b
            }
        } catch (ignored: Exception) {
        }

        return b.toString() + ""
    }

    private fun tieneHechizo(s: String, perso: Personaje?): String {
        var b = false
        try {
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val id = Integer.parseInt(args[0])
            b = perso!!.tieneHechizoID(id)
            if (s.contains("!")) {
                b = !b
            }
        } catch (ignored: Exception) {
        }

        return b.toString() + ""
    }

    private fun tieneDon(s: String, perso: Personaje?): String {
        var b = false
        try {
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val id = Integer.parseInt(args[0])
            var nivel = 1
            try {
                nivel = Integer.parseInt(args[1])
            } catch (ignored: Exception) {
            }

            nivel = max(1, nivel)
            b = perso!!.tieneDon(id, nivel)
            if (s.contains("!")) {
                b = !b
            }
        } catch (ignored: Exception) {
        }

        return b.toString() + ""
    }

    private fun tieneObjetoRecibidoDespuesDeHoras(s: String, perso: Personaje?): String {
        try {
            var dHoras: Long = -1
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val objetoID = Integer.parseInt(args[0])
            val horas = max(0, Integer.parseInt(args[1]))
            for (obj in perso!!.objetosTodos) {
                if (obj == null) {
                    continue
                }
                if (obj.objModeloID != objetoID) {
                    continue
                }
                if (!obj.tieneStatTexto(Constantes.STAT_RECIBIDO_EL)) {
                    continue
                }
                val tHoras = obj.getDiferenciaTiempo(Constantes.STAT_RECIBIDO_EL, 60 * 60 * 1000)
                if (tHoras > dHoras) {
                    dHoras = tHoras
                }
            }
            if (dHoras > -1) {
                return dHoras.toString() + "" + s[2] + "" + horas
            }
        } catch (ignored: Exception) {
        }

        return "false"
    }

    private fun tieneObjetoRecibidoDespuesDeMinutos(s: String, perso: Personaje?): String {
        try {
            var dMinutos: Long = -1
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val objetoID = Integer.parseInt(args[0])
            val minutos = max(0, Integer.parseInt(args[1]))
            for (obj in perso!!.objetosTodos) {
                if (obj == null) {
                    continue
                }
                if (obj.objModeloID != objetoID) {
                    continue
                }
                if (!obj.tieneStatTexto(Constantes.STAT_RECIBIDO_EL)) {
                    continue
                }
                val tMinutos = obj.getDiferenciaTiempo(Constantes.STAT_RECIBIDO_EL, 60 * 1000)
                if (tMinutos > dMinutos) {
                    dMinutos = tMinutos
                }
            }
            if (dMinutos > -1) {
                return dMinutos.toString() + "" + s[2] + "" + minutos
            }
        } catch (ignored: Exception) {
        }

        return "false"
    }

    private fun tieneObjetoRecibidoDespuesDeSegundos(s: String, perso: Personaje?): String {
        try {
            var dSegundos: Long = -1
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val objetoID = Integer.parseInt(args[0])
            val segundos = max(0, Integer.parseInt(args[1]))
            for (obj in perso!!.objetosTodos) {
                if (obj == null) {
                    continue
                }
                if (obj.objModeloID != objetoID) {
                    continue
                }
                if (!obj.tieneStatTexto(Constantes.STAT_RECIBIDO_EL)) {
                    continue
                }
                val tSegundos = obj.getDiferenciaTiempo(Constantes.STAT_RECIBIDO_EL, 1000)
                if (tSegundos > dSegundos) {
                    dSegundos = tSegundos
                }
            }
            if (dSegundos > -1) {
                return dSegundos.toString() + "" + s[2] + "" + segundos
            }
        } catch (ignored: Exception) {
        }

        return "false"
    }

    private fun tieneNivelOficio(s: String, perso: Personaje?): String {
        try {
            val a = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return if (a.size > 1) {
                perso!!.getNivelStatOficio(Integer.parseInt(a[0])).toString() + "" + s[2] + "" + a[1]
            } else {
                (perso!!.getStatOficioPorID(Integer.parseInt(a[0])) != null).toString() + ""
            }
        } catch (ignored: Exception) {
        }

        return "false"
    }

    private fun tieneOficio(s: String, perso: Personaje?): String {
        var b = false
        try {
            val id = Integer.parseInt(s.substring(3))
            b = perso!!.getStatOficioPorID(id) != null
            if (s.contains("!")) {
                b = !b
            }
        } catch (ignored: Exception) {
        }

        return b.toString() + ""
    }

    private fun tieneEtapa(s: String, perso: Personaje?): String {
        var b = false
        try {
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val id = Integer.parseInt(args[0])
            b = perso!!.tieneEtapa(id)
            if (s.contains("!")) {
                b = !b
            }
        } catch (ignored: Exception) {
        }

        return b.toString() + ""
    }

    private fun tieneEstadoObjetivo(s: String, perso: Personaje?): String {
        var b = false
        try {
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val id = Integer.parseInt(args[0])
            var realizado = Mision.ESTADO_NO_TIENE
            try {
                realizado = Integer.parseInt(args[1])
                b = perso!!.getEstadoObjetivo(id).toInt() == realizado
            } catch (e: Exception) {
                b = perso!!.getEstadoObjetivo(id).toInt() != realizado
            }

            if (s.contains("!")) {
                b = !b
            }
        } catch (ignored: Exception) {
        }

        return b.toString() + ""
    }

    private fun tieneEstadoMision(s: String, perso: Personaje?): String {
        var b = false
        try {
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val id = Integer.parseInt(args[0])
            var realizado = Mision.ESTADO_NO_TIENE
            try {
                realizado = Integer.parseInt(args[1])
                b = perso!!.getEstadoMision(id) == realizado
            } catch (e: Exception) {
                b = perso!!.getEstadoMision(id) != realizado
            }

            if (s.contains("!")) {
                b = !b
            }
        } catch (ignored: Exception) {
        }

        return b.toString() + ""
    }

    private fun confirmarObjetivoMision(s: String, perso: Personaje?): String {
        var b = false
        try {
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val id = Integer.parseInt(args[0])
            var preConfirma = true
            try {
                preConfirma = args[1] != "1"
            } catch (ignored: Exception) {
            }

            b = Mundo.getMisionObjetivoModelo(id)!!.confirmar(perso!!, null, preConfirma, 0)
            if (s.contains("!")) {
                b = !b
            }
        } catch (ignored: Exception) {
        }

        return b.toString() + ""
    }

    private fun confirmarEtapaMision(s: String, perso: Personaje?): String {
        var b = false
        try {
            val args = s.substring(3).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val id = Integer.parseInt(args[0])
            var preConfirma = true
            try {
                preConfirma = args[1] != "1"
            } catch (ignored: Exception) {
            }

            b = perso!!.confirmarEtapa(id, preConfirma)
            if (s.contains("!")) {
                b = !b
            }
        } catch (ignored: Exception) {
        }

        return b.toString() + ""
    }
}
