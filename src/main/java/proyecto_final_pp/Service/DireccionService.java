package proyecto_final_pp.Service;

import proyecto_final_pp.model.dto.UsuarioDTO;
import proyecto_final_pp.model.dto.DireccionDTO;
import proyecto_final_pp.model.dto.ZonaDTO;
import proyecto_final_pp.util.GestorDatos;

import java.util.List;

public class DireccionService {
    private GestorDatos gestorDatos;

    public DireccionService() {
        this.gestorDatos = GestorDatos.getInstance();
    }

    public List<DireccionDTO> obtenerDireccionesFrecuentes(UsuarioDTO usuarioDTO) {
        if (usuarioDTO == null || usuarioDTO.getIdUsuario() == null) {
            return List.of();
        }
        return gestorDatos.getDireccionesUsuario(usuarioDTO.getIdUsuario());
    }

    public List<ZonaDTO> obtenerTodasZonas() {
        List<ZonaDTO> zonas = gestorDatos.getAllZonas();
        System.out.println("DireccionService: Obteniendo " + zonas.size() + " zonas");
        return zonas;
    }

    public boolean agregarDireccionFrecuente(UsuarioDTO usuarioDTO, DireccionDTO nuevaDireccionDTO) {
        if (usuarioDTO == null || nuevaDireccionDTO == null) {
            return false;
        }
        return gestorDatos.agregarDireccionFrecuente(usuarioDTO, nuevaDireccionDTO);
    }

    public boolean eliminarDireccionFrecuente(UsuarioDTO usuarioDTO, DireccionDTO direccionAEliminarDTO) {
        if (usuarioDTO == null || direccionAEliminarDTO == null) {
            return false;
        }
        return gestorDatos.eliminarDireccionFrecuente(usuarioDTO, direccionAEliminarDTO);
    }
}