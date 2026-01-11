package udla.adminus.mmunoz;

import java.sql.Connection;

public interface Informacion {
    String mostrarInformacion();
    void mostrarInformacionCompleta(String cedula, Connection conn);
}