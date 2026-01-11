package udla.adminus.mmunoz.usuario;

import udla.adminus.mmunoz.Informacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Administrativo extends Empleado implements Informacion {
    private String cargo;
    private String area;

    public Administrativo(String cedula, String nombre, int edad, String genero,
                         String direccion, long telefono, String email,
                         double sueldoMensual, String jornadaLaboral, int horasTrabajadas,
                         String cargo, String area) {
        super(cedula, nombre, edad, genero, direccion, telefono, email,
              sueldoMensual, jornadaLaboral, horasTrabajadas);
        this.cargo = cargo;
        this.area = area;
    }

    @Override
    public String mostrarInformacion() {
        return super.mostrarInformacion() +
                "Cargo: " + this.cargo + "\n" +
                "Área: " + this.area + "\n";
    }

    @Override
    public void mostrarInformacionCompleta(String cedula, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT u.nombre_completo, u.edad, u.genero, u.direccion, u.telefono, u.email, " +
                        "a.cargo, a.area, a.jornada_laboral, a.horas_trabajadas, a.sueldo " +
                        "FROM Usuario u " +
                        "INNER JOIN Administrativo a ON u.cedula = a.cedula " +
                        "WHERE u.cedula = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps.executeQuery();

            if(rs.next()) {
                System.out.println("\n========================================");
                System.out.println("   MI INFORMACIÓN - PERSONAL ADMINISTRATIVO");
                System.out.println("========================================");
                System.out.println("Cédula: " + cedula);
                System.out.println("Nombre: " + rs.getString("nombre_completo"));
                System.out.println("Edad: " + rs.getInt("edad"));
                System.out.println("Género: " + rs.getString("genero"));
                System.out.println("Dirección: " + rs.getString("direccion"));
                System.out.println("Teléfono: " + rs.getLong("telefono"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("\n--- DATOS LABORALES ---");
                System.out.println("Cargo: " + rs.getString("cargo"));
                System.out.println("Área: " + rs.getString("area"));
                System.out.println("Jornada Laboral: " + rs.getString("jornada_laboral"));
                System.out.println("Horas Trabajadas: " + rs.getInt("horas_trabajadas") + " horas");
                System.out.println("Sueldo: $" + rs.getDouble("sueldo"));
                System.out.println("========================================\n");
            } else {
                System.out.println("No se encontró información del administrativo.");
            }

        } catch(SQLException ex) {
            System.out.println("ERROR al mostrar información: " + ex.getMessage());
        } finally {
            try {
                if(rs != null) rs.close();
                if(ps != null) ps.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public double calcularNomina() {
        return getSueldoMensual();
    }

    // ==================== GETTERS Y SETTERS ====================

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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