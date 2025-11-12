package proyecto_final_pp.observer;

import proyecto_final_pp.model.Envio;
import proyecto_final_pp.model.Usuario;

// Observador concreto: Notifica al usuario sobre cambios en su envío
public class UsuarioNotificacionObserver implements EnvioObserver {
    private final Usuario usuario;

    public UsuarioNotificacionObserver(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public void actualizar(Envio envio) {
        // Este método se llama automáticamente cuando el envío cambia de estado
        // Aquí se implementaría la lógica de notificación (email, SMS, push, etc.)
        // Por ahora, simulamos la notificación por consola

        // CORRECCIÓN: Usar getNombre() del estado actual (que es una instancia de EstadoEnvio)
        System.out.println("*** NOTIFICACIÓN ENVIADA A USUARIO " + usuario.getNombre() + " (" + usuario.getCorreo() + ") ***");
        System.out.println("El estado de su envío " + envio.getIdEnvio() + " ha cambiado a: " + envio.getEstadoActual().getNombre());
        System.out.println("Fecha de actualización: " + envio.getFechaActualizacionEstado());

        // Información adicional según el estado
        String informacionAdicional = obtenerInformacionAdicional(envio);
        if (!informacionAdicional.isEmpty()) {
            System.out.println("Información adicional: " + informacionAdicional);
        }

        System.out.println("*********************************************************************");

        // Aquí podrías agregar lógica para enviar email, SMS, etc.
        enviarNotificacionReal(envio);
    }

    private String obtenerInformacionAdicional(Envio envio) {
        String estado = envio.getEstadoActual().getNombre();
        switch (estado) {
            case "ASIGNADO":
                return "Repartidor asignado: " + (envio.getRepartidorAsignadoId() != null ? envio.getRepartidorAsignadoId() : "Por asignar");
            case "EN_RUTA":
                return "Su paquete está en camino al destino";
            case "ENTREGADO":
                return "¡Paquete entregado satisfactoriamente!";
            case "INCIDENCIA":
                return "Se ha reportado una incidencia. Contacte al soporte.";
            default:
                return "";
        }
    }

    private void enviarNotificacionReal(Envio envio) {
        // Simulación de envío de notificación por diferentes canales
        // En un sistema real, aquí se integraría con servicios de email, SMS, push, etc.

        try {
            // Simular envío de email
            System.out.println("[SIMULACIÓN] Enviando email a: " + usuario.getCorreo());
            System.out.println("[SIMULACIÓN] Asunto: Actualización de envío " + envio.getIdEnvio());
            System.out.println("[SIMULACIÓN] Cuerpo: Su envío ahora está en estado: " + envio.getEstadoActual().getNombre());

            // Simular notificación push (si la aplicación móvil estuviera implementada)
            System.out.println("[SIMULACIÓN] Notificación push enviada");

        } catch (Exception e) {
            System.err.println("Error al enviar notificación: " + e.getMessage());
        }
    }

    // Método para obtener el usuario asociado a este observador
    public Usuario getUsuario() {
        return usuario;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UsuarioNotificacionObserver that = (UsuarioNotificacionObserver) obj;
        return usuario != null ? usuario.getIdUsuario().equals(that.usuario.getIdUsuario()) : that.usuario == null;
    }

    @Override
    public int hashCode() {
        return usuario != null ? usuario.getIdUsuario().hashCode() : 0;
    }
}