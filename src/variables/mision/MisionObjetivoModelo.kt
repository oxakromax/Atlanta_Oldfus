package variables.mision

import estaticos.Constantes
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO
import variables.npc.NPC
import variables.objeto.Objeto
import variables.personaje.Personaje
import java.util.*
import java.util.regex.Pattern

class MisionObjetivoModelo(val iD: Int, val tipo: Byte, var args: String) {

    fun confirmar(
        perso: Personaje, mobs: Map<Int, Int>?, preConfirma: Boolean,
        idObjeto: Int
    ): Boolean { // preconfirma no borra nada solo confirma
        var b = false
        var npc: NPC? = null
        if (perso.conversandoCon < 0 && perso.conversandoCon > -100) {
            npc = perso.mapa.getNPC(perso.conversandoCon)
        }
        when (tipo) {
            NULL, 10.toByte(), 11.toByte(), 13.toByte() -> b = true
            VOLVER_VER_NPC, HABLAR_CON_NPC -> try {
                if (npc == null) {
                    return b
                }
                val args = argsPreparados()
                val idNPC = args[0].toInt()
                b = idNPC == npc.modeloID
                if (args.size >= 3) {
                    val x = args[1].split(":".toRegex()).toTypedArray()[1].toInt()
                    val y = args[2].split(":".toRegex()).toTypedArray()[1].toInt()
                    b = b and (perso.mapa.x.toInt() == x) and (perso.mapa.y.toInt() == y)
                }
            } catch (ignored: Exception) {
            }
            ENSEÑAR_OBJETO_NPC -> try {
                if (npc == null) {
                    return b
                }
                val args = argsPreparados()
                val req = args[0].split(",".toRegex()).toTypedArray()
                val idNPC = req[0].toInt()
                val idObjModelo = req[1].toInt()
                val cantObj = req[2].toInt()
                b = idNPC == npc.modeloID
                if (args.size >= 3) {
                    val x = args[1].split(":".toRegex()).toTypedArray()[1].toInt()
                    val y = args[2].split(":".toRegex()).toTypedArray()[1].toInt()
                    b = b and (perso.mapa.x.toInt() == x) and (perso.mapa.y.toInt() == y)
                }
                if (b) {
                    b = perso.tieneObjPorModYCant(idObjModelo, cantObj)
                }
            } catch (ignored: Exception) {
            }
            ENTREGAR_OBJETO_NPC -> try {
                if (npc == null) {
                    return b
                }
                val args = argsPreparados()
                val req = args[0].split(",".toRegex()).toTypedArray()
                val idNPC = req[0].toInt()
                val idObjModelo = req[1].toInt()
                val cantObj = req[2].toInt()
                b = idNPC == npc.modeloID
                if (args.size >= 3) {
                    val x = args[1].split(":".toRegex()).toTypedArray()[1].toInt()
                    val y = args[2].split(":".toRegex()).toTypedArray()[1].toInt()
                    b = b and (perso.mapa.x.toInt() == x) and (perso.mapa.y.toInt() == y)
                }
                if (b) {
                    if (preConfirma) {
                        b = perso.tieneObjPorModYCant(idObjModelo, cantObj)
                    } else if (perso.tenerYEliminarObjPorModYCant(idObjModelo, cantObj).also { b = it }) {
                        ENVIAR_Im_INFORMACION(perso, "022;$cantObj~$idObjModelo")
                    }
                }
            } catch (ignored: Exception) {
            }
            DESCUBRIR_MAPA -> try {
                b = args.toShort() == perso.mapa.id
            } catch (ignored: Exception) {
            }
            DESCUBRIR_ZONA -> try {
                b = args.toShort().toInt() == perso.mapa.subArea!!.area.id
            } catch (ignored: Exception) {
            }
            VENCER_AL_MOB, VENCER_MOBS_UN_COMBATE -> try {
                b = true
                val args = argsPreparados()
                val req = args[0].split(",".toRegex()).toTypedArray()
                var i = 0
                while (i < req.size) {
                    val idMob = req[i].toInt()
                    val cant = req[i + 1].toInt()
                    var t = false
                    if (mobs != null) {
                        for ((key, value) in mobs) {
                            if (key == idMob && value >= cant) {
                                t = true
                                break
                            }
                        }
                    }
                    b = b and t
                    i += 2
                }
                if (args.size >= 3) {
                    val x = args[1].split(":".toRegex()).toTypedArray()[1].toInt()
                    val y = args[2].split(":".toRegex()).toTypedArray()[1].toInt()
                    b = b and (perso.mapa.x.toInt() == x) and (perso.mapa.y.toInt() == y)
                }
            } catch (ignored: Exception) {
            }
            UTILIZAR_OBJETO -> try {
                val args =
                    args.replace("[", "").replace("]", "").replace(" ", "").split(",".toRegex()).toTypedArray()
                val idObj = args[0].toInt()
                b = idObj == idObjeto
            } catch (ignored: Exception) {
            }
            ENTREGAR_ALMAS_NPC -> {
                val args =
                    args.replace("[", "").replace("]", "").replace(" ", "").split(",".toRegex()).toTypedArray()
                val alma = Integer.toHexString(args[1].toInt())
                val cantidad = args[2].toInt()
                var van = 0
                val o = ArrayList<Objeto>()
                loop@ for (obj in perso.objetosTodos) {
                    if (van >= cantidad) {
                        break
                    }
                    when (obj.objModeloID) {
                        7010, 9720, 10417, 10418 -> {
                        }
                        else -> continue@loop
                    }
                    val stats =
                        obj.convertirStatsAString(true).split(",".toRegex()).toTypedArray()
                    var c = false
                    for (st in stats) {
                        try {
                            val statID = st.split("#".toRegex()).toTypedArray()[0].toInt(16)
                            if (statID != Constantes.STAT_INVOCA_MOB) {
                                continue
                            }
                            if (van >= cantidad) {
                                continue
                            }
                            if (st.split("#".toRegex()).toTypedArray()[3].equals(alma, ignoreCase = true)) {
                                van++
                                c = true
                            }
                        } catch (ignored: Exception) {
                        }
                    }
                    if (c) {
                        o.add(obj)
                    }
                }
                if (van >= cantidad) {
                    b = true
                    if (!preConfirma) {
                        van = 0
                        for (obj in o) {
                            val stats =
                                obj.convertirStatsAString(true).split(",".toRegex()).toTypedArray()
                            val nuevo = StringBuilder()
                            for (st in stats) {
                                if (nuevo.length > 0) {
                                    nuevo.append(",")
                                }
                                val statID = st.split("#".toRegex()).toTypedArray()[0].toInt(16)
                                if (statID == Constantes.STAT_INVOCA_MOB && st.split("#".toRegex())
                                        .toTypedArray()[3].equals(
                                        alma,
                                        ignoreCase = true
                                    ) && van < cantidad
                                ) {
                                    van++
                                } else {
                                    nuevo.append(st)
                                }
                            }
                            if (nuevo.length == 0) {
                                perso.borrarOEliminarConOR(obj.id, true)
                            } else {
                                obj.convertirStringAStats(nuevo.toString())
                                ENVIAR_OCK_ACTUALIZA_OBJETO(perso, obj)
                            }
                        }
                    }
                }
            }
        }
        return b
    }

    private fun argsPreparados(): Array<String> {
        return prepararArgs(args, ',', '|').replace(" ", "")
            .split(Pattern.quote("|").toRegex()).toTypedArray()
    }

    companion object {
        // public final static int SIN_CUMPLIR = 0, CUMPLIDO = 1;
        const val NULL: Byte = 0
        const val HABLAR_CON_NPC: Byte = 1
        const val ENSEÑAR_OBJETO_NPC: Byte = 2
        const val ENTREGAR_OBJETO_NPC: Byte = 3
        const val DESCUBRIR_MAPA: Byte = 4
        const val DESCUBRIR_ZONA: Byte = 5
        const val VENCER_MOBS_UN_COMBATE: Byte = 6
        const val VENCER_AL_MOB: Byte = 7
        const val UTILIZAR_OBJETO: Byte = 8
        const val VOLVER_VER_NPC: Byte = 9
        const val ENTREGAR_ALMAS_NPC: Byte = 12
        private fun prepararArgs(args: String, buscar: Char, reemplazar: Char): String {
            val s = StringBuilder()
            var corchetes = 0
            for (a in args.toCharArray()) {
                when (a) {
                    '[' -> {
                        corchetes++
                        if (corchetes > 1) {
                            s.append(a)
                        }
                    }
                    ']' -> {
                        if (corchetes > 1) {
                            s.append(a)
                        }
                        corchetes--
                    }
                    else -> if (corchetes == 0 && a == buscar) {
                        s.append(reemplazar)
                    } else {
                        s.append(a)
                    }
                }
            }
            return s.toString()
        }
    }

}