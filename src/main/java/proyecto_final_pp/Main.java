package proyecto_final_pp;

import proyecto_final_pp.util.GestorDatos;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void init() throws Exception {
        try {
            // Cargar datos de prueba al iniciar la aplicación
            GestorDatos.getInstance();
            System.out.println("Datos de prueba cargados exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al cargar datos de prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Debug: mostrar classpath
            System.out.println("Classpath: " + System.getProperty("java.class.path"));
            
            // Intentar cargar el FXML con diferentes rutas
            URL fxmlUrl = getClass().getResource("/view/Login.fxml");
            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("/view/Login.fxml");
            }
            if (fxmlUrl == null) {
                fxmlUrl = getClass().getResource("view/Login.fxml");
            }
            if (fxmlUrl == null) {
                throw new Exception("No se pudo encontrar el archivo FXML para la vista de Login.");
            }
            System.out.println("Cargando FXML desde: " + fxmlUrl);
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            primaryStage.setTitle("Plataforma de Logística - Inicio de Sesión");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            
            System.out.println("Aplicación iniciada correctamente.");
            
        } catch (Exception e) {
            mostrarError("Error crítico al iniciar la aplicación:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Inicio");
        alert.setHeaderText("No se pudo iniciar la aplicación");
        alert.setContentText(mensaje);
        alert.showAndWait();
        
        // También imprimir en consola
        System.err.println("ERROR: " + mensaje);
    }

    public static void main(String[] args) {
        try {
            System.out.println("Iniciando aplicación de logística...");
            launch(args);
        } catch (Exception e) {
            System.err.println("Error fatal en main: " + e.getMessage());
            e.printStackTrace();
        }
    }
}