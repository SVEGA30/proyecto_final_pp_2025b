package proyecto_final_pp.decorator;

import proyecto_final_pp.model.dto.DireccionDTO;

public class EnvioBase implements EnvioConServicioAdicional {

    public EnvioBase() {
        // Constructor vacío (opcional, pero explícito)
    }

    @Override
    public double calcularCosto(DireccionDTO origen, DireccionDTO destino, double peso, double volumen, String tipoEnvio) {
        double costo = 5000.0; // Costo base
        costo += peso * 1000.0; // $1000 por kg
        costo += volumen * 500.0; // $500 por m³

        if (tipoEnvio != null) {
            switch (tipoEnvio.toUpperCase()) {
                case "EXPRESS":
                    costo *= 1.5;
                    break;
                case "PRIORITARIO":
                    costo *= 1.2;
                    break;
                // ESTANDAR: sin recargo
            }
        }

        return costo;
    }

    @Override
    public String toString() {
        return "EnvioBase";
    }
}