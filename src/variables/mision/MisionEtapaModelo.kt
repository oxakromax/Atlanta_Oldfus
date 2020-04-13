package variables.mision

import estaticos.AtlantaMain
import estaticos.Constantes
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.GestorSalida.ENVIAR_Ow_PODS_DEL_PJ
import estaticos.Mundo.getMisionObjetivoModelo
import estaticos.Mundo.getObjetoModelo
import variables.objeto.ObjetoModelo.CAPACIDAD_STATS
import variables.personaje.Personaje
import variables.zotros.Accion
import java.util.*
import java.util.regex.Pattern

class MisionEtapaModelo(
    val iD: Int,
    recompensas: String,
    objetivos: String,
    val nombre: String
) {
    private val _recompensas = arrayOfNulls<String>(7)
    private val _objetivos =
        ArrayList<TreeMap<Int, MisionObjetivoModelo>>()
    private var _recompensa: String? = null
    private var _strObjetivos = ""
    fun getRecompensa(perso: Personaje): String {
        var recompensas: StringBuilder
        recompensas = StringBuilder(_recompensa)
        val recompensaslista = arrayOfNulls<String>(7)
        var i: Byte = 0
        for (str in recompensas.toString().split(Pattern.quote("|").toRegex()).toTypedArray()) {
            try {
                if (!str.equals("null", ignoreCase = true)) {
                    recompensaslista[i.toInt()] = str
                }
            } catch (ignored: Exception) {
            }
            i++
        }
        for (g in 0..1) {
            try {
                if (recompensaslista[g] != null) {
                    when (g) {
                        0 -> if (AtlantaMain.RATE_XP_PVM > 1 && !perso.cuenta.esAbonado()) {
                            recompensaslista[0] =
                                (Objects.requireNonNull(recompensaslista[0])!!.toInt() * (AtlantaMain.RATE_XP_PVM * 3)).toString()
                        } else if (AtlantaMain.RATE_XP_PVM > 1 && perso.cuenta.esAbonado()) {
                            recompensaslista[0] =
                                (Objects.requireNonNull(recompensaslista[0])!!.toInt() * (AtlantaMain.RATE_XP_PVM * 3) * AtlantaMain.RATE_XP_PVM_ABONADOS).toString()
                        }
                        1 -> if (AtlantaMain.RATE_KAMAS > 1 && !perso.cuenta.esAbonado()) {
                            recompensaslista[1] =
                                (recompensaslista[1]!!.toInt() * AtlantaMain.RATE_KAMAS).toString()
                        } else if (AtlantaMain.RATE_KAMAS > 1 && perso.cuenta.esAbonado()) {
                            recompensaslista[1] =
                                (recompensaslista[1]!!.toInt() * AtlantaMain.RATE_KAMAS * AtlantaMain.RATE_KAMAS_ABONADOS).toString()
                        }
                    }
                }
            } catch (ignored: Exception) {
            }
        }
        recompensas = StringBuilder()
        for (g in 0..6) {
            if (g != 6) {
                recompensas.append(recompensaslista[g]).append("|")
            } else {
                recompensas.append(recompensaslista[g])
            }
        }
        return recompensas.toString()
    }

    fun setObjetivos(objetivos: String) {
        _objetivos.clear()
        _strObjetivos = objetivos
        for (s in objetivos.split(Pattern.quote("|").toRegex()).toTypedArray()) {
            val map = TreeMap<Int, MisionObjetivoModelo>()
            for (str in s.split(",".toRegex()).toTypedArray()) {
                try {
                    val idObjetivo = str.toInt()
                    val objetivo = getMisionObjetivoModelo(idObjetivo)
                    if (objetivo != null) {
                        map[idObjetivo] = objetivo
                    }
                } catch (ignored: Exception) {
                }
            }
            _objetivos.add(map)
        }
    }

    fun strObjetivos(): String {
        return _strObjetivos
    }

    fun setRecompensa(recompensas: String) {
        _recompensa = recompensas
        var i: Byte = 0
        for (str in recompensas.split(Pattern.quote("|").toRegex()).toTypedArray()) {
            try {
                if (!str.equals("null", ignoreCase = true)) {
                    _recompensas[i.toInt()] = str
                }
            } catch (ignored: Exception) {
            }
            i++
        }
    }

    fun getObjetivosPorNivel(nivel: Int): TreeMap<Int, MisionObjetivoModelo>? {
        return if (_objetivos.size <= nivel) {
            null
        } else _objetivos[nivel]
    }

    // XP, KAMAS, ITEMS, EMOTES, OFICIOS, HECHIZO = 1000|5000|
// 311,15;9336,5;....etc|8,9,....|51,52,....|145,966,....
    fun darRecompensa(perso: Personaje) {
        for (i in 0..6) {
            try {
                if (_recompensas[i] != null) {
                    when (i) {
                        0 -> if (AtlantaMain.RATE_XP_PVM > 1 && !perso.cuenta.esAbonado()) {
                            perso.addExperiencia(
                                (_recompensas[0]!!.toInt() * (AtlantaMain.RATE_XP_PVM * 3)).toLong(),
                                true
                            )
                        } else if (AtlantaMain.RATE_XP_PVM > 1 && perso.cuenta.esAbonado()) {
                            perso.addExperiencia(
                                (_recompensas[0]!!.toInt() * (AtlantaMain.RATE_XP_PVM * 3) * AtlantaMain.RATE_XP_PVM_ABONADOS).toLong(),
                                true
                            )
                        } else {
                            perso.addExperiencia(
                                Objects.requireNonNull(_recompensas[0])!!.toInt().toLong(),
                                true
                            )
                        }
                        1 -> if (AtlantaMain.RATE_KAMAS > 1 && !perso.cuenta.esAbonado()) {
                            perso.addKamas(_recompensas[1]!!.toInt() * AtlantaMain.RATE_KAMAS.toLong(), true, true)
                        } else if (AtlantaMain.RATE_KAMAS > 1 && perso.cuenta.esAbonado()) {
                            perso.addKamas(
                                (_recompensas[1]!!.toInt() * AtlantaMain.RATE_KAMAS * AtlantaMain.RATE_KAMAS_ABONADOS).toLong(),
                                true,
                                true
                            )
                        } else {
                            perso.addKamas(_recompensas[1]!!.toInt().toLong(), true, true)
                        }
                        2 -> {
                            for (str in _recompensas[2]!!.split(";".toRegex()).toTypedArray()) {
                                if (str.isEmpty()) {
                                    continue
                                }
                                try {
                                    val id = str.split(",".toRegex()).toTypedArray()[0].toInt()
                                    val cant = str.split(",".toRegex()).toTypedArray()[1].toInt()
                                    perso.addObjIdentAInventario(
                                        getObjetoModelo(id)!!.crearObjeto(
                                            cant,
                                            Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM
                                        ), false
                                    )
                                    ENVIAR_Im_INFORMACION(perso, "021;$cant~$id")
                                } catch (ignored: Exception) {
                                }
                            }
                            ENVIAR_Ow_PODS_DEL_PJ(perso)
                        }
                        3 -> for (str in _recompensas[3]!!.split(",".toRegex()).toTypedArray()) {
                            try {
                                if (str.isEmpty()) {
                                    continue
                                }
                                Accion(65, str, "").realizarAccion(perso, perso, -1, (-1).toShort())
                            } catch (ignored: Exception) {
                            }
                        }
                        4 -> for (str in _recompensas[4]!!.split(",".toRegex()).toTypedArray()) {
                            try {
                                if (str.isEmpty()) {
                                    continue
                                }
                                Accion(6, str, "").realizarAccion(perso, perso, -1, (-1).toShort())
                            } catch (ignored: Exception) {
                            }
                        }
                        5 -> for (str in _recompensas[5]!!.split(",".toRegex()).toTypedArray()) {
                            try {
                                if (str.isEmpty()) {
                                    continue
                                }
                                Accion(9, str, "").realizarAccion(perso, perso, -1, (-1).toShort())
                            } catch (ignored: Exception) {
                            }
                        }
                        6 -> for (str in _recompensas[6]!!.split(Pattern.quote("*").toRegex()).toTypedArray()) {
                            try {
                                if (str.isEmpty()) {
                                    continue
                                }
                                val accion = str.split("@".toRegex()).toTypedArray()[0].toInt()
                                var arg = ""
                                try {
                                    arg = str.split("@".toRegex()).toTypedArray()[1]
                                } catch (ignored: Exception) {
                                }
                                Accion(accion, arg, "").realizarAccion(perso, perso, -1, (-1).toShort())
                            } catch (ignored: Exception) {
                            }
                        }
                    }
                }
            } catch (ignored: Exception) {
            }
        }
    }

    init {
        setRecompensa(recompensas)
        setObjetivos(objetivos)
    }
}