module com.example.myweather {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.material2;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    
    // HTTP and JSON dependencies
    requires okhttp3;
    requires com.google.gson;
    
    // Java base modules
    requires java.net.http;

    opens com.example.myweather to javafx.fxml, com.google.gson;
    opens com.example.myweather.model to com.google.gson;
    exports com.example.myweather;
}