package proyecto_final_pp.model.dto;

public class RepartidorDTO {
    private String idRepartidor;
    private String nombre;
    private String documento; // Campo añadido
    private String telefono;
    private String disponibilidad;
    private String zonaCobertura;

    public RepartidorDTO() {}

    public RepartidorDTO(String idRepartidor, String nombre, String documento, String telefono, String disponibilidad, String zonaCobertura) {
        this.idRepartidor = idRepartidor;
        this.nombre = nombre;
        this.documento = documento;
        this.telefono = telefono;
        this.disponibilidad = disponibilidad;
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

    public String getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(String disponibilidad) { this.disponibilidad = disponibilidad; }

    public String getZonaCobertura() { return zonaCobertura; }
    public void setZonaCobertura(String zonaCobertura) { this.zonaCobertura = zonaCobertura; }

    @Override
    public String toString() {
        return "RepartidorDTO{" +
                "idRepartidor='" + idRepartidor + '\'' +
                ", nombre='" + nombre + '\'' +
                ", documento='" + documento + '\'' +
                ", telefono='" + telefono + '\'' +
                ", disponibilidad='" + disponibilidad + '\'' +
                ", zonaCobertura='" + zonaCobertura + '\'' +
                '}';
    }
}