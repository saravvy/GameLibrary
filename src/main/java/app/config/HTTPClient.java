package app.config;

import app.dtos.GameDTO;
import app.dtos.GameDetailDTO;
import app.dtos.ResultDTO;
import app.entities.Game;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class HTTPClient {


    public ResultDTO getGames() throws Exception {

        String apiKey = System.getenv("rawgapi");
        String url = "https://api.rawg.io/api/games?key="+apiKey+"&page_size=40";



        try {
            // Create an HttpClient instance
            HttpClient client = HttpClient.newHttpClient();

            // Create a request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check the status code and print the response
            if (response.statusCode() == 200) {

                ObjectMapper objectMapper = new ObjectMapper();


                ResultDTO result = objectMapper.readValue(response.body(), ResultDTO.class);

                return result;
            } else {
                throw new Exception("GET request failed. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("error in fetchinng games");

        }

    }


    public ResultDTO getGames(String Url) throws Exception {

        String apiKey = System.getenv("rawgapi");
        String url = Url;


        try {
            // Create an HttpClient instance
            HttpClient client = HttpClient.newHttpClient();

            // Create a request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check the status code and print the response
            if (response.statusCode() == 200) {

                ObjectMapper objectMapper = new ObjectMapper();


                ResultDTO result = objectMapper.readValue(response.body(), ResultDTO.class);

                return result;
            } else {
                throw new Exception("GET request failed. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("error in fetchinng games");

        } }

        public GameDetailDTO getGameDetails(int gameId) throws Exception {

            String apiKey = System.getenv("rawgapi");
            String url = "https://api.rawg.io/api/games/" + gameId + "?key=" + apiKey;


            try {
                // Create an HttpClient instance
                HttpClient client = HttpClient.newHttpClient();

                // Create a request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(url))
                        .GET()
                        .build();

                // Send the request and get the response
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Check the status code and print the response
                if (response.statusCode() == 200) {

                    ObjectMapper objectMapper = new ObjectMapper();


                    GameDetailDTO result = objectMapper.readValue(response.body(), GameDetailDTO.class);

                    return result;
                } else {
                    throw new Exception("GET request failed. Status code: " + response.statusCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("error in fetchinng games");


        } }

            public List<Game> fetchGamesWithDetails() throws Exception {
                HTTPClient httpClient = new HTTPClient();
                ResultDTO result = httpClient.getGames();
                List<Game> games = new ArrayList<>();


                for (GameDTO gameDTO : result.games()) {
                    GameDetailDTO detail = httpClient.getGameDetails(gameDTO.id());
                    Game game = Game.fromDTO(detail);
                    games.add(game);
                }
                return games;
            }

    }