package variables.personaje;

import estaticos.*;
import estaticos.Mundo.Duo;
import servidor.ServidorServer;
import servidor.ServidorSocket;
import servidor.ServidorSocket.AccionDeJuego;
import sprites.Exchanger;
import sprites.PreLuchador;
import sprites.Preguntador;
import utilidades.economia.Economia;
import variables.casa.Casa;
import variables.casa.Cofre;
import variables.encarnacion.Encarnacion;
import variables.gremio.Gremio;
import variables.gremio.MiembroGremio;
import variables.hechizo.Hechizo;
import variables.hechizo.StatHechizo;
import variables.mapa.Celda;
import variables.mapa.Cercado;
import variables.mapa.Mapa;
import variables.mapa.interactivo.ObjetoInteractivo;
import variables.mapa.interactivo.OtroInteractivo;
import variables.mision.Mision;
import variables.mision.MisionModelo;
import variables.mision.MisionObjetivoModelo;
import variables.mob.MobGradoModelo;
import variables.mob.MobModelo;
import variables.montura.Montura;
import variables.montura.Montura.Ubicacion;
import variables.objeto.Objeto;
import variables.objeto.ObjetoModelo;
import variables.objeto.ObjetoModelo.CAPACIDAD_STATS;
import variables.objeto.ObjetoSet;
import variables.oficio.Oficio;
import variables.oficio.StatOficio;
import variables.oficio.Trabajo;
import variables.pelea.Luchador;
import variables.pelea.Pelea;
import variables.personaje.Clase.BoostStat;
import variables.personaje.Especialidad.Don;
import variables.stats.Stats;
import variables.stats.TotalStats;
import variables.zotros.Almanax;
import variables.zotros.Prisma;
import variables.zotros.Tutorial;

import javax.swing.Timer;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Personaje implements PreLuchador, Exchanger, Preguntador {
    // Restricciones
    // public boolean _RApuedeAgredir, _RApuedeDesafiar, _RApuedeIntercambiar, _RApuedeAtacar,
    // _RApuedeChatATodos,
    // _RApuedeMercante, _RApuedeUsarObjetos, _RApuedeInteractuarRecaudador,
    // _RApuedeInteractuarObjetos, _RApuedeHablarNPC,
    // _RApuedeAtacarMobsDungCuandoMutante, _RApuedeMoverTodasDirecciones,
    // _RApuedeAtacarMobsCualquieraCuandoMutante,
    // _RApuedeInteractuarPrisma, _RBpuedeSerAgredido, _RBpuedeSerDesafiado, _RBpuedeHacerIntercambio,
    // _RBpuedeSerAtacado,
    // _RBforzadoCaminar, _RBesFantasma, _RBpuedeSwitchModoCriatura, _RBesTumba;
    // RESTRICCIONES A
    public static final int RA_PUEDE_AGREDIR = 1;
    public static final int RA_PUEDE_DESAFIAR = 2;
    public static final int RA_PUEDE_INTERCAMBIAR = 4;
    public static final int RA_NO_PUEDE_ATACAR = 8;
    public static final int RA_PUEDE_CHAT_A_TODOS = 16;
    public static final int RA_PUEDE_MERCANTE = 32;
    public static final int RA_PUEDE_USAR_OBJETOS = 64;
    public static final int RA_PUEDE_INTERACTUAR_RECAUDADOR = 128;
    public static final int RA_PUEDE_INTERACTUAR_OBJETOS = 256;
    public static final int RA_PUEDE_HABLAR_NPC = 512;
    public static final int RA_NO_PUEDE_ATACAR_MOBS_DUNG_CUANDO_MUTENTE = 4096;
    public static final int RA_NO_PUEDE_MOVER_TODAS_DIRECCIONES = 8192;
    public static final int RA_NO_PUEDE_ATACAR_MOBS_CUALQUIERA_CUANDO_MUTANTE = 16384;
    public static final int RA_PUEDE_INTERACTUAR_PRISMA = 32768;
    // RESTRICCIONES B
    public static final int RB_PUEDE_SER_AGREDIDO = 1;
    public static final int RB_PUEDE_SER_DESAFIADO = 2;
    public static final int RB_PUEDE_HACER_INTERCAMBIO = 4;
    public static final int RB_PUEDE_SER_ATACADO = 8;
    public static final int RB_PUEDE_CORRER = 16;
    public static final int RB_NO_ES_FANTASMA = 32;
    public static final int RB_PUEDE_SWITCH_MODO_CRIATURA = 64;
    public static final int RB_NO_ES_TUMBA = 128;
    public final Cuenta Cuenta;
    public final Map<Integer, Integer> SubStatsBase = new HashMap<>();
    public final Map<Integer, Integer> SubStatsScroll = new HashMap<>();
    public final Map<Integer, Integer> Titulos = new HashMap<>();
    public final TotalStats TotalStats = new TotalStats(new Stats(), new Stats(), new Stats(), new Stats(), 1);
    public final ArrayList<Integer> Ornamentos = new ArrayList<>();
    public final ArrayList<Short> Zaaps = new ArrayList<>();
    public final ArrayList<Integer> CardMobs = new ArrayList<>(), Almanax = new ArrayList<>(),
            IdsOmitidos = new ArrayList<>();
    public final Tienda Tienda = new Tienda();
    public final CopyOnWriteArrayList<Mision> Misiones = new CopyOnWriteArrayList<>();
    public final Map<Integer, Duo<Integer, Integer>> BonusSetDeClase = new HashMap<>();
    public final Map<Integer, Objeto> Objetos = new ConcurrentHashMap<>();
    public final Map<Integer, SetRapido> SetsRapidos = new ConcurrentHashMap<>();
    public final Map<Byte, StatOficio> StatsOficios = new HashMap<>();
    public final ArrayList<HechizoPersonaje> Hechizos = new ArrayList<>();
    public final Objeto[] ObjPos49 = new Objeto[49];
    public final Map<Objeto, Boolean> DropPelea = new HashMap<>();
    //
    public boolean CreandoJuego = true;
    public boolean EsMercante, MostrarAlas, MostrarAmigos = true, Ocupado, Sentado, EnLinea, Montando, Ausente,
            Invisible, OlvidandoHechizo, PescarKuakua, Agresion, Indetectable, Huir = true, Inmovil, CambiarColor,
            Calabozo, CargandoMapa, RecienCreado, DeNoche, DeDia;
    public byte Orientacion = 1, Alineacion = Constantes.ALINEACION_NEUTRAL, Sexo, ClaseID, EmoteActivado,
            Rostro = 1, TipoExchange = Constantes.INTERCAMBIO_TIPO_NULO, Resets;
    public int Talla = 100, Ornamento, GfxID, Titulo, PorcXPMontura, IDMisionDocumento = 0;
    public short MapaSalvada, CeldaSalvada;
    public int GradoAlineacion = 1, Nivel = 1;
    public int Id, Color1 = -1, Color2 = -1, Color3 = -1, PuntosHechizos, PuntosStats, Energia = 10000, Emotes,
            Deshonor, Pdv, PdvMax, UltPDV, Honor, ConversandoCon, Pregunta, EsposoID, ColorNombre = -1;
    public int RestriccionesALocalPlayer = 8200, RestriccionesBCharacter = 8, PuntKoli, UltimoNivel, OrdenNivel,
            Orden;
    public int Pretendiente;
    public long Kamas, Experiencia, TiempoAgresion, ExperienciaDia, InicioTuto;
    public long TiempoDesconexion, TiempoUltEncarnacion, TiempoPenalizacionKoliseo, TiempoUltDesafio;
    // public float _velocidad;
    public String Nombre = "", ForjaEc = "", UltVictimaPVP = "", TituloVIP = "", Canales = "*#%!pi$:?^¡@~",
            TipoInvitacion = "";
    public MiembroGremio MiembroGremio;
    public Pelea Pelea, PrePelea;
    public Mapa Mapa;
    public Celda Celda;
    public Grupo Grupo;
    public Montura Montura;
    public Personaje Multi, Compañero, InvitandoA, Invitador;
    public ArrayList<Personaje> Multis = new ArrayList<>();
    public Personaje LiderIP;
    public boolean EsliderIP = false;
    public Exchanger Exchanger;
    public MisionPVP MisionPvp;
    public Cofre ConsultarCofre;
    public Casa CasaDentro, ConsultarCasa;
    public Map<Integer, StatHechizo> MapStatsHechizos;// solo es para los multiman
    public Map<String, ArrayList<Long>> Agredir;
    public Map<String, ArrayList<Long>> Agredido;
    public GrupoKoliseo Koliseo;
    public Encarnacion Encarnacion;
    public Tutorial Tutorial;
    public Clase Clase;
    public byte MedioPagoServicio = 0;
    public Timer RecuperarVida;
    public StringBuilder PacketsCola = new StringBuilder();
    public boolean ComandoPasarTurno;
    public Oficio OficioActual = null;
    public int UnirsePrePeleaAlID;
    public boolean Mostrardetallexp = false;
    public boolean Salvando;
    public boolean Refrescarmobsautomatico = false;
    public String Busquedagrupo = "0";
    public Mapa MapaGDM;
    public int ActualizadorIniciativa;
    public boolean Mostrarpanelpelea = true;
    public Personaje Exchangemismaip;
    public int PAoriginales = 0;
    public int PMoriginales = 0;
    public boolean Desconectando = false;
    public boolean xp_bloqueada = false;

    public Personaje(final int id, final String nombre, final byte sexo, final byte claseID, final int color1,
                     final int color2, final int color3, final long kamas, final int puntosHechizo, final int capital, final int energia,
                     final short nivel, final long exp, final int talla, final int gfxID, final byte alineacion, final int cuenta,
                     final Map<Integer, Integer> statsBase, final Map<Integer, Integer> statsScroll, final boolean mostrarAmigos,
                     final boolean mostarAlineacion, final String canal, final short mapa, final short celda, final String inventario,
                     final int porcPDV, final String hechizos, final String ptoSalvada, final String oficios, final byte porcXPMontura,
                     final int montura, final int honor, final int deshonor, final byte gradoAlineacion, final String zaaps,
                     final int esposoID, final String tienda, final boolean mercante, final int restriccionesA, final int restriccionesB,
                     final int encarnacion, final int emotes, final String titulos, final String tituloVIP, final String ornamentos,
                     final String misiones, final String coleccion, final byte resets, final String almanax, final int ultimoNivel,
                     final String setsRapidos, final int colorNombre, final String orden) {
        Cuenta = Mundo.getCuenta(cuenta);
        try {
            boolean modificar = false;
            try {
                Mapa = Mundo.getMapa(mapa);
                setCelda(Mapa.getCelda(celda));
            } catch (Exception e) {
                Mapa = Mundo.getMapa((short) 7411);
                setCelda(Mapa.getCelda((short) 311));
            }
            if (Mapa == null || Celda == null) {
                AtlantaMain.redactarLogServidorln("Mapa o celda invalido del personaje " + Nombre
                        + ", por lo tanto se cierra el server");
                System.exit(1);
                return;
            }
            this.Id = id;
            Nombre = nombre;
            ColorNombre = colorNombre;
            Sexo = sexo;
            ClaseID = claseID;
            Clase = Mundo.getClase(ClaseID);
            Color1 = color1;
            Color2 = color2;
            Color3 = color3;
            SubStatsBase.putAll(statsBase);
            PuntosHechizos = puntosHechizo;
            PuntosStats = capital;
            Energia = energia;
            Talla = talla;
            GfxID = gfxID;
            RestriccionesALocalPlayer = restriccionesA;
            RestriccionesBCharacter = restriccionesB;
            UltimoNivel = ultimoNivel;
            Canales = canal;
            if (AtlantaMain.PARAM_REINICIAR_CANALES) {
                Canales = "*#%!pi$:?^¡@~";
//                modificar = true;
            }
            addCanal("~");
            EsposoID = esposoID;
            Resets = resets;
            Experiencia = exp;
            Nivel = 1;
            while (Experiencia >= Mundo.getExpPersonaje(Nivel + 1)) {
                Nivel++;
                if (Nivel >= AtlantaMain.NIVEL_MAX_PERSONAJE) {
                    break;
                }
            }
            Alineacion = alineacion;
            if (Alineacion != Constantes.ALINEACION_NEUTRAL) {
                Honor = honor;
                Deshonor = deshonor;
                GradoAlineacion = gradoAlineacion;
                if (AtlantaMain.HONOR_FIJO_PARA_TODOS > -1) {
                    Honor = AtlantaMain.HONOR_FIJO_PARA_TODOS;
                    refrescarGradoAlineacion();
                }
            }
            if (orden.isEmpty()) {
                OrdenNivel = 0;
                switch (Alineacion) {
                    case Constantes.ALINEACION_BONTARIANO:
                        Orden = 1;
                        break;
                    case Constantes.ALINEACION_BRAKMARIANO:
                        Orden = 5;
                        break;
                    case Constantes.ALINEACION_MERCENARIO:
                        Orden = 9;
                        break;
                    default:
                        Orden = 0;
                        break;
                }
            } else {
                String[] ord = orden.split(",");
                try {
                    Orden = Integer.parseInt(ord[0]);
                    OrdenNivel = Integer.parseInt(ord[1]);
                } catch (Exception ignored) {
                }
            }
            actualizarStatsEspecialidad(Mundo.getEspecialidad(Orden, OrdenNivel));
            if (AtlantaMain.PARAM_START_EMOTES_COMPLETOS) {
                Emotes = 7667711;
            } else {
                Emotes = emotes;
            }
            if (montura < -1) {
                setMontura(Mundo.getMontura(montura));
            }
            setPorcXPMontura(porcXPMontura);
            SubStatsScroll.putAll(statsScroll);
            TotalStats.getStatsBase().nuevosStatsBase(SubStatsBase, this);
            TotalStats.getStatsBase().acumularStats(SubStatsScroll);
            addKamas(kamas, false, false);
            setPuntoSalvada(ptoSalvada);
            setMisiones(misiones);
            final String[] tArray = titulos.split(Pattern.quote(","));
            for (String t : tArray) {
                if (t.isEmpty()) {
                    continue;
                }
                try {
                    String[] tt = t.split(Pattern.quote("*"));
                    int titulo = Integer.parseInt(tt[0]);
                    int color = -1;
                    if (tt.length > 1) {
                        color = Integer.parseInt(tt[1]);
                    }
                    Titulos.put(titulo, color);
                    if (t.contains("+")) {
                        Titulo = titulo;
                    }
                } catch (Exception ignored) {
                }
            }
            TituloVIP = tituloVIP;
            for (final String str : ornamentos.split(",")) {
                if (str.isEmpty()) {
                    continue;
                }
                try {
                    int ornamento = Integer.parseInt(str);
                    Ornamentos.add(ornamento);
                    if (str.contains("+")) {
                        Ornamento = ornamento;
                    }
                } catch (final Exception ignored) {
                }
            }
            Ornamentos.trimToSize();
            for (final String str : coleccion.split(",")) {
                if (str.isEmpty()) {
                    continue;
                }
                try {
                    CardMobs.add(Integer.parseInt(str));
                } catch (final Exception ignored) {
                }
            }
            CardMobs.trimToSize();
            if (AtlantaMain.PARAM_PERMITIR_DESACTIVAR_ALAS) {
                MostrarAlas = mostarAlineacion;
            } else {
                MostrarAlas = Alineacion != Constantes.ALINEACION_NEUTRAL;
            }
            MostrarAmigos = mostrarAmigos;
            for (final String str : zaaps.split(",")) {
                try {
                    Zaaps.add(Short.parseShort(str));
                } catch (final Exception ignored) {
                }
            }
            Zaaps.trimToSize();
            for (final String str : almanax.split(",")) {
                try {
                    if (str.isEmpty()) {
                        continue;
                    }
                    Almanax.add(Integer.parseInt(str));
                } catch (final Exception ignored) {
                }
            }
            Almanax.trimToSize();
            for (final String idObjeto : inventario.split(Pattern.quote("|"))) {
                try {
                    if (idObjeto.isEmpty()) {
                        continue;
                    }
                    Objeto obj = Mundo.getObjeto(Integer.parseInt(idObjeto));
                    if (obj.getDueñoTemp() == 0) {
                        obj.setDueñoTemp(this.Id);
                        // se agrega el objeto al array _objPos
                        addObjetoConOAKO(obj, false);
                    } else {
                        modificar = true;
                        AtlantaMain.redactarLogServidorln("El objetoID " + idObjeto + " tiene dueño " + (obj.getDueñoTemp())
                                + " no se puede agregar a " + Nombre + "(" + this.Id + ")");
                    }
                } catch (Exception e) {
                    modificar = true;
                    AtlantaMain.redactarLogServidorln("El objetoID " + idObjeto + " pertenece a " + Nombre + "(" + this.Id + ")"
                            + ", no existe");
                }
            }
            for (final String idObjeto : tienda.split(Pattern.quote("|"))) {
                try {
                    if (idObjeto.isEmpty()) {
                        continue;
                    }
                    Objeto obj = Mundo.getObjeto(Integer.parseInt(idObjeto));
                    if (obj.getDueñoTemp() == 0) {
                        obj.setDueñoTemp(this.Id);
                        if (obj.getPrecio() <= 0) {
                            addObjetoConOAKO(obj, false);
                        } else {
                            Tienda.addObjeto(obj);
                        }
                    } else {
                        modificar = true;
                        AtlantaMain.redactarLogServidorln("La tiendaID " + idObjeto + " tiene dueño " + (obj.getDueñoTemp())
                                + " no se puede agregar a " + Nombre + "(" + this.Id + ")");
                    }
                } catch (Exception e) {
                    modificar = true;
                    AtlantaMain.redactarLogServidorln("El objetoID " + idObjeto + " pertenece a " + Nombre + "(" + this.Id + ")"
                            + ", no existe");
                }
            }
            // boolean mensaje = false;
            // StringBuilder str = new StringBuilder();
            // ArrayList<Objeto> objetos = new ArrayList<>();
            // objetos.addAll(_objetos.values());
            // objetos.addAll(_tienda);
            // for (Objeto o : objetos) {
            // if (o._reseteado) {
            // mensaje = true;
            // if (str.length() > 0)
            // str.append(",");
            // str.append("6962");
            // }
            // }
            // if (mensaje) {
            // _cuenta.addRegalo(str.toString());
            // _cuenta
            // .addMensaje(
            // "1223;Tus objetos magueados se han puesto con los stats base debido a una modificación en
            // la forjamagia muy importante, así evitarémos el over magueo en el servidor ANKALIKE. Te
            // hemos dejado un regalo por cada objeto que te modificamos.",
            // true);
            // }
            if (AtlantaMain.PARAM_RESET_STATS_PLAYERS) {
                modificar = true;
                resetearStats(false);
            }
            if (Cuenta != null) {
                EsMercante = mercante;
                if (EsMercante) {
                    if (!Tienda.estaVacia()) {
                        Mapa.addMercante(this);
                    } else {
                        EsMercante = false;
                    }
                }
                // se pone en la creacion para considerar como si recien se hubiera desconectado
                TiempoDesconexion = System.currentTimeMillis();
                analizarPosHechizos(hechizos);
                if (AtlantaMain.PARAM_PERMITIR_OFICIOS) {
                    StatsOficios.put((byte) 7, new StatOficio((byte) 7, Mundo.getOficio(1), 0));
                    if (!oficios.isEmpty()) {
                        for (final String data : oficios.split(";")) {
                            try {
                                final String[] infos = data.split(",");
                                aprenderOficio(Mundo.getOficio(Integer.parseInt(infos[0])), Integer.parseInt(infos[1]));
                            } catch (final Exception ignored) {
                            }
                        }
                    }
                }
                try {
                    for (String s : setsRapidos.split(Pattern.quote("*"))) {
                        if (s.isEmpty()) {
                            continue;
                        }
                        String[] split = s.split(Pattern.quote("|"));
                        int idSet = Integer.parseInt(split[0]);
                        String nombreSet = split[1];
                        int iconoSet = Integer.parseInt(split[2]);
                        String dataSet = split[3];
                        addSetRapido(idSet, nombreSet, iconoSet, dataSet);
                    }
                } catch (Exception ignored) {
                }
                if (Energia > 10000) {
                    Energia = 10000;
                } else if (Energia < 0 && !esTumba()) {
                    convertirseTumba();
                } else if (Energia == 0 && !esFantasma()) {
                    convertirseFantasma();
                }
                if (!AtlantaMain.PARAM_PERDER_ENERGIA) {
                    Energia = 10000;
                }
                actualizarPDV(porcPDV);
                if (modificar) {
                    GestorSQL.INSTANCE.SALVAR_PERSONAJE(this, false);
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (Cuenta == null) {
                AtlantaMain.redactarLogServidorln("SE DEBE ELIMINAR PERSONAJE " + nombre + " (" + id + ") - CUENTA " + cuenta);
                if (AtlantaMain.PARAM_ELIMINAR_PERSONAJES_BUG) {
                    Mundo.eliminarPersonaje(this, true);
                }
            } else {
                Cuenta.addPersonaje(this);
            }
        }
    }

    public Personaje(final int id, final String nombre, final byte sexo, final byte claseID, final int color1,
                     final int color2, final int color3, final long kamas, final int puntosHechizo, final int capital, final int nivel,
                     final long exp, final int talla, final int gfxID, final int cuenta, final short mapa, final short celda,
                     final String inventario, final String ptoSalvada, final String zaaps, final int emotes) {
        // personaje recien creado
        Cuenta = Mundo.getCuenta(cuenta);
        this.Id = id;
        Nombre = nombre;
        Sexo = sexo;
        ClaseID = claseID;
        Clase = Mundo.getClase(ClaseID);
        Color1 = color1;
        Color2 = color2;
        Color3 = color3;
        PuntosHechizos = puntosHechizo;
        PuntosStats = capital;
        Talla = talla;
        GfxID = gfxID;
        Experiencia = exp;
        while (Experiencia >= Mundo.getExpPersonaje(Nivel + 1)) {
            Nivel++;
            if (Nivel >= AtlantaMain.NIVEL_MAX_PERSONAJE) {
                break;
            }
        }
        reiniciarSubStats(SubStatsBase);
        reiniciarSubStats(SubStatsScroll);
        TotalStats.getStatsBase().nuevosStatsBase(SubStatsBase, this);
        TotalStats.getStatsBase().acumularStats(SubStatsScroll);
        addKamas(kamas, false, false);
        setPuntoSalvada(ptoSalvada);
        if (AtlantaMain.PARAM_START_EMOTES_COMPLETOS) {
            Emotes = 7667711;
        } else {
            Emotes = emotes;
        }
        if (!AtlantaMain.PARAM_PERMITIR_DESACTIVAR_ALAS) {
            MostrarAlas = Alineacion != Constantes.ALINEACION_NEUTRAL;
        }
        for (final String str : zaaps.split(",")) {
            try {
                Zaaps.add(Short.parseShort(str));
            } catch (final Exception ignored) {
            }
        }
        Zaaps.trimToSize();
        for (final String idObjeto : inventario.split(Pattern.quote("|"))) {
            try {
                if (idObjeto.isEmpty()) {
                    continue;
                }
                Objeto obj = Mundo.getObjeto(Integer.parseInt(idObjeto));
                if (obj.getDueñoTemp() == 0) {
                    obj.setDueñoTemp(this.Id);
                    // se agrega el objeto al array _objPos
                    addObjetoConOAKO(obj, false);
                } else {
                    AtlantaMain.redactarLogServidorln("El objetoID " + idObjeto + " tiene dueño " + (obj.getDueñoTemp())
                            + " no se puede agregar a " + Nombre + "(" + this.Id + ")");
                }
            } catch (Exception e) {
                AtlantaMain.redactarLogServidorln("El objetoID " + idObjeto + " pertenece a " + Nombre + "(" + this.Id + ")"
                        + ", no existe");
            }
        }
        if (AtlantaMain.PARAM_RESET_STATS_PLAYERS) {
            resetearStats(false);
        }
        fullPDV();
        if (AtlantaMain.PARAM_PERMITIR_OFICIOS) {
            StatsOficios.put((byte) 7, new StatOficio((byte) 7, Mundo.getOficio(1), 0));
        }
        fijarHechizosInicio();
        RecienCreado = true;
        Mapa = Mundo.getMapa(mapa);
        setCelda(Mapa.getCelda(celda));
    }

    // CLON
    public Personaje(final int id, final String nombre, final byte sexo, final byte clase, final int color1,
                     final int color2, final int color3, final int nivel, final int talla, final int gfxID, final TotalStats totalStats,
                     final float porcPDV, final int pdvMax, final boolean mostarAlineacion, final int gradoAlineacion,
                     final byte alineacion, Montura montura, Objeto[] objPos) {
        // crear clon
        this.Id = id;
        Nombre = nombre;
        Sexo = sexo;
        ClaseID = clase;
        Clase = Mundo.getClase(ClaseID);
        Color1 = color1;
        Color2 = color2;
        Color3 = color3;
        Nivel = nivel;
        GradoAlineacion = gradoAlineacion;
        Alineacion = alineacion;
        Talla = talla;
        GfxID = gfxID;
        TotalStats.getStatsBase().nuevosStats(totalStats.getStatsBase());
        TotalStats.getStatsObjetos().nuevosStats(totalStats.getStatsObjetos());
        if (AtlantaMain.PARAM_PERMITIR_DESACTIVAR_ALAS) {
            MostrarAlas = mostarAlineacion;
        } else {
            MostrarAlas = Alineacion != Constantes.ALINEACION_NEUTRAL;
        }
        actualizarPDV(porcPDV);
        if (montura != null) {
            Montando = true;
            Montura = montura;
        }
        if (objPos != null) {
            for (Objeto obj : objPos) {
                if (obj == null) {
                    continue;
                }
                ObjPos49[obj.getPosicion()] = obj;
            }
        }
        Cuenta = null;
    }

    public Personaje(final int id, final int nivel, int iniciativa, final MobModelo mobModelo) {
        Stats stats = new Stats();
        stats.fijarStatID(Constantes.STAT_MAS_PA, 6);
        stats.fijarStatID(Constantes.STAT_MAS_PM, 3);
        MobGradoModelo mobGrado = mobModelo.getGradoPorGrado((byte) 1);
        for (Entry<Integer, Integer> entry : mobGrado.getStats().getEntrySet()) {
            int valor = entry.getValue();
            switch (entry.getKey()) {
                case Constantes.STAT_MAS_PA:
                    valor -= 6;
                    break;
                case Constantes.STAT_MAS_PM:
                    valor -= 3;
                    break;
                case Constantes.STAT_MAS_INICIATIVA:
                    continue;
            }
            stats.addStatID(entry.getKey(), valor * nivel / AtlantaMain.NIVEL_MAX_PERSONAJE);
        }
        stats.addStatID(Constantes.STAT_MAS_INICIATIVA, iniciativa / 2);
        int PDV = mobGrado.getPDVMAX() * nivel / AtlantaMain.NIVEL_MAX_PERSONAJE;
        this.Id = id;
        Nombre = mobModelo.getNombre();
        ClaseID = Constantes.CLASE_MULTIMAN;
        Nivel = nivel;
        GfxID = mobModelo.getGfxID();
        Talla = mobModelo.getTalla();
        TotalStats.getStatsBase().nuevosStats(stats);
        PdvMax = this.Pdv = PDV;
        Cuenta = null;
        int i = 1;
        for (Entry<Integer, StatHechizo> entry : mobGrado.getHechizos().entrySet()) {
            StatHechizo st = entry.getValue();
            if (st == null) {
                continue;
            }
            addHechizoPersonaje(Encriptador.getValorHashPorNumero(i), st.getHechizo(), st.getGrado());
            i++;
        }
        MapStatsHechizos = new HashMap<>();
        MapStatsHechizos.putAll(mobGrado.getHechizos());
    }

    public static synchronized Personaje crearPersonaje(final String nombre, byte sexo, byte claseID, int color1,
                                                        int color2, int color3, final Cuenta cuenta) {
        try {
            color1 = Math.min(16777215, Math.max(-1, color1));
            color2 = Math.min(16777215, Math.max(-1, color2));
            color3 = Math.min(16777215, Math.max(-1, color3));
            sexo = (sexo != Constantes.SEXO_MASCULINO ? Constantes.SEXO_FEMENINO : Constantes.SEXO_MASCULINO);
            if (Mundo.getClase(claseID) == null) {
                claseID = 1;
            }
            Clase clase = Mundo.getClase(claseID);
            final StringBuilder zaaps = new StringBuilder();
            for (final String zaap : AtlantaMain.INICIO_ZAAPS.split(",")) {
                try {
                    if (zaap.isEmpty()) {
                        continue;
                    }
                    if (Mundo.getCeldaZaapPorMapaID(Short.parseShort(zaap)) == -1) {
                        continue;
                    }
                    if (zaaps.length() > 0) {
                        zaaps.append(",");
                    }
                    zaaps.append(zaap);
                } catch (Exception ignored) {
                }
            }
            long kamas = 0;
            final StringBuilder objetos = new StringBuilder();
            final int nivel = AtlantaMain.INICIO_NIVEL;
            if (!AtlantaMain.PARAM_SOLO_PRIMERA_VEZ || cuenta.getPrimeraVez() == 1) {
                cuenta.addKamasBanco(AtlantaMain.KAMAS_BANCO);
                for (final String str : AtlantaMain.INICIO_OBJETOS.split(";")) {
                    try {
                        if (str.isEmpty()) {
                            continue;
                        }
                        String[] arg = str.split(",");
                        final Objeto obj = Mundo.getObjetoModelo(Integer.parseInt(arg[0])).crearObjeto(Integer.parseInt(arg[1]),
                                Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO);
                        Mundo.addObjeto(obj, false);
                        try {
                            if (arg.length > 2) {
                                byte pos = Byte.parseByte(arg[2]);
                                obj.setPosicion(pos);
                            }
                        } catch (Exception ignored) {
                        }
                        if (objetos.length() > 0) {
                            objetos.append("|");
                        }
                        objetos.append(obj.getId());
                    } catch (final Exception ignored) {
                    }
                }
                for (final String str : AtlantaMain.INICIO_SET_ID.split(",")) {
                    if (str.isEmpty()) {
                        continue;
                    }
                    final ObjetoSet objSet = Mundo.getObjetoSet(Integer.parseInt(str));
                    if (objSet != null) {
                        for (final ObjetoModelo OM : objSet.getObjetosModelos()) {
                            final Objeto x = OM.crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO);
                            Mundo.addObjeto(x, false);
                            if (objetos.length() > 0) {
                                objetos.append("|");
                            }
                            objetos.append(x.getId());
                        }
                    }
                }
                kamas += AtlantaMain.INICIO_KAMAS;
                cuenta.setPrimeraVez();
            }
            short mapaID = clase.getMapaInicio();
            short celdaID = clase.getCeldaInicio();
            Mapa mapa = Mundo.getMapa(mapaID);
            if (mapa == null) {
                mapaID = 7411;
                celdaID = 311;
            }
            String puntoSalvada = mapaID + "," + celdaID;
            int id = Mundo.sigIDPersonaje();
            int puntosHechizo = (nivel - 1) * AtlantaMain.PUNTOS_HECHIZO_POR_NIVEL;
            int puntosStats = ((nivel - 1) * AtlantaMain.PUNTOS_STATS_POR_NIVEL) + AtlantaMain.INICIO_PUNTOS_STATS;
            int gfxID = clase.getGfxs(sexo);
            int talla = clase.getTallas(sexo);
            long xp = Mundo.getExpPersonaje(nivel);
            int emotes = AtlantaMain.INICIO_EMOTES;
            final Personaje nuevoPersonaje = new Personaje(id, nombre, sexo, claseID, color1, color2, color3, kamas,
                    puntosHechizo, puntosStats, nivel, xp, talla, gfxID, cuenta.getId(), mapaID, celdaID, objetos.toString(),
                    puntoSalvada, zaaps.toString(), emotes);
            Mundo.addPersonaje(nuevoPersonaje);
            for (Objeto x :
                    nuevoPersonaje.getObjetosTodos()) {
                if (nuevoPersonaje.getObjPosicion(Constantes.getPosObjeto(x.getObjModelo().getTipo(), nuevoPersonaje)) == null && x.getObjModelo().getNivel() <= nuevoPersonaje.getNivel()) {
                    x.setPosicion(Constantes.getPosObjeto(x.getObjModelo().getTipo(), nuevoPersonaje), nuevoPersonaje, false);
                }
            }
            GestorSQL.INSTANCE.SALVAR_PERSONAJE(nuevoPersonaje, true);
            return nuevoPersonaje;
        } catch (final Exception e) {
            AtlantaMain.redactarLogServidorln("EXCEPTION crearPersonaje " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    // Doble
    public static Personaje crearClon(final Personaje perso, final int id) {
        boolean mostrarAlas = false;
        int gradoAlineacion = 0;
        if (perso.alasActivadas()) {
            mostrarAlas = true;
            gradoAlineacion = perso.getGradoAlineacion();
        }
        return new Personaje(id, perso.Nombre, perso.Sexo, perso.ClaseID, perso.Color1, perso.Color2,
                perso.Color3, perso.Nivel, perso.Talla, perso.GfxID, perso.TotalStats, perso.getPorcPDV(), perso.getPdvMax(),
                mostrarAlas, gradoAlineacion, perso.Alineacion, (perso.Montando && perso.Montura != null)
                ? perso.Montura
                : null, perso.ObjPos49);
    }

    public static Personaje crearMultiman(final int id, final int nivel, int iniciativa, final MobModelo mobModelo) {
        return new Personaje(id, nivel, iniciativa, mobModelo);
    }

    public static String nombreValido(String nombre, boolean comando) {
        if (Mundo.getPersonajePorNombre(nombre) != null) {
            return null;
        }
        if (nombre.length() < 1 || nombre.length() > 20) {
            return "";
        }
        if (!comando) {
            StringBuilder nombreFinal = new StringBuilder();
            final String nLower = nombre.toLowerCase();
            final String abcMin = "abcdefghijklmnopqrstuvwxyz-";
            int cantSimbol = 0;
            char letra_A = ' ', letra_B = ' ';
            boolean primera = true;
            for (final char letra : nLower.toCharArray()) {
                if (primera && letra == '-' || !abcMin.contains(letra + "") || letra == letra_A && letra == letra_B) {
                    return "";
                }
                if (primera) {
                    nombreFinal.append((letra + "").toUpperCase());
                } else {
                    nombreFinal.append(letra);
                }
                primera = false;
                if (abcMin.contains(letra + "") && letra != '-') {
                    letra_A = letra_B;
                    letra_B = letra;
                } else if (letra == '-') {
                    primera = true;
                    if (cantSimbol >= 1) {
                        return "";
                    }
                    cantSimbol++;
                }
            }
            if (AtlantaMain.PARAM_CORREGIR_NOMBRE_JUGADOR) {
                nombre = nombreFinal.toString();
            }
        }
        return nombre;
    }

    public Map<Objeto, Boolean> getDropsPelea() {
        return DropPelea;
    }

    public long getTiempoUltDesafio() {
        return TiempoUltDesafio;
    }

    public int getIDMisionDocumento() {
        return IDMisionDocumento;
    }

    public void setIDMisionDocumento(int i) {
        IDMisionDocumento = i;
    }

    public void setTiempoUltDesafio() {
        TiempoUltDesafio = System.currentTimeMillis();
    }

    public boolean getComandoPasarTurno() {
        return ComandoPasarTurno;
    }

    public void setComandoPasarTurno(boolean _comandoPasarTurno) {
        this.ComandoPasarTurno = _comandoPasarTurno;
    }

    public String getBusquedagrupo() {
        return Busquedagrupo;
    }

    public void setBusquedagrupo(String a) {
        Busquedagrupo = a;
    }

    public boolean esDeNoche() {
        return DeNoche;
    }

    public boolean esDeDia() {
        return DeDia;
    }

    public void setDeNoche() {
        DeNoche = !DeNoche;
        if (DeNoche) {
            DeDia = false;
        }
    }

    public void setDeDia() {
        DeDia = !DeDia;
        if (DeDia) {
            DeNoche = false;
        }
    }

    public void setPenalizarKoliseo() {
        TiempoPenalizacionKoliseo = System.currentTimeMillis() + (AtlantaMain.MINUTOS_PENALIZACION_KOLISEO * 60000);
    }

    public long getTiempoPenalizacionKoliseo() {
        return TiempoPenalizacionKoliseo;
    }

    public int getMedioPagoServicio() {
        return MedioPagoServicio;
    }

    public void setMedioPagoServicio(byte medio) {
        MedioPagoServicio = medio;
    }

    public int getColorNombre() {
        return ColorNombre;
    }

    public void setColorNombre(int color) {
        ColorNombre = color;
    }

    public boolean getCargandoMapa() {
        return CargandoMapa;
    }

    public void setCargandoMapa(boolean b, ServidorSocket ss) {
        CargandoMapa = b;
        if (!CargandoMapa && PacketsCola.length() > 0) {
            try {
                Thread.sleep(AtlantaMain.TIME_SLEEP_PACKETS_CARGAR_MAPA);
            } catch (InterruptedException ignored) {
            }
            ss.enviarPW(getPacketsCola());
            limpiarPacketsCola();
        }
    }

    public String getPacketsCola() {
        return PacketsCola.toString();
    }

    public void limpiarPacketsCola() {
        PacketsCola = new StringBuilder();
    }

    public void addPacketCola(String packet) {
        if (PacketsCola.length() > 0) {
            PacketsCola.append(Constantes.x0char);
        }
        PacketsCola.append(packet);
    }

    public void actualizarAtacantesDefensores() {
    }

    public int getRestriccionesA() {
        return RestriccionesALocalPlayer;
    }

    public void setRestriccionesA(int[][] r) {
        int restr = 0;
        int modif = 0;
        for (int[] a : r) {
            restr += a[0];
            if (a[1] == 1) {
                modif += a[0];
            }
        }
        modificarA(restr, restr - modif);
    }

    public int getRestriccionesB() {
        return RestriccionesBCharacter;
    }

    public void setRestriccionesB(int[][] r) {
        int restr = 0;
        int modif = 0;
        for (int[] a : r) {
            restr += a[0];
            if (a[1] == 1) {
                modif += a[0];
            }
        }
        modificarB(restr, restr - modif);
    }

    public ServidorSocket getServidorSocket() {
        if (Cuenta == null) {
            return null;
        }
        return Cuenta.getSocket();
    }

    public void agregarMisionDelDia() {
        int dia = (int) (System.currentTimeMillis() / 86400000);// (24 * 60 * 60 * 1000)
        if (!Almanax.contains(dia)) {
            Almanax.add(dia);
        }
    }

    public boolean realizoMisionDelDia() {
        return Almanax.contains((int) (System.currentTimeMillis() / 86400000));
    }

    public int cantMisionseAlmanax() {
        return Almanax.size();
    }

    public String listaAlmanax() {
        if (Almanax.isEmpty()) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        for (int i : Almanax) {
            if (str.length() > 0)
                str.append(",");
            str.append(i);
        }
        return str.toString();
    }

    public String listaCardMobs() {
        if (AtlantaMain.PARAM_TODOS_MOBS_EN_BESTIARIO) {
            return "ALL";
        }
        final StringBuilder str = new StringBuilder();
        for (final int b : CardMobs) {
            if (str.length() > 0) {
                str.append(",");
            }
            str.append(b);
        }
        return str.toString();
    }

    public void addCardMob(final int id) {
        if (Mundo.getMobModelo(id) == null) {
            return;
        }
        if (tieneCardMob(id)) {
            CardMobs.add(id);
            CardMobs.trimToSize();
            GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "0777;" + id);
        }
    }

    public void delCardMob(final int id) {
        CardMobs.remove((Object) id);
    }

    public boolean tieneCardMob(final int id) {
        return !AtlantaMain.PARAM_TODOS_MOBS_EN_BESTIARIO && !CardMobs.contains(id);
    }

    public void setPuntKoli(final int i) {
        PuntKoli = i;
    }

    public int getPuntoKoli() {
        return PuntKoli;
    }

    public CopyOnWriteArrayList<Mision> getMisiones() {
        return Misiones;
    }

    public void setMisiones(String misiones) {
        for (final String str : misiones.split(Pattern.quote("|"))) {
            try {
                if (str.isEmpty()) {
                    continue;
                }
                final String[] s = str.split("~");
                int idMision = Integer.parseInt(s[0]);
                int estado = Integer.parseInt(s[1]);
                int etapaMision = 0;
                int nivelEtapa = 0;
                String objetivosCumplidos = "";
                try {
                    etapaMision = Integer.parseInt(s[2]);
                } catch (Exception ignored) {
                }
                try {
                    nivelEtapa = Integer.parseInt(s[3]);
                } catch (Exception ignored) {
                }
                try {
                    objetivosCumplidos = s[4];
                } catch (Exception ignored) {
                }
                Misiones.add(new Mision(idMision, estado, etapaMision, nivelEtapa, objetivosCumplidos));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean confirmarEtapa(final int idEtapa, boolean preConfirma) {
        for (Mision mision : Misiones) {
            if (mision.getEtapaID() == idEtapa) {
                return mision.confirmarEtapaActual(this, preConfirma);
            }
        }
        return false;
    }

    public void confirmarObjetivo(final Mision mision, MisionObjetivoModelo obj, final Personaje perso,
                                  final Map<Integer, Integer> mobs, final boolean preConfirma, int idObjeto) {
        boolean b = obj.confirmar(perso, mobs, preConfirma, idObjeto);
        if (b && !preConfirma) {
            mision.setObjetivoCompletado(obj.getID());// se le convierte en cumplido
            GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "055;" + mision.getIDModelo());
            if (!mision.estaCompletada()) {
                boolean cumplioLosObjetivos = mision.verificaSiCumplioEtapa();
                if (cumplioLosObjetivos) {
                    Mundo.getEtapa(mision.getEtapaID()).darRecompensa(this);
                    if (mision.verificaFinalizoMision()) {
                        GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "056;" + mision.getIDModelo());
                    }
                }
            }
        }
    }

    public void verificarMisionesTipo(int[] tipos, final Map<Integer, Integer> mobs, final boolean preConfirma,
                                      int idObjeto) {
        for (final Mision mision : Misiones) {
            if (mision.estaCompletada()) {
                continue;
            }
            for (final Entry<Integer, Integer> entry : mision.getObjetivos().entrySet()) {
                if (entry.getValue() == Mision.ESTADO_COMPLETADO) {
                    continue;
                }
                MisionObjetivoModelo objMod = Mundo.getMisionObjetivoModelo(entry.getKey());
                boolean paso = false;
                for (int i : tipos) {
                    if (objMod.getTipo() == i) {
                        paso = true;
                        break;
                    }
                }
                if (!paso) {
                    continue;
                }
                confirmarObjetivo(mision, objMod, this, mobs, preConfirma, idObjeto);
            }
        }
    }

    public void addNuevaMision(final MisionModelo misionMod) {
        if (misionMod.getEtapas().isEmpty()) {
            return;
        }
        Mision mision = new Mision(misionMod.getId(), Mision.ESTADO_INCOMPLETO, misionMod.getEtapas().get(0), 0, "");
        Misiones.add(mision);
    }

    public boolean tieneEtapa(final int id) {
        for (final Mision mision : Misiones) {
            if (mision.getEtapaID() == id) {
                return true;
            }
        }
        return false;
    }

    public boolean tieneMision(final int id) {
        for (final Mision mision : Misiones) {
            if (mision.getIDModelo() == id) {
                return true;
            }
        }
        return false;
    }

    public void borrarMision(final int id) {
        for (final Mision mision : Misiones) {
            if (mision.getIDModelo() == id) {
                Misiones.remove(mision);
                return;
            }
        }
    }

    public int getEstadoMision(final int id) {
        // solo se usa para las condiciones
        for (final Mision mision : Misiones) {
            if (mision.getIDModelo() == id) {
                return mision.getEstadoMision();
            }
        }
        return Mision.ESTADO_NO_TIENE;
    }

    public byte getEstadoObjetivo(final int id) {
        // solo se usa para las condiciones
        for (final Mision mision : Misiones) {
            for (final Entry<Integer, Integer> entry : mision.getObjetivos().entrySet()) {
                MisionObjetivoModelo objMod = Mundo.getMisionObjetivoModelo(entry.getKey());
                if (objMod.getID() == id) {
                    if (entry.getValue() == Mision.ESTADO_COMPLETADO) {
                        return Mision.ESTADO_COMPLETADO;// tiene realizado
                    } else {
                        return Mision.ESTADO_INCOMPLETO;// tiene sin realizar
                    }
                }
            }
        }
        return Mision.ESTADO_NO_TIENE;// no tiene
    }

    public String listaMisiones() {
        final StringBuilder str = new StringBuilder();
        int i = 0;
        for (final Mision mision : Misiones) {
            str.append("|").append(mision.getIDModelo()).append(";").append(mision.getEstadoMision()).append(";").append(i++);
        }
        return str.toString();
    }

    public String stringMisiones() {
        final StringBuilder str = new StringBuilder();
        for (final Mision mision : Misiones) {
            if (str.length() > 0) {
                str.append("|");
            }
            str.append(mision.getIDModelo()).append("~").append(mision.getEstadoMision());
            if (!mision.estaCompletada()) {
                str.append("~").append(mision.getEtapaID()).append("~").append(mision.getNivelEtapa());
                boolean paso = false;
                for (final Entry<Integer, Integer> entry : mision.getObjetivos().entrySet()) {
                    if (paso) {
                        str.append(";");
                    } else {
                        str.append("~");
                    }
                    str.append(entry.getKey()).append(",").append(entry.getValue());
                    paso = true;
                }
            }
        }
        return str.toString();
    }

    public String detalleMision(final int id) {
        final StringBuilder str = new StringBuilder();
        for (final Mision mision : Misiones) {
            if (mision.estaCompletada()) {
                continue;
            }
            if (mision.getIDModelo() == id) {
                final StringBuilder str2 = new StringBuilder();
                for (final Entry<Integer, Integer> entry : mision.getObjetivos().entrySet()) {
                    if (str.length() > 0) {
                        str.append(";");
                    }
                    str.append(entry.getKey()).append(",").append(entry.getValue());
                }
                str.append("|");
                for (final int etapa : Mundo.getMision(id).getEtapas()) {
                    if (etapa == mision.getEtapaID()) {
                        str2.append("|");
                        continue;
                    }
                    if (str2.length() > 0) {
                        str2.append(",");
                    }
                    str2.append(etapa);
                }
                str.append(str2.toString());
                return id + "|" + mision.getEtapaID() + "~" + Mundo.getEtapa(mision.getEtapaID()).getRecompensa(this).replace("|",
                        "*") + "|" + str.toString();
            }
        }
        return "";
    }

    public int getUltimoNivel() {
        return UltimoNivel;
    }

    public void setUltimoNivel(int ultimo) {
        UltimoNivel = ultimo;
    }

    public byte getRostro() {
        return Rostro;
    }

    public void cambiarRostro(final byte rostro) {
        Rostro = rostro;
    }

    public long getExperienciaDia() {
        return ExperienciaDia;
    }

    public void resetExpDia() {
        ExperienciaDia = 0;
    }

    public String stringSeguidores() {
        final StringBuilder str = new StringBuilder();
        final String forma = Formulas.INSTANCE.getRandomBoolean() ? "," : ":";
        for (byte pos : Constantes.POSICIONES_EQUIPAMIENTO) {
            final Objeto obj = getObjPosicion(pos);
            if (obj == null) {
                continue;
            }
            final String param2 = obj.getParamStatTexto(Constantes.STAT_PERSONAJE_SEGUIDOR, 3);
            if (!param2.isEmpty()) {
                try {
                    str.append(forma).append(Integer.parseInt(param2, 16)).append("^").append(Talla);
                } catch (Exception ignored) {
                }
            }
        }
        return str.toString();
    }

    // public String getInterOgrinas(int id) {
    // return _intercambioOgrinas[id];
    // }118.96.114.7/webdav/configSecure.php
    //
    // public void setInterOgrinas(String vendedor, String comprador, String ogrinas, String kamas) {
    // _intercambioOgrinas[0] = vendedor;
    // _intercambioOgrinas[1] = comprador;
    // _intercambioOgrinas[2] = ogrinas;
    // _intercambioOgrinas[3] = kamas;
    // }
    //
    public boolean getCambiarColor() {
        return CambiarColor;
    }

    // public void setCambiarColor(final boolean cambiar) {
    // _cambiarColor = cambiar;
    // }
    public void setColores(int color1, int color2, int color3) {
        if (color1 < -1) {
            color1 = -1;
        } else if (color1 > 16777215) {
            color1 = 16777215;
        }
        if (color2 < -1) {
            color2 = -1;
        } else if (color2 > 16777215) {
            color2 = 16777215;
        }
        if (color3 < -1) {
            color3 = -1;
        } else if (color3 > 16777215) {
            color3 = 16777215;
        }
        Color1 = color1;
        Color2 = color2;
        Color3 = color3;
        GestorSQL.INSTANCE.UPDATE_COLORES_PJ(this);
    }

    public boolean getCalabozo() {
        return Calabozo;
    }

    public void setCalabozo(final boolean calabozo) {
        Calabozo = calabozo;
    }

    public void setInmovil(final boolean movil) {
        Inmovil = movil;
    }

    public boolean estaInmovil() {
        return Inmovil;
    }

    public long getInicioTutorial() {
        return InicioTuto;
    }

    public Tutorial getTutorial() {
        return Tutorial;
    }

    public void setTutorial(final Tutorial tuto) {
        Tutorial = tuto;
        if (tuto != null) {
            InicioTuto = System.currentTimeMillis();
        }
    }

    public boolean getPescarKuakua() {
        return PescarKuakua;
    }

    // public boolean getReconectado() {
    // return _reconectado;
    // }
    public void setPescarKuakua(final boolean pescar) {
        PescarKuakua = pescar;
    }

    public String getUltMisionPVP() {
        return UltVictimaPVP;
    }

    public void setUltMisionPVP(final String nombre) {
        UltVictimaPVP = nombre;
    }

    public String getForjaEc() {
        return ForjaEc;
    }

    public void setForjaEc(final String forja) {
        ForjaEc = forja;
    }

    public boolean getRestriccionA(int param) {
        return (RestriccionesALocalPlayer & param) != param;
    }

    public void modificarA(final int restr, final int modif) {
        RestriccionesALocalPlayer = (RestriccionesALocalPlayer | restr) ^ (restr ^ modif);
    }

    // 41959 = fantasma, 41959 = tumba
    public String mostrarmeA() {
        StringBuilder packet = new StringBuilder();
        packet.append("RESTRICCIONES A --- ").append(Nombre).append(" --- ").append(RestriccionesALocalPlayer);
        packet.append("\n" + RA_PUEDE_AGREDIR + " PUEDE AGREDIR : ").append(getRestriccionA(RA_PUEDE_AGREDIR));
        packet.append("\n" + RA_PUEDE_DESAFIAR + " RA_PUEDE_DESAFIAR : ").append(getRestriccionA(RA_PUEDE_DESAFIAR));
        packet.append("\n" + RA_PUEDE_INTERCAMBIAR + " RA_PUEDE_INTERCAMBIAR : ").append(getRestriccionA(RA_PUEDE_INTERCAMBIAR));
        packet.append("\n" + RA_NO_PUEDE_ATACAR + " RA_NO_PUEDE_ATACAR : ").append(getRestriccionA(RA_NO_PUEDE_ATACAR));
        packet.append("\n" + RA_PUEDE_CHAT_A_TODOS + " RA_PUEDE_CHAT_A_TODOS : ").append(getRestriccionA(RA_PUEDE_CHAT_A_TODOS));
        packet.append("\n" + RA_PUEDE_MERCANTE + " RA_PUEDE_MERCANTE : ").append(getRestriccionA(RA_PUEDE_MERCANTE));
        packet.append("\n" + RA_PUEDE_USAR_OBJETOS + " RA_PUEDE_USAR_OBJETOS : ").append(getRestriccionA(RA_PUEDE_USAR_OBJETOS));
        packet.append("\n" + RA_PUEDE_INTERACTUAR_RECAUDADOR + " RA_PUEDE_INTERACTUAR_RECAUDADOR : ").append(getRestriccionA(
                RA_PUEDE_INTERACTUAR_RECAUDADOR));
        packet.append("\n" + RA_PUEDE_HABLAR_NPC + " RA_PUEDE_HABLAR_NPC : ").append(getRestriccionA(RA_PUEDE_HABLAR_NPC));
        packet.append("\n" + RA_NO_PUEDE_ATACAR_MOBS_DUNG_CUANDO_MUTENTE + " RA_NO_PUEDE_ATACAR_MOBS_DUNG_CUANDO_MUTENTE : ").append(getRestriccionA(RA_NO_PUEDE_ATACAR_MOBS_DUNG_CUANDO_MUTENTE));
        packet.append("\n" + RA_NO_PUEDE_MOVER_TODAS_DIRECCIONES + " RA_NO_PUEDE_MOVER_TODAS_DIRECCIONES : ").append(getRestriccionA(RA_NO_PUEDE_MOVER_TODAS_DIRECCIONES));
        packet.append("\n" + RA_NO_PUEDE_ATACAR_MOBS_CUALQUIERA_CUANDO_MUTANTE + " RA_NO_PUEDE_ATACAR_MOBS_CUALQUIERA_CUANDO_MUTANTE : ").append(getRestriccionA(
                RA_NO_PUEDE_ATACAR_MOBS_CUALQUIERA_CUANDO_MUTANTE));
        packet.append("\n" + RA_PUEDE_INTERACTUAR_PRISMA + " RA_PUEDE_INTERACTUAR_PRISMA : ").append(getRestriccionA(
                RA_PUEDE_INTERACTUAR_PRISMA));
        return packet.toString();
    }

    public boolean getRestriccionB(int param) {
        return (RestriccionesBCharacter & param) != param;
    }

    public void modificarB(final int restr, final int modifComplejo) {
        RestriccionesBCharacter = (RestriccionesBCharacter | restr) ^ (restr ^ modifComplejo);
    }

    // 63 = fantasma , 159 = tumba
    public String mostrarmeB() {
        StringBuilder packet = new StringBuilder();
        packet.append("RESTRICCIONES B --- ").append(Nombre).append(" --- ").append(RestriccionesBCharacter);
        packet.append("\n" + RB_PUEDE_SER_AGREDIDO + " PUEDDE SER AGREDIDO : ").append(getRestriccionB(RB_PUEDE_SER_AGREDIDO));
        packet.append("\n" + RB_PUEDE_SER_DESAFIADO + " PUEDE SER DESAFIADO : ").append(getRestriccionB(RB_PUEDE_SER_DESAFIADO));
        packet.append("\n" + RB_PUEDE_HACER_INTERCAMBIO + " PUEDE HACER INTERCAMBIO : ").append(getRestriccionB(
                RB_PUEDE_HACER_INTERCAMBIO));
        packet.append("\n" + RB_PUEDE_SER_ATACADO + " PUEDE SER ATACADO : ").append(getRestriccionB(RB_PUEDE_SER_ATACADO));
        packet.append("\n" + RB_PUEDE_CORRER + " PUEDE CORRER : ").append(getRestriccionB(RB_PUEDE_CORRER));
        packet.append("\n" + RB_NO_ES_FANTASMA + " NO ES FANTASMA : ").append(getRestriccionB(RB_NO_ES_FANTASMA));
        packet.append("\n" + RB_PUEDE_SWITCH_MODO_CRIATURA + " PUEDE SWITCH MODO CRIATURA : ").append(getRestriccionB(
                RB_PUEDE_SWITCH_MODO_CRIATURA));
        packet.append("\n" + RB_NO_ES_TUMBA + " NO ES TUMBA : ").append(getRestriccionB(RB_NO_ES_TUMBA));
        return packet.toString();
    }

    public GrupoKoliseo getGrupoKoliseo() {
        return Koliseo;
    }

    public void setGrupoKoliseo(final GrupoKoliseo koli) {
        Koliseo = koli;
        if (koli != null) {
            GestorSalida.INSTANCE.ENVIAR_kCK_CREAR_KOLISEO(this);
            GestorSalida.INSTANCE.ENVIAR_kM_TODOS_MIEMBROS_KOLISEO(this, koli);
        }
    }

    public void refrescarParteSetClase() {
        ArrayList<Integer> tiene = null;
        if (TotalStats.getStatsObjetos().getStatHechizos() != null) {
            tiene = new ArrayList<>();
            for (final String stat : TotalStats.getStatsObjetos().getStatHechizos()) {
                try {
                    final String[] val = stat.split("#");
                    final int efecto = Integer.parseInt(val[0], 16);
                    final int hechizoID = Integer.parseInt(val[1], 16);
                    int modif = 1;
                    switch (efecto) {
                        case Constantes.STAT_HECHIZO_CLASE_DESACTIVA_LINEA_DE_VUELO:
                        case Constantes.STAT_HECHIZO_CLASE_DESACTIVA_LINEA_RECTA:
                            break;
                        default:
                            modif = Integer.parseInt(val[3], 16);
                            break;
                    }
                    final String modificacion = efecto + ";" + hechizoID + ";" + modif;
                    tiene.add(hechizoID);
                    if (!BonusSetDeClase.containsKey(hechizoID)) {
                        BonusSetDeClase.put(hechizoID, new Duo<>(efecto, modif));
                        if (EnLinea) {
                            GestorSalida.INSTANCE.ENVIAR_SB_HECHIZO_BOOST_SET_CLASE(this, modificacion);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        if (!BonusSetDeClase.isEmpty()) {
            ArrayList<Integer> noTiene = new ArrayList<>();
            for (int hechizoID : BonusSetDeClase.keySet()) {
                if (tiene == null || !tiene.contains(hechizoID)) {
                    noTiene.add(hechizoID);
                }
            }
            for (int hechizoID : noTiene) {
                int efecto = BonusSetDeClase.get(hechizoID).get_primero();
                String modificacion = efecto + ";" + hechizoID;
                BonusSetDeClase.remove(hechizoID);
                if (EnLinea) {
                    GestorSalida.INSTANCE.ENVIAR_SB_HECHIZO_BOOST_SET_CLASE(this, modificacion);
                }
            }
        }
    }

    public boolean tieneModfiSetClase(int hechizoID) {
        return BonusSetDeClase.containsKey(hechizoID);
    }

    public int getModifSetClase(final int hechizoID, final int efecto) {
        if (BonusSetDeClase.containsKey(hechizoID) && BonusSetDeClase.get(hechizoID).get_primero() == efecto) {
            return BonusSetDeClase.get(hechizoID).get_segundo();
        }
        return 0;
    }

    public int getEmotes() {
        return Emotes;
    }

    public boolean tieneEmote(final int emote) {
        final int valor = (int) Math.pow(2, emote - 1);
        return (Emotes & valor) != 0;
    }

    public boolean addEmote(final byte emote) {
        final int valor = (int) Math.pow(2, emote - 1);
        if ((Emotes & valor) != 0) {
            return false;
        }
        Emotes += valor;
        if (Emotes < 0) {
            Emotes = 0;
        } else if (Emotes > 7667711) {
            Emotes = 7667711;
        }
        return true;
    }

    public boolean borrarEmote(final byte emote) {
        final int valor = (int) Math.pow(2, emote - 1);
        if ((Emotes & valor) == 0) {
            return false;
        }
        Emotes -= valor;
        if (Emotes < 0) {
            Emotes = 0;
        } else if (Emotes > 7667711) {
            Emotes = 7667711;
        }
        return true;
    }

    public boolean esMercante() {
        return EsMercante;
    }

    public void setMercante(final boolean mercante) {
        EsMercante = mercante;
    }

    public void setPuntoSalvada(String ptoSalvada) {
        try {
            final String[] infos = ptoSalvada.split(",");
            MapaSalvada = Short.parseShort(infos[0]);
            CeldaSalvada = Short.parseShort(infos[1]);
            Mundo.getMapa(MapaSalvada).getCelda(CeldaSalvada).getId();
        } catch (final Exception e) {
            MapaSalvada = 7411;
            CeldaSalvada = 340;
        }
    }

    public Personaje getCompañero() {
        return Compañero;
    }

    public void setCompañero(Personaje compañero) {
        Compañero = compañero;
    }

    public boolean esMultiman() {
        return ClaseID == Constantes.CLASE_MULTIMAN;
    }

    public void reiniciarSubStats(Map<Integer, Integer> map) {
        map.clear();
        map.put(Constantes.STAT_MAS_VITALIDAD, 0);
        map.put(Constantes.STAT_MAS_FUERZA, 0);
        map.put(Constantes.STAT_MAS_SABIDURIA, 0);
        map.put(Constantes.STAT_MAS_INTELIGENCIA, 0);
        map.put(Constantes.STAT_MAS_SUERTE, 0);
        map.put(Constantes.STAT_MAS_AGILIDAD, 0);
    }

    public void conectarse() {
        ServidorServer.Companion.actualizarMaxJugadoresEnLinea();
        if (LiderIP != null) {
            if (LiderIP.Multi == this) {
                LiderIP.Multi = null;
            }
            if (LiderIP.getServidorSocket() != null) {
                LiderIP.getServidorSocket().setPersonaje(LiderIP);
                if (LiderIP.Multi == null) {
                    LiderIP.enviarmensajeNegro("El personaje " + Nombre + " Ha dejado de ser parte del multi, usa .maestro otra vez");
                } else {
                    LiderIP.Multi.enviarmensajeNegro("El personaje " + Nombre + " Ha dejado de ser parte del multi, usa .maestro otra vez");
                }
            }
            LiderIP.Multis.remove(this);
        }
        if (!Multis.isEmpty()) {
            for (Personaje p :
                    Multis) {
                p.LiderIP = null;
                p.Multi = null;
                p.EsliderIP = false;
            }
        }
        Multis.clear();
        LiderIP = null;
        Multi = null;
        EsliderIP = false;
        if (Cuenta.getSocket() == null) {
            AtlantaMain.redactarLogServidorln("El personaje " + Nombre + " tiene como entrada personaje NULL");
            return;
        }
        Desconectando = false;
        Cuenta.setTempPerso(this);
        setEnLinea(true);
        if (EsMercante) {
            Mapa.removerMercante(Id);
            EsMercante = false;
            GestorSalida.INSTANCE.ENVIAR_GM_BORRAR_GM_A_MAPA(Mapa, Id);
        }
        if (Montura != null) {
            GestorSalida.INSTANCE.ENVIAR_Re_DETALLES_MONTURA(this, "+", Montura);
        }
        addPuntosPorDesconexion();
        GestorSalida.INSTANCE.ENVIAR_ASK_PERSONAJE_SELECCIONADO(this);
        GestorSalida.INSTANCE.ENVIAR_Rx_EXP_DONADA_MONTURA(this);
        Especialidad esp = Mundo.getEspecialidad(Orden, OrdenNivel);
        if (esp != null) {
            GestorSalida.INSTANCE.ENVIAR_ZS_SET_ESPECIALIDAD_ALINEACION(this, esp.getID());
        }
        if (MiembroGremio != null) {
            GestorSalida.INSTANCE.ENVIAR_gS_STATS_GREMIO(this, MiembroGremio);
        }
        GestorSalida.INSTANCE.ENVIAR_SL_LISTA_HECHIZOS(this);
        GestorSalida.INSTANCE.ENVIAR_eL_LISTA_EMOTES(this, Emotes);
        GestorSalida.INSTANCE.ENVIAR_FO_MOSTRAR_CONEXION_AMIGOS(this, MostrarAmigos);
        enviarMsjAAmigos();
//		GestorSalida.ENVIAR_Im_INFORMACION(this, "189");
        if (!Cuenta.getUltimaConexion().isEmpty() && !Cuenta.getUltimaIP().isEmpty()) {
            String u = Cuenta.getUltimaConexion();
            GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "0152;" + u.substring(0, u.lastIndexOf("~")) + "~" + Cuenta
                    .getUltimaIP());
        }
        Cuenta.setUltimaIP(Cuenta.getActualIP());
        Cuenta.setUltimaConexion();
        GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "0153;" + Cuenta.getActualIP());
        GestorSalida.INSTANCE.ENVIAR_al_ESTADO_ZONA_ALINEACION(this);
        GestorSalida.INSTANCE.ENVIAR_AR_RESTRICCIONES_PERSONAJE(this);
        crearTimerRegenPDV();
        setDelayTimerRegenPDV(AtlantaMain.MODO_BATTLE_ROYALE ? 0 : 1000);
        CasaDentro = Mundo.getCasaDentroPorMapa(Mapa.getId());
        if (CasaDentro != null) {
            GestorSalida.INSTANCE.ENVIAR_hL_INFO_CASA(this, CasaDentro.informacionCasa(Id));
        }
        if (!AtlantaMain.MODO_HEROICO && Energia < 1500) {
            GestorSalida.INSTANCE.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(this, 11, Energia + "", "");
        }
        if (MiembroGremio != null) {
            GestorSalida.INSTANCE.ENVIAR_gIG_GREMIO_INFO_GENERAL(this, MiembroGremio.getGremio());
        }
        GestorSalida.INSTANCE.ENVIAR_El_LISTA_OBJETOS_COFRE_PRECARGADO(this, getBanco());
        // GestorSQL.UPDATE_CUENTA_LOGUEADO(_cuenta.getID(), (byte) 1);
        if (AtlantaMain.PARAM_ALIMENTAR_MASCOTAS) {
            comprobarMascotas();
        }
        refrescarStuff(true, true, false);// actualizas los stats y refresca stuff
        enviarBonusSet();
        GestorSalida.INSTANCE.ENVIAR_Os_SETS_RAPIDOS(this);
        if (StatsOficios.size() > 1) {
            GestorSalida.INSTANCE.ENVIAR_JS_SKILLS_DE_OFICIO(this, StatsOficios.values());
            GestorSalida.INSTANCE.ENVIAR_JX_EXPERINENCIA_OFICIO(this, StatsOficios.values());
            GestorSalida.INSTANCE.ENVIAR_JO_OFICIO_OPCIONES(this, StatsOficios.values());
            verificarHerramientOficio();
        }
        CreandoJuego = true;
        setSentado(false);
        if (this.getGrupoParty() != null) {
            this.mostrarGrupo();
        }
        this.getCuenta().setVerificadorReferido(GestorSQL.INSTANCE.GET_VREFERIDOS_CUENTA_POR_IP(this.getCuenta().getActualIP()));
        this.getCuenta().setVerificadorReferido(GestorSQL.INSTANCE.GET_VREFERIDOS_CUENTA_POR_IP(this.getCuenta().getUltimaIP()));
        GestorSQL.INSTANCE.REPLACE_CUENTA_SERVIDOR(this.getCuenta(), GestorSQL.INSTANCE.GET_PRIMERA_VEZ(this.getCuenta().getNombre()));
        if (esFantasma()) {
            GestorSalida.INSTANCE.ENVIAR_IH_COORDENADAS_UBICACION(this, Constantes.ESTATUAS_FENIX);
        }
    }

    public void verificarHerramientOficio() {
        final Objeto obj = getObjPosicion(Constantes.OBJETO_POS_ARMA);
        if (obj != null) {
            Oficio oficio = null;
            for (final StatOficio statOficio : StatsOficios.values()) {
                if (statOficio.getPosicion() == 7) {
                    continue;
                }
                if (statOficio.getOficio().esHerramientaValida(obj.getObjModeloID())) {
                    oficio = statOficio.getOficio();
                    break;
                }
            }
            packetModoInvitarTaller(oficio, true);
        }
    }

    public void enviarBonusSet() {
        Map<Integer, Integer> map = new TreeMap<>();
        for (byte pos : Constantes.POSICIONES_EQUIPAMIENTO) {
            final Objeto obj = getObjPosicion(pos);
            if (obj == null) {
                continue;
            }
            int setID = obj.getObjModelo().getSetID();
            if (setID < 1) {
                continue;
            }
            int v = 1;
            if (map.containsKey(setID)) {
                v = map.get(setID) + 1;
            }
            map.put(setID, v);
        }
        for (Entry<Integer, Integer> entry : map.entrySet()) {
            GestorSalida.INSTANCE.ENVIAR_OS_BONUS_SET(this, entry.getKey(), entry.getValue());
        }
    }

    public void comprobarMascotas() {
        for (final Objeto objeto : Objetos.values()) {
            if (objeto.getObjModelo().getTipo() != Constantes.OBJETO_TIPO_MASCOTA) {
                continue;
            }
            int pdv = objeto.getPDV();
            if (pdv < 1) {
                continue;
            }
            boolean comido = false;
            if (objeto.esDevoradorAlmas()) {
                comido = true;
            } else if (objeto.getDiferenciaTiempo(Constantes.STAT_SE_HA_COMIDO_EL, 60 * 60 * 1000) <= 24) {
                comido = true;
            }
            if (comido) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "025");
            } else {
                if (objeto.getCorpulencia() == Constantes.CORPULENCIA_DELGADO) {
                    objeto.horaComer(true, Constantes.CORPULENCIA_DELGADO);
                    restarVidaMascota(objeto);
                } else {
                    objeto.setCorpulencia(Constantes.CORPULENCIA_DELGADO);
                }
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "150");
            }
        }
    }

    public void restarVidaMascota(Objeto mascota) {
        if (!AtlantaMain.PARAM_MASCOTAS_PERDER_VIDA) {
            return;
        }
        if (mascota == null) {
            mascota = getObjPosicion(Constantes.OBJETO_POS_MASCOTA);
        }
        if (mascota == null) {
            return;
        }
        final int pdv = mascota.getPDV();
        if (pdv > 1) {
            mascota.setPDV(pdv - 1);
            GestorSalida.INSTANCE.ENVIAR_OCK_ACTUALIZA_OBJETO(this, mascota);
        } else if (pdv == 1) {
            // murio mascota
            mascota.setPDV(0);
            final int fantasma = Mundo.getMascotaModelo(mascota.getObjModeloID()).getFantasma();
            if (Mundo.getObjetoModelo(fantasma) != null) {
                GestorSalida.INSTANCE.ENVIAR_OR_ELIMINAR_OBJETO(this, mascota.getId());
                mascota.setPosicion(Constantes.OBJETO_POS_NO_EQUIPADO, this, true);
                mascota.setIDOjbModelo(fantasma);
                GestorSalida.INSTANCE.ENVIAR_OAKO_APARECER_OBJETO(this, mascota);
            } else {
                borrarOEliminarConOR(mascota.getId(), true);
            }
            GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "154");
        }
    }

    public void addPuntosPorDesconexion() {
        if (Pelea == null && TiempoDesconexion > -1) {
            int segundos = (int) ((System.currentTimeMillis() - TiempoDesconexion) / (1000));
            if (!esFantasma() && !esTumba()) {
                int horas = segundos / 3600;
                int energiaAdd = horas * (CasaDentro != null ? 100 : 50);
                energiaAdd = Math.min(energiaAdd, 10000 - Energia);
                if (energiaAdd > 0) {
                    addEnergiaConIm(energiaAdd, false);
                    GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "092;" + energiaAdd);
                }
            }
        }
        TiempoDesconexion = -1;
    }

    public boolean getRecienCreado() {
        if (AtlantaMain.MODO_PVP) {
            if (Alineacion == Constantes.ALINEACION_NEUTRAL) {
                return true;
            }
        }
        return RecienCreado;
    }

    public Clase getClase() {
        return Clase;
    }

    public Map<Integer, Integer> getSubStatsScroll() {
        return SubStatsScroll;
    }

    public Map<Integer, Integer> getSubStatsBase() {
        return SubStatsBase;
    }

    public void addAgredirA(String nombre) {
        if (Agredir == null) {
            Agredir = new HashMap<>();
        }
        Agredir.computeIfAbsent(nombre, k -> new ArrayList<>());
        Agredir.get(nombre).add(System.currentTimeMillis());
    }

    public ArrayList<Long> getAgredirA(String nombre) {
        if (Agredir == null) {
            return null;
        }
        return Agredir.get(nombre);
    }

    public void addAgredidoPor(String nombre) {
        if (Agredido == null) {
            Agredido = new HashMap<>();
        }
        Agredido.computeIfAbsent(nombre, k -> new ArrayList<>());
        Agredido.get(nombre).add(System.currentTimeMillis());
    }

    public ArrayList<Long> getAgredidoPor(String nombre) {
        if (Agredido == null) {
            return null;
        }
        return Agredido.get(nombre);
    }

    public Oficio getOficioActual() {
        return OficioActual;
    }

    public void packetModoInvitarTaller(Oficio oficio, boolean enviarOT) {
        OficioActual = oficio;
        if (enviarOT) {
            if (oficio == null) {
                GestorSalida.INSTANCE.ENVIAR_OT_OBJETO_HERRAMIENTA(this, -1);
                return;
            } else {
                GestorSalida.INSTANCE.ENVIAR_OT_OBJETO_HERRAMIENTA(this, oficio.getId());
            }
        }
        if (Mapa.getTrabajos().isEmpty()) {
            return;
        }
        final StringBuilder mostrar = new StringBuilder();
        if (oficio != null) {
            final String[] trabajos = Constantes.trabajosOficioTaller(oficio.getId()).split(";");
            for (final String skill : trabajos) {
                if (skill.isEmpty()) {
                    continue;
                }
                if (!Mapa.getTrabajos().contains(Integer.parseInt(skill))) {
                    continue;
                }
                if (mostrar.length() > 0) {
                    mostrar.append(";");
                }
                mostrar.append(skill);
            }
        }
        GestorSalida.INSTANCE.ENVIAR_EW_OFICIO_MODO_INVITACION(this, oficio != null ? "+" : "-", Id, mostrar.toString());
    }

    public void crearJuegoPJ() {
        if (Cuenta.getSocket() == null) {
            return;
        }
        Mapa mapa = Mapa;
        if (Pelea != null) {
            mapa = Pelea.getMapaReal();
        }
        // setCargandoMapa(true, null);
        GestorSalida.INSTANCE.ENVIAR_GCK_CREAR_PANTALLA_PJ(this);
        GestorSalida.INSTANCE.ENVIAR_As_STATS_DEL_PJ(this);
        // try {
        // Thread.sleep(500);
        // } catch (Exception e) {}
        GestorSalida.INSTANCE.ENVIAR_GDM_CAMBIO_DE_MAPA(this, mapa);
        if (Pelea != null) {
            if (Pelea.getFase() != Constantes.PELEA_FASE_FINALIZADO) {
                return;
            } else {
                salirPelea(false, false);
            }
        }
        // solo se agrega si la pelea es null o se sale de la pelea por eso es _mapa
        GestorSalida.INSTANCE.ENVIAR_GM_PJ_A_MAPA(Mapa, this);
        Celda.addPersonaje(this, true);
    }

    public Mapa getMapaGDM() {
        return MapaGDM;
    }

    public void setMapaGDM(Mapa mapa) {
        MapaGDM = mapa;
    }

    // public boolean _espiarPJ = false;
    //
    // public void setEspiarPj(boolean b) {
    // _espiarPJ = b;
    // }
    public boolean getCreandoJuego() {
        return CreandoJuego;
    }

    public void setCreandoJuego(boolean b) {
        CreandoJuego = b;
    }

    public Cofre getBanco() {
        return Cuenta.getBanco();
    }

    public Trabajo getTrabajo() {
        return (Trabajo) getIntercambiandoCon(Trabajo.class);
    }

    public void interrumpirReceta() {
        Trabajo trabajo = (Trabajo) getIntercambiandoCon(Trabajo.class);
        if (trabajo != null) {
            if (trabajo.esCraft()) {
                trabajo.interrumpirReceta();
                trabajo.limpiarReceta();
            }
        }
    }

    public void previosDesconectar() {
        interrumpirReceta();
        if (Pelea != null) {
            if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
                if (Pelea.getTipoPelea() != Constantes.PELEA_TIPO_DESAFIO) {
                    Cuenta.addMensaje("Im1192;" + Nombre, false);
                }
            }
        }
    }

    public void rechazarGrupo() {
        if (!TipoInvitacion.equals("grupo")) {
            return;
        }
        Personaje invitandoA, invitador;
        if (Invitador != null) {
            invitador = Invitador;
            invitandoA = this;
        } else if (InvitandoA != null) {
            invitador = this;
            invitandoA = InvitandoA;
        } else {
            GestorSalida.INSTANCE.ENVIAR_BN_NADA(this);
            return;
        }
        invitador.setInvitandoA(null, "");
        invitandoA.setInvitador(null, "");
        GestorSalida.INSTANCE.ENVIAR_PR_RECHAZAR_INVITACION_GRUPO(invitador);
        GestorSalida.INSTANCE.ENVIAR_PR_RECHAZAR_INVITACION_GRUPO(invitandoA);
    }

    public void rechazarGremio() {
        if (!TipoInvitacion.equals("gremio")) {
            return;
        }
        Personaje invitandoA, invitador;
        if (Invitador != null) {
            invitador = Invitador;
            invitandoA = this;
        } else if (InvitandoA != null) {
            invitador = this;
            invitandoA = InvitandoA;
        } else {
            GestorSalida.INSTANCE.ENVIAR_BN_NADA(this);
            return;
        }
        invitador.setInvitandoA(null, "");
        invitandoA.setInvitador(null, "");
        GestorSalida.INSTANCE.ENVIAR_gJ_GREMIO_UNIR(invitador, "Ec");
        GestorSalida.INSTANCE.ENVIAR_gJ_GREMIO_UNIR(invitandoA, "Ec");
    }

    public void rechazarKoliseo() {
        if (!TipoInvitacion.equals("koliseo")) {
            return;
        }
        Personaje invitandoA, invitador;
        if (Invitador != null) {
            invitador = Invitador;
            invitandoA = this;
        } else if (InvitandoA != null) {
            invitador = this;
            invitandoA = InvitandoA;
        } else {
            GestorSalida.INSTANCE.ENVIAR_BN_NADA(this);
            return;
        }
        invitador.setInvitandoA(null, "");
        invitandoA.setInvitador(null, "");
        GestorSalida.INSTANCE.ENVIAR_kR_RECHAZAR_INVITACION_KOLISEO(invitador);
        GestorSalida.INSTANCE.ENVIAR_kR_RECHAZAR_INVITACION_KOLISEO(invitandoA);
    }

    public void rechazarDesafio() {
        if (!TipoInvitacion.equals("desafio")) {
            return;
        }
        Personaje invitandoA, invitador;
        if (Invitador != null) {
            invitador = Invitador;
            invitandoA = this;
        } else if (InvitandoA != null) {
            invitador = this;
            invitandoA = InvitandoA;
        } else {
            GestorSalida.INSTANCE.ENVIAR_BN_NADA(this);
            return;
        }
        invitador.setInvitandoA(null, "");
        invitandoA.setInvitador(null, "");
        GestorSalida.INSTANCE.ENVIAR_GA902_RECHAZAR_DESAFIO(invitador, invitador.getId(), Id);
        GestorSalida.INSTANCE.ENVIAR_GA902_RECHAZAR_DESAFIO(invitandoA, invitador.getId(), Id);
    }

    public boolean puedeInvitar() {
        return !TipoInvitacion.isEmpty();
    }

    public void Cargar_Objetos_Comando(Personaje perso, String objetos) {
        boolean modificar = false;
//        if (_inventario.length() <2){
        //        }
        for (final String idObjeto : objetos.split(Pattern.quote("|"))) {
            try {
                if (idObjeto.isEmpty()) {
                    continue;
                }
                Objeto obj = Mundo.getObjeto(Integer.parseInt(idObjeto));
                if (obj.getDueñoTemp() == Id) {
                    GestorSalida.INSTANCE.ENVIAR_BAT2_CONSOLA(perso, "El item: " + obj.getId() + " El cual es el modelo: " + obj.getObjModeloID() + " De nombre: " + obj.getObjModelo().getNombre() + " Ya lo tenia");
                    break;
                }
                if (obj.getDueñoTemp() == 0) {
                    obj.setDueñoTemp(Id);
                    // se agrega el objeto al array _objPos
                    addObjetoConOAKO(obj, false);
                    GestorSalida.INSTANCE.ENVIAR_BAT2_CONSOLA(perso, "El item: " + obj.getId() + " El cual es el modelo: " + obj.getObjModeloID() + " De nombre: " + obj.getObjModelo().getNombre() + " Fue agregado");
                } else {
                    modificar = true;
                    GestorSalida.INSTANCE.ENVIAR_BAT2_CONSOLA(perso, "El item: " + obj.getId() + " El cual es el modelo: " + obj.getObjModeloID() + " De nombre: " + obj.getObjModelo().getNombre() + " No era suyo\nEl dueño real es: +" + obj.getDueñoTemp());
                    AtlantaMain.redactarLogServidorln("El objetoID " + idObjeto + " tiene dueño " + (obj.getDueñoTemp())
                            + " no se puede agregar a " + Nombre + "(" + Id + ")");
                }
            } catch (Exception e) {
                modificar = true;
                AtlantaMain.redactarLogServidorln("El objetoID " + idObjeto + " pertenece a " + Nombre + "(" + Id + ")"
                        + ", no existe");
            }
        }
    }

    public void Cargar_Objetos_Rollback(Personaje perso) {
        boolean modificar = false;
        String lista = GestorSQL.INSTANCE.RECUPERAR_OBJETOS(Id);
        for (final String idObjeto : lista.split(Pattern.quote("|"))) {
            try {
                if (idObjeto.isEmpty()) {
                    continue;
                }
                Objeto obj = Mundo.getObjeto(Integer.parseInt(idObjeto));
                if (obj.getDueñoTemp() == Id) {
                    GestorSalida.INSTANCE.ENVIAR_BAT2_CONSOLA(perso, "El item: " + obj.getId() + " El cual es el modelo: " + obj.getObjModeloID() + " De nombre: " + obj.getObjModelo().getNombre() + " Ya lo tenia");
                    break;
                }
                if (obj.getDueñoTemp() == 0) {
                    obj.setDueñoTemp(Id);
                    // se agrega el objeto al array _objPos
                    addObjetoConOAKO(obj, false);
                    GestorSalida.INSTANCE.ENVIAR_BAT2_CONSOLA(perso, "El item: " + obj.getId() + " El cual es el modelo: " + obj.getObjModeloID() + " De nombre: " + obj.getObjModelo().getNombre() + " Fue agregado");
                } else {
                    modificar = true;
                    GestorSalida.INSTANCE.ENVIAR_BAT2_CONSOLA(perso, "El item: " + obj.getId() + " El cual es el modelo: " + obj.getObjModeloID() + " De nombre: " + obj.getObjModelo().getNombre() + " No era suyo\nEl dueño real es: +" + obj.getDueñoTemp());
                    AtlantaMain.redactarLogServidorln("El objetoID " + idObjeto + " tiene dueño " + (obj.getDueñoTemp())
                            + " no se puede agregar a " + Nombre + "(" + Id + ")");
                }
            } catch (Exception e) {
                modificar = true;
                AtlantaMain.redactarLogServidorln("El objetoID " + idObjeto + " pertenece a " + Nombre + "(" + Id + ")"
                        + ", no existe");
            }
        }
    }

    public void desconectar(boolean salvar) {
        try {
            if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE || Desconectando) {
                return;
            }
            Desconectando = true;
//            if (!_enLinea) {
//                return;
//            }
            if (EsliderIP) {
                EsliderIP = false;
                for (Personaje p :
                        Multis) {
                    p.LiderIP = null;
                    p.Multi = null;
                    p.EsliderIP = false;
                }
                Multis.clear();
            }
            rechazarGrupo();
            rechazarKoliseo();
            rechazarGremio();
            rechazarDesafio();
            cerrarExchange("");
            setEnLinea(false);
            if (Pelea != null) {
                if (Pelea.esEspectador(Id) || Pelea.getTipoPelea() == Constantes.PELEA_TIPO_DESAFIO) {
                    // cuando es espectador o desafio
                    Pelea.retirarsePelea(Id, 0, true);
                } else if (Pelea.getFase() == Constantes.PELEA_FASE_POSICION || Pelea
                        .getFase() == Constantes.PELEA_FASE_COMBATE) {
                    Pelea.desconectarLuchador(this);
                }
            } else if (Mapa != null) {
                GestorSalida.INSTANCE.ENVIAR_GM_BORRAR_GM_A_MAPA(Mapa, Id);
                if (esMercante()) {
                    GestorSalida.INSTANCE.ENVIAR_GM_MERCANTE_A_MAPA(Mapa, "+" + stringGMmercante());
                }
            }
            if (Pelea == null) {
                if (Grupo != null) {
                    Grupo.dejarGrupo(this, Grupo.esLiderGrupo(this));
                }
                if (Koliseo != null) {
                    Koliseo.dejarGrupo(this);
                }
                Mundo.delKoliseo(Id);
            }
            setCelda(null);
            setDelayTimerRegenPDV(0);
            resetearVariables();
            TiempoDesconexion = System.currentTimeMillis();
        } catch (Exception e) {
            // si ocurre algo
        } finally {
            if (salvar) {
                GestorSQL.INSTANCE.SALVAR_PERSONAJE(this, true);
            }
        }
    }

    public void cambiarSexo() {
        if (Sexo == Constantes.SEXO_FEMENINO) {
            Sexo = Constantes.SEXO_MASCULINO;
        } else {
            Sexo = Constantes.SEXO_FEMENINO;
        }
    }

    public boolean enLinea() {
        return EnLinea;
    }

    public void salvar() {
        if (!Salvando) {
            AtlantaMain.redactarLogServidor(" -> Salvando a " + this.getNombre() + " ... ");// Ecatome
            Salvando = true;
            GestorSQL.INSTANCE.SALVAR_PERSONAJE(this, true);
            if (Cuenta != null) {
                GestorSQL.INSTANCE.REPLACE_CUENTA_SERVIDOR(Cuenta, GestorSQL.INSTANCE.GET_PRIMERA_VEZ(Cuenta.getNombre()));
            }
            Salvando = false;
        } else {
            GestorSalida.INSTANCE.ENVIAR_cs_CHAT_MENSAJE(this, "Hubo un error al salvar su personaje, por favor use el comando, .salvar\n Si este mensaje persiste, por favor contacte al Staff de manera urgente", Constantes.COLOR_ROJO);
            AtlantaMain.redactarLogServidorln("Error al salvar el personaje: " + Nombre + " Esta en proceso en este momento");
        }

    }

    public void registrar(String packet) {
        if (Cuenta != null) {
            if (ServidorSocket.REGISTROS.get(Cuenta.getNombre()) == null) {
                ServidorSocket.REGISTROS.put(Cuenta.getNombre(), new StringBuilder());
            }
            ServidorSocket.REGISTROS.get(Cuenta.getNombre()).append(System.currentTimeMillis()).append(": \t").append(packet).append("\n");
        }
    }

    public void setEnLinea(final boolean linea) {
        if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
            return;
        }
        EnLinea = linea;
        if (EnLinea) {
            Mundo.addOnline(this);
        } else {
            Mundo.removeOnline(this);
        }
    }

    public void setGrupo(final Grupo grupo) {
        Grupo = grupo;
    }

    public Grupo getGrupoParty() {
        if (esMultiman()) {
            return Compañero.getGrupoParty();
        }
        return Grupo;
    }

    public String getPtoSalvada() {
        return MapaSalvada + "," + CeldaSalvada;
    }

    public int getConversandoCon() {
        return ConversandoCon;
    }

    public void setConversandoCon(final int conversando) {
        ConversandoCon = conversando;
    }

    public int getPreguntaID() {
        return Pregunta;
    }

    public void setPreguntaID(final int pregunta) {
        Pregunta = pregunta;
    }

    public void dialogoFin() {
        GestorSalida.INSTANCE.ENVIAR_DV_FINALIZAR_DIALOGO(this);
        ConversandoCon = 0;
        Pregunta = 0;
    }

    public long getKamas() {
        return Kamas;
    }

    public void setKamasCero() {
        Kamas = 0;
    }

    public void addKamas(final long kamas, final boolean msj, boolean conAk) {
        if (kamas == 0) {
            return;
        }
        Kamas += kamas;
        if (Kamas >= AtlantaMain.LIMITE_DETECTAR_FALLA_KAMAS) {
            AtlantaMain.redactarLogServidorln("EL PERSONAJE " + Nombre + " (" + Id + ") CON CUENTA " + getCuentaID()
                    + " POSSE " + Kamas);
            if (!ServidorSocket.JUGADORES_REGISTRAR.contains(Cuenta.getNombre())) {
                ServidorSocket.JUGADORES_REGISTRAR.add(Cuenta.getNombre());
            }
        }
        if (Kamas < 0) {
            Kamas = 0;
        }
        if (EnLinea) {
            if (conAk) {
                GestorSalida.INSTANCE.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
            }
            if (msj) {
                if (kamas < 0) {
                    GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "046;" + Economia.INSTANCE.formatNumber(-kamas));
                } else {
                    GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "045;" + Economia.INSTANCE.formatNumber(kamas));
                }
            }
        }
    }

    public synchronized boolean comprarTienda(final Personaje comprador, int cantidad, final Objeto objeto) {
        try {
            if (objeto == null || !Tienda.contains(objeto)) {
                return false;
            }
            if (cantidad < 1) {
                cantidad = 1;
            } else if (cantidad > objeto.getCantidad()) {
                cantidad = objeto.getCantidad();
            }
            if (Formulas.INSTANCE.valorValido(cantidad, objeto.getPrecio())) {
                GestorSalida.INSTANCE.ENVIAR_BN_NADA(this, "INTENTO BUG MULTIPLICADOR");
                return false;
            }
            long precio = objeto.getPrecio() * cantidad;
            if (precio <= 0) {
                return false;
            }
            if (comprador.getKamas() < precio) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(comprador, "1128;" + precio);
                return false;
            }
            comprador.addKamas(-precio, true, false);
            addKamas(precio, false, false);
            int nuevaCantidad = objeto.getCantidad() - cantidad;
            if (nuevaCantidad >= 1) {
                final Objeto nuevoObj = objeto.clonarObjeto(nuevaCantidad, Constantes.OBJETO_POS_NO_EQUIPADO);
                nuevoObj.setPrecio(objeto.getPrecio());
                Tienda.addObjeto(nuevoObj);
                objeto.setCantidad(cantidad);
                // GestorSalida.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(comprador, objeto);
            }
            borrarObjTienda(objeto);
            objeto.setPrecio(0);
            comprador.addObjIdentAInventario(objeto, true);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    public Cuenta getCuenta() {
        return Cuenta;
    }

    public void enviarmensajeNegro(String mensaje) {
        GestorSalida.INSTANCE.ENVIAR_cs_CHAT_MENSAJE(this, mensaje, Constantes.COLOR_NEGRO);
    }

    public void enviarmensajeVerde(String mensaje) {
        GestorSalida.INSTANCE.ENVIAR_cs_CHAT_MENSAJE(this, mensaje, Constantes.COLOR_VERDE_OSCURO);
    }

    public void guardarPAPMoriginales() {
        PAoriginales = getStatsObjEquipados().getStatParaMostrar(Constantes.STAT_MAS_PA);
        PMoriginales = getStatsObjEquipados().getStatParaMostrar(Constantes.STAT_MAS_PM);
    }

    public void retomarPAPMoriginales() {
        if (PAoriginales > 0) {
            getStatsObjEquipados().fijarStatID(Constantes.STAT_MAS_PA, PAoriginales);
            PAoriginales = 0;
        }
        if (PMoriginales > 0) {
            getStatsObjEquipados().fijarStatID(Constantes.STAT_MAS_PM, PMoriginales);
            PMoriginales = 0;
        }
    }

    public int getPuntosHechizos() {
        return PuntosHechizos;
    }

    public Gremio getGremio() {
        if (MiembroGremio == null) {
            return null;
        }
        return MiembroGremio.getGremio();
    }

    public Pelea getPelea() {
        return Pelea;
    }

    public void setPelea(final Pelea pelea) {
        retomarPAPMoriginales();
        if (Pelea != null && pelea == null) {
            if (Compañero != null) {
                GestorSalida.INSTANCE.ENVIAR_AI_CAMBIAR_ID(this, Id);
                GestorSalida.INSTANCE.ENVIAR_SL_LISTA_HECHIZOS(this);
                Compañero.setCompañero(null);
                Compañero = null;
            }
            if (Multi != null) {
                GestorSalida.INSTANCE.ENVIAR_AI_CAMBIAR_ID(this, Id);
                GestorSalida.INSTANCE.ENVIAR_SL_LISTA_HECHIZOS(this);
                if (enLinea()) {
                    Cuenta.getSocket().setPersonaje(this);
                }
                if (!Multis.isEmpty()) {
                    for (Personaje p :
                            Multis) {
                        if (!p.enLinea()) {
                            p.desconectar(true);
                        }
                    }
                }
                Multi = null;
            }
            if (enLinea()) {
                Cuenta.getSocket().setPersonaje(this);
                Multi = null;
                GestorSalida.INSTANCE.ENVIAR_AI_CAMBIAR_ID(this, Id);
                GestorSalida.INSTANCE.ENVIAR_SL_LISTA_HECHIZOS(this);
            } else {
                desconectar(true);
            }
            setDelayTimerRegenPDV(1000);
        } else if (pelea != null) {
            setDelayTimerRegenPDV(0);
        }
        Pelea = pelea;
    }

    public boolean mostrarAmigos() {
        return MostrarAmigos;
    }

    public boolean alasActivadas() {
        if (Alineacion == Constantes.ALINEACION_NEUTRAL) {
            return false;
        }
        return MostrarAlas;
    }

    public int getEnergia() {
        return Energia;
    }

    public void addEnergiaConIm(final int energia, boolean mensaje) {
        if (Mundo.SERVIDOR_ESTADO == Constantes.SERVIDOR_OFFLINE) {
            return;
        }
        if (esMultiman()) {
            return;
        }
        if (AtlantaMain.MODO_HEROICO || AtlantaMain.MAPAS_MODO_HEROICO.contains(Mapa.getId())
                || !AtlantaMain.PARAM_PERDER_ENERGIA) {
            return;
        }
        final int exEnergia = Energia;
        Energia = Math.min(10000, Energia + energia);
        if (energia > 0) {
            if (esFantasma() && exEnergia <= 0 && Energia > 0) {
                deformar();
                Ocupado = false;
                int[][] rA = {{RA_PUEDE_INTERCAMBIAR, 1}, {RA_PUEDE_HABLAR_NPC, 1}, {RA_PUEDE_MERCANTE, 1}, {
                        RA_PUEDE_INTERACTUAR_RECAUDADOR, 1}, {RA_PUEDE_INTERACTUAR_PRISMA, 1}, {RA_PUEDE_USAR_OBJETOS, 1}, {
                        RA_NO_PUEDE_ATACAR, 0}, {RA_PUEDE_DESAFIAR, 1}, {RA_PUEDE_INTERACTUAR_OBJETOS, 1}, {RA_PUEDE_AGREDIR, 1}};
                setRestriccionesA(rA);
                int[][] rB = {{RB_PUEDE_SER_AGREDIDO, 1}, {RB_PUEDE_SER_DESAFIADO, 1}, {RB_PUEDE_HACER_INTERCAMBIO, 1}, {
                        RB_NO_ES_FANTASMA, 1}, {RB_PUEDE_CORRER, 1}, {RB_PUEDE_SER_ATACADO, 0}, {RB_NO_ES_TUMBA, 1}};
                setRestriccionesB(rB);
                refrescarEnMapa();
                GestorSalida.INSTANCE.ENVIAR_AR_RESTRICCIONES_PERSONAJE(this);
            }
        } else {
            if (Energia <= 0) {
                convertirseTumba();
                Energia = 0;
            } else if (Energia < 1500) {
                GestorSalida.INSTANCE.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(this, 11, Energia + "", "");
            }
        }
        if (EnLinea && mensaje) {
            if (energia > 0) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "07;" + energia);
            } else if (energia < 0) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "034;" + Math.abs(energia));
            }
        }
        GestorSalida.INSTANCE.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
    }

    public void convertirseTumba() {
        if (esMultiman()) {
            return;
        }
        if (esTumba()) {
            return;
        }
        if (estaMontando()) {
            subirBajarMontura(false);
        }
        Energia = -1;
        int[][] rA = {{RA_PUEDE_INTERCAMBIAR, 0}, {RA_PUEDE_HABLAR_NPC, 0}, {RA_PUEDE_MERCANTE, 0}, {
                RA_PUEDE_INTERACTUAR_RECAUDADOR, 0}, {RA_PUEDE_INTERACTUAR_PRISMA, 0}, {RA_PUEDE_USAR_OBJETOS, 0}, {
                RA_NO_PUEDE_ATACAR, 1}, {RA_PUEDE_DESAFIAR, 0}, {RA_PUEDE_INTERACTUAR_OBJETOS, 0}, {RA_PUEDE_AGREDIR, 0}};
        setRestriccionesA(rA);
        int[][] rB = {{RB_PUEDE_SER_AGREDIDO, 0}, {RB_PUEDE_SER_DESAFIADO, 0}, {RB_PUEDE_HACER_INTERCAMBIO, 0}, {
                RB_NO_ES_FANTASMA, 1}, {RB_PUEDE_CORRER, 0}, {RB_PUEDE_SER_ATACADO, 0}, {RB_NO_ES_TUMBA, 0}};
        setRestriccionesB(rB);
        GfxID = Clase.getGfxs(3);
        refrescarEnMapa();
        GestorSalida.INSTANCE.ENVIAR_AR_RESTRICCIONES_PERSONAJE(this);
        GestorSalida.INSTANCE.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(this, 12, "", "");
    }

    public void convertirseFantasma() {
        if (esMultiman()) {
            return;
        }
        if (esFantasma()) {
            return;
        }
        Energia = 0;
        GfxID = 8004;
        int[][] rA = {{RA_PUEDE_INTERCAMBIAR, 0}, {RA_PUEDE_HABLAR_NPC, 0}, {RA_PUEDE_MERCANTE, 0}, {
                RA_PUEDE_INTERACTUAR_RECAUDADOR, 0}, {RA_PUEDE_INTERACTUAR_PRISMA, 0}, {RA_PUEDE_USAR_OBJETOS, 0}, {
                RA_NO_PUEDE_ATACAR, 1}, {RA_PUEDE_DESAFIAR, 0}, {RA_PUEDE_INTERACTUAR_OBJETOS, 0}, {RA_PUEDE_AGREDIR, 0}};
        setRestriccionesA(rA);
        int[][] rB = {{RB_PUEDE_SER_AGREDIDO, 0}, {RB_PUEDE_SER_DESAFIADO, 0}, {RB_PUEDE_HACER_INTERCAMBIO, 0}, {
                RB_NO_ES_FANTASMA, 0}, {RB_PUEDE_CORRER, 0}, {RB_PUEDE_SER_ATACADO, 0}, {RB_NO_ES_TUMBA, 1}};
        setRestriccionesB(rB);
        if (AtlantaMain.MODO_HEROICO || AtlantaMain.MAPAS_MODO_HEROICO.contains(Mapa.getId())) {
            if (!AtlantaMain.PARAM_HEROICO_GAME_OVER) {
                revivir(true);
                return;
            }
            if (EnLinea) {
                if (AtlantaMain.PARAM_MENSAJE_ASESINOS_HEROICO) {
                    GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION_A_TODOS("1DIE;" + Nombre);
                }
                GestorSalida.INSTANCE.ENVIAR_GM_BORRAR_GM_A_MAPA(Mapa, Id);
            }
            if (Grupo != null) {
                Grupo.dejarGrupo(this, false);
            }
            if (Koliseo != null) {
                Koliseo.dejarGrupo(this);
            }
            Mundo.delKoliseo(Id);
            setCelda(null);
            resetearVariables();
            GestorSalida.INSTANCE.ENVIAR_GO_GAME_OVER(this);
            Mundo.eliminarPersonaje(this, false);
            setEnLinea(false);
            GestorSQL.INSTANCE.SALVAR_PERSONAJE(this, true);
        } else {// si es fantasma
            refrescarEnMapa();
            GestorSalida.INSTANCE.ENVIAR_AR_RESTRICCIONES_PERSONAJE(this);
            String cementerio = Mapa.getSubArea().getCementerio();
            // teleport((short) 10342, (short) 223);
            if (cementerio.isEmpty()) {
                cementerio = "1188,297";
            }
            short mapaID = 1188;
            short celdaID = 297;
            try {
                mapaID = Short.parseShort(cementerio.split(",")[0]);
                celdaID = Short.parseShort(cementerio.split(",")[1]);
            } catch (Exception ignored) {
            }
            teleport(mapaID, celdaID);
            GestorSalida.INSTANCE.ENVIAR_IH_COORDENADAS_UBICACION(this, Constantes.ESTATUAS_FENIX);
            GestorSalida.INSTANCE.ENVIAR_M1_MENSAJE_SERVER_SVR_MUESTRA_INSTANTANEO(this, 15, "", "");
        }
    }

    public void revivir(final boolean aparecer) {
        if (esMultiman()) {
            return;
        }
        if (!esFantasma() && !esTumba()) {
            return;
        }
        Energia = AtlantaMain.MODO_HEROICO ? 10000 : 1000;
        deformar();
        Ocupado = false;
        int[][] rA = {{RA_PUEDE_INTERCAMBIAR, 1}, {RA_PUEDE_HABLAR_NPC, 1}, {RA_PUEDE_MERCANTE, 1}, {
                RA_PUEDE_INTERACTUAR_RECAUDADOR, 1}, {RA_PUEDE_INTERACTUAR_PRISMA, 1}, {RA_PUEDE_USAR_OBJETOS, 1}, {
                RA_NO_PUEDE_ATACAR, 0}, {RA_PUEDE_DESAFIAR, 1}, {RA_PUEDE_INTERACTUAR_OBJETOS, 1}, {RA_PUEDE_AGREDIR, 1}};
        setRestriccionesA(rA);
        int[][] rB = {{RB_PUEDE_SER_AGREDIDO, 1}, {RB_PUEDE_SER_DESAFIADO, 1}, {RB_PUEDE_HACER_INTERCAMBIO, 1}, {
                RB_NO_ES_FANTASMA, 1}, {RB_PUEDE_CORRER, 1}, {RB_PUEDE_SER_ATACADO, 0}, {RB_NO_ES_TUMBA, 1}};
        setRestriccionesB(rB);
        if (aparecer && Pelea == null) {
            refrescarEnMapa();
        }
        if (EnLinea) {
            GestorSalida.INSTANCE.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
            GestorSalida.INSTANCE.ENVIAR_AR_RESTRICCIONES_PERSONAJE(this);
            GestorSalida.INSTANCE.ENVIAR_IH_COORDENADAS_UBICACION(this, "");
            GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "033");
        }
    }

    public int getNivelGremio() {
        if (MiembroGremio == null) {
            return 0;
        }
        return MiembroGremio.getGremio().getNivel();
    }

    public int getNivel() {
        return Nivel;
    }

    public void setNivel(final int nivel) {
        Nivel = nivel;
    }

    public long getExperiencia() {
        return Experiencia;
    }

    public Celda getCelda() {
        return Celda;
    }

    public synchronized void setCelda(final Celda celda) {
        boolean difMapa = (celda == null || Celda == null) || (celda.getMapa() != Celda.getMapa());
        if (esMultiman()) {
            difMapa = false;
        }
        if (Celda != null) {
            Celda.removerPersonaje(this, difMapa || !EnLinea);
        }
        if (celda != null) {
            Celda = celda;
            Celda.addPersonaje(this, difMapa && EnLinea);
        }
    }

    public int getTalla() {
        return Talla;
    }

    public void setTalla(final short talla) {
        Talla = talla;
    }

    public void disminuirTurnos() {
        if (Montando && Montura != null) {
            Montura.energiaPerdida(5);
        }
        for (byte pos : Constantes.POSICIONES_EQUIPAMIENTO) {
            try {
                final Objeto obj = getObjPosicion(pos);
                if (obj == null) {
                    continue;
                }
                final String param = obj.getParamStatTexto(Constantes.STAT_TURNOS, 3);
                if (param.isEmpty()) {
                    continue;
                }
                final int turnos = Integer.parseInt(param, 16);
                if (turnos == 1) {
                    borrarOEliminarConOR(obj.getId(), true);
                } else if (turnos > 1) {
                    obj.addStatTexto(Constantes.STAT_TURNOS, "0#0#" + Integer.toString(turnos - 1, 16));
                    GestorSalida.INSTANCE.ENVIAR_OCK_ACTUALIZA_OBJETO(this, obj);
                }
            } catch (final Exception ignored) {
            }
        }
        if (AtlantaMain.PARAM_PERDER_PDV_ARMAS_ETEREAS) {
            Objeto arma = getObjPosicion(Constantes.OBJETO_POS_ARMA);
            if (arma != null && arma.getObjModelo().esEtereo()) {
                if (arma.addDurabilidad(-1)) {
                    borrarOEliminarConOR(arma.getId(), true);
                    GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "160");
                } else {
                    GestorSalida.INSTANCE.ENVIAR_OCK_ACTUALIZA_OBJETO(this, arma);
                }
            }
        }
    }

    public int getGfxID(boolean rolePlayBuff) {
        if (Encarnacion != null) {
            return Encarnacion.getGfxID();
        }
        int gfx = TotalStats.getStatsObjetos().getStatParaMostrar(Constantes.STAT_CAMBIA_APARIENCIA_2);
        if (rolePlayBuff && gfx != 0) {
            return gfx;
        }
        gfx = TotalStats.getStatsObjetos().getStatParaMostrar(Constantes.STAT_CAMBIA_APARIENCIA);
        if (gfx != 0) {
            return gfx;
        }
        return GfxID;
    }

    public int getGfxIDReal() {
        return GfxID;
    }

    public void setGfxID(final short gfxid) {
        GfxID = gfxid;
    }

    public void deformar() {
        if (Encarnacion != null) {
            GfxID = Encarnacion.getGfxID();
        } else {
            GfxID = (ClaseID * 10 + Sexo);
        }
    }

    public int getId() {
        return Id;
    }

    public Mapa getMapa() {
        return Mapa;
    }

    public void setMapa(final Mapa mapa) {
        Mapa = mapa;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(final String nombre) {
        Nombre = nombre;
        GestorSQL.INSTANCE.UPDATE_NOMBRE_PJ(this);
        if (getMiembroGremio() != null) {
            GestorSQL.INSTANCE.REPLACE_MIEMBRO_GREMIO(getMiembroGremio());
        }
        try {
            Mundo.getRankingPVP(Id).setNombre(Nombre);
            GestorSQL.INSTANCE.REPLACE_RANKING_PVP(Mundo.getRankingPVP(Id));
        } catch (final Exception ignored) {
        }
        try {
            Mundo.getRankingKoliseo(Id).setNombre(Nombre);
            GestorSQL.INSTANCE.REPLACE_RANKING_KOLISEO(Mundo.getRankingKoliseo(Id));
        } catch (final Exception ignored) {
        }
    }

    public boolean estaDisponible(boolean muerto, boolean otros) {
        if (estaOcupado() || getPelea() != null) {
            return true;
        }
        if (otros) {
            if (estaFullOcupado()) {
                return true;
            }
        }
        if (muerto) {
            return esFantasma() || esTumba();
        }
        return false;
    }

    public boolean estaFullOcupado() {
        return ConversandoCon != 0 || Exchanger != null || puedeInvitar() || estaExchange();
    }

    public boolean estaOcupado() {
        return Ocupado;
    }

    public void setOcupado(final boolean ocupado) {
        Ocupado = ocupado;
    }

    public boolean estaSentado() {
        return Sentado;
    }

    public byte getSexo() {
        return Sexo;
    }

    public byte getClaseID(final boolean original) {
        if (!original && Encarnacion != null) {
            return 20;
        }
        return ClaseID;
    }

    public void setClaseID(final byte clase) {
        ClaseID = clase;
    }

    public int getColor1() {
        return Color1;
    }

    public int getColor2() {
        return Color2;
    }

    public int getColor3() {
        return Color3;
    }

    public int getCapital() {
        return PuntosStats;
    }

    public void resetearStats(final boolean todo) {
        // ya contiene resfrescarStuff
        if (todo) {
            reiniciarSubStats(SubStatsScroll);
        }
        reiniciarSubStats(SubStatsBase);
        TotalStats.getStatsBase().nuevosStatsBase(SubStatsScroll, this);
        PuntosStats = 0;
        IntStream.range(0, Nivel - 1).forEachOrdered(i -> PuntosStats += (int) (AtlantaMain.PUNTOS_STATS_POR_NIVEL * AtlantaMain.FUN_RANDOM_PUNTOS()) + (AtlantaMain.BONUS_RESET_PUNTOS_STATS
                * Resets) + AtlantaMain.INICIO_PUNTOS_STATS);
        refrescarStuff(true, true, false);
    }

    public boolean cambiarClase(byte clase) {// cambiar raza
        if (clase < 1) {
            clase = 1;
        } else if (clase > 12) {
            clase = 12;
        }
        if (clase == getClaseID(true)) {
            GestorSalida.INSTANCE.ENVIAR_BN_NADA(this, "CAMBIAR CLASE - MISMA CLASE");
            return false;
        }
        ClaseID = clase;
        Clase = Mundo.getClase(ClaseID);
        GestorSalida.INSTANCE.ENVIAR_AC_CAMBIAR_CLASE(this, getClaseID(true));
        deformar();
        for (final HechizoPersonaje hp : Hechizos) {
            for (int i = 1; i < hp.getNivel(); i++) {
                PuntosHechizos += i;
            }
        }
        for (final HechizoPersonaje hp : Hechizos) {
            for (int i = 1; i < hp.getNivel(); i++) {
                hp.setNivel(1);
            }
        }
        fijarHechizosInicio();
        // _puntosHechizos = (_nivel - 1) + (MainServidor.BONUS_RESET_PUNTOS_HECHIZOS * _resets);
        resetearStats(false);
        refrescarEnMapa();
        GestorSQL.INSTANCE.CAMBIAR_SEXO_CLASE(this);
        this.salvar();
        GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "1PERSONAJE_GUARDADO_OK");
        return true;
    }

    public byte getResets() {
        return Resets;
    }

    public boolean aumentarReset() {
        if (Resets >= AtlantaMain.MAX_RESETS) {
            return false;
        }
        Encarnacion = null;
        Resets++;
        int difNivel = Nivel - AtlantaMain.INICIO_NIVEL;
        Nivel = AtlantaMain.INICIO_NIVEL;
        Alineacion = Constantes.ALINEACION_NEUTRAL;
        MostrarAlas = false;
        Honor = 0;
        Deshonor = 0;
        GradoAlineacion = 1;
        Experiencia = Mundo.getExpPersonaje(Nivel);
        monturaACertificado();
        PorcXPMontura = 0;
        RestriccionesALocalPlayer = 8200;
        RestriccionesBCharacter = 8;
        resetearTodosHechizos();
        PuntosHechizos += AtlantaMain.BONUS_RESET_PUNTOS_HECHIZOS;
        PuntosHechizos -= difNivel;
        resetearStats(false);
        fullPDV();// FIXME
        refrescarEnMapa();
        return true;
    }

    public void monturaACertificado() {
        try {
            if (Montura == null)
                return;
            if (estaMontando()) {
                subirBajarMontura(false);
            }
            final Objeto obj1 = Objects.requireNonNull(Montura.getObjModCertificado()).crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO,
                    CAPACIDAD_STATS.RANDOM);
            obj1.fijarStatValor(Constantes.STAT_CONSULTAR_MONTURA, Math.abs(Montura.getId()));
            obj1.addStatTexto(Constantes.STAT_PERTENECE_A, "0#0#0#" + Nombre);
            obj1.addStatTexto(Constantes.STAT_NOMBRE, "0#0#0#" + Montura.getNombre());
            addObjetoConOAKO(obj1, true);
            Montura.setPergamino(obj1.getId());
            GestorSalida.INSTANCE.ENVIAR_Ow_PODS_DEL_PJ(this);
            GestorSalida.INSTANCE.ENVIAR_Re_DETALLES_MONTURA(this, "-", null);
            GestorSQL.INSTANCE.REPLACE_MONTURA(Montura, false);
            Montura = null;
        } catch (Exception ignored) {
        }
    }

    public void reiniciarCero() {
        if (esMultiman()) {
            return;
        }
        Encarnacion = null;
        revivir(false);
        if (Nivel > UltimoNivel) {
            UltimoNivel = Nivel;
        }
        Nivel = AtlantaMain.INICIO_NIVEL;
        Kamas = 0;
        PuntosHechizos = (Nivel - 1) + (AtlantaMain.BONUS_RESET_PUNTOS_STATS * Resets);
        Alineacion = Constantes.ALINEACION_NEUTRAL;
        MostrarAlas = false;
        Honor = 0;
        Deshonor = 0;
        GradoAlineacion = 1;
        Energia = 10000;
        Experiencia = Mundo.getExpPersonaje(Nivel);
        Montura = null;
        PorcXPMontura = 0;
        Talla = Clase.getTallas(Sexo);
        GfxID = Clase.getGfxs(Sexo);
        final short mapaID = Clase.getMapaInicio();
        final short celdaID = Clase.getCeldaInicio();
        Mapa = Mundo.getMapa(mapaID);
        Misiones.clear();
        if (Mapa == null) {
            Mapa = Mundo.getMapa((short) 7411);
        }
        setCelda(Mapa.getCelda(celdaID));
        setPuntoSalvada(mapaID + "," + celdaID);
        Tienda.clear();
        EsMercante = false;
        fullPDV();
        StatsOficios.clear();
        if (AtlantaMain.PARAM_PERMITIR_OFICIOS) {
            StatsOficios.put((byte) 7, new StatOficio((byte) 7, Mundo.getOficio(1), 0));
        }
        RestriccionesALocalPlayer = 8200;
        RestriccionesBCharacter = 8;
        fijarHechizosInicio();
        resetearStats(true);
        EnLinea = true;
        GestorSalida.INSTANCE.ENVIAR_ALK_LISTA_DE_PERSONAJES(this, Cuenta);
        EnLinea = false;
        for (final String str : AtlantaMain.INICIO_OBJETOS.split(";")) {
            try {
                if (str.isEmpty()) {
                    continue;
                }
                String[] arg = str.split(",");
                final Objeto obj = Mundo.getObjetoModelo(Integer.parseInt(arg[0])).crearObjeto(Integer.parseInt(arg[1]),
                        Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO);
                Mundo.addObjeto(obj, false);
                this.addObjetoConOAKO(obj, false);
                if (this.getObjPosicion(Constantes.getPosObjeto(obj.getObjModelo().getTipo(), this)) == null && obj.getObjModelo().getNivel() <= this.getNivel()) {
                    obj.setPosicion(Constantes.getPosObjeto(obj.getObjModelo().getTipo(), this), this, false);
                }
            } catch (final Exception ignored) {
            }
        }
        for (final String str : AtlantaMain.INICIO_SET_ID.split(",")) {
            if (str.isEmpty()) {
                continue;
            }
            final ObjetoSet objSet = Mundo.getObjetoSet(Integer.parseInt(str));
            if (objSet != null) {
                for (final ObjetoModelo OM : objSet.getObjetosModelos()) {
                    final Objeto x = OM.crearObjeto(1, Constantes.OBJETO_POS_NO_EQUIPADO, CAPACIDAD_STATS.MAXIMO);
                    Mundo.addObjeto(x, false);
                    this.addObjetoConOAKO(x, false);
                    if (this.getObjPosicion(Constantes.getPosObjeto(x.getObjModelo().getTipo(), this)) == null && x.getObjModelo().getNivel() <= this.getNivel()) {
                        x.setPosicion(Constantes.getPosObjeto(x.getObjModelo().getTipo(), this), this, false);
                    }
                }
            }
        }
        GestorSQL.INSTANCE.SALVAR_PERSONAJE(this, false);
    }

    public boolean tieneHechizoID(final int hechizoID) {
        try {
            if (Encarnacion != null) {
                return Encarnacion.tieneHechizoID(hechizoID);
            }
            return getHechizoPersonajePorID(hechizoID) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void boostearFullTodosHechizos() {
        if (Encarnacion != null) {
            return;
        }
        for (HechizoPersonaje h : Hechizos) {
            if (h == null) {
                continue;
            }
            final int antNivel = h.getStatHechizo().getGrado();
            if (antNivel >= 6) {
                continue;
            }
            while (fijarNivelHechizoOAprender(h.getHechizo().getID(), h.getStatHechizo().getGrado() + 1, false)) {
            }
        }
        GestorSalida.INSTANCE.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
    }

    public boolean boostearHechizo(final int hechizoID) {// subir hechizo
        if (Encarnacion != null) {
            return false;
        }
        HechizoPersonaje h = getHechizoPersonajePorID(hechizoID);
        if (h == null) {
            return false;
        }
        final int antNivel = h.getStatHechizo().getGrado();
        if (antNivel >= 6) {
            return false;
        }
        if (PuntosHechizos < antNivel) {
            return false;
        }
        if (!fijarNivelHechizoOAprender(hechizoID, antNivel + 1, false)) {
            return false;
        }
        PuntosHechizos -= antNivel;
        GestorSalida.INSTANCE.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
        return true;
    }

    public void resetearTodosHechizos() {
        if (Encarnacion != null) {
            return;
        }
        final ArrayList<HechizoPersonaje> hechizos = new ArrayList<HechizoPersonaje>(Hechizos);
        for (final HechizoPersonaje hp : hechizos) {
            for (int i = 1; i < hp.getNivel(); i++) {
                PuntosHechizos += i;
            }
            hp.setNivel(1);
            fijarNivelHechizoOAprender(hp.getHechizo().getID(), hp.getStatHechizo().getNivelRequerido() > Nivel ? 0 : 1,
                    false);
        }
        GestorSalida.INSTANCE.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
        GestorSalida.INSTANCE.ENVIAR_SL_LISTA_HECHIZOS(this);
    }

    public boolean olvidarHechizo(final int hechizoID, boolean porCompleto, boolean mensaje) {
        if (Encarnacion != null) {
            return false;
        }
        HechizoPersonaje h = getHechizoPersonajePorID(hechizoID);
        if (h == null) {
            return false;
        }
        for (int i = 1; i < h.getNivel(); i++) {
            PuntosHechizos += i;
        }
        fijarNivelHechizoOAprender(hechizoID, porCompleto ? 0 : 1, false);
        if (mensaje) {
            GestorSalida.INSTANCE.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
        }
        return true;
    }

    public HechizoPersonaje getHechizoPersonajePorID(int hechizoID) {
        HechizoPersonaje h = null;
        for (HechizoPersonaje hp : Hechizos) {
            if (hp.getStatHechizo().getHechizoID() == hechizoID) {
                h = hp;
                break;
            }
        }
        return h;
    }

    public HechizoPersonaje getHechizoPersonajePorPos(char pos) {
        HechizoPersonaje h = null;
        for (HechizoPersonaje hp : Hechizos) {
            if (hp.getPosicion() == pos) {
                h = hp;
                break;
            }
        }
        return h;
    }

    public void ordenarHechizos() {
        if (esMultiman()) {
            return;
        }
        ArrayList<Integer> Posiciones = new ArrayList<>();
        ArrayList<Integer> PosicionesOcupadas = new ArrayList<>();
        ArrayList<HechizoPersonaje> HechizosSinPos = new ArrayList<>();
        ArrayList<Integer> PosicionesBarra = new ArrayList<>();
        int i = 0;
        for (HechizoPersonaje hp : Hechizos) {
            if (!Character.toString(hp.getPosicion()).equalsIgnoreCase("_")) {
                PosicionesOcupadas.add((int) Encriptador.getNumeroPorValorHash(hp.getPosicion()));
            } else {
                HechizosSinPos.add(hp);
            }
        }
        for (int j = 1; j < 60; j++) {
            if (PosicionesOcupadas.contains(j) || j == 24) {
                continue;
            } else if ((j >= 15 && j < 25) || j >= 39) {
                PosicionesBarra.add(j);
            } else {
                Posiciones.add(j);
            }
        }
        Posiciones.addAll(PosicionesBarra);
        for (HechizoPersonaje hp :
                HechizosSinPos) {
            hp.setPosicion(Encriptador.getValorHashPorNumero(Posiciones.get(0)));
            Posiciones.remove(0);
        }
        if (EnLinea) {
            GestorSalida.INSTANCE.ENVIAR_SL_LISTA_HECHIZOS(this);
        }
    }

    public void fijarHechizosInicio() {
        if (esMultiman()) {
            return;
        }
        ArrayList<StatHechizo> tempHechizos = new ArrayList<>();
        for (HechizoPersonaje h : Hechizos) {
            if (h.getStatHechizo() == null) {
                continue;
            }
            switch (h.getStatHechizo().getTipo()) {
                case 0:// normales
                case 4: // de clase
                    continue;
            }
            tempHechizos.add(h.getStatHechizo());
        }
        Hechizos.clear();
        // _mapStatsHechizos.clear();
        for (int nivel = 1; nivel <= Nivel; nivel++) {
            Clase.aprenderHechizo(this, nivel);
        }
        int i = 1;
        for (HechizoPersonaje hp : Hechizos) {
            hp.setPosicion(Encriptador.getValorHashPorNumero(i));
            i++;
        }
        for (StatHechizo sh : tempHechizos) {
            fijarNivelHechizoOAprender(sh.getHechizoID(), sh.getGrado(), false);
        }
        if (EnLinea) {
            GestorSalida.INSTANCE.ENVIAR_SL_LISTA_HECHIZOS(this);
        }
    }

    public boolean fijarNivelHechizoOAprender(final int hechizoID, final int nivel, final boolean mensaje) {
        if (Encarnacion != null) {
            return false;
        }
        if (nivel > 0) {
            final Hechizo hechizo = Mundo.getHechizo(hechizoID);
            if (hechizo == null) {
                return false;
            }
            final StatHechizo statHechizo = hechizo.getStatsPorNivel(nivel);
            if (statHechizo == null || statHechizo.getNivelRequerido() > Nivel) {
                return false;
            }
            HechizoPersonaje h = getHechizoPersonajePorID(hechizoID);
            if (h == null) {
                addHechizoPersonaje('_', hechizo, nivel);
            } else {
                h.setNivel(nivel);
                // _mapStatsHechizos.put(hechizo.getID(), statHechizo);
            }
        } else {
            HechizoPersonaje h = getHechizoPersonajePorID(hechizoID);
            if (h == null) {
                return false;
            }
            Hechizos.remove(h);
            // _mapStatsHechizos.remove(hechizoID);
        }
        if (EnLinea) {
            GestorSalida.INSTANCE.ENVIAR_SUK_NIVEL_HECHIZO(this, hechizoID, nivel);
            if (mensaje) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "03;" + hechizoID);
            }
        }
        ordenarHechizos();
        return true;
    }

    public void analizarPosHechizos(final String str) {
        for (final String s : str.split(";")) {
            try {
                String[] split = s.split(",");
                int id = Integer.parseInt(split[0]);
                int nivel = Integer.parseInt(split[1]);
                char pos = split[2].charAt(0);
                HechizoPersonaje h2 = getHechizoPersonajePorPos(pos);
                if (h2 != null) {
                    h2.setPosicion('_');
                }
                addHechizoPersonaje(pos, Mundo.getHechizo(id), nivel);
            } catch (final Exception ignored) {
            }
        }
    }

    public void setPosHechizo(final int hechizoID, final char pos) {
        if (Encarnacion != null) {
            Encarnacion.setPosHechizo(hechizoID, pos, this);
            return;
        }
        if (pos == 'a') {
            GestorSalida.INSTANCE.ENVIAR_BN_NADA(this, "SET POS HECHIZO - POS INVALIDA");
            return;
        }
        HechizoPersonaje h = getHechizoPersonajePorID(hechizoID);
        if (h == null) {
            GestorSalida.INSTANCE.ENVIAR_BN_NADA(this, "SET POS HECHIZO - NO TIENE HECHIZO");
            return;
        }
        HechizoPersonaje h2 = getHechizoPersonajePorPos(pos);
        if (h2 != null) {
            h2.setPosicion(h.getPosicion());
        }
        h.setPosicion(pos);
        if (EnLinea) {
            GestorSalida.INSTANCE.ENVIAR_SL_LISTA_HECHIZOS(this);
        }
        GestorSalida.INSTANCE.ENVIAR_BN_NADA(this);
    }

    public StatHechizo getStatsHechizo(final int hechizoID) {
        if (Encarnacion != null) {
            return Encarnacion.getStatsHechizo(hechizoID);
        }
        HechizoPersonaje h = getHechizoPersonajePorID(hechizoID);
        if (h == null) {
            return null;
        }
        return h.getStatHechizo();
    }

    public void addHechizoPersonaje(char pos, Hechizo hechizo, int nivel) {
        Hechizos.add(new HechizoPersonaje(pos, hechizo, nivel));
        // _mapStatsHechizos.put(hechizo.getID(), hechizo.getStatsPorNivel(nivel));
    }

    public String stringHechizosParaSQL() {
        final StringBuilder str = new StringBuilder();
        for (HechizoPersonaje hp : Hechizos) {
            if (hp.getHechizo() == null) {
                continue;
            }
            if (str.length() > 0) {
                str.append(";");
            }
            str.append(hp.getHechizo().getID()).append(",").append(hp.getNivel()).append(",").append(hp.getPosicion());
        }
        return str.toString();
    }

    public String stringListaHechizos() {
        if (Encarnacion != null) {
            return Encarnacion.stringListaHechizos();
        }
        final StringBuilder str = new StringBuilder();
        for (HechizoPersonaje hp : Hechizos) {
            if (hp.getHechizo() == null) {
                continue;
            }
            if (str.length() > 0) {
                str.append(";");
            }
            str.append(hp.getHechizo().getID()).append("~").append(hp.getNivel()).append("~").append(hp.getPosicion());
        }
        return str.toString();
    }

    public boolean sePuedePonerEncarnacion() {
        return System.currentTimeMillis() - TiempoUltEncarnacion > 60000;
    }

    public String stringParaListaPJsServer() {
        final StringBuilder str = new StringBuilder("|");
        str.append(Id).append(";");
        str.append(Nombre).append(";");
        str.append(Nivel).append(";");
        str.append(getGfxID(false)).append(";");
        str.append(Color1 > -1 ? Integer.toHexString(Color1) : -1).append(";");
        str.append(Color2 > -1 ? Integer.toHexString(Color2) : -1).append(";");
        str.append(Color3 > -1 ? Integer.toHexString(Color3) : -1).append(";");
        str.append(getStringAccesorios()).append(";");
        str.append(EsMercante ? 1 : 0).append(";");
        str.append(AtlantaMain.SERVIDOR_ID).append(";");
        if (AtlantaMain.MODO_HEROICO || AtlantaMain.MAPAS_MODO_HEROICO.contains(Mapa.getId())) {
            str.append(esFantasma() ? 1 : 0).append(";");
        } else {
            str.append("0;");
        }
        str.append(";");
        str.append(AtlantaMain.NIVEL_MAX_PERSONAJE);
        return str.toString();
    }

    public void mostrarAmigosEnLinea(final boolean mostrar) {
        MostrarAmigos = mostrar;
    }

    public void mostrarRates() {
        if (Cuenta.getIdioma().equalsIgnoreCase("fr")) {
            GestorSalida.INSTANCE.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(this, "<b>Bienvenu sur " + AtlantaMain.NOMBRE_SERVER
                    + ": \nPrix ressource : " + AtlantaMain.PRECIO_SISTEMA_RECURSO + "\nKamas par : " + AtlantaMain.RATE_KAMAS
                    + "   \nDrop par : " + AtlantaMain.RATE_DROP_NORMAL + "\nXP PVM par : " + AtlantaMain.RATE_XP_PVM
                    + "   \nXP PVP par : " + AtlantaMain.RATE_XP_PVP + "\nHonor par : " + AtlantaMain.RATE_HONOR
                    + "\nXP metier par : " + AtlantaMain.RATE_XP_OFICIO + " \nRate Elevage par : "
                    + AtlantaMain.RATE_CRIANZA_MONTURA + "\nTemps pour mettre bas par : " + AtlantaMain.MINUTOS_GESTACION_MONTURA
                    + " minutos" + " \nLes familiers se nourissant toutes les " + AtlantaMain.MINUTOS_ALIMENTACION_MASCOTA
                    + " minutos</b>");
        } else {
            GestorSalida.INSTANCE.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(this, "<b>BIENVENIDO A " + AtlantaMain.NOMBRE_SERVER
                    + ": \nPRECIO RECURSO : " + AtlantaMain.PRECIO_SISTEMA_RECURSO + "\nKAMAS por : " + AtlantaMain.RATE_KAMAS
                    + "   \nDROP por : " + AtlantaMain.RATE_DROP_NORMAL + "\nXP PVM por : " + AtlantaMain.RATE_XP_PVM
                    + "   \nXP PVP por : " + AtlantaMain.RATE_XP_PVP + "\nHONOR por : " + AtlantaMain.RATE_HONOR
                    + "\nXP OFICIO por : " + AtlantaMain.RATE_XP_OFICIO + " \nCRIANZA DE PAVOS por : "
                    + AtlantaMain.RATE_CRIANZA_MONTURA + "\nTIEMPO PARIR MONTURA por : " + AtlantaMain.MINUTOS_GESTACION_MONTURA
                    + " minutos" + " \nLAS MASCOTAS SE ALIMENTARAN CADA " + AtlantaMain.MINUTOS_ALIMENTACION_MASCOTA
                    + " minutos</b>");
        }
    }

    public void mostrarTutorial() {
        if (Cuenta.getIdioma().equalsIgnoreCase("fr")) {
            if (!AtlantaMain.TUTORIAL_FR.isEmpty())
                GestorSalida.INSTANCE.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(this, AtlantaMain.TUTORIAL_FR);
        } else {
            if (!AtlantaMain.TUTORIAL_ES.isEmpty())
                GestorSalida.INSTANCE.ENVIAR_M145_MENSAJE_PANEL_INFORMACION(this, AtlantaMain.TUTORIAL_ES);
        }
    }

    public String strRopaDelPJ() {// ropa del personaje, stuff del personaje
        return "Oa" + Id + "|" + getStringAccesorios();
    }

    public String stringGMmercante() {
        final StringBuilder str = new StringBuilder();
        str.append(Celda.getId()).append(";");
        str.append("1;");
        str.append("0;");
        str.append(Id).append(";");
        str.append(Nombre).append("^").append(ColorNombre).append(";");
        str.append("-5" + ",");
        int titulo = getTitulo(false);
        if (titulo > 0) {
            str.append(titulo);
            if (Titulos.containsKey(titulo) && Titulos.get(titulo) > -1) {
                str.append("**").append(Titulos.get(titulo));
            }
        }
        str.append(";");
        str.append(getGfxID(false)).append("^").append(Talla).append(";");
        str.append(Color1 == -1 ? "-1" : Integer.toHexString(Color1)).append(";");
        str.append(Color2 == -1 ? "-1" : Integer.toHexString(Color2)).append(";");
        str.append(Color3 == -1 ? "-1" : Integer.toHexString(Color3)).append(";");
        str.append(getStringAccesorios()).append(";");
        // str.append(_miembroGremio.getGremio().getNombre() + ";" +
        // _miembroGremio.getGremio().getEmblema() + ";");
        if (MiembroGremio != null && MiembroGremio.getGremio().getCantidadMiembros() >= 10) {
            str.append(MiembroGremio.getGremio().getNombre()).append(";").append(MiembroGremio.getGremio().getEmblema()).append(";");
        } else {
            str.append(";;");
        }
        str.append("0");
        return str.toString();
    }

    public String stringGM() {
        final StringBuilder str = new StringBuilder();
        if (Pelea != null) {
            return "";
        }
        str.append(Celda.getId()).append(";");
        str.append(Orientacion).append(";");
        str.append(Ornamento).append("^").append(AtlantaMain.PARAM_AURA_VIP ? ((esAbonado() ? 1 : 0)) : "").append(";");
        str.append(Id).append(";");
        str.append(Nombre).append("^").append(ColorNombre).append(";");
        str.append(ClaseID).append(",");
        int titulo = getTitulo(false);
        if (titulo > 0) {
            str.append(titulo);
            if (Titulos.containsKey(titulo) && Titulos.get(titulo) > -1) {
                str.append("**").append(Titulos.get(titulo));
            }
        }
        if (!TituloVIP.isEmpty()) {
            str.append("~").append(TituloVIP);
        }
        str.append(";");
        str.append(getGfxID(true)).append("^").append(Talla).append(stringSeguidores()).append(";");
        str.append(Sexo).append(";");
        str.append(Alineacion).append(",");
        str.append(OrdenNivel).append(",");
        str.append(alasActivadas() ? GradoAlineacion : "0").append(",");
        str.append(Id + Nivel).append(",");
        str.append(Deshonor > 0 ? 1 : 0).append(";");
        str.append(Color1 < 0 ? "-1" : Integer.toHexString(Color1)).append(";");
        str.append(Color2 < 0 ? "-1" : Integer.toHexString(Color2)).append(";");
        str.append(Color3 < 0 ? "-1" : Integer.toHexString(Color3)).append(";");
        str.append(getStringAccesorios()).append(";");
        if (AtlantaMain.PARAM_ACTIVAR_AURA) {
            int aura = TotalStats.getTotalStatParaMostrar(Constantes.STAT_AURA);
//			GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this, "Posees el aura #" + aura+"" + ". ");
            str.append(aura != 0 ? aura : (Nivel / 100)).append(";");
        } else {
            str.append("0;");
        }
        str.append(";");
        str.append(";");
        if (MiembroGremio != null && MiembroGremio.getGremio().getCantidadMiembros() >= 10) {
            str.append(MiembroGremio.getGremio().getNombre()).append(";").append(MiembroGremio.getGremio().getEmblema()).append(";");
        } else {
            str.append(";;");
        }
        str.append(Integer.toString(RestriccionesBCharacter, 36)).append(";");
        str.append(Montando && Montura != null ? Montura.getStringColor(stringColor()) : "").append(";");// 19
        str.append(Math.max(0.35, TotalStats.getStatsObjetos().getStatParaMostrar(Constantes.STAT_MAS_VELOCIDAD) / 1000f)).append(";");
        str.append(Resets).append(";");
        return str.toString();
    }

    public String getStringAccesorios() {
        final StringBuilder str = new StringBuilder();
        str.append(strObjEnPosParaOa(Constantes.OBJETO_POS_ARMA)).append(",");// arma
        str.append(strObjEnPosParaOa(Constantes.OBJETO_POS_SOMBRERO)).append(",");// sombrero
        str.append(strObjEnPosParaOa(Constantes.OBJETO_POS_CAPA)).append(",");// capa
        str.append(strObjEnPosParaOa(Constantes.OBJETO_POS_MASCOTA)).append(",");// mascota
        str.append(strObjEnPosParaOa(Constantes.OBJETO_POS_ESCUDO)).append(",");// escudo
        str.append(Rostro).append(",");// change face
        try {
            if (getMiembroGremio() != null) {
                final String[] args = getMiembroGremio().getGremio().getEmblema().split(",");
                final String colorEscudo = Integer.toHexString(Integer.parseInt(args[1], 36));
                final int emblemaID = Integer.parseInt(args[2], 36);
                final int colorEmblema = Integer.parseInt(args[3], 36) + 1;
                str.append(colorEscudo).append("~").append(emblemaID).append("~").append(colorEmblema);
            }
        } catch (final Exception ignored) {
        }
        return str.toString();
    }

    public String stringStats() {
        final StringBuilder str = new StringBuilder("As");
        str.append(stringStatsComplemento());
        str.append(getIniciativa()).append("|");
        int base = 0, equipo = 0, bendMald = 0, buff = 0, total = 0;
        total = TotalStats.getTotalStatConComplemento(Constantes.STAT_MAS_PROSPECCION);
        // prospeccion
        str.append(total).append("|");
        final int[] stats = {111, 128, 118, 125, 124, 123, 119, 126, 117, 182, 112, 142, 165, 138, 178, 225, 226, 220, 115,
                122, 160, 161, 244, 214, 264, 254, 240, 210, 260, 250, 241, 211, 261, 251, 242, 212, 262, 252, 243, 213, 263, 253,
                410, 413, 419, 416, 415, 417, 418, 850};
        for (final int s : stats) {
            if (LiderIP != null && Pelea != null && s == 182) {
                base = 99;
                equipo = 99;
                bendMald = 99;
                buff = 99;
                total = 99;
            } else {
                base = TotalStats.getStatsBase().getStatParaMostrar(s);
                equipo = TotalStats.getStatsObjetos().getStatParaMostrar(s);
                bendMald = TotalStats.getStatsBendMald().getStatParaMostrar(s);
                buff = TotalStats.getStatsBuff().getStatParaMostrar(s);
                total = TotalStats.getTotalStatParaMostrar(s);
            }
            str.append(base).append(",").append(equipo).append(",").append(bendMald).append(",").append(buff).append(",").append(total).append("|");
        }
        return str.toString();
    }

    public String stringStats2() {
        final StringBuilder str = new StringBuilder("Ak");
        str.append(stringStatsComplemento());
        return str.toString();
    }

    public String stringStatsComplemento() {
        final StringBuilder str = new StringBuilder();
        str.append(stringExperiencia(",")).append("|");
        str.append(Kamas).append("|");
        if (Encarnacion != null) {
            str.append("0|0|");
        } else {
            str.append(PuntosStats).append("|").append(PuntosHechizos).append("|");
        }
        str.append(Alineacion).append("~");
        str.append(Alineacion).append(",");// fake alineacion, si son diferentes se activa haveFakeAlignment
        str.append(OrdenNivel).append(",");// orden alineacion
        str.append(GradoAlineacion).append(",");// nValue
        str.append(Honor).append(",");// nHonour
        str.append(Deshonor).append(",");// nDisgrace
        str.append(alasActivadas() ? "1" : "0").append("|");// bEnabled
        int PDV = getPdv();
        int PDVMax = getPdvMax();
        if (Pelea != null && Pelea.getLuchadorPorID(Id) != null) {
            final Luchador luchador = Pelea.getLuchadorPorID(Id);
            if (luchador != null) {
                PDV = luchador.getPdvConBuff();
                PDVMax = luchador.getPdvMaxConBuff();
            }
        }
        str.append(PDV).append(",").append(PDVMax).append("|");
        str.append(Energia).append(",10000|");
        return str.toString();
    }

    public String stringExperiencia(final String c) {
        return Mundo.getExpPersonaje(Nivel) + c + Experiencia + c + Mundo.getExpPersonaje(Nivel + 1);
    }

    public int emoteActivado() {
        return EmoteActivado;
    }

    public void setEmoteActivado(final byte emoteActivado) {
        EmoteActivado = emoteActivado;
    }

    public Collection<Objeto> getObjetosTodos() {
        return Objetos.values();
    }

    public void actualizarObjEquipStats() {
        TotalStats.getStatsObjetos().clear();
        boolean esEncarnacion = false;
        final ArrayList<Integer> listaSetsEquipados = new ArrayList<>();
        for (byte pos : Constantes.POSICIONES_EQUIPAMIENTO) {
            final Objeto objeto = getObjPosicion(pos);
            if (objeto == null) {
                continue;
            }
            if (objeto.getEncarnacion() != null) {
                esEncarnacion = true;
            }
            TotalStats.getStatsObjetos().acumularStats(objeto.getStats());
            final int setID = objeto.getObjModelo().getSetID();
            if (setID > 0 && !listaSetsEquipados.contains(setID)) {
                listaSetsEquipados.add(setID);
                final ObjetoSet OS = Mundo.getObjetoSet(setID);
                if (OS != null) {
                    TotalStats.getStatsObjetos().acumularStats(OS.getBonusStatPorNroObj(getNroObjEquipadosDeSet(setID)));
                }
            }
        } // actualizando
        if (esEncarnacion) {
            Montando = false;
        } else if (Montando && Montura != null) {
            TotalStats.getStatsObjetos().acumularStats(Montura.getStats());
        }
    }

    public Encarnacion getEncarnacionN() {
        return Encarnacion;
    }

    public boolean getDetalleExp() {
        return Mostrardetallexp;
    }

    public void setDetalleExp(boolean estado) {
        Mostrardetallexp = estado;
    }

    public boolean get_Refrescarmobspelea() {
        return Refrescarmobsautomatico;
    }

    public void set_Refrescarmobspelea(boolean estado) {
        Refrescarmobsautomatico = estado;
    }

    public boolean get_MostrarPanelPelea() {
        return Mostrarpanelpelea;
    }

    public void set_MostrarPanelPelea(boolean estado) {
        Mostrarpanelpelea = estado;
    }

    public int getIniciativa() {
        return Formulas.INSTANCE.getIniciativa(getTotalStats(), getPorcPDV());
    }

    public TotalStats getTotalStats() {
        return TotalStats;
    }

    public TotalStats getTotalStatsPelea() {
        if (Encarnacion != null && Encarnacion.getTotalStats() != null) {
            return Encarnacion.getTotalStats();
        }
        return TotalStats;
    }

    public byte getOrientacion() {
        return Orientacion;
    }

    public void setOrientacion(final byte orientacion) {
        Orientacion = orientacion;
    }

    public int getPodsUsados() {
        int pods = 0;
        for (final Objeto objeto : Objetos.values()) {
            if (objeto == null) {
                continue;
            }
            if (objeto.getObjModelo() == null) {
                AtlantaMain.redactarLogServidorln("El objeto " + objeto.getId() + ", objModelo " + objeto.getObjModeloID()
                        + " nulo OBJETOS");
                continue;
            }
            pods += Math.abs(objeto.getObjModelo().getPeso() * objeto.getCantidad());
        }
        for (final Objeto objeto : Tienda.getObjetos()) {
            if (objeto == null) {
                continue;
            }
            if (objeto.getObjModelo() == null) {
                AtlantaMain.redactarLogServidorln("El objeto " + objeto.getId() + ", objModelo " + objeto.getObjModeloID()
                        + " nulo TIENDA");
                continue;
            }
            pods += Math.abs(objeto.getObjModelo().getPeso() * objeto.getCantidad());
        }
        return pods;
    }

    public int getPodsMaximos() {
        int pods = TotalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_PODS);
        pods += TotalStats.getStatsBase().getStatParaMostrar(Constantes.STAT_MAS_FUERZA) * 5;
        pods += TotalStats.getStatsObjetos().getStatParaMostrar(Constantes.STAT_MAS_FUERZA) * 5;
        pods += TotalStats.getStatsBendMald().getStatParaMostrar(Constantes.STAT_MAS_FUERZA) * 5;
        for (final StatOficio SO : StatsOficios.values()) {
            if (SO == null) {
                continue;
            }
            if (SO.getPosicion() == 7) {
                continue;
            }
            pods += SO.getNivel() * 5;
            if (SO.getNivel() >= 100) {
                pods += 1000;
            }
        }
        pods *= AtlantaMain.RATE_PODS;
        if (pods < 1000) {
            pods = 1000;
        }
        if (this.tieneObjetoIDModelo(11290)) {
            pods += 500 * this.Tieneobjeto_Cantidad_PorModelo(11290);
        }
        return pods;
    }

    public int getPdv() {
        return Pdv;
    }

    @Override
    public void setPdv(int pdv) {
        setPdv(pdv, false);
    }

    public int getPdvMax() {
        return PdvMax;
    }

    public void addPDV(int pdv) {
        setPdv(Pdv + pdv, false);
        GestorSalida.INSTANCE.ENVIAR_As_STATS_DEL_PJ(this);
    }

    public void actualizarPDV(float porcPDV) {
        int oldPDVMAX = PdvMax;
        if (porcPDV < 1) {
            porcPDV = getPorcPDV();
        } else if (porcPDV > 100) {
            porcPDV = 100;
        }
        actualizarPDVMax();
        int PDV = Math.round(porcPDV * PdvMax / 100);
        setPdv(PDV, PdvMax != oldPDVMAX);
    }

    public void actualizarPDVMax() {
        if (Encarnacion != null && Encarnacion.getTotalStats() != null) {
            PdvMax = Encarnacion.getTotalStats().getTotalStatParaMostrar(Constantes.STAT_MAS_VITALIDAD);
        } else {
            PdvMax = Clase.getPDV() + ((Nivel - 1) * 5);
            PdvMax += getTotalStatsPelea().getTotalStatParaMostrar(Constantes.STAT_MAS_VITALIDAD);
        }
    }

    public void setPdv(int pdv, boolean cambioPDVMAX) {
        int oldPDV = Pdv;
        if (pdv > PdvMax || AtlantaMain.PARAM_AUTO_RECUPERAR_TODA_VIDA) {
            Pdv = PdvMax;
        } else if (pdv < 1) {
            Pdv = 1;
        } else {
            Pdv = pdv;
        }
        if (oldPDV == Pdv && !cambioPDVMAX) {
            return;
        }
        actualizarInfoGrupo();
        if (Pelea != null && Pelea.getFase() != Constantes.PELEA_FASE_COMBATE) {
            final Luchador luchador = Pelea.getLuchadorPorID(Id);
            if (luchador != null) {
                luchador.setPDVMAX(getPdvMax(), false);
                luchador.setPDV(getPdv());
                GestorSalida.INSTANCE.ENVIAR_GTM_INFO_STATS_TODO_LUCHADORES_A_PERSO(this, Pelea);
            }
        }
    }

    public void fullPDV() {
        actualizarPDV(100);
    }

    public float getPorcPDV() {
        if (PdvMax <= 0) {
            return 0;
        }
        float porc = Pdv * 100f / PdvMax;
        porc = Math.max(0, porc);
        porc = Math.min(100, porc);
        return porc;
    }

    public void setDelayTimerRegenPDV(int tiempo) {
        if (RecuperarVida != null) {
            if (tiempo == 0) {
                RecuperarVida.stop();
            } else {
                RecuperarVida.setDelay(tiempo);
                RecuperarVida.restart();
            }
            if (EnLinea) {
                GestorSalida.INSTANCE.ENVIAR_ILS_TIEMPO_REGENERAR_VIDA(this, tiempo);
            }
        }
    }

    public void crearTimerRegenPDV() {
        // regenerar vida PDV
        if (RecuperarVida == null) {
            RecuperarVida = new Timer(1000, e -> {
                if (AtlantaMain.MODO_BATTLE_ROYALE) {
                    Pdv--;
                } else {
                    if (Pelea != null || Pdv >= PdvMax) {
                        // return;
                    } else {
                        int iniciativa = Formulas.INSTANCE.getIniciativa(this.TotalStats, this.getPorcPDV());
                        Pdv++;
                        if (ActualizadorIniciativa != iniciativa) {
                            ActualizadorIniciativa = iniciativa;
                            GestorSalida.INSTANCE.ENVIAR_As_STATS_DEL_PJ(this);
                        }
                    }
                }
            });
        }
        // _recuperarVida.restart();
    }

    public void setSentado(final boolean sentado) {
        if (AtlantaMain.MODO_BATTLE_ROYALE) {
            return;
        }
        GestorSalida.INSTANCE.ENVIAR_As_STATS_DEL_PJ(this);
        Sentado = sentado;
        if (Sentado) {
            UltPDV = Pdv;
        }
        final int tiempo = Sentado ? 500 : 1000;
        if (EnLinea) {
            if (!Sentado) {
                GestorSalida.INSTANCE.ENVIAR_ILF_CANTIDAD_DE_VIDA(this, Pdv - UltPDV);
                setPdv(Pdv, true);
            }
        }
        setDelayTimerRegenPDV(tiempo);
        if (!sentado && (EmoteActivado == Constantes.EMOTE_SENTARSE || EmoteActivado == Constantes.EMOTE_ACOSTARSE)) {
            EmoteActivado = 0;// no hay emote
        }
    }

    public byte getAlineacion() {
        return Alineacion;
    }

    public void actualizarInfoGrupo() {
        if (Grupo != null) {
            GestorSalida.INSTANCE.ENVIAR_PM_ACTUALIZAR_INFO_PJ_GRUPO(Grupo, stringInfoGrupo());
        }
    }

    public void mostrarEmoteIcon(final String str) {
        try {
            if (Pelea == null) {
                GestorSalida.INSTANCE.ENVIAR_cS_EMOTICON_MAPA(Mapa, Id, Integer.parseInt(str));
            } else {
                GestorSalida.INSTANCE.ENVIAR_cS_EMOTE_EN_PELEA(Pelea, 7, Id, Integer.parseInt(str));
            }
        } catch (final Exception ignored) {
        }
    }

    public void salirPelea(boolean ptoSalvada, boolean borrarMapa) {
        if (!enLinea()) {
            desconectar(true);
        }
        if (esMultiman()) {
            return;
        }
        if (Pelea == null) {
            return;
        }
        if (Pelea.getTipoPelea() != Constantes.PELEA_TIPO_DESAFIO && !Pelea.esEspectador(Id)) {
            disminuirTurnos();
        }
        setPelea(null);
        Ocupado = false;
        if (Energia < 1) {
            return;
        }
        if (ptoSalvada) {
            try {
                if (borrarMapa) {
                    GestorSalida.INSTANCE.ENVIAR_GM_BORRAR_GM_A_MAPA(Mapa, Id);
                }
                Mapa = Mundo.getMapa(MapaSalvada);
                if (Nivel > 15 && Mapa.getSubArea().getArea().getSuperArea().getId() == 3) {
                    Mapa = Mundo.getMapa((short) 7411);
                    setCelda(Mapa.getCelda((short) 340));
                } else {
                    setCelda(Mapa.getCelda(CeldaSalvada));
                }
            } catch (final Exception ignored) {
            }
        }
        setSentado(false);
        // GestorSalida.ENVIAR_GV_RESETEAR_PANTALLA_JUEGO(this);
    }

    public void teleportPtoSalvada() {
        if (esMultiman()) {
            return;
        }
        short mapa = MapaSalvada;
        short celda = CeldaSalvada;
        if (Nivel > 15 && Mapa.getSubArea().getArea().getSuperArea().getId() == 3) {
            mapa = (short) 7411;
            celda = (short) 340;
        }
        teleport(mapa, celda);
    }

    public void addScrollStat(int statID, int cantidad) {
        int anterior = SubStatsScroll.get(statID);
        int valor = anterior + cantidad;
        if (valor > AtlantaMain.LIMITE_SCROLL) {
            valor = AtlantaMain.LIMITE_SCROLL;
            cantidad = AtlantaMain.LIMITE_SCROLL - anterior;
        }
        SubStatsScroll.put(statID, valor);
        TotalStats.getStatsBase().addStatID(statID, cantidad);
    }

    public int getStatScroll(int statID) {
        if (SubStatsScroll.get(statID) == null) {
            return 0;
        }
        return SubStatsScroll.get(statID);
    }

    public void addStatBase(int statID, int cantidad) {
        int valor = 0;
        if (SubStatsBase.get(statID) != null) {
            valor = SubStatsBase.get(statID) + cantidad;
        }
        SubStatsBase.put(statID, valor);
        TotalStats.getStatsBase().addStatID(statID, cantidad);
    }

    public synchronized void boostStat2(final int tipo, int puntosUsar) {
        if (esMultiman()) {
            return;
        }
        if (PuntosStats <= 0) {
            return;
        }
        int statID = 0, usados = 0;
        switch (tipo) {
            case 10:
                statID = (Constantes.STAT_MAS_FUERZA);
                break;
            case 11:
                statID = (Constantes.STAT_MAS_VITALIDAD);
                break;
            case 12:
                statID = (Constantes.STAT_MAS_SABIDURIA);
                break;
            case 13:
                statID = (Constantes.STAT_MAS_SUERTE);
                break;
            case 14:
                statID = (Constantes.STAT_MAS_AGILIDAD);
                break;
            case 15:
                statID = (Constantes.STAT_MAS_INTELIGENCIA);
                break;
        }
        if (puntosUsar > PuntosStats) {
            puntosUsar = PuntosStats;
        }
        int valorStat = 0;
        BoostStat boost;
        boolean mod = false;
        while (true) {
            valorStat = TotalStats.getStatsBase().getStatParaMostrar(statID);
            boost = Clase.getBoostStat(statID, valorStat);
            usados += boost.getCoste();
            if (usados <= puntosUsar) {
                PuntosStats -= boost.getCoste();
                mod = true;
                addStatBase(statID, boost.getPuntos());
            } else {
                break;
            }
        }
        if (statID == Constantes.STAT_MAS_VITALIDAD) {// vitalidad
            actualizarPDV(0);
        }
        if (mod) {
            refrescarStuff(false, true, false);
        }
    }

    public String stringObjetosABD() {
        final StringBuilder str = new StringBuilder();
        for (final Objeto obj : Objetos.values()) {
            if (str.length() > 0) {
                str.append("|");
            }
            str.append(obj.getId());
        }
        return str.toString();
    }

    public boolean estaMuteado() {
        return Cuenta.estaMuteado();
    }

    public Objeto getObjIdentInventario(final Objeto objeto, Objeto prohibido) {
        if (objeto.puedeTenerStatsIguales()) {
            for (final Objeto obj : Objetos.values()) {
                if (obj.getPosicion() != Constantes.OBJETO_POS_NO_EQUIPADO) {
                    continue;
                }
                if (objeto.getId() == obj.getId()) {
                    continue;
                }
                if (prohibido != null && prohibido.getId() == obj.getId()) {
                    continue;
                }
                if (obj.getObjModeloID() == objeto.getObjModeloID() && obj.sonStatsIguales(objeto)) {
                    return obj;
                }
            }
        }
        return null;
    }

    public boolean addObjIdentAInventario(final Objeto objeto, final boolean eliminar) {
        if (Objetos.containsKey(objeto.getId())) {
            return false;
        }
        // tipo piedra de alma y mascota
        Objeto igual = getObjIdentInventario(objeto, null);
        if (igual != null) {
            igual.setCantidad(igual.getCantidad() + objeto.getCantidad());
            GestorSalida.INSTANCE.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, igual);
            if (eliminar && objeto.getId() > 0) {
                Mundo.eliminarObjeto(objeto.getId());
            }
            return true;
        }
        addObjetoConOAKO(objeto, true);
        return false;
    }

    public boolean addObjIdentAInventario(final Objeto objeto, final boolean eliminar, final int posicion) {
        if (Objetos.containsKey(objeto.getId())) {
            return false;
        }
        // tipo piedra de alma y mascota
        Objeto igual = Mundo.INSTANCE.ObjIdenticoPerso(objeto, null, this);
        if (igual != null) {
            igual.setCantidad(igual.getCantidad() + objeto.getCantidad());
            GestorSalida.INSTANCE.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, igual);
            if (eliminar && objeto.getId() > 0) {
                Mundo.eliminarObjeto(objeto.getId());
            }
            return true;
        }
        addObjetoConOAKO(objeto, true);
        return false;
    }

    public boolean addObjIdentAInventario(final Objeto objeto, final boolean eliminar, final boolean OAKO) {
        if (Objetos.containsKey(objeto.getId())) {
            return false;
        }
        // tipo piedra de alma y mascota
        Objeto igual = getObjIdentInventario(objeto, null);
        if (igual != null) {
            igual.setCantidad(igual.getCantidad() + objeto.getCantidad());
            GestorSalida.INSTANCE.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, igual);
            if (eliminar && objeto.getId() > 0) {
                Mundo.eliminarObjeto(objeto.getId());
            }
            return true;
        }
        addObjetoConOAKO(objeto, OAKO);
        return false;
    }

    public void addObjDropPelea(final Objeto objeto, final boolean eliminar) {
        if (Objetos.containsKey(objeto.getId()))
            return;
        // tipo piedra de alma y mascota
        Objeto igual = getObjIdentInventario(objeto, null);
        if (igual != null) {
            igual.setCantidad(igual.getCantidad() + objeto.getCantidad());
            DropPelea.put(igual, false);
            if (eliminar && objeto.getId() > 0) {
                Mundo.eliminarObjeto(objeto.getId());
            }
            return;
        }
        addObjetoConOAKO(objeto, false);
        DropPelea.put(objeto, true);
    }

    public void addObjetoConOAKO(final Objeto objeto, final boolean enviarOAKO) {
        if (objeto.getId() == 0) {
            Mundo.addObjeto(objeto, false);
        }
        Objetos.put(objeto.getId(), objeto);
        byte pos = objeto.getPosicion();
        if (Constantes.esPosicionObjeto(pos)) {
            if (Constantes.esPosicionEquipamiento(pos)) {
                boolean desequipar = false;
                if (objeto.getObjModelo().getNivel() > Nivel) {
                    desequipar = true;
                } else if (puedeEquiparRepetido(objeto.getObjModelo(), 1)) {
                    desequipar = true;
                }
                if (desequipar) {
                    pos = Constantes.OBJETO_POS_NO_EQUIPADO;
                    objeto.setPosicion(pos);
                }
            }
            cambiarPosObjeto(objeto, Constantes.OBJETO_POS_NO_EQUIPADO, pos, true);
        }
        if (EnLinea && enviarOAKO) {
            GestorSalida.INSTANCE.ENVIAR_OAKO_APARECER_OBJETO(this, objeto);
        }
    }

    public void borrarOEliminarConOR(final int id, final boolean eliminar) {
        if (borrarObjeto(id) && EnLinea) {
            GestorSalida.INSTANCE.ENVIAR_OR_ELIMINAR_OBJETO(this, id);
        }
        if (eliminar) {
            Mundo.eliminarObjeto(id);
        }
    }

    public boolean borrarObjeto(final int id) {
        if (Objetos.get(id) != null) {
            byte pos = Objetos.get(id).getPosicion();
            if (pos == Constantes.OBJETO_POS_NO_EQUIPADO || pos >= ObjPos49.length) {
                return Objetos.remove(id) != null;
            }
            if (ObjPos49[pos].getId() == id) {
                cambiarPosObjeto(null, pos, Constantes.OBJETO_POS_NO_EQUIPADO, true);
            }
        }
        return Objetos.remove(id) != null;
    }

    public void cambiarPosObjeto(final Objeto obj, final byte oldPos, final byte newPos, boolean refrescarStuff) {
        if (oldPos == newPos) {
            return;
        }
        if (oldPos != Constantes.OBJETO_POS_NO_EQUIPADO && oldPos < ObjPos49.length) {
            if (ObjPos49[oldPos] != null) {
                if (obj == null || obj.getId() == ObjPos49[oldPos].getId()) {
                    ObjPos49[oldPos].setPosicion(Constantes.OBJETO_POS_NO_EQUIPADO);
                    ObjPos49[oldPos] = null;
                }
            }
        }
        if (newPos != Constantes.OBJETO_POS_NO_EQUIPADO && newPos < ObjPos49.length) {
            ObjPos49[newPos] = obj;
        }
        if (obj != null) {
            obj.setPosicion(newPos);
        }
        if (EnLinea) {
            boolean visual = false;
            if (Constantes.esPosicionVisual(newPos) || Constantes.esPosicionVisual(oldPos)) {
                visual = true;
            }
            if (Constantes.esPosicionEquipamiento(newPos) || Constantes.esPosicionEquipamiento(oldPos)) {
                actualizarObjEquipStats();
                if (refrescarStuff) {
                    refrescarStuff(false, true, visual);
                }
            }
        }
    }

    public boolean restarCantObjOEliminar(final int idObjeto, int cantidad, final boolean eliminar) {
        try {
            final Objeto obj = Objetos.get(idObjeto);
            if (obj == null) {
                return false;
            }
            if (cantidad > obj.getCantidad()) {
                cantidad = obj.getCantidad();
            }
            if (obj.getCantidad() - cantidad > 0) {
                obj.setCantidad(obj.getCantidad() - cantidad);
                if (EnLinea) {
                    GestorSalida.INSTANCE.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, obj);
                }
            } else {
                borrarOEliminarConOR(obj.getId(), eliminar);
            }
            return true;
        } catch (final Exception ignored) {
        }
        return false;
    }

    public boolean tenerYEliminarObjPorModYCant(final int idModelo, int cantidad) {
        if (idModelo == -1) {
            return true;
        }
        final ArrayList<Objeto> listaObjBorrar = new ArrayList<>();
        for (final Objeto obj : Objetos.values()) {
            if (obj.getObjModeloID() != idModelo) {
                continue;
            }
            if (obj.getCantidad() >= cantidad) {
                final int nuevaCant = obj.getCantidad() - cantidad;
                if (nuevaCant > 0) {
                    obj.setCantidad(nuevaCant);
                    GestorSalida.INSTANCE.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, obj);
                } else {
                    listaObjBorrar.add(obj);
                }
                for (final Objeto objBorrar : listaObjBorrar) {
                    borrarOEliminarConOR(objBorrar.getId(), true);
                }
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "022;" + cantidad + "~" + idModelo);
                return true;
            } else {
                cantidad -= obj.getCantidad();
                listaObjBorrar.add(obj);
            }
        }
        return false;
    }

    public int restarObjPorModYCant(final int idModelo, int cantidad) {
        final ArrayList<Objeto> listaObjBorrar = new ArrayList<>();
        int eliminados = 0;
        for (final Objeto obj : Objetos.values()) {
            if (obj.getObjModeloID() != idModelo) {
                continue;
            }
            if (cantidad <= 0) {
                break;
            }
            if (obj.getCantidad() - cantidad > 0) {
                eliminados += cantidad;
                obj.setCantidad(obj.getCantidad() - cantidad);
                GestorSalida.INSTANCE.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, obj);
                break;
            } else {
                cantidad -= obj.getCantidad();
                eliminados += obj.getCantidad();
                listaObjBorrar.add(obj);
            }
        }
        for (final Objeto objBorrar : listaObjBorrar) {
            borrarOEliminarConOR(objBorrar.getId(), true);
        }
        return eliminados;
    }

    public int eliminarPorObjModeloRecibidoDesdeMinutos(final int objModeloID, int recibidoMinutos) {
        final ArrayList<Objeto> lista = new ArrayList<Objeto>(Objetos.values());
        int eliminados = 0;
        for (final Objeto obj : lista) {
            if (obj.getObjModeloID() != objModeloID) {
                continue;
            }
            if (recibidoMinutos > 0) {
                if (obj.tieneStatTexto(Constantes.STAT_RECIBIDO_EL)) {
                    if (obj.getDiferenciaTiempo(Constantes.STAT_RECIBIDO_EL, 60 * 1000) < recibidoMinutos) {
                        continue;
                    }
                }
            }
            eliminados += obj.getCantidad();
            borrarOEliminarConOR(obj.getId(), true);
        }
        return eliminados;
    }

    public Objeto getObjPosicion(final byte pos) {
        try {
            if (pos > Constantes.OBJETO_POS_NO_EQUIPADO) {
                return ObjPos49[pos];
            }
        } catch (final Exception ignored) {
        }
        return null;
    }

    public int Tieneobjeto_Cantidad_PorModelo(final int idModelo) {
        int cantidad = 0;
        for (final Objeto obj : Objetos.values()) {
            if (obj.getObjModeloID() != idModelo) {
                continue;
            }
            cantidad += obj.getCantidad();
        }
        return cantidad;
    }

    public Objeto getObjeto(final int id) {
        return Objetos.get(id);
    }

    public boolean tieneObjetoID(final int id) {
        return Objetos.containsKey(id);
    }

    public boolean tieneObjetoIDModelo(final int idModelo) {
        int cantidad = 0;
        for (final Objeto obj : Objetos.values()) {
            if (obj.getObjModeloID() != idModelo) {
                continue;
            }
            cantidad += obj.getCantidad();
        }
        return cantidad > 0;
    }

    public boolean tieneObjPorModYCant(final int idModelo, int cantidad) {
        for (final Objeto obj : Objetos.values()) {
            if (obj.getObjModeloID() != idModelo) {
                continue;
            }
            if (obj.getCantidad() >= cantidad) {
                return true;
            } else {
                cantidad -= obj.getCantidad();
            }
        }
        return false;
    }

    public String strListaObjetos() {
        final StringBuilder str = new StringBuilder();
        TreeMap<Integer, Objeto> objetos = new TreeMap<>(Objetos);
        for (final Objeto obj : objetos.values()) {
            if (obj == null) {
                continue;
            }
            str.append(obj.stringObjetoConGuiño());
        }
        return str.toString();
    }

    public String getObjetosPersonajePorID(final String separador) {
        final StringBuilder str = new StringBuilder();
        for (final int id : Objetos.keySet()) {
            if (str.length() != 0) {
                str.append(separador);
            }
            str.append(id);
        }
        return str.toString();
    }

    public void venderObjetos(String packet) {
        for (String str : packet.split(";")) {
            try {
                final String[] infos = str.split(Pattern.quote("|"));
                int id = Integer.parseInt(infos[0]);
                int cant = Integer.parseInt(infos[1]);
                final Objeto objeto = Objetos.get(id);
                if (objeto.getPosicion() != Constantes.OBJETO_POS_NO_EQUIPADO || objeto.getObjModelo()
                        .getTipo() == Constantes.OBJETO_TIPO_OBJETO_DE_BUSQUEDA) {
                    GestorSalida.INSTANCE.ENVIAR_ESE_ERROR_VENTA(this);
                    continue;
                }
                if (objeto.tieneStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER)) {
                    if (!objeto.getParamStatTexto(Constantes.STAT_PERTENECE_Y_NO_VENDER, 4).equalsIgnoreCase(Nombre)) {
                        return;
                    }
                }
                if (objeto.getCantidad() < cant) {
                    cant = objeto.getCantidad();
                }
                final long kamas = objeto.getObjModelo().getKamas() * cant;
                int ogrinas = 0;
                try {
                    ogrinas = objeto.getObjModelo().getOgrinas() * cant;
                } catch (final Exception e) {
                    continue;
                }
                if (ogrinas < 0 || kamas < 0) {
                    GestorSalida.INSTANCE.ENVIAR_ESE_ERROR_VENTA(this);
                    continue;
                }
                if (kamas == 0 && ogrinas > 0) {
                    if (!AtlantaMain.PARAM_DEVOLVER_OGRINAS) {
                        continue;
                    }
                    GestorSQL.INSTANCE.ADD_OGRINAS_CUENTA((int) (ogrinas * AtlantaMain.FACTOR_DEVOLVER_OGRINAS), Cuenta.getId());
                } else {
                    addKamas(kamas / 10, false, false);
                }
                if (objeto.getCantidad() - cant < 1) {
                    borrarOEliminarConOR(id, true);
                } else {
                    objeto.setCantidad(objeto.getCantidad() - cant);
                    GestorSalida.INSTANCE.ENVIAR_OQ_CAMBIA_CANTIDAD_DEL_OBJETO(this, objeto);
                }
                Thread.sleep(150);
            } catch (final Exception e) {
                GestorSalida.INSTANCE.ENVIAR_ESE_ERROR_VENTA(this);
            }
        }
        GestorSalida.INSTANCE.ENVIAR_ESK_VENDIDO(this);
        GestorSalida.INSTANCE.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
        GestorSalida.INSTANCE.ENVIAR_Ow_PODS_DEL_PJ(this);
    }

    public void addExperiencia(final long experiencia, boolean mensaje) {
        if (esMultiman() || xp_bloqueada) {
            this.enviarmensajeRojo("Recuerda que tu xp esta bloqueada\n" +
                    "Usa .block_xp para liberarla");
            return;
        }
        int exNivel = Nivel;
        int nuevoNivel = Nivel;
        if (Encarnacion != null) {
            exNivel = Encarnacion.getNivel();
            Encarnacion.addExperiencia(experiencia, this);
            nuevoNivel = Encarnacion.getNivel();
        } else {
            ExperienciaDia += experiencia;
            Experiencia += experiencia;
            while (Experiencia >= Mundo.getExpPersonaje(Nivel + 1) && Nivel < AtlantaMain.NIVEL_MAX_PERSONAJE) {
                subirNivel(false);
            }
            nuevoNivel = Nivel;
        }
        if (exNivel < nuevoNivel) {
            fullPDV();
        }
        if (EnLinea) {
            if (mensaje) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "08;" + experiencia);
            }
            if (exNivel < nuevoNivel) {
                GestorSalida.INSTANCE.ENVIAR_AN_MENSAJE_NUEVO_NIVEL(this, nuevoNivel);
                GestorSalida.INSTANCE.ENVIAR_SL_LISTA_HECHIZOS(this);
            }
            GestorSalida.INSTANCE.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
        }
    }

    public void subirHastaNivel(int nivel) {
        if (nivel > Nivel) {
            while (Nivel < nivel) {
                subirNivel(true);
            }
            fullPDV();
            if (EnLinea) {
                GestorSalida.INSTANCE.ENVIAR_SL_LISTA_HECHIZOS(this);
                GestorSalida.INSTANCE.ENVIAR_AN_MENSAJE_NUEVO_NIVEL(this, Nivel);
                GestorSalida.INSTANCE.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
            }
        }
    }

    public void subirNivel(final boolean expDeNivel) {
        if (esMultiman()) {
            return;
        }
        if (Nivel == AtlantaMain.NIVEL_MAX_PERSONAJE || Encarnacion != null) {
            return;
        }
        Nivel += 1;
        if (!AtlantaMain.MODO_PVP && Nivel == AtlantaMain.NIVEL_MAX_PERSONAJE && AtlantaMain.INSTANCE.getANUNCIO_NIVEL_MAX()) {
            GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION_A_TODOS("1CONGRATULATIONS_LVL_MAX;" + Nombre);
        }
        if (AtlantaMain.RATE_RANDOM_PUNTOS == 1) {
            PuntosStats += AtlantaMain.PUNTOS_STATS_POR_NIVEL;
            PuntosHechizos += AtlantaMain.PUNTOS_HECHIZO_POR_NIVEL;
        } else {
            PuntosStats += AtlantaMain.PUNTOS_STATS_POR_NIVEL * AtlantaMain.FUN_RANDOM_PUNTOS();
            PuntosHechizos += AtlantaMain.PUNTOS_HECHIZO_POR_NIVEL * AtlantaMain.FUN_RANDOM_PUNTOS();
        }
        if (Nivel == 100) {
            TotalStats.getStatsBase().addStatID(Constantes.STAT_MAS_PA, 1);
        }
        Clase.aprenderHechizo(this, Nivel);
        if (expDeNivel) {
            Experiencia = Mundo.getExpPersonaje(Nivel);
        }
    }

    public void cambiarNivelYAlineacion(int nivel, byte alineacion) {
        if (AtlantaMain.NIVEL_MAX_ESCOGER_NIVEL <= 1) {
            return;
        }
        if (nivel > AtlantaMain.NIVEL_MAX_ESCOGER_NIVEL) {
            nivel = AtlantaMain.NIVEL_MAX_ESCOGER_NIVEL;
        }
        subirHastaNivel(nivel);
        if (AtlantaMain.MODO_PVP) {
            if ((alineacion == Constantes.ALINEACION_BONTARIANO || alineacion == Constantes.ALINEACION_BRAKMARIANO)
                    && alineacion != Alineacion) {
                cambiarAlineacion(alineacion, false);
            }
            if (Alineacion == Constantes.ALINEACION_NEUTRAL) {
                GestorSalida.INSTANCE.ENVIAR_bA_ESCOGER_NIVEL(this);
            }
        }
        RecienCreado = false;
    }

    public Map<Byte, StatOficio> getStatsOficios() {
        return StatsOficios;
    }

    public StatOficio getStatOficioPorID(final int oficioID) {
        for (final StatOficio SO : StatsOficios.values()) {
            if (SO.getOficio().getId() == oficioID) {
                return SO;
            }
        }
        return null;
    }

    public StatOficio getStatOficioPorTrabajo(final int skillID) {
        for (final StatOficio SO : StatsOficios.values()) {
            if (SO.esValidoTrabajo(skillID)) {
                return SO;
            }
        }
        return null;
    }

    public int getNivelStatOficio(int oficioID) {
        try {
            StatOficio so = getStatOficioPorID(oficioID);
            if (so != null)
                return so.getNivel();
        } catch (Exception ignored) {
        }
        return 0;
    }

    public String stringOficios() {
        final StringBuilder str = new StringBuilder();
        for (byte i = 0; i < 6; i++) {
            try {
//                _statsOficios.get(i).getNivel();// es para activar el exception
                if (StatsOficios.get(i) == null) {
                    continue;
                }
                if (str.length() > 0) {
                    str.append(";");
                }
                str.append(StatsOficios.get(i).getOficio().getId()).append(",").append(StatsOficios.get(i).getExp());
            } catch (Exception ignored) {
            }
        }
        return str.toString();
    }

    public boolean olvidarOficio(final int oficio) {
        try {
            byte id = -1;
            for (Entry<Byte, StatOficio> s : StatsOficios.entrySet()) {
                if (s.getValue().getOficio().getId() == oficio) {
                    id = s.getKey();
                    break;
                }
            }
            if (id != -1) {
                StatsOficios.remove(id);
                GestorSalida.INSTANCE.ENVIAR_JR_OLVIDAR_OFICIO(this, oficio);
            }
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    public boolean puedeAprenderOficio(int oficioID) {
        if (!AtlantaMain.PARAM_PERMITIR_OFICIOS) {
            return false;
        }
        boolean esMago = Constantes.esOficioMago(oficioID);
        if (esMago) {
            int oficioPrimario = Constantes.getOficioPrimarioDeMago(oficioID);
            if (getNivelStatOficio(oficioPrimario) < 65) {
                if (EnLinea)
                    GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "16|" + oficioPrimario);
                return false;
            }
        }
        byte cant = 0, nvl30 = 0;
        for (final StatOficio SO : StatsOficios.values()) {
            if (SO.getPosicion() == 7) {
                continue;
            }
            if (SO.getOficio().getId() == oficioID) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "111");
                return false;
            }
            if (Constantes.esOficioMago(SO.getOficio().getId())) {
                if (esMago) {
                    cant++;
                    if (SO.getNivel() >= 30)
                        nvl30++;
                }
            } else if (!esMago) {
                cant++;
                if (SO.getNivel() >= 30)
                    nvl30++;
            }
        }
        if (StatsOficios.size() >= 7 || cant >= 3) {
            if (EnLinea) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "19");
            }
            return false;
        }
        if (nvl30 < cant) {
            if (EnLinea)
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "18;30");
            return false;
        }
        return true;
    }

    public int aprenderOficio(final Oficio oficio, int exp) {
        if (!AtlantaMain.PARAM_PERMITIR_OFICIOS) {
            return -1;
        }
        boolean esMago = Constantes.esOficioMago(oficio.getId());
        byte pos = -1, cant = 0;
        for (final StatOficio SO : StatsOficios.values()) {
            if (SO.getPosicion() == 7) {
                continue;
            }
        }
        for (final StatOficio SO : StatsOficios.values()) {
            if (SO.getPosicion() == 7) {
                continue;
            }
            if (SO.getOficio().getId() == oficio.getId()) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "111");
                return -1;
            }
            if (Constantes.esOficioMago(SO.getOficio().getId())) {
                if (esMago) {
                    cant++;
                }
            } else if (!esMago) {
                cant++;
            }
        }
        if (cant >= 3) {
            if (EnLinea)
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "19");
            return -1;
        }
        for (byte p = 0; p < 6; p++) {
            if (StatsOficios.get(p) == null) {
                pos = p;
                break;
            }
        }
        if (pos == -1) {
            if (EnLinea) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "19");
            }
            return -1;
        }
        final StatOficio statOficio = new StatOficio(pos, oficio, exp);
        StatsOficios.put(pos, statOficio);
        if (EnLinea) {
            GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "02;" + oficio.getId());
            GestorSalida.INSTANCE.ENVIAR_JS_SKILL_DE_OFICIO(this, statOficio);
            GestorSalida.INSTANCE.ENVIAR_JX_EXPERINENCIA_OFICIO(this, statOficio);
            GestorSalida.INSTANCE.ENVIAR_JO_OFICIO_OPCIONES(this, statOficio);
            verificarHerramientOficio();
        }
        return pos;
    }

    public boolean tieneDon(int id, int nivel) {
        Especialidad esp = Mundo.getEspecialidad(Orden, OrdenNivel);
        if (esp == null) {
            return false;
        }
        for (Don don : esp.getDones()) {
            if (don.getID() == id && don.getNivel() >= nivel) {
                return true;
            }
        }
        return false;
    }

    public boolean tieneObjModeloEquipado(final int id) {
        for (byte pos : Constantes.POSICIONES_EQUIPAMIENTO) {
            final Objeto obj = getObjPosicion(pos);
            if (obj == null) {
                continue;
            }
            if (obj.getObjModeloID() == id) {
                return true;
            }
        }
        return false;
    }

    public int cantEquipadoModelo(final int id) {
        int i = 0;
        for (byte pos : Constantes.POSICIONES_EQUIPAMIENTO) {
            final Objeto obj = getObjPosicion(pos);
            if (obj == null) {
                continue;
            }
            if (obj.getObjModeloID() == id) {
                i++;
            }
        }
        return i;
    }

    public void setInvitandoA(final Personaje invitando, String tipo) {
        InvitandoA = invitando;
        TipoInvitacion = tipo;
    }

    public Personaje getInvitandoA() {
        return InvitandoA;
    }

    public void setInvitador(final Personaje invitando, String tipo) {
        Invitador = invitando;
        TipoInvitacion = tipo;
    }

    public Personaje getInvitador() {
        return Invitador;
    }

    public String getTipoInvitacion() {
        return TipoInvitacion;
    }

    public boolean esMaestro() {
        return Grupo != null && Grupo.esLiderGrupo(this) && Grupo.tieneAlumnos();
    }

    public String stringInfoGrupo() {
        final StringBuilder str = new StringBuilder();
        str.append(Id).append(";");
        str.append(Nombre).append(";");
        str.append(getGfxID(false)).append(";");
        str.append(Color1).append(";");
        str.append(Color2).append(";");
        str.append(Color3).append(";");
        str.append(getStringAccesorios()).append(";");
        str.append(Pdv).append(",").append(PdvMax).append(";");
        str.append(Nivel).append(";");
        str.append(getIniciativa()).append(";");
        str.append(TotalStats.getTotalStatConComplemento(Constantes.STAT_MAS_PROSPECCION)).append(";");
        str.append("1");
        return str.toString();
    }

    public int getNroObjEquipadosDeSet(final int setID) {
        int nro = 0;
        for (byte pos : Constantes.POSICIONES_EQUIPAMIENTO) {
            final Objeto obj = getObjPosicion(pos);
            if (obj == null) {
                continue;
            }
            if (obj.getObjModelo().getSetID() == setID) {
                nro++;
            }
        }
        return nro;
    }

    public boolean puedeIniciarTrabajo(final int skillID, final ObjetoInteractivo objInterac, final int unicaID,
                                       final Celda celda) {
        try {
            StatOficio statOficio = getStatOficioPorTrabajo(skillID);
            if (statOficio == null) {
                return false;
            }
            Objeto arma = getObjPosicion(Constantes.OBJETO_POS_ARMA);
            int idObjModelo = arma != null ? arma.getObjModeloID() : -1;
            if (!statOficio.getOficio().esHerramientaValida(idObjModelo)) {
                return false;
            }
            if (idObjModelo != -1) {
                int distHerramienta = 2;
                if (arma.getObjModelo().getStatHechizo() != null) {
                    distHerramienta = Math.max(2, arma.getObjModelo().getStatHechizo().getMaxAlc());
                }
                int dist = Camino.distanciaDosCeldas(Mapa, Celda.getId(), celda.getId());
                if (dist == 0 || dist > distHerramienta) {
                    return false;
                }
            }
            return statOficio.iniciarTrabajo(skillID, this, objInterac, unicaID, celda);
        } catch (Exception ignored) {
        }
        // esta lejos para realizar el trabajo
        return false;
    }

    public boolean finalizarTrabajo(final int skillID) {
        StatOficio skill = getStatOficioPorTrabajo(skillID);
        if (skill == null) {
            return false;
        }
        return skill.finalizarTrabajo(this);
    }

    public boolean puedeIniciarAccionEnCelda(final AccionDeJuego AJ) {
        try {
            if (AJ == null) {
                return false;
            }
            short celdaID = Short.parseShort(AJ.getPathPacket().split(";")[0]);
            int skillID = Integer.parseInt(AJ.getPathPacket().split(";")[1]);
            Celda celda = Mapa.getCelda(celdaID);
            switch (Orientacion) {
                case 0:
                case 2:
                case 4:
                case 6:
                case 8:
                    cambiarOrientacionADiagonal();
                    break;
            }
            boolean puede = false;
            if (celda.puedeHacerAccion(skillID, PescarKuakua)) {
                puede = celda.puedeIniciarAccion(this, AJ);
            }
            if (esMaestro()) {
                Grupo.packetSeguirLider(AJ.getPacket());
            }
            return puede;
        } catch (final Exception e) {
            String error = "EXCEPTION iniciarAccionEnCelda AJ.getPacket(): " + AJ.getPathPacket() + " e: " + e.toString();
            GestorSalida.INSTANCE.ENVIAR_BN_NADA(this, error);
            AtlantaMain.redactarLogServidorln(error);
        }
        // si no puede realizar la accion porque no esta cerca al IO
        return false;
    }

    public void finalizarAccionEnCelda(final AccionDeJuego AJ) {
        try {
            if (AJ != null) {
                short celdaID = Short.parseShort(AJ.getPathPacket().split(";")[0]);
                Mapa.getCelda(celdaID).finalizarAccion(this, AJ);
            }
        } catch (final Exception e) {
            String error = "EXCEPTION finalizarAccionEnCelda e:" + e.toString();
            GestorSalida.INSTANCE.ENVIAR_BN_NADA(this, error);
            AtlantaMain.redactarLogServidorln(error);
        }
    }

    public boolean inicioAccionMoverse(final AccionDeJuego AJ) {
        try {
            if (AJ == null) {
                GestorSalida.INSTANCE.ENVIAR_BN_NADA(this, "inicioAccionMoverse AJ null");
                return false;
            }
            if (Pelea == null) {
                // no hay pelea
                return inicioMovimiento(AJ);
            } else { // pelea
                Luchador luch = Pelea.getLuchadorPorID(getId());
                if (!luch.puedeJugar()) {
                    if (getCompañero() == null) {
                        return false;
                    }
                    luch = Pelea.getLuchadorPorID(getCompañero().getId());
                    if (!luch.puedeJugar()) {
                        return false;
                    }
                }
                String moverse = Pelea.intentarMoverse(luch, AJ.getPathPacket(), AJ.getIDUnica(), AJ);
                return (moverse.equals("ok") || moverse.equals("stop"));
            }
        } catch (Exception e) {
            String error = "EXCEPTION inicioAccionMoverse AJ.getPacket(): " + Objects.requireNonNull(AJ).getPathPacket() + " e: " + e.toString();
            GestorSalida.INSTANCE.ENVIAR_BN_NADA(this, error);
            AtlantaMain.redactarLogServidorln(error);
        }
        return false;
    }

    public boolean inicioMovimiento(final AccionDeJuego AJ) {
        int linea = 0;
        try {
            if (esFantasma()) {
                linea = 1;
                // puede moverse normal
            } else if (esTumba() || Inmovil) {
                linea = 23;
                if (esTumba()) {
                    linea = 2;
                } else if (Inmovil) {
                    linea = 3;
                    GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "1ESTAS_INMOVIL");
                }
                linea = 5;
                // setOcupado(false);
                GestorSalida.INSTANCE.ENVIAR_GA_DEBUG_ACCIONES(this);
                // borrarAccionJuego(AJ.getIDUnica());
                return false;
            }
            linea = 24;
            if (getPodsUsados() > getPodsMaximos()) {
                linea = 4;
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "112");
                GestorSalida.INSTANCE.ENVIAR_GA_DEBUG_ACCIONES(this);
                // borrarAccionJuego(AJ.getIDUnica());
                return false;
            }
            linea = 6;
            if (TotalStats.getStatsObjetos().tieneStatID(Constantes.STAT_MOVER_DESAPARECE_BUFF)) {
                linea = 7;
                for (byte pos : Constantes.POSICIONES_EQUIPAMIENTO) {
                    try {
                        final Objeto obj = getObjPosicion(pos);
                        if (obj == null) {
                            continue;
                        }
                        if (obj.getStats().tieneStatID(Constantes.STAT_MOVER_DESAPARECE_BUFF)) {
                            borrarOEliminarConOR(obj.getId(), true);
                            break;
                        }
                    } catch (final Exception ignored) {
                    }
                }
            }
            linea = 8;
            final short celdaIDPersonaje = getCelda().getId();
            linea = 9;
            String path = AJ.getPathPacket();
            linea = 10;
            final AtomicReference<String> pathRef = new AtomicReference<>(path);
            linea = 11;
            final short ultCelda = Encriptador.hashACeldaID(path.substring(path.length() - 2));
            linea = 12;
            int nroCeldasMov = Camino.nroCeldasAMover(Mapa, null, pathRef, celdaIDPersonaje, ultCelda, this);
            linea = 13;
            if (nroCeldasMov == 0) {
                GestorSalida.INSTANCE.ENVIAR_GA_DEBUG_ACCIONES(this);
                // borrarAccionJuego(AJ.getIDUnica());
                return false;
            }
            linea = 14;
            if (nroCeldasMov == -1000) {
                linea = 15;
                path = Encriptador.getValorHashPorNumero(getOrientacion()) + Encriptador.celdaIDAHash(celdaIDPersonaje);
            } else {
                linea = 16;
                if (nroCeldasMov >= 10000) {
                    if (nroCeldasMov >= 20000) {
                        nroCeldasMov -= 10000;
                    }
                    nroCeldasMov -= 10000;
                }
                AJ.setCeldas(nroCeldasMov);
                path = pathRef.get();
            }
            linea = 17;
            AJ.setPathReal(path);
            linea = 18;
            if (esMaestro()) {
                linea = 19;
                Grupo.packetSeguirLider(AJ.getPacket());
            }
            linea = 20;
            GestorSalida.INSTANCE.ENVIAR_GA_ACCION_JUEGO_AL_MAPA(Mapa, AJ.getIDUnica(), 1, getId() + "", Encriptador
                    .getValorHashPorNumero(getOrientacion()) + Encriptador.celdaIDAHash(celdaIDPersonaje) + path);
            linea = 21;
            if (estaSentado()) {
                setSentado(false);
            }
            linea = 22;
            // setOcupado(true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            String error = "EXCEPTION inicioMovimiento LINEA " + linea + " AJ.getPacket(): " + AJ.getPathPacket() + " e: " + e
                    .toString();
            GestorSalida.INSTANCE.ENVIAR_BN_NADA(this, error);
            AtlantaMain.redactarLogServidorln(error);
        }
        return false;
    }

    public boolean finAccionMoverse(final AccionDeJuego AJ, String packet) {
        byte bug = 0;
        try {
            final boolean ok = packet.charAt(2) == 'K';
            bug = 1;
            if (AtlantaMain.PARAM_ANTI_SPEEDHACK && ok) {
                boolean correr = AJ.getCeldas() > 5;
                long debeSer = 0;
                if (Montando) {
                    if (correr) {
                        debeSer = 130;
                    } else {
                        debeSer = 280;
                    }
                } else {
                    if (correr) {
                        debeSer = 170;
                    } else {
                        debeSer = 390;
                    }
                }
                long ping = 0;
                try {
                    ping = Cuenta.getSocket().getPing();
                } catch (Exception ignored) {
                }
                float fVelocidad = (Math.max(0, TotalStats.getTotalStatParaMostrar(Constantes.STAT_MAS_VELOCIDAD) / 1000f));
                debeSer = (long) ((debeSer * AJ.getCeldas()) / (1 + fVelocidad));
                long fue = (System.currentTimeMillis() - AJ.getTiempoInicio()) + ping;
                if (debeSer > fue) {
                    AtlantaMain.redactarLogServidorln("PLAYER " + Nombre + "(" + Id + ") USE SPEEDHACK => MAYBE:" + debeSer
                            + " - WAS:" + fue + " - PING:" + ping + " (" + AJ.getCeldas() + ")");
                    // GestorSalida.ENVIAR_Im1223_MENSAJE_IMBORRABLE(this, "1DONT_USE_SPEEDHACK");
                    // GestorSalida.ENVIAR_M0_MENSAJE_BASICOS_SVR(this, "45", "DISCONNECT FOR USE SPEED HACK",
                    // "");
                    // try {
                    // Thread.sleep(3000);
                    // _cuenta.getEntradaPersonaje().cerrarSocket(true, "finAccionMoverse()");
                    // } catch (Exception e) {}
                    // return false;
                    try {
                        Thread.sleep(debeSer - fue + 1);
                    } catch (Exception ignored) {
                    }
                }
            }
            bug = 2;
            if (Pelea == null) {
                return finMovimiento(AJ, packet);
            } else {
                return Pelea.finalizarMovimiento(this);
            }
        } catch (Exception e) {
            String error = "EXCEPTION finAccionMoverse AJ.getPacket(): " + AJ.getPacket() + " , bug: " + bug + " e:" + e
                    .toString();
            GestorSalida.INSTANCE.ENVIAR_BN_NADA(this, error);
            AtlantaMain.redactarLogServidorln(error);
        }
        return false;
    }

    public boolean finMovimiento(final AccionDeJuego AJ, String packet) {
        byte bug = 0;
        try {
            short celdaAMover = -1, celdaPacket = -1;
            final String pathReal = AJ.getPathReal();
            final String pathPacket = AJ.getPathPacket();
            Orientacion = (Encriptador.getNumeroPorValorHash(pathReal.charAt(pathReal.length() - 3)));
            bug = 1;
            final boolean ok = packet.charAt(2) == 'K';
            if (ok) {
                bug = 2;
                celdaAMover = Encriptador.hashACeldaID(pathReal.substring(pathReal.length() - 2));
                celdaPacket = Encriptador.hashACeldaID(pathPacket.substring(pathPacket.length() - 2));
            } else {
                bug = 3;
                String[] infos = packet.substring(3).split(Pattern.quote("|"));
                celdaPacket = celdaAMover = Short.parseShort(infos[1]);
                if (Grupo != null && Grupo.esLiderGrupo(this)) {
                    Grupo.packetSeguirLider(packet);
                }
            }
            bug = 4;
            return Mapa.jugadorLLegaACelda(this, celdaAMover, celdaPacket, ok);
        } catch (Exception e) {
            String error = "EXCEPTION finMovimiento AJ.getPacket(): " + AJ.getPacket() + " , bug: " + bug + " e:" + e
                    .toString();
            GestorSalida.INSTANCE.ENVIAR_BN_NADA(this, error);
            AtlantaMain.redactarLogServidorln(error);
        }
        return false;
    }

    public void realizarOtroInteractivo(Celda celdaObjetivo, ObjetoInteractivo objInteractivo) {
        boolean b = false;
        if (objInteractivo == null || !objInteractivo.puedeIniciarRecolecta()) {
            return;
        }
        for (final OtroInteractivo oi : Mundo.OTROS_INTERACTIVOS) {
            if (oi.getGfxID() <= -1 && oi.getMapaID() <= -1 && oi.getCeldaID() <= -1) {
                continue;
            }
            if (oi.getGfxID() > -1 && oi.getGfxID() != objInteractivo.getGfxID()) {
                continue;
            }
            if (oi.getMapaID() > -1 && oi.getMapaID() != Mapa.getId()) {
                continue;
            }
            if (oi.getCeldaID() > -1 && oi.getCeldaID() != celdaObjetivo.getId()) {
                continue;
            }
            if (!Condiciones.INSTANCE.validaCondiciones(this, oi.getCondicion())) {
                continue;
            }
            // System.out.println("ENVIO LA ACCION 1");
            objInteractivo.forzarActivarRecarga(oi.getTiempoRecarga());
            oi.getAccion().realizarAccion(this, null, -1, (short) -1);
            return;
        }
    }

    public boolean puedeIrKoliseo() {
        return Tutorial == null && !Calabozo && Pelea == null && EnLinea;
    }

    public int getPretendiente() {
        return Pretendiente;
    }

    public boolean puedeCasarse() {
        if (Mapa.getId() != 2019) {
            return false;
        }
        if (Celda.getId() != 297 && Celda.getId() != 282) {
            return false;
        }
        if (!AtlantaMain.PARAM_MATRIMONIO_GAY) {
            if (Celda.getId() == 282 && Sexo == Constantes.SEXO_FEMENINO) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "1102");
                return false;
            }
            if (Celda.getId() == 297 && Sexo == Constantes.SEXO_MASCULINO) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "1102");
                return false;
            }
        }
        return EsposoID <= 0;
    }

    public void preguntaCasarse() {
        if (Mapa.getId() != 2019) {
            return;
        }
        short celda = 0;
        byte sexo = 0;
        if (Celda.getId() == 282) {
            celda = 297;
            sexo = Constantes.SEXO_FEMENINO;
        } else if (Celda.getId() == 297) {
            celda = 282;
        } else {
            return;
        }
        Personaje novio = Mapa.getCelda(celda).getPrimerPersonaje();
        if (novio == null || novio.getEsposoID() > 0) {
            return;
        }
        if (!AtlantaMain.PARAM_MATRIMONIO_GAY && novio.getSexo() != sexo) {
            return;
        }
        GestorSalida.INSTANCE.ENVIAR_GA_ACCIONES_MATRIMONIO(Mapa, 617, Id, novio.getId(), -51);
    }

    public void confirmarMatrimonio(int proponeID, boolean acepto) {
        Personaje propone = Mundo.getPersonaje(proponeID);
        if (propone == null)
            return;
        if (!acepto) {
            Pretendiente = propone.Pretendiente = 0;
            GestorSalida.INSTANCE.ENVIAR_GA_ACCIONES_MATRIMONIO(Mapa, 619, Id, proponeID, -51);
        } else {
            if (propone.getPretendiente() == Id) {
                Pretendiente = propone.Pretendiente = 0;
                EsposoID = propone.getId();
                propone.EsposoID = Id;
                GestorSalida.INSTANCE.ENVIAR_GA_ACCIONES_MATRIMONIO(Mapa, 618, Id, proponeID, -51);
            } else {
                Pretendiente = proponeID;
                GestorSalida.INSTANCE.ENVIAR_GA_ACCIONES_MATRIMONIO(Mapa, 617, Id, proponeID, -51);
            }
        }
    }

    public void divorciar() {
        try {
            if (EnLinea) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "047;" + Mundo.getPersonaje(EsposoID).getNombre());
            }
        } catch (final Exception ignored) {
        }
        EsposoID = 0;
    }

    public int getEsposoID() {
        return EsposoID;
    }

    public void setEsposoID(final int id) {
        EsposoID = id;
    }

    public byte getTipoExchange() {
        return TipoExchange;
    }

    public void setTipoExchange(final byte tipo) {
        TipoExchange = tipo;
    }

    public boolean estaExchange() {
        return TipoExchange != Constantes.INTERCAMBIO_TIPO_NULO;
    }

    public void cerrarVentanaExchange(String exito) {
        setExchanger(null);
        setTipoExchange(Constantes.INTERCAMBIO_TIPO_NULO);
        GestorSalida.INSTANCE.ENVIAR_EV_CERRAR_VENTANAS(this, exito);
    }

    public synchronized void cerrarExchange(String exito) {
        if ("intercambio".equals(getTipoInvitacion())) {
            Personaje invitandoA, invitador;
            if (Invitador != null) {
                invitador = Invitador;
                invitandoA = this;
            } else if (InvitandoA != null) {
                invitador = this;
                invitandoA = InvitandoA;
            } else {
                GestorSalida.INSTANCE.ENVIAR_BN_NADA(this);
                return;
            }
            invitador.setInvitandoA(null, "");
            invitandoA.setInvitador(null, "");
            invitador.cerrarVentanaExchange("");
            invitandoA.cerrarVentanaExchange("");
        } else {
            if (!estaExchange()) {
                GestorSalida.INSTANCE.ENVIAR_BN_NADA(this);
                return;
            }
            switch (TipoExchange) {
                case Constantes.INTERCAMBIO_TIPO_LIBRO_ARTESANOS:// libro de artesanos
                    cerrarVentanaExchange("");
                    break;
                case Constantes.INTERCAMBIO_TIPO_TIENDA_NPC:// tienda npc
                case Constantes.INTERCAMBIO_TIPO_MERCANTE:// mercante
                case Constantes.INTERCAMBIO_TIPO_MI_TIENDA:// misma tienda
                case Constantes.INTERCAMBIO_TIPO_MONTURA:// dragopavo
                case Constantes.INTERCAMBIO_TIPO_BOUTIQUE: // boutique
                case Constantes.INTERCAMBIO_TIPO_PERSONAJE:// intercambio
                case Constantes.INTERCAMBIO_TIPO_TALLER: // accion oficio
                case Constantes.INTERCAMBIO_TIPO_COFRE:// cofre o banco
                case Constantes.INTERCAMBIO_TIPO_RECAUDADOR: // recaudador
                case Constantes.INTERCAMBIO_TIPO_MERCADILLO_COMPRAR:
                case Constantes.INTERCAMBIO_TIPO_MERCADILLO_VENDER: // mercadillo
                case Constantes.INTERCAMBIO_TIPO_TALLER_CLIENTE:
                case Constantes.INTERCAMBIO_TIPO_TALLER_ARTESANO:// invitar taller
                case Constantes.INTERCAMBIO_TIPO_CERCADO:// cercado
                case Constantes.INTERCAMBIO_TIPO_TRUEQUE:
                case Constantes.INTERCAMBIO_TIPO_RESUCITAR_MASCOTA:// mascota
                case 9:// no se q es
                    Exchanger.cerrar(this, exito);
                    break;
            }
        }
    }

    public boolean esAbonado() {
        return (Cuenta != null && Cuenta.esAbonado());
    }

    public void teleport(final short nuevoMapaID, final short nuevaCeldaID) {
        if (esMultiman()) {
            return;
        }
        try {
            if (Tutorial != null || Inmovil || Calabozo) {
                if (Calabozo) {
                    GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "1YOU_ARE_IN_JAIL");
                }
                if (Tutorial != null) {
                    GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "1YOU_ARE_DOING_TUTORIAL");
                }
                if (Inmovil) {
                    GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "1DONT_MOVE_TEMP");
                }
                return;
            }
            if (estaExchange()) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "1NO_PUEDES_TELEPORT_POR_EXCHANGE");
                return;
            }
            if (!Huir) {
                if (System.currentTimeMillis() - TiempoAgresion > 8000) {
                    Huir = true;
                } else {
                    GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "1NO_PUEDES_HUIR;" + (System.currentTimeMillis() - TiempoAgresion)
                            / 1000);
                    return;
                }
            }
            Mapa nuevoMapa = Mundo.getMapa(nuevoMapaID);
            if (nuevoMapa == null) {
                nuevoMapa = Mundo.getMapa((short) 7411);
            }
            if (nuevoMapa.getCelda(nuevaCeldaID) == null) {
                return;
            }
            if (nuevoMapa.mapaAbonado() && !esAbonado()) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "131");
                return;
            }
            CasaDentro = Mundo.getCasaDentroPorMapa(nuevoMapaID);
            GestorSalida.INSTANCE.ENVIAR_GM_BORRAR_GM_A_MAPA(Mapa, Id);
            Mapa = nuevoMapa;
            setCelda(Mapa.getCelda(nuevaCeldaID));
            if (Pregunta > 0) {
                dialogoFin();
            }
            GestorSalida.INSTANCE.ENVIAR_GA2_CARGANDO_MAPA(this);
            GestorSalida.INSTANCE.ENVIAR_GDM_CAMBIO_DE_MAPA(this, Mapa);
            GestorSalida.INSTANCE.ENVIAR_GM_PJ_A_MAPA(Mapa, this);
            rastrearGrupo();
            int[] tt = {MisionObjetivoModelo.DESCUBRIR_MAPA, MisionObjetivoModelo.DESCUBRIR_ZONA};
            verificarMisionesTipo(tt, null, false, 0);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void rastrearGrupo() {
        if (Grupo != null && Grupo.getRastrear() != null && Grupo.getRastrear().getId() == Id) {
            for (final Personaje elQueSigue : Grupo.getMiembros()) {
                try {
                    if (elQueSigue.getId() == Grupo.getRastrear().getId()) {
                        continue;
                    }
                    if (elQueSigue.EnLinea) {
                        GestorSalida.INSTANCE.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(elQueSigue, Mapa.getX() + "|" + Mapa.getY());
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void teleportSinTodos(final short nuevoMapaID, final short nuevaCeldaID) {
        Mapa nuevoMapa = Mundo.getMapa(nuevoMapaID);
        if (nuevoMapa == null || esMultiman()) {
            return;
        }
        // if (_mapa.getID() == nuevoMapaID) {
        // return false;
        // }
        GestorSalida.INSTANCE.ENVIAR_GM_BORRAR_GM_A_MAPA(Mapa, Id);
        GestorSalida.INSTANCE.ENVIAR_GA2_CARGANDO_MAPA(this);
        GestorSalida.INSTANCE.ENVIAR_GDM_CAMBIO_DE_MAPA(this, nuevoMapa);
    }

    public boolean getHuir() {
        return !Huir;
    }

    public void setHuir(final boolean huir) {
        Huir = huir;
    }

    public long getTiempoAgre() {
        return TiempoAgresion;
    }

    public void setTiempoAgre(final long tiempo) {
        TiempoAgresion = tiempo;
    }

    public boolean getAgresion() {
        return Agresion;
    }

    public void setAgresion(final boolean agre) {
        Agresion = agre;
    }

    public int getCostoAbrirBanco() {
        return Cuenta.getObjetosBanco().size();
    }

    public String getStringVar(final String str) {
        if (str.equalsIgnoreCase("nombre")) {
            return Nombre;
        }
        if (str.equalsIgnoreCase("costoBanco")) {
            return getCostoAbrirBanco() + "";
        }
        return "";
    }

    public void addKamasBanco(final long i) {
        Cuenta.addKamasBanco(i);
    }

    public long getKamasBanco() {
        return Cuenta.getKamasBanco();
    }

    public void addPuntosStats(final int pts) {
        PuntosStats += pts;
    }

    public void addPuntosHechizos(final int puntos) {
        PuntosHechizos += puntos;
    }

    public void abrirCercado() {
        if (Deshonor >= 5) {
            GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "183");
            return;
        }
        Cercado cercado = Mapa.getCercado();
        if (cercado == null) {
            return;
        }
        Exchanger = cercado;
        TipoExchange = Constantes.INTERCAMBIO_TIPO_CERCADO;
        GestorSalida.INSTANCE.ENVIAR_ECK_PANEL_DE_INTERCAMBIOS(this, TipoExchange, analizarListaMonturas(cercado));
    }

    public String analizarListaMonturas(Cercado cercado) {
        final StringBuilder str = new StringBuilder();
        boolean primero = false;
        for (final Montura montura : Cuenta.getEstablo().values()) {
            if (primero) {
                str.append(";");
            }
            str.append(montura.detallesMontura());
            primero = true;
        }
        str.append("~");
        primero = false;
        for (final Montura montura : cercado.getCriando().values()) {
            if (montura.getDueñoID() == Id) {
                if (primero) {
                    str.append(";");
                }
                str.append(montura.detallesMontura());
            } else {
                if (cercado.esPublico() || MiembroGremio == null || MiembroGremio.puede(Constantes.G_OTRAS_MONTURAS)) {
                    continue;
                }
                if (primero) {
                    str.append(";");
                }
                str.append(montura.detallesMontura());
            }
            primero = true;
        }
        return str.toString();
    }

    public void refrescarStuff(boolean actualizar, boolean enviarAs, boolean visual) {
        // solo refresca los items equipados q no deben ser equipados
        if (actualizar) {
            actualizarObjEquipStats();
        }
        Encarnacion encarnacionTemp = Encarnacion;
        float velocidadTemp = Math.max(0, TotalStats.getStatsObjetos().getStatParaMostrar(Constantes.STAT_MAS_VELOCIDAD)
                / 1000f);
        int aparienciaTemp = TotalStats.getStatsObjetos().getStatParaMostrar(Constantes.STAT_CAMBIA_APARIENCIA_2);
        int tituloTemp = getTitulo(false);
        Encarnacion encarnacion = null;
        do {
            actualizar = false;
            for (byte i : Constantes.POSICIONES_EQUIPAMIENTO) {
                final Objeto objeto = getObjPosicion(i);
                if (objeto == null) {
                    continue;
                }
                if (objeto.getEncarnacion() != null) {
                    encarnacion = objeto.getEncarnacion();
                }
                ObjetoModelo objMoverMod = objeto.getObjModelo();
                boolean desequipar = false;
                if (objMoverMod.getNivel() > Nivel) {
                    desequipar = true;
                } else if (!Condiciones.INSTANCE.validaCondiciones(this, objMoverMod.getCondiciones())) {
                    desequipar = true;
                } else if (puedeEquiparRepetido(objMoverMod, 2)) {
                    desequipar = true;
                }
                if (desequipar) {
                    // si el item no debe ser equipado, se le pone posicio no equipado
                    actualizar = true;
                    enviarAs = true;
                    if (Constantes.esPosicionVisual(i)) {
                        visual = true;
                    }
                    objeto.setPosicion(Constantes.OBJETO_POS_NO_EQUIPADO, this, false);
                }
            }
        } while (actualizar);
        float velocidad = Math.max(0, TotalStats.getStatsObjetos().getStatParaMostrar(Constantes.STAT_MAS_VELOCIDAD)
                / 1000f);
        int apariencia = TotalStats.getStatsObjetos().getStatParaMostrar(Constantes.STAT_CAMBIA_APARIENCIA_2);
        int titulo = getTitulo(false);
        int[][] rB = {{RB_PUEDE_SWITCH_MODO_CRIATURA, apariencia != 0 ? 0 : 1}};
        setRestriccionesB(rB);
        boolean cambioEncarnacion = false;
        if (encarnacion != encarnacionTemp) {
            Encarnacion = encarnacion;
            if (Encarnacion != null) {
                TiempoUltEncarnacion = System.currentTimeMillis();
            }
            if (EnLinea) {
                GestorSalida.INSTANCE.ENVIAR_AC_CAMBIAR_CLASE(this, getClaseID(false));
                GestorSalida.INSTANCE.ENVIAR_SL_LISTA_HECHIZOS(this);
                cambioEncarnacion = true;
            }
        }
        if (Encarnacion == null) {
            if (EnLinea) {
                refrescarParteSetClase();
            }
        }
        actualizarPDV(0);
        if (enviarAs && EnLinea) {
            GestorSalida.INSTANCE.ENVIAR_As_STATS_DEL_PJ(this);
            GestorSalida.INSTANCE.ENVIAR_Ow_PODS_DEL_PJ(this);
        }
        if (cambioEncarnacion || velocidadTemp != velocidad || aparienciaTemp != apariencia || tituloTemp != titulo) {
            if (EnLinea) {
                refrescarEnMapa();
            }
        }
        if (visual) {
            cambiarRopaVisual();
        }
    }

    public boolean puedeEquiparRepetido(ObjetoModelo objMoverMod, int cant) {
        return (objMoverMod.getSetID() > 0 || objMoverMod.getTipo() == Constantes.OBJETO_TIPO_DOFUS || objMoverMod
                .getTipo() == Constantes.OBJETO_TIPO_TROFEO) && Mundo.getCreaTuItem(objMoverMod.getId()) == null
                && cantEquipadoModelo(objMoverMod.getId()) >= cant;
    }

    public void refrescarEnMapa() {
        if (Pelea == null) {
            GestorSalida.INSTANCE.ENVIAR_GM_REFRESCAR_PJ_EN_MAPA(Mapa, this);
        } else if (Pelea.getFase() == Constantes.PELEA_FASE_POSICION) {
            final Luchador luchador = Pelea.getLuchadorPorID(Id);
            if (luchador != null) {
                GestorSalida.INSTANCE.ENVIAR_GM_REFRESCAR_LUCHADOR_EN_PELEA(Pelea, luchador);
            }
        }
    }

    public void cambiarRopaVisual() {
        if (Pelea != null) {
            GestorSalida.INSTANCE.ENVIAR_Oa_CAMBIAR_ROPA_PELEA(Pelea, this);
        } else {
            GestorSalida.INSTANCE.ENVIAR_Oa_CAMBIAR_ROPA_MAPA(Mapa, this);
        }
        if (Grupo != null) {
            GestorSalida.INSTANCE.ENVIAR_PM_ACTUALIZAR_INFO_PJ_GRUPO(Grupo, stringInfoGrupo());
        }
    }

    public String analizarListaAmigos(final int id) {
        final StringBuilder str = new StringBuilder(";");
        str.append("?;");
        str.append(Nombre).append(";");
        if (Cuenta.esAmigo(id)) {
            str.append(Nivel).append(";");
            str.append(Alineacion).append(";");
        } else {
            str.append("?;");
            str.append("-1;");
        }
        str.append(ClaseID).append(";");
        str.append(Sexo).append(";");
        str.append(getGfxID(false));
        return str.toString();
    }

    public String analizarListaEnemigos(final int id) {
        final StringBuilder str = new StringBuilder(";");
        str.append("?;");
        str.append(Nombre).append(";");
        if (Cuenta.esEnemigo(id)) {
            str.append(Nivel).append(";");
            str.append(Alineacion).append(";");
        } else {
            str.append("?;");
            str.append("-1;");
        }
        str.append(ClaseID).append(";");
        str.append(Sexo).append(";");
        str.append(getGfxID(false));
        return str.toString();
    }

    public boolean estaMontando() {
        return Montando;
    }

    public synchronized void subirBajarMontura(boolean obligatorio) {
        if (Montura == null) {
            if (EnLinea) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "1MOUNT_NO_EQUIP");
            }
            return;
        }
        if (!obligatorio) {
            if (Encarnacion != null) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "134|44");
                return;
            }
            if (Pelea != null && (Pelea.getFase() != Constantes.PELEA_FASE_POSICION || Pelea.esEspectador(Id))) {
                return;
            }
            if (!Montando) {// va a montar
                if (Nivel < 60 || esFantasma() || esTumba()) {
                    GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "1MOUNT_ERROR_RIDE");
                    return;
                }
                if (AtlantaMain.PARAM_CRIAR_MONTURA) {
                    if (Montura.getEnergia() < 10) {
                        GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "1113");
                        return;
                    }
                    if (!Montura.esMontable()) {
                        GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "1176");
                        return;
                    }
                }
                if (CasaDentro != null) {
                    GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "1117");
                    return;
                }
                if (Montura.getDueñoID() != Id) {
                    GestorSalida.INSTANCE.ENVIAR_BN_NADA(this, "SUBIR BAJAR MONTURA NO DUEÑO " + Montura.getDueñoID());
                    return;
                }
            }
            Montura.energiaPerdida(2);
        }
        Montando = !Montando;
        final Objeto mascota = getObjPosicion(Constantes.OBJETO_POS_MASCOTA);
        if (Montando && mascota != null) {
            mascota.setPosicion(Constantes.OBJETO_POS_NO_EQUIPADO, this, false);
        }
        refrescarStuff(true, !obligatorio, false);
        if (!obligatorio) {
            if (EnLinea) {
                refrescarEnMapa();
                GestorSalida.INSTANCE.ENVIAR_Re_DETALLES_MONTURA(this, "+", Montura);
                GestorSalida.INSTANCE.ENVIAR_Rr_ESTADO_MONTADO(this, Montando ? "+" : "-");
            }
        }
    }

    public Montura getMontura() {
        return Montura;
    }

    public void setMontura(final Montura montura) {
        Montura = montura;
        if (Montura != null) {
            Montura.setUbicacion(Ubicacion.EQUIPADA);
        }
        if (EnLinea) {
            GestorSalida.INSTANCE.ENVIAR_Re_DETALLES_MONTURA(this, Montura != null ? "+" : "-", Montura);
        }
    }

    public int getPorcXPMontura() {
        return PorcXPMontura;
    }

    public void setPorcXPMontura(final int porcXP) {
        PorcXPMontura = porcXP;
        PorcXPMontura = Math.max(PorcXPMontura, 0);
        PorcXPMontura = Math.min(PorcXPMontura, 90);
    }

    public void resetearVariables() {
        PrePelea = null;// es para perco y prismas
        TipoExchange = Constantes.INTERCAMBIO_TIPO_NULO;
        if (InvitandoA != null) {
            InvitandoA.setInvitador(null, "");
            InvitandoA = null;
        }
        if (Invitador != null) {
            Invitador.setInvitandoA(null, "");
            Invitador = null;
        }
        TipoInvitacion = "";
        ConversandoCon = 0;
        Pretendiente = 0;
        Pregunta = 0;
        EmoteActivado = 0;
        Exchanger = null;
        Tutorial = null;
        ConsultarCasa = null;
        ConsultarCofre = null;
        Ocupado = false;
        Sentado = false;
        Ausente = false;// para q no recibas MP de todos
        Invisible = false;// para q solo recibias MP de tus amigos
        CargandoMapa = false;
        OlvidandoHechizo = false;
        DeDia = false;
        DeNoche = false;
        IdsOmitidos.clear();
        BonusSetDeClase.clear();
        if (estaMontando()) {
            subirBajarMontura(true);
        }
        addCanal("~");
        limpiarPacketsCola();
    }

    public void setPrePelea(Pelea pelea, int idUnirse) {
        PrePelea = pelea;
        UnirsePrePeleaAlID = idUnirse;
    }

    public Pelea getPrePelea() {
        return PrePelea;
    }

    public int getUnirsePrePeleaAlID() {
        return UnirsePrePeleaAlID;
    }

    public void addOmitido(String name) {
        Personaje p = Mundo.getPersonajePorNombre(name);
        if (p == null) {
            return;
        }
        if (!IdsOmitidos.contains(p.getId())) {
            IdsOmitidos.add(p.getId());
        }
    }

    public void borrarOmitido(String name) {
        Personaje p = Mundo.getPersonajePorNombre(name);
        if (p == null) {
            return;
        }
        if (IdsOmitidos.contains(p.getId())) {
            IdsOmitidos.remove((Object) p.getId());
        }
    }

    public String getCanales() {
        return Canales;
    }

    public boolean tieneCanal(String c) {
        switch (c) {
            case "p":// espectador
            case "F":// envia
            case "T":// recibe
            case "@":// admin
            case "¡":// vip
            case "¬":// unknown
            case "~":// all
                return false;
        }
        return !Canales.contains(c);
    }

    public void addCanal(final String canal) {
        if (EnLinea) {
            GestorSalida.INSTANCE.ENVIAR_cC_SUSCRIBIR_CANAL(this, '+', canal);
        }
        if (Canales.contains(canal)) {
            return;
        }
        Canales += canal;
    }

    public void removerCanal(final String canal) {
        Canales = Canales.replace(canal, "");
        if (EnLinea) {
            GestorSalida.INSTANCE.ENVIAR_cC_SUSCRIBIR_CANAL(this, '-', canal);
        }
    }

    public boolean cambiarAlineacion(final byte alineacion, boolean siOsi) {
        if (!siOsi) {
            if (getDeshonor() >= 2) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "183");
                return false;
            }
        }
        Honor = 0;
        Deshonor = 0;
        Alineacion = alineacion;
        MostrarAlas = alineacion != Constantes.ALINEACION_NEUTRAL;
        if (Alineacion != Constantes.ALINEACION_NEUTRAL) {
            if (AtlantaMain.HONOR_FIJO_PARA_TODOS > -1) {
                Honor = AtlantaMain.HONOR_FIJO_PARA_TODOS;
            }
        }
        OrdenNivel = 0;
        switch (Alineacion) {
            case Constantes.ALINEACION_BONTARIANO:
                Orden = 1;
                break;
            case Constantes.ALINEACION_BRAKMARIANO:
                Orden = 5;
                break;
            case Constantes.ALINEACION_MERCENARIO:
                Orden = 9;
                break;
            default:
                Orden = 0;
                break;
        }
        refrescarGradoAlineacion();
        Especialidad esp = Mundo.getEspecialidad(Orden, OrdenNivel);
        if (esp != null) {
            GestorSalida.INSTANCE.ENVIAR_ZC_CAMBIAR_ESPECIALIDAD_ALINEACION(this, esp.getID());
        }
        actualizarStatsEspecialidad(esp);
        refrescarStuff(true, true, false);
        refrescarEnMapa();
        return true;
    }

    public void addOrdenNivel(int nivel) {
        Especialidad esp = Mundo.getEspecialidad(Orden, OrdenNivel);
        OrdenNivel += nivel;
        if (OrdenNivel > 100) {
            OrdenNivel = 100;
        }
        if (esp != null && esp.getID() != (esp = Mundo.getEspecialidad(Orden, OrdenNivel)).getID()) {
            GestorSalida.INSTANCE.ENVIAR_ZC_CAMBIAR_ESPECIALIDAD_ALINEACION(this, esp.getID());
        }
        actualizarStatsEspecialidad(esp);
        GestorSalida.INSTANCE.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
    }

    public void actualizarStatsEspecialidad(Especialidad esp) {
        TotalStats.getStatsBendMald().clear();
        if (esp == null) {
            return;
        }
        for (Don don : esp.getDones()) {
            TotalStats.getStatsBendMald().acumularStats(don.getStat());
        }
    }

    public int getOrden() {
        return Orden;
    }

    public void setOrden(int orden) {
        Orden = orden;
        Especialidad esp = Mundo.getEspecialidad(Orden, OrdenNivel);
        if (esp != null) {
            GestorSalida.INSTANCE.ENVIAR_ZC_CAMBIAR_ESPECIALIDAD_ALINEACION(this, esp.getID());
        }
        actualizarStatsEspecialidad(esp);
        GestorSalida.INSTANCE.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
    }

    public int getOrdenNivel() {
        return OrdenNivel;
    }

    public int getEspecialidad() {
        Especialidad esp = Mundo.getEspecialidad(Orden, OrdenNivel);
        if (esp != null) {
            return esp.getID();
        }
        return 0;
    }

    public int getDeshonor() {
        return Deshonor;
    }

    public boolean addDeshonor(int deshonor) {
        if (Alineacion == Constantes.ALINEACION_NEUTRAL || !AtlantaMain.PARAM_PERMITIR_DESHONOR) {
            return false;
        }
        Deshonor += deshonor;
        if (Deshonor < 0) {
            Deshonor = 0;
        }
        return true;
    }

    public int getHonor() {
        return Honor;
    }

    public void addHonor(final int honor) {
        if (esMultiman()) {
            return;
        }
        if (honor == 0 || Alineacion == Constantes.ALINEACION_NEUTRAL) {
            return;
        }
        if (honor > 0) {
            GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "074;" + honor);
        } else {
            GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "076;" + -honor);
        }
        Honor += honor;
        refrescarGradoAlineacion();
    }

    public void refrescarGradoAlineacion() {
        final int nivelAntes = GradoAlineacion;
        if (Honor < 0) {
            Honor = 0;
        } else if (Honor >= Mundo.getExpAlineacion(AtlantaMain.NIVEL_MAX_ALINEACION)) {
            GradoAlineacion = AtlantaMain.NIVEL_MAX_ALINEACION;
            Honor = Mundo.getExpAlineacion(AtlantaMain.NIVEL_MAX_ALINEACION);
        }
        for (byte n = 1; n <= AtlantaMain.NIVEL_MAX_ALINEACION; n++) {
            if (Honor < Mundo.getExpAlineacion(n)) {
                GradoAlineacion = (byte) (n - 1);
                break;
            }
        }
        if (nivelAntes == GradoAlineacion) {
            return;
        }
        if (nivelAntes < GradoAlineacion) {
            GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "082;" + GradoAlineacion);
        } else {
            GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "083;" + GradoAlineacion);
        }
        refrescarStuff(true, true, false);
    }

    public int getGradoAlineacion() {
        if (Alineacion == Constantes.ALINEACION_NEUTRAL) {
            return 1;
        }
        return GradoAlineacion;
    }

    public void botonActDesacAlas(final char c) {
        if (Alineacion == Constantes.ALINEACION_NEUTRAL) {
            MostrarAlas = false;
            return;
        }
        if (!AtlantaMain.PARAM_PERMITIR_DESACTIVAR_ALAS) {
            MostrarAlas = true;
            return;
        }
        final int honorPerd = Honor / 20;
        switch (c) {
            case '*':
                GestorSalida.INSTANCE.ENVIAR_GIP_ACT_DES_ALAS_PERDER_HONOR(this, honorPerd);
                return;
            case '+':
                MostrarAlas = true;
                GestorSalida.INSTANCE.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
                break;
            case '-':
                MostrarAlas = false;
                addHonor(-honorPerd);
                GestorSalida.INSTANCE.ENVIAR_Ak_KAMAS_PDV_EXP_PJ(this);
                break;
        }
    }

    public MiembroGremio getMiembroGremio() {
        return MiembroGremio;
    }

    public void setMiembroGremio(final MiembroGremio gremio) {
        MiembroGremio = gremio;
    }

    public int getCuentaID() {
        if (Cuenta == null) {
            return -1;
        }
        return Cuenta.getId();
    }

    public boolean cumplirMisionAlmanax() {
        if (!AtlantaMain.PARAM_ALMANAX) {
            GestorSalida.INSTANCE.ENVIAR_BN_NADA(this, "ALMANAX NO DISPONIBLE");
            return false;
        }
        // buscar ontoral Zo
        if (realizoMisionDelDia()) {
            GestorSalida.INSTANCE.ENVIAR_BN_NADA(this, "YA REALIZO ALMANAX DEL DIA");
            return false;
        }
        Almanax almanax = Mundo.getAlmanaxDelDia();
        if (almanax == null) {
            GestorSalida.INSTANCE.ENVIAR_BN_NADA(this, "NO EXISTE ALMANAX DEL DIA");
            return false;
        }
        int id = almanax.getOfrenda().get_primero();
        int cant = almanax.getOfrenda().get_segundo();
        if (tenerYEliminarObjPorModYCant(id, cant)) {
            GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "022;" + cant + "~" + id);
            GestorSalida.INSTANCE.ENVIAR_Ow_PODS_DEL_PJ(this);
            agregarMisionDelDia();
        } else {
            GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "14");
        }
        return true;
    }

    public String listaPrismas() {
        final StringBuilder str = new StringBuilder(Mapa.getId());
        final int subAreaID = Mapa.getSubArea().getArea().getSuperArea().getId();
        for (final Prisma prisma : Mundo.getPrismas()) {
            try {
                if (prisma.getAlineacion() != Alineacion) {
                    continue;
                }
                if (prisma.getMapa().getSubArea().getArea().getSuperArea().getId() != subAreaID) {
                    continue;
                }
                if (prisma.getEstadoPelea() == 0 || prisma.getEstadoPelea() == -2) {
                    str.append("|").append(prisma.getMapa().getId()).append(";*");
                } else {
                    int costo = Formulas.INSTANCE.calcularCosteZaap(Mapa, prisma.getMapa());
                    str.append("|").append(prisma.getMapa().getId()).append(";").append(costo);
                }
            } catch (Exception ignored) {
            }
        }
        return str.toString();
    }

    public String listaZaap() {
        final StringBuilder str = new StringBuilder();
        if (Zaaps.contains(MapaSalvada)) {
            str.append(MapaSalvada);
        }
        final int superAreaID = Mapa.getSubArea().getArea().getSuperArea().getId();
        for (final short i : Zaaps) {
            try {
                if (Mundo.getMapa(i).getSubArea().getArea().getSuperArea().getId() != superAreaID) {
                    continue;
                }
                int costo = Formulas.INSTANCE.calcularCosteZaap(Mapa, Mundo.getMapa(i));
                str.append("|").append(i).append(";").append(costo);
            } catch (final Exception ignored) {
            }
        }
        return str.toString();
    }

    public boolean tieneZaap(final short mapaID) {
        return !Zaaps.contains(mapaID);
    }

    public void abrirMenuZaap() {
        if (Deshonor >= 3) {
            GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "183");
            return;
        }
        if (tieneZaap(Mapa.getId())) {
            Zaaps.add(Mapa.getId());
            Zaaps.trimToSize();
            GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "024");
        }
        GestorSalida.INSTANCE.ENVIAR_WC_MENU_ZAAP(this);
    }

    public void abrirMenuZaapiZonas() {
        String[] zonas = Mundo.LISTA_ZONAS.split(Pattern.quote("|"));
        final StringBuilder ListaZonas = new StringBuilder();
        int precio = 20;
        if (Alineacion == Constantes.ALINEACION_BONTARIANO) {
            precio = 10;
        }
        for (final String s : zonas) {
            if (s.length() > 0) {
                ListaZonas.append(s.split(Pattern.quote(";"))[1]).append(";").append(precio).append("|");
            }
        }
        GestorSalida.INSTANCE.ENVIAR_Wc_LISTA_ZAPPIS(this, Mapa.getId() + "|" + ListaZonas.toString());
    }

    public void abrirMenuZaapi() {
        if (Deshonor >= 3) {
            GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "183");
            return;
        }
        final StringBuilder listaZaapi = new StringBuilder();
        if (Mapa.getSubArea().getArea().getId() != 7 || Alineacion == Constantes.ALINEACION_BRAKMARIANO) {
            // nada
        } else {
            final String[] Zaapis = "6159,4174,8758,4299,4180,8759,4183,2221,4300,4217,4098,8757,4223,8760,2214,4179,4229,4232,8478,4238,4263,4216,4172,4247,4272,4271,4250,4178,4106,4181,4259,4090,4262,4287,4300,4240,4218,4074,4308"
                    .split(",");
            int precio = 20;
            if (Alineacion == Constantes.ALINEACION_BONTARIANO) {
                precio = 10;
            }
            for (final String s : Zaapis) {
                listaZaapi.append(s).append(";").append(precio).append("|");
            }
        }
        if (Mapa.getSubArea().getArea().getId() != 11 || Alineacion == Constantes.ALINEACION_BONTARIANO) {
            // nada
        } else {
            final String[] Zaapis = "8756,8755,8493,5304,5311,5277,5317,4612,4618,5112,4639,4637,5116,5332,4579,4588,4549,4562,5334,5295,4646,4629,4601,4551,4607,4930,4622,4620,4615,4595,4627,4623,4604,8754,8753,4630"
                    .split(",");
            int precio = 20;
            if (Alineacion == Constantes.ALINEACION_BRAKMARIANO) {
                precio = 10;
            }
            for (final String s : Zaapis) {
                listaZaapi.append(s).append(";").append(precio).append("|");
            }
        }
        GestorSalida.INSTANCE.ENVIAR_Wc_LISTA_ZAPPIS(this, Mapa.getId() + "|" + listaZaapi.toString());
    }

    public void enviarmensajeRojo(String text) {
        if (this.enLinea()) {
            GestorSalida.INSTANCE.ENVIAR_cs_CHAT_MENSAJE(this, text, Constantes.COLOR_ROJO);
        }
    }

    public void abrirMenuPrisma() {
        if (Deshonor >= 3) {
            GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "183");
            return;
        }
        GestorSalida.INSTANCE.ENVIAR_Wp_MENU_PRISMA(this);
    }

    public String stringZaapsParaBD() {
        final StringBuilder str = new StringBuilder();
        for (final short i : Zaaps) {
            if (str.length() > 0) {
                str.append(",");
            }
            str.append(i);
        }
        return str.toString();
    }

    public void usarZaap(final short mapaID) {
        try {
            final Mapa mapa = Mundo.getMapa(mapaID);
            if (mapa == null || mapaID == Mapa.getId() || tieneZaap(Mapa.getId()) || tieneZaap(mapaID)) {
                GestorSalida.INSTANCE.ENVIAR_BN_NADA(this);
                return;
            }
            final short celdaID = Mundo.getCeldaZaapPorMapaID(mapaID);
            if (mapa.getCelda(celdaID) == null || mapa.getSubArea().getArea().getSuperArea().getId() != Mapa.getSubArea()
                    .getArea().getSuperArea().getId()) {
                GestorSalida.INSTANCE.ENVIAR_WUE_ZAPPI_ERROR(this);
                return;
            }
            if (Alineacion == Constantes.ALINEACION_BRAKMARIANO) {
                if (mapaID == 4263 || Mapa.getSubArea().getAlineacion() == Constantes.ALINEACION_BONTARIANO) {
                    GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "1TEAM_DIFFERENT_ALIGNMENT");
                    GestorSalida.INSTANCE.ENVIAR_WUE_ZAPPI_ERROR(this);
                    return;
                }
            }
            if (Alineacion == Constantes.ALINEACION_BONTARIANO) {
                if (mapaID == 5295 || Mapa.getSubArea().getAlineacion() == Constantes.ALINEACION_BRAKMARIANO) {
                    GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "1TEAM_DIFFERENT_ALIGNMENT");
                    GestorSalida.INSTANCE.ENVIAR_WUE_ZAPPI_ERROR(this);
                    return;
                }
            }
            final int costo = Formulas.INSTANCE.calcularCosteZaap(Mapa, mapa);
            if (Kamas < costo) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "182");
                return;
            }
            addKamas(-costo, false, true);
            teleport(mapaID, celdaID);
            GestorSalida.INSTANCE.ENVIAR_WV_CERRAR_ZAAP(this);
        } catch (final Exception ignored) {
        }
    }

    public void usarZaapi(final short mapaID) {
        try {
            if (Deshonor >= 2) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "183");
                return;
            }
            final Mapa mapa = Mundo.getMapa(mapaID);
            short celdaID = 0;
            if (mapa == null || mapaID == Mapa.getId() || Mundo.esZaapi(Mapa.getId(), Alineacion) || Mundo.esZaapi(
                    mapaID, Alineacion)) {
                celdaID = mapa.getRandomCeldaIDLibre();
//                GestorSalida.INSTANCE.ENVIAR_BN_NADA(this);
//                return;
            }
            if (celdaID == 0) {
                for (final Celda celda : mapa.getCeldas().values()) {
                    try {
                        if (celda.getObjetoInteractivo().getObjIntModelo().getId() != 106) {
                            continue;
                        }
                        celdaID = (short) (celda.getId() + mapa.getAncho());
                        break;
                    } catch (final Exception ignored) {
                    }
                }
            }
            if (celdaID == 0) {
                return;
            }
            int costo = 20;
            if (Alineacion == Constantes.ALINEACION_BONTARIANO || Alineacion == Constantes.ALINEACION_BRAKMARIANO) {
                costo = 10;
            }
            if (Kamas < costo) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "182");
                return;
            }
            addKamas(-costo, false, true);
            teleport(mapaID, celdaID);
            GestorSalida.INSTANCE.ENVIAR_Wv_CERRAR_ZAPPI(this);
        } catch (final Exception ignored) {
        }
    }

    public void usarPrisma(final short mapaID) {
        try {
            if (Deshonor >= 1) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "183");
                return;
            }
            final Mapa mapa = Mundo.getMapa(mapaID);
            if (mapa == null || mapaID == Mapa.getId() || Mapa.getPrisma() == null || mapa.getPrisma() == null || Mapa
                    .getPrisma().getAlineacion() != Alineacion || mapa.getPrisma().getAlineacion() != Alineacion) {
                GestorSalida.INSTANCE.ENVIAR_BN_NADA(this);
                return;
            }
            if (!alasActivadas()) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "1144");
                return;
            }
            short celdaID = mapa.getPrisma().getCelda().getId();
            int costo = Formulas.INSTANCE.calcularCosteZaap(Mapa, Mundo.getMapa(mapaID));
            if (Kamas < costo) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "182");
                return;
            }
            addKamas(-costo, false, true);
            teleport(mapaID, celdaID);
            GestorSalida.INSTANCE.ENVIAR_Ww_CERRAR_PRISMA(this);
        } catch (final Exception ignored) {
        }
    }

    public void usarZonas(final short mapaID) {
        try {
            if (Mundo.getMapa(mapaID) == null || Mundo.ZONAS.get(mapaID) == null) {
                return;
            }
            teleport(mapaID, Mundo.ZONAS.get(mapaID));
        } catch (final Exception ignored) {
        }
    }

    public Objeto getObjModeloNoEquipado(final int idModelo, final int cantidad) {
        for (final Objeto obj : Objetos.values()) {
            if (Constantes.esPosicionEquipamiento(obj.getPosicion()) || obj.getObjModeloID() != idModelo) {
                continue;
            }
            if (obj.getCantidad() >= cantidad) {
                return obj;
            }
        }
        return null;
    }

    public void setOlvidandoHechizo(final boolean olvidandoHechizo) {
        OlvidandoHechizo = olvidandoHechizo;
    }

    public boolean estaOlvidandoHechizo() {
        return OlvidandoHechizo;
    }

    public boolean estaVisiblePara(final Personaje perso) {
        if (Ausente) {
            return false;
        }
        if (IdsOmitidos.contains(perso.getId())) {
            return false;
        }
        if (Cuenta.esEnemigo(perso.getCuentaID())) {
            return false;
        }
        if (Invisible) {
            return Cuenta.esAmigo(perso.getCuentaID());
        }
        return true;
    }

    public void enviarMsjAAmigos() {
        String str = getCuenta().getApodo() + " (<b><a href='asfunction:onHref,ShowPlayerPopupMenu," + getNombre() + "'>"
                + getNombre() + "</a></b>)";
        for (final Personaje online : Mundo.PERSONAJESONLINE) {
            try {
                if (online.Cuenta.esAmigo(Id)) {
                    GestorSalida.INSTANCE.ENVIAR_Im0143_AMIGO_CONECTADO(online, str);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public boolean estaAusente() {
        return Ausente;
    }

    public void setAusente(final boolean ausente) {
        Ausente = ausente;
    }

    public boolean esIndetectable() {
        return Indetectable;
    }

    public void setIndetectable(final boolean indetectable) {
        Indetectable = indetectable;
    }

    public boolean esInvisible() {
        return Invisible;
    }

    public void setInvisible(final boolean invisible) {
        Invisible = invisible;
    }

    public boolean esFantasma() {
        return !getRestriccionB(RB_NO_ES_FANTASMA);
    }

    public boolean esTumba() {
        return !getRestriccionB(RB_NO_ES_TUMBA);
    }

    public int getTitulo(boolean real) {
        if (!real) {
            try {
                String titulo = TotalStats.getStatsObjetos().getParamStatTexto(Constantes.STAT_TITULO, 3);
                if (!titulo.isEmpty()) {
                    return Integer.parseInt(titulo, 16);
                }
            } catch (Exception ignored) {
            }
        }
        return Titulo;
    }

    public String listaTitulosParaBD() {
        final StringBuilder str = new StringBuilder();
        for (final int b : Titulos.keySet()) {
            if (str.length() > 0) {
                str.append(",");
            }
            if (b == Titulo) {
                str.append("+");
            }
            str.append(b);
            if (Titulos.get(b) > -1) {
                str.append("*").append(Titulos.get(b));
            }
        }
        return str.toString();
    }

    public String getTituloVIP() {
        return TituloVIP;
    }

    public void setTituloVIP(final String titulo) {
        TituloVIP = titulo;
    }

    public void addTitulo(final int titulo, int color) {
        if (titulo > 0) {
            Titulos.put(titulo, color);
        }
        Titulo = titulo;
        if (EnLinea) {
            refrescarEnMapa();
        }
    }

    public void addOrnamento(final int ornamento) {
        if (ornamento <= 0 || Ornamentos.contains(ornamento)) {
            return;
        }
        Ornamentos.add(ornamento);
        Ornamentos.trimToSize();
    }

    public String listaOrnamentosParaBD() {
        final StringBuilder str = new StringBuilder();
        for (final int b : Ornamentos) {
            if (str.length() > 0) {
                str.append(",");
            }
            if (b == Ornamento) {
                str.append("+");
            }
            str.append(b);
        }
        return str.toString();
    }

    public boolean tieneOrnamento(final int ornamento) {
        return Ornamentos.contains(ornamento);
    }

    public boolean tieneTitulo(final int ornamento) {
        return Titulos.containsKey(ornamento);
    }

    public int getOrnamento() {
        return Ornamento;
    }

    public void setOrnamento(final int ornamento) {
        if (ornamento <= 0 || Ornamentos.contains(ornamento)) {
            Ornamento = ornamento;
            refrescarEnMapa();
        } else {
            GestorSalida.INSTANCE.ENVIAR_BN_NADA(this);
        }
    }

    public MisionPVP getMisionPVP() {
        return MisionPvp;
    }

    public void setMisionPVP(final MisionPVP mision) {
        MisionPvp = mision;
    }

    public String getEsposoListaAmigos() {
        final Personaje esposo = Mundo.getPersonaje(EsposoID);
        final StringBuilder str = new StringBuilder();
        if (esposo != null) {
            str.append(esposo.Nombre).append("|").append(esposo.ClaseID).append(esposo.Sexo).append("|").append(esposo.Color1).append("|").append(esposo.Color2).append("|").append(esposo.Color3).append("|");
            if (!esposo.EnLinea) {
                str.append("|");
            } else {
                str.append(esposo.stringUbicEsposo()).append("|");
            }
        } else {
            str.append("|");
        }
        return str.toString();
    }

    public String stringUbicEsposo() {
        return Mapa.getId() + "|" + Nivel + "|" + (Pelea != null ? 1 : 0);
    }

    public void seguirEsposo(Personaje esposo, String packet) {
        if (packet.charAt(3) == '+') {
            if (esposo.getMapa().getSubArea().getArea().getSuperArea() != Mapa.getSubArea().getArea().getSuperArea()) {
                if (esposo.getSexo() == Constantes.SEXO_FEMENINO) {
                    GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "178");
                } else {
                    GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "179");
                }
            }
            GestorSalida.INSTANCE.ENVIAR_IC_PERSONAJE_BANDERA_COMPAS(this, esposo.getMapa().getX() + "|" + esposo.getMapa().getY());
        } else {
            GestorSalida.INSTANCE.ENVIAR_IC_BORRAR_BANDERA_COMPAS(this);
        }
    }

    public void teleportEsposo(final Personaje esposo) {
        if (estaDisponible(false, true)) {
            if (esposo.getSexo() == Constantes.SEXO_FEMENINO) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "139");
            } else {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "140");
            }
            return;
        }
        if (esFantasma() || esTumba()) {
            if (esposo.getSexo() == Constantes.SEXO_FEMENINO) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "178");
            } else {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "179");
            }
            return;
        }
        final int dist = Camino.distanciaEntreMapas(Mapa, esposo.getMapa());
        if (dist > 10 || esposo.getMapa().esMazmorra()) {
            if (esposo.getSexo() == Constantes.SEXO_FEMENINO) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "181");
            } else {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "180");
            }
            return;
        }
        final short celdaPosicion = Camino.getCeldaIDCercanaLibre(esposo.getCelda(), esposo.getMapa());
        if (celdaPosicion == 0) {
            if (esposo.getSexo() == Constantes.SEXO_FEMENINO) {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "141");
            } else {
                GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "142");
            }
            return;
        }
        teleport(esposo.getMapa().getId(), celdaPosicion);
    }

    public void cambiarOrientacionADiagonal() {
        switch (Orientacion) {
            case 0:
            case 2:
            case 4:
            case 6:
                setOrientacion((byte) 7);
                GestorSalida.INSTANCE.ENVIAR_eD_CAMBIAR_ORIENTACION(Mapa, getId(), (byte) 7);
                break;
        }
    }

    public void addObjetoAlBanco(Objeto obj) {
        Cuenta.addObjetoAlBanco(obj);
    }

    public Cofre getConsultarCofre() {
        return ConsultarCofre;
    }

    public void setConsultarCofre(Cofre cofre) {
        ConsultarCofre = cofre;
    }

    public Casa getConsultarCasa() {
        return ConsultarCasa;
    }

    public void setConsultarCasa(final Casa casa) {
        ConsultarCasa = casa;
    }

    public Casa getAlgunaCasa() {
        if (ConsultarCasa != null) {
            return ConsultarCasa;
        }
        if (CasaDentro != null) {
            return CasaDentro;
        }
        return null;
    }

    public Casa getCasaDentro() {
        return CasaDentro;
    }

    public String stringColor() {
        return (Color1 <= -1 ? "" : Integer.toHexString(Color1)) + "," + (Color2 <= -1
                ? ""
                : Integer.toHexString(Color2)) + "," + (Color3 <= -1 ? "" : Integer.toHexString(Color3));
    }

    public String strObjEnPosParaOa(final byte posicion) {
        final Objeto obj = getObjPosicion(posicion);
        if (obj == null) {
            return "null";
        }
        try {
            if (!Objects.equals(obj.getParamStatTexto(Constantes.STAT_APARIENCIA_OBJETO, 2), "")) {
                return obj.getParamStatTexto(Constantes.STAT_APARIENCIA_OBJETO, 3);
            }
            if (obj.getObjevivoID() > 0) {
                final Objeto objVivo = Mundo.getObjeto(obj.getObjevivoID());
                if (objVivo != null) {
                    return Integer.toHexString(objVivo.getObjModeloID()) + "~" + obj.getObjModelo().getTipo() + "~" + Byte
                            .parseByte(objVivo.getParamStatTexto(Constantes.STAT_SKIN_OBJEVIVO, 3), 16);
                } else {
                    obj.setIDObjevivo(0);
                }
            }
            if (Mundo.getCreaTuItem(obj.getObjModeloID()) != null) {
                return Integer.toHexString(obj.getObjModeloID()) + "~" + obj.getObjModelo().getTipo() + "~" + (Integer.parseInt(
                        obj.getParamStatTexto(Constantes.STAT_CAMBIAR_GFX_OBJETO, 3), 16) + 1);
            }
        } catch (Exception ignored) {
        }
        return Integer.toHexString(obj.getObjModeloID());
    }

    @SuppressWarnings("rawtypes")
    public Object getIntercambiandoCon(Class clase) {
        if (Exchanger == null) {
            return null;
        }
        if (Exchanger.getClass() == clase) {
            return (clase.cast(Exchanger));
        }
        return null;
    }

    public Exchanger getExchanger() {
        return Exchanger;
    }

    public void setExchanger(final Exchanger intercambiando) {
        Exchanger = intercambiando;
    }

    public String getStringTienda() {
        final StringBuilder str = new StringBuilder();
        for (final Objeto obj : Tienda.getObjetos()) {
            str.append(obj.getId()).append("|");
        }
        return str.toString();
    }

    public void borrarObjTienda(final Objeto obj) {
        Tienda.borrarObjeto(obj);
    }

    public ArrayList<Objeto> getObjetosTienda() {
        return Tienda.getObjetos();
    }

    public Tienda getTienda() {
        return Tienda;
    }

    public long precioTotalTienda() {
        long precio = 0;
        for (final Objeto obj : Tienda.getObjetos()) {
            precio += obj.getPrecio();
        }
        return precio;
    }

    public String getListaExchanger(Personaje perso) {
        final StringBuilder str = new StringBuilder();
        for (final Objeto obj : Tienda.getObjetos()) {
            if (str.length() > 0) {
                str.append("|");
            }
            str.append(obj.getId()).append(";").append(obj.getCantidad()).append(";").append(obj.getObjModeloID()).append(";").append(obj.convertirStatsAString(
                    false)).append(";").append(obj.getPrecio());
        }
        return str.toString();
    }

    public String stringGMLuchador() {
        StringBuilder str = new StringBuilder();
        str.append(getClaseID(false)).append(";");
        str.append(getGfxID(false)).append("^").append(getTalla()).append(";");
        str.append(getSexo()).append(";");
        str.append(getNivel()).append(";");
        str.append(getAlineacion()).append(",");
        str.append(getOrdenNivel()).append(",");
        str.append(alasActivadas() ? getGradoAlineacion() : "0").append(",");
        str.append(getId() + getNivel()).append(",").append(getDeshonor() > 0 ? 1 : 0).append(";");
        str.append(getColor1() > -1 ? Integer.toHexString(getColor1()) : -1).append(";");
        str.append(getColor2() > -1 ? Integer.toHexString(getColor2()) : -1).append(";");
        str.append(getColor3() > -1 ? Integer.toHexString(getColor3()) : -1).append(";");
        str.append(getStringAccesorios()).append(";");
        return str.toString();
    }

    public Map<Integer, StatHechizo> getHechizos() {
        return MapStatsHechizos;
    }

    public void addKamasGanada(long kamas) {
    }

    public void addXPGanada(long exp) {
    }

    public void mostrarGrupo() {
        if (Grupo == null) {
            return;
        }
        Personaje lider = Grupo.getLiderGrupo();
        GestorSalida.INSTANCE.ENVIAR_PCK_CREAR_GRUPO(this, lider.getNombre());
        GestorSalida.INSTANCE.ENVIAR_PL_LIDER_GRUPO(this, lider.getId());
        GestorSalida.INSTANCE.ENVIAR_PM_TODOS_MIEMBROS_GRUPO_A_PERSO(this, Grupo);
    }

    @Override
    public void murio() {
    }

    @Override
    public void sobrevivio() {
    }

    public void cambiarNombre(String nombre) {
        setNombre(nombre);
        GestorSalida.INSTANCE.ENVIAR_bn_CAMBIAR_NOMBRE_CONFIRMADO(this, nombre);
        refrescarEnMapa();
        GestorSalida.INSTANCE.ENVIAR_Im_INFORMACION(this, "1NAME_CHANGED;" + nombre);
    }

    public void addSetRapido(int id, String nombre, int icono, String data) {
        SetRapido set = new SetRapido(id, nombre, icono, data);
        SetsRapidos.put(set.getId(), set);
    }

    public void borrarSetRapido(int id) {
        SetsRapidos.remove(id);
    }

    public SetRapido getSetRapido(int id) {
        return SetsRapidos.get(id);
    }

    public String getSetsRapidos() {
        StringBuilder str = new StringBuilder();
        for (SetRapido s : SetsRapidos.values()) {
            if (str.length() > 0) {
                str.append("*");
            }
            str.append(s.getString());
        }
        return str.toString();
    }

    public void actualizarSetsRapidos(int oldID, int newID, byte oldPos, byte newPos) {
        boolean b = false;
        for (SetRapido set : SetsRapidos.values()) {
            b |= set.actualizarObjetos(oldID, newID, oldPos, newPos);
        }
        if (b) {
            GestorSalida.INSTANCE.ENVIAR_Os_SETS_RAPIDOS(this);
        }
    }

    public Stats getStatsObjEquipados() {
        return TotalStats.getStatsObjetos();
    }

    @Override
    public void addKamas(long k, Personaje perso) {
        addKamas(k, false, true);
    }

    @Override
    public synchronized void addObjetoExchanger(Objeto objeto, int cantidad, Personaje perso, int precio) {
    }

    @Override
    public synchronized void remObjetoExchanger(Objeto objeto, int cantidad, Personaje perso, int precio) {
    }

    @Override
    public void cerrar(Personaje perso, String exito) {
        perso.cerrarVentanaExchange(exito);
    }

    public void botonOK(Personaje perso) {
    }

    @Override
    public String getArgsDialogo(String args) {
        if (args.isEmpty()) {
            return args;
        }
        return ";" + args.replace("[nombre]", getStringVar("nombre")).replace("[costoBanco]", getStringVar("costoBanco"))
                .replace("[lider]", Mundo.LIDER_RANKING).replace("[npcKamas]", AtlantaMain.KAMAS_RULETA_JALATO + "");
    }
}
