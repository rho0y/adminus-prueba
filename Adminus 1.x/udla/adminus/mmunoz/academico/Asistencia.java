package udla.adminus.mmunoz.academico;

import java.time.LocalDate;

public class Asistencia {
    private Estudiante estudiante;
    private Curso curso;
    private LocalDate fecha;
    private String estado;
    private String observacion;

    public Asistencia(Estudiante estudiante, Curso curso, LocalDate fecha, String estado) {
        this.estudiante = estudiante;
        this.curso = curso;
        this.fecha = fecha;
        this.estado = estado;
        this.observacion = "";
    }

    public String mostrarInformacion() {
        String info = "=== REGISTRO DE ASISTENCIA ===\n" +
                "Estudiante: " + this.estudiante.getNombre() + "\n" +
                "Cédula: " + this.estudiante.getCedula() + "\n" +
                "Curso: " + this.curso.getNivel() + " " + this.curso.getParalelo() + "\n" +
                "Fecha: " + this.fecha + "\n" +
                "Estado: " + this.estado + "\n";

        if(!this.observacion.isEmpty()) {
            info += "Observación: " + this.observacion + "\n";
        }

        return info;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Curso getCurso() {
        return curso;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public String getObservacion() {
        return observacion;
    }
}