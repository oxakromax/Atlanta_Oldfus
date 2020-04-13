package sprites

import variables.mapa.Celda
import variables.mapa.Mapa
import variables.pelea.Pelea

interface IniciaPelea {
    val celda: Celda?
    val mapa: Mapa?
    val iD: Int
    fun setPelea(pelea: Pelea?)
}