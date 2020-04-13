package variables.personaje

import estaticos.AtlantaMain
import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Constantes
import estaticos.Constantes.getOgrinasPorVotos
import estaticos.Constantes.getTiempoActualEscala
import estaticos.Constantes.getTiempoFechaX
import estaticos.GestorSQL.GET_ABONO
import estaticos.GestorSQL.GET_APODO
import estaticos.GestorSQL.GET_CONTRASEÑA_CUENTA
import estaticos.GestorSQL.GET_OGRINAS_CUENTA
import estaticos.GestorSQL.GET_PREGUNTA_SECRETA
import estaticos.GestorSQL.GET_PRIMERA_VEZ
import estaticos.GestorSQL.GET_RANGO
import estaticos.GestorSQL.GET_REGALO
import estaticos.GestorSQL.GET_RESPUESTA_SECRETA
import estaticos.GestorSQL.GET_ULTIMO_SEGUNDOS_VOTO
import estaticos.GestorSQL.GET_VIP
import estaticos.GestorSQL.GET_VOTOS
import estaticos.GestorSQL.REPLACE_CUENTA_SERVIDOR
import estaticos.GestorSQL.REPLACE_MONTURA
import estaticos.GestorSQL.SET_BANEADO
import estaticos.GestorSQL.SET_OGRINAS_CUENTA
import estaticos.GestorSQL.SET_PRIMERA_VEZ_CERO
import estaticos.GestorSQL.SET_RANGO
import estaticos.GestorSQL.SET_REGALO
import estaticos.GestorSQL.SET_ULTIMO_SEGUNDOS_VOTO
import estaticos.GestorSQL.SET_VERIFICADOR_REFERIDO
import estaticos.GestorSQL.SET_VOTOS
import estaticos.GestorSQL.UPDATE_MENSAJES_CUENTA
import estaticos.GestorSalida.ENVIAR_AlEb_CUENTA_BANEADA_DEFINITIVO
import estaticos.GestorSalida.ENVIAR_AlEk_CUENTA_BANEADA_TIEMPO
import estaticos.GestorSalida.ENVIAR_BN_NADA
import estaticos.GestorSalida.ENVIAR_FA_AGREGAR_AMIGO
import estaticos.GestorSalida.ENVIAR_FD_BORRAR_AMIGO
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.GestorSalida.ENVIAR_iA_AGREGAR_ENEMIGO
import estaticos.GestorSalida.ENVIAR_iD_BORRAR_ENEMIGO
import estaticos.Mundo
import estaticos.Mundo.borrarLasCuentas
import estaticos.Mundo.eliminarPersonaje
import estaticos.Mundo.getCuenta
import estaticos.Mundo.getMontura
import estaticos.Mundo.getObjeto
import servidor.ServidorSocket
import variables.casa.Cofre
import variables.montura.Montura
import variables.montura.Montura.Ubicacion
import variables.objeto.Objeto
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

class Cuenta(val id: Int, val nombre: String, ref: Int) {
    private val _idsAmigos = ArrayList<Int>()
    private val _idsEnemigos = ArrayList<Int>()
    private val _personajes: MutableMap<Int, Personaje?> = TreeMap()
    private val _idsReportes: MutableMap<Byte, ArrayList<Int>> =
        TreeMap()

    // private final Map<Integer, Objeto> _objetosEnBanco = new TreeMap<Integer, Objeto>();
    val establo = ConcurrentHashMap<Int, Montura>()
    val mensajes = ArrayList<String>()
    val banco = Cofre(-1, -1, 0.toShort(), 0.toShort(), 99999)
    var referido = 0
    var _vref = 0
    private var _muteado = false
    var sinco = false
        private set
    var tiempoMuteado: Long = 0
    var horaMuteado: Long = 0
    var ultimoReporte: Long = 0
    private var _ultVotoMilis: Long = 0
    var actualIP = ""
    var ultimaIP = ""
    var ultimaConexion = ""
        private set
    var idioma = "es"
    var socket: ServidorSocket? = null
    var tempPersonaje: Personaje? = null
        private set
    var bloqueado = false
    var intentosFallidosToken = 0

    fun addMensaje(str: String, salvar: Boolean) {
        mensajes.add(str)
        if (salvar) {
            UPDATE_MENSAJES_CUENTA(nombre, stringMensajes())
        }
    }

    fun stringMensajes(): String {
        if (mensajes.isEmpty()) {
            return ""
        }
        val str = StringBuilder()
        for (s in mensajes) {
            if (str.length > 0) str.append("&")
            str.append(s)
        }
        return str.toString()
    }

    val vip: Int
        get() = GET_VIP(nombre).toInt()

    fun cargarInfoServerPersonaje(
        bancoString: String,
        kamasBanco: Long,
        amigos: String,
        enemigos: String,
        establo: String,
        reportes: String,
        ultimaConexion: String,
        mensajesString: String,
        ultimaIP: String,
        vreferido: Int
    ) {
        addKamasBanco(kamasBanco)
        _vref = vreferido
        this.ultimaConexion = ultimaConexion
        this.ultimaIP = ultimaIP
        for (s in bancoString.split(Pattern.quote("|").toRegex()).toTypedArray()) {
            try {
                if (s.isEmpty()) {
                    continue
                }
                val obj = getObjeto(s.toInt()) ?: continue
                banco.addObjetoRapido(obj)
            } catch (ignored: Exception) {
            }
        }
        for (s in amigos.split(";".toRegex()).toTypedArray()) {
            try {
                if (s.isEmpty()) continue
                _idsAmigos.add(s.toInt())
            } catch (ignored: Exception) {
            }
        }
        for (s in enemigos.split(";".toRegex()).toTypedArray()) {
            try {
                if (s.isEmpty()) continue
                _idsEnemigos.add(s.toInt())
            } catch (ignored: Exception) {
            }
        }
        _idsAmigos.trimToSize()
        _idsEnemigos.trimToSize()
        for (s in establo.split(";".toRegex()).toTypedArray()) {
            try {
                if (s.isEmpty()) continue
                val montura = getMontura(s.toInt())
                montura?.let { addMonturaEstablo(it) }
            } catch (ignored: Exception) {
            }
        }
        var i: Byte = 0
        for (s in reportes.split(Pattern.quote("|").toRegex()).toTypedArray()) {
            val array = ArrayList<Int>()
            for (f in s.split(";".toRegex()).toTypedArray()) {
                try {
                    if (f.isEmpty()) continue
                    array.add(f.toInt())
                } catch (ignored: Exception) {
                }
            }
            _idsReportes[i] = array
            i++
        }
        for (s in mensajesString.split("&".toRegex()).toTypedArray()) {
            if (s.isEmpty()) continue
            mensajes.add(s)
        }
        try {
            if (ultimaConexion.isEmpty() || !AtlantaMain.PARAM_BORRAR_CUENTAS_VIEJAS) { // return;
            } else {
                val array = ultimaConexion.split("~".toRegex()).toTypedArray()
                val año = array[0].toInt()
                val mes = array[1].toInt()
                val dia = array[2].toInt()
                val hora = array[3].toInt()
                val minuto = array[4].toInt()
                val minutos = getTiempoFechaX(año, mes, dia, hora, minuto, 60 * 1000)
                if (borrarLasCuentas(minutos)) {
                    Mundo.CUENTAS_A_BORRAR.add(this)
                }
            }
        } catch (ignored: Exception) {
        }
    }

    fun tieneReporte(tipo: Byte, id: Int): Boolean {
        return try {
            _idsReportes[tipo]!!.contains(id)
        } catch (e: Exception) {
            false
        }
    }

    fun listaReportes(): String {
        val str = StringBuilder()
//        for (b in 0..3) {
//            if (str.isNotEmpty()) {
//                str.append("|")
//            }
//            val str2 = StringBuilder()
//            try {
//                if (b > _idsReportes.size) {
//                    for (f in _idsReportes[b.toByte()]!!) {
//                        if (str2.isNotEmpty()) {
//                            str2.append(";")
//                        }
//                        str2.append(f)
//                    }
//                }
//            } catch (ignored: Exception) {
//            }
//            str.append(str2.toString())
//        }
        return str.toString()
    }

    fun addIDReporte(tipo: Byte, id: Int) {
        try {
            _idsReportes.computeIfAbsent(
                tipo
            ) { k: Byte? -> ArrayList() }
            if (!_idsReportes[tipo]!!.contains(id)) {
                _idsReportes[tipo]!!.add(id)
            }
        } catch (ignored: Exception) {
        }
    }

    fun setUltimaConexion() {
        val hoy = Calendar.getInstance()
        val año = hoy[Calendar.YEAR]
        val dia = hoy[Calendar.DAY_OF_MONTH]
        val mes = hoy[Calendar.MONTH] + 1
        val hora = hoy[Calendar.HOUR_OF_DAY]
        val minutos = hoy[Calendar.MINUTE]
        val segundos = hoy[Calendar.SECOND]
        ultimaConexion = "$año~$mes~$dia~$hora~$minutos~$segundos"
    }

    var regalo: String?
        get() = GET_REGALO(nombre)
        set(regalo) {
            SET_REGALO(nombre, regalo!!)
        }

    fun addRegalo(regaloString: String?) {
        val r = StringBuilder()
        r.append(regaloString)
        if (r.length > 0) {
            r.append(",")
        }
        r.append(regaloString)
        regalo = r.toString()
    }

    fun setPrimeraVez() {
        SET_PRIMERA_VEZ_CERO(nombre)
    }

    val primeraVez: Byte
        get() = GET_PRIMERA_VEZ(nombre)

    val contraseña: String
        get() = GET_CONTRASEÑA_CUENTA(nombre)

    val apodo: String
        get() = GET_APODO(nombre)

    val pregunta: String
        get() = GET_PREGUNTA_SECRETA(nombre)

    val respuesta: String
        get() = GET_RESPUESTA_SECRETA(nombre)

    val admin: Int
        get() = GET_RANGO(nombre).toInt()

    val ultimoSegundosVoto: Long
        get() = GET_ULTIMO_SEGUNDOS_VOTO(actualIP, id)

    val votos: Int
        get() = GET_VOTOS(nombre)

    fun setRango(rango: Int) {
        SET_RANGO(nombre, rango)
    }

    fun tiempoRestanteParaVotar(): Int {
        val resta = getTiempoActualEscala(1000 * 60.toLong()) - minutosUltimoVoto
        return if (resta < 0 || resta >= AtlantaMain.MINUTOS_VALIDAR_VOTO) {
            0
        } else {
            (AtlantaMain.MINUTOS_VALIDAR_VOTO - resta).toInt()
        }
    }

    @get:Synchronized
    private val minutosUltimoVoto: Long
        private get() = ultimoSegundosVoto / 60

    @Synchronized
    fun puedeVotar(): Boolean {
        return if (_ultVotoMilis + 3000 > System.currentTimeMillis()) {
            false
        } else tiempoRestanteParaVotar() == 0
    }

    @Synchronized
    fun darOgrinasPorVoto() {
        val votos = votos
        val ogrinasXVotos = getOgrinasPorVotos(votos)
        _ultVotoMilis = System.currentTimeMillis()
        SET_ULTIMO_SEGUNDOS_VOTO(id, actualIP, System.currentTimeMillis() / 1000)
        SET_VOTOS(id, votos + 1)
        SET_OGRINAS_CUENTA(GET_OGRINAS_CUENTA(id) + ogrinasXVotos, id)
        ENVIAR_Im_INFORMACION(tempPersonaje, "1THANKS_FOR_VOTE;$ogrinasXVotos")
    }

    fun actSinco() {
        sinco = true
    }

    val kamasBanco: Long
        get() = banco.kamas

    fun addKamasBanco(kamas: Long) {
        if (kamas == 0L) {
            return
        }
        banco.addKamas(kamas, null)
        // if (_kamas >= AtlantaMain.LIMITE_DETECTAR_FALLA_KAMAS) {
// GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_ADMINS(0, "[EMULADOR-ELBUSTEMU]", "La cuenta " + _nombre
// + " (" + _id
// + ") posee " + _kamas + " en el banco.");
// AtlantaMain.redactarLogServidorln("LA CUENTA " + _nombre + " (" + _id + ") POSSE " +
// _kamas);
// if (!ServidorSocket.JUGADORES_REGISTRAR.contains(_nombre)) {
// ServidorSocket.JUGADORES_REGISTRAR.add(_nombre);
// }
// }
    }

    fun estaMuteado(): Boolean {
        return _muteado
    }

    fun mutear(b: Boolean, tiempo: Int) {
        _muteado = b
        if (tiempo == 0) {
            return
        } else {
            ENVIAR_Im_INFORMACION(tempPersonaje, "1124;$tiempo")
        }
        tiempoMuteado = tiempo * 1000.toLong()
        horaMuteado = System.currentTimeMillis()
    }

    fun stringBancoObjetosBD(): String {
        return banco.analizarObjetoCofreABD()
    }

    val objetosBanco: Collection<Objeto?>
        get() = banco.objetos

    fun addObjetoAlBanco(obj: Objeto?) {
        banco.addObjetoRapido(obj)
    }

    fun setEntradaPersonaje(t: ServidorSocket?) {
        socket = t
    }

    var verificadorReferido: Int
        get() = _vref
        set(i) {
            if (_vref != 1) {
                _vref = i
                SET_VERIFICADOR_REFERIDO(i, ultimaIP)
            }
        }

    fun enLinea(): Boolean {
        return socket != null || tempPersonaje != null
    }

    fun setBaneado(baneado: Boolean, minutos: Int) {
        if (baneado) {
            var tiempoBaneo: Long = -1
            if (minutos > 0) {
                tiempoBaneo = System.currentTimeMillis() + minutos * 60 * 1000
            }
            SET_BANEADO(nombre, tiempoBaneo)
            if (socket != null) {
                if (tiempoBaneo <= -1) {
                    ENVIAR_AlEb_CUENTA_BANEADA_DEFINITIVO(socket!!)
                } else if (tiempoBaneo > System.currentTimeMillis()) {
                    ENVIAR_AlEk_CUENTA_BANEADA_TIEMPO(socket!!, tiempoBaneo)
                }
            }
        } else {
            SET_BANEADO(nombre, 0)
        }
    }

    fun esAbonado(): Boolean {
        return tiempoAbono > 0
    }

    val tiempoAbono: Long
        get() = Math.max(0, GET_ABONO(nombre) - System.currentTimeMillis())

    fun crearPersonaje(
        nombre: String?, clase: Byte, sexo: Byte, color1: Int,
        color2: Int, color3: Int
    ): Personaje? {
        val perso = Personaje.crearPersonaje(nombre, sexo, clase, color1, color2, color3, this) ?: return null
        _personajes[perso.Id] = perso
        return perso
    }

    fun eliminarPersonaje(id: Int) {
        if (!_personajes.containsKey(id)) {
            return
        }
        val perso = _personajes[id] ?: return
        eliminarPersonaje(perso, true)
        _personajes.remove(id)
        redactarLogServidorln(
            "Se ha eliminado el personaje " + perso.nombre + "(" + perso.Id
                    + ") de la cuenta " + nombre + "(" + this.id + ")"
        )
    }

    fun addPersonaje(perso: Personaje) {
        if (_personajes.containsKey(perso.Id)) {
            redactarLogServidorln(
                "Se esta intentado volver agregar a la cuenta, al personaje " + perso
                    .nombre
            )
            return
        }
        _personajes[perso.Id] = perso
    }

    val personajes: Collection<Personaje?>
        get() = _personajes.values

    fun getPersonaje(id: Int): Personaje? {
        return _personajes[id]
    }

    fun setTempPerso(perso: Personaje?) {
        tempPersonaje = perso
    }

    @Synchronized
    fun desconexion() {
        tempPersonaje?.desconectar(true)
        socket = null
        sinco = false
        if (Mundo.SERVIDOR_ESTADO != Constantes.SERVIDOR_OFFLINE) {
            tempPersonaje = null
            REPLACE_CUENTA_SERVIDOR(this, GET_PRIMERA_VEZ(nombre))
            // GestorSQL.UPDATE_CUENTA_LOGUEADO(_id, (byte) 0);
        }
    }

    fun analizarListaAmigosABD(): String {
        val str = StringBuilder()
        for (i in _idsAmigos) {
            if (!str.toString().isEmpty()) {
                str.append(";")
            }
            str.append(i)
        }
        return str.toString()
    }

    fun stringListaEnemigosABD(): String {
        val str = StringBuilder()
        for (i in _idsEnemigos) {
            if (!str.toString().isEmpty()) {
                str.append(";")
            }
            str.append(i)
        }
        return str.toString()
    }

    fun stringListaAmigos(): String {
        val str = StringBuilder()
        for (i in _idsAmigos) {
            val cuenta = getCuenta(i) ?: continue
            str.append("|")
            if (AtlantaMain.PARAM_MOSTRAR_APODO_LISTA_AMIGOS) {
                str.append(cuenta.apodo)
            } else {
                str.append("EMPTY")
            }
            if (!cuenta.enLinea()) {
                continue
            }
            val perso = cuenta.tempPersonaje ?: continue
            str.append(perso.analizarListaAmigos(id))
        }
        return str.toString()
    }

    fun stringListaEnemigos(): String {
        val str = StringBuilder()
        for (i in _idsEnemigos) {
            val cuenta = getCuenta(i) ?: continue
            str.append("|").append(cuenta.apodo)
            if (!cuenta.enLinea()) {
                continue
            }
            val perso = cuenta.tempPersonaje ?: continue
            str.append(perso.analizarListaAmigos(id))
        }
        return str.toString()
    }

    fun addAmigo(id: Int) {
        if (this.id == id) {
            ENVIAR_FA_AGREGAR_AMIGO(tempPersonaje!!, "Ey")
            return
        }
        if (_idsEnemigos.contains(id)) {
            ENVIAR_iA_AGREGAR_ENEMIGO(tempPersonaje!!, "Ea")
            return
        }
        if (!_idsAmigos.contains(id)) {
            _idsAmigos.add(id)
            val amigo = getCuenta(id)
            if (amigo == null) {
                ENVIAR_BN_NADA(tempPersonaje!!)
                return
            }
            ENVIAR_FA_AGREGAR_AMIGO(
                tempPersonaje!!, "K" + amigo.apodo + amigo.tempPersonaje!!
                    .analizarListaAmigos(this.id)
            )
        } else {
            ENVIAR_FA_AGREGAR_AMIGO(tempPersonaje!!, "Ea")
        }
    }

    fun addEnemigo(packet: String?, id: Int) {
        if (this.id == id) {
            ENVIAR_iA_AGREGAR_ENEMIGO(tempPersonaje!!, "Ey")
            return
        }
        if (_idsAmigos.contains(id)) {
            ENVIAR_FA_AGREGAR_AMIGO(tempPersonaje!!, "Ea")
            return
        }
        if (!_idsEnemigos.contains(id)) {
            _idsEnemigos.add(id)
            val enemigo = getCuenta(id)
            if (enemigo == null) {
                ENVIAR_BN_NADA(tempPersonaje!!)
                return
            }
            ENVIAR_iA_AGREGAR_ENEMIGO(
                tempPersonaje!!, "K" + enemigo.apodo + enemigo.tempPersonaje!!
                    .analizarListaEnemigos(this.id)
            )
        } else {
            ENVIAR_iA_AGREGAR_ENEMIGO(tempPersonaje!!, "Ea")
        }
    }

    fun borrarAmigo(id: Int) {
        try {
            _idsAmigos.remove(id)
            ENVIAR_FD_BORRAR_AMIGO(tempPersonaje!!, "K")
        } catch (ignored: Exception) {
        }
    }

    fun borrarEnemigo(id: Int) {
        try {
            _idsEnemigos.remove(id)
            ENVIAR_iD_BORRAR_ENEMIGO(tempPersonaje!!, "K")
        } catch (ignored: Exception) {
        }
    }

    fun esAmigo(id: Int): Boolean {
        return _idsAmigos.contains(id)
    }

    fun esEnemigo(id: Int): Boolean {
        return _idsEnemigos.contains(id)
    }

    fun stringIDsEstablo(): String {
        val str = StringBuilder()
        for (DP in establo.values) {
            REPLACE_MONTURA(DP, false)
            if (str.length > 0) {
                str.append(";")
            }
            str.append(DP.id)
        }
        return str.toString()
    }

    fun addMonturaEstablo(montura: Montura) {
        establo[montura.id] = montura
        montura.ubicacion = Ubicacion.ESTABLO
    }

    fun borrarMonturaEstablo(id: Int): Boolean {
        return establo.remove(id) == null
    }

    init {
        referido = ref
    }
}