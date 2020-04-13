package variables.zotros

import estaticos.Mundo.Duo

class Almanax(val id: Int, val tipo: Int, val bonus: Int, ofrenda: String) {
    val ofrenda: Duo<Int, Int>

    init {
        val idObjeto = ofrenda.split(",".toRegex()).toTypedArray()[0].toInt()
        val cantidad = ofrenda.split(",".toRegex()).toTypedArray()[1].toInt()
        this.ofrenda = Duo(idObjeto, cantidad)
    }
}