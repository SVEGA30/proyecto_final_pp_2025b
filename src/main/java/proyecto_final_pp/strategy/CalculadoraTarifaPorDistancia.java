package proyecto_final_pp.strategy;

import proyecto_final_pp.model.dto.DireccionDTO;
import java.util.List;

// Estrategia de cálculo de tarifa basada en distancia (simulada)
public class CalculadoraTarifaPorDistancia implements CalculadoraTarifaStrategy {

    @Override
    public double calcularCosto(DireccionDTO origen, DireccionDTO destino, double peso, double volumen, List<String> serviciosExtras, String tipoEnvio) {
        double costoBase = 5000; // Costo base

        // Simular cálculo de distancia (usando coordenadas o lógica basada en zonas/calles)
        double distanciaKm = calcularDistanciaAproximada(origen, destino);

        // Costo basado en distancia
        double costoPorDistancia = distanciaKm * 2000; // $2000 por km (ejemplo)
        costoBase += costoPorDistancia;

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

    private double calcularDistanciaAproximada(DireccionDTO origen, DireccionDTO destino) {
        // Validar que las direcciones y zonas no sean nulas
        if (origen == null || destino == null ||
                origen.getZona() == null || destino.getZona() == null) {
            return 10.0; // Distancia por defecto
        }

        // Lógica ficticia para calcular distancia
        // En un sistema real, se usaría una API de geolocalización
        // Por ahora, simulamos una distancia basada en zonas o nombres
        if (origen.getZona().equalsIgnoreCase(destino.getZona())) {
            return 5.0; // 5 km si están en la misma zona
        } else {
            return 10.0; // 10 km si están en zonas diferentes
        }
    }
}