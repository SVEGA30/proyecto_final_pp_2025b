package proyecto_final_pp.model.dto;

import java.util.List;

public class UsuarioDTO {
    private String idUsuario;
    private String nombre;
    private String correo;
    private String telefono;
    private String contrasena;
    private List<DireccionDTO> direcciones; // Debe ser List<DireccionDTO>
    private List<String> metodosPago;       // Debe ser List<String>

    // Constructor vacío
    public UsuarioDTO() {}

    // Constructor completo
    public UsuarioDTO(String idUsuario, String nombre, String correo, String telefono,
                      String contrasena, List<DireccionDTO> direcciones, List<String> metodosPago) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.contrasena = contrasena;
        this.direcciones = direcciones;
        this.metodosPago = metodosPago;
    }

    // Getters y Setters
    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public List<DireccionDTO> getDirecciones() { return direcciones; }
    public void setDirecciones(List<DireccionDTO> direcciones) { this.direcciones = direcciones; }

    public List<String> getMetodosPago() { return metodosPago; }
    public void setMetodosPago(List<String> metodosPago) { this.metodosPago = metodosPago; }

    @Override
    public String toString() {
        return "UsuarioDTO{" +
                "idUsuario='" + idUsuario + '\'' +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direcciones=" + direcciones +
                ", metodosPago=" + metodosPago +
                '}';
    }
}