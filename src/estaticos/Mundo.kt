package estaticos

import estaticos.AtlantaMain.imprimirLogCombates
import estaticos.AtlantaMain.imprimirLogPlayers
import estaticos.AtlantaMain.modificarParam
import estaticos.AtlantaMain.redactarLogServidor
import estaticos.AtlantaMain.redactarLogServidorln
import estaticos.Constantes.getTiempoActualEscala
import estaticos.Encriptador.consultaWeb
import estaticos.Formulas.getRandomInt
import estaticos.GestorSalida.ENVIAR_BN_NADA
import estaticos.GestorSalida.ENVIAR_GM_BORRAR_GM_A_MAPA
import estaticos.GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION_A_TODOS
import estaticos.GestorSalida.ENVIAR_Im_INFORMACION_KOLISEO
import estaticos.GestorSalida.ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT
import estaticos.GestorSalida.ENVIAR_bRI_INICIAR_CUENTA_REGRESIVA_TODOS
import estaticos.GestorSalida.ENVIAR_bSO_PANEL_ITEMS_OBJETOS_POR_TIPO
import estaticos.GestorSalida.ENVIAR_bl_RANKING_DATA
import estaticos.GestorSalida.ENVIAR_hL_INFO_CASA
import estaticos.GestorSalida.ENVIAR_hP_PROPIEDADES_CASA
import estaticos.GestorSalida.ENVIAR_ÑL_BOTON_LOTERIA_TODOS
import estaticos.database.GestorSQL
import estaticos.database.GestorSQL.ADD_OGRINAS_CUENTA
import estaticos.database.GestorSQL.CARGAR_ACCIONES_USO_OBJETOS
import estaticos.database.GestorSQL.CARGAR_ACCION_FINAL_DE_PELEA
import estaticos.database.GestorSQL.CARGAR_ALMANAX
import estaticos.database.GestorSQL.CARGAR_AREA
import estaticos.database.GestorSQL.CARGAR_CASAS
import estaticos.database.GestorSQL.CARGAR_CERCADOS
import estaticos.database.GestorSQL.CARGAR_CLASES
import estaticos.database.GestorSQL.CARGAR_COFRES
import estaticos.database.GestorSQL.CARGAR_COMANDOS_MODELO
import estaticos.database.GestorSQL.CARGAR_COMIDAS_MASCOTAS
import estaticos.database.GestorSQL.CARGAR_CREA_OBJETOS_MODELOS
import estaticos.database.GestorSQL.CARGAR_CREA_OBJETOS_PRECIOS
import estaticos.database.GestorSQL.CARGAR_CUENTAS_SERVER_PERSONAJE
import estaticos.database.GestorSQL.CARGAR_DB_CUENTAS
import estaticos.database.GestorSQL.CARGAR_DONES_MODELOS
import estaticos.database.GestorSQL.CARGAR_DROPS
import estaticos.database.GestorSQL.CARGAR_DROPS_FIJOS
import estaticos.database.GestorSQL.CARGAR_ENCARNACIONES_MODELOS
import estaticos.database.GestorSQL.CARGAR_ESPECIALIDADES
import estaticos.database.GestorSQL.CARGAR_ETAPAS
import estaticos.database.GestorSQL.CARGAR_EXPERIENCIA
import estaticos.database.GestorSQL.CARGAR_GREMIOS
import estaticos.database.GestorSQL.CARGAR_HECHIZOS
import estaticos.database.GestorSQL.CARGAR_INTERACTIVOS
import estaticos.database.GestorSQL.CARGAR_MAPAS
import estaticos.database.GestorSQL.CARGAR_MAPAS_ESTRELLAS
import estaticos.database.GestorSQL.CARGAR_MAPAS_HEROICO
import estaticos.database.GestorSQL.CARGAR_MIEMBROS_GREMIO
import estaticos.database.GestorSQL.CARGAR_MISIONES
import estaticos.database.GestorSQL.CARGAR_MISION_OBJETIVOS
import estaticos.database.GestorSQL.CARGAR_MOBS_EVENTO
import estaticos.database.GestorSQL.CARGAR_MOBS_FIJOS
import estaticos.database.GestorSQL.CARGAR_MOBS_MODELOS
import estaticos.database.GestorSQL.CARGAR_MOBS_RAROS
import estaticos.database.GestorSQL.CARGAR_MONTURAS
import estaticos.database.GestorSQL.CARGAR_MONTURAS_MODELOS
import estaticos.database.GestorSQL.CARGAR_NPCS
import estaticos.database.GestorSQL.CARGAR_NPC_MODELOS
import estaticos.database.GestorSQL.CARGAR_OBJETOS
import estaticos.database.GestorSQL.CARGAR_OBJETOS_MODELOS
import estaticos.database.GestorSQL.CARGAR_OBJETOS_SETS
import estaticos.database.GestorSQL.CARGAR_OBJETOS_TRUEQUE
import estaticos.database.GestorSQL.CARGAR_OFICIOS
import estaticos.database.GestorSQL.CARGAR_ORNAMENTOS
import estaticos.database.GestorSQL.CARGAR_OTROS_INTERACTIVOS
import estaticos.database.GestorSQL.CARGAR_PERSONAJES
import estaticos.database.GestorSQL.CARGAR_PREGUNTAS
import estaticos.database.GestorSQL.CARGAR_PRISMAS
import estaticos.database.GestorSQL.CARGAR_RECAUDADORES
import estaticos.database.GestorSQL.CARGAR_RECETAS
import estaticos.database.GestorSQL.CARGAR_RESPUESTAS
import estaticos.database.GestorSQL.CARGAR_SERVICIOS
import estaticos.database.GestorSQL.CARGAR_SUBAREA
import estaticos.database.GestorSQL.CARGAR_TITULOS
import estaticos.database.GestorSQL.CARGAR_TRIGGERS
import estaticos.database.GestorSQL.CARGAR_TUTORIALES
import estaticos.database.GestorSQL.CARGAR_ZAAPS
import estaticos.database.GestorSQL.DELETE_CERCADO
import estaticos.database.GestorSQL.DELETE_DRAGOPAVO_LISTA
import estaticos.database.GestorSQL.DELETE_GREMIO
import estaticos.database.GestorSQL.DELETE_MONTURA
import estaticos.database.GestorSQL.DELETE_OBJETO
import estaticos.database.GestorSQL.DELETE_OBJETOS_LISTA
import estaticos.database.GestorSQL.DELETE_OTRO_INTERACTIVO
import estaticos.database.GestorSQL.DELETE_PERSONAJE
import estaticos.database.GestorSQL.DELETE_PRISMA
import estaticos.database.GestorSQL.DELETE_RANKING_PVP
import estaticos.database.GestorSQL.DELETE_RECAUDADOR
import estaticos.database.GestorSQL.GET_OGRINAS_CUENTA
import estaticos.database.GestorSQL.GET_PRIMERA_VEZ
import estaticos.database.GestorSQL.GET_SIG_ID_OBJETO
import estaticos.database.GestorSQL.GET_STATEMENT_SQL_DINAMICA
import estaticos.database.GestorSQL.RECARGAR_CASAS
import estaticos.database.GestorSQL.RECARGAR_CERCADOS
import estaticos.database.GestorSQL.RECARGAR_COFRES
import estaticos.database.GestorSQL.REPLACE_CASA
import estaticos.database.GestorSQL.REPLACE_CERCADO
import estaticos.database.GestorSQL.REPLACE_COFRE
import estaticos.database.GestorSQL.REPLACE_CUENTA_SERVIDOR
import estaticos.database.GestorSQL.REPLACE_GREMIO
import estaticos.database.GestorSQL.REPLACE_MAPAS_ESTRELLAS_BATCH
import estaticos.database.GestorSQL.REPLACE_MONTURA
import estaticos.database.GestorSQL.REPLACE_PRISMA
import estaticos.database.GestorSQL.REPLACE_RANKING_KOLISEO
import estaticos.database.GestorSQL.REPLACE_RANKING_PVP
import estaticos.database.GestorSQL.REPLACE_RECAUDADOR
import estaticos.database.GestorSQL.RESTAR_OGRINAS
import estaticos.database.GestorSQL.SALVAR_OBJETO
import estaticos.database.GestorSQL.SALVAR_OBJETOS
import estaticos.database.GestorSQL.SALVAR_PERSONAJE
import estaticos.database.GestorSQL.SELECT_ANIMACIONES
import estaticos.database.GestorSQL.SELECT_OBJETOS_MERCADILLO
import estaticos.database.GestorSQL.SELECT_PUESTOS_MERCADILLOS
import estaticos.database.GestorSQL.SELECT_RANKING_KOLISEO
import estaticos.database.GestorSQL.SELECT_RANKING_PVP
import estaticos.database.GestorSQL.SELECT_ZONAS
import estaticos.database.GestorSQL.SET_OGRINAS_CUENTA
import estaticos.database.GestorSQL.ejecutarBatch
import estaticos.database.GestorSQL.iniciarCommit
import estaticos.database.GestorSQL.timerCommit
import servidor.ServidorServer.Companion.clientes
import sincronizador.ExchangeClient
import utilidades.comandosAccion
import variables.casa.Casa
import variables.casa.Cofre
import variables.encarnacion.EncarnacionModelo
import variables.gremio.Gremio
import variables.gremio.Recaudador
import variables.hechizo.Hechizo
import variables.mapa.*
import variables.mapa.interactivo.ObjetoInteractivo
import variables.mapa.interactivo.ObjetoInteractivoModelo
import variables.mapa.interactivo.OtroInteractivo
import variables.mercadillo.Mercadillo
import variables.mision.MisionEtapaModelo
import variables.mision.MisionModelo
import variables.mision.MisionObjetivoModelo
import variables.mob.MobModelo
import variables.montura.Montura
import variables.montura.Montura.Ubicacion
import variables.montura.MonturaModelo
import variables.npc.NPCModelo
import variables.npc.ObjetoTrueque
import variables.npc.PreguntaNPC
import variables.npc.RespuestaNPC
import variables.objeto.*
import variables.objeto.ObjetoModelo.CAPACIDAD_STATS
import variables.oficio.Oficio
import variables.pelea.DropMob
import variables.personaje.*
import variables.ranking.RankingKoliseo
import variables.ranking.RankingPVP
import variables.zotros.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.regex.Pattern
import kotlin.collections.HashMap
import kotlin.concurrent.thread

//import com.mysql.jdbc.PreparedStatement;
//import variables.mob.GrupoMob;
object Mundo {
    // Fijos
    val MAPAS: MutableMap<Short, Mapa?> = TreeMap()
    val AREAS: MutableMap<Int, Area> = TreeMap()
    val SUPER_AREAS: MutableMap<Int, SuperArea> = TreeMap()
    val SUB_AREAS: MutableMap<Int, SubArea> = TreeMap()

    @JvmField
    val CERCADOS: MutableMap<Short, Cercado> = HashMap()
    val EXPERIENCIA: MutableMap<Int, Experiencia> = TreeMap()
    val HECHIZOS: MutableMap<Int, Hechizo> = HashMap()
    val OBJETOS_MODELOS: MutableMap<Int, ObjetoModelo> = HashMap()
    val SISTEMA_ITEMS: MutableMap<Short, String> = HashMap()
    val MOBS_MODELOS: MutableMap<Int, MobModelo> = HashMap()
    val MONTURAS_MODELOS: MutableMap<Int, MonturaModelo> = HashMap() //
    val NPC_MODELOS: MutableMap<Int, NPCModelo> = HashMap()
    val NPC_PREGUNTAS: MutableMap<Int, PreguntaNPC> = HashMap()
    val NPC_RESPUESTAS: MutableMap<Int, RespuestaNPC> = HashMap()
    val OFICIOS: MutableMap<Int, Oficio> = HashMap()
    val RECETAS: MutableMap<Int, ArrayList<Duo<Int, Int>>> =
        HashMap()
    val OBJETOS_SETS: MutableMap<Int, ObjetoSet> = HashMap()
    val CASAS: MutableMap<Int, Casa> = HashMap()
    val MERCADILLOS: MutableMap<Int, Mercadillo> = HashMap()
    val ANIMACIONES: MutableMap<Int, Animacion> = HashMap()
    val COMANDOSACCION: MutableMap<Int, comandosAccion> = HashMap() // El mapita de toda la vida
    val COFRES: MutableMap<Int, Cofre> = HashMap()
    val TUTORIALES: MutableMap<Int, Tutorial> = HashMap()
    val OBJETIVOS_MODELOS: MutableMap<Int, MisionObjetivoModelo> =
        HashMap()
    val ENCARNACIONES_MODELOS: MutableMap<Int, EncarnacionModelo> = HashMap()
    val ETAPAS: MutableMap<Int, MisionEtapaModelo> = HashMap()
    val MISIONES_MODELOS: MutableMap<Int, MisionModelo> = HashMap()
    val MASCOTAS_MODELOS: MutableMap<Int, MascotaModelo> = TreeMap()
    val ESPECIALIDADES: MutableMap<Int, Especialidad> = TreeMap()
    val DONES_MODELOS: MutableMap<Int, Int> = TreeMap()
    val objInteractivos =
        ArrayList<ObjetoInteractivoModelo>()
    val SERVICIOS: MutableMap<Int, Servicio> = HashMap()
    val COMANDOS: MutableMap<String, Int> = HashMap()
    val MAPAS_ESTRELLAS: MutableMap<Short, ArrayList<Short>> =
        HashMap()
    val MAPAS_HEROICOS: MutableMap<Short, ArrayList<String>> =
        HashMap()
    val MOBS_EVENTOS: MutableMap<Byte, ArrayList<Duo<Int, Int>>> =
        HashMap()
    val ALMANAX: MutableMap<Int, Almanax> = HashMap()

    @JvmField
    val OTROS_INTERACTIVOS = CopyOnWriteArrayList<OtroInteractivo>()

    @JvmField
    val PERSONAJESONLINE = CopyOnWriteArrayList<Personaje>()
    val OBJETOS_INTERACTIVOS = ArrayList<ObjetoInteractivo>()
    val DROPS_FIJOS = ArrayList<DropMob>()
    val OBJETOS_TRUEQUE = ArrayList<ObjetoTrueque>()
    val ZAAPS: MutableMap<Short, Short> = HashMap()
    val ZAAPIS_BONTA = ArrayList<Short>()
    val ZAAPIS_BRAKMAR = ArrayList<Short>()
    val CLASES: MutableMap<Int, Clase> = TreeMap()
    val CREA_TU_ITEM: MutableMap<Int, CreaTuItem> = TreeMap()
    val RULETA: MutableMap<Int, String> = TreeMap()
    val ORNAMENTOS: MutableMap<Int, Ornamento> = TreeMap()
    val TITULOS: MutableMap<Int, Titulo> = TreeMap()

    //
// concurrentes
//
    val cuentas = ConcurrentHashMap<Int, Cuenta>()
    val _PERSONAJES = ConcurrentHashMap<Int, Personaje>()
    val _MONTURAS = ConcurrentHashMap<Int, Montura>()

    //
// publicas
//
    @JvmField
    val ZONAS: MutableMap<Short, Short> = HashMap()

    // public static ArrayList<Short> MAPAS_OBJETIVOS = new ArrayList<>();
    val CAPTCHAS = ArrayList<String>()

    @JvmField
    val CUENTAS_A_BORRAR = ArrayList<Cuenta>()
    const val LIDER_RANKING = "Ninguno"
    private val _CUENTAS_POR_NOMBRE = ConcurrentHashMap<String, Int>()
    val _OBJETOS = ConcurrentHashMap<Int, Objeto>()
    private val _GREMIOS = ConcurrentHashMap<Int, Gremio>()
    private val _PRISMAS = ConcurrentHashMap<Int, Prisma>()
    private val _RECAUDADORES = ConcurrentHashMap<Int, Recaudador>()
    private val _RANKINGS_KOLISEO = ConcurrentHashMap<Int, RankingKoliseo>()
    private val _RANKINGS_PVP = ConcurrentHashMap<Int, RankingPVP>()

    // private static ConcurrentHashMap<Integer, Encarnacion> _ENCARNACIONES = new
// ConcurrentHashMap<Integer, Encarnacion>();
    private val _INSCRITOS_KOLISEO = ConcurrentHashMap<Int, Personaje>()
    private val _LADDER_KOLISEO = CopyOnWriteArrayList<RankingKoliseo>()
    private val _LADDER_PVP = CopyOnWriteArrayList<RankingPVP>()
    private val _LADDER_NIVEL = CopyOnWriteArrayList<Personaje>()
    private val _LADDER_EXP_DIA = CopyOnWriteArrayList<Personaje>()
    private val _LADDER_GREMIO = CopyOnWriteArrayList<Gremio>()

    //
// variables primitivas
//
    var BLOQUEANDO = false
    var VENDER_BOLETOS = false

    @JvmField
    var SERVIDOR_ESTADO = Constantes.SERVIDOR_OFFLINE
    var LOTERIA_BOLETOS = IntArray(10000)
    var SIG_ID_LINEA_MERCADILLO = 0
    var SIG_ID_OBJETO = 0
    var SIG_ID_PERSONAJE = 0
    var SIG_ID_MONTURA = -101
    var SIG_ID_RECAUDADOR = -100
    var SIG_ID_PRISMA = -102
    var CANT_SALVANDO = 0
    var TOTAL_SALVADO = 0
    var SEGUNDOS_INICIO_KOLISEO = 0
    var DIA_DEL_AÑO = 0
    var MOB_EVENTO: Byte = 0
    var SEG_CUENTA_REGRESIVA: Long = 0
    var MINUTOS_VIDA_REAL: Long = 0
    var MSJ_CUENTA_REGRESIVA = ""
    var LISTA_GFX = ""
    var LISTA_NIVEL = ""

    @JvmField
    var LISTA_ZONAS = ""
    var KAMAS_OBJ_CACERIA = ""
    var NOMBRE_CACERIA = ""
    var LISTA_MASCOTAS = ""
    var CLASES_PERMITIDAS = ""
    var CREA_TU_ITEM_OBJETOS = ""
    var CREA_TU_ITEM_DATA = ""
    var CREAT_TU_ITEM_PRECIOS = ""
    var SALVANDO = false

    fun crearServidor() {
        try {
            println(
                "TotalMemory: " + Runtime.getRuntime().totalMemory() / 1048576f + " MB\t" + "MaxMemory: "
                        + Runtime.getRuntime().maxMemory() / 1048576f + " MB"
            )
        } catch (ignored: Exception) {
        }
        for (s in Constantes.ZAAPI_BONTA.split(",".toRegex()).toTypedArray()) {
            if (s.isEmpty()) {
                continue
            }
            try {
                ZAAPIS_BONTA.add(s.toShort())
            } catch (ignored: Exception) {
            }
        }
        for (s in Constantes.ZAAPI_BRAKMAR.split(",".toRegex()).toTypedArray()) {
            if (s.isEmpty()) {
                continue
            }
            try {
                ZAAPIS_BRAKMAR.add(s.toShort())
            } catch (ignored: Exception) {
            }
        }
        DIA_DEL_AÑO = Calendar.getInstance()[Calendar.DAY_OF_YEAR]
        MINUTOS_VIDA_REAL = getTiempoActualEscala(1000 * 60.toLong())
        SEGUNDOS_INICIO_KOLISEO = AtlantaMain.SEGUNDOS_INICIAR_KOLISEO
        println("===========> Database Static <===========")
        CARGAR_CREA_OBJETOS_PRECIOS()
        CARGAR_CREA_OBJETOS_MODELOS()
        for (c in CREA_TU_ITEM.values) {
            if (!CREA_TU_ITEM_OBJETOS.isEmpty()) {
                CREA_TU_ITEM_OBJETOS += ","
                CREA_TU_ITEM_DATA += "|"
            }
            CREA_TU_ITEM_OBJETOS += c.iD
            CREA_TU_ITEM_DATA += c.iD.toString() + ";" + c.maximosStats + ";" + c.maxOgrinas + ";" + c.precioBase
        }
        print("Cargando las clases: ")
        CARGAR_CLASES()
        println(CLASES.size.toString() + " clases cargadas")
        for (c in CLASES.values) {
            if (!CLASES_PERMITIDAS.isEmpty()) {
                CLASES_PERMITIDAS += ","
            }
            CLASES_PERMITIDAS += c.id
        }
        print("Cargando los servicios: ")
        CARGAR_SERVICIOS()
        println(SERVICIOS.size.toString() + " servicios cargados")
        print("Cargando los ornamentos: ")
        CARGAR_ORNAMENTOS()
        println(ORNAMENTOS.size.toString() + " ornamentos cargados")
        print("Cargando los titulos: ")
        CARGAR_TITULOS()
        println(TITULOS.size.toString() + " titulos cargados")
        print("Cargando los comandos modelo: ")
        CARGAR_COMANDOS_MODELO()
        println(COMANDOS.size.toString() + " comandos modelo cargados")
        print("Cargando los dones: ")
        CARGAR_DONES_MODELOS()
        println(DONES_MODELOS.size.toString() + " dones cargados")
        print("Cargando las especialidades: ")
        CARGAR_ESPECIALIDADES()
        println(ESPECIALIDADES.size.toString() + " especialidades cargadas")
        print("Cargando los misiones almanax: ")
        CARGAR_ALMANAX()
        println(ALMANAX.size.toString() + " almanax cargados")
        print("Cargando los niveles de experiencia: ")
        CARGAR_EXPERIENCIA()
        EXPERIENCIA[AtlantaMain.NIVEL_MAX_PERSONAJE + 1] = Experiencia(8223372036854775808L, -1, -1, -1, -1, -1)
        println(EXPERIENCIA.size.toString() + " niveles cargados")
        print("Cargando los hechizos: ")
        CARGAR_HECHIZOS()
        println(HECHIZOS.size.toString() + " hechizos cargados")
        print("Cargando las encarnaciones modelos: ")
        CARGAR_ENCARNACIONES_MODELOS()
        println(ENCARNACIONES_MODELOS.size.toString() + " encarnaciones modelo cargados")
        print("Cargando los mounstros: ")
        CARGAR_MOBS_MODELOS()
        CARGAR_MOBS_RAROS()
        CARGAR_MOBS_EVENTO()
        println(MOBS_MODELOS.size.toString() + " mounstros cargados")
        print("Cargando los objetos modelos: ")
        CARGAR_OBJETOS_MODELOS()
        println(OBJETOS_MODELOS.size.toString() + " objetos modelos cargados")
        print("Cargando los sets de objetos: ")
        CARGAR_OBJETOS_SETS()
        println(OBJETOS_SETS.size.toString() + " set de objetos cargados")
        print("Cargando las monturas modelos: ")
        CARGAR_MONTURAS_MODELOS()
        println(MONTURAS_MODELOS.size.toString() + " monturas modelos cargados")
        print("Cargando los objetos trueque: ")
        CARGAR_OBJETOS_TRUEQUE()
        println(OBJETOS_TRUEQUE.size.toString() + " objetos trueque cargados")
        print("Cargando los drops: ")
        println(CARGAR_DROPS().toString() + " drops cargados")
        print("Cargando los drops fijos: ")
        println(CARGAR_DROPS_FIJOS().toString() + " drops fijos cargados")
        print("Cargando los NPC: ")
        CARGAR_NPC_MODELOS()
        println(NPC_MODELOS.size.toString() + " NPC cargados")
        print("Cargando las preguntas de NPC: ")
        CARGAR_PREGUNTAS()
        println(NPC_PREGUNTAS.size.toString() + " preguntas de NPC cargadas")
        print("Cargando las respuestas de NPC: ")
        CARGAR_RESPUESTAS()
        println(NPC_RESPUESTAS.size.toString() + " respuestas de NPC cargadas")
        print("Cargando las areas: ")
        CARGAR_AREA()
        println(AREAS.size.toString() + " areas cargadas")
        print("Cargando las sub-areas: ")
        CARGAR_SUBAREA()
        println(SUB_AREAS.size.toString() + " sub-areas cargadas")
        print("Cargando los objetos interactivos: ")
        CARGAR_INTERACTIVOS()
        println(objInteractivos.size.toString() + " objetos interactivos cargados")
        print("Cargando las recetas: ")
        CARGAR_RECETAS()
        println(RECETAS.size.toString() + " recetas cargadas")
        print("Cargando los oficios: ")
        CARGAR_OFICIOS()
        println(OFICIOS.size.toString() + " oficios cargados")
        print("Cargando los objetivos: ")
        CARGAR_MISION_OBJETIVOS()
        println(OBJETIVOS_MODELOS.size.toString() + " objetivos cargados")
        print("Cargando las etapas: ")
        CARGAR_ETAPAS()
        println(ETAPAS.size.toString() + " etapas cargadas")
        print("Cargando los misiones: ")
        CARGAR_MISIONES()
        println(MISIONES_MODELOS.size.toString() + " misiones cargados")
        CARGAR_MAPAS_ESTRELLAS()
        if (AtlantaMain.MODO_HEROICO || !AtlantaMain.MAPAS_MODO_HEROICO.isEmpty()) {
            print("Cargando los mapas heroicos: ")
            CARGAR_MAPAS_HEROICO()
            println(MAPAS_HEROICOS.size.toString() + " mapas heroicos cargados")
        }
        print("Cargando los mapas: ")
        val xxxx = System.currentTimeMillis()
        CARGAR_MAPAS()
        println(
            MAPAS.size.toString() + " mapas cargados ----> (en " + (System.currentTimeMillis() - xxxx)
                    + ") milisegundos"
        )
        print("Cargando los grupo mobs fijos: ")
        println(CARGAR_MOBS_FIJOS().toString() + " grupo mobs fijos cargados")
        print("Cargando los zaaps: ")
        CARGAR_ZAAPS()
        println(ZAAPS.size.toString() + " zaaps cargados")
        print("Cargando los triggers: ")
        println(CARGAR_TRIGGERS().toString() + " trigger cargados")
        print("Cargando las acciones de pelea: ")
        println(CARGAR_ACCION_FINAL_DE_PELEA().toString() + " acciones de pelea cargadas")
        print("Cargando los NPCs: ")
        println(CARGAR_NPCS().toString() + " NPCs cargados")
        print("Cargando las acciones de objetos: ")
        println(CARGAR_ACCIONES_USO_OBJETOS().toString() + " acciones de objetos cargados")
        print("Cargando las animaciones: ")
        SELECT_ANIMACIONES()
        println(ANIMACIONES.size.toString() + " animaciones cargadas")
        print("Cargando los otros interactivos: ")
        CARGAR_OTROS_INTERACTIVOS()
        println(OTROS_INTERACTIVOS.size.toString() + " otros interactivos cargados")
        print("Cargando las comidas de mascotas: ")
        println(CARGAR_COMIDAS_MASCOTAS().toString() + " comidas de mascotas cargadas")
        print("Cargando los tutoriales: ")
        CARGAR_TUTORIALES()
        println(TUTORIALES.size.toString() + " tutoriales cargados")
        print("Cargando las zonas: ")
        SELECT_ZONAS()
        println(ZONAS.size.toString() + " zonas cargados")
        println("===========> Database Dynamic <===========")
        print("Cargando los objetos: ")
        CARGAR_OBJETOS()
        println(_OBJETOS.size.toString() + " objetos cargados")
        print("Cargando los dragopavos: ")
        CARGAR_MONTURAS()
        println(_MONTURAS.size.toString() + " dragopavos cargados")
        print("Cargando los puesto mercadillos: ")
        SELECT_PUESTOS_MERCADILLOS()
        println(MERCADILLOS.size.toString() + " puestos mercadillos cargados")
        print("Cargando las cuentas: ")
        CARGAR_DB_CUENTAS()
        CARGAR_CUENTAS_SERVER_PERSONAJE()
        println(cuentas.size.toString() + " cuentas cargadas")
        print("Cargando los personajes: ")
        CARGAR_PERSONAJES()
        println(_PERSONAJES.size.toString() + " personajes cargados")
        print("Cargando los objetos mercadillos: ")
        println(SELECT_OBJETOS_MERCADILLO().toString() + " objetos mercadillos cargados")
        print("Cargando los rankings PVP: ")
        SELECT_RANKING_PVP()
        println(_RANKINGS_PVP.size.toString() + " rankings PVP cargados cargados")
        print("Cargando los prismas: ")
        CARGAR_PRISMAS()
        println(_PRISMAS.size.toString() + " prismas cargados")
        print("Cargando los rankings Koliseo: ")
        SELECT_RANKING_KOLISEO()
        println(_RANKINGS_KOLISEO.size.toString() + " rankings Koliseo cargados cargados")
        print("Cargando los gremios: ")
        CARGAR_GREMIOS()
        println(_GREMIOS.size.toString() + " gremios cargados")
        print("Cargando los miembros de gremio: ")
        println(CARGAR_MIEMBROS_GREMIO().toString() + " miembros de gremio cargados")
        print("Cargando los recaudadores: ")
        CARGAR_RECAUDADORES()
        println(_RECAUDADORES.size.toString() + " recaudadores cargados")
        print("Cargando los cercados: ")
        CARGAR_CERCADOS()
        RECARGAR_CERCADOS()
        println(CERCADOS.size.toString() + " cercados cargados")
        print("Cargando las casas: ")
        CARGAR_CASAS()
        RECARGAR_CASAS()
        println(CASAS.size.toString() + " casas cargadas")
        print("Cargando los cofres: ")
        CARGAR_COFRES()
        RECARGAR_COFRES()
        println(COFRES.size.toString() + " cofres cargados")
        print("Cargando los comandos personalizados: ")
        GestorSQL.CARGAR_COMANDOS_ACCION() // Oa los cargo
        println("${COMANDOSACCION.size} comandos personalizados cargados")
        SIG_ID_OBJETO = GET_SIG_ID_OBJETO()
        try {
            if (CUENTAS_A_BORRAR.isNotEmpty()) {
                var eliminados = 0
                Thread.sleep(100)
                for (cuenta in CUENTAS_A_BORRAR) {
                    for (perso in cuenta.personajes) {
                        if (perso == null) {
                            continue
                        }
                        cuenta.eliminarPersonaje(perso.Id)
                        eliminados++
                    }
                }
                if (eliminados > 0) {
                    redactarLogServidorln(
                        "\nSe eliminaron " + eliminados
                                + " personajes con sus objetos, dragopavos, casas\n"
                    )
                }
                Thread.sleep(100)
            }
        } catch (ignored: Exception) {
        }
        actualizarRankings()
        prepararListaGFX()
        prepararListaNivel()
        prepararPanelItems()
        listaMascotas()
        // lanzamiento del server
        setServidorEstado(Constantes.SERVIDOR_ONLINE)
    }

    @JvmStatic
    fun esZaapi(mapaID: Short, alineacion: Byte): Boolean {
        if (alineacion == Constantes.ALINEACION_BONTARIANO) {
            return !ZAAPIS_BONTA.contains(mapaID)
        } else if (alineacion == Constantes.ALINEACION_BRAKMARIANO) {
            return !ZAAPIS_BRAKMAR.contains(mapaID)
        }
        return !ZAAPIS_BONTA.contains(mapaID) && !ZAAPIS_BRAKMAR.contains(mapaID)
    }

    fun setServidorEstado(estado: Byte) {
        SERVIDOR_ESTADO = estado
        ExchangeClient.INSTANCE?.send("S$SERVIDOR_ESTADO")
    }

    private fun listaMascotas() {
        val s = StringBuilder()
        for (o in OBJETOS_MODELOS.values) {
            if (o.tipo.toInt() == Constantes.OBJETO_TIPO_MASCOTA) {
                if (s.length > 0) {
                    s.append(",")
                }
                s.append(o.id)
            }
        }
        LISTA_MASCOTAS = s.toString()
    }

    fun getServicio(id: Int): Servicio? {
        return SERVICIOS[id]
    }

    fun addServicio(servicio: Servicio) {
        SERVICIOS[servicio.id] = servicio
    }

    fun addEncarnacionModelo(encarnacion: EncarnacionModelo) {
        ENCARNACIONES_MODELOS[encarnacion.gfxID] = encarnacion
    }

    fun onlines(): CopyOnWriteArrayList<Personaje> {
        return PERSONAJESONLINE
    }

    fun mensajeCaceria(): String {
        val s = StringBuilder()
        val s2 = StringBuilder()
        val param =
            KAMAS_OBJ_CACERIA.split(Pattern.quote("|").toRegex()).toTypedArray()
        if (param.size > 1) {
            var i: Byte = 0
            for (a in param[1].split(";".toRegex()).toTypedArray()) {
                try {
                    val b = a.split(",".toRegex()).toTypedArray()
                    val stats = getObjetoModelo(b[0].toInt())!!.stringStatsModelo()
                    if (s.isNotEmpty()) {
                        s.append(", ")
                    }
                    s.append("°").append(i.toInt()).append("x").append(b[1])
                    if (s2.isNotEmpty()) {
                        s2.append("!")
                    }
                    s2.append(b[0]).append("!").append(stats)
                    i++
                } catch (ignored: Exception) {
                }
            }
        }
        s.append(", ").append(param[0]).append(" Kamas|").append(s2.toString())
        return s.toString()
    }

    fun addMision(mision: MisionModelo) {
        MISIONES_MODELOS[mision.id] = mision
    }

    @JvmStatic
    fun getMision(id: Int): MisionModelo? {
        return MISIONES_MODELOS[id]
    }

    fun addAlmanax(almanax: Almanax) {
        ALMANAX[almanax.id] = almanax
    }

    fun getAlmanax(id: Int): Almanax? {
        return ALMANAX[id]
    }

    @JvmStatic
    val almanaxDelDia: Almanax?
        get() = ALMANAX[DIA_DEL_AÑO]

    @JvmStatic
    fun getClase(clase: Int): Clase? {
        return CLASES[clase]
    }

    fun addOrnamento(ornamento: Ornamento) {
        ORNAMENTOS[ornamento.id] = ornamento
    }

    fun getOrnamento(id: Int): Ornamento? {
        return ORNAMENTOS[id]
    }

    fun addTitulo(ornamento: Titulo) {
        TITULOS[ornamento.id] = ornamento
    }

    fun getTitulo(id: Int): Titulo? {
        return TITULOS[id]
    }

    fun listarOrnamentos(perso: Personaje): String {
        val str = StringBuilder()
        for (o in ORNAMENTOS.values) {
            if (!o.esValido()) {
                continue
            }
            if (perso.tieneOrnamento(o.id)) {
                if (str.isNotEmpty()) {
                    str.append(";")
                }
                str.append(o.id)
            } else if (o.esParaVender()) {
                if (str.isNotEmpty()) {
                    str.append(";")
                }
                str.append(o.id).append(",").append(o.precioStr)
            }
        }
        return str.toString()
    }

    fun listarTitulos(perso: Personaje): String {
        val str = StringBuilder()
        for (o in TITULOS.values) {
            if (!o.esValido()) {
                continue
            }
            if (perso.tieneTitulo(o.id)) {
                if (str.isNotEmpty()) {
                    str.append(";")
                }
                str.append(o.id)
            } else if (o.esParaVender()) {
                if (str.isNotEmpty()) {
                    str.append(";")
                }
                str.append(o.id).append(",").append(o.precioStr)
            }
        }
        return str.toString()
    }

    fun addComando(comando: String, rango: Int) {
        COMANDOS[comando.toUpperCase()] = rango
    }

    fun getRangoComando(comando: String?): Int {
        return if (COMANDOS[comando] == null) {
            0
        } else COMANDOS[comando]!!
    }

    fun addEtapa(id: Int, recompensas: String, steps: String, nombre: String) {
        ETAPAS[id] = MisionEtapaModelo(id, recompensas, steps, nombre)
    }

    @JvmStatic
    fun getEtapa(id: Int): MisionEtapaModelo? {
        return ETAPAS[id]
    }

    fun addDropFijo(drop: DropMob) {
        DROPS_FIJOS.add(drop)
    }

    fun listaDropsFijos(): ArrayList<DropMob> {
        return DROPS_FIJOS
    }

    fun addMisionObjetivoModelo(id: Int, tipo: Byte, args: String) {
        OBJETIVOS_MODELOS[id] = MisionObjetivoModelo(id, tipo, args)
    }

    @JvmStatic
    fun getMisionObjetivoModelo(id: Int): MisionObjetivoModelo? {
        return OBJETIVOS_MODELOS[id]
    }

    fun prepararListaGFX() {
        val str = StringBuilder()
        for (obj in OBJETOS_MODELOS.values) {
            if (obj.gFX <= 0) {
                continue
            }
            if (str.isNotEmpty()) {
                str.append(";")
            }
            str.append(obj.id).append(",").append(obj.gFX)
        }
        LISTA_GFX = str.toString()
    }

    fun prepararListaNivel() {
        val str = StringBuilder()
        for (obj in OBJETOS_MODELOS.values) {
            if (!obj.nivelModifi) {
                continue
            }
            if (str.isNotEmpty()) {
                str.append(";")
            }
            str.append(obj.id).append(",").append(obj.nivel.toInt())
        }
        LISTA_NIVEL = str.toString()
    }

    fun addMobEvento(evento: Byte, mobOriginal: Int, mobEvento: Int) {
        MOBS_EVENTOS.computeIfAbsent(
            evento
        ) { k: Byte? -> ArrayList() }
        MOBS_EVENTOS[evento]!!.add(Duo(mobOriginal, mobEvento))
    }

    val mobsEventoDelDia: ArrayList<Duo<Int, Int>>?
        get() = MOBS_EVENTOS[MOB_EVENTO]

    //	public static void refrescarTodosMobs_con_estrellas(int estrellas) {
//		for (final Mapa mapa : MAPAS.values()) {
//			if (mapa.getGrupoMobsTotales().size()>0 && mapa.getArrayPersonajes().size()>0){
//				mapa.refrescarGrupoMobs_con_estrellas(estrellas);
//			}
//		}
//	}
    fun refrescarTodosMobs() {
        for (mapa in MAPAS.values) {
            if (mapa!!.grupoMobsTotales!!.isNotEmpty()) {
                try {
                    mapa.refrescarGrupoMobs()
                } catch (e: Exception) {
                    println("Mapa id: " + mapa.id + " Con problemas para refrescar mobs")
                }
            }
        }
    }

    fun resetearStatsObjetos(idsModelo: ArrayList<Int>) {
        val objetos = ArrayList<Objeto>()
        for (obj in _OBJETOS.values) {
            if (idsModelo.contains(obj.objModeloID)) {
                objetos.add(obj)
                obj.objModelo?.generarStatsModelo(CAPACIDAD_STATS.MAXIMO)?.let { obj.convertirStringAStats(it) }
            }
        }
        SALVAR_OBJETOS(objetos)
    }

    fun moverMobs() {
        for (mapa in MAPAS.values) {
            if (!mapa!!.arrayPersonajes!!.isEmpty() && !mapa.grupoMobsTotales!!.isEmpty()) {
                mapa.moverGrupoMobs(AtlantaMain.CANTIDAD_GRUPO_MOBS_MOVER_POR_MAPA)
            }
        }
        //        Mundo.pequeñosalvar(false);
    }

    fun AleatorizarMobs() {
        if (AtlantaMain.RATE_RANDOM_MOB != 1.0) {
            for (mob in MOBS_MODELOS.values) {
                if (!mob.listaNiveles().equals("1, 2, 3, 4, 5, 6", ignoreCase = true)) {
                    mob.Aleatorizarstats()
                }
            }
        } else {
            for (mob in MOBS_MODELOS.values) {
                mob.NormalizarStats()
            }
        }
    }

    fun NormalizarMobs() {
        if (AtlantaMain.RATE_RANDOM_MOB == 1.0) {
            for (mob in MOBS_MODELOS.values) {
                mob.NormalizarStats()
            }
        }
    }

    private fun rankingNivel() {
        if (!AtlantaMain.PARAM_LADDER_NIVEL) return
        //		for (Personaje a :
//				_PERSONAJES.values()) {
//			if (a.getCuenta().getAdmin()==0){
//				persos.add(a);
//			}
//		}
        val persos = ArrayList(_PERSONAJES.values)
        persos.sortWith(CompNivelMasMenos())
        _LADDER_NIVEL.clear()
        _LADDER_NIVEL.addAll(persos)
    }

    private fun rankingDia() {
        if (!AtlantaMain.PARAM_LADDER_EXP_DIA) return
        val persos = ArrayList(_PERSONAJES.values)
        persos.sortWith(CompDiaMasMenos())
        _LADDER_EXP_DIA.clear()
        _LADDER_EXP_DIA.addAll(persos)
    }

    private fun rankingGremio() {
        if (!AtlantaMain.PARAM_LADDER_GREMIO) return
        val persos = ArrayList(_GREMIOS.values)
        persos.sortWith(CompGremioMasMenos())
        _LADDER_GREMIO.clear()
        _LADDER_GREMIO.addAll(persos)
    }

    private fun rankingPVP() {
        if (!AtlantaMain.PARAM_LADDER_PVP) {
            return
        }
        val persos = ArrayList(_RANKINGS_PVP.values)
        persos.sortWith(CompPVPMasMenos())
        _LADDER_PVP.clear()
        _LADDER_PVP.addAll(persos)
    }

    private fun rankingKoliseo() {
        if (!AtlantaMain.PARAM_LADDER_KOLISEO) {
            return
        }
        val persos =
            ArrayList(_RANKINGS_KOLISEO.values)
        persos.sortWith(CompKoliseoMasMenos())
        _LADDER_KOLISEO.clear()
        _LADDER_KOLISEO.addAll(persos)
    }

    private fun addPaginas(temp: StringBuilder, inicio: Int, add: Int) {
        temp.append("|").append(if (inicio == -1) 0 else 1).append("|")
            .append(if (add == AtlantaMain.LIMITE_LADDER + 1) 1 else 0)
    }

    private fun addStringParaLadder(
        temp: StringBuilder,
        perso: Personaje,
        pos: Int,
        ladderpvp: Boolean = false,
        ladderxpdia: Boolean = false
    ) {
        if (temp.isNotEmpty()) {
            temp.append("#")
        }
        temp.append(getStringParaLadder(perso, pos, ladderpvp, ladderxpdia))
    }

    private fun getStringParaLadder(
        perso: Personaje,
        pos: Int,
        ladderpvp: Boolean = false,
        ladderxpdia: Boolean = false
    ): String {
        return if (ladderpvp) {
            (pos.toString() + ";" + perso.getGfxID(false) + ";" + perso.nombre + ";" + perso.getTitulo(false) + ";" + perso
                .nivel + ";" + "[${rankingpj(perso.Id)?.victorias}]\t\t[${rankingpj(perso.Id)?.derrotas}]" + ";" + (if (perso.enLinea()) if (perso.pelea != null) 2 else 1 else 0) + ";"
                    + perso.alineacion)
        } else if (ladderxpdia) {
            (pos.toString() + ";" + perso.getGfxID(false) + ";" + perso.nombre + ";" + perso.getTitulo(false) + ";" + perso
                .nivel + ";" + perso.experienciaDia + ";" + (if (perso.enLinea()) if (perso.pelea != null) 2 else 1 else 0) + ";"
                    + perso.alineacion)
        } else {
            (pos.toString() + ";" + perso.getGfxID(false) + ";" + perso.nombre + ";" + perso.getTitulo(false) + ";" + perso
                .nivel + ";" + perso.experiencia + ";" + (if (perso.enLinea()) if (perso.pelea != null) 2 else 1 else 0) + ";"
                    + perso.alineacion)
        }
    }

    private fun strStaffOnline(out: Personaje, buscar: String, iniciarEn: Int) {
        if (!AtlantaMain.PARAM_LADDER_STAFF) {
            return
        }
        var pos = 0
        var add = 0
        var inicio = 0
        val temp = StringBuilder()
        for (perso in PERSONAJESONLINE) {
            try {
                if (add > AtlantaMain.LIMITE_LADDER) {
                    break
                }
                if (perso.esIndetectable()) {
                    continue
                }
                if (perso.cuenta.admin <= 0) {
                    continue
                }
                pos++
                if (!buscar.isEmpty()) {
                    if (!perso.nombre.toUpperCase().contains(buscar)) {
                        continue
                    }
                }
                if (inicio == 0) {
                    inicio = pos
                }
                if (pos < iniciarEn) {
                    continue
                }
                if (pos == inicio) {
                    inicio = -1
                }
                if (add < AtlantaMain.LIMITE_LADDER) {
                    addStringParaLadder(temp, perso, pos)
                }
                add++
            } catch (ignored: Exception) {
            }
        }
        addPaginas(temp, inicio, add)
        ENVIAR_bl_RANKING_DATA(out, "STAFF", temp.toString())
    }

    private fun strRankingNivel(out: Personaje, buscar: String, iniciarEn: Int) {
        if (!AtlantaMain.PARAM_LADDER_NIVEL) {
            return
        }
        var pos = 0
        var add = 0
        var inicio = 0
        val temp = StringBuilder()
        for (perso in _LADDER_NIVEL) {
            try {
                if (add > AtlantaMain.LIMITE_LADDER) {
                    break
                }
                if (perso.esIndetectable()) {
                    continue
                }
                if (!AtlantaMain.PARAM_PERMITIR_ADMIN_EN_LADDER) {
                    if (perso.cuenta.admin > 0) {
                        continue
                    }
                }
                pos++
                if (!buscar.isEmpty()) {
                    if (!perso.nombre.toUpperCase().contains(buscar)) {
                        continue
                    }
                }
                if (inicio == 0) {
                    inicio = pos
                }
                if (pos < iniciarEn) {
                    continue
                }
                if (pos == inicio) {
                    inicio = -1
                }
                if (add < AtlantaMain.LIMITE_LADDER) {
                    addStringParaLadder(temp, perso, pos)
                }
                add++
            } catch (ignored: Exception) {
            }
        }
        addPaginas(temp, inicio, add)
        ENVIAR_bl_RANKING_DATA(out, "NIVEL", temp.toString())
    }

    private fun strRankingDia(out: Personaje, buscar: String, iniciarEn: Int) {
        if (!AtlantaMain.PARAM_LADDER_EXP_DIA) {
            return
        }
        var pos = 0
        var add = 0
        var inicio = 0
        val temp = StringBuilder()
        for (perso in _LADDER_EXP_DIA) {
            try {
                if (perso.experienciaDia == 0.toLong()) {
                    continue
                }
                if (add > AtlantaMain.LIMITE_LADDER) {
                    break
                }
                if (perso.esIndetectable()) {
                    continue
                }
                if (!AtlantaMain.PARAM_PERMITIR_ADMIN_EN_LADDER) {
                    if (perso.cuenta.admin > 0) {
                        continue
                    }
                }
                pos++
                if (buscar.isNotEmpty()) {
                    if (!perso.nombre.toUpperCase().contains(buscar)) {
                        continue
                    }
                }
                if (inicio == 0) {
                    inicio = pos
                }
                if (pos < iniciarEn) {
                    continue
                }
                if (pos == inicio) {
                    inicio = -1
                }
                if (add < AtlantaMain.LIMITE_LADDER) {
                    addStringParaLadder(temp, perso, pos, false, true)
                }
                add++
            } catch (ignored: Exception) {
            }
        }
        if (pos == 0) addStringParaLadder(temp, out, 1, false, true) // Para evitar que salga vacio
        addPaginas(temp, inicio, add)
        ENVIAR_bl_RANKING_DATA(out, "DIA", temp.toString())
    }

    private fun strRankingPVP(out: Personaje, buscar: String, iniciarEn: Int) {
        if (!AtlantaMain.PARAM_LADDER_PVP) {
            return
        }
        var pos = 0
        var add = 0
        var inicio = 0
        val temp = StringBuilder()
        for (rank in _LADDER_PVP) {
            try {
                if (add > AtlantaMain.LIMITE_LADDER) {
                    break
                }
                val perso = getPersonaje(rank.id) ?: continue
                if (perso.esIndetectable()) {
                    continue
                }
                if (!AtlantaMain.PARAM_PERMITIR_ADMIN_EN_LADDER) {
                    if (perso.cuenta.admin > 0) {
                        continue
                    }
                }
                pos++
                if (!buscar.isEmpty()) {
                    if (!perso.nombre.toUpperCase().contains(buscar)) {
                        continue
                    }
                }
                if (inicio == 0) {
                    inicio = pos
                }
                if (pos < iniciarEn) {
                    continue
                }
                if (pos == inicio) {
                    inicio = -1
                }
                if (add < AtlantaMain.LIMITE_LADDER) {
                    addStringParaLadder(temp, perso, pos, true)
                }
                add++
            } catch (ignored: Exception) {
            }
        }
        addPaginas(temp, inicio, add)
        ENVIAR_bl_RANKING_DATA(out, "PVP", temp.toString())
    }

    fun rankingpj(id: Int): RankingPVP? {
        for (rank in _LADDER_PVP) {
            if (id == rank.id) return rank
        }
        return null
    }

    private fun strRankingKoliseo(out: Personaje, buscar: String, iniciarEn: Int) {
        if (!AtlantaMain.PARAM_LADDER_KOLISEO) {
            return
        }
        var pos = 0
        var add = 0
        var inicio = 0
        val temp = StringBuilder()
        for (rank in _LADDER_KOLISEO) {
            try {
                if (add > AtlantaMain.LIMITE_LADDER) {
                    break
                }
                val perso = getPersonaje(rank.id) ?: continue
                if (perso.esIndetectable()) {
                    continue
                }
                if (!AtlantaMain.PARAM_PERMITIR_ADMIN_EN_LADDER) {
                    if (perso.cuenta.admin > 0) {
                        continue
                    }
                }
                pos++
                if (!buscar.isEmpty()) {
                    if (!perso.nombre.toUpperCase().contains(buscar)) {
                        continue
                    }
                }
                if (inicio == 0) {
                    inicio = pos
                }
                if (pos < iniciarEn) {
                    continue
                }
                if (pos == inicio) {
                    inicio = -1
                }
                if (add < AtlantaMain.LIMITE_LADDER) {
                    addStringParaLadder(temp, perso, pos)
                }
                add++
            } catch (ignored: Exception) {
            }
        }
        addPaginas(temp, inicio, add)
        ENVIAR_bl_RANKING_DATA(out, "KOLISEO", temp.toString())
    }

    private fun strRankingGremio(out: Personaje, buscar: String, iniciarEn: Int) {
        if (!AtlantaMain.PARAM_LADDER_GREMIO) {
            return
        }
        var pos = 0
        var add = 0
        var inicio = 0
        val temp = StringBuilder()
        for (gremio in _LADDER_GREMIO) {
            try {
                if (add > AtlantaMain.LIMITE_LADDER) {
                    break
                }
                pos++
                if (!buscar.isEmpty()) {
                    if (!gremio.nombre.toUpperCase().contains(buscar)) {
                        continue
                    }
                }
                if (inicio == 0) {
                    inicio = pos
                }
                if (pos < iniciarEn) {
                    continue
                }
                if (pos == inicio) {
                    inicio = -1
                }
                if (add < AtlantaMain.LIMITE_LADDER) {
                    if (temp.length > 0) {
                        temp.append("#")
                    }
                    temp.append(pos).append(";").append(gremio.emblema).append(";").append(gremio.nombre)
                        .append(";").append(gremio.cantidadMiembros).append(";").append(gremio.nivel.toInt())
                        .append(";").append(gremio.experiencia).append(";;;")
                }
                add++
            } catch (ignored: Exception) {
            }
        }
        addPaginas(temp, inicio, add)
        ENVIAR_bl_RANKING_DATA(out, "GREMIO", temp.toString())
    }

    fun nombreLiderRankingPVP(): String {
        var nombre = ""
        if (_RANKINGS_PVP.size <= 0) {
            return nombre
        }
        var vict = 0
        var derr = 0
        for (rank in _RANKINGS_PVP.values) {
            if (rank.victorias > vict) {
                nombre = rank.nombre
                vict = rank.victorias
                derr = rank.derrotas
            } else {
                if (rank.victorias != vict || rank.derrotas > derr) {
                    continue
                }
                nombre = rank.nombre
                vict = rank.victorias
                derr = rank.derrotas
            }
        }
        return nombre
    }

    // public static void actualizarLiderPVP() {
// final String antiguoLider = _liderRanking;
// final Personaje liderViejo = getPersonajePorNombre(antiguoLider);
// if (liderViejo != null) {
// liderViejo.setTitulo((byte) 0);
// }
// GestorSQL.ACTUALIZAR_TITULO_POR_NOMBRE(antiguoLider);
// final Personaje perso = getPersonajePorNombre(nombreLiderRankingPVP());
// if (perso != null) {
// perso.setTitulo((byte) 8);
// getNPCModelo(1350).modificarNPC( perso.getSexo(), perso.getGfxID(false), perso.getColor1(),
// perso.getColor2(),
// perso.getColor3());
// }
// _liderRanking = nombreLiderRankingPVP();
// }
    fun rankingsPermitidos(): String {
        val temp = StringBuilder()
        if (AtlantaMain.PARAM_LADDER_NIVEL) {
            if (temp.length > 0) {
                temp.append("|")
            }
            temp.append("Nivel")
        }
        if (AtlantaMain.PARAM_LADDER_PVP) {
            if (temp.length > 0) {
                temp.append("|")
            }
            temp.append("PVP")
        }
        if (AtlantaMain.PARAM_LADDER_GREMIO) {
            if (temp.length > 0) {
                temp.append("|")
            }
            temp.append("Gremio")
        }
        if (AtlantaMain.PARAM_LADDER_KOLISEO) {
            if (temp.length > 0) {
                temp.append("|")
            }
            temp.append("Koliseo")
        }
        if (AtlantaMain.PARAM_LADDER_EXP_DIA) {
            if (temp.length > 0) {
                temp.append("|")
            }
            temp.append("DiaXP")
        }
        if (AtlantaMain.PARAM_LADDER_STAFF) {
            if (temp.length > 0) {
                temp.append("|")
            }
            temp.append("Staff")
        }
        return temp.toString()
    }

    fun enviarRanking(perso: Personaje, param: String?, buscar: String, iniciarEn: Int) {
        when (param) {
            "NIVEL" -> strRankingNivel(perso, buscar, iniciarEn)
            "PVP" -> strRankingPVP(perso, buscar, iniciarEn)
            "DIA" -> strRankingDia(perso, buscar, iniciarEn)
            "STAFF" -> strStaffOnline(perso, buscar, iniciarEn)
            "KOLISEO" -> strRankingKoliseo(perso, buscar, iniciarEn)
            "GREMIO" -> strRankingGremio(perso, buscar, iniciarEn)
            else -> {
                ENVIAR_BN_NADA(perso)
                return
            }
        }
    }

    fun actualizarRankings() {
        rankingNivel()
        rankingPVP()
        rankingGremio()
        rankingKoliseo()
        rankingDia()
    }

    fun misBoletos(persoID: Int): String {
        if (!VENDER_BOLETOS) {
            return ""
        }
        val str = StringBuilder()
        for (a in 1..LOTERIA_BOLETOS.size) {
            if (LOTERIA_BOLETOS[a - 1] != persoID) {
                continue
            }
            if (str.length > 0) {
                str.append(", ")
            }
            str.append(a)
        }
        return str.toString()
    }

    fun devolverBoletos() {
        for (loteriaBoleto in LOTERIA_BOLETOS) {
            try {
                if (loteriaBoleto == 0) {
                    continue
                }
                val perso = getPersonaje(loteriaBoleto)
                if (AtlantaMain.PARAM_LOTERIA_OGRINAS) {
                    val idCuenta = perso!!.cuentaID
                    SET_OGRINAS_CUENTA(
                        AtlantaMain.PRECIO_LOTERIA + GET_OGRINAS_CUENTA(idCuenta),
                        idCuenta
                    )
                } else {
                    perso!!.addKamas(AtlantaMain.PRECIO_LOTERIA.toLong(), false, true)
                }
            } catch (ignored: Exception) {
            }
        }
    }

    @Synchronized
    fun comprarLoteria(packet: String, perso: Personaje) {
        if (!VENDER_BOLETOS) {
            ENVIAR_Im_INFORMACION(perso, "1DONT_TIME_BUY_LOTERIE")
            return
        }
        var boleto = 1
        try {
            boleto = packet.substring(3).toInt()
        } catch (ignored: Exception) {
        }
        if (boleto < 1) {
            boleto = 1
        } else if (boleto > LOTERIA_BOLETOS.size) {
            boleto = LOTERIA_BOLETOS.size
        }
        if (boleto > 9999) {
            ENVIAR_Im_INFORMACION(perso, "1NUMBER_LOTERIE_INCORRECT")
            return
        }
        if (LOTERIA_BOLETOS[boleto - 1] != 0) {
            ENVIAR_Im_INFORMACION(perso, "1NUMBER_LOTERIE_OCCUPED")
            return
        }
        if (AtlantaMain.PARAM_LOTERIA_OGRINAS) {
            if (RESTAR_OGRINAS(perso.cuenta, AtlantaMain.PRECIO_LOTERIA.toLong(), perso)) {
                LOTERIA_BOLETOS[boleto - 1] = perso.Id
            }
        } else {
            if (perso.kamas >= AtlantaMain.PRECIO_LOTERIA) {
                perso.addKamas((-AtlantaMain.PRECIO_LOTERIA).toLong(), true, true)
                LOTERIA_BOLETOS[boleto - 1] = perso.Id
                ENVIAR_Im_INFORMACION(perso, "1TU_BOLETO;$boleto")
            } else {
                ENVIAR_Im_INFORMACION(perso, "182")
            }
        }
    }

    fun iniciarLoteria() {
        if (!AtlantaMain.PARAM_LOTERIA) {
            return
        }
        if (MSJ_CUENTA_REGRESIVA.equals(
                "RESET RATES",
                ignoreCase = true
            ) || MSJ_CUENTA_REGRESIVA.equals("LOTERIA", ignoreCase = true)
        ) {
            return
        }
        MSJ_CUENTA_REGRESIVA = "LOTERIA"
        SEG_CUENTA_REGRESIVA = 1800
        VENDER_BOLETOS = true
        ENVIAR_bRI_INICIAR_CUENTA_REGRESIVA_TODOS()
    }

    fun sortearBoletos() {
        if (!VENDER_BOLETOS) {
            return
        }
        VENDER_BOLETOS = false
        val lista = ArrayList<Int>()
        for (x in 1..LOTERIA_BOLETOS.size) {
            if (LOTERIA_BOLETOS[x - 1] != 0) {
                lista.add(x)
            }
        }
        if (lista.size < 10) {
            SEG_CUENTA_REGRESIVA = 600
            MSJ_CUENTA_REGRESIVA = "LOTERIA"
            VENDER_BOLETOS = true
            ENVIAR_ÑL_BOTON_LOTERIA_TODOS(true)
            ENVIAR_bRI_INICIAR_CUENTA_REGRESIVA_TODOS()
            ENVIAR_Im_INFORMACION_A_TODOS("1PLUS_TIME_SORTEO")
            return
        }
        ENVIAR_Im_INFORMACION_A_TODOS("1SORTEO_LOTERIE")
        try {
            Thread.sleep(10000)
        } catch (ignored: Exception) {
        }
        var premios = 1
        premios += lista.size / AtlantaMain.GANADORES_POR_BOLETOS
        val ganadores: MutableMap<Int, Int> = TreeMap()
        for (a in 0 until premios) {
            val boleto = lista[getRandomInt(0, lista.size - 1)]
            ganadores[boleto] = LOTERIA_BOLETOS[boleto - 1]
            lista.remove(boleto)
        }
        var b = 1
        for ((key, value) in ganadores) {
            val perso = getPersonaje(value) ?: continue
            val idCuenta = perso.cuentaID
            ENVIAR_Im_INFORMACION_A_TODOS(
                "1NUMBER_WIN_LOTERIE;$b~($key) - " + perso
                    .nombre
            )
            if (AtlantaMain.PARAM_LOTERIA_OGRINAS) {
                ADD_OGRINAS_CUENTA(AtlantaMain.PREMIO_LOTERIA.toLong(), idCuenta)
            } else {
                perso.addKamas(AtlantaMain.PREMIO_LOTERIA.toLong(), true, true)
            }
            try {
                Thread.sleep(2000)
            } catch (ignored: InterruptedException) {
            }
            b++
        }
        ENVIAR_Im_INFORMACION_A_TODOS("1FINISH_LOTERIE")
        LOTERIA_BOLETOS = IntArray(10000)
    }

    fun resetExpDia() {
        for (perso in _PERSONAJES.values) {
            perso.resetExpDia()
        }
    }

    fun moverMonturas() {
        for (cercado in CERCADOS.values) {
            cercado.startMoverMontura()
        }
    }

    fun moverRecaudadores() {
        for (recauador in _RECAUDADORES.values) {
            recauador.puedeMoverRecaudador()
        }
    }

    // public static void embarazoMonturas() {
// for (final Montura montura : Monturas.values()) {
// montura.aumentarTiempoFecundacion();
// if (montura.getUbicacion() != Ubicacion.ESTABLO) {
// continue;
// }
// montura.disminuirFatiga();
// }
// }
    fun disminuirFatigaMonturas() {
        for (montura in _MONTURAS.values) {
            if (montura.ubicacion != Ubicacion.ESTABLO) {
                continue
            }
            montura.disminuirFatiga()
        }
    }

    fun checkearObjInteractivos() {
        for (oi in OBJETOS_INTERACTIVOS) {
            oi.recargando(false)
            oi.subirEstrella()
        }
    }

    fun expulsarInactivos() {
        for (ss in clientes) {
            try {
                if (ss.tiempoUltPacket + AtlantaMain.SEGUNDOS_INACTIVIDAD * 1000 < System.currentTimeMillis()) {
                    ss.registrar("<===> EXPULSAR POR INACTIVIDAD!!!")
                    ENVIAR_M0_MENSAJE_BASICOS_SVR_MUESTRA_DISCONNECT(ss, "1", "", "")
                    ss.cerrarSocket(true, "expulsarInactivos()")
                }
            } catch (ignored: Exception) {
            }
        }
    }

    fun lanzarPublicidad(str: String?) {
        ENVIAR_Im1223_MENSAJE_IMBORRABLE_TODOS(str!!)
    }

    fun salvarMapasEstrellas() {
        if (AtlantaMain.MODO_DEBUG) {
            return
        }
        CANT_SALVANDO = 0
        redactarLogServidor("Salvando las estrellas de los mobs: ")
        //        GestorSQL.VACIAR_MAPAS_ESTRELLAS();
        val declaracion =
            GET_STATEMENT_SQL_DINAMICA("REPLACE INTO `mapas_estrellas` VALUES (?,?);")
        for (mapa in MAPAS.values) {
            try {
                if (mapa!!.grupoMobsTotales!!.isEmpty()) {
                    continue
                }
                val s = StringBuilder()
                for (gm in mapa.grupoMobsTotales!!.values) {
                    if (gm.realBonusEstrellas() <= 0) {
                        continue
                    }
                    if (s.length > 0) {
                        s.append(",")
                    }
                    s.append(gm.realBonusEstrellas())
                }
                if (s.length == 0) {
                    continue
                }
                CANT_SALVANDO++
                REPLACE_MAPAS_ESTRELLAS_BATCH(declaracion!!, mapa.id.toInt(), s.toString())
            } catch (e: Exception) {
                redactarLogServidorln(e.toString())
            }
        }
        if (CANT_SALVANDO > 0) {
            ejecutarBatch(Objects.requireNonNull(declaracion)!!)
        }
        redactarLogServidorln("Finalizo con $CANT_SALVANDO")
        TOTAL_SALVADO += CANT_SALVANDO
    }

    fun finalizarPeleas() {
        for (mapa in MAPAS.values) {
            try {
                if (mapa == null) {
                    continue
                }
                if (mapa.peleas == null) {
                    continue
                }
                if (mapa.peleas?.isEmpty() != false) {
                    continue
                }
                while (mapa.peleas?.values?.isEmpty() != true) {
                    try {
                        for (pelea in mapa.peleas?.values ?: break) {
                            try {
                                if (pelea.tipoPelea == Constantes.PELEA_TIPO_PVM.toInt() || pelea.tipoPelea == Constantes.PELEA_TIPO_PVM_NO_ESPADA.toInt()) {
                                    pelea.acaboPelea(2.toByte())
                                } else {
                                    pelea.cancelarPelea()
                                }
                            } catch (e: Exception) {
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            } catch (e: Exception) {
                redactarLogServidorln("EXCEPTION finalizarPeleas $e")
                e.printStackTrace()
            }
        }
    }

    fun cancelarPeleas() {
        for (mapa in MAPAS.values) {
            try {
                if (mapa!!.peleas!!.isEmpty()) {
                    continue
                }
                while (!mapa.peleas!!.values.isEmpty()) {
                    try {
                        for (pelea in mapa.peleas!!.values) {
                            try {
                                pelea.cancelarPelea()
                            } catch (e: Exception) {
                                break
                            }
                        }
                    } catch (e: Exception) {
                        redactarLogServidorln("EXCEPTION finalizarPeleas $e")
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                redactarLogServidorln("EXCEPTION finalizarPeleas $e")
                e.printStackTrace()
            }
        }
    }

    fun pequeñosalvar(inclusoOffline: Boolean) {
        val xxxx = System.currentTimeMillis()
        if (!SALVANDO) {
            redactarLogServidorln("---------------- INICIO PEQUEÑO SALVADO ----------------")
            //            GestorSQL.cerrarConexion();
//            GestorSQL.iniciarConexion();
//			System.out.println("Tiempo de reconexion de bdd: " + (System.currentTimeMillis() - xxxx));
            SALVANDO = true
            TOTAL_SALVADO = 0
            try {
                var CANT_SALVANDO = 0
                redactarLogServidorln("Salvando los personajes y sus cuentas: ")
                thread {
                    for (perso in _PERSONAJES.values) {
                        try {
                            if (perso == null || perso.cuenta == null) {
                                continue
                            }
                            if ((perso.enLinea() || inclusoOffline) && perso.pelea == null) {
                                if (SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
                                    perso.previosDesconectar()
                                }
                                perso.salvar()
                                REPLACE_CUENTA_SERVIDOR(
                                    perso.cuenta,
                                    GET_PRIMERA_VEZ(perso.cuenta.nombre)
                                )
                                redactarLogServidorln(" [ONLINE] " + " 100%")
                                CANT_SALVANDO++
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    redactarLogServidorln("Finalizó con $CANT_SALVANDO")
                    TOTAL_SALVADO += CANT_SALVANDO
                }
                thread {
                    var CANT_SALVANDO = 0
                    redactarLogServidor("Salvando los gremios: ")
                    for (gremio in _GREMIOS.values) {
                        try {
                            REPLACE_GREMIO(gremio)
                            CANT_SALVANDO++
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    redactarLogServidorln("Finalizó con $CANT_SALVANDO")
                    TOTAL_SALVADO += CANT_SALVANDO
                }
                thread {
                    var CANT_SALVANDO = 0
                    redactarLogServidor("Salvando los cercados: ")
                    for (cercado in CERCADOS.values) {
                        try {
                            if (cercado.dueñoID != 0 || cercado.gremio != null || !cercado.criando.isEmpty()) {
                                REPLACE_CERCADO(cercado)
                                CANT_SALVANDO++
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    redactarLogServidorln("Finalizó con $CANT_SALVANDO")
                    TOTAL_SALVADO += CANT_SALVANDO
                }
                thread {
                    var CANT_SALVANDO = 0
                    redactarLogServidor("Salvando las monturas: ")
                    for (montura in _MONTURAS.values) {
                        try {
                            if (montura.estaCriando()) {
                                REPLACE_MONTURA(montura, true)
                                CANT_SALVANDO++
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    redactarLogServidorln("Finalizó con $CANT_SALVANDO")
                    TOTAL_SALVADO += CANT_SALVANDO
                }
                thread {
                    var CANT_SALVANDO = 0
                    redactarLogServidor("Salvando las casas: ")
                    for (casa in CASAS.values) {
                        try {
                            if (casa.dueño != null) {
                                REPLACE_CASA(casa)
                                CANT_SALVANDO++
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    redactarLogServidorln("Finalizó con $CANT_SALVANDO")
                    TOTAL_SALVADO += CANT_SALVANDO
                }
                thread {
                    var CANT_SALVANDO = 0
                    redactarLogServidor("Salvando los cofres: ")
                    for (cofre in COFRES.values) {
                        try {
                            if (cofre.dueñoID != 0) {
                                REPLACE_COFRE(cofre, true)
                                CANT_SALVANDO++
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    redactarLogServidorln("Finalizó con $CANT_SALVANDO")
                    TOTAL_SALVADO += CANT_SALVANDO
                }
                //                CANT_SALVANDO = 0;
//                AtlantaMain.redactarLogServidor("Salvando las cuentas: ");
//                for (final Cuenta cuenta : _CUENTAS.values()) {
//                    try {
//                        if (cuenta.enLinea() || inclusoOffline) {
//                            GestorSQL.REPLACE_CUENTA_SERVIDOR(cuenta, GestorSQL.GET_PRIMERA_VEZ(cuenta.getNombre()));
//                            CANT_SALVANDO++;
//                        }
//                    } catch (final Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                AtlantaMain.redactarLogServidorln("Finalizó con " + CANT_SALVANDO);
//                TOTAL_SALVADO += CANT_SALVANDO
                SALVANDO = false
                redactarLogServidorln("------------ Pequeño salvado ejecutado por completo 100% ------------")
                println(TOTAL_SALVADO.toString() + " Datos Guardados ----> en " + (System.currentTimeMillis() - xxxx) + " Milisegundos <------")
                val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                val date = Date()
                println("Fecha exacta de realización: " + dateFormat.format(date))
                println("---------------------------------- Fin Salvado pequeño ---------------------------------")
            } catch (e: Exception) {
                redactarLogServidorln("------------ Error al salvar : $e")
                e.printStackTrace()
            }
        }
        GestorSQL.CARGAR_COMANDOS_ACCION() // Oa como la wea elimina la lista cada vez que carga, los puse en los guardados del servidor
    }

    fun salvarServidor(inclusoOffline: Boolean) {
        while (SALVANDO) {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        val xxxx = System.currentTimeMillis()
        redactarLogServidorln("---------------- INICIO SALVADO ----------------")
        SALVANDO = true
        redactarLogServidorln("Se invoco el metodo salvar Servidor (MUNDO DOFUS) ")
        if (SERVIDOR_ESTADO != Constantes.SERVIDOR_OFFLINE) {
            setServidorEstado(Constantes.SERVIDOR_SALVANDO)
        }
        if (!AtlantaMain.PARAM_AUTO_COMMIT) {
            timerCommit(false)
            iniciarCommit(true)
        }
        redactarLogServidor("Iniciando salvado de registros JUGADORES Y SQL ... ")
        imprimirLogPlayers()
        redactarLogServidorln("100%")
        TOTAL_SALVADO = 0
        try {
            redactarLogServidor("Salvando Kamas de la Ruleta de Jalato")
            modificarParam("KAMAS_RULETA_JALATO", AtlantaMain.KAMAS_RULETA_JALATO.toString() + "")
            // PERSONAJES
            thread {
                var CANT_SALVANDO = 0
                redactarLogServidorln("Salvando los personajes: ")
                for (perso in _PERSONAJES.values) {
                    try {
                        if (perso == null || perso.cuenta == null) {
                            continue
                        }
                        if (perso.enLinea() || inclusoOffline) {
                            redactarLogServidor(" -> Salvando a " + perso.nombre + " ... ") // Ecatome
                            if (SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
                                perso.previosDesconectar()
                            }
                            SALVAR_PERSONAJE(perso, true)
                            redactarLogServidorln(" [ONLINE] " + " 100%")
                            CANT_SALVANDO++
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                redactarLogServidorln("Finalizó con $CANT_SALVANDO")
                TOTAL_SALVADO += CANT_SALVANDO
            }
            thread {
                var CANT_SALVANDO = 0
                redactarLogServidorln("Salvando los mercantes: ")
                for (perso in _PERSONAJES.values) {
                    try {
                        if (perso == null || perso.cuenta == null || inclusoOffline) {
                            continue
                        }
                        if (perso.esMercante()) {
                            redactarLogServidor(" -> Salvando a " + perso.nombre + " ... ") // Ecatome
                            SALVAR_PERSONAJE(perso, true)
                            redactarLogServidorln(" [MERCANTE] " + " 100%")
                            CANT_SALVANDO++
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                redactarLogServidorln("Finalizó con $CANT_SALVANDO")
                TOTAL_SALVADO += CANT_SALVANDO
            }
            thread {
                var CANT_SALVANDO = 0
                redactarLogServidor("Salvando los prismas: ")
                for (prisma in _PRISMAS.values) {
                    try {
                        REPLACE_PRISMA(prisma)
                        CANT_SALVANDO++
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                redactarLogServidorln("Finalizó con $CANT_SALVANDO")
                TOTAL_SALVADO += CANT_SALVANDO
            }
            thread {
                var CANT_SALVANDO = 0
                redactarLogServidor("Salvando los gremios: ")
                for (gremio in _GREMIOS.values) {
                    try {
                        REPLACE_GREMIO(gremio)
                        CANT_SALVANDO++
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                redactarLogServidorln("Finalizó con $CANT_SALVANDO")
                TOTAL_SALVADO += CANT_SALVANDO
            }
            thread {
                var CANT_SALVANDO = 0
                redactarLogServidor("Salvando los recaudadores: ")
                for (recau in _RECAUDADORES.values) {
                    try {
                        if (recau.gremio == null) {
                            continue
                        }
                        REPLACE_RECAUDADOR(recau, true)
                        CANT_SALVANDO++
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                redactarLogServidorln("Finalizó con $CANT_SALVANDO")
                TOTAL_SALVADO += CANT_SALVANDO
            }
            thread {
                var CANT_SALVANDO = 0
                redactarLogServidor("Salvando los cercados: ")
                for (cercado in CERCADOS.values) {
                    try {
                        REPLACE_CERCADO(cercado)
                        CANT_SALVANDO++
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                redactarLogServidorln("Finalizó con $CANT_SALVANDO")
                TOTAL_SALVADO += CANT_SALVANDO
            }
            thread {
                var CANT_SALVANDO = 0
                redactarLogServidor("Salvando las monturas: ")
                for (montura in _MONTURAS.values) {
                    try {
                        if (montura.estaCriando()) {
                            REPLACE_MONTURA(montura, false)
                            CANT_SALVANDO++
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                redactarLogServidorln("Finalizó con $CANT_SALVANDO")
                TOTAL_SALVADO += CANT_SALVANDO
            }
            thread {
                var CANT_SALVANDO = 0
                redactarLogServidor("Salvando las casas: ")
                for (casa in CASAS.values) {
                    try {
                        REPLACE_CASA(casa)
                        CANT_SALVANDO++
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                redactarLogServidorln("Finalizó con $CANT_SALVANDO")
                TOTAL_SALVADO += CANT_SALVANDO
            }
            thread {
                var CANT_SALVANDO = 0
                redactarLogServidor("Salvando los cofres: ")
                for (cofre in COFRES.values) {
                    try {
                        if (cofre.dueñoID != 0) {
                            REPLACE_COFRE(cofre, true)
                            CANT_SALVANDO++
                        } else if (!cofre.objetos.isEmpty()) {
                            cofre.limpiarCofre()
                            println("El cofre: " + cofre.iD + " Fue vaciado")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                redactarLogServidorln("Finalizó con $CANT_SALVANDO")
                TOTAL_SALVADO += CANT_SALVANDO
            }
            thread {
                var CANT_SALVANDO = 0
                redactarLogServidor("Salvando los rankings PVP: ")
                for (rank in _RANKINGS_PVP.values) {
                    try {
                        REPLACE_RANKING_PVP(rank)
                        CANT_SALVANDO++
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                redactarLogServidorln("Finalizó con $CANT_SALVANDO")
                TOTAL_SALVADO += CANT_SALVANDO
            }
            thread {
                var CANT_SALVANDO = 0
                redactarLogServidor("Salvando los rankings Koliseo: ")
                for (rank in _RANKINGS_KOLISEO.values) {
                    try {
                        REPLACE_RANKING_KOLISEO(rank)
                        CANT_SALVANDO++
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                redactarLogServidorln("Finalizó con $CANT_SALVANDO")
                TOTAL_SALVADO += CANT_SALVANDO
            }
            thread {
                var CANT_SALVANDO = 0
                redactarLogServidor("Salvando las cuentas: ")
                for (cuenta in cuentas.values) {
                    try {
                        if (cuenta.enLinea() || inclusoOffline) {
                            REPLACE_CUENTA_SERVIDOR(cuenta, GET_PRIMERA_VEZ(cuenta.nombre))
                            CANT_SALVANDO++
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                for (mapa in MAPAS.values) {
                    mapa!!.limpiarobjetostirados()
                }
                redactarLogServidorln("Finalizó con $CANT_SALVANDO")
                TOTAL_SALVADO += CANT_SALVANDO
            }
            redactarLogServidorln("------------ Se salvó exitosamente el servidor 100% ------------")
            println(TOTAL_SALVADO.toString() + " Datos Guardados ----> en " + (System.currentTimeMillis() - xxxx) + " Milisegundos <------")
            val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val date = Date()
            println("Fecha exacta de realización: " + dateFormat.format(date))
            //            System.gc();
            println("---------------------------------- Fin Salvado General ---------------------------------")
        } catch (e: ConcurrentModificationException) {
            redactarLogServidorln("------------ Ocurrio un error de concurrent $e")
            e.printStackTrace()
        } catch (e: Exception) {
            redactarLogServidorln("------------ Error al salvar : $e")
            e.printStackTrace()
        } finally {
            if (!AtlantaMain.PARAM_AUTO_COMMIT) {
                iniciarCommit(true)
                timerCommit(true)
            }
            if (SERVIDOR_ESTADO != Constantes.SERVIDOR_OFFLINE) {
                setServidorEstado(Constantes.SERVIDOR_ONLINE)
            }
            if (AtlantaMain.URL_BACKUP_PHP.isNotEmpty()) {
                try {
                    if (!AtlantaMain.PARAM_AUTO_COMMIT) {
                        Thread.sleep(20000)
                    }
                    redactarLogServidorln("REALIZANDO BACKUP SQL DEL SERVIDOR")
                    consultaWeb(AtlantaMain.URL_BACKUP_PHP)
                    redactarLogServidorln("BACKUP SQL REALIZADO CON EXITO")
                } catch (e: Exception) {
                    redactarLogServidorln("ERROR AL REALIZAR BACKUP SQL")
                    e.printStackTrace()
                }
            }
            imprimirLogCombates()
            SALVANDO = false
        }
        GestorSQL.CARGAR_COMANDOS_ACCION()
    }

    @JvmStatic
    fun getCofresPorCasa(casa: Casa): ArrayList<Cofre> {
        val cofres = ArrayList<Cofre>()
        for (cofre in COFRES.values) {
            if (cofre.casaID == casa.id) {
                cofres.add(cofre)
            }
        }
        return cofres
    }

    @JvmStatic
    fun getCofrePorUbicacion(mapaID: Short, celdaID: Short): Cofre? {
        for (cofre in COFRES.values) {
            if (cofre.mapaID == mapaID && cofre.celdaID == celdaID) {
                return cofre
            }
        }
        return null
    }

    @JvmStatic
    fun borrarLasCuentas(minutos: Long): Boolean {
        return if (!AtlantaMain.PARAM_BORRAR_CUENTAS_VIEJAS) {
            false
        } else MINUTOS_VIDA_REAL - minutos > AtlantaMain.DIAS_PARA_BORRAR * 24 * 60
        // se convierte a minutos para comparar 2 meses
    }

    fun usoMemoria() {
        println("======== FreeMemory: " + Runtime.getRuntime().freeMemory() / 1048576f + " MB ========")
    }

    fun getCantCercadosGremio(id: Int): Byte {
        var i: Byte = 0
        for (cercado in CERCADOS.values) {
            if (cercado.gremio != null && cercado.gremio!!.id == id) {
                i++
            }
        }
        return i
    }

    // private static void salvarMapasHeroico() {
// _cantSalvado = 0;
// Bustemu.redactarLogServidor("Salvando los objetos heroicos: ");
// GestorSQL.VACIAR_MAPAS_HEROICO();
// for (final Mapa mapa : Mapas.values()) {
// if (mapa.getGrupoMobsTotales().isEmpty() && mapa.getGrupoMobsHeroicos().isEmpty()) {
// continue;
// }
// StringBuilder mobs = new StringBuilder();
// StringBuilder objetos = new StringBuilder();
// StringBuilder kamas = new StringBuilder();
// ArrayList<GrupoMob> grupos = new ArrayList<>();
// grupos.addAll(mapa.getGrupoMobsTotales().values());
// grupos.addAll(mapa.getGrupoMobsHeroicos());
// boolean paso = false;
// for (GrupoMob g : grupos) {
// if (g.getKamasHeroico() <= 0 && g.cantObjHeroico() == 0) {
// continue;
// }
// if (paso) {
// mobs.append("|");
// objetos.append("|");
// kamas.append("|");
// }
// mobs.append(g.getStrGrupoMob());
// objetos.append(g.getIDsObjeto());
// kamas.append(g.getKamasHeroico());
// paso = true;
// }
// if (!paso) {
// continue;
// }
// _cantSalvado++;
// GestorSQL.REPLACE_MAPAS_HEROICO(mapa.getID(), mobs.toString(), objetos.toString(),
// kamas.toString());
// }
// Bustemu.redactarLogServidorln("Finalizo con " + _cantSalvado);
// _totalSalvado += _cantSalvado;
// }
    fun addMapaEstrellas(id: Short, estrellas: String) {
        try {
            val array = ArrayList<Short>()
            for (s in estrellas.split(",".toRegex()).toTypedArray()) {
                array.add(s.toShort())
            }
            MAPAS_ESTRELLAS[id] = array
        } catch (ignored: Exception) {
        }
    }

    fun addMapaHeroico(id: Short, mobs: String, objetos: String, kamas: String) {
        try {
            val array = ArrayList<String>()
            val m = mobs.split(Pattern.quote("|").toRegex()).toTypedArray()
            val o =
                objetos.split(Pattern.quote("|").toRegex()).toTypedArray()
            val k =
                kamas.split(Pattern.quote("|").toRegex()).toTypedArray()
            for (i in m.indices) {
                array.add(m[i] + "|" + o[i] + "|" + k[i])
            }
            MAPAS_HEROICOS[id] = array
        } catch (ignored: Exception) {
        }
    }

    fun getMapaHeroico(id: Short): ArrayList<String>? {
        return MAPAS_HEROICOS[id]
    }

    fun getMapaEstrellas(id: Short): ArrayList<Short>? {
        return MAPAS_ESTRELLAS[id]
    }

    @JvmStatic
    fun getArea(area: Int): Area? {
        return AREAS[area]
    }

    @JvmStatic
    fun getSubArea(subArea: Int): SubArea? {
        return SUB_AREAS[subArea]
    }

    fun getSuperArea(superArea: Int): SuperArea? {
        return SUPER_AREAS[superArea]
    }

    fun addArea(area: Area) {
        AREAS[area.id] = area
    }

    fun addSubArea(subArea: SubArea) {
        SUB_AREAS[subArea.id] = subArea
    }

    fun addSuperArea(superArea: SuperArea) {
        SUPER_AREAS[superArea.id] = superArea
    }

    fun addExpNivel(nivel: Int, exp: Experiencia) { // if (nivel > Bustemu.NIVEL_MAX_PERSONAJE) {
// return;
// }
        EXPERIENCIA[nivel] = exp
    }

    @JvmStatic
    fun getCreaTuItem(id: Int): CreaTuItem? {
        return CREA_TU_ITEM[id]
    }

    @JvmStatic
    fun getCuenta(id: Int): Cuenta? {
        return cuentas[id]
    }

    fun addRespuestaNPC(respuesta: RespuestaNPC) {
        NPC_RESPUESTAS[respuesta.id] = respuesta
    }

    fun getRespuestaNPC(id: Int): RespuestaNPC? {
        return NPC_RESPUESTAS[id]
    }

    fun addPreguntaNPC(pregunta: PreguntaNPC) {
        NPC_PREGUNTAS[pregunta.id] = pregunta
    }

    fun getPreguntaNPC(id: Int): PreguntaNPC? {
        return NPC_PREGUNTAS[id]
    }

    @JvmStatic
    fun getNPCModelo(id: Int): NPCModelo? {
        return NPC_MODELOS[id]
    }

    fun addNPCModelo(npcModelo: NPCModelo) {
        NPC_MODELOS[npcModelo.id] = npcModelo
    }

    @JvmStatic
    fun getMapa(id: Short): Mapa? {
        return MAPAS[id]
    }

    fun addMapa(mapa: Mapa) {
        if (!MAPAS.containsKey(mapa.id)) {
            MAPAS[mapa.id] = mapa
        }
    }

    fun mapaExiste(mapa: Short): Boolean {
        return MAPAS.containsKey(mapa)
    }

    fun mapaPorCoordXYContinente(mapaX: Int, mapaY: Int, idContinente: Int): Mapa? {
        try {
            for (mapa in MAPAS.values) {
                if (mapa!!.x.toInt() == mapaX && mapa.y.toInt() == mapaY && mapa.subArea!!.area.superArea!!.id == idContinente
                ) {
                    return mapa
                }
            }
        } catch (e: Exception) {
            redactarLogServidorln(e.toString())
        }
        return null
    }

    fun mapaPorCoordenadas(mapaX: Int, mapaY: Int, idContinente: Int): String {
        val str = StringBuilder()
        try {
            for (mapa in MAPAS.values) {
                if (mapa!!.x.toInt() == mapaX && mapa.y.toInt() == mapaY && mapa.subArea!!.area.superArea!!.id == idContinente
                ) {
                    str.append(mapa.id.toInt()).append(", ")
                }
            }
        } catch (e: Exception) {
            redactarLogServidorln(e.toString())
        }
        return str.toString()
    }

    fun subirEstrellasMobs(cant: Int) {
        for (mapa in MAPAS.values) {
            try {
                mapa!!.subirEstrellasMobs(cant)
                //			if (mapa.getArrayPersonajes().size()>0){
//                mapa.RefrescarGM_Mobs_Enmapa();
//            }
            } catch (e: Exception) {
                redactarLogServidorln(e.toString())
            }
        }
    }

    fun subirEstrellasOI(cant: Int) {
        for (mapa in MAPAS.values) {
            mapa!!.subirEstrellasOI(cant)
        }
    }

    fun getCuentaPorApodo(apodo: String): Cuenta? {
        for (cuenta in cuentas.values) {
            if (cuenta.apodo == apodo) {
                return cuenta
            }
        }
        return null
    }

    fun strCuentasOnline(): String {
        val str = StringBuilder()
        for (perso in PERSONAJESONLINE) {
            if (str.length > 0) {
                str.append(",")
            }
            str.append(perso.cuentaID)
        }
        return str.toString()
    }

    fun getCuentaPorNombre(nombre: String): Cuenta? {
        return if (_CUENTAS_POR_NOMBRE[nombre.toLowerCase()] != null) cuentas[_CUENTAS_POR_NOMBRE[nombre.toLowerCase()]!!] else null
    }

    fun addCuenta(cuenta: Cuenta) {
        cuentas[cuenta.id] = cuenta
        _CUENTAS_POR_NOMBRE[cuenta.nombre.toLowerCase()] = cuenta.id
    }

    @JvmStatic
    fun addPersonaje(perso: Personaje) {
        if (perso.Id > SIG_ID_PERSONAJE) {
            SIG_ID_PERSONAJE = perso.Id
        }
        _PERSONAJES[perso.Id] = perso
    }

    @JvmStatic
    fun getPersonaje(id: Int): Personaje? {
        return _PERSONAJES[id]
    }

    val cantidadPersonajes: Int
        get() = _PERSONAJES.size

    @JvmStatic
    fun getPersonajePorNombre(nombre: String?): Personaje? {
        val Ps = ArrayList(_PERSONAJES.values)
        for (perso in Ps) {
            if (perso.nombre.equals(nombre, ignoreCase = true)) {
                return perso
            }
        }
        return null
    }

    fun getCasaPorUbicacion(mapaID: Short, celdaID: Int): Casa? {
        for (casa in CASAS.values) {
            if (casa.mapaIDFuera == mapaID && casa.celdaIDFuera.toInt() == celdaID) {
                return casa
            }
        }
        return null
    }

    fun cargarPropiedadesCasa(perso: Personaje) {
        for (casa in CASAS.values) {
            try {
                if (casa.mapaIDFuera == perso.mapa.id) {
                    ENVIAR_hP_PROPIEDADES_CASA(perso, casa.propiedadesPuertaCasa(perso))
                    ENVIAR_hL_INFO_CASA(perso, casa.informacionCasa(perso.Id))
                    Thread.sleep(5)
                }
            } catch (ignored: InterruptedException) {
            }
        }
    }

    @JvmStatic
    fun cantCasasGremio(gremioID: Int): Byte {
        var i: Byte = 0
        for (casa in CASAS.values) {
            if (casa.gremioID == gremioID) {
                i++
            }
        }
        return i
    }

    @JvmStatic
    fun getCasaDePj(persoID: Int): Casa? {
        for (casa in CASAS.values) {
            if (casa.esSuCasa(persoID)) {
                return casa
            }
        }
        return null
    }

    fun borrarCasaGremio(gremioID: Int) {
        for (casa in CASAS.values) {
            if (casa.gremioID == gremioID) {
                casa.nullearGremio()
                casa.actualizarDerechos(0)
            }
        }
    }

    @JvmStatic
    fun getCasaDentroPorMapa(mapaID: Short): Casa? {
        for (casa in CASAS.values) {
            if (casa.mapasContenidos.contains(mapaID)) {
                return casa
            }
        }
        return null
    }

    // if (!subarea.getConquistable()) {
// continue;
// }
    val alineacionTodasSubareas: String
        get() {
            val str = StringBuilder()
            for (subarea in SUB_AREAS.values) { // if (!subarea.getConquistable()) {
// continue;
// }
                if (str.length > 0) {
                    str.append("|")
                }
                str.append(subarea.id).append(";").append(subarea.alineacion.toInt())
            }
            return str.toString()
        }

    // public static long getExpMinPersonaje(int nivel) {
// if (nivel > Bustemu.NIVEL_MAX_PERSONAJE) {
// nivel = Bustemu.NIVEL_MAX_PERSONAJE;
// } else if (nivel < 1) {
// nivel = 1;
// }
// return Experiencia.get(nivel)._personaje;
// }
//
// public static long getExpMaxPersonaje(int nivel) {
// if (nivel >= Bustemu.NIVEL_MAX_PERSONAJE) {
// nivel = Bustemu.NIVEL_MAX_PERSONAJE - 1;
// } else if (nivel <= 1) {
// nivel = 1;
// }
// return Experiencia.get(nivel + 1)._personaje;
// }
//
// public static long getExpMaxEncarnacion(int nivel) {
// if (nivel >= Bustemu.NIVEL_MAX_ENCARNACION) {
// nivel = Bustemu.NIVEL_MAX_ENCARNACION - 1;
// } else if (nivel <= 1) {
// nivel = 1;
// }
// return Experiencia.get(nivel + 1)._encarnacion;
// }
//
// public static long getExpMaxGremio(int nivel) {
// if (nivel >= Bustemu.NIVEL_MAX_GREMIO) {
// nivel = Bustemu.NIVEL_MAX_GREMIO - 1;
// } else if (nivel <= 1) {
// nivel = 1;
// }
// return Experiencia.get(nivel + 1)._gremio;
// }
    fun getExpCazaCabezas(nivel: Int): Long {
        var nivel = nivel
        if (nivel >= AtlantaMain.NIVEL_MAX_PERSONAJE) {
            nivel = AtlantaMain.NIVEL_MAX_PERSONAJE - 1
        } else if (nivel < 1) {
            nivel = 1
        }
        if (AtlantaMain.PARAM_EXP_PVP_MISION_POR_TABLA) {
            var exp = 0
            if (nivel < 60) {
                exp = 65000
            } else if (nivel < 70) {
                exp = 90000
            } else if (nivel < 80) {
                exp = 120000
            } else if (nivel < 90) {
                exp = 160000
            } else if (nivel < 100) {
                exp = 210000
            } else if (nivel < 110) {
                exp = 270000
            } else if (nivel < 120) {
                exp = 350000
            } else if (nivel < 130) {
                exp = 440000
            } else if (nivel < 140) {
                exp = 540000
            } else if (nivel < 150) {
                exp = 650000
            } else if (nivel < 155) {
                exp = 760000
            } else if (nivel < 160) {
                exp = 880000
            } else if (nivel < 165) {
                exp = 1000000
            } else if (nivel < 170) {
                exp = 1130000
            } else if (nivel < 175) {
                exp = 1300000
            } else if (nivel < 180) {
                exp = 1000000
            } else if (nivel < 185) {
                exp = 1700000
            } else if (nivel < 190) {
                exp = 2000000
            } else if (nivel < 195) {
                exp = 2000000
            } else if (nivel <= 200) {
                exp = 3000000
            }
            return exp.toLong()
        }
        return EXPERIENCIA[nivel]!!._personaje / 20
    }

    @JvmStatic
    fun getExpPersonaje(nivel: Int): Long {
        var nivel = nivel
        if (nivel > AtlantaMain.NIVEL_MAX_PERSONAJE) {
            return Long.MAX_VALUE
        } else if (nivel < 1) {
            nivel = 1
        }
        return EXPERIENCIA[nivel]!!._personaje
    }

    @JvmStatic
    fun getExpGremio(nivel: Int): Long {
        var nivel = nivel
        if (nivel > AtlantaMain.NIVEL_MAX_GREMIO) {
            nivel = AtlantaMain.NIVEL_MAX_GREMIO
        } else if (nivel < 1) {
            nivel = 1
        }
        return EXPERIENCIA[nivel]!!._gremio
    }

    @JvmStatic
    fun getExpMontura(nivel: Int): Long {
        var nivel = nivel
        if (nivel > AtlantaMain.NIVEL_MAX_MONTURA) {
            nivel = AtlantaMain.NIVEL_MAX_MONTURA
        } else if (nivel < 1) {
            nivel = 1
        }
        return EXPERIENCIA[nivel]!!._montura.toLong()
    }

    @JvmStatic
    fun getExpEncarnacion(nivel: Int): Long {
        var nivel = nivel
        if (nivel > AtlantaMain.NIVEL_MAX_ENCARNACION) {
            nivel = AtlantaMain.NIVEL_MAX_ENCARNACION
        } else if (nivel < 1) {
            nivel = 1
        }
        return EXPERIENCIA[nivel]!!._encarnacion
    }

    fun getExpOficio(nivel: Int): Int {
        var nivel = nivel
        if (nivel > AtlantaMain.NIVEL_MAX_OFICIO) {
            nivel = AtlantaMain.NIVEL_MAX_OFICIO
        } else if (nivel < 1) {
            nivel = 1
        }
        return EXPERIENCIA[nivel]!!._oficio
    }

    @JvmStatic
    fun getExpAlineacion(nivel: Int): Int {
        var nivel = nivel
        if (nivel > AtlantaMain.NIVEL_MAX_ALINEACION) {
            nivel = AtlantaMain.NIVEL_MAX_ALINEACION
        } else if (nivel < 1) {
            nivel = 1
        }
        return EXPERIENCIA[nivel]!!._alineacion
    }

    fun getExpParaNivelAlineacion(nivel: Int): Int {
        var nivel = nivel
        if (nivel > AtlantaMain.NIVEL_MAX_ALINEACION) {
            nivel = AtlantaMain.NIVEL_MAX_ALINEACION
        } else if (nivel < 2) {
            nivel = 2
        }
        return EXPERIENCIA[nivel]!!._alineacion - EXPERIENCIA[nivel - 1]!!._alineacion
    }

    // public static Experiencia getExpNivel(final int nivel) {
// return Experiencia.get(nivel);
// }
    fun getObjInteractivoModelo(id: Int): ObjetoInteractivoModelo {
        return objInteractivos[id]
    }

    fun getObjIntModeloPorGfx(gfx: Int): ObjetoInteractivoModelo? {
        for (oi in objInteractivos) {
            if (oi.gfxs.contains(gfx)) {
                return oi
            }
        }
        return null
    }

    fun addObjInteractivo(oi: ObjetoInteractivo) {
        OBJETOS_INTERACTIVOS.add(oi)
    }

    fun addObjInteractivoModelo(OIM: ObjetoInteractivoModelo) {
        objInteractivos.add(OIM)
    }

    @JvmStatic
    fun getOficio(id: Int): Oficio? {
        return OFICIOS[id]
    }

    fun addOficio(oficio: Oficio) {
        OFICIOS[oficio.id] = oficio
    }

    fun addReceta(id: Int, arrayDuos: ArrayList<Duo<Int, Int>>) {
        RECETAS[id] = arrayDuos
    }

    fun getReceta(id: Int): ArrayList<Duo<Int, Int>>? {
        return RECETAS[id]
    }

    fun esIngredienteDeReceta(id: Int): Boolean {
        for (a in RECETAS.values) {
            for (d in a) {
                if (d._primero == id) return true
            }
        }
        return false
    }

    fun getIDRecetaPorIngredientes(
        listaIDRecetas: ArrayList<Int>?,
        ingredientes: MutableMap<Int, Int?>
    ): Int {
        if (listaIDRecetas == null) {
            return -1
        }
        for (id in listaIDRecetas) {
            val receta: ArrayList<Duo<Int, Int>>? = RECETAS[id]
            if (receta == null || receta.size != ingredientes.size) {
                continue
            }
            var ok = true
            for (ing in receta) {
                if (ingredientes[ing._primero] == null) {
                    ok = false
                    break
                }
                val primera = ingredientes[ing._primero]!!
                val segunda = ing._segundo
                if (primera != segunda) {
                    ok = false
                    break
                }
            }
            if (ok) {
                return id
            }
        }
        return -1
    }

    fun addObjetoSet(objetoSet: ObjetoSet) {
        OBJETOS_SETS[objetoSet.iD] = objetoSet
    }

    @JvmStatic
    fun getObjetoSet(id: Int): ObjetoSet? {
        return OBJETOS_SETS[id]
    }

    val numeroObjetoSet: Int
        get() = OBJETOS_SETS.size

    @JvmStatic
    fun sigIDPersonaje(): Int {
        return ++SIG_ID_PERSONAJE
    }

    // public static int sigIDCofre() {
// return ++sigIDCofre;
// }
    @Synchronized
    fun sigIDObjeto(): Int {
        return ++SIG_ID_OBJETO
    }

    @JvmStatic
    @Synchronized
    fun sigIDLineaMercadillo(): Int {
        return ++SIG_ID_LINEA_MERCADILLO
    }

    fun sigIDRecaudador(): Int {
        SIG_ID_RECAUDADOR -= 3
        return SIG_ID_RECAUDADOR
    }

    @JvmStatic
    @Synchronized
    fun sigIDMontura(): Int {
        SIG_ID_MONTURA -= 3
        return SIG_ID_MONTURA
    }

    @Synchronized
    fun sigIDPrisma(): Int {
        SIG_ID_PRISMA -= 3
        return SIG_ID_PRISMA
    }

    @JvmStatic
    @Synchronized
    fun sigIDGremio(): Int {
        if (_GREMIOS.isEmpty()) {
            return 1
        }
        var n = 0
        for ((x) in _GREMIOS) {
            if (n < x) {
                n = x
            }
        }
        return n + 1
    }

    fun addGremio(gremio: Gremio) {
        _GREMIOS[gremio.id] = gremio
    }

    @JvmStatic
    @Synchronized
    fun nombreGremioUsado(nombre: String?): Boolean {
        try {
            for (gremio in _GREMIOS.values) {
                if (gremio.nombre.equals(nombre, ignoreCase = true)) {
                    return true
                }
            }
        } catch (e: Exception) {
            return true
        }
        return false
    }

    @JvmStatic
    @Synchronized
    fun emblemaGremioUsado(emblema: String): Boolean {
        for (gremio in _GREMIOS.values) {
            if (gremio.emblema == emblema) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun getGremio(i: Int): Gremio? {
        return _GREMIOS[i]
    }

    fun addZaap(mapa: Short, celda: Short) {
        ZAAPS[mapa] = celda
    }

    @JvmStatic
    fun getCeldaZaapPorMapaID(mapaID: Short): Short {
        try {
            if (ZAAPS[mapaID] != null) {
                return ZAAPS[mapaID]!!
            }
        } catch (ignored: Exception) {
        }
        return -1
    }

    fun getCeldaCercadoPorMapaID(mapaID: Short): Short {
        val cercado = getMapa(mapaID)!!.cercado
        return if (cercado != null && cercado.celdaID > 0) {
            cercado.celdaID
        } else -1
    }

    @JvmStatic
    fun eliminarMontura(montura: Montura?) {
        montura!!.ubicacion = Ubicacion.NULL
        _MONTURAS.remove(montura.id)
        DELETE_MONTURA(montura)
        val objetos = ArrayList(montura.objetos)
        eliminarObjetosPorArray(objetos)
    }

    @JvmStatic
    @Synchronized
    fun eliminarPersonaje(perso: Personaje, totalmente: Boolean) { // perso.getObjetos().clear();
        if (perso.esMercante()) {
            perso.mapa.removerMercante(perso.Id)
            ENVIAR_GM_BORRAR_GM_A_MAPA(perso.mapa, perso.Id)
        }
        if (totalmente) {
            if (perso.montura != null) {
                eliminarMontura(perso.montura)
            }
            val casa = getCasaDePj(perso.Id)
            casa?.resetear()
            for (cercado in CERCADOS.values) {
                if (cercado.dueñoID == perso.Id) {
                    val criando =
                        cercado.strPavosCriando().split(";".toRegex()).toTypedArray()
                    for (pavo in criando) {
                        try {
                            eliminarMontura(getMontura(pavo.toInt()))
                        } catch (ignored: Exception) {
                        }
                    }
                    if (cercado.strPavosCriando().length > 0) {
                        DELETE_DRAGOPAVO_LISTA(cercado.strPavosCriando().replace(";".toRegex(), ","))
                    }
                    cercado.resetear()
                    DELETE_CERCADO(cercado.mapa!!.id.toInt())
                }
            }
            if (perso.miembroGremio != null) {
                val gremio = perso.gremio
                if (gremio.cantidadMiembros <= 1 || perso.miembroGremio.rango == 1) {
                    eliminarGremio(gremio)
                }
                gremio.expulsarMiembro(perso.Id)
            }
            delRankingPVP(perso.Id)
            delRankingKoliseo(perso.Id)
            val esposo = getPersonaje(perso.esposoID)
            if (esposo != null) {
                esposo.divorciar()
                perso.divorciar()
            }
            val objetos = ArrayList(perso.objetosTodos)
            eliminarObjetosPorArray(objetos)
            objetos.clear()
            objetos.addAll(perso.objetosTienda)
            eliminarObjetosPorArray(objetos)
            DELETE_PERSONAJE(perso)
            _PERSONAJES.remove(perso.Id)
        }
        redactarLogServidorln(
            "SE ELIMINO EL PERSONAJE " + perso.nombre + " (" + perso.Id
                    + ") PERTENICIENTE A LA CUENTA " + perso.cuentaID
        )
    }

    fun eliminarGremio(gremio: Gremio) {
        gremio.eliminarTodosRecaudadores()
        gremio.expulsarTodosMiembros()
        borrarCasaGremio(gremio.id)
        DELETE_GREMIO(gremio.id)
        _GREMIOS.remove(gremio.id)
        // gremio.destruir();
    }

    fun cuentasIP(ip: String): Int {
        var veces = 0
        for (c in cuentas.values) {
            if (!c.enLinea()) {
                continue
            }
            if (c.actualIP == ip) {
                veces++
            }
        }
        return veces
    }

    @JvmStatic
    fun addOnline(perso: Personaje) {
        if (!PERSONAJESONLINE.contains(perso)) PERSONAJESONLINE.add(perso)
    }

    @JvmStatic
    fun removeOnline(perso: Personaje?) {
        PERSONAJESONLINE.remove(perso)
    }

    fun addHechizo(hechizo: Hechizo) {
        HECHIZOS[hechizo.iD] = hechizo
    }

    fun addObjModelo(objMod: ObjetoModelo) {
        OBJETOS_MODELOS[objMod.id] = objMod
    }

    private fun prepararPanelItems() {
        for (tipo in 1..199) {
            val add = StringBuilder()
            for (objMod in OBJETOS_MODELOS.values) {
                if (objMod.tipo.toInt() != tipo) {
                    continue
                }
                if (AtlantaMain.SISTEMA_ITEMS_TIPO_DE_PAGO.equals("KAMAS", ignoreCase = true)) {
                    if (objMod.precioPanelKamas <= 0) {
                        continue
                    }
                } else {
                    if (objMod.precioPanelOgrinas <= 0) {
                        continue
                    }
                }
                if (add.length > 0) {
                    add.append("|")
                }
                add.append(objMod.id).append(";")
                add.append(objMod.stringStatsModelo()).append(";")
                if (AtlantaMain.SISTEMA_ITEMS_TIPO_DE_PAGO.equals("KAMAS", ignoreCase = true)) {
                    add.append(objMod.precioPanelKamas)
                } else {
                    add.append(objMod.precioPanelOgrinas)
                }
                add.append(";")
                if (AtlantaMain.PARAM_SISTEMA_ITEMS_SOLO_PERFECTO) {
                    add.append("1") // solo perfecto
                } else {
                    add.append("0")
                    if (AtlantaMain.PARAM_SISTEMA_ITEMS_EXO_PA_PM) {
                        if (!AtlantaMain.SISTEMA_ITEMS_EXO_TIPOS_NO_PERMITIDOS.contains(objMod.tipo)) {
                            add.append(";").append(if (objMod.tieneStatInicial(Constantes.STAT_MAS_PA)) "0" else "1")
                                .append(";").append(
                                    if (objMod
                                            .tieneStatInicial(Constantes.STAT_MAS_PM)
                                    ) "0" else "1"
                                )
                        }
                    }
                }
            }
            if (add.length > 0) {
                SISTEMA_ITEMS[tipo.toShort()] = add.toString()
            }
        }
    }

    val tiposPanelItems: String
        get() {
            val str = StringBuilder()
            for (s in SISTEMA_ITEMS.keys) {
                if (str.length > 0) {
                    str.append(";")
                }
                str.append(s.toInt())
            }
            return str.toString()
        }

    fun getObjetosPorTipo(out: Personaje?, tipo: Short) {
        if (SISTEMA_ITEMS[tipo] == null) {
            for (s in SISTEMA_ITEMS.keys) {
                ENVIAR_bSO_PANEL_ITEMS_OBJETOS_POR_TIPO(
                    out!!,
                    s.toString() + "@" + SISTEMA_ITEMS[s]
                )
            }
            ENVIAR_bSO_PANEL_ITEMS_OBJETOS_POR_TIPO(out!!, "-1@")
        } else {
            ENVIAR_bSO_PANEL_ITEMS_OBJETOS_POR_TIPO(
                out!!,
                tipo.toString() + "@" + SISTEMA_ITEMS[tipo]
            )
        }
    }

    @JvmStatic
    fun getHechizo(id: Int): Hechizo? {
        return HECHIZOS[id]
    }

    @JvmStatic
    fun getObjetoModelo(id: Int): ObjetoModelo? {
        return OBJETOS_MODELOS[id]
    }

    fun addMobModelo(mob: MobModelo) {
        MOBS_MODELOS[mob.id] = mob
    }

    @JvmStatic
    fun getMobModelo(id: Int): MobModelo? {
        return MOBS_MODELOS[id]
    }

    @JvmStatic
    fun getMonturaModelo(id: Int): MonturaModelo? {
        return MONTURAS_MODELOS[id]
    }

    fun addMonturaModelo(montura: MonturaModelo) {
        MONTURAS_MODELOS[montura.colorID] = montura
    }

    fun objetoIniciarServer(
        id: Int, idObjModelo: Int, cant: Int, pos: Byte,
        strStats: String?, idObvi: Int, precio: Int
    ) {
        if (getObjetoModelo(idObjModelo) == null) {
            redactarLogServidorln(
                "La id del objeto " + id + " esta bug porque no tiene objModelo "
                        + idObjModelo
            )
            if (!AtlantaMain.PARAM_DESHABILITAR_SQL) {
                DELETE_OBJETO(id)
            }
            return
        }
        val obj = Objeto(id, idObjModelo, cant, pos, strStats ?: "", idObvi, precio, true)
        if (AtlantaMain.PARAM_RESETEAR_LUPEAR_OBJETOS_MAGUEADOS) {
            when (obj.objModelo?.tipo?.toInt()) {
                Constantes.OBJETO_TIPO_AMULETO, Constantes.OBJETO_TIPO_ANILLO, Constantes.OBJETO_TIPO_CINTURON, Constantes.OBJETO_TIPO_BOTAS, Constantes.OBJETO_TIPO_SOMBRERO, Constantes.OBJETO_TIPO_CAPA, Constantes.OBJETO_TIPO_BASTON, Constantes.OBJETO_TIPO_HACHA, Constantes.OBJETO_TIPO_PALA, Constantes.OBJETO_TIPO_ESPADA, Constantes.OBJETO_TIPO_ARCO, Constantes.OBJETO_TIPO_MARTILLO, Constantes.OBJETO_TIPO_GUADAÑA, Constantes.OBJETO_TIPO_DAGAS -> {
                    obj.objModelo?.generarStatsModelo(CAPACIDAD_STATS.MAXIMO)?.let { obj.convertirStringAStats(it) }
                    // obj._reseteado = true;
                    SALVAR_OBJETO(obj)
                }
            }
        }
        _OBJETOS[id] = obj
    }

    @JvmStatic
    @Synchronized
    fun addObjeto(obj: Objeto, salvarSQL: Boolean) {
        try {
            if (obj.id == 0) {
                obj.id = sigIDObjeto()
            }
            _OBJETOS[obj.id] = obj
            if (SERVIDOR_ESTADO != Constantes.SERVIDOR_OFFLINE) {
                if (salvarSQL || obj.objModelo?.tipo?.toInt() == Constantes.OBJETO_TIPO_OBJEVIVO) {
                    SALVAR_OBJETO(obj)
                }
            } else if (AtlantaMain.PARAM_RESET_STATS_OBJETO) {
                obj.objModelo?.generarStatsModelo(CAPACIDAD_STATS.MAXIMO)?.let { obj.convertirStringAStats(it) }
                SALVAR_OBJETO(obj)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getObjeto(id: Int): Objeto? {
        return _OBJETOS[id]
    }

    @JvmStatic
    fun eliminarObjeto(id: Int) {
        if (id == 0) {
            return
        }
        DELETE_OBJETO(id)
        _OBJETOS.remove(id)
    }

    fun ObjIdenticoPerso(objeto: Objeto?, prohibido: Objeto?, perso: Personaje?): Objeto? {
        if (perso == null || objeto === null) {
            return null
        }
        if (objeto.puedeTenerStatsIguales()) {
            for (obj in perso.Objetos.values) {
                if (obj.posicion != Constantes.OBJETO_POS_NO_EQUIPADO && Constantes.OBJETO_POS_EQUIPADOS.contains(obj.posicion)) {
                    continue
                }
                if (objeto.id == obj.id) {
                    continue
                }
                if (prohibido != null && prohibido.id == obj.id) {
                    continue
                }
                if (obj.objModeloID == objeto.objModeloID && obj.sonStatsIguales(objeto)) {
                    return obj
                }
            }
        }
        return null
    }

    private fun eliminarObjetosPorArray(objetos: ArrayList<Objeto?>) {
        if (objetos.isEmpty()) {
            return
        }
        val str = StringBuilder()
        for (obj in objetos) {
            if (obj != null) {
                str.append(if (str.isNotEmpty()) "," else "").append(obj.id)
                _OBJETOS.remove(obj.id)
            }
        }
        DELETE_OBJETOS_LISTA(str.toString())
    }

    @JvmStatic
    fun getMontura(id: Int): Montura? {
        return _MONTURAS[id]
    }

    @JvmStatic
    @Synchronized
    fun addMontura(montura: Montura, agregar: Boolean) {
        if (montura.id < SIG_ID_MONTURA) {
            SIG_ID_MONTURA = montura.id
        }
        _MONTURAS[montura.id] = montura
        if (agregar) {
            REPLACE_MONTURA(montura, false)
        }
    }

    @Synchronized
    fun addPuestoMercadillo(mercadillo: Mercadillo) {
        MERCADILLOS[mercadillo.iD] = mercadillo
    }

    fun getPuestoMercadillo(id: Int): Mercadillo? {
        return MERCADILLOS[id]
    }

    fun getPuestoPorMapa(mapa: Short): Mercadillo? {
        for (merca in MERCADILLOS.values) {
            if (merca.mapas.contains(mapa)) {
                return merca
            }
        }
        return null
    }

    fun getPuestoPorid(id: Short): Mercadillo? {
        for (merca in MERCADILLOS.values) {
            if (merca.iD == id.toInt()) {
                return merca
            }
        }
        return null
    }

    fun cantPuestosMercadillos(): Int {
        return MERCADILLOS.size
    }

    // public synchronized static void addObjMercadillo(final int cuentaID, final int mercadilloID,
// final ObjetoMercadillo objMercadillo) {
// if (_OBJETOS_MERCADILLOS.get(cuentaID) == null) {
// _OBJETOS_MERCADILLOS.put(cuentaID, new HashMap<Integer, ArrayList<ObjetoMercadillo>>());
// }
// if (_OBJETOS_MERCADILLOS.get(cuentaID).get(mercadilloID) == null) {
// _OBJETOS_MERCADILLOS.get(cuentaID).put(mercadilloID, new ArrayList<ObjetoMercadillo>());
// }
// _OBJETOS_MERCADILLOS.get(cuentaID).get(mercadilloID).add(objMercadillo);
// }
//
// public synchronized static void borrarObjMercadillo(final int cuentaID, final int mercadilloID,
// final ObjetoMercadillo objMerca) {
// try {
// _OBJETOS_MERCADILLOS.get(cuentaID).get(mercadilloID).remove(objMerca);
// } catch (Exception e) {}
// }
//
// public static Map<Integer, ArrayList<ObjetoMercadillo>> getMisObjetosMercadillos(final int
// cuentaID) {
// return _OBJETOS_MERCADILLOS.get(cuentaID);
// }
    fun getAnimacion(animacionId: Int): Animacion? {
        return ANIMACIONES[animacionId]
    }

    fun addAnimacion(animacion: Animacion) {
        ANIMACIONES[animacion.id] = animacion
    }

    fun addObjetoTrueque(objetoID: Int, necesita: String?, prioridad: Int, npcs: String?) {
        val objT = ObjetoTrueque(objetoID, necesita!!, prioridad, npcs!!)
        if (!objT.necesita.isEmpty()) {
            OBJETOS_TRUEQUE.add(objT)
        }
    }

    fun borrarOtroInteractivo(gfxID: Int, mapaID: Short, celdaID: Short, accion: Int, conAccion: Boolean) {
        for (oi in OTROS_INTERACTIVOS) {
            if (gfxID == oi.gfxID && mapaID == oi.mapaID && celdaID == oi.celdaID && (!conAccion
                        || accion == oi.accionID)
            ) {
                OTROS_INTERACTIVOS.remove(oi)
                DELETE_OTRO_INTERACTIVO(gfxID, mapaID, celdaID, oi.accionID)
            }
        }
    }

    fun addOtroInteractivo(otro: OtroInteractivo) {
        OTROS_INTERACTIVOS.add(otro)
    }

    fun addMascotaModelo(mascota: MascotaModelo) {
        MASCOTAS_MODELOS[mascota.iD] = mascota
    }

    @JvmStatic
    fun getMascotaModelo(id: Int): MascotaModelo? {
        return MASCOTAS_MODELOS[id]
    }

    fun addEspecialidad(especialidad: Especialidad) {
        ESPECIALIDADES[especialidad.iD] = especialidad
    }

    @JvmStatic
    fun getEspecialidad(orden: Int, nivel: Int): Especialidad? {
        var esp: Especialidad? = null
        for (e in ESPECIALIDADES.values) {
            if (e.orden != orden) {
                continue
            }
            if (esp == null || e.nivel <= nivel && e.nivel > esp.nivel) {
                esp = e
            }
        }
        return esp
    }

    fun addDonModelo(id: Int, stat: Int) {
        DONES_MODELOS[id] = stat
    }

    @JvmStatic
    fun getDonStat(id: Int): Int {
        return DONES_MODELOS[id]!!
    }

    fun addCasa(casa: Casa) {
        CASAS[casa.id] = casa
    }

    @JvmStatic
    val casas: Map<Int, Casa>
        get() = CASAS

    @JvmStatic
    fun getCasa(id: Int): Casa? {
        return CASAS[id]
    }

    fun addCercado(cercado: Cercado) {
        CERCADOS[cercado.mapa!!.id] = cercado
    }

    fun getCercadoPorMapa(mapa: Short): Cercado? {
        return CERCADOS[mapa]
    }

    fun getPrisma(id: Int): Prisma? {
        return _PRISMAS[id]
    }

    fun addPrisma(prisma: Prisma) {
        if (prisma.mapa?.prisma != null) {
            _PRISMAS.remove(prisma.id)
            DELETE_PRISMA(prisma.id)
            return
        }
        prisma.mapa?.prisma = prisma
        if (prisma.area != null) {
            prisma.area.prisma = prisma
        }
        if (prisma.subArea != null) {
            prisma.subArea.prisma = prisma
        }
        if (prisma.id < SIG_ID_PRISMA) {
            SIG_ID_PRISMA = prisma.id
        }
        _PRISMAS[prisma.id] = prisma
    }

    @JvmStatic
    fun eliminarPrisma(prisma: Prisma) {
        prisma.mapa?.prisma = null
        if (prisma.area != null) {
            prisma.area.prisma = null
        }
        if (prisma.subArea != null) {
            prisma.subArea.prisma = null
        }
        _PRISMAS.remove(prisma.id)
        DELETE_PRISMA(prisma.id)
    }

    @JvmStatic
    val prismas: Collection<Prisma>
        get() = _PRISMAS.values

    fun getRecaudador(id: Int): Recaudador? {
        return _RECAUDADORES[id]
    }

    fun addRecaudador(recaudador: Recaudador) {
        if (recaudador.mapa?.recaudador != null) {
            recaudador.gremio?.delRecaudador(recaudador)
            DELETE_RECAUDADOR(recaudador.id)
            return
        }
        recaudador.mapa?.recaudador = recaudador
        if (recaudador.id < SIG_ID_RECAUDADOR) {
            SIG_ID_RECAUDADOR = recaudador.id
        }
        _RECAUDADORES[recaudador.id] = recaudador
    }

    @JvmStatic
    fun eliminarRecaudador(recaudador: Recaudador) {
        recaudador.mapa?.recaudador = null
        recaudador.gremio?.delRecaudador(recaudador)
        _RECAUDADORES.remove(recaudador.id)
        DELETE_RECAUDADOR(recaudador.id)
    }

    fun puedePonerRecauEnZona(subAreaID: Int, gremioID: Int): Boolean {
        var i = 0
        for (recau in _RECAUDADORES.values) {
            if (recau.gremio!!.id == gremioID && recau.mapa!!.subArea!!.id == subAreaID) {
                i++
            }
        }
        return i < AtlantaMain.MAX_RECAUDADORES_POR_ZONA
    }

    @JvmStatic
    fun addCofre(cofre: Cofre) {
        COFRES[cofre.iD] = cofre
    }

    fun getCofre(id: Int): Cofre? {
        return COFRES[id]
    }

    fun addRankingKoliseo(rank: RankingKoliseo) {
        _RANKINGS_KOLISEO[rank.id] = rank
    }

    fun delRankingKoliseo(id: Int) {
        DELETE_RANKING_PVP(id)
        _RANKINGS_KOLISEO.remove(id)
    }

    @JvmStatic
    fun getRankingKoliseo(id: Int): RankingKoliseo? {
        return _RANKINGS_KOLISEO[id]
    }

    fun addRankingPVP(rank: RankingPVP) {
        _RANKINGS_PVP[rank.id] = rank
    }

    fun delRankingPVP(id: Int) {
        DELETE_RANKING_PVP(id)
        _RANKINGS_PVP.remove(id)
    }

    @JvmStatic
    fun getRankingPVP(id: Int): RankingPVP? {
        return _RANKINGS_PVP[id]
    }

    fun getBalanceMundo(perso: Personaje): Float {
        var cant = 0
        for (subarea in SUB_AREAS.values) {
            if (subarea.alineacion == perso.alineacion) {
                cant++
            }
        }
        return if (cant == 0 || SUB_AREAS.isEmpty()) {
            0f
        } else Math.rint(1000f * cant / SUB_AREAS.size.toDouble()).toFloat() / 10f
    }

    fun getBalanceArea(perso: Personaje): Float {
        var cant = 0
        var area: Area? = null
        try {
            area = perso.mapa.subArea!!.area
        } catch (ignored: Exception) {
        }
        if (area == null) {
            return 0f
        }
        for (subarea in SUB_AREAS.values) {
            if (subarea.area == area && subarea.alineacion == perso.alineacion) {
                cant++
            }
        }
        return if (cant == 0 || area.subAreas.isEmpty()) {
            0f
        } else Math.rint(1000f * cant / area.subAreas.size.toDouble()).toFloat() / 10f
    }

    fun getBonusAlinExp(perso: Personaje): Float {
        val bonus =
            (Math.rint(Math.sqrt(AtlantaMain.RATE_CONQUISTA_EXPERIENCIA.toDouble()) * 100) / 100f).toFloat()
        return perso.gradoAlineacion / 2.5f + bonus
    }

    fun getBonusAlinRecolecta(perso: Personaje): Float {
        val bonus =
            (Math.rint(Math.sqrt(AtlantaMain.RATE_CONQUISTA_RECOLECTA.toDouble()) * 100) / 100f).toFloat()
        return perso.gradoAlineacion / 2.5f + bonus
    }

    fun getBonusAlinDrop(perso: Personaje): Float {
        val bonus =
            (Math.rint(Math.sqrt(AtlantaMain.RATE_CONQUISTA_DROP.toDouble()) * 100) / 100f).toFloat()
        return perso.gradoAlineacion / 2.5f + bonus
    }

    fun prismasGeoposicion(alineacion: Int): String {
        val str = StringBuilder()
        if (alineacion == Constantes.ALINEACION_BONTARIANO.toInt()) {
            str.append(SubArea.BONTAS)
        } else if (alineacion == Constantes.ALINEACION_BRAKMARIANO.toInt()) {
            str.append(SubArea.BRAKMARS)
        }
        str.append("|").append(SUB_AREAS.size).append("|")
            .append(SUB_AREAS.size - (SubArea.BONTAS + SubArea.BRAKMARS)).append("|")
        var primero = false
        for (subArea in SUB_AREAS.values) {
            if (subArea.esConquistable()) {
                continue
            }
            if (primero) {
                str.append(";")
            }
            str.append(subArea.id).append(",")
            str.append(subArea.alineacion.toInt()).append(",")
            str.append(if (subArea.prisma == null) 0 else if (subArea.prisma!!.pelea == null) 0 else 1)
                .append(",") // pelea
            str.append(if (subArea.prisma == null) 0 else subArea.prisma!!.mapa!!.id.toInt()).append(",")
            str.append("1") // atacable
            primero = true
        }
        str.append("|")
        if (alineacion == Constantes.ALINEACION_BONTARIANO.toInt()) {
            str.append(Area.BONTAS)
        } else if (alineacion == Constantes.ALINEACION_BRAKMARIANO.toInt()) {
            str.append(Area.BRAKMARS)
        }
        str.append("|").append(AREAS.size).append("|")
        primero = false
        for (area in AREAS.values) {
            if (primero) {
                str.append(";")
            }
            str.append(area.id).append(",")
            str.append(area.alineacion.toInt()).append(",") // alineacion
            str.append("1,") // door
            str.append(if (area.prisma == null) 0 else 1) // tiene prisma
            primero = true
        }
        return str.toString()
    }

    @JvmStatic
    fun getEncarnacionModelo(id: Int): EncarnacionModelo? {
        return ENCARNACIONES_MODELOS[id]
    }

    fun addTutorial(tutorial: Tutorial) {
        TUTORIALES[tutorial.id] = tutorial
    }

    fun getTutorial(id: Int): Tutorial? {
        return TUTORIALES[id]
    }

    @JvmStatic
    fun getDuoPorIDPrimero(objetos: ArrayList<Duo<Int, Int>>, id: Int): Duo<Int, Int>? {
        for (duo in objetos) {
            if (duo._primero == id) {
                return duo
            }
        }
        return null
    }

    fun stringServicios(perso: Personaje): String {
        val abonado = perso.cuenta.esAbonado()
        val str = StringBuilder()
        for (s in SERVICIOS.values) {
            if (s.string(abonado).isEmpty()) {
                continue
            }
            if (str.length > 0) {
                str.append("|")
            }
            str.append(s.string(abonado))
        }
        return str.toString()
    }

    fun getMascotaPorFantasma(fantasma: Int): Int {
        for (masc in MASCOTAS_MODELOS.values) {
            if (masc.fantasma == fantasma) {
                return masc.iD
            }
        }
        return 0
    }

    @JvmStatic
    fun delKoliseo(id: Int) {
        _INSCRITOS_KOLISEO.remove(id)
    }

    fun addKoliseo(perso: Personaje) {
        _INSCRITOS_KOLISEO[perso.Id] = perso
        if (_RANKINGS_KOLISEO[perso.Id] == null) {
            val rank = RankingKoliseo(perso.Id, perso.nombre, 0, 0)
            addRankingKoliseo(rank)
            REPLACE_RANKING_KOLISEO(rank)
        }
    }

    fun estaEnKoliseo(id: Int): Boolean {
        return _INSCRITOS_KOLISEO[id] != null
    }

    fun cantKoliseo(): Int {
        return _INSCRITOS_KOLISEO.size
    }

    val inscritosKoliseo: Collection<Personaje>
        get() = _INSCRITOS_KOLISEO.values

    fun iniciarKoliseo() {
        try {
            val mapas = ArrayList<Mapa?>()
            for (s in AtlantaMain.MAPAS_KOLISEO.split(",".toRegex()).toTypedArray()) {
                try {
                    mapas.add(getMapa(s.toShort()))
                } catch (ignored: Exception) {
                }
            }
            if (mapas.isEmpty()) {
                SEGUNDOS_INICIO_KOLISEO = AtlantaMain.SEGUNDOS_INICIAR_KOLISEO
                ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_FALTA_MAPAS")
                return
            }
            val listos = ArrayList<Personaje>()
            for (p in _INSCRITOS_KOLISEO.values) {
                if (p.puedeIrKoliseo()) {
                    listos.add(p)
                } else {
                    if (p.grupoKoliseo != null) {
                        p.grupoKoliseo.dejarGrupo(p)
                    }
                }
            }
            if (listos.size < AtlantaMain.CANTIDAD_MIEMBROS_EQUIPO_KOLISEO * 2) {
                SEGUNDOS_INICIO_KOLISEO = AtlantaMain.SEGUNDOS_INICIAR_KOLISEO
                ENVIAR_Im_INFORMACION_KOLISEO("1KOLISEO_FALTA_INSCRITOS")
                return
            }
            _INSCRITOS_KOLISEO.clear()
            for (p in listos) {
                val ptsNivel = p.nivel * 5
                val ptsStats = 0
                //			int ptsKoliseo = 0;
//			if (AtlantaMain.PARAM_CLASIFICAR_POR_STUFF_EN_KOLISEO) {   KOLISEO OFF SILVA
//				ptsStats = Constantes.convertirStatsEnPuntosKoliseo(p.getStatsObjEquipados());
//			}
//			if (AtlantaMain.PARAM_CLASIFICAR_POR_RANKING_EN_KOLISEO) {
//				int ptsVictorias = getRankingKoliseo(p.getID()).getVictorias() * 10;
//				int ptsDerrotas = getRankingKoliseo(p.getID()).getDerrotas() * 7;
//				ptsKoliseo = Math.max(-40, Math.min(40, ptsVictorias - ptsDerrotas));
//			}
                p.setPuntKoli(ptsNivel + ptsStats) // ptsStats + ptsKoliseo
            }
            val ordenados = ArrayList<Personaje>()
            while (true) {
                var maximo = 0
                if (ordenados.size == listos.size) {
                    break
                }
                var p: Personaje? = null
                for (d in listos) {
                    if (ordenados.contains(d)) {
                        continue
                    }
                    if (d.puntoKoli > maximo) {
                        maximo = d.puntoKoli
                        p = d
                    }
                }
                if (p != null) {
                    ordenados.add(p)
                } else {
                    break
                }
            }
            val grupos = ArrayList<GrupoKoliseo>()
            var i = 0
            var j = 1
            var b = true
            for (p in ordenados) {
                if (p.grupoKoliseo != null) {
                    if (!grupos.contains(p.grupoKoliseo)) {
                        grupos.add(p.grupoKoliseo)
                    }
                } else {
                    while (true) {
                        if (b) {
                            try {
                                if (grupos[i].addPersonaje(p)) {
                                    p.grupoKoliseo = grupos[i]
                                    break
                                } else {
                                    i = i + 2
                                }
                            } catch (e: Exception) {
                                val g = GrupoKoliseo(p)
                                p.grupoKoliseo = g
                                grupos.add(g)
                                break
                            }
                        } else {
                            try {
                                if (grupos[j].addPersonaje(p)) {
                                    p.grupoKoliseo = grupos[j]
                                    break
                                } else {
                                    j = j + 2
                                }
                            } catch (e: Exception) {
                                val g = GrupoKoliseo(p)
                                p.grupoKoliseo = g
                                grupos.add(g)
                                break
                            }
                        }
                        b = !b
                    }
                }
            }
            val combate = ArrayList<GrupoKoliseo>()
            while (true) {
                var maximo = 0
                if (combate.size == grupos.size) {
                    break
                }
                var p: GrupoKoliseo? = null
                for (d in grupos) {
                    if (combate.contains(d)) {
                        continue
                    }
                    if (d.puntuacion > maximo) {
                        maximo = d.puntuacion
                        p = d
                    }
                }
                if (p != null) {
                    combate.add(p)
                } else {
                    break
                }
            }
            var x = 0
            while (x + 1 < combate.size) {
                try {
                    val grupoA = combate[x]
                    val grupoB = combate[x + 1]
                    var promedioA = 0
                    var promedioB = 0
                    for (koli in grupoA.miembros) {
                        promedioA += koli.nivel
                    }
                    promedioA /= grupoA.miembros.size
                    for (koli in grupoB.miembros) {
                        promedioB += koli.nivel
                    }
                    promedioB /= grupoB.miembros.size
                    if (grupoA.cantPjs != grupoB.cantPjs || grupoA.contieneIPOtroGrupo(grupoB)) {
                        x -= 1
                        x += 2
                        continue
                    }
                    if (Math.max(promedioA, promedioB) - Math.min(
                            promedioA,
                            promedioB
                        ) > AtlantaMain.RANGO_NIVEL_KOLISEO
                    ) {
                        x -= 1
                        x += 2
                        continue
                    }
                    for (koli in grupoA.miembros) {
                        koli.fullPDV()
                    }
                    for (koli in grupoB.miembros) {
                        koli.fullPDV()
                    }
                    val mapa = mapas[getRandomInt(0, mapas.size - 1)]
                    if (mapa!!.iniciarPeleaKoliseo(grupoA, grupoB)) {
                        grupos.remove(grupoA)
                        grupos.remove(grupoB)
                    }
                } catch (ignored: Exception) {
                }
                x += 2
            }
            for (k in grupos) {
                for (p in k.miembros) {
                    ENVIAR_Im_INFORMACION(p, "1KOLISEO_NO_ELEGIDO")
                    _INSCRITOS_KOLISEO[p.Id] = p
                }
                k.limpiarGrupo()
            }
        } catch (e: Exception) {
            redactarLogServidorln(e.toString())
        }
        SEGUNDOS_INICIO_KOLISEO = AtlantaMain.SEGUNDOS_INICIAR_KOLISEO
    }

    fun listaObjetosTruequePor(aDar: Map<Int, Int>, npcID: Int): Map<Int, Int> {
        val recibir: MutableMap<Int, Int> = TreeMap()
        val aDar2: MutableMap<Int, Int> = TreeMap(aDar)
        val objetos = ArrayList<ObjetoTrueque>()
        for (objT in OBJETOS_TRUEQUE) {
            if (objT.permiteNPC(npcID) && !objT.necesita.isEmpty()) {
                objetos.add(objT)
            }
        }
        objetos.sort()
        for (objT in objetos) {
            var cantFinal = -1
            var completo = true
            for ((idNecesita, cantNecesita) in objT.necesita) {
                var cant = 0
                try {
                    val tiene = aDar2[idNecesita]!!
                    cant = tiene / cantNecesita
                } catch (e: Exception) {
                    completo = false
                    break
                }
                if (cant <= 0) {
                    completo = false
                    break
                }
                if (cantFinal == -1 || cant < cantFinal) {
                    cantFinal = cant
                }
            }
            if (completo) {
                for ((idNecesita, cantNecesita) in objT.necesita) {
                    val tiene = aDar2[idNecesita]!! - cantFinal * cantNecesita
                    if (tiene <= 0) {
                        aDar2.remove(idNecesita)
                    } else {
                        aDar2[idNecesita] = tiene
                    }
                }
                var actual = 0
                if (recibir.containsKey(objT.iD)) {
                    actual = recibir[objT.iD]!!
                }
                recibir[objT.iD] = actual + cantFinal
            }
        }
        return recibir
    }

    private class CompNivelMasMenos : Comparator<Personaje> {
        override fun compare(p1: Personaje, p2: Personaje): Int {
            return p2.experiencia.compareTo(p1.experiencia)
        }
    }

    private class CompDiaMasMenos : Comparator<Personaje> {
        override fun compare(p1: Personaje, p2: Personaje): Int {
            return p2.experienciaDia.compareTo(p1.experienciaDia)
        }
    }

    private class CompGremioMasMenos : Comparator<Gremio> {
        override fun compare(p1: Gremio, p2: Gremio): Int {
            return p2.experiencia.compareTo(p1.experiencia)
        }
    }

    private class CompPVPMasMenos : Comparator<RankingPVP> {
        override fun compare(p1: RankingPVP, p2: RankingPVP): Int {
            val v = p2.victorias.toLong().compareTo(p1.victorias.toLong())
            return if (v == 0) {
                p1.derrotas.toLong().compareTo(p2.derrotas.toLong())
            } else v
        }
    }

    private class CompKoliseoMasMenos : Comparator<RankingKoliseo> {
        override fun compare(p1: RankingKoliseo, p2: RankingKoliseo): Int {
            val v = p2.victorias.toLong().compareTo(p1.victorias.toLong())
            return if (v == 0) {
                p1.derrotas.toLong().compareTo(p2.derrotas.toLong())
            } else v
        }
    }

    // public static void crearGruposKoliseo1() {
// CopyOnWriteArrayList<Personaje> kolis1 = new CopyOnWriteArrayList<Personaje>();
// for (Personaje persos : Koliseo1) {
// if ((persos == null) || (!persos.enLinea()))
// continue;
// kolis1.add(persos);
// }
// if (kolis1.size() < 6)
// return;
// int size = kolis1.size();
// for (int i = 0; i < size; i += 3) {
// Personaje koli1 = null;
// Personaje koli2 = null;
// Personaje koli3 = null;
// Random rand = new Random();
// int random = rand.nextInt(kolis1.size() - 1);
// koli1 = kolis1.get(random);
// kolis1.remove(random);
// random = rand.nextInt(kolis1.size() - 1);
// koli2 = kolis1.get(random);
// kolis1.remove(random);
// random = rand.nextInt(kolis1.size() - 1);
// koli3 = kolis1.get(random);
// kolis1.remove(random);
// if ((koli1 != null) && (koli2 != null) && (koli3 != null)) {
// GrupoKoliseo grupo = new GrupoKoliseo(koli1, koli2, koli3, 1);
// GrupoKoliseo1.add(grupo);
// }
// }
// }
//
// public static void crearGruposKoliseo2() {
// CopyOnWriteArrayList<Personaje> kolis1 = new CopyOnWriteArrayList<Personaje>();
// for (Personaje persos : Koliseo2) {
// if ((persos == null) || (!persos.enLinea()))
// continue;
// kolis1.add(persos);
// }
// if (kolis1.size() < 6)
// return;
// int size = kolis1.size();
// for (int i = 0; i < size; i += 3) {
// Personaje koli1 = null;
// Personaje koli2 = null;
// Personaje koli3 = null;
// Random rand = new Random();
// int random = rand.nextInt(kolis1.size() - 1);
// koli1 = kolis1.get(random);
// kolis1.remove(random);
// random = rand.nextInt(kolis1.size() - 1);
// koli2 = kolis1.get(random);
// kolis1.remove(random);
// random = rand.nextInt(kolis1.size() - 1);
// koli3 = kolis1.get(random);
// kolis1.remove(random);
// if ((koli1 != null) && (koli2 != null) && (koli3 != null)) {
// GrupoKoliseo grupo = new GrupoKoliseo(koli1, koli2, koli3, 1);
// GrupoKoliseo2.add(grupo);
// }
// }
// }
//
// public static void crearGruposKoliseo3() {
// CopyOnWriteArrayList<Personaje> kolis1 = new CopyOnWriteArrayList<Personaje>();
// for (Personaje persos : Koliseo3) {
// if ((persos == null) || (!persos.enLinea()))
// continue;
// kolis1.add(persos);
// }
// if (kolis1.size() < 6)
// return;
// int size = kolis1.size();
// for (int i = 0; i < size; i += 3) {
// Personaje koli1 = null;
// Personaje koli2 = null;
// Personaje koli3 = null;
// Random rand = new Random();
// int random = rand.nextInt(kolis1.size() - 1);
// koli1 = kolis1.get(random);
// kolis1.remove(random);
// random = rand.nextInt(kolis1.size() - 1);
// koli2 = kolis1.get(random);
// kolis1.remove(random);
// random = rand.nextInt(kolis1.size() - 1);
// koli3 = kolis1.get(random);
// kolis1.remove(random);
// if ((koli1 != null) && (koli2 != null) && (koli3 != null)) {
// GrupoKoliseo grupo = new GrupoKoliseo(koli1, koli2, koli3, 1);
// GrupoKoliseo3.add(grupo);
// }
// }
// }
    class Duo<L, R>(var _primero: L, var _segundo: R)

    class Experiencia(
        val _personaje: Long, val _oficio: Int, val _montura: Int, val _gremio: Long, val _alineacion: Int,
        encarnacion: Int
    ) {
        val _encarnacion: Long

        init {
            _encarnacion = encarnacion.toLong()
        }
    }
}