package utilidades.algoritmos

import java.util.*

object RandomCondicionado {
    fun getRandA(max: Int, num: Int, ratio: Float): Int {
        if (max == num) {
            return max
        }
        var r = Random()
        var rand = r.nextInt(max) + 1
        val x: Float = max * ratio
        return if (rand <= x) {
            num
        } else {
            do {
                rand = r.nextInt(max) + 1
            } while (rand == num)
            rand
        }
    }
}