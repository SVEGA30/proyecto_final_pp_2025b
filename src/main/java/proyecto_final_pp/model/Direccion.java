package proyecto_final_pp.model;

import proyecto_final_pp.model.dto.DireccionDTO;
import java.util.Objects;

public class Direccion {
    private String idDireccion;
    private String calle;
    private String ciudad;
    private String zona;
    private String alias;

    public Direccion() {}

    public Direccion(String calle, String ciudad, String zona, String alias) {
        this.calle = calle;
        this.ciudad = ciudad;
        this.zona = zona;
        this.alias = alias;
        this.idDireccion = "DIR_" + System.currentTimeMillis() + "_" + Objects.hash(calle, ciudad, zona);
    }

    public Direccion(String idDireccion, String calle, String ciudad, String zona, String alias) {
        this.idDireccion = idDireccion;
        this.calle = calle;
        this.ciudad = ciudad;
        this.zona = zona;
        this.alias = alias;
    }

    public String getIdDireccion() { return idDireccion; }
    public void setIdDireccion(String idDireccion) { this.idDireccion = idDireccion; }

    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }


    public DireccionDTO toDTO() {
        DireccionDTO dto = new DireccionDTO();
        dto.setIdDireccion(this.idDireccion);
        dto.setCalle(this.calle);
        dto.setCiudad(this.ciudad);
        dto.setZona(this.zona);
        dto.setAlias(this.alias);
        return dto;
    }

    public static Direccion fromDTO(DireccionDTO dto) {
        if (dto == null) return null;
        Direccion direccion = new Direccion();
        direccion.setIdDireccion(dto.getIdDireccion());
        direccion.setCalle(dto.getCalle());
        direccion.setCiudad(dto.getCiudad());
        direccion.setZona(dto.getZona());
        direccion.setAlias(dto.getAlias());
        return direccion;
    }

    @Override
    public String toString() {
        return alias + " - " + calle + ", " + ciudad + " (" + zona + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Direccion that = (Direccion) obj;

        // Comparar por contenido, no por ID
        return Objects.equals(calle, that.calle) &&
                Objects.equals(ciudad, that.ciudad) &&
                Objects.equals(zona, that.zona) &&
                Objects.equals(alias, that.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calle, ciudad, zona, alias);
    }
}