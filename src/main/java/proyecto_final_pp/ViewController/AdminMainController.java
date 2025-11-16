package proyecto_final_pp.ViewController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import proyecto_final_pp.facade.LogisticaFacade;

public class AdminMainController {

    @FXML private Label lblBienvenidaAdmin;
    @FXML private Label lblEstadisticas;

    private LogisticaFacade logisticaFacade;

    @FXML
    public void initialize() {
        this.logisticaFacade = new LogisticaFacade();
        System.out.println("AdminMainController inicializado");
        lblBienvenidaAdmin.setText("Panel de Administrador - Sistema de Logística");
        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        String estadisticas = logisticaFacade.obtenerEstadisticasGenerales();
        lblEstadisticas.setText(estadisticas);
    }

    @FXML
    private void navegarAGestionUsuarios() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GestionUsuarios.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) lblBienvenidaAdmin.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestión de Usuarios - Urban Express");
        } catch (Exception e) {
            mostrarError("Error al cargar gestión de usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void navegarAGestionRepartidores() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GestionRepartidores.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) lblBienvenidaAdmin.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestión de Repartidores - Urban Express");
        } catch (Exception e) {
            mostrarError("Error al cargar gestión de repartidores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void navegarAReportes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Reportes.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) lblBienvenidaAdmin.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Reportes - Urban Express");
        } catch (Exception e) {
            mostrarError("Error al cargar reportes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void navegarAEnviosPendientes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EnvioPendientes.fxml"));
            Parent root = loader.load();
            EnviosPendientesController controller = loader.getController();
            controller.cargarEnviosPendientes();

            Scene scene = new Scene(root);
            Stage stage = (Stage) lblBienvenidaAdmin.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Envíos Pendientes - Urban Express");
        } catch (Exception e) {
            mostrarError("Error al cargar envíos pendientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void navegarATodosLosEnvios() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TodosLosEnvios.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) lblBienvenidaAdmin.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Todos los Envíos - Urban Express");
        } catch (Exception e) {
            mostrarError("Error al cargar todos los envíos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void actualizarEstadisticasView() {
        actualizarEstadisticas();
        mostrarInfo("Estadísticas actualizadas");
    }

    @FXML
    private void cerrarSesion() {
        System.out.println("Cerrando sesión de administrador...");
        Stage stage = (Stage) lblBienvenidaAdmin.getScene().getWindow();
        stage.close();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("Plataforma de Logística - Inicio de Sesión");
            loginStage.show();
        } catch (Exception e) {
            mostrarError("Error al cargar ventana de login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarInfo(String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}