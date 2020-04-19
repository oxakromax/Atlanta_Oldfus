package variables.mapa

import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Condiciones.validaCondiciones
import estaticos.Constantes
import estaticos.Encriptador
import estaticos.Formulas.getRandomInt
import estaticos.GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ
import estaticos.GestorSalida.ENVIAR_BN_NADA
import estaticos.GestorSalida.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS
import estaticos.GestorSalida.ENVIAR_GA2_CINEMATIC
import estaticos.GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA
import estaticos.GestorSalida.ENVIAR_GDC_ACTUALIZAR_CELDA_MAPA
import estaticos.GestorSalida.ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO
import estaticos.GestorSalida.ENVIAR_IQ_NUMERO_ARRIBA_PJ
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.GestorSalida.ENVIAR_Ow_PODS_DEL_PJ
import estaticos.GestorSalida.ENVIAR_RD_COMPRAR_CERCADO
import estaticos.GestorSalida.ENVIAR_cS_EMOTICON_MAPA
import estaticos.Mundo
import servidor.ServidorSocket.AccionDeJuego
import variables.casa.Casa
import variables.casa.Cofre
import variables.mapa.interactivo.ObjetoInteractivo
import variables.objeto.Objeto
import variables.objeto.ObjetoModelo.CAPACIDAD_STATS
import variables.pelea.Glifo
import variables.pelea.Luchador
import variables.pelea.Trampa
import variables.personaje.Personaje
import variables.zotros.Accion
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.floor

// public void destruir() {
// try {
// // for (Celda celda : _celdas.values()) {
// // celda.destruir();
// // }
// // this.finalize();
// } catch (Throwable e) {
// Bustemu.escribirLog("Throwable destruir mapa " + e.toString());
// e.printStackTrace();
// }
// }
class Celda(
    val mapa: Mapa, id: Short, activo: Boolean, movimiento: Byte, level: Byte,
    slope: Byte, lineaDeVista: Boolean, objID: Int
) {
    val id: Short = id
    private val _mapaID: Short = mapa.id
    val activo: Boolean = activo
    private val _esCaminableLevel: Boolean
    val level: Byte
    val coordX: Byte
    val coordY: Byte
    private val _movimientoInicial: Byte
    val slope: Byte
    var objetoInteractivo: ObjetoInteractivo? = null
    var ultimoUsoTrigger: Long = 0
        private set
    private var _personajes: CopyOnWriteArrayList<Personaje>? = null
    var trampas: CopyOnWriteArrayList<Trampa>? = null
        private set
    var glifos: CopyOnWriteArrayList<Glifo>? = null
        private set
    var luchadores: ArrayList<Luchador>? = null
        private set
    private var _acciones: MutableMap<Int, Accion>? = null
    private var _lineaDeVista = true
    private var _conGDF = false
    var movimiento: Byte
        private set
    var estado: Byte
        private set
    // private int getTipoObjInterac() {
// if (_objetoInterac == null) {
// return -1;
// }
// return _objetoInterac.getTipoObjInteractivo();
// }
    var objetoTirado: Objeto? = null

    fun celdaNornmal() {
        _personajes = CopyOnWriteArrayList()
        _acciones = TreeMap()
    }

    fun celdaPelea() {
        luchadores = ArrayList()
    }

    val alto: Float
        get() {
            val a: Float = if (slope.toInt() == 1) 0F else 0.5f
            val b = level - 7
            return a + b
        }

    fun aplicarAccion(perso: Personaje?) {
        if (_acciones == null || _acciones!!.isEmpty()) {
            return
        }
        for (accion in _acciones!!.values) {
            if (!validaCondiciones(perso, accion.condicion)) {
                ENVIAR_Im_INFORMACION(perso!!, "119|45")
                return
            }
        }
        var tieneCondicion = false
        for (accion in _acciones!!.values) {
            if (accion.condicion.isNotEmpty()) {
                tieneCondicion = true
            }
            accion.realizarAccion(perso!!, null, -1, (-1).toShort())
        }
        if (tieneCondicion) {
            ultimoUsoTrigger = System.currentTimeMillis()
        }
    }

    fun addAccion(idAccion: Int, args: String?, condicion: String?) {
        if (_acciones == null) {
            return
        }
        val accion = Accion(idAccion, args!!, condicion!!)
        _acciones!![idAccion] = accion
    }

    fun eliminarAcciones() {
        if (_acciones == null) {
            return
        }
        _acciones!!.clear()
    }

    fun accionesIsEmpty(): Boolean {
        return if (_acciones == null) {
            true
        } else _acciones!!.isEmpty()
    }

    val acciones: Map<Int, Accion>?
        get() = _acciones

    fun librerParaMercante(): Boolean {
        if (_personajes == null) {
            return false
        }
        return if (mapa.mercantesEnCelda(id) > 0) {
            false
        } else _personajes!!.size <= 1
    }

    val primerPersonaje: Personaje?
        get() {
            if (_personajes == null) {
                return null
            }
            return if (_personajes!!.isEmpty()) {
                null
            } else try {
                _personajes!![0]
            } catch (e: Exception) {
                primerPersonaje
            }
        }

    fun addPersonaje(perso: Personaje, aMapa: Boolean) {
        if (_personajes == null) {
            return
        }
        if (!_personajes!!.contains(perso)) {
            _personajes!!.add(perso)
        }
        if (aMapa) {
            mapa.addPersonaje(perso)
        }
    }

    fun removerPersonaje(perso: Personaje?, aMapa: Boolean) {
        if (_personajes == null) {
            return
        }
        _personajes!!.remove(perso)
        if (aMapa) {
            mapa.removerPersonaje(perso)
        }
    }

    fun esCaminable(pelea: Boolean): Boolean {
        return if (!activo || movimiento.toInt() == 0 || movimiento.toInt() == 1) {
            false
        } else _esCaminableLevel
    }

    fun lineaDeVista(): Boolean {
        return _lineaDeVista
    }

    // public boolean lineaDeVistaLibre(int idLuch) {
// if (!_activo) {
// return false;
// }
// if (_luchadores == null || _luchadores.isEmpty() || !_lineaDeVista) {
// return _lineaDeVista;
// }
// for (final Luchador luch : _luchadores) {
// if (luch.getID() == idLuch) {
// continue;
// }
// if (!luch.esInvisible(0)) {
// return false;
// }
// }
// return _lineaDeVista;
// }
    fun tieneSprite(idLuch: Int, suponiendo: Boolean): Boolean {
        if (luchadores == null || luchadores!!.isEmpty()) {
            return false
        }
        for (luch in luchadores!!) {
            if (luch.id == idLuch && suponiendo) {
                continue
            }
            if (!luch.esInvisible(idLuch)) {
                return true
            }
        }
        return false
    }

    fun moverLuchadoresACelda(celdaNew: Celda) {
        if (luchadores == null || celdaNew.id == id) {
            return
        }
        for (luch in luchadores!!) {
            celdaNew.addLuchador(luch)
            luch.celdaPelea = celdaNew
        }
        // limpia al final a los luchadores
        luchadores!!.clear()
    }

    fun addLuchador(luchador: Luchador) {
        if (luchadores == null) {
            return
        }
        if (!luchadores!!.contains(luchador)) {
            luchadores!!.add(luchador)
            luchadores!!.trimToSize()
        }
    }

    fun removerLuchador(luchador: Luchador?) {
        if (luchadores == null) {
            return
        }
        luchadores!!.remove(luchador)
    }

    fun limpiarLuchadores() {
        if (luchadores == null) {
            return
        }
        luchadores!!.clear()
    }

    val primerLuchador: Luchador?
        get() {
            if (luchadores == null) {
                return null
            }
            return if (luchadores!!.isEmpty()) {
                null
            } else luchadores!![0]
        }

    fun addGlifo(glifo: Glifo) {
        if (glifos == null) {
            glifos = CopyOnWriteArrayList()
        }
        if (!glifos!!.contains(glifo)) {
            glifos!!.add(glifo)
        }
    }

    fun borrarGlifo(glifo: Glifo?) {
        if (glifos == null) {
            return
        }
        glifos!!.remove(glifo)
    }

    fun tieneGlifo(): Boolean {
        return if (glifos == null) {
            false
        } else !glifos!!.isEmpty()
    }

    fun esGlifo(): Boolean {
        if (glifos == null) {
            return false
        }
        for (glifo in glifos!!) {
            if (glifo.celda.id == id) {
                return true
            }
        }
        return false
    }

    fun addTrampa(trampa: Trampa) {
        if (trampas == null) {
            trampas = CopyOnWriteArrayList()
        }
        if (!trampas!!.contains(trampa)) {
            trampas!!.add(trampa)
            val arrayList = mutableListOf<Trampa>()
            arrayList.addAll(trampas!!)
            arrayList.sort()
            trampas!!.clear()
            trampas!!.addAll(arrayList)
        }
    }

    fun borrarTrampa(trampa: Trampa?) {
        if (trampas == null) {
            return
        }
        trampas!!.remove(trampa)
    }

    fun tieneTrampa(): Boolean {
        return if (trampas == null) {
            false
        } else !trampas!!.isEmpty()
    }

    fun esTrampa(): Boolean {
        if (trampas == null) {
            return false
        }
        for (trampa in trampas!!) {
            if (trampa.celda.id == id) {
                return true
            }
        }
        return false
    }

    @Synchronized
    fun activarCelda(conGDF: Boolean, milisegundos: Long) {
        if (estado != Constantes.CI_ESTADO_LLENO) {
            return
        }
        val t = Thread(Runnable {
            movimiento = 4 // caminable
            _conGDF = conGDF
            estado = Constantes.CI_ESTADO_VACIANDO
            val permisos = BooleanArray(16)
            val valores = IntArray(16)
            permisos[11] = true
            valores[11] = movimiento.toInt()
            ENVIAR_GDC_ACTUALIZAR_CELDA_MAPA(
                mapa, id, Encriptador.stringParaGDC(permisos, valores),
                false
            )
            if (_conGDF) {
                ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(mapa, mapa.getCelda(id)!!)
                try {
                    Thread.sleep(2000)
                } catch (e: Exception) {
                }
            }
            if (milisegundos > 0) {
                estado = Constantes.CI_ESTADO_VACIO
                try {
                    Thread.sleep(milisegundos) // hace de timer;
                } catch (e: Exception) {
                }
                movimiento = _movimientoInicial
                estado = Constantes.CI_ESTADO_LLENANDO
                if (_conGDF) {
                    ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(mapa, mapa.getCelda(id)!!)
                    try {
                        Thread.sleep(2000)
                    } catch (e: Exception) {
                    }
                }
                estado = Constantes.CI_ESTADO_LLENO
                valores[11] = movimiento.toInt()
                ENVIAR_GDC_ACTUALIZAR_CELDA_MAPA(
                    mapa, id, Encriptador.stringParaGDC(permisos, valores),
                    false
                )
            } else {
                movimiento = _movimientoInicial
                estado = Constantes.CI_ESTADO_LLENO
                if (_conGDF) {
                    ENVIAR_GDF_ESTADO_OBJETO_INTERACTIVO(mapa, mapa.getCelda(id)!!)
                }
            }
        })
        t.isDaemon = true
        t.start()
    }

    fun puedeHacerAccion(skillID: Int, pescarKuakua: Boolean): Boolean {
        if (objetoInteractivo == null) {
            return false
        }
        if (objetoInteractivo!!.objIntModelo!!.tieneSkill(skillID)) {
            return if (skillID == Constantes.SKILL_PESCAR_KUAKUA) {
                pescarKuakua
            } else if (objetoInteractivo!!.objIntModelo!!.tipo.toInt() == 1) { // trigo, cereal, flores
                objetoInteractivo!!.estado == Constantes.OI_ESTADO_LLENO
            } else {
                true
            }
        } else if (skillID == 153 && objetoInteractivo?.objIntModelo?.id == 85) { // Basura, como cofre
            return true
        }
        redactarLogServidorln("Bug al verificar si se puede realizar el skill ID = $skillID")
        return false
    }

    fun puedeIniciarAccion(perso: Personaje, AJ: AccionDeJuego?): Boolean {
        try {
            if (perso.pelea != null) {
                return false
            }
            if (AJ == null) {
                return false
            }
            var celdaID: Short = -1
            var skillID = -1
            try {
                celdaID = AJ.pathPacket.split(";".toRegex()).toTypedArray()[0].toShort()
                skillID = AJ.pathPacket.split(";".toRegex()).toTypedArray()[1].toInt()
            } catch (e: Exception) {
                return false
            }
            if (Constantes.esTrabajo(skillID)) {
                return perso.puedeIniciarTrabajo(skillID, objetoInteractivo, AJ.iDUnica, this)
            } else {
                val casa1: Casa?
                when (skillID) {
                    Constantes.SKILL_PELAR_PATATAS -> {
                    }
                    Constantes.SKILL_GUARDAR_POSICION -> {
                        perso.setPuntoSalvada("$_mapaID,$id")
                        ENVIAR_Im_INFORMACION(perso, "06")
                    }
                    Constantes.SKILL_REGENERARSE -> {
                        perso.fullPDV()
                        ENVIAR_Ak_KAMAS_PDV_EXP_PJ(perso)
                    }
                    Constantes.SKILL_UTILIZAR_ZAAP -> perso.abrirMenuZaap()
                    152 -> {
                        perso.pescarKuakua = false
                        if (!objetoInteractivo!!.puedeIniciarRecolecta()) {
                            return false
                        }
                        objetoInteractivo!!.iniciarRecolecta(objetoInteractivo!!.duracion.toLong())
                        ENVIAR_GA_ACCION_JUEGO_AL_MAPA(
                            perso.mapa, AJ.iDUnica, 501, perso.Id.toString() + "",
                            id.toString() + "," + objetoInteractivo!!.duracion + "," + objetoInteractivo!!.animacionPJ
                        )
                        return true
                    }
                    Constantes.SKILL_SACAR_AGUA, Constantes.SKILL_JUGAR_MAQUINA_FUERZA -> {
                        if (!objetoInteractivo!!.puedeIniciarRecolecta()) {
                            return false
                        }
                        objetoInteractivo!!.iniciarRecolecta(objetoInteractivo!!.duracion.toLong())
                        ENVIAR_GA_ACCION_JUEGO_AL_MAPA(
                            perso.mapa, AJ.iDUnica, 501, perso.Id.toString() + "",
                            id.toString() + "," + objetoInteractivo!!.duracion + "," + objetoInteractivo!!.animacionPJ
                        )
                        return true
                    }
                    157 -> perso.abrirMenuZaapi()
                    175 -> perso.abrirCercado()
                    176 -> {
                        val cercado = perso.mapa.cercado
                        if (cercado!!.esPublico()) {
                            ENVIAR_Im_INFORMACION(perso, "196")
                            return false
                        }
                        if (cercado.precio <= 0) {
                            ENVIAR_Im_INFORMACION(perso, "197")
                            return false
                        }
                        if (perso.gremio == null) {
                            ENVIAR_Im_INFORMACION(perso, "1135")
                            return false
                        }
                        // if (perso.getMiembroGremio().getRango() != 1) {
// GestorSalida.ENVIAR_Im_INFORMACION(perso, "198");
// break;
// }
                        ENVIAR_RD_COMPRAR_CERCADO(perso, cercado.precio.toString() + "|" + cercado.precio)
                    }
                    177, 178 -> {
                        val cercado1 = perso.mapa.cercado
                        if (cercado1!!.esPublico()) {
                            ENVIAR_Im_INFORMACION(perso, "194")
                            return false
                        }
                        if (cercado1.dueñoID != perso.Id) {
                            ENVIAR_Im_INFORMACION(perso, "195")
                            return false
                        }
                        ENVIAR_RD_COMPRAR_CERCADO(perso, cercado1.precio.toString() + "|" + cercado1.precio)
                    }
                    Constantes.SKILL_ACCIONAR_PALANCA -> perso.realizarOtroInteractivo(this, objetoInteractivo)
                    183 -> if (perso.nivel > 15) {
                        ENVIAR_Im_INFORMACION(perso, "1127")
                    } else {
                        ENVIAR_GA2_CINEMATIC(perso, "5")
                        val mapa = Constantes.getMapaInicioIncarnam(perso.getClaseID(true).toInt()).split(",".toRegex())
                            .toTypedArray()
                        perso.teleport(mapa[0].toShort(), mapa[1].toShort())
                    }
                    81 -> {
                        casa1 = Mundo.getCasaPorUbicacion(_mapaID, celdaID.toInt())
                        if (casa1 == null) {
                            return false
                        }
                        casa1.ponerClave(perso, true)
                    }
                    100 -> {
                        casa1 = Mundo.getCasaPorUbicacion(_mapaID, celdaID.toInt())
                        if (casa1 == null) {
                            return false
                        }
                        // perso.setConsultarCasa(casa1);
                        casa1.quitarCerrojo(perso)
                    }
                    84 -> {
                        casa1 = Mundo.getCasaPorUbicacion(_mapaID, celdaID.toInt())
                        if (casa1 == null) {
                            return false
                        }
                        casa1.intentarAcceder(perso, "")
                    }
                    97, 98, 108 -> {
                        casa1 = Mundo.getCasaPorUbicacion(_mapaID, celdaID.toInt())
                        if (casa1 == null) {
                            return false
                        }
                        casa1.abrirVentanaCompraVentaCasa(perso)
                    }
                    104 -> {
                        if (_mapaID.toInt() == 7442) {
                            ENVIAR_BN_NADA(perso)
                            return false
                        }
                        var cofre2 = Mundo.getCofrePorUbicacion(_mapaID, celdaID)
                        if (cofre2 == null) {
                            cofre2 = Cofre.insertarCofre(_mapaID, id)
                        }
                        if (cofre2 == null) {
                            return false
                        }
                        cofre2.intentarAcceder(perso, "")
                    }
                    105 -> {
                        if (_mapaID.toInt() == 7442) {
                            ENVIAR_BN_NADA(perso)
                            return false
                        }
                        var cofre = Mundo.getCofrePorUbicacion(_mapaID, celdaID)
                        if (cofre == null) {
                            cofre = Cofre.insertarCofre(_mapaID, id)
                        }
                        if (cofre == null) {
                            return false
                        }
                        cofre.ponerClave(perso, true)
                    }
                    153 -> {
                        val basura = Mundo.getCofrePorUbicacion(0.toShort(), 0.toShort()) ?: return false
                        basura.intentarAcceder(perso, "")
                    }
                    170 -> {
                        ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(
                            perso, Constantes.INTERCAMBIO_TIPO_LIBRO_ARTESANOS.toInt(),
                            Constantes.SKILLS_LIBRO_ARTESANOS
                        )
                        perso.tipoExchange = Constantes.INTERCAMBIO_TIPO_LIBRO_ARTESANOS
                    }
                    181, 121 -> if (perso.estaDisponible(false, true)) {
                        ENVIAR_BN_NADA(perso)
                        return false
                    }
                    else -> redactarLogServidorln("Bug al iniciar la skill ID = $skillID")
                }
            }
        } catch (e: Exception) {
            val error = "EXCEPTION iniciarAccion AJ.getPacket(): " + AJ!!.pathPacket + " e: " + e.toString()
            ENVIAR_BN_NADA(perso, error)
            redactarLogServidorln(error)
        }
        return false
    }

    fun finalizarAccion(perso: Personaje, AJ: AccionDeJuego?): Boolean {
        try {
            if (AJ == null) {
                return false
            }
            var accionID = -1
            accionID = try {
                AJ.pathPacket.split(";".toRegex()).toTypedArray()[1].toInt()
            } catch (e: Exception) {
                return false
            }
            if (Constantes.esTrabajo(accionID)) { // es de oficio
                return perso.finalizarTrabajo(accionID)
            } else {
                when (accionID) {
                    Constantes.SKILL_PELAR_PATATAS, Constantes.SKILL_GUARDAR_POSICION, 81, 84, 97, 98, 104, 105, 108, 114, Constantes.SKILL_MACHACAR_RECURSOS, 157, 170, 175, 176, 177, 178, 181, 183, 153, Constantes.SKILL_ACCIONAR_PALANCA -> return true
                    Constantes.SKILL_JUGAR_MAQUINA_FUERZA, Constantes.SKILL_SACAR_AGUA, Constantes.SKILL_PESCAR_KUAKUA -> {
                        if (!objetoInteractivo!!.puedeFinalizarRecolecta()) {
                            return false
                        }
                        objetoInteractivo!!.activandoRecarga(Constantes.OI_ESTADO_VACIANDO, Constantes.OI_ESTADO_VACIO)
                        when (accionID) {
                            Constantes.SKILL_SACAR_AGUA -> {
                                val cantidad = getRandomInt(1, 10)
                                perso.addObjIdentAInventario(
                                    Mundo.getObjetoModelo(311)?.crearObjeto(
                                        cantidad,
                                        Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM
                                    ), false
                                )
                                ENVIAR_IQ_NUMERO_ARRIBA_PJ(perso, perso.Id, cantidad)
                                ENVIAR_Ow_PODS_DEL_PJ(perso)
                            }
                            Constantes.SKILL_PESCAR_KUAKUA -> {
                                val x = getRandomInt(0, 5)
                                if (x == 5) {
                                    ENVIAR_cS_EMOTICON_MAPA(perso.mapa, perso.Id, 11)
                                    perso.addObjIdentAInventario(
                                        Mundo.getObjetoModelo(6659)?.crearObjeto(
                                            1,
                                            Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM
                                        ), false
                                    )
                                    ENVIAR_IQ_NUMERO_ARRIBA_PJ(perso, perso.Id, 1)
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "1TRY_OTHER")
                                    ENVIAR_cS_EMOTICON_MAPA(perso.mapa, perso.Id, 12)
                                }
                                ENVIAR_Ow_PODS_DEL_PJ(perso)
                            }
                            Constantes.SKILL_JUGAR_MAQUINA_FUERZA -> {
                            }
                        }
                    }
                    else -> redactarLogServidorln("Bug al finalizar la accion ID = $accionID")
                }
            }
        } catch (ignored: Exception) {
        }
        return true
    } // public void destruir() {

    // try {
// this.finalize();
// } catch (Throwable e) {
// Bustemu.escribirLog("Throwable destruir celda " + e.toString());
// e.printStackTrace();
// }
// }
    init {
        this.level = level
        this.movimiento = movimiento
        _lineaDeVista = lineaDeVista
        estado = Constantes.CI_ESTADO_LLENO
        _movimientoInicial = this.movimiento
        this.slope = slope
        val ancho = mapa.ancho
        val _loc5 = floor(this.id / (ancho * 2 - 1).toDouble()).toInt()
        val _loc6 = this.id - _loc5 * (ancho * 2 - 1)
        val _loc7 = _loc6 % ancho
        coordY = (_loc5 - _loc7).toByte()
        // es en plano inclinado, solo Y es negativo partiendo del 0 arriba negativo, abajo positivo
        coordX = ((this.id - (ancho - 1) * coordY) / ancho).toByte()
        if (objID == -1) {
            objetoInteractivo = null
        } else {
            objetoInteractivo = ObjetoInteractivo(mapa, this, objID)
            Mundo.addObjInteractivo(objetoInteractivo!!)
        }
        val tempD = ((coordX + coordY - 1) * 13.5f).toInt()
        val tempL = (this.level - 7) * 20
        _esCaminableLevel = tempD - tempL >= 0
    }
}

private fun addAll(elements: MutableList<Any>) {

}
