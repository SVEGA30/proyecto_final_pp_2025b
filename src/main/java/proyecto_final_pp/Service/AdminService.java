package proyecto_final_pp.Service;

import proyecto_final_pp.model.dto.UsuarioDTO;
import proyecto_final_pp.model.dto.EnvioDTO;
import proyecto_final_pp.model.dto.DireccionDTO;
import proyecto_final_pp.model.dto.ZonaDTO;
import proyecto_final_pp.model.dto.RepartidorDTO;
import proyecto_final_pp.model.Envio;
import proyecto_final_pp.model.Repartidor;
import proyecto_final_pp.strategy.CalculadoraTarifaStrategy;
import proyecto_final_pp.strategy.CalculadoraTarifaBasica;
import proyecto_final_pp.util.GestorDatos;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminService {

    private final GestorDatos gestorDatos;
    private final EnvioService envioService;

    public AdminService() {
        this.gestorDatos = GestorDatos.getInstance();
        CalculadoraTarifaStrategy estrategiaPorDefecto = new CalculadoraTarifaBasica();
        this.envioService = new EnvioService(estrategiaPorDefecto);
    }

    public AdminService(CalculadoraTarifaStrategy estrategia) {
        this.gestorDatos = GestorDatos.getInstance();
        this.envioService = new EnvioService(estrategia);
    }

    // Gestionar usuarios
    public boolean crearUsuario(UsuarioDTO usuarioDTO) {
        return gestorDatos.agregarUsuario(usuarioDTO);
    }

    public boolean actualizarUsuario(UsuarioDTO usuarioDTO) {
        return gestorDatos.actualizarUsuario(usuarioDTO);
    }

    public boolean eliminarUsuario(String idUsuario) {
        return gestorDatos.eliminarUsuario(idUsuario);
    }

    public List<UsuarioDTO> listarUsuarios() {
        return gestorDatos.getAllUsuarios();
    }

    // Gestionar repartidores y su disponibilidad
    public boolean crearRepartidor(RepartidorDTO repartidorDTO) {
        return gestorDatos.agregarRepartidor(repartidorDTO);
    }

    public boolean actualizarRepartidor(RepartidorDTO repartidorDTO) {
        return gestorDatos.actualizarRepartidor(repartidorDTO);
    }

    public boolean eliminarRepartidor(String idRepartidor) {
        return gestorDatos.eliminarRepartidor(idRepartidor);
    }

    public List<RepartidorDTO> listarRepartidores() {
        return gestorDatos.getAllRepartidores();
    }

    public List<RepartidorDTO> listarRepartidoresDisponibles() {
        return gestorDatos.getRepartidoresDisponibles();
    }

    public boolean actualizarDisponibilidadRepartidor(String idRepartidor, String nuevaDisponibilidad) {
        return gestorDatos.actualizarDisponibilidadRepartidor(idRepartidor, nuevaDisponibilidad);
    }

    //Asignar/enviar/incidencias
    public boolean asignarEnvioARepartidor(String idEnvio, String idRepartidor) {
        Envio envioModelo = gestorDatos.getEnvioModeloPorId(idEnvio);
        if (envioModelo != null) {
            try {
                envioModelo.asignarARepartidor(idRepartidor);
                return true;
            } catch (IllegalStateException e) {
                System.err.println("No se puede asignar envío " + idEnvio + ": " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    public boolean iniciarRutaEnvio(String idEnvio) {
        Envio envioModelo = gestorDatos.getEnvioModeloPorId(idEnvio);
        if (envioModelo != null) {
            try {
                envioModelo.iniciarRuta();
                return true;
            } catch (IllegalStateException e) {
                System.err.println("No se puede iniciar ruta envío " + idEnvio + ": " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    public boolean entregarEnvio(String idEnvio) {
        Envio envioModelo = gestorDatos.getEnvioModeloPorId(idEnvio);
        if (envioModelo != null) {
            try {
                envioModelo.entregar();
                return true;
            } catch (IllegalStateException e) {
                System.err.println("No se puede entregar envío " + idEnvio + ": " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    public boolean reportarIncidenciaEnvio(String idEnvio) {
        Envio envioModelo = gestorDatos.getEnvioModeloPorId(idEnvio);
        if (envioModelo != null) {
            try {
                envioModelo.reportarIncidencia();
                return true;
            } catch (IllegalStateException e) {
                System.err.println("No se puede reportar incidencia envío " + idEnvio + ": " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    //Panel de métricas
    public double getTiempoPromedioEntregaFiltradoPorZona(LocalDate desde, LocalDate hasta, String zona) {
        return gestorDatos.getTiempoPromedioEntregaFiltradoPorZonaFiltradosPorFecha(desde, hasta, zona);
    }

    public Map<String, Long> getConteoServiciosAdicionalesMasUsados(LocalDate desde, LocalDate hasta) {
        return gestorDatos.getEnviosFiltradosPorFecha(desde, hasta).stream()
                .flatMap(e -> e.getServiciosExtras().stream())
                .collect(Collectors.groupingBy(servicio -> servicio, Collectors.counting()));
    }

    public double getIngresosTotalesPorPeriodo(LocalDate desde, LocalDate hasta) {
        return gestorDatos.getEnviosEntregadosFiltradosPorFecha(desde, hasta).stream()
                .mapToDouble(Envio::getCosto)
                .sum();
    }

    public Map<String, Long> getConteoIncidenciasPorZona(LocalDate desde, LocalDate hasta) {
        return gestorDatos.getEnviosConIncidenciaFiltradosPorFecha(desde, hasta).stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDireccionDestino().getZona(),
                        Collectors.counting()
                ));
    }

    public Map<String, Long> getConteoEnviosPorEstado(LocalDate desde, LocalDate hasta) {
        return gestorDatos.getEnviosFiltradosPorFecha(desde, hasta).stream()
                .collect(Collectors.groupingBy(
                        e -> e.getEstadoActual().getNombre(),
                        Collectors.counting()
                ));
    }

    //Métodos para obtener ingresos por servicios adicionales
    public Map<String, Double> getIngresosPorServicioAdicional(LocalDate desde, LocalDate hasta) {
        return gestorDatos.getIngresosPorServicioAdicionalFiltradosPorFecha(desde, hasta);
    }

    public Map<String, Long> getTopIncidencias(LocalDate desde, LocalDate hasta) {
        return gestorDatos.getTopIncidenciasFiltradasPorFecha(desde, hasta);
    }

    //Otros métodos de utilidad para admin
    public List<EnvioDTO> getTodosLosEnvios() {
        return gestorDatos.getEnvios().stream()
                .map(Envio::toDTOSimplified)
                .collect(Collectors.toList());
    }

    public List<EnvioDTO> getEnviosFiltradosPorFecha(LocalDate desde, LocalDate hasta) {
        return gestorDatos.getEnviosFiltradosPorFecha(desde, hasta).stream()
                .map(Envio::toDTOSimplified)
                .collect(Collectors.toList());
    }

    public List<EnvioDTO> getEnviosFiltradosPorEstado(String estado) {
        return gestorDatos.getEnviosPorEstado(estado);
    }

    public List<ZonaDTO> getTodasLasZonas() {
        return gestorDatos.getAllZonas();
    }

    public String obtenerEstadisticasGenerales() {
        int totalUsuarios = listarUsuarios().size();
        int totalEnvios = getTodosLosEnvios().size();
        int totalRepartidores = listarRepartidores().size();

        long enviosEntregados = gestorDatos.getEnvios().stream()
                .filter(e -> "ENTREGADO".equals(e.getEstadoActual().getNombre()))
                .count();

        long enviosConIncidencia = gestorDatos.getEnvios().stream()
                .filter(e -> "INCIDENCIA".equals(e.getEstadoActual().getNombre()))
                .count();

        return String.format(
                "Estadísticas Generales:\n" +
                        "Total Usuarios: %d\n" +
                        "Total Envíos: %d\n" +
                        "Total Repartidores: %d\n" +
                        "Envíos Entregados: %d\n" +
                        "Envíos con Incidencia: %d",
                totalUsuarios, totalEnvios, totalRepartidores, enviosEntregados, enviosConIncidencia
        );
    }

    public void cambiarEstrategiaCalculoTarifa(CalculadoraTarifaStrategy nuevaEstrategia) {
        envioService.setCStrategy(nuevaEstrategia);
    }

    public double calcularCostoEstimado(DireccionDTO origenDTO, DireccionDTO destinoDTO,
                                        double peso, double volumen, List<String> serviciosExtras,
                                        String tipoEnvio) {
        return envioService.calcularCostoEstimado(origenDTO, destinoDTO, peso, volumen, serviciosExtras, tipoEnvio);
    }

    public boolean cancelarEnvio(String idEnvio) {
        return envioService.cancelarEnvio(idEnvio);
    }

    public List<EnvioDTO> getEnviosPorUsuario(UsuarioDTO usuarioDTO) {
        return envioService.getEnviosPorUsuario(usuarioDTO);
    }
}