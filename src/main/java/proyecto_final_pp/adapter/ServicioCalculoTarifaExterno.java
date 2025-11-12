package proyecto_final_pp.adapter;

// Simula un servicio externo de cálculo de tarifas
public class ServicioCalculoTarifaExterno {

    // Este servicio tiene una interfaz incompatible con nuestra CalculadoraTarifaStrategy
    public double calcularTarifaAPI(String origen, String destino, double peso, double volumen, String tipoEnvio, String[] serviciosAdicionales) {
        // Simula una llamada a una API externa
        // Lógica interna de la API externa
        double tarifa = 4000; // Costo base de la API externa
        tarifa += peso * 800; // $800 por kg
        tarifa += volumen * 400; // $400 por m³

        // Aplicar recargos por servicios (simulación)
        for (String servicio : serviciosAdicionales) {
            switch (servicio.toUpperCase()) {
                case "SEGURO":
                    tarifa += 2500;
                    break;
                case "FRAGIL":
                    tarifa += 1800;
                    break;
                case "FIRMA_REQUERIDA":
                    tarifa += 900;
                    break;
                case "EMBALAJE_ESPECIAL":
                    tarifa += 1300;
                    break;
            }
        }

        // Recargo por tipo de envío
        switch (tipoEnvio.toUpperCase()) {
            case "EXPRESS":
                tarifa *= 1.6;
                break;
            case "PRIORITARIO":
                tarifa *= 1.25;
                break;
            // ESTANDAR no recargo
        }

        return tarifa;
    }
}