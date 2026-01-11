package udla.adminus.mmunoz;

import udla.adminus.mmunoz.academico.Estudiante;
import udla.adminus.mmunoz.usuario.Administrativo;
import udla.adminus.mmunoz.academico.Docente;

import java.sql. Connection;
import java.sql. DriverManager;
import java. sql.PreparedStatement;
import java.sql.ResultSet;
import java. sql.SQLException;

public class SQL {

    public Connection getConnection() {
        String url = "jdbc:mysql://localhost:3306/adminus";
        String user = "root";
        String passwd = "ñ?km877AS/%*";

        try {
            Connection conn = DriverManager.getConnection(url, user, passwd);
            return conn;
        } catch (SQLException ex) {
            System.out. println("Error estableciendo la conexion con la base de datos:");
            ex.printStackTrace();
        }
        return null;
    }

    // ==================== METODOS DE VERIFICACION ====================

    public boolean verificarUsuarioExiste(String cedula, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT cedula FROM persona WHERE cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            System.out. println("Error al verificar usuario:");
            ex.printStackTrace();
            return false;
        } finally {
            cerrarRecursos(rs, ps);
        }
    }

    public boolean verificarCredenciales(String cedula, String email, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT cedula FROM persona WHERE cedula = ? AND email = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            ps.setString(2, email);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            System.out.println("Error al verificar credenciales:");
            ex.printStackTrace();
            return false;
        } finally {
            cerrarRecursos(rs, ps);
        }
    }

    public int verificarTipoUsuario(String cedula, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT cedula FROM estudiante WHERE cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps.executeQuery();
            if (rs.next()) return 1;
            cerrarRecursos(rs, ps);

            sql = "SELECT cedula FROM docente WHERE cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps.executeQuery();
            if (rs.next()) return 2;
            cerrarRecursos(rs, ps);

            sql = "SELECT cedula FROM administrativo WHERE cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps.executeQuery();
            if (rs.next()) return 3;

            return 0;
        } catch (SQLException ex) {
            System. out.println("Error al verificar tipo de usuario:");
            ex.printStackTrace();
            return 0;
        } finally {
            cerrarRecursos(rs, ps);
        }
    }

    public boolean verificarDocenteEnseñaMateria(String cedulaDocente, int asignaturaId, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT COUNT(*) FROM docente_materia WHERE docente_cedula = ? AND materia_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaDocente);
            ps.setInt(2, asignaturaId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.out.println("Error al verificar materia del docente:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
        return false;
    }

    public boolean estudianteInscritoEnAsignatura(String cedulaEstudiante, int asignaturaId, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT COUNT(*) FROM inscripcion WHERE estudiante_cedula = ? AND asignatura_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            ps.setInt(2, asignaturaId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.out.println("Error al verificar inscripcion:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
        return false;
    }

    // ==================== METODOS DE INSERCION ====================

    public void insertarDatos(Estudiante estudiante, Connection conn) {
        PreparedStatement psPersona = null;
        PreparedStatement psEstudiante = null;
        PreparedStatement psRol = null;

        try {
            conn.setAutoCommit(false);

            String sqlPersona = "INSERT INTO persona (cedula, nombre_completo, edad, genero, direccion, telefono, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
            psPersona = conn.prepareStatement(sqlPersona);
            psPersona.setString(1, estudiante.getCedula());
            psPersona.setString(2, estudiante.getNombre());
            psPersona.setInt(3, estudiante.getEdad());
            psPersona. setString(4, estudiante. getGenero());
            psPersona.setString(5, estudiante.getDireccion());
            psPersona.setLong(6, estudiante.getTelefono());
            psPersona.setString(7, estudiante.getEmail());
            int personaResultado = psPersona.executeUpdate();

            int cursoId = obtenerCursoPorNivelParalelo(estudiante.getNivelEducativo(), estudiante.getParalelo(), conn);

            String sqlEstudiante;
            if (cursoId > 0) {
                sqlEstudiante = "INSERT INTO estudiante (cedula, nivel_educativo, paralelo, representante, curso_id) VALUES (?, ?, ?, ?, ?)";
            } else {
                sqlEstudiante = "INSERT INTO estudiante (cedula, nivel_educativo, paralelo, representante) VALUES (?, ?, ?, ?)";
            }
            psEstudiante = conn.prepareStatement(sqlEstudiante);
            psEstudiante.setString(1, estudiante.getCedula());
            psEstudiante.setString(2, estudiante.getNivelEducativo());
            psEstudiante.setString(3, estudiante.getParalelo());
            psEstudiante. setString(4, estudiante.getDatosRepresentante());
            if (cursoId > 0) {
                psEstudiante.setInt(5, cursoId);
            }
            int estudianteResultado = psEstudiante.executeUpdate();

            String sqlRol = "INSERT INTO rolusuario (usuario_cedula, rol_id) VALUES (?, 1)";
            psRol = conn.prepareStatement(sqlRol);
            psRol.setString(1, estudiante.getCedula());
            psRol.executeUpdate();

            if (personaResultado > 0 && estudianteResultado > 0) {
                System.out.println("El estudiante se ha insertado correctamente.");
                conn. commit();
            } else {
                System.out.println("No se pudo ingresar al estudiante");
                conn.rollback();
            }
        } catch (SQLException ex) {
            System.out.println("Error al ingresar el estudiante");
            ex.printStackTrace();
            rollback(conn);
        } finally {
            cerrarRecursos(null, psPersona);
            cerrarRecursos(null, psEstudiante);
            cerrarRecursos(null, psRol);
        }
    }

    public void insertarDocente(Docente docente, Connection conn, String horario, int asignaturaId) {
        PreparedStatement psPersona = null;
        PreparedStatement psDocente = null;
        PreparedStatement psRol = null;
        PreparedStatement psEstado = null;
        PreparedStatement psDocenteMateria = null;

        try {
            conn.setAutoCommit(false);

            String sqlPersona = "INSERT INTO persona (cedula, nombre_completo, edad, genero, direccion, telefono, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
            psPersona = conn.prepareStatement(sqlPersona);
            psPersona.setString(1, docente.getCedula());
            psPersona.setString(2, docente.getNombre());
            psPersona. setInt(3, docente. getEdad());
            psPersona.setString(4, docente.getGenero());
            psPersona.setString(5, docente.getDireccion());
            psPersona.setLong(6, docente. getTelefono());
            psPersona.setString(7, docente.getEmail());
            int personaResultado = psPersona.executeUpdate();

            String sqlDocente = "INSERT INTO docente (cedula, especialidad, titulo_academico, jornada_laboral, sueldo_mensual, carga_horaria, horario_clases, asignatura_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            psDocente = conn.prepareStatement(sqlDocente);
            psDocente.setString(1, docente.getCedula());
            psDocente.setString(2, docente.getEspecialidad());
            psDocente.setString(3, docente.getTituloAcademico());
            psDocente.setString(4, docente.getJornadaLaboral());
            psDocente.setDouble(5, docente.getSueldo());
            psDocente. setInt(6, docente.getCargaHoraria());
            psDocente.setString(7, horario);
            psDocente. setInt(8, asignaturaId);
            int docenteResultado = psDocente.executeUpdate();

            String sqlDocenteMateria = "INSERT INTO docente_materia (docente_cedula, materia_id) VALUES (?, ?)";
            psDocenteMateria = conn.prepareStatement(sqlDocenteMateria);
            psDocenteMateria.setString(1, docente.getCedula());
            psDocenteMateria.setInt(2, asignaturaId);
            psDocenteMateria.executeUpdate();

            String sqlRol = "INSERT INTO rolusuario (usuario_cedula, rol_id) VALUES (?, 2)";
            psRol = conn.prepareStatement(sqlRol);
            psRol. setString(1, docente. getCedula());
            psRol.executeUpdate();

            String sqlEstado = "INSERT INTO estadoempleabilidad (cedula, estado) VALUES (?, 'Activo')";
            psEstado = conn.prepareStatement(sqlEstado);
            psEstado.setString(1, docente.getCedula());
            psEstado.executeUpdate();

            if (personaResultado > 0 && docenteResultado > 0) {
                System.out.println("El docente se ha insertado correctamente.");
                conn.commit();
            } else {
                System.out.println("No se pudo ingresar al docente");
                conn.rollback();
            }
        } catch (SQLException ex) {
            System. out.println("Error al ingresar el docente");
            ex.printStackTrace();
            rollback(conn);
        } finally {
            cerrarRecursos(null, psPersona);
            cerrarRecursos(null, psDocente);
            cerrarRecursos(null, psRol);
            cerrarRecursos(null, psEstado);
            cerrarRecursos(null, psDocenteMateria);
        }
    }

    public void insertarAdministrativo(Administrativo admin, Connection conn) {
        PreparedStatement psPersona = null;
        PreparedStatement psAdmin = null;
        PreparedStatement psRol = null;
        PreparedStatement psEstado = null;

        try {
            conn.setAutoCommit(false);

            String sqlPersona = "INSERT INTO persona (cedula, nombre_completo, edad, genero, direccion, telefono, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
            psPersona = conn.prepareStatement(sqlPersona);
            psPersona.setString(1, admin.getCedula());
            psPersona.setString(2, admin.getNombre());
            psPersona.setInt(3, admin.getEdad());
            psPersona.setString(4, admin.getGenero());
            psPersona. setString(5, admin.getDireccion());
            psPersona.setLong(6, admin.getTelefono());
            psPersona.setString(7, admin.getEmail());
            int personaResultado = psPersona.executeUpdate();

            String sqlAdmin = "INSERT INTO administrativo (cedula, cargo, area, jornada_laboral, horas_trabajadas, sueldo) VALUES (?, ?, ?, ?, ?, ?)";
            psAdmin = conn.prepareStatement(sqlAdmin);
            psAdmin.setString(1, admin. getCedula());
            psAdmin.setString(2, admin. getCargo());
            psAdmin. setString(3, admin.getArea());
            psAdmin.setString(4, admin.getJornadaLaboral());
            psAdmin.setInt(5, admin.getHorasTrabajadas());
            psAdmin.setDouble(6, admin.getSueldo());
            int adminResultado = psAdmin.executeUpdate();

            String sqlRol = "INSERT INTO rolusuario (usuario_cedula, rol_id) VALUES (?, 3)";
            psRol = conn.prepareStatement(sqlRol);
            psRol.setString(1, admin.getCedula());
            psRol. executeUpdate();

            String sqlEstado = "INSERT INTO estadoempleabilidad (cedula, estado) VALUES (?, 'Activo')";
            psEstado = conn.prepareStatement(sqlEstado);
            psEstado. setString(1, admin.getCedula());
            psEstado.executeUpdate();

            if (personaResultado > 0 && adminResultado > 0) {
                System.out.println("El administrativo se ha insertado correctamente.");
                conn.commit();
            } else {
                System.out.println("No se pudo ingresar al administrativo");
                conn.rollback();
            }
        } catch (SQLException ex) {
            System.out.println("Error al ingresar el administrativo");
            ex.printStackTrace();
            rollback(conn);
        } finally {
            cerrarRecursos(null, psPersona);
            cerrarRecursos(null, psAdmin);
            cerrarRecursos(null, psRol);
            cerrarRecursos(null, psEstado);
        }
    }

    public void insertarNota(String cedulaEstudiante, int asignaturaId, String parcial, double nota, Connection conn) {
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO notas (estudiante_cedula, asignatura_id, parcial, nota) VALUES (?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            ps.setInt(2, asignaturaId);
            ps.setString(3, parcial);
            ps.setDouble(4, nota);

            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas > 0) {
                System.out.println("Nota registrada exitosamente.");
            } else {
                System.out. println("No se pudo registrar la nota.");
            }
        } catch (SQLException ex) {
            System.out.println("Error al insertar nota:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(null, ps);
        }
    }

    public void inscribirEstudianteEnAsignatura(String cedulaEstudiante, int asignaturaId, Connection conn) {
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO inscripcion (estudiante_cedula, asignatura_id) VALUES (?, ?)";
            ps = conn. prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            ps.setInt(2, asignaturaId);

            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas > 0) {
                System.out. println("\n========================================");
                System.out.println("  INSCRIPCION EXITOSA");
                System.out.println("========================================");
                System.out.println("Estudiante inscrito correctamente.");
                System. out.println("Fecha:  " + java.time.LocalDate.now());
                System.out.println("========================================");
            } else {
                System.out.println("No se pudo realizar la inscripcion.");
            }
        } catch (SQLException ex) {
            System.out.println("Error al inscribir estudiante:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(null, ps);
        }
    }

    // ==================== METODOS DE ACTUALIZACION ====================

    public void actualizarNota(String cedulaEstudiante, int asignaturaId, String parcial, double nota, Connection conn) {
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE notas SET nota = ? WHERE estudiante_cedula = ? AND asignatura_id = ? AND parcial = ?";
            ps = conn.prepareStatement(sql);
            ps.setDouble(1, nota);
            ps.setString(2, cedulaEstudiante);
            ps.setInt(3, asignaturaId);
            ps.setString(4, parcial);

            int filasActualizadas = ps.executeUpdate();
            if (filasActualizadas > 0) {
                System.out.println("Nota actualizada exitosamente.");
            } else {
                System.out.println("No se encontro el registro a actualizar.");
            }
        } catch (SQLException ex) {
            System.out.println("Error al actualizar nota:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(null, ps);
        }
    }

    public void registrarAsistencia(String cedulaEstudiante, int asignaturaId, String fecha, String estado, Connection conn) {
        PreparedStatement ps = null;

        try {
            int cursoId = obtenerCursoEstudiante(cedulaEstudiante, conn);
            if (cursoId == 0) {
                cursoId = 1;
            }

            String sql = "INSERT INTO asistencia (estudiante_cedula, asignatura_id, curso_id, fecha, estado) VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            ps.setInt(2, asignaturaId);
            ps.setInt(3, cursoId);
            ps.setString(4, fecha);
            ps.setString(5, estado);

            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas > 0) {
                System.out.println("Asistencia registrada exitosamente.");
            } else {
                System.out.println("No se pudo registrar la asistencia.");
            }
        } catch (SQLException ex) {
            System. out.println("Error al registrar asistencia:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(null, ps);
        }
    }

    public void actualizarAsistencia(String cedulaEstudiante, int asignaturaId, String fecha, String estado, Connection conn) {
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE asistencia SET estado = ? WHERE estudiante_cedula = ? AND asignatura_id = ? AND fecha = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, estado);
            ps.setString(2, cedulaEstudiante);
            ps.setInt(3, asignaturaId);
            ps.setString(4, fecha);

            int filasActualizadas = ps.executeUpdate();
            if (filasActualizadas > 0) {
                System.out.println("Asistencia actualizada exitosamente.");
            } else {
                System.out.println("No se encontro el registro a actualizar.");
            }
        } catch (SQLException ex) {
            System. out.println("Error al actualizar asistencia:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(null, ps);
        }
    }

    public void actualizarEstadoEmpleabilidad(String cedula, String nuevoEstado, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sqlVerificar = "SELECT id_estado FROM estadoempleabilidad WHERE cedula = ?";
            ps = conn.prepareStatement(sqlVerificar);
            ps.setString(1, cedula);
            rs = ps.executeQuery();

            if (rs.next()) {
                cerrarRecursos(rs, ps);
                String sqlUpdate = "UPDATE estadoempleabilidad SET estado = ?, fecha_actualizacion = CURRENT_DATE WHERE cedula = ?";
                ps = conn.prepareStatement(sqlUpdate);
                ps.setString(1, nuevoEstado);
                ps.setString(2, cedula);
                int filasActualizadas = ps.executeUpdate();
                if (filasActualizadas > 0) {
                    System. out.println("Estado de empleabilidad actualizado exitosamente.");
                }
            } else {
                cerrarRecursos(rs, ps);
                String sqlInsert = "INSERT INTO estadoempleabilidad (cedula, estado) VALUES (?, ?)";
                ps = conn.prepareStatement(sqlInsert);
                ps.setString(1, cedula);
                ps.setString(2, nuevoEstado);
                int filasInsertadas = ps.executeUpdate();
                if (filasInsertadas > 0) {
                    System.out.println("Estado de empleabilidad registrado exitosamente.");
                }
            }
        } catch (SQLException ex) {
            System. out.println("Error al actualizar estado de empleabilidad:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
    }

    // ==================== METODOS DE CONSULTA ====================

    public void mostrarNotasEstudiante(String cedula, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT n.asignatura_id, a.nombre, n.parcial, n.nota, n.fecha_registro " +
                    "FROM notas n " +
                    "JOIN asignatura a ON n.asignatura_id = a.id_asignatura " +
                    "WHERE n.estudiante_cedula = ?  " +
                    "ORDER BY a.nombre, n.parcial";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps.executeQuery();

            boolean hayNotas = false;
            System.out.println("\n===================== MIS NOTAS =====================");
            System.out.printf("%-25s %-15s %-10s %-15s%n", "ASIGNATURA", "PARCIAL", "NOTA", "FECHA");
            System.out.println("======================================================");

            while (rs. next()) {
                hayNotas = true;
                String nombreMateria = rs.getString("nombre");
                String parcial = rs.getString("parcial");
                double nota = rs.getDouble("nota");
                String fecha = rs.getString("fecha_registro");
                System.out.printf("%-25s %-15s %-10.2f %-15s%n", nombreMateria, parcial, nota, fecha);
            }

            if (!hayNotas) {
                System.out. println("No hay notas registradas.");
            }
            System.out.println("======================================================");
        } catch (SQLException ex) {
            System.out. println("Error al mostrar notas del estudiante:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
    }

    public void mostrarAsistencias(String cedulaEstudiante, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT a.fecha, a.estado, ag.nombre AS asignatura " +
                    "FROM asistencia a " +
                    "JOIN asignatura ag ON a.asignatura_id = ag.id_asignatura " +
                    "WHERE a.estudiante_cedula = ? " +
                    "ORDER BY a.fecha DESC, ag.nombre";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            rs = ps.executeQuery();

            System.out. println("\n=================== MI ASISTENCIA ===================");
            System.out.printf("%-15s %-25s %-15s%n", "FECHA", "ASIGNATURA", "ESTADO");
            System.out.println("======================================================");

            boolean hayAsistencias = false;
            while (rs.next()) {
                hayAsistencias = true;
                String fecha = rs. getString("fecha");
                String asignatura = rs.getString("asignatura");
                String estado = rs.getString("estado");
                System.out.printf("%-15s %-25s %-15s%n", fecha, asignatura, estado);
            }

            if (!hayAsistencias) {
                System.out.println("No hay registros de asistencia.");
            }
            System. out.println("======================================================");
        } catch (SQLException ex) {
            System.out.println("Error al mostrar asistencias:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
    }

    public void mostrarInformacionEstudiante(String cedula, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT p.cedula, p.nombre_completo, p.edad, p.genero, p.direccion, p.telefono, p. email, " +
                    "e.nivel_educativo, e.paralelo, e.representante " +
                    "FROM persona p " +
                    "JOIN estudiante e ON p.cedula = e.cedula " +
                    "WHERE p.cedula = ?";
            ps = conn. prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps. executeQuery();

            if (rs.next()) {
                System. out.println("\n=== MI INFORMACION PERSONAL ===");
                System. out.println("Cedula: " + rs.getString("cedula"));
                System.out.println("Nombre Completo: " + rs. getString("nombre_completo"));
                System.out.println("Edad: " + rs.getInt("edad"));
                System.out.println("Genero: " + rs.getString("genero"));
                System.out.println("Direccion: " + rs.getString("direccion"));
                System.out.println("Telefono: " + rs.getLong("telefono"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("\n=== INFORMACION ACADEMICA ===");
                System.out.println("Nivel Educativo: " + rs.getString("nivel_educativo"));
                System.out.println("Paralelo: " + rs.getString("paralelo"));
                System.out.println("Nombre del Representante: " + rs. getString("representante"));
            } else {
                System.out. println("No se encontro informacion del estudiante.");
            }
        } catch (SQLException ex) {
            System.out.println("Error al mostrar informacion del estudiante:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
    }

    public void mostrarInformacionAdministrativo(String cedula, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT p.nombre_completo, p.edad, p.genero, p. direccion, p.telefono, p.email, " +
                    "a.sueldo, a.jornada_laboral, a. horas_trabajadas, a.cargo, a.area, " +
                    "e.estado AS estado_empleabilidad " +
                    "FROM persona p " +
                    "JOIN administrativo a ON p.cedula = a.cedula " +
                    "LEFT JOIN estadoempleabilidad e ON p.cedula = e.cedula " +
                    "WHERE p.cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n========================================");
                System.out.println("      MI INFORMACION");
                System.out.println("========================================");
                System.out.println("Cedula: " + cedula);
                System.out. println("Nombre:  " + rs.getString("nombre_completo"));
                System.out.println("Edad: " + rs.getInt("edad"));
                System.out.println("Genero: " + rs.getString("genero"));
                System. out.println("Direccion: " + rs.getString("direccion"));
                System.out.println("Telefono: " + rs.getLong("telefono"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("\n--- DATOS LABORALES ---");
                System.out.println("Cargo: " + rs.getString("cargo"));
                System.out.println("Area: " + rs.getString("area"));
                System.out.println("Sueldo: $" + rs.getDouble("sueldo"));
                System.out.println("Jornada Laboral: " + rs.getString("jornada_laboral"));
                System.out.println("Horas Trabajadas: " + rs.getInt("horas_trabajadas"));
                String estado = rs.getString("estado_empleabilidad");
                System. out.println("Estado Empleabilidad: " + (estado != null ? estado : "Activo"));
                System.out. println("========================================");
            } else {
                System.out.println("No se encontro informacion del administrativo.");
            }
        } catch (SQLException ex) {
            System.out.println("Error al mostrar informacion:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
    }

    public void mostrarTodosLosDocentes(Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT p.nombre_completo, d.especialidad, d.titulo_academico, a.nombre AS materia " +
                    "FROM docente d " +
                    "JOIN persona p ON d.cedula = p.cedula " +
                    "LEFT JOIN asignatura a ON d. asignatura_id = a. id_asignatura";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            boolean hayDocentes = false;
            System.out.println("\n=== DOCENTES REGISTRADOS ===");
            System.out.printf("%-30s %-20s %-25s %-20s%n", "NOMBRE", "ESPECIALIDAD", "TITULO", "MATERIA");
            System.out.println("================================================================================");

            while (rs.next()) {
                hayDocentes = true;
                String nombre = rs.getString("nombre_completo");
                String especialidad = rs. getString("especialidad");
                String titulo = rs.getString("titulo_academico");
                String materia = rs.getString("materia");
                if (materia == null) materia = "Sin asignar";
                System.out.printf("%-30s %-20s %-25s %-20s%n", nombre, especialidad, titulo, materia);
            }

            if (!hayDocentes) {
                System.out.println("No se han ingresado ningun docente.");
            }
        } catch (SQLException ex) {
            System.out.println("Error al mostrar docentes:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
    }

    public void mostrarTodasLasMaterias(Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT a.nombre, a.codigo, a.horas_semanales, p.nombre_completo AS docente " +
                    "FROM asignatura a " +
                    "LEFT JOIN docente d ON a.id_asignatura = d.asignatura_id " +
                    "LEFT JOIN persona p ON d.cedula = p.cedula " +
                    "ORDER BY a.nombre";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            System.out.println("\n=== TODAS LAS MATERIAS ===");
            System.out.printf("%-25s %-10s %-10s %-30s%n", "MATERIA", "CODIGO", "HORAS", "DOCENTE ASIGNADO");
            System.out.println("================================================================================");

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String codigo = rs.getString("codigo");
                int horas = rs.getInt("horas_semanales");
                String docente = rs.getString("docente");
                if (docente == null) docente = "Sin asignar";
                System.out.printf("%-25s %-10s %-10d %-30s%n", nombre, codigo, horas, docente);
            }
        } catch (SQLException ex) {
            System.out.println("Error al mostrar materias:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
    }

    public void mostrarTodosLosEstudiantes(Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT e.cedula, p.nombre_completo, e. nivel_educativo, e.paralelo " +
                    "FROM estudiante e " +
                    "JOIN persona p ON e.cedula = p.cedula " +
                    "ORDER BY e.nivel_educativo, e.paralelo, p.nombre_completo";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            System. out.println("\n========================================================================================================");
            System.out.println("                                    LISTADO DE ESTUDIANTES");
            System.out.println("========================================================================================================");
            System.out.printf("%-15s %-35s %-25s %-15s%n", "CEDULA", "NOMBRE COMPLETO", "NIVEL EDUCATIVO", "PARALELO");
            System.out.println("--------------------------------------------------------------------------------------------------------");

            boolean hayEstudiantes = false;
            while (rs.next()) {
                hayEstudiantes = true;
                String cedula = rs. getString("cedula");
                String nombre = rs.getString("nombre_completo");
                String nivelEducativo = rs.getString("nivel_educativo");
                String paralelo = rs.getString("paralelo");
                System.out.printf("%-15s %-35s %-25s %-15s%n", cedula, nombre, nivelEducativo, paralelo);
            }

            if (!hayEstudiantes) {
                System.out.println("No hay estudiantes registrados en el sistema.");
            }
            System.out.println("========================================================================================================");
        } catch (SQLException ex) {
            System.out.println("Error al mostrar estudiantes:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
    }

    public void mostrarCursoDocente(String cedulaDocente, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT d.especialidad, a.nombre AS materia " +
                    "FROM docente d " +
                    "LEFT JOIN asignatura a ON d. asignatura_id = a. id_asignatura " +
                    "WHERE d.cedula = ? ";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaDocente);
            rs = ps.executeQuery();

            if (rs.next()) {
                String especialidad = rs.getString("especialidad");
                String materia = rs.getString("materia");
                System.out.println("Especialidad: " + especialidad);
                System.out.println("Materia asignada: " + (materia != null ? materia :  "Sin asignar"));
            } else {
                System.out.println("No se encontro informacion del curso.");
            }
        } catch (SQLException ex) {
            System.out.println("Error al mostrar curso:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
    }

    public void mostrarHorarioDocente(String cedulaDocente, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT horario_clases FROM docente WHERE cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaDocente);
            rs = ps.executeQuery();

            if (rs.next()) {
                String horario = rs.getString("horario_clases");
                System.out.println("Horario: " + (horario != null ? horario :  "No definido"));
            } else {
                System.out.println("No se encontro informacion del horario.");
            }
        } catch (SQLException ex) {
            System.out.println("Error al mostrar horario:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
    }

    public void mostrarTodosLosEmpleados(Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT p.cedula, p.nombre_completo, 'Docente' AS tipo, e.estado " +
                    "FROM persona p " +
                    "JOIN docente d ON p.cedula = d.cedula " +
                    "LEFT JOIN estadoempleabilidad e ON p.cedula = e.cedula " +
                    "UNION " +
                    "SELECT p.cedula, p.nombre_completo, 'Administrativo' AS tipo, e.estado " +
                    "FROM persona p " +
                    "JOIN administrativo a ON p. cedula = a.cedula " +
                    "LEFT JOIN estadoempleabilidad e ON p. cedula = e.cedula " +
                    "ORDER BY tipo, nombre_completo";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            System.out.println("\n=================== PERSONAL DE LA INSTITUCION ===================");
            System.out. printf("%-15s %-30s %-15s %-15s%n", "CEDULA", "NOMBRE", "TIPO", "ESTADO");
            System.out.println("==================================================================");

            boolean hayEmpleados = false;
            while (rs.next()) {
                hayEmpleados = true;
                String cedula = rs.getString("cedula");
                String nombre = rs.getString("nombre_completo");
                String tipo = rs.getString("tipo");
                String estado = rs.getString("estado");
                if (estado == null) estado = "Activo";
                System.out. printf("%-15s %-30s %-15s %-15s%n", cedula, nombre, tipo, estado);
            }

            if (!hayEmpleados) {
                System.out. println("No hay empleados registrados.");
            }
            System.out.println("==================================================================");
        } catch (SQLException ex) {
            System.out. println("Error al mostrar empleados:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
    }

    public void listarEstudiantesInscritos(int asignaturaId, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT p.cedula, p.nombre_completo, e.nivel_educativo, e.paralelo, i.fecha_inscripcion " +
                    "FROM inscripcion i " +
                    "JOIN estudiante e ON i.estudiante_cedula = e.cedula " +
                    "JOIN persona p ON e.cedula = p.cedula " +
                    "WHERE i.asignatura_id = ? " +
                    "ORDER BY p.nombre_completo";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, asignaturaId);
            rs = ps.executeQuery();

            System. out.println("================================================================================");
            System.out.printf("%-15s %-30s %-15s %-10s %-15s%n", "CEDULA", "NOMBRE", "NIVEL", "PARALELO", "FECHA INSC.");
            System.out.println("================================================================================");

            boolean hayEstudiantes = false;
            while (rs.next()) {
                hayEstudiantes = true;
                String cedula = rs.getString("cedula");
                String nombre = rs.getString("nombre_completo");
                String nivel = rs.getString("nivel_educativo");
                String paralelo = rs.getString("paralelo");
                String fecha = rs.getString("fecha_inscripcion");
                System.out.printf("%-15s %-30s %-15s %-10s %-15s%n", cedula, nombre, nivel, paralelo, fecha);
            }

            if (!hayEstudiantes) {
                System.out.println("No hay estudiantes inscritos en esta asignatura.");
            }
            System.out.println("================================================================================");
        } catch (SQLException ex) {
            System.out.println("Error al listar estudiantes inscritos:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
    }

    public void listarEstudiantesNoInscritos(int asignaturaId, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT p.cedula, p.nombre_completo, e. nivel_educativo, e.paralelo " +
                    "FROM estudiante e " +
                    "JOIN persona p ON e.cedula = p.cedula " +
                    "WHERE e.cedula NOT IN (SELECT estudiante_cedula FROM inscripcion WHERE asignatura_id = ?) " +
                    "ORDER BY p.nombre_completo";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, asignaturaId);
            rs = ps.executeQuery();

            System. out.println("================================================================================");
            System.out.printf("%-15s %-35s %-20s %-10s%n", "CEDULA", "NOMBRE", "NIVEL", "PARALELO");
            System.out.println("================================================================================");

            boolean hayEstudiantes = false;
            while (rs.next()) {
                hayEstudiantes = true;
                String cedula = rs.getString("cedula");
                String nombre = rs.getString("nombre_completo");
                String nivel = rs.getString("nivel_educativo");
                String paralelo = rs. getString("paralelo");
                System.out.printf("%-15s %-35s %-20s %-10s%n", cedula, nombre, nivel, paralelo);
            }

            if (! hayEstudiantes) {
                System.out.println("Todos los estudiantes ya estan inscritos en esta asignatura.");
            }
            System.out.println("================================================================================");
        } catch (SQLException ex) {
            System.out.println("Error al listar estudiantes:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
    }

    // ==================== METODOS AUXILIARES ====================

    public int obtenerAsignaturaDocente(String cedulaDocente, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT asignatura_id FROM docente WHERE cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaDocente);
            rs = ps.executeQuery();

            if (rs.next()) {
                int asignaturaId = rs. getInt("asignatura_id");
                if (! rs.wasNull()) {
                    return asignaturaId;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al obtener asignatura del docente:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
        return -1;
    }

    public String obtenerNombreAsignatura(int asignaturaId, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT nombre FROM asignatura WHERE id_asignatura = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, asignaturaId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("nombre");
            }
        } catch (SQLException ex) {
            System.out. println("Error al obtener nombre de asignatura:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
        return "Desconocida";
    }

    public String obtenerNombrePersona(String cedula, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT nombre_completo FROM persona WHERE cedula = ?";
            ps = conn. prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps. executeQuery();

            if (rs.next()) {
                return rs.getString("nombre_completo");
            }
        } catch (SQLException ex) {
            System. out.println("Error al obtener nombre:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
        return "Desconocido";
    }

    public int obtenerCursoEstudiante(String cedulaEstudiante, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT curso_id FROM estudiante WHERE cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            rs = ps.executeQuery();

            if (rs.next()) {
                int cursoId = rs.getInt("curso_id");
                if (!rs.wasNull()) {
                    return cursoId;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al obtener curso del estudiante:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
        return 0;
    }

    public int obtenerCursoPorNivelParalelo(String nivel, String paralelo, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT id_curso FROM curso WHERE nivel = ? AND paralelo = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, nivel);
            ps.setString(2, paralelo);
            rs = ps. executeQuery();

            if (rs.next()) {
                return rs.getInt("id_curso");
            }
        } catch (SQLException ex) {
            System.out.println("Error al obtener curso:");
            ex.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }
        return 0;
    }

    // ==================== METODOS PRIVADOS ====================

    private void cerrarRecursos(ResultSet rs, PreparedStatement ps) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void rollback(Connection conn) {
        try {
            if (conn != null) conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}