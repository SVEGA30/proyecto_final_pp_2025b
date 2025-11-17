package proyecto_final_pp.factory;

import proyecto_final_pp.model.Envio;
import proyecto_final_pp.model.Usuario;
import proyecto_final_pp.model.Direccion;
import proyecto_final_pp.builder.EnvioBuilder;
import java.util.Arrays;
import java.util.List;

public class EnvioFactory {

    public static Envio crearEnvioExpress(Usuario u, Direccion o, Direccion d, double p, double v) {
        return new EnvioBuilder().conUsuario(u).conDireccionOrigen(o).conDireccionDestino(d)
                .conPeso(p).conVolumen(v).conTipoEnvio("EXPRESS").agregarServicio("SEGURO").build();
    }

    public static Envio crearEnvioEstandar(Usuario u, Direccion o, Direccion d, double p, double v) {
        return new EnvioBuilder().conUsuario(u).conDireccionOrigen(o).conDireccionDestino(d)
                .conPeso(p).conVolumen(v).conTipoEnvio("ESTANDAR").build();
    }

    public static Envio crearEnvioFragil(Usuario u, Direccion o, Direccion d, double p, double v) {
        return new EnvioBuilder().conUsuario(u).conDireccionOrigen(o).conDireccionDestino(d)
                .conPeso(p).conVolumen(v).conTipoEnvio("ESTANDAR")
                .agregarServicio("FRAGIL").agregarServicio("EMBALAJE_ESPECIAL").build();
    }

    public static Envio crearEnvioPrioritario(Usuario u, Direccion o, Direccion d, double p, double v) {
        return new EnvioBuilder().conUsuario(u).conDireccionOrigen(o).conDireccionDestino(d)
                .conPeso(p).conVolumen(v).conTipoEnvio("PRIORITARIO").build();
    }

    public static Envio crearEnvioConSeguro(Usuario u, Direccion o, Direccion d, double p, double v) {
        return new EnvioBuilder().conUsuario(u).conDireccionOrigen(o).conDireccionDestino(d)
                .conPeso(p).conVolumen(v).conTipoEnvio("ESTANDAR")
                .agregarServicio("SEGURO").agregarServicio("FIRMA_REQUERIDA").build();
    }

    public static Envio crearEnvioPorTipo(String t, Usuario u, Direccion o, Direccion d, double p, double v) {
        switch (t.toUpperCase()) {
            case "EXPRESS": return crearEnvioExpress(u, o, d, p, v);
            case "FRAGIL": return crearEnvioFragil(u, o, d, p, v);
            case "PRIORITARIO": return crearEnvioPrioritario(u, o, d, p, v);
            case "CON_SEGURO": return crearEnvioConSeguro(u, o, d, p, v);
            default: return crearEnvioEstandar(u, o, d, p, v);
        }
    }

    // Métodos rápidos para casos comunes
    public static Envio crearEnvioRapido(Usuario u, Direccion o, Direccion d) {
        return crearEnvioExpress(u, o, d, 1.0, 0.1);
    }

    public static Envio crearEnvioLiviano(Usuario u, Direccion o, Direccion d) {
        return crearEnvioEstandar(u, o, d, 0.5, 0.05);
    }

    public static Envio crearEnvioGrande(Usuario u, Direccion o, Direccion d) {
        return crearEnvioEstandar(u, o, d, 10.0, 2.0);
    }

    public static String obtenerDescripcionTipo(String t) {
        switch (t.toUpperCase()) {
            case "EXPRESS": return "Express - 24h con seguro";
            case "FRAGIL": return "Frágil - Manejo especial";
            case "PRIORITARIO": return "Prioritario - 48h";
            case "CON_SEGURO": return "Con Seguro - Protección total";
            case "ESTANDAR": return "Estándar - 3-5 días";
            default: return "Tipo no reconocido";
        }
    }

    public static boolean esTipoValido(String t) {
        if (t == null) return false;
        String upper = t.toUpperCase();
        return upper.equals("EXPRESS") || upper.equals("FRAGIL") || upper.equals("PRIORITARIO")
                || upper.equals("CON_SEGURO") || upper.equals("ESTANDAR");
    }

    public static String[] obtenerTiposDisponibles() {
        return new String[]{"ESTANDAR", "EXPRESS", "PRIORITARIO", "FRAGIL", "CON_SEGURO"};
    }

    public static List<String> obtenerServiciosRecomendados(String t) {
        switch (t.toUpperCase()) {
            case "EXPRESS": return Arrays.asList("SEGURO", "FIRMA_REQUERIDA");
            case "FRAGIL": return Arrays.asList("FRAGIL", "EMBALAJE_ESPECIAL", "SEGURO");
            case "CON_SEGURO": return Arrays.asList("SEGURO", "FIRMA_REQUERIDA", "EMBALAJE_ESPECIAL");
            case "PRIORITARIO": return Arrays.asList("FIRMA_REQUERIDA");
            default: return Arrays.asList();
        }
    }

    public static String obtenerTiempoEstimadoEntrega(String t) {
        switch (t.toUpperCase()) {
            case "EXPRESS": return "24 horas";
            case "PRIORITARIO": return "48 horas";
            case "ESTANDAR": return "3-5 días";
            case "FRAGIL": case "CON_SEGURO": return "2-4 días";
            default: return "No especificado";
        }
    }
}