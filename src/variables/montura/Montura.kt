package variables.montura

import estaticos.AtlantaMain
import estaticos.Camino
import estaticos.Constantes
import estaticos.Constantes.esPosicionEquipamiento
import estaticos.Constantes.getCaracObjCria
import estaticos.Constantes.getColorCria
import estaticos.Constantes.getObjCriaPorMapa
import estaticos.Encriptador.celdaIDAHash
import estaticos.Encriptador.getNumeroPorValorHash
import estaticos.Encriptador.getValorHashPorNumero
import estaticos.Formulas.getRandomInt
import estaticos.GestorSalida.ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO
import estaticos.GestorSalida.ENVIAR_GA_MOVER_SPRITE_MAPA
import estaticos.GestorSalida.ENVIAR_GDE_FRAME_OBJECT_EXTERNAL
import estaticos.GestorSalida.ENVIAR_GDO_OBJETO_TIRAR_SUELO
import estaticos.GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA
import estaticos.GestorSalida.ENVIAR_GM_DRAGOPAVO_A_MAPA
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO
import estaticos.GestorSalida.ENVIAR_eD_CAMBIAR_ORIENTACION
import estaticos.GestorSalida.ENVIAR_eUK_EMOTE_MAPA
import estaticos.Mundo.addMontura
import estaticos.Mundo.addObjeto
import estaticos.Mundo.eliminarMontura
import estaticos.Mundo.getExpMontura
import estaticos.Mundo.getMapa
import estaticos.Mundo.getMonturaModelo
import estaticos.Mundo.getObjeto
import estaticos.Mundo.getObjetoModelo
import estaticos.Mundo.getPersonaje
import estaticos.Mundo.sigIDMontura
import sprites.Exchanger
import variables.mapa.Celda
import variables.mapa.Mapa
import variables.objeto.Objeto
import variables.objeto.ObjetoModelo
import variables.personaje.Personaje
import variables.stats.Stats
import java.util.*

class Montura : Exchanger {
    private val _sexo: Byte
    val id: Int
    val color: Int
    val stats = Stats()
    private val _objetos: MutableMap<Int, Objeto?> = TreeMap()
    val capacidades = ArrayList<Byte>(2)
    private val _monturaModelo: MonturaModelo?
    private var _salvaje: Boolean
    private var _orientacion: Byte = 1
    private var _talla: Byte = 100
    private var _reproducciones: Byte = 0
    var mapa: Mapa? = null
        private set
    var celda: Celda? = null
        private set
    var dueñoID: Int
    var nivel = 1
        private set
    var parejaID = -1
        private set
    private var _certificadoID = -1
    var fatiga = 0
    var energia = 0
        private set
    var madurez = 0
        private set
    var serenidad = 0
        private set
    var amor = 0
    var resistencia = 0
    private var _semiPod = 0
    private var _maxPod = 0
    private var maxMadurez = 0
    private var _maxEnergia = 0
    var exp: Long = 0
        private set
    private var _tiempoInicioDescanso: Long = 0
    var tiempoFecundacion: Long
        private set
    var ancestros = "?,?,?,?,?,?,?,?,?,?,?,?,?,?"
        private set
    var nombre = "Sin Nombre"
    private var _ubicacion = Ubicacion.PERGAMINO // por defecto

    constructor(color: Int, dueño: Int, castrado: Boolean, salvaje: Boolean) {
        id = sigIDMontura()
        _sexo = SEXO_POSIBLES[getRandomInt(0, SEXO_POSIBLES.size - 1)]
        this.color = color
        _monturaModelo = getMonturaModelo(this.color)
        addExperiencia(getExpMontura(AtlantaMain.INICIO_NIVEL_MONTURA))
        energia = maxEnergia
        madurez = maxMadurez
        dueñoID = dueño
        if (castrado) {
            castrarPavo()
        }
        _salvaje = salvaje
        tiempoFecundacion = 0
        statsMontura
        maximos()
        addMontura(this, true)
    }

    constructor(madre: Montura, padre: Montura?) {
        var padre = padre
        if (padre == null) {
            padre = madre
        }
        id = sigIDMontura()
        _sexo = SEXO_POSIBLES[getRandomInt(0, SEXO_POSIBLES.size - 1)]
        color = getColorCria(
            madre.color, padre.color, madre.capacidades.contains(
                Constantes.HABILIDAD_PREDISPUESTA
            ), padre.capacidades.contains(Constantes.HABILIDAD_PREDISPUESTA)
        )
        _monturaModelo = getMonturaModelo(color)
        addExperiencia(getExpMontura(AtlantaMain.INICIO_NIVEL_MONTURA))
        val papa = padre.ancestros.split(",".toRegex()).toTypedArray()
        val mama = madre.ancestros.split(",".toRegex()).toTypedArray()
        val primero_papa = papa[0] + "," + papa[1]
        val primera_mama = mama[0] + "," + mama[1]
        val segundo_papa =
            papa[2] + "," + papa[3] + "," + papa[4] + "," + papa[5]
        val segunda_mama =
            mama[2] + "," + mama[3] + "," + mama[4] + "," + mama[5]
        ancestros =
            (padre.color.toString() + "," + madre.color + "," + primero_papa + "," + primera_mama + ","
                    + segundo_papa + "," + segunda_mama)
        for (i in 1..2) {
            val habilidad = getRandomInt(1, 20).toByte()
            if (habilidad >= 9) {
                continue
            }
            addHabilidad(habilidad)
        }
        dueñoID = madre.dueñoID
        _talla = 50
        _salvaje = false
        tiempoFecundacion = 0
        statsMontura
        maximos()
        addMontura(this, true)
    }

    constructor(
        id: Int, color: Int, sexo: Byte, amor: Int, resistencia: Int, nivel: Int,
        exp: Long, nombre: String, fatiga: Int, energia: Int, reprod: Byte, madurez: Int,
        serenidad: Int, objetos: String, anc: String, habilidad: String, talla: Byte,
        celda: Short, mapa1: Short, dueño: Int, orientacion: Byte, fecundada: Long, pareja: Int,
        salvaje: Byte
    ) {
        this.id = id
        this.color = color
        _monturaModelo = getMonturaModelo(color)
        _sexo = sexo
        addExperiencia(exp)
        this.amor = amor
        this.resistencia = resistencia
        this.nombre = nombre
        this.fatiga = fatiga
        this.energia = energia
        _reproducciones = reprod
        this.madurez = madurez
        this.serenidad = serenidad
        ancestros = anc
        _talla = talla
        this.mapa = getMapa(mapa1)
        if (this.mapa != null) {
            this.celda = mapa!!.getCelda(celda)
            if (this.celda != null) {
                ubicacion = Ubicacion.CERCADO
            }
        }
        dueñoID = dueño
        _orientacion = orientacion
        tiempoFecundacion = fecundada
        parejaID = pareja
        _salvaje = salvaje.toInt() == 1
        for (s in habilidad.split(",".toRegex()).toTypedArray()) {
            try {
                if (s.isEmpty()) {
                    continue
                }
                capacidades.add(s.toByte())
            } catch (ignored: Exception) {
            }
        }
        objetos.replace(";".toRegex(), ",")
        for (str in objetos.split(",".toRegex()).toTypedArray()) {
            try {
                if (str.isEmpty()) {
                    continue
                }
                val obj = getObjeto(str.toInt())
                if (obj != null) {
                    _objetos[str.toInt()] = obj
                }
            } catch (ignored: Exception) {
            }
        }
        statsMontura
        maximos()
    }

    val statsMontura: Unit
        get() {
            if (_monturaModelo == null) {
                return
            }
            stats.clear()
            for ((key, value) in _monturaModelo.stats) {
                val valor = value * nivel / AtlantaMain.NIVEL_MAX_MONTURA
                if (valor > 0) stats.addStatID(key!!, valor)
            }
            return
        }

    val objModCertificado: ObjetoModelo?
        get() = if (_monturaModelo == null) {
            null
        } else getObjetoModelo(_monturaModelo.certificadoModeloID)

    fun disminuirFatiga() {
        if (_tiempoInicioDescanso == 0L || fatiga == 0) {
            return
        }
        if (System.currentTimeMillis() - _tiempoInicioDescanso >= 60 * 60 * 1000) {
            _tiempoInicioDescanso = System.currentTimeMillis()
            restarFatiga()
        }
    }

    var ubicacion: Ubicacion
        get() = _ubicacion
        set(ubicacion) {
            _ubicacion = ubicacion
            _tiempoInicioDescanso = if (_ubicacion == Ubicacion.ESTABLO) {
                System.currentTimeMillis()
            } else {
                0
            }
        }

    private fun maximos() {
        if (_monturaModelo == null) {
            return
        }
        val _generacion = _monturaModelo.generacionID.toInt()
        _semiPod = (_generacion + 1) / 2 * 5
        _maxPod = 50 + 50 * _generacion
        maxMadurez = 1000 * _generacion
        _maxEnergia = 1000 + (_generacion - 1) * 100
    }

    var pergamino: Int
        get() = _certificadoID
        set(pergamino) {
            _certificadoID = pergamino
            if (_certificadoID > 0) {
                ubicacion = Ubicacion.PERGAMINO
            }
        }

    fun esSalvaje(): Boolean {
        return _salvaje
    }

    fun setSalvaje(s: Boolean) {
        _salvaje = s
    }

    val sexo: Int
        get() = _sexo.toInt()

    val pods: Int
        get() {
            var pods = 0
            for (obj in _objetos.values) {
                pods += obj!!.objModelo?.peso?.times(obj.cantidad) ?: 0
            }
            return pods
        }

    override fun getListaExchanger(perso: Personaje): String {
        val objetos = StringBuilder()
        for (obj in _objetos.values) {
            objetos.append("O").append(obj!!.stringObjetoConGuiño())
        }
        return objetos.toString()
    }

    private fun getSimilarObjeto(objeto: Objeto): Objeto? {
        if (objeto.puedeTenerStatsIguales()) {
            for (obj in _objetos.values) {
                if (esPosicionEquipamiento(obj!!.posicion)) {
                    continue
                }
                if (objeto.id != obj.id && obj.objModeloID == objeto.objModeloID && obj.sonStatsIguales(
                        objeto
                    )
                ) {
                    return obj
                }
            }
        }
        return null
    }

    @Synchronized
    override fun addObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        if (pods >= totalPods) {
            return
        }
        if (!perso.tieneObjetoID(objeto.id) || objeto.posicion != Constantes.OBJETO_POS_NO_EQUIPADO) {
            ENVIAR_Im_INFORMACION(perso, "1OBJECT_DONT_EXIST")
            return
        }
        if (cantidad > objeto.cantidad) {
            cantidad = objeto.cantidad
        }
        var str = ""
        var objMontura = getSimilarObjeto(objeto)
        val nuevaCant = objeto.cantidad - cantidad
        if (objMontura == null) {
            if (nuevaCant <= 0) {
                perso.borrarOEliminarConOR(objeto.id, false)
                _objetos[objeto.id] = objeto
                str = "O+" + objeto.id + "|" + objeto.cantidad + "|" + objeto.objModeloID + "|" + objeto
                    .convertirStatsAString(false)
            } else {
                objMontura = objeto.clonarObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
                addObjeto(objMontura, false)
                _objetos[objMontura.id] = objMontura
                objeto.cantidad = nuevaCant
                str =
                    ("O+" + objMontura.id + "|" + objMontura.cantidad + "|" + objMontura.objModeloID + "|"
                            + objMontura.convertirStatsAString(false))
                ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objeto)
            }
        } else {
            if (nuevaCant <= 0) {
                perso.borrarOEliminarConOR(objeto.id, true)
                objMontura.cantidad = objMontura.cantidad + objeto.cantidad
                str =
                    ("O+" + objMontura.id + "|" + objMontura.cantidad + "|" + objMontura.objModeloID + "|"
                            + objMontura.convertirStatsAString(false))
            } else {
                objeto.cantidad = nuevaCant
                objMontura.cantidad = objMontura.cantidad + cantidad
                str =
                    ("O+" + objMontura.id + "|" + objMontura.cantidad + "|" + objMontura.objModeloID + "|"
                            + objMontura.convertirStatsAString(false))
                ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(perso, objeto)
            }
        }
        ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, str)
    }

    @Synchronized
    override fun remObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
        var cantidad = cantidad
        if (!_objetos.containsKey(objeto.id)) {
            return
        }
        if (cantidad > objeto.cantidad) {
            cantidad = objeto.cantidad
        }
        val nuevaCant = objeto.cantidad - cantidad
        val str: String
        if (nuevaCant < 1) {
            _objetos.remove(objeto.id)
            perso.addObjIdentAInventario(objeto, true)
            str = "O-" + objeto.id
        } else {
            val nuevoObj = objeto.clonarObjeto(cantidad, Constantes.OBJETO_POS_NO_EQUIPADO)
            perso.addObjIdentAInventario(nuevoObj, true)
            objeto.cantidad = nuevaCant
            str = "O+" + objeto.id + "|" + objeto.cantidad + "|" + objeto.objModeloID + "|" + objeto
                .convertirStatsAString(false)
        }
        ENVIAR_EsK_MOVER_A_TIENDA_COFRE_BANCO(perso, str)
    }

    fun estaCriando(): Boolean {
        return celda != null
    }

    val fecundadaHaceMinutos: Int
        get() {
            if (esCastrado() || _reproducciones >= 20 || tiempoFecundacion <= 0) {
                tiempoFecundacion = 0
                return -1
            }
            val minutos = ((System.currentTimeMillis() - tiempoFecundacion) / (60 * 1000)).toInt()
            return minutos + 1
        }

    private fun disponibleParaFecundar(): Boolean {
        return if (esCastrado() || _reproducciones >= 20 || tiempoFecundacion > 0) {
            false
        } else amor >= 7500 && resistencia >= 7500 && (_salvaje || nivel >= 5)
    }

    fun setMapaCelda(mapa: Mapa?, celda: Celda?) {
        this.mapa = mapa
        this.celda = celda
    }

    val talla: Int
        get() = _talla.toInt()

    val reprod: Int
        get() = _reproducciones.toInt()

    val objetos: Collection<Objeto?>
        get() = _objetos.values

    fun castrarPavo() {
        _reproducciones = -1
    }

    fun quitarCastrado() {
        _reproducciones = 0
    }

    fun strCapacidades(): String {
        val s = StringBuilder()
        for (b in capacidades) {
            if (s.length > 0) {
                s.append(",")
            }
            s.append(b.toInt())
        }
        return s.toString()
    }

    fun detallesMontura(): String {
        val str = StringBuilder("$id:")
        str.append(color).append(":")
        str.append(ancestros).append(":")
        str.append(",,").append(strCapacidades()).append(":")
        str.append(nombre).append(":")
        str.append(_sexo.toInt()).append(":")
        str.append(stringExp()).append(":")
        str.append(nivel).append(":")
        str.append(if (esMontable()) "1" else "0").append(":")
        str.append(totalPods).append(":")
        str.append(if (_salvaje) 1 else 0).append(":") // salvaje
        str.append(resistencia).append(",10000:")
        str.append(madurez).append(",").append(maxMadurez).append(":")
        str.append(energia).append(",").append(maxEnergia).append(":")
        str.append(serenidad).append(",-10000,10000:")
        str.append(amor).append(",10000:")
        str.append(fecundadaHaceMinutos).append(":")
        str.append(if (disponibleParaFecundar()) 10 else 0).append(":")
        str.append(convertirStringAStats()).append(":")
        str.append(fatiga).append(",240:")
        str.append(_reproducciones.toInt()).append(",20:")
        return str.toString()
    }

    private fun convertirStringAStats(): String {
        val stats1 = StringBuilder()
        for ((key, value) in stats.entrySet) {
            if (stats1.length > 0) {
                stats1.append(",")
            }
            stats1.append(Integer.toHexString(key)).append("#")
                .append(Integer.toHexString(value)).append("#0#0")
        }
        return stats1.toString()
    }

    private val maxEnergia: Int
        private get() = _maxEnergia + _maxPod / 10 * nivel

    // portadora
    val totalPods: Int
        get() =// portadora
            (if (capacidades.contains(Constantes.HABILIDAD_PORTADORA)) 2 else 1) * (_maxPod + _semiPod * nivel)

    private fun stringExp(): String {
        return exp.toString() + "," + getExpMontura(nivel) + "," + getExpMontura(nivel + 1)
    }

    fun esMontable(): Boolean {
        if (AtlantaMain.PARAM_MONTURA_SIEMPRE_MONTABLES) {
            return true
        }
        return if (!AtlantaMain.PARAM_CRIAR_MONTURA || _monturaModelo != null && _monturaModelo.colorID == 88) {
            true
        } else !_salvaje && energia >= 10 && madurez >= maxMadurez && fatiga < 240 && (!AtlantaMain.MODO_ANKALIKE
                || nivel >= 5)
    }

    fun setMaxMadurez() {
        madurez = maxMadurez
    }

    fun setMaxEnergia() {
        energia = maxEnergia
    }

    private fun restarFatiga() {
        if (!AtlantaMain.PARAM_CRIAR_MONTURA) {
            return
        }
        fatiga -= 10
        if (fatiga < 0) {
            fatiga = 0
        }
    }

    fun restarAmor(amor: Int) {
        if (!AtlantaMain.PARAM_CRIAR_MONTURA) {
            return
        }
        this.amor -= amor
        if (this.amor < 0) {
            this.amor = 0
        }
    }

    fun restarResistencia(resistencia: Int) {
        if (!AtlantaMain.PARAM_CRIAR_MONTURA) {
            return
        }
        this.resistencia -= resistencia
        if (this.resistencia < 0) {
            this.resistencia = 0
        }
    }

    private fun restarSerenidad() {
        if (!AtlantaMain.PARAM_CRIAR_MONTURA) {
            return
        }
        val dueño = getPersonaje(dueñoID)
        if (dueño == null) {
            eliminarMontura(this)
            return
        }
        if (getPersonaje(dueñoID)!!.esAbonado()) {
            serenidad -= 100 * AtlantaMain.RATE_CRIANZA_MONTURA_ABONADOS
        } else {
            serenidad -= 100 * AtlantaMain.RATE_CRIANZA_MONTURA
        }
        if (serenidad < -10000) {
            serenidad = -10000
        }
    }

    private fun aumentarMadurez() {
        val maxMadurez = maxMadurez
        val dueño = getPersonaje(dueñoID)
        if (dueño == null) {
            eliminarMontura(this)
            return
        }
        if (madurez < maxMadurez) {
            if (getPersonaje(dueñoID)!!.esAbonado()) {
                madurez += 100 * AtlantaMain.RATE_CRIANZA_MONTURA_ABONADOS
            } else {
                madurez += 100 * AtlantaMain.RATE_CRIANZA_MONTURA
            }
            if (capacidades.contains(Constantes.HABILIDAD_PRECOZ)) {
                if (getPersonaje(dueñoID)!!.esAbonado()) {
                    madurez += 100 * AtlantaMain.RATE_CRIANZA_MONTURA_ABONADOS
                } else {
                    madurez += 100 * AtlantaMain.RATE_CRIANZA_MONTURA
                }
            }
            if (_talla < 100) {
                val talla = _talla
                if (maxMadurez / madurez <= 1) {
                    _talla = 100
                } else if (_talla < 75 && maxMadurez / madurez == 2) {
                    _talla = 75
                } else if (_talla < 50 && maxMadurez / madurez >= 3) {
                    _talla = 50
                }
                if (talla != _talla) ENVIAR_GM_DRAGOPAVO_A_MAPA(mapa!!, "~", this)
            }
        }
        if (madurez > maxMadurez) {
            madurez = maxMadurez
        }
    }

    private fun aumentarAmor() { // enamorada
        if (!AtlantaMain.PARAM_CRIAR_MONTURA) {
            return
        }
        val dueño = getPersonaje(dueñoID)
        if (dueño == null) {
            eliminarMontura(this)
            return
        }
        if (getPersonaje(dueñoID)!!.esAbonado()) {
            amor += 100 * AtlantaMain.RATE_CRIANZA_MONTURA_ABONADOS
        } else {
            amor += 100 * AtlantaMain.RATE_CRIANZA_MONTURA
        }
        if (amor > 10000) {
            amor = 10000
        }
    }

    private fun aumentarResistencia() { // resistente
        if (!AtlantaMain.PARAM_CRIAR_MONTURA) {
            return
        }
        val dueño = getPersonaje(dueñoID)
        if (dueño == null) {
            eliminarMontura(this)
            return
        }
        if (getPersonaje(dueñoID)!!.esAbonado()) {
            resistencia += ((if (capacidades.contains(Constantes.HABILIDAD_RESISTENTE)) 2 else 1) * 100
                    * AtlantaMain.RATE_CRIANZA_MONTURA_ABONADOS)
        } else {
            resistencia += ((if (capacidades.contains(Constantes.HABILIDAD_RESISTENTE)) 2 else 1) * 100
                    * AtlantaMain.RATE_CRIANZA_MONTURA)
        }
        if (resistencia > 10000) {
            resistencia = 10000
        }
    }

    private fun aumentarFatiga() { // infatigable
        if (!AtlantaMain.PARAM_CRIAR_MONTURA) {
            return
        }
        fatiga += if (capacidades.contains(Constantes.HABILIDAD_INFATIGABLE)) 1 else 2
        if (fatiga > 240) {
            fatiga = 240
        }
    }

    private fun aumentarSerenidad() {
        if (!AtlantaMain.PARAM_CRIAR_MONTURA) {
            return
        }
        val dueño = getPersonaje(dueñoID)
        if (dueño == null) {
            eliminarMontura(this)
            return
        }
        if (getPersonaje(dueñoID)!!.esAbonado()) {
            serenidad += 100 * AtlantaMain.RATE_CRIANZA_MONTURA_ABONADOS
        } else {
            serenidad += 100 * AtlantaMain.RATE_CRIANZA_MONTURA
        }
        if (serenidad > 10000) {
            serenidad = 10000
        }
    }

    private fun aumentarEnergia() {
        if (!AtlantaMain.PARAM_CRIAR_MONTURA) {
            return
        }
        val dueño = getPersonaje(dueñoID)
        if (dueño == null) {
            eliminarMontura(this)
            return
        }
        if (dueño.esAbonado()) {
            energia += 10 * AtlantaMain.RATE_CRIANZA_MONTURA_ABONADOS * AtlantaMain.RATE_CRIANZA_MONTURA
        } else {
            energia += 10 * AtlantaMain.RATE_CRIANZA_MONTURA
        }
        val maxEnergia = maxEnergia
        if (energia > maxEnergia) {
            energia = maxEnergia
        }
    }

    fun aumentarEnergia(valor: Int, veces: Int) {
        energia += valor * veces
        val maxEnergia = maxEnergia
        if (energia > maxEnergia) {
            energia = maxEnergia
        }
    }

    fun energiaPerdida(energia: Int) {
        this.energia -= energia
        if (this.energia < 0) {
            this.energia = 0
        }
    }

    fun aumentarReproduccion() {
        if (esCastrado()) {
            return
        }
        _reproducciones = (_reproducciones + 1).toByte()
    }

    fun stringObjetosBD(): String {
        val str = StringBuilder()
        for (id in _objetos.keys) {
            str.append(if (str.isNotEmpty()) "," else "").append(id)
        }
        return str.toString()
    }

    fun addExperiencia(exp: Long) {
        var exp = exp
        if (capacidades.contains(Constantes.HABILIDAD_SABIA)) {
            exp *= 2
        }
        val nivel = nivel
        this.exp += exp
        while (this.exp >= getExpMontura(this.nivel + 1) && this.nivel < AtlantaMain.NIVEL_MAX_MONTURA) {
            this.nivel++
        }
        if (nivel != this.nivel) {
            statsMontura
        }
    }

    fun getStringColor(colorDueñoPavo: String): String {
        return color.toString() + if (capacidades.contains(Constantes.HABILIDAD_CAMALEON)) ",$colorDueñoPavo" else ""
    }

    fun addHabilidad(habilidad: Byte) {
        if (habilidad >= 1 && habilidad <= 9) {
            capacidades.add(habilidad)
        }
    }

    fun getHabilidad(habilidad: Byte): Boolean {
        return capacidades.contains(habilidad)
    }

    val orientacion: Int
        get() = _orientacion.toInt()

    fun setFecundada(b: Boolean) {
        if (esCastrado() || _sexo == Constantes.SEXO_MASCULINO) {
            tiempoFecundacion = 0
            return
        }
        tiempoFecundacion = if (b) System.currentTimeMillis() else 0
    }

    fun setFecundada(minutos: Int) {
        if (_sexo == Constantes.SEXO_MASCULINO) {
            return
        }
        tiempoFecundacion = System.currentTimeMillis() - minutos * 60 * 1000
    }

    fun esCastrado(): Boolean {
        return _reproducciones.toInt() == -1
    }

    fun stringGM(): String {
        val str = StringBuilder()
        if (celda == null) {
            str.append(mapa!!.cercado!!.celdaMontura.toInt())
        } else {
            str.append(celda!!.id.toInt())
        }
        str.append(";")
        str.append(_orientacion.toInt()).append(";0;").append(id).append(";").append(nombre).append(";-9;")
        if (color == 88) {
            str.append(7005)
        } else {
            str.append(7002)
        }
        str.append("^").append(_talla.toInt()).append(";")
        try {
            str.append(getPersonaje(dueñoID)!!.nombre)
        } catch (e: Exception) {
            str.append("Sin Dueño")
        }
        str.append(";").append(nivel).append(";").append(color)
        return str.toString()
    }

    fun moverMontura(dueño: Personaje?, dir: Int, celdasAMover: Int, alejar: Boolean) {
        val cercado = mapa!!.cercado
        if (mapa == null || celda == null || cercado == null) {
            return
        }
        var direccion: Int
        val celdaInicio = celda!!.id
        direccion = if (dir == -1) {
            if (dueño == null || dueño.celda.id == celdaInicio) {
                return
            }
            Camino.direccionEntreDosCeldas(mapa, celdaInicio, dueño.celda.id, true)
        } else {
            dir
        }
        if (alejar) {
            direccion = Camino.getDireccionOpuesta(direccion)
        }
        val cDir = Camino.getDireccionPorIndex(direccion)
        var accion = 0
        var celdasMovidas = 0
        val path = StringBuilder()
        var tempCeldaID = celdaInicio
        var celdaPrueba = celdaInicio
        var golpeoObjetoCrianza = false
        for (i in 0 until celdasAMover) {
            celdaPrueba = Camino.getSigIDCeldaMismaDir(celdaPrueba, direccion, mapa, false)
            if (mapa!!.getCelda(celdaPrueba) == null) {
                return
            }
            if (cercado.objetosCrianza.containsKey(celdaPrueba)) {
                val objeto = cercado.objetosCrianza[celdaPrueba]
                if (objeto == null && !cercado.esPublico()) {
                    break
                }
                golpeoObjetoCrianza = true
                val caract =
                    getCaracObjCria(objeto?.objModeloID ?: getObjCriaPorMapa(mapa!!.id))
                when (caract) {
                    1 -> if (serenidad <= 2000 && serenidad >= -2000) {
                        aumentarMadurez()
                    }
                    2 -> if (serenidad < 0) {
                        aumentarResistencia()
                    }
                    3 -> if (serenidad > 0) {
                        aumentarAmor()
                    }
                    4 -> restarSerenidad()
                    5 -> aumentarSerenidad()
                    6 -> {
                        restarFatiga()
                        aumentarEnergia()
                    }
                }
                aumentarFatiga()
                if (!cercado.esPublico()) {
                    if (Objects.requireNonNull(objeto)!!.addDurabilidad(-AtlantaMain.DURABILIDAD_REDUCIR_OBJETO_CRIA)) {
                        if (cercado.retirarObjCria(celdaPrueba, null)) {
                            ENVIAR_GDO_OBJETO_TIRAR_SUELO(mapa!!, '-', celdaPrueba, 0, false, "")
                        }
                    } else {
                        ENVIAR_GDO_OBJETO_TIRAR_SUELO(
                            mapa!!, '+', celdaPrueba, objeto!!.objModeloID, true, objeto
                                .durabilidad.toString() + ";" + objeto.durabilidadMax
                        )
                    }
                }
                break
            }
            if (!mapa!!.getCelda(celdaPrueba)!!.esCaminable(false) || cercado.celdaPuerta == celdaPrueba || Camino
                    .celdaSalienteLateral(mapa!!.ancho, mapa!!.alto, tempCeldaID, celdaPrueba)
            ) {
                break
            }
            tempCeldaID = celdaPrueba
            path.append(cDir).append(celdaIDAHash(tempCeldaID))
            celdasMovidas++
        }
        if (tempCeldaID != celdaInicio) {
            ENVIAR_GA_MOVER_SPRITE_MAPA(
                mapa!!, 0, 1, id.toString() + "", getValorHashPorNumero(_orientacion.toInt())
                    .toString() + celdaIDAHash(celdaInicio) + path
            )
            celda = mapa!!.getCelda(tempCeldaID)
            val azar = getRandomInt(1, 10)
            if (azar == 5) {
                accion = 8
            }
            if (cercado.criando.size > 1) {
                for (montura in cercado.criando.values) {
                    if (puedeFecundar(montura)) {
                        accion = 4 // accion de aparearse
                        break
                    }
                }
            }
        } else {
            ENVIAR_eD_CAMBIAR_ORIENTACION(mapa!!, id, getNumeroPorValorHash(cDir))
        }
        if (_ubicacion == Ubicacion.NULL) {
            return
        }
        _orientacion = getNumeroPorValorHash(cDir)
        when (accion) {
            4 -> ENVIAR_eUK_EMOTE_MAPA(mapa!!, id, accion, 0)
            0 -> if (golpeoObjetoCrianza) {
                ENVIAR_GDE_FRAME_OBJECT_EXTERNAL(mapa!!, "$celdaPrueba;4")
            }
            else -> {
                ENVIAR_eUK_EMOTE_MAPA(mapa!!, id, accion, 0)
                if (golpeoObjetoCrianza) {
                    ENVIAR_GDE_FRAME_OBJECT_EXTERNAL(mapa!!, "$celdaPrueba;4")
                }
            }
        }
    }

    private fun puedeFecundar(montura: Montura): Boolean {
        if (montura.id == id) {
            return false
        }
        if (montura.celda != celda) { // diferente celdas
            return false
        }
        if (montura.sexo == _sexo.toInt() || !montura.disponibleParaFecundar() || !disponibleParaFecundar() || montura
                .esCastrado() || esCastrado()
        ) {
            return false
        }
        if (mapa!!.cercado!!.esPublico() && montura.dueñoID != dueñoID) {
            return false
        }
        if (montura.capacidades.contains(Constantes.HABILIDAD_ENAMORADA) || capacidades.contains(
                Constantes.HABILIDAD_ENAMORADA
            ) || getRandomInt(0, 5) == 2
        ) {
            var madre: Montura? = null
            var padre: Montura? = null
            if (_sexo == Constantes.SEXO_FEMENINO) {
                madre = this
                padre = montura
            } else if (montura.sexo == Constantes.SEXO_FEMENINO.toInt()) {
                padre = this
                madre = montura
            }
            // madre
            Objects.requireNonNull(madre)!!.setFecundada(true)
            madre!!.parejaID = Objects.requireNonNull(padre)!!.id
            // padre
            padre!!.aumentarReproduccion()
            padre.restarAmor(7500)
            padre.restarResistencia(7500)
            if (padre.pudoEscapar()) {
                ENVIAR_GM_BORRAR_GM_A_MAPA(mapa!!, id)
            }
            return true
        }
        return false
    }

    fun pudoEscapar(): Boolean {
        if (esSalvaje()) {
            val prob = getRandomInt(1, 100)
            if (prob <= AtlantaMain.PROBABILIDAD_ESCAPAR_MONTURA_DESPUES_FECUNDAR) {
                val dueñoOtro = getPersonaje(dueñoID)
                if (dueñoOtro != null) {
                    if (dueñoOtro.enLinea()) {
                        ENVIAR_Im_INFORMACION(
                            dueñoOtro,
                            "0111; <b>" + nombre + "</b>~" + mapa!!.id
                        )
                    } else {
                        dueñoOtro.cuenta.addMensaje("0111; <b>" + nombre + "</b>~" + mapa!!.id, true)
                    }
                }
                return true
            }
        }
        return false
    }

    fun velocidadAprendizaje(): Byte {
        if (color == 18) { // dragopavo dorado
            return 20
        }
        return if (_monturaModelo == null) {
            0
        } else when (_monturaModelo.generacionID.toInt()) {
            2, 3, 4 -> 80
            5, 6, 7 -> 60
            8, 9 -> 40
            10 -> 20
            1 -> 100
            else -> 100
        }
    }

    fun minutosParir(): Int {
        return AtlantaMain.MINUTOS_GESTACION_MONTURA + ((_monturaModelo!!.generacionID - 1)
                * (AtlantaMain.MINUTOS_GESTACION_MONTURA / 4)) + ((_reproducciones - 1) * AtlantaMain.MINUTOS_GESTACION_MONTURA
                / 8)
    }

    override fun addKamas(k: Long, perso: Personaje?) {}
    override val kamas: Long
        get() = 0L

    override fun cerrar(perso: Personaje?, exito: String) {
        if (perso != null) {
            perso.cerrarVentanaExchange(exito)
        }
    }

    override fun botonOK(perso: Personaje) {}
    enum class Ubicacion {
        ESTABLO, CERCADO, PERGAMINO, EQUIPADA, NULL
    }

    companion object {
        var SEXO_POSIBLES = byteArrayOf(0, 0, 0, 1, 1, 1) // 0 = macho , 1 = hembra
    }
}