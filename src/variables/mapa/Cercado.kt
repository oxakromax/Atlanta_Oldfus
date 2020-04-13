package variables.mapa

import estaticos.Formulas.getRandomInt
import estaticos.Mundo
import sprites.Exchanger
import variables.gremio.Gremio
import variables.montura.Montura
import variables.montura.Montura.Ubicacion
import variables.objeto.Objeto
import variables.personaje.Personaje
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

class Cercado(
    val mapa: Mapa?, var capacidadMax: Int, var cantObjMax: Byte, celdaID: Short,
    celdaPuerta: Short, celdaMontura: Short, celdasObjetos: String, precioOriginal: Int
) : Exchanger {
    val celdaPuerta: Short
    // private final Map<Short, Map<Integer, Objeto>> _objCrianzaConDueño = new HashMap<Short,
// Map<Integer, Objeto>>();
    private val _objCrianza: MutableMap<Short, Objeto?> = HashMap()
    val criando = ConcurrentHashMap<Int, Montura>()
    val celdasObj = ArrayList<Short>()
    var celdaID: Short = -1
    var celdaMontura: Short
        private set
    var dueñoID = 0
    var precioPJ = 0
    private var _precioOriginal: Int
    var gremio: Gremio? = null
    fun actualizarCercado(dueñoID: Int, gremio: Int, precio: Int, objCrianza: String, criandostring: String) {
        this.dueñoID = dueñoID
        this.gremio = null
        if (this.dueñoID > 0) {
            val dueño = Mundo.getPersonaje(this.dueñoID)
            if (dueño == null) {
                this.dueñoID = 0
            } else {
                this.gremio = dueño.gremio
            }
            precioPJ = precio
            for (str in objCrianza.split(Pattern.quote("|").toRegex()).toTypedArray()) {
                try {
                    val infos = str.split(";".toRegex()).toTypedArray()
                    if (infos[1].toInt() == 0) {
                        _objCrianza[infos[0].toShort()] = null
                        continue
                    }
                    val objeto = Mundo.getObjeto(infos[1].toInt())
                    if (objeto == null || objeto.durabilidad <= 0) {
                        continue
                    }
                    _objCrianza[infos[0].toShort()] = objeto
                } catch (ignored: Exception) {
                }
            }
        }
        for (montura in criandostring.split(";".toRegex()).toTypedArray()) {
            try {
                val DP = Mundo.getMontura(montura.toInt())
                if (DP != null) {
                    if (DP.celda == null) {
                        continue
                    }
                }
                if (DP != null) {
                    criando[DP.id] = DP
                }
            } catch (ignored: Exception) {
            }
        }
    }

    fun resetear() {
        dueñoID = 0
        precioPJ = 3000000
        gremio = null
        _objCrianza.clear()
        criando.clear()
    }

    @Synchronized
    fun startMoverMontura() {
        for (montura in criando.values) {
            val dir = getRandomInt(0, 3) * 2 + 1
            montura.moverMontura(null, dir, 4, false)
            //			try {
//				Thread.sleep(300);
//			} catch (final Exception e) {}
        }
    }

    fun strObjCriaParaBD(): String {
        if (_objCrianza.isEmpty()) {
            return ""
        }
        val str = StringBuilder()
        for ((key, value) in _objCrianza) {
            if (str.isNotEmpty()) {
                str.append("|")
            }
            str.append(key).append(";").append(if (esPublico()) 0 else value!!.id)
        }
        return str.toString()
    }

    val objetosParaBD: ArrayList<Objeto>
        get() {
            val objetos = ArrayList<Objeto>()
            for (obj in _objCrianza.values) {
                if (obj == null) {
                    continue
                }
                objetos.add(obj)
            }
            return objetos
        }

    val objetosCrianza: Map<Short, Objeto?>
        get() = _objCrianza

    fun setTamañoyObjetos(tamaño: Int, objetos: Byte) {
        capacidadMax = tamaño
        cantObjMax = objetos
    }

    fun addObjetoCria(
        celda: Short,
        objeto: Objeto?,
        dueño: Int
    ) { // final Map<Integer, Objeto> otro = new TreeMap<Integer, Objeto>();
// otro.put(dueño, objeto);
        _objCrianza[celda] = objeto
        // _objCrianzaConDueño.put(celda, otro);
    }

    fun retirarObjCria(celda: Short, perso: Personaje?): Boolean {
//        if (!_objCrianza.containsKey(celda)) {
//            return false
//        }
        val objCrianza = _objCrianza[celda] ?: return false
        // si el jugador lo retira intencionalmente
        perso?.addObjIdentAInventario(objCrianza, false, 0) ?: // si se elimnia por desgaste
        Mundo.eliminarObjeto(objCrianza.id)
        _objCrianza.remove(celda)
//        if (perso != null) {
//            GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(perso,objCrianza)
//        }
        return true
    }


    val cantObjColocados: Int
        get() = _objCrianza.size

    val stringCeldasObj: String
        get() {
            if (celdasObj.isEmpty()) {
                return ""
            }
            val str = StringBuilder()
            for (celda in celdasObj) {
                if (str.isNotEmpty()) {
                    str.append(";")
                }
                str.append(celda.toInt())
            }
            return str.toString()
        }

    fun addCeldaObj(celda: Short) {
        if (celdasObj.contains(celda) || celda <= 0) {
            return
        }
        celdasObj.add(celda)
        celdasObj.trimToSize()
    }

    fun addCeldaMontura(celda: Short) {
        celdaMontura = celda
    }

    fun strPavosCriando(): String {
        if (criando.isEmpty()) {
            return ""
        }
        val str = StringBuilder()
        for ((key) in criando) {
            if (str.isNotEmpty()) {
                str.append(";")
            }
            str.append(key)
        }
        return str.toString()
    }

    fun addCriando(montura: Montura) {
        criando[montura.id] = montura
        montura.ubicacion = Ubicacion.CERCADO
    }

    fun puedeAgregar(): Boolean {
        return criando.size < capacidadMax
    }

    fun borrarMonturaCercado(id: Int): Boolean {
        return criando.remove(id) != null
    }

    fun esPublico(): Boolean {
        return dueñoID == -1
    }

    val precio: Int
        get() = if (dueñoID > 0) precioPJ else _precioOriginal

    fun informacionCercado(): String {
        return "Rp" + dueñoID + ";" + precio + ";" + capacidadMax + ";" + cantObjMax + ";" + if (gremio == null) ";" else gremio!!.nombre + ";" + gremio!!.emblema
    }

    override fun addKamas(kamas: Long, perso: Personaje?) { // TODO Auto-generated method stub
    }

    override val kamas: Long
        get() = 0L


    @Synchronized
    override fun addObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
    }

    @Synchronized
    override fun remObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
    }

    override fun cerrar(perso: Personaje?, exito: String) {
        if (perso != null) {
            perso.cerrarVentanaExchange(exito)
        }
    }

    override fun botonOK(perso: Personaje) {}
    override fun getListaExchanger(perso: Personaje): String { // TODO Auto-generated method stub
        return ""
    }

    // private static String CERCADO_8848 =
// "305;0|171;0|308;0|311;0|413;0|470;0|228;0|527;0|194;0|254;0|117;0|251;0|365;0";
// private static String CERCADO_8744 =
// "550;0|304;0|474;0|337;0|545;0|400;0|394;0|213;0|453;0|270;0|451;0|420;0|361;0";
// private static String CERCADO_8743 =
// "305;0|272;0|413;0|470;0|522;0|319;0|359;0|601;0|416;0|215;0|421;0|362;0|211;0";
// private static String CERCADO_8747 =
// "476;0|415;0|234;0|432;0|438;0|358;0|325;0|291;0|486;0|301;0|637;0|268;0|211;0";
// private static String CERCADO_8746 =
// "513;0|559;0|380;0|527;0|377;0|193;0|288;0|488;0|323;0|355;0|635;0|454;0|603;0";
// private static String CERCADO_8745 =
// "307;0|341;0|512;0|581;0|505;0|231;0|471;0|383;0|587;0|395;0|429;0|417;0|301;0";
// private static String CERCADO_8752 =
// "304;0|476;0|474;0|65;0|544;0|472;0|232;0|381;0|468;0|228;0|253;0|156;0|396;0";
// private static String CERCADO_8750 =
// "100;0|472;0|172;0|197;0|400;0|324;0|252;0|320;0|248;0|396;0|213;0|244;0|121;0";
// private static String CERCADO_8851 =
// "544;0|472;0|400;0|286;0|379;0|358;0|190;0|217;0|430;0|82;0|247;0|214;0|328;0";
// private static String CERCADO_8749 =
// "342;0|580;0|504;0|475;0|432;0|471;0|465;0|392;0|253;0|250;0|528;0|418;0|177;0";
// private static String CERCADO_8748 =
// "343;0|137;0|308;0|402;0|436;0|393;0|564;0|80;0|397;0|560;0|321;0|267;0|451;0";
// private static String CERCADO_8751 =
// "342;0|504;0|472;0|567;0|493;0|495;0|290;0|251;0|396;0|176;0|419;0|385;0|542;0";
    init {
        this.celdaID = celdaID
        this.celdaMontura = celdaMontura
        this.celdaPuerta = celdaPuerta
        _precioOriginal = precioOriginal
        for (celda in celdasObjetos.split(";".toRegex()).toTypedArray()) {
            try {
                celdasObj.add(celda.toShort())
            } catch (ignored: Exception) {
            }
        }
        celdasObj.trimToSize()
        if (mapa != null) {
            mapa.cercado = this
        }
        if (mapa != null) {
            var publico = true
            var objCrianza = ""
            when (mapa.id.toInt()) {
                8848 -> objCrianza = "305;0|171;0|308;0|311;0|413;0|470;0|228;0|527;0|194;0|254;0|117;0|251;0|365;0"
                8744 -> objCrianza = "550;0|304;0|474;0|337;0|545;0|400;0|394;0|213;0|453;0|270;0|451;0|420;0|361;0"
                8743 -> objCrianza = "305;0|272;0|413;0|470;0|522;0|319;0|359;0|601;0|416;0|215;0|421;0|362;0|211;0"
                8747 -> objCrianza = "476;0|415;0|234;0|432;0|438;0|358;0|325;0|291;0|486;0|301;0|637;0|268;0|211;0"
                8746 -> objCrianza = "513;0|559;0|380;0|527;0|377;0|193;0|288;0|488;0|323;0|355;0|635;0|454;0|603;0"
                8745 -> objCrianza = "307;0|341;0|512;0|581;0|505;0|231;0|471;0|383;0|587;0|395;0|429;0|417;0|301;0"
                8752 -> objCrianza = "304;0|476;0|474;0|65;0|544;0|472;0|232;0|381;0|468;0|228;0|253;0|156;0|396;0"
                8750 -> objCrianza = "100;0|472;0|172;0|197;0|400;0|324;0|252;0|320;0|248;0|396;0|213;0|244;0|121;0"
                8851 -> objCrianza = "544;0|472;0|400;0|286;0|379;0|358;0|190;0|217;0|430;0|82;0|247;0|214;0|328;0"
                8749 -> objCrianza = "342;0|580;0|504;0|475;0|432;0|471;0|465;0|392;0|253;0|250;0|528;0|418;0|177;0"
                8748 -> objCrianza = "343;0|137;0|308;0|402;0|436;0|393;0|564;0|80;0|397;0|560;0|321;0|267;0|451;0"
                8751 -> objCrianza = "342;0|504;0|472;0|567;0|493;0|495;0|290;0|251;0|396;0|176;0|419;0|385;0|542;0"
                else -> publico = false
            }
            if (publico) {
                dueñoID = -1
                precioPJ = 0
                _precioOriginal = precioPJ
                for (str in objCrianza.split(Pattern.quote("|").toRegex()).toTypedArray()) {
                    try {
                        val infos = str.split(";".toRegex()).toTypedArray()
                        if (infos[1].toInt() == 0) {
                            _objCrianza[infos[0].toShort()] = null
                            continue
                        }
                        val objeto = Mundo.getObjeto(infos[1].toInt())
                        if (objeto == null || objeto.durabilidad <= 0) {
                            continue
                        }
                        _objCrianza[infos[0].toShort()] = objeto
                    } catch (ignored: Exception) {
                    }
                }
            }
        }
    }
}