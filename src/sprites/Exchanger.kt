package sprites

import variables.objeto.Objeto
import variables.personaje.Personaje

interface Exchanger {
    fun addKamas(kamas: Long, perso: Personaje?)
    val kamas: Long
    fun addObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int)
    fun remObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int)
    fun cerrar(perso: Personaje?, exito: String)
    fun botonOK(perso: Personaje)
    fun getListaExchanger(perso: Personaje): String
}