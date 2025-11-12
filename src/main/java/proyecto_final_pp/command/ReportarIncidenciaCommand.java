package proyecto_final_pp.command;

import proyecto_final_pp.Service.EnvioService;

// Comando concreto para reportar una incidencia de un envío
public class ReportarIncidenciaCommand implements Command {
    private final EnvioService envioService;
    private final String idEnvio;

    public ReportarIncidenciaCommand(EnvioService envioService, String idEnvio) {
        this.envioService = envioService;
        this.idEnvio = idEnvio;
    }

    @Override
    public void execute() {
        System.out.println("Ejecutando comando: Reportar incidencia para envío " + idEnvio);
        boolean exito = envioService.reportarIncidenciaEnvio(idEnvio);
        if (exito) {
            System.out.println("Incidencia reportada correctamente para envío " + idEnvio);
        } else {
            System.err.println("Fallo al reportar incidencia para envío " + idEnvio);
        }
    }
}