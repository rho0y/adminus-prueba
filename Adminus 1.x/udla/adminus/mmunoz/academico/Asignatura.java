package udla.adminus.mmunoz.academico;

import java.util.ArrayList;
import java.util.List;

public class Asignatura {
    private String nombre;
    private String codigo;
    private int horasSemanales;
    private Docente docente;
    private Curso curso;
    private List<Estudiante> estudiantesInscritos;

    public Asignatura(String nombre, String codigo, int horasSemanales) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.horasSemanales = horasSemanales;
        this.estudiantesInscritos = new ArrayList<>();
    }

    public String mostrarInformacion() {
        String info = "=== INFORMACIÓN DE ASIGNATURA ===\n" +
                "Nombre: " + this.nombre + "\n" +
                "Código: " + this.codigo + "\n" +
                "Horas Semanales: " + this.horasSemanales + " horas\n";

        if(this.docente != null) {
            info += "Docente: " + this.docente.getNombre() + "\n";
        }

        if(this.curso != null) {
            info += "Curso: " + this.curso.getNivel() + " " + this.curso.getParalelo() + "\n";
        }

        info += "Estudiantes Inscritos: " + this.estudiantesInscritos.size() + "\n";

        return info;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setHorasSemanales(int horasSemanales) {
        this.horasSemanales = horasSemanales;
    }

    public void setDocente(Docente docente) {
        this.docente = docente;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public void addEstudiante(Estudiante estudiante) {
        this.estudiantesInscritos.add(estudiante);
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getHorasSemanales() {
        return horasSemanales;
    }

    public Docente getDocente() {
        return docente;
    }

    public Curso getCurso() {
        return curso;
    }

    public List<Estudiante> getEstudiantesInscritos() {
        return estudiantesInscritos;
    }
}