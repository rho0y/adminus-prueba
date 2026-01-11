package udla.adminus.mmunoz;

import java.sql.Connection;
import java.util.Scanner;

import udla.adminus.mmunoz.academico.Estudiante;
import udla.adminus.mmunoz.usuario.Administrativo;
import udla.adminus.mmunoz.academico.Docente;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static SQL util = new SQL();

    private static String cedulaActual = "";
    private static int tipoUsuarioActual = 0;
    private static boolean sesionIniciada = false;

    public static void main(String[] args) {
        int opcion = 0;

        while(opcion != 3) {
            mostrarMenuInicial();
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch(opcion) {
                case 1:
                    iniciarSesion();
                    if(sesionIniciada) {
                        menuPrincipal();
                        sesionIniciada = false;
                    }
                    break;
                case 2:
                    registrarse();
                    break;
                case 3:
                    System.out.println("\nGracias por usar ADMINUS!  Hasta pronto.");
                    break;
                default:
                    System. out.println("Opcion invalida.  Intente nuevamente.");
            }
        }

        scanner.close();
    }

    private static void mostrarMenuInicial() {
        System.out.println("\n========================================");
        System.out. println("      BIENVENIDO A ADMINUS");
        System.out.println("========================================");
        System.out.println("1. Iniciar Sesion");
        System.out.println("2. Registrarse");
        System.out. println("3. Salir");
        System.out.print("Seleccione una opcion: ");
    }

    private static void iniciarSesion() {
        System.out.println("\n========================================");
        System.out.println("  INICIAR SESIÓN");
        System.out.println("========================================");
        System.out. print("Ingrese su cedula: ");
        String cedula = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        if(cedula.isEmpty() || email.isEmpty()) {
            System.out.println("ERROR:  Debe ingresar cedula y email.");
            System.out. println("Si no tiene cuenta, debe registrarse primero.");
            return;
        }

        Connection conn = util.getConnection();
        if (conn != null) {
            boolean credencialesValidas = util. verificarCredenciales(cedula, email, conn);

            if (! credencialesValidas) {
                System.out.println("ERROR: Credenciales incorrectas.");
                System.out.println("Verifique su cedula y email, o registrese si no tiene cuenta.");
                return;
            }

            int tipoUsuario = util.verificarTipoUsuario(cedula, conn);

            if (tipoUsuario == 0) {
                System. out.println("ERROR: Usuario no registrado correctamente.");
                return;
            }

            cedulaActual = cedula;
            tipoUsuarioActual = tipoUsuario;
            sesionIniciada = true;

            String nombreRol = "";
            switch(tipoUsuarioActual) {
                case 1: nombreRol = "Estudiante"; break;
                case 2: nombreRol = "Docente"; break;
                case 3: nombreRol = "Administrativo"; break;
            }

            System.out.println("\nBienvenido! Ha iniciado sesion como: " + nombreRol);
        } else {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }
    }

    private static void registrarse() {
        System.out.println("\n========================================");
        System.out.println("  REGISTRO DE NUEVO USUARIO");
        System.out.println("========================================");
        System.out.println("¿Qué tipo de usuario desea registrar?");
        System.out.println("1. Estudiante");
        System.out.println("2. Docente");
        System.out.println("3. Personal Administrativo");
        System.out.print("Seleccione una opción: ");

        int tipo = scanner.nextInt();
        scanner.nextLine();

        if(tipo < 1 || tipo > 3) {
            System.out. println("ERROR: Opcion invalida. Seleccione entre 1 y 3.");
            return;
        }

        System.out.println("\n--- DATOS PERSONALES ---");

        System.out.print("Cedula: ");
        String cedula = scanner.nextLine();
        if(cedula.isEmpty()) {
            System.out.println("ERROR: La cedula no puede estar vacia.  Registro cancelado.");
            return;
        }

        Connection conn = util.getConnection();
        if (conn != null) {
            boolean existe = util.verificarUsuarioExiste(cedula, conn);
            if (existe) {
                System.out.println("ERROR:  Esta cedula ya esta registrada en el sistema.");
                System.out.println("Si es su cuenta, puede iniciar sesion directamente.");
                return;
            }
        } else {
            System.out. println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        System.out.print("Nombre completo: ");
        String nombre = scanner.nextLine();
        if(nombre.isEmpty()) {
            System.out.println("ERROR:  El nombre no puede estar vacio.  Registro cancelado.");
            return;
        }

        int edad = obtenerEdadValida();

        System.out.print("Genero (M/F):  ");
        String genero = scanner.nextLine();
        if(genero.isEmpty()) {
            System.out.println("ERROR:  El genero no puede estar vacio. Registro cancelado.");
            return;
        }

        System.out.print("Direccion: ");
        String direccion = scanner.nextLine();
        if(direccion.isEmpty()) {
            System.out.println("ERROR: La direccion no puede estar vacia.  Registro cancelado.");
            return;
        }

        System.out.print("Telefono: ");
        long telefono = scanner.nextLong();
        scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();
        if(email.isEmpty()) {
            System.out.println("ERROR:  El email no puede estar vacio.  Registro cancelado.");
            return;
        }

        switch(tipo) {
            case 1:
                registrarNuevoEstudiante(cedula, nombre, edad, genero, direccion, telefono, email);
                break;
            case 2:
                registrarNuevoDocente(cedula, nombre, edad, genero, direccion, telefono, email);
                break;
            case 3:
                registrarNuevoAdministrativo(cedula, nombre, edad, genero, direccion, telefono, email);
                break;
            default:
                System.out. println("Opcion invalida.");
        }
    }

    private static void registrarNuevoEstudiante(String cedula, String nombre, int edad,
                                                 String genero, String direccion, long telefono, String email) {
        System.out.println("\n--- DATOS ACADEMICOS DEL ESTUDIANTE ---");

        System.out.println("Seleccione el nivel educativo:");
        System.out. println("1. Bachillerato");
        System.out.println("2. Basica");
        System.out. print("Seleccione una opcion: ");
        int opcionNivel = scanner.nextInt();
        scanner.nextLine();

        String nivel;
        if(opcionNivel == 1) {
            nivel = "Bachillerato";
        } else if(opcionNivel == 2) {
            nivel = "Basica";
        } else {
            System.out.println("ERROR: Opcion invalida.  Registro cancelado.");
            return;
        }

        System.out. print("Paralelo: ");
        String paralelo = scanner.nextLine();
        if(paralelo.isEmpty()) {
            System.out.println("ERROR: El paralelo no puede estar vacio. Registro cancelado.");
            return;
        }

        System.out.print("Nombre del Representante: ");
        String representante = scanner.nextLine();
        if(representante.isEmpty()) {
            System.out.println("ERROR: Los datos del representante no pueden estar vacios. Registro cancelado.");
            return;
        }

        Estudiante estudiante = new Estudiante(cedula, nombre, edad, genero, direccion,
                telefono, email, nivel, paralelo, representante);

        Connection conn = util.getConnection();
        if (conn != null) {
            System.out.println("Conectado a la base de datos.. .");
            util.insertarDatos(estudiante, conn);
        } else {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        System.out.println("\n--- MATERIAS PREDEFINIDAS ---");
        Materia[] todasLasMaterias = Materia.values();
        for(int i = 0; i < todasLasMaterias.length; i++) {
            estudiante.addAsignatura(todasLasMaterias[i]. name());
            System.out.println((i+1) + ". " + todasLasMaterias[i].name());
        }

        System.out.println("\nEstudiante registrado exitosamente con todas las materias!");
        System.out.println(estudiante.mostrarInformacion());
    }

    private static void registrarNuevoDocente(String cedula, String nombre, int edad,
                                              String genero, String direccion, long telefono, String email) {
        System.out.println("\n--- DATOS LABORALES DEL DOCENTE ---");

        double sueldo = obtenerSueldoValido();

        System.out.print("Jornada Laboral: ");
        String jornada = scanner.nextLine();
        if(jornada.isEmpty()) {
            System.out.println("ERROR: La jornada no puede estar vacia. Registro cancelado.");
            return;
        }

        int horas = obtenerHorasTrabajadasValidas();

        System.out.print("Especialidad: ");
        String especialidad = scanner.nextLine();
        if(especialidad.isEmpty()) {
            System.out.println("ERROR: La especialidad no puede estar vacia. Registro cancelado.");
            return;
        }

        System.out.print("Titulo Academico: ");
        String titulo = scanner.nextLine();
        if(titulo.isEmpty()) {
            System.out.println("ERROR: El titulo no puede estar vacio. Registro cancelado.");
            return;
        }

        System.out.print("Carga Horaria: ");
        int carga = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Horario de Clases (Ej:  Lunes-Viernes 8:00-12:00): ");
        String horario = scanner.nextLine();
        if(horario.isEmpty()) {
            System.out.println("ERROR:  El horario no puede estar vacio. Registro cancelado.");
            return;
        }

        System.out.println("\n--- SELECCION DE MATERIA ---");
        System.out.println("Seleccione la materia que impartira:");
        Materia[] todasLasMaterias = Materia. values();
        for(int i = 0; i < todasLasMaterias.length; i++) {
            System.out.println((i+1) + ". " + todasLasMaterias[i].name());
        }
        System.out.print("Opcion: ");
        int opcionMateria = scanner.nextInt();
        scanner.nextLine();

        if(opcionMateria < 1 || opcionMateria > todasLasMaterias.length) {
            System.out.println("ERROR:  Opcion invalida. Registro cancelado.");
            return;
        }

        Docente docente = new Docente(cedula, nombre, edad, genero, direccion, telefono,
                email, sueldo, jornada, horas, especialidad, titulo, carga);

        docente.setMateriaAsignada(todasLasMaterias[opcionMateria - 1]);
        docente.addCurso(todasLasMaterias[opcionMateria - 1].name());

        Connection conn = util. getConnection();
        if (conn != null) {
            System. out.println("Conectado a la base de datos...");
            util.insertarDocente(docente, conn, horario, opcionMateria);
        } else {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        System.out.println("Materia asignada:  " + todasLasMaterias[opcionMateria - 1].name());
        System.out.println("\nDocente registrado exitosamente!");
        System.out.println(docente.mostrarInformacion());
    }

    private static void registrarNuevoAdministrativo(String cedula, String nombre, int edad,
                                                     String genero, String direccion, long telefono, String email) {
        System.out. println("\n--- DATOS LABORALES DEL ADMINISTRATIVO ---");

        double sueldo = obtenerSueldoValido();

        System.out.print("Jornada Laboral: ");
        String jornada = scanner.nextLine();
        if(jornada. isEmpty()) {
            System.out.println("ERROR: La jornada no puede estar vacia.  Registro cancelado.");
            return;
        }

        int horas = obtenerHorasTrabajadasValidas();

        System.out.print("Cargo: ");
        String cargo = scanner.nextLine();
        if(cargo.isEmpty()) {
            System.out.println("ERROR: El cargo no puede estar vacio.  Registro cancelado.");
            return;
        }

        System.out.print("Area:  ");
        String area = scanner. nextLine();
        if(area.isEmpty()) {
            System. out.println("ERROR: El area no puede estar vacia. Registro cancelado.");
            return;
        }

        Administrativo admin = new Administrativo(cedula, nombre, edad, genero, direccion,
                telefono, email, sueldo, jornada, horas, cargo, area);

        Connection conn = util. getConnection();
        if (conn != null) {
            System. out.println("Conectado a la base de datos...");
            util.insertarAdministrativo(admin, conn);
        } else {
            System.out. println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        System.out.println("\nAdministrativo registrado exitosamente!");
        System.out. println(admin.mostrarInformacion());
    }

    private static void menuPrincipal() {
        int opcion = 0;

        if(tipoUsuarioActual == 1) {
            while(opcion != 6) {
                mostrarMenuEstudiante();
                opcion = scanner.nextInt();
                scanner.nextLine();
                menuEstudiante(opcion);
            }
        } else if(tipoUsuarioActual == 2) {
            while(opcion != 7) {
                mostrarMenuDocente();
                opcion = scanner.nextInt();
                scanner. nextLine();
                menuDocente(opcion);
            }
        } else if(tipoUsuarioActual == 3) {
            while(opcion != 7) {
                mostrarMenuAdministrativo();
                opcion = scanner.nextInt();
                scanner.nextLine();
                menuAdministrativo(opcion);
            }
        }
    }

    private static void mostrarMenuEstudiante() {
        System.out.println("\n========================================");
        System.out.println("      MENU ESTUDIANTE");
        System.out.println("========================================");
        System.out.println("1. Ver mi informacion");
        System.out. println("2. Ver mis Notas");
        System.out. println("3. Ver mi Registro de Asistencia");
        System.out.println("4. Ver mis Docentes");
        System.out. println("5. Ver mis Materias");
        System.out.println("6. Cerrar Sesion");
        System.out.print("Seleccione una opcion: ");
    }

    private static void mostrarMenuDocente() {
        System.out.println("\n========================================");
        System.out.println("      MENU DOCENTE");
        System.out.println("========================================");
        System.out.println("1. Ver y Editar Notas de Estudiantes");
        System.out.println("2. Ver y Editar Asistencias");
        System.out.println("3. Inscribir Estudiante a mi Materia");
        System.out.println("4. Ver Estudiantes Inscritos en mi Materia");
        System.out.println("5. Ver mi Curso Asignado");
        System.out.println("6. Ver mi Horario de Clases");
        System.out.println("7. Cerrar Sesion");
        System.out. print("Seleccione una opcion: ");
    }

    private static void mostrarMenuAdministrativo() {
        System.out.println("\n========================================");
        System.out.println("      MENU ADMINISTRATIVO");
        System.out.println("========================================");
        System.out. println("1. Ver mi informacion");
        System.out. println("2. Ver Estudiantes");
        System.out.println("3. Ver y Editar Notas");
        System.out. println("4. Ver y Editar Asistencia");
        System.out.println("5. Ver Docentes");
        System.out. println("6. Ver Personal y Estado de Empleabilidad");
        System.out. println("7. Cerrar Sesion");
        System.out.print("Seleccione una opcion: ");
    }

    private static void menuEstudiante(int opcion) {
        Connection conn;
        switch(opcion) {
            case 1:
                conn = util.getConnection();
                if(conn != null) {
                    util.mostrarInformacionEstudiante(cedulaActual, conn);
                } else {
                    System.out.println("ERROR: No se pudo conectar a la base de datos.");
                }
                break;
            case 2:
                System.out.println("\n=== MIS NOTAS ===");
                conn = util.getConnection();
                if(conn != null) {
                    util.mostrarNotasEstudiante(cedulaActual, conn);
                } else {
                    System. out.println("ERROR: No se pudo conectar a la base de datos.");
                }
                break;
            case 3:
                System.out.println("\n=== MI ASISTENCIA ===");
                conn = util.getConnection();
                if(conn != null) {
                    util.mostrarAsistencias(cedulaActual, conn);
                } else {
                    System.out.println("ERROR: No se pudo conectar a la base de datos.");
                }
                break;
            case 4:
                conn = util.getConnection();
                if(conn != null) {
                    util.mostrarTodosLosDocentes(conn);
                } else {
                    System.out.println("ERROR: No se pudo conectar a la base de datos.");
                }
                break;
            case 5:
                conn = util. getConnection();
                if(conn != null) {
                    util.mostrarTodasLasMaterias(conn);
                } else {
                    System.out.println("ERROR: No se pudo conectar a la base de datos.");
                }
                break;
            case 6:
                cerrarSesion();
                break;
            default:
                System.out.println("Opcion invalida.");
        }
    }

    private static void menuDocente(int opcion) {
        switch(opcion) {
            case 1:
                gestionarNotasEstudiantes();
                break;
            case 2:
                gestionarAsistencias();
                break;
            case 3:
                inscribirEstudianteEnMateria();
                break;
            case 4:
                verEstudiantesInscritosEnMiMateria();
                break;
            case 5:
                verCursoAsignado();
                break;
            case 6:
                verHorarioDocente();
                break;
            case 7:
                cerrarSesion();
                break;
            default:
                System. out.println("Opcion invalida.");
        }
    }

    private static void menuAdministrativo(int opcion) {
        Connection conn;
        switch(opcion) {
            case 1:
                conn = util.getConnection();
                if(conn != null) {
                    util.mostrarInformacionAdministrativo(cedulaActual, conn);
                } else {
                    System.out.println("ERROR: No se pudo conectar a la base de datos.");
                }
                break;
            case 2:
                System.out. println("\n=== VER ESTUDIANTES ===");
                conn = util.getConnection();
                if(conn != null) {
                    util.mostrarTodosLosEstudiantes(conn);
                } else {
                    System.out.println("ERROR: No se pudo conectar a la base de datos.");
                }
                break;
            case 3:
                System. out.println("\n=== VER Y EDITAR NOTAS ===");
                gestionarNotasAdmin();
                break;
            case 4:
                System.out.println("\n=== VER Y EDITAR ASISTENCIA ===");
                gestionarAsistenciasAdmin();
                break;
            case 5:
                System.out.println("\n=== VER DOCENTES ===");
                conn = util.getConnection();
                if(conn != null) {
                    util.mostrarTodosLosDocentes(conn);
                } else {
                    System.out.println("ERROR:  No se pudo conectar a la base de datos.");
                }
                break;
            case 6:
                gestionarEmpleabilidad();
                break;
            case 7:
                cerrarSesion();
                break;
            default:
                System. out.println("Opcion invalida.");
        }
    }

    private static void gestionarNotasEstudiantes() {
        System.out.println("\n=== GESTION DE NOTAS ===");
        System.out. println("1. Ingresar notas de estudiantes");
        System.out. println("2. Ver notas de estudiantes");
        System.out.println("3. Editar notas de estudiantes");
        System.out.print("Seleccione una opcion: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();

        Connection conn = util.getConnection();
        if(conn == null) {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        if(opcion == 1) {
            System.out.print("Ingrese la cedula del estudiante: ");
            String cedulaEstudiante = scanner.nextLine();

            if(! util.verificarUsuarioExiste(cedulaEstudiante, conn)) {
                System. out.println("ERROR: El estudiante con cedula " + cedulaEstudiante + " no existe.");
                return;
            }

            System.out.println("\n--- MATERIAS DISPONIBLES ---");
            Materia[] materias = Materia.values();
            for(int i = 0; i < materias.length; i++) {
                System.out.println((i+1) + ". " + materias[i].name());
            }

            System.out.print("Ingrese el ID de la asignatura (1-6): ");
            int asignaturaId = scanner.nextInt();
            scanner.nextLine();

            if(asignaturaId < 1 || asignaturaId > 6) {
                System.out.println("ERROR: El ID debe estar entre 1 y 6.");
                return;
            }

            if(!util.verificarDocenteEnseñaMateria(cedulaActual, asignaturaId, conn)) {
                System.out.println("ERROR:  Usted no enseña esta materia.");
                return;
            }

            System.out.print("Ingrese el parcial:  ");
            String parcial = scanner.nextLine();

            System.out.print("Ingrese la nota (0-10): ");
            double nota = scanner.nextDouble();
            scanner.nextLine();

            if(nota >= 0 && nota <= 10) {
                util.insertarNota(cedulaEstudiante, asignaturaId, parcial, nota, conn);
            } else {
                System.out.println("ERROR: La nota debe estar entre 0 y 10.");
            }

        } else if(opcion == 2) {
            System.out.print("Ingrese la cedula del estudiante: ");
            String cedulaEstudiante = scanner.nextLine();

            if(!util.verificarUsuarioExiste(cedulaEstudiante, conn)) {
                System.out.println("ERROR:  El estudiante con cedula " + cedulaEstudiante + " no existe.");
                return;
            }

            util.mostrarNotasEstudiante(cedulaEstudiante, conn);

        } else if(opcion == 3) {
            System.out.print("Ingrese la cedula del estudiante: ");
            String cedulaEstudiante = scanner.nextLine();

            if(!util.verificarUsuarioExiste(cedulaEstudiante, conn)) {
                System. out.println("ERROR: El estudiante con cedula " + cedulaEstudiante + " no existe.");
                return;
            }

            System.out.println("\n--- MATERIAS DISPONIBLES ---");
            Materia[] materias = Materia.values();
            for(int i = 0; i < materias.length; i++) {
                System.out.println((i+1) + ". " + materias[i].name());
            }

            System.out.print("Ingrese el ID de la asignatura (1-6): ");
            int asignaturaId = scanner.nextInt();
            scanner. nextLine();

            if(asignaturaId < 1 || asignaturaId > 6) {
                System.out.println("ERROR: El ID debe estar entre 1 y 6.");
                return;
            }

            if(!util.verificarDocenteEnseñaMateria(cedulaActual, asignaturaId, conn)) {
                System.out.println("ERROR: Usted no enseña esta materia.");
                return;
            }

            System.out.print("Ingrese el parcial: ");
            String parcial = scanner.nextLine();

            System.out.print("Ingrese la nueva nota (0-10): ");
            double nota = scanner.nextDouble();
            scanner.nextLine();

            if(nota >= 0 && nota <= 10) {
                util.actualizarNota(cedulaEstudiante, asignaturaId, parcial, nota, conn);
            } else {
                System. out.println("ERROR: La nota debe estar entre 0 y 10.");
            }
        } else {
            System.out.println("Opcion invalida.");
        }
    }

    private static void gestionarNotasAdmin() {
        System.out.println("1. Ver notas de un estudiante");
        System.out.println("2. Ingresar nota");
        System.out.println("3. Editar nota");
        System.out.print("Seleccione una opcion: ");
        int opcion = scanner. nextInt();
        scanner.nextLine();

        Connection conn = util.getConnection();
        if(conn == null) {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        if(opcion == 1) {
            System.out.print("Ingrese la cedula del estudiante: ");
            String cedulaEstudiante = scanner.nextLine();
            util.mostrarNotasEstudiante(cedulaEstudiante, conn);

        } else if(opcion == 2 || opcion == 3) {
            System.out.print("Ingrese la cedula del estudiante: ");
            String cedulaEstudiante = scanner. nextLine();

            if(!util.verificarUsuarioExiste(cedulaEstudiante, conn)) {
                System.out.println("ERROR: El estudiante no existe.");
                return;
            }

            System.out.println("\n--- MATERIAS DISPONIBLES ---");
            Materia[] materias = Materia. values();
            for(int i = 0; i < materias.length; i++) {
                System.out.println((i+1) + ". " + materias[i].name());
            }

            System.out.print("Ingrese el ID de la asignatura (1-6): ");
            int asignaturaId = scanner.nextInt();
            scanner.nextLine();

            if(asignaturaId < 1 || asignaturaId > 6) {
                System.out.println("ERROR:  El ID debe estar entre 1 y 6.");
                return;
            }

            System.out.print("Ingrese el parcial: ");
            String parcial = scanner.nextLine();

            System.out.print("Ingrese la nota (0-10): ");
            double nota = scanner.nextDouble();
            scanner.nextLine();

            if(nota >= 0 && nota <= 10) {
                if(opcion == 2) {
                    util.insertarNota(cedulaEstudiante, asignaturaId, parcial, nota, conn);
                } else {
                    util.actualizarNota(cedulaEstudiante, asignaturaId, parcial, nota, conn);
                }
            } else {
                System. out.println("ERROR: La nota debe estar entre 0 y 10.");
            }
        } else {
            System.out. println("Opcion invalida.");
        }
    }

    private static void gestionarAsistencias() {
        System.out. println("\n=== GESTION DE ASISTENCIAS ===");
        System.out.println("1. Registrar asistencia de estudiantes");
        System.out.println("2. Ver asistencias de estudiantes");
        System.out. println("3. Editar asistencia de estudiantes");
        System.out.print("Seleccione una opcion:  ");
        int opcion = scanner.nextInt();
        scanner.nextLine();

        Connection conn = util.getConnection();
        if(conn == null) {
            System.out.println("ERROR:  No se pudo conectar a la base de datos.");
            return;
        }

        if(opcion == 1) {
            System.out.print("Ingrese la cedula del estudiante:  ");
            String cedulaEstudiante = scanner.nextLine();

            System.out.println("\n--- MATERIAS DISPONIBLES ---");
            Materia[] materias = Materia. values();
            for(int i = 0; i < materias.length; i++) {
                System.out.println((i+1) + ". " + materias[i].name());
            }

            System.out.print("Ingrese el ID de la asignatura (1-6): ");
            int asignaturaId = scanner.nextInt();
            scanner.nextLine();

            if(asignaturaId < 1 || asignaturaId > 6) {
                System.out.println("ERROR:  El ID debe estar entre 1 y 6.");
                return;
            }

            System.out.print("Ingrese la fecha (YYYY-MM-DD): ");
            String fecha = scanner.nextLine();

            System. out.println("\nEstado:");
            System.out.println("1. Presente");
            System.out.println("2. Ausente");
            System.out.println("3. Tarde");
            System.out.print("Seleccione una opcion: ");
            int estadoOpcion = scanner.nextInt();
            scanner.nextLine();

            String estado = "";
            if(estadoOpcion == 1) {
                estado = "Presente";
            } else if(estadoOpcion == 2) {
                estado = "Ausente";
            } else if(estadoOpcion == 3) {
                estado = "Tarde";
            } else {
                System.out. println("Opcion invalida.");
                return;
            }

            util. registrarAsistencia(cedulaEstudiante, asignaturaId, fecha, estado, conn);

        } else if(opcion == 2) {
            System. out.print("Ingrese la cedula del estudiante: ");
            String cedulaEstudiante = scanner.nextLine();
            util.mostrarAsistencias(cedulaEstudiante, conn);

        } else if(opcion == 3) {
            System.out.print("Ingrese la cedula del estudiante: ");
            String cedulaEstudiante = scanner. nextLine();

            System.out.println("\n--- MATERIAS DISPONIBLES ---");
            Materia[] materias = Materia.values();
            for(int i = 0; i < materias.length; i++) {
                System. out.println((i+1) + ". " + materias[i].name());
            }

            System.out.print("Ingrese el ID de la asignatura (1-6): ");
            int asignaturaId = scanner.nextInt();
            scanner.nextLine();

            if(asignaturaId < 1 || asignaturaId > 6) {
                System. out.println("ERROR: El ID debe estar entre 1 y 6.");
                return;
            }

            System.out.print("Ingrese la fecha (YYYY-MM-DD): ");
            String fecha = scanner.nextLine();

            System.out.println("\nNuevo estado:");
            System.out.println("1. Presente");
            System.out.println("2. Ausente");
            System.out.println("3. Tarde");
            System.out.print("Seleccione una opcion: ");
            int estadoOpcion = scanner.nextInt();
            scanner.nextLine();

            String estado = "";
            if(estadoOpcion == 1) {
                estado = "Presente";
            } else if(estadoOpcion == 2) {
                estado = "Ausente";
            } else if(estadoOpcion == 3) {
                estado = "Tarde";
            } else {
                System.out.println("Opcion invalida.");
                return;
            }

            util. actualizarAsistencia(cedulaEstudiante, asignaturaId, fecha, estado, conn);
        } else {
            System.out.println("Opcion invalida.");
        }
    }

    private static void gestionarAsistenciasAdmin() {
        System.out.println("1. Ver asistencias de un estudiante");
        System.out.println("2. Registrar asistencia");
        System.out.println("3. Editar asistencia");
        System.out.print("Seleccione una opcion: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();

        Connection conn = util.getConnection();
        if(conn == null) {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        if(opcion == 1) {
            System.out.print("Ingrese la cedula del estudiante: ");
            String cedulaEstudiante = scanner.nextLine();
            util.mostrarAsistencias(cedulaEstudiante, conn);

        } else if(opcion == 2 || opcion == 3) {
            System.out.print("Ingrese la cedula del estudiante: ");
            String cedulaEstudiante = scanner. nextLine();

            if(!util.verificarUsuarioExiste(cedulaEstudiante, conn)) {
                System.out.println("ERROR: El estudiante no existe.");
                return;
            }

            System.out. println("\n--- MATERIAS DISPONIBLES ---");
            Materia[] materias = Materia.values();
            for(int i = 0; i < materias.length; i++) {
                System.out.println((i+1) + ". " + materias[i].name());
            }

            System.out. print("Ingrese el ID de la asignatura (1-6): ");
            int asignaturaId = scanner.nextInt();
            scanner.nextLine();

            if(asignaturaId < 1 || asignaturaId > 6) {
                System.out.println("ERROR: El ID debe estar entre 1 y 6.");
                return;
            }

            System. out.print("Ingrese la fecha (YYYY-MM-DD): ");
            String fecha = scanner.nextLine();

            System. out.println("\nEstado:");
            System.out. println("1. Presente");
            System.out.println("2. Ausente");
            System.out.println("3. Tarde");
            System.out. print("Seleccione una opcion: ");
            int estadoOpcion = scanner.nextInt();
            scanner.nextLine();

            String estado = "";
            if(estadoOpcion == 1) {
                estado = "Presente";
            } else if(estadoOpcion == 2) {
                estado = "Ausente";
            } else if(estadoOpcion == 3) {
                estado = "Tarde";
            } else {
                System.out.println("Opcion invalida.");
                return;
            }

            if(opcion == 2) {
                util.registrarAsistencia(cedulaEstudiante, asignaturaId, fecha, estado, conn);
            } else {
                util.actualizarAsistencia(cedulaEstudiante, asignaturaId, fecha, estado, conn);
            }
        } else {
            System.out. println("Opcion invalida.");
        }
    }

    private static void inscribirEstudianteEnMateria() {
        System.out.println("\n=== INSCRIBIR ESTUDIANTE A MI MATERIA ===");

        Connection conn = util.getConnection();
        if(conn == null) {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        int asignaturaId = util.obtenerAsignaturaDocente(cedulaActual, conn);
        if(asignaturaId == -1) {
            System.out.println("ERROR: No tiene una asignatura asignada.");
            return;
        }

        String nombreAsignatura = util.obtenerNombreAsignatura(asignaturaId, conn);
        System.out.println("Asignatura: " + nombreAsignatura);
        System.out.println("\n--- ESTUDIANTES DISPONIBLES PARA INSCRIBIR ---");
        util.listarEstudiantesNoInscritos(asignaturaId, conn);

        System.out.print("\nIngrese la cedula del estudiante a inscribir (0 para cancelar): ");
        String cedulaEstudiante = scanner.nextLine();

        if(cedulaEstudiante.equals("0")) {
            System.out.println("Operacion cancelada.");
            return;
        }

        if(!util.verificarUsuarioExiste(cedulaEstudiante, conn)) {
            System.out.println("ERROR: El estudiante con cedula " + cedulaEstudiante + " no existe.");
            return;
        }

        if(util.verificarTipoUsuario(cedulaEstudiante, conn) != 1) {
            System. out.println("ERROR: La cedula no corresponde a un estudiante.");
            return;
        }

        if(util.estudianteInscritoEnAsignatura(cedulaEstudiante, asignaturaId, conn)) {
            System.out.println("ERROR: El estudiante ya esta inscrito en esta materia.");
            return;
        }

        String nombreEstudiante = util.obtenerNombrePersona(cedulaEstudiante, conn);
        System.out.print("¿Confirma inscribir a " + nombreEstudiante + " en " + nombreAsignatura + "?  (S/N): ");
        String confirmacion = scanner.nextLine();

        if(confirmacion.equalsIgnoreCase("S")) {
            util.inscribirEstudianteEnAsignatura(cedulaEstudiante, asignaturaId, conn);
        } else {
            System.out.println("Inscripcion cancelada.");
        }
    }

    private static void verEstudiantesInscritosEnMiMateria() {
        System.out.println("\n=== ESTUDIANTES INSCRITOS EN MI MATERIA ===");

        Connection conn = util.getConnection();
        if(conn == null) {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        int asignaturaId = util. obtenerAsignaturaDocente(cedulaActual, conn);
        if(asignaturaId == -1) {
            System.out.println("ERROR: No tiene una asignatura asignada.");
            return;
        }

        String nombreAsignatura = util.obtenerNombreAsignatura(asignaturaId, conn);
        System.out.println("Asignatura: " + nombreAsignatura + "\n");

        util.listarEstudiantesInscritos(asignaturaId, conn);
    }

    private static void gestionarEmpleabilidad() {
        System.out.println("\n=== GESTION DE EMPLEABILIDAD ===");
        System.out.println("1. Ver todo el personal y su estado");
        System.out.println("2. Actualizar estado de empleabilidad");
        System.out. print("Seleccione una opcion: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();

        Connection conn = util.getConnection();
        if(conn == null) {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        if(opcion == 1) {
            util.mostrarTodosLosEmpleados(conn);

        } else if(opcion == 2) {
            System. out.print("Ingrese la cedula del empleado: ");
            String cedulaEmpleado = scanner.nextLine();

            int tipoEmpleado = util.verificarTipoUsuario(cedulaEmpleado, conn);
            if(tipoEmpleado != 2 && tipoEmpleado != 3) {
                System.out.println("ERROR: La cedula no corresponde a un docente o administrativo.");
                return;
            }

            System.out.println("\nSeleccione el nuevo estado:");
            System.out.println("1. Activo");
            System.out.println("2. Inactivo");
            System.out.println("3. Licencia");
            System.out.println("4. Vacaciones");
            System.out.println("5. Despedido");
            System.out.print("Opcion: ");
            int estadoOpcion = scanner.nextInt();
            scanner.nextLine();

            String nuevoEstado = "";
            switch(estadoOpcion) {
                case 1: nuevoEstado = "Activo"; break;
                case 2: nuevoEstado = "Inactivo"; break;
                case 3: nuevoEstado = "Licencia"; break;
                case 4: nuevoEstado = "Vacaciones"; break;
                case 5: nuevoEstado = "Despedido"; break;
                default:
                    System.out.println("Opcion invalida.");
                    return;
            }

            util.actualizarEstadoEmpleabilidad(cedulaEmpleado, nuevoEstado, conn);
        } else {
            System.out.println("Opcion invalida.");
        }
    }

    private static void verCursoAsignado() {
        System.out.println("\n=== MI CURSO ASIGNADO ===");
        Connection conn = util.getConnection();
        if(conn != null) {
            util.mostrarCursoDocente(cedulaActual, conn);
        } else {
            System.out. println("ERROR: No se pudo conectar a la base de datos.");
        }
    }

    private static void verHorarioDocente() {
        System.out.println("\n=== MI HORARIO DE CLASES ===");
        Connection conn = util.getConnection();
        if(conn != null) {
            util.mostrarHorarioDocente(cedulaActual, conn);
        } else {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
        }
    }

    private static void cerrarSesion() {
        System.out.println("\nSesion cerrada exitosamente.");
        cedulaActual = "";
        tipoUsuarioActual = 0;
    }

    private static int obtenerEdadValida() {
        int edad = 0;
        boolean valido = false;
        while (! valido) {
            try {
                System.out.print("Edad: ");
                edad = scanner. nextInt();
                scanner.nextLine();
                if (edad >= 5 && edad <= 100) {
                    valido = true;
                } else {
                    System.out.println("ERROR: La edad debe estar entre 5 y 100 años.");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("ERROR: Ingrese un numero valido.");
                scanner.nextLine();
            }
        }
        return edad;
    }

    private static double obtenerSueldoValido() {
        double sueldo = 0;
        boolean valido = false;
        while (!valido) {
            try {
                System. out.print("Sueldo Mensual: ");
                sueldo = scanner.nextDouble();
                scanner.nextLine();
                if (sueldo > 0) {
                    valido = true;
                } else {
                    System.out. println("ERROR: El sueldo debe ser mayor a 0.");
                }
            } catch (java.util. InputMismatchException e) {
                System.out.println("ERROR: Ingrese un numero valido.");
                scanner.nextLine();
            }
        }
        return sueldo;
    }

    private static int obtenerHorasTrabajadasValidas() {
        int horas = 0;
        boolean valido = false;
        while (!valido) {
            try {
                System.out.print("Horas Trabajadas:  ");
                horas = scanner. nextInt();
                scanner.nextLine();
                if (horas > 0 && horas <= 24) {
                    valido = true;
                } else {
                    System.out.println("ERROR: Las horas trabajadas deben estar entre 1 y 24.");
                }
            } catch (java.util.InputMismatchException e) {
                System. out.println("ERROR: Ingrese un numero valido.");
                scanner.nextLine();
            }
        }
        return horas;
    }
}