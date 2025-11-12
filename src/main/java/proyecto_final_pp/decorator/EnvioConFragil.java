package proyecto_final_pp.decorator;

import proyecto_final_pp.model.dto.DireccionDTO;

public class EnvioConFragil extends ServicioAdicionalDecorator {

    public EnvioConFragil(EnvioConServicioAdicional envioDecorado) {
        super(envioDecorado);
    }

    @Override
    public double calcularCosto(DireccionDTO origen, DireccionDTO destino, double peso, double volumen, String tipoEnvio) {
        double costoBase = super.calcularCosto(origen, destino, peso, volumen, tipoEnvio);
        return costoBase + getCostoAdicional();
    }

    @Override
    public String getDescripcionServicio() {
        return "Manejo de artículo frágil";
    }

    @Override
    public double getCostoAdicional() {
        return 2000;
    }
}