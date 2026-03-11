module com.example.projetoapigrafospokeapi {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.net.http;
    requires org.json;
    requires java.desktop;

    opens com.example.projetoapigrafospokeapi to javafx.fxml;
    exports com.example.projetoapigrafospokeapi;
}