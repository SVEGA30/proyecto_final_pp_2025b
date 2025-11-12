package proyecto_final_pp.model;

public class EstadoSolicitado implements EstadoEnvio {
    @Override
    public String getNombre() {
        return "SOLICITADO";
    }

    @Override
    public boolean puedeAsignar() {
        return true;
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
        return true;
    }

    @Override
    public void ejecutarAccion(Envio envio) {
        System.out.println("Envío " + envio.getIdEnvio() + " ha sido solicitado");
    }
}