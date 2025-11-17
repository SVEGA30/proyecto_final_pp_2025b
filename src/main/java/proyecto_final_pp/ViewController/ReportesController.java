package proyecto_final_pp.ViewController;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import proyecto_final_pp.facade.LogisticaFacade;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

// Importaciones para PDFBox 3.x
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class ReportesController {

    @FXML private ComboBox<String> cbTipoReporte;
    @FXML private DatePicker dpFechaDesde;
    @FXML private DatePicker dpFechaHasta;
    @FXML private TextArea txtVistaPrevia;

    private LogisticaFacade logisticaFacade;
    private String contenidoReporteActual;

    @FXML
    public void initialize() {
        this.logisticaFacade = new LogisticaFacade();

        // Poblar el ComboBox con tipos de reporte
        cbTipoReporte.setItems(FXCollections.observableArrayList(
                "Entregas por Periodo",
                "Tiempos Promedio por Zona",
                "Ingresos por Servicio Adicional",
                "Top Incidencias",
                "Estadísticas Generales"
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
            case "Estadísticas Generales":
                contenidoReporte = generarReporteEstadisticasGenerales(desde, hasta, formatter);
                break;
            default:
                contenidoReporte = "Tipo de reporte no implementado.";
        }

        contenidoReporteActual = contenidoReporte;
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

            var totalEnvios = logisticaFacade.getTodosLosEnvios().size();

            reporte.append("Total Entregas: ").append(enviosEntregados).append("\n");
            reporte.append("Total Envíos: ").append(totalEnvios).append("\n");

            if (totalEnvios > 0) {
                double porcentajeEntregas = (enviosEntregados * 100.0) / totalEnvios;
                reporte.append(String.format("Porcentaje de Éxito: %.1f%%\n", porcentajeEntregas));
            }

            // Datos para CSV/PDF
            reporte.append("\n--- DETALLE POR ESTADO ---\n");
            Map<String, Long> conteoPorEstado = new HashMap<>();
            logisticaFacade.getTodosLosEnvios().forEach(envio -> {
                String estado = envio.getEstadoActual().name();
                conteoPorEstado.put(estado, conteoPorEstado.getOrDefault(estado, 0L) + 1);
            });

            conteoPorEstado.forEach((estado, cantidad) -> {
                reporte.append(estado).append(": ").append(cantidad).append("\n");
            });

        } catch (Exception e) {
            reporte.append("Error al generar estadísticas: ").append(e.getMessage()).append("\n");
        }

        reporte.append("\n----------------------------------------\n");
        reporte.append("Generado el: ").append(LocalDate.now().format(formatter)).append("\n");
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
        reporte.append("\n----------------------------------------\n");
        reporte.append("Tiempo Promedio General: 41 minutos\n");
        reporte.append("Generado el: ").append(LocalDate.now().format(formatter)).append("\n");

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
        reporte.append("\n----------------------------------------\n");
        reporte.append("TOTAL: $5.100.000\n");
        reporte.append("Generado el: ").append(LocalDate.now().format(formatter)).append("\n");

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
        reporte.append("\n----------------------------------------\n");
        reporte.append("TOTAL INCIDENCIAS: 36 casos\n");
        reporte.append("Generado el: ").append(LocalDate.now().format(formatter)).append("\n");

        return reporte.toString();
    }

    private String generarReporteEstadisticasGenerales(LocalDate desde, LocalDate hasta, DateTimeFormatter formatter) {
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE: ESTADÍSTICAS GENERALES ===\n");
        reporte.append("Rango: ").append(desde.format(formatter)).append(" a ").append(hasta.format(formatter)).append("\n\n");

        try {
            String estadisticas = logisticaFacade.obtenerEstadisticasGenerales();
            reporte.append(estadisticas).append("\n");
        } catch (Exception e) {
            reporte.append("Error al obtener estadísticas generales: ").append(e.getMessage()).append("\n");
        }

        reporte.append("\n----------------------------------------\n");
        reporte.append("Generado el: ").append(LocalDate.now().format(formatter)).append("\n");
        return reporte.toString();
    }

    @FXML
    private void descargarReporte() {
        if (contenidoReporteActual == null || contenidoReporteActual.isEmpty()) {
            mostrarError("No hay reporte generado para descargar.");
            return;
        }

        Alert dialogoOpciones = new Alert(Alert.AlertType.CONFIRMATION);
        dialogoOpciones.setTitle("Descargar Reporte");
        dialogoOpciones.setHeaderText("Seleccione el formato de descarga:");
        ButtonType btnCSV = new ButtonType("CSV");
        ButtonType btnPDF = new ButtonType("PDF");
        ButtonType btnTXT = new ButtonType("TXT");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogoOpciones.getButtonTypes().setAll(btnCSV, btnPDF, btnTXT, btnCancelar);

        dialogoOpciones.showAndWait().ifPresent(tipoBtn -> {
            if (tipoBtn == btnCSV) {
                descargarCSV();
            } else if (tipoBtn == btnPDF) {
                descargarPDF();
            } else if (tipoBtn == btnTXT) {
                descargarTXT();
            }
        });
    }

    private void descargarCSV() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte CSV");
            fileChooser.setInitialFileName(generarNombreArchivo() + ".csv");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv")
            );

            File file = fileChooser.showSaveDialog(cbTipoReporte.getScene().getWindow());
            if (file != null) {
                String csvContent = convertirACSV(contenidoReporteActual);
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(csvContent);
                }
                mostrarInfo("Reporte CSV guardado exitosamente en: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            mostrarError("Error al guardar CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void descargarPDF() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte PDF");
            fileChooser.setInitialFileName(generarNombreArchivo() + ".pdf");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf")
            );

            File file = fileChooser.showSaveDialog(cbTipoReporte.getScene().getWindow());
            if (file != null) {
                generarPDFReal(file, contenidoReporteActual);
                mostrarInfo("Reporte PDF guardado exitosamente en: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            mostrarError("Error al guardar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void descargarTXT() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte TXT");
            fileChooser.setInitialFileName(generarNombreArchivo() + ".txt");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Archivos de Texto (*.txt)", "*.txt")
            );

            File file = fileChooser.showSaveDialog(cbTipoReporte.getScene().getWindow());
            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(contenidoReporteActual);
                }
                mostrarInfo("Reporte TXT guardado exitosamente en: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            mostrarError("Error al guardar TXT: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // MÉTODO IMPLEMENTADO: Generar PDF real con PDFBox 3.x
    private void generarPDFReal(File file, String contenido) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // Fuentes para PDFBox 3.x
            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font fontItalic = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

            // Variables para control de páginas
            float margin = 50;
            float lineHeight = 14;
            float titleLineHeight = 20;
            float yPosition = page.getMediaBox().getHeight() - margin;

            // Crear el primer contentStream
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            try {
                // Título del reporte
                String titulo = cbTipoReporte.getValue() != null ? cbTipoReporte.getValue() : "Reporte";
                contentStream.setFont(fontBold, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(titulo);
                contentStream.endText();
                yPosition -= titleLineHeight;

                // Fechas del período
                LocalDate desde = dpFechaDesde.getValue();
                LocalDate hasta = dpFechaHasta.getValue();
                if (desde != null && hasta != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    contentStream.setFont(fontNormal, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("Período: " + desde.format(formatter) + " - " + hasta.format(formatter));
                    contentStream.endText();
                    yPosition -= lineHeight;
                }

                yPosition -= lineHeight; // Espacio adicional

                // Contenido del reporte
                contentStream.setFont(fontNormal, 10);
                String[] lineas = contenido.split("\n");

                for (String linea : lineas) {
                    // Verificar si necesitamos nueva página
                    if (yPosition < margin + 50) {
                        contentStream.close(); // Cerrar página actual

                        // Crear nueva página
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.setFont(fontNormal, 10);
                        yPosition = page.getMediaBox().getHeight() - margin;
                    }

                    if (linea.startsWith("===")) {
                        // Encabezado principal
                        contentStream.setFont(fontBold, 12);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(margin, yPosition);
                        contentStream.showText(linea.replace("===", "").trim());
                        contentStream.endText();
                        contentStream.setFont(fontNormal, 10);
                        yPosition -= lineHeight;
                    } else if (linea.startsWith("---")) {
                        // Separador
                        yPosition -= 5; // Espacio extra para separadores
                    } else if (!linea.trim().isEmpty()) {
                        // Línea normal de contenido
                        contentStream.beginText();
                        contentStream.newLineAtOffset(margin, yPosition);
                        contentStream.showText(linea.trim());
                        contentStream.endText();
                        yPosition -= lineHeight;
                    }
                }

                // Pie de página
                contentStream.setFont(fontItalic, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, margin);
                contentStream.showText("Generado el: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                contentStream.endText();

            } finally {
                // Asegurarse de cerrar el contentStream
                if (contentStream != null) {
                    contentStream.close();
                }
            }

            // Guardar el documento
            document.save(file);
        }
    }

    private String convertirACSV(String contenido) {
        StringBuilder csv = new StringBuilder();
        String[] lineas = contenido.split("\n");

        // Encabezado CSV
        csv.append("Campo,Valor\n");

        for (String linea : lineas) {
            // Limpiar la línea y convertir a formato CSV
            if (linea.contains(":")) {
                String[] partes = linea.split(":", 2);
                if (partes.length == 2) {
                    String clave = partes[0].trim().replace("\"", "\"\"");
                    String valor = partes[1].trim().replace("\"", "\"\"");
                    csv.append("\"").append(clave).append("\",\"").append(valor).append("\"\n");
                }
            } else if (!linea.trim().isEmpty() && !linea.startsWith("===") && !linea.startsWith("---")) {
                // Líneas de datos generales
                csv.append("\"Información\",\"").append(linea.trim().replace("\"", "\"\"")).append("\"\n");
            }
        }

        return csv.toString();
    }

    private String generarNombreArchivo() {
        String tipoReporte = cbTipoReporte.getValue() != null ?
                cbTipoReporte.getValue().replace(" ", "_") : "Reporte";
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return tipoReporte + "_" + fecha;
    }

    @FXML
    private void volverAlMenuPrincipal() {
        try {
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
        contenidoReporteActual = null;
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