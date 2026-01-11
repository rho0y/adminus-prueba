package udla.adminus.mmunoz.usuario;

import udla.adminus.mmunoz.Informacion;

import java.sql.Connection;

public class Persona implements Informacion {
    protected String cedula;
    protected String nombre;
    protected int edad;
    protected String genero;
    protected String direccion;
    protected long telefono;
    protected String email;

    public Persona(String cedula, String nombre, int edad, String genero,
                   String direccion, long telefono, String email) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.edad = edad;
        this.genero = genero;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
    }

    @Override
    public String mostrarInformacion() {
        return "\n=== INFORMACION PERSONAL ===" +
               "\nCedula: " + cedula +
               "\nNombre: " + nombre +
               "\nEdad: " + edad +
               "\nGenero: " + genero +
               "\nDireccion: " + direccion +
               "\nTelefono: " + telefono +
               "\nEmail: " + email + "\n";
    }

    @Override
    public void mostrarInformacionCompleta(String cedula, Connection conn) {
        // Implementación base - puede ser sobrescrita por las subclases
        System.out.println(mostrarInformacion());
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setTelefono(long telefono) {
        this.telefono = telefono;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    public String getGenero() {
        return genero;
    }

    public String getDireccion() {
        return direccion;
    }

    public long getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }
}