package variables.personaje

class MisionPVP(// public Personaje getPjMision() {
// return _victimaPVP;
// }
    val tiempoInicio: Long, // private Personaje _victimaPVP;
    val nombreVictima: String, val kamasRecompensa: Long, val expMision: Long, craneo: Int, pergRec: Int
) {
    private val _cazaCabezas: Boolean
    //
    val craneo: Int
    val pergRec: Int

    fun esCazaCabezas(): Boolean {
        return _cazaCabezas
    }

    init {
        this.pergRec = if (expMision <= 0) 0 else pergRec
        this.craneo = if (expMision <= 0) 0 else craneo
        _cazaCabezas = true
    }
}