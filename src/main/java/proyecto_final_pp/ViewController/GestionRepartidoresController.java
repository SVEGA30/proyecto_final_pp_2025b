package proyecto_final_pp.ViewController;

import javafx.beans.property.SimpleStringProperty;
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
import proyecto_final_pp.model.dto.RepartidorDTO;
import proyecto_final_pp.Service.AdminService;

import java.io.IOException;
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

    @FXML private Label lblContadorRepartidores;

    private AdminService adminService;
    private ObservableList<RepartidorDTO> repartidoresObservable;

    @FXML
    public void initialize() {
        System.out.println("🔧 Inicializando GestionRepartidoresController...");

        this.adminService = new AdminService();
        this.repartidoresObservable = FXCollections.observableArrayList();

        // Configurar ComboBox de disponibilidad
        configurarComboBox();

        // Configurar tabla
        configurarTabla();

        // Cargar datos iniciales
        cargarRepartidores();

        System.out.println("✅ GestionRepartidoresController inicializado");
    }

    private void configurarComboBox() {
        System.out.println("🔧 Configurando ComboBox...");
        // Opciones de disponibilidad
        ObservableList<String> opcionesDisponibilidad = FXCollections.observableArrayList(
                "ACTIVO",
                "INACTIVO",
                "EN_RUTA",
                "DISPONIBLE",
                "NO_DISPONIBLE"
        );
        cbDisponibilidadRepartidor.setItems(opcionesDisponibilidad);
        cbDisponibilidadRepartidor.setValue("ACTIVO"); // Valor por defecto
        System.out.println("✅ ComboBox configurado con " + opcionesDisponibilidad.size() + " opciones");
    }

    private void configurarTabla() {
        System.out.println("🔧 Configurando tabla...");

        // Usar PropertyValueFactory (más robusto)
        colIdRepartidor.setCellValueFactory(new PropertyValueFactory<>("idRepartidor"));
        colNombreRepartidor.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDocumentoRepartidor.setCellValueFactory(new PropertyValueFactory<>("documento"));
        colTelefonoRepartidor.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colZonaCobertura.setCellValueFactory(new PropertyValueFactory<>("zonaCobertura"));
        colDisponibilidadRepartidor.setCellValueFactory(new PropertyValueFactory<>("disponibilidad"));

        System.out.println("✅ CellValueFactories configurados con PropertyValueFactory");

        // Configurar columna de acciones
        colAccionesRepartidor.setCellFactory(param -> new AccionesRepartidorCell());

        // Asignar datos a la tabla
        tablaRepartidores.setItems(repartidoresObservable);
    }

    @FXML
    private void cargarRepartidores() {
        try {
            System.out.println("🔄 Cargando repartidores desde AdminService...");
            List<RepartidorDTO> repartidores = adminService.listarRepartidores();
            System.out.println("📊 Repartidores obtenidos del servicio: " + repartidores.size());

            // Debug: mostrar cada repartidor
            for (RepartidorDTO rep : repartidores) {
                System.out.println("   - " + rep.getIdRepartidor() + ": " + rep.getNombre() +
                        " | Doc: " + rep.getDocumento() +
                        " | Tel: " + rep.getTelefono() +
                        " | Zona: " + rep.getZonaCobertura() +
                        " | Disp: " + rep.getDisponibilidad());
            }

            repartidoresObservable.setAll(repartidores);
            System.out.println("✅ ObservableList actualizado con " + repartidoresObservable.size() + " repartidores");


            // Verificar estado de la tabla
            System.out.println("📋 Estado tabla - Items: " + tablaRepartidores.getItems().size());
            System.out.println("📋 Estado tabla - Observable: " + repartidoresObservable.size());

        } catch (Exception e) {
            System.err.println("❌ Error al cargar los repartidores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void agregarRepartidor() {
        if (!validarCamposFormulario()) {
            return;
        }

        try {
            String nombre = txtNombreRepartidor.getText().trim();
            String documento = txtDocumentoRepartidor.getText().trim();
            String telefono = txtTelefonoRepartidor.getText().trim();
            String zonaCobertura = txtZonaCobertura.getText().trim();
            String disponibilidad = cbDisponibilidadRepartidor.getValue();

            // Crear DTO del repartidor
            RepartidorDTO nuevoRepartidor = new RepartidorDTO(
                    null, // ID se generará automáticamente
                    nombre,
                    documento,
                    telefono,
                    disponibilidad,
                    zonaCobertura
            );

            boolean creado = adminService.crearRepartidor(nuevoRepartidor);

            if (creado) {
                mostrarExito("✅ Repartidor agregado exitosamente.");
                limpiarFormulario();
                cargarRepartidores(); // Actualizar tabla
            } else {
                mostrarError("❌ No se pudo agregar el repartidor. Verifique los datos.");
            }
        } catch (Exception e) {
            mostrarError("❌ Error al agregar repartidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void actualizarRepartidor() {
        String idRepartidor = txtIdRepartidor.getText().trim();

        if (idRepartidor.isEmpty()) {
            mostrarError("⚠️ Seleccione un repartidor para editar.");
            return;
        }

        if (!validarCamposFormulario()) {
            return;
        }

        try {
            String nombre = txtNombreRepartidor.getText().trim();
            String documento = txtDocumentoRepartidor.getText().trim();
            String telefono = txtTelefonoRepartidor.getText().trim();
            String zonaCobertura = txtZonaCobertura.getText().trim();
            String disponibilidad = cbDisponibilidadRepartidor.getValue();

            RepartidorDTO repartidorActualizado = new RepartidorDTO(
                    idRepartidor,
                    nombre,
                    documento,
                    telefono,
                    disponibilidad,
                    zonaCobertura
            );

            boolean actualizado = adminService.actualizarRepartidor(repartidorActualizado);

            if (actualizado) {
                mostrarExito("✅ Repartidor actualizado exitosamente.");
                limpiarFormulario();
                cargarRepartidores(); // Actualizar tabla
            } else {
                mostrarError("❌ No se pudo actualizar el repartidor.");
            }
        } catch (Exception e) {
            mostrarError("❌ Error al actualizar repartidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void limpiarFormulario() {
        txtIdRepartidor.clear();
        txtNombreRepartidor.clear();
        txtDocumentoRepartidor.clear();
        txtTelefonoRepartidor.clear();
        txtZonaCobertura.clear();
        cbDisponibilidadRepartidor.setValue("ACTIVO");
        txtNombreRepartidor.requestFocus();
    }

    // Método para manejar la edición desde la tabla
    public void editarRepartidor(RepartidorDTO repartidor) {
        txtIdRepartidor.setText(repartidor.getIdRepartidor());
        txtNombreRepartidor.setText(repartidor.getNombre());
        txtDocumentoRepartidor.setText(repartidor.getDocumento());
        txtTelefonoRepartidor.setText(repartidor.getTelefono());
        txtZonaCobertura.setText(repartidor.getZonaCobertura());
        cbDisponibilidadRepartidor.setValue(repartidor.getDisponibilidad());

        txtNombreRepartidor.requestFocus();
    }

    // Método para manejar la eliminación desde la tabla
    public void eliminarRepartidor(RepartidorDTO repartidor) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro de que desea eliminar este repartidor?");
        confirmacion.setContentText("ID: " + repartidor.getIdRepartidor() + "\nNombre: " + repartidor.getNombre() +
                "\n\n⚠️ Esta acción no se puede deshacer.");

        // Personalizar botones
        ButtonType btnSi = new ButtonType("✅ Sí, eliminar");
        ButtonType btnCancelar = new ButtonType("❌ Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(btnSi, btnCancelar);

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnSi) {
            try {
                boolean eliminado = adminService.eliminarRepartidor(repartidor.getIdRepartidor());
                if (eliminado) {
                    mostrarExito("✅ Repartidor eliminado exitosamente.");
                    cargarRepartidores();
                } else {
                    mostrarError("❌ No se pudo eliminar el repartidor.");
                }
            } catch (Exception e) {
                mostrarError("❌ Error al eliminar repartidor: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private boolean validarCamposFormulario() {
        String nombre = txtNombreRepartidor.getText().trim();
        String documento = txtDocumentoRepartidor.getText().trim();
        String telefono = txtTelefonoRepartidor.getText().trim();
        String zonaCobertura = txtZonaCobertura.getText().trim();

        // Validar campos obligatorios
        if (nombre.isEmpty() || documento.isEmpty() || telefono.isEmpty() || zonaCobertura.isEmpty()) {
            mostrarError("⚠️ Todos los campos son obligatorios.");
            return false;
        }

        // Validar formato de documento (solo números)
        if (!documento.matches("\\d+")) {
            mostrarError("⚠️ El documento debe contener solo números.");
            return false;
        }

        // Validar formato de teléfono (10 dígitos)
        if (!telefono.matches("\\d{10}")) {
            mostrarError("⚠️ El teléfono debe tener 10 dígitos.");
            return false;
        }

        return true;
    }

    @FXML
    private void volverAlMenuPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminMain.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) txtNombreRepartidor.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panel de Administrador - Urban Express");
            stage.centerOnScreen();
        } catch (IOException e) {
            mostrarError("❌ Error al cargar el panel principal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operación Exitosa");
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

    // Clase interna para la celda de acciones - CORREGIDA
    private class AccionesRepartidorCell extends TableCell<RepartidorDTO, Void> {
        private final HBox contenedorBotones;
        private final Button btnEditar;
        private final Button btnEliminar;

        public AccionesRepartidorCell() {
            this.contenedorBotones = new HBox(5);
            this.contenedorBotones.setStyle("-fx-alignment: CENTER;");

            // Configurar botón Editar
            this.btnEditar = new Button("✏️");
            btnEditar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-min-width: 35; -fx-min-height: 25; -fx-background-radius: 6; " +
                    "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 3, 0, 0, 1);");
            btnEditar.setTooltip(new Tooltip("Editar repartidor"));

            // Configurar botón Eliminar
            this.btnEliminar = new Button("🗑️");
            btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-min-width: 35; -fx-min-height: 25; -fx-background-radius: 6; " +
                    "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(231,76,60,0.3), 3, 0, 0, 1);");
            btnEliminar.setTooltip(new Tooltip("Eliminar repartidor"));

            // Asignar eventos
            btnEditar.setOnAction(event -> {
                RepartidorDTO repartidor = getTableView().getItems().get(getIndex());
                editarRepartidor(repartidor);
            });

            btnEliminar.setOnAction(event -> {
                RepartidorDTO repartidor = getTableView().getItems().get(getIndex());
                eliminarRepartidor(repartidor);
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