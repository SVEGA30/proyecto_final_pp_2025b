package proyecto_final_pp.strategy;

import proyecto_final_pp.model.dto.DireccionDTO;
import java.util.List;

// Interfaz base para las estrategias de cálculo de tarifa
public interface CalculadoraTarifaStrategy {
    double calcularCosto(DireccionDTO origen, DireccionDTO destino, double peso, double volumen, List<String> serviciosExtras, String tipoEnvio);
}