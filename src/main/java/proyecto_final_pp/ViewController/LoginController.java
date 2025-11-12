package proyecto_final_pp.ViewController;

import proyecto_final_pp.model.dto.UsuarioDTO;
import proyecto_final_pp.facade.LogisticaFacade;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class LoginController {

    @FXML private ComboBox<String> perfilComboBox;
    @FXML private TextField correoField;
    @FXML private TextField usuarioAdminField;
    @FXML private PasswordField contrasenaField;
    @FXML private Label mensajeLabel;
    @FXML private Button btnIniciarSesion;
    @FXML private Label idregister;
    @FXML private VBox campoCorreoBox;
    @FXML private VBox campoUsuarioBox;

    private LogisticaFacade logisticaFacade;

    @FXML
    public void initialize() {
        try {
            this.logisticaFacade = new LogisticaFacade();

            // Debug: Verificar inyección de elementos FXML
            System.out.println("=== INICIALIZANDO LOGIN CONTROLLER ===");
            System.out.println("btnIniciarSesion: " + (btnIniciarSesion != null ? "OK" : "NULL"));
            System.out.println("perfilComboBox: " + (perfilComboBox != null ? "OK" : "NULL"));
            System.out.println("correoField: " + (correoField != null ? "OK" : "NULL"));
            System.out.println("usuarioAdminField: " + (usuarioAdminField != null ? "OK" : "NULL"));

            // Configurar ComboBox de perfiles
            if (perfilComboBox != null) {
                perfilComboBox.getItems().addAll("Usuario", "Administrador");
                perfilComboBox.setValue("Usuario");

                // Configurar listener para cambiar campos según perfil
                perfilComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                    actualizarCamposPorPerfil(newVal);
                });
            }

            // Inicializar visibilidad de campos
            actualizarCamposPorPerfil("Usuario");

            // Debug: Verificar rutas FXML
            debugFXMLPaths();

        } catch (Exception e) {
            System.err.println("Error en initialize de LoginController: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarCamposPorPerfil(String perfil) {
        if (campoCorreoBox == null || campoUsuarioBox == null) {
            System.err.println("Error: Campos FXML no inyectados correctamente");
            return;
        }

        if ("Administrador".equals(perfil)) {
            campoCorreoBox.setVisible(false);
            campoCorreoBox.setManaged(false);
            campoUsuarioBox.setVisible(true);
            campoUsuarioBox.setManaged(true);
        } else {
            campoCorreoBox.setVisible(true);
            campoCorreoBox.setManaged(true);
            campoUsuarioBox.setVisible(false);
            campoUsuarioBox.setManaged(false);
        }
    }

    @FXML
    private void iniciarSesion() {
        try {
            String perfil = perfilComboBox != null ? perfilComboBox.getValue() : "Usuario";
            String password = contrasenaField != null ? contrasenaField.getText() : "";

            if (password.isEmpty()) {
                mostrarError("Por favor, ingrese la contraseña.");
                return;
            }

            if ("Administrador".equals(perfil)) {
                // Login como administrador
                String usuario = usuarioAdminField != null ? usuarioAdminField.getText().trim() : "";
                if (usuario.isEmpty()) {
                    mostrarError("Por favor, ingrese el usuario administrador.");
                    return;
                }

                boolean credencialesValidas = logisticaFacade.validarCredencialesAdmin(usuario, password);
                if (credencialesValidas) {
                    cargarVentana("/view/AdminMain.fxml", "Panel de Administrador - Urban Express");
                } else {
                    mostrarError("Credenciales de administrador incorrectas.");
                }
            } else {
                // Login como usuario normal
                String correo = correoField != null ? correoField.getText().trim() : "";
                if (correo.isEmpty()) {
                    mostrarError("Por favor, ingrese el correo electrónico.");
                    return;
                }

                UsuarioDTO usuarioDTO = logisticaFacade.iniciarSesion(correo, password);

                if (usuarioDTO != null) {
                    cargarVentanaUsuario("/view/UsuarioMain.fxml", "Panel de Usuario - Urban Express", usuarioDTO);
                } else {
                    mostrarError("Credenciales incorrectas o usuario no encontrado.");
                }
            }
        } catch (Exception e) {
            mostrarError("Error durante el inicio de sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarVentana(String fxmlPath, String titulo) throws IOException {
        URL fxmlUrl = getClass().getResource(fxmlPath);
        if (fxmlUrl == null) {
            throw new IOException("No se pudo encontrar el archivo FXML: " + fxmlPath);
        }

        System.out.println("Cargando FXML desde: " + fxmlUrl);

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage stage = getCurrentStage();
        if (stage != null) {
            stage.setScene(scene);
            stage.setTitle(titulo);
            stage.centerOnScreen();
        } else {
            mostrarError("No se pudo obtener la ventana actual.");
        }
    }

    private void cargarVentanaUsuario(String fxmlPath, String titulo, UsuarioDTO usuarioDTO) throws IOException {
        URL fxmlUrl = getClass().getResource(fxmlPath);
        if (fxmlUrl == null) {
            throw new IOException("No se pudo encontrar el archivo FXML: " + fxmlPath);
        }

        System.out.println("Cargando FXML usuario desde: " + fxmlUrl);

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        // Configurar el controlador si es necesario
        Object controller = loader.getController();
        if (controller instanceof UsuarioMainController) {
            ((UsuarioMainController) controller).setUsuarioActual(usuarioDTO);
        }

        Scene scene = new Scene(root);
        Stage stage = getCurrentStage();
        if (stage != null) {
            stage.setScene(scene);
            stage.setTitle(titulo);
            stage.centerOnScreen();
        } else {
            mostrarError("No se pudo obtener la ventana actual.");
        }
    }

    @FXML
    private void OnRegister() {
        try {
            cargarVentana("/view/RegistroMain.fxml", "Registro - Urban Express");
        } catch (IOException e) {
            mostrarError("Error al cargar ventana de registro: " + e.getMessage());
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
            // Intentar con diferentes elementos FXML
            if (btnIniciarSesion != null && btnIniciarSesion.getScene() != null) {
                return (Stage) btnIniciarSesion.getScene().getWindow();
            }
            if (perfilComboBox != null && perfilComboBox.getScene() != null) {
                return (Stage) perfilComboBox.getScene().getWindow();
            }
            if (correoField != null && correoField.getScene() != null) {
                return (Stage) correoField.getScene().getWindow();
            }
            if (mensajeLabel != null && mensajeLabel.getScene() != null) {
                return (Stage) mensajeLabel.getScene().getWindow();
            }

            // Si no se pudo obtener de ningún elemento, mostrar error
            System.err.println("No se pudo obtener el Stage actual - todos los elementos son null o no tienen escena");
            return null;

        } catch (Exception e) {
            System.err.println("Error al obtener Stage: " + e.getMessage());
            return null;
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void debugFXMLPaths() {
        System.out.println("=== DEBUG FXML PATHS ===");

        String[] paths = {
                "/view/AdminMain.fxml",
                "/view/UsuarioMain.fxml",
                "/view/RegistroMain.fxml",
                "/view/Envio.fxml",
                "/view/GestionDirecciones.fxml",
                "/view/HistorialEnvios.fxml",
                "/view/Login.fxml"
        };

        for (String path : paths) {
            URL url = getClass().getResource(path);
            if (url != null) {
                System.out.println("✓ Encontrado: " + path);
            } else {
                System.out.println("✗ NO encontrado: " + path);
            }
        }
        System.out.println("========================");
    }
}