package variables.gremio

import estaticos.AtlantaMain
import estaticos.Constantes
import estaticos.Mundo
import variables.mapa.Mapa
import variables.pelea.Pelea

object GestorGuerras {
    val GremiosEnGuerra = arrayListOf<Guerra>()

    //    val Peleas = arrayListOf<Short>()
    fun Vigilante() {
        try {
            val map: Mapa = Mundo.getMapa(AtlantaMain.MAPA_GUERRA.toShort()) ?: return
            val Pelea = arrayListOf<Guerra>()
            val guerrasAremover = arrayListOf<Guerra>()
            if (GremiosEnGuerra.size % 2 == 0) {
                try {
                    for (guerra in GremiosEnGuerra) {
                        if (guerra.EnPelea || guerra.pelea != null) {
                            if (guerra.pelea?.acaboPelea(3) == true) guerrasAremover.add(guerra)
                            continue
                        }
                        guerra.filtrarIntegrantes()
                        if (!GremiosEnGuerra.contains(guerra)) continue
                        if (Pelea.size == 2) break
                        Pelea.add(guerra)
                    }
                } catch (e: Exception) {
                }
                if (Pelea.size == 2) {
                    val gremio1 = Pelea[0]
                    val gremio2 = Pelea[1]
                    val idPelea = map.sigIDPelea()
                    val ptemp = Pelea(
                        idPelea,
                        map,
                        gremio1.recaudadores[0],
                        gremio2.recaudadores[0],
                        map.randomCeldaIDLibre,
                        map.randomCeldaIDLibre,
                        Constantes.PELEA_TIPO_RECAUDADOR,
                        null,
                        map.strCeldasPeleaPosAtacante()
                    )
                    for (guerra in Pelea) {
                        guerra.EnPelea = true
                        guerra.pelea = ptemp
                        for (recaudador in guerra.recaudadores) {
                            if (recaudador.pelea != null) continue else {
                                ptemp.unirsePelea(recaudador, guerra.recaudadores[0].id)
                            }
                        }
                        for (personaje in guerra.Integrantes) {
                            personaje.MapaAnteriorPVP = personaje.mapa
                            ptemp.unirsePelea(personaje, guerra.recaudadores[0].id)
                        }
                    }
                }
                for (guerra in guerrasAremover) {
                    GremiosEnGuerra.remove(guerra)
                    guerra.finalizarGuerra()
                }
            }
        } catch (e: Exception) {
            AtlantaMain.redactarLogServidorln("Error en el vigilante de guerras $e")
        }
    }
}