package variables.hechizo

import variables.pelea.Luchador

class HechizoLanzado(lanzador: Luchador, sHechizo: StatHechizo, private val _idObjetivo: Int) {
    val hechizoID: Int
    var sigLanzamiento = 0
        private set

    fun actuSigLanzamiento() {
        sigLanzamiento -= 1
    }

    companion object {
        fun poderSigLanzamiento(lanzador: Luchador, idHechizo: Int): Int {
            for (HL in lanzador.hechizosLanzados) {
                if (HL.hechizoID == idHechizo && HL.sigLanzamiento > 0) {
                    return HL.sigLanzamiento
                }
            }
            return 0
        }

        fun getNroLanzamientos(lanzador: Luchador, idHechizo: Int): Int {
            var nro = 0
            for (HL in lanzador.hechizosLanzados) {
                if (HL.hechizoID == idHechizo) {
                    nro++
                }
            }
            return nro
        }

        fun getNroLanzPorObjetivo(lanzador: Luchador, idObjetivo: Int, idHechizo: Int): Int {
            var nro = 0
            if (idObjetivo != 0) {
                for (HL in lanzador.hechizosLanzados) {
                    if (HL.hechizoID == idHechizo && HL._idObjetivo == idObjetivo) {
                        nro++
                    }
                }
            }
            return nro
        }

        fun puedeLanzPorObjetivo(lanzador: Luchador, idObjetivo: Int, SH: StatHechizo): Boolean {
            if (SH.maxLanzPorObjetivo <= 0) {
                return false
            }
            var nro = 0
            if (idObjetivo != 0) {
                for (HL in lanzador.hechizosLanzados) {
                    if (HL.hechizoID == SH.hechizoID && HL._idObjetivo == idObjetivo) {
                        nro++
                    }
                }
            }
            return nro >= SH.maxLanzPorObjetivo // 0 < 1
        }
    }

    init {
        hechizoID = sHechizo.hechizoID
        if (lanzador.personaje != null && lanzador.personaje!!.tieneModfiSetClase(hechizoID)) {
            sigLanzamiento = sHechizo.sigLanzamiento - lanzador.personaje!!.getModifSetClase(hechizoID, 286)
        } else {
            sigLanzamiento = sHechizo.sigLanzamiento.toInt()
        }
    }
}