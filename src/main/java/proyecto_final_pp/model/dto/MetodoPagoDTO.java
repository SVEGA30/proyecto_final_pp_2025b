package proyecto_final_pp.model.dto;

// DTO para la entidad MetodoPago
public class MetodoPagoDTO {
    private String idMetodo;
    private String tipo; // "Tarjeta Crédito", "Nequi", etc.
    private String referencia; // Número de tarjeta (parcial), número de celular, etc. (simulado)

    // Constructor vacío
    public MetodoPagoDTO() {}

    // Constructor principal
    public MetodoPagoDTO(String idMetodo, String tipo, String referencia) {
        this.idMetodo = idMetodo;
        this.tipo = tipo;
        this.referencia = referencia;
    }

    // Getters y Setters
    public String getIdMetodo() { return idMetodo; }
    public void setIdMetodo(String idMetodo) { this.idMetodo = idMetodo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    @Override
    public String toString() {
        return "MetodoPagoDTO{" +
                "idMetodo='" + idMetodo + '\'' +
                ", tipo='" + tipo + '\'' +
                ", referencia='" + referencia + '\'' +
                '}';
    }
}