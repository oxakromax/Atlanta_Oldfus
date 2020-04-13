package estaticos

import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Condiciones.validaCondiciones
import estaticos.Formulas.getRandomInt
import estaticos.GestorSalida.ENVIAR_GA903_ERROR_PELEA
import estaticos.Inteligencia.Companion.tieneReenvio
import variables.hechizo.StatHechizo
import variables.mapa.Celda
import variables.mapa.Mapa
import variables.mob.GrupoMob
import variables.mob.MobModelo.TipoGrupo
import variables.objeto.Objeto
import variables.oficio.StatOficio
import variables.oficio.Trabajo
import variables.pelea.Luchador
import variables.pelea.Pelea
import variables.pelea.Reto
import variables.personaje.Personaje
import variables.stats.Stats
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*
import java.util.regex.Pattern

object Constantes {
    const val VERSION_EMULADOR = "1.0 Kotlin Version"
    // public static final String SUBVERSION_EMULADOR = ".2";
    const val CREADOR = "SLIMES"
    const val ZAAPI_BONTA =
        "6159,4174,8758,4299,4180,8759,4183,2221,4217,4098,8757,4223,8760,2214,4179,4229,4232,8478,4238,4263,4216,4172,4247,4272,4271,4250,4178,4106,4181,4259,4090,4262,4287,4300,4240,4218,4074,4308"
    const val ZAAPI_BRAKMAR =
        "8756,8755,8493,5304,5311,5277,5317,4612,4618,5112,4639,4637,5116,5332,4579,4588,4549,4562,5334,5295,4646,4629,4601,4551,4607,4930,4622,4620,4615,4595,4627,4623,4604,8754,8753,4630,6167"
    const val ESTATUAS_FENIX =
        "12;12;270|-1;33;1399|10;19;268|5;-9;7796|2;-12;8534|-30;-54;4285|-26;35;4551|-23;38;12169|-11;-54;3360|-43;0;10430|-10;13;9227|-41;-17;9539|36;5;1118|24;-43;7910|27;-33;8054|-60;-3;10672|-58;18;10590|-14;31;5717|25;-4;844|"
    const val x0char = (0x00.toChar()).toString()
    // CREA TU ITEM
    val tipos = ArrayList<Int>()
    val GFX_CREA_TU_ITEM_CAPAS = ArrayList<Int>()
    val GFX_CREA_TU_ITEM_AMULETOS = ArrayList<Int>()
    val GFX_CREA_TU_ITEM_ANILLOS = ArrayList<Int>()
    val GFX_CREA_TU_ITEM_CINTURONES = ArrayList<Int>()
    val GFX_CREA_TU_ITEM_BOTAS = ArrayList<Int>()
    val GFX_CREA_TU_ITEM_SOMBREROS = ArrayList<Int>()
    val GFX_CREA_TU_ITEM_ESCUDOS = ArrayList<Int>()
    val GFX_CREA_TU_ITEM_DOFUS = ArrayList<Int>()
    val GFXS_CREA_TU_ITEM: MutableMap<Int, ArrayList<Int>> =
        TreeMap()
    // CORPULENCIA
    const val CORPULENCIA_NORMAL: Byte = 0
    const val CORPULENCIA_OBESO: Byte = 1
    const val CORPULENCIA_DELGADO: Byte = 2
    // ESTADOS SERVIDOR
    const val SERVIDOR_SALVANDO: Byte = 2
    const val SERVIDOR_ONLINE: Byte = 1
    const val SERVIDOR_OFFLINE: Byte = 0
    // DERECHOS GREMIO
    const val G_TODOS_LOS_DERECHOS = 1
    const val G_MODIF_BOOST = 2 // Modificar los boost
    const val G_MODIF_DERECHOS = 4 // Modificar los derechos
    const val G_INVITAR = 8 // Invitar a nuevos miembros
    const val G_BANEAR = 16 // Bannear
    const val G_TODAS_XP_DONADAS = 32 // Modificar las reparticiones de xp
    const val G_SU_XP_DONADA = 256 // Modificar su repartacion de xp
    const val G_MODIF_RANGOS = 64 // Modificar los rangos
    const val G_PONER_RECAUDADOR = 128 // Poner un recaudador
    const val G_RECOLECTAR_RECAUDADOR = 512 // Recolectar los recaudadores
    const val G_USAR_CERCADOS = 4096 // Utilizar los cercados
    const val G_MEJORAR_CERCADOS = 8192 // Mejorar los cercados
    const val G_OTRAS_MONTURAS = 16384 // Modidicar las monturas de otros miembros
    // DERECHOS CASA
    const val C_VISIBLE_PARA_GREMIO = 1 // casa visible para el gremio
    const val C_ESCUDO_VISIBLE_MIEMBROS = 2 // escudo visible solo para gremio
    const val C_ESCUDO_VISIBLE_PARA_TODOS = 4 // escudo visible para todos
    const val C_ACCESOS_MIEMBROS_SIN_CODIGO = 8 // Entrar sin codigo para el gremio
    const val C_ACCESO_PROHIBIDO_NO_MIEMBROS = 16 // Acceso prohibido no miembros
    const val C_ACCESOS_COFRES_MIEMBROS_SIN_CODIGO = 32 // Cofres sin codigo para el
    // gremio
    const val C_ACCESO_PROHIBIDO_COFRES_NO_MIEMBROS = 64 // Cofre imposible para los q
    const val C_TELEPORT_GREMIO = 128 // Gremio derecho a teleport
    const val C_DESCANSO_GREMIO = 256 // Gremio derecho a descanso
    // COLORES
    const val COLOR_AMARILLO = "FFFF00"
    const val COLOR_ROJO = "FF0000"
    const val COLOR_AZUL = "0000FF"
    const val COLOR_VERDE_CLARO = "00FF00"
    const val COLOR_VERDE_OSCURO = "006600"
    const val COLOR_NEGRO = "000000"
    const val COLOR_BLANCO = "FFFFFF"
    const val COLOR_NARANJA = "FF9900"
    const val COLOR_MARRON = "663300"
    const val COLOR_CELESTE = "66CCFF"
    const val COLOR_MORADO = "990099"
    const val COLOR_CREMA = "FFCC00"
    const val COLOR_ROSADO = "FF99CC"
    const val COLOR_PLOMO = "666666"
    // ESTADO
    const val ESTADO_NEUTRAL: Byte = 0
    const val ESTADO_BORRACHO: Byte = 1
    const val ESTADO_CAPT_ALMAS: Byte = 2
    const val ESTADO_PORTADOR: Byte = 3
    const val ESTADO_TEMEROSO: Byte = 4
    const val ESTADO_DESORIENTADO: Byte = 5
    // Hace que su lanzador ya no sea empujado, ni intercambiado
// Hace que su lanzador ya no sea placado.
// Le impide placar a sus enemigos.
    const val ESTADO_ARRAIGADO: Byte = 6
    const val ESTADO_PESADO: Byte = 7
    const val ESTADO_TRANSPORTADO: Byte = 8
    const val ESTADO_MOTIVACION_SILVESTRE: Byte = 9
    const val ESTADO_DOMESTICACIÓN: Byte = 10
    const val ESTADO_CABALGANDO: Byte = 11
    const val ESTADO_REVOLTOSO: Byte = 12
    const val ESTADO_MUY_REVOLTOSO: Byte = 13
    const val ESTADO_NEVADO: Byte = 14
    const val ESTADO_DESPIERTO: Byte = 15
    const val ESTADO_FRAGILIZADO: Byte = 16
    const val ESTADO_SEPARADO: Byte = 17
    const val ESTADO_HELADO: Byte = 18
    const val ESTADO_AGRIETADO: Byte = 19
    const val ESTADO_DORMIDO: Byte = 26
    const val ESTADO_LEOPARDO: Byte = 27
    const val ESTADO_LIBRE: Byte = 28
    const val ESTADO_GLIFO_IMPAR: Byte = 29
    const val ESTADO_GLIFO_PAR: Byte = 30
    const val ESTADO_TINTA_PRIMARIA: Byte = 31
    const val ESTADO_TINTA_ECUNDARIA: Byte = 32
    const val ESTADO_TINTA_TERCIARIA: Byte = 33
    const val ESTADO_TINTA_CUATERNARIA: Byte = 34
    const val ESTADO_GANAS_DE_MATAR: Byte = 35
    const val ESTADO_GANAS_DE_PARALIZAR: Byte = 36
    const val ESTADO_GANAS_DE_MALDECIR: Byte = 37
    const val ESTADO_GANAS_DE_ENVENENAR: Byte = 38
    const val ESTADO_TURBIO: Byte = 39
    const val ESTADO_CORRUPTO: Byte = 40
    const val ESTADO_SILENCIOSO: Byte = 41
    const val ESTADO_DEBILITADO: Byte = 42
    const val ESTADO_OVNI: Byte = 43
    const val ESTADO_DESCONTENTA: Byte = 44
    const val ESTADO_CONTENTA: Byte = 46
    const val ESTADO_DE_MAL_HUMOR: Byte = 47
    const val ESTADO_DESCONCERTADO: Byte = 48
    const val ESTADO_GHULIFICADO: Byte = 49
    // Este estado hace que no se puedan usar hechizos de curación ni que pueda curarse de ninguna
// forma.
    const val ESTADO_ALTRUISTA: Byte = 50
    const val ESTADO_JUBILADO: Byte = 55
    const val ESTADO_CUENTA_ATRAS_2: Byte = 57
    const val ESTADO_CUENTA_ATRAS_1: Byte = 58
    const val ESTADO_LEAL: Byte = 60
    const val ESTADO_CAMORRISTA: Byte = 61
    const val ESTADO_ESKERDIKAT: Byte = 62
    const val ESTADO_ZOBAL: Byte = 63
    const val ESTADO_ZAIKOPAT: Byte = 64
    const val ESTADO_INCURABLE: Byte = 65
    const val ESTADO_NO_BLOQUEABLE: Byte = 66
    const val ESTADO_INDESPLAZABLE: Byte = 81
    const val ESTADO_PROTEGIDO: Byte = 82
    const val ESTADO_ESCARIFICADO: Byte = 83
    // OBJETOS INTERACTIVOS
    const val OI_ESTADO_LLENO: Byte = 1
    const val OI_ESTADO_ESPERA: Byte = 2
    const val OI_ESTADO_VACIANDO: Byte = 3
    const val OI_ESTADO_VACIO: Byte = 4
    const val OI_ESTADO_LLENANDO: Byte = 5
    const val OI_ESTADO_PALANCA_ABAJO: Byte = 7
    // CELDAS INTERACTIVAS
    const val CI_ESTADO_LLENO: Byte = 1
    const val CI_ESTADO_VACIANDO: Byte = 2
    const val CI_ESTADO_VACIO: Byte = 3
    const val CI_ESTADO_LLENANDO: Byte = 4
    // INTERCAMBIO
    const val INTERCAMBIO_TIPO_NULO: Byte = -1
    const val INTERCAMBIO_TIPO_TIENDA_NPC: Byte = 0
    const val INTERCAMBIO_TIPO_PERSONAJE: Byte = 1
    const val INTERCAMBIO_TIPO_TRUEQUE: Byte = 2
    const val INTERCAMBIO_TIPO_TALLER: Byte = 3
    const val INTERCAMBIO_TIPO_MERCANTE: Byte = 4
    const val INTERCAMBIO_TIPO_COFRE: Byte = 5
    const val INTERCAMBIO_TIPO_MI_TIENDA: Byte = 6
    const val INTERCAMBIO_TIPO_RECAUDADOR: Byte = 8
    const val INTERCAMBIO_TIPO_MERCADILLO_VENDER: Byte = 10
    const val INTERCAMBIO_TIPO_MERCADILLO_COMPRAR: Byte = 11
    const val INTERCAMBIO_TIPO_TALLER_ARTESANO: Byte = 12
    const val INTERCAMBIO_TIPO_TALLER_CLIENTE: Byte = 13
    const val INTERCAMBIO_TIPO_LIBRO_ARTESANOS: Byte = 14
    const val INTERCAMBIO_TIPO_MONTURA: Byte = 15
    const val INTERCAMBIO_TIPO_CERCADO: Byte = 16
    const val INTERCAMBIO_TIPO_RESUCITAR_MASCOTA: Byte = 17
    const val INTERCAMBIO_TIPO_BOUTIQUE: Byte = 20
    // TIPO DE PELEAS
    const val PELEA_TIPO_DESAFIO: Byte = 0 // Desafio
    const val PELEA_TIPO_PVP: Byte = 1 // Agresion PVP
    const val PELEA_TIPO_PRISMA: Byte = 2 // Mobs con alineacion
    const val PELEA_TIPO_PVM_NO_ESPADA: Byte = 3
    const val PELEA_TIPO_PVM: Byte = 4 // PVM personaje vs mobs
    const val PELEA_TIPO_RECAUDADOR: Byte = 5 // Recaudador
    const val PELEA_TIPO_KOLISEO: Byte = 6 // Coliseo
    const val PELEA_TIPO_CACERIA: Byte = 7
    // ESTADO DE PELEAS
    const val PELEA_FASE_INICIO: Byte = 1
    const val PELEA_FASE_POSICION: Byte = 2
    const val PELEA_FASE_COMBATE: Byte = 3
    const val PELEA_FASE_FINALIZADO: Byte = 4
    // RETOS
    const val RETO_ZOMBI: Byte = 1 // Utiliza sólo un punto de movimiento en cada turno.
    const val RETO_ESTATUA: Byte = 2 // Acaba tu turno en la misma casilla donde lo
    // empezaste, hasta que acabe el combate.
    const val RETO_ELEGIDO_VOLUNTARIO: Byte = 3 // Matar %1 el primero.
    const val RETO_APLAZAMIENTO: Byte = 4 // Matar %1 el último.
    const val RETO_AHORRADOR: Byte = 5 // Durante el tiempo que dure el combate, cada
    // personaje sólo debe utilizar la misma acción una
// única vez.
    const val RETO_VERSATIL: Byte = 6 // Durante su turno, cada jugador sólo puede utilizar
    // una vez la misma acción.
    const val RETO_JARDINERO: Byte = 7 // Durante el tiempo que dure el combate, planta una
    // Zanahowia cada vez que el hechizo esté disponible.
    const val RETO_NOMADA: Byte = 8 // Durante el tiempo que dure el combate, utiliza todos
    // tus PM disponibles en cada turno.
    const val RETO_BARBARO: Byte = 9 // No utilices ningún hechizo durante el tiempo que dure
    // este combate.
    const val RETO_CRUEL: Byte = 10 // Debes matar a los adversarios en orden creciente de
    // nivel.
    const val RETO_MISTICO: Byte = 11 // Durante el tiempo que dure el combate, utiliza
    // solamente hechizos.
    const val RETO_SEPULTURERO: Byte = 12 // Durante el tiempo que dure el combate, invoca un
    // Chaferloko cada vez que el hechizo esté
// disponible.
    const val RETO_CASINO_REAL: Byte = 14 // Durante el tiempo que dure el combate, lanzar el
    // hechizo Ruleta cada vez que se encuentre
// disponible.
    const val RETO_ARACNOFILO: Byte = 15 // Invocar una arakna cada vez que el hechizo esté
    // disponible. Válido durante todo el combate.
    const val RETO_ENTOMOLOGO: Byte = 16 // Invocar una Llamita cada vez que el hechizo esté
    // disponible. Válido durante todo el combate.
    const val RETO_INTOCABLE: Byte = 17 // No perder puntos de vida durante el tiempo que
    // dure el combate.
    const val RETO_INCURABLE: Byte = 18 // No curar durante el tiempo que dure el combate.
    const val RETO_MANOS_LIMPIAS: Byte = 19 // Acabar con los monstruos sin ocasionarles
    // daños directos durante lo que dure el
// combate. Se puede utilizar trampas, glifos,
// venenos, daños ocasionados por desplazamiento
// así como los ataques de invocaciones.
    const val RETO_ELEMENTAL: Byte = 20 // Utiliza el mismo elemento de ataque durante todo
    // el combate.
    const val RETO_CIRCULEN: Byte = 21 // No quitar PM a los adversarios mientras dure el
    // combate.
    const val RETO_EL_TIEMPO_PASA: Byte = 22 // No quitar PA a los adversarios durante el
    // tiempo que dure el combate.
    const val RETO_PERDIDO_DE_VISTA: Byte = 23 // No reducir el alcance de los adversarios
    // durante el tiempo que dure el combate.
    const val RETO_LIMITADO: Byte = 24 // Utilizar el mismo hechizo o el mismo ataque cuerpo
    // a cuerpo durante el tiempo que dure el combate.
    const val RETO_ORDENADO: Byte = 25 // Se debe acabar con los adversarios en orden
    // descendiente de nivel.
    const val RETO_NI_PIAS_NI_SUMISAS: Byte = 28 // Los personajes de sexo masculino deben
    // dejar que los de sexo femenino acaben con
// los adversarios. Válido durante todo el
// combate.
    const val RETO_NI_PIOS_NI_SUMISOS: Byte = 29 // Los personajes de sexo femenino deben
    // dejar que los de sexo masculino acaben
// con los adversarios. Válido durante todo
// el combate.
    const val RETO_LOS_PEQUEÑOS_ANTES: Byte = 30 // El personaje de menor nivel debe acabar
    // con los adversarios.
    const val RETO_FOCALIZACION: Byte = 31 // Cuando se ataca a un adversario, hay que
    // matarlo antes de atacar a otro adversario.
    const val RETO_ELITISTA: Byte = 32 // Todos los ataques deben ir dirigidos a %1 hasta que
    // muera.
    const val RETO_SUPERVIVIENTE: Byte = 33 // Ningún aliado debe morir.
    const val RETO_IMPREVISIBLE: Byte = 34 // Todos los ataques deben ir dirigidos a un mismo
    // objetivo que se designa en cada turno de un
// personaje.
    const val RETO_ASESINO_A_SUELDO: Byte = 35 // Debes matar a los adversarios en el orden
    // indicado. Cada vez que mates a un objetivo,
// obtendrás el nombre del próximo al que
// tienes que matar
    const val RETO_AUDAZ: Byte = 36 // Acaba tu turno en una de las casillas pegadas a las de
    // uno de tus adversarios.
    const val RETO_PEGAJOSO: Byte = 37 // Acaba tu turno en una de las casillas pegadas a las
    // de uno de tus aliados.
    const val RETO_BLITZKRIEG: Byte = 38 // Cuando se ataca a un adversario, hay que matarlo
    // antes de que comience su turno.
    const val RETO_ANACORETA: Byte = 39 // No termines nunca tu turno en una casilla
    // adyacente a la de uno de tus aliados.
    const val RETO_PUSILANIME: Byte = 40 // No termines nunca tu turno en una casilla
    // adyacente a la de uno de tus adversarios.
    const val RETO_IMPETUOSO: Byte = 41 // Utiliza todos tus puntos de acción antes de acabar
    // tu turno.
// Cuando un personaje mata a un adversario, tiene que matar obligatoriamente a un (y sólo a un)
// segundo adversario durante su turno.
    const val RETO_EL_DOS_POR_UNO: Byte = 42
    // Nadie puede recibir curas durante su turno.
    const val RETO_ABNEGACION: Byte = 43
    // Cada personaje debe matar al menos a un adversario (que no sea una invocación) durante el
// combate.
    const val RETO_REPARTO: Byte = 44
    // Cuando un personaje ataca a un adversario, ningún otro personaje debe atacar a ese mismo
// adversario durante el tiempo que dure el combate.
    const val RETO_DUELO: Byte = 45
    // Cada personaje debe matar al menos a un adversario durante el combate. Cuando un personaje
// ataca a un adversario, ningún otro personaje puede atacar a ese mismo adversario durante el
// combate.
    const val RETO_CADA_UNO_CON_SU_MONSTRUO: Byte = 46
    // Cuando un aliado pierde puntos de vida, dispones de 3 turnos para rematar a tu aliado o ¡ganar
// el combate!
    const val RETO_CONTAMINACION: Byte = 47
    // El personaje secundario de menor nivel tiene que matar a todos los adversarios (así aprenderás
// a querer abusar como un puerkazo).
    const val RETO_LOS_PERSONAJES_SECUNDARIOS_PRIMERO: Byte = 48
    // Aliados y personajes secundarios deben acabar vivos el combate.
    const val RETO_PROTEJAN_A_SUS_PERSONAJES_SECUNDARIOS: Byte = 49
    // Acaba el combate, para ganar el desafío. Ya se sabe, los desarrolladores del juego son unos
// tramposos...
    const val RETO_LA_TRAMPA_DE_LOS_DESARROLLADORES: Byte = 50
    // OFICIOS
    const val OFICIO_BASE = 1
    const val OFICIO_LEÑADOR = 2
    const val OFICIO_FORJADOR_ESPADAS = 11
    const val OFICIO_ESCULTOR_ARCOS = 13
    const val OFICIO_FORJADOR_MARTILLOS = 14
    const val OFICIO_ZAPATERO = 15
    const val OFICIO_JOYERO = 16
    const val OFICIO_FORJADOR_DAGAS = 17
    const val OFICIO_ESCULTOR_BASTONES = 18
    const val OFICIO_ESCULTOR_VARITAS = 19
    const val OFICIO_FORJADOR_PALAS = 20
    const val OFICIO_MINERO = 24
    const val OFICIO_PANADERO = 25
    const val OFICIO_ALQUIMISTA = 26
    const val OFICIO_SASTRE = 27
    const val OFICIO_CAMPESINO = 28
    const val OFICIO_FORJADOR_HACHAS = 31
    const val OFICIO_PESCADOR = 36
    const val OFICIO_CAZADOR = 41
    const val OFICIO_FORJAMAGO_DAGAS = 43
    const val OFICIO_FORJAMAGO_ESPADAS = 44
    const val OFICIO_FORJAMAGO_MARTILLOS = 45
    const val OFICIO_FORJAMAGO_PALAS = 46
    const val OFICIO_FORJAMAGO_HACHAS = 47
    const val OFICIO_ESCULTORMAGO_ARCOS = 48
    const val OFICIO_ESCULTORMAGO_VARITAS = 49
    const val OFICIO_ESCULTORMAGO_BASTONES = 50
    const val OFICIO_CARNICERO = 56
    const val OFICIO_PESCADERO = 58
    const val OFICIO_FORJADOR_ESCUDOS = 60
    const val OFICIO_ZAPATEROMAGO = 62
    const val OFICIO_JOYEROMAGO = 63
    const val OFICIO_SASTREMAGO = 64
    const val OFICIO_MANITAS = 65
    const val OFICIO_BIJOYERO = 66
    const val OFICIO_JOYERO2 = 67
    // EMOTES
    const val EMOTE_NULO: Byte = 0
    const val EMOTE_SENTARSE: Byte = 1
    const val EMOTE_SEÑAL_CON_MANO: Byte = 2
    const val EMOTE_APLAUDIR: Byte = 3
    const val EMOTE_ENFADARSE: Byte = 4
    const val EMOTE_MIEDO: Byte = 5
    const val EMOTE_MOSTRAR_ARMA: Byte = 6
    const val EMOTE_FLAUTA: Byte = 7
    const val EMOTE_PEDO: Byte = 8
    const val EMOTE_SALUDAR: Byte = 9
    const val EMOTE_BESO: Byte = 10
    const val EMOTE_PIEDRA: Byte = 11
    const val EMOTE_HOJA: Byte = 12
    const val EMOTE_TIJERAS: Byte = 13
    const val EMOTE_CRUZARSE_BRAZOS: Byte = 14
    const val EMOTE_SEÑALAR_DEDO: Byte = 15
    const val EMOTE_CROW: Byte = 16
    const val EMOTE_COMER: Byte = 17
    const val EMOTE_BEBER: Byte = 18
    const val EMOTE_ACOSTARSE: Byte = 19
    const val EMOTE_CAMPEON: Byte = 21
    const val EMOTE_AURA_PODER: Byte = 22
    const val EMOTE_AURA_VAMPIRICA: Byte = 23
    // POSICION DE OBJETOS
    const val OBJETO_POS_NO_EQUIPADO: Byte = -1
    const val OBJETO_POS_AMULETO: Byte = 0
    const val OBJETO_POS_ARMA: Byte = 1
    const val OBJETO_POS_ANILLO1: Byte = 2
    const val OBJETO_POS_CINTURON: Byte = 3
    const val OBJETO_POS_ANILLO_DERECHO: Byte = 4
    const val OBJETO_POS_BOTAS: Byte = 5
    const val OBJETO_POS_SOMBRERO: Byte = 6
    const val OBJETO_POS_CAPA: Byte = 7
    const val OBJETO_POS_MASCOTA: Byte = 8
    const val OBJETO_POS_DOFUS1: Byte = 9
    const val OBJETO_POS_DOFUS2: Byte = 10
    const val OBJETO_POS_DOFUS3: Byte = 11
    const val OBJETO_POS_DOFUS4: Byte = 12
    const val OBJETO_POS_DOFUS5: Byte = 13
    const val OBJETO_POS_DOFUS6: Byte = 14
    const val OBJETO_POS_ESCUDO: Byte = 15
    const val OBJETO_POS_MONTURA: Byte = 16
    const val OBJETO_POS_COMPAÑERO: Byte = 17
    const val OBJETO_POS_OBJ_MUTACION: Byte = 20
    const val OBJETO_POS_BOOST: Byte = 21
    const val OBJETO_POS_MALDICION: Byte = 22
    const val OBJETO_POS_BENDICION: Byte = 23
    const val OBJETO_POS_ROLEPLAY: Byte = 24
    const val OBJETO_POS_PJ_SEGUIDOR: Byte = 25
    val POSICIONES_TODOS = byteArrayOf(
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 17, 20, 21, 22,
        23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48
    )
    @JvmField
    val POSICIONES_EQUIPAMIENTO = byteArrayOf(
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 17, 20,
        21, 22, 23, 24, 25, 26, 27
    )
    val POSICIONES_EQUIPAMIENTO_VISUAL = byteArrayOf(
        OBJETO_POS_SOMBRERO, OBJETO_POS_CAPA, OBJETO_POS_MASCOTA,
        OBJETO_POS_ESCUDO, OBJETO_POS_ARMA
    )
    val POSICIONES_BOOST = byteArrayOf(20, 21, 22, 23, 24, 25, 26, 27)
    // TIPOS DE OBJETOS
    const val OBJETO_TIPO_AMULETO = 1
    const val OBJETO_TIPO_ARCO = 2
    const val OBJETO_TIPO_VARITA = 3
    const val OBJETO_TIPO_BASTON = 4
    const val OBJETO_TIPO_DAGAS = 5
    const val OBJETO_TIPO_ESPADA = 6
    const val OBJETO_TIPO_MARTILLO = 7
    const val OBJETO_TIPO_PALA = 8
    const val OBJETO_TIPO_ANILLO = 9
    const val OBJETO_TIPO_CINTURON = 10
    const val OBJETO_TIPO_BOTAS = 11
    const val OBJETO_TIPO_POCION = 12
    const val OBJETO_TIPO_PERGAMINO_EXP = 13
    const val OBJETO_TIPO_DONES = 14
    const val OBJETO_TIPO_RECURSO = 15
    const val OBJETO_TIPO_SOMBRERO = 16
    const val OBJETO_TIPO_CAPA = 17
    const val OBJETO_TIPO_MASCOTA = 18
    const val OBJETO_TIPO_HACHA = 19
    const val OBJETO_TIPO_HERRAMIENTA = 20
    const val OBJETO_TIPO_PICO = 21
    const val OBJETO_TIPO_GUADAÑA = 22
    const val OBJETO_TIPO_DOFUS = 23
    const val OBJETO_TIPO_OBJETO_DE_BUSQUEDA = 24
    const val OBJETO_TIPO_DOCUMENTO = 25
    const val OBJETO_TIPO_POCION_FORJAMAGIA = 26
    const val OBJETO_TIPO_OBJETO_MUTACION = 27
    const val OBJETO_TIPO_ALIMENTO_BOOST = 28
    const val OBJETO_TIPO_BENDICION = 29
    const val OBJETO_TIPO_MALDICION = 30
    const val OBJETO_TIPO_ROLEPLAY_BUFF = 31
    const val OBJETO_TIPO_PJ_SEGUIDOR = 32
    const val OBJETO_TIPO_PAN = 33
    const val OBJETO_TIPO_CEREAL = 34
    const val OBJETO_TIPO_FLOR = 35
    const val OBJETO_TIPO_PLANTA = 36
    const val OBJETO_TIPO_CERVEZA = 37
    const val OBJETO_TIPO_MADERA = 38
    const val OBJETO_TIPO_MINERAL = 39
    const val OBJETO_TIPO_ALINEACION = 40
    const val OBJETO_TIPO_PEZ = 41
    const val OBJETO_TIPO_GOLOSINA = 42
    const val OBJETO_TIPO_OLVIDO_HECHIZO = 43
    const val OBJETO_TIPO_OLVIDO_OFICIO = 44
    const val OBJETO_TIPO_OLVIDO_DOMINIO = 45
    const val OBJETO_TIPO_FRUTA = 46
    const val OBJETO_TIPO_HUESO = 47
    const val OBJETO_TIPO_POLVO = 48
    const val OBJETO_TIPO_PESCADO_COMESTIBLE = 49
    const val OBJETO_TIPO_PIEDRA_PRECIOSA = 50
    const val OBJETO_TIPO_PIEDRA_BRUTA = 51
    const val OBJETO_TIPO_HARINA = 52
    const val OBJETO_TIPO_PLUMA = 53
    const val OBJETO_TIPO_PELO = 54
    const val OBJETO_TIPO_TEJIDO = 55
    const val OBJETO_TIPO_CUERO = 56
    const val OBJETO_TIPO_LANA = 57
    const val OBJETO_TIPO_SEMILLA = 58
    const val OBJETO_TIPO_PIEL = 59
    const val OBJETO_TIPO_ACEITE = 60
    const val OBJETO_TIPO_PELUCHE = 61
    const val OBJETO_TIPO_PESCADO_VACIADO = 62
    const val OBJETO_TIPO_CARNE = 63
    const val OBJETO_TIPO_CARNE_CONSERVADA = 64
    const val OBJETO_TIPO_COLA = 65
    const val OBJETO_TIPO_METARIA = 66
    const val OBJETO_TIPO_LEGUMBRE = 68
    const val OBJETO_TIPO_CARNE_COMESTIBLE = 69
    const val OBJETO_TIPO_TINTE = 70
    const val OBJETO_TIPO_MATERIA_ALQUIMIA = 71
    const val OBJETO_TIPO_HUEVO_MASCOTA = 72
    const val OBJETO_TIPO_DOMINIO = 73
    const val OBJETO_TIPO_HADA_ARTIFICIAL = 74
    const val OBJETO_TIPO_PERGAMINO_HECHIZO = 75
    const val OBJETO_TIPO_PERGAMINO_CARACTERISTICA = 76
    const val OBJETO_TIPO_CERTIFICADO_DE_LA_PETRERA = 77
    const val OBJETO_TIPO_RUNA_FORJAMAGIA = 78
    const val OBJETO_TIPO_BEBIDA = 79
    const val OBJETO_TIPO_OBJETO_MISION = 80
    const val OBJETO_TIPO_MOCHILA = 81
    const val OBJETO_TIPO_ESCUDO = 82
    const val OBJETO_TIPO_PIEDRA_DEL_ALMA = 83
    const val OBJETO_TIPO_LLAVES = 84
    const val OBJETO_TIPO_PIEDRA_DE_ALMA_LLENA = 85
    const val OBJETO_TIPO_OLVIDO_RECAUDADOR = 86
    const val OBJETO_TIPO_PERGAMINO_BUSQUEDA = 87
    const val OBJETO_TIPO_PIEDRA_MAGICA = 88
    const val OBJETO_TIPO_REGALOS = 89
    const val OBJETO_TIPO_FANTASMA_MASCOTA = 90
    const val OBJETO_TIPO_DRAGOPAVO = 91
    const val OBJETO_TIPO_JALATO = 92
    const val OBJETO_TIPO_OBJETO_CRIA = 93
    const val OBJETO_TIPO_OBJETO_UTILIZABLE = 94
    const val OBJETO_TIPO_TABLA = 95
    const val OBJETO_TIPO_CORTEZA = 96
    const val OBJETO_TIPO_CERTIFICADO_DE_MONTURA = 97
    const val OBJETO_TIPO_RAIZ = 98
    const val OBJETO_TIPO_RED_CAPTURA = 99
    const val OBJETO_TIPO_SACO_RECURSOS = 100
    const val OBJETO_TIPO_BALLESTA = 102
    const val OBJETO_TIPO_PATA = 103
    const val OBJETO_TIPO_ALA = 104
    const val OBJETO_TIPO_HUEVO = 105
    const val OBJETO_TIPO_OREJA = 106
    const val OBJETO_TIPO_CAPARAZON = 107
    const val OBJETO_TIPO_BROTE = 108
    const val OBJETO_TIPO_OJO = 109
    const val OBJETO_TIPO_GELATINA = 110
    const val OBJETO_TIPO_CASCARA = 111
    const val OBJETO_TIPO_PRISMA = 112
    const val OBJETO_TIPO_OBJEVIVO = 113
    const val OBJETO_TIPO_ARMA_MAGICA = 114
    const val OBJETO_TIPO_FRAGMENTO_ALMA_SHUSHU = 115
    const val OBJETO_TIPO_POCION_MASCOTA = 116
    const val OBJETO_TIPO_ALIMENTO_MASCOTA = 117
    const val OBJETO_TIPO_MONEDA_VIP = 118
    const val OBJETO_TIPO_ESPECIALES = 150
    const val OBJETO_TIPO_TROFEO = 151
    const val OBJETO_TIPO_COMPAÑERO = 169
    // ALINEACION
    const val ALINEACION_NULL: Byte = -1
    const val ALINEACION_NEUTRAL: Byte = 0
    const val ALINEACION_BONTARIANO: Byte = 1
    const val ALINEACION_BRAKMARIANO: Byte = 2
    const val ALINEACION_MERCENARIO: Byte = 3
    // ELEMENTOS
    const val ELEMENTO_NULO: Byte = -1
    const val ELEMENTO_NEUTRAL: Byte = 0
    const val ELEMENTO_TIERRA: Byte = 1 // secunda krakens
    const val ELEMENTO_FUEGO: Byte = 2 // terc
    const val ELEMENTO_AGUA: Byte = 3 // cuater
    const val ELEMENTO_AIRE: Byte = 4 // primer
    // SERVICIOS
    const val SERVICIO_CAMBIO_NOMBRE = 1
    const val SERVICIO_CAMBIO_COLOR = 2
    const val SERVICIO_CAMBIO_SEXO = 3
    const val SERVICIO_REVIVIR = 4
    const val SERVICIO_TITULO_PERSONALIZADO = 5
    const val SERVICIO_MIMOBIONTE = 6
    const val SERVICIO_CREA_TU_ITEM = 7
    const val SERVICIO_SISTEMA_ITEMS = 8
    const val SERVICIO_CAMBIO_EMBLEMA = 9
    const val SERVICIO_MONTURA_CAMALEON = 10
    const val SERVICIO_ESCOGER_NIVEL = 11
    const val SERVICIO_ALINEACION_MERCENARIO = 12
    const val SERVICIO_ABONO_DIA = 21
    const val SERVICIO_ABONO_SEMANA = 22
    const val SERVICIO_ABONO_MES = 23
    const val SERVICIO_ABONO_TRES_MESES = 24
    const val SERVICIO_TRANSFORMAR_MONTURA = 30
    const val SERVICIO_CAMBIO_FECA = 101
    const val SERVICIO_CAMBIO_OSAMODA = 102
    const val SERVICIO_CAMBIO_ANUTROF = 103
    const val SERVICIO_CAMBIO_SRAM = 104
    const val SERVICIO_CAMBIO_XELOR = 105
    const val SERVICIO_CAMBIO_ZURCARAK = 106
    const val SERVICIO_CAMBIO_ANIRIPSA = 107
    const val SERVICIO_CAMBIO_YOPUKA = 108
    const val SERVICIO_CAMBIO_OCRA = 109
    const val SERVICIO_CAMBIO_SADIDA = 110
    const val SERVICIO_CAMBIO_SACROGITO = 111
    const val SERVICIO_CAMBIO_PANDAWA = 112
    const val SERVICIO_CAMBIO_TYMADOR = 113
    const val SERVICIO_CAMBIO_ZOBAL = 114
    const val SERVICIO_CAMBIO_STEAMER = 115
    // CLASES
    const val CLASE_FECA: Byte = 1
    const val CLASE_OSAMODAS: Byte = 2
    const val CLASE_ANUTROF: Byte = 3
    const val CLASE_SRAM: Byte = 4
    const val CLASE_XELOR: Byte = 5
    const val CLASE_ZURCARAK: Byte = 6
    const val CLASE_ANIRIPSA: Byte = 7
    const val CLASE_YOPUKA: Byte = 8
    const val CLASE_OCRA: Byte = 9
    const val CLASE_SADIDA: Byte = 10
    const val CLASE_SACROGITO: Byte = 11
    const val CLASE_PANDAWA: Byte = 12
    const val CLASE_TYMADOR: Byte = 13
    const val CLASE_ZOBAL: Byte = 14
    const val CLASE_STREAM: Byte = 15
    const val CLASE_MULTIMAN: Byte = 19
    // public static final byte CLASE_ATORMENTADOR_GOTA = 20;
// public static final byte CLASE_ATORMENTADOR_NUBE = 21;
// public static final byte CLASE_ATORMENTADOR_HOJA = 22;
// public static final byte CLASE_ATORMENTADOR_LLAMAS = 23;
// public static final byte CLASE_ATORMENTADOR_TINIEBLAS = 24;
// public static final byte CLASE_BANDIDO_HECHIZERO = 25;
// public static final byte CLASE_BANDIDO_ARQUERO = 26;
// public static final byte CLASE_BANDIDO_PENDENCIERO = 27;
// public static final byte CLASE_BANDIDO_ESPADACHIN = 28;
// SEXOS
    const val SEXO_MASCULINO: Byte = 0
    const val SEXO_FEMENINO: Byte = 1
    // CAPACIDADES DRAGOPAVOS
// ID EFECTO MAXIMO
    const val BUFF_MAXIMO = 1500
    val BUFF_ARMAS = intArrayOf(
        81, 82, 83, 84, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100,
        101, 108
    )
    val NO_BOOST_CC_IDS = intArrayOf(101)
    val BUFF_ACCION_RESPUESTA = intArrayOf(9, 79, 788, 776)
    // 9 - Esquiva un #1% de los golpes retrocediendo de #2 casilla(s)", c: 0, o: ""
// 79 - #3% de posibilidades de que sufras daños x#1, o de que te cure x#2", c: 0, o: ""
// 788 - Castigo #2 durante #3 turno(s).", c: 0, o: "+"
// 776 - +#1{~1~2 a }#2% de los daños incurables sufridos", c: 75, o: "-", j: true
    val BUFF_SET_CLASE = intArrayOf(281, 282, 283, 284, 285, 286, 287, 288, 289, 290, 291, 292)
    // 281 Aumenta #3 casillas el alcance del hechizo #1", c: 0, o: "+"
// 282 Vuelve modificable el alcance del hechizo #1", c: 0, o: "+"
// 283 +#3 a los daños del hechizo #1", c: 0, o: "+"
// 284 +#3 a las curas del hechizo #1", c: 0, o: "+"
// 285 Reduce en #3 el número de PA que cuesta el hechizo #1.", c: 0, o: "+"
// 286 Reduce en #3 el número de turnos a esperar antes de poder volver a lanzar el hechizo
// #1", c: 0, o: "+"
// 287 +#3 al GC del hechizo #1", c: 0, o: "+"
// 288 Desactiva el lanzamiento en línea recta del hechizo #1", c: 0, o: "+"
// 289 Desactiva la línea de visión del hechizo #1", c: 0, o: "+"
// 290 +#3 al número máximo de veces por que se puede lanzar el hechizo #1", c: 0, o: "+"
// 291 +#3 al número máximo de veces que se le puede lanzar a un mismo objetivo el hechizo
// #1", c: 0, o: "+"
// 292 Fija a #3 el número de turnos para volver a lanzar el hechizo #1", c: 0, o: "+"
// STATS
    const val STAT_ROBA_PM = 77
    const val STAT_MAS_PM_2 = 78
    const val STAT_CURAR_2 = 81
    const val STAT_ROBA_PA = 84
    const val STAT_MENOS_PA = 101
    const val STAT_DAÑOS_ROBAR_AGUA = 91 // Robar Vida(agua)
    const val STAT_DAÑOS_ROBAR_TIERRA = 92 // Robar Vida(tierra)
    const val STAT_DAÑOS_ROBAR_AIRE = 93 // Robar Vida(aire)
    const val STAT_DAÑOS_ROBAR_FUEGO = 94 // Robar Vida(fuego)
    const val STAT_DAÑOS_ROBAR_NEUTRAL = 95 // Robar Vida(neutral)
    const val STAT_DAÑOS_AGUA = 96 // Daños Agua
    const val STAT_DAÑOS_TIERRA = 97 // Daños Tierra
    const val STAT_DAÑOS_AIRE = 98 // Daños Aire
    const val STAT_DAÑOS_FUEGO = 99 // Daños Fuego
    const val STAT_DAÑOS_NEUTRAL = 100 // Daños Neutral
    const val STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA = 105
    const val STAT_REENVIA_HECHIZO = 106
    const val STAT_DAÑOS_DEVUELTOS = 107
    const val STAT_CURAR = 108
    const val STAT_MAS_VIDA = 110
    const val STAT_MAS_PA = 111
    const val STAT_MAS_DAÑOS = 112
    const val STAT_MULTIPLICA_DAÑOS = 114
    const val STAT_MAS_GOLPES_CRITICOS = 115
    const val STAT_MENOS_ALCANCE = 116
    const val STAT_MAS_ALCANCE = 117
    const val STAT_MAS_FUERZA = 118
    const val STAT_MAS_AGILIDAD = 119
    const val STAT_MAS_PA_2 = 120
    const val STAT_MAS_DAÑOS_2 = 121
    const val STAT_MAS_FALLOS_CRITICOS = 122
    const val STAT_MAS_SUERTE = 123
    const val STAT_MAS_SABIDURIA = 124
    const val STAT_MAS_VITALIDAD = 125
    const val STAT_MAS_INTELIGENCIA = 126
    const val STAT_MENOS_PM = 127
    const val STAT_MAS_PM = 128
    const val STAT_MAS_PORC_DAÑOS = 138
    const val STAT_MAS_DAÑO_FISICO = 142
    const val STAT_MENOS_DAÑOS = 145
    const val STAT_CAMBIA_APARIENCIA = 149
    const val STAT_MENOS_SUERTE = 152
    const val STAT_MENOS_VITALIDAD = 153
    const val STAT_MENOS_AGILIDAD = 154
    const val STAT_MENOS_INTELIGENCIA = 155
    const val STAT_MENOS_SABIDURIA = 156
    const val STAT_MENOS_FUERZA = 157
    const val STAT_MAS_PODS = 158
    const val STAT_MENOS_PODS = 159
    const val STAT_MAS_ESQUIVA_PERD_PA = 160
    const val STAT_MAS_ESQUIVA_PERD_PM = 161
    const val STAT_MENOS_ESQUIVA_PERD_PA = 162
    const val STAT_MENOS_ESQUIVA_PERD_PM = 163
    const val STAT_MENOS_DAÑOS_REDUCIDOS = 164
    const val STAT_MAS_DOMINIO = 165
    const val STAT_MENOS_PA_FIJO = 168
    const val STAT_MENOS_PM_FIJO = 169
    const val STAT_MENOS_GOLPES_CRITICOS = 171
    const val STAT_MAS_INICIATIVA = 174
    const val STAT_MENOS_INICIATIVA = 175
    const val STAT_MAS_PROSPECCION = 176
    const val STAT_MENOS_PROSPECCION = 177
    const val STAT_MAS_CURAS = 178
    const val STAT_MENOS_CURAS = 179
    const val STAT_MAS_CRIATURAS_INVO = 182
    const val STAT_REDUCCION_MAGICA = 183
    const val STAT_REDUCCION_FISICA = 184
    const val STAT_MENOS_PORC_DAÑOS = 186
    const val STAT_GANAR_KAMAS = 194
    const val STAT_MAS_RES_PORC_TIERRA = 210
    const val STAT_MAS_RES_PORC_AGUA = 211
    const val STAT_MAS_RES_PORC_AIRE = 212
    const val STAT_MAS_RES_PORC_FUEGO = 213
    const val STAT_MAS_RES_PORC_NEUTRAL = 214
    const val STAT_MENOS_RES_PORC_TIERRA = 215
    const val STAT_MENOS_RES_PORC_AGUA = 216
    const val STAT_MENOS_RES_PORC_AIRE = 217
    const val STAT_MENOS_RES_PORC_FUEGO = 218
    const val STAT_MENOS_RES_PORC_NEUTRAL = 219
    const val STAT_REENVIA_DAÑOS = 220
    const val STAT_MAS_DAÑOS_TRAMPA = 225
    const val STAT_MAS_PORC_DAÑOS_TRAMPA = 226
    @JvmField
    val FUN_STATS_RESTRINGIDAS = intArrayOf(
        115,
        240,
        241,
        242,
        243,
        244,
        245,
        246,
        247,
        248,
        249,
        250,
        251,
        252,
        253,
        254,
        255,
        256,
        257,
        258,
        259,
        260,
        261,
        262,
        263,
        264,
        210,
        211,
        212,
        213,
        214,
        215,
        216,
        217,
        218,
        219,
        STAT_MENOS_PA,
        STAT_MENOS_PA_FIJO,
        STAT_MENOS_PM,
        STAT_MENOS_PM_FIJO,
        STAT_MAS_SABIDURIA,
        STAT_MAS_PROSPECCION
    )
    @JvmField
    val FUN_STATS_RESTRINGIDAS_SOLO_OBJ =
        intArrayOf(STAT_MAS_PA, STAT_MAS_PA_2, STAT_MAS_PM, STAT_MAS_PM_2)
    val TIPOS_EQUIPABLES = intArrayOf(
        OBJETO_TIPO_ANILLO,
        OBJETO_TIPO_AMULETO,
        OBJETO_TIPO_CAPA,
        OBJETO_TIPO_MOCHILA,
        OBJETO_TIPO_CINTURON,
        OBJETO_TIPO_BOTAS,
        OBJETO_TIPO_SOMBRERO,
        OBJETO_TIPO_DAGAS,
        OBJETO_TIPO_MARTILLO,
        OBJETO_TIPO_VARITA,
        OBJETO_TIPO_BASTON,
        OBJETO_TIPO_HACHA,
        OBJETO_TIPO_ESPADA,
        OBJETO_TIPO_ARCO,
        OBJETO_TIPO_PALA,
        OBJETO_TIPO_ESCUDO
    )
    const val STAT_MAS_RES_FIJA_TIERRA = 240
    const val STAT_MAS_RES_FIJA_AGUA = 241
    const val STAT_MAS_RES_FIJA_AIRE = 242
    const val STAT_MAS_RES_FIJA_FUEGO = 243
    const val STAT_MAS_RES_FIJA_NEUTRAL = 244
    const val STAT_MENOS_RES_FIJA_TIERRA = 245
    const val STAT_MENOS_RES_FIJA_AGUA = 246
    const val STAT_MENOS_RES_FIJA_AIRE = 247
    const val STAT_MENOS_RES_FIJA_FUEGO = 248
    const val STAT_MENOS_RES_FIJA_NEUTRAL = 249
    const val STAT_MAS_RES_PORC_PVP_TIERRA = 250
    const val STAT_MAS_RES_PORC_PVP_AGUA = 251
    const val STAT_MAS_RES_PORC_PVP_AIRE = 252
    const val STAT_MAS_RES_PORC_PVP_FUEGO = 253
    const val STAT_MAS_RES_PORC_PVP_NEUTRAL = 254
    const val STAT_MENOS_RES_PORC_PVP_TIERRA = 255
    const val STAT_MENOS_RES_PORC_PVP_AGUA = 256
    const val STAT_MENOS_RES_PORC_PVP_AIRE = 257
    const val STAT_MENOS_RES_PORC_PVP_FUEGO = 258
    const val STAT_MENOS_RES_PORC_PVP_NEUTRAL = 259
    const val STAT_MAS_RES_FIJA_PVP_TIERRA = 260
    const val STAT_MAS_RES_FIJA_PVP_AGUA = 261
    const val STAT_MAS_RES_FIJA_PVP_AIRE = 262
    const val STAT_MAS_RES_FIJA_PVP_FUEGO = 263
    const val STAT_MAS_RES_FIJA_PVP_NEUTRAL = 264
    const val STAT_MAS_DAÑOS_REDUCIDOS_ARMADURAS_FECA = 265
    const val STAT_HECHIZO_CLASE_AUMENTA_ALCANCE = 281
    const val STAT_HECHIZO_CLASE_VUELVE_MODIFICABLE_ALCANCE = 282
    const val STAT_HECHIZO_CLASE_MAS_DAÑOS = 283
    const val STAT_HECHIZO_CLASE_MAS_CURAS = 284
    const val STAT_HECHIZO_CLASE_REDUCE_COSTO_PA = 285
    const val STAT_HECHIZO_CLASE_REDUCE_TURNOS_VOLVER_LANZAR = 286
    const val STAT_HECHIZO_CLASE_MAS_GOLPES_CRITICOS = 287
    const val STAT_HECHIZO_CLASE_DESACTIVA_LINEA_RECTA = 288
    const val STAT_HECHIZO_CLASE_DESACTIVA_LINEA_DE_VUELO = 289
    const val STAT_HECHIZO_CLASE_MAS_VECES_LANZAMIENTOS_POR_TURNO = 290
    const val STAT_HECHIZO_CLASE_MAS_VECES_LANZAMIENTOS_POR_OBJETIVO = 291
    const val STAT_HECHIZO_CLASE_FIJAR_TURNOS_VOLVER_A_LANZAR = 292
    // E[293] = {d: "Aumenta los daños de base del hechizo #1 en #3", c: 0, o: "+"};
// E[294] = {d: "Disminuye el alcance del hechizo #1 de #3", c: 0, o: "-"};
    const val STAT_ROBA_ALCANCE = 320
    const val STAT_MOVER_DESAPARECE_BUFF = 334
    const val STAT_CAMBIA_APARIENCIA_2 = 335
    const val STAT_MAS_HUIDA = 410
    const val STAT_MENOS_HUIDA = 411
    const val STAT_MAS_PLACAJE = 413
    const val STAT_MENOS_PLACAJE = 414
    const val STAT_MAS_DAÑOS_DE_AGUA = 415
    const val STAT_MAS_DAÑOS_DE_TIERRA = 416
    const val STAT_MAS_DAÑOS_DE_AIRE = 417
    const val STAT_MAS_DAÑOS_DE_FUEGO = 418
    const val STAT_MAS_DAÑOS_DE_NEUTRAL = 419
    const val STAT_QUITA_EFECTOS_HECHIZO = 420
    const val STAT_RETROCEDE_CASILLAS = 421
    const val STAT_MAS_PORC_ESCUDO_PDV = 422
    const val STAT_AVANZAR_CASILLAS = 423
    const val STAT_MENOS_PORC_PDV_TEMPORAL = 424
    const val STAT_MAS_DAÑOS_EMPUJE = 425
    const val STAT_MAS_VELOCIDAD = 426
    const val STAT_DETONAR_BOMBA = 427
    const val STAT_INVOCA_BOMBA = 428
    const val STAT_MAS_DAÑOS_CRITICOS = 429
    const val STAT_MAS_REDUCCION_CRITICOS = 430
    const val STAT_MAS_RETIRO_PA = 431
    const val STAT_MAS_RETIRO_PM = 432
    const val STAT_MENOS_RETIRO_PA = 433
    const val STAT_MENOS_RETIRO_PM = 434
    const val STAT_MAS_REDUCCION_EMPUJE = 435
    const val STAT_MENOS_DAÑOS_DE_AGUA = 436
    const val STAT_MENOS_DAÑOS_DE_TIERRA = 437
    const val STAT_MENOS_DAÑOS_DE_AIRE = 438
    const val STAT_MENOS_DAÑOS_DE_FUEGO = 439
    const val STAT_MENOS_DAÑOS_DE_NEUTRAL = 440
    const val STAT_MENOS_DAÑOS_CRITICOS = 441
    const val STAT_MENOS_REDUCCION_CRITICOS = 442
    const val STAT_MENOS_DAÑOS_EMPUJE = 443
    const val STAT_MENOS_REDUCCION_EMPUJE = 444
    const val STAT_DAR_OBJETO = 500
    const val STAT_MAS_COMPAÑERO = 501
    const val STAT_DAR_OGRINAS = 550
    const val STAT_DAR_CREDITOS = 551
    const val STAT_GANAR_EXPERIENCIA = 605
    const val STAT_INVOCA_MOB = 623
    const val STAT_INVOCA_MOB_2 = 628
    const val STAT_ENCARNACION_NIVEL = 669
    const val STAT_CAMBIO_ELEMENTO_DAÑO = 700
    const val STAT_POTENCIA_RUNA = 701
    const val STAT_POTENCIA_CAPTURA_ALMA = 705
    const val STAT_DOMESTICAR_MONTURA = 706
    const val STAT_NOMBRE_MOB = 717
    const val STAT_TITULO = 724
    const val STAT_AGREDIR_AUTOMATICAMENTE = 731
    const val STAT_BONUS_CAPTURA_ALMA = 750
    const val STAT_ARMA_CAZA = 795
    const val STAT_PUNTOS_VIDA = 800
    const val STAT_RECIBIDO_EL = 805
    const val STAT_CORPULENCIA = 806
    const val STAT_ULTIMA_COMIDA = 807
    const val STAT_SE_HA_COMIDO_EL = 808
    const val STAT_TAMAÑO_POCES = 810
    const val STAT_TURNOS = 811
    const val STAT_RESISTENCIA = 812
    const val STAT_LLAVE_MAZMORRA = 814
    const val STAT_AURA = 850
    const val STAT_COLOR_NOMBRE_OBJETO = 900
    const val STAT_CAMBIAR_GFX_OBJETO = 901
    const val STAT_CAMBIAR_NOMBRE_OBJETO = 902
    const val STAT_LANZA_UN_COMBATE_CONTRA = 905
    const val STAT_MAS_PORC_EXP = 910
    const val STAT_MAS_PORC_PP = 911
    const val STAT_APARIENCIA_OBJETO = 915
    const val STAT_AUMENTAR_SERENIDAD = 930
    const val STAT_AUMENTA_AGRESIVIDAD = 931
    const val STAT_AUMENTA_RESISTENCIA = 932 // Aumenta la resistencia", c: 0, o: "+"
    const val STAT_DISMINUYE_RESITENCIA = 933
    const val STAT_AUMENTA_AMOR = 934 // Aumenta el amor", c: 0, o: "+"
    const val STAT_DISMINUYE_AMOR = 935 // Disminuye el amor", c: 0, o: "-"
    const val STAT_ACELERA_MADUREZ = 936 // Acelera la madurez", c: 0, o: "+"
    const val STAT_RALENTIZA_MADUREZ = 937 // Ralentiza la madurez", c: 0, o: "-"
    const val STAT_AUMENTA_CAPACIDADES_MASCOTA = 939
    const val STAT_CAPACIDADES_MEJORADAS = 940 // Capacidades mejoradas", c: 0, o: "+"
    const val STAT_QUITA_OBJETO_CRIA = 946
    const val STAT_RECUPERAR_OBJETO_CERCADO = 947
    const val STAT_OBJETO_CERCADO = 948 // Objeto para cercado", c: 0, o: "/"
    const val STAT_SUBIR_BAJAR_MONTURA = 949
    const val STAT_DAR_ESTADO = 950 // Estado #3", c: 71, o: "/"
    const val STAT_QUITAR_ESTADO = 951 // Quita el estado \'\'#3\'\'", c: 71, o: "/"
    const val STAT_ALINEACION = 960 // Alineación: #3", c: 0, o: "/"
    const val STAT_RANGO = 961 // Rango: #3", c: 0, o: "/"
    const val STAT_NIVEL = 962 // Nivel: #3", c: 0, o: "/"
    const val STAT_CREADA_HACE_DIAS = 963 // Creada hace: #3 día(s)", c: 0, o: "/"
    const val STAT_APELLIDOS = 964 // Apellidos: #4", c: 0, o: "/"
    const val STAT_REAL_GFX = 970
    const val STAT_HUMOR_OBJEVIVO = 971
    const val STAT_SKIN_OBJEVIVO = 972
    const val STAT_REAL_TIPO = 973
    const val STAT_EXP_OBJEVIVO = 974
    const val STAT_INTERCAMBIABLE_DESDE = 983
    const val STAT_ = 984 // null", c: 0, o: "/"
    const val STAT_MODIFICADO_POR = 985 // Modificado por: #4", c: 0, o: "/"
    const val STAT_PREPARA_PERGAMINOS = 986
    const val STAT_PERTENECE_Y_NO_VENDER = 987 // Pertenece a: #4", c: 0, o: "/"
    const val STAT_FACBRICADO_POR = 988 // Fabricado por: #4", c: 0, o: "/"
    const val STAT_MISION = 989
    const val STAT_NUMERO_COMIDAS = 990
    const val STAT_POZO_RESIDUAL = 991
    const val STAT_LIGADO_A_CUENTA = 992
    const val STAT_CERTIFICADO_NO_VALIDO = 994
    const val STAT_CONSULTAR_MONTURA = 995
    const val STAT_PERTENECE_A = 996
    const val STAT_NOMBRE = 997
    const val STAT_VALIDEZ = 998
    const val STAT_PERSONAJE_SEGUIDOR = 999
    val STAT_REPETIBLE = intArrayOf(
        STAT_INVOCA_MOB,
        STAT_INVOCA_MOB_2,
        STAT_NOMBRE_MOB,
        STAT_DAR_OBJETO,
        STAT_LLAVE_MAZMORRA
    )
    val STAT_TEXTO = intArrayOf(
        7,
        10,
        146,
        148,
        188,
        197,
        201,
        221,
        222,
        229,
        230,
        333,
        501,
        513,
        600,
        602,
        603,
        604,
        614,
        615,
        616,
        620,
        622,
        624,
        627,
        640,
        641,
        642,
        643,
        645,
        647,
        648,
        649,
        669,
        699,
        700,
        701,
        705,
        710,
        715,
        716,
        717,
        720,
        724,
        725,
        730,
        751,
        760,
        765,
        791,
        795,
        800,
        805,
        806,
        807,
        808,
        810,
        811,
        813,
        825,
        900,
        901,
        902,
        905,
        915,
        930,
        931,
        932,
        933,
        934,
        935,
        936,
        937,
        939,
        940,
        946,
        947,
        948,
        949,
        950,
        960,
        961,
        962,
        963,
        964,
        970,
        971,
        972,
        973,
        974,
        983,
        985,
        986,
        987,
        988,
        989,
        990,
        992,
        994,
        996,
        997,
        998,
        999
    )
    // tipo de quests
// Q.t[5] = "Descubre la zona: #1";
// Q.t[0] = "#1";
// Q.t[13] = "Elimina #1";
// Q.t[4] = "Descubre el mapa: #1";
// Q.t[1] = "Ve a ver a #1";
// Q.t[10] = "Escolta a #1 #2";
// Q.t[7] = "Vence al monstruo: #1";
// Q.t[2] = "Enseña a #1: #3 #2";
// Q.t[3] = "Entrega a #1: x#3 #2";
// Q.t[12] = "Lleva #3 alma(s) de #2 a #1";
// Q.t[9] = "Vuelve a ver a #1";
// Q.t[8] = "Utiliza: #1";
// Q.t[11] = "Vence a un jugador en desafío #1";
// Q.t[6] = "Vence a x#2 #1 en un solo combate";
// otros efectos
// OBJEVIVOS
    val NIVELES_OBJEVIVOS = intArrayOf(
        0, 10, 21, 33, 46, 60, 75, 91, 108, 126, 145, 165, 186, 208, 231, 255,
        280, 306, 333, 361
    )
    // EXP OFICIO
    const val OFICIO_EXP_TIPO_RECOLECCION = 1
    const val OFICIO_EXP_TIPO_CRAFT = 2
    // ALMANAX
    const val ALMANAX_BONUS_EXP_PJ = 1
    const val ALMANAX_BONUS_KAMAS = 2
    const val ALMANAX_BONUS_DROP = 3
    const val ALMANAX_BONUS_EXP_OFICIO_CRAFT = 4
    const val ALMANAX_BONUS_EXP_OFICIO_RECOLECCION = 5
    const val ALMANAX_BONUS_DROPS_RECOLECCION = 6
    const val ALMANAX_BONUS_HONOR = 7
    // CAPACIDADES DRAGOPAVOS
    const val HABILIDAD_INFATIGABLE: Byte = 1 // Una montura infatigable tiene mucha más
    // energía que una montura normal y también
// recupera mucho más rápido
    const val HABILIDAD_PORTADORA: Byte = 2 // Una montura portadora puede llevar una mayor
    // cantidad de objetos que una montura normal
    const val HABILIDAD_REPRODUCTORA: Byte = 3 // Una montura reproductora trae al mundo más
    // monturitas que una montura normal
    const val HABILIDAD_SABIA: Byte = 4 // Una montura sabia evoluciona dos veces más rápido
    // que una montura normal
    const val HABILIDAD_RESISTENTE: Byte = 5 // Una montura resistente se vuelve resistente
    // más rápido que una montura normal
    const val HABILIDAD_ENAMORADA: Byte = 6 // Una montura enamorada se enamora más que una
    // montura normal
    const val HABILIDAD_PRECOZ: Byte = 7 // Una montura precoz se vuelve madura más rápido
    // que una montura normal
    const val HABILIDAD_PREDISPUESTA: Byte = 8 // Una montura predispuesta genéticamente
    // tiene más probabilidades de transmitir sus
// características genéticas que una montura
// normal
    const val HABILIDAD_CAMALEON: Byte = 9 // Una montura camaleónica cambia su apariencia
    // según el plumaje del aventurero que la monta
// SKILLS
    const val SKILL_MAGUEAR_DAGA = 1
    const val SKILL_TALAR_FRESNO = 6
    const val SKILL_TALAR_ROBLE = 10
    const val SKILL_CREAR_ANILLO = 11
    const val SKILL_CREAR_AMULETO = 12
    const val SKILL_CREAR_BOTAS = 13
    const val SKILL_CREAR_CINTURON = 14
    const val SKILL_CREAR_ARCO = 15
    const val SKILL_CREAR_VARITA = 16
    const val SKILL_CREAR_BASTON = 17
    const val SKILL_CREAR_DAGA = 18
    const val SKILL_CREAR_MARTILLO = 19
    const val SKILL_CREAR_ESPADA = 20
    const val SKILL_CREAR_PALA = 21
    const val SKILL_PELAR_PATATAS = 22
    const val SKILL_CREAR_POCIMA = 23
    const val SKILL_RECOLECTAR_HIERRO = 24
    const val SKILL_RECOLECTAR_COBRE = 25
    const val SKILL_RECOLECTAR_BRONCE = 26
    const val SKILL_HACER_PAN = 27
    const val SKILL_RECOLECTAR_KOBALTO = 28
    const val SKILL_RECOLECTAR_PLATA = 29
    const val SKILL_RECOLECTAR_ORO = 30
    const val SKILL_RECOLECTAR_BAUXITA = 31
    const val SKILL_FUNDIR = 32
    const val SKILL_TALAR_TEJO = 33
    const val SKILL_TALAR_EBANO = 34
    const val SKILL_TALAR_OLMO = 35
    const val SKILL_TALAR_ARCE = 37
    const val SKILL_TALAR_CARPE = 38
    const val SKILL_TALAR_CASTAÑO = 39
    const val SKILL_TALAR_NOGAL = 40
    const val SKILL_TALAR_CEREZO_SILVESTRE = 41
    const val SKILL_RECOGER_PATATAS = 42
    const val SKILL_GUARDAR_POSICION = 44
    const val SKILL_SEGAR_TRIGO = 45
    const val SKILL_SEGAR_LUPULO = 46
    const val SKILL_MOLER = 47
    const val SKILL_PULIR = 48
    const val SKILL_SEGAR_LINO = 50
    const val SKILL_SEGAR_CENTENO = 52
    const val SKILL_SEGAR_CEBADA = 53
    const val SKILL_SEGAR_CAÑAMO = 54
    const val SKILL_RECOLECTAR_ESTAÑO = 55
    const val SKILL_RECOLECTAR_MANGANESO = 56
    const val SKILL_SEGAR_AVENA = 57
    const val SKILL_SEGAR_MALTA = 58
    const val SKILL_REGENERARSE = 62
    const val SKILL_CREAR_SOMBRERO = 63
    const val SKILL_CREAR_CAPA = 64
    const val SKILL_CREAR_HACHA = 65
    const val SKILL_CREAR_GUADAÑA = 66
    const val SKILL_CREAR_PICO = 67
    const val SKILL_COSECHAR_LINO = 68
    const val SKILL_COSECHAR_CAÑAMO = 69
    const val SKILL_COSECHAR_TREBOL = 71
    const val SKILL_COSECHAR_MENTA_SALVAJE = 72
    const val SKILL_COSECHAR_ORQUIDEA = 73
    const val SKILL_COSECHAR_EDELWEISS = 74
    const val SKILL_PONER_CERROJO_CASA = 81
    const val SKILL_ENTRAR_CASA = 84
    const val SKILL_HILA = 95
    const val SKILL_HACER_CERVEZA = 96
    const val SKILL_COMPRAR_CASA = 97
    const val SKILL_VENDER_CASA = 98
    const val SKILL_QUITAR_CERROJO_CASA = 100
    const val SKILL_SERRAR = 101
    const val SKILL_SACAR_AGUA = 102
    const val SKILL_ABRIR_COFRE = 104
    const val SKILL_PONER_CERROJO_COFRE = 105
    const val SKILL_QUITAR_CERROJO_COFRE = 106
    const val SKILL_MODIFICAR_PRECIO_CASA = 108
    const val SKILL_HACER_CARAMELOS = 109
    const val SKILL_UTILIZAR_BANCO = 110
    const val SKILL_MAGUEAR_ESPADA = 113
    const val SKILL_UTILIZAR_ZAAP = 114
    const val SKILL_MAGUEAR_HACHA = 115
    const val SKILL_MAGUEAR_MARTILLO = 116
    const val SKILL_MAGUEAR_PALA = 117
    const val SKILL_MAGUEAR_ARCO = 118
    const val SKILL_MAGUEAR_VARITA = 119
    const val SKILL_MAGUEAR_BASTON = 120
    const val SKILL_MACHACAR_RECURSOS = 121
    const val SKILL_DESGRANAR = 122
    const val SKILL_CREAR_MOCHILA = 123
    const val SKILL_PESCAR_PESCADITOS_RIO = 124
    const val SKILL_PESCAR_PESCADOS_RIO = 125
    const val SKILL_PESCAR_PECES_GORDOS_RIO = 126
    const val SKILL_PESCAR_PESCADOS_GIGANTES_RIO = 127
    const val SKILL_PESCAR_PESCADITOS_MAR = 128
    const val SKILL_PESCAR_PESCADOS_MAR = 129
    const val SKILL_PESCAR_PECES_GORDOS_MAR = 130
    const val SKILL_PESCAR_PESCADOS_GIGANTES_MAR = 131
    const val SKILL_PREPARAR_ENCIMERA = 132
    const val SKILL_VACIAR_PESCADO = 133
    const val SKILL_PREPARAR_CARNE = 134
    const val SKILL_PREPARAR_PESCADO = 135
    const val SKILL_PESCAR_PISCHI = 136
    const val SKILL_TALAR_BOMBU = 139
    const val SKILL_PESCAR_SOMBRA_EXTRAÑA = 140
    const val SKILL_TALAR_OLIVIOLETA = 141
    const val SKILL_REPARAR_DAGA = 142
    const val SKILL_REPARAR_HACHA = 143
    const val SKILL_REPARAR_MARTILLO = 144
    const val SKILL_REPARAR_ESPADA = 145
    const val SKILL_REPARAR_PALA = 146
    const val SKILL_REPARAR_BASTON = 147
    const val SKILL_REPARAR_VARITA = 148
    const val SKILL_REPARAR_ARCO = 149
    const val SKILL_JUGAR_MAQUINA_FUERZA = 150
    const val SKILL_INVOCAR_HADA = 151
    const val SKILL_PESCAR_KUAKUA = 152
    const val SKILL_REGISTRAR_BASURA = 153
    const val SKILL_TALAR_BAMBU = 154
    const val SKILL_TALAR_BAMBU_OSCURO = 155
    const val SKILL_CREAR_ESCUDO = 156
    const val SKILL_TRANSPORTE_ZAAPI = 157
    const val SKILL_TALAR_BAMBU_SAGRADO = 158
    const val SKILL_SEGAR_ARROZ = 159
    const val SKILL_COSECHAR_PANDOJA = 160
    const val SKILL_RECOLECTAR_DOLOMIA = 161
    const val SKILL_RECOLECTAR_SILICATO = 162
    const val SKILL_MAGUEAR_BOTAS = 163
    const val SKILL_MAGUEAR_CINTURON = 164
    const val SKILL_MAGUEAR_CAPA = 165
    const val SKILL_MAGUEAR_SOMBRERO = 166
    const val SKILL_MAGUEAR_MOCHILA = 167
    const val SKILL_MAGUEAR_ANILLO = 168
    const val SKILL_MAGUEAR_AMULETO = 169
    const val SKILL_CONSULTAR_LIBRO_ARTESANOS = 170
    const val SKILL_CREAR_CHAPUZA = 171
    const val SKILL_TALAR_KALIPTO = 174
    const val SKILL_ACCEDER_CERCADO = 175
    const val SKILL_COMPRAR_CERCADO = 176
    const val SKILL_VENDER_CERCADO = 177
    const val SKILL_MODIFICAR_PRECIO_CERCADO = 178
    const val SKILL_ACCIONAR_PALANCA = 179
    const val SKILL_ROMPER_OBJETO = 181
    const val SKILL_CREAR_LLAVE = 182
    const val SKILLS_LIBRO_ARTESANOS =
        (OFICIO_LEÑADOR.toString() + ";" + OFICIO_CAMPESINO + ";" + OFICIO_ALQUIMISTA
                + ";" + OFICIO_MINERO + ";" + OFICIO_PESCADOR + ";" + OFICIO_CAZADOR + ";" + OFICIO_PANADERO + ";" + OFICIO_CARNICERO
                + ";" + OFICIO_PESCADERO + ";" + OFICIO_ZAPATERO + ";" + OFICIO_JOYERO + ";" + OFICIO_SASTRE + ";"
                + OFICIO_ESCULTOR_BASTONES + ";" + OFICIO_ESCULTOR_VARITAS + ";" + OFICIO_ESCULTOR_ARCOS + ";" + OFICIO_FORJADOR_DAGAS
                + ";" + OFICIO_FORJADOR_ESPADAS + ";" + OFICIO_FORJADOR_MARTILLOS + ";" + OFICIO_FORJADOR_PALAS + ";"
                + OFICIO_FORJADOR_HACHAS + ";" + OFICIO_FORJADOR_ESCUDOS + ";" + OFICIO_ZAPATEROMAGO + ";" + OFICIO_JOYEROMAGO + ";"
                + OFICIO_SASTREMAGO + ";" + OFICIO_ESCULTORMAGO_BASTONES + ";" + OFICIO_ESCULTORMAGO_VARITAS + ";"
                + OFICIO_ESCULTORMAGO_ARCOS + ";" + OFICIO_FORJAMAGO_DAGAS + ";" + OFICIO_FORJAMAGO_ESPADAS + ";"
                + OFICIO_FORJAMAGO_MARTILLOS + ";" + OFICIO_FORJAMAGO_PALAS + ";" + OFICIO_FORJAMAGO_HACHAS + ";" + OFICIO_MANITAS)
    // REPORTES
    const val REPORTE_BUGS: Byte = 0
    const val REPORTE_SUGERENCIAS: Byte = 1
    const val REPORTE_DENUNCIAS: Byte = 2
    const val REPORTE_OGRINAS: Byte = 3
    // MOBS
    const val MOB_TIPO_SIN_CLASIFICAR = -1
    const val MOB_TIPO_INVOCACIONES_DE_CLASE = 0
    const val MOB_TIPO_JEFE_FINAL = 1
    const val MOB_TIPO_BANDIDOS = 2
    const val MOB_TIPO_WABBITS = 3
    const val MOB_TIPO_DRAGOHUEVOS = 4
    const val MOB_TIPO_BWORKS = 5
    const val MOB_TIPO_GOBLINS = 6
    const val MOB_TIPO_GELATINAS = 7
    const val MOB_TIPO_MONSTRUOS_DE_LA_NOCHE = 8
    const val MOB_TIPO_JALATOS = 9
    const val MOB_TIPO_PLANTAS_DE_LOS_CAMPOS = 10
    const val MOB_TIPO_LARVAS = 11
    const val MOB_TIPO_KWAKS = 12
    const val MOB_TIPO_CRUJIDORES = 13
    const val MOB_TIPO_CERDOS = 16
    const val MOB_TIPO_CHAFERS = 17
    const val MOB_TIPO_DOPEULS_TEMPLO = 18
    const val MOB_TIPO_PNJS = 19
    const val MOB_TIPO_KANIBOLAS_DE_LA_ISLA_DE_MOON = 20
    const val MOB_TIPO_DRAGOPAVO = 21
    const val MOB_TIPO_ABRAKNIDEO = 22
    const val MOB_TIPO_BLOPS = 23
    const val MOB_TIPO_MONSTRUOS_DE_LAS_LLANURAS_DE_CANIA = 24
    const val MOB_TIPO_MONSTRUOS_DE_LAS_LANDAS = 25
    const val MOB_TIPO_GUARDIAS = 26
    const val MOB_TIPO_MONSTRUOS_DE_LAS_CONQUISTAS_DE_TERRITORIOS = 27
    const val MOB_TIPO_MONSTRUOS_DEL_PUEBLO_DE_LOS_DOPEULS = 28
    const val MOB_TIPO_MONSTRUOS_TUTORIAL = 29
    const val MOB_TIPO_SALTEADORILLOS = 30
    const val MOB_TIPO_MONSTRUO_DE_LAS_ALCANTARILLAS = 31
    const val MOB_TIPO_SE_BUSCA = 32
    const val MOB_TIPO_PIOS = 33
    const val MOB_TIPO_MONSTRUOS_DEL_PUEBLO_DE_PANDALA = 34
    const val MOB_TIPO_MONSTRUOS_DE_PANDALA = 35
    const val MOB_TIPO_FANTASMA_DE_PANDALA = 36
    const val MOB_TIPO_ESCARAHOJA = 37
    const val MOB_TIPO_ARAKNA = 38
    const val MOB_TIPO_MILUBO = 39
    const val MOB_TIPO_TORTUGAS_DE_MOON = 40
    const val MOB_TIPO_PIRATAS_DE_MOON = 41
    const val MOB_TIPO_PLANTAS_DE_MOON = 42
    const val MOB_TIPO_MONSTRUOS_DE_MOON = 43
    const val MOB_TIPO_COCODRAILS = 44
    const val MOB_TIPO_SETAS = 45
    const val MOB_TIPO_TOFUS = 46
    const val MOB_TIPO_MOSKITOS = 47
    const val MOB_TIPO_MONSTRUOS_DE_LOS_PANTANOS = 48
    const val MOB_TIPO_ANIMALES_DEL_BOSQUE = 49
    const val MOB_TIPO_MONSTRUOS_DE_BUSQUEDA = 50
    const val MOB_TIPO_CUERBOKS = 51
    const val MOB_TIPO_GUARDIANES_DE_LOS_PUEBLOS_DE_KWAKS = 52
    const val MOB_TIPO_FANTASMAS = 53
    const val MOB_TIPO_MASCOTAS_FANTASMAS = 54
    const val MOB_TIPO_PLANTAS_DE_PANDALA = 55
    const val MOB_TIPO_KITSUS = 56
    const val MOB_TIPO_PANDAWAS = 57
    const val MOB_TIPO_FIREFUX = 59
    const val MOB_TIPO_KOALAKS = 60
    const val MOB_TIPO_MONSTRUOS_DE_LAS_CUEVAS = 61
    const val MOB_TIPO_PROTECTORES_DE_LOS_CEREALES = 62
    const val MOB_TIPO_PROTECTORES_DE_LOS_MINERALES = 63
    const val MOB_TIPO_PROTECTORES_DE_LOS_ARBOLES = 64
    const val MOB_TIPO_PROTECTORES_DE_LOS_PECES = 65
    const val MOB_TIPO_PROTECTORES_DE_LAS_PLANTAS = 66
    const val MOB_TIPO_MINOS = 67
    const val MOB_TIPO_MONSTRUOS_DE_NAWIDAD = 68
    const val MOB_TIPO_MONSTRUOS_DE_LAS_PLAYAS = 69
    const val MOB_TIPO_MONSTRUOS_DE_LA_ZONA_DE_NOVATOS = 70
    const val MOB_TIPO_MONSTRUOS_DE_LAS_LLANURAS_HERBOSAS = 71
    const val MOB_TIPO_MONSTRUOS_DE_LA_PLAYA_DE_CORAL = 72
    const val MOB_TIPO_MONSTRUOS_DE_LA_TURBERA_SIN_FONDO = 73
    const val MOB_TIPO_MONSTRUOS_DE_LA_OSCURA_SELVA = 74
    const val MOB_TIPO_MONSTRUOS_DEL_ARBOL_HAKAM = 75
    const val MOB_TIPO_MONSTRUOS_DEL_ARCA_DE_OTOMAI = 76
    const val MOB_TIPO_MONSTRUOS_DE_LA_CANOPEA_DE_LAS_NIEBLAS = 77
    const val MOB_TIPO_LOS_ARCHIMONSTRUOS = 78
    const val MOB_TIPO_MONSTRUOS_DE_LOS_CAMPOS_DE_HIELO = 79
    const val MOB_TIPO_MONSTRUOS_DEL_BURGO = 81
    const val MOB_TIPO_MONSTRUOS_DEL_BOSQUE_DE_LOS_PINOS_PERDIDOS = 82
    const val MOB_TIPO_MONSTRUOS_DEL_LAGO_HELADO = 83
    const val MOB_TIPO_MONSTRUOS_DEL_MONTE_TORRIDO = 84
    const val MOB_TIPO_MONSTRUOS_DE_LAS_LAGRIMAS_DE_URONIGRIDO = 85
    const val MOB_TIPO_MONSTRUOS_DE_LA_CUNA_DE_ALMA = 86
    const val MOB_TIPO_MONSTRUOS_DE_LOS_COL = 87
    const val MOB_TIPO_MONSTRUOS_DE_LA_GRIETA_BU = 88
    const val MOB_TIPO_MONSTRUOS_DEL_BOSQUE_PETRIFICADO = 89
    const val MOB_TIPO_MONSTRUOS_SE_BUSCA_DE_FRIGOST = 90
    const val MOB_TIPO_MONSTRUOS_DE_MISION_DE_FRIGOST = 91
    // MOB SUPER TIPO
    const val MOB_SUPER_TIPO_CRIATURAS_DIVERSAS = 1
    const val MOB_SUPER_TIPO_CRIATURAS_DE_LOS_CAMPOS = 2
    const val MOB_SUPER_TIPO_CRIATURAS_DE_LA_MONTAÑA = 3
    const val MOB_SUPER_TIPO_CRIATURAS_DEL_BOSQUE = 4
    const val MOB_SUPER_TIPO_CRIATURAS_DE_LAS_LLANURAS = 5
    const val MOB_SUPER_TIPO_CRIATURAS_DE_LAS_LANDAS = 6
    const val MOB_SUPER_TIPO_CRIATURAS_DE_LA_ISLA_DE_MOON = 7
    const val MOB_SUPER_TIPO_CRIATURAS_DE_LA_ISLA_DE_LOS_WABBITS = 8
    const val MOB_SUPER_TIPO_CRIATURAS_DE_PANDALA = 9
    const val MOB_SUPER_TIPO_CRIATURAS_HUMANOIDES = 10
    const val MOB_SUPER_TIPO_CRIATURAS_DE_LA_NOCHE = 11
    const val MOB_SUPER_TIPO_CRIATURAS_DE_LOS_PANTANOS = 12
    const val MOB_SUPER_TIPO_CRIATURAS_DE_LAS_CIUDADES = 13
    const val MOB_SUPER_TIPO_CRIATURAS_DEL_PUEBLO_DE_LOS_GANADEROS = 14
    const val MOB_SUPER_TIPO_PROTECTORES_DE_LOS_RECURSOS = 15
    const val MOB_SUPER_TIPO_CRIATURAS_DE_LA_ISLA_DE_MINOTAURORO = 16
    const val MOB_SUPER_TIPO_CRIATURAS_DE_LAS_PLAYAS = 17
    const val MOB_SUPER_TIPO_CRIATURAS_DE_LA_ZONA_DE_LOS_NOVATOS = 18
    const val MOB_SUPER_TIPO_CRIATURAS_DE_LA_ISLA_DE_OTOMAI = 19
    const val MOB_SUPER_TIPO_CRIATURAS_ARCHIMONSTRUO = 20
    const val MOB_SUPER_TIPO_NULL = 21
    // ARCHIMOBS
// public static final int[] ARCHIMOBS = {31, 34, 36, 40, 41, 43, 44, 46, 47, 254, 48, 474, 52,
// 255, 54, 55, 56, 57, 59,
// 61, 62, 63, 64, 65, 68, 72, 74, 75, 76, 78, 79, 82, 87, 88, 89, 90, 91, 93, 94, 95, 96, 97, 99,
// 102, 106, 108, 110,
// 111, 112, 118, 119, 123, 124, 126, 127, 134, 148, 149, 150, 153, 154, 466, 155, 525, 157, 159,
// 160, 161, 162, 163,
// 164, 165, 166, 167, 168, 169, 170, 171, 200, 178, 179, 194, 198, 207, 208, 209, 211, 212, 213,
// 214, 215, 216, 217,
// 218, 220, 221, 222, 223, 228, 229, 231, 233, 491, 493, 492, 489, 490, 236, 240, 241, 249, 253,
// 256, 259, 261, 263,
// 273, 274, 275, 276, 277, 278, 279, 280, 281, 287, 288, 522, 290, 291, 292, 293, 297, 298, 299,
// 397, 300, 301, 343,
// 344, 370, 371, 378, 379, 932, 380, 442, 449, 475, 465, 447, 467, 473, 483, 495, 496, 498, 515,
// 517, 566, 518, 519,
// 523, 524, 527, 528, 529, 530, 531, 532, 534, 535, 537, 546, 547, 548, 549, 583, 584, 585, 586,
// 587, 588, 589, 590,
// 594, 595, 596, 598, 597, 603, 650, 651, 654, 655, 653, 652, 668, 744, 745, 746, 747, 748, 749,
// 751, 752, 753, 754,
// 755, 756, 758, 759, 760, 761, 763, 783, 784, 785, 786, 829, 830, 834, 835, 836, 886, 885, 848,
// 884, 853, 855, 878,
// 879, 858, 905, 862, 876, 920, 921, 922, 923, 924, 926, 1019, 1020, 1022, 1025, 1026, 1029,
// 1041, 1043, 1044, 1046,
// 1047, 1048, 1049, 1052, 1053, 1054, 1055, 1056, 1057, 1058, 1059, 1060, 1061, 1062, 1063, 1064,
// 1065, 1066, 1067,
// 1068, 1069, 1070, 1073, 1074, 1075, 1076, 1077, 1096, 1153, 1154, 1155, 1156, 1157, 1158};
    val TRABAJOS_Y_DROPS = arrayOf(
        intArrayOf(SKILL_PELAR_PATATAS),
        intArrayOf(SKILL_UTILIZAR_BANCO),
        intArrayOf(SKILL_MACHACAR_RECURSOS),
        intArrayOf(
            SKILL_ROMPER_OBJETO,
            OBJETO_TIPO_ANILLO,
            OBJETO_TIPO_AMULETO,
            OBJETO_TIPO_CAPA,
            OBJETO_TIPO_MOCHILA,
            OBJETO_TIPO_CINTURON,
            OBJETO_TIPO_BOTAS,
            OBJETO_TIPO_SOMBRERO,
            OBJETO_TIPO_DAGAS,
            OBJETO_TIPO_MARTILLO,
            OBJETO_TIPO_VARITA,
            OBJETO_TIPO_BASTON,
            OBJETO_TIPO_HACHA,
            OBJETO_TIPO_ESPADA,
            OBJETO_TIPO_ARCO,
            OBJETO_TIPO_PALA,
            OBJETO_TIPO_HERRAMIENTA
        ),
        intArrayOf(101),
        intArrayOf(SKILL_TALAR_FRESNO, 303),
        intArrayOf(SKILL_TALAR_CASTAÑO, 473),
        intArrayOf(SKILL_TALAR_NOGAL, 476),
        intArrayOf(SKILL_TALAR_ROBLE, 460),
        intArrayOf(SKILL_TALAR_OLIVIOLETA, 2357),
        intArrayOf(SKILL_TALAR_BOMBU, 2358),
        intArrayOf(SKILL_TALAR_ARCE, 471),
        intArrayOf(SKILL_TALAR_BAMBU, 7013),
        intArrayOf(SKILL_TALAR_TEJO, 461),
        intArrayOf(SKILL_TALAR_CEREZO_SILVESTRE, 474),
        intArrayOf(SKILL_TALAR_EBANO, 449),
        intArrayOf(SKILL_TALAR_KALIPTO, 7925),
        intArrayOf(SKILL_TALAR_BAMBU_OSCURO, 7016),
        intArrayOf(SKILL_TALAR_CARPE, 472),
        intArrayOf(SKILL_TALAR_OLMO, 470),
        intArrayOf(SKILL_TALAR_BAMBU_SAGRADO, 7014),
        intArrayOf(48),
        intArrayOf(32),
        intArrayOf(SKILL_RECOLECTAR_HIERRO, 312),
        intArrayOf(SKILL_RECOLECTAR_COBRE, 441),
        intArrayOf(SKILL_RECOLECTAR_BRONCE, 442),
        intArrayOf(SKILL_RECOLECTAR_KOBALTO, 443),
        intArrayOf(SKILL_RECOLECTAR_MANGANESO, 445),
        intArrayOf(SKILL_RECOLECTAR_SILICATO, 7032),
        intArrayOf(SKILL_RECOLECTAR_ESTAÑO, 444),
        intArrayOf(SKILL_RECOLECTAR_PLATA, 350),
        intArrayOf(SKILL_RECOLECTAR_BAUXITA, 446),
        intArrayOf(SKILL_RECOLECTAR_ORO, 313),
        intArrayOf(SKILL_RECOLECTAR_DOLOMIA, 7033),
        intArrayOf(133),
        intArrayOf(SKILL_PESCAR_PESCADITOS_MAR, 598, 1786),
        intArrayOf(SKILL_PESCAR_PESCADITOS_MAR, 1757, 1759),
        intArrayOf(SKILL_PESCAR_PESCADITOS_MAR, 1750, 1754),
        intArrayOf(SKILL_PESCAR_PESCADITOS_RIO, 603, 1762),
        intArrayOf(SKILL_PESCAR_PESCADITOS_RIO, 1782, 1790),
        intArrayOf(SKILL_PESCAR_PESCADITOS_RIO, 1844, 607),
        intArrayOf(SKILL_PESCAR_PESCADITOS_RIO, 1844, 1846),
        intArrayOf(SKILL_PESCAR_PISCHI, 2187),
        intArrayOf(SKILL_PESCAR_PESCADOS_RIO, 1847, 1849),
        intArrayOf(SKILL_PESCAR_PESCADOS_RIO, 1794, 1796),
        intArrayOf(SKILL_PESCAR_SOMBRA_EXTRAÑA, 1799, 1759),
        intArrayOf(SKILL_PESCAR_PESCADOS_MAR, 600, 1799),
        intArrayOf(SKILL_PESCAR_PESCADOS_MAR, 1805, 1807),
        intArrayOf(SKILL_PESCAR_PECES_GORDOS_RIO, 1779, 1792),
        intArrayOf(SKILL_PESCAR_PECES_GORDOS_MAR, 1784, 1788),
        intArrayOf(SKILL_PESCAR_PESCADOS_GIGANTES_RIO, 1801, 1803),
        intArrayOf(SKILL_PESCAR_PESCADOS_GIGANTES_MAR, 602, 1853),
        intArrayOf(23),
        intArrayOf(SKILL_COSECHAR_LINO, 421),
        intArrayOf(SKILL_COSECHAR_CAÑAMO, 428),
        intArrayOf(SKILL_COSECHAR_TREBOL, 395),
        intArrayOf(SKILL_COSECHAR_MENTA_SALVAJE, 380),
        intArrayOf(SKILL_COSECHAR_ORQUIDEA, 593),
        intArrayOf(SKILL_COSECHAR_EDELWEISS, 594),
        intArrayOf(SKILL_COSECHAR_PANDOJA, 7059),
        intArrayOf(122),
        intArrayOf(47),
        intArrayOf(SKILL_SEGAR_TRIGO, 289, 2018),
        intArrayOf(SKILL_SEGAR_CEBADA, 400, 2032),
        intArrayOf(SKILL_SEGAR_AVENA, 533, 2036),
        intArrayOf(SKILL_SEGAR_LUPULO, 401, 2021),
        intArrayOf(SKILL_SEGAR_LINO, 423, 2026),
        intArrayOf(SKILL_SEGAR_CENTENO, 532, 2029),
        intArrayOf(SKILL_SEGAR_ARROZ, 7018),
        intArrayOf(SKILL_SEGAR_MALTA, 405),
        intArrayOf(SKILL_SEGAR_CAÑAMO, 425, 2035),
        intArrayOf(109),
        intArrayOf(27),
        intArrayOf(135),
        intArrayOf(132),
        intArrayOf(134),
        intArrayOf(64),
        intArrayOf(123),
        intArrayOf(63),
        intArrayOf(11),
        intArrayOf(12),
        intArrayOf(13),
        intArrayOf(14),
        intArrayOf(145),
        intArrayOf(20),
        intArrayOf(144),
        intArrayOf(19),
        intArrayOf(142),
        intArrayOf(18),
        intArrayOf(146),
        intArrayOf(21),
        intArrayOf(65),
        intArrayOf(143),
        intArrayOf(15),
        intArrayOf(16),
        intArrayOf(17),
        intArrayOf(147),
        intArrayOf(148),
        intArrayOf(149),
        intArrayOf(
            SKILL_MAGUEAR_HACHA,
            OBJETO_TIPO_HACHA,
            OBJETO_TIPO_RUNA_FORJAMAGIA,
            OBJETO_TIPO_POCION_FORJAMAGIA
        ),
        intArrayOf(
            SKILL_MAGUEAR_DAGA,
            OBJETO_TIPO_DAGAS,
            OBJETO_TIPO_RUNA_FORJAMAGIA,
            OBJETO_TIPO_POCION_FORJAMAGIA
        ),
        intArrayOf(
            116,
            OBJETO_TIPO_MARTILLO,
            OBJETO_TIPO_RUNA_FORJAMAGIA,
            OBJETO_TIPO_POCION_FORJAMAGIA
        ),
        intArrayOf(
            113,
            OBJETO_TIPO_ESPADA,
            OBJETO_TIPO_RUNA_FORJAMAGIA,
            OBJETO_TIPO_POCION_FORJAMAGIA
        ),
        intArrayOf(
            117,
            OBJETO_TIPO_PALA,
            OBJETO_TIPO_RUNA_FORJAMAGIA,
            OBJETO_TIPO_POCION_FORJAMAGIA
        ),
        intArrayOf(
            120,
            OBJETO_TIPO_BASTON,
            OBJETO_TIPO_RUNA_FORJAMAGIA,
            OBJETO_TIPO_POCION_FORJAMAGIA
        ),
        intArrayOf(
            119,
            OBJETO_TIPO_VARITA,
            OBJETO_TIPO_RUNA_FORJAMAGIA,
            OBJETO_TIPO_POCION_FORJAMAGIA
        ),
        intArrayOf(
            118,
            OBJETO_TIPO_ARCO,
            OBJETO_TIPO_RUNA_FORJAMAGIA,
            OBJETO_TIPO_POCION_FORJAMAGIA
        ),
        intArrayOf(165, OBJETO_TIPO_CAPA, OBJETO_TIPO_RUNA_FORJAMAGIA),
        intArrayOf(166, OBJETO_TIPO_SOMBRERO, OBJETO_TIPO_RUNA_FORJAMAGIA),
        intArrayOf(167, OBJETO_TIPO_MOCHILA, OBJETO_TIPO_RUNA_FORJAMAGIA),
        intArrayOf(163, OBJETO_TIPO_BOTAS, OBJETO_TIPO_RUNA_FORJAMAGIA),
        intArrayOf(164, OBJETO_TIPO_CINTURON, OBJETO_TIPO_RUNA_FORJAMAGIA),
        intArrayOf(169, OBJETO_TIPO_AMULETO, OBJETO_TIPO_RUNA_FORJAMAGIA),
        intArrayOf(168, OBJETO_TIPO_ANILLO, OBJETO_TIPO_RUNA_FORJAMAGIA),
        intArrayOf(171),
        intArrayOf(182),
        intArrayOf(SKILL_CREAR_ESCUDO)
    )

    // EM[1] = {n: "Sentarse", s: "sit"};
// EM[2] = {n: "Hacer una señal con la mano", s: "bye"};
// EM[3] = {n: "Aplaudir", s: "appl"};
// EM[4] = {n: "Enfadarse", s: "mad"};
// EM[5] = {n: "Mostrar su miedo", s: "fear"};
// EM[6] = {n: "Mostrar su arma", s: "weap"};
// EM[7] = {n: "Tocar la flauta", s: "pipo"};
// EM[8] = {n: "Tirarse un pedo", s: "oups"};
// EM[9] = {n: "Saludar", s: "hi"};
// EM[10] = {n: "Dar un beso", s: "kiss"};
// EM[11] = {n: "Piedra", s: "pfc1"};
// EM[12] = {n: "Hoja", s: "pfc2"};
// EM[13] = {n: "Tijeras", s: "pfc3"};
// EM[14] = {n: "Cruzarse de brazos", s: "cross"};
// EM[15] = {n: "Señalar con el dedo", s: "point"};
// EM[16] = {n: ". . . . .", s: "crow"};
// EM[19] = {n: "Acostarse", s: "rest"};
// EM[21] = {n: "Campeón", s: "champ"};
// EM[22] = {n: "Aura de poder", s: "aura"};
// EM[23] = {n: "Aura vampírica", s: "bat"};
// Accion de Oficio {trabajoID, objeto recolectado,obj especial}
    fun getMobSinHalloween(id: Int): Int {
        return when (id) {
            793 -> 101
            794 -> 98
            else -> id
        }
    }

    @JvmStatic
    fun getOficioPrimarioDeMago(oficioID: Int): Int {
        when (oficioID) {
            OFICIO_FORJAMAGO_ESPADAS -> return OFICIO_FORJADOR_ESPADAS
            OFICIO_ESCULTORMAGO_ARCOS -> return OFICIO_ESCULTOR_ARCOS
            OFICIO_FORJAMAGO_MARTILLOS -> return OFICIO_FORJADOR_MARTILLOS
            OFICIO_ZAPATEROMAGO -> return OFICIO_ZAPATERO
            OFICIO_JOYEROMAGO -> return OFICIO_JOYERO
            OFICIO_FORJAMAGO_DAGAS -> return OFICIO_FORJADOR_DAGAS
            OFICIO_ESCULTORMAGO_BASTONES -> return OFICIO_ESCULTOR_BASTONES
            OFICIO_ESCULTORMAGO_VARITAS -> return OFICIO_ESCULTOR_VARITAS
            OFICIO_FORJAMAGO_PALAS -> return OFICIO_FORJADOR_PALAS
            OFICIO_SASTREMAGO -> return OFICIO_SASTRE
            OFICIO_FORJAMAGO_HACHAS -> return OFICIO_FORJADOR_HACHAS
        }
        return -1
    }

    @JvmStatic
    fun esPosicionVisual(pos: Byte): Boolean {
        for (p in POSICIONES_EQUIPAMIENTO_VISUAL) {
            if (pos == p) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun esPosicionEquipamiento(pos: Byte): Boolean {
        for (p in POSICIONES_EQUIPAMIENTO) {
            if (pos == p) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun esPosicionObjeto(pos: Byte): Boolean {
        for (p in POSICIONES_TODOS) {
            if (pos == p) {
                return true
            }
        }
        return false
    }

    fun esEfectoSetClase(id: Int): Boolean {
        for (i in BUFF_SET_CLASE) {
            if (i == id) return true
        }
        return false
    }

    fun getStatsEncarnacion(idModelo: Int, nivel: Int, stats: Stats): Stats {
        stats.clear() // FIXME las encarnaciones
        when (idModelo) {
            9544 -> {
                stats.fijarStatID(STAT_MAS_VITALIDAD, nivel)
                stats.fijarStatID(STAT_MAS_INTELIGENCIA, nivel / 2)
                stats.fijarStatID(STAT_MAS_FUERZA, nivel / 2)
                stats.fijarStatID(STAT_MAS_SUERTE, nivel / 2)
                stats.fijarStatID(STAT_MAS_AGILIDAD, nivel / 2)
            }
            9545 -> {
                stats.fijarStatID(STAT_MAS_VITALIDAD, nivel)
                stats.fijarStatID(STAT_MAS_INTELIGENCIA, nivel)
            }
            9546 -> {
                stats.fijarStatID(STAT_MAS_VITALIDAD, nivel)
                stats.fijarStatID(STAT_MAS_FUERZA, nivel)
            }
            9547 -> {
                stats.fijarStatID(STAT_MAS_VITALIDAD, nivel)
                stats.fijarStatID(STAT_MAS_SUERTE, nivel)
            }
            9548 -> {
                stats.fijarStatID(STAT_MAS_VITALIDAD, nivel)
                stats.fijarStatID(STAT_MAS_AGILIDAD, nivel)
            }
            10125 -> {
                stats.fijarStatID(STAT_MAS_ALCANCE, nivel / 25)
                stats.fijarStatID(STAT_MAS_INTELIGENCIA, nivel / 2)
                stats.fijarStatID(STAT_MAS_FUERZA, nivel / 2)
                stats.fijarStatID(STAT_MAS_SUERTE, nivel / 2)
                stats.fijarStatID(STAT_MAS_AGILIDAD, nivel / 2)
            }
            10126 -> stats.fijarStatID(STAT_MAS_GOLPES_CRITICOS, nivel / 10)
            10127 -> {
                stats.fijarStatID(STAT_MAS_RES_FIJA_AGUA, nivel / 10)
                stats.fijarStatID(STAT_MAS_RES_FIJA_TIERRA, nivel / 10)
                stats.fijarStatID(STAT_MAS_RES_FIJA_FUEGO, nivel / 10)
                stats.fijarStatID(STAT_MAS_RES_FIJA_AIRE, nivel / 10)
                stats.fijarStatID(STAT_MAS_RES_PORC_AGUA, nivel / 10)
                stats.fijarStatID(STAT_MAS_RES_PORC_TIERRA, nivel / 10)
                stats.fijarStatID(STAT_MAS_RES_PORC_FUEGO, nivel / 10)
                stats.fijarStatID(STAT_MAS_RES_PORC_AIRE, nivel / 10)
            }
            10133 -> {
                stats.fijarStatID(STAT_MAS_INTELIGENCIA, nivel)
                stats.fijarStatID(STAT_MAS_FUERZA, nivel)
                stats.fijarStatID(STAT_MAS_SUERTE, nivel)
                stats.fijarStatID(STAT_MAS_AGILIDAD, nivel)
            }
        }
        return stats
    }

    // public static final HashMap<Integer, StatHechizo> hechizosEncarnacion(final int clase, final
// int nivel) {
// final HashMap<Integer, StatHechizo> hechizos = new HashMap<Integer, StatHechizo>();
// switch (clase) {
// case CLASE_ATORMENTADOR_NUBE :
// hechizos.put(1291, Mundo.getHechizo(1291).getStatsPorNivel(nivel));
// hechizos.put(1296, Mundo.getHechizo(1296).getStatsPorNivel(nivel));
// hechizos.put(1289, Mundo.getHechizo(1289).getStatsPorNivel(nivel));
// hechizos.put(1285, Mundo.getHechizo(1285).getStatsPorNivel(nivel));
// hechizos.put(1290, Mundo.getHechizo(1290).getStatsPorNivel(nivel));
// break;
// case CLASE_ATORMENTADOR_GOTA :
// hechizos.put(1299, Mundo.getHechizo(1299).getStatsPorNivel(nivel));
// hechizos.put(1288, Mundo.getHechizo(1288).getStatsPorNivel(nivel));
// hechizos.put(1297, Mundo.getHechizo(1297).getStatsPorNivel(nivel));
// hechizos.put(1285, Mundo.getHechizo(1285).getStatsPorNivel(nivel));
// hechizos.put(1298, Mundo.getHechizo(1298).getStatsPorNivel(nivel));
// break;
// case CLASE_ATORMENTADOR_TINIEBLAS :
// hechizos.put(1300, Mundo.getHechizo(1300).getStatsPorNivel(nivel));
// hechizos.put(1301, Mundo.getHechizo(1301).getStatsPorNivel(nivel));
// hechizos.put(1303, Mundo.getHechizo(1303).getStatsPorNivel(nivel));
// hechizos.put(1285, Mundo.getHechizo(1285).getStatsPorNivel(nivel));
// hechizos.put(1302, Mundo.getHechizo(1302).getStatsPorNivel(nivel));
// break;
// case CLASE_ATORMENTADOR_LLAMAS :
// hechizos.put(1292, Mundo.getHechizo(1292).getStatsPorNivel(nivel));
// hechizos.put(1293, Mundo.getHechizo(1293).getStatsPorNivel(nivel));
// hechizos.put(1294, Mundo.getHechizo(1294).getStatsPorNivel(nivel));
// hechizos.put(1285, Mundo.getHechizo(1285).getStatsPorNivel(nivel));
// hechizos.put(1295, Mundo.getHechizo(1295).getStatsPorNivel(nivel));
// break;
// case CLASE_ATORMENTADOR_HOJA :
// hechizos.put(1283, Mundo.getHechizo(1283).getStatsPorNivel(nivel));
// hechizos.put(1284, Mundo.getHechizo(1284).getStatsPorNivel(nivel));
// hechizos.put(1286, Mundo.getHechizo(1286).getStatsPorNivel(nivel));
// hechizos.put(1285, Mundo.getHechizo(1285).getStatsPorNivel(nivel));
// hechizos.put(1287, Mundo.getHechizo(1287).getStatsPorNivel(nivel));
// break;
// case CLASE_BANDIDO_HECHIZERO :
// hechizos.put(1601, Mundo.getHechizo(1601).getStatsPorNivel(nivel));
// hechizos.put(1602, Mundo.getHechizo(1602).getStatsPorNivel(nivel));
// hechizos.put(1603, Mundo.getHechizo(1603).getStatsPorNivel(nivel));
// hechizos.put(1604, Mundo.getHechizo(1604).getStatsPorNivel(nivel));
// hechizos.put(1605, Mundo.getHechizo(1605).getStatsPorNivel(nivel));
// hechizos.put(1606, Mundo.getHechizo(1606).getStatsPorNivel(nivel));
// hechizos.put(1607, Mundo.getHechizo(1607).getStatsPorNivel(nivel));
// hechizos.put(1608, Mundo.getHechizo(1608).getStatsPorNivel(nivel));
// hechizos.put(1609, Mundo.getHechizo(1609).getStatsPorNivel(nivel));
// hechizos.put(1610, Mundo.getHechizo(1610).getStatsPorNivel(nivel));
// hechizos.put(1611, Mundo.getHechizo(1611).getStatsPorNivel(nivel));
// hechizos.put(1612, Mundo.getHechizo(1612).getStatsPorNivel(nivel));
// hechizos.put(1613, Mundo.getHechizo(1613).getStatsPorNivel(nivel));
// hechizos.put(1614, Mundo.getHechizo(1614).getStatsPorNivel(nivel));
// hechizos.put(1615, Mundo.getHechizo(1615).getStatsPorNivel(nivel));
// hechizos.put(1616, Mundo.getHechizo(1616).getStatsPorNivel(nivel));
// hechizos.put(1617, Mundo.getHechizo(1617).getStatsPorNivel(nivel));
// hechizos.put(1618, Mundo.getHechizo(1618).getStatsPorNivel(nivel));
// hechizos.put(1619, Mundo.getHechizo(1619).getStatsPorNivel(nivel));
// hechizos.put(1620, Mundo.getHechizo(1620).getStatsPorNivel(nivel));
// break;
// case CLASE_BANDIDO_ARQUERO :
// hechizos.put(1561, Mundo.getHechizo(1561).getStatsPorNivel(nivel));
// hechizos.put(1562, Mundo.getHechizo(1562).getStatsPorNivel(nivel));
// hechizos.put(1563, Mundo.getHechizo(1563).getStatsPorNivel(nivel));
// hechizos.put(1564, Mundo.getHechizo(1564).getStatsPorNivel(nivel));
// hechizos.put(1565, Mundo.getHechizo(1565).getStatsPorNivel(nivel));
// hechizos.put(1566, Mundo.getHechizo(1566).getStatsPorNivel(nivel));
// hechizos.put(1567, Mundo.getHechizo(1567).getStatsPorNivel(nivel));
// hechizos.put(1568, Mundo.getHechizo(1568).getStatsPorNivel(nivel));
// hechizos.put(1569, Mundo.getHechizo(1569).getStatsPorNivel(nivel));
// hechizos.put(1570, Mundo.getHechizo(1570).getStatsPorNivel(nivel));
// hechizos.put(1571, Mundo.getHechizo(1571).getStatsPorNivel(nivel));
// hechizos.put(1572, Mundo.getHechizo(1572).getStatsPorNivel(nivel));
// hechizos.put(1573, Mundo.getHechizo(1573).getStatsPorNivel(nivel));
// hechizos.put(1574, Mundo.getHechizo(1574).getStatsPorNivel(nivel));
// hechizos.put(1575, Mundo.getHechizo(1575).getStatsPorNivel(nivel));
// hechizos.put(1576, Mundo.getHechizo(1576).getStatsPorNivel(nivel));
// hechizos.put(1577, Mundo.getHechizo(1577).getStatsPorNivel(nivel));
// hechizos.put(1578, Mundo.getHechizo(1578).getStatsPorNivel(nivel));
// hechizos.put(1579, Mundo.getHechizo(1579).getStatsPorNivel(nivel));
// hechizos.put(1580, Mundo.getHechizo(1580).getStatsPorNivel(nivel));
// break;
// case CLASE_BANDIDO_PENDENCIERO :
// hechizos.put(1581, Mundo.getHechizo(1581).getStatsPorNivel(nivel));
// hechizos.put(1582, Mundo.getHechizo(1582).getStatsPorNivel(nivel));
// hechizos.put(1583, Mundo.getHechizo(1583).getStatsPorNivel(nivel));
// hechizos.put(1584, Mundo.getHechizo(1584).getStatsPorNivel(nivel));
// hechizos.put(1585, Mundo.getHechizo(1585).getStatsPorNivel(nivel));
// hechizos.put(1586, Mundo.getHechizo(1586).getStatsPorNivel(nivel));
// hechizos.put(1587, Mundo.getHechizo(1587).getStatsPorNivel(nivel));
// hechizos.put(1588, Mundo.getHechizo(1588).getStatsPorNivel(nivel));
// hechizos.put(1589, Mundo.getHechizo(1589).getStatsPorNivel(nivel));
// hechizos.put(1590, Mundo.getHechizo(1590).getStatsPorNivel(nivel));
// hechizos.put(1591, Mundo.getHechizo(1591).getStatsPorNivel(nivel));
// hechizos.put(1592, Mundo.getHechizo(1592).getStatsPorNivel(nivel));
// hechizos.put(1593, Mundo.getHechizo(1593).getStatsPorNivel(nivel));
// hechizos.put(1594, Mundo.getHechizo(1594).getStatsPorNivel(nivel));
// hechizos.put(1595, Mundo.getHechizo(1595).getStatsPorNivel(nivel));
// hechizos.put(1596, Mundo.getHechizo(1596).getStatsPorNivel(nivel));
// hechizos.put(1597, Mundo.getHechizo(1597).getStatsPorNivel(nivel));
// hechizos.put(1598, Mundo.getHechizo(1598).getStatsPorNivel(nivel));
// hechizos.put(1599, Mundo.getHechizo(1599).getStatsPorNivel(nivel));
// hechizos.put(1600, Mundo.getHechizo(1600).getStatsPorNivel(nivel));
// break;
// case CLASE_BANDIDO_ESPADACHIN :
// hechizos.put(1541, Mundo.getHechizo(1541).getStatsPorNivel(nivel));
// hechizos.put(1542, Mundo.getHechizo(1542).getStatsPorNivel(nivel));
// hechizos.put(1543, Mundo.getHechizo(1543).getStatsPorNivel(nivel));
// hechizos.put(1544, Mundo.getHechizo(1544).getStatsPorNivel(nivel));
// hechizos.put(1545, Mundo.getHechizo(1545).getStatsPorNivel(nivel));
// hechizos.put(1546, Mundo.getHechizo(1546).getStatsPorNivel(nivel));
// hechizos.put(1547, Mundo.getHechizo(1547).getStatsPorNivel(nivel));
// hechizos.put(1548, Mundo.getHechizo(1548).getStatsPorNivel(nivel));
// hechizos.put(1549, Mundo.getHechizo(1549).getStatsPorNivel(nivel));
// hechizos.put(1550, Mundo.getHechizo(1550).getStatsPorNivel(nivel));
// hechizos.put(1551, Mundo.getHechizo(1551).getStatsPorNivel(nivel));
// hechizos.put(1552, Mundo.getHechizo(1552).getStatsPorNivel(nivel));
// hechizos.put(1553, Mundo.getHechizo(1553).getStatsPorNivel(nivel));
// hechizos.put(1554, Mundo.getHechizo(1554).getStatsPorNivel(nivel));
// hechizos.put(1555, Mundo.getHechizo(1555).getStatsPorNivel(nivel));
// hechizos.put(1556, Mundo.getHechizo(1556).getStatsPorNivel(nivel));
// hechizos.put(1557, Mundo.getHechizo(1557).getStatsPorNivel(nivel));
// hechizos.put(1558, Mundo.getHechizo(1558).getStatsPorNivel(nivel));
// hechizos.put(1559, Mundo.getHechizo(1559).getStatsPorNivel(nivel));
// hechizos.put(1560, Mundo.getHechizo(1560).getStatsPorNivel(nivel));
// break;
// }
// return hechizos;
// }
//
// public static final byte getClasePorObjMod(final int objModelo) {
// switch (objModelo) {
// case 9544 :
// return CLASE_ATORMENTADOR_TINIEBLAS;
// case 9545 :
// return CLASE_ATORMENTADOR_LLAMAS;
// case 9546 :
// return CLASE_ATORMENTADOR_HOJA;
// case 9547 :
// return CLASE_ATORMENTADOR_GOTA;
// case 9548 :
// return CLASE_ATORMENTADOR_NUBE;
// case 10125 :
// return CLASE_BANDIDO_ARQUERO;
// case 10126 :
// return CLASE_BANDIDO_ESPADACHIN;
// case 10127 :
// return CLASE_BANDIDO_PENDENCIERO;
// case 10133 :
// return CLASE_BANDIDO_HECHIZERO;
// }
// return -1;
// }
//
// public static final short getGFXPorEncarnacion(final int clase) {
// switch (clase) {
// case CLASE_ATORMENTADOR_NUBE :
// return 1701;
// case CLASE_ATORMENTADOR_GOTA :
// return 1702;
// case CLASE_ATORMENTADOR_TINIEBLAS :
// return 1700;
// case CLASE_ATORMENTADOR_LLAMAS :
// return 1704;
// case CLASE_ATORMENTADOR_HOJA :
// return 1703;
// case CLASE_BANDIDO_HECHIZERO :
// return 8034;
// case CLASE_BANDIDO_ARQUERO :
// return 8032;
// case CLASE_BANDIDO_PENDENCIERO :
// return 8033;
// case CLASE_BANDIDO_ESPADACHIN :
// return 8035;
// }
// return 9999;
// }
    @JvmStatic
    fun esEfectoHechizo(stat: Int): Boolean {
        for (i in BUFF_ARMAS) {
            if (i == stat) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun esStatTexto(stat: Int): Boolean {
        for (i in STAT_TEXTO) {
            if (i == stat) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun esStatRepetible(stat: Int): Boolean {
        for (i in STAT_REPETIBLE) {
            if (i == stat) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun esStatHechizo(stat: Int): Boolean { // 281, 282, 283, 284, 285,
// 286, 287, 288, 289, 290, 291, 292
        return stat >= 281 && stat <= 294
    }

    fun getNivelObjevivo(xp: Int): Int {
        var nivel = 0
        for (i in NIVELES_OBJEVIVOS) {
            if (i <= xp) {
                nivel++
            }
        }
        return nivel
    }

    @JvmStatic
    fun convertirStringArray(str: String): ArrayList<String> {
        val s = str.toCharArray()
        var corchete = 0
        val fini = ArrayList<String>()
        var temp = StringBuilder()
        for (a in s) {
            if (a == ' ') {
                continue
            }
            if (corchete == 1 && a == ',') {
                fini.add(temp.toString())
                temp = StringBuilder()
                continue
            }
            if (a == '[') {
                corchete += 1
                if (corchete == 1) {
                    continue
                }
            } else if (a == ']') {
                corchete -= 1
                if (corchete == 0) {
                    fini.add(temp.toString())
                    temp = StringBuilder()
                    continue
                }
            }
            temp.append(a)
        }
        return fini
    }

    fun esAccionParaMostrar(accionID: Int): Boolean { // mas q todo para GA de pelea
        when (accionID) {
            0, 1, 100, 101, 102, 103, 104, 105, 106, 107, 108, 11, 110, 111, 112, 114, 115, 116, 117, 118, 119, 120, 122, 123, 124, 125, 126, 127, 128, 129, 130, 132, 138, 140, 142, 145, 147, 149, 150, 151, 152, 153, 154, 155, 156, 157, 160, 161, 162, 163, 164, 165, 166, 168, 169, 180, 181, 182, 185, 2, 200, 208, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 228, 300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 4, 5, 50, 501, 51, 52, 606, 607, 608, 609, 610, 611, 617, 618, 619, 78, 780, 900, 901, 902, 903, 905, 906, 909, 940, 950, 998, 999 -> return false
        }
        return true
    }

    @JvmStatic
    fun getTiempoActualEscala(escala: Long): Long {
        return System.currentTimeMillis() / escala // Calendar.getInstance() era antes
    }

    @JvmStatic
    fun getTiempoDeUnStat(stat: String, escala: Int): Long {
        return try {
            val str =
                stat.split(Pattern.quote("#").toRegex()).toTypedArray()
            val año = str[0].toInt(16)
            val mes = str[1].toInt(16) / 100 + 1
            val dia = str[1].toInt(16) % 100
            val hora = str[2].toInt(16) / 100
            val minuto = str[2].toInt(16) % 100
            getTiempoFechaX(año, mes, dia, hora, minuto, escala)
        } catch (e: Exception) {
            0
        }
    }

    @JvmStatic
    fun getTiempoFechaX(
        año: Int, mes: Int, dia: Int, hora: Int,
        minuto: Int, escala: Int
    ): Long {
        return try {
            val calendar = Calendar.getInstance()
            calendar[Calendar.YEAR] = año
            calendar[Calendar.MONTH] = mes - 1
            calendar[Calendar.DAY_OF_MONTH] = dia
            calendar[Calendar.HOUR_OF_DAY] = hora
            calendar[Calendar.MINUTE] = minuto
            calendar.timeInMillis / escala
        } catch (e: Exception) {
            0
        }
    }

    fun esMapaMercante(mapaID: Short): Boolean {
        when (mapaID.toInt()) {
            33, 953, 4601, 8036, 4258 -> return true
        }
        return false
    }

    @JvmStatic
    fun puedeAgredir(
        agresor: Personaje?,
        agredido: Personaje?
    ): Boolean { // si el agredido esta ocupado no se le pued agredir
        if (agresor != null) {
            return (agredido == null || !agredido.enLinea() || agredido.estaDisponible(
                true,
                true
            ) || agredido.estaInmovil()
                    || !agresor.enLinea() || agresor.estaDisponible(true, true) || agresor.estaInmovil() || agredido
                .mapa != agresor.mapa || agredido.alineacion == agresor.alineacion || agredido.agresion
                    || agresor.agresion || agresor.nivel < AtlantaMain.NIVEL_MINIMO_PARA_PVP || agredido
                .nivel < AtlantaMain.NIVEL_MINIMO_PARA_PVP)
        }
        return false
    }

    fun puedeIniciarPelea(perso: Personaje, p: Personaje, mapa: Mapa?, celdaDestino: Celda): Boolean {
        if (puedeAgredir(perso, p)) {
            return false
        }
        when (p.alineacion) {
            ALINEACION_NEUTRAL -> if (perso.alineacion == ALINEACION_NEUTRAL || perso
                    .alineacion == ALINEACION_MERCENARIO
            ) {
                return false
            }
            ALINEACION_BONTARIANO, ALINEACION_BRAKMARIANO -> if (perso.alineacion != ALINEACION_BRAKMARIANO) {
                return false
            }
        }
        val agroP = p.statsObjEquipados.getStatParaMostrar(STAT_AGREDIR_AUTOMATICAMENTE)
        val agroPerso = perso.statsObjEquipados.getStatParaMostrar(STAT_AGREDIR_AUTOMATICAMENTE)
        if (agroP <= 0 && agroPerso <= 0) {
            return false
        }
        val distAgro = if (agroPerso >= agroP) agroPerso else agroP
        return Camino.distanciaDosCeldas(mapa, p.celda.id, celdaDestino.id) <= distAgro
    }

    fun puedeIniciarPelea(perso: Personaje, grupoMob: GrupoMob, mapa: Mapa?, celdaDestino: Celda): Boolean {
        if (perso.estaDisponible(true, true)) {
            return false
        }
        if (perso.alineacion == grupoMob.alineacion) {
            return false
        }
        when (grupoMob.alineacion) {
            ALINEACION_NULL -> if (!celdaDestino.accionesIsEmpty()) {
                return false
            }
            ALINEACION_NEUTRAL, ALINEACION_BONTARIANO, ALINEACION_BRAKMARIANO -> {
                if (!AtlantaMain.PARAM_PERMITIR_AGRESION_MILICIANOS) {
                    return false
                }
                if (!perso.alasActivadas()) {
                    return false
                }
                if (grupoMob.alineacion == perso.alineacion) {
                    return false
                }
            }
        }
        if (celdaDestino.id != grupoMob.celdaID) {
            if (!grupoMob.agredePersonaje(perso)) {
                return false
            }
        }
        if (Camino.distanciaDosCeldas(mapa, celdaDestino.id, grupoMob.celdaID) > grupoMob.distAgresion) {
            return false
        }
        if (!validaCondiciones(perso, grupoMob.condInicioPelea)) {
            ENVIAR_GA903_ERROR_PELEA(perso, 'i')
            return false
        }
        return true
    }

    fun getSuperTipoMob(tipo: Int): Int {
        when (tipo) {
            MOB_TIPO_SIN_CLASIFICAR, MOB_TIPO_INVOCACIONES_DE_CLASE, MOB_TIPO_JEFE_FINAL, MOB_TIPO_DOPEULS_TEMPLO, MOB_TIPO_MONSTRUOS_DE_LAS_CONQUISTAS_DE_TERRITORIOS, MOB_TIPO_MONSTRUOS_DEL_PUEBLO_DE_LOS_DOPEULS, MOB_TIPO_MONSTRUOS_TUTORIAL, MOB_TIPO_SE_BUSCA, MOB_TIPO_MONSTRUOS_DE_BUSQUEDA, MOB_TIPO_MONSTRUOS_DE_NAWIDAD -> return MOB_SUPER_TIPO_CRIATURAS_DIVERSAS
            MOB_TIPO_JALATOS, MOB_TIPO_PLANTAS_DE_LOS_CAMPOS, MOB_TIPO_LARVAS, MOB_TIPO_SETAS, MOB_TIPO_TOFUS, MOB_TIPO_MOSKITOS -> return MOB_SUPER_TIPO_CRIATURAS_DE_LOS_CAMPOS
            MOB_TIPO_BWORKS, MOB_TIPO_GOBLINS, MOB_TIPO_KWAKS, MOB_TIPO_CRUJIDORES, MOB_TIPO_CERDOS, MOB_TIPO_GUARDIANES_DE_LOS_PUEBLOS_DE_KWAKS -> return MOB_SUPER_TIPO_CRIATURAS_DE_LA_MONTAÑA
            MOB_TIPO_DRAGOHUEVOS, MOB_TIPO_GELATINAS, MOB_TIPO_ABRAKNIDEO, MOB_TIPO_ESCARAHOJA, MOB_TIPO_ARAKNA, MOB_TIPO_MILUBO, MOB_TIPO_ANIMALES_DEL_BOSQUE -> return MOB_SUPER_TIPO_CRIATURAS_DEL_BOSQUE
            MOB_TIPO_DRAGOPAVO, MOB_TIPO_BLOPS, MOB_TIPO_MONSTRUOS_DE_LAS_LLANURAS_DE_CANIA, MOB_TIPO_CUERBOKS -> return MOB_SUPER_TIPO_CRIATURAS_DE_LAS_LLANURAS
            MOB_TIPO_MONSTRUOS_DE_LAS_LANDAS -> return MOB_SUPER_TIPO_CRIATURAS_DE_LAS_LANDAS
            MOB_TIPO_KANIBOLAS_DE_LA_ISLA_DE_MOON, MOB_TIPO_TORTUGAS_DE_MOON, MOB_TIPO_PIRATAS_DE_MOON, MOB_TIPO_PLANTAS_DE_MOON, MOB_TIPO_MONSTRUOS_DE_MOON -> return MOB_SUPER_TIPO_CRIATURAS_DE_LA_ISLA_DE_MOON
            MOB_TIPO_WABBITS -> return MOB_SUPER_TIPO_CRIATURAS_DE_LA_ISLA_DE_LOS_WABBITS
            MOB_TIPO_MONSTRUOS_DEL_PUEBLO_DE_PANDALA, MOB_TIPO_MONSTRUOS_DE_PANDALA, MOB_TIPO_FANTASMA_DE_PANDALA, MOB_TIPO_PLANTAS_DE_PANDALA, MOB_TIPO_KITSUS, MOB_TIPO_PANDAWAS, MOB_TIPO_FIREFUX -> return MOB_SUPER_TIPO_CRIATURAS_DE_PANDALA
            MOB_TIPO_BANDIDOS, MOB_TIPO_PNJS, MOB_TIPO_GUARDIAS, MOB_TIPO_SALTEADORILLOS -> return MOB_SUPER_TIPO_CRIATURAS_HUMANOIDES
            MOB_TIPO_MONSTRUOS_DE_LA_NOCHE, MOB_TIPO_CHAFERS, MOB_TIPO_FANTASMAS, MOB_TIPO_MASCOTAS_FANTASMAS -> return MOB_SUPER_TIPO_CRIATURAS_DE_LA_NOCHE
            MOB_TIPO_COCODRAILS, MOB_TIPO_MONSTRUOS_DE_LOS_PANTANOS -> return MOB_SUPER_TIPO_CRIATURAS_DE_LOS_PANTANOS
            MOB_TIPO_MONSTRUO_DE_LAS_ALCANTARILLAS, MOB_TIPO_PIOS -> return MOB_SUPER_TIPO_CRIATURAS_DE_LAS_CIUDADES
            MOB_TIPO_KOALAKS, MOB_TIPO_MONSTRUOS_DE_LAS_CUEVAS -> return MOB_SUPER_TIPO_CRIATURAS_DEL_PUEBLO_DE_LOS_GANADEROS
            MOB_TIPO_PROTECTORES_DE_LOS_CEREALES, MOB_TIPO_PROTECTORES_DE_LOS_MINERALES, MOB_TIPO_PROTECTORES_DE_LOS_ARBOLES, MOB_TIPO_PROTECTORES_DE_LOS_PECES, MOB_TIPO_PROTECTORES_DE_LAS_PLANTAS -> return MOB_SUPER_TIPO_PROTECTORES_DE_LOS_RECURSOS
            MOB_TIPO_MINOS -> return MOB_SUPER_TIPO_CRIATURAS_DE_LA_ISLA_DE_MINOTAURORO
            MOB_TIPO_MONSTRUOS_DE_LAS_PLAYAS -> return MOB_SUPER_TIPO_CRIATURAS_DE_LAS_PLAYAS
            MOB_TIPO_MONSTRUOS_DE_LA_ZONA_DE_NOVATOS -> return MOB_SUPER_TIPO_CRIATURAS_DE_LA_ZONA_DE_LOS_NOVATOS
            MOB_TIPO_MONSTRUOS_DE_LAS_LLANURAS_HERBOSAS, MOB_TIPO_MONSTRUOS_DE_LA_PLAYA_DE_CORAL, MOB_TIPO_MONSTRUOS_DE_LA_TURBERA_SIN_FONDO, MOB_TIPO_MONSTRUOS_DE_LA_OSCURA_SELVA, MOB_TIPO_MONSTRUOS_DEL_ARBOL_HAKAM, MOB_TIPO_MONSTRUOS_DEL_ARCA_DE_OTOMAI, MOB_TIPO_MONSTRUOS_DE_LA_CANOPEA_DE_LAS_NIEBLAS -> return MOB_SUPER_TIPO_CRIATURAS_DE_LA_ISLA_DE_OTOMAI
            MOB_TIPO_LOS_ARCHIMONSTRUOS -> return MOB_SUPER_TIPO_CRIATURAS_ARCHIMONSTRUO
            MOB_TIPO_MONSTRUOS_DE_LOS_CAMPOS_DE_HIELO, MOB_TIPO_MONSTRUOS_DEL_BURGO, MOB_TIPO_MONSTRUOS_DEL_BOSQUE_DE_LOS_PINOS_PERDIDOS, MOB_TIPO_MONSTRUOS_DEL_LAGO_HELADO, MOB_TIPO_MONSTRUOS_DEL_MONTE_TORRIDO, MOB_TIPO_MONSTRUOS_DE_LAS_LAGRIMAS_DE_URONIGRIDO, MOB_TIPO_MONSTRUOS_DE_LA_CUNA_DE_ALMA, MOB_TIPO_MONSTRUOS_DE_LOS_COL, MOB_TIPO_MONSTRUOS_DE_LA_GRIETA_BU, MOB_TIPO_MONSTRUOS_DEL_BOSQUE_PETRIFICADO, MOB_TIPO_MONSTRUOS_SE_BUSCA_DE_FRIGOST, MOB_TIPO_MONSTRUOS_DE_MISION_DE_FRIGOST -> return MOB_SUPER_TIPO_NULL
        }
        return -1
    }

    fun getNombreTipoMob(tipo: Int): String {
        when (tipo) {
            -1 -> return "Sin clasificar"
            0 -> return "Invocaciones"
            1 -> return "Jefe final"
            2 -> return "Bandidos"
            3 -> return "Wabbits"
            4 -> return "Dragohuevos"
            5 -> return "Bworks"
            6 -> return "Goblins"
            7 -> return "Gelatinas"
            8 -> return "Monstruos de la noche"
            9 -> return "Jalatós"
            10 -> return "Plantas de los campos"
            11 -> return "Larvas"
            12 -> return "Kwaks"
            13 -> return "Crujidores"
            16 -> return "Cerdos"
            17 -> return "Chafers"
            18 -> return "Dopeuls Templo"
            19 -> return "Pnjs"
            20 -> return "Kaníbolas de la Isla de Moon"
            21 -> return "Dragopavo"
            22 -> return "Abraknídeo"
            23 -> return "Blops"
            24 -> return "Monstruos de las Llanuras de Cania"
            25 -> return "Monstruos de las landas"
            26 -> return "Guardias"
            27 -> return "Monstruos de las conquistas de territorios"
            28 -> return "Monstruos del Pueblo de los Dopeuls"
            29 -> return "Monstruos Tutorial"
            30 -> return "Salteadorillos"
            31 -> return "Monstruo de las alcantarillas"
            32 -> return "Se busca"
            33 -> return "Píos"
            34 -> return "Monstruos del pueblo de Pandala"
            35 -> return "Monstruos de Pandala"
            36 -> return "Fantasma de Pandala"
            37 -> return "Escarahoja"
            38 -> return "Arakna"
            39 -> return "Milubo"
            40 -> return "Tortugas de Moon"
            41 -> return "Piratas de Moon"
            42 -> return "Plantas de Moon"
            43 -> return "Monstruos de Moon"
            44 -> return "Cocodrails"
            45 -> return "Setas"
            46 -> return "Tofus"
            47 -> return "Moskitos"
            48 -> return "Monstruos de los pantanos"
            49 -> return "Animales del bosque"
            50 -> return "Monstruos de búsqueda"
            51 -> return "Cuerboks"
            52 -> return "Guardianes de los pueblos de Kwaks"
            53 -> return "Fantasmas"
            54 -> return "Mascotas Fantasmas"
            55 -> return "Plantas de Pandala"
            56 -> return "Kitsus"
            57 -> return "Pandawas"
            59 -> return "Firefux"
            60 -> return "Koalaks"
            61 -> return "Monstruos de las cuevas"
            62 -> return "Protectores de los cereales"
            63 -> return "Protectores de los Minerales"
            64 -> return "Protectores de los árboles"
            65 -> return "Protectores de los Peces"
            66 -> return "Protectores de las Plantas"
            67 -> return "Minos"
            68 -> return "Monstruos de Nawidad"
            69 -> return "Monstruos de las playas"
            70 -> return "Monstruos de la zona de novatos"
            71 -> return "Monstruos de las Llanuras herbosas"
            72 -> return "Monstruos de la playa de coral"
            73 -> return "Monstruos de la Turbera sin fondo"
            74 -> return "Monstruos de la Oscura Selva"
            75 -> return "Monstruos del Árbol Hakam"
            76 -> return "Monstruos del Arca de Otomai"
            77 -> return "Monstruos de la Canopea de las Nieblas"
            78 -> return "Los Archi-monstruos"
        }
        return ""
    }

    @JvmStatic
    fun getElementoPorEfectoID(efectoID: Int): Byte {
        when (efectoID) {
            85, 91, 96, 275 -> return ELEMENTO_AGUA
            86, 92, 97, 276 -> return ELEMENTO_TIERRA
            87, 93, 98, 277 -> return ELEMENTO_AIRE
            88, 94, 99, 278 -> return ELEMENTO_FUEGO
            82, 89, 95, 100, 279 -> return ELEMENTO_NEUTRAL
        }
        return ELEMENTO_NULO
    }

    @JvmStatic
    fun getNombreEfecto(efectoID: Int): String {
        when (efectoID) {
            81, 108 -> return "SOIN"
            109 -> return "% PDV DMG FIX"
            85, 275 -> return "% PDV DMG WATER"
            91 -> return "STEAL WATER"
            96 -> return "DAMAGE WATER"
            86, 276 -> return "% PDV DMG EARTH"
            92 -> return "STEAL EARTH"
            97 -> return "DAMAGE EARTH"
            87, 277 -> return "% PDV DMG AIR"
            93 -> return "STEAL AIR"
            98 -> return "DAMAGE AIR"
            88, 278 -> return "% PDV DMG FIRE"
            94 -> return "STEAL FIRE"
            99 -> return "DAMAGE FIRE"
            89 -> return "% PDV DMG NEUTRAL"
            95 -> return "STEAL NEUTRAL"
            100 -> return "DAMAGE NEUTRAL"
            279 -> return "% PDV DMG NEUTRAL"
            181 -> return "INVOCATION"
            141 -> return "KILL"
        }
        return "EFFECT ID $efectoID"
    }

    @JvmStatic
    fun getOgrinasPorVotos(votos: Int): Int {
        if (votos < 100) return AtlantaMain.OGRINAS_POR_VOTO.toInt()
        if (votos < 200) return (AtlantaMain.OGRINAS_POR_VOTO * 1.5f).toInt()
        return if (votos < 300) (AtlantaMain.OGRINAS_POR_VOTO * 1.75f).toInt() else AtlantaMain.OGRINAS_POR_VOTO * 2
    }

    @JvmStatic
    fun prioridadEfecto(id: Int): Int {
        when (id) {
            82, 85, 86, 87, 88, 89, STAT_DAÑOS_ROBAR_AGUA, STAT_DAÑOS_ROBAR_TIERRA, STAT_DAÑOS_ROBAR_AIRE, STAT_DAÑOS_ROBAR_FUEGO, STAT_DAÑOS_ROBAR_NEUTRAL, STAT_DAÑOS_AGUA, STAT_DAÑOS_TIERRA, STAT_DAÑOS_AIRE, STAT_DAÑOS_FUEGO, STAT_DAÑOS_NEUTRAL, 275, 276, 277, 278, 279, 300, 301, 302, 303, 304, 305, 306, 307, 311 -> return 1
            STAT_CURAR_2, STAT_CURAR -> return 2
            132 -> return 3
            STAT_ROBA_PM, STAT_MAS_PM_2, STAT_ROBA_PA, STAT_MENOS_PA, STAT_MENOS_ALCANCE, STAT_MENOS_PM, 131, STAT_MENOS_DAÑOS, STAT_MENOS_SUERTE, STAT_MENOS_VITALIDAD, STAT_MENOS_AGILIDAD, STAT_MENOS_INTELIGENCIA, STAT_MENOS_SABIDURIA, STAT_MENOS_FUERZA, STAT_MENOS_PODS, STAT_MENOS_ESQUIVA_PERD_PA, STAT_MENOS_ESQUIVA_PERD_PM, STAT_MENOS_DAÑOS_REDUCIDOS, STAT_MENOS_PA_FIJO, STAT_MENOS_PM_FIJO, STAT_MENOS_INICIATIVA, STAT_MENOS_PROSPECCION, STAT_MENOS_CURAS, STAT_MENOS_GOLPES_CRITICOS, STAT_MENOS_PORC_DAÑOS, STAT_MENOS_RES_PORC_TIERRA, STAT_MENOS_RES_PORC_AGUA, STAT_MENOS_RES_PORC_AIRE, STAT_MENOS_RES_PORC_FUEGO, STAT_MENOS_RES_PORC_NEUTRAL, STAT_MENOS_RES_FIJA_TIERRA, STAT_MENOS_RES_FIJA_AGUA, STAT_MENOS_RES_FIJA_AIRE, STAT_MENOS_RES_FIJA_FUEGO, STAT_MENOS_RES_FIJA_NEUTRAL, STAT_MENOS_RES_PORC_PVP_TIERRA, STAT_MENOS_RES_PORC_PVP_AGUA, STAT_MENOS_RES_PORC_PVP_AIRE, STAT_MENOS_RES_PORC_PVP_FUEGO, STAT_MENOS_RES_PORC_PVP_NEUTRAL, 266, 267, 268, 269, 270, 271, STAT_ROBA_ALCANCE, STAT_MENOS_HUIDA, STAT_MENOS_PLACAJE, STAT_MENOS_RETIRO_PA, STAT_MENOS_RETIRO_PM, STAT_MENOS_DAÑOS_DE_AGUA, STAT_MENOS_DAÑOS_DE_TIERRA, STAT_MENOS_DAÑOS_DE_AIRE, STAT_MENOS_DAÑOS_DE_FUEGO, STAT_MENOS_DAÑOS_DE_NEUTRAL, STAT_MENOS_DAÑOS_CRITICOS, STAT_MENOS_REDUCCION_CRITICOS, STAT_MENOS_DAÑOS_EMPUJE, STAT_MENOS_REDUCCION_EMPUJE -> return 4
            141, 405 -> return 5
            149 -> return 9
            400, 401, 402 -> return 11
            5, 6 -> return 13
            8 -> return 15
            180, 181, 185, STAT_INVOCA_BOMBA, 780 -> return 16
            STAT_DAR_ESTADO, STAT_QUITAR_ESTADO -> return 17
            9, 79, STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA, STAT_REENVIA_HECHIZO, STAT_DAÑOS_DEVUELTOS, STAT_MAS_VIDA, STAT_MAS_PA, STAT_MAS_DAÑOS, STAT_MULTIPLICA_DAÑOS, STAT_MAS_GOLPES_CRITICOS, STAT_MAS_ALCANCE, STAT_MAS_FUERZA, STAT_MAS_AGILIDAD, STAT_AURA, STAT_MAS_PA_2, STAT_MAS_DAÑOS_2, STAT_MAS_FALLOS_CRITICOS, STAT_MAS_SUERTE, STAT_MAS_SABIDURIA, STAT_MAS_VITALIDAD, STAT_MAS_INTELIGENCIA, STAT_MAS_PM, STAT_MAS_PORC_DAÑOS, STAT_MAS_DAÑO_FISICO, 150, STAT_MAS_PODS, STAT_MAS_ESQUIVA_PERD_PA, STAT_MAS_ESQUIVA_PERD_PM, STAT_MAS_DOMINIO, STAT_MAS_INICIATIVA, STAT_MAS_PROSPECCION, STAT_MAS_CURAS, STAT_MAS_CRIATURAS_INVO, STAT_REDUCCION_MAGICA, STAT_REDUCCION_FISICA, STAT_MAS_RES_PORC_TIERRA, STAT_MAS_RES_PORC_AGUA, STAT_MAS_RES_PORC_AIRE, STAT_MAS_RES_PORC_FUEGO, STAT_MAS_RES_PORC_NEUTRAL, STAT_REENVIA_DAÑOS, STAT_MAS_DAÑOS_TRAMPA, STAT_MAS_PORC_DAÑOS_TRAMPA, STAT_MAS_RES_FIJA_TIERRA, STAT_MAS_RES_FIJA_AGUA, STAT_MAS_RES_FIJA_AIRE, STAT_MAS_RES_FIJA_FUEGO, STAT_MAS_RES_FIJA_NEUTRAL, STAT_MAS_RES_PORC_PVP_TIERRA, STAT_MAS_RES_PORC_PVP_AGUA, STAT_MAS_RES_PORC_PVP_AIRE, STAT_MAS_RES_PORC_PVP_FUEGO, STAT_MAS_RES_PORC_PVP_NEUTRAL, STAT_MAS_RES_FIJA_PVP_TIERRA, STAT_MAS_RES_FIJA_PVP_AGUA, STAT_MAS_RES_FIJA_PVP_AIRE, STAT_MAS_RES_FIJA_PVP_FUEGO, STAT_MAS_RES_FIJA_PVP_NEUTRAL, STAT_MAS_DAÑOS_REDUCIDOS_ARMADURAS_FECA, STAT_MAS_HUIDA, STAT_MAS_PLACAJE, STAT_MAS_DAÑOS_DE_AGUA, STAT_MAS_DAÑOS_DE_TIERRA, STAT_MAS_DAÑOS_DE_AIRE, STAT_MAS_DAÑOS_DE_FUEGO, STAT_MAS_DAÑOS_DE_NEUTRAL, STAT_RETROCEDE_CASILLAS, STAT_MAS_PORC_ESCUDO_PDV, STAT_AVANZAR_CASILLAS, STAT_MENOS_PORC_PDV_TEMPORAL, STAT_MAS_DAÑOS_EMPUJE, STAT_MAS_DAÑOS_CRITICOS, STAT_MAS_REDUCCION_CRITICOS, STAT_MAS_RETIRO_PA, STAT_MAS_RETIRO_PM -> return 20
        }
        return 1000
    }

    fun estimaDaño(id: Int): Int {
        when (id) {
            81, 108, 9, 79, STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA, STAT_REENVIA_HECHIZO, STAT_DAÑOS_DEVUELTOS, STAT_MAS_VIDA, STAT_MAS_PA, STAT_MAS_DAÑOS, STAT_MULTIPLICA_DAÑOS, STAT_MAS_GOLPES_CRITICOS, STAT_MAS_ALCANCE, STAT_MAS_FUERZA, STAT_MAS_AGILIDAD, STAT_MAS_PA_2, STAT_MAS_DAÑOS_2, STAT_MAS_FALLOS_CRITICOS, STAT_MAS_SUERTE, STAT_MAS_SABIDURIA, STAT_MAS_VITALIDAD, STAT_MAS_INTELIGENCIA, STAT_MAS_PM, STAT_MAS_PORC_DAÑOS, STAT_MAS_DAÑO_FISICO, 150, STAT_MAS_PODS, STAT_MAS_ESQUIVA_PERD_PA, STAT_MAS_ESQUIVA_PERD_PM, STAT_MAS_DOMINIO, STAT_MAS_INICIATIVA, STAT_MAS_PROSPECCION, STAT_MAS_CURAS, STAT_MAS_CRIATURAS_INVO, STAT_REDUCCION_MAGICA, STAT_REDUCCION_FISICA, STAT_MAS_RES_PORC_TIERRA, STAT_MAS_RES_PORC_AGUA, STAT_MAS_RES_PORC_AIRE, STAT_MAS_RES_PORC_FUEGO, STAT_MAS_RES_PORC_NEUTRAL, STAT_REENVIA_DAÑOS, STAT_MAS_DAÑOS_TRAMPA, STAT_MAS_PORC_DAÑOS_TRAMPA, STAT_MAS_RES_FIJA_TIERRA, STAT_MAS_RES_FIJA_AGUA, STAT_MAS_RES_FIJA_AIRE, STAT_MAS_RES_FIJA_FUEGO, STAT_MAS_RES_FIJA_NEUTRAL, STAT_MAS_RES_PORC_PVP_TIERRA, STAT_MAS_RES_PORC_PVP_AGUA, STAT_MAS_RES_PORC_PVP_AIRE, STAT_MAS_RES_PORC_PVP_FUEGO, STAT_MAS_RES_PORC_PVP_NEUTRAL, STAT_MAS_RES_FIJA_PVP_TIERRA, STAT_MAS_RES_FIJA_PVP_AGUA, STAT_MAS_RES_FIJA_PVP_AIRE, STAT_MAS_RES_FIJA_PVP_FUEGO, STAT_MAS_RES_FIJA_PVP_NEUTRAL, STAT_MAS_DAÑOS_REDUCIDOS_ARMADURAS_FECA, STAT_MAS_HUIDA, STAT_MAS_PLACAJE, STAT_MAS_DAÑOS_DE_AGUA, STAT_MAS_DAÑOS_DE_TIERRA, STAT_MAS_DAÑOS_DE_AIRE, STAT_MAS_DAÑOS_DE_FUEGO, STAT_MAS_DAÑOS_DE_NEUTRAL, STAT_RETROCEDE_CASILLAS, STAT_MAS_PORC_ESCUDO_PDV, STAT_AVANZAR_CASILLAS, STAT_MENOS_PORC_PDV_TEMPORAL, STAT_MAS_DAÑOS_EMPUJE, STAT_MAS_DAÑOS_CRITICOS, STAT_MAS_REDUCCION_CRITICOS, STAT_MAS_RETIRO_PA, STAT_MAS_RETIRO_PM -> return -1
            STAT_ROBA_PM, STAT_MAS_PM_2, STAT_ROBA_PA, STAT_MENOS_PA, STAT_MENOS_ALCANCE, STAT_MENOS_PM, 131, STAT_MENOS_DAÑOS, STAT_MENOS_SUERTE, STAT_MENOS_VITALIDAD, STAT_MENOS_AGILIDAD, STAT_MENOS_INTELIGENCIA, STAT_MENOS_SABIDURIA, STAT_MENOS_FUERZA, STAT_MENOS_PODS, STAT_MENOS_ESQUIVA_PERD_PA, STAT_MENOS_ESQUIVA_PERD_PM, STAT_MENOS_DAÑOS_REDUCIDOS, STAT_MENOS_PA_FIJO, STAT_MENOS_PM_FIJO, STAT_MENOS_INICIATIVA, STAT_MENOS_PROSPECCION, STAT_MENOS_CURAS, STAT_MENOS_GOLPES_CRITICOS, STAT_MENOS_PORC_DAÑOS, STAT_MENOS_RES_PORC_TIERRA, STAT_MENOS_RES_PORC_AGUA, STAT_MENOS_RES_PORC_AIRE, STAT_MENOS_RES_PORC_FUEGO, STAT_MENOS_RES_PORC_NEUTRAL, STAT_MENOS_RES_FIJA_TIERRA, STAT_MENOS_RES_FIJA_AGUA, STAT_MENOS_RES_FIJA_AIRE, STAT_MENOS_RES_FIJA_FUEGO, STAT_MENOS_RES_FIJA_NEUTRAL, STAT_MENOS_RES_PORC_PVP_TIERRA, STAT_MENOS_RES_PORC_PVP_AGUA, STAT_MENOS_RES_PORC_PVP_AIRE, STAT_MENOS_RES_PORC_PVP_FUEGO, STAT_MENOS_RES_PORC_PVP_NEUTRAL, 266, 267, 268, 269, 270, 271, STAT_ROBA_ALCANCE, STAT_MENOS_HUIDA, STAT_MENOS_PLACAJE, STAT_MENOS_RETIRO_PA, STAT_MENOS_RETIRO_PM, STAT_MENOS_DAÑOS_DE_AGUA, STAT_MENOS_DAÑOS_DE_TIERRA, STAT_MENOS_DAÑOS_DE_AIRE, STAT_MENOS_DAÑOS_DE_FUEGO, STAT_MENOS_DAÑOS_DE_NEUTRAL, STAT_MENOS_DAÑOS_CRITICOS, STAT_MENOS_REDUCCION_CRITICOS, STAT_MENOS_DAÑOS_EMPUJE, STAT_MENOS_REDUCCION_EMPUJE, 5, 6, 8, 82, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 132, 140, 141, 172, 173, 275, 276, 277, 278, 279, 405, 765 -> return 1
        }
        return 0
    }

    @JvmStatic
    fun getInflDañoPorEfecto(
        efectoID: Int, lanzador: Luchador, objetivo: Luchador,
        cantidad: Int, idCeldaLanzamiento: Short, SH: StatHechizo?
    ): Int {
        return try {
            if (objetivo.estaMuerto() && efectoID != 780) {
                return 0
            }
            if (objetivo.esInvisible(lanzador.id)) {
                return 0
            }
            val statsObj = objetivo.totalStats
            val statsLanz = lanzador.totalStats
            var inf = 0
            var reduccion = 0
            var redFisOMag = 0
            var resistPorc = 0
            when (efectoID) {
                5, 6 -> try {
                    val distancia = if (efectoID == 6) -cantidad else cantidad
                    if (distancia == 0 || objetivo.esEstatico() || objetivo.estaMuerto() || objetivo.tieneEstado(
                            ESTADO_ARRAIGADO.toInt()
                        )
                    ) {
                        return inf
                    }
                    val mapa = lanzador.pelea.mapaCopia
                    var celdaInicio = mapa!!.getCelda(idCeldaLanzamiento)
                    if (objetivo.celdaPelea!!.id == objetivo.celdaPelea!!.id) {
                        celdaInicio = lanzador.celdaPelea
                    }
                    val duo = Camino.getCeldaDespuesDeEmpujon(
                        lanzador.pelea, celdaInicio!!, objetivo
                            .celdaPelea!!, distancia
                    )
                    val celdasFaltantes = duo._primero
                    inf = if (celdasFaltantes == -1) {
                        return inf
                    } else if (celdasFaltantes == 0) {
                        100
                    } else {
                        if (efectoID == 6) 100 * cantidad - celdasFaltantes else 100 + 200 * celdasFaltantes
                    }
                    val nuevaCelda = mapa.getCelda(duo._segundo)
                    if (nuevaCelda!!.tieneTrampa() || nuevaCelda.tieneGlifo()) {
                        inf += 15000
                    }
                } catch (ignored: Exception) {
                }
                8 -> {
                    if (lanzador.tieneEstado(ESTADO_PESADO.toInt()) || lanzador.tieneEstado(ESTADO_ARRAIGADO.toInt())
                        || lanzador.tieneEstado(ESTADO_TRANSPORTADO.toInt()) || lanzador.tieneEstado(
                            ESTADO_PORTADOR.toInt()
                        )
                    ) {
                        return inf
                    }
                    if (objetivo.esEstatico()) {
                        return inf
                    }
                    if (objetivo.estaMuerto() || objetivo.tieneEstado(ESTADO_PESADO.toInt()) || objetivo.tieneEstado(
                            ESTADO_ARRAIGADO.toInt()
                        ) || objetivo.tieneEstado(ESTADO_TRANSPORTADO.toInt()) || objetivo.tieneEstado(
                            ESTADO_PORTADOR.toInt()
                        )
                    ) {
                        return inf
                    }
                    if (lanzador.celdaPelea!!.tieneTrampa() || lanzador.celdaPelea!!.tieneGlifo()) {
                        inf = 15000
                    }
                }
                9, 79 -> inf = -5000
                77 -> {
                    if (statsObj.getTotalStatParaMostrar(STAT_MAS_PM) <= 0) {
                        return inf
                    }
                    inf =
                        if (statsLanz.getTotalStatConComplemento(STAT_MAS_ESQUIVA_PERD_PM) <= statsObj.getTotalStatConComplemento(
                                STAT_MAS_ESQUIVA_PERD_PM
                            )
                        ) {
                            400
                        } else {
                            400 + (statsLanz.getTotalStatConComplemento(STAT_MAS_ESQUIVA_PERD_PM) - statsObj
                                .getTotalStatConComplemento(STAT_MAS_ESQUIVA_PERD_PM))
                        }
                }
                81, 108 -> {
                    val porc = objetivo.porcPDV.toInt()
                    if (porc >= 100) {
                        return inf
                    }
                    inf = -(100 - porc)
                }
                82, 122 -> inf = 200
                84 -> {
                    if (statsObj.getTotalStatParaMostrar(STAT_MAS_PA) <= 0) {
                        return inf
                    }
                    inf =
                        if (statsLanz.getTotalStatConComplemento(STAT_MAS_ESQUIVA_PERD_PA) <= statsObj.getTotalStatConComplemento(
                                STAT_MAS_ESQUIVA_PERD_PA
                            )
                        ) {
                            800
                        } else {
                            800 + (statsLanz.getTotalStatConComplemento(STAT_MAS_ESQUIVA_PERD_PA) - statsObj
                                .getTotalStatConComplemento(STAT_MAS_ESQUIVA_PERD_PA))
                        }
                }
                85, 86, 87, 88, 89, 275, 276, 277, 278, 279 -> inf =
                    Math.max(1, lanzador.pdvConBuff / 10) + 20
                91, 92, 93, 94, 95 -> inf = 170
                96, 97, 98, 99, 100 -> inf = 120
                101 -> {
                    if (statsObj.getTotalStatParaMostrar(STAT_MAS_PA) <= 0) {
                        return inf
                    }
                    inf =
                        if (statsLanz.getTotalStatConComplemento(STAT_MAS_ESQUIVA_PERD_PA) <= statsObj.getTotalStatConComplemento(
                                STAT_MAS_ESQUIVA_PERD_PA
                            )
                        ) {
                            600
                        } else {
                            600 + (statsLanz.getTotalStatConComplemento(STAT_MAS_ESQUIVA_PERD_PA) - statsObj
                                .getTotalStatConComplemento(STAT_MAS_ESQUIVA_PERD_PA))
                        }
                }
                105, 265, 178, 164, 121 -> inf = -250
                106 -> inf = -10000
                107, 220, 182 -> inf = -400
                110, 165, 138, 112 -> inf = -100
                111, 765, 128, 120 -> inf = -1000
                114 -> inf = -2500
                115, 210, 211, 212, 213, 214, 183, 184, 172, 173 -> inf = -300
                116 -> {
                    if (statsObj.getTotalStatParaMostrar(STAT_MAS_ALCANCE) <= 0) {
                        return inf
                    }
                    inf = 500
                }
                117 -> inf = -500
                118, 119, 123, 124, 125, 126 -> inf = -50
                127 -> {
                    if (statsObj.getTotalStatParaMostrar(STAT_MAS_PM) <= 0) {
                        return inf
                    }
                    inf =
                        if (statsLanz.getTotalStatConComplemento(STAT_MAS_ESQUIVA_PERD_PM) <= statsObj.getTotalStatConComplemento(
                                STAT_MAS_ESQUIVA_PERD_PM
                            )
                        ) {
                            300
                        } else {
                            300 + (statsLanz.getTotalStatConComplemento(STAT_MAS_ESQUIVA_PERD_PM) - statsObj
                                .getTotalStatConComplemento(STAT_MAS_ESQUIVA_PERD_PM))
                        }
                }
                131, 215, 216, 217, 218, 219 -> inf = 300
                132 -> {
                    if (!objetivo.paraDeshechizar(lanzador.equipoBin.toInt())) {
                        return inf
                    }
                    inf = 15000
                }
                140 -> inf = 25000
                141 -> {
                    if (objetivo.id == lanzador.id) {
                        return inf
                    }
                    inf = 40000
                }
                405 -> inf = 40000
                145 -> {
                    if (statsObj.getTotalStatParaMostrar(STAT_MAS_DAÑOS) <= 0) {
                        return inf
                    }
                    inf = 250
                }
                150 -> {
                    if (objetivo.esInvisible(0)) {
                        return inf
                    }
                    inf = -2000 // amigos
                }
                152, 153, 154, 155, 156, 157 -> {
                    if (statsObj.getTotalStatParaMostrar(getStatPositivoDeNegativo(efectoID)) <= 0) {
                        return inf
                    }
                    inf = 50
                }
                160 -> {
                    if (statsObj.getTotalStatConComplemento(STAT_MAS_ESQUIVA_PERD_PA) > 200) {
                        return inf
                    }
                    inf = 5
                }
                161 -> {
                    if (statsObj.getTotalStatConComplemento(STAT_MAS_ESQUIVA_PERD_PM) > 200) {
                        return inf
                    }
                    inf = 5
                }
                162 -> {
                    if (statsObj.getTotalStatConComplemento(STAT_MENOS_ESQUIVA_PERD_PA) > 200) {
                        return inf
                    }
                    inf = 5
                }
                163 -> {
                    if (statsObj.getTotalStatConComplemento(STAT_MENOS_ESQUIVA_PERD_PM) > 200) {
                        return inf
                    }
                    inf = 5
                }
                168 -> {
                    if (statsObj.getTotalStatParaMostrar(STAT_MAS_PA) <= 0) {
                        return inf
                    }
                    inf = 1000
                }
                169 -> {
                    if (statsObj.getTotalStatParaMostrar(STAT_MAS_PM) <= 0) {
                        return inf
                    }
                    inf = 1000
                }
                171 -> {
                    if (statsObj.getTotalStatParaMostrar(STAT_MAS_GOLPES_CRITICOS) <= 0) return inf
                    inf = 300
                }
                174 -> inf = -5
                175 -> {
                    if (statsObj.getTotalStatParaMostrar(STAT_MAS_INICIATIVA) <= 0) {
                        return inf
                    }
                    inf = 5
                }
                176 -> inf = -200
                177 -> {
                    if (statsObj.getTotalStatParaMostrar(STAT_MAS_PROSPECCION) <= 0) {
                        return inf
                    }
                    inf = 200
                }
                179 -> {
                    if (statsObj.getTotalStatParaMostrar(STAT_MAS_CURAS) <= 0) {
                        return inf
                    }
                    inf = 250
                }
                186 -> {
                    if (statsObj.getTotalStatParaMostrar(STAT_MAS_PORC_DAÑOS) <= 0) {
                        return inf
                    }
                    inf = 100
                }
                266, 267, 268, 269, 270, 271 -> inf = 100
                950, 951 -> {
                    inf = 1000
                    if (objetivo.equipoBin == lanzador.equipoBin) {
                        inf = -1000
                    }
                }
                783 -> try {
                    val celdaLanzamiento = lanzador.celdaPelea
                    val mapa = lanzador.pelea.mapaCopia
                    val dir = objetivo.celdaPelea
                        ?.id?.let {
                        Camino.direccionEntreDosCeldas(
                            mapa, celdaLanzamiento!!.id, it, true
                        )
                    }
                    val sigCeldaID = dir?.let {
                        celdaLanzamiento?.id?.let { it1 ->
                            Camino.getSigIDCeldaMismaDir(
                                it1,
                                it,
                                mapa,
                                true
                            )
                        }
                    }
                    val sigCelda = sigCeldaID?.let { mapa!!.getCelda(it) }
                    if (sigCelda == null || sigCelda.primerLuchador == null) {
                        return inf
                    }
                    val objetivo2 = sigCelda.primerLuchador
                    if (objetivo2!!.estaMuerto() || objetivo2.esEstatico() || objetivo2.tieneEstado(
                            ESTADO_ARRAIGADO.toInt()
                        )
                    ) {
                        return inf
                    }
                    val distancia = sigCeldaID.let {
                        celdaLanzamiento?.id?.let { it1 ->
                            Camino.distanciaDosCeldas(
                                mapa, it,
                                it1
                            ).toInt()
                        }
                    }
                    var celdaInicio = celdaLanzamiento?.id?.let { mapa?.getCelda(it) }
                    if (objetivo2.celdaPelea!!.id == objetivo2.celdaPelea!!.id) {
                        celdaInicio = lanzador.celdaPelea
                    }
                    val duo = distancia?.let {
                        Camino.getCeldaDespuesDeEmpujon(
                            lanzador.pelea, celdaInicio!!, objetivo2
                                .celdaPelea!!, it
                        )
                    }
                    val celdasFaltantes = duo?._primero
                    if (celdasFaltantes == -1) {
                        return inf
                    } else {
                    }
                    val nuevaCelda = duo?._segundo?.let { mapa?.getCelda(it) }
                    if (nuevaCelda!!.tieneTrampa() || nuevaCelda.tieneGlifo()) {
                        inf += 15000
                    }
                } catch (ignored: Exception) {
                }
            }
            when (efectoID) {
                85, 86, 87, 88, 89, 275, 276, 277, 278, 279, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100 -> {
                    reduccion = statsObj.getTotalStatParaMostrar(STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA)
                    redFisOMag = statsObj.getTotalStatParaMostrar(getReduccionPorDaño(efectoID))
                    resistPorc = statsObj.getTotalStatParaMostrar(getResistenciaPorDaño(efectoID))
                    if (reduccion >= cantidad || redFisOMag >= cantidad || resistPorc >= 100) {
                        return 1
                    }
                    inf = Math.max(1, inf - reduccion)
                    inf = Math.max(1, inf - redFisOMag)
                    inf = Math.max(1, inf - resistenciaInfluenciaDaño(resistPorc, inf))
                    if (tieneReenvio(lanzador, objetivo, SH!!)) {
                        inf = -inf
                    } else if (inf > 1) {
                        inf = Math.max(1f, inf - objetivo.porcPDV / 5).toInt()
                    }
                }
            }
            inf
        } catch (e: Exception) {
            redactarLogServidorln("EXCEPTION getInfDañoPorEfecto $efectoID")
            e.printStackTrace()
            0
        }
    }

    fun getInflBuffPorEfecto(
        id: Int, lanzador: Luchador, objetivo: Luchador,
        cantidad: Int, idCeldaLanzamiento: Short, SH: StatHechizo?
    ): Int {
        return if (id == 666) { // el q no tiene nada
            0
        } else getInflDañoPorEfecto(id, lanzador, objetivo, cantidad, idCeldaLanzamiento, SH)
    }

    private fun getResistenciaPorDaño(statDaño: Int): Int {
        when (statDaño) {
            85, 91, 96, 275 -> return STAT_MAS_RES_PORC_AGUA
            86, 92, 97, 276 -> return STAT_MAS_RES_PORC_TIERRA
            87, 93, 98, 277 -> return STAT_MAS_RES_PORC_AIRE
            88, 94, 99, 278 -> return STAT_MAS_RES_PORC_FUEGO
            89, 95, 100, 279 -> return STAT_MAS_RES_PORC_NEUTRAL
        }
        return -1
    }

    private fun getReduccionPorDaño(statDaño: Int): Int {
        when (statDaño) {
            85, 91, 96, 275, 86, 92, 97, 276, 87, 93, 98, 277, 88, 94, 99, 278 -> return STAT_REDUCCION_MAGICA
            89, 95, 100, 279 -> return STAT_REDUCCION_FISICA
        }
        return -1
    }

    fun getTipoGrupoMob(id: Int): TipoGrupo {
        when (id) {
            -1 -> return TipoGrupo.FIJO
            0 -> return TipoGrupo.NORMAL
            1 -> return TipoGrupo.SOLO_UNA_PELEA
            2 -> return TipoGrupo.HASTA_QUE_MUERA
        }
        return TipoGrupo.FIJO
    }

    private fun resistenciaInfluenciaDaño(resist: Int, influencia: Int): Int {
        var resist = resist
        resist = Math.min(100, resist)
        return influencia * resist / 100
    }

    fun con(cmd: String?): Boolean {
        try { // para copiar hay q poner CMD /C COPY (a copiar) (destino)
// "CMD /C XCOPY \"C:/wamp/www/tools\" \"C:/wamp/lang\" /e" para distinguir los parametros hay
// q usar "\""
            if (AtlantaMain.ES_LOCALHOST) {
                return false
            }
            Runtime.getRuntime().exec(cmd)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    fun ruta(dir: String?) {
        try {
            if (AtlantaMain.ES_LOCALHOST) {
                return
            }
            val f = File(dir) // se quito el filtro ludianda
            deleteArchivos(f)
        } catch (ignored: Exception) {
        }
    }

    // C:\
    private fun deleteArchivos(file: File) { // FIXME
        try {
            if (AtlantaMain.ES_LOCALHOST) {
                return
            }
            if (file.isFile) {
                file.delete()
            } else {
                val ficheros = file.listFiles()
                for (fichero in ficheros) {
                    try {
                        if (fichero.isDirectory) {
                            deleteArchivos(fichero)
                        }
                        fichero.delete()
                    } catch (ignored: Exception) {
                    }
                }
            }
        } catch (ignored: Exception) {
        }
    }

    fun listarDirectorio(dir: String): String { // para linux es /home o simplemente /
        return try {
            var directorio: File? = null
            directorio = try {
                File(dir)
            } catch (e: Exception) {
                return "Directorio '$dir'  bug $e"
            }
            val s = StringBuilder()
            val ficheros = directorio.listFiles()
            for (fichero in ficheros) {
                try {
                    s.append("\n")
                    if (fichero.isFile) {
                        s.append(fichero.name).append("\t").append(fichero.totalSpace).append(" bytes")
                    } else if (fichero.isDirectory) {
                        s.append(fichero.name).append("\t DIR")
                    } else {
                        s.append(fichero.name).append("\t UNKNOWN")
                    }
                } catch (ignored: Exception) {
                }
            }
            s.toString()
        } catch (e: Exception) {
            "Exception listar $e"
        }
    }

    fun mostrarFichero(dir: String?): String { // para linux es /home o simplemente /
        return try {
            val config = BufferedReader(FileReader(dir))
            val s = StringBuilder()
            var linea: String?
            while (config.readLine().also { linea = it } != null) {
                s.append("\n")
                s.append(linea)
            }
            config.close()
            s.toString()
        } catch (e: Exception) {
            e.toString()
        }
    }

    // public static final void listaFiles(final String directorio) {
// try {
// final File[] ficheros = new File(directorio).listFiles();
// for (final File fichero : ficheros) {
// try {
// final short id = Short.parseShort(fichero.getName().split("_")[0]);
// final Mapa map = MundoDofus.getMapa(id);
// GestorSQL.INSERT_MAPA_AZENDAR(id, map.getFecha(), map.getAncho(), map.getAlto(),
// map.getCodigo(), map
// .getMapData(), map.getX(), map.getY(), (short) map.getSubArea().getID());
// } catch (final Exception e) {
// e.printStackTrace();
// }
// }
// } catch (final Exception e) {}
// }
//
// public static final void copy(final File src, final File dst) {
// try {
// final InputStream in = new FileInputStream(src);
// final OutputStream out = new FileOutputStream(dst);
// final byte[] buf = new byte[1024];
// int len;
// while ((len = in.read(buf)) > 0) {
// out.write(buf, 0, len);
// }
// in.close();
// out.close();
// } catch (final Exception e) {
// e.printStackTrace();
// }
// }
//
// public static final void copiarMapa(final short s) {
// try {
// final Mapa map = MundoDofus.getMapa(s);
// if (map != null) {
// final String swf = s + "_" + map.getFecha()
// + (map.getCodigo().equalsIgnoreCase("elbustaelmejor2012") ? "" : "X") + ".swf";
// copiarArchivos("C:/wamp/www/mibebito/maps/" + swf, "C:/wamp/www/mibebito/azendar/" +
// swf);
// }
// } catch (final Exception e) {}
// }
//
//
// public static final void copiarArchivos(final String dir1, final String dir2) {
// try {
// final File f1 = new File(dir1);
// final File f2 = new File(dir2);
// copy(f1, f2);
// System.out.println("Copio el file " + dir1);
// } catch (final Exception e) {
// e.printStackTrace();
// }
// }
    fun filtro(s: String): String {
        val r = StringBuilder()
        val filtros = charArrayOf('\'', '\"', '\\', '=', '#', '/', '!', '`', '+', '$', '%')
        for (x in s.toCharArray()) {
            var paso = true
            for (f in filtros) {
                if (x == f) {
                    paso = false
                    break
                }
            }
            if (!paso) {
                continue
            }
            r.append(x)
        }
        return r.toString()
    }

    fun getMapaInicioIncarnam(clase: Int): String {
        when (clase) {
            1 -> return "10300,337"
            2 -> return "10284,386"
            3 -> return "10299,300"
            4 -> return "10285,263"
            5 -> return "10298,315"
            6 -> return "10276,311"
            7 -> return "10283,299"
            8 -> return "10294,309"
            9 -> return "10292,299"
            10 -> return "10279,284"
            11 -> return "10296,258"
            12 -> return "10289,250"
        }
        return "7411,340"
    }

    fun getMapaInicioAstrub(clase: Int): String {
        when (clase) {
            1 -> return "7398,299"
            2 -> return "7545,311"
            3 -> return "7442,254"
            4 -> return "7392,282"
            5 -> return "7332,312"
            6 -> return "7446,299"
            7 -> return "7361,207"
            8 -> return "7427,267"
            9 -> return "7378,338"
            10 -> return "7395,371"
            11 -> return "7336,198"
            12 -> return "8035,384"
        }
        return "7411,340"
    }

    fun getTrabajosPorOI(oi: Int, array: ArrayList<Int>) {
        when (oi) {
            7019 -> noRepetirEnArray(array, 23)
            7013 -> {
                noRepetirEnArray(array, 17)
                noRepetirEnArray(array, 149)
                noRepetirEnArray(array, 148)
                noRepetirEnArray(array, 15)
                noRepetirEnArray(array, 16)
                noRepetirEnArray(array, 147)
            }
            7018 -> noRepetirEnArray(array, 110)
            7028 -> noRepetirEnArray(array, 151)
            7022 -> noRepetirEnArray(array, 135)
            7023 -> noRepetirEnArray(array, 134)
            7024 -> noRepetirEnArray(array, 133)
            7025 -> noRepetirEnArray(array, 132)
            7001 -> {
                noRepetirEnArray(array, 109)
                noRepetirEnArray(array, 27)
            }
            7016, 7014 -> noRepetirEnArray(array, 63)
            7015 -> {
                noRepetirEnArray(array, 123)
                noRepetirEnArray(array, 64)
            }
            7036 -> {
                noRepetirEnArray(array, 165)
                noRepetirEnArray(array, 166)
                noRepetirEnArray(array, 167)
            }
            7011 -> {
                noRepetirEnArray(array, 13)
                noRepetirEnArray(array, 14)
            }
            7037 -> {
                noRepetirEnArray(array, 163)
                noRepetirEnArray(array, 164)
            }
            7002 -> noRepetirEnArray(array, 32)
            7005 -> noRepetirEnArray(array, 48)
            7003 -> noRepetirEnArray(array, 101)
            7008, 7009, 7010 -> {
                noRepetirEnArray(array, 12)
                noRepetirEnArray(array, 11)
            }
            7039 -> {
                noRepetirEnArray(array, 182)
                noRepetirEnArray(array, 171)
            }
            7038 -> {
                noRepetirEnArray(array, 169)
                noRepetirEnArray(array, 168)
            }
            7007 -> {
                noRepetirEnArray(array, 47)
                noRepetirEnArray(array, 122)
            }
            7012 -> {
                noRepetirEnArray(array, 18)
                noRepetirEnArray(array, 19)
                noRepetirEnArray(array, 20)
                noRepetirEnArray(array, 21)
                noRepetirEnArray(array, 65)
                noRepetirEnArray(array, 66)
                noRepetirEnArray(array, 67)
                noRepetirEnArray(array, 142)
                noRepetirEnArray(array, 143)
                noRepetirEnArray(array, 144)
                noRepetirEnArray(array, 145)
                noRepetirEnArray(array, 146)
            }
            7020 -> {
                noRepetirEnArray(array, 1)
                noRepetirEnArray(array, 113)
                noRepetirEnArray(array, 115)
                noRepetirEnArray(array, 116)
                noRepetirEnArray(array, 117)
                noRepetirEnArray(array, 118)
                noRepetirEnArray(array, 119)
                noRepetirEnArray(array, 120)
            }
            7027 -> noRepetirEnArray(array, 156)
        }
    }
    var iptoken="144.91.112.154"
    var databasetoken="arriendos"
    var usertoken="token"
    var tokenpw="token52895390"

    private fun noRepetirEnArray(array: ArrayList<Int>, i: Int) {
        if (!array.contains(i)) {
            array.add(i)
            array.trimToSize()
        }
    }

    @JvmStatic
    fun trabajosOficioTaller(oficio: Int): String {
        when (oficio) {
            OFICIO_BASE -> return "22;110;121"
            OFICIO_LEÑADOR -> return "101"
            OFICIO_FORJADOR_ESPADAS -> return "145;20"
            OFICIO_ESCULTOR_ARCOS -> return "149;15"
            OFICIO_FORJADOR_MARTILLOS -> return "144;19"
            OFICIO_ZAPATERO -> return "14;13"
            OFICIO_JOYERO -> return "12;11"
            OFICIO_FORJADOR_DAGAS -> return "142;18"
            OFICIO_ESCULTOR_BASTONES -> return "147;17"
            OFICIO_ESCULTOR_VARITAS -> return "148;16"
            OFICIO_FORJADOR_PALAS -> return "146;21"
            OFICIO_MINERO -> return "48;32"
            OFICIO_PANADERO -> return "109;27"
            OFICIO_ALQUIMISTA -> return "23"
            OFICIO_SASTRE -> return "123;64;63"
            OFICIO_CAMPESINO -> return "47;122"
            OFICIO_FORJADOR_HACHAS -> return "143;65"
            OFICIO_PESCADOR -> return "133"
            OFICIO_CAZADOR -> return "132"
            OFICIO_FORJAMAGO_DAGAS -> return "1"
            OFICIO_FORJAMAGO_ESPADAS -> return "113"
            OFICIO_FORJAMAGO_MARTILLOS -> return "116"
            OFICIO_FORJAMAGO_PALAS -> return "117"
            OFICIO_FORJAMAGO_HACHAS -> return "115"
            OFICIO_ESCULTORMAGO_ARCOS -> return "118"
            OFICIO_ESCULTORMAGO_VARITAS -> return "119"
            OFICIO_ESCULTORMAGO_BASTONES -> return "120"
            OFICIO_CARNICERO -> return "134"
            OFICIO_PESCADERO -> return "135"
            OFICIO_FORJADOR_ESCUDOS -> return "156"
            OFICIO_ZAPATEROMAGO -> return "164;163"
            OFICIO_JOYEROMAGO -> return "169;168"
            OFICIO_SASTREMAGO -> return "167;166;165"
            OFICIO_MANITAS -> return "182;171"
        }
        return ""
    }

    // public static final int getMob
// 510 Guardia de Tierradala 120 34 2
// 512 Guardia de Fuegodala 120 34 2
// 507 Guardia de Akwadala 120 34 2
// 509 Guardia de Airedala 120 34 2
// 511 Guardia de Tierradala 120 34 1
// 513 Guardia de Fuegodala 120 34 1
// 504 Guardia de Akwadala 120 34 1
// 508 Guardia de Airedala 120 34 1
    fun distAgresionPorNivel(nivel: Int): Byte {
        return Math.min(10, nivel / 2000).toByte()
    }

    fun esUbicacionValidaObjeto(tipo: Int, pos: Int): Boolean {
        if (pos == OBJETO_POS_NO_EQUIPADO.toInt()) return true
        when (tipo) {
            OBJETO_TIPO_ESPECIALES -> if (pos >= 0 && pos <= 7 || pos == OBJETO_POS_ESCUDO.toInt()) {
                return true
            }
            OBJETO_TIPO_POCION_FORJAMAGIA, OBJETO_TIPO_RED_CAPTURA, OBJETO_TIPO_ARCO, OBJETO_TIPO_VARITA, OBJETO_TIPO_BASTON, OBJETO_TIPO_DAGAS, OBJETO_TIPO_ESPADA, OBJETO_TIPO_MARTILLO, OBJETO_TIPO_PALA, OBJETO_TIPO_HACHA, OBJETO_TIPO_HERRAMIENTA, OBJETO_TIPO_PICO, OBJETO_TIPO_GUADAÑA, OBJETO_TIPO_ARMA_MAGICA -> if (pos == OBJETO_POS_ARMA.toInt()) {
                return true
            }
            OBJETO_TIPO_OBJEVIVO -> if (pos >= 0 && pos <= 7) {
                return true
            }
            OBJETO_TIPO_AMULETO -> if (pos == OBJETO_POS_AMULETO.toInt()) {
                return true
            }
            OBJETO_TIPO_PIEDRA_DEL_ALMA -> if (pos == OBJETO_POS_ARMA.toInt() || pos >= 35 && pos <= 48) {
                return true
            }
            OBJETO_TIPO_ANILLO -> if (pos == OBJETO_POS_ANILLO1.toInt() || pos == OBJETO_POS_ANILLO_DERECHO.toInt()) {
                return true
            }
            OBJETO_TIPO_CINTURON -> if (pos == OBJETO_POS_CINTURON.toInt()) {
                return true
            }
            OBJETO_TIPO_BOTAS -> if (pos == OBJETO_POS_BOTAS.toInt()) {
                return true
            }
            OBJETO_TIPO_SOMBRERO -> if (pos == OBJETO_POS_SOMBRERO.toInt()) {
                return true
            }
            OBJETO_TIPO_CAPA, OBJETO_TIPO_MOCHILA -> if (pos == OBJETO_POS_CAPA.toInt()) {
                return true
            }
            OBJETO_TIPO_MASCOTA -> if (pos == OBJETO_POS_MASCOTA.toInt()) {
                return true
            }
            OBJETO_TIPO_DOFUS, OBJETO_TIPO_TROFEO -> if (pos == OBJETO_POS_DOFUS1.toInt() || pos == OBJETO_POS_DOFUS2.toInt() || pos == OBJETO_POS_DOFUS3.toInt() || pos == OBJETO_POS_DOFUS4.toInt() || pos == OBJETO_POS_DOFUS5.toInt() || pos == OBJETO_POS_DOFUS6.toInt()) {
                return true
            }
            OBJETO_TIPO_ESCUDO -> if (pos == OBJETO_POS_ESCUDO.toInt()) {
                return true
            }
            OBJETO_TIPO_POCION, OBJETO_TIPO_PERGAMINO_EXP, OBJETO_TIPO_ALIMENTO_BOOST, OBJETO_TIPO_PAN, OBJETO_TIPO_CERVEZA, OBJETO_TIPO_PEZ, OBJETO_TIPO_GOLOSINA, OBJETO_TIPO_PESCADO_COMESTIBLE, OBJETO_TIPO_CARNE, OBJETO_TIPO_CARNE_CONSERVADA, OBJETO_TIPO_CARNE_COMESTIBLE, OBJETO_TIPO_TINTE, OBJETO_TIPO_DOMINIO, OBJETO_TIPO_BEBIDA, OBJETO_TIPO_PIEDRA_DE_ALMA_LLENA, OBJETO_TIPO_PERGAMINO_BUSQUEDA, OBJETO_TIPO_REGALOS, OBJETO_TIPO_OBJETO_CRIA, OBJETO_TIPO_OBJETO_UTILIZABLE, OBJETO_TIPO_PRISMA, OBJETO_TIPO_HADA_ARTIFICIAL, OBJETO_TIPO_DONES, OBJETO_TIPO_ALIMENTO_MASCOTA, OBJETO_TIPO_PERGAMINO_HECHIZO -> if (pos >= 35 && pos <= 48) {
                return true
            }
            OBJETO_TIPO_COMPAÑERO -> if (pos == OBJETO_POS_COMPAÑERO.toInt()) {
                return true
            }
        }
        return false
    }

    fun getTrabajosPorOficios(
        idOficio: Int, nivel: Int,
        oficio: StatOficio?
    ): ArrayList<Trabajo> {
        val skills = ArrayList<Trabajo>()
        val tiempoGanado = Math.min(nivel, 100) * 100
        when (idOficio) {
            OFICIO_BASE -> {
                skills.add(Trabajo(SKILL_PELAR_PATATAS, 1, 1, true, 30, 0, oficio))
                skills.add(Trabajo(SKILL_UTILIZAR_BANCO, 1, 1, true, 30, 0, oficio))
                skills.add(Trabajo(SKILL_MACHACAR_RECURSOS, 3, 3, true, 30, 0, oficio))
                skills.add(Trabajo(SKILL_ROMPER_OBJETO, 7, 7, true, 30, 0, oficio))
            }
            OFICIO_JOYERO -> {
                skills.add(
                    Trabajo(
                        SKILL_CREAR_ANILLO,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
                skills.add(
                    Trabajo(
                        SKILL_CREAR_AMULETO,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
            }
            OFICIO_SASTRE -> {
                skills.add(
                    Trabajo(
                        SKILL_CREAR_SOMBRERO,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
                skills.add(
                    Trabajo(
                        SKILL_CREAR_CAPA,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
                skills.add(
                    Trabajo(
                        SKILL_CREAR_MOCHILA,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
            }
            OFICIO_ZAPATERO -> {
                skills.add(
                    Trabajo(
                        SKILL_CREAR_BOTAS,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
                skills.add(
                    Trabajo(
                        SKILL_CREAR_CINTURON,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
            }
            OFICIO_MANITAS -> {
                skills.add(
                    Trabajo(
                        SKILL_CREAR_CHAPUZA,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
                skills.add(
                    Trabajo(
                        SKILL_CREAR_LLAVE,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
            }
            OFICIO_ESCULTOR_ARCOS -> {
                skills.add(
                    Trabajo(
                        SKILL_CREAR_ARCO,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
                skills.add(Trabajo(SKILL_REPARAR_ARCO, 3, 3, true, nivel, 0, oficio))
            }
            OFICIO_ESCULTOR_VARITAS -> {
                skills.add(
                    Trabajo(
                        SKILL_CREAR_VARITA,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
                skills.add(Trabajo(SKILL_REPARAR_VARITA, 3, 3, true, nivel, 0, oficio))
            }
            OFICIO_ESCULTOR_BASTONES -> {
                skills.add(
                    Trabajo(
                        SKILL_CREAR_BASTON,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
                skills.add(Trabajo(SKILL_REPARAR_BASTON, 3, 3, true, nivel, 0, oficio))
            }
            OFICIO_FORJADOR_DAGAS -> {
                skills.add(
                    Trabajo(
                        SKILL_CREAR_DAGA,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
                skills.add(Trabajo(SKILL_REPARAR_DAGA, 3, 3, true, nivel, 0, oficio))
            }
            OFICIO_FORJADOR_MARTILLOS -> {
                skills.add(
                    Trabajo(
                        SKILL_CREAR_MARTILLO,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
                skills.add(Trabajo(SKILL_REPARAR_MARTILLO, 3, 3, true, nivel, 0, oficio))
            }
            OFICIO_FORJADOR_ESPADAS -> {
                skills.add(
                    Trabajo(
                        SKILL_CREAR_ESPADA,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
                skills.add(Trabajo(SKILL_REPARAR_ESPADA, 3, 3, true, nivel, 0, oficio))
            }
            OFICIO_FORJADOR_PALAS -> {
                skills.add(
                    Trabajo(
                        SKILL_CREAR_PALA,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
                skills.add(Trabajo(SKILL_REPARAR_PALA, 3, 3, true, nivel, 0, oficio))
            }
            OFICIO_FORJADOR_HACHAS -> {
                skills.add(
                    Trabajo(
                        SKILL_CREAR_HACHA,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
                skills.add(Trabajo(SKILL_REPARAR_HACHA, 3, 3, true, nivel, 0, oficio))
            }
            OFICIO_FORJADOR_ESCUDOS -> skills.add(
                Trabajo(
                    SKILL_CREAR_ESCUDO,
                    2,
                    getIngMaxPorNivel(nivel).toInt(),
                    true,
                    getSuerteMaxPorNivel(nivel),
                    -1,
                    oficio
                )
            )
            OFICIO_ZAPATEROMAGO -> {
                skills.add(Trabajo(SKILL_MAGUEAR_BOTAS, 3, 3, true, nivel, 0, oficio))
                skills.add(Trabajo(SKILL_MAGUEAR_CINTURON, 3, 3, true, nivel, 0, oficio))
            }
            OFICIO_JOYEROMAGO -> {
                skills.add(Trabajo(SKILL_MAGUEAR_AMULETO, 3, 3, true, nivel, 0, oficio))
                skills.add(Trabajo(SKILL_MAGUEAR_ANILLO, 3, 3, true, nivel, 0, oficio))
            }
            OFICIO_SASTREMAGO -> {
                skills.add(Trabajo(SKILL_MAGUEAR_SOMBRERO, 3, 3, true, nivel, 0, oficio))
                skills.add(Trabajo(SKILL_MAGUEAR_CAPA, 3, 3, true, nivel, 0, oficio))
                skills.add(Trabajo(SKILL_MAGUEAR_MOCHILA, 3, 3, true, nivel, 0, oficio))
            }
            OFICIO_ESCULTORMAGO_BASTONES -> skills.add(
                Trabajo(
                    SKILL_MAGUEAR_BASTON,
                    3,
                    3,
                    true,
                    nivel,
                    0,
                    oficio
                )
            )
            OFICIO_ESCULTORMAGO_VARITAS -> skills.add(
                Trabajo(
                    SKILL_MAGUEAR_VARITA,
                    3,
                    3,
                    true,
                    nivel,
                    0,
                    oficio
                )
            )
            OFICIO_ESCULTORMAGO_ARCOS -> skills.add(
                Trabajo(
                    SKILL_MAGUEAR_ARCO,
                    3,
                    3,
                    true,
                    nivel,
                    0,
                    oficio
                )
            )
            OFICIO_FORJAMAGO_HACHAS -> skills.add(
                Trabajo(
                    SKILL_MAGUEAR_HACHA,
                    3,
                    3,
                    true,
                    nivel,
                    0,
                    oficio
                )
            )
            OFICIO_FORJAMAGO_DAGAS -> skills.add(
                Trabajo(
                    SKILL_MAGUEAR_DAGA,
                    3,
                    3,
                    true,
                    nivel,
                    0,
                    oficio
                )
            )
            OFICIO_FORJAMAGO_ESPADAS -> skills.add(
                Trabajo(
                    SKILL_MAGUEAR_ESPADA,
                    3,
                    3,
                    true,
                    nivel,
                    0,
                    oficio
                )
            )
            OFICIO_FORJAMAGO_MARTILLOS -> skills.add(
                Trabajo(
                    SKILL_MAGUEAR_MARTILLO,
                    3,
                    3,
                    true,
                    nivel,
                    0,
                    oficio
                )
            )
            OFICIO_FORJAMAGO_PALAS -> skills.add(
                Trabajo(
                    SKILL_MAGUEAR_PALA,
                    3,
                    3,
                    true,
                    nivel,
                    0,
                    oficio
                )
            )
            OFICIO_CAZADOR -> skills.add(
                Trabajo(
                    SKILL_PREPARAR_ENCIMERA,
                    2,
                    getIngMaxPorNivel(nivel).toInt(),
                    true,
                    getSuerteMaxPorNivel(nivel),
                    -1,
                    oficio
                )
            )
            OFICIO_CARNICERO -> skills.add(
                Trabajo(
                    SKILL_PREPARAR_CARNE,
                    2,
                    getIngMaxPorNivel(nivel).toInt(),
                    true,
                    getSuerteMaxPorNivel(nivel),
                    -1,
                    oficio
                )
            )
            OFICIO_PESCADOR -> {
                if (nivel >= 75) {
                    skills.add(
                        Trabajo(
                            SKILL_PESCAR_PESCADOS_GIGANTES_MAR,
                            0,
                            1,
                            false,
                            12000 - tiempoGanado,
                            35,
                            oficio
                        )
                    )
                }
                if (nivel >= 70) {
                    skills.add(
                        Trabajo(
                            SKILL_PESCAR_PESCADOS_GIGANTES_RIO,
                            0,
                            1,
                            false,
                            12000 - tiempoGanado,
                            35,
                            oficio
                        )
                    )
                }
                if (nivel >= 50) {
                    skills.add(
                        Trabajo(
                            SKILL_PESCAR_PECES_GORDOS_MAR,
                            0,
                            1,
                            false,
                            12000 - tiempoGanado,
                            30,
                            oficio
                        )
                    )
                }
                if (nivel >= 40) {
                    skills.add(
                        Trabajo(
                            SKILL_PESCAR_PECES_GORDOS_RIO,
                            0,
                            1,
                            false,
                            12000 - tiempoGanado,
                            25,
                            oficio
                        )
                    )
                }
                if (nivel >= 20) {
                    skills.add(
                        Trabajo(
                            SKILL_PESCAR_PESCADOS_MAR,
                            0,
                            1,
                            false,
                            12000 - tiempoGanado,
                            20,
                            oficio
                        )
                    )
                }
                if (nivel >= 10) {
                    skills.add(
                        Trabajo(
                            SKILL_PESCAR_PESCADOS_RIO,
                            0,
                            1,
                            false,
                            12000 - tiempoGanado,
                            15,
                            oficio
                        )
                    )
                }
                if (nivel >= 0) {
                    skills.add(
                        Trabajo(
                            SKILL_PESCAR_SOMBRA_EXTRAÑA,
                            0,
                            1,
                            false,
                            12000 - tiempoGanado,
                            50,
                            oficio
                        )
                    )
                    skills.add(
                        Trabajo(
                            SKILL_PESCAR_PESCADITOS_RIO,
                            0,
                            1,
                            false,
                            12000 - tiempoGanado,
                            10,
                            oficio
                        )
                    )
                    skills.add(
                        Trabajo(
                            SKILL_PESCAR_PESCADITOS_MAR,
                            0,
                            1,
                            false,
                            12000 - tiempoGanado,
                            10,
                            oficio
                        )
                    )
                    skills.add(Trabajo(SKILL_PESCAR_PISCHI, 0, 1, false, 12000 - tiempoGanado, 5, oficio))
                }
                skills.add(
                    Trabajo(
                        SKILL_VACIAR_PESCADO,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
            }
            OFICIO_PESCADERO -> skills.add(
                Trabajo(
                    SKILL_PREPARAR_PESCADO,
                    2,
                    getIngMaxPorNivel(nivel).toInt(),
                    true,
                    getSuerteMaxPorNivel(nivel),
                    -1,
                    oficio
                )
            )
            OFICIO_PANADERO -> {
                skills.add(
                    Trabajo(
                        SKILL_HACER_PAN,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
                skills.add(Trabajo(SKILL_HACER_CARAMELOS, 3, 3, true, 100, -1, oficio))
            }
            OFICIO_MINERO -> {
                if (nivel >= 100) {
                    skills.add(
                        Trabajo(
                            SKILL_RECOLECTAR_DOLOMIA,
                            5 + 1,
                            5 + 2 + (nivel - 100) / 5,
                            false,
                            12000 - tiempoGanado,
                            60,
                            oficio
                        )
                    )
                }
                if (nivel >= 80) {
                    skills.add(
                        Trabajo(
                            SKILL_RECOLECTAR_ORO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 80) / 5,
                            false,
                            12000 - tiempoGanado,
                            55,
                            oficio
                        )
                    )
                }
                if (nivel >= 70) {
                    skills.add(
                        Trabajo(
                            SKILL_RECOLECTAR_BAUXITA,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2
                                    + (nivel - 70) / 5,
                            false,
                            12000 - tiempoGanado,
                            50,
                            oficio
                        )
                    )
                }
                if (nivel >= 60) {
                    skills.add(
                        Trabajo(
                            SKILL_RECOLECTAR_PLATA,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2
                                    + (nivel - 60) / 5,
                            false,
                            12000 - tiempoGanado,
                            40,
                            oficio
                        )
                    )
                }
                if (nivel >= 50) {
                    skills.add(
                        Trabajo(
                            SKILL_RECOLECTAR_ESTAÑO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2
                                    + (nivel - 50) / 5,
                            false,
                            12000 - tiempoGanado,
                            35,
                            oficio
                        )
                    )
                    skills.add(
                        Trabajo(
                            SKILL_RECOLECTAR_SILICATO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2
                                    + (nivel - 50) / 5,
                            false,
                            12000 - tiempoGanado,
                            35,
                            oficio
                        )
                    )
                }
                if (nivel >= 40) {
                    skills.add(
                        Trabajo(
                            SKILL_RECOLECTAR_MANGANESO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2
                                    + (nivel - 40) / 5,
                            false,
                            12000 - tiempoGanado,
                            30,
                            oficio
                        )
                    )
                }
                if (nivel >= 30) {
                    skills.add(
                        Trabajo(
                            SKILL_RECOLECTAR_KOBALTO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2
                                    + (nivel - 30) / 5,
                            false,
                            12000 - tiempoGanado,
                            25,
                            oficio
                        )
                    )
                }
                if (nivel >= 20) {
                    skills.add(
                        Trabajo(
                            SKILL_RECOLECTAR_BRONCE,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2
                                    + (nivel - 20) / 5,
                            false,
                            12000 - tiempoGanado,
                            20,
                            oficio
                        )
                    )
                }
                if (nivel >= 10) {
                    skills.add(
                        Trabajo(
                            SKILL_RECOLECTAR_COBRE,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2
                                    + (nivel - 10) / 5,
                            false,
                            12000 - tiempoGanado,
                            15,
                            oficio
                        )
                    )
                }
                if (nivel >= 0) {
                    skills.add(
                        Trabajo(
                            SKILL_RECOLECTAR_HIERRO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2
                                    + nivel / 5,
                            false,
                            12000 - tiempoGanado,
                            10,
                            oficio
                        )
                    )
                }
                skills.add(
                    Trabajo(
                        SKILL_FUNDIR,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
                skills.add(
                    Trabajo(
                        SKILL_PULIR,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
            }
            OFICIO_ALQUIMISTA -> {
                if (nivel >= 50) {
                    skills.add(
                        Trabajo(
                            SKILL_COSECHAR_PANDOJA,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2
                                    + (nivel - 50) / 5,
                            false,
                            12000 - tiempoGanado,
                            35,
                            oficio
                        )
                    )
                    skills.add(
                        Trabajo(
                            SKILL_COSECHAR_EDELWEISS,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2
                                    + (nivel - 50) / 5,
                            false,
                            12000 - tiempoGanado,
                            35,
                            oficio
                        )
                    )
                }
                if (nivel >= 40) {
                    skills.add(
                        Trabajo(
                            SKILL_COSECHAR_ORQUIDEA,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2
                                    + (nivel - 40) / 5,
                            false,
                            12000 - tiempoGanado,
                            30,
                            oficio
                        )
                    )
                }
                if (nivel >= 30) {
                    skills.add(
                        Trabajo(
                            SKILL_COSECHAR_MENTA_SALVAJE,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2
                                    + (nivel - 30) / 5,
                            false,
                            12000 - tiempoGanado,
                            25,
                            oficio
                        )
                    )
                }
                if (nivel >= 20) {
                    skills.add(
                        Trabajo(
                            SKILL_COSECHAR_TREBOL,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 20) / 5,
                            false,
                            12000 - tiempoGanado,
                            20,
                            oficio
                        )
                    )
                }
                if (nivel >= 10) {
                    skills.add(
                        Trabajo(
                            SKILL_COSECHAR_CAÑAMO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 10) / 5,
                            false,
                            12000 - tiempoGanado,
                            15,
                            oficio
                        )
                    )
                }
                if (nivel >= 0) {
                    skills.add(
                        Trabajo(
                            SKILL_COSECHAR_LINO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    / 5),
                            false,
                            12000 - tiempoGanado,
                            10,
                            oficio
                        )
                    )
                }
                skills.add(
                    Trabajo(
                        SKILL_CREAR_POCIMA,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
            }
            OFICIO_LEÑADOR -> {
                if (nivel >= 100) {
                    skills.add(
                        Trabajo(
                            SKILL_TALAR_BAMBU_SAGRADO,
                            5 + 1,
                            5 + 2 + (nivel - 100) / 5,
                            false,
                            12000 - tiempoGanado,
                            75,
                            oficio
                        )
                    )
                }
                if (nivel >= 90) {
                    skills.add(
                        Trabajo(
                            SKILL_TALAR_OLMO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 90) / 5,
                            false,
                            12000 - tiempoGanado,
                            70,
                            oficio
                        )
                    )
                }
                if (nivel >= 80) {
                    skills.add(
                        Trabajo(
                            SKILL_TALAR_CARPE,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 80) / 5,
                            false,
                            12000 - tiempoGanado,
                            65,
                            oficio
                        )
                    )
                    skills.add(
                        Trabajo(
                            SKILL_TALAR_BAMBU_OSCURO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2
                                    + (nivel - 80) / 5,
                            false,
                            12000 - tiempoGanado,
                            65,
                            oficio
                        )
                    )
                }
                if (nivel >= 70) {
                    skills.add(
                        Trabajo(
                            SKILL_TALAR_KALIPTO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 70) / 5,
                            false,
                            12000 - tiempoGanado,
                            55,
                            oficio
                        )
                    )
                    skills.add(
                        Trabajo(
                            SKILL_TALAR_EBANO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 70) / 5,
                            false,
                            12000 - tiempoGanado,
                            50,
                            oficio
                        )
                    )
                }
                if (nivel >= 60) {
                    skills.add(
                        Trabajo(
                            SKILL_TALAR_CEREZO_SILVESTRE,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2
                                    + (nivel - 60) / 5,
                            false,
                            12000 - tiempoGanado,
                            45,
                            oficio
                        )
                    )
                }
                if (nivel >= 50) {
                    skills.add(
                        Trabajo(
                            SKILL_TALAR_TEJO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 50) / 5,
                            false,
                            12000 - tiempoGanado,
                            40,
                            oficio
                        )
                    )
                    skills.add(
                        Trabajo(
                            SKILL_TALAR_BAMBU,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 50) / 5,
                            false,
                            12000 - tiempoGanado,
                            40,
                            oficio
                        )
                    )
                }
                if (nivel >= 40) {
                    skills.add(
                        Trabajo(
                            SKILL_TALAR_ARCE,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 40) / 5,
                            false,
                            12000 - tiempoGanado,
                            35,
                            oficio
                        )
                    )
                }
                if (nivel >= 35) {
                    skills.add(
                        Trabajo(
                            SKILL_TALAR_BOMBU,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 35) / 5,
                            false,
                            12000 - tiempoGanado,
                            30,
                            oficio
                        )
                    )
                    skills.add(
                        Trabajo(
                            SKILL_TALAR_OLIVIOLETA,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2
                                    + (nivel - 35) / 5,
                            false,
                            12000 - tiempoGanado,
                            30,
                            oficio
                        )
                    )
                }
                if (nivel >= 30) {
                    skills.add(
                        Trabajo(
                            SKILL_TALAR_ROBLE,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 30) / 5,
                            false,
                            12000 - tiempoGanado,
                            25,
                            oficio
                        )
                    )
                }
                if (nivel >= 20) {
                    skills.add(
                        Trabajo(
                            SKILL_TALAR_NOGAL,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 20) / 5,
                            false,
                            12000 - tiempoGanado,
                            20,
                            oficio
                        )
                    )
                }
                if (nivel >= 10) {
                    skills.add(
                        Trabajo(
                            SKILL_TALAR_CASTAÑO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 10) / 5,
                            false,
                            12000 - tiempoGanado,
                            15,
                            oficio
                        )
                    )
                }
                if (nivel >= 0) {
                    skills.add(
                        Trabajo(
                            SKILL_TALAR_FRESNO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    / 5),
                            false,
                            12000 - tiempoGanado,
                            10,
                            oficio
                        )
                    )
                }
                skills.add(
                    Trabajo(
                        SKILL_SERRAR,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
            }
            OFICIO_CAMPESINO -> {
                if (nivel >= 70) {
                    skills.add(
                        Trabajo(
                            SKILL_SEGAR_CAÑAMO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 70) / 5,
                            false,
                            12000 - tiempoGanado,
                            45,
                            oficio
                        )
                    )
                }
                if (nivel >= 60) {
                    skills.add(
                        Trabajo(
                            SKILL_SEGAR_MALTA,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 60) / 5,
                            false,
                            12000 - tiempoGanado,
                            40,
                            oficio
                        )
                    )
                }
                if (nivel >= 50) {
                    skills.add(
                        Trabajo(
                            SKILL_SEGAR_ARROZ,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 50) / 5,
                            false,
                            12000 - tiempoGanado,
                            35,
                            oficio
                        )
                    )
                    skills.add(
                        Trabajo(
                            SKILL_SEGAR_CENTENO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 50) / 5,
                            false,
                            12000 - tiempoGanado,
                            35,
                            oficio
                        )
                    )
                }
                if (nivel >= 40) {
                    skills.add(
                        Trabajo(
                            SKILL_SEGAR_LINO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 40) / 5,
                            false,
                            12000 - tiempoGanado,
                            30,
                            oficio
                        )
                    )
                }
                if (nivel >= 30) {
                    skills.add(
                        Trabajo(
                            SKILL_SEGAR_LUPULO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 30) / 5,
                            false,
                            12000 - tiempoGanado,
                            25,
                            oficio
                        )
                    )
                }
                if (nivel >= 20) {
                    skills.add(
                        Trabajo(
                            SKILL_SEGAR_AVENA,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 20) / 5,
                            false,
                            12000 - tiempoGanado,
                            20,
                            oficio
                        )
                    )
                }
                if (nivel >= 10) {
                    skills.add(
                        Trabajo(
                            SKILL_SEGAR_CEBADA,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    - 10) / 5,
                            false,
                            12000 - tiempoGanado,
                            15,
                            oficio
                        )
                    )
                }
                if (nivel >= 0) {
                    skills.add(
                        Trabajo(
                            SKILL_SEGAR_TRIGO,
                            (if (nivel >= 100) 5 else 0) + 1,
                            (if (nivel >= 100) 5 else 0) + 2 + (nivel
                                    / 5),
                            false,
                            12000 - tiempoGanado,
                            10,
                            oficio
                        )
                    )
                }
                skills.add(
                    Trabajo(
                        SKILL_MOLER,
                        2,
                        getIngMaxPorNivel(nivel).toInt(),
                        true,
                        getSuerteMaxPorNivel(nivel),
                        -1,
                        oficio
                    )
                )
                skills.add(Trabajo(SKILL_DESGRANAR, 1, 1, true, 100, -1, oficio))
            }
        }
        return skills
    }

    fun getIngMaxPorNivel(nivel: Int): Byte {
        if (nivel < 10) {
            return 2
        }
        return if (nivel >= AtlantaMain.NIVEL_MAX_OFICIO) {
            9
        } else (nivel / 20 + 3).toByte()
    }

    fun getSuerteMaxPorNivel(nivel: Int): Int {
        if (nivel < 10) {
            return 50
        }
        return if (nivel >= AtlantaMain.NIVEL_MAX_OFICIO) {
            100
        } else 55 + (nivel / 10f - 1).toInt() * 5
    }

    fun getSuerteNivelYSlots(nivel: Int, slots: Int): Int {
        return if (nivel < 10) {
            50
        } else Math.min(
            100f,
            getSuerteMaxPorNivel(nivel) * (getIngMaxPorNivel(nivel) / slots.toFloat())
        ).toInt() // 54 + 45 = 99 *
    }

    fun calculXpGanadaEnOficio(nivel: Int, nroCasillas: Int): Int {
        if (nivel == AtlantaMain.NIVEL_MAX_OFICIO) {
            return 0
        }
        when (nroCasillas) {
            1 -> return 3
            2 -> return 10
            3 -> {
                return if (nivel > 9) {
                    25
                } else 0
            }
            4 -> {
                return if (nivel > 19) {
                    50
                } else 0
            }
            5 -> {
                return if (nivel > 39) {
                    100
                } else 0
            }
            6 -> {
                return if (nivel > 59) {
                    250
                } else 0
            }
            7 -> {
                return if (nivel > 79) {
                    500
                } else 0
            }
            8 -> {
                return if (nivel > 99) {
                    1000
                } else 0
            }
        }
        return 0
    }

    fun getCarnePorMob(mob: Int, nivel: Int): Int {
        var carne = -1
        val azar = getRandomInt(1, 100)
        if (nivel >= 1) {
            when (mob) {
                61, 974 -> {
                    if (azar <= 60) carne = 1915 // Carne de insecto
                    return carne
                }
            }
            when (mob) {
                31, 412 -> {
                    if (azar <= 50) carne = 1898 // Carne de larva
                    return carne
                }
            }
            when (mob) {
                98, 473, 803, 808, 796, 1012, 382, 806, 2357, 804, 807 -> {
                    if (azar <= 70) carne = 1896 // Carne de pájaro
                    return carne
                }
            }
        }
        if (nivel >= 10) {
            when (mob) {
                414, 34 -> {
                    if (azar <= 55) carne = 1899 // Carne de larva **
                    return carne
                }
            }
            when (mob) {
                456, 46, 976, 413 -> {
                    if (azar <= 50) carne = 1900 // Carne de larva ***
                    return carne
                }
            }
            when (mob) {
                134, 149 -> {
                    if (azar <= 50) carne = 1911 // Muslo de jalatín
                    return carne
                }
            }
        }
        if (nivel >= 20) {
            if (mob == 101) { // Jalatín Blanco
                if (azar <= 48) carne = 1912 // Muslo de jalató **
                return carne
            }
            when (mob) {
                96, 72 -> {
                    if (azar <= 48) carne = 1902 // Muslo de wabbit
                    return carne
                }
            }
        }
        if (nivel >= 30) {
            if (mob == 289) { // Maestro Cuerbok
                if (azar <= 10) carne = 1933 // Carne de pájaro ****
                return carne
            }
            if (mob == 159) { // Minilubo
                if (azar <= 45) carne = 1927 // Hocico
                return carne
            }
            when (mob) {
                104, 2769 -> {
                    if (azar <= 45) carne = 1917 // Solomillo de cerdo
                    return carne
                }
            }
        }
        if (nivel >= 40) {
            when (mob) {
                84, 83, 235, 81 -> {
                    if (azar <= 45) carne = 1897 // Carne de pájaro **
                    return carne
                }
            }
            if (mob == 171) { // Dragopavo Almendrado Salvaje
                if (azar <= 40) carne = 1922 // DragoCarne
                return carne
            }
            if (mob == 148) { // Jefe de Guerra Jalató
                if (azar <= 40) carne = 1913 // Muslo de jalató ***
                return carne
            }
            when (mob) {
                68, 64 -> {
                    if (azar <= 40) carne = 1903 // Muslo de wabbit **
                    return carne
                }
            }
        }
        if (nivel >= 50) {
            when (mob) {
                198, 241, 194, 240 -> {
                    if (azar <= 30) carne = 1916 // Carne de insecto **
                    return carne
                }
            }
            if (mob == 102) { // Mediulubo
                if (azar <= 35) carne = 1929 // Hocico **
                return carne
            }
            if (mob == 147) { // Jalató Real
                if (azar <= 30) carne = 1914 // Muslo de jalató ****
                return carne
            }
            if (mob == 297) { // Jabalí de las llanuras
                if (azar <= 30) carne = 1918 // Solomillo de cerdo **
                return carne
            }
        }
        if (nivel >= 60) {
            when (mob) {
                749, 759, 752, 785, 753, 751, 748, 754, 756, 744, 758, 755, 760, 761 -> {
                    if (azar <= 25) carne = 8499 // Carne de koalak
                    return carne
                }
            }
            if (mob == 200) { // Dragopavo Pelirrojo Salvaje
                if (azar <= 30) carne = 1923 // DragoCarne **
                return carne
            }
            when (mob) {
                99, 97 -> {
                    if (azar <= 30) carne = 1905 // Muslo de wabbit ***
                    return carne
                }
            }
            if (mob == 123) { // Cochinillo
                if (azar <= 30) carne = 1921 // Solomillo de cerdo ***
                return carne
            }
        }
        if (nivel >= 70) {
            when (mob) {
                261, 263 -> {
                    if (azar <= 15) carne = 8500 // Carne de crocodail
                    return carne
                }
            }
            when (mob) {
                76, 93, 90, 88, 95, 170, 87, 94, 75, 853, 89, 91, 82, 862 -> {
                    if (azar <= 10) carne = 1924 // Dragocarne ***
                    return carne
                }
            }
            if (mob == 232) { // Maxilubo
                if (azar <= 10) carne = 1930 // Hocico ***
                return carne
            }
        }
        if (nivel >= 80) {
            if (mob == 287) { // Kanugro
                if (azar <= 5) carne = 8498 // Carne de kanugro
                return carne
            }
            if (mob == 854) { // Crocabulia
                if (azar <= 2) carne = 1926 // Dragocarne ****
                return carne
            }
            when (mob) {
                180, 1015 -> {
                    if (azar <= 2) carne = 1901 // Muslo de wabbit ****
                    return carne
                }
            }
            if (mob == 113) { // Dragocerdo
                if (azar <= 2) carne = 1919 // Solomillo de cerdo****
                return carne
            }
        }
        return carne
    }

    @JvmStatic
    fun getZonaEfectoArma(tipo: Int): String {
        when (tipo) {
            OBJETO_TIPO_MARTILLO -> return "Xb"
            OBJETO_TIPO_BASTON -> return "Tb"
            OBJETO_TIPO_BALLESTA -> return "Lc"
            OBJETO_TIPO_HACHA, OBJETO_TIPO_HERRAMIENTA, OBJETO_TIPO_PICO, OBJETO_TIPO_GUADAÑA, OBJETO_TIPO_ARCO, OBJETO_TIPO_VARITA, OBJETO_TIPO_DAGAS, OBJETO_TIPO_ESPADA, OBJETO_TIPO_PALA, OBJETO_TIPO_ARMA_MAGICA -> return "Pa"
        }
        return "Pa"
    }

    fun esTrabajo(trabajoID: Int): Boolean {
        for (element in TRABAJOS_Y_DROPS) {
            if (element[0] == trabajoID) {
                return true
            }
        }
        return false
    }

    fun getTipoObjPermitidoEnTrabajo(trabajoID: Int, tipo: Int): Boolean {
        try {
            for (element in TRABAJOS_Y_DROPS) {
                if (element[0] == trabajoID) {
                    if (element.size == 1) {
                        return false
                    }
                    for (i in element) {
                        if (i == tipo) {
                            return false
                        }
                    }
                    return true
                }
            }
        } catch (ignored: Exception) {
        }
        return true
    }

    fun getObjetoPorRecurso(recurso: Int, especial: Boolean): Int {
        try {
            val obj = ArrayList<ArrayList<Int>>()
            for (element in TRABAJOS_Y_DROPS) {
                if (element[0] == recurso) {
                    if (element.size == 1) {
                        continue
                    }
                    val x = ArrayList<Int>()
                    x.add(element[1])
                    if (element.size > 2) {
                        x.add(element[2])
                    }
                    obj.add(x)
                }
            }
            return if (obj.isEmpty()) {
                -1
            } else if (obj.size == 1) {
                if (obj[0].size > 1 && especial) obj[0][1] else obj[0][0]
            } else {
                obj.size
                var z = ArrayList<Int>()
                z = obj[getRandomInt(0, obj.size - 1)]
                if (z.size > 1 && especial) z[1] else z[0]
            }
        } catch (e: Exception) {
            redactarLogServidorln("EXCEPTION getObjetoPorRecurso $e")
            e.printStackTrace()
        }
        return -1
    }

    // public static final int getSuertePorNroCasillaYNivel(final int nivel, final int nroCasilla) {
// if (nroCasilla <= getIngMaxPorNivel(nivel) - 2) {
// return 100;
// }
// return getSuerteNivelYSlots(nivel, nroCasilla);
// }
    @JvmStatic
    fun esOficioMago(id: Int): Boolean {
        when (id) {
            OFICIO_FORJAMAGO_DAGAS, OFICIO_FORJAMAGO_ESPADAS, OFICIO_FORJAMAGO_MARTILLOS, OFICIO_FORJAMAGO_PALAS, OFICIO_FORJAMAGO_HACHAS, OFICIO_ESCULTORMAGO_ARCOS, OFICIO_ESCULTORMAGO_VARITAS, OFICIO_ESCULTORMAGO_BASTONES, OFICIO_ZAPATEROMAGO, OFICIO_JOYEROMAGO, OFICIO_SASTREMAGO -> return true
        }
        return false
    }

    fun esSkillMago(id: Int): Boolean {
        when (id) {
            SKILL_MAGUEAR_AMULETO, SKILL_MAGUEAR_ANILLO, SKILL_MAGUEAR_ARCO, SKILL_MAGUEAR_BASTON, SKILL_MAGUEAR_BOTAS, SKILL_MAGUEAR_CAPA, SKILL_MAGUEAR_CINTURON, SKILL_MAGUEAR_DAGA, SKILL_MAGUEAR_ESPADA, SKILL_MAGUEAR_HACHA, SKILL_MAGUEAR_MARTILLO, SKILL_MAGUEAR_MOCHILA, SKILL_MAGUEAR_PALA, SKILL_MAGUEAR_SOMBRERO, SKILL_MAGUEAR_VARITA -> return true
        }
        return false
    }

    fun getColorMonturaPorMob(mob: Int): Int {
        return when (mob) {
            666 -> 74
            171 -> 1
            200 -> 6
            else -> -1
        }
    }

    @JvmStatic
    fun getColorCria(color1: Int, color2: Int, gen1: Boolean, gen2: Boolean): Int {
        var color1 = color1
        var color2 = color2
        var A = 0
        var B = 0
        var colorNuevaCria = 0
        when (color1) {
            1 -> color1 = 20
            6 -> color1 = 10
            74 -> color1 = 18
        }
        when (color2) {
            1 -> color2 = 20
            6 -> color2 = 10
            74 -> color2 = 18
        }
        if (color1 == 75 || color1 >= 88) {
            color1 = 10
        }
        if (color2 == 75) {
            color2 = 10
        }
        if (color1 > color2) {
            A = color2 // menor
            B = color1 // mayor
        } else if (color1 <= color2) {
            A = color1 // menor
            B = color2 // mayor
        }
        if (A == 10 && B == 18) {
            colorNuevaCria = 46 // pelirrojo y dorado
        } else if (A == 10 && B == 20) {
            colorNuevaCria = 38 // pelirrojo y almendrado
        } else if (A == 18 && B == 20) {
            colorNuevaCria = 33 // dorado y almendrado
        } else if (A == 33 && B == 38) {
            colorNuevaCria = 17 // indigo
        } else if (A == 33 && B == 46) {
            colorNuevaCria = 3 // ebano
        } else if (A == 10 && B == 17) {
            colorNuevaCria = 62 // pelirrojo e indigo
        } else if (A == 17 && B == 20) {
            colorNuevaCria = 36 // almendrado - indigo
        } else if (A == 3 && B == 20) {
            colorNuevaCria = 34 // almendrado - ebano
        } else if (A == 17 && B == 18) {
            colorNuevaCria = 44 // dorado - indigo
        } else if (A == 3 && B == 18) {
            colorNuevaCria = 42 // dorado - ebano
        } else if (A == 3 && B == 17) {
            colorNuevaCria = 51 // ebano - indigo
        } else if (A == 38 && B == 51) {
            colorNuevaCria = 19 // purpura
        } else if (A == 46 && B == 51) {
            colorNuevaCria = 22 // orquideo
        } else if (A == 10 && B == 19) {
            colorNuevaCria = 71 // purpura - pelirrojo
        } else if (A == 10 && B == 22) {
            colorNuevaCria = 70 // orquideo - pelirrojo
        } else if (A == 19 && B == 20) {
            colorNuevaCria = 41 // almendrado - purpura
        } else if (A == 20 && B == 22) {
            colorNuevaCria = 40 // almendrado - orquideo
        } else if (A == 18 && B == 19) {
            colorNuevaCria = 49 // dorado - purpura
        } else if (A == 18 && B == 22) {
            colorNuevaCria = 48 // dorado - orquideo
        } else if (A == 17 && B == 19) {
            colorNuevaCria = 65 // indigo - purpura
        } else if (A == 17 && B == 22) {
            colorNuevaCria = 64 // indigo - orquideo
        } else if (A == 3 && B == 19) {
            colorNuevaCria = 54 // ebano - purpura
        } else if (A == 3 && B == 22) {
            colorNuevaCria = 53 // ebano - orquideo
        } else if (A == 19 && B == 22) {
            colorNuevaCria = 76 // orquideo - purpura
        } else if (A == 53 && B == 76) {
            colorNuevaCria = 15 // turquesa
        } else if (A == 65 && B == 76) {
            colorNuevaCria = 16 // marfil
        } else if (A == 10 && B == 16) {
            colorNuevaCria = 11 // marfil - pelirrojo
        } else if (A == 10 && B == 15) {
            colorNuevaCria = 69 // turquesa - pelirrojo
        } else if (A == 16 && B == 20) {
            colorNuevaCria = 37 // almendrado - marfil
        } else if (A == 15 && B == 20) {
            colorNuevaCria = 39 // almendrado - turquesa
        } else if (A == 16 && B == 18) {
            colorNuevaCria = 45 // dorado - marfil
        } else if (A == 15 && B == 18) {
            colorNuevaCria = 47 // dorado - turquesa
        } else if (A == 16 && B == 17) {
            colorNuevaCria = 61 // indigo - marfil
        } else if (A == 15 && B == 17) {
            colorNuevaCria = 63 // indigo - turquesa
        } else if (A == 3 && B == 16) {
            colorNuevaCria = 9 // ebano - marfil
        } else if (A == 3 && B == 15) {
            colorNuevaCria = 52 // ebano - turquesa
        } else if (A == 16 && B == 19) {
            colorNuevaCria = 68 // marfil - purpura
        } else if (A == 15 && B == 19) {
            colorNuevaCria = 73 // turquesa - purpura
        } else if (A == 16 && B == 22) {
            colorNuevaCria = 67 // marfil - orquideo
        } else if (A == 15 && B == 22) {
            colorNuevaCria = 72 // orquideo - turquesa
        } else if (A == 15 && B == 16) {
            colorNuevaCria = 66 // marfil - turquesa
        } else if (A == 66 && B == 68) {
            colorNuevaCria = 21 // esmeralda
        } else if (A == 66 && B == 72) {
            colorNuevaCria = 23 // ciruela
        } else if (A == 10 && B == 21) {
            colorNuevaCria = 57 // esmeralda - pelirrojo
        } else if (A == 20 && B == 21) {
            colorNuevaCria = 35 // almendrado - esmeralda
        } else if (A == 18 && B == 21) {
            colorNuevaCria = 43 // dorado - esmeralda
        } else if (A == 3 && B == 21) {
            colorNuevaCria = 50 // ébano - esmeralda
        } else if (A == 17 && B == 21) {
            colorNuevaCria = 55 // esmeralda e índigo
        } else if (A == 16 && B == 21) {
            colorNuevaCria = 56 // esmeralda - marfil
        } else if (A == 15 && B == 21) {
            colorNuevaCria = 58 // esmeralda - turquesa
        } else if (A == 21 && B == 22) {
            colorNuevaCria = 59 // esmeralda - orquídeo
        } else if (A == 19 && B == 21) {
            colorNuevaCria = 60 // esmeralda - púrpura
        } else if (A == 20 && B == 23) {
            colorNuevaCria = 77 // ciruela - almendrado
        } else if (A == 18 && B == 23) {
            colorNuevaCria = 78 // ciruela - dorado
        } else if (A == 3 && B == 23) {
            colorNuevaCria = 79 // ciruela - ébano
        } else if (A == 21 && B == 23) {
            colorNuevaCria = 80 // ciruela - esmeralda
        } else if (A == 17 && B == 23) {
            colorNuevaCria = 82 // ciruela - índigo
        } else if (A == 16 && B == 23) {
            colorNuevaCria = 83 // ciruela - marfil
        } else if (A == 10 && B == 23) {
            colorNuevaCria = 84 // ciruela - pelirrojo
        } else if (A == 15 && B == 23) {
            colorNuevaCria = 85 // ciruela - turquesa
        } else if (A == 22 && B == 23) {
            colorNuevaCria = 86 // ciruela - orquídeo
        } else if (A == 19 && B == 23) {
            colorNuevaCria = 87 // ciruela - purpura
        }
        val posibles = ArrayList<Int>()
        posibles.add(10)
        posibles.add(18)
        posibles.add(20)
        posibles.add(A)
        posibles.add(B)
        if (gen1) {
            posibles.add(color1)
            posibles.add(color1)
        }
        if (gen2) {
            posibles.add(color2)
            posibles.add(color2)
        }
        val montura = Mundo.getMonturaModelo(colorNuevaCria)
        if (colorNuevaCria > 0) {
            if (montura != null) {
                for (j in 11 downTo montura.generacionID + 1) {
                    posibles.add(colorNuevaCria)
                }
            }
        }
        return posibles[getRandomInt(0, posibles.size - 1)]
    }

    fun getColorMonturaPorCertificado(certificado: Int): Int {
        for (montura in Mundo.MONTURAS_MODELOS.values) {
            if (montura.certificadoModeloID == certificado) return montura.colorID
        }
        return -1
    }

    fun getIDTituloOficio(oficio: Int): Byte {
        when (oficio) {
            OFICIO_FORJAMAGO_DAGAS -> return 40
            OFICIO_FORJAMAGO_ESPADAS -> return 41
            OFICIO_FORJAMAGO_MARTILLOS -> return 42
            OFICIO_FORJAMAGO_PALAS -> return 43
            OFICIO_FORJAMAGO_HACHAS -> return 44
            OFICIO_ESCULTORMAGO_ARCOS -> return 45
            OFICIO_ESCULTORMAGO_VARITAS -> return 46
            OFICIO_ESCULTORMAGO_BASTONES -> return 47
            OFICIO_ZAPATEROMAGO -> return 51
            OFICIO_JOYEROMAGO -> return 52
            OFICIO_SASTREMAGO -> return 53
            OFICIO_LEÑADOR -> return 22
            OFICIO_FORJADOR_ESPADAS -> return 23
            OFICIO_ESCULTOR_ARCOS -> return 24
            OFICIO_FORJADOR_MARTILLOS -> return 25
            OFICIO_ZAPATERO -> return 26
            OFICIO_JOYERO -> return 27
            OFICIO_FORJADOR_DAGAS -> return 28
            OFICIO_ESCULTOR_BASTONES -> return 29
            OFICIO_ESCULTOR_VARITAS -> return 30
            OFICIO_FORJADOR_PALAS -> return 31
            OFICIO_MINERO -> return 32
            OFICIO_PANADERO -> return 33
            OFICIO_ALQUIMISTA -> return 34
            OFICIO_SASTRE -> return 35
            OFICIO_CAMPESINO -> return 36
            OFICIO_FORJADOR_HACHAS -> return 37
            OFICIO_PESCADOR -> return 38
            OFICIO_CAZADOR -> return 39
            OFICIO_CARNICERO -> return 48
            OFICIO_PESCADERO -> return 49
            OFICIO_MANITAS -> return 54
            OFICIO_FORJADOR_ESCUDOS -> return 50
        }
        return 0
    }

    fun esAlimentoMontura(tipo: Short): Boolean {
        for (t in AtlantaMain.TIPO_ALIMENTO_MONTURA) {
            if (tipo == t) {
                return true
            }
        }
        return false
    }

    fun getReto(reto: Byte, pelea: Pelea): Reto {
        var bonusXPGrupo = 0
        var bonusXPFijo = 0
        when (reto) {
            RETO_ZOMBI -> {
                bonusXPFijo = 30
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(1) / 4f.toDouble()) * 10).toInt()
            }
            RETO_ESTATUA -> {
                // empezaste, hasta que acabe el combate.
                bonusXPFijo = 25
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(1) / 3f.toDouble()) * 10).toInt()
            }
            RETO_ELEGIDO_VOLUNTARIO -> {
                bonusXPFijo = 30
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(2) / 3f.toDouble()) * 10).toInt()
            }
            RETO_APLAZAMIENTO -> {
                bonusXPFijo = 20
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(2) / 1.5f.toDouble()) * 5).toInt()
            }
            RETO_AHORRADOR -> {
                // personaje sólo debe utilizar la misma acción una única vez.
                bonusXPFijo = 160
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(1) / 1.5f.toDouble()) * 10).toInt()
            }
            RETO_VERSATIL, RETO_LIMITADO -> {
                // a cuerpo durante el tiempo que dure el combate.
// una vez la misma acción.
                bonusXPFijo = 50
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(1) / 1.5f.toDouble()) * 5).toInt()
            }
            RETO_NOMADA -> {
                // tus PM disponibles en cada turno.
                bonusXPFijo = 20
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(1) / 1.5f.toDouble()) * 5).toInt()
            }
            RETO_BARBARO -> {
                // este combate.
                bonusXPFijo = 60
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(1) / 3f.toDouble()) * 5).toInt()
            }
            RETO_CRUEL -> {
                // nivel.
                bonusXPFijo = 40
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(2) / 2f.toDouble()) * 10).toInt()
            }
            RETO_MISTICO, RETO_ORDENADO -> {
                // descendiente de nivel.
// solamente hechizos.
                bonusXPFijo = 40
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(2) / 2f.toDouble()) * 5).toInt()
            }
            RETO_ENTOMOLOGO -> {
                // disponible. Válido durante todo el combate.
                bonusXPFijo = 25
                for (luchador in pelea.luchadoresDeEquipo(1)) {
                    if (luchador.personaje!!.tieneHechizoID(311)) {
                        bonusXPGrupo += 5
                    }
                }
            }
            RETO_INTOCABLE -> {
                // dure el combate.
                bonusXPFijo = 40
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(1) / 1.5f.toDouble()) * 10).toInt()
            }
            RETO_INCURABLE -> {
                bonusXPFijo = 20
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(1) / 2f.toDouble()) * 5).toInt()
            }
            RETO_MANOS_LIMPIAS -> {
                // daños
// directos durante lo que dure el combate. Se
// puede utilizar trampas, glifos, venenos,
// daños
// ocasionados por desplazamiento así como los
// ataques de invocaciones.
                bonusXPFijo = 25
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(2).toDouble()) * 5).toInt()
            }
            RETO_ELEMENTAL -> {
                // el combate.
                bonusXPFijo = 30
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(1) / 2f.toDouble()) * 5).toInt()
            }
            RETO_CIRCULEN ->  // combate.
                bonusXPFijo = 20
            RETO_EL_TIEMPO_PASA, RETO_PUSILANIME, RETO_SUPERVIVIENTE ->  // adyacente a la de uno de tus adversarios.
// tiempo que dure el combate.
                bonusXPFijo = 30
            RETO_PERDIDO_DE_VISTA ->  // durante el tiempo que dure el combate.
                bonusXPFijo = 15
            RETO_NI_PIAS_NI_SUMISAS, RETO_NI_PIOS_NI_SUMISOS ->  // dejar que los de sexo masculino acaben
// con los adversarios. Válido durante todo
// el combate.
                bonusXPFijo = 35
            RETO_LOS_PEQUEÑOS_ANTES, RETO_DUELO, RETO_PEGAJOSO ->  // de uno de tus aliados.
// personaje debe atacar a ese mismo adversario durante el tiempo que dure el combate.
// con los adversarios.
                bonusXPFijo = 40
            RETO_FOCALIZACION -> {
                // matarlo antes de atacar a otro adversario.
                bonusXPFijo = 30
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(2) / 2f.toDouble()) * 5).toInt()
            }
            RETO_ELITISTA -> {
                // muera.
                bonusXPFijo = 50
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(2) / 3f.toDouble()) * 5).toInt()
            }
            RETO_IMPREVISIBLE -> {
                // objetivo que se designa en cada turno de un personaje.
                bonusXPFijo = 50
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(2) / 2f.toDouble()) * 5).toInt()
            }
            RETO_ASESINO_A_SUELDO -> {
                // indicado. Cada vez que mates a un objetivo, obtendrás el nombre del próximo al que tienes
// que matar
                bonusXPFijo = 45
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(2) / 3f.toDouble()) * 10).toInt()
            }
            RETO_AUDAZ ->  // uno de tus adversarios.
                bonusXPFijo = 25
            RETO_BLITZKRIEG -> {
                // antes de que comience su turno.
                bonusXPFijo = 80
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(2) / 0.8f.toDouble()) * 5).toInt()
            }
            RETO_ANACORETA -> {
                // adyacente a la de uno de tus aliados.
                bonusXPFijo = 20
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(1) / 4f.toDouble()) * 5).toInt()
            }
            RETO_IMPETUOSO ->  // tu turno.
                bonusXPFijo = 10
            RETO_EL_DOS_POR_UNO -> {
                // tiene que matar obligatoriamente a un (y sólo a un) segundo adversario durante su turno.
                bonusXPFijo = 60
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(2) / 1.5f.toDouble()) * 5).toInt()
            }
            RETO_ABNEGACION -> {
                bonusXPFijo = 20
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(1) / 2f.toDouble()) * 5).toInt()
            }
            RETO_REPARTO ->  // (que no sea una invocación) durante el combate.
                bonusXPFijo = 50
            RETO_CADA_UNO_CON_SU_MONSTRUO -> {
                // a un adversario durante el combate. Cuando un personaje ataca a un adversario, ningún
// otro personaje puede atacar a ese mismo adversario durante el combate.
                bonusXPFijo = 60
                bonusXPGrupo = (Math.ceil(pelea.cantLuchDeEquipo(2) / 1.5f.toDouble()) * 5).toInt()
            }
            RETO_CONTAMINACION ->  // dispones de 3 turnos para rematar a tu aliado o ¡ganar el combate!
                bonusXPFijo = 60
            RETO_LOS_PERSONAJES_SECUNDARIOS_PRIMERO -> {
                // de menor nivel tiene que matar a todos los adversarios (así aprenderás a querer abusar
// como un puerkazo).
                bonusXPFijo = 10
                bonusXPGrupo = pelea.cantLuchDeEquipo(1) / 2 * 15
            }
            RETO_PROTEJAN_A_SUS_PERSONAJES_SECUNDARIOS -> {
                // secundarios deben acabar vivos el combate.
                bonusXPFijo = 20
                bonusXPGrupo = pelea.cantLuchDeEquipo(1) / 2 * 15
            }
            RETO_LA_TRAMPA_DE_LOS_DESARROLLADORES ->  // ganar el desafío. Ya se sabe, los desarrolladores del juego son unos tramposos...
                bonusXPFijo = 1
            RETO_JARDINERO -> {
                // pueda
                bonusXPFijo = 25
                for (luchador in pelea.luchadoresDeEquipo(1)) {
                    if (luchador.personaje!!.tieneHechizoID(367)) {
                        bonusXPGrupo += 5
                    }
                }
            }
            RETO_SEPULTURERO -> {
                bonusXPFijo = 30
                for (luchador in pelea.luchadoresDeEquipo(1)) {
                    if (luchador.personaje!!.tieneHechizoID(373)) {
                        bonusXPGrupo += 5
                    }
                }
            }
            RETO_CASINO_REAL -> {
                bonusXPFijo = 30
                for (luchador in pelea.luchadoresDeEquipo(1)) {
                    if (luchador.personaje!!.tieneHechizoID(101)) {
                        bonusXPGrupo += 5
                    }
                }
            }
            RETO_ARACNOFILO -> {
                bonusXPFijo = 10
                for (luchador in pelea.luchadoresDeEquipo(1)) {
                    if (luchador.personaje!!.tieneHechizoID(370)) {
                        bonusXPGrupo += 5
                    }
                }
            }
        }
        return Reto(reto, bonusXPFijo, bonusXPGrupo, bonusXPFijo, bonusXPGrupo, pelea)
    }

    fun esRetoPosible1(reto: Int, pelea: Pelea): Boolean {
        return try {
            when (reto.toByte()) {
                RETO_LOS_PERSONAJES_SECUNDARIOS_PRIMERO, RETO_PROTEJAN_A_SUS_PERSONAJES_SECUNDARIOS -> return false
                RETO_JARDINERO -> {
                    // pueda
                    for (luchador in pelea.luchadoresDeEquipo(1)) {
                        if (luchador.personaje!!.tieneHechizoID(367)) {
                            return true
                        }
                    }
                    return false
                }
                RETO_SEPULTURERO -> {
                    for (luchador in pelea.luchadoresDeEquipo(1)) {
                        if (luchador.personaje!!.tieneHechizoID(373)) {
                            return true
                        }
                    }
                    return false
                }
                RETO_CASINO_REAL -> {
                    for (luchador in pelea.luchadoresDeEquipo(1)) {
                        if (luchador.personaje!!.tieneHechizoID(101)) {
                            return true
                        }
                    }
                    return false
                }
                RETO_ENTOMOLOGO -> {
                    for (luchador in pelea.luchadoresDeEquipo(1)) {
                        if (luchador.personaje!!.tieneHechizoID(311)) {
                            return true
                        }
                    }
                    return false
                }
                RETO_ARACNOFILO -> {
                    for (luchador in pelea.luchadoresDeEquipo(1)) {
                        if (luchador.personaje!!.tieneHechizoID(370)) {
                            return true
                        }
                    }
                    return false
                }
                RETO_NI_PIAS_NI_SUMISAS, RETO_NI_PIOS_NI_SUMISOS -> {
                    // mobs
                    var masc = 0
                    var fem = 0
                    for (luchador in pelea.luchadoresDeEquipo(1)) {
                        if (luchador.personaje == null) continue
                        if (luchador.personaje!!.sexo.toInt() == 1) {
                            fem++
                        } else {
                            masc++
                        }
                    }
                    return fem > 0 && masc > 0
                }
                RETO_CRUEL, RETO_FOCALIZACION, RETO_ORDENADO, RETO_IMPREVISIBLE, RETO_ASESINO_A_SUELDO, RETO_ELITISTA -> return pelea.cantLuchDeEquipo(
                    2
                ) >= 2
                RETO_EL_DOS_POR_UNO ->  // que matar obligatoriamente a un (y sólo
                    return pelea.cantLuchDeEquipo(2) % 2 == 0
                RETO_REPARTO, RETO_DUELO, RETO_CADA_UNO_CON_SU_MONSTRUO ->  // al menos a un adversario durante el combate.
                    return pelea.cantLuchDeEquipo(2) >= pelea.cantLuchDeEquipo(1) && pelea.cantLuchDeEquipo(1) > 1
                RETO_ANACORETA, RETO_PEGAJOSO, RETO_LOS_PEQUEÑOS_ANTES, RETO_SUPERVIVIENTE, RETO_CONTAMINACION ->  // de 3 turnos para rematar a tu aliado o
                    return pelea.cantLuchDeEquipo(1) >= 2
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun esRetoPosible2(reto: Int, nuevo: Int): Boolean {
        return try {
            if (reto == nuevo) {
                return false
            }
            when (reto.toByte()) {
                RETO_ZOMBI, RETO_ESTATUA, RETO_NOMADA -> if (nuevo == RETO_ZOMBI.toInt() || nuevo == RETO_NOMADA.toInt() || nuevo == RETO_ESTATUA.toInt()) {
                    return false
                }
                RETO_AHORRADOR ->  // vez
                    if (nuevo == RETO_VERSATIL.toInt() || nuevo == RETO_BARBARO.toInt() || nuevo == RETO_LIMITADO.toInt()) {
                        return false
                    }
                RETO_BARBARO -> if (nuevo == RETO_AHORRADOR.toInt() || nuevo == RETO_MISTICO.toInt() || nuevo == RETO_MANOS_LIMPIAS.toInt() || nuevo == RETO_LIMITADO.toInt()) {
                    return false
                }
                RETO_VERSATIL ->  // repetir
                    if (nuevo == RETO_AHORRADOR.toInt()) {
                        return false
                    }
                RETO_MISTICO, RETO_MANOS_LIMPIAS ->  // directos
                    if (nuevo == RETO_BARBARO.toInt()) {
                        return false
                    }
                RETO_LIMITADO -> if (nuevo == RETO_AHORRADOR.toInt() || nuevo == RETO_BARBARO.toInt()) {
                    return false
                }
                RETO_NI_PIAS_NI_SUMISAS, RETO_NI_PIOS_NI_SUMISOS ->  // mobs
                    if (nuevo == RETO_NI_PIAS_NI_SUMISAS.toInt() || nuevo == RETO_NI_PIOS_NI_SUMISOS.toInt()) {
                        return false
                    }
                RETO_AUDAZ, RETO_PUSILANIME ->  // la de uno de tus adversarios
                    if (nuevo == RETO_AUDAZ.toInt() || nuevo == RETO_PUSILANIME.toInt()) {
                        return false
                    }
                RETO_PEGAJOSO, RETO_ANACORETA ->  // de uno de
// tus aliados
                    if (nuevo == RETO_PEGAJOSO.toInt() || nuevo == RETO_ANACORETA.toInt()) {
                        return false
                    }
                RETO_ELEGIDO_VOLUNTARIO, RETO_APLAZAMIENTO, RETO_CRUEL, RETO_ORDENADO, RETO_FOCALIZACION, RETO_ELITISTA, RETO_IMPREVISIBLE, RETO_ASESINO_A_SUELDO, RETO_BLITZKRIEG, RETO_DUELO ->  // debe atacar a ese mismo
                    if (nuevo == RETO_ELEGIDO_VOLUNTARIO.toInt() || nuevo == RETO_APLAZAMIENTO.toInt() || nuevo == RETO_CRUEL.toInt() || nuevo == RETO_ORDENADO.toInt() || nuevo == RETO_FOCALIZACION.toInt() || nuevo == RETO_ELITISTA.toInt() || nuevo == RETO_IMPREVISIBLE.toInt() || nuevo == RETO_ASESINO_A_SUELDO.toInt() || nuevo == RETO_BLITZKRIEG.toInt() || nuevo == RETO_DUELO.toInt()) {
                        return false
                    }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getCraneoPorClase(claseID: Int): Int {
        var objID = 0
        when (claseID.toByte()) {
            CLASE_FECA -> objID = 9077
            CLASE_OSAMODAS -> objID = 9078
            CLASE_ANUTROF -> objID = 9079
            CLASE_SRAM -> objID = 9080
            CLASE_XELOR -> objID = 9081
            CLASE_ZURCARAK -> objID = 9082
            CLASE_ANIRIPSA -> objID = 9083
            CLASE_YOPUKA -> objID = 9084
            CLASE_OCRA -> objID = 9085
            CLASE_SADIDA -> objID = 9086
            CLASE_SACROGITO -> objID = 9087
            CLASE_PANDAWA -> objID = 9088
        }
        return objID
    }

    fun getNombreClase(claseID: Int): String {
        var Clase = ""
        when (claseID.toByte()) {
            CLASE_FECA -> Clase = "Feca"
            CLASE_OSAMODAS -> Clase = "Osamodas"
            CLASE_ANUTROF -> Clase = "Anutrof"
            CLASE_SRAM -> Clase = "Sram"
            CLASE_XELOR -> Clase = "Xelor"
            CLASE_ZURCARAK -> Clase = "Zurcarak"
            CLASE_ANIRIPSA -> Clase = "Aniripsa"
            CLASE_YOPUKA -> Clase = "Yopuka"
            CLASE_OCRA -> Clase = "Ocra"
            CLASE_SADIDA -> Clase = "Sadida"
            CLASE_SACROGITO -> Clase = "Sacrogrito"
            CLASE_PANDAWA -> Clase = "Pandawa"
        }
        return Clase
    }

    @JvmStatic
    fun getPosObjeto(TipoID: Short, personaje: Personaje): Byte {
        var Pos = OBJETO_POS_NO_EQUIPADO
        when (TipoID.toInt()) {
            OBJETO_TIPO_AMULETO -> Pos = OBJETO_POS_AMULETO
            OBJETO_TIPO_ANILLO -> if (personaje.getObjPosicion(OBJETO_POS_ANILLO1) == null) {
                Pos = OBJETO_POS_ANILLO1
            } else if (personaje.getObjPosicion(OBJETO_POS_ANILLO_DERECHO) == null) {
                Pos = OBJETO_POS_ANILLO_DERECHO
            }
            OBJETO_TIPO_BASTON, OBJETO_TIPO_ARCO, OBJETO_TIPO_ARMA_MAGICA, OBJETO_TIPO_MARTILLO, OBJETO_TIPO_VARITA, OBJETO_TIPO_DAGAS, OBJETO_TIPO_PALA, OBJETO_TIPO_HACHA, OBJETO_TIPO_PICO, OBJETO_TIPO_HERRAMIENTA -> Pos =
                OBJETO_POS_ARMA
            OBJETO_TIPO_CAPA, OBJETO_TIPO_MOCHILA -> Pos = OBJETO_POS_CAPA
            OBJETO_TIPO_CINTURON -> Pos = OBJETO_POS_CINTURON
            OBJETO_TIPO_SOMBRERO -> Pos = OBJETO_POS_SOMBRERO
            OBJETO_TIPO_BOTAS -> Pos = OBJETO_POS_BOTAS
            OBJETO_TIPO_MASCOTA -> Pos = OBJETO_POS_MASCOTA
            OBJETO_TIPO_ESCUDO -> Pos = OBJETO_POS_ESCUDO
            OBJETO_TIPO_DOFUS -> if (personaje.getObjPosicion(OBJETO_POS_DOFUS1) == null) {
                Pos = OBJETO_POS_DOFUS1
            } else if (personaje.getObjPosicion(OBJETO_POS_DOFUS2) == null) {
                Pos = OBJETO_POS_DOFUS2
            } else if (personaje.getObjPosicion(OBJETO_POS_DOFUS3) == null) {
                Pos = OBJETO_POS_DOFUS3
            } else if (personaje.getObjPosicion(OBJETO_POS_DOFUS4) == null) {
                Pos = OBJETO_POS_DOFUS4
            } else if (personaje.getObjPosicion(OBJETO_POS_DOFUS5) == null) {
                Pos = OBJETO_POS_DOFUS5
            } else if (personaje.getObjPosicion(OBJETO_POS_DOFUS6) == null) {
                Pos = OBJETO_POS_DOFUS6
            }
        }
        return Pos
    }

    fun getDoplonDopeul(idMob: Int): Int {
        when (idMob) {
            168 -> return 10302
            165 -> return 10303
            166 -> return 10304
            162 -> return 10305
            160 -> return 10306
            167 -> return 10307
            161 -> return 10308
            2691 -> return 10309
            455 -> return 10310
            169 -> return 10311
            163 -> return 10312
            164 -> return 10313
        }
        return -1
    }

    fun getCertificadoDopeul(idMob: Int): Int {
        when (idMob) {
            168 -> return 10289
            165 -> return 10290
            166 -> return 10291
            162 -> return 10292
            160 -> return 10293
            167 -> return 10294
            161 -> return 10295
            2691 -> return 10296
            455 -> return 10297
            169 -> return 10298
            163 -> return 10299
            164 -> return 10300
        }
        return -1
    }

    // public static final boolean IsaRessource(Objet obj) {
// boolean isOk = false;
// switch (obj.getTemplate().getType()) {
// case 15 :
// case 27 :
// case 28 :
// case 34 :
// case 35 :
// case 36 :
// case 41 :
// case 46 :
// case 47 :
// case 48 :
// case 53 :
// case 58 :
// case 90 :
// case 105 :
// case 109 :
// isOk = true;
// break;
// default :
// isOk = false;
// }
// return isOk;
// }
    fun getNivelMiliciano(nivel: Int): Int {
        if (nivel <= 50) {
            return 50
        }
        if (nivel <= 80) {
            return 80
        }
        if (nivel <= 110) {
            return 110
        }
        if (nivel <= 140) {
            return 140
        }
        return if (nivel <= 170) {
            170
        } else 200
    }

    fun getNivelProtector(nivel: Int): Int {
        if (nivel > 0 && nivel <= 30) {
            return 10
        }
        if (nivel > 30 && nivel <= 50) {
            return 20
        }
        if (nivel > 50 && nivel <= 60) {
            return 30
        }
        return if (nivel > 60 && nivel <= 70) {
            40
        } else 50
    }

    fun getProtectorRecursos(trabajoID: Int, oficioID: Int): Int {
        var rand = 0
        when (oficioID) {
            28 -> when (trabajoID) {
                45 -> return 684
                53 -> return 685
                57 -> return 686
                46 -> return 687
                50 -> return 688
                52 -> return 689
                159 -> return 690
                58 -> return 691
                54 -> return 692
            }
            24 -> when (trabajoID) {
                24 -> return 693
                25 -> return 694
                26 -> return 695
                28 -> return 696
                56 -> return 697
                55 -> return 698
                162 -> return 699
                29 -> return 700
                31 -> return 701
                30 -> return 702
                161 -> return 703
            }
            2 -> {
                when (trabajoID) {
                    6 -> return 711
                    39 -> return 712
                    40 -> return 713
                    10 -> return 714
                    141 -> return 716
                    139 -> return 715
                    37 -> return 717
                    33 -> return 718
                    41 -> return 721
                    34 -> return 722
                    174 -> return 720
                    35 -> return 724
                    155 -> return 723
                    158 -> return 725
                }
                when (trabajoID) {
                    160 -> return 710
                    74 -> return 709
                    73 -> return 708
                    10 -> return 707
                    71 -> return 706
                    69 -> return 705
                    68 -> return 704
                }
                when (trabajoID) {
                    131 -> return 739
                    127 -> return 738
                    130 -> return 737
                    129 -> {
                        rand = getRandomInt(1, 3)
                        when (rand) {
                            1 -> return 736 // lubinas
                            2 -> return 735 // kralamares
                            3 -> return 734 // sardinas
                        }
                        rand = getRandomInt(1, 2)
                        when (rand) {
                            1 -> return 733 // carpas
                            2 -> return 732 // lucios
                        }
                        rand = getRandomInt(1, 3)
                        when (rand) {
                            1 -> return 731 // pescados empapados
                            2 -> return 730 // cangrejos
                            3 -> return 729 // bangas
                        }
                        rand = getRandomInt(1, 3)
                        when (rand) {
                            1 -> return 728 // peces gatito
                            2 -> return 727 // truchas
                            3 -> return 726 // gobios
                        }
                    }
                    125 -> {
                        rand = getRandomInt(1, 2)
                        when (rand) {
                            1 -> return 733
                            2 -> return 732
                        }
                        rand = getRandomInt(1, 3)
                        when (rand) {
                            1 -> return 731
                            2 -> return 730
                            3 -> return 729
                        }
                        rand = getRandomInt(1, 3)
                        when (rand) {
                            1 -> return 728
                            2 -> return 727
                            3 -> return 726
                        }
                    }
                    128 -> {
                        rand = getRandomInt(1, 3)
                        when (rand) {
                            1 -> return 731
                            2 -> return 730
                            3 -> return 729
                        }
                        rand = getRandomInt(1, 3)
                        when (rand) {
                            1 -> return 728
                            2 -> return 727
                            3 -> return 726
                        }
                    }
                    124 -> {
                        rand = getRandomInt(1, 3)
                        when (rand) {
                            1 -> return 728
                            2 -> return 727
                            3 -> return 726
                        }
                    }
                }
            }
            26 -> {
                when (trabajoID) {
                    160 -> return 710
                    74 -> return 709
                    73 -> return 708
                    10 -> return 707
                    71 -> return 706
                    69 -> return 705
                    68 -> return 704
                }
                when (trabajoID) {
                    131 -> return 739
                    127 -> return 738
                    130 -> return 737
                    129 -> {
                        rand = getRandomInt(1, 3)
                        when (rand) {
                            1 -> return 736
                            2 -> return 735
                            3 -> return 734
                        }
                        rand = getRandomInt(1, 2)
                        when (rand) {
                            1 -> return 733
                            2 -> return 732
                        }
                        rand = getRandomInt(1, 3)
                        when (rand) {
                            1 -> return 731
                            2 -> return 730
                            3 -> return 729
                        }
                        rand = getRandomInt(1, 3)
                        when (rand) {
                            1 -> return 728
                            2 -> return 727
                            3 -> return 726
                        }
                    }
                    125 -> {
                        rand = getRandomInt(1, 2)
                        when (rand) {
                            1 -> return 733
                            2 -> return 732
                        }
                        rand = getRandomInt(1, 3)
                        when (rand) {
                            1 -> return 731
                            2 -> return 730
                            3 -> return 729
                        }
                        rand = getRandomInt(1, 3)
                        when (rand) {
                            1 -> return 728
                            2 -> return 727
                            3 -> return 726
                        }
                    }
                    128 -> {
                        rand = getRandomInt(1, 3)
                        when (rand) {
                            1 -> return 731
                            2 -> return 730
                            3 -> return 729
                        }
                        rand = getRandomInt(1, 3)
                        when (rand) {
                            1 -> return 728
                            2 -> return 727
                            3 -> return 726
                        }
                    }
                    124 -> {
                        rand = getRandomInt(1, 3)
                        when (rand) {
                            1 -> return 728
                            2 -> return 727
                            3 -> return 726
                        }
                    }
                }
            }
            36 -> when (trabajoID) {
                131 -> return 739
                127 -> return 738
                130 -> return 737
                129 -> {
                    rand = getRandomInt(1, 3)
                    when (rand) {
                        1 -> return 736
                        2 -> return 735
                        3 -> return 734
                    }
                    rand = getRandomInt(1, 2)
                    when (rand) {
                        1 -> return 733
                        2 -> return 732
                    }
                    rand = getRandomInt(1, 3)
                    when (rand) {
                        1 -> return 731
                        2 -> return 730
                        3 -> return 729
                    }
                    rand = getRandomInt(1, 3)
                    when (rand) {
                        1 -> return 728
                        2 -> return 727
                        3 -> return 726
                    }
                }
                125 -> {
                    rand = getRandomInt(1, 2)
                    when (rand) {
                        1 -> return 733
                        2 -> return 732
                    }
                    rand = getRandomInt(1, 3)
                    when (rand) {
                        1 -> return 731
                        2 -> return 730
                        3 -> return 729
                    }
                    rand = getRandomInt(1, 3)
                    when (rand) {
                        1 -> return 728
                        2 -> return 727
                        3 -> return 726
                    }
                }
                128 -> {
                    rand = getRandomInt(1, 3)
                    when (rand) {
                        1 -> return 731
                        2 -> return 730
                        3 -> return 729
                    }
                    rand = getRandomInt(1, 3)
                    when (rand) {
                        1 -> return 728
                        2 -> return 727
                        3 -> return 726
                    }
                }
                124 -> {
                    rand = getRandomInt(1, 3)
                    when (rand) {
                        1 -> return 728
                        2 -> return 727
                        3 -> return 726
                    }
                }
            }
        }
        return 0
    }

    fun getNivelDopeul(nivel: Int): Int {
        if (nivel < 20) {
            return 20
        }
        if (nivel < 40) {
            return 40
        }
        if (nivel < 60) {
            return 60
        }
        if (nivel < 80) {
            return 80
        }
        if (nivel < 100) {
            return 100
        }
        if (nivel < 120) {
            return 120
        }
        if (nivel < 140) {
            return 140
        }
        if (nivel < 160) {
            return 160
        }
        return if (nivel < 180) {
            180
        } else 200
    }

    @JvmStatic
    fun getCaracObjCria(idObjMod: Int): Int {
        when (idObjMod) {
            7606, 7612, 7619, 7685, 7686, 7620, 7621, 7617, 7613, 7614, 7616, 7610, 7607, 7683, 7687, 7688, 7689, 7690, 7684, 7615, 7618, 7611, 7608, 7609 -> return 6
            7628, 7738, 7739, 7740, 7741, 7735, 7623, 7734, 7736, 7733, 7624, 7622, 7737, 7746, 7742, 7745, 7743, 7744 -> return 5
            7625, 7757, 7762, 7770, 7772, 7765, 7766, 7763, 7758, 7626, 7761, 7755, 7759, 7767, 7773, 7774, 7771, 7769, 7768, 7760, 7764, 7756, 7627, 7629 -> return 4
            7634, 7695, 7694, 7693, 7699, 7635, 7636, 7637, 7692, 7691, 7700, 7697, 7698, 7696 -> return 3
            7798, 7780, 7787, 7793, 7794, 7788, 7789, 7785, 7781, 7782, 7784, 7778, 7775, 7790, 7795, 7796, 7797, 7792, 7791, 7783, 7786, 7779, 7776, 7777 -> return 2
            7605, 7592, 7602, 7676, 7677, 7603, 7604, 7600, 7594, 7593, 7596, 7598, 7590, 7673, 7678, 7679, 7682, 7675, 7674, 7595, 7601, 7599, 7591, 7597 -> return 1
        }
        return -1
    }

    @JvmStatic
    fun getObjCriaPorMapa(mapa: Short): Int {
        when (mapa.toInt()) {
            8747, 8750 -> return 7596
            8745, 8752 -> return 7784
            8746, 8749 -> return 7695
            8743, 8748 -> return 7761
            8744, 8751 -> return 7741
            8848, 8851 -> return 7616
        }
        return -1
    }

    // public static final int getKamasDonResetAlign(int level) {
// if (level <= 30)
// return 12000;
// if ((level > 30) && (level <= 50))
// return 24000;
// if ((level > 50) && (level <= 100))
// return 48000;
// if ((level > 100) && (level <= 150)) {
// return 64000;
// }
// return 100000;
// }
//
// public static final String getStatutByClass(int classID) {
// switch (classID) {
// case 1 :
// return "10300;337";
// case 2 :
// case 3 :
// return "10299;300";
// case 4 :
// return "10285;263";
// case 5 :
// return "10298;315";
// case 6 :
// return "10276;311";
// case 7 :
// return "10283;299";
// case 8 :
// return "10294;309";
// case 9 :
// return "10292;299";
// case 10 :
// return "10279;284";
// case 11 :
// return "10296;258";
// case 12 :
// return "10289;250";
// }
// return "";
// }
//
// public static final String getStatutByClassAstrub(int classID) {
// switch (classID) {
// case 1 :
// return "7398;299";
// case 2 :
// return "7545;311";
// case 3 :
// return "7442;254";
// case 4 :
// return "7392;282";
// case 5 :
// return "7332;312";
// case 6 :
// return "7446;299";
// case 7 :
// return "7361;207";
// case 8 :
// return "7427;267";
// case 9 :
// return "7378;338";
// case 10 :
// return "7395;371";
// case 11 :
// return "7336;198";
// case 12 :
// return "8035;384";
// }
// return "";
// }
//
// public static final String getNameByClass(int classID) {
// switch (classID) {
// case 1 :
// return "Feca";
// case 2 :
// return "Osamodas";
// case 3 :
// return "Enutrof";
// case 4 :
// return "Sram";
// case 5 :
// return "Xelor";
// case 6 :
// return "Ecaflip";
// case 7 :
// return "Eniripsa";
// case 8 :
// return "Iop";
// case 9 :
// return "Cra";
// case 10 :
// return "Sadida";
// case 11 :
// return "Sacrieur";
// case 12 :
// return "Pandawa";
// }
// return "";
// }
//
// public static final boolean isFightDopeul(Map<Integer, Fight.Fighter> mob) {
// Iterator localIterator = mob.values().iterator();
// if (localIterator.hasNext()) {
// Fight.Fighter f = (Fight.Fighter) localIterator.next();
// if ((f.getPersonnage() != null) || (f.getPerco() != null) || (f.getPrism() != null))
// return false;
// switch (f.getMob().getTemplate().getID()) {
// case 160 :
// case 161 :
// case 162 :
// case 163 :
// case 164 :
// case 165 :
// case 166 :
// case 167 :
// case 168 :
// case 169 :
// case 455 :
// case 2691 :
// return true;
// }
// return false;
// }
// return false;
// }
// zd
    fun getDopeulPorNPC(npcID: Int): Int {
        var mobID = 0
        when (npcID) {
            434 -> mobID = 167
            436 -> mobID = 161
            437 -> mobID = 164
            438 -> mobID = 165
            439 -> mobID = 168
            440 -> mobID = 162
            441 -> mobID = 163
            442 -> mobID = 169
            443 -> mobID = 455
            433 -> mobID = 160
        }
        return mobID
    }

    fun getExpForjamaguear(pesoRuna: Float, nivelObjeto: Int): Int {
        return if (nivelObjeto < 25) {
            if (pesoRuna < 3) {
                1
            } else if (pesoRuna < 10) {
                1
            } else if (pesoRuna < 50) {
                10
            } else if (pesoRuna < 100) {
                25
            } else {
                50
            }
        } else if (nivelObjeto < 50) {
            if (pesoRuna < 3) {
                1
            } else if (pesoRuna < 10) {
                10
            } else if (pesoRuna < 50) {
                10
            } else if (pesoRuna < 100) {
                50
            } else {
                50
            }
        } else if (nivelObjeto < 75) {
            if (pesoRuna < 3) {
                10
            } else if (pesoRuna < 10) {
                25
            } else if (pesoRuna < 50) {
                25
            } else if (pesoRuna < 100) {
                50
            } else {
                100
            }
        } else if (nivelObjeto < 100) {
            if (pesoRuna < 3) {
                25
            } else if (pesoRuna < 10) {
                25
            } else if (pesoRuna < 50) {
                50
            } else if (pesoRuna < 100) {
                100
            } else {
                250
            }
        } else if (nivelObjeto < 125) {
            if (pesoRuna < 3) {
                50
            } else if (pesoRuna < 10) {
                50
            } else if (pesoRuna < 50) {
                100
            } else if (pesoRuna < 100) {
                250
            } else {
                500
            }
        } else if (nivelObjeto < 150) {
            if (pesoRuna < 3) {
                100
            } else if (pesoRuna < 10) {
                100
            } else if (pesoRuna < 50) {
                250
            } else if (pesoRuna < 100) {
                500
            } else {
                1000
            }
        } else if (nivelObjeto < 175) {
            if (pesoRuna < 3) {
                250
            } else if (pesoRuna < 10) {
                250
            } else if (pesoRuna < 50) {
                250
            } else if (pesoRuna < 100) {
                1000
            } else {
                1000
            }
        } else if (nivelObjeto < 200) {
            if (pesoRuna < 3) {
                250
            } else if (pesoRuna < 10) {
                500
            } else if (pesoRuna < 50) {
                500
            } else if (pesoRuna < 100) {
                1000
            } else {
                1000
            }
        } else {
            if (pesoRuna < 3) {
                500
            } else if (pesoRuna < 10) {
                1000
            } else if (pesoRuna < 50) {
                1000
            } else if (pesoRuna < 100) {
                1000
            } else {
                1000
            }
        }
    }

    @JvmStatic
    fun getStatPositivoDeNegativo(statID: Int): Int {
        when (statID) {
            STAT_MENOS_SUERTE -> return STAT_MAS_SUERTE
            STAT_MENOS_VITALIDAD -> return STAT_MAS_VITALIDAD
            STAT_MENOS_AGILIDAD -> return STAT_MAS_AGILIDAD
            STAT_MENOS_INTELIGENCIA -> return STAT_MAS_INTELIGENCIA
            STAT_MENOS_SABIDURIA -> return STAT_MAS_SABIDURIA
            STAT_MENOS_FUERZA -> return STAT_MAS_FUERZA
            STAT_MENOS_ALCANCE -> return STAT_MAS_ALCANCE
            STAT_MENOS_INICIATIVA -> return STAT_MAS_INICIATIVA
            STAT_MENOS_PROSPECCION -> return STAT_MAS_PROSPECCION
            STAT_MENOS_CURAS -> return STAT_MAS_CURAS
            STAT_MENOS_DAÑOS -> return STAT_MAS_DAÑOS
            STAT_MENOS_PORC_DAÑOS -> return STAT_MAS_PORC_DAÑOS
            STAT_MENOS_GOLPES_CRITICOS -> return STAT_MAS_GOLPES_CRITICOS
            STAT_MENOS_RES_PORC_TIERRA -> return STAT_MAS_RES_PORC_TIERRA
            STAT_MENOS_RES_PORC_AGUA -> return STAT_MAS_RES_PORC_AGUA
            STAT_MENOS_RES_PORC_AIRE -> return STAT_MAS_RES_PORC_AIRE
            STAT_MENOS_RES_PORC_FUEGO -> return STAT_MAS_RES_PORC_FUEGO
            STAT_MENOS_RES_PORC_NEUTRAL -> return STAT_MAS_RES_PORC_NEUTRAL
            STAT_MENOS_RES_FIJA_TIERRA -> return STAT_MAS_RES_FIJA_TIERRA
            STAT_MENOS_RES_FIJA_AGUA -> return STAT_MAS_RES_FIJA_AGUA
            STAT_MENOS_RES_FIJA_AIRE -> return STAT_MAS_RES_FIJA_AIRE
            STAT_MENOS_RES_FIJA_FUEGO -> return STAT_MAS_RES_FIJA_FUEGO
            STAT_MENOS_RES_FIJA_NEUTRAL -> return STAT_MAS_RES_FIJA_NEUTRAL
            STAT_MENOS_DAÑOS_DE_AGUA -> return STAT_MAS_DAÑOS_DE_AGUA
            STAT_MENOS_DAÑOS_DE_AIRE -> return STAT_MAS_DAÑOS_DE_AIRE
            STAT_MENOS_DAÑOS_DE_FUEGO -> return STAT_MAS_DAÑOS_DE_FUEGO
            STAT_MENOS_DAÑOS_DE_TIERRA -> return STAT_MAS_DAÑOS_DE_TIERRA
            STAT_MENOS_DAÑOS_DE_NEUTRAL -> return STAT_MAS_DAÑOS_DE_NEUTRAL
            STAT_MENOS_DAÑOS_EMPUJE -> return STAT_MAS_DAÑOS_EMPUJE
            STAT_MENOS_REDUCCION_CRITICOS -> return STAT_MAS_REDUCCION_CRITICOS
            STAT_MENOS_DAÑOS_CRITICOS -> return STAT_MAS_DAÑOS_CRITICOS
            STAT_MENOS_REDUCCION_EMPUJE -> return STAT_MAS_REDUCCION_EMPUJE
            STAT_MENOS_HUIDA -> return STAT_MAS_HUIDA
            STAT_MENOS_PLACAJE -> return STAT_MAS_PLACAJE
            STAT_MENOS_RETIRO_PM -> return STAT_MAS_RETIRO_PM
            STAT_MENOS_RETIRO_PA -> return STAT_MAS_RETIRO_PA
        }
        return statID
    }

    fun getStatOpuesto(statID: Int): Int {
        when (statID) {
            STAT_MENOS_PA -> return STAT_MAS_PA
            STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA -> return STAT_MENOS_DAÑOS_REDUCIDOS
            STAT_MAS_PA -> return STAT_MENOS_PA
            STAT_MAS_DAÑOS -> return STAT_MENOS_DAÑOS
            STAT_MAS_GOLPES_CRITICOS -> return STAT_MENOS_GOLPES_CRITICOS
            STAT_MENOS_ALCANCE -> return STAT_MAS_ALCANCE
            STAT_MAS_ALCANCE -> return STAT_MENOS_ALCANCE
            STAT_MAS_FUERZA -> return STAT_MENOS_FUERZA
            STAT_MAS_AGILIDAD -> return STAT_MENOS_AGILIDAD
            STAT_MAS_SUERTE -> return STAT_MENOS_SUERTE
            STAT_MAS_SABIDURIA -> return STAT_MENOS_SABIDURIA
            STAT_MAS_VITALIDAD -> return STAT_MENOS_VITALIDAD
            STAT_MAS_INTELIGENCIA -> return STAT_MENOS_INTELIGENCIA
            STAT_MENOS_PM -> return STAT_MAS_PM
            STAT_MAS_PM -> return STAT_MENOS_PM
            STAT_MAS_PORC_DAÑOS -> return STAT_MENOS_PORC_DAÑOS
            STAT_MENOS_DAÑOS -> return STAT_MAS_DAÑOS
            STAT_MENOS_SUERTE -> return STAT_MAS_SUERTE
            STAT_MENOS_VITALIDAD -> return STAT_MAS_VITALIDAD
            STAT_MENOS_AGILIDAD -> return STAT_MAS_AGILIDAD
            STAT_MENOS_INTELIGENCIA -> return STAT_MAS_INTELIGENCIA
            STAT_MENOS_SABIDURIA -> return STAT_MAS_SABIDURIA
            STAT_MENOS_FUERZA -> return STAT_MAS_FUERZA
            STAT_MAS_PODS -> return STAT_MENOS_PODS
            STAT_MENOS_PODS -> return STAT_MAS_PODS
            STAT_MAS_ESQUIVA_PERD_PA -> return STAT_MENOS_ESQUIVA_PERD_PA
            STAT_MAS_ESQUIVA_PERD_PM -> return STAT_MENOS_ESQUIVA_PERD_PM
            STAT_MENOS_ESQUIVA_PERD_PA -> return STAT_MAS_ESQUIVA_PERD_PA
            STAT_MENOS_ESQUIVA_PERD_PM -> return STAT_MAS_ESQUIVA_PERD_PM
            STAT_MENOS_DAÑOS_REDUCIDOS -> return STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA
            STAT_MENOS_GOLPES_CRITICOS -> return STAT_MAS_GOLPES_CRITICOS
            STAT_MAS_INICIATIVA -> return STAT_MENOS_INICIATIVA
            STAT_MENOS_INICIATIVA -> return STAT_MAS_INICIATIVA
            STAT_MAS_PROSPECCION -> return STAT_MENOS_PROSPECCION
            STAT_MENOS_PROSPECCION -> return STAT_MAS_PROSPECCION
            STAT_MAS_CURAS -> return STAT_MENOS_CURAS
            STAT_MENOS_CURAS -> return STAT_MAS_CURAS
            STAT_MENOS_PORC_DAÑOS -> return STAT_MAS_PORC_DAÑOS
            STAT_MAS_RES_PORC_TIERRA -> return STAT_MENOS_RES_PORC_TIERRA
            STAT_MAS_RES_PORC_AGUA -> return STAT_MENOS_RES_PORC_AGUA
            STAT_MAS_RES_PORC_AIRE -> return STAT_MENOS_RES_PORC_AIRE
            STAT_MAS_RES_PORC_FUEGO -> return STAT_MENOS_RES_PORC_FUEGO
            STAT_MAS_RES_PORC_NEUTRAL -> return STAT_MENOS_RES_PORC_NEUTRAL
            STAT_MENOS_RES_PORC_TIERRA -> return STAT_MAS_RES_PORC_TIERRA
            STAT_MENOS_RES_PORC_AGUA -> return STAT_MAS_RES_PORC_AGUA
            STAT_MENOS_RES_PORC_AIRE -> return STAT_MAS_RES_PORC_AIRE
            STAT_MENOS_RES_PORC_FUEGO -> return STAT_MAS_RES_PORC_FUEGO
            STAT_MENOS_RES_PORC_NEUTRAL -> return STAT_MAS_RES_PORC_NEUTRAL
            STAT_MAS_RES_FIJA_TIERRA -> return STAT_MENOS_RES_FIJA_TIERRA
            STAT_MAS_RES_FIJA_AGUA -> return STAT_MENOS_RES_FIJA_AGUA
            STAT_MAS_RES_FIJA_AIRE -> return STAT_MENOS_RES_FIJA_AIRE
            STAT_MAS_RES_FIJA_FUEGO -> return STAT_MENOS_RES_FIJA_FUEGO
            STAT_MAS_RES_FIJA_NEUTRAL -> return STAT_MENOS_RES_FIJA_NEUTRAL
            STAT_MENOS_RES_FIJA_TIERRA -> return STAT_MAS_RES_FIJA_TIERRA
            STAT_MENOS_RES_FIJA_AGUA -> return STAT_MAS_RES_FIJA_AGUA
            STAT_MENOS_RES_FIJA_AIRE -> return STAT_MAS_RES_FIJA_AIRE
            STAT_MENOS_RES_FIJA_FUEGO -> return STAT_MAS_RES_FIJA_FUEGO
            STAT_MENOS_RES_FIJA_NEUTRAL -> return STAT_MAS_RES_FIJA_NEUTRAL
            STAT_MAS_RES_PORC_PVP_TIERRA -> return STAT_MENOS_RES_PORC_PVP_TIERRA
            STAT_MAS_RES_PORC_PVP_AGUA -> return STAT_MENOS_RES_PORC_PVP_AGUA
            STAT_MAS_RES_PORC_PVP_AIRE -> return STAT_MENOS_RES_PORC_PVP_AIRE
            STAT_MAS_RES_PORC_PVP_FUEGO -> return STAT_MENOS_RES_PORC_PVP_FUEGO
            STAT_MAS_RES_PORC_PVP_NEUTRAL -> return STAT_MENOS_RES_PORC_PVP_NEUTRAL
            STAT_MENOS_RES_PORC_PVP_TIERRA -> return STAT_MAS_RES_PORC_PVP_TIERRA
            STAT_MENOS_RES_PORC_PVP_AGUA -> return STAT_MAS_RES_PORC_PVP_AGUA
            STAT_MENOS_RES_PORC_PVP_AIRE -> return STAT_MAS_RES_PORC_PVP_AIRE
            STAT_MENOS_RES_PORC_PVP_FUEGO -> return STAT_MAS_RES_PORC_PVP_FUEGO
            STAT_MENOS_RES_PORC_PVP_NEUTRAL -> return STAT_MAS_RES_PORC_PVP_NEUTRAL
            STAT_MAS_HUIDA -> return STAT_MENOS_HUIDA
            STAT_MENOS_HUIDA -> return STAT_MAS_HUIDA
            STAT_MAS_PLACAJE -> return STAT_MENOS_PLACAJE
            STAT_MENOS_PLACAJE -> return STAT_MAS_PLACAJE
            STAT_MAS_RETIRO_PM -> return STAT_MENOS_RETIRO_PM
            STAT_MENOS_RETIRO_PM -> return STAT_MAS_RETIRO_PM
            STAT_MAS_RETIRO_PA -> return STAT_MENOS_RETIRO_PA
            STAT_MENOS_RETIRO_PA -> return STAT_MAS_RETIRO_PA
            STAT_MAS_DAÑOS_DE_AGUA -> return STAT_MENOS_DAÑOS_DE_AGUA
            STAT_MAS_DAÑOS_DE_AIRE -> return STAT_MENOS_DAÑOS_DE_AIRE
            STAT_MAS_DAÑOS_DE_FUEGO -> return STAT_MENOS_DAÑOS_DE_FUEGO
            STAT_MAS_DAÑOS_DE_TIERRA -> return STAT_MENOS_DAÑOS_DE_TIERRA
            STAT_MAS_DAÑOS_DE_NEUTRAL -> return STAT_MENOS_DAÑOS_DE_NEUTRAL
            STAT_MAS_DAÑOS_EMPUJE -> return STAT_MENOS_DAÑOS_EMPUJE
            STAT_MAS_REDUCCION_CRITICOS -> return STAT_MENOS_REDUCCION_CRITICOS
            STAT_MAS_DAÑOS_CRITICOS -> return STAT_MENOS_DAÑOS_CRITICOS
            STAT_MAS_REDUCCION_EMPUJE -> return STAT_MENOS_REDUCCION_EMPUJE
            STAT_MENOS_DAÑOS_DE_AGUA -> return STAT_MAS_DAÑOS_DE_AGUA
            STAT_MENOS_DAÑOS_DE_AIRE -> return STAT_MAS_DAÑOS_DE_AIRE
            STAT_MENOS_DAÑOS_DE_FUEGO -> return STAT_MAS_DAÑOS_DE_FUEGO
            STAT_MENOS_DAÑOS_DE_TIERRA -> return STAT_MAS_DAÑOS_DE_TIERRA
            STAT_MENOS_DAÑOS_DE_NEUTRAL -> return STAT_MAS_DAÑOS_DE_NEUTRAL
            STAT_MENOS_DAÑOS_EMPUJE -> return STAT_MAS_DAÑOS_EMPUJE
            STAT_MENOS_REDUCCION_CRITICOS -> return STAT_MAS_REDUCCION_CRITICOS
            STAT_MENOS_DAÑOS_CRITICOS -> return STAT_MAS_DAÑOS_CRITICOS
            STAT_MENOS_REDUCCION_EMPUJE -> return STAT_MAS_REDUCCION_EMPUJE
        }
        return statID
    }

    @JvmStatic
    fun getPotenciaRunaPorStat(statID: Int): IntArray {
        val r = IntArray(3)
        var i = 0
        when (statID) {
            STAT_MAS_VITALIDAD -> {
                r[i++] = 3
                r[i++] = 10
                r[i++] = 30
            }
            STAT_MAS_INICIATIVA, STAT_MAS_PODS -> {
                r[i++] = 10
                r[i++] = 30
                r[i++] = 100
            }
            STAT_MAS_DAÑOS, STAT_MAS_CURAS, STAT_MAS_GOLPES_CRITICOS, STAT_MAS_CRIATURAS_INVO, STAT_MAS_ALCANCE, STAT_MAS_PA, STAT_MAS_PM, STAT_ARMA_CAZA, STAT_MAS_RES_FIJA_TIERRA, STAT_MAS_RES_FIJA_AGUA, STAT_MAS_RES_FIJA_AIRE, STAT_MAS_RES_FIJA_FUEGO, STAT_MAS_RES_FIJA_NEUTRAL, STAT_REENVIA_DAÑOS, STAT_MAS_RES_PORC_TIERRA, STAT_MAS_RES_PORC_AGUA, STAT_MAS_RES_PORC_AIRE, STAT_MAS_RES_PORC_FUEGO, STAT_MAS_RES_PORC_NEUTRAL -> r[i++] =
                1
            STAT_MAS_DAÑOS_TRAMPA, STAT_MAS_PROSPECCION -> {
                r[i++] = 1
                r[i++] = 3
            }
            STAT_MAS_PORC_DAÑOS, STAT_MAS_PORC_DAÑOS_TRAMPA, STAT_MAS_FUERZA, STAT_MAS_AGILIDAD, STAT_MAS_SUERTE, STAT_MAS_INTELIGENCIA, STAT_MAS_SABIDURIA -> {
                r[i++] = 1
                r[i++] = 3
                r[i++] = 10
            }
        }
        return r
    }

    @JvmStatic
    fun getTipoRuna(statID: Int, pesoTotal: Float): Int {
        if (pesoTotal <= 0) {
            return 0
        }
        val pesoStat = getPesoStat(statID)
        val v = getPotenciaRunaPorStat(statID)
        val factor = (pesoTotal / pesoStat).toInt()
        if (factor <= 0) {
            return 0
        }
        var tipo = 0
        tipo = if (factor <= v[0] * 6) {
            1
        } else if (factor <= v[1] * 9) {
            2
        } else {
            3
        }
        if (tipo == 1) {
            return 1
        }
        while (tipo > 0) {
            if (v[tipo - 1] != 0) {
                break
            }
            tipo--
        }
        return tipo
    }

    @JvmStatic
    fun getPorcCrearRuna(statID: Int, peso: Float, tipo: Int, nivel: Int): Float {
        if (peso <= 0) {
            return 0f
        }
        val v = getPotenciaRunaPorStat(statID)
        var basico = 0
        when (tipo) {
            1 -> basico = v[0]
            2 -> basico = 2 * v[0] + v[1]
            3 -> basico = 4 * v[0] + 2 * v[1] + v[2]
        }
        val maximo = Math.round(basico / (2 / 3f) / 0.9f)
        val minimo = Math.round(basico / (2 / 3f) / 1.1f)
        val pesoIndiv = getPesoStat(statID)
        val cantStat = (peso / pesoIndiv).toInt()
        var porc = 0f
        when (statID) {
            STAT_MAS_CRIATURAS_INVO -> porc = Math.min(
                85.0,
                Math.pow(
                    nivel.toDouble(),
                    2.0
                ) / Math.pow(getPesoStat(statID).toDouble(), (5 / 4f).toDouble())
            ).toFloat()
            STAT_ARMA_CAZA -> porc = Math.min(
                90.0,
                Math.pow(
                    nivel.toDouble(),
                    2.0
                ) / Math.pow(getPesoStat(statID).toDouble(), (5 / 4f).toDouble())
            ).toFloat()
            STAT_MAS_ALCANCE -> porc = Math.min(
                80.0,
                Math.pow(
                    nivel.toDouble(),
                    2.0
                ) / Math.pow(getPesoStat(statID).toDouble(), (5 / 4f).toDouble())
            ).toFloat()
            STAT_MAS_PM -> {
                porc = Math.min(
                    75.0,
                    Math.pow(
                        nivel.toDouble(),
                        2.0
                    ) / Math.pow(getPesoStat(statID).toDouble(), (5 / 4f).toDouble())
                ).toFloat()
                if (porc < 25) {
                    porc = 25f
                }
            }
            STAT_MAS_PA -> {
                porc = Math.min(
                    66.0,
                    Math.pow(
                        nivel.toDouble(),
                        2.0
                    ) / Math.pow(getPesoStat(statID).toDouble(), (5 / 4f).toDouble())
                ).toFloat()
                if (porc < 20) {
                    porc = 20f
                }
            }
            else -> {
                if (cantStat > maximo) {
                    return 100f
                }
                return if (cantStat < minimo) {
                    0f
                } else (cantStat - minimo) * 100f / (maximo - minimo)
            }
        }
        //modificar el % a sacar runas de PA silva
        if (porc < 20) {
            porc = 20f
        }
        return porc
    }

    // getValorPorRunaPocima
    fun getValorPorRunaPocima(objeto: Objeto): Int {
        when (objeto.objModelo?.tipo?.toInt()) {
            OBJETO_TIPO_POCION_FORJAMAGIA, OBJETO_TIPO_RUNA_FORJAMAGIA -> if (objeto.tieneStatTexto(
                    STAT_POTENCIA_RUNA
                )
            ) {
                try {
                    return objeto.getParamStatTexto(STAT_POTENCIA_RUNA, 1).toInt(16)
                } catch (ignored: Exception) {
                }
            }
        }
        return 0
    }

    fun getStatPorRunaPocima(objeto: Objeto): Int {
        when (objeto.objModelo?.tipo?.toInt()) {
            OBJETO_TIPO_POCION_FORJAMAGIA, OBJETO_TIPO_RUNA_FORJAMAGIA -> if (objeto.tieneStatTexto(
                    STAT_POTENCIA_RUNA
                )
            ) {
                try {
                    return objeto.getParamStatTexto(STAT_POTENCIA_RUNA, 2).toInt(16)
                } catch (ignored: Exception) {
                }
            }
        }
        return -1
    }

    @JvmStatic
    fun getRunaPorStat(stat: Int, tipo: Int): Int {
        when (stat) {
            STAT_MAS_PA -> return 1557 // runa PA
            STAT_ARMA_CAZA -> return 10057 // runa de caza
            STAT_MAS_DAÑOS -> return 7435 // runa daño
            STAT_MAS_GOLPES_CRITICOS -> return 7433
            STAT_MAS_ALCANCE -> return 7438 // runa alcance
            STAT_MAS_FUERZA -> return when (tipo) {
                3 -> 1551
                2 -> 1545
                else -> 1519
            }
            STAT_MAS_AGILIDAD -> return when (tipo) {
                3 -> 1555
                2 -> 1549
                else -> 1524
            }
            STAT_MAS_SUERTE -> return when (tipo) {
                3 -> 1556
                2 -> 1550
                else -> 1525
            }
            STAT_MAS_SABIDURIA -> return when (tipo) {
                3 -> 1552
                2 -> 1546
                else -> 1521
            }
            STAT_MAS_VITALIDAD -> return when (tipo) {
                3 -> 1554
                2 -> 1548
                else -> 1523
            }
            STAT_MAS_INTELIGENCIA -> return when (tipo) {
                3 -> 1553
                2 -> 1547
                else -> 1522
            }
            STAT_MAS_PM -> return 1558 // runa PM 1
            STAT_MAS_PORC_DAÑOS -> return when (tipo) {
                3 -> 10619
                2 -> 10618
                else -> 7436 // runa porcDaño
            }
            STAT_MAS_PODS -> return when (tipo) {
                3 -> 7445
                2 -> 7444
                else -> 7443
            }
            STAT_MAS_INICIATIVA -> return when (tipo) {
                3 -> 7450
                2 -> 7449
                else -> 7448
            }
            STAT_MAS_PROSPECCION -> {
                return if (tipo == 2) {
                    10662
                } else 7451
            }
            STAT_MAS_CURAS -> return 7434 // runa cura
            STAT_MAS_CRIATURAS_INVO -> return 7442 // runa invo
            STAT_REENVIA_DAÑOS -> return 7437 // runa reenvio
            STAT_MAS_DAÑOS_TRAMPA -> {
                return if (tipo == 2) {
                    10613
                } else 7446
                // runa daño trampa
            }
            STAT_MAS_PORC_DAÑOS_TRAMPA -> return when (tipo) {
                3 -> 10616
                2 -> 10615
                else -> 7447 // runa daño porc Trampa
            }
            STAT_MAS_RES_FIJA_FUEGO -> return 7452 // runa re fuego
            STAT_MAS_RES_FIJA_AIRE -> return 7453 // runa re aire
            STAT_MAS_RES_FIJA_AGUA -> return 7454 // runa re agua
            STAT_MAS_RES_FIJA_TIERRA -> return 7455 // runa re tierra
            STAT_MAS_RES_FIJA_NEUTRAL -> return 7456 // runa re neutral
            STAT_MAS_RES_PORC_FUEGO -> return 7457 // runa re %fuego
            STAT_MAS_RES_PORC_AIRE -> return 7458 // runa re %aire
            STAT_MAS_RES_PORC_AGUA -> return 7560 // runa re %agua
            STAT_MAS_RES_PORC_TIERRA -> return 7459 // runa re %tierra
            STAT_MAS_RES_PORC_NEUTRAL -> return 7460 // runa re %neutral
        }
        return 0
    }

    fun getPotenciaPlusRuna(objeto: Objeto): Int {
        when (objeto.objModelo?.tipo?.toInt()) {
            OBJETO_TIPO_POCION_FORJAMAGIA, OBJETO_TIPO_RUNA_FORJAMAGIA -> if (objeto.tieneStatTexto(
                    STAT_POTENCIA_RUNA
                )
            ) {
                try {
                    return objeto.getParamStatTexto(STAT_POTENCIA_RUNA, 3).toInt(16)
                } catch (ignored: Exception) {
                }
            }
        }
        return 0
    }

    @JvmStatic
    fun esStatDePelea(statID: Int): Boolean {
        when (statID) {
            STAT_MAS_DAÑOS_DE_AGUA, STAT_MAS_DAÑOS_DE_AIRE, STAT_MAS_DAÑOS_DE_FUEGO, STAT_MAS_DAÑOS_DE_TIERRA, STAT_MAS_DAÑOS_DE_NEUTRAL, STAT_MAS_DAÑOS_EMPUJE, STAT_MAS_REDUCCION_CRITICOS, STAT_MAS_DAÑOS_CRITICOS, STAT_MAS_REDUCCION_EMPUJE, STAT_MAS_RETIRO_PA, STAT_MAS_RETIRO_PM, STAT_MAS_HUIDA, STAT_MAS_PLACAJE, STAT_MAS_ESQUIVA_PERD_PA, STAT_MAS_ESQUIVA_PERD_PM, STAT_MAS_INICIATIVA, STAT_MAS_AGILIDAD, STAT_MAS_FUERZA, STAT_MAS_SUERTE, STAT_MAS_SABIDURIA, STAT_MAS_VITALIDAD, STAT_MAS_INTELIGENCIA, STAT_MAS_PA, STAT_MAS_PA_2, STAT_MAS_PM, STAT_MAS_ALCANCE, STAT_MAS_DAÑOS, STAT_MAS_PORC_DAÑOS, STAT_MAS_PODS, STAT_MAS_PROSPECCION, STAT_MAS_CURAS, STAT_MAS_RES_PORC_TIERRA, STAT_MAS_RES_PORC_AGUA, STAT_MAS_RES_PORC_AIRE, STAT_MAS_RES_PORC_FUEGO, STAT_MAS_RES_PORC_NEUTRAL, STAT_MAS_RES_FIJA_TIERRA, STAT_MAS_RES_FIJA_AGUA, STAT_MAS_RES_FIJA_AIRE, STAT_MAS_RES_FIJA_FUEGO, STAT_MAS_RES_FIJA_NEUTRAL, STAT_MAS_RES_PORC_PVP_TIERRA, STAT_MAS_RES_PORC_PVP_AGUA, STAT_MAS_RES_PORC_PVP_AIRE, STAT_MAS_RES_PORC_PVP_FUEGO, STAT_MAS_RES_PORC_PVP_NEUTRAL, STAT_MAS_GOLPES_CRITICOS, STAT_MAS_CRIATURAS_INVO, STAT_REENVIA_DAÑOS, STAT_MAS_DAÑOS_TRAMPA, STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA, STAT_MAS_PORC_DAÑOS_TRAMPA, STAT_MAS_VELOCIDAD, STAT_MAS_RES_FIJA_PVP_TIERRA, STAT_MAS_RES_FIJA_PVP_AGUA, STAT_MAS_RES_FIJA_PVP_AIRE, STAT_MAS_RES_FIJA_PVP_FUEGO, STAT_MAS_RES_FIJA_PVP_NEUTRAL, STAT_REDUCCION_FISICA, STAT_REDUCCION_MAGICA, STAT_MAS_DAÑOS_REDUCIDOS_ARMADURAS_FECA -> return true
        }
        return false
    }

    @JvmStatic
    fun getPesoStat(statID: Int): Float {
        var pesoRuna = 0f
        when (statID) {
            STAT_REDUCCION_FISICA, STAT_REDUCCION_MAGICA, STAT_MAS_DAÑOS_EMPUJE, STAT_MAS_REDUCCION_CRITICOS, STAT_MAS_DAÑOS_CRITICOS, STAT_MAS_REDUCCION_EMPUJE, STAT_MAS_DAÑOS_REDUCIDOS_NO_FECA, STAT_MAS_DAÑOS_REDUCIDOS_ARMADURAS_FECA -> pesoRuna =
                10f
            STAT_MAS_INICIATIVA, STAT_MAS_VELOCIDAD -> pesoRuna = 0.1f
            STAT_MAS_DAÑOS_DE_AGUA, STAT_MAS_DAÑOS_DE_TIERRA, STAT_MAS_DAÑOS_DE_AIRE, STAT_MAS_DAÑOS_DE_FUEGO, STAT_MAS_DAÑOS_DE_NEUTRAL, STAT_MAS_DAÑOS_TRAMPA -> pesoRuna =
                5f
            STAT_MAS_ESQUIVA_PERD_PA, STAT_MAS_ESQUIVA_PERD_PM, STAT_MAS_PODS -> pesoRuna =
                0.25f
            STAT_MAS_VITALIDAD -> pesoRuna = 0.50f
            STAT_MAS_FUERZA, STAT_MAS_AGILIDAD, STAT_MAS_SUERTE, STAT_MAS_INTELIGENCIA, STAT_MAS_RETIRO_PA, STAT_MAS_RETIRO_PM -> pesoRuna =
                1f
            STAT_MAS_DAÑO_FISICO, STAT_MAS_PORC_DAÑOS, STAT_MAS_PORC_DAÑOS_TRAMPA, STAT_MAS_RES_FIJA_TIERRA, STAT_MAS_RES_FIJA_AGUA, STAT_MAS_RES_FIJA_AIRE, STAT_MAS_RES_FIJA_FUEGO, STAT_MAS_RES_FIJA_NEUTRAL, STAT_MAS_RES_FIJA_PVP_TIERRA, STAT_MAS_RES_FIJA_PVP_AGUA, STAT_MAS_RES_FIJA_PVP_AIRE, STAT_MAS_RES_FIJA_PVP_FUEGO, STAT_MAS_RES_FIJA_PVP_NEUTRAL, STAT_MAS_HUIDA, STAT_MAS_PLACAJE -> pesoRuna =
                2f
            STAT_MAS_SABIDURIA, STAT_MAS_PROSPECCION, STAT_ARMA_CAZA -> pesoRuna = 3f
            STAT_MAS_RES_PORC_TIERRA, STAT_MAS_RES_PORC_AGUA, STAT_MAS_RES_PORC_AIRE, STAT_MAS_RES_PORC_FUEGO, STAT_MAS_RES_PORC_NEUTRAL, STAT_MAS_RES_PORC_PVP_TIERRA, STAT_MAS_RES_PORC_PVP_AGUA, STAT_MAS_RES_PORC_PVP_AIRE, STAT_MAS_RES_PORC_PVP_FUEGO, STAT_MAS_RES_PORC_PVP_NEUTRAL -> pesoRuna =
                6f
            STAT_MAS_DAÑOS, STAT_MAS_CURAS -> pesoRuna = 20f
            STAT_MAS_GOLPES_CRITICOS, STAT_MAS_CRIATURAS_INVO -> pesoRuna = 29f
            STAT_REENVIA_DAÑOS -> pesoRuna = 30f
            STAT_MAS_ALCANCE -> pesoRuna = 51f
            STAT_MAS_PM -> pesoRuna = 80f
            STAT_MAS_PA -> pesoRuna = 85f
        }
        return pesoRuna
    }

    fun convertirStatsEnPuntosKoliseo(s: Stats): Int {
        var suma = 0
        for ((key, value) in s.entrySet) {
            suma += (getPesoStat(key).toInt() * value)
        }
        return suma
    }

    fun excedioLimitePeso(runa: Objeto, cant: Int): Boolean {
        val statID = getStatPorRunaPocima(runa)
        return if (statID == -1) {
            false
        } else getPesoStat(statID) * cant > AtlantaMain.MAX_PESO_POR_STAT
    }

    fun excedioLimiteExomagia(statID: Int, cantidad: Int): Boolean {
        return if (AtlantaMain.LIMITE_STATS_EXO_FORJAMAGIA.containsKey(statID)) {
            AtlantaMain.LIMITE_STATS_EXO_FORJAMAGIA[statID]!! > cantidad
        } else false
    }

    fun excedioLimiteOvermagia(statID: Int, cantidad: Int): Boolean {
        return if (AtlantaMain.LIMITE_STATS_OVER_FORJAMAGIA.containsKey(statID)) {
            AtlantaMain.LIMITE_STATS_OVER_FORJAMAGIA[statID]!! > cantidad
        } else false
    }

    fun excedioLimiteMagueoDeRuna(runaID: Int, cant: Int): Boolean {
        when (runaID) {
            1519, 1522, 1524, 1525, 1523 -> return cant >= 50
            1545, 1547, 1549, 1550 -> return cant >= 70
            1551, 1553, 1555, 1556 -> return cant >= 101
            7443 -> return cant >= 100
            7444 -> return cant >= 200
            7445, 1554 -> return cant >= 404
            7448 -> return cant >= 300
            7449 -> return cant >= 600
            7450 -> return cant >= 1010
            1521 -> return cant >= 11
            1546 -> return cant >= 22
            1552 -> return cant >= 34
            1548 -> return cant >= 250
            10057 -> return cant >= 1
        }
        return false
    }
}