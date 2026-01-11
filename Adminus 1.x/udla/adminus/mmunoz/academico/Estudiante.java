package udla.adminus.mmunoz.academico;

import udla.adminus.mmunoz.Informacion;
import udla.adminus.mmunoz.usuario.Persona;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Estudiante extends Persona implements Informacion {
    private String nivelEducativo;
    private String paralelo;
    private String datosRepresentante;
    private String periodoAcademico;
    private List<String> asignaturas;

    // Constructor original - mantener compatibilidad
    public Estudiante(String cedula, String nombre, int edad, String genero,
                      String direccion, long telefono, String email,
                      String nivelEducativo, String paralelo, String datosRepresentante) {
        super(cedula, nombre, edad, genero, direccion, telefono, email);
        this.nivelEducativo = nivelEducativo;
        this.paralelo = paralelo;
        this.datosRepresentante = datosRepresentante;
        this.periodoAcademico = "2024-2025"; // Valor predeterminado
        this.asignaturas = new ArrayList<>();
    }

    // Constructor con periodo académico
    public Estudiante(String cedula, String nombre, int edad, String genero,
                      String direccion, long telefono, String email,
                      String nivelEducativo, String paralelo, String datosRepresentante,
                      String periodoAcademico) {
        super(cedula, nombre, edad, genero, direccion, telefono, email);
        this.nivelEducativo = nivelEducativo;
        this.paralelo = paralelo;
        this.datosRepresentante = datosRepresentante;
        this.periodoAcademico = periodoAcademico;
        this.asignaturas = new ArrayList<>();
    }

    public void addAsignatura(String asignatura) {
        this.asignaturas.add(asignatura);
    }

    @Override
    public String mostrarInformacion() {
        return "=== INFORMACIÓN DEL ESTUDIANTE ===\n" +
                "Cédula: " + this.cedula + "\n" +
                "Nombre: " + this.nombre + "\n" +
                "Edad: " + this.edad + " años\n" +
                "Género: " + this.genero + "\n" +
                "Nivel Educativo: " + this.nivelEducativo + "\n" +
                "Paralelo: " + this.paralelo + "\n" +
                "Periodo Académico: " + this.periodoAcademico + "\n" +
                "Representante: " + this.datosRepresentante + "\n" +
                "Asignaturas: " + this.asignaturas + "\n";
    }

    @Override
    public void mostrarInformacionCompleta(String cedula, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT u.cedula, u.nombre_completo, u.edad, u.genero, u.direccion, u.telefono, u.email, " +
                        "e.nivel_educativo, e.paralelo, e.representante, e.periodo_academico " +
                        "FROM Usuario u JOIN Estudiante e ON u.cedula = e.cedula WHERE u.cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps.executeQuery();

            if(rs.next()) {
                System.out.println("\n--- INFORMACION DEL ESTUDIANTE ---");
                System.out.println("Cedula: " + rs.getString("cedula"));
                System.out.println("Nombre: " + rs.getString("nombre_completo"));
                System.out.println("Edad: " + rs.getInt("edad"));
                System.out.println("Genero: " + rs.getString("genero"));
                System.out.println("Direccion: " + rs.getString("direccion"));
                System.out.println("Telefono: " + rs.getLong("telefono"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Nivel: " + rs.getString("nivel_educativo"));
                System.out.println("Paralelo: " + rs.getString("paralelo"));
                System.out.println("Periodo Academico: " + rs.getString("periodo_academico"));
                System.out.println("Representante: " + rs.getString("representante"));
            } else {
                System.out.println("No se encontro informacion del estudiante.");
            }
        } catch(SQLException ex) {
            System.out.println("ERROR al mostrar informacion: " + ex.getMessage());
        } finally {
            try {
                if(rs != null) rs.close();
                if(ps != null) ps.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Getters y Setters
    public String getNivelEducativo() {
        return nivelEducativo;
    }

    public void setNivelEducativo(String nivelEducativo) {
        this.nivelEducativo = nivelEducativo;
    }

    public String getParalelo() {
        return paralelo;
    }

    public void setParalelo(String paralelo) {
        this.paralelo = paralelo;
    }

    public String getDatosRepresentante() {
        return datosRepresentante;
    }

    public void setDatosRepresentante(String datosRepresentante) {
        this.datosRepresentante = datosRepresentante;
    }

    public String getRepresentante() {
        return datosRepresentante;
    }

    public String getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(String periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public List<String> getAsignaturas() {
        return asignaturas;
    }
}