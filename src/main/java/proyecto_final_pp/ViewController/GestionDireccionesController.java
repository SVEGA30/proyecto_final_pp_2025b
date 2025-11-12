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
import proyecto_final_pp.model.dto.DireccionDTO;
import proyecto_final_pp.model.dto.UsuarioDTO;
import proyecto_final_pp.model.dto.ZonaDTO;
import proyecto_final_pp.facade.LogisticaFacade;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GestionDireccionesController {

    @FXML private TextField txtNuevoAlias;
    @FXML private ComboBox<ZonaDTO> cbNuevaZona;
    @FXML private TextField txtNuevaCalle;
    @FXML private Button btnAgregarDireccion;
    @FXML private TableView<DireccionDTO> tablaDirecciones;
    @FXML private TableColumn<DireccionDTO, String> colAlias;
    @FXML private TableColumn<DireccionDTO, String> colDireccion;
    @FXML private TableColumn<DireccionDTO, String> colZona;
    @FXML private TableColumn<DireccionDTO, Void> colAcciones;
    @FXML private Label lblInfoDireccionSeleccionada;

    private LogisticaFacade logisticaFacade;
    private UsuarioDTO usuarioActualDTO;
    private ObservableList<DireccionDTO> direccionesObservable;

    @FXML
    public void initialize() {
        try {
            this.logisticaFacade = new LogisticaFacade();
            this.direccionesObservable = FXCollections.observableArrayList();

            // Configurar columnas de la tabla
            configurarTabla();

            // Configurar listeners
            configurarListeners();

        } catch (Exception e) {
            System.err.println("Error al inicializar GestionDireccionesController: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarTabla() {
        // Configurar columnas de la tabla
        colAlias.setCellValueFactory(new PropertyValueFactory<>("alias"));
        colDireccion.setCellValueFactory(cellData -> {
            DireccionDTO dir = cellData.getValue();
            String direccionCompleta = dir.getCalle() != null ? dir.getCalle() : "";
            if (dir.getCiudad() != null && !dir.getCiudad().isEmpty()) {
                direccionCompleta += ", " + dir.getCiudad();
            }
            return new javafx.beans.property.SimpleStringProperty(direccionCompleta);
        });
        colZona.setCellValueFactory(cellData -> {
            DireccionDTO dir = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(dir.getZona() != null ? dir.getZona() : "");
        });

        // Configurar columna de acciones
        colAcciones.setCellFactory(new Callback<TableColumn<DireccionDTO, Void>, TableCell<DireccionDTO, Void>>() {
            @Override
            public TableCell<DireccionDTO, Void> call(TableColumn<DireccionDTO, Void> param) {
                return new AccionesDireccionCell(GestionDireccionesController.this);
            }
        });

        tablaDirecciones.setItems(direccionesObservable);
    }

    private void configurarListeners() {
        // Listener para selección de fila en la tabla
        tablaDirecciones.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        lblInfoDireccionSeleccionada.setText(
                                "Seleccionada: " + newValue.getAlias() + " - " + newValue.getCalle()
                        );
                    } else {
                        lblInfoDireccionSeleccionada.setText("Selecciona una dirección para ver detalles");
                    }
                }
        );
    }

    public void setUsuarioActual(UsuarioDTO usuarioDTO) {
        this.usuarioActualDTO = usuarioDTO;
        if (usuarioDTO != null) {
            cargarDireccionesUsuario();
            cargarZonasDisponibles();
            actualizarInterfazUsuario();
        }
    }

    private void actualizarInterfazUsuario() {
        if (usuarioActualDTO != null && lblInfoDireccionSeleccionada != null) {
            lblInfoDireccionSeleccionada.setText("Gestionando direcciones de: " + usuarioActualDTO.getNombre());
        }
    }

    private void cargarDireccionesUsuario() {
        try {
            if (usuarioActualDTO != null) {
                List<DireccionDTO> direcciones = logisticaFacade.obtenerDireccionesFrecuentes(usuarioActualDTO);
                if (direcciones != null) {
                    direccionesObservable.setAll(direcciones);
                } else {
                    direccionesObservable.clear();
                    System.out.println("No se encontraron direcciones para el usuario");
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar direcciones del usuario: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al cargar las direcciones: " + e.getMessage());
        }
    }

    private void cargarZonasDisponibles() {
        try {
            List<ZonaDTO> zonas = logisticaFacade.obtenerTodasZonas();
            if (zonas != null && !zonas.isEmpty()) {
                cbNuevaZona.setItems(FXCollections.observableArrayList(zonas));
            } else {
                System.err.println("No se encontraron zonas disponibles");
                mostrarError("No hay zonas disponibles para seleccionar");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar zonas: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al cargar las zonas disponibles: " + e.getMessage());
        }
    }

    @FXML
    private void agregarDireccion() {
        try {
            String alias = txtNuevoAlias.getText().trim();
            ZonaDTO zona = cbNuevaZona.getValue();
            String calle = txtNuevaCalle.getText().trim();

            // Validaciones
            if (alias.isEmpty()) {
                mostrarError("El alias es obligatorio.");
                txtNuevoAlias.requestFocus();
                return;
            }

            if (zona == null) {
                mostrarError("Debe seleccionar una zona.");
                cbNuevaZona.requestFocus();
                return;
            }

            if (calle.isEmpty()) {
                mostrarError("La dirección es obligatoria.");
                txtNuevaCalle.requestFocus();
                return;
            }

            // Verificar si ya existe una dirección con el mismo alias
            if (existeDireccionConAlias(alias)) {
                mostrarError("Ya existe una dirección con el alias: " + alias);
                txtNuevoAlias.requestFocus();
                return;
            }

            // Crear DTO de la nueva dirección
            DireccionDTO nuevaDireccionDTO = new DireccionDTO();
            nuevaDireccionDTO.setCalle(calle);
            nuevaDireccionDTO.setCiudad("Bogotá"); // Ciudad por defecto
            nuevaDireccionDTO.setZona(zona.getNombre());
            nuevaDireccionDTO.setAlias(alias);

            // Agregar la dirección al usuario actual usando la fachada
            boolean agregada = logisticaFacade.agregarDireccionFrecuente(usuarioActualDTO, nuevaDireccionDTO);

            if (agregada) {
                mostrarExito("Dirección '" + alias + "' agregada exitosamente.");
                limpiarFormulario();
                cargarDireccionesUsuario();
            } else {
                mostrarError("No se pudo agregar la dirección. Verifique los datos.");
            }

        } catch (Exception e) {
            System.err.println("Error al agregar dirección: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error inesperado al agregar la dirección: " + e.getMessage());
        }
    }

    private boolean existeDireccionConAlias(String alias) {
        return direccionesObservable.stream()
                .anyMatch(dir -> dir.getAlias().equalsIgnoreCase(alias));
    }

    private void limpiarFormulario() {
        txtNuevoAlias.clear();
        cbNuevaZona.setValue(null);
        txtNuevaCalle.clear();
        txtNuevoAlias.requestFocus();
    }

    @FXML
    private void limpiarCampos() {
        limpiarFormulario();
        tablaDirecciones.getSelectionModel().clearSelection();
    }

    // Método para manejar la eliminación desde la celda de la tabla
    public void eliminarDireccion(DireccionDTO direccion) {
        try {
            if (direccion == null) {
                mostrarError("No se ha seleccionado ninguna dirección para eliminar.");
                return;
            }

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Eliminación");
            confirmacion.setHeaderText("¿Está seguro de que desea eliminar esta dirección?");
            confirmacion.setContentText("Alias: " + direccion.getAlias() + "\nDirección: " + direccion.getCalle());

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                boolean eliminada = logisticaFacade.eliminarDireccionFrecuente(usuarioActualDTO, direccion);
                if (eliminada) {
                    mostrarExito("Dirección '" + direccion.getAlias() + "' eliminada exitosamente.");
                    cargarDireccionesUsuario();
                } else {
                    mostrarError("No se pudo eliminar la dirección.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar dirección: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error inesperado al eliminar la dirección: " + e.getMessage());
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

            Scene scene = new Scene(root);
            Stage stage = (Stage) btnAgregarDireccion.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Panel de Usuario - Urban Express");
            stage.centerOnScreen();
        } catch (IOException e) {
            System.err.println("Error al cargar ventana principal: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al cargar ventana principal: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    @FXML
    private void actualizarDirecciones() {
        cargarDireccionesUsuario();
        mostrarInfo("Direcciones actualizadas correctamente.");
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
    private static class AccionesDireccionCell extends TableCell<DireccionDTO, Void> {
        private final GestionDireccionesController controller;
        private final Button btnEliminar;

        public AccionesDireccionCell(GestionDireccionesController controller) {
            this.controller = controller;
            this.btnEliminar = new Button("Eliminar");

            // Estilizar botón
            btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 5;");
            btnEliminar.setOnAction(event -> {
                DireccionDTO direccion = getTableView().getItems().get(getIndex());
                if (direccion != null) {
                    controller.eliminarDireccion(direccion);
                }
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(btnEliminar);
            }
        }
    }

    // Métodos getter para posibles usos externos
    public UsuarioDTO getUsuarioActual() {
        return usuarioActualDTO;
    }

    public ObservableList<DireccionDTO> getDireccionesObservable() {
        return direccionesObservable;
    }
}