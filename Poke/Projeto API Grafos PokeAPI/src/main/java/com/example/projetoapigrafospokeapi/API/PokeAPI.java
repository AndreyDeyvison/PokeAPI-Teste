package com.example.projetoapigrafospokeapi.API;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

public class PokeAPI {

    List<String> minhaPokedex = new ArrayList<>();
    List<String[]> matrizPokedex = new ArrayList<>();
    List<String> pokemonsValidos = new ArrayList<>();
    Map<String, String[]> listaDeEvolucoes = new HashMap<>();
    Map<String, Integer> indexPokemon = new HashMap<>();

    int numPoke = 0;



    public void montarPokeDex(String pokemon) {


        //metodo bem simples, vai rodar ate que o usuario digite 0, ate lá, vai receber o nome dos pokemons,
        //no final, acho bom limitir o usuario com um auto complete(ou combo box, sla), pra não deixar ele escrever o nome do bicho errado
        //da pra pegar uma tabela com varios pokemons e consumir com o apache


        indexPokemon.put(pokemon.toLowerCase(), numPoke);
        numPoke++;
        minhaPokedex.add(pokemon.toLowerCase());

    }

    public List<String> getMinhaPokedex(){
        return minhaPokedex;

    }

    public boolean validarPokedex(){
        try {

            //Primeiramente, vamos olhar se tem pokemon suficiente para validar a pokedex, mais de 1
            if (minhaPokedex.size() <= 1) {
                System.out.println("Não há pokémons válidos suficientes para montar pares.");
                return false;
            }

            System.out.println("Validando linhagens evolutivas...");

            // 1. Primeiro, passa por TODOS os Pokémons e filtra os que têm evolução
            for (String poke : minhaPokedex) {


                String urlSpecie = "https://pokeapi.co/api/v2/pokemon-species/" + poke;
                List<String> linhagem = fetchEvolutionNames(getEvolutionChainUrl(urlSpecie));
                System.out.println(linhagem);

                if (linhagem.size() <= 1) {
                    System.out.println("O pokemon " + poke + " não tem evolução registrada e será ignorado.");
                } else {
                    // Se tem evolução, adiciona na lista de válidos
                    pokemonsValidos.add(poke);

                    listaDeEvolucoes.put(poke, linhagem.toArray(new String[0]));
                }
            }


            // 2. Depois, monta a matriz APENAS com os Pokémons que passaram no filtro
            for (int i = 0; i < pokemonsValidos.size(); i++) {
                String pokeAtual = pokemonsValidos.get(i);

                // ja vamos adicionandoessa relação na matriz, ao menos que seja o ultimo elemento, montamos a matriz com o pokemons da pokedex
                if (i < pokemonsValidos.size() - 1) {
                    String pokePosterior = pokemonsValidos.get(i + 1);
                    matrizPokedex.add(new String[]{pokePosterior, pokeAtual});
                }

                // analise da linhagem/depedencia
                List<String> linhagem = Arrays.asList(listaDeEvolucoes.get(pokeAtual));
                int posicaoLinhagem = linhagem.indexOf(pokeAtual);

                //se não for o poke base, pega o pai; o pai pode ser o poke na fase base, o pai é o que vem antes
                if (posicaoLinhagem > 0) {
                    String devagarPae = linhagem.get(posicaoLinhagem - 1);

                    //tem q ver se a pokedex tem o pai, se não tiver, acaba aqui
                    if (indexPokemon.containsKey(devagarPae)) {
                        matrizPokedex.add(new String[]{pokeAtual, devagarPae});
                    } else {

                        System.out.println("Falta o Pai " + devagarPae);
                        return false;
                    }
                }
            }

            System.out.println("\n--- Resultado da Matriz ---");
            for (String[] par : matrizPokedex) {
                System.out.println("Esse: " + par[0] + " | Depende desse: " + par[1]);
            }


            return canFinish(minhaPokedex.size(), matrizPokedex.toArray(new String[0][0]));

        } catch (Exception e) {
            System.err.println("Erro ao validar: " + e.getMessage());
            return false;
        }
    }

    //LeetCode 207
    public boolean canFinish(int numPoke, String[][] preRequisites) {
        int[][] matrizFinal = new int[numPoke][numPoke];
        for (String[] hieraquia : preRequisites) {
            matrizFinal[indexPokemon.get(hieraquia[1])][indexPokemon.get(hieraquia[0])] = 1;
        }

        for (int k = 0; k < numPoke; k++) {
            for (int i = 0; i < numPoke; i++) {
                for (int j = 0; j < numPoke; j++) {
                    if (matrizFinal[i][k] == 1 && matrizFinal[k][j] == 1) {
                        matrizFinal[i][j] = 1;
                    }
                }
            }
        }

        for (int i = 0; i < numPoke; i++) {
            if (matrizFinal[i][i] == 1) return false;
        }
        return true;
    }

    // Métodos auxiliares de API (Apenas limpei a lógica de adicionar na lista automaticamente)
    private static String getEvolutionChainUrl(String speciesUrl) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(speciesUrl)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return new JSONObject(response.body()).getJSONObject("evolution_chain").getString("url");
    }

    private static List<String> fetchEvolutionNames(String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject chain = new JSONObject(response.body()).getJSONObject("chain");
        List<String> nomesEvolucoes = new ArrayList<>();

        // Inicia a extração a partir do nó principal
        extrairNomesRecursivo(chain, nomesEvolucoes);

        return nomesEvolucoes;
    }

    private static void extrairNomesRecursivo(JSONObject node, List<String> lista) {
        // 1. Extrai o nome do Pokémon no nó atual
        String nome = node.getJSONObject("species").getString("name");
        lista.add(nome);

        // 2. Percorre a lista "evolves_to" para processar as próximas evoluções
        JSONArray evolvesTo = node.getJSONArray("evolves_to");
        for (int i = 0; i < evolvesTo.length(); i++) {
            extrairNomesRecursivo(evolvesTo.getJSONObject(i), lista);
        }
    }

    public static boolean isPokemonValido(String nomePokemon) {
        // Validação básica para evitar strings vazias ou nulas
        if (nomePokemon == null || nomePokemon.trim().isEmpty()) {
            return false;
        }

        // A PokéAPI exige que o nome esteja em letras minúsculas e sem espaços extras
        String nomeFormatado = nomePokemon.trim().toLowerCase();
        String url = "https://pokeapi.co/api/v2/pokemon/" + nomeFormatado;

        try {
            // Criação do cliente HTTP
            HttpClient client = HttpClient.newHttpClient();

            // Construção da requisição GET
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            // Enviamos a requisição. Usamos discarding() pois não precisamos ler
            // o corpo do JSON de resposta (os dados do Pokémon), apenas o código de status.
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

            // Se o status for 200, o Pokémon existe. Qualquer outro status (como 404), ele não existe.
            return response.statusCode() == 200;

        } catch (Exception e) {
            // Em caso de erro de rede ou URL inválida, o método retorna falso de forma segura
            System.err.println("Erro ao consultar a PokéAPI: " + e.getMessage());
            return false;
        }
    }


}
