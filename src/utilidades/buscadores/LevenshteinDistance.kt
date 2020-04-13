package utilidades.buscadores

class LevenshteinDistance {
    var str1: String? = null
    var str2: String? = null
    var distancia = 0
    lateinit var matriz: Array<IntArray>
    fun setWords(str1: String, str2: String) {
        this.str1 = str1.toLowerCase()
        this.str2 = str2.toLowerCase()
        calculoLevenshtein()
    }

    val afinidad: Float
        get() {
            val longitud = if (str1!!.length > str2!!.length) str1!!.length.toFloat() else str2!!.length.toFloat()
            return 1 - distancia / longitud
        }

    fun calculoLevenshtein() {
        matriz = Array(str1!!.length + 1) { IntArray(str2!!.length + 1) }
        for (i in 0..str1!!.length) {
            matriz[i][0] = i
        }
        for (j in 0..str2!!.length) {
            matriz[0][j] = j
        }
        for (i in 1 until matriz.size) {
            for (j in 1 until matriz[i].size) {
                if (str1!![i - 1] == str2!![j - 1]) {
                    matriz[i][j] = matriz[i - 1][j - 1]
                } else {
                    var min = Int.MAX_VALUE
                    if (matriz[i - 1][j] + 1 < min) {
                        min = matriz[i - 1][j] + 1
                    }
                    if (matriz[i][j - 1] + 1 < min) {
                        min = matriz[i][j - 1] + 1
                    }
                    if (matriz[i - 1][j - 1] + 1 < min) {
                        min = matriz[i - 1][j - 1] + 1
                    }
                    matriz[i][j] = min
                }
            }
        }
        distancia = matriz[str1!!.length][str2!!.length]
    }
}