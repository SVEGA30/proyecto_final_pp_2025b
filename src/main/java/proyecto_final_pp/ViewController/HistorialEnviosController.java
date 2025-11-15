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
    @FXML private Label lblContadorResultados;

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

        // Configurar valores por defecto para fechas (últimos 30 días)
        dpFiltroDesde.setValue(LocalDate.now().minusDays(30));
        dpFiltroHasta.setValue(LocalDate.now());

        System.out.println("HistorialEnviosController inicializado");
    }

    private void configurarColumnasTabla() {
        colIdEnvio.setCellValueFactory(new PropertyValueFactory<>("idEnvio"));

        colOrigen.setCellValueFactory(cellData -> {
            var dir = cellData.getValue().getDireccionOrigen();
            if (dir != null && dir.getAlias() != null) {
                return new javafx.beans.property.SimpleStringProperty(dir.getAlias());
            } else {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });

        colDestino.setCellValueFactory(cellData -> {
            var dir = cellData.getValue().getDireccionDestino();
            if (dir != null && dir.getAlias() != null) {
                return new javafx.beans.property.SimpleStringProperty(dir.getAlias());
            } else {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });

        colEstado.setCellValueFactory(cellData -> {
            var estado = cellData.getValue().getEstadoActual();
            if (estado != null) {
                return new javafx.beans.property.SimpleStringProperty(estado.name());
            } else {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });

        colCosto.setCellValueFactory(new PropertyValueFactory<>("costo"));

        // Formatear el costo como moneda
        colCosto.setCellFactory(column -> new TableCell<EnvioDTO, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%,.0f", item));
                }
            }
        });

        colFechaCreacion.setCellValueFactory(cellData -> {
            var fecha = cellData.getValue().getFechaCreacion();
            if (fecha != null) {
                var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                return new javafx.beans.property.SimpleStringProperty(fecha.format(formatter));
            } else {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });

        // Configurar columna de acciones
        colAcciones.setCellFactory(param -> new AccionesEnvioCell(this));
    }

    public void setUsuarioActual(UsuarioDTO usuarioDTO) {
        this.usuarioActualDTO = usuarioDTO;
        if (usuarioDTO != null) {
            System.out.println("Usuario establecido en HistorialEnvios: " + usuarioDTO.getNombre());
            cargarEnviosUsuario();
        } else {
            System.err.println("UsuarioDTO es null en setUsuarioActual");
        }
    }

    private void cargarEnviosUsuario() {
        if (usuarioActualDTO != null) {
            try {
                System.out.println("Cargando envíos para usuario: " + usuarioActualDTO.getNombre());
                List<EnvioDTO> envios = logisticaFacade.getEnviosPorUsuario(usuarioActualDTO);

                System.out.println("Envíos encontrados: " + envios.size());
                envios.forEach(envio -> System.out.println(" - " + envio.getIdEnvio() + " - " + envio.getEstadoActual()));

                enviosOriginales.setAll(envios);
                aplicarFiltros();

            } catch (Exception e) {
                System.err.println("Error al cargar envíos del usuario: " + e.getMessage());
                e.printStackTrace();
                mostrarError("Error al cargar los envíos: " + e.getMessage());
            }
        } else {
            System.err.println("No hay usuario actual para cargar envíos");
        }
    }

    @FXML
    private void aplicarFiltros() {
        try {
            String estadoFiltro = cbFiltroEstado.getValue();
            LocalDate fechaDesde = dpFiltroDesde.getValue();
            LocalDate fechaHasta = dpFiltroHasta.getValue();

            System.out.println("Aplicando filtros - Estado: " + estadoFiltro + ", Desde: " + fechaDesde + ", Hasta: " + fechaHasta);

            List<EnvioDTO> filtrados = enviosOriginales.stream()
                    .filter(envio -> {
                        // Filtro por estado
                        boolean cumpleEstado = "TODOS".equals(estadoFiltro) ||
                                (envio.getEstadoActual() != null &&
                                        envio.getEstadoActual().name().equals(estadoFiltro));

                        // Filtro por fecha
                        boolean cumpleFecha = true;
                        if (fechaDesde != null && envio.getFechaCreacion() != null) {
                            cumpleFecha = !envio.getFechaCreacion().toLocalDate().isBefore(fechaDesde);
                        }
                        if (fechaHasta != null && envio.getFechaCreacion() != null) {
                            cumpleFecha = cumpleFecha && !envio.getFechaCreacion().toLocalDate().isAfter(fechaHasta);
                        }

                        return cumpleEstado && cumpleFecha;
                    })
                    .collect(Collectors.toList());

            enviosFiltrados.setAll(filtrados);
            actualizarContadorResultados();

            System.out.println("Filtros aplicados - Resultados: " + filtrados.size());

            // Mostrar mensaje si no hay resultados
            if (filtrados.isEmpty()) {
                mostrarInfo("No se encontraron envíos con los filtros aplicados.");
            }

        } catch (Exception e) {
            System.err.println("Error al aplicar filtros: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al aplicar los filtros: " + e.getMessage());
        }
    }

    private void actualizarContadorResultados() {
        if (lblContadorResultados != null) {
            int total = enviosFiltrados.size();
            lblContadorResultados.setText("Mostrando " + total + " envío" + (total != 1 ? "s" : ""));
        }
    }

    @FXML
    private void limpiarFiltros() {
        cbFiltroEstado.setValue("TODOS");
        dpFiltroDesde.setValue(LocalDate.now().minusDays(30));
        dpFiltroHasta.setValue(LocalDate.now());
        aplicarFiltros();
        mostrarInfo("Filtros limpiados correctamente.");
    }

    @FXML
    private void actualizarEnvios() {
        cargarEnviosUsuario();
        mostrarInfo("Lista de envíos actualizada.");
    }

    public void verDetalleEnvio(EnvioDTO envio) {
        try {
            Alert detalle = new Alert(Alert.AlertType.INFORMATION);
            detalle.setTitle("Detalle del Envío");
            detalle.setHeaderText("📦 Detalles del Envío " + envio.getIdEnvio());

            StringBuilder contenido = new StringBuilder();
            contenido.append("📊 **Estado:** ").append(envio.getEstadoActual().name()).append("\n\n")
                    .append("💰 **Costo:** $").append(String.format("%,.0f", envio.getCosto())).append("\n")
                    .append("⚖️ **Peso:** ").append(envio.getPeso()).append(" kg\n")
                    .append("📏 **Volumen:** ").append(envio.getVolumen()).append(" m³\n")
                    .append("🚚 **Tipo:** ").append(envio.getTipoEnvio()).append("\n\n");

            if (envio.getDireccionOrigen() != null) {
                contenido.append("📍 **Origen:**\n")
                        .append("   Alias: ").append(envio.getDireccionOrigen().getAlias()).append("\n")
                        .append("   Dirección: ").append(envio.getDireccionOrigen().getCalle()).append("\n")
                        .append("   Zona: ").append(envio.getDireccionOrigen().getZona()).append("\n\n");
            }

            if (envio.getDireccionDestino() != null) {
                contenido.append("🎯 **Destino:**\n")
                        .append("   Alias: ").append(envio.getDireccionDestino().getAlias()).append("\n")
                        .append("   Dirección: ").append(envio.getDireccionDestino().getCalle()).append("\n")
                        .append("   Zona: ").append(envio.getDireccionDestino().getZona()).append("\n\n");
            }

            contenido.append("🔧 **Servicios Extras:** ")
                    .append(envio.getServiciosExtras() != null && !envio.getServiciosExtras().isEmpty() ?
                            String.join(", ", envio.getServiciosExtras()) : "Ninguno").append("\n\n")
                    .append("📅 **Fecha Creación:** ").append(envio.getFechaCreacion() != null ?
                            envio.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A").append("\n")
                    .append("🔄 **Última Actualización:** ").append(envio.getFechaActualizacionEstado() != null ?
                            envio.getFechaActualizacionEstado().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A");

            detalle.setContentText(contenido.toString());
            detalle.getDialogPane().setPrefSize(500, 400);
            detalle.showAndWait();

        } catch (Exception e) {
            System.err.println("Error al mostrar detalle del envío: " + e.getMessage());
            mostrarError("Error al mostrar los detalles del envío.");
        }
    }

    @FXML
    private void descargarReporteCSV() {
        if (enviosFiltrados.isEmpty()) {
            mostrarError("No hay datos para generar el reporte CSV.");
            return;
        }

        try {
            // Implementar la lógica de generación de CSV
            System.out.println("Generando reporte CSV con " + enviosFiltrados.size() + " registros...");

            // Simulación de generación de CSV
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("ID Envío,Origen,Destino,Estado,Costo,Fecha Creación\n");

            for (EnvioDTO envio : enviosFiltrados) {
                csvContent.append(envio.getIdEnvio()).append(",")
                        .append(envio.getDireccionOrigen() != null ? envio.getDireccionOrigen().getAlias() : "N/A").append(",")
                        .append(envio.getDireccionDestino() != null ? envio.getDireccionDestino().getAlias() : "N/A").append(",")
                        .append(envio.getEstadoActual().name()).append(",")
                        .append(String.format("%.0f", envio.getCosto())).append(",")
                        .append(envio.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                        .append("\n");
            }

            // En una aplicación real, aquí guardarías el archivo
            System.out.println("Contenido CSV generado:\n" + csvContent.toString());

            mostrarInfo("Reporte CSV generado exitosamente.\nRegistros: " + enviosFiltrados.size() + "\n(En una aplicación real, se descargaría el archivo)");
        } catch (Exception e) {
            mostrarError("Error al generar reporte CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void descargarReportePDF() {
        if (enviosFiltrados.isEmpty()) {
            mostrarError("No hay datos para generar el reporte PDF.");
            return;
        }

        try {
            // Implementar la lógica de generación de PDF
            System.out.println("Generando reporte PDF con " + enviosFiltrados.size() + " registros...");
            mostrarInfo("Reporte PDF generado exitosamente.\nRegistros: " + enviosFiltrados.size() + "\n(En una aplicación real, se descargaría el archivo PDF)");
        } catch (Exception e) {
            mostrarError("Error al generar reporte PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void volverAlMenuPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UsuarioMain.fxml"));
            Parent root = loader.load();

            // Pasar el usuario actual al controlador principal
            UsuarioMainController controller = loader.getController();
            if (controller != null) {
                controller.setUsuarioActual(usuarioActualDTO);
            }

            Stage stage = (Stage) tablaEnvios.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Panel de Usuario - Urban Express");
            stage.centerOnScreen();
        } catch (IOException e) {
            mostrarError("Error al cargar ventana principal: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
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
            this.btnDetalle = new Button("👁️ Ver");

            // Estilizar el botón
            btnDetalle.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 5; -fx-font-size: 11px;");
            btnDetalle.setOnAction(event -> {
                EnvioDTO envio = getTableView().getItems().get(getIndex());
                if (envio != null) {
                    controller.verDetalleEnvio(envio);
                }
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