package proyecto_final_pp.strategy;

import proyecto_final_pp.decorator.*;
import proyecto_final_pp.model.dto.DireccionDTO;
import java.util.List;

// Estrategia de cálculo de tarifa básica que ahora usa Decorator para servicios extras
public class CalculadoraTarifaBasica implements CalculadoraTarifaStrategy {

    @Override
    public double calcularCosto(DireccionDTO origen, DireccionDTO destino, double peso, double volumen, List<String> serviciosExtras, String tipoEnvio) {
        // Creamos el componente base SIN pasar ningún costo
        EnvioConServicioAdicional envioCalculable = new EnvioBase();

        // Aplicamos los decoradores según los servicios extras
        if (serviciosExtras != null) {
            for (String servicio : serviciosExtras) {
                if (servicio == null) continue;
                switch (servicio.toUpperCase()) {
                    case "SEGURO":
                        envioCalculable = new EnvioConSeguro(envioCalculable);
                        break;
                    case "FRAGIL":
                        envioCalculable = new EnvioConFragil(envioCalculable);
                        break;
                    case "FIRMA_REQUERIDA":
                        envioCalculable = new EnvioConFirmaRequerida(envioCalculable);
                        break;
                    case "EMBALAJE_ESPECIAL":
                        envioCalculable = new EnvioConEmbalajeEspecial(envioCalculable);
                        break;
                }
            }
        }

        // Todo el cálculo (base + tipo de envío + decoradores) ocurre aquí
        return envioCalculable.calcularCosto(origen, destino, peso, volumen, tipoEnvio);
    }
}