package variables.zotros

import estaticos.*
import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Formulas.formatoTiempo
import estaticos.Formulas.getRandomInt
import estaticos.Formulas.getXPMision
import estaticos.GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ
import estaticos.GestorSalida.ENVIAR_As_STATS_DEL_PJ
import estaticos.GestorSalida.ENVIAR_BN_NADA
import estaticos.GestorSalida.ENVIAR_DQ_DIALOGO_PREGUNTA
import estaticos.GestorSalida.ENVIAR_Ej_AGREGAR_LIBRO_ARTESANO
import estaticos.GestorSalida.ENVIAR_GA2_CINEMATIC
import estaticos.GestorSalida.ENVIAR_GA_ACCION_JUEGO_AL_MAPA
import estaticos.GestorSalida.ENVIAR_GDF_FORZADO_MAPA
import estaticos.GestorSalida.ENVIAR_GDF_FORZADO_PERSONAJE
import estaticos.GestorSalida.ENVIAR_GDO_OBJETO_TIRAR_SUELO
import estaticos.GestorSalida.ENVIAR_GM_PRISMA_A_MAPA
import estaticos.GestorSalida.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA_SIN_HUMO
import estaticos.GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS
import estaticos.GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION
import estaticos.GestorSalida.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO
import estaticos.GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO
import estaticos.GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA_MAPA
import estaticos.GestorSalida.ENVIAR_Ow_PODS_DEL_PJ
import estaticos.GestorSalida.ENVIAR_Re_DETALLES_MONTURA
import estaticos.GestorSalida.ENVIAR_SF_OLVIDAR_HECHIZO
import estaticos.GestorSalida.ENVIAR_TC_CARGAR_TUTORIAL
import estaticos.GestorSalida.ENVIAR_aM_CAMBIAR_ALINEACION_AREA
import estaticos.GestorSalida.ENVIAR_am_CAMBIAR_ALINEACION_SUBAREA
import estaticos.GestorSalida.ENVIAR_brP_RULETA_PREMIOS
import estaticos.GestorSalida.ENVIAR_cs_CHAT_MENSAJE
import estaticos.GestorSalida.ENVIAR_dC_ABRIR_DOCUMENTO
import estaticos.GestorSalida.ENVIAR_eA_AGREGAR_EMOTE
import estaticos.GestorSalida.ENVIAR_eR_BORRAR_EMOTE
import estaticos.GestorSalida.ENVIAR_eUK_EMOTE_MAPA
import estaticos.GestorSalida.ENVIAR_gC_CREAR_PANEL_GREMIO
import estaticos.GestorSalida.ENVIAR_gUF_PANEL_CERCADOS_GREMIO
import estaticos.GestorSalida.ENVIAR_gUT_PANEL_CASA_GREMIO
import estaticos.GestorSalida.ENVIAR_gn_CREAR_GREMIO
import estaticos.GestorSalida.enviar
import estaticos.database.GestorSQL.ADD_OGRINAS_CUENTA
import estaticos.database.GestorSQL.CAMBIAR_SEXO_CLASE
import estaticos.database.GestorSQL.GET_ABONO
import estaticos.database.GestorSQL.GET_CREDITOS_CUENTA
import estaticos.database.GestorSQL.REPLACE_MONTURA
import estaticos.database.GestorSQL.SET_ABONO
import estaticos.database.GestorSQL.SET_CREDITOS_CUENTA
import variables.mision.Mision
import variables.mob.GrupoMob
import variables.mob.MobModelo.TipoGrupo
import variables.npc.PreguntaNPC
import variables.objeto.ObjetoModelo
import variables.objeto.ObjetoModelo.CAPACIDAD_STATS
import variables.objeto.ObjetoModelo.Companion.stringFechaIntercambiable
import variables.personaje.MisionPVP
import variables.personaje.Personaje
import java.util.*
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.max

class Accion(val id: Int, val args: String, condicion: String) {
    var condicion = ""

    fun realizarAccion(perso: Personaje, objetivo: Personaje?, idObjUsar: Int, celda: Short): Boolean {
        return realizar_Accion_Estatico(id, args, perso, objetivo, idObjUsar, celda)
    }

    companion object {
        private const val ACCION_CREAR_GREMIO = -2
        private const val ACCION_ABRIR_BANCO = -1
        private const val ACCION_TELEPORT_MAPA = 0
        private const val ACCION_DIALOGO = 1
        private const val ACCION_AGREGAR_OBJETO_AZAR = 2
        private const val ACCION_NADA = 3
        private const val ACCION_DAR_QUITAR_KAMAS = 4
        private const val ACCION_DAR_QUITAR_OBJETOS = 5
        private const val ACCION_APRENDER_OFICIO = 6
        private const val ACCION_RETORNAR_PUNTO_SALVADA = 7
        private const val ACCION_BOOST_STATS = 8
        private const val ACCION_APRENDER_HECHIZO = 9
        private const val ACCION_CURAR = 10
        private const val ACCION_CAMBIAR_ALINEACION = 11
        private const val ACCION_CREAR_GRUPO_MOB_CON_PIEDRA = 12
        private const val ACCION_RESETEAR_STATS = 13
        private const val ACCION_OLVIDAR_HECHIZO_PANEL = 14
        private const val ACCION_USAR_LLAVE = 15
        private const val ACCION_DAR_QUITAR_HONOR = 16
        private const val ACCION_EXP_OFICIO = 17
        private const val ACCION_TELEPORT_CASA = 18
        private const val ACCION_PANEL_CASA_GREMIO = 19
        private const val ACCION_DAR_QUITAR_PUNTOS_HECHIZO = 20
        private const val ACCION_DAR_QUITAR_ENERGIA = 21
        private const val ACCION_DAR_EXPERIENCIA = 22
        private const val ACCION_OLVIDAR_OFICIO = 23
        private const val ACCION_CAMBIAR_GFX = 24
        private const val ACCION_DEFORMAR = 25
        private const val ACCION_PANEL_CERCADO_GREMIO = 26
        private const val ACCION_INICIAR_PELEA_VS_MOBS = 27
        private const val ACCION_SUBIR_BAJAR_MONTURA = 28
        private const val ACCION_INCIAR_PELEA_VS_MOBS_NO_ESPADA = 29
        private const val ACCION_REFRESCAR_MOBS = 30
        private const val ACCION_CAMBIAR_CLASE = 31
        private const val ACCION_AUMENTAR_RESETS = 32
        private const val ACCION_OBJETO_BOOST = 33
        private const val ACCION_CAMBIAR_SEXO = 34
        private const val ACCION_PAGAR_PESCAR_KUAKUA = 35
        private const val ACCION_RULETA_JALATO = 36
        private const val ACCION_DAR_ORNAMENTO = 37
        private const val ACCION_TELEPORT_CELDA_MISMO_MAPA = 38
        private const val ACCION_GANAR_RULETA_JALATO = 39
        private const val ACCION_KAMAS_RULETA_JALATO = 40
        private const val ACCION_INICIAR_PELEA_DOPEUL = 41
        private const val ACCION_DAR_SET_OBJETOS = 42
        private const val ACCION_CONFIRMAR_CUMPLIO_OBJETIVO_MISION = 43
        private const val ACCION_DAR_MISION = 44
        private const val ACCION_AGREGAR_MOB_ALBUM = 45
        private const val ACCION_DAR_TITULO = 46
        private const val ACCION_RECOMPENSA_DOPEUL = 47
        private const val ACCION_VERIFICA_MISION_ALMANAX = 48
        private const val ACCION_DAR_MISION_PVP_CON_PERGAMINOS = 49
        private const val ACCION_DAR_MISION_PVP = 50
        private const val ACCION_GEOPOSICION_MISION_PVP = 51
        private const val ACCION_TELEPORT_MISION_PVP = 52
        private const val ACCION_CONFIRMA_CUMPLIO_MISION = 53
        private const val ACCION_BOOST_FULL_STATS = 54
        private const val ACCION_PAGAR_PARA_REALIZAR_ACCION = 55
        private const val ACCION_SOLICITAR_OBJETOS_PARA_DAR_OTROS = 56
        private const val ACCION_REVIVIR = 57
        private const val ACCION_ABRIR_DOCUMENTO = 58
        private const val ACCION_DAR_SET_OBJETOS_POR_FICHAS = 59
        private const val ACCION_REALIZAR_ACCION_PJS_EN_MAPA_POR_ALINEACION_Y_DISTANCIA = 60
        private const val ACCION_LIBERAR_TUMBA = 61
        private const val ACCION_REVIVIR2 = 62
        private const val ACCION_AGREGAR_PJ_LIBRO_ARTESANOS = 63
        private const val ACCION_ACTIVAR_CELDAS_INTERACTIVAS = 64
        private const val ACCION_DAR_QUITAR_EMOTE = 65
        private const val ACCION_SOLICITAR_OBJETOS_PARA_REALIZAR_ACCION = 66
        private const val ACCION_CAMBIAR_ROSTRO = 67
        private const val ACCION_MENSAJE_INFORMACION = 68
        private const val ACCION_MENSAJE_PANEL = 69
        private const val ACCION_DAR_OBJETOS_DE_LOS_STATS = 70
        private const val ACCION_DAR_ABONO_DIAS = 71
        private const val ACCION_DAR_ABONO_HORAS = 72
        private const val ACCION_DAR_ABONO_MINUTOS = 73
        private const val ACCION_DAR_NIVEL_DE_ORDEN = 74
        private const val ACCION_DAR_ORDEN = 75
        private const val ACCION_BORRAR_OBJETO_MODELO = 76
        private const val ACCION_VERIFICA_STAT_OBJETO_Y_LO_BORRA = 77
        private const val ACCION_BORRAR_OBJETO_AL_AZAR_PARA_DAR_OTROS = 78
        private const val ACCION_GDF_PERSONA = 79
        private const val ACCION_GDF_MAPA = 80
        private const val ACCION_RULETA_PREMIOS = 81
        private const val ACCION_TIEMPO_PROTECCION_RECAUDADOR = 82
        private const val ACCION_TIEMPO_PROTECCION_PRISMA = 83
        private const val ACCION_OLVIDAR_HECHIZO_RECAUDADOR = 84
        private const val ACCION_ENVIAR_PACKET = 99
        private const val ACCION_DAR_HABILIDAD_MONTURA = 100
        private const val ACCION_CASAR_DOS_PJS = 101
        private const val ACCION_DISCURSO_SACEDORTE = 102
        private const val ACCION_DIVORCIARSE = 103
        private const val ACCION_DAR_OGRINAS = 104
        private const val ACCION_DAR_CREDITOS = 105
        private const val ACCION_MAPA_RANDOM = 106
        private const val ACCION_APRENDER_HECHIZO_CLASE = 107
        private const val ACCION_CUMPLIO_OBJETIVO_PVP = 108
        private const val ACCION_AGREGAR_OBJETO_A_CERCADO = 200
        private const val ACCION_AGREGAR_PRISMA_A_MAPA = 201
        private const val ACCION_LANZAR_ANIMACION = 227
        private const val ACCION_LANZAR_ANIMACION2 = 228
        private const val ACCION_CAMBIAR_COLOR = 229
        private const val ACCION_ESTATUAS_ASTRUB = 230
        private const val ACCION_CAMBIAR_NOMBRE = 231
        private const val ACCION_GUARDAR_POS = 232
        private const val ACCION_MANOJO_LLAVES = 233
        private const val ACCION_OLVIDAR_HECHIZO_TEMPLO = 234
        private const val ACCION_RESET_TEMPLO = 235
        private const val ACCION_DAR_ABONO_SEMANA = 236
        private const val ACCION_BOLSA_ALEATORIA = 237
        private const val ACCION_BOLSA_ALEATORIA_MAZMORRA = 238
        private const val HACER_MUCHAS_ACCIONES = 239
        fun realizar_Accion_Estatico(
            _id: Int, _args: String, perso: Personaje,
            objetivo: Personaje?, idObjUsar: Int, celdaID: Short
        ): Boolean {
            var objetivo = objetivo
            var celdaID = celdaID
            return try {
                if (objetivo == null) {
                    objetivo = perso
                }
                if (celdaID.toInt() == -1) {
                    celdaID = perso.celda.id
                }
                // if (!Condicion.validaCondiciones(perso, condicion)) {
// return false;
// }
                val objUsar = Mundo.getObjeto(idObjUsar)
                when (_id) {
                    ACCION_CREAR_GREMIO -> try {
                        if (perso.estaDisponible(false, false)) {
                            ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        if (perso.gremio != null || perso.miembroGremio != null) {
                            ENVIAR_gC_CREAR_PANEL_GREMIO(perso, "Ea")
                            return false
                        }
                        // perso.addObjIdentAInventario(Mundo.getObjetoModelo(1575).crearObjDesdeModelo(1,
// Constantes.OBJETO_POS_NO_EQUIPADO, 0), false);
                        perso.setOcupado(true)
                        ENVIAR_gn_CREAR_GREMIO(perso)
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_ABRIR_BANCO -> try {
                        if (perso.deshonor >= 1) {
                            ENVIAR_Im_INFORMACION(perso, "183")
                            return false
                        }
                        if (perso.estaDisponible(false, false)) {
                            ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        val costo = perso.costoAbrirBanco
                        if (perso.kamas - costo < 0) {
                            ENVIAR_Im_INFORMACION(perso, "1128;$costo")
                            ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(perso, 10, costo.toString() + "", "")
                        } else {
                            perso.addKamas(-costo.toLong(), false, true)
                            ENVIAR_Im_INFORMACION(perso, "020;$costo")
                            perso.banco.abrirCofre(perso)
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_TELEPORT_MAPA -> try {
                        val args = _args.split(",".toRegex()).toTypedArray()
                        val nuevoMapa = Mundo.getMapa(args[0].toShort())
                        if (nuevoMapa == null) {
                            ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        if (objUsar != null) {
                            if (perso.mapa.esMazmorra()) {
                                ENVIAR_Im_INFORMACION(perso, "113")
                                return false
                            }
                        }
                        if (perso.pelea != null) {
                            val nuevaCelda = nuevoMapa.getCelda(args[1].toShort())
                            if (nuevaCelda != null) {
                                perso.mapa = nuevoMapa
                                perso.celda = nuevaCelda
                            }
                        } else {
                            if (args.size > 2) {
                                ENVIAR_GA2_CINEMATIC(perso, args[2])
                            }
                            perso.teleport(args[0].toShort(), args[1].toShort())
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_GUARDAR_POS -> try {
                        val args = _args.split(",".toRegex()).toTypedArray()
                        val nuevoMapa = Mundo.getMapa(args[0].toShort())
                        if (nuevoMapa == null) {
                            ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        perso.setPuntoSalvada(args[0] + "," + args[1])
                    } catch (ignored: Exception) {
                    }
                    ACCION_MANOJO_LLAVES -> when (perso.getClaseID(true).toInt()) {
                        1 -> {
                            if (perso.tenerYEliminarObjPorModYCant(10207, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has renovado el manojo de llaves", "da6c00")
                                return false
                            }
                            if (perso.mapa.id.toInt() != 1554) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(10306, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has obtenido el manojo de llaves", "da6c00")
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        2 -> {
                            if (perso.tenerYEliminarObjPorModYCant(10207, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has renovado el manojo de llaves", "da6c00")
                                return false
                            }
                            if (perso.mapa.id.toInt() != 1546) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(10308, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has obtenido el manojo de llaves", "da6c00")
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        3 -> {
                            if (perso.tenerYEliminarObjPorModYCant(10207, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has renovado el manojo de llaves", "da6c00")
                                return false
                            }
                            if (perso.mapa.id.toInt() != 1470) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(10305, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has obtenido el manojo de llaves", "da6c00")
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        4 -> {
                            if (perso.tenerYEliminarObjPorModYCant(10207, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has renovado el manojo de llaves", "da6c00")
                                return false
                            }
                            if (perso.mapa.id.toInt() != 6926) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(10312, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has obtenido el manojo de llaves", "da6c00")
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        5 -> {
                            if (perso.tenerYEliminarObjPorModYCant(10207, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has renovado el manojo de llaves", "da6c00")
                                return false
                            }
                            if (perso.mapa.id.toInt() != 1469) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(10313, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has obtenido el manojo de llaves", "da6c00")
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        6 -> {
                            if (perso.tenerYEliminarObjPorModYCant(10207, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has renovado el manojo de llaves", "da6c00")
                                return false
                            }
                            if (perso.mapa.id.toInt() != 1544) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(10303, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has obtenido el manojo de llaves", "da6c00")
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        7 -> {
                            if (perso.tenerYEliminarObjPorModYCant(10207, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has renovado el manojo de llaves", "da6c00")
                                return false
                            }
                            if (perso.mapa.id.toInt() != 6928) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(10304, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has obtenido el manojo de llaves", "da6c00")
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        8 -> {
                            if (perso.tenerYEliminarObjPorModYCant(10207, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has renovado el manojo de llaves", "da6c00")
                                return false
                            }
                            if (perso.mapa.id.toInt() != 1549) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(10307, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has obtenido el manojo de llaves", "da6c00")
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        9 -> {
                            if (perso.tenerYEliminarObjPorModYCant(10207, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has renovado el manojo de llaves", "da6c00")
                                return false
                            }
                            if (perso.mapa.id.toInt() != 1558) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(10302, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has obtenido el manojo de llaves", "da6c00")
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        10 -> {
                            if (perso.tenerYEliminarObjPorModYCant(10207, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has renovado el manojo de llaves", "da6c00")
                                return false
                            }
                            if (perso.mapa.id.toInt() != 1466) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(10311, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has obtenido el manojo de llaves", "da6c00")
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        11 -> {
                            if (perso.tenerYEliminarObjPorModYCant(10207, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has renovado el manojo de llaves", "da6c00")
                                return false
                            }
                            if (perso.mapa.id.toInt() != 6949) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(10310, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has obtenido el manojo de llaves", "da6c00")
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        12 -> {
                            if (perso.tenerYEliminarObjPorModYCant(10207, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has renovado el manojo de llaves", "da6c00")
                                return false
                            }
                            if (perso.mapa.id.toInt() != 8490) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(10309, 1)) {
                                realizar_Accion_Estatico(5, "10207,1", perso, null, -1, (-1).toShort())
                                ENVIAR_cs_CHAT_MENSAJE(perso, "Has obtenido el manojo de llaves", "da6c00")
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                    }
                    ACCION_RESET_TEMPLO -> try {
                        var todo = false
                        when (perso.getClaseID(true).toInt()) {
                            1 -> {
                                try {
                                    todo = _args.equals("true", ignoreCase = true)
                                } catch (ignored: Exception) {
                                }
                                if (perso.mapa.id.toInt() != 1554) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10306, 1)) {
                                    perso.resetearStats(todo)
                                    realizar_Accion_Estatico(5, "6708,1", perso, null, -1, (-1).toShort())
                                    if (todo) {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado por completo", "da6c00")
                                    } else {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado parcialmente", "da6c00")
                                    }
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            2 -> {
                                todo = false
                                try {
                                    todo = _args.equals("true", ignoreCase = true)
                                } catch (ignored: Exception) {
                                }
                                if (perso.mapa.id.toInt() != 1546) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10308, 1)) {
                                    perso.resetearStats(todo)
                                    realizar_Accion_Estatico(5, "6708,1", perso, null, -1, (-1).toShort())
                                    if (todo) {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado por completo", "da6c00")
                                    } else {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado parcialmente", "da6c00")
                                    }
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            3 -> {
                                todo = false
                                try {
                                    todo = _args.equals("true", ignoreCase = true)
                                } catch (ignored: Exception) {
                                }
                                if (perso.mapa.id.toInt() != 1470) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10305, 1)) {
                                    perso.resetearStats(todo)
                                    realizar_Accion_Estatico(5, "6708,1", perso, null, -1, (-1).toShort())
                                    if (todo) {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado por completo", "da6c00")
                                    } else {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado parcialmente", "da6c00")
                                    }
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            4 -> {
                                todo = false
                                try {
                                    todo = _args.equals("true", ignoreCase = true)
                                } catch (ignored: Exception) {
                                }
                                if (perso.mapa.id.toInt() != 6926) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10312, 1)) {
                                    perso.resetearStats(todo)
                                    realizar_Accion_Estatico(5, "6708,1", perso, null, -1, (-1).toShort())
                                    if (todo) {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado por completo", "da6c00")
                                    } else {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado parcialmente", "da6c00")
                                    }
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            5 -> {
                                todo = false
                                try {
                                    todo = _args.equals("true", ignoreCase = true)
                                } catch (ignored: Exception) {
                                }
                                if (perso.mapa.id.toInt() != 1469) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10313, 1)) {
                                    perso.resetearStats(todo)
                                    realizar_Accion_Estatico(5, "6708,1", perso, null, -1, (-1).toShort())
                                    if (todo) {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado por completo", "da6c00")
                                    } else {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado parcialmente", "da6c00")
                                    }
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            6 -> {
                                todo = false
                                try {
                                    todo = _args.equals("true", ignoreCase = true)
                                } catch (ignored: Exception) {
                                }
                                if (perso.mapa.id.toInt() != 1544) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10303, 1)) {
                                    perso.resetearStats(todo)
                                    realizar_Accion_Estatico(5, "6708,1", perso, null, -1, (-1).toShort())
                                    if (todo) {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado por completo", "da6c00")
                                    } else {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado parcialmente", "da6c00")
                                    }
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            7 -> {
                                todo = false
                                try {
                                    todo = _args.equals("true", ignoreCase = true)
                                } catch (ignored: Exception) {
                                }
                                if (perso.mapa.id.toInt() != 6928) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10304, 1)) {
                                    perso.resetearStats(todo)
                                    realizar_Accion_Estatico(5, "6708,1", perso, null, -1, (-1).toShort())
                                    if (todo) {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado por completo", "da6c00")
                                    } else {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado parcialmente", "da6c00")
                                    }
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            8 -> {
                                todo = false
                                try {
                                    todo = _args.equals("true", ignoreCase = true)
                                } catch (ignored: Exception) {
                                }
                                if (perso.mapa.id.toInt() != 1549) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10307, 1)) {
                                    perso.resetearStats(todo)
                                    realizar_Accion_Estatico(5, "6708,1", perso, null, -1, (-1).toShort())
                                    if (todo) {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado por completo", "da6c00")
                                    } else {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado parcialmente", "da6c00")
                                    }
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            9 -> {
                                todo = false
                                try {
                                    todo = _args.equals("true", ignoreCase = true)
                                } catch (ignored: Exception) {
                                }
                                if (perso.mapa.id.toInt() != 1558) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10302, 1)) {
                                    perso.resetearStats(todo)
                                    realizar_Accion_Estatico(5, "6708,1", perso, null, -1, (-1).toShort())
                                    if (todo) {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado por completo", "da6c00")
                                    } else {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado parcialmente", "da6c00")
                                    }
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            10 -> {
                                todo = false
                                try {
                                    todo = _args.equals("true", ignoreCase = true)
                                } catch (ignored: Exception) {
                                }
                                if (perso.mapa.id.toInt() != 1466) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10311, 1)) {
                                    perso.resetearStats(todo)
                                    realizar_Accion_Estatico(5, "6708,1", perso, null, -1, (-1).toShort())
                                    if (todo) {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado por completo", "da6c00")
                                    } else {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado parcialmente", "da6c00")
                                    }
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            11 -> {
                                todo = false
                                try {
                                    todo = _args.equals("true", ignoreCase = true)
                                } catch (ignored: Exception) {
                                }
                                if (perso.mapa.id.toInt() != 6949) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10310, 1)) {
                                    perso.resetearStats(todo)
                                    realizar_Accion_Estatico(5, "6708,1", perso, null, -1, (-1).toShort())
                                    if (todo) {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado por completo", "da6c00")
                                    } else {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado parcialmente", "da6c00")
                                    }
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            12 -> {
                                todo = false
                                try {
                                    todo = _args.equals("true", ignoreCase = true)
                                } catch (ignored: Exception) {
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10309, 1)) {
                                    perso.resetearStats(todo)
                                    realizar_Accion_Estatico(5, "6708,1", perso, null, -1, (-1).toShort())
                                    if (todo) {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado por completo", "da6c00")
                                    } else {
                                        ENVIAR_cs_CHAT_MENSAJE(perso, "Te has reseteado parcialmente", "da6c00")
                                    }
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        redactarLogServidorln("Error e: $e")
                    }
                    ACCION_OLVIDAR_HECHIZO_TEMPLO -> when (perso.getClaseID(true).toInt()) {
                        1 -> {
                            if (perso.mapa.id.toInt() != 1554) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(
                                    10306,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10308, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10305,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10312, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10313,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10303, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10304,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10307, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10302,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10309, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10310,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10311, 7)
                            ) {
                                realizar_Accion_Estatico(14, "", perso, null, -1, (-1).toShort())
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        2 -> {
                            if (perso.mapa.id.toInt() != 1546) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(
                                    10306,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10308, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10305,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10312, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10313,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10303, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10304,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10307, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10302,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10309, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10310,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10311, 7)
                            ) {
                                realizar_Accion_Estatico(14, "", perso, null, -1, (-1).toShort())
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        3 -> {
                            if (perso.mapa.id.toInt() != 1470) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(
                                    10306,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10308, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10305,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10312, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10313,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10303, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10304,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10307, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10302,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10309, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10310,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10311, 7)
                            ) {
                                realizar_Accion_Estatico(14, "", perso, null, -1, (-1).toShort())
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        4 -> {
                            if (perso.mapa.id.toInt() != 6926) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(
                                    10306,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10308, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10305,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10312, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10313,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10303, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10304,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10307, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10302,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10309, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10310,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10311, 7)
                            ) {
                                realizar_Accion_Estatico(14, "", perso, null, -1, (-1).toShort())
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        5 -> {
                            if (perso.mapa.id.toInt() != 1469) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(
                                    10306,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10308, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10305,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10312, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10313,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10303, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10304,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10307, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10302,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10309, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10310,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10311, 7)
                            ) {
                                realizar_Accion_Estatico(14, "", perso, null, -1, (-1).toShort())
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        6 -> {
                            if (perso.mapa.id.toInt() != 1544) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(
                                    10306,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10308, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10305,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10312, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10313,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10303, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10304,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10307, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10302,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10309, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10310,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10311, 7)
                            ) {
                                realizar_Accion_Estatico(14, "", perso, null, -1, (-1).toShort())
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        7 -> {
                            if (perso.mapa.id.toInt() != 6928) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(
                                    10306,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10308, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10305,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10312, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10313,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10303, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10304,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10307, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10302,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10309, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10310,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10311, 7)
                            ) {
                                realizar_Accion_Estatico(14, "", perso, null, -1, (-1).toShort())
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        8 -> {
                            if (perso.mapa.id.toInt() != 1549) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(
                                    10306,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10308, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10305,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10312, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10313,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10303, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10304,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10307, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10302,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10309, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10310,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10311, 7)
                            ) {
                                realizar_Accion_Estatico(14, "", perso, null, -1, (-1).toShort())
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        9 -> {
                            if (perso.mapa.id.toInt() != 1558) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(
                                    10306,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10308, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10305,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10312, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10313,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10303, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10304,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10307, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10302,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10309, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10310,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10311, 7)
                            ) {
                                realizar_Accion_Estatico(14, "", perso, null, -1, (-1).toShort())
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        10 -> {
                            if (perso.mapa.id.toInt() != 1466) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(
                                    10306,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10308, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10305,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10312, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10313,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10303, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10304,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10307, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10302,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10309, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10310,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10311, 7)
                            ) {
                                realizar_Accion_Estatico(14, "", perso, null, -1, (-1).toShort())
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        11 -> {
                            if (perso.mapa.id.toInt() != 6949) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(
                                    10306,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10308, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10305,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10312, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10313,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10303, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10304,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10307, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10302,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10309, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10310,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10311, 7)
                            ) {
                                realizar_Accion_Estatico(14, "", perso, null, -1, (-1).toShort())
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                        12 -> {
                            if (perso.mapa.id.toInt() != 8490) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "Debes estar en tu templo para realizar la acción",
                                    "da6c00"
                                )
                                return false
                            }
                            if (perso.tenerYEliminarObjPorModYCant(
                                    10306,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10308, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10305,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10312, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10313,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10303, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10304,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10307, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10302,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10309, 7) || perso.tenerYEliminarObjPorModYCant(
                                    10310,
                                    7
                                ) || perso.tenerYEliminarObjPorModYCant(10311, 7)
                            ) {
                                realizar_Accion_Estatico(14, "", perso, null, -1, (-1).toShort())
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14")
                            }
                        }
                    }
                    ACCION_DIALOGO -> try {
                        if (_args == "DV") {
                            perso.dialogoFin()
                        } else {
                            var preguntaID = 0
                            try {
                                preguntaID = _args.toInt()
                            } catch (ignored: Exception) {
                            }
                            if (preguntaID <= 0) {
                                perso.dialogoFin()
                            }
                            var pregunta = Mundo.getPreguntaNPC(preguntaID)
                            if (pregunta == null) {
                                pregunta = PreguntaNPC(preguntaID, "", "", "")
                                Mundo.addPreguntaNPC(pregunta)
                            }
                            ENVIAR_DQ_DIALOGO_PREGUNTA(perso, pregunta.stringArgParaDialogo(perso, perso))
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_AGREGAR_OBJETO_AZAR -> {
                        // no borra el objeto q se usa
                        try {
                            val quitar = _args.split(Pattern.quote("|").toRegex()).toTypedArray()[0]
                            val azar = _args.split(Pattern.quote("|").toRegex()).toTypedArray()[1].split(";".toRegex())
                                .toTypedArray()
                            val idQuitar = quitar.split(",".toRegex()).toTypedArray()[0].toInt()
                            val cantQuitar = abs(quitar.split(",".toRegex()).toTypedArray()[1].toInt())
                            if (perso.tenerYEliminarObjPorModYCant(idQuitar, cantQuitar)) {
                                val objetoAzar = azar[getRandomInt(0, azar.size - 1)]
                                val idDar = objetoAzar.split(",".toRegex()).toTypedArray()[0].toInt()
                                val cantDar = abs(objetoAzar.split(",".toRegex()).toTypedArray()[1].toInt())
                                perso.addObjIdentAInventario(
                                    Mundo.getObjetoModelo(idDar)?.crearObjeto(
                                        cantDar,
                                        Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM
                                    ), false
                                )
                                //							GestorSalida.ENVIAR_Im_INFORMACION(perso, "022;" + cantQuitar + "~" + idQuitar);
                                ENVIAR_Im_INFORMACION(perso, "021;$cantDar~$idDar")
                                ENVIAR_Ow_PODS_DEL_PJ(perso)
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "14|43")
                            }
                        } catch (e: Exception) {
                            ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        }
                        return false // no borra
                    }
                    ACCION_NADA -> {
                    }
                    ACCION_DAR_QUITAR_KAMAS -> try {
                        var kamas = 0
                        val s = _args.split(",".toRegex()).toTypedArray()
                        kamas = if (s.size == 1) {
                            s[0].toInt()
                        } else {
                            getRandomInt(s[0].toInt(), s[1].toInt())
                        }
                        perso.addKamas(kamas.toLong(), true, true)
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_QUITAR_OBJETOS -> try {
                        var b = false
                        for (s in _args.split(";".toRegex()).toTypedArray()) {
                            val ss = s.split(",".toRegex()).toTypedArray()
                            val id = ss[0].toInt()
                            var cant = 1 // corregir los otros
                            if (ss.size > 1) {
                                cant = ss[1].toInt()
                            }
                            val tempObjMod = Mundo.getObjetoModelo(id)
                            if (tempObjMod == null) {
                                ENVIAR_BN_NADA(objetivo, "BUG ACCION $_id idObjMod $id")
                            } else {
                                if (cant > 0) {
                                    b = true
                                    perso.addObjIdentAInventario(
                                        tempObjMod.crearObjeto(
                                            cant, Constantes.OBJETO_POS_NO_EQUIPADO,
                                            CAPACIDAD_STATS.RANDOM
                                        ), false
                                    )
                                    ENVIAR_Im_INFORMACION(perso, "021;$cant~$id")
                                } else if (cant < 0) {
                                    val borrados = perso.restarObjPorModYCant(id, abs(cant))
                                    if (borrados > 0) {
                                        b = true
                                        ENVIAR_Im_INFORMACION(perso, "022;$borrados~$id")
                                    }
                                }
                            }
                        }
                        if (b) {
                            ENVIAR_Ow_PODS_DEL_PJ(perso)
                        } else {
                            return false
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_APRENDER_OFICIO -> try {
                        var b = false
                        for (s in _args.split(";".toRegex()).toTypedArray()) {
                            val idOficio = s.split(",".toRegex()).toTypedArray()[0].toInt()
                            var siOSi = false
                            try {
                                siOSi = s.split(",".toRegex()).toTypedArray()[1] == "1"
                            } catch (ignored: Exception) {
                            }
                            if (Mundo.getOficio(idOficio) == null) {
                                ENVIAR_BN_NADA(objetivo)
                            } else {
                                if (siOSi || perso.puedeAprenderOficio(idOficio)) {
                                    b = true
                                    perso.aprenderOficio(Mundo.getOficio(idOficio), 0)
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "16")
                                }
                            }
                        }
                        if (!b) {
                            return false
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_RETORNAR_PUNTO_SALVADA -> try {
                        if (perso.pelea != null) {
                            ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        perso.teleportPtoSalvada()
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_BOOST_STATS -> try {
                        var `as` = false
                        for (s in _args.split(";".toRegex()).toTypedArray()) {
                            val statID = s.split(",".toRegex()).toTypedArray()[0].toInt()
                            val cantidad = s.split(",".toRegex()).toTypedArray()[1].toInt()
                            var mensajeID = 0
                            when (statID) {
                                Constantes.STAT_MAS_SABIDURIA -> {
                                    perso.addScrollStat(Constantes.STAT_MAS_SABIDURIA, cantidad)
                                    mensajeID = 9
                                }
                                Constantes.STAT_MAS_FUERZA -> {
                                    perso.addScrollStat(Constantes.STAT_MAS_FUERZA, cantidad)
                                    mensajeID = 10
                                }
                                Constantes.STAT_MAS_SUERTE -> {
                                    perso.addScrollStat(Constantes.STAT_MAS_SUERTE, cantidad)
                                    mensajeID = 11
                                }
                                Constantes.STAT_MAS_AGILIDAD -> {
                                    perso.addScrollStat(Constantes.STAT_MAS_AGILIDAD, cantidad)
                                    mensajeID = 12
                                }
                                Constantes.STAT_MAS_VITALIDAD -> {
                                    perso.addScrollStat(Constantes.STAT_MAS_VITALIDAD, cantidad)
                                    mensajeID = 13
                                }
                                Constantes.STAT_MAS_INTELIGENCIA -> {
                                    perso.addScrollStat(Constantes.STAT_MAS_INTELIGENCIA, cantidad)
                                    mensajeID = 14
                                }
                            }
                            if (mensajeID > 0) {
                                `as` = true
                                ENVIAR_Im_INFORMACION(perso, "0$mensajeID;$cantidad")
                            }
                        }
                        if (`as`) {
                            ENVIAR_As_STATS_DEL_PJ(perso)
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_APRENDER_HECHIZO -> try {
                        for (s in _args.split(";".toRegex()).toTypedArray()) {
                            val hechizoID = s.toInt()
                            if (Mundo.getHechizo(hechizoID) == null) {
                                ENVIAR_BN_NADA(objetivo)
                                return false
                            }
                            if (!objetivo.tieneHechizoID(hechizoID)) {
                                objetivo.fijarNivelHechizoOAprender(hechizoID, 1, true)
                            } else {
                                ENVIAR_Im_INFORMACION(objetivo, "17;$hechizoID")
                                return false
                            }
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_CURAR -> try {
                        if (objetivo.porcPDV >= 100) {
                            ENVIAR_BN_NADA(perso)
                            return false
                        }
                        try {
                            if (objUsar != null) {
                                val tipo = objUsar.objModelo?.tipo ?: return false
                                when (tipo.toInt()) {
                                    Constantes.OBJETO_TIPO_BEBIDA, Constantes.OBJETO_TIPO_POCION -> ENVIAR_eUK_EMOTE_MAPA(
                                        objetivo.mapa,
                                        objetivo.Id,
                                        18,
                                        0
                                    )
                                    Constantes.OBJETO_TIPO_PAN, Constantes.OBJETO_TIPO_CARNE_COMESTIBLE, Constantes.OBJETO_TIPO_PESCADO_COMESTIBLE -> ENVIAR_eUK_EMOTE_MAPA(
                                        objetivo.mapa,
                                        objetivo.Id,
                                        17,
                                        0
                                    )
                                }
                            }
                        } catch (ignored: Exception) {
                        }
                        var valor = 0
                        val s = _args.split(",".toRegex()).toTypedArray()
                        valor = if (s.size == 1) {
                            s[0].toInt()
                        } else {
                            getRandomInt(s[0].toInt(), s[1].toInt())
                        }
                        objetivo.addPDV(valor)
                        ENVIAR_Im_INFORMACION(perso, "01;$valor")
                        ENVIAR_Ak_KAMAS_PDV_EXP_PJ(objetivo)
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_CAMBIAR_ALINEACION -> {
                        return try {
                            val alineacion = _args.split(",".toRegex()).toTypedArray()[0].toByte()
                            if (alineacion == perso.alineacion) {
                                false
                            } else perso.cambiarAlineacion(alineacion, false)
                        } catch (e: Exception) {
                            ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            false
                        }
                    }
                    ACCION_CREAR_GRUPO_MOB_CON_PIEDRA -> try {
                        if (objUsar == null) {
                            ENVIAR_Im_INFORMACION(perso, "14|43")
                            return false
                        }
                        val enArena = _args.split(",".toRegex()).toTypedArray()[1].equals("true", ignoreCase = true)
                        if (enArena && !perso.mapa.esArena()) {
                            ENVIAR_Im_INFORMACION(perso, "113")
                            return false
                        }
                        if (perso.mapa.esMazmorra()) {
                            ENVIAR_Im_INFORMACION(perso, "113")
                            return false
                        }
                        val condicion = "Mi=" + perso.Id
                        val grupoMob = perso.mapa.addGrupoMobPorTipo(
                            perso.celda.id, objUsar.strGrupoMob(),
                            TipoGrupo.SOLO_UNA_PELEA, condicion, null
                        )
                        grupoMob!!.condUnirsePelea = ""
                        grupoMob.startTiempoCondicion()
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_INICIAR_PELEA_DOPEUL -> try {
                        val mobDopeul = _args.toInt()
                        val nivel = Constantes.getNivelDopeul(perso.nivel)
                        val grupoMob = GrupoMob(
                            perso.mapa, perso.celda.id, mobDopeul.toString() + "," + nivel + ","
                                    + nivel, TipoGrupo.SOLO_UNA_PELEA, ""
                        )
                        perso.mapa.iniciarPelea(
                            perso, null, perso.celda.id, (-1).toShort(),
                            Constantes.PELEA_TIPO_PVM_NO_ESPADA, grupoMob
                        )
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_INICIAR_PELEA_VS_MOBS, ACCION_INCIAR_PELEA_VS_MOBS_NO_ESPADA ->  // iniciar pelea vs 1 mob no espada mobID,mobnivel|mobID,mobNivel|.....
                        try {
                            if (objUsar != null) {
                                if (perso.mapa.esMazmorra()) {
                                    ENVIAR_Im_INFORMACION(perso, "113")
                                    return false
                                }
                            }
                            val mobGrupo = StringBuilder()
                            for (mobYNivel in _args.split(Pattern.quote(";").toRegex()).toTypedArray()) {
                                val mobONivel = mobYNivel.split(",".toRegex()).toTypedArray()
                                val mobID = mobONivel[0].toInt()
                                mobGrupo.append(mobID)
                                if (mobONivel.size > 1) {
                                    mobGrupo.append(",").append(mobONivel[1].toInt())
                                }
                                if (mobONivel.size > 2) {
                                    mobGrupo.append(",").append(mobONivel[2].toInt())
                                }
                                mobGrupo.append(";")
                            }
                            val grupoMob = GrupoMob(
                                perso.mapa, (perso.celda.id + 1).toShort(), mobGrupo
                                    .toString(), TipoGrupo.SOLO_UNA_PELEA, ""
                            )
                            if (grupoMob.cantMobs <= 0) {
                                return false
                            }
                            val tipoPelea =
                                if (ACCION_INICIAR_PELEA_VS_MOBS == _id) Constantes.PELEA_TIPO_PVM else Constantes.PELEA_TIPO_PVM_NO_ESPADA
                            perso.mapa.iniciarPelea(perso, null, perso.celda.id, (-1).toShort(), tipoPelea, grupoMob)
                        } catch (e: Exception) {
                            ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    ACCION_RESETEAR_STATS -> try {
                        var todo = false
                        try {
                            todo = _args.equals("true", ignoreCase = true)
                        } catch (ignored: Exception) {
                        }
                        perso.resetearStats(todo)
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_OLVIDAR_HECHIZO_PANEL -> try {
                        try {
                            return perso.olvidarHechizo(_args.toInt(), false, true)
                        } catch (e: Exception) {
                            perso.setOlvidandoHechizo(true)
                            ENVIAR_SF_OLVIDAR_HECHIZO('+', perso)
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_USAR_LLAVE -> try { // tpmap,tpcelda,objnecesario,mapanecesario
                        val nuevoMapaID = _args.split(",".toRegex()).toTypedArray()[0].toShort()
                        val nuevaCeldaID = _args.split(",".toRegex()).toTypedArray()[1].toShort()
                        var objNecesario = 0
                        try {
                            objNecesario = _args.split(",".toRegex()).toTypedArray()[2].toInt()
                        } catch (ignored: Exception) {
                        }
                        var mapaNecesario = 0
                        try {
                            mapaNecesario = _args.split(",".toRegex()).toTypedArray()[3].toInt()
                        } catch (ignored: Exception) {
                        }
                        if (objNecesario == 0) {
                            perso.teleport(nuevoMapaID, nuevaCeldaID)
                        } else if (objNecesario > 0) {
                            if (mapaNecesario == 0) {
                                if (perso.tenerYEliminarObjPorModYCant(objNecesario, 1)) {
                                    ENVIAR_Ow_PODS_DEL_PJ(perso)
                                    perso.teleport(nuevoMapaID, nuevaCeldaID)
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14|45")
                                }
                            } else if (mapaNecesario > 0) {
                                if (perso.mapa.id.toInt() == mapaNecesario) {
                                    if (perso.tenerYEliminarObjPorModYCant(objNecesario, 1)) {
                                        ENVIAR_Ow_PODS_DEL_PJ(perso)
                                        perso.teleport(nuevoMapaID, nuevaCeldaID)
                                    } else {
                                        ENVIAR_Im_INFORMACION(perso, "14|45")
                                    }
                                } else if (perso.mapa.id.toInt() != mapaNecesario) {
                                    ENVIAR_Im_INFORMACION(perso, "113")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_QUITAR_HONOR -> try {
                        if (perso.alineacion == Constantes.ALINEACION_NEUTRAL) {
                            ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        perso.addHonor(_args.toInt())
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_EXP_OFICIO -> try {
                        val oficioID = _args.split(",".toRegex()).toTypedArray()[0].toInt()
                        val xp = _args.split(",".toRegex()).toTypedArray()[1].toInt()
                        if (perso.getStatOficioPorID(oficioID) == null) {
                            ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        ENVIAR_Im_INFORMACION(perso, "017;$xp~$oficioID")
                        perso.getStatOficioPorID(oficioID).addExperiencia(perso, xp, 0)
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_TELEPORT_CASA -> try {
                        val casa = Mundo.getCasaDePj(perso.Id)
                        if (objUsar == null || casa == null) {
                            ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        if (casa.actParametros) {
                            ENVIAR_Im_INFORMACION(perso, "126")
                            return false
                        }
                        perso.teleport(casa.mapaIDDentro, casa.celdaIDDentro)
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_PANEL_CASA_GREMIO -> ENVIAR_gUT_PANEL_CASA_GREMIO(perso)
                    ACCION_DAR_QUITAR_PUNTOS_HECHIZO -> try {
                        perso.addPuntosHechizos(_args.toInt())
                        ENVIAR_Im_INFORMACION(perso, "016;" + _args.toInt())
                        ENVIAR_Ak_KAMAS_PDV_EXP_PJ(perso)
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_QUITAR_ENERGIA -> try {
                        var valor = 0
                        val s = _args.split(",".toRegex()).toTypedArray()
                        valor = if (s.size == 1) {
                            s[0].toInt()
                        } else {
                            getRandomInt(s[0].toInt(), s[1].toInt())
                        }
                        perso.addEnergiaConIm(valor, true)
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_EXPERIENCIA -> try {
                        var valor = 0
                        val s = _args.split(",".toRegex()).toTypedArray()
                        valor = if (s.size == 1) {
                            s[0].toInt()
                        } else {
                            getRandomInt(s[0].toInt(), s[1].toInt())
                        }
                        perso.addExperiencia(valor.toLong(), true)
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_OLVIDAR_OFICIO -> {
                        return try {
                            val oficio = _args.toInt()
                            if (oficio < 1) {
                                ENVIAR_BN_NADA(objetivo)
                                return false
                            }
                            perso.olvidarOficio(oficio)
                        } catch (e: Exception) {
                            ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            false
                        }
                    }
                    ACCION_CAMBIAR_GFX -> try {
                        val gfxID = _args.toShort()
                        if (gfxID <= 0) {
                            ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        perso.setGfxID(gfxID)
                        perso.refrescarEnMapa()
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DEFORMAR -> try {
                        perso.setGfxID((perso.getClaseID(true) * 10 + perso.sexo).toShort())
                        perso.refrescarEnMapa()
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_PANEL_CERCADO_GREMIO -> ENVIAR_gUF_PANEL_CERCADOS_GREMIO(perso)
                    ACCION_SUBIR_BAJAR_MONTURA -> {
                        perso.subirBajarMontura(false)
                        return false
                    }
                    ACCION_REFRESCAR_MOBS -> perso.mapa.refrescarGrupoMobs()
                    ACCION_CAMBIAR_CLASE -> {
                        return try {
                            if (perso.encarnacionN != null) {
                                ENVIAR_BN_NADA(perso)
                                return false
                            }
                            perso.cambiarClase(_args.toByte())
                        } catch (e: Exception) {
                            ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            false
                        }
                    }
                    ACCION_AUMENTAR_RESETS -> {
                        return try {
                            if (perso.encarnacionN != null) {
                                ENVIAR_BN_NADA(perso)
                                return false
                            }
                            if (perso.estaDisponible(false, false)) {
                                ENVIAR_BN_NADA(perso)
                                return false
                            }
                            perso.aumentarReset()
                        } catch (e: Exception) {
                            ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            false
                        }
                    }
                    ACCION_OBJETO_BOOST -> try {
                        val args = _args.split(",".toRegex()).toTypedArray()
                        val nuevo = Mundo.getObjetoModelo(args[0].toInt())?.crearObjeto(
                            1,
                            Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM
                        )
                        if (nuevo != null) {
                            if (nuevo.posicion < 20 || nuevo.posicion > 25) {
                                return false
                            }
                        }
                        if (args.size > 1) {
                            val stats = nuevo?.convertirStatsAString(true)
                            if (nuevo != null) {
                                if (stats != null) {
                                    nuevo.convertirStringAStats((if (stats.isEmpty()) "" else "$stats,") + args[1])
                                }
                            }
                        }
                        if (nuevo != null) {
                            if (perso.getObjPosicion(nuevo.posicion) != null) {
                                perso.borrarOEliminarConOR(perso.getObjPosicion(nuevo.posicion).id, true)
                            }
                        }
                        perso.addObjetoConOAKO(nuevo, true)
                        ENVIAR_Ow_PODS_DEL_PJ(perso)
                        ENVIAR_As_STATS_DEL_PJ(perso)
                        if (nuevo != null) {
                            if (nuevo.getParamStatTexto(Constantes.STAT_PERSONAJE_SEGUIDOR, 3).isNotEmpty()) {
                                perso.refrescarEnMapa()
                            }
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_CAMBIAR_SEXO -> try {
                        perso.cambiarSexo()
                        perso.deformar()
                        CAMBIAR_SEXO_CLASE(perso)
                        perso.refrescarEnMapa()
                    } catch (ignored: Exception) {
                    }
                    ACCION_CAMBIAR_COLOR -> try {
                        val a: GestorSalida? = null
                        a!!.ENVIAR_bC_CAMBIAR_COLOR(perso)
                    } catch (ignored: Exception) {
                    }
                    ACCION_ESTATUAS_ASTRUB -> try {
                        val c: Constantes? = null
                        val coord = Constantes.getMapaInicioAstrub(perso.getClaseID(true).toInt())
                        val argumentos = coord.split(",".toRegex()).toTypedArray()
                        val nuevoMapa = Mundo.getMapa(argumentos[0].toShort())
                        val nuevaCelda = nuevoMapa?.getCelda(argumentos[1].toShort())
                        perso.mapa = nuevoMapa
                        perso.celda = nuevaCelda
                        perso.setPuntoSalvada(coord)
                        perso.teleport(argumentos[0].toShort(), argumentos[1].toShort())
                    } catch (ignored: Exception) {
                    }
                    ACCION_CAMBIAR_NOMBRE -> try {
                        val a: GestorSalida? = null
                        a!!.ENVIAR_AlEr_CAMBIAR_NOMBRE(perso)
                    } catch (ignored: Exception) {
                    }
                    ACCION_PAGAR_PESCAR_KUAKUA -> try {
                        val kamasApostar = _args.toInt()
                        val tempKamas = perso.kamas
                        if (tempKamas < kamasApostar) {
                            ENVIAR_Im_INFORMACION(perso, "1128;$kamasApostar")
                            return false
                        }
                        perso.addKamas(kamasApostar.toLong(), true, true)
                        perso.pescarKuakua = true
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_RULETA_JALATO -> try {
                        val precio = _args.split(",".toRegex()).toTypedArray()[0].toInt().toLong()
                        var tutorial = _args.split(",".toRegex()).toTypedArray()[1].toInt()
                        if (tutorial == 30) {
                            val aleatorio = getRandomInt(1, 200)
                            if (aleatorio == 100) {
                                tutorial = 31
                            }
                        }
                        val tuto = Mundo.getTutorial(tutorial)
                        if (tuto == null || precio < 0) {
                            return false
                        }
                        if (perso.kamas < precio) {
                            ENVIAR_Im_INFORMACION(perso, "182")
                            return false
                        }
                        perso.addKamas(-precio, true, true)
                        if (tuto.inicio != null) {
                            tuto.inicio!!.realizarAccion(perso, null, -1, (-1).toShort())
                        }
                        Thread.sleep(1500)
                        ENVIAR_TC_CARGAR_TUTORIAL(perso, tutorial)
                        perso.tutorial = tuto
                        perso.setOcupado(true)
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_ORNAMENTO -> try {
                        perso.addOrnamento(_args.toInt())
                        perso.ornamento = _args.toInt()
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_TELEPORT_CELDA_MISMO_MAPA -> try {
                        perso.celda = perso.mapa.getCelda(_args.toShort())
                        ENVIAR_GM_REFRESCAR_PJ_EN_MAPA_SIN_HUMO(perso.mapa, perso)
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_GANAR_RULETA_JALATO -> try {
                        ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(
                            "El anutrofado ganador de la RULETA DEL JALATO es: "
                                    + perso.nombre + ", demosle un fuerte aplauso!!!",
                            "Vous avez gagnez en jouant a la roulette du bouftou :  " + perso.nombre + ", félicitations !"
                        )
                        perso.addKamas(AtlantaMain.KAMAS_RULETA_JALATO.toLong(), true, true)
                        AtlantaMain.KAMAS_RULETA_JALATO = 10000
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_KAMAS_RULETA_JALATO -> AtlantaMain.KAMAS_RULETA_JALATO += 1000
                    ACCION_DAR_SET_OBJETOS -> try {
                        for (s in _args.split(";".toRegex()).toTypedArray()) {
                            val OS = Mundo.getObjetoSet(s.toInt())
                            if (OS == null) {
                                ENVIAR_BN_NADA(objetivo)
                                return false
                            }
                            for (objMod in OS.objetosModelos) {
                                objetivo.addObjIdentAInventario(
                                    objMod.crearObjeto(
                                        1, Constantes.OBJETO_POS_NO_EQUIPADO,
                                        CAPACIDAD_STATS.RANDOM
                                    ), false
                                )
                            }
                        }
                        ENVIAR_Ow_PODS_DEL_PJ(objetivo)
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_CONFIRMAR_CUMPLIO_OBJETIVO_MISION ->  // confirma si se cumplio el objetivo de la mision
                        try {
                            for (s in _args.split(";".toRegex()).toTypedArray()) {
                                for (mision in perso.misiones) {
                                    if (mision.estaCompletada()) {
                                        continue
                                    }
                                    for ((key, value) in mision.objetivos) {
                                        if (value == Mision.ESTADO_COMPLETADO) {
                                            continue
                                        }
                                        val objMod = Mundo.getMisionObjetivoModelo(key)
                                        if (objMod != null) {
                                            if (objMod.iD != s.toInt()) {
                                                continue
                                            }
                                        }
                                        perso.confirmarObjetivo(mision, objMod, perso, null, false, 0)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    ACCION_DAR_MISION -> try {
                        if (perso.tieneMision(_args.toInt())) {
                            ENVIAR_BN_NADA(objetivo, "TIENE MISION")
                            return false
                        }
                        val misionMod = Mundo.getMision(_args.toInt())
                        if (misionMod != null) {
                            if (misionMod.etapas.isEmpty()) {
                                ENVIAR_BN_NADA(objetivo, "ETAPAS VACIAS")
                                return false
                            }
                        }
                        if (misionMod != null) {
                            if (Mundo.getEtapa(misionMod.etapas[0])!!.getObjetivosPorNivel(0)!!.isEmpty()) {
                                ENVIAR_BN_NADA(objetivo, "OBJETIVOS VACIOS")
                                return false
                            }
                        }
                        perso.addNuevaMision(misionMod)
                        ENVIAR_Im_INFORMACION(perso, "054;" + _args.toInt())
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_AGREGAR_MOB_ALBUM -> try {
                        for (s in _args.split(";".toRegex()).toTypedArray()) {
                            perso.addCardMob(s.toInt())
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(perso, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_TITULO -> try {
                        val titulo = _args.toByte()
                        if (titulo < 1) {
                            ENVIAR_BN_NADA(perso)
                            return false
                        }
                        perso.addTitulo(titulo.toInt(), -1)
                        perso.refrescarEnMapa()
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_RECOMPENSA_DOPEUL -> {
                        try {
                            var exp = 0
                            if (perso.nivel < 20) {
                                exp = 6000
                            } else if (perso.nivel <= 40) {
                                exp = 16000
                            } else if (perso.nivel <= 60) {
                                exp = 38000
                            } else if (perso.nivel <= 80) {
                                exp = 68000
                            } else if (perso.nivel <= 100) {
                                exp = 114000
                            } else if (perso.nivel <= 120) {
                                exp = 180000
                            } else if (perso.nivel <= 140) {
                                exp = 260000
                            } else if (perso.nivel <= 160) {
                                exp = 360000
                            } else if (perso.nivel <= 180) {
                                exp = 490000
                            } else if (perso.nivel <= 200) {
                                exp = 640000
                            }
                            perso.addExperiencia(exp.toLong(), true)
                        } catch (e: Exception) {
                            ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                        return false
                    }
                    ACCION_VERIFICA_MISION_ALMANAX -> return perso.cumplirMisionAlmanax()
                    ACCION_DAR_MISION_PVP_CON_PERGAMINOS -> try {
                        var cantidad = 1
                        try {
                            cantidad = _args.toInt()
                        } catch (ignored: Exception) {
                        }
                        if (objetivo === perso) {
                            return false
                        }
                        val nombreVict = objetivo.nombre
                        val pergamino = Mundo.getObjetoModelo(10085)?.crearObjeto(
                            cantidad, Constantes.OBJETO_POS_NO_EQUIPADO,
                            CAPACIDAD_STATS.RANDOM
                        )
                        if (pergamino != null) {
                            pergamino.addStatTexto(Constantes.STAT_MISION, "0#0#0#$nombreVict")
                        }
                        if (pergamino != null) {
                            pergamino.addStatTexto(
                                Constantes.STAT_RANGO,
                                "0#0#" + Integer.toHexString(objetivo.getGradoAlineacion())
                            )
                        }
                        if (pergamino != null) {
                            pergamino.addStatTexto(
                                Constantes.STAT_NIVEL,
                                "0#0#" + Integer.toHexString(objetivo.getNivel())
                            )
                        }
                        if (pergamino != null) {
                            pergamino.addStatTexto(
                                Constantes.STAT_ALINEACION,
                                "0#0#" + Integer.toHexString(objetivo.getAlineacion().toInt())
                            )
                        }
                        perso.addObjetoConOAKO(pergamino, true)
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(perso, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_MISION_PVP -> try {
                        if (perso.nivel < AtlantaMain.NIVEL_MINIMO_PARA_PVP) {
                            return false
                        }
                        if (perso.alineacion == Constantes.ALINEACION_NEUTRAL) {
                            ENVIAR_Im_INFORMACION(perso, "134")
                            return false
                        }
                        val mision = perso.misionPVP
                        if (mision != null) {
                            if (System.currentTimeMillis() - mision.tiempoInicio < AtlantaMain.MINUTOS_MISION_PVP * 60 * 1000) {
                                if (perso.cuenta.idioma.equals("fr", ignoreCase = true)) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "<b>[Thomas Sacre]</b> Tu viens de terminer un contrat, tu dois attendre 10 minutes avant de te relancer dans ta quête de meurtre.",
                                        "000000"
                                    )
                                } else {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "<b>Cacería:</b><br>Usted recibió un objetivo hace poco, por ahora debes descansar.",
                                        "e78800"
                                    )
                                }
                                return false
                            }
                        }
                        if (!perso.alasActivadas()) {
                            perso.botonActDesacAlas('+')
                        }
                        var victima: Personaje? = null
                        val victimas = ArrayList<Personaje>()
                        for (temp in Mundo.PERSONAJESONLINE) {
                            if (temp.nivel < AtlantaMain.NIVEL_MINIMO_PARA_PVP) {
                                continue
                            }
                            if (temp === perso || temp.alineacion == perso.alineacion || temp
                                    .alineacion == Constantes.ALINEACION_NEUTRAL || !temp.alasActivadas()
                            ) {
                                continue
                            }
                            if (!AtlantaMain.ES_LOCALHOST) {
                                if (temp.nombre.equals(
                                        perso.ultMisionPVP,
                                        ignoreCase = true
                                    ) || temp.cuenta.admin > 0
                                ) {
                                    continue
                                }
                            }
                            if (!AtlantaMain.PARAM_PERMITIR_MULTICUENTA_PELEA_PVP) {
                                if (temp.cuenta.actualIP == perso.cuenta.actualIP) {
                                    continue
                                }
                            }
                            if (perso.nivel + AtlantaMain.RANGO_NIVEL_PVP >= temp.nivel && perso.nivel
                                - AtlantaMain.RANGO_NIVEL_PVP <= temp.nivel
                            ) {
                                victimas.add(temp)
                            }
                        }
                        if (victimas.isEmpty()) {
                            if (perso.cuenta.idioma.equals("fr", ignoreCase = true)) {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "<b>Cacería</b> Je ne trouve pas de victime à ta hauteur, reviens plus tard.",
                                    "da6c00"
                                )
                            } else {
                                ENVIAR_cs_CHAT_MENSAJE(
                                    perso,
                                    "<b>Cacería:</b><br>No has tenido suerte, regresa más tarde.",
                                    "da6c00"
                                )
                            }
                            return false
                        }
                        victima = victimas[getRandomInt(0, victimas.size - 1)]
                        val nombreVict = victima.nombre
                        if (perso.cuenta.idioma.equals("fr", ignoreCase = true)) {
                            ENVIAR_cs_CHAT_MENSAJE(
                                perso, "<b>NUeva Cacería</b> Ta victime est : $nombreVict.",
                                "da6c00"
                            )
                        } else {
                            ENVIAR_cs_CHAT_MENSAJE(
                                perso,
                                "<b>Nueva Cacería</b><br><b>Nombre:</b> " + nombreVict + "<br><b>Nivel:</b> " + victima.getNivel() + "<br><b>Grado:</b> " + victima.gradoAlineacion + "<br><b>Buena Suerte</b>.",
                                "da6c00"
                            )
                        }
                        val recompensaExp = getXPMision(victima.nivel)
                        val pergRec = 9920
                        val misionPVP = MisionPVP(
                            System.currentTimeMillis(), nombreVict, AtlantaMain.MISION_PVP_KAMAS.toLong(),
                            recompensaExp, Constantes.getCraneoPorClase(victima.getClaseID(true).toInt()), pergRec
                        )
                        val pergamino = Mundo.getObjetoModelo(10085)?.crearObjeto(
                            25, Constantes.OBJETO_POS_NO_EQUIPADO,
                            CAPACIDAD_STATS.RANDOM
                        )
                        val pergRecc = Mundo.getObjetoModelo(9917)?.crearObjeto(
                            1, Constantes.OBJETO_POS_NO_EQUIPADO,
                            CAPACIDAD_STATS.RANDOM
                        )
                        val ordenSacre = Mundo.getObjetoModelo(10621)?.crearObjeto(
                            1, Constantes.OBJETO_POS_NO_EQUIPADO,
                            CAPACIDAD_STATS.RANDOM
                        )
                        val mission = "449"
                        val misionMod = Mundo.getMision(mission.toShort().toInt())
                        perso.addNuevaMision(misionMod)
                        ENVIAR_Im_INFORMACION(perso, "054;$mission")
                        ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(
                            perso, victima.mapa.x.toString() + "|" + victima.mapa
                                .y
                        )
                        if (pergamino != null) {
                            pergamino.addStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE, stringFechaIntercambiable(365))
                            pergamino.addStatTexto(Constantes.STAT_MISION, "0#0#0#$nombreVict")
                            pergamino.addStatTexto(
                                Constantes.STAT_RANGO,
                                "0#0#" + Integer.toHexString(victima.gradoAlineacion)
                            )
                            pergamino.addStatTexto(
                                Constantes.STAT_NIVEL,
                                "0#0#" + Integer.toHexString(victima.nivel)
                            )
                            pergamino.addStatTexto(
                                Constantes.STAT_ALINEACION,
                                "0#0#" + Integer.toHexString(victima.alineacion.toInt())
                            )
                        }
                        perso.addObjetoConOAKO(pergamino, true)
                        if (ordenSacre != null) {
                            ordenSacre.addStatTexto(
                                Constantes.STAT_INTERCAMBIABLE_DESDE,
                                stringFechaIntercambiable(365)
                            )
                        }
                        if (pergRecc != null) {
                            pergRecc.addStatTexto(Constantes.STAT_MISION, "0#0#0#$nombreVict")
                            pergRecc.addStatTexto(
                                Constantes.STAT_RANGO,
                                "0#0#" + Integer.toHexString(victima.gradoAlineacion)
                            )
                            pergRecc.addStatTexto(
                                Constantes.STAT_NIVEL,
                                "0#0#" + Integer.toHexString(victima.nivel)
                            )
                            pergRecc.addStatTexto(
                                Constantes.STAT_ALINEACION,
                                "0#0#" + Integer.toHexString(victima.alineacion.toInt())
                            )
                        }
                        perso.addObjetoConOAKO(pergRecc, true)
                        perso.addObjetoConOAKO(ordenSacre, true)
                        perso.ultMisionPVP = nombreVict
                        perso.misionPVP = misionPVP
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_CUMPLIO_OBJETIVO_PVP -> try {
                        val accionID = 43
                        val args2 = "2971"
                        realizar_Accion_Estatico(accionID, args2, perso, null, -1, (-1).toShort())
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_GEOPOSICION_MISION_PVP -> try {
                        if (perso.pelea != null || perso.esFantasma() || objUsar == null) {
                            ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        val victima = Mundo.getPersonajePorNombre(objUsar.getParamStatTexto(Constantes.STAT_MISION, 4))
                        if (victima == null || !victima.enLinea()) {
                            ENVIAR_Im_INFORMACION(perso, "1211")
                            return false
                        }
                        if (victima.esFantasma()) {
                            ENVIAR_Im_INFORMACION(perso, "1OBJETIVE_GHOST")
                            return false
                        }
                        if (perso.misionPVP == null || !perso.misionPVP.nombreVictima.equals(
                                victima
                                    .nombre, ignoreCase = true
                            )
                        ) {
                            var recompensaExp: Long = 0
                            if (objUsar.tieneStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE)) {
                                recompensaExp = getXPMision(victima.nivel)
                            }
                            val pergRec = 0
                            perso.misionPVP = MisionPVP(
                                0, victima.nombre, objUsar.getStatValor(
                                    Constantes.STAT_GANAR_KAMAS
                                ).toLong(), recompensaExp, victima.getClaseID(true).toInt(), pergRec
                            )
                        }
                        ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(
                            perso, victima.mapa.x.toString() + "|" + victima.mapa
                                .y
                        )
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_TELEPORT_MISION_PVP -> try {
                        if (perso.pelea != null || perso.esFantasma() || objUsar == null) {
                            ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        val victima = Mundo.getPersonajePorNombre(objUsar.getParamStatTexto(989, 4))
                        if (victima == null || !victima.enLinea()) {
                            ENVIAR_Im_INFORMACION(perso, "1211")
                            return false
                        }
                        if (victima.esFantasma()) {
                            ENVIAR_Im_INFORMACION(perso, "1OBJETIVE_GHOST")
                            return false
                        }
                        if (!victima.alasActivadas()) {
                            ENVIAR_Im_INFORMACION(perso, "1195")
                            return false
                        }
                        if (victima.pelea != null) {
                            ENVIAR_Im_INFORMACION(perso, "1OBJETIVE_IN_FIGHT")
                            return false
                        }
                        if (victima.huir) {
                            if (System.currentTimeMillis() - victima.tiempoAgre > 10000) {
                                victima.huir = true
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "1OBJETIVE_IN_FIGHT")
                                return false
                            }
                        }
                        val mapas = arrayOf<Short>(4422, 7810, 952, 1887, 833)
                        val mapa = mapas[getRandomInt(0, 4)]
                        perso.teleport(mapa, 399.toShort())
                        victima.teleport(mapa, 194.toShort())
                        perso.huir = false
                        victima.huir = false
                        perso.tiempoAgre = System.currentTimeMillis()
                        victima.tiempoAgre = System.currentTimeMillis()
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_CONFIRMA_CUMPLIO_MISION -> try {
                        for (s in _args.split(";".toRegex()).toTypedArray()) {
                            for (mision in perso.misiones) {
                                if (mision.estaCompletada()) {
                                    continue
                                }
                                if (mision.iDModelo != s.toInt()) {
                                    continue
                                }
                                for ((key, value) in mision.objetivos) {
                                    if (value == Mision.ESTADO_COMPLETADO) {
                                        continue
                                    }
                                    val objMod = Mundo.getMisionObjetivoModelo(key)
                                    perso.confirmarObjetivo(mision, objMod, perso, null, false, 0)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_BOOST_FULL_STATS -> try {
                        val args = _args.toInt()
                        if (args < 10 || args > 15) {
                            return false
                        }
                        perso.boostStat2(args, perso.capital)
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_PAGAR_PARA_REALIZAR_ACCION -> try {
                        val sep = _args.split(Pattern.quote("|").toRegex()).toTypedArray()
                        val precio = sep[0].toInt()
                        if (perso.kamas >= precio) {
                            perso.addKamas(-precio.toLong(), true, true)
                            var args = ""
                            try {
                                args = sep[1].split(";".toRegex(), 2).toTypedArray()[1]
                            } catch (ignored: Exception) {
                            }
                            realizar_Accion_Estatico(
                                sep[1].split(";".toRegex()).toTypedArray()[0].toInt(),
                                args,
                                perso,
                                null,
                                -1,
                                (-1).toShort()
                            )
                        } else {
                            ENVIAR_Im_INFORMACION(perso, "182")
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_SOLICITAR_OBJETOS_PARA_DAR_OTROS ->  // quita unos objetos para dar otros , pedir, solicitar
                        try {
                            val t = _args.split(Pattern.quote("|").toRegex()).toTypedArray()
                            var args = t[0]
                            if (t.size < 2) {
                                if (objUsar == null) {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                    return false
                                }
                            } else {
                                args = t[1]
                                for (s in t[0].split(";".toRegex()).toTypedArray()) {
                                    val id = s.split(",".toRegex()).toTypedArray()[0].toInt()
                                    val cant = s.split(",".toRegex()).toTypedArray()[1].toInt()
                                    if (!perso.tieneObjPorModYCant(id, cant)) {
                                        ENVIAR_Im_INFORMACION(perso, "14")
                                        return false
                                    }
                                }
                                for (s in t[0].split(";".toRegex()).toTypedArray()) {
                                    val id = s.split(",".toRegex()).toTypedArray()[0].toInt()
                                    val cant = s.split(",".toRegex()).toTypedArray()[1].toInt()
                                    if (perso.tenerYEliminarObjPorModYCant(id, cant)) {
                                        ENVIAR_Im_INFORMACION(perso, "022;$cant~$id")
                                    }
                                }
                            }
                            for (s in args.split(";".toRegex()).toTypedArray()) {
                                val id = s.split(",".toRegex()).toTypedArray()[0].toInt()
                                val cant = s.split(",".toRegex()).toTypedArray()[1].toInt()
                                var max = false
                                try {
                                    if (s.split(",".toRegex()).toTypedArray().size > 2) max =
                                        s.split(",".toRegex()).toTypedArray()[2] == "1"
                                } catch (ignored: Exception) {
                                }
                                val tempObjMod = Mundo.getObjetoModelo(id)
                                if (tempObjMod == null) {
                                    ENVIAR_BN_NADA(objetivo, "BUG ACCION $_id idObjMod $id")
                                    continue
                                }
                                if (cant > 0) {
                                    perso.addObjIdentAInventario(
                                        tempObjMod.crearObjeto(
                                            cant, Constantes.OBJETO_POS_NO_EQUIPADO,
                                            CAPACIDAD_STATS.RANDOM
                                        ), max
                                    )
                                    ENVIAR_Im_INFORMACION(perso, "021;$cant~$id")
                                }
                            }
                            ENVIAR_Ow_PODS_DEL_PJ(perso)
                        } catch (e: Exception) {
                            ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    ACCION_REVIVIR, ACCION_REVIVIR2 -> {
                        if (objetivo.pelea != null) {
                            return false
                        }
                        objetivo.revivir(true)
                    }
                    ACCION_ABRIR_DOCUMENTO -> try {
                        val documento = _args.split(";".toRegex()).toTypedArray()[0]
                        val idMision = _args.split(";".toRegex()).toTypedArray()[1].toInt()
                        ENVIAR_dC_ABRIR_DOCUMENTO(perso, documento)
                        perso.idMisionDocumento = idMision
                    } catch (e: Exception) {
                        ENVIAR_dC_ABRIR_DOCUMENTO(perso, _args)
                    }
                    ACCION_DAR_SET_OBJETOS_POR_FICHAS -> try {
                        val idSet = _args.split(",".toRegex()).toTypedArray()[0].toInt()
                        val fichas = _args.split(",".toRegex()).toTypedArray()[1].toInt()
                        val OS = Mundo.getObjetoSet(idSet)
                        if (OS == null) {
                            ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        if (perso.tenerYEliminarObjPorModYCant(1749, fichas)) {
                            for (objM in OS.objetosModelos) {
                                objetivo.addObjIdentAInventario(
                                    objM.crearObjeto(
                                        1, Constantes.OBJETO_POS_NO_EQUIPADO,
                                        CAPACIDAD_STATS.RANDOM
                                    ), false
                                )
                                ENVIAR_Im_INFORMACION(perso, "021;1~" + objM.id)
                            }
                        } else {
                            ENVIAR_Im_INFORMACION(perso, "14|43")
                        }
                        ENVIAR_Ow_PODS_DEL_PJ(objetivo)
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_REALIZAR_ACCION_PJS_EN_MAPA_POR_ALINEACION_Y_DISTANCIA ->  // personajes en el mapa
                        try {
                            val t = _args.split(Pattern.quote("|").toRegex()).toTypedArray()
                            var tipoAlin = 0
                            try {
                                tipoAlin = t[0].toInt()
                            } catch (ignored: Exception) {
                            }
                            var dist = 1000
                            try {
                                dist = t[1].toInt()
                            } catch (ignored: Exception) {
                            }
                            val idAccion = t[2].split(";".toRegex()).toTypedArray()[0].toInt()
                            var args2 = ""
                            try {
                                args2 = t[2].split(";".toRegex(), 2).toTypedArray()[1]
                            } catch (ignored: Exception) {
                            }
                            val mapa = perso.mapa
                            val celdaPerso = perso.celda.id
                            val aplicar = ArrayList<Personaje>()
                            for (o in mapa.arrayPersonajes!!) {
                                if (tipoAlin == 0) { // pasan normal
                                } else {
                                    if (o.alineacion == Constantes.ALINEACION_NEUTRAL) {
                                        continue
                                    }
                                    if (tipoAlin == 1 && o.alineacion != perso.alineacion) {
                                        continue
                                    } else if (tipoAlin == 2 && o.alineacion == perso.alineacion) {
                                        continue
                                    }
                                }
                                if (o.mapa.id != mapa.id) {
                                    continue
                                }
                                if (Camino.distanciaDosCeldas(mapa, o.celda.id, celdaPerso) > dist) {
                                    continue
                                }
                                aplicar.add(o)
                            }
                            for (o in aplicar) {
                                realizar_Accion_Estatico(idAccion, args2, o, null, -1, (-1).toShort())
                            }
                        } catch (e: Exception) {
                            ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    ACCION_LIBERAR_TUMBA -> {
                        if (objetivo.pelea != null) {
                            return false
                        }
                        if (objetivo.esTumba()) {
                            objetivo.convertirseFantasma()
                        }
                    }
                    ACCION_AGREGAR_PJ_LIBRO_ARTESANOS -> {
                        try {
                            val idOficio = _args.toInt()
                            for (SO in perso.statsOficios.values) {
                                if (SO.oficio.id == idOficio) {
                                    SO.libroArtesano = true
                                    ENVIAR_Ej_AGREGAR_LIBRO_ARTESANO(perso, "+$idOficio")
                                    return false
                                }
                            }
                        } catch (ignored: Exception) {
                        }
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_ACTIVAR_CELDAS_INTERACTIVAS -> try {
                        for (s in _args.split(";".toRegex()).toTypedArray()) {
                            var m = perso.mapa.id
                            var c: Short = -1
                            val split = s.split(",".toRegex()).toTypedArray()
                            try {
                                val cm = split[0]
                                c = cm.split("m".toRegex()).toTypedArray()[0].toShort()
                                m = cm.split("m".toRegex()).toTypedArray()[1].toShort()
                            } catch (ignored: Exception) {
                            }
                            var bAnimacionMovimiento = false // conGDF
                            var milisegundos: Long = 30000
                            try {
                                bAnimacionMovimiento = split[1] == "1"
                            } catch (ignored: Exception) {
                            }
                            try {
                                milisegundos = split[2].toLong()
                            } catch (ignored: Exception) {
                            }
                            val mapa = Mundo.getMapa(m)
                            if (mapa == null) {
                                ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id MAPA $m ES NULO")
                                return false
                            }
                            val celda = mapa.getCelda(c)
                            if (celda == null) {
                                ENVIAR_BN_NADA(
                                    objetivo, "EXCEPTION ACCION " + _id + " MAPA " + m + " CELDA " + c
                                            + " ES NULO"
                                )
                                return false
                            }
                            celda.activarCelda(bAnimacionMovimiento, milisegundos)
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_QUITAR_EMOTE -> try {
                        for (s in _args.split(",".toRegex()).toTypedArray()) {
                            var emote = s.toByte()
                            if (emote < 0) {
                                emote = abs(emote.toInt()).toByte()
                                if (perso.borrarEmote(emote)) {
                                    ENVIAR_eR_BORRAR_EMOTE(perso, emote.toInt(), true)
                                }
                            } else if (emote > 0) {
                                if (perso.addEmote(emote)) {
                                    ENVIAR_eA_AGREGAR_EMOTE(perso, emote.toInt(), true)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_CAMBIAR_ROSTRO -> try {
                        perso.cambiarRostro(_args.toByte())
                        ENVIAR_Oa_CAMBIAR_ROPA_MAPA(perso.mapa, perso)
                    } catch (ignored: Exception) {
                    }
                    ACCION_MENSAJE_INFORMACION -> ENVIAR_Im_INFORMACION(perso, _args)
                    ACCION_MENSAJE_PANEL -> ENVIAR_M145_MENSAJE_PANEL_INFORMACION(perso, _args)
                    ACCION_DAR_OBJETOS_DE_LOS_STATS -> {
                        if (objUsar == null) {
                            return false
                        }
                        for (s in objUsar.strDarObjetos().split(";".toRegex()).toTypedArray()) {
                            try {
                                if (s.isEmpty()) {
                                    continue
                                }
                                val id = s.split(",".toRegex()).toTypedArray()[0].toInt()
                                val cant = s.split(",".toRegex()).toTypedArray()[1].toInt()
                                val tempObjMod = Mundo.getObjetoModelo(id)
                                if (tempObjMod == null) {
                                    ENVIAR_BN_NADA(objetivo, "BUG ACCION $_id idObjMod $id")
                                    continue
                                }
                                if (cant > 0) {
                                    perso.addObjIdentAInventario(
                                        tempObjMod.crearObjeto(
                                            cant, Constantes.OBJETO_POS_NO_EQUIPADO,
                                            CAPACIDAD_STATS.RANDOM
                                        ), false
                                    )
                                    ENVIAR_Im_INFORMACION(perso, "021;$cant~$id")
                                }
                            } catch (e: Exception) {
                                ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            }
                        }
                    }
                    ACCION_DAR_ABONO_DIAS -> try {
                        val idInt = _args.toInt()
                        var abono = max(GET_ABONO(objetivo.cuenta.nombre), System.currentTimeMillis())
                        abono += (idInt * 24 * 3600 * 1000).toLong()
                        abono = max(abono, System.currentTimeMillis() - 1000)
                        SET_ABONO(abono, objetivo.cuentaID)
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_ABONO_HORAS -> try {
                        val idInt = _args.toInt()
                        var abono = max(GET_ABONO(objetivo.cuenta.nombre), System.currentTimeMillis())
                        abono += (idInt * 3600 * 1000).toLong()
                        abono = max(abono, System.currentTimeMillis() - 1000)
                        SET_ABONO(abono, objetivo.cuentaID)
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_ABONO_MINUTOS -> try {
                        val idInt = _args.toInt()
                        var abono = max(GET_ABONO(objetivo.cuenta.nombre), System.currentTimeMillis())
                        abono += (idInt * 60 * 1000).toLong()
                        abono = max(abono, System.currentTimeMillis() - 1000)
                        SET_ABONO(abono, objetivo.cuentaID)
                        ENVIAR_Im_INFORMACION(perso, "GRACIAS POR ABONARTE: $idInt MINUTOS")
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_ABONO_SEMANA -> try {
                        val idInt = _args.toInt()
                        var i = 0
                        while (i < idInt) {
                            var abono = max(GET_ABONO(objetivo.cuenta.nombre), System.currentTimeMillis())
                            abono += (7 * 24 * 3600 * 1000).toLong()
                            abono = max(abono, System.currentTimeMillis() - 1000)
                            SET_ABONO(abono, objetivo.cuentaID)
                            i++
                        }
                        ENVIAR_Im_INFORMACION(perso, "GRACIAS POR ABONARTE $idInt SEMANAS")
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_BOLSA_ALEATORIA -> try {
                        val args = _args.split(",".toRegex()).toTypedArray()
                        var nivelmin = -1
                        var nivelmax = -1
                        if (args.size > 3) {
                            nivelmin = args[2].toInt()
                            nivelmax = args[3].toInt()
                        }
                        val tipo = args[1].toInt()
                        val id = args[0].toInt()
                        val lista = ArrayList<ObjetoModelo>()
                        if (nivelmin == -1) {
                            for (a in Mundo.OBJETOS_MODELOS.values) {
                                if (a.tipo.toInt() == tipo && !a.esEtereo()) {
                                    lista.add(a)
                                }
                            }
                        } else {
                            for (a in Mundo.OBJETOS_MODELOS.values) {
                                if (a.tipo.toInt() == tipo && a.nivel >= nivelmin && a.nivel <= nivelmax && !a.esEtereo()) {
                                    lista.add(a)
                                }
                            }
                        }
                        val objetos = StringBuilder()
                        for (a in lista) {
                            if (lista[lista.size - 1] != a) {
                                objetos.append(a.id).append(",1;")
                            } else {
                                objetos.append(a.id).append(",1")
                            }
                        }
                        if (lista.size > 1) {
                            realizar_Accion_Estatico(
                                ACCION_AGREGAR_OBJETO_AZAR,
                                "$id,-1|$objetos",
                                perso,
                                null,
                                -1,
                                (-1).toShort()
                            )
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_BOLSA_ALEATORIA_MAZMORRA -> try {
                        val args = _args.split(",".toRegex()).toTypedArray()
                        val nivelmax = args[2].toInt()
                        val nivelmin = args[1].toInt()
                        val id = args[0].toInt()
                        var tipo = 0
                        if (args.size < 3) {
                            return false
                        }
                        val tipos = ArrayList<Int>()
                        for (i in Constantes.TIPOS_EQUIPABLES) {
                            tipos.add(i)
                        }
                        tipo = tipos[getRandomInt(0, tipos.size - 1)]
                        realizar_Accion_Estatico(
                            ACCION_BOLSA_ALEATORIA,
                            "$id,$tipo,$nivelmin,$nivelmax",
                            perso,
                            null,
                            -1,
                            (-1).toShort()
                        )
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    HACER_MUCHAS_ACCIONES -> try { // Oa codigo de hacer muchas pelotudeces a la vez jaja
                        val sep = _args.split(Pattern.quote("|").toRegex()).toTypedArray()
                        var args = ""
                        for (accion in sep) {
                            try {
                                args = accion.split(";".toRegex(), 2).toTypedArray()[1]
                            } catch (ignored: Exception) {
                            }
                            realizar_Accion_Estatico(
                                accion.split(";".toRegex()).toTypedArray()[0].toInt(),
                                args,
                                perso,
                                null,
                                -1,
                                (-1).toShort()
                            )
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_NIVEL_DE_ORDEN -> try {
                        objetivo.addOrdenNivel(_args.toInt())
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_ORDEN -> try {
                        objetivo.orden = _args.toInt()
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_BORRAR_OBJETO_MODELO -> try {
                        var b = false
                        for (s in _args.split(";".toRegex()).toTypedArray()) {
                            val args = s.split(",".toRegex()).toTypedArray()
                            val id = args[0].toInt()
                            var minutos = 0
                            try {
                                minutos = args[1].toInt()
                            } catch (ignored: Exception) {
                            }
                            val borrados = perso.eliminarPorObjModeloRecibidoDesdeMinutos(id, minutos)
                            if (borrados > 0) {
                                b = true
                                ENVIAR_Im_INFORMACION(perso, "022;$borrados~$id")
                            }
                        }
                        if (b) {
                            ENVIAR_Ow_PODS_DEL_PJ(perso)
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_VERIFICA_STAT_OBJETO_Y_LO_BORRA ->  // borra
                        try {
                            var b = true
                            for (s in _args.split(";".toRegex()).toTypedArray()) {
                                var bb = false
                                val args = s.split(",".toRegex()).toTypedArray()
                                val idObjModelo = args[0].toInt()
                                val llaveID = args[1].toInt()
                                for (obj in perso.objetosTodos) {
                                    if (obj.objModeloID != idObjModelo) {
                                        continue
                                    }
                                    val stats = obj.convertirStatsAString(true).split(",".toRegex()).toTypedArray()
                                    for (st in stats) {
                                        val statID = st.split("#".toRegex()).toTypedArray()[0].toInt(16)
                                        val tempObjetoID = st.split("#".toRegex()).toTypedArray()[3].toInt(16)
                                        if (statID != Constantes.STAT_LLAVE_MAZMORRA) {
                                            continue
                                        }
                                        if (tempObjetoID == llaveID) {
                                            bb = true
                                        }
                                    }
                                    if (bb) {
                                        break
                                    }
                                }
                                b = b and bb
                            }
                            if (b) {
                                for (s in _args.split(";".toRegex()).toTypedArray()) {
                                    val args = s.split(",".toRegex()).toTypedArray()
                                    val id = args[0].toInt()
                                    val objetoID = args[1].toInt()
                                    for (obj in perso.objetosTodos) {
                                        if (obj.objModeloID != id) {
                                            continue
                                        }
                                        val stats = obj.convertirStatsAString(true).split(",".toRegex()).toTypedArray()
                                        val nuevo = StringBuilder()
                                        b = false
                                        for (st in stats) {
                                            if (nuevo.isNotEmpty()) {
                                                nuevo.append(",")
                                            }
                                            val statID = st.split("#".toRegex()).toTypedArray()[0].toInt(16)
                                            val tempObjetoID = st.split("#".toRegex()).toTypedArray()[3].toInt(16)
                                            if (statID == Constantes.STAT_LLAVE_MAZMORRA && tempObjetoID == objetoID) {
                                                b = true
                                            } else {
                                                nuevo.append(st)
                                            }
                                        }
                                        if (b) {
                                            obj.convertirStringAStats(nuevo.toString())
                                            ENVIAR_OCK_ACTUALIZA_OBJETO(perso, obj)
                                            break
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    ACCION_BORRAR_OBJETO_AL_AZAR_PARA_DAR_OTROS -> try {
                        val quitar = _args.split(Pattern.quote("|").toRegex()).toTypedArray()[0].split(";".toRegex())
                            .toTypedArray()
                        val dar = _args.split(Pattern.quote("|").toRegex()).toTypedArray()[1].split(";".toRegex())
                            .toTypedArray()
                        var quito = false
                        val array = ArrayList<String>()
                        array.addAll(listOf(*quitar))
                        while (array.isNotEmpty()) {
                            val random = Random().nextInt(array.size)
                            val s = array[random]
                            val id = s.split(",".toRegex()).toTypedArray()[0].toInt()
                            val cant = abs(s.split(",".toRegex()).toTypedArray()[1].toInt())
                            if (perso.tenerYEliminarObjPorModYCant(id, cant)) {
                                quito = true
                                ENVIAR_Im_INFORMACION(perso, "022;$cant~$id")
                                break
                            }
                            array.removeAt(random)
                        }
                        if (quito) {
                            for (s in dar) {
                                val id = s.split(",".toRegex()).toTypedArray()[0].toInt()
                                val cant = s.split(",".toRegex()).toTypedArray()[1].toInt()
                                val tempObjMod = Mundo.getObjetoModelo(id)
                                if (tempObjMod == null) {
                                    ENVIAR_BN_NADA(objetivo, "BUG ACCION $_id idObjMod $id")
                                    continue
                                } else {
                                    perso.addObjIdentAInventario(
                                        tempObjMod.crearObjeto(
                                            cant, Constantes.OBJETO_POS_NO_EQUIPADO,
                                            CAPACIDAD_STATS.RANDOM
                                        ), false
                                    )
                                    ENVIAR_Im_INFORMACION(perso, "021;$cant~$id")
                                }
                            }
                            ENVIAR_Ow_PODS_DEL_PJ(perso)
                        } else {
                            ENVIAR_Im_INFORMACION(perso, "14|43")
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_GDF_PERSONA -> try {
                        for (s in _args.split(";".toRegex()).toTypedArray()) {
                            val c = s.split(",".toRegex()).toTypedArray()[0].toShort()
                            var estado = 3
                            var interactivo = 0
                            try {
                                estado = s.split(",".toRegex()).toTypedArray()[1].toInt()
                            } catch (ignored: Exception) {
                            }
                            try {
                                interactivo = s.split(",".toRegex()).toTypedArray()[2].toInt()
                            } catch (ignored: Exception) {
                            }
                            ENVIAR_GDF_FORZADO_PERSONAJE(
                                perso, perso.mapa.getCelda(c)!!.id.toString() + ";" + estado + ";"
                                        + interactivo
                            )
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_GDF_MAPA -> try {
                        for (s in _args.split(";".toRegex()).toTypedArray()) {
                            val c = s.split(",".toRegex()).toTypedArray()[0].toShort()
                            var estado = 3
                            var interactivo = 0
                            try {
                                estado = s.split(",".toRegex()).toTypedArray()[1].toInt()
                            } catch (ignored: Exception) {
                            }
                            try {
                                interactivo = s.split(",".toRegex()).toTypedArray()[2].toInt()
                            } catch (ignored: Exception) {
                            }
                            ENVIAR_GDF_FORZADO_MAPA(
                                perso.mapa, perso.mapa.getCelda(c)!!.id.toString() + ";" + estado
                                        + ";" + interactivo
                            )
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_RULETA_PREMIOS -> {
                        val premios = Mundo.RULETA[objUsar!!.objModeloID]
                        ENVIAR_brP_RULETA_PREMIOS(perso, premios + ";" + objUsar.objModeloID)
                        return false
                    }
                    ACCION_ENVIAR_PACKET -> enviar(perso, _args)
                    ACCION_DAR_HABILIDAD_MONTURA -> try {
                        if (perso.montura == null) {
                            return false
                        }
                        for (s in _args.split(",".toRegex()).toTypedArray()) {
                            if (s.isEmpty()) {
                                continue
                            }
                            val habilidad = s.toByte()
                            perso.montura.addHabilidad(habilidad)
                        }
                        ENVIAR_Re_DETALLES_MONTURA(perso, "+", perso.montura)
                        REPLACE_MONTURA(perso.montura, false)
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_CASAR_DOS_PJS -> try {
                        if (perso.mapa.id.toInt() != 2019) {
                            return false
                        }
                        if (perso.sexo == Constantes.SEXO_MASCULINO && perso.celda.id.toInt() == 282 || perso
                                .sexo == Constantes.SEXO_FEMENINO && perso.celda.id.toInt() == 297
                        ) { // no pasa nada
                        } else {
                            ENVIAR_Im_INFORMACION(perso, "1102")
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DISCURSO_SACEDORTE -> perso.preguntaCasarse()
                    ACCION_DIVORCIARSE -> try {
                        if (perso.mapa.id.toInt() != 2019) {
                            return false
                        }
                        val precio = 50000
                        if (perso.kamas < precio) {
                            ENVIAR_Im_INFORMACION(perso, "1128;$precio")
                            return false
                        }
                        perso.addKamas(-precio.toLong(), true, true)
                        val esposo = Mundo.getPersonaje(perso.esposoID)
                        esposo?.divorciar()
                        perso.divorciar()
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_OGRINAS -> try {
                        var idInt: Int
                        idInt = if (objUsar == null) {
                            _args.toInt()
                        } else {
                            try {
                                _args.toInt()
                            } catch (e: Exception) {
                                objUsar.getStatValor(Constantes.STAT_DAR_OGRINAS)
                            }
                        }
                        if (idInt != 0) {
                            ADD_OGRINAS_CUENTA(idInt.toLong(), objetivo.cuentaID)
                        } else {
                            return false
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_DAR_CREDITOS -> try {
                        var idInt = 0
                        idInt = if (objUsar == null) {
                            _args.toInt()
                        } else {
                            try {
                                _args.toInt()
                            } catch (e: Exception) {
                                objUsar.getStatValor(Constantes.STAT_DAR_CREDITOS)
                            }
                        }
                        if (idInt != 0) {
                            SET_CREDITOS_CUENTA(
                                GET_CREDITOS_CUENTA(objetivo.cuentaID) + idInt, objetivo
                                    .cuentaID
                            )
                        } else {
                            return false
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_SOLICITAR_OBJETOS_PARA_REALIZAR_ACCION ->  // quitar o restar el objeto para realizar una accion
                        try {
                            val t = _args.split(Pattern.quote("|").toRegex()).toTypedArray()
                            var nuevaAccion = t[0]
                            if (t.size < 2) {
                                if (objUsar == null) {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                    return false
                                }
                            } else {
                                nuevaAccion = t[1]
                                val solicita = t[0]
                                for (s in solicita.split(";".toRegex()).toTypedArray()) {
                                    val id = s.split(",".toRegex()).toTypedArray()[0].toInt()
                                    val cant = abs(s.split(",".toRegex()).toTypedArray()[1].toInt())
                                    if (!perso.tieneObjPorModYCant(id, cant)) {
                                        ENVIAR_Im_INFORMACION(perso, "14")
                                        return false
                                    }
                                }
                                for (s in solicita.split(";".toRegex()).toTypedArray()) {
                                    val id = s.split(",".toRegex()).toTypedArray()[0].toInt()
                                    val cant = abs(s.split(",".toRegex()).toTypedArray()[1].toInt())
                                    if (perso.tenerYEliminarObjPorModYCant(id, cant)) {
                                        ENVIAR_Im_INFORMACION(perso, "022;$cant~$id")
                                    }
                                }
                            }
                            var args2 = ""
                            val accionID = nuevaAccion.split(";".toRegex()).toTypedArray()[0].toInt()
                            try {
                                args2 = nuevaAccion.split(";".toRegex(), 2).toTypedArray()[1]
                            } catch (ignored: Exception) {
                            }
                            realizar_Accion_Estatico(accionID, args2, perso, null, -1, (-1).toShort())
                        } catch (e: Exception) {
                            ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    ACCION_APRENDER_HECHIZO_CLASE -> try {
                        var id = 0
                        var cant = 0
                        when (perso.getClaseID(true).toInt()) {
                            1 -> {
                                if (perso.mapa.id.toInt() != 1554) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10306, 1) || perso.tenerYEliminarObjPorModYCant(
                                        10308,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10305,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10312,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10313,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10303,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10304,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10307,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10302,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10309,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10310,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(10311, 1)
                                ) {
                                    ENVIAR_Im_INFORMACION(perso, "022;" + 1 + "~" + 10306)
                                    perso.fijarNivelHechizoOAprender(422, 1, false)
                                    ENVIAR_Im_INFORMACION(perso, "03;" + "422")
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            2 -> {
                                id = 10308
                                cant = 1
                                if (perso.mapa.id.toInt() != 1546) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10306, 1) || perso.tenerYEliminarObjPorModYCant(
                                        10308,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10305,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10312,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10313,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10303,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10304,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10307,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10302,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10309,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10310,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(10311, 1)
                                ) {
                                    ENVIAR_Im_INFORMACION(perso, "022;$cant~$id")
                                    perso.fijarNivelHechizoOAprender(420, 1, false)
                                    ENVIAR_Im_INFORMACION(perso, "03;" + "420")
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            3 -> {
                                id = 10305
                                cant = 1
                                if (perso.mapa.id.toInt() != 1470) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10306, 1) || perso.tenerYEliminarObjPorModYCant(
                                        10308,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10305,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10312,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10313,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10303,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10304,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10307,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10302,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10309,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10310,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(10311, 1)
                                ) {
                                    ENVIAR_Im_INFORMACION(perso, "022;$cant~$id")
                                    perso.fijarNivelHechizoOAprender(425, 1, false)
                                    ENVIAR_Im_INFORMACION(perso, "03;" + "425")
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            4 -> {
                                id = 10312
                                cant = 1
                                if (perso.mapa.id.toInt() != 6926) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10306, 1) || perso.tenerYEliminarObjPorModYCant(
                                        10308,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10305,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10312,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10313,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10303,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10304,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10307,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10302,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10309,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10310,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(10311, 1)
                                ) {
                                    ENVIAR_Im_INFORMACION(perso, "022;$cant~$id")
                                    perso.fijarNivelHechizoOAprender(416, 1, false)
                                    ENVIAR_Im_INFORMACION(perso, "03;" + "416")
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            5 -> {
                                id = 10313
                                cant = 1
                                if (perso.mapa.id.toInt() != 1469) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10306, 1) || perso.tenerYEliminarObjPorModYCant(
                                        10308,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10305,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10312,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10313,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10303,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10304,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10307,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10302,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10309,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10310,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(10311, 1)
                                ) {
                                    ENVIAR_Im_INFORMACION(perso, "022;$cant~$id")
                                    perso.fijarNivelHechizoOAprender(424, 1, false)
                                    ENVIAR_Im_INFORMACION(perso, "03;" + "424")
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            6 -> {
                                id = 10303
                                cant = 1
                                if (perso.mapa.id.toInt() != 1544) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10306, 1) || perso.tenerYEliminarObjPorModYCant(
                                        10308,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10305,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10312,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10313,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10303,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10304,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10307,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10302,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10309,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10310,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(10311, 1)
                                ) {
                                    ENVIAR_Im_INFORMACION(perso, "022;$cant~$id")
                                    perso.fijarNivelHechizoOAprender(412, 1, false)
                                    ENVIAR_Im_INFORMACION(perso, "03;" + "412")
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            7 -> {
                                id = 10304
                                cant = 1
                                if (perso.mapa.id.toInt() != 6928) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10306, 1) || perso.tenerYEliminarObjPorModYCant(
                                        10308,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10305,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10312,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10313,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10303,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10304,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10307,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10302,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10309,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10310,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(10311, 1)
                                ) {
                                    ENVIAR_Im_INFORMACION(perso, "022;$cant~$id")
                                    perso.fijarNivelHechizoOAprender(427, 1, false)
                                    ENVIAR_Im_INFORMACION(perso, "03;" + "427")
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            8 -> {
                                id = 10307
                                cant = 1
                                if (perso.mapa.id.toInt() != 1549) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10306, 1) || perso.tenerYEliminarObjPorModYCant(
                                        10308,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10305,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10312,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10313,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10303,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10304,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10307,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10302,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10309,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10310,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(10311, 1)
                                ) {
                                    ENVIAR_Im_INFORMACION(perso, "022;$cant~$id")
                                    perso.fijarNivelHechizoOAprender(410, 1, false)
                                    ENVIAR_Im_INFORMACION(perso, "03;" + "410")
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            9 -> {
                                id = 10302
                                cant = 1
                                if (perso.mapa.id.toInt() != 1558) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10306, 1) || perso.tenerYEliminarObjPorModYCant(
                                        10308,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10305,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10312,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10313,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10303,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10304,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10307,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10302,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10309,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10310,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(10311, 1)
                                ) {
                                    ENVIAR_Im_INFORMACION(perso, "022;$cant~$id")
                                    perso.fijarNivelHechizoOAprender(418, 1, false)
                                    ENVIAR_Im_INFORMACION(perso, "03;" + "418")
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            10 -> {
                                id = 10311
                                cant = 1
                                if (perso.mapa.id.toInt() != 1466) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10306, 1) || perso.tenerYEliminarObjPorModYCant(
                                        10308,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10305,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10312,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10313,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10303,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10304,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10307,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10302,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10309,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10310,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(10311, 1)
                                ) {
                                    ENVIAR_Im_INFORMACION(perso, "022;$cant~$id")
                                    realizar_Accion_Estatico(9, "426", perso, null, -1, (-1).toShort())
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            11 -> {
                                id = 10310
                                cant = 1
                                if (perso.mapa.id.toInt() != 6949) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10306, 1) || perso.tenerYEliminarObjPorModYCant(
                                        10308,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10305,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10312,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10313,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10303,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10304,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10307,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10302,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10309,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10310,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(10311, 1)
                                ) {
                                    realizar_Accion_Estatico(9, "421", perso, null, -1, (-1).toShort())
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                            12 -> {
                                id = 10309
                                cant = 1
                                if (perso.mapa.id.toInt() != 8490) {
                                    ENVIAR_cs_CHAT_MENSAJE(
                                        perso,
                                        "Debes estar en tu templo para realizar la acción",
                                        "da6c00"
                                    )
                                    return false
                                }
                                if (perso.tenerYEliminarObjPorModYCant(10306, 1) || perso.tenerYEliminarObjPorModYCant(
                                        10308,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10305,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10312,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10313,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10303,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10304,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10307,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10302,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10309,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(
                                        10310,
                                        1
                                    ) || perso.tenerYEliminarObjPorModYCant(10311, 1)
                                ) {
                                    ENVIAR_Im_INFORMACION(perso, "022;$cant~$id")
                                    perso.fijarNivelHechizoOAprender(423, 1, false)
                                    ENVIAR_Im_INFORMACION(perso, "03;" + "423")
                                    return false
                                } else {
                                    ENVIAR_Im_INFORMACION(perso, "14")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        redactarLogServidorln("Error en la accion e: $e")
                    }
                    ACCION_MAPA_RANDOM ->  // Classe
                        try { //GestorSalida.ENVIAR_GA2_CINEMATIC(perso, "6");
                            val mapasR = _args.split(";".toRegex()).toTypedArray()
                            val escogido = mapasR[getRandomInt(0, mapasR.size - 1)]
                            val mapa = escogido.split(",".toRegex()).toTypedArray()[0].toShort()
                            val celda = escogido.split(",".toRegex()).toTypedArray()[1].toShort()
                            perso.teleport(mapa, celda)
                        } catch (e: Exception) {
                            ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            return false
                        }
                    ACCION_AGREGAR_OBJETO_A_CERCADO -> {
                        try {
                            val cercado = perso.mapa.cercado
                            if (cercado == null) {
                                ENVIAR_BN_NADA(objetivo)
                                return false
                            }
                            if (!perso.nombre.equals("SlimeS", ignoreCase = true)) {
                                if (perso.gremio == null || cercado.gremio!!.id != perso.gremio.id) {
                                    ENVIAR_Im_INFORMACION(perso, "1100")
                                    return false
                                }
                                if (perso.miembroGremio.puede(Constantes.G_MEJORAR_CERCADOS)) {
                                    ENVIAR_Im_INFORMACION(perso, "193")
                                    return false
                                }
                                if (!cercado.celdasObj.contains(celdaID)) {
                                    ENVIAR_BN_NADA(objetivo)
                                    return false
                                }
                            }
                            if (cercado.cantObjColocados < cercado.cantObjMax) {
                                cercado.addObjetoCria(celdaID, objUsar, perso.Id)
                                val nuevaCantidad = objUsar!!.cantidad - 1
                                if (nuevaCantidad >= 1) {
                                    val nuevoObj = objUsar.clonarObjeto(nuevaCantidad, objUsar.posicion)
                                    perso.addObjIdentAInventario(nuevoObj, false)
                                }
                                objUsar.cantidad = 1
                                perso.borrarOEliminarConOR(idObjUsar, false)
                                ENVIAR_GDO_OBJETO_TIRAR_SUELO(
                                    perso.mapa, '+', celdaID, objUsar.objModeloID, true,
                                    objUsar.durabilidad.toString() + ";" + objUsar.durabilidadMax
                                )
                                ENVIAR_Im_INFORMACION(perso, "022;" + 1 + "~" + objUsar.objModeloID)
                            } else {
                                ENVIAR_Im_INFORMACION(perso, "1107")
                            }
                        } catch (e: Exception) {
                            ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        }
                        return false
                    }
                    ACCION_AGREGAR_PRISMA_A_MAPA -> try {
                        val mapa = perso.mapa
                        val alineacion = perso.alineacion
                        if (perso.deshonor > 0) {
                            ENVIAR_Im_INFORMACION(perso, "183")
                            return false
                        }
                        if (perso.gradoAlineacion < 1) {
                            ENVIAR_Im_INFORMACION(perso, "1155")
                            return false
                        }
                        if (alineacion != Constantes.ALINEACION_BONTARIANO && alineacion != Constantes.ALINEACION_BRAKMARIANO) {
                            ENVIAR_Im_INFORMACION(perso, "134|43")
                            return false
                        }
                        if (!perso.alasActivadas()) {
                            ENVIAR_Im_INFORMACION(perso, "1148")
                            return false
                        }
                        if (mapa.mapaNoPrisma()) {
                            ENVIAR_Im_INFORMACION(perso, "1146")
                            return false
                        }
                        val subarea = mapa.subArea
                        if (subarea!!.alineacion != Constantes.ALINEACION_NEUTRAL || subarea.esConquistable()) {
                            ENVIAR_Im_INFORMACION(perso, "1149")
                            return false
                        }
                        if (objUsar == null) {
                            ENVIAR_Im_INFORMACION(perso, "14")
                            return false
                        }
                        val area = subarea.area
                        val cambio = area.alineacion == Constantes.ALINEACION_NEUTRAL
                        val prisma = Prisma(
                            Mundo.sigIDPrisma(),
                            alineacion,
                            1.toByte(),
                            mapa.id,
                            perso.celda
                                .id,
                            0,
                            if (area.alineacion == Constantes.ALINEACION_NEUTRAL) area.id else -1,
                            subarea.id,
                            0
                        )
                        Mundo.addPrisma(prisma)
                        for (pj in Mundo.PERSONAJESONLINE) {
                            ENVIAR_am_CAMBIAR_ALINEACION_SUBAREA(
                                pj, subarea.id, alineacion, pj
                                    .alineacion != Constantes.ALINEACION_NEUTRAL
                            )
                            if (cambio) {
                                ENVIAR_aM_CAMBIAR_ALINEACION_AREA(pj, area.id, alineacion)
                            }
                        }
                        ENVIAR_GM_PRISMA_A_MAPA(mapa, "+" + prisma.stringGM())
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_LANZAR_ANIMACION -> try {
                        val args = _args.split(",".toRegex()).toTypedArray()
                        val animacion = Mundo.getAnimacion(args[0].toInt())
                        if (perso.pelea != null) {
                            ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        if (animacion != null) {
                            ENVIAR_GA_ACCION_JUEGO_AL_MAPA(
                                perso.mapa, 0, 227, perso.Id.toString() + ";" + celdaID + ","
                                        + animacion.animacionID + "," + animacion.tipoDisplay + "," + animacion.spriteAnimacion + ","
                                        + args[1] + "," + animacion.duracion + "," + animacion.talla, ""
                            )
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_LANZAR_ANIMACION2 -> try {
                        val args = _args.split(",".toRegex()).toTypedArray()
                        val animacion = Mundo.getAnimacion(args[0].toInt())
                        if (perso.pelea != null) {
                            ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        if (animacion != null) {
                            ENVIAR_GA_ACCION_JUEGO_AL_MAPA(
                                perso.mapa, 0, 228, perso.Id.toString() + ";" + celdaID + ","
                                        + animacion.animacionID + "," + animacion.tipoDisplay + "," + animacion.spriteAnimacion + ","
                                        + animacion.level + "," + animacion.duracion, ""
                            )
                        }
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_TIEMPO_PROTECCION_PRISMA -> try {
                        val mapa = perso.mapa
                        val prisma = mapa.prisma
                        if (prisma == null) {
                            ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        if (prisma.pelea != null) {
                            ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        prisma.addTiempProtecion(_args.toInt())
                        val t = prisma.tiempoRestProteccion
                        val f = formatoTiempo(t)
                        ENVIAR_Im_INFORMACION(
                            objetivo, "1TIENE_PROTECCION;" + f[4] + "~" + f[3] + "~" + f[2] + "~"
                                    + f[1]
                        )
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    ACCION_OLVIDAR_HECHIZO_RECAUDADOR -> {
                        return try {
                            if (perso.gremio == null) {
                                ENVIAR_BN_NADA(objetivo, "NO TIENE GREMIO")
                                return false
                            }
                            perso.gremio.olvidarHechizo(_args.toInt(), true)
                        } catch (e: Exception) {
                            ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                            false
                        }
                    }
                    ACCION_TIEMPO_PROTECCION_RECAUDADOR -> try {
                        val mapa = perso.mapa
                        val recaudador = mapa.recaudador
                        if (recaudador == null) {
                            ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        if (recaudador.pelea != null) {
                            ENVIAR_BN_NADA(objetivo)
                            return false
                        }
                        recaudador.addTiempProtecion(_args.toInt())
                        val t = recaudador.tiempoRestProteccion
                        val f = formatoTiempo(t)
                        ENVIAR_Im_INFORMACION(
                            objetivo, "1TIENE_PROTECCION;" + f[4] + "~" + f[3] + "~" + f[2] + "~"
                                    + f[1]
                        )
                    } catch (e: Exception) {
                        ENVIAR_BN_NADA(objetivo, "EXCEPTION ACCION $_id")
                        return false
                    }
                    else -> {
                        redactarLogServidorln("Accion ID = $_id no implantada")
                        return false
                    }
                }
                true
            } catch (e: Exception) {
                redactarLogServidorln(
                    "EXCEPTION id: $_id args: $_args, realizar_Accion_Estatico " + e
                        .toString()
                )
                e.printStackTrace()
                false
            }
        }
    }

    init {
        this.condicion = condicion
    }
}