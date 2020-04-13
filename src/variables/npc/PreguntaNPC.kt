package variables.npc

import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Condiciones.validaCondiciones
import estaticos.Mundo
import sprites.Preguntador
import variables.personaje.Personaje
import java.util.*

class PreguntaNPC(val id: Int, respuestas: String, var params: String, alternos: String) {
    val respuestas = ArrayList<Int>()
    private val _pregCondicionales: MutableMap<String, Int> = TreeMap()
    var strAlternos: String? = null
        private set

    fun setPreguntasCondicionales(alternos: String) {
        strAlternos = alternos
        _pregCondicionales.clear()
        val alt =
            alternos.replace("],\\[".toRegex(), "¬").replace("[\\[\\]]".toRegex(), "").split("¬".toRegex())
                .toTypedArray()
        for (s in alt) {
            try {
                val split = s.split(";".toRegex()).toTypedArray()
                _pregCondicionales[split[0]] = split[1].toInt()
            } catch (ignored: Exception) {
            }
        }
    }

    val strRespuestas: String
        get() {
            val str = StringBuilder()
            for (i in respuestas) {
                if (str.length > 0) {
                    str.append(";")
                }
                str.append(i)
            }
            return str.toString()
        }

    fun setRespuestas(respuestasnuevas: String) {
        respuestas.clear()
        for (s in respuestasnuevas.replace(";", ",").split(",".toRegex()).toTypedArray()) {
            try {
                respuestas.add(s.toInt())
            } catch (ignored: Exception) {
            }
        }
    }

    fun stringArgParaDialogo(perso: Personaje, preguntador: Preguntador): String {
        val str = StringBuilder(id.toString() + "")
        try {
            for ((key, value) in _pregCondicionales) {
                if (value == id || value <= 0) {
                    continue
                }
                if (validaCondiciones(perso, key)) {
                    if (Mundo.getPreguntaNPC(value) == null) {
                        Mundo.addPreguntaNPC(PreguntaNPC(value, "", "", ""))
                    }
                    return Mundo.getPreguntaNPC(value)!!.stringArgParaDialogo(perso, preguntador)
                }
            }
            str.append(preguntador.getArgsDialogo(params))
            var b = true
            for (i in respuestas) {
                if (i <= 0) {
                    continue
                }
                var respuesta = Mundo.getRespuestaNPC(i)
                if (respuesta == null) {
                    respuesta = RespuestaNPC(i)
                    Mundo.addRespuestaNPC(respuesta)
                }
                val cond = respuesta.condicion
                if (!validaCondiciones(perso, cond)) {
                    continue
                }
                if (b) {
                    str.append("|")
                } else {
                    str.append(";")
                }
                b = false
                str.append(i)
            }
            perso.preguntaID = id
        } catch (e: Exception) {
            redactarLogServidorln("Hay un error en el NPC Pregunta " + id)
        }
        return str.toString()
    }

    init {
        setRespuestas(respuestas)
        setPreguntasCondicionales(alternos)
    }
}