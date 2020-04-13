package variables.mapa

import java.util.*

class SuperArea(val id: Int) {
    private val _areas = ArrayList<Area>()
    fun addArea(area: Area) {
        _areas.add(area)
    }

}