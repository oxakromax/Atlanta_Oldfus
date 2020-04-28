package variables.mapa.interactivo

import estaticos.AtlantaMain
import estaticos.Constantes
import estaticos.GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO
import estaticos.Mundo
import variables.mapa.Celda
import variables.mapa.Mapa
import kotlin.concurrent.thread
import kotlin.math.max

class ObjetoInteractivo(private val _mapa: Mapa, private val _celda: Celda, val gfxID: Int) {
    val objIntModelo: ObjetoInteractivoModelo? = Mundo.getObjIntModeloPorGfx(gfxID)
    var _esInteractivo: Boolean
    var estado = Constantes.OI_ESTADO_LLENO
        private set
    private var _bonusEstrellas = -1
    private var _milisegundosRecarga = 0
    private var _tiempoProxRecarga: Long = -1
    private var _tiempoProxSubidaEstrella: Long = -1
    private var _tiempoFinalizarRecolecta: Long = 0
    fun puedeFinalizarRecolecta(): Boolean {
        val b = System.currentTimeMillis() >= _tiempoFinalizarRecolecta
        if (b) {
            _tiempoFinalizarRecolecta = 0
        }
        return b
    }

    fun setTiempoInicioRecolecta(t: Long) {
        _tiempoFinalizarRecolecta = t
    }

    private val tipoObjInteractivo: Int
        get() = objIntModelo?.tipo?.toInt() ?: -1

    val infoPacket: String
        get() = estado.toString() + ";" + (if (_esInteractivo) "1" else "0") + ";" + _bonusEstrellas

    var bonusEstrellas: Int
        get() {
            bonusEstrellas = _bonusEstrellas
            return max(0, _bonusEstrellas)
        }
        set(estrellas) {
            _bonusEstrellas = estrellas
            if (_bonusEstrellas < AtlantaMain.INICIO_BONUS_ESTRELLAS_RECURSOS) {
                _bonusEstrellas = AtlantaMain.INICIO_BONUS_ESTRELLAS_RECURSOS
            }
            if (_bonusEstrellas > AtlantaMain.MAX_BONUS_ESTRELLAS_RECURSOS) {
                _bonusEstrellas = if (AtlantaMain.PARAM_REINICIAR_ESTRELLAS_SI_LLEGA_MAX) {
                    AtlantaMain.INICIO_BONUS_ESTRELLAS_RECURSOS
                } else {
                    AtlantaMain.MAX_BONUS_ESTRELLAS_RECURSOS
                }
            }
            ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(_mapa, _celda)
        }

    fun realBonusEstrellas(): Int {
        return _bonusEstrellas
    }

    fun subirBonusEstrellas(cant: Int) {
        if (!AtlantaMain.PARAM_ESTRELLAS_RECURSOS) {
            return
        }
        bonusEstrellas = _bonusEstrellas + cant
        ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(_mapa, _celda)
    }

    fun subirEstrella() {
        if (!AtlantaMain.PARAM_ESTRELLAS_RECURSOS || _tiempoProxSubidaEstrella <= 0 || AtlantaMain.SEGUNDOS_ESTRELLAS_RECURSOS <= 0) {
            return
        }
        if (System.currentTimeMillis() - _tiempoProxSubidaEstrella >= 0) {
            subirBonusEstrellas(20)
            restartSubirEstrellas()
        }
    }

    private fun restartSubirEstrellas() {
        if (!AtlantaMain.PARAM_ESTRELLAS_RECURSOS) {
            return
        }
        if (tipoObjInteractivo == 1) { // recursos para recoger
            _tiempoProxSubidaEstrella = System.currentTimeMillis() + AtlantaMain.SEGUNDOS_ESTRELLAS_RECURSOS * 1000
        }
        ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(_mapa, _celda)
    }

    // public boolean esInteractivo() {
// return _esInteractivo;
// }
// private void setInteractivo(final boolean b) {
// _esInteractivo = b;
// }
    val duracion: Int
        get() {
            var duracion = 1500
            if (objIntModelo != null) {
                duracion = objIntModelo.duracion
            }
            return duracion
        }

    val animacionPJ: Int
        get() {
            var animacionID = 4
            if (objIntModelo != null) {
                animacionID = objIntModelo.animacionPJ
            }
            return animacionID
        }

    @Synchronized
    fun iniciarRecolecta(t: Long) {
        if (_milisegundosRecarga <= 0) {
            return
        }
        _tiempoFinalizarRecolecta = System.currentTimeMillis() + t
        _esInteractivo = false
    }

    fun forzarActivarRecarga(milis: Int) {
        if (milis <= 0) {
            return
        }
        _milisegundosRecarga = milis
        activandoRecarga(Constantes.OI_ESTADO_LLENANDO, Constantes.OI_ESTADO_LLENO)
    }

    fun puedeIniciarRecolecta(): Boolean { // if (!_esInteractivo) {
// return false;
// }
        if (_tiempoFinalizarRecolecta > 0) {
            return false
        }
        if (_milisegundosRecarga <= 0) {
            return true
        }
        return if (_tiempoProxRecarga <= 0) {
            true
        } else System.currentTimeMillis() - _tiempoProxRecarga >= 0
    }

    fun activandoRecarga(vaciando: Byte, vacio: Byte) {
        if (_milisegundosRecarga <= 0) {
            return
        }
        _esInteractivo = false
        estado = vaciando
        ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(_mapa, _celda)
        estado = vacio
        _tiempoProxRecarga = System.currentTimeMillis() + _milisegundosRecarga
        _tiempoProxSubidaEstrella = -1
    }

    fun recargando(forzado: Boolean) {
        if (_tiempoProxRecarga <= 0) {
            return
        }
        if (forzado || System.currentTimeMillis() - _tiempoProxRecarga >= 0) {
            _esInteractivo = true
            estado = Constantes.OI_ESTADO_LLENANDO
            if (AtlantaMain.PARAM_ESTRELLAS_RECURSOS && tipoObjInteractivo == 1) {
                _bonusEstrellas = AtlantaMain.INICIO_BONUS_ESTRELLAS_RECURSOS
                restartSubirEstrellas()
            }
            if (!forzado) {
                try {
                    thread(true, true) {
                        ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(_mapa, _celda)
                        estado = Constantes.OI_ESTADO_LLENO
                        _tiempoProxRecarga = -1
                        Thread.currentThread().interrupt()
                    }
                } catch (e: Exception) {
                    AtlantaMain.redactarLogServidorln("Error en Thread de Objetos Interactivos ${e.stackTrace}")
                }

//                val t = Thread(Runnable {
//                    ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(_mapa, _celda)
////                    try {
////                        Thread.sleep(2000)
////                    } catch (e: InterruptedException) {
////                    }
//                    estado = Constantes.OI_ESTADO_LLENO
//                    _tiempoProxRecarga = -1
//                })
//                t.isDaemon = true
//                t.start()
            } else {
                estado = Constantes.OI_ESTADO_LLENO
                _tiempoProxRecarga = -1
            }
        }
    }

    init {
        _esInteractivo = true
        if (objIntModelo != null) {
            _milisegundosRecarga = objIntModelo.tiempoRecarga // milis
            if (objIntModelo.tipo.toInt() == 1) { // solo recursos para recoger
                _mapa.objetosInteractivos!!.add(this)
                if (AtlantaMain.PARAM_ESTRELLAS_RECURSOS) {
                    _bonusEstrellas = AtlantaMain.INICIO_BONUS_ESTRELLAS_RECURSOS
                    restartSubirEstrellas()
                }
            }
        }
    }
}