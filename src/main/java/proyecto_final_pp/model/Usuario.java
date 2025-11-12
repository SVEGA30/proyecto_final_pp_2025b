package proyecto_final_pp.model;

import proyecto_final_pp.model.dto.UsuarioDTO;
import proyecto_final_pp.model.dto.DireccionDTO;
import proyecto_final_pp.model.dto.MetodoPagoDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Usuario {
    private String idUsuario;
    private String nombre;
    private String correo;
    private String telefono;
    private String contrasena;
    private List<Direccion> direcciones;
    private List<String> metodosPago;

    public Usuario(String idUsuario, String nombre, String correo, String telefono, String contrasena) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.contrasena = contrasena;
        this.direcciones = new ArrayList<>();
        this.metodosPago = new ArrayList<>();
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

    public List<Direccion> getDirecciones() { return new ArrayList<>(direcciones); }
    public void setDirecciones(List<Direccion> direcciones) { this.direcciones = direcciones != null ? new ArrayList<>(direcciones) : new ArrayList<>(); }

    public List<String> getMetodosPago() { return new ArrayList<>(metodosPago); }
    public void setMetodosPago(List<String> metodosPago) { this.metodosPago = metodosPago != null ? new ArrayList<>(metodosPago) : new ArrayList<>(); }

    // Métodos para agregar direcciones y métodos de pago
    public void agregarDireccion(Direccion direccion) {
        if (direccion != null && !direcciones.contains(direccion)) {
            direcciones.add(direccion);
        }
    }

    public void agregarMetodoPago(String metodo) {
        if (metodo != null && !metodosPago.contains(metodo)) {
            metodosPago.add(metodo);
        }
    }

    // Métodos de Conversión DTO <-> Modelo
    // DTO -> Modelo
    public static Usuario fromDTO(UsuarioDTO dto) {
        if (dto == null) return null;
        Usuario usuario = new Usuario(dto.getIdUsuario(), dto.getNombre(), dto.getCorreo(), dto.getTelefono(), dto.getContrasena());

        // Convertir direcciones del DTO al modelo
        if (dto.getDirecciones() != null) {
            List<Direccion> direccionesModelo = dto.getDirecciones().stream()
                    .map(Direccion::fromDTO)
                    .collect(Collectors.toList());
            usuario.setDirecciones(direccionesModelo);
        }

        // Convertir métodos de pago
        if (dto.getMetodosPago() != null) {
            usuario.setMetodosPago(new ArrayList<>(dto.getMetodosPago()));
        }

        return usuario;
    }

    // Modelo -> DTO
    public UsuarioDTO toDTO() {
        // Convertir direcciones del modelo a DTO
        List<DireccionDTO> direccionesDTO = this.direcciones.stream()
                .map(Direccion::toDTO)
                .collect(Collectors.toList());

        // Convertir métodos de pago
        List<String> metodosPagoDTO = new ArrayList<>(this.metodosPago);

        return new UsuarioDTO(
                this.idUsuario,
                this.nombre,
                this.correo,
                this.telefono,
                this.contrasena,
                direccionesDTO,        // CORREGIDO: List<DireccionDTO>
                metodosPagoDTO         // CORREGIDO: List<String>
        );
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario='" + idUsuario + '\'' +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direcciones=" + direcciones +
                ", metodosPago=" + metodosPago +
                '}';
    }
}