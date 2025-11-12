package proyecto_final_pp.decorator;

import proyecto_final_pp.model.dto.DireccionDTO;

public class EnvioConSeguro extends ServicioAdicionalDecorator {

    public EnvioConSeguro(EnvioConServicioAdicional envioDecorado) {
        super(envioDecorado);
    }

    @Override
    public double calcularCosto(DireccionDTO origen, DireccionDTO destino, double peso, double volumen, String tipoEnvio) {
        double costoBase = super.calcularCosto(origen, destino, peso, volumen, tipoEnvio);
        return costoBase + 3000; // Costo adicional por seguro
    }

    @Override
    public String getDescripcionServicio() {
        return "Envío con seguro incluido";
    }

    @Override
    public double getCostoAdicional() {
        return 3000;
    }
}