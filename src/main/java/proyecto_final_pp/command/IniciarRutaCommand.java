package proyecto_final_pp.command;

import proyecto_final_pp.Service.EnvioService;

// Comando concreto para iniciar la ruta de un envío
public class IniciarRutaCommand implements Command {
    private final EnvioService envioService;
    private final String idEnvio;

    public IniciarRutaCommand(EnvioService envioService, String idEnvio) {
        this.envioService = envioService;
        this.idEnvio = idEnvio;
    }

    @Override
    public void execute() {
        System.out.println("Ejecutando comando: Iniciar ruta para envío " + idEnvio);
        boolean exito = envioService.iniciarRutaEnvio(idEnvio);
        if (exito) {
            System.out.println("Envío " + idEnvio + " inició ruta correctamente.");
        } else {
            System.err.println("Fallo al iniciar ruta para envío " + idEnvio);
        }
    }
}