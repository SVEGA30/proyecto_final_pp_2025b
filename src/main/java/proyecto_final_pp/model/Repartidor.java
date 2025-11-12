package proyecto_final_pp.model;

import proyecto_final_pp.model.dto.RepartidorDTO;

public class Repartidor {
    private String idRepartidor;
    private String nombre;
    private String documento;
    private String telefono;
    private EstadoDisponibilidad disponibilidad;
    private String zonaCobertura;

    public Repartidor(String idRepartidor, String nombre, String documento, String telefono, String zonaCobertura) {
        this.idRepartidor = idRepartidor;
        this.nombre = nombre;
        this.documento = documento;
        this.telefono = telefono;
        this.disponibilidad = EstadoDisponibilidad.ACTIVO;
        this.zonaCobertura = zonaCobertura;
    }

    // Getters y Setters
    public String getIdRepartidor() { return idRepartidor; }
    public void setIdRepartidor(String idRepartidor) { this.idRepartidor = idRepartidor; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public EstadoDisponibilidad getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(EstadoDisponibilidad disponibilidad) { this.disponibilidad = disponibilidad; }

    public String getZonaCobertura() { return zonaCobertura; }
    public void setZonaCobertura(String zonaCobertura) { this.zonaCobertura = zonaCobertura; }

    // Métodos de negocio
    public boolean estaDisponible() {
        return disponibilidad == EstadoDisponibilidad.ACTIVO;
    }

    public void cambiarDisponibilidad(EstadoDisponibilidad nuevaDisponibilidad) {
        this.disponibilidad = nuevaDisponibilidad;
    }

    // Conversión DTO
    public static Repartidor fromDTO(RepartidorDTO dto) {
        if (dto == null) return null;
        Repartidor repartidor = new Repartidor(dto.getIdRepartidor(), dto.getNombre(), dto.getDocumento(), dto.getTelefono(), dto.getZonaCobertura());
        repartidor.setDisponibilidad(EstadoDisponibilidad.valueOf(dto.getDisponibilidad()));
        return repartidor;
    }

    public RepartidorDTO toDTO() {
        return new RepartidorDTO(idRepartidor, nombre, documento, telefono, disponibilidad.name(), zonaCobertura);
    }

    @Override
    public String toString() {
        return nombre + " (" + idRepartidor + ") - " + disponibilidad;
    }

    public enum EstadoDisponibilidad {
        ACTIVO, INACTIVO, EN_RUTA
    }
}