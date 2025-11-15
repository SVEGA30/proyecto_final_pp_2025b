package proyecto_final_pp.ViewController;

import proyecto_final_pp.model.dto.UsuarioDTO;
import proyecto_final_pp.facade.LogisticaFacade;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

public class UsuarioMainController {

    @FXML private Label lblBienvenida;
    @FXML private Label lblInfoUsuario;
    @FXML private Label lblIdUsuario;

    private UsuarioDTO usuarioActualDTO;
    private LogisticaFacade logisticaFacade;

    @FXML
    public void initialize() {
        try {
            this.logisticaFacade = new LogisticaFacade();
            System.out.println("UsuarioMainController inicializado correctamente");

        } catch (Exception e) {
            System.err.println("Error al inicializar UsuarioMainController: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setUsuarioActual(UsuarioDTO usuarioDTO) {
        try {
            this.usuarioActualDTO = usuarioDTO;
            actualizarInterfazUsuario();

        } catch (Exception e) {
            System.err.println("Error en setUsuarioActual: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarInterfazUsuario() {
        try {
            // Actualizar la interfaz con los datos del usuario
            if (lblBienvenida != null && usuarioActualDTO != null && usuarioActualDTO.getNombre() != null) {
                lblBienvenida.setText("¡Bienvenido, " + usuarioActualDTO.getNombre() + "!");
            } else {
                System.err.println("lblBienvenida es null o usuarioDTO es inválido");
                if (lblBienvenida != null) {
                    lblBienvenida.setText("¡Bienvenido!");
                }
            }

            if (lblInfoUsuario != null) {
                lblInfoUsuario.setText("Gestiona tus envíos de forma rápida y eficiente");
            }

            if (lblIdUsuario != null && usuarioActualDTO != null && usuarioActualDTO.getIdUsuario() != null) {
                lblIdUsuario.setText("ID: " + usuarioActualDTO.getIdUsuario());
            } else {
                System.err.println("lblIdUsuario es null o usuarioDTO es inválido");
                if (lblIdUsuario != null) {
                    lblIdUsuario.setText("ID: ---");
                }
            }

        } catch (Exception e) {
            System.err.println("Error al actualizar interfaz: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void crearNuevoEnvio() {
        cargarVentana("/view/Envio.fxml", "Crear Nuevo Envío - Urban Express");
    }

    @FXML
    private void verMisEnvios() {
        cargarVentana("/view/HistorialEnvios.fxml", "Mis Envíos - Urban Express");
    }

    @FXML
    private void verHistorial() {
        cargarVentana("/view/HistorialEnvios.fxml", "Historial de Envíos - Urban Express");
    }

    @FXML
    private void gestionarDirecciones() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GestionDirecciones.fxml"));
            Parent root = loader.load();

            // OBTENER EL CONTROLADOR ESPECÍFICAMENTE PARA GESTIÓN DE DIRECCIONES
            GestionDireccionesController controller = loader.getController();
            if (controller != null) {
                System.out.println("Estableciendo usuario en GestionDireccionesController: " +
                        (usuarioActualDTO != null ? usuarioActualDTO.getNombre() : "null"));
                controller.setUsuarioActual(usuarioActualDTO);
            } else {
                System.err.println("No se pudo obtener el controlador de GestionDirecciones");
            }

            Scene scene = new Scene(root);
            Stage stage = getCurrentStage();
            if (stage != null) {
                stage.setScene(scene);
                stage.setTitle("Gestionar Direcciones - Urban Express");
                stage.centerOnScreen();
            } else {
                mostrarError("No se pudo obtener la ventana actual");
            }
        } catch (IOException e) {
            mostrarError("Error al cargar gestión de direcciones: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarSesion() {
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
                mostrarError("No se pudo obtener la ventana actual");
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
    private void actualizarEstadisticasBtn() {
        mostrarInfo("Información actualizada correctamente");
        actualizarInterfazUsuario();
    }

    // Método genérico para cargar ventanas
    private void cargarVentana(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Intentar establecer el usuario actual en el controlador de destino
            try {
                Object controller = loader.getController();
                if (controller != null) {
                    java.lang.reflect.Method setUsuarioMethod = controller.getClass().getMethod("setUsuarioActual", UsuarioDTO.class);
                    if (setUsuarioMethod != null) {
                        setUsuarioMethod.invoke(controller, usuarioActualDTO);
                    }
                }
            } catch (Exception e) {
                System.out.println("El controlador de " + fxmlPath + " no tiene método setUsuarioActual");
            }

            Scene scene = new Scene(root);
            Stage stage = getCurrentStage();
            if (stage != null) {
                stage.setScene(scene);
                stage.setTitle(titulo);
                stage.centerOnScreen();
            } else {
                mostrarError("No se pudo obtener la ventana actual");
            }
        } catch (IOException e) {
            mostrarError("Error al cargar " + titulo + ": " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método auxiliar mejorado para obtener el Stage actual
    private Stage getCurrentStage() {
        try {
            // Intentar con cada label en orden
            if (lblBienvenida != null && lblBienvenida.getScene() != null) {
                return (Stage) lblBienvenida.getScene().getWindow();
            }
            if (lblInfoUsuario != null && lblInfoUsuario.getScene() != null) {
                return (Stage) lblInfoUsuario.getScene().getWindow();
            }
            if (lblIdUsuario != null && lblIdUsuario.getScene() != null) {
                return (Stage) lblIdUsuario.getScene().getWindow();
            }

            // Si llegamos aquí, buscar cualquier nodo de la escena actual
            // Esto es más seguro que lanzar una excepción
            System.err.println("No se pudo obtener el Stage actual de los labels");
            return null;

        } catch (Exception e) {
            System.err.println("Error al obtener el Stage actual: " + e.getMessage());
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

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Getter para el usuario actual
    public UsuarioDTO getUsuarioActual() {
        return usuarioActualDTO;
    }
}