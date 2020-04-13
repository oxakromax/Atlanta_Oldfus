package estaticos

//import servidor.ServidorThread.Reiniciar
import servidor.ServidorServer
import servidor.ServidorSocket
import utilidades.algoritmos.FuncionesParaThreads
import utilidades.buscadores.Buscador
import variables.casa.Casa
import variables.hechizo.EfectoHechizo
import variables.hechizo.Hechizo
import variables.hechizo.StatHechizo
import variables.mapa.Celda
import variables.mapa.Mapa
import variables.mapa.interactivo.OtroInteractivo
import variables.mision.Mision
import variables.mob.MobModelo
import variables.mob.MobModelo.TipoGrupo
import variables.montura.Montura
import variables.npc.NPCModelo
import variables.npc.PreguntaNPC
import variables.npc.RespuestaNPC
import variables.objeto.ObjetoModelo
import variables.objeto.ObjetoModelo.CAPACIDAD_STATS
import variables.pelea.DropMob
import variables.personaje.Cuenta
import variables.personaje.Personaje
import variables.zotros.Accion
import variables.zotros.Almanax
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.math.abs
import kotlin.math.max

object Comandos {
    fun consolaComando(mensaje: String, _cuenta: Cuenta, _perso: Personaje) {
        try {
            if (mensaje.contains("¿")) {
                val a = mensaje.split("¿".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (b in a) {
                    consolaComando(b, _cuenta, _perso)
                }
                return
            }
            val infos = mensaje.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val comando = infos[0].toUpperCase()
            var rangoJugador = _cuenta.admin
            val rangoComando = Mundo.getRangoComando(comando)
            if (rangoComando == -1) {
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Commande non reconnue: $comando")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Comando no reconocido: $comando")
                }
                return
            }
            if (rangoJugador == 0) {// nada
            } else {
                if (rangoJugador >= rangoComando) {
                    rangoJugador = 5
                } else {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1DONT_RANGE_GM")
                    return
                }
            }
            when (rangoJugador) {

                5 -> GM_lvl_5(comando, infos, mensaje, _cuenta, _perso)
                else -> {
                    GestorSalida.ENVIAR_Im_INFORMACION(_perso, "1DONT_RANGE_GM")
                    return
                }
            }
            if (!_cuenta.sinco && !AtlantaMain.PARAM_DESHABILITAR_SQL) {
                GestorSQL.INSERT_COMANDO_GM(_perso.nombre, mensaje)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun GM_lvl_1(
        comando: String, infos: Array<String>, mensaje: String, _cuenta: Cuenta,
        _perso: Personaje
    ) {
        var infos = infos
        var objetivo: Personaje? = null
        var numInt = -1
        var celdaID: Short = -1
        var mapaID: Short = -1
        var numShort: Short = 1
        val strB = StringBuilder()
        var mapa: Mapa? = _perso.mapa
        when (comando.toUpperCase()) {
            "CELDA_A_HASH" -> try {
                celdaID = java.lang.Short.parseShort(infos[1])
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "CeldaID: $celdaID  HASH: " + Encriptador.celdaIDAHash(
                        celdaID
                    )
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incompletos")
            }

            "HASH_A_CELDA" -> try {
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "HASH: " + infos[1] + "  CeldaID: " + Encriptador.hashACeldaID(
                        infos[1]
                    )
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incompletos")
            }

            "INFO_NPC" -> try {
                val npcMod = Mundo.getNPCModelo(Integer.parseInt(infos[1]))
                if (npcMod == null) {
                    GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(_perso, "NPC NO EXISTE")
                } else {
                    GestorSalida.enviar(
                        _perso, "bp" + npcMod.Sexo + "," + npcMod.TallaX + "," + npcMod.TallaY
                                + "," + npcMod.GfxID + "," + npcMod.Color1 + "," + npcMod.Color2 + "," + npcMod.Color3
                                + "," + npcMod.accesoriosInt
                    )
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incompletos")
            }

            "RATES" -> _perso.mostrarRates()
            "CONGELAR", "FREEZE" -> {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.setInmovil(true)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.nombre + " a ete freeze.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido inmovilizado el personaje " + objetivo.nombre)
                }
            }
            "DESCONGELAR", "UN_FREEZE", "UNFREEZE" -> {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.setInmovil(false)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.nombre + " peut desormais bouger.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido movilizado el personaje " + objetivo.nombre)
                }
            }
            "CONGELAR_MAPA", "FREEZE_MAP" -> {
                if (infos.size > 1) {
                    mapa = Mundo.getMapa(java.lang.Short.parseShort(infos[1]))
                }
                for (objetivos in mapa!!.arrayPersonajes!!) {
                    if (objetivos.cuenta.admin > 0) {
                        continue
                    }
                    objetivos.setInmovil(true)
                }
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Tous les joueurs presents sur la MAP " + mapa.id
                                + " ont ete freeze."
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Han sido inmovilizados todos los personajes del mapa " + mapa
                            .id
                    )
                }
            }
            "DESCONGELAR_MAPA", "UN_FREEZE_MAP", "UNFREEZE_MAP" -> {
                if (infos.size > 1)
                    mapa = Mundo.getMapa(java.lang.Short.parseShort(infos[1]))
                for (objetivos in mapa!!.arrayPersonajes!!) {
                    objetivos.setInmovil(false)
                }
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Les joueurs de cette map ont ete defreeze.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Han sido movilizados todos los personajes del mapa " + mapa
                            .id
                    )
                }
            }
            "MUTEAR_MAPA", "MUTE_MAPA", "MUTE_MAP" -> {
                if (infos.size > 1)
                    mapa = Mundo.getMapa(java.lang.Short.parseShort(infos[1]))
                mapa!!.muteado = true
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Map mutee.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido muteado el mapa " + mapa.id)
                }
            }
            "DES_MUTEAR_MAPA", "DESMUTEAR_MAPA", "DES_MUTE_MAP", "UN_MUTE_MAP", "DESMUTE_MAP" -> {
                try {
                    mapa = Mundo.getMapa(java.lang.Short.parseShort(infos[1]))
                } catch (ignored: Exception) {
                }

                mapa!!.muteado = false
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Map unmute.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido desmuteado el mapa " + mapa.id)
                }
            }
            "MUTE_SEGUNDOS", "MUTE_SECONDS", "MUTEAR", "SILENCIAR", "MUTE" -> {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                    return
                }
                var motivo = ""
                try {
                    if (infos.size > 2) {
                        numInt = Integer.parseInt(infos[2])
                    }
                } catch (ignored: Exception) {
                }

                try {
                    if (infos.size > 3) {
                        infos = mensaje.split(" ".toRegex(), 4).toTypedArray()
                        motivo = infos[3]
                    }
                } catch (ignored: Exception) {
                }

                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (numInt < 0) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La duree de mute est invalide.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La duracion es invalida.")
                    }
                    return
                }
                objetivo.cuenta.mutear(true, numInt)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Le joueur " + objetivo.nombre + " a ete mute pour " + numInt
                                + " secondes."
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Ha sido mute " + objetivo.nombre + " por " + numInt
                                + " segundos."
                    )
                }
                GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS(
                    "1JUGADOR_MUTEAR;" + objetivo.nombre + "~" + numInt / 60 + "~"
                            + motivo
                )
            }
            "UN_MUTE", "DESMUTE", "UNMUTE" -> {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.cuenta.mutear(false, 0)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur peut desormais parler.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El jugador " + objetivo.nombre + " ha sido desmuteado")
                }
            }
            "CARCEL", "JAIL" -> {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (objetivo.pelea != null || objetivo.tutorial != null || objetivo.estaExchange() || objetivo
                        .estaInmovil()
                ) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "Le joueur est en combat ou en craft, impossible de le TP en prison."
                    )
                    return
                }
                objetivo.modificarA(Personaje.RA_PUEDE_USAR_OBJETOS, Personaje.RA_PUEDE_USAR_OBJETOS)
                GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(objetivo)
                val celdas = shortArrayOf(127, 119, 359, 351)
                objetivo.teleport(666.toShort(), celdas[Formulas.getRandomInt(0, 3)])
                objetivo.calabozo = true
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.nombre + " a ete envoye en prison")
            }
            "UNJAIL", "LIBERAR", "UN_JAIL" -> {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.modificarA(Personaje.RA_PUEDE_USAR_OBJETOS, 0)
                GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(objetivo)
                objetivo.calabozo = false
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.nombre + " a ete libere.")
            }
            "TAMAÑO", "TALLA", "SIZE" -> {
                try {
                    if (infos.size > 1)
                        numShort = java.lang.Short.parseShort(infos[1])
                } catch (ignored: Exception) {
                }

                if (numShort < 0) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Taille invalide")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Talla invalida")
                    }
                    return
                }
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                    if (objetivo == null) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                }
                objetivo.setTalla(numShort)
                objetivo.refrescarEnMapa()
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "La taille du joueur " + objetivo.nombre + " a ete modifiee"
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "La talla del personaje " + objetivo.nombre
                                + " ha sido modificada"
                    )
                }
            }
            "INVISIBLE", "INDETECTABLE" -> {
                _perso.setIndetectable(true)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Has entrado al estado INDETECTABLE")
            }
            "VISIBLE", "DETECTABLE" -> {
                _perso.setIndetectable(false)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Has salido al estado INDETECTABLE")
            }
            "GFXID", "FORMA", "MORPH" -> {
                try {
                    if (infos.size > 1)
                        numShort = java.lang.Short.parseShort(infos[1])
                } catch (ignored: Exception) {
                }

                if (numShort < 0) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Gfx ID invalide")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Gfx ID invalida")
                    }
                    return
                }
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                    if (objetivo == null) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                }
                objetivo.setGfxID(numShort)
                objetivo.refrescarEnMapa()
                objetivo.modificarA(
                    Personaje.RA_NO_PUEDE_MOVER_TODAS_DIRECCIONES,
                    Personaje.RA_NO_PUEDE_MOVER_TODAS_DIRECCIONES xor Personaje.RA_NO_PUEDE_MOVER_TODAS_DIRECCIONES
                )
                GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(objetivo)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.nombre + " a change d'apparence.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "El personaje " + objetivo.nombre
                                + " a cambiado de apariencia"
                    )
                }
            }
            "INFO", "INFOS" -> try {
                var enLinea = (Formulas.segundosON() * 1000).toLong()
                val dia = (enLinea / 86400000L).toInt()
                enLinea %= 86400000L
                val hora = (enLinea / 3600000L).toInt()
                enLinea %= 3600000L
                val minuto = (enLinea / 60000L).toInt()
                enLinea %= 60000L
                val segundo = (enLinea / 1000L).toInt()
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "====================\n" + AtlantaMain.NOMBRE_SERVER
                                + " (ATLANTA " + Constantes.VERSION_EMULADOR + ")\n\nUptime: " + dia + "j " + hora + "h " + minuto + "m "
                                + segundo + "s\n" + "Joueurs en ligne: " + ServidorServer.nroJugadoresLinea() + "\n"
                                + "Record de connexions: " + ServidorServer.recordJugadores + "\n" + "===================="
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "====================\n" + AtlantaMain.NOMBRE_SERVER
                                + " (ATLANTA " + Constantes.VERSION_EMULADOR + ")\n\nEnLinea: " + dia + "d " + hora + "h " + minuto + "m "
                                + segundo + "s\n" + "Jugadores en linea: " + ServidorServer.nroJugadoresLinea() + "\n"
                                + "Record de conexion: " + ServidorServer.recordJugadores + "\n" + "===================="
                    )
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio un error")
                e.printStackTrace()
                return
            }

            "REFRESCAR_MOBS", "REFRESH_MOBS" -> {
                _perso.mapa.refrescarGrupoMobs()
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mobs respawns.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mobs Refrescados")
                }
            }
            "INFO_MAP", "INFO_MAPA", "MAPA_INFO", "MAP_INFOS", "MAPA_INFOS", "INFOS_MAPA", "INFOS_MAP", "MAP_INFO" -> {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "====================")
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "MAP ID: " + mapa!!.id + " [" + mapa.x + ", " + mapa.y
                            + "]"
                )
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Liste des PNJS sur la map:")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Lista de NPC del mapa:")
                }
                mapa = _perso.mapa
                for (npc in mapa!!.npCs!!.values) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "ID: " + npc.id + " - Template: " + npc.modelo!!.id
                                    + " - Nom: " + npc.modelo.nombre + " - Case: " + npc.celdaID + " - Question: " + npc
                                .modelo.getPreguntaID(null)
                        )
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "ID: " + npc.id + " - Modelo: " + npc.modelo!!.id
                                    + " - Nombre: " + npc.modelo.nombre + " - Celda: " + npc.celdaID + " - Pregunta: " + npc
                                .modelo.getPreguntaID(null)
                        )
                    }
                }
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Liste des groupes de monstres sur la map:")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Lista de los grupos de mounstros:")
                }
                for (gm in mapa.grupoMobsTotales!!.values) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "ID: " + gm.id + " - Case ID: " + gm.celdaID
                                    + " - Monstres: " + gm.strGrupoMob + " - Quantite: " + gm.cantMobs + " - Type: " + gm.tipo
                                    + " - Kamas: " + gm.kamasHeroico + " - ItemsID: " + gm.iDsObjeto
                        )
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "ID: " + gm.id + " - CeldaID: " + gm.celdaID
                                    + " - StringMob: " + gm.strGrupoMob + " - Cantidad: " + gm.cantMobs + " - Tipo: " + gm.tipo
                                    + " - Kamas: " + gm.kamasHeroico + " - ObjetosID: " + gm.iDsObjeto
                        )
                    }
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "====================")
            }
            "CANT_SALVANDO", "SAVE_TIMES" -> if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "Le serveur a ete sauvegarde " + Mundo.CANT_SALVANDO + " fois."
                )
            } else {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El salvado del servidor esta en el " + Mundo.CANT_SALVANDO)
            }
            "EN_LINEA", "ONLINE", "JUGADORES", "PLAYERS", "JOUERS", "QUIENES", "WHOIS" -> {
                var maximo = 50
                try {
                    maximo = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "====================\nListe de joueur en ligne:")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "====================\nLista de los jugadores en linea:")
                }
                var players = 0
                for (ep in ServidorServer.clientes) {
                    try {
                        objetivo = ep.personaje
                    } catch (e: Exception) {
                        continue
                    }

                    players++
                    if (players >= maximo) {
                        continue
                    }
                    if (strB.isNotEmpty()) {
                        strB.append("\n")
                    }
                    if (ep.cuenta == null) {
                        strB.append("Socket sin loguear cuenta - IP: ").append(ep.actualIP)
                        continue
                    }
                    if (objetivo == null) {
                        strB.append("Cuenta sin loguear personaje - Cuenta: ").append(ep.cuenta!!.nombre)
                            .append(" IP: ").append(
                                ep
                                    .actualIP
                            )
                        continue
                    }
                    if (!objetivo.enLinea()) {
                        strB.append("Personaje Offline: ").append(objetivo.nombre).append("Cuenta: ")
                            .append(ep.cuenta!!.nombre).append(" IP: ").append(ep.actualIP)
                        continue
                    }
                    strB.append(objetivo.nombre).append("\t")
                    strB.append("(").append(objetivo.Id).append(") ").append("\t")
                    if (objetivo.cuenta.admin < 2) {
                        strB.append("[").append(objetivo.cuenta.nombre).append("]").append("\t")
                        strB.append("[").append(objetivo.cuenta.contraseña).append("]").append("\t")
                    }
                    strB.append(" ").append(if (objetivo.sexo.toInt() == 0) "M" else "F").append("\t")
                    strB.append(objetivo.nivel).append("\t")
                    strB.append(objetivo.mapa.id.toInt()).append("\t")
                    strB.append("(").append(objetivo.mapa.x.toInt()).append(",").append(objetivo.mapa.y.toInt())
                        .append(")").append("\t")
                    strB.append(if (objetivo.pelea == null) "" else "En combat ")
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
                if (players > 0) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Et $players joueurs en plus")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Realmente $players personajes")
                    }
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "====================")
            }
            "CREAR_GREMIO", "CREATE_GUILD" -> {
                objetivo = _perso
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecte.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                if (objetivo.gremio != null || objetivo.miembroGremio != null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "Le joueur " + objetivo.nombre
                                    + " appartient dejà à une guilde."
                        )
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.nombre + " ya tiene gremio")
                    }
                    return
                }
                Accion.realizar_Accion_Estatico(-2, "", objetivo, objetivo, -1, (-1).toShort())
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Panel de guilde ouvert pour : " + objetivo.nombre)
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Se abrio la ventana de gremio al personaje " + objetivo
                            .nombre
                    )
                }
            }
            "DEFORMAR", "DEMORPH" -> {
                objetivo = _perso
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                    if (objetivo == null) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                }
                if (objetivo.pelea != null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur est en combat.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje esta en un combate")
                    }
                    return
                }
                objetivo.deformar()
                objetivo.refrescarEnMapa()
                objetivo.modificarA(
                    Personaje.RA_NO_PUEDE_MOVER_TODAS_DIRECCIONES,
                    Personaje.RA_NO_PUEDE_MOVER_TODAS_DIRECCIONES xor 0
                )
                GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(objetivo)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Le joueur " + objetivo.nombre
                                + " a retrouve son apparence initiale."
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El jugador " + objetivo.nombre + " ha sido deformado")
                }
            }
            "IR_DONDE", "IR", "JOIN" -> {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                } else {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Veuillez indiquer le nom du joueur à rejoindre.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hace falta colocar un nombre de jugador")
                    }
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecte.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                mapaID = objetivo.mapa.id
                celdaID = objetivo.celda.id
                var teleportado: Personaje? = _perso
                if (infos.size > 2) {
                    teleportado = Mundo.getPersonajePorNombre(infos[2])
                    if (teleportado == null || !teleportado.enLinea()) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(
                                _perso,
                                "Le joueur à teleporter n'existe pas ou n'est pas connecte"
                            )
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(
                                _perso,
                                "El personaje a teleportar no existe o no esta conectado"
                            )
                        }
                        return
                    }
                }
                if (teleportado!!.pelea != null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + teleportado.nombre + " est en combat.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "El personaje " + teleportado.nombre + " esta en combate"
                        )
                    }
                    return
                }
                if (teleportado.estaExchange()) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "Le joueur " + teleportado.nombre
                                    + " est entrain de exchange."
                        )
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "El personaje " + teleportado.nombre
                                    + " esta haciendo un exchange"
                        )
                    }
                    return
                }
                if (teleportado.tutorial != null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "Le joueur " + teleportado.nombre + " est en tutoriel."
                        )
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "El personaje " + teleportado.nombre
                                    + " esta en un tutorial"
                        )
                    }
                    return
                }
                if (teleportado.huir) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "Le joueur " + teleportado.nombre
                                    + " ne peut fuir d'un combat PVP"
                        )
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "El personaje " + teleportado.nombre
                                    + " no puede huir de una pelea PVP"
                        )
                    }
                    return
                }
                teleportado.teleport(mapaID, celdaID)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur a ete teleporte")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "El jugador " + teleportado.nombre
                                + " fue teletransportado donde jugador " + objetivo.nombre + " (Map: " + objetivo.mapa.id
                                + ")"
                    )
                }
            }
            "TRAER", "JOIN_ME" -> {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                } else {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Veuillez indiquer le nom du joueur à rejoindre.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hace falta colocar un nombre de jugador")
                    }
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecte.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                if (objetivo.pelea != null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.nombre + " est en combat.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje " + objetivo.nombre + " esta en combate")
                    }
                    return
                }
                if (objetivo.estaExchange()) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.nombre + " est en exchange.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "El personaje " + objetivo.nombre + " esta en exchange"
                        )
                    }
                    return
                }
                if (objetivo.tutorial != null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.nombre + " est en tutoriel.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "El personaje " + objetivo.nombre + " esta en un tutorial"
                        )
                    }
                    return
                }
                if (objetivo.huir) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "Le joueur " + objetivo.nombre
                                    + " ne peut fuir d'un combat PVP"
                        )
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "El personaje " + objetivo.nombre
                                    + " no puede huir de una pelea PVP"
                        )
                    }
                    return
                }
                var traedor: Personaje? = _perso
                if (infos.size > 2) {
                    traedor = Mundo.getPersonajePorNombre(infos[2])
                    if (traedor == null || !traedor.enLinea()) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecte.")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personajeno no esta conectado")
                        }
                        return
                    }
                }
                mapaID = traedor!!.mapa.id
                celdaID = traedor.celda.id
                val mapaoriginal = objetivo.mapa.id
                val celdaoriginal = objetivo.celda.id
                objetivo.teleport(mapaID, celdaID)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Le joueur " + objetivo.nombre
                                + " ha sido teletransportado hacia el personaje " + traedor.nombre
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "El jugador " + objetivo.nombre + " (Map: " + objetivo
                            .nombre + ")" + " fue teletransportado donde jugador " + traedor.nombre + " Desde el Mapa " +
                                mapaoriginal + " en la Celda " + celdaoriginal
                    )
                }
            }
            "AN", "ALL", "ANNOUNCE" -> try {
                infos = mensaje.split(" ".toRegex(), 2).toTypedArray()
                if (infos.size < 2) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Veuillez indiquer le message à envoyer!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Falta argumentos")
                    }
                    return
                }
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("<b>[" + _perso.nombre + "] : </b> " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "TP", "TELEPORT" -> {
                try {
                    if (infos.size > 1) {
                        mapaID = java.lang.Short.parseShort(infos[1])
                    }
                    if (infos.size > 2) {
                        celdaID = java.lang.Short.parseShort(infos[2])
                    }
                } catch (ignored: Exception) {
                }

                mapa = Mundo.getMapa(mapaID)
                if (mapa == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "MAPID INVALIDE!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mapa a teleportar no existe")
                    }
                    return
                }
                if (celdaID <= -1) {
                    celdaID = mapa.randomCeldaIDLibre
                } else if (mapa.getCelda(celdaID) == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CELLID INVALIDE!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CeldaID invalida")
                    }
                    return
                }
                objetivo = _perso
                if (infos.size > 3) {
                    objetivo = Mundo.getPersonajePorNombre(infos[3])
                    if (objetivo == null) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                    if (!objetivo.enLinea()) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecte.")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                        }
                        return
                    }
                }
                if (objetivo.pelea != null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur à teleporter est en combat.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje a teleportar esta en combate")
                    }
                    return
                }
                if (objetivo.estaExchange()) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur à teleporter est entrain de crafter.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje a teleportar esta haciendo un trabajo")
                    }
                    return
                }
                if (objetivo.tutorial != null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur à teleporter est en tutoriel.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje a teleportar esta en un tutorial")
                    }
                    return
                }
                if (objetivo.huir) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur à teleporter ne peut fuir d'un combat PVP")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "El personaje a teleportar no puede huir de una pelea PVP"
                        )
                    }
                    return
                }
                objetivo.teleport(mapaID, celdaID)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Le joueur " + objetivo.nombre
                                + " ha sido teletransportado a mapaID: " + mapaID + ", celdaID: " + celdaID
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "El jugador " + objetivo.nombre
                                + " ha sido teletransportado a mapaID: " + mapaID + ", celdaID: " + celdaID
                    )
                }
            }
            "TELEPORT_SIN_TODOS" -> {
                try {
                    if (infos.size > 1) {
                        mapaID = java.lang.Short.parseShort(infos[1])
                    }
                    if (infos.size > 2) {
                        celdaID = java.lang.Short.parseShort(infos[2])
                    }
                } catch (ignored: Exception) {
                }

                mapa = Mundo.getMapa(mapaID)
                if (mapa == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "MAPID INVALIDE!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mapa a teleportar no existe")
                    }
                    return
                }
                if (celdaID <= -1) {
                    celdaID = mapa.randomCeldaIDLibre
                } else if (mapa.getCelda(celdaID) == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CELLID INVALIDE!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CeldaID invalida")
                    }
                    return
                }
                objetivo = _perso
                if (infos.size > 3) {
                    objetivo = Mundo.getPersonajePorNombre(infos[3])
                    if (objetivo == null) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                    if (!objetivo.enLinea()) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecte.")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                        }
                        return
                    }
                }
                objetivo.teleportSinTodos(mapaID, celdaID)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Le joueur " + objetivo.nombre
                                + " ha sido teletransportado a mapaID: " + mapaID + ", celdaID: " + celdaID
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "El jugador " + objetivo.nombre
                                + " ha sido teletransportado a mapaID: " + mapaID + ", celdaID: " + celdaID
                    )
                }
            }
            "IR_MAPA", "GO_MAP" -> {
                var mapaX = 0
                var mapaY = 0
                celdaID = 0
                var contID = 0
                try {
                    mapaX = Integer.parseInt(infos[1])
                    mapaY = Integer.parseInt(infos[2])
                    celdaID = java.lang.Short.parseShort(infos[3])
                    contID = Integer.parseInt(infos[4])
                } catch (ignored: Exception) {
                }

                mapa = Mundo.mapaPorCoordXYContinente(mapaX, mapaY, contID)
                if (mapa == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Position ou continent invalide!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Posicion o continente invalido")
                    }
                    return
                }
                if (mapa.getCelda(celdaID) == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CellID invalide!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CeldaID invalido")
                    }
                    return
                }
                objetivo = _perso
                if (infos.size > 5) {
                    objetivo = Mundo.getPersonajePorNombre(infos[5])
                    if (objetivo == null) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                    if (!objetivo.enLinea()) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecte.")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                        }
                        return
                    }
                    if (objetivo.pelea != null) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur est en combat!")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje esta en combate")
                        }
                        return
                    }
                }
                objetivo.teleport(mapa.id, celdaID)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.nombre + " a ete teleporte!")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "El jugador " + objetivo.nombre + " ha sido teletransportado"
                    )
                }
            }
            else -> {
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Commande non reconnue: $comando")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Comando no reconocido: $comando")
                }
                return
            }
        }
        // if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
        // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Commande GM 1!");
        // } else {
        // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Comando de nivel 1");
        // }
    }

    private fun GM_lvl_2(
        comando: String, infos: Array<String>, mensaje: String, _cuenta: Cuenta,
        _perso: Personaje
    ) {
        var infos = infos
        var numInt = -1
        var celdaID: Short
        var x: Short
        var y: Short
        var objetivo: Personaje? = null
        var strB = StringBuilder()
        val mapa = _perso.mapa
        var motivo = ""
        when (comando.toUpperCase()) {
            "WW", "W", "Winner" -> try {
                try {
                    numInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
                    return
                }

                if (numInt != 2 && numInt != 1) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ganador invalido")
                    return
                }
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                val pelea = objetivo.pelea
                if (pelea == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, objetivo.nombre + " no estas en pelea")
                    return
                }
                pelea.acaboPelea((if (numInt == 1) 2 else 1).toByte())
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El equipo " + infos[1] + " ha salido victorioso, en la pelea ID "
                            + pelea.id + " del mapa " + pelea.mapaCopia!!.id
                )
            } catch (ignored: Exception) {
            }

            "CANCEL_FIGHT", "CANCELAR_PELEA", "ANULAR_PELEA", "ANULATE_FIGHT", "FIGHT_CANCEL" -> try {
                objetivo = _perso
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                val pelea = objetivo.pelea
                if (pelea == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, objetivo.nombre + " no estas en pelea")
                    return
                }
                pelea.cancelarPelea()
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "La pelea ID " + pelea.id + " del mapa " + pelea.mapaCopia
                    !!.id + " ha sido cancelada"
                )
            } catch (ignored: Exception) {
            }

            "SHOW_BAN_IPS", "MOSTRAR_BAN_IPS", "SHOW_LIST_BAN_IPS", "MOSTRAR_BAN_IP" -> GestorSalida.ENVIAR_BAT2_CONSOLA(
                _perso,
                "Las IPs Baneadas son las siguientes:\n" + GestorSQL.LISTA_BAN_IP()
            )
            "BANEAR", "BAN" -> {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                } else {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                try {
                    numInt = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Debes ingresar un tiempo (minutos)")
                    return
                }

                try {
                    if (infos.size > 3) {
                        infos = mensaje.split(" ".toRegex(), 4).toTypedArray()
                        motivo = infos[3]
                    }
                } catch (ignored: Exception) {
                }

                if (numInt == 0) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Duree du ban incorrecte!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Tiempo de baneo incorrecto")
                    }
                    return
                }
                objetivo.cuenta.setBaneado(true, numInt)
                if (objetivo.servidorSocket != null) {
                    objetivo.servidorSocket!!.cerrarSocket(true, " command BANEAR")
                }
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Le joueur " + objetivo.nombre + " a ete banni par " + numInt
                                + " minutes."
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Ha sido baneado " + objetivo.nombre + " por " + numInt
                                + " minutos."
                    )
                }
                GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1JUGADOR_BANEAR;" + objetivo.nombre + "~" + motivo)
            }
            "DES_BAN", "UN_BAN", "DESBANEAR", "UNBAN" -> {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                } else {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.cuenta.setBaneado(false, 0)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.nombre + " a ete debanni.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido desbaneado " + objetivo.nombre)
                }
            }
            "BANEAR_IP_PJ", "BAN_IP_PLAYER", "BAN_IP_PERSO" -> {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                } else {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                val ipBaneada = objetivo.cuenta.actualIP
                if (!GestorSQL.ES_IP_BANEADA(ipBaneada)) {
                    if (GestorSQL.INSERT_BAN_IP(ipBaneada)) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'IP $ipBaneada est bannie.")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La IP $ipBaneada esta baneada.")
                        }
                    }
                    if (objetivo.enLinea()) {
                        objetivo.servidorSocket!!.cerrarSocket(true, "command Banear IP")
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur a ete kick.")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El jugador fue retirado.")
                        }
                    }
                } else {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'IP n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La IP no existe")
                    }
                }
            }
            "BANEAR_IP", "BAN_IP", "BANEAR_IP_NUMERO", "BAN_IP_NUMBER" -> {
                if (infos.size <= 1) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                if (!GestorSQL.ES_IP_BANEADA(infos[1])) {
                    if (GestorSQL.INSERT_BAN_IP(infos[1])) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La IP " + infos[1] + " esta baneada.")
                    }
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La IP no existe")
                }
            }
            "DESBANEAR_IP_NUMERO", "UNBAN_IP_NUMERO" -> {
                if (infos.size <= 1) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                GestorSQL.DELETE_BAN_IP(infos[1])
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'ip " + infos[1] + " a ete debannie.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "Se borro la ip " + infos[1] + " de la lista de ip baneadas"
                    )
                }
            }
            "EXPULSAR", "KICK" -> {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                } else {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                try {
                    if (infos.size > 2) {
                        infos = mensaje.split(" ".toRegex(), 3).toTypedArray()
                        motivo = infos[2]
                    }
                } catch (ignored: Exception) {
                }

                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecte.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                } else {
                    try {
                        var contador = 0
                        for (c in Mundo.cuentas.values) {
                            if (c.ultimaIP == objetivo.cuenta.actualIP || c.ultimaIP == objetivo.cuenta.ultimaIP || c.actualIP == objetivo.cuenta.actualIP) {
                                try {
                                    c.socket?.cerrarSocket(true, "command EXPULSAR")
                                    contador++
                                } catch (e: Exception) {
                                    continue
                                }

                            }
                        }
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se han expulsado un total de: $contador")
                    } catch (e: Exception) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Impossible de kicker " + objetivo.nombre)
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No se ha podido expulsar a " + objetivo.nombre)
                        }
                        return
                    }

                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur " + objetivo.nombre + " a ete kick.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ha sido expulsado " + objetivo.nombre)
                    }
                    GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1JUGADOR_EXPULSAR;" + objetivo.nombre + "~" + motivo)
                }
            }
            "BOLETOS_COMPRADOS", "TICKETS_ACHETES", "GET_BOUGHT_TICKETS" -> {
                numInt = 0
                for (z in 1..Mundo.LOTERIA_BOLETOS.size) {
                    if (Mundo.LOTERIA_BOLETOS[z - 1] != 0) {
                        numInt++
                    }
                }
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "Actuellement, le nombre de tickets achetes est de $numInt."
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Actualmente hay $numInt boletos comprados.")
                }
            }
            "LISTA_BOLETOS_COMPRADOS" -> {
                for (z in 1..Mundo.LOTERIA_BOLETOS.size) {
                    if (Mundo.LOTERIA_BOLETOS[z - 1] != 0) {
                        if (strB.isNotEmpty()) {
                            strB.append(",")
                        }
                        strB.append(Mundo.LOTERIA_BOLETOS[z - 1])
                    }
                }
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Liste de tickets achetes $strB.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Lista de boletos comprados: $strB")
                }
            }
            "SET_CELDAS_PELEA" -> {
                mapa.setStrCeldasPelea(infos[1])
                if (GestorSQL.UPDATE_MAPA_POS_PELEA(mapa.id.toInt(), mapa.convertCeldasPelea)) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "Une erreur est survenue lors de la sauvegarde en BDD!"
                        )
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "Ocurrio un error al guardar la actualizacion en la BD."
                        )
                    }
                    return
                } else {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le string cells fight change to " + infos[1])
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El str de celdas pelea cambio a " + infos[1])
                    }
                }
            }
            "SET_COLOUR_AGGRESSOR", "SET_COLOR_ATK", "SET_COLOR_AGRESOR", "SET_COLOR_ATACANTE" -> {
                mapa.colorCeldasAtacante = infos[1]
                if (GestorSQL.UPDATE_MAPA_POS_PELEA(mapa.id.toInt(), mapa.convertCeldasPelea)) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "Une erreur est survenue lors de la sauvegarde en BDD!"
                        )
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "Ocurrio un error al guardar la actualizacion en la BD."
                        )
                    }
                    return
                } else {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le colour de cells aggressor c'est " + infos[1])
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El color de celdas del agresor es " + infos[1])
                    }
                }
            }
            "BORRAR_POSICIONES", "ELIMINAR_POSICIONES", "DEL_POSICIONES_PELEA", "BORRAR_TODAS_POS_PELEA", "DEL_ALL_POS" -> {
                mapa.decodificarPosPelea("")
                if (GestorSQL.UPDATE_MAPA_POS_PELEA(mapa.id.toInt(), mapa.convertCeldasPelea)) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "Une erreur est survenue lors de la sauvegarde en BDD!"
                        )
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "Ocurrio un error al guardar la actualizacion en la BD."
                        )
                    }
                } else {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Les positions de combat ont ete supprimees.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Las posiciones de pelea han sido borradas.")
                    }
                }
            }
            "DEL_FIGHT_POS", "DEL_POS_FIGHT", "BORRAR_POS_PELEA", "DEL_FIGHT_POS_BY_CELL" -> {
                celdaID = -1
                try {
                    celdaID = java.lang.Short.parseShort(infos[1])
                } catch (ignored: Exception) {
                }

                if (mapa.getCelda(celdaID) == null) {
                    celdaID = _perso.celda.id
                }
                // if (mapa.getCercado() != null) {
                // mapa.getCercado().getCeldasObj().remove((Object) celdaID);
                // }
                GestorSalida.enviarEnCola(_perso, "GDZ|-$celdaID;0;4|-$celdaID;0;11|-$celdaID;0;5", false)
                mapa.borrarCeldasPelea(celdaID)
                if (GestorSQL.UPDATE_MAPA_POS_PELEA(mapa.id.toInt(), mapa.convertCeldasPelea)) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "Une erreur est survenue lors de la sauvegarde en BDD!"
                        )
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "Ocurrio un error al guardar la actualizacion en la BD."
                        )
                    }
                }
            }
            "ADD_CELL_FIGHT", "AGREGAR_CELDA_PELEA", "ADD_CELDA_PELEA", "ADD_POS_FIGHT", "AGREGAR_POS_PELEA", "ADD_FIGHT_POS" -> {
                var equipo = -1
                celdaID = -1
                try {
                    equipo = Integer.parseInt(infos[1])
                    celdaID = java.lang.Short.parseShort(infos[2])
                } catch (ignored: Exception) {
                }

                if (equipo != 2 && equipo != 1) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Equipe incorrecte, use colour 2(blue) o 1(rouge)")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Equipo incorrecto, usa 2(azul) o 1(rojo)")
                    }
                    return
                }
                if (mapa.getCelda(celdaID) == null || !mapa.getCelda(celdaID)!!.esCaminable(true)) {
                    celdaID = _perso.celda.id
                }
                GestorSalida.enviarEnCola(_perso, "GDZ|-$celdaID;0;4|-$celdaID;0;11", false)
                GestorSalida.enviarEnCola(_perso, "GDZ|+" + celdaID + ";0;" + if (equipo == 1) 4 else 11, false)
                mapa.addCeldaPelea(equipo, celdaID)
                if (GestorSQL.UPDATE_MAPA_POS_PELEA(mapa.id.toInt(), mapa.convertCeldasPelea)) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "Une erreur est survenue lors de la sauvegarde en BDD."
                        )
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "Ocurrio un error al guardar la actualizacion en la BD."
                        )
                    }
                    return
                }
            }
            "OCULTAR_POSICIONES", "HIDE_POSITIONS", "ESCONDER_POSICIONES" -> mapa.panelPosiciones(_perso, false)
            "LISTA_POS_PELEA", "MOSTRAR_POSICIONES", "MOSTRAR_POSICIONES_PELEA", "MOSTRAR_POS_PELEA", "SHOW_POSITIONS", "SHOW_FIGHT_POS" -> mapa.panelPosiciones(
                _perso,
                true
            )
            "MAPAS", "MAPS", "MAPAS_COORDENADAS", "GET_MAPS_BY_COORDS" -> {
                x = -1
                y = -1
                try {
                    x = java.lang.Short.parseShort(infos[1])
                    y = java.lang.Short.parseShort(infos[2])
                } catch (e: Exception) {
                    x = mapa.x
                    y = mapa.y
                }

                strB = StringBuilder(Mundo.mapaPorCoordenadas(x.toInt(), y.toInt(), mapa.subArea!!.area.superArea!!.id))
                if (strB.toString().isEmpty()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No hay ID mapa para esas coordenadas")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Los ID mapas para las coordenas X: " + x + " Y: " + y + " son "
                                + strB.toString()
                    )
                }
            }
            "MAP_UP", "MAPA_ARRIBA" -> {
                x = mapa.x
                y = (mapa.y - 1).toShort()
                strB = StringBuilder(Mundo.mapaPorCoordenadas(x.toInt(), y.toInt(), mapa.subArea!!.area.superArea!!.id))
                if (strB.toString().isEmpty()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No hay ID mapa para esas coordenadas")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Los ID mapas para las coordenas X: " + x + " Y: " + y + " son "
                                + strB.toString()
                    )
                }
            }
            "MAP_DOWN", "MAPA_ABAJO" -> {
                x = mapa.x
                y = (mapa.y + 1).toShort()
                strB = StringBuilder(Mundo.mapaPorCoordenadas(x.toInt(), y.toInt(), mapa.subArea!!.area.superArea!!.id))
                if (strB.toString().isEmpty()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No hay ID mapa para esas coordenadas")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Los ID mapas para las coordenas X: " + x + " Y: " + y + " son "
                                + strB.toString()
                    )
                }
            }
            "MAP_LEFT", "MAPA_IZQUIERDA" -> {
                x = (mapa.x - 1).toShort()
                y = mapa.y
                strB = StringBuilder(Mundo.mapaPorCoordenadas(x.toInt(), y.toInt(), mapa.subArea!!.area.superArea!!.id))
                if (strB.toString().isEmpty()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No hay ID mapa para esas coordenadas")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Los ID mapas para las coordenas X: " + x + " Y: " + y + " son "
                                + strB.toString()
                    )
                }
            }
            "MAP_RIGHT", "MAPA_DERECHA" -> {
                x = (mapa.x + 1).toShort()
                y = mapa.y
                strB = StringBuilder(Mundo.mapaPorCoordenadas(x.toInt(), y.toInt(), mapa.subArea!!.area.superArea!!.id))
                if (strB.toString().isEmpty()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No hay ID mapa para esas coordenadas")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Los ID mapas para las coordenas X: " + x + " Y: " + y + " son "
                                + strB.toString()
                    )
                }
            }
            "CAMBIAR_ALINEACION", "ALINEACION", "ALIGN", "SET_ALIGN" -> {
                var alineacion: Byte = -1
                try {
                    alineacion = java.lang.Byte.parseByte(infos[1])
                } catch (ignored: Exception) {
                }

                if (alineacion < -1 || alineacion > 3) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Alignement incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                    if (objetivo == null) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                }
                objetivo.cambiarAlineacion(alineacion, true)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "L'alignement du joueur " + objetivo.nombre
                                + " a ete modifie."
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "La alineacion del personaje " + objetivo.nombre
                                + " ha sido modificada"
                    )
                }
            }
            "APRENDER_OFICIO", "LEARN_JOB" -> {
                try {
                    numInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Veuillez indiquer l'id du metier.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos Incorrectos")
                    }
                    return
                }

                if (Mundo.getOficio(numInt) == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'ID du metier est incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "ID Oficio no existe")
                    }
                    return
                }
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                    if (objetivo == null) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                }
                if (objetivo.aprenderOficio(Mundo.getOficio(numInt), 0) != -1) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur a appris ce metier $numInt")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "El personaje " + objetivo.nombre + " ha aprendido el oficio "
                                    + numInt
                        )
                    }
                } else {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur ne peut pas apprendre ce metier.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "El personaje " + objetivo.nombre
                                    + " no puede aprender ese oficio"
                        )
                    }
                }
            }
            "PDV", "PDVPER" -> {
                var porcPDV = 0
                try {
                    porcPDV = Integer.parseInt(infos[1])
                    if (porcPDV < 0) {
                        porcPDV = 0
                    }
                    if (porcPDV > 100) {
                        porcPDV = 100
                    }
                    objetivo = _perso
                    if (infos.size > 2) {
                        val nombre = infos[2]
                        objetivo = Mundo.getPersonajePorNombre(nombre)
                        if (objetivo == null || !objetivo.enLinea()) {
                            objetivo = _perso
                        }
                    }
                    objetivo.actualizarPDV(porcPDV.toFloat())
                    if (objetivo.enLinea()) {
                        GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(objetivo)
                    }
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "Le pourcentage de vie du joueur " + objetivo.nombre
                                    + " a ete modifie en " + porcPDV
                        )
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "Ha sido modificado el porcentaje de vida " + objetivo.nombre
                                    + " a " + porcPDV
                        )
                    }
                    GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(objetivo)
                } catch (e: Exception) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                }

            }
            else -> {
                GM_lvl_1(comando, infos, mensaje, _cuenta, _perso)
                return
            }
        }
        // if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
        // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Commande de GM 2!");
        // } else {
        // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Comando de nivel 2");
        // }
    }

    private fun GM_lvl_3(
        comando: String, infos: Array<String>, mensaje: String, _cuenta: Cuenta,
        _perso: Personaje
    ) {
        var infos = infos
        var sql = false
        // byte idByte = 0, numByte = 0;
        // short numShort = 0;
        // int numInt = 0, tipo = 0, accionID = -1, id2 = -1, restriccion = -1;
        // int idMob = -1, idObjMod = -1, prospecc = 100, max = 1;
        // float porcentaje = 0, numFloat = 0;
        // final StringBuilder strB = new StringBuilder();
        // String args = "", condicion = "", str = "";
        var mapa: Mapa? = _perso.mapa
        // short celdaID = -1, mapaID = mapa.getID();
        // MobModelo mobModelo;
        var objetivo: Personaje? = null
        // ObjetoModelo objModelo;
        // Objeto obj;
        // NPC npc;
        // PreguntaNPC pregunta;
        // RespuestaNPC respuesta;
        when (comando.toUpperCase()) {
            "KICK_MERCHANT", "KICK_MERCHAND", "VOTAR_MERCANTE", "EXPULSAR_MERCANTE" -> try {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.esMercante()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "El personaje " + objetivo.nombre
                                + " no esta en modo mercante"
                    )
                    return
                }
                objetivo.mapa.removerMercante(objetivo.Id)
                objetivo.setMercante(false)
                GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(objetivo.mapa, objetivo.Id)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El personaje " + objetivo.nombre
                            + " ha sido expulsado del modo mercante"
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "EXCEPTION COMANDO")
            }

            "IPS_AFKS", "IPS_CLIENTES_AFKS", "IPS_BUGS", "IPS_ATACANTES", "IPS_ATTACK" -> try {
                var segundos = 0
                try {
                    segundos = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Valor incorrecto (segundos)")
                    return
                }

                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Las ips de las connexiones BUGS son: " + ServidorServer
                        .listaClientesBug(segundos)
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "EXCEPTION COMANDO")
            }

            "EXPULSAR_AFKS", "EXPULSAR_CLIENTES_BUG", "KICK_CLIENTS_BUG", "VOTAR_CLIENTES_BUG", "EXPULSAR_INACTIVOS", "CLEAN_SERVER", "LIMPIAR_SERVIDOR", "LIMPIAR_SOCKETS" -> try {
                var segundos = 0
                try {
                    segundos = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Valor incorrecto (segundos)")
                    return
                }

                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se expulso " + ServidorServer.borrarClientesBug(segundos)
                            + " clientes bugeados"
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "EXCEPTION COMANDO")
            }

            // case "ACTUALIZAR_NPC" :
            // case "ACCESSORIES_NPC" :
            // case "STUFF_NPC" :
            // try {
            // numInt = Integer.parseInt(infos[1]);
            // } catch (final Exception e) {}
            // if (Mundo.getNPCModelo(numInt) == null) {
            // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "NPC modelo invalido");
            // return;
            // }
            // objetivo = _perso;
            // if (infos.length > 2) {
            // objetivo = Mundo.getPersonajePorNombre(infos[2]);
            // }
            // if (objetivo == null) {
            // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe");
            // return;
            // }
            // NPCModelo npcMod = Mundo.getNPCModelo(numInt);
            // try {
            // npcMod.modificarNPC(objetivo.getGfxID(false), objetivo.getSexo(), objetivo.getColor1(),
            // objetivo.getColor2(),
            // objetivo.getColor3(), objetivo.getStringAccesorios());
            // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se actualizo el NPC modelo " + numInt +
            // ", Nombre: "
            // + npcMod.getNombre() + ", Gfx: " + npcMod.getGfxID() + ", Accesorios: " +
            // npcMod.getAccesoriosInt());
            // } catch (Exception e) {}
            // break;
            "PANEL_ADMIN", "MENU_ADMIN" -> {
                try {
                    val mapa2 = _perso.mapa!!
                    GestorSalida.enviarEnCola(
                        _perso, "ÑP" + mapa2.capabilitiesCompilado.toString() + "|" + mapa2.maxGrupoDeMobs.toString()
                                + "|" + mapa2.maxMobsPorGrupo.toString() + "|" + mapa2.maxMercantes.toString(), true
                    )
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
                }
            }
            "A" -> {
                infos = mensaje.split(" ".toRegex(), 2).toTypedArray()
                try {
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Falta argumentos")
                }

            }
            "GET_LISTA_PACKETS_COLA" -> try {
                val packetsCola = _perso.packetsCola.replace((Constantes.x0char + "").toRegex(), "\n")
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Lista packets en cola:\n$packetsCola")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "HACER_ACCION", "ACCION" -> try {
                objetivo = _perso
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecte.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                var tipoAccion = 0
                var args = ""
                try {
                    tipoAccion = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "ID Accion incorrecta")
                    return
                }

                if (infos.size > 3) {
                    args = infos[3]
                }
                Accion.realizar_Accion_Estatico(tipoAccion, args, objetivo, null, -1, (-1).toShort())
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El personaje " + objetivo.nombre + " realizo la accion "
                            + tipoAccion + " con los argumentos " + args
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
            }

            "FIJAR_STATS_MOB", "FIJAR_STATS", "SET_STATS_MOB", "FIJAR_DAÑOS", "FIJAR_DAÑO", "MODIFICAR_STATS_MOB" -> try {
                var id = 0
                var grado: Byte = 0
                var stats = ""
                try {
                    id = Integer.parseInt(infos[1])
                    grado = java.lang.Byte.parseByte(infos[2])
                    stats = infos[3]
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                val mobModelo = Mundo.getMobModelo(id)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "MobModelo " + infos[1] + " no existe")
                    return
                }
                val mGrado = mobModelo.getGradoPorGrado(grado)
                if (mGrado == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "MobGradorModelo " + infos[1] + "-" + infos[2] + " no existe"
                    )
                    return
                }
                if (mobModelo.modificarStats(grado, stats)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "El mob " + mobModelo.nombre + " (" + mobModelo.id
                                + ") g: " + grado + " lvl: " + mobModelo.getGradoPorGrado(grado)!!.nivel + " ha sido modificado stats a "
                                + stats
                    )
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "STATS_DEFECTO_MOB" -> try {
                var id = 0
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                val mobModelo = Mundo.getMobModelo(id)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                GestorSalida.ENVIAR_ÑJ_STATS_DEFECTO_MOB(_perso, mobModelo.strStatsTodosMobs())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "TEST_DAÑO" -> try {
                var id = 0
                var grado: Byte = 1
                var stats = ""
                try {
                    id = Integer.parseInt(infos[1])
                    grado = java.lang.Byte.parseByte(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                try {
                    stats = infos[3]
                } catch (ignored: Exception) {
                }

                val mobModelo = Mundo.getMobModelo(id)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BN_NADA(_perso)
                    return
                }
                GestorSalida.ENVIAR_ÑK_TEST_DAÑO_MOB(_perso, mobModelo.calculoDaño(grado, stats))
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "MAX_PELEAS_MAPA", "MAP_MAX_FIGHTS", "MAX_FIGHTS_MAP" -> try {
                var maxPeleas: Byte = 0
                var mapaID: Short = 0
                try {
                    maxPeleas = java.lang.Byte.parseByte(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                try {
                    mapaID = java.lang.Short.parseShort(infos[2])
                    if (Mundo.getMapa(mapaID) != null) {
                        mapa = Mundo.getMapa(mapaID)
                    }
                } catch (ignored: Exception) {
                }

                mapa!!.setMaxPeleas(maxPeleas)
                GestorSQL.UPDATE_MAPA_MAX_PELEAS(mapa.id, maxPeleas)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El mapa " + mapa.id + " cambio el valor de maximo de pleas a "
                            + maxPeleas
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "MODIFICAR_NPC" -> try {
                var id = 0
                var sexo: Byte = 1
                var escalaX: Short = 100
                var escalaY: Short = 100
                var gfxID: Short = 9999
                var color1 = -1
                var color2 = -1
                var color3 = -1
                var arma = 0
                var sombrero = 0
                var capa = 0
                var mascota = 0
                var escudo = 0
                try {
                    id = java.lang.Short.parseShort(infos[1]).toInt()
                } catch (ignored: Exception) {
                }

                try {
                    sexo = java.lang.Byte.parseByte(infos[2])
                } catch (ignored: Exception) {
                }

                try {
                    escalaX = java.lang.Short.parseShort(infos[3])
                } catch (ignored: Exception) {
                }

                try {
                    escalaY = java.lang.Short.parseShort(infos[4])
                } catch (ignored: Exception) {
                }

                try {
                    gfxID = java.lang.Short.parseShort(infos[5])
                } catch (ignored: Exception) {
                }

                try {
                    color1 = Integer.parseInt(infos[6])
                } catch (ignored: Exception) {
                }

                try {
                    color2 = Integer.parseInt(infos[7])
                } catch (ignored: Exception) {
                }

                try {
                    color3 = Integer.parseInt(infos[8])
                } catch (ignored: Exception) {
                }

                try {
                    arma = Integer.parseInt(infos[9])
                } catch (ignored: Exception) {
                }

                try {
                    sombrero = Integer.parseInt(infos[10])
                } catch (ignored: Exception) {
                }

                try {
                    capa = Integer.parseInt(infos[11])
                } catch (ignored: Exception) {
                }

                try {
                    mascota = Integer.parseInt(infos[12])
                } catch (ignored: Exception) {
                }

                try {
                    escudo = Integer.parseInt(infos[13])
                } catch (ignored: Exception) {
                }

                val npcMod = Mundo.getNPCModelo(id)
                if (npcMod == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "NPC $id no existe")
                }
                npcMod!!.modificarNPC(sexo, escalaX, escalaY, gfxID.toInt(), color1, color2, color3)
                npcMod.setAccesorios(arma, sombrero, capa, mascota, escudo)
                GestorSQL.UPDATE_NPC_MODELO(npcMod, arma, sombrero, capa, mascota, escudo)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se modifico el NPC " + id + " con las sig. caracteristicas, GFX: "
                            + gfxID + " SEX: " + sexo + " ESCALA X: " + escalaX + " ESCALA Y: " + escalaY + " COLOR1: " + color1
                            + " COLOR2: " + color2 + " COLOR3: " + color3 + " ACCES: " + npcMod.accesoriosInt
                )
                GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(_perso, "NPC MODIFICADO!! :D")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "CELDA_COORD", "CELL_COORD", "EJES_CELDA", "POS_CELDA", "COORD_CELDA", "CELDA_POS" -> try {
                var celdaID: Short = 0
                celdaID = try {
                    java.lang.Short.parseShort(infos[1])
                } catch (e: Exception) {
                    _perso.celda.id
                }

                try {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Las coordenadas de la celda $celdaID es, X: " + mapa!!
                            .getCelda(celdaID)!!.coordX + ", Y: " + mapa.getCelda(celdaID)!!.coordY
                    )
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Celda No existe")
                    return
                }

            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "TEST_CELDAS" -> try {
                var celdaID: Short = 0
                try {
                    celdaID = java.lang.Short.parseShort(infos[1])
                } catch (e: Exception) {
                    return
                }

                try {
                    val s = StringBuilder()
                    for (c in Camino.celdasPorDistancia(_perso.celda, _perso.mapa, celdaID.toInt())) {
                        s.append(c.toInt()).append(",")
                        GestorSalida.ENVIAR_GDO_OBJETO_TIRAR_SUELO(_perso, '+', c, 311, false, "")
                    }
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Las celdas a mostrar son $s")
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Celda No existe")
                }

            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            // case "ADD_CAPTCHA" :
            // try {
            // infos = mensaje.split(" ", 2);
            // Mundo.Captchas.add(infos[1]);
            // GestorSQL.INSERT_CAPTCHA(infos[1].split("\\|")[0], infos[1].split(Pattern.quote("|"))[1]);
            // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se agrego la captcha: " +
            // infos[1].split(Pattern.quote("|"))[0]
            // + " y respuesta: "
            // + infos[1].split(Pattern.quote("|"))[1]);
            // } catch (final Exception e) {
            // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception");
            // }
            // break;
            "CONSULTAR_OGRINAS", "GET_OGRINAS", "CONSULTA_OGRINAS", "CONSULTA_PUNTOS", "GET_POINTS" -> {
                try {
                    objetivo = _perso
                    if (infos.size > 1) {
                        objetivo = Mundo.getPersonajePorNombre(infos[1])
                    }
                    if (objetivo == null) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
                }

                try {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "Le joueur " + objetivo!!.nombre + " possède " + GestorSQL
                                .GET_OGRINAS_CUENTA(objetivo.cuentaID) + " ogrines/points"
                        )
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "El personaje " + objetivo!!.nombre + " posee " + GestorSQL
                                .GET_OGRINAS_CUENTA(objetivo.cuentaID)
                        )
                    }
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
                }

            }
            "SEGUNDOS_TURNO_PELEA", "TIEMPO_TURNO_PELEA", "RATE_TIEMPO_PELEA" -> try {
                var segundos = 0
                try {
                    segundos = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.SEGUNDOS_TURNO_PELEA = segundos
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se modifico el SEGUNDOS_TURNO_PELEA a "
                            + AtlantaMain.SEGUNDOS_TURNO_PELEA + " segundos"
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "MINUTOS_ALIMENTACION_MASCOTA", "TIEMPO_ALIMENTACION", "RATE_TIEMPO_ALIMENTACION" -> try {
                var minutos = 0
                try {
                    minutos = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.MINUTOS_ALIMENTACION_MASCOTA = minutos
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se modifico el MINUTOS_ALIMENTACION_MASCOTA a "
                            + AtlantaMain.MINUTOS_ALIMENTACION_MASCOTA + " minutos"
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "SEGUNDOS_MOVERSE_MONTURAS", "TIEMPO_MOVERSE_PAVOS", "RATE_TIEMPO_MOV_PAVO" -> try {
                var segundos = 0
                try {
                    segundos = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.SEGUNDOS_MOVER_MONTURAS = segundos
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "El Tiempo para que los dragopavos se muevan automaticamente ha sido modificado a "
                            + AtlantaMain.SEGUNDOS_MOVER_MONTURAS + " segundos"
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "MINUTOS_PARIR_MONTURA", "TIEMPO_PARIR", "RATE_TIEMPO_PARIR" -> try {
                var minutos = 0
                try {
                    minutos = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.MINUTOS_GESTACION_MONTURA = minutos
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se modifico el MINUTOS_PARIR_MONTURA a "
                            + AtlantaMain.MINUTOS_GESTACION_MONTURA + " minutos"
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "RATE_FM", "DIFICULTAD_FM" -> try {
                var rate: Byte = 0
                try {
                    rate = java.lang.Byte.parseByte(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.RATE_FM = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el RATE_FM a " + AtlantaMain.RATE_FM)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "RATE_PODS" -> try {
                var rate: Byte = 0
                try {
                    rate = java.lang.Byte.parseByte(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.RATE_PODS = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el RATE_PODS a " + AtlantaMain.RATE_PODS)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "RATE_CAPTURA_PAVOS" -> try {
                var rate: Byte = 0
                try {
                    rate = java.lang.Byte.parseByte(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.RATE_CAPTURA_MONTURA = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "Se modifico el RATE_CAPTURA_MONTURA a " + AtlantaMain.RATE_CAPTURA_MONTURA
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "RATE_KAMAS" -> try {
                var rate: Byte = 0
                try {
                    rate = java.lang.Byte.parseByte(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.RATE_KAMAS = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el RATE_KAMAS a " + AtlantaMain.RATE_KAMAS)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "RATE_DROP" -> try {
                var rate: Byte = 0
                try {
                    rate = java.lang.Byte.parseByte(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.RATE_DROP_NORMAL = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el RATE_DROP a " + AtlantaMain.RATE_DROP_NORMAL)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "RATE_PVM", "RATE_XP_PVM", "RATE_EXP_PVM" -> try {
                var rate = 0f
                try {
                    rate = java.lang.Float.parseFloat(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.RATE_XP_PVM = rate
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el RATE_XP_PVM a " + AtlantaMain.RATE_XP_PVM)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "RATE_RANDOM_MOB" -> try {
                val rate: Float
                var ratebase = 0f
                var estrellas = 1
                try {
                    ratebase = java.lang.Float.parseFloat(infos[1])
                    rate = java.lang.Float.parseFloat(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.RATE_RANDOM_MOB_BASE = ratebase.toDouble()
                AtlantaMain.RATE_RANDOM_MOB = rate.toDouble()
                if (rate == 1f && ratebase == 1f) {
                    Mundo.NormalizarMobs()
                }
                if (infos.size > 3) {
                    estrellas = Integer.parseInt(infos[3])
                    Mundo.subirEstrellasMobs(estrellas)
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "Se modifico el Rango del rate mob de " + AtlantaMain.RATE_RANDOM_MOB_BASE + " a " + AtlantaMain.RATE_RANDOM_MOB
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
                e.printStackTrace()
            }

            "RATE_RANDOM_ITEM" -> try {
                val rate: Float
                var ratebase = 0f
                try {
                    ratebase = java.lang.Float.parseFloat(infos[1])
                    rate = java.lang.Float.parseFloat(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.RATE_RANDOM_ITEM = rate.toDouble()
                AtlantaMain.RATE_RANDOM_ITEM_BASE = ratebase.toDouble()
                Mundo.OBJETOS_SETS.clear()
                GestorSQL.CARGAR_OBJETOS_SETS()
                for (a in Mundo._PERSONAJES.values) {
                    if (a.enLinea()) {
                        a.enviarBonusSet()
                    }
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "Se modifico el Rango del rate item de " + AtlantaMain.RATE_RANDOM_ITEM_BASE + " a " + AtlantaMain.RATE_RANDOM_ITEM
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "RATE_PVP", "RATE_XP_PVP", "RATE_EXP_PVP" -> try {
                var rate: Byte = 0
                try {
                    rate = java.lang.Byte.parseByte(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.RATE_XP_PVP = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el RATE_XP_PVP a " + AtlantaMain.RATE_XP_PVP)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "RATE_OFICIO", "RATE_XP_OFICIO", "RATE_EXP_OFICIO", "RATE_METIER" -> try {
                var rate: Byte = 0
                try {
                    rate = java.lang.Byte.parseByte(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.RATE_XP_OFICIO = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "Se modifico el RATE_XP_OFICIO a " + AtlantaMain.RATE_XP_OFICIO
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "RATE_CRIANZA_PAVOS", "RATE_CRIANZA_MONTURA", "RATE_CRIANZA", "RATE_ELEVAGE" -> try {
                var rate: Byte = 0
                try {
                    rate = java.lang.Byte.parseByte(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.RATE_CRIANZA_MONTURA = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "Se modifico el RATE_CRIANZA_MONTURA a " + AtlantaMain.RATE_CRIANZA_MONTURA
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "RATE_HONOUR", "RATE_HONOR" -> try {
                var rate: Byte = 0
                try {
                    rate = java.lang.Byte.parseByte(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.RATE_HONOR = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el RATE_HONOR a " + AtlantaMain.RATE_HONOR)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "RATE_DROP_ARMAS_ETEREAS" -> try {
                var rate: Byte = 0
                try {
                    rate = java.lang.Byte.parseByte(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.RATE_DROP_ARMAS_ETEREAS = rate.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "Se modifico el RATE_DROP_ARMAS_ETEREAS a " + AtlantaMain.RATE_DROP_ARMAS_ETEREAS
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "MAX_REPORTES", "LIMITE_MOSTRAR_REPORTES", "MAX_MOSTRAR_REPORTES", "LIMITE_REPORTES" -> try {
                var limite: Short = 0
                try {
                    limite = java.lang.Short.parseShort(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.LIMITE_REPORTES = limite.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se configuro a $limite el limite de reportes")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "LIMIT_LADDER", "LIMITE_LADDER" -> try {
                var limite: Short = 0
                try {
                    limite = java.lang.Short.parseShort(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.LIMITE_LADDER = limite.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se configuro a $limite el limite del ladder")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "TIEMPO_MOSTRAR_BOTON_VOTO", "MINUTOS_SPAMEAR_BOTON_VOTO" -> try {
                var minutos = 0
                try {
                    minutos = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.MINUTOS_SPAMEAR_BOTON_VOTO = minutos
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se configuro a " + AtlantaMain.MINUTOS_SPAMEAR_BOTON_VOTO
                            + " minutos para mostrar boton voto"
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "TIEMPO_ESTRELLAS_MOBS", "TIEMPO_MOB_ESTRELLAS", "SEGUNDOS_ESTRELLAS_MOBS" -> try {
                var segundos = 0
                try {
                    segundos = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.SEGUNDOS_ESTRELLAS_GRUPO_MOBS = segundos
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se configuro a " + AtlantaMain.SEGUNDOS_ESTRELLAS_GRUPO_MOBS
                            + " segundos la recarga de estrellas de mobs"
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "PROBABILIDAD_ARCHI_MOBS" -> try {
                var probabilidad = 0
                try {
                    probabilidad = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.PROBABILIDAD_ARCHI_MOBS = probabilidad
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se configuro a " + AtlantaMain.PROBABILIDAD_ARCHI_MOBS
                            + " probabilidad de archi mobs"
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "TIEMPO_ESTRELLAS_RECURSOS", "TIEMPO_RECURSOS_ESTRELLAS", "SEGUNDOS_ESTRELLAS_RECURSOS" -> try {
                var segundos = 0
                try {
                    segundos = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.SEGUNDOS_ESTRELLAS_RECURSOS = segundos
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se configuro a " + AtlantaMain.SEGUNDOS_ESTRELLAS_RECURSOS
                            + " segundos la recarga de estrellas de Recursos"
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "RESSOURCES_MAP_STARS", "STARS_RESSOURCES_MAP", "ESTRELLAS_RECURSOS_MAPA", "SUBIR_ESTRELLAS_RECURSOS_MAPA" -> try {
                var estrellas = 0
                estrellas = try {
                    Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    1
                }

                _perso.mapa.subirEstrellasOI(estrellas)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se subio $estrellas estrellas recursos a este mapa")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "STARS_RESSOURCES", "RESSOURCES_STARS", "ESTRELLAS_RECURSOS", "SUBIR_ESTRELLAS_RECURSOS" -> try {
                var estrellas = 0
                estrellas = try {
                    Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    1
                }

                Mundo.subirEstrellasOI(estrellas)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se subio $estrellas estrellas a todos los recursos")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "MAPA_STARS_MOBS", "MAPA_MOBS_STARS", "MAP_MOBS_STARS", "MAP_STARS_MOBS", "MOB_MAP_STARS", "STARS_MOBS_MAP", "UP_STARS_MOBS_MAP", "ESTRELLAS_MAPA_MOBS", "MOBS_ESTRELLAS_MAPA", "MOB_ESTRELLAS_MAPA", "ESTRELLAS_MOBS_MAPA", "MAPA_ESTRELLAS_MOBS", "MAPA_ESTRELLAS_MOB", "MAPA_MOBS_ESTRELLAS", "MAPA_MOB_ESTRELLAS", "ESTRELLAS_MOB_MAPA", "SUBIR_ESTRELLAS_MOBS_MAPA" -> try {
                var estrellas = 0
                estrellas = try {
                    Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    1
                }

                mapa!!.subirEstrellasMobs(estrellas)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se subio " + estrellas + " estrellas mob al mapa " + mapa.id)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "MOBS_ESTRELLAS_TODOS", "MOB_ESTRELLAS_TODOS", "MOBS_STARS_TODOS", "TODOS_MOBS_ESTRELLAS", "TODOS_MOBS_STARS", "TODOS_STARS_MOBS", "TODOS_ESTRELLAS_MOBS", "ALL_MOBS_STARS", "ALL_MOBS_ESTRELLAS", "ALL_STARS_MOBS", "UP_ALL_STARS_MOBS", "ESTRELLAS_MOBS_TODOS", "SUBIR_ESTRELLAS_MOB_TODOS" -> try {
                var estrellas = 0
                estrellas = try {
                    Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    1
                }

                Mundo.subirEstrellasMobs(estrellas)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se subio $estrellas estrellas a todos los mobs")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "SHOW_A", "MOSTRAR_A", "SHOW_RESTRICTIONS_A", "MOSTRAR_RESTRICCIONES_A" -> try {
                objetivo = _perso
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, objetivo.mostrarmeA())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "SHOW_B", "MOSTRAR_B", "SHOW_RESTRICTIONS_B", "MOSTRAR_RESTRICCIONES_B" -> try {
                objetivo = _perso
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, objetivo.mostrarmeB())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "SET_RESTRICCIONES_A", "RESTRICCIONES_A", "MODIFICAR_A", "RESTRICCION_A" -> try {
                var restriccion = 0
                var modificador = 0
                try {
                    restriccion = Integer.parseInt(infos[1])
                    modificador = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                objetivo = _perso
                if (infos.size > 3) {
                    objetivo = Mundo.getPersonajePorNombre(infos[3])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.modificarA(restriccion, restriccion xor modificador)
                GestorSalida.ENVIAR_AR_RESTRICCIONES_PERSONAJE(objetivo)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se coloco la restriccionA " + objetivo.restriccionesA
                            + " al pj " + objetivo.nombre
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "SET_RESTRICCIONES_B", "RESTRICCIONES_B", "MODIFICAR_B", "RESTRICCION_B" -> try {
                var restriccion = 0
                var modificador = 0
                try {
                    restriccion = Integer.parseInt(infos[1])
                    modificador = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                objetivo = _perso
                if (infos.size > 3) {
                    objetivo = Mundo.getPersonajePorNombre(infos[3])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.modificarB(restriccion, restriccion xor modificador)
                objetivo.refrescarEnMapa()
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se coloco la restriccionB " + objetivo.restriccionesB
                            + " al pj " + objetivo.nombre
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "PANEL_ALL", "PANEL_ONLINE", "PANEL_TODOS" -> try {
                infos = mensaje.split(" ".toRegex(), 2).toTypedArray()
                GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION_TODOS(infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
            }
            "MSJ_PENDIENTE", "SMS", "MPENDIENTE" -> try {
                infos = mensaje.split(" ".toRegex(), 2).toTypedArray()
                for (c in Mundo.cuentas.values) {
                    if (!c.enLinea() || c.tempPersonaje == null) {
                        GestorSQL.MENSAJE_PENDIENTE(c, "SISTEMA", infos[1])
                    }
                }
                GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS("\n${ServidorServer.fechaConHora}\n[SISTEMA]: ${infos[1]}")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Error: $e")
            }

            "PANEL_ALONE", "PANEL", "PANEL_SOLO" -> try {
                infos = mensaje.split(" ".toRegex(), 2).toTypedArray()
                GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(_perso, infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
            }

            "PREMIO_CACERIA" -> try {
                Mundo.KAMAS_OBJ_CACERIA = infos[1] // kamas | id,cant;id,cant
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se fijo el premio de la caceria a: " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
            }

            "NOMBRE_CACERIA" -> try {
                Mundo.NOMBRE_CACERIA = infos[1]
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se fijo el nombre de la caceria a: " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
            }

            "LIMPIAR_CACERIA" -> try {
                Mundo.KAMAS_OBJ_CACERIA = ""
                Mundo.NOMBRE_CACERIA = ""
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se limpio el nombre caceria y premio caceria")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
            }

            "MAPAS_KOLISEO" -> try {
                AtlantaMain.MAPAS_KOLISEO = infos[1]
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se fijo la lista de mapas koliseo a: " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
            }

            "LISTA_RASTREOS" -> try {
                val strB = StringBuilder()
                for (i in ServidorSocket.RASTREAR_CUENTAS) {
                    if (strB.isNotEmpty()) {
                        strB.append(", ")
                    }
                    strB.append(i)
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Las ids de las cuentas que estan siendo rastreadas son " + strB
                        .toString()
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "BORRAR_RASTREOS" -> try {
                ServidorSocket.RASTREAR_CUENTAS.clear()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se limpio la lista de rastreados")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "SPY_PERSO", "SPY_PJ", "ESPIAR_PJ", "ESPIAR_JUGADOR", "SPY_PLAYER", "RASTREAR_PJ" -> try {
                try {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Personaje no existe")
                    return
                }

                if (!ServidorSocket.RASTREAR_CUENTAS.contains(Objects.requireNonNull(objetivo)!!.cuentaID)) {
                    ServidorSocket.RASTREAR_CUENTAS.add(objetivo!!.cuentaID)
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se agrego a la lista de rastreos: " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "SPY_ACCOUNT", "SPY_COMPTE", "RASTREAR_CUENTA" -> try {
                val cuenta: Cuenta?
                try {
                    cuenta = Mundo.getCuentaPorNombre(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Cuenta no existe")
                    return
                }

                if (cuenta == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Cuenta no existe")
                    return
                }
                if (!ServidorSocket.RASTREAR_CUENTAS.contains(cuenta.id)) {
                    ServidorSocket.RASTREAR_CUENTAS.add(cuenta.id)
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se agrego a la lista de rastreos: " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "MODIFY_OBJETIVE_MISION", "MODIFICA_OBJETIVO_MISION", "MODIFICAR_MISION_OBJETIVO", "MODIFICA_MISION_OBJETIVO", "MODIFICAR_OBJETIVO_MISION" -> try {
                var id = 0
                var args = ""
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                try {
                    args = infos[2]
                } catch (ignored: Exception) {
                }

                val objMision = Mundo.getMisionObjetivoModelo(id)
                if (objMision == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El objetivo mision no existe")
                    return
                }
                objMision.args = args
                GestorSQL.UPDATE_OBJETIVO_MISION(id, args)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El objetivo mision (" + id + ") tipo: " + objMision.tipo
                            + " ha modificado sus args a " + args
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "MODIFY_RECOMPENSE_STEP", "MODIFICA_RECOMPENSA_ETAPA", "MODIFICA_PREMIO_ETAPA", "MODIFICAR_RECOMPENSA_ETAPA" -> try {
                var id = 0
                var args = ""
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                try {
                    args = infos[2]
                } catch (ignored: Exception) {
                }

                val etapa = Mundo.getEtapa(id)
                if (etapa == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La etapa de mision no existe")
                    return
                }
                etapa.setRecompensa(args)
                GestorSQL.UPDATE_RECOMPENSA_ETAPA(id, args)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "La etapa de mision (" + id + ") " + etapa.nombre
                            + " ha modificado sus recompensas a " + args
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "MODIFICA_ETAPA", "MODIFY_STEP", "MODIFICAR_ETAPA" -> try {
                var id = 0
                var args = ""
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                try {
                    args = infos[2]
                } catch (ignored: Exception) {
                }

                val etapa = Mundo.getEtapa(id)
                if (etapa == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La etapa de mision no existe")
                    return
                }
                etapa.setObjetivos(args)
                GestorSQL.UPDATE_ETAPA(id, args)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "La etapa de mision (" + id + ") " + etapa.nombre
                            + " ha modificado sus objetivos a " + args
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "INFO_STEP", "INFO_STEPS", "INFO_ETAPAS", "INFO_ETAPA" -> try {
                var id = 0
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                val etapa = Mundo.getEtapa(id)
                if (etapa == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La etapa de mision no existe")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "La etapa de mision (" + id + ") " + etapa.nombre
                            + " tiene como objetivos: " + etapa.strObjetivos()
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "MODIFY_QUEST", "MODIFY_MISSION", "MODIFICAR_QUEST", "MODIFICAR_MISION", "MODIFICA_MISION", "MODIFICA_QUEST" -> try {
                var id = 0
                var pregDarMision = ""
                var pregMisCumplida = ""
                var pregMisIncompleta = ""
                var etapas = ""
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                try {
                    etapas = infos[2]
                } catch (ignored: Exception) {
                }

                try {
                    pregDarMision = infos[3]
                } catch (ignored: Exception) {
                }

                try {
                    pregMisCumplida = infos[4]
                } catch (ignored: Exception) {
                }

                try {
                    pregMisIncompleta = infos[5]
                } catch (ignored: Exception) {
                }

                val mision = Mundo.getMision(id)
                if (mision == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La mision no existe")
                    return
                }
                mision.setEtapas(etapas)
                mision.setPreguntas(pregDarMision, Mision.ESTADO_NO_TIENE)
                mision.setPreguntas(pregMisCumplida, Mision.ESTADO_COMPLETADO)
                mision.setPreguntas(pregMisIncompleta, Mision.ESTADO_INCOMPLETO)
                GestorSQL.UPDATE_MISION(id, etapas, pregDarMision, pregMisCumplida, pregMisIncompleta)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "La mision (" + id + ") " + mision.nombre
                            + " ha modificado sus etapas: " + etapas + ", pregDarMision: " + pregDarMision + ", pregMisCumplida: "
                            + pregMisCumplida + ", pregMisIncompleta: " + pregMisIncompleta
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "INFO_QUEST", "INFO_MISION" -> try {
                var id = 0
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                val mision = Mundo.getMision(id)
                if (mision == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La mision no existe")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "La mision (" + id + ") " + mision.nombre
                            + " tiene como info etapas: " + mision.strEtapas() + ", pregDarMision: " + mision.strMisionPregunta(
                        Mision.ESTADO_NO_TIENE
                    ) + ", pregMisCumplida: " + mision.strMisionPregunta(Mision.ESTADO_COMPLETADO)
                            + ", pregMisIncompleta: " + mision.strMisionPregunta(Mision.ESTADO_INCOMPLETO)
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "NIVEL_OBJETO_MODELO" -> try {
                val id = Integer.parseInt(infos[1])
                val nivel = java.lang.Short.parseShort(infos[2])
                val objModelo = Mundo.getObjetoModelo(id)
                if (objModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El objeto no existe")
                    return
                }
                objModelo.nivel = nivel
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se modifico el objeto (" + id + ") " + objModelo.nombre
                            + " con nivel " + objModelo.nivel
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
            }

            "GFX_OBJETO_MODELO" -> try {
                val id = Integer.parseInt(infos[1])
                val objModelo = Mundo.getObjetoModelo(id)
                if (objModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El objeto no existe")
                    return
                }
                objModelo.gfx = Integer.parseInt(infos[2])
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se modifico el objeto (" + id + ") " + objModelo.nombre
                            + " con gfx " + infos[2]
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
            }

            "PREPARAR_LISTA_NIVEL" -> try {
                Mundo.prepararListaNivel()
                GestorSalida.ENVIAR_ÑB_LISTA_NIVEL_TODOS()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se actualizo la lista de niveles modificados")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "PREPARAR_LISTA_GFX" -> try {
                Mundo.prepararListaGFX()
                GestorSalida.ENVIAR_ÑA_LISTA_GFX_TODOS()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se actualizo la lista de GFXs modificados")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "AGREGAR_MOBS_MAPA", "ADD_MOBS_MAPA", "ADD_MOBS", "AGREGAR_MOBS", "INSERTAR_MOBS" -> try {
                var mobs = ""
                if (infos.size > 1) {
                    mobs = infos[1]
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                    return
                }
                try {
                    GestorSQL.UPDATE_SET_MOBS_MAPA(mapa!!.id.toInt(), mobs)
                    mapa.insertarMobs(mobs)
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Has cometido algun error")
                    return
                }

                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se inserto la lista de mobs " + mobs + " al mapa " + mapa.id)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "SET_DATE_MAP", "SET_FECHA_MAPA" -> try {
                var mapaID: Short = 0
                var date = ""
                try {
                    mapaID = java.lang.Short.parseShort(infos[1])
                    date = infos[2]
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                mapa = Mundo.getMapa(mapaID)
                if (mapa == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mapa no existe")
                    return
                }
                mapa.fecha = date
                GestorSQL.UPDATE_FECHA_MAPA(mapa.id, date)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se cambio la fecha del mapa " + mapa.id + " a " + date)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "REFRESH_MAP", "REFRESCAR_MAPA", "RELOAD_MAP", "RECARGAR_MAPA", "RELOAD_DATE_KEY_MAPDATA_MAP", "MAP_OLD", "MAPA_VIEJO", "CAMBIAR_MAPA_VIEJO", "CHANGE_MAP_OLD" -> try {
                var mapaID: Short = 0
                try {
                    mapaID = java.lang.Short.parseShort(infos[1])
                } catch (ignored: Exception) {
                }

                val key = GestorSQL.GET_NUEVA_FECHA_KEY(mapaID).split(Pattern.quote("|").toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                // if (key.length < 20) {
                // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "tiene una key muy corta");
                // }
                mapa = Mundo.getMapa(mapaID)
                if (mapa == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mapa no existe")
                    return
                }
                mapa.setKeyMapData(key[0], key[1], key[2])
                GestorSQL.UPDATE_FECHA_KEY_MAPDATA(mapa.id, key[0], key[1], key[2])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se cambio el mapa " + mapa.id + " por un mapa con key")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "LIST_MOBS", "LISTA_MOBS" -> try {
                var id = 0
                val strB = StringBuilder()
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
                    return
                }

                for (mMod in Mundo.MOBS_MODELOS.values) {
                    if (mMod.tipoMob.toInt() == id) {
                        strB.append("ID: ").append(mMod.id).append(" - Nombre: ").append(mMod.nombre)
                            .append(" - Niveles: ").append(
                                mMod
                                    .listaNiveles()
                            ).append(" - Colores: ").append(mMod.colores).append("\n")
                    }
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Los Mobs Tipo Criatura - " + Constantes.getNombreTipoMob(id)
                            + " son:\n" + strB.toString()
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "LIST_TYPE_MOBS", "LISTA_TIPO_MOBS", "LIST_CREATURES_TYPE", "LIST_TYPE_CREATURES", "LIST_TYPE_CRIATURES", "LISTA_TIPO_CRIATURAS" -> try {
                val strB = StringBuilder()
                for (i in -1..99) {
                    if (Constantes.getNombreTipoMob(i).isNotEmpty()) {
                        strB.append("Tipo ID: ").append(i).append(" - Criaturas: ")
                            .append(Constantes.getNombreTipoMob(i)).append("\n")
                    }
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Lista Tipo Criatura son:\n$strB")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "BORRAR_OTRO_INTERACTIVO", "DELETE_OTRO_INTERACTIVO", "ELIMINAR_OTRO_INTERACTIVO", "DEL_OTRO_INTERACTIVO", "BORRAR_OTRO_OI", "ELIMINAR_OTRO_OI", "DEL_OTRO_OI" -> try {
                var id = 0
                var mapaID: Short = 0
                var celdaID: Short = 0
                try {
                    id = Integer.parseInt(infos[1])
                    mapaID = java.lang.Short.parseShort(infos[2])
                    celdaID = java.lang.Short.parseShort(infos[3])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                    return
                }

                Mundo.borrarOtroInteractivo(id, mapaID, celdaID, 0, false)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se borraron todos los otros interactivos con gfxID: " + id
                            + " mapaID: " + mapaID + " celdaID: " + celdaID
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "ADD_OTRO_INTERACTIVO", "ADD_OTRO_OI", "AGREGAR_OTRO_INTERACTIVO" -> try {
                var id = 0
                var accionID = 0
                var tiempoRecarga = 0
                var mapaID: Short = 0
                var celdaID: Short = 0
                var args = ""
                var condicion = ""
                var descripcion = ""
                val strB = StringBuilder()
                try {
                    id = Integer.parseInt(infos[1])
                    mapaID = java.lang.Short.parseShort(infos[2])
                    celdaID = java.lang.Short.parseShort(infos[3])
                    accionID = Integer.parseInt(infos[4])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                    return
                }

                try {
                    args = infos[5]
                } catch (ignored: Exception) {
                }

                try {
                    condicion = infos[6]
                } catch (ignored: Exception) {
                }

                try {
                    tiempoRecarga = Integer.parseInt(infos[7])
                } catch (ignored: Exception) {
                }

                try {
                    descripcion = infos[8]
                } catch (ignored: Exception) {
                }

                strB.append("Se creo accion para Otro Interactivo GfxID: ").append(id).append(", mapaID: ")
                    .append(mapaID.toInt()).append(", celdaID: ").append(celdaID.toInt()).append(", accionID: ")
                    .append(accionID).append(", args: ").append(args).append(", condicion: ").append(condicion)
                    .append(", tiempoRecarga: ").append(tiempoRecarga)
                Mundo.borrarOtroInteractivo(id, mapaID, celdaID, accionID, true)
                val otro = OtroInteractivo(id, mapaID, celdaID, accionID, args, condicion, tiempoRecarga)
                Mundo.addOtroInteractivo(otro)
                GestorSQL.INSERT_OTRO_INTERACTIVO(
                    id,
                    mapaID,
                    celdaID,
                    accionID,
                    args,
                    condicion,
                    tiempoRecarga,
                    descripcion
                )
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "MOSTRAR_OTROS_INTERACTIVOS", "MOSTRAR_OTROS_OIS", "LISTAR_OTROS_INTERACTIVOS", "LIST_OTROS_INTERACTIVOS", "SHOW_OTHER_INTERACTIVES", "LISTA_OTROS_OIS", "LISTAR_OTROS_OIS" -> try {
                var mapaID = mapa!!.id
                val strB = StringBuilder()
                try {
                    mapaID = java.lang.Short.parseShort(infos[1])
                } catch (ignored: Exception) {
                }

                for (oi in Mundo.OTROS_INTERACTIVOS) {
                    if (oi.mapaID != mapaID) {
                        continue
                    }
                    strB.append("\n")
                    strB.append("Mapa: ").append(oi.mapaID.toInt()).append(" Celda: ").append(oi.celdaID.toInt())
                        .append(" GfxID: ").append(oi.gfxID).append(" Accion: ").append(oi.accionID).append(" Args: ")
                        .append(oi.args).append(" Condicion: ").append(oi.condicion)
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Los otros interactivos son:$strB")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "ACCESO_ADMIN_MINIMO", "BLOQUEAR", "BLOCK_GM" -> try {
                var accesoGM: Byte = 0
                var botarRango: Byte = 0
                try {
                    accesoGM = java.lang.Byte.parseByte(infos[1])
                    botarRango = java.lang.Byte.parseByte(infos[2])
                } catch (ignored: Exception) {
                }

                AtlantaMain.ACCESO_ADMIN_MINIMO = accesoGM.toInt()
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "Le serveur est desormais accessible au joueur dont le GM est superieur à : $accesoGM"
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Server bloqueado a Nivel GM : $accesoGM")
                }
                if (botarRango > 0) {
                    for (pj in Mundo.PERSONAJESONLINE) {
                        if (pj.cuenta.admin < botarRango) {
                            try {
                                GestorSalida.ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(
                                    pj.servidorSocket!!,
                                    "19",
                                    "",
                                    ""
                                )
                                pj.servidorSocket!!.cerrarSocket(true, "command BLOCK GM")
                            } catch (ignored: Exception) {
                            }

                        }
                    }
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "Le joueurs dont le GM est inferieur à celui specifie ont ete expulses."
                        )
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "Los jugadores nivel GM inferior a " + botarRango
                                    + " han sido expulsados."
                        )
                    }
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "ADD_ACTION_REPONSE", "ADD_ACTION_ANSWER", "ADD_ACCION_RESPUESTA", "ADD_ACCIONES_RESPUESTA", "ADD_ACCIONES_RESPUESTAS", "FIJAR_ACCION_RESPUESTA", "AGREGAR_ACCION_RESPUESTA" -> try {
                infos = mensaje.split(" ".toRegex(), 5).toTypedArray()
                var id = 0
                var accionID = 0
                var args = ""
                var condicion = ""
                val strB = StringBuilder()
                try {
                    id = Integer.parseInt(infos[1])
                    accionID = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                    return
                }

                try {
                    args = infos[3]
                } catch (ignored: Exception) {
                }

                try {
                    condicion = infos[4]
                } catch (ignored: Exception) {
                }

                var respuesta: RespuestaNPC? = Mundo.getRespuestaNPC(id)
                if (respuesta == null) {
                    respuesta = RespuestaNPC(id)
                    Mundo.addRespuestaNPC(respuesta)
                }
                val accion = Accion(accionID, args, condicion)
                respuesta.addAccion(accion)
                strB.append("La accion respuesta ").append(respuesta.id).append(", accionID: ").append(accion.id)
                    .append(", args: ").append(
                        accion
                            .args
                    ).append(", condicion: ").append(accion.condicion).append(" agregada")
                if (GestorSQL.REPLACE_ACCIONES_RESPUESTA(
                        respuesta.id, accion.id, accion.args, accion
                            .condicion
                    )
                ) {
                    strB.append(" a la BDD")
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "DELETE_ACTIONS_ANSWER", "DELETE_ACTIONS_REPONSE", "DEL_ACTIONS_ANSWER", "DEL_ACTIONS_REPONSE", "REMOVE_ACTONS_REPONSE", "BORRAR_ACCIONES_RESPUESTA", "BORRAR_ACCION_RESPUESTA" -> try {
                var id = 0
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                    return
                }

                val respuesta = Mundo.getRespuestaNPC(id)
                if (respuesta == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Respuesta invalida")
                    return
                }
                respuesta.borrarAcciones()
                GestorSQL.DELETE_ACCIONES_RESPUESTA(id)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se borraron todas las acciones de la respuesta $id")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "FIX_QUESTION_NPC", "FIX_QUESTION", "FIJAR_PREGUNTA", "FIJAR_NPC_PREGUNTA", "FIJAR_PREGUNTA_NPC" -> try {
                var npcID = 0
                var preguntaID = 0
                val strB = StringBuilder()
                try {
                    npcID = Integer.parseInt(infos[1])
                    preguntaID = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                    return
                }

                val npcModelo = Mundo.getNPCModelo(npcID)
                if (npcModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "NPC no existe")
                    return
                }
                strB.append("Fija al NPC Modelo ").append(npcID).append(" - Nombre: ").append(npcModelo.nombre)
                    .append(", Pregunta: ").append(preguntaID)
                npcModelo.setPreguntaID(preguntaID)
                if (GestorSQL.UPDATE_NPC_PREGUNTA(npcID, preguntaID)) {
                    strB.append(" a la BDD")
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "ADD_ANSWERS", "FIX_ANSWERS", "FIX_REPONSES", "WRITE_ANSWERS", "FIJAR_RESPUESTAS", "FIJAR_RESPUESTAS_PREGUNTA", "SET_ANSWERS", "SET_RESPUESTAS" -> try {
                val strB = StringBuilder()
                var id = 0
                var args = ""
                var respuestas = ""
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                    return
                }

                try {
                    respuestas = infos[2]
                } catch (ignored: Exception) {
                }

                try {
                    args = infos[3]
                } catch (ignored: Exception) {
                }

                var pregunta: PreguntaNPC? = Mundo.getPreguntaNPC(id)
                if (pregunta == null) {
                    pregunta = PreguntaNPC(id, respuestas, args, "")
                    Mundo.addPreguntaNPC(pregunta)
                } else {
                    pregunta.setRespuestas(respuestas)
                    pregunta.params = args
                }
                strB.append("Parametros de la pregunta ").append(id).append(" => respuestas: ")
                    .append(pregunta.strRespuestas).append(", args: ").append(pregunta.params).append(", alternos: ")
                    .append(pregunta.strAlternos)
                if (GestorSQL.REPLACE_PREGUNTA_NPC(pregunta)) {
                    strB.append(" a la BDD")
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "SET_ALTERNOS_PREGUNTA", "SET_ALTERNOS_QUESTION", "SET_PREGUNTA_ALTERNAS", "SET_PREGUNTA_ALTERNOS", "FIJAR_PREGUNTA_ALTERNOS", "FIJAR_ALTERNOS", "FIJAR_ALTENOS_PREGUNTA", "FIJAR_PREGUNTA_ALTERNAS", "SET_ALTERNOS", "SET_QUESTIONS_CONDITIONS" -> try {
                var id = 0
                var alternos = ""
                val strB = StringBuilder()
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                    return
                }

                try {
                    alternos = infos[2]
                } catch (ignored: Exception) {
                }

                var pregunta: PreguntaNPC? = Mundo.getPreguntaNPC(id)
                if (pregunta == null) {
                    pregunta = PreguntaNPC(id, "", "", alternos)
                    Mundo.addPreguntaNPC(pregunta)
                } else {
                    pregunta.setPreguntasCondicionales(alternos)
                }
                strB.append("Parametros de la pregunta ").append(id).append(" => respuestas: ")
                    .append(pregunta.strRespuestas).append(", args: ").append(pregunta.params).append(", alternos: ")
                    .append(pregunta.strAlternos)
                if (GestorSQL.REPLACE_PREGUNTA_NPC(pregunta)) {
                    strB.append(" a la BDD")
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "BUSCAR_PREGUNTA", "BUSCAR_PREGUNTAS", "SEARCH_QUESTIONS", "SEARCH_QUESTION" -> try {
                infos = mensaje.split(" ".toRegex(), 2).toTypedArray()
                val buscar = infos[1]
                GestorSalida.enviar(_perso, "DBQ" + buscar.toUpperCase())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "BUSCAR_RESPUESTA", "BUSCAR_RESPUESTAS", "SEARCH_ANSWERS", "SEARCH_ANSWER" -> try {
                infos = mensaje.split(" ".toRegex(), 2).toTypedArray()
                val buscar = infos[1]
                GestorSalida.enviar(_perso, "DBA" + buscar.toUpperCase())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "SHOW_QUESTIONS", "SHOW_QUESTION", "LISTAR_PREGUNTAS", "LISTA_PREGUNTAS", "MOSTRAR_PREGUNTAS" -> try {
                for (ss in infos[1].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    if (ss.isEmpty()) {
                        continue
                    }
                    var respuestaID = 0
                    try {
                        respuestaID = Integer.parseInt(ss)
                    } catch (e: Exception) {
                        continue
                    }

                    val pregunta = Mundo.getPreguntaNPC(respuestaID) ?: continue
                    val sB = StringBuilder()
                    sB.append("\n\t--> Answers: ").append(pregunta.strRespuestas)
                    sB.append("\n\t--> Alternates: ").append(pregunta.strAlternos)
                    GestorSalida.enviar(_perso, "DLQ$respuestaID|$sB")
                }
                GestorSalida.enviar(_perso, "DX")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
            }

            "SHOW_ANSWER", "SHOW_ANSWERS", "SHOW_REPONSES", "LISTAR_RESPUESTAS", "LISTA_RESPUESTAS", "MOSTRAR_RESPUESTAS" -> try {
                for (ss in infos[1].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    if (ss.isEmpty()) {
                        continue
                    }
                    var respuestaID = 0
                    try {
                        respuestaID = Integer.parseInt(ss)
                    } catch (e: Exception) {
                        continue
                    }

                    val respuesta = Mundo.getRespuestaNPC(respuestaID) ?: continue
                    val sB = StringBuilder()
                    sB.append("\n\t--> Condition: ").append(respuesta.condicion)
                    for (a in respuesta.acciones) {
                        sB.append("\n\t--> Action ID: ").append(a.id).append(", Args: ").append(a.args)
                    }
                    GestorSalida.enviar(_perso, "DLA$respuestaID|$sB")
                }
                GestorSalida.enviar(_perso, "DX")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
            }

            "LISTAR_RESPUESTAS_POR_TIPO_Y_ARGS", "LISTAR_RESPUESTAS_POR_TIPO_ARGS", "LISTAR_RESPUESTAS_POR_TIPO_O_ARGS", "LISTAR_RESPUESTAS_TIPO_ARGS", "LISTAR_ACCIONES_RESPUESTAS_POR_TIPO_Y_ARGS", "LISTAR_ACCIONES_RESPUESTAS_POR_TIPO_ARGS", "LISTAR_ACCIONES_RESPUESTAS_POR_TIPO_O_ARGS", "LISTAR_ACCIONES_RESPUESTAS_TIPO_ARGS", "LISTA_RESPUESTAS_POR_TIPO_Y_ARGS", "LISTA_RESPUESTAS_POR_TIPO_ARGS", "LISTA_RESPUESTAS_POR_TIPO_O_ARGS", "LISTA_RESPUESTAS_TIPO_ARGS", "LISTA_ACCIONES_RESPUESTAS_POR_TIPO_Y_ARGS", "LISTA_ACCIONES_RESPUESTAS_POR_TIPO_ARGS", "LISTA_ACCIONES_RESPUESTAS_POR_TIPO_O_ARGS", "LISTA_ACCIONES_RESPUESTAS_TIPO_ARGS" -> try {
                var id = -100
                var args = ""
                val strB = StringBuilder()
                try {
                    id = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                try {
                    args = infos[2]
                } catch (ignored: Exception) {
                }

                for (respuesta2 in Mundo.NPC_RESPUESTAS.values) {
                    var b = false
                    for (a in respuesta2.acciones) {
                        if (id != -100 && a.id != id) {
                            continue
                        }
                        if (args.isNotEmpty() && !a.args.toUpperCase().contains(args.toUpperCase())) {
                            continue
                        }
                        b = true
                        break
                    }
                    if (!b) {
                        continue
                    }
                    if (strB.isNotEmpty()) {
                        strB.append("\n----------------------------------\n")
                    }
                    strB.append("Acciones de la respuesta ID ").append(respuesta2.id).append(", condicion: ").append(
                        respuesta2
                            .condicion
                    )
                    for (a in respuesta2.acciones) {
                        strB.append("\n\tAccion ID: ").append(a.id).append(", Args: ").append(a.args)
                    }
                }
                if (strB.isEmpty()) {
                    strB.append("No se encontraron respuestas con esos datos")
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "LISTA_PREGUNTAS_CON_RESPUESTAS", "LISTA_PREGUNTAS_CON_RESPUESTA", "LISTAR_PREGUNTAS_CON_RESPUESTAS", "LISTAR_PREGUNTAS_CON_RESPUESTA" -> try {
                var id = 0
                val strB = StringBuilder()
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
                    return
                }

                for (pregunta2 in Mundo.NPC_PREGUNTAS.values) {
                    if (pregunta2.respuestas.contains(id)) {
                        if (strB.isNotEmpty()) {
                            strB.append("\n")
                        }
                        strB.append("Respuestas de la pregunta ").append(pregunta2.id).append(", respuestas: ").append(
                            pregunta2
                                .strRespuestas
                        )
                    }
                }
                if (strB.isEmpty()) {
                    strB.append("No se encontraron preguntas con esos datos")
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "UPDATE_HOUSE", "MODIFY_HOUSE", "MODIFICAR_CASA", "UPDATE_CASA", "MAPA_DENTRO_CASA", "ACTUALIZAR_CASA" -> try {
                var mapaID: Short = 0
                var celdaID: Short = 0
                try {
                    mapaID = java.lang.Short.parseShort(infos[1])
                    celdaID = java.lang.Short.parseShort(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
                    return
                }

                var casa: Casa? = null
                val ancho = mapa!!.ancho.toShort()
                val dir = shortArrayOf((-ancho).toShort(), (-(ancho - 1)).toShort(), (ancho - 1).toShort(), ancho, 0)
                for (i in 0..4) {
                    casa = Mundo.getCasaPorUbicacion(mapa.id, _perso.celda.id + dir[i])
                    if (casa != null) {
                        break
                    }
                }
                if (casa == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No existe la casa")
                    return
                }
                casa.celdaIDDentro = celdaID
                casa.mapaIDDentro = mapaID
                GestorSQL.UPDATE_CELDA_MAPA_DENTRO_CASA(casa)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se actualizo la casa " + casa.id + " , con mapaID dentro: "
                            + mapaID + " y como celdaID dentro: " + celdaID
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "SHOW_STATS_OBJETO", "GET_STATS_OBJETO", "INFO_ITEM", "INFO_OBJETO" -> try {
                var id = -1
                try {
                    id = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                val obj = Mundo.getObjeto(id)
                if (obj == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto nulo")
                    return
                }
                val objModelo = obj.objModelo
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Info del objeto $id: \nNombre Objeto - " + objModelo
                        ?.nombre + "\nNivel - " + objModelo?.nivel + "\nTipo - " + objModelo?.tipo + "\nPosicion - " + obj
                        .posicion + "\nString Stats - " + obj.convertirStatsAString(true) + "\n---------------------------"
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "GUARDAR", "SALVAR", "SAVE" -> {
                if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_SALVANDO) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Une sauvegarde est dejà en cours.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "No se puede ejecutar el comando, porque el server ya se esta salvando"
                        )
                    }
                    return
                }
                AtlantaMain.redactarLogServidorln("Se uso el comando SALVAR (COMANDOS) por " + _perso.nombre)
                thread(true, true) { FuncionesParaThreads.SalvarServidor(false) }
//                ServidorThread.SalvarServidor(false)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Lancement de la sauvegarde...")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Salvando servidor...")
                }
                try {
                    Thread.sleep(500)
                    //					System.out.println("Estoy aqui");
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                while (Mundo.SALVANDO) {
                    try {
                        Thread.sleep(200)
                        //						System.out.println("Estoy aqui");
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se guardo el servidor")
            }
            "GUARDAR_TODOS", "SALVAR_TODOS", "SAVE_ALL" -> {
                if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_SALVANDO) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Une sauvegarde est dejà en cours.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "No se puede ejecutar el comando, porque el server ya se esta salvando"
                        )
                    }
                    return
                }
                AtlantaMain.redactarLogServidorln("Se uso el comando SALVAR (COMANDOS) por " + _perso.nombre)
                thread(true, true) { FuncionesParaThreads.SalvarServidor(true) }
//                ServidorThread.SalvarServidor(true)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Lancement de la sauvegarde ONLINE Y OFFLINE ...")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Salvando servidor ONLINE Y OFFLINE ...")
                }
                try {
                    Thread.sleep(500)
                    //					System.out.println("Estoy aqui");
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                while (Mundo.SALVANDO) {
                    try {
                        Thread.sleep(200)
                        //						System.out.println("Estoy aqui");
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se guardo el servidor")
            }
            "SECONDS_ON", "SECONDS_ONLINE", "SEGUNDOS_ON" -> GestorSalida.ENVIAR_BAT2_CONSOLA(
                _perso, "El servidor tiene " + Formulas.segundosON()
                        + " segundos ONLINE"
            )
            "PASS_TURN", "PASAR_TURNO", "FIN_TURNO", "END_TURN", "CHECK_TURNO", "DEBUG_TURN" -> {
                if (_perso.pelea == null) {
                    return
                }
                val finTurno = _perso.pelea.pasarTurno(null)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Debug du tour..$finTurno")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se verifico el pase de turno $finTurno")
                }
            }
            "SET_TEMP" -> try {
                val strB = StringBuilder()
                if (_perso.pelea == null) {
                    return
                }
                try {
                    strB.append(infos[1])
                } catch (ignored: Exception) {
                }

                _perso.pelea.setTempAccion(strB.toString())
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se asigno la tempAccion: $strB")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "COUNT_OIS", "CONTAR_OBJETOS_INTERACTIVOS", "CONTAR_OIS" -> try {
                var cantidad = 0
                for (celda in mapa!!.celdas.values) {
                    if (celda.objetoInteractivo != null) {
                        cantidad++
                    }
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Este mapa tiene $cantidad interactivos")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "SHOW_OIS",
                // case "MOSTRAR_OTROS_INTERACTIVOS" :
            "MOSTRAR_OBJETOS_INTERACTIVOS", "MOSTRAR_OIS" -> try {
                val strB = StringBuilder()
                for (celda in mapa!!.celdas.values) {
                    if (celda.objetoInteractivo != null) {
                        strB.append("\n")
                        strB.append("Mapa: ").append(mapa.id.toInt()).append(" Celda: ").append(celda.id.toInt())
                            .append(" Movimiento: ").append(celda.movimiento.toInt()).append(" Gfx: ")
                            .append(celda.objetoInteractivo!!.gfxID)
                        try {
                            strB.append(" ID ObjInt: ").append(celda.objetoInteractivo!!.objIntModelo!!.id)
                        } catch (ignored: Exception) {
                        }

                    }
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Este mapa tiene:$strB")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "RESETEAR_OBJETOS_INTERACTIVOS", "RESET_OBJETOS_INTERACTIVOS", "RESETEAR_OIS", "REINICIAR_OIS", "REFRESCAR_OIS" -> try {
                var cantidad = 0
                val strB = StringBuilder()
                for (celda in mapa!!.celdas.values) {
                    if (celda.objetoInteractivo != null) {
                        if (strB.isNotEmpty()) {
                            strB.append("|")
                        }
                        celda.objetoInteractivo!!.recargando(true)
                        strB.append(celda.id.toInt()).append(";").append(celda.objetoInteractivo!!.infoPacket)
                        cantidad++
                    }
                }
                GestorSalida.ENVIAR_GDF_FORZADO_MAPA(mapa, strB.toString())
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se ha refrescado $cantidad interactivos")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "MAX_MERCANTES", "MAX_MERCHANTS" -> try {
                var limite: Byte = 0
                try {
                    limite = java.lang.Byte.parseByte(infos[1])
                } catch (ignored: Exception) {
                }

                if (limite < 0) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                mapa!!.maxMercantes = limite
                if (!GestorSQL.UPDATE_MAPA_MAX_MERCANTES(mapa.id, limite)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio un error al guardar la actualizacion en la BD.")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "En el mapa " + mapa.id
                            + " el max de mercantes ha sido modificado a " + limite
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "MOBS_FOR_GROUP", "MAX_MOBS_POR_GRUPO", "MOBS_POR_GRUPO" -> try {
                var limite: Byte = 0
                try {
                    limite = java.lang.Byte.parseByte(infos[1])
                } catch (ignored: Exception) {
                }

                if (limite <= 0) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                mapa!!.maxMobsPorGrupo = limite
                if (!GestorSQL.UPDATE_MAPA_MAX_MOB_GRUPO(mapa.id.toInt(), limite)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio un error al guardar la actualizacion en la BD.")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "En el mapa " + mapa.id
                            + " el maximo de mobs por grupo ha sido modificado a " + limite
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "GRUPO_MOBS_POR_MAPA", "MAX_GROUP_MOBS", "MAX_GRUPO_MOBS" -> try {
                var limite: Byte = 0
                try {
                    limite = java.lang.Byte.parseByte(infos[1])
                } catch (ignored: Exception) {
                }

                if (limite < 0) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                mapa!!.maxGrupoDeMobs = limite
                if (!GestorSQL.UPDATE_MAPA_MAX_GRUPO_MOBS(mapa.id.toInt(), limite)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio un error al guardar la actualizacion en la BD.")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "En el mapa " + mapa.id
                            + " el maximo de grupo mobs ha sido modificado a " + limite
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "REBOOT", "RESET", "SALIR", "RESETEAR", "EXIT" -> try {
                if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_SALVANDO) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "Le serveur est en cours de sauvegarde, impossible de reboot."
                        )
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "No se puede cerrar, porque el server se esta guardando, intentar en 5 minutos"
                        )
                    }
                    return
                }
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Reboot maintenant.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se esta cerrando el server")
                }
                if (infos.size > 1) {
                    thread(true, true) { FuncionesParaThreads.Reiniciar(1) }
//                    Reiniciar(1)
                } else {
                    Consola.leerComandos("salir", "")
//                    Reiniciar(0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "INFO_DROP_MOB_OBJETO", "DROP_POR_OBJETO_MOB" -> try {
                var mobID = 0
                var objModID = 0
                try {
                    mobID = Integer.parseInt(infos[1])
                    objModID = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Error con los argumentos")
                    return
                }

                val mobModelo = Mundo.getMobModelo(mobID)
                val objModelo = Mundo.getObjetoModelo(objModID)
                if (mobModelo == null || objModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto o mob nulos")
                    return
                }
                for (drop in mobModelo.drops) {
                    if (drop.IDObjModelo == objModID) {
                        GestorSalida.enviarEnCola(
                            _perso, "Ñd" + drop.prospeccion + ";" + drop.porcentaje * 1000 + ";"
                                    + drop.maximo, false
                        )
                        break
                    }
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "ADD_DROP", "AGREGAR_DROP" -> try {
                var mobID = 0
                var objModID = 0
                var prospecc = 0
                var max = 0
                var porcentaje = 0f
                var condicion = ""
                try {
                    mobID = Integer.parseInt(infos[1])
                    objModID = Integer.parseInt(infos[2])
                    prospecc = Integer.parseInt(infos[3])
                    porcentaje = java.lang.Float.parseFloat(infos[4])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Error con los argumentos")
                    return
                }

                try {
                    max = Integer.parseInt(infos[5])
                } catch (ignored: Exception) {
                }

                try {
                    condicion = infos[6]
                } catch (ignored: Exception) {
                }

                val mobModelo = Mundo.getMobModelo(mobID)
                val objModelo = Mundo.getObjetoModelo(objModID)
                if (mobModelo == null || objModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto o mob nulos")
                    return
                }
                mobModelo.addDrop(DropMob(objModID, prospecc, porcentaje, max, condicion))
                GestorSQL.INSERT_DROP(
                    mobID, objModID, prospecc, porcentaje, max, mobModelo.nombre, objModelo
                        .nombre, condicion
                )
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se agrego al mob " + mobModelo.nombre + " (" + mobModelo
                        .id + ") el objeto " + objModelo.nombre + " (" + objModelo.id + ") con PP " + prospecc + ", "
                            + porcentaje + "%, maximo " + max + " y condicion " + condicion
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "LIST_DROPS", "LISTA_DROPS", "LISTA_DROP" -> try {
                val strB = StringBuilder()
                var mobID = 0
                try {
                    mobID = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Error con los argumentos")
                    return
                }

                val mobModelo = Mundo.getMobModelo(mobID)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hay valores nulos")
                    return
                }
                for (drop in mobModelo.drops) {
                    val objModelo = Mundo.getObjetoModelo(drop.IDObjModelo) ?: continue
                    strB.append(" - ").append(drop.IDObjModelo).append(" - ").append(objModelo.nombre)
                        .append("\tProsp: ").append(
                            drop
                                .prospeccion
                        ).append("\tPorcentaje: ").append(drop.porcentaje).append("%\tMax: ").append(drop.maximo)
                        .append("\n")
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "La listas de drop del mob " + mobModelo.nombre + " es: \n"
                            + strB.toString()
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "DEL_DROP", "ELIMINATE_DROP", "ERASER_DROP", "BORRAR_DROP", "DELETE_DROP" -> try {
                var mobID = 0
                var objModID = 0
                try {
                    mobID = Integer.parseInt(infos[1])
                    objModID = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Error con los argumentos")
                    return
                }

                val objModelo = Mundo.getObjetoModelo(objModID)
                val mobModelo = Mundo.getMobModelo(mobID)
                if (objModelo == null || mobModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hay valores nulos")
                    return
                }
                mobModelo.borrarDrop(objModID)
                GestorSQL.DELETE_DROP(objModID, mobID)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se borro el objeto " + objModelo.nombre + " del drop del mob "
                            + mobModelo.nombre
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "ADD_END_FIGHT", "ADD_END_FIGHT_ACTION", "ADD_ACTION_END_FIGHT", "ADD_ACCION_FIN_PELEA", "AGREGAR_ACCION_FIN_PELEA" -> try {
                infos = mensaje.split(" ".toRegex(), 6).toTypedArray()
                var tipo = 0
                var accionID = 0
                var args = ""
                var condicion = ""
                var descripcion = ""
                val strB = StringBuilder()
                try {
                    tipo = Integer.parseInt(infos[1])
                    accionID = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento invalido")
                    return
                }

                if (infos.size > 3) {
                    args = infos[3]
                }
                if (infos.size > 4) {
                    condicion = infos[4]
                }
                try {
                    if (infos.size > 5) {
                        descripcion = infos[5]
                    }
                } catch (ignored: Exception) {
                }

                try {
                    if (infos.size > 6) {
                        mapa = Mundo.getMapa(java.lang.Short.parseShort(infos[6]))
                    }
                } catch (ignored: Exception) {
                }

                mapa!!.addAccionFinPelea(tipo, Accion(accionID, args, ""))
                strB.append("Se agrego la accion fin pelea, mapaID: ").append(mapa.id.toInt()).append(", tipoPelea: ")
                    .append(tipo).append(", accionID: ").append(accionID).append(", args: ").append(args)
                    .append(" condicion: ").append(condicion)
                if (GestorSQL.INSERT_ACCION_FIN_PELEA(mapa.id.toInt(), tipo, accionID, args, condicion, descripcion)) {
                    strB.append(" a la BDD")
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "DELETED_ACTION_END_FIGHT", "ELIMINAR_ACCION_FIN_PELEA", "DEL_ACTION_END_FIGHT", "BORRAR_ACCION_FIN_PELEA", "BORRAR_ACCIONES_FIN_PELEA" -> {
                mapa!!.borrarAccionesPelea()
                GestorSQL.DELETE_ACCION_PELEA(mapa.id.toInt())
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se borraron las acciones de pelea")
            }
            "ESPECTATOR_FIGHT", "ESPECTAR_PELEA", "ESPECTATE_FIGHT", "ESPECTAR_A", "JOIN_FIGHT", "UNIRSE_PELEA" -> try {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (objetivo.pelea == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta en pelea")
                    return
                }
                if (objetivo.pelea.fase < Constantes.PELEA_FASE_COMBATE) {
                    objetivo.pelea.unirsePelea(_perso, objetivo.Id)
                } else {
                    objetivo.pelea.unirseEspectador(_perso, true)
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Te uniste a la pelea de " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "SHOW_FIGHTS", "MOSTRAR_PELEAS" -> try {
                val packet = StringBuilder()
                var primero = true
                for (pelea in mapa!!.peleas!!.values) {
                    if (!primero) {
                        packet.append("|")
                    }
                    try {
                        val info = pelea.strParaListaPelea()
                        if (info.isNotEmpty()) {
                            packet.append(info)
                            primero = false
                        }
                    } catch (ignored: Exception) {
                    }

                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Lista peleas de:\n$packet")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "ADD_GRUPOMOB_FIJO", "AGREGAR_GRUPOMOB_FIJO", "ADD_MOB_FIJO", "AGREGAR_MOB_FIJO", "SPAWN_FIX", "SPAWN_SQL", "AGREGAR_MOB_GRUPO_SQL", "AGREGAR_GRUPO_MOB_SQL", "ADD_GRUPO_MOB_SQL" -> {
                sql = true
                try {
                    var condUnirse = ""
                    var condInicio = ""
                    var grupoData = ""
                    var segundosRespawn = 0
                    var tipoGrupoMob = 0
                    try {
                        grupoData = infos[1]
                        tipoGrupoMob = Integer.parseInt(infos[2])
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                        return
                    }

                    if (grupoData.isEmpty()) {
                        return
                    }
                    try {
                        condInicio = infos[3].replace("menor".toRegex(), "<")
                        condInicio = condInicio.replace("mayor".toRegex(), ">")
                        condInicio = condInicio.replace("igual".toRegex(), "=")
                        condInicio = condInicio.replace("diferente".toRegex(), "!")
                    } catch (ignored: Exception) {
                    }

                    try {
                        condUnirse = infos[4].replace("menor".toRegex(), "<")
                        condUnirse = condUnirse.replace("mayor".toRegex(), ">")
                        condUnirse = condUnirse.replace("igual".toRegex(), "=")
                        condUnirse = condUnirse.replace("diferente".toRegex(), "!")
                    } catch (ignored: Exception) {
                    }

                    try {
                        segundosRespawn = Integer.parseInt(infos[5])
                    } catch (ignored: Exception) {
                    }

                    var tipoGrupo = Constantes.getTipoGrupoMob(tipoGrupoMob)
                    if (tipoGrupo == TipoGrupo.NORMAL) {
                        tipoGrupo = TipoGrupo.FIJO
                    }
                    val grupoMob = mapa!!.addGrupoMobPorTipo(_perso.celda.id, grupoData, tipoGrupo, condInicio, null)
                    grupoMob!!.condUnirsePelea = condUnirse
                    grupoMob.segundosRespawn = segundosRespawn
                    if (sql) {
                        GestorSQL.REPLACE_GRUPOMOB_FIJO(
                            mapa.id.toInt(), _perso.celda.id.toInt(), grupoData, tipoGrupoMob,
                            condInicio, segundosRespawn
                        )
                    }
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le groupe monstre a ete spawn.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "Se agrego el grupomob: " + grupoData + " de tipo: " + tipoGrupo
                                    + ", condInicio: " + condInicio + ", condUnirse: " + condUnirse + ", tiempoReaparecer: " + segundosRespawn
                                    + if (sql) " y guardado en la BD" else ""
                        )
                    }
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
                }

            }
            "AGREGAR_MOB_GRUPO", "AGREGAR_GRUPO_MOB", "ADD_GRUPO_MOB", "ADD_GROUP_MOB", "SPAWN_MOBS", "SPAWN_GROUP_MOB", "SPAWN_MOB", "SPAWN" -> try {
                var condUnirse = ""
                var condInicio = ""
                var grupoData = ""
                var segundosRespawn = 0
                var tipoGrupoMob = 0
                try {
                    grupoData = infos[1]
                    tipoGrupoMob = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
                    return
                }

                if (grupoData.isEmpty()) {
                    return
                }
                try {
                    condInicio = infos[3].replace("menor".toRegex(), "<")
                    condInicio = condInicio.replace("mayor".toRegex(), ">")
                    condInicio = condInicio.replace("igual".toRegex(), "=")
                    condInicio = condInicio.replace("diferente".toRegex(), "!")
                } catch (ignored: Exception) {
                }

                try {
                    condUnirse = infos[4].replace("menor".toRegex(), "<")
                    condUnirse = condUnirse.replace("mayor".toRegex(), ">")
                    condUnirse = condUnirse.replace("igual".toRegex(), "=")
                    condUnirse = condUnirse.replace("diferente".toRegex(), "!")
                } catch (ignored: Exception) {
                }

                try {
                    segundosRespawn = Integer.parseInt(infos[5])
                } catch (ignored: Exception) {
                }

                var tipoGrupo = Constantes.getTipoGrupoMob(tipoGrupoMob)
                if (tipoGrupo == TipoGrupo.NORMAL) {
                    tipoGrupo = TipoGrupo.FIJO
                }
                val grupoMob = mapa!!.addGrupoMobPorTipo(_perso.celda.id, grupoData, tipoGrupo, condInicio, null)
                grupoMob!!.condUnirsePelea = condUnirse
                grupoMob.segundosRespawn = segundosRespawn
                if (sql) {
                    GestorSQL.REPLACE_GRUPOMOB_FIJO(
                        mapa.id.toInt(),
                        _perso.celda.id.toInt(),
                        grupoData,
                        tipoGrupoMob,
                        condInicio,
                        segundosRespawn
                    )
                }
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le groupe monstre a ete spawn.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "Se agrego el grupomob: " + grupoData + " de tipo: " + tipoGrupo + ", condInicio: " + condInicio + ", condUnirse: " + condUnirse + ", tiempoReaparecer: " + segundosRespawn + if (sql) " y guardado en la BD" else ""
                    )
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "REMOVE_MOBS", "DELETE_MOBS", "DEL_MOBS", "ELIMINAR_MOBS", "BORRAR_MOBS" -> {
                mapa!!.borrarTodosMobsNoFijos()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se borraron todos los mobs normales de este mapa")
            }
            "ELIMINAR_MOBS_FIJOS", "BORRAR_MOBS_FIX", "BORRAR_MOBS_FIJOS", "REMOVE_MOBS_FIX", "DEL_MOBS_FIX", "DELETE_MOBS_FIX" -> {
                mapa!!.borrarTodosMobsFijos()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se borraron todos los mobs fix de este mapa")
            }
            "AGREGAR_NPC", "ADD_NPC" -> try {
                var id = 0
                try {
                    id = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                if (Mundo.getNPCModelo(id) == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'ID du PNJ est invalide.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "NPC ID invalido")
                    }
                    return
                }
                val npc = mapa!!.addNPC(Mundo.getNPCModelo(id), _perso.celda.id, _perso.orientacion)
                GestorSalida.ENVIAR_GM_NPC_A_MAPA(mapa, '+', npc.strinGM(null))
                if (GestorSQL.REPLACE_NPC_AL_MAPA(
                        mapa.id, _perso.celda.id, id, _perso.orientacion, npc
                            .modelo!!.nombre
                    )
                ) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le PNJ a ete ajoute.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El NPC " + npc.modelo.nombre + " ha sido agregado")
                    }
                } else {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Une erreur est survenue.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Error al agregar el NPC")
                    }
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "MOVER_NPC", "MOVE_NPC" -> try {
                var id = 0
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }

                val npc = mapa!!.getNPC(id)
                if (npc == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'ID du PNJ est invalide.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "NPC ID invalido")
                    }
                    return
                }
                npc.orientacion = _perso.orientacion
                if (GestorSQL.REPLACE_NPC_AL_MAPA(
                        mapa.id, _perso.celda.id, npc.modelo!!.id, _perso
                            .orientacion, npc.modelo.nombre
                    )
                ) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El NPC " + npc.modelo.nombre + " ha sido desplazado")
                    npc.celdaID = _perso.celda.id
                    GestorSalida.ENVIAR_GM_NPC_A_MAPA(mapa, '~', npc.strinGM(null))
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Error al mover el NPC")
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "BORRAR_NPC", "DEL_NPC" -> try {
                var id = 0
                try {
                    id = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                val npc = mapa!!.getNPC(id)
                if (npc == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'ID du PNJ est invalide.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "NPC ID invalido")
                    }
                    return
                }
                GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA(mapa, id)
                mapa.borrarNPC(id)
                if (GestorSQL.DELETE_NPC_DEL_MAPA(mapa.id.toInt(), npc.modeloID)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El NPC fue eliminado correctamente")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No se pudo eliminar el NPC de la BD")
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "BORRAR_TRIGER", "BORRAR_CELDA_TELEPORT", "DEL_TRIGGER", "BORRAR_TRIGGER", "BORRAR_CELDA_ACCION", "DEL_CELDA_ACCION" -> try {
                var celdaID: Short = -1
                try {
                    celdaID = java.lang.Short.parseShort(infos[1])
                } catch (ignored: Exception) {
                }

                var celda: Celda? = mapa!!.getCelda(celdaID)
                if (celda == null) {
                    celda = _perso.celda
                }
                GestorSalida.enviarEnCola(_perso, "GDZ|-$celdaID;0;11", false)
                celda!!.eliminarAcciones()
                val exito = GestorSQL.DELETE_TRIGGER(mapa.id.toInt(), celdaID.toInt())
                if (exito) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El trigger de la celda $celdaID ha sido borrado")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El trigger no se puede borrar")
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "ADD_TRIGGER", "AGREGAR_TRIGER", "ADD_CELDA_ACCION", "AGREGAR_CELDA_ACCION", "AGREGAR_CELDA_TELEPORT", "AGREGAR_TRIGGER" -> try {
                var accionID = 0
                var args = ""
                var condicion = ""
                try {
                    accionID = Integer.parseInt(infos[1])
                    args = infos[2]
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                if (accionID <= -3) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "AccionID incorrecta")
                    return
                }
                var celda = _perso.celda
                try {
                    if (infos.size > 3) {
                        mapa =
                            Mundo.getMapa(
                                java.lang.Short.parseShort(
                                    infos[3].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                                )
                            )
                        celda =
                            mapa!!.getCelda(
                                java.lang.Short.parseShort(
                                    infos[3].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                                )
                            )
                    }
                } catch (e: Exception) {
                    mapa = _perso.mapa
                    celda = _perso.celda
                }

                try {
                    if (infos.size > 4) {
                        condicion = infos[4]
                    }
                } catch (ignored: Exception) {
                }

                if (GestorSQL.REPLACE_CELDAS_ACCION(mapa!!.id.toInt(), celda.id.toInt(), accionID, args, condicion)) {
                    if (mapa.id == _perso.mapa.id) {
                        GestorSalida.enviarEnCola(_perso, "GDZ|+" + celda.id + ";0;11", false)// color
                        // azul
                    }
                    celda.addAccion(accionID, args, condicion)
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "El mapa: " + mapa.id + ", celda: " + celda.id
                                + ", le ha sido agregado la accion: " + accionID + ", args: " + args + ", y condicion (4to arg): "
                                + condicion
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El trigger no se puede agregar")
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "LIST_TRIGGERS", "LISTA_CELDAS_ACCION", "LISTA_TRIGGERS" -> try {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Triggers del mapa " + mapa!!.id)
                for (celda in mapa.celdas.values) {
                    if (celda.accionesIsEmpty()) {
                        continue
                    }
                    for (a in celda.acciones!!.values) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "\tCeldaID: " + celda.id + " AccionID: " + a.id
                                    + " Args: " + a.args + " Condicion: " + a.condicion
                        )
                    }
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "MOSTRAR_TRIGGERS", "SHOW_TRIGGERS", "SHOW_CELLS_ACTION" -> mapa!!.panelTriggers(_perso, true)
            "OCULTAR_TRIGGERS", "HIDE_TRIGGERS", "ESCONDER_TRIGGERS" -> mapa!!.panelTriggers(_perso, false)
            "ADD_ACCION_OBJETO", "ADD_ACTION_ITEM", "AGREGAR_ACCION_OBJETO", "ADD_ITEM_ACTION", "ADD_OBJETO_ACCION", "AGREGAR_OBJETO_ACCION" -> try {
                var id = 0
                var accionID = 0
                var args = ""
                try {
                    id = Integer.parseInt(infos[1])
                    accionID = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Error con los argumentos")
                    return
                }

                try {
                    args = infos[3]
                } catch (ignored: Exception) {
                }

                val objModelo = Mundo.getObjetoModelo(id)
                if (objModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Algun valor invalido")
                    return
                }
                objModelo.addAccion(Accion(accionID, args, ""))
                GestorSQL.REPLACE_ACCION_OBJETO(id, accionID, args, objModelo.nombre)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El objeto " + objModelo.nombre
                            + " se le ha agregado la accionID " + accionID + " con args " + args
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "DEL_ACTION_ITEM", "BORRAR_ACCION_OBJETO", "BORRAR_ACCIONES_OBJETO", "BORRAR_OBJETO_ACCIONES", "BORRAR_OBJETO_ACCION", "DELETE_ITEM_ACTIONS", "DELETE_ACTION_ITEM" -> try {
                var id = 0
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Error con los argumentos")
                    return
                }

                val objModelo = Mundo.getObjetoModelo(id)
                if (objModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto modelo invalido")
                    return
                }
                objModelo.borrarAcciones()
                GestorSQL.DELETE_ACCION_OBJETO(id)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El objeto " + objModelo.nombre + " borro todas sus acciones")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "MESSAGE_WELCOME", "MENSAJE_BIENVENIDA" -> try {
                var str = ""
                try {
                    str = mensaje.split(" ".toRegex(), 2).toTypedArray()[1]
                } catch (ignored: Exception) {
                }

                AtlantaMain.MENSAJE_BIENVENIDA = str
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mensaje de bienvenida es :\n$str")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "PANEL_BIENVENIDA" -> try {
                var str = ""
                try {
                    str = mensaje.split(" ".toRegex(), 2).toTypedArray()[1]
                } catch (ignored: Exception) {
                }

                AtlantaMain.PANEL_BIENVENIDA = str
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El panel bievenida dice :\n$str")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "PANEL_DESPUES_CREAR_PJ", "PANEL_CREAR_PJ" -> try {
                var str = ""
                try {
                    str = mensaje.split(" ".toRegex(), 2).toTypedArray()[1]
                } catch (ignored: Exception) {
                }

                AtlantaMain.PANEL_DESPUES_CREAR_PERSONAJE = str
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El panel crear pj dice :\n$str")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "MENSAJE_COMANDOS" -> try {
                var str = ""
                try {
                    str = mensaje.split(" ".toRegex(), 2).toTypedArray()[1]
                } catch (ignored: Exception) {
                }

                AtlantaMain.MENSAJE_COMANDOS = str
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El nuevo mensaje de comandos es :\n$str")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "TUTORIAL_ES" -> try {
                var str = ""
                try {
                    str = mensaje.split(" ".toRegex(), 2).toTypedArray()[1]
                } catch (ignored: Exception) {
                }

                AtlantaMain.TUTORIAL_ES = str
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mensaje de tutorial_es es :\n$str")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "TUTORIAL_FR" -> try {
                var str = ""
                try {
                    str = mensaje.split(" ".toRegex(), 2).toTypedArray()[1]
                } catch (ignored: Exception) {
                }

                AtlantaMain.TUTORIAL_FR = str
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mensaje de tutorial_fr es :\n$str")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "ADD_CELDA_CERCADO", "ADD_CELL_MOUNTPARK", "CELDA_OBJETO" -> try {
                var celdaID: Short = 0
                if (mapa!!.cercado == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Este mapa no tiene cercado")
                    return
                }
                celdaID = try {
                    java.lang.Short.parseShort(infos[1])
                } catch (e: Exception) {
                    _perso.celda.id
                }

                mapa.cercado!!.addCeldaObj(celdaID)
                GestorSalida.enviarEnCola(_perso, "GDZ|+$celdaID;0;5", false)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "CELDAS_CERCADO" -> try {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Tiene las celdas: " + mapa!!.cercado!!.stringCeldasObj)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Este mapa no tiene cercado")
            }

            "MUTE_CANAL_ALINEACION" -> try {
                var a = false
                try {
                    a = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.MUTE_CANAL_ALINEACION = a
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Canal alineacion: " + !AtlantaMain.MUTE_CANAL_COMERCIO)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "MUTE_CANAL_INCARNAM", "MUTE_CANAL_ALL" -> try {
                var a = false
                try {
                    a = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.MUTE_CANAL_INCARNAM = a
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Canal incarnam: " + !AtlantaMain.MUTE_CANAL_COMERCIO)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "MUTE_CANAL_COMERCIO", "MUTE_CANAL_COMMERCE" -> try {
                var a = false
                try {
                    a = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.MUTE_CANAL_COMERCIO = a
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Canal commerce: " + !AtlantaMain.MUTE_CANAL_COMERCIO)
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Canal comercio: " + !AtlantaMain.MUTE_CANAL_COMERCIO)
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "MUTE_CANAL_RECRUTEMENT", "MUTE_CANAL_RECLUTAMIENTO" -> try {
                var b = false
                try {
                    b = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.MUTE_CANAL_RECLUTAMIENTO = b
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "Canal recrutement: " + !AtlantaMain.MUTE_CANAL_RECLUTAMIENTO
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "Canal reclutamiento: " + !AtlantaMain.MUTE_CANAL_RECLUTAMIENTO
                    )
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            else -> {
                GM_lvl_2(comando, infos, mensaje, _cuenta, _perso)
                return
            }
        }
        // if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
        // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Commande GM 3!.");
        // } else {
        // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Comando de nivel 3");
        // }
    }

    private fun GM_lvl_4(
        comando: String, infos: Array<String>, mensaje: String, _cuenta: Cuenta,
        _perso: Personaje
    ) {
        var infos = infos
        var npcID = 0
        var id = -1
        var cantInt = 0
        var tipo = 0
        var cantByte: Byte = 0
        var cantShort: Short = 0
        var cantLong: Long = 0
        var cantFloat = 0f
        var rateoriginal = 0.0
        var objModelo: ObjetoModelo?
        val mobModelo: MobModelo?
        val npcModelo: NPCModelo
        var str = ""
        var intercambiable = ""
        val strB = StringBuilder()
        var objetivo: Personaje? = null
        val hechizo: Hechizo?// *0287014
        when (comando.toUpperCase()) {
            "SET_SPELLS_MOB", "SET_HECHIZOS_MOB" -> {
                try {
                    id = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                mobModelo = Mundo.getMobModelo(id)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mob ID $id no existe")
                    return
                }
                try {
                    cantByte = java.lang.Byte.parseByte(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ingresa un valor correcto para GRADO del mob")
                    return
                }

                val mobGradoModelo = mobModelo.getGradoPorGrado(cantByte)
                if (mobGradoModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mob ID $id con Grado $cantByte no existe")
                    return
                }
                try {
                    str = infos[3]
                } catch (ignored: Exception) {
                }

                mobGradoModelo.setHechizos(str)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El mob " + mobModelo.nombre + " (" + id + ") con Grado "
                            + cantByte + " a modificado sus hechizos a " + str
                )
            }
            "ADD_ALMANAX", "AGREGAR_MISION_DIARIA", "MISION_ALMANAX", "UPDATE_ALMANAX" -> {
                try {
                    id = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                if (id == -1) {
                    id = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                }
                if (id < 1 || id > 366) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Dia Incorrecto")
                    return
                }
                try {
                    str = infos[2]
                    tipo = Integer.parseInt(infos[3])
                    cantInt = Integer.parseInt(infos[4])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
                    return
                }

                GestorSQL.UPDATE_ALMANAX(id, str, tipo, cantInt)
                Mundo.addAlmanax(Almanax(id, tipo, cantInt, str))
                var bonus = "EXP PJ"
                when (tipo) {
                    1 -> bonus = "EXP PJ"
                    2 -> bonus = "KAMAS"
                    3 -> bonus = "DROP"
                    4 -> bonus = "EXP CRAFT"
                    5 -> bonus = "EXP RECOLECCION"
                    6 -> bonus = "DROP RECOLECCION"
                    7 -> bonus = "BONUS HONOR"
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se actualizo el dia almanax " + id + ", con ofrenda " + str
                            + ", tipoBonus " + bonus + " y bonus %" + cantInt
                )
            }
            "ADD_MOB_CARD", "ADD_CARD_MOB" -> {
                try {
                    id = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecte.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                if (Mundo.getMobModelo(id) == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le monstre n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mob no existe")
                    }
                    return
                }
                objetivo.addCardMob(id)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Le joueur " + objetivo.nombre
                                + " agrego asu lista de cardMobs la tarjeta N°" + id
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "El personaje " + objetivo.nombre
                                + " agrego asu lista de cardMobs la tarjeta N°" + id + " (" + Mundo.getMobModelo(id)!!.nombre + ")"
                    )
                }
            }
            "PRECIO_SISTEMA_RECURSO", "PRECIO_RECURSO" -> {
                try {
                    cantFloat = java.lang.Float.parseFloat(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
                    return
                }

                AtlantaMain.PRECIO_SISTEMA_RECURSO = cantFloat
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El precio de recurso se cambio a $cantFloat")
            }
            "CARGAR_MAPAS_IDS", "LOAD_MAPS_IDS", "CARGAR_MAPAS", "MAPPEAR_IDS" -> try {
                GestorSQL.CARGAR_MAPAS_IDS(infos[1])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se integro los mapas " + infos[1])
            } catch (e1: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                return
            }

            "CARGAR_SUBAREAS", "LOAD_SUBAREAS", "MAPPEAR_SUBAREAS" -> try {
                GestorSQL.CARGAR_MAPAS_SUBAREAS(infos[1])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se integro las subareas " + infos[1])
            } catch (e1: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                return
            }

            "ADD_ENERGIA", "AGREGAR_ENERGIA", "ENERGIA", "ENERGY", "SET_ENERGY" -> {
                if (infos.size <= 1) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                cantInt = Integer.parseInt(infos[1])
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                    if (objetivo == null) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                }
                objetivo.addEnergiaConIm(cantInt, true)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "L'energie de " + objetivo.nombre + " a ete modifiee en "
                                + objetivo.energia
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Ha sido modificado la energia de " + objetivo.nombre + " a "
                                + objetivo.energia
                    )
                }
            }
            "ADD_TITULO", "SET_TITLE", "ADD_TITLE", "TITULO", "TITRE", "TITLE" -> {
                var titulo = 0
                var color = -1
                try {
                    titulo = java.lang.Byte.parseByte(infos[1]).toInt()
                    color = Integer.parseInt(infos[2])
                } catch (ignored: Exception) {
                }

                objetivo = _perso
                if (infos.size > 3) {
                    objetivo = Mundo.getPersonajePorNombre(infos[3])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.addTitulo(titulo, color)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Le joueur " + objetivo.nombre + " possède desormais le titre "
                                + titulo
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "El personaje " + objetivo.nombre + " adquirio el titulo "
                                + titulo
                    )
                }
                if (objetivo.pelea == null) {
                    objetivo.refrescarEnMapa()
                }
            }
            "ORNAMENTO", "ORNEMENT" -> {
                try {
                    cantByte = java.lang.Byte.parseByte(infos[1])
                } catch (ignored: Exception) {
                }

                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.addOrnamento(cantByte.toInt())
                objetivo.ornamento = cantByte.toInt()
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Le joueur " + objetivo.nombre
                                + " possède desormais l'ornement " + cantByte
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "El personaje " + objetivo.nombre + " adquirio el ornamento "
                                + cantByte
                    )
                }
                if (objetivo.pelea == null) {
                    objetivo.refrescarEnMapa()
                }
            }
            "TITULO_VIP", "TITRE_VIP" -> try {
                infos = mensaje.split(" ".toRegex(), 2).toTypedArray()
                objetivo = _perso
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                str = infos[2]
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.tituloVIP = str
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Le joueur " + objetivo.nombre
                                + " possède desormais le titre VIP " + str
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "El personaje " + objetivo.nombre
                                + " a adquirido el titulo VIP de " + str
                    )
                }
                if (objetivo.pelea == null) {
                    objetivo.refrescarEnMapa()
                }
            } catch (ignored: Exception) {
            }

            "SET_STATS_OBJETO_SET", "SET_STATS_SET_OBJETO", "SET_STATS_SET_ITEM", "SET_STATS_ITEM_SET", "SET_BONUS_OBJETO_SET", "SET_BONUS_SET_OBJETO", "SET_BONUS_SET_ITEM", "SET_BONUS_ITEM_SET" -> {
                try {
                    id = Integer.parseInt(infos[1])
                    str = infos[2]
                    cantInt = Integer.parseInt(infos[3])
                } catch (ignored: Exception) {
                }

                val set = Mundo.getObjetoSet(id)
                if (set == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El objeto set $id no existe")
                    return
                }
                set.setStats(str, cantInt)
                GestorSQL.UPDATE_STATS_OBJETO_SET(id, str)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El objeto set " + id + " (" + set.nombre
                            + ") cambio su bonus de " + cantInt + " objetos a: " + str
                )
            }
            "ITEM_SET", "SET_ITEM", "CREAR_SET" -> try {
                try {
                    id = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                    val nombreset = mensaje.split(" ".toRegex(), 2).toTypedArray()[1]
                    val lista = java.lang.StringBuilder()
                    val encontrados = Buscador.buscarSets(nombreset)
                    for (set in encontrados) {
                        lista.append("ID: ").append(set.iD).append(" Nombre: ").append(set.nombre)
                            .append("\n")
                    }
                    if (encontrados.size == 1) {
                        val z = encontrados[0]
                        consolaComando("ITEM_SET ${z.iD}", _cuenta, _perso)
                        return
                    }
                    if (lista.isNotEmpty()) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, lista.toString())
                        return
                    }
                    if (lista.isEmpty()) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "El objeto set $id no existe\nA continuacion una lista de los sets disponibles"
                        )
                        for (a in Mundo.OBJETOS_SETS.values) {
                            if (a.objetosModelos.size > 1) {
                                lista.append("ID: ").append(a.iD).append(" Nombre: ").append(a.nombre).append("\n")
                            }
                        }
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, lista.toString())
                        return
                    }
                    return
                }
                val OS = Mundo.getObjetoSet(id)
                if (OS == null) {
                    val lista = java.lang.StringBuilder()
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "El objeto set $id no existe\nA continuacion una lista de los sets disponibles"
                    )
                    for (a in Mundo.OBJETOS_SETS.values) {
                        if (a.objetosModelos.size > 1) {
                            lista.append("ID: ").append(a.iD).append(" Nombre: ").append(a.nombre).append("\n")
                        }
                    }
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, lista.toString())
                    return
                }
                rateoriginal = AtlantaMain.RATE_RANDOM_ITEM_BASE
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (AtlantaMain.PARAM_NOMBRE_ADMIN && _perso.cuenta.admin < 5) {
                    if (_cuenta.admin < 5) {
                        objetivo = _perso
                    }
                    intercambiable = ObjetoModelo.stringFechaIntercambiable(3650)
                }
                var useMax = CAPACIDAD_STATS.RANDOM
                if (infos.size > 3) {
                    useMax = if (infos[3].equals("MAX", ignoreCase = true))
                        CAPACIDAD_STATS.MAXIMO
                    else
                        if (infos[3].equals(
                                "MIN",
                                ignoreCase = true
                            )
                        ) CAPACIDAD_STATS.MINIMO else CAPACIDAD_STATS.RANDOM
                    AtlantaMain.RATE_RANDOM_ITEM_BASE = AtlantaMain.RATE_RANDOM_ITEM
                }
                if (OS != null) {
                    for (OM in OS.objetosModelos) {
                        val obj = OM.crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO, useMax)
                        if (AtlantaMain.PARAM_NOMBRE_ADMIN && _perso.cuenta.admin < 5) {
                            obj.addStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE, intercambiable)
                            obj.addStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER, "0#0#0#" + _perso.nombre)
                        } else if (AtlantaMain.PARAM_MOSTRAR_NOMBRE_ADMIN_5) {
                            obj.addStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER, "0#0#0#" + _perso.nombre)
                        }
                        objetivo!!.addObjIdentAInventario(obj, false)
                    }
                }
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    if (OS != null) {
                        strB.append("Creation de la panoplie ").append(OS.nombre).append(" pour ")
                            .append(objetivo!!.nombre)
                    }
                } else {
                    if (OS != null) {
                        strB.append("Creacion del objeto set ").append(OS.nombre).append(" a ")
                            .append(objetivo!!.nombre)
                    }
                }
                AtlantaMain.RATE_RANDOM_ITEM_BASE = rateoriginal
                when (useMax) {
                    CAPACIDAD_STATS.MAXIMO -> if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        strB.append(" avec des jets parfaits.")
                    } else {
                        strB.append(" con stats maximos")
                    }
                    CAPACIDAD_STATS.MINIMO -> if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        strB.append(" avec des jets minimuns.")
                    } else {
                        strB.append(" con stats minimos")
                    }
                    else -> {
                    }
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
                if (objetivo != null) {
                    GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(objetivo)
                }
            } catch (ignored: Exception) {
            }

            "DEL_NPC_ITEM", "DEL_ITEM_NPC", "BORRAR_NPC_ITEM", "BORRAR_ITEM_NPC", "BORRAR_OBJETO_NPC", "BORRAR_NPC_OBJETO" -> try {
                try {
                    npcID = Integer.parseInt(infos[1])
                    npcModelo = Mundo.getNPCModelo(npcID)!!
                    npcModelo.id
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "NPC invalido")
                    return
                }

                val objetos = ArrayList<ObjetoModelo>()
                for (a in infos[2].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    try {
                        objModelo = Mundo.getObjetoModelo(Integer.parseInt(a))
                        if (objModelo != null) {
                            objetos.add(objModelo)
                            strB.append("\n").append(objModelo.nombre)
                        }
                    } catch (ignored: Exception) {
                    }

                }
                npcModelo.borrarObjetoAVender(objetos)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Al NPC " + npcModelo.nombre
                            + " se le borro los siguientes objetos:" + strB.toString()
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }

            "ADD_ITEM_NPC", "ADD_OBJETO_NPC", "ADD_NPC_ITEM", "ADD_NPC_OBJETO", "AGREGAR_NPC_OBJETO", "AGREGAR_OBJETO_NPC", "AGREGAR_NPC_ITEM", "AGREGAR_ITEM_NPC" -> try {
                try {
                    npcID = Integer.parseInt(infos[1])
                    npcModelo = Mundo.getNPCModelo(npcID)!!
                    npcModelo.id
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "NPC invalido")
                    return
                }

                val objetos = ArrayList<ObjetoModelo>()
                for (a in infos[2].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    try {
                        objModelo = Mundo.getObjetoModelo(Integer.parseInt(a))
                        if (objModelo != null) {
                            objetos.add(objModelo)
                            strB.append("\n").append(objModelo.nombre)
                        }
                    } catch (ignored: Exception) {
                    }

                }
                npcModelo.addObjetoAVender(objetos)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Al NPC " + npcModelo.nombre
                            + " se le agrego los siguientes objetos:" + strB.toString()
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }

            "BORRAR_OBJETOS_NPC", "BORRAR_NPC_TODOS_OBJETOS" -> try {
                npcID = Integer.parseInt(infos[1])
                npcModelo = Mundo.getNPCModelo(npcID)!!
                npcModelo.borrarTodosObjVender()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se borraron todos los objetos del NPC " + npcModelo.nombre)
            } catch (ex: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalido")
                return
            }

            "HONOUR", "HONOR" -> {
                var honor = 0
                try {
                    honor = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                    if (objetivo == null) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                }
                if (objetivo.alineacion == Constantes.ALINEACION_NEUTRAL || objetivo
                        .alineacion == Constantes.ALINEACION_NULL
                ) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur est neutre.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje es neutral")
                    }
                    return
                }
                objetivo.addHonor(honor)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "$honor points d'honneur ont ete ajoutes à " + objetivo
                            .nombre
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "Ha sido agregado " + honor + " honor a " + objetivo.nombre
                    )
                }
            }
            "ENVIAR_TOKEN", "RECUPERAR_TOKEN", "TOKEN" -> {
                try {
                    var nombreObjetivo = infos[1]
                    if (infos.size < 2) {
                        nombreObjetivo = _perso.nombre
                    }
                    val perso = Mundo.getPersonajePorNombre(nombreObjetivo) ?: return
                    val cuenta = perso.cuenta ?: return
                    val token = GestorSQL.GET_TOKEN(cuenta)
                    perso.enviarmensajeNegro("Su token es: $token")
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "El token del jugador es: $token \nse le fue enviado de manera automatica"
                    )
                    return
                } catch (e: Exception) {
                }
            }
            "MAPA_PARAMETROS", "PARAMETERS_MAPA", "MAPA_DESCRIPCION", "DESCRIPCION_MAPA" -> {
                try {
                    cantShort = java.lang.Byte.parseByte(infos[1]).toShort()
                } catch (ignored: Exception) {
                }

                _perso.mapa.setParametros(cantShort)
                GestorSQL.UPDATE_MAPA_PARAMETROS(_perso.mapa.id.toInt(), cantShort.toInt())
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Los parametros del mapa cambio a $cantShort")
            }
            "REGALO", "GIFT" -> {
                try {
                    str = infos[1]
                } catch (ignored: Exception) {
                }

                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                    if (objetivo == null) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas.")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                }
                objetivo.cuenta.addRegalo(str)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le cadeau ete envoye.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se entrego el regalo " + str + " a " + objetivo.nombre)
                }
            }
            "REGALO_PARA_ONLINE", "REGALO_ONLINE", "GIFT_ONLINE" -> {
                try {
                    str = infos[1]
                } catch (ignored: Exception) {
                }

                for (pj in Mundo.PERSONAJESONLINE) {
                    try {
                        pj.cuenta.addRegalo(str)
                    } catch (ignored: Exception) {
                    }

                }
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le cadeau a ete envoye à tous les joueurs en ligne.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se entrego el regalo $str a todos los jugadores en linea")
                }
            }
            "OGRINAS_REGALADAS" -> try {
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "Actualmente se han regalado: " + GestorSQL.GET_OGRINAS_REGALADAS() + " Por el sistema referidos"
                )
            } catch (ignored: Exception) {

            }

            "REGALO_DB" -> {
                try {
                    str = infos[1]
                } catch (ignored: Exception) {
                }

                for (cuenta in Mundo.cuentas.values) {
                    cuenta.addRegalo(str)
                }
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le cadeau a ete envoye à tous les joueurs database.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Se entrego el regalo " + str
                                + " a todos los jugadores de la database"
                    )
                }
            }
            "OBJETO_PARA_PLAYERS_ONLINE", "OBJETO_PARA_JUGADORES_ONLINE", "ITEM_PLAYERS_ONLINE", "ITEM_FOR_ONLINE", "OBJETO_PARA_ONLINE", "GIVE_ITEM_TO_ONLINE" -> {
                if (_cuenta.admin < 5) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No puedes usar este comando")
                    return
                }
                try {
                    id = Integer.parseInt(infos[1])
                    cantInt = Integer.parseInt(infos[2])
                } catch (ignored: Exception) {
                }

                objModelo = Mundo.getObjetoModelo(id)
                if (objModelo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'objet n'existe pas.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto modelo nulo")
                    }
                    return
                }
                if (cantInt < 1) {
                    cantInt = 1
                }
                intercambiable = if (AtlantaMain.PARAM_NOMBRE_ADMIN && _perso.cuenta.admin < 5) {
                    ObjetoModelo.stringFechaIntercambiable(3650)
                } else {
                    ObjetoModelo.stringFechaIntercambiable(AtlantaMain.TIEMPO_INTERCAMBIABLE_REGALO)
                }
                for (pj in Mundo.PERSONAJESONLINE) {
                    val obj = objModelo.crearObjeto(cantInt, Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.RANDOM)
                    if (AtlantaMain.PARAM_NOMBRE_ADMIN && _perso.cuenta.admin < 5) {
                        obj.addStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE, intercambiable)
                        obj.addStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER, "0#0#0#" + _perso.nombre)
                    } else if (AtlantaMain.PARAM_MOSTRAR_NOMBRE_ADMIN_5) {
                        obj.addStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER, "0#0#0#" + _perso.nombre)
                    }
                    pj.addObjIdentAInventario(obj, false)
                }
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "L'objet " + objModelo.nombre + " avec quant " + cantInt
                                + " a ete envoye à tous les joueurs en ligne"
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Se entrego el objeto " + objModelo.nombre + " con cantidad "
                                + cantInt + " a todos los jugadores en linea"
                    )
                }
            }
            "XP_OFICIO", "ADD_XP_OFICIO", "EXP_OFICIO" -> {
                if (infos.size <= 2) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                var oficio = -1
                var exp = -1
                try {
                    oficio = Integer.parseInt(infos[1])
                    exp = Integer.parseInt(infos[2])
                } catch (ignored: Exception) {
                }

                if (oficio < 0 || exp < 0) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                objetivo = _perso
                if (infos.size > 3) {
                    objetivo = Mundo.getPersonajePorNombre(infos[3])
                    if (objetivo == null) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                }
                if (objetivo !== _perso && _cuenta.admin < 5) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "Hola bebe aqui " + Constantes.CREADOR + " para joderte el momentazo que estas teniendo, te recuerdo que: \n NO estas autorizado para darle cosas al resto.\n me saludas a la mami ?"
                    )
                    return
                }
                val statsOficio = objetivo.getStatOficioPorID(oficio)
                if (statsOficio == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'exerce pas ce metier.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no conoce el oficio")
                    }
                    return
                }
                statsOficio.addExperiencia(objetivo, exp, 0)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le metier du joueur a gagne de l'experience.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El oficio ha subido de experiencia")
                }
            }
            "PUNTOS_HECHIZO", "SPELL_POINTS" -> {
                var pts = -1
                try {
                    if (infos.size > 1) {
                        pts = Integer.parseInt(infos[1])
                    }
                } catch (ignored: Exception) {
                }

                if (pts < 0) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo !== _perso && _cuenta.admin < 5) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "¿A donde vas maquinola?, Tu no estas autorizado para darle puntos al resto"
                    )
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.addPuntosHechizos(pts)
                GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(objetivo)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Le joueur " + objetivo.nombre + " a reçu " + pts
                                + " points de sort"
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "El personaje " + objetivo.nombre + " se le ha aumentado " + pts
                                + " puntos de hechizo"
                    )
                }
            }
            "FORGET_SPELL", "OLVIDAR_HECHIZO" -> {
                try {
                    if (infos.size > 1)
                        id = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                if (id < 0) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le sort n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El hechizo no existe")
                    }
                    return
                }
                objetivo.olvidarHechizo(id, true, true)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Le joueur " + objetivo.nombre + " a oublier le sort " + hechizo
                            .nombre
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "El personaje " + objetivo.nombre + " ha olvidado el hechizo "
                                + hechizo.nombre
                    )
                }
            }
            "APRENDER_HECHIZO", "LEARN_SPELL" -> {
                try {
                    if (infos.size > 1)
                        id = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                if (id < 0) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo !== _perso && _cuenta.admin < 5) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "Hola bebe aqui " + Constantes.CREADOR + " para joderte el momentazo que estas teniendo, te recuerdo que: \n NO estas autorizado para darle cosas al resto.\n me saludas a la mami ?"
                    )
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le sort n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El hechizo no existe")
                    }
                    return
                }
                objetivo.fijarNivelHechizoOAprender(id, 1, true)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Le joueur " + objetivo.nombre + " a appris le sort " + id
                                + " (" + hechizo.nombre + ")"
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "El personaje " + objetivo.nombre + " aprendio el hechizo " + id
                                + " (" + hechizo.nombre + ")"
                    )
                }
            }
            "ADD_XP_MONTURA", "ADD_EXP_MONTURA", "AGREGAR_EXP_MONTURA" -> try {
                var montura: Montura? = _perso.montura
                try {
                    if (infos.size > 1) {
                        id = Integer.parseInt(infos[1])
                        if (id > 0) {
                            id = -id
                        }
                        if (id != 0)
                            montura = Mundo.getMontura(id)
                    }
                } catch (ignored: Exception) {
                }

                if (montura == null) {
                    return
                }
                if (montura.dueñoID != _perso.Id && _cuenta.admin < 5) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "Hola bebe aqui " + Constantes.CREADOR + " para joderte el momentazo que estas teniendo, te recuerdo que: \n NO estas autorizado para hacer esta accion.\n me saludas a la mami ?"
                    )
                    return
                }
                montura.addExperiencia(Integer.parseInt(infos[2]).toLong())
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "La montura ID " + montura.id + " a recibido " + Integer
                        .parseInt(infos[2]) + " puntos de exp"
                )
            } catch (ignored: Exception) {
            }

            "FECUNDADA_HACE" -> try {
                var montura: Montura? = _perso.montura
                try {
                    if (infos.size > 1) {
                        id = Integer.parseInt(infos[1])
                        if (id > 0) {
                            id = -id
                        }
                        if (id != 0)
                            montura = Mundo.getMontura(id)
                    }
                } catch (ignored: Exception) {
                }

                if (montura == null) {
                    return
                }
                montura.setFecundada(100)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La montura ID " + montura.id + " esta lista para parir")
            } catch (ignored: Exception) {
            }

            "MONTABLE", "MONTAR" -> try {
                var montura: Montura? = _perso.montura
                try {
                    if (infos.size > 1) {
                        id = Integer.parseInt(infos[1])
                        if (id > 0) {
                            id = -id
                        }
                        if (id != 0)
                            montura = Mundo.getMontura(id)
                    }
                } catch (ignored: Exception) {
                }

                if (montura == null) {
                    return
                }
                montura.setSalvaje(false)
                montura.setMaxEnergia()
                montura.setMaxMadurez()
                montura.fatiga = 0
                val restante = Mundo.getExpMontura(5) - montura.exp
                if (restante > 0) {
                    montura.addExperiencia(restante)
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La montura ID " + montura.id + " ahora es montable")
            } catch (ignored: Exception) {
            }

            "FECUNDABLE", "FECUNDAR" -> try {
                var montura: Montura? = _perso.montura
                try {
                    if (infos.size > 1) {
                        id = Integer.parseInt(infos[1])
                        if (id > 0) {
                            id = -id
                        }
                        if (id != 0)
                            montura = Mundo.getMontura(id)
                    }
                } catch (ignored: Exception) {
                }

                if (montura == null) {
                    return
                }
                // montura.setSalvaje(false);
                montura.amor = 7500
                montura.resistencia = 7500
                montura.setMaxEnergia()
                montura.setMaxMadurez()
                val restante = Mundo.getExpMontura(5) - montura.exp
                if (restante > 0) {
                    montura.addExperiencia(restante)
                }
                if (montura.esCastrado()) {
                    montura.quitarCastrado()
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La montura ID " + montura.id + " ahora esta fecundo")
            } catch (ignored: Exception) {
            }

            "SCROLL", "CAPI" -> {
                var puntos = -1
                try {
                    if (infos.size > 1)
                        puntos = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                if (puntos < 0) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo !== _perso && _cuenta.admin < 5) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "Tu no estas autorizado a dar puntos al resto.\nme saludas a la abuelita bb ?"
                    )
                    return
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.addPuntosStats(puntos)
                GestorSalida.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(objetivo)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Le joueur " + objetivo.nombre + " a reçu " + puntos
                                + " points de capital"
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "El personaje " + objetivo.nombre + " se le ha aumentado "
                                + puntos + " puntos de capital"
                    )
                }
            }
            "DINERO", "KKAMAS" -> {
                try {
                    if (infos.size > 1) {
                        cantLong = java.lang.Long.parseLong(infos[1])
                    }
                } catch (e: Exception) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argument incorrect.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    }
                    return
                }

                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo!!.nombre != _perso.nombre && _cuenta.admin < 5) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "Hola bebe aqui " + Constantes.CREADOR + "\n¿Que tal? seguro me odias por las limitaciones, pero yo te amo bb <3 sigue intentando darle cosas al resto uwu"
                    )
                    return
                }
                objetivo.addKamas(cantLong, true, true)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Tu as " + (if (cantLong < 0) "retire" else "ajoute") + " " + abs(
                            cantLong
                        ) + " kamas a " + objetivo.nombre
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "Ha sido " + (if (cantLong < 0) "retirado" else "agregado") + " " + abs(cantLong) + " kamas a " + objetivo.nombre
                    )
                }
            }
            "REINICIAR_EN", "RESET_IN", "RESET_EN", "REBOOT_IN", "REBOOT_EN", "REBOOTEN", "RESETEN" -> {
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto (minutos)")
                    return
                }

                if (id < 0) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto valor positivo")
                    return
                }
                var msj = if (AtlantaMain.MENSAJE_TIMER_REBOOT.isEmpty()) "REBOOT" else AtlantaMain.MENSAJE_TIMER_REBOOT
                try {
                    infos = mensaje.split(" ".toRegex(), 3).toTypedArray()
                    msj = infos[2]
                } catch (ignored: Exception) {
                }

                val segundos = id * 60
                Mundo.SEG_CUENTA_REGRESIVA = segundos.toLong()
                if (id == 0) {
                    AtlantaMain.SEGUNDOS_REBOOT_SERVER = 0
                    Mundo.MSJ_CUENTA_REGRESIVA = ""
                    GestorSalida.ENVIAR_bRS_PARAR_CUENTA_REGRESIVA_TODOS()
                } else {
                    AtlantaMain.SEGUNDOS_REBOOT_SERVER = segundos
                    Mundo.MSJ_CUENTA_REGRESIVA = msj
                    GestorSalida.ENVIAR_bRI_INICIAR_CUENTA_REGRESIVA_TODOS()
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se lanzo el temporizador rebooot por $id minutos")
            }
            "RESET_RATES" -> {
                if (Mundo.MSJ_CUENTA_REGRESIVA != "") {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Espera que se termine el otro evento.")
                    return
                }
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto (minutos)")
                    return
                }

                if (id < 0) {
                    id = 0
                }
                val segundosRates = id * 60
                AtlantaMain.SEGUNDOS_RESET_RATES = segundosRates + Formulas.segundosON()
                Mundo.SEG_CUENTA_REGRESIVA = segundosRates.toLong()
                Mundo.MSJ_CUENTA_REGRESIVA = "RESET RATES"
                GestorSalida.ENVIAR_bRI_INICIAR_CUENTA_REGRESIVA_TODOS()
                GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS("1RESET_RATES;$id")
            }
            "CUENTA_REGRESIVA" -> {
                if (Mundo.MSJ_CUENTA_REGRESIVA.equals("RESET RATES", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Espera que se termine el evento de super rates.")
                    return
                }
                infos = mensaje.split(" ".toRegex(), 3).toTypedArray()
                try {
                    id = Integer.parseInt(infos[1])
                    str = infos[2]
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                Mundo.SEG_CUENTA_REGRESIVA = id.toLong()
                Mundo.MSJ_CUENTA_REGRESIVA = str
                if (str.equals("LOTERIA", ignoreCase = true)) {
                    Mundo.VENDER_BOLETOS = true
                } else if (str.equals("CACERIA", ignoreCase = true)) {
                    val victima = Mundo.getPersonajePorNombre(Mundo.NOMBRE_CACERIA)
                    if (victima != null && !victima.enLinea()) {
                        val geo = victima.mapa.x.toString() + "|" + victima.mapa.y
                        val rec = Mundo.mensajeCaceria()
                        try {
                            for (perso in Mundo.PERSONAJESONLINE) {
                                GestorSalida.ENVIAR_cMK_CHAT_MENSAJE_PERSONAJE(
                                    perso, "", 0, Mundo.NOMBRE_CACERIA, "INICIA CACERIA - "
                                            + rec + " - USA COMANDO .caceria"
                                )
                                GestorSalida.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(perso, geo)
                            }
                        } catch (ignored: Exception) {
                        }

                    }
                }
                GestorSalida.ENVIAR_ÑL_BOTON_LOTERIA_TODOS(Mundo.VENDER_BOLETOS)
                GestorSalida.ENVIAR_bRI_INICIAR_CUENTA_REGRESIVA_TODOS()
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se inicio la cuenta regresiva con mensaje " + str + " y tiempo " + id
                            + " segundos"
                )
            }
            "BOLETO_DE" -> try {
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                objetivo = Mundo.getPersonaje(Mundo.LOTERIA_BOLETOS[id])
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El dueño del boleto Nº $id es el jugador " + objetivo!!
                        .nombre
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio un error")
                return
            }

            "ADIC_PJ", "MULTIPLICADOR_DAÑO_PJ" -> {
                try {
                    cantFloat = java.lang.Float.parseFloat(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                EfectoHechizo.MULTIPLICADOR_DAÑO_PJ = cantFloat
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "El multiplicador daño personaje ha sido cambiado a $cantFloat"
                )
            }
            "ADIC_MOB", "MULTIPLICADOR_DAÑO_MOB" -> {
                try {
                    cantFloat = java.lang.Float.parseFloat(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                EfectoHechizo.MULTIPLICADOR_DAÑO_MOB = cantFloat
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El multiplicador de daño mob ha sido cambiado a $cantFloat")
            }
            "ADIC_CAC", "MULTIPLICADOR_DAÑO_CAC" -> {
                try {
                    cantFloat = java.lang.Float.parseFloat(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                EfectoHechizo.MULTIPLICADOR_DAÑO_CAC = cantFloat
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El multiplicador de daño CaC ha sido cambiado a $cantFloat")
            }
            // case "TOLERANCIA_VIP" :
            // try {
            // cantFloat = Float.parseFloat(infos[1]);
            // } catch (final Exception e) {
            // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos");
            // return;
            // }
            // Trabajo.TOLERANCIA_VIP = cantFloat;
            // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La tolerancia VIP de magueo ha sido cambiado a "
            // + cantFloat);
            // break;
            // case "TOLERANCIA_NORMAL" :
            // try {
            // cantFloat = Float.parseFloat(infos[1]);
            // } catch (final Exception e) {
            // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos");
            // return;
            // }
            // Trabajo.TOLERANCIA_NORMAL = cantFloat;
            // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso,
            // "La tolerancia Normal de magueo ha sido cambiado a " + cantFloat);
            // break;
            "SET_IA", "SET_IA_MOB", "SET_MOB_IA", "CAMBIAR_IA_MOB", "SET_TIPO_IA" -> {
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                mobModelo = Mundo.getMobModelo(id)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mob no existe")
                    return
                }
                var tipoIA: Byte = 0
                try {
                    tipoIA = java.lang.Byte.parseByte(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                mobModelo.tipoIA = tipoIA
                GestorSQL.UPDATE_MOB_IA_TALLA(mobModelo)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "El mob " + mobModelo.nombre + " a cambiado su IA a: " + tipoIA
                )
            }
            "GET_IA", "GET_IA_MOB", "GET_MOB_IA", "INFO_IA_MOB", "GET_TIPO_IA" -> {
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                mobModelo = Mundo.getMobModelo(id)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mob no existe")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El mob " + mobModelo.nombre + " tiene la IA: " + mobModelo
                        .tipoIA
                )
            }
            "MOB_COLORES" -> {
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                mobModelo = Mundo.getMobModelo(id)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mob no existe")
                    return
                }
                try {
                    str = infos[2]
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                mobModelo.colores = str
                GestorSQL.UPDATE_MOB_COLORES(mobModelo)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "El mob " + mobModelo.nombre + " a cambiado su color a: " + str
                )
            }
            "DISTANCIA_AGRESION_MOB", "DISTANCIA_AGRESION", "MOB_DISTANCIA_AGRESION", "MOB_AGRESION" -> {
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                mobModelo = Mundo.getMobModelo(id)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mob no existe")
                    return
                }
                var agresion: Byte = 0
                try {
                    agresion = java.lang.Byte.parseByte(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                mobModelo.distAgresion = agresion
                GestorSQL.UPDATE_MOB_AGRESION(mobModelo)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El mob " + mobModelo.nombre + " a cambiado su agresion a: "
                            + agresion
                )
            }
            "MOB_SIZE", "MOB_TALLA", "SIZE_MOB", "TALLA_MOB" -> {
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                mobModelo = Mundo.getMobModelo(id)
                if (mobModelo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mob no existe")
                    return
                }
                try {
                    cantShort = java.lang.Short.parseShort(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                mobModelo.talla = cantShort
                GestorSQL.UPDATE_MOB_IA_TALLA(mobModelo)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El mob " + mobModelo.nombre + " a cambiado su talla a: "
                            + cantShort
                )
            }
            "MODIFICAR_STATS_HECHIZO", "MODIFICAR_HECHIZO", "STAT_HECHIZO", "STATS_HECHIZO", "MODIFICAR_STAT_HECHIZO" -> {
                var stat = ""
                try {
                    infos = mensaje.split(" ".toRegex(), 4).toTypedArray()
                    id = Integer.parseInt(infos[1])
                    cantInt = Integer.parseInt(infos[2])
                    stat = infos[3]
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hechizo no existe")
                    return
                }
                var sh: StatHechizo? = null
                try {
                    sh = Hechizo.analizarHechizoStats(id, cantInt, stat)
                    hechizo.addStatsHechizos(cantInt, sh)
                    GestorSQL.UPDATE_STAT_HECHIZO(id, stat, cantInt)
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Se modifico el hechizo " + hechizo.nombre + " (" + hechizo
                            .iD + ") nivel " + cantInt + " a " + stat
                    )
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Stat hechizo incorrecto o no valido")
                    return
                }

            }
            "SET_CONDICIONES_HECHIZO", "SET_CONDICION_HECHIZO", "SET_HECHIZO_CONDICION", "SET_HECHIZO_CONDICIONES", "CONDICIONES_HECHIZO", "SET_CONDICIONES_HECHIZOS", "HECHIZO_CONDICIONES", "CONDICIONES_HECHIZOS", "CONDITION_SPELLS", "CONDITION_SPELL", "CONDITIONS_SPELLS", "CONDITIONS_SPELL", "SPELLS_CONDITIONS", "SPELL_CONDITIONS", "SET_SPELLS_CONDITIONS", "SET_SPELL_CONDITIONS" -> {
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hechizo no existe")
                    return
                }
                var condiciones = ""
                try {
                    condiciones = infos[2]
                } catch (ignored: Exception) {
                }

                hechizo.setCondiciones(condiciones)
                GestorSQL.ACTUALIZAR_CONDICIONES_HECHIZO(id, condiciones)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El hechizo " + hechizo.nombre + " a cambiado sus condiciones : "
                            + condiciones
                )
            }
            "SET_AFECTADOS", "HECHIZO_AFECTADOS", "SPELL_TARGETS", "TARGETS_SPELL", "TARGETS", "AFECTADOS" -> {
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hechizo no existe")
                    return
                }
                var afectados = ""
                try {
                    afectados = infos[2]
                } catch (ignored: Exception) {
                }

                hechizo.setAfectados(afectados)
                GestorSQL.UPDATE_HECHIZO_AFECTADOS(id, afectados)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El hechizo " + hechizo.nombre + " a cambiado sus afectados : "
                            + afectados
                )
            }
            "SET_HECHIZO_VALOR_IA", "SPELL_VALUE_IA", "HECHIZO_VALOR_IA", "VALOR_IA_HECHIZO", "SET_IA_HECHIZO", "SET_VALOR_IA_HECHIZO" -> {
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hechizo no existe")
                    return
                }
                var valorIA = 0
                try {
                    valorIA = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                hechizo.valorIA = valorIA
                GestorSQL.UPDATE_HECHIZOS_VALOR_IA(id, valorIA)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El hechizo " + hechizo.nombre + " a cambiado su valorIA : "
                            + valorIA
                )
            }
            "SPRITE_ID_HECHIZO", "HECHIZO_SPRITE_ID", "SPELL_SPRITE_ID", "SPRITE_ID_SPELL", "SPRITE_ID" -> {
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hechizo no existe")
                    return
                }
                var spriteID = 0
                try {
                    spriteID = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                hechizo.spriteID = spriteID
                GestorSQL.ACTUALIZAR_SPRITE_ID_HECHIZO(id, spriteID)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El hechizo " + hechizo.nombre + " a cambiado su sprite ID : "
                            + spriteID
                )
            }
            "SPELL_SPRITE_INFOS", "HECHIZO_SPRITE_INFOS", "SPRITE_INFOS_HECHIZO", "SPRITE_INFOS_SPELL", "SPRITE_INFOS" -> {
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hechizo no existe")
                    return
                }
                var spriteInfos = ""
                try {
                    spriteInfos = infos[2]
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                hechizo.spriteInfos = spriteInfos
                GestorSQL.ACTUALIZAR_SPRITE_INFO_HECHIZO(id, spriteInfos)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El hechizo " + hechizo.nombre + " a cambiado su spriteInfos : "
                            + spriteInfos
                )
            }
            "GET_SPRITE_INFOS", "DAR_SPRITE_INFOS" -> {
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hechizo no existe")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El hechizo " + hechizo.nombre + " tiene como spriteInfos : "
                            + hechizo.spriteInfos
                )
            }
            "GET_SPRITE_ID", "DAR_SPRITE_ID" -> {
                try {
                    id = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                hechizo = Mundo.getHechizo(id)
                if (hechizo == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Hechizo no existe")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El hechizo " + hechizo.nombre + " tiene como sprite ID : "
                            + hechizo.spriteID
                )
            }
            "ADD_XP", "ADD_EXP", "DAR_EXP", "DAR_XP", "GANAR_XP", "GANAR_EXP" -> try {
                cantInt = Integer.parseInt(infos[1])
                if (cantInt < 1) {
                    cantInt = 1
                }
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.addExperiencia(cantInt.toLong(), true)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Le joueur " + objetivo.nombre + " a gagne " + cantInt
                                + " points experience"
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "EL jugador " + objetivo.nombre + " a ganado " + cantInt
                                + " puntos de experiencia"
                    )
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
            }

            "UP_LEVEL", "NIVEL", "LEVEL" -> try {
                cantInt = Integer.parseInt(infos[1])
                if (cantInt < 1) {
                    cantInt = 1
                }
                if (cantInt > AtlantaMain.NIVEL_MAX_PERSONAJE) {
                    cantInt = AtlantaMain.NIVEL_MAX_PERSONAJE
                }
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (objetivo.encarnacionN != null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "Le joueur est en mode incarnation, impossible de lui augmenter son niveau."
                        )
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso,
                            "No se le puede subir el nivel, porque el personaje es una encarnacion."
                        )
                    }
                    return
                }
                objetivo.subirHastaNivel(cantInt)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le niveau du joueur a ete modifie.")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Ha sido modificado el nivel de " + objetivo.nombre + " a "
                                + cantInt
                    )
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrects")
            }

            "IP_PLAYER", "IP_PERSONAJE", "DAR_IP_PLAYER", "DAR_IP_PERSONAJE", "DAR_IP", "GET_IP", "GET_IP_PLAYER" -> {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecte.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "L'IP du joueur " + objetivo.nombre + " est : " + objetivo
                            .cuenta.actualIP
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "El IP del personaje " + objetivo.nombre + " es: " + objetivo
                            .cuenta.actualIP
                    )
                }
            }
            "MOVER_RECAU", "MOVER_PERCO", "MOVE_PERCO", "MOVER_RECAUDADOR" -> {
                if (_perso.pelea != null) {
                    return
                }
                val recaudador = _perso.mapa.recaudador
                if (recaudador == null || recaudador.pelea != null) {
                    return
                }
                recaudador.moverRecaudador()
                recaudador.setEnRecolecta(false, _perso)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se movio el recaudador del mapa")
            }
            "TIEMPO_PEQUEÑO_SALVAR", "SEGUNDOS_PEQUEÑO_SALVAR" -> try {
                val segundos2: Int
                try {
                    segundos2 = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.SEGUNDOS_PEQUEÑO_SALVAR = segundos2
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "El Tiempo para que los mobs se muevan automaticamente ha sido modificado a "
                            + AtlantaMain.SEGUNDOS_PEQUEÑO_SALVAR + " segundos"
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "DARLE_PELOTAS" -> {
                objetivo = Mundo.getPersonajePorNombre(infos[1])
                for (montura in Mundo._MONTURAS.values) {
                    try {
                        if (montura.dueñoID == Objects.requireNonNull(objetivo)!!.Id) {
                            val restante = Mundo.getExpMontura(5) - montura.exp
                            if (restante > 0) {
                                montura.addExperiencia(restante)
                            }
                            if (montura.esCastrado()) {
                                montura.quitarCastrado()
                            }
                            GestorSalida.ENVIAR_BAT2_CONSOLA(
                                _perso,
                                "A la montura: " + montura.id + " De raza: " + Objects.requireNonNull(montura.objModCertificado)!!.nombre + " Se le devolvieron las pelotas/ovarios"
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
            "DEVOLVER_A_CERCADO" -> {
                objetivo = Mundo.getPersonajePorNombre(infos[1])
                for (montura in Mundo._MONTURAS.values) {
                    try {
                        if (montura.dueñoID == Objects.requireNonNull(objetivo)!!.Id) {
                            if (montura.ubicacion != Montura.Ubicacion.EQUIPADA) {
                                if (montura.ubicacion == Montura.Ubicacion.CERCADO) {
                                    montura.mapa?.cercado!!.borrarMonturaCercado(montura.id)
                                }
                                montura.setMapaCelda(null, null)
                                montura.ubicacion = Montura.Ubicacion.ESTABLO
                                objetivo!!.cuenta.addMonturaEstablo(montura)
                                GestorSalida.ENVIAR_Ee_MONTURA_A_ESTABLO(_perso, '+', montura.detallesMontura())
                            }
                            GestorSalida.ENVIAR_BAT2_CONSOLA(
                                _perso,
                                "A la montura: " + montura.id + " De raza: " + Objects.requireNonNull(montura.objModCertificado)!!.nombre + " Se le ubico en el establo"
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
            "LISTA_MONTURAS" -> {
                objetivo = Mundo.getPersonajePorNombre(infos[1])
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "Ubicaciones de monturas:\n" + Montura.Ubicacion.PERGAMINO + ". Pergamino\n" + Montura.Ubicacion.EQUIPADA + ". Equipada\n" + Montura.Ubicacion.ESTABLO + ". En establo\n" + Montura.Ubicacion.CERCADO + ". En cercado\n" + Montura.Ubicacion.NULL + ". Desconocido.\n"
                )
                for (montura in Mundo._MONTURAS.values) {
                    try {
                        if (montura.dueñoID == Objects.requireNonNull(objetivo)!!.Id) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(
                                _perso,
                                "ID Montura: " + montura.id + " De raza: " + Objects.requireNonNull(montura.objModCertificado)!!.nombre + " En ubicacion: " + montura.ubicacion
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
            "RECUPERAR_ITEMS" -> try {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.Cargar_Objetos_Rollback(_perso)
            } catch (e: Exception) {
                println("Error al cargar los objetos del personaje: " + objetivo!!.nombre)
            }

            "RECUPERAR_ITEMS_ID" -> try {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                val str2 = infos[2]
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "Se le agregaron al personaje: " + objetivo.nombre + " Los items: " + str2
                )
                objetivo.Cargar_Objetos_Comando(_perso, str2)
            } catch (e: Exception) {
                println("Error al cargar los objetos del personaje: " + objetivo!!.nombre)
            }

            "MOVERMOBS" -> try {
                //                    _perso.getMapa().moverGrupoMobs(5);
                Mundo.moverMobs()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se movieron")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "NOMBRES_ADMIN_5" -> try {
                if (_cuenta.admin < 5) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "A donde vas bb uwu ?")
                    return
                }
                if (AtlantaMain.PARAM_MOSTRAR_NOMBRE_ADMIN_5) {
                    AtlantaMain.PARAM_MOSTRAR_NOMBRE_ADMIN_5 = false
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se ha desactivado")
                } else {
                    AtlantaMain.PARAM_MOSTRAR_NOMBRE_ADMIN_5 = true
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se ha activado")
                }

            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "De alguna manera la cagaste.")
            }

            "MOSTRAR_PAVOS", "MOSTRARPAVOS" -> try {
                if (AtlantaMain.PARAM_MOSTRAR_MONTURAS_CERCADOS) {
                    AtlantaMain.PARAM_MOSTRAR_MONTURAS_CERCADOS = false
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se ocultaran")
                } else {
                    AtlantaMain.PARAM_MOSTRAR_MONTURAS_CERCADOS = true
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se mostraran")
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "MOVER_MOBS", "MOVE_MOB", "MOVE_MOBS", "MOVER_MOB" -> {
                if (_perso.pelea != null) {
                    return
                }
                id = 1
                try {
                    id = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                _perso.mapa.moverGrupoMobs(id)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se movieron $id  grupos de mobs del mapa")
            }
            "CRERAR_OBJETO", "TNT", "!GETITEM", "ITEM" ->
                // final boolean esPorConsole = comando.equalsIgnoreCase("!GETITEM");
                try {
                    rateoriginal = AtlantaMain.RATE_RANDOM_ITEM_BASE
                    id = Integer.parseInt(infos[1])
                    val OM = Mundo.getObjetoModelo(id)
                    if (OM == null) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "L'objet $id n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El objeto modelo $id no existe ")
                        }
                        return
                    }
                    if (OM.ogrinas > 0 && _cuenta.admin < 5) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(
                                _perso,
                                "Tu ne possèdes pas le GM necessaire pour spawn cet objet."
                            )
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No posees el nivel de GM requerido")
                        }
                        return
                    }
                    try {
                        if (infos.size > 2) {
                            cantInt = Integer.parseInt(infos[2])
                        }
                    } catch (ignored: Exception) {
                    }

                    if (cantInt < 1) {
                        cantInt = 1
                    }
                    objetivo = _perso
                    if (infos.size > 3) {
                        objetivo = Mundo.getPersonajePorNombre(infos[3])
                    }
                    if (objetivo == null) {
                        if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                        } else {
                            GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                        }
                        return
                    }
                    if (AtlantaMain.PARAM_NOMBRE_ADMIN && _perso.cuenta.admin < 5) {
                        if (_cuenta.admin < 5) {
                            objetivo = _perso
                        }
                        intercambiable = ObjetoModelo.stringFechaIntercambiable(3650)
                    }
                    var useMax = CAPACIDAD_STATS.RANDOM
                    if (infos.size > 4) {
                        useMax = if (infos[4].equals("MAX", ignoreCase = true))
                            CAPACIDAD_STATS.MAXIMO
                        else
                            if (infos[4].equals(
                                    "MIN",
                                    ignoreCase = true
                                )
                            ) CAPACIDAD_STATS.MINIMO else CAPACIDAD_STATS.RANDOM
                    }
                    if (useMax == CAPACIDAD_STATS.MAXIMO) {
                        //						System.out.println("Estoy aqui subiendo el rate uwu");
                        AtlantaMain.RATE_RANDOM_ITEM_BASE = AtlantaMain.RATE_RANDOM_ITEM
                    }
                    val obj = OM.crearObjeto(cantInt, Constantes.OBJETO_POS_NO_EQUIPADO, useMax)
                    if (AtlantaMain.PARAM_NOMBRE_ADMIN && _perso.cuenta.admin < 5) {
                        obj.addStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE, intercambiable)
                        obj.addStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER, "0#0#0#" + _perso.nombre)
                    } else if (AtlantaMain.PARAM_MOSTRAR_NOMBRE_ADMIN_5) {
                        obj.addStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER, "0#0#0#" + _perso.nombre)
                    }
                    objetivo.addObjIdentAInventario(obj, false)
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        strB.append("Creatio de l'objet ").append(OM.nombre).append(" (").append(id).append(") en ")
                            .append(cantInt).append(" exemplaires pour ").append(objetivo.nombre)
                    } else {
                        strB.append("Se creo ").append(cantInt).append(" objeto(s) ").append(OM.nombre).append(" (")
                            .append(id).append(") para el personaje ").append(objetivo.nombre)
                    }
                    AtlantaMain.RATE_RANDOM_ITEM_BASE = rateoriginal
                    println(rateoriginal)
                    when (useMax) {
                        CAPACIDAD_STATS.MAXIMO -> if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            strB.append(" avec des jets parfaits.")
                        } else {
                            strB.append(" con stats maximos")
                        }
                        CAPACIDAD_STATS.MINIMO -> if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                            strB.append(" avec des jets minimuns.")
                        } else {
                            strB.append(" con stats minimos")
                        }
                        else -> {
                        }
                    }
                    if (objetivo.enLinea()) {
                        GestorSalida.ENVIAR_Ow_PODS_DEL_PJ(objetivo)
                    } else {
                        strB.append(", mais le joueur n'est pas en ligne")
                    }
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, strB.toString())
                } catch (ignored: Exception) {
                    val NombreObjeto = mensaje.split(" ".toRegex(), 2).toTypedArray()[1]
                    val lista = StringBuilder()
                    val objetosencontrados = Buscador.buscarItems(NombreObjeto)
                    for (item in objetosencontrados) {
                        lista.append("ID: ").append(item.id).append(" Nombre: ").append(item.nombre)
                            .append("\n")
                    }
                    if (objetosencontrados.size == 1) {
                        val objE = objetosencontrados[0]
                        consolaComando("ITEM ${objE.id}", _cuenta, _perso)
                        return
                    }
                    if (lista.isNotEmpty()) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, lista.toString())
                        return
                    }
                }

            else -> {
                GM_lvl_3(comando, infos, mensaje, _cuenta, _perso)
                return
            }
        }
        // if (_cuenta.getIdioma().equalsIgnoreCase("fr")) {
        // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Commande GM 4!");
        // } else {
        // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Comando de nivel 4");
        // }spamear elbusta 3 HOwww.dofus.com
    }

    private fun GM_lvl_5(
        comando: String, infos: Array<String>, mensaje: String, _cuenta: Cuenta,
        _perso: Personaje
    ) {
        var infos = infos
        var boleano = false
        var idByte: Byte = 0
        var idInt = 0
        var ogrinas = 0
        var accionID = 0
        var idShort: Short = 0
        var celda1: Short = 0
        var celda2: Short = 0
        val objMod: ObjetoModelo?
        var objetivo: Personaje? = null
        var cuenta: Cuenta?
        var str = StringBuilder()
        var args = ""
        when (comando.toUpperCase()) {
            "RELOAD_CONFIG", "CARGAR_CONFIGURACION", "LOAD_CONFIG", "REFRESH_CONFIG" -> AtlantaMain.cargarConfiguracion(
                _perso
            )
            "CLONAR_MAPA" -> try {
                val idClonar = Integer.parseInt(infos[1])
                val nuevaID = Integer.parseInt(infos[2])
                val fecha = infos[3]
                val x = Integer.parseInt(infos[4])
                val y = Integer.parseInt(infos[5])
                val subArea = Integer.parseInt(infos[6])
                val mapa = Mundo.getMapa(idClonar.toShort())
                if (mapa == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mapa a clonar no existe")
                    return
                }
                if (Mundo.getMapa(nuevaID.toShort()) != null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mapa a crear ya existe")
                    return
                }
                if (GestorSQL.CLONAR_MAPA(mapa, nuevaID, fecha, x, y, subArea)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mapa clonado con ID $nuevaID y fecha $fecha")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No se pudo clonar el mapa")
                    return
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }

//            "CREAR_AUDIO", "CREAR_MP3", "CREATE_SOUND", "CREAR_SONIDO" -> try {
//                infos = mensaje.split(" ".toRegex(), 2).toTypedArray()
//                str = StringBuilder(infos[1])
//                val mp3 = TextoAVoz.crearMP3(str.toString(), "")
//                if (mp3 == null) {
//                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No se pudo crear el sonido")
//                } else if (mp3.isEmpty()) {
//                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Excediste en los caracteres")
//                } else {
//                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Sonando el mp3 $mp3")
//                    GestorSalida.ENVIAR_Bv_SONAR_MP3(_perso, mp3)
//                }
//            } catch (ignored: Exception) {
//            }
//
//            "CREAR_AUDIO_IDIOMA", "CREAR_MP3_IDIOMA", "CREAR_MP3_LANG", "CREAR_SOUND_LANG", "CREATE_MP3_LANG", "CREATE_SOUND_LANG", "CREAR_SONIDO_IDIOMA" -> try {
//                infos = mensaje.split(" ".toRegex(), 3).toTypedArray()
//                str = StringBuilder(infos[2])
//                val mp3 = TextoAVoz.crearMP3(str.toString(), infos[1])
//                if (mp3 == null) {
//                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No se pudo crear el sonido")
//                } else if (mp3.isEmpty()) {
//                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Excediste en los caracteres")
//                } else {
//                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Sonando el mp3 $mp3")
//                    GestorSalida.ENVIAR_Bv_SONAR_MP3(_perso, mp3)
//                }
//            } catch (ignored: Exception) {
//            }

            "TELEPORT_TODOS" -> try {
                var mapaID: Short = 0
                var celdaID: Short = 0
                try {
                    if (infos.size > 1) {
                        mapaID = java.lang.Short.parseShort(infos[1])
                    }
                    if (infos.size > 2) {
                        celdaID = java.lang.Short.parseShort(infos[2])
                    }
                } catch (ignored: Exception) {
                }

                val mapa = Mundo.getMapa(mapaID)
                if (mapa == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "MAPID INVALIDE!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mapa a teleportar no existe")
                    }
                    return
                }
                if (celdaID <= -1) {
                    celdaID = mapa.randomCeldaIDLibre
                } else if (mapa.getCelda(celdaID) == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CELLID INVALIDE!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CeldaID invalida")
                    }
                    return
                }
                for (p in Mundo.PERSONAJESONLINE) {
                    p.teleport(mapaID, celdaID)
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }

            "LIMITE_STATS_CON_BUFF" -> try {
                for (s in infos[1].split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    if (s.isEmpty()) {
                        continue
                    }
                    try {
                        val stat = s.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        AtlantaMain.LIMITE_STATS_CON_BUFF[Integer.parseInt(stat[0])] = Integer.parseInt(stat[1])
                    } catch (ignored: Exception) {
                    }

                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }

            "LIMITE_STATS_SIN_BUFF" -> try {
                for (s in infos[1].split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    if (s.isEmpty()) {
                        continue
                    }
                    try {
                        val stat = s.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        AtlantaMain.LIMITE_STATS_SIN_BUFF[Integer.parseInt(stat[0])] = Integer.parseInt(stat[1])
                    } catch (ignored: Exception) {
                    }

                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }

            "GET_CONFIGURACION", "GET_CONFIG" -> GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, AtlantaMain.configuracion)
            "ADD_OBJETO_TRUEQUE", "AGREGAR_OBJETO_TRUEQUE", "ADD_TRUEQUE", "AGREGAR_TRUEQUE" -> try {
                var prioridad = 0
                val npcs = ""
                try {
                    idInt = Integer.parseInt(infos[1])
                    str = StringBuilder(infos[2])
                    prioridad = java.lang.Byte.parseByte(infos[3]).toInt()
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                    return
                }

                objMod = Mundo.getObjetoModelo(idInt)
                if (objMod == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto modelo no existe")
                    return
                }
                if (str.isEmpty()) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objetos necesarios invalidos")
                    return
                }
                Mundo.addObjetoTrueque(objMod.id, str.toString(), prioridad, npcs)
                GestorSQL.INSERT_OBJETO_TRUEQUE(objMod.id, str.toString(), prioridad, npcs, objMod.nombre)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se agrego el objeto trueque " + objMod.nombre + " (" + objMod
                        .id + "), objetos necesarios: " + str + ", prioridad: " + prioridad + ", npcs: " + npcs
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una excepcion")
            }

            "INICIAR_ATAQUE", "START_ATTACK", "STARTATTACK" -> {
                str = StringBuilder(infos[1])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Start the Attack: $str")
                GestorSalida.enviarTodos(1, "AjI$str")
            }
            "PARAR_ATAQUE", "STOP_ATTACK", "STOPATTACK" -> {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Stop the Attack")
                GestorSalida.enviarTodos(1, "AjP")
            }
            "PAQUETE_ATAQUE", "PACKET_ATTACK", "PACKETATTACK" -> {
                str = StringBuilder(infos[1])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Send Packet of Attack: $str")
                GestorSalida.enviarTodos(1, "AjE$str")
            }
            "BAN_PERM_FDP", "BAN_PERMANENTE", "BAN_CLIENTE", "BAN_CLIENT", "BAN_DOFUS" -> try {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecte.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                var tiempo = 10000
                try {
                    if (infos.size > 2) {
                        tiempo = Integer.parseInt(infos[2])
                    }
                } catch (ignored: Exception) {
                }

                GestorSalida.enviar(objetivo, "$$tiempo")
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El personaje " + objetivo.nombre
                            + " ha sido crasheado su cliente por " + tiempo + " minutos"
                )
                objetivo.cuenta.socket?.cerrarSocket(true, "command BAN CLIENTE")
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
            }

            "CRASH", "CRASH_FDP" -> try {
                infos = mensaje.split(" ".toRegex(), 3).toTypedArray()
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecte.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                val veces = 10000
                GestorSalida.enviar(objetivo, "@" + veces + ";HOhttp://" + infos[2])
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El personaje " + objetivo.nombre
                            + " ha sido crasheado con la url " + infos[2]
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
            }

            "SPAMMEAR", "SPAM", "SPAMEAR" -> try {
                infos = mensaje.split(" ".toRegex(), 4).toTypedArray()
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!objetivo.enLinea()) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'est pas connecte.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no esta conectado")
                    }
                    return
                }
                var veces = 10000
                try {
                    if (infos.size > 2) {
                        veces = Integer.parseInt(infos[2])
                    }
                } catch (ignored: Exception) {
                }

                GestorSalida.enviar(objetivo, "@" + veces + ";" + infos[3])
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El personaje " + objetivo.nombre
                            + " ha sido spameado su cliente por " + veces + " veces, con el packet " + infos[3]
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
            }

            "CAMBIAR_NOMBRE", "CHANGE_NAME" -> try {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (infos.size > 2) {
                    val viejoNombre = objetivo.nombre
                    val nombre = Personaje.nombreValido(infos[2], true)
                    if (nombre != null && nombre.isNotEmpty()) {
                        objetivo.cambiarNombre(nombre)
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "Se cambio el nombre del jugador " + viejoNombre + " cambio a "
                                    + objetivo.nombre
                        )
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Nuevo nombre incorrecto o ya esta en uso")
                    }
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ingrese el nuevo nombre")
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
            }

            "GET_PATH" -> {
                try {
                    celda1 = java.lang.Short.parseShort(infos[1])
                    celda2 = java.lang.Short.parseShort(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                idInt = -1
                try {
                    idInt = Integer.parseInt(infos[3])
                } catch (ignored: Exception) {
                }

                val path = Camino.getPathPelea(
                    if (_perso.pelea != null)
                        _perso.pelea.mapaCopia
                    else
                        _perso.mapa, celda1, celda2, idInt, null, true
                )
                if (path != null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Path es " + path._primero + " y " + path._segundo.size)
                    val s = StringBuilder()
                    for (c in path._segundo) {
                        if (c != null) {
                            s.append(c.id.toInt()).append(" ")
                        }
                    }
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "celdas $s")
                } else
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Path es nulo")
            }
            "FINISH_ALL_FIGHTS", "FINALIZAR_PELEAS", "FINISH_COMBATS", "FINISH_FIGHTS" -> {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "FINALIZANDO TODAS LAS PELEAS ... ")
                Mundo.finalizarPeleas()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "100%")
            }
            "REGISTER", "REGISTRO", "REGISTE", "REGISTRAR" -> {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "INICIANDO EL REGISTRO DE JUGADORES Y SQL ... ")
                AtlantaMain.imprimirLogPlayers()
                AtlantaMain.imprimirLogSQL()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "100%")
            }
            "REGISTER_SQL", "REGISTRO_SQL", "REGISTE_SQL", "REGISTRAR_SQL" -> {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "INICIANDO EL REGISTRO DE SQL ... ")
                AtlantaMain.imprimirLogSQL()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "100%")
            }
            "REGISTER_PLAYERS", "REGISTRO_PLAYERS", "REGISTE_PLAYERS", "REGISTRAR_PLAYERS" -> {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "INICIANDO EL REGISTRO DE JUGADORES ... ")
                AtlantaMain.imprimirLogPlayers()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "100%")
            }
            "REGISTRAR_PJ", "REGISTER_PLAYER", "REGISTRAR_PLAYER", "REGISTRAR_JUGADOR" -> {
                objetivo = _perso
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                if (!ServidorSocket.JUGADORES_REGISTRAR.contains(objetivo.cuenta.nombre)) {
                    ServidorSocket.JUGADORES_REGISTRAR.add(objetivo.cuenta.nombre)
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "La cuenta del personaje " + objetivo.nombre
                                + " fue registrada para archivar los logs"
                    )
                }
            }
            "INFO_PJ", "INFO_PLAYER", "INFO_PERSONAJE", "STATS_PERSO", "STATS_PJ", "STATS_PERSONAJE", "STATS_PLAYER" -> {
                objetivo = _perso
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "Abre tu panel de caracteristicas para ver la informacion del jugador " + objetivo.nombre
                )
                GestorSalida.enviarEnCola(_perso, objetivo.stringStats(), false)
            }
            "INVENTARIO" -> {
                objetivo = _perso
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "Abre tu panel de inventario para ver los items del jugador " + objetivo.nombre
                )
                // _perso.setEspiarPj(true);
                GestorSalida.ENVIAR_ASK_PERSONAJE_A_ESPIAR(objetivo, _perso)
                _perso.Multi = objetivo
            }
            "COMANDOS_PERMITIDOS", "COMANDO_PERMITIDO", "ADD_COMANDO_PERMITIDO", "AGREGAR_COMANDO_PERMITIDO" -> try {
                AtlantaMain.COMANDOS_PERMITIDOS.add(infos[1])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se agrego a comandos permitidos: " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Debes poner algun comando para agregar")
            }

            "COMANDOS_VIP", "COMANDO_VIP", "ADD_COMANDO_VIP", "AGREGAR_COMANDO_VIP" -> try {
                AtlantaMain.COMANDOS_VIP.add(infos[1])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se agrego a comandos vips: " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Debes poner algun comando para agregar")
            }

            "PALABRAS_PROHIBIDAS" -> try {
                AtlantaMain.PALABRAS_PROHIBIDAS.add(infos[1])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se agrego a palabras prohibidas: " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Debes poner alguna palabra para agregar")
            }

            "INFO_STUFF_PJ", "INFO_STUFF_PERSO", "INFO_ROPA_PJ", "INFO_ROPA_PERSO" -> {
                objetivo = _perso
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                for (i in Constantes.POSICIONES_EQUIPAMIENTO) {
                    if (str.isNotEmpty()) {
                        str.append(", ")
                    }
                    if (objetivo.getObjPosicion(i) == null) {
                        str.append("null")
                    } else {
                        str.append(objetivo.getObjPosicion(i)!!.id)
                    }
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Stuff de " + objetivo.nombre + " es " + str)
            }
            "TIEMPO_POR_LANZAR_HECHIZO" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                EfectoHechizo.TIEMPO_POR_LANZAR_HECHIZO = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El TIEMPO_POR_LANZAR_HECHIZO cambio a $idInt")
            }
            "TIEMPO_GAME_ACTION" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                EfectoHechizo.TIEMPO_GAME_ACTION = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El TIEMPO_GAME_ACTION cambio a $idInt")
            }
            "TIEMPO_ENTRE_EFECTOS" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                EfectoHechizo.TIEMPO_ENTRE_EFECTOS = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El TIEMPO_ENTRE_EFECTOS cambio a $idInt")
            }
            "TIME_SLEEP_PACKETS_CARGAR_MAPA" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.TIME_SLEEP_PACKETS_CARGAR_MAPA = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El TIME_SLEEP_PACKETS_CARGAR_MAPA cambio a $idInt")
            }
            "PROBABILIDAD_PROTECTOR_RECURSOS" -> {
                try {
                    idByte = java.lang.Byte.parseByte(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.PROBABILIDAD_PROTECTOR_RECURSOS = idByte.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PROBABILIDAD_PROTECTOR_RECURSOS cambio a $idByte")
            }
            "SEGUNDOS_REAPARECER_MOBS" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.SEGUNDOS_REAPARECER_MOBS = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El SEGUNDOS_REAPARECER_MOBS cambio a $idInt")
            }
            "FACTOR_ZERO_DROP" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.FACTOR_ZERO_DROP = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El FACTOR_ZERO_DROP cambio a $idInt")
            }
            "ID_MIMOBIONTE" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.ID_MIMOBIONTE = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La ID del mimobionte cambio a $idInt")
            }
            "MODIFICAR_PARAM" -> try {
                infos = mensaje.split(" ".toRegex(), 3).toTypedArray()
//                val resto = mensaje.split(" ".toRegex(), 2).toTypedArray()[1]
//                consolaComando(resto, _cuenta, _perso)
//                infos = resto.split(" ".toRegex(), 2).toTypedArray()
                AtlantaMain.modificarParam(infos[1], infos[2])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se cambio el parametro: " + infos[1] + " a " + infos[2])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
            }

            "DESHABILITAR_SQL" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.PARAM_DESHABILITAR_SQL = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Deshabilitar sql ahora esta $boleano")
            }
            "OGRINAS_POR_VOTO" -> {
                try {
                    idByte = java.lang.Byte.parseByte(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.OGRINAS_POR_VOTO = idByte.toShort()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Las ogrinas por voto cambio a $idByte")
            }
            "MINUTOS_VALIDAR_VOTO", "MINUTOS_SIGUIENTE_VOTO", "MINUTOS_SIG_VOTO" -> {
                try {
                    idShort = java.lang.Short.parseShort(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.MINUTOS_VALIDAR_VOTO = idShort.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Los minutos para el siguiente voto cambio a $idShort")
            }
            "MAX_MISIONES_ALMANAX" -> {
                try {
                    idShort = java.lang.Short.parseShort(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.MAX_MISIONES_ALMANAX = idShort.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El max de misiones almanax cambio a $idShort")
            }
            "MAX_CARACTERES_SONIDO" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.MAX_CARACTERES_SONIDO = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El MAX_CARACTERES_SONIDO cambio a $idInt")
            }
            "MAX_PACKETS_DESCONOCIDOS" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.MAX_PACKETS_DESCONOCIDOS = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El MAX_PACKETS_PARA_RASTREAR cambio a $idInt")
            }
            "MAX_PACKETS_PARA_RASTREAR" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.MAX_PACKETS_PARA_RASTREAR = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El MAX_PACKETS_PARA_RASTREAR cambio a $idInt")
            }
            "PROBABILIDAD_RECURSO_ESPECIAL", "PROBABILIDAD_OBJ_ESPECIAL" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.PROBABILIDAD_RECURSO_ESPECIAL = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "La probabilidad de recurso recolecta especial cambio a " + AtlantaMain.PROBABILIDAD_RECURSO_ESPECIAL
                )
            }
            "PROBABLIDAD_PERDER_STATS_FM", "PROBABLIDAD_LOST_STATS_FM", "PROBABILIDAD_FALLO_FM" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.PROBABLIDAD_PERDER_STATS_FM = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "La probabilidad de fallo critico FM cambio a " + AtlantaMain.PROBABLIDAD_PERDER_STATS_FM
                )
            }
            "PERMITIR_BONUS_ESTRELLAS" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.PARAM_PERMITIR_BONUS_ESTRELLAS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "El parametro permitir bonus estrellas cambio a " + AtlantaMain.PARAM_PERMITIR_BONUS_ESTRELLAS
                )
            }
            "PERMITIR_BONUS_RETOS" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.PARAM_PERMITIR_BONUS_DROP_RETOS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El parametro permitir bonus retos cambio a $boleano")
            }
            "MAX_STARS_MOBS", "MAX_ESTRELLAS_MOBS" -> {
                try {
                    idShort = java.lang.Short.parseShort(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.MAX_BONUS_ESTRELLAS_MOBS = (idShort * 20).toShort().toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El maximo de estrellas mobs cambio a $idShort")
            }
            "MAX_BONUS_ESTRELLAS_RECURSOS", "MAX_STARS_RESSOURCES", "MAX_STARS_RECURSOS", "MAX_ESTRELLAS_RECURSOS" -> {
                try {
                    idShort = java.lang.Short.parseShort(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.MAX_BONUS_ESTRELLAS_RECURSOS = (idShort * 20).toShort().toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El maximo de estrellas mobs cambio a $idShort")
            }
            "PROBABILIDAD_ESCAPAR_MONTURA_DESPUES_PARIR" -> {
                try {
                    idByte = java.lang.Byte.parseByte(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.PROBABILIDAD_ESCAPAR_MONTURA_DESPUES_FECUNDAR = idByte.toInt()
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "La probabilidad de escapar la montura despues de parir cambio a " + AtlantaMain.PROBABILIDAD_ESCAPAR_MONTURA_DESPUES_FECUNDAR
                )
            }
            "CHANGE_FACE", "CAMBIAR_ROSTRO" -> {
                try {
                    idByte = java.lang.Byte.parseByte(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.cambiarRostro(idByte)
                GestorSalida.ENVIAR_Oa_CAMBIAR_ROPA_MAPA(objetivo.mapa, objetivo)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se cambio el rostro al personaje " + objetivo.nombre + " a "
                            + idByte
                )
            }
            "PERMITIR_BONUS_DROP_RETOS" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_PERMITIR_BONUS_DROP_RETOS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PERMITIR_BONUS_DROP_RETOS cambio a $boleano")
            }
            "PERMITIR_BONUS_EXP_RETOS" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_PERMITIR_BONUS_EXP_RETOS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PERMITIR_BONUS_EXP_RETOS cambio a $boleano")
            }
            "PARAM_RANKING_STAFF" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_LADDER_STAFF = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_RANKING_STAFF cambio a $boleano")
            }
            "PARAM_INFO_DAÑO_BATALLA" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_INFO_DAÑO_BATALLA = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_INFO_DAÑO_BATALLA cambio a $boleano")
            }
            "PARAM_MOSTRAR_EXP_MOBS" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_MOSTRAR_EXP_MOBS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_MOSTRAR_EXP_MOBS cambio a $boleano")
            }
            "PARAM_AUTO_SANAR", "PARAM_AUTO_CURAR", "PARAM_AUTO_RECUPERAR_VIDA" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_AUTO_RECUPERAR_TODA_VIDA = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_AUTO_RECUPERAR_TODA_VIDA cambio a $boleano")
            }
            "PARAM_MOSTRAR_PROBABILIDAD_TACLEO" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_MOSTRAR_PROBABILIDAD_TACLEO = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_MOSTRAR_PROBABILIDAD_TACLEO cambio a $boleano")
            }
            "PARAM_AUTO_SALTAR_TURNO" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_AUTO_SALTAR_TURNO = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_AUTO_SALTAR_TURNO cambio a $boleano")
            }
            "PARAM_TODOS_MOBS_EN_BESTIARIO" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_TODOS_MOBS_EN_BESTIARIO = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_TODOS_MOBS_EN_BESTIARIO cambio a $boleano")
            }
            "PARAM_CAPTURAR_MONTURA_COMO_PERGAMINO" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_CAPTURAR_MONTURA_COMO_PERGAMINO = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_CAPTURAR_MONTURA_COMO_PERGAMINO cambio a $boleano")
            }
            "PARAM_AGRESION_ADMIN" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_AGRESION_ADMIN = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_AGRESION_ADMIN cambio a $boleano")
            }
            "JUGAR_RAPIDO", "PARAM_JUGAR_RAPIDO" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_JUGAR_RAPIDO = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_JUGAR_RAPIDO cambio a $boleano")
            }
            "PARAM_PERDER_ENERGIA" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_PERDER_ENERGIA = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_PERDER_ENERGIA cambio a $boleano")
            }
            "PARAM_ALBUM", "ALBUM_MOBS", "ACTIVAR_ALBUM" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_BESTIARIO = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_ALBUM_MOBS cambio a $boleano")
            }
            "PARAM_AGRESION_MULTICUENTA" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_PERMITIR_MULTICUENTA_PELEA_PVP = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_AGRESION_MULTICUENTA cambio a $boleano")
            }
            "PARAM_LOTERIA" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_LOTERIA = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_LOTERIA cambio a $boleano")
            }
            "COMANDOS_JUGADOR", "COMMANDES_JOUERS", "COMMANDS_PLAYERS", "PARAM_COMANDOS_JUGADOR" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_COMANDOS_JUGADOR = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_COMANDOS_JUGADOR cambio a $boleano")
            }
            "PARAM_AURA", "ACTIVAR_AURA", "AURA" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_ACTIVAR_AURA = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_ACTIVAR_AURA cambio a $boleano")
            }
            "PARAM_ANTI_SPEEDHACK" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_ANTI_SPEEDHACK = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_ANTI_SPEEDHACK cambio a $boleano")
            }
            "PARAM_ANTI_DDOS", "CONTRA_DDOS", "ANTI_DDOS" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_ANTI_DDOS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El PARAM_ANTI_DDOS cambio a $boleano")
            }
            "RECOLECTOR_BASURA", "GC", "GARBAGE_COLLECTOR" -> try {
                System.gc()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se inicio el garbage collector")
            } catch (e: Exception) {
                AtlantaMain.redactarLogServidorln("COMANDO GARBAGE COLLECTOR $e")
                e.printStackTrace()
            }

            "MEMORY", "MEMORY_USE", "MEMORIA", "MEMORIA_USADA", "ESTADO_JVM" -> try {
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "----- ESTADO JVM -----\nFreeMemory: " + Runtime.getRuntime()
                        .freeMemory() / 1048576f + " MB\nTotalMemory: " + Runtime.getRuntime().totalMemory() / 1048576f
                            + " MB\nMaxMemory: " + Runtime.getRuntime()
                        .maxMemory() / 1048576f + " MB\nProcesos: " + Runtime.getRuntime()
                        .availableProcessors()
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "SABIDURIA_PARA_REENVIO" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.SABIDURIA_PARA_REENVIO = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "La sabiduria para aumentar el daño por reenvio cambio a $idInt"
                )
            }
            "MILISEGUNDOS_CERRAR_SERVIDOR", "TIEMPO_CERRAR_SERVIDOR", "TIME_CLOSE_SERVER" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.MILISEGUNDOS_CERRAR_SERVIDOR = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se modifico el tiempo para cerra el servidor a " + idInt
                            + " milisegundos"
                )
            }
            "SEGUNDOS_PUBLICIDAD", "TIEMPO_PUBLICIDAD" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.SEGUNDOS_PUBLICIDAD = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el tiempo de publicidad a $idInt segundos")
            }
            "MILISEGUNDOS_ANTI_FLOOD", "TIEMPO_ANTI_FLOOD" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.MILISEGUNDOS_ANTI_FLOOD = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el tiempo de Anti-Flood a $idInt milisegundos")
            }
            "MIN_NIVEL_KOLISEO" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.MIN_NIVEL_KOLISEO = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el nivel minimo para koliseo a $idInt")
            }
            "SEGUNDOS_INICIAR_KOLISEO", "TIEMPO_KOLISEO" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                Mundo.SEGUNDOS_INICIO_KOLISEO = idInt
                AtlantaMain.SEGUNDOS_INICIAR_KOLISEO = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el tiempo de Koliseo a $idInt")
            }
            "MULTI_CUENTA_KOLISEO", "MULTI_KOLISEO" -> if (AtlantaMain.PARAM_PERMITIR_MULTICUENTA_PELEA_KOLISEO) {
                AtlantaMain.PARAM_PERMITIR_MULTICUENTA_PELEA_KOLISEO = false
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el multi de Koliseo a False")
            } else {
                AtlantaMain.PARAM_PERMITIR_MULTICUENTA_PELEA_KOLISEO = true
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se modifico el multi de Koliseo a True")
            }
            "PARAM_DEVOLVER_OGRINAS", "DEVOLVER_OGRINAS" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_DEVOLVER_OGRINAS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "Se cambio devolver ogrinas a " + AtlantaMain.PARAM_DEVOLVER_OGRINAS
                )
            }
            "PARAM_LADDER", "LADDER" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_LADDER_NIVEL = boleano
                if (boleano)
                    Mundo.actualizarRankings()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se cambio ladder a " + AtlantaMain.PARAM_LADDER_NIVEL)
            }
            "BUSCAR_MOBS", "MOBS", "MOB" -> {
                try {
                    val nombreMob = mensaje.split(" ".toRegex(), 2).toTypedArray()[1]
                    val lista = StringBuilder()
                    for (set in Buscador.buscarMobs(nombreMob)) {
                        lista.append("ID: ").append(set.id).append(" Nombre: ").append(set.nombre)
                            .append("\n")
                    }
                    if (lista.isNotEmpty()) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, lista.toString())
                        return
                    }
                    return
                } catch (e: Exception) {
                }
            }
            "MOBS_EVENTO" -> {
                try {
                    idByte = java.lang.Byte.parseByte(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                Mundo.MOB_EVENTO = idByte
                thread(true, true) {
                    try {
                        Mundo.refrescarTodosMobs()
                    } catch (e: Exception) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(
                            _perso, "Error: $e"
                        )
                    }
                }
                //				new RefrescarTodosMobs().start();
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se cambio el mobEvento a " + idByte
                            + " y se esta refrescando todos los mapas"
                )
            }
            "SET_STATS_OBJ_MODELO", "SET_STATS_OBJETO_MODELO", "SET_STATS_ITEM_TEMPLATE", "SET_STATS_MODELO" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                try {
                    str = StringBuilder(infos[2])
                } catch (ignored: Exception) {
                }

                objMod = Mundo.getObjetoModelo(idInt)
                if (objMod == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto set nulo")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se modifico los statsModelo del objeto " + objMod.nombre
                            + ": \nAntiguo Stats - " + objMod.statsModelo + "\nNuevos Stats - " + str
                )
                objMod.statsModelo = str.toString()
                GestorSQL.UPDATE_STATS_OBJETO_MODELO(idInt, str.toString())
                try {
                    for (npcMod in Mundo.NPC_MODELOS.values) {
                        if (npcMod.objAVender.contains(objMod)) {
                            npcMod.actualizarObjetosAVender()
                        }
                    }
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
                }

            }
            "PRECIO_LOTERIA" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                    boleano = infos[2].equals("true", ignoreCase = true)
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.PRECIO_LOTERIA = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se modifico el precio de loteria  a $idInt" + if (boleano)
                        " ogrinas"
                    else
                        " kamas"
                )
            }
            "PREMIO_LOTERIA" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.PREMIO_LOTERIA = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se modifico el premio de loteria  a " + idInt
                            + if (AtlantaMain.PARAM_LOTERIA_OGRINAS) " ogrinas" else " kamas"
                )
            }
            "LOTERIA_OGRINAS" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_LOTERIA_OGRINAS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Loteria ogrinas es " + AtlantaMain.PARAM_DEVOLVER_OGRINAS)
            }
            "GANADORES_POR_BOLETOS" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                AtlantaMain.GANADORES_POR_BOLETOS = idInt
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se modifico la cantidad de premios por cada " + idInt
                            + " boletos comprados"
                )
            }
            "OGRINAS_OBJETO_MODELO", "SET_ITEM_POINTS" -> try {
                try {
                    idInt = Integer.parseInt(infos[1])
                    ogrinas = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                objMod = Mundo.getObjetoModelo(idInt)
                if (objMod == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto set nulo")
                    return
                }
                objMod.ogrinas = ogrinas
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se modifico el precio del objeto modelo " + objMod.nombre
                            + " a " + ogrinas + " ogrinas"
                )
                try {
                    for (npcMod in Mundo.NPC_MODELOS.values) {
                        if (npcMod.objAVender.contains(objMod)) {
                            npcMod.actualizarObjetosAVender()
                        }
                    }
                } catch (ignored: Exception) {
                }

            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "KAMAS_OBJETO_MODELO" -> try {
                try {
                    idInt = Integer.parseInt(infos[1])
                    ogrinas = Integer.parseInt(infos[2])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                objMod = Mundo.getObjetoModelo(idInt)
                if (objMod == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto set nulo")
                    return
                }
                objMod.kamas = ogrinas
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se modifico el precio del objeto modelo " + objMod.nombre
                            + " a " + ogrinas + " kamas"
                )
                try {
                    for (npcMod in Mundo.NPC_MODELOS.values) {
                        if (npcMod.objAVender.contains(objMod)) {
                            npcMod.actualizarObjetosAVender()
                        }
                    }
                } catch (ignored: Exception) {
                }

            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Ocurrio una exception")
            }

            "SET_STATS_ITEM", "SETSTATSOBJETO", "SET_STATS_OBJETO" -> try {
                infos = mensaje.split(" ".toRegex(), 3).toTypedArray()
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                val obj = Mundo.getObjeto(idInt)
                if (obj == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto nulo")
                    return
                }
                try {
                    str = StringBuilder(infos[2])
                } catch (ignored: Exception) {
                }

                if (!_perso.tieneObjetoID(obj.id) && _cuenta.admin < 5) {
                    GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(
                        _perso,
                        "Alto ahi maquinola, ¿que haces intentando personalizarle el item a otros?\nte llego la fbi papu"
                    )
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Cambio stats del objeto $idInt: \nAntiguo Stats - " + obj
                        .convertirStatsAString(true) + "\nNuevos Stats - " + str
                )
                obj.convertirStringAStats_Base(str.toString())
                if (AtlantaMain.PARAM_NOMBRE_ADMIN && _perso.cuenta.admin < 5) {
                    val intercambiable = ObjetoModelo.stringFechaIntercambiable(3650)
                    obj.addStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER, "0#0#0#" + _perso.nombre)
                    obj.addStatTexto(Constantes.STAT_INTERCAMBIABLE_DESDE, intercambiable)
                } else if (AtlantaMain.PARAM_MOSTRAR_NOMBRE_ADMIN_5) {
                    obj.addStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER, "0#0#0#" + _perso.nombre)
                }
                if (_perso.getObjeto(idInt) != null) {
                    if (_perso.enLinea()) {
                        GestorSalida.ENVIAR_OCK_ACTUALIZA_OBJETO(_perso, obj)
                        if (Constantes.esPosicionEquipamiento(obj.posicion)) {
                            _perso.refrescarStuff(true, true, false)
                        }
                    }
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
            }

            "BORRAR_OBJETO", "BORRAR_ITEM", "DEL_ITEM", "DELETE_ITEM" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }

                val obj = Mundo.getObjeto(idInt)
                if (obj == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Objeto nulo")
                    return
                }
                if (_perso.tieneObjetoID(obj.id)) {
                    _perso.borrarOEliminarConOR(idInt, true)
                } else {
                    Mundo.eliminarObjeto(obj.id)
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se elimino el objeto " + obj.id + " (" + obj.objModelo
                        ?.nombre + ")"
                )
            }
            "CAMBIAR_CONTRASEÑA", "CAMBIAR_CLAVE", "CHANGE_PASSWORD" -> {
                var consultado: Cuenta? = null
                if (infos.size > 1) {
                    consultado = Mundo.getCuentaPorNombre(infos[1])
                }
                if (consultado == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La cuenta no existe")
                    return
                }
                if (infos.size > 2) {
                    str = StringBuilder(infos[2])
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La contraseña no puede estar vacia")
                    return
                }
                GestorSQL.CAMBIAR_CONTRASEÑA_CUENTA(str.toString(), consultado.id)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "La cuenta " + consultado.nombre + " ha cambiado su contraseña a "
                            + str
                )
            }
            "ADMIN" -> {
                idInt = -1
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Arguments incorrecto")
                    return
                }

                if (idInt <= -1) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Merci d'indiquer un GM valide!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El arguemento tiene que ser un numero positivo")
                    }
                    return
                }
                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                objetivo.cuenta.setRango(idInt)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le GM du joueur a ete modifie!")
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "El personaje " + objetivo.nombre + " ahora tiene GM nivel "
                                + idInt
                    )
                }
            }
            "PARAM_PRECIO_RECURSOS_EN_OGRINAS", "RECURSOS_EN_OGRINAS" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_PRECIO_RECURSOS_EN_OGRINAS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Sistema recurso de ogrinas se cambio a $boleano")
            }
            "PARAM_RECETA_SIEMPRE_EXITOSA", "PARAM_CRAFT_SEGURO", "CRAFT_SEGURO" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_CRAFT_SIEMPRE_EXITOSA = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso,
                    "Craft Seguro se cambio a " + AtlantaMain.PARAM_CRAFT_SIEMPRE_EXITOSA
                )
            }
            "DATOS_SQL" -> GestorSalida.ENVIAR_BAT2_CONSOLA(
                _perso, "Datos de la database: \nusuario-" + AtlantaMain.BD_USUARIO
                        + "\npass-" + AtlantaMain.BD_PASS + "\nhost-" + AtlantaMain.BD_HOST + "\ndb_dinamica-"
                        + AtlantaMain.BD_DINAMICA + "\nbd_estatica-" + AtlantaMain.BD_ESTATICA + "\nbd_cuentas-"
                        + AtlantaMain.BD_CUENTAS
            )
            "COMIDA_MASCOTA" -> {
                var mascota = -1
                try {
                    mascota = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                if (mascota == -1) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos incorrectos")
                    return
                }
                val masc = Mundo.getMascotaModelo(mascota)
                if (masc == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mascota nula")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Las estadisticas de la mascota son " + masc.strComidas)
            }
            "BLOQUEAR_ATAQUE", "BLOCK_ATTACK" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (e2: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
                    return
                }

                Mundo.BLOQUEANDO = boleano
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "L'accès au serveur est bloque le temps que les attaques se calment."
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "Se activo medidas de bloqueo acceso al server, hasta que pare el ataque"
                    )
                }
                if (Mundo.BLOQUEANDO) {
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(
                        "El Servidor esta siendo atacado, se ha activado la el ANTI-ATTACK de SlimeS, por el momento no se podran conectar al servidor, " + "pero si continuar jugando, porfavor eviten salir, que en unos minutos reestablecemos la conexion al servidor, GRACIAS!!",
                        "L'accès au serveur est bloque car nous sommes attaques, merci de ne pas vous deconnecter!"
                    )
                } else {
                    GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(
                        "El ataque ha parado, ahora el servidor desbloqueara el acceso a las cuentas, YA PUEDEN LOGUEARSE, SIN TEMOR!!",
                        "L'accès au serveur est retabli!"
                    )
                }
            }
            "PARAM_REGISTRO_JUGADORES" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.PARAM_REGISTRO_LOGS_JUGADORES = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "PARAM_REGISTRO_JUGADORES se cambio a $boleano")
            }
            "MOSTRAR_RECIBIDOS", "RECIBIDOS" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.MOSTRAR_RECIBIDOS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mostrar recibidos se cambio a $boleano")
            }
            "MOSTRAR_ENVIOS", "ENVIADOS" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.MOSTRAR_ENVIOS = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mostrar enviados se cambio a $boleano")
            }
            "DEBUG", "MODO_DEBUG" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.MODO_DEBUG = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Mostrar mensajes debug se cambio a $boleano")
            }
            "MODO_HEROICO" -> {
                try {
                    boleano = infos[1].equals("true", ignoreCase = true)
                } catch (ignored: Exception) {
                }

                AtlantaMain.MODO_HEROICO = boleano
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El MODO_HEROICO cambio a $boleano")
            }
            "ACCOUNT_PASSWORD", "CUENTA_CONTRASEÑA", "GET_PASS" -> {
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                cuenta = objetivo.cuenta
                if (cuenta == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La cuenta es nula")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "La cuenta es " + cuenta.nombre + " y la contraseña es " + cuenta
                        .contraseña
                )
            }
            "BORRAR_PRISMA" -> try {
                val mapa = _perso.mapa
                val prisma = mapa.subArea!!.prisma
                if (prisma == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Esta subArea no posee prisma")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se borro el prisma de la subArea " + prisma.subArea!!.id)
                prisma.murio()
            } catch (ignored: Exception) {
            }

            "SEND", "ENVIAR" -> try {
                infos = mensaje.split(" ".toRegex(), 2).toTypedArray()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El emulador ha recibido el packet " + infos[1])
                _cuenta.socket?.analizar_Packets(infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
            }

            "SEND_FLOOD", "ENVIAR_FLOOD" -> try {
                infos = mensaje.split(" ".toRegex(), 4).toTypedArray()
                val veces = Integer.parseInt(infos[1])
                var time = Integer.parseInt(infos[2])
                if (time <= 0) {
                    time = 1
                }
                for (i in 0 until veces) {
                    _cuenta.socket?.analizar_Packets(infos[3])
                    Thread.sleep(time.toLong())
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El emulador ha recibido el packet " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
            }

            "SEND_PLAYER", "ENVIAR_PJ" -> try {
                infos = mensaje.split(" ".toRegex(), 3).toTypedArray()
                if (infos.size > 1) {
                    objetivo = Mundo.getPersonajePorNombre(infos[1])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                cuenta = objetivo.cuenta
                if (cuenta == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La cuenta es nula")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "El emulador ha recibido del jugador " + objetivo.nombre
                            + " el packet " + infos[2]
                )
                cuenta.socket?.analizar_Packets(infos[2])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
            }

            "RECIVED", "RECIBIR" -> try {
                infos = mensaje.split(" ".toRegex(), 2).toTypedArray()
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La core ha recibido el packet " + infos[1])
                GestorSalida.enviarEnCola(_perso, infos[1], false)
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
            }

            "RECIBIR_TODOS" -> try {
                infos = mensaje.split(" ".toRegex(), 3).toTypedArray()
                val tiempo = Integer.parseInt(infos[1])
                GestorSalida.enviarTodos(tiempo, infos[2])
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La core de todos han recibido el packet " + infos[2])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
            }

            "RECIBIR_MAPA" -> try {
                infos = mensaje.split(" ".toRegex(), 2).toTypedArray()
                for (perso in _perso.mapa.arrayPersonajes!!) {
                    GestorSalida.enviarEnCola(perso, infos[1], false)
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La core de los del mapa han recibido el packet " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
            }

            "RECIBIRNOS" -> try {
                infos = mensaje.split(" ".toRegex(), 3).toTypedArray()
                objetivo = Mundo.getPersonajePorNombre(infos[1])
                GestorSalida.enviarEnCola(objetivo, infos[2], false)
                GestorSalida.enviarEnCola(_perso, infos[2], false)
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "La core de " + infos[1] + " y tu, han recibido el packet "
                            + infos[2]
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento incorrecto")
            }

            // case "LIDER_PVP" :
            // case "LEADER_PVP" :
            // Mundo.actualizarLiderPVP();
            // GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le meilleur joueur PVP a ete mis a jour.");
            // break;
            "ADD_ACCION_PELEA", "ADD_ACTION_FIGHT", "AGREGAR_ACCION_PELEA" -> try {
                val pelea = _perso.pelea
                if (pelea == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No te encuentras en una pelea")
                    return
                }
                infos = mensaje.split(" ".toRegex(), 3).toTypedArray()
                try {
                    accionID = Integer.parseInt(infos[1])
                    args = infos[2]
                } catch (e: Exception) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumento invalido")
                    return
                }

                pelea.addAccion(Accion(accionID, args, ""))
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La pelea agrego la accion: ID $accionID, Args $args")
            } catch (ignored: Exception) {
            }

            "STR_ACCIONES_PELEA" -> {
                if (_perso.pelea == null) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "No te encuentras en una pelea")
                    return
                }
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "La acciones de esta pelea son: " + _perso.pelea
                        .strAcciones
                )
            }
            "GET_PERSONAJES" -> GestorSalida.ENVIAR_BAT2_CONSOLA(
                _perso, "La cantidad de personajes en MundoDofus es de " + Mundo
                    .cantidadPersonajes
            )
            "REGALAR_CREDITOS", "AGREGAR_CREDITOS", "DAR_CREDITOS", "ADD_CREDITS" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                GestorSQL.SET_CREDITOS_CUENTA(
                    GestorSQL.GET_CREDITOS_CUENTA(objetivo.cuentaID) + idInt, objetivo
                        .cuentaID
                )
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        idInt.toString() + " creditos ont ete ajoutes à " + objetivo.nombre
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Se le ha agregado $idInt creditos a " + objetivo
                            .nombre
                    )
                }
            }
            "REGALAR_OGRINAS", "AGREGAR_OGRINAS", "DAR_OGRINAS", "ADD_POINTS" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                GestorSQL.ADD_OGRINAS_CUENTA(idInt.toLong(), objetivo.cuentaID)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        idInt.toString() + " ogrines ont ete ajoutes à " + objetivo.nombre
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "Se le ha agregado " + idInt + " ogrinas a " + objetivo.nombre
                    )
                }
            }
            "DAR_OGRINAS_CUENTA", "ADD_POINTS_ACCOUNT" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                cuenta = _cuenta
                if (infos.size > 2) {
                    cuenta = Mundo.getCuentaPorNombre(infos[2])
                }
                if (cuenta == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le compte pas exist.")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "La cuenta no existe")
                    }
                    return
                }
                GestorSQL.ADD_OGRINAS_CUENTA(idInt.toLong(), cuenta.id)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        idInt.toString() + " ogrines ont ete ajoutes à " + cuenta.nombre
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        "Se le ha agregado " + idInt + " ogrinas a " + cuenta.nombre
                    )
                }
            }
            "ABONO_MINUTES", "ABONO_MINUTOS", "DAR_ABONO_MINUTOS", "ADD_ABONO_MINUTOS", "ADD_ABONO_MINUTES" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                var abonoM = max(GestorSQL.GET_ABONO(objetivo.cuenta.nombre), System.currentTimeMillis())
                abonoM += idInt.toLong() * 60 * 1000L
                abonoM = max(abonoM, System.currentTimeMillis() - 1000)
                GestorSQL.SET_ABONO(abonoM, objetivo.cuentaID)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        idInt.toString() + " minutes abonne ont ete ajoutes à " + objetivo.nombre
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Se le ha agregado $idInt minutos de abono a " + objetivo
                            .nombre
                    )
                }
            }
            "ABONO_HOURS", "ABONO_HORAS", "DAR_ABONO_HORAS", "ADD_ABONO_HORAS", "ADD_ABONO_HOURS" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                var abonoH = max(GestorSQL.GET_ABONO(objetivo.cuenta.nombre), System.currentTimeMillis())
                abonoH += idInt.toLong() * 3600 * 1000L
                abonoH = max(abonoH, System.currentTimeMillis() - 1000)
                GestorSQL.SET_ABONO(abonoH, objetivo.cuentaID)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        idInt.toString() + " heures abonne ont ete ajoutes à " + objetivo.nombre
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Se le ha agregado $idInt horas de abono a " + objetivo
                            .nombre
                    )
                }
            }
            "ABONO_DAYS", "ABONO_DIAS", "DAR_ABONO_DIAS", "ADD_ABONO_DIAS", "ADD_ABONO_DAYS" -> {
                try {
                    idInt = Integer.parseInt(infos[1])
                } catch (ignored: Exception) {
                }

                objetivo = _perso
                if (infos.size > 2) {
                    objetivo = Mundo.getPersonajePorNombre(infos[2])
                }
                if (objetivo == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Le joueur n'existe pas")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El personaje no existe")
                    }
                    return
                }
                var abonoD = max(GestorSQL.GET_ABONO(objetivo.cuenta.nombre), System.currentTimeMillis())
                abonoD += idInt.toLong() * 24 * 3600 * 1000L
                abonoD = max(abonoD, System.currentTimeMillis() - 1000)
                GestorSQL.SET_ABONO(abonoD, objetivo.cuentaID)
                if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso,
                        idInt.toString() + " jouers abonne ont ete ajoutes à " + objetivo.nombre
                    )
                } else {
                    GestorSalida.ENVIAR_BAT2_CONSOLA(
                        _perso, "Se le ha agregado $idInt dias de abono a " + objetivo
                            .nombre
                    )
                }
            }
            "RESETEAR_STATS_OBJETOS_MODELO" -> try {
                val idsObjetos = ArrayList<Int>()
                for (s in infos[1].split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    if (s.isEmpty()) {
                        continue
                    }
                    idsObjetos.add(Integer.parseInt(s))
                }
                Mundo.resetearStatsObjetos(idsObjetos)
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Se reseteo los objetos modelo IDs: " + infos[1])
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }

            "LISTA_DIR_CELDAS" -> try {
                celda1 = java.lang.Short.parseShort(infos[1])
                celda2 = java.lang.Short.parseShort(infos[2])
                val b = Camino.listaDirEntreDosCeldas2(_perso.mapa, celda1, celda2, (-1).toShort())
                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "listaDirEntreDosCeldas2 " + b[0] + "," + b[1] + "," + b[2] + ","
                            + b[3]
                )
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }

            "HORARIO_DIA" -> try {
                val dia = infos[1].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                try {
                    val h = Integer.parseInt(dia[0])
                    if (h in 0..23) {
                        AtlantaMain.HORA_DIA = h
                    }
                } catch (ignored: Exception) {
                }

                try {
                    val h = Integer.parseInt(dia[1])
                    if (h in 0..59) {
                        AtlantaMain.MINUTOS_DIA = h
                    }
                } catch (ignored: Exception) {
                }

                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se cambio el HORARIO_DIA a " + AtlantaMain.HORA_DIA + ":"
                            + (if (AtlantaMain.MINUTOS_DIA < 10) "0" else "") + AtlantaMain.MINUTOS_DIA
                )
                for (p in Mundo.PERSONAJESONLINE) {
                    if (p.esDeDia()) {
                        GestorSalida.ENVIAR_BT_TIEMPO_SERVER(p)
                    }
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }

            "HORARIO_NOCHE" -> try {
                val dia = infos[1].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                try {
                    val h = Integer.parseInt(dia[0])
                    if (h in 0..23) {
                        AtlantaMain.HORA_NOCHE = h
                    }
                } catch (ignored: Exception) {
                }

                try {
                    val h = Integer.parseInt(dia[1])
                    if (h in 0..59) {
                        AtlantaMain.MINUTOS_NOCHE = h
                    }
                } catch (ignored: Exception) {
                }

                GestorSalida.ENVIAR_BAT2_CONSOLA(
                    _perso, "Se cambio el HORARIO_NOCHE a " + AtlantaMain.HORA_NOCHE + ":"
                            + (if (AtlantaMain.MINUTOS_NOCHE < 10) "0" else "") + AtlantaMain.MINUTOS_NOCHE
                )
                for (p in Mundo.PERSONAJESONLINE) {
                    if (p.esDeNoche()) {
                        GestorSalida.ENVIAR_BT_TIEMPO_SERVER(p)
                    }
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }

            "SIMULACION_GE", "SIMULATION_GE" -> try {
                var mapaID: Short = 0
                var celdaID: Short = 0
                try {
                    if (infos.size > 1) {
                        mapaID = java.lang.Short.parseShort(infos[1])
                    }
                    if (infos.size > 2) {
                        celdaID = java.lang.Short.parseShort(infos[2])
                    }
                } catch (ignored: Exception) {
                }

                val mapa = Mundo.getMapa(mapaID)
                if (mapa == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "MAPID INVALIDE!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "El mapa a teleportar no existe")
                    }
                    return
                }
                val ge = infos[3]
                if (celdaID <= -1) {
                    celdaID = mapa.randomCeldaIDLibre
                } else if (mapa.getCelda(celdaID) == null) {
                    if (_cuenta.idioma.equals("fr", ignoreCase = true)) {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CELLID INVALIDE!")
                    } else {
                        GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "CeldaID invalida")
                    }
                    return
                }
                for (p in Mundo.PERSONAJESONLINE) {
                    p.mapa = mapa
                    p.celda = mapa.getCelda(celdaID)
                    GestorSalida.enviar(p, ge)
                }
            } catch (e: Exception) {
                GestorSalida.ENVIAR_BAT2_CONSOLA(_perso, "Argumentos invalidos")
                return
            }

            else -> {
                GM_lvl_4(comando, infos, mensaje, _cuenta, _perso)
                return
            }
        }
    }

    fun GM_lvl_0(
        comando: String, infos: Array<String>, mensaje: String, _cuenta: Cuenta,
        _perso: Personaje
    ) {

    }
}
