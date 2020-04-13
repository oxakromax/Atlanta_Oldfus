package variables.gremio

import estaticos.Constantes
import estaticos.Mundo.getPersonaje
import org.joda.time.Hours
import org.joda.time.LocalDateTime
import variables.personaje.Personaje
import java.util.*

class MiembroGremio(
    val id: Int, gremio: Gremio, rango: Int, xpDonada: Long, porcXp: Byte,
    derechos: Int
) {
    val gremio: Gremio
    val personaje: Personaje?
    private val _tieneDerecho: MutableMap<Int, Boolean> = TreeMap()
    var porcXpDonada: Int
        private set
    private var _rango: Int
    var derechos = 0
        private set
    var xpDonada: Long
        private set

    var rango: Int
        get() = if (derechos == 1) 1 else _rango
        set(rango) {
            _rango = rango
        }

    fun analizarDerechos(): String {
        return Integer.toString(derechos, 36)
    }

    val gfx: Int
        get() = personaje!!.getGfxID(false)

    val nivel: Int
        get() = personaje!!.nivel

    val nombre: String
        get() = personaje!!.nombre

    val ultimaConexion: String
        get() = personaje!!.cuenta.ultimaConexion

    val horasDeUltimaConeccion: Int
        get() = try {
            val strFecha = ultimaConexion.split("~".toRegex()).toTypedArray()
            val ultConeccion = LocalDateTime(
                strFecha[0].toInt(),
                strFecha[1].toInt(),
                strFecha[2].toInt(),
                strFecha[3].toInt(),
                strFecha[4].toInt(),
                strFecha[5].toInt()
            )
            val ahora = LocalDateTime()
            Hours.hoursBetween(ultConeccion, ahora).hours
        } catch (e: Exception) {
            0
        }

    fun puede(derecho: Int): Boolean {
        return if (_tieneDerecho[Constantes.G_TODOS_LOS_DERECHOS]!! || _rango == 1) {
            false
        } else !_tieneDerecho[derecho]!!
    }

    fun darXpAGremio(xp: Long) {
        xpDonada += xp
        gremio.addExperiencia(xp, false)
    }

    fun setTodosDerechos(rango: Int, porcXpdonar: Int, derechos: Int) {
        var porcXpdonar = porcXpdonar
        if (rango != -1) {
            _rango = rango
        }
        if (porcXpdonar != -1) {
            if (porcXpdonar < 0) {
                porcXpdonar = 0
            }
            if (porcXpdonar > 90) {
                porcXpdonar = 90
            }
            porcXpDonada = porcXpdonar
        }
        if (derechos != -1) {
            convertirDerechosAInt(derechos)
        }
    }

    private fun convertirDerechosAInt(derechos: Int) { // derechosIniciales();
        var newDerechos = 0
        for (i in 0..14) {
            val elevado = Math.pow(2.0, i.toDouble()).toInt()
            var permiso = derechos and elevado == elevado
            if (_rango == 1) {
                permiso = true
                newDerechos = 1
            } else {
                if (derechos == 1) {
                    permiso = elevado != 1
                }
                if (permiso) {
                    newDerechos += elevado
                }
            }
            _tieneDerecho[elevado] = permiso
        }
        this.derechos = newDerechos
    } // private void derechosIniciales() {

    // _tieneDerecho.put(Informacion.G_TODOS_LOS_DERECHOS, false);
// _tieneDerecho.put(Informacion.G_MODIF_BOOST, false);
// _tieneDerecho.put(Informacion.G_MODIF_DERECHOS, false);
// _tieneDerecho.put(Informacion.G_INVITAR, false);
// _tieneDerecho.put(Informacion.G_BANEAR, false);
// _tieneDerecho.put(Informacion.G_TODAS_XP_DONADAS, false);
// _tieneDerecho.put(Informacion.G_MODIF_RANGOS, false);
// _tieneDerecho.put(Informacion.G_PONER_RECAUDADOR, false);
// _tieneDerecho.put(Informacion.G_SU_XP_DONADA, false);
// _tieneDerecho.put(Informacion.G_RECOLECTAR_RECAUDADOR, false);
// _tieneDerecho.put(Informacion.G_USAR_CERCADOS, false);
// _tieneDerecho.put(Informacion.G_MEJORAR_CERCADOS, false);
// _tieneDerecho.put(Informacion.G_OTRAS_MONTURAS, false);
// }
    init {
        personaje = getPersonaje(id)
        this.gremio = gremio
        _rango = rango
        this.xpDonada = xpDonada
        porcXpDonada = porcXp.toInt()
        convertirDerechosAInt(derechos)
        personaje!!.miembroGremio = this
    }
}