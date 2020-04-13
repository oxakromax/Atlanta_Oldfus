package variables.mision

import estaticos.Mundo.getEtapa
import estaticos.Mundo.getMision
import estaticos.Mundo.getMisionObjetivoModelo
import variables.personaje.Personaje
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class Mision(val iDModelo: Int, estado: Int, etapaID: Int, nivelEtapa: Int, objetivosString: String) {
    val objetivos = ConcurrentHashMap<Int, Int>()
    var etapaID = 0
        private set
    var nivelEtapa = 0
        private set
    var estadoMision = ESTADO_NO_TIENE
        private set

    fun estaCompletada(): Boolean {
        return estadoMision == ESTADO_COMPLETADO
    }

    fun confirmarEtapaActual(perso: Personaje, preConfirma: Boolean): Boolean {
        var p = false
        try {
            val copia: Map<Int, Int> = TreeMap(objetivos)
            for ((key, value) in copia) {
                if (value == ESTADO_INCOMPLETO) {
                    val mObjMod = getMisionObjetivoModelo(key)
                    perso.confirmarObjetivo(this, mObjMod, perso, null, preConfirma, 0)
                }
            }
            p = verificaSiCumplioEtapa()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return p
    }

    fun setObjetivoCompletado(objetivoID: Int) {
        if (objetivos[objetivoID] != null) {
            objetivos[objetivoID] = ESTADO_COMPLETADO
        }
    }

    private fun setNuevosObjetivos() {
        objetivos.clear()
        val etapa = getEtapa(etapaID) ?: return
        for (objMod in etapa.getObjetivosPorNivel(nivelEtapa)!!.values) {
            objetivos[objMod.iD] = ESTADO_INCOMPLETO
        }
    }

    fun verificaSiCumplioEtapa(): Boolean {
        var cumplioLosObjetivos = true
        for (estado in objetivos.values) {
            if (estado == ESTADO_INCOMPLETO) {
                cumplioLosObjetivos = false
                break
            }
        }
        return cumplioLosObjetivos && !verificaSiHayOtroNivelEtapa()
    }

    private fun verificaSiHayOtroNivelEtapa(): Boolean {
        val etapa = getEtapa(etapaID)
        val obj = etapa!!.getObjetivosPorNivel(nivelEtapa + 1) ?: return false
        nivelEtapa++
        setNuevosObjetivos()
        return true
    }

    fun verificaFinalizoMision(): Boolean {
        val sigEtapa = getMision(iDModelo)!!.siguienteEtapa(etapaID)
        etapaID = sigEtapa
        if (sigEtapa == -1) {
            estadoMision = ESTADO_COMPLETADO
            return true
        }
        nivelEtapa = 0
        setNuevosObjetivos()
        return false
    }

    companion object {
        const val ESTADO_COMPLETADO = 1
        const val ESTADO_INCOMPLETO = 2
        const val ESTADO_NO_TIENE = 0
    }

    init {
        estadoMision = estado
        if (!estaCompletada()) {
            this.etapaID = etapaID
            this.nivelEtapa = nivelEtapa
            setNuevosObjetivos()
            if (!objetivosString.isEmpty()) {
                for (str in objetivosString.split(";".toRegex()).toTypedArray()) {
                    try {
                        if (str.isEmpty()) {
                            continue
                        }
                        val duo = str.split(",".toRegex()).toTypedArray()
                        val objetivoID = duo[0].toInt()
                        val estadoObj = duo[1].toInt()
                        if (getMisionObjetivoModelo(objetivoID) == null) {
                            continue
                        }
                        objetivos.put(objetivoID, estadoObj)
                    } catch (ignored: Exception) {
                    }
                }
            }
        }
    }
}