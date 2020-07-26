package variables.npc

import estaticos.Constantes
import estaticos.GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO
import estaticos.GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL
import estaticos.GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO
import estaticos.GestorSalida.ENVIAR_OR_ELIMINAR_OBJETO
import estaticos.Mundo
import estaticos.Mundo.Duo
import sprites.Exchanger
import variables.objeto.Objeto
import variables.objeto.ObjetoModelo.CAPACIDAD_STATS
import variables.personaje.Personaje
import java.util.*

class Trueque(private val _perso: Personaje, private val _resucitar: Boolean, npcID: Int) : Exchanger {
    private val _entregar = ArrayList<Duo<Int, Int>>()
    private val _objetosModelo: MutableMap<Int, Int> = HashMap()
    private var _dar: Map<Int, Int> = HashMap()
    private var _ok = false
    private var _polvo = false
    private var _idMascota = 0
    private var _npcID = 0

    @Synchronized
    override fun botonOK(perso: Personaje) {
        _ok = !_ok
        ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso, _ok, _perso.Id)
        if (_ok) {
            aplicar()
        }
    }

    @Synchronized
    override fun cerrar(perso: Personaje?, exito: String) {
        _perso.cerrarVentanaExchange(exito)
    }

    @Synchronized
    fun aplicar() {
        var mascota: Objeto? = null
        for (duo in _entregar) {
            val cant = duo._segundo
            if (cant == 0) {
                continue
            }
            val obj = _perso.getObjeto(duo._primero)
            if (obj != null) {
                val nuevaCant = obj.cantidad - cant
                if (_resucitar && _polvo && obj.objModelo?.tipo?.toInt() == Constantes.OBJETO_TIPO_FANTASMA_MASCOTA) {
                    ENVIAR_OR_ELIMINAR_OBJETO(_perso, duo._primero)
                    mascota = obj
                } else {
                    if (nuevaCant <= 0) {
                        _perso.borrarOEliminarConOR(duo._primero, true)
                    } else {
                        obj.cantidad = nuevaCant
                        ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso, obj)
                    }
                }
            }
        }
        if (mascota != null) {
            mascota.pDV = 1
            mascota.cantidad = 1
            mascota.setIDOjbModelo(Mundo.getMascotaPorFantasma(mascota.objModeloID))
            _perso.addObjetoConOAKO(mascota, true)
        } else if (!_dar.isEmpty()) {
            for ((idObjModelo, cantidad) in _dar) {
                try {
                    _perso.addObjIdentAInventario(
                        Mundo.getObjetoModelo(idObjModelo)?.crearObjeto(
                            cantidad,
                            Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM
                        ), false
                    )
                } catch (ignored: Exception) {
                }
            }
        }
        cerrar(_perso, "a")
    }

    @Synchronized
    override fun addObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        if (!perso.tieneObjetoID(objeto.id) || objeto.posicion != Constantes.OBJETO_POS_NO_EQUIPADO) {
            ENVIAR_Im_INFORMACION(perso, "1OBJECT_DONT_EXIST")
            return
        }
        val idModelo = objeto.objModeloID
        val cantInter = getCantObjeto(objeto.id)
        if (cantidad > objeto.cantidad - cantInter) {
            cantidad = objeto.cantidad - cantInter
        }
        if (cantidad <= 0) {
            return
        }
        var duo = Mundo.getDuoPorIDPrimero(_entregar, objeto.id)
        if (_objetosModelo[idModelo] != null) {
            _objetosModelo[idModelo] = _objetosModelo[idModelo]!! + cantidad
        } else {
            _objetosModelo[idModelo] = cantidad
        }
        if (duo != null) {
            duo._segundo += cantidad
        } else {
            duo = Duo(objeto.id, cantidad)
            _entregar.add(duo)
        }
        ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso, 'O', "+", objeto.id.toString() + "|" + duo._segundo)
        refrescar()
    }

    @Synchronized
    override fun remObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        val idModelo = objeto.objModeloID
        val cantInter = getCantObjeto(objeto.id)
        if (cantidad > objeto.cantidad - cantInter) {
            cantidad = objeto.cantidad - cantInter
        }
        if (cantidad <= 0) {
            return
        }
        val duo = Mundo.getDuoPorIDPrimero(_entregar, objeto.id) ?: return
        try {
            _objetosModelo[idModelo] = _objetosModelo[idModelo]!! - cantidad
            if (_objetosModelo[idModelo]!! <= 0) {
                _objetosModelo.remove(idModelo)
            }
        } catch (ignored: Exception) {
        }
        duo._segundo -= cantidad
        if (duo._segundo <= 0) {
            _entregar.remove(duo)
            ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso, 'O', "-", objeto.id.toString() + "")
        } else {
            ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso, 'O', "+", objeto.id.toString() + "|" + duo._segundo)
        }
        refrescar()
    }

    private fun refrescar() {
        if (!_resucitar) {
            var i = 1000000
            for (xx in 0 until _dar.size) {
                ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso, 'O', "-", "" + i++)
            }
        } else {
            ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso, 'O', "-", "" + _idMascota)
        }
        var mascota: Objeto? = null
        _polvo = false
        _idMascota = 0
        for (duo in _entregar) {
            val objModelo = Mundo.getObjeto(duo._primero)?.objModelo
            if (_resucitar) {
                if (objModelo != null) {
                    if (objModelo.tipo.toInt() == Constantes.OBJETO_TIPO_FANTASMA_MASCOTA) { // fantasma
                        mascota = Mundo.getObjeto(duo._primero)
                        _idMascota = duo._primero
                    }
                }
                if (objModelo != null) {
                    if (objModelo.id == 8012) { // polvo de resurreccion
                        _polvo = true
                    }
                }
            }
        }
        if (_resucitar) {
            if (mascota != null && _polvo) {
                ENVIAR_EmK_MOVER_OBJETO_DISTANTE(
                    _perso, 'O', "+", mascota.id.toString() + "|1|" + Mundo
                        .getMascotaPorFantasma(mascota.objModeloID) + "|" + mascota.convertirStatsAString(false)
                )
            }
        } else {
            var i = 1000000
            _dar = Mundo.listaObjetosTruequePor(_objetosModelo, _npcID)
            for ((key, value) in _dar) {
                ENVIAR_EmK_MOVER_OBJETO_DISTANTE(
                    _perso,
                    'O',
                    "+",
                    i++.toString() + "|" + value + "|" + key + "|" + Mundo.getObjetoModelo(key)!!.stringStatsModelo()
                )
            }
        }
    }

    @Synchronized
    fun getCantObjeto(objetoID: Int): Int {
        for (duo in _entregar) {
            if (duo._primero == objetoID) {
                return duo._segundo
            }
        }
        return 0
    }

    override fun addKamas(k: Long, perso: Personaje?) {}
    override val kamas: Long
        get() = 0L

    override fun getListaExchanger(perso: Personaje): String { // TODO Auto-generated method stub
        return ""
    }

    init {
        _npcID = npcID
    }
}