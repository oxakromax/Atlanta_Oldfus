package variables.pelea

class Botin(val drop: DropMob) {
    var botinMaximo: Int
        private set

    fun addBotinMaximo(cant: Int) {
        botinMaximo += cant
    }

    val idObjModelo: Int
        get() = drop.IDObjModelo

    val prospeccionBotin: Int
        get() = drop.prospeccion

    val porcentajeBotin: Float
        get() = drop.porcentaje

    val condicionBotin: String
        get() = drop.condicion

    fun esDropFijo(): Boolean {
        return drop.esDropFijo()
    }

    init {
        botinMaximo = drop.maximo
    }
}