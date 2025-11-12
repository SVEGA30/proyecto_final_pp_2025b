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
import javafx.util.Callback;
import proyecto_final_pp.model.dto.EnvioDTO;
import proyecto_final_pp.Service.AdminService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class TodosLosEnviosController {

    @FXML private ComboBox<String> cbFiltroEstado;
    @FXML private DatePicker dpFiltroDesde;
    @FXML private DatePicker dpFiltroHasta;
    @FXML private TableView<EnvioDTO> tablaTodosEnvios;
    @FXML private TableColumn<EnvioDTO, String> colIdEnvioTabla;
    @FXML private TableColumn<EnvioDTO, String> colIdUsuarioTabla;
    @FXML private TableColumn<EnvioDTO, String> colOrigenTabla;
    @FXML private TableColumn<EnvioDTO, String> colDestinoTabla;
    @FXML private TableColumn<EnvioDTO, String> colEstadoTabla;
    @FXML private TableColumn<EnvioDTO, Double> colCostoTabla;
    @FXML private TableColumn<EnvioDTO, String> colFechaCreacionTabla;
    @FXML private TableColumn<EnvioDTO, Void> colAccionesTabla;

    private AdminService adminService;
    private ObservableList<EnvioDTO> enviosObservable;

    @FXML
    public void initialize() {
        this.adminService = new AdminService();
        this.enviosObservable = FXCollections.observableArrayList();

        configurarColumnasTabla();
        configurarFiltros();
        configurarColumnaAcciones();

        tablaTodosEnvios.setItems(enviosObservable);

        cargarEnvios();
    }

    private void configurarColumnasTabla() {
        colIdEnvioTabla.setCellValueFactory(new PropertyValueFactory<>("idEnvio"));
        colIdUsuarioTabla.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));

        colOrigenTabla.setCellValueFactory(cellData -> {
            var dir = cellData.getValue().getDireccionOrigen();
            if (dir != null) {
                return new javafx.beans.property.SimpleStringProperty(dir.getAlias() + " - " + dir.getCalle());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        colDestinoTabla.setCellValueFactory(cellData -> {
            var dir = cellData.getValue().getDireccionDestino();
            if (dir != null) {
                return new javafx.beans.property.SimpleStringProperty(dir.getAlias() + " - " + dir.getCalle());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        colEstadoTabla.setCellValueFactory(new PropertyValueFactory<>("estadoActual"));
        colCostoTabla.setCellValueFactory(new PropertyValueFactory<>("costo"));

        colFechaCreacionTabla.setCellValueFactory(cellData -> {
            var fecha = cellData.getValue().getFechaCreacion();
            if (fecha != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                return new javafx.beans.property.SimpleStringProperty(fecha.format(formatter));
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
    }

    private void configurarFiltros() {
        cbFiltroEstado.getItems().addAll("TODOS", "SOLICITADO", "ASIGNADO", "EN RUTA", "ENTREGADO", "INCIDENCIA", "CANCELADO");
        cbFiltroEstado.setValue("TODOS");

        // Configurar listeners para aplicar filtros automáticamente
        cbFiltroEstado.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        dpFiltroDesde.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        dpFiltroHasta.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }

    private void configurarColumnaAcciones() {
        colAccionesTabla.setCellFactory(new Callback<TableColumn<EnvioDTO, Void>, TableCell<EnvioDTO, Void>>() {
            @Override
            public TableCell<EnvioDTO, Void> call(TableColumn<EnvioDTO, Void> param) {
                return new AccionesEnvioCell(TodosLosEnviosController.this);
            }
        });
    }

    private void cargarEnvios() {
        try {
            List<EnvioDTO> envios = adminService.getTodosLosEnvios();
            enviosObservable.setAll(envios);

            if (envios.isEmpty()) {
                mostrarInformacion("No hay envíos registrados en el sistema.");
            }
        } catch (Exception e) {
            mostrarError("Error al cargar los envíos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void aplicarFiltros() {
        try {
            String estadoFiltro = cbFiltroEstado.getValue();
            LocalDate fechaDesde = dpFiltroDesde.getValue();
            LocalDate fechaHasta = dpFiltroHasta.getValue();

            List<EnvioDTO> envios = adminService.getTodosLosEnvios();

            List<EnvioDTO> filtrados = envios.stream()
                    .filter(envio -> {
                        // Filtro por estado
                        boolean cumpleEstado = "TODOS".equals(estadoFiltro) ||
                                envio.getEstadoActual().name().equals(estadoFiltro);

                        // Filtro por fecha
                        boolean cumpleFecha = true;
                        if (fechaDesde != null && envio.getFechaCreacion() != null) {
                            cumpleFecha = envio.getFechaCreacion().toLocalDate().compareTo(fechaDesde) >= 0;
                        }
                        if (fechaHasta != null && envio.getFechaCreacion() != null) {
                            cumpleFecha = cumpleFecha && envio.getFechaCreacion().toLocalDate().compareTo(fechaHasta) <= 0;
                        }

                        return cumpleEstado && cumpleFecha;
                    })
                    .collect(Collectors.toList());

            enviosObservable.setAll(filtrados);

            if (filtrados.isEmpty()) {
                mostrarInformacion("No se encontraron envíos con los filtros aplicados.");
            }
        } catch (Exception e) {
            mostrarError("Error al aplicar filtros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void limpiarFiltros() {
        cbFiltroEstado.setValue("TODOS");
        dpFiltroDesde.setValue(null);
        dpFiltroHasta.setValue(null);
        cargarEnvios(); // Recargar todos los envíos sin filtros
    }

    public void verDetalleEnvio(EnvioDTO envio) {
        if (envio == null) {
            mostrarError("No se puede mostrar el detalle: envío nulo.");
            return;
        }

        try {
            StringBuilder detalles = new StringBuilder();
            detalles.append("ID Envío: ").append(envio.getIdEnvio()).append("\n");
            detalles.append("Estado: ").append(envio.getEstadoActual().name()).append("\n");
            detalles.append("Costo: $").append(String.format("%.2f", envio.getCosto())).append("\n");
            detalles.append("Usuario: ").append(envio.getIdUsuario()).append("\n");

            // Información de origen
            if (envio.getDireccionOrigen() != null) {
                detalles.append("Origen: ").append(envio.getDireccionOrigen().getAlias()).append("\n");
                detalles.append("  Dirección: ").append(envio.getDireccionOrigen().getCalle())
                        .append(", ").append(envio.getDireccionOrigen().getCiudad()).append("\n");
            } else {
                detalles.append("Origen: N/A\n");
            }

            // Información de destino
            if (envio.getDireccionDestino() != null) {
                detalles.append("Destino: ").append(envio.getDireccionDestino().getAlias()).append("\n");
                detalles.append("  Dirección: ").append(envio.getDireccionDestino().getCalle())
                        .append(", ").append(envio.getDireccionDestino().getCiudad()).append("\n");
            } else {
                detalles.append("Destino: N/A\n");
            }

            detalles.append("Peso: ").append(envio.getPeso()).append(" kg\n");
            detalles.append("Volumen: ").append(envio.getVolumen()).append(" m³\n");

            if (envio.getServiciosExtras() != null && !envio.getServiciosExtras().isEmpty()) {
                detalles.append("Servicios Extras: ").append(String.join(", ", envio.getServiciosExtras())).append("\n");
            } else {
                detalles.append("Servicios Extras: Ninguno\n");
            }

            if (envio.getFechaCreacion() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                detalles.append("Fecha Creación: ").append(envio.getFechaCreacion().format(formatter)).append("\n");
            }

            if (envio.getFechaActualizacionEstado() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                detalles.append("Última Actualización: ").append(envio.getFechaActualizacionEstado().format(formatter)).append("\n");
            }

            Alert detalle = new Alert(Alert.AlertType.INFORMATION);
            detalle.setTitle("Detalle del Envío");
            detalle.setHeaderText("Información Completa del Envío");
            detalle.setContentText(detalles.toString());
            detalle.getDialogPane().setPrefSize(400, 500);
            detalle.showAndWait();

        } catch (Exception e) {
            mostrarError("Error al mostrar detalle del envío: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void volverAlMenuPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminMain.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) tablaTodosEnvios.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panel de Administrador - Urban Express");
            stage.centerOnScreen();
        } catch (IOException e) {
            mostrarError("Error al cargar ventana principal de admin: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void actualizarEnvios() {
        cargarEnvios();
        mostrarExito("Lista de envíos actualizada correctamente.");
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInformacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
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
    private static class AccionesEnvioCell extends TableCell<EnvioDTO, Void> {
        private final Button btnDetalle;
        private final TodosLosEnviosController controller;

        public AccionesEnvioCell(TodosLosEnviosController controller) {
            this.controller = controller;
            this.btnDetalle = new Button("Ver Detalle");
            this.btnDetalle.getStyleClass().add("boton-detalle");

            this.btnDetalle.setOnAction(event -> {
                EnvioDTO envio = getTableView().getItems().get(getIndex());
                controller.verDetalleEnvio(envio);
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(btnDetalle);
            }
        }
    }
}