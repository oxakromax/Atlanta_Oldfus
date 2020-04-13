package variables.mercadillo

import estaticos.GestorSalida.ENVIAR_EHM_MOVER_OBJMERCA_POR_MODELO
import variables.personaje.Personaje
import java.util.*

class TipoObjetos(val tipoObjetoID: Int) {
    private val _modelosDeUnTipo: MutableMap<Int, ModeloMercadillo> =
        HashMap()

    fun addModeloVerificacion(objMerca: ObjetoMercadillo) {
        val modeloID = objMerca.objeto.objModeloID
        val modelo = _modelosDeUnTipo[modeloID]
        if (modelo == null) {
            _modelosDeUnTipo[modeloID] = ModeloMercadillo(modeloID, objMerca)
        } else {
            modelo.addObjMercaConLinea(objMerca)
        }
    }

    fun borrarObjMercaDeModelo(objMerca: ObjetoMercadillo, perso: Personaje?, puesto: Mercadillo): Boolean {
        val idModelo = objMerca.objeto.objModeloID
        val borrable = _modelosDeUnTipo[idModelo]!!.borrarObjMercaDeUnaLinea(objMerca, perso, puesto)
        if (_modelosDeUnTipo[idModelo]!!.estaVacio()) {
            _modelosDeUnTipo.remove(idModelo)
            ENVIAR_EHM_MOVER_OBJMERCA_POR_MODELO(perso!!, "-", idModelo.toString() + "")
        }
        return borrable
    }

    fun getModelo(modeloID: Int): ModeloMercadillo? {
        return _modelosDeUnTipo[modeloID]
    }

    // public ArrayList<ObjetoMercadillo> todoListaObjMercaDeUnTipo() {
// final ArrayList<ObjetoMercadillo> listaObjMerca = new ArrayList<ObjetoMercadillo>();
// for (final ModeloMercadillo modelo : _modelosDeUnTipo.values()) {
// listaObjMerca.addAll(modelo.todosObjMercaDeUnModelo());
// }
// return listaObjMerca;
// }
    fun stringModelo(): String {
        val str = StringBuilder()
        for (idModelo in _modelosDeUnTipo.keys) {
            if (str.length > 0) {
                str.append(";")
            }
            str.append(idModelo)
        }
        return str.toString()
    }

}