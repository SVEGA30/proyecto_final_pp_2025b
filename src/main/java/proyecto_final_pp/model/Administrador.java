package proyecto_final_pp.model;

import proyecto_final_pp.model.dto.AdministradorDTO;

public class Administrador {
    private String usuario; // Nombre de usuario
    private String contrasena;

    public Administrador(String usuario, String contrasena) {
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public static Administrador fromDTO(AdministradorDTO dto) {
        if (dto == null) return null;
        return new Administrador(dto.getNombreUsuario(), dto.getContrasena());
    }

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