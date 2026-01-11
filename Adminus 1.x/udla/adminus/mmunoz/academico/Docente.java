package udla.adminus.mmunoz.academico;

import udla.adminus.mmunoz.Informacion;
import udla.adminus.mmunoz.Materia;
import udla.adminus.mmunoz.usuario.Empleado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java. sql.SQLException;
import java. util.ArrayList;
import java. util.List;

public class Docente extends Empleado implements Informacion {
    private String especialidad;
    private String tituloAcademico;
    private int cargaHoraria;
    private List<String> cursosAsignados;
    private Materia materiaAsignada;

    public Docente(String cedula, String nombre, int edad, String genero,
                   String direccion, long telefono, String email,
                   double sueldoMensual, String jornadaLaboral, int horasTrabajadas,
                   String especialidad, String tituloAcademico, int cargaHoraria) {
        super(cedula, nombre, edad, genero, direccion, telefono, email,
                sueldoMensual, jornadaLaboral, horasTrabajadas);
        this.especialidad = especialidad;
        this.tituloAcademico = tituloAcademico;
        this.cargaHoraria = cargaHoraria;
        this. cursosAsignados = new ArrayList<>();
    }

    @Override
    public String mostrarInformacion() {
        String info = super.mostrarInformacion() +
                "Especialidad: " + this. especialidad + "\n" +
                "Titulo Academico: " + this.tituloAcademico + "\n" +
                "Carga Horaria: " + this. cargaHoraria + " horas\n";

        if(materiaAsignada != null) {
            info += "Materia Asignada: " + materiaAsignada.name() + "\n";
        }

        if(! cursosAsignados.isEmpty()) {
            info += "Cursos Asignados:\n";
            for(String curso : this.cursosAsignados) {
                info += "  - " + curso + "\n";
            }
        }

        return info;
    }

    @Override
    public void mostrarInformacionCompleta(String cedula, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT p.nombre_completo, p.edad, p.genero, p.direccion, p.telefono, p.email, " +
                    "d.especialidad, d.horario_clases, d.sueldo_mensual, d. jornada_laboral, d.carga_horaria, " +
                    "a.nombre AS asignatura, e.estado AS estado_empleabilidad " +
                    "FROM persona p " +
                    "INNER JOIN docente d ON p.cedula = d.cedula " +
                    "LEFT JOIN asignatura a ON d. asignatura_id = a.id_asignatura " +
                    "LEFT JOIN estadoempleabilidad e ON p.cedula = e.cedula " +
                    "WHERE p.cedula = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps.executeQuery();

            if(rs.next()) {
                System.out.println("\n========================================");
                System.out.println("      MI INFORMACION - DOCENTE");
                System. out.println("========================================");
                System.out.println("Cedula: " + cedula);
                System.out. println("Nombre: " + rs.getString("nombre_completo"));
                System.out.println("Edad: " + rs.getInt("edad"));
                System. out.println("Genero: " + rs.getString("genero"));
                System.out.println("Direccion: " + rs.getString("direccion"));
                System.out.println("Telefono: " + rs.getLong("telefono"));
                System. out.println("Email: " + rs.getString("email"));
                System.out.println("\n--- DATOS LABORALES ---");
                System.out.println("Especialidad: " + rs.getString("especialidad"));
                System.out.println("Horario de Clases: " + rs. getString("horario_clases"));
                String asignatura = rs.getString("asignatura");
                System.out.println("Asignatura: " + (asignatura != null ? asignatura : "Sin asignar"));
                System.out. println("Sueldo:  $" + rs.getDouble("sueldo_mensual"));
                System.out.println("Jornada Laboral: " + rs.getString("jornada_laboral"));
                System.out.println("Carga Horaria: " + rs.getInt("carga_horaria") + " horas");
                String estado = rs.getString("estado_empleabilidad");
                System. out.println("Estado: " + (estado != null ? estado : "Activo"));
                System.out.println("========================================\n");
            }

        } catch(SQLException ex) {
            System.out.println("ERROR al mostrar informacion: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public double calcularNomina() {
        return getSueldoMensual();
    }

    public void addCurso(String curso) {
        this.cursosAsignados.add(curso);
    }

    public void setMateriaAsignada(Materia materia) {
        this.materiaAsignada = materia;
    }

    public Materia getMateriaAsignada() {
        return materiaAsignada;
    }

    public List<String> getCursosAsignados() {
        return cursosAsignados;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getTituloAcademico() {
        return tituloAcademico;
    }

    public void setTituloAcademico(String tituloAcademico) {
        this.tituloAcademico = tituloAcademico;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(int cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public String getJornada() {
        return getJornadaLaboral();
    }

    public int getHoras() {
        return getHorasTrabajadas();
    }

    public double getSueldo() {
        return getSueldoMensual();
    }
}