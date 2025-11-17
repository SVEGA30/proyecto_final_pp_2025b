package proyecto_final_pp.model.dto;

import java.util.Objects;

public class DireccionDTO {
    private String idDireccion;
    private String calle;
    private String ciudad;
    private String zona;
    private String alias;

    public DireccionDTO() {}

    public DireccionDTO(String idDireccion, String calle, String ciudad, String zona, String alias) {
        this.idDireccion = idDireccion;
        this.calle = calle;
        this.ciudad = ciudad;
        this.zona = zona;
        this.alias = alias;
    }

    // Getters y Setters
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

    @Override
    public String toString() {
        return alias + " - " + calle + ", " + ciudad + " (" + zona + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        DireccionDTO that = (DireccionDTO) obj;

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