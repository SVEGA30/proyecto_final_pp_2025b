module proyecto_final_pp {
    requires javafx.controls;
    requires javafx.fxml;   
    requires javafx.graphics;
    

    exports proyecto_final_pp;
    exports proyecto_final_pp.ViewController;
    exports proyecto_final_pp.model;
    exports proyecto_final_pp.util;


    // Solo abrir los paquetes que existen (los que tienen clases Java)
    opens proyecto_final_pp.ViewController to javafx.fxml;
    
    // QUITA o COMENTA esta línea:
    // opens proyecto_final_pp.view to javafx.fxml;
}