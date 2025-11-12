package proyecto_final_pp.model.dto;

public class ServicioAdicionalDTO {
    private String tipoServicio;
    private String descripcion;
    private double costoAdicional;
    private boolean activo;

    public ServicioAdicionalDTO() {}

    public ServicioAdicionalDTO(String tipoServicio, String descripcion, double costoAdicional) {
        this.tipoServicio = tipoServicio;
        this.descripcion = descripcion;
        this.costoAdicional = costoAdicional;
        this.activo = true;
    }

    // Getters y Setters
    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getCostoAdicional() {
        return costoAdicional;
    }

    public void setCostoAdicional(double costoAdicional) {
        this.costoAdicional = costoAdicional;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return tipoServicio + " - " + descripcion + " ($" + costoAdicional + ")";
    }
}