package variables.ranking

class RankingKoliseo(val id: Int, var nombre: String, victorias: Int, derrotas: Int) {
    var victorias = 0
        private set
    var derrotas = 0
        private set

    fun aumentarVictoria() {
        victorias += 1
    }

    fun aumentarDerrota() {
        derrotas += 1
    }

    init {
        this.victorias = victorias
        this.derrotas = derrotas
    }
}