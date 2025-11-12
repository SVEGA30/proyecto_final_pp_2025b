package proyecto_final_pp.model;

import proyecto_final_pp.model.dto.ZonaDTO;

public class Zona {
    private String idZona; // Asumiendo ID para Zona
    private String nombre;

    public Zona(String idZona, String nombre) {
        this.idZona = idZona;
        this.nombre = nombre;
    }

    // Constructor alternativo para crear sin ID
    public Zona(String nombre) {
        this.idZona = "ZONA_" + System.currentTimeMillis(); // Generación simple de ID
        this.nombre = nombre;
    }

    // Getters y Setters...
    public String getIdZona() { return idZona; }
    public void setIdZona(String idZona) { this.idZona = idZona; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Métodos de Conversión DTO <-> Modelo
    // DTO -> Modelo
    public static Zona fromDTO(ZonaDTO dto) {
        if (dto == null) return null;
        return new Zona(dto.getIdZona(), dto.getNombre());
    }

    // Modelo -> DTO
    public ZonaDTO toDTO() {
        return new ZonaDTO(this.idZona, this.nombre);
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zona zona = (Zona) o;
        return idZona != null ? idZona.equals(zona.idZona) : nombre.equals(zona.nombre);
    }

    @Override
    public int hashCode() {
        return idZona != null ? idZona.hashCode() : nombre.hashCode();
    }
}