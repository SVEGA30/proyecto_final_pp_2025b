package proyecto_final_pp.model;

public class EstadoIncidencia implements EstadoEnvio {
    @Override
    public String getNombre() {
        return "INCIDENCIA";
    }

    @Override
    public boolean puedeAsignar() {
        return false;
    }

    @Override
    public boolean puedeIniciarRuta() {
        return true;
    }

    @Override
    public boolean puedeEntregar() {
        return false;
    }

    @Override
    public boolean puedeReportarIncidencia() {
        return false;
    }

    @Override
    public boolean puedeCancelar() {
        return true;
    }

    @Override
    public void ejecutarAccion(Envio envio) {
        System.out.println("Incidencia reportada para el envío " + envio.getIdEnvio());
    }
}