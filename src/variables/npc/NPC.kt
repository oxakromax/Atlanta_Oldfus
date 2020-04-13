package variables.npc

import sprites.Exchanger
import variables.objeto.Objeto
import variables.personaje.Personaje

class NPC(val modelo: NPCModelo?, val id: Int, var celdaID: Short, var orientacion: Byte) : Exchanger {

    val modeloID: Int
        get() = modelo!!.id

    fun getPreguntaID(perso: Personaje?): Int {
        return if (modelo == null) {
            0
        } else modelo.getPreguntaID(perso)
    }

    fun strinGM(perso: Personaje?): String {
        val str = StringBuilder()
        str.append(celdaID.toInt()).append(";")
        str.append(orientacion.toInt()).append(";")
        str.append("0" + ";")
        str.append(id).append(";")
        if (modelo != null) {
            str.append(modelo.id).append(";")
        }
        str.append("-4" + ";") // tipo = NPC
        if (modelo != null) {
            str.append(modelo.GfxID).append("^").append(modelo.TallaX.toInt()).append("x")
                .append(modelo.TallaY.toInt()).append(";")
        }
        if (modelo != null) {
            str.append(modelo.Sexo.toInt()).append(";")
        }
        if (modelo != null) {
            str.append(if (modelo.Color1 != -1) Integer.toHexString(modelo.Color1) else "-1")
                .append(";")
        }
        if (modelo != null) {
            str.append(if (modelo.Color2 != -1) Integer.toHexString(modelo.Color2) else "-1")
                .append(";")
        }
        if (modelo != null) {
            str.append(if (modelo.Color3 != -1) Integer.toHexString(modelo.Color3) else "-1")
                .append(";")
        }
        if (modelo != null) {
            str.append(modelo.accesoriosHex).append(";")
        }
        str.append(modelo!!.getExtraClip(perso)).append(";")
        str.append(modelo.foto)
        return str.toString()
    }

    override fun addKamas(k: Long, perso: Personaje?) {}
    override val kamas: Long
        get() = 0L

    @Synchronized
    override fun addObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
    }

    @Synchronized
    override fun remObjetoExchanger(objeto: Objeto, cantidad: Int, perso: Personaje, precio: Int) {
    }

    override fun cerrar(perso: Personaje?, exito: String) {
        perso?.cerrarVentanaExchange(exito)
    }

    override fun botonOK(perso: Personaje) {}
    override fun getListaExchanger(perso: Personaje): String {
        return modelo!!.listaObjetosAVender()
    }

}