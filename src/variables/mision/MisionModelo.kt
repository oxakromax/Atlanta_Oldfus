package variables.mision

import estaticos.Mundo.getNPCModelo
import java.util.*

class MisionModelo(
    val id: Int, etapas: String, nombre: String, pregDarMision: String,
    pregMisCumplida: String, pregMisIncompleta: String, puedeRepetirse: Boolean
) {
    val puedeRepetirse: Boolean
    val nombre: String
    val etapas = ArrayList<Int>()
    private val _preguntas = arrayOfNulls<MisionPregunta>(3)

    fun setPreguntas(pregunta: String, estado: Int) {
        try {
            val s = pregunta.split(";".toRegex()).toTypedArray()
            var npc = 0
            var pregID = 0
            var condicion = ""
            try {
                npc = s[0].toInt()
            } catch (ignored: Exception) {
            }
            try {
                pregID = s[1].toInt()
            } catch (ignored: Exception) {
            }
            try {
                condicion = s[2]
            } catch (ignored: Exception) {
            }
            _preguntas[estado] = MisionPregunta(pregID, npc, condicion)
            if (npc > 0) {
                getNPCModelo(npc)!!.addMision(this)
            }
        } catch (ignored: Exception) {
        }
    }

    fun getMisionPregunta(estado: Int): MisionPregunta? {
        return _preguntas[estado]
    }

    fun strMisionPregunta(estado: Int): String {
        val preg = _preguntas[estado] ?: return "null"
        var str = preg.nPCID.toString() + ";" + preg.preguntaID
        if (!preg.condicion.isEmpty()) {
            str += ";" + preg.condicion
        }
        return str
    }

    fun setEtapas(etapasString: String) {
        etapas.clear()
        for (str in etapasString.split(",".toRegex()).toTypedArray()) {
            try {
                etapas.add(str.toInt())
            } catch (ignored: Exception) {
            }
        }
    }

    fun strEtapas(): String {
        val s = StringBuilder()
        for (i in etapas) {
            if (s.length > 0) {
                s.append(",")
            }
            s.append(i)
        }
        return s.toString()
    }

    fun siguienteEtapa(id: Int): Int {
        return try {
            etapas[etapas.indexOf(id) + 1]
        } catch (e: Exception) {
            -1
        }
    }

    init {
        setEtapas(etapas)
        this.nombre = nombre
        setPreguntas(pregDarMision, Mision.Companion.ESTADO_NO_TIENE)
        setPreguntas(pregMisCumplida, Mision.Companion.ESTADO_COMPLETADO)
        setPreguntas(pregMisIncompleta, Mision.Companion.ESTADO_INCOMPLETO)
        this.puedeRepetirse = puedeRepetirse
    }
}