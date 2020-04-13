package variables.personaje

import variables.hechizo.Hechizo
import variables.hechizo.StatHechizo

class HechizoPersonaje(var posicion: Char, val hechizo: Hechizo?, var nivel: Int) {

    val statHechizo: StatHechizo?
        get() = if (hechizo == null) {
            null
        } else hechizo.getStatsPorNivel(nivel)

}