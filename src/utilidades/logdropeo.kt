package utilidades

import variables.objeto.Objeto
import variables.pelea.Luchador

class logdropeo(val luchador: Luchador) {
    val personaje = if (luchador.esInvocacion()) luchador.invocador?.personaje else luchador.personaje
    val listaObj = arrayListOf<objetos>()

    class objetos(objeto: Objeto) {
        var cantidad = objeto.cantidad
        val modelo = objeto.objModelo?.id ?: -1
        val nombre = objeto.objModelo?.nombre ?: ""
    }

    fun addlogdrop(objeto: Objeto) {
        val obj1 = objetos(objeto)
        for (obj2 in listaObj) {
            if (obj2.modelo == obj1.modelo) {
                obj2.cantidad += obj1.cantidad
                return
            }
        }
        listaObj.add(obj1)
        return
    }

    fun imprimirlog() {
        if (personaje?.detalleExp != true) {
            return
        }
        personaje.enviarmensajeNegro("Su listado de drop en esta pelea es:")
        val msj = StringBuilder()
        listaObj.sortedBy { it.nombre }.forEach { msj.append("[${it.nombre}]x${it.cantidad}\n") }
        personaje.enviarmensajeNegro(msj.toString())
    }
}