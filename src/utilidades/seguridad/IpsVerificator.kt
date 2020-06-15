package utilidades.seguridad

import estaticos.Formulas
import estaticos.GestorSalida
import estaticos.database.GestorSQL
import variables.personaje.Cuenta

object IpsVerificator {
    fun ipdiferente(cuenta: Cuenta?): Boolean {
        if (cuenta == null) {
            return false
        }
        var diferente = false
        val ipActual = cuenta.actualIP
        val ips = GestorSQL.GET_IPS_AUTORIZADAS(cuenta)
        val tokensql = GestorSQL.GET_TOKEN(cuenta)
        if (ips.isEmpty()) {
            GestorSQL.REPLACE_IP_AUTORIZADA(cuenta, true)
        } else if (!ips.contains(ipActual) && tokensql.isNotEmpty()) {
            GestorSQL.REPLACE_IP_AUTORIZADA(cuenta, false)
            cuenta.bloqueado = true
            diferente = true
            GestorSQL.MENSAJE_PENDIENTE(
                cuenta, "SISTEMA", "Su cuenta se encuentra bloqueda, Porfavor use su .token" +
                        "\n Ejemplo:\n" +
                        ".token Huduf778s8asS"
            )
        } else if (!ips.contains(ipActual)) { // Si no tiene seguridad, se agrega al historial de ips
            GestorSQL.REPLACE_IP_AUTORIZADA(cuenta, true)
            cuenta.bloqueado = false
        }
        return diferente
    }

    fun desbloquearCuenta(cuenta: Cuenta?, token: String?) {
        if (cuenta == null) {
            return
        }
        if (token == null) {
            return
        }
        val tokensql = GestorSQL.GET_TOKEN(cuenta)
        val perso = cuenta.socket?.personaje
        if (tokensql == token) {
            cuenta.bloqueado = false
            GestorSQL.REPLACE_IP_AUTORIZADA(cuenta, true)
            perso?.enviarmensajeNegro("Su cuenta se ha desbloqueado exitosamente")
            cuenta.intentosFallidosToken = 0
            cuenta.socket?.comando_jugador(".deblo")
        } else if (cuenta.bloqueado) {
            if (cuenta.intentosFallidosToken < Formulas.getRandomInt(4, 6)) { // Para que sea Apatronico
                cuenta.intentosFallidosToken++
                perso?.enviarmensajeNegro(
                    "Intentos fallidos: ${cuenta.intentosFallidosToken}, " +
                            "Despues de determinadas veces de fallo, su ip será baneada y sera expulsado del servidor."
                )
            } else {
                val ip = cuenta.actualIP
                val pj = cuenta.socket?.personaje?.nombre ?: "Indeterminado"
                GestorSQL.INSERT_BAN_IP(ip)
                cuenta.socket?.cerrarSocket(true, "Intento de robo, Token fallido")
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(
                    "La ip: $ip Ha intentado robar al personaje: $pj " +
                            "La ip ha sido baneada de manera automatica por el sistema de Tokens De SlimeS"
                )
            }
        } else {
            cuenta.socket?.personaje?.enviarmensajeNegro("Tu ip ya se encuentra desbloqueada")
        }
    }
}