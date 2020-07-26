package estaticos.database

//import com.mysql.jdbc.PreparedStatement
//import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const
import estaticos.AtlantaMain
import estaticos.Constantes
import estaticos.GestorSalida
import estaticos.Mundo
import estaticos.Mundo.Duo
import estaticos.Mundo.Experiencia
import estaticos.Mundo.sigIDPersonaje
import servidor.ServidorServer
import utilidades.comandosAccion
import utilidades.economia.Economia
import variables.casa.Casa
import variables.casa.Cofre
import variables.encarnacion.EncarnacionModelo
import variables.gremio.Gremio
import variables.gremio.MiembroGremio
import variables.gremio.Recaudador
import variables.hechizo.Hechizo
import variables.hechizo.StatHechizo
import variables.mapa.Area
import variables.mapa.Cercado
import variables.mapa.Mapa
import variables.mapa.SubArea
import variables.mapa.interactivo.ObjetoInteractivoModelo
import variables.mapa.interactivo.OtroInteractivo
import variables.mercadillo.Mercadillo
import variables.mercadillo.ObjetoMercadillo
import variables.mision.MisionModelo
import variables.mob.MobModelo
import variables.mob.MobModelo.TipoGrupo
import variables.montura.Montura
import variables.montura.MonturaModelo
import variables.npc.NPC
import variables.npc.NPCModelo
import variables.npc.PreguntaNPC
import variables.npc.RespuestaNPC
import variables.objeto.*
import variables.objeto.ObjetoModelo.CAPACIDAD_STATS
import variables.oficio.Oficio
import variables.pelea.DropMob
import variables.personaje.Clase
import variables.personaje.Cuenta
import variables.personaje.Especialidad
import variables.personaje.Personaje
import variables.ranking.RankingKoliseo
import variables.ranking.RankingPVP
import variables.zotros.*
import java.sql.*
import java.util.*
import java.util.Date
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.math.min


object GestorSQL {
    var LOG_SQL = StringBuilder()
    private var _bdDinamica: Connection? = null
    private var _bdEstatica: Connection? = null
    private var _bdCuentas: Connection? = null
    private var _bdAlterna: Connection? = null
    private var _timerComienzo: Timer? = null
    private var _necesitaCommit: Boolean = false

    private fun cerrarResultado(resultado: ResultSet) {
        try {
            resultado.statement.close()
            resultado.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun exceptionExit(e: Exception) {
        AtlantaMain.redactarLogServidorln("EXCEP EXIT SQL : $e")
        e.printStackTrace()
//        exitProcess(1)
    }

    private fun exceptionNormal(e: Exception, metodo: String) {
        AtlantaMain.redactarLogServidorln("EXCEP NORMAL SQL $metodo: $e")
        e.printStackTrace()
    }

    private fun exceptionModify(e: Exception, consultaSQL: String, metodo: String) {
        AtlantaMain.redactarLogServidorln("EXCEP MODIFY SQL $metodo: $e")
        AtlantaMain.redactarLogServidorln("LINEA MODIFY SQL $metodo: $consultaSQL")
        e.printStackTrace()
    }

    @Throws(Exception::class)
    fun consultaSQL(consultaSQL: String, coneccion: Connection): ResultSet {
        val declaracion = coneccion.prepareStatement(consultaSQL) as PreparedStatement
        val resultado = declaracion.executeQuery()
        declaracion.queryTimeout = 300
        return resultado
    }

    @Throws(Exception::class)
    fun transaccionSQL(consultaSQL: String, conexion: Connection): PreparedStatement {
        val declaracion = conexion.prepareStatement(consultaSQL) as PreparedStatement
        _necesitaCommit = true
        return declaracion
    }

    private fun ejecutarTransaccion(declaracion: PreparedStatement) {
        var ejecutar = 0
        try {
            ejecutar = declaracion.executeUpdate()
        } catch (e: SQLException) {
            AtlantaMain.redactarLogServidorln("EXECUTE UPDATE $declaracion")
            e.printStackTrace()
        }

        val str = declaracion.toString()
        LOG_SQL.append(System.currentTimeMillis()).append(" ").append(str.substring(str.indexOf(":"))).append("\n")
    }

    fun ejecutarBatch(declaracion: PreparedStatement) {
        try {
            declaracion.executeBatch()
            cerrarDeclaracion(declaracion)
        } catch (e: SQLException) {
            AtlantaMain.redactarLogServidorln("EXECUTE UPDATE $declaracion")
            e.printStackTrace()
        }

    }

    private fun cerrarDeclaracion(declaracion: PreparedStatement) {
        try {
            declaracion.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun timerCommit(iniciar: Boolean) {
        if (AtlantaMain.PARAM_AUTO_COMMIT) {
            return
        }
        if (iniciar) {
            _timerComienzo = Timer()
            _timerComienzo!!.schedule(
                object : TimerTask() {
                    override fun run() {
                        if (!_necesitaCommit || AtlantaMain.PARAM_DESHABILITAR_SQL) {
                            return
                        }
                        iniciarCommit(true)
                    }
                },
                (AtlantaMain.SEGUNDOS_TRANSACCION_BD * 1000).toLong(),
                (AtlantaMain.SEGUNDOS_TRANSACCION_BD * 1000).toLong()
            )
        } else if (_timerComienzo != null) {
            _timerComienzo!!.cancel()
        }
    }

    fun iniciarConexion(): Boolean {
        try {
            _bdDinamica = DriverManager.getConnection(
                "jdbc:mysql://" + AtlantaMain.BD_HOST + "/" + AtlantaMain.BD_DINAMICA
                        + "?autoReconnect=true", AtlantaMain.BD_USUARIO, AtlantaMain.BD_PASS
            )
            _bdDinamica?.autoCommit = AtlantaMain.PARAM_AUTO_COMMIT
            _bdEstatica = DriverManager.getConnection(
                "jdbc:mysql://" + AtlantaMain.BD_HOST + "/" + AtlantaMain.BD_ESTATICA
                        + "?autoReconnect=true", AtlantaMain.BD_USUARIO, AtlantaMain.BD_PASS
            )
            _bdEstatica?.autoCommit = AtlantaMain.PARAM_AUTO_COMMIT
            _bdCuentas = DriverManager.getConnection(
                "jdbc:mysql://" + AtlantaMain.BD_HOST + "/" + AtlantaMain.BD_CUENTAS
                        + "?autoReconnect=true", AtlantaMain.BD_USUARIO, AtlantaMain.BD_PASS
            )
            _bdCuentas?.autoCommit = AtlantaMain.PARAM_AUTO_COMMIT
            if (!_bdEstatica!!.isValid(1000) || !_bdDinamica!!.isValid(1000) || !_bdCuentas!!.isValid(1000)) {
                AtlantaMain.redactarLogServidorln("SQLError : Conexion a la BDD invalida")
                return false
            }
            timerCommit(true)
            return true
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln("ERROR SQL INICIAR CONEXION: $e")
            e.printStackTrace()
        }

        return false
    }


    fun conexionAlterna(host: String, database: String, user: String, pass: String): Boolean {
        return try {
            if (_bdAlterna != null) {
                try {
                    _bdAlterna!!.close()
                } catch (ignored: Exception) {
                }

            }
            _bdAlterna = DriverManager.getConnection(
                "jdbc:mysql://${Constantes.iptoken}/${Constantes.databasetoken}",
                Constantes.usertoken,
                Constantes.tokenpw
            )
            _bdAlterna?.autoCommit = AtlantaMain.PARAM_AUTO_COMMIT
            _bdAlterna?.isValid(5000) ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun recambiarAlterna(host: String?, database: String?, user: String?, pass: String?): Boolean {
        return try {
            _bdAlterna?.close()
            _bdAlterna = DriverManager.getConnection(
                "jdbc:mysql://${host}/${database}",
                user,
                pass
            )
            _bdAlterna?.autoCommit = AtlantaMain.PARAM_AUTO_COMMIT
            _bdAlterna?.isValid(5000) ?: false
        } catch (e: Exception) {
            false
        }
    }


    fun iniciarCommit(reiniciando: Boolean) {
        if (_bdDinamica!!.isClosed || _bdEstatica!!.isClosed || _bdCuentas!!.isClosed) {
            cerrarConexion()
            iniciarConexion()
            try {
                Thread.sleep(500)
            } catch (e: Exception) {
            }
        }
        if (AtlantaMain.PARAM_AUTO_COMMIT) {
            return
        }
        try {
            if (reiniciando) {
                if (_bdDinamica!!.isClosed || _bdEstatica!!.isClosed || _bdCuentas!!.isClosed) {
                    cerrarConexion()
                    iniciarConexion()
                }
            }
            _necesitaCommit = false
            try {
                _bdCuentas!!.commit()
            } catch (e: Exception) {
                AtlantaMain.redactarLogServidorln("EXCEPTION COMMIT ACCOUNTS: $e")
                e.printStackTrace()
            }

            try {
                _bdEstatica!!.commit()
            } catch (e: Exception) {
                AtlantaMain.redactarLogServidorln("EXCEPTION COMMIT STATIC: $e")
                e.printStackTrace()
            }

            try {
                _bdDinamica!!.commit()
            } catch (e: Exception) {
                AtlantaMain.redactarLogServidorln("EXCEPTION COMMIT DYNAMIC: $e")
                e.printStackTrace()
            }

//            Thread.sleep(1000)
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln("SQL ERROR COMENZAR TRANSACCIONES: $e")
            e.printStackTrace()
        }

    }

    private fun cerrarConexion() {
        iniciarCommit(false)
        try {
            _bdCuentas!!.close()
            _bdDinamica!!.close()
            _bdEstatica!!.close()
            AtlantaMain.redactarLogServidorln("########## CONEXIONES SQL CERRADAS ##########")
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln("SQL ERROR CERRAR CONEXION: $e")
            e.printStackTrace()
        }

    }

    fun tokenInicio(str: String): Boolean {
        var token = ""
        val resultado = _bdAlterna?.let {
            consultaSQL(
                "SELECT `token` from `tokens` where `token` = \"$str\";",
                it
            )
        }
        if (resultado != null) {
            if (resultado.first()) {
                token = resultado.getString("token")
                cerrarResultado(resultado)
                return token == str
            }
        }
        return false
    }

    fun GET_TOKEN(cuenta: Cuenta?): String {
        if (cuenta == null) {
            return ""
        }
        try {
            var token = ""
            val resultado = _bdDinamica?.let {
                consultaSQL(
                    "SELECT `token` from `tokens` where `id` = ${cuenta.id};",
                    it
                )
            }
            if (resultado != null) {
                if (resultado.first()) {
                    token = resultado.getString("token")
                    cerrarResultado(resultado)
                    return token
                }
            }
            return token
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln("Error en obtener un token")
        }
        return ""
    }

    fun ES_IP_BANEADA(ip: String): Boolean {
        var b = false
        val consultaSQL = "SELECT `ip` FROM `banip` WHERE `ip` = '$ip';"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdCuentas!!
            )
            if (resultado.first()) {
                b = true
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return b
    }

    fun LISTA_BAN_IP(): String {
        val str = StringBuilder()
        try {
            val resultado = consultaSQL(
                "SELECT `ip` FROM `banip`;",
                _bdCuentas!!
            )
            while (resultado.next()) {
                if (str.isNotEmpty()) {
                    str.append(", ")
                }
                str.append(resultado.getString("ip"))
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return str.toString()
    }

    fun INSERT_BAN_IP(ip: String): Boolean {
        // return true;
        AtlantaMain.redactarLogServidorln("IP BANEADA : $ip")
        val consultaSQL = "INSERT INTO `banip` (`ip`) VALUES (?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdCuentas!!
            )
            declaracion.setString(1, ip)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return false
    }

    fun DELETE_BAN_IP(ip: String) {
        val consultaSQL = "DELETE FROM `banip` WHERE `ip` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdCuentas!!
            )
            declaracion.setString(1, ip)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun GET_VIP(cuenta: String): Byte {
        var b: Byte = 0
        val consultaSQL = "SELECT * FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdCuentas!!
            )
            while (resultado.next()) {
                b = try {
                    resultado.getByte("vip")
                } catch (e: Exception) {
                    1
                }

            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            // exceptionNormal(e, "");
        }

        return b
    }

    fun GET_RANGO(cuenta: String): Byte {
        var b: Byte = 0
        val consultaSQL = "SELECT `rango` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdCuentas!!
            )
            while (resultado.next()) {
                try {
                    b = resultado.getByte("rango")
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return b
    }

    // public static String GET_ULTIMA_IP(final String cuenta) {
    // String str = "";
    // String consultaSQL = "SELECT `ultimaIP` FROM `cuentas` WHERE `cuenta` = '" + cuenta + "' ;";
    // try {
    // final ResultSet resultado = consultaSQL(consultaSQL, _bdCuentas);
    // while (resultado.next()) {
    // str = resultado.getString("ultimaIP");
    // }
    // cerrarResultado(resultado);
    // } catch (final Exception e) {
    // Bustemu.redactarLogServidorln("ERROR SQL GET ULTIMA IP: " + e.toString());
    // Bustemu.redactarLogServidorln("LINEA SQL: " + consultaSQL);
    // e.printStackTrace();
    // }
    // return str;
    // }
    fun GET_ID_WEB(cuenta: String): Int {
        var str = -1
        val consultaSQL = "SELECT `idWeb` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdCuentas!!
            )
            if (resultado.first()) {
                str = resultado.getInt("idWeb")
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return str
    }

    fun GET_APODO(cuenta: String): String {
        var str = ""
        val consultaSQL = "SELECT `apodo` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdCuentas!!
            )
            if (resultado.first()) {
                str = resultado.getString("apodo")
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return str
    }

    fun GET_ABONO(cuenta: String): Long {
        var l: Long = 0
        val consultaSQL = "SELECT `abono` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdCuentas!!
            )
            if (resultado.first()) {
                l = resultado.getLong("abono")
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return l
    }

    fun SET_ABONO(abono: Long, cuentaID: Int) {
        val consultaSQL = "UPDATE `cuentas` SET `abono`='$abono' WHERE `id`= '$cuentaID'"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdCuentas!!
            )
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun GET_OGRINAS_CUENTA(cuentaID: Int): Int {
        if (AtlantaMain.PARAM_NO_USAR_OGRINAS) {
            return 9999999
        }
        var i = 0
        val consultaSQL = "SELECT `ogrinas` FROM `cuentas` WHERE `id` = '$cuentaID' ;"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdCuentas!!
            )
            if (resultado.first()) {
                i = resultado.getInt("ogrinas")
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return i
    }

    fun GET_REFERIDOS_CUENTA(cuentaID: Int): Int {
        if (AtlantaMain.PARAM_NO_USAR_OGRINAS) {
            return 9999999
        }
        var i = 0
        val consultaSQL = "SELECT `referido` FROM `cuentas` WHERE `id` = '$cuentaID' ;"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdCuentas!!
            )
            if (resultado.first()) {
                i = resultado.getInt("referido")
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return i
    }

    fun GET_VREFERIDOS_CUENTA_POR_IP(Ip: String): Int {
        if (AtlantaMain.PARAM_NO_USAR_OGRINAS) {
            return 9999999
        }
        var i = ""
        val consultaSQL = "SELECT `vreferido` FROM `cuentas_servidor` WHERE `ultimaIP` = \"$Ip\" and vreferido=1 ;"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdDinamica!!
            )
            if (resultado.first()) {
                i = resultado.getString("vreferido")
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return if (i.equals("1", ignoreCase = true)) {
            1
        } else {
            0
        }
    }

    fun GET_CREDITOS_CUENTA(cuentaID: Int): Int {
        if (AtlantaMain.PARAM_NO_USAR_CREDITOS) {
            return 9999999
        }
        var i = 0
        val consultaSQL = "SELECT `creditos` FROM `cuentas` WHERE `id` = '$cuentaID' ;"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdCuentas!!
            )
            if (resultado.first()) {
                i = resultado.getInt("creditos")
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return i
    }

    fun SET_OGRINAS_CUENTA(ogrinas: Int, cuentaID: Int) {
        if (AtlantaMain.PARAM_NO_USAR_OGRINAS) {
            return
        }
        val consultaSQL = "UPDATE `cuentas` SET `ogrinas` = '$ogrinas' WHERE `id` = '$cuentaID'"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdCuentas!!
            )
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun SET_VERIFICADOR_REFERIDO(i: Int, ip: String) {
        if (AtlantaMain.PARAM_NO_USAR_OGRINAS) {
            return
        }
        val consultaSQL = "UPDATE `cuentas_servidor` SET `vreferido` = $i WHERE `ultimaIP` = \"$ip\""
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun SET_CREDITOS_CUENTA(creditos: Int, cuentaID: Int) {
        if (AtlantaMain.PARAM_NO_USAR_CREDITOS) {
            return
        }
        val consultaSQL = "UPDATE `cuentas` SET `creditos` = '$creditos' WHERE `id` = '$cuentaID'"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdCuentas!!
            )
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun ADD_OGRINAS_CUENTA(ogrinas: Long, cuentaID: Int) {
        if (AtlantaMain.PARAM_NO_USAR_OGRINAS) {
            return
        }
        val exOgrinas = GET_OGRINAS_CUENTA(cuentaID)
        val consultaSQL = ("UPDATE `cuentas` SET `ogrinas` = '" + (ogrinas + exOgrinas) + "' WHERE `id` = '" + cuentaID
                + "'")
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdCuentas!!
            )
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    private fun ADD_CREDITOS_CUENTA(creditos: Long, cuentaID: Int) {
        if (AtlantaMain.PARAM_NO_USAR_CREDITOS) {
            return
        }
        val exOgrinas = GET_CREDITOS_CUENTA(cuentaID)
        val consultaSQL =
            ("UPDATE `cuentas` SET `creditos` = '" + (creditos + exOgrinas) + "' WHERE `id` = '" + cuentaID
                    + "'")
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdCuentas!!
            )
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun RESTAR_OGRINAS(cuenta: Cuenta, restar: Long, perso: Personaje?): Boolean {
        if (AtlantaMain.PARAM_NO_USAR_OGRINAS) {
            return true
        }
        var resto = false
        try {
            var consultaSQL = "SELECT `ogrinas` FROM `cuentas` WHERE `id` = '" + cuenta.id + "' ;"
            val ogrinas = GET_OGRINAS_CUENTA(cuenta.id)
            if (restar <= 0 || ogrinas < restar) {
                if (perso != null) {
                    GestorSalida.ENVIAR_Im_INFORMACION(
                        perso,
                        "1ERROR_BUY_WITH_OGRINES;" + (restar - ogrinas)
                    )
                }
                return false
            }
            consultaSQL = "UPDATE `cuentas` SET `ogrinas` = ? WHERE `id` = '" + cuenta.id + "';"
            try {
                val declaracion = transaccionSQL(
                    consultaSQL,
                    _bdCuentas!!
                )
                declaracion.setLong(1, ogrinas - restar)
                ejecutarTransaccion(declaracion)
                cerrarDeclaracion(declaracion)
                resto = true
            } catch (e: Exception) {
                exceptionModify(e, consultaSQL, "")
                return false
            }

            if (perso != null) {
                GestorSalida.ENVIAR_Im_INFORMACION(
                    perso, "1EXITO_BUY_VIP;" + Economia.formatNumber(ogrinas - restar) + "~"
                            + AtlantaMain.NOMBRE_SERVER
                )
            }
            // GestorSalida.ENVIAR_bOA_ACTUALIZAR_PANEL_OGRINAS(out, ogrinas - restar);
            return true
        } catch (e: Exception) {
            if (resto) {
                ADD_OGRINAS_CUENTA(restar, cuenta.id)
            }
            exceptionNormal(
                e,
                "RESTAR OGRINAS A " + cuenta.nombre + ", OGRINAS " + restar + ", LE RESTO? " + resto
            )
        }

        return false
    }

    fun RESTAR_CREDITOS(cuenta: Cuenta, restar: Long, perso: Personaje): Boolean {
        if (AtlantaMain.PARAM_NO_USAR_CREDITOS) {
            return false
        }
        var resto = false
        try {
            var consultaSQL = "SELECT `creditos` FROM `cuentas` WHERE `id` = '" + cuenta.id + "' ;"
            val creditos = GET_CREDITOS_CUENTA(cuenta.id)
            if (restar <= 0 || creditos < restar) {
                GestorSalida.ENVIAR_Im_INFORMACION(
                    perso,
                    "1ERROR_BUY_VIP;" + (restar - creditos)
                )
                return true
            }
            consultaSQL = "UPDATE `cuentas` SET `creditos` = ? WHERE `id` = '" + cuenta.id + "';"
            try {
                val declaracion = transaccionSQL(
                    consultaSQL,
                    _bdCuentas!!
                )
                declaracion.setLong(1, creditos - restar)
                ejecutarTransaccion(declaracion)
                cerrarDeclaracion(declaracion)
                resto = true
            } catch (e: Exception) {
                exceptionModify(e, consultaSQL, "")
                return true
            }

            GestorSalida.ENVIAR_Im_INFORMACION(
                perso, "1EXITO_BUY_WITH_CREDITS;" + (creditos - restar) + "~"
                        + AtlantaMain.NOMBRE_SERVER
            )
            // GestorSalida.ENVIAR_bOA_ACTUALIZAR_PANEL_OGRINAS(out, ogrinas - restar);
            return false
        } catch (e: Exception) {
            if (resto) {
                ADD_CREDITOS_CUENTA(restar, cuenta.id)
            }
            exceptionNormal(
                e,
                "RESTAR CREDITOS A " + cuenta.nombre + ", CREDITOS " + restar + ", LE RESTO? " + resto
            )
        }

        return true
    }

    fun GET_CONTRASEÑA_CUENTA(cuenta: String): String {
        var str = ""
        val consultaSQL = "SELECT `contraseña` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdCuentas!!
            )
            if (resultado.first()) {
                try {
                    str = resultado.getString("contraseña")
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return str
    }

    fun CAMBIAR_CONTRASEÑA_CUENTA(contraseña: String, cuentaID: Int) {
        val consultaSQL = "UPDATE `cuentas` SET `contraseña`= ? WHERE `id`= ?"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdCuentas!!
            )
            declaracion.setString(1, contraseña)
            declaracion.setInt(2, cuentaID)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun GET_PREGUNTA_SECRETA(cuenta: String): String {
        var str = ""
        val consultaSQL = "SELECT `pregunta` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdCuentas!!
            )
            if (resultado.first()) {
                try {
                    str = resultado.getString("pregunta")
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return str
    }

    fun GET_BANEADO(cuenta: String): Long {
        var i: Long = 0
        try {
            val consultaSQL = "SELECT `baneado` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
            val resultado = consultaSQL(
                consultaSQL,
                _bdCuentas!!
            )
            if (resultado.first()) {
                i = resultado.getLong("baneado")
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return i
    }

    fun GET_RESPUESTA_SECRETA(cuenta: String): String {
        var str = ""
        val consultaSQL = "SELECT `respuesta` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdCuentas!!
            )
            if (resultado.first()) {
                try {
                    str = resultado.getString("respuesta")
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return str
    }

    fun SET_RANGO(cuenta: String, rango: Int) {
        val consultaSQL = "UPDATE `cuentas` SET `rango` = ? WHERE `cuenta` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdCuentas!!
            )
            declaracion.setInt(1, rango)
            declaracion.setString(2, cuenta)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    // public static void SET_ULTIMA_IP(final String cuenta, final String ip) {
    // String consultaSQL = "UPDATE `cuentas` SET `ultimaIP` = ? WHERE `cuenta` = ?;";
    // try {
    // final PreparedStatement declaracion = transaccionSQL(consultaSQL, _bdCuentas);
    // declaracion.setString(1, ip);
    // declaracion.setString(2, cuenta);
    // ejecutarTransaccion(declaracion);
    // cerrarDeclaracion(declaracion);
    // } catch (final Exception e) {
    // Bustemu.redactarLogServidorln("ERROR SQL SET ULTIMA IP: " + e.toString());
    // Bustemu.redactarLogServidorln("LINEA SQL: " + consultaSQL);
    // e.printStackTrace();
    // }
    // }
    fun SET_BANEADO(cuenta: String, baneado: Long) {
        val consultaSQL = "UPDATE `cuentas` SET `baneado` = '$baneado' WHERE `cuenta` = '$cuenta';"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdCuentas!!
            )
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun GET_CUENTAS_CONECTADAS_IP(ip: String): Int {
        var i = 0
        val consultaSQL = "SELECT * FROM `cuentas` WHERE `ultimaIP` = '$ip' AND `logeado` = 1 ;"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdCuentas!!
            )
            while (resultado.next()) {
                i++
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return i
    }

    fun GET_CUENTA_ID_ALTERNA(cuenta: String, pass: String): Int {
        var i = -1
        val consultaSQL = "SELECT * FROM `accounts` WHERE `account`='$cuenta' AND `pass`='$pass';"
        try {
            val resultado = _bdAlterna?.let { consultaSQL(consultaSQL, it) } ?: return -1
            while (resultado.next()) {
                return resultado.getInt("guid")
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
        }
        return i
    }

    fun GET_PERSONAJES_ALTERNA(idacc: Int, cuenta: Cuenta) {
        val consultaSQL = "SELECT * FROM `players` WHERE `account`=$idacc"
        try {
            val resultado = _bdAlterna?.let { consultaSQL(consultaSQL, it) } ?: return
            while (resultado.next()) {
                try {
                    val statsBase = TreeMap<Int, Int>()
                    statsBase[Constantes.STAT_MAS_VITALIDAD] = resultado.getInt("vitalite")
                    statsBase[Constantes.STAT_MAS_FUERZA] = resultado.getInt("force")
                    statsBase[Constantes.STAT_MAS_SABIDURIA] = resultado.getInt("sagesse")
                    statsBase[Constantes.STAT_MAS_INTELIGENCIA] = resultado.getInt("intelligence")
                    statsBase[Constantes.STAT_MAS_SUERTE] = resultado.getInt("chance")
                    statsBase[Constantes.STAT_MAS_AGILIDAD] = resultado.getInt("agilite")
                    val statsScroll = TreeMap<Int, Int>()
                    try {
                        for (s in resultado.getString("parcho").split(";")) {
                            val i = s.split(",")
                            statsScroll[i[0].toInt()] = i[1].toInt()
                        }
                    } catch (e: Exception) {
                    }
                    var nombre = resultado.getString("name")
                    while (Mundo.getPersonajePorNombre(nombre) != null) {
                        nombre += "${(Math.random() * 10).toInt()}"
                    }
                    val perso = Personaje(
                        sigIDPersonaje(),
                        nombre,
                        resultado.getByte(
                            "sexe"
                        ),
                        resultado.getByte("class"),
                        resultado.getInt("color1"),
                        resultado.getInt("color2"),
                        resultado.getInt(
                            "color3"
                        ),
                        resultado.getLong("kamas"),
                        resultado.getInt("spellboost"),
                        resultado.getInt("capital"),
                        resultado.getInt("energy"),
                        resultado.getShort("level"),
                        resultado.getLong("xp"),
                        resultado.getInt("size"),
                        resultado.getInt("gfx"),
                        resultado.getByte("alignement"),
                        cuenta.id,
                        statsBase,
                        statsScroll,
                        true,
                        false,
                        "*#%!pi\$:?^¡@~",
                        resultado.getShort("map"),
                        resultado.getShort("cell"),
                        "",
                        resultado.getByte(
                            "pdvper"
                        ).toInt(),
                        resultado.getString("spells"),
                        resultado.getString("savepos"),
                        resultado.getString("jobs"),
                        0,
                        -1,
                        resultado.getInt("honor"),
                        resultado.getInt(
                            "deshonor"
                        ),
                        1,
                        "164,528,844,935,951,1158,1242,1841,2191,3022,3250,4263,4739,5295,6137,6855,6954,7411,8037,8088,8125,8163,8437,8785,9454,10297,10304,10317,10349,10643,11170,11210",
                        0,
                        "",
                        false,
                        8200,
                        8,
                        0,
                        1,
                        "",
                        "",
                        "",
                        "",
                        "ALL",
                        0,
                        "",
                        0,
                        "",
                        -1,
                        "0,0"
                    )
                    if (perso.cuenta != null) {
                        Mundo.addPersonaje(perso)
                    }
                    val objetos = resultado.getString("objets").replace("|", ",")
                    val consultaSQL2 =
                        "select * from `world.entity.objects` where id in (${objetos.substring(0, objetos.lastIndex)});"
                    val resultado2 = consultaSQL(consultaSQL2, _bdAlterna!!)
                    while (resultado2.next()) {
                        val objnopermitidos = arrayListOf<Int>(
                            22098,
                            17029,
                            17031,
                            17030,
                            17018,
                            17019,
                            17020,
                            17021,
                            9233,
                            9234,
                            10866,
                            17023,
                            17024,
                            17025,
                            17026,
                            17027,
                            17028,
                            17756,
                            10802,
                            17000,
                            17001,
                            17002,
                            17003,
                            17004,
                            17005,
                            17006,
                            17007,
                            17008,
                            17009,
                            17010,
                            17011,
                            17757,
                            7708,
                            7709,
                            7710,
                            7711,
                            7712,
                            7713,
                            8155,
                            9624,
                            7525,
                            8167,
                            9363,
                            9552,
                            9553,
                            9554,
                            9555,
                            9556,
                            9964,
                            17762,
                            10140,
                            17759,
                            9362,
                            10564,
                            17758,
                            17760,
                            17761,
                            17763,
                            17750,
                            17751,
                            17752,
                            17767,
                            17922,
                            17755,
                            972,
                            8815,
                            17743,
                            17748,
                            17749,
                            17754,
                            17915,
                            17917,
                            17916,
                            17919,
                            17918,
                            17742,
                            17744,
                            17745,
                            17746,
                            17747,
                            17908,
                            17909,
                            17910,
                            17911,
                            17912,
                            17913,
                            17914,
                            17920,
                            17921,
                            8814,
                            10862,
                            22040,
                            22045,
                            22046,
                            22047,
                            22056,
                            22057,
                            22058,
                            22059,
                            22063,
                            22064,
                            22076,
                            22085,
                            22086,
                            22087,
                            22088,
                            22089,
                            22090,
                            22091,
                            22092,
                            22093,
                            22094,
                            22095,
                            22096,
                            22097,
                            22099,
                            7521,
                            7526,
                            7527,
                            8164,
                            8165,
                            8166,
                            15001,
                            22027,
                            22028,
                            22029,
                            22030,
                            22031,
                            22032,
                            22033,
                            22034,
                            22035,
                            22036,
                            22037,
                            22038,
                            22039,
                            22041,
                            22042,
                            22043,
                            22044,
                            22049,
                            22050,
                            22051,
                            22053,
                            22054,
                            22060,
                            22061,
                            22062,
                            22065,
                            22066,
                            22067,
                            22068,
                            22070,
                            22071,
                            22072,
                            22073,
                            22074,
                            22075,
                            22077,
                            22078,
                            22079,
                            22080,
                            22081,
                            22082,
                            22083,
                            22084,
                            19072
                        )
                        try {
                            val rs = resultado2
                            val om = Mundo.getObjetoModelo(rs.getInt("template")) ?: continue
                            if (om.id in objnopermitidos) continue
                            val obj =
                                om.crearObjeto(rs.getInt("quantity"), rs.getByte("position"), CAPACIDAD_STATS.RANDOM)
                            obj.convertirStringAStats(rs.getString("stats"))
                            Mundo.addObjeto(obj, false)
                            obj.setIDObjevivo(0) // Se saca el objevivo de encima
                            perso.addObjetoConOAKO(obj, true)
                        } catch (e: Exception) {
                        }
                    }
                    perso.salvar()
                    cerrarResultado(resultado2)
                } catch (e: Exception) {
//                    if (cuenta.enLinea()) {
//                        cuenta.socket?.personaje?.enviarmensajeRojo("Hubo un error en tu recuperación de personaje, por favor contactate con un admin")
//                        cerrarResultado(resultado)
//                        return
//                    }
                }
            }
            cerrarResultado(resultado)
            ELIMINAR_CUENTA_ALTERNA(idacc)
            // Terminamos con lo del personaje, ahora vamos a buscar los items y kk del banco
            val consultaSQL3 = "select * from `banks` where id = $idacc"
            val resultado3 = consultaSQL(consultaSQL3, _bdAlterna!!)
            val rs = resultado3
            while (rs.next()) {
                cuenta.addKamasBanco(rs.getLong("kamas"))
                val objetosB = rs.getString("items").replace("|", ",")
                val consultaSQL4 =
                    "select * from `world.entity.objects` where id in (${objetosB.substring(0, objetosB.lastIndex)});"
                val resultado4 = consultaSQL(consultaSQL4, _bdAlterna!!)
                val rs2 = resultado4
                while (rs2.next()) {
                    try {
                        val om = Mundo.getObjetoModelo(rs2.getInt("template")) ?: continue
                        val obj =
                            om.crearObjeto(rs2.getInt("quantity"), rs2.getByte("position"), CAPACIDAD_STATS.RANDOM)
                        obj.convertirStringAStats(rs2.getString("stats"))
                        Mundo.addObjeto(obj, false)
                        cuenta.banco.addObjetoRapido(obj)
                    } catch (e: Exception) {
                        //
                    }
                }
                cerrarResultado(resultado4)
            }
            cerrarResultado(resultado3)
        } catch (e: Exception) {
        }
    }

    fun ELIMINAR_CUENTA_ALTERNA(id: Int) {
        val consultaSQL = "DELETE FROM `accounts` WHERE `guid`=?;"
        try {
            val declaracion = _bdAlterna?.let {
                transaccionSQL(
                    consultaSQL,
                    it
                )
            } ?: return
            declaracion.setInt(1, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }
    }

    // public static void UPDATE_CUENTA_LOGUEADO(final int cuentaID, final byte log) {
    // String consultaSQL = "UPDATE `cuentas` SET `logeado`= " + log + " WHERE `id`=" + cuentaID +
    // ";";
    // try {
    // final PreparedStatement declaracion = transaccionSQL(consultaSQL, _bdCuentas);
    // ejecutarTransaccion(declaracion);
    // cerrarDeclaracion(declaracion);
    // } catch (final Exception e) {
    // exceptionModify(e, consultaSQL, "");
    // }
    // }
    //
    // public static void UPDATE_TODAS_CUENTAS_CERO() {
    // final String cuentas = Mundo.strCuentasOnline();
    // if (cuentas.isEmpty()) {
    // return;
    // }
    // String consultaSQL = "UPDATE `cuentas` SET `logeado`= 0 WHERE `id` IN (" + cuentas + ");";
    // try {
    // final PreparedStatement declaracion = transaccionSQL(consultaSQL, _bdCuentas);
    // ejecutarTransaccion(declaracion);
    // cerrarDeclaracion(declaracion);
    // } catch (final Exception e) {
    // exceptionModify(e, consultaSQL, "");
    // }
    // }
    fun CARGAR_CUENTA_POR_ID(id: Int) {
        val consultaSQL = "SELECT * FROM `cuentas` WHERE `id` = $id;"
        try {
            if (Mundo.getCuenta(id) != null) {
                return
            }
            val resultado = consultaSQL(
                consultaSQL,
                _bdCuentas!!
            )
            if (resultado.first()) {
                val cuenta = Cuenta(
                    resultado.getInt("id"),
                    resultado.getString("cuenta"),
                    resultado.getInt("referido")
                )
                Mundo.addCuenta(cuenta)
                REPLACE_CUENTA_SERVIDOR(cuenta, 1.toByte())
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

    }

    fun CARGAR_DB_CUENTAS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `cuentas`;",
                _bdCuentas!!
            )
            while (resultado.next()) {
                val cuenta = Cuenta(
                    resultado.getInt("id"),
                    resultado.getString("cuenta"),
                    resultado.getInt("referido")
                )
                Mundo.addCuenta(cuenta)
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun GET_REGALO(cuenta: String): String {
        var str = ""
        val consultaSQL = "SELECT `regalo` FROM `cuentas_servidor` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdDinamica!!
            )
            if (resultado.first()) {
                try {
                    str = resultado.getString("regalo")
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return str
    }

    fun CARGAR_CAPTCHAS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `captchas`;",
                _bdCuentas!!
            )
            while (resultado.next()) {
                Mundo.CAPTCHAS.add(resultado.getString("captcha") + "|" + resultado.getString("respuesta"))
            }
            cerrarResultado(resultado)
        } catch (ignored: Exception) {
        }

    }

    fun GET_ULTIMO_SEGUNDOS_VOTO(ip: String, cuentaID: Int): Long {
        var time: Long = 0
        var consultaSQL = ("SELECT `ultimoVoto` FROM `cuentas` WHERE `ultimaIP` = '" + ip
                + "' ORDER BY `ultimoVoto` DESC ;")
        try {
            var resultado = consultaSQL(
                consultaSQL,
                _bdCuentas!!
            )
            while (resultado.next()) {
                try {
                    if (resultado.getString("ultimoVoto").isEmpty()) {
                        continue
                    }
                    time = resultado.getLong("ultimoVoto")
                    break
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
            consultaSQL = "SELECT `ultimoVoto` FROM `cuentas` WHERE `id` = '$cuentaID' ;"
            resultado = consultaSQL(
                consultaSQL,
                _bdCuentas!!
            )
            while (resultado.next()) {
                if (resultado.getString("ultimoVoto").isEmpty()) {
                    continue
                }
                time = max(time, resultado.getLong("ultimoVoto"))
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return time
    }

    fun GET_VOTOS(cuenta: String): Int {
        var i = 0
        val consultaSQL = "SELECT `votos` FROM `cuentas` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdCuentas!!
            )
            if (resultado.first()) {
                try {
                    i = resultado.getInt("votos")
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return i
    }

    fun SET_ULTIMO_SEGUNDOS_VOTO(cuentaID: Int, ip: String, time: Long) {
        val consultaSQL = "UPDATE `cuentas` SET `ultimoVoto` = ? WHERE `id` = ? OR `ultimaIP` = ? ;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdCuentas!!
            )
            declaracion.setLong(1, time)
            declaracion.setInt(2, cuentaID)
            declaracion.setString(3, ip)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun RECUPERAR_OBJETOS(id: Int): String {
        var str = ""
        val consultaSQL = "select `objetos` FROM `personajes_r` WHERE `id`=$id;"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdDinamica!!
            )

            while (resultado.next()) {
                try {
                    str = resultado.getString("objetos")
                    println("Objetos: $str")
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return str
    }

    fun SET_VOTOS(cuentaID: Int, votos: Int) {
        val consultaSQL = "UPDATE `cuentas` SET `votos` = ? WHERE `id` = ? ;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdCuentas!!
            )
            declaracion.setInt(1, votos)
            declaracion.setInt(2, cuentaID)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun CARGAR_CUENTAS_SERVER_PERSONAJE() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `cuentas_servidor`;",
                _bdDinamica!!
            )
            while (resultado.next()) {
                try {
                    Mundo.getCuenta(resultado.getInt("id"))
                        ?.cargarInfoServerPersonaje(
                            resultado.getString("objetos"),
                            resultado.getLong("kamas"),
                            resultado.getString("amigos"),
                            resultado.getString("enemigos"),
                            resultado.getString(
                                "establo"
                            ),
                            resultado.getString("reportes"),
                            resultado.getString("ultimaConexion"),
                            resultado.getString(
                                "mensajes"
                            ),
                            resultado.getString("ultimaIP"),
                            resultado.getInt("vreferido")
                        )
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

    }

    fun GET_PRIMERA_VEZ(cuenta: String): Byte {
        var b: Byte = 0
        val consultaSQL = "SELECT `primeraVez` FROM `cuentas_servidor` WHERE `cuenta` = '$cuenta' ;"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdDinamica!!
            )
            if (resultado.first()) {
                try {
                    b = resultado.getByte("primeraVez")
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return b
    }

    fun SET_PRIMERA_VEZ_CERO(cuenta: String) {
        val consultaSQL = "UPDATE `cuentas_servidor` SET `primeraVez` = 0 WHERE `cuenta` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setString(1, cuenta)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun SET_REGALO(cuenta: String, regalo: String) {
        val consultaSQL = "UPDATE `cuentas_servidor` SET `regalo` = ? WHERE `cuenta` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setString(1, regalo)
            declaracion.setString(2, cuenta)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_MENSAJES_CUENTA(cuenta: String, mensajes: String) {
        val consultaSQL = "UPDATE `cuentas_servidor` SET `mensajes` = ? WHERE `cuenta` = ? ;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setString(1, mensajes)
            declaracion.setString(2, cuenta)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun INSERT_TOKEN(cuenta: Cuenta, token: String) {
        val consultaSQL = "INSERT INTO `tokens` (`id`,`token`) values (?,?);"
        try {
            val declaracion = _bdDinamica?.let {
                transaccionSQL(
                    consultaSQL,
                    it
                )
            } ?: return
            declaracion.setInt(1, cuenta.id)
            declaracion.setString(2, token)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }
    }


    fun REPLACE_CUENTA_SERVIDOR(cuenta: Cuenta, primeraVez: Byte) {
        val consultaSQL = "REPLACE INTO `cuentas_servidor` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, cuenta.id)
            declaracion.setString(2, cuenta.nombre)
            declaracion.setLong(3, cuenta.kamasBanco)
            declaracion.setString(4, cuenta.stringBancoObjetosBD())
            declaracion.setString(5, cuenta.stringIDsEstablo())
            declaracion.setString(6, cuenta.analizarListaAmigosABD())
            declaracion.setString(7, cuenta.stringListaEnemigosABD())
            declaracion.setString(8, cuenta.listaReportes())
            declaracion.setByte(9, primeraVez)
            declaracion.setString(10, cuenta.regalo)
            declaracion.setString(11, cuenta.ultimaConexion)
            if (cuenta.actualIP.length < 2) {
                declaracion.setString(12, cuenta.ultimaIP)
            } else {
                declaracion.setString(12, cuenta.actualIP)
            }
            //			declaracion.setString(12, cuenta.getUltimaIP());
            declaracion.setString(13, cuenta.stringMensajes())
            declaracion.setInt(14, cuenta.verificadorReferido)
            // cuenta.setUltimaIP(cuenta.getActualIP());
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            SALVAR_OBJETOS(cuenta.objetosBanco)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    //
    // public static ArrayList<Personaje> GET_RANKING_NIVEL() {
    // final ArrayList<Personaje> persos = new ArrayList<>();
    // String consultaSQL = "SELECT `id` FROM `personajes` ORDER BY `xp` DESC LIMIT " +
    // Bustemu.LIMITE_LADDER + ";";
    // try {
    // final ResultSet resultado = consultaSQL(consultaSQL, _bdDinamica);
    // while (resultado.next()) {
    // try {
    // persos.add(Mundo.getPersonaje(Integer.parseInt(resultado.getString("id"))));
    // } catch (final Exception e) {}
    // }
    // cerrarResultado(resultado);
    // } catch (final Exception e) {
    // Bustemu.redactarLogServidorln("ERROR SQL: " + e.toString());
    // e.printStackTrace();
    // }
    // return persos;
    // }
    //
    // public static ArrayList<Gremio> GET_RANKING_GREMIOS() {
    // final ArrayList<Gremio> gremios = new ArrayList<>();
    // String consultaSQL = "SELECT `id` FROM `gremios` ORDER BY `xp` DESC LIMIT " +
    // Bustemu.LIMITE_LADDER + ";";
    // try {
    // final ResultSet resultado = consultaSQL(consultaSQL, _bdDinamica);
    // while (resultado.next()) {
    // try {
    // gremios.add(Mundo.getGremio(Integer.parseInt(resultado.getString("id"))));
    // } catch (final Exception e) {}
    // }
    // cerrarResultado(resultado);
    // } catch (final Exception e) {
    // Bustemu.redactarLogServidorln("ERROR SQL: " + e.toString());
    // e.printStackTrace();
    // }
    // return gremios;
    // }
    //
    // public static ArrayList<Personaje> GET_RANKING_PVP() {
    // final ArrayList<Personaje> persos = new ArrayList<>();
    // String consultaSQL =
    // "SELECT `id` FROM `ranking_pvp` ORDER BY `victorias` DESC, `derrotas`ASC LIMIT "
    // + Bustemu.LIMITE_LADDER + ";";
    // try {
    // final ResultSet resultado = consultaSQL(consultaSQL, _bdDinamica);
    // while (resultado.next()) {
    // try {
    // persos.add(Mundo.getPersonaje(resultado.getInt("id")));
    // } catch (final Exception e) {}
    // }
    // cerrarResultado(resultado);
    // } catch (final Exception e) {
    // Bustemu.redactarLogServidorln("ERROR SQL: " + e.toString());
    // e.printStackTrace();
    // }
    // return persos;
    // }
    //
    // public static ArrayList<Personaje> GET_RANKING_KOLISEO() {
    // final ArrayList<Personaje> persos = new ArrayList<>();
    // String consultaSQL =
    // "SELECT `id` FROM `ranking_koliseo` ORDER BY `victorias` DESC, `derrotas`ASC LIMIT "
    // + Bustemu.LIMITE_LADDER + ";";
    // try {
    // final ResultSet resultado = consultaSQL(consultaSQL, _bdDinamica);
    // while (resultado.next()) {
    // try {
    // persos.add(Mundo.getPersonaje(resultado.getInt("id")));
    // } catch (final Exception e) {}
    // }
    // cerrarResultado(resultado);
    // } catch (final Exception e) {
    // Bustemu.redactarLogServidorln("ERROR SQL: " + e.toString());
    // e.printStackTrace();
    // }
    // return persos;
    // }
    //
    fun GET_SIG_ID_OBJETO(): Int {
        var id = 1
        try {
            val resultado = consultaSQL(
                "SELECT MAX(id) AS max FROM `objetos`;",
                _bdDinamica!!
            )
            if (resultado.first()) {
                id = resultado.getInt("max")
            }
            cerrarResultado(resultado)
            return id
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return id
    }

    fun CARGAR_RECETAS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `recetas`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val arrayDuos = ArrayList<Duo<Int, Int>>()
                var continua = false
                val idReceta = resultado.getInt("id")
                val receta = resultado.getString("receta")
                for (str in receta.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    continua = try {
                        val s = str.split(Pattern.quote(",").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val idModeloObj = Integer.parseInt(s[0])
                        val cantidad = Integer.parseInt(s[1])
                        arrayDuos.add(Duo(idModeloObj, cantidad))
                        true
                    } catch (e: Exception) {
                        false
                    }

                }
                if (continua) {
                    Mundo.addReceta(idReceta, arrayDuos)
                }
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_DROPS(): Int {
        var numero = 0
        try {
            if (!AtlantaMain.PARAM_SISTEMA_ORBES) {
                val resultado = consultaSQL(
                    "SELECT * FROM `drops`;",
                    _bdEstatica!!
                )
                while (resultado.next()) {
                    if (Mundo.getObjetoModelo(resultado.getInt("objeto")) == null) {
                        continue
                    }
                    val drop = DropMob(
                        resultado.getInt("objeto"),
                        resultado.getInt("prospeccion"),
                        resultado.getFloat(
                            "porcentaje"
                        ),
                        resultado.getInt("max"),
                        resultado.getString("condicion")
                    )
                    Mundo.getMobModelo(resultado.getInt("mob"))?.addDrop(drop)
                    numero++
                }
                cerrarResultado(resultado)
            }
        } catch (e: Exception) {
            exceptionExit(e)
        }

        return numero
    }

    fun CARGAR_DROPS_FIJOS(): Int {
        var numero = 0
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `drops_fijos`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                if (Mundo.getObjetoModelo(resultado.getInt("objeto")) == null) {
                    continue
                }
                Mundo.addDropFijo(
                    DropMob(
                        resultado.getInt("objeto"),
                        resultado.getFloat("porcentaje"),
                        resultado.getInt(
                            "nivelMin"
                        ),
                        resultado.getInt("nivelMax")
                    )
                )
                numero++
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

        return numero
    }

    fun SELECT_ZONAS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM zonas;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.ZONAS[resultado.getShort("mapa")] = resultado.getShort("celda")
                Mundo.LISTA_ZONAS += "|" + resultado.getString("nombre") + ";" + resultado.getShort(
                    "mapa"
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_COMANDOS_ACCION() {
        try {
            val resultado = _bdEstatica?.let {
                consultaSQL(
                    "SELECT * FROM comandosaccion;",
                    it
                )
            } ?: return
            Mundo.COMANDOSACCION.clear() // Como siempre, debes acordarte de eliminar la lista antes de volver a cargar
            while (resultado.next()) {
                Mundo.COMANDOSACCION[resultado.getInt("id")] = comandosAccion(
                    resultado.getInt("id"),
                    resultado.getString("comando"),
                    resultado.getInt("id_accion"),
                    resultado.getString("arg"),
                    resultado.getString("condicion"),
                    resultado.getString("activo").equals("true", ignoreCase = true)
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
        }
    }


    fun CARGAR_OBJETOS_SETS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM objetos_set;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val set = ObjetoSet(
                    resultado.getInt("id"),
                    resultado.getString("nombre"),
                    resultado.getString(
                        "objetos"
                    )
                )
                for (i in 2..8) {
                    set.setStats(resultado.getString(i.toString() + "_objetos"), i)
                }
                Mundo.addObjetoSet(set)
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_CERCADOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `cercados_modelo`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val mapa = Mundo.getMapa(resultado.getShort("mapa")) ?: continue
                val cercado = Cercado(
                    mapa,
                    resultado.getInt("capacidad"),
                    resultado.getByte("objetos"),
                    resultado.getShort("celdaPJ"),
                    resultado.getShort("celdaPuerta"),
                    resultado.getShort("celdaMontura"),
                    resultado.getString("celdasObjetos"),
                    resultado.getInt("precioOriginal")
                )
                Mundo.addCercado(cercado)
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_OFICIOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `oficios`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.addOficio(
                    Oficio(
                        resultado.getInt("id"),
                        resultado.getString("herramientas"),
                        resultado.getString(
                            "recetas"
                        )
                    )
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_SERVICIOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `servicios`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.addServicio(
                    Servicio(
                        resultado.getInt("id"),
                        resultado.getInt("creditos"),
                        resultado.getInt(
                            "ogrinas"
                        ),
                        resultado.getBoolean("activado"),
                        resultado.getInt("creditosVIP"),
                        resultado.getInt("ogrinasVIP")
                    )
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_ENCARNACIONES_MODELOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `encarnaciones_modelo`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.addEncarnacionModelo(
                    EncarnacionModelo(
                        resultado.getInt("gfx"), resultado.getString("statsFijos"),
                        resultado.getString("statsPorNivel"), resultado.getString("hechizos")
                    )
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_CLASES() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `clases`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val clase = Clase(
                    resultado.getInt("id"),
                    resultado.getString("gfxs"),
                    resultado.getString("tallas"),
                    resultado.getShort("mapaInicio"),
                    resultado.getShort("celdaInicio"),
                    resultado.getInt("PDV"),
                    resultado.getString("boostVitalidad"),
                    resultado.getString("boostSabiduria"),
                    resultado.getString("boostFuerza"),
                    resultado.getString("boostInteligencia"),
                    resultado.getString("boostAgilidad"),
                    resultado.getString(
                        "boostSuerte"
                    ),
                    resultado.getString("statsInicio"),
                    resultado.getString("hechizos")
                )
                Mundo.CLASES[clase.id] = clase
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_CREA_OBJETOS_MODELOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `crear_objetos_modelos`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val creaTuItem = CreaTuItem(
                    resultado.getInt("id"),
                    resultado.getString("statsMaximos"),
                    resultado.getInt("limiteOgrinas"),
                    resultado.getInt("precioBase")
                )
                Mundo.CREA_TU_ITEM[creaTuItem.iD] = creaTuItem
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_CREA_OBJETOS_PRECIOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `crear_objetos_stats`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                if (Mundo.CREAT_TU_ITEM_PRECIOS.isNotEmpty()) {
                    Mundo.CREAT_TU_ITEM_PRECIOS += ";"
                }
                val stat = resultado.getInt("id")
                val ogrinas = resultado.getFloat("ogrinas")
                Mundo.CREAT_TU_ITEM_PRECIOS += "$stat,$ogrinas"
                CreaTuItem.PRECIOS[stat] = ogrinas
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_AREA() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `areas`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val area = Area(
                    resultado.getShort("id").toInt(),
                    resultado.getShort("superarea"),
                    resultado.getString(
                        "nombre"
                    )
                )
                Mundo.addArea(area)
                area.superArea!!.addArea(area)
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_SUBAREA() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `subareas`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val subarea = SubArea(
                    resultado.getShort("id").toInt(),
                    resultado.getShort("area"),
                    resultado.getString(
                        "nombre"
                    ),
                    resultado.getInt("conquistable") == 1,
                    resultado.getInt("minNivelGrupoMob"),
                    resultado.getInt(
                        "maxNivelGrupoMob"
                    ),
                    resultado.getString("cementerio")
                )
                Mundo.addSubArea(subarea)
                if (subarea.area != null) {
                    subarea.area.addSubArea(subarea)
                }
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_NPCS(): Int {
        var numero = 0
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `npcs_ubicacion`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                try {
                    val npcModelo = Mundo.getNPCModelo(resultado.getInt("npc"))
                    if (npcModelo == null) {
                        DELETE_NPC_UBICACION(resultado.getInt("npc"))
                        continue
                    }
                    Mundo.getMapa(resultado.getShort("mapa"))?.addNPC(
                        npcModelo, resultado.getShort("celda"), resultado.getByte(
                            "orientacion"
                        )
                    )
                    numero++
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

        return numero
    }

    fun CARGAR_CASAS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `casas_modelo`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                if (Mundo.getMapa(resultado.getShort("mapaFuera")) == null) {
                    continue
                }
                Mundo.addCasa(
                    Casa(
                        resultado.getInt("id"),
                        resultado.getShort("mapaFuera"),
                        resultado.getShort(
                            "celdaFuera"
                        ),
                        resultado.getShort("mapaDentro"),
                        resultado.getShort("celdaDentro"),
                        resultado.getLong("precio"),
                        resultado.getString("mapasContenidos")
                    )
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun RECARGAR_CASAS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `casas`;",
                _bdDinamica!!
            )
            while (resultado.next()) {
                try {
                    Mundo.getCasa(resultado.getInt("id"))?.actualizarCasa(
                        resultado.getInt("dueño"),
                        resultado.getInt("precio").toLong(),
                        resultado.getByte("bloqueado"),
                        resultado.getString("clave"),
                        resultado.getInt("derechosGremio")
                    )
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

    }

    fun CARGAR_COFRES() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `cofres_modelo`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.addCofre(
                    Cofre(
                        resultado.getInt("id"),
                        resultado.getInt("casa"),
                        resultado.getShort("mapa"),
                        resultado.getShort("celda"),
                        AtlantaMain.LIMITE_OBJETOS_COFRE
                    )
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_EXPERIENCIA() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `experiencia`;",
                _bdEstatica!!
            )
            var maxAlineacion = 0
            var maxPersonaje = 0
            var maxGremio = 0
            var maxOficio = 0
            var maxMontura = 0
            var maxEncarnacion = 0
            while (resultado.next()) {
                val nivel = resultado.getInt("nivel")
                val exp = Experiencia(
                    resultado.getLong("personaje"),
                    resultado.getInt("oficio"),
                    resultado.getInt(
                        "dragopavo"
                    ),
                    resultado.getLong("gremio"),
                    resultado.getInt("pvp"),
                    resultado.getInt("encarnacion")
                )
                if (exp._alineacion > 0) {
                    maxAlineacion = max(maxAlineacion, nivel)
                }
                if (exp._personaje > 0) {
                    maxPersonaje = max(maxPersonaje, nivel)
                }
                if (exp._gremio > 0) {
                    maxGremio = max(maxGremio, nivel)
                }
                if (exp._oficio > 0) {
                    maxOficio = max(maxOficio, nivel)
                }
                if (exp._montura > 0) {
                    maxMontura = max(maxMontura, nivel)
                }
                if (exp._encarnacion > 0) {
                    maxEncarnacion = max(maxEncarnacion, nivel)
                }
                Mundo.addExpNivel(nivel, exp)
            }
            if (AtlantaMain.NIVEL_MAX_PERSONAJE <= 0) {
                AtlantaMain.NIVEL_MAX_PERSONAJE = maxPersonaje
            }
            if (AtlantaMain.NIVEL_MAX_ALINEACION <= 0) {
                AtlantaMain.NIVEL_MAX_ALINEACION = maxAlineacion
            }
            if (AtlantaMain.NIVEL_MAX_GREMIO <= 0) {
                AtlantaMain.NIVEL_MAX_GREMIO = maxGremio
            }
            if (AtlantaMain.NIVEL_MAX_OFICIO <= 0) {
                AtlantaMain.NIVEL_MAX_OFICIO = maxOficio
            }
            if (AtlantaMain.NIVEL_MAX_MONTURA <= 0) {
                AtlantaMain.NIVEL_MAX_MONTURA = maxMontura
            }
            if (AtlantaMain.NIVEL_MAX_ENCARNACION <= 0) {
                AtlantaMain.NIVEL_MAX_ENCARNACION = maxEncarnacion
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_TRIGGERS(): Int {
        var numero = 0
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `celdas_accion`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val mapa = Mundo.getMapa(resultado.getShort("mapa"))
                if (mapa?.getCelda(resultado.getShort("celda")) == null) {
                    continue
                }
                mapa.getCelda(resultado.getShort("celda"))!!.addAccion(
                    resultado.getInt("accion"), resultado.getString("args"),
                    resultado.getString("condicion")
                )
                numero++
            }
            cerrarResultado(resultado)
            return numero
        } catch (e: Exception) {
            exceptionExit(e)
        }

        return numero
    }

    fun CARGAR_MOBS_EVENTO() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `mobs_evento`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.addMobEvento(
                    resultado.getByte("evento"),
                    resultado.getInt("mobOriginal"),
                    resultado.getInt("mobEvento")
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_PERSONAJES() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `personajes`;",
                _bdDinamica!!
            )
            while (resultado.next()) {
                val statsBase = TreeMap<Int, Int>()
                statsBase[Constantes.STAT_MAS_VITALIDAD] = resultado.getInt("vitalidad")
                statsBase[Constantes.STAT_MAS_FUERZA] = resultado.getInt("fuerza")
                statsBase[Constantes.STAT_MAS_SABIDURIA] = resultado.getInt("sabiduria")
                statsBase[Constantes.STAT_MAS_INTELIGENCIA] = resultado.getInt("inteligencia")
                statsBase[Constantes.STAT_MAS_SUERTE] = resultado.getInt("suerte")
                statsBase[Constantes.STAT_MAS_AGILIDAD] = resultado.getInt("agilidad")
                val statsScroll = TreeMap<Int, Int>()
                statsScroll[Constantes.STAT_MAS_VITALIDAD] = resultado.getInt("sVitalidad")
                statsScroll[Constantes.STAT_MAS_FUERZA] = resultado.getInt("sFuerza")
                statsScroll[Constantes.STAT_MAS_SABIDURIA] = resultado.getInt("sSabiduria")
                statsScroll[Constantes.STAT_MAS_INTELIGENCIA] = resultado.getInt("sInteligencia")
                statsScroll[Constantes.STAT_MAS_SUERTE] = resultado.getInt("sSuerte")
                statsScroll[Constantes.STAT_MAS_AGILIDAD] = resultado.getInt("sAgilidad")
                val perso = Personaje(
                    resultado.getInt("id"),
                    resultado.getString("nombre"),
                    resultado.getByte(
                        "sexo"
                    ),
                    resultado.getByte("clase"),
                    resultado.getInt("color1"),
                    resultado.getInt("color2"),
                    resultado.getInt(
                        "color3"
                    ),
                    resultado.getLong("kamas"),
                    resultado.getInt("puntosHechizo"),
                    resultado.getInt("capital"),
                    resultado.getInt("energia"),
                    resultado.getShort("nivel"),
                    resultado.getLong("xp"),
                    resultado.getInt("talla"),
                    resultado.getInt("gfx"),
                    resultado.getByte("alineacion"),
                    resultado.getInt("cuenta"),
                    statsBase,
                    statsScroll,
                    resultado.getInt("mostrarAmigos") == 1,
                    resultado.getByte("mostrarAlineacion").toInt() == 1,
                    resultado.getString("canal"),
                    resultado.getShort("mapa"),
                    resultado.getShort("celda"),
                    resultado.getString("objetos"),
                    resultado.getByte(
                        "porcVida"
                    ).toInt(),
                    resultado.getString("hechizos"),
                    resultado.getString("posSalvada"),
                    resultado.getString("oficios"),
                    resultado.getByte("xpMontura"),
                    resultado.getInt("montura"),
                    resultado.getInt("honor"),
                    resultado.getInt(
                        "deshonor"
                    ),
                    resultado.getByte("nivelAlin"),
                    resultado.getString("zaaps"),
                    resultado.getInt("esposo"),
                    resultado.getString("tienda"),
                    resultado.getInt("mercante") == 1,
                    resultado.getInt("restriccionesA"),
                    resultado.getInt(
                        "restriccionesB"
                    ),
                    resultado.getInt("encarnacion"),
                    resultado.getInt("emotes"),
                    resultado.getString("titulos"),
                    resultado.getString("tituloVIP"),
                    resultado.getString("ornamentos"),
                    resultado.getString("misiones"),
                    resultado.getString("coleccion"),
                    resultado.getByte("resets"),
                    resultado.getString("almanax"),
                    resultado.getInt(
                        "ultimoNivel"
                    ),
                    resultado.getString("setsRapidos"),
                    resultado.getInt("colorNombre"),
                    resultado.getString(
                        "orden"
                    )
                )
                if (perso.cuenta != null) {
                    Mundo.addPersonaje(perso)
                }
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_PRISMAS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `prismas`;",
                _bdDinamica!!
            )
            while (resultado.next()) {
                if (Mundo.getMapa(resultado.getShort("mapa")) == null) {
                    continue
                }
                val prisma = Prisma(
                    resultado.getInt("id"),
                    resultado.getByte("alineacion"),
                    resultado.getByte("nivel"),
                    resultado.getShort("mapa"),
                    resultado.getShort("celda"),
                    resultado.getInt("honor"),
                    resultado.getShort("area").toInt(),
                    resultado.getShort("subArea").toInt(),
                    resultado.getLong("tiempoProteccion")
                )
                Mundo.addPrisma(prisma)
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun SELECT_OBJETOS_MERCADILLO(): Int {
        var num = 0
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `mercadillo_objetos`;",
                _bdDinamica!!
            )
            while (resultado.next()) {
                val puesto = Mundo.getPuestoMercadillo(resultado.getInt("mercadillo"))
                val objeto = Mundo.getObjeto(resultado.getInt("objeto"))
                if (puesto == null || objeto == null || objeto.dueñoTemp != 0) {
                    AtlantaMain.redactarLogServidorln(
                        "Se borro el objeto mercadillo id:" + resultado.getInt("objeto")
                                + ", dueño: " + resultado.getInt("dueño")
                    )
                    DELETE_OBJ_MERCADILLO(resultado.getInt("objeto"))
                    continue
                }
                puesto.addObjMercaAlPuesto(
                    ObjetoMercadillo(
                        resultado.getInt("precio").toLong(), resultado.getByte("cantidad").toInt(),
                        resultado.getInt("dueño"), objeto, puesto.iD
                    )
                )
                num++
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

        return num
    }

    fun CARGAR_RECAUDADORES() {
        var numero = 0
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `recaudadores`;",
                _bdDinamica!!
            )
            while (resultado.next()) {
                val mapa = Mundo.getMapa(resultado.getShort("mapa")) ?: continue
                val recaudador = Recaudador(
                    resultado.getInt("id"),
                    resultado.getShort("mapa"),
                    resultado.getShort("celda"),
                    resultado.getByte("orientacion"),
                    resultado.getInt("gremio"),
                    resultado.getString(
                        "nombre1"
                    ),
                    resultado.getString("nombre2"),
                    resultado.getString("objetos"),
                    resultado.getLong("kamas"),
                    resultado.getLong("xp"),
                    resultado.getLong("tiempoProteccion"),
                    resultado.getLong("tiempoCreacion"),
                    resultado.getInt("dueño")
                )
                Mundo.addRecaudador(recaudador)
                numero++
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_GREMIOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM gremios;",
                _bdDinamica!!
            )
            while (resultado.next()) {
                Mundo.addGremio(
                    Gremio(
                        resultado.getInt("id"),
                        resultado.getString("nombre"),
                        resultado.getString(
                            "emblema"
                        ),
                        resultado.getShort("nivel"),
                        resultado.getLong("xp"),
                        resultado.getShort("capital"),
                        resultado.getByte("recaudadores"),
                        resultado.getString("hechizos"),
                        resultado.getString("stats")
                    )
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    @Throws(SQLException::class)
    fun RESULSET_MAP(resultado: ResultSet): Mapa {
        val mapaID = resultado.getShort("id")
        if (AtlantaMain.MODO_DEBUG) {
            println("Cargando mapa ID $mapaID")
        }
        return Mapa(
            mapaID,
            resultado.getString("fecha"),
            resultado.getByte("ancho"),
            resultado.getByte("alto"),
            resultado.getString("posPelea"),
            resultado.getString("mapData"),
            resultado.getString("key"),
            resultado.getString(
                "mobs"
            ),
            resultado.getShort("X"),
            resultado.getShort("Y"),
            resultado.getShort("subArea"),
            resultado.getByte(
                "maxGrupoMobs"
            ),
            resultado.getByte("maxMobsPorGrupo"),
            resultado.getByte("maxMercantes"),
            resultado.getShort(
                "capabilities"
            ),
            resultado.getByte("maxPeleas"),
            resultado.getShort("bgID"),
            resultado.getShort("musicID"),
            resultado.getShort("ambienteID"),
            resultado.getByte("outDoor"),
            resultado.getInt("minNivelGrupoMob"),
            resultado.getInt("maxNivelGrupoMob")
        )
    }

    fun CLONAR_MAPA(
        mapaClonar: Mapa, nuevaID: Int, nuevaFecha: String, nuevaX: Int, nuevaY: Int,
        nuevaSubArea: Int
    ): Boolean {
        var consultaSQL = "REPLACE INTO `mapas` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
        try {
            var i = 1
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(i++, nuevaID)
            declaracion.setString(i++, nuevaFecha)
            declaracion.setInt(i++, mapaClonar.ancho.toInt())
            declaracion.setInt(i++, mapaClonar.alto.toInt())
            declaracion.setInt(i++, mapaClonar.bgID)
            declaracion.setInt(i++, mapaClonar.ambienteID)
            declaracion.setInt(i++, mapaClonar.musicID)
            declaracion.setInt(i++, mapaClonar.outDoor)
            declaracion.setInt(i++, mapaClonar.capabilities)
            declaracion.setString(i++, mapaClonar.strCeldasPelea())
            declaracion.setString(i++, mapaClonar.key)
            declaracion.setString(i++, mapaClonar.mapData)
            declaracion.setString(i++, "")
            declaracion.setInt(i++, nuevaX)
            declaracion.setInt(i++, nuevaY)
            declaracion.setInt(i++, nuevaSubArea)
            declaracion.setInt(i++, mapaClonar.maxGrupoDeMobs.toInt())
            declaracion.setInt(i++, mapaClonar.maxMobsPorGrupo.toInt())
            declaracion.setInt(i++, mapaClonar.minNivelGrupoMob)
            declaracion.setInt(i++, mapaClonar.maxNivelGrupoMob)
            declaracion.setInt(i++, mapaClonar.maxMercantes.toInt())
            declaracion.setInt(i++, mapaClonar.maxNumeroPeleas)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            consultaSQL = "SELECT * FROM `mapas` WHERE `id` = $nuevaID;"
            val resultado = consultaSQL(
                consultaSQL,
                _bdEstatica!!
            )
            while (resultado.next()) {
                if (Mundo.mapaExiste(resultado.getShort("id"))) {
                    continue
                }
                val mapa = RESULSET_MAP(resultado)
                Mundo.addMapa(mapa)
                return true
            }
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return false
    }

    fun CARGAR_MAPAS() {
        var consultaSQL = "SELECT * FROM `mapas` LIMIT " + AtlantaMain.LIMITE_MAPAS + ";"
        try {
            if (AtlantaMain.MODO_MAPAS_LIMITE) {
                consultaSQL =
                    ("SELECT * FROM `mapas` WHERE `subArea` IN (" + AtlantaMain.STR_SUBAREAS_LIMITE + ") OR `id` IN ("
                            + AtlantaMain.STR_MAPAS_LIMITE + ");")
            }
            val resultado = consultaSQL(
                consultaSQL,
                _bdEstatica!!
            )
            var mapa: Mapa
            // 256 MB = 1500 MAPAS
            // 1 GB = 6000 MAPAS
            while (resultado.next()) {
                if (Mundo.mapaExiste(resultado.getShort("id"))) {
                    continue
                }
                mapa = RESULSET_MAP(resultado)
                Mundo.addMapa(mapa)
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_MAPAS_IDS(ids: String) {
        if (ids.isEmpty()) {
            return
        }
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `mapas` WHERE `id` IN ($ids) ;",
                _bdEstatica!!
            )
            var mapa: Mapa
            while (resultado.next()) {
                if (Mundo.mapaExiste(resultado.getShort("id"))) {
                    continue
                }
                mapa = RESULSET_MAP(resultado)
                Mundo.addMapa(mapa)
                CARGAR_TRIGGERS_POR_MAPA(mapa.id)
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_MAPAS_SUBAREAS(subAreas: String) {
        if (subAreas.isEmpty()) {
            return
        }
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `mapas` WHERE `subArea` IN ($subAreas) ;",
                _bdEstatica!!
            )
            var mapa: Mapa
            while (resultado.next()) {
                if (Mundo.mapaExiste(resultado.getShort("id"))) {
                    continue
                }
                mapa = RESULSET_MAP(resultado)
                Mundo.addMapa(mapa)
                CARGAR_TRIGGERS_POR_MAPA(mapa.id)
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_TRIGGERS_POR_MAPA(id: Short) {
        val consultaSQL = "SELECT * FROM `celdas_accion` WHERE `mapa` = '$id';"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdEstatica!!
            )
            while (resultado.next()) {
                val mapa = Mundo.getMapa(resultado.getShort("mapa"))
                if (mapa?.getCelda(resultado.getShort("celda")) == null) {
                    continue
                }
                mapa.getCelda(resultado.getShort("celda"))!!.addAccion(
                    resultado.getInt("accion"), resultado.getString("args"),
                    resultado.getString("condicion")
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

    }

    fun CARGAR_MOBS_FIJOS(): Int {
        var numero = 0
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `mobs_fix`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val mapas = ArrayList<Mapa>()
                for (m in resultado.getString("mapa").split(",".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()) {
                    if (m.isEmpty()) {
                        continue
                    }
                    try {
                        val mapa = Mundo.getMapa(m.toShort())
                        if (mapa != null) {
                            mapas.add(mapa)
                        }
                    } catch (ignored: Exception) {
                    }

                }
                if (mapas.isEmpty()) {
                    continue
                }
                val mapa = mapas[0]
                if (mapa == null) {
                    AtlantaMain.redactarLogServidorln("EL MAPA " + resultado.getShort("mapa") + " NO EXISTE")
                    continue
                }
                if (mapa.getCelda(resultado.getShort("celda")) == null) {
                    AtlantaMain.redactarLogServidorln(
                        "LA CELDA " + resultado.getShort("celda") + " DEL MAPA " + resultado.getShort(
                            "mapa"
                        ) + " NO EXISTE"
                    )
                    continue
                }
                var tipoGrupo = Constantes.getTipoGrupoMob(resultado.getInt("tipo"))
                if (tipoGrupo == TipoGrupo.NORMAL) {
                    tipoGrupo = TipoGrupo.FIJO
                }
                val grupoMob = mapa.addGrupoMobPorTipo(
                    resultado.getShort("celda"), resultado.getString("mobs"), tipoGrupo,
                    resultado.getString("condicion"), mapas
                )
                if (grupoMob != null) {
                    val s1 = Mundo.getMapaEstrellas(mapa.id)
                    val s2 = Mundo.getMapaHeroico(mapa.id)
                    val estrellas = if (s1 == null) -1 else if (s1.isEmpty()) -1 else s1[0]
                    val heroico = if (s2 == null) "" else if (s2.isEmpty()) "" else s2[0]
                    if (estrellas > -1) {
                        grupoMob.bonusEstrellas = estrellas.toInt()
                    }
                    if (heroico.isNotEmpty()) {
                        grupoMob.addObjetosKamasInicioServer(heroico)
                    }
                    if (s1 != null && s1.isNotEmpty()) {
                        s1.removeAt(0)
                    }
                    if (s2 != null && s2.isNotEmpty()) {
                        s2.removeAt(0)
                    }
                    grupoMob.segundosRespawn = resultado.getInt("segundosRespawn")
                    numero++
                } else {
                    AtlantaMain.redactarLogServidorln(
                        "NO SE PUDO AGREGAR EL GRUPOMOB FIJO " + resultado.getString("mobs")
                                + " EN EL MAPA " + resultado.getShort("mapa") + ", CELDA " + resultado.getShort(
                            "celda"
                        )
                    )
                }
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

        return numero
    }

    fun SELECT_ANIMACIONES() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `animaciones`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.addAnimacion(
                    Animacion(
                        resultado.getInt("id"),
                        resultado.getInt("hechizoAnimacion"),
                        resultado.getInt(
                            "tipoDisplay"
                        ),
                        resultado.getInt("spriteAnimacion"),
                        resultado.getInt("level"),
                        resultado.getInt("duracion"),
                        resultado.getInt("talla")
                    )
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_COMANDOS_MODELO() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `comandos_modelo`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.addComando(resultado.getString("comando"), resultado.getInt("rango"))
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_OTROS_INTERACTIVOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `otros_interactivos`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.addOtroInteractivo(
                    OtroInteractivo(
                        resultado.getInt("gfx"),
                        resultado.getShort("mapaID"),
                        resultado.getShort("celdaID"),
                        resultado.getInt("accion"),
                        resultado.getString("args"),
                        resultado.getString("condicion"),
                        resultado.getInt("tiempoRecarga")
                    )
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_COMIDAS_MASCOTAS(): Int {
        var numero = 0
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `mascotas_modelo`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.addMascotaModelo(
                    MascotaModelo(
                        resultado.getInt("mascota"),
                        resultado.getInt("maximoComidas"),
                        resultado.getString("statsPorEfecto"),
                        resultado.getString("comidas"),
                        resultado.getInt("devorador"),
                        resultado.getInt("fantasma")
                    )
                )
                numero++
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

        return numero
    }

    fun CARGAR_HECHIZOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `hechizos`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val id = resultado.getInt("id")
                val hechizo = Hechizo(
                    id,
                    resultado.getString("nombre"),
                    resultado.getInt("sprite"),
                    resultado.getString("spriteInfos"),
                    resultado.getInt("valorIA")
                )
                Mundo.addHechizo(hechizo)
                for (i in 1..6) {
                    var sh: StatHechizo? = null
                    val txt = resultado.getString("nivel$i")
                    if (txt.isNotEmpty()) {
                        try {
                            sh = Hechizo.analizarHechizoStats(id, i, txt)
                        } catch (e: Exception) {
                            AtlantaMain.redactarLogServidorln("BUG HECHIZO: $id NIVEL $i")
                            exceptionExit(e)
                        }

                    }
                    if (sh != null) {
                        hechizo.addStatsHechizos(i, sh)
                    }
                }
                hechizo.setAfectados(resultado.getString("afectados"))
                hechizo.setCondiciones(resultado.getString("condiciones"))
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_ESPECIALIDADES() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `especialidades`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.addEspecialidad(
                    Especialidad(
                        resultado.getInt("id"),
                        resultado.getInt("orden"),
                        resultado.getInt(
                            "nivel"
                        ),
                        resultado.getString("dones")
                    )
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_DONES_MODELOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `dones`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.addDonModelo(resultado.getInt("id"), resultado.getInt("stat"))
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_OBJETOS_MODELOS() {
        try {
            var maxID = 0
            val resultado = consultaSQL(
                "SELECT * FROM `objetos_modelo`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                if (resultado.getInt("id") > AtlantaMain.MAX_ID_OBJETO_MODELO) {
                    continue
                }
                try {
                    if (resultado.getString("condicion") == null) {
                        val condicion = ""
                        val obj = ObjetoModelo(
                            resultado.getInt("id"),
                            resultado.getString("statsModelo"),
                            resultado.getString("nombre"),
                            resultado.getShort("tipo"),
                            resultado.getShort("nivel"),
                            resultado.getShort("pods"),
                            resultado.getInt("kamas"),
                            condicion,
                            resultado.getString("infosArma"),
                            resultado.getInt(
                                "vendidos"
                            ),
                            resultado.getInt("precioMedio").toLong(),
                            resultado.getInt("ogrinas"),
                            resultado.getBoolean("magueable"),
                            resultado.getShort("gfx").toInt(),
                            resultado.getBoolean("nivelCore"),
                            resultado.getBoolean("etereo"),
                            resultado.getInt(
                                "diasIntercambio"
                            ),
                            resultado.getInt("panelOgrinas"),
                            resultado.getInt("panelKamas"),
                            resultado.getString(
                                "itemPago"
                            )
                        )
                        Mundo.addObjModelo(obj)
                        maxID = max(maxID, obj.id)
                    } else {
                        val obj = ObjetoModelo(
                            resultado.getInt("id"),
                            resultado.getString("statsModelo"),
                            resultado.getString("nombre"),
                            resultado.getShort("tipo"),
                            resultado.getShort("nivel"),
                            resultado.getShort("pods"),
                            resultado.getInt("kamas"),
                            resultado.getString("condicion"),
                            resultado.getString("infosArma"),
                            resultado.getInt(
                                "vendidos"
                            ),
                            resultado.getInt("precioMedio").toLong(),
                            resultado.getInt("ogrinas"),
                            resultado.getBoolean("magueable"),
                            resultado.getShort("gfx").toInt(),
                            resultado.getBoolean("nivelCore"),
                            resultado.getBoolean("etereo"),
                            resultado.getInt(
                                "diasIntercambio"
                            ),
                            resultado.getInt("panelOgrinas"),
                            resultado.getInt("panelKamas"),
                            resultado.getString(
                                "itemPago"
                            )
                        )
                        Mundo.addObjModelo(obj)
                        maxID = max(maxID, obj.id)
                    }
                } catch (e: Exception) {
                    println("No se ha cargado el objeto ID " + resultado.getInt("id"))
                    continue
                }


            }
            AtlantaMain.MAX_ID_OBJETO_MODELO = min(
                maxID,
                AtlantaMain.MAX_ID_OBJETO_MODELO
            )
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_MONTURAS_MODELOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `monturas_modelo`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.addMonturaModelo(
                    MonturaModelo(
                        resultado.getInt("id"),
                        resultado.getString("stats"),
                        resultado.getString("color"),
                        resultado.getInt("certificado"),
                        resultado.getByte("generacion")
                    )
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_MOBS_MODELOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `mobs_modelo`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                //				final boolean capturable = resultado.getInt("capturable") == 1;
                val capturable = true
                val esKickeable = resultado.getString("kickeable") == "true"
                val alineacion = resultado.getByte("alineacion")
                val tipoIA = resultado.getByte("tipoIA")
                val tipo = resultado.getByte("tipo")
                val talla = resultado.getShort("talla")
                val distAgresion = resultado.getByte("agresion")
                val id = resultado.getInt("id")
                val gfxID = resultado.getShort("gfxID")
                val mK = resultado.getString("minKamas")
                val MK = resultado.getString("maxKamas")
                val nombre = resultado.getString("nombre")
                val colores = resultado.getString("colores")
                val grados =
                    resultado.getString("grados").replace(" ".toRegex(), "").replace(",g".toRegex(), "g")
                        .replace(":\\{l:".toRegex(), "@").replace(",r:\\[".toRegex(), ",").replace("]".toRegex(), "|")
                        .replace("]}".toRegex(), "|")
                // g1: {l: 1, r: [25, 0, -12, 6, -50, 15, 15], lp: 30, ap: 5, mp: 2}, g2: {l: 2
                val hechizos = resultado.getString("hechizos")
                val stats = resultado.getString("stats")
                val pdvs = resultado.getString("pdvs")
                val puntos = resultado.getString("puntos")
                val iniciativa = resultado.getString("iniciativa")
                val xp = resultado.getString("exps")
                Mundo.addMobModelo(
                    MobModelo(
                        id, nombre, gfxID, alineacion, colores, grados, hechizos, stats, pdvs, puntos,
                        iniciativa, mK, MK, xp, tipoIA, capturable, talla, distAgresion, tipo, esKickeable
                    )
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_MOBS_RAROS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `mobs_raros`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val idMobRaro = resultado.getInt("idMobRaro")
                val idMobNormal = resultado.getInt("idMobNormal")
                val subAreas = resultado.getString("subAreas")
                val probabilidad = resultado.getInt("probabilidad")
                val mobM = Mundo.getMobModelo(idMobRaro) ?: continue
                val mobN = Mundo.getMobModelo(idMobNormal)
                if (mobN != null) {
                    mobN.archiMob = mobM
                }
                mobM.setDataExtra(probabilidad, subAreas)
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_MIEMBROS_GREMIO(): Int {
        var numero = 0
        try {
            val resultado = consultaSQL(
                "SELECT * FROM miembros_gremio;",
                _bdDinamica!!
            )
            while (resultado.next()) {
                if (Mundo.getPersonaje(resultado.getInt("id")) == null) {
                    DELETE_MIEMBRO_GREMIO(resultado.getInt("id"))
                    continue
                }
                val gremio = Mundo.getGremio(resultado.getInt("gremio")) ?: continue
                gremio.addMiembro(
                    resultado.getInt("id"),
                    resultado.getInt("rango"),
                    resultado.getLong("xpDonada"),
                    resultado.getByte("porcXp"),
                    resultado.getInt("derechos")
                )
                numero++
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

        return numero
    }

    fun CARGAR_MONTURAS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `monturas`;",
                _bdDinamica!!
            )
            while (resultado.next()) {
                Mundo.addMontura(
                    Montura(
                        resultado.getInt("id"),
                        resultado.getInt("color"),
                        resultado.getByte("sexo"),
                        resultado.getInt("amor"),
                        resultado.getInt("resistencia"),
                        resultado.getInt("nivel"),
                        resultado.getLong("xp"),
                        resultado.getString("nombre"),
                        resultado.getInt("fatiga"),
                        resultado.getInt("energia"),
                        resultado.getByte(
                            "reproducciones"
                        ),
                        resultado.getInt("madurez"),
                        resultado.getInt("serenidad"),
                        resultado.getString("objetos"),
                        resultado.getString("ancestros"),
                        resultado.getString("habilidad"),
                        resultado.getByte("talla"),
                        resultado.getShort("celda"),
                        resultado.getShort("mapa"),
                        resultado.getInt("dueño"),
                        resultado.getByte("orientacion"),
                        resultado.getLong("fecundable"),
                        resultado.getInt("pareja"),
                        resultado.getByte("salvaje")
                    ), false
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_NPC_MODELOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `npcs_modelo`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val id = resultado.getInt("id")
                val gfxID = resultado.getInt("gfxID")
                val escalaX = resultado.getShort("scaleX")
                val escalaY = resultado.getShort("scaleY")
                val sexo = resultado.getByte("sexo")
                val color1 = resultado.getInt("color1")
                val color2 = resultado.getInt("color2")
                val color3 = resultado.getInt("color3")
                val foto = resultado.getInt("foto")
                val preguntaID = resultado.getInt("pregunta")
                val ventas = resultado.getString("ventas")
                val nombre = resultado.getString("nombre")
                val npcModelo = NPCModelo(
                    id,
                    gfxID,
                    escalaX,
                    escalaY,
                    sexo,
                    color1,
                    color2,
                    color3,
                    foto,
                    preguntaID,
                    ventas,
                    nombre,
                    resultado.getInt("arma"),
                    resultado.getInt("sombrero"),
                    resultado.getInt("capa"),
                    resultado.getInt("mascota"),
                    resultado.getInt("escudo")
                )
                Mundo.addNPCModelo(npcModelo)
                if (AtlantaMain.ID_NPC_BOUTIQUE.toInt() == npcModelo.id) {
                    AtlantaMain.NPC_BOUTIQUE = NPC(npcModelo, 0, 0.toShort(), 0.toByte())
                }
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_MISION_OBJETIVOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `mision_objetivos`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.addMisionObjetivoModelo(
                    resultado.getInt("id"),
                    resultado.getByte("tipo"),
                    resultado.getString("args")
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_ORNAMENTOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `ornamentos`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val o = Ornamento(
                    resultado.getInt("id"),
                    resultado.getString("nombre"),
                    resultado.getInt("creditos"),
                    resultado.getInt("ogrinas"),
                    resultado.getInt("kamas"),
                    resultado.getString("vender").equals("true", ignoreCase = true),
                    resultado.getString("valido").equals("true", ignoreCase = true)
                )
                Mundo.addOrnamento(o)
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_TITULOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `titulos`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val o = Titulo(
                    resultado.getInt("id"),
                    resultado.getString("nombre"),
                    resultado.getInt("creditos"),
                    resultado.getInt("ogrinas"),
                    resultado.getInt("kamas"),
                    resultado.getString("vender").equals("true", ignoreCase = true),
                    resultado.getString("valido").equals("true", ignoreCase = true)
                )
                Mundo.addTitulo(o)
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_ZAAPS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `zaaps`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.addZaap(resultado.getShort("mapa"), resultado.getShort("celda"))
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_PREGUNTAS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `npc_preguntas`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.addPreguntaNPC(
                    PreguntaNPC(
                        resultado.getInt("id"),
                        resultado.getString("respuestas"),
                        resultado.getString("params"),
                        resultado.getString("alternos")
                    )
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_RESPUESTAS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `npc_respuestas`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val id = resultado.getInt("id")
                val tipo = resultado.getInt("accion")
                val args = resultado.getString("args")
                val condicion = resultado.getString("condicion")
                var respuesta: RespuestaNPC? = Mundo.getRespuestaNPC(id)
                if (respuesta == null) {
                    respuesta = RespuestaNPC(id)
                    Mundo.addRespuestaNPC(respuesta)
                }
                respuesta.addAccion(Accion(tipo, args, condicion))
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_ACCION_FINAL_DE_PELEA(): Int {
        var numero = 0
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `accion_pelea`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val mapa = Mundo.getMapa(resultado.getShort("mapa")) ?: continue
                val accion = Accion(
                    resultado.getInt("accion"),
                    resultado.getString("args"),
                    resultado.getString(
                        "condicion"
                    )
                )
                mapa.addAccionFinPelea(resultado.getInt("tipoPelea"), accion)
                numero++
            }
            cerrarResultado(resultado)
            return numero
        } catch (e: Exception) {
            exceptionExit(e)
        }

        return numero
    }

    fun CARGAR_ACCIONES_USO_OBJETOS(): Int {
        var numero = 0
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `objetos_accion`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val objMod = Mundo.getObjetoModelo(resultado.getInt("objetoModelo")) ?: continue
                objMod.addAccion(
                    Accion(
                        resultado.getInt("accion"),
                        resultado.getString("args"),
                        ""
                    )
                )
                numero++
            }
            cerrarResultado(resultado)
            return numero
        } catch (e: Exception) {
            exceptionExit(e)
        }

        return numero
    }

    fun CARGAR_TUTORIALES() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `tutoriales`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val id = resultado.getInt("id")
                val inicio = resultado.getString("inicio")
                val recompensa =
                    (resultado.getString("recompensa1") + "$" + resultado.getString("recompensa2") + "$"
                            + resultado.getString("recompensa3") + "$" + resultado.getString("recompensa4"))
                val fin = resultado.getString("final")
                Mundo.addTutorial(Tutorial(id, recompensa, inicio, fin))
            }
            cerrarResultado(resultado)
            return
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_INTERACTIVOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `objetos_interactivos`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.addObjInteractivoModelo(
                    ObjetoInteractivoModelo(
                        resultado.getInt("id"),
                        resultado.getInt("recarga"),
                        resultado.getInt("duracion"),
                        resultado.getByte("accionPJ"),
                        resultado.getByte("caminable"),
                        resultado.getByte(
                            "tipo"
                        ),
                        resultado.getString("gfx"),
                        resultado.getString("skill")
                    )
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun RECARGAR_CERCADOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `cercados`;",
                _bdDinamica!!
            )
            while (resultado.next()) {
                try {
                    Mundo.getCercadoPorMapa(resultado.getShort("mapa"))?.actualizarCercado(
                        resultado.getInt("propietario"),
                        resultado.getInt("gremio"),
                        resultado.getInt("precio"),
                        resultado.getString("objetosColocados"),
                        resultado.getString("criando")
                    )
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun RECARGAR_COFRES() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `cofres`;",
                _bdDinamica!!
            )
            while (resultado.next()) {
                try {
                    Mundo.getCofre(resultado.getInt("id"))?.actualizarCofre(
                        resultado.getString("objetos"), resultado.getLong(
                            "kamas"
                        ), resultado.getString("clave"), resultado.getInt("dueño")
                    )
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_OBJETOS_TRUEQUE() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `objetos_trueque` ORDER BY `prioridad` DESC;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                try {
                    Mundo.addObjetoTrueque(
                        resultado.getInt("idObjeto"),
                        resultado.getString("necesita"),
                        resultado.getInt(
                            "prioridad"
                        ),
                        resultado.getString("npc_ids")
                    )
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
            return
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun GET_IPS_AUTORIZADAS(cuenta: Cuenta): ArrayList<String> {
        val listaips = ArrayList<String>()
        try {
            val resultado =
                _bdDinamica?.let {
                    consultaSQL(
                        "select `ip`,`autorizado` from `historial_ip` " +
                                "where `cuenta`=${cuenta.id};", it
                    )
                } ?: return listaips
            while (resultado.next()) {
                try {
                    val a = resultado.getInt("autorizado")
                    val auth = a == 1
                    if (auth) {
                        listaips.add(resultado.getString("ip"))
                    }
                } catch (e: Exception) {
//                    exceptionExit(e)
                }
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
//            exceptionExit(e)
        }
        return listaips
    }

    fun CARGAR_ALMANAX() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `almanax`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                try {
                    if (resultado.getString("ofrenda").isEmpty()) {
                        continue
                    }
                    Mundo.addAlmanax(
                        Almanax(
                            resultado.getInt("id"),
                            resultado.getInt("tipo"),
                            resultado.getInt("bonus"),
                            resultado.getString("ofrenda")
                        )
                    )
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
            return
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_MISIONES() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `misiones`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                val mision = MisionModelo(
                    resultado.getInt("id"),
                    resultado.getString("etapas"),
                    resultado.getString("nombre"),
                    resultado.getString("pregDarMision"),
                    resultado.getString("pregMisCompletada"),
                    resultado.getString("pregMisIncompleta"),
                    resultado.getString("puedeRepetirse").equals("true", ignoreCase = true)
                )
                Mundo.addMision(mision)
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun SELECT_PUESTOS_MERCADILLOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `mercadillos`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.addPuestoMercadillo(
                    Mercadillo(
                        resultado.getInt("id"),
                        resultado.getString("mapa"),
                        resultado.getInt(
                            "porcVenta"
                        ),
                        resultado.getShort("tiempoVenta"),
                        resultado.getShort("cantidad"),
                        resultado.getShort("nivelMax"),
                        resultado.getString("categorias")
                    )
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_ETAPAS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `mision_etapas`;",
                _bdEstatica!!
            )
            while (resultado.next()) {
                Mundo.addEtapa(
                    resultado.getInt("id"),
                    resultado.getString("recompensas"),
                    resultado.getString("objetivos"),
                    resultado.getString("nombre")
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_MAPAS_ESTRELLAS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `mapas_estrellas`;",
                _bdDinamica!!
            )
            while (resultado.next()) {
                try {
                    Mundo.addMapaEstrellas(
                        resultado.getShort("mapa"),
                        resultado.getString("estrellas")
                    )
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
            return
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_OBJETOS() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `objetos`;",
                _bdDinamica!!
            )
            while (resultado.next()) {
                Mundo.objetoIniciarServer(
                    resultado.getInt("id"),
                    resultado.getInt("modelo"),
                    resultado.getInt("cantidad"),
                    resultado.getByte("posicion"),
                    resultado.getString("stats"),
                    resultado.getInt("objevivo"),
                    resultado.getInt(
                        "precio"
                    )
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun SELECT_RANKING_KOLISEO() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `ranking_koliseo`;",
                _bdDinamica!!
            )
            while (resultado.next()) {
                Mundo.addRankingKoliseo(
                    RankingKoliseo(
                        resultado.getInt("id"),
                        resultado.getString("nombre"),
                        resultado.getInt("victorias"),
                        resultado.getInt("derrotas")
                    )
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun SELECT_RANKING_PVP() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `ranking_pvp`;",
                _bdDinamica!!
            )
            while (resultado.next()) {
                Mundo.addRankingPVP(
                    RankingPVP(
                        resultado.getInt("id"),
                        resultado.getString("nombre"),
                        resultado.getInt(
                            "victorias"
                        ),
                        resultado.getInt("derrotas"),
                        resultado.getInt("nivelAlineacion")
                    )
                )
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun CARGAR_MAPAS_HEROICO() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `mapas_heroico`;",
                _bdDinamica!!
            )
            while (resultado.next()) {
                try {
                    Mundo.addMapaHeroico(
                        resultado.getShort("mapa"),
                        resultado.getString("mobs"),
                        resultado.getString("objetos"),
                        resultado.getString("kamas")
                    )
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
            return
        } catch (e: Exception) {
            exceptionExit(e)
        }

    }

    fun UPDATE_IPS_AUTORIZADAS_TOKEN(cuenta: Cuenta) {
        val consultaSQL = "UPDATE `historial_ip` SET `autorizado` = ? WHERE `cuenta` = ?;"
        try {
            val declaracion = _bdDinamica?.let {
                transaccionSQL(
                    consultaSQL,
                    it
                )
            } ?: return
            declaracion.setInt(1, 0)
            declaracion.setInt(2, cuenta.id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            REPLACE_IP_AUTORIZADA(cuenta, true)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }
    }

    fun REPLACE_IP_AUTORIZADA(cuenta: Cuenta, auth: Boolean) {
        val consultaSQL = "DELETE FROM `historial_ip` WHERE `cuenta`=? AND `ip`=?;"
        try {
            val declaracion = _bdDinamica?.let {
                transaccionSQL(
                    consultaSQL,
                    it
                )
            } ?: return
            declaracion.setInt(1, cuenta.id)
            declaracion.setString(2, cuenta.actualIP)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            INSERT_IP_AUTORIZADA(
                cuenta,
                auth
            ) // vuelta weona que me di, era con update pero me da paja solucionarlo
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }
    }

    fun INSERT_IP_AUTORIZADA(cuenta: Cuenta, auth: Boolean) {
        val consultaSQL = "INSERT INTO `historial_ip` (`cuenta`,`ip`,`autorizado`) VALUES (?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, cuenta.id)
            declaracion.setString(2, cuenta.actualIP)
            if (auth) declaracion.setInt(3, 1) else declaracion.setInt(3, 0)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }
    }

    fun INSERT_CEMENTERIO(nombre: String, nivel: Int, sexo: Byte, clase: Byte, asesino: String, subArea: Int) {
        val consultaSQL =
            "INSERT INTO `cementerio` (`nombre`,`nivel`,`sexo`,`clase`,`asesino`,`subArea`,`fecha`) VALUES (?,?,?,?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setString(1, nombre)
            declaracion.setInt(2, nivel)
            declaracion.setByte(3, sexo)
            declaracion.setByte(4, clase)
            declaracion.setString(5, asesino)
            declaracion.setInt(6, subArea)
            declaracion.setLong(7, System.currentTimeMillis())
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun INSERT_OBJETO_TRUEQUE(objeto: Int, solicita: String, prioridad: Int, npcs: String, nombre: String) {
        val consultaSQL =
            "INSERT INTO `objetos_trueque` (`idObjeto`,`necesita`,`prioridad`,`npc_ids`,`nombre_objeto`) VALUES (?,?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, objeto)
            declaracion.setString(2, solicita)
            declaracion.setInt(3, prioridad)
            declaracion.setString(4, npcs)
            declaracion.setString(5, nombre)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_STATS_OBJETO_SET(id: Int, bonus: String) {
        val consultaSQL = "UPDATE `objetos_set` SET `bonus` = ? WHERE `id` = ? ;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, bonus)
            declaracion.setInt(2, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_PRECIO_OBJETO_MODELO(id: Int, ogrinas: Int, vip: Boolean) {
        var consultaSQL = "UPDATE `objetos_modelo` SET `kamas` = ? WHERE `id` = ? ;"
        if (vip) {
            consultaSQL = "UPDATE `objetos_modelo` SET `ogrinas` = ? WHERE `id` = ? ;"
        }
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, ogrinas)
            declaracion.setInt(2, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_STATS_OBJETO_MODELO(id: Int, stats: String) {
        val consultaSQL = "UPDATE `objetos_modelo` SET `statsModelo` = ? WHERE `id` = ? ;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, stats)
            declaracion.setInt(2, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun INSERT_COFRE_MODELO(casaID: Int, mapaID: Short, celdaID: Short) {
        val consultaSQL = "INSERT INTO `cofres_modelo` (`casa`,`mapa`,`celda`) VALUES (?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, casaID)
            declaracion.setInt(2, mapaID.toInt())
            declaracion.setInt(3, celdaID.toInt())
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun GET_COFRE_POR_MAPA_CELDA(mapa: Short, celda: Short): Int {
        var id = -1
        val consultaSQL = "SELECT * FROM `cofres_modelo` WHERE `mapa` = '$mapa' AND `celda` = '$celda';"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdEstatica!!
            )
            if (resultado.first()) {
                id = resultado.getInt("id")
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return id
    }

    fun REPLACE_COFRE(cofre: Cofre?, salvarObjetos: Boolean) {
        if (cofre == null) {
            return
        }
        val consultaSQL = "REPLACE INTO `cofres` VALUES (?,?,?,?,?)"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, cofre.iD)
            declaracion.setString(2, cofre.analizarObjetoCofreABD())
            declaracion.setLong(3, cofre.kamas)
            declaracion.setString(4, cofre.clave)
            declaracion.setInt(5, cofre.dueñoID)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            if (salvarObjetos) {
                SALVAR_OBJETOS(cofre.objetos)
            }
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    // private static void CARGAR_PERSONAJES_POR_CUENTA(final Cuenta cuenta) {
    // try {
    // final ResultSet resultado = consultaSQL("SELECT * FROM `personajes` WHERE `cuenta` = " +
    // cuenta.getID() + ";",
    // _bdDinamica);
    // while (resultado.next()) {
    // try {
    // cuenta.addPersonaje(Mundo.getPersonaje(resultado.getInt("id")));
    // } catch (final Exception e) {
    // Bustemu.redactarLogServidorln("El personaje " + resultado.getString("nombre")
    // + " no se pudo agregar a la cuenta (REFRESCAR CUENTA)");
    // }
    // }
    // cerrarResultado(resultado);
    // } catch (final Exception e) {
    // Bustemu.redactarLogServidorln("ERROR SQL: " + e.toString());
    // e.printStackTrace();
    // }
    // }
    fun DELETE_PERSONAJE(perso: Personaje) {
        val consultaSQL = "DELETE FROM `personajes` WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, perso.Id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun INSERT_CAPTCHA(captcha: String, respuesta: String) {
        val consultaSQL = "INSERT INTO `captchas` (`captcha`,`respuesta`) VALUES (?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, captcha)
            declaracion.setString(2, respuesta)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (ignored: Exception) {
        }

    }

    fun INSERT_MAPA(
        id: Short, fecha: String, ancho: Byte, alto: Byte,
        mapData: String, X: Short, Y: Short, subArea: Short
    ) {
        val consultaSQL =
            "INSERT INTO `mapas` (`id`,`fecha`,`ancho`,`alto`,`mapData`,`X`,`Y`, `subArea`,`key`, `mobs`) VALUES (?,?,?,?,?,?,?,?,'','');"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setShort(1, id)
            declaracion.setString(2, fecha)
            declaracion.setByte(3, ancho)
            declaracion.setByte(4, alto)
            declaracion.setString(5, mapData)
            declaracion.setShort(6, X)
            declaracion.setShort(7, Y)
            declaracion.setShort(8, subArea)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_GFX_OBJMODELO(id: Int, gfx: Int) {
        val consultaSQL = "UPDATE `objetos_modelo` SET `gfx` = ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, gfx)
            declaracion.setInt(2, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_NIVEL_OBJMODELO(id: Int, nivel: Short) {
        val consultaSQL = "UPDATE `objetos_modelo` SET `nivel` = ?, `nivelCore` = 'true' WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setShort(1, nivel)
            declaracion.setInt(2, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun ACTUALIZAR_NPC_VENTAS(npc: NPCModelo) {
        val consultaSQL = "UPDATE `npcs_modelo` SET `ventas` = ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, npc.actualizarStringBD())
            declaracion.setInt(2, npc.id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun REPLACE_GRUPOMOB_FIJO(
        mapaID: Int, celdaID: Int, grupoData: String, tipo: Int,
        condicion: String, segundos: Int
    ) {
        val consultaSQL =
            "REPLACE INTO `mobs_fix` (`mapa`,`celda`,`mobs`,`tipo`,`condicion`,`segundosRespawn`,`descripcion`) VALUES (?,?,?,?,?,?,'')"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, mapaID)
            declaracion.setInt(2, celdaID)
            declaracion.setString(3, grupoData)
            declaracion.setInt(4, tipo)
            declaracion.setString(5, condicion)
            declaracion.setInt(6, segundos)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_MISION(
        id: Int, etapas: String, pregDarMision: String,
        pregMisCompletada: String, pregMisIncompleta: String
    ) {
        val consultaSQL =
            "UPDATE `misiones` SET `etapas`= ?, `pregDarMision`= ?, `pregMisCompletada`= ?, `pregMisIncompleta`= ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, etapas)
            declaracion.setString(2, pregDarMision)
            declaracion.setString(3, pregMisCompletada)
            declaracion.setString(4, pregMisIncompleta)
            declaracion.setInt(5, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_OBJETIVO_MISION(id: Int, args: String) {
        val consultaSQL = "UPDATE `mision_objetivos` SET `args`= ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, args)
            declaracion.setInt(2, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_RECOMPENSA_ETAPA(id: Int, recompensas: String) {
        val consultaSQL = "UPDATE `mision_etapas` SET `recompensas`= ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, recompensas)
            declaracion.setInt(2, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_ETAPA(id: Int, objetivos: String) {
        val consultaSQL = "UPDATE `mision_etapas` SET `objetivos`= ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, objetivos)
            declaracion.setInt(2, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_NPC_MODELO(
        npcMod: NPCModelo, arma: Int, sombrero: Int, capa: Int,
        mascota: Int, escudo: Int
    ) {
        val consultaSQL =
            "UPDATE `npcs_modelo` SET `sexo`= ?, `scaleX`= ?, `scaleY`= ?, `gfxID`= ?, `color1`= ?, `color2`= ?, `color3`= ?, `arma`= ?, `sombrero`= ?, `capa`= ?, `mascota`= ?, `escudo`= ?  WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setByte(1, npcMod.Sexo)
            declaracion.setShort(2, npcMod.TallaX)
            declaracion.setShort(3, npcMod.TallaY)
            declaracion.setInt(4, npcMod.GfxID)
            declaracion.setInt(5, npcMod.Color1)
            declaracion.setInt(6, npcMod.Color2)
            declaracion.setInt(7, npcMod.Color3)
            declaracion.setInt(8, arma)
            declaracion.setInt(9, sombrero)
            declaracion.setInt(10, capa)
            declaracion.setInt(11, mascota)
            declaracion.setInt(12, escudo)
            declaracion.setInt(13, npcMod.id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_ALMANAX(id: Int, ofrenda: String, tipo: Int, bonus: Int) {
        val consultaSQL = "UPDATE `almanax` SET `ofrenda`=?, `tipo`=?, `bonus`= ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, ofrenda)
            declaracion.setInt(2, tipo)
            declaracion.setInt(3, bonus)
            declaracion.setInt(4, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun CAMBIAR_SEXO_CLASE(perso: Personaje) {
        val consultaSQL = "UPDATE `personajes` SET `sexo`=?, `clase`= ?, `hechizos`= ? WHERE `id`= ?"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, perso.sexo.toInt())
            declaracion.setInt(2, perso.getClaseID(true).toInt())
            declaracion.setString(3, perso.stringHechizosParaSQL())
            declaracion.setInt(4, perso.Id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_NOMBRE_PJ(perso: Personaje) {
        val consultaSQL = "UPDATE `personajes` SET `nombre` = ? WHERE `id` = ? ;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setString(1, perso.nombre)
            declaracion.setInt(2, perso.Id)
            ejecutarTransaccion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_COLORES_PJ(perso: Personaje) {
        val consultaSQL = "UPDATE `personajes` SET `color1` = ?, `color2`= ?, `color3` = ? WHERE `id` = ? ;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, perso.color1)
            declaracion.setInt(2, perso.color2)
            declaracion.setInt(3, perso.color3)
            declaracion.setInt(4, perso.Id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun SALVAR_PERSONAJE(perso: Personaje, salvarObjetos: Boolean) {
        val consultaSQL =
            "REPLACE INTO `personajes` VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);"
        try {
            var parametro = 1
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(parametro++, perso.Id)
            declaracion.setString(parametro++, perso.nombre)
            declaracion.setByte(parametro++, perso.sexo)
            declaracion.setByte(parametro++, perso.getClaseID(true))
            declaracion.setInt(parametro++, perso.color1)
            declaracion.setInt(parametro++, perso.color2)
            declaracion.setInt(parametro++, perso.color3)
            declaracion.setLong(parametro++, perso.kamas)
            declaracion.setInt(parametro++, perso.puntosHechizos)
            declaracion.setInt(parametro++, perso.capital)
            declaracion.setInt(parametro++, perso.energia)
            declaracion.setInt(parametro++, perso.nivel)
            declaracion.setLong(parametro++, perso.experiencia)
            declaracion.setInt(parametro++, perso.talla)
            declaracion.setInt(parametro++, perso.gfxIDReal)
            declaracion.setInt(parametro++, perso.alineacion.toInt())
            declaracion.setInt(parametro++, perso.honor)
            declaracion.setInt(parametro++, perso.deshonor)
            declaracion.setInt(parametro++, perso.gradoAlineacion)
            declaracion.setInt(parametro++, perso.cuentaID)
            declaracion.setInt(
                parametro++,
                if (perso.subStatsBase[Constantes.STAT_MAS_VITALIDAD] != null) perso.subStatsBase[Constantes.STAT_MAS_VITALIDAD]!! else 0
            )
            declaracion.setInt(
                parametro++,
                if (perso.subStatsBase[Constantes.STAT_MAS_FUERZA] != null) perso.subStatsBase[Constantes.STAT_MAS_FUERZA]!! else 0
            )
            declaracion.setInt(
                parametro++,
                if (perso.subStatsBase[Constantes.STAT_MAS_SABIDURIA] != null) perso.subStatsBase[Constantes.STAT_MAS_SABIDURIA]!! else 0
            )
            declaracion.setInt(
                parametro++,
                if (perso.subStatsBase[Constantes.STAT_MAS_INTELIGENCIA] != null) perso.subStatsBase[Constantes.STAT_MAS_INTELIGENCIA]!! else 0
            )
            declaracion.setInt(
                parametro++,
                if (perso.subStatsBase[Constantes.STAT_MAS_SUERTE] != null) perso.subStatsBase[Constantes.STAT_MAS_SUERTE]!! else 0
            )
            declaracion.setInt(
                parametro++,
                if (perso.subStatsBase[Constantes.STAT_MAS_AGILIDAD] != null) perso.subStatsBase[Constantes.STAT_MAS_AGILIDAD]!! else 0
            )
            declaracion.setInt(parametro++, if (perso.mostrarAmigos()) 1 else 0)
            declaracion.setInt(parametro++, if (perso.alasActivadas()) 1 else 0)
            declaracion.setString(parametro++, perso.canales)
            declaracion.setInt(parametro++, perso.mapa.id.toInt())
            declaracion.setInt(parametro++, perso.celda.id.toInt())
            declaracion.setInt(parametro++, perso.porcPDV.toInt())
            declaracion.setString(parametro++, perso.stringHechizosParaSQL())
            declaracion.setString(parametro++, perso.stringObjetosABD())
            declaracion.setString(parametro++, perso.ptoSalvada)
            declaracion.setString(parametro++, perso.stringZaapsParaBD())
            declaracion.setString(parametro++, perso.stringOficios())
            declaracion.setInt(parametro++, perso.porcXPMontura)
            declaracion.setInt(parametro++, if (perso.montura != null) perso.montura.id else -1)
            declaracion.setInt(parametro++, perso.esposoID)
            declaracion.setString(parametro++, perso.stringTienda)
            declaracion.setInt(parametro++, if (perso.esMercante()) 1 else 0)
            declaracion.setInt(
                parametro++,
                if (perso.subStatsScroll[Constantes.STAT_MAS_FUERZA] != null) perso.subStatsScroll[Constantes.STAT_MAS_FUERZA]!! else 0
            )
            declaracion.setInt(
                parametro++,
                if (perso.subStatsScroll[Constantes.STAT_MAS_INTELIGENCIA] != null) perso.subStatsScroll[Constantes.STAT_MAS_INTELIGENCIA]!! else 0
            )
            declaracion.setInt(
                parametro++,
                if (perso.subStatsScroll[Constantes.STAT_MAS_AGILIDAD] != null) perso.subStatsScroll[Constantes.STAT_MAS_AGILIDAD]!! else 0
            )
            declaracion.setInt(
                parametro++,
                if (perso.subStatsScroll[Constantes.STAT_MAS_SUERTE] != null) perso.subStatsScroll[Constantes.STAT_MAS_SUERTE]!! else 0
            )
            declaracion.setInt(
                parametro++,
                if (perso.subStatsScroll[Constantes.STAT_MAS_VITALIDAD] != null) perso.subStatsScroll[Constantes.STAT_MAS_VITALIDAD]!! else 0
            )
            declaracion.setInt(
                parametro++,
                if (perso.subStatsScroll[Constantes.STAT_MAS_SABIDURIA] != null) perso.subStatsScroll[Constantes.STAT_MAS_SABIDURIA]!! else 0
            )
            declaracion.setLong(parametro++, perso.restriccionesA.toLong())
            declaracion.setLong(parametro++, perso.restriccionesB.toLong())
            declaracion.setInt(parametro++, 0)
            declaracion.setInt(parametro++, perso.emotes)
            declaracion.setString(parametro++, perso.listaTitulosParaBD())
            declaracion.setString(parametro++, perso.tituloVIP)
            declaracion.setString(parametro++, perso.listaOrnamentosParaBD())
            declaracion.setString(parametro++, perso.stringMisiones())
            declaracion.setString(parametro++, perso.listaCardMobs())
            declaracion.setByte(parametro++, perso.resets)
            declaracion.setString(parametro++, perso.listaAlmanax())
            declaracion.setInt(parametro++, perso.ultimoNivel)
            declaracion.setString(parametro++, perso.setsRapidos)
            declaracion.setInt(parametro++, perso.colorNombre)
            declaracion.setString(parametro++, perso.orden.toString() + "," + perso.ordenNivel)
            ejecutarTransaccion(declaracion)
            val str = declaracion.toString()
            perso.registrar("<=SQL=> " + str.substring(str.indexOf(":")))
            if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
                AtlantaMain.redactarLogServidorSinPrint(
                    "SAVE SQL [" + perso.nombre + "] ==>" + str.substring(
                        str.indexOf(
                            ":"
                        )
                    )
                )
            }
            cerrarDeclaracion(declaracion)
            if (perso.miembroGremio != null) {
                REPLACE_MIEMBRO_GREMIO(perso.miembroGremio)
            }
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "PERSONAJE NO SALVADO")
        }

        if (salvarObjetos) {
            SALVAR_OBJETOS(perso.objetosTienda)
            SALVAR_OBJETOS(perso.objetosTodos)
            if (perso.montura != null) {
                REPLACE_MONTURA(perso.montura, true)
            }
        }
    }

    fun SALVAR_OBJETOS(objetos: Collection<Objeto?>?) {
        if (objetos == null || objetos.isEmpty()) {
            return
        }
        var tempObjetos: List<Objeto?>? = ArrayList(objetos)
        val consultaSQL = "REPLACE INTO `objetos` VALUES(?,?,?,?,?,?,?,?);"
        try {
            for (obj in tempObjetos!!) {
                if (obj == null) {
                    continue
                } else {
                    val declaracion = transaccionSQL(
                        consultaSQL,
                        _bdDinamica!!
                    )
                    declaracion.setInt(1, obj.id)
                    declaracion.setInt(2, obj.objModeloID)
                    declaracion.setInt(3, obj.cantidad)
                    declaracion.setInt(4, obj.posicion.toInt())
                    declaracion.setString(5, obj.convertirStatsAString(true))
                    declaracion.setInt(6, obj.objevivoID)
                    declaracion.setLong(7, obj.precio.toLong())
                    declaracion.setInt(8, obj.dueñoTemp)
                    ejecutarTransaccion(declaracion)
                }
            }
            tempObjetos = null
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun SALVAR_OBJETO(objeto: Objeto) {
        val consultaSQL = "REPLACE INTO `objetos` VALUES (?,?,?,?,?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, objeto.id)
            declaracion.setInt(2, objeto.objModeloID)
            declaracion.setInt(3, objeto.cantidad)
            declaracion.setInt(4, objeto.posicion.toInt())
            declaracion.setString(5, objeto.convertirStatsAString(true))
            declaracion.setInt(6, objeto.objevivoID)
            declaracion.setLong(7, objeto.precio.toLong())
            declaracion.setInt(8, objeto.dueñoTemp)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun VACIAR_MAPAS_ESTRELLAS() {
        try {
            val declaracion = transaccionSQL(
                "TRUNCATE `mapas_estrellas`;",
                _bdDinamica!!
            )
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

    }

    fun VACIAR_MAPAS_HEROICO() {
        try {
            val declaracion = transaccionSQL(
                "TRUNCATE `mapas_heroico`;",
                _bdDinamica!!
            )
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

    }

    fun GET_STATEMENT_SQL_DINAMICA(consultaSQL: String): PreparedStatement? {
        try {
            return transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
        } catch (ignored: Exception) {
        }

        return null
    }

    fun REPLACE_MAPAS_ESTRELLAS_BATCH(declaracion: PreparedStatement, mapaID: Int, estrellas: String) {
        try {
            declaracion.setInt(1, mapaID)
            declaracion.setString(2, estrellas)
            declaracion.addBatch()
        } catch (ignored: Exception) {
        }

        return
    }

    fun REPLACE_MAPAS_HEROICO(mapaID: Int, mobs: String, objetos: String, kamas: String) {
        val consultaSQL = "REPLACE INTO `mapas_heroico` VALUES (?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, mapaID)
            declaracion.setString(2, mobs)
            declaracion.setString(3, objetos)
            declaracion.setString(4, kamas)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_MAPA_HEROICO(mapaID: Int) {
        val consultaSQL = "DELETE FROM `mapas_heroico` WHERE `mapa` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, mapaID)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_MONTURA(drago: Montura) {
        val consultaSQL = "DELETE FROM `monturas` WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, drago.id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_DRAGOPAVO_LISTA(lista: String) {
        val consultaSQL = "DELETE FROM `monturas` WHERE `id` IN ($lista);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_OBJETOS_LISTA(lista: String) {
        val consultaSQL = "DELETE FROM `objetos` WHERE `id` IN ($lista);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_OBJETO(id: Int) {
        val consultaSQL = "DELETE FROM `objetos` WHERE `id` = ?"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun ACTUALIZAR_TITULO_POR_NOMBRE(nombre: String) {
        val consultaSQL = "UPDATE `personajes` SET `titulo` = 0 WHERE `nombre` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setString(1, nombre)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun REPLACE_MONTURA(montura: Montura, salvarObjetos: Boolean) {
        val consultaSQL = "REPLACE INTO `monturas` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, montura.id)
            declaracion.setInt(2, montura.color)
            declaracion.setInt(3, montura.sexo)
            declaracion.setString(4, montura.nombre)
            declaracion.setLong(5, montura.exp)
            declaracion.setInt(6, montura.nivel)
            declaracion.setInt(7, montura.talla)
            declaracion.setInt(8, montura.resistencia)
            declaracion.setInt(9, montura.amor)
            declaracion.setInt(10, montura.madurez)
            declaracion.setInt(11, montura.serenidad)
            declaracion.setInt(12, montura.reprod)
            declaracion.setInt(13, montura.fatiga)
            declaracion.setInt(14, montura.energia)
            declaracion.setString(15, montura.stringObjetosBD())
            declaracion.setString(16, montura.ancestros)
            declaracion.setString(17, montura.strCapacidades())
            declaracion.setInt(18, montura.orientacion)
            declaracion.setInt(19, (if (montura.celda == null) -1 else montura.celda!!.id).toInt())
            declaracion.setInt(20, (if (montura.mapa == null) -1 else montura.mapa!!.id).toInt())
            declaracion.setInt(21, montura.dueñoID)
            declaracion.setLong(22, montura.tiempoFecundacion)
            declaracion.setInt(23, montura.parejaID)
            declaracion.setString(24, if (montura.esSalvaje()) "1" else "0")
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            if (salvarObjetos) {
                SALVAR_OBJETOS(montura.objetos)
            }
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_CERCADO(id: Int) {
        val consultaSQL = "DELETE FROM `cercados` WHERE `mapa` = ? ;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun REPLACE_CERCADO(cercado: Cercado) {
        var consultaSQL = "REPLACE INTO `cercados` VALUES (?,?,?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, cercado.mapa!!.id.toInt())
            declaracion.setInt(2, cercado.dueñoID)
            declaracion.setInt(3, if (cercado.gremio == null) -1 else cercado.gremio!!.id)
            declaracion.setInt(4, cercado.precioPJ)
            declaracion.setString(5, cercado.strPavosCriando())
            declaracion.setString(6, cercado.strObjCriaParaBD())
            ejecutarTransaccion(declaracion)
            consultaSQL = "REPLACE INTO `objetos` VALUES (?,?,?,?,?,?,?,?);"
            try {
                for (obj in cercado.objetosParaBD) {
                    val declaracion = transaccionSQL(
                        consultaSQL,
                        _bdDinamica!!
                    )
                    declaracion.setInt(1, obj.id)
                    declaracion.setInt(2, obj.objModeloID)
                    declaracion.setInt(3, obj.cantidad)
                    declaracion.setInt(4, obj.posicion.toInt())
                    declaracion.setString(5, obj.convertirStatsAString(true))
                    declaracion.setInt(6, obj.objevivoID)
                    declaracion.setLong(7, obj.precio.toLong())
                    declaracion.setInt(8, obj.dueñoTemp)
                    ejecutarTransaccion(declaracion)
                }
            } catch (ignored: Exception) {
            }

            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun REPLACE_RANKING_KOLISEO(rank: RankingKoliseo) {
        val consultaSQL = "REPLACE INTO `ranking_koliseo` VALUES (?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, rank.id)
            declaracion.setString(2, rank.nombre)
            declaracion.setInt(3, rank.victorias)
            declaracion.setInt(4, rank.derrotas)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_RANKING_KOLISEO(id: Int): Boolean {
        val consultaSQL = "DELETE FROM `ranking_koliseo` WHERE `id` = ? ;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return false
    }

    fun REPLACE_RANKING_PVP(rank: RankingPVP) {
        val consultaSQL = "REPLACE INTO `ranking_pvp` VALUES (?,?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, rank.id)
            declaracion.setString(2, rank.nombre)
            declaracion.setInt(3, rank.victorias)
            declaracion.setInt(4, rank.derrotas)
            declaracion.setInt(5, rank.gradoAlineacion)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_RANKING_PVP(id: Int) {
        val consultaSQL = "DELETE FROM `ranking_pvp` WHERE `id` = ? ;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun REPLACE_ACCION_OBJETO(
        idModelo: Int, accion: Int, args: String,
        nombre: String
    ) {
        val consultaSQL = "REPLACE INTO `objetos_accion` VALUES(?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, idModelo)
            declaracion.setInt(2, accion)
            declaracion.setString(3, args)
            declaracion.setString(4, nombre)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_ACCION_OBJETO(id: Int) {
        val consultaSQL = "DELETE FROM `objetos_accion` WHERE `objetoModelo` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun INSERT_DROP(
        mob: Int, objeto: Int, prosp: Int, porcentaje: Float, max: Int,
        nMob: String, nObjeto: String, condicion: String
    ) {
        val consultaSQL =
            "INSERT INTO `drops` (`mob`,`objeto`,`prospeccion`, `porcentaje`,`max`, `nombre_mob`, `nombre_objeto`,`condicion`) VALUES (?,?,?,?,?,?,?,?);"
        try {
            DELETE_DROP(objeto, mob)
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, mob)
            declaracion.setInt(2, objeto)
            declaracion.setInt(3, prosp)
            declaracion.setFloat(4, porcentaje)
            declaracion.setInt(5, max)
            declaracion.setString(6, nMob)
            declaracion.setString(7, nObjeto)
            declaracion.setString(8, condicion)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun INSERTAR_MENSAJE_PENDIENTE(invitador: Cuenta, invitado: Personaje, Estado: Int) {
        val consultaSQL = "INSERT INTO `mensajes_pendientes` (`cuenta`,`mensaje`,`verificador`) VALUES (?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, invitador.id)
            declaracion.setString(
                2,
                "En tu auscencia has recibido: " + AtlantaMain.OGRINAS_INVITADOR + " Ogrinas. Por invitar a " + invitado.nombre
            )
            declaracion.setInt(3, Estado)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }
    }

    fun MENSAJE_PENDIENTE(idCuentaDestino: Cuenta, remitente: String, mensaje: String) {
        val consultaSQL = "INSERT INTO `mensajes_pendientes` (`cuenta`,`mensaje`,`verificador`) VALUES (?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, idCuentaDestino.id)
            declaracion.setString(2, "\n${ServidorServer.fechaConHora}\n[$remitente]: $mensaje")
            declaracion.setInt(3, 0)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }
    }

    fun GET_MENSAJE_PENDIENTE(p: Personaje) {
        val consultaSQL =
            "SELECT `mensaje` FROM `mensajes_pendientes` where `cuenta`=" + p.cuentaID + " and `verificador` != 1;"
        try {
            try {
                val resultado = consultaSQL(
                    consultaSQL,
                    _bdDinamica!!
                )
                while (resultado.next()) {
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                        p,
                        resultado.getString("mensaje")
                    )
                }
                UPDATE_MENSAJE_PENDIENTE(p.cuentaID.toString())
                cerrarResultado(resultado)
            } catch (e: Exception) {
                exceptionNormal(e, "")
            }

        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun GET_OGRINAS_REGALADAS(): Int {
        val consultaSQL = "SELECT ultimaIP from cuentas_servidor WHERE vreferido=1 GROUP BY ultimaIP;"
        var contador = 0
        try {
            try {
                val resultado = consultaSQL(
                    consultaSQL,
                    _bdDinamica!!
                )
                while (resultado.next()) {
                    contador += 1
                }
                cerrarResultado(resultado)
            } catch (e: Exception) {
                exceptionNormal(e, "")
            }

        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return contador * (AtlantaMain.OGRINAS_INVITADOR + AtlantaMain.OGRINAS_INVITADO)
    }

    private fun UPDATE_MENSAJE_PENDIENTE(idcuenta: String) {
        val consultaSQL = "UPDATE `mensajes_pendientes` SET `verificador`=1 WHERE `cuenta`=?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setString(1, idcuenta)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_DROPS(
        idMob: Int, idObjeto: Int, nombreMob: String,
        nombreObjeto: String
    ) {
        val consultaSQL = "UPDATE `drops` SET `nombre_mob`=?, `nombre_objeto` =? WHERE `mob`=? AND `objeto`= ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, nombreMob)
            declaracion.setString(2, nombreObjeto)
            declaracion.setInt(3, idMob)
            declaracion.setInt(4, idObjeto)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_DROP(objeto: Int, mob: Int) {
        val consultaSQL = "DELETE FROM `drops` WHERE `objeto` ='$objeto' AND `mob`= '$mob' ;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun ACTUALIZAR_SERVER(estatica: Boolean, dinamica: Boolean, cuentas: Boolean) {
        try {
            if (AtlantaMain.ES_LOCALHOST) {
                return
            }
            if (estatica) {
                val declaracion = transaccionSQL(
                    "DROP DATABASE " + AtlantaMain.BD_ESTATICA + " ;",
                    _bdEstatica!!
                )
                ejecutarTransaccion(declaracion)
                cerrarDeclaracion(declaracion)
            }
            if (dinamica) {
                val declaracion = transaccionSQL(
                    "DROP DATABASE " + AtlantaMain.BD_DINAMICA + " ;",
                    _bdDinamica!!
                )
                ejecutarTransaccion(declaracion)
                cerrarDeclaracion(declaracion)
            }
            if (cuentas) {
                val declaracion = transaccionSQL(
                    "DROP DATABASE " + AtlantaMain.BD_CUENTAS + " ;",
                    _bdCuentas!!
                )
                ejecutarTransaccion(declaracion)
                cerrarDeclaracion(declaracion)
            }
        } catch (ignored: Exception) {
        }

    }

    fun QUERY_ESTATICA(query: String) {
        try {
            val declaracion = transaccionSQL(
                query,
                _bdEstatica!!
            )
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            e.toString()
        }

    }

    fun QUERY_DINAMICA(query: String) {
        try {
            val declaracion = transaccionSQL(
                query,
                _bdDinamica!!
            )
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            e.toString()
        }

    }

    fun QUERY_CUENTAS(query: String) {
        try {
            val declaracion = transaccionSQL(
                query,
                _bdCuentas!!
            )
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            e.toString()
        }

    }

    fun QUERY_ALTERNA(query: String) {
        try {
            val declaracion = transaccionSQL(
                query,
                _bdAlterna!!
            )
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            e.toString()
        }

    }

    fun REPLACE_CELDAS_ACCION(
        mapa1: Int, celda1: Int, accion: Int, args: String,
        condicion: String
    ): Boolean {
        val consultaSQL = "REPLACE INTO `celdas_accion` VALUES (?,?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, mapa1)
            declaracion.setInt(2, celda1)
            declaracion.setInt(3, accion)
            declaracion.setString(4, args)
            declaracion.setString(5, condicion)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return false
    }

    fun REPLACE_OBJETO_MODELO(
        id: Int, tipo: Short, nombre: String, gfx: Short,
        nivelCore: Boolean, nivel: Short, stats: String, peso: Short, set: Short, kamas: Int,
        ogrinas: Int, magueable: Boolean, infoArma: String, condicion: String
    ): Boolean {
        val consultaSQL = "REPLACE INTO `objetos_modelo` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,'0','0');"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, id)
            declaracion.setShort(2, tipo)
            declaracion.setString(3, nombre)
            declaracion.setShort(4, gfx)
            declaracion.setString(5, if (nivelCore) "true" else "false")
            declaracion.setShort(6, nivel)
            declaracion.setString(7, stats)
            declaracion.setShort(8, peso)
            declaracion.setShort(9, set)
            declaracion.setInt(10, kamas)
            declaracion.setInt(11, ogrinas)
            declaracion.setString(12, if (magueable) "true" else "false")
            declaracion.setString(13, infoArma)
            declaracion.setString(14, condicion)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return false
    }

    fun DELETE_TRIGGER(mapaID: Int, celdaID: Int): Boolean {
        val consultaSQL = "DELETE FROM `celdas_accion` WHERE `mapa` = ? AND `celda` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, mapaID)
            declaracion.setInt(2, celdaID)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return false
    }

    fun UPDATE_MAPA_POS_PELEA(mapaID: Int, pos: String): Boolean {
        val consultaSQL = "UPDATE `mapas` SET `posPelea` = ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, pos)
            declaracion.setInt(2, mapaID)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            return false
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return true
    }

    fun UPDATE_MAPA_MAX_PELEAS(mapaID: Short, max: Byte) {
        val consultaSQL = "UPDATE `mapas` SET `maxPeleas` = ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setByte(1, max)
            declaracion.setShort(2, mapaID)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_MAPA_MAX_MERCANTES(mapaID: Short, max: Byte): Boolean {
        val consultaSQL = "UPDATE `mapas` SET `maxMercantes` = ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setByte(1, max)
            declaracion.setShort(2, mapaID)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return false
    }

    fun UPDATE_MAPA_MAX_GRUPO_MOBS(mapaID: Int, max: Byte): Boolean {
        val consultaSQL = "UPDATE `mapas` SET `maxGrupoMobs` = ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setByte(1, max)
            declaracion.setInt(2, mapaID)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return false
    }

    fun UPDATE_MAPA_MAX_MOB_GRUPO(mapaID: Int, max: Byte): Boolean {
        val consultaSQL = "UPDATE `mapas` SET `maxMobsPorGrupo` = ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setByte(1, max)
            declaracion.setInt(2, mapaID)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return false
    }

    fun UPDATE_MAPA_PARAMETROS(id: Int, param: Int) {
        val consultaSQL = "UPDATE `mapas` SET `capabilities` = ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, param)
            declaracion.setInt(2, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_NPC_DEL_MAPA(mapa: Int, id: Int): Boolean {
        val consultaSQL = "DELETE FROM `npcs_ubicacion` WHERE `mapa` = ? AND `npc` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, mapa)
            declaracion.setInt(2, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return false
    }

    private fun DELETE_NPC_UBICACION(id: Int) {
        val consultaSQL = "DELETE FROM `npcs_ubicacion` WHERE `npc` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_RECAUDADOR(id: Int) {
        val consultaSQL = "DELETE FROM `recaudadores` WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            return
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun REPLACE_NPC_AL_MAPA(
        mapa: Short, celda: Short, id: Int, direccion: Byte,
        nombre: String
    ): Boolean {
        val consultaSQL = "REPLACE INTO `npcs_ubicacion` VALUES (?,?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setShort(1, mapa)
            declaracion.setShort(2, celda)
            declaracion.setInt(3, id)
            declaracion.setByte(4, direccion)
            declaracion.setString(5, nombre)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return false
    }

    fun REPLACE_RECAUDADOR(recaudador: Recaudador, salvarObjetos: Boolean) {
        val consultaSQL = "REPLACE INTO `recaudadores` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, recaudador.id)
            recaudador.mapa?.id?.toInt()?.let { declaracion.setInt(2, it) }
            recaudador.celda?.id?.toInt()?.let { declaracion.setInt(3, it) }
            declaracion.setInt(4, recaudador.orientacion)
            recaudador.gremio?.id?.let { declaracion.setInt(5, it) }
            declaracion.setString(6, recaudador.n1)
            declaracion.setString(7, recaudador.n2)
            declaracion.setString(8, recaudador.stringListaObjetosBD())
            declaracion.setLong(9, recaudador.kamas)
            declaracion.setLong(10, recaudador.exp)
            declaracion.setLong(11, recaudador.tiempoProteccion)
            declaracion.setInt(12, recaudador.dueño)
            declaracion.setLong(13, recaudador.tiempoCreacion)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            try {
                if (salvarObjetos) {
                    SALVAR_OBJETOS(recaudador.objetos)
                }
            } catch (ignored: Exception) {
            }

        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun INSERT_LOG_PELEA(
        cuentas_g: String,
        personajes_g: String,
        ips_g: String,
        puntos_g: String,
        cuentas_p: String,
        personajes_p: String,
        ips_p: String,
        puntos_p: String,
        duracion: Long,
        agresor: Int,
        agredido: Int,
        mapa: Short
    ) {
        val consultaSQL =
            "INSERT INTO `logs_aggro` (`gagnant_account`,`gagnant_perso`,`gagnant_ip`,`gagnant_ph`,`perdant_account`,`perdant_perso`,`perdant_ip`,`perdant_ph`,`duree`,`aggroBy`,`aggroTo`,`idMap`,`timestamp`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setString(1, cuentas_g)
            declaracion.setString(2, personajes_g)
            declaracion.setString(3, ips_g)
            declaracion.setString(4, puntos_g)
            declaracion.setString(5, cuentas_p)
            declaracion.setString(6, personajes_p)
            declaracion.setString(7, ips_p)
            declaracion.setString(8, puntos_p)
            declaracion.setLong(9, duracion)
            declaracion.setInt(10, agresor)
            declaracion.setInt(11, agredido)
            declaracion.setInt(12, mapa.toInt())
            declaracion.setLong(13, System.currentTimeMillis())
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun INSERT_ACCION_FIN_PELEA(
        mapaID: Int, tipoPelea: Int, accionID: Int,
        args: String, condicion: String, descripcion: String
    ): Boolean {
        DELETE_FIN_ACCION_PELEA(mapaID, tipoPelea, accionID)
        val consultaSQL = "INSERT INTO `accion_pelea` VALUES (?,?,?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, mapaID)
            declaracion.setInt(2, tipoPelea)
            declaracion.setInt(3, accionID)
            declaracion.setString(4, args)
            declaracion.setString(5, condicion)
            declaracion.setString(6, descripcion)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return false
    }

    private fun DELETE_FIN_ACCION_PELEA(mapaID: Int, tipoPelea: Int, accionID: Int) {
        val consultaSQL = "DELETE FROM `accion_pelea` WHERE mapa = ? AND tipoPelea = ? AND accion = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, mapaID)
            declaracion.setInt(2, tipoPelea)
            declaracion.setInt(3, accionID)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun INSERT_GREMIO(gremio: Gremio) {
        val consultaSQL = "INSERT INTO `gremios` VALUES (?,?,?,1,0,0,0,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, gremio.id)
            declaracion.setString(2, gremio.nombre)
            declaracion.setString(3, gremio.emblema)
            declaracion.setString(4, "462;0|461;0|460;0|459;0|458;0|457;0|456;0|455;0|454;0|453;0|452;0|451;0|")
            declaracion.setString(5, "176;100|158;1000|124;100|")
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun REPLACE_GREMIO(gremio: Gremio) {
        val consultaSQL = "REPLACE INTO `gremios` VALUES(?,?,?,?,?,?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, gremio.id)
            declaracion.setString(2, gremio.nombre)
            declaracion.setString(3, gremio.emblema)
            declaracion.setInt(4, gremio.nivel.toInt())
            declaracion.setLong(5, gremio.experiencia)
            declaracion.setInt(6, gremio.capital)
            declaracion.setInt(7, gremio.nroMaxRecau)
            declaracion.setString(8, gremio.compilarHechizo())
            declaracion.setString(9, gremio.compilarStats())
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_GREMIO(id: Int) {
        val consultaSQL = "DELETE FROM `gremios` WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun REPLACE_MIEMBRO_GREMIO(miembro: MiembroGremio) {
        val consultaSQL = "REPLACE INTO `miembros_gremio` VALUES(?,?,?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, miembro.id)
            declaracion.setInt(2, miembro.gremio.id)
            declaracion.setInt(3, miembro.rango)
            declaracion.setLong(4, miembro.xpDonada)
            declaracion.setInt(5, miembro.porcXpDonada)
            declaracion.setInt(6, miembro.derechos)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_MIEMBRO_GREMIO(id: Int) {
        val consultaSQL = "DELETE FROM `miembros_gremio` WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_OTRO_INTERACTIVO(gfxID: Int, mapaID: Short, celdaID: Short, accion: Int) {
        val consultaSQL =
            "DELETE FROM `otros_interactivos` WHERE `gfx` = ? AND `mapaID` = ? AND `celdaID` = ? AND `accion` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, gfxID)
            declaracion.setInt(2, mapaID.toInt())
            declaracion.setInt(3, celdaID.toInt())
            declaracion.setInt(4, accion)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun INSERT_OTRO_INTERACTIVO(
        gfxID: Int, mapaID: Short, celdaID: Short,
        accionID: Int, args: String, condiciones: String, tiempoRecarga: Int, descripcion: String
    ) {
        val consultaSQL = "REPLACE INTO `otros_interactivos` VALUES (?,?,?,?,?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, gfxID)
            declaracion.setInt(2, mapaID.toInt())
            declaracion.setInt(3, celdaID.toInt())
            declaracion.setInt(4, accionID)
            declaracion.setString(5, args)
            declaracion.setString(6, condiciones)
            declaracion.setInt(7, tiempoRecarga)
            declaracion.setString(8, descripcion)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            return
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return
    }

    fun REPLACE_ACCIONES_RESPUESTA(
        respuestaID: Int, accion: Int, args: String,
        condicion: String
    ): Boolean {
        var consultaSQL = "REPLACE INTO `npc_respuestas` VALUES (?,?,?,?);"
        try {
            var declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, respuestaID)
            declaracion.setInt(2, accion)
            declaracion.setString(3, args)
            declaracion.setString(4, condicion)
            ejecutarTransaccion(declaracion)
            consultaSQL = "UPDATE `npc_respuestas` SET `condicion` = ? WHERE `id` = ?;"
            declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, condicion)
            declaracion.setInt(2, respuestaID)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return false
    }

    fun DELETE_ACCIONES_RESPUESTA(respuestaID: Int) {
        val consultaSQL = "DELETE FROM `npc_respuestas` WHERE `id` = ? ;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, respuestaID)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_NPC_PREGUNTA(id: Int, pregunta: Int): Boolean {
        val consultaSQL = "UPDATE `npcs_modelo` SET `pregunta` = ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, pregunta)
            declaracion.setInt(2, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return false
    }

    fun REPLACE_PREGUNTA_NPC(pregunta: PreguntaNPC): Boolean {
        val consultaSQL = "REPLACE INTO `npc_preguntas` VALUES (?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, pregunta.id)
            declaracion.setString(2, pregunta.strRespuestas)
            declaracion.setString(3, pregunta.params)
            declaracion.setString(4, pregunta.strAlternos)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return false
    }

    fun REPLACE_CASA(casa: Casa) {
        val consultaSQL = "REPLACE INTO `casas` VALUES (?,?,?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, casa.id)
            declaracion.setInt(2, if (casa.dueño != null) casa.dueño!!.Id else 0)
            declaracion.setLong(3, casa.kamasVenta)
            declaracion.setByte(4, (if (casa.actParametros) 1 else 0).toByte())
            declaracion.setString(5, casa.clave)
            declaracion.setInt(6, casa.derechosGremio)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_CELDA_MAPA_DENTRO_CASA(casa: Casa) {
        val consultaSQL = "UPDATE `casas_modelo` SET `mapaDentro` = ?, `celdaDentro` = ? WHERE id = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setShort(1, casa.mapaIDDentro)
            declaracion.setShort(2, casa.celdaIDDentro)
            declaracion.setInt(3, casa.id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun REPLACE_OBJETOS_MERCADILLOS(lista: ArrayList<ObjetoMercadillo>) {
        try {
            for (objMerca in lista) {
                REPLACE_OBJETO_MERCADILLO(objMerca)
            }
        } catch (ignored: Exception) {
        }

    }

    fun REPLACE_OBJETO_MERCADILLO(objMerca: ObjetoMercadillo): Boolean {
        val consultaSQL =
            "REPLACE INTO `mercadillo_objetos` (`objeto`,`mercadillo`,`cantidad`,`dueño`,`precio`) VALUES (?,?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            if (objMerca.cuentaID == 0) {
                return false
            }
            declaracion.setInt(1, objMerca.objeto.id)
            declaracion.setInt(2, objMerca.mercadilloID)
            declaracion.setInt(3, objMerca.getTipoCantidad(false))
            declaracion.setInt(4, objMerca.cuentaID)
            declaracion.setLong(5, objMerca.precio)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            SALVAR_OBJETO(objMerca.objeto)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
            return false
        }

        return true
    }

    fun DELETE_OBJ_MERCADILLO(idObjeto: Int) {
        val consultaSQL = "DELETE FROM `mercadillo_objetos` WHERE `objeto` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, idObjeto)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_PRECIO_MEDIO_OBJETO_MODELO(objMod: ObjetoModelo) {
        val consultaSQL = "UPDATE `objetos_modelo` SET vendidos = ?, precioMedio = ? WHERE id = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, objMod.vendidos)
            declaracion.setLong(2, objMod.precioPromedio)
            declaracion.setInt(3, objMod.id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_MOB_IA_TALLA(mob: MobModelo) {
        val consultaSQL = "UPDATE `mobs_modelo` SET `tipoIA` = ?, `talla` = ? WHERE id = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, mob.tipoIA.toInt())
            declaracion.setInt(2, mob.talla.toInt())
            declaracion.setInt(3, mob.id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_STATS_MOB(id: Int, stats: String) {
        val consultaSQL = "UPDATE `mobs_modelo` SET `stats` = ? WHERE id = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, stats)
            declaracion.setInt(2, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_STATS_PUNTOS_PDV_XP_MOB(
        id: Int, stats: String, pdv: String, exp: String, minKamas: String,
        maxKamas: String
    ) {
        val consultaSQL =
            "UPDATE `mobs_modelo` SET `stats` = ?, `pdvs` = ?,`exps` = ? ,`minKamas` = ?,`maxKamas` = ? WHERE id = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, stats)
            declaracion.setString(2, pdv)
            declaracion.setString(3, exp)
            declaracion.setString(4, minKamas)
            declaracion.setString(5, maxKamas)
            declaracion.setInt(6, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_MOB_COLORES(mob: MobModelo) {
        val consultaSQL = "UPDATE `mobs_modelo` SET `colores` = ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, mob.colores)
            declaracion.setInt(2, mob.id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_MOB_AGRESION(mob: MobModelo) {
        val consultaSQL = "UPDATE `mobs_modelo` SET `agresion` = ? WHERE id = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, mob.distAgresion.toInt())
            declaracion.setInt(2, mob.id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_HECHIZO_AFECTADOS(id: Int, afectados: String) {
        val consultaSQL = "UPDATE `hechizos` SET `afectados` = ? WHERE id = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, afectados)
            declaracion.setInt(2, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_HECHIZOS_VALOR_IA(id: Int, valorIA: Int) {
        val consultaSQL = "UPDATE `hechizos` SET `valorIA` = ? WHERE id = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, valorIA)
            declaracion.setInt(2, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun ACTUALIZAR_CONDICIONES_HECHIZO(id: Int, condiciones: String) {
        val consultaSQL = "UPDATE `hechizos` SET `condiciones` = ? WHERE id = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, condiciones)
            declaracion.setInt(2, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_STAT_HECHIZO(id: Int, stat: String, grado: Int) {
        val consultaSQL = "UPDATE `hechizos` SET `nivel$grado` = ? WHERE id = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, stat)
            declaracion.setInt(2, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun ACTUALIZAR_SPRITE_INFO_HECHIZO(id: Int, str: String) {
        val consultaSQL = "UPDATE `hechizos` SET `spriteInfos` = ? WHERE id = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, str)
            declaracion.setInt(2, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun ACTUALIZAR_SPRITE_ID_HECHIZO(id: Int, sprite: Int) {
        val consultaSQL = "UPDATE `hechizos` SET `sprite` = ? WHERE id = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, sprite)
            declaracion.setInt(2, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_MASCOTA(id: Int) {
        val consultaSQL = "DELETE FROM `mascotas` WHERE `objeto` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun GET_NUEVA_FECHA_KEY(mapa: Short): String {
        var str = ""
        val consultaSQL = "SELECT * FROM `mapas` WHERE `id` = '$mapa';"
        try {
            val resultado = consultaSQL(
                consultaSQL,
                _bdEstatica!!
            )
            while (resultado.next()) {
                try {
                    str =
                        resultado.getString("fecha") + "|" + resultado.getString("key") + "|" + resultado.getString(
                            "mapData"
                        )
                } catch (ignored: Exception) {
                }

            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return str
    }

    fun UPDATE_FECHA_KEY_MAPDATA(
        mapa: Short, fecha: String, key: String,
        mapData: String
    ) {
        val consultaSQL = "UPDATE `mapas` SET `fecha` = ?, `key`= ?, `mapData`= ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, fecha)
            declaracion.setString(2, key)
            declaracion.setString(3, mapData)
            declaracion.setShort(4, mapa)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_KEY_MAPA(mapa: Short, key: String) {
        val consultaSQL = "UPDATE `mapas` SET `key` = ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, key)
            declaracion.setShort(2, mapa)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_FECHA_MAPA(mapa: Short, fecha: String) {
        val consultaSQL = "UPDATE `mapas` SET `fecha` = ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, fecha)
            declaracion.setShort(2, mapa)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun UPDATE_SET_MOBS_MAPA(mapa: Int, mob: String) {
        val consultaSQL = "UPDATE `mapas` SET `mobs` = ? WHERE `id` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setString(1, mob)
            declaracion.setInt(2, mapa)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_MOBS_FIX_MAPA(mapa: Int) {
        val consultaSQL = "DELETE FROM `mobs_fix` WHERE `mapa` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, mapa)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_ACCION_PELEA(mapa: Int) {
        val consultaSQL = "DELETE FROM `accion_pelea` WHERE `mapa` = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdEstatica!!
            )
            declaracion.setInt(1, mapa)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun CARGAR_LIVE_ACTION() {
        try {
            val resultado = consultaSQL(
                "SELECT * FROM `live_action`;",
                _bdDinamica!!
            )
            while (resultado.next()) {
                val objMod = Mundo.getObjetoModelo(resultado.getInt("idModelo")) ?: continue
                val objNew = objMod.crearObjeto(
                    resultado.getInt("cantidad"), Constantes.OBJETO_POS_NO_EQUIPADO,
                    CAPACIDAD_STATS.RANDOM
                )
                objNew.convertirStringAStats(resultado.getString("stats"))
                val perso = Mundo.getPersonaje(resultado.getInt("idPersonaje"))
                if (perso != null) {
                    perso.addObjetoConOAKO(objNew, true)
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(
                        perso, "Vous avez reçu " + resultado.getInt("cantidad") + " "
                                + resultado.getString("nombreObjeto")
                    )
                }
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

    }

    fun VACIAR_LIVE_ACTION() {
        val consultaSQL = "TRUNCATE `live_action`;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_PRISMA(id: Int) {
        val consultaSQL = "DELETE FROM `prismas` WHERE id = ?;"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, id)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun REPLACE_PRISMA(prisma: Prisma) {
        val consultaSQL = "REPLACE INTO `prismas` VALUES(?,?,?,?,?,?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setInt(1, prisma.id)
            declaracion.setInt(2, prisma.Alineacion.toInt())
            declaracion.setInt(3, prisma.Nivel)
            prisma.mapa?.id?.toInt()?.let { declaracion.setInt(4, it) }
            prisma.celda?.id?.toInt()?.let { declaracion.setInt(5, it) }
            declaracion.setInt(6, prisma.Honor)
            prisma.subArea?.id?.let { declaracion.setInt(7, it) }
            declaracion.setInt(8, if (prisma.area == null) -1 else prisma.area.id)
            declaracion.setLong(9, prisma.tiempoProteccion)
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun INSERT_COMANDO_GM(rango: String, comando: String) {
        if (rango.equals("Maguz", ignoreCase = true) || rango.equals("SlimeS", ignoreCase = true)) {
            return
        }
        val consultaSQL = "INSERT INTO `comandos` (`nombre gm`,`comando`,`date`) VALUES (?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setString(1, rango)
            declaracion.setString(2, comando)
            declaracion.setString(3, Date().toLocaleString())
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun INSERT_INTERCAMBIO(inte: String) {
        val consultaSQL = "INSERT INTO `intercambios` (`intercambio`,`fecha`) VALUES (?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setString(1, inte)
            declaracion.setString(2, Date().toLocaleString())
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun INSERT_REPORTE_BUG(nombre: String, tema: String, detalle: String) {
        val consultaSQL = "INSERT INTO `reporte_bugs` (`perso`,`asunto`,`detalle`,`fecha`) VALUES (?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setString(1, nombre)
            declaracion.setString(2, tema)
            declaracion.setString(3, detalle)
            declaracion.setString(4, Date().toLocaleString())
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun INSERT_PROBLEMA_OGRINAS(nombre: String, tema: String, detalle: String) {
        val consultaSQL = "INSERT INTO `problema_ogrinas` (`perso`,`asunto`,`detalle`,`fecha`) VALUES (?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setString(1, nombre)
            declaracion.setString(2, tema)
            declaracion.setString(3, detalle)
            declaracion.setString(4, Date().toLocaleString())
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun INSERT_DENUNCIAS(nombre: String, tema: String, detalle: String) {
        val consultaSQL = "INSERT INTO `denuncias` (`perso`,`asunto`,`detalle`,`fecha`) VALUES (?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setString(1, nombre)
            declaracion.setString(2, tema)
            declaracion.setString(3, detalle)
            declaracion.setString(4, Date().toLocaleString())
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun INSERT_SUGERENCIAS(nombre: String, tema: String, detalle: String) {
        val consultaSQL = "INSERT INTO `sugerencias` (`perso`,`asunto`,`detalle`,`fecha`) VALUES (?,?,?,?);"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            declaracion.setString(1, nombre)
            declaracion.setString(2, tema)
            declaracion.setString(3, detalle)
            declaracion.setString(4, Date().toLocaleString())
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

    }

    fun DELETE_REPORTE(tipo: Byte, id: Int): Boolean {
        val tipos = arrayOf("reporte_bugs", "sugerencias", "denuncias", "problema_ogrinas")
        val consultaSQL = "DELETE FROM `" + tipos[tipo.toInt()] + "` WHERE `id` = '" + id + "';"
        try {
            val declaracion = transaccionSQL(
                consultaSQL,
                _bdDinamica!!
            )
            ejecutarTransaccion(declaracion)
            cerrarDeclaracion(declaracion)
            return true
        } catch (e: Exception) {
            exceptionModify(e, consultaSQL, "")
        }

        return false
    }

    fun GET_OGRINAS_TOTALES(): Int {
        var retorno = 0
        try {
            var resultado =
                _bdCuentas?.let {
                    consultaSQL(
                        "SELECT SUM(ogrinas) AS total FROM cuentas WHERE rango = 0",
                        it
                    )
                }
                    ?: return 0
            while (resultado.next()) {
                retorno = resultado.getInt("total")
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }
        return retorno
    }

    fun GET_DESCRIPTION_REPORTE(tipo: Byte, id: Int): String {
        var str = ""
        try {
            val tipos = arrayOf("reporte_bugs", "sugerencias", "denuncias", "problema_ogrinas")
            val resultado = consultaSQL(
                "SELECT * FROM `" + tipos[tipo.toInt()] + "` WHERE `id` = '" + id + "';",
                _bdDinamica!!
            )
            while (resultado.next()) {
                str =
                    ("<b>" + resultado.getString("perso") + "</b> - <i><u>" + resultado.getString("asunto") + "</i></u>: "
                            + resultado.getString("detalle"))
            }
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return str
    }

    fun GET_LISTA_REPORTES(cuenta: Cuenta): String {
        val str = StringBuilder()
        try {
            var resultado =
                consultaSQL(
                    "SELECT * FROM `reporte_bugs` LIMIT " + AtlantaMain.LIMITE_REPORTES + ";",
                    _bdDinamica!!
                )
            var str2 = StringBuilder()
            while (resultado.next()) {
                if (str2.isNotEmpty()) {
                    str2.append("#")
                }
                str2.append(resultado.getInt("id")).append(";").append(resultado.getString("perso"))
                    .append(";")
                    .append(resultado.getString("asunto")).append(";")
                    .append(resultado.getString("fecha")).append(";")
                    .append(
                        if (cuenta.tieneReporte(
                                Constantes.REPORTE_BUGS, resultado.getInt(
                                    "id"
                                )
                            )
                        )
                            1
                        else
                            0
                    )
            }
            str.append(str2.toString()).append("|")
            cerrarResultado(resultado)
            resultado =
                consultaSQL(
                    "SELECT * FROM `sugerencias` LIMIT " + AtlantaMain.LIMITE_REPORTES + ";",
                    _bdDinamica!!
                )
            str2 = StringBuilder()
            while (resultado.next()) {
                if (str2.isNotEmpty()) {
                    str2.append("#")
                }
                str2.append(resultado.getInt("id")).append(";").append(resultado.getString("perso"))
                    .append(";")
                    .append(resultado.getString("asunto")).append(";")
                    .append(resultado.getString("fecha")).append(";")
                    .append(
                        if (cuenta.tieneReporte(
                                Constantes.REPORTE_SUGERENCIAS, resultado.getInt("id")
                            )
                        )
                            1
                        else
                            0
                    )
            }
            str.append(str2.toString()).append("|")
            cerrarResultado(resultado)
            resultado =
                consultaSQL(
                    "SELECT * FROM `denuncias` LIMIT " + AtlantaMain.LIMITE_REPORTES + ";",
                    _bdDinamica!!
                )
            str2 = StringBuilder()
            while (resultado.next()) {
                if (str2.isNotEmpty()) {
                    str2.append("#")
                }
                str2.append(resultado.getInt("id")).append(";").append(resultado.getString("perso"))
                    .append(";")
                    .append(resultado.getString("asunto")).append(";")
                    .append(resultado.getString("fecha")).append(";")
                    .append(
                        if (cuenta.tieneReporte(
                                Constantes.REPORTE_DENUNCIAS, resultado.getInt("id")
                            )
                        )
                            1
                        else
                            0
                    )
            }
            str.append(str2.toString()).append("|")
            cerrarResultado(resultado)
            resultado = consultaSQL(
                "SELECT * FROM `problema_ogrinas` LIMIT " + AtlantaMain.LIMITE_REPORTES + ";",
                _bdDinamica!!
            )
            str2 = StringBuilder()
            while (resultado.next()) {
                if (str2.isNotEmpty()) {
                    str2.append("#")
                }
                str2.append(resultado.getInt("id")).append(";").append(resultado.getString("perso"))
                    .append(";")
                    .append(resultado.getString("asunto")).append(";")
                    .append(resultado.getString("fecha")).append(";")
                    .append(
                        if (cuenta.tieneReporte(
                                Constantes.REPORTE_OGRINAS, resultado.getInt(
                                    "id"
                                )
                            )
                        )
                            1
                        else
                            0
                    )
            }
            str.append(str2.toString())
            cerrarResultado(resultado)
        } catch (e: Exception) {
            exceptionNormal(e, "")
        }

        return str.toString()
    }
}
