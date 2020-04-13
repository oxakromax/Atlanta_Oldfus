package variables.ranking

class RankingPVP(
    val id: Int, var nombre: String, victorias: Int, derrotas: Int,
    gradoAlineacion: Int
) {
    var victorias = 0
        private set
    var derrotas = 0
        private set
    var gradoAlineacion = 1

    fun aumentarVictoria() {
        victorias += 1
    }

    fun aumentarDerrota() {
        derrotas += 1
    }

    init {
        this.victorias = victorias
        this.derrotas = derrotas
        this.gradoAlineacion = gradoAlineacion
    }
}