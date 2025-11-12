package proyecto_final_pp.factory;

import proyecto_final_pp.model.dto.ServicioAdicionalDTO;
import java.util.Arrays;
import java.util.List;

public class ServicioAdicionalFactory {

    public static ServicioAdicionalDTO crearSeguro() {
        return new ServicioAdicionalDTO("SEGURO", "Protección contra pérdida o daño", 3000.0);
    }

    public static ServicioAdicionalDTO crearFragil() {
        return new ServicioAdicionalDTO("FRAGIL", "Manejo especial para artículos frágiles", 2000.0);
    }

    public static ServicioAdicionalDTO crearFirmaRequerida() {
        return new ServicioAdicionalDTO("FIRMA_REQUERIDA", "Entrega con firma del destinatario", 1000.0);
    }

    public static ServicioAdicionalDTO crearEmbalajeEspecial() {
        return new ServicioAdicionalDTO("EMBALAJE_ESPECIAL", "Embalaje reforzado y protector", 1500.0);
    }

    public static List<ServicioAdicionalDTO> obtenerTodosLosServicios() {
        return Arrays.asList(
                crearSeguro(),
                crearFragil(),
                crearFirmaRequerida(),
                crearEmbalajeEspecial()
        );
    }

    public static ServicioAdicionalDTO obtenerServicioPorTipo(String tipoServicio) {
        switch (tipoServicio.toUpperCase()) {
            case "SEGURO":
                return crearSeguro();
            case "FRAGIL":
                return crearFragil();
            case "FIRMA_REQUERIDA":
                return crearFirmaRequerida();
            case "EMBALAJE_ESPECIAL":
                return crearEmbalajeEspecial();
            default:
                return null;
        }
    }
}