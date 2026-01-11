package udla.adminus.mmunoz.usuario;

public abstract class Empleado extends Persona {
    protected double sueldoMensual;
    protected String jornadaLaboral;
    protected int horasTrabajadas;

    public Empleado(String cedula, String nombre, int edad, String genero,
                    String direccion, long telefono, String email,
                    double sueldoMensual, String jornadaLaboral, int horasTrabajadas) {
        super(cedula, nombre, edad, genero, direccion, telefono, email);
        this.sueldoMensual = sueldoMensual;
        this.jornadaLaboral = jornadaLaboral;
        this.horasTrabajadas = horasTrabajadas;
    }

    @Override
    public String mostrarInformacion() {
        return super.mostrarInformacion() +
                "Sueldo Mensual: $" + sueldoMensual + "\n" +
                "Jornada Laboral: " + jornadaLaboral + "\n" +
                "Horas Trabajadas: " + horasTrabajadas + "\n";
    }

    public abstract double calcularNomina();

    public void setSueldoMensual(double sueldoMensual) {
        this.sueldoMensual = sueldoMensual;
    }

    public void setJornadaLaboral(String jornadaLaboral) {
        this.jornadaLaboral = jornadaLaboral;
    }

    public void setHorasTrabajadas(int horasTrabajadas) {
        this.horasTrabajadas = horasTrabajadas;
    }

    public double getSueldoMensual() {
        return sueldoMensual;
    }

    public String getJornadaLaboral() {
        return jornadaLaboral;
    }

    public int getHorasTrabajadas() {
        return horasTrabajadas;
    }
}