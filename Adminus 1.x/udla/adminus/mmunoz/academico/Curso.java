package udla.adminus.mmunoz.academico;

import java.util.ArrayList;
import java.util.List;

public class Curso {
    private String nivel;
    private String paralelo;
    private String periodoAcademico;
    private Docente docenteTutor;
    private List<Estudiante> estudiantes;
    private List<String> asignaturas;

    public Curso(String nivel, String paralelo, String periodoAcademico) {
        this.nivel = nivel;
        this.paralelo = paralelo;
        this.periodoAcademico = periodoAcademico;
        this.estudiantes = new ArrayList<>();
        this.asignaturas = new ArrayList<>();
    }

    public void setDocenteTutor(Docente docente) {
        this.docenteTutor = docente;
    }

    public void addEstudiante(Estudiante estudiante) {
        this.estudiantes.add(estudiante);
    }

    public void addAsignatura(String asignatura) {
        this.asignaturas.add(asignatura);
    }

    public String mostrarInformacion() {
        String info = "=== INFORMACIÓN DEL CURSO ===\n" +
                "Nivel: " + this.nivel + "\n" +
                "Paralelo: " + this.paralelo + "\n" +
                "Periodo Académico: " + this.periodoAcademico + "\n";

        if(this.docenteTutor != null) {
            info += "Docente Tutor: " + this.docenteTutor.getNombre() + "\n";
        }

        info += "Número de Estudiantes: " + this.estudiantes.size() + "\n";
        info += "Asignaturas:\n";

        for(String asig : this.asignaturas) {
            info += "  - " + asig + "\n";
        }

        return info;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public void setParalelo(String paralelo) {
        this.paralelo = paralelo;
    }

    public void setPeriodoAcademico(String periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public String getNivel() {
        return nivel;
    }

    public String getParalelo() {
        return paralelo;
    }

    public String getPeriodoAcademico() {
        return periodoAcademico;
    }

    public Docente getDocenteTutor() {
        return docenteTutor;
    }

    public List<Estudiante> getEstudiantes() {
        return estudiantes;
    }

    public List<String> getAsignaturas() {
        return asignaturas;
    }
}