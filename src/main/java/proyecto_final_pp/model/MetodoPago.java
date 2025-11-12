package proyecto_final_pp.model;

import proyecto_final_pp.model.dto.MetodoPagoDTO;

// Asumiendo que MetodoPago es una clase simple como en el DTO
public class MetodoPago {
    private String idMetodo;
    private String tipo; // "Tarjeta Crédito", "Nequi", etc.
    private String referencia; // Número de tarjeta (parcial), número de celular, etc. (simulado)

    // Constructor existente (ajustado para incluir ID)
    public MetodoPago(String idMetodo, String tipo, String referencia) {
        this.idMetodo = idMetodo;
        this.tipo = tipo;
        this.referencia = referencia;
    }

    // Constructor alternativo para crear sin ID
    public MetodoPago(String tipo, String referencia) {
        this.idMetodo = "PAGO_" + System.currentTimeMillis(); // Generación simple de ID
        this.tipo = tipo;
        this.referencia = referencia;
    }

    // Getters y Setters...
    public String getIdMetodo() { return idMetodo; }
    public void setIdMetodo(String idMetodo) { this.idMetodo = idMetodo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    // Métodos de Conversión DTO <-> Modelo
    // DTO -> Modelo
    public static MetodoPago fromDTO(MetodoPagoDTO dto) {
        if (dto == null) return null;
        return new MetodoPago(dto.getIdMetodo(), dto.getTipo(), dto.getReferencia());
    }

    // Modelo -> DTO
    public MetodoPagoDTO toDTO() {
        return new MetodoPagoDTO(this.idMetodo, this.tipo, this.referencia);
    }

    @Override
    public String toString() {
        return tipo + " - " + referencia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetodoPago that = (MetodoPago) o;
        return idMetodo != null ? idMetodo.equals(that.idMetodo) : tipo.equals(that.tipo) && referencia.equals(that.referencia);
    }

    @Override
    public int hashCode() {
        return idMetodo != null ? idMetodo.hashCode() : (tipo + referencia).hashCode();
    }
}