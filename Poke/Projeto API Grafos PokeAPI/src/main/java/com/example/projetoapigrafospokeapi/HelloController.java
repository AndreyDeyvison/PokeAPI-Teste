package com.example.projetoapigrafospokeapi;

import com.example.projetoapigrafospokeapi.API.PokeAPI;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;
import javafx.scene.layout.HBox;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.List;

public class HelloController {

    @FXML
    TextField entradaPoke;

    @FXML
    HBox logoApp;

    @FXML
    VBox listaPokedex;

    @FXML
    Text textoResposta;

    @FXML
    Button validar;

    ObservableList<String> listaDePokemons = FXCollections.observableArrayList();
    PokeAPI poke = new PokeAPI();

    @FXML
    public void initialize(){

        aplicarAnimacaoFlutuante(logoApp,3, 10,false);

        configurarScrollPane();



    }

    public void validarPokeDex(){
        if(poke.validarPokedex()){
            textoResposta.setText("Pokedex Válida");
            textoResposta.setFill(Color.DARKGREEN);
            validar.setDisable(true);
        }else{
            textoResposta.setText("Pokedex Inválida");
            textoResposta.setFill(Color.DARKRED);
            validar.setDisable(true);
        }
    }

    public void configurarScrollPane(){

        Runnable atualizarLista = () ->{
            listaPokedex.getChildren().clear();
            listaPokedex.setSpacing(20);
            for(String s : poke.getMinhaPokedex()){

                HBox linhaPoke = new HBox();



                ImageView visualizadorFoto = new ImageView();
                visualizadorFoto.setFitHeight(50);
                visualizadorFoto.setFitWidth(50);
                visualizadorFoto.setPreserveRatio(true);

                String urlFoto = "https://img.pokemondb.net/artwork/large/" + s + ".jpg";

                Image img = new Image(urlFoto, true);
                visualizadorFoto.setImage(img);

                linhaPoke.setSpacing(30);

                Text t = new Text(s.toUpperCase());
                t.getStyleClass().add("nome-pokemon");

                linhaPoke.setAlignment(Pos.CENTER_LEFT);
                linhaPoke.getChildren().add(visualizadorFoto);
                linhaPoke.getChildren().add(t);

                listaPokedex.getChildren().add(linhaPoke);
            }
        };

        entradaPoke.setOnKeyPressed(keyEvent -> {

            if (keyEvent.getCode() == KeyCode.ENTER){

                String pokemon = entradaPoke.getText();

                if(PokeAPI.isPokemonValido(pokemon)){
                    poke.montarPokeDex(pokemon);
                    listaDePokemons.add(pokemon);

                    entradaPoke.setText("");

                    atualizarLista.run();
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Pokemon Inválido");
                    alert.setHeaderText(null); // Remove o texto de cabeçalho padrão para um visual mais clean
                    alert.setContentText("Pokemon Inválido, insira um Pokemon Válido");

                    // Remove a barra de título padrão do SO (opcional, para um look mais moderno)
                    // alert.initStyle(StageStyle.UNIFIED);

                    var resource = getClass().getResource("/style.css");
                    // Acessando o painel do diálogo para estilizar
                    DialogPane dialogPane = alert.getDialogPane();

                    // Adicionando um arquivo CSS customizado
                    dialogPane.getStylesheets().add(resource.toExternalForm());
                    dialogPane.getStyleClass().add("meu-dialogo-erro");

                    Stage stage = (Stage) dialogPane.getScene().getWindow();
                    stage.initStyle(StageStyle.TRANSPARENT);

                    alert.showAndWait();
                }
            }
        });
    }


    private void aplicarAnimacaoFlutuante(HBox objeto, double duracao, int distancia, boolean horizontal) {

        TranslateTransition tt = new TranslateTransition(Duration.seconds(duracao), objeto);

        tt.setByY(distancia);
        tt.setCycleCount(TranslateTransition.INDEFINITE);
        tt.setAutoReverse(true);

        if(horizontal){
            tt.setByX(15);
            tt.setCycleCount(TranslateTransition.INDEFINITE);
            tt.setAutoReverse(true);
        }


        tt.setInterpolator(Interpolator.EASE_BOTH);

        tt.play();
    }

}
