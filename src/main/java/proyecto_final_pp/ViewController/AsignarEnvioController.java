package proyecto_final_pp.ViewController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import proyecto_final_pp.facade.LogisticaFacade;

import java.io.IOException;

public class AsignarEnvioController {

    @FXML private TextField txtIdEnvio;
    @FXML private TextField txtIdRepartidor;
    @FXML private Button btnAsignar;

    private LogisticaFacade logisticaFacade;

    @FXML
    public void initialize() {
        this.logisticaFacade = new LogisticaFacade();

        // Debug para verificar que los elementos se inyectaron correctamente
        System.out.println("=== INICIALIZANDO ASIGNAR ENVIO CONTROLLER ===");
        System.out.println("txtIdEnvio: " + (txtIdEnvio != null ? "OK" : "NULL"));
        System.out.println("txtIdRepartidor: " + (txtIdRepartidor != null ? "OK" : "NULL"));
        System.out.println("btnAsignar: " + (btnAsignar != null ? "OK" : "NULL"));
    }

    @FXML
    private void asignarEnvio() {
        try {
            String idEnvio = txtIdEnvio.getText().trim();
            String idRepartidor = txtIdRepartidor.getText().trim();

            // Validaciones
            if (idEnvio.isEmpty()) {
                mostrarError("Por favor, ingrese el ID del envío.");
                txtIdEnvio.requestFocus();
                return;
            }

            if (idRepartidor.isEmpty()) {
                mostrarError("Por favor, ingrese el ID del repartidor.");
                txtIdRepartidor.requestFocus();
                return;
            }

            // Verificar formato de IDs
            if (!idEnvio.startsWith("ENV_")) {
                mostrarError("Formato de ID de envío inválido. Debe comenzar con 'ENV_'");
                txtIdEnvio.requestFocus();
                return;
            }

            if (!idRepartidor.startsWith("R")) {
                mostrarError("Formato de ID de repartidor inválido. Debe comenzar con 'R'");
                txtIdRepartidor.requestFocus();
                return;
            }

            // Llamar a la fachada para asignar el envío
            boolean asignado = logisticaFacade.asignarEnvioARepartidor(idEnvio, idRepartidor);

            if (asignado) {
                mostrarExito("✅ Envío asignado exitosamente\n\n" +
                        "ID Envío: " + idEnvio + "\n" +
                        "Repartidor: " + idRepartidor + "\n\n" +
                        "El envío ha sido asignado y está en camino.");
                limpiarFormulario();
            } else {
                mostrarError("❌ No se pudo asignar el envío\n\n" +
                        "Posibles causas:\n" +
                        "• El envío no existe\n" +
                        "• El repartidor no existe\n" +
                        "• El envío ya está asignado\n" +
                        "• El repartidor no está disponible");
            }

        } catch (Exception e) {
            mostrarError("Error inesperado al asignar envío: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void volverAlMenuPrincipal() {
        try {
            // RUTA CORREGIDA
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminMain.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = getCurrentStage();
            if (stage != null) {
                stage.setScene(scene);
                stage.setTitle("Panel de Administrador - Urban Express");
                stage.centerOnScreen();
            } else {
                mostrarError("No se pudo obtener la ventana actual.");
            }
        } catch (IOException e) {
            mostrarError("Error al cargar ventana principal de admin: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        txtIdEnvio.clear();
        txtIdRepartidor.clear();
        txtIdEnvio.requestFocus();
    }

    // Método seguro para obtener el Stage actual
    private Stage getCurrentStage() {
        try {
            if (btnAsignar != null && btnAsignar.getScene() != null) {
                return (Stage) btnAsignar.getScene().getWindow();
            }
            if (txtIdEnvio != null && txtIdEnvio.getScene() != null) {
                return (Stage) txtIdEnvio.getScene().getWindow();
            }
            if (txtIdRepartidor != null && txtIdRepartidor.getScene() != null) {
                return (Stage) txtIdRepartidor.getScene().getWindow();
            }

            System.err.println("No se pudo obtener el Stage actual");
            return null;
        } catch (Exception e) {
            System.err.println("Error al obtener Stage: " + e.getMessage());
            return null;
        }
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("✅ Asignación Exitosa");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("❌ Error en Asignación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}