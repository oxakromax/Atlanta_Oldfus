package variables.mercadillo

import estaticos.GestorSalida.ENVIAR_EHm_DETALLE_LINEA_CON_PRECIOS
import estaticos.Mundo.sigIDLineaMercadillo
import variables.personaje.Personaje
import java.util.*

class ModeloMercadillo(private val _modeloID: Int, objMerca: ObjetoMercadillo?) {
    private val _lineasDeUnModelo: MutableMap<Int, LineaMercadillo> = HashMap()
    fun addObjMercaConLinea(objMerca: ObjetoMercadillo?) {
        for (linea in _lineasDeUnModelo.values) {
            if (linea.tieneIgual(objMerca!!)) {
                return
            }
        }
        val lineaID = sigIDLineaMercadillo()
        _lineasDeUnModelo[lineaID] = LineaMercadillo(lineaID, objMerca!!)
    }

    fun getLinea(lineaID: Int): LineaMercadillo? {
        return _lineasDeUnModelo[lineaID]
    }

    fun borrarObjMercaDeUnaLinea(
        objMerca: ObjetoMercadillo, perso: Personaje?,
        puesto: Mercadillo
    ): Boolean {
        val lineaID = objMerca.lineaID
        val borrable = _lineasDeUnModelo[lineaID]!!.borrarObjMercaDeLinea(objMerca)
        if (_lineasDeUnModelo[lineaID]!!.lineaVacia()) {
            puesto.borrarPath(lineaID)
            _lineasDeUnModelo.remove(lineaID)
            ENVIAR_EHm_DETALLE_LINEA_CON_PRECIOS(perso, "-", lineaID.toString() + "")
        } else {
            ENVIAR_EHm_DETALLE_LINEA_CON_PRECIOS(perso, "+", _lineasDeUnModelo[lineaID]!!.str3PrecioPorLinea())
        }
        return borrable
    }

    // public ArrayList<ObjetoMercadillo> todosObjMercaDeUnModelo() {
// final ArrayList<ObjetoMercadillo> listaObj = new ArrayList<ObjetoMercadillo>();
// for (final LineaMercadillo linea : _lineasDeUnModelo.values()) {
// listaObj.addAll(linea.todosObjMercaDeUnaLinea());
// }
// return listaObj;
// }
    fun strLineasPorObjMod(): String {
        val str = StringBuilder()
        for (linea in _lineasDeUnModelo.values) {
            if (str.length > 0) {
                str.append("|")
            }
            str.append(linea.strListaDeLineasDeModelo())
        }
        return "$_modeloID|$str"
    }

    fun estaVacio(): Boolean {
        return _lineasDeUnModelo.isEmpty()
    }

    init {
        addObjMercaConLinea(objMerca)
    }
}