package proyecto_final_pp.ViewController;

import javafx.scene.Node;
import proyecto_final_pp.model.dto.*;
import proyecto_final_pp.facade.LogisticaFacade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnvioController {

    @FXML private ComboBox<DireccionDTO> cbDireccionesOrigen;
    @FXML private ComboBox<DireccionDTO> cbDireccionesDestino;
    @FXML private ComboBox<ZonaDTO> cbZonaOrigen;
    @FXML private ComboBox<ZonaDTO> cbZonaDestino;
    @FXML private TextField txtPeso;
    @FXML private TextField txtVolumen;
    @FXML private ComboBox<String> cbTipoEnvio;
    @FXML private CheckBox chkSeguro;
    @FXML private CheckBox chkFragil;
    @FXML private CheckBox chkFirmaRequerida;
    @FXML private CheckBox chkEmbalajeEspecial;
    @FXML private Label lblCostoCalculado;
    @FXML private Label lblInfoUsuario;
    @FXML private Button btnCrearEnvio;
    @FXML private Button btnLimpiar;
    @FXML private Button btnVolverMenu;
    @FXML private ComboBox<MetodoPagoDTO> cbMetodoPago;


    private LogisticaFacade logisticaFacade;
    private UsuarioDTO usuarioActualDTO;
    private proyecto_final_pp.Service.ServicioAdicionalService servicioAdicionalService = new proyecto_final_pp.Service.ServicioAdicionalService();

    @FXML
    public void initialize() {
        this.logisticaFacade = new LogisticaFacade();
        configurarInterfaz();
        configurarListeners();
        cargarZonasDisponibles();
        cargarMetodosPago();
    }

    private void cargarMetodosPago() {
        ObservableList<MetodoPagoDTO> metodosPago = FXCollections.observableArrayList(
                new MetodoPagoDTO("1", "Tarjeta de Crédito", "**** 1234"),
                new MetodoPagoDTO("2", "Tarjeta de Débito", "**** 5678"),
                new MetodoPagoDTO("4", "Efectivo", "Pago contra entrega"),
                new MetodoPagoDTO("6", "Cuenta Corriente", "Empresa"),
                new MetodoPagoDTO("7", "Nequi", "312****890"),
                new MetodoPagoDTO("8", "Daviplata", "315****123")
        );
        cbMetodoPago.setItems(metodosPago);
        // Mostrar una representación legible del método de pago
        cbMetodoPago.setCellFactory(listView -> new ListCell<MetodoPagoDTO>() {
            @Override
            protected void updateItem(MetodoPagoDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String ref = item.getReferencia() != null ? item.getReferencia() : "";
                    setText(item.getTipo() + (ref.isEmpty() ? "" : " (" + ref + ")"));
                }
            }
        });

        cbMetodoPago.setConverter(new StringConverter<MetodoPagoDTO>() {
            @Override
            public String toString(MetodoPagoDTO metodo) {
                if (metodo == null) return "";
                String ref = metodo.getReferencia() != null ? metodo.getReferencia() : "";
                return metodo.getTipo() + (ref.isEmpty() ? "" : " (" + ref + ")");
            }

            @Override
            public MetodoPagoDTO fromString(String string) {
                return null; // No necesario en este contexto
            }
        });
    }

    // Método para obtener el método de pago seleccionado (devuelve el DTO completo)
    public MetodoPagoDTO getMetodoPagoSeleccionado() {
        return cbMetodoPago.getValue();
    }

    // Método para obtener solo el tipo de pago seleccionado
    public String getTipoPagoSeleccionado() {
        MetodoPagoDTO metodo = cbMetodoPago.getValue();
        return metodo != null ? metodo.getTipo() : null;
    }

    // Método para obtener el ID del método de pago seleccionado
    public String getIdMetodoPagoSeleccionado() {
        MetodoPagoDTO metodo = cbMetodoPago.getValue();
        return metodo != null ? metodo.getIdMetodo() : null;
    }

    // Validar que se haya seleccionado un método de pago
    private boolean validarMetodoPago() {
        if (cbMetodoPago.getValue() == null) {
            mostrarError("Por favor, seleccione un método de pago");
            return false;
        }
        return true;
    }


    public void setUsuarioActual(UsuarioDTO usuarioDTO) {
        this.usuarioActualDTO = usuarioDTO;
        if (usuarioDTO != null) {
            lblInfoUsuario.setText("Usuario: " + usuarioDTO.getNombre());
            cargarDireccionesUsuario(usuarioDTO);
        }
    }

    private void configurarInterfaz() {
        // Configurar ComboBox de direcciones con display personalizado
        configurarComboBoxDirecciones();

        // Configurar ComboBox de zonas con display personalizado
        configurarComboBoxZonas();

        cbTipoEnvio.setItems(FXCollections.observableArrayList("ESTANDAR", "EXPRESS", "PRIORITARIO"));
        lblCostoCalculado.setText("Costo: $0");
        btnCrearEnvio.setDisable(true);

        // Configurar botón de volver
        if (btnVolverMenu != null) {
            btnVolverMenu.setOnAction(event -> volverAlMenuPrincipal());
        }
    }

    private void configurarComboBoxDirecciones() {
        // Configurar cómo se muestran las direcciones en los ComboBox
        Callback<ListView<DireccionDTO>, ListCell<DireccionDTO>> cellFactory = new Callback<ListView<DireccionDTO>, ListCell<DireccionDTO>>() {
            @Override
            public ListCell<DireccionDTO> call(ListView<DireccionDTO> param) {
                return new ListCell<DireccionDTO>() {
                    @Override
                    protected void updateItem(DireccionDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            // Mostrar solo alias y calle
                            setText(item.getAlias() + " - " + item.getCalle());
                        }
                    }
                };
            }
        };

        cbDireccionesOrigen.setCellFactory(cellFactory);
        cbDireccionesDestino.setCellFactory(cellFactory);

        // Configurar cómo se muestra el valor seleccionado
        cbDireccionesOrigen.setConverter(new StringConverter<DireccionDTO>() {
            @Override
            public String toString(DireccionDTO direccion) {
                if (direccion == null) {
                    return "";
                }
                return direccion.getAlias() + " - " + direccion.getCalle();
            }

            @Override
            public DireccionDTO fromString(String string) {
                return null; // No necesario para display
            }
        });

        cbDireccionesDestino.setConverter(new StringConverter<DireccionDTO>() {
            @Override
            public String toString(DireccionDTO direccion) {
                if (direccion == null) {
                    return "";
                }
                return direccion.getAlias() + " - " + direccion.getCalle();
            }

            @Override
            public DireccionDTO fromString(String string) {
                return null;
            }
        });
    }

    private void configurarComboBoxZonas() {
        // Configurar cómo se muestran las zonas en los ComboBox
        Callback<ListView<ZonaDTO>, ListCell<ZonaDTO>> cellFactory = new Callback<ListView<ZonaDTO>, ListCell<ZonaDTO>>() {
            @Override
            public ListCell<ZonaDTO> call(ListView<ZonaDTO> param) {
                return new ListCell<ZonaDTO>() {
                    @Override
                    protected void updateItem(ZonaDTO item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            // Mostrar solo el nombre de la zona
                            setText(item.getNombre() != null ? item.getNombre() : "Sin nombre");
                        }
                    }
                };
            }
        };

        cbZonaOrigen.setCellFactory(cellFactory);
        cbZonaDestino.setCellFactory(cellFactory);

        // Configurar cómo se muestra el valor seleccionado
        cbZonaOrigen.setConverter(new StringConverter<ZonaDTO>() {
            @Override
            public String toString(ZonaDTO zona) {
                if (zona == null) {
                    return "";
                }
                return zona.getNombre() != null ? zona.getNombre() : "";
            }

            @Override
            public ZonaDTO fromString(String string) {
                return null;
            }
        });

        cbZonaDestino.setConverter(new StringConverter<ZonaDTO>() {
            @Override
            public String toString(ZonaDTO zona) {
                if (zona == null) {
                    return "";
                }
                return zona.getNombre() != null ? zona.getNombre() : "";
            }

            @Override
            public ZonaDTO fromString(String string) {
                return null;
            }
        });
    }

    private void configurarListeners() {
        txtPeso.textProperty().addListener((obs, oldVal, newVal) -> calcularCostoEnvio());
        txtVolumen.textProperty().addListener((obs, oldVal, newVal) -> calcularCostoEnvio());

        chkSeguro.selectedProperty().addListener((obs, oldVal, newVal) -> calcularCostoEnvio());
        chkFragil.selectedProperty().addListener((obs, oldVal, newVal) -> calcularCostoEnvio());
        chkFirmaRequerida.selectedProperty().addListener((obs, oldVal, newVal) -> calcularCostoEnvio());
        chkEmbalajeEspecial.selectedProperty().addListener((obs, oldVal, newVal) -> calcularCostoEnvio());

        cbDireccionesOrigen.valueProperty().addListener((obs, oldVal, newVal) -> validarFormularioCompleto());
        cbDireccionesDestino.valueProperty().addListener((obs, oldVal, newVal) -> validarFormularioCompleto());
        cbZonaOrigen.valueProperty().addListener((obs, oldVal, newVal) -> validarFormularioCompleto());
        cbZonaDestino.valueProperty().addListener((obs, oldVal, newVal) -> validarFormularioCompleto());
        txtPeso.textProperty().addListener((obs, oldVal, newVal) -> validarFormularioCompleto());
        txtVolumen.textProperty().addListener((obs, oldVal, newVal) -> validarFormularioCompleto());
    }

    private void cargarDireccionesUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioDTO != null) {
            List<DireccionDTO> direcciones = logisticaFacade.obtenerDireccionesFrecuentes(usuarioDTO);
            ObservableList<DireccionDTO> observableDirecciones = FXCollections.observableArrayList(direcciones);
            cbDireccionesOrigen.setItems(observableDirecciones);
            cbDireccionesDestino.setItems(observableDirecciones);

            System.out.println("Direcciones cargadas para usuario " + usuarioDTO.getNombre() + ": " + direcciones.size());
            direcciones.forEach(d -> System.out.println("  - " + d.getAlias() + " - " + d.getCalle()));
        }
    }

    private void cargarZonasDisponibles() {
        try {
            List<ZonaDTO> zonas = logisticaFacade.obtenerTodasZonas();
            System.out.println("Zonas cargadas: " + zonas.size());

            ObservableList<ZonaDTO> observableZonas = FXCollections.observableArrayList(zonas);
            cbZonaOrigen.setItems(observableZonas);
            cbZonaDestino.setItems(observableZonas);

            zonas.forEach(z -> System.out.println("  - Zona: " + z.getNombre()));

        } catch (Exception e) {
            System.err.println("Error al cargar zonas: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al cargar las zonas disponibles: " + e.getMessage());
        }
    }

    private void calcularCostoEnvio() {
        if (esFormularioValidoParaCalculo()) {
            try {
                DireccionDTO origenDTO = cbDireccionesOrigen.getValue();
                DireccionDTO destinoDTO = cbDireccionesDestino.getValue();
                double peso = Double.parseDouble(txtPeso.getText());
                double volumen = Double.parseDouble(txtVolumen.getText());
                List<String> serviciosExtras = obtenerServiciosSeleccionados();
                String tipoEnvio = cbTipoEnvio.getValue();

                System.out.println("=== CÁLCULO DE COSTO ===");
                System.out.println("Origen: " + (origenDTO != null ? origenDTO.toString() : "null"));
                System.out.println("Destino: " + (destinoDTO != null ? destinoDTO.toString() : "null"));
                System.out.println("Peso: " + peso + " kg");
                System.out.println("Volumen: " + volumen + " m³");
                System.out.println("Tipo: " + tipoEnvio);
                System.out.println("Servicios: " + serviciosExtras);

                // Obtener el método de pago seleccionado (puede ser null si no se eligió)
                MetodoPagoDTO metodoPagoDTO = cbMetodoPago != null ? cbMetodoPago.getValue() : null;

                double costoEstimado = logisticaFacade.calcularCostoEstimado(origenDTO, destinoDTO, peso, volumen, serviciosExtras, tipoEnvio, metodoPagoDTO);

                lblCostoCalculado.setText(String.format("Costo: $%.0f", costoEstimado));
                System.out.println("Costo calculado: $" + costoEstimado);

            } catch (NumberFormatException e) {
                lblCostoCalculado.setText("Costo: $0");
                btnCrearEnvio.setDisable(true);
            }
        } else {
            lblCostoCalculado.setText("Costo: $0");
        }
    }

    private boolean esFormularioValidoParaCalculo() {
        return cbDireccionesOrigen.getValue() != null &&
                cbDireccionesDestino.getValue() != null &&
                !txtPeso.getText().isEmpty() &&
                !txtVolumen.getText().isEmpty();
    }

    private List<String> obtenerServiciosSeleccionados() {
        List<String> servicios = new ArrayList<>();
        if (chkSeguro.isSelected()) servicios.add("SEGURO");
        if (chkFragil.isSelected()) servicios.add("FRAGIL");
        if (chkFirmaRequerida.isSelected()) servicios.add("FIRMA_REQUERIDA");
        if (chkEmbalajeEspecial.isSelected()) servicios.add("EMBALAJE_ESPECIAL");
        return servicios;
    }

    private void validarFormularioCompleto() {
        boolean formularioValido = cbZonaOrigen.getValue() != null &&
                cbDireccionesOrigen.getValue() != null &&
                cbZonaDestino.getValue() != null &&
                cbDireccionesDestino.getValue() != null &&
                !txtPeso.getText().isEmpty() &&
                !txtVolumen.getText().isEmpty();
        try {
            double peso = Double.parseDouble(txtPeso.getText());
            double volumen = Double.parseDouble(txtVolumen.getText());
            formularioValido = formularioValido && peso > 0 && peso <= 50 && volumen > 0 && volumen <= 5;
        } catch (NumberFormatException e) {
            formularioValido = false;
        }

        btnCrearEnvio.setDisable(!formularioValido);

        // Debug de validación
        if (formularioValido) {
            System.out.println("Formulario VÁLIDO - Botón habilitado");
        } else {
            System.out.println("Formulario INVÁLIDO - Botón deshabilitado");
        }
    }

    @FXML
    private void crearEnvio() {
        try {
            DireccionDTO origenDTO = cbDireccionesOrigen.getValue();
            DireccionDTO destinoDTO = cbDireccionesDestino.getValue();
            double peso = Double.parseDouble(txtPeso.getText());
            double volumen = Double.parseDouble(txtVolumen.getText());
            List<String> serviciosExtras = obtenerServiciosSeleccionados();
            String tipoEnvio = cbTipoEnvio.getValue();
            MetodoPagoDTO metodoPagoDTO = cbMetodoPago.getValue(); // Nuevo: Obtener método de pago

            // DEBUG: Mostrar información detallada antes de crear
            System.out.println("=== INTENTANDO CREAR ENVÍO ===");
            System.out.println("Usuario: " + (usuarioActualDTO != null ? usuarioActualDTO.getNombre() : "null"));
            System.out.println("Origen: " + (origenDTO != null ?
                    origenDTO.getAlias() + " - " + origenDTO.getCalle() + ", " + origenDTO.getCiudad() + " (" + origenDTO.getZona() + ")" : "null"));
            System.out.println("Destino: " + (destinoDTO != null ?
                    destinoDTO.getAlias() + " - " + destinoDTO.getCalle() + ", " + destinoDTO.getCiudad() + " (" + destinoDTO.getZona() + ")" : "null"));
            System.out.println("Peso: " + peso + " kg");
            System.out.println("Volumen: " + volumen + " m³");
            System.out.println("Tipo: " + tipoEnvio);
            System.out.println("Servicios: " + serviciosExtras);
            System.out.println("Método de Pago: " + (metodoPagoDTO != null ?
                    metodoPagoDTO.getTipo() + " (" + metodoPagoDTO.getReferencia() + ")" : "null")); // Nuevo debug

            // Validar método de pago
            if (metodoPagoDTO == null) {
                mostrarError("Por favor, seleccione un método de pago");
                return;
            }

            // Verificar si las direcciones son realmente diferentes
            if (origenDTO != null && destinoDTO != null) {
                boolean mismaCalle = origenDTO.getCalle() != null && origenDTO.getCalle().equalsIgnoreCase(destinoDTO.getCalle());
                boolean mismaCiudad = origenDTO.getCiudad() != null && origenDTO.getCiudad().equalsIgnoreCase(destinoDTO.getCiudad());
                boolean mismaZona = origenDTO.getZona() != null && origenDTO.getZona().equalsIgnoreCase(destinoDTO.getZona());

                System.out.println("Comparación detallada de direcciones:");
                System.out.println("Misma calle: " + mismaCalle);
                System.out.println("Misma ciudad: " + mismaCiudad);
                System.out.println("Misma zona: " + mismaZona);
                System.out.println("¿Son iguales? " + (mismaCalle && mismaCiudad && mismaZona));
            }

            // Llamar al facade con el método de pago
            EnvioDTO nuevoEnvioDTO = logisticaFacade.crearEnvio(
                    usuarioActualDTO,
                    origenDTO,
                    destinoDTO,
                    peso,
                    volumen,
                    serviciosExtras,
                    tipoEnvio,
                    metodoPagoDTO  // Nuevo parámetro
            );

            if (nuevoEnvioDTO != null) {
                System.out.println("✅ Envío creado exitosamente - ID: " + nuevoEnvioDTO.getIdEnvio());
                mostrarExito("¡Envío creado exitosamente!\n" +
                        "ID: " + nuevoEnvioDTO.getIdEnvio() + "\n" +
                        "Costo: $" + nuevoEnvioDTO.getCosto() + "\n" +
                        "Método de Pago: " + metodoPagoDTO.getTipo() + "\n" + // Nuevo en mensaje
                        "Estado: " + nuevoEnvioDTO.getEstadoActual().name());
                limpiarFormulario();
            } else {
                System.out.println("❌ No se pudo crear el envío");
                mostrarError("No se pudo crear el envío. Verifique los datos.");
            }

        } catch (NumberFormatException e) {
            System.err.println("❌ Error en formato numérico: " + e.getMessage());
            mostrarError("Error en los campos numéricos: Peso y volumen deben ser números válidos.");
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error de validación: " + e.getMessage());
            mostrarError("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error inesperado al crear envío: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al crear envío: " + e.getMessage());
        }
    }

    @FXML
    private void limpiarFormulario() {
        cbDireccionesOrigen.getSelectionModel().clearSelection();
        cbDireccionesDestino.getSelectionModel().clearSelection();
        cbZonaOrigen.getSelectionModel().clearSelection();
        cbZonaDestino.getSelectionModel().clearSelection();
        txtPeso.clear();
        txtVolumen.clear();
        cbTipoEnvio.getSelectionModel().clearSelection();

        // Limpiar método de pago
        cbMetodoPago.getSelectionModel().clearSelection();

        chkSeguro.setSelected(false);
        chkFragil.setSelected(false);
        chkFirmaRequerida.setSelected(false);
        chkEmbalajeEspecial.setSelected(false);
        lblCostoCalculado.setText("Costo: $0");
        btnCrearEnvio.setDisable(true);

        System.out.println("Formulario limpiado");
    }

    @FXML
    private void volverAlMenuPrincipal() {
        try {
            System.out.println("Intentando volver al menú principal...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UsuarioMain.fxml"));
            Parent root = loader.load();

            UsuarioMainController controller = loader.getController();
            if (controller != null) {
                controller.setUsuarioActual(usuarioActualDTO);
                System.out.println("Usuario pasado al controlador principal: " + usuarioActualDTO.getNombre());
            }

            Scene scene = new Scene(root);
            Stage stage = getCurrentStage();
            if (stage != null) {
                stage.setScene(scene);
                stage.setTitle("Panel de Usuario - Urban Express");
                stage.centerOnScreen();
                System.out.println("Navegación al menú principal exitosa");
            } else {
                System.err.println("No se pudo obtener la ventana actual");
                mostrarError("No se pudo obtener la ventana actual.");
            }
        } catch (IOException e) {
            System.err.println("Error de E/S al cargar ventana principal: " + e.getMessage());
            mostrarError("Error al cargar ventana principal: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalStateException e) {
            System.err.println("Error de estado al cargar FXML: " + e.getMessage());
            mostrarError("Error en la configuración de la interfaz: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado al volver al menú: " + e.getMessage());
            mostrarError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void navegarAHistorial() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/HistorialEnvios.fxml"));
            Parent root = loader.load();

            // PASAR EL USUARIO AL CONTROLADOR DE HISTORIAL
            HistorialEnviosController controller = loader.getController();
            if (controller != null) {
                controller.setUsuarioActual(usuarioActualDTO);
            }

            Scene scene = new Scene(root);
            Stage stage = getCurrentStage();
            if (stage != null) {
                stage.setScene(scene);
                stage.setTitle("Historial de Envíos - Urban Express");
                stage.centerOnScreen();
            } else {
                mostrarError("No se pudo obtener la ventana actual.");
            }
        } catch (IOException e) {
            mostrarError("Error al cargar ventana de historial: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void navegarAGestionDirecciones() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GestionDirecciones.fxml"));
            Parent root = loader.load();

            // PASAR EL USUARIO AL CONTROLADOR DE GESTIÓN DE DIRECCIONES
            GestionDireccionesController controller = loader.getController();
            if (controller != null) {
                controller.setUsuarioActual(usuarioActualDTO);
            }

            Scene scene = new Scene(root);
            Stage stage = getCurrentStage();
            if (stage != null) {
                stage.setScene(scene);
                stage.setTitle("Gestión de Direcciones - Urban Express");
                stage.centerOnScreen();
            } else {
                mostrarError("No se pudo obtener la ventana actual.");
            }
        } catch (IOException e) {
            mostrarError("Error al cargar ventana de direcciones: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Stage getCurrentStage() {
        try {
            // Intentar con diferentes nodos en orden de prioridad
            if (btnCrearEnvio != null && btnCrearEnvio.getScene() != null) {
                return (Stage) btnCrearEnvio.getScene().getWindow();
            }
            if (btnLimpiar != null && btnLimpiar.getScene() != null) {
                return (Stage) btnLimpiar.getScene().getWindow();
            }
            if (lblInfoUsuario != null && lblInfoUsuario.getScene() != null) {
                return (Stage) lblInfoUsuario.getScene().getWindow();
            }
            if (btnVolverMenu != null && btnVolverMenu.getScene() != null) {
                return (Stage) btnVolverMenu.getScene().getWindow();
            }

            // Si no se pudo obtener de los nodos específicos, buscar cualquier nodo de la escena
            Node anyNode = cbDireccionesOrigen != null ? cbDireccionesOrigen :
                    cbDireccionesDestino != null ? cbDireccionesDestino : null;
            if (anyNode != null && anyNode.getScene() != null) {
                return (Stage) anyNode.getScene().getWindow();
            }

            System.err.println("No se pudo obtener el Stage actual desde ningún nodo");
            return null;
        } catch (Exception e) {
            System.err.println("Error al obtener Stage: " + e.getMessage());
            return null;
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

    private List<ServicioAdicionalDTO> obtenerServiciosSeleccionadosDTO() {
        List<ServicioAdicionalDTO> servicios = new ArrayList<>();
        if (chkSeguro.isSelected()) servicios.add(servicioAdicionalService.obtenerServicioPorTipo("SEGURO"));
        if (chkFragil.isSelected()) servicios.add(servicioAdicionalService.obtenerServicioPorTipo("FRAGIL"));
        if (chkFirmaRequerida.isSelected()) servicios.add(servicioAdicionalService.obtenerServicioPorTipo("FIRMA_REQUERIDA"));
        if (chkEmbalajeEspecial.isSelected()) servicios.add(servicioAdicionalService.obtenerServicioPorTipo("EMBALAJE_ESPECIAL"));
        return servicios;
    }
}