package utilidades.buscadores

import java.util.*

object Comparador {
    const val a = "áàä"
    const val e = "éàë"
    const val i = "íìï"
    const val o = "öòó"
    const val u = "úùü"
    const val n = "ñ"
    var s1: String = ""
    var s2: String = ""
    private var b1 = false
    private var b2 = false
    private val mapPalabras: MutableMap<String, String> = TreeMap()
    fun equalsignore(str1: String, str2: String): Boolean {
        return str1.equals(str2, ignoreCase = true)
    }

    fun remplazarAcentos(str1: String): String {
        val array = str1.toCharArray()
        var strfinal = ""
        for (x in array) {
            when {
                a.contains(x) -> {
                    strfinal += "a"
                }
                e.contains(x) -> {
                    strfinal += "e"
                }
                i.contains(x) -> {
                    strfinal += "i"
                }
                o.contains(x) -> {
                    strfinal += "o"
                }
                u.contains(x) -> {
                    strfinal += "u"
                }
                n.contains(x) -> {
                    strfinal += "n"
                }
                else -> {
                    strfinal += x
                }
            }
        }
        return strfinal
    }

    fun compararPalabrasPorcentaje(str1: String, str2: String): Float {
        val ld = LevenshteinDistance()
        b1 = false
        b2 = false
        s1 = ""
        s2 = ""
        try {
            if (mapPalabras[str1]!!.length > 1) {
                s1 = mapPalabras[str1].toString()
                b1 = true
            }
            if (mapPalabras[str2]!!.length > 1) {
                s2 = mapPalabras[str2].toString()
                b2 = true
            }
        } catch (e: Exception) {
        }
        return if (b1 && b2) {
            ld.setWords(
                s1,
                s2
            )
            ld.afinidad * 100
        } else {
            s1 =
                remplazarAcentos(str1)
            s2 =
                remplazarAcentos(str2)
            ld.setWords(
                s1,
                s2
            )
            mapPalabras[str1] =
                s1
            mapPalabras[str2] =
                s2
            ld.afinidad * 100
        }
    }

    fun contienePalabra(str1: String, str2: String): Boolean {
        if (str2.isEmpty()) {
            return false
        }
        b1 = false
        b2 = false
        s1 = ""
        s2 = ""
        try {
            if (mapPalabras[str1]!!.length > 1) {
                s1 = mapPalabras[str1].toString()
                b1 = true
            }
            if (mapPalabras[str2]!!.length > 1) {
                s2 = mapPalabras[str2].toString()
                b2 = true
            }
        } catch (e: Exception) {
        }
        return if (b1 && b2) {
            s1.contains(s2) or s2.contains(
                s1
            )
        } else {
            s1 = remplazarAcentos(
                str1
            ).toLowerCase()
            s2 = remplazarAcentos(
                str2
            ).toLowerCase()
            mapPalabras[str1] =
                s1
            mapPalabras[str2] =
                s2
            s1.contains(s2) or s2.contains(
                s1
            )
        }
    }
}