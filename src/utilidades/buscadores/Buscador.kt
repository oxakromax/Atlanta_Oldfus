package utilidades.buscadores

import estaticos.Mundo
import variables.mob.MobModelo
import variables.objeto.ObjetoModelo
import variables.objeto.ObjetoSet

object Buscador {
    fun buscarEnNombre(NombreObjeto: String, palabra: String): Boolean {
        var PC = NombreObjeto.toLowerCase().split(" ")
        if (Comparador.contienePalabra(NombreObjeto.toLowerCase(), palabra.toLowerCase())) {
            return true
        }
        for (n in PC) {
            if (Comparador.compararPalabrasPorcentaje(
                    palabra.toLowerCase(),
                    n
                ) > 85
            ) {
                return true
            }
        }
        return false
    }

    fun buscarSets(Nombre: String): ArrayList<ObjetoSet> {
        val ListaSets = ArrayList<ObjetoSet>()
        for (set in Mundo.OBJETOS_SETS.values) {
            if (buscarEnNombre(set.nombre, Nombre)) {
                ListaSets.add(set)
            }
        }
        return ListaSets
    }

    fun buscarItems(Nombre: String): ArrayList<ObjetoModelo> {
        val ListaObjetos = ArrayList<ObjetoModelo>()
        for (obj in Mundo.OBJETOS_MODELOS.values) {
            if (buscarEnNombre(obj.nombre, Nombre)) {
                ListaObjetos.add(obj)
            }
        }
        return ListaObjetos
    }

    fun buscarMobs(Nombre: String): ArrayList<MobModelo> {
        val Lista = ArrayList<MobModelo>()
        for (obj in Mundo.MOBS_MODELOS.values) {
            if (buscarEnNombre(obj.nombre, Nombre)) {
                Lista.add(obj)
            }
        }
        return Lista
    }
}