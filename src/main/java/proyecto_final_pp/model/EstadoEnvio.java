package proyecto_final_pp.model;

public interface EstadoEnvio {
    String getNombre();
    boolean puedeAsignar();
    boolean puedeIniciarRuta();
    boolean puedeEntregar();
    boolean puedeReportarIncidencia();
    boolean puedeCancelar();
    void ejecutarAccion(Envio envio);
}