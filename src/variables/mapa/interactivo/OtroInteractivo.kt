package variables.mapa.interactivo

import variables.zotros.Accion

class OtroInteractivo(
    val gfxID: Int, val mapaID: Short, val celdaID: Short, var accionID: Int, val args: String,
    val condicion: String, val tiempoRecarga: Int
) {
    val accion: Accion = Accion(accionID, args, "")
}