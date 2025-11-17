package proyecto_final_pp.model;

import proyecto_final_pp.model.dto.RepartidorDTO;

public class Repartidor {
    private String idRepartidor;
    private String nombre;
    private String documento;
    private String telefono;
    private String zonaCobertura;
    private String disponibilidad;

    public Repartidor() {}

    public Repartidor(String idRepartidor, String nombre, String documento, String telefono, String zonaCobertura) {
        this.idRepartidor = idRepartidor;
        this.nombre = nombre;
        this.documento = documento;
        this.telefono = telefono;
        this.zonaCobertura = zonaCobertura;
        this.disponibilidad = "ACTIVO";
    }


    public String getIdRepartidor() { return idRepartidor; }
    public void setIdRepartidor(String idRepartidor) { this.idRepartidor = idRepartidor; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getZonaCobertura() { return zonaCobertura; }
    public void setZonaCobertura(String zonaCobertura) { this.zonaCobertura = zonaCobertura; }

    public String getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(String disponibilidad) { this.disponibilidad = disponibilidad; }

    // Método para verificar disponibilidad
    public boolean estaDisponible() {
        return "ACTIVO".equals(disponibilidad) || "DISPONIBLE".equals(disponibilidad);
    }

    // Métodos de conversión DTO
    public RepartidorDTO toDTO() {
        return new RepartidorDTO(
                this.idRepartidor,
                this.nombre,
                this.documento,
                this.telefono,
                this.disponibilidad,
                this.zonaCobertura
        );
    }

    public static Repartidor fromDTO(RepartidorDTO dto) {
        Repartidor repartidor = new Repartidor();
        repartidor.setIdRepartidor(dto.getIdRepartidor());
        repartidor.setNombre(dto.getNombre());
        repartidor.setDocumento(dto.getDocumento());
        repartidor.setTelefono(dto.getTelefono());
        repartidor.setZonaCobertura(dto.getZonaCobertura());
        repartidor.setDisponibilidad(dto.getDisponibilidad() != null ? dto.getDisponibilidad() : "ACTIVO");
        return repartidor;
    }

    @Override
    public String toString() {
        return "Repartidor{" +
                "idRepartidor='" + idRepartidor + '\'' +
                ", nombre='" + nombre + '\'' +
                ", documento='" + documento + '\'' +
                ", telefono='" + telefono + '\'' +
                ", zonaCobertura='" + zonaCobertura + '\'' +
                ", disponibilidad='" + disponibilidad + '\'' +
                '}';
    }
}