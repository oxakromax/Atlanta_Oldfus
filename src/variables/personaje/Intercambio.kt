package variables.personaje

import estaticos.AtlantaMain
import estaticos.Constantes
import estaticos.GestorSalida.ENVIAR_EK_CHECK_OK_INTERCAMBIO
import estaticos.GestorSalida.ENVIAR_EMK_MOVER_OBJETO_LOCAL
import estaticos.GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO
import estaticos.GestorSalida.ENVIAR_Ow_PODS_DEL_PJ
import estaticos.Mundo.Duo
import estaticos.Mundo.getDuoPorIDPrimero
import estaticos.database.GestorSQL.INSERT_INTERCAMBIO
import sprites.Exchanger
import variables.objeto.Objeto
import java.util.*

class Intercambio(private val _perso1: Personaje, private val _perso2: Personaje) : Exchanger {
    private val _objetos1 = ArrayList<Duo<Int, Int>>()
    private val _objetos2 = ArrayList<Duo<Int, Int>>()
    private var _kamas1: Long = 0
    private var _kamas2: Long = 0
    private var _ok1 = false
    private var _ok2 = false
    @Synchronized
    override fun botonOK(perso: Personaje) {
        if (_perso1.Id == perso.Id) {
            _ok1 = !_ok1
            ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso1, _ok1, perso.Id)
            ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso2, _ok1, perso.Id)
        } else if (_perso2.Id == perso.Id) {
            _ok2 = !_ok2
            ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso1, _ok2, perso.Id)
            ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso2, _ok2, perso.Id)
        }
        if (_ok1 && _ok2) {
            aplicar()
        }
    }

    fun desCheck() {
        _ok1 = false
        _ok2 = false
        ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso1, false, _perso1.Id)
        ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso2, _ok1, _perso1.Id)
        ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso1, _ok2, _perso2.Id)
        ENVIAR_EK_CHECK_OK_INTERCAMBIO(_perso2, _ok2, _perso2.Id)
    }

    @Synchronized
    fun getKamas(id: Int): Long {
        if (_perso1.Id == id) {
            return _kamas1
        }
        return if (_perso2.Id == id) {
            _kamas2
        } else 0
    }

    override fun cerrar(perso: Personaje?, exito: String) {
        _perso1.cerrarVentanaExchange(exito)
        ENVIAR_Ow_PODS_DEL_PJ(_perso1)
        _perso2.cerrarVentanaExchange(exito)
        ENVIAR_Ow_PODS_DEL_PJ(_perso2)
    }

    @Synchronized
    fun aplicar() {
        _kamas1 = Math.min(_kamas1, _perso1.kamas)
        _kamas2 = Math.min(_kamas2, _perso2.kamas)
        _perso1.addKamas(-_kamas1 + _kamas2, true, true)
        _perso2.addKamas(-_kamas2 + _kamas1, true, true)
        val str = StringBuilder()
        str.append(_perso1.nombre).append(" (").append(_perso1.Id).append(") >> ")
        str.append("[").append(_kamas1).append(" KAMAS]")
        for (duo in _objetos1) {
            try {
                val obj1 = _perso1.getObjeto(duo._primero)
                val cantidad = duo._segundo
                str.append(", ")
                if (obj1 == null) {
                    str.append("[NO TIENE - ID ").append(duo._primero).append(" CANT ").append(duo._segundo)
                    continue
                }
                str.append("[").append(obj1.objModelo?.nombre).append(" ID:").append(obj1.id).append(" Mod:")
                    .append(obj1.objModeloID).append(" Cant:").append(cantidad).append("]")
                val nuevaCantidad = obj1.cantidad - cantidad
                if (nuevaCantidad >= 1) {
                    val nuevoObj = obj1.clonarObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                    _perso2.addObjIdentAInventario(nuevoObj, false)
                    obj1.cantidad = obj1.cantidad - cantidad
                    ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso1, obj1)
                } else {
                    _perso1.borrarOEliminarConOR(obj1.id, false)
                    _perso2.addObjIdentAInventario(obj1, true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        str.append(" ### ")
        str.append(_perso2.nombre).append(" (").append(_perso2.Id).append(") >> ")
        str.append("[").append(_kamas2).append(" KAMAS]")
        for (duo in _objetos2) {
            try {
                val obj2 = _perso2.getObjeto(duo._primero)
                val cantidad = duo._segundo
                str.append(", ")
                if (obj2 == null) {
                    str.append("[NO TIENE - ID ").append(duo._primero).append(" CANT ").append(duo._segundo)
                    continue
                }
                str.append("[").append(obj2.objModelo?.nombre).append(" ID:").append(obj2.id).append(" Mod:")
                    .append(obj2.objModeloID).append(" Cant:").append(cantidad).append("]")
                val nuevaCantidad = obj2.cantidad - cantidad
                if (nuevaCantidad >= 1) {
                    val nuevoObj = obj2.clonarObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                    _perso1.addObjIdentAInventario(nuevoObj, false)
                    obj2.cantidad = nuevaCantidad
                    ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(_perso2, obj2)
                } else {
                    _perso2.borrarOEliminarConOR(obj2.id, false)
                    _perso1.addObjIdentAInventario(obj2, true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (AtlantaMain.PARAM_GUARDAR_LOGS_INTERCAMBIOS) {
            INSERT_INTERCAMBIO(str.toString())
        }
        cerrar(null, "a")
    }

    @Synchronized
    override fun addObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        if (!perso.tieneObjetoID(objeto.id) || objeto.posicion != Constantes.OBJETO_POS_NO_EQUIPADO) {
            ENVIAR_Im_INFORMACION(perso, "1OBJECT_DONT_EXIST")
            return
        }
        val cantInter = getCantObjeto(objeto.id, perso.Id)
        if (cantidad > objeto.cantidad - cantInter) {
            cantidad = objeto.cantidad - cantInter
        }
        if (cantidad < 1) {
            return
        }
        desCheck()
        val objetoID = objeto.id
        val str = "$objetoID|$cantidad"
        val add = "|" + objeto.objModeloID + "|" + objeto.convertirStatsAString(false)
        if (_perso1.Id == perso.Id) {
            val duo = getDuoPorIDPrimero(_objetos1, objetoID)
            if (duo != null) {
                duo._segundo = duo._segundo + cantidad
                ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso1, 'O', "+", objetoID.toString() + "|" + duo._segundo)
                ENVIAR_EmK_MOVER_OBJETO_DISTANTE(
                    _perso2,
                    'O',
                    "+",
                    objetoID.toString() + "|" + duo._segundo + add
                )
            } else {
                ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso1, 'O', "+", str)
                ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso2, 'O', "+", str + add)
                _objetos1.add(Duo(objetoID, cantidad))
            }
        } else if (_perso2.Id == perso.Id) {
            val duo = getDuoPorIDPrimero(_objetos2, objetoID)
            if (duo != null) {
                duo._segundo = duo._segundo + cantidad
                ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso2, 'O', "+", objetoID.toString() + "|" + duo._segundo)
                ENVIAR_EmK_MOVER_OBJETO_DISTANTE(
                    _perso1,
                    'O',
                    "+",
                    objetoID.toString() + "|" + duo._segundo + add
                )
            } else {
                ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso2, 'O', "+", str)
                ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso1, 'O', "+", str + add)
                _objetos2.add(Duo(objetoID, cantidad))
            }
        }
    }

    @Synchronized
    override fun remObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        val cantInter = getCantObjeto(objeto.id, perso.Id)
        if (cantidad > cantInter) {
            cantidad = cantInter
        }
        if (cantidad < 1) {
            return
        }
        desCheck()
        if (_perso1.Id == perso.Id) {
            val duo = getDuoPorIDPrimero(_objetos1, objeto.id)
            if (duo != null) {
                val nuevaCantidad = duo._segundo - cantidad
                if (nuevaCantidad <= 0) {
                    _objetos1.remove(duo)
                    ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso1, 'O', "-", objeto.id.toString() + "")
                    ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso2, 'O', "-", objeto.id.toString() + "")
                } else {
                    duo._segundo = nuevaCantidad
                    ENVIAR_EMK_MOVER_OBJETO_LOCAL(
                        _perso1,
                        'O',
                        "+",
                        objeto.id.toString() + "|" + nuevaCantidad
                    )
                    ENVIAR_EmK_MOVER_OBJETO_DISTANTE(
                        _perso2, 'O', "+", objeto.id.toString() + "|" + nuevaCantidad + "|"
                                + objeto.objModeloID + "|" + objeto.convertirStatsAString(false)
                    )
                }
            }
        } else if (_perso2.Id == perso.Id) {
            val duo = getDuoPorIDPrimero(_objetos2, objeto.id)
            if (duo != null) {
                val nuevaCantidad = duo._segundo - cantidad
                if (nuevaCantidad <= 0) {
                    _objetos2.remove(duo)
                    ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso1, 'O', "-", objeto.id.toString() + "")
                    ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso2, 'O', "-", objeto.id.toString() + "")
                } else {
                    duo._segundo = nuevaCantidad
                    ENVIAR_EmK_MOVER_OBJETO_DISTANTE(
                        _perso1, 'O', "+", objeto.id.toString() + "|" + nuevaCantidad + "|"
                                + objeto.objModeloID + "|" + objeto.convertirStatsAString(false)
                    )
                    ENVIAR_EMK_MOVER_OBJETO_LOCAL(
                        _perso2,
                        'O',
                        "+",
                        objeto.id.toString() + "|" + nuevaCantidad
                    )
                }
            }
        }
    }

    @Synchronized
    fun getCantObjeto(objetoID: Int, persoID: Int): Int {
        val objetos: ArrayList<Duo<Int, Int>>
        objetos = if (_perso1.Id == persoID) {
            _objetos1
        } else {
            _objetos2
        }
        for (duo in objetos) {
            if (duo._primero == objetoID) {
                return duo._segundo
            }
        }
        return 0
    }

    override fun addKamas(kamas: Long, perso: Personaje?) {
        desCheck()
        if (kamas < 0) {
            return
        }
        if (perso != null) {
            if (_perso1.Id == perso.Id) {
                _kamas1 = kamas
                ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso1, 'G', "", kamas.toString() + "")
                ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso2, 'G', "", kamas.toString() + "")
            } else if (_perso2.Id == perso.Id) {
                _kamas2 = kamas
                ENVIAR_EMK_MOVER_OBJETO_LOCAL(_perso2, 'G', "", kamas.toString() + "")
                ENVIAR_EmK_MOVER_OBJETO_DISTANTE(_perso1, 'G', "", kamas.toString() + "")
            }
        }
    }

    override val kamas: Long
        get() = 0L

    override fun getListaExchanger(perso: Personaje): String { // TODO Auto-generated method stub
        return ""
    }

}