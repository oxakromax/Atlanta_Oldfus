package variables.pelea

import estaticos.Mundo
import kotlin.math.max
import kotlin.math.min

class DropMob {
    val IDObjModelo: Int
    val prospeccion: Int
    val maximo: Int
    val porcentaje: Float
    private val _esDropFijo: Boolean
    var nivelMin: Int = 0
    var nivelMax: Int = 0
    var condicion = ""

    val string: String
        get() = (this.toString() + " [_objModeloID=" + IDObjModelo + " " + Mundo.getObjetoModelo(IDObjModelo)!!.nombre
                + ", _prospeccion=" + prospeccion + ", _maximo=" + maximo + ", _nivelMin=" + nivelMin + ", _nivelMax="
                + nivelMax + ", _porcentaje=" + porcentaje + ", _condicion=" + condicion + ", _esEtereo=" + _esDropFijo + "]")

    constructor(
        objeto: Int, prospeccion: Int, porcentaje: Float, max: Int,
        condicion: String
    ) {
        IDObjModelo = objeto
        this.prospeccion = prospeccion
        this.porcentaje = max(0.001, min(100f, porcentaje).toDouble()).toFloat()
        maximo = max(max, 1)
        this.condicion = condicion
        _esDropFijo = false
    }

    constructor(objeto: Int, porcentaje: Float, nivelMin: Int, nivelMax: Int) {
        IDObjModelo = objeto
        prospeccion = 1
        this.porcentaje = max(0.001, min(100f, porcentaje).toDouble()).toFloat()
        this.nivelMin = nivelMin
        this.nivelMax = nivelMax
        maximo = 1
        _esDropFijo = true
    }

    fun esDropFijo(): Boolean {
        return _esDropFijo
    }

    fun esIdentico(d: DropMob): Boolean {
        if (d._esDropFijo != _esDropFijo) {
            return false
        }
        if (d.condicion != condicion) {
            return false
        }
        if (d.porcentaje != porcentaje) {
            return false
        }
        if (d.prospeccion != prospeccion) {
            return false
        }
        if (d.IDObjModelo != IDObjModelo) {
            return false
        }
        return if (d.nivelMax != nivelMax) {
            false
        } else d.nivelMin == nivelMin
    }
}
