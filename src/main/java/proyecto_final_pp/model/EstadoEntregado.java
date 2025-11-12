package proyecto_final_pp.model;

public class EstadoEntregado implements EstadoEnvio {
    @Override
    public String getNombre() {
        return "ENTREGADO";
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
        return false;
    }

    @Override
    public boolean puedeReportarIncidencia() {
        return false;
    }

    @Override
    public boolean puedeCancelar() {
        return false;
    }

    @Override
    public void ejecutarAccion(Envio envio) {
        System.out.println("Envío " + envio.getIdEnvio() + " ha sido entregado exitosamente");
    }
}