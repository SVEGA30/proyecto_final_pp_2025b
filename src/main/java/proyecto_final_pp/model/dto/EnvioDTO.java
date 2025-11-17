package proyecto_final_pp.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class EnvioDTO {
    private String idEnvio;
    private String idUsuario;
    private DireccionDTO direccionOrigen;
    private DireccionDTO direccionDestino;
    private double peso;
    private double volumen;
    private double costo;
    private EstadoEnvio estadoActual;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacionEstado;
    private String tipoEnvio;
    private List<String> serviciosExtras;
    private MetodoPagoDTO metodoPago;

    // Constructores
    public EnvioDTO() {}

    public EnvioDTO(String idEnvio, String idUsuario, DireccionDTO direccionOrigen,
                    DireccionDTO direccionDestino, double peso, double volumen, double costo,
                    EstadoEnvio estadoActual, LocalDateTime fechaCreacion,
                    LocalDateTime fechaActualizacionEstado, String tipoEnvio,
                    List<String> serviciosExtras, MetodoPagoDTO metodoPago) {
        this.idEnvio = idEnvio;
        this.idUsuario = idUsuario;
        this.direccionOrigen = direccionOrigen;
        this.direccionDestino = direccionDestino;
        this.peso = peso;
        this.volumen = volumen;
        this.costo = costo;
        this.estadoActual = estadoActual;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacionEstado = fechaActualizacionEstado;
        this.tipoEnvio = tipoEnvio;
        this.serviciosExtras = serviciosExtras;
        this.metodoPago = metodoPago;
    }

    // Getters y Setters
    public String getIdEnvio() { return idEnvio; }
    public void setIdEnvio(String idEnvio) { this.idEnvio = idEnvio; }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public DireccionDTO getDireccionOrigen() { return direccionOrigen; }
    public void setDireccionOrigen(DireccionDTO direccionOrigen) { this.direccionOrigen = direccionOrigen; }

    public DireccionDTO getDireccionDestino() { return direccionDestino; }
    public void setDireccionDestino(DireccionDTO direccionDestino) { this.direccionDestino = direccionDestino; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public double getVolumen() { return volumen; }
    public void setVolumen(double volumen) { this.volumen = volumen; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }

    public EstadoEnvio getEstadoActual() { return estadoActual; }
    public void setEstadoActual(EstadoEnvio estadoActual) { this.estadoActual = estadoActual; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacionEstado() { return fechaActualizacionEstado; }
    public void setFechaActualizacionEstado(LocalDateTime fechaActualizacionEstado) { this.fechaActualizacionEstado = fechaActualizacionEstado; }

    public String getTipoEnvio() { return tipoEnvio; }
    public void setTipoEnvio(String tipoEnvio) { this.tipoEnvio = tipoEnvio; }

    public List<String> getServiciosExtras() { return serviciosExtras; }
    public void setServiciosExtras(List<String> serviciosExtras) { this.serviciosExtras = serviciosExtras; }

    // Nuevos getters y setters para método de pago
    public MetodoPagoDTO getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPagoDTO metodoPago) { this.metodoPago = metodoPago; }

    @Override
    public String toString() {
        return "EnvioDTO{" +
                "idEnvio='" + idEnvio + '\'' +
                ", idUsuario='" + idUsuario + '\'' +
                ", estadoActual=" + estadoActual +
                ", costo=" + costo +
                ", tipoEnvio='" + tipoEnvio + '\'' +
                ", metodoPago=" + (metodoPago != null ? metodoPago.getTipo() : "null") +
                '}';
    }

    // Método toString más detallado
    public String toStringDetallado() {
        return "EnvioDTO{" +
                "idEnvio='" + idEnvio + '\'' +
                ", idUsuario='" + idUsuario + '\'' +
                ", direccionOrigen=" + (direccionOrigen != null ? direccionOrigen.getAlias() : "null") +
                ", direccionDestino=" + (direccionDestino != null ? direccionDestino.getAlias() : "null") +
                ", peso=" + peso +
                ", volumen=" + volumen +
                ", costo=" + costo +
                ", estadoActual=" + estadoActual +
                ", fechaCreacion=" + fechaCreacion +
                ", tipoEnvio='" + tipoEnvio + '\'' +
                ", serviciosExtras=" + serviciosExtras +
                ", metodoPago=" + (metodoPago != null ? metodoPago.getTipo() + " (" + metodoPago.getReferencia() + ")" : "null") +
                '}';
    }

    // ✅ Enum interno
    public static enum EstadoEnvio {
        SOLICITADO, ASIGNADO, EN_RUTA, ENTREGADO, INCIDENCIA, CANCELADO
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnvioDTO envioDTO = (EnvioDTO) o;
        return Objects.equals(idEnvio, envioDTO.idEnvio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEnvio);
    }
}