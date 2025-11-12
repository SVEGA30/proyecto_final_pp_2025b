package proyecto_final_pp.decorator;

import proyecto_final_pp.model.dto.DireccionDTO;

public class EnvioConEmbalajeEspecial extends ServicioAdicionalDecorator {

    private static final double COSTO_EMBALAJE = 1500.0;

    public EnvioConEmbalajeEspecial(EnvioConServicioAdicional envioDecorado) {
        super(envioDecorado);
    }

    @Override
    public double calcularCosto(DireccionDTO origen, DireccionDTO destino, double peso, double volumen, String tipoEnvio) {
        double costoBase = super.calcularCosto(origen, destino, peso, volumen, tipoEnvio);
        return costoBase + getCostoAdicional();
    }

    @Override
    public String getDescripcionServicio() {
        return "Embalaje especial para protección adicional";
    }

    @Override
    public double getCostoAdicional() {
        return COSTO_EMBALAJE;
    }
}