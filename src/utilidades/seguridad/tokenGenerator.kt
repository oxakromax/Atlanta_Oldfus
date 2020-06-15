package utilidades.seguridad

import estaticos.Formulas
import estaticos.database.GestorSQL
import variables.personaje.Cuenta

object tokenGenerator {
    val letters = "a9tyuiop123s23NMASDhjkbnmqwer78456JKLQWER7867890ZXCVFGH45lzxcv4569123dfgB1TYUIOP"
    fun generarToken(longitud: Int): String {
        val token = StringBuilder()
        for (x in 0..longitud) {
            val pos = Formulas.getRandomInt(0, letters.length - 1)
            token.append(letters[pos])
        }
        return token.toString()
    }

    fun tieneToken(cuenta: Cuenta?): Boolean {
        if (cuenta == null) {
            return false
        }
        val tokensql = GestorSQL.GET_TOKEN(cuenta)
        return tokensql.length > 4
    }

    fun asignarToken(cuenta: Cuenta?): Boolean {
        if (cuenta == null) {
            return false
        }
        val tokenSql = GestorSQL.GET_TOKEN(cuenta)
        return when {
            cuenta.bloqueado -> {
                false
            }
            tokenSql.length > 4 -> {
                cuenta.socket?.personaje?.enviarmensajeNegro("Su token es: $tokenSql")
                true
            }
            else -> {
                val token = generarToken(15)
                GestorSQL.INSERT_TOKEN(cuenta, token)
                GestorSQL.UPDATE_IPS_AUTORIZADAS_TOKEN(cuenta)
                cuenta.socket?.personaje?.enviarmensajeNegro("Su nuevo token es: $token")
                true
            }
        }
    }
}