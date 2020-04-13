package utilidades.economia

import estaticos.AtlantaMain
import estaticos.GestorSQL
import estaticos.GestorSalida
import estaticos.Mundo
import variables.casa.Cofre
import variables.personaje.Personaje

object Economia {
    var kamas = 0L
    var ogrinas = 0
    var precioOgrinas = 0
    var enproceso = false
    private fun FactorZero(): Boolean {
        return kamas <= 0.toLong() || ogrinas <= 0
    }

    fun economiaActual(perso: Personaje?) {
        if (perso == null) {
            return
        }
        val limit = 50
        var c = 1
        var kktop = 0L
        for (personaje in Mundo._PERSONAJES.values.sortedWith(compareByDescending { it.kamas })) {
            if (personaje.cuenta.admin > 0) continue
            if (c <= limit) {
                kktop += personaje.kamas
                c += 1
            } else break
        }
        kktop /= limit
        GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(
            perso,
            "En la Economia actual del servidor existe lo siguiente:\n\n" +
                    "Kamas totales: ${formatNumber(kamas)}\n" +
                    "Kamas Promedio TOP 50 : ${formatNumber(kktop)}\n" +
                    "Kamas Promedio Players: ${formatNumber(
                        kamas / Mundo._PERSONAJES.values.size
                    )}\n" +
                    "\n==============\n\n" +
                    "Ogrinas totales: ${formatNumber(ogrinas)}\n" +
                    "Precio Compra ogrina (unidad): ${formatNumber(
                        precioCompra()
                    )} Kamas\n" +
                    "Precio Venta ogrina (unidad): ${formatNumber(
                        precioVenta()
                    )} Kamas"
        )
    }

    private fun waiter() {
        while (enproceso) try {
            Thread.sleep(50)
        } catch (e: Exception) {
        }
    }

    fun reiniciarContador() {
        kamas = 0
        ogrinas = 0
    }

    fun updatePrecioOgrinas() {
        precioOgrinas = (kamas / ogrinas).toInt()
    }

    fun precioCompra(): Int {
        return (precioOgrinas.toDouble() * AtlantaMain.FACTOR_COMPRA).toInt()
    }

    fun precioVenta(): Int {
        return (precioOgrinas.toDouble() * AtlantaMain.FACTOR_VENTA).toInt()
    }

    fun formatNumber(num: Number): String {
        val numS = num.toString()
        val numF = arrayListOf<String>()
        var c = 0
        for (s in (numS.length - 1) downTo 0) {
            if (c % 3 == 0 && c != 0) {
                numF.add(0, ".")
                numF.add(0, numS[s].toString())
            } else numF.add(0, numS[s].toString())
            c += 1
        }
        return numF.joinToString(separator = "")
    }

    fun updateEconomia() {
        waiter()
        enproceso = true
        reiniciarContador()
        Mundo._OBJETOS.filter { (_, v) -> v.objModelo?.id == 12010 }.forEach { (_, u) -> ogrinas += u.cantidad * 50 }
        for (personaje in Mundo._PERSONAJES.values) {
            if (personaje.cuenta.admin > 0) continue
            kamas += personaje.kamas.toInt()
            for (objeto in personaje.objetosTodos) {
                if (objeto.objModeloID == 12010) {
                    ogrinas += (objeto.cantidad * 50)
                }
            }
        }
        val cofres = arrayListOf<Cofre>()
        for (cofre in Mundo.COFRES.values) {
            if (cofre.dueñoID == 0 || cofres.contains(cofre)) continue
            val admin = Mundo.getPersonaje(cofre.dueñoID)?.cuenta?.admin != 0
            if (admin) continue
            kamas += cofre.kamas.toInt()
            cofres.add(cofre)
            for (objeto in cofre.objetos) {
                if (objeto != null) {
                    if (objeto.objModeloID == 12010 && objeto.dueñoTemp <= 0) ogrinas += (objeto.cantidad * 50)
                }
            }
        }
        for (cuenta in Mundo.cuentas.values) {
            if (cuenta.admin > 0) continue
            kamas += cuenta.kamasBanco.toInt()
        }
        ogrinas += GestorSQL.GET_OGRINAS_TOTALES()
        for (merca in Mundo.MERCADILLOS.values) {
            for (objm in merca.objetosMercadillos) {
                if (Mundo.getCuenta(objm.cuentaID)?.admin ?: 0 > 0) continue
                if (objm.objeto.objModeloID == 12010 && objm.objeto.dueñoTemp <= 0) ogrinas += (objm.objeto.cantidad * 50)
            }
        }
        updatePrecioOgrinas()
        enproceso = false
    }


    fun comprarOgrinas(ogrinasd: Int, perso: Personaje?, proyeccion: Boolean, panel: Boolean = false) {
        if (perso == null) {
            return
        }
        if (FactorZero()) {
            perso.enviarmensajeRojo("Comunicate con un administrador, La economia esta quebrada")
            return
        }
        waiter()
        var kamasperso = perso.kamas
        if (kamasperso < precioCompra()) return
        enproceso = true
        var ogrinasCompradas = 0
        while (kamasperso >= precioCompra() && ogrinasCompradas < ogrinasd) {
            ogrinasCompradas += 1
            kamasperso -= precioCompra()
            ogrinas += 1
            kamas -= precioCompra()
            updatePrecioOgrinas()
        }
        if (kamasperso < precioCompra()) {
            if (proyeccion) {
                if (panel) {
                    GestorSalida.ENVIAR_SIMULACION_ECONOMIA_COMPRA(perso, (kamasperso - perso.kamas) * -1)
                    GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(
                        perso,
                        "Superó la cantidad de ogrinas que puede comprar, La simulación se adapto al precio de ${formatNumber(
                            ogrinasCompradas
                        )} Ogrinas y no a las ${formatNumber(ogrinasd)} Ogrinas Deseadas"
                    )
                } else {
                    perso.enviarmensajeRojo(
                        "Usted solo puede comprar ${formatNumber(
                            ogrinasCompradas
                        )} Ogrinas " +
                                "y no ${formatNumber(ogrinasd)} Ogrinas.\nA un precio de: ${formatNumber(
                                    (kamasperso - perso.kamas) * -1
                                )}\n" +
                                "Para confimar use:\n" +
                                ".cog $ogrinasCompradas -y"
                    )
                }
            } else {
                if (panel) {
                    GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(
                        perso, "Ha logrado comprar ${formatNumber(ogrinasCompradas)} Ogrinas y no" +
                                " ${formatNumber(ogrinasd)} Ogrinas\nYa que no poseia las KK suficientes"
                    )
                } else {
                    perso.enviarmensajeRojo(
                        "Ha logrado comprar ${formatNumber(ogrinasCompradas)} Ogrinas y no" +
                                " ${formatNumber(ogrinasd)} Ogrinas\nYa que no poseia las KK suficientes"
                    )
                }
            }
        } else {
            if (proyeccion) {
                if (panel) {
                    GestorSalida.ENVIAR_SIMULACION_ECONOMIA_COMPRA(perso, (kamasperso - perso.kamas) * -1)
                } else {
                    perso.enviarmensajeNegro(
                        "El precio de comprar ${formatNumber(ogrinasCompradas)} Ogrinas es de: " +
                                formatNumber((kamasperso - perso.kamas) * -1) + " Kamas"
                    )
                }
            } else {
                if (panel) {
                    GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(
                        perso, "Ha comprado ${formatNumber(ogrinasCompradas)} Ogrinas.\nA un precio de: " +
                                "${formatNumber((kamasperso - perso.kamas) * -1)} Kamas"
                    )
                } else {
                    perso.enviarmensajeVerde(
                        "Ha comprado ${formatNumber(
                            ogrinasCompradas
                        )} Ogrinas"
                    )
                }
            }
        }
        if (!proyeccion) {
            perso.addKamas(kamasperso - perso.kamas, true, true)
            GestorSQL.ADD_OGRINAS_CUENTA(ogrinasCompradas.toLong(), perso.cuentaID)
        }
        enproceso = false // Evita choque de procesos
    }

    fun ventaOgrinas(OgrinasV: Int, perso: Personaje?, proyeccion: Boolean, panel: Boolean = false) {
        if (perso == null) {
            return
        }
        if (FactorZero()) {
            perso.enviarmensajeRojo("Comunicate con un administrador, La economia esta quebrada")
            return
        }
        waiter()
        val ogrinasP = GestorSQL.GET_OGRINAS_CUENTA(perso.cuentaID)
        if (ogrinasP <= 0) return
        enproceso = true
        var ogrinasv = OgrinasV
        var kamasCompradas = 0
        if (OgrinasV > ogrinasP) {
            ogrinasv = ogrinasP
            if (panel) {
                GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(
                    perso, "Usted solo posee ${formatNumber(
                        ogrinasv
                    )} Ogrinas asi que se ha ajustado a este maximo la venta"
                )
            } else {
                perso.enviarmensajeNegro(
                    "Usted solo posee ${formatNumber(
                        ogrinasv
                    )} asi que se ha ajustado a este maximo la venta"
                )
            }
        }
        for (i in 1..ogrinasv) {
            try {
                kamasCompradas += precioVenta()
                kamas -= precioVenta()
                ogrinas += 1
                updatePrecioOgrinas()
            } catch (e: ArithmeticException) {
                ogrinasv = i
                break
            } catch (e: Exception) {
                continue
            }
        }
        if (proyeccion) {
            if (panel) {
                GestorSalida.ENVIAR_SIMULACION_ECONOMIA_VENTA(perso, kamasCompradas.toLong())
            } else {
                perso.enviarmensajeNegro(
                    "La ganancia de vender ${formatNumber(ogrinasv)} Ogrinas es de Aproximadamente: ${formatNumber(
                        kamasCompradas
                    )}\n" +
                            "Para confirmar use:\n" +
                            ".vog ${formatNumber(ogrinasv)} -y"
                )
            }
        } else {
            if (panel) {
                GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(
                    perso,
                    "Ha vendido ${formatNumber(ogrinasv)} Ogrinas por:\n${formatNumber(kamasCompradas.toLong())} Kamas"
                )
            } else {
                perso.enviarmensajeVerde("Ha perdido ${formatNumber(ogrinasv)} Ogrinas")
            }
            GestorSQL.RESTAR_OGRINAS(perso.cuenta, ogrinasv.toLong(), perso)
            perso.addKamas(kamasCompradas.toLong(), true, true)
        }
        enproceso = false
    }
}