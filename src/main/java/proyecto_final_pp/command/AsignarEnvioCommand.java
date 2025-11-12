package proyecto_final_pp.command;

import proyecto_final_pp.Service.EnvioService;

// Comando concreto para asignar un envío a un repartidor
public class AsignarEnvioCommand implements Command {
    private final EnvioService envioService;
    private final String idEnvio;
    private final String idRepartidor;

    public AsignarEnvioCommand(EnvioService envioService, String idEnvio, String idRepartidor) {
        this.envioService = envioService;
        this.idEnvio = idEnvio;
        this.idRepartidor = idRepartidor;
    }

    @Override
    public void execute() {
        System.out.println("Ejecutando comando: Asignar envío " + idEnvio + " al repartidor " + idRepartidor);
        boolean exito = envioService.asignarEnvioARepartidor(idEnvio, idRepartidor);
        if (exito) {
            System.out.println("Envío " + idEnvio + " asignado correctamente al repartidor " + idRepartidor);
        } else {
            System.err.println("Fallo al asignar envío " + idEnvio + " al repartidor " + idRepartidor);
        }
    }
}