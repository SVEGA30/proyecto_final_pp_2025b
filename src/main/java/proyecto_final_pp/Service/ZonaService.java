package proyecto_final_pp.Service;

import proyecto_final_pp.model.dto.ZonaDTO;
import proyecto_final_pp.util.GestorDatos;

import java.util.List;

public class ZonaService {
    private GestorDatos gestorDatos;

    public ZonaService() {
        this.gestorDatos = GestorDatos.getInstance();
    }

    public List<ZonaDTO> obtenerTodasZonas() {
        return gestorDatos.getAllZonas();
    }

    public ZonaDTO buscarZonaPorNombre(String nombreZona) {
        List<ZonaDTO> zonas = obtenerTodasZonas();
        return zonas.stream()
                .filter(z -> z.getNombre().equalsIgnoreCase(nombreZona))
                .findFirst()
                .orElse(null);
    }
}