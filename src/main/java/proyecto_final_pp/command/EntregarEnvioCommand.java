package proyecto_final_pp.command;

import proyecto_final_pp.Service.EnvioService;

// Comando concreto para entregar un envío
public class EntregarEnvioCommand implements Command {
    private final EnvioService envioService;
    private final String idEnvio;

    public EntregarEnvioCommand(EnvioService envioService, String idEnvio) {
        this.envioService = envioService;
        this.idEnvio = idEnvio;
    }

    @Override
    public void execute() {
        System.out.println("Ejecutando comando: Entregar envío " + idEnvio);
        boolean exito = envioService.entregarEnvio(idEnvio);
        if (exito) {
            System.out.println("Envío " + idEnvio + " entregado correctamente.");
        } else {
            System.err.println("Fallo al entregar envío " + idEnvio);
        }
    }
}