package variables.mapa.interactivo

import java.util.*

class ObjetoInteractivoModelo(
    val id: Int, val tiempoRecarga: Int, val duracion: Int, private val _animacionnPJ: Byte,
    caminable: Byte, // 1 recursos para recoger
    val tipo: Byte, gfx: String, skill: String
) {
    val gfxs = ArrayList<Int>()
    private val skills = ArrayList<Int>()

    val animacionPJ: Int
        get() = _animacionnPJ.toInt()

    // @SuppressWarnings("unused")
// private boolean acercarse() {
// return (_caminable & 1) == 1;
// }
//
// @SuppressWarnings("unused")
// private boolean esCaminable() {
// return (_caminable & 2) == 2;
// }
    fun tieneSkill(skillID: Int): Boolean {
        return skills.contains(skillID)
    }

    init {
        for (str in gfx.split(",".toRegex()).toTypedArray()) {
            if (str.isEmpty()) {
                continue
            }
            try {
                gfxs.add(str.toInt())
            } catch (ignored: Exception) {
            }
        }
        for (str in skill.split(",".toRegex()).toTypedArray()) {
            if (str.isEmpty()) {
                continue
            }
            try {
                skills.add(str.toInt())
            } catch (ignored: Exception) {
            }
        }
    }
}