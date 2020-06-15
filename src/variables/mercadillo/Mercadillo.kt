package variables.mercadillo

import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Constantes
import estaticos.GestorSalida.ENVIAR_EmK_MOVER_OBJETO_DISTANTE
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.Mundo.Duo
import estaticos.Mundo.getCuenta
import estaticos.Mundo.getObjetoModelo
import estaticos.database.GestorSQL.DELETE_OBJ_MERCADILLO
import estaticos.database.GestorSQL.REPLACE_CUENTA_SERVIDOR
import estaticos.database.GestorSQL.REPLACE_OBJETO_MERCADILLO
import sprites.Exchanger
import variables.objeto.Objeto
import variables.personaje.Personaje
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class Mercadillo(
    val iD: Int, mapaID: String, tasa: Int, tiempoVenta: Short,
    maxObjCuenta: Short, nivelMax: Short, tipoObj: String
) : Exchanger {
    val porcentajeImpuesto: Int
    val maxObjCuenta: Short
    val nivelMax: Short
    val tipoObjPermitidos: String
    val mapas = ArrayList<Short>()
    val objetosMercadillos = CopyOnWriteArrayList<ObjetoMercadillo>()
    private val _tipoObjetos: MutableMap<Int, TipoObjetos> = HashMap()
    private val _lineas: MutableMap<Int, Duo<Int, Int>> = HashMap()
    //
// public float getImpuesto() {
// return _porcMercadillo / 100f;
// }
    val tiempoVenta: Short = 0

    override fun getListaExchanger(perso: Personaje): String {
        val packet = StringBuilder()
        for (objMerca in objetosMercadillos) {
            if (objMerca == null) {
                continue
            }
            if (objMerca.cuentaID != perso.cuentaID) {
                continue
            }
            if (packet.length > 0) {
                packet.append("|")
            }
            packet.append(objMerca.analizarParaEL())
        }
        return packet.toString()
    }

    fun strListaLineasPorModelo(modeloID: Int): String {
        return try {
            val tipo = getObjetoModelo(modeloID)!!.tipo.toInt()
            _tipoObjetos[tipo]!!.getModelo(modeloID)!!.strLineasPorObjMod()
        } catch (e: Exception) {
            ""
        }
    }

    fun hayModeloEnEsteMercadillo(tipo: Int, modeloID: Int): Boolean {
        return _tipoObjetos[tipo]!!.getModelo(modeloID) != null
    }

    fun stringModelo(tipoObj: Int): String {
        return _tipoObjetos[tipoObj]!!.stringModelo()
    }

    fun esTipoDeEsteMercadillo(tipoObj: Int): Boolean {
        return _tipoObjetos[tipoObj] != null
    }

    fun getLinea(lineaID: Int): LineaMercadillo? {
        return try {
            val tipoObj = _lineas[lineaID]!!._primero
            val modeloID = _lineas[lineaID]!!._segundo
            _tipoObjetos[tipoObj]!!.getModelo(modeloID)!!.getLinea(lineaID)
        } catch (e: Exception) {
            redactarLogServidorln("EXCEPTION getLinea linea: $lineaID")
            e.printStackTrace()
            null
        }
    }

    fun addObjMercaAlPuesto(objMerca: ObjetoMercadillo) {
        if (objMerca.objeto == null) {
            redactarLogServidorln("Objeto del mercadillo no tiene objeto, linea: " + objMerca.lineaID)
            return
        }
        val tipoObj = objMerca.objeto.objModelo?.tipo?.toInt() ?: return
        val modeloID = objMerca.objeto.objModeloID
        if (_tipoObjetos[tipoObj] == null) {
            redactarLogServidorln(
                "Bug Objeto del mercadillo $iD , objetoID: " + objMerca.objeto
                    .id + ", objetoTipo: " + tipoObj
            )
            return
        }
        // objMerca.setMercadilloID(_id);
        _tipoObjetos[tipoObj]!!.addModeloVerificacion(objMerca)
        _lineas[objMerca.lineaID] = Duo(tipoObj, modeloID)
        objetosMercadillos.add(objMerca)
    }

    fun borrarPath(linea: Int) {
        _lineas.remove(linea)
    }

    fun borrarObjMercaDelPuesto(objMerca: ObjetoMercadillo?, perso: Personaje?): Boolean {
        return try {
            val tipo = objMerca!!.objeto.objModelo?.tipo?.toInt()
            val borrable = _tipoObjetos[tipo]?.borrarObjMercaDeModelo(objMerca, perso, this) ?: false
            if (borrable) {
                objetosMercadillos.remove(objMerca)
                DELETE_OBJ_MERCADILLO(objMerca.objeto.id)
            }
            borrable
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @Synchronized
    fun comprarObjeto(
        lineaID: Int, cant: Int, precio: Long,
        nuevoDueño: Personaje
    ): Boolean {
        try {
            if (nuevoDueño.kamas < precio) {
                ENVIAR_Im_INFORMACION(nuevoDueño, "1128;$precio")
                return false
            }
            val linea = getLinea(lineaID)
            val objAComprar = linea!!.tuTienes(cant, precio)
            val objeto = objAComprar!!.objeto
            if (objeto == null || !borrarObjMercaDelPuesto(objAComprar, nuevoDueño)) {
                redactarLogServidorln("Bug objeto mercadillo " + Objects.requireNonNull(objeto)!!.id)
                return false
            }
            nuevoDueño.addObjIdentAInventario(objeto, true)
            objeto.objModelo?.nuevoPrecio(objAComprar.getTipoCantidad(true), precio)
            nuevoDueño.addKamas(-precio, true, true)
            val viejoProp = getCuenta(objAComprar.cuentaID)
            if (viejoProp != null) {
                viejoProp.addKamasBanco(precio)
                if (viejoProp.tempPersonaje != null) {
                    ENVIAR_Im_INFORMACION(
                        viejoProp.tempPersonaje, "065;$precio~" + objeto
                            .objModeloID + "~" + objeto.objModeloID + "~" + objeto.cantidad
                    )
                } else {
                    viejoProp.addMensaje(
                        "Im073;" + precio + "~" + objeto.objModeloID + "~" + objeto.objModeloID + "~"
                                + objeto.cantidad, false
                    )
                }
                REPLACE_CUENTA_SERVIDOR(viejoProp, 0.toByte())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun addKamas(kamas: Long, perso: Personaje?) {}
    override val kamas: Long
        get() = 0

    fun cantObjMercaEnPuesto(cuentaID: Int): Int {
        var i = 0
        for (objMerca in objetosMercadillos) {
            if (objMerca.cuentaID == cuentaID) {
                i++
            }
        }
        return i
    }

    @Synchronized
    override fun addObjetoExchanger(objeto: Objeto, cantPow: Int, perso: Personaje, precio: Int) {
        var cantPow = cantPow
        if (precio <= 1) {
            return
        }
        if (!perso.tieneObjetoID(objeto.id) || objeto.posicion != Constantes.OBJETO_POS_NO_EQUIPADO) {
            ENVIAR_Im_INFORMACION(perso, "1OBJECT_DONT_EXIST")
            return
        }
        if (cantObjMercaEnPuesto(perso.cuentaID) >= maxObjCuenta) {
            ENVIAR_Im_INFORMACION(perso, "166")
            return
        }
        val restPrecio = (precio * porcentajeImpuesto / 100).toLong()
        if (perso.kamas < restPrecio) {
            ENVIAR_Im_INFORMACION(perso, "176")
            return
        }
        if (cantPow > 3) {
            cantPow = 3
        } else if (cantPow < 1) {
            cantPow = 1
        }
        val cantReal = Math.pow(10.0, cantPow - 1.toDouble()).toInt()
        val nuevaCantidad = objeto.cantidad - cantReal
        if (nuevaCantidad >= 1) {
            val nuevoObj = objeto.clonarObjeto(nuevaCantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
            perso.addObjetoConOAKO(nuevoObj, true)
            objeto.cantidad = cantReal
            // GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objeto);
        }
        perso.borrarOEliminarConOR(objeto.id, false)
        val objMerca = ObjetoMercadillo(precio.toLong(), cantPow, perso.cuentaID, objeto, iD)
        if (!REPLACE_OBJETO_MERCADILLO(objMerca)) {
            return
        }
        perso.addKamas(-restPrecio, true, true)
        addObjMercaAlPuesto(objMerca)
        ENVIAR_EmK_MOVER_OBJETO_DISTANTE(perso, '+', "", objMerca.analizarParaEmK())
    }

    @Synchronized
    override fun remObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
        var objMerca: ObjetoMercadillo? = null
        try {
            for (temp in objetosMercadillos) {
                if (temp.objeto.id == objeto.id) {
                    objMerca = temp
                    break
                }
            }
        } catch (e: Exception) {
            return
        }
        if (objMerca == null) {
            return
        }
        perso.addObjIdentAInventario(objMerca.objeto, true)
        borrarObjMercaDelPuesto(objMerca, perso)
        ENVIAR_EmK_MOVER_OBJETO_DISTANTE(perso, '-', "", objeto.id.toString() + "")
    }

    override fun cerrar(perso: Personaje?, exito: String) {
        if (perso != null) {
            perso.cerrarVentanaExchange(exito)
        }
    }

    override fun botonOK(perso: Personaje) {}

    init {
        val mapasString = mapaID.split(",".toRegex()).toTypedArray()
        for (str in mapasString) {
            mapas.add(str.toShort())
        }
        porcentajeImpuesto = tasa
        this.maxObjCuenta = maxObjCuenta
        tipoObjPermitidos = tipoObj
        this.nivelMax = nivelMax
        for (tipo in tipoObj.split(",".toRegex()).toTypedArray()) {
            val tipoID = tipo.toInt()
            _tipoObjetos[tipoID] = TipoObjetos(tipoID)
        }
    }
}