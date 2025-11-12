package proyecto_final_pp.decorator;

import proyecto_final_pp.model.dto.DireccionDTO;

public interface EnvioConServicioAdicional {

    double calcularCosto(DireccionDTO origen, DireccionDTO destino, double peso, double volumen, String tipoEnvio);
}