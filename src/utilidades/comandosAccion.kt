package utilidades

import variables.personaje.Personaje
import variables.zotros.Accion

class comandosAccion(
    val id: Int,
    val comando: String,
    val accionID: Int,
    val arg: String,
    val condicion: String,
    val activo: Boolean
) {
    fun realizarAccion(personaje: Personaje?): Boolean {
        return if (personaje == null || !activo) {
            false
        } else {
            Accion.realizar_Accion_Estatico(accionID, arg, personaje, null, -1, -1)
            true
        }
    }
}