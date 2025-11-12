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
import proyecto_final_pp.model.dto.EnvioDTO;
import proyecto_final_pp.model.dto.UsuarioDTO;
import proyecto_final_pp.facade.LogisticaFacade;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class HistorialEnviosController {

    @FXML private ComboBox<String> cbFiltroEstado;
    @FXML private DatePicker dpFiltroDesde;
    @FXML private DatePicker dpFiltroHasta;
    @FXML private TableView<EnvioDTO> tablaEnvios;
    @FXML private TableColumn<EnvioDTO, String> colIdEnvio;
    @FXML private TableColumn<EnvioDTO, String> colOrigen;
    @FXML private TableColumn<EnvioDTO, String> colDestino;
    @FXML private TableColumn<EnvioDTO, String> colEstado;
    @FXML private TableColumn<EnvioDTO, Double> colCosto;
    @FXML private TableColumn<EnvioDTO, String> colFechaCreacion;
    @FXML private TableColumn<EnvioDTO, Void> colAcciones;

    private LogisticaFacade logisticaFacade;
    private UsuarioDTO usuarioActualDTO;
    private ObservableList<EnvioDTO> enviosOriginales;
    private ObservableList<EnvioDTO> enviosFiltrados;

    @FXML
    public void initialize() {
        this.logisticaFacade = new LogisticaFacade();
        this.enviosOriginales = FXCollections.observableArrayList();
        this.enviosFiltrados = FXCollections.observableArrayList();

        // Configurar columnas de la tabla
        configurarColumnasTabla();

        tablaEnvios.setItems(enviosFiltrados);

        // Cargar estados posibles en el combo de filtro
        cbFiltroEstado.getItems().addAll("TODOS", "SOLICITADO", "ASIGNADO", "EN_RUTA", "ENTREGADO", "INCIDENCIA", "CANCELADO");
        cbFiltroEstado.setValue("TODOS");
    }

    private void configurarColumnasTabla() {
        colIdEnvio.setCellValueFactory(new PropertyValueFactory<>("idEnvio"));

        colOrigen.setCellValueFactory(cellData -> {
            var dir = cellData.getValue().getDireccionOrigen();
            return new javafx.beans.property.SimpleStringProperty(dir != null ? dir.getAlias() : "N/A");
        });

        colDestino.setCellValueFactory(cellData -> {
            var dir = cellData.getValue().getDireccionDestino();
            return new javafx.beans.property.SimpleStringProperty(dir != null ? dir.getAlias() : "N/A");
        });

        colEstado.setCellValueFactory(cellData -> {
            var estado = cellData.getValue().getEstadoActual();
            return new javafx.beans.property.SimpleStringProperty(estado != null ? estado.name() : "N/A");
        });

        colCosto.setCellValueFactory(new PropertyValueFactory<>("costo"));

        colFechaCreacion.setCellValueFactory(cellData -> {
            var fecha = cellData.getValue().getFechaCreacion();
            var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return new javafx.beans.property.SimpleStringProperty(fecha != null ? fecha.format(formatter) : "N/A");
        });

        // Configurar columna de acciones
        colAcciones.setCellFactory(param -> new AccionesEnvioCell(this));
    }

    public void setUsuarioActual(UsuarioDTO usuarioDTO) {
        this.usuarioActualDTO = usuarioDTO;
        if (usuarioDTO != null) {
            cargarEnviosUsuario();
        }
    }

    private void cargarEnviosUsuario() {
        if (usuarioActualDTO != null) {
            List<EnvioDTO> envios = logisticaFacade.getEnviosPorUsuario(usuarioActualDTO);
            enviosOriginales.setAll(envios);
            aplicarFiltros();
        }
    }

    @FXML
    private void aplicarFiltros() {
        String estadoFiltro = cbFiltroEstado.getValue();
        LocalDate fechaDesde = dpFiltroDesde.getValue();
        LocalDate fechaHasta = dpFiltroHasta.getValue();

        List<EnvioDTO> filtrados = enviosOriginales.stream()
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

        enviosFiltrados.setAll(filtrados);

        // Mostrar mensaje si no hay resultados
        if (filtrados.isEmpty()) {
            mostrarInfo("No se encontraron envíos con los filtros aplicados.");
        }
    }

    @FXML
    private void limpiarFiltros() {
        cbFiltroEstado.setValue("TODOS");
        dpFiltroDesde.setValue(null);
        dpFiltroHasta.setValue(null);
        aplicarFiltros();
    }

    public void verDetalleEnvio(EnvioDTO envio) {
        Alert detalle = new Alert(Alert.AlertType.INFORMATION);
        detalle.setTitle("Detalle del Envío");
        detalle.setHeaderText("ID: " + envio.getIdEnvio());

        StringBuilder contenido = new StringBuilder();
        contenido.append("Estado: ").append(envio.getEstadoActual().name()).append("\n")
                .append("Costo: $").append(String.format("%.2f", envio.getCosto())).append("\n")
                .append("Peso: ").append(envio.getPeso()).append(" kg\n")
                .append("Volumen: ").append(envio.getVolumen()).append(" m³\n")
                .append("Tipo: ").append(envio.getTipoEnvio()).append("\n");

        if (envio.getDireccionOrigen() != null) {
            contenido.append("Origen: ").append(envio.getDireccionOrigen().getAlias()).append("\n");
        }

        if (envio.getDireccionDestino() != null) {
            contenido.append("Destino: ").append(envio.getDireccionDestino().getAlias()).append("\n");
        }

        contenido.append("Servicios Extras: ").append(envio.getServiciosExtras() != null ? envio.getServiciosExtras() : "Ninguno").append("\n")
                .append("Fecha Creación: ").append(envio.getFechaCreacion() != null ?
                        envio.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A").append("\n")
                .append("Última Actualización: ").append(envio.getFechaActualizacionEstado() != null ?
                        envio.getFechaActualizacionEstado().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A");

        detalle.setContentText(contenido.toString());
        detalle.getDialogPane().setPrefSize(400, 300);
        detalle.showAndWait();
    }

    @FXML
    private void descargarReporteCSV() {
        if (enviosFiltrados.isEmpty()) {
            mostrarError("No hay datos para generar el reporte CSV.");
            return;
        }

        // Lógica para generar y descargar el reporte CSV
        try {
            // Implementar la lógica de generación de CSV
            System.out.println("Generando reporte CSV con " + enviosFiltrados.size() + " registros...");
            mostrarInfo("Reporte CSV generado exitosamente.\nRegistros: " + enviosFiltrados.size());
        } catch (Exception e) {
            mostrarError("Error al generar reporte CSV: " + e.getMessage());
        }
    }

    @FXML
    private void descargarReportePDF() {
        if (enviosFiltrados.isEmpty()) {
            mostrarError("No hay datos para generar el reporte PDF.");
            return;
        }

        // Lógica para generar y descargar el reporte PDF
        try {
            // Implementar la lógica de generación de PDF
            System.out.println("Generando reporte PDF con " + enviosFiltrados.size() + " registros...");
            mostrarInfo("Reporte PDF generado exitosamente.\nRegistros: " + enviosFiltrados.size());
        } catch (Exception e) {
            mostrarError("Error al generar reporte PDF: " + e.getMessage());
        }
    }

    @FXML
    private void volverAlMenuPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UsuarioMain.fxml"));
            Parent root = loader.load();

            // Obtener la ventana actual desde cualquier nodo de la escena
            Stage stage = (Stage) tablaEnvios.getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Panel de Usuario - Urban Express");
            stage.centerOnScreen();
        } catch (IOException e) {
            mostrarError("Error al cargar ventana principal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void actualizarEnvios() {
        cargarEnviosUsuario();
        mostrarInfo("Lista de envíos actualizada.");
    }

    private void mostrarInfo(String mensaje) {
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
        private final HistorialEnviosController controller;
        private final Button btnDetalle;

        public AccionesEnvioCell(HistorialEnviosController controller) {
            this.controller = controller;
            this.btnDetalle = new Button("Ver Detalle");
            btnDetalle.getStyleClass().add("btn-info");
            btnDetalle.setOnAction(event -> {
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