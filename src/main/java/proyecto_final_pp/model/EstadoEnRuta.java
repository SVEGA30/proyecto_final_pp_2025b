package proyecto_final_pp.model;

public class EstadoEnRuta implements EstadoEnvio {
    @Override
    public String getNombre() {
        return "EN_RUTA";
    }

    @Override
    public boolean puedeAsignar() {
        return false;
    }

    @Override
    public boolean puedeIniciarRuta() {
        return false;
    }

    @Override
    public boolean puedeEntregar() {
        return true;
    }

    @Override
    public boolean puedeReportarIncidencia() {
        return true;
    }

    @Override
    public boolean puedeCancelar() {
        return false;
    }

    @Override
    public void ejecutarAccion(Envio envio) {
        System.out.println("Envío " + envio.getIdEnvio() + " en ruta de entrega");
    }
}