package proyecto_final_pp.adapter;

import proyecto_final_pp.strategy.CalculadoraTarifaStrategy;
import proyecto_final_pp.model.dto.DireccionDTO;
import java.util.List;

// Adapter que implementa la interfaz CalculadoraTarifaStrategy
// para adaptar ServicioCalculoTarifaExterno
public class CalculadoraTarifaExternaAdapter implements CalculadoraTarifaStrategy {

    private final ServicioCalculoTarifaExterno servicioExterno;

    public CalculadoraTarifaExternaAdapter() {
        this.servicioExterno = new ServicioCalculoTarifaExterno();
    }

    // Implementa el método de la interfaz Strategy con DireccionDTO
    @Override
    public double calcularCosto(
            DireccionDTO origen,
            DireccionDTO destino,
            double peso,
            double volumen,
            List<String> serviciosExtras,
            String tipoEnvio) {

        // Convierte los parámetros de nuestra interfaz a los del servicio externo
        String origenStr = origen.getCalle() + ", " + origen.getCiudad();
        String destinoStr = destino.getCalle() + ", " + destino.getCiudad();
        String[] serviciosArray = (serviciosExtras != null)
                ? serviciosExtras.toArray(new String[0])
                : new String[0];

        // Llama al método del servicio externo adaptado
        return servicioExterno.calcularTarifaAPI(
                origenStr,
                destinoStr,
                peso,
                volumen,
                tipoEnvio,
                serviciosArray
        );
    }
}