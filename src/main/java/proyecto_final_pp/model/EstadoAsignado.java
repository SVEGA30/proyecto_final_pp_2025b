package proyecto_final_pp.model;

public class EstadoAsignado implements EstadoEnvio {
    @Override
    public String getNombre() {
        return "ASIGNADO";
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
        return true;
    }

    @Override
    public boolean puedeCancelar() {
        return true;
    }

    @Override
    public void ejecutarAccion(Envio envio) {
        System.out.println("Envío " + envio.getIdEnvio() + " asignado al repartidor: " + envio.getRepartidorAsignadoId());
    }
}