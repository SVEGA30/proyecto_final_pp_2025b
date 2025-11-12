package proyecto_final_pp.model.dto;

import java.util.ArrayList;
import java.util.List;

public class EnvioConServiciosDTO extends EnvioDTO {
    private List<ServicioAdicionalDTO> serviciosAdicionales;
    private double costoTotalServicios;

    public EnvioConServiciosDTO() {
        super();
        this.serviciosAdicionales = new ArrayList<>();
        this.costoTotalServicios = 0.0;
    }

    public EnvioConServiciosDTO(EnvioDTO envioBase) {
        super();
        // Copiar propiedades del envío base
        this.setIdEnvio(envioBase.getIdEnvio());
        this.setIdUsuario(envioBase.getIdUsuario());
        this.setDireccionOrigen(envioBase.getDireccionOrigen());
        this.setDireccionDestino(envioBase.getDireccionDestino());
        this.setPeso(envioBase.getPeso());
        this.setVolumen(envioBase.getVolumen());
        this.setCosto(envioBase.getCosto());
        this.setTipoEnvio(envioBase.getTipoEnvio());
        this.setServiciosExtras(envioBase.getServiciosExtras());
        this.setEstadoActual(envioBase.getEstadoActual());
        this.setFechaCreacion(envioBase.getFechaCreacion());

        this.serviciosAdicionales = new ArrayList<>();
        this.costoTotalServicios = 0.0;
    }

    // Getters y Setters
    public List<ServicioAdicionalDTO> getServiciosAdicionales() {
        if (serviciosAdicionales == null) {
            serviciosAdicionales = new ArrayList<>();
        }
        return serviciosAdicionales;
    }

    public void setServiciosAdicionales(List<ServicioAdicionalDTO> serviciosAdicionales) {
        this.serviciosAdicionales = serviciosAdicionales;
        calcularCostoTotalServicios();
    }

    public double getCostoTotalServicios() {
        return costoTotalServicios;
    }

    public void setCostoTotalServicios(double costoTotalServicios) {
        this.costoTotalServicios = costoTotalServicios;
    }

    // Métodos de utilidad
    public void agregarServicio(ServicioAdicionalDTO servicio) {
        if (servicio == null) {
            return;
        }
        getServiciosAdicionales().add(servicio);
        calcularCostoTotalServicios();
    }

    public void removerServicio(ServicioAdicionalDTO servicio) {
        if (servicio != null && serviciosAdicionales != null) {
            serviciosAdicionales.remove(servicio);
            calcularCostoTotalServicios();
        }
    }

    public void removerServicioPorTipo(String tipoServicio) {
        if (tipoServicio != null && serviciosAdicionales != null) {
            serviciosAdicionales.removeIf(servicio ->
                    tipoServicio.equalsIgnoreCase(servicio.getTipoServicio()));
            calcularCostoTotalServicios();
        }
    }

    public boolean tieneServicio(String tipoServicio) {
        if (tipoServicio == null || serviciosAdicionales == null) {
            return false;
        }
        return serviciosAdicionales.stream()
                .anyMatch(servicio -> tipoServicio.equalsIgnoreCase(servicio.getTipoServicio()));
    }

    private void calcularCostoTotalServicios() {
        if (serviciosAdicionales == null) {
            this.costoTotalServicios = 0.0;
            return;
        }
        this.costoTotalServicios = serviciosAdicionales.stream()
                .mapToDouble(ServicioAdicionalDTO::getCostoAdicional)
                .sum();
    }

    public double getCostoTotal() {
        // Usar Double para manejar posible null y evitar el error
        Double costoBase = super.getCosto();
        double costoBaseValue = (costoBase != null ? costoBase : 0.0);
        return costoBaseValue + costoTotalServicios;
    }

    // Método para obtener el costo base de forma segura
    public double getCostoBase() {
        Double costo = super.getCosto();
        return costo != null ? costo : 0.0;
    }

    // Método para establecer el costo de forma segura
    public void setCostoBase(double costo) {
        super.setCosto(costo);
    }

    // Método para limpiar todos los servicios
    public void limpiarServicios() {
        if (serviciosAdicionales != null) {
            serviciosAdicionales.clear();
            calcularCostoTotalServicios();
        }
    }

    // Método para obtener la cantidad de servicios
    public int getCantidadServicios() {
        return serviciosAdicionales != null ? serviciosAdicionales.size() : 0;
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nServicios Adicionales: " + getCantidadServicios() +
                "\nCosto Servicios: $" + costoTotalServicios +
                "\nCosto Total: $" + getCostoTotal();
    }

    // Método para obtener descripción detallada de servicios
    public String getDescripcionServicios() {
        if (serviciosAdicionales == null || serviciosAdicionales.isEmpty()) {
            return "Sin servicios adicionales";
        }

        StringBuilder sb = new StringBuilder();
        for (ServicioAdicionalDTO servicio : serviciosAdicionales) {
            sb.append("\n- ").append(servicio.getTipoServicio())
                    .append(": $").append(servicio.getCostoAdicional());
        }
        return sb.toString();
    }
}