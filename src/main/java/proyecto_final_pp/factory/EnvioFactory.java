package proyecto_final_pp.factory;

import proyecto_final_pp.model.Envio;
import proyecto_final_pp.model.Usuario;
import proyecto_final_pp.model.Direccion;
import proyecto_final_pp.builder.EnvioBuilder;

import java.util.Arrays;
import java.util.List;

public class EnvioFactory {

    // Métodos corregidos que usan objetos Direccion
    public static Envio crearEnvioExpress(Usuario usuario, Direccion origen, Direccion destino,
                                          double peso, double volumen) {
        return new EnvioBuilder()
                .conUsuario(usuario)
                .conDireccionOrigen(origen)
                .conDireccionDestino(destino)
                .conPeso(peso)
                .conVolumen(volumen)
                .conTipoEnvio("EXPRESS")
                .agregarServicio("SEGURO")
                .build();
    }

    public static Envio crearEnvioEstandar(Usuario usuario, Direccion origen, Direccion destino,
                                           double peso, double volumen) {
        return new EnvioBuilder()
                .conUsuario(usuario)
                .conDireccionOrigen(origen)
                .conDireccionDestino(destino)
                .conPeso(peso)
                .conVolumen(volumen)
                .conTipoEnvio("ESTANDAR")
                .build();
    }

    public static Envio crearEnvioFragil(Usuario usuario, Direccion origen, Direccion destino,
                                         double peso, double volumen) {
        return new EnvioBuilder()
                .conUsuario(usuario)
                .conDireccionOrigen(origen)
                .conDireccionDestino(destino)
                .conPeso(peso)
                .conVolumen(volumen)
                .conTipoEnvio("ESTANDAR")
                .agregarServicio("FRAGIL")
                .agregarServicio("EMBALAJE_ESPECIAL")
                .build();
    }

    public static Envio crearEnvioPrioritario(Usuario usuario, Direccion origen, Direccion destino,
                                              double peso, double volumen) {
        return new EnvioBuilder()
                .conUsuario(usuario)
                .conDireccionOrigen(origen)
                .conDireccionDestino(destino)
                .conPeso(peso)
                .conVolumen(volumen)
                .conTipoEnvio("PRIORITARIO")
                .build();
    }

    public static Envio crearEnvioConSeguro(Usuario usuario, Direccion origen, Direccion destino,
                                            double peso, double volumen) {
        return new EnvioBuilder()
                .conUsuario(usuario)
                .conDireccionOrigen(origen)
                .conDireccionDestino(destino)
                .conPeso(peso)
                .conVolumen(volumen)
                .conTipoEnvio("ESTANDAR")
                .agregarServicio("SEGURO")
                .agregarServicio("FIRMA_REQUERIDA")
                .build();
    }

    // Método principal corregido
    public static Envio crearEnvioPorTipo(String tipo, Usuario usuario, Direccion origen,
                                          Direccion destino, double peso, double volumen) {
        switch (tipo.toUpperCase()) {
            case "EXPRESS":
                return crearEnvioExpress(usuario, origen, destino, peso, volumen);
            case "FRAGIL":
                return crearEnvioFragil(usuario, origen, destino, peso, volumen);
            case "PRIORITARIO":
                return crearEnvioPrioritario(usuario, origen, destino, peso, volumen);
            case "CON_SEGURO":
                return crearEnvioConSeguro(usuario, origen, destino, peso, volumen);
            case "ESTANDAR":
            default:
                return crearEnvioEstandar(usuario, origen, destino, peso, volumen);
        }
    }

    // Métodos de utilidad para crear envíos con valores por defecto
    public static Envio crearEnvioRapido(Usuario usuario, Direccion origen, Direccion destino) {
        return crearEnvioExpress(usuario, origen, destino, 1.0, 0.1);
    }

    public static Envio crearEnvioLiviano(Usuario usuario, Direccion origen, Direccion destino) {
        return crearEnvioEstandar(usuario, origen, destino, 0.5, 0.05);
    }

    public static Envio crearEnvioGrande(Usuario usuario, Direccion origen, Direccion destino) {
        return crearEnvioEstandar(usuario, origen, destino, 10.0, 2.0);
    }

    /**
     * Obtiene la descripción del tipo de envío
     */
    public static String obtenerDescripcionTipo(String tipo) {
        switch (tipo.toUpperCase()) {
            case "EXPRESS":
                return "Envío Express - Entrega en 24 horas con seguro incluido";
            case "FRAGIL":
                return "Envío Frágil - Manejo especial y embalaje reforzado";
            case "PRIORITARIO":
                return "Envío Prioritario - Entrega en 48 horas";
            case "CON_SEGURO":
                return "Envío con Seguro - Protección total y firma requerida";
            case "ESTANDAR":
                return "Envío Estándar - Entrega en 3-5 días hábiles";
            default:
                return "Tipo de envío no reconocido";
        }
    }

    /**
     * Valida si un tipo de envío es válido
     */
    public static boolean esTipoValido(String tipo) {
        if (tipo == null) return false;
        String tipoUpper = tipo.toUpperCase();
        return tipoUpper.equals("EXPRESS") ||
                tipoUpper.equals("FRAGIL") ||
                tipoUpper.equals("PRIORITARIO") ||
                tipoUpper.equals("CON_SEGURO") ||
                tipoUpper.equals("ESTANDAR");
    }

    /**
     * Obtiene todos los tipos de envío disponibles
     */
    public static String[] obtenerTiposDisponibles() {
        return new String[]{"ESTANDAR", "EXPRESS", "PRIORITARIO", "FRAGIL", "CON_SEGURO"};
    }

    /**
     * Obtiene los servicios adicionales recomendados para cada tipo
     */
    public static List<String> obtenerServiciosRecomendados(String tipo) {
        switch (tipo.toUpperCase()) {
            case "EXPRESS":
                return Arrays.asList("SEGURO", "FIRMA_REQUERIDA");
            case "FRAGIL":
                return Arrays.asList("FRAGIL", "EMBALAJE_ESPECIAL", "SEGURO");
            case "CON_SEGURO":
                return Arrays.asList("SEGURO", "FIRMA_REQUERIDA", "EMBALAJE_ESPECIAL");
            case "PRIORITARIO":
                return Arrays.asList("FIRMA_REQUERIDA");
            default:
                return Arrays.asList();
        }
    }

    /**
     * Calcula el tiempo estimado de entrega por tipo
     */
    public static String obtenerTiempoEstimadoEntrega(String tipo) {
        switch (tipo.toUpperCase()) {
            case "EXPRESS":
                return "24 horas";
            case "PRIORITARIO":
                return "48 horas";
            case "ESTANDAR":
                return "3-5 días hábiles";
            case "FRAGIL":
            case "CON_SEGURO":
                return "2-4 días hábiles";
            default:
                return "No especificado";
        }
    }
}