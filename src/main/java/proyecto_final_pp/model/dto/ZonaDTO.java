package proyecto_final_pp.model.dto;

public class ZonaDTO {
    private String idZona;
    private String nombre;

    public ZonaDTO() {}

    public ZonaDTO(String idZona, String nombre) {
        this.idZona = idZona;
        this.nombre = nombre;
    }

    public String getIdZona() { return idZona; }
    public void setIdZona(String idZona) { this.idZona = idZona; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre; // Solo muestra el nombre de la zona
    }
}