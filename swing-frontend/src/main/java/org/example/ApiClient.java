package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient client;
    private final Gson gson;

    public ApiClient() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public List<Product> getAllProducts() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/products")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), new TypeToken<List<Product>>(){}.getType());
    }

    public JsonArray getAllTransactions() throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/transactions")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return JsonParser.parseString(response.body()).getAsJsonArray();
    }

    public void addProduct(String jsonPayload) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/products"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void updateProduct(Long id, String jsonPayload) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/products/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload)).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void deleteProduct(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/products/" + id)).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public Long processSale(Long productId, int quantity) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/products/" + productId + "/sell?quantity=" + quantity))
                .PUT(HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            try {
                JsonObject errorJson = JsonParser.parseString(response.body()).getAsJsonObject();
                if (errorJson.has("message")) {
                    throw new Exception(errorJson.get("message").getAsString());
                }
            } catch (com.google.gson.JsonSyntaxException e) {
            }
            throw new Exception("Action rejected by server. Check stock availability.");
        }

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        return jsonObject.get("id").getAsLong();
    }

    public void exportSalesExcel(String savePath) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/transactions/export")).GET().build();
        client.send(request, HttpResponse.BodyHandlers.ofFile(Paths.get(savePath)));
    }

    public void downloadBill(Long transactionId, String savePath) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/transactions/" + transactionId + "/bill")).GET().build();
        client.send(request, HttpResponse.BodyHandlers.ofFile(Paths.get(savePath)));
    }

    public void downloadChalan(Long transactionId, String address, String mapLink, String recipientName, String savePath) throws Exception {
        String encodedAddress = URLEncoder.encode(address != null ? address : "", StandardCharsets.UTF_8);
        String encodedLink = URLEncoder.encode(mapLink != null ? mapLink : "", StandardCharsets.UTF_8);
        String encodedRecipient = URLEncoder.encode(recipientName != null ? recipientName : "", StandardCharsets.UTF_8);

        String url = String.format("%s/transactions/%d/chalan?address=%s&mapLink=%s&recipientName=%s",
                BASE_URL, transactionId, encodedAddress, encodedLink, encodedRecipient);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        client.send(request, HttpResponse.BodyHandlers.ofFile(Paths.get(savePath)));
    }
}