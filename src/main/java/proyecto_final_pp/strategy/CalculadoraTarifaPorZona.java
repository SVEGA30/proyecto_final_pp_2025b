package proyecto_final_pp.strategy;

import proyecto_final_pp.model.dto.DireccionDTO;
import java.util.List;

// Estrategia de cálculo de tarifa basada en zonas
public class CalculadoraTarifaPorZona implements CalculadoraTarifaStrategy {

    @Override
    public double calcularCosto(DireccionDTO origen, DireccionDTO destino, double peso, double volumen, List<String> serviciosExtras, String tipoEnvio) {
        double costoBase = 5000; // Costo base

        // Validar que las direcciones no sean nulas
        if (origen == null || destino == null) {
            return costoBase; // Retornar costo base si hay datos faltantes
        }

        // Obtener zonas de origen y destino
        String zonaOrigen = origen.getZona();
        String zonaDestino = destino.getZona();

        // Definir tarifas por zona (esto podría venir de una configuración o base de datos)
        double tarifaZonaOrigen = obtenerTarifaZona(zonaOrigen);
        double tarifaZonaDestino = obtenerTarifaZona(zonaDestino);

        // Calcular un factor basado en las zonas (ej: promedio, diferencia, etc.)
        double factorZona = (tarifaZonaOrigen + tarifaZonaDestino) / 2.0;

        // Aplicar factor de zona al costo base
        costoBase += factorZona;

        // Calcular costos adicionales por peso y volumen
        costoBase += peso * 1000; // $1000 por kg
        costoBase += volumen * 500; // $500 por m³

        // Agregar costos por servicios extras
        if (serviciosExtras != null) {
            for (String servicio : serviciosExtras) {
                switch (servicio.toUpperCase()) {
                    case "SEGURO":
                        costoBase += 3000;
                        break;
                    case "FRAGIL":
                        costoBase += 2000;
                        break;
                    case "FIRMA_REQUERIDA":
                        costoBase += 1000;
                        break;
                    case "EMBALAJE_ESPECIAL":
                        costoBase += 1500;
                        break;
                }
            }
        }

        // Ajustar costo según tipo de envío
        if (tipoEnvio != null) {
            switch (tipoEnvio.toUpperCase()) {
                case "EXPRESS":
                    costoBase *= 1.5; // Recargo del 50% para express
                    break;
                case "PRIORITARIO":
                    costoBase *= 1.2; // Recargo del 20% para prioritario
                    break;
                // ESTANDAR no tiene recargo
            }
        }

        return costoBase;
    }

    private double obtenerTarifaZona(String zona) {
        if (zona == null) {
            return 500; // Valor por defecto si la zona es nula
        }

        // Lógica para obtener la tarifa base asociada a una zona
        switch (zona.toUpperCase()) {
            case "CHAPINERO": return 500;
            case "USAQUÉN": return 600;
            case "USAQUEN": return 600; // Alternativa sin acento
            case "SUBA": return 400;
            case "TEUSAQUILLO": return 700;
            default: return 500; // Valor por defecto
        }
    }
}