package variables.personaje

import estaticos.Constantes
import estaticos.GestorSalida.ENVIAR_BN_NADA
import estaticos.GestorSalida.ENVIAR_EiK_MOVER_OBJETO_TIENDA
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO
import estaticos.Mundo.addObjeto
import sprites.Exchanger
import variables.objeto.Objeto
import java.util.*

class Tienda : Exchanger {
    val objetos = ArrayList<Objeto>()
    fun addObjeto(objeto: Objeto) {
        if (objeto.id == 0) {
            addObjeto(objeto, false)
        }
        if (objetos.contains(objeto)) {
            return
        }
        objetos.add(objeto)
    }

    fun borrarObjeto(obj: Objeto?) {
        objetos.remove(obj)
    }

    operator fun contains(obj: Objeto?): Boolean {
        return objetos.contains(obj)
    }

    override fun addKamas(kamas: Long, perso: Personaje?) {}
    override val kamas: Long
        get() = 0L

    fun clear() {
        objetos.clear()
    }

    fun estaVacia(): Boolean {
        return objetos.isEmpty()
    }

    @Synchronized
    override fun addObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        if (!objetos.contains(objeto)) { // si no lo tiene en la tienda
            if (cantidad == 0) {
                ENVIAR_BN_NADA(perso)
                return
            }
            if (!perso.tieneObjetoID(objeto.id) || objeto.posicion != Constantes.OBJETO_POS_NO_EQUIPADO) {
                ENVIAR_Im_INFORMACION(perso, "1OBJECT_DONT_EXIST")
                return
            }
            if (objetos.size >= perso.nivel) {
                ENVIAR_Im_INFORMACION(perso, "166")
                return
            }
            if (cantidad > objeto.cantidad) {
                cantidad = objeto.cantidad
            }
            val nuevaCantidad = objeto.cantidad - cantidad
            if (nuevaCantidad >= 1) {
                val nuevoObj = objeto.clonarObjeto(nuevaCantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                perso.addObjetoConOAKO(nuevoObj, true)
                objeto.cantidad = cantidad
                ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objeto)
            }
            perso.borrarOEliminarConOR(objeto.id, false)
            objeto.precio = precio
            addObjeto(objeto)
        } else { // si lo tiene en la tienda
            cantidad = objeto.cantidad
            objeto.precio = precio
        }
        ENVIAR_EiK_MOVER_OBJETO_TIENDA(
            perso, '+', "", objeto.id.toString() + "|" + cantidad + "|" + objeto
                .objModeloID + "|" + objeto.convertirStatsAString(false) + "|" + precio
        )
    }

    @Synchronized
    override fun remObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
        println("cantidad $cantidad")
        if (objetos.remove(objeto)) {
            if (!perso.addObjIdentAInventario(objeto, true)) {
                objeto.precio = 0
            }
            ENVIAR_EiK_MOVER_OBJETO_TIENDA(perso, '-', "", objeto.id.toString() + "")
        }
    }

    override fun cerrar(perso: Personaje?, exito: String) {
        perso?.cerrarVentanaExchange(exito)
    }

    override fun botonOK(perso: Personaje) {}
    override fun getListaExchanger(perso: Personaje): String { // TODO Auto-generated method stub
        return ""
    }
}