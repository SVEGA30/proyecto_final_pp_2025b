package proyecto_final_pp.model;

import proyecto_final_pp.model.dto.AdministradorDTO;

public class Administrador {
    private String usuario; // Nombre de usuario
    private String contrasena;

    public Administrador(String usuario, String contrasena) {
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    // Getters y Setters...
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    // Métodos de Conversión DTO <-> Modelo
    // DTO -> Modelo
    public static Administrador fromDTO(AdministradorDTO dto) {
        if (dto == null) return null;
        return new Administrador(dto.getNombreUsuario(), dto.getContrasena());
    }

    // Modelo -> DTO
    public AdministradorDTO toDTO() {
        return new AdministradorDTO(this.usuario, this.contrasena);
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "usuario='" + usuario + '\'' +
                '}';
    }
}