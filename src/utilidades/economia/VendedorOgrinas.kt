package utilidades.economia

import estaticos.Mundo

class VendedorOgrinas(val id: Int, val id_perso: Int, var interes: Double, var ogrinas: Int) {
    val personaje = Mundo.getPersonaje(id_perso)
    val interesInt = (interes * 100).toInt()
    fun guardar() {
// Nuuunca termine esta mierda
    }

    fun eliminar() {

    }

    fun insertar() {

    }
}