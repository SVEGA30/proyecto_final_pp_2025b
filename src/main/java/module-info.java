module proyecto_final_pp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.apache.pdfbox;

    exports proyecto_final_pp;
    exports proyecto_final_pp.ViewController;
    exports proyecto_final_pp.model;
    exports proyecto_final_pp.util;

    opens proyecto_final_pp.ViewController to javafx.fxml;
    opens proyecto_final_pp.model.dto to javafx.base;

}