package proyecto_final_pp.builder;

import proyecto_final_pp.model.Envio;
import proyecto_final_pp.model.Usuario;
import proyecto_final_pp.model.Direccion;
import proyecto_final_pp.model.EstadoSolicitado; // Estado inicial
import proyecto_final_pp.observer.UsuarioNotificacionObserver; // Importar el observador

import java.util.ArrayList;
import java.util.List;


public class EnvioBuilder {
    private String idEnvio;
    private Usuario usuario;
    private Direccion direccionOrigen;
    private Direccion direccionDestino;
    private double peso = 1.0;
    private double volumen = 0.1;
    private double costo = 0.0;
    private String tipoEnvio = "ESTANDAR"; // Valor por defecto
    private List<String> serviciosExtras = new ArrayList<>();

    public EnvioBuilder conUsuario(Usuario usuario) {
        this.usuario = usuario;
        return this;
    }

    public EnvioBuilder conDireccionOrigen(Direccion direccion) {
        this.direccionOrigen = direccion;
        return this;
    }

    public EnvioBuilder conDireccionDestino(Direccion direccion) {
        this.direccionDestino = direccion;
        return this;
    }

    public EnvioBuilder conPeso(double peso) {
        this.peso = peso;
        return this;
    }

    public EnvioBuilder conVolumen(double volumen) {
        this.volumen = volumen;
        return this;
    }

    public EnvioBuilder conTipoEnvio(String tipoEnvio) {
        this.tipoEnvio = tipoEnvio;
        return this;
    }

    public EnvioBuilder conCosto(double costo) {
        this.costo = costo;
        return this;
    }

    public EnvioBuilder agregarServicio(String servicio) {
        if (servicio != null && !this.serviciosExtras.contains(servicio)) {
            this.serviciosExtras.add(servicio);
        }
        return this;
    }

    public EnvioBuilder agregarServicios(List<String> servicios) {
        if (servicios != null) {
            this.serviciosExtras.addAll(servicios);
        }
        return this;
    }

    public Envio build() {
        if (usuario == null || direccionOrigen == null || direccionDestino == null) {
            throw new IllegalStateException("Usuario, Origen y Destino son obligatorios para construir un Envío.");
        }

        // Generar ID si no se proporcionó
        if (idEnvio == null || idEnvio.trim().isEmpty()) {
            idEnvio = generarIdUnico();
        }

        Envio envio = new Envio(idEnvio, usuario.getIdUsuario(), direccionOrigen, direccionDestino, peso, volumen, costo, tipoEnvio, serviciosExtras);


        UsuarioNotificacionObserver observadorUsuario = new UsuarioNotificacionObserver(usuario);
        envio.agregarObservador(observadorUsuario);

        envio.setCosto(this.costo);

        return envio;
    }

    private String generarIdUnico() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String userHash = usuario != null && usuario.getIdUsuario() != null ?
                String.valueOf(Math.abs(usuario.getIdUsuario().hashCode())).substring(0, 4) :
                "0000";
        return "ENV_" + timestamp + "_" + userHash;
    }

    // Métodos estáticos de conveniencia (Factory Methods del Builder)
    public static EnvioBuilder crearEnvioEstandar(Usuario usuario, Direccion origen, Direccion destino) {
        return new EnvioBuilder()
                .conUsuario(usuario) // Importante: pasar el usuario
                .conDireccionOrigen(origen)
                .conDireccionDestino(destino)
                .conTipoEnvio("ESTANDAR");
    }

    public static EnvioBuilder crearEnvioExpress(Usuario usuario, Direccion origen, Direccion destino) {
        return crearEnvioEstandar(usuario, origen, destino)
                .conTipoEnvio("EXPRESS")
                .agregarServicio("SEGURO");
    }

    public static EnvioBuilder crearEnvioFragil(Usuario usuario, Direccion origen, Direccion destino) {
        return crearEnvioEstandar(usuario, origen, destino)
                .conTipoEnvio("ESTANDAR")
                .agregarServicio("FRAGIL")
                .agregarServicio("EMBALAJE_ESPECIAL");
    }
}