package variables.oficio

import java.util.*
import java.util.regex.Pattern

class Oficio(val id: Int, herramientas: String, recetas: String) {
    private val _herramientas = ArrayList<Int>()
    private val _recetas: MutableMap<Int, ArrayList<Int>> = TreeMap()
    fun listaRecetaPorTrabajo(trabajo: Int): ArrayList<Int>? {
        return _recetas[trabajo]
    }

    fun puedeReceta(trabajo: Int, modelo: Int): Boolean {
        if (_recetas[trabajo] != null) {
            for (a in _recetas[trabajo]!!) {
                if (a == modelo) {
                    return false
                }
            }
        }
        return true
    }

    fun esHerramientaValida(idObjModelo: Int): Boolean {
        return _herramientas.isEmpty() || _herramientas.contains(idObjModelo)
    }

    init {
        if (herramientas.isNotEmpty()) {
            for (str in herramientas.split(",".toRegex()).toTypedArray()) {
                try {
                    _herramientas.add(str.toInt())
                } catch (ignored: Exception) {
                }
            }
        }
        if (recetas.isNotEmpty()) {
            for (str in recetas.split(Pattern.quote("|").toRegex()).toTypedArray()) {
                try {
                    val trabajoID = str.split(";".toRegex()).toTypedArray()[0].toInt()
                    val list = ArrayList<Int>()
                    for (str2 in str.split(";".toRegex()).toTypedArray()[1].split(",".toRegex()).toTypedArray()) {
                        list.add(str2.toInt())
                    }
                    _recetas[trabajoID] = list
                } catch (ignored: Exception) {
                }
            }
        }
    }
}