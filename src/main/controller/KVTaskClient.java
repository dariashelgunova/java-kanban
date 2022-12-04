package main.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final HttpClient client;
    private final HttpResponse.BodyHandler<String> handler;
    private final String apiToken;
    private final String baseUrl;


    public KVTaskClient(String urlString, int port) throws Exception {
        this.baseUrl = urlString + port;
        URI registerUrl = URI.create(baseUrl + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(registerUrl)
                .build();
        client = HttpClient.newHttpClient();
        handler = HttpResponse.BodyHandlers.ofString();
        try {
            HttpResponse<String> response = client.send(request, handler);
            apiToken = response.body();
        } catch (IOException | InterruptedException exception) {
            throw new Exception(exception);
        }
    }

    public void put(String key, String json) throws Exception {
        String urlString = baseUrl + "/save/" + key + "?API_TOKEN=" + this.apiToken;
        URI URL = URI.create(urlString);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URL)
                .build();
        try {
            HttpResponse<String> response = client.send(request, handler);
            System.out.println("Код ответа: " + response.statusCode());
        } catch (IOException | InterruptedException exception) {
            throw new Exception(exception);
        }
    }

    public String load(String key) throws Exception {
        String result;
        String urlString = baseUrl + "/load/" + key + "?API_TOKEN=" + this.apiToken;
        URI URL = URI.create(urlString);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URL)
                .build();
        try {
            HttpResponse<String> response = client.send(request, handler);
            System.out.println("Код ответа: " + response.statusCode());
            result = response.body();
        } catch (IOException | InterruptedException exception) {
            throw new Exception(exception);
        }
        return result;
    }

}
