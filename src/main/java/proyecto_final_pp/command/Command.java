package proyecto_final_pp.command;

// Interfaz base para todos los comandos
public interface Command {
    void execute();
    // Opcional para deshacer: boolean undo();
}