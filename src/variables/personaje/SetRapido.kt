package variables.personaje

import estaticos.Constantes

class SetRapido(val id: Int, val nombre: String, private val _icono: Int, data: String) {
    val objetos = IntArray(18)
    fun actualizarObjetos(oldID: Int, newID: Int, oldPos: Byte, newPos: Byte): Boolean {
        var b = false
        for (i in objetos.indices) {
            if ((oldPos == Constantes.OBJETO_POS_NO_EQUIPADO || oldPos == i.toByte()) && objetos[i] == oldID) {
                if (newPos != i.toByte()) {
                    b = true
                    objetos[i] = newID
                }
            }
        }
        return b
    }

    val string: String
        get() {
            val data = StringBuilder()
            for (i in objetos.indices) {
                if (objetos[i] <= 0) {
                    continue
                }
                if (data.length > 0) {
                    data.append(";")
                }
                data.append(objetos[i]).append(",").append(i)
            }
            return id.toString() + "|" + nombre + "|" + _icono + "|" + data.toString()
        }

    init {
        for (s in data.split(";".toRegex()).toTypedArray()) {
            if (s.isEmpty()) {
                continue
            }
            val idObjeto = s.split(",".toRegex()).toTypedArray()[0].toInt()
            val posObjeto = s.split(",".toRegex()).toTypedArray()[1].toInt()
            try {
                objetos[posObjeto] = idObjeto
            } catch (ignored: Exception) {
            }
        }
    }
}