package variables.npc

import variables.personaje.Personaje
import variables.zotros.Accion
import java.util.*

class RespuestaNPC(val id: Int) {
    val acciones = ArrayList<Accion>()
    var condicion = ""
        private set

    //
// public void setCondicion(String cond) {
// _condicion = cond;
// }
    fun borrarAcciones() {
        acciones.clear()
        condicion = ""
    }

    fun addAccion(accion: Accion) {
        val c = ArrayList(acciones)
        var condicion: String? = accion.condicion
        if (condicion!!.isEmpty()) {
            condicion = null
        } else if (condicion == "BN") {
            condicion = ""
        }
        for (a in c) {
            if (a.id == accion.id) {
                acciones.remove(a)
            } else if (condicion != null) {
                a.condicion = condicion
            }
        }
        if (condicion != null) {
            accion.condicion = condicion
            this.condicion = condicion
        }
        acciones.add(accion)
    }

    fun aplicar(perso: Personaje) {
        perso.preguntaID = 0
        for (accion in acciones) {
            accion.realizarAccion(perso, null, -1, (-1).toShort())
        }
    }

}