package variables.mercadillo

import variables.objeto.Objeto

class ObjetoMercadillo(
    val precio: Long,
    private val _tipoCantidad: Int,
    val cuentaID: Int,
    val objeto: Objeto,
    val mercadilloID: Int
) :
    Comparable<ObjetoMercadillo> {
    var lineaID = 0

    fun getTipoCantidad(cantidadReal: Boolean): Int {
        return if (cantidadReal) {
            (Math.pow(10.0, _tipoCantidad.toDouble()) / 10.0).toInt()
        } else _tipoCantidad
    }

    val objetoID: Int
        get() = objeto.id

    fun analizarParaEL(): String {
        return objeto.id.toString() + ";" + getTipoCantidad(true) + ";" + objeto.objModeloID + ";" + objeto
            .convertirStatsAString(false) + ";" + precio + ";350"
    }

    fun analizarParaEmK(): String {
        return objeto.id.toString() + "|" + getTipoCantidad(true) + "|" + objeto.objModeloID + "|" + objeto
            .convertirStatsAString(false) + "|" + precio + "|350"
    }

    fun analizarObjeto(separador: Char): String {
        return lineaID.toString() + separador.toInt() + getTipoCantidad(true) + separador + objeto.objModeloID + separador + objeto
            .convertirStatsAString(false) + separador + precio + separador + "350"
    }

    override fun compareTo(objMercadillo: ObjetoMercadillo): Int {
        val otroPrecio = objMercadillo.precio
        if (otroPrecio > precio) {
            return -1
        }
        return if (otroPrecio == precio) {
            0
        } else 1
    }

}