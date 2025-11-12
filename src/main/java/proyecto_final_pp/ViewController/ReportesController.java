package proyecto_final_pp.ViewController;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import proyecto_final_pp.facade.LogisticaFacade;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReportesController {

    @FXML private ComboBox<String> cbTipoReporte;
    @FXML private DatePicker dpFechaDesde;
    @FXML private DatePicker dpFechaHasta;
    @FXML private TextArea txtVistaPrevia;

    private LogisticaFacade logisticaFacade;

    @FXML
    public void initialize() {
        this.logisticaFacade = new LogisticaFacade();

        // Poblar el ComboBox con tipos de reporte
        cbTipoReporte.setItems(FXCollections.observableArrayList(
                "Entregas por Periodo",
                "Tiempos Promedio por Zona",
                "Ingresos por Servicio Adicional",
                "Top Incidencias"
        ));
    }

    @FXML
    private void generarReporte() {
        String tipo = cbTipoReporte.getValue();
        LocalDate desde = dpFechaDesde.getValue();
        LocalDate hasta = dpFechaHasta.getValue();

        if (tipo == null || desde == null || hasta == null) {
            mostrarError("Seleccione un tipo de reporte y un rango de fechas.");
            return;
        }

        if (desde.isAfter(hasta)) {
            mostrarError("La fecha 'Desde' no puede ser posterior a la fecha 'Hasta'.");
            return;
        }

        String contenidoReporte = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        switch (tipo) {
            case "Entregas por Periodo":
                contenidoReporte = generarReporteEntregas(desde, hasta, formatter);
                break;
            case "Tiempos Promedio por Zona":
                contenidoReporte = generarReporteTiemposPromedio(desde, hasta, formatter);
                break;
            case "Ingresos por Servicio Adicional":
                contenidoReporte = generarReporteIngresos(desde, hasta, formatter);
                break;
            case "Top Incidencias":
                contenidoReporte = generarReporteIncidencias(desde, hasta, formatter);
                break;
            default:
                contenidoReporte = "Tipo de reporte no implementado.";
        }

        txtVistaPrevia.setText(contenidoReporte);
    }

    private String generarReporteEntregas(LocalDate desde, LocalDate hasta, DateTimeFormatter formatter) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE: ENTREGAS POR PERIODO ===\n");
        reporte.append("Rango: ").append(desde.format(formatter)).append(" a ").append(hasta.format(formatter)).append("\n\n");

        try {
            // Obtener envíos entregados
            var enviosEntregados = logisticaFacade.getTodosLosEnvios().stream()
                    .filter(envio -> "ENTREGADO".equals(envio.getEstadoActual().name()))
                    .count();

            reporte.append("Total Entregas: ").append(enviosEntregados).append("\n");

            // Estadísticas adicionales
            var totalEnvios = logisticaFacade.getTodosLosEnvios().size();
            if (totalEnvios > 0) {
                double porcentajeEntregas = (enviosEntregados * 100.0) / totalEnvios;
                reporte.append(String.format("Porcentaje de Éxito: %.1f%%\n", porcentajeEntregas));
            }

        } catch (Exception e) {
            reporte.append("Error al generar estadísticas: ").append(e.getMessage()).append("\n");
        }

        reporte.append("----------------------------------------\n");
        return reporte.toString();
    }

    private String generarReporteTiemposPromedio(LocalDate desde, LocalDate hasta, DateTimeFormatter formatter) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE: TIEMPOS PROMEDIO POR ZONA ===\n");
        reporte.append("Rango: ").append(desde.format(formatter)).append(" a ").append(hasta.format(formatter)).append("\n\n");

        // Datos simulados - en una implementación real usarías métodos del servicio
        reporte.append("Zona Chapinero: 45 minutos\n");
        reporte.append("Zona Usaquén: 38 minutos\n");
        reporte.append("Zona Suba: 52 minutos\n");
        reporte.append("Zona Teusaquillo: 41 minutos\n");
        reporte.append("Zona Centro: 29 minutos\n");
        reporte.append("----------------------------------------\n");
        reporte.append("Tiempo Promedio General: 41 minutos\n");

        return reporte.toString();
    }

    private String generarReporteIngresos(LocalDate desde, LocalDate hasta, DateTimeFormatter formatter) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE: INGRESOS POR SERVICIO ADICIONAL ===\n");
        reporte.append("Rango: ").append(desde.format(formatter)).append(" a ").append(hasta.format(formatter)).append("\n\n");

        // Datos simulados
        reporte.append("Servicio Seguro: $1.200.000\n");
        reporte.append("Servicio Frágil: $800.000\n");
        reporte.append("Servicio Embalaje Especial: $600.000\n");
        reporte.append("Servicio Firma Requerida: $400.000\n");
        reporte.append("Servicio Express: $2.100.000\n");
        reporte.append("----------------------------------------\n");
        reporte.append("TOTAL: $5.100.000\n");

        return reporte.toString();
    }

    private String generarReporteIncidencias(LocalDate desde, LocalDate hasta, DateTimeFormatter formatter) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE: TOP INCIDENCIAS ===\n");
        reporte.append("Rango: ").append(desde.format(formatter)).append(" a ").append(hasta.format(formatter)).append("\n\n");

        // Datos simulados
        reporte.append("1. Paquete Dañado: 15 casos\n");
        reporte.append("2. Retraso en Entrega: 8 casos\n");
        reporte.append("3. Dirección Incorrecta: 6 casos\n");
        reporte.append("4. Cliente No Encontrado: 4 casos\n");
        reporte.append("5. Rechazo de Paquete: 3 casos\n");
        reporte.append("----------------------------------------\n");
        reporte.append("TOTAL INCIDENCIAS: 36 casos\n");

        return reporte.toString();
    }

    @FXML
    private void descargarReporte() {
        String contenido = txtVistaPrevia.getText();
        if (contenido.isEmpty()) {
            mostrarError("No hay reporte generado para descargar.");
            return;
        }

        Alert dialogoOpciones = new Alert(Alert.AlertType.CONFIRMATION);
        dialogoOpciones.setTitle("Descargar Reporte");
        dialogoOpciones.setHeaderText("Seleccione el formato de descarga:");
        ButtonType btnCSV = new ButtonType("CSV");
        ButtonType btnPDF = new ButtonType("PDF");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogoOpciones.getButtonTypes().setAll(btnCSV, btnPDF, btnCancelar);

        dialogoOpciones.showAndWait().ifPresent(tipoBtn -> {
            if (tipoBtn == btnCSV) {
                System.out.println("Descargando reporte como CSV...");
                mostrarInfo("Descarga CSV simulada para: " + cbTipoReporte.getValue());
            } else if (tipoBtn == btnPDF) {
                System.out.println("Descargando reporte como PDF...");
                mostrarInfo("Descarga PDF simulada para: " + cbTipoReporte.getValue());
            }
        });
    }

    @FXML
    private void volverAlMenuPrincipal() {
        try {
            // CORRECCIÓN: Ruta corregida
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminMain.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) cbTipoReporte.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panel de Administrador - Urban Express");
        } catch (IOException e) {
            mostrarError("Error al cargar ventana principal de admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void limpiarCampos() {
        cbTipoReporte.setValue(null);
        dpFechaDesde.setValue(null);
        dpFechaHasta.setValue(null);
        txtVistaPrevia.clear();
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
}