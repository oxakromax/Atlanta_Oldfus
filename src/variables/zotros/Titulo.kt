package variables.zotros

import estaticos.GestorSQL.RESTAR_CREDITOS
import estaticos.GestorSQL.RESTAR_OGRINAS
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import variables.personaje.Personaje

class Titulo(
    val id: Int,
    private var _nombre: String,
    private var _creditos: Int,
    private var _ogrinas: Int,
    private var _kamas: Int,
    private val _vender: Boolean,
    private val _valido: Boolean
) {
    fun adquirirTitulo(_perso: Personaje): Boolean {
        if (!_valido) {
            return false
        }
        if (_perso.tieneTitulo(id)) {
            return true
        }
        if (_vender) {
            if (_creditos > 0) {
                return !RESTAR_CREDITOS(_perso.cuenta, _creditos.toLong(), _perso)
            } else if (_ogrinas > 0) {
                return RESTAR_OGRINAS(_perso.cuenta, _ogrinas.toLong(), _perso)
            } else if (_kamas > 0) {
                if (_perso.kamas < _kamas) {
                    ENVIAR_Im_INFORMACION(_perso, "1128;$_kamas")
                    return false
                }
                _perso.addKamas(-_kamas.toLong(), true, true)
            }
        }
        return true
    }

    val precioStr: String
        get() {
            if (_creditos > 0) {
                return "C$_creditos"
            } else if (_ogrinas > 0) {
                return "O$_ogrinas"
            }
            return "K$_kamas"
        }

    fun get_nombre(): String {
        return _nombre
    }

    fun set_nombre(_nombre: String) {
        this._nombre = _nombre
    }

    fun get_creditos(): Int {
        return _creditos
    }

    fun set_creditos(_creditos: Int) {
        this._creditos = _creditos
    }

    fun get_ogrinas(): Int {
        return _ogrinas
    }

    fun set_ogrinas(_ogrinas: Int) {
        this._ogrinas = _ogrinas
    }

    fun get_kamas(): Int {
        return _kamas
    }

    fun set_kamas(_kamas: Int) {
        this._kamas = _kamas
    }

    fun esParaVender(): Boolean {
        return _vender
    }

    fun esValido(): Boolean {
        return _valido
    }

}