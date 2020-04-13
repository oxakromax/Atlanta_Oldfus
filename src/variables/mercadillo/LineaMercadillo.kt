package variables.mercadillo

import variables.objeto.Objeto
import java.util.*

class LineaMercadillo(private val _lineaID: Int, objMerca: ObjetoMercadillo) {
    private val _modeloID: Int
    private val _categoriasDeUnaLinea =
        ArrayList<ArrayList<ObjetoMercadillo>>(
            3
        )
    private val _strStats: String
    fun tieneIgual(objMerca: ObjetoMercadillo): Boolean {
        if (!lineaVacia() && !tieneMismoStats(objMerca.objeto)) {
            return false
        }
        objMerca.lineaID = _lineaID
        val categoria = objMerca.getTipoCantidad(false) - 1
        _categoriasDeUnaLinea[categoria].add(objMerca)
        ordenar(categoria)
        return true
    }

    private fun tieneMismoStats(objeto: Objeto): Boolean {
        return _strStats.equals(objeto.convertirStatsAString(false), ignoreCase = true)
    }

    fun tuTienes(categoria: Int, precio: Long): ObjetoMercadillo? {
        val index = categoria - 1
        for (i in _categoriasDeUnaLinea[index].indices) {
            if (_categoriasDeUnaLinea[index][i].precio == precio) {
                return _categoriasDeUnaLinea[index][i]
            }
        }
        return null
    }

    val los3PreciosPorLinea: LongArray
        get() {
            val str = LongArray(3)
            for (i in _categoriasDeUnaLinea.indices) {
                try {
                    str[i] = _categoriasDeUnaLinea[i][0].precio
                } catch (e: IndexOutOfBoundsException) {
                    str[i] = 0
                }
            }
            return str
        }

    //	public ArrayList<ObjetoMercadillo> todosObjMercaDeUnaLinea() {
//		final int totalEntradas = _categoriasDeUnaLinea.get(0).size() + _categoriasDeUnaLinea.get(1).size()
//		+ _categoriasDeUnaLinea.get(2).size();
//		final ArrayList<ObjetoMercadillo> todosObjMerca = new ArrayList<ObjetoMercadillo>(totalEntradas);
//		for (int cat = 0; cat < _categoriasDeUnaLinea.size(); cat++) {
//			todosObjMerca.addAll(_categoriasDeUnaLinea.get(cat));
//		}
//		return todosObjMerca;
//	}
    fun borrarObjMercaDeLinea(objMerca: ObjetoMercadillo): Boolean {
        val categoria = objMerca.getTipoCantidad(false) - 1 // 1, 10 ,100
        val borrable = _categoriasDeUnaLinea[categoria].remove(objMerca)
        ordenar(categoria)
        return borrable
    }

    fun strListaDeLineasDeModelo(): String {
        val precio = los3PreciosPorLinea
        return (_lineaID.toString() + ";" + _strStats + ";" + (if (precio[0] == 0.toLong()) "" else precio[0]) + ";"
                + (if (precio[1] == 0.toLong()) "" else precio[1]) + ";" + if (precio[2] == 0.toLong()) "" else precio[2])
    }

    fun str3PrecioPorLinea(): String {
        val precio = los3PreciosPorLinea
        return ("$_lineaID|$_modeloID|$_strStats|" + (if (precio[0] == 0.toLong()) "" else precio[0]) + "|"
                + (if (precio[1] == 0.toLong()) "" else precio[1]) + "|" + if (precio[2] == 0.toLong()) "" else precio[2])
    }

    fun ordenar(categoria: Int) {
        Collections.sort(_categoriasDeUnaLinea[categoria])
    }

    fun lineaVacia(): Boolean {
        var i = 0
        while (i < 3) {
            // 3 categorias
            try {
                if (_categoriasDeUnaLinea[i][0] != null) {
                    return false
                }
            } catch (ignored: Exception) {
            }
            i++
        }
        return true
    }

    init {
        val objeto = objMerca.objeto
        _strStats = objeto.convertirStatsAString(false)
        _modeloID = objeto.objModeloID
        for (i in 0..2) {
            _categoriasDeUnaLinea.add(ArrayList())
        }
        val categoria = objMerca.getTipoCantidad(false) - 1
        _categoriasDeUnaLinea[categoria].add(objMerca)
        ordenar(categoria)
        objMerca.lineaID = _lineaID
    }
}