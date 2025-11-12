package proyecto_final_pp.model;

import proyecto_final_pp.model.dto.EnvioDTO;
import proyecto_final_pp.model.dto.DireccionDTO;
import proyecto_final_pp.observer.EnvioObserver;
import proyecto_final_pp.observer.EnvioSubject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Envio implements EnvioSubject {
    private String idEnvio;
    private String idUsuario;
    private Direccion direccionOrigen;
    private Direccion direccionDestino;
    private double peso;
    private double volumen;
    private double costo;
    private EstadoEnvio estadoActual;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacionEstado;
    private String tipoEnvio;
    private List<String> serviciosExtras;
    private String repartidorAsignadoId;
    private List<EnvioObserver> observadores;

    public Envio(String idEnvio, String idUsuario, Direccion direccionOrigen, Direccion direccionDestino,
                 double peso, double volumen, double costo, String tipoEnvio, List<String> serviciosExtras) {
        this.idEnvio = idEnvio;
        this.idUsuario = idUsuario;
        this.direccionOrigen = direccionOrigen;
        this.direccionDestino = direccionDestino;
        this.peso = peso;
        this.volumen = volumen;
        this.costo = costo;
        this.tipoEnvio = tipoEnvio;
        this.serviciosExtras = serviciosExtras != null ? new ArrayList<>(serviciosExtras) : new ArrayList<>();
        this.estadoActual = new EstadoSolicitado();
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacionEstado = this.fechaCreacion;
        this.repartidorAsignadoId = null;
        this.observadores = new ArrayList<>();
    }

    // Getters y Setters
    public String getIdEnvio() { return idEnvio; }
    public void setIdEnvio(String idEnvio) { this.idEnvio = idEnvio; }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public Direccion getDireccionOrigen() { return direccionOrigen; }
    public void setDireccionOrigen(Direccion direccionOrigen) { this.direccionOrigen = direccionOrigen; }

    public Direccion getDireccionDestino() { return direccionDestino; }
    public void setDireccionDestino(Direccion direccionDestino) { this.direccionDestino = direccionDestino; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public double getVolumen() { return volumen; }
    public void setVolumen(double volumen) { this.volumen = volumen; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }

    public EstadoEnvio getEstadoActual() { return estadoActual; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacionEstado() { return fechaActualizacionEstado; }
    public void setFechaActualizacionEstado(LocalDateTime fechaActualizacionEstado) { this.fechaActualizacionEstado = fechaActualizacionEstado; }

    public String getTipoEnvio() { return tipoEnvio; }
    public void setTipoEnvio(String tipoEnvio) { this.tipoEnvio = tipoEnvio; }

    public List<String> getServiciosExtras() { return new ArrayList<>(serviciosExtras); }
    public void setServiciosExtras(List<String> serviciosExtras) { this.serviciosExtras = serviciosExtras != null ? new ArrayList<>(serviciosExtras) : new ArrayList<>(); }

    public String getRepartidorAsignadoId() { return repartidorAsignadoId; }
    public void setRepartidorAsignadoId(String repartidorAsignadoId) { this.repartidorAsignadoId = repartidorAsignadoId; }

    // Métodos de gestión de estado
    public void asignarARepartidor(String idRepartidor) {
        if (estadoActual.puedeAsignar()) {
            this.repartidorAsignadoId = idRepartidor;
            cambiarEstado(new EstadoAsignado());
        } else {
            throw new IllegalStateException("No se puede asignar el envío en estado " + estadoActual.getNombre());
        }
    }

    public void iniciarRuta() {
        if (estadoActual.puedeIniciarRuta()) {
            cambiarEstado(new EstadoEnRuta());
        } else {
            throw new IllegalStateException("No se puede iniciar la ruta del envío en estado " + estadoActual.getNombre());
        }
    }

    public void entregar() {
        if (estadoActual.puedeEntregar()) {
            cambiarEstado(new EstadoEntregado());
        } else {
            throw new IllegalStateException("No se puede entregar el envío en estado " + estadoActual.getNombre());
        }
    }

    public void reportarIncidencia() {
        if (estadoActual.puedeReportarIncidencia()) {
            cambiarEstado(new EstadoIncidencia());
        } else {
            throw new IllegalStateException("No se puede reportar incidencia del envío en estado " + estadoActual.getNombre());
        }
    }

    public void cancelar() {
        if (estadoActual.puedeCancelar()) {
            cambiarEstado(new EstadoCancelado());
        } else {
            throw new IllegalStateException("No se puede cancelar el envío en estado " + estadoActual.getNombre());
        }
    }

    private void cambiarEstado(EstadoEnvio nuevoEstado) {
        this.estadoActual = nuevoEstado;
        this.fechaActualizacionEstado = LocalDateTime.now();
        this.estadoActual.ejecutarAccion(this);
        notificarObservadores();
    }

    // Implementación de Observer Pattern
    @Override
    public void agregarObservador(EnvioObserver observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void removerObservador(EnvioObserver observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores() {
        for (EnvioObserver observador : observadores) {
            observador.actualizar(this);
        }
    }

    // Métodos de Conversión DTO <-> Modelo
    public static Envio fromDTO(EnvioDTO dto) {
        if (dto == null) return null;
        Direccion origen = Direccion.fromDTO(dto.getDireccionOrigen());
        Direccion destino = Direccion.fromDTO(dto.getDireccionDestino());

        Envio envio = new Envio(
                dto.getIdEnvio(),
                dto.getIdUsuario(),
                origen,
                destino,
                dto.getPeso(),
                dto.getVolumen(),
                dto.getCosto(),
                dto.getTipoEnvio(),
                dto.getServiciosExtras()
        );

        return envio;
    }

    public EnvioDTO toDTOSimplified() {
        DireccionDTO origenDTO = this.direccionOrigen != null ? this.direccionOrigen.toDTO() : null;
        DireccionDTO destinoDTO = this.direccionDestino != null ? this.direccionDestino.toDTO() : null;

        // Conversión segura del estado al enum de EnvioDTO
        proyecto_final_pp.model.dto.EnvioDTO.EstadoEnvio enumEstado;
        try {
            enumEstado = proyecto_final_pp.model.dto.EnvioDTO.EstadoEnvio.valueOf(
                    this.estadoActual.getNombre().toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            // Si hay inconsistencias en los nombres, usar un estado por defecto
            enumEstado = proyecto_final_pp.model.dto.EnvioDTO.EstadoEnvio.SOLICITADO;
            System.err.println("Estado no mapeado: " + this.estadoActual.getNombre());
        }

        return new EnvioDTO(
                this.idEnvio,
                this.idUsuario,
                origenDTO,
                destinoDTO,
                this.peso,
                this.volumen,
                this.costo,
                enumEstado,
                this.fechaCreacion,
                this.fechaActualizacionEstado,
                this.tipoEnvio,
                this.serviciosExtras
        );
    }

    @Override
    public String toString() {
        return "Envio{" +
                "idEnvio='" + idEnvio + '\'' +
                ", idUsuario='" + idUsuario + '\'' +
                ", direccionOrigen=" + direccionOrigen +
                ", direccionDestino=" + direccionDestino +
                ", peso=" + peso +
                ", volumen=" + volumen +
                ", costo=" + costo +
                ", estadoActual=" + estadoActual.getNombre() +
                ", fechaCreacion=" + fechaCreacion +
                ", tipoEnvio='" + tipoEnvio + '\'' +
                ", serviciosExtras=" + serviciosExtras +
                ", repartidorAsignadoId='" + repartidorAsignadoId + '\'' +
                '}';
    }
}