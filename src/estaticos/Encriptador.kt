package estaticos

import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Formulas.getRandomInt
import variables.mapa.Celda
import variables.mapa.Mapa
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.regex.Pattern
import kotlin.experimental.and
import kotlin.math.floor
import kotlin.math.pow

object Encriptador {
    const val ABC_MIN = "abcdefghijklmnopqrstuvwxyz"
    const val ABC_MAY = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    const val VOCALES = "aeiouAEIOU"
    const val CONSONANTES = "bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ"
    const val NUMEROS = "0123456789"
    const val ESPACIO = " "
    const val GUIONES = "_-"
    private val HASH = charArrayOf(
        'a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j',
        'k',
        'l',
        'm',
        'n',
        'o',
        'p',  // 15
        'q',
        'r',
        's',
        't',
        'u',
        'v',
        'w',
        'x',
        'y',
        'z',
        'A',
        'B',
        'C',
        'D',
        'E',
        'F',
        'G',
        'H',
        'I',
        'J',
        'K',
        'L',
        'M',  // 38
        'N',
        'O',
        'P',
        'Q',
        'R',
        'S',
        'T',
        'U',
        'V',
        'W',
        'X',
        'Y',
        'Z',
        '0',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',  // 61
        '-',
        '_'
    ) // q = 16, N = 40, - = 63 _ = 64
    private val HEX_CHARS =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

    fun crearKey(limite: Int): String {
        val nombre = StringBuilder()
        while (nombre.length < limite) {
            nombre.append(HASH[getRandomInt(0, HASH.size - 1)])
        }
        val key = StringBuilder()
        for (c in nombre.toString().toCharArray()) {
            key.append(Integer.toHexString(c.toInt()))
        }
        return key.toString()
    }

    fun palabraAleatorio(limite: Int): String {
        val nombre = StringBuilder()
        var i = Math.floor(Math.random() * ABC_MAY.length).toInt()
        var temp = ABC_MAY[i]
        nombre.append(temp)
        var xxx: Char
        while (nombre.length < limite) {
            i = floor(Math.random() * ABC_MIN.length).toInt()
            xxx = ABC_MIN[i]
            if (temp == xxx || VOCALES.contains(temp.toString() + "") && VOCALES.contains(xxx.toString() + "") || (CONSONANTES.contains(
                    temp.toString() + ""
                )
                        && CONSONANTES.contains(xxx.toString() + ""))
            ) {
                continue
            }
            temp = xxx
            nombre.append(xxx)
        }
        return nombre.toString()
    }

    fun stringParaGDC(
        permisos: BooleanArray,
        valores: IntArray
    ): String { // 16 var layerObjectExternalAutoSize = (_loc6 & 65536) != 0;
// 15 var layerObjectExternalInteractive = (_loc6 & 32768) != 0;
// 14 var layerObjectExternal = (_loc6 & 16384) != 0;
// 13 var active = (_loc6 & 8192) != 0;
// 12 var lineOfSight = (_loc6 & 4096) != 0;
// 11 var movement = (_loc6 & 2048) != 0;
// 10 var groundLevel = (_loc6 & 1024) != 0;
// 9 var groundSlope = (_loc6 & 512) != 0;
// 8 var layerGroundNum = (_loc6 & 256) != 0;
// 7 var layerGroundFlip = (_loc6 & 128) != 0;
// 6 var layerGroundRot = (_loc6 & 64) != 0;
// 5 var layerObject1Num = (_loc6 & 32) != 0;
// 4 var layerObject1Flip = (_loc6 & 16) != 0;
// 3 var layerObject1Rot = (_loc6 & 8) != 0;
// 2 var layerObject2Num = (_loc6 & 4) != 0;
// 1 var layerObject2Flip = (_loc6 & 2) != 0;
// 0 var layerObject2Interactive = (_loc6 & 1) != 0; << 0
        var finalPermiso = 0
        for ((i, b) in permisos.withIndex()) {
            if (b) {
                finalPermiso += 1 shl i
            }
        }
        val fP = Integer.toHexString(finalPermiso)
        val preData = IntArray(10)
        preData[0] = (if (valores[13] == 1) 1 else 0) shl 5
        preData[0] = preData[0] or if (valores[12] == 1) 1 else 0
        preData[0] = preData[0] or (valores[8] and 1536) shr 6
        preData[0] = preData[0] or (valores[5] and 8192) shr 11
        preData[0] = preData[0] or (valores[2] and 8192) shr 12
        preData[1] = valores[3] and 3 shl 4
        preData[1] = preData[1] or valores[10] and 15
        preData[2] = valores[11] and 7 shl 3
        preData[2] = preData[2] or valores[8] shr 6 and 7
        preData[3] = valores[8] and 63
        preData[4] = valores[9] and 15 shl 2
        preData[4] = preData[4] or (if (valores[7] == 1) 1 else 0) shl 1
        preData[4] = preData[4] or valores[5] shr 12 and 1
        preData[5] = valores[5] shr 6 and 63
        preData[6] = valores[5] and 63
        preData[7] = valores[3] and 3 shl 4
        preData[7] = preData[7] or (if (valores[4] == 1) 1 else 0) shl 3
        preData[7] = preData[7] or (if (valores[1] == 1) 1 else 0) shl 2
        preData[7] = preData[7] or (if (valores[0] == 1) 1 else 0) shl 1
        preData[7] = preData[7] or valores[2] shr 12 and 1
        preData[8] = valores[2] shr 6 and 63
        preData[9] = valores[2] and 63
        val fD = StringBuilder()
        for (d in preData) {
            fD.append(getValorHashPorNumero(d))
        }
        return fD.toString() + fP
    }

    fun encriptarIP(IP: String): String {
        val split = IP.split(Pattern.quote(".").toRegex()).toTypedArray()
        val encriptado = StringBuilder()
        var cantidad = 0
        var i = 0
        while (i < 50) {
            var o = 0
            while (o < 50) {
                if (i and 15 shl 4 or o and 15 == split[cantidad].toInt()) {
                    val A = (i + 48).toChar()
                    val B = (o + 48).toChar()
                    encriptado.append(A).append(B)
                    i = 0
                    o = 0
                    cantidad++
                    if (cantidad == 4) {
                        return encriptado.toString()
                    }
                }
                o++
            }
            i++
        }
        return "DD"
    }

    fun encriptarPuerto(puerto: Int): String {
        var P = puerto
        val numero = StringBuilder()
        for (a in 2 downTo 0) {
            numero.append(HASH[(P / 64.0.pow(a.toDouble())).toInt()])
            P %= 64.0.pow(a.toDouble()).toInt()
        }
        return numero.toString()
    }

    @JvmStatic
    fun celdaIDAHash(celdaID: Short): String {
        return HASH[celdaID.div(64)].toString() + "" + HASH[celdaID % 64]
    }

    @JvmStatic
    fun hashACeldaID(celdaCodigo: String): Short {
        val char1 = celdaCodigo[0]
        val char2 = celdaCodigo[1]
        var code1: Short = 0
        var code2: Short = 0
        var a: Short = 0
        while (a < HASH.size) {
            if (HASH[a.toInt()] == char1) {
                code1 = (a.times(64)).toShort()
            }
            if (HASH[a.toInt()] == char2) {
                code2 = a
            }
            a++
        }
        return (code1.plus(code2)).toShort()
    }

    @JvmStatic
    fun getNumeroPorValorHash(c: Char): Byte {
        for (a in HASH.indices) {
            if (HASH[a].equals(c)) {
                return a.toByte()
            }
        }
        return -1
    }

    @JvmStatic
    fun getValorHashPorNumero(c1: Int): Char {
        var c = c1
        return try {
            if (c >= HASH.size || c < 0) {
                c = 0
            }
            HASH[c]
        } catch (e: Exception) {
            'a'
        }
    }

    fun analizarCeldasDeInicio(posPelea: String, listaCeldas: ArrayList<Short>) {
        try {
            var a = 0
            while (a < posPelea.length) {
                listaCeldas.add(
                    ((getNumeroPorValorHash(posPelea[a]).toInt().shl(6)).plus(
                        getNumeroPorValorHash(
                            posPelea[a.plus(1)]
                        )
                    )).toShort()
                )
                a = a.plus(2)
            }
        } catch (ignored: Exception) {
        }
    }

    fun decompilarMapaData(mapa: Mapa) {
        try {
            var activo: Boolean
            var lineaDeVista: Boolean
            var tieneObjInteractivo: Boolean
            var caminable: Byte
            var level: Byte
            var slope: Byte
            var objInteractivo: Short
            var f: Short = 0
            while (f < mapa.mapData.length) {
                val celdaData =
                    StringBuilder(mapa.mapData.substring(f.toInt(), f.plus(10)))
                val celdaInfo = ArrayList<Byte>()
                for (element in celdaData) {
                    celdaInfo.add(getNumeroPorValorHash(element))
                }
                activo = celdaInfo[0].and(32).toInt().shr(5) != 0
                lineaDeVista = celdaInfo[0].and(1) != 0.toByte()
                tieneObjInteractivo = celdaInfo[7].and(2).toInt().shr(1) != 0
                caminable = celdaInfo[2].and(56).toInt().shr(3).toByte() // 0 = no, 1 = medio, 4 = si
                level = celdaInfo[1].and(15)
                slope = celdaInfo[4].and(60).toInt().shr(2).toByte()
                objInteractivo =
                    celdaInfo[0].and(2).toInt().shl(12).plus(celdaInfo[7].and(1).toInt().shl(12))
                        .plus(celdaInfo[8].toInt().shl(6)).plus(celdaInfo[9]).toShort()
                val celdaID = (f.div(10)).toShort()
                val celda = Celda(
                    mapa,
                    celdaID,
                    activo,
                    caminable,
                    level,
                    slope,
                    lineaDeVista,
                    if (tieneObjInteractivo) objInteractivo.toInt() else -1
                )
                mapa.celdas[celdaID] = celda
                celda.celdaNornmal()
                if (tieneObjInteractivo && objInteractivo.toInt() != -1) {
                    mapa.trabajos?.let { Constantes.getTrabajosPorOI(objInteractivo.toInt(), it) }
                }
                f = f.plus(10).toShort()
            }
        } catch (e: Exception) {
            redactarLogServidorln(
                "El mapa ID " + mapa.id + " esta errado, con mapData lenght " + mapa
                    .mapData.length
            )
            e.printStackTrace()
        }
    }

    fun decifrarMapData(key2: String, preData: String): String {
        var key = key2
        var data = preData
        try {
            key = prepareKey(key)
            data = decypherData(preData, key, checksum(key).toString() + "")
        } catch (ignored: Exception) {
        }
        return data
    }

    fun unprepareData(s: String, currentKey: Int, aKeys: Array<String?>): String {
        return try {
            if (currentKey < 1) {
                return s
            }
            val _loc3 = aKeys[s.substring(0, 1).toInt(16)] ?: return s
            val _loc4 = s.substring(1, 2).toUpperCase()
            val _loc5 = decypherData(s.substring(2), _loc3, _loc4)
            if (checksum(_loc5) != _loc4[0]) {
                s
            } else _loc5
        } catch (e: Exception) {
            s
        }
    }

    fun prepareData(s: String, currentKey: Int, aKeys: Array<String?>): String {
        if (currentKey < 1) {
            return s
        }
        if (aKeys[currentKey] == null) {
            return s
        }
        val _loc3 = HEX_CHARS[currentKey]
        val _loc4 = checksum(s)
        return _loc3.toString() + "" + _loc4 + "" + cypherData(
            s,
            aKeys[currentKey],
            (_loc4.toString() + "").toInt(16).times(2)
        )
    }

    private fun cypherData(d1: String, k: String?, c: Int): String {
        var d = d1
        val _loc5 = StringBuilder()
        val _loc6 = k!!.length
        d = preEscape(d)
        for (_loc7 in d.indices) {
            _loc5.append(d2h(d[_loc7].toInt().xor(k[(_loc7.plus(c)) % _loc6].toInt())))
        }
        return _loc5.toString()
    }

    private fun decypherData(d: String, k: String, checksum: String): String {
        val c = checksum.toInt(16).times(2)
        val _loc5 = StringBuilder()
        val _loc6 = k.length
        var _loc7 = 0
        var _loc9 = 0
        while (_loc9 < d.length) {
            _loc5.append(
                (d.substring(
                    _loc9,
                    _loc9.plus(2)
                ).toInt(16).xor(k.codePointAt((_loc7.plus(c)) % _loc6))).toChar()
            )
            _loc7++
            _loc9 = _loc9.plus(2)
        }
        return StringBuilder(unescape(_loc5.toString())).toString()
    }

    private fun d2h(d1: Int): String {
        var d = d1
        if (d > 255) {
            d = 255
        }
        return HEX_CHARS[floor(d.toDouble().div(16)).toInt()].toString() + "" + HEX_CHARS[d % 16]
    }

    private fun unescape(s1: String): String {
        var s = s1
        try {
            s = URLDecoder.decode(s, StandardCharsets.UTF_8.toString())
        } catch (ignored: Exception) {
        }
        return s
    }

    // oscila del 32 al 127, todos los contenidos de k 95
    private fun escape(s1: String): String {
        var s = s1
        try {
            s = URLEncoder.encode(s, StandardCharsets.UTF_8.toString())
        } catch (ignored: Exception) {
        }
        return s
    }

    private fun preEscape(s: String): String {
        val _loc3 = StringBuilder()
        for (element in s) {
            val _loc5 = element
            if (_loc5.toInt() < 32 || _loc5.toInt() > 127 || _loc5 == '%' || _loc5 == '+') {
                _loc3.append(escape(_loc5.toString() + ""))
                continue
            }
            _loc3.append(_loc5)
        }
        return _loc3.toString()
    }

    fun prepareKey(d: String): String {
        val _loc3 = StringBuilder("")
        var _loc4 = 0
        while (_loc4 < d.length) {
            _loc3.append(d.substring(_loc4, _loc4.plus(2)).toInt(16).toChar())
            _loc4 += 2
        }
        return StringBuilder(unescape(_loc3.toString())).toString()
    }

    private fun checksum(s: String): Char {
        var _loc3 = 0
        var _loc4 = 0
        while (_loc4 < s.length) {
            _loc3 = _loc3.plus(s.codePointAt(_loc4).div(16))
            _loc4++
        }
        return HEX_CHARS[_loc3.div(16)]
    }

    @JvmStatic
    @Throws(Exception::class)
    fun consultaWeb(url: String?) {
        val obj = URL(url)
        val con = obj.openConnection()
        con.setRequestProperty("Content-type", "charset=Unicode")
        val `in` = BufferedReader(InputStreamReader(con.getInputStream()))
        while (`in`.readLine() != null) {
            Thread.sleep(5)
        }
        `in`.close()
    }

    fun aUTF(entrada: String): String {
        var out = ""
        try {
            out = String(entrada.toByteArray(StandardCharsets.UTF_8))
        } catch (e: Exception) {
            println("Conversion en UTF-8 fallida! : $e")
        }
        return out
    }

    fun aUnicode(entrada: String): String {
        var out = ""
        try {
            out = String(entrada.toByteArray(), StandardCharsets.UTF_8)
        } catch (e: Exception) {
            println("Conversion en UNICODE fallida! : $e")
        }
        return out
    }
}
