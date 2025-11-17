package proyecto_final_pp.model;

import proyecto_final_pp.model.dto.EnvioDTO;
import proyecto_final_pp.model.dto.DireccionDTO;
import proyecto_final_pp.model.dto.MetodoPagoDTO;
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

    // Métodos de estado
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

        // CORREGIDO: Usar setters en lugar del constructor para evitar problemas de parámetros
        EnvioDTO envioDTO = new EnvioDTO();
        envioDTO.setIdEnvio(this.idEnvio);
        envioDTO.setIdUsuario(this.idUsuario);
        envioDTO.setDireccionOrigen(origenDTO);
        envioDTO.setDireccionDestino(destinoDTO);
        envioDTO.setPeso(this.peso);
        envioDTO.setVolumen(this.volumen);
        envioDTO.setCosto(this.costo);
        envioDTO.setEstadoActual(enumEstado);
        envioDTO.setFechaCreacion(this.fechaCreacion);
        envioDTO.setFechaActualizacionEstado(this.fechaActualizacionEstado);
        envioDTO.setTipoEnvio(this.tipoEnvio);
        envioDTO.setServiciosExtras(this.serviciosExtras != null ? new ArrayList<>(this.serviciosExtras) : new ArrayList<>());

        // El método de pago no se establece aquí ya que el modelo Envio no tiene ese campo
        // Se establecerá desde el servicio cuando sea necesario

        return envioDTO;
    }

    // Método para obtener información básica del envío (para logs)
    public String getInfoBasica() {
        return String.format("Envío %s - %s -> %s - Estado: %s",
                idEnvio,
                direccionOrigen != null ? direccionOrigen.getCiudad() : "N/A",
                direccionDestino != null ? direccionDestino.getCiudad() : "N/A",
                estadoActual.getNombre());
    }

    // Método para verificar si el envío puede ser cancelado
    public boolean puedeSerCancelado() {
        return estadoActual.puedeCancelar();
    }

    // Método para verificar si el envío está en un estado final
    public boolean estaEnEstadoFinal() {
        return estadoActual instanceof EstadoEntregado ||
                estadoActual instanceof EstadoCancelado;
    }

    @Override
    public String toString() {
        return "Envio{" +
                "idEnvio='" + idEnvio + '\'' +
                ", idUsuario='" + idUsuario + '\'' +
                ", direccionOrigen=" + (direccionOrigen != null ? direccionOrigen.getCiudad() : "null") +
                ", direccionDestino=" + (direccionDestino != null ? direccionDestino.getCiudad() : "null") +
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

    // Método equals y hashCode para comparaciones
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Envio envio = (Envio) o;
        return idEnvio != null && idEnvio.equals(envio.idEnvio);
    }

    @Override
    public int hashCode() {
        return idEnvio != null ? idEnvio.hashCode() : 0;
    }
}