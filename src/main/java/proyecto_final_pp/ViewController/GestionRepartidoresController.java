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
import proyecto_final_pp.model.Repartidor;
import proyecto_final_pp.model.dto.RepartidorDTO;
import proyecto_final_pp.Service.AdminService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GestionRepartidoresController {

    @FXML private TextField txtIdRepartidor;
    @FXML private TextField txtNombreRepartidor;
    @FXML private TextField txtDocumentoRepartidor;
    @FXML private TextField txtTelefonoRepartidor;
    @FXML private TextField txtZonaCobertura;
    @FXML private ComboBox<String> cbDisponibilidadRepartidor;
    @FXML private Button btnAgregarRepartidor;
    @FXML private Button btnActualizarRepartidor;
    @FXML private Button btnLimpiarFormulario;
    @FXML private TableView<RepartidorDTO> tablaRepartidores;
    @FXML private TableColumn<RepartidorDTO, String> colIdRepartidor;
    @FXML private TableColumn<RepartidorDTO, String> colNombreRepartidor;
    @FXML private TableColumn<RepartidorDTO, String> colDocumentoRepartidor;
    @FXML private TableColumn<RepartidorDTO, String> colTelefonoRepartidor;
    @FXML private TableColumn<RepartidorDTO, String> colZonaCobertura;
    @FXML private TableColumn<RepartidorDTO, String> colDisponibilidadRepartidor;
    @FXML private TableColumn<RepartidorDTO, Void> colAccionesRepartidor;

    private AdminService adminService;
    private ObservableList<RepartidorDTO> repartidoresObservable;

    @FXML
    public void initialize() {
        this.adminService = new AdminService();
        this.repartidoresObservable = FXCollections.observableArrayList();

        // Configurar columnas de la tabla
        colIdRepartidor.setCellValueFactory(new PropertyValueFactory<>("idRepartidor"));
        colNombreRepartidor.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDocumentoRepartidor.setCellValueFactory(new PropertyValueFactory<>("documento"));
        colTelefonoRepartidor.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colZonaCobertura.setCellValueFactory(new PropertyValueFactory<>("zonaCobertura"));
        colDisponibilidadRepartidor.setCellValueFactory(new PropertyValueFactory<>("disponibilidad"));

        // Configurar ComboBox de disponibilidad
        cbDisponibilidadRepartidor.getItems().addAll(
                Repartidor.EstadoDisponibilidad.ACTIVO.name(),
                Repartidor.EstadoDisponibilidad.INACTIVO.name(),
                Repartidor.EstadoDisponibilidad.EN_RUTA.name()
        );

        // Configurar columna de acciones (Editar/Eliminar)
        colAccionesRepartidor.setCellFactory(param -> new AccionesRepartidorCell(this));

        tablaRepartidores.setItems(repartidoresObservable);

        // Cargar repartidores iniciales
        cargarRepartidores();

        // Configurar botones inicialmente
        btnActualizarRepartidor.setDisable(true);
    }

    private void cargarRepartidores() {
        try {
            List<RepartidorDTO> repartidores = adminService.listarRepartidores();
            repartidoresObservable.setAll(repartidores);
        } catch (Exception e) {
            mostrarError("Error al cargar repartidores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void agregarRepartidor() {
        try {
            String nombre = txtNombreRepartidor.getText().trim();
            String documento = txtDocumentoRepartidor.getText().trim();
            String telefono = txtTelefonoRepartidor.getText().trim();
            String zonaCobertura = txtZonaCobertura.getText().trim();
            String disponibilidadStr = cbDisponibilidadRepartidor.getValue();

            if (nombre.isEmpty() || documento.isEmpty() || telefono.isEmpty() || zonaCobertura.isEmpty() || disponibilidadStr == null) {
                mostrarError("Todos los campos son obligatorios.");
                return;
            }

            // Validar formato de teléfono
            if (!telefono.matches("\\d{10}")) {
                mostrarError("El teléfono debe tener 10 dígitos.");
                return;
            }

            // Crear DTO
            RepartidorDTO nuevoRepartidor = new RepartidorDTO(null, nombre, documento, telefono, disponibilidadStr, zonaCobertura);

            // Llamar a AdminService para crear
            boolean creado = adminService.crearRepartidor(nuevoRepartidor);

            if (creado) {
                mostrarExito("Repartidor agregado exitosamente.");
                limpiarFormulario();
                cargarRepartidores();
            } else {
                mostrarError("No se pudo agregar el repartidor. Verifique los datos.");
            }
        } catch (Exception e) {
            mostrarError("Error al agregar repartidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void actualizarRepartidor() {
        try {
            String idRepartidor = txtIdRepartidor.getText().trim();
            String nombre = txtNombreRepartidor.getText().trim();
            String documento = txtDocumentoRepartidor.getText().trim();
            String telefono = txtTelefonoRepartidor.getText().trim();
            String zonaCobertura = txtZonaCobertura.getText().trim();
            String disponibilidadStr = cbDisponibilidadRepartidor.getValue();

            if (idRepartidor.isEmpty() || nombre.isEmpty() || documento.isEmpty() || telefono.isEmpty() || zonaCobertura.isEmpty() || disponibilidadStr == null) {
                mostrarError("Seleccione un repartidor y complete todos los campos.");
                return;
            }

            // Crear DTO con ID
            RepartidorDTO repartidorActualizado = new RepartidorDTO(idRepartidor, nombre, documento, telefono, disponibilidadStr, zonaCobertura);

            // Llamar a AdminService para actualizar
            boolean actualizado = adminService.actualizarRepartidor(repartidorActualizado);

            if (actualizado) {
                mostrarExito("Repartidor actualizado exitosamente.");
                limpiarFormulario();
                cargarRepartidores();
            } else {
                mostrarError("No se pudo actualizar el repartidor.");
            }
        } catch (Exception e) {
            mostrarError("Error al actualizar repartidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para manejar la edición desde la celda de la tabla
    public void editarRepartidor(RepartidorDTO repartidor) {
        txtIdRepartidor.setText(repartidor.getIdRepartidor());
        txtNombreRepartidor.setText(repartidor.getNombre());
        txtDocumentoRepartidor.setText(repartidor.getDocumento());
        txtTelefonoRepartidor.setText(repartidor.getTelefono());
        txtZonaCobertura.setText(repartidor.getZonaCobertura());
        cbDisponibilidadRepartidor.setValue(repartidor.getDisponibilidad());

        // Habilitar botón de actualizar y deshabilitar agregar
        btnActualizarRepartidor.setDisable(false);
        btnAgregarRepartidor.setDisable(true);
    }

    // Método para manejar la eliminación desde la celda de la tabla
    public void eliminarRepartidor(RepartidorDTO repartidor) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro de que desea eliminar este repartidor?");
        confirmacion.setContentText("ID: " + repartidor.getIdRepartidor() + "\nNombre: " + repartidor.getNombre());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean eliminado = adminService.eliminarRepartidor(repartidor.getIdRepartidor());
                if (eliminado) {
                    mostrarExito("Repartidor eliminado exitosamente.");
                    cargarRepartidores();
                } else {
                    mostrarError("No se pudo eliminar el repartidor.");
                }
            } catch (Exception e) {
                mostrarError("Error al eliminar repartidor: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtIdRepartidor.clear();
        txtNombreRepartidor.clear();
        txtDocumentoRepartidor.clear();
        txtTelefonoRepartidor.clear();
        txtZonaCobertura.clear();
        cbDisponibilidadRepartidor.setValue(null);

        // Restablecer botones
        btnActualizarRepartidor.setDisable(true);
        btnAgregarRepartidor.setDisable(false);
    }

    @FXML
    private void volverAlMenuPrincipal() {
        try {
            // CORRECCIÓN: Ruta corregida
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminMain.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) btnAgregarRepartidor.getScene().getWindow();
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
    private class AccionesRepartidorCell extends TableCell<RepartidorDTO, Void> {
        private final GestionRepartidoresController controller;
        private final Button btnEditar;
        private final Button btnEliminar;
        private final HBox botonesContainer;

        public AccionesRepartidorCell(GestionRepartidoresController controller) {
            this.controller = controller;
            this.btnEditar = new Button("Editar");
            this.btnEliminar = new Button("Eliminar");

            // Estilos de botones
            btnEditar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 11px;");
            btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 11px;");

            btnEditar.setOnAction(event -> {
                RepartidorDTO repartidor = getTableView().getItems().get(getIndex());
                controller.editarRepartidor(repartidor);
            });
            btnEliminar.setOnAction(event -> {
                RepartidorDTO repartidor = getTableView().getItems().get(getIndex());
                controller.eliminarRepartidor(repartidor);
            });

            this.botonesContainer = new HBox(5, btnEditar, btnEliminar);
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(botonesContainer);
            }
        }
    }
}