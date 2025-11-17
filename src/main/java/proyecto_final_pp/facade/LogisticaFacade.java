package proyecto_final_pp.facade;

import proyecto_final_pp.model.dto.*;
import proyecto_final_pp.Service.EnvioService;
import proyecto_final_pp.Service.DireccionService;
import proyecto_final_pp.Service.AdminService;
import proyecto_final_pp.util.GestorDatos;
import proyecto_final_pp.strategy.CalculadoraTarifaStrategy;
import proyecto_final_pp.strategy.CalculadoraTarifaBasica;

import java.util.List;

public class LogisticaFacade {

    private final EnvioService envioService;
    private final DireccionService direccionService;
    private final AdminService adminService;
    private final GestorDatos gestorDatos;

    public LogisticaFacade() {
        this.gestorDatos = GestorDatos.getInstance();
        this.direccionService = new DireccionService();
        this.envioService = new EnvioService(new CalculadoraTarifaBasica());
        this.adminService = new AdminService();
    }

    public LogisticaFacade(EnvioService envioService, DireccionService direccionService) {
        this.gestorDatos = GestorDatos.getInstance();
        this.direccionService = direccionService != null ? direccionService : new DireccionService();
        this.envioService = envioService != null ? envioService : new EnvioService(new CalculadoraTarifaBasica());
        this.adminService = new AdminService();
    }

    // --- OPERACIONES PARA USUARIO ---
    public UsuarioDTO iniciarSesion(String correo, String password) {
        UsuarioDTO usuario = gestorDatos.buscarUsuarioPorCorreo(correo);
        if (usuario != null && usuario.getContrasena().equals(password)) {
            return usuario;
        }
        return null;
    }

    public boolean registrarUsuario(UsuarioDTO usuarioDTO) {
        return gestorDatos.agregarUsuario(usuarioDTO);
    }

    public List<DireccionDTO> obtenerDireccionesFrecuentes(UsuarioDTO usuarioDTO) {
        return direccionService.obtenerDireccionesFrecuentes(usuarioDTO);
    }

    public List<ZonaDTO> obtenerTodasZonas() {
        return direccionService.obtenerTodasZonas();
    }

    public EnvioDTO crearEnvio(UsuarioDTO usuarioDTO, DireccionDTO direccionOrigenDTO,
                               DireccionDTO direccionDestinoDTO, double peso, double volumen,
                               List<String> serviciosExtras, String tipoEnvio, MetodoPagoDTO metodoPagoDTO) {
        return envioService.crearEnvio(usuarioDTO, direccionOrigenDTO, direccionDestinoDTO, peso, volumen, serviciosExtras, tipoEnvio, metodoPagoDTO);
    }

    public List<EnvioDTO> getEnviosPorUsuario(UsuarioDTO usuarioDTO) {
        return envioService.getEnviosPorUsuario(usuarioDTO);
    }

    public double calcularCostoEstimado(DireccionDTO origenDTO, DireccionDTO destinoDTO, double peso, double volumen, List<String> serviciosExtras, String tipoEnvio, MetodoPagoDTO metodoPagoDTO) {
        return envioService.calcularCostoEstimado(origenDTO, destinoDTO, peso, volumen,
                serviciosExtras, tipoEnvio, metodoPagoDTO);
    }

    public boolean cancelarEnvio(String idEnvio) {
        return envioService.cancelarEnvio(idEnvio);
    }

    public String obtenerEstadisticasEnvios(UsuarioDTO usuarioDTO) {
        return envioService.obtenerEstadisticasEnvios(usuarioDTO);
    }

    public boolean agregarDireccionFrecuente(UsuarioDTO usuarioDTO, DireccionDTO nuevaDireccionDTO) {
        return direccionService.agregarDireccionFrecuente(usuarioDTO, nuevaDireccionDTO);
    }

    public boolean eliminarDireccionFrecuente(UsuarioDTO usuarioDTO, DireccionDTO direccionAEliminarDTO) {
        return direccionService.eliminarDireccionFrecuente(usuarioDTO, direccionAEliminarDTO);
    }

    // --- OPERACIONES PARA ADMINISTRADOR ---
    public boolean asignarEnvioARepartidor(String idEnvio, String idRepartidor) {
        return adminService.asignarEnvioARepartidor(idEnvio, idRepartidor);
    }

    public boolean iniciarRutaEnvio(String idEnvio) {
        return adminService.iniciarRutaEnvio(idEnvio);
    }

    public boolean entregarEnvio(String idEnvio) {
        return adminService.entregarEnvio(idEnvio);
    }

    public boolean reportarIncidenciaEnvio(String idEnvio) {
        return adminService.reportarIncidenciaEnvio(idEnvio);
    }

    public List<RepartidorDTO> listarRepartidores() {
        return adminService.listarRepartidores();
    }

    public List<RepartidorDTO> listarRepartidoresDisponibles() {
        return adminService.listarRepartidoresDisponibles();
    }

    public boolean crearRepartidor(RepartidorDTO repartidorDTO) {
        return adminService.crearRepartidor(repartidorDTO);
    }

    // CORREGIDO: Usar String en lugar del enum eliminado
    public List<EnvioDTO> getEnviosPorEstado(String estado) {
        return adminService.getEnviosFiltradosPorEstado(estado);
    }

    public List<UsuarioDTO> listarUsuarios() {
        return adminService.listarUsuarios();
    }

    public boolean actualizarUsuario(UsuarioDTO usuarioDTO) {
        return adminService.actualizarUsuario(usuarioDTO);
    }

    public boolean eliminarUsuario(String idUsuario) {
        return adminService.eliminarUsuario(idUsuario);
    }

    public List<EnvioDTO> getTodosLosEnvios() {
        return adminService.getTodosLosEnvios();
    }

    public String obtenerEstadisticasGenerales() {
        return adminService.obtenerEstadisticasGenerales();
    }

    // --- MÉTODOS DE UTILIDAD ---
    public boolean existeUsuarioConCorreo(String correo) {
        return gestorDatos.buscarUsuarioPorCorreo(correo) != null;
    }

    public boolean validarCredencialesAdmin(String usuario, String contrasena) {
        return "admin".equals(usuario) && "admin123".equals(contrasena);
    }

    public void cambiarEstrategiaCalculoTarifa(CalculadoraTarifaStrategy nuevaEstrategia) {
        envioService.setCStrategy(nuevaEstrategia);
    }




}