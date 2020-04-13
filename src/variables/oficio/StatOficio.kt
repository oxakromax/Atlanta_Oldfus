package variables.oficio

import estaticos.AtlantaMain
import estaticos.Constantes
import estaticos.Formulas.getRandomInt
import estaticos.GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ
import estaticos.GestorSalida.ENVIAR_JN_OFICIO_NIVEL
import estaticos.GestorSalida.ENVIAR_JO_OFICIO_OPCIONES
import estaticos.GestorSalida.ENVIAR_JS_SKILL_DE_OFICIO
import estaticos.GestorSalida.ENVIAR_JX_EXPERINENCIA_OFICIO
import estaticos.GestorSalida.ENVIAR_Ow_PODS_DEL_PJ
import estaticos.Mundo
import variables.mapa.Celda
import variables.mapa.interactivo.ObjetoInteractivo
import variables.mob.GrupoMob
import variables.mob.MobModelo.TipoGrupo
import variables.personaje.Personaje
import java.util.*
import kotlin.math.sqrt

class StatOficio(val posicion: Byte, val oficio: Oficio, exp: Int) {
    var slotsPublico: Byte
    var nivel = 0
        private set
    private var adicional = 0
    private var _experiencia = 0
    private var _trabajosPoderRealizar = ArrayList<Trabajo>()
    private var _esPagable = false
    private var _gratisSiFalla = false
    private var _noProporcRecurso = false
    private var _libroArtesano: Boolean
    private var _tempTrabajo: Trabajo? = null
    fun trabajosARealizar(): ArrayList<Trabajo> {
        return _trabajosPoderRealizar
    }

    // FIXME solo para q se vean todos
    var libroArtesano: Boolean
        get() = _libroArtesano || !AtlantaMain.MODO_HEROICO // FIXME solo para q se vean todos
        set(bo) {
            _libroArtesano = bo
        }

    fun esPagable(): Boolean {
        return _esPagable
    }

    fun esGratisSiFalla(): Boolean {
        return _gratisSiFalla
    }

    fun noProveerRecuerso(): Boolean {
        return _noProporcRecurso
    }

    private fun subirNivel() {
        if (posicion.toInt() == 7) {
            return
        }
        nivel++
        _trabajosPoderRealizar = Constantes.getTrabajosPorOficios(oficio.id, nivel, this)
        _trabajosPoderRealizar.trimToSize()
        adicional = sqrt(nivel / 2.toDouble()).toInt()
    }

    fun stringSKillsOficio(): String {
        val str = StringBuilder()
        for (trabajo in _trabajosPoderRealizar) {
            if (str.isNotEmpty()) {
                str.append(",")
            }
            str.append(trabajo.trabajoID).append("~")
            if (trabajo.esCraft()) {
                str.append(trabajo.casillasMax).append("~").append("0~0~").append(trabajo.suerte)
            } else {
                str.append(trabajo.casillasMin).append("~").append(trabajo.casillasMax).append("~0~")
                    .append(trabajo.tiempo)
            }
        }
        return "|" + oficio.id + ";" + str.toString()
    }

    val exp: Long
        get() = _experiencia.toLong()

    @Synchronized
    fun iniciarTrabajo(
        trabajoID: Int, perso: Personaje?, OI: ObjetoInteractivo?,
        idUnica: Int, celda: Celda?
    ): Boolean {
        for (trabajo in _trabajosPoderRealizar) {
            if (trabajo.trabajoID == trabajoID) {
                _tempTrabajo = trabajo
                // perso.setTrabajo(_tempTrabajo);
                return trabajo.iniciarTrabajo(perso, idUnica, celda)
            }
        }
        // no puede realizara el trabajo
        return false
    }

    @Synchronized
    fun finalizarTrabajo(perso: Personaje): Boolean {
        if (_tempTrabajo == null) {
            return false
        }
        var r = true
        if (!_tempTrabajo!!.esCraft()) { // recolecta
            r = finalizarRecoleccion(perso)
        }
        if (r) {
            _tempTrabajo = null
        }
        return r
        // perso.setTrabajo(_tempTrabajo);
    }

    @Synchronized
    private fun finalizarRecoleccion(perso: Personaje): Boolean {
        if (_tempTrabajo == null) {
            return false
        }
        if (!_tempTrabajo!!.puedeFinalizarRecolecta()) {
            return false
        }
        val protector = Constantes.getProtectorRecursos(_tempTrabajo!!.trabajoID, oficio.id)
        val bProtector = (getRandomInt(1, 100) < AtlantaMain.PROBABILIDAD_PROTECTOR_RECURSOS
                && protector != 0 && nivel >= 20)
        val experiencia = _tempTrabajo!!.expFinalizarRecoleccion
        addExperiencia(perso, experiencia, 2)
        if (bProtector && perso.enLinea()) {
            val nivel = Constantes.getNivelProtector(nivel)
            val grupoMob = GrupoMob(
                perso.mapa, perso.celda.id, protector.toString() + "," + nivel + ","
                        + nivel, TipoGrupo.SOLO_UNA_PELEA, ""
            )
            perso.mapa.iniciarPelea(
                perso, null, perso.celda.id, (-1).toShort(),
                Constantes.PELEA_TIPO_PVM_NO_ESPADA, grupoMob
            )
        } else {
            _tempTrabajo!!.recogerRecolecta()
        }
        return true
    }

    fun addExperiencia(perso: Personaje?, exp: Int, tipo: Int) {
        var exp = exp
        if (posicion.toInt() == 7 || nivel >= AtlantaMain.NIVEL_MAX_OFICIO) {
            return
        }
        when (tipo) {
            Constantes.OFICIO_EXP_TIPO_RECOLECCION -> if (perso!!.realizoMisionDelDia()) {
                val almanax = Mundo.almanaxDelDia
                if (almanax != null && almanax.tipo == Constantes.ALMANAX_BONUS_EXP_OFICIO_RECOLECCION) {
                    exp += exp * almanax.bonus / 100
                }
            }
            Constantes.OFICIO_EXP_TIPO_CRAFT -> if (perso!!.realizoMisionDelDia()) {
                val almanax = Mundo.almanaxDelDia
                if (almanax != null && almanax.tipo == Constantes.ALMANAX_BONUS_EXP_OFICIO_CRAFT) {
                    exp += exp * almanax.bonus / 100
                }
            }
        }
        val exNivel = nivel
        _experiencia += exp
        // _exp = Math.min(_exp + exp, Mundo.getExpOficio()(Bustemu.NIVEL_MAX_OFICIO)._oficio);
        while (_experiencia >= Mundo.getExpOficio(nivel + 1) && nivel < AtlantaMain.NIVEL_MAX_OFICIO) {
            subirNivel()
        }
        if (perso != null && perso.enLinea()) {
            if (nivel > exNivel) {
                ENVIAR_JS_SKILL_DE_OFICIO(perso, this)
                ENVIAR_JN_OFICIO_NIVEL(perso, oficio.id, nivel)
                ENVIAR_JO_OFICIO_OPCIONES(perso, this)
                ENVIAR_Ak_KAMAS_PDV_EXP_PJ(perso)
                ENVIAR_Ow_PODS_DEL_PJ(perso)
            }
            ENVIAR_JX_EXPERINENCIA_OFICIO(perso, this)
        }
    }

    fun getExpString(s: String): String {
        return Mundo.getExpOficio(nivel).toString() + s + _experiencia + s + Mundo.getExpOficio(nivel + 1)
    }

    fun esValidoTrabajo(id: Int): Boolean {
        for (AT in _trabajosPoderRealizar) {
            if (AT.trabajoID == id) {
                return true
            }
        }
        return false
    }

    val opcionBin: Int
        get() {
            var nro = 0
            nro += if (_noProporcRecurso) 4 else 0
            nro += if (_gratisSiFalla) 2 else 0
            nro += if (_esPagable) 1 else 0
            return nro
        }

    fun setOpciones(bin: Int) {
        _noProporcRecurso = bin and 4 == 4
        _gratisSiFalla = bin and 2 == 2
        _esPagable = bin and 1 == 1
    }

    init {
        addExperiencia(null, exp, 0)
        if (_trabajosPoderRealizar.isEmpty()) {
            _trabajosPoderRealizar = Constantes.getTrabajosPorOficios(oficio.id, nivel, this)
            _trabajosPoderRealizar.trimToSize()
        }
        slotsPublico = Constantes.getIngMaxPorNivel(nivel)
        _libroArtesano = false
    }
}