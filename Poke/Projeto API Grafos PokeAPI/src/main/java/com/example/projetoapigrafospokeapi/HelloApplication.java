package com.example.projetoapigrafospokeapi;

import com.example.projetoapigrafospokeapi.API.PokeAPI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        stage.initStyle(StageStyle.TRANSPARENT);

        //Carregando o FXML
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        //Fazendo o fundo da tela ser tranparente
        scene.setFill(Color.TRANSPARENT);


        stage.setScene(scene);
        stage.setResizable(false);

        //Metodo que faz a tela se mexer, depois de remover a barra, não tem como mexer
        //WindowMoveUtil.makeMovable(root);

        stage.show();



    }
}
