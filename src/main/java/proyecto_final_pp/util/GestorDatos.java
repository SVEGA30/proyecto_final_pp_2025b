package proyecto_final_pp.util;

import proyecto_final_pp.model.dto.UsuarioDTO;
import proyecto_final_pp.model.dto.EnvioDTO;
import proyecto_final_pp.model.dto.DireccionDTO;
import proyecto_final_pp.model.dto.MetodoPagoDTO;
import proyecto_final_pp.model.dto.ZonaDTO;
import proyecto_final_pp.model.dto.AdministradorDTO;
import proyecto_final_pp.model.dto.RepartidorDTO;
import proyecto_final_pp.model.Usuario;
import proyecto_final_pp.model.Envio;
import proyecto_final_pp.model.Direccion;
import proyecto_final_pp.model.MetodoPago;
import proyecto_final_pp.model.Zona;
import proyecto_final_pp.model.Administrador;
import proyecto_final_pp.model.Repartidor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GestorDatos {
    private static GestorDatos instance;
    private List<Usuario> usuarios;
    private List<Envio> envios;
    private List<Zona> zonas;
    private List<Repartidor> repartidores;
    private AtomicInteger contadorUsuarios;
    private AtomicInteger contadorRepartidores;
    private AtomicInteger contadorEnvios;
    private Administrador administrador;

    private GestorDatos() {
        this.usuarios = new ArrayList<>();
        this.envios = new ArrayList<>();
        this.zonas = new ArrayList<>();
        this.repartidores = new ArrayList<>();
        this.contadorUsuarios = new AtomicInteger(1);
        this.contadorRepartidores = new AtomicInteger(1);
        this.contadorEnvios = new AtomicInteger(1);
        this.administrador = new Administrador("admin", "admin123");
        cargarDatosIniciales();
    }

    public static GestorDatos getInstance() {
        if (instance == null) {
            instance = new GestorDatos();
        }
        return instance;
    }

    private void cargarDatosIniciales() {
        try {
            // Cargar zonas
            Zona zona1 = new Zona("Chapinero");
            Zona zona2 = new Zona("Usaquén");
            Zona zona3 = new Zona("Suba");
            Zona zona4 = new Zona("Teusaquillo");
            this.zonas.add(zona1);
            this.zonas.add(zona2);
            this.zonas.add(zona3);
            this.zonas.add(zona4);

            // Cargar usuarios de prueba
            Usuario usuario1 = new Usuario("K000001", "Juan Pérez", "juan@gmail.com", "1234567890", "1234");
            usuario1.agregarDireccion(new Direccion("Calle 123 #45-67", "Bogotá", zona1.getNombre(), "Casa"));
            usuario1.agregarDireccion(new Direccion("Avenida 456 #78-90", "Bogotá", zona2.getNombre(), "Oficina"));
            usuario1.agregarMetodoPago("Tarjeta Crédito");
            usuarios.add(usuario1);

            Usuario usuario2 = new Usuario("K000002", "María García", "maria@gmail.com", "0987654321", "1234");
            usuario2.agregarDireccion(new Direccion("Carrera 789 #12-34", "Bogotá", zona3.getNombre(), "Casa"));
            usuario2.agregarMetodoPago("Nequi");
            usuarios.add(usuario2);

            Usuario usuario3 = new Usuario("K000003", "Carlos López", "carlos@gmail.com", "5551234567", "1234");
            usuario3.agregarDireccion(new Direccion("Diagonal 234 #56-78", "Bogotá", zona4.getNombre(), "Casa"));
            usuario3.agregarMetodoPago("DaviPlata");
            usuarios.add(usuario3);

            // Cargar repartidores de prueba
            Repartidor repartidor1 = new Repartidor("R000001", "Carlos Rodríguez", "12345678", "3001234567", "Chapinero");
            Repartidor repartidor2 = new Repartidor("R000002", "Ana Gómez", "87654321", "3007654321", "Usaquén");
            Repartidor repartidor3 = new Repartidor("R000003", "Luis Martínez", "11223344", "3001122334", "Suba");
            this.repartidores.add(repartidor1);
            this.repartidores.add(repartidor2);
            this.repartidores.add(repartidor3);

            // Cargar envíos de prueba
            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime fechaEntrega1 = ahora.minusDays(2);
            LocalDateTime fechaEntrega2 = ahora.minusDays(1);
            LocalDateTime fechaSolicitado = ahora.plusDays(1);
            LocalDateTime fechaIncidencia = ahora.minusHours(12);

            Envio e1 = crearEnvioEntregado("ENV_000001", usuario1, usuario2, fechaEntrega1.minusHours(2), fechaEntrega1);
            this.envios.add(e1);

            Envio e2 = crearEnvioEntregado("ENV_000002", usuario2, usuario3, fechaEntrega2.minusHours(1), fechaEntrega2);
            e2.setCosto(e2.getCosto() + 3000);
            e2.getServiciosExtras().add("SEGURO");
            this.envios.add(e2);

            Envio e3 = new Envio("ENV_000003", usuario3.getIdUsuario(),
                    usuario3.getDirecciones().get(0), usuario1.getDirecciones().get(0),
                    1.5, 0.008, 9000.0, "EXPRESS", List.of("FRAGIL"));
            e3.setFechaCreacion(fechaSolicitado);
            this.envios.add(e3);

            Envio e4 = crearEnvioConIncidencia("ENV_000004", usuario1, usuario3, fechaIncidencia.minusHours(1), fechaIncidencia);
            this.envios.add(e4);

        } catch (Exception e) {
            System.err.println("Error al cargar datos iniciales: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Envio crearEnvioEntregado(String id, Usuario usuario, Usuario usuarioDestino, LocalDateTime fechaCreacion, LocalDateTime fechaEntrega) {
        Envio envio = new Envio(id, usuario.getIdUsuario(), usuario.getDirecciones().get(0),
                usuarioDestino.getDirecciones().get(0), 2.0, 0.01, 12000.0, "ESTANDAR", List.of("SEGURO"));
        envio.setFechaCreacion(fechaCreacion);
        envio.asignarARepartidor("R000001");
        envio.iniciarRuta();
        envio.entregar();
        return envio;
    }

    private Envio crearEnvioConIncidencia(String id, Usuario usuario, Usuario usuarioDestino, LocalDateTime fechaCreacion, LocalDateTime fechaIncidencia) {
        Envio envio = new Envio(id, usuario.getIdUsuario(), usuario.getDirecciones().get(0),
                usuarioDestino.getDirecciones().get(0), 3.0, 0.015, 15000.0, "ESTANDAR", List.of("EMBALAJE_ESPECIAL"));
        envio.setFechaCreacion(fechaCreacion);
        envio.asignarARepartidor("R000002");
        envio.iniciarRuta();
        envio.reportarIncidencia();
        return envio;
    }

    // --- MÉTODOS DE USUARIO ---
    public UsuarioDTO buscarUsuarioPorCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) return null;
        Usuario usuario = usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo.trim()))
                .findFirst()
                .orElse(null);
        return usuario != null ? usuario.toDTO() : null;
    }

    public boolean agregarUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioDTO == null || buscarUsuarioPorCorreo(usuarioDTO.getCorreo()) != null) return false;
        Usuario usuario = Usuario.fromDTO(usuarioDTO);
        if (usuario.getIdUsuario() == null || usuario.getIdUsuario().isEmpty()) {
            String id = "K" + String.format("%06d", contadorUsuarios.getAndIncrement());
            usuario.setIdUsuario(id);
        }
        usuarios.add(usuario);
        return true;
    }

    public boolean actualizarUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioDTO == null) return false;
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getIdUsuario().equals(usuarioDTO.getIdUsuario())) {
                Usuario usuarioActualizado = Usuario.fromDTO(usuarioDTO);
                usuarios.set(i, usuarioActualizado);
                return true;
            }
        }
        return false;
    }

    public boolean eliminarUsuario(String idUsuario) {
        return usuarios.removeIf(usuario -> usuario.getIdUsuario().equals(idUsuario));
    }

    public List<UsuarioDTO> getAllUsuarios() {
        return usuarios.stream().map(Usuario::toDTO).collect(Collectors.toList());
    }

    // --- MÉTODOS DE ENVÍO ---
    public EnvioDTO buscarEnvioPorId(String idEnvio) {
        if (idEnvio == null || idEnvio.trim().isEmpty()) return null;
        Envio envio = envios.stream()
                .filter(e -> e.getIdEnvio().equals(idEnvio))
                .findFirst()
                .orElse(null);
        return envio != null ? envio.toDTOSimplified() : null;
    }

    public Envio getEnvioModeloPorId(String idEnvio) {
        return envios.stream()
                .filter(e -> e.getIdEnvio().equals(idEnvio))
                .findFirst()
                .orElse(null);
    }

    public boolean agregarEnvio(EnvioDTO envioDTO) {
        if (envioDTO == null) {
            System.err.println("❌ Intento de agregar envío nulo");
            return false;
        }

        // ✅ Asegurar que el estado no sea nulo
        if (envioDTO.getEstadoActual() == null) {
            envioDTO.setEstadoActual(EnvioDTO.EstadoEnvio.SOLICITADO);
        }

        // ✅ Generar ID automático si no viene
        String idEnvio = envioDTO.getIdEnvio();
        if (idEnvio == null || idEnvio.trim().isEmpty()) {
            idEnvio = "ENV_" + String.format("%06d", contadorEnvios.getAndIncrement());
            envioDTO.setIdEnvio(idEnvio);
            System.out.println("🆕 ID generado automáticamente: " + idEnvio);
        }

        // ✅ Evitar duplicados
        if (buscarEnvioPorId(idEnvio) != null) {
            System.err.println("❌ Ya existe un envío con ID: " + idEnvio);
            return false;
        }

        // ✅ Crear modelo a partir del DTO
        Envio modeloEnvio = Envio.fromDTO(envioDTO);
        if (modeloEnvio == null) {
            System.err.println("❌ No se pudo convertir EnvioDTO a modelo Envio");
            return false;
        }

        // ✅ Guardar
        envios.add(modeloEnvio);
        System.out.println("✅ Envío guardado con ID: " + idEnvio);
        return true;
    }

    public List<EnvioDTO> getEnviosPorUsuario(String idUsuario) {
        if (idUsuario == null || idUsuario.trim().isEmpty()) return new ArrayList<>();
        return envios.stream()
                .filter(envio -> envio.getIdUsuario().equals(idUsuario))
                .map(Envio::toDTOSimplified)
                .collect(Collectors.toList());
    }

    public List<EnvioDTO> getEnviosPorUsuarioDTO(UsuarioDTO usuarioDTO) {
        if (usuarioDTO == null || usuarioDTO.getIdUsuario() == null) return new ArrayList<>();
        return getEnviosPorUsuario(usuarioDTO.getIdUsuario());
    }

    public List<EnvioDTO> getEnviosPorEstado(String estado) {
        if (estado == null) return new ArrayList<>();
        return envios.stream()
                .filter(envio -> envio.getEstadoActual().getNombre().equals(estado))
                .map(Envio::toDTOSimplified)
                .collect(Collectors.toList());
    }

    public boolean actualizarEstadoEnvio(String idEnvio, String nuevoEstado) {
        Envio envio = getEnvioModeloPorId(idEnvio);
        if (envio != null) {
            try {
                switch (nuevoEstado.toUpperCase()) {
                    case "INCIDENCIA":
                        envio.reportarIncidencia();
                        return true;
                    case "ASIGNADO":
                        // Necesitaríamos el ID del repartidor para asignar
                        return false;
                    case "EN_RUTA":
                        envio.iniciarRuta();
                        return true;
                    case "ENTREGADO":
                        envio.entregar();
                        return true;
                    case "CANCELADO":
                        envio.cancelar();
                        return true;
                    default:
                        return false;
                }
            } catch (IllegalStateException e) {
                System.err.println("Error al cambiar estado: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    public boolean actualizarEnvio(Envio envioActualizado) {
        if (envioActualizado == null || envioActualizado.getIdEnvio() == null) return false;
        for (int i = 0; i < envios.size(); i++) {
            if (envios.get(i).getIdEnvio().equals(envioActualizado.getIdEnvio())) {
                envios.set(i, envioActualizado);
                return true;
            }
        }
        return false;
    }

    public boolean puedeCancelarEnvio(String idEnvio) {
        Envio envio = getEnvioModeloPorId(idEnvio);
        return envio != null && envio.getEstadoActual().puedeCancelar();
    }

    // --- MÉTODOS DE DIRECCIÓN ---
    public List<DireccionDTO> getDireccionesUsuario(String idUsuario) {
        Usuario usuario = usuarios.stream()
                .filter(u -> u.getIdUsuario().equals(idUsuario))
                .findFirst()
                .orElse(null);
        if (usuario != null) {
            return usuario.getDirecciones().stream()
                    .map(Direccion::toDTO)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public boolean agregarDireccionFrecuente(UsuarioDTO usuarioDTO, DireccionDTO nuevaDireccionDTO) {
        if (usuarioDTO == null || nuevaDireccionDTO == null) return false;

        // Buscar el usuario en la lista
        for (Usuario usuario : usuarios) {
            if (usuario.getIdUsuario().equals(usuarioDTO.getIdUsuario())) {
                Direccion modeloDireccion = Direccion.fromDTO(nuevaDireccionDTO);
                usuario.agregarDireccion(modeloDireccion);
                return true;
            }
        }
        return false;
    }

    public boolean eliminarDireccionFrecuente(UsuarioDTO usuarioDTO, DireccionDTO direccionAEliminarDTO) {
        if (usuarioDTO == null || direccionAEliminarDTO == null) return false;

        for (Usuario usuario : usuarios) {
            if (usuario.getIdUsuario().equals(usuarioDTO.getIdUsuario())) {
                boolean removido = usuario.getDirecciones().removeIf(
                        d -> d.getIdDireccion().equals(direccionAEliminarDTO.getIdDireccion())
                );
                return removido;
            }
        }
        return false;
    }

    // --- MÉTODOS DE ZONA ---
    public List<ZonaDTO> getAllZonas() {
        return zonas.stream().map(Zona::toDTO).collect(Collectors.toList());
    }

    // --- MÉTODOS DE REPARTIDOR ---
    public List<RepartidorDTO> getAllRepartidores() {
        return repartidores.stream().map(Repartidor::toDTO).collect(Collectors.toList());
    }

    public boolean agregarRepartidor(RepartidorDTO repartidorDTO) {
        if (repartidorDTO == null) return false;
        Repartidor repartidor = Repartidor.fromDTO(repartidorDTO);
        if (repartidor.getIdRepartidor() == null || repartidor.getIdRepartidor().isEmpty()) {
            String id = "R" + String.format("%06d", contadorRepartidores.getAndIncrement());
            repartidor.setIdRepartidor(id);
        }
        repartidores.add(repartidor);
        return true;
    }

    public boolean actualizarRepartidor(RepartidorDTO repartidorDTO) {
        if (repartidorDTO == null) return false;
        for (int i = 0; i < repartidores.size(); i++) {
            if (repartidores.get(i).getIdRepartidor().equals(repartidorDTO.getIdRepartidor())) {
                Repartidor repartidorActualizado = Repartidor.fromDTO(repartidorDTO);
                repartidores.set(i, repartidorActualizado);
                return true;
            }
        }
        return false;
    }

    public boolean eliminarRepartidor(String idRepartidor) {
        return repartidores.removeIf(repartidor -> repartidor.getIdRepartidor().equals(idRepartidor));
    }

    public boolean actualizarDisponibilidadRepartidor(String idRepartidor, String nuevaDisponibilidad) {
        Repartidor repartidor = getRepartidorModeloPorId(idRepartidor);
        if (repartidor != null) {
            try {
                Repartidor.EstadoDisponibilidad estado = Repartidor.EstadoDisponibilidad.valueOf(nuevaDisponibilidad);
                repartidor.setDisponibilidad(estado);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }

    public RepartidorDTO buscarRepartidorPorId(String idRepartidor) {
        Repartidor repartidor = getRepartidorModeloPorId(idRepartidor);
        return repartidor != null ? repartidor.toDTO() : null;
    }

    public List<RepartidorDTO> getRepartidoresDisponibles() {
        return repartidores.stream()
                .filter(Repartidor::estaDisponible)
                .map(Repartidor::toDTO)
                .collect(Collectors.toList());
    }

    public Repartidor getRepartidorModeloPorId(String idRepartidor) {
        return repartidores.stream()
                .filter(r -> r.getIdRepartidor().equals(idRepartidor))
                .findFirst()
                .orElse(null);
    }

    // --- MÉTODO DE ADMINISTRADOR ---
    public AdministradorDTO getAdministrador() {
        return administrador != null ? administrador.toDTO() : null;
    }

    // --- MÉTODOS PARA REPORTES ---
    public List<Envio> getEnvios() { return new ArrayList<>(envios); }
    public List<Usuario> getUsuarios() { return new ArrayList<>(usuarios); }
    public List<Zona> getZonas() { return new ArrayList<>(zonas); }
    public List<Repartidor> getRepartidores() { return new ArrayList<>(repartidores); }

    public List<Envio> getEnviosFiltradosPorFecha(LocalDate desde, LocalDate hasta) {
        return envios.stream()
                .filter(e -> !e.getFechaCreacion().toLocalDate().isBefore(desde) &&
                        !e.getFechaCreacion().toLocalDate().isAfter(hasta))
                .collect(Collectors.toList());
    }

    public List<Envio> getEnviosEntregadosFiltradosPorFecha(LocalDate desde, LocalDate hasta) {
        return getEnviosFiltradosPorFecha(desde, hasta).stream()
                .filter(e -> e.getEstadoActual().getNombre().equals("ENTREGADO"))
                .collect(Collectors.toList());
    }

    public List<Envio> getEnviosConIncidenciaFiltradosPorFecha(LocalDate desde, LocalDate hasta) {
        return getEnviosFiltradosPorFecha(desde, hasta).stream()
                .filter(e -> e.getEstadoActual().getNombre().equals("INCIDENCIA"))
                .collect(Collectors.toList());
    }

    public Map<String, Long> getConteoEnviosPorZonaFiltradosPorFecha(LocalDate desde, LocalDate hasta) {
        return getEnviosFiltradosPorFecha(desde, hasta).stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDireccionDestino().getZona(),
                        Collectors.counting()
                ));
    }

    public Map<String, Double> getIngresosPorServicioAdicionalFiltradosPorFecha(LocalDate desde, LocalDate hasta) {
        return getEnviosFiltradosPorFecha(desde, hasta).stream()
                .flatMap(e -> e.getServiciosExtras().stream())
                .collect(Collectors.groupingBy(
                        servicio -> servicio,
                        Collectors.summingDouble(servicio -> {
                            switch (servicio.toUpperCase()) {
                                case "SEGURO": return 3000.0;
                                case "FRAGIL": return 2000.0;
                                case "FIRMA_REQUERIDA": return 1000.0;
                                case "EMBALAJE_ESPECIAL": return 1500.0;
                                default: return 0.0;
                            }
                        })
                ));
    }

    public Map<String, Long> getTopIncidenciasFiltradasPorFecha(LocalDate desde, LocalDate hasta) {
        return getEnviosConIncidenciaFiltradosPorFecha(desde, hasta).stream()
                .collect(Collectors.groupingBy(
                        e -> "Reporte de Incidencia",
                        Collectors.counting()
                ));
    }

    public double getTiempoPromedioEntregaFiltradoPorZonaFiltradosPorFecha(LocalDate desde, LocalDate hasta, String zona) {
        List<Envio> enviosEntregados = getEnviosEntregadosFiltradosPorFecha(desde, hasta);
        List<Envio> enviosDeZona = enviosEntregados.stream()
                .filter(e -> e.getDireccionDestino().getZona().equals(zona))
                .collect(Collectors.toList());

        if (enviosDeZona.isEmpty()) return 0.0;

        long totalHoras = enviosDeZona.stream()
                .mapToLong(e -> ChronoUnit.HOURS.between(e.getFechaCreacion(), e.getFechaActualizacionEstado()))
                .sum();

        return (double) totalHoras / enviosDeZona.size();
    }

    public String obtenerEstadisticas() {
        return String.format("Sistema: %d usuarios, %d repartidores, %d envíos",
                usuarios.size(), repartidores.size(), envios.size());
    }
}