package proyecto_final_pp.decorator;

import proyecto_final_pp.model.dto.DireccionDTO;

public class EnvioConFirmaRequerida extends ServicioAdicionalDecorator {

    private static final double RECARGO_FIRMA = 1000.0;

    public EnvioConFirmaRequerida(EnvioConServicioAdicional envioDecorado) {
        super(envioDecorado);
    }

    @Override
    public double calcularCosto(DireccionDTO origen, DireccionDTO destino, double peso, double volumen, String tipoEnvio) {
        double costoBase = super.calcularCosto(origen, destino, peso, volumen, tipoEnvio);
        return costoBase + getCostoAdicional();
    }

    @Override
    public String getDescripcionServicio() {
        return "Firma requerida en entrega";
    }

    @Override
    public double getCostoAdicional() {
        return RECARGO_FIRMA;
    }
}