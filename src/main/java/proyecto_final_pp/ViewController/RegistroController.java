package proyecto_final_pp.ViewController;

import proyecto_final_pp.model.dto.UsuarioDTO;
import proyecto_final_pp.model.dto.DireccionDTO;
import proyecto_final_pp.facade.LogisticaFacade;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegistroController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtAliasDireccion;
    @FXML private ComboBox<String> cbMetodoPago;
    @FXML private Label mensajeLabel;
    @FXML private Button btnRegistrar;

    private LogisticaFacade logisticaFacade;

    @FXML
    public void initialize() {
        try {
            this.logisticaFacade = new LogisticaFacade();

            // Debug: Verificar que los elementos FXML se inyectaron
            System.out.println("=== INICIALIZANDO REGISTRO CONTROLLER ===");
            System.out.println("cbMetodoPago: " + (cbMetodoPago != null ? "OK" : "NULL"));
            System.out.println("txtNombre: " + (txtNombre != null ? "OK" : "NULL"));

            // Cargar métodos de pago
            if (cbMetodoPago != null) {
                cbMetodoPago.setItems(FXCollections.observableArrayList(
                        "Tarjeta Crédito", "Nequi", "DaviPlata", "Efecty"
                ));
            } else {
                System.err.println("ERROR: cbMetodoPago es null - verificar FXML");
            }

        } catch (Exception e) {
            System.err.println("Error en initialize de RegistroController: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al inicializar el formulario de registro: " + e.getMessage());
        }
    }

    @FXML
    private void registrarUsuario() {
        try {
            if (!validarCampos()) {
                return;
            }

            String nombre = txtNombre.getText().trim();
            String correo = txtCorreo.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String password = txtPassword.getText();
            String direccionCalle = txtDireccion.getText().trim();
            String aliasDireccion = txtAliasDireccion.getText().trim();
            String metodoPagoSeleccionado = cbMetodoPago.getValue();

            // Crear dirección inicial (sin zona)
            DireccionDTO direccionDTO = new DireccionDTO();
            direccionDTO.setCalle(direccionCalle);
            direccionDTO.setCiudad("Bogotá");
            direccionDTO.setAlias(aliasDireccion.isEmpty() ? "Casa" : aliasDireccion);
            // No establecer zona

            List<DireccionDTO> direcciones = new ArrayList<>();
            direcciones.add(direccionDTO);

            List<String> metodosPago = new ArrayList<>();
            metodosPago.add(metodoPagoSeleccionado);

            // Crear DTO de usuario
            UsuarioDTO nuevoUsuarioDTO = new UsuarioDTO();
            nuevoUsuarioDTO.setNombre(nombre);
            nuevoUsuarioDTO.setCorreo(correo);
            nuevoUsuarioDTO.setTelefono(telefono);
            nuevoUsuarioDTO.setContrasena(password);
            nuevoUsuarioDTO.setDirecciones(direcciones);
            nuevoUsuarioDTO.setMetodosPago(metodosPago);

            // Usar la fachada para registrar el usuario
            boolean registrado = logisticaFacade.registrarUsuario(nuevoUsuarioDTO);

            if (registrado) {
                mostrarExito("¡Usuario registrado exitosamente! Ahora puede iniciar sesión.");
                volverAlLogin();
            } else {
                mostrarError("Error: El correo ya está registrado.");
            }
        } catch (Exception e) {
            mostrarError("Error inesperado durante el registro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        if (txtNombre == null || txtNombre.getText().trim().isEmpty()) {
            mostrarError("El nombre es obligatorio");
            return false;
        }
        if (txtCorreo == null || txtCorreo.getText().trim().isEmpty()) {
            mostrarError("El correo electrónico es obligatorio");
            return false;
        }
        if (!txtCorreo.getText().trim().contains("@")) {
            mostrarError("Formato de correo inválido");
            return false;
        }
        if (txtTelefono == null || txtTelefono.getText().trim().isEmpty()) {
            mostrarError("El teléfono es obligatorio");
            return false;
        }
        if (!txtTelefono.getText().trim().matches("\\d{10}")) {
            mostrarError("Teléfono debe tener 10 dígitos");
            return false;
        }
        if (txtPassword == null || txtPassword.getText().length() < 4) {
            mostrarError("La contraseña debe tener al menos 4 caracteres");
            return false;
        }
        if (txtDireccion == null || txtDireccion.getText().trim().isEmpty()) {
            mostrarError("La dirección es obligatoria");
            return false;
        }
        if (cbMetodoPago == null || cbMetodoPago.getValue() == null) {
            mostrarError("Seleccione un método de pago");
            return false;
        }
        return true;
    }

    @FXML
    private void volverAlLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = getCurrentStage();
            if (stage != null) {
                stage.setScene(scene);
                stage.setTitle("Login - Urban Express");
                stage.centerOnScreen();
            } else {
                mostrarError("No se pudo obtener la ventana actual.");
            }
        } catch (IOException e) {
            mostrarError("Error al cargar ventana de login: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void salir() {
        System.exit(0);
    }

    // Método seguro para obtener el Stage actual
    private Stage getCurrentStage() {
        try {
            if (btnRegistrar != null && btnRegistrar.getScene() != null) {
                return (Stage) btnRegistrar.getScene().getWindow();
            }
            if (txtNombre != null && txtNombre.getScene() != null) {
                return (Stage) txtNombre.getScene().getWindow();
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
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}