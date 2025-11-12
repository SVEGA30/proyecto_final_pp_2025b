package proyecto_final_pp.ViewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    @FXML private TableColumn<UsuarioDTO, Void> colAccionesUsuario; // Botones de Editar/Eliminar

    private AdminService adminService;
    private ObservableList<UsuarioDTO> usuariosObservable;

    @FXML
    public void initialize() {
        this.adminService = new AdminService();
        this.usuariosObservable = FXCollections.observableArrayList();

        // Configurar columnas de la tabla
        colIdUsuario.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNombreUsuario.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreoUsuario.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colTelefonoUsuario.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        // Configurar columna de acciones (Editar/Eliminar)
        colAccionesUsuario.setCellFactory(param -> new AccionesUsuarioCell(this));

        tablaUsuarios.setItems(usuariosObservable);

        // Cargar usuarios iniciales
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        List<UsuarioDTO> usuarios = adminService.listarUsuarios(); // Usar AdminService
        usuariosObservable.setAll(usuarios);
    }

    @FXML
    private void agregarUsuario() {
        String nombre = txtNombreUsuario.getText().trim();
        String correo = txtCorreoUsuario.getText().trim();
        String telefono = txtTelefonoUsuario.getText().trim();

        if (nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty()) {
            mostrarError("Todos los campos son obligatorios.");
            return;
        }

        // Crear DTO
        UsuarioDTO nuevoUsuario = new UsuarioDTO(null, nombre, correo, telefono, null, null, null); // Contraseña, direcciones, metodosPago pueden ser nulos o simulados para creación

        // Llamar a AdminService para crear
        boolean creado = adminService.crearUsuario(nuevoUsuario); // Usar AdminService

        if (creado) {
            mostrarExito("Usuario agregado exitosamente.");
            limpiarFormulario();
            cargarUsuarios(); // Refrescar la tabla
        } else {
            mostrarError("No se pudo agregar el usuario. Verifique los datos.");
        }
    }

    @FXML
    private void actualizarUsuario() {
        String idUsuario = txtIdUsuario.getText().trim();
        String nombre = txtNombreUsuario.getText().trim();
        String correo = txtCorreoUsuario.getText().trim();
        String telefono = txtTelefonoUsuario.getText().trim();

        if (idUsuario.isEmpty() || nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty()) {
            mostrarError("Seleccione un usuario y complete todos los campos.");
            return;
        }

        // Crear DTO con ID
        UsuarioDTO usuarioActualizado = new UsuarioDTO(idUsuario, nombre, correo, telefono, null, null, null);

        // Llamar a AdminService para actualizar
        boolean actualizado = adminService.actualizarUsuario(usuarioActualizado); // Usar AdminService

        if (actualizado) {
            mostrarExito("Usuario actualizado exitosamente.");
            limpiarFormulario();
            cargarUsuarios(); // Refrescar la tabla
        } else {
            mostrarError("No se pudo actualizar el usuario.");
        }
    }

    // Método para manejar la edición desde la celda de la tabla
    public void editarUsuario(UsuarioDTO usuario) {
        txtIdUsuario.setText(usuario.getIdUsuario());
        txtNombreUsuario.setText(usuario.getNombre());
        txtCorreoUsuario.setText(usuario.getCorreo());
        txtTelefonoUsuario.setText(usuario.getTelefono());
        // No se carga contraseña, direcciones ni métodos de pago en el formulario de gestión simple
    }

    // Método para manejar la eliminación desde la celda de la tabla
    public void eliminarUsuario(UsuarioDTO usuario) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro de que desea eliminar este usuario?");
        confirmacion.setContentText("ID: " + usuario.getIdUsuario() + "\nNombre: " + usuario.getNombre());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Llamar a AdminService para eliminar
            boolean eliminado = adminService.eliminarUsuario(usuario.getIdUsuario()); // Usar AdminService
            if (eliminado) {
                mostrarExito("Usuario eliminado exitosamente.");
                cargarUsuarios(); // Refrescar la tabla
            } else {
                mostrarError("No se pudo eliminar el usuario.");
            }
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtIdUsuario.clear();
        txtNombreUsuario.clear();
        txtCorreoUsuario.clear();
        txtTelefonoUsuario.clear();
    }

    @FXML
    private void volverAlMenuPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminMain.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) btnAgregarUsuario.getScene().getWindow(); // Obtener stage desde cualquier componente
            stage.setScene(scene);
            stage.setTitle("Panel de Administrador - Urban Express");
        } catch (IOException e) {
            mostrarError("Error al cargar ventana principal de admin: " + e.getMessage());
            e.printStackTrace();
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

    // Clase interna para la celda de acciones
    private class AccionesUsuarioCell extends TableCell<UsuarioDTO, Void> {
        private final GestionUsuariosController controller;
        private final Button btnEditar;
        private final Button btnEliminar;

        public AccionesUsuarioCell(GestionUsuariosController controller) {
            this.controller = controller;
            this.btnEditar = new Button("Editar");
            this.btnEliminar = new Button("Eliminar");
            btnEditar.setOnAction(event -> {
                UsuarioDTO usuario = getTableView().getItems().get(getIndex());
                controller.editarUsuario(usuario);
            });
            btnEliminar.setOnAction(event -> {
                UsuarioDTO usuario = getTableView().getItems().get(getIndex());
                controller.eliminarUsuario(usuario);
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(new javafx.scene.layout.HBox(btnEditar, btnEliminar)); // Agrupar botones
            }
        }
    }
}