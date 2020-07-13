package estaticos

import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Constantes.puedeAgredir
import estaticos.Encriptador.celdaIDAHash
import estaticos.Encriptador.getNumeroPorValorHash
import estaticos.Encriptador.hashACeldaID
import estaticos.Formulas.getRandomInt
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.Mundo.Duo
import variables.hechizo.StatHechizo
import variables.mapa.Celda
import variables.mapa.Mapa
import variables.pelea.Luchador
import variables.pelea.Pelea
import variables.personaje.Personaje
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import java.util.regex.Pattern
import kotlin.math.abs

object Camino {
    private val DIRECCIONES = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
    private val COORD_ALREDEDOR =
        arrayOf(byteArrayOf(1, 0), byteArrayOf(0, 1), byteArrayOf(-1, 0), byteArrayOf(0, -1))

    fun getPathPelea(
        mapa: Mapa?, celdaInicio: Short,
        celdaDestino: Short, PM: Int, tacleado: Luchador?, ignoraLuchadores: Boolean
    ): Duo<Int, ArrayList<Celda?>>? {
        var celdaDestino = celdaDestino
        var PM = PM
        var intentos = 0
        while (intentos < 5) {
            try {
                if (celdaInicio == celdaDestino || mapa!!.getCelda(celdaInicio) == null || mapa.getCelda(celdaDestino) == null) {
                    return null
                }
                if (PM < 0) {
                    PM = 500
                }
                val ancho = mapa.ancho
                // final int nroLados = 4;
                val diagonales =
                    byteArrayOf(ancho, (ancho - 1).toByte(), (-ancho).toByte(), (-(ancho - 1)).toByte())
                val unos = byteArrayOf(1, 1, 1, 1)
                val celdas: Map<Short, Celda> = TreeMap(mapa.celdas)
                val celdasCamino1: MutableMap<Short, CeldaCamino> = TreeMap()
                val celdasCamino2: MutableMap<Short, CeldaCamino> = TreeMap()
                var ok = true
                val newCeldaCamino = CeldaCamino()
                newCeldaCamino.id = celdaInicio
                newCeldaCamino.cantPM = 0
                newCeldaCamino.valorX = 0
                newCeldaCamino.distEstimada = distanciaEstimada(mapa, celdaInicio, celdaDestino).toInt()
                newCeldaCamino.distEstimadaX = newCeldaCamino.distEstimada
                newCeldaCamino.level = celdas[celdaInicio]!!.level.toShort()
                newCeldaCamino.movimiento = celdas[celdaInicio]!!.movimiento.toShort()
                newCeldaCamino.anterior = null
                celdasCamino1[newCeldaCamino.id] = newCeldaCamino
                // pone la primera celda de inicio
                var celdaAnterior: Short = -1
                while (ok) {
                    var sigCelda: Short = -1
                    var distEntreCeldas = 500000
                    for (c in celdasCamino1.values) {
                        if (c.distEstimadaX < distEntreCeldas) {
                            distEntreCeldas = c.distEstimadaX
                            sigCelda = c.id
                        }
                    }
                    var celdaCamino = celdasCamino1[sigCelda]
                    celdasCamino1.remove(sigCelda)
                    if (celdaCamino!!.anterior != null) {
                        celdaAnterior = celdaCamino.anterior!!.id
                    }
                    if (celdaCamino.id == celdaDestino) { // se llego al objetivo
                        val tempCeldas = ArrayList<Celda?>()
                        while (Objects.requireNonNull(celdaCamino)!!.id != celdaInicio) {
                            if (celdaCamino!!.movimiento.toInt() == 0) {
                                tempCeldas.clear()
                            } else {
                                tempCeldas.add(0, celdas[celdaCamino.id])
                            }
                            celdaCamino = celdaCamino.anterior
                        }
                        return Duo(intentos, tempCeldas)
                    }
                    var enemigoAlr = false
                    if (tacleado != null) {
                        if (hayAlrededorAmigoOEnemigo(mapa, tacleado, false, true)) {
                            enemigoAlr = true
                        }
                    }
                    val direcciones =
                        listaDirEntreDosCeldas2(mapa, celdaCamino.id, celdaDestino, celdaAnterior)
                    var puedeLlegarDestino = false
                    if (!enemigoAlr) {
                        for (i in 0..3) {
                            val direccion = direcciones[i]
                            val tempCeldaID = (celdaCamino.id + diagonales[direccion.toInt()]).toShort()
                            if (celdas[tempCeldaID] == null) {
                                continue
                            }
                            if (Math.abs(celdas[tempCeldaID]!!.coordX - celdas[celdaCamino.id]!!.coordX) <= 53) {
                                val tempCelda = celdas[tempCeldaID]
                                val tempLevelCelda = tempCelda!!.level
                                val sinLuchador =
                                    tempCeldaID == celdaDestino || ignoraLuchadores || tempCelda.primerLuchador == null
                                puedeLlegarDestino = tempCeldaID == celdaDestino && tempCelda.movimiento.toInt() == 1
                                val caminable =
                                    celdaCamino.level.toInt() == -1 || Math.abs(tempLevelCelda - celdaCamino.level) < 2
                                if (caminable && tempCelda.activo && sinLuchador) {
                                    val aaaa =
                                        if (tempCelda.movimiento.toInt() == 0 || tempCelda.movimiento.toInt() == 1) 1000 else 0
                                    val valorX =
                                        (celdaCamino.valorX + unos[direccion.toInt()] + aaaa.toDouble() + if (tempCelda
                                                .movimiento.toInt() == 1 && puedeLlegarDestino
                                        ) -1000.0 else (if (direccion.toShort() != celdaCamino.direccion) 0.5 else 0.0) + (5 - tempCelda.movimiento) / 3).toShort()
                                    val cantMov = (celdaCamino.cantPM + unos[direccion.toInt()]).toShort()
                                    var tempValorX: Short = -1
                                    if (celdasCamino1[tempCeldaID] != null) {
                                        tempValorX = celdasCamino1[tempCeldaID]!!.valorX
                                    } else if (celdasCamino2[tempCeldaID] != null) {
                                        tempValorX = celdasCamino2[tempCeldaID]!!.valorX
                                    }
                                    if ((tempValorX.toInt() == -1 || tempValorX > valorX) && cantMov <= PM) {
                                        if (celdasCamino2[tempCeldaID] != null) {
                                            celdasCamino2.remove(tempCeldaID)
                                        }
                                        val tempCeldaCamino = CeldaCamino()
                                        tempCeldaCamino.id = tempCeldaID
                                        tempCeldaCamino.cantPM = cantMov
                                        tempCeldaCamino.valorX = valorX.toShort()
                                        tempCeldaCamino.distEstimada =
                                            distanciaEstimada(mapa, tempCeldaID, celdaDestino).toInt()
                                        tempCeldaCamino.distEstimadaX =
                                            tempCeldaCamino.valorX + tempCeldaCamino.distEstimada + i * 3
                                        tempCeldaCamino.direccion = direccion.toShort()
                                        tempCeldaCamino.level = tempLevelCelda.toShort()
                                        tempCeldaCamino.movimiento = tempCelda.movimiento.toShort()
                                        tempCeldaCamino.anterior = celdaCamino
                                        celdasCamino1[tempCeldaID] = tempCeldaCamino
                                    }
                                }
                            }
                        }
                    }
                    celdasCamino2[celdaCamino.id] = CeldaCamino()
                    celdasCamino2[celdaCamino.id]!!.valorX = celdaCamino.valorX
                    ok = false
                    for (c in celdasCamino1.values) {
                        if (c == null) {
                            continue
                        }
                        ok = true
                        break
                    }
                }
                return null
            } catch (e: Exception) {
                celdaDestino = celdaMasCercanaACeldaObjetivo(mapa, celdaDestino, celdaInicio, null)
                intentos++
            }
        }
        return null
    }

    @JvmStatic
    fun nroCeldasAMover(
        mapa: Mapa, pelea: Pelea?, pathRef: AtomicReference<String>,
        celdaInicio: Short, celdaFinal: Short, perso: Personaje?
    ): Short {
        var nuevaCelda = celdaInicio
        var movimientos: Short = 0
        val path = pathRef.get()
        val nuevoPath = StringBuilder()
        var i = 0
        while (i < path.length) {
            if (path.length < i + 3) {
                return movimientos
            }
            val miniPath = path.substring(i, i + 3)
            val cDir = miniPath[0]
            val celdaTemp = hashACeldaID(miniPath.substring(1))
            // if (pelea != null && i > 0) {
// if (getEnemigoAlrededor(nuevaCelda, mapa, null, pelea.getLuchadorTurno().getEquipoBin())
// != null) {
// pathRef.set(nuevoPath.toString());
// return (short) (movimientos + 10000);
// }
// for (final Trampa trampa : pelea.getTrampas()) {
// final int dist = distanciaDosCeldas(mapa, trampa.getCelda().getID(), nuevaCelda);
// if (dist <= trampa.getTamaño()) {
// pathRef.set(nuevoPath.toString());
// return (short) (movimientos + 10000);
// }
// }
// if (pelea.getMapaCopia().getCelda(nuevaCelda).getPrimerLuchador() != null) {
// pathRef.set(nuevoPath.toString());
// return (short) (movimientos + 20000);
// }
// }
            val aPathInfos = pathSimpleValido(
                nuevaCelda, celdaTemp, getIndexPorDireccion(cDir).toInt(), mapa, pelea,
                celdaFinal, perso
            ).split(Pattern.quote(";").toRegex()).toTypedArray()
            val resultado = aPathInfos[0]
            val nroCeldas = aPathInfos[1].toInt()
            if (aPathInfos.size > 2) {
                nuevaCelda = aPathInfos[2].toShort()
            }
            when (resultado) {
                "invisible" -> {
                    movimientos = (movimientos + nroCeldas.toShort()).toShort()
                    nuevoPath.append(cDir).append(celdaIDAHash(nuevaCelda))
                    pathRef.set(nuevoPath.toString())
                    return (movimientos + 20000).toShort()
                }
                "stop", "trampa" -> {
                    movimientos = (movimientos + nroCeldas.toShort()).toShort()
                    nuevoPath.append(cDir).append(celdaIDAHash(nuevaCelda))
                    pathRef.set(nuevoPath.toString())
                    return (movimientos + 10000).toShort()
                }
                "no" -> {
                    pathRef.set(nuevoPath.toString())
                    return -1000
                }
                "ok" -> {
                    nuevaCelda = celdaTemp
                    movimientos = (movimientos + nroCeldas.toShort()).toShort()
                }
            }
            nuevoPath.append(cDir).append(celdaIDAHash(nuevaCelda))
            i += 3
        }
        pathRef.set(nuevoPath.toString())
        return movimientos
    }

    private fun pathSimpleValido(
        celdaID: Short, celdaSemiFinal: Short, dir: Int,
        mapa: Mapa, pelea: Pelea?, celdaFinalDest: Short, perso: Personaje?
    ): String {
        var ultimaCelda = celdaID
        var _nroMovimientos = 1
        while (_nroMovimientos <= 64) {
            val celdaTempID = getSigIDCeldaMismaDir(ultimaCelda, dir, mapa, pelea != null)
            val celdaTemp = mapa.getCelda(celdaTempID)
            if (celdaTemp == null || !celdaTemp.esCaminable(true)) {
                _nroMovimientos--
                return "stop;$_nroMovimientos;$ultimaCelda"
            }
            if (pelea != null) {
                val ocupado = mapa.getCelda(celdaTempID)!!.primerLuchador
                val luchTurno = pelea.luchadorTurno
                if (ocupado != null) {
                    _nroMovimientos--
                    return if (ocupado.esInvisible(luchTurno!!.id)) {
                        "invisible;$_nroMovimientos;$ultimaCelda"
                    } else {
                        "stop;$_nroMovimientos;$ultimaCelda"
                    }
                }
                if (celdaTempID != celdaFinalDest) { // si algun luchador esta alrededor por donde va a pasar
                    val alrededor = getEnemigoAlrededor(
                        celdaTempID,
                        mapa,
                        null,
                        if (luchTurno!!.esIAChafer()) 3 else luchTurno.equipoBin.toInt()
                    )
                    if (alrededor != null && alrededor.id != luchTurno.id) {
                        return if (alrededor.esInvisible(luchTurno.id)) {
                            "invisible;$_nroMovimientos;$celdaTempID"
                        } else {
                            "stop;$_nroMovimientos;$celdaTempID"
                        }
                    }
                    // si se topa con una trampa
                    if (pelea.trampas != null) {
                        for (trampa in pelea.trampas!!) {
                            val dist = distanciaDosCeldas(mapa, trampa.celda.id, celdaTempID).toInt()
                            if (dist <= trampa.tamaño) {
                                return "trampa;$_nroMovimientos;$celdaTempID"
                            }
                        }
                    }
                }
            } else {
                try {
                    for (p in mapa.arrayPersonajes!!) {
                        if (puedeAgredir(perso, p)) {
                            continue
                        }
                        if (perso != null) {
                            if (p.alineacion == Constantes.ALINEACION_BONTARIANO && perso
                                    .alineacion == Constantes.ALINEACION_NEUTRAL || p
                                    .alineacion == Constantes.ALINEACION_BRAKMARIANO && perso
                                    .alineacion == Constantes.ALINEACION_NEUTRAL || (p.alineacion == Constantes.ALINEACION_MERCENARIO
                                        && perso.alineacion == Constantes.ALINEACION_NEUTRAL)
                            ) {
                                continue
                            }
                        }
                        val agroP =
                            p.totalStats.getTotalStatParaMostrar(Constantes.STAT_AGREDIR_AUTOMATICAMENTE)
                        val agroPerso =
                            perso?.totalStats?.getTotalStatParaMostrar(Constantes.STAT_AGREDIR_AUTOMATICAMENTE)
                        if (agroPerso != null) {
                            if (agroP <= 0 && agroPerso <= 0) {
                                continue
                            }
                        }
                        val distAgro = if (agroPerso!! >= agroP) agroPerso else agroP
                        if (distanciaDosCeldas(mapa, p.celda.id, celdaTempID) <= distAgro) {
                            return "stop;$_nroMovimientos;$celdaTempID"
                        }
                    }
                } catch (ignored: Exception) {
                }
                try {
                    for (gm in mapa.grupoMobsTotales!!.values) {
                        if (perso != null) {
                            if (perso.estaDisponible(true, true)) {
                                continue
                            }
                        }
                        if (gm.distAgresion <= 0) {
                            continue
                        }
                        if (perso != null) {
                            if (perso.alineacion == gm.alineacion || perso.alineacion == Constantes.ALINEACION_MERCENARIO) {
                                continue
                            }
                        }
                        if (perso != null) {
                            if (gm.alineacion == Constantes.ALINEACION_BONTARIANO && perso
                                    .alineacion == Constantes.ALINEACION_NEUTRAL || gm
                                    .alineacion == Constantes.ALINEACION_BRAKMARIANO && perso
                                    .alineacion == Constantes.ALINEACION_NEUTRAL || gm
                                    .alineacion == Constantes.ALINEACION_MERCENARIO && perso
                                    .alineacion == Constantes.ALINEACION_NEUTRAL
                            ) {
                                continue
                            }
                        }
                        if (distanciaDosCeldas(mapa, gm.celdaID, celdaTempID) <= gm.distAgresion) {
                            return if (gm.agredePersonaje(perso)) {
                                "stop;$_nroMovimientos;$celdaTempID"
                            } else {
                                continue
                            }
                        }
                    }
                } catch (ignored: Exception) {
                }
                if (celdaTempID == celdaFinalDest) {
                    if (celdaTemp.objetoInteractivo != null) { // para hacer q los trigos, cereales e interactivos caminables
// los demas como nidos bwaks y otros se muevan hasta ahi
                        return "stop;$_nroMovimientos;$ultimaCelda"
                    }
                }
                if (!celdaTemp.accionesIsEmpty() && celdaTempID == celdaFinalDest) {
                    return "stop;$_nroMovimientos;$celdaTempID"
                }
            }
            if (celdaTempID == celdaSemiFinal) {
                return "ok;$_nroMovimientos"
            }
            ultimaCelda = celdaTempID
            _nroMovimientos++
        }
        return "no" + ";" + 0
    }

    fun getPathComoString(
        mapa: Mapa?,
        celdas: ArrayList<Celda>,
        celdaInicio: Short,
        esPelea: Boolean
    ): String {
        val pathStr = StringBuilder()
        var tempCeldaID = celdaInicio
        for (celda in celdas) {
            val dir = direccionEntreDosCeldas(mapa, tempCeldaID, celda.id, esPelea)
            if (dir == -1) {
                return ""
            }
            pathStr.append(getDireccionPorIndex(dir))
            pathStr.append(celdaIDAHash(celda.id))
            tempCeldaID = celda.id
        }
        return pathStr.toString()
    }

    fun getPathComoString2(
        mapa: Mapa?,
        celdas: ArrayList<Celda?>,
        celdaInicio: Short,
        esPelea: Boolean
    ): String {
        val pathStr = StringBuilder()
        var tempCeldaID = celdaInicio
        for (celda in celdas) {
            if (celda != null) {
                val dir = direccionEntreDosCeldas(mapa, tempCeldaID, celda.id, esPelea)
                if (dir == -1) {
                    return ""
                }
                pathStr.append(getDireccionPorIndex(dir))
                pathStr.append(celdaIDAHash(celda.id))
                tempCeldaID = celda.id
            }
        }
        return pathStr.toString()
    }

    fun getEnemigoAlrededor(
        celdaID: Short, mapa: Mapa, noRepetir: ArrayList<Int?>?,
        equipoBinLanz: Int
    ): Luchador? {
        val celda = mapa.getCelda(celdaID)
        for (c in COORD_ALREDEDOR) {
            val cell =
                mapa.getCeldaPorPos((celda!!.coordX + c[0]).toByte(), (celda.coordY + c[1]).toByte())
                    ?: continue
            val luchador = cell.primerLuchador ?: continue
            if (noRepetir != null) {
                if (noRepetir.contains(luchador.id)) {
                    continue
                }
            }
            if (luchador.equipoBin.toInt() != equipoBinLanz) {
                return luchador
            }
        }
        return null
    }

    fun hayAlrededorAmigoOEnemigo(
        mapa: Mapa?, lanzador: Luchador, amigo: Boolean,
        invisible: Boolean
    ): Boolean {
        val celda = lanzador.celdaPelea
        for (c in COORD_ALREDEDOR) {
            val cell =
                mapa!!.getCeldaPorPos((celda!!.coordX + c[0]).toByte(), (celda.coordY + c[1]).toByte())
                    ?: continue
            val luchador = cell.primerLuchador
            if (luchador != null && !luchador.estaMuerto()) {
                if (amigo) {
                    if (luchador.equipoBin == lanzador.equipoBin) {
                        return true
                    }
                } else { // enemigo
                    if (luchador.equipoBin != lanzador.equipoBin) {
                        if (invisible) {
                            if (luchador.esInvisible(lanzador.id)) {
                                continue
                            }
                        }
                        return true
                    }
                }
            }
        }
        return false
    }

    fun luchadoresAlrededor(mapa: Mapa, pelea: Pelea?, celda: Celda): ArrayList<Luchador> {
        val luchadores = ArrayList<Luchador>()
        for (c in COORD_ALREDEDOR) {
            val cell =
                mapa.getCeldaPorPos((celda.coordX + c[0]).toByte(), (celda.coordY + c[1]).toByte())
                    ?: continue
            val luchador = cell.primerLuchador
            if (luchador != null) {
                luchadores.add(luchador)
            }
        }
        return luchadores
    }

    fun esSiguienteA(celda1: Celda, celda2: Celda): Boolean {
        val x = Math.abs(celda1.coordX - celda2.coordX).toByte()
        val y = Math.abs(celda1.coordY - celda2.coordY).toByte()
        return x.toInt() == 1 && y.toInt() == 0 || x.toInt() == 0 && y.toInt() == 1
    }

    @JvmStatic
    fun distanciaEntreMapas(mapa1: Mapa, mapa2: Mapa): Int {
        return if (mapa1.subArea!!.area.superArea != mapa2.subArea!!.area.superArea) 10000 else Math.abs(mapa2.x - mapa1.x) + Math.abs(
            mapa2.y - mapa1.y
        )
    }

    fun getSigIDCeldaMismaDir(
        celdaID: Short, direccion: Int, mapa: Mapa?,
        combate: Boolean
    ): Short {
        when (direccion) {
            0 -> return (if (combate) -1 else celdaID + 1).toShort() // derecha
            1 -> return (celdaID + mapa!!.ancho).toShort() // diagonal derecha abajo
            2 -> return (if (combate) -1 else celdaID + (mapa!!.ancho * 2 - 1)).toShort() // abajo
            3 -> return (celdaID + (mapa!!.ancho - 1)).toShort() // diagonal izquierda abajo
            4 -> return (if (combate) -1 else celdaID - 1).toShort() // izquierda
            5 -> return (celdaID - mapa!!.ancho).toShort() // diagonal izquierda arriba
            6 -> return (if (combate) -1 else celdaID - (mapa!!.ancho * 2 - 1)).toShort() // arriba
            7 -> return (celdaID - mapa!!.ancho + 1).toShort() // diagonal derecha arriba
        }
        return -1
    }

    @JvmStatic
    fun distanciaDosCeldas(mapa: Mapa?, celdaInicio: Short, celdaDestino: Short): Short {
        if (celdaInicio == celdaDestino) {
            return 0
        }
        val cInicio = mapa!!.getCelda(celdaInicio)
        val cDestino = mapa.getCelda(celdaDestino)
        if (cInicio == null || cDestino == null) {
            return 0
        }
        val difX = Math.abs(cInicio.coordX - cDestino.coordX)
        val difY = Math.abs(cInicio.coordY - cDestino.coordY)
        return (difX + difY).toShort()
    }

    private fun distanciaEstimada(mapa: Mapa?, celdaInicio: Short, celdaDestino: Short): Short {
        if (celdaInicio == celdaDestino) {
            return 0
        }
        val cInicio = mapa!!.getCelda(celdaInicio)
        val cDestino = mapa.getCelda(celdaDestino)
        if (cInicio == null || cDestino == null) {
            return 0
        }
        val difX = Math.abs(cInicio.coordX - cDestino.coordX)
        val difY = Math.abs(cInicio.coordY - cDestino.coordY)
        // return (short) Math.sqrt(Math.pow(difX, 2) + Math.pow(difY, 2));
// era antes pero lo modifique
        return (difX + difY).toShort()
    }

    fun getCeldaDespuesDeEmpujon(
        pelea: Pelea, celdaInicio: Celda,
        celdaObjetivo: Celda, movimientos: Int
    ): Duo<Int, Short> {
        var movimientos = movimientos
        if (celdaInicio.id == celdaObjetivo.id) {
            return Duo(-1, (-1).toShort())
        }
        val mapa = pelea.mapaCopia
        var dir = direccionEntreDosCeldas(mapa, celdaInicio.id, celdaObjetivo.id, true)
        var celdaID = celdaObjetivo.id
        if (movimientos < 0) {
            dir = getDireccionOpuesta(dir)
            movimientos = -movimientos
        }
        for (i in 0 until movimientos) {
            val sigCeldaID = getSigIDCeldaMismaDir(celdaID, dir, mapa, true)
            val sigCelda = mapa!!.getCelda(sigCeldaID)
            if (sigCelda == null || !sigCelda.esCaminable(true) || sigCelda.primerLuchador != null) {
                return Duo(movimientos - i, celdaID)
            }
            if (pelea.trampas != null) {
                for (trampa in pelea.trampas!!) {
                    val dist = distanciaDosCeldas(mapa, trampa.celda.id, sigCeldaID).toInt()
                    var encontro = false
                    for (a in trampa.celda.trampas!!) {
                        for (b in a.GetSH().efectosNormales) {
                            if (b.efectoID == 5) {
                                encontro = true
                            }
                        }
                    }
                    if (encontro && dist <= trampa.tamaño) {
                        return Duo(0, sigCeldaID)
                    } else if (dist <= trampa.tamaño) {
                        return Duo(movimientos - i + 1000, sigCeldaID)
                    }
                }
            }
            celdaID = sigCeldaID
        }
        return if (celdaID == celdaObjetivo.id) {
            Duo(-1, (-1).toShort())
        } else Duo(0, celdaID)
    }

    fun getDireccionOpuesta(dir: Int): Int {
        return correctaDireccion(dir - 4)
    }

    fun siCeldasEstanEnMismaLinea(mapa: Mapa?, c1: Celda, c2: Celda): Boolean {
        return if (c1.id == c2.id) {
            true
        } else c1.coordX == c2.coordX || c1.coordY == c2.coordY
    }

    fun getIndexPorDireccion(c: Char): Byte {
        var b: Byte = 0
        for (a in DIRECCIONES) {
            if (a == c) {
                return b
            }
            b++
        }
        return 0
    }

    fun getDireccionAleatorio(combate: Boolean): Char {
        return DIRECCIONES[getRandomInt(0, if (combate) 7 else 3)]
    }

    fun getDireccionPorIndex(index: Int): Char {
        return DIRECCIONES[index]
    }

    fun direccionEntreDosCeldas(
        mapa: Mapa?, celdaInicio: Short, celdaDestino: Short,
        esPelea: Boolean
    ): Int {
        if (celdaInicio == celdaDestino || mapa == null) {
            return -1
        }
        if (!esPelea) {
            val ancho = mapa.ancho
            val alrededores = byteArrayOf(
                1, ancho, (ancho * 2 - 1).toByte(), (ancho - 1).toByte(), -1, (-ancho).toByte(),
                (-ancho * 2 + 1).toByte(), (-(ancho - 1)).toByte()
            )
            val _loc7 = celdaDestino - celdaInicio
            for (_loc8 in 7 downTo 0) {
                if (alrededores[_loc8].toInt() == _loc7) {
                    return _loc8
                }
            }
        }
        val cInicio = mapa.getCelda(celdaInicio)
        val cDestino = mapa.getCelda(celdaDestino)
        val difX = cDestino!!.coordX - cInicio!!.coordX
        val difY = cDestino.coordY - cInicio.coordY
        return if (difX == 0) {
            if (difY > 0) {
                3
            } else {
                7
            }
        } else if (difX > 0) {
            1
        } else {
            5
        }
    }

    private fun listaDirEntreDosCeldas(mapa: Mapa?, celdaInicio: Short, celdaDestino: Short): CharArray {
        if (celdaInicio == celdaDestino || mapa == null) {
            return charArrayOf()
        }
        val abc = CharArray(4)
        val b = listaDirEntreDosCeldas2(mapa, celdaInicio, celdaDestino, (-1).toShort())
        for (i in 0..3) {
            when (b[i].toInt()) {
                0 -> abc[i] = 'b'
                1 -> abc[i] = 'd'
                2 -> abc[i] = 'f'
                3 -> abc[i] = 'h'
            }
        }
        return abc
    }

    fun listaDirEntreDosCeldas2(
        mapa: Mapa?, celdaInicio: Short, celdaDestino: Short,
        celdaAnterior: Short
    ): ByteArray {
        if (celdaInicio == celdaDestino || mapa == null) {
            return byteArrayOf()
        }
        val cInicio = mapa.getCelda(celdaInicio)
        val cDestino = mapa.getCelda(celdaDestino)
        val difX = cDestino!!.coordX - cInicio!!.coordX
        val difY = cDestino.coordY - cInicio.coordY
        return if (abs(difY) == abs(difX) && celdaAnterior > 0) {
            listaDirEntreDosCeldas2(mapa, celdaAnterior, celdaDestino, (-1).toShort())
        } else if (abs(difY) > abs(difX)) {
            val c =
                arrayOf(intArrayOf(difX, 0, 2), intArrayOf(difY, 1, 3))
            formulaDireccion(c)
        } else {
            val c =
                arrayOf(intArrayOf(difY, 1, 3), intArrayOf(difX, 0, 2))
            formulaDireccion(c)
        }
    }

    private fun formulaDireccion(c: Array<IntArray>): ByteArray {
        val abc = ByteArray(4)
        for (i in 0..1) {
            val dif = c[i][0]
            var p = i
            if (dif < 0) {
                p = Math.abs(3 - i)
            }
            abc[p] = c[i][1].toByte()
            abc[Math.abs(3 - p)] = c[i][2].toByte()
        }
        return abc
    }

    private fun correctaDireccion(dir: Int): Int {
        var dir = dir
        while (dir < 0) {
            dir += 8
        }
        while (dir >= 8) {
            dir -= 8
        }
        return dir
    }

    fun getCoordPorDireccion(dir: Int): ByteArray {
        val f = arrayOf(
            byteArrayOf(1, -1),
            byteArrayOf(1, 0),
            byteArrayOf(1, 1),
            byteArrayOf(0, 1),
            byteArrayOf(-1, 1),
            byteArrayOf(-1, 0),
            byteArrayOf(-1, -1),
            byteArrayOf(0, -1)
        )
        return f[dir]
    }

    fun celdasAfectadasEnElArea(
        mapa: Mapa, celdaIDObjetivo: Short,
        celdaIDLanzador: Short, areaEfecto: String
    ): ArrayList<Celda> {
        val celdas = ArrayList<Celda>()
        if (mapa.getCelda(celdaIDObjetivo) == null) {
            return celdas
        }
        val tamaño = getNumeroPorValorHash(areaEfecto[1]).toInt()
        when (areaEfecto[0]) {
            'A' -> {
                var a = tamaño
                while (a >= 0) {
                    for (celda2 in celdasPorCruz(mapa.getCelda(celdaIDLanzador), mapa, a)) {
                        val celda = mapa.getCelda(celda2)
                        if (!celdas.contains(celda)) {
                            if (celda != null) {
                                celdas.add(celda)
                            }
                        }
                    }
                    a--
                }
            }
            'D' -> {
                var i = if (tamaño % 2 == 0) 1 else 0
                while (i < tamaño) {
                    for (celda2 in celdasPorDistancia(mapa.getCelda(celdaIDObjetivo), mapa, i + 1)) {
                        val celda = mapa.getCelda(celda2)
                        if (!celdas.contains(celda)) {
                            if (celda != null) {
                                celdas.add(celda)
                            }
                        }
                    }
                    i += 2
                }
            }
            'C' -> {
                if (tamaño >= 64) {
                    celdas.addAll(mapa.celdas.values)
                    return celdas
                }
                var a = tamaño
                while (a >= 0) {
                    for (celda2 in celdasPorDistancia(mapa.getCelda(celdaIDObjetivo), mapa, a)) {
                        val celda = mapa.getCelda(celda2)
                        if (!celdas.contains(celda)) {
                            if (celda != null) {
                                celdas.add(celda)
                            }
                        }
                    }
                    a--
                }
            }
            'O' -> for (celda2 in celdasPorDistancia(mapa.getCelda(celdaIDObjetivo), mapa, tamaño)) {
                val celda = mapa.getCelda(celda2)
                if (!celdas.contains(celda)) {
                    if (celda != null) {
                        celdas.add(celda)
                    }
                }
            }
            'X' -> {
                var a = tamaño
                while (a >= 0) {
                    for (celda2 in celdasPorCruz(mapa.getCelda(celdaIDObjetivo), mapa, a)) {
                        val celda = mapa.getCelda(celda2)
                        if (!celdas.contains(celda)) {
                            if (celda != null) {
                                celdas.add(celda)
                            }
                        }
                    }
                    a--
                }
            }
            'T' -> {
                val dir2 = direccionEntreDosCeldas(mapa, celdaIDLanzador, celdaIDObjetivo, true)
                for (celda2 in celdasPorLinea(
                    mapa.getCelda(celdaIDObjetivo), mapa, tamaño, correctaDireccion(
                        dir2
                                - 2
                    )
                )) {
                    val celda = mapa.getCelda(celda2)
                    if (!celdas.contains(celda)) {
                        if (celda != null) {
                            celdas.add(celda)
                        }
                    }
                }
                for (celda2 in celdasPorLinea(
                    mapa.getCelda(celdaIDObjetivo), mapa, tamaño, correctaDireccion(
                        dir2
                                + 2
                    )
                )) {
                    val celda = mapa.getCelda(celda2)
                    if (!celdas.contains(celda)) {
                        if (celda != null) {
                            celdas.add(celda)
                        }
                    }
                }
                if (!celdas.contains(mapa.getCelda(celdaIDObjetivo)!!)) {
                    celdas.add(mapa.getCelda(celdaIDObjetivo)!!)
                }
            }
            'L' -> {
                val dir = direccionEntreDosCeldas(mapa, celdaIDLanzador, celdaIDObjetivo, true)
                for (celda2 in celdasPorLinea(
                    mapa.getCelda(celdaIDObjetivo), mapa, tamaño, correctaDireccion(
                        dir
                    )
                )) {
                    val celda = mapa.getCelda(celda2)
                    if (!celdas.contains(celda)) {
                        if (celda != null) {
                            celdas.add(celda)
                        }
                    }
                }
            }
            'P' -> {
                mapa.getCelda(celdaIDObjetivo)
                celdas.add(mapa.getCelda(celdaIDObjetivo)!!)
            }
            else -> redactarLogServidorln("[FIXME]Tipo de alcance no reconocido: " + areaEfecto[0])
        }
        return celdas
    }

    fun celdasPorDistancia(celda: Celda?, mapa: Mapa?, distancia: Int): ArrayList<Short> {
        val celdas = ArrayList<Short>()
        val x = celda!!.coordX
        val y = celda.coordY
        val f =
            arrayOf(byteArrayOf(1, 1), byteArrayOf(1, -1), byteArrayOf(-1, 1), byteArrayOf(-1, -1))
        for (x2 in 0..distancia) {
            val y2 = distancia - x2
            for (b in f) {
                val cell =
                    mapa!!.getCeldaPorPos((x + b[0] * x2).toByte(), (y + b[1] * y2).toByte())
                if (cell != null) {
                    if (!celdas.contains(cell.id)) celdas.add(cell.id)
                }
            }
        }
        return celdas
    }

    private fun celdasPorCruz(celda: Celda?, mapa: Mapa, distancia: Int): ArrayList<Short> {
        val celdas = ArrayList<Short>()
        val x = celda!!.coordX
        val y = celda.coordY
        for (b in COORD_ALREDEDOR) {
            val cell = mapa.getCeldaPorPos(
                (x + b[0] * distancia).toByte(),
                (y + b[1] * distancia).toByte()
            )
            if (cell != null) {
                if (!celdas.contains(cell.id)) celdas.add(cell.id)
            }
        }
        return celdas
    }

    private fun celdasPorLinea(celda: Celda?, mapa: Mapa, distancia: Int, dir: Int): ArrayList<Short> {
        val celdas = ArrayList<Short>()
        if (dir == -1) {
            return celdas
        }
        val x = celda!!.coordX
        val y = celda.coordY
        val b = getCoordPorDireccion(dir)
        for (x2 in distancia downTo 0) {
            val cell =
                mapa.getCeldaPorPos((x + b[0] * x2).toByte(), (y + b[1] * x2).toByte())
            if (cell != null) {
                if (!celdas.contains(cell.id)) celdas.add(cell.id)
            }
        }
        return celdas
    }

    fun celdasPosibleLanzamiento(
        SH: StatHechizo, lanzador: Luchador,
        mapa: Mapa, tempCeldaIDLanzador: Short, celdaObjetivo: Short
    ): ArrayList<Celda> {
        val celdasF = ArrayList<Celda>()
        val perso = lanzador.personaje
        var maxAlc = SH.maxAlc.toInt()
        val minAlc = SH.minAlc.toInt()
        var alcModificable = SH.esAlcanceModificable()
        var lineaVista = SH.esLineaVista()
        var lanzarLinea = SH.esLanzarLinea()
        val necesitaCeldaLibre = SH.esNecesarioCeldaLibre()
        val necesitaObjetivo = SH.esNecesarioObjetivo()
        val hechizoID = SH.hechizoID
        if (perso != null && perso.tieneModfiSetClase(hechizoID)) {
            maxAlc += perso.getModifSetClase(hechizoID, 281)
            alcModificable = alcModificable or (perso.getModifSetClase(hechizoID, 282) == 1)
            lanzarLinea = lanzarLinea and (perso.getModifSetClase(hechizoID, 288) != 1)
            lineaVista = lineaVista and (perso.getModifSetClase(hechizoID, 289) != 1)
        }
        if (alcModificable) {
            maxAlc += lanzador.totalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_ALCANCE)
        }
        if (maxAlc < minAlc) {
            maxAlc = minAlc
        }
        var celdaI = mapa.getCelda(tempCeldaIDLanzador)
        if (celdaI == null) {
            celdaI = lanzador.celdaPelea
        }
        val suponiendo = lanzador.celdaPelea!!.id != celdaI!!.id
        for (celdaC in mapa.celdas.values) {
            if (celdaC == null) {
                continue
            }
            val dist = distanciaDosCeldas(mapa, celdaI.id, celdaC.id).toInt()
            if (dist < minAlc || dist > maxAlc) {
                if (celdaObjetivo == celdaC.id) {
                    if (perso != null) {
                        ENVIAR_Im_INFORMACION(perso, "1171;$minAlc~$maxAlc~$dist")
                    }
                    if (AtlantaMain.MODO_DEBUG) {
                        println("El hechizo " + SH.hechizo!!.nombre + " esta fuera del rango")
                    }
                }
                continue
            }
            if (lanzarLinea) {
                if (celdaI.coordX != celdaC.coordX && celdaI.coordY != celdaC.coordY) {
                    if (celdaObjetivo == celdaC.id) {
                        if (perso != null) {
                            ENVIAR_Im_INFORMACION(perso, "1173")
                        }
                        if (AtlantaMain.MODO_DEBUG) {
                            println("El hechizo " + SH.hechizo!!.nombre + " necesita lanzarse en linea recta")
                        }
                    }
                    continue
                }
            }
            if (necesitaCeldaLibre) {
                if (celdaC.movimiento > 1 && celdaC.primerLuchador != null) {
                    if (celdaObjetivo == celdaC.id) {
                        if (perso != null) {
                            ENVIAR_Im_INFORMACION(perso, "1172")
                        }
                        if (AtlantaMain.MODO_DEBUG) {
                            println("El hechizo " + SH.hechizo!!.nombre + " necesita celda libre")
                        }
                    }
                    continue
                }
                if (celdaC.movimiento <= 1) {
                    continue
                }
            }
            if (necesitaObjetivo) {
                if (celdaC.primerLuchador == null) {
                    if (celdaObjetivo == celdaC.id) {
                        if (perso != null) {
                            ENVIAR_Im_INFORMACION(perso, "1172")
                        }
                        if (AtlantaMain.MODO_DEBUG) {
                            println("El hechizo " + SH.hechizo!!.nombre + " necesita un objetivo")
                        }
                    }
                    continue
                }
            }
            if (lineaVista) {
                if (!lineaDeVista1(mapa, celdaI, celdaC, lanzador, suponiendo, celdaObjetivo)) {
                    if (celdaObjetivo == celdaC.id) {
                        if (perso != null) {
                            ENVIAR_Im_INFORMACION(perso, "1174")
                        }
                        if (AtlantaMain.MODO_DEBUG) {
                            println("El hechizo " + SH.hechizo!!.nombre + " tiene linea de vista")
                        }
                    }
                    continue
                }
            }
            celdasF.add(celdaC)
        }
        // for (Celda c : celdasF) {
// GestorSalida.enviar(perso, "GDZ|+" + c.getID() + ";0;3");
// }
        return celdasF
    }

    private fun celdaPorCoordenadas(mapa: Mapa, x: Int, y: Int): Celda? {
        return mapa.getCelda((x * mapa.ancho + y * (mapa.ancho - 1)).toShort())
    }

    private fun lineaDeVista1(
        mapa: Mapa, celdaI: Celda?, celdaC: Celda, lanzador: Luchador, suponiendo: Boolean,
        celdaObjetivo: Short
    ): Boolean {
        val _loc9: Float = if (celdaI!!.tieneSprite(lanzador.id, suponiendo)) 1.5f else 0f
        val _loc10: Float = if (celdaC.tieneSprite(lanzador.id, suponiendo)) 1.5f else 0f
        val zI = celdaI.alto + _loc9
        val zC = celdaC.alto + _loc10
        val _loc11 = zC - zI
        val _loc12 = Math.max(
            Math.abs(celdaI.coordY - celdaC.coordY), Math.abs(
                celdaI.coordX - celdaC
                    .coordX
            )
        ).toFloat()
        val _loc13 = (celdaI.coordY - celdaC.coordY).toFloat() / (celdaI.coordX - celdaC
            .coordX).toFloat()
        val isNaN = java.lang.Float.isInfinite(_loc13) || java.lang.Float.isNaN(_loc13)
        val _loc14 = celdaI.coordY - _loc13 * celdaI.coordX
        val _loc15 = if (celdaC.coordX - celdaI.coordX < 0) (-1).toFloat() else 1.toFloat()
        val _loc16 = if (celdaC.coordY - celdaI.coordY < 0) (-1).toFloat() else 1.toFloat()
        var _loc17 = celdaI.coordY.toInt()
        // int _loc18 = celdaI.getX();
        val _loc19 = celdaC.coordX * _loc15
        // int _loc20 = celdaC.getY() * _loc16;
        var _loc26 = 0f
        var _loc27 = celdaI.coordX + 0.5f * _loc15
        // if (celdaC.getID() == celdaObjetivo) {
// System.out.println("_loc5.x " + celdaI.getX() + " _loc5.y " + celdaI.getY() + " alto5 " +
// celdaI.getAlto()
// + " _loc5.z " + zI + " _loc6.x " + celdaC.getX() + " _loc6.y " + celdaC.getY() + " alto6 " +
// celdaC.getAlto()
// + " _loc6.z " + zC + " _loc9 " + _loc9 + " _loc10 " + _loc10 + " _loc11 " + _loc11 +
// " _loc12 " + _loc12
// + " _loc13 " + _loc13 + " _loc14 " + _loc14 + " _loc15 " + _loc15 + " _loc16 " + _loc16 +
// " _loc17 " + _loc17
// + " _loc19 " + _loc19 + " _loc27 " + _loc27);
// }
        if (!isNaN) {
            while (_loc27 * _loc15 <= _loc19) {
                val _loc25 = _loc13 * _loc27 + _loc14
                var _loc21 = 0
                var _loc22 = 0
                if (_loc16 > 0) {
                    _loc21 = Math.round(_loc25)
                    _loc22 = Math.ceil(_loc25 - 0.5f.toDouble()).toInt()
                } else {
                    _loc21 = Math.ceil(_loc25 - 0.5f.toDouble()).toInt()
                    _loc22 = Math.round(_loc25)
                }
                _loc26 = _loc17.toFloat()
                while (_loc26 * _loc16 <= _loc22 * _loc16) {
                    if (lineaDeVista2(
                            mapa, (_loc27 - _loc15 / 2).toInt(), _loc26.toInt(), false, celdaI, celdaC, zI, zC, _loc11,
                            _loc12, lanzador.id, suponiendo, celdaObjetivo
                        )
                    ) {
                        return false
                    }
                    _loc26 = _loc26 + _loc16
                }
                _loc17 = _loc21
                _loc27 = _loc27 + _loc15
            }
        }
        _loc26 = _loc17.toFloat() // celdaI.getY();
        while (_loc26 * _loc16 <= celdaC.coordY * _loc16) {
            if (lineaDeVista2(
                    mapa, (_loc27 - 0.5f * _loc15).toInt(), _loc26.toInt(), false, celdaI, celdaC, zI, zC, _loc11,
                    _loc12, lanzador.id, suponiendo, celdaObjetivo
                )
            ) { // if (celdaC.getID() == celdaObjetivo) {
// System.out.println("FUE EN LINEA 2 ");
// }
                return false
            }
            _loc26 = _loc26 + _loc16
        }
        // if (celdaC.getID() == celdaObjetivo) {
// System.out.println("FUE EN LINEA 3 ");
// }
        return !lineaDeVista2(
            mapa, (_loc27 - 0.5f * _loc15).toInt(), (_loc26 - _loc16).toInt(), true, celdaI, celdaC, zI, zC,
            _loc11, _loc12, lanzador.id, suponiendo, celdaObjetivo
        )
    }

    private fun lineaDeVista2(
        mapa: Mapa,
        x: Int,
        y: Int,
        bool: Boolean,
        celdaI: Celda?,
        celdaC: Celda,
        zI: Float,
        zC: Float,
        zDiff: Float,
        d: Float,
        idLanzador: Int,
        suponiendo: Boolean,
        celdaObjetivo: Short
    ): Boolean {
        val _loc11 = celdaPorCoordenadas(mapa, x, y)
        val _loc12 =
            Math.max(Math.abs(celdaI!!.coordY - y), Math.abs(celdaI.coordX - x)).toFloat()
        val _loc13 = _loc12 / d * zDiff + zI
        val _loc14 = _loc11!!.alto
        val _loc15 = _loc11.tieneSprite(
            idLanzador,
            suponiendo
        ) && _loc12 != 0f && !bool && (celdaC.coordX.toInt() != x || celdaC.coordY.toInt() != y)
        // if (celdaObjetivo == _loc11.getID()) {
// System.out.println(" _loc11.lineaDeVista " + _loc11.lineaDeVista() + " _loc12 " + _loc12 +
// " _loc13 " + _loc13
// + " _loc14 " + _loc14 + " _loc15 " + _loc15 + " _loc14 <= _loc13 " + (_loc14 <= _loc13)
// + " (_loc14 <= _loc13 && !_loc15) " + (_loc14 <= _loc13 && !_loc15));
// }
// NaN en java con condicional siempre es FALSE, en AS2 es true con >= <= y FALSE con ==
        return if (_loc11.lineaDeVista() && (java.lang.Float.isNaN(_loc13) || _loc14 <= _loc13) && !_loc15) {
            false
        } else {
            !bool
        }
    }

    private fun celdaMasCercanaACeldaObjetivo(
        mapa: Mapa?, celdaInicio: Short, celdaDestino: Short,
        celdasProhibidas: ArrayList<Celda?>?
    ): Short {
        var celdasProhibidas = celdasProhibidas
        if (mapa!!.getCelda(celdaInicio) == null || mapa.getCelda(celdaDestino) == null) {
            return -1
        }
        var distancia = 1000
        var celdaID = celdaInicio
        if (celdasProhibidas == null) {
            celdasProhibidas = ArrayList()
        }
        val dirs = listaDirEntreDosCeldas(mapa, celdaInicio, celdaDestino)
        for (d in dirs) {
            val sigCelda = getSigIDCeldaMismaDir(celdaInicio, d.toInt(), mapa, true)
            val celda = mapa.getCelda(sigCelda) ?: continue
            val tempDistancia = distanciaDosCeldas(mapa, celdaDestino, sigCelda).toInt()
            if (tempDistancia < distancia && celda.esCaminable(true) && (!false || celda.primerLuchador == null)
                && !celdasProhibidas.contains(celda)
            ) {
                distancia = tempDistancia
                celdaID = sigCelda
            }
        }
        return if (celdaID == celdaInicio) -1 else celdaID
    }

    fun celdasDeMovimiento(
        pelea: Pelea, celdaInicio: Celda, filtro: Boolean,
        ocupadas: Boolean, tacleado: Luchador?
    ): ArrayList<Short> {
        val celdas = ArrayList<Short>()
        if (pelea.PMLuchadorTurno <= 0) {
            return celdas
        }
        val mapa = pelea.mapaCopia
        for (a in 0..pelea.PMLuchadorTurno) {
            for (tempCeldaID in celdasPorDistancia(celdaInicio, mapa, a)) {
                val tempCelda = mapa!!.getCelda(tempCeldaID)
                if (!tempCelda!!.esCaminable(true)) {
                    continue
                }
                if (ocupadas && tempCelda.primerLuchador != null) {
                    continue
                }
                if (!celdas.contains(tempCeldaID)) {
                    if (filtro) {
                        val pathTemp = getPathPelea(
                            mapa, celdaInicio.id, tempCeldaID, pelea
                                .PMLuchadorTurno, tacleado, false
                        )
                            ?: continue
                        if (pathTemp._segundo.isEmpty()) {
                            continue
                        }
                        if (pathTemp._segundo[pathTemp._segundo.size - 1]!!.id != tempCeldaID) {
                            continue
                        }
                    }
                    celdas.add(tempCeldaID)
                }
            }
        }
        return celdas
    }

    fun celdaMoverSprite(mapa: Mapa, celda: Short): Short {
        val celdasPosibles = ArrayList<Short>()
        val ancho = mapa.ancho.toShort()
        val dir = shortArrayOf((-ancho).toShort(), (-(ancho - 1)).toShort(), (ancho - 1).toShort(), ancho)
        for (element in dir) {
            try {
                if (celda + element > 14 || celda + element < 464) {
                    if (mapa.getCelda((celda + element).toShort())!!.esCaminable(false)) {
                        celdasPosibles.add((celda + element).toShort())
                    }
                }
            } catch (ignored: Exception) {
            }
        }
        return if (celdasPosibles.size <= 0) {
            -1
        } else celdasPosibles[getRandomInt(0, celdasPosibles.size - 1)]
    }

    @JvmStatic
    fun getCeldaIDCercanaLibre(celda: Celda, mapa: Mapa): Short {
        for (c in COORD_ALREDEDOR) {
            val cell =
                mapa.getCeldaPorPos((celda.coordX + c[0]).toByte(), (celda.coordY + c[1]).toByte())
            if (cell != null && cell.objetoTirado == null && cell.primerPersonaje == null && cell.esCaminable(
                    false
                )
            ) {
                return cell.id
            }
        }
        return 0
    }

    fun ultimaCeldaID(mapa: Mapa): Short {
        return (mapa.ancho * mapa.alto * 2 - (mapa.alto + mapa.ancho)).toShort()
    }

    fun esCeldaLadoIzq(ancho: Byte, alto: Byte, celda: Short): Boolean {
        var ladoIzq = ancho.toShort()
        for (i in 0 until alto) {
            if (celda == ladoIzq || celda.toInt() == ladoIzq - ancho) {
                return true
            }
            ladoIzq = (ladoIzq + (ancho * 2 - 1)).toShort()
        }
        return false
    }

    fun esCeldaLadoDer(ancho: Byte, alto: Byte, celda: Short): Boolean {
        var ladoDer = (2 * (ancho - 1)).toShort()
        for (i in 0 until alto) {
            if (celda == ladoDer || celda.toInt() == ladoDer - ancho + 1) {
                return true
            }
            ladoDer = (ladoDer + (ancho * 2 - 1)).toShort()
        }
        return false
    }

    fun celdaSalienteLateral(ancho: Byte, alto: Byte, celda1: Short, celda2: Short): Boolean {
        return if (esCeldaLadoIzq(
                ancho,
                alto,
                celda1
            ) && (celda2.toInt() == celda1 + ancho - 1 || celda2.toInt() == celda1 - ancho)
        ) {
            true
        } else esCeldaLadoDer(
            ancho,
            alto,
            celda1
        ) && (celda2.toInt() == celda1 - ancho + 1 || celda2.toInt() == celda1 + ancho)
    }

    private class CeldaCamino {
        var id: Short = 0
        var valorX: Short = 0
        var cantPM: Short = 0
        var direccion: Short = 0
        var movimiento: Short = 0
        var level: Short = 0
        var distEstimadaX = 0
        var distEstimada = 0
        var anterior: CeldaCamino? = null
    }
}