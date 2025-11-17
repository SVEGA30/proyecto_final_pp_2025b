package proyecto_final_pp.ViewController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import proyecto_final_pp.facade.LogisticaFacade;
import proyecto_final_pp.model.dto.EnvioDTO;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraficasController {

    @FXML private VBox chartsContainer;
    @FXML private Label lblTitulo;

    private LogisticaFacade logisticaFacade;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private String tipoReporte;

    @FXML
    public void initialize() {
        this.logisticaFacade = new LogisticaFacade();
    }

    public void setDatosReporte(LocalDate desde, LocalDate hasta, String tipoReporte) {
        this.fechaDesde = desde;
        this.fechaHasta = hasta;
        this.tipoReporte = tipoReporte;

        lblTitulo.setText("Gráficas - " + tipoReporte + " (" + desde + " a " + hasta + ")");
        generarGraficas();
    }

    private void generarGraficas() {
        chartsContainer.getChildren().clear();

        switch (tipoReporte) {
            case "Entregas por Periodo":
                generarGraficasEntregas();
                break;
            case "Tiempos Promedio por Zona":
                generarGraficasTiemposPromedio();
                break;
            case "Ingresos por Servicio Adicional":
                generarGraficasIngresos();
                break;
            case "Top Incidencias":
                generarGraficasIncidencias();
                break;
            case "Estadísticas Generales":
                generarGraficasEstadisticasGenerales();
                break;
        }
    }

    private void generarGraficasEntregas() {
        try {
            List<EnvioDTO> envios = logisticaFacade.getTodosLosEnvios().stream()
                    .filter(envio -> estaEnRangoFechas(envio))
                    .collect(Collectors.toList());

            // Gráfica de pastel - Distribución por estado
            Map<String, Long> conteoPorEstado = envios.stream()
                    .collect(Collectors.groupingBy(
                            envio -> envio.getEstadoActual().name(),
                            Collectors.counting()
                    ));

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            conteoPorEstado.forEach((estado, cantidad) -> {
                pieChartData.add(new PieChart.Data(estado + " (" + cantidad + ")", cantidad));
            });

            PieChart pieChart = new PieChart(pieChartData);
            pieChart.setTitle("Distribución de Envíos por Estado");
            pieChart.setLegendVisible(true);
            pieChart.setPrefSize(500, 400);

            // Gráfica de barras - Envíos por día
            Map<LocalDate, Long> enviosPorDia = envios.stream()
                    .collect(Collectors.groupingBy(
                            envio -> envio.getFechaCreacion().toLocalDate(),
                            Collectors.counting()
                    ));

            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Envíos por Día");

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Cantidad de Envíos");

            enviosPorDia.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
                    });

            barChart.getData().add(series);
            barChart.setPrefSize(600, 400);

            // Layout para las gráficas
            HBox chartsRow = new HBox(20);
            chartsRow.getChildren().addAll(pieChart, barChart);
            chartsContainer.getChildren().add(chartsRow);

        } catch (Exception e) {
            mostrarError("Error al generar gráficas de entregas: " + e.getMessage());
        }
    }

    private void generarGraficasTiemposPromedio() {
        try {
            List<EnvioDTO> enviosEntregados = logisticaFacade.getTodosLosEnvios().stream()
                    .filter(envio -> estaEnRangoFechas(envio) && "ENTREGADO".equals(envio.getEstadoActual().name()))
                    .collect(Collectors.toList());

            // Agrupar por zona y calcular tiempo promedio
            Map<String, Double> tiempoPromedioPorZona = enviosEntregados.stream()
                    .filter(envio -> envio.getDireccionDestino() != null)
                    .collect(Collectors.groupingBy(
                            envio -> envio.getDireccionDestino().getZona(),
                            Collectors.averagingDouble(envio -> {
                                // Calcular diferencia en horas entre creación y actualización
                                long horas = java.time.Duration.between(
                                        envio.getFechaCreacion(),
                                        envio.getFechaActualizacionEstado()
                                ).toHours();
                                return horas * 60; // Convertir a minutos
                            })
                    ));

            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Minutos");
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Tiempo Promedio de Entrega por Zona");

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Minutos");

            tiempoPromedioPorZona.forEach((zona, minutos) -> {
                series.getData().add(new XYChart.Data<>(zona, minutos));
            });

            barChart.getData().add(series);
            barChart.setPrefSize(800, 500);

            chartsContainer.getChildren().add(barChart);

        } catch (Exception e) {
            mostrarError("Error al generar gráficas de tiempos promedio: " + e.getMessage());
        }
    }

    private void generarGraficasIngresos() {
        try {
            List<EnvioDTO> envios = logisticaFacade.getTodosLosEnvios().stream()
                    .filter(envio -> estaEnRangoFechas(envio))
                    .collect(Collectors.toList());

            // Calcular ingresos por servicio adicional
            Map<String, Double> ingresosPorServicio = new HashMap<>();
            for (EnvioDTO envio : envios) {
                if (envio.getServiciosExtras() != null) {
                    for (String servicio : envio.getServiciosExtras()) {
                        double costo = calcularCostoServicio(servicio);
                        ingresosPorServicio.merge(servicio, costo, Double::sum);
                    }
                }
            }

            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Ingresos ($)");
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Ingresos por Servicio Adicional");

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Ingresos");

            ingresosPorServicio.forEach((servicio, ingreso) -> {
                series.getData().add(new XYChart.Data<>(formatearServicio(servicio), ingreso));
            });

            barChart.getData().add(series);
            barChart.setPrefSize(600, 400);

            // Gráfica de pastel para distribución porcentual
            double totalIngresos = ingresosPorServicio.values().stream().mapToDouble(Double::doubleValue).sum();
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            ingresosPorServicio.forEach((servicio, ingreso) -> {
                double porcentaje = (ingreso / totalIngresos) * 100;
                pieChartData.add(new PieChart.Data(
                        formatearServicio(servicio) + String.format(" (%.1f%%)", porcentaje),
                        ingreso
                ));
            });

            PieChart pieChart = new PieChart(pieChartData);
            pieChart.setTitle("Distribución Porcentual de Ingresos");
            pieChart.setPrefSize(500, 400);

            HBox chartsRow = new HBox(20);
            chartsRow.getChildren().addAll(barChart, pieChart);
            chartsContainer.getChildren().add(chartsRow);

        } catch (Exception e) {
            mostrarError("Error al generar gráficas de ingresos: " + e.getMessage());
        }
    }

    private void generarGraficasIncidencias() {
        try {
            List<EnvioDTO> enviosConIncidencia = logisticaFacade.getTodosLosEnvios().stream()
                    .filter(envio -> estaEnRangoFechas(envio) && "INCIDENCIA".equals(envio.getEstadoActual().name()))
                    .collect(Collectors.toList());

            // Gráfica de incidencias por zona
            Map<String, Long> incidenciasPorZona = enviosConIncidencia.stream()
                    .filter(envio -> envio.getDireccionDestino() != null)
                    .collect(Collectors.groupingBy(
                            envio -> envio.getDireccionDestino().getZona(),
                            Collectors.counting()
                    ));

            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Número de Incidencias");
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Incidencias por Zona");

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Incidencias");

            incidenciasPorZona.forEach((zona, cantidad) -> {
                series.getData().add(new XYChart.Data<>(zona, cantidad));
            });

            barChart.getData().add(series);
            barChart.setPrefSize(600, 400);

            chartsContainer.getChildren().add(barChart);

        } catch (Exception e) {
            mostrarError("Error al generar gráficas de incidencias: " + e.getMessage());
        }
    }

    private void generarGraficasEstadisticasGenerales() {
        try {
            List<EnvioDTO> todosLosEnvios = logisticaFacade.getTodosLosEnvios();

            // Gráfica de distribución general de estados
            Map<String, Long> conteoEstados = todosLosEnvios.stream()
                    .collect(Collectors.groupingBy(
                            envio -> envio.getEstadoActual().name(),
                            Collectors.counting()
                    ));

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            conteoEstados.forEach((estado, cantidad) -> {
                pieChartData.add(new PieChart.Data(estado + " (" + cantidad + ")", cantidad));
            });

            PieChart pieChart = new PieChart(pieChartData);
            pieChart.setTitle("Distribución General de Estados de Envíos");
            pieChart.setPrefSize(600, 500);

            chartsContainer.getChildren().add(pieChart);

        } catch (Exception e) {
            mostrarError("Error al generar gráficas de estadísticas generales: " + e.getMessage());
        }
    }

    private boolean estaEnRangoFechas(EnvioDTO envio) {
        if (fechaDesde == null || fechaHasta == null) return true;
        LocalDate fechaEnvio = envio.getFechaCreacion().toLocalDate();
        return !fechaEnvio.isBefore(fechaDesde) && !fechaEnvio.isAfter(fechaHasta);
    }

    private double calcularCostoServicio(String servicio) {
        switch (servicio.toUpperCase()) {
            case "SEGURO": return 3000.0;
            case "FRAGIL": return 2000.0;
            case "FIRMA_REQUERIDA": return 1000.0;
            case "EMBALAJE_ESPECIAL": return 1500.0;
            case "EXPRESS": return 5000.0;
            default: return 0.0;
        }
    }

    private String formatearServicio(String servicio) {
        switch (servicio.toUpperCase()) {
            case "SEGURO": return "Seguro";
            case "FRAGIL": return "Frágil";
            case "FIRMA_REQUERIDA": return "Firma Requerida";
            case "EMBALAJE_ESPECIAL": return "Embalaje Especial";
            case "EXPRESS": return "Express";
            default: return servicio;
        }
    }

    @FXML
    private void volverAReportes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Reportes.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) chartsContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Generar Reportes - Urban Express");
        } catch (IOException e) {
            mostrarError("Error al cargar ventana de reportes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}