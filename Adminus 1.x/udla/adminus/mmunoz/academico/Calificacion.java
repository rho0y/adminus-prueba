package udla.adminus.mmunoz.academico;

public class Calificacion {
    private Estudiante estudiante;
    private String asignatura;
    private String periodoAcademico;
    private double nota;
    private String parcial;
    private String observacion;

    public Calificacion(Estudiante estudiante, String asignatura, String periodoAcademico,
                        double nota, String parcial) {
        this.estudiante = estudiante;
        this.asignatura = asignatura;
        this.periodoAcademico = periodoAcademico;
        this.nota = nota;
        this.parcial = parcial;
        this.observacion = "";
    }

    public String mostrarInformacion() {
        String info = "=== REGISTRO DE CALIFICACIÓN ===\n" +
                "Estudiante: " + this.estudiante.getNombre() + "\n" +
                "Cédula: " + this.estudiante.getCedula() + "\n" +
                "Asignatura: " + this.asignatura + "\n" +
                "Periodo Académico: " + this.periodoAcademico + "\n" +
                "Parcial: " + this.parcial + "\n" +
                "Nota: " + this.nota + "/10\n";

        if(!this.observacion.isEmpty()) {
            info += "Observación: " + this.observacion + "\n";
        }

        return info;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public void setPeriodoAcademico(String periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public void setParcial(String parcial) {
        this.parcial = parcial;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public String getPeriodoAcademico() {
        return periodoAcademico;
    }

    public double getNota() {
        return nota;
    }

    public String getParcial() {
        return parcial;
    }

    public String getObservacion() {
        return observacion;
    }
}