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
import proyecto_final_pp.model.dto.RepartidorDTO;
import proyecto_final_pp.facade.LogisticaFacade;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class EnviosPendientesController {

    @FXML private TableView<EnvioDTO> tablaEnviosPendientes;
    @FXML private TableColumn<EnvioDTO, String> colIdEnvio;
    @FXML private TableColumn<EnvioDTO, String> colIdUsuario;
    @FXML private TableColumn<EnvioDTO, String> colOrigen;
    @FXML private TableColumn<EnvioDTO, String> colDestino;
    @FXML private TableColumn<EnvioDTO, String> colEstado;
    @FXML private TableColumn<EnvioDTO, Double> colCosto;
    @FXML private TableColumn<EnvioDTO, Void> colAcciones;

    private LogisticaFacade logisticaFacade;
    private ObservableList<EnvioDTO> enviosPendientesObservable;

    @FXML
    public void initialize() {
        this.logisticaFacade = new LogisticaFacade();
        this.enviosPendientesObservable = FXCollections.observableArrayList();

        configurarColumnasTabla();
        tablaEnviosPendientes.setItems(enviosPendientesObservable);
        cargarEnviosPendientes();
    }

    private void configurarColumnasTabla() {
        colIdEnvio.setCellValueFactory(new PropertyValueFactory<>("idEnvio"));
        colIdUsuario.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));

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

        // CORREGIDO: Usar Callback para la celda de acciones
        colAcciones.setCellFactory(new Callback<TableColumn<EnvioDTO, Void>, TableCell<EnvioDTO, Void>>() {
            @Override
            public TableCell<EnvioDTO, Void> call(TableColumn<EnvioDTO, Void> param) {
                return new AccionesAsignarCell(EnviosPendientesController.this);
            }
        });
    }

    public void cargarEnviosPendientes() {
        try {
            List<EnvioDTO> todosLosEnvios = logisticaFacade.getTodosLosEnvios();
            List<EnvioDTO> pendientes = todosLosEnvios.stream()
                    .filter(envio -> envio.getEstadoActual() != null &&
                            "SOLICITADO".equals(envio.getEstadoActual().name()))
                    .collect(Collectors.toList());

            enviosPendientesObservable.setAll(pendientes);

            if (pendientes.isEmpty()) {
                mostrarInfo("No hay envíos pendientes para asignar.");
            }
        } catch (Exception e) {
            mostrarError("Error al cargar envíos pendientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void asignarEnvio(EnvioDTO envio) {
        if (envio == null) return;

        String idRepartidor = mostrarDialogoSeleccionRepartidor();
        if (idRepartidor != null && !idRepartidor.trim().isEmpty()) {
            String idRepartidorLimpio = idRepartidor.split(" - ")[0];

            boolean asignado = logisticaFacade.asignarEnvioARepartidor(envio.getIdEnvio(), idRepartidorLimpio);

            if (asignado) {
                mostrarExito("Envío " + envio.getIdEnvio() + " asignado exitosamente al repartidor " + idRepartidorLimpio);
                cargarEnviosPendientes();
            } else {
                mostrarError("No se pudo asignar el envío " + envio.getIdEnvio());
            }
        } else {
            mostrarInfo("Asignación cancelada. No se seleccionó ningún repartidor.");
        }
    }

    private String mostrarDialogoSeleccionRepartidor() {
        List<RepartidorDTO> repartidoresDisponibles = logisticaFacade.listarRepartidores();

        List<String> opcionesRepartidores = repartidoresDisponibles.stream()
                .filter(repartidor -> repartidor.getDisponibilidad() != null &&
                        "DISPONIBLE".equals(repartidor.getDisponibilidad()))
                .map(repartidor -> repartidor.getIdRepartidor() + " - " + repartidor.getNombre())
                .collect(Collectors.toList());

        if (opcionesRepartidores.isEmpty()) {
            mostrarError("No hay repartidores disponibles en este momento.");
            return null;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(opcionesRepartidores.get(0), opcionesRepartidores);
        dialog.setTitle("Seleccionar Repartidor");
        dialog.setHeaderText("Asignar Envío");
        dialog.setContentText("Seleccione un repartidor:");

        return dialog.showAndWait().orElse(null);
    }

    @FXML
    private void volverAlMenuPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminMain.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) tablaEnviosPendientes.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panel de Administrador - Urban Express");
            stage.centerOnScreen();
        } catch (IOException e) {
            mostrarError("Error al cargar ventana principal de admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void actualizarLista() {
        cargarEnviosPendientes();
        mostrarInfo("Lista de envíos pendientes actualizada.");
    }

    @FXML
    private void asignarTodosAutomaticamente() {
        if (enviosPendientesObservable.isEmpty()) {
            mostrarInfo("No hay envíos pendientes para asignar.");
            return;
        }

        int asignados = 0;
        List<RepartidorDTO> repartidoresDisponibles = logisticaFacade.listarRepartidores().stream()
                .filter(repartidor -> repartidor.getDisponibilidad() != null &&
                        "DISPONIBLE".equals(repartidor.getDisponibilidad()))
                .collect(Collectors.toList());

        if (repartidoresDisponibles.isEmpty()) {
            mostrarError("No hay repartidores disponibles para asignación automática.");
            return;
        }

        int repartidorIndex = 0;
        for (EnvioDTO envio : enviosPendientesObservable) {
            if (repartidorIndex >= repartidoresDisponibles.size()) {
                repartidorIndex = 0;
            }

            String idRepartidor = repartidoresDisponibles.get(repartidorIndex).getIdRepartidor();
            if (logisticaFacade.asignarEnvioARepartidor(envio.getIdEnvio(), idRepartidor)) {
                asignados++;
                repartidorIndex++;
            }
        }

        if (asignados > 0) {
            mostrarExito(asignados + " envíos asignados automáticamente.");
            cargarEnviosPendientes();
        } else {
            mostrarError("No se pudo asignar ningún envío automáticamente.");
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

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Clase interna para la celda de acciones
    private static class AccionesAsignarCell extends TableCell<EnvioDTO, Void> {
        private final EnviosPendientesController controller;
        private final Button btnAsignar;

        public AccionesAsignarCell(EnviosPendientesController controller) {
            this.controller = controller;
            this.btnAsignar = new Button("Asignar");
            // Estilizar el botón
            btnAsignar.setStyle("-fx-background-color: #2b2b2b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand;");
            btnAsignar.setOnAction(event -> {
                EnvioDTO envio = getTableView().getItems().get(getIndex());
                controller.asignarEnvio(envio);
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(btnAsignar);
            }
        }
    }
}