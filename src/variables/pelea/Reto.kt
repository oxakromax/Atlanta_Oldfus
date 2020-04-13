package variables.pelea

import estaticos.Constantes
import estaticos.GestorSalida.ENVIAR_GdaK_RETO_REALIZADO
import estaticos.GestorSalida.ENVIAR_GdaO_RETO_FALLADO

class Reto(
    val id: Byte, private val _bonusXPFijo: Int, private val _bonusXPGrupo: Int, private val _bonusPPFijo: Int,
    private val _bonusPPGrupo: Int, private val _pelea: Pelea
) {
    private var _esLupa = false
    var luchMob: Luchador? = null
        private set
    private var _estado: EstReto

    var estado: EstReto
        get() = _estado
        set(e) {
            if (_estado == EstReto.EN_ESPERA) {
                if (e == EstReto.REALIZADO) {
                    ENVIAR_GdaK_RETO_REALIZADO(_pelea, id.toInt())
                } else if (e == EstReto.FALLADO) {
                    ENVIAR_GdaO_RETO_FALLADO(_pelea, id.toInt())
                } else return
                _estado = e
            }
        }

    private fun numeroEstado(): Int {
        return if (_estado == EstReto.EN_ESPERA) {
            0
        } else if (_estado == EstReto.REALIZADO) {
            1
        } else {
            2
        }
    }

    fun infoReto(): String {
        return (id.toString() + ";" + (if (_esLupa) 1 else 0) + ";" + (if (luchMob != null) luchMob!!.id else 0) + ";" + _bonusXPFijo + ";"
                + _bonusXPGrupo + ";" + _bonusPPFijo + ";" + _bonusPPGrupo + ";" + numeroEstado())
    }

    fun setMob(mob: Luchador?) {
        luchMob = mob
    }

    fun bonusXP(): Int {
        return _bonusXPFijo + _bonusXPGrupo
    }

    fun bonusDrop(): Int {
        return _bonusPPFijo + _bonusPPGrupo
    }

    enum class EstReto {
        EN_ESPERA, REALIZADO, FALLADO
    }

    init {
        _estado = EstReto.EN_ESPERA // 0
        when (id) {
            Constantes.RETO_ZOMBI, Constantes.RETO_ESTATUA, Constantes.RETO_AHORRADOR, Constantes.RETO_VERSATIL, Constantes.RETO_JARDINERO, Constantes.RETO_NOMADA, Constantes.RETO_BARBARO, Constantes.RETO_CRUEL, Constantes.RETO_MISTICO, Constantes.RETO_SEPULTURERO, Constantes.RETO_CASINO_REAL, Constantes.RETO_ARACNOFILO, Constantes.RETO_ENTOMOLOGO, Constantes.RETO_INTOCABLE, Constantes.RETO_INCURABLE, Constantes.RETO_MANOS_LIMPIAS, Constantes.RETO_ELEMENTAL, Constantes.RETO_CIRCULEN, Constantes.RETO_EL_TIEMPO_PASA, Constantes.RETO_PERDIDO_DE_VISTA, Constantes.RETO_LIMITADO, Constantes.RETO_ORDENADO, Constantes.RETO_NI_PIAS_NI_SUMISAS, Constantes.RETO_NI_PIOS_NI_SUMISOS, Constantes.RETO_LOS_PEQUEÑOS_ANTES, Constantes.RETO_SUPERVIVIENTE, Constantes.RETO_AUDAZ, Constantes.RETO_PEGAJOSO, Constantes.RETO_BLITZKRIEG, Constantes.RETO_ANACORETA, Constantes.RETO_PUSILANIME, Constantes.RETO_IMPETUOSO, Constantes.RETO_EL_DOS_POR_UNO, Constantes.RETO_ABNEGACION, Constantes.RETO_REPARTO, Constantes.RETO_DUELO, Constantes.RETO_CADA_UNO_CON_SU_MONSTRUO, Constantes.RETO_CONTAMINACION, Constantes.RETO_LOS_PERSONAJES_SECUNDARIOS_PRIMERO, Constantes.RETO_PROTEJAN_A_SUS_PERSONAJES_SECUNDARIOS, Constantes.RETO_LA_TRAMPA_DE_LOS_DESARROLLADORES -> {
            }
            Constantes.RETO_ELEGIDO_VOLUNTARIO, Constantes.RETO_APLAZAMIENTO, Constantes.RETO_ELITISTA, Constantes.RETO_ASESINO_A_SUELDO, Constantes.RETO_FOCALIZACION, Constantes.RETO_IMPREVISIBLE -> _esLupa =
                true
        }
    }
}