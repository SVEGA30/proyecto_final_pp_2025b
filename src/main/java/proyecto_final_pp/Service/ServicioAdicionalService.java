package proyecto_final_pp.Service;

import proyecto_final_pp.model.dto.ServicioAdicionalDTO;
import proyecto_final_pp.model.dto.EnvioConServiciosDTO;
import proyecto_final_pp.factory.ServicioAdicionalFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ServicioAdicionalService {

    public List<ServicioAdicionalDTO> obtenerServiciosDisponibles() {
        return ServicioAdicionalFactory.obtenerTodosLosServicios();
    }

    public ServicioAdicionalDTO obtenerServicioPorTipo(String tipoServicio) {
        return ServicioAdicionalFactory.obtenerServicioPorTipo(tipoServicio);
    }

    public EnvioConServiciosDTO aplicarServiciosAEnvio(EnvioConServiciosDTO envio, List<String> tiposServicios) {
        if (tiposServicios != null) {
            for (String tipo : tiposServicios) {
                ServicioAdicionalDTO servicio = obtenerServicioPorTipo(tipo);
                if (servicio != null) {
                    envio.agregarServicio(servicio);
                }
            }
        }
        return envio;
    }

    public double calcularCostoServicios(List<String> tiposServicios) {
        if (tiposServicios == null) return 0.0;

        return tiposServicios.stream()
                .map(this::obtenerServicioPorTipo)
                .filter(servicio -> servicio != null)
                .mapToDouble(ServicioAdicionalDTO::getCostoAdicional)
                .sum();
    }

    public List<ServicioAdicionalDTO> convertirListaTiposAServicios(List<String> tiposServicios) {
        if (tiposServicios == null) return List.of();

        return tiposServicios.stream()
                .map(this::obtenerServicioPorTipo)
                .filter(servicio -> servicio != null)
                .collect(Collectors.toList());
    }
}