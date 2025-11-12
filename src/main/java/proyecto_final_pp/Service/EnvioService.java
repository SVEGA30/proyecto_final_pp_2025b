package proyecto_final_pp.Service;

import proyecto_final_pp.model.dto.UsuarioDTO;
import proyecto_final_pp.model.dto.EnvioDTO;
import proyecto_final_pp.model.dto.DireccionDTO;
import proyecto_final_pp.model.Envio;
import proyecto_final_pp.strategy.CalculadoraTarifaStrategy;
import proyecto_final_pp.util.GestorDatos;

import java.util.*;

public class EnvioService {
    private GestorDatos gestorDatos;
    private CalculadoraTarifaStrategy cStrategy;

    public EnvioService(CalculadoraTarifaStrategy cStrategy) {
        this.gestorDatos = GestorDatos.getInstance();
        this.cStrategy = cStrategy;
    }

    public EnvioDTO crearEnvio(UsuarioDTO usuarioDTO, DireccionDTO direccionOrigenDTO,
                               DireccionDTO direccionDestinoDTO, double peso, double volumen,
                               List<String> serviciosExtras, String tipoEnvio) {
        try {
            // Validaciones
            if (direccionOrigenDTO == null || direccionDestinoDTO == null) {
                throw new IllegalArgumentException("Las direcciones de origen y destino son requeridas");
            }
            if (sonDireccionesIguales(direccionOrigenDTO, direccionDestinoDTO)) {
                throw new IllegalArgumentException("Origen y destino no pueden ser iguales");
            }
            if (peso <= 0 || peso > 50) {
                throw new IllegalArgumentException("El peso debe estar entre 0.1 y 50 kg");
            }
            if (volumen <= 0 || volumen > 5) {
                throw new IllegalArgumentException("El volumen debe estar entre 0.001 y 5 m³");
            }

            // Calcular costo
            double costo = calcularCostoEstimado(direccionOrigenDTO, direccionDestinoDTO,
                    peso, volumen, serviciosExtras, tipoEnvio);

            // ✅ CORRECCIÓN: Generar ID y establecer estado
            EnvioDTO nuevoEnvio = new EnvioDTO();
            String idEnvio = "ENV-" + System.currentTimeMillis(); // o UUID.randomUUID().toString()
            nuevoEnvio.setIdEnvio(idEnvio);
            nuevoEnvio.setIdUsuario(usuarioDTO.getIdUsuario());
            nuevoEnvio.setDireccionOrigen(direccionOrigenDTO);
            nuevoEnvio.setDireccionDestino(direccionDestinoDTO);
            nuevoEnvio.setPeso(peso);
            nuevoEnvio.setVolumen(volumen);
            nuevoEnvio.setCosto(costo);
            nuevoEnvio.setTipoEnvio(tipoEnvio);
            nuevoEnvio.setServiciosExtras(serviciosExtras != null ? new ArrayList<>(serviciosExtras) : new ArrayList<>());
            nuevoEnvio.setEstadoActual(EnvioDTO.EstadoEnvio.SOLICITADO); // ✅ Estado inicial
            nuevoEnvio.setFechaCreacion(java.time.LocalDateTime.now());
            nuevoEnvio.setFechaActualizacionEstado(java.time.LocalDateTime.now());

            // Guardar
            if (gestorDatos.agregarEnvio(nuevoEnvio)) {
                System.out.println("✅ Envío creado con ID: " + idEnvio);
                return nuevoEnvio;
            } else {
                throw new RuntimeException("No se pudo guardar el envío en la base de datos (ID probablemente nulo o duplicado)");
            }
        } catch (Exception e) {
            System.err.println("Error al crear envío: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("No se pudo guardar el envío en la base de datos", e);
        }
    }

    private boolean sonDireccionesIguales(DireccionDTO origen, DireccionDTO destino) {
        if (origen == destino) return true;
        if (origen == null || destino == null) return false;
        return Objects.equals(origen.getCalle(), destino.getCalle()) &&
                Objects.equals(origen.getCiudad(), destino.getCiudad()) &&
                Objects.equals(origen.getZona(), destino.getZona());
    }

    public double calcularCostoEstimado(DireccionDTO origenDTO, DireccionDTO destinoDTO,
                                        double peso, double volumen, List<String> serviciosExtras,
                                        String tipoEnvio) {
        return cStrategy.calcularCosto(origenDTO, destinoDTO, peso, volumen, serviciosExtras, tipoEnvio);
    }

    public void setCStrategy(CalculadoraTarifaStrategy nuevaEstrategia) {
        this.cStrategy = nuevaEstrategia;
    }

    public List<EnvioDTO> getEnviosPorUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioDTO == null || usuarioDTO.getIdUsuario() == null) {
            return new ArrayList<>();
        }
        return gestorDatos.getEnviosPorUsuario(usuarioDTO.getIdUsuario());
    }

    public List<EnvioDTO> getEnviosPorEstado(String estado) {
        return gestorDatos.getEnviosPorEstado(estado);
    }

    public boolean cancelarEnvio(String idEnvio) {
        if (gestorDatos.puedeCancelarEnvio(idEnvio)) {
            Envio envioModelo = gestorDatos.getEnvioModeloPorId(idEnvio);
            if (envioModelo != null) {
                try {
                    envioModelo.cancelar();
                    return true;
                } catch (IllegalStateException e) {
                    System.err.println("No se puede cancelar envío " + idEnvio + ": " + e.getMessage());
                    return false;
                }
            }
        }
        return false;
    }

    public String obtenerEstadisticasEnvios(UsuarioDTO usuarioDTO) {
        if (usuarioDTO == null) return "Usuario no válido";
        List<EnvioDTO> envios = getEnviosPorUsuario(usuarioDTO);
        long total = envios.size();
        long activos = envios.stream()
                .filter(e -> e.getEstadoActual() != null &&
                        e.getEstadoActual() != EnvioDTO.EstadoEnvio.ENTREGADO &&
                        e.getEstadoActual() != EnvioDTO.EstadoEnvio.CANCELADO)
                .count();
        long entregados = envios.stream()
                .filter(e -> e.getEstadoActual() == EnvioDTO.EstadoEnvio.ENTREGADO)
                .count();
        return String.format("Envíos: %d total, %d activos, %d entregados", total, activos, entregados);
    }

    // Otros métodos (asignar, entregar, etc.) se mantienen igual
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
}