package variables.zotros

import java.util.*

class Tutorial(val id: Int, recompensatext: String, inicio: String, fin: String) {
    val recompensa =
        ArrayList<Accion?>(4)
    var inicio: Accion? = null
    var fin: Accion? = null

    init {
        try {
            for (str in recompensatext.split("\\$".toRegex()).toTypedArray()) {
                if (str.isEmpty()) {
                    recompensa.add(null)
                } else {
                    val a = str.split("@".toRegex()).toTypedArray()
                    if (a.size >= 2) {
                        recompensa.add(Accion(a[0].toInt(), a[1], ""))
                    } else {
                        recompensa.add(Accion(a[0].toInt(), "", ""))
                    }
                }
            }
            if (inicio.isEmpty()) {
                this.inicio = null
            } else {
                val b = inicio.split("@".toRegex()).toTypedArray()
                if (b.size >= 2) {
                    this.inicio = Accion(b[0].toInt(), b[1], "")
                } else {
                    this.inicio = Accion(b[0].toInt(), "", "")
                }
            }
            if (fin.isEmpty()) {
                this.fin = null
            } else {
                val c = fin.split("@".toRegex()).toTypedArray()
                if (c.size >= 2) {
                    this.fin = Accion(c[0].toInt(), c[1], "")
                } else {
                    this.fin = Accion(c[0].toInt(), "", "")
                }
            }
        } catch (e: Exception) {
            println("Ocurrio un error al cargar el tutorial $id")
            System.exit(1)
        }
    }
}