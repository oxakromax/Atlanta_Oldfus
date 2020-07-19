package estaticos

import estaticos.GestorSalida.ENVIAR_BAT2_CONSOLA
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS
import estaticos.database.GestorSQL
import estaticos.database.GestorSQL.LOG_SQL
import estaticos.database.GestorSQL.iniciarCommit
import estaticos.database.GestorSQL.iniciarConexion
import servidor.ServidorHandler
import servidor.ServidorServer
import servidor.ServidorSocket
import sincronizador.ExchangeClient
import utilidades.algoritmos.FuncionesParaThreads
import variables.hechizo.EfectoHechizo
import variables.montura.Montura
import variables.npc.NPC
import variables.pelea.Pelea.Companion.LOG_COMBATES
import variables.personaje.Personaje
import java.io.*
import java.net.InetAddress
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.thread
import kotlin.system.exitProcess

//import utilidades.algoritmos.FuncionesParaThreads.*
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.net.URLConnection;
//import java.nio.charset.Charset;
object AtlantaMain {
    val ELIMINANDO_OBJETOS = ArrayList<Thread>()

    @JvmField
    val COMANDOS_PERMITIDOS = ArrayList<String>()

    @JvmField
    val COMANDOS_VIP = ArrayList<String>()

    @JvmField
    val SISTEMA_ITEMS_EXO_TIPOS_NO_PERMITIDOS = ArrayList<Short>()

    @JvmField
    val MAPAS_MODO_HEROICO = ArrayList<Short>()
    private val RUNAS_NO_PERMITIDAS = ArrayList<Int>()
    val MOBS_DOBLE_ORBES = ArrayList<Int>()
    val MOBS_NO_ORBES = ArrayList<Int>()

    @JvmField
    val IDS_NPCS_VENDE_OBJETOS_STATS_MAXIMOS = ArrayList<Int>()

    @JvmField
    val IDS_OBJETOS_STATS_MAXIMOS = ArrayList<Int>()

    @JvmField
    val IDS_OBJETOS_STATS_RANDOM = ArrayList<Int>()

    @JvmField
    val IDS_OBJETOS_STATS_MINIMOS = ArrayList<Int>()
    val SALVAR_LOGS_TIPO_COMBATE = ArrayList<Byte>()
    val PERMITIR_MULTIMAN_TIPO_COMBATE = ArrayList<Byte>()

    @JvmField
    val OGRINAS_CREAR_CLASE: MutableMap<Byte, Int> = TreeMap()

    @JvmField
    val PRECIOS_SERVICIOS: MutableMap<String, String> = TreeMap()
    val MAX_GOLPES_CAC: MutableMap<Int, Int> = TreeMap()
    private const val GFX_CREA_TU_ITEM_ESCUDOS =
        "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,71,72,73,74"

    //
    @JvmField
    val SUBAREAS_NO_PVP = ArrayList<Int>()

    @JvmField
    val TIPO_RECURSOS = ArrayList<Short>()

    @JvmField
    val OBJ_NO_PERMITIDOS = ArrayList<Int>()

    @JvmField
    val TIPO_ALIMENTO_MONTURA = ArrayList<Short>()
    val PUBLICIDAD = ArrayList<String>()
    const val SEGUNDOS_INICIO_PELEA = 45 // segundos
    val IP_MULTISERVIDOR = CopyOnWriteArrayList<String>() // 25.91.217.194
    private val IP_PERMTIDAS = CopyOnWriteArrayList<String>() // 25.91.217.194
    const val SRAM_EMPUJADOR = true

    @JvmField
    val LIMITE_STATS_SIN_BUFF: MutableMap<Int, Int> = TreeMap()

    @JvmField
    val LIMITE_STATS_CON_BUFF: MutableMap<Int, Int> = TreeMap()

    @JvmField
    val LIMITE_STATS_EXO_FORJAMAGIA: MutableMap<Int, Int> = TreeMap()

    @JvmField
    val LIMITE_STATS_OVER_FORJAMAGIA: MutableMap<Int, Int> = TreeMap()

    //
    @JvmField
    val PALABRAS_PROHIBIDAS = ArrayList<String>()
    private const val ARCHIVO_CONFIG = "config_Servidor.txt"

    @JvmField
    var ES_LOCALHOST = false

    //
//
//
//
    @JvmField
    var MAX_PACKETS_PARA_RASTREAR = 10

    @JvmField
    var MAX_CARACTERES_SONIDO = 50

    @JvmField
    var MAX_PACKETS_DESCONOCIDOS = 20

    @JvmField
    var URL_LINK_COMPRA = ""

    @JvmField
//    var RESET_CON_WIN = false
    var URL_LINK_VOTO = ""

    @JvmField
    var URL_LINK_BUG = ""

    @JvmField
    var URL_IMAGEN_VOTO = ""

    @JvmField
    var URL_BACKUP_PHP = ""
    var URL_DETECTAR_DDOS = ""
    var URL_LINK_MP3 = "http://localhost/mp3/"

    @JvmField
    var DIRECTORIO_LOCAL_MP3 = "C://wamp/www/mp3/"

    @JvmField
    var ID_MIMOBIONTE = -1
    var ID_ORBE = 0
    var INDEX_IP = 0

    @JvmField
    var TIME_SLEEP_PACKETS_CARGAR_MAPA = 25

    @JvmField
    var CANTIDAD_GRUPO_MOBS_MOVER_POR_MAPA = 5

    @JvmField
    var VECES_PARA_BAN_IP_SIN_ESPERA = 3

    @JvmField
    var NIVEL_INTELIGENCIA_ARTIFICIAL = 12

    // horas
    @JvmField
    var HORA_NOCHE = 2

    @JvmField
    var MINUTOS_NOCHE = 0

    @JvmField
    var HORA_DIA = 14

    @JvmField
    var MINUTOS_DIA = 0

    // public static short PODER_PRISMA = 100;
// public static short PDV_PRISMA = 1000;
// public static short DIVISOR_PP = 2000;
    private var ACTIVAR_CONSOLA = true

    @JvmField
    var MOSTRAR_RECIBIDOS = false

    @JvmField
    var MOSTRAR_ENVIOS = false
    var MOSTRAR_SINCRONIZACION = false

    // public static boolean REGISTER_SENDING = false;
// public static boolean REGISTER_RECIVED = true;
// MODOS
    @JvmField
    var MODO_DEBUG = false
    var MODO_MAPAS_LIMITE = false

    @JvmField
    var MODO_PVP = false

    @JvmField
    var MODO_HEROICO = false

    @JvmField
    var MODO_ANKALIKE = false

    @JvmField
    var MODO_BATTLE_ROYALE = false
    private var MODO_BETA = false

    @JvmField
    var NPC_BOUTIQUE: NPC? = null

    @JvmField
    var ID_NPC_BOUTIQUE: Short = 0

    @JvmField
    var ID_BOLSA_OGRINAS = 0

    @JvmField
    var ID_BOLSA_CREDITOS = 0

    @JvmField
    var IMPUESTO_BOLSA_OGRINAS = 1

    @JvmField
    var IMPUESTO_BOLSA_CREDITOS = 1

    @JvmField
    var DURABILIDAD_REDUCIR_OBJETO_CRIA = 10

    @JvmField
    var DIAS_PARA_BORRAR: Short = 60

    @JvmField
    var PALABRA_CLAVE_CONSOLA = ""
    private var PERMITIR_MULTIMAN = "0,4"

    @JvmField
    var SISTEMA_ITEMS_PERFECTO_MULTIPLICA_POR = 2f

    @JvmField
    var SISTEMA_ITEMS_EXO_PA_PRECIO: Short = 100

    @JvmField
    var SISTEMA_ITEMS_EXO_PM_PRECIO: Short = 100

    @JvmField
    var SISTEMA_ITEMS_TIPO_DE_PAGO = "OGRINAS"

    @JvmField
    var COLOR_CELDAS_PELEA_AGRESOR = ""

    // CREAR TU ITEM
    private var GFX_CREA_TU_ITEM_CAPAS =
        "1,2,3,4,5,7,8,9,10,11,12,15,16,17,18,19,21,22,23,33,34,35,36,37,38,39,40,41,42,43,44,46,47,48,49,50,51,52,53,54,55,56,58,59,60,61,62,63,64,65,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,89,90,91,92,93,94,95,96,97,98,99,100,101,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,224,225,226,227,230,231,232,233,234,236,237,238,239,240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255,256,257,258,259"
    private var GFX_CREA_TU_ITEM_AMULETOS =
        "1,2,3,4,5,6,7,8,9,10,11,12,13,15,16,17,18,19,20,22,23,24,25,26,27,28,29,30,31,32,33,34,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,224,225"
    private var GFX_CREA_TU_ITEM_ANILLOS =
        "1,2,3,4,5,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,232,233,234,235,236,237,238,239,240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255,256"
    private var GFX_CREA_TU_ITEM_CINTURONES =
        "3,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,232,233,234,235,236,237"
    private var GFX_CREA_TU_ITEM_BOTAS =
        "1,2,3,4,5,6,7,8,9,10,11,12,14,15,16,17,18,19,20,21,22,23,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230"
    private var GFX_CREA_TU_ITEM_SOMBREROS =
        "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,20,21,22,23,24,25,26,27,28,29,30,31,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,61,64,65,66,67,68,69,70,71,72,73,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,102,103,108,109,110,111,112,114,115,116,117,118,119,120,121,122,123,124,125,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,199,200,201,202,203,204,205,206,207,208,209,210,212,213,214,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,232,233,234,235,236,237,238,239,240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255,256,257,259,260,261,262,263,264,265,266,268,269,270,271,272,273,274,275,276,277,278,279,280,281,282,283,284,285,286,287,288,289,290,291,292,293,294,295,296,297,300,301,302,304,305,306,307,308,309,310,313,314,315,316,317,318,319,320,321,322,323,324,325,326,327,328,329,330,331,332,333,334,335,336,337,339,340,341,342,343"
    private var GFX_CREA_TU_ITEM_DOFUS = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18"

    // BONUS RESETS
    var SUFIJO_RESET = "R"

    @JvmField
    var BONUS_RESET_PUNTOS_HECHIZOS: Short = 3

    @JvmField
    var BONUS_RESET_PUNTOS_STATS: Short = 200

    // KAMAS
    @JvmField
    var KAMAS_RULETA_JALATO = 1000

    @JvmField
    var KAMAS_BANCO = 0

    @JvmField
    var KAMAS_MOSTRAR_PROBABILIDAD_FORJA = 0

    // OGRINAS
    @JvmField
    var OGRINAS_POR_VOTO: Short = -1

    @JvmField
    var VALOR_KAMAS_POR_OGRINA = 0

    @JvmField
    var DIAS_INTERCAMBIO_COMPRAR_SISTEMA_ITEMS = 0

    // LOTERIA
    @JvmField
    var PRECIO_LOTERIA = 5

    @JvmField
    var PREMIO_LOTERIA = 50

    @JvmField
    var GANADORES_POR_BOLETOS = 20

    // SISTEMA OGRINAS
//
    var STR_MAPAS_LIMITE = "7411,8534,951"
    var STR_SUBAREAS_LIMITE = "1,2"

    @JvmField
    var MAPAS_KOLISEO = "12189,12167,12155,12181,12147,12136,12128,12108,12096,12094,12092,12069,12068"

    @JvmField
    var MENSAJE_BIENVENIDA = ""

    @JvmField
    var PANEL_BIENVENIDA = ""

    @JvmField
    var PANEL_DESPUES_CREAR_PERSONAJE = ""

    @JvmField
    var TUTORIAL_FR = ""

    @JvmField
    var TUTORIAL_ES = ""

    @JvmField
    var MENSAJE_SERVICIOS = "Mensaje de lista de servicios del servidor"

    @JvmField
    var MENSAJE_COMANDOS = "Mensaje de lista de comandos del servidor"

    @JvmField
    var MENSAJE_VIP = "Beneficios de ser vip"

    @JvmField
    var MENSAJE_ERROR_OGRINAS_CREAR_CLASE = "PRICE OGRINES"
    var CANALES_COLOR_CHAT = ""

    // public static String COLOR_CHAT_ALL = "#777777";
// MAPA CELDA
    @JvmField
    var CERCADO_MAPA_CELDA = ""

    @JvmField
    var START_MAPA_CELDA = "7411,311"

    @JvmField
    var SHOP_MAPA_CELDA = "7411,311"

    @JvmField
    var PVP_MAPA_CELDA = "952,342"

    // MUTE
    @JvmField
    var MUTE_CANAL_INCARNAM = false

    @JvmField
    var MUTE_CANAL_COMERCIO = false

    @JvmField
    var MUTE_CANAL_ALINEACION = false

    @JvmField
    var MUTE_CANAL_RECLUTAMIENTO = false

    // PARAMETROS
    @JvmField
    var PARAM_SUPER_CRAFT_SPEED = true

    @JvmField
    var PARAM_CORREGIR_NOMBRE_JUGADOR = false

    @JvmField
    var PARAM_ANTIFLOOD = true

    @JvmField
    var PARAM_LIMITAR_RECAUDADOR_GREMIO_POR_ZONA = false

    @JvmField
    var PARAM_RESETEAR_LUPEAR_OBJETOS_MAGUEADOS = false

    @JvmField
    var PARAM_DESHABILITAR_SQL = false

    @JvmField
    var PARAM_FM_CON_POZO_RESIDUAL = true

    @JvmField
    var PARAM_MOSTRAR_STATS_INVOCACION = true

    @JvmField
    var PARAM_ENCRIPTAR_PACKETS = false

    @JvmField
    var PARAM_PERMITIR_ORNAMENTOS = true
    var PARAM_RESTRINGIR_COLOR_DIA = false

    @JvmField
    var PARAM_REINICIAR_ESTRELLAS_SI_LLEGA_MAX = false
    private var PARAM_CLASIFICAR_POR_STUFF_EN_KOLISEO = false
    private var PARAM_CLASIFICAR_POR_RANKING_EN_KOLISEO = false

    @JvmField
    var PARAM_MOSTRAR_APODO_LISTA_AMIGOS = true

    @JvmField
    var PARAM_MOSTRAR_EXP_MOBS = true

    @JvmField
    var PARAM_PERMITIR_MISMAS_CLASES_EN_KOLISEO = true

    @JvmField
    var PARAM_PERMITIR_DESACTIVAR_ALAS = true

    @JvmField
    var PARAM_AGREDIR_ALAS_DESACTIVADAS = true

    @JvmField
    var PARAM_PERMITIR_MULTICUENTA_PELEA_KOLISEO = false
    var PARAM_PERMITIR_MULTICUENTA_PELEA_RECAUDADOR = true

    @JvmField
    var PARAM_PERMITIR_MULTICUENTA_PELEA_PVP = false

    @JvmField
    var PARAM_SISTEMA_IP_ESPERA = true

    @JvmField
    var PARAM_BORRAR_CUENTAS_VIEJAS = false

    @JvmField
    var PARAM_AUTO_COMMIT = false

    @JvmField
    var PARAM_AGREDIR_NEUTRAL = true

    @JvmField
    var PARAM_MOVER_MOBS_FIJOS = true

    @JvmField
    var PARAM_MOBS_RANDOM_REAPARECER_OTRA_CELDA = true

    @JvmField
    var PARAM_CRIAR_MONTURA = true

    @JvmField
    var PARAM_TIMER_ACCESO = true

    @JvmField
    var PARAM_START_EMOTES_COMPLETOS = false

    @JvmField
    var PARAM_SOLO_PRIMERA_VEZ = false
    private var PARAM_PVP = true

    @JvmField
    var PARAM_PERMITIR_MOBS = true

    @JvmField
    var PARAM_ACTIVAR_AURA = true

    @JvmField
    var PARAM_AURA_VIP = true

    @JvmField
    var PARAM_PERDER_ENERGIA = true

    @JvmField
    var PARAM_COMANDOS_JUGADOR = true

    @JvmField
    var PARAM_ALMANAX = false

    @JvmField
    var PARAM_ESTRELLAS_RECURSOS = false
    var PARAM_HEROICO_PIERDE_ITEMS_VIP = true

    @JvmField
    var PARAM_LOTERIA = false

    @JvmField
    var PARAM_LOTERIA_OGRINAS = true

    @JvmField
    var PARAM_PERDER_PDV_ARMAS_ETEREAS = true

    @JvmField
    var PARAM_HEROICO_GAME_OVER = true

    @JvmField
    var PARAM_DEVOLVER_OGRINAS = false

    @JvmField
    var PARAM_KOLISEO = false

    @JvmField
    var PARAM_LADDER_NIVEL = true

    @JvmField
    var PARAM_LADDER_KOLISEO = false

    @JvmField
    var PARAM_LADDER_PVP = true

    @JvmField
    var PARAM_LADDER_GREMIO = true

    @JvmField
    var PARAM_LADDER_EXP_DIA = true

    @JvmField
    var PARAM_LADDER_STAFF = false

    @JvmField
    var PARAM_ANTI_SPEEDHACK = false
    var PARAM_MOSTRAR_CHAT_VIP_TODOS = false

    // public static boolean PARAM_CREAR_ITEM;
    var PARAM_VER_JUGADORES_KOLISEO = false

    // public static boolean PARAM_SISTEMA_OBJETOS_POR_OGRINAS;
    @JvmField
    var PARAM_PRECIO_RECURSOS_EN_OGRINAS = false
    var PARAM_BESTIARIO = false

    @JvmField
    var PARAM_TODOS_MOBS_EN_BESTIARIO = false

    @JvmField
    var PARAM_AUTO_RECUPERAR_TODA_VIDA = false

    @JvmField
    var PARAM_CRAFT_SIEMPRE_EXITOSA = false

    @JvmField
    var PARAM_CRAFT_PERFECTO_STATS = false

    @JvmField
    var PARAM_MONTURA_SIEMPRE_MONTABLES = false
    var PARAM_JUGAR_RAPIDO = false

    @JvmField
    var PARAM_ANTI_DDOS = false
    var PARAM_MOSTRAR_NRO_TURNOS = false

    @JvmField
    var PARAM_RESET_STATS_OBJETO = false

    @JvmField
    var PARAM_OBJETOS_PEFECTOS_COMPRADOS_NPC = false

    @JvmField
    var PARAM_DAR_ALINEACION_AUTOMATICA = false

    @JvmField
    var PARAM_CINEMATIC_CREAR_PERSONAJE = true
    var PARAM_REGISTRO_LOGS_JUGADORES = false
    private var PARAM_REGISTRO_LOGS_SQL = false

    @JvmField
    var PARAM_NOMBRE_ADMIN = true

    @JvmField
    var PARAM_OBJETOS_OGRINAS_LIGADO = false
    private var PARAM_VARIOS_RECAUDADORES = false

    @JvmField
    var PARAM_ELIMINAR_PERSONAJES_BUG = true

    @JvmField
    var PARAM_AGREDIR_JUGADORES_ASESINOS = true

    @JvmField
    var PARAM_MOSTRAR_IP_CONECTANDOSE = false

    @JvmField
    var PARAM_MENSAJE_ASESINOS_HEROICO = true
    var PARAM_MENSAJE_ASESINOS_PVP = false
    var PARAM_MENSAJE_ASESINOS_KOLISEO = false

    @JvmField
    var PARAM_GUARDAR_LOGS_INTERCAMBIOS = true
    var PARAM_FORMULA_TIPO_OFICIAL = false

    @JvmField
    var PARAM_STOP_SEGUNDERO = false

    @JvmField
    var PARAM_BOTON_BOUTIQUE = false

    @JvmField
    var PARAM_SISTEMA_ITEMS_SOLO_PERFECTO = false

    @JvmField
    var PARAM_SISTEMA_ITEMS_EXO_PA_PM = false
    var PARAM_GANAR_HONOR_RANDOM = false

    @JvmField
    var PARAM_RESET_STATS_PLAYERS = false

    @JvmField
    var PARAM_AGRESION_ADMIN = true
    var PARAM_AUTO_SALTAR_TURNO = false
    private var PARAM_TITULO_MAESTRO_OFICIO = true
    var PARAM_GANAR_KAMAS_PVP = true
    var PARAM_GANAR_EXP_PVP = true

    @JvmField
    var PARAM_ALIMENTAR_MASCOTAS = true

    @JvmField
    var PARAM_MASCOTAS_PERDER_VIDA = true

    @JvmField
    var PARAM_LIMITE_MIEMBROS_GREMIO = true
    var PARAM_MOSTRAR_PROBABILIDAD_TACLEO = false
    var PARAM_SISTEMA_ORBES = false

    @JvmField
    var PARAM_MATRIMONIO_GAY = false

    @JvmField
    var PARAM_PERMITIR_OFICIOS = true
    var PARAM_SALVAR_LOGS_AGRESION_SQL = false

    @JvmField
    var PARAM_MOB_TENER_NIVEL_INVOCADOR_PARA_EMPUJAR = false
    var PARAM_NO_USAR_OGRINAS = false
    var PARAM_NO_USAR_CREDITOS = false

    @JvmField
    var PARAM_PERMITIR_DESHONOR = true

    @JvmField
    var PARAM_PERMITIR_AGRESION_MILICIANOS = true
    var PARAM_PERMITIR_MILICIANOS_EN_PELEA = true
    var PARAM_CAPTURAR_MONTURA_COMO_PERGAMINO = false
    var PARAM_EXPULSAR_PREFASE_PVP = true
    var PARAM_JUGADORES_HEROICO_MORIR = true

    @JvmField
    var PARAM_INFO_DAÑO_BATALLA = false

    @JvmField
    var PARAM_BOOST_SACRO_DESBUFEABLE = false

    @JvmField
    var PARAM_REINICIAR_CANALES = false
    var PARAM_PERMITIR_BONUS_PELEA_AFECTEN_PROSPECCION = true
    var PARAM_PERMITIR_BONUS_ESTRELLAS = true
    var PARAM_PERMITIR_BONUS_DROP_RETOS = true
    var PARAM_PERMITIR_BONUS_EXP_RETOS = true

    @JvmField
    var PARAM_PERMITIR_ADMIN_EN_LADDER = false

    @JvmField
    var PARAM_MOVER_MULTIPLE_OBJETOS_SOLO_ABONADOS = false

    @JvmField
    var PARAM_EXP_PVP_MISION_POR_TABLA = false

    @JvmField
    var PARAM_MOSTRAR_MONTURAS_CERCADOS = false
    var PARAM_MOSTRAR_NOMBRE_ADMIN_5 = false

    //
    @JvmField
    var MENSAJE_TIMER_REBOOT = ""
    var ARMAS_ENCARNACIONES = "9544,9545,9546,9547,9548,10125,10126,10127,10133"

    @JvmField
    var SABIDURIA_PARA_REENVIO = 100

    // TIEMPOS MILISEGUNDOS
    @JvmField
    var MILISEGUNDOS_ANTI_FLOOD = 5 * 1000 // 5 segundos
    var MILISEGUNDOS_CERRAR_SERVIDOR = 3 * 1000 // segundos

    // TIEMPO SEGUNDOS
    @JvmField
    var SEGUNDOS_ENTRE_DESAFIOS_PJ = 5

    @JvmField
    var SEGUNDOS_ARENA = 120 * 60 // segundos
    var SEGUNDOS_TURNO_PELEA = 30 // segundos

    @JvmField
    var SEGUNDOS_CANAL_COMERCIO = 45 // segundos

    @JvmField
    var SEGUNDOS_CANAL_RECLUTAMIENTO = 20 // segundos

    @JvmField
    var SEGUNDOS_CANAL_ALINEACION = 20 // segundos

    @JvmField
    var SEGUNDOS_CANAL_VIP = 10 // segundos

    @JvmField
    var SEGUNDOS_CANAL_INCARNAM = 5 // segundos

    @JvmField
    var SEGUNDOS_CANAL_ALL = 5 // segundos

    @JvmField
    var SEGUNDOS_INACTIVIDAD = 30 * 60 // segundos
    var SEGUNDOS_TRANSACCION_BD = 30 // segundos

    @JvmField
    var SEGUNDOS_MOVER_MONTURAS = 20 // segundos (10 miuntos)

    @JvmField
    var SEGUNDOS_MOVER_RECAUDADOR = 30 // segundos (10 miuntos)

    @JvmField
    var SEGUNDOS_MOVER_GRUPO_MOBS = 30 // segundos (1 minuto)

    @JvmField
    var SEGUNDOS_PEQUEÑO_SALVAR = 120

    @JvmField
    var SEGUNDOS_ESTRELLAS_GRUPO_MOBS = 20 * 60 // segundos (20 minutos)

    @JvmField
    var SEGUNDOS_ESTRELLAS_RECURSOS = 15 * 60 // segundos (15 minutos)

    @JvmField
    var SEGUNDOS_PUBLICIDAD = 55 * 60 // segundos (55 minutos)

    @JvmField
    var SEGUNDOS_SALVAR = 60 * 60 // segundos (60 minutos = 1 hora)

    @JvmField
    var SEGUNDOS_INICIAR_KOLISEO = 3 * 60 // segundos (10 minutos)

    @JvmField
    var SEGUNDOS_LIMPIAR_MEMORIA = 30 // segundos

    @JvmField
    var SEGUNDOS_RESET_RATES = 0 // segundos

    @JvmField
    var SEGUNDOS_LIVE_ACTION = 0 // segundos

    @JvmField
    var SEGUNDOS_REBOOT_SERVER = 24 * 60 * 60 // minutos (24 horas = 1 dia)

    @JvmField
    var SEGUNDOS_DETECTAR_DDOS = 60 // 5 minutos

    @JvmField
    var SEGUNDOS_REAPARECER_MOBS = 0

    @JvmField
    var SEGUNDOS_AGREDIR_RECIEN_LLEGADO_MAPA = 2

    // TIEMPO MINUTOS
    @JvmField
    var MINUTOS_MISION_PVP = 10 // minutos (10 minutos)

    @JvmField
    var MINUTOS_ALIMENTACION_MASCOTA = 10 // minutos (10 minutos)

    @JvmField
    var MINUTOS_GESTACION_MONTURA = 60 // minutos (1 hora)

    @JvmField
    var MINUTOS_SPAMEAR_BOTON_VOTO = 30

    @JvmField
    var MINUTOS_VALIDAR_VOTO = 180

    @JvmField
    var MINUTOS_PENALIZACION_KOLISEO = 0

    // TIEMPO HORAS
    @JvmField
    var HORAS_VOLVER_A_PONER_RECAUDADOR_MAPA = 6

    @JvmField
    var HORAS_PERDER_CRIAS_MONTURA = 24

    // INFO SERVER
    var SERVIDOR_PRIORIDAD = 10

    // public static int PUERTO_MULTISERVIDOR = 444;
    @JvmField
    var PUERTO_SERVIDOR = 5555
    var PUERTO_SINCRONIZADOR = 19999
    var IP_PUBLICA_SERVIDOR = ""
    var BD_HOST: String? = null
    var BD_PORT = "3306"
    var BD_USUARIO: String? = null
    var BD_PASS: String? = null
    var BD_ESTATICA: String? = null
    var BD_DINAMICA: String? = null
    var BD_CUENTAS: String? = null

    @JvmField
    var NOMBRE_SERVER = "TEST"

    @JvmField
    var SERVIDOR_ID = 18

    @JvmField
    var ACCESO_ADMIN_MINIMO = 0

    // RATES
    @JvmField
    var RATE_XP_PVP = 1

    @JvmField
    var RATE_XP_PVM = 1f

    @JvmField
    var RATE_RANDOM_MOB = 3.0

    @JvmField
    var RATE_RANDOM_PUNTOS = 2.0

    @JvmField
    var RATE_RANDOM_ITEM = 3.5
    var RATE_RANDOM_MOB_BASE = 2.0
    var RATE_RANDOM_ITEM_BASE = 2.0
    private var RATE_RANDOM_PUNTOS_BASE = 1.0
    var RATE_XP_MONTURA = 1
    private var RATE_XP_MONTURA_ABONADOS = 1
    var RATE_XP_RECAUDADOR = 1

    @JvmField
    var RATE_DROP_NORMAL = 1

    @JvmField
    var RATE_KAMAS = 1

    @JvmField
    var RATE_XP_OFICIO = 1

    @JvmField
    var RATE_XP_PVM_ABONADOS = 1f
    var RATE_DROP_ABONADOS = 1f

    @JvmField
    var RATE_KAMAS_ABONADOS = 1f

    @JvmField
    var RATE_XP_OFICIO_ABONADOS = 1f

    @JvmField
    var RATE_HONOR = 1

    @JvmField
    var RATE_CRIANZA_MONTURA = 1

    @JvmField
    var RATE_CRIANZA_MONTURA_ABONADOS = 2
    var RATE_CAPTURA_MONTURA = 1
    private var RATE_CAPTURA_MONTURA_ABONADOS = 2
    var RATE_DROP_ARMAS_ETEREAS = 1

    @JvmField
    var RATE_PODS = 2

    @JvmField
    var RATE_FM = 1

    @JvmField
    var RATE_CONQUISTA_EXPERIENCIA = 1

    @JvmField
    var RATE_CONQUISTA_RECOLECTA = 1

    @JvmField
    var RATE_CONQUISTA_DROP = 1

    // INICIO PERSONAJE
    @JvmField
    var INICIO_NIVEL = 1

    @JvmField
    var INICIO_KAMAS = 0

    @JvmField
    var INICIO_EMOTES = 1 // 7667711

    @JvmField
    var INICIO_PUNTOS_STATS = 0

    @JvmField
    var INICIO_OBJETOS = ""

    @JvmField
    var INICIO_SET_ID = ""

    @JvmField
    var INICIO_ZAAPS =
        "164,528,844,935,951,1158,1242,1841,2191,3022,3250,4263,4739,5295,6137,6855,6954,7411,8037,8088,8125,8163,8437,8785,9454,10297,10304,10317,10349,10643,11170,11210"

    @JvmField
    var INICIO_BONUS_ESTRELLAS_RECURSOS = -20

    @JvmField
    var INICIO_BONUS_ESTRELLAS_MOBS = -20

    @JvmField
    var INICIO_NIVEL_MONTURA = 1

    @JvmField
    var PUNTOS_STATS_POR_NIVEL = 5

    @JvmField
    var PUNTOS_HECHIZO_POR_NIVEL = 1

    // OTROS SERVER
    @JvmField
    var PRECIO_SISTEMA_RECURSO = 0f

    @JvmField
    var FACTOR_DEVOLVER_OGRINAS = 0.90f

    @JvmField
    var FACTOR_OBTENER_RUNAS = 1.5f
    var FACTOR_PLUS_PP_PARA_DROP = 2f

    @JvmField
    var PRECIO_CAMALEON = 100

    @JvmField
    var OGRINAS_INVITADOR = 100

    @JvmField
    var OGRINAS_INVITADO = 50
    var FACTOR_ZERO_DROP = 3

    @JvmField
    var MIN_CANTIDAD_MOBS_EN_GRUPO = 1
    var MAX_ID_OBJETO_MODELO = 99999

    @JvmField
    var MAX_CUENTAS_POR_IP = 50

    @JvmField
    var MAX_MISIONES_ALMANAX = 180

    @JvmField
    var MAX_RECAUDADORES_POR_ZONA = 1

    @JvmField
    var MAX_BONUS_ESTRELLAS_RECURSOS = 10 * 20

    @JvmField
    var MAX_BONUS_ESTRELLAS_MOBS = 50 * 20

    @JvmField
    var MAX_PESO_POR_STAT = 101

    @JvmField
    var MAX_RESETS = 3
    var MAX_CAC_POR_TURNO = 0

    @JvmField
    var MAX_PJS_POR_CUENTA = 5

    @JvmField
    var MAX_PORCENTAJE_DE_STAT_PARA_FM = 300

    @JvmField
    var PORCENTAJE_DAÑO_NO_CURABLE = 10
    var PROBABILIDAD_ARCHI_MOBS = 10

    @JvmField
    var PROBABILIDAD_PROTECTOR_RECURSOS = 1

    @JvmField
    var PROBABILIDAD_RECURSO_ESPECIAL = 1
    var PROBABLIDAD_PERDER_STATS_FM = -1

    @JvmField
    var PROBABILIDAD_ESCAPAR_MONTURA_DESPUES_FECUNDAR = 30

    @JvmField
    var HONOR_FIJO_PARA_TODOS = -1

    @JvmField
    var NIVEL_MINIMO_PARA_PVP = 25

    @JvmField
    var RANGO_NIVEL_PVP = 20

    @JvmField
    var RANGO_NIVEL_KOLISEO = 15
    var TIEMPO_INTERCAMBIABLE_REGALO = 7

    @JvmField
    var CANTIDAD_MIEMBROS_EQUIPO_KOLISEO = 1

    // KOLISEO
    @JvmField
    var MIN_NIVEL_KOLISEO = 1
    var KOLISEO_PREMIO_KAMAS = 0
    var KOLISEO_DIVISOR_XP = 3
    var KOLISEO_PREMIO_OBJETOS = ""

    @JvmField
    var MISION_PVP_KAMAS = 2000
    var MISION_PVP_OBJETOS = "10275,2;9920,1"

    // LIMITES
    var LIMITE_MAPAS = 12000

    @JvmField
    var LIMITE_LADDER = 20
    var LIMITE_REPORTES = 50

    @JvmField
    var LIMITE_SCROLL = 101

    @JvmField
    var LIMITE_MIEMBROS_GREMIO = 0

    @JvmField
    var LIMITE_OBJETOS_COFRE = 80

    @JvmField
    var LIMITE_DETECTAR_FALLA_KAMAS: Long = 10000000
    var LIMITE_PA_PVP = 12
    var LIMITE_PM_PVP = 7

    // public static short LIMITE_ALCANCE = 30;
// public static short LIMITE_PORC_RESISTENCIA_OBJETOS = 75;
// public static short LIMITE_PORC_RESISTENCIA_BUFFS = 75;
// NIVELES MAXIMOS
    @JvmField
    var NIVEL_MAX_OFICIO = 0

    @JvmField
    var NIVEL_MAX_PERSONAJE = 0

    @JvmField
    var NIVEL_MAX_MONTURA = 0

    @JvmField
    var NIVEL_MAX_GREMIO = 0

    @JvmField
    var NIVEL_MAX_ENCARNACION = 0

    @JvmField
    var NIVEL_MAX_ALINEACION = 0

    @JvmField
    var NIVEL_MAX_ESCOGER_NIVEL = 0
    var INFO_CHAT_COLOR = "009900"
    var MSG_CHAT_COLOR = "111111"
    var EMOTE_CHAT_COLOR = "222222"
    var THINK_CHAT_COLOR = "232323"
    var MSGCHUCHOTE_CHAT_COLOR = "0066FF"
    var GROUP_CHAT_COLOR = "006699"
    var ERROR_CHAT_COLOR = "C10000"
    var GUILD_CHAT_COLOR = "663399"
    var PVP_CHAT_COLOR = "DD7700"
    var RECRUITMENT_CHAT_COLOR = "737373"
    var TRADE_CHAT_COLOR = "663300"
    var MEETIC_CHAT_COLOR = "0000CC"
    var ADMIN_CHAT_COLOR = "FF00FF"
    var VIP_CHAT_COLOR = "FF00FF"

    // private static StringBuilder Log_Servidor = new StringBuilder();
    private var LOG_SERVIDOR: PrintStream? = null

    // private static StringBuilder Log_Servidor = new StringBuilder();
//	private static PrintStream LOG_SERVIDOR;
// PRIVATES
    private var DEFECTO_XP_PVM = 0f
    private var DEFECTO_XP_PVP = 0
    private var DEFECTO_XP_OFICIO = 0
    private var DEFECTO_XP_HONOR = 0
    private var DEFECTO_DROP = 0
    private var DEFECTO_KAMAS = 0
    private var DEFECTO_CRIANZA_MONTURA = 0
    var DEVOLVER_ITEMS = false
    var PORCENTAJE_DEVOLVER_ITEMS = 60.0
    var encendido = System.currentTimeMillis()
    var TOKEN = "Testing"
    var HECHIZOS_NO_DESHECHIZABLE = arrayListOf<Int>()
    var FACTOR_COMPRA = 1.3
    var FACTOR_VENTA = 0.9
    var ANUNCIO_NIVEL_MAX = true
    var OBJETOS_PELEA_PRISMA = ""
    var VALOR_RECAUDADOR = 1000

    @JvmStatic
    fun main(args: Array<String>) {
        Runtime.getRuntime().addShutdownHook(Thread(Runnable { cerrarServer() }))
        println("ATLANTA " + Constantes.VERSION_EMULADOR)
        println("By Oxakromax para OLDFUS")
        // cargando la config
        println("Cargando la configuración")
        //		leyendoIpsPermitidas();
        cargarConfiguracion(null)
        while (!IP_MULTISERVIDOR[0].equals("127.0.0.1", ignoreCase = true)) {
            try {
                val fecha =
                    Calendar.getInstance()[Calendar.DAY_OF_MONTH].toString() + "-" + (Calendar.getInstance()[Calendar.MONTH] + 1) + "-" + Calendar.getInstance()[Calendar.YEAR]
                LOG_SERVIDOR = PrintStream(
                    FileOutputStream(
                        "Logs_Servidor_" + NOMBRE_SERVER + "/Log_Servidor_" + fecha
                                + ".txt", true
                    )
                )
                LOG_SERVIDOR!!.println("---------- INICIO DEL SERVER ----------")
                LOG_SERVIDOR!!.flush()
                System.setErr(LOG_SERVIDOR)
                break
            } catch (e: IOException) {
                File("Logs_Servidor_$NOMBRE_SERVER").mkdir()
            } catch (e: Exception) {
                e.printStackTrace()
                break
            }
        }
//        if (!(IP_MULTISERVIDOR[0] == "127.0.0.1" || IP_MULTISERVIDOR[0] == "localhost" || TOKEN.equals(
//                "Testing",
//                ignoreCase = true
//            ))
//        ) {
//            if ((!GestorSQL.conexionAlterna(
//                    "slimes.sytes.net",
//                    "clientes",
//                    "takataka",
//                    "Akilesbailo"
//                ) || !GestorSQL.tokenInicio(TOKEN)
//                        )
//            ) return
//        }
        // conectado a la base de datos sql
        print("Conexión a la base de datos:  ")
        if (iniciarConexion()) {
            println("CONEXION OK!!")
        } else {
            redactarLogServidorln("CONEXION SQL INVALIDA!!")
            // System.exit(1);
            return
        }
        GestorSQL.recambiarAlterna(BD_HOST, "mint_login", BD_USUARIO, BD_PASS)
        println("Creando el Servidor ...")
        Mundo.crearServidor()
        ExchangeClient.INSTANCE?.start()
//        IniciarSincronizacion()
        if (!ServidorServer.INSTANCE.start()) {
            exitProcess(0)
        }
        if (ACTIVAR_CONSOLA) {
            Consola()
        }
        encendido = System.currentTimeMillis()
        val fixedRateTimer = fixedRateTimer(
            name = "segunderoServer",
            initialDelay = 1000, period = 1000
        ) {
            try {
                thread {
                    FuncionesParaThreads.cadaSegundo()
                }
                try {
                    ServidorServer.borrarCuentasBug(60)
                } catch (e: Exception) {
                }
            } catch (e: Exception) {
            }
        }
        fixedRateTimer(
            name = "Recolector de Basura",
            initialDelay = 10000, period = (SEGUNDOS_LIMPIAR_MEMORIA * 1000).toLong()
        ) {
            try {
                thread { System.gc() }
            } catch (e: Exception) {
            }
        }
        fixedRateTimer(name = "Desbaneador Temporal", initialDelay = 0, period = 1000) {
            try {
                ServidorHandler.filter.Desbanear()
            } catch (e: Exception) {
            }
        }
        println("Esperando que los jugadores se conecten")
        try {
            thread(true, true, null, null, 5) {
                FuncionesParaThreads.ResetExpDia()
                Thread.sleep(5000)
            }
        } catch (e: Exception) {
        }
        while (true) {
            try {
                Thread.sleep(9999999999999999)
            } catch (e: Exception) {
            } // Simplemente para que el sv no se cierre por que si
        }
    }

    //	private static void leyendoIpsPermitidas() {
//		final String url = "http://Atlanta-fenix.com/clientes/ips.txt";
//		URL obj;
//		try {
//			obj = new URL(url);
//			final URLConnection con = obj.openConnection();
//			con.setRequestProperty("Content-type", "charset=Unicode");
//			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//			String inputLine;
//			Charset utf8charset = Charset.forName("UTF-8");
//			while ((inputLine = in.readLine()) != null) {
//				String linea = new String(inputLine.getBytes(), utf8charset);
//				IP_PERMTIDAS.add(linea);
//			}
//			in.close();
//		} catch (Exception e) {
//			// e.printStackTrace();
//		}
//	}
    @JvmStatic
    fun modificarParam(p: String?, v: String?) {
        try {
            val config = BufferedReader(FileReader(ARCHIVO_CONFIG))
            var linea: String
            val str = StringBuilder()
            var tiene = false
            while (config.readLine().also { linea = it } != null) {
                if (linea.split("=".toRegex()).toTypedArray().size == 1) {
                    str.append(linea)
                } else {
                    val param = linea.split("=".toRegex()).toTypedArray()[0].trim { it <= ' ' }
                    if (param.equals(p, ignoreCase = true)) {
                        str.append(param).append(" = ").append(v)
                        tiene = true
                    } else {
                        str.append(linea)
                    }
                }
                str.append("\n")
            }
            if (!tiene) {
                str.append(p).append(" = ").append(v)
            }
            config.close()
            val mod = BufferedWriter(FileWriter(ARCHIVO_CONFIG))
            mod.write(str.toString())
            mod.flush()
            mod.close()
        } catch (ignored: Exception) {
        }
    }

    val configuracion: String
        get() {
            val str = StringBuilder()
            try {
                val config = BufferedReader(FileReader(ARCHIVO_CONFIG))
                var linea: String?
                while (config.readLine().also { linea = it } != null) {
                    str.append(linea).append("\n")
                }
                config.close()
            } catch (ignored: Exception) {
            }
            return str.toString()
        }

    fun cargarConfiguracion(perso: Personaje?) {
        try {
            val config = BufferedReader(FileReader(ARCHIVO_CONFIG))
            var linea = config.readLine()
            val parametros = ArrayList<String>()
            val repetidos: MutableMap<String, String> = TreeMap()
            while (linea != null) {
                try {
                    if (linea.contains("#") || linea.isBlank() || linea.isEmpty()) {
                        linea = config.readLine()
                        continue
                    }
                    val parametro = linea.split("=".toRegex(), 2).toTypedArray()[0].trim { it <= ' ' }
                    var valor = linea.split("=".toRegex(), 2).toTypedArray()[1].trim { it <= ' ' }
                    if (parametros.contains(parametro)) {
                        println("EN EL ARCHIVO $ARCHIVO_CONFIG SE REPITE EL PARAMETRO $parametro")
                        exitProcess(1)
                    } else {
                        parametros.add(parametro)
                    }
                    var variable = ""
                    valor = valor.replace("\\n", "\n").replace("\\t", "\t").replace("\\r", "\r").replace("\\b", "\b")
                    when (parametro.toUpperCase()) {
                        "ES_LOCALHOST" -> {
                            ES_LOCALHOST = valor.equals("true", ignoreCase = true)
                            variable = "ES_LOCALHOST"
                        }
                        "MOSTRAR_ENVIADOS", "ENVIADOS" -> {
                            MOSTRAR_ENVIOS = valor.equals("true", ignoreCase = true)
                            variable = "MOSTRAR_ENVIOS"
                        }
                        "MOSTRAR_SINCRONIZADOR", "SINCRONIZADOS", "MOSTRAR_SINCRONIZACION" -> {
                            MOSTRAR_SINCRONIZACION = valor.equals("true", ignoreCase = true)
                            variable = "MOSTRAR_SINCRONIZACION"
                        }
                        "ACTIVAR_CONSOLA" -> {
                            ACTIVAR_CONSOLA = valor.equals("true", ignoreCase = true)
                            variable = "ACTIVAR_CONSOLA"
                        }
                        "MOSTRAR_RECIBIDOS", "RECIBIDOS" -> {
                            MOSTRAR_RECIBIDOS = valor.equals("true", ignoreCase = true)
                            variable = "MOSTRAR_RECIBIDOS"
                        }
                        "MODO_DEBUG" -> {
                            MODO_DEBUG = valor.equals("true", ignoreCase = true)
                            variable = "MODO_DEBUG"
                        }
                        "INICIO_NIVEL_MONTURA" -> {
                            INICIO_NIVEL_MONTURA = valor.toInt()
                            variable = "INICIO_NIVEL_MONTURA"
                        }
                        "INICIO_KAMAS" -> {
                            INICIO_KAMAS = valor.toInt()
                            variable = "INICIO_KAMAS"
                            if (INICIO_KAMAS < 0) {
                                INICIO_KAMAS = 0
                            }
                        }
                        "INICIO_NIVEL" -> {
                            INICIO_NIVEL = valor.toShort().toInt()
                            variable = "INICIO_NIVEL"
                            if (INICIO_NIVEL < 1) {
                                INICIO_NIVEL = 1
                            }
                        }
                        "INICIO_SET_ID" -> {
                            INICIO_SET_ID = valor
                            variable = "INICIO_SET_ID"
                        }
                        "TOKEN" -> {
                            TOKEN = valor
                            variable = "TOKEN"
                        }
                        "INICIO_ZAAPS" -> {
                            INICIO_ZAAPS = valor
                            variable = "INICIO_ZAAPS"
                        }
                        "INICIO_OBJETOS" -> {
                            INICIO_OBJETOS = valor
                            variable = "INICIO_OBJETOS"
                        }
                        "INICIO_EMOTES" -> {
                            INICIO_EMOTES = valor.toInt()
                            variable = "INICIO_EMOTES"
                        }
                        "INICIO_PUNTOS_STATS" -> {
                            INICIO_PUNTOS_STATS = valor.toInt()
                            variable = "INICIO_PUNTOS_STATS"
                        }
                        "PUNTOS_STAT_POR_NIVEL", "PUNTOS_STATS_POR_NIVEL" -> {
                            PUNTOS_STATS_POR_NIVEL = valor.toInt()
                            variable = "PUNTOS_STATS_POR_NIVEL"
                        }
                        "PUNTOS_HECHIZO_POR_NIVEL" -> {
                            PUNTOS_HECHIZO_POR_NIVEL = valor.toInt()
                            variable = "PUNTOS_HECHIZO_POR_NIVEL"
                        }
                        "KOLISEO_PREMIO_KAMAS", "KOLISEO_KAMAS" -> {
                            KOLISEO_PREMIO_KAMAS = valor.toInt()
                            variable = "KOLISEO_PREMIO_KAMAS"
                        }
                        "MISION_PVP_KAMAS" -> {
                            MISION_PVP_KAMAS = valor.toInt()
                            variable = "MISION_PVP_KAMAS"
                        }
                        "KOLISEO_DIVISOR_XP" -> {
                            KOLISEO_DIVISOR_XP = valor.toInt()
                            variable = "KOLISEO_DIVISOR_XP"
                        }
                        "KOLISEO_PREMIO_OBJETOS", "KOLISEO_OBJETOS" -> {
                            KOLISEO_PREMIO_OBJETOS = valor
                            variable = "KOLISEO_PREMIO_OBJETOS"
                        }
                        "MISION_PVP_OBJETOS" -> {
                            MISION_PVP_OBJETOS = valor
                            variable = "MISION_PVP_OBJETOS"
                        }
                        "PARAM_PERMITIR_BONUS_PELEA_AFECTEN_PROSPECCION" -> {
                            PARAM_PERMITIR_BONUS_PELEA_AFECTEN_PROSPECCION = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_PERMITIR_BONUS_PELEA_AFECTEN_PROSPECCION"
                        }
                        "PERMITIR_BONUS_ESTRELLAS" -> {
                            PARAM_PERMITIR_BONUS_ESTRELLAS = valor.equals("true", ignoreCase = true)
                            variable = "PERMITIR_BONUS_ESTRELLAS"
                        }
                        "PERMITIR_BONUS_DROP_RETOS" -> {
                            PARAM_PERMITIR_BONUS_DROP_RETOS = valor.equals("true", ignoreCase = true)
                            variable = "PERMITIR_BONUS_DROP_RETOS"
                        }
                        "DEVOLVER_ITEMS" -> {
                            DEVOLVER_ITEMS = valor.equals("true", true)
                            variable = "DEVOLVER_ITEMS"
                        }
                        "PERMITIR_BONUS_EXP_RETOS" -> {
                            PARAM_PERMITIR_BONUS_EXP_RETOS = valor.equals("true", ignoreCase = true)
                            variable = "PERMITIR_BONUS_EXP_RETOS"
                        }
                        "PARAM_PERMITIR_ADMIN_EN_LADDER" -> {
                            PARAM_PERMITIR_ADMIN_EN_LADDER = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_PERMITIR_ADMIN_EN_LADDER"
                        }
                        "PARAM_MOVER_MULTIPLE_OBJETOS_SOLO_ABONADOS" -> {
                            PARAM_MOVER_MULTIPLE_OBJETOS_SOLO_ABONADOS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MOVER_MULTIPLE_OBJETOS_SOLO_ABONADOS"
                        }
                        "PARAM_EXP_PVP_MISION_POR_TABLA" -> {
                            PARAM_EXP_PVP_MISION_POR_TABLA = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_EXP_PVP_MISION_POR_TABLA"
                        }
                        "PARAM_MOSTRAR_MONTURAS_CERCADOS" -> {
                            PARAM_MOSTRAR_MONTURAS_CERCADOS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MOSTRAR_MONTURAS_CERCADOS"
                        }
                        "FACTOR_ZERO_DROP" -> {
                            FACTOR_ZERO_DROP = valor.toInt()
                            variable = "FACTOR_ZERO_DROP"
                        }
                        "PRECIO_CAMALEON" -> {
                            PRECIO_CAMALEON = valor.toInt()
                            variable = "PRECIO_CAMALEON"
                        }
                        "OGRINAS_INVITADOR" -> {
                            OGRINAS_INVITADOR = valor.toInt()
                            variable = "OGRINAS_INVITADOR"
                        }
                        "OGRINAS_INVITADO" -> {
                            OGRINAS_INVITADO = valor.toInt()
                            variable = "OGRINAS_INVITADO"
                        }
                        "RATE_FM" -> {
                            // case "DIFICULTAD_FM" :
                            RATE_FM = valor.toInt()
                            variable = "RATE_FM"
                        }
                        "RATE_KAMAS" -> {
                            RATE_KAMAS = valor.toInt()
                            variable = "RATE_KAMAS"
                            DEFECTO_KAMAS = RATE_KAMAS
                        }
                        "RATE_HONOR" -> {
                            RATE_HONOR = valor.toInt()
                            variable = "RATE_HONOR"
                            DEFECTO_XP_HONOR = RATE_HONOR
                        }
                        "RATE_XP_OFICIO" -> {
                            RATE_XP_OFICIO = valor.toInt()
                            variable = "RATE_XP_OFICIO"
                            DEFECTO_XP_OFICIO = RATE_XP_OFICIO
                        }
                        "RATE_PVM", "RATE_XP_PVM" -> {
                            RATE_XP_PVM = valor.toInt().toFloat()
                            variable = "RATE_XP_PVM"
                            DEFECTO_XP_PVM = RATE_XP_PVM
                        }
                        "RATE_FUN_PVM_BASE" -> {
                            RATE_RANDOM_MOB_BASE = valor.toFloat().toDouble()
                            variable = "RATE_FUN_PVM_BASE"
                        }
                        "RATE_FUN_PVM" -> {
                            RATE_RANDOM_MOB = valor.toFloat().toDouble()
                            variable = "RATE_FUN_PVM"
                        }
                        "RATE_FUN_ITEM_BASE" -> {
                            RATE_RANDOM_ITEM_BASE = valor.toFloat().toDouble()
                            variable = "RATE_FUN_ITEM_BASE"
                        }
                        "RATE_FUN_ITEM" -> {
                            RATE_RANDOM_ITEM = valor.toFloat().toDouble()
                            variable = "RATE_FUN_ITEM"
                        }
                        "RATE_FUN_CAPITAL" -> {
                            RATE_RANDOM_PUNTOS = valor.toFloat().toDouble()
                            variable = "RATE_FUN_CAPITAL"
                        }
                        "RATE_FUN_CAPITAL_BASE" -> {
                            RATE_RANDOM_PUNTOS_BASE = valor.toFloat().toDouble()
                            variable = "RATE_FUN_CAPITAL_BASE"
                        }
                        "RATE_XP_MONTURA" -> {
                            RATE_XP_MONTURA = valor.toInt()
                            variable = "RATE_XP_MONTURA"
                        }
                        "RATE_XP_MONTURA_ABONADOS" -> {
                            RATE_XP_MONTURA_ABONADOS = valor.toInt()
                            variable = "RATE_XP_MONTURA_ABONADOS"
                        }
                        "RATE_XP_PERCO", "RATE_XP_RECAUDADOR" -> {
                            RATE_XP_RECAUDADOR = valor.toInt()
                            variable = "RATE_XP_RECAUDADOR"
                        }
                        "RATE_PVP", "RATE_XP_PVP" -> {
                            RATE_XP_PVP = valor.toInt()
                            variable = "RATE_XP_PVP"
                            DEFECTO_XP_PVP = RATE_XP_PVP
                        }
                        "RATE_DROP_PORC", "RATE_DROP" -> {
                            RATE_DROP_NORMAL = valor.toInt()
                            variable = "RATE_DROP_PORC"
                            DEFECTO_DROP = RATE_DROP_NORMAL
                        }
                        "RATE_XP_PVM_ABONADOS" -> {
                            RATE_XP_PVM_ABONADOS = valor.toInt().toFloat()
                            variable = "RATE_XP_PVM_ABONADOS"
                        }
                        "RATE_DROP_ABONADOS" -> {
                            RATE_DROP_ABONADOS = valor.toInt().toFloat()
                            variable = "RATE_DROP_ABONADOS"
                        }
                        "RATE_KAMAS_ABONADOS" -> {
                            RATE_KAMAS_ABONADOS = valor.toInt().toFloat()
                            variable = "RATE_KAMAS_ABONADOS"
                        }
                        "RATE_XP_OFICIO_ABONADOS" -> {
                            RATE_XP_OFICIO_ABONADOS = valor.toInt().toFloat()
                            variable = "RATE_XP_OFICIO_ABONADOS"
                        }
                        "RATE_PODS" -> {
                            RATE_PODS = valor.toInt()
                            variable = "RATE_PODS"
                        }
                        "RATE_CONQUISTA_EXPERIENCIA" -> {
                            RATE_CONQUISTA_EXPERIENCIA = valor.toInt()
                            variable = "RATE_CONQUISTA_EXPERIENCIA"
                        }
                        "RATE_CONQUISTA_RECOLECTA" -> {
                            RATE_CONQUISTA_RECOLECTA = valor.toInt()
                            variable = "RATE_CONQUISTA_RECOLECTA"
                        }
                        "RATE_CONQUISTA_DROP" -> {
                            RATE_CONQUISTA_DROP = valor.toInt()
                            variable = "RATE_CONQUISTA_DROP"
                        }
                        "RATE_DROP_ARMAS_ETEREAS" -> {
                            RATE_DROP_ARMAS_ETEREAS = valor.toInt()
                            variable = "RATE_DROP_ARMAS_ETEREAS"
                        }
                        "RATE_CRIANZA_MONTURAS", "RATE_CRIANZA_MONTURA", "RATE_CRIANZA_PAVOS" -> {
                            RATE_CRIANZA_MONTURA = valor.toInt()
                            variable = "RATE_CRIANZA_MONTURA"
                            DEFECTO_CRIANZA_MONTURA = RATE_CRIANZA_MONTURA
                        }
                        "RATE_CRIANZA_MONTURAS_ABONADOS", "RATE_CRIANZA_MONTURA_ABONADOS", "RATE_CRIANZA_PAVOS_ABONADOS" -> {
                            RATE_CRIANZA_MONTURA_ABONADOS = valor.toInt()
                            variable = "RATE_CRIANZA_MONTURA"
                        }
                        "RATE_CAPTURA_MONTURAS", "RATE_CAPTURA_MONTURA", "RATE_CAPTURA_PAVOS" -> {
                            RATE_CAPTURA_MONTURA = valor.toInt()
                            variable = "RATE_CAPTURA_MONTURA"
                        }
                        "RATE_CAPTURA_MONTURAS_ABONADOS", "RATE_CAPTURA_MONTURA_ABONADOS", "RATE_CAPTURA_PAVOS_ABONADOS" -> {
                            RATE_CAPTURA_MONTURA_ABONADOS = valor.toInt()
                            variable = "RATE_CAPTURA_MONTURA_ABONADOS"
                        }
                        "MENSAJE_BIENVENIDA_1", "MENSAJE_BIENVENIDA" -> {
                            MENSAJE_BIENVENIDA = valor
                            variable = "MENSAJE_BIENVENIDA"
                        }
                        "PANEL_BIENVENIDA_1", "PANEL_BIENVENIDA" -> {
                            PANEL_BIENVENIDA = valor
                            variable = "PANEL_BIENVENIDA"
                        }
                        "PANEL_CREAR_PJ" -> {
                            PANEL_DESPUES_CREAR_PERSONAJE = valor
                            variable = "PANEL_DESPUES_CREAR_PJ"
                        }
                        "MESSAGE_COMMANDS", "MENSAJE_COMANDOS" -> {
                            MENSAJE_COMANDOS = valor
                            variable = "MENSAJE_COMANDOS"
                        }
                        "MESSAGE_SERVICIES", "MENSAJE_SERVICIOS" -> {
                            MENSAJE_SERVICIOS = valor
                            variable = "MENSAJE_SERVICIOS"
                        }
                        "MENSAJE_ERROR_OGRINAS_CREAR_CLASE" -> {
                            MENSAJE_ERROR_OGRINAS_CREAR_CLASE = valor
                            variable = "MENSAJE_ERROR_OGRINAS_CREAR_CLASE"
                        }
                        "MESSAGE_VIP", "MENSAJE_VIP" -> {
                            MENSAJE_VIP = valor
                            variable = "MENSAJE_VIP"
                        }
                        "TUTORIAL_FR" -> {
                            TUTORIAL_FR = valor
                            variable = "TUTORIAL_FR"
                        }
                        "TUTORIAL_ES" -> {
                            TUTORIAL_ES = valor
                            variable = "TUTORIAL_ES"
                        }
                        "PUBLICIDAD_1", "PUBLICIDAD_2", "PUBLICIDAD_3", "PUBLICIDAD_4", "PUBLICIDAD_5" -> PUBLICIDAD.add(
                            valor
                        )
                        "MAPAS_KOLISEO" -> {
                            MAPAS_KOLISEO = valor
                            variable = "MAPAS_KOLISEO"
                        }
                        "PUERTO_SERVER", "PUERTO_SERVIDOR" -> {
                            PUERTO_SERVIDOR = valor.toInt()
                            variable = "PUERTO_SERVIDOR"
                        }
                        "PUERTO_SINCRONIZACION", "PUERTO_SINCRONIZADOR" -> {
                            PUERTO_SINCRONIZADOR = valor.toInt()
                            variable = "PUERTO_SINCRONIZADOR"
                        }
                        "SERVIDOR_PRIORIDAD" -> {
                            SERVIDOR_PRIORIDAD = valor.toInt()
                            variable = "SERVIDOR_PRIORIDAD"
                        }
                        "LIMITE_PA_PVP" -> {
                            LIMITE_PA_PVP = valor.toInt()
                            variable = "LIMITE_PA_PVP"
                        }
                        "LIMITE_PM_PVP" -> {
                            LIMITE_PM_PVP = valor.toInt()
                            variable = "LIMITE_PM_PVP"
                        }
                        "IP_PUBLIC_SERVER", "IP_SERVIDOR_PUBLICA", "IP_SERVIDOR_FIJA", "IP_FIJA_SERVIDOR", "IP_FIX_SERVER", "IP_PUBLICA_SERVIDOR" -> {
                            variable = "IP_PUBLICA_SERVIDOR"
                            if (IP_PERMTIDAS.contains(valor) || IP_PERMTIDAS.contains("*")) {
                                IP_PUBLICA_SERVIDOR = valor
                            }
                        }
                        "IP_MULTISERVIDOR", "IP_MULTISERVER" -> {
                            variable = "IP_MULTISERVIDOR"
                            for (s in valor.split(";".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                //								if (IP_PERMTIDAS.contains(s) || IP_PERMTIDAS.contains("*")) {
                                try {
                                    val `in` = InetAddress.getByName(s)
                                    IP_MULTISERVIDOR.add(`in`.hostAddress)
                                } catch (e: Exception) {
                                    IP_MULTISERVIDOR.add(s)
                                }
                            }
                        }
                        "DB_HOST", "BD_HOST" -> {
                            BD_HOST = valor
                            variable = "BD_HOST"
                        }
                        "DB_PORT", "BD_PORT" -> {
                            BD_PORT = valor
                            variable = "BD_PORT"
                        }
                        "DB_USER", "BD_USER", "BD_USUARIO" -> {
                            BD_USUARIO = valor
                            variable = "BD_USUARIO"
                        }
                        "DB_PASSWORD", "DB_PASS", "BD_PASSWORD", "BD_CONTRASEÑA", "BD_PASS" -> {
                            BD_PASS = valor
                            variable = "BD_PASS"
                        }
                        "DB_STATIC", "BD_STATIC", "BD_STATIQUE", "BD_FIJA", "BD_LUIS" -> {
                            BD_ESTATICA = valor
                            variable = "BD_ESTATICA"
                        }
                        "DB_DYNAMIC", "BD_TANIA", "BD_DYNAMIC", "BD_DINAMICA", "BD_OTHERS" -> {
                            BD_DINAMICA = valor
                            variable = "BD_DINAMICA"
                        }
                        "DB_ACCOUNTS", "BD_ACCOUNTS", "BD_COMPTES", "BD_CUENTAS", "BD_LOGIN", "BD_REALM" -> {
                            BD_CUENTAS = valor
                            variable = "BD_CUENTAS"
                        }
                        "NAME_SERVER", "NOMBRE_SERVIDOR", "NOMBRE_SERVER" -> {
                            NOMBRE_SERVER = valor
                            variable = "NOMBRE_SERVER"
                        }
                        "URL_IMAGEN_VOTO" -> {
                            URL_IMAGEN_VOTO = valor
                            variable = "URL_IMAGEN_VOTO"
                        }
                        "URL_LINK_VOTE", "URL_LINK_VOTO" -> {
                            URL_LINK_VOTO = valor
                            variable = "URL_LINK_VOTO"
                        }
                        "URL_LINK_COMPRA" -> {
                            URL_LINK_COMPRA = valor
                            variable = "URL_LINK_COMPRA"
                        }
                        "URL_BACKUP_PHP" -> {
                            URL_BACKUP_PHP = valor
                            variable = "URL_BACKUP_PHP"
                        }
                        "URL_DETECTAR_DDOS" -> {
                            URL_DETECTAR_DDOS = valor
                            variable = "URL_DETECTAR_DDOS"
                        }
                        "URL_REPORT_BUG", "URL_LINK_BUG" -> {
                            URL_LINK_BUG = valor
                            variable = "URL_LINK_BUG"
                        }
                        "URL_LINK_MP3" -> {
                            URL_LINK_MP3 = valor
                            variable = "URL_LINK_MP3"
                        }
                        "DIRECTORIO_MP3", "DIRECTORIO_LOCAL_MP3" -> {
                            DIRECTORIO_LOCAL_MP3 = valor
                            variable = "DIRECTORIO_LOCAL_MP3"
                        }
                        "COMMANDS_AUTHORIZATE", "COMMANDS_PLAYER", "COMANDOS_AUTORIZADOS", "COMANDOS_JUGADOR", "COMANDOS_PERMITIDOS" -> {
                            variable = "COMANDOS_PERMITIDOS"
                            for (s in valor.split(",".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                COMANDOS_PERMITIDOS.add(s)
                            }
                        }
                        "COMMANDS_VIP", "COMMANDS_BOUTIQUE", "COMANDOS_ABONADO", "COMANDOS_VIP" -> {
                            variable = "COMANDOS_VIP"
                            for (s in valor.split(",".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                COMANDOS_VIP.add(s)
                            }
                        }
                        "SALVAR_LOGS_TIPO_PELEA", "SALVAR_LOGS_TIPO_COMBATE" -> {
                            variable = "SALVAR_LOGS_TIPO_COMBATE"
                            for (s in valor.split(",".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    SALVAR_LOGS_TIPO_COMBATE.add(s.toByte())
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "CANALES_COLOR_CHAT", "COLORES_CANALES_CHAT", "COLOR_CHAT" -> {
                            CANALES_COLOR_CHAT = valor
                            variable = "CANALES_COLOR_CHAT"
                        }
                        "PERMITIR_MULTIMAN_TIPO_PELEA", "PERMITIR_MULTIMAN_TIPO_COMBATE" -> {
                            PERMITIR_MULTIMAN = valor
                            variable = "PERMITIR_MULTIMAN"
                        }
                        "PALABRA_CLAVE_CONSOLA" -> {
                            PALABRA_CLAVE_CONSOLA = valor
                            variable = "PALABRA_CLAVE_CONSOLA"
                        }
                        "SISTEMA_ITEMS_TIPO_DE_PAGO", "PANEL_ITEMS_TIPO_DE_PAGO" -> {
                            SISTEMA_ITEMS_TIPO_DE_PAGO = valor.toUpperCase()
                            variable = "SISTEMA_ITEMS_TIPO_DE_PAGO"
                        }
                        "INICIO_MAPA_CELDA", "START_MAPA_CELDA", "RETURN_MAPA_CELDA" -> {
                            START_MAPA_CELDA = valor
                            variable = "START_MAPA_CELDA"
                        }
                        "ENCLO_MAPA_CELDA", "ENCLOS_MAPA_CELDA", "CERCADO_MAPA_CELDA", "CERCADOS_MAPA_CELDA" -> {
                            CERCADO_MAPA_CELDA = valor
                            variable = "CERCADO_MAPA_CELDA"
                        }
                        "TIENDA_MAPA_CELDA", "SHOP_MAPA_CELDA" -> {
                            SHOP_MAPA_CELDA = valor
                            variable = "SHOP_MAPA_CELDA"
                        }
                        "PVP_MAPA_CELDA" -> {
                            PVP_MAPA_CELDA = valor
                            variable = "PVP_MAPA_CELDA"
                        }
                        "NIVEL_IA", "LEVEL_PROCCESS_IA", "NIVEL_PROCESAMIENTO_IA", "NIVEL_EJECUCION_IA", "NIVEL_INTELIGENCIA_ARTIFICIAL" -> {
                            NIVEL_INTELIGENCIA_ARTIFICIAL = valor.toInt()
                            variable = "NIVEL_INTELIGENCIA_ARTIFICIAL"
                        }
                        "VECES_PARA_BAN_IP_SIN_ESPERA" -> {
                            VECES_PARA_BAN_IP_SIN_ESPERA = valor.toInt()
                            variable = "VECES_PARA_BAN_IP_SIN_ESPERA"
                        }
                        "SEGUNDOS_CANAL_COMERCIO" -> {
                            SEGUNDOS_CANAL_COMERCIO = valor.toInt()
                            variable = "SEGUNDOS_CANAL_COMERCIO"
                        }
                        "SEGUNDOS_CANAL_RECLUTAMIENTO" -> {
                            SEGUNDOS_CANAL_RECLUTAMIENTO = valor.toInt()
                            variable = "SEGUNDOS_CANAL_RECLUTAMIENTO"
                        }
                        "SEGUNDOS_CANAL_ALINEACION" -> {
                            SEGUNDOS_CANAL_ALINEACION = valor.toInt()
                            variable = "SEGUNDOS_CANAL_ALINEACION"
                        }
                        "SEGUNDOS_CANAL_INCARNAM" -> {
                            SEGUNDOS_CANAL_INCARNAM = valor.toInt()
                            variable = "SEGUNDOS_CANAL_INCARNAM"
                        }
                        "SEGUNDOS_CANAL_ALL" -> {
                            SEGUNDOS_CANAL_ALL = valor.toInt()
                            variable = "SEGUNDOS_CANAL_ALL"
                        }
                        "SEGUNDOS_CANAL_VIP" -> {
                            SEGUNDOS_CANAL_VIP = valor.toInt()
                            variable = "SEGUNDOS_CANAL_VIP"
                        }
                        "SEGUNDOS_TURNO_PELEA" -> {
                            SEGUNDOS_TURNO_PELEA = valor.toInt()
                            variable = "SEGUNDOS_TURNO_PELEA"
                        }
                        "SEGUNDOS_ARENA" -> {
                            SEGUNDOS_ARENA = valor.toInt()
                            variable = "SEGUNDOS_ARENA"
                        }
                        "SEGUNDOS_ENTRE_DESAFIOS_PJ" -> {
                            SEGUNDOS_ENTRE_DESAFIOS_PJ = valor.toInt()
                            variable = "SEGUNDOS_ENTRE_DESAFIOS_PJ"
                        }
                        "SEGUNDOS_INACTIVIDAD" -> {
                            SEGUNDOS_INACTIVIDAD = valor.toInt()
                            variable = "SEGUNDOS_INACTIVIDAD"
                        }
                        "SEGUNDOS_TRANSACCION_BD" -> {
                            SEGUNDOS_TRANSACCION_BD = valor.toInt()
                            variable = "SEGUNDOS_TRANSACCION_BD"
                        }
                        "SEGUNDOS_SALVAR" -> {
                            SEGUNDOS_SALVAR = valor.toInt()
                            variable = "SEGUNDOS_SALVAR"
                        }
                        "SEGUNDOS_REBOOT_SERVER", "SEGUNDOS_RESET_SERVER", "SEGUNDOS_REBOOT", "SEGUNDOS_RESET" -> {
                            SEGUNDOS_REBOOT_SERVER = valor.toInt()
                            variable = "SEGUNDOS_REBOOT_SERVER"
                        }
                        "SEGUNDOS_DETECTAR_DDOS" -> {
                            SEGUNDOS_DETECTAR_DDOS = valor.toInt()
                            variable = "SEGUNDOS_DETECTAR_DDOS"
                        }
                        "SEGUNDOS_VOLVER_APARECER_MOBS", "SEGUNDOS_REAPARECER_GRUPO_MOBS", "SEGUNDOS_REAPARECER_MOBS" -> {
                            SEGUNDOS_REAPARECER_MOBS = valor.toInt()
                            variable = "SEGUNDOS_REAPARECER_MOBS"
                        }
                        "FACTOR_COMPRA"->{
                            FACTOR_COMPRA = valor.toDouble()
                            variable = "FACTOR_COMPRA"
                        }
                        "FACTOR_VENTA"->{
                            FACTOR_VENTA = valor.toDouble()
                            variable = "FACTOR_VENTA"
                        }
                        "ANUNCIO_NIVEL_MAX"->{
                            ANUNCIO_NIVEL_MAX = valor.equals("true",ignoreCase = true)
                            variable = "ANUNCIO_NIVEL_MAX"
                        }
                        "SEGUNDOS_AGREDIR_RECIEN_LLEGADO_MAPA" -> {
                            SEGUNDOS_AGREDIR_RECIEN_LLEGADO_MAPA = valor.toInt()
                            variable = "SEGUNDOS_AGREDIR_RECIEN_LLEGADO_MAPA"
                        }
                        "SEGUNDOS_LIMPIAR_MEMORIA" -> {
                            SEGUNDOS_LIMPIAR_MEMORIA = valor.toInt()
                            variable = "SEGUNDOS_LIMPIAR_MEMORIA"
                        }
                        "SEGUNDOS_RESET_RATES" -> {
                            SEGUNDOS_RESET_RATES = valor.toInt()
                            variable = "SEGUNDOS_RESET_RATES"
                        }
                        "MINUTOS_VALIDAR_VOTO", "MINUTOS_SIGUIENTE_VOTO", "MINUTOS_SIG_VOTO" -> {
                            MINUTOS_VALIDAR_VOTO = valor.toInt()
                            variable = "MINUTOS_VALIDAR_VOTO"
                        }
                        "MINUTOS_PENALIZACION_KOLISEO" -> {
                            MINUTOS_PENALIZACION_KOLISEO = valor.toInt()
                            variable = "MINUTOS_PENALIZACION_KOLISEO"
                        }
                        "MINUTOS_SPAMEAR_BOTON_VOTO" -> {
                            MINUTOS_SPAMEAR_BOTON_VOTO = valor.toInt()
                            variable = "MINUTOS_SPAMEAR_BOTON_VOTO"
                        }
                        "MILISEGUNDOS_CERRAR_SERVIDOR" -> {
                            MILISEGUNDOS_CERRAR_SERVIDOR = valor.toInt()
                            variable = "MILISEGUNDOS_CERRAR_SERVIDOR"
                        }
                        "SEGUNDOS_LIVE_ACTION" -> {
                            SEGUNDOS_LIVE_ACTION = valor.toInt()
                            variable = "SEGUNDOS_LIVE_ACTION"
                        }
                        "SEGUNDOS_PUBLICIDAD" -> {
                            SEGUNDOS_PUBLICIDAD = valor.toInt()
                            variable = "SEGUNDOS_PUBLICIDAD"
                        }
                        "SEGUNDOS_ESTRELLAS_GRUPO_MOBS", "SEGUNDOS_ESTRELLAS_MOBS" -> {
                            SEGUNDOS_ESTRELLAS_GRUPO_MOBS = valor.toInt()
                            variable = "SEGUNDOS_ESTRELLAS_GRUPO_MOBS"
                        }
                        "SEGUNDOS_ESTRELLAS_RECURSOS" -> {
                            SEGUNDOS_ESTRELLAS_RECURSOS = valor.toInt()
                            variable = "SEGUNDOS_ESTRELLAS_RECURSOS"
                        }
                        "SEGUNDOS_INICIAR_KOLISEO" -> {
                            SEGUNDOS_INICIAR_KOLISEO = valor.toInt()
                            variable = "SEGUNDOS_INICIAR_KOLISEO"
                        }
                        "MINUTOS_ALIMENTACION_MASCOTA", "MINUTOS_ALIMENTACION" -> {
                            MINUTOS_ALIMENTACION_MASCOTA = valor.toInt()
                            variable = "MINUTOS_ALIMENTACION_MASCOTA"
                        }
                        "MINUTOS_MISION_PVP" -> {
                            MINUTOS_MISION_PVP = valor.toInt()
                            variable = "MINUTOS_MISION_PVP"
                        }
                        "MINUTOS_GESTACION_MONTURA", "MINUTOS_PARIR", "MINUTOS_PARIR_MONTURA" -> {
                            MINUTOS_GESTACION_MONTURA = valor.toInt()
                            variable = "MINUTOS_GESTACION_MONTURA"
                        }
                        "SEGUNDOS_MOVER_RECAUDADOR" -> {
                            SEGUNDOS_MOVER_RECAUDADOR = valor.toInt()
                            variable = "SEGUNDOS_MOVER_RECAUDADOR"
                        }
                        "SEGUNDOS_MOVER_MONTURAS", "SEGUNDOS_MOVERSE_MONTURAS", "SEGUNDOS_MOVERSE_PAVOS" -> {
                            SEGUNDOS_MOVER_MONTURAS = valor.toInt()
                            variable = "SEGUNDOS_MOVER_MONTURAS"
                        }
                        "SEGUNDOS_MOVER_GRUPO_MOBS", "SEGUNDOS_MOVER_MOBS", "SEGUNDOS_MOVERSE_MOBS" -> {
                            SEGUNDOS_MOVER_GRUPO_MOBS = valor.toInt()
                            variable = "SEGUNDOS_MOVER_GRUPO_MOBS"
                        }
                        "SEGUNDOS_PEQUEÑO_SALVAR" -> {
                            SEGUNDOS_PEQUEÑO_SALVAR = valor.toInt()
                            variable = "SEGUNDOS_PEQUEÑO_SALVAR"
                        }
                        "HORAS_VOLVER_A_PONER_RECAUDADOR_MAPA" -> {
                            HORAS_VOLVER_A_PONER_RECAUDADOR_MAPA = valor.toInt()
                            variable = "HORAS_VOLVER_A_PONER_RECAUDADOR_MAPA"
                        }
                        "HORAS_PERDER_CRIAS_MONTURA" -> {
                            HORAS_PERDER_CRIAS_MONTURA = valor.toInt()
                            variable = "HORAS_PERDER_CRIAS_MONTURA"
                        }
                        "MIN_NIVEL_KOLISEO" -> {
                            MIN_NIVEL_KOLISEO = valor.toShort().toInt()
                            variable = "MIN_NIVEL_KOLISEO"
                        }
                        "LIMITE_SCROLL", "MAX_SCROLL" -> {
                            LIMITE_SCROLL = valor.toShort().toInt()
                            variable = "LIMITE_SCROLL"
                        }
                        "MAX_MIEMBROS_GREMIO", "LIMITE_MIEMBROS_GREMIO" -> {
                            LIMITE_MIEMBROS_GREMIO = valor.toInt()
                            variable = "LIMITE_MIEMBROS_GREMIO"
                        }
                        "LIMITE_OBJETOS_COFRE", "MAX_OBJETOS_COFRE", "LIMITE_MAX_OBJETOS_COFRE" -> {
                            LIMITE_OBJETOS_COFRE = valor.toInt()
                            variable = "LIMITE_OBJETOS_COFRE"
                        }
                        "ID_BOLSA_CREDITOS" -> {
                            ID_BOLSA_CREDITOS = valor.toInt()
                            variable = "ID_BOLSA_CREDITOS"
                        }
                        "ID_BOLSA_OGRINAS" -> {
                            ID_BOLSA_OGRINAS = valor.toInt()
                            variable = "ID_BOLSA_OGRINAS"
                        }
                        "IMPUESTO_BOLSA_OGRINAS" -> {
                            IMPUESTO_BOLSA_OGRINAS = valor.toInt()
                            variable = "IMPUESTO_BOLSA_OGRINAS"
                        }
                        "DURABILIDAD_REDUCIR_OBJETO_CRIA" -> {
                            DURABILIDAD_REDUCIR_OBJETO_CRIA = valor.toInt()
                            variable = "DURABILIDAD_REDUCIR_OBJETO_CRIA"
                        }
                        "IMPUESTO_BOLSA_CREDITOS" -> {
                            IMPUESTO_BOLSA_CREDITOS = valor.toInt()
                            variable = "IMPUESTO_BOLSA_CREDITOS"
                        }
                        "LIMITE_LADDER" -> {
                            LIMITE_LADDER = valor.toShort().toInt()
                            variable = "LIMITE_LADDER"
                        }
                        "LIMITE_MOSTRAR_REPORTES", "MAX_MOSTRAR_REPORTES", "MAX_REPORTES", "LIMIT_REPORTES" -> {
                            LIMITE_REPORTES = valor.toShort().toInt()
                            variable = "LIMITE_REPORTES"
                        }
                        "MAX_MISIONES_ALMANAX" -> {
                            MAX_MISIONES_ALMANAX = valor.toShort().toInt()
                            variable = "MAX_MISIONES_ALMANAX"
                        }
                        "COLOR_CASES_PLAYER", "COLOR_CASES_FIGHT_AGRESSOR", "COLOR_CELDAS_PELEA_AGRESOR" -> {
                            COLOR_CELDAS_PELEA_AGRESOR = valor.toLowerCase()
                            variable = "COLOR_CELDAS_PELEA_AGRESOR"
                        }
                        "MAX_RECAUDADORES_POR_ZONA" -> {
                            MAX_RECAUDADORES_POR_ZONA = valor.toInt()
                            variable = "MAX_RECAUDADORES_POR_ZONA"
                        }
                        "MAX_BONUS_STARS_MOB", "MAX_BONUS_STARS_MOBS", "MAX_BONUS_ESTRELLAS_MOBS" -> {
                            MAX_BONUS_ESTRELLAS_MOBS = valor.toShort().toInt()
                            variable = "MAX_BONUS_ESTRELLAS_MOBS"
                        }
                        "MAX_BONUS_STARS_RESSOURCES", "MAX_BONUS_ESTRELLAS_RECURSO", "MAX_BONUS_ESTRELLAS_RECURSOS" -> {
                            MAX_BONUS_ESTRELLAS_RECURSOS = valor.toShort().toInt()
                            variable = "MAX_BONUS_ESTRELLAS_RECURSOS"
                        }
                        "MAX_STARS_MOBS", "MAX_ESTRELLAS_MOB", "MAX_ESTRELLAS_MOBS" -> {
                            MAX_BONUS_ESTRELLAS_MOBS = (valor.toShort() * 20)
                            variable = "MAX_BONUS_ESTRELLAS_MOBS"
                        }
                        "MAX_STARS_RESSOURCES", "MAX_STARS_RECURSOS", "MAX_ESTRELLAS_RECURSO", "MAX_ESTRELLAS_RECURSOS" -> {
                            MAX_BONUS_ESTRELLAS_RECURSOS = (valor.toShort() * 20)
                            variable = "MAX_BONUS_ESTRELLAS_RECURSOS"
                        }
                        "INICIO_BONUS_ESTRELLAS_MOBS" -> {
                            INICIO_BONUS_ESTRELLAS_MOBS = valor.toShort().toInt()
                            variable = "INICIO_BONUS_ESTRELLAS_MOBS"
                        }
                        "INICIO_ESTRELLAS_MOBS" -> {
                            INICIO_BONUS_ESTRELLAS_MOBS = (valor.toShort() * 20)
                            variable = "INICIO_BONUS_ESTRELLAS_MOBS"
                        }
                        "INICIO_BONUS_ESTRELLAS_RECURSOS" -> {
                            INICIO_BONUS_ESTRELLAS_RECURSOS = valor.toShort().toInt()
                            variable = "INICIO_BONUS_ESTRELLAS_RECURSOS"
                        }
                        "INICIO_ESTRELLAS_RECURSOS" -> {
                            INICIO_BONUS_ESTRELLAS_RECURSOS = (valor.toShort() * 20)
                            variable = "INICIO_BONUS_ESTRELLAS_RECURSOS"
                        }
                        "HECHIZOS_NO_DESHECHIZABLES" -> {
                            val lista = valor.split(",")
                            HECHIZOS_NO_DESHECHIZABLE.clear()
                            for (s in lista) {
                                try {
                                    val i = s.toInt()
                                    if (!HECHIZOS_NO_DESHECHIZABLE.contains(i)) HECHIZOS_NO_DESHECHIZABLE.add(i)
                                } catch (e: Exception) {
                                    continue
                                }
                            }
                            variable = "HECHIZOS_NO_DESHECHIZABLES"
                        }
                        "NIVEL_MAX_PERSONAJE" -> {
                            NIVEL_MAX_PERSONAJE = valor.toShort().toInt()
                            variable = "NIVEL_MAX_PERSONAJE"
                        }
                        "NIVEL_MAX_ESCOGER_NIVEL" -> {
                            NIVEL_MAX_ESCOGER_NIVEL = valor.toShort().toInt()
                            variable = "NIVEL_MAX_ESCOGER_NIVEL"
                        }
                        "NIVEL_MAX_MONTURA", "NIVEL_MAX_DRAGOPAVO" -> {
                            NIVEL_MAX_MONTURA = valor.toShort().toInt()
                            variable = "NIVEL_MAX_MONTURA"
                        }
                        "PORCENTAJE_DEVOLVER_ITEMS" -> {
                            PORCENTAJE_DEVOLVER_ITEMS = valor.toDouble()
                            variable = "PORCENTAJE_DEVOLVER_ITEMS"
                        }
                        "NIVEL_MAX_GREMIO" -> {
                            NIVEL_MAX_GREMIO = valor.toShort().toInt()
                            variable = "NIVEL_MAX_GREMIO"
                        }
                        "TIEMPO_INTERCAMBIABLE_REGALO" -> {
                            TIEMPO_INTERCAMBIABLE_REGALO = valor.toShort().toInt()
                            variable = "TIEMPO_INTERCAMBIABLE_REGALO"
                        }
                        "NIVEL_MAX_ENCARNACION" -> {
                            NIVEL_MAX_ENCARNACION = valor.toShort().toInt()
                            variable = "NIVEL_MAX_ENCARNACION"
                        }
                        "NIVEL_MAX_ALINEACION" -> {
                            NIVEL_MAX_ALINEACION = valor.toByte().toInt()
                            variable = "NIVEL_MAX_ALINEACION"
                        }
                        "MAX_PESO_POR_STAT" -> {
                            MAX_PESO_POR_STAT = valor.toInt()
                            variable = "MAX_PESO_POR_STAT"
                        }
                        "MAX_RESET", "MAX_RESETS" -> {
                            MAX_RESETS = valor.toInt()
                            variable = "MAX_RESETS"
                        }
                        "MAX_CAC_POR_TURNO" -> {
                            MAX_CAC_POR_TURNO = valor.toInt()
                            variable = "MAX_CAC_POR_TURNO"
                        }
                        "PRECIO_LOTERIA" -> {
                            PRECIO_LOTERIA = valor.toShort().toInt()
                            variable = "PRECIO_LOTERIA"
                        }
                        "PREMIO_LOTERIA" -> {
                            PREMIO_LOTERIA = valor.toShort().toInt()
                            variable = "PREMIO_LOTERIA"
                        }
                        "DIAS_PARA_BORRAR" -> {
                            DIAS_PARA_BORRAR = valor.toShort()
                            variable = "DIAS_PARA_BORRAR"
                        }
                        "PARAM_SUPER_CRAFT_SPEED" -> {
                            PARAM_SUPER_CRAFT_SPEED = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_SUPER_CRAFT_SPEED"
                        }
                        "OBJETOS_PELEA_PRISMA" -> {
                            OBJETOS_PELEA_PRISMA = valor
                            variable = "OBJETOS_PELEA_PRISMA"
                        }
                        "VALOR_RECAUDADOR" -> {
                            VALOR_RECAUDADOR = valor.toInt()
                            variable = "VALOR_RECAUDADOR"
                        }
                        "PARAM_CORREGIR_NOMBRE_JUGADOR" -> {
                            PARAM_CORREGIR_NOMBRE_JUGADOR = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_CORREGIR_NOMBRE_JUGADOR"
                        }
                        "PARAM_REINICIAR_ESTRELLAS_SI_LLEGA_MAX", "RESETEAR_ESTRELLAS" -> {
                            PARAM_REINICIAR_ESTRELLAS_SI_LLEGA_MAX = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_REINICIAR_ESTRELLAS_SI_LLEGA_MAX"
                        }
                        "PARAM_ANTI_FLOOD", "PARAM_ANTIFLOOD" -> {
                            PARAM_ANTIFLOOD = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_ANTIFLOOD"
                        }
                        "PARAM_LIMITAR_RECAUDADOR_GREMIO_POR_ZONA" -> {
                            PARAM_LIMITAR_RECAUDADOR_GREMIO_POR_ZONA = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_LIMITAR_RECAUDADOR_GREMIO_POR_ZONA"
                        }
                        "RESETEAR_LUPEAR_OBJETOS_MAGUEADOS" -> {
                            PARAM_RESETEAR_LUPEAR_OBJETOS_MAGUEADOS = valor.equals("true", ignoreCase = true)
                            variable = "RESETEAR_LUPEAR_OBJETOS_MAGUEADOS"
                        }
                        "NPC_BOUTIQUE" -> {
                            ID_NPC_BOUTIQUE = valor.toShort()
                            variable = "ID_NPC_BOUTIQUE"
                        }
                        "PRECIO_SISTEMA_RECURSO", "PRECIO_RECURSO" -> {
                            PRECIO_SISTEMA_RECURSO = valor.toFloat()
                            variable = "PRECIO_SISTEMA_RECURSO"
                        }
                        "FACTOR_OBTENER_RUNAS" -> {
                            FACTOR_OBTENER_RUNAS = valor.toFloat()
                            variable = "FACTOR_OBTENER_RUNAS"
                        }
                        "FACTOR_PLUS_PP_PARA_DROP" -> {
                            FACTOR_PLUS_PP_PARA_DROP = valor.toFloat()
                            variable = "FACTOR_PLUS_PP_PARA_DROP"
                        }
                        "FACTOR_DEVOLVER_OGRINAS" -> {
                            FACTOR_DEVOLVER_OGRINAS = valor.toFloat()
                            variable = "FACTOR_DEVOLVER_OGRINAS"
                        }
                        "OGRINAS_CREAR_CLASE" -> {
                            variable = "OGRINAS_CREAR_CLASE"
                            for (s in valor.split(";".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat = s.split(",".toRegex()).toTypedArray()
                                    OGRINAS_CREAR_CLASE[stat[0].toByte()] = stat[1].toInt()
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "GFX_CREA_TU_ITEM_CAPAS" -> {
                            GFX_CREA_TU_ITEM_CAPAS = valor
                            variable = "GFX_CREA_TU_ITEM_CAPAS"
                        }
                        "GFX_CREA_TU_ITEM_AMULETOS" -> {
                            GFX_CREA_TU_ITEM_AMULETOS = valor
                            variable = "GFX_CREA_TU_ITEM_AMULETOS"
                        }
                        "GFX_CREA_TU_ITEM_ANILLOS" -> {
                            GFX_CREA_TU_ITEM_ANILLOS = valor
                            variable = "GFX_CREA_TU_ITEM_ANILLOS"
                        }
                        "GFX_CREA_TU_ITEM_CINTURONES" -> {
                            GFX_CREA_TU_ITEM_CINTURONES = valor
                            variable = "GFX_CREA_TU_ITEM_CINTURONES"
                        }
                        "GFX_CREA_TU_ITEM_BOTAS" -> {
                            GFX_CREA_TU_ITEM_BOTAS = valor
                            variable = "GFX_CREA_TU_ITEM_BOTAS"
                        }
                        "GFX_CREA_TU_ITEM_SOMBREROS", "GFX_CREA_TU_ITEM_ESCUDOS" -> {
                            GFX_CREA_TU_ITEM_SOMBREROS = valor
                            variable = "GFX_CREA_TU_ITEM_SOMBREROS"
                        }
                        "GFX_CREA_TU_ITEM_DOFUS" -> {
                            GFX_CREA_TU_ITEM_DOFUS = valor
                            variable = "GFX_CREA_TU_ITEM_DOFUS"
                        }
                        "PRECIOS_SERVICIOS", "PRECIO_SERVICIOS" -> {
                            variable = "PRECIOS_SERVICIOS"
                            for (s in valor.split(";".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat = s.split(",".toRegex()).toTypedArray()
                                    PRECIOS_SERVICIOS[stat[0].toLowerCase()] = stat[1].toLowerCase()
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "MAX_GOLPES_CAC" -> {
                            variable = "MAX_GOLPES_CAC"
                            for (s in valor.split(";".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat = s.split(",".toRegex()).toTypedArray()
                                    MAX_GOLPES_CAC[stat[0].toInt()] = stat[1].toInt()
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "LIMITE_STATS_CON_BUFF" -> {
                            variable = "LIMITE_STATS_CON_BUFF"
                            for (s in valor.split(";".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat = s.split(",".toRegex()).toTypedArray()
                                    LIMITE_STATS_CON_BUFF[stat[0].toInt()] = stat[1].toInt()
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "LIMITE_STATS_EXO_FM", "LIMITE_STATS_EXOMAGIA", "LIMITE_STATS_EXO_FORJAMAGIA" -> {
                            variable = "LIMITE_STATS_EXO_FORJAMAGIA"
                            for (s in valor.split(";".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat = s.split(",".toRegex()).toTypedArray()
                                    LIMITE_STATS_EXO_FORJAMAGIA[stat[0].toInt()] = stat[1].toInt()
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "LIMITE_STATS_OVER_FM", "LIMITE_STATS_OVERMAGIA", "LIMITE_STATS_OVER_FORJAMAGIA" -> {
                            variable = "LIMITE_STATS_OVER_FORJAMAGIA"
                            for (s in valor.split(";".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat = s.split(",".toRegex()).toTypedArray()
                                    LIMITE_STATS_OVER_FORJAMAGIA[stat[0].toInt()] = stat[1].toInt()
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "LIMITE_STATS_SIN_BUFF" -> {
                            variable = "LIMITE_STATS_SIN_BUFF"
                            for (s in valor.split(";".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    val stat = s.split(",".toRegex()).toTypedArray()
                                    LIMITE_STATS_SIN_BUFF[stat[0].toInt()] = stat[1].toInt()
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "SERVER_ID" -> {
                            SERVIDOR_ID = valor.toShort().toInt()
                            variable = "SERVIDOR_ID"
                        }
                        "ADMIN_ACCESS", "ACCESO_ADMIN", "ACCESS_ADMIN", "ACCESO_ADMIN_MIN" -> {
                            ACCESO_ADMIN_MINIMO = valor.toInt()
                            variable = "ACCESO_ADMIN_MINIMO"
                        }
                        "PORCENTAJE_DAÑO_NO_CURABLE", "DAÑO_PERMANENTE" -> {
                            PORCENTAJE_DAÑO_NO_CURABLE = valor.toInt()
                            variable = "PORCENTAJE_DAÑO_NO_CURABLE"
                            if (PORCENTAJE_DAÑO_NO_CURABLE < 0) {
                                PORCENTAJE_DAÑO_NO_CURABLE = 0
                            }
                            if (PORCENTAJE_DAÑO_NO_CURABLE > 100) {
                                PORCENTAJE_DAÑO_NO_CURABLE = 100
                            }
                        }
                        "LIMITE_MAPAS" -> {
                            LIMITE_MAPAS = valor.toShort().toInt()
                            variable = "LIMITE_MAPAS"
                        }
                        "LIMITE_DETECTAR_FALLA_KAMAS" -> {
                            LIMITE_DETECTAR_FALLA_KAMAS = valor.toLong()
                            variable = "LIMITE_DETECTAR_FALLA_KAMAS"
                        }
                        "MAX_PJS_POR_CUENTA" -> {
                            MAX_PJS_POR_CUENTA = valor.toInt()
                            variable = "MAX_PJS_POR_CUENTA"
                        }
                        "BONUS_RESET_PUNTOS_STATS" -> {
                            BONUS_RESET_PUNTOS_STATS = valor.toShort()
                            variable = "BONUS_RESET_PUNTOS_STATS"
                        }
                        "BONUS_RESET_PUNTOS_HECHIZOS" -> {
                            BONUS_RESET_PUNTOS_HECHIZOS = valor.toShort()
                            variable = "BONUS_RESET_PUNTOS_HECHIZOS"
                        }
                        "SISTEMA_ITEMS_EXO_PM_PRECIO", "PANEL_ITEMS_EXO_PM_PRECIO", "PANEL_ITEMS_EXO_PM" -> {
                            SISTEMA_ITEMS_EXO_PM_PRECIO = valor.toShort()
                            variable = "SISTEMA_ITEMS_EXO_PM_PRECIO"
                        }
                        "SISTEMA_ITEMS_EXO_PA_PRECIO", "PANEL_ITEMS_EXO_PA_PRECIO", "PANEL_ITEMS_EXO_PA" -> {
                            SISTEMA_ITEMS_EXO_PA_PRECIO = valor.toShort()
                            variable = "SISTEMA_ITEMS_EXO_PA_PRECIO"
                        }
                        "SISTEMA_ITEMS_PERFECTO_MULTIPLICA_POR", "PANEL_ITEMS_PERFECTO_MULTIPLICA_POR" -> {
                            SISTEMA_ITEMS_PERFECTO_MULTIPLICA_POR = valor.toFloat()
                            variable = "SISTEMA_ITEMS_PERFECTO_MULTIPLICA_POR"
                        }
                        "SISTEMA_ITEMS_EXO_TIPOS_NO_PERMITIDOS", "PANEL_ITEMS_EXO_TIPOS_NO_PERMITIDOS" -> {
                            variable = "SISTEMA_ITEMS_EXO_TIPOS_NO_PERMITIDOS"
                            for (s in valor.split(",".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    SISTEMA_ITEMS_EXO_TIPOS_NO_PERMITIDOS.add(s.toShort())
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "MAPAS_MODO_HEROICO" -> {
                            variable = "MAPAS_MODO_HEROICO"
                            for (s in valor.split(",".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    MAPAS_MODO_HEROICO.add(s.toShort())
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "RUNAS_NO_PERMITIDAS" -> {
                            variable = "RUNAS_NO_PERMITIDAS"
                            for (s in valor.split(",".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    RUNAS_NO_PERMITIDAS.add(s.toInt())
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "MOBS_DOBLE_ORBES" -> {
                            variable = "MOBS_DOBLE_ORBES"
                            for (s in valor.split(",".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    MOBS_DOBLE_ORBES.add(s.toInt())
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "MOBS_NO_ORBES" -> {
                            variable = "MOBS_NO_ORBES"
                            for (s in valor.split(",".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    MOBS_NO_ORBES.add(s.toInt())
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "NPCS_VENDE_OBJETOS_STATS_MAXIMOS", "IDS_NPCS_VENDE_OBJETOS_STATS_MAXIMOS" -> {
                            variable = "IDS_NPCS_VENDE_OBJETOS_STATS_MAXIMOS"
                            for (s in valor.split(",".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    IDS_NPCS_VENDE_OBJETOS_STATS_MAXIMOS.add(s.toInt())
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "IDS_OBJETOS_STATS_MAXIMOS", "OBJETOS_STATS_MAXIMOS" -> {
                            variable = "IDS_OBJETOS_STATS_MAXIMOS"
                            for (s in valor.split(",".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    IDS_OBJETOS_STATS_MAXIMOS.add(s.toInt())
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "IDS_OBJETOS_STATS_MINIMOS", "OBJETOS_STATS_MINIMOS" -> {
                            variable = "IDS_OBJETOS_STATS_MINIMOS"
                            for (s in valor.split(",".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    IDS_OBJETOS_STATS_MINIMOS.add(s.toInt())
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "IDS_OBJETOS_STATS_RANDOM", "OBJETOS_STATS_RANDOM" -> {
                            variable = "IDS_OBJETOS_STATS_RANDOM"
                            for (s in valor.split(",".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    IDS_OBJETOS_STATS_RANDOM.add(s.toInt())
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "OGRINAS_POR_VOTO" -> {
                            OGRINAS_POR_VOTO = valor.toShort()
                            variable = "OGRINAS_POR_VOTO"
                        }
                        "VALOR_KAMAS_POR_OGRINA" -> {
                            VALOR_KAMAS_POR_OGRINA = valor.toInt()
                            variable = "VALOR_KAMAS_POR_OGRINA"
                        }
                        "DIAS_INTERCAMBIO_COMPRAR_SISTEMA_ITEMS", "DIAS_INTERCAMBIO_COMPRAR_PANEL_ITEMS" -> {
                            DIAS_INTERCAMBIO_COMPRAR_SISTEMA_ITEMS = valor.toInt()
                            variable = "DIAS_INTERCAMBIO_COMPRAR_SISTEMA_ITEMS"
                        }
                        "KAMAS_RULETA_JALATO" -> {
                            KAMAS_RULETA_JALATO = valor.toInt()
                            variable = "KAMAS_RULETA_JALATO"
                        }
                        "KAMAS_MOSTRAR_PROBABILIDAD_FORJA_FM", "KAMAS_MOSTRAR_PROBABILIDAD_FORJAMAGIA", "KAMAS_MOSTRAR_PROBABILIDAD_FORJA" -> {
                            KAMAS_MOSTRAR_PROBABILIDAD_FORJA = valor.toInt()
                            variable = "KAMAS_MOSTRAR_PROBABILIDAD_FORJA"
                        }
                        "KAMAS_BANCO" -> {
                            KAMAS_BANCO = valor.toInt()
                            variable = "KAMAS_BANCO"
                        }
                        "SABIDURIA_PARA_REENVIO" -> {
                            SABIDURIA_PARA_REENVIO = valor.toInt()
                            variable = "SABIDURIA_PARA_REENVIO"
                        }
                        "ID_MIMOBIONTE" -> {
                            ID_MIMOBIONTE = valor.toInt()
                            variable = "ID_MIMOBIONTE"
                        }
                        "ID_ORBE" -> {
                            ID_ORBE = valor.toInt()
                            variable = "ID_ORBE"
                        }
                        "CANTIDAD_GRUPO_MOBS_MOVER_POR_MAPA" -> {
                            CANTIDAD_GRUPO_MOBS_MOVER_POR_MAPA = valor.toInt()
                            variable = "CANTIDAD_GRUPO_MOBS_MOVER_POR_MAPA"
                        }
                        "MAX_CARACTERES_SONIDO" -> {
                            MAX_CARACTERES_SONIDO = valor.toInt()
                            variable = "MAX_CARACTERES_SONIDO"
                        }
                        "MAX_PACKETS_PARA_RASTREAR" -> {
                            MAX_PACKETS_PARA_RASTREAR = valor.toInt()
                            variable = "MAX_PACKETS_PARA_RASTREAR"
                        }
                        "MAX_PACKETS_DESCONOCIDOS" -> {
                            MAX_PACKETS_DESCONOCIDOS = valor.toInt()
                            variable = "MAX_PACKETS_DESCONOCIDOS"
                        }
                        "MININMO_CANTIDAD_MOBS_EN_GRUPO", "MIN_CANTIDAD_MOBS_EN_GRUPO" -> {
                            MIN_CANTIDAD_MOBS_EN_GRUPO = valor.toInt()
                            variable = "MIN_CANTIDAD_MOBS_EN_GRUPO"
                        }
                        "MAX_CUENTAS_POR_IP" -> {
                            MAX_CUENTAS_POR_IP = valor.toInt()
                            variable = "MAX_CUENTAS_POR_IP"
                        }
                        "TIME_SLEEP_PACKETS_CARGAR_MAPA" -> {
                            TIME_SLEEP_PACKETS_CARGAR_MAPA = valor.toInt()
                            variable = "TIME_SLEEP_PACKETS_CARGAR_MAPA"
                        }
                        "MAX_ID_OBJETO_MODELO", "ID_OBJETO_MODELO_MAX" -> {
                            MAX_ID_OBJETO_MODELO = valor.toInt()
                            variable = "MAX_ID_OBJETO_MODELO"
                        }
                        "CANTIDAD_MIEMBROS_EQUIPO_KOLISEO", "CANTIDAD_VS_KOLISEO" -> {
                            CANTIDAD_MIEMBROS_EQUIPO_KOLISEO = valor.toInt()
                            if (CANTIDAD_MIEMBROS_EQUIPO_KOLISEO > 3) {
                                CANTIDAD_MIEMBROS_EQUIPO_KOLISEO = 3
                            } else if (CANTIDAD_MIEMBROS_EQUIPO_KOLISEO < 1) {
                                CANTIDAD_MIEMBROS_EQUIPO_KOLISEO = 1
                            }
                            variable = "CANTIDAD_MIEMBROS_EQUIPO_KOLISEO"
                        }
                        "RANGO_NIVEL_PVP" -> {
                            RANGO_NIVEL_PVP = valor.toInt()
                            variable = "RANGO_NIVEL_PVP"
                        }
                        "RANGO_NIVEL_KOLISEO" -> {
                            RANGO_NIVEL_KOLISEO = valor.toInt()
                            variable = "RANGO_NIVEL_KOLISEO"
                        }
                        "PROBABILIDAD_ESCAPAR_MONTURA_DESPUES_FECUNDAR" -> {
                            PROBABILIDAD_ESCAPAR_MONTURA_DESPUES_FECUNDAR = valor.toInt()
                            variable = "PROBABILIDAD_ESCAPAR_MONTURA_DESPUES_FECUNDAR"
                        }
                        "HONOR_FIJO_PARA_TODOS" -> {
                            HONOR_FIJO_PARA_TODOS = valor.toInt()
                            variable = "HONOR_FIJO_PARA_TODOS"
                        }
                        "NIVEL_MINIMO_PARA_PVP" -> {
                            NIVEL_MINIMO_PARA_PVP = valor.toInt()
                            variable = "NIVEL_MINIMO_PARA_PVP"
                        }
                        "ADIC_CAC" -> {
                            EfectoHechizo.MULTIPLICADOR_DAÑO_CAC = valor.toFloat()
                            variable = "MULTIPLICADOR_DAÑO_CAC"
                        }
                        "ADIC_MOB" -> {
                            EfectoHechizo.MULTIPLICADOR_DAÑO_MOB = valor.toFloat()
                            variable = "MULTIPLICADOR_DAÑO_MOB"
                        }
                        "ADIC_PJ" -> {
                            EfectoHechizo.MULTIPLICADOR_DAÑO_PJ = valor.toFloat()
                            variable = "MULTIPLICADOR_DAÑO_PJ"
                        }
                        "PROBABILIDAD_ARCHI_MOBS" -> {
                            PROBABILIDAD_ARCHI_MOBS = valor.toInt()
                            variable = "PROBABILIDAD_ARCHI_MOBS"
                        }
                        "MAX_PORCENTAJE_DE_STAT_PARA_FM", "PORCENTAJE_MAX_STAT_PARA_FM" -> {
                            MAX_PORCENTAJE_DE_STAT_PARA_FM = valor.toInt()
                            variable = "MAX_PORCENTAJE_DE_STAT_PARA_FM"
                        }
                        "PROBABILIDAD_PROTECTOR_RECURSOS" -> {
                            PROBABILIDAD_PROTECTOR_RECURSOS = valor.toInt()
                            variable = "PROBABILIDAD_PROTECTOR_RECURSOS"
                        }
                        "PROBABILIDAD_RECURSO_ESPECIAL", "PROBABILIDAD_OBJ_ESPECIAL" -> {
                            PROBABILIDAD_RECURSO_ESPECIAL = valor.toInt()
                            variable = "PROBABILIDAD_RECURSO_ESPECIAL"
                        }
                        "PROBABILIDAD_LOST_STATS_FM", "PROBABLIDAD_PERDER_STATS_FM", "PROBABILIDAD_FALLO_FM" -> {
                            PROBABLIDAD_PERDER_STATS_FM = valor.toInt()
                            variable = "PROBABLIDAD_PERDER_STATS_FM"
                        }
                        "MODO_MAPAS_LIMITE", "MODO_MAPAS_TEST" -> {
                            MODO_MAPAS_LIMITE = valor.equals("true", ignoreCase = true)
                            variable = "MODO_MAPAS_LIMITE"
                        }
                        "STR_MAPAS_LIMITE", "STR_MAPAS_TEST" -> {
                            STR_MAPAS_LIMITE = valor
                            variable = "STR_MAPAS_LIMITE"
                        }
                        "STR_SUBAREAS_LIMITE", "STR_SUBAREAS_TEST" -> {
                            STR_SUBAREAS_LIMITE = valor
                            variable = "STR_SUBAREAS_LIMITE"
                        }
                        "SUFIJO_RESET" -> {
                            SUFIJO_RESET = valor
                            variable = "SUFIJO_RESET"
                        }
                        "MUTE_CANAL_INCARNAM" -> {
                            MUTE_CANAL_INCARNAM = valor.equals("true", ignoreCase = true)
                            variable = "MUTE_CANAL_INCARNAM"
                        }
                        "MUTE_CANAL_RECLUTAMIENTO" -> {
                            MUTE_CANAL_RECLUTAMIENTO = valor.equals("true", ignoreCase = true)
                            variable = "MUTE_CANAL_RECLUTAMIENTO"
                        }
                        "MUTE_CANAL_ALINEACION" -> {
                            MUTE_CANAL_ALINEACION = valor.equals("true", ignoreCase = true)
                            variable = "MUTE_CANAL_ALINEACION"
                        }
                        "MUTE_CANAL_COMERCIO" -> {
                            MUTE_CANAL_COMERCIO = valor.equals("true", ignoreCase = true)
                            variable = "MUTE_CANAL_COMERCIO"
                        }
                        "PARAM_RESTRINGIR_COLOR_DIA", "PARAM_SIEMPRE_DIA" -> {
                            PARAM_RESTRINGIR_COLOR_DIA = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_RESTRINGIR_COLOR_DIA"
                        }
                        "PARAM_PERMITIR_ORNAMENTOS" -> {
                            PARAM_PERMITIR_ORNAMENTOS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_PERMITIR_ORNAMENTOS"
                        }
                        "PARAM_MOSTRAR_STATS_INVOCACION" -> {
                            PARAM_MOSTRAR_STATS_INVOCACION = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MOSTRAR_STATS_INVOCACION"
                        }
                        "PARAM_ENCRIPTAR_PACKETS" -> {
                            PARAM_ENCRIPTAR_PACKETS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_ENCRIPTAR_PACKETS"
                        }
                        "PARAM_FM_CON_POZO_RESIDUAL" -> {
                            PARAM_FM_CON_POZO_RESIDUAL = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_FM_CON_POZO_RESIDUAL"
                        }
                        "PARAM_SISTEMA_IP_ESPERA", "SISTEMA_IP_ESPERA" -> {
                            PARAM_SISTEMA_IP_ESPERA = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_SISTEMA_IP_ESPERA"
                        }
                        "PARAM_PERMITIR_MULTICUENTA_PELEA_KOLISEO", "PARAM_MISMA_IP_VS_KOLISEO" -> {
                            PARAM_PERMITIR_MULTICUENTA_PELEA_KOLISEO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_PERMITIR_MULTICUENTA_PELEA_KOLISEO"
                        }
                        "PARAM_PERMITIR_MULTICUENTA_PELEA_RECAUDADOR", "PARAM_MISMA_IP_VS_RECAUDADOR" -> {
                            PARAM_PERMITIR_MULTICUENTA_PELEA_RECAUDADOR = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_PERMITIR_MULTICUENTA_PELEA_RECAUDADOR"
                        }
                        "PARAM_PERMITIR_MULTICUENTA_PELEA_PVP", "PARAM_AGRESION_MULTICUENTA" -> {
                            PARAM_PERMITIR_MULTICUENTA_PELEA_PVP = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_PERMITIR_MULTICUENTA_PELEA_PVP"
                        }
                        "PARAM_MOSTRAR_EXP_MOBS" -> {
                            PARAM_MOSTRAR_EXP_MOBS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MOSTRAR_EXP_MOBS"
                        }
                        "PARAM_MOSTRAR_APODO_LISTA_AMIGOS" -> {
                            PARAM_MOSTRAR_APODO_LISTA_AMIGOS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MOSTRAR_APODO_LISTA_AMIGOS"
                        }
                        "PARAM_CLASIFICAR_POR_STUFF_EN_KOLISEO" -> {
                            PARAM_CLASIFICAR_POR_STUFF_EN_KOLISEO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_CLASIFICAR_POR_STUFF_EN_KOLISEO"
                        }
                        "PARAM_CLASIFICAR_POR_RANKING_EN_KOLISEO" -> {
                            PARAM_CLASIFICAR_POR_RANKING_EN_KOLISEO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_CLASIFICAR_POR_RANKING_EN_KOLISEO"
                        }
                        "PARAM_PERMITIR_MISMAS_CLASES_EN_KOLISEO" -> {
                            PARAM_PERMITIR_MISMAS_CLASES_EN_KOLISEO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_PERMITIR_MISMAS_CLASES_EN_KOLISEO"
                        }
                        "PARAM_PERMITIR_DESACTIVAR_ALINEACION", "PARAM_PERMITIR_DESACTIVAR_ALAS" -> {
                            PARAM_PERMITIR_DESACTIVAR_ALAS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_PERMITIR_DESACTIVAR_ALAS"
                        }
                        "PARAM_AGREDIR_ALAS_DESACTIVADAS", "PARAM_AGREDIR_PJ_ALAS_DESACTIVADAS" -> {
                            PARAM_AGREDIR_ALAS_DESACTIVADAS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_AGREDIR_ALAS_DESACTIVADAS"
                        }
                        "MENSAJE_REBOOT", "MENSAJE_RESET", "MENSAJE_TIMER_REBOOT", "MENSAJE_TIMER_RESET" -> {
                            MENSAJE_TIMER_REBOOT = valor
                            variable = "MENSAJE_TIMER_REBOOT"
                        }
                        "PARAM_SISTEMA_ORBES" -> {
                            PARAM_SISTEMA_ORBES = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_SISTEMA_ORBES"
                        }
                        "PARAM_MATRIMONIO_GAY" -> {
                            PARAM_MATRIMONIO_GAY = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MATRIMONIO_GAY"
                        }
                        "PARAM_PERMITIR_OFICIOS" -> {
                            PARAM_PERMITIR_OFICIOS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_PERMITIR_OFICIOS"
                        }
                        "PARAM_SALVAR_LOGS_AGRESION_SQL" -> {
                            PARAM_SALVAR_LOGS_AGRESION_SQL = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_SALVAR_LOGS_AGRESION_SQL"
                        }
                        "PARAM_MOB_TENER_NIVEL_INVOCADOR_PARA_EMPUJAR" -> {
                            PARAM_MOB_TENER_NIVEL_INVOCADOR_PARA_EMPUJAR = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MOB_TENER_NIVEL_INVOCADOR_PARA_EMPUJAR"
                        }
                        "PARAM_NO_USAR_CREDITOS" -> {
                            PARAM_NO_USAR_CREDITOS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_NO_USAR_CREDITOS"
                        }
                        "PARAM_PERMITIR_DESHONOR" -> {
                            PARAM_PERMITIR_DESHONOR = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_PERMITIR_DESHONOR"
                        }
                        "PARAM_PERMITIR_MILICIANOS_EN_PELEA" -> {
                            PARAM_PERMITIR_MILICIANOS_EN_PELEA = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_PERMITIR_MILICIANOS_EN_PELEA"
                        }
                        "PARAM_MOSTRAR_NOMBRE_ADMIN_5" -> {
                            PARAM_MOSTRAR_NOMBRE_ADMIN_5 = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MOSTRAR_NOMBRE_ADMIN_5"
                        }
                        "PARAM_PERMITIR_AGRESION_MILICIANOS" -> {
                            PARAM_PERMITIR_AGRESION_MILICIANOS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_PERMITIR_AGRESION_MILICIANOS"
                        }
                        "PARAM_CAPTURAR_MONTURA_COMO_PERGAMINO" -> {
                            PARAM_CAPTURAR_MONTURA_COMO_PERGAMINO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_CAPTURAR_MONTURA_COMO_PERGAMINO"
                        }
                        "PARAM_EXPULSAR_PREFASE_PVP" -> {
                            PARAM_EXPULSAR_PREFASE_PVP = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_EXPULSAR_PREFASE_PVP"
                        }
                        "PARAM_TODOS_MOBS_EN_BESTIARIO" -> {
                            PARAM_TODOS_MOBS_EN_BESTIARIO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_TODOS_MOBS_EN_BESTIARIO"
                        }
                        "PARAM_INFO_DAÑO_BATALLA" -> {
                            PARAM_INFO_DAÑO_BATALLA = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_INFO_DAÑO_BATALLA"
                        }
                        "PARAM_BOOST_SACRO_DESBUFEABLE" -> {
                            PARAM_BOOST_SACRO_DESBUFEABLE = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_BOOST_SACRO_DESBUFEABLE"
                        }
                        "PARAM_REINICIAR_CANALES" -> {
                            PARAM_REINICIAR_CANALES = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_REINICIAR_CANALES"
                        }
                        "PARAM_NO_USAR_OGRINAS" -> {
                            PARAM_NO_USAR_OGRINAS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_NO_USAR_OGRINAS"
                        }
                        "PARAM_REGISTRO_LOGS_JUGADORES", "PARAM_REGISTRO_JUGADORES" -> {
                            PARAM_REGISTRO_LOGS_JUGADORES = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_REGISTRO_LOGS_JUGADORES"
                        }
                        "PARAM_REGISTRO_LOGS_SQL" -> {
                            PARAM_REGISTRO_LOGS_SQL = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_REGISTRO_LOGS_SQL"
                        }
                        "PARAM_BOTON_BOUTIQUE" -> {
                            PARAM_BOTON_BOUTIQUE = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_BOTON_BOUTIQUE"
                        }
                        "PARAM_AUTO_SALTAR_TURNO" -> {
                            PARAM_AUTO_SALTAR_TURNO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_AUTO_SALTAR_TURNO"
                        }
                        "PARAM_TITULO_MAESTRO_OFICIO" -> {
                            PARAM_TITULO_MAESTRO_OFICIO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_TITULO_MAESTRO_OFICIO"
                        }
                        "PARAM_GANAR_KAMAS_PVP" -> {
                            PARAM_GANAR_KAMAS_PVP = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_GANAR_KAMAS_PVP"
                        }
                        "PARAM_GANAR_EXP_PVP" -> {
                            PARAM_GANAR_EXP_PVP = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_GANAR_EXP_PVP"
                        }
                        "PARAM_MASCOTAS_PERDER_VIDA" -> {
                            PARAM_MASCOTAS_PERDER_VIDA = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MASCOTAS_PERDER_VIDA"
                        }
                        "PARAM_LIMITE_MIEMBROS_GREMIO" -> {
                            PARAM_LIMITE_MIEMBROS_GREMIO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_LIMITE_MIEMBROS_GREMIO"
                        }
                        "PARAM_MOSTRAR_PROBABILIDAD_TACLEO" -> {
                            PARAM_MOSTRAR_PROBABILIDAD_TACLEO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MOSTRAR_PROBABILIDAD_TACLEO"
                        }
                        "PARAM_AGRESION_ADMIN" -> {
                            PARAM_AGRESION_ADMIN = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_AGRESION_ADMIN"
                        }
                        "PARAM_RESET_STATS_PLAYERS" -> {
                            PARAM_RESET_STATS_PLAYERS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_RESET_STATS_PLAYERS"
                        }
                        "PARAM_GANAR_HONOR_RANDOM" -> {
                            PARAM_GANAR_HONOR_RANDOM = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_GANAR_HONOR_RANDOM"
                        }
                        "PARAM_SISTEMA_ITEMS_SOLO_PERFECTO", "PARAM_SISTEMA_ITEMS_PERFECTO" -> {
                            PARAM_SISTEMA_ITEMS_SOLO_PERFECTO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_SISTEMA_ITEMS_SOLO_PERFECTO"
                        }
                        "PARAM_SISTEMA_ITEMS_EXO_PA_PM" -> {
                            PARAM_SISTEMA_ITEMS_EXO_PA_PM = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_SISTEMA_ITEMS_EXO_PA_PM"
                        }
                        "PARAM_FORMULA_TIPO_OFICIAL" -> {
                            PARAM_FORMULA_TIPO_OFICIAL = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_FORMULA_TIPO_OFICIAL"
                        }
                        "PARAM_BORRAR_CUENTAS_VIEJAS", "BORRAR_CUENTAS_VIEJAS" -> {
                            PARAM_BORRAR_CUENTAS_VIEJAS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_BORRAR_CUENTAS_VIEJAS"
                        }
                        "PARAM_MOSTRAR_IP_CONECTANDOSE" -> {
                            PARAM_MOSTRAR_IP_CONECTANDOSE = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MOSTRAR_IP_CONECTANDOSE"
                        }
                        "PARAM_TIMER_ACCESO" -> {
                            PARAM_TIMER_ACCESO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_TIMER_ACCESO"
                        }
                        "PARAM_START_EMOTES_COMPLETOS", "PARAM_START_EMOTES" -> {
                            PARAM_START_EMOTES_COMPLETOS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_START_EMOTES_COMPLETOS"
                        }
                        "PARAM_CRIAR_MONTURA", "PARAM_CRIAR_DRAGOPAVO" -> {
                            PARAM_CRIAR_MONTURA = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_CRIAR_MONTURA"
                        }
                        "PARAM_MOVER_MOBS_FIJOS" -> {
                            PARAM_MOVER_MOBS_FIJOS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MOVER_MOBS_FIJOS"
                        }
                        "PARAM_MOBS_RANDOM_REAPARECER_OTRA_CELDA" -> {
                            PARAM_MOBS_RANDOM_REAPARECER_OTRA_CELDA = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MOBS_RANDOM_REAPARECER_OTRA_CELDA"
                        }
                        "PARAM_ALIMENTAR_MASCOTAS" -> {
                            PARAM_ALIMENTAR_MASCOTAS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_ALIMENTAR_MASCOTAS"
                        }
                        "PARAM_LOTERIA" -> {
                            PARAM_LOTERIA = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_LOTERIA"
                        }
                        "PARAM_LOTERIA_OGRINAS", "LOTERIA_OGRINAS" -> {
                            PARAM_LOTERIA_OGRINAS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_LOTERIA_OGRINAS"
                        }
                        "PARAM_PERDER_PDV_ARMAS_ETEREAS", "PARAM_LOST_PDV_WEAPONS_ETHEREES" -> {
                            PARAM_PERDER_PDV_ARMAS_ETEREAS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_PERDER_PDV_ARMAS_ETEREAS"
                        }
                        "PARAM_MENSAJE_ASESINOS_HEROICO" -> {
                            PARAM_MENSAJE_ASESINOS_HEROICO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MENSAJE_ASESINOS_HEROICO"
                        }
                        "PARAM_MENSAJE_ASESINOS_PVP" -> {
                            PARAM_MENSAJE_ASESINOS_PVP = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MENSAJE_ASESINOS_PVP"
                        }
                        "PARAM_MENSAJE_ASESINOS_KOLISEO" -> {
                            PARAM_MENSAJE_ASESINOS_KOLISEO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MENSAJE_ASESINOS_KOLISEO"
                        }
                        "PARAM_GUARDAR_LOGS_INTERCAMBIOS" -> {
                            PARAM_GUARDAR_LOGS_INTERCAMBIOS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_GUARDAR_LOGS_INTERCAMBIOS"
                        }
                        "PARAM_AGRESION_NEUTRALES", "PARAM_AGREDIR_NEUTRALES", "PARAM_AGRESION_NEUTRAL", "PARAM_AGREDIR_NEUTRAL" -> {
                            PARAM_AGREDIR_NEUTRAL = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_AGREDIR_NEUTRAL"
                        }
                        "BD_AUTO_COMMIT", "PARAM_AUTO_COMMIT" -> {
                            PARAM_AUTO_COMMIT = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_AUTO_COMMIT"
                        }
                        "PARAM_HEROICO_PIERDE_ITEMS_VIP" -> {
                            PARAM_HEROICO_PIERDE_ITEMS_VIP = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_HEROICO_PIERDE_ITEMS_VIP"
                        }
                        "PARAM_ESTRELLAS_RECURSOS" -> {
                            PARAM_ESTRELLAS_RECURSOS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_ESTRELLAS_RECURSOS"
                        }
                        "PARAM_ALMANAX" -> {
                            PARAM_ALMANAX = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_ALMANAX"
                        }
                        "PARAM_DEVOLVER_OGRINAS" -> {
                            PARAM_DEVOLVER_OGRINAS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_DEVOLVER_OGRINAS"
                        }
                        "PARAM_HEROICO_GAME_OVER" -> {
                            PARAM_HEROICO_GAME_OVER = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_HEROICO_GAME_OVER"
                        }
                        "PARAM_KOLISEO" -> {
                            PARAM_KOLISEO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_KOLISEO"
                        }
                        "PARAM_ALBUM_MOBS", "PARAM_BESTIARIO", "PARAM_ALBUM" -> {
                            PARAM_BESTIARIO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_BESTIARIO"
                        }
                        "PARAM_AUTO_PDV", "PARAM_AUTO_RECUPERAR_VIDA", "PARAM_AUTO_CURAR", "PARAM_AUTO_SANAR" -> {
                            PARAM_AUTO_RECUPERAR_TODA_VIDA = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_AUTO_RECUPERAR_TODA_VIDA"
                        }
                        "PARAM_VER_JUGADORES_KOLISEO" -> {
                            PARAM_VER_JUGADORES_KOLISEO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_VER_JUGADORES_KOLISEO"
                        }
                        "PARAM_OBJETOS_OGRINAS_LIGADO" -> {
                            PARAM_OBJETOS_OGRINAS_LIGADO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_OBJETOS_OGRINAS_LIGADO"
                        }
                        "PARAM_NOMBRE_ADMIN" -> {
                            PARAM_NOMBRE_ADMIN = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_NOMBRE_ADMIN"
                        }
                        "PARAM_VARIOS_RECAUDADORES" -> {
                            PARAM_VARIOS_RECAUDADORES = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_VARIOS_RECAUDADORES"
                        }
                        "PARAM_AGREDIR_JUGADORES_ASESINOS" -> {
                            PARAM_AGREDIR_JUGADORES_ASESINOS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_AGREDIR_JUGADORES_ASESINOS"
                        }
                        "PARAM_STOP_SEGUNDERO", "STOP_SEGUNDERO" -> {
                            PARAM_STOP_SEGUNDERO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_STOP_SEGUNDERO"
                        }
                        "PARAM_DELETE_PLAYERS_BUG", "PARAM_ELIMINAR_PERSONAJES_BUG" -> {
                            PARAM_ELIMINAR_PERSONAJES_BUG = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_ELIMINAR_PERSONAJES_BUG"
                        }
                        "PARAM_PERDER_ENERGIA" -> {
                            PARAM_PERDER_ENERGIA = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_PERDER_ENERGIA"
                        }
                        "PARAM_LADDER_NIVEL", "PARAM_RANKING_NIVEL" -> {
                            PARAM_LADDER_NIVEL = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_RANKING_NIVEL"
                        }
                        "PARAM_LADDER_STAFF", "PARAM_RANKING_STAFF" -> {
                            PARAM_LADDER_STAFF = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_RANKING_STAFF"
                        }
                        "PARAM_LADDER_KOLISEO", "PARAM_RANKING_KOLISEO" -> {
                            PARAM_LADDER_KOLISEO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_RANKING_KOLISEO"
                        }
                        "PARAM_LADDER_PVP", "PARAM_RANKING_PVP" -> {
                            PARAM_LADDER_PVP = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_RANKING_PVP"
                        }
                        "PARAM_LADDER_GREMIO", "PARAM_RANKING_GREMIO" -> {
                            PARAM_LADDER_GREMIO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_RANKING_GREMIO"
                        }
                        "PARAM_LADDER_EXP_DIA", "PARAM_RANKING_EXP_DIA" -> {
                            PARAM_LADDER_EXP_DIA = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_RANKING_EXP_DIA"
                        }
                        "PARAM_ANTI_SPEEDHACK" -> {
                            PARAM_ANTI_SPEEDHACK = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_ANTI_SPEEDHACK"
                        }
                        "PARAM_MOSTRAR_CHAT_VIP_TODOS" -> {
                            PARAM_MOSTRAR_CHAT_VIP_TODOS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MOSTRAR_CHAT_VIP_TODOS"
                        }
                        "PARAM_SOLO_PRIMERA_VEZ" -> {
                            PARAM_SOLO_PRIMERA_VEZ = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_SOLO_PRIMERA_VEZ"
                        }
                        "PARAM_PRECIO_RECURSOS_EN_OGRINAS", "PARAM_RECURSOS_EN_OGRINAS" -> {
                            PARAM_PRECIO_RECURSOS_EN_OGRINAS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_PRECIO_RECURSOS_EN_OGRINAS"
                        }
                        "PARAM_PVP" -> {
                            PARAM_PVP = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_PVP"
                        }
                        "PARAM_AURA" -> {
                            PARAM_ACTIVAR_AURA = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_ACTIVAR_AURA"
                        }
                        "PARAM_AURA_VIP" -> {
                            PARAM_AURA_VIP = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_AURA_VIP"
                        }
                        "PARAM_CRAFT_SIEMPRE_EXITOSA", "PARAM_RECETA_SIEMPRE_EXITOSA" -> {
                            PARAM_CRAFT_SIEMPRE_EXITOSA = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_CRAFT_SIEMPRE_EXITOSA"
                        }
                        "PARAM_CRAFT_PERFECTO_STATS", "PARAM_CRAFT_PERFECTO" -> {
                            PARAM_CRAFT_PERFECTO_STATS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_CRAFT_PERFECTO_STATS"
                        }
                        "PARAM_MONTURA_SIEMPRE_MONTABLES" -> {
                            PARAM_MONTURA_SIEMPRE_MONTABLES = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MONTURA_SIEMPRE_MONTABLES"
                        }
                        "JUGAR_RAPIDO", "PARAM_JUGAR_RAPIDO" -> {
                            PARAM_JUGAR_RAPIDO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_JUGAR_RAPIDO"
                        }
                        "PARAM_PERMITIR_MOBS", "PARAM_MOBS" -> {
                            PARAM_PERMITIR_MOBS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_PERMITIR_MOBS"
                        }
                        "PARAM_COMANDOS_JUGADOR" -> {
                            PARAM_COMANDOS_JUGADOR = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_COMANDOS_JUGADOR"
                        }
                        "PARAM_ANTI_DDOS" -> {
                            PARAM_ANTI_DDOS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_ANTI_DDOS"
                        }
                        "PARAM_MOSTRAR_NRO_TURNOS" -> {
                            PARAM_MOSTRAR_NRO_TURNOS = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_MOSTRAR_NRO_TURNOS"
                        }
                        "PARAM_RESET_STATS_OBJETO" -> {
                            PARAM_RESET_STATS_OBJETO = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_RESET_STATS_OBJETO"
                        }
                        "PARAM_OBJETOS_PEFECTOS_COMPRADOS_NPC" -> {
                            PARAM_OBJETOS_PEFECTOS_COMPRADOS_NPC = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_OBJETOS_PEFECTOS_COMPRADOS_NPC"
                        }
                        "PARAM_CINEMATIC_CREAR_PERSONAJE" -> {
                            PARAM_CINEMATIC_CREAR_PERSONAJE = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_CINEMATIC_CREAR_PERSONAJE"
                        }
                        "PARAM_DAR_ALINEACION_AUTOMATICA" -> {
                            PARAM_DAR_ALINEACION_AUTOMATICA = valor.equals("true", ignoreCase = true)
                            variable = "PARAM_DAR_ALINEACION_AUTOMATICA"
                        }
                        "MODO_PVP" -> {
                            MODO_PVP = valor.equals("true", ignoreCase = true)
                            variable = "MODO_PVP"
                        }
                        "MODO_HEROICO" -> {
                            MODO_HEROICO = valor.equals("true", ignoreCase = true)
                            variable = "MODO_HEROICO"
                        }
                        "MODO_ANKALIKE" -> {
                            MODO_ANKALIKE = valor.equals("true", ignoreCase = true)
                            variable = "MODO_ANKALIKE"
                        }
                        "MODO_BATTLE_ROYALE" -> {
                            MODO_BATTLE_ROYALE = valor.equals("true", ignoreCase = true)
                            variable = "MODO_BATTLE_ROYALE"
                        }
                        "MODO_BETA" -> {
                            MODO_BETA = valor.equals("true", ignoreCase = true)
                            variable = "MODO_BETA"
                        }
                        "DESHABILITAR_SQL" -> {
                            PARAM_DESHABILITAR_SQL = valor.equals("true", ignoreCase = true)
                            variable = "DESHABILITAR_SQL"
                        }
                        "PROBABILIDAD_MONTURAS_MACHOS_HEMBRAS", "PROBABILIDAD_MONTURAS_MACHOS_Y_HEMBRAS" -> {
                            val sss = valor.split(",".toRegex()).toTypedArray()
                            val machos = sss[0].toInt()
                            val hembras = sss[1].toInt()
                            Montura.SEXO_POSIBLES = ByteArray(machos + hembras)
                            var i = 0
                            while (i < machos + hembras) {
                                Montura.SEXO_POSIBLES[i] = (if (i < machos) 0 else 1).toByte()
                                i++
                            }
                            variable = "PROBABILIDAD_MONTURAS_MACHOS_HEMBRAS"
                        }
                        "RULETA_1", "RULETA_2", "RULETA_3", "RULETA_4", "RULETA_5", "RULETA_6", "RULETA_7", "RULETA_8" -> {
                            val ficha = valor.split(";".toRegex()).toTypedArray()[0].toInt()
                            val premios = valor.split(";".toRegex()).toTypedArray()[1]
                            Mundo.RULETA[ficha] = premios
                        }
                        "TIPO_RECURSOS" -> {
                            variable = "TIPO_RECURSOS"
                            for (s in valor.split(",".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    TIPO_RECURSOS.add(s.toShort())
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "OBJ_NO_PERMITIDOS" -> {
                            variable = "OBJ_NO_PERMITIDOS"
                            for (s in valor.split(",".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    OBJ_NO_PERMITIDOS.add(s.toInt())
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "SUBAREAS_NO_PVP" -> {
                            variable = "SUBAREAS_NO_PVP"
                            for (s in valor.split(",".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    SUBAREAS_NO_PVP.add(s.toInt())
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "TIPO_ALIMENTO_MONTURA" -> {
                            variable = "TIPO_ALIMENTO_MONTURA"
                            for (s in valor.split(",".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    TIPO_ALIMENTO_MONTURA.add(s.toShort())
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        "HORARIO_DIA" -> {
                            val dia = valor.split(":".toRegex()).toTypedArray()
                            try {
                                val h = dia[0].toInt()
                                if (h in 0..23) {
                                    HORA_DIA = h
                                }
                            } catch (ignored: Exception) {
                            }
                            try {
                                val h = dia[1].toInt()
                                if (h in 0..59) {
                                    MINUTOS_DIA = h
                                }
                            } catch (ignored: Exception) {
                            }
                        }
                        "HORARIO_NOCHE" -> {
                            val noche = valor.split(":".toRegex()).toTypedArray()
                            try {
                                val h = noche[0].toInt()
                                if (h in 0..23) {
                                    HORA_NOCHE = h
                                }
                            } catch (ignored: Exception) {
                            }
                            try {
                                val h = noche[1].toInt()
                                if (h in 0..59) {
                                    MINUTOS_NOCHE = h
                                }
                            } catch (ignored: Exception) {
                            }
                        }
                        "PALABRAS_PROHIBIDAS", "BLOCK_WORD" -> {
                            variable = "PALABRAS_PROHIBIDAS"
                            for (s in valor.split(",".toRegex()).toTypedArray()) {
                                if (s.isEmpty()) {
                                    continue
                                }
                                try {
                                    PALABRAS_PROHIBIDAS.add(s.toLowerCase())
                                } catch (ignored: Exception) {
                                }
                            }
                        }
                        else -> if (parametro.isNotEmpty() && parametro[0] != '#') {
                            println("NO EXISTE EL COMANDO O PARAMETRO : $parametro")
                        }
                    }
                    if (variable.isNotEmpty()) {
                        if (repetidos[variable] != null) {
                            if (perso != null) {
                                ENVIAR_BAT2_CONSOLA(
                                    perso, "Config Exception COMMAND REPEAT " + parametro.toUpperCase()
                                            + " WITH " + repetidos[variable]
                                )
                            }
                            println("EL PARAMETRO " + parametro.toUpperCase() + " ES SIMILAR AL PARAMETRO " + repetidos[variable] + " POR FAVOR ELIMINA UNO")
                            if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
                                exitProcess(1)
                            }
                            return
                        }
                        repetidos[variable] = parametro.toUpperCase()
                    }
                } catch (ignored: Exception) {
                }
                linea = config.readLine()
            }
            config.close()
            if (BD_ESTATICA == null || BD_CUENTAS == null || BD_DINAMICA == null || BD_HOST == null || BD_PASS == null || BD_USUARIO == null) {
                throw Exception()
            }
        } catch (e: Exception) {
            if (perso != null) {
                ENVIAR_BAT2_CONSOLA(perso, "Config Exception DONT FILE")
            }
            println(e.toString())
            println("Ficha de la configuración no existe o ilegible")
            println("Cerrando el server")
            if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
                exitProcess(1)
            }
            return
        }
        for (s in PERMITIR_MULTIMAN.split(",".toRegex()).toTypedArray()) {
            try {
                PERMITIR_MULTIMAN_TIPO_COMBATE.add(s.toByte())
            } catch (ignored: Exception) {
            }
        }
        for (str in GFX_CREA_TU_ITEM_CAPAS.split(",".toRegex()).toTypedArray()) {
            try {
                Constantes.GFX_CREA_TU_ITEM_CAPAS.add(str.toInt())
            } catch (ignored: Exception) {
            }
        }
        for (str in GFX_CREA_TU_ITEM_CINTURONES.split(",".toRegex()).toTypedArray()) {
            try {
                Constantes.GFX_CREA_TU_ITEM_CINTURONES.add(str.toInt())
            } catch (ignored: Exception) {
            }
        }
        for (str in GFX_CREA_TU_ITEM_SOMBREROS.split(",".toRegex()).toTypedArray()) {
            try {
                Constantes.GFX_CREA_TU_ITEM_SOMBREROS.add(str.toInt())
            } catch (ignored: Exception) {
            }
        }
        for (str in GFX_CREA_TU_ITEM_DOFUS.split(",".toRegex()).toTypedArray()) {
            try {
                Constantes.GFX_CREA_TU_ITEM_DOFUS.add(str.toInt())
            } catch (ignored: Exception) {
            }
        }
        for (str in GFX_CREA_TU_ITEM_ANILLOS.split(",".toRegex()).toTypedArray()) {
            try {
                Constantes.GFX_CREA_TU_ITEM_ANILLOS.add(str.toInt())
            } catch (ignored: Exception) {
            }
        }
        for (str in GFX_CREA_TU_ITEM_AMULETOS.split(",".toRegex()).toTypedArray()) {
            try {
                Constantes.GFX_CREA_TU_ITEM_AMULETOS.add(str.toInt())
            } catch (ignored: Exception) {
            }
        }
        for (str in GFX_CREA_TU_ITEM_ESCUDOS.split(",".toRegex()).toTypedArray()) {
            try {
                Constantes.GFX_CREA_TU_ITEM_ESCUDOS.add(str.toInt())
            } catch (ignored: Exception) {
            }
        }
        for (str in GFX_CREA_TU_ITEM_BOTAS.split(",".toRegex()).toTypedArray()) {
            try {
                Constantes.GFX_CREA_TU_ITEM_BOTAS.add(str.toInt())
            } catch (ignored: Exception) {
            }
        }
        Constantes.GFXS_CREA_TU_ITEM[Constantes.OBJETO_TIPO_CAPA] = Constantes.GFX_CREA_TU_ITEM_CAPAS
        Constantes.GFXS_CREA_TU_ITEM[Constantes.OBJETO_TIPO_SOMBRERO] = Constantes.GFX_CREA_TU_ITEM_SOMBREROS
        Constantes.GFXS_CREA_TU_ITEM[Constantes.OBJETO_TIPO_CINTURON] = Constantes.GFX_CREA_TU_ITEM_CINTURONES
        Constantes.GFXS_CREA_TU_ITEM[Constantes.OBJETO_TIPO_BOTAS] = Constantes.GFX_CREA_TU_ITEM_BOTAS
        Constantes.GFXS_CREA_TU_ITEM[Constantes.OBJETO_TIPO_AMULETO] = Constantes.GFX_CREA_TU_ITEM_AMULETOS
        Constantes.GFXS_CREA_TU_ITEM[Constantes.OBJETO_TIPO_ANILLO] = Constantes.GFX_CREA_TU_ITEM_ANILLOS
        Constantes.GFXS_CREA_TU_ITEM[Constantes.OBJETO_TIPO_ESCUDO] = Constantes.GFX_CREA_TU_ITEM_ESCUDOS
        Constantes.GFXS_CREA_TU_ITEM[Constantes.OBJETO_TIPO_DOFUS] = Constantes.GFX_CREA_TU_ITEM_DOFUS
        COMANDOS_PERMITIDOS.add("endaction")
        COMANDOS_PERMITIDOS.add("finaccion")
        if (IP_MULTISERVIDOR.isEmpty()) {
            IP_MULTISERVIDOR.add("127.0.0.1")
        }
        if (NIVEL_MAX_ESCOGER_NIVEL > NIVEL_MAX_PERSONAJE) {
            NIVEL_MAX_ESCOGER_NIVEL = NIVEL_MAX_PERSONAJE
        }
        if (MODO_ANKALIKE) { // MODO_HEROICO = false;
            PARAM_ESTRELLAS_RECURSOS = true
            PARAM_CRIAR_MONTURA = true
            PARAM_PVP = true
            PARAM_PERMITIR_MOBS = true
            PARAM_BESTIARIO = true
            PROBABILIDAD_RECURSO_ESPECIAL = 75
        }
        if (perso != null) {
            ENVIAR_BAT2_CONSOLA(perso, "CONFIG LOADED PERFECTLY!!!")
        }
    }

    fun redactarLogServidorSinPrint(str: String) {
        try {
            if (LOG_SERVIDOR == null) {
                return
            }
            val hora = Calendar.getInstance().time
            LOG_SERVIDOR!!.println("[$hora]  $str")
            LOG_SERVIDOR!!.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun FUN_RANDOM_MOB(): Double {
        return if (RATE_RANDOM_MOB == 1.0) {
            1.0
        } else Math.random() * (RATE_RANDOM_MOB - RATE_RANDOM_MOB_BASE) + RATE_RANDOM_MOB_BASE
    }

    @JvmStatic
    fun FUN_RANDOM_MOB_RESIS(): Double {
        return if (RATE_RANDOM_MOB == 1.0) {
            1.0
        } else Math.random() * 1 + 1
    }

    @JvmStatic
    fun FUN_RANDOM_PUNTOS(): Double {
        return if (RATE_RANDOM_PUNTOS == 1.0) {
            1.0
        } else Math.random() * (RATE_RANDOM_PUNTOS - RATE_RANDOM_PUNTOS_BASE) + RATE_RANDOM_PUNTOS_BASE
    }

    @JvmStatic
    fun FUN_RANDOM_ITEM(): Double {
        if (RATE_RANDOM_ITEM == 1.0) {
            return 1.0
        }
        return if (RATE_RANDOM_ITEM_BASE == RATE_RANDOM_ITEM) {
            RATE_RANDOM_ITEM
        } else Math.random() * (RATE_RANDOM_ITEM - RATE_RANDOM_ITEM_BASE) + RATE_RANDOM_ITEM_BASE
    }

    @JvmStatic
    fun redactarLogServidorln(str: String) {
        try {
            println(str)
            if (LOG_SERVIDOR == null) {
                return
            }
            val hora = Calendar.getInstance().time
            LOG_SERVIDOR!!.println("[$hora]  $str")
            LOG_SERVIDOR!!.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun redactarLogServidor(str: String) {
        try {
            print(str)
            if (LOG_SERVIDOR == null) {
                return
            }
            val hora = Calendar.getInstance().time
            LOG_SERVIDOR!!.print("[$hora]  $str")
            LOG_SERVIDOR!!.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun imprimirLogPlayers() {
        try {
            if (ServidorSocket.REGISTROS.isEmpty()) {
                return
            }
            val dia =
                Calendar.getInstance()[Calendar.DAY_OF_MONTH].toString() + "-" + (Calendar.getInstance()[Calendar.MONTH] + 1) + "-" + Calendar.getInstance()[Calendar.YEAR]
            val dir = File("Logs_Players_$NOMBRE_SERVER")
            if (!dir.exists()) {
                dir.mkdir()
            }
            val dir2 = File("Logs_Players_$NOMBRE_SERVER/$dia")
            if (!dir2.exists()) {
                dir2.mkdir()
            }
            for ((key, value) in ServidorSocket.REGISTROS) {
                if (!PARAM_REGISTRO_LOGS_JUGADORES && !ServidorSocket.JUGADORES_REGISTRAR.contains(key)) {
                    continue
                }
                val log = PrintStream(
                    FileOutputStream(
                        "Logs_Players_" + NOMBRE_SERVER + "/" + dia + "/Log_"
                                + key + "_" + dia + ".txt", true
                    )
                )
                log.println(value.toString())
                log.flush()
                log.close()
            }
            ServidorSocket.REGISTROS.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun imprimirLogCombates() {
        try {
            if (LOG_COMBATES.isEmpty()) {
                return
            }
            val fecha =
                Calendar.getInstance()[Calendar.DAY_OF_MONTH].toString() + "-" + (Calendar.getInstance()[Calendar.MONTH] + 1) + "-" + Calendar.getInstance()[Calendar.YEAR]
            try {
                val f = FileOutputStream(
                    "Logs_Combates_$NOMBRE_SERVER/Log_Combates_$fecha.txt",
                    true
                )
                val log = PrintStream(f)
                log.flush()
            } catch (e: IOException) {
                File("Logs_Combates_$NOMBRE_SERVER").mkdir()
                val log = PrintStream(
                    FileOutputStream(
                        "Log_Combates_" + NOMBRE_SERVER + "/Log_Combates_"
                                + fecha + ".txt", true
                    )
                )
                log.println("----- FECHA -----\t- TIPO -\t-- MAPA --\t-------- PANEL RESULTADOS --------")
                log.flush()
                log.close()
            }
            val log = PrintStream(
                FileOutputStream(
                    "Log_Combates_" + NOMBRE_SERVER + "/Log_Combates_" + fecha
                            + ".txt", true
                )
            )
            log.println(LOG_COMBATES.toString())
            log.flush()
            log.close()
            LOG_COMBATES = StringBuilder()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun imprimirLogSQL() {
        try {
            if (LOG_SQL.isEmpty()) {
                return
            }
            val fecha =
                Calendar.getInstance()[Calendar.DAY_OF_MONTH].toString() + "-" + (Calendar.getInstance()[Calendar.MONTH] + 1) + "-" + Calendar.getInstance()[Calendar.YEAR]
            try {
                val f = FileOutputStream("Logs_SQL_$NOMBRE_SERVER/Log_SQL_$fecha.txt", true)
                val log = PrintStream(f)
                log.flush()
            } catch (e: IOException) {
                File("Logs_SQL_$NOMBRE_SERVER").mkdir()
            }
            val log = PrintStream(
                FileOutputStream(
                    "Logs_SQL_$NOMBRE_SERVER/Log_SQL_$fecha.txt",
                    true
                )
            )
            log.println(LOG_SQL.toString())
            log.flush()
            log.close()
            LOG_SQL = StringBuilder()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cerrarServer() { // GestorSalida.ENVIAR_M145_MENSAJE_PANEL_INFORMACION_TODOS("CERRANDO SERVIDOR / CLOSING SERVER
// / FERMER SERVEUR");
        ENVIAR_Im_INFORMACION_A_TODOS("115;1 seconde")
        redactarLogServidorln(" ######## INICIANDO CIERRE DEL SERVIDOR  ########")
        try {
            val estabaCorriendo = Mundo.SERVIDOR_ESTADO != Constantes.SERVIDOR_OFFLINE
            Mundo.setServidorEstado(Constantes.SERVIDOR_OFFLINE)
            if (estabaCorriendo) { // GestorSQL.UPDATE_TODAS_CUENTAS_CERO();
                Mundo.devolverBoletos()
                while (Mundo.SALVANDO) {
                    try {
                        Thread.sleep(5000)
                    } catch (ignored: Exception) {
                    }
                }
                Mundo.salvarServidor(false)
                Mundo.salvarMapasEstrellas()
                //                try {
//                    Thread.sleep(1000);
//                } catch (final Exception ignored) {
//                }
                redactarLogServidor(" ########  CERRANDO SERVERSOCKET...  ")
                ServidorServer.INSTANCE.stop()
                redactarLogServidorln("... IS OK  ########")
                while (ELIMINANDO_OBJETOS.isNotEmpty()) {
                    try {
                        Thread.sleep(50)
                    } catch (e: Exception) {
                    }
                }
                if (!PARAM_AUTO_COMMIT) {
                    iniciarCommit(false)
                    redactarLogServidorln("######## ESPERANDO COMMIT SQL  ########")
                    try {
                        Thread.sleep(MILISEGUNDOS_CERRAR_SERVIDOR.toLong())
                    } catch (ignored: Exception) {
                    }
                }
            }
            try {
                Thread.sleep(1000)
            } catch (ignored: Exception) {
            }
        } catch (e: Exception) {
            redactarLogServidorln("EXCEPTION MIENTRAS SE CERRABA EL SERVIDOR : $e")
            e.printStackTrace()
        }
        redactarLogServidorln(" ########  IMPRIMIENDO LOGS PLAYERS  ########")
        imprimirLogPlayers()
        if (PARAM_REGISTRO_LOGS_SQL) {
            redactarLogServidorln(" ########  IMPRIMIENDO LOGS SQL  ########")
            imprimirLogPlayers()
        }
        redactarLogServidorln(" ########  SERVIDOR CERRO CON EXITO  ########")
    }

    fun resetRates() {
        RATE_XP_PVM = DEFECTO_XP_PVM
        RATE_XP_PVP = DEFECTO_XP_PVP
        RATE_XP_OFICIO = DEFECTO_XP_OFICIO
        RATE_HONOR = DEFECTO_XP_HONOR
        RATE_DROP_NORMAL = DEFECTO_DROP
        RATE_KAMAS = DEFECTO_KAMAS
        RATE_CRIANZA_MONTURA = DEFECTO_CRIANZA_MONTURA
    }

}