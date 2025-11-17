package proyecto_final_pp.ViewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import proyecto_final_pp.model.dto.UsuarioDTO;
import proyecto_final_pp.Service.AdminService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GestionUsuariosController {

    @FXML private TextField txtIdUsuario;
    @FXML private TextField txtNombreUsuario;
    @FXML private TextField txtCorreoUsuario;
    @FXML private TextField txtTelefonoUsuario;
    @FXML private Button btnAgregarUsuario;
    @FXML private Button btnActualizarUsuario;
    @FXML private Button btnLimpiarFormulario;
    @FXML private TableView<UsuarioDTO> tablaUsuarios;
    @FXML private TableColumn<UsuarioDTO, String> colIdUsuario;
    @FXML private TableColumn<UsuarioDTO, String> colNombreUsuario;
    @FXML private TableColumn<UsuarioDTO, String> colCorreoUsuario;
    @FXML private TableColumn<UsuarioDTO, String> colTelefonoUsuario;
    @FXML private TableColumn<UsuarioDTO, Void> colAccionesUsuario;

    private AdminService adminService;
    private ObservableList<UsuarioDTO> usuariosObservable;

    @FXML
    public void initialize() {
        this.adminService = new AdminService();
        this.usuariosObservable = FXCollections.observableArrayList();

        // Configurar columnas de la tabla
        configurarTabla();

        // Cargar usuarios iniciales
        cargarUsuarios();


    }

    private void configurarTabla() {
        colIdUsuario.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNombreUsuario.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreoUsuario.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colTelefonoUsuario.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        // Configurar columna de acciones con botones estilizados
        colAccionesUsuario.setCellFactory(param -> new AccionesUsuarioCell(this));

        tablaUsuarios.setItems(usuariosObservable);

        // Añadir estilo a la tabla
        tablaUsuarios.setStyle("-fx-background-radius: 10; -fx-border-radius: 10;");
    }

    private void configurarEstilosBotones() {
        // Estilo para botones principales (Agregar y Actualizar)
        String estiloBotonPrincipal = "-fx-background-color: #2b2b2b; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-pref-width: 120; -fx-pref-height: 45; -fx-font-size: 14px; -fx-background-radius: 12; " +
                "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(43,43,43,0.3), 8, 0, 0, 2);";

        // Estilo para botón secundario (Limpiar)
        String estiloBotonSecundario = "-fx-background-color: transparent; -fx-text-fill: #7a7a7a; " +
                "-fx-border-color: #e9ecef; -fx-border-width: 2; -fx-pref-width: 120; -fx-pref-height: 45; " +
                "-fx-font-size: 14px; -fx-background-radius: 10; -fx-border-radius: 10; -fx-cursor: hand;";

        btnAgregarUsuario.setStyle(estiloBotonPrincipal);
        btnActualizarUsuario.setStyle(estiloBotonPrincipal);

    }

    @FXML
    private void cargarUsuarios() {
        try {
            List<UsuarioDTO> usuarios = adminService.listarUsuarios();
            usuariosObservable.setAll(usuarios);

            // Actualizar estadísticas si es necesario
            actualizarEstadisticas();

        } catch (Exception e) {
            mostrarError("Error al cargar los usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void agregarUsuario() {
        if (!validarCampos()) {
            return;
        }

        String nombre = txtNombreUsuario.getText().trim();
        String correo = txtCorreoUsuario.getText().trim();
        String telefono = txtTelefonoUsuario.getText().trim();

        // Crear DTO
        UsuarioDTO nuevoUsuario = new UsuarioDTO(null, nombre, correo, telefono, null, null, null);

        try {
            boolean creado = adminService.crearUsuario(nuevoUsuario);

            if (creado) {
                mostrarExito("✅ Usuario agregado exitosamente.");
                limpiarFormulario();
                cargarUsuarios();
            } else {
                mostrarError("❌ No se pudo agregar el usuario. Verifique los datos.");
            }
        } catch (Exception e) {
            mostrarError("❌ Error al agregar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void actualizarUsuario() {
        String idUsuario = txtIdUsuario.getText().trim();

        if (idUsuario.isEmpty()) {
            mostrarError("⚠️ Seleccione un usuario para editar.");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        String nombre = txtNombreUsuario.getText().trim();
        String correo = txtCorreoUsuario.getText().trim();
        String telefono = txtTelefonoUsuario.getText().trim();

        UsuarioDTO usuarioActualizado = new UsuarioDTO(idUsuario, nombre, correo, telefono, null, null, null);

        try {
            boolean actualizado = adminService.actualizarUsuario(usuarioActualizado);

            if (actualizado) {
                mostrarExito("✅ Usuario actualizado exitosamente.");
                limpiarFormulario();
                cargarUsuarios();
            } else {
                mostrarError("❌ No se pudo actualizar el usuario.");
            }
        } catch (Exception e) {
            mostrarError("❌ Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        String nombre = txtNombreUsuario.getText().trim();
        String correo = txtCorreoUsuario.getText().trim();
        String telefono = txtTelefonoUsuario.getText().trim();

        if (nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty()) {
            mostrarError("⚠️ Todos los campos son obligatorios.");
            return false;
        }

        // Validación básica de email
        if (!correo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            mostrarError("⚠️ Por favor ingrese un correo electrónico válido.");
            return false;
        }

        return true;
    }

    // Método para manejar la edición desde la celda de la tabla
    public void editarUsuario(UsuarioDTO usuario) {
        txtIdUsuario.setText(usuario.getIdUsuario());
        txtNombreUsuario.setText(usuario.getNombre());
        txtCorreoUsuario.setText(usuario.getCorreo());
        txtTelefonoUsuario.setText(usuario.getTelefono());

        // Cambiar el foco al primer campo
        txtNombreUsuario.requestFocus();
    }

    // Método para manejar la eliminación desde la celda de la tabla
    public void eliminarUsuario(UsuarioDTO usuario) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro de que desea eliminar este usuario?");
        confirmacion.setContentText("ID: " + usuario.getIdUsuario() + "\nNombre: " + usuario.getNombre() +
                "\n\n⚠️ Esta acción no se puede deshacer.");

        // Personalizar botones
        ButtonType btnSi = new ButtonType("✅ Sí, eliminar");
        ButtonType btnCancelar = new ButtonType("❌ Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(btnSi, btnCancelar);

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnSi) {
            try {
                boolean eliminado = adminService.eliminarUsuario(usuario.getIdUsuario());
                if (eliminado) {
                    mostrarExito("✅ Usuario eliminado exitosamente.");
                    cargarUsuarios();
                } else {
                    mostrarError("❌ No se pudo eliminar el usuario.");
                }
            } catch (Exception e) {
                mostrarError("❌ Error al eliminar usuario: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtIdUsuario.clear();
        txtNombreUsuario.clear();
        txtCorreoUsuario.clear();
        txtTelefonoUsuario.clear();
        txtNombreUsuario.requestFocus();
    }

    @FXML
    private void volverAlMenuPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminMain.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) txtNombreUsuario.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panel de Administrador - Urban Express");
            stage.centerOnScreen();
        } catch (IOException e) {
            mostrarError("❌ Error al cargar el panel principal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarEstadisticas() {
        // Aquí puedes añadir lógica para actualizar estadísticas si es necesario
        System.out.println("Total de usuarios cargados: " + usuariosObservable.size());
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operación Exitosa");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        // Añadir estilo personalizado
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; -fx-border-color: #27ae60; -fx-border-width: 2; " +
                "-fx-background-radius: 10; -fx-border-radius: 10;");

        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        // Añadir estilo personalizado
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; -fx-border-color: #e74c3c; -fx-border-width: 2; " +
                "-fx-background-radius: 10; -fx-border-radius: 10;");

        alert.showAndWait();
    }

    // Clase interna para la celda de acciones con botones estilizados
    private class AccionesUsuarioCell extends TableCell<UsuarioDTO, Void> {
        private final GestionUsuariosController controller;
        private final HBox contenedorBotones;
        private final Button btnEditar;
        private final Button btnEliminar;

        public AccionesUsuarioCell(GestionUsuariosController controller) {
            this.controller = controller;
            this.contenedorBotones = new HBox(5);
            this.contenedorBotones.setStyle("-fx-alignment: CENTER;");

            // Configurar botón Editar
            this.btnEditar = new Button("✏️");
            btnEditar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-min-width: 40; -fx-min-height: 30; -fx-background-radius: 8; " +
                    "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 4, 0, 0, 1);");
            btnEditar.setTooltip(new Tooltip("Editar usuario"));

            // Configurar botón Eliminar
            this.btnEliminar = new Button("🗑️");
            btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-min-width: 40; -fx-min-height: 30; -fx-background-radius: 8; " +
                    "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(231,76,60,0.3), 4, 0, 0, 1);");
            btnEliminar.setTooltip(new Tooltip("Eliminar usuario"));

            // Asignar eventos
            btnEditar.setOnAction(event -> {
                UsuarioDTO usuario = getTableView().getItems().get(getIndex());
                controller.editarUsuario(usuario);
            });

            btnEliminar.setOnAction(event -> {
                UsuarioDTO usuario = getTableView().getItems().get(getIndex());
                controller.eliminarUsuario(usuario);
            });

            contenedorBotones.getChildren().addAll(btnEditar, btnEliminar);
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(contenedorBotones);
            }
        }
    }
}