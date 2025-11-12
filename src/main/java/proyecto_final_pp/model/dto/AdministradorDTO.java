package proyecto_final_pp.model.dto;


// DTO para la entidad Administrador
public class AdministradorDTO {
    private String nombreUsuario;
    private String contrasena; // Aunque se maneje con cuidado

    // Constructor vacío
    public AdministradorDTO() {}

    // Constructor principal
    public AdministradorDTO(String nombreUsuario, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }

    // Getters y Setters
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    @Override
    public String toString() {
        return "AdministradorDTO{" +
                "nombreUsuario='" + nombreUsuario + '\'' +
                '}';
    }
}