package variables.npc

import estaticos.Condiciones.validaCondiciones
import estaticos.Mundo
import estaticos.database.GestorSQL.ACTUALIZAR_NPC_VENTAS
import variables.mision.Mision
import variables.mision.MisionModelo
import variables.mision.MisionPregunta
import variables.objeto.ObjetoModelo
import variables.personaje.Personaje
import java.util.*

class NPCModelo(
    val id: Int, var GfxID: Int, var TallaX: Short, var TallaY: Short, var Sexo: Byte,
    var Color1: Int, var Color2: Int, var Color3: Int, foto: Int, preguntaID: Int, objVender: String,
    nombre: String, arma: Int, sombrero: Int, capa: Int, mascota: Int, escudo: Int
) {
    val foto: Int
    val nombre: String
    val objAVender = ArrayList<ObjetoModelo>()
    private val _misiones = ArrayList<MisionModelo>()
    private var _arma = 0
    private var _sombrero = 0
    private var _capa = 0
    private var _mascota = 0
    private var _escudo = 0
    private var _preguntaID: Int
    var accesoriosHex: String? = null
        private set
    private var _listaObjetos = ""
    fun setAccesorios(arma: Int, sombrero: Int, capa: Int, mascota: Int, escudo: Int) {
        _arma = arma
        _sombrero = sombrero
        _capa = capa
        _mascota = mascota
        _escudo = escudo
        accesoriosHex =
            (Integer.toHexString(arma) + "," + Integer.toHexString(sombrero) + "," + Integer.toHexString(
                capa
            )
                    + "," + Integer.toHexString(mascota) + "," + Integer.toHexString(escudo))
    }

    val accesoriosInt: String
        get() = "$_arma,$_sombrero,$_capa,$_mascota,$_escudo"

    fun addMision(mision: MisionModelo) {
        if (!_misiones.contains(mision)) {
            _misiones.add(mision)
        }
    }

    fun getExtraClip(perso: Personaje?): String {
        if (perso == null) {
            return ""
        }
        for (mision in _misiones) {
            if (perso.tieneMision(mision.id)) {
                continue
            }
            return "4" // signo de admiracion
        }
        return ""
    }

    fun setPreguntaID(pregunta: Int) {
        _preguntaID = pregunta
    }

    fun getPreguntaID(perso: Personaje?): Int {
        if (perso != null) {
            var completado = -1
            var noTiene = -1
            var incompleto = -1
            var preg: MisionPregunta
            loop@ for (misionMod in _misiones) {
                when (perso.getEstadoMision(misionMod.id)) {
                    Mision.ESTADO_COMPLETADO -> {
                        if (misionMod.puedeRepetirse) {
                            perso.borrarMision(misionMod.id)
                        }
                        preg = misionMod.getMisionPregunta(Mision.ESTADO_COMPLETADO)!!
                        if (preg.nPCID != id || preg.preguntaID == 0) {
                            continue@loop
                        }
                        if (validaCondiciones(perso, preg.condicion)) {
                            completado = preg.preguntaID
                        }
                    }
                    Mision.ESTADO_INCOMPLETO -> {
                        preg = misionMod.getMisionPregunta(Mision.ESTADO_INCOMPLETO)!!
                        if (preg.nPCID != id || preg.preguntaID == 0) {
                            continue@loop
                        }
                        if (validaCondiciones(perso, preg.condicion)) {
                            incompleto = preg.preguntaID
                        }
                    }
                    Mision.ESTADO_NO_TIENE -> {
                        preg = misionMod.getMisionPregunta(Mision.ESTADO_NO_TIENE)!!
                        if (preg.nPCID != id || preg.preguntaID == 0) {
                            continue@loop
                        }
                        if (validaCondiciones(perso, preg.condicion)) {
                            noTiene = preg.preguntaID
                        }
                    }
                }
            }
            if (incompleto != -1) {
                return incompleto
            }
            if (noTiene != -1) {
                return noTiene
            }
            if (completado != -1) {
                return completado
            }
        }
        return _preguntaID
    }

    fun modificarNPC(
        sexo: Byte, escalaX: Short, escalaY: Short, gfxID: Int,
        color1: Int, color2: Int, color3: Int
    ) {
        this.Sexo = sexo
        TallaX = escalaX
        TallaY = escalaY
        this.GfxID = gfxID
        this.Color1 = color1
        this.Color2 = color2
        this.Color3 = color3
        // GestorSQL.ACTUALIZAR_NPC_COLOR_SEXO(this);
    }

    fun actualizarObjetosAVender() {
        if (objAVender.isEmpty()) {
            _listaObjetos = ""
        }
        val objetos = StringBuilder()
        for (obj in objAVender) {
            objetos.append(obj.stringDeStatsParaTienda()).append("|")
        }
        _listaObjetos = objetos.toString()
    }

    fun actualizarStringBD(): String {
        if (objAVender.isEmpty()) {
            return ""
        }
        val objetos = StringBuilder()
        for (obj in objAVender) {
            objetos.append(obj.id).append(",")
        }
        return objetos.toString()
    }

    fun listaObjetosAVender(): String {
        return _listaObjetos
    }

    fun addObjetoAVender(objetos: ArrayList<ObjetoModelo>) {
        var retorna = false
        for (obj in objetos) {
            if (objAVender.contains(obj)) {
                continue
            }
            objAVender.add(obj)
            retorna = true
        }
        if (!retorna) {
            return
        }
        actualizarObjetosAVender()
        ACTUALIZAR_NPC_VENTAS(this)
    }

    fun borrarObjetoAVender(objetos: ArrayList<ObjetoModelo>) {
        var retorna = false
        for (obj in objetos) {
            if (objAVender.remove(obj)) {
                retorna = true
            }
        }
        if (!retorna) {
            return
        }
        actualizarObjetosAVender()
        ACTUALIZAR_NPC_VENTAS(this)
    }

    fun borrarTodosObjVender() {
        objAVender.clear()
        actualizarObjetosAVender()
        ACTUALIZAR_NPC_VENTAS(this)
    }

    fun tieneObjeto(idModelo: Int): Boolean {
        for (OM in objAVender) {
            if (OM.id == idModelo) {
                return false
            }
        }
        return true
    }

    init { // super();
        setAccesorios(arma, sombrero, capa, mascota, escudo)
        this.foto = foto
        _preguntaID = preguntaID
        this.nombre = nombre
        if (!objVender.isEmpty()) {
            var objModelo: ObjetoModelo?
            for (obj in objVender.split(",".toRegex()).toTypedArray()) {
                try {
                    objModelo = Mundo.getObjetoModelo(obj.toInt())
                    if (objModelo == null) {
                        continue
                    }
                    objAVender.add(objModelo)
                } catch (ignored: Exception) {
                }
            }
            actualizarObjetosAVender()
        }
    }
}